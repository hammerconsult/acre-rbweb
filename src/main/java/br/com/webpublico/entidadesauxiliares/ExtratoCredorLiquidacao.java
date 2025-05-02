package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateus on 01/12/2014.
 */
public class ExtratoCredorLiquidacao {

    private Long liquidacaoId;
    private String dataRegistro;
    private String numero;
    private String operacao;
    private BigDecimal valor;
    //utilizado para o estorno
    private String numeroLiquidacao;
    private List<ExtratoCredorLiquidacao> estornos;
    private List<ExtratoCredorDoctoComprobatorio> documentos;

    public ExtratoCredorLiquidacao() {
        estornos = new ArrayList<>();
    }

    public Long getLiquidacaoId() {
        return liquidacaoId;
    }

    public void setLiquidacaoId(Long liquidacaoId) {
        this.liquidacaoId = liquidacaoId;
    }

    public String getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(String dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getNumeroLiquidacao() {
        return numeroLiquidacao;
    }

    public void setNumeroLiquidacao(String numeroLiquidacao) {
        this.numeroLiquidacao = numeroLiquidacao;
    }

    public List<ExtratoCredorLiquidacao> getEstornos() {
        return estornos;
    }

    public void setEstornos(List<ExtratoCredorLiquidacao> estornos) {
        this.estornos = estornos;
    }

    public List<ExtratoCredorDoctoComprobatorio> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<ExtratoCredorDoctoComprobatorio> documentos) {
        this.documentos = documentos;
    }
}
