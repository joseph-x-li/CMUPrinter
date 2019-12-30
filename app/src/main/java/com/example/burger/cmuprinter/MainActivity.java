package com.example.burger.cmuprinter;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
{
    //Have we done a search? starts as false. Every onFind, true; every onCancel, false;
    private boolean foundPrinter;
    //ID of printer, if foundPrinter==true;
    private String foundID;
    //are we in detailed view
    private boolean detailed;
    //Object from which we can get the location of phone
    private FusedLocationProviderClient fusedLocationClient;
    //map of our printers
    private HashMap<String, Printer> printerList;
    //our location
    private Pair<Double, Double> myLocation;
    //SHITFUCK
    private ArrayList<String> PrinterList;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize data members
        foundPrinter = false;
        foundID = "";
        detailed= false;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        printerList = new HashMap<>();
        populateMap();
        findLocation();
        myLocation = new Pair(1.0, 1.0);
        ArrayList<String> PrinterList = new ArrayList<>();
        //END initialize data members

    }
    //-----------------------------------------------------------------------------------------------------------Begin Functions
    private void findLocation() //DONE
    {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>()
                {
                    @Override
                    public void onSuccess(Location location)
                    {
                        if (location != null)
                        {
                            myLocation = new Pair<>(new Double(location.getLatitude()), new Double(location.getLongitude()));
                        }
                    }
                });
    }
    double dist(Pair<Double,Double> a, Pair<Double, Double> b)//DONE
    {
        return Math.sqrt(Math.pow((a.first - b.first), 2.0) + Math.pow((a.second - b.second), 2.0));
    }
    private void closestPrinter(boolean color) //DONE
    {
//        scrapeWebpage();
//        findLocation();
        String answer = "";
//        double smallestDist = 1000;
//        for(int i= 0; i< PrinterList.size(); i++)
//        {
//            if((PrinterList.get(i).contains("Color")&&!color)||(PrinterList.get(i).contains("B&W")&&color))
//            {
//                continue;
//            }
//            if (printerList.containsKey(PrinterList.get(i))) {
//                double tmp = dist(myLocation, printerList.get(PrinterList.get(i)).gpsLocation);
//                if(tmp< smallestDist)
//                {
//                    smallestDist = tmp;
//                    answer = PrinterList.get(i);
//                }
//            }
//        }
        answer = "SCS - B&W - Gates Hall Third Floor";
        foundID= answer;
    }
    private void closestPrinterAll(boolean color)//DONE
    {
        double smallestDist = 1000;
        String answer = "";
        for (String sk : printerList.keySet()) {
            if((printerList.get(sk).printerID.contains("Color")&&!color)||(printerList.get(sk).printerID.contains("B&W")&&color))
            {
                continue;
            }
            double tmp = dist(myLocation, printerList.get(sk).gpsLocation);
            if (tmp < smallestDist) {
                smallestDist = tmp;
                answer = sk;
            }
        }
        foundID = answer;
    }
    private void scrapeWebpage()//DONE
    {
        Thread thread = new Thread(new Runnable(){
            public void run() {
                try {
                    PrinterList = new ArrayList<>();
                    try{

                        String url = "https://clustersweb.andrew.cmu.edu/PrinterStats/All/";
                        Document document = Jsoup.connect(url).get();
                        Elements Table = document.select("Table").first().children().select("tr");

                        int i = 0;
                        for(Element Row: Table){
                            if (i>= 2) {
                                String line = Row.select("td").text();
                                Boolean working = line.toLowerCase().contains("ready");
                                Pattern p = Pattern.compile("(^.*?\\s\\s)");
                                Matcher m = p.matcher(line);
                                if(m.find()) {
                                    String name = m.group(1);
                                    PrinterList.add(name.trim());
                                }
                            }
                            i++;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try
        {
            Thread.sleep(1000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void populateMap() //DONE
    {
        Pair<Double, Double> scobell = new Pair(40.441343, -79.939637);
        Pair<Double, Double> g_ghc3 = new Pair(40.44334, -79.9446);
        Pair<Double, Double> g_ghc5 = new Pair(40.44335, -79.94441);
        Pair<Double, Double> sorrells = new Pair(40.442653, -79.945812);
        Pair<Double, Double> tepper = new Pair(40.445037, -79.945303);
        String trashPic = "trash";
        String ghc3_fun = "Was Kicked by John Mackey";
        String ghc5_fun = "Was rolled down the Spiral";
        String boringText = "Does not accept DineX";
        printerList.put("CEE - Color 1 - Porter Hall 118A", new Printer("CEE - Color 1 - Porter Hall 118A", scobell, trashPic, "Porter Hall 118A", boringText));
        printerList.put("CEE - B&W 2 - Porter Hall A7", new Printer("CEE - B&W 2 - Porter Hall A7", scobell, trashPic, "Porter Hall A7", boringText));
        printerList.put("CEE - Color 2 - Porter Hall A7", new Printer("CEE - Color 2 - Porter Hall A7", scobell, trashPic, "Porter Hall A7", boringText));
        printerList.put("CEE - B&W 3 - Wean Hall 3503", new Printer("CEE - B&W 3 - Wean Hall 3503", scobell, trashPic, "Wean Hall 3503", boringText));
        printerList.put("CEE - Color 3 - Wean Hall 3503", new Printer("CEE - Color 3 - Wean Hall 3503", scobell, trashPic, "Wean Hall 3503", boringText));
        printerList.put("ChemE - B&W - Doherty 1270", new Printer("ChemE - B&W - Doherty 1270", scobell, trashPic, "Doherty 1270", boringText));
        printerList.put("Computer Labs - Baker 1 B&W", new Printer("Computer Labs - Baker 1 B&W", scobell, trashPic, "Baker 1", boringText));
        printerList.put("Computer Labs - Baker 2 B&W", new Printer("Computer Labs - Baker 2 B&W", scobell, trashPic, "Baker 2", boringText));
        printerList.put("Computer Labs - CFA B&W", new Printer("Computer Labs - CFA B&W", scobell, trashPic, "CFA", boringText));
        printerList.put("Computer Labs - CFA Color", new Printer("Computer Labs - CFA Color", scobell, trashPic, "CFA", boringText));
        printerList.put("Computer Labs - Cyert B&W", new Printer("Computer Labs - Cyert B&W", scobell, trashPic, "Cyert", boringText));
        printerList.put("Computer Labs - Hunt B&W", new Printer("Computer Labs - Hunt B&W", scobell, trashPic, "Hunt", boringText));
        printerList.put("Computer Labs - Morewood B&W", new Printer("Computer Labs - Morewood B&W", scobell, trashPic, "Morewood", boringText));
        printerList.put("Computer Labs - UC 1 B&W", new Printer("Computer Labs - UC 1 B&W", scobell, trashPic, "UC 1", boringText));
        printerList.put("Computer Labs - UC 2 B&W", new Printer("Computer Labs - UC 2 B&W", scobell, trashPic, "UC 2", boringText));
        printerList.put("Computer Labs - Wean B&W", new Printer("Computer Labs - Wean B&W", scobell, trashPic, "Wean", boringText));
        printerList.put("Computer Labs - Wean Color", new Printer("Computer Labs - Wean Color", scobell, trashPic, "Wean", boringText));
        printerList.put("Computer Labs - West Wing B&W", new Printer("Computer Labs - West Wing B&W", scobell, trashPic, "West Wing", boringText));
        printerList.put("ECE - B&W 1 - Hamerschlag Hall A104", new Printer("ECE - B&W 1 - Hamerschlag Hall A104", scobell, trashPic, "Hamerschlag Hall A104", boringText));
        printerList.put("ECE - B&W 2 - Hamerschlag Hall A101", new Printer("ECE - B&W 2 - Hamerschlag Hall A101", scobell, trashPic, "Hamerschlag Hall A101", boringText));
        printerList.put("ECE - B&W 3 - Hamerschlag Hall 1303", new Printer("ECE - B&W 3 - Hamerschlag Hall 1303", scobell, trashPic, "Hamerschlag Hall 1303", boringText));
        printerList.put("ECE - B&W 4 - Hamerschlag Hall 1310", new Printer("ECE - B&W 4 - Hamerschlag Hall 1310", scobell, trashPic, "Hamerschlag Hall 1310", boringText));
        printerList.put("ECE - B&W 5 - Masters Lounge in Porter Hall", new Printer("ECE - B&W 5 - Masters Lounge in Porter Hall", scobell, trashPic, "Masters Lounge in Porter Hall", boringText));
        printerList.put("ECE - B&W 6 - Porter Hall B-Level", new Printer("ECE - B&W 6 - Porter Hall B Level", scobell, trashPic, "Porter Hall B Level", boringText));
        printerList.put("ECE - Color 6 - Porter Hall B-Level", new Printer("ECE - Color 6 - Porter Hall B Level", scobell, trashPic, "Porter Hall B Level", boringText));
        printerList.put("Heinz - B&W 1 - Hamburg Hall A200", new Printer("Heinz - B&W 1 - Hamburg Hall A200", scobell, trashPic, "Hamburg Hall A200", boringText));
        printerList.put("Heinz - B&W 2 - Hamburg Hall A002A", new Printer("Heinz - B&W 2 - Hamburg Hall A002A", scobell, trashPic, "Hamburg Hall A002A", boringText));
        printerList.put("Heinz - B&W 3 - Hamburg Hall A102", new Printer("Heinz - B&W 3 - Hamburg Hall A102", scobell, trashPic, "Hamburg Hall A102", boringText));
        printerList.put("Heinz - B&W 4 - Hamburg Hall 2004", new Printer("Heinz - B&W 4 - Hamburg Hall 2004", scobell, trashPic, "Hamburg Hall 2004", boringText));
        printerList.put("Housing - Doherty Apts B&W", new Printer("Housing - Doherty Apts B&W", scobell, trashPic, "Doherty Apts", boringText));
        printerList.put("Housing - Donner B&W", new Printer("Housing - Donner B&W", scobell, trashPic, "Donner", boringText));
        printerList.put("Housing - Mudge B&W", new Printer("Housing - Mudge B&W", scobell, trashPic, "Mudge", boringText));
        printerList.put("Housing - Res5 B&W", new Printer("Housing - Res5 B&W", scobell, trashPic, "Res5", boringText));
        printerList.put("Housing - The Hill B&W", new Printer("Housing - The Hill B&W", scobell, trashPic, "The Hill", boringText));
        printerList.put("INI - B&W 1 - INI Second Floor", new Printer("INI - B&W 1 - INI Second Floor", scobell, trashPic, "INI Second Floor", boringText));
        printerList.put("INI - B&W 2 - INI A-Level", new Printer("INI - B&W 2 - INI A Level", scobell, trashPic, "INI A Level", boringText));
        printerList.put("INI - B&W B - INI A-Level", new Printer("INI - B&W B - INI A Level", scobell, trashPic, "INI A Level", boringText));
        printerList.put("Library - Hunt 1 Ref 1 Color", new Printer("Library - Hunt 1 Ref 1 Color", scobell, trashPic, "Hunt 1 Ref 1", boringText));
        printerList.put("Library - Hunt 1 Ref 4 B&W", new Printer("Library - Hunt 1 Ref 4 B&W", scobell, trashPic, "Hunt 1 Ref 4", boringText));
        printerList.put("Library - Hunt 2 B&W", new Printer("Library - Hunt 2 B&W", scobell, trashPic, "Hunt 2", boringText));
        printerList.put("Library - Hunt 3 B&W", new Printer("Library - Hunt 3 B&W", scobell, trashPic, "Hunt 3", boringText));
        printerList.put("Library - Hunt 4 Arts B&W", new Printer("Library - Hunt 4 Arts B&W", scobell, trashPic, "Hunt 4 Arts", boringText));
        printerList.put("Library - Hunt 4 Arts Color", new Printer("Library - Hunt 4 Arts Color", scobell, trashPic, "Hunt 4 Arts", boringText));
        printerList.put("Library - Mellon B&W", new Printer("Library - Mellon B&W", scobell, trashPic, "Mellon", boringText));
        printerList.put("Library - Mellon Color", new Printer("Library - Mellon Color", scobell, trashPic, "Mellon", boringText));
        printerList.put("Library - Sorrells 1 B&W", new Printer("Library - Sorrells 1 B&W", sorrells, trashPic, "Sorrells 1", boringText));
        printerList.put("Library - Sorrells 2 B&W", new Printer("Library - Sorrells 2 B&W", sorrells, trashPic, "Sorrells 2", boringText));
        printerList.put("Library - Sorrells 3 Color", new Printer("Library - Sorrells 3 Color", sorrells, "sorrellscolor", "Sorrells 3", boringText));
        printerList.put("MSCF - B&W 1 - Hallway outside GSIA 139", new Printer("MSCF - B&W 1 - Hallway outside GSIA 139", scobell, trashPic, "Hallway outside GSIA 139", boringText));
        printerList.put("MSCF - B&W 2 - GSIA 132", new Printer("MSCF - B&W 2 - GSIA 132", scobell, trashPic, "GSIA 132", boringText));
        //important
        printerList.put("SCS - B&W - Gates Hall Third Floor", new Printer("SCS - B&W - Gates Hall Third Floor", g_ghc3, "ghc3", "Gates Hall Third Floor", ghc3_fun));
        printerList.put("SCS - B&W - Gates Hall Fifth Floor", new Printer("SCS - B&W - Gates Hall Fifth Floor", scobell, "ghc5", "Gates Hall Fifth Floor", ghc5_fun));
        //important
        printerList.put("Tepper - B&W 1 - Tepper Quad near Room 2202", new Printer("Tepper - B&W 1 - Tepper Quad near Room 2202", scobell, trashPic, "Tepper Quad near Room 2202", boringText));
        printerList.put("Tepper - B&W 2 - Tepper Quad near Room 2104", new Printer("Tepper - B&W 2 - Tepper Quad near Room 2104", scobell, trashPic, "Tepper Quad near Room 2104", boringText));
        printerList.put("Tepper - B&W 3 - Tepper Quad near Room 3805", new Printer("Tepper - B&W 3 - Tepper Quad near Room 3805", tepper, "tepper", "Tepper Quad near Room 3805", boringText));
        printerList.put("Tepper - B&W 4 - Tepper Quad across From Accelerate", new Printer("Tepper - B&W 4 - Tepper Quad across From Accelerate", scobell, trashPic, "Tepper Quad across From Accelerate", boringText));
        printerList.put("Tepper - B&W 5 - Tepper Quad near MBA Quiet Area", new Printer("Tepper - B&W 5 - Tepper Quad near MBA Quiet Area", scobell, trashPic, "Tepper Quad near MBA Quiet Area", boringText));
        printerList.put("Tepper - B&W 6 - Tepper Quad near Accelerate", new Printer("Tepper - B&W 6 - Tepper Quad near Accelerate", scobell, trashPic, "Tepper Quad near Accelerate", boringText));

    }
    private void showMap(boolean zoom)
    {
        //shows map zooomed if zoom
        //else shows map fully zoomed
        ImageView map = (ImageView) findViewById(R.id.printerPic);
        if(!zoom)
        {
            map.setImageResource(R.drawable.map);
        }
        else
        {
            Bitmap source = BitmapFactory.decodeResource(getResources(), R.drawable.map);
            Integer sourceWidth = source.getWidth();
            Integer sourceHeight = source.getHeight();
//            Integer personpixelx = (int) (sourceWidth * (myLocation.second + 79.947842) / (-79.937361+79.947842));
//            Integer printerpixelx = (int) (sourceWidth * (printerList.get(foundID).gpsLocation.second + 79.947842) / (-79.937361+79.947842));
//
//            Integer personpixely = (int) (sourceHeight * (myLocation.first - 40.439867) / (40.447606-40.439867));
//            Integer printerpixely = (int) (sourceHeight * (printerList.get(foundID).gpsLocation.first - 40.439867) / (40.447606-40.439867));
            Double TLLat = 40.447606;
            double TLLong = -79.947842;
            Double BRLat = 40.439867;
            double BRLong = -79.937361;

            Integer pXpixel = (int) (sourceWidth-(printerList.get(foundID).gpsLocation.first-40.439867)*(sourceWidth/.007731));
            Integer pYpixel = (int) ((printerList.get(foundID).gpsLocation.second-TLLong)*(sourceHeight/.010481));
            Integer currXpixel = (int) (sourceWidth-(myLocation.first-BRLat)*(sourceWidth/.007731));
            Integer currYpixel = (int) ((myLocation.second-TLLong)*(sourceHeight/0.010481));
//            Double TLLat = 40.447606;
//            double TLLong = -79.947842;
//            Double BRLat = 40.439867;
//            double BRLong = -79.937361;
//            double pLat = printerList.get(foundPrinter).gpsLocation.first;
//            double pLong = printerList.get(foundPrinter).gpsLocation.second;
//            double currLat = myLocation.first;
//            double currLong = myLocation.second;
//
//            Integer pXpixel = (int) (sourceWidth-(pLat-BRLat)*(sourceWidth/.007731));
//            Integer pYpixel = (int) ((pLong-TLLong)*(sourceHeight/.010481));
//            Integer currXpixel = (int) (sourceWidth-(currLat-BRLat)*(sourceWidth/.007731));
//            Integer currYpixel = (int) ((currLong-TLLong)*(sourceHeight/0.010481));
//
//
//
//            Bitmap dest = Bitmap.createBitmap(source, pXpixel, pYpixel-pXpixel, currXpixel, currYpixel - currXpixel);
//            Integer xmin = Math.min(personpixelx, printerpixelx) - 300;
//            Integer xmax = Math.max(personpixelx, printerpixelx) + 300;
//            Integer ymin = Math.min(personpixely, printerpixely) - 300;
//            Integer ymax = Math.max(personpixely, printerpixely) + 300;
            Integer middleX = (currXpixel + pXpixel)/2;
            Integer middleY = (currYpixel + pYpixel)/2;

            Integer Xdelta = pXpixel - currXpixel;
            Integer Ydelta = pYpixel - currYpixel;

            if(Xdelta < 0){
                Xdelta = -1 * Xdelta;
            }
            if(Ydelta < 0){
                Ydelta = -1 * Ydelta;
            }

            Integer delta = Math.max(Xdelta,Ydelta)/2 + 200;

            for(int i = 0; i < 100; i++){
                if(middleX - delta > 0 && middleX + delta < sourceWidth && middleY - delta > 0 && middleY + delta < sourceHeight){
                    break;
                }
                delta = delta - 10;
            }


            Integer xmin = middleX - delta;
            Integer ymin = middleY - delta;
            source = source.copy( Bitmap.Config.ARGB_8888 , true);
            for(int i=0; i<15; i++)
            {
                for(int j=0; j<15; j++)
                {
                    if((i < 4 || i > 10) || (j < 4 || j > 10))
                    source.setPixel((currYpixel-i)+7, (currXpixel-j)+7, Color.argb(255, 255, 0, 0));
                }
            }
            Boolean pletter [][] =
                    {
                            {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
                            {false, false, false, true, true, true, true, true, true, false, false, false, false, false, false},
                            {false, false, false, true, true, true, true, true, true, true, false, false, false, false, false},
                            {false, false, false, true, true, false, false, false, true, true, true, false, false, false, false},
                            {false, false, false, true, true, false, false, false, false, true, true, false, false, false, false},
                            {false, false, false, true, true, false, false, false, false, true, true, false, false, false, false},
                            {false, false, false, true, true, false, false, false, true, true, false, false, false, false, false},
                            {false, false, false, true, true, true, true, true, true, true, false, false, false, false, false},
                            {false, false, false, true, true, true, true, true, true, false, false, false, false, false, false},
                            {false, false, false, true, true, false, false, false, false, false, false, false, false, false, false},
                            {false, false, false, true, true, false, false, false, false, false, false, false, false, false, false},
                            {false, false, false, true, true, false, false, false, false, false, false, false, false, false, false},
                            {false, false, false, true, true, false, false, false, false, false, false, false, false, false, false},
                            {false, false, false, true, true, false, false, false, false, false, false, false, false, false, false},
                            {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false}
                    };
            for(int i=0; i<15; i++)
            {
                for(int j=0; j<15; j++)
                {
                    if(pletter[14-j][14-i])
                    {
                        source.setPixel((pYpixel-i)+7, (pXpixel-j)+7, Color.argb(255, 0, 0, 255));
                    }
                }
            }
            Bitmap dest = Bitmap.createBitmap(source, ymin, xmin, delta*2, delta*2);

            map.setImageBitmap(dest);

        }
    }
    //-----------------------------------------------------------------------------------------------------------End Functions
    //-----------------------------------------------------------------------------------------------------------Begin UI FN
    public void onFind(View v)//DONE
    {
        Switch theSwitch = (Switch) findViewById(R.id.switch1);
        boolean isColor = theSwitch.isChecked();
        //find closest printer
        closestPrinterAll(isColor);
        //find my position
        findLocation();
        //update map
        showMap(true);
        //update text fields
        TextView h_idbox = (TextView) findViewById(R.id.idbox);
        TextView h_printerLoc= (TextView) findViewById(R.id.printerLoc);
        TextView h_funFact= (TextView) findViewById(R.id.funFact);
        if (printerList.containsKey(foundID)) {

            double lldist = 111000 * dist(printerList.get(foundID).gpsLocation, myLocation);

            h_idbox.setText(printerList.get(foundID).printerID);
            h_printerLoc.setText(printerList.get(foundID).buildingNameFloor + " (~" + (int) lldist +"m)");
            h_funFact.setText("Fun Fact: " + printerList.get(foundID).funFact);
        }
        foundPrinter = true;
        detailed = false;
        Button bruh = (Button) findViewById(R.id.button);
        bruh.setText("SHOW DETAIL");
    }
    public void onCancel(View v)//DONE
    {
        TextView h_idbox = (TextView) findViewById(R.id.idbox);
        TextView h_printerLoc= (TextView) findViewById(R.id.printerLoc);
        TextView h_funFact= (TextView) findViewById(R.id.funFact);
        h_idbox.setText("Find a Printer!");
        h_printerLoc.setText("Find a Printer!");
        h_funFact.setText("Find a Printer!");
        showMap(false);
        foundPrinter = false;
        detailed = false;
        Button bruh = (Button) findViewById(R.id.button);
        bruh.setText("SHOW DETAIL");
    }
    public void onDetail(View v) //DONE
    {
        Button bruh = (Button) findViewById(R.id.button);
        if(!foundPrinter)
        {
            TextView h_idbox = (TextView) findViewById(R.id.idbox);
            TextView h_printerLoc= (TextView) findViewById(R.id.printerLoc);
            TextView h_funFact= (TextView) findViewById(R.id.funFact);
            h_idbox.setText("Find a Printer!");
            h_printerLoc.setText("Find a Printer!");
            h_funFact.setText("Find a Printer!");
            showMap(false);
            bruh.setText("SHOW DETAIL");
        }
        else if(detailed)
        {
            detailed = false;
            showMap(false);
            bruh.setText("SHOW DETAIL");
        }
        else
        {
            detailed = true;
            bruh.setText("HIDE DETAIL");
            ImageView d_printer = (ImageView) findViewById(R.id.printerPic);
            String e = printerList.get(foundID).pictureID;
            d_printer.setImageResource(getResources().getIdentifier(e, "drawable", getPackageName()));
        }

    }
    //-----------------------------------------------------------------------------------------------------------End UI FN
}
