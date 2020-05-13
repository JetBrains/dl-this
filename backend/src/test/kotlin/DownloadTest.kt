import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class DownloadTest : FreeSpec({

    "downloadLink" - {
       "Download with default settings" {
            downloadByURL("https://vk.com/doc132800647_548972002?hash=eccee483abefba8d39&dl=174d2cb1ea16038c30")
        }
        "Download with parameters" {
            downloadByURL("https://bipbap.ru/wp-content/uploads/2017/10/0_8eb56_842bba74_XL-640x400.jpg", file_Name = "image.jpg" )
        }
        "Download error" {
            shouldThrow<BadUrlException> {
                downloadByURL("kek")
            }
        }
    }

})
