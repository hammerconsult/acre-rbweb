/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.pccr.MesesProgressaoProgressaoPCS;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Progressao PCCR")
public class ProgressaoPCS implements Serializable, Comparable<ProgressaoPCS> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private String codigo;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Plano de Cargos Salários")
    @ManyToOne
    private PlanoCargosSalarios planoCargosSalarios;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Categoria PCS")
    private CategoriaPCS categoriaPCS;
    @Tabelavel
    @Etiqueta("Progressão PCCR Superior")
    @ManyToOne
    @Pesquisavel
    private ProgressaoPCS superior;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "superior", orphanRemoval = true)
    private List<ProgressaoPCS> filhos;
    @OneToMany(mappedBy = "progressaoPCS", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MesesProgressaoProgressaoPCS> mesesProgressao = new ArrayList<>();
    private Integer ordem;
    @Invisivel
    private String migracaoChave;
    @Transient
    @Invisivel
    private Long criadoEm;

    public ProgressaoPCS() {
        criadoEm = System.nanoTime();
        filhos = new LinkedList<>();
    }

    public ProgressaoPCS(String descricao, PlanoCargosSalarios planoCargosSalarios, ProgressaoPCS superior, List<ProgressaoPCS> filhos) {
        this.descricao = descricao;
        this.planoCargosSalarios = planoCargosSalarios;
        this.superior = superior;
        this.filhos = filhos;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<ProgressaoPCS> getFilhos() {
        return filhos;
    }

    public void setFilhos(List<ProgressaoPCS> filhos) {
        this.filhos = filhos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlanoCargosSalarios getPlanoCargosSalarios() {
        return planoCargosSalarios;
    }

    public void setPlanoCargosSalarios(PlanoCargosSalarios planoCargosSalarios) {
        this.planoCargosSalarios = planoCargosSalarios;
    }

    public ProgressaoPCS getSuperior() {
        return superior;
    }

    public void setSuperior(ProgressaoPCS superior) {
        this.superior = superior;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public CategoriaPCS getCategoriaPCS() {
        return categoriaPCS;
    }

    public void setCategoriaPCS(CategoriaPCS categoriaPCS) {
        this.categoriaPCS = categoriaPCS;
    }

    public List<MesesProgressaoProgressaoPCS> getMesesProgressao() {
        return mesesProgressao;
    }

    public void setMesesProgressao(List<MesesProgressaoProgressaoPCS> mesesProgressao) {
        this.mesesProgressao = mesesProgressao;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getEstruturaProgressaoPCS() {
        if (getSuperior() == null) {
            return descricao;

        } else {
            return getSuperior().toString() + "/" + descricao;
        }
    }

    @Override
    public int compareTo(ProgressaoPCS o) {
        try {
            if (ordem == null && descricao != null) {
                return descricao.compareTo(o.getDescricao());
            }
            return ordem.compareTo(o.getOrdem());
        } catch (Exception e) {
            return 0;
        }
    }
}
