package dev.galasa.genapp.tests;

import static org.assertj.core.api.Assertions.assertThat;

import dev.galasa.Test;
import dev.galasa.genapp.manager.Customer;
import dev.galasa.genapp.manager.GenApp;
import dev.galasa.genapp.manager.ICustomer;
import dev.galasa.genapp.manager.IGenApp;

@Test
public class ProvisionedTest {

    @GenApp
    public IGenApp genapp;

    @Customer()
    public ICustomer customer;

    @Test
    public void ProsivionedAccountTest() {
        
    }
}