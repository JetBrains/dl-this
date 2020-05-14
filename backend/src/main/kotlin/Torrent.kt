import com.turn.ttorrent.client.SimpleClient
import java.net.InetAddress

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
    }
}

