/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jflex;

import java.io.IOException;
import java.io.StringReader;

/**
 *
 * @author Matheus Prachedes Batista
 */
public class Token {
    public static final int NUM_REAL_MALFORMADO = -2;
    public static final int INVALIDO = -1;
    public static final int OP_SUB = 0;
    public static final int OP_SOMA = 1;
    public static final int OP_MULT = 2;
    public static final int OP_DIV = 3;
    public static final int ABRE_P = 4;
    public static final int FECHA_P = 5;
    public static final int NUM_INTEIRO = 6;
    public static final int NUM_REAL = 7;
   
    private String lexema;
    private int tipo;
    private int linha;
    private int colunaInicio;
    private int colunaFinal;

    
    public Token(String lexema, int tipo, int linha, int colunaInicio) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.linha = linha;
        this.colunaInicio = colunaInicio;
        this.colunaFinal = colunaInicio + lexema.length()-1;
    }

    public Token(String lexema, int tipo, int linha, int colunaInicio, int somaColunaFinal) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.linha = linha;
        this.colunaInicio = colunaInicio;
        this.colunaFinal = colunaInicio + lexema.length()-1+somaColunaFinal;
    }
    
    
    
    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getLinha() {
        return linha;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

    public int getColunaInicio() {
        return colunaInicio;
    }

    public void setColunaInicio(int colunaInicio) {
        this.colunaInicio = colunaInicio;
    }

    public int getColunaFinal() {
        return colunaFinal;
    }

    public void setColunaFinal(int colunaFinal) {
        this.colunaFinal = colunaFinal;
    }
    
    public String toString(){
        return lexema + " -> " + tipo + " " + colunaInicio + " " + colunaFinal + " " + linha; 
    }
    
    public static void main(String[] args) throws IOException{
        String exp = "3. 4+";
        AnalisadorLexico lex = new AnalisadorLexico(new StringReader(exp));
        System.out.println(lex.yylex());
        System.out.println(lex.yylex());
        System.out.println(lex.yylex());
    }
    
}
