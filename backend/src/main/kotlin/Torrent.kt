import com.turn.ttorrent.client.Client
import com.turn.ttorrent.client.SharedTorrent
import java.io.File
import java.net.InetAddress



fun downloadTorrent(link: String = "", inputPath: String = "", outputPath: String = ""){
    if (link.isBlank() ){
        val client = Client(
                InetAddress.getLocalHost(),
                SharedTorrent.fromFile(File(inputPath) , File(outputPath))
        )
        client.setMaxDownloadRate(0.0)
        client.download()
        client.waitForCompletion()
    }
    else{

    }

}

