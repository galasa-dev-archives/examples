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

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import dev.galasa.BeforeClass;
import dev.galasa.ICredentialsUsernamePassword;
import dev.galasa.Test;
import dev.galasa.artifact.BundleResources;
import dev.galasa.artifact.IBundleResources;
import dev.galasa.artifact.TestBundleResourceException;
import dev.galasa.core.manager.CoreManager;
import dev.galasa.core.manager.CoreManagerException;
import dev.galasa.core.manager.ICoreManager;
import dev.galasa.docker.DockerContainer;
import dev.galasa.docker.DockerManagerException;
import dev.galasa.docker.IDockerContainer;
import dev.galasa.genapp.manager.BasicGenApp;
import dev.galasa.genapp.manager.IBasicGenApp;
import dev.galasa.http.HttpClient;
import dev.galasa.http.HttpClientException;
import dev.galasa.http.IHttpClient;
import dev.galasa.zos.IZosImage;
import dev.galasa.zos.ZosImage;
import dev.galasa.zos3270.FieldNotFoundException;
import dev.galasa.zos3270.ITerminal;
import dev.galasa.zos3270.KeyboardLockedException;
import dev.galasa.zos3270.TextNotFoundException;
import dev.galasa.zos3270.TimeoutException;
import dev.galasa.zos3270.Zos3270Exception;
import dev.galasa.zos3270.Zos3270Terminal;
import dev.galasa.zos3270.spi.NetworkException;

@Test
public class BasicHybridTest {

    @CoreManager
    public ICoreManager coreManager;

    @ZosImage(imageTag = "GENAPP")
    public IZosImage image;

    @Zos3270Terminal(imageTag = "GENAPP")
    public ITerminal terminal;

    @BundleResources
    public IBundleResources bundleResources;

    @HttpClient
    public IHttpClient client;

    @BasicGenApp
    public IBasicGenApp genApp;

    // Docker image that contains a running Spring-instance that talks to the Genapp webservice
    @DockerContainer(image = "lukasmarivoet/genapp-spring:latest")
    public IDockerContainer container;

    // Necessary to allow the Spring service to boot up and expose its 80 TCP port
    @BeforeClass
    public void waitForRestService() throws InterruptedException {
        Thread.sleep(5000);
    }

    @Test
    public void basicHybridTest() throws CoreManagerException, InterruptedException, Zos3270Exception, TestBundleResourceException,
            JsonSyntaxException, IOException, HttpClientException, URISyntaxException , DockerManagerException{
        InetSocketAddress socket = container.getFirstSocketForExposedPort("8080/tcp");
        client.setURI(new URI("http://" + socket.getAddress().getHostName() + ":" + socket.getPort()));

        String defaultId = "0000000000";

        // Adding an empty customer to the data so that we can retrieve its customer number
        JsonObject addResponseBody = client.postJson("addcustomer?host=" + genApp.getBaseAddress(), new JsonObject()).getContent();
        String customerId = addResponseBody.get("content").getAsString();

        // Inquiring the specific customer through Spring that in turn calls towards the Genapp Web based instance
        JsonObject inquireResponseBody = client.postJson("inquirecustomer?host=" + genApp.getBaseAddress() + "&id=" + customerId , new JsonObject()).getContent();
        JsonObject ca = inquireResponseBody.get("LGICUS01OperationResponse").getAsJsonObject().get("ca")
                .getAsJsonObject();

        // Ensuring that the http-response actually contain an empty user as requested
        assertThat(ca.get("ca_first_name").getAsString()).isEqualTo("");
        assertThat(ca.get("ca_last_name").getAsString()).isEqualTo("");

        // Ensuring that the added customer is also available through 3270-terminal
        loginToSCC1();

        inquireCustomer(customerId);

        // Updating the customer through 3270
        String updateProvisionedName = genApp.provisionCustomerName();

        terminal.positionCursorToFieldContaining("Cust Number").tab()
                .type(defaultId.substring(0,defaultId.length()-customerId.length()) + customerId)
                .positionCursorToFieldContaining("Select Option").tab()
                .type("4").enter().waitForKeyboard()
                .positionCursorToFieldContaining("First").tab()
                .type(updateProvisionedName)
                .positionCursorToFieldContaining("Last").tab()
                .type(updateProvisionedName)
                .enter().waitForKeyboard();

        // Inquiring through 3270
        inquireCustomer(customerId);

        assertThat(terminal.retrieveScreen()).contains(updateProvisionedName);

        JsonObject inquireAfterUpdateResponseBody = client.postJson("inquirecustomer?host=" + genApp.getBaseAddress() + "&id=" + customerId , new JsonObject()).getContent();
        JsonObject caAfterUpdate = inquireAfterUpdateResponseBody.get("LGICUS01OperationResponse").getAsJsonObject().get("ca")
                .getAsJsonObject();

        // Ensuring that the http-response actually contains user as requested
        assertThat(caAfterUpdate.get("ca_first_name").getAsString()).isEqualTo(updateProvisionedName);
        assertThat(caAfterUpdate.get("ca_last_name").getAsString()).isEqualTo(updateProvisionedName);
    }

    private void loginToSCC1() throws InterruptedException, CoreManagerException, Zos3270Exception {
        // Retrieving the credentials that have the correct RACF-premissions to enter GenApp-region
        ICredentialsUsernamePassword creds = (ICredentialsUsernamePassword) coreManager.getCredentials("GENAPP");
        coreManager.registerConfidentialText(creds.getPassword(), creds.getUsername() + " password");
        // Logging into the CICS-region that has GenApp running on it
        terminal.waitForKeyboard().type("logon applid(" + genApp.getApplId() + ")").enter().waitForTextInField("Userid").pf3()
                .waitForTextInField("Sign-on is terminated").clear().waitForKeyboard().type("cesl").enter()
                .waitForTextInField("Userid").positionCursorToFieldContaining("Userid").tab().type(creds.getUsername())
                .positionCursorToFieldContaining("Password").tab().type(creds.getPassword()).enter().waitForKeyboard()
                .clear().waitForKeyboard().type("ssc1").enter().waitForKeyboard();
    }

    private void inquireCustomer(String customerId) throws TimeoutException, KeyboardLockedException, NetworkException,
            FieldNotFoundException, TextNotFoundException, InterruptedException {
        String defaultId = "0000000000";

        terminal.positionCursorToFieldContaining("Cust Number").tab()
                .type(defaultId.substring(0,defaultId.length()-customerId.length()) + customerId).enter().waitForKeyboard()
                .positionCursorToFieldContaining("Select Option").tab()
                .type("1").enter().waitForKeyboard();
    }
}