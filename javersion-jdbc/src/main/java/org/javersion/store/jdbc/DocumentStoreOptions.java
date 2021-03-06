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

import org.javersion.util.Check;

import com.querydsl.core.types.Expression;

public class DocumentStoreOptions<Id, M, V extends JDocumentVersion<Id>> extends StoreOptions<Id, M, V> {

    public final Expression<Long> nextOrdinal;

    protected DocumentStoreOptions(Builder<Id, M, V> builder) {
        super(builder);
        this.nextOrdinal = Check.notNull(builder.nextOrdinal, "nextOrdinal");
    }

    @Override
    public Builder<Id, M, V> toBuilder() {
        return new Builder<>(this);
    }

    public static class Builder<Id, M, V extends JDocumentVersion<Id>> extends AbstractBuilder<Id, M, V, DocumentStoreOptions<Id, M, V>, Builder<Id, M, V>> {

        protected Expression<Long> nextOrdinal;

        public Builder() {}

        public Builder(DocumentStoreOptions<Id, M, V> options) {
            super(options);
            this.nextOrdinal = options.nextOrdinal;
        }

        public Builder<Id, M, V> nextOrdinal(Expression<Long> nextOrdinal) {
            this.nextOrdinal = nextOrdinal;
            return this;
        }

        @Override
        public DocumentStoreOptions<Id, M, V> build() {
            return new DocumentStoreOptions<>(this);
        }

    }

}
