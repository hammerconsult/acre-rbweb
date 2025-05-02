package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 17/12/13
 * Time: 14:42
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Parâmetros de Subvenção")
public class SubvencaoParametro implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(mappedBy = "subvencaoParametro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CadastroEconomicoSubvencao> cadastroEconomicoSubvencao;
    @OneToMany(mappedBy = "subvencaoParametro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DividaSubvencao> listaDividaSubvencao;
    @OneToMany(mappedBy = "subvencaoParametro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdenacaoParcelaSubvencao> itemOrdanacaoParcelaSubvencao;
    @ManyToOne
    private TipoDoctoOficial tipoDoctoOficialTermo;
    @Tabelavel
    private String descricao;
    private String primeiroAssinante;
    private String primeiroCargo;
    private String primeiroDecreto;
    private String segundoAssinante;
    private String segundoCargo;
    private String segundoDecreto;


    public SubvencaoParametro() {
        this.listaDividaSubvencao = Lists.newArrayList();
        this.cadastroEconomicoSubvencao = Lists.newArrayList();
        this.itemOrdanacaoParcelaSubvencao = Lists.newArrayList();
        this.descricao = "PARÂMETROS DE SUBVENÇÃO";
    }

    public String getSegundoAssinante() {
        return segundoAssinante;
    }

    public void setSegundoAssinante(String segundoAssinante) {
        this.segundoAssinante = segundoAssinante;
    }

    public String getSegundoCargo() {
        return segundoCargo;
    }

    public void setSegundoCargo(String segundoCargo) {
        this.segundoCargo = segundoCargo;
    }

    public String getSegundoDecreto() {
        return segundoDecreto;
    }

    public void setSegundoDecreto(String segundoDecreto) {
        this.segundoDecreto = segundoDecreto;
    }

    public String getPrimeiroAssinante() {
        return primeiroAssinante;
    }

    public void setPrimeiroAssinante(String primeiroAssinante) {
        this.primeiroAssinante = primeiroAssinante;
    }

    public String getPrimeiroCargo() {
        return primeiroCargo;
    }

    public void setPrimeiroCargo(String primeiroCargo) {
        this.primeiroCargo = primeiroCargo;
    }

    public String getPrimeiroDecreto() {
        return primeiroDecreto;
    }

    public void setPrimeiroDecreto(String primeiroDecreto) {
        this.primeiroDecreto = primeiroDecreto;
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

    public List<DividaSubvencao> getListaDividaSubvencao() {
        return listaDividaSubvencao;
    }

    public void setListaDividaSubvencao(List<DividaSubvencao> listaDividaSubvencao) {
        this.listaDividaSubvencao = listaDividaSubvencao;
    }

    public List<CadastroEconomicoSubvencao> getCadastroEconomicoSubvencao() {
        return cadastroEconomicoSubvencao;
    }

    public void setCadastroEconomicoSubvencao(List<CadastroEconomicoSubvencao> cadastroEconomicoSubvencao) {
        this.cadastroEconomicoSubvencao = cadastroEconomicoSubvencao;
    }

    public List<OrdenacaoParcelaSubvencao> getItemOrdanacaoParcelaSubvencao() {
        return itemOrdanacaoParcelaSubvencao;
    }

    public void setItemOrdanacaoParcelaSubvencao(List<OrdenacaoParcelaSubvencao> itemOrdanacaoParcelaSubvencao) {
        this.itemOrdanacaoParcelaSubvencao = itemOrdanacaoParcelaSubvencao;
    }

    public TipoDoctoOficial getTipoDoctoOficialTermo() {
        return tipoDoctoOficialTermo;
    }

    public void setTipoDoctoOficialTermo(TipoDoctoOficial tipoDoctoOficialTermo) {
        this.tipoDoctoOficialTermo = tipoDoctoOficialTermo;
    }
}
