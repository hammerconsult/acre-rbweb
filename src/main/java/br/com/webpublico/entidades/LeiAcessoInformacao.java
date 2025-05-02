package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by carlos on 22/07/15.
 */
@Audited
@Entity
@Etiqueta("LAI - Lei de Acesso à Informação")
public class LeiAcessoInformacao implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Mês")
    @Enumerated(EnumType.STRING)
    private Mes mes;
    @Etiqueta("Ano")
    private Integer ano;
    @ManyToOne
    @Etiqueta("Matrícula Servidor")
    private MatriculaFP matriculaFP;
    @ManyToOne
    @Etiqueta("Unidade")
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    private ContratoFP contratoFP;
    @ManyToOne
    private PessoaFisica pessoaFisica;
    @ManyToOne
    private Cargo cargo;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    private Integer versao;
    private BigDecimal salarioBruto;
    private BigDecimal DescontoSalario;
    private BigDecimal salarioLiquido;
    private String tipoFolha;

    public LeiAcessoInformacao() {
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MatriculaFP getMatriculaFP() {
        return matriculaFP;
    }

    public void setMatriculaFP(MatriculaFP matriculaFP) {
        this.matriculaFP = matriculaFP;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public BigDecimal getSalarioBruto() {
        return salarioBruto;
    }

    public void setSalarioBruto(BigDecimal salarioBruto) {
        this.salarioBruto = salarioBruto;
    }

    public BigDecimal getDescontoSalario() {
        return DescontoSalario;
    }

    public void setDescontoSalario(BigDecimal descontoSalario) {
        DescontoSalario = descontoSalario;
    }

    public BigDecimal getSalarioLiquido() {
        return salarioLiquido;
    }

    public void setSalarioLiquido(BigDecimal salarioLiquido) {
        this.salarioLiquido = salarioLiquido;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public String getTipoFolha() {
        return tipoFolha;
    }

    public void setTipoFolha(String tipoFolha) {
        this.tipoFolha = tipoFolha;
    }
}
