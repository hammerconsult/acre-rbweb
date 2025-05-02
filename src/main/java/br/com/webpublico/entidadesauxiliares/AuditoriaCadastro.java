package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

/**
 * Created by HardRock on 11/04/2017.
 */
public class AuditoriaCadastro {

    private String usuarioSistema;
    private Date dataRevisao;
    private String tipoMovimento;
    private String inscricao;
    private String detalhe;
    private Integer quantidade;
    private Long idMovimento;
    private Long idRevisao;
    private TipoCadastro tipoCadastro;
    private String tipoCadastroAsString;

    public String getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(String usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataRevisao() {
        return dataRevisao;
    }

    public void setDataRevisao(Date dataRevisao) {
        this.dataRevisao = dataRevisao;
    }

    public String getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(String tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public String getInscricao() {
        return inscricao;
    }

    public void setInscricao(String inscricao) {
        this.inscricao = inscricao;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(String detalhe) {
        this.detalhe = detalhe;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Long getIdMovimento() {
        return idMovimento;
    }

    public void setIdMovimento(Long idMovimento) {
        this.idMovimento = idMovimento;
    }

    public Long getIdRevisao() {
        return idRevisao;
    }

    public void setIdRevisao(Long idRevisao) {
        this.idRevisao = idRevisao;
    }

    public String getTipoCadastroAsString() {
        return tipoCadastro.getDescricao();
    }

    public TipoCadastro getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastro tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public enum TipoCadastro {
        IMOBILIARIO("Imobiliário"),
        ECONOMICO("Econômico"),
        PESSOA_FISICA("Pessoa Física"),
        PESSOA_JURIDICA("Pessoa Jurídica");
        private String descricao;

        TipoCadastro(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public boolean isCadastroImobiliario() {
        return this.tipoCadastro != null && TipoCadastro.IMOBILIARIO.equals(this.tipoCadastro);
    }

    public boolean isCadastroEconomico() {
        return this.tipoCadastro != null && TipoCadastro.ECONOMICO.equals(this.tipoCadastro);
    }

    public boolean isCadastroPessoaFisica() {
        return this.tipoCadastro != null && TipoCadastro.PESSOA_FISICA.equals(this.tipoCadastro);
    }

    public boolean isCadastroPessoaJuridica() {
        return this.tipoCadastro != null && TipoCadastro.PESSOA_JURIDICA.equals(this.tipoCadastro);
    }
}
