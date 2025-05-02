package br.com.webpublico.entidades.tributario.regularizacaoconstrucao;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributario")
@Etiqueta("Item de Serviços de Construção")
public class ItemServicoConstrucao extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Integer codigo;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Serviços de Construção")
    private ServicoConstrucao servicoConstrucao;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    @OrderBy("areaInicial desc ")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "itemServicoConstrucao")
    @Etiqueta("Lista de Faixa de Valores de Serviço de Construção")
    private List<FaixaDeValoresSCL> listaFaixaDeValoresSCL;
    private Boolean exibirPortalContribuinte;

    public ItemServicoConstrucao() {
        listaFaixaDeValoresSCL = Lists.newArrayList();
        exibirPortalContribuinte = Boolean.TRUE;
    }

    public ItemServicoConstrucao(Integer codigo, ServicoConstrucao servicoConstrucao, String descricao) {
        this.codigo = codigo;
        this.servicoConstrucao = servicoConstrucao;
        this.descricao = descricao;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public ServicoConstrucao getServicoConstrucao() {
        return servicoConstrucao;
    }

    public void setServicoConstrucao(ServicoConstrucao servicoConstrucao) {
        this.servicoConstrucao = servicoConstrucao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<FaixaDeValoresSCL> getListaFaixaDeValoresSCL() {
        if (listaFaixaDeValoresSCL == null) listaFaixaDeValoresSCL = Lists.newArrayList();
        return listaFaixaDeValoresSCL;
    }

    public void setListaFaixaDeValoresSCL(List<FaixaDeValoresSCL> listaFaixaDeValoresSCL) {
        this.listaFaixaDeValoresSCL = listaFaixaDeValoresSCL;
    }

    public Boolean getExibirPortalContribuinte() {
        return exibirPortalContribuinte;
    }

    public void setExibirPortalContribuinte(Boolean exibirPortalContribuinte) {
        this.exibirPortalContribuinte = exibirPortalContribuinte;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
