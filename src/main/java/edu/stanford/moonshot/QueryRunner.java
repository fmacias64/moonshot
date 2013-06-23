/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.stanford.moonshot;

import com.hp.hpl.jena.query.*;

import com.hp.hpl.jena.tdb.TDBFactory;

import org.apache.jena.larq.IndexWriterFactory;
import org.apache.jena.larq.IndexBuilderString;
import org.apache.jena.larq.IndexLARQ;
import org.apache.jena.larq.LARQ;
import org.apache.jena.atlas.lib.StrUtils;

// The ARQ application API.
import org.apache.jena.atlas.io.IndentedWriter ;

import com.hp.hpl.jena.query.Query ;
import com.hp.hpl.jena.query.QueryExecution ;
import com.hp.hpl.jena.query.QueryExecutionFactory ;
import com.hp.hpl.jena.query.QueryFactory ;
import com.hp.hpl.jena.query.QuerySolution ;
import com.hp.hpl.jena.query.ResultSet ;
import com.hp.hpl.jena.rdf.model.Literal ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.rdf.model.ModelFactory ;
import com.hp.hpl.jena.rdf.model.RDFNode ;
import com.hp.hpl.jena.rdf.model.Resource ;
import com.hp.hpl.jena.rdf.model.Statement ;
import com.hp.hpl.jena.rdf.model.StmtIterator ;
import com.hp.hpl.jena.rdf.model.NodeIterator ;

import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.IndexWriter;

import java.io.*;
import java.util.*;

/** Example of creating a TDB-backed model.
 *  The preferred way is to create a dataset then get the mode required from the dataset.
 *  The dataset can be used for SPARQL query and update
 *  but the Model (or Graph) can also be used.
 *  
 *  All the Jena APIs work on the model.
 *   
 *  Calling TDBFactory is the only place TDB-specific code is needed.
 */

public class QueryRunner
{
    Model model;

    public QueryRunner()
    {

        // Load a TDB backed model

        String directory = "yago-jena" ;
        model = TDBFactory.createModel(directory);

        // Open the LARQ index

        try {
            IndexWriter indexWriter = IndexWriterFactory.create(FSDirectory.open(new File(directory+"/larq")));
            IndexBuilderString larqBuilder = new IndexBuilderString(indexWriter);
            IndexLARQ index = larqBuilder.getIndex();
            larqBuilder.closeWriter();
            LARQ.setDefaultIndex(index);
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public static void main(String args[]) {
        QueryRunner qr = new QueryRunner();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));        
        
        while (true) {
            try {
                System.out.println("Enter SPARQL query against the database:");
                String testString = br.readLine();
                ArrayList<RDFNode> results = qr.runQuery(testString);
                for (RDFNode result : results) {
                    System.out.println(result);
                }
            }
            catch (IOException e) {
                System.out.println("IOException occured. That's weird");
                e.printStackTrace();
            }
        }
    }

    /*
     * Figure out the "semantic coherence" of two ideas, as in DEANNA paper
     */
    public double jaccardCoefficient(RDFNode n1, RDFNode n2) {
        double intersectCount = runQuery("SELECT (count(DISTINCT *) AS ?x) WHERE { ?y :linksTo "+n1+" . ?y :linksTo "+n2+" }").get(0).asLiteral().getDouble();
        double unionCount = runQuery("SELECT (count(DISTINCT *) AS ?x) WHERE { { ?y :linksTo "+n1+" } UNION { ?y :linksTo "+n2+" } }").get(0).asLiteral().getDouble();
        return intersectCount / unionCount;
    }

    public ArrayList<RDFNode> matchLabels(String phrase) {
        phrase = phrase.replaceAll(" ","+");
        return runQuery("SELECT DISTINCT ?x WHERE { ?z pf:textMatch ( '+"+phrase+"' 0.5 10 ) . ?x rdfs:label ?z }");
    }

    public ArrayList<RDFNode> runQuery(String queryString) {

        String s = prepareQuery(queryString);
        
        Query query = QueryFactory.create(s) ;
        
        // Create a single execution of this query, apply to a model
        // which is wrapped up as a Dataset
        
        QueryExecution qexec = QueryExecutionFactory.create(query, model) ;

        // Prepare the arraylist to return the list of nodes

        ArrayList<RDFNode> nodeList = new ArrayList<RDFNode>();

        try {

            // Assumption: it's a SELECT query.

            ResultSet rs = qexec.execSelect();

            // The order of results is undefined. 

            while (rs.hasNext())
            {
                QuerySolution rb = rs.nextSolution() ;

                // Get title - variable names do not include the '?' (or '$')

                RDFNode x = rb.get("x") ;
                nodeList.add(x);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {

            // QueryExecution objects should be closed to free any system resources 

            qexec.close() ;
        }

        return nodeList;
    }

    private String prepareQuery(String query) {
        return "PREFIX : <http://yago-knowledge.org/resource/>"+
            " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
            " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
            " PREFIX pf: <http://jena.hpl.hp.com/ARQ/property#> "+
            query;
    }
}

