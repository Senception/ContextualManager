package com.senception.security;

/**
 * Created by Senception on 16/03/2018.
 */

public class MacSecurity {

    /**
     * Hashes the given string using MD5
     * @param strToHash
     * @return hashed string
     */
    public static String MD5hash(String strToHash) {
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
