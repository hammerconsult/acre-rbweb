/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author daniel
 */
@Entity

@Audited
@Etiqueta("Processo de Cálculo")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ProcessoCalculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Exercício")
    @Tabelavel
    @Pesquisavel
    private Exercicio exercicio;
    private Boolean completo;
    @ManyToOne
    @Etiqueta("Dívida")
    @Tabelavel
    @Pesquisavel
    private Divida divida;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Lançamento")
    @Tabelavel
    @Pesquisavel
    private Date dataLancamento;
    @Etiqueta("Descrição")
    @Tabelavel
    @Pesquisavel
    private String descricao;
    @ManyToOne
    private UsuarioSistema usuarioSistema;

    @Etiqueta("Número Protocolo")
    @Tabelavel
    @Pesquisavel
    private String numeroProtocolo;

    @Etiqueta("Exercício Protocolo")
    @Tabelavel
    @Pesquisavel
    private String anoProtocolo;

    public ProcessoCalculo(Long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
        dataLancamento = new Date();
    }

    public ProcessoCalculo() {
    }

    public abstract List<? extends Calculo> getCalculos();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getCompleto() {
        return completo;
    }

    public void setCompleto(Boolean completo) {
        this.completo = completo;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
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
        if (!(object instanceof ProcessoCalculo)) {
            return false;
        }
        ProcessoCalculo other = (ProcessoCalculo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ProcessoCalculo[ id=" + id + " ]";
    }
}
