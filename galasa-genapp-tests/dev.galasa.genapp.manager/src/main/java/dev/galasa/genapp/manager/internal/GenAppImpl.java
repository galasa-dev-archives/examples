package dev.galasa.genapp.manager.internal;

import dev.galasa.ICredentialsUsernamePassword;
import dev.galasa.framework.spi.IConfidentialTextService;
import dev.galasa.framework.spi.IFramework;
import dev.galasa.genapp.manager.GenAppManagerException;
import dev.galasa.genapp.manager.ICustomer;
import dev.galasa.genapp.manager.IGenApp;
import dev.galasa.zos.IZosImage;
import dev.galasa.zos.ZosManagerException;
import dev.galasa.zos3270.ITerminal;
import dev.galasa.zos3270.Zos3270Exception;

public class GenAppImpl implements IGenApp {

    private ITerminal terminal;
    private String applID;
    private int webnet;
    private String baseAddress;
    private ICredentialsUsernamePassword creds;

    private final String PREFIX = "GENAPP";

    private final String getCustomer = "getCustomerDetails";
    private final String addCustomer = "addCustomerDetails";
    private final String getCommercial = "getCommercialPolicyDetails";
    private final String addCommercial = "addCommercialPolicyDetails";
    private final String getEndowment = "getEndowmentPolicyDetails";
    private final String addEndowment = "addEndowmentPolicyDetails";
    private final String getHouse = "getHousePolicyDetails";
    private final String addHouse = "addHousePolicyDetails";
    private final String getMotor = "getMotorPolicyDetails";
    private final String addMotor = "addMotorPolicyDetails";

    public GenAppImpl(ITerminal terminal3270, String applid, int webnetPort, IZosImage image, IFramework framework)
            throws GenAppManagerException {
        try {
            this.applID = applid;
            this.webnet = webnetPort;
            this.terminal = terminal3270;
            this.baseAddress = image.getIpHost().getIpv4Hostname() + ":" + this.webnet;
            this.creds = (ICredentialsUsernamePassword) image.getDefaultCredentials();
            IConfidentialTextService cts = framework.getConfidentialTextService();
            cts.registerText(creds.getUsername(), "GenApp username");
            cts.registerText(creds.getPassword(), "GenApp password");
        } catch (ZosManagerException e) {
            throw new GenAppManagerException("Issue generating GenApp Instance", e);
        }

        logon();
    }

    @Override
    public String getApplId() {
        return this.applID;
    }

    @Override
    public String getAddress() {
        return this.baseAddress;
    }

    @Override
    public String getAddCustomerPath() {
        return this.PREFIX + "/" + this.addCustomer;
    }

    @Override
    public String getInquireCustomerPath() {
        return this.PREFIX + "/" + this.getCustomer;
    }

    @Override
    public String getAddMotorPolicyPath() {
        return this.PREFIX + "/" + this.addMotor;
    }

    @Override
    public String getInquireMotorPolicyPath() {
        return this.PREFIX + "/" + this.getMotor;
    }

    @Override
    public String getAddEndowmentPolicyPath() {
        return this.PREFIX + "/" + this.addEndowment;
    }

    @Override
    public String getInquireEndowmentPolicyPath() {
        return this.PREFIX + "/" + this.getEndowment;
    }

    @Override
    public String getAddHousePolicyPath() {
        return this.PREFIX + "/" + this.addHouse;
    }

    @Override
    public String getInquireHousePolicyPath() {
        return this.PREFIX + "/" + this.getHouse;
    }

    @Override
    public String getAddCommericalPolicyPath() {
        return this.PREFIX + "/" + this.addCommercial;
    }

    @Override
    public String getInquireCommericalPolicyPath() {
        return this.PREFIX + "/" + this.getCommercial;
    }

    public ICustomer inquireCustomer(int id) throws GenAppManagerException {
        String defaultId = "0000000000";
        String customerId = Integer.toString(id);

        try {
        terminal.waitForKeyboard()
                .type("ssc1").enter().waitForKeyboard()
                .positionCursorToFieldContaining("Cust Number").tab()
                .type(defaultId.substring(0,defaultId.length()-customerId.length()) + customerId).enter().waitForKeyboard()
                .positionCursorToFieldContaining("Select Option").tab()
                .type("1").enter().waitForKeyboard();

        if(terminal.retrieveScreen().contains("No data was returned."))
            return null;

        String firstName = terminal.retrieveFieldTextAfterFieldWithString("First");
        String lastName = terminal.retrieveFieldTextAfterFieldWithString("Last");
        String dob = terminal.retrieveFieldTextAfterFieldWithString("DOB");
        String houseName = terminal.retrieveFieldTextAfterFieldWithString("House Name");
        String houseNum = terminal.retrieveFieldTextAfterFieldWithString("House Number");
        String postcode = terminal.retrieveFieldTextAfterFieldWithString("Postcode");
        String homePhone = terminal.retrieveFieldTextAfterFieldWithString("Phone: Home");
        String mobPhone = terminal.retrieveFieldTextAfterFieldWithString("Phone: Mob");
        String emailAddress = terminal.retrieveFieldTextAfterFieldWithString("Email  Addr");

        ICustomer customer = new CustomerImpl(id, firstName, lastName, dob, houseName, 
                                houseNum, postcode, homePhone, mobPhone, emailAddress);

        terminal.pf3().waitForKeyboard().clear().waitForKeyboard();

        return customer;
        } catch(InterruptedException | Zos3270Exception e) {
            throw new GenAppManagerException("Issue Inquiring Customer", e);
        }
    }

    public ICustomer addCustomer() throws GenAppManagerException {
        terminal.waitForKeyboard()
                .type("ssc1").enter().waitForKeyboard()
                .positionCursorToFieldContaining("Select Option").tab()
                .type("2").enter().waitForKeyboard();
    }

    private void logon() throws GenAppManagerException {
        try {
            terminal.waitForKeyboard()
                    .type("logon applid(" + this.applID + ")")
                    .enter().waitForTextInField("Userid")
                    .pf3().waitForTextInField("Sign-on is terminated")
                    .clear().waitForKeyboard()
			        .type("cesl").enter().waitForTextInField("Userid")
			        .positionCursorToFieldContaining("Userid").tab()
			        .type(creds.getUsername())
			        .positionCursorToFieldContaining("Password").tab()
			        .type(creds.getPassword())
			        .enter().waitForKeyboard()
			        .clear().waitForKeyboard();
		} catch (InterruptedException | Zos3270Exception e) {
			throw new GenAppManagerException("Issue logging into GenApp", e);
		}
    }

}