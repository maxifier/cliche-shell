/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.maxifier.cliche;

import org.apache.sshd.common.keyprovider.AbstractKeyPairProvider;

import java.io.*;
import java.security.KeyPair;

/**
 * @author aleksey.didik@maxifier.com (Aleksey Didik)
 */
public class ResourceHostKeyProvider extends AbstractKeyPairProvider {

    private String path;
    private KeyPair keyPair;

    public ResourceHostKeyProvider(String path) {
        this.path = path;
    }

    public synchronized KeyPair[] loadKeys() {
        if (keyPair == null) {
            InputStream in = ResourceHostKeyProvider.class.getClassLoader().getResourceAsStream(path);
            keyPair = readKeyPair(in);
        }
        return new KeyPair[]{keyPair};
    }

    private KeyPair readKeyPair(InputStream is) {
        if (is == null) {
            throw new IllegalStateException("Unable to read keypair resource in jar");
        }
        try {
            ObjectInputStream r = new ObjectInputStream(is);
            return (KeyPair) r.readObject();
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to read key %s", path), e);
        } finally {
            close(is);
        }
    }


    private void close(Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (IOException e) {
            // Ignore
        }
    }
}
