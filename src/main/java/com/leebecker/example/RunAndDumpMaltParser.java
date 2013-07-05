package com.leebecker.example;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.cleartk.opennlp.POSTagger;
import org.cleartk.syntax.dependency.malt.MaltParser;
import org.cleartk.syntax.opennlp.SentenceAnnotator;
import org.cleartk.token.tokenizer.TokenAnnotator;
import org.cleartk.util.Options_ImplBase;
import org.cleartk.util.ae.UriToDocumentTextAnnotator;
import org.cleartk.util.cr.UriCollectionReader;
import org.kohsuke.args4j.Option;
import org.uimafit.factory.AggregateBuilder;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.pipeline.SimplePipeline;

public class RunAndDumpMaltParser {
	
	public static class Options extends Options_ImplBase {
		
		@Option(name="--input-dir")
		public File inputDir = new File("/tmp/files_to_parse");
		
	}
	
	public static void main (String[] args) throws UIMAException, IOException {
		
		Options options = new Options();
		options.parseOptions(args);
		
		File inputDir = options.inputDir;
		
		CollectionReader reader = CollectionReaderFactory.createCollectionReader(
				UriCollectionReader.class, 
				UriCollectionReader.PARAM_DIRECTORY,
				inputDir);
		
		AggregateBuilder pipeline = new AggregateBuilder();
		pipeline.add(UriToDocumentTextAnnotator.getDescription());
		pipeline.add(SentenceAnnotator.getDescription());
		pipeline.add(TokenAnnotator.getDescription());
		pipeline.add(POSTagger.getDescription("en"));
		pipeline.add(MaltParser.getDescription());
		pipeline.add(DumpDependencyParsesAnnotator.getDescription());
		
		SimplePipeline.runPipeline(reader, pipeline.createAggregate());
	}

}
