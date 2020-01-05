/*
 * Copyright 2019 Matthias Hanisch (reallyinsane)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.mathan.trainsimulator.service.presenter;

import static org.assertj.core.api.Assertions.assertThat;

import io.mathan.trainsimulator.model.Control;
import io.mathan.trainsimulator.service.Present;
import io.mathan.trainsimulator.service.Presenter;
import org.junit.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Import;

public class PresenterWrongSignatureTest {

  @TestConfiguration
  @Import({Presenter.class, PresentBean.class})
  static class Configuration {

  }

  static class PresentBean {
    @Present(controls = {Control.Pzb55})
    public void present(Control control) {

    }
  }

  @Test
  public void failed() {
    ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withUserConfiguration(Configuration.class)
        .run(context -> {
          assertThat(context).hasFailed();
        });
  }

}
