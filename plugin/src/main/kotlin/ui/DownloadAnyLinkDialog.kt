package ui

import DownloadLauncher
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.Disposer
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import java.awt.Font

class DownloadAnyLinkDialog(val project: Project, val destinationDir: String) : DialogWrapper(project, true) {

    val linksEditor = createLinksEditor()

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
            .addToBottom(JBLabel(DOWNLOAD_ANY_LINK_HINT).apply { font = font.deriveFont(Font.ITALIC) })

    override fun getPreferredFocusedComponent() = linksEditor.editorField

    override fun getDimensionServiceKey() = DownloadAnyLinkDialog::class.simpleName

    private fun createLinksEditor(): LinksEditor {
        return LinksEditor(project).apply {
            editorField.apply {
                setCaretPosition(0)
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
        return listOf(linksEditor.editorField.text)  // todo: split links by line feeds
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

        val linkList = extractLinks()
        DownloadLauncher.runDownloadsInBackground(project, linkList, destinationDir)
    }

    companion object {

        private const val DOWNLOAD_ANY_LINK_VALIDATION_FAIL_TEXT = "Please enter at least one link"
        private const val DOWNLOAD_ANY_LINK_OK_TEXT = "Download"
        private const val DOWNLOAD_ANY_LINK_MESSAGE = "Add links to download:"
        private const val DOWNLOAD_ANY_LINK_HINT = "One link per line (HTTP links, Magnet links, and info-hashes are supported)"
        private const val DOWNLOAD_ANY_LINK_TITLE = "Download Any Link"
    }
}
