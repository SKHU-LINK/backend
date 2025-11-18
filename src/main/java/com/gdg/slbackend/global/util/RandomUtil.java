package com.gdg.slbackend.global.util;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 공통으로 사용하는 랜덤 관련 유틸 기능을 제공함.
 * 리스트에서 임의의 요소를 골라주는 기능을 포함하며,
 * 메모 색상 랜덤 선택 등 다양한 곳에서 활용되도록 함.
 */

public class RandomUtil {

    public static <T> T pickRandom(List<T> list) {
        int index = ThreadLocalRandom.current().nextInt(list.size());
        return list.get(index);
    }
}
