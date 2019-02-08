package carbonara.relay

import java.nio.file.{Files, Paths}

import scopt.{DefaultOParserSetup, OParser, OParserBuilder, OParserSetup}

final case class Arguments(metricsPort: MetricsPort = MetricsPort(), configurationPath: Configuration=Configuration(),configurationValidation:ConfigurationValidation=ConfigurationValidation())

final case class MetricsPort(value: Int = 7080) extends AnyVal

final case class Configuration(value: String = "/Users/m.moulin/graphite-conf/conf/carbon.conf") extends AnyVal

final case class ConfigurationValidation(value:Boolean =false) extends AnyVal
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

object Configuration {
  def validate(path: String)(implicit builder: OParserBuilder[Arguments]): Either[String, Unit] = {
    if (Files.isRegularFile(Paths.get(path)))
      {
        builder.success
      }
    else{
      builder.failure("not a readable file")
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
        .action((value, arguments) => arguments.copy(metricsPort = MetricsPort(value))),
      builder.opt[String]("conf").text("Configuration path").validate(Configuration.validate).action((value,arguments)=> arguments.copy(metricsPort = arguments.metricsPort,configurationPath = Configuration(value))),
      builder.opt[Boolean]("validate-conf")
        .text("Validate configuration")
        .action((value, arguments) => arguments.copy(metricsPort = arguments.metricsPort,configurationPath = arguments.configurationPath,configurationValidation = ConfigurationValidation(value))))
  }

  def parse(args: Array[String]): Option[Arguments] = {
    OParser.parse(parser, args, Arguments(), setup)
  }
}
