// File removed - TrustModifier.java
// @rezliant RZ-A91B50A1 · 2026-10-06 — Prevents man-in-the-middle attacks by eliminating trust-all TLS bypass

// This file contained a trust-all TrustManager and HostnameVerifier that disabled
// certificate validation. The class has been removed to prevent man-in-the-middle attacks.
// Callers must use standard HTTPS connections with proper certificate validation via
// the platform default TrustManagerFactory and legitimate CA certificates.

/*
@rezliant-change-log:start
RZ-A91B50A1 · 2026-10-06 · Trust-all TrustManager disables certificate validation (line 80)
Change: Removed entire TrustModifier class containing AlwaysTrustManager and TrustingHostnameVerifier
Benefit: Prevents man-in-the-middle attacks by eliminating trust-all TLS bypass
Scope: TrustModifier class

Rezliant remediation history: 1 total · 1 most recent shown
@rezliant-change-log:end
*/