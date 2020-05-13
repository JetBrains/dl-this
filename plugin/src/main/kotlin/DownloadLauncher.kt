import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import javax.swing.SwingUtilities

object DownloadLauncher {

    private val LOG = logger<DownloadLauncher>()

    private const val DOWNLOAD_ANY_LINK_CANT_RECOGNIZE_LINK_MESSAGE = "Bad link: %s"
    private const val DOWNLOAD_ANY_LINK_CANT_RECOGNIZE_LINK_TITLE = "Can't recognize link"

    private const val DOWNLOAD_IN_BACKGROUND_TITLE = "Downloading this: %s"

    fun runDownloadsInBackground(project: Project, links: Iterable<String>, destinationDir: String) {
        links.forEach { link ->
            runDownloadInBackground(project, link, destinationDir)
        }
    }

    private fun runDownloadInBackground(project: Project, link: String, destinationDir: String) {
        object : Task.Backgroundable(project, DOWNLOAD_IN_BACKGROUND_TITLE) {
            override fun run(indicator: ProgressIndicator) {
                try {
                    LOG.debug("Downloading '$link' to '$destinationDir'")
                    downloadByURL(link, destinationDir)
                } catch (e: BadUrlException) {
                    LOG.warn("Downloading '$link' to '$destinationDir' failed", e)
                    SwingUtilities.invokeLater {
                        Messages.showErrorDialog(
                                project,
                                DOWNLOAD_ANY_LINK_CANT_RECOGNIZE_LINK_MESSAGE.format(link),
                                DOWNLOAD_ANY_LINK_CANT_RECOGNIZE_LINK_TITLE
                        )
                    }
                }
            }
        }.queue()
    }
}
