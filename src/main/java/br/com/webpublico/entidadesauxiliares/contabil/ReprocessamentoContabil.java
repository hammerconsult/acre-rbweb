package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoBalancete;
import br.com.webpublico.enums.TipoEventoContabil;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class ReprocessamentoContabil {
    private Date dataInicio;
    private Date dataFim;
    private Exercicio exercicio;
    private UnidadeOrganizacional unidadeOrganizacional;
    private UsuarioSistema usuarioSistema;
    private List<TipoBalancete> tipoBalancetes;
    private List<EventoContabil> eventos;
    private List<HierarquiaOrganizacional> listaHierarquias;
    private BarraProgressoAssistente barraProgressoAssistente;
    private List<TipoEventoContabil> tipoEventoContabils;
    private List<EventosReprocessar> eventosReprocessar;

    public ReprocessamentoContabil() {
        eventos = Lists.newArrayList();
        tipoBalancetes = Lists.newArrayList();
        barraProgressoAssistente = new BarraProgressoAssistente();
        listaHierarquias = Lists.newArrayList();
        tipoEventoContabils = Lists.newArrayList();
        eventosReprocessar = Lists.newArrayList();
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }


    public List<HierarquiaOrganizacional> getListaHierarquias() {
        return listaHierarquias;
    }

    public void setListaHierarquias(List<HierarquiaOrganizacional> listaHierarquias) {
        this.listaHierarquias = listaHierarquias;
    }

    public List<TipoBalancete> getTipoBalancetes() {
        return tipoBalancetes;
    }

    public void setTipoBalancetes(List<TipoBalancete> tipoBalancetes) {
        this.tipoBalancetes = tipoBalancetes;
    }

    public List<EventoContabil> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventoContabil> eventos) {
        this.eventos = eventos;
    }

    public BarraProgressoAssistente getBarraProgressoAssistente() {
        return barraProgressoAssistente;
    }

    public void setBarraProgressoAssistente(BarraProgressoAssistente barraProgressoAssistente) {
        this.barraProgressoAssistente = barraProgressoAssistente;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public List<TipoEventoContabil> getTipoEventoContabils() {
        return tipoEventoContabils;
    }

    public void setTipoEventoContabils(List<TipoEventoContabil> tipoEventoContabils) {
        this.tipoEventoContabils = tipoEventoContabils;
    }

    public List<EventosReprocessar> getEventosReprocessar() {
        return eventosReprocessar;
    }

    public void setEventosReprocessar(List<EventosReprocessar> eventosReprocessar) {
        this.eventosReprocessar = eventosReprocessar;
    }

    public String getUnidadesAsString() {
        StringBuilder retorno = new StringBuilder();
        String separador = "";
        for (HierarquiaOrganizacional hierarquiaOrganizacional : getListaHierarquias()) {
            retorno.append(separador);
            retorno.append(hierarquiaOrganizacional);
            separador = ", ";
        }
        return retorno.toString().length() >= 170 ? retorno.toString().substring(0, 170) + " ..." : retorno.toString();
    }
}
