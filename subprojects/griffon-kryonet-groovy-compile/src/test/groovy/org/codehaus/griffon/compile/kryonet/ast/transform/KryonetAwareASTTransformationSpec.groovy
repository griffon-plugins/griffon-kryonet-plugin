/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020 The author and/or original authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing pekryonetssions and
 * limitations under the License.
 */
package org.codehaus.griffon.compile.kryonet.ast.transform

import griffon.plugins.kryonet.KryonetHandler
import spock.lang.Specification

import java.lang.reflect.Method

/**
 * @author Andres Almiray
 */
class KryonetAwareASTTransformationSpec extends Specification {
    def 'KryonetAwareASTTransformation is applied to a bean via @KryonetAware'() {
        given:
        GroovyShell shell = new GroovyShell()

        when:
        def bean = shell.evaluate('''
        @griffon.transform.kryonet.KryonetAware
        class Bean { }
        new Bean()
        ''')

        then:
        bean instanceof KryonetHandler
        KryonetHandler.methods.every { Method target ->
            bean.class.declaredMethods.find { Method candidate ->
                candidate.name == target.name &&
                    candidate.returnType == target.returnType &&
                    candidate.parameterTypes == target.parameterTypes &&
                    candidate.exceptionTypes == target.exceptionTypes
            }
        }
    }

    def 'KryonetAwareASTTransformation is not applied to a KryonetHandler subclass via @KryonetAware'() {
        given:
        GroovyShell shell = new GroovyShell()

        when:
        def bean = shell.evaluate('''
        import griffon.plugins.kryonet.*
        import griffon.plugins.kryonet.exceptions.*

        import griffon.annotations.core.Nonnull
        @griffon.transform.kryonet.KryonetAware
        class KryonetHandlerBean implements KryonetHandler {
            @Override
            void withKryonet(@Nonnull Map<String,Object> params,@Nonnull KryonetClientCallback callback) throws KryonetException {}
            @Override
            void destroyKryonetClient(@Nonnull String clientId) {}
        }
        new KryonetHandlerBean()
        ''')

        then:
        bean instanceof KryonetHandler
        KryonetHandler.methods.every { Method target ->
            bean.class.declaredMethods.find { Method candidate ->
                candidate.name == target.name &&
                    candidate.returnType == target.returnType &&
                    candidate.parameterTypes == target.parameterTypes &&
                    candidate.exceptionTypes == target.exceptionTypes
            }
        }
    }
}
