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

    @Customer(userID = 12)
    public ICustomer existingCustomer;

    @Customer()
    public ICustomer newCustomer;

    @Test
    public void testTest() {
        assertThat(1).isEqualTo(1);
    }
}