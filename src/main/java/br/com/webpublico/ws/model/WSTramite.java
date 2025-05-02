package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.Tramite;

import java.util.Date;

public class WSTramite {

    private Integer indice;
    private String destino;
    private String origem;
    private Date aceite;
    private Date conclusao;
    private String situacao;
    private String observacao;
    private String motivo;

    public Integer getIndice() {
        return indice;
    }

    public void setIndice(Integer indice) {
        this.indice = indice;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public Date getAceite() {
        return aceite;
    }

    public void setAceite(Date aceite) {
        this.aceite = aceite;
    }

    public Date getConclusao() {
        return conclusao;
    }

    public void setConclusao(Date conclusao) {
        this.conclusao = conclusao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public WSTramite() {
    }

    public WSTramite(Tramite t) {
        if (t != null) {
            this.aceite = t.getDataAceite();
            this.conclusao = t.getDataConclusao();
            if (t.getSituacaoTramite() != null) {
                this.situacao = t.getSituacaoTramite().getNome();
            }
            this.destino = t.getDestinoTramite();
            if (t.getOrigem() != null) {
                this.origem = t.getOrigem().getDescricao();
            }
            this.indice = t.getIndice();
            if (t.getObservacoes() != null && !t.getObservacoes().isEmpty()) {
                this.observacao = t.getObservacoes();
            }
            if (t.getMotivo() != null && !t.getMotivo().isEmpty()) {
                this.motivo = t.getMotivo();
            }
        }
    }
}
