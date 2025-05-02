/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Modalidade de Benefícios")
public class ModalidadeBeneficioFP implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Descrição")
    @Pesquisavel
    @Tabelavel
    private String descricao;
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Vantagem Vencimento Base")
    @Tabelavel
    @Obrigatorio
    private EventoFP vantagemVencimentoBase;
    @OneToMany(mappedBy = "modalidadeBeneficioFP")
    private List<BeneficioFP> beneficios;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    @Obrigatorio
    private Long codigo;

    public List<BeneficioFP> getBeneficios() {
        return beneficios;
    }

    public void setBeneficios(List<BeneficioFP> beneficios) {
        this.beneficios = beneficios;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventoFP getVantagemVencimentoBase() {
        return vantagemVencimentoBase;
    }

    public void setVantagemVencimentoBase(EventoFP vantagemVencimentoBase) {
        this.vantagemVencimentoBase = vantagemVencimentoBase;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ModalidadeBeneficioFP other = (ModalidadeBeneficioFP) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }
}
