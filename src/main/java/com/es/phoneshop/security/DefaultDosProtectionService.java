package com.es.phoneshop.security;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDosProtectionService implements DosProtectionService {

    private static final long THRESHOLD = 20;
    private Map<String, Long> countMap = new ConcurrentHashMap<>();
    private Set<String> ipsBlackList = new HashSet<>();

    private static DefaultDosProtectionService instance;

    public static synchronized DefaultDosProtectionService getInstance() {
        if (instance == null) {
            instance = new DefaultDosProtectionService();
        }
        return instance;
    }

    @Override
    public boolean isAllowed(String ip) {
        if (ipsBlackList.contains(ip)) {
            return false;
        }

        Long count = countMap.get(ip);
        if (count == null) {
            count = 1L;
        } else {
            if (count > THRESHOLD) {
                ipsBlackList.add(ip);
                return false;
            }
            count++;
        }
        countMap.put(ip, count);
        return true;
    }

    @Override
    public void checkCountPerMinute() {
        countMap.entrySet().stream()
                .filter(ip -> ip.getValue() > 20)
                .map(Map.Entry::getKey)
                .peek(ip -> ipsBlackList.add(ip));

        countMap.entrySet().stream()
                .filter(ip -> ip.getValue() <= 20)
                .peek(ip -> ip.setValue(0L));
    }
}
