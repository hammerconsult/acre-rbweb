package br.com.webpublico.entidadesauxiliares;


import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ReceitaAbrasf implements Serializable {
    private Date dataRelatorio;
    private List<ArrecadacaoPropriaAbrasf> arrecadacoesProprias;
    private List<RepasseFinanceiroAbrasf> repassesFinanceiros;
    private List<NotasFiscaisAbrasf> notasFiscais;
    private List<AvaliacaoEconomicaAbrasf> avaliacoes;

    public ReceitaAbrasf(Date dataRelatorio) {
        this.dataRelatorio = dataRelatorio;
        this.arrecadacoesProprias = Lists.newLinkedList();
        this.repassesFinanceiros = Lists.newLinkedList();
        this.notasFiscais = Lists.newLinkedList();
        this.avaliacoes = Lists.newLinkedList();
    }

    public Date getDataRelatorio() {
        return dataRelatorio;
    }

    public void setDataRelatorio(Date dataRelatorio) {
        this.dataRelatorio = dataRelatorio;
    }

    public List<ArrecadacaoPropriaAbrasf> getArrecadacoesProprias() {
        return arrecadacoesProprias;
    }

    public void setArrecadacoesProprias(List<ArrecadacaoPropriaAbrasf> arrecadacoesProprias) {
        this.arrecadacoesProprias = arrecadacoesProprias;
    }

    public List<RepasseFinanceiroAbrasf> getRepassesFinanceiros() {
        return repassesFinanceiros;
    }

    public void setRepassesFinanceiros(List<RepasseFinanceiroAbrasf> repassesFinanceiros) {
        this.repassesFinanceiros = repassesFinanceiros;
    }

    public List<NotasFiscaisAbrasf> getNotasFiscais() {
        return notasFiscais;
    }

    public void setNotasFiscais(List<NotasFiscaisAbrasf> notasFiscais) {
        this.notasFiscais = notasFiscais;
    }

    public List<AvaliacaoEconomicaAbrasf> getAvaliacoes() {
        return avaliacoes;
    }

    public void setAvaliacoes(List<AvaliacaoEconomicaAbrasf> avaliacoes) {
        this.avaliacoes = avaliacoes;
    }
}
