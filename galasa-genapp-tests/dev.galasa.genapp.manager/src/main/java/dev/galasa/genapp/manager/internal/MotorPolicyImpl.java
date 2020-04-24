package dev.galasa.genapp.manager.internal;

import dev.galasa.genapp.manager.ICustomer;
import dev.galasa.genapp.manager.IMotorPolicy;

public class MotorPolicyImpl implements IMotorPolicy {

    private ICustomer owner;
    private final int policyNumber;
    private String carMake;
    private String carModel;
    private int carValue;
    private String carRegistration;

    public MotorPolicyImpl(ICustomer customer, int policyNumber, String carMake, String carModel, int carValue, String carRegistration) {
        this.owner = customer;
        this.policyNumber = policyNumber;
        this.carMake = carMake;
        this.carModel = carModel;
        this.carValue = carValue;
        this.carRegistration = carRegistration;
    }

    @Override
    public int getPolicyNumber() {
        return this.policyNumber;
    }

    @Override
    public int getCustomerNumber() {
        return this.owner.getCustomerNumber();
    }

    @Override
    public String getCarMake() {
        return this.carMake;
    }

    @Override
    public String getCarModel() {
        return this.carModel;
    }

    @Override
    public int getCarValue() {
        return this.carValue;
    }

    @Override
    public String getCarRegistration() {
        return this.carRegistration;
    }

}