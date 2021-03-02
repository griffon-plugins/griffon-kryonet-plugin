/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2021 The author and/or original authors.
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

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import griffon.annotations.inject.BindTo;
import griffon.test.core.GriffonUnitRule;
import griffon.test.core.TestFor;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@TestFor(CommandService.class)
public class CommandServiceTest {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
    }

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule();

    private CommandService service;
    private Server server;

    @BindTo(KryoConfigurer.class)
    private TestKryoConfigurer configurer = new TestKryoConfigurer();

    @BindTo(KryonetClientListener.class)
    private CommandKryonetClientListener clientListener = new CommandKryonetClientListener();

    @Before
    public void setup() throws IOException {
        server = new Server();
        configurer.configure(server.getKryo());
        server.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                Command cmd = (Command) object;
                server.sendToAllTCP(new Command().setMessage(cmd.getMessage().toUpperCase()));
            }
        });
        server.bind(54555);
        server.start();
    }

    @After
    public void cleanup() {
        server.stop();
    }

    @Test
    public void sendMessageToServer() {
        // expect:
        assertFalse(clientListener.isConnected());
        assertNull(clientListener.getReceived());

        // when:
        service.send("hello");

        await().atMost(2, TimeUnit.SECONDS).until(() -> clientListener.getReceived() != null);

        // then:
        assertTrue(clientListener.isConnected());
        assertEquals(new Command().setMessage("HELLO"), clientListener.getReceived());
    }
}
