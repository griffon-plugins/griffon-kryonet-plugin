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
import griffon.plugins.kryonet.KryonetClientCallback;
import griffon.plugins.kryonet.KryonetClientFactory;
import griffon.plugins.kryonet.KryonetClientStorage;
import griffon.plugins.kryonet.KryonetHandler;
import griffon.plugins.kryonet.exceptions.KryonetException;

import javax.inject.Inject;
import java.util.Map;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultKryonetHandler implements KryonetHandler {
    private static final String ERROR_PARAMS_NULL = "Argument 'params' must not be null";
    private static final String ERROR_CALLBACK_NULL = "Argument 'callback' must not be null";
    private static final String KEY_ID = "id";

    private final KryonetClientFactory kryonetClientFactory;
    private final KryonetClientStorage kryonetClientStorage;

    @Inject
    public DefaultKryonetHandler(@Nonnull KryonetClientFactory kryonetClientFactory, @Nonnull KryonetClientStorage kryonetClientStorage) {
        this.kryonetClientFactory = kryonetClientFactory;
        this.kryonetClientStorage = kryonetClientStorage;
    }

    @Override
    public void withKryonet(@Nonnull Map<String, Object> params, @Nonnull KryonetClientCallback callback) throws KryonetException {
        requireNonNull(callback, ERROR_CALLBACK_NULL);
        try {
            Client client = getKryonetClient(params);
            callback.handle(params, client);
        } catch (Exception e) {
            throw new KryonetException("An error occurred while executing Kryonet call", e);
        }
    }

    @Nonnull
    private Client getKryonetClient(@Nonnull Map<String, Object> params) {
        requireNonNull(params, ERROR_PARAMS_NULL);
        if (params.containsKey(KEY_ID)) {
            String id = String.valueOf(params.remove(KEY_ID));
            Client client = kryonetClientStorage.get(id);
            if (client == null) {
                client = kryonetClientFactory.create(params, id);
                kryonetClientStorage.set(id, client);
            }
            return client;
        }
        return kryonetClientFactory.create(params, null);
    }

    @Override
    public void destroyKryonetClient(@Nonnull String clientId) {
        requireNonBlank(clientId, "Argument 'clientId' must not be blank");
        Client kryonetClient = kryonetClientStorage.get(clientId);
        try {
            if (kryonetClient != null) {
                kryonetClientFactory.destroy(kryonetClient);
            }
        } finally {
            kryonetClientStorage.remove(clientId);
        }
    }
}
