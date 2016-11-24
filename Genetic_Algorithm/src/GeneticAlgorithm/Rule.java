/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GeneticAlgorithm;

/**
 *
 * @author sa2-arogundade
 */
public class Rule {
    private int[] cond;
    private int result;

    public Rule(int[] cond, int result) {
        this.cond = cond;
        this.result = result;
    }

    public int[] getCond() {
        return cond;
    }

    public void setCond(int[] cond) {
        this.cond = cond;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
    
    public String display(){
        String out = "";
        for(int i : cond){
            out = out + i;
        }
        out = out + " " + result;
        return out;
    }
    
}
