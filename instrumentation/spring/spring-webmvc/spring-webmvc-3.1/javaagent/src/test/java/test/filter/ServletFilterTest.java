/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package test.filter;

import com.google.common.collect.ImmutableMap;
import filter.AbstractServletFilterTest;
import filter.FilteredAppConfig;
import io.opentelemetry.instrumentation.testing.junit.InstrumentationExtension;
import io.opentelemetry.instrumentation.testing.junit.http.HttpServerInstrumentationExtension;
import io.opentelemetry.instrumentation.testing.junit.http.HttpServerTestOptions;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import test.boot.SecurityConfig;

class ServletFilterTest extends AbstractServletFilterTest {

  @RegisterExtension
  private static final InstrumentationExtension testing =
      HttpServerInstrumentationExtension.forAgent();

  @Override
  protected Class<?> securityConfigClass() {
    return SecurityConfig.class;
  }

  @Override
  protected Class<?> filterConfigClass() {
    return ServletFilterConfig.class;
  }

  @Override
  protected ConfigurableApplicationContext setupServer() {
    SpringApplication app =
        new SpringApplication(FilteredAppConfig.class, securityConfigClass(), filterConfigClass());
    app.setDefaultProperties(
        ImmutableMap.of("server.port", port, "server.error.include-message", "always"));
    return app.run();
  }

  @Override
  protected void configure(HttpServerTestOptions options) {
    super.configure(options);
    options.setResponseCodeOnNonStandardHttpMethod(
        Boolean.getBoolean("testLatestDeps") ? 500 : 200);
  }
}
