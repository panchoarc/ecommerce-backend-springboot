package com.buyit.ecommerce.config;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.time.Duration;
import java.util.Objects;


@Slf4j
public class TestContainersConfig {


    private static final String POSTGRES_IMAGE_VERSION = "postgres:15-alpine";
    private static final String KEYCLOAK_IMAGE_VERSION = "quay.io/keycloak/keycloak:26.0";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE_VERSION))
            .withDatabaseName("ecommerce")
            .withUsername("admin")
            .withPassword("admin")
            .withInitScript("init-data.sql")
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(3)));


    @Container
    static KeycloakContainer keycloakContainer = new KeycloakContainer(KEYCLOAK_IMAGE_VERSION)
            .withCopyToContainer(MountableFile.forClasspathResource("realm-export.json"), "/opt/keycloak/data/import/realm-export.json")
            .withExposedPorts(8080)
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(3)))
            .withReuse(true)
            .withCreateContainerCmdModifier(cmd -> Objects.requireNonNull(cmd.getHostConfig()).withPortBindings(
                    new PortBinding(Ports.Binding.bindPort(8081), new ExposedPort(8080))
            ));


    @BeforeAll
    static void beforeAll() {
        postgreSQLContainer.start();
        keycloakContainer.start();
    }


    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("keycloak.auth-server-url", keycloakContainer::getAuthServerUrl);
    }
}
