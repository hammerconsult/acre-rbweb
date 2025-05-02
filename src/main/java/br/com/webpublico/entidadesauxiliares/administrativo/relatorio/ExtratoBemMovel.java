package br.com.webpublico.entidadesauxiliares.administrativo.relatorio;

import br.com.webpublico.util.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Desenvolvimento on 16/06/2017.
 */
public class ExtratoBemMovel implements Serializable {
    protected static final Logger logger = LoggerFactory.getLogger(ExtratoBemMovel.class);

    private String codigoOrgao;
    private String descricaoOrgao;
    private String codigoUnidade;
    private String descricaoUnidade;
    private String codigoAdministrativa;
    private String descricaoAdministrativa;
    private String registroPatrimonial;
    private String descricaoBem;
    private BigDecimal bemId;
    private String codigoGrupo;
    private String descricaoGrupo;
    private String localizacao;
    private String responsavel;
    private BigDecimal orcId;
    private BigDecimal admId;
    private BigDecimal saldoAnteriorOriginal;
    private BigDecimal saldoAnterioDepreciacao;
    private BigDecimal saldoAnterioExaustao;
    private BigDecimal saldoAnterioAmortizacao;
    private BigDecimal saldoAnterioReducao;
    private String dataOperacaoSaldoAnterior;
    private String dataLancamentoSaldoAnterior;
    private String dataOperacao;
    private String dataLancamento;
    private String tipoEventoBem;
    private BigDecimal valorLancamentoDebito;
    private BigDecimal valorLancamentoCredito;
    private String tipoSaldo;
    private Date dataEvento;
    private Integer codigoQuebraPagina;

    public static List<ExtratoBemMovel> preencherDados(List<Object[]> objetos) {
        List<ExtratoBemMovel> retorno = new ArrayList<>();
        for (Object[] obj : objetos) {
            ExtratoBemMovel extrato = new ExtratoBemMovel();
            extrato.setCodigoOrgao((String) obj[0]);
            extrato.setDescricaoOrgao((String) obj[1]);
            extrato.setCodigoUnidade((String) obj[2]);
            extrato.setDescricaoUnidade((String) obj[3]);
            extrato.setCodigoAdministrativa((String) obj[4]);
            extrato.setDescricaoAdministrativa((String) obj[5]);
            extrato.setRegistroPatrimonial((String) obj[6]);
            extrato.setDescricaoBem((String) obj[7]);
            extrato.setBemId((BigDecimal) obj[8]);
            extrato.setCodigoGrupo((String) obj[9]);
            extrato.setDescricaoGrupo((String) obj[10]);
            extrato.setLocalizacao((String) obj[11]);
            extrato.setResponsavel((String) obj[12]);
            extrato.setOrcId((BigDecimal) obj[13]);
            extrato.setAdmId((BigDecimal) obj[14]);

            extrato.setDataOperacao((String) obj[15]);
            extrato.setDataLancamento((String) obj[16]);
            extrato.setTipoEventoBem(obj[17] != null ? (String) obj[17] : "");
            extrato.setValorLancamentoDebito(obj[18] != null ? (BigDecimal) obj[18] : BigDecimal.ZERO);
            extrato.setValorLancamentoCredito(obj[19] != null ? (BigDecimal) obj[19] : BigDecimal.ZERO);
            extrato.setTipoSaldo(obj[20] != null ? (String) obj[20] : "");
            extrato.setDataEvento((Date) obj[21]);
            retorno.add(extrato);
        }
        return retorno;
    }

    public static void preencherSaldoAnterior(ExtratoBemMovel extratoBemMovel, Object[] obj) {
        extratoBemMovel.setDataOperacaoSaldoAnterior((String) obj[0]);
        extratoBemMovel.setDataLancamentoSaldoAnterior((String) obj[1]);
        extratoBemMovel.setSaldoAnteriorOriginal((BigDecimal) obj[2]);
        extratoBemMovel.setSaldoAnterioDepreciacao((BigDecimal) obj[3]);
        extratoBemMovel.setSaldoAnterioExaustao((BigDecimal) obj[4]);
        extratoBemMovel.setSaldoAnterioAmortizacao((BigDecimal) obj[5]);
        extratoBemMovel.setSaldoAnterioReducao((BigDecimal) obj[6]);
    }

    public static void preencherSaldoInicial(ExtratoBemMovel extratoBemMovel, Date dataSaldo) {
        extratoBemMovel.setDataOperacaoSaldoAnterior(DataUtil.getDataFormatada(dataSaldo));
        extratoBemMovel.setDataLancamentoSaldoAnterior(DataUtil.getDataFormatada(dataSaldo));
        extratoBemMovel.setSaldoAnteriorOriginal(BigDecimal.ZERO);
        extratoBemMovel.setSaldoAnterioDepreciacao(BigDecimal.ZERO);
        extratoBemMovel.setSaldoAnterioExaustao(BigDecimal.ZERO);
        extratoBemMovel.setSaldoAnterioAmortizacao(BigDecimal.ZERO);
        extratoBemMovel.setSaldoAnterioReducao(BigDecimal.ZERO);
    }

    public Date getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(Date dataEvento) {
        this.dataEvento = dataEvento;
    }

    public String getCodigoOrgao() {
        return codigoOrgao;
    }

    public void setCodigoOrgao(String codigoOrgao) {
        this.codigoOrgao = codigoOrgao;
    }

