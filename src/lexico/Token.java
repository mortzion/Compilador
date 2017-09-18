/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lexico;

import java.util.ArrayList;

/**
 *
 * @author Matheus Prachedes Batista
 */
public class Token {
    public static final int EOF = -2;
    public static final int INVALIDO = -1;
    public static final int NUMERO_INTEIRO = 0;
    public static final int NUMERO_REAL = 1;
    public static final int OP_SOMA = 2;
    public static final int OP_SUB = 3;
    public static final int OP_MULT = 4;
    public static final int OP_DIV = 5;
    public static final int ABRE_PARENTESIS = 6;
    public static final int FECHA_PARENTESIS = 7;
    
    
    
    private int tipo = INVALIDO;
    private String lexema;
    
    public void setToken(int tipo, String lexema){
        this.tipo = tipo;
        this.lexema = lexema;
    }
    
    private void tokenNum(String cadeia,int inicio, int atual) {
        if(atual < cadeia.length()){
            char c = cadeia.charAt(atual++);
            while(c >= '0' && c <= '9' && atual < cadeia.length())c = cadeia.charAt(atual++);
            if(c == '.'){
                tokenNumReal(cadeia,inicio,atual);
                return;
            }
            atual--;
        }
        setToken(NUMERO_INTEIRO, cadeia.substring(inicio, atual));
    }

    private void tokenNumReal(String cadeia, int inicio, int atual) {
        if(atual == cadeia.length()){
            setToken(INVALIDO,cadeia.substring(inicio,atual-1));
            return;
        }
        char c = cadeia.charAt(atual++);
        if(c < '0' || c > '9'){
            setToken(INVALIDO, cadeia.substring(inicio,atual-1));
            return;
        }
        if(atual < cadeia.length()){
            while(c >= '0' && c<='9' && atual < cadeia.length())c = cadeia.charAt(atual++);
            atual--;
        }
        setToken(NUMERO_REAL, cadeia.substring(inicio,atual));
    }
    
    public int getToken(String cadeia, int inicio){
        int i = inicio;
        char c = cadeia.charAt(i++);
        while((c == ' ' || c == '\n' || c == '\t') && i<cadeia.length())c = cadeia.charAt(i++);
        if(i==cadeia.length()){
            setToken(EOF,"\n");
            return i-inicio;
        }
        switch(c){
            case '(':
                setToken(ABRE_PARENTESIS, "(");
                break;
            case ')':
                setToken(FECHA_PARENTESIS, ")");
                break;
            case '+':
                setToken(OP_SOMA, "+");
                break;
            case '-':
                setToken(OP_SUB, "-");
                break;
            case '/':
                setToken(OP_DIV,"/");
                break;
            case '*':
                setToken(OP_MULT,"*");
                break;
            default:
                if(c >= '0' && c <= '9')tokenNum(cadeia,i-1,i);
                else setToken(INVALIDO,String.valueOf(c));
                break;
        }
        return lexema.length() + (i - inicio-1);
    }
    
    @Override
    public String toString(){
        return lexema + " => " + tipo;
    }
    
    public static ArrayList<Token> getTokens(String input){
        ArrayList<Token> tokens = new ArrayList<>();
        for(int i=0;i<input.length();){
            Token t = new Token();
            i = i+t.getToken(input,i);
            tokens.add(t);
        }
        if(tokens.get(tokens.size()-1).tipo==EOF)tokens.remove(tokens.size()-1);
        return tokens;
    }
    
    public static void main (String[] args){
        ArrayList<Token> tokens = Token.getTokens("    3.2  + 2.2  ");
        for(Token t : tokens){
            System.out.println(t + "\n");
        }
    }
}
