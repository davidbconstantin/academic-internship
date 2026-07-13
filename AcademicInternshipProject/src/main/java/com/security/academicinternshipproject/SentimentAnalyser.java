/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 * https://docs.djl.ai/master/docs/serving/serving/docs/lmi/deployment_guide/model-artifacts.html
 * https://javadoc.io/doc/ai.djl/api/latest/ai/djl/repository/zoo/ZooModel.html
 * https://www.baeldung.com/jackson-object-mapper-tutorial 
 * https://www.mongodb.com/resources/basics/chunking-explained 
 */
package com.security.academicinternshipproject;

import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.nlp.bert.BertTokenizer;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author rokom
 * https://github.com/deepjavalibrary/djl/blob/master/examples/src/main/java/ai/djl/examples/inference/nlp/SentimentAnalysis.java
 */
public class SentimentAnalyser {
    private Classifications classifications;
    
    private static final Logger logger = LoggerFactory.getLogger(SentimentAnalyser.class);
    private String input;
    private String modelUrl;
    
    public SentimentAnalyser(String modelUrl) {
        this.modelUrl = modelUrl;
        System.setProperty("ai.djl.repository.zoo.refresh", "true");
    }
    
    public Classifications predict(String input) throws MalformedModelException, ModelNotFoundException, IOException, TranslateException {
        Criteria<String, Classifications> criteria =
                Criteria.builder()
                    .setTypes(String.class, Classifications.class)
                    .optModelUrls(modelUrl)
                    .optEngine("PyTorch")
                    .optDevice(Device.cpu())
                    .optProgress(new ProgressBar())
                    .build();
        
        try (ZooModel<String, Classifications> model = criteria.loadModel();
            Predictor<String, Classifications> predictor = model.newPredictor()) {
            
            // check if input size exceeds max position embeddings and truncate if it does
            BertTokenizer tokenizer = new BertTokenizer();
            List<String> tokens = tokenizer.tokenize(input);
            if (tokens.size() > 512) {
                input = input.substring(0, 511);
            }
            classifications = predictor.predict(input);
            this.input = input;
            logger.info(classifications.toString());
            
            // returns model artefacts
            Path modelPath = model.getModelPath();
            System.out.println("Model Path: " + modelPath);
            
            Path configPath = modelPath.resolve("config.json");
            Files.list(modelPath).forEach(p -> System.out.println(" " + p.getFileName()));
            return classifications;
        }
    }
    
    public Classifications getClassifications() {
        return classifications;
    }
    
    public String getInput() {
        return input;
    }
    
    public String getModelUrl() {
        return modelUrl;
    }
}
