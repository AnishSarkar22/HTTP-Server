package com.httpserver.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class HttpVersionTest {

    
    // Tests that an exact HTTP version string returns the correct enum value
    @Test
    void getBestCompatibleVersionExactMatch() {
        HttpVersion version = null;
        try {
            version = HttpVersion.getBestCompatibleVersion("HTTP/1.1");
        } catch (BaddHttpVersionException e) {
            fail();
        }

        assertNotNull(version);
        assertEquals(version, HttpVersion.HTTP_1_1);
    }

    // Tests that a badly formatted HTTP version string throws an exception
    @Test
    void getBestCompatibleVersionBadFormat() {
        HttpVersion version = null;
        try {
            version = HttpVersion.getBestCompatibleVersion("http/1.1");
            fail();
        } catch (BaddHttpVersionException e) {
        }       
    }

    // Tests that a higher minor version returns the closest lower supported version
    @Test
    void getBestCompatibleVersionHigherVersion() {
        HttpVersion version = null;
        try {
            version = HttpVersion.getBestCompatibleVersion("HTTP/1.2");
            assertNotNull(version);
            assertEquals(version, HttpVersion.HTTP_1_1);
        } catch (BaddHttpVersionException e) {
            fail();
        }
    }
}
