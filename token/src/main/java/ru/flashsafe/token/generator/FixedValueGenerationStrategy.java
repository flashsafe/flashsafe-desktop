package ru.flashsafe.token.generator;

import ru.flashsafe.token.exception.CodeGenerationException;

/**
 * An implementation of {@link CodeGenerationStrategy} that returns predefined
 * code value on each {@link #generateCodeFor(String)} call.
 * 
 * @author Andrew
 * 
 */
public class FixedValueGenerationStrategy implements CodeGenerationStrategy {

    private final String code;
    
    public FixedValueGenerationStrategy(String value) {
        this.code = value;
    }
    
    public FixedValueGenerationStrategy() {
        this("test_code");
    }
    
    @Override
    public String generateCodeFor(String key) throws CodeGenerationException {
        return code;
    }

}
