package carbonara.relay.configuration


import com.typesafe.scalalogging.Logger
import io.circe.generic.auto._
import io.circe.syntax._
import org.apache.commons.configuration2.INIConfiguration
import org.apache.commons.configuration2.builder.fluent.Configurations

import scala.collection.JavaConverters._

class CarbonaraConfigurationFactory {
  private val logger = Logger(getClass)
  private val minPort:Int = 1
  private val maxPort:Int = 65535



  def getCarbonaraConfiguration(path: String): CarbonaraConfiguration = {
    logger.debug("Parsing configuration begin.")
    val configurations = new Configurations()
    val iniConfiguration = configurations.iniBuilder(path).getConfiguration
    val mapRelaySection: Map[String, String] = getSectionConfiguration(iniConfiguration, "relay")
    val carbonaraConfiguration = carbonaraConfigurationBuilder(mapRelaySection)
    logger.info(carbonaraConfiguration.asJson.toString())
    carbonaraConfiguration
  }

  private def getSectionConfiguration(iniConfiguration: INIConfiguration, section: String): Map[String, String] = {
    iniConfiguration.getSection(section).getKeys.asScala.toStream.map(key => key -> iniConfiguration.getSection(section).get(key.getClass, key)).toMap
  }

  private def carbonaraConfigurationBuilder(mapRelaySection: Map[String, String]) = {
    val destinations: Array[Destination] = getDestinations(mapRelaySection("DESTINATIONS"))

    val maxDataPointsPerMessage = mapRelaySection.get("MAX_DATAPOINTS_PER_MESSAGE").map(_.toInt).getOrElse(500)
    val timeToDeferSending = mapRelaySection.get("TIME_TO_DEFER_SENDING").map(_.toFloat).getOrElse(0.0001f)

    val relayMethod = mapRelaySection.getOrElse("RELAY_METHOD","rules")
    if (!isRelayMethod(relayMethod)) {
      sys.error(s"Bad [relay] RELAY_METHOD '$relayMethod'")
    }

    val maxQueueSize = mapRelaySection.get("MAX_QUEUE_SIZE").map(_.toInt).getOrElse(1000)
    val tcpPort = mapRelaySection.get("LINE_RECEIVER_PORT").map(_.toInt).getOrElse(3341)
    if (!isPort(tcpPort)) {
      sys.error(s"Bad [relay] LINE_RECEIVER_PORT '$tcpPort'")
    }

    val udpPort = mapRelaySection.get("UDP_RECEIVER_PORT").map(_.toInt).getOrElse(3341)

    if (!isPort(udpPort)) {
      sys.error(s"Bad [relay] UDP_RECEIVER_PORT '$udpPort'")
    }

    val isUdpActive = mapRelaySection.get("ENABLE_UDP_LISTENER").map(_.toBoolean).getOrElse(true)

    val routerHashType = mapRelaySection.getOrElse("ROUTER_HASH_TYPE","fnv1a_ch")
    if (!isRouterHashType(routerHashType)) {
      sys.error("ROUTER HASH TYPE not supported")
    }

    CarbonaraConfiguration(destinations, maxDataPointsPerMessage, timeToDeferSending, relayMethod, maxQueueSize, routerHashType, tcpPort, udpPort, isUdpActive)

  }

  private def getDestinations(destinations: String) = {
    destinations.split(",").map(destination => {
      destination.split(":") match {
        case Array(hostname, port, groupId) => Destination(hostname, port.toInt, groupId)
        case Array(hostname, port) => Destination(hostname, port.toInt)
        case _ => sys.error(s"Bad relay Destinations '$destination'")
      }
    })
  }

  private def isRelayMethod(relayMethod: String) = {
    val relayMethods: Set[String] = Set("rules", "fast-aggregated-hashing")
    relayMethods.contains(relayMethod)
  }

  private def isRouterHashType(routerHashType: String) = {
    routerHashType == "fnv1a_ch"
  }

  private def isPort(port: Int): Boolean = {
    (minPort to maxPort).contains(port)
  }


}
