package com.senception.cmumobile.resource_usage;

/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains AppResourceUsage. This class represents an object that
 * records all the social network usage.
 *
 */
public class SocialNetworkUsage {

    private int SocialNet_Id;
    private int likes;
    private int dislikes;
    private String CounterCategoryPreferences;

    public SocialNetworkUsage(int socialNet_Id, int likes, int dislikes, String counterCategoryPreferences) {
        SocialNet_Id = socialNet_Id;
        this.likes = likes;
        this.dislikes = dislikes;
        CounterCategoryPreferences = counterCategoryPreferences;
    }

    public int getSocialNet_Id() {

        return SocialNet_Id;
    }

    public void setSocialNet_Id(int socialNet_Id) {
        SocialNet_Id = socialNet_Id;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public String getCounterCategoryPreferences() {
        return CounterCategoryPreferences;
    }

    public void setCounterCategoryPreferences(String counterCategoryPreferences) {
        CounterCategoryPreferences = counterCategoryPreferences;
    }
}
