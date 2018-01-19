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
 * @author Matheus Prachedes Batista
 */
public class Sintatico {

    private ArrayList<SintaxError> erros;
    private ArrayList<Token> tokenIgnorados = new ArrayList<>();
    private LinguagemMaquina lMaquina = new LinguagemMaquina();
    private TabelaSimbolos tabela;
    private CustomScanner sc;
    private Token tokenAtual;

    public void consumir() {
        tokenAtual = sc.nextToken();
        if (tokenAtual.getTipo() >= 41) {
            lexicalError(tokenAtual);
            consumir();
        }
    }

    public void consumir(Integer... tokens) {
        while (!tokenIs(tokens) && !tokenIs(sym.EOF)) {
            tokenIgnorados.add(tokenAtual);
            consumir();
        }
    }

    public Sintatico(CustomScanner sc, ArrayList<SintaxError> erros) {
        this.sc = sc;
        tokenAtual = sc.nextToken();
        this.erros = erros;
        this.tabela = new TabelaSimbolos(erros);
    }

    public boolean start() {
        program();
        if (!erros.isEmpty()) {
            lMaquina.clear();
        }
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
        this.lMaquina.addInstrucao(LinguagemMaquina.INICIO_PROGRAMA, null);
        if (tokenIs(sym.RSRVDA_PROGRAM)) {
            consumir();
        } else {
            programError("program");
            return;
        }
        if (tokenIs(sym.IDENTIFICADOR)) {
            tabela.addSimbolo(tokenAtual, TabelaSimbolos.CATEGORIA_PROG_NAME, TabelaSimbolos.TIPO_NULL);
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
        }
        if (tokenIs(sym.EOF)) {
            consumir();
        } else {
            sintaxError("Espera-se fim de arquivo");
        }
        for (int i = 0; i < tabela.getNumVariaveis(); i++) {
            lMaquina.addInstrucao(LinguagemMaquina.DESALLOCA_MEMORIA, 1);
        }
        lMaquina.addInstrucao(LinguagemMaquina.PARA, null);
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
                    consumir(sym.RSRVDA_BOOLEAN, sym.RSRVDA_INTEGER, sym.RSRVDA_PROCEDURE, sym.RSRVDA_BEGIN, sym.PONTO);
                    if (tokenIs(sym.PONTO)) {
                        return;
                    }
                    bloco();
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
        pt2_dec_var2();
    }

    //pt_dec_var2 ::= pt_dec_var | /**vazio**/;
    public void pt2_dec_var2() {
        if (tokenIs(sym.RSRVDA_BOOLEAN, sym.RSRVDA_INTEGER)) {
            pt_dec_var();
        } else {
            if (tokenIs(sym.RSRVDA_BEGIN, sym.RSRVDA_PROCEDURE)) {
                return;
            }
        }
    }

    //dec_var ::= tipo lsta_id
    private void dec_var() {
        int tipo = tipo();
        ArrayList<Token> tokens = lsta_id();
        for (Token t : tokens) {
            boolean jaDeclarado = tabela.addSimbolo(t, TabelaSimbolos.CATEGORIA_VAR, tipo);
            if (!jaDeclarado) {
                lMaquina.addInstrucao(LinguagemMaquina.ALLOCA_MEMORIA, 1);
            }
        }
    }

