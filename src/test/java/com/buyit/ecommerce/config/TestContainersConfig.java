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
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.time.Duration;
import java.util.Objects;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.*;


@Slf4j
public class TestContainersConfig {


    private static final String POSTGRES_IMAGE_VERSION = "postgres:15-alpine";
    private static final String KEYCLOAK_IMAGE_VERSION = "quay.io/keycloak/keycloak:26.0";
    private static final String LOCALSTACK_IMAGE_VERSION = "localstack/localstack:4";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE_VERSION))
            .withDatabaseName("ecommerce")
            .withUsername("admin")
            .withPassword("admin")
            .waitingFor(Wait.defaultWaitStrategy().withStartupTimeout(Duration.ofMinutes(5)))
            .withReuse(true);


    @Container
    static KeycloakContainer keycloakContainer = new KeycloakContainer(KEYCLOAK_IMAGE_VERSION)
            .withCopyToContainer(MountableFile.forClasspathResource("realm-export.json"), "/opt/keycloak/data/import/realm-export.json")
            .withExposedPorts(8080)
            .waitingFor(Wait.defaultWaitStrategy().withStartupTimeout(Duration.ofMinutes(5)))
            .withReuse(true)
            .withCreateContainerCmdModifier(cmd -> Objects.requireNonNull(cmd.getHostConfig()).withPortBindings(
                    new PortBinding(Ports.Binding.bindPort(8081), new ExposedPort(8080))
            ));


    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse(LOCALSTACK_IMAGE_VERSION))
            .withExposedPorts(4566)
            .withServices(S3, SQS, DYNAMODB)
            .waitingFor(Wait.defaultWaitStrategy().withStartupTimeout(Duration.ofMinutes(5)))
            .withReuse(true);


    @BeforeAll
    static void setupContainers() {
        localStackContainer.start();
        postgreSQLContainer.start();
        keycloakContainer.start();
    }


    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("keycloak.auth-server-url", keycloakContainer::getAuthServerUrl);
        registry.add("aws.s3.bucket-name", () -> "my-test-bucket");
        registry.add("aws.s3.endpoint", () -> localStackContainer.getEndpointOverride(S3).toString());
        registry.add("aws.s3.region", () -> localStackContainer.getRegion());
        registry.add("aws.s3.access-key", localStackContainer::getAccessKey);
        registry.add("aws.s3.secret-key", localStackContainer::getSecretKey);

    }
}
