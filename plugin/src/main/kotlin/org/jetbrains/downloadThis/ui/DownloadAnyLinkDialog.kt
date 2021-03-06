/*
 * MIT License
 *
 * Copyright (c) 2020-2020 JetBrains s.r.o.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.jetbrains.downloadThis.ui

import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.Disposer
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import org.jetbrains.downloadThis.DownloadLauncher
import java.awt.Font

class DownloadAnyLinkDialog(
        private val project: Project,
        private val defaultDestinationDir: String,
        initialText: String
) : DialogWrapper(project, true) {

    private val linksEditor = createLinksEditor(initialText)
    private val downloadInProjectRoot = JBCheckBox(DOWNLOAD_IN_PROJECT_ROOT_TEXT, true)

    init {
        Disposer.register(disposable, linksEditor)

        init()
        isModal = false
        title = DOWNLOAD_ANY_LINK_TITLE
        setOKButtonText(DOWNLOAD_ANY_LINK_OK_TEXT)
    }

    override fun createCenterPanel() = JBUI.Panels.simplePanel(UIUtil.DEFAULT_HGAP, UIUtil.DEFAULT_VGAP)
            .addToTop(JBLabel(DOWNLOAD_ANY_LINK_MESSAGE).apply { font = font.deriveFont(Font.BOLD) })
            .addToCenter(linksEditor)
            .addToBottom(JBUI.Panels.simplePanel(UIUtil.DEFAULT_HGAP, UIUtil.DEFAULT_VGAP)
                    .addToCenter(JBLabel(DOWNLOAD_ANY_LINK_HINT).apply { font = font.deriveFont(Font.ITALIC) })
                    .addToBottom(downloadInProjectRoot)
            )

    override fun getPreferredFocusedComponent() = linksEditor.editorField

    override fun getDimensionServiceKey() = DownloadAnyLinkDialog::class.simpleName

    private fun createLinksEditor(initialText: String): LinksEditor {
        return LinksEditor(project).apply {
            editorField.apply {
                setCaretPosition(0)
                text = initialText
                addSettingsProvider {
                    // display at least several rows
                    val MIN_ROWS = 3

                    if ((it as EditorImpl).visibleLineCount < MIN_ROWS) {
                        verticalStretch = 1.5F
                    }
                }
            }
        }
    }

    private fun extractLinks(): List<String> {
        return linksEditor.editorField.text
                .split("\n")
                .map(String::trim)
                .filter(String::isNotEmpty)
    }

    override fun doValidate(): ValidationInfo? {
        if (extractLinks().isEmpty()) {
            return ValidationInfo(DOWNLOAD_ANY_LINK_VALIDATION_FAIL_TEXT)
        }

        return null
    }

    override fun doOKAction() {
        super.doOKAction()

        val destinationDir = when (downloadInProjectRoot.isSelected) {
            true -> defaultDestinationDir
            false -> {
                val folderDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
                val chosenFolder = FileChooser.chooseFile(folderDescriptor, project, null)
                chosenFolder?.canonicalPath ?: defaultDestinationDir
            }
        }

        extractLinks().forEach { link ->
            DownloadLauncher.runDownloadInBackground(project, link, destinationDir)
        }
    }

    companion object {

        private const val DOWNLOAD_IN_PROJECT_ROOT_TEXT = "Download to project root"
        private const val DOWNLOAD_ANY_LINK_VALIDATION_FAIL_TEXT = "Please enter at least one link"
        private const val DOWNLOAD_ANY_LINK_OK_TEXT = "Download"
        private const val DOWNLOAD_ANY_LINK_MESSAGE = "Add links to download:"
        private const val DOWNLOAD_ANY_LINK_HINT = "One link per line (HTTP links and local torrent files are supported)"
        private const val DOWNLOAD_ANY_LINK_TITLE = "Download Any Link"
    }
}
