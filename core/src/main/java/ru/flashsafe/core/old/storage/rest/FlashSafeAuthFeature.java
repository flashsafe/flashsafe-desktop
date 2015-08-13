package ru.flashsafe.core.old.storage.rest;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

/**
 * 
 * 
 * @author Andrew
 *
 */
public class FlashSafeAuthFeature implements Feature {
    
    @Override
    public boolean configure(FeatureContext context) {
        final Configuration config = context.getConfiguration();
        if (!config.isRegistered(FlashSafeAuthClientFilter.class)) {
            context.register(FlashSafeAuthClientFilter.class);
        }
        return true;
    }

}
