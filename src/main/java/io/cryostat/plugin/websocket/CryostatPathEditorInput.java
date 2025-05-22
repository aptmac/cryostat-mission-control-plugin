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

import java.io.File;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;

public class CryostatPathEditorInput implements IPathEditorInput {
    private final File file;

    public CryostatPathEditorInput(File file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof IEditorInput)) {
            return false;
        }
        return file.equals(object);
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public <T> T getAdapter(Class<T> adapter) {
        return null;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public IPath getPath() {
        return Path.fromOSString(file.getAbsolutePath());
    }

    @Override
    public IPersistableElement getPersistable() {
        return null;
    }

    @Override
    public String getToolTipText() {
        return file.getPath();
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }
}
