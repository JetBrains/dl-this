import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class DownloadTest : FreeSpec({

    "downloadLink"  {
        download("https://vk.com/doc132800647_548972002?hash=eccee483abefba8d39&dl=174d2cb1ea16038c30", "D:/Tests/1.txt")
    }
})
