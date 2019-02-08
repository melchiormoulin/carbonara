package carbonara.relay.configuration

case class CarbonaraConfiguration(destinations: Array[Destination],
                                  maxDataPointsPerMessage: Int,
                                  timeToDeferSending: Float,
                                  relayMethod: String,
                                  maxQueueSize: Int,
                                  routerHashType: String,
                                  tcpPort: Int,
                                  udpPort: Int,
                                  isUdpActive: Boolean)