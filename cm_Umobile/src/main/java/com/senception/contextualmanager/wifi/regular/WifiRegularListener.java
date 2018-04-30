package com.senception.contextualmanager.wifi.regular;

/**
 * Copyright (C) Senception Lda
 * Update to Contextual Manager 2017-2018
 * @author José Soares - jose.soares@senception.com
 * @version 0.1
 *
 * @file holds WifiRegular, to set the Wi-Fi
 */

public interface WifiRegularListener {

    void onConnected();
    void onDisconnected();
}