    //lsta_id ::= IDENTIFICADOR{,IDENTIFICADOR}
    private ArrayList<Token> lsta_id() {
        ArrayList<Token> t = new ArrayList<>();
        if (tokenIs(sym.IDENTIFICADOR)) {
            t.add(tokenAtual);
            consumir();
        } else {
            sintaxError("Espera-se um identificador");
        }
        while (tokenIs(sym.VIRGULA)) {
            consumir();
            if (tokenIs(sym.IDENTIFICADOR)) {
                t.add(tokenAtual);
                consumir();
            } else {
                sintaxError("Espera-se um identificador");
            }
        }
        return t;
    }

//    //lsta_id ::= IDENTIFICADOR lsta_id2
//    private void lsta_id() {
//        if (tokenIs(sym.IDENTIFICADOR)) {
//            consumir();
//        } else {
//            sintaxError("Espera-se um identificador");
//        }
//        lsta_id2();
//    }
//
//    //lsta_id2 ::= VIRGULA lsta_id | vazio
//    private void lsta_id2() {
//        if (tokenIs(sym.VIRGULA)) {
//            consumir();
//            lsta_id();
//        } else {
//            if (tokenIs(sym.DOIS_PONTOS, sym.PTO_VIRGULA)) {
//                return;
//            }
//        }
//    }
    //apos_pt_dec_var ::= pt_dec_sub cmd_composto|cmd_composto;
    private void apos_pt_dec_var() {
        if (tokenIs(sym.RSRVDA_PROCEDURE)) {
            pt_dec_sub();
            if (!tokenIs(sym.RSRVDA_BEGIN)) {
                sintaxError("Espera-se PROCEDURE ou BEGIN");
                consumir(sym.PONTO);
                return;
            }
            cmd_composto();
        } else {
            if (tokenIs(sym.RSRVDA_BEGIN)) {
                cmd_composto();
            } else {
                sintaxError("Espera-se PROCEDURE ou BEGIN");
                consumir(sym.PONTO);
            }
        }
    }

    //pt_dec_sub ::= dec_sub PTO_VIRGULA pt2_dec_sub;
    private void pt_dec_sub() {
        dec_sub();
        if (tokenIs(sym.PTO_VIRGULA)) {
            consumir();
        } else {
            sintaxError("Espera-se ';'");
            consumir(sym.RSRVDA_PROCEDURE, sym.RSRVDA_BEGIN);
        }
        pt2_dec_sub();
    }

    //cmd_composto ::= RSRVDA_BEGIN cmd lsta_cmd RSRVDA_END;
    private void cmd_composto() {
        if (tokenIs(sym.RSRVDA_BEGIN)) {
            consumir();
        } else {
            sintaxError("Espera-se begin");
            //FIRST CMD
            consumir(sym.IDENTIFICADOR, sym.RSRVDA_BEGIN, sym.RSRVDA_WHILE, sym.RSRVDA_IF, sym.RSRVDA_END, sym.PTO_VIRGULA);
            //Se não encontrou o first do CMD, mas encontrou o follow do lsta_cmd, então consome o end e termina esta regra
            if (tokenIs(sym.RSRVDA_END)) {
                consumir();
                return;
            }
            //Se encontrou o first de lsta_cmd, então consome e continua para a regra cmd
            if (tokenIs(sym.PTO_VIRGULA)) {
                consumir();
            }
        }
        cmd();
        lsta_cmd();
        if (tokenIs(sym.RSRVDA_END)) {
            consumir();
        } else {
            sintaxError("Espera-se END");
            consumir(sym.RSRVDA_ELSE, sym.PTO_VIRGULA, sym.PONTO, sym.RSRVDA_END);
        }
    }

    //dec_sub ::= RSRVDA_PROCEDURE IDENTIFICADOR dec_sub2
    private void dec_sub() {
        if (tokenIs(sym.RSRVDA_PROCEDURE)) {
            consumir();
        } else {
            sintaxError("Espera-se procedure");
            consumir(sym.IDENTIFICADOR, sym.PTO_VIRGULA, sym.ABRE_P);
            if (tokenIs(sym.ABRE_P, sym.PTO_VIRGULA)) {
                dec_sub2();
                return;
            }
        }
        if (tokenIs(sym.IDENTIFICADOR)) {
            Token t = tokenAtual;
            tabela.addSimbolo(t, TabelaSimbolos.CATEGORIA_PROC, TabelaSimbolos.TIPO_NULL);
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
        } else {
            consumir(sym.RSRVDA_VAR, sym.RSRVDA_BEGIN, sym.PTO_VIRGULA);
            if (tokenIs(sym.PTO_VIRGULA)) {
                consumir();
            }
            if (tokenIs(sym.RSRVDA_VAR)) {
                prmtrs_formal();
                if (tokenIs(sym.PTO_VIRGULA)) {
                    consumir();
                } else {
                    sintaxError("Espera-se ';'");
                    consumir(sym.RSRVDA_BEGIN, sym.PTO_VIRGULA);
                    if (tokenIs(sym.PTO_VIRGULA)) {
                        return;
                    }
                }
            } else {
                sintaxError("Espera-se ';' ou '('");
            }
        }
        dec_sub3();
    }

