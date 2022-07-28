/*-
 * #%L
 * OpenAPI Generator for Spring Boot :: WebMVC
 * %%
 * Copyright (C) 2020 QAware GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package de.qaware.openapigeneratorforspring.webmvc.reflection;

import de.qaware.openapigeneratorforspring.common.paths.HandlerMethodWithInfo;
import de.qaware.openapigeneratorforspring.common.paths.HandlerMethodsProvider;
import de.qaware.openapigeneratorforspring.model.path.RequestMethod;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;

@RequiredArgsConstructor
public class ReflectionHandlerMethodsProviderForWebMvc implements HandlerMethodsProvider {

    private final Collection<Class<?>> classes;

    @Override
    public List<HandlerMethodWithInfo> getHandlerMethods() {
        List<HandlerMethodWithInfo> result = new ArrayList<>();
        for (Class<?> controller : classes) {
            String[] baseRequestMapping = getBaseRequestMapping(controller);
            Method[] methods = controller.getMethods();
            for (Method method : methods) {
                HandlerMethodWithInfo methodInfo = getMethodInfo(method, baseRequestMapping);
                if (methodInfo != null) {
                    result.add(methodInfo);
                }
            }
        }
        return result;
    }

    private static HandlerMethodWithInfo getMethodInfo(Method method, String[] baseRequestMapping) {
        Set<String> requestMapping = new HashSet<>();
        Set<RequestMethod> requestMethods = new HashSet<>();
        parseRequestMapping(method, baseRequestMapping, requestMapping, requestMethods);
        parseGetMapping(method, baseRequestMapping, requestMapping, requestMethods);
        parsePutMapping(method, baseRequestMapping, requestMapping, requestMethods);
        parsePostMapping(method, baseRequestMapping, requestMapping, requestMethods);
        parseDeleteMapping(method, baseRequestMapping, requestMapping, requestMethods);
        if (requestMethods.isEmpty()) {
            return null;
        }
        return new HandlerMethodWithInfo(new ReflectionHandlerMethod(method), requestMapping, requestMethods);
    }

    private static void parseRequestMapping(Method method, String[] baseRequestMapping, Set<String> requestMapping, Set<RequestMethod> requestMethods) {
        RequestMapping requestMappingAnnotation = method.getAnnotation(RequestMapping.class);
        if (requestMappingAnnotation == null) {
            return;
        }
        String[] value = requestMappingAnnotation.value();
        String[] path = requestMappingAnnotation.path();
        String[] valueAndPath = ArrayUtils.addAll(value, path);
        requestMapping.addAll(combineRequestMapping(baseRequestMapping, valueAndPath));
        Arrays.stream(requestMappingAnnotation.method()).map(m -> RequestMethod.valueOf(m.name())).forEach(requestMethods::add);
    }

    private static void parseGetMapping(Method method, String[] baseRequestMapping, Set<String> requestMapping, Set<RequestMethod> requestMethods) {
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping == null) {
            return;
        }
        String[] value = getMapping.value();
        String[] path = getMapping.path();
        String[] valueAndPath = ArrayUtils.addAll(value, path);
        requestMapping.addAll(combineRequestMapping(baseRequestMapping, valueAndPath));
        requestMethods.add(RequestMethod.GET);
    }

    private static void parsePostMapping(Method method, String[] baseRequestMapping, Set<String> requestMapping, Set<RequestMethod> requestMethods) {
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping == null) {
            return;
        }
        String[] value = postMapping.value();
        String[] path = postMapping.path();
        String[] valueAndPath = ArrayUtils.addAll(value, path);
        requestMapping.addAll(combineRequestMapping(baseRequestMapping, valueAndPath));
        requestMethods.add(RequestMethod.POST);
    }


    private static void parsePutMapping(Method method, String[] baseRequestMapping, Set<String> requestMapping, Set<RequestMethod> requestMethods) {
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if (putMapping == null) {
            return;
        }
        String[] value = putMapping.value();
        String[] path = putMapping.path();
        String[] valueAndPath = ArrayUtils.addAll(value, path);
        requestMapping.addAll(combineRequestMapping(baseRequestMapping, valueAndPath));
        requestMethods.add(RequestMethod.PUT);
    }

    private static void parseDeleteMapping(Method method, String[] baseRequestMapping, Set<String> requestMapping, Set<RequestMethod> requestMethods) {
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if (deleteMapping == null) {
            return;
        }
        String[] value = deleteMapping.value();
        String[] path = deleteMapping.path();
        String[] valueAndPath = ArrayUtils.addAll(value, path);
        requestMapping.addAll(combineRequestMapping(baseRequestMapping, valueAndPath));
        requestMethods.add(RequestMethod.DELETE);
    }

    private static Set<String> combineRequestMapping(String[] baseRequestMapping, String[] requestMapping) {
        if (baseRequestMapping.length == 0) {
            return new HashSet<>(Arrays.asList(requestMapping));
        } else if (requestMapping.length == 0) {
            return new HashSet<>(Arrays.asList(baseRequestMapping));
        } else {
            Set<String> mappings = new HashSet<>();
            for (String base : baseRequestMapping) {
                for (String mapping : requestMapping) {
                    StringBuilder sb = new StringBuilder(base);
                    if (!base.endsWith("/") && !mapping.startsWith("/")) {
                        sb.append("/");
                    }
                    sb.append(mapping);
                    mappings.add(sb.toString());
                }
            }
            return mappings;
        }
    }

    private static String[] getBaseRequestMapping(Class<?> clazz) {
        RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
        if (requestMapping == null) {
            return new String[0];
        }
        String[] value = requestMapping.value();
        String[] path = requestMapping.path();
        return ArrayUtils.addAll(value, path);
    }

}
