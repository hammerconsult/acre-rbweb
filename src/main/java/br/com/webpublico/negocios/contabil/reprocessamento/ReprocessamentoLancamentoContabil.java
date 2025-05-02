package br.com.webpublico.negocios.contabil.reprocessamento;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoEventoContabil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 23/12/13
 * Time: 14:20
 * To change this template use File | Settings | File Templates.
 */
public class ReprocessamentoLancamentoContabil {
    private Date dataInicio;
    private Date dataFim;
    private List<TipoEventoContabil> tiposEventoContabilSelecionado;
    private List<EventosReprocessar> eventosReprocessarDoTipoSelecionado;
    private List<EventosReprocessar> eventosParaAdicionar;
    private List<EventosReprocessar> eventosPendentes;
    private List<EventosReprocessar> eventosProcessados;
    private List<EventosReprocessar> eventosIgnorados;
    private List<ReprocessamentoContabilHistorico> historicos;
    private EventosReprocessar eventoReprocessarRemover;
    private EventosReprocessar eventoReprocessarVisualizacao;
    private List<EventosReprocessar> lista;
    private List<HierarquiaOrganizacional> listaHierarquias;
    private Boolean reprocessamentoInicial;
    private Date data;
    private UnidadeOrganizacional unidadeOrganizacional;
    private UsuarioSistema usuarioSistema;
    private List<ReprocessamentoLancamentoContabilLog> logsAuxiliares;


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

    public List<TipoEventoContabil> getTiposEventoContabilSelecionado() {
        return tiposEventoContabilSelecionado;
    }

    public void setTiposEventoContabilSelecionado(List<TipoEventoContabil> tiposEventoContabilSelecionado) {
        this.tiposEventoContabilSelecionado = tiposEventoContabilSelecionado;
    }

    public List<EventosReprocessar> getEventosReprocessarDoTipoSelecionado() {
        return eventosReprocessarDoTipoSelecionado;
    }

    public void setEventosReprocessarDoTipoSelecionado(List<EventosReprocessar> eventosReprocessarDoTipoSelecionado) {
        this.eventosReprocessarDoTipoSelecionado = eventosReprocessarDoTipoSelecionado;
    }

    public List<EventosReprocessar> getEventosParaAdicionar() {
        return eventosParaAdicionar;
    }

    public void setEventosParaAdicionar(List<EventosReprocessar> eventosParaAdicionar) {
        this.eventosParaAdicionar = eventosParaAdicionar;
    }

    public List<EventosReprocessar> getEventosPendentes() {
        return eventosPendentes;
    }

    public void setEventosPendentes(List<EventosReprocessar> eventosPendentes) {
        this.eventosPendentes = eventosPendentes;
    }

    public List<EventosReprocessar> getEventosProcessados() {
        return eventosProcessados;
    }

    public void setEventosProcessados(List<EventosReprocessar> eventosProcessados) {
        this.eventosProcessados = eventosProcessados;
    }

    public List<EventosReprocessar> getEventosIgnorados() {
        return eventosIgnorados;
    }

    public void setEventosIgnorados(List<EventosReprocessar> eventosIgnorados) {
        this.eventosIgnorados = eventosIgnorados;
    }

    public EventosReprocessar getEventoReprocessarRemover() {
        return eventoReprocessarRemover;
    }

    public void setEventoReprocessarRemover(EventosReprocessar eventoReprocessarRemover) {
        this.eventoReprocessarRemover = eventoReprocessarRemover;
    }

    public EventosReprocessar getEventoReprocessarVisualizacao() {
        return eventoReprocessarVisualizacao;
    }

    public void setEventoReprocessarVisualizacao(EventosReprocessar eventoReprocessarVisualizacao) {
        this.eventoReprocessarVisualizacao = eventoReprocessarVisualizacao;
    }

    public List<EventosReprocessar> getLista() {
        return lista;
    }

    public void setLista(List<EventosReprocessar> lista) {
        this.lista = lista;
    }

    public List<HierarquiaOrganizacional> getListaHierarquias() {
        return listaHierarquias;
    }

    public void setListaHierarquias(List<HierarquiaOrganizacional> listaHierarquias) {
        this.listaHierarquias = listaHierarquias;
    }

    public Boolean getReprocessamentoInicial() {
        return reprocessamentoInicial;
    }

    public void setReprocessamentoInicial(Boolean reprocessamentoInicial) {
        this.reprocessamentoInicial = reprocessamentoInicial;
    }

    public List<ReprocessamentoContabilHistorico> getHistoricos() {
        return historicos;
    }

    public void setHistoricos(List<ReprocessamentoContabilHistorico> historicos) {
        this.historicos = historicos;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
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

    public List<ReprocessamentoLancamentoContabilLog> getLogsAuxiliares() {
        if(logsAuxiliares == null){
            logsAuxiliares = new ArrayList<ReprocessamentoLancamentoContabilLog>();
        }
        return logsAuxiliares;
    }

    public void setLogsAuxiliares(List<ReprocessamentoLancamentoContabilLog> logsAuxiliares) {
        this.logsAuxiliares = logsAuxiliares;
    }
}
