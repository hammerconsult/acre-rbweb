package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Desenvolvimento on 02/05/2017.
 */
public class RelatorioRevisaoIPTU implements Serializable, Comparable<RelatorioRevisaoIPTU> {

    private BigDecimal id;
    private String numeroProtocolo;
    private String anoProtocolo;
    private String cpfCnpj;
    private String proprietario;
    private String inscricaoCadastral;
    private String logradouro;
    private String numero;
    private String bairro;
    private Date dataLancamento;
    private BigDecimal imposto;
    private BigDecimal taxa;
    private BigDecimal valorEfetivo;
    private BigDecimal percentualDescontoImposto;
    private Date vencimento;
    private Boolean atual;

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getProprietario() {
        return proprietario;
    }

    public void setProprietario(String proprietario) {
        this.proprietario = proprietario;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public BigDecimal getImposto() {
        return imposto;
    }

    public void setImposto(BigDecimal imposto) {
        this.imposto = imposto;
    }

    public BigDecimal getTaxa() {
        return taxa;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    public BigDecimal getValorEfetivo() {
        return valorEfetivo;
    }

    public void setValorEfetivo(BigDecimal valorEfetivo) {
        this.valorEfetivo = valorEfetivo;
    }

    public BigDecimal getPercentualDescontoImposto() {
        return percentualDescontoImposto;
    }

    public void setPercentualDescontoImposto(BigDecimal percentualDescontoImposto) {
        this.percentualDescontoImposto = percentualDescontoImposto;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public Boolean getAtual() {
        return atual;
    }

    public void setAtual(Boolean atual) {
        this.atual = atual;
    }

    public BigDecimal getValorTotal(){
        BigDecimal total = imposto != null ? imposto : BigDecimal.ZERO;
        if(percentualDescontoImposto != null){
            total = total.subtract(percentualDescontoImposto);
        }
        if(taxa != null){
            total = total.add(taxa);
        }
        return total;
    }

    public static List<RelatorioRevisaoIPTU> preencherDados(List<Object[]> objs){
        List<RelatorioRevisaoIPTU> retorno = new ArrayList<>();
        if (objs != null && !objs.isEmpty()) {
            for (Object[] obj : objs) {
                RelatorioRevisaoIPTU rev = new RelatorioRevisaoIPTU();
                rev.setNumeroProtocolo((String) obj[0]);
                rev.setAnoProtocolo((String) obj[1]);
                rev.setCpfCnpj(obj[2] != null ? (String) obj[2] : "");
                rev.setProprietario(obj[3] != null ? (String) obj[3] : "");
                rev.setInscricaoCadastral(obj[4] != null ? (String) obj[4] : "");
                rev.setLogradouro(obj[5] != null ? (String) obj[5] : "");
                rev.setNumero(obj[6] != null ? (String) obj[6] : "");
                rev.setBairro(obj[7] != null ? (String) obj[7] : "");
                rev.setDataLancamento(obj[8] != null ? (Date) obj[8] : new Date());
                rev.setImposto(obj[9] != null ? (BigDecimal) obj[9] : BigDecimal.ZERO);
                rev.setTaxa(obj[10] != null ? (BigDecimal) obj[10] : BigDecimal.ZERO);
                rev.setValorEfetivo(obj[11] != null ? (BigDecimal) obj[11] : BigDecimal.ZERO);
                rev.setPercentualDescontoImposto(obj[12] != null ? (BigDecimal) obj[12] : BigDecimal.ZERO);
                rev.setVencimento((Date) obj[13]);
                rev.setId((BigDecimal) obj[14]);
                rev.setAtual(((BigDecimal) obj[15]).compareTo(BigDecimal.ZERO) == 0 ? false : true);
                retorno.add(rev);
            }
        }
        return retorno;
    }

    @Override
    public int compareTo(RelatorioRevisaoIPTU o) {
        int i = this.getInscricaoCadastral().compareTo(o.getInscricaoCadastral());
        if (i == 0) {
            i = this.getDataLancamento().compareTo(o.getDataLancamento());
        }
        return i;
    }
}
