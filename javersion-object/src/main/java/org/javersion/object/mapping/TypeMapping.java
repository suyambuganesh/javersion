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
package org.javersion.object.mapping;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.javersion.object.DescribeContext;
import org.javersion.object.TypeContext;
import org.javersion.object.types.ValueType;
import org.javersion.path.PropertyPath;
import org.javersion.reflect.TypeDescriptor;

@ThreadSafe
public interface TypeMapping {

    default boolean applies(@Nullable PropertyPath path, TypeContext typeContext) {
        return false;
    }

    @Nonnull
    default ValueType describe(@Nullable PropertyPath path, TypeDescriptor type, DescribeContext context) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    default Optional<ValueType> describe(@Nullable PropertyPath path, TypeContext typeContext, DescribeContext context) {
        if (applies(path, typeContext)) {
            return Optional.of(describe(path, typeContext.type, context));
        }
        return Optional.empty();
    }

}
