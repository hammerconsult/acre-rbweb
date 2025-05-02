package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by William on 11/01/2016.
 */
@Entity
@Audited
public class SociedadePessoaJuridica extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Pessoa socio;
    private Double proporcao;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataInicio;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataFim;
    @ManyToOne
    private PessoaJuridica pessoaJuridica;

    private BigDecimal participacao;
    private String nire;
    private String condicao;
    private Boolean administrador;
    private String cargo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getSocio() {
        return socio;
    }

    public void setSocio(Pessoa socio) {
        this.socio = socio;
    }

    public Double getProporcao() {
        return proporcao != null ? proporcao : 0D;
    }

    public void setProporcao(Double proporcao) {
        this.proporcao = proporcao;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    public BigDecimal getParticipacao() {
        return participacao;
    }

    public void setParticipacao(BigDecimal participacao) {
        this.participacao = participacao;
    }

    public String getNire() {
        return nire;
    }

    public void setNire(String nire) {
        this.nire = nire;
    }

    public String getCondicao() {
        return condicao;
    }

    public void setCondicao(String condicao) {
        this.condicao = condicao;
    }

    public Boolean getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Boolean administrador) {
        this.administrador = administrador;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}
