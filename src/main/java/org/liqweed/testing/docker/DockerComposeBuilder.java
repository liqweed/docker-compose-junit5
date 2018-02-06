package org.liqweed.testing.docker;

import com.palantir.docker.compose.connection.Cluster;
import com.palantir.docker.compose.connection.ContainerCache;
import com.palantir.docker.compose.connection.DockerMachine;
import com.palantir.docker.compose.connection.ImmutableCluster;
import com.palantir.docker.compose.execution.Docker;
import com.palantir.docker.compose.execution.DockerExecutable;
import com.palantir.docker.compose.logging.DoNothingLogCollector;
import com.palantir.docker.compose.logging.LogCollector;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;

public class DockerComposeBuilder {

    private DockerMachine dockerMachine = DockerMachine.localMachine().build();
    private Cluster cluster = ImmutableCluster.builder()
                    .ip(dockerMachine.getIp())
                    .containerCache(new ContainerCache(docker(), dockerCompose))
                    .build();


    public DockerExecutable dockerExecutable() {
        return DockerExecutable.builder()
                .dockerConfiguration(dockerMachine)
                .build();
    }

    public Docker docker() {
        return new Docker(dockerExecutable());
    }

    Integer retryAttempts = 2;
    boolean removeConflictingContainersOnStartup = true;
    boolean pullOnStartup = false;

    ReadableDuration nativeServiceHealthCheckTimeout = Duration.standardMinutes(2);
    LogCollector logCollector = new DoNothingLogCollector();


    public DockerComposeClient build() {
        DockerComposeConfiguration configuration = new DockerComposeConfiguration(retryAttempts, removeConflictingContainersOnStartup, pullOnStartup, nativeServiceHealthCheckTimeout, logCollector, shutdownStrategy);
        return new DockerComposeClient(docker, dockerMachine, cluster, dockerCompose, clusterWaits, configuration);
    }
}
