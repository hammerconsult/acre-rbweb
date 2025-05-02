package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.AprovacaoFerias;
import br.com.webpublico.entidades.SugestaoFerias;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by peixe on 12/08/2015.
 */
public class WSProgramacaoFerias implements Serializable {

    private Date inicioVigencia;
    private Date finalVigencia;
    private Boolean aprovado;
    private Date dataAprovacao;

    public WSProgramacaoFerias(Date inicioVigencia, Date finalVigencia, Boolean aprovado, Date dataAprovacao) {
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.aprovado = aprovado;
        this.dataAprovacao = dataAprovacao;
    }

    public WSProgramacaoFerias() {

    }

    public static List<WSProgramacaoFerias> convertSugestaoFeriasToWsSugestaoFerias(List<SugestaoFerias> sugestaoFerias) {
        List<WSProgramacaoFerias> wsSugestaoFerias = new ArrayList<>();
        for (SugestaoFerias sugestao : sugestaoFerias) {
            AprovacaoFerias ap = getAprovacaoFerias(sugestao.getListAprovacaoFerias());
            WSProgramacaoFerias ws = new WSProgramacaoFerias(sugestao.getDataInicio(), sugestao.getDataFim(), ap != null ? ap.getAprovado() : false, ap != null ? ap.getDataAprovacao() : null);
            wsSugestaoFerias.add(ws);
        }
        return wsSugestaoFerias;
    }

    public static AprovacaoFerias getAprovacaoFerias(List<AprovacaoFerias> aprovacoes) {
        if (aprovacoes != null && !aprovacoes.isEmpty()) {
            for (AprovacaoFerias aprovacao : aprovacoes) {
                if (aprovacao.getAprovado()) return aprovacao;
            }
            return aprovacoes.get(0);
        }
        return null;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Boolean getAprovado() {
        return aprovado;
    }

    public void setAprovado(Boolean aprovado) {
        this.aprovado = aprovado;
    }

    public Date getDataAprovacao() {
        return dataAprovacao;
    }

    public void setDataAprovacao(Date dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }
}
