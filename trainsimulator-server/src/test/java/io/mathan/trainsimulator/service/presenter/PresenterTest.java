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
import io.mathan.trainsimulator.model.ControlData;
import io.mathan.trainsimulator.service.Event;
import io.mathan.trainsimulator.service.Present;
import io.mathan.trainsimulator.service.Presenter;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Import;

public class PresenterTest {
  private static int calls = 0;

  @TestConfiguration
  @Import({Presenter.class, PresentBean.class})
  static class Configuration {

  }

  public static class PresentBean {
    @Present(controls = {Control.Pzb55})
    public void present(Event event) {
      calls++;
    }
  }

  @Test
  public void pzb55() {
    ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withUserConfiguration(Configuration.class)
        .run(context -> {
          assertThat(context).getBean(Presenter.class).isNotNull();
          Control control = Control.Pzb55;
          ControlData data = new ControlData();
          int calls = PresenterTest.calls;
          context.getBean(Presenter.class).present(control, data);
          Assert.assertTrue(PresenterTest.calls == calls + 1);
        });
  }

  @Test
  public void pzb70() {
    ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withUserConfiguration(Configuration.class)
        .run(context -> {
          assertThat(context).getBean(Presenter.class).isNotNull();
          Control control = Control.Pzb70;
          ControlData data = new ControlData();
          int calls = PresenterTest.calls;
          context.getBean(Presenter.class).present(control, data);
          Assert.assertTrue(PresenterTest.calls == calls);
        });
  }

}
