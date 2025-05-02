package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RelacaoTaxasDiversas {

    private Long id;
    private Long numero;
    private Integer exercicio;
    private Date dataRequerimento;
    private Date dataVencimentoDam;
    private String cadastro;
    private String requerente;
    private String situacaoTaxa;
    private String situacaoDam;
    private String usuarioTaxa;
    private String usuarioDam;
    private BigDecimal totalTaxa;
    private List<ItemRelacaoTaxasDiversas> tributos;

    public RelacaoTaxasDiversas() {
        this.tributos = Lists.newArrayList();
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

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public Date getDataRequerimento() {
        return dataRequerimento;
    }

    public void setDataRequerimento(Date dataRequerimento) {
        this.dataRequerimento = dataRequerimento;
    }

    public Date getDataVencimentoDam() {
        return dataVencimentoDam;
    }

    public void setDataVencimentoDam(Date dataVencimentoDam) {
        this.dataVencimentoDam = dataVencimentoDam;
    }

    public String getCadastro() {
        return cadastro;
    }

    public void setCadastro(String cadastro) {
        this.cadastro = cadastro;
    }

    public String getRequerente() {
        return requerente;
    }

    public void setRequerente(String requerente) {
        this.requerente = requerente;
    }

    public String getSituacaoTaxa() {
        return situacaoTaxa;
    }

    public void setSituacaoTaxa(String situacaoTaxa) {
        this.situacaoTaxa = situacaoTaxa;
    }

    public String getSituacaoDam() {
        return situacaoDam;
    }

    public void setSituacaoDam(String situacaoDam) {
        this.situacaoDam = situacaoDam;
    }

    public String getUsuarioTaxa() {
        return usuarioTaxa;
    }

    public void setUsuarioTaxa(String usuarioTaxa) {
        this.usuarioTaxa = usuarioTaxa;
    }

    public String getUsuarioDam() {
        return usuarioDam;
    }

    public void setUsuarioDam(String usuarioDam) {
        this.usuarioDam = usuarioDam;
    }

    public BigDecimal getTotalTaxa() {
        return totalTaxa;
    }

    public void setTotalTaxa(BigDecimal totalTaxa) {
        this.totalTaxa = totalTaxa;
    }

    public List<ItemRelacaoTaxasDiversas> getTributos() {
        return tributos;
    }

    public void setTributos(List<ItemRelacaoTaxasDiversas> tributos) {
        this.tributos = tributos;
    }

    public static class ItemRelacaoTaxasDiversas {
        private String descricao;

        public ItemRelacaoTaxasDiversas() {
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }
}
