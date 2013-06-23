package edu.stanford.moonshot;

import java.io.*;
import java.util.*;

import edu.knowitall.ollie.Ollie;
import edu.knowitall.ollie.OllieExtraction;
import edu.knowitall.ollie.OllieExtractionInstance;
import edu.knowitall.tool.parse.StanfordParser;
import edu.knowitall.tool.parse.graph.DependencyGraph;

public class TripletExtractor {

    // the extractor itself

    private Ollie ollie;

    // the parser--a step required before the extractor

    private StanfordParser stanfordParser;

    public TripletExtractor() {

        // initialize the Stanford Parser

        stanfordParser = new StanfordParser();

        // initialize Ollie

        ollie = new Ollie();
    }

    /**
     * Gets Ollie extractions from a single sentence.
     * @param sentence
     * @return the set of ollie extractions
     */
    public Iterable<OllieExtractionInstance> extract(String sentence) {

        // parse the sentence

        DependencyGraph graph = stanfordParser.dependencyGraph(sentence);

        // run Ollie over the sentence and convert to a Java collection

        Iterable<OllieExtractionInstance> extrs = scala.collection.JavaConversions.asJavaIterable(ollie.extract(graph));
        return extrs;
    }

    public ArrayList<MappedTriplet> extractTriplets(String sentence) {

        // Make the extractions

        Iterable<OllieExtractionInstance> extrs = extract(sentence);

        // Prepare the arraylist

        ArrayList<MappedTriplet> mts = new ArrayList<MappedTriplet>();

        // Fill the arraylist

        for (OllieExtractionInstance inst : extrs) {
            OllieExtraction extr = inst.extr();
            MappedTriplet mt = new MappedTriplet(extr.arg1().text(), extr.rel().text(), extr.arg2().text(), extr.openparseConfidence());
            mts.add(mt);
        }
        return mts;
    }
}
