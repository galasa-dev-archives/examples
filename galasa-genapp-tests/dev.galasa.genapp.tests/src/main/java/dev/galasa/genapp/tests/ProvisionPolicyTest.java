package dev.galasa.genapp.tests;

import dev.galasa.Test;
import dev.galasa.genapp.manager.AreasTested;
import dev.galasa.genapp.manager.Customer;
import dev.galasa.genapp.manager.GenApp;
import dev.galasa.genapp.manager.GenAppManagerException;
import dev.galasa.genapp.manager.ICustomer;
import dev.galasa.genapp.manager.IGenApp;

@Test
@AreasTested(areas = {"customer", "policy"})
public class ProvisionPolicyTest {

    @GenApp
    public IGenApp genApp;

    @Customer
    public ICustomer customer;

    @Test
    public void newCustomerAllPolicies() throws GenAppManagerException {
        customer.createCommercialPolicy("9999", "IBM", "99");
        customer.createEndowmentPolicy("fundName", "lifeAssured", "1", "0", "managedFunds");
        customer.createHousePolicy("propertyType", 10, 10, "123", "asdasd", "12345");
        customer.createMotorPolicy("carMake", "carModel", 100, "asd");
    }
}