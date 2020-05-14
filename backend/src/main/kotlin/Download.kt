
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/*fun getFileNameFromUrl(url: URL): String {
    val urlPath = url.path
    return urlPath.substring(urlPath.lastIndexOf('/') + 1)
}*/
private fun String.toFileName(): String = URLEncoder.encode(this, Charsets.UTF_8.toString())

internal fun downloadHttpLink(urlValue: String, destinationDir: File, statusListener: (DownloadStatus) -> Unit) {
    val url = try {
        URL(urlValue)
    } catch (e: MalformedURLException) {
        statusListener(Failed("Bad HTTP url", urlValue, destinationDir))
        return
    }

    val inputStream = url.openStream()
    val destinationFile = File(destinationDir, urlValue.toFileName())
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
