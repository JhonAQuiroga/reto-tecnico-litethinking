package com.litethinking.backend;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class HashTest {
    @Test
    void genHash() {
        System.out.println("HASH=" + new BCryptPasswordEncoder().encode("Visita@123"));
    }
}
