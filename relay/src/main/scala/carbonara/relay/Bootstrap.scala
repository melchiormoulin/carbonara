package carbonara.relay

import java.net.InetSocketAddress

import com.typesafe.scalalogging.Logger
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.HTTPServer

case class CNContext(registry: CollectorRegistry)

object Bootstrap {

  private val logger = Logger(getClass)

  def run(args: Array[String]): CNContext = {
    val arguments = ArgumentParser.parse(args) orDie()

    val registry = CollectorRegistry.defaultRegistry
    configureMetrics(registry, arguments.metricsPort)

    CNContext(registry)
  }

  private def configureMetrics(registry: CollectorRegistry, metricsPort: MetricsPort) = {
    val server = new HTTPServer(new InetSocketAddress("0.0.0.0", metricsPort.value), registry, true)
    logger.info(s"Exposing metrics on port ${metricsPort.value}")
    sys.addShutdownHook {
      server.stop()
    }
  }

  implicit class OptionOrDie[T](value: Option[T]) {
    def orDie(exitCode: Int = 1): T = {
      value match {
        case Some(v) => v
        case _       => sys.exit(exitCode)
      }
    }
  }

}
