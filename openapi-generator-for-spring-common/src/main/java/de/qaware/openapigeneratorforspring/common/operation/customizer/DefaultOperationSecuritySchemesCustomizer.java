package de.qaware.openapigeneratorforspring.common.operation.customizer;

import de.qaware.openapigeneratorforspring.common.annotation.AnnotationsSupplier;
import de.qaware.openapigeneratorforspring.common.mapper.SecuritySchemeAnnotationMapper;
import de.qaware.openapigeneratorforspring.common.operation.OperationBuilderContext;
import de.qaware.openapigeneratorforspring.common.reference.component.securityscheme.ReferencedSecuritySchemesConsumer;
import de.qaware.openapigeneratorforspring.model.operation.Operation;
import de.qaware.openapigeneratorforspring.model.security.SecurityScheme;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import java.util.Map;

import static de.qaware.openapigeneratorforspring.common.util.OpenApiMapUtils.buildStringMapFromStream;

@RequiredArgsConstructor
public class DefaultOperationSecuritySchemesCustomizer implements OperationCustomizer {

    public static final int ORDER = DEFAULT_ORDER;

    private final SecuritySchemeAnnotationMapper securitySchemeAnnotationMapper;

    @Override
    public void customize(Operation operation, @Nullable io.swagger.v3.oas.annotations.Operation operationAnnotation, OperationBuilderContext operationBuilderContext) {
        AnnotationsSupplier annotationsSupplier = operationBuilderContext.getOperationInfo().getHandlerMethod().getAnnotationsSupplier();
        Map<String, SecurityScheme> securitySchemes = buildStringMapFromStream(
                annotationsSupplier.findAnnotations(io.swagger.v3.oas.annotations.security.SecurityScheme.class),
                io.swagger.v3.oas.annotations.security.SecurityScheme::name,
                securitySchemeAnnotationMapper::map
        );
        operationBuilderContext.getReferencedItemConsumer(ReferencedSecuritySchemesConsumer.class)
                .accept(securitySchemes);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
