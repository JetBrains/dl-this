import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

class DownloadAnyLink : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val currentProject = event.project
        val destinationDir = getDestinationDir(currentProject)

        val links = Messages.showInputDialog(  // todo: use dialog like "Rename commit" to support multiple lines
                currentProject,
                DOWNLOAD_ANY_LINK_MESSAGE,
                DOWNLOAD_ANY_LINK_TITLE,
                Messages.getQuestionIcon()
        )

        if (links != null) {
            val linkList = listOf(links)  // todo: split links by line feeds

            linkList.forEach { link ->
                try {
                    LOG.debug("Downloading '$link' to '$destinationDir'")
                    downloadByURL(link, destinationDir)
                } catch (e: BadUrlException) {
                    LOG.error("Downloading '$link' to '$destinationDir' failed", e)
                    Messages.showErrorDialog(
                            currentProject,
                            DOWNLOAD_ANY_LINK_CANT_RECOGNIZE_LINK_MESSAGE.format(link),
                            DOWNLOAD_ANY_LINK_CANT_RECOGNIZE_LINK_TITLE
                    )
                }
            }
        }
    }

    companion object {

        private val LOG = Logger.getInstance(DownloadAnyLink::class.java)

        private const val DOWNLOAD_ANY_LINK_MESSAGE = "Enter links to download:"
        private const val DOWNLOAD_ANY_LINK_TITLE = "Download Any Link"

        private const val DOWNLOAD_ANY_LINK_CANT_RECOGNIZE_LINK_MESSAGE = "Bad link: %s"
        private const val DOWNLOAD_ANY_LINK_CANT_RECOGNIZE_LINK_TITLE = "Can't recognize link"

        private fun getDestinationDir(project: Project?): String {
            return ProjectUtil.getBaseDir()  // todo: use project to determine dir
        }
    }
}