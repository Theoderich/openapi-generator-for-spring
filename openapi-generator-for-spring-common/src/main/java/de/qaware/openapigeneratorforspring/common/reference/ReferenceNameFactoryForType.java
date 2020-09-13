package de.qaware.openapigeneratorforspring.common.reference;

import javax.annotation.Nullable;
import java.util.List;

public interface ReferenceNameFactoryForType<T> {
    ReferenceName.Type getReferenceNameType();

    List<Object> buildIdentifierComponents(T item, @Nullable String suggestedIdentifier);
}
