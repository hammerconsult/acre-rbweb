package br.com.webpublico.entidadesauxiliares.rh.relatorio;

import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.ws.model.WSFichaFinanceira;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

/**
 * Created by tharlyson on 17/01/20.
 */
public class AssistenteContraCheque {

    private Long idFicha;
    private Long idVinculo;
    private String condicoes;
    private OperacaoFormula operacaoFormula;
    private Date dataOperacao;
    private List<WSFichaFinanceira> wsFichaFinanceira;

    public AssistenteContraCheque() {
        wsFichaFinanceira = Lists.newArrayList();
    }

    public Long getIdFicha() {
        return idFicha;
    }

    public void setIdFicha(Long idFicha) {
        this.idFicha = idFicha;
    }

    public Long getIdVinculo() {
        return idVinculo;
    }

    public void setIdVinculo(Long idVinculo) {
        this.idVinculo = idVinculo;
    }

    public String getCondicoes() {
        return condicoes;
    }

    public void setCondicoes(String condicoes) {
        this.condicoes = condicoes;
    }

    public OperacaoFormula getOperacaoFormula() {
        return operacaoFormula;
    }

    public void setOperacaoFormula(OperacaoFormula operacaoFormula) {
        this.operacaoFormula = operacaoFormula;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public List<WSFichaFinanceira> getWsFichaFinanceira() {
        return wsFichaFinanceira;
    }

    public void setWsFichaFinanceira(List<WSFichaFinanceira> wsFichaFinanceira) {
        this.wsFichaFinanceira = wsFichaFinanceira;
    }
}
