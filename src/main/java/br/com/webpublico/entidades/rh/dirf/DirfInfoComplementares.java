package br.com.webpublico.entidades.rh.dirf;

import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by William on 28/12/2018.
 */
@Entity
@Etiqueta("Informações Complementares")
public class DirfInfoComplementares implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private DirfVinculoFP dirfVinculoFP;
    @ManyToOne
    private PessoaFisica pessoaFisica;
    private BigDecimal valor;
    private BigDecimal valorDecimo;
    @Transient
    private String cpfBeneficiarioPensao;
    @Transient
    private Date nascimentoBeneficiarioPensao;
    @Transient
    private String nomeBeneficiarioPensao;

    public DirfInfoComplementares() {
        valor = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DirfVinculoFP getDirfVinculoFP() {
        return dirfVinculoFP;
    }

    public void setDirfVinculoFP(DirfVinculoFP dirfVinculoFP) {
        this.dirfVinculoFP = dirfVinculoFP;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getCpfBeneficiarioPensao() {
        return cpfBeneficiarioPensao;
    }

    public void setCpfBeneficiarioPensao(String cpfBeneficiarioPensao) {
        this.cpfBeneficiarioPensao = cpfBeneficiarioPensao;
    }

    public Date getNascimentoBeneficiarioPensao() {
        return nascimentoBeneficiarioPensao;
    }

    public void setNascimentoBeneficiarioPensao(Date nascimentoBeneficiarioPensao) {
        this.nascimentoBeneficiarioPensao = nascimentoBeneficiarioPensao;
    }

    public String getNomeBeneficiarioPensao() {
        return nomeBeneficiarioPensao;
    }

    public void setNomeBeneficiarioPensao(String nomeBeneficiarioPensao) {
        this.nomeBeneficiarioPensao = nomeBeneficiarioPensao;
    }

    public BigDecimal getValorDecimo() {
        return valorDecimo;
    }

    public void setValorDecimo(BigDecimal valorDecimo) {
        this.valorDecimo = valorDecimo;
    }
}
