package edu.stanford.moonshot;

import java.util.*;
import java.io.*;

public class Engine {

    QueryRunner qr;
    TripletExtractor te;

    public Engine() {
        qr = new QueryRunner();
        te = new TripletExtractor();
    }

    public void test(String testString) {
        System.out.println("Extracting: "+testString);
        ArrayList<MappedTriplet> results = extract(testString);
        for (MappedTriplet result : results) {
            System.out.println("*");
            System.out.println(result.arg1Mapping+"\t"+result.rel+"\t"+result.arg2Mapping);
            System.out.println("*");
        }
    }

    public static void main(String args[]) {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));        
        Engine eng = new Engine();

        while (true) {
            try {
                System.out.println("Enter a string to extract information from:");
                String testString = br.readLine();
                eng.test(testString);
            }
            catch (IOException e) {
                System.out.println("IOException occured. That's weird");
                e.printStackTrace();
            }
        }
    }

    public ArrayList<MappedTriplet> extract(String text) {

        // Get the triplets out of the text
        
        ArrayList<MappedTriplet> mts = te.extractTriplets(text);

        // Map tripelts to the database

        for (MappedTriplet mt : mts) {
            mt.mapToDatabase(qr);
        }

        return mts;
    }
}
