/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.as.quickstarts.tasksJsf;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Verify the base JPA entities are not injectable.
 */
@RunWith(Arquillian.class)
public class JpaInjectionTest {
    @Deployment(name = "with-extension")
    public static Archive<?> createTestArchiveWithExtension() {
        return new DefaultDeployment("with-extension.war").withCdiExtension().getArchive()
                .addClasses(Task.class, User.class);
    }

    @Deployment(name = "with-extension-with-producer")
    public static Archive<?> createTestArchiveWithExtensionAndProducer() {
        return new DefaultDeployment("with-extension-with-producer.war").withCdiExtension().getArchive()
                .addClasses(Task.class, CurrentTask.class, CurrentTaskStore.class, User.class);
    }

    @Deployment(name = "without-extension")
    public static Archive<?> createTestArchiveWithoutExtension() {
        return new DefaultDeployment("without-extension.war").getArchive()
                .addClasses(Task.class, User.class);
    }

    @Deployment(name = "without-extension-with-producer")
    public static Archive<?> createTestArchiveWithoutExtensionAndProducer() {
        return new DefaultDeployment("without-extension-with-producer.war").getArchive()
                .addClasses(Task.class, CurrentTask.class, CurrentTaskStore.class, User.class);
    }

    @Inject @Any
    Instance<Task> allInjectableTasks;

    @Test
    @OperateOnDeployment("with-extension")
    public void assertInjectsAreUnsatisfied() {
        // With the extension, and no producer in the archive,
        // there is no way to properly inject an entity.
        assertThat(allInjectableTasks.isUnsatisfied(), is(true));
    }

    @Test
    @OperateOnDeployment("with-extension-with-producer")
    public void assertInjectsAreNotAmbiguousAndSatisfied() {
        assertThat(allInjectableTasks.isAmbiguous(), is(false));
        assertThat(allInjectableTasks.isUnsatisfied(), is(false));
    }

    @Test
    @OperateOnDeployment("without-extension")
    public void assertInjectsAreNotUnsatisfied() {
        // Without the extension and no producers there is only one
        // possible injection.
        assertThat(allInjectableTasks.isUnsatisfied(), is(false));
    }

    @Test
    @OperateOnDeployment("without-extension-with-producer")
    public void assertInjectionsAreAmbiguousAndSatisfied() {
        assertThat(allInjectableTasks.isUnsatisfied(), is(false));
        assertThat(allInjectableTasks.isAmbiguous(), is(true));
    }
}
