package carbonara.relay

import java.net.InetSocketAddress

import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.HTTPServer

case class CNContext(registry: CollectorRegistry)

object Bootstrap {
  def run(args: Array[String]): CNContext = {
    val registry = CollectorRegistry.defaultRegistry
    configureMetrics(registry)

    CNContext(registry)
  }

  private def configureMetrics(registry: CollectorRegistry) = {
    val socketAddress = new InetSocketAddress("0.0.0.0", 7080)
    val server = new HTTPServer(socketAddress, registry, true)
    sys.addShutdownHook {
      server.stop()
    }
  }
}
