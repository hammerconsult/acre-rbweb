/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades.rh.administracaodepagamento;

import br.com.webpublico.entidades.RecursoFP;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.annotations.OrderBy;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
public class FichaFinanceiraFPSimulacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private FolhaDePagamentoSimulacao folhaDePagamentoSimulacao;
    @ManyToOne
    private VinculoFP vinculoFP;
    @ManyToOne
    private RecursoFP recursoFP;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @OneToMany(mappedBy = "fichaFinanceiraFPSimulacao")
    @OrderBy(clause = "eventoFP DESC")
    private List<ItemFichaFinanceiraFPSimulacao> itemFichaFinanceiraFPSimulacoes;

    public FolhaDePagamentoSimulacao getFolhaDePagamentoSimulacao() {
        return folhaDePagamentoSimulacao;
    }

    public void setFolhaDePagamentoSimulacao(FolhaDePagamentoSimulacao folhaDePagamentoSimulacao) {
        this.folhaDePagamentoSimulacao = folhaDePagamentoSimulacao;
    }

    public List<ItemFichaFinanceiraFPSimulacao> getItemFichaFinanceiraFPSimulacoes() {
        return itemFichaFinanceiraFPSimulacoes;
    }

    public void setItemFichaFinanceiraFPSimulacoes(List<ItemFichaFinanceiraFPSimulacao> itemFichaFinanceiraFPSimulacoes) {
        this.itemFichaFinanceiraFPSimulacoes = itemFichaFinanceiraFPSimulacoes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RecursoFP getRecursoFP() {
        return recursoFP;
    }

    public void setRecursoFP(RecursoFP recursoFP) {
        this.recursoFP = recursoFP;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FichaFinanceiraFPSimulacao other = (FichaFinanceiraFPSimulacao) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
