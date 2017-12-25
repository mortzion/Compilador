/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.util.ArrayList;

/**
 *
 * @author Matheus Prachedes Batista
 */
public class LinguagemMaquina {

    public static final String INICIO_PROGRAMA = "INPP";
    public static final String PARA = "PARA";
    public static final String ALLOCA_MEMORIA = "AMEM";
    public static final String DESALLOCA_MEMORIA = "DMEM";
    public static final String CARREGA_VALOR = "CRVL";
    public static final String CARREGA_CONSTANTE = "CRCT";
    public static final String ARMAZENA = "ARMZ";
    public static final String NOT = "NEGA";
    public static final String MULT = "MULT";
    public static final String DIV = "DIVI";
    public static final String AND = "CONJ";
    public static final String SOMA = "SOMA";
    public static final String SUB = "SUBT";
    public static final String OR = "DISJ";
    public static final String INVERTE = "INVR";
    public static final String COMPARA_SE_MENOR = "CMME";
    public static final String COMPARA_SE_MAIOR = "CMMA";
    public static final String COMAPRA_SE_IGUAL = "CMIG";
    public static final String COMPARA_SE_DESIGUAL = "CMDG";
    public static final String COMPARA_SE_MAIOR_IGUAL = "CMAG";
    public static final String COMPARA_SE_MENOR_IGUAL = "CMEG";
    public static final String DESVIO_FALSE = "DSVF";
    public static final String DESVIO_SEMPRE = "DSVS";
    public static final String IMPRIME = "IMPR";
    public static final String LEITURA = "LEIT";
    public static final String NADA = "NADA";

    private ArrayList<Instrucao> programa;

    public LinguagemMaquina() {
        programa = new ArrayList<>();
    }

    public void addInstrucao(String instrucao, Integer operando) {
        this.programa.add(new Instrucao(programa.size(), instrucao, operando));
    }

    public void addInstrucao(int endereco, String instrucao, Integer operando) {
        this.programa.set(endereco, new Instrucao(endereco, instrucao, operando));
    }

    public int getEnderecoAtual() {
        return programa.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(programa.size()*7);
        for(Instrucao s : programa){
            sb.append(s.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
    
    

    private class Instrucao {

        private int rotulo;
        private String instrucao;
        private Integer operando;

        public Instrucao(int rotulo, String instrucao, Integer operando) {
            this.rotulo = rotulo;
            this.instrucao = instrucao;
            this.operando = operando;
        }

        @Override
        public String toString() {
            String print = rotulo + " " + instrucao;
            if(operando != null){
                print+=" " + operando;
            }
            return print;
        }
        
        
    }
}
