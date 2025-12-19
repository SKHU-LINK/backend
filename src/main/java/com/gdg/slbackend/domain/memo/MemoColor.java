package com.gdg.slbackend.domain.memo;

import java.security.SecureRandom;
import java.util.List;

public enum MemoColor {
    RED,
    ORANGE,
    YELLOW,
    GREEN,
    BLUE,
    NAVY,
    PURPLE;

    private static final List<MemoColor> VALUES = List.of(values());
    private static final SecureRandom RANDOM = new SecureRandom();

    public static MemoColor randomColor() {
        return VALUES.get(RANDOM.nextInt(VALUES.size()));
    }
}