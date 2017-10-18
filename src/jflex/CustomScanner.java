/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jflex;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java_cup.runtime.Symbol;

/**
 *
 * @author Matheus Prachedes Batista
 */
public class CustomScanner extends AnalisadorLexicoLALG{

    private ArrayList<Token> tabelaTokens;
    
    public CustomScanner(Reader in, ArrayList<Token> tabelaTokens) {
        super(in);
        this.tabelaTokens = tabelaTokens;
    }
    
    @Override
    public Symbol next_token() throws IOException{
        Symbol s = super.next_token();
        Token t = super.yylex();
        if(t.getTipo()!=0)tabelaTokens.add(super.yylex());
        return s;
    }
}
