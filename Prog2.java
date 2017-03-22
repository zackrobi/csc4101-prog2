/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package prog2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ZackRobi
 */
public class Prog2 {
    
    static class LRU {
     private int noPageReferences, noPageMisses, pageMissTime, pageWriteTime, cacheSize;
     private String fileName;
     private ArrayList<Line> frames, recentlyUsedValues;
     
     public LRU(int cacheSize, String fileName) throws IOException{
         this.noPageReferences = 0;
         this.fileName = fileName;
         this.noPageMisses = 0;
         this.pageMissTime = 0;
         this.pageWriteTime = 0;
         this.cacheSize = cacheSize;
         this.recentlyUsedValues = new ArrayList<>();
         this.frames = new ArrayList<>();
                 
         
         FileInputStream fstream;
         try {
            fstream = new FileInputStream(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                String[] lne = strLine.split(" ");
                Line line = new Line(lne[0], lne[1]);
                noPageReferences++;
                //Checking for page hit (lines 51-58)
               boolean found = false;
                for (Line val : frames) {
                    if (val.getPageNumber().equals(line.getPageNumber())) { 
                        found = true;
                        break;
                        }
                }
                if (!found){
                   noPageMisses++;
                   pageMissTime+=5;
                   //Checks to see if there are too many stored frames. If there are, find the least recently used frame and swap it with the target frame. 
                   //If not, then add the target frame to the list.
                   if (frames.size() == cacheSize){
                       int i = 0;
                       Line lru = recentlyUsedValues.get(i);
                       while (!frames.contains(lru))
                           lru = recentlyUsedValues.get(i++);
                       //If the page has been written, then increase pageWriteTime by 10.
                       if (lru.getReadOrWrite().equals("W"))
                           pageWriteTime+=10;
                       //Adds the frame to its respective list
                       frames.set(frames.indexOf(lru), line);
                   }
                   else
                       frames.add(line);
                }
                //Updating recently UsedValues
                for (Line val : recentlyUsedValues) {
                    if (val.getPageNumber().equals(line.getPageNumber())) { 
                        found = true;
                        break;
                    }
                }
                if (!found)
                    recentlyUsedValues.remove(line);
                recentlyUsedValues.add(line);
            }
            System.out.println("Total page references: " + noPageReferences);
            System.out.println("Total page misses: " + noPageMisses);
            System.out.println("Total time units for page misses: " + pageMissTime);
            System.out.println("Total time units for page writing back: " + pageWriteTime);
            //Print here
            br.close();
        } catch (FileNotFoundException ex) {
             Logger.getLogger(Prog2.class.getName()).log(Level.SEVERE, null, ex);
         }
     }
    }
    
    
    static class Clock {
        private int noPageReferences, noPageMisses, pageMissTime, pageWriteTime, cacheSize;
        private String fileName;
        private ArrayList<Line> frames;
        private ClockObject hand, clock;
        
        public Clock(int cacheSize, String fileName) throws IOException {
            this.noPageReferences = 0;
            this.fileName = fileName;
            this.noPageMisses = 0;
            this.pageMissTime = 0;
            this.pageWriteTime = 0;
            this.cacheSize = cacheSize;
            this.hand = this.clock = new ClockObject();
            this.frames = new ArrayList<>();
            //Creates a circular linked list of empty nodes
            for (int i = 0; i < cacheSize -1; i++){
                clock.next = new ClockObject();
                clock = clock.next;
            }
            clock.next = hand;
            FileInputStream fstream = new FileInputStream(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                String[] lne = strLine.split(" ");
                Line line = new Line(lne[0], lne[1]);
                noPageReferences++;
               boolean found = false;
                for (Line val : frames) {
                    if (val.getPageNumber().equals(line.getPageNumber())) { 
                        found = true;
                        break;
                        }
                }
                if (!found){
                   noPageMisses++;
                   hand.value = line;
                   hand = hand.next;
                   pageMissTime+=5;
                   if (frames.size() == cacheSize){
                        int i = frames.indexOf(hand.value);
                        if (hand.value.getReadOrWrite().equals("W"))
                           pageWriteTime+=10;
                        frames.set(i, line);
                    }
                
                else
                    frames.add(line);
                }  
            }
            System.out.println("Total page references: " + noPageReferences);
            System.out.println("Total page misses: " + noPageMisses);
            System.out.println("Total time units for page misses: " + pageMissTime);
            System.out.println("Total time units for page writing back: " + pageWriteTime);
        }
    } 
    static class Line {
        private String readOrWrite, pageNumber;
        public Line(String readOrWrite, String pageNumber){
            this.readOrWrite = readOrWrite;
            this.pageNumber = pageNumber;
        }
        
        public String getReadOrWrite(){
            return readOrWrite;
        }
        
        public String getPageNumber(){
            return pageNumber;
        }
    }
 
    static class ClockObject {
        Line value;
        ClockObject next;
        
        public boolean hasNext(){
            if (next == null)
                return false;
            return true;
        }
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String alg = args[0];
        int noPageFrames = Integer.parseInt(args[1]);
        String fileName = args[2];
        LRU replace1;
        Clock replace2;
        if (alg.equals("LRU"))
            replace1 = new LRU(noPageFrames, fileName); 
        if (alg.equals("CLOCK"))
            replace2 = new Clock(noPageFrames, fileName); 
    }
    
}
