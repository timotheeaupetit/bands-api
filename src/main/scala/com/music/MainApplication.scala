package com.music

import com.music.api.connector.Connector
import com.music.api.utils.ProjectConfiguration
import com.music.api.utils.ProjectConfiguration._

object MainApplication extends App {
  ProjectConfiguration
    .projectConfiguration()
    .fold(
      Launcher.showConfigError, { configuration: ProjectConfig =>
        new Connector(configuration)
          .startServer("0.0.0.0", configuration.appConfig.port)
      }
    )

}
