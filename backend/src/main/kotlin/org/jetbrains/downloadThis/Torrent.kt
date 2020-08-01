/*
 * MIT License
 *
 * Copyright (c) 2020-2020 JetBrains s.r.o.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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

