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
