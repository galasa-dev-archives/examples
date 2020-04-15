/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2020.
 */
package dev.galasa.genapp.manager;

public interface IGenApp {

    /**
     * Refers to the application ID of the CICS-region that has GenApp installed
     */
    public String getApplId();

    /**
     * Refers to the address that the http-client will contact
     */
    public String getAddress();

    public String provisionCustomerName();

    /**
     * Return an ICustomer object of a given ID
     */
    public ICustomer inquireCustomer(int id) throws GenAppManagerException;

    /**
     * The additional path for getAddCustomerPath
     */
    public String getAddCustomerPath();

    /**
     * The additional path for getInquireCustomerPath
     */
    public String getInquireCustomerPath();

    /**
     * The additional path for getAddMotorPolicyPath
     */
    public String getAddMotorPolicyPath();

    /**
     * The additional path for getInquireMotorPolicyPath
     */
    public String getInquireMotorPolicyPath();

    /**
     * The additional path for getAddEndowmentPolicyPath
     */
    public String getAddEndowmentPolicyPath();

    /**
     * The additional path for getInquireEndowmentPolicyPath
     */
    public String getInquireEndowmentPolicyPath();

    /**
     * The additional path for getAddHousePolicyPath
     */
    public String getAddHousePolicyPath();

    /**
     * The additional path for getInquireHousePolicyPath
     */
    public String getInquireHousePolicyPath();

    /**
     * The additional path for getAddCommericalPolicyPath
     */
    public String getAddCommericalPolicyPath();

    /**
     * The additional path for getInquireCommericalPolicyPath
     */
    public String getInquireCommericalPolicyPath();

}