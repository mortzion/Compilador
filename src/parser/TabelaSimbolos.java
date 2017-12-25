/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;
import jflex.Token;

/**
 *
 * @author Matheus Prachedes Batista
 */
public class TabelaSimbolos {

    public static final int VARIAVEL_NAO_ENCONTRADA = -1;
    
    public static final int CATEGORIA_VAR = 1;//Simbolos que representam variaveis
    public static final int CATEGORIA_PROC = 2;//Simbolos que representam nome de procedimentos
    public static final int CATEGORIA_PARA = 3;//Simbolos que representam variaveis de parametro
    public static final int CATEGORIA_PROG_NAME = 4;//Simbolo que representam o nome do programa

    public static final int TIPO_INT = 1;
    public static final int TIPO_BOOLEAN = 2;
    public static final int TIPO_NULL = 4;
    public static final int TIPO_READ = -1;
    public static final int TIPO_WRITE = -1;

    private HashMap<KeyTabelaSimbolos, ValueTabelaSimbolos> tabelaSimbolosGlobal;
    private ArrayList<LinkedHashMap<KeyTabelaSimbolos, ValueTabelaSimbolos>> tabelasProcedimentos;
    private LinkedHashMap<KeyTabelaSimbolos, ValueTabelaSimbolos> procedimento;
    private ArrayList<SintaxError> erros;
    private int enderecoAtual;
    
    public TabelaSimbolos(ArrayList<SintaxError> erros) {
        this.tabelaSimbolosGlobal = new HashMap();
        this.tabelasProcedimentos = new ArrayList();
        procedimento = null;
        this.erros = erros;
        this.enderecoAtual = 0;
        init();
    }

    private final void init() {
        ValueTabelaSimbolos read = new ValueTabelaSimbolos("read", CATEGORIA_PROC, TIPO_READ);
        ValueTabelaSimbolos write = new ValueTabelaSimbolos("write", CATEGORIA_PROC, TIPO_WRITE);
        tabelaSimbolosGlobal.put(read.getKey(), read);
        tabelaSimbolosGlobal.put(write.getKey(), write);
    }

    public boolean addSimbolo(Token t, int categoria, int tipo) {
        boolean erro = false;
        ValueTabelaSimbolos v = new ValueTabelaSimbolos(t, categoria, tipo,enderecoAtual);
        KeyTabelaSimbolos k = v.getKey();
        if (procedimento != null) {
            if (procedimento.containsKey(k)) {
                erros.add(new SintaxError(t.getLinha(), t.getColunaInicio(), t.getOffset(), getMsgErroAdd(categoria)));
                erro = true;
            } else {
                procedimento.put(k, v);
            }
        } else {
            if (tabelaSimbolosGlobal.containsKey(k)) {
                erros.add(new SintaxError(t.getLinha(), t.getColunaInicio(), t.getOffset(), getMsgErroAdd(categoria)));
                erro = true;
            } else {
                if (categoria == CATEGORIA_PROC) {
                    v.tipo = tabelasProcedimentos.size();
                    procedimento = new LinkedHashMap<>();
                    tabelasProcedimentos.add(procedimento);
                }
                tabelaSimbolosGlobal.put(k, v);
            }
        }
        if(categoria == CATEGORIA_VAR){
            enderecoAtual++;
        }
        return erro;
    }

    public int buscaSimbolo(Token t, int categoria) {
        ValueTabelaSimbolos v = new ValueTabelaSimbolos(t, categoria, TIPO_NULL,0);
        KeyTabelaSimbolos k = v.getKey();
        if (procedimento != null) {
            if (procedimento.containsKey(k)) {
                return procedimento.get(k).tipo;
            }
        }
        if (!tabelaSimbolosGlobal.containsKey(k)) {
            erros.add(new SintaxError(t.getLinha(), t.getColunaInicio(), t.getOffset(), getMsgErroBusca(categoria)));
            return TIPO_NULL;
        } else {
            return tabelaSimbolosGlobal.get(k).tipo;
        }
    }

    public void removeVarLocaisProcedimento() {
        if (procedimento != null) {
            Collection<ValueTabelaSimbolos> values = procedimento.values();
            Iterator<ValueTabelaSimbolos> iterator = values.iterator();
            while (iterator.hasNext()) {
                ValueTabelaSimbolos value = iterator.next();
                if (value.categoria == CATEGORIA_VAR) {
                    iterator.remove();
                }
            }
        }
        procedimento = null;
    }

