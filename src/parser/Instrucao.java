/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package parser;

/**
 *
 * @author Matheus Prachedes Batista
 */
public class Instrucao {

        public Integer rotulo;
        public String instrucao;
        public Integer operando;

        public Instrucao(Integer rotulo, String instrucao, Integer operando) {
            this.rotulo = rotulo;
            this.instrucao = instrucao;
            this.operando = operando;
        }

        @Override
        public String toString() {
            String print = "";
            if (rotulo != null) {
                print += rotulo;
            }
            if (instrucao != null) {
                print += " " + instrucao;
            }
            if (operando != null) {
                print += " " + operando;
            }
            return print;
        }

    }

