/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2020.
 */
package dev.galasa.genapp.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import dev.galasa.ResultArchiveStoreContentType;
import dev.galasa.SetContentType;
import dev.galasa.Test;
import dev.galasa.core.manager.StoredArtifactRoot;
import dev.galasa.genapp.manager.Customer;
import dev.galasa.genapp.manager.GenApp;
import dev.galasa.genapp.manager.GenAppManagerException;
import dev.galasa.genapp.manager.ICustomer;
import dev.galasa.genapp.manager.IGenApp;


@Test
public class ProvisionedNazareTest {

    @GenApp
    public IGenApp genapp;

    @Customer
    public ICustomer customer;

    @StoredArtifactRoot
    public Path artifactRoot;

    @Test
    public void customerManagerUpdate() throws GenAppManagerException, IOException {
        String firstName = genapp.provisionCustomerName();
        String lastName = genapp.provisionCustomerName();

        storeOutput("provisionedNames", "generated.txt", firstName + " " + lastName);

        customer.updateFirstName(firstName);
        customer.updateLastName(lastName);

        ICustomer inquiredCustomer = genapp.inquireCustomer(customer.getCustomerNumber());

        storeOutput("provisionedNames", "inquired.txt", inquiredCustomer.getFirstName() + " " + inquiredCustomer.getLastName());

        assertThat(inquiredCustomer.getFirstName()).isEqualTo(firstName);
        assertThat(inquiredCustomer.getLastName()).isEqualTo(lastName);
    }

    private void storeOutput(String folder, String file, String content) throws IOException {
        Path requestPath = artifactRoot.resolve(folder).resolve(file);
        Files.write(requestPath, content.getBytes(), new SetContentType(ResultArchiveStoreContentType.TEXT),
                StandardOpenOption.CREATE);
    }
}