package ru.dgolubets.reactjs.server.util

import java.io.{File, FileInputStream, InputStream}
import java.security.MessageDigest

import akka.util.ByteString

private[server] object MD5 {

  def ofStream(stream: InputStream, bufferSize: Int = 4096): ByteString = {
    val md = MessageDigest.getInstance("MD5")
    val buffer = new Array[Byte](bufferSize)
    var read = 0
    do {
      read = stream.read(buffer)
      if (read > 0) {
        md.update(buffer, 0, read)
      }
    } while (read > 0)

    ByteString(md.digest())
  }

  def ofFile(file: File, bufferSize: Int = 4096): ByteString = {
    val stream = new FileInputStream(file)
    try {
      ofStream(stream, bufferSize)
    }
    finally {
      stream.close()
    }
  }
}
