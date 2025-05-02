/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Representa a Lei de Diretrizes Orçamentária gerada para cada ano fiscal.
 *
 * @author arthur
 */
@GrupoDiagrama(nome = "PPA")
@Audited
@Entity

@Etiqueta("LDO")
public class LDO extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Exercício")
    @ManyToOne
    @Obrigatorio
    private Exercicio exercicio;
    @Tabelavel
    @ManyToOne
    @Pesquisavel
    @Etiqueta("PPA")
    @Obrigatorio
    private PPA ppa;
    /**
     * Lei aprovada ("a" LDO).
     */
    @ManyToOne
    @Etiqueta(value = "Lei")
    @Pesquisavel
    @Tabelavel
    private AtoLegal atoLegal;
    /**
     * Data na qual esta versão da LDO foi gerada.
     */
    @Etiqueta("Data de Registro")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    /**
     * Data na qual esta versão da LDO foi aprovada.
     */
    @Tabelavel
    @Etiqueta("Aprovação")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Pesquisavel
    private Date aprovacao;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Versão")
    private String versao;
    @OneToMany(mappedBy = "ldo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProvisaoPPALDO> provisaoPPALDOs;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Alocado")
    private Boolean alocado;
    @Transient
    private Long criadoEm;

    public LDO() {
        dataRegistro = new Date();
        alocado = Boolean.FALSE;
    }

    public LDO(Exercicio exercicio, PPA ppa, AtoLegal atoLegal, Date dataRegistro, Date aprovacao, String versao) {
        this.exercicio = exercicio;
        this.ppa = ppa;
        this.atoLegal = atoLegal;
        this.dataRegistro = dataRegistro;
        this.aprovacao = aprovacao;
        this.versao = versao;
    }

    public List<ProvisaoPPALDO> getProvisaoPPALDOs() {
        return provisaoPPALDOs;
    }

    public void setProvisaoPPALDOs(List<ProvisaoPPALDO> provisaoPPALDOs) {
        this.provisaoPPALDOs = provisaoPPALDOs;
    }

    public Date getAprovacao() {
        return aprovacao;
    }

    public void setAprovacao(Date aprovacao) {
        this.aprovacao = aprovacao;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = new Date();
    }

    public Boolean getAlocado() {
        return alocado;
    }

    public void setAlocado(Boolean alocado) {
        this.alocado = alocado;
    }

    //    public String getVersao() {
//        return versao;
//    }
//
//    public void setVersao(String versao) {
//        this.versao = versao;
//    }
    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public PPA getPpa() {
        return ppa;
    }

    public void setPpa(PPA ppa) {
        this.ppa = ppa;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this,obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        if (atoLegal != null) {
            return exercicio.getAno() + " - " + atoLegal + " (V.: " + versao + " )";
        } else {
            return exercicio.getAno().toString() + " (V.: " + versao + " )";
        }

    }
}
