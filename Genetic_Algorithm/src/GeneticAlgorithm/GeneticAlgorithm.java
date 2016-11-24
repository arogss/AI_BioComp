/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GeneticAlgorithm;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author sa2-arogundade
 */
public class GeneticAlgorithm {

    public static Random rn;

    public static final int DATA_SET = 3;
    public static final int DATA_SIZE = 6;
    public static final int NUM_OF_RULES = 30;
    public static final int GENE_SIZE = NUM_OF_RULES * (DATA_SIZE + 1);
    public static final int POP_SIZE = 500;
    public static final int NUM_OF_RUNS = 10;
    public static final int TOURNAMENT_SIZE = 8;
    public static final double MUTATION_RATE = 0.02;
    public static final double CROSSOVER_RATE = 0.8;
    public static final double TEST_PERCENTAGE = 0.2;
    public static ArrayList<Rule> trainingData;

    
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        rn = new Random();

        int[] epochTaken = new int[10];
        double[] fitnessPercentages = new double[10];

        trainingData = new ArrayList<>();
        ArrayList<Rule> data = new ArrayList<>();
        String dirPath = "H:\\Personal\\NetBeansProjects\\Genetic_Algorithm\\";
        
        File in = new File(dirPath + "data" + DATA_SET + ".txt");

        Scanner scan = new Scanner(in);
        scan.nextLine(); 

        // Read in the data from the file
        while (scan.hasNext()) {
            String line = scan.nextLine();
            if (DATA_SET != 3) {
                int[] tmpValue = new int[DATA_SIZE - 1];
                for (int i = 0; i < DATA_SIZE - 1; i++) {
                    tmpValue[i] = Character.getNumericValue(line.charAt(i));
                }
                trainingData.add(new Rule(tmpValue, Integer.parseInt(line.substring(DATA_SIZE))));
            } else {
                int[] inputs = new int[DATA_SIZE];
                String[] items = line.split("\\s");
                for (int i = 0; i < inputs.length; i++) {
                    inputs[i] = (int) Math.round(Double.parseDouble(items[i]));
                }
                trainingData.add(new Rule(inputs, Integer.parseInt(items[DATA_SIZE])));
            }
        }
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        int trainingNumber = (int) Math.round(trainingData.size() * TEST_PERCENTAGE);

        File dir = new File(dirPath + time + "\\");
        dir.mkdir();
        
        File s = new File(dirPath + time + "\\parameters.txt");
        PrintWriter parameters = new PrintWriter(s, "UTF-8");
        parameters.println("NUM OF RUNS: " + NUM_OF_RUNS);
        parameters.println("POP SIZE: " + POP_SIZE);
        parameters.println("NUM OF RULES: " + NUM_OF_RULES);
        parameters.println("TOURNAMENT SIZE: " + TOURNAMENT_SIZE);
        parameters.println("MUTATION RATE: " + MUTATION_RATE);
        parameters.println("CROSSOVER RATE: " + CROSSOVER_RATE);
        parameters.println("TEST PERCENTAGE: " + TEST_PERCENTAGE);
        parameters.close();

