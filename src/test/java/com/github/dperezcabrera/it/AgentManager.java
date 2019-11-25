package com.github.dperezcabrera.it;

import com.github.dockerjava.api.command.CreateContainerCmd;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AgentManager {

    private static final int CHROME_PORT = 4444;
    private static final int CONFIG_PORT = 8888;
    private static final int WEB_PORT = 8080;
    private static final int BBDD_PORT = 5432;
    private static final String CONFIG_HOSTNAME = "test-config";
    private static final String WEB_HOSTNAME = "test-web";
    private static final String BBDD_HOSTNAME = "test-postgres";

    private Network network;
    private GenericContainer springApp;
    private GenericContainer cloudConfigServer;
    private GenericContainer postgres;
    private List<GenericContainer> containers = new ArrayList<>();
    private List<RemoteWebDriver> browsers = new ArrayList<>();

    public static AgentManager create() {
        return new AgentManager();
    }

    public AgentManager deployConfigServer() {
        configServer().start();
        return this;
    }
    
    public AgentManager deployPostgres() {
        postgres().start();
        return this;
    }
    
    public AgentManager deployTestWeb() {
        testWeb().start();
        return this;
    }
    
    public Network network() {
        if (network == null){
            network = Network.newNetwork();
        }
        return network;
    }

    public GenericContainer testWeb() {
        if (springApp == null) {
            Consumer<CreateContainerCmd> cWeb = c -> c.withName(WEB_HOSTNAME);
            springApp = new GenericContainer("openjdk:8-jre-alpine")
                    .withCreateContainerCmdModifier(cWeb)
                    .withNetwork(network())
                    .withFileSystemBind("target/test-1.0.0-SNAPSHOT.jar", "/app/spring-boot-app.jar", BindMode.READ_ONLY)
                    .withEnv("ENCRYPT_KEY", "password")
                    .withEnv("SPRING_PROFILES_ACTIVE", "production,develop")
                    .withCommand("java", "-jar", "/app/spring-boot-app.jar", "--spring.application.name=test", "--spring.cloud.config.uri=http://" + CONFIG_HOSTNAME + ":" + CONFIG_PORT)
                    .waitingFor(Wait.forLogMessage(".*Started App in.*", 1));
        }
        return springApp;
    }

    public GenericContainer postgres() {
        if (postgres == null) {
            Consumer<CreateContainerCmd> cPostgres = c -> c.withName(BBDD_HOSTNAME);
            postgres = new GenericContainer("postgres:12-alpine")
                    .withCreateContainerCmdModifier(cPostgres)
                    .withNetwork(network())
                    .withEnv("POSTGRES_DB", "test")
                    .withEnv("POSTGRES_USER", "postgres")
                    .withEnv("POSTGRES_PASSWORD", "password")
                    .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*", 1));
        }
        return postgres;
    }

    public GenericContainer configServer() {
        if (cloudConfigServer == null) {
            Consumer<CreateContainerCmd> cConfig = c -> c.withName(CONFIG_HOSTNAME);
            cloudConfigServer = new GenericContainer("dperezcabrera/spring-cloud-config-server:latest")
                    .withCreateContainerCmdModifier(cConfig)
                    .withNetwork(network())
                    .withEnv("SPRING_PROFILES_ACTIVE", "native")
                    .withEnv("ENCRYPT_KEY", "password")
                    .withFileSystemBind("src/test/resources/config", "/config", BindMode.READ_ONLY)
                    .waitingFor(Wait.forLogMessage(".*Started ConfigServerApplication.*", 1));
        }
        return cloudConfigServer;
    }

    public Agent newChromeAgent() {
        try {
            GenericContainer container = new GenericContainer("selenium/standalone-chrome:3.141.59-xenon")
                    .withNetwork(network())
                    .withFileSystemBind("/dev/shm", "/dev/shm", BindMode.READ_WRITE)
                    .waitingFor(Wait.forLogMessage(".*Selenium Server is up and running on port.*", 1));

            container.start();

            RemoteWebDriver driver = new RemoteWebDriver(new URL("http://localhost:" + container.getMappedPort(CHROME_PORT) + "/wd/hub"), new ChromeOptions());
            browsers.add(driver);
            containers.add(container);
            return new Agent(driver);
        } catch (MalformedURLException e) {
            throw new RuntimeException("No se pudo inicializar el chrome", e);
        }
    }

    public Agent newFirefoxAgent() {
        try {
            GenericContainer container = new GenericContainer("selenium/standalone-firefox:3.141.59-xenon")
                    .withNetwork(network())
                    .withFileSystemBind("/dev/shm", "/dev/shm", BindMode.READ_WRITE)
                    .waitingFor(Wait.forLogMessage(".*Selenium Server is up and running on port.*", 1));

            container.start();

            RemoteWebDriver driver = new RemoteWebDriver(new URL("http://localhost:" + container.getMappedPort(CHROME_PORT) + "/wd/hub"), new FirefoxOptions());
            browsers.add(driver);
            containers.add(container);
            return new Agent(driver);
        } catch (MalformedURLException e) {
            throw new RuntimeException("No se pudo inicializar el chrome", e);
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Agent {

        private RemoteWebDriver webdriver;

        public Agent goToWebTest() {
            webdriver.get("http://" + WEB_HOSTNAME + ":" + WEB_PORT + "/");
            return this;
        }

        public Agent checkTitleContains(String title) {
            assertTrue(webdriver.getTitle().contains(title));
            return this;
        }
    }
}
