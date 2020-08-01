package org.jetbrains.downloadThis

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import java.io.File
import javax.swing.SwingUtilities

object DownloadLauncher {

    private val LOG = logger<DownloadLauncher>()

    private const val DOWNLOAD_ANY_LINK_CANT_DOWNLOAD_LINK_MESSAGE = "%s: %s"
    private const val DOWNLOAD_ANY_LINK_CANT_DOWNLOAD_LINK_TITLE = "Can't download link"

    private const val DOWNLOAD_IN_BACKGROUND_TITLE = "Downloading this: %s"

    fun runDownloadInBackground(project: Project, link: String, destinationDir: String) {
        object : Task.Backgroundable(project, DOWNLOAD_IN_BACKGROUND_TITLE.format(link)) {
            override fun run(indicator: ProgressIndicator) {
                LOG.debug("Downloading '$link' to '$destinationDir'")
                download(link, File(destinationDir)) {
                    when (it) {
                        is Downloading -> SwingUtilities.invokeLater {
                            indicator.fraction = it.fraction
                        }

                        is Failed -> SwingUtilities.invokeLater {
                            Messages.showErrorDialog(
                                    project,
                                    DOWNLOAD_ANY_LINK_CANT_DOWNLOAD_LINK_MESSAGE.format(it.reason, link),
                                    DOWNLOAD_ANY_LINK_CANT_DOWNLOAD_LINK_TITLE
                            )
                        }

                        is Finished -> DownloadFinishNotifier.notify(
                                project = project,
                                title = "Download finished",
                                content = """<a href="${it.destinationFile}">${it.destinationFile}</a>"""  // todo: make it openable by IDE
                        )
                    }
                }
            }
        }.queue()
    }
}