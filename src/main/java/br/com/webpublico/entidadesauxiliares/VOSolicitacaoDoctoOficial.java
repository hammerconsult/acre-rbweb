package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.util.Date;

public class VOSolicitacaoDoctoOficial implements Serializable {
    private Long id;
    private Long codigoSolicitacao;
    private Date dataSolicitacao;
    private Long codigoTipoDocto;
    private String descricaoTipoDocto;
    private String agrupador;

    public VOSolicitacaoDoctoOficial() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigoSolicitacao() {
        return codigoSolicitacao;
    }

    public void setCodigoSolicitacao(Long codigoSolicitacao) {
        this.codigoSolicitacao = codigoSolicitacao;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public Long getCodigoTipoDocto() {
        return codigoTipoDocto;
    }

    public void setCodigoTipoDocto(Long codigoTipoDocto) {
        this.codigoTipoDocto = codigoTipoDocto;
    }

    public String getDescricaoTipoDocto() {
        return descricaoTipoDocto;
    }

    public void setDescricaoTipoDocto(String descricaoTipoDocto) {
        this.descricaoTipoDocto = descricaoTipoDocto;
    }

    public String getAgrupador() {
        return agrupador;
    }

    public void setAgrupador(String agrupador) {
        this.agrupador = agrupador;
    }
}
