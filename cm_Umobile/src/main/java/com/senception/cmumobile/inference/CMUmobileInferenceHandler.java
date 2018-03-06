package com.senception.cmumobile.inference;

import android.util.Log;

import com.senception.cmumobile.databases.CMUmobileDataSource;
import com.senception.cmumobile.databases.CMUmobileSQLiteHelper;
import com.senception.cmumobile.interfaces.CMUmobileInference;
import com.senception.cmumobile.modals.CMUmobileWeight;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * TODO
 * Created by Senception on 05/02/2018.
 */

public class CMUmobileInferenceHandler implements CMUmobileInference {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss");
    private CMUmobileDataSource dataSource;

    /**
     * Constructor
     * @param dataSource the database where the U, A and I are stored.
     */
    public CMUmobileInferenceHandler(CMUmobileDataSource dataSource){
        this.dataSource = dataSource;
    }

    /**
     * Function to get the availability
     */
    @Override
    public ArrayList<Integer> getA() {
        CMUmobileWeight weight = dataSource.getWeight(CMUmobileSQLiteHelper.TABLE_WEIGHTS);
        return weight.getA();
    }

    /**
     * Function to get the centrality
     */
    @Override
    public ArrayList<Integer> getC() {
        CMUmobileWeight weight = dataSource.getWeight(CMUmobileSQLiteHelper.TABLE_WEIGHTS);
        return weight.getA();
    }

    /**
     * Function to get the similarity
     */
    @Override
    public ArrayList<Integer> getI() {
        return null;
    }

    /**
     * Function to get all the parameters that show
     * if a device is a good conductor to transmit data
     */
    @Override
    public ArrayList<ArrayList<Integer>> getAll() {
        ArrayList<ArrayList<Integer>> all = new ArrayList<>();
        all.add(getA());
        all.add(getC());
        //all.add(getI());
        return all;
    }
}
