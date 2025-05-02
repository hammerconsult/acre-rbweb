/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoAlteracaoReceitaLoa;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author major
 */
@Entity
@Audited
@Etiqueta("Alteração de Receita LOA")

public class AlteracaoReceitaLoa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Receita LOA")
    private ReceitaLOA receitaLOA;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Alteração")
    private Date dataAlteracao;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo da Alteração")
    private TipoAlteracaoReceitaLoa tipoAlteracaoReceitaLoa;
    @Tabelavel
    @Obrigatorio
    @Monetario
    @Pesquisavel
    @Etiqueta("Valor")
    private BigDecimal valor;

    public AlteracaoReceitaLoa() {
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public ReceitaLOA getReceitaLOA() {
        return receitaLOA;
    }

    public void setReceitaLOA(ReceitaLOA receitaLOA) {
        this.receitaLOA = receitaLOA;
    }

    public TipoAlteracaoReceitaLoa getTipoAlteracaoReceitaLoa() {
        return tipoAlteracaoReceitaLoa;
    }

    public void setTipoAlteracaoReceitaLoa(TipoAlteracaoReceitaLoa tipoAlteracaoReceitaLoa) {
        this.tipoAlteracaoReceitaLoa = tipoAlteracaoReceitaLoa;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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
        if (!(object instanceof AlteracaoReceitaLoa)) {
            return false;
        }
        AlteracaoReceitaLoa other = (AlteracaoReceitaLoa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.AlteracaoReceitaLoa[ id=" + id + " ]";
    }
}
