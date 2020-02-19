package org.jmotor.scalikejdbc

import java.util.Properties

import com.typesafe.config.Config
import scala.jdk.CollectionConverters._

/**
 * Component:
 * Description:
 * Date: 2018/8/21
 *
 * @author AI
 */
object DatasourceConfig {

  def getProperties(config: Config, name: String): Properties = {
    val properties = new Properties()
    properties.setProperty("dataSourceClassName", config.getString("scalike.dataSourceClassName"))
    val databaseConfig = config.getConfig("scalike").getConfig(name)
    databaseConfig.entrySet().asScala.foreach { e ⇒
      properties.setProperty(e.getKey, e.getValue.unwrapped().toString)
    }
    properties
  }

}
