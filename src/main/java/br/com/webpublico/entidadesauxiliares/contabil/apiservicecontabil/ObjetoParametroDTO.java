/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil;

import br.com.webpublico.entidades.ObjetoParametro;

import java.io.Serializable;

public class ObjetoParametroDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String idObjeto;
    private String classeObjeto;
    private ObjetoParametro.TipoObjetoParametro tipoObjetoParametro;
    private Boolean gerarContaAuxiliar;
    private Boolean movimentarContabilidadeRecebido;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdObjeto() {
        return idObjeto;
    }

    public void setIdObjeto(String idObjeto) {
        this.idObjeto = idObjeto;
    }

    public String getClasseObjeto() {
        return classeObjeto;
    }

    public void setClasseObjeto(String classeObjeto) {
        this.classeObjeto = classeObjeto;
    }

    public ObjetoParametro.TipoObjetoParametro getTipoObjetoParametro() {
        return tipoObjetoParametro;
    }

    public void setTipoObjetoParametro(ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) {
        this.tipoObjetoParametro = tipoObjetoParametro;
    }

    public Boolean getGerarContaAuxiliar() {
        return gerarContaAuxiliar;
    }

    public void setGerarContaAuxiliar(Boolean gerarContaAuxiliar) {
        this.gerarContaAuxiliar = gerarContaAuxiliar;
    }

    public Boolean getMovimentarContabilidadeRecebido() {
        return movimentarContabilidadeRecebido;
    }

    public void setMovimentarContabilidadeRecebido(Boolean movimentarContabilidadeRecebido) {
        this.movimentarContabilidadeRecebido = movimentarContabilidadeRecebido;
    }
}
