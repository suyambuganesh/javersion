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

import java.sql.Types;

import org.javersion.core.Revision;

import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.RelationalPathBase;

public class JVersionParent extends RelationalPathBase<JVersionParent> {

    public final SimplePath<Revision> parentRevision = createSimple("parentRevision", org.javersion.core.Revision.class);

    public final SimplePath<org.javersion.core.Revision> revision = createSimple("revision", org.javersion.core.Revision.class);

    public final EnumPath<VersionStatus> status = createEnum("status", VersionStatus.class);

    public JVersionParent(RelationalPathBase<?> table) {
        super(JVersionParent.class, table.getMetadata(), table.getSchemaName(), table.getTableName());
        table.getColumns().forEach(path -> addMetadata(path, table.getMetadata(path)));
    }

    public JVersionParent(String repositoryName) {
        this("PUBLIC", repositoryName + "_VERSION_PARENT");
    }

    public JVersionParent(String schema, String table) {
        super(JVersionParent.class, PathMetadataFactory.forVariable(table), schema, table);
        addMetadata(revision, ColumnMetadata.named("REVISION").withIndex(1).ofType(Types.VARCHAR).withSize(32).notNull());
        addMetadata(parentRevision, ColumnMetadata.named("PARENT_REVISION").withIndex(2).ofType(Types.VARCHAR).withSize(32).notNull());
        addMetadata(status, ColumnMetadata.named("STATUS").withIndex(3).ofType(Types.INTEGER).withSize(1).notNull());
    }

}

