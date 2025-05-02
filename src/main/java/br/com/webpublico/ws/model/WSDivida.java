package br.com.webpublico.ws.model;


import br.com.webpublico.entidades.Alvara;
import br.com.webpublico.entidades.Divida;
import br.com.webpublico.enums.TipoCadastroTributario;

import java.io.Serializable;
import java.util.Date;

public class WSDivida implements Serializable {

    private Long id;
    private String descricao;
    private TipoCadastroTributario tipoCadastro;

    public WSDivida() {
    }

    public WSDivida(Divida divida) {
        this.id = divida.getId();
        this.descricao = divida.getDescricao();
        this.tipoCadastro = divida.getTipoCadastro();
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoCadastroTributario getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastroTributario tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
