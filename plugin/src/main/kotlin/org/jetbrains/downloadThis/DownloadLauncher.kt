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