    public String getDescricaoOrgao() {
        return descricaoOrgao;
    }

    public void setDescricaoOrgao(String descricaoOrgao) {
        this.descricaoOrgao = descricaoOrgao;
    }

    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
    }

    public String getDescricaoUnidade() {
        return descricaoUnidade;
    }

    public void setDescricaoUnidade(String descricaoUnidade) {
        this.descricaoUnidade = descricaoUnidade;
    }

    public String getCodigoAdministrativa() {
        return codigoAdministrativa;
    }

    public void setCodigoAdministrativa(String codigoAdministrativa) {
        this.codigoAdministrativa = codigoAdministrativa;
    }

    public String getDescricaoAdministrativa() {
        return descricaoAdministrativa;
    }

    public void setDescricaoAdministrativa(String descricaoAdministrativa) {
        this.descricaoAdministrativa = descricaoAdministrativa;
    }

    public String getRegistroPatrimonial() {
        return registroPatrimonial;
    }

    public void setRegistroPatrimonial(String registroPatrimonial) {
        this.registroPatrimonial = registroPatrimonial;
    }

    public String getDescricaoBem() {
        return descricaoBem;
    }

    public void setDescricaoBem(String descricaoBem) {
        this.descricaoBem = descricaoBem;
    }

    public BigDecimal getBemId() {
        return bemId;
    }

    public void setBemId(BigDecimal bemId) {
        this.bemId = bemId;
    }

    public String getCodigoGrupo() {
        return codigoGrupo;
    }

    public void setCodigoGrupo(String codigoGrupo) {
        this.codigoGrupo = codigoGrupo;
    }

    public String getDescricaoGrupo() {
        return descricaoGrupo;
    }

    public void setDescricaoGrupo(String descricaoGrupo) {
        this.descricaoGrupo = descricaoGrupo;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public BigDecimal getOrcId() {
        return orcId;
    }

    public void setOrcId(BigDecimal orcId) {
        this.orcId = orcId;
    }

    public BigDecimal getAdmId() {
        return admId;
    }

    public void setAdmId(BigDecimal admId) {
        this.admId = admId;
    }

    public BigDecimal getSaldoAnteriorOriginal() {
        return saldoAnteriorOriginal;
    }

    public void setSaldoAnteriorOriginal(BigDecimal saldoAnteriorOriginal) {
        this.saldoAnteriorOriginal = saldoAnteriorOriginal;
    }

    public BigDecimal getSaldoAnterioDepreciacao() {
        return saldoAnterioDepreciacao;
    }

    public void setSaldoAnterioDepreciacao(BigDecimal saldoAnterioDepreciacao) {
        this.saldoAnterioDepreciacao = saldoAnterioDepreciacao;
    }

    public BigDecimal getSaldoAnterioExaustao() {
        return saldoAnterioExaustao;
    }

    public void setSaldoAnterioExaustao(BigDecimal saldoAnterioExaustao) {
        this.saldoAnterioExaustao = saldoAnterioExaustao;
    }

    public BigDecimal getSaldoAnterioAmortizacao() {
        return saldoAnterioAmortizacao;
    }

    public void setSaldoAnterioAmortizacao(BigDecimal saldoAnterioAmortizacao) {
        this.saldoAnterioAmortizacao = saldoAnterioAmortizacao;
    }

    public BigDecimal getSaldoAnterioReducao() {
        return saldoAnterioReducao;
    }

    public void setSaldoAnterioReducao(BigDecimal saldoAnterioReducao) {
        this.saldoAnterioReducao = saldoAnterioReducao;
    }

    public String getDataOperacaoSaldoAnterior() {
        return dataOperacaoSaldoAnterior;
    }

    public void setDataOperacaoSaldoAnterior(String dataOperacaoSaldoAnterior) {
        this.dataOperacaoSaldoAnterior = dataOperacaoSaldoAnterior;
    }

    public String getDataLancamentoSaldoAnterior() {
        return dataLancamentoSaldoAnterior;
    }

    public void setDataLancamentoSaldoAnterior(String dataLancamentoSaldoAnterior) {
        this.dataLancamentoSaldoAnterior = dataLancamentoSaldoAnterior;
    }

    public String getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(String dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public String getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(String dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getTipoEventoBem() {
        return tipoEventoBem;
    }

    public void setTipoEventoBem(String tipoEventoBem) {
        this.tipoEventoBem = tipoEventoBem;
    }

    public BigDecimal getValorLancamentoDebito() {
        return valorLancamentoDebito;
    }

    public void setValorLancamentoDebito(BigDecimal valorLancamentoDebito) {
        this.valorLancamentoDebito = valorLancamentoDebito;
    }

    public BigDecimal getValorLancamentoCredito() {
        return valorLancamentoCredito;
    }

    public void setValorLancamentoCredito(BigDecimal valorLancamentoCredito) {
        this.valorLancamentoCredito = valorLancamentoCredito;
    }

    public String getTipoSaldo() {
        return tipoSaldo;
    }

    public void setTipoSaldo(String tipoSaldo) {
        this.tipoSaldo = tipoSaldo;
    }

    public Integer getCodigoQuebraPagina() {
        return codigoQuebraPagina;
    }

    public void setCodigoQuebraPagina(Integer codigoQuebraPagina) {
        this.codigoQuebraPagina = codigoQuebraPagina;
    }
}
