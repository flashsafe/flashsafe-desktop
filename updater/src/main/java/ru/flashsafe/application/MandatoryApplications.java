package ru.flashsafe.application;

import ru.flashsafe.common.application.Application;
import ru.flashsafe.common.application.ApplicationDescriptor;
import ru.flashsafe.common.application.ApplicationDescriptor.LaunchInformation;

public class MandatoryApplications {

    public static final String FILE_BROWSER_NAME = "ru_flashsafe_file_browser";

    public static final ApplicationDescriptor FILE_BROWSER_DEFAULT_DESCRIPTOR;

    public static final Application FILE_BROWSER_APPLICATION;

    static {
        LaunchInformation browserLaunchInformation = new LaunchInformation("file-browser.jar", true, "");
        FILE_BROWSER_DEFAULT_DESCRIPTOR = new ApplicationDescriptor(FILE_BROWSER_NAME, null, "file-browser", browserLaunchInformation);
        FILE_BROWSER_APPLICATION = new Application(FILE_BROWSER_NAME);
    }

}
