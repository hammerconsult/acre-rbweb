package br.com.webpublico.repositorios.jdbc;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Daniel Franco
 * @since 10/12/2015 07:57
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repository
@Lazy
public @interface RepositorioJDBC {
    boolean lazy() default true;
}
