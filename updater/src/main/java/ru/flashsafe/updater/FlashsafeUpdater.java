package ru.flashsafe.updater;

import java.io.IOException;

import ru.flashsafe.common.partition.Partition.Type;
import ru.flashsafe.partition.WindowsPartitionLocator;

public class FlashsafeUpdater {

    
    public static void main(String[] args) throws IOException, InterruptedException {
        Updater updater = new Updater();
        updater.runFileBrowserApplication();
    }

}
