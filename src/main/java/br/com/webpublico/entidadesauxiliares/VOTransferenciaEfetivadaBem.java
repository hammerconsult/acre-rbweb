package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by HardRock on 16/02/2017.
 */
public class VOTransferenciaEfetivadaBem {

    private Date dataEfetivacao;
    private String codigoEfetivacao;
    private String descricao;
    private String codigoBem;
    private String descricaoBem;
    private String grupoBem;
    private String codigoAdmDestino;
    private String descricaoAdmDestino;
    private String codigoAdmOrigem;
    private String descricaoAdmOrigem;
    private BigDecimal valorBem;
    private BigDecimal valorAjuste;
    private BigDecimal valorAtual;

    public VOTransferenciaEfetivadaBem() {
        this.valorBem = BigDecimal.ZERO;
        this.valorAjuste = BigDecimal.ZERO;
        this.valorAtual = BigDecimal.ZERO;
    }

    public Date getDataEfetivacao() {
        return dataEfetivacao;
    }

    public void setDataEfetivacao(Date dataEfetivacao) {
        this.dataEfetivacao = dataEfetivacao;
    }

    public String getCodigoEfetivacao() {
        return codigoEfetivacao;
    }

    public void setCodigoEfetivacao(String codigoEfetivacao) {
        this.codigoEfetivacao = codigoEfetivacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigoBem() {
        return codigoBem;
    }

    public void setCodigoBem(String codigoBem) {
        this.codigoBem = codigoBem;
    }

    public String getDescricaoBem() {
        return descricaoBem;
    }

    public void setDescricaoBem(String descricaoBem) {
        this.descricaoBem = descricaoBem;
    }

    public BigDecimal getValorBem() {
        return valorBem;
    }

    public void setValorBem(BigDecimal valorBem) {
        this.valorBem = valorBem;
    }

    public String getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(String grupoBem) {
        this.grupoBem = grupoBem;
    }

    public String getCodigoAdmDestino() {
        return codigoAdmDestino;
    }

    public void setCodigoAdmDestino(String codigoAdmDestino) {
        this.codigoAdmDestino = codigoAdmDestino;
    }

    public String getDescricaoAdmDestino() {
        return descricaoAdmDestino;
    }

    public void setDescricaoAdmDestino(String descricaoAdmDestino) {
        this.descricaoAdmDestino = descricaoAdmDestino;
    }

    public BigDecimal getValorAjuste() {
        return valorAjuste;
    }

    public void setValorAjuste(BigDecimal valorAjuste) {
        this.valorAjuste = valorAjuste;
    }

    public BigDecimal getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(BigDecimal valorAtual) {
        this.valorAtual = valorAtual;
    }

    public String getCodigoAdmOrigem() {
        return codigoAdmOrigem;
    }

    public void setCodigoAdmOrigem(String codigoAdmOrigem) {
        this.codigoAdmOrigem = codigoAdmOrigem;
    }

    public String getDescricaoAdmOrigem() {
        return descricaoAdmOrigem;
    }

    public void setDescricaoAdmOrigem(String descricaoAdmOrigem) {
        this.descricaoAdmOrigem = descricaoAdmOrigem;
    }

    public static List<VOTransferenciaEfetivadaBem> preencherDados(List<Object[]> objects) {
        List<VOTransferenciaEfetivadaBem> toReturn = new ArrayList<>();
        for (Object[] obj : objects) {
            VOTransferenciaEfetivadaBem item = new VOTransferenciaEfetivadaBem();
            item.setDataEfetivacao((Date) obj[0]);
            item.setCodigoEfetivacao(((BigDecimal) obj[1]).toString());
            item.setDescricao((String) obj[2]);
            item.setCodigoAdmDestino((String) obj[3]);
            item.setDescricaoAdmDestino(((String) obj[4]).toUpperCase());
            item.setCodigoAdmOrigem((String) obj[5]);
            item.setDescricaoAdmOrigem(((String) obj[6]).toUpperCase());
            item.setCodigoBem((String) obj[7]);
            item.setDescricaoBem((String) obj[8]);
            item.setGrupoBem((String) obj[9]);
            item.setValorBem((BigDecimal) obj[10]);
            item.setValorAjuste((BigDecimal) obj[11]);
            item.setValorAtual((BigDecimal) obj[12]);
            toReturn.add(item);
        }
        return toReturn;
    }
}
