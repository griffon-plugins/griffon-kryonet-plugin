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
import com.esotericsoftware.kryonet.Listener;

/**
 * @author Andres Almiray
 */
public abstract class KryonetClientListener extends Listener {
    protected Client client;

    public void setClient(Client client) {
        this.client = client;
    }
}
