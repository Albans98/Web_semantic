package tools;
 
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.util.FileManager;
import java.io.IOException;
import java.io.OutputStream;

public class JenaEngine {
    static private String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
 
    ///////////////////////////////////////////////////////////////////////////////////////////
   //////////////////////////////////////Model////////////////////////////////////////////////
   ///////////////////////////////////////////////////////////////////////////////////////////
    static public Model readModel(String inputDataFile) {
		//create an empty model
        Model model = ModelFactory.createDefaultModel();

        // use the FileManager to find the input file
        InputStream in = FileManager.get().open(inputDataFile);
        if (in == null) {
            System.out.println("Ontology file: " + inputDataFile + " not found");
            return null;
        }

        // read the RDF/XML file
        model.read(in, "");
        try {
            in.close();
        } catch (IOException e) {
            return null;
        }
        return model;
    }
  //https://jena.apache.org/documentation/javadoc/jena/org/apache/jena/rdf/model/Model.html
    static public void addRDF(String DataFile,Model model ) {
     //cree un model avec le ficher rdf et l'ajouter sur notre model
      Model modelRDF = ModelFactory.createDefaultModel();
      InputStream in = FileManager.get().open(DataFile);
      if (in == null) {
          System.out.println("Ontology file: " + DataFile + " not found");
      }
      
      //model.read(in, ""); //OR ca marche aussi  
      modelRDF.read(in, "");
      try {
          in.close();
      } catch (IOException e) {
      }
      model.add(modelRDF);
  }
    static public Model readInferencedModelFromRuleFile(Model model, String inputRuleFile) {
        InputStream in = FileManager.get().open(inputRuleFile);
        if (in == null) {
            System.out.println("Rule File: " + inputRuleFile + " not found");
            return null;
        } else {
            try {
                in.close();
            } catch (IOException e) {
                return null;
            }
        }

        List rules = Rule.rulesFromURL(inputRuleFile);
        GenericRuleReasoner reasoner = new GenericRuleReasoner(rules);
        reasoner.setDerivationLogging(true);
        reasoner.setOWLTranslation(true);               // not needed in RDFS case
        reasoner.setTransitiveClosureCaching(true);
        InfModel inf = ModelFactory.createInfModel(reasoner, model);
        return inf;
    }

     ///////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////Qurey////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Executer une requete
     * @param args
     * + Entree: l'objet model Jena avec une chaine des caracteres SparQL
     * + Sortie: le resultat de la requete en String
     */
    static public String executeQuery(Model model, String queryString) {
        Query query = QueryFactory.create(queryString);
        // No reasoning
        // Execute the query and obtain results
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        OutputStream output = new OutputStream() {

            private StringBuilder string = new StringBuilder();

            @Override
            public void write(int b) throws IOException {
                this.string.append((char) b);
            }

            //Netbeans IDE automatically overrides this toString()
            public String toString() {
                return this.string.toString();
            }
        };

        ResultSetFormatter.out(output, results, query);
        return output.toString();
    }
    /**
     * Executer un fichier d'une requete
     * @param args
     * + Entree: l'objet model Jena avec une chaine des caracteres SparQL
     * + Sortie: le resultat de la requete en String
     */
    static public String executeQueryFile(Model model, String filepath) {
        File queryFile = new File(filepath);
        // use the FileManager to find the input file
        InputStream in = FileManager.get().open(filepath);
        if (in == null) {
            System.out.println("Query file: " + filepath + " not found");
            return null;
        } else {
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return null;
            }
        }
        String queryString = FileTool.getContents(queryFile);
        return executeQuery(model, queryString);
    }
    /**
     * Executer un fichier d'une requete avec le parametre
     * @param args
     * + Entree: l'objet model Jena avec une chaine des caracteres SparQL
     * + Sortie: le resultat de la requete en String
     */
    static public String executeQueryFileWithParameter(Model model, String filepath, String parameter) {
        File queryFile = new File(filepath);
        // use the FileManager to find the input file
        InputStream in = FileManager.get().open(filepath);
        if (in == null) {
            System.out.println("Query file: " + filepath + " not found");
            return null;
        } else {
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return null;
            }
        }
        String queryString = FileTool.getContents(queryFile);
        queryString = queryString.replace("%PARAMETER%", parameter);
        return executeQuery(model, queryString);
    }
     
    ///////////////////////////////////////////////////////////////////////////////////////////
   /////////////////////////////////////read the value ////////////////////////////////////////
   ///////////////////////////////////////////////////////////////////////////////////////////
	//read the value of property of type ObjectType
	// read the value of property of type DataType from a resource
    /* NOT USED
	static public void readRsDataType(Model model, String namespace,
			Resource rs, String propertyName) {
		Property p = model.getProperty(namespace + propertyName);
		if (p != null) {
			if (rs.getProperty(p) != null) {
				System.out.println(propertyName + ": "
						+ rs.getProperty(p).getString());
			}
		}
	}
	
    static public void readObjectType(Model model, String namespace,
			String objectName, String propertyName) {

		Resource rs = model.getResource(namespace + objectName);
		Property p = model.getProperty(namespace + propertyName);
		if ((rs != null) && (p != null)) {
			StmtIterator it = rs.listProperties(p);
			if (!it.hasNext()) {
				System.out.println(objectName + " " + propertyName + ": "
						+ null);
			} else {
				System.out.println(objectName + " " + propertyName + ": ");
				while (it.hasNext()) {

					Statement s = it.next();
					Resource re = s.getResource();
					readRsDataType(model, namespace, re, "name");
				}
			}
		}
	}*/

    static public List<Resource> getALLsatation(Model model, String namespace) 
	{
		List<Resource> stations = new ArrayList<Resource>();
		Resource rs = model.getResource(namespace + "station");
		Property p = model.getProperty(RDF + "type");
		ResIterator it = model.listSubjectsWithProperty(p,rs);
		while (it.hasNext()) 
		{
					Resource re = it.next();
					stations.add(re);		
		}
		return stations;
	}
    static public double getdistance(Model model, String namespace,double lat,double lon,Resource rs ) {
		Property p = model.getProperty(namespace + "coordinate");
		double coord[]=new double[2];
		
  		if ((rs != null) && (p != null)) {
  			StmtIterator it = rs.listProperties(p);
  			for (int i = 0; i < coord.length ; i++){

				Statement s = it.next();
				coord[i]=s.getDouble();
				//System.out.println(coord[i]);
				
			}
  		}
  	
  		return Math.sqrt(Math.pow(coord[0]-lon,2)+Math.pow(coord[1]-lat,2));
  		
  	}
	// read the value of property of type DataType
	static public void readDataType(Model model, String namespace,String objectName, String propertyName) {
		Property p = model.getProperty(namespace + propertyName);
  		Resource rs = model.getResource("http://www.owl-ontologies.com/stations-velos.owl/" + objectName);
  		if ((rs != null) && (p != null)) {
  			if (rs.getProperty(p) != null) {
  				System.out.println(propertyName + " : "
  						+ rs.getProperty(p).getString());
  			} else {
  				System.out.println(propertyName + " : "
  						+ null);
  			}
  		}
  	}

   
}
