/*
 * Copyright 2024 - Charles Dabadie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.jleblanc64.libcustom.custom.hibernate;

import io.github.jleblanc64.libcustom.LibCustom;
import io.github.jleblanc64.libcustom.custom.utils.FieldCustomType;
import io.github.jleblanc64.libcustom.custom.utils.TypeImpl;
import io.github.jleblanc64.libcustom.meta.MetaList;
import io.github.jleblanc64.libcustom.meta.MetaListVavr;
import io.github.jleblanc64.libcustom.meta.MetaOption;
import io.github.jleblanc64.libcustom.meta.MetaOptionVavr;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static io.github.jleblanc64.libcustom.FieldMocked.getRefl;

public class VavrHibernate5 {
    static MetaOption metaOption = new MetaOptionVavr();
    static MetaList metaList = new MetaListVavr();

    public static void override() {
        override(metaOption, metaList);
    }

    @SneakyThrows
    public static void override(MetaOption metaOption, MetaList metaList) {
        var bagTypeClass = Class.forName("org.hibernate.type.BagType");
        var setterFieldImplClass = Class.forName("org.hibernate.property.access.spi.SetterFieldImpl");
        var getterFieldImplClass = Class.forName("org.hibernate.property.access.spi.GetterFieldImpl");

        LibCustom.modifyReturn(Class.forName("org.hibernate.metamodel.internal.AttributeFactory$BaseAttributeMetadata"), "getJavaType", x -> {
            var clazz = x.returned;
            if (metaList.isSuperClassOf(clazz))
                return List.class;

            return LibCustom.ORIGINAL;
        });

        LibCustom.modifyArgWithSelf(setterFieldImplClass, "set", 1, argsSelf -> {
            var args = argsSelf.args;
            var value = args[1];
            var self = argsSelf.self;
            var field = (Field) getRefl(self, setterFieldImplClass.getDeclaredField("field"));

            if (metaList.isSuperClassOf(field.getType()))
                return metaList.fromJava((List) value);

            if (metaOption.isSuperClassOf(field.getType()) && !metaOption.isSuperClassOf(value))
                return metaOption.fromValue(value);

            return LibCustom.ORIGINAL;
        });

        LibCustom.modifyReturn(getterFieldImplClass, "get", x -> {
            var ret = x.returned;
            if (metaOption.isSuperClassOf(ret))
                return metaOption.getOrNull(ret);

            return ret;
        });

        LibCustom.modifyArg(Class.forName("org.hibernate.annotations.common.reflection.java.JavaXProperty"), "create", 0, args -> {
            var member = args[0];
            if (member instanceof Field) {
                var field = (Field) member;
                if (!(field.getGenericType() instanceof ParameterizedType))
                    return LibCustom.ORIGINAL;

                var type = (ParameterizedType) field.getGenericType();
                var typeRaw = type.getRawType();
                var typeParam = type.getActualTypeArguments()[0];
                var ownerType = ((ParameterizedType) field.getGenericType()).getOwnerType();
                if (metaList.isSuperClassOf(typeRaw))
                    return FieldCustomType.create(field, new TypeImpl(List.class, new Type[]{typeParam}, ownerType));

                if (metaOption.isSuperClassOf(typeRaw))
                    return FieldCustomType.create(field, new TypeImpl((Class<?>) typeParam, new Type[]{}, ownerType));
            }

            return LibCustom.ORIGINAL;
        });

        LibCustom.modifyArg(Class.forName("org.hibernate.type.CollectionType"), "getElementsIterator", 0, args -> {
            var collection = args[0];
            if (metaList.isSuperClassOf(collection))
                return metaList.toJava(collection);

            return collection;
        });

        LibCustom.modifyArg(bagTypeClass, "wrap", 1, args -> {
            var collection = args[1];
            if (metaList.isSuperClassOf(collection))
                return metaList.toJava(collection);

            return collection;
        });
    }
}
