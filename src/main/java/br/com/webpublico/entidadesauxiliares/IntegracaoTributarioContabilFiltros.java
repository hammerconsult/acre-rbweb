package br.com.webpublico.entidadesauxiliares;


import br.com.webpublico.entidades.ContaReceita;
import br.com.webpublico.entidades.LoteBaixa;
import br.com.webpublico.entidades.UnidadeOrganizacional;

import java.util.Date;
import java.util.List;

public class IntegracaoTributarioContabilFiltros {

    private UnidadeOrganizacional unidadeOrganizacional;
    private Date dataInicial;
    private Date dataFinal;
    private ContaReceita contaReceita;
    private List<LoteBaixa> lotesBaixa;

    public IntegracaoTributarioContabilFiltros(UnidadeOrganizacional unidadeOrganizacional, Date dataInicial, Date dataFinal, ContaReceita contaReceita, List<LoteBaixa> lotesBaixa) {
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        this.contaReceita = contaReceita;
        this.lotesBaixa = lotesBaixa;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
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

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public List<LoteBaixa> getLotesBaixa() {
        return lotesBaixa;
    }

    public void setLotesBaixa(List<LoteBaixa> lotesBaixa) {
        this.lotesBaixa = lotesBaixa;
    }
}
