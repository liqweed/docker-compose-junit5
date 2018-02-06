package org.liqweed.testing.docker;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.execution.DefaultDockerCompose;
import com.palantir.docker.compose.execution.DockerCompose;
import com.palantir.docker.compose.execution.DockerComposeExecutable;
import org.junit.jupiter.api.extension.*;

import static com.palantir.docker.compose.connection.waiting.HealthChecks.toHaveAllPortsOpen;

public class DockerComposeExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    private DockerComposeExecutable docker;
    private DockerComposeRule dockerRule;

    public DockerComposeExtension() {
        docker = new DefaultDockerCompose();
        dockerRule = DockerComposeRule.builder()
                .pullOnStartup(true)
                .file("src/test/resources/docker-compose.yml")
                .saveLogsTo("target/test-docker-logs")
                .waitingForService("postgres", toHaveAllPortsOpen())
                .waitingForService("nginx", toHaveAllPortsOpen())
                .build();
    }

    public void beforeAll(ExtensionContext extensionContext) throws Exception {

        dockerRule.before();
    }

    public void afterAll(ExtensionContext extensionContext) throws Exception {
        dockerRule.after();
    }

    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(DockerComposeExecutable.class);
    }

    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return docker;
    }

}