/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author major
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Entidade Beneficiária")
public class EntidadeBeneficiaria extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número")
    private String codigo;
    @Pesquisavel
    @Etiqueta("Responsável")
    @Tabelavel
    @ManyToOne
    @Obrigatorio
    private PessoaFisica pessoaFisica;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Pessoa Jurídica")
    @ManyToOne
    @Obrigatorio
    private PessoaJuridica pessoaJuridica;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Exercício")
    @ManyToOne
    @Obrigatorio
    private Exercicio exercicio;
    @Etiqueta("Classe Beneficiária")
    @ManyToOne
    private ClasseBeneficiario classeBeneficiario;
    @Invisivel
    @OneToMany(mappedBy = "entidadeBeneficiaria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AcoesBeneficiario> listaAcoesBeneficiarios;
    @Invisivel
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "ENTBENEFIC_BENEFICCERT",
            joinColumns = {
                    @JoinColumn(name = "ENTIDADEBENEFICIARIAS_ID", referencedColumnName = "ID")}, inverseJoinColumns = {
            @JoinColumn(name = "CLAUSULASBENEFICIARIOS_ID", referencedColumnName = "ID")})
    private List<ClausulaBenificiario> clausulasBenificiarios;
    @Invisivel
    @OneToMany(mappedBy = "entidadeBeneficiaria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntidadeBeneficCertidoes> entidadeBeneficCertidoess;
    private String objeto;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataCadastro;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Inicio de Vigência")
    @Obrigatorio
    private Date inicioVigencia;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Fim de Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fimVigencia;
    private String orgaoConvenente;

    public EntidadeBeneficiaria() {
        this.inicioVigencia = new Date();
        this.clausulasBenificiarios = new ArrayList<ClausulaBenificiario>();
        this.entidadeBeneficCertidoess = new ArrayList<EntidadeBeneficCertidoes>();
        this.listaAcoesBeneficiarios = new ArrayList<AcoesBeneficiario>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClasseBeneficiario getClasseBeneficiario() {
        return classeBeneficiario;
    }

    public void setClasseBeneficiario(ClasseBeneficiario classeBeneficiario) {
        this.classeBeneficiario = classeBeneficiario;
    }

    public List<ClausulaBenificiario> getClausulasBenificiarios() {
        return clausulasBenificiarios;
    }

    public void setClausulasBenificiarios(List<ClausulaBenificiario> clausulasBenificiarios) {
        this.clausulasBenificiarios = clausulasBenificiarios;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<AcoesBeneficiario> getListaAcoesBeneficiarios() {
        return listaAcoesBeneficiarios;
    }

    public void setListaAcoesBeneficiarios(List<AcoesBeneficiario> listaAcoesBeneficiarios) {
        this.listaAcoesBeneficiarios = listaAcoesBeneficiarios;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public List<EntidadeBeneficCertidoes> getEntidadeBeneficCertidoess() {
        return entidadeBeneficCertidoess;
    }

    public void setEntidadeBeneficCertidoess(List<EntidadeBeneficCertidoes> entidadeBeneficCertidoess) {
        this.entidadeBeneficCertidoess = entidadeBeneficCertidoess;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public String getOrgaoConvenente() {
        return orgaoConvenente;
    }

    public void setOrgaoConvenente(String orgaoConvenente) {
        this.orgaoConvenente = orgaoConvenente;
    }

    @Override
    public String toString() {
        return this.codigo + " " + this.objeto;
    }
}
