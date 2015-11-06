/*
 * Copyright 2015 Samppa Saarela
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
package org.javersion.store.jdbc;

import java.util.Map;

import org.javersion.core.Persistent;
import org.javersion.core.Revision;
import org.javersion.core.VersionNode;
import org.javersion.path.PropertyPath;

import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.types.Path;

public abstract class AbstractUpdateBatch<Id, M, V extends JVersion<Id>, Options extends StoreOptions<Id, V>> {

    protected final Options options;

    protected final SQLInsertClause versionBatch;

    protected final SQLInsertClause parentBatch;

    protected final SQLInsertClause propertyBatch;

    public AbstractUpdateBatch(Options options) {
        this.options = options;
        versionBatch = options.queryFactory.insert(options.version);
        parentBatch = options.queryFactory.insert(options.parent);
        propertyBatch = options.queryFactory.insert(options.property);
    }

    public void addVersion(Id docId, VersionNode<PropertyPath, Object, M> version) {
        insertVersion(docId, version);
        insertParents(docId, version);
        insertProperties(docId, version);
    }

    public void execute() {
        if (!versionBatch.isEmpty()) {
            versionBatch.execute();
        }
        if (!parentBatch.isEmpty()) {
            parentBatch.execute();
        }
        if (!propertyBatch.isEmpty()) {
            propertyBatch.execute();
        }
    }

    protected void insertVersion(Id docId, VersionNode<PropertyPath, Object, M> version) {
        versionBatch
                .set(options.version.docId, docId)
                .set(options.version.revision, version.revision)
                .set(options.version.type, version.type)
                .set(options.version.branch, version.branch);

        if (!options.versionTableProperties.isEmpty()) {
            Map<PropertyPath, Object> properties = version.getProperties();
            for (Map.Entry<PropertyPath, Path<?>> entry : options.versionTableProperties.entrySet()) {
                PropertyPath path = entry.getKey();
                @SuppressWarnings("unchecked")
                Path<Object> column = (Path<Object>) entry.getValue();
                versionBatch.set(column, properties.get(path));
            }
        }
        versionBatch.addBatch();
    }

    protected void insertParents(Id docId, VersionNode<PropertyPath, Object, M> version) {
        for (Revision parentRevision : version.parentRevisions) {
            parentBatch
                    .set(options.parent.revision, version.revision)
                    .set(options.parent.parentRevision, parentRevision)
                    .addBatch();
        }
    }

    protected void insertProperties(Id docId, VersionNode<PropertyPath, Object, M> version) {
        insertProperties(docId, version.revision, version.getChangeset());
    }

    protected void insertProperties(Id docId, Revision revision, Map<PropertyPath, Object> changeset) {
        for (Map.Entry<PropertyPath, Object> entry : changeset.entrySet()) {
            if (!options.versionTableProperties.containsKey(entry.getKey())) {
                propertyBatch
                        .set(options.property.revision, revision)
                        .set(options.property.path, entry.getKey().toString());
                setValue(entry.getKey(), entry.getValue());
                propertyBatch.addBatch();
            }
        }
    }

    protected void setValue(PropertyPath path, Object value) {
        // type:
        // n=null, O=object, A=array, s=string,
        // b=boolean, l=long, d=double, D=bigdecimal
        char type;
        String str = null;
        Long nbr = null;
        switch (Persistent.Type.of(value)) {
            case NULL:
                type = 'n';
                break;
            case OBJECT:
                type = 'O';
                str = ((Persistent.Object) value).type;
                break;
            case ARRAY:
                type = 'A';
                break;
            case STRING:
                type = 's';
                str = (String) value;
                break;
            case BOOLEAN:
                type = 'b';
                nbr = ((Boolean) value) ? 1l : 0l;
                break;
            case LONG:
                type = 'l';
                nbr = (Long) value;
                break;
            case DOUBLE:
                type = 'd';
                nbr = Double.doubleToRawLongBits((Double) value);
                break;
            case BIG_DECIMAL:
                type = 'D';
                str = value.toString();
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + value.getClass());
        }
        propertyBatch
                .set(options.property.type, Character.toString(type))
                .set(options.property.str, str)
                .set(options.property.nbr, nbr);
    }
}