package com.leebecker.example;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.clearnlp.DependencyParser;
import org.cleartk.clearnlp.MPAnalyzer;
import org.cleartk.clearnlp.PosTagger;
import org.cleartk.clearnlp.Tokenizer;
import org.cleartk.syntax.dependency.malt.MaltParser;
import org.cleartk.syntax.opennlp.SentenceAnnotator;
import org.cleartk.token.tokenizer.TokenAnnotator;
import org.cleartk.util.ae.UriToDocumentTextAnnotator;
import org.cleartk.util.cr.UriCollectionReader;
import org.uimafit.factory.AggregateBuilder;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.pipeline.SimplePipeline;

public class RunAndDumpClearParser {
	
	public static void main (String[] args) throws UIMAException, IOException {
		
		
		File inputDir = new File("/tmp/files_to_parse");
		
		CollectionReader reader = CollectionReaderFactory.createCollectionReader(
				UriCollectionReader.class, 
				UriCollectionReader.PARAM_DIRECTORY,
				inputDir);
		
		AggregateBuilder pipeline = new AggregateBuilder();
		pipeline.add(UriToDocumentTextAnnotator.getDescription());
		pipeline.add(SentenceAnnotator.getDescription());
		pipeline.add(Tokenizer.getDescription());
		pipeline.add(PosTagger.getDescription());
		pipeline.add(MPAnalyzer.getDescription());
		pipeline.add(DependencyParser.getDescription());
		pipeline.add(DumpDependencyParsesAnnotator.getDescription());
		
		SimplePipeline.runPipeline(reader, pipeline.createAggregate());
	}

}
