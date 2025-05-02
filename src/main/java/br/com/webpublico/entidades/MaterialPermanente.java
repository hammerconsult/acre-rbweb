package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 02/12/14
 * Time: 15:04
 * To change this template use File | Settings | File Templates.
 */
@Audited
@Entity
@Etiqueta("Material Permanente")
public class MaterialPermanente extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Código")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Column(unique = true)
    private Long codigo;

    @Obrigatorio
    @Etiqueta("Descrição")
    @Tabelavel
    @Pesquisavel
    private String descricao;

    @Etiqueta("Descrição Complementar")
    private String descricaoComplementar;

    @Etiqueta("Marca")
    @Pesquisavel
    @ManyToOne
    @Tabelavel
    private Marca marca;

    @Etiqueta("Modelo")
    @Pesquisavel
    @ManyToOne
    @Tabelavel
    private Modelo modelo;

    @Pesquisavel
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Objeto de Compra")
    @Tabelavel
    private ObjetoCompra objetoCompra;

    @Pesquisavel
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade de Medida")
    @Tabelavel
    private UnidadeMedida unidadeMedida;

    @ManyToOne
    private GrupoObjetoCompraGrupoBem associacaoDeGrupos;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    public MaterialPermanente() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao.trim();
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar.trim();
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public GrupoObjetoCompraGrupoBem getAssociacaoDeGrupos() {
        return associacaoDeGrupos;
    }

    public void setAssociacaoDeGrupos(GrupoObjetoCompraGrupoBem associacaoDeGrupos) {
        this.associacaoDeGrupos = associacaoDeGrupos;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }

    public GrupoObjetoCompra getGrupoObjetoCompra() {
        try {
            return this.getObjetoCompra().getGrupoObjetoCompra();
        } catch (NullPointerException npe) {
            return null;
        }
    }
}
