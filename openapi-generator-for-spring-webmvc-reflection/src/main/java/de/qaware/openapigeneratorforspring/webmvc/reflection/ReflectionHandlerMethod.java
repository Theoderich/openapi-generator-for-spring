/*-
 * #%L
 * OpenAPI Generator for Spring Boot :: WebMVC via Reflection
 * %%
 * Copyright (C) 2020 - 2022 QAware GmbH
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

import de.qaware.openapigeneratorforspring.common.annotation.AnnotationsSupplier;
import de.qaware.openapigeneratorforspring.common.paths.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

public class ReflectionHandlerMethod implements HandlerMethod {

    private final String identifier;
    private final List<Parameter> parameters;
    private final AnnotationsSupplier annotationsSupplier;


    public ReflectionHandlerMethod(Method method) {
        this.identifier = method.getName();
        List<Parameter> tmpParameters = new ArrayList<>();
        for (java.lang.reflect.Parameter parameter : method.getParameters()) {
            tmpParameters.add(new ReflectionParameter(parameter));
        }
        this.parameters = Collections.unmodifiableList(tmpParameters);
        this.annotationsSupplier = new AnnotationsSupplier() {
            @Override
            public <A extends Annotation> Stream<A> findAnnotations(Class<A> annotationType) {
                return Arrays.stream((A[]) method.getAnnotations()).filter(a -> a.annotationType().equals(annotationType));
            }
        };
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public List<Parameter> getParameters() {
        return this.parameters;
    }

    @Override
    public <A extends Annotation> ContextAwareAnnotations<A> findAnnotationsWithContext(Class<A> annotationType) {
        return () -> annotationsSupplier.findAnnotations(annotationType);
    }

    private static class ReflectionParameter implements Parameter {
        private final Optional<String> name;
        private final Optional<Type> type;

        private final AnnotationsSupplier annotationsSupplier;

        public ReflectionParameter(java.lang.reflect.Parameter parameter) {
            if (parameter.isNamePresent()) {
                this.name = Optional.of(parameter.getName());
            } else {
                this.name = Optional.empty();
            }
            this.type = Optional.of(new ReflectionType(parameter.getAnnotatedType()));
            this.annotationsSupplier = new AnnotationsSupplier() {
                @Override
                public <A extends Annotation> Stream<A> findAnnotations(Class<A> annotationType) {
                    return Arrays.stream((A[]) parameter.getAnnotations()).filter(a -> a.annotationType().equals(annotationType));
                }
            };
        }

        @Override
        public AnnotationsSupplier getAnnotationsSupplier() {
            return this.annotationsSupplier;
        }

        @Override
        public Optional<String> getName() {
            return this.name;
        }

        @Override
        public Optional<Type> getType() {
            return this.type;
        }
    }

    private static class ReflectionType implements Type {

        private final java.lang.reflect.Type type;
        private final AnnotationsSupplier annotationsSupplier;

        public ReflectionType(AnnotatedType type) {
            this.type = type.getType();
            this.annotationsSupplier = new AnnotationsSupplier() {
                @Override
                public <A extends Annotation> Stream<A> findAnnotations(Class<A> annotationType) {
                    return Arrays.stream((A[]) type.getAnnotations()).filter(a -> a.annotationType().equals(annotationType));
                }
            };
        }

        @Override
        public AnnotationsSupplier getAnnotationsSupplier() {
            return this.annotationsSupplier;
        }

        @Override
        public java.lang.reflect.Type getType() {
            return this.type;
        }
    }
}
