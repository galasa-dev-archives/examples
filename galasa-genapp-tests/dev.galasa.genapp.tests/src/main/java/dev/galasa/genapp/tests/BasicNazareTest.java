/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2020.
 */
package dev.galasa.genapp.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;

import com.google.gson.JsonSyntaxException;

import dev.galasa.ICredentialsUsernamePassword;
import dev.galasa.Test;
import dev.galasa.artifact.TestBundleResourceException;
import dev.galasa.core.manager.CoreManager;
import dev.galasa.core.manager.CoreManagerException;
import dev.galasa.core.manager.ICoreManager;
import dev.galasa.genapp.manager.BasicGenApp;
import dev.galasa.genapp.manager.IBasicGenApp;
import dev.galasa.http.HttpClientException;
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
public class BasicNazareTest {

    @CoreManager
    public ICoreManager coreManager;

    @ZosImage(imageTag = "GENAPP")
    public IZosImage image;

    @Zos3270Terminal(imageTag = "GENAPP")
    public ITerminal terminal;

    @BasicGenApp
    public IBasicGenApp genApp;

    @Test
    public void customer3270Update() throws CoreManagerException, Zos3270Exception,
            TestBundleResourceException, JsonSyntaxException, IOException, HttpClientException, URISyntaxException {

        loginToSSC1();

        String customerId = addCustomer();

        inquireCustomer(customerId);

        String updateProvisionedName = genApp.provisionCustomerName();

        terminal.positionCursorToFieldContaining("Select Option").tab().type("4").enter().waitForKeyboard()
                .positionCursorToFieldContaining("First").tab().type(updateProvisionedName)
                .positionCursorToFieldContaining("Last").tab().type(updateProvisionedName).enter().waitForKeyboard();

        inquireCustomer(customerId);

        assertThat(terminal.retrieveScreen()).contains(updateProvisionedName);
    }

    private void loginToSSC1() throws CoreManagerException, Zos3270Exception {
        if (terminal.waitForKeyboard().retrieveScreen().contains("VAMP")) {
            ICredentialsUsernamePassword creds = (ICredentialsUsernamePassword) coreManager.getCredentials("GENAPP");
            coreManager.registerConfidentialText(creds.getPassword(), creds.getUsername() + " password");

            terminal.waitForKeyboard().type("logon applid(" + genApp.getApplId() + ")").enter()
                    .waitForTextInField("Userid").pf3().waitForTextInField("Sign-on is terminated").clear()
                    .waitForKeyboard().type("cesl").enter().waitForTextInField("Userid")
                    .positionCursorToFieldContaining("Userid").tab().type(creds.getUsername())
                    .positionCursorToFieldContaining("Password").tab().type(creds.getPassword()).enter()
                    .waitForKeyboard().clear().waitForKeyboard().type("ssc1").enter().waitForKeyboard();
        } else {
            terminal.waitForKeyboard().type("logon applid(" + genApp.getApplId() + ")").enter().waitForKeyboard()
                    .clear().waitForKeyboard().type("ssc1").enter().waitForKeyboard();
        }
    }

    private void inquireCustomer(String customerId) throws TimeoutException, KeyboardLockedException, NetworkException,
            FieldNotFoundException, TextNotFoundException, TerminalInterruptedException {
        String defaultId = "0000000000";

        terminal.positionCursorToFieldContaining("Cust Number").tab()
                .type(defaultId.substring(0,defaultId.length()-customerId.length()) + customerId).enter().waitForKeyboard()
                .positionCursorToFieldContaining("Select Option").tab()
                .type("1").enter().waitForKeyboard();
    }


    private String addCustomer() throws TimeoutException, KeyboardLockedException, NetworkException,
            FieldNotFoundException, TextNotFoundException, TerminalInterruptedException {

        terminal.waitForKeyboard()
                .positionCursorToFieldContaining("Select Option").tab()
                .type("2").enter().waitForKeyboard();

        return terminal.retrieveFieldTextAfterFieldWithString("Cust Number");
    }
}