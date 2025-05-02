/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author reidocrime
 */
public class OuvinteDeEntidade {

    private static final Logger logger = LoggerFactory.getLogger(OuvinteDeEntidade.class);

    @PrePersist
    public void antesDoPersist(Object obj) {
        upperCaseString(obj);
    }

    @PreUpdate
    public void antesDoMerge(Object obj) {
        upperCaseString(obj);
    }

    private void upperCaseString(Object obj) {
        List<Field> campos = camposString(obj);

        for (Field f : campos) {
            f.setAccessible(true);
            ////System.out.println("O nome do campo e " + f.getName() + " seu modificador é " + f.getModifiers());
            try {
                if ((f.get(obj) != null) && !f.isAnnotationPresent(Transient.class)) {
                    ////System.out.println("antes do upper " + f.get(obj).toString());

                    f.set(obj, f.get(obj).toString().toUpperCase());
                    ////System.out.println("Depois do upper " + f.get(obj).toString().toUpperCase());
                    f.setAccessible(false);
                }
            } catch (IllegalArgumentException ex) {
                logger.error("Erro: ", ex);
            } catch (IllegalAccessException ex) {
                logger.error("Erro: ", ex);
            }
        }
    }

    private List<Field> camposString(Object obj) {
        Field[] campos = obj.getClass().getDeclaredFields();
        List<Field> fields = new ArrayList<Field>();

        if (obj.getClass().getSuperclass() != null) {
            Field[] camposSuper = obj.getClass().getSuperclass().getDeclaredFields();
            for (Field f : camposSuper) {
                if (f.getType().equals(String.class)) {
                    fields.add(f);
                }
            }
        }

        for (Field f : campos) {
            if (f.getType().equals(String.class)) {
                fields.add(f);
            }
        }
        return fields;
    }
}
