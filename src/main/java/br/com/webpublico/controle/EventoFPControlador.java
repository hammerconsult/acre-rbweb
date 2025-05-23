/*
 * Codigo gerado automaticamente em Wed Jun 29 14:01:43 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.administracaodepagamento.BloqueioFuncionalidade;
import br.com.webpublico.entidades.rh.administracaodepagamento.EventoFPTipoFolha;
import br.com.webpublico.entidades.rh.esocial.*;
import br.com.webpublico.entidadesauxiliares.ResultadoSimulacao;
import br.com.webpublico.entidadesauxiliares.rh.FiltrosFuncoesFolha;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.TipoClassificacaoConsignacao;
import br.com.webpublico.enums.rh.TipoFuncionalidadeRH;
import br.com.webpublico.enums.rh.TipoNaturezaRefenciaCalculo;
import br.com.webpublico.enums.rh.previdencia.TipoContribuicaoBBPrev;
import br.com.webpublico.esocial.dto.OcorrenciaESocialDTO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ExecutaScript;
import br.com.webpublico.interfaces.NomeObjetosConstantesScriptFP;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.script.ExecutaScriptImpl;
import br.com.webpublico.script.rh.ExecutaScriptEventoFPImpl;
import br.com.webpublico.script.rh.GeraBibliotecaJavaScriptFP;
import br.com.webpublico.util.*;
import br.com.webpublico.util.anotacoes.DescricaoMetodo;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ManagedBean(name = "eventoFPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoEventoFP", pattern = "/eventofp/novo/", viewId = "/faces/rh/administracaodepagamento/eventofp/edita.xhtml"),
    @URLMapping(id = "editarEventoFP", pattern = "/eventofp/editar/#{eventoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/eventofp/edita.xhtml"),
    @URLMapping(id = "editarCodigoEventoFP", pattern = "/eventofp/codigo/#{eventoFPControlador.codigo}/", viewId = "/faces/rh/administracaodepagamento/eventofp/edita.xhtml"),
    @URLMapping(id = "listarEventoFP", pattern = "/eventofp/listar/", viewId = "/faces/rh/administracaodepagamento/eventofp/lista.xhtml"),
    @URLMapping(id = "verEventoFP", pattern = "/eventofp/ver/#{eventoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/eventofp/visualizar.xhtml"),

    @URLMapping(id = "duplicarEventoFP", pattern = "/eventofp/duplicar/#{eventoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/eventofp/edita.xhtml"),

})
public class EventoFPControlador extends PrettyControlador<EventoFP> implements Serializable, CRUD {

    private Logger logger = LoggerFactory.getLogger(EventoFPControlador.class);

    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private TipoBaseFPFacade tipoBaseFPFacade;
    @EJB
    private ReferenciaFPFacade referenciaFPFacade;
    private String metodoRegraSelecionado;
    private String metodoFormulaSelecionado;
    private String metodoFormulaValorIntegralSelecionado;
    private String metodoReferenciaSelecionado;
    private String metodoValorBaseDeCalculoSelecionado;
    private List<String> listaMetodosRegra;
    private List<String> listaMetodosFormula;
    private List<String> listaMetodosFormulaValorIntegral;
    private List<String> listaMetodosReferencia;
    private List<String> listaMetodosValorBaseDeCalculo;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    private VinculoFP vinculoFP;
    private ConverterAutoComplete converterVinculoFP;
    private GeraBibliotecaJavaScriptFP geraBibliotecaJavaScript;
    @EJB
    private FormulaPadraoFPFacade formulaPadraoFPFacade;
    @EJB
    private BaseFPFacade baseFacade;
    @EJB
    private FuncoesFolhaFacade funcoesFolhaFacade;
    private EventoFP eventoFP;
    private ResultadoSimulacao resultadoSimulacao;
    private TipoExecucaoEP tipoExecucaoEP = TipoExecucaoEP.FOLHA;
    private ItemBaseFP itemBaseFP;
    private List<ItemBaseFP> listaItemBaseFP;
    @EJB
    private ItemBaseFPFacade itemBaseFPFacade;
    //private ConverterGenerico converterItemBaseFP;
    private ConverterGenerico converterBaseFP;
    @EJB
    private BaseFPFacade baseFPFacade;
    private MoneyConverter moneyConverter;
    private ConverterAutoComplete converterTipoBaseFP;
    private ConverterAutoComplete converterRefenciaFP;
    private String codigo;
    private Converter converterEventoFP;
    private EventoFPTipoFolha eventoFPTipoFolha;
    private BloqueioFuncionalidade bloqueioFuncionalidade;
    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;

    private ConverterAutoComplete converterHierarquia;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private EventoFPUnidade eventoFPUnidade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial;
    private List<OcorrenciaESocialDTO> ocorrencias;
    private EventoFPEmpregador eventoFPEmpregador;
    private List<ConfiguracaoEmpregadorESocial> empregadorSemConfiguracao;
    private List<ConfiguracaoEmpregadorESocial> novosEmpregadoresConfigurados;

    private List<EventoFPEmpregador> eventoFPEmpregadorList;

    private EventoFPEmpregador eventoFPEmpregadorSelecionado;

    private FiltrosFuncoesFolha filtrosFuncoesFolha;


    public EventoFPControlador() {
        super(EventoFP.class);
        ocorrencias = Lists.newArrayList();
        eventoFPEmpregador = new EventoFPEmpregador();
        empregadorSemConfiguracao = Lists.newArrayList();
        novosEmpregadoresConfigurados = Lists.newArrayList();
        eventoFPEmpregadorSelecionado = new EventoFPEmpregador();
    }

    private void getEventoPorIdentificador() {
        selecionado.setEventosEsocial(eventoFPFacade.getEventoPorIdentificador(selecionado.getId()));
    }

    public List<OcorrenciaESocialDTO> getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(Set<OcorrenciaESocialDTO> ocorrencias) {
        this.ocorrencias = Lists.newArrayList(ocorrencias);
    }

    public EventoFPEmpregador getEventoFPEmpregadorSelecionado() {
        return eventoFPEmpregadorSelecionado;
    }

    public void setEventoFPEmpregadorSelecionado(EventoFPEmpregador eventoFPEmpregadorSelecionado) {
        this.eventoFPEmpregadorSelecionado = eventoFPEmpregadorSelecionado;
    }

    public List<ConfiguracaoEmpregadorESocial> getEmpregadorSemConfiguracao() {
        return empregadorSemConfiguracao;
    }

    public void setEmpregadorSemConfiguracao(List<ConfiguracaoEmpregadorESocial> empregadorSemConfiguracao) {
        this.empregadorSemConfiguracao = empregadorSemConfiguracao;
    }

    public List<ConfiguracaoEmpregadorESocial> getNovosEmpregadoresConfigurados() {
        return novosEmpregadoresConfigurados;
    }

    public void setNovosEmpregadoresConfigurados(List<ConfiguracaoEmpregadorESocial> novosEmpregadoresConfigurados) {
        this.novosEmpregadoresConfigurados = novosEmpregadoresConfigurados;
    }

    public EventoFPEmpregador getEventoFPEmpregador() {
        return eventoFPEmpregador;
    }

    public void setEventoFPEmpregador(EventoFPEmpregador eventoFPEmpregador) {
        this.eventoFPEmpregador = eventoFPEmpregador;
    }

    public ResultadoSimulacao getResultadoSimulacao() {
        return resultadoSimulacao;
    }

    public EventoFPUnidade getEventoFPUnidade() {
        return eventoFPUnidade;
    }

    public void setEventoFPUnidade(EventoFPUnidade eventoFPUnidade) {
        this.eventoFPUnidade = eventoFPUnidade;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Converter getConverterHierarquia() {
        if (converterHierarquia == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquia;
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (tipoExecucaoEP != null) {
                selecionado.setTipoExecucaoEP(tipoExecucaoEP);
            }
            if (Operacoes.NOVO.equals(operacao)) {
                selecionado.setDataRegistro(UtilRH.getDataOperacao());
            } else {
                selecionado.setDataAlteracao(UtilRH.getDataOperacao());
            }
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @Override
    public EventoFPFacade getFacede() {
        return eventoFPFacade;
    }

    public EventoFPTipoFolha getEventoFPTipoFolha() {
        return eventoFPTipoFolha;
    }

    public void setEventoFPTipoFolha(EventoFPTipoFolha eventoFPTipoFolha) {
        this.eventoFPTipoFolha = eventoFPTipoFolha;
    }

    public BloqueioFuncionalidade getBloqueioFuncionalidade() {
        return bloqueioFuncionalidade;
    }

    public void setBloqueioFuncionalidade(BloqueioFuncionalidade bloqueioFuncionalidade) {
        this.bloqueioFuncionalidade = bloqueioFuncionalidade;
    }

    private List<String> filtrarListaString(List<String> listaString, String parte) {
        return listaString.stream().filter(metodo -> metodo.toLowerCase().contains(parte.trim().toLowerCase())).collect(Collectors.toList());
    }

    public List<String> getListaMetodosRegra() {
        return listaMetodosRegra;
    }

    public void atualizarListaFiltradaMetodosRegra() {
        filtrosFuncoesFolha.setListaFiltradaMetodosRegra(filtrarListaString(listaMetodosRegra, filtrosFuncoesFolha.getParteFuncaoRegra()));
    }

    public void setListaMetodosRegra(List<String> listaMetodosRegra) {
        this.listaMetodosRegra = listaMetodosRegra;
    }

    public String getMetodoFormulaSelecionado() {
        return metodoFormulaSelecionado;
    }

    public void setMetodoFormulaSelecionado(String metodoFormulaSelecionado) {
        this.metodoFormulaSelecionado = metodoFormulaSelecionado;
    }

    public String getMetodoRegraSelecionado() {
        return metodoRegraSelecionado;
    }

    public void setMetodoRegraSelecionado(String metodoRegraSelecionado) {
        this.metodoRegraSelecionado = metodoRegraSelecionado;
    }

    public String getMetodoFormulaValorIntegralSelecionado() {
        return metodoFormulaValorIntegralSelecionado;
    }

    public void setMetodoFormulaValorIntegralSelecionado(String metodoFormulaValorIntegralSelecionado) {
        this.metodoFormulaValorIntegralSelecionado = metodoFormulaValorIntegralSelecionado;
    }

    public String getMetodoReferenciaSelecionado() {
        return metodoReferenciaSelecionado;
    }

    public void setMetodoReferenciaSelecionado(String metodoReferenciaSelecionado) {
        this.metodoReferenciaSelecionado = metodoReferenciaSelecionado;
    }

    public List<String> getListaMetodosFormula() {
        return listaMetodosFormula;
    }

    public void atualizarListaFiltradaMetodosFormula() {
        filtrosFuncoesFolha.setListaFiltradaMetodosFormula(filtrarListaString(listaMetodosFormula, filtrosFuncoesFolha.getParteFuncaoFormula()));
    }

    public void setListaMetodosFormula(List<String> listaMetodosFormula) {
        this.listaMetodosFormula = listaMetodosFormula;
    }

    public List<String> getListaMetodosFormulaValorIntegral() {
        return listaMetodosFormulaValorIntegral;
    }

    public void atualizarListaFiltradaMetodosFormulaValorIntegral() {
        filtrosFuncoesFolha.setListaFiltradaMetodosFormulaValorIntegral(filtrarListaString(listaMetodosFormulaValorIntegral, filtrosFuncoesFolha.getParteFuncaoFormulaValorIntegral()));
    }

    public void setListaMetodosFormulaValorIntegral(List<String> listaMetodosFormulaValorIntegral) {
        this.listaMetodosFormulaValorIntegral = listaMetodosFormulaValorIntegral;
    }

    public List<String> getListaMetodosReferencia() {
        return listaMetodosReferencia;
    }
    public void atualizarListaFiltradaMetodosReferencia() {
        filtrosFuncoesFolha.setListaFiltradaMetodosReferencia(filtrarListaString(listaMetodosReferencia, filtrosFuncoesFolha.getParteFuncaoReferencia()));
    }

    public void setListaMetodosReferencia(List<String> listaMetodosReferencia) {
        this.listaMetodosReferencia = listaMetodosReferencia;
    }

    public List<String> getListaMetodosValorBaseDeCalculo() {
        return listaMetodosValorBaseDeCalculo;
    }

    public void atualizarListaFiltradaMetodosValorBaseDeCalculo() {
        filtrosFuncoesFolha.setListaFiltradaMetodosValorBaseDeCalculo(filtrarListaString(listaMetodosValorBaseDeCalculo, filtrosFuncoesFolha.getParteFuncaoValorBaseCalculo()));
    }

    public void setListaMetodosValorBaseDeCalculo(List<String> listaMetodosValorBaseDeCalculo) {
        this.listaMetodosValorBaseDeCalculo = listaMetodosValorBaseDeCalculo;
    }

    public String getMetodoValorBaseDeCalculoSelecionado() {
        return metodoValorBaseDeCalculoSelecionado;
    }

    public void setMetodoValorBaseDeCalculoSelecionado(String metodoValorBaseDeCalculoSelecionado) {
        this.metodoValorBaseDeCalculoSelecionado = metodoValorBaseDeCalculoSelecionado;
    }

    public List<SelectItem> getTipoEventos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(""));
        for (TipoEventoFP obj : TipoEventoFP.values()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public List<SelectItem> getTipoCalculo13() {
        List<SelectItem> toReturn = new ArrayList<>();
//        toReturn.add(new SelectItem(""));
        for (TipoCalculo13 obj : TipoCalculo13.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoDeConsignacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoDeConsignacao obj : TipoDeConsignacao.values()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoBases() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoBase obj : TipoBase.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getBloqueioFerias() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (BloqueioFerias obj : BloqueioFerias.values()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> tiposExecucao() {
        return Util.getListSelectItem(TipoExecucaoEP.values());
    }

    public Boolean validaCodigoAlfaNumerico(String codigo) {
        return codigo.matches("[a-zA-Z0-9]*");
    }


    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        String codigo = selecionado.getCodigo();
        EventoFP evento = selecionado;
        if (!validaCodigoAlfaNumerico(codigo)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Código deve ser Alfanumérico(O Código deve possuir apenas letras e números)");
        }
        if (codigo != null && operacao == Operacoes.NOVO) {
            if (eventoFPFacade.verificaCodigoSalvo(codigo)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um código com este número.");
            }
        }
        if (codigo != null && operacao == Operacoes.EDITAR) {
            if (!eventoFPFacade.verificaCodigoEditar(selecionado) && eventoFPFacade.verificaCodigoSalvo(codigo)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Edição não permitida, já existe um código com este número.");
            }
        }
        if (!evento.getFormula().toLowerCase().contains("return") || !evento.getFormula().toLowerCase().contains(";")) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A formula dos eventos não contem as palavras reservadas como return e ponto e vírgula(;)");
        }
        if (!evento.getRegra().toLowerCase().contains("return") || !evento.getRegra().toLowerCase().contains(";")) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A regra dos eventos não contem as palavras reservadas como return e ponto e vírgula(;)");
        }
        if (!evento.getReferencia().toLowerCase().contains("return") || !evento.getReferencia().toLowerCase().contains(";")) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A referência dos eventos não contem as palavras reservadas como return e ponto e vírgula(;)");
        }
        if (!evento.getValorBaseDeCalculo().toLowerCase().contains("return") || !evento.getValorBaseDeCalculo().toLowerCase().contains(";")) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A valor base de calculo dos eventos não contem as palavras reservadas como return e ponto e vírgula(;)");
        }
        if (!evento.getFormulaValorIntegral().toLowerCase().contains("return") || !evento.getFormulaValorIntegral().toLowerCase().contains(";")) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A formula valor integral dos eventos não contem as palavras reservadas como return e ponto e vírgula(;)");
        }
        if (!TipoEventoFP.INFORMATIVO.equals(evento.getTipoEventoFP()) && (evento.getItensEventoFPEmpregador() == null || evento.getItensEventoFPEmpregador().isEmpty())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Ao menos um empregador do E-Social deve ser registrado para a verba.");
        }
        if (evento.getClassificacaoVerba() == null && !evento.getNaoEnviarVerbaSicap()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Classificação de Verba, na guia SICAP, deve ser informado");
        }
        ve.lancarException();
    }

    public List<String> buscaMetodosFuncoesFolhaFacade() {
        Class classe = null;
        try {
            classe = Class.forName("br.com.webpublico.negocios.FolhaDePagamentoNovoCalculador");
        } catch (ClassNotFoundException ex) {
            logger.error("Erro: ", ex);
        }

        Method[] metodos = classe.getDeclaredMethods();
        List<String> listaNomes = new ArrayList<>();

        for (int i = 0; i < metodos.length; i++) {

            if (metodos[i].isAnnotationPresent(DescricaoMetodo.class)) {
                DescricaoMetodo anotacao = metodos[i].getAnnotation(DescricaoMetodo.class);
                String descricao = anotacao.value();
                listaNomes.add(NomeObjetosConstantesScriptFP.NOME_OBJETO_JAVA.toString() + "." + descricao);
            }

//            String descricao = metodos[i].getName() + "(";
//
//            Class[] parametro = metodos[i].getParameterTypes();
//
//            if (parametro.length != 0) {
//                for (int x = 0; x < parametro.length; x++) {
//                    descricao += parametro[x].getSimpleName() + ",";
//                }
//                StringBuilder builder = new StringBuilder(descricao);
//                builder.replace(descricao.length() - 1, descricao.length(), "");
//                descricao = builder.toString();
//            }
//
//            descricao += ")";
//            lista.add(descricao);
        }

        Collections.sort(listaNomes, new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                String p1 = (String) o1;
                String p2 = (String) o2;

                return p1.toString().compareTo(p2.toString());
            }
        });
        listaNomes.addAll(buscaMetodosFuncoesFichaRPA());
        return listaNomes;
    }

    public List<String> buscaMetodosFuncoesFichaRPA() {
        Class classe = null;
        try {
            classe = Class.forName("br.com.webpublico.negocios.FichaRPACalculador");
        } catch (ClassNotFoundException ex) {
            logger.error("Erro: ", ex);
        }

        Method[] metodos = classe.getDeclaredMethods();
        List<String> listaNomes = new ArrayList<>();

        for (int i = 0; i < metodos.length; i++) {

            if (metodos[i].isAnnotationPresent(DescricaoMetodo.class)) {
                DescricaoMetodo anotacao = metodos[i].getAnnotation(DescricaoMetodo.class);
                String descricao = anotacao.value();
                listaNomes.add(NomeObjetosConstantesScriptFP.NOME_OBJETO_JAVA_FICHA_RPA.toString() + "." + descricao);
            }
        }

        Collections.sort(listaNomes, new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                String p1 = (String) o1;
                String p2 = (String) o2;

                return p1.toString().compareTo(p2.toString());
            }
        });

        return listaNomes;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/eventofp/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoEventoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        listaMetodosRegra = buscaMetodosFuncoesFolhaFacade();
        listaMetodosFormula = buscaMetodosFuncoesFolhaFacade();
        listaMetodosFormulaValorIntegral = buscaMetodosFuncoesFolhaFacade();
        listaMetodosReferencia = buscaMetodosFuncoesFolhaFacade();
        listaMetodosValorBaseDeCalculo = buscaMetodosFuncoesFolhaFacade();
        geraBibliotecaJavaScript = new GeraBibliotecaJavaScriptFP();
        eventoFPUnidade = new EventoFPUnidade();

        EventoFP efp = ((EventoFP) selecionado);
        //referente ao sub-cadastro dentro da propria tela
        listaItemBaseFP = new ArrayList<>();
        efp.setItensBasesFPs(listaItemBaseFP);
        itemBaseFP = new ItemBaseFP();

        resultadoSimulacao = new ResultadoSimulacao();
        eventoFPTipoFolha = new EventoFPTipoFolha();
        bloqueioFuncionalidade = new BloqueioFuncionalidade();
        filtrosFuncoesFolha = new FiltrosFuncoesFolha(listaMetodosRegra, listaMetodosFormula, listaMetodosFormulaValorIntegral, listaMetodosReferencia, listaMetodosValorBaseDeCalculo);
        selecionado.setExibirNaFichaRPA(Boolean.TRUE);
    }

    @URLAction(mappingId = "editarEventoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();    //To change body of overridden methods use File | Settings | File Templates.
        inicializarEdicao();
        getEventoPorIdentificador();
    }

    @URLAction(mappingId = "editarCodigoEventoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarCodigo() {
        EventoFP e = eventoFPFacade.recuperaPorCodigo(codigo);
        if (e != null) {
            selecionado = e;
            setId(selecionado.getId());
            editar();
        }
    }

    @URLAction(mappingId = "duplicarEventoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void duplicar() {
        editar();
        EventoFP novoObjeto = new EventoFP();
        operacao = Operacoes.NOVO;
        novoObjeto.setCodigo(eventoFPFacade.getSingletonGeradorCodigo().getProximoCodigo(EventoFP.class, "codigo").toString());
        novoObjeto.setFormula(selecionado.getFormula());
        novoObjeto.setFormulaValorIntegral(selecionado.getFormulaValorIntegral());
        novoObjeto.setReferencia(selecionado.getReferencia());
        novoObjeto.setRegra(selecionado.getRegra());
        novoObjeto.setTipoPrevidenciaFP(selecionado.getTipoPrevidenciaFP());
        novoObjeto.setEventoFPAgrupador(selecionado.getEventoFPAgrupador());
        novoObjeto.setValorBaseDeCalculo(selecionado.getValorBaseDeCalculo());
        novoObjeto.setDataRegistro(new Date());
        novoObjeto.setPropMesesTrabalhados(selecionado.getPropMesesTrabalhados());
        novoObjeto.setTipoExecucaoEP(selecionado.getTipoExecucaoEP());
        novoObjeto.setArredondarValor(selecionado.getArredondarValor());
        novoObjeto.setAutomatico(selecionado.getAutomatico());
        novoObjeto.setAtivo(selecionado.getAtivo());
        novoObjeto.setCalculoRetroativo(selecionado.getCalculoRetroativo());
        novoObjeto.setComplementoReferencia(selecionado.getComplementoReferencia());
        novoObjeto.setDescricao(selecionado.getDescricao());
        novoObjeto.setDescricaoReduzida(selecionado.getDescricaoReduzida());
        novoObjeto.setTipoEventoFP(selecionado.getTipoEventoFP());
        novoObjeto.setUnidadeReferencia(selecionado.getUnidadeReferencia());
        novoObjeto.setTipoBase(selecionado.getTipoBase());
        novoObjeto.setQuantificacao(selecionado.getQuantificacao());
        novoObjeto.setTipoLancamentoFP(selecionado.getTipoLancamentoFP());
        novoObjeto.setVerbaFixa(selecionado.getVerbaFixa());
        novoObjeto.setNaoPermiteLancamento(selecionado.getNaoPermiteLancamento());
        novoObjeto.setTipoCalculo13(selecionado.getTipoCalculo13());
        novoObjeto.setTipoBaseFP(selecionado.getTipoBaseFP());
        novoObjeto.setReferenciaFP(selecionado.getReferenciaFP());
        novoObjeto.setEstornoFerias(selecionado.getEstornoFerias());
        novoObjeto.setOrdemProcessamento(selecionado.getOrdemProcessamento());
        novoObjeto.setConsignacao(selecionado.getConsignacao());
        novoObjeto.setTipoDeConsignacao(selecionado.getTipoDeConsignacao());
        novoObjeto.setProporcionalizaDiasTrab(selecionado.getProporcionalizaDiasTrab());
        novoObjeto.setValorMaximoLancamento(selecionado.getValorMaximoLancamento());
        novoObjeto.setIdentificacaoEventoFP(selecionado.getIdentificacaoEventoFP());
        novoObjeto.setItensBasesFPs(new ArrayList<ItemBaseFP>());
        copiarItensBaseFP(novoObjeto);
        novoObjeto.setBaseFPs(new ArrayList<BaseFP>());
        novoObjeto.setTiposFolha(new ArrayList<EventoFPTipoFolha>());
        copiarTipoFolha(novoObjeto);
        novoObjeto.setBloqueiosFuncionalidade(new ArrayList<BloqueioFuncionalidade>());
        copiarBloqueioFuncionalidade(novoObjeto);
        selecionado = novoObjeto;
    }

    private void copiarItensBaseFP(EventoFP eventoFP) {
        List<ItemBaseFP> itens = Lists.newArrayList();
        for (ItemBaseFP itemBaseFP : selecionado.getItensBasesFPs()) {
            ItemBaseFP item = new ItemBaseFP();
            item.setBaseFP(itemBaseFP.getBaseFP());
            item.setEventoFP(eventoFP);
            item.setOperacaoFormula(itemBaseFP.getOperacaoFormula());
            item.setDataRegistro(new Date());
            item.setTipoValor(itemBaseFP.getTipoValor());
            item.setSomaValorRetroativo(itemBaseFP.getSomaValorRetroativo());
            itens.add(item);
        }
        eventoFP.getItensBasesFPs().addAll(itens);
    }

    private void copiarTipoFolha(EventoFP eventoFP) {
        List<EventoFPTipoFolha> itens = Lists.newArrayList();
        for (EventoFPTipoFolha tipo : selecionado.getTiposFolha()) {
            EventoFPTipoFolha item = new EventoFPTipoFolha();
            item.setEventoFP(eventoFP);
            item.setTipoFolhaDePagamento(tipo.getTipoFolhaDePagamento());
            itens.add(item);
        }
        eventoFP.getTiposFolha().addAll(itens);
    }

    private void copiarBloqueioFuncionalidade(EventoFP eventoFP) {
        List<BloqueioFuncionalidade> itens = Lists.newArrayList();
        for (BloqueioFuncionalidade tipo : selecionado.getBloqueiosFuncionalidade()) {
            BloqueioFuncionalidade item = new BloqueioFuncionalidade();
            item.setEventoFP(eventoFP);
            item.setTipoFuncionalidadeRH(tipo.getTipoFuncionalidadeRH());
            itens.add(item);
        }
        eventoFP.getBloqueiosFuncionalidade().addAll(itens);
    }

    void inicializarEdicao() {
        listaMetodosRegra = buscaMetodosFuncoesFolhaFacade();
        listaMetodosFormula = buscaMetodosFuncoesFolhaFacade();
        listaMetodosFormulaValorIntegral = buscaMetodosFuncoesFolhaFacade();
        listaMetodosReferencia = buscaMetodosFuncoesFolhaFacade();
        listaMetodosValorBaseDeCalculo = buscaMetodosFuncoesFolhaFacade();
        geraBibliotecaJavaScript = new GeraBibliotecaJavaScriptFP();
        itemBaseFP = new ItemBaseFP();
        tipoExecucaoEP = ((EventoFP) selecionado).getTipoExecucaoEP();
        eventoFPTipoFolha = new EventoFPTipoFolha();
        bloqueioFuncionalidade = new BloqueioFuncionalidade();
        eventoFPUnidade = new EventoFPUnidade();
        Collections.sort(selecionado.getItensEventoFPEmpregador());
        filtrosFuncoesFolha = new FiltrosFuncoesFolha(listaMetodosRegra, listaMetodosFormula, listaMetodosFormulaValorIntegral, listaMetodosReferencia, listaMetodosValorBaseDeCalculo);
    }

    @URLAction(mappingId = "verEventoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();    //To change body of overridden methods use File | Settings | File Templates.
    }

//    @Override
//    public void selecionar(ActionEvent evento) {
//        //super.selecionar(evento);
//        operacao = Operacoes.EDITAR;
//        EventoFP even = (EventoFP) evento.getComponent().getAttributes().get("objeto");
//        selecionado = eventoFPFacade.recuperar(even.getId());
//
//    }

    public List<VinculoFP> completeVinculosFP(String parte) {
        return vinculoFPFacade.listaTodosVinculos1(parte.trim());
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public Converter getConverterVinculoFPs() {
        if (converterVinculoFP == null) {
            converterVinculoFP = new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
        }
        return converterVinculoFP;
    }

    public void testar() {
        //System.out.println("alright");
        if (vinculoFP != null) {
            List<VinculoFP> vinculos = new ArrayList<>();

            List<EventoFP> eventos = eventoFPFacade.lista();

            if (operacao == Operacoes.EDITAR) {
                eventos.remove(eventos.indexOf(((EventoFP) selecionado)));
            }
            vinculos.add(vinculoFP);
            eventos.add((EventoFP) selecionado);
            ScriptEngineManager manager = new ScriptEngineManager();
            String libJavaScript = geraBibliotecaJavaScript.gerar(eventos, formulaPadraoFPFacade.lista(), baseFacade.listaBaseFPs(), tipoExecucaoEP.FOLHA);
            ScriptEngine engine = manager.getEngineByName("JavaScript");

            try {
                engine.put(NomeObjetosConstantesScriptFP.NOME_OBJETO_JAVA, funcoesFolhaFacade);
                engine.eval(libJavaScript);
                Invocable jsInvoke = (Invocable) engine;
//                eventos.addAll(eventoFPFacade.listaEventosLancados(vinculoFP));
//                eventos.addAll(eventoFPFacade.listaEventosAutomaticos());

                //retirar o evento adicional... TODO so no caso do editar
                EventoFP evento = ((EventoFP) selecionado);
                ExecutaScript executaScript = new ExecutaScriptImpl(jsInvoke);
                ExecutaScriptEventoFPImpl executaScriptEventoFP = new ExecutaScriptEventoFPImpl(executaScript);


                resultadoSimulacao = new ResultadoSimulacao();
                resultadoSimulacao.setResultadoRegra(executaScriptEventoFP.executaRegra(evento, vinculoFP));
                resultadoSimulacao.setResultadoFormula(executaScriptEventoFP.executaFormula(evento, vinculoFP));
                resultadoSimulacao.setResultadoValorIntegral(executaScriptEventoFP.executaValorIntegral(evento, vinculoFP));
                resultadoSimulacao.setResultadoReferencia(executaScriptEventoFP.executaReferencia(evento, vinculoFP));
                resultadoSimulacao.setResultadoValorBaseDeCalculo(executaScriptEventoFP.executaValorBaseDeCalculo(evento, vinculoFP));

            } catch (Exception e) {
                FacesUtil.addOperacaoNaoRealizada(e.getMessage());
                logger.debug(e.getMessage());
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "ERRO", "Erro Regra:" + resultadoSimulacao.getResultadoRegra().getItemErroScript().getMensagem() + " - " + resultadoSimulacao.getResultadoRegra().getItemErroScript().getMensagemException()));
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "ERRO", "Erro Fórmula:" + resultadoSimulacao.getResultadoFormula().getItemErroScript().getMensagem() + " - " + resultadoSimulacao.getResultadoRegra().getItemErroScript().getMensagemException()));
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "ERRO", "Erro Valor Integral:" + resultadoSimulacao.getResultadoValorIntegral().getItemErroScript().getMensagem() + " - " + resultadoSimulacao.getResultadoRegra().getItemErroScript().getMensagemException()));
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "ERRO", "Erro Valor Referência:" + resultadoSimulacao.getResultadoReferencia().getItemErroScript().getMensagem() + " - " + resultadoSimulacao.getResultadoRegra().getItemErroScript().getMensagemException()));


            }
        }
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public List<SelectItem> getTipoExecucaoEPs() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoExecucaoEP obj : TipoExecucaoEP.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public TipoExecucaoEP getTipoExecucaoEP() {
        return tipoExecucaoEP;
    }

    public void setTipoExecucaoEP(TipoExecucaoEP tipoExecucaoEP) {
        this.tipoExecucaoEP = tipoExecucaoEP;
    }

    public List<SelectItem> getBaseFP() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (BaseFP object : baseFPFacade.listaBasesPorFiltroBase(FiltroBaseFP.NORMAL)) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterBaseFP() {
        if (converterBaseFP == null) {
            converterBaseFP = new ConverterGenerico(BaseFP.class, baseFPFacade);
        }
        return converterBaseFP;
    }

    public void addItemBaseFP() {
        for (ItemBaseFP item : ((EventoFP) selecionado).getItensBasesFPs()) {
            if (item.getBaseFP().equals(itemBaseFP.getBaseFP())) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Já existe um item semelhante ao qual está sendo cadastrado !", "Já existe um item semelhante ao qual está sendo cadastrado !");
                FacesContext.getCurrentInstance().addMessage("msgs", msg);
                return;
            }
        }

        EventoFP efp = (EventoFP) selecionado;
        itemBaseFP.setEventoFP(efp);
        itemBaseFP.setDataRegistro(new Date());
        if (itemBaseFP.getTipoValor() == null) {
            itemBaseFP.setTipoValor(TipoValor.NORMAL);
        }
        itemBaseFP.setBaseFP(baseFacade.recuperar(itemBaseFP.getBaseFP().getId()));
        ((EventoFP) selecionado).getItensBasesFPs().add(itemBaseFP);
        selecionado.getBaseFPs().add(itemBaseFP.getBaseFP());
        itemBaseFP.getBaseFP().getItensBasesFPs().add(itemBaseFP);
        itemBaseFP = new ItemBaseFP();
    }

    public void removeItemBaseFP(ActionEvent e) {
        itemBaseFP = (ItemBaseFP) e.getComponent().getAttributes().get("objeto");
        itemBaseFP.getBaseFP().getItensBasesFPs().remove(itemBaseFP);
        ((EventoFP) selecionado).getItensBasesFPs().remove(itemBaseFP);
        selecionado.getBaseFPs().add(itemBaseFP.getBaseFP());
        itemBaseFP = new ItemBaseFP();
    }

    public List<SelectItem> getOperacaoFormula() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (OperacaoFormula object : OperacaoFormula.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ItemBaseFP getItemBaseFP() {
        return itemBaseFP;
    }

    public void setItemBaseFP(ItemBaseFP itemBaseFP) {
        this.itemBaseFP = itemBaseFP;
    }

    public List<ItemBaseFP> getListaItemBaseFP() {
        return listaItemBaseFP;
    }

    public void setListaItemBaseFP(List<ItemBaseFP> listaItemBaseFP) {
        this.listaItemBaseFP = listaItemBaseFP;
    }

    public List<SelectItem> getTipoLancamentoFP() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoLancamentoFP object : TipoLancamentoFP.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> tiposNaturezaReferencia() {
        return Util.getListSelectItem(TipoNaturezaRefenciaCalculo.values());
    }

    public Converter getMoneyConverter() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String str) {
                if (str == null || str.toString().trim().equals("")) {
                    return new BigDecimal("0.00");
                } else {
                    str = str.replaceAll(Pattern.quote("."), "");
                    try {
                        str = str.replaceAll(Pattern.quote(","), ".");
                        BigDecimal valor = new BigDecimal(str);
                        return valor;
                    } catch (Exception e) {
                        return new BigDecimal("0.00");
                    }
                }
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object obj) {
                String s = null;
                if (obj == null || obj.toString().trim().equals("")) {
                    return "0,00";
                } else {
                    NumberFormat nf = NumberFormat.getCurrencyInstance();
                    nf.setMinimumFractionDigits(2);
                    nf.setMaximumFractionDigits(3);
                    String saida = String.valueOf(obj).replaceAll("\\.", ",");
                    return saida;
                }
            }
        };
    }

    public List<TipoBaseFP> completaTipoBaseFP(String parte) {
        return tipoBaseFPFacade.listaFiltrando(parte.trim(), "codigo", "descricao");
    }

    public List<ReferenciaFP> completaReferenciaFP(String parte) {
        return referenciaFPFacade.listaFiltrandoPorTipoECodigo(TipoReferenciaFP.VALOR_VALOR, parte.trim());
    }

    public Converter getConverterTipoBaseFP() {
        if (converterTipoBaseFP == null) {
            converterTipoBaseFP = new ConverterAutoComplete(TipoBaseFP.class, tipoBaseFPFacade);
        }
        return converterTipoBaseFP;
    }

    public Converter getConverterReferenciaFP() {
        if (converterRefenciaFP == null) {
            converterRefenciaFP = new ConverterAutoComplete(ReferenciaFP.class, referenciaFPFacade);
        }
        return converterRefenciaFP;
    }

    public Converter getConverterEventoAgrupador() {
        if (converterEventoFP == null) {
            converterEventoFP = new ConverterAutoComplete(EventoFP.class, eventoFPFacade);
        }
        return converterEventoFP;
    }

    public List<ClassificacaoVerba> completarClassificacoesVerbas(String filtro) {
        return eventoFPFacade.buscarClassificacoesVerbas(filtro);
    }

    public List<SelectItem> getEventos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (EventoFP object : eventoFPFacade.listaEventosAtivosFolha()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }


    public List<SelectItem> getTiposFuncionalidade() {
        return Util.getListSelectItem(TipoFuncionalidadeRH.values());
    }

    public List<SelectItem> getEventosComOpcaoNull() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(selecionado, "Este Evento."));
        for (EventoFP object : eventoFPFacade.listaEventosAtivosFolha()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getIdentificadorEventos() {
        return Util.getListSelectItem(IdentificacaoEventoFP.values());
    }

    public void aplicarEventoAgrupador() {
        if (selecionado.getConsignacao()) {
            selecionado.setEventoFPAgrupador(selecionado);
        } else {
            selecionado.setEventoFPAgrupador(null);
        }
    }

    public void desmarcarProporcionalizar13Salario() {
        if (selecionado.getTipoCalculo13() != null && TipoCalculo13.NAO.equals(selecionado.getTipoBase())) {
            selecionado.setPropMesesTrabalhados(false);
        }
    }

    public void adicionarEventoFPTipoFolha() {
        try {
            validarCamposObrigatorios();
            associarEAdicionarEventoTipoFolha(eventoFPTipoFolha);
            cancelarEventoTipoFolha();
        } catch (ValidacaoException val) {
            logger.error("Erro: ", val);
            if (val.temMensagens()) {
                FacesUtil.printAllFacesMessages(val.getMensagens());
            } else {
                FacesUtil.addAtencao(val.getMessage());
            }
        } catch (Exception e) {
            logger.error("Erro: ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao adicionar um novo Tipo de Folha, erro:" + e.getMessage());
        }
    }

    public void adicionarBloqueioFuncionalidade() {
        try {
            validarCamposObrigatoriosBloqueio();
            validarRegistrosExistentes(bloqueioFuncionalidade, selecionado.getBloqueiosFuncionalidade());
            associarEventoAoBloqueioFuncionalidade(bloqueioFuncionalidade);
            cancelarEventoBloqueio();
        } catch (ValidacaoException val) {
            logger.error("Erro: ", val);
            if (val.temMensagens()) {
                FacesUtil.printAllFacesMessages(val.getMensagens());
            } else {
                FacesUtil.addAtencao(val.getMessage());
            }
        } catch (Exception e) {
            logger.error("Erro: ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao adicionar um novo Bloqueio de Funcionalidade, erro:" + e.getMessage());
        }
    }

    private void validarRegistrosExistentes(BloqueioFuncionalidade bloqueioFuncionalidade, List<BloqueioFuncionalidade> bloqueiosFuncionalidade) {
        if (bloqueioFuncionalidade != null) {
            if (bloqueioFuncionalidade.getTipoFuncionalidadeRH() != null) {
                for (BloqueioFuncionalidade funcionalidade : bloqueiosFuncionalidade) {
                    if (funcionalidade.getTipoFuncionalidadeRH().equals(bloqueioFuncionalidade.getTipoFuncionalidadeRH())) {
                        throw new ValidacaoException("Já existe um registro do tipo <b>" + bloqueioFuncionalidade.getTipoFuncionalidadeRH() + " </b> adicionado.");
                    }
                }
            }
        }
    }

    private void associarEAdicionarEventoTipoFolha(EventoFPTipoFolha eventoFPTipoFolha) {
        eventoFPTipoFolha.setEventoFP(selecionado);
        selecionado.getTiposFolha().add(eventoFPTipoFolha);
    }

    private void associarEventoAoBloqueioFuncionalidade(BloqueioFuncionalidade bloqueio) {
        bloqueio.setEventoFP(selecionado);
        selecionado.getBloqueiosFuncionalidade().add(bloqueio);
    }

    private void validarCamposObrigatorios() throws ValidacaoException {
        if (eventoFPTipoFolha != null) {
            if (eventoFPTipoFolha.getTipoFolhaDePagamento() == null) {
                throw new ValidacaoException("Selecione corretamente o Tipo de Folha.");
            }
        }
    }

    private void validarCamposObrigatoriosBloqueio() throws ValidacaoException {
        if (bloqueioFuncionalidade != null) {
            if (bloqueioFuncionalidade.getTipoFuncionalidadeRH() == null) {
                throw new ValidacaoException("Selecione corretamente o Tipo de Funcionalidade.");
            }
        }
    }

    public void cancelarEventoTipoFolha() {
        eventoFPTipoFolha = new EventoFPTipoFolha();
    }

    public void cancelarEventoBloqueio() {
        bloqueioFuncionalidade = new BloqueioFuncionalidade();
    }

    public void selecionarEvento(EventoFPTipoFolha evento) {
        eventoFPTipoFolha = evento;
        if (selecionado.getTiposFolha().contains(evento)) {
            selecionado.getTiposFolha().remove(evento);
        }
    }

    public void selecionarBloqueio(BloqueioFuncionalidade bloqueio) {
        bloqueioFuncionalidade = bloqueio;
        if (selecionado.getBloqueiosFuncionalidade().contains(bloqueio)) {
            selecionado.getBloqueiosFuncionalidade().remove(bloqueio);
        }
    }

    public void removerEvento(EventoFPTipoFolha evento) {
        if (selecionado.getTiposFolha().contains(evento)) {
            selecionado.getTiposFolha().remove(evento);
        }
    }

    public void removerBloqueio(BloqueioFuncionalidade bloqueio) {
        if (selecionado.getBloqueiosFuncionalidade().contains(bloqueio)) {
            selecionado.getBloqueiosFuncionalidade().remove(bloqueio);
        }
    }

    public List<SelectItem> getNaturezaRubrica() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (NaturezaRubrica naturezaRubrica : eventoFPFacade.getNaturezaRubricaFacade().lista()) {
            toReturn.add(new SelectItem(naturezaRubrica, naturezaRubrica.getCodigo() + " - " + naturezaRubrica.getNome()));
        }
        return toReturn;
    }

    public List<SelectItem> getIncidenciaTributariaPrevidenciaSocial() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (IncidenciaTributariaPrevidencia previdencia : eventoFPFacade.getIncidenciaTributariaPrevidenciaFacade().lista()) {
            toReturn.add(new SelectItem(previdencia, previdencia.getCodigo() + " - " + previdencia.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getIncidenciaTributariaIRRF() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (IncidenciaTributariaIRRF irrf : eventoFPFacade.getIncidenciaTributariaIRRFFacade().lista()) {
            toReturn.add(new SelectItem(irrf, irrf.getCodigo() + " - " + irrf.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getIncidenciaTributariaFGTS() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (IncidenciaTributariaFGTS fgts : eventoFPFacade.getIncidenciaTributariaFGTSFacade().lista()) {
            toReturn.add(new SelectItem(fgts, fgts.getCodigo() + " - " + fgts.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getIncidenciaContribuicaoSindical() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (IncidenciaTributariaRPPS contribuicaoSindical : eventoFPFacade.getIncidenciaTributariaRPPSFacade().lista()) {
            toReturn.add(new SelectItem(contribuicaoSindical, contribuicaoSindical.getCodigo() + " - " + contribuicaoSindical.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getEntidade() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (ConfiguracaoEmpregadorESocial configuracao : eventoFPFacade.getConfiguracaoEmpregadorESocialFacade().lista()) {
            toReturn.add(new SelectItem(configuracao.getEntidade(), configuracao.getEntidade().getPessoaJuridica().getCnpj() + " - " + configuracao.getEntidade().getNome()));
        }
        return toReturn;
    }

    public List<HierarquiaOrganizacional> completarHierarquia(String parte) {
        return hierarquiaOrganizacionalFacade.buscarHierarquiaFiltrandoTipoAdministrativaAndHierarquiaSemRaiz(parte.trim());

    }

    private void validarLotacaoFuncional() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Lotação Funcional.");
        } else {
            for (EventoFPUnidade fpUnidade : selecionado.getEventoFPUnidade()) {
                if (hierarquiaOrganizacional.getSubordinada().equals(fpUnidade.getUnidadeOrganizacional())) {
                    ve.adicionarMensagemDeCampoObrigatorio("Lotação Funcional já adicionada.");
                }
            }
        }
        ve.lancarException();
    }

    public void removerLotacao(EventoFPUnidade eventoUnidade) {
        selecionado.getEventoFPUnidade().remove(eventoUnidade);
    }

    public void adicionarLotacao() {
        try {

            validarLotacaoFuncional();
            eventoFPUnidade.setEventoFP(selecionado);
            eventoFPUnidade.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
            selecionado.getEventoFPUnidade().add(eventoFPUnidade);
            eventoFPUnidade = new EventoFPUnidade();
            hierarquiaOrganizacional = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    private void validarEvento() {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoEmpregadorESocial == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado configuração de empregador do e-social para esse evento.");
        }
        ve.lancarException();
    }

    public void prepararEventoS1010() {
        if (selecionado.getItensEventoFPEmpregador() != null) {
            if (selecionado.getItensEventoFPEmpregador().stream().count() > 1) {
                FacesUtil.executaJavaScript("dlgListEmpregador.show()");
            } else {
                eventoFPEmpregadorList = selecionado.getItensEventoFPEmpregador();
                enviarEventoS1010();
            }
        }
    }

    public void enviarEventoS1010() {
        try {
            for (EventoFPEmpregador fpEmpregador : eventoFPEmpregadorList) {
                configuracaoEmpregadorESocial = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(fpEmpregador.getEntidade());
                validarEvento();
                eventoFPFacade.enviarEventoS1010(configuracaoEmpregadorESocial, selecionado);
            }
            FacesUtil.addOperacaoRealizada("Evento enviado com sucesso! ");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<SelectItem> getTipoClassificacaoConsignacao() {
        return Util.getListSelectItem(TipoClassificacaoConsignacao.values());
    }

    public void adicionarEmpregadorEsocial() {
        try {
            validarEventoEsocial();
            eventoFPEmpregador.setEventoFP(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getItensEventoFPEmpregador(), eventoFPEmpregador);
            Collections.sort(selecionado.getItensEventoFPEmpregador());
            eventoFPEmpregador = new EventoFPEmpregador();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarEventoEsocial() {
        ValidacaoException ve = new ValidacaoException();
        if (eventoFPEmpregador.getEntidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Entidade");
        }
        if (Strings.isNullOrEmpty(eventoFPEmpregador.getIdentificacaoTabela())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Identificação da Tabela");
        }
        if (eventoFPEmpregador.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Inicio de vigência");
            ve.lancarException();
        }
        if (eventoFPEmpregador.getNaturezaRubrica() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Natureza da Rubricas");
        }
        if (eventoFPEmpregador.getIncidenciaPrevidencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Incidência Tributária para a Previdência Social");
        }
        if (eventoFPEmpregador.getIncidenciaTributariaIRRF() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Incidência Tributária para IRRF");
        }
        if (eventoFPEmpregador.getIncidenciaTributariaFGTS() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Incidência Tributária para FGTS");
        }
        if (eventoFPEmpregador.getIncidenciaTributariaRPPS() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Incidência Tributária para o RPPS");
        }
        if (eventoFPEmpregador.getEntidade() != null && eventoFPEmpregador.getInicioVigencia() != null) {
            ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(eventoFPEmpregador.getEntidade());
            if (configuracaoEmpregadorESocial != null) {
                if (configuracaoEmpregadorESocial.getDataInicioObrigatoriedadeFase1() != null && eventoFPEmpregador.getInicioVigencia().before(configuracaoEmpregadorESocial.getDataInicioObrigatoriedadeFase1())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Inicio de vigência configurado na aba E-social deve ser maior ou igual a data de " +
                        "inicio da obrigatoriedade do empregador, ou seja,  " + DataUtil.getDataFormatada(configuracaoEmpregadorESocial.getDataInicioObrigatoriedadeFase1()));
                }
            }

            if (eventoFPEmpregador.getFimVigencia() != null && (eventoFPEmpregador.getInicioVigencia().after(eventoFPEmpregador.getFimVigencia()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Inicio de vigência deve ser menor que o Final de vigência");
            }
        }
        for (EventoFPEmpregador fpEmpregador : selecionado.getItensEventoFPEmpregador()) {
            if (!eventoFPEmpregador.equals(fpEmpregador)) {
                if (eventoFPEmpregador.getEntidade().equals(fpEmpregador.getEntidade()) && fpEmpregador.getFimVigencia() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A Entidade " + eventoFPEmpregador.getEntidade() + " está vigente");
                }
                if (fpEmpregador.getFimVigencia() != null && eventoFPEmpregador.getInicioVigencia().compareTo(fpEmpregador.getFimVigencia()) <= 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Inicio de vigência deve ser maior que " + DataUtil.getDataFormatada(fpEmpregador.getFimVigencia()));
                }
            }

        }
        ve.lancarException();
    }

    public void editarEventoEmpregador(EventoFPEmpregador empregador) {
        eventoFPEmpregador = (EventoFPEmpregador) Util.clonarObjeto(empregador);
    }

    public void removerEventoEmpregador(EventoFPEmpregador empregador) {
        selecionado.getItensEventoFPEmpregador().remove(empregador);
    }

    public void atribuirFalseTetoEmpregador() {
        eventoFPEmpregador.setTetoRemuneratorio(Boolean.FALSE);
    }

    public boolean eventoFPEmpregadorIsEmpty() {
        if (selecionado != null) {
            return selecionado.getItensEventoFPEmpregador().isEmpty();
        }
        return true;
    }

    public List<EventoFPEmpregador> getEventoFPEmpregadorList() {
        return eventoFPEmpregadorList;
    }

    public List<SelectItem> getEmpregadores() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (EventoFPEmpregador obj : selecionado.getItensEventoFPEmpregador()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public void setEventoFPEmpregadorList(List<EventoFPEmpregador> eventoFPEmpregadorList) {
        this.eventoFPEmpregadorList = eventoFPEmpregadorList;
    }

    public void marcarTodosEmpregadores() {
        setEventoFPEmpregadorList(eventoFPEmpregadorList = selecionado.getItensEventoFPEmpregador());
    }

    public void desmarcarTodosEmpregadores() {
        List<EventoFPEmpregador> novo = new ArrayList<>();
        eventoFPEmpregadorList = novo;
    }

    public boolean todosEmpregadoresMarcados() {
        return eventoFPEmpregadorList != null && selecionado.getItensEventoFPEmpregador().size() == eventoFPEmpregadorList.size();
    }


    public void confirmarEmpregadoresParaConfigurar() {
        try {
            validarNovosEmpregadores();
            for (ConfiguracaoEmpregadorESocial novosEmpregadoresConfigurado : novosEmpregadoresConfigurados) {
                EventoFPEmpregador item = duplicarConfiguracaoEmpregadorEsocial(novosEmpregadoresConfigurado);
                selecionado.getItensEventoFPEmpregador().add(item);
            }
            FacesUtil.executaJavaScript(" dialogCopiarConfig.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    private void validarNovosEmpregadores() {
        ValidacaoException ve = new ValidacaoException();
        if (novosEmpregadoresConfigurados == null || novosEmpregadoresConfigurados.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi informado nenhuma Entidade.");
        } else {
            for (ConfiguracaoEmpregadorESocial novosEmpregadoresConfigurado : novosEmpregadoresConfigurados) {
                if (Strings.isNullOrEmpty(novosEmpregadoresConfigurado.getIdentificacaoTabela())) {
                    ve.adicionarMensagemDeCampoObrigatorio("Não foi informado a Identificação Tabela da Entidade " + novosEmpregadoresConfigurado.getEntidade());
                    break;
                }
            }
        }
        ve.lancarException();
    }

    private EventoFPEmpregador duplicarConfiguracaoEmpregadorEsocial(ConfiguracaoEmpregadorESocial novoEmpregadorCOnfigurado) {
        EventoFPEmpregador item = new EventoFPEmpregador();
        item.setInicioVigencia(eventoFPEmpregadorSelecionado.getInicioVigencia());
        item.setFimVigencia(eventoFPEmpregadorSelecionado.getFimVigencia());
        item.setEventoFP(eventoFPEmpregadorSelecionado.getEventoFP());
        item.setTetoRemuneratorio(eventoFPEmpregadorSelecionado.getTetoRemuneratorio());
        item.setEntidade(novoEmpregadorCOnfigurado.getEntidade());
        item.setIdentificacaoTabela(novoEmpregadorCOnfigurado.getIdentificacaoTabela());
        item.setIncidenciaPrevidencia(eventoFPEmpregadorSelecionado.getIncidenciaPrevidencia());
        item.setIncidenciaTributariaRPPS(eventoFPEmpregadorSelecionado.getIncidenciaTributariaRPPS());
        item.setIncidenciaTributariaFGTS(eventoFPEmpregadorSelecionado.getIncidenciaTributariaFGTS());
        item.setIncidenciaTributariaIRRF(eventoFPEmpregadorSelecionado.getIncidenciaTributariaIRRF());
        item.setNaturezaRubrica(eventoFPEmpregadorSelecionado.getNaturezaRubrica());
        return item;
    }

    public void carregarEmpregadoresQueNaoEstaoNaConfiguracao(EventoFPEmpregador itemEvento) {
        eventoFPEmpregadorSelecionado = itemEvento;
        empregadorSemConfiguracao = configuracaoEmpregadorESocialFacade.getEntidadesSemConfiguracao(selecionado.getItensEventoFPEmpregador());
    }

    public boolean mostrarBotaoSelecionarEmpregadores(ConfiguracaoEmpregadorESocial config) {
        return !novosEmpregadoresConfigurados.contains(config);
    }

    public void removerConfiguracaoEmpregador(ConfiguracaoEmpregadorESocial config) {
        config.setIdentificacaoTabela(null);
        novosEmpregadoresConfigurados.remove(config);
    }

    public void adicionarConfiguracaoEmpregador(ConfiguracaoEmpregadorESocial config) {
        novosEmpregadoresConfigurados.add(config);
    }

    public FiltrosFuncoesFolha getFiltrosFuncoesFolha() {
        return filtrosFuncoesFolha;
    }

    public void limparClassificacaoVerba() {
        if (selecionado.getNaoEnviarVerbaSicap()) {
            selecionado.setClassificacaoVerba(null);
        }
    }

    public boolean habilitarExibirFichaRPA(){
        return selecionado != null && TipoEventoFP.INFORMATIVO.equals(selecionado.getTipoEventoFP()) && TipoExecucaoEP.RPA.equals(tipoExecucaoEP);
    }

    public List<SelectItem> getTiposContribuicaoBBPrev() {
        return Util.getListSelectItem(TipoContribuicaoBBPrev.values());
    }
}
