/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author venon
 */
@Entity

@Audited
@Inheritance(strategy = InheritanceType.JOINED)
public class OrigemContaContabil implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("TAG")
    private TagOCC tagOCC;
    @ManyToOne
    @Etiqueta(value = "Conta Extra")
    @Tabelavel
    @Pesquisavel
    private ContaContabil contaContabil;
    @ManyToOne
    @Etiqueta(value = "Conta Intra")
    @Tabelavel
    @Pesquisavel
    private ContaContabil contaIntra;
    @ManyToOne
    @Etiqueta(value = "Conta Inter União")
    @Tabelavel
    @Pesquisavel
    private ContaContabil contaInterUniao;
    @ManyToOne
    @Etiqueta(value = "Conta Inter Estado")
    @Tabelavel
    @Pesquisavel
    private ContaContabil contaInterEstado;
    @ManyToOne
    @Etiqueta(value = "Conta Inter Município")
    @Tabelavel
    @Pesquisavel
    private ContaContabil contaInterMunicipal;
    @ManyToOne
    private TipoContaAuxiliar tipoContaAuxiliarSistema;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo Conta Auxiliar SICONFI")
    private TipoContaAuxiliar tipoContaAuxiliarSiconfi;
    @Tabelavel
    @Etiqueta("Inicio de Vigência")
    @Nullable
    @Obrigatorio
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Fim de Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fimVigencia;
    private Boolean reprocessar;
    @ManyToOne
    private OrigemContaContabil origem;
    @Transient
    private Long criadoEm;
    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clp", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<LCP> lcps;
    @Transient
    private Boolean erroDuranteProcessamento;
    @Transient
    private String mensagemDeErro;

    public OrigemContaContabil() {
        criadoEm = System.nanoTime();
        reprocessar = Boolean.FALSE;
        inicioVigencia = new Date();
        lcps = new ArrayList<>();
    }

    public OrigemContaContabil(TagOCC tagOCC, ContaContabil contaContabil, Date inicioVigencia, Date fimVigencia, Boolean reprocessar, OrigemContaContabil origem) {
        this.tagOCC = tagOCC;
        this.contaContabil = contaContabil;
        this.inicioVigencia = inicioVigencia;
        this.fimVigencia = fimVigencia;
        this.reprocessar = reprocessar;
        this.origem = origem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContaContabil getContaContabil() {
        return contaContabil;
    }

    public void setContaContabil(ContaContabil contaContabil) {
        this.contaContabil = contaContabil;
    }

    public ContaContabil getContaIntra() {
        return contaIntra;
    }

    public void setContaIntra(ContaContabil contaIntra) {
        this.contaIntra = contaIntra;
    }

    public ContaContabil getContaInterUniao() {
        return contaInterUniao;
    }

    public void setContaInterUniao(ContaContabil contaInterUniao) {
        this.contaInterUniao = contaInterUniao;
    }

    public ContaContabil getContaInterEstado() {
        return contaInterEstado;
    }

    public void setContaInterEstado(ContaContabil contaInterEstado) {
        this.contaInterEstado = contaInterEstado;
    }

    public ContaContabil getContaInterMunicipal() {
        return contaInterMunicipal;
    }

    public void setContaInterMunicipal(ContaContabil contaInterMunicipal) {
        this.contaInterMunicipal = contaInterMunicipal;
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

    public TagOCC getTagOCC() {
        return tagOCC;
    }

    public void setTagOCC(TagOCC tagOCC) {
        this.tagOCC = tagOCC;
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

    public Boolean getReprocessar() {
        return reprocessar;
    }

    public void setReprocessar(Boolean reprocessar) {
        this.reprocessar = reprocessar;
    }

    public OrigemContaContabil getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemContaContabil origem) {
        this.origem = origem;
    }

    public List<LCP> getLcps() {
        return lcps;
    }

    public void setLcps(List<LCP> lcps) {
        this.lcps = lcps;
    }

    public TipoContaAuxiliar getTipoContaAuxiliarSistema() {
        return tipoContaAuxiliarSistema;
    }

    public void setTipoContaAuxiliarSistema(TipoContaAuxiliar tipoContaAuxiliarSistema) {
        this.tipoContaAuxiliarSistema = tipoContaAuxiliarSistema;
    }

    public TipoContaAuxiliar getTipoContaAuxiliarSiconfi() {
        return tipoContaAuxiliarSiconfi;
    }

    public void setTipoContaAuxiliarSiconfi(TipoContaAuxiliar tipoContaAuxiliarSiconfi) {
        this.tipoContaAuxiliarSiconfi = tipoContaAuxiliarSiconfi;
    }

    public int meuHashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.id);
        hash = 29 * hash + Objects.hashCode(this.contaContabil);
        hash = 29 * hash + Objects.hashCode(this.contaIntra);
        hash = 29 * hash + Objects.hashCode(this.contaInterUniao);
        hash = 29 * hash + Objects.hashCode(this.contaInterEstado);
        hash = 29 * hash + Objects.hashCode(this.contaInterMunicipal);
        return hash;
    }

    public boolean meuEquals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OrigemContaContabil other = (OrigemContaContabil) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.contaContabil, other.contaContabil)) {
            return false;
        }
        if (!Objects.equals(this.contaIntra, other.contaIntra)) {
            return false;
        }
        if (!Objects.equals(this.contaInterUniao, other.contaInterUniao)) {
            return false;
        }
        if (!Objects.equals(this.contaInterEstado, other.contaInterEstado)) {
            return false;
        }
        if (!Objects.equals(this.contaInterMunicipal, other.contaInterMunicipal)) {
            return false;
        }
        return true;
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
        try {
            return "Vigente em " + DataUtil.getDataFormatada(this.inicioVigencia)
                + ", tag: " + (this.tagOCC != null ? this.tagOCC.toString() : "") + ",";
        } catch (Exception ex) {
            return "";
        }
    }
}
