/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoUnidadeGestora;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Administrativo")
@Etiqueta("Unidade Gestora")
public class UnidadeGestora extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Etiqueta("Código")
    @Tabelavel
    @Pesquisavel
    private String codigo;
    @Etiqueta("Código TCE")
    @Tabelavel
    @Pesquisavel
    private String codigoTCE;
    @Obrigatorio
    @Etiqueta("Descrição")
    @Tabelavel
    @Pesquisavel
    private String descricao;
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Pessoa Jurídica")
    private PessoaJuridica pessoaJuridica;
    @ManyToOne
    @Tabelavel
    private Exercicio exercicio;
    @Etiqueta("Unidades Organizacionais")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "unidadeGestora", orphanRemoval = true)
    private List<UnidadeGestoraUnidadeOrganizacional> unidadeGestoraUnidadesOrganizacionais;
    @Transient
    private Boolean erroDuranteProcessamento;
    @Transient
    private String mensagemDeErro;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Unidade Gestora")
    private TipoUnidadeGestora tipoUnidadeGestora;

    public UnidadeGestora() {
        super();
        unidadeGestoraUnidadesOrganizacionais = Lists.newArrayList();
    }

    public List<UnidadeGestoraUnidadeOrganizacional> getUnidadeGestoraUnidadesOrganizacionais() {
        return unidadeGestoraUnidadesOrganizacionais;
    }

    public void setUnidadeGestoraUnidadesOrganizacionais(List<UnidadeGestoraUnidadeOrganizacional> unidadeGestoraUnidadesOrganizacionais) {
        this.unidadeGestoraUnidadesOrganizacionais = unidadeGestoraUnidadesOrganizacionais;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoTCE() {
        return codigoTCE;
    }

    public void setCodigoTCE(String codigoTCE) {
        this.codigoTCE = codigoTCE;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Boolean getErroDuranteProcessamento() {
        return erroDuranteProcessamento != null ? erroDuranteProcessamento : Boolean.FALSE;
    }

    public void setErroDuranteProcessamento(Boolean erroDuranteProcessamento) {
        this.erroDuranteProcessamento = erroDuranteProcessamento;
    }

    public String getMensagemDeErro() {
        return mensagemDeErro;
    }

    public void setMensagemDeErro(String mensagemDeErro) {
        this.mensagemDeErro = mensagemDeErro;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao + " - PJ (" + pessoaJuridica + ")";
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoUnidadeGestora getTipoUnidadeGestora() {
        return tipoUnidadeGestora;
    }

    public void setTipoUnidadeGestora(TipoUnidadeGestora tipoUnidadeGestora) {
        this.tipoUnidadeGestora = tipoUnidadeGestora;
    }
}
