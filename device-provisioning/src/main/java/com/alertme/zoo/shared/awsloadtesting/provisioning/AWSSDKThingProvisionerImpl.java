/*
 * Copyright (C) 2016 AlertMe.com Ltd
 */
package com.alertme.zoo.shared.awsloadtesting.provisioning;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AWSSDKThingProvisionerImpl implements ThingProvisioner {

    private static final String TEST_POLICY_NAME = "PolicyForLoadTesting";
    private static final String POLICY = "{\n"
            + "  \"Version\": \"2012-10-17\",\n"
            + "  \"Statement\": [\n"
            + "    {\n"
            + "      \"Effect\": \"Allow\",\n"
            + "      \"Action\": \"iot:*\",\n"
            + "      \"Resource\": \"*\"\n"
            + "    }\n"
            + "  ]\n"
            + "}";
    private final AWSIotClient client;
    private final List<String> thingNames;

    public AWSSDKThingProvisionerImpl(List<String> thingNames) {
        this.thingNames = thingNames;
        client = new AWSIotClient();
        client.setRegion(Region.getRegion(Regions.EU_WEST_1));
    }

    private CreateThingResult createThing(String name) {
        CreateThingRequest createThingRequest = new CreateThingRequest().withThingName(name);
        client.createThing(createThingRequest);
        return client.createThing(createThingRequest);
    }


    private CreateKeysAndCertificateResult createCertificate() {
        CreateKeysAndCertificateRequest req = new CreateKeysAndCertificateRequest();
        req.setSetAsActive(true);
        return client.createKeysAndCertificate(req);
    }

//    ListCertificatesResult getCertificate() {
//        ListCertificatesRequest certificatesRequest = new ListCertificatesRequest()
//                .withPageSize(50);
//        ListCertificatesResult certificatesResult = client.listCertificates(certificatesRequest);
//         certificatesResult.getCertificates().get(0);
//        return null != certificatesResult ? certificatesRequest : createCertificate();
//    }

    private boolean noPolicy(final String name) {
        ListPoliciesRequest policiesRequest = new ListPoliciesRequest().withPageSize(50);
        ListPoliciesResult policiesResult = client.listPolicies(policiesRequest);
        return policiesResult
                .getPolicies()
                .stream()
                .filter(policy -> name.equals(policy.getPolicyName()))
                .collect(Collectors.toList())
                .isEmpty();
    }

    CreatePolicyResult createPolicy(String name) {
        CreatePolicyRequest createPolicyRequest = new CreatePolicyRequest()
                .withPolicyName(name)
                .withPolicyDocument(POLICY);
        return client.createPolicy(createPolicyRequest);
    }

    private void attachPolicy(String certificateArn, String policyName) {
        AttachPrincipalPolicyRequest req = new AttachPrincipalPolicyRequest()
                .withPrincipal(certificateArn)
                .withPolicyName(policyName);
        client.attachPrincipalPolicy(req);
    }

    @Override
    public CreateKeysAndCertificateResult provisionThings() {
        CreateKeysAndCertificateResult certAndKey = provisionCertificateAndPolicy();
        thingNames.stream().forEach(thingName -> createThing(thingName));
        return certAndKey;
    }

    private CreateKeysAndCertificateResult provisionCertificateAndPolicy() {
        final CreateKeysAndCertificateResult createKeysAndCertificateResult = createCertificate();
        if (noPolicy(TEST_POLICY_NAME)) {
            createPolicy(TEST_POLICY_NAME);
        }
        attachPolicy(createKeysAndCertificateResult.getCertificateArn(), TEST_POLICY_NAME);
        return createKeysAndCertificateResult;
    }

    private DeleteThingResult deleteThing(String thingName) {
        DeleteThingRequest req = new DeleteThingRequest().withThingName(thingName);
        return client.deleteThing(req);
    }

    private void detachPolicy(String certificateArn, String policyName) {
        DetachPrincipalPolicyRequest req = new DetachPrincipalPolicyRequest().withPrincipal(certificateArn)
                .withPolicyName(policyName);
        client.detachPrincipalPolicy(req);
    }

    private void deleteCertificate(String certificateId) {
        DeleteCertificateRequest req = new DeleteCertificateRequest().withCertificateId(certificateId);
        client.deleteCertificate(req);
    }

    private void deletePolicy(String policyName) {
        DeletePolicyRequest req = new DeletePolicyRequest().withPolicyName(policyName);
        client.deletePolicy(req);
    }

    private void updateCertificate(String certificateId, boolean activate) {
        UpdateCertificateRequest req = new UpdateCertificateRequest().withCertificateId(certificateId).withNewStatus(
                activate ? CertificateStatus.ACTIVE : CertificateStatus.INACTIVE);

        client.updateCertificate(req);
    }

    @Override
    public void cleanUp(final CreateKeysAndCertificateResult certForAccess) {
        detachPolicy(certForAccess.getCertificateArn(), TEST_POLICY_NAME);
        updateCertificate(certForAccess.getCertificateId(), false);
        deleteCertificate(certForAccess.getCertificateId());
        deletePolicy(TEST_POLICY_NAME);
        thingNames.stream().forEach(thingName -> deleteThing(thingName));
    }


    // main method is just for testing purposes.
//    public static void main(String[] args) {
//        final String[] names =
//                {"vtkachevSDKThing1"};
//        AWSSDKThingProvisionerImpl thingCreator = new AWSSDKThingProvisionerImpl(Arrays.asList(names));
////        CreateKeysAndCertificateResult certForAccess = thingCreator.provisionThings();
////        thingCreator.cleanUp(certForAccess);
//    }

}
