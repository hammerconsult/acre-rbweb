package br.com.webpublico.entidadesauxiliares.rh.cbo;

import br.com.webpublico.enums.rh.cbo.TipoCBO;

public class ConteudoCBO {
    private Long codigo;
    private String descricao;
    private TipoCBO tipoCBO;

    public ConteudoCBO(Long codigo, String descricao, TipoCBO tipoCBO) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.tipoCBO = tipoCBO;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoCBO getTipoCBO() {
        return tipoCBO;
    }

    public void setTipoCBO(TipoCBO tipoCBO) {
        this.tipoCBO = tipoCBO;
    }
}
