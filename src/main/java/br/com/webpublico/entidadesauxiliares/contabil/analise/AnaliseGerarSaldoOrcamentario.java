package br.com.webpublico.entidadesauxiliares.contabil.analise;

import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.contabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.OperacaoORC;
import br.com.webpublico.enums.TipoOperacaoORC;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class AnaliseGerarSaldoOrcamentario {

    private UnidadeOrganizacional unidade;
    private OperacaoORC operacaoOrc;
    private TipoOperacaoORC tipoCredito;
    private BigDecimal valor;
    private Date data;
    private List<AnaliseGerarSaldoOrcamentarioFonte> fontes;


    public AnaliseGerarSaldoOrcamentario() {
        fontes  = Lists.newArrayList();
    }

    public OperacaoORC getOperacaoOrc() {
        return operacaoOrc;
    }

    public void setOperacaoOrc(OperacaoORC operacaoOrc) {
        this.operacaoOrc = operacaoOrc;
    }

    public TipoOperacaoORC getTipoCredito() {
        return tipoCredito;
    }

    public void setTipoCredito(TipoOperacaoORC tipoCredito) {
        this.tipoCredito = tipoCredito;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public UnidadeOrganizacional getUnidade() {
        return unidade;
    }

    public void setUnidade(UnidadeOrganizacional unidade) {
        this.unidade = unidade;
    }

    public List<AnaliseGerarSaldoOrcamentarioFonte> getFontes() {
        return fontes;
    }

    public void setFontes(List<AnaliseGerarSaldoOrcamentarioFonte> fontes) {
        this.fontes = fontes;
    }
}
