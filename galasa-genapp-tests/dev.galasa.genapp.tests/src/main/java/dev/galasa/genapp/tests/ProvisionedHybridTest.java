/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2020.
 */
package dev.galasa.genapp.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import dev.galasa.BeforeClass;
import dev.galasa.Test;
import dev.galasa.artifact.BundleResources;
import dev.galasa.artifact.IBundleResources;
import dev.galasa.artifact.TestBundleResourceException;
import dev.galasa.core.manager.StoredArtifactRoot;
import dev.galasa.docker.DockerContainer;
import dev.galasa.docker.IDockerContainer;
import dev.galasa.genapp.manager.AreasTested;
import dev.galasa.genapp.manager.Customer;
import dev.galasa.genapp.manager.GenApp;
import dev.galasa.genapp.manager.GenAppManagerException;
import dev.galasa.genapp.manager.ICustomer;
import dev.galasa.genapp.manager.IGenApp;
import dev.galasa.http.HttpClient;
import dev.galasa.http.HttpClientException;
import dev.galasa.http.IHttpClient;

@Test
@AreasTested(areas = {"customer"})
public class ProvisionedHybridTest {

    @GenApp
    public IGenApp genApp;

    @Customer
    public ICustomer customer;

    @HttpClient
    public IHttpClient client;

    @BundleResources
    public IBundleResources resources;

    @StoredArtifactRoot
    public Path artifactRoot;

    @DockerContainer(image = "lukasmarivoet/genapp-spring:latest")
    public IDockerContainer container;

    @BeforeClass
    public void waitForRestService() throws InterruptedException {
        Thread.sleep(5000);
    }

    @Test
    public void prosivionedAccountTest() throws GenAppManagerException, URISyntaxException, TestBundleResourceException,
            JsonSyntaxException, IOException, HttpClientException {
        //Generate a random string and update the first name of the customer using a 3270 terminal
        String provisionedName = genApp.provisionCustomerName();
        customer.updateFirstName(provisionedName);

        InetSocketAddress socket = container.getFirstSocketForExposedPort("8080/tcp");
        client.setURI(new URI("http://" + socket.getAddress().getHostName() + ":" + socket.getPort()));
        JsonObject inquireResponseBody = client.postJson("inquirecustomer?host=" + genApp.getAddress() + "&id=" + customer.getCustomerNumber() , new JsonObject()).getContent();

        //Ensure that the first name in the Json response is the updated value
        String webServiceFirstName = extractFromInquireJson(inquireResponseBody, "ca_first_name");
        assertThat(webServiceFirstName).isEqualTo(customer.getFirstName());
    }

    @Test
    public void addWebserviceAccount() throws TestBundleResourceException, IOException, HttpClientException,
            URISyntaxException, GenAppManagerException {
        InetSocketAddress socket = container.getFirstSocketForExposedPort("8080/tcp");
        client.setURI(new URI("http://" + socket.getAddress().getHostName() + ":" + socket.getPort()));
        JsonObject addResponseBody = client.postJson("addcustomer?host=" + genApp.getAddress(), new JsonObject()).getContent();

        //Retrieve the created customer data using a 3270 terminal
        int customerNumber = Integer.parseInt(addResponseBody.get("content").getAsString());
        ICustomer customer = genApp.inquireCustomer(customerNumber);

        //Assert that the customer was created correctly
        assertThat(customer).isNotNull();
    }

    private String extractFromInquireJson(JsonObject json, String field) {
        return json.get("LGICUS01OperationResponse").getAsJsonObject().get("ca")
                   .getAsJsonObject().get(field).getAsString();
    }

}