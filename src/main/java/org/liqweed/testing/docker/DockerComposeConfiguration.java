package org.liqweed.testing.docker;

import com.palantir.docker.compose.configuration.ShutdownStrategy;
import com.palantir.docker.compose.logging.LogCollector;
import org.joda.time.ReadableDuration;

public class DockerComposeConfiguration {
    private final int retryAttempts;
    private final boolean removeConflictingContainersOnStartup;
    private final boolean pullOnStartup;
    private final ReadableDuration nativeServiceHealthCheckTimeout;
    private final LogCollector logCollector;
    private final ShutdownStrategy shutdownStrategy;

    public DockerComposeConfiguration(int retryAttempts, boolean removeConflictingContainersOnStartup, boolean pullOnStartup, ReadableDuration nativeServiceHealthCheckTimeout, LogCollector logCollector, ShutdownStrategy shutdownStrategy) {
        this.retryAttempts = retryAttempts;
        this.removeConflictingContainersOnStartup = removeConflictingContainersOnStartup;
        this.pullOnStartup = pullOnStartup;
        this.nativeServiceHealthCheckTimeout = nativeServiceHealthCheckTimeout;
        this.logCollector = logCollector;
        this.shutdownStrategy = shutdownStrategy;
    }

    public int getRetryAttempts() {
        return retryAttempts;
    }

    public boolean isRemoveConflictingContainersOnStartup() {
        return removeConflictingContainersOnStartup;
    }

    public boolean isPullOnStartup() {
        return pullOnStartup;
    }

    public ReadableDuration getNativeServiceHealthCheckTimeout() {
        return nativeServiceHealthCheckTimeout;
    }

    public LogCollector getLogCollector() {
        return logCollector;
    }

    public ShutdownStrategy getShutdownStrategy() {
        return shutdownStrategy;
    }
}
