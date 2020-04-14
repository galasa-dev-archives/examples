/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2020.
 */
package dev.galasa.genapp.manager;

public interface IEndowmentPolicy {

    public int getPolicyNumber();

    public int getCustomerNumber();

    public String getFundName();

    public String getLifeAssured();

    public int getWithProfits();

    public String getEquities();

    public String getManagedFunds();

}