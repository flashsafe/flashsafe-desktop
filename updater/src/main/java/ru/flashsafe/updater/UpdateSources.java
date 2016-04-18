package ru.flashsafe.updater;

public enum UpdateSources {

    FLASH_SO("https://flash.so/"),

    FLASHSAFE_RU("https://flashsafe.ru/");

    public final String updateSourceURL;

    UpdateSources(String updateSourceURL) {
        this.updateSourceURL = updateSourceURL;
    }

}
