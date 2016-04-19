package ru.flashsafe.common.fv.checksum;

import ru.flashsafe.common.fv.Algorithms;

public class Checksum {
    
    private final Algorithms algorithm;
    
    private final String checksumValue;
    
    public Checksum(Algorithms algorithm, String checksumValue) {
        this.algorithm = algorithm;
        this.checksumValue = checksumValue;
    }

    public Algorithms getAlgorithm() {
        return algorithm;
    }

    public String getChecksumValue() {
        return checksumValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((algorithm == null) ? 0 : algorithm.hashCode());
        result = prime * result + ((checksumValue == null) ? 0 : checksumValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Checksum other = (Checksum) obj;
        if (algorithm != other.algorithm)
            return false;
        if (checksumValue == null) {
            if (other.checksumValue != null)
                return false;
        } else if (!checksumValue.equals(other.checksumValue))
            return false;
        return true;
    }
}
