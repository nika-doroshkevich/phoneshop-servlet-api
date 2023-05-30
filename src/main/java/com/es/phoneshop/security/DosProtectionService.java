package com.es.phoneshop.security;

public interface DosProtectionService {

    boolean isAllowed(String ip);

    void checkCountPerMinute();
}
