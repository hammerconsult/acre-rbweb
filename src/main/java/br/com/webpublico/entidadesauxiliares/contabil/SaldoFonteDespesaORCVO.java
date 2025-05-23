package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.OperacaoORC;
import br.com.webpublico.enums.TipoOperacaoORC;

import java.math.BigDecimal;
import java.util.Date;

public class SaldoFonteDespesaORCVO {
    private FonteDespesaORC fonte;
    private OperacaoORC operacaoOrc;
    private TipoOperacaoORC tipoCredito;
    private BigDecimal valor;
    private Date data;
    private UnidadeOrganizacional unidade;
    private String idOrigem;
    private String classeOrigem;
    private String numeroMovimento;
    private String historico;

    public SaldoFonteDespesaORCVO(FonteDespesaORC fonte, OperacaoORC operacaoOrc, TipoOperacaoORC tipoCredito, BigDecimal valor, Date data, UnidadeOrganizacional unidade, String idOrigem, String classeOrigem, String numeroMovimento, String historico) {
        this.fonte = fonte;
        this.operacaoOrc = operacaoOrc;
        this.tipoCredito = tipoCredito;
        this.valor = valor;
        this.data = data;
        this.unidade = unidade;
        this.idOrigem = idOrigem;
        this.classeOrigem = classeOrigem;
        this.numeroMovimento = numeroMovimento;
        this.historico = historico != null && historico.length() > 3000 ? historico.substring(0, 3000) : historico;
    }

    public FonteDespesaORC getFonte() {
        return fonte;
    }

    public void setFonte(FonteDespesaORC fonte) {
        this.fonte = fonte;
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

    public String getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(String idOrigem) {
        this.idOrigem = idOrigem;
    }

    public String getClasseOrigem() {
        return classeOrigem;
    }

    public void setClasseOrigem(String classeOrigem) {
        this.classeOrigem = classeOrigem;
    }

    public String getNumeroMovimento() {
        return numeroMovimento;
    }

    public void setNumeroMovimento(String numeroMovimento) {
        this.numeroMovimento = numeroMovimento;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }
}
