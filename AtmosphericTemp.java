//Jackie Lin 
//COP4520 - CONCEPTS PARALLEL DISTRIBUTED
// 04/20/2023

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.*;

public class AtmosphericTemp {

    //total number of temperature sensors
    private static final int TOTAL_SENSORS = 8;
    //number of readings per hour
    private static final int HOURLY_READINGS = 60;
    //max temperature 
    private static final int MAX_TEMP = 70;
    //min temperature
    private static final int MIN_TEMP = -100;

    //store temp readings for every minute in concurrent hash map 
    private static final ConcurrentHashMap<Integer, AtomicInteger> tempReadings = new ConcurrentHashMap<>();
    
    //Cyclic barrier will initiate hourly report once all sensors have been reported
    private static final CyclicBarrier hourlyBarrier = new CyclicBarrier(TOTAL_SENSORS, new HourlyReporter());

    //hourly report class 
    private static class HourlyReporter implements Runnable {
        @Override
        public void run() {
            //array list for highest temps
            List<Integer> highestTemp = new ArrayList<>();
            //array list for lowest temps
            List<Integer> lowestTemperatures = new ArrayList<>();

            //Tracks biggest temp difference and when it happened
            int biggestTempDif = 0;
            //start point
            int biggestTempDif_Start = 0;
            //end point
            int biggestTempDif_End = 0;

            int i = 0;
            //iterate through every minute in an hour
            for (i = 0; i < HOURLY_READINGS; i++) {
                //List stores temp readings for each sensor for the given minute
                List<Integer> temperatures = new ArrayList<>();
                int j = 0;
                //loop through each sensor
                for (j = 0; j < TOTAL_SENSORS; j++) {
                    //get temp reading
                    temperatures.add(tempReadings.get(i * TOTAL_SENSORS + j).get());
                }
                //find highest temp for given case
                int max = Collections.max(temperatures);
                //find lowest temp for given case
                int min = Collections.min(temperatures);

                // Add both highest and lowest temp to their arrays
                highestTemp.add(max);
                lowestTemperatures.add(min);

                //find difference between highest and lowest temp
                int tempDif = max - min;
                
                //set new highest temp dif and when it happened if greater
                if (tempDif > biggestTempDif) {
                    biggestTempDif = tempDif;
                    biggestTempDif_Start = i;
                    biggestTempDif_End = i;
                }
            }

            //print top 5 highest temperatures for that hour
            System.out.println("Top 5 highest temperatures: " + getTopNUnique(highestTemp, 5));
            //print top 5 lowest temperatures for that hour
            System.out.println("Top 5 lowest temperatures: " + getTopNUnique(lowestTemperatures, 5, false));
            //print largest temp difference and when it occured
            System.out.println("Largest temperature difference " + biggestTempDif + "F observed from minute " + biggestTempDif_Start +
                    " to minute " + biggestTempDif_End);

            System.exit(0); 
        }

        //functions gets top n unique temperatures from a list
        private List<Integer> getTopNUnique(List<Integer> temp, int n) {
            return getTopNUnique(temp, n, true);
        }

        //function gets top n unique temp from list, either highest
        private List<Integer> getTopNUnique(List<Integer> temp, int n, boolean highest) {
           
            Set<Integer> uniqueTemp = new HashSet<>(temp);
            List<Integer> sortedTemp = new ArrayList<>(uniqueTemp);
           
            Collections.sort(sortedTemp);
            if (highest) {
                Collections.reverse(sortedTemp);
            }
            return sortedTemp.subList(0, Math.min(n, sortedTemp.size()));
        }
     }

    //Main method
    public static void main(String[] args) {

        //declare fixed thread pool executor and have  number of threads equal to total number of sensors
        ExecutorService executor = Executors.newFixedThreadPool(TOTAL_SENSORS);
        
        int i= 0;
        //iterate through each sensor and send a task to executor
        for (i = 0; i < TOTAL_SENSORS; i++) {

            //current sensor id that is used in given task
            final int sensorID = i;

            //Send task to executor to get temp readings for the current sensor of the given hour
            executor.submit(() -> {
                //rand generator generator random temp readings
                Random rand = new Random();

                int j = 0;
                //iterate each minute of the hour and get a temp reading for  each sensor for each given minute
                for (j = 0; j < HOURLY_READINGS; j++) {

                    //get rand temp between min and max temperatures
                    int temp = rand.nextInt(MAX_TEMP - MIN_TEMP + 1) + MIN_TEMP;
                    //add our temp reading to our concurrent hash map for the current sensor and minute
                    tempReadings.put(sensorID * HOURLY_READINGS + j, new AtomicInteger(temp));
                }
                //waits for all sensors to produce temp reading for the current minute
                try {
                    hourlyBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
        //shut down executor once all task are done
        executor.shutdown();
    }
}

