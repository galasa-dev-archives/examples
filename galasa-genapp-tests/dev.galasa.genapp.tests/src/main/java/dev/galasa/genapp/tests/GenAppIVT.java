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

    //Logging into the CICS-region with the provided CICS ID
    @Test
    public void login() throws InterruptedException, CoreManagerException, Zos3270Exception {
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

        assertThat(terminal.retrieveScreen()).containsOnlyOnce("Sign-on is complete");
    }
    
    @Test
    public void customerGenApp() throws InterruptedException, CoreManagerException, Zos3270Exception {
        //Open the GenApp customer application
        terminal.type("ssc1").enter().waitForKeyboard();

        //Assert that the application menu is showing
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("General Insurance Customer Menu");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("1. Cust Inquiry       Cust Number");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("2. Cust Add           Cust Name :First");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("4. Cust Update        DOB");

        terminal.pf3().waitForKeyboard();
    }

    @Test
    public void motorPolicyGenApp() throws InterruptedException, CoreManagerException, Zos3270Exception {
        //Open the GenApp Motor Policy application
        terminal.type("ssp1").enter().waitForKeyboard();

        //Assert that the application menu is showing
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("General Insurance Motor Policy Menu");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("1. Policy Inquiry     Policy Number");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("2. Policy Add         Cust Number");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("Car Make");

        terminal.pf3().waitForKeyboard();
    }

    @Test
    public void endowmentPolicyGenApp() throws InterruptedException, CoreManagerException, Zos3270Exception {
        //Open the GenApp Emdowment Policy application
        terminal.type("ssp2").enter().waitForKeyboard();

        //Assert that the application menu is showing
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("General Insurance Endowment Policy Menu");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("1. Policy Inquiry     Policy Number");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("2. Policy Add         Cust Number");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("Fund Name");

        terminal.pf3().waitForKeyboard();
    }

    @Test
    public void housePolicyGenApp() throws InterruptedException, CoreManagerException, Zos3270Exception {
        //Open the GenApp House Policy application
        terminal.type("ssp3").enter().waitForKeyboard();

        //Assert that the application menu is showing
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("General Insurance House Policy Menu");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("1. Policy Inquiry     Policy Number");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("2. Policy Add         Cust Number");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("3. Policy Delete");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("4. Policy Update");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("Property Type");

        terminal.pf3().waitForKeyboard();
    }

    @Test
    public void commercialPolicyGenApp() throws InterruptedException, CoreManagerException, Zos3270Exception {
        //Open the GenApp Commercial Policy application
        terminal.type("ssp4").enter().waitForKeyboard();

        //Assert that the application menu is showing
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("General Insurance Commercial Policy Menu");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("1. Policy Inquiry     Policy Number");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("2. Policy Add         Cust Name");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("3. Policy Delete      Cust Name");
        assertThat(terminal.retrieveScreen()).containsOnlyOnce("Status");

        terminal.pf3().waitForKeyboard();
    }

    
}