package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoEventoCalculoIPTUFacade;
import br.com.webpublico.negocios.EventoCalculoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.auxiliares.AssistenteCalculadorIPTU;
import br.com.webpublico.negocios.tributario.auxiliares.CalculadorIPTU;
import br.com.webpublico.negocios.tributario.auxiliares.EventoCalculado;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.script.ScriptEngine;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean(name = "eventoCalculoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoEventoCalculo", pattern = "/evento-de-calculo/novo/",
        viewId = "/faces/tributario/configuracao/eventos/eventocalculo/edita.xhtml"),
    @URLMapping(id = "editarEventoCalculo", pattern = "/evento-de-calculo/editar/#{eventoCalculoControlador.id}/",
        viewId = "/faces/tributario/configuracao/eventos/eventocalculo/edita.xhtml"),
    @URLMapping(id = "listarEventoCalculo", pattern = "/evento-de-calculo/listar/",
        viewId = "/faces/tributario/configuracao/eventos/eventocalculo/lista.xhtml"),
    @URLMapping(id = "verEventoCalculo", pattern = "/evento-de-calculo/ver/#{eventoCalculoControlador.id}/",
        viewId = "/faces/tributario/configuracao/eventos/eventocalculo/visualizar.xhtml")})
public class EventoCalculoControlador extends PrettyControlador<EventoCalculo> implements Serializable, CRUD {


    ScriptEngine engine;
    @EJB
    private EventoCalculoFacade eventoCalculoFacade;
    @EJB
    private ConfiguracaoEventoCalculoIPTUFacade configuracaoEventoCalculoIPTUFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private ConfiguracaoEventoIPTU configuracaoEventoIPTU;
    private String inicio, fim;
    private ConverterAutoComplete converterTributo;
    private ConverterGenerico converterEvento;
    private Boolean temCalculoParaEsseEvento;
    private boolean editavel;
    private Calculo.TipoCalculo tipoCalculo;
    private TipoCadastroTributario tipoCadastroTributario;
    private Cadastro cadastro;
    private List<EventoCalculado> calculados;

    public EventoCalculoControlador() {
        super(EventoCalculo.class);
    }

    public ConfiguracaoEventoIPTU getConfiguracaoEventoIPTU() {
        return configuracaoEventoIPTU;
    }

