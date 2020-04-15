package dev.galasa.genapp.manager.internal;

import dev.galasa.genapp.manager.GenAppManagerException;
import dev.galasa.genapp.manager.ICustomer;

public class CustomerImpl implements ICustomer {

    private GenAppImpl genApp;

    private final int customerNumber;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String houseName;
    private String houseNumber;
    private String postcode;
    private String homePhone;
    private String mobilePhone;
    private String emailAddress;

    public CustomerImpl(GenAppImpl genapp, int customerNumber, String firstName, String lastName, String dateOfBirth,
            String houseName, String houseNumber, String postcode, String homePhone, String mobilePhone,
            String emailAddress) {
        this.genApp = genapp;
        this.customerNumber = customerNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.houseName = houseName;
        this.houseNumber = houseNumber;
        this.postcode = postcode;
        this.homePhone = homePhone;
        this.mobilePhone = mobilePhone;
        this.emailAddress = emailAddress;
    }

    @Override
    public int getCustomerNumber() {
        return this.customerNumber;
    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public String getDateOfBirth() {
        return this.dateOfBirth;
    }

    @Override
    public String getHouseName() {
        return this.houseName;
    }

    @Override
    public String getHouseNumber() {
        return this.houseNumber;
    }

    @Override
    public String getPostCode() {
        return this.postcode;
    }

    @Override
    public String getHomePhone() {
        return this.homePhone;
    }

    @Override
    public String getMobilePhone() {
        return this.mobilePhone;
    }

    @Override
    public String getEmailAddress() {
        return this.emailAddress;
    }

    @Override
    public void updateFirstName(String firstName) throws GenAppManagerException {
        this.firstName = firstName;
        genApp.updateCustomer(this, "First",firstName);
    }

    @Override
    public void updateLastName(String lastName) throws GenAppManagerException {
        this.lastName = lastName;
        genApp.updateCustomer(this, "Last",lastName);
    }

    @Override
    public void updateDateOfBirth(String dateOfBirth) throws GenAppManagerException {
        this.dateOfBirth = dateOfBirth;
        genApp.updateCustomer(this, "DOB",dateOfBirth);
    }

    @Override
    public void updateHouseName(String houseName) throws GenAppManagerException {
        this.houseName = houseName;
        genApp.updateCustomer(this, "House Name",houseName);
    }

    @Override
    public void updateHouseNumber(String houseNumber) throws GenAppManagerException {
        this.houseNumber = houseNumber;
        genApp.updateCustomer(this, "House Number",houseNumber);
    }

    @Override
    public void updatePostCode(String postcode) throws GenAppManagerException {
        this.postcode = postcode;
        genApp.updateCustomer(this, "Postcode",postcode);
    }

    @Override
    public void updateHomePhone(String homePhone) throws GenAppManagerException {
        this.homePhone = homePhone;
        genApp.updateCustomer(this, "Phone: Home",homePhone);
    }

    @Override
    public void updateMobilePhone(String mobilePhone) throws GenAppManagerException {
        this.mobilePhone = mobilePhone;
        genApp.updateCustomer(this, "Phone: Mob",mobilePhone);
    }

    @Override
    public void updateEmailAddress(String emailAddress) throws GenAppManagerException {
        this.emailAddress = emailAddress;
        genApp.updateCustomer(this, "Email  Addr",emailAddress);
    }

}