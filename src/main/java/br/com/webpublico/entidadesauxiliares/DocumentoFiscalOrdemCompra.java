package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.UF;
import br.com.webpublico.entidadesauxiliares.contabil.LiquidacaoVO;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class DocumentoFiscalOrdemCompra {

    private Long id;
    private String numero;
    private Date dataDocto;
    private Date dataAtesto;
    private String tipoDocumento;
    private String chaveDeAcesso;
    private String serie;
    private UF uf;
    private BigDecimal valor;
    private String situacao;
    private List<LiquidacaoVO> liquidacoes;

    public DocumentoFiscalOrdemCompra() {
        liquidacoes = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataDocto() {
        return dataDocto;
    }

    public void setDataDocto(Date dataDocto) {
        this.dataDocto = dataDocto;
    }

    public Date getDataAtesto() {
        return dataAtesto;
    }

    public void setDataAtesto(Date dataAtesto) {
        this.dataAtesto = dataAtesto;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getChaveDeAcesso() {
        return chaveDeAcesso;
    }

    public void setChaveDeAcesso(String chaveDeAcesso) {
        this.chaveDeAcesso = chaveDeAcesso;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public UF getUf() {
        return uf;
    }

    public void setUf(UF uf) {
        this.uf = uf;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public List<LiquidacaoVO> getLiquidacoes() {
        return liquidacoes;
    }

    public void setLiquidacoes(List<LiquidacaoVO> liquidacoes) {
        this.liquidacoes = liquidacoes;
    }
}
