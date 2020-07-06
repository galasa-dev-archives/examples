/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2020.
 */
package dev.galasa.genapp.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.logging.Log;

import dev.galasa.ResultArchiveStoreContentType;
import dev.galasa.SetContentType;
import dev.galasa.Test;
import dev.galasa.artifact.BundleResources;
import dev.galasa.artifact.IBundleResources;
import dev.galasa.artifact.TestBundleResourceException;
import dev.galasa.core.manager.Logger;
import dev.galasa.core.manager.StoredArtifactRoot;
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
public class ProvisionedTest {

    @Logger
    Log logger;

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

    @Test
    public void prosivionedAccountTest() throws GenAppManagerException, URISyntaxException, TestBundleResourceException,
            JsonSyntaxException, IOException, HttpClientException {
        //Generate a random string and update the first name of the customer using a 3270 terminal
        String provisionedName = genApp.provisionCustomerName();
        customer.updateFirstName(provisionedName);

        //Set up the customer inquire Json request
        HashMap<String, Object> inquireParameters = new HashMap<String, Object>();
        inquireParameters.put("NUM", customer.getCustomerNumber());
        InputStream inquireIs = resources.retrieveSkeletonFile("resources/skeletons/customerInquire.skel", inquireParameters);
        String jsonRequest = resources.streamAsString(inquireIs);

        //Store the request to the RAS
        storeOutput("apicall", "inquireRequest.txt", jsonRequest);

        //Invoke the Json request
        JsonObject customerInquireJson = new Gson().fromJson(jsonRequest, JsonObject.class);
        client.setURI(new URI(genApp.getAddress()));
        JsonObject inquireResponseBody = client.postJson(genApp.getInquireCustomerPath(), customerInquireJson).getContent();

        //Store the Json service response
        storeOutput("apicall", "inquireResponse.txt", new Gson().toJson(inquireResponseBody));

        //Ensure that the first name in the Json response is the updated value
        String webServiceFirstName = extractFromInquireJson(inquireResponseBody, "ca_first_name");
        assertThat(webServiceFirstName).isEqualTo(customer.getFirstName());
    }

    @Test
    public void addWebserviceAccount() throws TestBundleResourceException, IOException, HttpClientException,
            URISyntaxException, GenAppManagerException {
        //Set up an add customer Json request with a random string generated for the name
        String provisionedName = genApp.provisionCustomerName();
        HashMap<String, Object> addParameters = new HashMap<String, Object>();
        addParameters.put("ID", provisionedName);
        InputStream addIs = resources.retrieveSkeletonFile("resources/skeletons/customerAdd.skel", addParameters);
        String jsonRequest = resources.streamAsString(addIs);

        //Store the Json request in the RAS
        storeOutput("apicall", "addRequest.txt", jsonRequest);

        //Invoke the Json request
        JsonObject customerAddJson = new Gson().fromJson(jsonRequest, JsonObject.class);
        client.setURI(new URI(genApp.getAddress()));
        JsonObject addResponseBody = client.postJson(genApp.getAddCustomerPath(), customerAddJson).getContent();

        //Store the Json response in the RAS
        storeOutput("apicall", "addResponse.txt", new Gson().toJson(addResponseBody));

        //Retrieve the created customer data using a 3270 terminal
        int customerNumber = extractFromAddJson(addResponseBody);
        ICustomer customer = genApp.inquireCustomer(customerNumber);

        //Assert that the customer was created correctly
        assertThat(customer).isNotNull();
        assertThat(customer.getFirstName()).isEqualTo(provisionedName);
        assertThat(customer.getLastName()).isEqualTo(provisionedName);
    }

    private String extractFromInquireJson(JsonObject json, String field) {
        return json.get("LGICUS01OperationResponse").getAsJsonObject().get("ca")
                   .getAsJsonObject().get(field).getAsString();
    }

    private int extractFromAddJson(JsonObject json) {
        return json.get("LGACUS01OperationResponse").getAsJsonObject().get("ca")
                   .getAsJsonObject().get("ca_customer_num").getAsInt();
    }

    private void storeOutput(String folder, String file, String content) throws IOException {
        Path requestPath = artifactRoot.resolve(folder).resolve(file);
        Files.write(requestPath, content.getBytes(), new SetContentType(ResultArchiveStoreContentType.TEXT),
                StandardOpenOption.CREATE);
    }

}