package com.leebecker.example;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasCreationUtils;
import org.cleartk.syntax.dependency.type.DependencyNode;
import org.cleartk.syntax.dependency.type.DependencyRelation;
import org.cleartk.syntax.dependency.type.TopDependencyNode;
import org.cleartk.token.type.Sentence;
import org.cleartk.token.type.Token;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.util.JCasUtil;

import com.google.common.collect.Maps;


public class DumpDependencyParsesAnnotator extends JCasAnnotator_ImplBase {
	
	public static final String PARAM_TARGET_VIEW_NAME = "targetViewName";
	public static final String DEFAULT_TARGET_VIEW_NAME = "DumpDependencyParsesView";
	
	@ConfigurationParameter(
		name = PARAM_TARGET_VIEW_NAME,
		mandatory = false,
		defaultValue = DEFAULT_TARGET_VIEW_NAME)
	protected String targetViewName;
	
	public static final String PARAM_PRINT_TO_STDOUT = "printToStdout";
	@ConfigurationParameter(
		name = PARAM_PRINT_TO_STDOUT,
		mandatory = false,
		defaultValue = "true")
	protected boolean printToStdout; 
	
	public static AnalysisEngineDescription getDescription() throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(DumpDependencyParsesAnnotator.class); 
	}
	
	public static AnalysisEngineDescription getDescription(String targetViewName, boolean printToStdout) throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(DumpDependencyParsesAnnotator.class, 
			DumpDependencyParsesAnnotator.PARAM_TARGET_VIEW_NAME,
			targetViewName,
			DumpDependencyParsesAnnotator.PARAM_PRINT_TO_STDOUT,
			printToStdout);
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		JCas tgtView;
		try {
			tgtView = jCas.createView(this.targetViewName);
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
		
		StringBuilder builder = new StringBuilder();
		for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
			List<Token> tokens = JCasUtil.selectCovered(jCas, Token.class, sentence);
			Map<Token, Integer> tokenToId = Maps.newHashMap();
			
			builder.append(sentence.getCoveredText());
			builder.append("\n");
			
			int i = 1;
			for (Token token : tokens) { tokenToId.put(token, i++); }
			
			for (Token token : tokens) {
				
				DependencyNode tokenDepNode = JCasUtil.selectCovered(jCas, DependencyNode.class, token).get(0);
				if (tokenDepNode.getHeadRelations().size() == 0) {
					continue;
				}
				DependencyRelation headRelation = tokenDepNode.getHeadRelations(0);
				builder.append(relationToString(jCas, headRelation, tokenToId));
				builder.append("\n");
			}
			builder.append("\n");
		}
		
		if (this.printToStdout) {
			System.out.println(builder.toString());
		}
		tgtView.setDocumentText(builder.toString());
	}
	
	public static String relationToString(JCas jCas, DependencyRelation relation, Map<Token, Integer> tokenToId) {
		DependencyNode headNode = relation.getHead();
		DependencyNode childNode = relation.getChild();
		return String.format("%s(%s, %s)", 
				relation.getRelation(), 
				nodeToString(jCas, headNode, tokenToId),
				nodeToString(jCas, childNode, tokenToId));
	}
	
	public static String nodeToString(JCas jCas, DependencyNode node, Map<Token, Integer> tokenToId) {
		if (node instanceof TopDependencyNode) {
			return "ROOT-0";
		} else {
			Token token = JCasUtil.selectCovered(jCas, Token.class, node).get(0);
			return String.format("%s-%d", node.getCoveredText(), tokenToId.get(token));
		}
	}
	

}
