package dev.galasa.genapp.tests;

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
public class GenAppTest {

    @CoreManager
    public ICoreManager coreManager;

    @ZosImage(imageTag = "GENAPP")
    public IZosImage image;

    @Zos3270Terminal(imageTag = "GENAPP")
    public ITerminal terminal;

    final String APPLID = "CICPY01D";

    @Test
    public void login() throws InterruptedException, CoreManagerException, Zos3270Exception {
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

                .type("ssc1").enter().waitForKeyboard()
                .positionCursorToFieldContaining("Cust Number").tab()
                .type("0000000013")
                .positionCursorToFieldContaining("Select Option").tab()
                .type("1").enter().waitForKeyboard();
    }
}