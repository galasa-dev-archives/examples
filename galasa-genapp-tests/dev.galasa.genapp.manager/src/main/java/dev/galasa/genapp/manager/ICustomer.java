/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2020.
 */
package dev.galasa.genapp.manager;

public interface ICustomer {

    public int getCustomerNumber();

    public String getFirstName();

    public String getLastName();

    public String getDateOfBirth();

    public String getHouseName();

    public String getHouseNumber();

    public String getPostCode();

    public String getHomePhone();

    public String getMobilePhone();

    public String getEmailAddress();

    public void updateCustomerNumber(int customerNumber);

    public void updateFirstName(String firstName);

    public void updateLastName(String lastName);

    public void updateDateOfBirth(String dateOfBirth);

    public void updateHouseName(String houseName);

    public void updateHouseNumber(String houseNumber);

    public void updatePostCode(String postCode);

    public void updateHomePhone(String homePhone);

    public void updateMobilePhone(String mobilePhone);

    public void updateEmailAddress(String emailAddress);

}