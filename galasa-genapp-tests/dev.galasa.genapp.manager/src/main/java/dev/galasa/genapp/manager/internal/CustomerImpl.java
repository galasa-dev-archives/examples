package dev.galasa.genapp.manager.internal;

import dev.galasa.genapp.manager.ICustomer;

public class CustomerImpl implements ICustomer {

    private int customerNumber;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String houseName;
    private String houseNumber;
    private String postcode;
    private String homePhone;
    private String mobilePhone;
    private String emailAddress;

    private GenAppImpl genAppImpl;

    public CustomerImpl(GenAppImpl genAppImpl, int customerNumber, String firstName, String lastName,
            String dateOfBirth, String houseName, String houseNumber, String postcode,
            String homePhone, String mobilePhone, String emailAddress) {
        this.genAppImpl = genAppImpl;
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
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getFirstName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getLastName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDateOfBirth() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getHouseName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getHouseNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPostCode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getHomePhone() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMobilePhone() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEmailAddress() {
        // TODO Auto-generated method stub
        return null;
    }

    
    private int setCustomerNumber() {
        // TODO Auto-generated method stub
        return 0;
    }

    private String setFirstName() {
        // TODO Auto-generated method stub
        return null;
    }

    private String setLastName() {
        // TODO Auto-generated method stub
        return null;
    }

    private String setDateOfBirth() {
        // TODO Auto-generated method stub
        return null;
    }

    private String setHouseName() {
        // TODO Auto-generated method stub
        return null;
    }

    private String setHouseNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    private String setPostCode() {
        // TODO Auto-generated method stub
        return null;
    }

    private String setHomePhone() {
        // TODO Auto-generated method stub
        return null;
    }

    private String setMobilePhone() {
        // TODO Auto-generated method stub
        return null;
    }

    private String setEmailAddress() {
        // TODO Auto-generated method stub
        return null;
    }

}