package com.devy.trafficstudypart3.config;

import org.apache.catalina.core.StandardThreadExecutor;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11NioProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addConnectorCustomizers(connector -> {

            ProtocolHandler protocolHandler = connector.getProtocolHandler();

            StandardThreadExecutor executor = new CustomThreadExecutor();
            executor.setMaxThreads(1);
            executor.setMinSpareThreads(1);
            executor.setMaxQueueSize(10);
            protocolHandler.setExecutor(executor);
        });
    }

    static class CustomThreadExecutor extends StandardThreadExecutor {
        private Logger log = LoggerFactory.getLogger(CustomThreadExecutor.class);
        @Override
        public void execute(Runnable command) {
            super.execute(command);
            log.info("🚀 Task is executed! Queue Size is {}", this.getQueueSize());
        }
    }
}
