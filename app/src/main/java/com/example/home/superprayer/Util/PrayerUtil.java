package com.example.home.superprayer.Util;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.home.superprayer.Fragment.TimesFragment;
import com.example.home.superprayer.Model.NextPrayerEnum;
import com.example.home.superprayer.Model.PrayerModel;
import com.example.home.superprayer.Model.PrayerNextModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Home on 12/11/2017.
 *
 * This class is unused but is kept for possible later use
 *
 *
 *
 *
 */

public class PrayerUtil {



    private enum CurrentPrayer {FAJR,DUHR,ASR,MAGHRIB,ISHA,END}

    private PrayerNextModel getNextPrayer(PrayerModel model){

        PrayerNextModel nextModel = new PrayerNextModel(model);

        long nextPrayerTime;



        int fajr = convertPrayertoInt(model.getFajr24());
        int duhr = convertPrayertoInt(model.getDuhr24());
        int asr = convertPrayertoInt(model.getAsr24());
        int maghrib = convertPrayertoInt(model.getMaghrb24());
        int isha = convertPrayertoInt(model.getIsha24());


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date currentDate = new Date();
        String stringTime = sdf.format(currentDate);


        int absoluteNumberTime = convertPrayertoInt(stringTime);
        int times_array[] = {fajr,duhr,asr,maghrib,isha,absoluteNumberTime};

        Arrays.sort(times_array);

        for (int i = 0; i < times_array.length; i++) {

            Log.d("ARRAY TAG" , "  " + times_array[i]);

            if(times_array[i] == absoluteNumberTime){

                //   Log.d("LOOP TAG",  " Time : " + times_array[i] + "  current time  " + absoluteNumberTime + "  Next prayer imes  " + times_array[i + 1] );

                if(i == 5){
                    nextPrayerTime = 0;
                    nextModel.seteNextPrayer(NextPrayerEnum.END);
                    nextModel.setTimeUntilNextPrayer(0);
                    return nextModel;
                }
                int nextPrayer = times_array[i + 1];

                if(nextPrayer == fajr){
                    nextPrayerTime = timeUntilNextPrayer(model.getIsha24(),stringTime,model.getFajr24());
                    nextModel.seteNextPrayer(NextPrayerEnum.FAJR);
                    nextModel.setTimeUntilNextPrayer(nextPrayerTime);

                    return nextModel;
                }
                if(nextPrayer == duhr){
                    nextPrayerTime = timeUntilNextPrayer(model.getFajr24(),stringTime,model.getDuhr24());
                    nextModel.seteNextPrayer(NextPrayerEnum.DUHR);
                    nextModel.setTimeUntilNextPrayer(nextPrayerTime);

                    return nextModel;
                }
                if(nextPrayer == asr){
                    nextPrayerTime = timeUntilNextPrayer(model.getDuhr24(),stringTime,model.getAsr24());
                    nextModel.seteNextPrayer(NextPrayerEnum.ASR);
                    nextModel.setTimeUntilNextPrayer(nextPrayerTime);
                    return nextModel;
                }
                if(nextPrayer == maghrib){
                    nextPrayerTime = timeUntilNextPrayer(model.getAsr24(),stringTime,model.getMaghrb24());
                    nextModel.seteNextPrayer(NextPrayerEnum.MAGHRIB);
                    nextModel.setTimeUntilNextPrayer(nextPrayerTime);
                    return nextModel;
                }
                if(nextPrayer == isha){
                    nextPrayerTime = timeUntilNextPrayer(model.getMaghrb24(),stringTime,model.getIsha24());
                    nextModel.seteNextPrayer(NextPrayerEnum.ISHA);
                    nextModel.setTimeUntilNextPrayer(nextPrayerTime);
                    return nextModel;
                }
            }

        }

        return null;

        //  Log.d("CURRENT TIME" , " Time is  " + stringTime);
        //   Log.d("CURRENT TIME" , " Time is  " + absoluteNumberTime);
        //  Log.d("Prayer Time Stamps", "Fajr" +  "\t" + fajr + "\n" + "Duhr" + "\t" + duhr + "\n" + "Asr" + "\t" + asr +" \n" + "Maghrib" +"\t" +maghrib + " \n " +"isha" + "\n" + isha);
    }

    private long timeUntilNextPrayer(String lastPrayer , String currentTime, String nextPrayerTime){

        SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
        try {

            Date date1 = sdf.parse(lastPrayer);
            Date date2 = sdf.parse(nextPrayerTime);
            Date currentDate = sdf.parse(currentTime);

            long diff =  (date2.getTime() - date1.getTime()) / 1000;
            long diff2 = (date2.getTime() - currentDate.getTime()) / 1000;
            Log.d("NEXT PRAYER TIME CALC" ,  "   " + lastPrayer + "  " + nextPrayerTime + "  " + diff);
            Log.d("NEXT PRAYER TIME CALC" ,  "   " + lastPrayer + "  " + nextPrayerTime + "  " + diff2);
            double difference = (double)diff;
            double difference2 = (double)diff2;
            double absoluteDiff = (difference2/difference) * 100;
            Log.d("Progress Tag", "  " +" diff1 " + " " + difference + "  " + difference2 + " "  + (difference2/difference) * 100);


            //convert from seconds to minutes
            return diff2 / 60;

        }catch (ParseException e){
            e.printStackTrace();
        }


        return 0;

    }

    public static int convertPrayertoInt(String time) {

        String[] timeSplit = time.split(":");
        String concat = (timeSplit[0] + timeSplit[1]);
        int absoluteTime = Integer.parseInt(concat);

        return absoluteTime;
    }

}
