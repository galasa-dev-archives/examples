/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2020.
 */
package dev.galasa.genapp.manager;

public interface IMotorPolicy {

    public int getPolicyNumber();

    public int getCustomerNumber();

    public String getCarMake();

    public String getCarModel();

    public int getCarValue();

    public String getCarRegistration();

}