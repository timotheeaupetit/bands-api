package com.music

import com.music.connector.Connector
import com.music.utils.ProjectConfiguration
import com.music.utils.ProjectConfiguration._

object MainApplication extends App {
  ProjectConfiguration
    .projectConfiguration()
    .fold(
      Launcher.showConfigError, { (configuration: ProjectConfig) =>
        new Connector(configuration)
          .startServer("0.0.0.0", configuration.appConfig.port)
      }
    )

}
