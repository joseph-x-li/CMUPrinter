package com.example.burger.cmuprinter;
import android.util.Pair;
public class Printer
{
    String printerID;
    Pair<Double, Double> gpsLocation;
    String pictureID;
    String buildingNameFloor;
    String funFact;
    boolean isColor;
    public Printer()
    {
        printerID = "";
        gpsLocation = new Pair<>(0.0, 0.0);
        pictureID = "";
        buildingNameFloor = "";
        funFact = "";
        isColor = false;
    }
    public Printer(String printID, Pair<Double, Double> gpLoc, String picID, String buildName, String fact)
    {
        printerID = printID;
        gpsLocation = gpLoc;
        pictureID = picID;
        buildingNameFloor = buildName;
        funFact = fact;
        isColor = printerID.contains("Color");
    }
}