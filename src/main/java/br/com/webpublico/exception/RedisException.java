/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.exception;


public class RedisException extends RuntimeException {

    public RedisException(String message) {
        super(message);
    }

}
