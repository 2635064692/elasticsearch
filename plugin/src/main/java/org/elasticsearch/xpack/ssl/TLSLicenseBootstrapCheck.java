/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.ssl;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.bootstrap.BootstrapCheck;
import org.elasticsearch.bootstrap.BootstrapContext;
import org.elasticsearch.common.inject.internal.Nullable;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.license.License;
import org.elasticsearch.license.LicenseService;
import org.elasticsearch.xpack.XPackSettings;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Bootstrap check to ensure that if we are starting up with a production license in the local clusterstate TLS is enabled
 */
public final class TLSLicenseBootstrapCheck implements BootstrapCheck {
    @Override
    public BootstrapCheckResult check(BootstrapContext context) {
        if (XPackSettings.TRANSPORT_SSL_ENABLED.get(context.settings) == false) {
            License license = LicenseService.getLicense(context.metaData);
            if (license != null && license.isProductionLicense()) {
                return BootstrapCheckResult.failure("Transport SSL must be enabled for setups with production licenses. Please set " +
                        "[xpack.security.transport.ssl.enabled] or disables security via [xpack.security.enabled]");
            }
        }
        return BootstrapCheckResult.success();
    }
}
