package edu.stanford.moonshot;

import java.io.*;
import java.net.MalformedURLException;

import edu.knowitall.ollie.Ollie;
import edu.knowitall.ollie.OllieExtraction;
import edu.knowitall.ollie.OllieExtractionInstance;
import edu.knowitall.tool.parse.StanfordParser;
import edu.knowitall.tool.parse.graph.DependencyGraph;

/** This is an example class that shows one way of using Ollie from Java. */
public class News {
    // the extractor itself
    private Ollie ollie;

    // the parser--a step required before the extractor
    private StanfordParser stanfordParser;

    // the path of the malt parser model file
    private static final String MALT_PARSER_FILENAME = "engmalt.linear-1.7.mco";

    public News() throws MalformedURLException {
        // initialize MaltParser
        scala.Option<File> nullOption = scala.Option.apply(null);
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

    public static void runApp() {
        try {
            System.out.println(News.class.getResource("/logback.xml"));
            // initialize
            News ollieWrapper = new News();

            // extract from a single sentence.
            String sentence = "Obama met with lawmakers today to discuss a pending war with Hitler.";
            /*BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                sentence = br.readLine();
            }
            catch (IOException e) {
                System.out.println("Something funky");
            }*/
            System.out.println("Extracting relations from : \n-----------\n"+sentence+"\n-----------");
            Iterable<OllieExtractionInstance> extrs = ollieWrapper.extract(sentence);

            // print the extractions.
            for (OllieExtractionInstance inst : extrs) {
                OllieExtraction extr = inst.extr();
                System.out.println(extr.arg1().text()+"\t"+extr.rel().text()+"\t"+extr.arg2().text()+"\t:"+extr.openparseConfidence());
                // System.out.println("Enabler: "+extr.enabler());
                // System.out.println("Attribution: "+extr.attribution());
                System.out.println(extr.toString());
            }
        }
        catch (MalformedURLException e) {
        }
    }
}
