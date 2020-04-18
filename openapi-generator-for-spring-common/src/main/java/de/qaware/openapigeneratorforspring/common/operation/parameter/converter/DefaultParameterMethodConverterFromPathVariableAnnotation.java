package de.qaware.openapigeneratorforspring.common.operation.parameter.converter;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springframework.web.bind.annotation.PathVariable;

public class DefaultParameterMethodConverterFromPathVariableAnnotation extends ParameterMethodConverterFromAnnotation<PathVariable> {

    public static final int ORDER = DEFAULT_ORDER;

    private final DefaultParameterBuilderFromSpringWebAnnotation parameterBuilder;

    public DefaultParameterMethodConverterFromPathVariableAnnotation(DefaultParameterBuilderFromSpringWebAnnotation parameterBuilder) {
        super(PathVariable.class);
        this.parameterBuilder = parameterBuilder;
    }

    @Override
    protected Parameter buildParameter(PathVariable annotation) {
        return parameterBuilder.build(ParameterIn.PATH,
                annotation.name(), annotation.required()
        );
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
