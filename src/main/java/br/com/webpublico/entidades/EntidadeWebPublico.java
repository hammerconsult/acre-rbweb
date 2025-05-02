package br.com.webpublico.entidades;

/**
 * @author Daniel
 * @since 26/08/2015 10:29
 */
public interface EntidadeWebPublico {

    Long getId();

    default void setId(Long id) {
    }

    Long getCriadoEm();
}
