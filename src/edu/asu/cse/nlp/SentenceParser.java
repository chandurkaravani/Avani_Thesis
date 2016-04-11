package edu.asu.cse.nlp;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

class SentenceParser {

	private static final String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
	LexicalizedParser lp;
	List<Tree> verbList = new ArrayList<Tree>();
	List<Tree> nounList = new ArrayList<Tree>();
	List<Tree> whList = new ArrayList<Tree>();
	List<Tree> nounPhrase = new ArrayList<Tree>();
	List<Tree> vbnList = new ArrayList<Tree>();
	List<Tree> npList = new ArrayList<Tree>();
	List<Tree> adjList = new ArrayList<Tree>();
	List<Tree> nnList = new ArrayList<Tree>();
	List<Tree> nnsList = new ArrayList<Tree>();
	List<Tree> vbdList = new ArrayList<Tree>();
	List<Tree> wpList = new ArrayList<Tree>();
	List<Tree> exList = new ArrayList<Tree>();
	List<Tree> wdtList = new ArrayList<Tree>();


	public List<Tree> getVbdList() {
		return vbdList;
	}

	public List<Tree> getWdtList() {
		return wdtList;
	}

	public List<Tree> getAdjList() {
		return adjList;
	}

	public List<Tree> getNnList() {
		return nnList;
	}

	public List<Tree> getNnsList() {
		return nnsList;
	}

	public List<Tree> getNpList() {
		return npList;
	}

	public List<Tree> getVbnList() {
		return vbnList;
	}

	public List<Tree> getVerbList() {
		return verbList;
	}

	public List<Tree> getNounList() {
		return nounList;
	}

	public List<Tree> getWhList() {
		return whList;
	}

	public List<Tree> getWpList() {
		return wpList;
	}

	public List<Tree> getNounPhrase() {
		return nounPhrase;
	}
	public List<Tree> getexList() {
		return exList;
	}


	public LexicalizedParser getLexicalizedParser()
	{
		return LexicalizedParser.loadModel(parserModel);
	}

	public void setProcessedTree(String phrase)
	{
		TokenizerFactory<CoreLabel> tokenizerFactory =
				PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
		Tokenizer<CoreLabel> tok =
				tokenizerFactory.getTokenizer(new StringReader(phrase));
		List<CoreLabel> rawWords2 = tok.tokenize();
		Tree tree = getLexicalizedParser().apply(rawWords2);
		setAttributeLists(tree);

	}

	public void setAttributeLists(Tree tree)
	{

		for (Tree subtree: tree){

			if(subtree.label().value().equals("VBN")){
				vbnList.add(subtree.lastChild());
			}
			if(subtree.label().value().equals("VB")){
				verbList.add(subtree.lastChild());
			}
			if(subtree.label().value().equals("NNP")){
				nounList.add(subtree.lastChild());
			}
			if(subtree.label().value().equals("NN")){
				nnList.add(subtree.lastChild());
			}

			if(subtree.label().value().equals("WRB")){
				whList.add(subtree.lastChild());
			}

			if(subtree.label().value().equals("NP")){
				npList.add(subtree.lastChild());
			}
			if(subtree.label().value().equals("JJ")){
				adjList.add(subtree.lastChild());
			}
			if(subtree.label().value().equals("NNS")){
				nnsList.add(subtree.lastChild());
			}
			if(subtree.label().value().equals("VBD")){
				vbdList.add(subtree.lastChild());
			}
			if(subtree.label().value().equals("WP")){
				wpList.add(subtree.lastChild());
			}
			if(subtree.label().value().equals("EX")){
				exList.add(subtree.lastChild());
			}
			if(subtree.label().value().equals("WDT")){
				wdtList.add(subtree.lastChild());
			}

		}
		//          System.out.println(verbList);
		//          System.out.println(nounList);
		//          System.out.println(whList);
		//          System.out.println(nounPhrase);
	}

	public void getDependencies(String sent){

		String modelPath = DependencyParser.DEFAULT_MODEL;
		String taggerPath = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";

//		for (int argIndex = 0; argIndex < args.length; ) {
//			switch (args[argIndex]) {
//			case "-tagger":
//				taggerPath = args[argIndex + 1];
//				argIndex += 2;
//				break;
//			case "-model":
//				modelPath = args[argIndex + 1];
//				argIndex += 2;
//				break;
//			default:
//				throw new RuntimeException("Unknown argument " + args[argIndex]);
//			}
//		}

		//String text = "I can almost always tell when movies use fake dinosaurs.";

		MaxentTagger tagger = new MaxentTagger(taggerPath);
		DependencyParser parser = DependencyParser.loadFromModelFile(modelPath);

		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(sent));
		for (List<HasWord> sentence : tokenizer) {
			List<TaggedWord> tagged = tagger.tagSentence(sentence);
			GrammaticalStructure gs = parser.predict(tagged);

			// Print typed dependencies
			System.out.println(gs);
		}

	}




}