    //prmtrs_formal::= ABRE_P sec_prmtrs_formal lsta_prmtrs_formal FECHA_P
    private void prmtrs_formal() {
        if (tokenIs(sym.ABRE_P)) {
            consumir();
        } else {
            sintaxError("Espera-se '('");
            consumir(sym.RSRVDA_VAR, sym.PTO_VIRGULA, sym.FECHA_P);
            if (tokenIs(sym.PTO_VIRGULA)) {
                lsta_prmtrs_formal();
            }
            if (tokenIs(sym.FECHA_P)) {
                consumir();
                return;
            }
        }
        sec_prmtrs_formal();
        lsta_prmtrs_formal();
        if (tokenIs(sym.FECHA_P)) {
            consumir();
        } else {
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
        tabela.removeVarLocaisProcedimento();
    }

    //sec_prmtrs_formal ::= RSRVDA_VAR lsta_id DOIS_PONTOS tipo | lsta_id DOIS_PONTOS tipo;
    private void sec_prmtrs_formal() {
        if (tokenIs(sym.RSRVDA_VAR)) {
            consumir();
        }
        ArrayList<Token> ids = lsta_id();
        int tipo = TabelaSimbolos.TIPO_NULL;
        if (tokenIs(sym.DOIS_PONTOS)) {
            consumir();
        } else {
            sintaxError("Espear-se ':'");
            consumir(sym.RSRVDA_BOOLEAN, sym.RSRVDA_INTEGER, sym.PTO_VIRGULA);
            if (tokenIs(sym.PTO_VIRGULA)) {
                sintaxError("Espera-se um tipo");
                return;
            }
        }
        if (tokenIs(sym.RSRVDA_INTEGER, sym.RSRVDA_BOOLEAN)) {
            tipo = tipo();
        } else {
            sintaxError("Espera-se boolean ou int");
            consumir(sym.PTO_VIRGULA, sym.FECHA_P);
            return;
        }
        for (Token t : ids) {
            tabela.addSimbolo(t, TabelaSimbolos.CATEGORIA_PARA, tipo);
        }
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
    private int tipo() {
        if (tokenIs(sym.RSRVDA_BOOLEAN)) {
            consumir();
            return TabelaSimbolos.TIPO_BOOLEAN;
        }
        if (tokenIs(sym.RSRVDA_INTEGER)) {
            consumir();
            return TabelaSimbolos.TIPO_INT;
        }
        sintaxError("Espera-se boolean ou int.");
        consumir(sym.IDENTIFICADOR, sym.PTO_VIRGULA, sym.FECHA_P);
        return TabelaSimbolos.TIPO_NULL;
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
            }
        }
    }

    //cmd_condicional ::= RSRVDA_IF expressao RSRVDA_THEN cmd cmd_condicional2;
    private void cmd_condicional() {
        if (tokenIs(sym.RSRVDA_IF)) {
            consumir();
        }
        int tipo = expressao();
        int endereco = lMaquina.getEnderecoAtual();
        lMaquina.addInstrucao(LinguagemMaquina.DESVIO_FALSE, null);
        if (tipo != TabelaSimbolos.TIPO_BOOLEAN) {
            sintaxError("A expressão deve ser do IF deve ser do tipo booleano");
        }
        if (tokenIs(sym.RSRVDA_THEN)) {
            consumir();
        } else {
            sintaxError("Espera-se THEN");
            consumir(sym.IDENTIFICADOR, sym.RSRVDA_BEGIN, sym.RSRVDA_WHILE, sym.RSRVDA_IF,//first(cmd)
                    sym.RSRVDA_ELSE,//first(cmd_cond2)
                    sym.PTO_VIRGULA, sym.RSRVDA_END);//follow(cmd_condicional);
            if (tokenIs(sym.RSRVDA_ELSE)) {
                cmd_condicional2();
                return;
            }
            if (tokenIs(sym.PTO_VIRGULA, sym.RSRVDA_END)) {
                return;
            }
        }
        cmd();
        lMaquina.addInstrucao(endereco, LinguagemMaquina.DESVIO_FALSE, lMaquina.getEnderecoAtual() + 1);
        cmd_condicional2();
    }

