/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

/**
 *
 * @author YiazmaT
 */
public class SintaxError {
    private int linha;
    private int coluna;
    private String erro;

    public SintaxError(int linha, int coluna, String erro) {
        this.linha = linha;
        this.coluna = coluna;
        this.erro = erro;
    }

    public int getLinha() {
        return linha;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

    public int getColuna() {
        return coluna;
    }

    public void setColuna(int coluna) {
        this.coluna = coluna;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }
    
    
}
