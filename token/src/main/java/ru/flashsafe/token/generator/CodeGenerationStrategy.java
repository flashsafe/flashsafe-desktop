package ru.flashsafe.token.generator;

import ru.flashsafe.token.exception.CodeGenerationException;

/**
 * An object that represents code generator. An implementation of this interface
 * has to be thread-safe.
 * 
 * 
 * @author Andrew
 * 
 */
public interface CodeGenerationStrategy {

    /**
     * Generates code for provided key.
     * 
     * @param key the key
     * @return the code
     * @throws CodeGenerationException if an error occurs while creating code
     */
    String generateCodeFor(String key) throws CodeGenerationException;
    
}
