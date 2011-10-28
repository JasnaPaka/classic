package org.jboss.seam.classic.test.init.scan;

import static org.jboss.seam.classic.test.utils.Archives.createSeamJar;
import static org.jboss.seam.classic.test.utils.Archives.createSeamWebApp;
import static org.jboss.seam.classic.test.utils.Dependencies.SEAM_SOLDER;
import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.classic.test.init.scan.subpackage.AlphaJet;
import org.jboss.seam.classic.test.utils.Dependencies;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class EarTest extends WarTest {

    @Deployment
    public static Archive<?> getDeployment() {

        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "test.ear");

        ear.addAsLibrary(createSeamJar("alpha.jar", Alpha.class, EarTest.class, WarTest.class).addPackage(
                AlphaJet.class.getPackage())); // EAR lib
        ear.addAsModule(createSeamJar("bravo.jar", Bravo.class, Foo.class, Bar.class)); // EAR module
        ear.addAsLibraries(SEAM_SOLDER).addAsLibraries(Dependencies.REFLECTIONS);
        ear.addAsLibrary(createSeamClassic());
        ear.setApplicationXML("org/jboss/seam/classic/test/init/scan/application.xml");
        ear.addAsManifestResource("META-INF/jboss-deployment-structure.xml", "jboss-deployment-structure.xml");

        WebArchive war = createSeamWebApp("test.war", false, false, Alpha.class);
        ear.addAsModule(war);
        return ear;
    }

    @Before
    public void scan() {
        super.scan();
    }

    @Test
    public void testAnnotationScanning() {
        Set<Class<?>> classes = getScanner().getTypesAnnotatedWith(Name.class);
        assertEquals(2, classes.size());
    }
}
