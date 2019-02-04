package carbonara.relay

import scopt.{DefaultOParserSetup, OParser, OParserBuilder, OParserSetup}

final case class Arguments(metricsPort: MetricsPort = MetricsPort())

final case class MetricsPort(value: Int = 7080) extends AnyVal

object MetricsPort {
  private val minPort = 1
  private val maxPort = 65535

  def validate(value: Int)(implicit builder: OParserBuilder[Arguments]): Either[String, Unit] = {
    if ((minPort to maxPort).contains(value)) {
      builder.success
    }
    else {
      builder.failure(s"Metrics port should be defined between $minPort and $maxPort (provided: $value)")
    }
  }
}

object ArgumentParser {
  private implicit val builder: OParserBuilder[Arguments] = OParser.builder[Arguments]
  private val setup: OParserSetup = new DefaultOParserSetup {
    override def showUsageOnError = Some(true)
  }
  private val parser = {
    import builder._
    OParser.sequence(
      programName("carbonara-relay"),
      builder.opt[Int]("metrics-port")
        .text("Port used to expose application metrics")
        .validate(MetricsPort.validate)
        .action((value, arguments) => arguments.copy(metricsPort = MetricsPort(value)))
    )
  }

  def parse(args: Array[String]): Option[Arguments] = {
    OParser.parse(parser, args, Arguments(), setup)
  }
}
