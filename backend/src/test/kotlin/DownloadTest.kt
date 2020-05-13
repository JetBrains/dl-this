import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class DownloadTest : FreeSpec({
    "sqr should return square" {
        sqr(5) shouldBe 25
    }

    "downloadLink" - {
        "(google.com, ...)" {
            downloadLink("https://goodle.rtas", "dsadas")

            // todo: check content of "dsadas" file
        }

        "(kek, ...)" {
            shouldThrow<BadUrlException> {
                downloadLink("goodle.rtas", "dsadas")
            }
        }
    }
})
