package carbonara.relay

import org.apache.commons.configuration2.builder.fluent.Configurations

class CarbonaraConfiguration {
  def getConfig(path:String): Unit = {
    println("validating conf")
    val configurations = new Configurations()
    val iniConfiguration = configurations.iniBuilder(path).getConfiguration
    println("ini config ok")
    iniConfiguration.getSections.forEach(section => {
      iniConfiguration.getSection(section).getKeys().forEachRemaining(key => {
        val value = iniConfiguration.getSection(section).get(key.getClass, key)
        println("section=" + section + " key=" + key + " value=" + value)
      })
    })
  }

}