    public void parametrosChamadaProcedimento(ArrayList<Integer> tipos,Token idProcedimento){
        KeyTabelaSimbolos k = new KeyTabelaSimbolos(idProcedimento.getLexema(), CATEGORIA_PROC);
        ValueTabelaSimbolos v = tabelaSimbolosGlobal.get(k);
        if(v==null)return;
        if(v.tipo == TIPO_READ || v.tipo == TIPO_WRITE){
            for(int i=1;i<tipos.size();i++){
                if(!tipos.get(i).equals(tipos.get(i-1))){
                    erros.add(new SintaxError(idProcedimento.getLinha(), idProcedimento.getColunaInicio(), idProcedimento.getOffset(), "Read/Write com parametros de tipo diferentes"));
                    return;
                }
            }
            return;
        }
        LinkedHashMap<KeyTabelaSimbolos,ValueTabelaSimbolos> procedimento = tabelasProcedimentos.get(v.tipo);
        Iterator iterator = procedimento.values().iterator();
        int cont=0;
        while(iterator.hasNext() && cont < tipos.size()){
            ValueTabelaSimbolos entry = (ValueTabelaSimbolos)iterator.next();
            if(entry.categoria!=CATEGORIA_PARA)continue;
            if(entry.tipo != tipos.get(cont)){
                erros.add(new SintaxError(idProcedimento.getLinha(), idProcedimento.getColunaInicio(), idProcedimento.getOffset(), "O tipo do parametro formal e real diferem"));
            }
            cont++;
        }
        if(cont != tipos.size()){
            erros.add(new SintaxError(idProcedimento.getLinha(), idProcedimento.getColunaInicio(), idProcedimento.getOffset(), "Quantidade de parametros formal e real diferem"));
        }
    }
    
    private String getMsgErroBusca(int categoria) {
        switch (categoria) {
            case CATEGORIA_PROC:
                return "Procedimento não declarado";
            case CATEGORIA_PARA:
            case CATEGORIA_VAR:
                return "Variavel não declarada";
        }
        return "";
    }

    private String getMsgErroAdd(int categoria) {
        switch (categoria) {
            case CATEGORIA_PARA:
                return "Parâmetro já declarado.";
            case CATEGORIA_PROC:
                return "Procedimento já declarado.";
            case CATEGORIA_VAR:
                return "Variavel já decladara.";
        }
        return "";

    }

    public Integer enderecoSimbolo(Token t) {
        KeyTabelaSimbolos k = new KeyTabelaSimbolos(t.getLexema(), CATEGORIA_VAR);
        ValueTabelaSimbolos v=null;
        if (procedimento != null) {
            v = procedimento.get(k);
        }
        if(v==null){
            v = tabelaSimbolosGlobal.get(k);
        }
        if(v!=null){
            return v.endereco;
        }else return VARIAVEL_NAO_ENCONTRADA;
    }

    public int getNumVariaveis() {
        return  enderecoAtual;
    }

    private class ValueTabelaSimbolos {

        public String lexema;
        public int categoria;
        public int tipo;
        public int valor;
        public boolean utilizada;
        public int endereco;

        public ValueTabelaSimbolos(Token t, int categoria, int tipo,int endereco) {
            lexema = t.getLexema();
            this.categoria = categoria;
            this.tipo = tipo;
            this.utilizada = false;
            this.endereco = endereco;
        }

        public ValueTabelaSimbolos(String lexema, int categoria, int tipo) {
            this.lexema = lexema;
            this.categoria = categoria;
            this.tipo = tipo;
            this.utilizada = false;
        }

        public KeyTabelaSimbolos getKey() {
            return new KeyTabelaSimbolos(lexema, categoria);
        }
    }

    private class KeyTabelaSimbolos {

        public String lexema;
        public int categoria;

        public KeyTabelaSimbolos(String lexema, int categoria) {
            this.lexema = lexema;
            this.categoria = categoria;
            if (this.categoria == CATEGORIA_PARA) {
                this.categoria = CATEGORIA_VAR;
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final KeyTabelaSimbolos other = (KeyTabelaSimbolos) obj;
            if (this.categoria != other.categoria) {
                return false;
            }
            if (!Objects.equals(this.lexema, other.lexema)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            Object a;
            int hash = 3;
            hash = 71 * hash + this.lexema.hashCode();
            hash = 71 * hash + (this.categoria);
            return hash;
        }
    }
}
