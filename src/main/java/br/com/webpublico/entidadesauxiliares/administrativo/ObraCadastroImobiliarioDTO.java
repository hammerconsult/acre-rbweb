package br.com.webpublico.entidadesauxiliares.administrativo;

import java.math.BigDecimal;

public class ObraCadastroImobiliarioDTO {


    private String descricao;
    private String inscricacaoCadastral;
    private String lote;
    private String quadra;
    private String setor;
    private String endereco;
    private BigDecimal porcentagem;


    public ObraCadastroImobiliarioDTO() {

        porcentagem = BigDecimal.ZERO;
    }

    public String getInscricacaoCadastral() {
        return inscricacaoCadastral;
    }

    public void setInscricacaoCadastral(String inscricacaoCadastral) {
        this.inscricacaoCadastral = inscricacaoCadastral;
    }

    public BigDecimal getPorcentagem() {
        return porcentagem;
    }

    public void setPorcentagem(BigDecimal porcentagem) {
        this.porcentagem = porcentagem;
    }


    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getQuadra() {
        return quadra;
    }

    public void setQuadra(String quadra) {
        this.quadra = quadra;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public void setEndereco(String endereco) {
        endereco = endereco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEndereco() {
        return endereco;
    }
}
