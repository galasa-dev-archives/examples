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
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import dev.galasa.ICredentialsUsernamePassword;
import dev.galasa.Test;
import dev.galasa.artifact.BundleResources;
import dev.galasa.artifact.IBundleResources;
import dev.galasa.artifact.TestBundleResourceException;
import dev.galasa.core.manager.CoreManager;
import dev.galasa.core.manager.CoreManagerException;
import dev.galasa.core.manager.ICoreManager;
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
import dev.galasa.zos3270.TerminalInterruptedException;
import dev.galasa.zos3270.TextNotFoundException;
import dev.galasa.zos3270.TimeoutException;
import dev.galasa.zos3270.Zos3270Exception;
import dev.galasa.zos3270.Zos3270Terminal;
import dev.galasa.zos3270.spi.NetworkException;

@Test
public class BasicTest {

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

        @Test
        public void basicWebservices() throws CoreManagerException, Zos3270Exception, TestBundleResourceException,
                        JsonSyntaxException, IOException, HttpClientException, URISyntaxException {
                // Coupling the URI of GenApp to this instance of the Http-client
                client.setURI(new URI(genApp.getBaseAddress()));

                HashMap<String, Object> addParameters = new HashMap<String, Object>();
                HashMap<String, Object> inquireParameters = new HashMap<String, Object>();
                String provisionedName = genApp.provisionCustomerName();
                String defaultId = "0000000000";

                // Adding a customer using the exposed http-ports of the CICS-region that has
                // GenApp running
                addParameters.put("ID", provisionedName);
                InputStream addIs = bundleResources.retrieveSkeletonFile("resources/skeletons/customerAdd.skel",
                                addParameters);
                JsonObject customerAddJson = new Gson().fromJson(bundleResources.streamAsString(addIs),
                                JsonObject.class);
                JsonObject addResponseBody = client.postJson("GENAPP/addCustomerDetails", customerAddJson).getContent();
                String customerId = addResponseBody.get("LGACUS01OperationResponse").getAsJsonObject().get("ca")
                                .getAsJsonObject().get("ca_customer_num").getAsString();

                // Inquiring about the added customer using the exposed http-ports of the
                // CICS-region that has GenApp running
                inquireParameters.put("NUM", customerId);
                InputStream inquireIs = bundleResources.retrieveSkeletonFile("resources/skeletons/customerInquire.skel",
                                inquireParameters);
                JsonObject customerInquireJson = new Gson().fromJson(bundleResources.streamAsString(inquireIs),
                                JsonObject.class);
                JsonObject inquireResponseBody = client.postJson("GENAPP/getCustomerDetails", customerInquireJson)
                                .getContent();
                JsonObject ca = inquireResponseBody.get("LGICUS01OperationResponse").getAsJsonObject().get("ca")
                                .getAsJsonObject();

                // Ensuring that the http-response actually contains user as requested
                assertThat(ca.get("ca_first_name").getAsString()).isEqualTo(provisionedName);
                assertThat(ca.get("ca_last_name").getAsString()).isEqualTo(provisionedName);

                // Ensuring that the added customer is also available through 3270-terminal
                loginToSCC1();

                inquireCustomer(customerId);

                assertThat(terminal.retrieveScreen()).contains(provisionedName);

                String updateProvisionedName = genApp.provisionCustomerName();

                terminal.positionCursorToFieldContaining("Cust Number").tab()
                                .type(defaultId.substring(0, defaultId.length() - customerId.length()) + customerId)
                                .positionCursorToFieldContaining("Select Option").tab().type("4").enter()
                                .waitForKeyboard().positionCursorToFieldContaining("First").tab()
                                .type(updateProvisionedName).positionCursorToFieldContaining("Last").tab()
                                .type(updateProvisionedName).enter().waitForKeyboard();

                inquireCustomer(customerId);

                assertThat(terminal.retrieveScreen()).contains(updateProvisionedName);
        }

        private void loginToSCC1() throws CoreManagerException, Zos3270Exception {
                // Retrieving the credentials that have the correct RACF-premissions to enter
                // GenApp
                ICredentialsUsernamePassword creds = (ICredentialsUsernamePassword) coreManager
                                .getCredentials("GENAPP");
                coreManager.registerConfidentialText(creds.getPassword(), creds.getUsername() + " password");
                // Logging into the CICS-region that has GenApp running on it
                terminal.waitForKeyboard().type("logon applid(" + genApp.getApplId() + ")").enter()
                                .waitForTextInField("Userid").pf3().waitForTextInField("Sign-on is terminated").clear()
                                .waitForKeyboard().type("cesl").enter().waitForTextInField("Userid")
                                .positionCursorToFieldContaining("Userid").tab().type(creds.getUsername())
                                .positionCursorToFieldContaining("Password").tab().type(creds.getPassword()).enter()
                                .waitForKeyboard().clear().waitForKeyboard().type("ssc1").enter().waitForKeyboard();
        }

        private void inquireCustomer(String customerId)
                        throws TimeoutException, KeyboardLockedException, NetworkException, FieldNotFoundException,
                        TextNotFoundException, TerminalInterruptedException {
        String defaultId = "0000000000";

        terminal.positionCursorToFieldContaining("Cust Number").tab()
                .type(defaultId.substring(0,defaultId.length()-customerId.length()) + customerId).enter().waitForKeyboard()
                .positionCursorToFieldContaining("Select Option").tab()
                .type("1").enter().waitForKeyboard();
    }
}