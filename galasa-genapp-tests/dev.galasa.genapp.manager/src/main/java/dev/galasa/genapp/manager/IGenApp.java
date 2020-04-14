/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2020.
 */
package dev.galasa.genapp.manager;

public interface IGenApp {

    public String getApplId();

    public String getAddress();

    public String getAddCustomerPath();

    public String getInquireCustomerPath();

    public String getAddMotorPolicyPath();

    public String getInquireMotorPolicyPath();

    public String getAddEndowmentPolicyPath();

    public String getInquireEndowmentPolicyPath();

    public String getAddHousePolicyPath();

    public String getInquireHousePolicyPath();

    public String getAddCommericalPolicyPath();

    public String getInquireCommericalPolicyPath();

}