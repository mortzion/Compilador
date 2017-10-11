
import cup.parser;
import java.io.StringReader;
import jflex.AnalisadorLexicoLALG;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Matheus Prachedes Batista
 */
public class teste {
    public static void main(String[] args) throws Exception{
        parser p = new parser(new AnalisadorLexicoLALG(new StringReader("program teste;.")));
        p.parse();
    }
}
