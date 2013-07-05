package com.leebecker.example;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.cleartk.clearnlp.DependencyParser;
import org.cleartk.clearnlp.MPAnalyzer;
import org.cleartk.clearnlp.PosTagger;
import org.cleartk.clearnlp.Tokenizer;
import org.cleartk.syntax.opennlp.SentenceAnnotator;
import org.cleartk.util.Options_ImplBase;
import org.cleartk.util.ae.UriToDocumentTextAnnotator;
import org.cleartk.util.cr.UriCollectionReader;
import org.kohsuke.args4j.Option;
import org.uimafit.factory.AggregateBuilder;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.pipeline.SimplePipeline;

public class RunAndDumpClearParser {
	
	public static class Options extends Options_ImplBase {
		
		@Option(name="--input-dir")
		public File inputDir = new File("/tmp/files_to_parse");
		
	}
	
	public static void main (String[] args) throws UIMAException, IOException {
		
		
		Options options = new Options();
		options.parseOptions(args);
		
		File inputDir = options.inputDir;
		
		// Creates a collection reader that creates a CAS object for each file found in the input directory
		// and sets the CAS's URI field to the file's URI path.
		CollectionReader reader = CollectionReaderFactory.createCollectionReader(
				UriCollectionReader.class, 
				UriCollectionReader.PARAM_DIRECTORY,
				inputDir);
		
		// Create a pipeline for parsing
		AggregateBuilder pipeline = new AggregateBuilder();
		// Reads text from URI field and populates view CAS.NAME_DEFAULT_SOFA
		pipeline.add(UriToDocumentTextAnnotator.getDescription());
		// Sentence segmentation
		pipeline.add(SentenceAnnotator.getDescription());
		// ClearNLP tokenizer
		pipeline.add(Tokenizer.getDescription());
		// ClearNLP POS tagger
		pipeline.add(PosTagger.getDescription());
		// ClearNLP Morphological Analyzer (aka lemmatizer)
		pipeline.add(MPAnalyzer.getDescription());
		// ClearNLP Dependency parser
		pipeline.add(DependencyParser.getDescription());
		// Simple analysis engine to dump parses
		pipeline.add(DumpDependencyParsesAnnotator.getDescription());
		
		SimplePipeline.runPipeline(reader, pipeline.createAggregate());
	}

}
