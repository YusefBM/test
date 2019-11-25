package com.github.dperezcabrera.it;

import com.github.dockerjava.api.command.CreateContainerCmd;
import static io.github.bonigarcia.seljup.BrowserType.CHROME;
import io.github.bonigarcia.seljup.DockerBrowser;
import io.github.bonigarcia.seljup.SeleniumExtension;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("integration")
@Testcontainers
public class IT {

    @RegisterExtension
    static SeleniumExtension seleniumExtension = new SeleniumExtension();

    static GenericContainer springApp;
    static GenericContainer cloudConfigServer;
    static GenericContainer postgres;

    @BeforeAll
    static void setup() {
        Network network = Network.builder().id("test-network").build();

        Consumer<CreateContainerCmd> cConfig = c -> {
            c.withName("test-config");
            c.withPortSpecs("8888:8888");
        };
        cloudConfigServer = new GenericContainer("dperezcabrera/spring-cloud-config-server:latest")
                .withCreateContainerCmdModifier(cConfig)
                .withEnv("SPRING_PROFILES_ACTIVE", "native")
                .withEnv("ENCRYPT_KEY", "password")
                .withNetwork(network)
                .withExposedPorts(8888)
                .withNetworkAliases("test-config")
                .withFileSystemBind("src/test/resources/config", "/config", BindMode.READ_ONLY)
                .waitingFor(Wait.forLogMessage(".*Started ConfigServerApplication.*", 1));

        cloudConfigServer.start();

        Consumer<CreateContainerCmd> cPostgres = c -> {
                c.withName("test-postgres");
                c.withPortSpecs("5432");
        };
        postgres = new GenericContainer("postgres:12-alpine")
                .withCreateContainerCmdModifier(cPostgres)
                .withEnv("POSTGRES_DB", "test")
                .withEnv("POSTGRES_USER", "postgres")
                .withEnv("POSTGRES_PASSWORD", "password")
                .withNetwork(network)
                .withExposedPorts(5432)
                .withNetworkAliases("test-postgres")
                .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*", 1));

        postgres.start();

        Consumer<CreateContainerCmd> cWeb = c -> {
                c.withName("test-web");
                c.withPortSpecs("8080:8080");
        };
        springApp = new GenericContainer("openjdk:8-jre-alpine")
                .withCreateContainerCmdModifier(cWeb)
                .withNetworkAliases("test-web")
                .withNetwork(network)
                .withExposedPorts(8080)
                .withFileSystemBind("target/test-1.0.0-SNAPSHOT.jar", "/app/spring-boot-app.jar", BindMode.READ_ONLY)
                .withEnv("ENCRYPT_KEY", "password")
                .withEnv("SPRING_PROFILES_ACTIVE", "production,develop")
                .withCommand("java", "-jar", "/app/spring-boot-app.jar", "--spring.application.name=test", "--spring.cloud.config.uri=http://test-config:8888")
                .waitingFor(Wait.forLogMessage(".*Started App in.*", 1));

        springApp.start();

        seleniumExtension.getConfig().setDockerNetwork(network.getId());
        seleniumExtension.getConfig().setVnc(true);
    }

    @AfterAll
    static void finish() {
        springApp.stop();
        postgres.stop();
        cloudConfigServer.stop();

    }

    @Test
    public void testChrome(@DockerBrowser(type = CHROME) RemoteWebDriver driver) throws InterruptedException {

        driver.get("http://test-web:8080/");
        assertTrue(driver.getTitle().contains("App Test"));
        Thread.sleep(60000);
    }
}
