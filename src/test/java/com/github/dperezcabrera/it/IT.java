package com.github.dperezcabrera.it;

import com.github.dockerjava.api.command.CreateContainerCmd;
import java.net.URL;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

@Tag("integration")
public class IT {

    static GenericContainer springApp;
    static GenericContainer cloudConfigServer;
    static GenericContainer postgres;
    static GenericContainer chrome;
    static final int CHROME_PORT = 4444;
    static final int CONFIG_PORT = 8888;
    static final int WEB_PORT = 8080;
    static final int BBDD_PORT = 5432;
    static final String CONFIG_HOSTNAME = "test-config";
    static final String WEB_HOSTNAME = "test-web";
    static final String BBDD_HOSTNAME = "test-postgres";

    @BeforeAll
    static void setup() throws InterruptedException {
        Network network = Network.newNetwork();
        
        chrome = new GenericContainer("selenium/standalone-chrome:3.141.59-xenon")
                .withNetwork(network)
                .withFileSystemBind("/dev/shm", "/dev/shm", BindMode.READ_WRITE)
                .waitingFor(Wait.forLogMessage(".*Selenium Server is up and running on port.*", 1));
        
        chrome.start();
        
        Consumer<CreateContainerCmd> cConfig = c -> c.withName(CONFIG_HOSTNAME);
        cloudConfigServer = new GenericContainer("dperezcabrera/spring-cloud-config-server:latest")
                .withCreateContainerCmdModifier(cConfig)
                .withNetwork(network)
                .withEnv("SPRING_PROFILES_ACTIVE", "native")
                .withEnv("ENCRYPT_KEY", "password")
                .withFileSystemBind("src/test/resources/config", "/config", BindMode.READ_ONLY)
                .waitingFor(Wait.forLogMessage(".*Started ConfigServerApplication.*", 1));

        cloudConfigServer.start();

        Consumer<CreateContainerCmd> cPostgres = c -> c.withName(BBDD_HOSTNAME);
        postgres = new GenericContainer("postgres:12-alpine")
                .withCreateContainerCmdModifier(cPostgres)
                .withNetwork(network)
                .withEnv("POSTGRES_DB", "test")
                .withEnv("POSTGRES_USER", "postgres")
                .withEnv("POSTGRES_PASSWORD", "password")
                .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*", 1));

        postgres.start();

        Consumer<CreateContainerCmd> cWeb = c -> c.withName(WEB_HOSTNAME);
        springApp = new GenericContainer("openjdk:8-jre-alpine")
                .withCreateContainerCmdModifier(cWeb)
                .withNetwork(network)
                .withFileSystemBind("target/test-1.0.0-SNAPSHOT.jar", "/app/spring-boot-app.jar", BindMode.READ_ONLY)
                .withEnv("ENCRYPT_KEY", "password")
                .withEnv("SPRING_PROFILES_ACTIVE", "production,develop")
                .withCommand("java", "-jar", "/app/spring-boot-app.jar", "--spring.application.name=test", "--spring.cloud.config.uri=http://"+CONFIG_HOSTNAME+":"+CONFIG_PORT)
                .waitingFor(Wait.forLogMessage(".*Started App in.*", 1));

        springApp.start();
    }

    @AfterAll
    static void finish() {
        chrome.stop();
        springApp.stop();
        postgres.stop();
        cloudConfigServer.stop();
    }

    @Test
    public void testChrome() throws Exception {
        RemoteWebDriver driver = new RemoteWebDriver(new URL("http://localhost:"+chrome.getMappedPort(CHROME_PORT)+"/wd/hub"), new ChromeOptions());
        driver.get("http://"+WEB_HOSTNAME+":"+WEB_PORT+"/");
        assertTrue(driver.getTitle().contains("App Test"));
        //Thread.sleep(10000);
    }
}
