/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jflex;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java_cup.runtime.Symbol;

/**
 *
 * @author Matheus Prachedes Batista
 */
public class CustomScanner extends AnalisadorLexicoLALG{

    private ArrayList<Token> tabelaTokens;
    private ArrayList<Token> lookahead;
    
    public CustomScanner(Reader in, ArrayList<Token> tabelaTokens) {
        super(in);
        this.tabelaTokens = tabelaTokens;
        this.lookahead = new ArrayList<>();
    }
    
    @Override
    public Symbol next_token() throws IOException{
        Symbol s = super.next_token();
        Token t = super.yylex();
        if(t.getTipo()!=0 && tabelaTokens!=null)tabelaTokens.add(super.yylex());
        return s;
    }
    
    public Token nextToken(){
        try {
            Token t;
            if(lookahead.isEmpty()){
                super.next_token();
                t = super.yylex();
            }else{
                t = lookahead.remove(0);
            }
            if(t.getTipo()!= 0 && tabelaTokens!=null)tabelaTokens.add(t);
            return t;
        } catch (IOException ex) {
            return null;
        }
    }
    
    public int lookahead(Integer /*<gambiarra>*/.../*</gambiarra>*/ tokens){
        Token t = nextToken();
        try{
            while(!tokenIs(t,tokens)){
                lookahead.add(t);
                super.next_token();
                t = super.yylex();
                if(t.getTipo()==sym.EOF)break;
            }
        }catch(IOException e){
            return sym.EOF;
        }
        lookahead.add(t);
        return t.getTipo();
    }
    
    public boolean tokenIs(Token token, Integer ... tokens){
        for(Integer t : tokens){
            if(t == token.getTipo()){
                return true;
            }
        }
        return false;
    }
    

}
