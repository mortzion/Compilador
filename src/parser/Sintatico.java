/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import cup.sym;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import javax.swing.LookAndFeel;
import jflex.CustomScanner;
import jflex.Token;

/**
 *
 * @author Matheus Prachedes Batista
 */
public class Sintatico {

    private ArrayList<SintaxError> erros;
    private CustomScanner sc;
    private Token tokenAtual;

    public void consumir() {
        tokenAtual = sc.nextToken();
    }

    public void consumir(Integer... tokens) {
        while (!tokenIs(tokens)) {
            consumir();
        }
    }

    public Sintatico(CustomScanner sc, ArrayList<SintaxError> erros) {
        this.sc = sc;
        tokenAtual = sc.nextToken();
        this.erros = erros;
    }

    public boolean start() {
        program();
        return tokenAtual.getTipo() == sym.EOF;
    }

    private boolean tokenIs(Integer... tokens) {
        if (tokens != null) {
            for (Integer i : tokens) {
                if (tokenAtual.getTipo() == i) {
                    return true;
                }
            }
        }
        return false;
    }

    //RSRVDA_PROGRAM ::= RSRVDA_PGORAM IDENTIFICADOR PTO_VIRGULA bloco PONTO
    private void program() {
        if (tokenIs(sym.RSRVDA_PROGRAM)) {
            consumir();
        } else {
            programError("program");
            return;
        }
        if (tokenIs(sym.IDENTIFICADOR)) {
            consumir();
        } else {
            programError("identificador");
            return;
        }
        if (tokenIs(sym.PTO_VIRGULA)) {
            consumir();
        } else {
            programError("';'");
            return;
        }
        bloco();
        if (tokenIs(sym.PONTO)) {
            consumir();
        } else {
            sintaxError("Espera-se ponto");
            return;
        }
        if (tokenIs(sym.EOF)) {
            consumir();
        }
    }

    private void programError(String msg) {
        sintaxError("Declaração de programa incorreto, espera-se " + msg);
        if (sc.lookahead(sym.RSRVDA_BEGIN, sym.RSRVDA_BOOLEAN, sym.RSRVDA_INTEGER, sym.RSRVDA_PROCEDURE) != sym.EOF) {
            consumir(sym.RSRVDA_BEGIN, sym.RSRVDA_BOOLEAN, sym.RSRVDA_INTEGER, sym.RSRVDA_PROCEDURE);
            bloco();
        } else {
            sintaxError("Espera-se declaração de variaveis, procedimentos ou inicio do bloco de comandos");
        }
    }

    //bloco ::= pt_dec_var apos_pt_dec_var | pt_dec_sub cmd_composto | cmd_composto;
    private void bloco() {
        //bloco ::= pt_dec_var apos_pt_dec_var
        if (tokenIs(sym.RSRVDA_BOOLEAN, sym.RSRVDA_INTEGER)) {
            pt_dec_var();
            apos_pt_dec_var();
        } else {
            //bloco ::= pt_dec_sub cmd_composto
            if (tokenIs(sym.RSRVDA_PROCEDURE)) {
                pt_dec_sub();
                cmd_composto();
            } else {
                //bloco ::= cmd_composto
                if (tokenIs(sym.RSRVDA_BEGIN)) {
                    cmd_composto();
                } else {
                    sintaxError("Espera-se declaração de variaveis, procedimentos ou inicio do bloco de comandos");
                    consumir(sym.PONTO);
                }
            }
        }
    }

    //pt_dec_var ::= dec_var PTO_VIRGULA pt_dec_var2
    private void pt_dec_var() {
        if (tokenIs(sym.RSRVDA_INTEGER, sym.RSRVDA_BOOLEAN)) {
            dec_var();
        }
        if (tokenIs(sym.PTO_VIRGULA)) {
            consumir();
        } else {
            sintaxError("Espera-se ';'");
            consumir(sym.RSRVDA_BOOLEAN, sym.RSRVDA_INTEGER, sym.RSRVDA_BEGIN, sym.RSRVDA_PROCEDURE);
        }
        if (tokenIs(sym.RSRVDA_BOOLEAN, sym.RSRVDA_INTEGER, sym.RSRVDA_BEGIN, sym.RSRVDA_PROCEDURE)) {
            pt2_dec_var2();
        }
    }

