package de.qaware.openapigeneratorforspring.webmvc.reflection;

import de.qaware.openapigeneratorforspring.common.paths.HandlerMethod;
import de.qaware.openapigeneratorforspring.common.paths.HandlerMethodWithInfo;
import de.qaware.openapigeneratorforspring.model.path.RequestMethod;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ReflectionHandlerMethodsProviderForWebMvcTest {
    @Test
    void testSingleParseableClass() {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(DemoController.class);
        ReflectionHandlerMethodsProviderForWebMvc sut = new ReflectionHandlerMethodsProviderForWebMvc(classes);
        List<HandlerMethodWithInfo> handlerMethods = sut.getHandlerMethods();
        assertThat(handlerMethods).hasSize(1);
        HandlerMethodWithInfo handlerMethodWithInfo = handlerMethods.get(0);
        assertThat(handlerMethodWithInfo.getRequestMethods()).containsExactlyInAnyOrder(RequestMethod.GET);
        assertThat(handlerMethodWithInfo.getPathPatterns()).containsExactlyInAnyOrder("/api/hello");
        HandlerMethod handlerMethod = handlerMethodWithInfo.getHandlerMethod();
        assertThat(handlerMethod.getIdentifier()).isEqualTo("greet");
        List<GetMapping> methodAnnotations = handlerMethod.findAnnotations(GetMapping.class).collect(Collectors.toList());
        assertThat(methodAnnotations).hasSize(1);
        assertThat(methodAnnotations.get(0).value()).containsExactlyInAnyOrder("hello");
        List<HandlerMethod.Parameter> parameters = handlerMethod.getParameters();
        assertThat(parameters).hasSize(1);
        HandlerMethod.Parameter firstParameter = parameters.get(0);
        assertThat(firstParameter.getName()).contains("name");
        assertThat(firstParameter.getType()).isPresent();
        HandlerMethod.Type firstParameterType = firstParameter.getType().get();
        assertThat(firstParameterType.getType().getTypeName()).isEqualTo("java.lang.String");

    }


    @RestController
    @RequestMapping("/api")
    private static final class DemoController {

        @GetMapping("hello")
        public String greet(String name) {
            return "Hello " + name;
        }

    }
}
