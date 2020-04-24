package dev.galasa.genapp.manager.internal;

import dev.galasa.genapp.manager.ICommercialPolicy;
import dev.galasa.genapp.manager.ICustomer;

public class CommercialPolicyImpl implements ICommercialPolicy {
    
    private ICustomer owner;
    private int policyNum;
    private String postcode;
    private String customerName;
    private String status;

    public CommercialPolicyImpl(ICustomer owner, int policyNum, String postcode, String customerName, String status) {
        this.owner = owner;
        this.policyNum = policyNum;
        this.postcode = postcode;
        this.customerName = customerName;
        this.status = status;
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
    public String getPostcode() {
        return this.postcode;
    }

    @Override
    public String getCustomerName() {
        return this.customerName;
    }

    @Override
    public String getStatus() {
        return this.status;
    }

}