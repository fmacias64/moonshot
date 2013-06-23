package edu.stanford.moonshot;

import com.hp.hpl.jena.rdf.model.RDFNode;
import java.util.*;

public class MappedTriplet {

    // Original string data - for verification only

    String arg1;
    String rel;
    String arg2;
    double extractionConfidence;

    // Mapped RDFNode

    RDFNode arg1Mapping;
    RDFNode relMapping;
    RDFNode arg2Mapping;

    // Construct a triplet from text

    public MappedTriplet(String arg1, String rel, String arg2, double confidence) {
        this.arg1 = arg1;
        this.rel = rel;
        this.arg2 = arg2;
        this.extractionConfidence = extractionConfidence;
    }

    // Map the text onto our database

    public void mapToDatabase(QueryRunner qr) {
        ArrayList<RDFNode> arg1Mappings = qr.matchLabels(arg1);
        ArrayList<RDFNode> relMappings = qr.matchLabels(rel);
        ArrayList<RDFNode> arg2Mappings = qr.matchLabels(arg2);

        // Eventually we'll do a max-matching with jaccard coefficients on wikipedia inlinks
        // For now, we'll just grab the first off the stack

        arg1Mapping = arg1Mappings.get(0);
        relMapping = relMappings.get(0);
        arg2Mapping = arg2Mappings.get(0);
    }
}
