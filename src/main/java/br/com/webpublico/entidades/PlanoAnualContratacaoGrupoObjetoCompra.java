package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
@Table(name = "PLANOANUALCONTGRUPO")
@Etiqueta("Grupo Objeto de Compra - PAC")
public class PlanoAnualContratacaoGrupoObjetoCompra extends SuperEntidade implements Comparable<PlanoAnualContratacaoGrupoObjetoCompra>{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private PlanoAnualContratacao planoAnualContratacao;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Grupo Objeto de Compra")
    private GrupoObjetoCompra grupoObjetoCompra;

    @OneToMany(mappedBy = "planoAnualContratacaoGrupo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanoAnualContratacaoObjetoCompra> objetosCompra;

    public PlanoAnualContratacaoGrupoObjetoCompra() {
        objetosCompra = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlanoAnualContratacao getPlanoAnualContratacao() {
        return planoAnualContratacao;
    }

    public void setPlanoAnualContratacao(PlanoAnualContratacao planoAnualContratacao) {
        this.planoAnualContratacao = planoAnualContratacao;
    }

    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(GrupoObjetoCompra grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public List<PlanoAnualContratacaoObjetoCompra> getObjetosCompra() {
        return objetosCompra;
    }

    public void setObjetosCompra(List<PlanoAnualContratacaoObjetoCompra> objetosCompra) {
        this.objetosCompra = objetosCompra;
    }

    @Override
    public int compareTo(PlanoAnualContratacaoGrupoObjetoCompra o) {
        if (getGrupoObjetoCompra() !=null && o.getGrupoObjetoCompra() !=null){
            return ComparisonChain.start().compare(getGrupoObjetoCompra().getCodigo(), o.getGrupoObjetoCompra().getCodigo()).result();
        }
        return 0;
    }
}
