/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.autocomplete;

import java.io.Serializable;

/**
 * @author Arthur
 */
public class AutoCompleteVO implements Serializable {
    private final Long id;
    private final String valorExibicao;

    public AutoCompleteVO(Long id, String valorExibicao) {
        this.id = id;
        this.valorExibicao = valorExibicao;
    }

    public Long getId() {
        return id;
    }

    public String getValorExibicao() {
        return valorExibicao;
    }

    @Override
    public String toString() {
        return valorExibicao;
    }
}
