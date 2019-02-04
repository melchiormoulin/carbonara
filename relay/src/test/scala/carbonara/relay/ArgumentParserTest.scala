package carbonara.relay

import java.io.ByteArrayOutputStream

import org.scalatest.{FunSuite, Matchers}

class ArgumentParserTest extends FunSuite with Matchers {
  test("parse should provide default values when no argument is provided") {
    val result = ArgumentParser.parse(Array.empty[String])

    result shouldBe Some(Arguments())
  }

  test("parse should parse metrics HTTP port") {
    val metricsPort = 1234
    val result = ArgumentParser.parse(Array("--metrics-port", s"$metricsPort"))

    result shouldBe Some(Arguments(MetricsPort(metricsPort)))
  }

  test("parse should validate metrics port range") {
    val errCaptureStream = new ByteArrayOutputStream

    val metricsPort = 123456789
    val result = Console.withErr(errCaptureStream) {
      ArgumentParser.parse(Array("--metrics-port", s"$metricsPort"))
    }

    val errCapture = errCaptureStream.toString
    result shouldBe None
    errCapture should startWith("Error: Metrics port")
    errCapture should include(s"provided: $metricsPort")
  }

  test("parse should fail on unknown parameters") {
    val errCaptureStream = new ByteArrayOutputStream

    val unknownOption = "--truc-stuff"
    val unknownArgument = "wat"

    val result = Console.withErr(errCaptureStream) {
      ArgumentParser.parse(Array(unknownOption, unknownArgument))
    }

    result shouldBe None
    val errCapture = errCaptureStream.toString
    errCapture should include(s"Error: Unknown option $unknownOption")
    errCapture should include(s"Error: Unknown argument '$unknownArgument'")
  }
}
