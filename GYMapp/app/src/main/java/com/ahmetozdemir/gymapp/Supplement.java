package com.ahmetozdemir.gymapp;

import java.io.Serializable;

public class Supplement implements Serializable //Serializable interface'i ile oluşturulan nesneyi farklı yerlerde de kullanabiliyoruz.
{
    String supplementName; // Supplement adı
    String supplementInfo; // Supplement bilgisi
    String supplementImage;   // Supplement görseli
    int supplementID;

    public Supplement(String supplementName, String supplementInfo, String supplementImage, int supplementID) // Constructor
    {
        this.supplementName = supplementName;
        this.supplementInfo = supplementInfo;
        this.supplementImage = supplementImage;
        this.supplementID = supplementID;
    }
    public int getSupplementID()
    {
        return supplementID;
    }
}
