package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoLancamentoIsencaoIPTU;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Wellington Abdo on 24/08/2016.
 */
public class RelatorioIsencaoCadastroImobiliario implements Serializable {

    private String grupo;
    private Long numero;
    private String usuario;
    private String inscricaoCadastral;
    private Integer exercicio;
    private String responsavel;
    private String cpfCnpj;
    private String categoria;
    private String quadra;
    private String lote;
    private String setor;
    private TipoLancamentoIsencaoIPTU tipoLancamentoIsencaoIPTU;
    private BigDecimal valorIsentoTaxa;
    private BigDecimal valorIsentoImposto;
    private Date dataCalculo;
    private BigDecimal valor;
    private List<RelatorioIsencaoCadastroImobiliario> iptus;

    public RelatorioIsencaoCadastroImobiliario() {
        iptus = Lists.newArrayList();
    }

    public RelatorioIsencaoCadastroImobiliario(Object[] registro) {
        this.grupo = (String) registro[0];
        this.numero = ((BigDecimal) registro[1]).longValue();
        this.inscricaoCadastral = (String) registro[2];
        this.exercicio = ((BigDecimal) registro[3]).intValue();
        this.categoria = (String) registro[4];
        this.responsavel = (String) registro[5];
        this.cpfCnpj = (String) registro[6];
        this.tipoLancamentoIsencaoIPTU = TipoLancamentoIsencaoIPTU.valueOf((String) registro[7]);
        this.lote = (String) registro[8];
        this.quadra = (String) registro[9];
        this.setor = (String) registro[10];
        this.valorIsentoTaxa = (BigDecimal) registro[11];
        this.valorIsentoImposto = (BigDecimal) registro[12];
        this.usuario = (String) registro[13];
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public TipoLancamentoIsencaoIPTU getTipoLancamentoIsencaoIPTU() {
        return tipoLancamentoIsencaoIPTU;
    }

    public void setTipoLancamentoIsencaoIPTU(TipoLancamentoIsencaoIPTU tipoLancamentoIsencaoIPTU) {
        this.tipoLancamentoIsencaoIPTU = tipoLancamentoIsencaoIPTU;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getQuadra() {
        return quadra;
    }

    public void setQuadra(String quadra) {
        this.quadra = quadra;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public BigDecimal getValorIsentoTaxa() {
        return valorIsentoTaxa;
    }

    public void setValorIsentoTaxa(BigDecimal valorIsentoTaxa) {
        this.valorIsentoTaxa = valorIsentoTaxa;
    }

    public BigDecimal getValorIsentoImposto() {
        return valorIsentoImposto;
    }

    public void setValorIsentoImposto(BigDecimal valorIsentoImposto) {
        this.valorIsentoImposto = valorIsentoImposto;
    }

    public Date getDataCalculo() {
        return dataCalculo;
    }

    public void setDataCalculo(Date dataCalculo) {
        this.dataCalculo = dataCalculo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<RelatorioIsencaoCadastroImobiliario> getIptus() {
        return iptus;
    }

    public void setIptus(List<RelatorioIsencaoCadastroImobiliario> iptus) {
        this.iptus = iptus;
    }
}
