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
package org.codehaus.griffon.runtime.kryonet;

import griffon.core.addon.GriffonAddon;
import griffon.core.injection.Module;
import griffon.plugins.kryonet.KryonetClientFactory;
import griffon.plugins.kryonet.KryonetClientStorage;
import griffon.plugins.kryonet.KryonetHandler;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.inject.Named;

/**
 * @author Andres Almiray
 */
@Named("kryonet")
@ServiceProviderFor(Module.class)
public class KryonetModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        // tag::bindings[]
        bind(KryonetClientStorage.class)
            .to(DefaultKryonetClientStorage.class)
            .asSingleton();

        bind(KryonetClientFactory.class)
            .to(DefaultKryonetClientFactory.class)
            .asSingleton();

        bind(KryonetHandler.class)
            .to(DefaultKryonetHandler.class)
            .asSingleton();

        bind(GriffonAddon.class)
            .to(KryonetAddon.class)
            .asSingleton();
        // end::bindings[]
    }
}
