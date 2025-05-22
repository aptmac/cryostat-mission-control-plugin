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
package io.cryostat.plugin;

import io.cryostat.plugin.preferences.PreferenceConstants;
import io.cryostat.plugin.websocket.CryostatWebSocketServer;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class CryostatPlugin extends AbstractUIPlugin implements BundleActivator, IStartup {

    private static final Logger LOGGER = Logger.getLogger(CryostatPlugin.class.getName());
    private static CryostatPlugin plugin;
    private CryostatWebSocketServer server;

    public CryostatPlugin() {
    }

    public static CryostatPlugin getDefault() {
    	return plugin;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public void start(BundleContext bundleContext) throws Exception {
    	super.start(bundleContext);
    	this.getPreferenceStore().addPropertyChangeListener(preferenceChangeListener);
    	plugin = this;
        startServer(getCryostatPort());
        LOGGER.log(Level.INFO, "Cryostat Plugin for JDK Mission Control is live!");
    }

    public void stop(BundleContext bundleContext) throws Exception {
        server.shutdown();
        plugin = null;
        super.stop(bundleContext);
    }

    private void startServer(int port) {
    	if (getPluginEnabled()) {
    		server = new CryostatWebSocketServer(port);
    	}
    }

    private void stopServer() {
		try {
			server.shutdown();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error shutting down the Jetty WebSocket server", e);
		}
    }

    private IPropertyChangeListener preferenceChangeListener = new IPropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(PreferenceConstants.P_PLUGIN_ENABLED)) {
				if (getPluginEnabled()) {
					startServer(getCryostatPort());
				} else {
					stopServer();
				}
			}
			if (event.getProperty().equals(PreferenceConstants.P_PLUGIN_PORT) && getPluginEnabled()) {
				stopServer();
				startServer(getCryostatPort());
			}
		}
	};

    private int getCryostatPort() {
    	return this.getPreferenceStore().getInt(PreferenceConstants.P_PLUGIN_PORT);
    }

    private boolean getPluginEnabled() {
    	return this.getPreferenceStore().getBoolean(PreferenceConstants.P_PLUGIN_ENABLED);
    }

    @Override
    public void earlyStartup() {
        // do nothing, we're only implementing IStartup to force a load of the plugin
    }
}
