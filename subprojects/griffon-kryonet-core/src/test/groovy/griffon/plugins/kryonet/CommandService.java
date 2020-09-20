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
package griffon.plugins.kryonet;

import com.esotericsoftware.kryonet.Client;
import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.artifact.GriffonService;
import griffon.plugins.kryonet.exceptions.KryonetException;
import griffon.util.CollectionUtils;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonService;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.inject.Inject;
import java.util.Map;

@ServiceProviderFor(GriffonService.class)
public class CommandService extends AbstractGriffonService {
    @Inject
    private KryonetHandler kryonetHandler;

    public void send(final @Nonnull String message) {
        Map<String, Object> params = CollectionUtils.<String, Object>map()
            .e("host", "localhost")
            .e("timeout", 1000)
            .e("id", "client");
        kryonetHandler.withKryonet(params,
            new KryonetClientCallback() {
                @Override
                @Nullable
                public void handle(Map<String, Object> params, Client client) throws KryonetException {
                    client.sendTCP(new Command().setMessage(message));
                }
            });
    }
}