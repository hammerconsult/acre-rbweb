package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class NFSAvulsaNfseDTO implements NfseDTO {

    private Long id;
    private Long numero;
    private UserNfseDTO user;
    private Date dataNota;
    private PessoaNfseDTO prestador;
    private PessoaNfseDTO tomador;
    private List<NFSAvulsaItemNfseDTO> itens;
    private BigDecimal valorIss;
    private BigDecimal valorTotalIss;
    private BigDecimal valorServicos;

    public NFSAvulsaNfseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataNota() {
        return dataNota;
    }

    public void setDataNota(Date dataNota) {
        this.dataNota = dataNota;
    }

    public UserNfseDTO getUser() {
        return user;
    }

    public void setUser(UserNfseDTO user) {
        this.user = user;
    }

    public PessoaNfseDTO getPrestador() {
        return prestador;
    }

    public void setPrestador(PessoaNfseDTO prestador) {
        this.prestador = prestador;
    }

    public PessoaNfseDTO getTomador() {
        return tomador;
    }

    public void setTomador(PessoaNfseDTO tomador) {
        this.tomador = tomador;
    }

    public List<NFSAvulsaItemNfseDTO> getItens() {
        return itens;
    }

    public void setItens(List<NFSAvulsaItemNfseDTO> itens) {
        this.itens = itens;
    }

    public BigDecimal getValorIss() {
        return valorIss;
    }

    public void setValorIss(BigDecimal valorIss) {
        this.valorIss = valorIss;
    }

    public BigDecimal getValorTotalIss() {
        return valorTotalIss;
    }

    public void setValorTotalIss(BigDecimal valorTotalIss) {
        this.valorTotalIss = valorTotalIss;
    }

    public BigDecimal getValorServicos() {
        return valorServicos;
    }

    public void setValorServicos(BigDecimal valorServicos) {
        this.valorServicos = valorServicos;
    }
}
