import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


fun download(urlValue: String, filepath: String = "D:/Tests/file_name.txt" ){
    val url = URL(urlValue)
    val inputStream = url.openStream()
    Files.copy(inputStream, Paths.get(filepath), StandardCopyOption.REPLACE_EXISTING)

}





/*InputStream inputStream = new URL("http://example.com/my-file-path.txt").openStream()
Files.copy(inputStream, Paths.get("/Users/username/Documents/file_name.txt"), StandardCopyOption.REPLACE_EXISTING)*/