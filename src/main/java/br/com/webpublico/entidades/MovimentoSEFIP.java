/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoMovimentoSEFIP;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author boy
 */
@Entity

@Audited
@Etiqueta("Movimento SEFIP")
@GrupoDiagrama(nome = "RecursosHumanos")
public class MovimentoSEFIP implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private String codigo;
    @Etiqueta("Descrição")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String descricao;
    @Etiqueta("Tipo de Movimento SEFIP")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoMovimentoSEFIP tipoMovimentoSEFIP;

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoMovimentoSEFIP getTipoMovimentoSEFIP() {
        return tipoMovimentoSEFIP;
    }

    public void setTipoMovimentoSEFIP(TipoMovimentoSEFIP tipoMovimentoSEFIP) {
        this.tipoMovimentoSEFIP = tipoMovimentoSEFIP;
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
        if (!(object instanceof MovimentoSEFIP)) {
            return false;
        }
        MovimentoSEFIP other = (MovimentoSEFIP) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