    //cmd_condicional2 ::= RSRVDA_ELSE cmd | vazio
    private void cmd_condicional2() {
        if (tokenIs(sym.RSRVDA_ELSE)) {
            int enderecoJumpTrue = lMaquina.getEnderecoAtual();
            lMaquina.addInstrucao(LinguagemMaquina.DESVIO_SEMPRE, null);
            lMaquina.addInstrucao(LinguagemMaquina.NADA, null);
            consumir();
            cmd();
            lMaquina.addInstrucao(enderecoJumpTrue, LinguagemMaquina.DESVIO_SEMPRE, lMaquina.getEnderecoAtual());
            lMaquina.addInstrucao(LinguagemMaquina.NADA, null);
        } else {
            if (tokenIs(sym.PTO_VIRGULA, sym.RSRVDA_END)) {
                return;
            }
        }
    }

    //cmd_repetitivo ::= RSRVDA_WHILE expressao RSRVDA_DO cmd;
    private void cmd_repetitivo() {
        if (tokenIs(sym.RSRVDA_WHILE)) {
            consumir();
        }
        int enderecoJumpBack = lMaquina.getEnderecoAtual();
        lMaquina.addInstrucao(LinguagemMaquina.NADA, null);
        int tipo = expressao();
        if (tipo != TabelaSimbolos.TIPO_BOOLEAN) {
            sintaxError("A expressão deve ser do WHILE deve ser do tipo booleano");
        }
        int enderecoEndLoop = lMaquina.getEnderecoAtual();
        lMaquina.addInstrucao(LinguagemMaquina.DESVIO_FALSE, null);
        if (tokenIs(sym.RSRVDA_DO)) {
            consumir();
        } else {
            sintaxError("Espera-se DO");
            consumir(sym.IDENTIFICADOR, sym.RSRVDA_BEGIN, sym.RSRVDA_WHILE, sym.RSRVDA_IF,//first(cmd)
                    sym.RSRVDA_ELSE, sym.PTO_VIRGULA, sym.RSRVDA_END);//follow cmd_repetitivo
            if (tokenIs(sym.RSRVDA_ELSE, sym.PTO_VIRGULA, sym.RSRVDA_END)) {//se for follow
                return;
            }
        }
        cmd();
        lMaquina.addInstrucao(LinguagemMaquina.DESVIO_SEMPRE, enderecoJumpBack);
        lMaquina.addInstrucao(enderecoEndLoop, LinguagemMaquina.DESVIO_FALSE, lMaquina.getEnderecoAtual());
        lMaquina.addInstrucao(LinguagemMaquina.NADA, null);
    }

    //cmd2 ::= ATRIBUICAO atribuicao | chamada_sub
    private void cmd2() {
        Token id = tokenAtual;
        consumir();
        if (tokenIs(sym.ATRIBUIÇÃO)) {
            int tipo = tabela.buscaSimbolo(id, TabelaSimbolos.CATEGORIA_VAR);
            consumir();
            int tipo2 = atribuicao();
            lMaquina.addInstrucao(LinguagemMaquina.ARMAZENA, tabela.enderecoSimbolo(id));
            if (tipo != tipo2) {
                sintaxError("Atribuição com tipos conflitantes");
            }
        } else {
            if (tokenIs(sym.ABRE_P)) {
                tabela.buscaSimbolo(id, TabelaSimbolos.CATEGORIA_PROC);
                if (id.getLexema().equals("read")) {
                    chamada_sub_read(id);
                } else {
                    chamada_sub(id);
                }
            } else {
                sintaxError("Espera-se ':=' ou '('");
                consumir(sym.RSRVDA_ELSE, sym.PTO_VIRGULA, sym.RSRVDA_END);
            }
        }
    }

