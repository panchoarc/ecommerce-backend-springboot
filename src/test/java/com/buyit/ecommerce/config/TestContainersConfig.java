package com.buyit.ecommerce.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.time.Duration;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.*;

@Slf4j
public class TestContainersConfig {

    private static final String POSTGRES_IMAGE_VERSION = "postgres:15-alpine";
    private static final String KEYCLOAK_IMAGE_VERSION = "quay.io/keycloak/keycloak:26.0";
    private static final String LOCALSTACK_IMAGE_VERSION = "localstack/localstack:4";

    public static final String BUCKET_NAME = "ecommerce-buyit-bucket";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE_VERSION))
            .withDatabaseName("ecommerce")
            .withUsername("admin")
            .withPassword("admin")
            .waitingFor(Wait.forListeningPort())
            .withStartupTimeout(Duration.ofMinutes(5));

    @Container
    static GenericContainer<?> keycloakContainer = new GenericContainer<>(DockerImageName.parse(KEYCLOAK_IMAGE_VERSION))
            .withExposedPorts(8080)
            .withCopyToContainer(MountableFile.forClasspathResource("realm-export.json"), "/opt/keycloak/data/import/realm-export.json")
            .withEnv("KC_BOOTSTRAP_ADMIN_USERNAME", "admin")
            .withEnv("KC_BOOTSTRAP_ADMIN_PASSWORD", "admin")
            .withEnv("KC_HEALTH_ENABLED", "true")
            .withEnv("KC_METRICS_ENABLED", "true")
            .withCommand("start-dev --import-realm")
            .waitingFor(Wait.forListeningPort())
            .withStartupTimeout(Duration.ofMinutes(5));

    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse(LOCALSTACK_IMAGE_VERSION))
            .withServices(S3, SQS, DYNAMODB)
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("localstack/scripts/config-aws.sh"),
                    "/etc/localstack/init/ready.d/config-aws.sh"
            )
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("localstack/scripts/create-bucket.sh"),
                    "/etc/localstack/init/ready.d/create-bucket.sh"
            )
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("localstack/scripts/cors.json"),
                    "/etc/localstack/init/ready.d/cors.json"
            )
            .waitingFor(Wait.forHealthcheck())
            .withStartupTimeout(Duration.ofMinutes(5));


    @BeforeAll
    static void setupContainers() {
        localStackContainer.start();
        postgreSQLContainer.start();
        keycloakContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("frontend.url", () -> "http://localhost:5173");

        // Keycloak dynamic endpoint
        registry.add("keycloak.auth-server-url", () ->
                "http://" + keycloakContainer.getHost() + ":" + keycloakContainer.getMappedPort(8080)
        );

        // LocalStack dynamic endpoints & credentials
        registry.add("aws.s3.useLocalStack", () -> true);
        registry.add("aws.s3.bucket-name", () -> BUCKET_NAME);
        registry.add("aws.s3.localstackEndpoint", () -> localStackContainer.getEndpointOverride(S3).toString());
        registry.add("aws.s3.region", () -> localStackContainer.getRegion());
        registry.add("aws.s3.access-key", () -> localStackContainer.getAccessKey());
        registry.add("aws.s3.secret-key", () -> localStackContainer.getSecretKey());
    }
}