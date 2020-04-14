package dev.galasa.genapp.tests;

import static org.assertj.core.api.Assertions.assertThat;

import dev.galasa.ICredentialsUsernamePassword;
import dev.galasa.Test;
import dev.galasa.core.manager.CoreManager;
import dev.galasa.core.manager.CoreManagerException;
import dev.galasa.core.manager.ICoreManager;
import dev.galasa.zos.IZosImage;
import dev.galasa.zos.ZosImage;
import dev.galasa.zos3270.ITerminal;
import dev.galasa.zos3270.Zos3270Exception;
import dev.galasa.zos3270.Zos3270Terminal;

@Test
public class GenAppIVT {

    @CoreManager
    public ICoreManager coreManager;

    @ZosImage(imageTag = "GENAPP")
    public IZosImage image;

    @Zos3270Terminal(imageTag = "GENAPP")
    public ITerminal terminal;

    final String APPLID = "CICPY01D";

    @Test
    public void customerGenAppTesting() throws InterruptedException, CoreManagerException, Zos3270Exception {
        //Logging into the CICS-region of GenApp CB12
        login();
        //Open the GenApp application
        terminal.type("ssc1").enter().waitForKeyboard();

        //Assert that the application menu is showing
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("General Insurance Customer Menu");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("1. Cust Inquiry       Cust Number");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("2. Cust Add           Cust Name :First");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("4. Cust Update        DOB");
    }

    //Logging into the CICS-region with the provided CICS ID
    private void login() throws InterruptedException, CoreManagerException, Zos3270Exception {
        //Logon to the CICS Region
        terminal.waitForKeyboard().type("logon applid(" + APPLID + ")")
                .enter().waitForTextInField("Userid")
                .pf3().waitForTextInField("Sign-on is terminated").clear().waitForKeyboard()
                .type("cesl").enter().waitForTextInField("Userid");

        //Retrieve credentials from the Credentials Store and register password as confidential
        ICredentialsUsernamePassword creds = (ICredentialsUsernamePassword) coreManager.getCredentials("GENAPP");
        coreManager.registerConfidentialText(creds.getPassword(), creds.getUsername() + " password");

        //Use the credentials to log in
        terminal.positionCursorToFieldContaining("Userid").tab()
                .type(creds.getUsername())
                .positionCursorToFieldContaining("Password").tab()
                .type(creds.getPassword())
                .enter().waitForKeyboard()
                .clear().waitForKeyboard();
    }
}