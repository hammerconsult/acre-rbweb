package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.TipoAfastamento;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WSTipoAfastamento implements Serializable {

    private Long id;
    private Integer codigo;
    private String descricao;

    public WSTipoAfastamento() {
    }

    public WSTipoAfastamento(TipoAfastamento tipoAfastamento) {
        id = tipoAfastamento.getId();
        codigo = tipoAfastamento.getCodigo();
        descricao = tipoAfastamento.getDescricao();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
