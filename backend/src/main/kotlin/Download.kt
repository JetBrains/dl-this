fun sqr(i: Int): Int = i * i

class BadUrlException : Throwable()

fun downloadLink(downloadUrl: String, destinationPath: String) {
    if ("http" !in downloadUrl) {
        throw BadUrlException()
    }
}
