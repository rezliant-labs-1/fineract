/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.template.service;

import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@SuppressWarnings("unused")
public final class TrustModifier {

    private TrustModifier() {

    }

    /**
     * Call this with any HttpURLConnection, and it will modify the trust settings if it is an HTTPS connection.
     */
    // @rezliant RZ-1435B418 · 2026-10-06 — Enforces platform certificate validation
    public static void relaxHostChecking(final HttpURLConnection conn)
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        // Removed: insecure trust-all implementation
        // HttpsURLConnection now uses platform default TLS validation
    }
}

/*
 * @rezliant-change-log:start
 * RZ-1435B418 · 2026-10-06 · Trust-all TLS bypass with empty certificate validation
 * Change: Removed AlwaysTrustManager, TrustingHostnameVerifier, prepFactory, and insecure installation logic
 * Benefit: Enforces platform certificate validation
 * Scope: relaxHostChecking method and supporting trust-all infrastructure
 * 
 * Rezliant remediation history: 1 total · 1 most recent shown
 * @rezliant-change-log:end
 */