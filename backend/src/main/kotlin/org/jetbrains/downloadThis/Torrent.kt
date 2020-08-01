package org.jetbrains.downloadThis

import com.turn.ttorrent.client.SimpleClient
import java.io.File
import java.net.InetAddress

private val client = SimpleClient()

fun downloadTorrent(torrentFile: File, destinationDir: File, statusListener: (DownloadStatus) -> Unit) {
    try {
        if (!destinationDir.exists()) {
            require(destinationDir.mkdirs()) { "Can't make dirs" }
        }
        val address = InetAddress.getLocalHost()
        client.downloadTorrent(torrentFile.absolutePath, destinationDir.absolutePath, address)
        client.stop()
    } catch (e: Exception) {
        statusListener(Failed("Can't download torrent", torrentFile.absolutePath, destinationDir))
        return
    }

    statusListener(Finished(
            destinationFile = destinationDir.absolutePath,
            query = torrentFile.absolutePath,
            destinationDir = destinationDir
    ))
}

