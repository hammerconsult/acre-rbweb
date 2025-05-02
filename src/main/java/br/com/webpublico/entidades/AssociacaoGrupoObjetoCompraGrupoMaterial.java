package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Buzatto on 06/12/2016.
 */
@Entity
@Audited
@Etiqueta("Associação Grupo de Objeto de Compra e Grupo de Materiais")
@Table(name = "ASSOCIACAOGRUOBJCOMGRUMAT")
public class AssociacaoGrupoObjetoCompraGrupoMaterial extends SuperEntidade implements Serializable, ValidadorVigencia {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Etiqueta("Início de Vigência")
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;

    @Etiqueta("Final de Vigência")
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;

    @Obrigatorio
    @Etiqueta("Grupo de Objeto de Compra")
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private GrupoObjetoCompra grupoObjetoCompra;

    @Obrigatorio
    @Etiqueta("Grupo de Materiais")
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private GrupoMaterial grupoMaterial;

    public AssociacaoGrupoObjetoCompraGrupoMaterial() {
        super();
    }

    public AssociacaoGrupoObjetoCompraGrupoMaterial(Date inicioVigencia, GrupoObjetoCompra grupoObjetoCompra, GrupoMaterial grupoMaterial) {
        this();
        this.inicioVigencia = inicioVigencia;
        this.grupoObjetoCompra = grupoObjetoCompra;
        this.grupoMaterial = grupoMaterial;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return getFinalVigencia();
    }

    @Override
    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        setFinalVigencia(data);
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(GrupoObjetoCompra grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    @Override
    public String toString() {
        return "AssociacaoGrupoObjetoCompraGrupoMaterial{" +
            "inicioVigencia=" + inicioVigencia +
            ", finalVigencia=" + finalVigencia +
            ", grupoObjetoCompra=" + grupoObjetoCompra +
            ", grupoMaterial=" + grupoMaterial +
            '}';
    }
}
