package de.qaware.openapigeneratorforspring.common.operation.response;

import de.qaware.openapigeneratorforspring.common.operation.OperationBuilderContext;
import de.qaware.openapigeneratorforspring.common.schema.Schema;
import de.qaware.openapigeneratorforspring.common.schema.SchemaResolver;
import de.qaware.openapigeneratorforspring.common.util.OpenApiAnnotationUtils;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class MethodResponseApiResponseCustomizer implements OperationApiResponseCustomizer, Ordered {

    public static final int ORDER = Ordered.LOWEST_PRECEDENCE - 1000;

    private final DefaultApiResponseCodeMapper defaultApiResponseCodeMapper;
    private final SchemaResolver schemaResolver;

    @Override
    public void customize(ApiResponses apiResponses, OperationBuilderContext operationBuilderContext) {
        Method method = operationBuilderContext.getHandlerMethod().getMethod();
        String responseCodeFromMethod = defaultApiResponseCodeMapper.getResponseCodeFromMethod(method);
        ApiResponse defaultApiResponse = apiResponses.computeIfAbsent(responseCodeFromMethod, ignored -> new ApiResponse());

        if (StringUtils.isBlank(defaultApiResponse.getDescription())) {
            // TODO make this description customizable?
            defaultApiResponse.setDescription("Default response");
        }
        Content content = getOrCreateEmptyContent(defaultApiResponse);
        List<String> producesContentType = getProducesContentType(method);
        for (String contentType : producesContentType) {
            MediaType mediaType = content.computeIfAbsent(contentType, ignored -> new MediaType());
            Schema schema = schemaResolver.resolveFromClass(method.getReturnType(), operationBuilderContext.getReferencedSchemaConsumer());
            mediaType.setSchema(schema);
            // TODO investigate @Schema annotation on operation method?
        }

    }

    private Content getOrCreateEmptyContent(ApiResponse apiResponse) {
        if (apiResponse.getContent() != null) {
            return apiResponse.getContent();
        }
        Content content = new Content();
        apiResponse.setContent(content);
        return content;
    }

    private List<String> getProducesContentType(Method method) {
        RequestMapping requestMappingAnnotation = OpenApiAnnotationUtils.findAnnotationOnMethodOrClass(method, RequestMapping.class);
        if (requestMappingAnnotation == null || ArrayUtils.isEmpty(requestMappingAnnotation.produces())) {
            return Collections.singletonList(org.springframework.http.MediaType.ALL_VALUE);
        }
        return Arrays.asList(requestMappingAnnotation.produces());
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