    //pt_dec_var2 ::= pt_dec_var | /**vazio**/;
    public void pt2_dec_var2() {
        if (tokenIs(sym.RSRVDA_BOOLEAN, sym.RSRVDA_INTEGER)) {
            pt_dec_var();
        } else {
            if (tokenIs(sym.RSRVDA_BEGIN, sym.RSRVDA_PROCEDURE)) {
                return;
            }else{
                sintaxError("Espera-se tipo, BEGIN ou PROCEDURE");
                consumir(sym.RSRVDA_BEGIN, sym.RSRVDA_PROCEDURE);
                return;
            }
        }
    }

    //dec_var ::= tipo lsta_id
    private void dec_var() {
        if (tokenIs(sym.RSRVDA_BOOLEAN, sym.RSRVDA_INTEGER)) {
            consumir();
        }
        lsta_id();
    }

    //lsta_id ::= IDENTIFICADOR lsta_id2
    private void lsta_id() {
        if (tokenIs(sym.IDENTIFICADOR)) {
            consumir();
        } else {
            sintaxError("Espera-se um identificador");
        }
        lsta_id2();
    }

    //lsta_id2 ::= VIRGULA lsta_id | vazio
    private void lsta_id2() {
        if (tokenIs(sym.VIRGULA)) {
            consumir();
            lsta_id();
        } else {
            if (tokenIs(sym.DOIS_PONTOS, sym.PTO_VIRGULA)) {
                return;
            } else {
                sintaxError("Espera-se ',', ';' ou ':'.");
                consumir(sym.DOIS_PONTOS, sym.PTO_VIRGULA);
            }
        }
    }

    //apos_pt_dec_var ::= pt_dec_sub cmd_composto|cmd_composto;
    private void apos_pt_dec_var() {
        if (tokenIs(sym.RSRVDA_PROCEDURE)) {
            pt_dec_sub();
        }
        cmd_composto();
    }

    //pt_dec_sub ::= dec_sub PTO_VIRGULA pt2_dec_sub;
    private void pt_dec_sub() {
        dec_sub();
        if (tokenIs(sym.PTO_VIRGULA)) {
            consumir();
        }else{
            sintaxError("Espera-se ';'");
            consumir(sym.RSRVDA_PROCEDURE, sym.RSRVDA_BEGIN);
        }
        pt2_dec_sub();
    }

    //cmd_composto ::= RSRVDA_BEGIN cmd lsta_cmd RSRVDA_END;
    private void cmd_composto() {
        if (tokenIs(sym.RSRVDA_BEGIN)) {
            consumir();
        }else{
            sintaxError("Espera-se begin");
            //FIRST CMD
            consumir(sym.IDENTIFICADOR, sym.RSRVDA_BEGIN, sym.RSRVDA_WHILE, sym.RSRVDA_IF,sym.RSRVDA_END,sym.PTO_VIRGULA);
            //Se não encontrou o first do CMD, mas encontrou o follow do lsta_cmd, então consome o end e termina esta regra
            if(tokenIs(sym.RSRVDA_END)){
                consumir();
                return;
            }
            //Se encontrou o first de lsta_cmd, então consome e continua para a regra cmd
            if(tokenIs(sym.PTO_VIRGULA)){
                consumir();
            }
        }
        cmd();
        lsta_cmd();
        if (tokenIs(sym.RSRVDA_END)) {
            consumir();
        }else{
            sintaxError("Espera-se begin");
            consumir(sym.RSRVDA_ELSE, sym.PTO_VIRGULA, sym.PONTO, sym.RSRVDA_END);
        }
    }

    //dec_sub ::= RSRVDA_PROCEDURE IDENTIFICADOR dec_sub2
    private void dec_sub() {
        if (tokenIs(sym.RSRVDA_PROCEDURE)) {
            consumir();
        }else{
            sintaxError("Espera-se procedure");
            consumir(sym.IDENTIFICADOR, sym.PTO_VIRGULA, sym.ABRE_P);
            if(tokenIs(sym.ABRE_P,sym.PTO_VIRGULA)){
                dec_sub2();
                return;
            }
        }
        if (tokenIs(sym.IDENTIFICADOR)) {
            consumir();
        }
        dec_sub2();
    }

    //dec_sub2 → prmtrs_formal PTO_VIRGULA dec_sub3 | PTO_VIRGULA dec_sub3
    private void dec_sub2() {
        if (tokenIs(sym.ABRE_P)) {
            prmtrs_formal();
        }
        if (tokenIs(sym.PTO_VIRGULA)) {
            consumir();
        }else{
            sintaxError("Espera-se ';'");
            consumir(sym.RSRVDA_BOOLEAN, sym.RSRVDA_INTEGER, sym.RSRVDA_BEGIN,sym.PTO_VIRGULA);
            if(tokenIs(sym.PTO_VIRGULA)){
                return;
            }
        }
        dec_sub3();
    }

