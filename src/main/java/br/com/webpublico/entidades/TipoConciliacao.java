/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoCadastralContabil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author major
 */
@Entity
@Audited

@Etiqueta("Tipo de Conciliação")
public class TipoConciliacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data")
    private Date dataLancamento;
    @Etiqueta("Número")
    @Pesquisavel
    @Tabelavel
    private String numero;
    @Obrigatorio
    @Etiqueta("Descrição")
    @Pesquisavel
    @Tabelavel
    private String descricao;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoCadastralContabil situacaoCadastralContabil;

    public TipoConciliacao() {
        situacaoCadastralContabil = SituacaoCadastralContabil.ATIVO;
    }

    public TipoConciliacao(String numero, String descricao, SituacaoCadastralContabil situacaoCadastralContabil) {
        this.numero = numero;
        this.descricao = descricao;
        this.situacaoCadastralContabil = situacaoCadastralContabil;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public SituacaoCadastralContabil getSituacaoCadastralContabil() {
        return situacaoCadastralContabil;
    }

    public void setSituacaoCadastralContabil(SituacaoCadastralContabil situacaoCadastralContabil) {
        this.situacaoCadastralContabil = situacaoCadastralContabil;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
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
        if (!(object instanceof TipoConciliacao)) {
            return false;
        }
        TipoConciliacao other = (TipoConciliacao) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String retorno = "";
        if (numero != null && descricao != null) {
            return retorno + numero + " - " + descricao;
        }
        if (!"".equals(retorno)) {
            return retorno;
        }
        return "";
    }
}
