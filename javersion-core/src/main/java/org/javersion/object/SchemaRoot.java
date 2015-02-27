/*
 * Copyright 2013 Samppa Saarela
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.javersion.object;

import org.javersion.path.PropertyPath;
import org.javersion.path.PropertyPath.NodeId;
import org.javersion.util.Check;

public class SchemaRoot extends Schema  {

    SchemaRoot() {}

    public Schema get(PropertyPath path) {
        Schema schema = find(path);
        if (schema == null) {
            throw new IllegalArgumentException("Path not found: " + path);
        }
        return schema;
    }

    public Schema find(PropertyPath path) {
        Check.notNull(path, "path");
        Schema currentMapping = this;
        for (PropertyPath currentPath : path.asList()) {
            NodeId nodeId = currentPath.getNodeId();
            Schema childMapping = currentMapping.getChild(nodeId);
            currentMapping = childMapping != null ? childMapping : currentMapping.getChild(nodeId.fallbackId());
            if (currentMapping == null) {
                return null;
            }
        }
        return currentMapping;
    }

}
