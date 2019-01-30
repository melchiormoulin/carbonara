package carbonara.relay

import io.prometheus.client.exporter.HTTPServer

object Bootstrap {
  def run(args: Array[String]): Unit = {
    configureMetrics()
  }

  private def configureMetrics() = {
    val server = new HTTPServer("0.0.0.0", 7080, true)
    sys.addShutdownHook {
      server.stop()
    }
  }
}
