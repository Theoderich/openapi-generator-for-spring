package de.qaware.openapigeneratorforspring.common.schema.resolver;

import de.qaware.openapigeneratorforspring.common.annotation.AnnotationsSupplier;
import de.qaware.openapigeneratorforspring.common.schema.Schema;
import de.qaware.openapigeneratorforspring.common.schema.reference.ReferencedSchemaConsumer;

import java.lang.reflect.Type;
import java.util.function.Consumer;

public interface SchemaResolver {
    void resolveFromType(Type type, AnnotationsSupplier annotationsSupplier, ReferencedSchemaConsumer referencedSchemaConsumer, Consumer<Schema> schemaSetter);

    void resolveFromClass(Class<?> clazz, ReferencedSchemaConsumer referencedSchemaConsumer, Consumer<Schema> schemaSetter);

    Schema resolveFromClassWithoutReference(Class<?> clazz, ReferencedSchemaConsumer referencedSchemaConsumer);
}
