/*-
 * #%L
 * OpenAPI Generator for Spring Boot :: Maven Plugin
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

package de.qaware.openapigeneratorforspring.maven;

import de.qaware.openapigeneratorforspring.common.OpenApiGenerator;
import de.qaware.openapigeneratorforspring.common.supplier.OpenApiObjectMapperSupplier;
import de.qaware.openapigeneratorforspring.model.OpenApi;
import de.qaware.openapigeneratorforspring.webmvc.reflection.ReflectionHandlerMethodsProviderForWebMvc;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE, threadSafe = true)
public class GeneratorMojo extends AbstractMojo {
    @Parameter(property = "outputFile")
    private File outputFile;

    @Parameter(property = "basePackage")
    private String basePackage;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final List<URL> urls = new ArrayList<>();
        try {
            for (String element : project.getCompileClasspathElements()) {
                urls.add(new File(element).toURI().toURL());
            }
        } catch (DependencyResolutionRequiredException | IOException e) {
            throw new MojoFailureException("Unable to get compiled classpath elements", e);
        }
        URLClassLoader cl = new URLClassLoader(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
        ClassGraph classGraph = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .ignoreClassVisibility()
                .overrideClasspath(urls)
                .ignoreParentClassLoaders()
                .acceptPackages(basePackage);
        try (ScanResult scanResult = classGraph.scan()) {

            Set<String> classNames = scanResult.getAllStandardClasses().stream().filter(c -> c.hasAnnotation(RestController.class)).map(ClassInfo::getName).collect(Collectors.toSet());
            List<Class<?>> classes = new ArrayList<>(classNames.size());
            for (String className : classNames) {
                classes.add(cl.loadClass(className));
            }
            AnnotationConfigApplicationContext springContext = new CustomApplicationContext("de.qaware.openapigeneratorforspring");
            springContext.registerBean("reflectionHandlerMethodsProviderForWebMvc", ReflectionHandlerMethodsProviderForWebMvc.class, classes);
            springContext.refresh();
            springContext.start();
            OpenApiGenerator openApiGenerator = springContext.getBean(OpenApiGenerator.class);
            OpenApiObjectMapperSupplier objectMapperSupplier = springContext.getBean(OpenApiObjectMapperSupplier.class);
            OpenApi openApi = openApiGenerator.generateOpenApi();
            objectMapperSupplier.get(OpenApiObjectMapperSupplier.Purpose.OPEN_API_JSON).writeValue(outputFile, openApi);

        } catch (IOException | ClassNotFoundException e) {
            throw new MojoFailureException("Unable to write output file", e);

        }
    }

    /**
     * Application context that does not load immediately, so we can register custom beans first.
     */
    private static class CustomApplicationContext extends AnnotationConfigApplicationContext {
        public CustomApplicationContext(String... basePackages) {
            scan(basePackages);
        }
    }
}
