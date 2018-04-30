package com.senception.contextualmanager.wifi.p2p;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Copyright Senception Lda
 * Update to Contextual Manager 2018 Senception Lda
 * @author Miguel Tavares (COPELABS/ULHT)
 * @author José Soares (Senception Lda) - jose.soares@senception.com
 * @version 1.1, 2018
 *
 * @file WifiPepTxtRecord, this manages the txtRecords.
 */
public abstract class WifiP2pTxtRecord {

    /**
     * This variable is used as a key to write and read data on preferences
     */
    private static final String WIFI_P2P_TXT_RECORD = "Records";

    public static void setRecord(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(WIFI_P2P_TXT_RECORD, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static Map<String, String> getEntries(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(WIFI_P2P_TXT_RECORD, MODE_PRIVATE);
        return castsMapToStringString(sharedPreferences.getAll());
    }

    private static Map<String, String> castsMapToStringString(Map<String, ?> currentMap) {
        Map<String, String> newMap = new HashMap<>();
        for (Map.Entry<String, ?> entry : currentMap.entrySet()) {
            if (entry.getValue() instanceof String) {
                newMap.put(entry.getKey(), (String) entry.getValue());
            }
        }
        return newMap;
    }

}
