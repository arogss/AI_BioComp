/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GeneticAlgorithm;

import java.util.Random;

/**
 *
 * @author sa2-arogundade
 */
public class Individual {
    private int[] gene;
    private int fitness;
    private Random rn;

    public Individual(int[] gene) {
        this.gene = gene;
        this.fitness = 0;
        rn = new Random();
    }

    public Individual(int size) {
        gene = new int[size];
        rn = new Random();
        this.generate();
        this.fitness = 0;
    }

    private void generate() {
        for (int i = 0; i < gene.length; i++) {
            if ((i+1)%(gene.length/GeneticAlgorithm.NUM_OF_RULES)==0){
                gene[i] = rn.nextInt(2);
            } else {
                gene[i] = rn.nextInt(3);
            }
        }
    }

    public Individual[] crossover(Individual parent2) {
        Individual parent1 = this;
        int a = gene.length -1;
        int b = 1;
        int point = rn.nextInt(a - b) + b;
        int[] gene1, gene2;

        gene1 = new int[gene.length];
        gene2 = new int[gene.length];
        
        for (int i = 0; i < gene.length; i++) {
            if (i < point){
                gene1[i] = parent1.getGene()[i];
                gene2[i] = parent2.getGene()[i];
            } else {
                gene1[i] = parent2.getGene()[i];
                gene2[i] = parent1.getGene()[i];
            }
        }
        
        Individual[] children = new Individual[2];
        children[0] = new Individual(gene1);
        children[1] = new Individual(gene2);

        return children;
    }
    
    public Individual mutatation(double mutationRate){
        int[] tempGene = this.gene;
        for (int i = 0; i < tempGene.length; i++) {
            if (rn.nextDouble() <= mutationRate){
                switch (tempGene[i]){
                    case 0:
                        if (((i+1)%(gene.length/GeneticAlgorithm.NUM_OF_RULES)==0) || (rn.nextInt(2) == 0)){
                            tempGene[i] = 1;
                        } else {
                            tempGene[i] = 2;
                        }
                        break;
                    case 1:
                        if (((i+1)%(gene.length/GeneticAlgorithm.NUM_OF_RULES)==0) || (rn.nextInt(2) == 0)){
                            tempGene[i] = 0;
                        } else {
                            tempGene[i] = 2;
                        }
                        break;
                    case 2:
                        tempGene[i] = rn.nextInt(2);
                        break;
                }
            }
        }
        return new Individual(tempGene);
    }
    
    public void updateFitness(){
        this.fitness = GeneticAlgorithm.calculateFitness(this);
    }

    public String displayGene() {
        String out = "";
        for (int i = 0; i < gene.length; i++) {
            out = out.concat(Integer.toString(gene[i]));
        }
        return out;
    }

    public int[] getGene() {
        return gene;
    }

    public void setGene(int[] gene) {
        this.gene = gene;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

}
