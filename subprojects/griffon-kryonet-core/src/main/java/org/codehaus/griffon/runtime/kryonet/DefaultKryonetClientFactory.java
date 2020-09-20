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

import com.esotericsoftware.kryonet.Client;
import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.injection.Injector;
import griffon.exceptions.InstanceNotFoundException;
import griffon.plugins.kryonet.KryoConfigurer;
import griffon.plugins.kryonet.KryonetClientFactory;
import griffon.plugins.kryonet.KryonetClientListener;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static griffon.util.ConfigUtils.getConfigValue;
import static griffon.util.ConfigUtils.getConfigValueAsInt;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultKryonetClientFactory implements KryonetClientFactory {
    @Inject
    private Injector injector;

    @Nonnull
    public Client create(@Nonnull Map<String, Object> params, @Nullable String id) {
        requireNonNull(params, "Argument 'params' must not be null");
        String host = getConfigValue(params, "host", "localhost");
        int port = getConfigValueAsInt(params, "port", 54555);
        int timeout = getConfigValueAsInt(params, "timeout", 5000);

        Client client = createClient();

        try {
            client.connect(timeout, host, port);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return client;
    }

    @Override
    public void destroy(@Nonnull Client client) {
        requireNonNull(client, "Argument 'client' must not be null");

        try {
            client.stop();
            client.dispose();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Client createClient() {
        Client client = new Client();

        Collection<KryonetClientListener> listeners = getKryonetClientListeners();
        System.out.println("listeners = " + listeners);
        listeners.forEach(l -> l.setClient(client));

        client.start();

        getKryoConfigurers().forEach(c -> c.configure(client.getKryo()));
        listeners.forEach(client::addListener);

        return client;
    }

    @Nonnull
    private Collection<KryonetClientListener> getKryonetClientListeners() {
        try {
            return injector.getInstances(KryonetClientListener.class);
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Nonnull
    private Collection<KryoConfigurer> getKryoConfigurers() {
        try {
            return injector.getInstances(KryoConfigurer.class);
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
