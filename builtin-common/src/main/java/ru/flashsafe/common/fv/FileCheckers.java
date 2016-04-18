package ru.flashsafe.common.fv;

import java.util.EnumMap;

public class FileCheckers {

    private static final EnumMap<Algorithms, FileChecker> ALGORITHM_TO_CHECKER;

    static {
        ALGORITHM_TO_CHECKER = new EnumMap<Algorithms, FileChecker>(Algorithms.class);
        ALGORITHM_TO_CHECKER.put(Algorithms.CRC32, new CRC32FileChecker());
        ALGORITHM_TO_CHECKER.put(Algorithms.MD5, new MD5FileChecker());
    }

    private FileCheckers() {
    }

    FileChecker checkerFor(Algorithms algorithm) {
        return ALGORITHM_TO_CHECKER.get(algorithm);
    }

}