    //expressao ::= exp_simples expressao2;
    private int expressao() {
        int tipo1 = exp_simples();
        boolean opEntreInt = false;
        if (tokenIs(sym.OP_IGUAL, sym.OP_MAIOR, sym.OP_MAIOR_IGUAL, sym.OP_MENOR,
                sym.OP_MENOR_IGUAL, sym.OP_DIFERENTE)) {
            Token t = tokenAtual;
            if (!tokenIs(sym.OP_IGUAL, sym.OP_DIFERENTE)) {
                opEntreInt = true;
            }
            consumir();
            int tipo2 = exp_simples();
            switch (t.getTipo()) {
                case sym.OP_IGUAL:
                    lMaquina.addInstrucao(LinguagemMaquina.COMAPRA_SE_IGUAL, null);
                    break;
                case sym.OP_DIFERENTE:
                    lMaquina.addInstrucao(LinguagemMaquina.COMPARA_SE_DESIGUAL, null);
                    break;
                case sym.OP_MAIOR:
                    lMaquina.addInstrucao(LinguagemMaquina.COMPARA_SE_MAIOR, null);
                    break;
                case sym.OP_MAIOR_IGUAL:
                    lMaquina.addInstrucao(LinguagemMaquina.COMPARA_SE_MAIOR_IGUAL, null);
                    break;
                case sym.OP_MENOR:
                    lMaquina.addInstrucao(LinguagemMaquina.COMPARA_SE_MENOR, null);
                    break;
                case sym.OP_MENOR_IGUAL:
                    lMaquina.addInstrucao(LinguagemMaquina.COMPARA_SE_MENOR_IGUAL, null);
                    break;
            }
            if (tipo1 != tipo2) {
                sintaxError("Operação de comparação deve ser entre tipos iguais");
            } else if (opEntreInt && (tipo1 != TabelaSimbolos.TIPO_INT || tipo2 != TabelaSimbolos.TIPO_INT)) {
                sintaxError("Operação de comparação deve ser entre tipos inteiros");
            }
            return TabelaSimbolos.TIPO_BOOLEAN;
        }
        return tipo1;
    }

//    //???????????????????????????????w
//    //expressao2 ::= relacao exp_simples | vazio
//    private void expressao2() {
//        if (tokenIs(sym.OP_IGUAL, sym.OP_MAIOR, sym.OP_MAIOR_IGUAL, sym.OP_MENOR,
//                sym.OP_MENOR_IGUAL, sym.OP_DIFERENTE)) {
//            consumir();
//            exp_simples();
//        } else {
//            if (tokenIs(sym.VIRGULA, sym.FECHA_P, sym.RSRVDA_DO, sym.RSRVDA_THEN,
//                    sym.RSRVDA_ELSE, sym.PTO_VIRGULA, sym.RSRVDA_END)) {
//                return;
//            }
//        }
//    }
    //atribuicao ::= expressao
    private int atribuicao() {
        int tipo = expressao();
        return tipo;
    }

