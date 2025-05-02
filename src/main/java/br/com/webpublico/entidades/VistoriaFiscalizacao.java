/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoVistoria;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Heinz
 */
@Etiqueta(value = "Vistoria e Fiscalização")
@GrupoDiagrama(nome = "CadastroEconomico")
@Audited
@Entity

public class VistoriaFiscalizacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Data")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataRegistro;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Número do Processo")
    private String numeroProcesso;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Agente")
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Obrigatorio
    @Etiqueta("Órgao Vistoriador")
    private String orgaoVistoriador;
    @Etiqueta("Parecer")
    private String parecer;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data do Parecer")
    private Date dataParecer;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Tipo de Vistoria")
    @ManyToOne
    private TipoVistoria tipoVistoria;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private SituacaoVistoria situacaoVistoria;
    @Etiqueta("Observação")
    private String observacao;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    @Etiqueta("C.M.C.")
    private CadastroEconomico cadastroEconomico;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vistoriaFiscalizacao", orphanRemoval = true)
    private List<ItemValidacaoFiscalizacao> itens;

    public VistoriaFiscalizacao() {
        this.dataRegistro = new Date();
        this.cadastroEconomico = new CadastroEconomico();
        this.tipoVistoria = new TipoVistoria();
        this.usuarioSistema = new UsuarioSistema();
        this.itens = new ArrayList<ItemValidacaoFiscalizacao>();
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataParecer() {
        return dataParecer;
    }

    public void setDataParecer(Date dataParecer) {
        this.dataParecer = dataParecer;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getOrgaoVistoriador() {
        return orgaoVistoriador;
    }

    public void setOrgaoVistoriador(String orgaoVistoriador) {
        this.orgaoVistoriador = orgaoVistoriador;
    }

    public String getParecer() {
        return parecer;
    }

    public void setParecer(String parecer) {
        this.parecer = parecer;
    }

    public SituacaoVistoria getSituacaoVistoria() {
        return situacaoVistoria;
    }

    public void setSituacaoVistoria(SituacaoVistoria situacaoVistoria) {
        this.situacaoVistoria = situacaoVistoria;
    }

    public TipoVistoria getTipoVistoria() {
        return tipoVistoria;
    }

    public void setTipoVistoria(TipoVistoria tipoVistoria) {
        this.tipoVistoria = tipoVistoria;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public List<ItemValidacaoFiscalizacao> getItens() {
        return itens;
    }

    public void setItens(List<ItemValidacaoFiscalizacao> itens) {
        this.itens = itens;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VistoriaFiscalizacao)) {
            return false;
        }
        VistoriaFiscalizacao other = (VistoriaFiscalizacao) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Vistoria de Fiscalização";
    }
}
