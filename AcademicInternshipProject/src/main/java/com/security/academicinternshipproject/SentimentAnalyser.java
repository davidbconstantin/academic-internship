/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.security.academicinternshipproject;

import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import java.io.IOException;
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
            classifications = predictor.predict(input);
            this.input = input;
            logger.info(classifications.toString());
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
