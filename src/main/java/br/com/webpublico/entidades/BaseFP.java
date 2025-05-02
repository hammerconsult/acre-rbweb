/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.FiltroBaseFP;
import br.com.webpublico.enums.rh.administracaopagamento.IdentificadorBaseFP;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author andre
 */
@Entity

@GrupoDiagrama(nome = "RecursosHumanos")
@Audited
@Etiqueta("Base Folha de Pagamento")
public class BaseFP implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Invisivel
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * Código utilizado nas impressões dos documentos.
     */
    @Pesquisavel
    @Etiqueta("Código")
    @Obrigatorio
    @Tabelavel
    private String codigo;
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Descrição")
    @Tabelavel
    private String descricao;
    /**
     * Utilizada para exibição em documentos impressos com pouco espaço.
     */
    @Pesquisavel
    @Etiqueta("Descrição Reduzida")
    @Tabelavel
    private String descricaoReduzida;
    @OneToMany(mappedBy = "baseFP", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy
    private List<ItemBaseFP> itensBasesFPs;
    @Invisivel
    private String migracaoChave;
    @Enumerated(EnumType.STRING)
    private FiltroBaseFP filtroBaseFP;
    @Enumerated(EnumType.STRING)
    private IdentificadorBaseFP tipoBaseFP;

    public BaseFP() {
        filtroBaseFP = FiltroBaseFP.NORMAL;
        itensBasesFPs = new ArrayList<>();
    }

    public FiltroBaseFP getFiltroBaseFP() {
        return filtroBaseFP;
    }

    public void setFiltroBaseFP(FiltroBaseFP filtroBaseFP) {
        this.filtroBaseFP = filtroBaseFP;
    }

    public Long getId() {
        return id;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
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

    public String getDescricaoReduzida() {
        return descricaoReduzida;
    }

    public void setDescricaoReduzida(String descricaoReduzida) {
        this.descricaoReduzida = descricaoReduzida;
    }

    public List<ItemBaseFP> getItensBasesFPs() {
        return itensBasesFPs;
    }

    public void setItensBasesFPs(List<ItemBaseFP> itensBasesFPs) {
        this.itensBasesFPs = itensBasesFPs;
    }

    public IdentificadorBaseFP getTipoBaseFP() {
        return tipoBaseFP;
    }

    public void setTipoBaseFP(IdentificadorBaseFP tipoBaseFP) {
        this.tipoBaseFP = tipoBaseFP;
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
        if (!(object instanceof BaseFP)) {
            return false;
        }
        BaseFP other = (BaseFP) object;
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
