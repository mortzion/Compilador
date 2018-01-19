package interpretador;


import java.util.ArrayList;
import parser.LinguagemMaquina;
import java.util.Stack;
import parser.Instrucao;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Matheus Prachedes Batista
 */
public class Interpretador {

    private IO io;
    
    private ArrayList<Instrucao> c;
    private Stack<Integer> d;
    private int i;
    private int s;

    public Interpretador(ArrayList<Instrucao> programa, IO io) {
        this.c = programa;
        this.d = new Stack<>();
        this.io = io;
    }

    public void run() {
        while (i < c.size()) {
            Instrucao inst = (c.get(i));
            boolean desvio = execute(inst);
            if (desvio) {
                i = getRealAddress(inst.operando);
            } else {
                i++;
            }
            if(inst.instrucao.equals("PARA"))return;
        }
    }

    public int getRealAddress(int logicalAddress){
        for(int i=0;i<c.size();i++){
            if(c.get(i).rotulo!=null && c.get(i).rotulo == logicalAddress){
                return i;
            }
        }
        return c.size();
    }
    
    private boolean execute(Instrucao instrucao) {
        int aux;
        switch (instrucao.instrucao) {
            case LinguagemMaquina.INICIO_PROGRAMA:
                d.clear();
                break;
            case LinguagemMaquina.ALLOCA_MEMORIA:
                for(int i=0;i<instrucao.operando;i++){
                    d.add(0);
                }
                break;
            case LinguagemMaquina.DESALLOCA_MEMORIA:
                for(int i=0;i<instrucao.operando;i++){
                    d.pop();
                }
                break;
            case LinguagemMaquina.CARREGA_CONSTANTE:
                d.add(instrucao.operando);
                break;
            case LinguagemMaquina.CARREGA_VALOR:
                d.add(d.get(instrucao.operando));
                break;
            case LinguagemMaquina.ARMAZENA:
                d.set(instrucao.operando, d.pop());
                break;
            case LinguagemMaquina.SOMA:
                aux = d.pop();
                d.add(d.pop() + aux);
                break;
            case LinguagemMaquina.SUB:
                aux = d.pop();
                d.add(d.pop() - aux);
                break;
            case LinguagemMaquina.MULT:
                aux = d.pop();
                d.add(d.pop() * aux);
                break;
            case LinguagemMaquina.DIV:
                aux = d.pop();
                d.add(d.pop() / aux);
                break;
            case LinguagemMaquina.INVERTE:
                d.add(-d.pop());
                break;
            case LinguagemMaquina.AND:
                d.add(d.pop() & d.pop());
                break;
            case LinguagemMaquina.OR:
                d.add(d.pop() | d.pop());
                break;
            case LinguagemMaquina.NOT:
                d.add(1 - d.pop());
                break;
            case LinguagemMaquina.COMAPRA_SE_IGUAL:
                aux = d.pop();
                d.add((((int) d.pop()) == ((int) aux)) ? 1 : 0);
                break;
            case LinguagemMaquina.COMPARA_SE_DESIGUAL:
                aux = d.pop();
                d.add((((int) d.pop()) != ((int) aux)) ? 1 : 0);
                break;
            case LinguagemMaquina.COMPARA_SE_MAIOR:
                aux = d.pop();
                d.add((((int) d.pop()) > ((int) aux)) ? 1 : 0);
                break;
            case LinguagemMaquina.COMPARA_SE_MAIOR_IGUAL:
                aux = d.pop();
                d.add((((int) d.pop()) >= ((int) aux)) ? 1 : 0);
                break;
            case LinguagemMaquina.COMPARA_SE_MENOR:
                aux = d.pop();
                d.add((((int) d.pop()) < ((int) aux)) ? 1 : 0);
                break;
            case LinguagemMaquina.COMPARA_SE_MENOR_IGUAL:
                aux = d.pop();
                d.add((((int) d.pop()) <= ((int) aux)) ? 1 : 0);
                break;
            case LinguagemMaquina.DESVIO_SEMPRE:
                return true;
            case LinguagemMaquina.DESVIO_FALSE:
                return d.pop() == 0;
            case LinguagemMaquina.NADA:
                //Nada (na piscinia ou no rio).
                break;
            case LinguagemMaquina.LEITURA:
                d.add(io.readInt());
                break;
            case LinguagemMaquina.IMPRIME:
                io.printInt(d.pop());
                break;
        }
        return false;
    }

}
