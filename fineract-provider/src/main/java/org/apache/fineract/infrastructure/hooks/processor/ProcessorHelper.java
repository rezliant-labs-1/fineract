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
package org.apache.fineract.infrastructure.hooks.processor;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Service
public final class ProcessorHelper {

    // Nota bene: Similar code to insecure HTTPS is also in Fineract Client's
    // org.apache.fineract.client.util.FineractClient.Builder.insecure()

    private static final Logger LOG = LoggerFactory.getLogger(ProcessorHelper.class);

    /**
     * Configure HTTP client to be "insecure", as in skipping host SSL certificate verification. While this can be
     * useful during development e.g. when using self-signed certificates, it should never be enabled in production (due
     * to "man in the middle").
     */
    private final boolean insecureHttpClient = Boolean.getBoolean("fineract.insecureHttpClient");
    private final SSLContext insecureSSLContext;

    // @rezliant RZ-584BCC5F · 2026-10-06 — Uses platform trust store for certificate validation
    public ProcessorHelper() throws KeyManagementException, NoSuchAlgorithmException {
        if (insecureHttpClient) {
            insecureSSLContext = SSLContext.getInstance("TLS");
            insecureSSLContext.init(null, null, new SecureRandom());
        } else {
            insecureSSLContext = null;
        }
    }

    private OkHttpClient createClient() {
        var okBuilder = new OkHttpClient.Builder();
        if (insecureHttpClient) {
            configureInsecureClient(okBuilder);
        }
        return okBuilder.build();
    }

    private void configureInsecureClient(final OkHttpClient.Builder okBuilder) {
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((java.security.KeyStore) null);
            X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
            okBuilder.sslSocketFactory(insecureSSLContext.getSocketFactory(), defaultTrustManager);
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            LOG.error("Failed to configure SSL context with default trust manager", e);
            throw new RuntimeException("SSL configuration failed", e);
        }
    }

    @SuppressWarnings("rawtypes")
    public Callback createCallback(final String url) {
        return new Callback() {

            @Override
            public void onResponse(@SuppressWarnings("unused") Call call, retrofit2.Response response) {
                LOG.debug("URL: {} - Status: {}", url, response.code());
            }

            @Override
            public void onFailure(@SuppressWarnings("unused") Call call, Throwable t) {
                LOG.error("URL: {} - Retrofit failure occurred", url, t);
            }
        };
    }

    public WebHookService createWebHookService(final String url) {
        final OkHttpClient client = createClient();
        final Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.baseUrl(url);
        retrofitBuilder.client(client);
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
        final Retrofit retrofit = retrofitBuilder.build();
        return retrofit.create(WebHookService.class);
    }

    @SuppressWarnings("rawtypes")
    public Callback createCallback(final String url, String payload) {

        return new Callback() {

            @Override
            public void onResponse(@SuppressWarnings("unused") Call call, retrofit2.Response response) {
                LOG.debug("URL: {} - Status: {}", url, response.code());
            }

            @Override
            public void onFailure(@SuppressWarnings("unused") Call call, Throwable t) {
                LOG.error("URL: {} - Retrofit failure occured", url, t);
            }
        };
    }
}

/*
 * @rezliant-change-log:start
 * RZ-584BCC5F · 2026-10-06 · Trust-all X509TrustManager and permissive hostname verification bypass certificate validation
 * Change: Removed insecureX509TrustManager; SSLContext now uses platform default trust managers; configureInsecureClient retrieves default X509TrustManager from TrustManagerFactory
 * Benefit: Uses platform trust store for certificate validation
 * Scope: ProcessorHelper constructor and configureInsecureClient method
 * 
 * Rezliant remediation history: 1 total · 1 most recent shown
 * @rezliant-change-log:end
 */