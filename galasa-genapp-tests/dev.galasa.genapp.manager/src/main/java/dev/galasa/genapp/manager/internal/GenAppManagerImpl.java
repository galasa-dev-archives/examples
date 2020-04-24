package dev.galasa.genapp.manager.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.annotations.Component;

import dev.galasa.ManagerException;
import dev.galasa.docker.IDockerManager;
import dev.galasa.framework.spi.AbstractManager;
import dev.galasa.framework.spi.AnnotatedField;
import dev.galasa.framework.spi.ConfigurationPropertyStoreException;
import dev.galasa.framework.spi.GenerateAnnotatedField;
import dev.galasa.framework.spi.IConfigurationPropertyStoreService;
import dev.galasa.framework.spi.IFramework;
import dev.galasa.framework.spi.IManager;
import dev.galasa.framework.spi.ResourceUnavailableException;
import dev.galasa.genapp.manager.BasicGenApp;
import dev.galasa.genapp.manager.Customer;
import dev.galasa.genapp.manager.GenApp;
import dev.galasa.genapp.manager.GenAppManagerException;
import dev.galasa.genapp.manager.IBasicGenApp;
import dev.galasa.genapp.manager.ICustomer;
import dev.galasa.genapp.manager.IGenApp;
import dev.galasa.genapp.manager.IGenAppManager;
import dev.galasa.genapp.manager.internal.properties.GenAppCicsApplID;
import dev.galasa.genapp.manager.internal.properties.GenAppDseInstance;
import dev.galasa.genapp.manager.internal.properties.GenAppPropertiesSingleton;
import dev.galasa.genapp.manager.internal.properties.GenAppWebPort;
import dev.galasa.genapp.manager.internal.properties.GenAppZosImage;
import dev.galasa.ipnetwork.IpNetworkManagerException;
import dev.galasa.zos.IZosImage;
import dev.galasa.zos.IZosManager;
import dev.galasa.zos.ZosManagerException;
import dev.galasa.zos.spi.IZosManagerSpi;
import dev.galasa.zos3270.IZos3270Manager;
import dev.galasa.zos3270.Zos3270ManagerException;
import dev.galasa.zos3270.spi.IZos3270ManagerSpi;
import dev.galasa.zos3270.spi.NetworkException;
import dev.galasa.zos3270.spi.Zos3270TerminalImpl;

/**
 * General application manager implementation.
 * 
 * Extracting from the test class the 6 current annotations
 * of @GenApp, @Customer, @CommercialPolicy, @EndowmentPolicy, @HousePolicy
 * and @MotorPolicy
 * 
 * @GenApp - provides the actual environment that allows interaction with GenApp
 * @Customer - provides an Customer that can actually be used through an
 *           existing ID or a newly created customer
 * @CommercialPolicy - provides a CommercialPolicy in Genapp that can actually
 *                   be used through an existing PolicyID or a newly created
 *                   CommercialPolicy
 * @EndowmentPolicy - provides a EndowmentPolicy in Genapp that can actually be
 *                  used through an existing PolicyID or a newly created
 *                  EndowmentPolicy
 * @HousePolicy - provides a HousePolicy in Genapp that can actually be used
 *              through an existing PolicyID or a newly created CommercialPolicy
 * @MotorPolicy - provides a MotorPolicy in Genapp that can actually be used
 *              through an existing PolicyID or a newly created MotorPolicy
 */
@Component(service = { IManager.class })
public class GenAppManagerImpl extends AbstractManager implements IGenAppManager {

    private static final Log logger = LogFactory.getLog(GenAppManagerImpl.class);

    public final static String NAMESPACE = "genapp";
    private IConfigurationPropertyStoreService cps;
    private IFramework framework;

    private IZosManagerSpi zosManager;
    private IZos3270ManagerSpi zos3270Manager;

    private GenAppImpl genapp;

    @Override
    public void initialise(@NotNull IFramework framework, @NotNull List<IManager> allManagers,
            @NotNull List<IManager> activeManagers, @NotNull Class<?> testClass) throws ManagerException {
        super.initialise(framework, allManagers, activeManagers, testClass);
        List<AnnotatedField> ourFields = findAnnotatedFields(GenAppManagerField.class);
        if (!ourFields.isEmpty()) {
            youAreRequired(allManagers, activeManagers);
        }

        try {
            this.framework = framework;
            this.cps = framework.getConfigurationPropertyService(NAMESPACE);
            GenAppPropertiesSingleton.setCps(cps);
        } catch (Exception e) {
            throw new GenAppManagerException("Unable to request framework services", e);
        }
    }

