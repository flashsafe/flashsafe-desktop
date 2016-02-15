package ru.flashsafe.core.operation.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.inject.Singleton;

import ru.flashsafe.core.operation.Operation;
import ru.flashsafe.core.operation.OperationRegistry;
import ru.flashsafe.core.operation.monitor.OperationMonitor;

@Singleton
public class OperationRegistryImpl implements OperationRegistry, OperationMonitor {

    private final Map<Long, Operation> operationsMap = new ConcurrentHashMap<Long, Operation>();
    
    OperationRegistryImpl() {
    }
    
    @Override
    public void registerOperation(Operation operation) {
        operationsMap.putIfAbsent(operation.getId(), operation);
    }
    
    @Override
    public void unregisterOperation(Operation operation) {
        operationsMap.remove(operation.getId(), operation);
    }

}