    //prmtrs_formal::= ABRE_P sec_prmtrs_formal lsta_prmtrs_formal FECHA_P
    private void prmtrs_formal() {
        if (tokenIs(sym.ABRE_P)) {
            consumir();
        }else{
            sintaxError("Espera-se '('");
            consumir(sym.RSRVDA_VAR, sym.IDENTIFICADOR,sym.PTO_VIRGULA, sym.FECHA_P);
            if(tokenIs(sym.PTO_VIRGULA)){
                lsta_prmtrs_formal();
            }
            if(tokenIs(sym.FECHA_P)){
                consumir();
                return;
            }
        }
        sec_prmtrs_formal();
        lsta_prmtrs_formal();
        if (tokenIs(sym.FECHA_P)) {
            consumir();
        }else{
            sintaxError("Espera-se ')'");
            consumir(sym.PTO_VIRGULA);
        }
    }

    //dec_sub3 ::= pt_dec_var cmd_composto | cmd_composto;
    private void dec_sub3() {
        if (tokenIs(sym.RSRVDA_BOOLEAN, sym.RSRVDA_INTEGER)) {
            pt_dec_var();
        }
        cmd_composto();
    }

    //sec_prmtrs_formal ::= RSRVDA_VAR lsta_id DOIS_PONTOS tipo | lsta_id DOIS_PONTOS tipo;
    private void sec_prmtrs_formal() {
        if (tokenIs(sym.RSRVDA_VAR)) {
            consumir();
        }
        lsta_id();
        if (tokenIs(sym.DOIS_PONTOS)) {
            consumir();
        }else{
            sintaxError("Espear-se ':'");
            consumir(sym.RSRVDA_BOOLEAN, sym.RSRVDA_INTEGER,sym.PTO_VIRGULA);
            if(tokenIs(sym.PTO_VIRGULA)){
                sintaxError("Espera-se um tipo");
                return;
            }
        }
        tipo();
    }

    //lsta_prmtrs_formal ::= PTO_VIRGULA sec_prmtrs_formal lsta_prmtrs_formal|vazio;
    private void lsta_prmtrs_formal() {
        if (tokenIs(sym.PTO_VIRGULA)) {
            consumir();
            sec_prmtrs_formal();
            lsta_prmtrs_formal();
        } else {
            if (tokenIs(sym.FECHA_P)) {
                return;
            }
        }
    }

    //tipo ::= RSRVDA_BOOLEAN | RSRVDA_INTEGER
    private void tipo() {
        if (tokenIs(sym.RSRVDA_BOOLEAN, sym.RSRVDA_INTEGER)) {
            consumir();
        } else {
            sintaxError("Espera-se boolean ou int.");
        }
    }

    //pt2_dec_sub ::= pt_dec_sub | /**VAZIO**/;
    private void pt2_dec_sub() {
        if (tokenIs(sym.RSRVDA_PROCEDURE)) {
            pt_dec_sub();
        } else {
            if (tokenIs(sym.RSRVDA_BEGIN)) {
                return;
            }
        }
    }

    //cmd ::= IDENTIFICADOR cmd2 | cmd_composto | cmd_condicional | cmd_repetitivo;
    private void cmd() {
        if (tokenIs(sym.IDENTIFICADOR)) {
            consumir();
            cmd2();
        } else {
            if (tokenIs(sym.RSRVDA_BEGIN)) {
                cmd_composto();
            } else if (tokenIs(sym.RSRVDA_IF)) {
                cmd_condicional();
            } else if (tokenIs(sym.RSRVDA_WHILE)) {
                cmd_repetitivo();
            } else {
                sintaxError("Espera-se um comando.");
                consumir(sym.RSRVDA_END, sym.PTO_VIRGULA);
            }
        }
    }

    //lsta_cmd ::= PTO_VIRGULA cmd lsta_cmd | /**vazio**/;
    private void lsta_cmd() {
        if (tokenIs(sym.PTO_VIRGULA)) {
            consumir();
            cmd();
            lsta_cmd();
        } else {
            if (tokenIs(sym.RSRVDA_END)) {
                return;
            }else{
                sintaxError("Espera-se ';' ou END");
                consumir(sym.PTO_VIRGULA, sym.RSRVDA_END);
                if(tokenIs(sym.PTO_VIRGULA)){
                    lsta_cmd();
                }else{
                    return;
                }
            }
        }
    }

