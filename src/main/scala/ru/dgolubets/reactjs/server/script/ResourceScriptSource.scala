package ru.dgolubets.reactjs.server.script
import java.io.{InputStreamReader, Reader}
import java.nio.charset.{Charset, StandardCharsets}

case class ResourceScriptSource(name: String,
                           classLoader: ClassLoader = Thread.currentThread.getContextClassLoader,
                           charset: Charset = StandardCharsets.UTF_8) extends ScriptSource {
  override def reader: Reader = {
    val stream = classLoader.getResourceAsStream(name)
    new InputStreamReader(stream, charset)
  }
}
