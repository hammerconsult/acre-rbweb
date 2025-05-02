package br.com.webpublico.controle;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.EventoBem;
import br.com.webpublico.interfaces.ManipuladorDeBem;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.BemFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 01/10/14
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public abstract class ManipuladorDeBemImpl implements ManipuladorDeBem, Serializable {

    List listaEncontradosNaPesquisa;
    List listaDeEventosSelecionados;
    private BemFacade bemFacade;


    protected ManipuladorDeBemImpl(BemFacade bemFacade) {
        this.listaDeEventosSelecionados = Lists.newArrayList();
        this.listaEncontradosNaPesquisa = Lists.newArrayList();
        this.bemFacade = bemFacade;
    }

    @Override
    public void selecionar(EventoBem evento) {
        try {
            bemFacade.verificarDisponibilidadeParaMoviventacoes(evento.getBem());
            listaDeEventosSelecionados.add(evento);
            atualizar();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void reinicializarManipulador() {
        this.listaDeEventosSelecionados.clear();
        this.listaEncontradosNaPesquisa.clear();
        atualizar();
    }

    @Override
    public void selecionarTodos() {
        for (EventoBem novoEventoBem : (List<EventoBem>) listaEncontradosNaPesquisa) {
            try {
                if (!listaDeEventosSelecionados.contains(novoEventoBem)) {
                    bemFacade.verificarDisponibilidadeParaMoviventacoes(novoEventoBem.getBem());
                    listaDeEventosSelecionados.add(novoEventoBem);
                }
            } catch (ExcecaoNegocioGenerica ex) {
                FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            }
        }
        atualizar();
    }

    @Override
    public void desmarcarTodos() {
        listaDeEventosSelecionados.clear();
        atualizar();
    }

    @Override
    public void desmarcar(EventoBem eventoBem) {
        try{
            listaDeEventosSelecionados.remove(recuperarEventoDoBem(eventoBem.getBem()));
            atualizar();
        }catch (Exception e){
        }
    }

    @Override
    public EventoBem recuperarEventoDoBem(Bem bem) {
        for (EventoBem ev : (List<EventoBem>) listaDeEventosSelecionados) {
            if (ev.getBem().equals(bem)) {
                return ev;
            }
        }
        return null;
    }

    public void adicionarEventoBemSomenteDisponiveis(List eventos) {
        for (EventoBem novoEventoBem : (List<EventoBem>) eventos) {
            try {
                bemFacade.verificarDisponibilidadeParaMoviventacoes(novoEventoBem.getBem());
                listaDeEventosSelecionados.add(novoEventoBem);
            } catch (ExcecaoNegocioGenerica ex) {
                FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            }
        }
    }


    public abstract void atualizar();

    public List getListaDeEventosSelecionados() {
        return listaDeEventosSelecionados;
    }

    public void setListaDeEventosSelecionados(List listaDeEventosSelecionados) {
        this.listaDeEventosSelecionados = listaDeEventosSelecionados;
    }

    public List getListaEncontradosNaPesquisa() {
        return listaEncontradosNaPesquisa;
    }

    public void setListaEncontradosNaPesquisa(List listaEncontradosNaPesquisa) {
        this.listaEncontradosNaPesquisa = listaEncontradosNaPesquisa;
    }


    public Boolean mostrarBotaoSelecionarTodos() {
        try {
            return listaDeEventosSelecionados.size() != listaEncontradosNaPesquisa.size();
        } catch (NullPointerException ex) {
            return Boolean.FALSE;
        }
    }

    public Boolean mostrarBotaoSelecionar(EventoBem eventoBem) {
        for (EventoBem ev : (List<EventoBem>) listaDeEventosSelecionados) {
            if (ev.getBem().equals(eventoBem.getBem())) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public void actionPesquisar() {
        if (validarCampos()) {
            pesquisar();
        }
    }

    protected boolean validarCampos() {
        return true;
    }

    public BigDecimal valorTotalDosBens() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (!getListaDeEventosSelecionados().isEmpty()) {
            for (EventoBem evento :(List<EventoBem>) getListaDeEventosSelecionados()) {
                valorTotal = valorTotal.add(evento.getValorDoLancamento());
            }
        }
        return valorTotal;
    }

    public BigDecimal valorTotalDosAjustes() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (!getListaDeEventosSelecionados().isEmpty()) {
            for (EventoBem evento :(List<EventoBem>) getListaDeEventosSelecionados()) {
                valorTotal = valorTotal.add(evento.getEstadoResultante().getValorDosAjustes());
            }
        }
        return valorTotal;
    }

    public BigDecimal getValorTotalDoLiquido() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (!getListaDeEventosSelecionados().isEmpty()) {
            for (EventoBem evento : (List<EventoBem>) getListaDeEventosSelecionados()) {
                valorTotal = valorTotal.add(evento.getBem().getValorLiquido());
            }
        }
        return valorTotal;
    }

    public String formatarValor(BigDecimal valor) {
        return Util.formataValor(valor);
    }
}
