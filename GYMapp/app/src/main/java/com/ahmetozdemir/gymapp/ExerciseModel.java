package com.ahmetozdemir.gymapp;

import java.io.Serializable;

public class ExerciseModel implements Serializable
{
    private String exerciseName;
    private  int exerciseID;
    private String exerciseDescription;

    private String exerciseLink;
    // private int imageResource; // Eğer resim kullanacaksak

    public ExerciseModel(String exerciseName, String exerciseDescription ,String exerciseLink,int exerciseID)
    {
        this.exerciseName = exerciseName;
        this.exerciseDescription = exerciseDescription;
        this.exerciseLink = exerciseLink;
        this.exerciseID = exerciseID;
        // this.imageResource = imageResource;
        // resim olayı database için sıkıntıymış yasirle konuş
    }

    public String getExerciseName()
    {
        return exerciseName;
    }
    public String getExerciseLink()
    {
        return exerciseLink;
    }
    public int getExerciseID()
    {
        return exerciseID;
    }
    public void setName(String n)
    {
        exerciseName = n;
    }

    public String getExerciseDescription()
    {
        return exerciseDescription;
    }

    // public int getImageResource()
    // {
    //     return imageResource;
    // }
}