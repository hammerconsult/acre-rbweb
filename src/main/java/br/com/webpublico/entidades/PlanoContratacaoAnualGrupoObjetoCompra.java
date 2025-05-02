package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
@Table(name = "PLANOCONTRATANUALGRUPO")
@Etiqueta("Grupo Objeto de Compra - PCA")
public class PlanoContratacaoAnualGrupoObjetoCompra extends SuperEntidade implements Comparable<PlanoContratacaoAnualGrupoObjetoCompra>{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private PlanoContratacaoAnual planoContratacaoAnual;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Grupo Objeto de Compra")
    private GrupoObjetoCompra grupoObjetoCompra;

    @OneToMany(mappedBy = "planoContratacaoAnualGrupo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanoContratacaoAnualObjetoCompra> objetosCompra;

    public PlanoContratacaoAnualGrupoObjetoCompra() {
        objetosCompra = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlanoContratacaoAnual getPlanoContratacaoAnual() {
        return planoContratacaoAnual;
    }

    public void setPlanoContratacaoAnual(PlanoContratacaoAnual planoContratacaoAnual) {
        this.planoContratacaoAnual = planoContratacaoAnual;
    }

    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(GrupoObjetoCompra grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public List<PlanoContratacaoAnualObjetoCompra> getObjetosCompra() {
        return objetosCompra;
    }

    public void setObjetosCompra(List<PlanoContratacaoAnualObjetoCompra> objetosCompra) {
        this.objetosCompra = objetosCompra;
    }

    @Override
    public int compareTo(PlanoContratacaoAnualGrupoObjetoCompra o) {
        if (getGrupoObjetoCompra() !=null && o.getGrupoObjetoCompra() !=null){
            return ComparisonChain.start().compare(getGrupoObjetoCompra().getCodigo(), o.getGrupoObjetoCompra().getCodigo()).result();
        }
        return 0;
    }
}
