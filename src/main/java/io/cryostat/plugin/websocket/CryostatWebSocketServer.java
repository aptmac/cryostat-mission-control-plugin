/*
 * Copyright The Cryostat Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cryostat.plugin.websocket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.eclipse.jetty.ee9.servlet.ServletContextHandler;
import org.eclipse.jetty.ee9.websocket.server.config.JettyWebSocketServletContainerInitializer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import io.cryostat.plugin.CryostatPlugin;

public class CryostatWebSocketServer {
    private Server server;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public CryostatWebSocketServer(int port) {
        executorService.execute(() -> startServer(port));
    }

    public void startServer(int port) {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setHost("127.0.0.1");
        connector.setPort(port);
        server.addConnector(connector);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        JettyWebSocketServletContainerInitializer.configure(
                context,
                (servletContext, container) -> {
                    container.setMaxBinaryMessageSize(Long.MAX_VALUE);
                    container.addMapping("/cryostat/*", new CryostatEndpointCreator());
                });

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            CryostatPlugin.getLogger().log(Level.SEVERE, "Failed to start websocket server", e);
        }
    }

    public void shutdown() throws Exception {
        server.stop();
    }
}
