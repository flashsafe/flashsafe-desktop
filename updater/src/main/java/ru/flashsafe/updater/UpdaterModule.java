package ru.flashsafe.updater;

import ru.flashsafe.common.ssl.SSLService;
import ru.flashsafe.common.util.OSUtils;
import ru.flashsafe.dev.SSLServiceDevImplementation;
import ru.flashsafe.partition.LinuxPartitionLocator;
import ru.flashsafe.partition.MacOSPartitionLocator;
import ru.flashsafe.partition.PartitionLocator;
import ru.flashsafe.partition.WindowsPartitionLocator;

import com.google.inject.AbstractModule;

/**
 * Guice's configuration for module Updater.
 *
 */
public class UpdaterModule extends AbstractModule {

    @Override
    protected void configure() {
        //FIXME switch to actual SSLService implementation
        bind(SSLService.class).to(SSLServiceDevImplementation.class);
        bindOSDependentBeans();
    }

    private void bindOSDependentBeans() {
        if (OSUtils.isWindows()) {
            bindForWindows();
        } else if (OSUtils.isUnix()) {
            bindForLinux();
        } else if (OSUtils.isMacOS()) {
            bindForMacOS();
        }
    }

    private void bindForWindows() {
        bind(PartitionLocator.class).to(WindowsPartitionLocator.class);
    }

    private void bindForLinux() {
        bind(PartitionLocator.class).to(LinuxPartitionLocator.class);
    }

    private void bindForMacOS() {
        bind(PartitionLocator.class).to(MacOSPartitionLocator.class);
    }
}
