package edu.asu.cse.nlp;

import java.io.StringReader;
import java.util.List;

import edu.stanford.nlp.ie.machinereading.ResultsPrinter;
	import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

import java.util.ArrayList;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.sparql.resultset.ResultsFormat;
import org.apache.jena.sparql.util.ResultSetUtils;

public class POS_tagger {
	
	private String subject;
	private String question;
	
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
	    	String result ="";
	        String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";

	        LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);


	          

	          return demoAPI(lp);
	    }
	    	
	    	
	    
	    

	    
	    public String demoAPI(LexicalizedParser lp) {
	        String[] sent = { "This", "is", "an", "easy", "sentence", "." };
	        List<CoreLabel> rawWords = Sentence.toCoreLabelList(sent);
	        Tree parse = lp.apply(rawWords);
	        parse.pennPrint();
	        System.out.println();
	        
	        //String sent2 = "When did James Dean die ?";
	        TokenizerFactory<CoreLabel> tokenizerFactory =
	            PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
	        Tokenizer<CoreLabel> tok =
	            tokenizerFactory.getTokenizer(new StringReader(question));
	        List<CoreLabel> rawWords2 = tok.tokenize();
	        parse = lp.apply(rawWords2);

	        TreebankLanguagePack tlp = lp.treebankLanguagePack(); // PennTreebankLanguagePack for English
	        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	        GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
	        List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
	        System.out.println(tdl);
	        System.out.println();

	        // You can also use a TreePrint object to print trees and dependencies
	        TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
	        tp.printTree(parse);
	        return getPhrase(parse);
	    }
	    
	    public String getPhrase(Tree parse)
	    {

	        List<Tree> verbList=new ArrayList<Tree>();
	        List<Tree> nounList=new ArrayList<Tree>();
	        List<Tree> whList = new ArrayList<Tree>();
	        for (Tree subtree: parse){
	            
	          if(subtree.label().value().equals("VB") || subtree.label().value().equals("VBN")){
	            verbList.add(subtree.lastChild());
	          }
	          if(subtree.label().value().equals("NNP") || subtree.label().value().equals("NN")){
	              nounList.add(subtree.lastChild());
	          }
	          
	          if(subtree.label().value().equals("WRB")){
	              whList.add(subtree.lastChild());
	          }
	          
	        }
	          System.out.println(verbList);
	          System.out.println(nounList);
	          System.out.println(whList);
	          
	          return question_classifier(verbList, nounList, whList);
	    }
	    
	    public String question_classifier(List<Tree> verbList, List<Tree> nounList, List<Tree> whList){
	    	
	    	String whQuestion = whList.get(0).toString();
	    	
	    	String result= "";
	    	
	    	if(whQuestion.equals("When")){
	    		
	    		System.out.println("Inside When");
	    		
	    		result = when_questions(verbList);
	    		
	    	}
	    	return result;
	    }
	    
	    public String when_questions(List<Tree> verbList){
	    	
	    	//String subject = "James Dean";
    		subject = subject.replace(" ", "_");
    		String url = "<http://dbpedia.org/resource/";
    		url = url + subject + ">";
    		String sparql_query="";
	    	
	    	if(verbList.get(0).toString().equals("born")){
	    		
	    		String query = "SELECT ?variable WHERE {";
	    		String query2 = " dbp:dateOfBirth ?variable }";
	    		
	    		sparql_query = query + url + query2;
	    		System.out.println(sparql_query);
	    		
	    		
	    	}
	    	
	    	if(verbList.get(0).toString().equals("die")){
	    		
	    		String query = "SELECT ?variable WHERE {";
	    		String query2 = " dbp:dateOfDeath ?variable}";
	    		
	    		sparql_query = query + url + query2;
	    		System.out.println(sparql_query);
	    	}
	    	
	    	return getFinalResponse(sparql_query);
	    }
	    
	    public String getFinalResponse(String sparql_query)
	    {
	    	String stringQuery = "PREFIX dbont: <http://dbpedia.org/ontology/> " +
	    	        "PREFIX dbp: <http://dbpedia.org/property/>" +
	    	        "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>" + sparql_query;
	    	Query query = QueryFactory.create(stringQuery);
	    	QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);

	    	ResultSet results = qexec.execSelect();
	    	QuerySolution soln = results.nextSolution();
	    	Literal l = (Literal) soln.get("variable");
	    	qexec.close() ;
	    	return l.getString();
	    }
	    
	    
	}