    //cmd_condicional ::= RSRVDA_IF expressao RSRVDA_THEN cmd cmd_condicional2;
    private void cmd_condicional() {
        if (tokenIs(sym.RSRVDA_IF)) {
            consumir();
        }
        expressao();
        if (tokenIs(sym.RSRVDA_THEN)) {
            consumir();
        }else{
            sintaxError("Espera-se THEN");
            consumir(sym.IDENTIFICADOR, sym.RSRVDA_BEGIN, sym.RSRVDA_WHILE, sym.RSRVDA_IF,//first(cmd)
                    sym.RSRVDA_ELSE,//first(cmd_cond2)
                    sym.PTO_VIRGULA, sym.RSRVDA_END);//follow(cmd_condicional);
            if(tokenIs(sym.RSRVDA_ELSE)){
                cmd_condicional2();
                return;
            }
            if(tokenIs(sym.PTO_VIRGULA, sym.RSRVDA_END)){
                return;
            }
        }
        cmd();
        cmd_condicional2();
    }

    //cmd_repetitivo ::= RSRVDA_WHILE expressao RSRVDA_DO cmd;
    private void cmd_repetitivo() {
        if (tokenIs(sym.RSRVDA_WHILE)) {
            consumir();
        }
        expressao();
        if (tokenIs(sym.RSRVDA_DO)) {
            consumir();
        }else{
            sintaxError("Espera-se DO");
            consumir(sym.IDENTIFICADOR, sym.RSRVDA_BEGIN, sym.RSRVDA_WHILE, sym.RSRVDA_IF,//first(cmd)
                    sym.RSRVDA_ELSE, sym.PTO_VIRGULA, sym.RSRVDA_END);//follow cmd_repetitivo
            if(tokenIs(sym.RSRVDA_ELSE, sym.PTO_VIRGULA, sym.RSRVDA_END)){//se for follow
                return;
            }
        }
        cmd();
    }

    //cmd2 ::= ATRIBUICAO atribuicao | chamada_sub
    private void cmd2() {
        if (tokenIs(sym.ATRIBUIÇÃO)) {
            consumir();
            atribuicao();
        } else {
            if (tokenIs(sym.ABRE_P)) {
                chamada_sub();
            }else{
                sintaxError("Espera-se ':=' ou '('");
                consumir(sym.RSRVDA_ELSE, sym.PTO_VIRGULA, sym.RSRVDA_END);
            }
        }
    }

    //cmd_condicional2 ::= RSRVDA_ELSE cmd | vazio
    private void cmd_condicional2() {
        if (tokenIs(sym.RSRVDA_ELSE)) {
            consumir();
            cmd();
        } else {
            if (tokenIs(sym.PTO_VIRGULA, sym.RSRVDA_END)) {
                return;
            }else{
                sintaxError("Espera-se ELSE, ';' ou END");
                consumir(sym.PTO_VIRGULA, sym.RSRVDA_END);
                return;
            }
        }
    }

    //expressao ::= exp_simples expressao2;
    private void expressao() {
        exp_simples();
        expressao2();
    }

    //atribuicao ::= expressao
    private void atribuicao() {
        expressao();
    }

    //chamada_sub ::= ABRE_P lsta_expressao FECHA_P |
    private void chamada_sub() {
        if (tokenIs(sym.ABRE_P)) {
            consumir();
            lsta_expressao();
            if (tokenIs(sym.FECHA_P)) {
                consumir();
            }else{
                sintaxError("Espera-se ')'");
                consumir(sym.RSRVDA_ELSE, sym.PTO_VIRGULA, sym.RSRVDA_END);
                return;
            }
        } else {
            if (tokenIs(sym.RSRVDA_ELSE, sym.RSRVDA_END, sym.PTO_VIRGULA)) {
                return;
            }
        }
    }
 
    //???????????????????????????????w
    //expressao2 ::= relacao exp_simples | vazio
    private void expressao2() {
        if (tokenIs(sym.OP_IGUAL, sym.OP_MAIOR, sym.OP_MAIOR_IGUAL, sym.OP_MENOR,
                sym.OP_MENOR_IGUAL, sym.OP_DIFERENTE)) {
            consumir();
            exp_simples();
        } else {
            if (tokenIs(sym.VIRGULA, sym.FECHA_P, sym.RSRVDA_DO, sym.RSRVDA_THEN,
                    sym.RSRVDA_ELSE, sym.PTO_VIRGULA, sym.RSRVDA_END)) {
                return;
            }
        }
    }

