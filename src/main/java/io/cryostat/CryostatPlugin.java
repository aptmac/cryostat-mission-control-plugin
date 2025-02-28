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
package io.cryostat;

import io.cryostat.websocket.CryostatWebSocketServer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class CryostatPlugin implements BundleActivator, IStartup {

    private static final Logger LOGGER = Logger.getLogger(CryostatPlugin.class.getName());
    private static BundleContext context;

    private CryostatWebSocketServer server;

    static BundleContext getContext() {
        return context;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public void start(BundleContext bundleContext) throws Exception {
        CryostatPlugin.context = bundleContext;
        LOGGER.log(Level.INFO, "Cryostat Plugin for JDK Mission Control is live!");
        server = new CryostatWebSocketServer(8029);
    }

    public void stop(BundleContext bundleContext) throws Exception {
        CryostatPlugin.context = null;
        server.shutdown();
    }

    @Override
    public void earlyStartup() {
        // do nothing, we're only implementing IStartup to force a load of the plugin
    }
}
