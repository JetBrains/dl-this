import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.io.File

class TorrentTest: FreeSpec( {
    "torrent" {
        var lastStatus: DownloadStatus? = null
        val torrentFile = File("src/test/resources/wired-cd.torrent")  // https://webtorrent.io/free-torrents
        downloadTorrent(torrentFile = torrentFile, destinationDir = File("wired-cd")) { lastStatus = it }
        lastStatus.shouldBeInstanceOf<Finished> {
            val destinationFile = File(it.destinationFile)
            destinationFile.absolutePath shouldBe File("wired-cd").absolutePath

            destinationFile.listFiles()!!.forEach { f -> f.delete() }
            destinationFile.delete()
        }
    }
})