    //exp_simples ::= OP_SOMA termo lista_exp_simples | OP_SUB termo lista_exp_simples  | termo lista_exp_simples;
    private void exp_simples() {
        if (tokenIs(sym.OP_SOMA, sym.OP_SUB)) {
            consumir();
        }
        termo();
        lista_exp_simples();
    }

    //lsta_expressao ::= expressao lsta_expressao2;
    private void lsta_expressao() {
        expressao();
        lsta_expressao2();
    }

    //termo ::= fator lista_termo;
    private void termo() {
        fator();
        lista_termo();
    }

    //lista_exp_simples ::= OP_SOMA termo lista_exp_simples | OP_SUB termo lista_exp_simples |
    //OP_OR termo lista_exp_simples| /**vazio**/;
    private void lista_exp_simples() {
        if (tokenIs(sym.OP_SOMA, sym.OP_SUB, sym.OP_OR)) {
            consumir();
            termo();
            lista_exp_simples();
        } else {
            if (tokenIs(sym.OP_IGUAL, sym.OP_DIFERENTE, sym.OP_MENOR_IGUAL,
                    sym.OP_MENOR, sym.OP_MAIOR_IGUAL, sym.OP_MAIOR, sym.VIRGULA,
                    sym.FECHA_P, sym.RSRVDA_DO, sym.RSRVDA_THEN, sym.RSRVDA_ELSE,
                    sym.PTO_VIRGULA, sym.RSRVDA_END)) {
                return;
            }
        }
    }

    //lsta_expressao2 ::= VIRGULA expressao lsta_expressao2 | /*vazio*/ ;
    private void lsta_expressao2() {
        if (tokenIs(sym.VIRGULA)) {
            consumir();
            expressao();
            lsta_expressao2();
        } else {
            if (tokenIs(sym.FECHA_P)) {
                return;
            }
        }
    }

    //fator ::= variavel | NUM_INTEIRO | ABRE_P expressao FECHA_P | OP_NOT fator|
    //RSRVDA_FALSE | RSRVDA_TRUE
    private void fator() {
        if (tokenIs(sym.IDENTIFICADOR)) {
            variavel();
        } else if (tokenIs(sym.NUM_INTEIRO, sym.RSRVDA_FALSE, sym.RSRVDA_TRUE)) {
            consumir();
            return;
        } else if (tokenIs(sym.ABRE_P)) {
            consumir();
            expressao();
            if (tokenIs(sym.FECHA_P)) {
                consumir();
            }
            return;
        } else if (tokenIs(sym.OP_NOT)) {
            consumir();
            fator();
            return;
        }else{
            sintaxError("Expressão ilegal, espera-se um identificador, número, '(', NOT, FALSE ou TRUE");
            consumir(sym.OP_DIV, sym.OP_AND, sym.OP_MULT, sym.OP_SOMA, sym.OP_IGUAL, sym.OP_DIFERENTE, 
                    sym.OP_MENOR_IGUAL, sym.OP_MENOR, sym.OP_MAIOR_IGUAL, sym.OP_MAIOR, 
                    sym.VIRGULA, sym.FECHA_P, sym.RSRVDA_DO, sym.RSRVDA_THEN, sym.RSRVDA_ELSE, 
                    sym.PTO_VIRGULA, sym.RSRVDA_END);
        }
    }

    //lista_termo ::= OP_DIV fator lista_termo | OP_AND fator lista_termo |
    //OP_MULT fator lista_termo|/**vazio**/;
    private void lista_termo() {
        if (tokenIs(sym.OP_DIV, sym.OP_AND, sym.OP_MULT)) {
            consumir();
            fator();
            lista_termo();
            return;
        } else {
            if (tokenIs(sym.OP_SOMA, sym.OP_SUB, sym.OP_OR, sym.OP_IGUAL,
                    sym.OP_DIFERENTE, sym.OP_MENOR_IGUAL, sym.OP_MENOR,
                    sym.OP_MAIOR_IGUAL, sym.OP_MAIOR, sym.VIRGULA, sym.FECHA_P,
                    sym.RSRVDA_DO, sym.RSRVDA_THEN, sym.RSRVDA_ELSE, sym.PTO_VIRGULA,
                    sym.RSRVDA_END)) {
                return;
            }
        }
    }

    //variavel ::= IDENTIFICADOR
    private void variavel() {
        if (tokenIs(sym.IDENTIFICADOR)) {
            consumir();
        }
    }

    private void sintaxError(String msgErro) {
        if (erros != null) {
            erros.add(new SintaxError(tokenAtual.getLinha() + 1,
                    tokenAtual.getColunaInicio(),
                    msgErro));
        }
    }

}
