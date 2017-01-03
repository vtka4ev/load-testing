/*
 * Copyright (C) 2016 AlertMe.com Ltd
 */
package com.alertme.zoo.shared.aws_load_testing.provisioning;

import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;

public interface ThingProvisioner {

    CreateKeysAndCertificateResult provisionThings();

    void cleanUp(CreateKeysAndCertificateResult certForAccess);
}
