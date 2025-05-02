package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

/**
 * Created by Fabio on 30/01/2020.
 */
public class VOLoteBaixa implements Comparable<VOLoteBaixa> {

    private Long id;
    private String codigoLote;
    private String conta;
    private String banco;
    private Date dataPagamento;
    private Date dataContabilizacao;
    private Long idBanco;

    public static VOLoteBaixa toVO(VOItemLoteBaixa vo) {
        VOLoteBaixa voLoteBaixa = new VOLoteBaixa();
        voLoteBaixa.setId(vo.getIdLoteBaixa());
        voLoteBaixa.setCodigoLote(vo.getCodigoLote());
        voLoteBaixa.setConta(vo.getConta());
        voLoteBaixa.setBanco(vo.getBanco());
        voLoteBaixa.setDataPagamento(vo.getDataPagamento());
        voLoteBaixa.setDataContabilizacao(vo.getDataContabilizacao());
        voLoteBaixa.setIdBanco(vo.getIdBanco());
        return voLoteBaixa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public Long getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(Long idBanco) {
        this.idBanco = idBanco;
    }

    public Date getDataContabilizacao() {
        return dataContabilizacao;
    }

    public void setDataContabilizacao(Date dataContabilizacao) {
        this.dataContabilizacao = dataContabilizacao;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public void setCodigoLote(String codigoLote) {
        this.codigoLote = codigoLote;
    }

    public String getBancoConta() {
        return getBanco() + " - " + getConta();
    }

    @Override
    public int compareTo(VOLoteBaixa o) {
        return this.getId().compareTo(o.getId());
    }
}
