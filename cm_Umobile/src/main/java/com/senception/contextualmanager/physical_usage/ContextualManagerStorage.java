package com.senception.contextualmanager.physical_usage;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

/**
 * Copyright (C) Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@senception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017/2018
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Class to get the storage usage.
 *
 */
public class ContextualManagerStorage {

    final static StatFs statFsE = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());

    /**
     * Function to get the current storage usage on this device.
     * @return the current storage usage in percentage.
     */
    public static int getCurrentStorage() {

        long totalE;
        long freeE;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            totalE = statFsE.getBlockSizeLong() * statFsE.getBlockCountLong();
            freeE = statFsE.getAvailableBlocksLong() * statFsE.getBlockSizeLong();
        }
        else {
            freeE = (statFsE.getAvailableBlocks() * (long) statFsE.getBlockSize());
            totalE = (long)statFsE.getBlockSize() * (long) statFsE.getBlockCount();
        }

        int storagePercentage = (int) ((freeE/(double)totalE * 100.0) + 0.5);
        return storagePercentage;
    }
}
