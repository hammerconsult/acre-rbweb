/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil;

import br.com.webpublico.entidades.ParametroEvento;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.enums.TipoBalancete;


import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


public class ParametroEventoDTO implements Serializable {


    private Long id;
    private LocalDate dataEvento;
    private String complementoHistorico;
    private List<ItemParametroEventoDTO> itensParametrosEvento;
    private Long idEventoContabil;
    private Long idUnidadeOrganizacional;
    private String classeOrigem;
    private String idOrigem;
    private ParametroEvento.ComplementoId complementoId;
    private Long idExercicio;
    private TipoBalancete tipoBalancete;
    private GeradorContaAuxiliarDTO geradorContaAuxiliar;
    private Boolean simulacao;

    public ParametroEventoDTO() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(LocalDate dataEvento) {
        this.dataEvento = dataEvento;
    }

    public String getComplementoHistorico() {
        return complementoHistorico;
    }

    public void setComplementoHistorico(String complementoHistorico) {
        this.complementoHistorico = complementoHistorico;
    }

    public List<ItemParametroEventoDTO> getItensParametrosEvento() {
        return itensParametrosEvento;
    }

    public void setItensParametrosEvento(List<ItemParametroEventoDTO> itensParametrosEvento) {
        this.itensParametrosEvento = itensParametrosEvento;
    }

    public Long getIdEventoContabil() {
        return idEventoContabil;
    }

    public void setIdEventoContabil(Long idEventoContabil) {
        this.idEventoContabil = idEventoContabil;
    }

    public Long getIdUnidadeOrganizacional() {
        return idUnidadeOrganizacional;
    }

    public void setIdUnidadeOrganizacional(Long idUnidadeOrganizacional) {
        this.idUnidadeOrganizacional = idUnidadeOrganizacional;
    }

    public String getClasseOrigem() {
        return classeOrigem;
    }

    public void setClasseOrigem(String classeOrigem) {
        this.classeOrigem = classeOrigem;
    }

    public String getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(String idOrigem) {
        this.idOrigem = idOrigem;
    }

    public ParametroEvento.ComplementoId getComplementoId() {
        return complementoId;
    }

    public void setComplementoId(ParametroEvento.ComplementoId complementoId) {
        this.complementoId = complementoId;
    }

    public Long getIdExercicio() {
        return idExercicio;
    }

    public void setIdExercicio(Long idExercicio) {
        this.idExercicio = idExercicio;
    }

    public TipoBalancete getTipoBalancete() {
        return tipoBalancete;
    }

    public void setTipoBalancete(TipoBalancete tipoBalancete) {
        this.tipoBalancete = tipoBalancete;
    }

    public GeradorContaAuxiliarDTO getGeradorContaAuxiliar() {
        return geradorContaAuxiliar;
    }

    public void setGeradorContaAuxiliar(GeradorContaAuxiliarDTO geradorContaAuxiliar) {
        this.geradorContaAuxiliar = geradorContaAuxiliar;
    }

    public Boolean getSimulacao() {
        return simulacao;
    }

    public void setSimulacao(Boolean simulacao) {
        this.simulacao = simulacao;
    }
}
