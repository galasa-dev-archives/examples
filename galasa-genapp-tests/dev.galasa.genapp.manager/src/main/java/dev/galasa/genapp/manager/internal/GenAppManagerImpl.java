package dev.galasa.genapp.manager.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.annotations.Component;

import dev.galasa.ManagerException;
import dev.galasa.framework.spi.AbstractManager;
import dev.galasa.framework.spi.AnnotatedField;
import dev.galasa.framework.spi.ConfigurationPropertyStoreException;
import dev.galasa.framework.spi.GenerateAnnotatedField;
import dev.galasa.framework.spi.IConfigurationPropertyStoreService;
import dev.galasa.framework.spi.IFramework;
import dev.galasa.framework.spi.IManager;
import dev.galasa.framework.spi.ResourceUnavailableException;
import dev.galasa.genapp.manager.Account;
import dev.galasa.genapp.manager.CommercialPolicy;
import dev.galasa.genapp.manager.EndowmentPolicy;
import dev.galasa.genapp.manager.GenApp;
import dev.galasa.genapp.manager.GenAppManagerException;
import dev.galasa.genapp.manager.HousePolicy;
import dev.galasa.genapp.manager.IGenApp;
import dev.galasa.genapp.manager.IGenAppManager;
import dev.galasa.genapp.manager.MotorPolicy;
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

@Component(service = { IManager.class })
public class GenAppManagerImpl extends AbstractManager implements IGenAppManager {

    private static final Log logger = LogFactory.getLog(GenAppManagerImpl.class);

    public final static String NAMESPACE = "genapp";
    private IConfigurationPropertyStoreService cps;
    private IFramework framework;

    private IZosManagerSpi zosManager;
    private IZos3270ManagerSpi zos3270Manager;

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
        if (otherManager instanceof IZosManager || otherManager instanceof IZos3270Manager) {
            return true;
        }

        return super.areYouProvisionalDependentOn(otherManager);
    }

    @Override
    public void provisionGenerate() throws ManagerException, ResourceUnavailableException {
        generateAnnotatedFields(GenAppManagerField.class);
    }

    @GenerateAnnotatedField(annotation = GenApp.class)
    public IGenApp generateGenApp(Field field, List<Annotation> annotations) throws GenAppManagerException {
        try {
            String dseInstance = GenAppDseInstance.get();

            String applid = GenAppCicsApplID.get(dseInstance);
            int webnetPort = GenAppWebPort.get(dseInstance);

            String imageId = GenAppZosImage.get(dseInstance);
            IZosImage image = zosManager.getUnmanagedImage(imageId);

            String host = image.getIpHost().getIpv4Hostname();
            int port = image.getIpHost().getTelnetPort();
            Boolean tls = image.getIpHost().isTelnetPortTls();

            Zos3270TerminalImpl terminal3270 = new Zos3270TerminalImpl("GenAppTerminal", host, port, tls, framework);
            terminal3270.connect();

            IGenApp genApp = new GenAppImpl(terminal3270, applid, webnetPort, image, framework);
            return genApp;
        } catch (Zos3270ManagerException | InterruptedException | IpNetworkManagerException | ZosManagerException
                | ConfigurationPropertyStoreException | NetworkException e) {
            throw new GenAppManagerException("Issue generating sources for GenApp instance", e);
        }
    }

    @GenerateAnnotatedField(annotation = Account.class)
    public IGenApp generateAccount(Field field, List<Annotation> annotations) {
        return null; //TODO finish this
    }

    @GenerateAnnotatedField(annotation = CommercialPolicy.class)
    public IGenApp generateCommercialPolicy(Field field, List<Annotation> annotations) {
        return null; //TODO finish this
    }

    @GenerateAnnotatedField(annotation = EndowmentPolicy.class)
    public IGenApp generateEndowmentPolicy(Field field, List<Annotation> annotations) {
        return null; //TODO finish this
    }

    @GenerateAnnotatedField(annotation = HousePolicy.class)
    public IGenApp generateHousePolicy(Field field, List<Annotation> annotations) {
        return null; //TODO finish this
    }

    @GenerateAnnotatedField(annotation = MotorPolicy.class)
    public IGenApp generateMotorPolicy(Field field, List<Annotation> annotations) {
        return null; //TODO finish this
    }
    
}