    public void setConfiguracaoEventoIPTU(ConfiguracaoEventoIPTU configuracaoEventoIPTU) {
        this.configuracaoEventoIPTU = configuracaoEventoIPTU;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public String getFim() {
        return fim;
    }

    public void setFim(String fim) {
        this.fim = fim;
    }

    public ConverterGenerico getConverterEvento() {
        if (converterEvento == null) {
            converterEvento = new ConverterGenerico(EventoCalculo.class, eventoCalculoFacade);
        }
        return converterEvento;
    }

    public void setConverterEvento(ConverterGenerico converterEvento) {
        this.converterEvento = converterEvento;
    }

    public boolean isEditavel() {
        return editavel;
    }

    public Calculo.TipoCalculo getTipoCalculo() {
        return tipoCalculo;
    }

    public void setTipoCalculo(Calculo.TipoCalculo tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
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

    @URLAction(mappingId = "novoEventoCalculo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();

    }

    @URLAction(mappingId = "verEventoCalculo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.setSessao(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("sessao"));
        operacao = Operacoes.VER;
        selecionado = (EventoCalculo) Web.pegaDaSessao(metadata.getEntidade());
        if (selecionado == null) {
            selecionado = (EventoCalculo) getFacede().recuperar(getId());
        }
        editavel = !(!getVigente());
    }

    @URLAction(mappingId = "editarEventoCalculo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        ver();
        operacao = Operacoes.EDITAR;
        if (!getVigente()) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + getId());
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/evento-de-calculo/";
    }

    @Override
    public AbstractFacade getFacede() {
        return eventoCalculoFacade;
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List getTiposLancamentos() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (EventoCalculo.TipoLancamento tipo : EventoCalculo.TipoLancamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List getEventos() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (EventoCalculo evento : eventoCalculoFacade.lista()) {
            retorno.add(new SelectItem(evento, evento.getDescricao()));
        }
        return retorno;
    }

    public List<Tributo> completaTribtuos(String parte) {
        return eventoCalculoFacade.getTributoFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterTributo() {
        if (converterTributo == null) {
            converterTributo = new ConverterAutoComplete(Tributo.class, eventoCalculoFacade.getTributoFacade());
        }
        return converterTributo;
    }

    public Boolean getVigente() {
        return selecionado.getFinalVigencia() == null || selecionado.getFinalVigencia().after(new Date());
    }

    public Boolean getTemEsseEventoEmAlgumCalculo() {
        if (temCalculoParaEsseEvento == null) {
            temCalculoParaEsseEvento = eventoCalculoFacade.getTemCalculoParaEvento(selecionado);
        }
        return temCalculoParaEsseEvento;
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

    public boolean validaCampos() {
        boolean valida = true;
        if (selecionado.getIdentificacao() == null || selecionado.getIdentificacao().trim().isEmpty()) {
            FacesUtil.addError("Campo Obrigatório", "Informe um valor para o campo identificação");
            valida = false;
        }
        if (selecionado.getSigla() == null || selecionado.getSigla().trim().isEmpty()) {
            FacesUtil.addError("Campo Obrigatório", "Informe um valor para o campo sigla");
            valida = false;
        }
        if (selecionado.getDescricao() == null || selecionado.getDescricao().trim().isEmpty()) {
            FacesUtil.addError("Campo Obrigatório", "Informe um valor para o campo descrição");
            valida = false;
        }
        if (selecionado.getRegra() == null || selecionado.getRegra().trim().isEmpty()) {
            FacesUtil.addError("Campo Obrigatório", "Informe uma regra para este evento");
            valida = false;
        }

        if (selecionado.getTipoLancamento() == null) {
            FacesUtil.addError("Campo Obrigatório", "Selecione um valor para o campo tipo de lançamento");
            valida = false;
        } else if (selecionado.getTipoLancamento().equals(EventoCalculo.TipoLancamento.DESCONTO) && selecionado.getDescontoSobre() == null) {
            FacesUtil.addError("Campo Obrigatório", "Para eventos do tipo DESCONTO você precisa informar qual evento receberá o desconto no campo 'Desconto Sobre'");
            valida = false;
        } else if ((selecionado.getTipoLancamento().equals(EventoCalculo.TipoLancamento.IMPOSTO) || selecionado.getTipoLancamento().equals(EventoCalculo.TipoLancamento.TAXA)) && selecionado.getTributo() == null) {
            FacesUtil.addError("Campo Obrigatório", "Para eventos do tipo IMPOSTO ou TAXA você precisa selecionar um tributo no campo 'Tributo para o Lançamento'");
            valida = false;
        }

        if (selecionado.getInicioVigencia() == null) {
            FacesUtil.addError("Campo Obrigatório", "Informe uma data para o campo inicio de vigência");
            valida = false;
        }

        if ((selecionado.getIdentificacao() != null && !selecionado.getIdentificacao().trim().isEmpty()) && (selecionado.getInicioVigencia() != null)) {
            EventoCalculo e = eventoCalculoFacade.validaVigencia(selecionado);
            if (e != null) {
                FacesUtil.addError("Operação não Permitida", "Já existem um evento com o mesmo identificador informado e que a vigência entra em conflito com a vigência informada.");
                valida = false;
            }
        }

        return valida;
    }

    public void copiaRegistro() {
        EventoCalculo novo = new EventoCalculo();
        novo.setIdentificacao(selecionado.getIdentificacao());
        novo.setRegra(selecionado.getRegra());
        novo.setDescontoSobre(selecionado.getDescontoSobre());
        novo.setDescricao(selecionado.getDescricao());
        novo.setInicioVigencia(new Date());
        novo.setTipoLancamento(selecionado.getTipoLancamento());
        novo.setTributo(selecionado.getTributo());
        EventoCalculo outro = eventoCalculoFacade.validaVigencia(selecionado);
        if (outro == null) {
            Web.poeNaSessao(novo);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "novo/?sessao=true");
        } else {
            FacesUtil.addError("Operação não Permitida", "Já existe um outro evento vigente com o mesmo identificador.");
        }
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

    public void selecionarTipoCadastro() {
        this.cadastro = null;
    }

    public void validarSimulacao() {
        ValidacaoException ve = new ValidacaoException();
        if (tipoCalculo == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Cálculo deve ser informado.");
        }
        if (tipoCadastroTributario == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Cadastro deve ser informado.");
        }
        if (cadastro == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Cadastro deve ser informado.");
        }
        ve.lancarException();
    }

    public void simular() {
        try {
            validarSimulacao();
            switch (tipoCalculo) {
                case IPTU: {
                    calculados = simularCalculoIPTU();
                    break;
                }
                default:
                    FacesUtil.addAtencao("Simulação de cálculo não implementada.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    private List<EventoCalculado> simularCalculoIPTU() {
        ProcessoCalculoIPTU processo = new ProcessoCalculoIPTU();
        processo.setConfiguracaoEventoIPTU(criarConfiguracaoEventoIPTU());
        processo.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        processo.setExercicio(sistemaFacade.getExercicioCorrente());
        processo.setDataLancamento(new Date());

        AssistenteCalculadorIPTU assistente = new AssistenteCalculadorIPTU(processo, 1);
        assistente.setPersisteCalculo(false);
        CalculadorIPTU calculador = new CalculadorIPTU();
        calculador.calcularIPTU(Lists.newArrayList((CadastroImobiliario) cadastro), assistente, null);

        if (!processo.getCalculosIPTU().isEmpty()) {
            return processo.getCalculosIPTU().get(0).getMemorias()
                .stream()
                .map(m -> new EventoCalculado(m.getEvento(), m.getValor()))
                .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    private ConfiguracaoEventoIPTU criarConfiguracaoEventoIPTU() {
        ConfiguracaoEventoIPTU configuracaoEventoIPTU = new ConfiguracaoEventoIPTU();
        EventoConfiguradoIPTU eventoConfiguradoIPTU = new EventoConfiguradoIPTU();
        eventoConfiguradoIPTU.setConfiguracaoEventoIPTU(configuracaoEventoIPTU);
        eventoConfiguradoIPTU.setEventoCalculo(selecionado);
        configuracaoEventoIPTU.getEventos().add(eventoConfiguradoIPTU);
        return configuracaoEventoIPTU;
    }

    public void alterouTipoLancamento() {
        selecionado.setEventoImunidade(null);
    }
}
