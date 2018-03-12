package com.senception.contextualmanager.physical_usage;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Class to get the storage usage.
 *
 */
public class ContextualManagerStorage {

    //internal storage : storage space on device that can be used for installing applications and their associated data.
    //final static StatFs statFsI = new StatFs(Environment.getDataDirectory().getAbsolutePath());
    //external storage : refers to the storage space that the user can access for music, pictures, downloads, etc.
    final static StatFs statFsE = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());

    public static int getCurrentStorage(Context context) {

        final int sdk = android.os.Build.VERSION.SDK_INT;

        long totalE = 0;
        long freeE = 0;
        long totalI = 0;
        long freeI = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            totalE = statFsE.getBlockSizeLong() * statFsE.getBlockCountLong();
            freeE = statFsE.getAvailableBlocksLong() * statFsE.getBlockSizeLong();
            //totalI = statFsI.getBlockSizeLong() * statFsI.getBlockCountLong();
            //freeI = statFsI.getAvailableBlocksLong() * statFsI.getBlockSizeLong();
        }
        else {
            freeE = (statFsE.getAvailableBlocks() * (long) statFsE.getBlockSize());
            totalE = (long)statFsE.getBlockSize() * (long) statFsE.getBlockCount();
            //freeI = (statFsI.getAvailableBlocks() * (long) statFsI.getBlockSize());
            //totalI = (long)statFsI.getBlockSize() * (long) statFsI.getBlockCount();
        }

        //Log.d("RESOURCE", "totalE: " + totalE/0x100000 + "MB");
        //Log.d("RESOURCE", "freeE: " + freeE/0x100000 + "MB");
        int percentAvailE = (int) ((freeE/(double)totalE * 100.0) + 0.5);
        //Log.d("RESOURCE", "percentage: " + percentAvailE  + "%");

        /*int percentAvailI = (int) ((freeI/(double)totalI * 100.0) + 0.5);
        Log.d("RESOURCE", "totalI: " + totalI/0x100000 + "MB");
        Log.d("RESOURCE", "freeI: " + freeI/0x100000 + "MB");
        Log.d("RESOURCE", "percentage: " + percentAvailI + "%");*/

        return percentAvailE;
    }

    /*
     *Function to convert bytes to mb, gb...

    public static String floatForm (double d)

    {
        return new DecimalFormat("#.##").format(d);
    }

    public static String bytesToHuman (long size)
    {
        long Kb = 1  * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size <  Kb)                 return floatForm(        size     ) + " byte";
        if (size >= Kb && size < Mb)    return floatForm((double)size / Kb) + " Kb";
        if (size >= Mb && size < Gb)    return floatForm((double)size / Mb) + " Mb";
        if (size >= Gb && size < Tb)    return floatForm((double)size / Gb) + " Gb";
        if (size >= Tb && size < Pb)    return floatForm((double)size / Tb) + " Tb";
        if (size >= Pb && size < Eb)    return floatForm((double)size / Pb) + " Pb";
        if (size >= Eb)                 return floatForm((double)size / Eb) + " Eb";

        return "???";
    }
    */
}
