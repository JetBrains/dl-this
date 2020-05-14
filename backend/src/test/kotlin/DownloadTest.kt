import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.io.File

class DownloadTest : FreeSpec({

    "downloadHttpLink" - {
        "Download without extension".config(enabled = false) {
            var lastStatus: DownloadStatus? = null
            val url = "https://vk.com/doc132800647_548972002?hash=eccee483abefba8d39&dl=174d2cb1ea16038c30"
            downloadHttpLink(url, File(".")) { lastStatus = it }
            lastStatus.shouldBeInstanceOf<Finished> {
                File(it.destinationFile).extension shouldBe "txt"
            }
        }
        "Download with extension" {
            var lastStatus: DownloadStatus? = null
            val url = "https://bipbap.ru/wp-content/uploads/2017/10/0_8eb56_842bba74_XL-640x400.jpg"
            downloadHttpLink(url, File(".")) { lastStatus = it }
            lastStatus.shouldBeInstanceOf<Finished> {
                File(it.destinationFile).extension shouldBe "jpg"
            }
        }
        "Download error" {
            var lastStatus: DownloadStatus? = null
            val url = "kek"
            downloadHttpLink(url, File(".")) { lastStatus = it }
            lastStatus.shouldBeInstanceOf<Failed>()
        }
        "Download without protocol (HTML page)".config(enabled = false) {
            var lastStatus: DownloadStatus? = null
            val url = "example.com"
            downloadHttpLink(url, File(".")) { lastStatus = it }
            lastStatus.shouldBeInstanceOf<Finished> {
                File(it.destinationFile).extension shouldBe "html"
            }
        }
        "Download without protocol (REST API)".config(enabled = false) {
            var lastStatus: DownloadStatus? = null
            val url = "reqres.in/api/users?page=2"
            downloadHttpLink(url, File(".")) { lastStatus = it }
            lastStatus.shouldBeInstanceOf<Finished> {
                File(it.destinationFile).extension shouldBe "json"
            }
        }
    }
})
