package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 27/05/14
 * Time: 10:07
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Etiqueta("Configuração de Grupo Material")
public class ConfigGrupoMaterial implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Grupo Material")
    private GrupoMaterial grupoMaterial;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Tipo de Estoque")
    private TipoEstoque tipoEstoque;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Conta de Despesa")
    private ContaDespesa contaDespesa;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Fim de Vigência")
    private Date fimVigencia;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Transient
    private Boolean erroDuranteProcessamento;
    @Transient
    private String mensagemDeErro;

    public ConfigGrupoMaterial() {
        criadoEm = System.nanoTime();
    }

    public ConfigGrupoMaterial(GrupoMaterial grupoMaterial, TipoEstoque tipoEstoque, ContaDespesa contaDespesa, Date inicioVigencia, Date fimVigencia) {
        this.grupoMaterial = grupoMaterial;
        this.tipoEstoque = tipoEstoque;
        this.contaDespesa = contaDespesa;
        this.inicioVigencia = inicioVigencia;
        this.fimVigencia = fimVigencia;
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public TipoEstoque getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(TipoEstoque tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }

    public ContaDespesa getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(ContaDespesa contaDespesa) {
        this.contaDespesa = contaDespesa;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigGrupoMaterial)) return false;

        ConfigGrupoMaterial that = (ConfigGrupoMaterial) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String toString() {
        return DataUtil.getDataFormatada(inicioVigencia) + " - " + grupoMaterial + " - " + contaDespesa;
    }
}
