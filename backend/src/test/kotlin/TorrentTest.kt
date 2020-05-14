import io.kotest.core.spec.style.FreeSpec

class TorrentTest: FreeSpec( {
    "torrent" {
        downloadTorrent(inputPath = "src/test/resources/wired-cd.torrent", outputPath = ".")  // https://webtorrent.io/free-torrents
    }
})