        for (int run = 0; run < 10; run++) { // Run 10 times for an average
            // Split off some data for testing later.
            for (int i = 0; i < trainingNumber; i++) {
                data.add(trainingData.remove(rn.nextInt(trainingData.size())));
            }
            File output = new File(dirPath + time + "\\" + "output" + run + ".csv");
            PrintWriter wr = new PrintWriter(output, "UTF-8");
            wr.println("Run , FitSum, Best Fitness, Mean, Worst, Best Gene");

            // Create a new random population
            Population pop = new Population(POP_SIZE, GENE_SIZE);
            pop.runFitnessAll();

            Individual generationBest = pop.getFittestIndividual();
            Individual generationWorst = pop.getworstIndividual();
            int i = 1;

            // Run until an individual matches the training data perfectly or we hit the max number of runs
            while ((generationBest.getFitness() != trainingData.size()) && (i < NUM_OF_RUNS)) {
                Population parents = pop.selection();
                parents.crossover();
                parents.popMutate();
                parents.runFitnessAll();
                generationBest = parents.getFittestIndividual();
                generationWorst = parents.getworstIndividual();

                System.out.println("Run " + i + "\tFitSum: " + parents.calculateFitnessSum() + "\tBest: (" + generationBest.getFitness() + ")"  + "\tMean: " + parents.calculateFitnessMean() + "\tWorst: (" + generationWorst.getFitness() + ")\t " + generationBest.displayGene());
                wr.println(i + "," + parents.calculateFitnessSum() + "," + generationBest.getFitness() + "," + parents.calculateFitnessMean() + "," + generationWorst.getFitness() + ",[" + generationBest.displayGene() + "]");
                pop = parents;
                i++;
            }

            epochTaken[run] = i;

            System.out.println("TRAINING OVER");
            wr.println();
            wr.println();
            wr.println();
            wr.println("TEST DATA:");
            
            if (!data.isEmpty()) {
                int fitness = 0;
                ArrayList<Rule> rules = Rules(generationBest.getGene());
                for (Rule d : data) {
                    wr.println(d.display());
                    for (Rule rule : rules) {
                        boolean ruleFits = true;
                        for (int j = 0; j < d.getCond().length; j++) {
                            if ((rule.getCond()[j] != 2) && (rule.getCond()[j]) != d.getCond()[j]) {
                                ruleFits = false;
                                break;
                            }
                        }
                        if (ruleFits) {
                            if (d.getResult() == rule.getResult()) {
                                fitness++;
                            }
                            break;
                        }
                    }
                }
                if (DATA_SET == 1) {
                    
                } else {
                    // Calculate the percentage the GA got right
                    double testPercentage = ((float) fitness / (float) data.size()) * 100;
                    System.out.println("Final Gen Best Test results");
                    System.out.println("Fitness: " + fitness + " (" + testPercentage + "%)");
                    wr.println("RESULT:," + fitness + "," + testPercentage);
                    fitnessPercentages[run] = testPercentage;
                }
            }
            wr.close();
            while (!data.isEmpty()) {
                trainingData.add(data.remove(0));
            }
        }
        
        File r = new File(dirPath + time + "\\results.txt");
        PrintWriter wr = new PrintWriter(r, "UTF-8");
        wr.println("RUN RESULTS: ");
        double fitnessSum = 0;
        int epochTakenSum = 0;
        if (DATA_SET == 1) {
            for (int run : epochTaken) {
                wr.println(run);
                epochTakenSum += run;
            }
            wr.println();
            wr.print("AVERAGE: " + (float) (epochTakenSum / 10));
        } else {
            for (double result : fitnessPercentages) {
                wr.println(result);
                fitnessSum += result;
            }
            wr.println();
            // average of runs
            wr.println("AVERAGE: " + (fitnessSum / 10));
        }

        wr.close();
        
        File allRuns = new File(dirPath + "summary" + DATA_SET + ".csv");
        FileWriter fw = new FileWriter(allRuns, true);
        if (DATA_SET == 1) {
            fw.append(" Pop size: " + POP_SIZE + ", RuleNum: " + NUM_OF_RULES + ", RunNum: " + NUM_OF_RUNS + ", TournamentSize: " + TOURNAMENT_SIZE + ", Mutation: " + MUTATION_RATE + ", Cross: " + CROSSOVER_RATE + "," + (float) (epochTakenSum / 10) + ", Time: " + time + "\n");
        } else {
            fw.append(" Pop size: " + POP_SIZE + ", RuleNum: " + NUM_OF_RULES + ", RunNum: " + NUM_OF_RUNS + ", TournamentSize: " + TOURNAMENT_SIZE + ", Mutation: " + MUTATION_RATE + ", Cross: " + CROSSOVER_RATE + ", Test %: " + TEST_PERCENTAGE + ", Ave Fitness: " + (fitnessSum / 10) + ", Time: " + time + "\n");
        }
        fw.close();
    } 

    public static ArrayList<Rule> Rules(int[] gene) {
        ArrayList<Rule> rules = new ArrayList<>();
        int ruleLength = (gene.length / NUM_OF_RULES) - 1;
        int[] tmpValue = new int[ruleLength];
        int count = 0;
        for (int i = 0; i < gene.length; i++) {
            if (count < (ruleLength)) {
                tmpValue[count] = gene[i];
                count++;
            } else {
                count = 0;
                rules.add(new Rule(tmpValue, gene[i]));
                tmpValue = new int[ruleLength];
            }
        }
        return rules;
    }

    public static int calculateFitness(Individual ind) {
        int fitness = 0;
        ArrayList<Rule> rules = Rules(ind.getGene());
        for (Rule d : trainingData) {
            for (Rule rule : rules) {
                boolean match = true;
                for (int i = 0; i < d.getCond().length; i++) {
                    if ((rule.getCond()[i] != 2) && (rule.getCond()[i]) != d.getCond()[i]) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    if (d.getResult() == rule.getResult()) {
                        fitness++;
                    } 
                    break;
                }
            }
        }
        return fitness;
    }
}
