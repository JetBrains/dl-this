
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

fun getFileNameFromUrl(url: URL): String {
    val urlPath = url.path
    return urlPath.substring(urlPath.lastIndexOf('/') + 1)
}

fun download(urlValue: String, filePath: String = "D:/Tests", file_Name: String = "defaultSettings" ){
    val url = URL(urlValue)
    var fileName = file_Name
    if (fileName == "defaultSettings"){
        fileName = getFileNameFromUrl(url)
    }
    val inputStream = url.openStream()
    Files.copy(inputStream, Paths.get("$filePath/$fileName"), StandardCopyOption.REPLACE_EXISTING)

}
