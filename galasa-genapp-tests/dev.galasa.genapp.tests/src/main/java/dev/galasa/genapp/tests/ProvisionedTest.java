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

import dev.galasa.ResultArchiveStoreContentType;
import dev.galasa.SetContentType;
import dev.galasa.Test;
import dev.galasa.artifact.BundleResources;
import dev.galasa.artifact.IBundleResources;
import dev.galasa.artifact.TestBundleResourceException;
import dev.galasa.core.manager.CoreManager;
import dev.galasa.core.manager.ICoreManager;
import dev.galasa.core.manager.StoredArtifactRoot;
import dev.galasa.genapp.manager.Customer;
import dev.galasa.genapp.manager.GenApp;
import dev.galasa.genapp.manager.GenAppManagerException;
import dev.galasa.genapp.manager.ICustomer;
import dev.galasa.genapp.manager.IGenApp;
import dev.galasa.http.HttpClient;
import dev.galasa.http.HttpClientException;
import dev.galasa.http.IHttpClient;

@Test
public class ProvisionedTest {

    @GenApp
    public IGenApp genapp;

    @Customer
    public ICustomer customer;

    @HttpClient
    public IHttpClient client;

    @BundleResources
    public IBundleResources resources;

    @StoredArtifactRoot
    public Path artifactRoot;

    @CoreManager
    public ICoreManager coreManager;

    @Test
    public void prosivionedAccountTest() throws GenAppManagerException, URISyntaxException, TestBundleResourceException,
            JsonSyntaxException, IOException, HttpClientException {
        String runId = coreManager.getRunName();

        customer.updateFirstName("Test" + runId);

        HashMap<String, Object> inquireParameters = new HashMap<String, Object>();
        inquireParameters.put("NUM", customer.getCustomerNumber());
        InputStream inquireIs = resources.retrieveSkeletonFile("resources/skeletons/customerInquire.skel", inquireParameters);
        String jsonRequest = resources.streamAsString(inquireIs);

        storeOutput("webservice", "inquireRequest.txt", jsonRequest);

        JsonObject customerInquireJson = new Gson().fromJson(jsonRequest, JsonObject.class);

        client.setURI(new URI("http://" + genapp.getAddress() + ":" + genapp.getWebnetPort()));
        JsonObject inquireResponseBody = client.postJson(genapp.getInquireCustomerPath(), customerInquireJson).getContent();

        storeOutput("webservice", "inquireResponse.txt", new Gson().toJson(inquireResponseBody));

        String webServiceFirstName = extractFromInquireJson(inquireResponseBody, "ca_first_name");

        assertThat(webServiceFirstName).isEqualTo(customer.getFirstName());
    }

    @Test
    public void addWebserviceAccount() throws TestBundleResourceException, IOException, HttpClientException,
            URISyntaxException, GenAppManagerException {
        String runId = coreManager.getRunName();

        HashMap<String, Object> addParameters = new HashMap<String, Object>();
        addParameters.put("ID", runId);
        InputStream addIs = resources.retrieveSkeletonFile("resources/skeletons/customerAdd.skel", addParameters);
        String jsonRequest = resources.streamAsString(addIs);

        storeOutput("webservice", "addRequest.txt", jsonRequest);

        JsonObject customerAddJson = new Gson().fromJson(jsonRequest, JsonObject.class);

        client.setURI(new URI("http://" + genapp.getAddress() + ":" + genapp.getWebnetPort()));
        JsonObject addResponseBody = client.postJson(genapp.getAddCustomerPath(), customerAddJson).getContent();

        storeOutput("webservice", "addResponse.txt", new Gson().toJson(addResponseBody));

        int customerNumber = extractFromAddJson(addResponseBody);

        ICustomer customer = genapp.inquireCustomer(customerNumber);

        assertThat(customer).isNotNull();
        assertThat(customer.getFirstName()).isEqualTo("Test" + runId);
        assertThat(customer.getLastName()).isEqualTo("Case" + runId);
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