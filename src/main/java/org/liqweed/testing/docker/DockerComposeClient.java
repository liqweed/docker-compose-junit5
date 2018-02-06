package org.liqweed.testing.docker;

import com.palantir.docker.compose.connection.Cluster;
import com.palantir.docker.compose.connection.waiting.ClusterHealthCheck;
import com.palantir.docker.compose.connection.waiting.ClusterWait;
import com.palantir.docker.compose.execution.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class DockerComposeClient {
    private static final Logger log = LoggerFactory.getLogger(DockerComposeClient.class);

    private final Docker docker;
    private final Cluster cluster;
    private final DockerCompose dockerCompose;
    private final List<ClusterWait> clusterWaits;
    private final DockerComposeConfiguration configuration;

    public DockerComposeClient(Docker docker, Cluster cluster, DockerCompose dockerCompose, List<ClusterWait> clusterWaits, DockerComposeConfiguration configuration) {
        this.docker = docker;
        this.cluster = cluster;
        this.dockerCompose = dockerCompose;
        this.clusterWaits = clusterWaits;
        this.configuration = configuration;
    }

    public void before() throws IOException, InterruptedException {
        log.debug("Starting docker-compose cluster");
        if (configuration.isPullOnStartup()) {
            dockerCompose.pull();
        }

        dockerCompose.build();

        DockerCompose upDockerCompose = dockerCompose;
        if (configuration.isRemoveConflictingContainersOnStartup()) {
            upDockerCompose = new ConflictingContainerRemovingDockerCompose(upDockerCompose, docker);
        }
        upDockerCompose.up();

        configuration.getLogCollector().startCollecting(dockerCompose);
        log.debug("Waiting for services");
        new ClusterWait(ClusterHealthCheck.nativeHealthChecks(), configuration.getNativeServiceHealthCheckTimeout())
                .waitUntilReady(cluster);
        clusterWaits.forEach(clusterWait -> clusterWait.waitUntilReady(cluster));
        log.debug("docker-compose cluster started");
    }

    public void after() {
        try {
            configuration.getShutdownStrategy().shutdown(dockerCompose, docker);
            configuration.getLogCollector().stopCollecting();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error cleaning up docker compose cluster", e);
        }
    }

    public String exec(DockerComposeExecOption options, String containerName,
                       DockerComposeExecArgument arguments) throws IOException, InterruptedException {
        return dockerCompose.exec(options, containerName, arguments);
    }

    public String run(DockerComposeRunOption options, String containerName,
                      DockerComposeRunArgument arguments) throws IOException, InterruptedException {
        return dockerCompose.run(options, containerName, arguments);
    }
}
