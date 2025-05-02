package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SolicitacaoCotaFinanceira;
import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.enums.StatusSolicitacaoCotaFinanceira;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

/**
 * Created by renato on 14/02/2020.
 */
public class FiltroAprovacaoSolicitacaoFinanceira {

    private Date dataInicial;
    private Date dataFinal;
    private String numero;
    private Exercicio exercicio;
    private List<HierarquiaOrganizacional> unidades;
    private Boolean pendente;
    private UnidadeGestora unidadeGestora;

    private List<SolicitacaoCotaFinanceira> solicitacoes;


    public FiltroAprovacaoSolicitacaoFinanceira() {
        this.unidades = Lists.newArrayList();
        this.solicitacoes = Lists.newArrayList();
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public List<HierarquiaOrganizacional> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<HierarquiaOrganizacional> unidades) {
        this.unidades = unidades;
    }

    public Boolean getPendente() {
        if (pendente == null) {
            pendente = Boolean.FALSE;
        }
        return pendente;
    }

    public void setPendente(Boolean pendente) {
        this.pendente = pendente;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public List<SolicitacaoCotaFinanceira> getSolicitacoes() {
        return solicitacoes;
    }

    public void setSolicitacoes(List<SolicitacaoCotaFinanceira> solicitacoes) {
        this.solicitacoes = solicitacoes;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<Long> getIdUnidades() {
        List<Long> retorno = Lists.newArrayList();
        for (HierarquiaOrganizacional unidade : unidades) {
            retorno.add(unidade.getId());
        }
        return retorno;
    }
}
