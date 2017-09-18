/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jflex;

/**
 *
 * @author Matheus Prachedes Batista
 */
public class Token {
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
    
}
