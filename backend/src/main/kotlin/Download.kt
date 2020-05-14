import MimeTypes.getDefaultExt
import java.io.File
import java.io.IOException
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
        this.substringAfterLast("/")
    } else{
        URLEncoder.encode(this, Charsets.UTF_8.toString())
    }
}

internal fun downloadHttpLink(urlValue: String, destinationDir: File, statusListener: (DownloadStatus) -> Unit) {
    val url = try {
        if("http" !in urlValue){
            URL("https://$urlValue")
        }
        else {
            URL(urlValue)
        }

    } catch (e: MalformedURLException) {
        statusListener(Failed("Bad HTTP url", urlValue, destinationDir))
        return
    }
    var destinationFile = File(destinationDir, urlValue.toFileName())


    val httpConn = url.openConnection() as HttpURLConnection
    val disposition = httpConn.getHeaderField("Content-Disposition")
    val contentType: String = try {
        httpConn.getHeaderField("Content-Type")
    } catch (e: IllegalStateException){
        statusListener(Failed("Nothing to download", urlValue, destinationDir))
        return
    }

    val ext = getDefaultExt(contentType.substringBefore(";"))
    var indexFlag = false
    if (disposition != null) {
        var fileName: String
        val index = disposition.indexOf("filename=")
        if (index > 0) {
            indexFlag = true
            fileName = disposition.substring(index + 10, disposition.length - 1)
            if( "\"" in fileName){
                fileName = fileName.substringBefore("\"")
            }
        } else{
            fileName = httpConn.toString()
            fileName = fileName.substring(fileName.lastIndexOf("/"))
            if ("?" in fileName){
                fileName = fileName.substring(0, fileName.indexOf("?"))
            }
        }
        destinationFile = File(destinationDir, fileName)
    }

    if (ext != "unknown" && !indexFlag){
        if (destinationFile.extension != ext){
            destinationFile = File(destinationDir, "${urlValue.toFileName()}.$ext")
        }
    }

    val inputStream = try {
        url.openStream()
    } catch (e: IOException) {
        statusListener(Failed("Can't connect", urlValue, destinationDir))
        return
    }
    Files.copy(inputStream, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
    statusListener(Finished(destinationFile.absolutePath, urlValue, destinationDir))
}

private fun isLocalTorrentFile(query: String) = File(query).let { it.exists() && it.extension == "torrent" }
private fun isMagnetLink(query: String) = query.startsWith("magnet:")
private fun isTorrentHash(query: String) = false
private fun isCurlQuery(query: String) = query.startsWith("curl ")
private fun isHttpLink(query: String) = query.startsWith("http")  // todo: HTTP links can omit HTTP prefix

fun download(query: String, destinationDir: File, statusListener: (DownloadStatus) -> Unit) {
    if (!destinationDir.isDirectory) {
        statusListener(Failed("${destinationDir.absolutePath} is not a directory", query, destinationDir))
        return
    }

    when {
        isLocalTorrentFile(query) -> downloadTorrent(File(query), destinationDir, statusListener)
        isMagnetLink(query) -> statusListener(Failed("Magnet links are not supported now", query, destinationDir))
        isTorrentHash(query) -> statusListener(Failed("Torrent hashes are not supported now", query, destinationDir))
        isCurlQuery(query) -> statusListener(Failed("Curl queries are not supported now", query, destinationDir))
        isHttpLink(query) -> downloadHttpLink(query, destinationDir, statusListener)
        else -> statusListener(Failed("Can't understand link type", query, destinationDir))
    }
}