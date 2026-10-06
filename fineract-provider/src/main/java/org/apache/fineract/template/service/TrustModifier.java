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

// @rezliant RZ-068F7235 · 2026-10-06 — Eliminates callable SSL/TLS validation bypass

/*
 * @rezliant-change-log:start
 * RZ-068F7235 · 2026-10-06 · SSL/TLS certificate validation bypass through trust-all manager
 * Change: Removed TrustModifier class containing AlwaysTrustManager and TrustingHostnameVerifier
 * Benefit: Eliminates callable SSL/TLS validation bypass preventing man-in-the-middle attacks
 * Scope: Entire TrustModifier class
 * 
 * Rezliant remediation history: 1 total · 1 most recent shown
 * @rezliant-change-log:end
 */