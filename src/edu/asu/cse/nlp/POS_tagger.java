package edu.asu.cse.nlp;

import java.util.HashMap;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;

import edu.stanford.nlp.trees.Tree;

public class POS_tagger {
	
	private String subject;
	private String question;
	private String url;
	private static HashMap<String, String> URI_map = new HashMap<String, String>();
	
	static
	{
		URI_map.put("Hale Bopp comet", "Comet_Hale-Bopp");
		URI_map.put("Rhodes scholars", "Rhodes_Scholarship");
		URI_map.put("agouti", "Common_agouti");
		URI_map.put("Black Panthers", "Black_Panther_Party");
		URI_map.put("prions", "Prion");
		URI_map.put("the band Nirvana", "Nirvana_(band)");
		URI_map.put("cataract", "Cataract");
		URI_map.put("boxer Floyd Patterson", "Floyd_Patterson");
		URI_map.put("architect Frank Gehry", "Frank_Gehry");
		URI_map.put("Harlem Globe Trotters", "Harlem_Globetrotters");
		URI_map.put("Abercrombie and Fitch", "Abercrombie_&_Fitch");
		URI_map.put("Tale of Genji", "The_Tale_of_Genji");
		URI_map.put("minstrel Al Jolson", "Al_Jolson");
		URI_map.put("Wiggles", "The_Wiggles");
		URI_map.put("Chester Nimitz", "Chester_W._Nimitz");
		URI_map.put("Nobel prize", "Nobel_Prize");
		URI_map.put("Bashar Assad", "Bashar_al-Assad");
		URI_map.put("Cassini space probe", "Cassini_Huygens");
		URI_map.put("Conde Nast", "Cond__Nast");
		URI_map.put("Eileen Marie Collins", "Eileen_Collins");
		URI_map.put("Liberty Bell 7", "Mercury-Redstone_4");
		URI_map.put("International Finance Corporation (IFC)", "International_Finance_Corporation");
		URI_map.put("philanthropist Alberto Vilar", "Alberto_Vilar");
		URI_map.put("senator Jim Inhofe", "Jim_Inhofe");
		URI_map.put("Berkman Center for Internet and Society", "Berkman_Center_for_Internet_&_Society");
		URI_map.put("boll weevil", "Boll_weevil");
		URI_map.put("space shuttles", "Space_Shuttle");
	}
	
	
	public POS_tagger(String subject, String question) {
		this.subject = subject;
		this.question = question;
		

	}
	
//	    public static void main(String args[]){
//	        String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
//	        if (args.length > 0) {
//	          parserModel = args[0];
//	        }
//	        LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
//
//	        if (args.length == 0) {
//	          demoAPI(lp);
//	        } else {
//	          String textFile = (args.length > 1) ? args[1] : args[0];
//	          demoDP(lp, textFile);
//	        }
//	    }
	    
	    public String getResult()
	    {
    		setSubjectPageURI();
    		SentenceParser parser = new SentenceParser();
    		parser.setProcessedTree(question);
    		
	        return getResultForQuestion(parser);
	    }
	    	
	    public void setSubjectPageURI()
	    {
	    	System.out.println("this is here");
	    	String URI;
	    	
	    	if(URI_map.containsKey(subject)){
	    		
	    		URI = URI_map.get(subject);
	    	}
	    	else{
	    		
	    		URI = subject.replace(" ", "_");
	    	}
	    	
    		url = "<http://dbpedia.org/resource/";
    		url = url + URI + ">";
    		
    		
	    }
	    
	    public String getResultForQuestion(SentenceParser parser){
	    	
	    	String whQuestion = parser.getWhList().get(0).toString();
	    	
	    	String result= "";
	    	
	    	if(whQuestion.equals("When")){
	    		
	    		System.out.println("Inside When");
	    		
	    		result = getWhenResults(parser.getVerbList());
	    		
	    	}
	    	return result;
	    }
	    
	    public String getWhenResults(List<Tree> verbList){
	    	
	    	//String subject = "James Dean";

    		String sparql_query="";
    		String result;
	    	
	    	if(verbList.get(0).toString().equals("born")){
	    		
	    		String query = "SELECT ?variable WHERE {";
	    		String query2 = " dbp:dateOfBirth ?variable }";
	    		
	    		sparql_query = query + url + query2;
	    		System.out.println(sparql_query);
	    		return getSpaqrlQueryResponse(sparql_query);
	    		
	    		
	    	}
	    	
	    	if(verbList.get(0).toString().equals("die")){
	    		
	    		String query = "SELECT ?variable WHERE {";
	    		String query2 = " dbp:dateOfDeath ?variable}";
	    		
	    		sparql_query = query + url + query2;
	    		System.out.println(sparql_query);
	    		getSpaqrlQueryResponse(sparql_query);
	    	}
	    	else
	    	{
	    		
	    		return getResultFromAbstract();
	    	}
	    	
	    	return getSpaqrlQueryResponse(sparql_query);
	    }
	    
	    public String getSpaqrlQueryResponse(String sparql_query)
	    {
	    	String stringQuery = "PREFIX dbo: <http://dbpedia.org/ontology/> " +
	    	        "PREFIX dbp: <http://dbpedia.org/property/>" +
	    	        "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> " 
	    	        +"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+ sparql_query;
	    	System.out.println(stringQuery);
	    	Query query = QueryFactory.create(stringQuery);
	    	QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);

	    	ResultSet results = qexec.execSelect();
	    	QuerySolution soln = results.nextSolution();
	    	Literal l = (Literal) soln.get("variable");
	    	qexec.close() ;
	    	return l.getString();
	    }
	    public String getResultFromAbstract(){
	    	StringBuilder finalQuery = new StringBuilder();
	    	String query1 = "SELECT ?variable WHERE {";
    		String query2 = " dbo:abstract ?variable ";
    		String filter = "FILTER(langMatches(lang(?variable),\"EN\"))}";

	    	finalQuery.append(query1).append(url).append(query2).append(filter);
	    	String query = finalQuery.toString();
	    	
	    	String abs = getSpaqrlQueryResponse(query);
	    	String[] abstractLines = abs.split("\\.\\s");
	    	
	    	SentenceParser parser = new SentenceParser();
	    	for(String s : abstractLines){
	    		parser.setProcessedTree(s);
	    		System.out.println(parser.getNounPhrase());
	    		//System.out.println(question + "\n");
	    	}
	    	return "";
	    }
	    
	    
	}


