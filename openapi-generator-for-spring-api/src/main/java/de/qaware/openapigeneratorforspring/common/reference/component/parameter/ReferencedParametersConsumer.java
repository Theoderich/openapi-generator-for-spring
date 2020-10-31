package de.qaware.openapigeneratorforspring.common.reference.component.parameter;

import de.qaware.openapigeneratorforspring.common.reference.fortype.ReferencedItemConsumerForType;
import de.qaware.openapigeneratorforspring.model.parameter.Parameter;

import java.util.List;

public interface ReferencedParametersConsumer extends ReferencedItemConsumerForType<List<Parameter>> {
    ReferencedParametersConsumer withOwner(Object owner);
}