    //chamada_sub ::= ABRE_P lsta_expressao FECHA_P |
    private void chamada_sub(Token idProcedimento) {
        if (tokenIs(sym.ABRE_P)) {
            consumir();
            ArrayList<Integer> tipos = lsta_expressao(idProcedimento.getLexema().equals("write"));
            tabela.parametrosChamadaProcedimento(tipos, idProcedimento);
            if (tokenIs(sym.FECHA_P)) {
                consumir();
            } else {
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

    private void chamada_sub_read(Token idProcedimento) {
        if (tokenIs(sym.ABRE_P)) {
            consumir();
            ArrayList<Integer> tipos = new ArrayList<>();
            if (tokenIs(sym.IDENTIFICADOR)) {
                lMaquina.addInstrucao(LinguagemMaquina.LEITURA, null);
                lMaquina.addInstrucao(LinguagemMaquina.ARMAZENA, tabela.enderecoSimbolo(tokenAtual));
                tipos.add(tabela.buscaSimbolo(tokenAtual, TabelaSimbolos.CATEGORIA_VAR));
                consumir();
                while (tokenIs(sym.VIRGULA)) {
                    consumir();
                    if (tokenIs(sym.IDENTIFICADOR)) {
                        lMaquina.addInstrucao(LinguagemMaquina.LEITURA, null);
                        lMaquina.addInstrucao(LinguagemMaquina.ARMAZENA, tabela.enderecoSimbolo(tokenAtual));
                        tipos.add(tabela.buscaSimbolo(tokenAtual, TabelaSimbolos.CATEGORIA_VAR));
                        consumir();
                    } else {
                        sintaxError("Espera-se um identificador");
                        consumir(sym.FECHA_P, sym.RSRVDA_ELSE, sym.PTO_VIRGULA, sym.RSRVDA_END);
                    }
                }
                tabela.parametrosChamadaProcedimento(tipos, idProcedimento);
            } else {
                sintaxError("Espera-se um identificador");
                consumir(sym.FECHA_P, sym.RSRVDA_ELSE, sym.PTO_VIRGULA, sym.RSRVDA_END);
            }
            if (tokenIs(sym.FECHA_P)) {
                consumir();
            } else {
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

    //exp_simples ::= OP_SOMA termo lista_exp_simples | OP_SUB termo lista_exp_simples  | termo lista_exp_simples;
    private int exp_simples() {
        boolean sinal = false;
        if (tokenIs(sym.OP_SOMA, sym.OP_SUB)) {
            consumir();
            if (tokenIs(sym.OP_SUB)) {
                sinal = true;
            }
        }
        int tipo1 = termo();
        if (sinal) {
            lMaquina.addInstrucao(LinguagemMaquina.INVERTE, null);
        }
        int tipoOp;
        int tipo2;
        while (tokenIs(sym.OP_SOMA, sym.OP_SUB, sym.OP_OR)) {
            Token t = tokenAtual;
            if (tokenIs(sym.OP_OR)) {
                tipoOp = TabelaSimbolos.TIPO_BOOLEAN;
            } else {
                tipoOp = TabelaSimbolos.TIPO_INT;
            }
            consumir();
            tipo2 = termo();

            switch (t.getTipo()) {
                case sym.OP_SOMA:
                    lMaquina.addInstrucao(LinguagemMaquina.SOMA, null);
                    break;
                case sym.OP_SUB:
                    lMaquina.addInstrucao(LinguagemMaquina.SUB, null);
                    break;
                case sym.OP_OR:
                    lMaquina.addInstrucao(LinguagemMaquina.SOMA, null);
                    break;
            }

            boolean erro = false;
            if (tipo1 != tipoOp && tipo1 != TabelaSimbolos.TIPO_NULL) {
                erro = true;
            }
            if (tipo2 != tipoOp && tipo2 != TabelaSimbolos.TIPO_NULL) {
                erro = true;
            }
            if (erro) {
                if (tipoOp == TabelaSimbolos.TIPO_BOOLEAN) {
                    sintaxError("Operação OR deve ser entre boleanos");
                } else {
                    sintaxError("Operação númerica deve ser entre números inteiros");
                }
            }
        }
        return tipo1;
    }

//    //lista_exp_simples ::= OP_SOMA termo lista_exp_simples | OP_SUB termo lista_exp_simples |
//    //OP_OR termo lista_exp_simples| /**vazio**/;
//    private void lista_exp_simples() {
//        if (tokenIs(sym.OP_SOMA, sym.OP_SUB, sym.OP_OR)) {
//            consumir();
//            termo();
//            lista_exp_simples();
//        } else {
//            if (tokenIs(sym.OP_IGUAL, sym.OP_DIFERENTE, sym.OP_MENOR_IGUAL,
//                    sym.OP_MENOR, sym.OP_MAIOR_IGUAL, sym.OP_MAIOR, sym.VIRGULA,
//                    sym.FECHA_P, sym.RSRVDA_DO, sym.RSRVDA_THEN, sym.RSRVDA_ELSE,
//                    sym.PTO_VIRGULA, sym.RSRVDA_END)) {
//                return;
//            }
//        }
//    }
    //lsta_expressao ::= expressao lsta_expressao2;
    private ArrayList<Integer> lsta_expressao(boolean isWrite) {
        ArrayList<Integer> tipos = new ArrayList<>();
        tipos.add(expressao());
        if (isWrite) {
            lMaquina.addInstrucao(LinguagemMaquina.IMPRIME, null);
        }
        while (tokenIs(sym.VIRGULA)) {
            consumir();
            tipos.add(expressao());
            if (isWrite) {
                lMaquina.addInstrucao(LinguagemMaquina.IMPRIME, null);
            }
        }
        return tipos;
    }

//      //lsta_expressao2 ::= VIRGULA expressao lsta_expressao2 | /*vazio*/ ;
//    private void lsta_expressao2() {
//        if (tokenIs(sym.VIRGULA)) {
//            consumir();
//            expressao();
//            lsta_expressao2();
//        } else {
//            if (tokenIs(sym.FECHA_P)) {
//                return;
//            }
//        }
//    }
    //termo ::= fator lista_termo;
    private int termo() {
        int tipo1 = fator();
        int tipoOp;
        int tipo2;
        while (tokenIs(sym.OP_DIV, sym.OP_AND, sym.OP_MULT)) {
            Token t = tokenAtual;
            if (tokenIs(sym.OP_AND)) {
                tipoOp = TabelaSimbolos.TIPO_BOOLEAN;
            } else {
                tipoOp = TabelaSimbolos.TIPO_INT;
            }
            consumir();
            tipo2 = fator();

            switch (t.getTipo()) {
                case sym.OP_DIV:
                    lMaquina.addInstrucao(LinguagemMaquina.DIV, null);
                    break;
                case sym.OP_AND:
                    lMaquina.addInstrucao(LinguagemMaquina.AND, null);
                    break;
                case sym.OP_MULT:
                    lMaquina.addInstrucao(LinguagemMaquina.MULT, null);
                    break;
                default:
                    break;
            }

            boolean erro = false;
            if (tipo1 != tipoOp && tipo1 != TabelaSimbolos.TIPO_NULL) {
                erro = true;
            }
            if (tipo2 != tipoOp && tipo2 != TabelaSimbolos.TIPO_NULL) {
                erro = true;
            }
            if (erro) {
                if (tipoOp == TabelaSimbolos.TIPO_BOOLEAN) {
                    sintaxError("Operação AND deve ser entre boleanos");
                } else {
                    sintaxError("Operação númerica deve ser entre números inteiros");
                }
            }

            //if (tipo1 != tipo2 && tipo1 != tipoOp) {
//                if (tipo1 != TabelaSimbolos.TIPO_NULL && tipo2 != TabelaSimbolos.TIPO_NULL) {
//                    if (tipoOp == TabelaSimbolos.TIPO_BOOLEAN) {
//                        sintaxError("Operação AND deve ser entre boleanos");
//                    } else {
//                        sintaxError("Operação númerica deve ser entre números inteiros");
//                    }
//                } else {
//                    tipo1 = tipoOp;
//                }
        }
        return tipo1;
    }

//    //lista_termo ::= OP_DIV fator lista_termo | OP_AND fator lista_termo |
//    //OP_MULT fator lista_termo|/**vazio**/;
//    private void lista_termo() {
//        if (tokenIs(sym.OP_DIV, sym.OP_AND, sym.OP_MULT)) {
//            consumir();
//            fator();
//            lista_termo();
//            return;
//        } else {
//            if (tokenIs(sym.OP_SOMA, sym.OP_SUB, sym.OP_OR, sym.OP_IGUAL,
//                    sym.OP_DIFERENTE, sym.OP_MENOR_IGUAL, sym.OP_MENOR,
//                    sym.OP_MAIOR_IGUAL, sym.OP_MAIOR, sym.VIRGULA, sym.FECHA_P,
//                    sym.RSRVDA_DO, sym.RSRVDA_THEN, sym.RSRVDA_ELSE, sym.PTO_VIRGULA,
//                    sym.RSRVDA_END)) {
//                return;
//            }
//        }
//
//    }
//fator ::= variavel | NUM_INTEIRO | ABRE_P expressao FECHA_P | OP_NOT fator|
//RSRVDA_FALSE | RSRVDA_TRUE
    private int fator() {
        int tipo = TabelaSimbolos.TIPO_NULL;
        if (tokenIs(sym.IDENTIFICADOR)) {
            tipo = variavel();
        } else if (tokenIs(sym.NUM_INTEIRO)) {
            tipo = TabelaSimbolos.TIPO_INT;
            lMaquina.addInstrucao(LinguagemMaquina.CARREGA_CONSTANTE, Integer.valueOf(tokenAtual.getLexema()));
            consumir();
        } else if (tokenIs(sym.RSRVDA_FALSE, sym.RSRVDA_TRUE)) {
            if (tokenIs(sym.RSRVDA_FALSE)) {
                lMaquina.addInstrucao(LinguagemMaquina.CARREGA_CONSTANTE, 0);
            } else {
                lMaquina.addInstrucao(LinguagemMaquina.CARREGA_CONSTANTE, 1);
            }
            tipo = TabelaSimbolos.TIPO_BOOLEAN;
            consumir();
        } else if (tokenIs(sym.ABRE_P)) {
            consumir();
            tipo = expressao();
            if (tokenIs(sym.FECHA_P)) {
                consumir();
            } else {
                sintaxError("Está faltando ')' na expressão");
                consumir(sym.OP_DIV, sym.OP_AND, sym.OP_MULT, sym.OP_SOMA, sym.OP_IGUAL, sym.OP_DIFERENTE,
                        sym.OP_MENOR_IGUAL, sym.OP_MENOR, sym.OP_MAIOR_IGUAL, sym.OP_MAIOR,
                        sym.VIRGULA, sym.FECHA_P, sym.RSRVDA_DO, sym.RSRVDA_THEN, sym.RSRVDA_ELSE,
                        sym.PTO_VIRGULA, sym.RSRVDA_END);
            }
        } else if (tokenIs(sym.OP_NOT)) {//OPERAção entre tipos está certo?-------------------------------------------
            consumir();
            tipo = fator();
            lMaquina.addInstrucao(LinguagemMaquina.NOT, null);
            if (tipo != TabelaSimbolos.TIPO_BOOLEAN) {
                if (tipo != TabelaSimbolos.TIPO_NULL) {
                    sintaxError("Operação NOT com um valor não boleano");
                }
                tipo = TabelaSimbolos.TIPO_BOOLEAN;
            }
        } else {
            sintaxError("Expressão ilegal, espera-se um identificador, número, '(', NOT, FALSE ou TRUE");
            consumir(sym.OP_DIV, sym.OP_AND, sym.OP_MULT, sym.OP_SOMA, sym.OP_IGUAL, sym.OP_DIFERENTE,
                    sym.OP_MENOR_IGUAL, sym.OP_MENOR, sym.OP_MAIOR_IGUAL, sym.OP_MAIOR,
                    sym.VIRGULA, sym.FECHA_P, sym.RSRVDA_DO, sym.RSRVDA_THEN, sym.RSRVDA_ELSE,
                    sym.PTO_VIRGULA, sym.RSRVDA_END);
        }
        return tipo;
    }

    //variavel ::= IDENTIFICADOR
    private int variavel() {
        int tipo = TabelaSimbolos.TIPO_NULL;
        if (tokenIs(sym.IDENTIFICADOR)) {
            tipo = tabela.buscaSimbolo(tokenAtual, TabelaSimbolos.CATEGORIA_VAR);
            lMaquina.addInstrucao(LinguagemMaquina.CARREGA_VALOR, tabela.enderecoSimbolo(tokenAtual));
            consumir();
        }
        return tipo;
    }

    private void sintaxError(String msgErro) {
        if (erros != null) {
            erros.add(new SintaxError(tokenAtual.getLinha() + 1,
                    tokenAtual.getColunaInicio(), tokenAtual.getOffset(),
                    msgErro));
        }
    }

    public ArrayList<Token> getTokenIgnorados() {
        return tokenIgnorados;
    }

    private void lexicalError(Token tokenAtual) {
        if (erros != null) {
            erros.add(new SintaxError(tokenAtual.getLinha() + 1, tokenAtual.getColunaInicio(), tokenAtual.getOffset(),
                    "Erro léxico: " + tokenAtual.getTokenName()));
        }
    }

    public LinguagemMaquina getCodigoMaquina() {
        return lMaquina;
    }

}
