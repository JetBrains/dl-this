package org.jetbrains.downloadThis

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.io.File

class DownloadTest : FreeSpec({

    "downloadHttpLink" - {
        "Downloading URL without extension should predict correct extension" - {
            "html" {
                var lastStatus: DownloadStatus? = null
                val url = "https://example.com"
                downloadHttpLink(url, File(".")) { lastStatus = it }
                lastStatus.shouldBeInstanceOf<Finished> {
                    val destinationFile = File(it.destinationFile)
                    setOf("html", "htm") shouldContain destinationFile.extension.toLowerCase()

                    destinationFile.delete()
                }
            }
            "json" {
                var lastStatus: DownloadStatus? = null
                val url = "http://dummy.restapiexample.com/api/v1/employees"
                downloadHttpLink(url, File(".")) { lastStatus = it }
                lastStatus.shouldBeInstanceOf<Finished> {
                    val destinationFile = File(it.destinationFile)
                    destinationFile.extension.toLowerCase() shouldBe "json"

                    destinationFile.delete()
                }
            }
        }
        "Downloading URL with extension should give correct extension" - {
            "png" {
                var lastStatus: DownloadStatus? = null
                val url = "https://en.wikipedia.org/static/images/project-logos/enwiki.png"
                downloadHttpLink(url, File(".")) { lastStatus = it }
                lastStatus.shouldBeInstanceOf<Finished> {
                    val destinationFile = File(it.destinationFile)
                    destinationFile.extension shouldBe "png"

                    destinationFile.delete()
                }
            }
        }
        "Downloading invalid URL should give failed result" {
            var lastStatus: DownloadStatus? = null
            val url = "kek"
            downloadHttpLink(url, File(".")) { lastStatus = it }
            lastStatus.shouldBeInstanceOf<Failed>()
        }
        "Downloading URL without protocol should work" - {
            "html" {
                var lastStatus: DownloadStatus? = null
                val url = "example.com"
                downloadHttpLink(url, File(".")) { lastStatus = it }
                lastStatus.shouldBeInstanceOf<Finished> {
                    val destinationFile = File(it.destinationFile)
                    setOf("html", "htm") shouldContain destinationFile.extension.toLowerCase()

                    destinationFile.delete()
                }
            }
        }
    }
})
