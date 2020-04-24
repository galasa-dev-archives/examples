package dev.galasa.genapp.manager.internal;

import dev.galasa.genapp.manager.ICustomer;
import dev.galasa.genapp.manager.IHousePolicy;

public class HousePolicyImpl implements IHousePolicy{
    
    private ICustomer owner;
    private final int policyNumber;
    private String propertyType;
    private int bedRooms;
    private int houseValue;
    private String houseName;
    private String houseNumber;
    private String postcode;


    public HousePolicyImpl(ICustomer customer, int policyNumber, String propertyType, int bedRooms, int houseValue, String houseName, String houseNumber, String postcode) {
        this.owner = customer;
        this.policyNumber = policyNumber;
        this.propertyType = propertyType;
        this.bedRooms = bedRooms;
        this.houseValue = houseValue;
        this.houseName = houseName;
        this.houseNumber = houseNumber;
        this.postcode = postcode;
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
    public String getPropertyType() {
        return this.propertyType;
    }

    @Override
    public int getBedrooms() {
        return this.bedRooms;
    }

    @Override
    public int getHouseValue() {
        return this.houseValue;
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
    public String getPostcode() {
        return this.postcode;
    }

}