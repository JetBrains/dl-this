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

class DownloadAnyLinkDialog(
        private val project: Project,
        private val destinationDir: String,
        initialText: String
) : DialogWrapper(project, true) {

    private val linksEditor = createLinksEditor(initialText)

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

        extractLinks().forEach { link ->
            DownloadLauncher.runDownloadInBackground(project, link, destinationDir)
        }
    }

    companion object {

        private const val DOWNLOAD_ANY_LINK_VALIDATION_FAIL_TEXT = "Please enter at least one link"
        private const val DOWNLOAD_ANY_LINK_OK_TEXT = "Download"
        private const val DOWNLOAD_ANY_LINK_MESSAGE = "Add links to download:"
        private const val DOWNLOAD_ANY_LINK_HINT = "One link per line (HTTP links and local torrent files are supported)"
        private const val DOWNLOAD_ANY_LINK_TITLE = "Download Any Link"
    }
}
