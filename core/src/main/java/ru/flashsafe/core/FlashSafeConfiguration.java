package ru.flashsafe.core;

import java.util.Properties;

public class FlashSafeConfiguration {

    private final Properties configurationProperties = new Properties();
    
    public FlashSafeConfiguration() {
    }
    
    public void registerParameter(String name, Object value) {
        configurationProperties.put(name, value);
    }
    
    Properties getConfigurationProperties() {
        return configurationProperties;
    }
    
    public static FlashSafeConfigurationBuilder builder() {
        return new FlashSafeConfigurationBuilder();
    }
    
    public static class FlashSafeConfigurationBuilder {
        
        private final FlashSafeConfiguration configuration = new FlashSafeConfiguration();
        
        private FlashSafeConfigurationBuilder() {
        }
        
        public FlashSafeConfigurationBuilder registerUserId(String id) {
            configuration.registerParameter(FlashSafeRegistry.USER_ID, id);
            return this;
        }
        
        public FlashSafeConfigurationBuilder registerSecret(String secret) {
            configuration.registerParameter(FlashSafeRegistry.SECRET, secret);
            return this;
        }
        
        public FlashSafeConfiguration build() {
            return configuration;
        }
        
    }
}
