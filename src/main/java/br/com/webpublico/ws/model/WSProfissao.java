package br.com.webpublico.ws.model;

import java.io.Serializable;

/**
 * Created by Buzatto on 06/03/2017.
 */
public class WSProfissao implements Serializable {

    private Long id;
    private String descricao;

    public WSProfissao() {
    }

    public WSProfissao(Long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "WSProfissao{" +
            "id=" + id +
            ", descricao='" + descricao + '\'' +
            '}';
    }
}
