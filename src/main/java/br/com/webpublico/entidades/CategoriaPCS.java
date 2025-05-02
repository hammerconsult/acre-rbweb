/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.pccr.MesesProgressaoCategoria;
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
@Etiqueta("Categoria PCCR")
public class CategoriaPCS extends SuperEntidade implements Serializable, Comparable<CategoriaPCS> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Plano de Cargos Salário")
    @ManyToOne
    private PlanoCargosSalarios planoCargosSalarios;
    @Tabelavel
    @ManyToOne
    @Etiqueta("Categoria PCCR superior")
    @Pesquisavel
    private CategoriaPCS superior;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "superior")
    private List<CategoriaPCS> filhos;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoriaPCS", orphanRemoval = true)
    private List<CargoCategoriaPCS> cargosCategoriaPCS = new ArrayList<CargoCategoriaPCS>();
    @OneToMany(mappedBy = "categoriaPCS", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MesesProgressaoCategoria> mesesPromocao = new ArrayList<>();

    private Integer ordem;
    private String requisito;
    @Invisivel
    private String migracaoChave;
    private Integer codigo;
    @Transient
    private Long criadoEm;

    public CategoriaPCS(String descricao, PlanoCargosSalarios planoCargosSalarios, CategoriaPCS superior, List<CategoriaPCS> filhos, List<CargoCategoriaPCS> cargosCategoriaPCS) {
        this.descricao = descricao;
        this.planoCargosSalarios = planoCargosSalarios;
        this.superior = superior;
        this.filhos = filhos;
        this.cargosCategoriaPCS = cargosCategoriaPCS;
    }

    public CategoriaPCS() {
        criadoEm = System.nanoTime();
        filhos = new LinkedList<>();
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getRequisito() {
        return requisito;
    }

    public void setRequisito(String requisito) {
        this.requisito = requisito;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public List<CategoriaPCS> getFilhos() {
        return filhos;
    }

    public void setFilhos(List<CategoriaPCS> filhos) {
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

    public CategoriaPCS getSuperior() {
        return superior;
    }

    public void setSuperior(CategoriaPCS superior) {
        this.superior = superior;
    }

    public List<CargoCategoriaPCS> getCargosCategoriaPCS() {
        return cargosCategoriaPCS;
    }

    public void setCargosCategoriaPCS(List<CargoCategoriaPCS> cargosCategoriaPCS) {
        this.cargosCategoriaPCS = cargosCategoriaPCS;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<MesesProgressaoCategoria> getMesesPromocao() {
        return mesesPromocao;
    }

    public void setMesesPromocao(List<MesesProgressaoCategoria> mesesPromocao) {
        this.mesesPromocao = mesesPromocao;
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

    public String getEstruturaCategoriaPCS() {
        if (getSuperior() == null) {
            return descricao;

        } else {
            return getSuperior().toString() + "/" + descricao;
        }
    }

    public List<CategoriaPCS> getTodosFilhos() {
        List<CategoriaPCS> lista = new ArrayList<>();
        for (CategoriaPCS cpcs : filhos) {
            if (cpcs.getFilhos().isEmpty()) {
                lista.add(cpcs);
            } else {
                lista.addAll(cpcs.getTodosFilhos());
            }
        }

        return lista;
    }

    @Override
    public int compareTo(CategoriaPCS o) {
        if (ordem != null && o.getOrdem() != null) {
            return ordem.compareTo(o.getOrdem());
        }
        if (descricao != null && o.getDescricao() != null) {
            return descricao.compareTo(o.getDescricao());
        }
        return 0;
    }

    public boolean temPlanoCargosSalarios() {
        return planoCargosSalarios != null;
    }
//      public Cliente getNodePai(Node n) {
//        while (n.getPai() != null) {
//            Node node = em.find(Node.class, n.getPai().getId());
//            if (node.getPai() == null) {
//                return (Cliente) node.getPessoa();
//            }
//            getNodePai(node);
//        }
//        return (Cliente) n.getPessoa();
//    }
}
