/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jflex;

import java.io.IOException;
import java.io.StringReader;
import cup.sym;

/**
 *
 * @author Matheus Prachedes Batista
 */
public class Token {

    public static final String[] terminalNames = new String[]{
        "EOF",
        "error",
        "RSRVDA_PROGRAM",
        "RSRVDA_VAR",
        "RSRVDA_PROCEDURE",
        "RSRVDA_BEGIN",
        "RSRVDA_END",
        "RSRVDA_IF",
        "RSRVDA_THEN",
        "RSRVDA_ELSE",
        "RSRVDA_WHILE",
        "RSRVDA_DO",
        "RSRVDA_TRUE",
        "RSRVDA_FALSE",
        "RSRVDA_INTEGER",
        "RSRVDA_BOOLEAN",
        "PTO_VIRGULA",
        "DOIS_PONTOS",
        "VIRGULA",
        "ABRE_P",
        "FECHA_P",
        "ATRIBUIÇÃO",
        "OP_MENOR",
        "OP_MAIOR",
        "OP_MENOR_IGUAL",
        "OP_MAIOR_IGUAL",
        "OP_DIFERENTE",
        "OP_IGUAL",
        "OP_SOMA",
        "OP_SUB",
        "OP_DIV",
        "OP_MULT",
        "OP_OR",
        "OP_AND",
        "OP_NOT",
        "NUM_INTEIRO",
        "NUM_REAL",
        "IDENTIFICADOR",
        "PONTO",
        "COMENTARIO_LINHA",
        "COMENTARIO_BLOCO",
        "COMENTARIO_BLOCO_SEM_FECHAR",
        "COMENTARIO_BLOCO_SEM_ABRIR",
        "NUM_REAL_MALFORMADO",
        "CHAR_INVALIDO"
    };

    private String lexema;
    private int tipo;
    private int linha;
    private int colunaInicio;
    private int colunaFinal;
    private int offset;

    public String getTokenName() {
        return terminalNames[tipo];
    }

    public Token(String lexema, int tipo, int linha, int colunaInicio, int offset) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.linha = linha;
        this.colunaInicio = colunaInicio;
        this.colunaFinal = colunaInicio + lexema.length() - 1;
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

    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return lexema + " -> " + tipo + " " + colunaInicio + " " + colunaFinal + " " + (linha+1) + " " + offset;
    }

}
