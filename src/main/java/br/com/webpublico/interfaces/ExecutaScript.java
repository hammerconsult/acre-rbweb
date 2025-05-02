/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.interfaces;

import br.com.webpublico.script.Resultado;

/**
 * @author Gécen Dacome De Marchi
 */
public interface ExecutaScript {

    public <T> Resultado<T> invocar(String nomeFuncao, Class<T> clazz, Object... args);
}
