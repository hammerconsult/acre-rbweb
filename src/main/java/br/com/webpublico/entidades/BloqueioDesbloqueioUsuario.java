package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Audited
@Entity
@Etiqueta("Bloqueio/Desbloqueio de Usuário")
public class BloqueioDesbloqueioUsuario extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Fim de Vigência")
    private Date fimVigencia;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Bloquear em Afastamento?")
    private Boolean bloquearAfastamento;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Bloquear em Férias?")
    private Boolean bloquearFerias;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Bloquear em Licensa Prêmio?")
    private Boolean bloquearLicensaPremio;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Bloquear em Exoneração?")
    private Boolean bloquearExoneracao;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Bloquear em Cessão Sem Ônus?")
    private Boolean bloquearCessaoSemOnus;
    @Etiqueta("Quantidade de Dias para Bloqueio")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Integer quantidadeDiasBloqueio;

    @Etiqueta("Tempo de bloqueio por inatividade (em dias)")
    private Integer tempoMaximoInatividade;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "bloqueioDesbloqueioUsuario")
    private List<BloqueioDesbloqueioUsuarioEmail> emails;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "bloqueioDesbloqueioUsuario")
    private List<BloqueioDesbloqueioUsuarioTipoAfastamento> tiposDeAfastamento;

    public BloqueioDesbloqueioUsuario() {
        bloquearAfastamento = false;
        bloquearExoneracao = false;
        bloquearFerias = false;
        bloquearLicensaPremio = false;
        emails = Lists.newArrayList();
        tiposDeAfastamento = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public Boolean getBloquearAfastamento() {
        return bloquearAfastamento == null ? Boolean.FALSE : bloquearAfastamento;
    }

    public void setBloquearAfastamento(Boolean bloquearAfastamento) {
        this.bloquearAfastamento = bloquearAfastamento;
    }

    public Boolean getBloquearFerias() {
        return bloquearFerias == null ? Boolean.FALSE : bloquearFerias;
    }

    public void setBloquearFerias(Boolean bloquearFerias) {
        this.bloquearFerias = bloquearFerias;
    }

    public Boolean getBloquearLicensaPremio() {
        return bloquearLicensaPremio == null ? Boolean.FALSE : bloquearLicensaPremio;
    }

    public void setBloquearLicensaPremio(Boolean bloquearLicensaPremio) {
        this.bloquearLicensaPremio = bloquearLicensaPremio;
    }

    public Boolean getBloquearExoneracao() {
        return bloquearExoneracao == null ? Boolean.FALSE : bloquearExoneracao;
    }

    public void setBloquearExoneracao(Boolean bloquearExoneracao) {
        this.bloquearExoneracao = bloquearExoneracao;
    }

    public Integer getQuantidadeDiasBloqueio() {
        return quantidadeDiasBloqueio;
    }

    public void setQuantidadeDiasBloqueio(Integer quantidadeDiasBloqueio) {
        this.quantidadeDiasBloqueio = quantidadeDiasBloqueio;
    }

    public List<BloqueioDesbloqueioUsuarioEmail> getEmails() {
        return emails;
    }

    public void setEmails(List<BloqueioDesbloqueioUsuarioEmail> emails) {
        this.emails = emails;
    }

    public List<BloqueioDesbloqueioUsuarioTipoAfastamento> getTiposDeAfastamento() {
        return tiposDeAfastamento;
    }

    public void setTiposDeAfastamento(List<BloqueioDesbloqueioUsuarioTipoAfastamento> tiposDeAfastamento) {
        this.tiposDeAfastamento = tiposDeAfastamento;
    }

    public Boolean getBloquearCessaoSemOnus() {
        return bloquearCessaoSemOnus != null ? bloquearCessaoSemOnus : false;
    }

    public void setBloquearCessaoSemOnus(Boolean bloquearCessaoSemOnus) {
        this.bloquearCessaoSemOnus = bloquearCessaoSemOnus;
    }

    public Integer getTempoMaximoInatividade() {
        return tempoMaximoInatividade;
    }

    public void setTempoMaximoInatividade(Integer tempoMaximoInatividade) {
        this.tempoMaximoInatividade = tempoMaximoInatividade;
    }
}
