package dev.galasa.genapp.manager.internal;

import dev.galasa.ICredentialsUsernamePassword;
import dev.galasa.framework.spi.IConfidentialTextService;
import dev.galasa.framework.spi.IFramework;
import dev.galasa.genapp.manager.GenAppManagerException;
import dev.galasa.genapp.manager.ICustomer;
import dev.galasa.genapp.manager.IGenApp;
import dev.galasa.zos.IZosImage;
import dev.galasa.zos.ZosManagerException;
import dev.galasa.zos3270.FieldNotFoundException;
import dev.galasa.zos3270.ITerminal;
import dev.galasa.zos3270.KeyboardLockedException;
import dev.galasa.zos3270.TextNotFoundException;
import dev.galasa.zos3270.TimeoutException;
import dev.galasa.zos3270.Zos3270Exception;
import dev.galasa.zos3270.spi.NetworkException;

public class GenAppImpl implements IGenApp {

    private ITerminal terminal;
    private String applID;
    private int webnet;
    private String baseAddress;
    private ICredentialsUsernamePassword creds;

    private final String PREFIX = "GENAPP";

    /**
     * These variables are referring to the JSON-requests that are allowed by the GenApp installation
     */
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
    public int getWebnetPort() {
        return this.webnet;
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

    /**
     * Interacting with GenApp through its 3270-connection to be able to inquire a specific customer through its unique identifier ID
     * @throws GenAppManagerException
     */
    public ICustomer inquireCustomer(int id) throws GenAppManagerException {
        String defaultId = "0000000000";
        String customerId = Integer.toString(id);

        try {
            terminal.waitForKeyboard().type("ssc1").enter().waitForKeyboard()
                    .positionCursorToFieldContaining("Cust Number").tab()
                    .type(defaultId.substring(0, defaultId.length() - customerId.length()) + customerId).enter()
                    .waitForKeyboard().positionCursorToFieldContaining("Select Option").tab().type("1").enter()
                    .waitForKeyboard();

            if (terminal.retrieveScreen().contains("No data was returned."))
                return null;

            String firstName = terminal.retrieveFieldTextAfterFieldWithString("First").trim();
            String lastName = terminal.retrieveFieldTextAfterFieldWithString("Last").trim();
            String dob = terminal.retrieveFieldTextAfterFieldWithString("DOB").trim();
            String houseName = terminal.retrieveFieldTextAfterFieldWithString("House Name").trim();
            String houseNum = terminal.retrieveFieldTextAfterFieldWithString("House Number").trim();
            String postcode = terminal.retrieveFieldTextAfterFieldWithString("Postcode").trim();
            String homePhone = terminal.retrieveFieldTextAfterFieldWithString("Phone: Home").trim();
            String mobPhone = terminal.retrieveFieldTextAfterFieldWithString("Phone: Mob").trim();
            String emailAddress = terminal.retrieveFieldTextAfterFieldWithString("Email  Addr").trim();

            ICustomer customer = new CustomerImpl(this, id, firstName, lastName, dob, houseName, houseNum, postcode,
                    homePhone, mobPhone, emailAddress);

            terminal.pf3().waitForKeyboard().clear().waitForKeyboard();

            return customer;
        } catch (InterruptedException | Zos3270Exception e) {
            throw new GenAppManagerException("Issue Inquiring Customer", e);
        }
    }

    /**
     * Interacting with GenApp through its 3270-connection to be able to add a clean new customer
     * @throws GenAppManagerException
     */
    public ICustomer addCustomer() throws GenAppManagerException {
        try {
            terminal.waitForKeyboard().type("ssc1").enter().waitForKeyboard()
                    .positionCursorToFieldContaining("Select Option").tab().type("2").enter().waitForKeyboard();

            String customerNum = terminal.retrieveFieldTextAfterFieldWithString("Cust Number");

            return new CustomerImpl(this, Integer.parseInt(customerNum), "", "", "", "", "", "", "", "", "");
        } catch (InterruptedException | Zos3270Exception e) {
            throw new GenAppManagerException("Issue Inquiring Customer", e);
        }
    }

    /**
     * Interacting with GenApp through its 3270-connection to update a specific value of the GenApp-data
     * @throws GenAppManagerException
     */
    public ICustomer updateCustomer(ICustomer customer, String field, String value) throws GenAppManagerException {
        try {
            String defaultId = "0000000000";
            String defaultValue = "          ";

            String customerId = Integer.toString(customer.getCustomerNumber());

            terminal.positionCursorToFieldContaining("Cust Number").tab()
                    .type(defaultId.substring(0,defaultId.length()-customerId.length()) + customerId)
                    .positionCursorToFieldContaining("Select Option").tab()
                    .type("4").enter().waitForKeyboard();

            int fieldLength = terminal.retrieveFieldTextAfterFieldWithString(field).length();
            if(value.length() > fieldLength)
                throw new GenAppManagerException("Value " + value + " too long for field " + field + ". Must be " + fieldLength + " or fewer characters");

            String extendedValue = value + defaultValue.substring(value.length(), fieldLength);
            
            terminal.positionCursorToFieldContaining(field).tab()
                    .type(extendedValue).enter().waitForKeyboard();

            terminal.pf3().waitForKeyboard().clear().waitForKeyboard();

            return inquireCustomer(customer.getCustomerNumber());
            
        } catch(InterruptedException | Zos3270Exception e) {
            throw new GenAppManagerException("Issue Adding Customer", e);
        }
    }

    /**
     * A static way to log in to the Application ID that is assigned through the cps.properties
     * @throws GenAppManagerException
     */
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