package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoEventoCalculoIPTUFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.auxiliares.AssistenteCalculadorIPTU;
import br.com.webpublico.negocios.tributario.auxiliares.CalculadorIPTU;
import br.com.webpublico.negocios.tributario.auxiliares.EventoCalculado;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean(name = "configuracaoEventoCalculoIPTUControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoConfiguracaoEventoCalculoIPTU",
        pattern = "/configuracao-de-calculo-de-iptu/novo/",
        viewId = "/faces/tributario/configuracao/eventos/configuracaoeventosiptu/edita.xhtml"),

    @URLMapping(id = "editarConfiguracaoEventoCalculoIPTU",
        pattern = "/configuracao-de-calculo-de-iptu/editar/#{configuracaoEventoCalculoIPTUControlador.id}/",
        viewId = "/faces/tributario/configuracao/eventos/configuracaoeventosiptu/edita.xhtml"),

    @URLMapping(id = "verConfiguracaoEventoCalculoIPTU",
        pattern = "/configuracao-de-calculo-de-iptu/ver/#{configuracaoEventoCalculoIPTUControlador.id}/",
        viewId = "/faces/tributario/configuracao/eventos/configuracaoeventosiptu/visualizar.xhtml"),
    @URLMapping(id = "listarConfiguracaoEventoCalculoIPTU",

        pattern = "/configuracao-de-calculo-de-iptu/listar/",
        viewId = "/faces/tributario/configuracao/eventos/configuracaoeventosiptu/lista.xhtml"),
})
public class ConfiguracaoEventoCalculoIPTUControlador extends PrettyControlador<ConfiguracaoEventoIPTU> implements Serializable, CRUD {


    @EJB
    private ConfiguracaoEventoCalculoIPTUFacade facade;
    @EJB
    private SistemaFacade sistemaFacade;
    private List<EventoCalculo> eventos;
    private Cadastro cadastro;
    private List<EventoCalculado> calculados;

    public ConfiguracaoEventoCalculoIPTUControlador() {
        super(ConfiguracaoEventoIPTU.class);
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public List<EventoCalculado> getCalculados() {
        return calculados;
    }

    public void setCalculados(List<EventoCalculado> calculados) {
        this.calculados = calculados;
    }

    @URLAction(mappingId = "novoConfiguracaoEventoCalculoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        preparaListaDeEventosDisponiveis();
    }

    @URLAction(mappingId = "verConfiguracaoEventoCalculoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarConfiguracaoEventoCalculoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        preparaListaDeEventosDisponiveis();
    }

    public void preparaListaDeEventosDisponiveis() {
        eventos = facade.getEventoCalculoFacade().listaEventosParaCalculo();
        Iterator<EventoCalculo> iterator = eventos.iterator();
        while (iterator.hasNext()) {
            EventoCalculo e = iterator.next();
            for (EventoConfiguradoIPTU eventoConfigurado : selecionado.getEventos()) {
                if (eventoConfigurado.getEventoCalculo().equals(e)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-de-calculo-de-iptu/";
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<EventoCalculo> getEventos() {
        return eventos;
    }

    public void addEvento(EventoCalculo evento) {
        EventoConfiguradoIPTU eventoConfigurado = new EventoConfiguradoIPTU();
        eventoConfigurado.setEventoCalculo(evento);
        eventoConfigurado.setConfiguracaoEventoIPTU(selecionado);
        selecionado.getEventos().add(eventoConfigurado);
        eventos.remove(evento);

    }

    public void removeEvento(EventoConfiguradoIPTU evento) {
        selecionado.getEventos().remove(evento);
        eventos.add(evento.getEventoCalculo());
    }

    public boolean temCalculoParaEvento(EventoCalculo evento) {
        return facade.getEventoCalculoFacade().getTemCalculoParaEvento(evento);
    }

    public List<SelectItem> getTiposCalculo() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(Calculo.TipoCalculo.IPTU, Calculo.TipoCalculo.IPTU.getDescricao()));
        return toReturn;
    }

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(TipoCadastroTributario.IMOBILIARIO, TipoCadastroTributario.IMOBILIARIO.getDescricao()));
        return toReturn;
    }

    private void validarExercicios() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getExercicioInicial() != null && selecionado.getExercicioFinal() != null) {
            if (selecionado.getExercicioInicial().getAno().compareTo(selecionado.getExercicioFinal().getAno()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Exercício Final não pode ser menor que o Exercício Inicial!");
            }
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarExercicios();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarSimulacao() {
        ValidacaoException ve = new ValidacaoException();
        if (cadastro == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Cadastro deve ser informado.");
        }
        ve.lancarException();
    }

    public void simular() {
        try {
            validarSimulacao();
            calculados = Lists.newArrayList();
            ProcessoCalculoIPTU processo = new ProcessoCalculoIPTU();
            processo.setConfiguracaoEventoIPTU(selecionado);
            processo.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            processo.setExercicio(sistemaFacade.getExercicioCorrente());
            processo.setDataLancamento(new Date());

            AssistenteCalculadorIPTU assistente = new AssistenteCalculadorIPTU(processo, 1);
            assistente.setPersisteCalculo(false);
            CalculadorIPTU calculador = new CalculadorIPTU();
            calculador.calcularIPTU(Lists.newArrayList((CadastroImobiliario) cadastro), assistente, null);

            if (!processo.getCalculosIPTU().isEmpty()) {
                calculados = processo.getCalculosIPTU().get(0).getMemorias()
                    .stream()
                    .map(m -> new EventoCalculado(m.getEvento(), m.getValor()))
                    .collect(Collectors.toList());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

}
