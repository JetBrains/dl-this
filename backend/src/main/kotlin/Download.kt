
import java.io.File
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/*fun getFileNameFromUrl(url: URL): String {
    val urlPath = url.path
    return urlPath.substring(urlPath.lastIndexOf('/') + 1)
}*/
private fun String.toFileName(): String {
    return if (this.lastIndexOf("/") != this.length && "/" in this ){
        this.substring(this.lastIndexOf("/") + 1, this.length)
    } else{
        URLEncoder.encode(this, Charsets.UTF_8.toString())
    }
}

internal fun downloadHttpLink(urlValue: String, destinationDir: File, statusListener: (DownloadStatus) -> Unit) {
    val url = try {
        URL(urlValue)
    } catch (e: MalformedURLException) {
        statusListener(Failed("Bad HTTP url", urlValue, destinationDir))
        return
    }
    var destinationFile = File(destinationDir, urlValue.toFileName())


    val httpConn = url.openConnection() as HttpURLConnection
    val disposition = httpConn.getHeaderField("Content-Disposition")
    val contentType= httpConn.getHeaderField("Content-Type")

    if (disposition != null) {
        var fileName = ""
        val index = disposition.indexOf("filename=")
        if (index > 0) {
            fileName = disposition.substring(index + 10, disposition.length - 1)
            if( "\"" in fileName){
                fileName = fileName.substringBefore("\"")
            }
        }
        else{
            fileName = httpConn.toString()
            fileName = fileName.substring(fileName.lastIndexOf("/"))
            if ("?" in fileName){
                fileName = fileName.substring(0, fileName.indexOf("?"))
            }
        }
        destinationFile = File(destinationDir, fileName)
    }

    val inputStream = url.openStream()
    Files.copy(inputStream, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
    statusListener(Finished(destinationFile.absolutePath, urlValue, destinationDir))
}

private fun isMagnetLink(query: String) = false
private fun isTorrentHash(query: String) = false
private fun isCurlQuery(query: String) = query.startsWith("curl ")
private fun isHttpLink(query: String) = query.startsWith("http")  // todo: HTTP links can omit HTTP prefix

fun download(query: String, destinationDir: File, statusListener: (DownloadStatus) -> Unit) {
    if (!destinationDir.isDirectory) {
        statusListener(Failed("${destinationDir.absolutePath} is not a directory", query, destinationDir))
        return
    }

    when {
        isMagnetLink(query) -> statusListener(Failed("Magnet links are not supported now", query, destinationDir))
        isTorrentHash(query) -> statusListener(Failed("Torrent hashes are not supported now", query, destinationDir))
        isCurlQuery(query) -> statusListener(Failed("Curl queries are not supported now", query, destinationDir))
        isHttpLink(query) -> downloadHttpLink(query, destinationDir, statusListener)
        else -> statusListener(Failed("Can't understand link type", query, destinationDir))
    }
}
