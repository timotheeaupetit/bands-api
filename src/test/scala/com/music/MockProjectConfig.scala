package com.music

import com.music.utils.ProjectConfiguration.ProjectConfig
import org.specs2.mock.Mockito

trait MockProjectConfig extends ProjectConfig with Mockito  {
  def projectConfig: ProjectConfig = mock[ProjectConfig]
}
