package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Servico;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by William on 23/05/2017.
 */
public class FiltroRelatorioInconsistenciaISS {
    private Date dataInicial;
    private Date dataFinal;
    private BigDecimal percentualMenos;
    private BigDecimal percentualMais;
    private List<RelatorioInconsistenciaISS> recuperados;
    private List<Divida> dividas;
    private List<Servico> servicos;
    private Map<String, List<BigDecimal>> totaisCadastro;
    private List<RelatorioInconsistenciaISS> inconsistencias;
    private List<RelatorioInconsistenciaISSTotal> totais;

    public FiltroRelatorioInconsistenciaISS() {
        recuperados = Lists.newArrayList();
        dividas = Lists.newArrayList();
        servicos = Lists.newArrayList();
        totais = Lists.newArrayList();
        limparListasAssincronas();
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

    public BigDecimal getPercentualMenos() {
        return percentualMenos;
    }

    public void setPercentualMenos(BigDecimal percentualMenos) {
        this.percentualMenos = percentualMenos;
    }

    public BigDecimal getPercentualMais() {
        return percentualMais;
    }

    public void setPercentualMais(BigDecimal percentualMais) {
        this.percentualMais = percentualMais;
    }

    public List<RelatorioInconsistenciaISS> getRecuperados() {
        return recuperados;
    }

    public void setRecuperados(List<RelatorioInconsistenciaISS> recuperados) {
        this.recuperados = recuperados;
    }

    public List<Divida> getDividas() {
        return dividas;
    }

    public void setDividas(List<Divida> dividas) {
        this.dividas = dividas;
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    public void setServicos(List<Servico> servicos) {
        this.servicos = servicos;
    }

    public Map<String, List<BigDecimal>> getTotaisCadastro() {
        return totaisCadastro;
    }

    public void setTotaisCadastro(Map<String, List<BigDecimal>> totaisCadastro) {
        this.totaisCadastro = totaisCadastro;
    }

    public List<RelatorioInconsistenciaISS> getInconsistencias() {
        return inconsistencias;
    }

    public void setInconsistencias(List<RelatorioInconsistenciaISS> inconsistencias) {
        this.inconsistencias = inconsistencias;
    }

    public List<RelatorioInconsistenciaISSTotal> getTotais() {
        return totais;
    }

    public void setTotais(List<RelatorioInconsistenciaISSTotal> totais) {
        this.totais = totais;
    }

    public void limparListasAssincronas() {
        totaisCadastro = Maps.newHashMap();
        inconsistencias = Lists.newArrayList();
        totais = Lists.newArrayList();
    }
}
