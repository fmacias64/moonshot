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

public class Index
{
    public static void main(String[] args)
    {
        // Direct way: Make a TDB-back Jena model in the named directory.
        String directory = "yago-jena" ;
        Model model = TDBFactory.createModel(directory);

        try {
            IndexWriter indexWriter = IndexWriterFactory.create(FSDirectory.open(new File(directory+"/larq")));
            IndexBuilderString larqBuilder = new IndexBuilderString(indexWriter);
            StmtIterator sIter = model.listStatements();
            for (int i = 0; sIter.hasNext(); i++) {
                larqBuilder.indexStatement(sIter.next());
                System.out.print("Indexed: "+i+"\r");
                if (i % 100000 == 99999) {
                    indexWriter.commit();
                }
            }

            IndexLARQ index = larqBuilder.getIndex();

            larqBuilder.closeWriter();

            LARQ.setDefaultIndex(index);

            NodeIterator nIter = index.searchModelByIndex("+Obama");
            while (nIter.hasNext()) {
                Literal lit = (Literal)nIter.nextNode();
                System.out.println(lit);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

