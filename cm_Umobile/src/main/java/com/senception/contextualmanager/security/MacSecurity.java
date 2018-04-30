package com.senception.contextualmanager.security;

/**
 * Copyright (C) Senception Lda
 * Author(s):
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017/2018
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Class to get the memory usage.
 *
 */

public class MacSecurity {

    /**
     * Hashes the given string using MD5
     * @param strToHash
     * @return hashed string
     */
    public static String md5Hash(String strToHash) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(strToHash.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }
}
