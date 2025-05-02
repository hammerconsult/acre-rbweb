/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Persistencia;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * @author major
 */
//@Entity
//
//@Audited
public class ObjetoParametro implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String idObjeto;
    private String classeObjeto;
    @Transient
    private TipoObjetoParametro tipoObjetoParametro;
    //    @ManyToOne
    private ItemParametroEvento itemParametroEvento;
    @Transient
    private Long criadoEm;
    @Transient
    private Boolean gerarContaAuxiliar;
    @Transient
    private Object entidade;

    public ObjetoParametro() {
        criadoEm = System.nanoTime();
        this.tipoObjetoParametro = TipoObjetoParametro.AMBOS;
    }

    public ObjetoParametro(Object entidade, ItemParametroEvento itemParametroEvento) {
        criarObjeto(entidade, itemParametroEvento, TipoObjetoParametro.AMBOS);
    }

    public ObjetoParametro(Object entidade, ItemParametroEvento itemParametroEvento, TipoObjetoParametro tipoObjetoParametro) {
        criarObjeto(entidade, itemParametroEvento, tipoObjetoParametro);
    }
    private void criarObjeto(Object entidade, ItemParametroEvento itemParametroEvento, TipoObjetoParametro tipoObjetoParametro) {
        this.entidade = entidade;
        this.classeObjeto = entidade.getClass().getSimpleName();
        if (!entidade.getClass().isEnum()) {
            Object id = Persistencia.getId(entidade);
            if (id != null) {
                this.idObjeto = id.toString();
            }
        } else {
            Enum<?> e = (Enum<?>) entidade;
            this.idObjeto = e.name();
        }
        this.gerarContaAuxiliar = entidade instanceof SuperEntidadeContabilGerarContaAuxiliar;
        this.itemParametroEvento = itemParametroEvento;
        this.criadoEm = System.nanoTime();
        this.tipoObjetoParametro = tipoObjetoParametro;
    }

    public ItemParametroEvento getItemParametroEvento() {
        return itemParametroEvento;
    }

    public void setItemParametroEvento(ItemParametroEvento itemParametroEvento) {
        this.itemParametroEvento = itemParametroEvento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClasseObjeto() {
        return classeObjeto;
    }

    public void setClasseObjeto(String classeObjeto) {
        this.classeObjeto = classeObjeto;
    }

    public String getIdObjeto() {
        return idObjeto;
    }

    public void setIdObjeto(String idObjeto) {
        this.idObjeto = idObjeto;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public TipoObjetoParametro getTipoObjetoParametro() {
        return tipoObjetoParametro;
    }

    public void setTipoObjetoParametro(TipoObjetoParametro tipoObjetoParametro) {
        this.tipoObjetoParametro = tipoObjetoParametro;
    }

    public Boolean getGerarContaAuxiliar() {
        return gerarContaAuxiliar;
    }

    public void setGerarContaAuxiliar(Boolean gerarContaAuxiliar) {
        this.gerarContaAuxiliar = gerarContaAuxiliar;
    }

    public Object getEntidade() {
        return entidade;
    }

    public void setEntidade(Object entidade) {
        this.entidade = entidade;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "ObjetoParametro{" +
            "id=" + id +
            ", idObjeto='" + idObjeto + '\'' +
            ", classeObjeto='" + classeObjeto + '\'' +
            ", tipoObjetoParametro=" + tipoObjetoParametro +
            ", itemParametroEvento=" + itemParametroEvento +
            ", criadoEm=" + criadoEm +
            '}';
    }

    public enum TipoObjetoParametro {
        DEBITO,
        CREDITO,
        AMBOS;
    }
}
