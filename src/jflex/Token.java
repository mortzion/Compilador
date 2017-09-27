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
    public static final int BLOCO_COMENTARIO_SEM_ABRIR = -4;
    public static final int BLOCO_COMENTARIO_SEM_FECHAR = -3;
    public static final int NUM_REAL_MALFORMADO = -2;
    public static final int INVALIDO = -1;
    public static final int RSRVDA_PROGRAM = 0;
    public static final int RSRVDA_VAR = 1;
    public static final int RSRVDA_PROCEDURE = 2; 
    public static final int RSRVDA_BEGIN = 3;
    public static final int RSRVDA_END = 4;
    public static final int RSRVDA_IF = 5;
    public static final int RSRVDA_THEN = 6;
    public static final int RSRVDA_ELSE = 7;
    public static final int RSRVDA_WHILE = 8;
    public static final int RSRVDA_DO = 9;
    public static final int RSRVDA_READ = 10;
    public static final int RSRVDA_WRITE = 11;
    public static final int RSRVDA_TRUE = 12;
    public static final int RSRVDA_FALSE = 13;
    public static final int RSRVDA_INTEGER = 14;
    public static final int RSRVDA_BOOLEAN = 16;

    public static final int PTO_VIRGULA = 17;
    public static final int DOIS_PONTOS = 18;
    public static final int VIRGULA = 19;
    public static final int ABRE_P = 20;
    public static final int FECHA_P = 21;
    public static final int ATRIBUICAO = 22;
    public static final int OP_MENOR = 23;
    public static final int OP_MAIOR = 24;
    public static final int OP_MENOR_IGUAL = 25;
    public static final int OP_MAIOR_IGUAL = 26;
    public static final int OP_DIFERENTE = 27;
    public static final int OP_SOMA = 28;
    public static final int OP_SUB = 29;
    public static final int OP_DIV = 30;
    public static final int OP_MULT = 31;
    public static final int OP_OR  = 32;
    public static final int OP_AND = 33;
    public static final int OP_NOT = 34;
    public static final int NUM_INTEIRO = 35;
    public static final int NUM_REAL = 36;
    public static final int IDENTIFICADOR = 37;
    public static final int PONTO = 38;
    
    private String lexema;
    private int tipo;
    private int linha;
    private int colunaInicio;
    private int colunaFinal;
    private int offset;

    public String getTokenName(){
        switch(this.tipo){
            case -4: return "BLOCO_COMENTARIO_SEM_ABRIR";
            case -3: return "BLOCO_COMENTARIO_SEM_FECHAR";
            case -2: return "NUM_REAL_MALFORMADO";
            case -1: return "INVALIDO";
            case 0: return "RSRVDA_PROGRAM";
            case 1: return "RSRVDA_VAR";
            case 2: return "RSRVDA_PROCEDURE";
            case 3: return "RSRVDA_BEGIN";
            case 4: return "RSRVDA_END";
            case 5: return "RSRVDA_IF";
            case 6: return "RSRVDA_THEN";
            case 7: return "RSRVDA_ELSE";
            case 8: return "RSRVDA_WHILE";
            case 9: return "RSRVDA_DO";
            case 10: return "RSRVDA_READ";
            case 11: return "RSRVDA_WRITE";
            case 12: return "RSRVDA_TRUE";
            case 13: return "RSRVDA_FALSE";
            case 14: return "RSRVDA_INTEGER";
            case 16: return "RSRVDA_BOOLEAN";
            case 17: return "PTO_VIRGULA";
            case 18: return "DOIS_PONTOS";
            case 19: return "VIRGULA";
            case 20: return "ABRE_P";
            case 21: return "FECHA_P";
            case 22: return "ATRIBUICAO";
            case 23: return "OP_MENOR";
            case 24: return "OP_MAIOR";
            case 25: return "OP_MENOR_IGUAL";
            case 26: return "OP_MAIOR_IGUAL";
            case 27: return "OP_DIFERENTE";
            case 28: return "OP_SOMA";
            case 29: return "OP_SUB";
            case 30: return "OP_DIV";
            case 31: return "OP_MULT";
            case 32: return "OP_OR";
            case 33: return "OP_AND";
            case 34: return "OP_NOT";
            case 35: return "NUM_INTEIRO";
            case 36: return "NUM_REAL";
            case 37: return "IDENTIFICADOR";
            case 38: return "PONTO";
        }
        return "Não Encontrado!";
    }
    
    public Token(String lexema, int tipo, int linha, int colunaInicio, int offset) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.linha = linha;
        this.colunaInicio = colunaInicio;
        this.colunaFinal = colunaInicio + lexema.length()-1;
        this.offset = offset;
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
    
    public int getOffset(){
        return offset;
    }
    
    @Override
    public String toString(){
        return lexema + " -> " + tipo + " " + colunaInicio + " " + colunaFinal + " " + linha + " " + offset; 
    }
    
}
