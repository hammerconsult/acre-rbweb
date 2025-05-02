package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 09/08/13
 * Time: 17:24
 * To change this template use File | Settings | File Templates.
 */
@Entity

@Audited
@Etiqueta("Parâmetros de Cobrança Administrativa")
@GrupoDiagrama(nome = "Parâmetros de Cobrança Administrativa")
@Table(name = "PARAMETROCOBRANCAADMINI")
public class ParametrosCobrancaAdministrativa implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Exercício")
    @Tabelavel(ordemApresentacao = 1)
    private Exercicio exercicio;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Diretor do Departamento")
    private PessoaFisica diretorDepartamento;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Chefe da Divisão")
    private PessoaFisica chefeDaDivisao;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Cargo")
    private String cargo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Portaria")
    private String portaria;
    @Etiqueta("Dias Após a Notificação")
    private Long diasAposNotificacao;
    @Obrigatorio
    @Etiqueta(value = "Tipo Docto Oficial Cadastro Imobiliário Aviso de Cobrança")
    @ManyToOne
    private TipoDoctoOficial doctoImobiliarioAviso;
    @Obrigatorio
    @Etiqueta(value = "Tipo Docto Oficial Cadastro Econômico Aviso de Cobrança")
    @ManyToOne
    private TipoDoctoOficial doctoEconomicoAviso;
    @Obrigatorio
    @Etiqueta(value = "Tipo Docto Oficial Cadastro Rural Aviso de Cobrança")
    @ManyToOne
    private TipoDoctoOficial doctoRuralAviso;
    @Obrigatorio
    @Etiqueta(value = "Tipo Docto Oficial Contribuinte Geral (PF) Aviso de Cobrança")
    @ManyToOne
    private TipoDoctoOficial doctoContribPFAviso;
    @Obrigatorio
    @Etiqueta(value = "Tipo Docto Oficial Contribuinte Geral (PJ) Aviso de Cobrança")
    @ManyToOne
    private TipoDoctoOficial doctoContribPJAviso;
    @Obrigatorio
    @Etiqueta(value = "Tipo Docto Oficial Cadastro Imobiliário Aviso de Cobrança de Cobrança")
    @ManyToOne
    private TipoDoctoOficial doctoImobiliarioNotificacao;
    @Obrigatorio
    @Etiqueta(value = "Tipo Docto Oficial Cadastro Econômico Notificação de Cobrança")
    @ManyToOne
    private TipoDoctoOficial doctoEconomicoNotificacao;
    @Obrigatorio
    @Etiqueta(value = "Tipo Docto Oficial Cadastro Rural Notificação de Cobrança")
    @ManyToOne
    private TipoDoctoOficial doctoRuralNotificacao;
    @Obrigatorio
    @Etiqueta(value = "Tipo Docto Oficial Contribuinte Geral (PF) Notificação de Cobrança")
    @ManyToOne
    private TipoDoctoOficial doctoContribPFNotificacao;
    @Obrigatorio
    @Etiqueta(value = "Tipo Docto Oficial Contribuinte Geral (PJ) Notificação de Cobrança")
    @ManyToOne
    private TipoDoctoOficial doctoContribPJNotificacao;
    @Invisivel
    @Transient
    private Long criadoEm;

    public ParametrosCobrancaAdministrativa() {
        this.criadoEm = System.currentTimeMillis();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoDoctoOficial getDoctoImobiliarioAviso() {
        return doctoImobiliarioAviso;
    }

    public void setDoctoImobiliarioAviso(TipoDoctoOficial doctoImobiliarioAviso) {
        this.doctoImobiliarioAviso = doctoImobiliarioAviso;
    }

    public TipoDoctoOficial getDoctoEconomicoAviso() {
        return doctoEconomicoAviso;
    }

    public void setDoctoEconomicoAviso(TipoDoctoOficial doctoEconomicoAviso) {
        this.doctoEconomicoAviso = doctoEconomicoAviso;
    }

    public TipoDoctoOficial getDoctoRuralAviso() {
        return doctoRuralAviso;
    }

    public void setDoctoRuralAviso(TipoDoctoOficial doctoRuralAviso) {
        this.doctoRuralAviso = doctoRuralAviso;
    }

    public TipoDoctoOficial getDoctoContribPFAviso() {
        return doctoContribPFAviso;
    }

    public void setDoctoContribPFAviso(TipoDoctoOficial doctoContribPFAviso) {
        this.doctoContribPFAviso = doctoContribPFAviso;
    }

    public TipoDoctoOficial getDoctoContribPJAviso() {
        return doctoContribPJAviso;
    }

    public void setDoctoContribPJAviso(TipoDoctoOficial doctoContribPJAviso) {
        this.doctoContribPJAviso = doctoContribPJAviso;
    }

    public TipoDoctoOficial getDoctoImobiliarioNotificacao() {
        return doctoImobiliarioNotificacao;
    }

    public void setDoctoImobiliarioNotificacao(TipoDoctoOficial doctoImobiliarioNotificacao) {
        this.doctoImobiliarioNotificacao = doctoImobiliarioNotificacao;
    }

    public TipoDoctoOficial getDoctoEconomicoNotificacao() {
        return doctoEconomicoNotificacao;
    }

    public void setDoctoEconomicoNotificacao(TipoDoctoOficial doctoEconomicoNotificacao) {
        this.doctoEconomicoNotificacao = doctoEconomicoNotificacao;
    }

    public TipoDoctoOficial getDoctoRuralNotificacao() {
        return doctoRuralNotificacao;
    }

    public void setDoctoRuralNotificacao(TipoDoctoOficial doctoRuralNotificacao) {
        this.doctoRuralNotificacao = doctoRuralNotificacao;
    }

    public TipoDoctoOficial getDoctoContribPFNotificacao() {
        return doctoContribPFNotificacao;
    }

    public void setDoctoContribPFNotificacao(TipoDoctoOficial doctoContribPFNotificacao) {
        this.doctoContribPFNotificacao = doctoContribPFNotificacao;
    }

    public TipoDoctoOficial getDoctoContribPJNotificacao() {
        return doctoContribPJNotificacao;
    }

    public void setDoctoContribPJNotificacao(TipoDoctoOficial doctoContribPJNotificacao) {
        this.doctoContribPJNotificacao = doctoContribPJNotificacao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public PessoaFisica getDiretorDepartamento() {
        return diretorDepartamento;
    }

    public void setDiretorDepartamento(PessoaFisica diretorDepartamento) {
        this.diretorDepartamento = diretorDepartamento;
    }

    public PessoaFisica getChefeDaDivisao() {
        return chefeDaDivisao;
    }

    public void setChefeDaDivisao(PessoaFisica chefeDaDivisao) {
        this.chefeDaDivisao = chefeDaDivisao;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getPortaria() {
        return portaria;
    }

    public void setPortaria(String portaria) {
        this.portaria = portaria;
    }

    public Long getDiasAposNotificacao() {
        return diasAposNotificacao;
    }

    public void setDiasAposNotificacao(Long diasAposNotificacao) {
        this.diasAposNotificacao = diasAposNotificacao;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "Parâmetros Cobrança Administrativa";
    }
}
