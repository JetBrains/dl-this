
import java.io.File
import java.net.URL
import java.net.URLEncoder
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class BadUrlException : Throwable()

/*fun getFileNameFromUrl(url: URL): String {
    val urlPath = url.path
    return urlPath.substring(urlPath.lastIndexOf('/') + 1)
}*/
fun String.toFileName() = URLEncoder.encode(this, Charsets.UTF_8.toString())

fun downloadByURL(urlValue: String, file: File){
    if ("http" !in urlValue) {
        throw BadUrlException()
    }
    val url = URL(urlValue)



    val inputStream = url.openStream()
    Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING)

}
