/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jflex;

import java.io.File;
import java.nio.file.Paths;

/**
 *
 * @author Matheus Prachedes Batista
 */
public class Generator {
     
    public static void main(String[] args) {

        String rootPath = Paths.get("").toAbsolutePath(). toString();
        String subPath = "/src/jflex/";

        String file = rootPath + subPath + "jflexlalg.txt";

        File sourceCode = new File(file);

        jflex.Main.generate(sourceCode);

    }
}
