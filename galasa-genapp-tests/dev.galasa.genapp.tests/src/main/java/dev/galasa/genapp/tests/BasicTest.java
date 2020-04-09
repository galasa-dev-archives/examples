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
import dev.galasa.http.HttpClient;
import dev.galasa.http.HttpClientException;
import dev.galasa.http.IHttpClient;
import dev.galasa.zos.IZosImage;
import dev.galasa.zos.ZosImage;
import dev.galasa.zos3270.ITerminal;
import dev.galasa.zos3270.Zos3270Exception;
import dev.galasa.zos3270.Zos3270Terminal;

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

    final String APPLID = "CICPY01D";

    @Test
    public void test() throws CoreManagerException, InterruptedException, Zos3270Exception,
            TestBundleResourceException, JsonSyntaxException, IOException, HttpClientException, URISyntaxException {
        client.setURI(new URI("http://winmvs2c.hursley.ibm.com:23571"));
        
        HashMap<String, Object> addParameters = new HashMap<String, Object>();
        String runId = coreManager.getRunName();
        addParameters.put("ID", runId);
        InputStream addIs = bundleResources.retrieveSkeletonFile("resources/skeletons/customerAdd.skel", addParameters);
        JsonObject customerAddJson = new Gson().fromJson(bundleResources.streamAsString(addIs), JsonObject.class);
        JsonObject addResponseBody = client.postJson("GENAPP/addCustomerDetails", customerAddJson).getContent();
        String customerId = addResponseBody.get("LGACUS01OperationResponse").getAsJsonObject()
                                        .get("ca").getAsJsonObject()
                                        .get("ca_customer_num").getAsString();
        
        HashMap<String, Object> InquireParameters = new HashMap<String, Object>();
        InquireParameters.put("NUM", customerId);
        InputStream inquireIs = bundleResources.retrieveSkeletonFile("resources/skeletons/customerInquire.skel", InquireParameters);
        JsonObject customerInquireJson = new Gson().fromJson(bundleResources.streamAsString(inquireIs), JsonObject.class);
        JsonObject inquireResponseBody = client.postJson("GENAPP/getCustomerDetails", customerInquireJson).getContent();
        JsonObject ca = inquireResponseBody.get("LGICUS01OperationResponse").getAsJsonObject()
                                   .get("ca").getAsJsonObject();

        assertThat(ca.get("ca_first_name").getAsString()).isEqualTo("Test" + runId);
        assertThat(ca.get("ca_last_name").getAsString()).isEqualTo("Case" + runId);

        loginToSCC1();

        String defaultId = "0000000000";
        terminal.positionCursorToFieldContaining("Cust Number").tab()
                .type(defaultId.substring(0,defaultId.length()-customerId.length()) + customerId).enter().waitForKeyboard()
                .positionCursorToFieldContaining("Select Option").tab()
                .type("1").enter().waitForKeyboard();
        
        assertThat(terminal.retrieveScreen()).contains("Test" + runId);
        assertThat(terminal.retrieveScreen()).contains("Case" + runId);
    }

    private void loginToSCC1() throws InterruptedException, CoreManagerException, Zos3270Exception {
        terminal.waitForKeyboard().type("logon applid(" + APPLID + ")")
                .enter().waitForTextInField("Userid")
                .pf3().waitForTextInField("Sign-on is terminated").clear().waitForKeyboard()
                .type("cesl").enter().waitForTextInField("Userid");
        ICredentialsUsernamePassword creds = (ICredentialsUsernamePassword) coreManager.getCredentials("GENAPP");
        coreManager.registerConfidentialText(creds.getPassword(), creds.getUsername() + " password");
        terminal.positionCursorToFieldContaining("Userid").tab()
                .type(creds.getUsername())
                .positionCursorToFieldContaining("Password").tab()
                .type(creds.getPassword())
                .enter().waitForKeyboard()
                .clear().waitForKeyboard()
                .type("ssc1").enter().waitForKeyboard();
    }
}