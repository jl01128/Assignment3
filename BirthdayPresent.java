//Jackie Lin 
//COP4520 - CONCEPTS PARALLEL DISTRIBUTED
// 04/20/2023

import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//each present 
class Present {
    //each present has a tag
    int tag;

    public Present(int tag) {
        this.tag = tag;
    }

}

public class BirthdayPresent {
    //number of presents received from guests
    private static final int TOTAL_PRESENTS = 500000;
    //TOTAL number of servants
    private static final int TOTAL_SERVANTS = 4;
    
    private static final int MAX_ITERATIONS_PER_SERVANT = 10000;

    public static void main(String[] args) throws InterruptedException {
        //start of our timer
        long start = System.currentTimeMillis();

        //initialize linked list to store presents
        LinkedList<Present> presents = new LinkedList<>();

        //track number of written thank you letters
        AtomicInteger notesWritten = new AtomicInteger(0);

        //track number of the number tags checked
        AtomicInteger tagsChecked = new AtomicInteger(0);

        //total servant num thread pool
        ExecutorService executor = Executors.newFixedThreadPool(TOTAL_SERVANTS);

        //random number generator
        Random rand = new Random();

        //List contains NUM_PRESENT amount of shuffled presents tags
        List<Integer> shuffledTags = IntStream.rangeClosed(1, TOTAL_PRESENTS).boxed().collect(Collectors.toList());
        Collections.shuffle(shuffledTags);

        //Set access to the presentList with ReentrantLock
        Lock presentLock = new ReentrantLock();

        //for loop index
        int i = 0;
        
        //iterate through every servant and add their given task to thread pool
        for (i = 0; i < TOTAL_SERVANTS; i++) {
            
            executor.submit(() -> {
                //iteration per servant
                int j = 0;

                // Loop through a max num of iterations per servant while both shuffledTags or present list are not empty
                while (j < MAX_ITERATIONS_PER_SERVANT && (!shuffledTags.isEmpty() || !presents.isEmpty())) {

                    //Randomly generate our action from 0-2 a random action 
                    int action = rand.nextInt(3);

                    //add present action
                    if (action == 0) {
                        presentLock.lock();
                        try {
                            //Add a present to present list with the next tag from suffleTag list when there are still shuffled tags remaining, 
                            if (!shuffledTags.isEmpty()) {
                                int tag = shuffledTags.remove(0);
                                //index variable
                                int index = 0;
                                //loop through suffletag list
                                while (index < presents.size() && presents.get(index).tag < tag) {
                                    index++;
                                }
                                presents.add(index, new Present(tag));
                               
                            }
                        } finally {
                            presentLock.unlock();
                        }
                    }
                    //remove present and send thank you note 
                    else if (action == 1) {
                        //set out lock
                        presentLock.lock();
                        try {
                            //Remove the first present from present List and increment counter for notesWritten, if our presentList isn't empty
                            if (!presents.isEmpty()) {
                                Present removedPresent = presents.removeFirst();
                                notesWritten.incrementAndGet();
                            }
                        } finally {
                            //unlock
                            presentLock.unlock();
                        }
                    } 
                    //check if present exists
                    else {
                        //generate random num within our current presents tags to check
                        int randTag = rand.nextInt(TOTAL_PRESENTS) + 1;
                        presentLock.lock();

                        try {
                            //See if our present List holds a present with our given random tag
                            boolean contains = presents.stream().anyMatch(p -> p.tag == randTag);
                            if (contains) {
                                tagsChecked.incrementAndGet();
                            }
                        } finally {
                            //unlock
                            presentLock.unlock();
                        }
                    }
                    //Added a short delay as realistics portrayal of servants doing this task
                    try {
                        Thread.sleep(1);
                        j++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
        long end = System.currentTimeMillis();

        System.out.println("Total presents checked: " + (TOTAL_PRESENTS - shuffledTags.size()));
        System.out.println("Total \"Thank you\" notes written: " + notesWritten.get());
        System.out.println("Total checked tags: " + tagsChecked.get());
        //Time taken to execute
        System.out.println("Time: " + (end - start) + " ms");
    }
}
