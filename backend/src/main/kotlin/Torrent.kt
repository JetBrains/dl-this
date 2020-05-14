import com.turn.ttorrent.client.SimpleClient
import java.net.InetAddress
import com.turn.ttorrent.tracker.TrackedTorrent
import com.turn.ttorrent.tracker.Tracker
import java.io.File
import java.io.FilenameFilter



fun downloadTorrent(link: String = "", inputPath: String = "", outputPath: String = ""){
    if (link.isBlank() ){
        val client = SimpleClient()
        val address = InetAddress.getLocalHost()

        try {
            client.downloadTorrent(inputPath,
                    outputPath,
                    address)
            //download finished
        } catch (e: Exception) {
            //download failed, see exception for details
            e.printStackTrace()
        }
        client.stop()

       /* val tracker = Tracker(6969)
        val filter = FilenameFilter { _, name -> name.endsWith(".torrent") }

        for (f in File(inputPath).listFiles(filter)) {
            tracker.announce(TrackedTorrent.load(f))
        }
        tracker.setAcceptForeignTorrents(true)
        tracker.start(true)
        tracker.stop()*/

    }
}

