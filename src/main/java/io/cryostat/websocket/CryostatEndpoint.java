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
package io.cryostat.websocket;

import io.cryostat.CryostatPathEditorInput;
import io.cryostat.CryostatPlugin;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jetty.ee9.websocket.api.Session;
import org.eclipse.jetty.ee9.websocket.api.WebSocketAdapter;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.WorkbenchJob;

public class CryostatEndpoint extends WebSocketAdapter {

    private String recordingName;

    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);
        CryostatPlugin.getLogger().log(Level.INFO, "WebSocket Connect: " + session);
    }

    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        recordingName = null;
        CryostatPlugin.getLogger()
                .log(Level.INFO, "WebSocket Close: " + statusCode + " - " + reason);
    }

    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        recordingName = message;
        CryostatPlugin.getLogger().log(Level.INFO, "WebSocket message received: " + message);
    }

    public void onWebSocketError(Throwable cause) {
        CryostatPlugin.getLogger().log(Level.INFO, "WebSocket Error: " + cause);
    }

    public void onWebSocketBinary(byte[] payload, int offset, int len) {
        super.onWebSocketBinary(payload, offset, len);
        CryostatPlugin.getLogger().log(Level.INFO, "WebSocket Binary received with length: " + len);
        ByteArrayInputStream in = new ByteArrayInputStream(payload, offset, len);
        String filename;
        if (recordingName != null) {
            filename = recordingName;
        } else {
            filename = "cryostat-sent-me-here";
        }
        try {
            final File tempFile = File.createTempFile(filename, ".jfr");
            tempFile.deleteOnExit();
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                IOUtils.copy(in, out);
            }
            Job job =
                    new WorkbenchJob("openJfrFile") {
                        public IStatus runInUIThread(IProgressMonitor monitor) {
                            openEditor((IPathEditorInput) new CryostatPathEditorInput(tempFile));
                            return Status.OK_STATUS;
                        }
                    };
            job.schedule();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openEditor(IPathEditorInput ei) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IEditorDescriptor editorDescriptor =
                PlatformUI.getWorkbench()
                        .getEditorRegistry()
                        .getDefaultEditor(ei.getPath().lastSegment());
        if (window == null) {
            CryostatPlugin.getLogger().log(Level.INFO, "Workbench Window not available");
        } else if (editorDescriptor == null) {
            CryostatPlugin.getLogger().log(Level.INFO, "Editor not found for " + ei.getName());
        } else {
            try {
                window.getActivePage().openEditor(ei, editorDescriptor.getId());
            } catch (PartInitException e) {
                CryostatPlugin.getLogger()
                        .log(Level.INFO, "Failed to open " + editorDescriptor.getId(), e);
            }
        }
    }
}
