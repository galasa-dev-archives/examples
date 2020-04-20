package dev.galasa.genapp.manager.internal;

import dev.galasa.genapp.manager.ICustomer;
import dev.galasa.genapp.manager.IEndowmentPolicy;

public class EndowmentPolicyImpl implements IEndowmentPolicy {
    
    private ICustomer owner;
    private int policyNum;
    private String fundName;
    private String lifeAssured;
    private String withProfits;
    private String equities;
    private String managedFunds;

    public EndowmentPolicyImpl(ICustomer owner, int policyNum, String fundName, String lifeAssured, String withProfits, String equities, String managedFunds) {
        this.owner = owner;
        this.policyNum = policyNum;
        this.fundName = fundName;
        this.lifeAssured = lifeAssured;
        this.withProfits = withProfits;
        this.equities = equities;
        this.managedFunds = managedFunds;
    }

    @Override
    public int getPolicyNumber() {
        return this.policyNum;
    }

    @Override
    public int getCustomerNumber() {
        return this.owner.getCustomerNumber();
    }

    @Override
    public String getFundName() {
        return this.fundName;
    }

    @Override
    public String getLifeAssured() {
        return this.lifeAssured;
    }

    @Override
    public String getWithProfits() {
        return this.withProfits;
    }

    @Override
    public String getEquities() {
        return this.equities;
    }

    @Override
    public String getManagedFunds() {
        return this.managedFunds;
    }


}