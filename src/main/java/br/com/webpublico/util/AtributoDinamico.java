/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Munif
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AtributoDinamico {

    Class entidade();

}
