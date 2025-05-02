package br.com.webpublico.entidades;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import javax.persistence.*;

@Entity
public class ParcelaRemessaProtesto extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String numeroCDA;

    @ManyToOne
    private RemessaProtesto remessaProtesto;
    @ManyToOne
    private ParcelaValorDivida parcelaValorDivida;

    private String codigoProcesso;
    private String cadastroProcesso;
    private String nossoNumero;
    private String situacaoProtesto;
    @ManyToOne
    private CertidaoDividaAtiva cda;
    @Transient
    private ResultadoParcela resultadoParcela;

    public ParcelaRemessaProtesto() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroCDA() {
        return numeroCDA;
    }

    public void setNumeroCDA(String numeroCDA) {
        this.numeroCDA = numeroCDA;
    }

    public RemessaProtesto getRemessaProtesto() {
        return remessaProtesto;
    }

    public void setRemessaProtesto(RemessaProtesto remessaProtesto) {
        this.remessaProtesto = remessaProtesto;
    }

    public ParcelaValorDivida getParcelaValorDivida() {
        return parcelaValorDivida;
    }

    public void setParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        this.parcelaValorDivida = parcelaValorDivida;
    }

    public String getCodigoProcesso() {
        return codigoProcesso;
    }

    public void setCodigoProcesso(String codigoProcesso) {
        this.codigoProcesso = codigoProcesso;
    }

    public String getCadastroProcesso() {
        return cadastroProcesso;
    }

    public void setCadastroProcesso(String cadastroProcesso) {
        this.cadastroProcesso = cadastroProcesso;
    }

    public String getSituacaoProtesto() {
        return situacaoProtesto;
    }

    public void setSituacaoProtesto(String situacaoProtesto) {
        this.situacaoProtesto = situacaoProtesto;
    }

    public ResultadoParcela getResultadoParcela() {
        return resultadoParcela;
    }

    public void setResultadoParcela(ResultadoParcela resultadoParcela) {
        this.resultadoParcela = resultadoParcela;
    }

    public String getNossoNumero() {
        return nossoNumero;
    }

    public void setNossoNumero(String nossoNumero) {
        this.nossoNumero = nossoNumero;
    }

    public CertidaoDividaAtiva getCda() {
        return cda;
    }

    public void setCda(CertidaoDividaAtiva cda) {
        this.cda = cda;
    }
}