    @Override
    public void youAreRequired(@NotNull List<IManager> allManagers, @NotNull List<IManager> activeManagers)
            throws ManagerException {
        if (activeManagers.contains(this)) {
            return;
        }

        activeManagers.add(this);

        zosManager = addDependentManager(allManagers, activeManagers, IZosManagerSpi.class);
        zos3270Manager = addDependentManager(allManagers, activeManagers, IZos3270ManagerSpi.class);
    }

    @Override
    public boolean areYouProvisionalDependentOn(@NotNull IManager otherManager) {
        if (otherManager instanceof IZosManager || otherManager instanceof IZos3270Manager
                || otherManager instanceof IDockerManager) {
            return true;
        }

        return super.areYouProvisionalDependentOn(otherManager);
    }

    @Override
    public void provisionGenerate() throws ManagerException, ResourceUnavailableException {
        List<AnnotatedField> foundAnnotatedFields = findAnnotatedFields(GenAppManagerField.class);
        for (AnnotatedField annotatedField : foundAnnotatedFields) {
            Field field = annotatedField.getField();
            List<Annotation> annotations = annotatedField.getAnnotations();

            if (field.getType() == IGenApp.class) {
                GenApp annotation = field.getAnnotation(GenApp.class);
                if (annotation != null) {
                    IGenApp genapp = generateGenApp(field, annotations);
                    registerAnnotatedField(field, genapp);
                }
            }
        }

        generateAnnotatedFields(GenAppManagerField.class);
    }

    @Override
    public void startOfTestClass() throws GenAppManagerException {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new GenAppManagerException("Issue waiting in start of test method");
        }
    }

    public IGenApp generateGenApp(Field field, List<Annotation> annotations) throws GenAppManagerException {
        try {
            String dseInstance = GenAppDseInstance.get();

            String applid = GenAppCicsApplID.get(dseInstance);
            int port = GenAppWebPort.get(dseInstance);

            String imageId = GenAppZosImage.get(dseInstance);
            IZosImage image = zosManager.getUnmanagedImage(imageId);

            String host = image.getIpHost().getIpv4Hostname();
            int telnetPort = image.getIpHost().getTelnetPort();
            Boolean tls = image.getIpHost().isTelnetPortTls();

            Zos3270TerminalImpl terminal3270 = new Zos3270TerminalImpl("GenAppTerminal", host, telnetPort, tls, framework, false);
            terminal3270.connect();

            GenAppImpl genApp = new GenAppImpl(terminal3270, applid, port, image, framework);
            this.genapp = genApp;
            return genApp;
        } catch (Zos3270ManagerException | InterruptedException | IpNetworkManagerException | ZosManagerException
                | ConfigurationPropertyStoreException | NetworkException e) {
            throw new GenAppManagerException("Issue generating sources for GenApp instance", e);
        }
    }

    @GenerateAnnotatedField(annotation = BasicGenApp.class)
    public IBasicGenApp generateBasicGenApp(Field field, List<Annotation> annotations) throws GenAppManagerException {
        String dseInstance;
        try {
            dseInstance = GenAppDseInstance.get();
            String applid = GenAppCicsApplID.get(dseInstance);

            int port = GenAppWebPort.get(dseInstance);

            String imageId = GenAppZosImage.get(dseInstance);
            IZosImage image = zosManager.getUnmanagedImage(imageId);

            String host = image.getIpHost().getIpv4Hostname();

            return new BasicGenAppimpl(applid, host, port);
        } catch (ConfigurationPropertyStoreException | GenAppManagerException | ZosManagerException e) {
            throw new GenAppManagerException("Issue creating Basic GenApp Instance", e);
        }
    }

    @GenerateAnnotatedField(annotation = Customer.class)
    public ICustomer generateCustomer(Field field, List<Annotation> annotations) throws GenAppManagerException {
        if(this.genapp == null)
            generateGenApp(null, null);
        Customer custAnnotation = field.getAnnotation(Customer.class);
        int customerId = custAnnotation.userID();
        if(customerId != 0) {
            ICustomer customer = this.genapp.inquireCustomer(customerId);
            if(customer == null)
                throw new GenAppManagerException("Customer with id " + customerId + " does not exist");
            return customer;
        } else {
            return this.genapp.addCustomer();
        }
    }
    
}