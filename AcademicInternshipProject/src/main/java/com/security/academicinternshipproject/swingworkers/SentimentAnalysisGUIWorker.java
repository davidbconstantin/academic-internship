/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 * https://docs.oracle.com/javase/8/docs/api/javax/swing/Timer.html
 */
package com.security.academicinternshipproject.swingworkers;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.translate.TranslateException;
import com.security.academicinternshipproject.MainMenuForm;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;
import javax.swing.Timer;

/**
 *
 * @author rokom
 */
public class SentimentAnalysisGUIWorker extends SwingWorker<List<Integer>, Integer> {
    
    private MainMenuForm mainMenuForm;
    private javax.swing.JTextArea statusTA;
    private javax.swing.JTextField inputTF;
    private javax.swing.JProgressBar positivePB;
    private javax.swing.JProgressBar negativePB;
    private javax.swing.JLabel pValueLBL;
    private javax.swing.JLabel nValueLBL;
    private List<Integer> positiveScores;
    private List<Integer> negativeScores;
    private List<Integer> scores;
    private int index;
    private String row;
    private Timer timer;
    
    public SentimentAnalysisGUIWorker(MainMenuForm mainMenuForm, javax.swing.JTextArea statusTA,
            javax.swing.JTextField inputTF, javax.swing.JProgressBar positivePB,
            javax.swing.JProgressBar negativePB, javax.swing.JLabel pValueLBL,
            javax.swing.JLabel nValueLBL) {
        this.mainMenuForm = mainMenuForm;
        this.statusTA = statusTA;
        this.inputTF = inputTF;
        this.positivePB = positivePB;
        this.negativePB = negativePB;
        this.pValueLBL = pValueLBL;
        this.nValueLBL = nValueLBL;
    }
    
    @Override
    protected List<Integer> doInBackground() {
        scores = new ArrayList<>();
        mainMenuForm.getTaskNotifier().setToolTip("Performing sentiment analysis on table " + mainMenuForm.getSelectedTableName() + "...");
        try {
            if (!statusTA.getText().equals(""))
                mainMenuForm.getSentimentAnalyser().predict(statusTA.getText());
            else if (!mainMenuForm.isAnalysingSQLTable())
                mainMenuForm.getSentimentAnalyser().predict(inputTF.getText());
            else {
                // predict sentiment for each table row
                index = 0;
                positiveScores = new ArrayList<>();
                negativeScores = new ArrayList<>();
                timer = new Timer(5000, e -> {
                    mainMenuForm.getTaskNotifier().setToolTip("Performing sentiment analysis on table " + mainMenuForm.getSelectedTableName() + "... " +
                    index + "/" + mainMenuForm.getCrs().size() + " row(s) processed.");
                });
                timer.start();
                while (mainMenuForm.getCrs().next()) {
                    index++;
                    System.out.println("Analysing row " + index + " out of " + mainMenuForm.getCrs().size()); 
                    if (mainMenuForm.getCrs().getString(mainMenuForm.getSelectedColumnName()) != null &&
                            !mainMenuForm.getCrs().getString(mainMenuForm.getSelectedColumnName()).trim().isEmpty()) {
                        row = mainMenuForm.getCrs().getString(mainMenuForm.getSelectedColumnName());
                        mainMenuForm.getSentimentAnalyser().predict(row);
                        positiveScores.add((int)(mainMenuForm.getSentimentAnalyser().getClassifications().get("Positive").getProbability() * 100));
                        scores.add((int)(mainMenuForm.getSentimentAnalyser().getClassifications().get("Positive").getProbability() * 100));
                        negativeScores.add((int)(mainMenuForm.getSentimentAnalyser().getClassifications().get("Negative").getProbability() * 100));
                        scores.add((int)(mainMenuForm.getSentimentAnalyser().getClassifications().get("Negative").getProbability() * 100));
                        publish(scores.get(scores.size() - 2));
                        publish(scores.get(scores.size() - 1));
                    } else
                        System.out.println("Skipping row...");
                }
            }
        } catch (MalformedModelException ex) {
            System.out.println(ex);
        } catch (ModelNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (TranslateException ex) {
            System.out.println(ex);
        } catch (SQLException ex) {
            System.out.println(ex);
        }      
        return scores;
    }
    
    @Override
    protected void process(List<Integer> values) {
        pValueLBL.setText(String.valueOf(values.get(values.size() - 2)));
        positivePB.setValue(values.get(values.size() - 2));
        nValueLBL.setText(String.valueOf(values.get(values.size() - 1)));
        negativePB.setValue(values.get(values.size() - 1));
        statusTA.append("Processed " + index + "/" + mainMenuForm.getCrs().size() + " row(s): "  
                + row + "\n");
    }
    
    @Override
    protected void done() {
        try {
            mainMenuForm.getCrs().close();
            mainMenuForm.setIsAnalysingTable(false);
            inputTF.setText("");    
        } catch (SQLException ex) {
            System.out.println(ex);
        } finally {
            if (!mainMenuForm.isAnalysingSQLTable()) {
            positivePB.setValue((int)(mainMenuForm.getSentimentAnalyser().getClassifications().get("Positive").getProbability() * 100));
            negativePB.setValue((int)(mainMenuForm.getSentimentAnalyser().getClassifications().get("Negative").getProbability() * 100));
            } else {
                positivePB.setValue(computeAggregateScore((ArrayList<Integer>) positiveScores));
                negativePB.setValue(computeAggregateScore((ArrayList<Integer>) negativeScores));
                positiveScores.clear();
                negativeScores.clear();
            }
            pValueLBL.setText(String.valueOf(mainMenuForm.getSentimentAnalyser().getClassifications().get("Positive").getProbability()));
            nValueLBL.setText(String.valueOf(mainMenuForm.getSentimentAnalyser().getClassifications().get("Negative").getProbability()));
        }
        mainMenuForm.getTaskNotifier().displayMessage("Sentiment analysis is finished!");
        mainMenuForm.getTaskNotifier().setToolTip("Web Crawler App");
        timer.stop();
    }
    
    public int computeAggregateScore(ArrayList<Integer> values) {
        int finalValue = 0;
        int denominator = 0;
        for (int value: values) {
            denominator++;
            System.out.println("Value " + denominator + ": "+ value);
            finalValue += value;
        }
        System.out.println("Final value: " + finalValue + "/" + denominator);
        return finalValue / denominator;
    }
}
