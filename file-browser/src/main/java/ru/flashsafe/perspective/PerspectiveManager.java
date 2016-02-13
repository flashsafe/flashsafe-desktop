package ru.flashsafe.perspective;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PerspectiveManager {

    private final Map<PerspectiveType, Perspective> perspectivesMap = new HashMap<>();
    
    private Perspective currentPerspective = NullObjectPerspective.INSTANCE;
    
    public PerspectiveManager(Collection<Perspective> perspectives) {
        Objects.requireNonNull(perspectives);
        perspectives.forEach(perspective -> perspectivesMap.put(perspective.getType(), perspective) );
    }
    
    public synchronized void switchTo(PerspectiveType perspective) {
        if (currentPerspective.getType().equals(perspective)) {
            return;
        }
        Perspective newPerspective = perspectivesMap.get(perspective);
        if (newPerspective == null) {
            throw new IllegalStateException("Perspective " + perspective + " is unavailable");
        }
        switchTo(newPerspective);
        currentPerspective = newPerspective;
    }
    
    private void switchTo(Perspective newPerspective) {
        currentPerspective.switchOff();
        newPerspective.switchOn();
    }
    
    private static final class NullObjectPerspective implements Perspective {
        
        public static final Perspective INSTANCE = new NullObjectPerspective();
        
        private NullObjectPerspective() {
        }
        
        @Override
        public void switchOn() {
        }

        @Override
        public void switchOff() {
        }

        @Override
        public PerspectiveType getType() {
            return PerspectiveType.NULL;
        }
        
    }
    
}
