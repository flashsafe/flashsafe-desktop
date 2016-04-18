package ru.flashsafe.updater;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class UpdateSourcesTest {

    private static final String EXPECTED_PROTOCOL = "https";

    @Test
    public void allUpdateSourcesUseHttps() throws MalformedURLException {
        for (UpdateSources updateSource : UpdateSources.values()) {
            URL updateSourceURL = new URL(updateSource.updateSourceURL);
            assertEquals(updateSource.name() + " uses unexpected protocol!", EXPECTED_PROTOCOL, updateSourceURL.getProtocol());
        }
    }

}
