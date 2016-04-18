package ru.flashsafe.updater;

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
        bind(PartitionLocator.class).to(WindowsPartitionLocator.class);
    }

}
