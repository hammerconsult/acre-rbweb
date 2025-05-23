/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcCadastroImobiliarioDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcCadastroRuralDAO;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;


@ManagedBean(name = "processoDebitoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listaProcessoCancelamento", pattern = "/tributario/conta-corrente/processo-de-debitos/cancelamento/listar/", viewId = "/faces/tributario/contacorrente/processodebitoscancelamento/lista.xhtml"),
    @URLMapping(id = "novoProcessoCancelamento", pattern = "/tributario/conta-corrente/processo-de-debitos/cancelamento/novo/", viewId = "/faces/tributario/contacorrente/processodebitoscancelamento/edita.xhtml"),
    @URLMapping(id = "verProcessoCancelamento", pattern = "/tributario/conta-corrente/processo-de-debitos/cancelamento/ver/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitoscancelamento/visualizar.xhtml"),
    @URLMapping(id = "editarProcessoCancelamento", pattern = "/tributario/conta-corrente/processo-de-debitos/cancelamento/editar/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitoscancelamento/edita.xhtml"),

    @URLMapping(id = "listaProcessoRemissao", pattern = "/tributario/conta-corrente/processo-de-debitos/remissao/listar/", viewId = "/faces/tributario/contacorrente/processodebitosremissao/lista.xhtml"),
    @URLMapping(id = "novoProcessoRemissao", pattern = "/tributario/conta-corrente/processo-de-debitos/remissao/novo/", viewId = "/faces/tributario/contacorrente/processodebitosremissao/edita.xhtml"),
    @URLMapping(id = "verProcessoRemissao", pattern = "/tributario/conta-corrente/processo-de-debitos/remissao/ver/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitosremissao/visualizar.xhtml"),
    @URLMapping(id = "editarProcessoRemissao", pattern = "/tributario/conta-corrente/processo-de-debitos/remissao/editar/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitosremissao/edita.xhtml"),

    @URLMapping(id = "listaProcessoSuspensao", pattern = "/tributario/conta-corrente/processo-de-debitos/suspensao/listar/", viewId = "/faces/tributario/contacorrente/processodebitossuspencao/lista.xhtml"),
    @URLMapping(id = "novoProcessoSuspensao", pattern = "/tributario/conta-corrente/processo-de-debitos/suspensao/novo/", viewId = "/faces/tributario/contacorrente/processodebitossuspencao/edita.xhtml"),
    @URLMapping(id = "verProcessoSuspensao", pattern = "/tributario/conta-corrente/processo-de-debitos/suspensao/ver/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitossuspencao/visualizar.xhtml"),
    @URLMapping(id = "editarProcessoSuspensao", pattern = "/tributario/conta-corrente/processo-de-debitos/suspensao/editar/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitossuspencao/edita.xhtml"),

    @URLMapping(id = "listaProcessoReativacao", pattern = "/tributario/conta-corrente/processo-de-debitos/suspensao-reativacao/listar/", viewId = "/faces/tributario/contacorrente/processodebitosreativacao/lista.xhtml"),
    @URLMapping(id = "novoProcessoReativacao", pattern = "/tributario/conta-corrente/processo-de-debitos/suspensao-reativacao/novo/", viewId = "/faces/tributario/contacorrente/processodebitosreativacao/edita.xhtml"),
    @URLMapping(id = "verProcessoReativacao", pattern = "/tributario/conta-corrente/processo-de-debitos/suspensao-reativacao/ver/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitosreativacao/visualizar.xhtml"),
    @URLMapping(id = "editarProcessoReativacao", pattern = "/tributario/conta-corrente/processo-de-debitos/suspensao-reativacao/editar/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitosreativacao/edita.xhtml"),

    @URLMapping(id = "listaProcessoDacao", pattern = "/tributario/conta-corrente/processo-de-debitos/dacao/listar/", viewId = "/faces/tributario/contacorrente/processodebitosdacao/lista.xhtml"),
    @URLMapping(id = "novoProcessoDacao", pattern = "/tributario/conta-corrente/processo-de-debitos/dacao/novo/", viewId = "/faces/tributario/contacorrente/processodebitosdacao/edita.xhtml"),
    @URLMapping(id = "verProcessoDacao", pattern = "/tributario/conta-corrente/processo-de-debitos/dacao/ver/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitosdacao/visualizar.xhtml"),
    @URLMapping(id = "editarProcessoDacao", pattern = "/tributario/conta-corrente/processo-de-debitos/dacao/editar/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitosdacao/edita.xhtml"),

    @URLMapping(id = "listaProcessoDecadencia", pattern = "/tributario/conta-corrente/processo-de-debitos/decadencia/listar/", viewId = "/faces/tributario/contacorrente/processodebitosdecadencia/lista.xhtml"),
    @URLMapping(id = "novoProcessoDecadencia", pattern = "/tributario/conta-corrente/processo-de-debitos/decadencia/novo/", viewId = "/faces/tributario/contacorrente/processodebitosdecadencia/edita.xhtml"),
    @URLMapping(id = "verProcessoDecadencia", pattern = "/tributario/conta-corrente/processo-de-debitos/decadencia/ver/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitosdecadencia/visualizar.xhtml"),
    @URLMapping(id = "editarProcessoDecadencia", pattern = "/tributario/conta-corrente/processo-de-debitos/decadencia/editar/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitosdecadencia/edita.xhtml"),

    @URLMapping(id = "listaProcessoIsencao", pattern = "/tributario/conta-corrente/processo-de-debitos/isencao/listar/", viewId = "/faces/tributario/contacorrente/processodebitosisencao/lista.xhtml"),
    @URLMapping(id = "novoProcessoIsencao", pattern = "/tributario/conta-corrente/processo-de-debitos/isencao/novo/", viewId = "/faces/tributario/contacorrente/processodebitosisencao/edita.xhtml"),
    @URLMapping(id = "verProcessoIsencao", pattern = "/tributario/conta-corrente/processo-de-debitos/isencao/ver/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitosisencao/visualizar.xhtml"),
    @URLMapping(id = "editarProcessoIsencao", pattern = "/tributario/conta-corrente/processo-de-debitos/isencao/editar/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitosisencao/edita.xhtml"),

    @URLMapping(id = "listaProcessoPrescricao", pattern = "/tributario/conta-corrente/processo-de-debitos/prescricao/listar/", viewId = "/faces/tributario/contacorrente/processodebitosprescricao/lista.xhtml"),
    @URLMapping(id = "novoProcessoPrescricao", pattern = "/tributario/conta-corrente/processo-de-debitos/prescricao/novo/", viewId = "/faces/tributario/contacorrente/processodebitosprescricao/edita.xhtml"),
    @URLMapping(id = "verProcessoPrescricao", pattern = "/tributario/conta-corrente/processo-de-debitos/prescricao/ver/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitosprescricao/visualizar.xhtml"),
    @URLMapping(id = "editarProcessoPrescricao", pattern = "/tributario/conta-corrente/processo-de-debitos/prescricao/editar/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitosprescricao/edita.xhtml"),

    @URLMapping(id = "listaProcessoAnistia", pattern = "/tributario/conta-corrente/processo-de-debitos/anistia/listar/", viewId = "/faces/tributario/contacorrente/processodebitosanistia/lista.xhtml"),
    @URLMapping(id = "novoProcessoAnistia", pattern = "/tributario/conta-corrente/processo-de-debitos/anistia/novo/", viewId = "/faces/tributario/contacorrente/processodebitosanistia/edita.xhtml"),
    @URLMapping(id = "verProcessoAnistia", pattern = "/tributario/conta-corrente/processo-de-debitos/anistia/ver/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitosanistia/visualizar.xhtml"),
    @URLMapping(id = "editarProcessoAnistia", pattern = "/tributario/conta-corrente/processo-de-debitos/anistia/editar/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitosanistia/edita.xhtml"),

    @URLMapping(id = "listaProcessoDesistencia", pattern = "/tributario/conta-corrente/processo-de-debitos/desistencia/listar/", viewId = "/faces/tributario/contacorrente/processodebitosdesistencia/lista.xhtml"),
    @URLMapping(id = "novoProcessoDesistencia", pattern = "/tributario/conta-corrente/processo-de-debitos/desistencia/novo/", viewId = "/faces/tributario/contacorrente/processodebitosdesistencia/edita.xhtml"),
    @URLMapping(id = "verProcessoDesistencia", pattern = "/tributario/conta-corrente/processo-de-debitos/desistencia/ver/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitosdesistencia/visualizar.xhtml"),
    @URLMapping(id = "editarProcessoDesistencia", pattern = "/tributario/conta-corrente/processo-de-debitos/desistencia/editar/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitosdesistencia/edita.xhtml"),

    @URLMapping(id = "listaProcessoBaixa", pattern = "/tributario/conta-corrente/processo-de-debitos/baixa/listar/", viewId = "/faces/tributario/contacorrente/processodebitosbaixa/lista.xhtml"),
    @URLMapping(id = "novoProcessoBaixa", pattern = "/tributario/conta-corrente/processo-de-debitos/baixa/novo/", viewId = "/faces/tributario/contacorrente/processodebitosbaixa/edita.xhtml"),
    @URLMapping(id = "verProcessoBaixa", pattern = "/tributario/conta-corrente/processo-de-debitos/baixa/ver/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitosbaixa/visualizar.xhtml"),
    @URLMapping(id = "editarProcessoBaixa", pattern = "/tributario/conta-corrente/processo-de-debitos/baixa/editar/#{processoDebitoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitosbaixa/edita.xhtml")
})

public class ProcessoDebitoControlador extends PrettyControlador<ProcessoDebito> implements Serializable, CRUD {

    private final String DIVIDA_ATIVA = "Dívida Ativa";
    private final String DIVIDA_ATIVA_AJUIZADA = "Dívida Ativa Ajuizada";
    private final String DO_EXERCICIO = "Do Exercício";
    @EJB
    private ProcessoDebitoFacade processoDebitoFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private CadastroFacade cadastroFacade;
    private JdbcCadastroImobiliarioDAO jdbcCadastroImobiliarioDAO;
    private JdbcCadastroRuralDAO jdbcCadastroRuralDAO;
    private ConverterAutoComplete converterAtoLegal;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterDivida;
    private ConverterAutoComplete converterExercicio;
    private List<ParcelaValorDivida> listaParcela;
    //FILTROS PARA CONSULTA
    private String filtroCodigoBarras;
    private Pessoa filtroContribuinte;
    private Cadastro cadastro;
    private String filtroNumeroDAM;
    private ItemProcessoDebito itemProcessoDebito;
    private TipoCadastroTributario tipoCadastroTributario;
    private Divida filtroDivida;
    private Exercicio filtroExercicioInicio;
    private Exercicio filtroExercicioFinal;
    private boolean dividaDoExercicio;
    private boolean dividaAtiva;
    private boolean dividaAtivaAzuijada;
    private ConverterAutoComplete converterCadastroImobiliario;
    private ConverterAutoComplete converterCadastroEconomico;
    private ConverterAutoComplete converterCadastroRendasPatrimoniais;
    private ConverterAutoComplete converterCadastroRural;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private ContratoRendasPatrimoniaisFacade contratoRendasPatrimoniaisFacade;
    @EJB
    private LoteFacade loteFacade;
    private List<ResultadoParcela> resultadoConsulta;
    private SituacaoParcela situacaoParcela;
    private Date vencimentoInicial;
    private Date vencimentoFinal;
    private ResultadoParcela[] resultados;
    private SituacaoCadastralCadastroEconomico situacaoEscolhidaCadastroEconomico;
    private Boolean situacaoEscolhidaCadastroImobiliario;
    private Boolean situacaoEscolhidaCadastroRural;
    private SituacaoCadastralPessoa situacaoEscolhidaCadastroPessoa;
    private List<ItemProcessoDebito> novosItens = Lists.newArrayList();
    private List<ItemProcessoDebito> itensRemovidos = Lists.newArrayList();

    private LazyDataModel<ItemProcessoDebito> itensDoProcesso;

    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Future<ResultadoValidacao> futureEstorno;

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public List<ResultadoParcela> getResultadoConsulta() {
        return resultadoConsulta;
    }

    public void setResultadoConsulta(List<ResultadoParcela> resultadoConsulta) {
        this.resultadoConsulta = resultadoConsulta;
    }

    public List<ItemProcessoDebito> getItensRemovidos() {
        return itensRemovidos;
    }

    public List<ItemProcessoDebito> getNovosItens() {
        return novosItens;
    }

    private void criarDataModelDosItens() {
        itensDoProcesso = new LazyDataModel<ItemProcessoDebito>() {
            @Override
            public List<ItemProcessoDebito> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                logger.error("First {}", first);
                setRowCount(processoDebitoFacade.contarItens(selecionado, itensRemovidos) + novosItens.size());
                if (novosItens != null && novosItens.size() > first) {
                    List<ItemProcessoDebito> itens = novosItens.subList(first, first + (novosItens.size() < pageSize ? novosItens.size() : pageSize));
                    if (itens.size() < pageSize) {
                        itens.addAll(processoDebitoFacade.buscarItemProcesso(0, pageSize - itens.size(), selecionado, itensRemovidos));
                    }
                    return itens;
                }
                return processoDebitoFacade.buscarItemProcesso(first, pageSize, selecionado, itensRemovidos);
            }
        };
    }

    public LazyDataModel<ItemProcessoDebito> getItensDoProcesso() {
        return itensDoProcesso;
    }


    public ProcessoDebitoControlador() {
        super(ProcessoDebito.class);
        resultadoConsulta = Lists.newArrayList();
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        jdbcCadastroImobiliarioDAO = (JdbcCadastroImobiliarioDAO) ap.getBean("cadastroImobiliarioJDBCDAO");
        jdbcCadastroRuralDAO = (JdbcCadastroRuralDAO) ap.getBean("cadastroRuralDAO");
    }

    public SituacaoParcela getSituacaoParcela() {
        return situacaoParcela;
    }

    public void setSituacaoParcela(SituacaoParcela situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
    }

    public ProcessoDebito getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ProcessoDebito selecionado) {
        this.selecionado = selecionado;
    }

    public String getFiltroCodigoBarras() {
        return filtroCodigoBarras;
    }

    public void setFiltroCodigoBarras(String filtroCodigoBarras) {
        this.filtroCodigoBarras = filtroCodigoBarras;
    }

    public Date getVencimentoFinal() {
        return vencimentoFinal;
    }

    public void setVencimentoFinal(Date vencimentoFinal) {
        this.vencimentoFinal = vencimentoFinal;
    }

    public Date getVencimentoInicial() {
        return vencimentoInicial;
    }

    public void setVencimentoInicial(Date vencimentoInicial) {
        this.vencimentoInicial = vencimentoInicial;
    }

    public Pessoa getFiltroContribuinte() {
        return filtroContribuinte;
    }

    public void setFiltroContribuinte(Pessoa filtroContribuinte) {
        this.filtroContribuinte = filtroContribuinte;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public String getFiltroNumeroDAM() {
        return filtroNumeroDAM;
    }

    public void setFiltroNumeroDAM(String filtroNumeroDAM) {
        this.filtroNumeroDAM = filtroNumeroDAM;
    }

    public List<ParcelaValorDivida> getListaParcela() {
        return listaParcela;
    }

    public void setListaParcela(List<ParcelaValorDivida> listaParcela) {
        this.listaParcela = listaParcela;
    }

    public ItemProcessoDebito getItemProcessoDebito() {
        return itemProcessoDebito;
    }

    public void setItemProcessoDebito(ItemProcessoDebito itemProcessoDebito) {
        this.itemProcessoDebito = itemProcessoDebito;
    }

    public boolean isDividaAtiva() {
        return dividaAtiva;
    }

    public void setDividaAtiva(boolean dividaAtiva) {
        this.dividaAtiva = dividaAtiva;
    }

    public boolean isDividaAtivaAzuijada() {
        return dividaAtivaAzuijada;
    }

    public void setDividaAtivaAzuijada(boolean dividaAtivaAzuijada) {
        this.dividaAtivaAzuijada = dividaAtivaAzuijada;
    }

    public boolean isDividaDoExercicio() {
        return dividaDoExercicio;
    }

    public void setDividaDoExercicio(boolean dividaDoExercicio) {
        this.dividaDoExercicio = dividaDoExercicio;
    }

    public Divida getFiltroDivida() {
        return filtroDivida;
    }

    public void setFiltroDivida(Divida filtroDivida) {
        this.filtroDivida = filtroDivida;
    }

    public Exercicio getFiltroExercicioFinal() {
        return filtroExercicioFinal;
    }

    public void setFiltroExercicioFinal(Exercicio filtroExercicioFinal) {
        this.filtroExercicioFinal = filtroExercicioFinal;
    }

    public Exercicio getFiltroExercicioInicio() {
        return filtroExercicioInicio;
    }

    public void setFiltroExercicioInicio(Exercicio filtroExercicioInicio) {
        this.filtroExercicioInicio = filtroExercicioInicio;
    }

    public ResultadoParcela[] getResultados() {
        return resultados;
    }

    public void setResultados(ResultadoParcela[] resultados) {
        this.resultados = resultados;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, processoDebitoFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return processoDebitoFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public ConverterAutoComplete getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterAutoComplete(Divida.class, processoDebitoFacade.getDividaFacade());
        }
        return converterDivida;
    }

    public List<Divida> completarDividas(String parte) {
        return processoDebitoFacade.getDividaFacade().listaFiltrandoDividas(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, processoDebitoFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public List<Exercicio> completaExercicio(String parte) {
        return processoDebitoFacade.getExercicioFacade().listaFiltrandoEspecial(parte.trim());
    }

    @Override
    public AbstractFacade getFacede() {
        return processoDebitoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        if (selecionado.getTipo() != null) {
            return "/tributario/conta-corrente/processo-de-debitos/" + selecionado.getTipo().getUrl() + "/";
        }
        return "/tributario/conta-corrente/processo-de-debitos/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void novoGeral() {
        itemProcessoDebito = new ItemProcessoDebito();
        operacao = Operacoes.NOVO;
        cadastro = null;
        tipoCadastroTributario = null;
        novosItens = Lists.newArrayList();
        itensRemovidos = Lists.newArrayList();
        try {
            selecionado.setUsuarioIncluiu(processoDebitoFacade.getSistemaFacade().getUsuarioCorrente());
            selecionado.setSituacao(SituacaoProcessoDebito.EM_ABERTO);
            selecionado.setLancamento(processoDebitoFacade.getSistemaFacade().getDataOperacao());
            selecionado.setExercicio(processoDebitoFacade.getSistemaFacade().getExercicioCorrente());
            selecionado.setCodigo(processoDebitoFacade.recuperarProximoCodigoPorExercicioTipo(selecionado.getExercicio(), selecionado.getTipo()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro inesperado durante o processo!", " Log do servidor: " + ex.getMessage()));
        }
    }

    @URLAction(mappingId = "novoProcessoCancelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoCancelamento() {
        super.novo();
        selecionado.setTipo(TipoProcessoDebito.CANCELAMENTO);
        novoGeral();
    }


    @URLAction(mappingId = "novoProcessoRemissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRemissao() {
        super.novo();
        selecionado.setTipo(TipoProcessoDebito.REMISSAO);
        novoGeral();
    }

    @URLAction(mappingId = "novoProcessoSuspensao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoSuspensao() {
        super.novo();
        selecionado.setTipo(TipoProcessoDebito.SUSPENSAO);
        novoGeral();
    }

    @URLAction(mappingId = "novoProcessoReativacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoSuspensaoReativacao() {
        super.novo();
        selecionado.setTipo(TipoProcessoDebito.REATIVACAO);
        novoGeral();
    }

    @URLAction(mappingId = "novoProcessoDacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoDacao() {
        super.novo();
        selecionado.setTipo(TipoProcessoDebito.DACAO);
        novoGeral();
    }

    @URLAction(mappingId = "novoProcessoDecadencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoDecadencia() {
        super.novo();
        selecionado.setTipo(TipoProcessoDebito.DECADENCIA);
        novoGeral();
    }

    @URLAction(mappingId = "novoProcessoIsencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoIsencao() {
        super.novo();
        selecionado.setTipo(TipoProcessoDebito.ISENCAO);
        novoGeral();
    }

    @URLAction(mappingId = "novoProcessoPrescricao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoPrescricao() {
        super.novo();
        selecionado.setTipo(TipoProcessoDebito.PRESCRICAO);
        novoGeral();
    }

    @URLAction(mappingId = "novoProcessoAnistia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoAnistia() {
        super.novo();
        selecionado.setTipo(TipoProcessoDebito.ANISTIA);
        novoGeral();
    }

    @URLAction(mappingId = "novoProcessoDesistencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoDesistencia() {
        super.novo();
        selecionado.setTipo(TipoProcessoDebito.DESISTENCIA);
        novoGeral();
    }

    @URLAction(mappingId = "novoProcessoBaixa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoBaixa() {
        super.novo();
        selecionado.setTipo(TipoProcessoDebito.BAIXA);
        novoGeral();
    }


    @URLAction(mappingId = "verProcessoCancelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verCancelamento() {
        ver();
    }

    @URLAction(mappingId = "verProcessoRemissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verRemissao() {
        ver();
    }

    @URLAction(mappingId = "verProcessoSuspensao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verSuspensao() {
        ver();
    }

    @URLAction(mappingId = "verProcessoReativacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verReativacao() {
        ver();
    }

    @URLAction(mappingId = "verProcessoDacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verDacao() {
        ver();
    }

    @URLAction(mappingId = "verProcessoDecadencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verDecadencia() {
        ver();
    }

    @URLAction(mappingId = "verProcessoIsencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verIsencao() {
        ver();
    }

    @URLAction(mappingId = "verProcessoPrescricao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verPrescricao() {
        ver();
    }

    @URLAction(mappingId = "verProcessoAnistia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verAnistia() {
        ver();
    }

    @URLAction(mappingId = "verProcessoDesistencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verDesistencia() {
        ver();
    }

    @URLAction(mappingId = "verProcessoBaixa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verBaixa() {
        ver();
    }

    @Override
    public void cancelar() {
        if (selecionado.getTipo() != null) {
            String url = selecionado.getTipo().getUrl();
            Web.navegacao("/tributario/conta-corrente/processo-de-debitos/" + url + "/editar/", "/tributario/conta-corrente/processo-de-debitos/" + url + "/listar/");
        }
    }

    public void ver() {
        recuperarObjeto();
        operacao = Operacoes.VER;
        inicializarObjetos();
        criarDataModelDosItens();
    }

    @Override
    public void editar() {
        super.editar();
        inicializarObjetos();
        criarDataModelDosItens();
    }

    private void inicializarObjetos() {
        cadastro = null;
        selecionado.setDataEstorno(processoDebitoFacade.getSistemaFacade().getDataOperacao());
        novosItens = Lists.newArrayList();
        itensRemovidos = Lists.newArrayList();
    }

    @URLAction(mappingId = "editarProcessoCancelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarCancelamento() {
        editar();
    }


    @URLAction(mappingId = "editarProcessoRemissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarRemissao() {
        editar();
    }

    @URLAction(mappingId = "editarProcessoSuspensao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarSuspensao() {
        editar();
    }

    @URLAction(mappingId = "editarProcessoReativacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarSuspensaoReativacao() {
        editar();
    }

    @URLAction(mappingId = "editarProcessoDacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarSuspensaoDacao() {
        editar();
    }

    @URLAction(mappingId = "editarProcessoDecadencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarDecadencia() {
        editar();
    }

    @URLAction(mappingId = "editarProcessoIsencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarIsencao() {
        editar();
    }

    @URLAction(mappingId = "editarProcessoPrescricao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarPrescricao() {
        editar();
    }

    @URLAction(mappingId = "editarProcessoAnistia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarAnistia() {
        editar();
    }

    @URLAction(mappingId = "editarProcessoDesistencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarDesistencia() {
        editar();
    }

    @URLAction(mappingId = "editarProcessoBaixa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarBaixa() {
        editar();
    }

    @Override
    public void salvar() {
        try {
            ResultadoValidacao resultadoValidacao = validarProcesso(selecionado);
            if (resultadoValidacao.isValidado()) {
                selecionado = processoDebitoFacade.salvaRetornando(selecionado);
                for (ItemProcessoDebito novo : novosItens) {
                    novo.setProcessoDebito(selecionado);
                }
                processoDebitoFacade.salvarItens(novosItens);
                processoDebitoFacade.deletarItens(itensRemovidos);
                navegaParaVisualizar();
            } else {
                FacesUtil.printAllMessages(resultadoValidacao.getMensagens());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Não foi possível continuar!", "Ocorreu um erro ao salvar: " + e.getMessage());
        }
    }

    public ResultadoValidacao validarProcesso(ProcessoDebito processo) {
        ResultadoValidacao retorno = new ResultadoValidacao();
        final String summary = "Não foi possível continuar!";
        if (processo.getCodigo() == null) {
            retorno.addErro(null, summary, "O Código deve ser preenchido!");
        } else if (processo.getCodigo().longValue() <= 0) {
            retorno.addErro(null, summary, "O Código deve ser maio que zero!");
        } else if (processoDebitoFacade.codigoEmUso(processo)) {
            retorno.addErro(null, summary, "Código informado em uso! Foi gerado um novo código.");
        }
        if (processo.getTipo() == null) {
            retorno.addErro(null, summary, "O Tipo de Processo de Débitos deve ser informado!");
        }
        if (processo.getLancamento() == null) {
            retorno.addErro(null, summary, "A Data de Lançamento deve ser preenchida!");
        }
        if (processo.getMotivo() == null || processo.getMotivo().trim().length() <= 0) {
            retorno.addErro(null, summary, "O Motivo ou Fundamentação Legal deve ser preenchido!");
        }
        if (((novosItens == null || novosItens.isEmpty()) && itensDoProcesso.getRowCount() == 0)) {
            retorno.addErro(null, summary, "O processo deve conter ao menos uma parcela!");
        }
        if (processo.getUsuarioIncluiu() == null || processo.getUsuarioIncluiu().getId() == null) {
            retorno.addErro(null, summary, "O usuário que incluiu o processo deve ser informado!");
        }
        if (processo.getSituacao() == null) {
            retorno.addErro(null, summary, "A Situação do Processo deve ser preenchida!");
        } else if (processo.getId() != null && processo.getSituacao() != SituacaoProcessoDebito.EM_ABERTO) {
            retorno.addErro(null, summary, "O processo selecionado encontra-se " + processo.getSituacao().getDescricao() + ". Somente processos EM ABERTO podem ser alterados.");
        }
        if (TipoProcessoDebito.BAIXA.equals(processo.getTipo())) {
            if (processo.getDataPagamento() == null) {
                retorno.addErro(null, summary, "A Data do Pagamento deve ser preenchida!");
            }
            if (processo.getValorPago() == null || processo.getValorPago().compareTo(BigDecimal.ZERO) <= 0) {
                retorno.addErro(null, summary, "O Valor Pago deve ser preenchido!");
            }
        }
        if (TipoProcessoDebito.SUSPENSAO.equals(processo.getTipo())) {
            if (processo.getValidade() == null || Util.getDataHoraMinutoSegundoZerado(processo.getValidade())
                .compareTo(Util.getDataHoraMinutoSegundoZerado(new Date())) < 0) {
                retorno.addErro(null, summary, "A Data de Validade deve ser preenchida e deve ser igual ou posterior a data atual!");
            }
        }
        return retorno;
    }

    private void navegaParaVisualizar() {
        String url = selecionado.getTipo().getUrl();
        Web.navegacao(getUrlAtual(), new StringBuilder("/tributario/conta-corrente/processo-de-debitos/")
            .append(url).append("/ver/").append(selecionado.getId()).append("/").toString());
    }

    public void encerrarProcesso() {
        try {
            validarEncerramentoProcesso();
            ResultadoValidacao resultado = processoDebitoFacade.encerrarProcesso(selecionado, processoDebitoFacade.getSistemaFacade().getUsuarioCorrente());
            if (resultado.isValidado()) {
                processoDebitoFacade.executarDependenciasDoProcesso(selecionado);
            }
            Web.navegacao(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/", getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada("Processo de Débitos processado com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Não foi possível continuar!", "Ocorreu um erro ao processar: " + e.getMessage());
        }
    }

    private void validarEncerramentoProcesso() {
        ValidacaoException ve = new ValidacaoException();
        if (TipoProcessoDebito.PRESCRICAO.equals(selecionado.getTipo())) {
            selecionado = processoDebitoFacade.recuperarItensDoProcesso(selecionado.getId());
            for (ItemProcessoDebito item : selecionado.getItens()) {
                if (item.getParcela().isDebitoProtestado()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido efetivar um processo de prescrição com débito protestado!");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    public void limpaEstorno() {
        selecionado.setMotivoEstorno(null);
    }

    public void validarEstornoProcesso() {
        try {
            validarEstorno();
            iniciarEstornoProcesso();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Não foi possível continuar!", "Ocorreu um erro ao salvar: " + e.getMessage());
        }
    }

    public void iniciarCancelamentoProcesso() {
        try {
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            futureEstorno = processoDebitoFacade.estornar(assistenteBarraProgresso, selecionado, SituacaoProcessoDebito.CANCELADO);
            FacesUtil.executaJavaScript("pollEstorno.start()");
            FacesUtil.executaJavaScript("openDialog(dlgEstorno)");
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Não foi possível continuar!", "Ocorreu um erro ao salvar: " + e.getMessage());
        }
    }

    public void iniciarEstornoProcesso() {
        try {
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            futureEstorno = processoDebitoFacade.estornar(assistenteBarraProgresso, selecionado, SituacaoProcessoDebito.ESTORNADO);
            FacesUtil.executaJavaScript("pollEstorno.start()");
            FacesUtil.executaJavaScript("openDialog(dlgEstorno)");
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Não foi possível continuar!", "Ocorreu um erro ao salvar: " + e.getMessage());
        }
    }

    public void acompanharEstornoProcesso() {
        if (futureEstorno.isDone()) {
            FacesUtil.executaJavaScript("pollEstorno.stop()");
            FacesUtil.executaJavaScript("closeDialog(dlgEstorno)");
            FacesUtil.executaJavaScript("rcFinalizarEstorno()");
        }
        if (futureEstorno.isCancelled()) {
            FacesUtil.executaJavaScript("pollEstorno.stop()");
            FacesUtil.executaJavaScript("closeDialog(dlgEstorno");
        }
    }

    public void finalizarEstornoProcesso() {
        try {
            ResultadoValidacao resultadoValidacao = futureEstorno.get();
            FacesUtil.printAllMessages(resultadoValidacao.getMensagens());
            if (resultadoValidacao.isValidado()) {
                processoDebitoFacade.executarDependenciasDoProcesso(selecionado);
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarEstorno() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getMotivoEstorno() == null || selecionado.getMotivoEstorno().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o motivo do estorno!");
        }
        if (selecionado.getDataEstorno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data do estorno!");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    @Override
    public void excluir() {
        if (selecionado.getSituacao().equals(selecionado.getSituacao().ESTORNADO) || (selecionado.getSituacao().equals(selecionado.getSituacao().FINALIZADO))) {
            FacesUtil.addError("O registro selecionado está finalizado e não pode ser excluido!", "");
        } else {
            try {
                getFacede().remover(selecionado);
                FacesUtil.addInfo("Registro excluído com sucesso!", "");
                cancelar();
            } catch (Exception e) {
                FacesUtil.addError("Não foi possível realizar a exclusão!", "");
            }
        }
    }

    public void adicionarItem(ParcelaValorDivida parcela) {
        FacesUtil.printAllMessages(processoDebitoFacade.adicionarItem(parcela, selecionado, novosItens, listaParcela).getMensagens());
    }

    public void adicionarParcelas() {
        if (resultados.length > 0) {
            try {
                validarParcelaParaAdicionar();
                for (ResultadoParcela resultadoParcela : resultados) {
                    processoDebitoFacade.adicionarItem(resultadoParcela, selecionado, novosItens, resultadoConsulta);
                }
                criarDataModelDosItens();
                FacesUtil.addOperacaoRealizada(resultados.length + " parcelas adicionadas ao Processo de Débitos!");
                FacesUtil.atualizarComponente("Formulario");
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        } else {
            FacesUtil.addOperacaoNaoRealizada("Nenhuma parcela foi selecionada!");
        }
    }

    private void validarParcelaParaAdicionar() {
        ValidacaoException ve = new ValidacaoException();
        for (ResultadoParcela resultado : resultados) {
            if (TipoProcessoDebito.PRESCRICAO.equals(selecionado.getTipo())) {
                if (resultado.getPrescricao().after(selecionado.getLancamento())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido adicionar parcela que tenha a Data de Prescrição maior que a Data de Lançamento!");
                }
                if (resultado.getDebitoProtestado()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido adicionar débito protestado!");
                }
            }
        }
        ve.lancarException();
    }

    public void removerItem(ItemProcessoDebito item) {
        if (item != null) {
            itensRemovidos.add(item);
            if (novosItens.contains(item)) {
                novosItens.remove(item);
            }
        }
    }

    public void pesquisar() {
        try {
            validarCamposPreenchidos();
            resultadoConsulta.clear();
            if (tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA)) {
                ConsultaParcela consulta = new ConsultaParcela();
                List<BigDecimal> cadastros = cadastroFacade.buscarIdsCadastrosAtivosDaPessoa(filtroContribuinte, false);
                if (!cadastros.isEmpty()) {
                    adicionarParametro(consulta);
                    consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IN, cadastros);
                    consulta.executaConsulta();
                    resultadoConsulta.addAll(consulta.getResultados());
                }
                consulta = new ConsultaParcela();
                adicionarParametro(consulta);
                consulta.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, filtroContribuinte.getId());
                consulta.addComplementoDoWhere(" and vw.cadastro_id is null");
                consulta.executaConsulta();
                resultadoConsulta.addAll(consulta.getResultados());
                Collections.sort(resultadoConsulta, ResultadoParcela.getComparatorByValuePadrao());

            } else {
                ConsultaParcela consulta = new ConsultaParcela();
                adicionarParametro(consulta);
                consulta.executaConsulta();
                resultadoConsulta.addAll(consulta.getResultados());
                Collections.sort(resultadoConsulta, ResultadoParcela.getComparatorByValuePadraoSemCadastro());
            }
            if (!resultadoConsulta.isEmpty() && TipoProcessoDebito.PRESCRICAO.equals(selecionado.getTipo())) {
                List<ResultadoParcela> resultadoConsultaPrescrito = new ArrayList<>();
                for (ResultadoParcela resultadoParcela : resultadoConsulta) {
                    if (!resultadoParcela.getDebitoProtestado()) {
                        if (resultadoParcela.getPrescricao() != null && resultadoParcela.getPrescricao().before(processoDebitoFacade.getSistemaFacade().getDataOperacao())) {
                            resultadoConsultaPrescrito.add(resultadoParcela);
                        }
                    }
                }
                resultadoConsulta = resultadoConsultaPrescrito;
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        try {
            if (resultadoConsulta.isEmpty()) {
                FacesUtil.addError("A pesquisa não encontrou nenhum Débito!", "");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Impossível continuar", e.getMessage());
        }
    }


    public ConsultaParcela adicionarParametro(ConsultaParcela consulta) {
        if (filtroExercicioInicio != null && filtroExercicioFinal != null) {
            consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MAIOR_IGUAL, filtroExercicioInicio.getAno());
            consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MENOR_IGUAL, filtroExercicioFinal.getAno());
        }
        if (cadastro != null && cadastro.getId() != null) {
            consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, cadastro.getId());
        }

        if (filtroDivida != null) {
            consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, filtroDivida.getId());
        }

        if (selecionado.getTipo().equals(TipoProcessoDebito.PRESCRICAO)) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.DIFERENTE, 1);

        } else {
            if (dividaDoExercicio && !dividaAtiva && !dividaAtivaAzuijada) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 0);
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 0);
            } else if (dividaDoExercicio && dividaAtiva && !dividaAtivaAzuijada) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 0);
            } else if (dividaDoExercicio && !dividaAtiva && dividaAtivaAzuijada) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 0);
            } else if (!dividaDoExercicio && dividaAtiva && dividaAtivaAzuijada) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 1);
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 1);
            } else if (!dividaDoExercicio && dividaAtiva && !dividaAtivaAzuijada) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 1);
            } else if (!dividaDoExercicio && !dividaAtiva && dividaAtivaAzuijada) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 1);
            }
        }

        if (vencimentoInicial != null && vencimentoFinal != null) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR_IGUAL, vencimentoFinal);
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MAIOR_IGUAL, vencimentoInicial);
        }
        consulta.addOrdem(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Ordem.TipoOrdem.ASC).addOrdem(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Ordem.TipoOrdem.ASC);
        if (TipoProcessoDebito.REATIVACAO.equals(selecionado.getTipo())) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, Lists.newArrayList(SituacaoParcela.SUSPENSAO, SituacaoParcela.SUSPENSAO_LEI));
        } else {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        }
        return consulta;
    }

    public void inicializarFiltros() {
        filtroCodigoBarras = null;
        filtroContribuinte = null;
        filtroNumeroDAM = null;
        listaParcela = Lists.newArrayList();
        resultadoConsulta = Lists.newArrayList();
        filtroDivida = null;
        filtroExercicioFinal = null;
        filtroExercicioInicio = null;
        dividaAtiva = false;
        dividaAtivaAzuijada = false;
        dividaDoExercicio = false;
    }

    private void validarCamposPreenchidos() {
        ValidacaoException ve = new ValidacaoException();
        if (tipoCadastroTributario == null && cadastro == null && filtroContribuinte == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o tipo de cadastro.");
        } else if ((filtroContribuinte == null || filtroContribuinte.getId() == null)
            && (tipoCadastroTributario == null)
            && (cadastro == null || cadastro.getId() != null)
            && (filtroCodigoBarras == null || filtroCodigoBarras.trim().isEmpty())
            && (filtroNumeroDAM == null || filtroNumeroDAM.trim().isEmpty())
            && (filtroDivida == null || filtroDivida.getId() == null)
            && (filtroExercicioInicio == null || filtroExercicioInicio.getId() == null)
            && (filtroExercicioFinal == null || filtroExercicioFinal.getId() == null)
            && !dividaAtiva
            && !dividaAtivaAzuijada
            && !dividaDoExercicio) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos um filtro.");
        } else if ((cadastro == null && filtroContribuinte == null) && tipoCadastroTributario != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Quando informado o tipo de cadastro, é necessário que seja informado também o cadastro.");
        } else if (filtroExercicioInicio != null && filtroExercicioFinal != null) {
            if (filtroExercicioFinal.getAno().compareTo(filtroExercicioInicio.getAno()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Exercício final não pode ser posterior ao exercício inicial.");
            }
        } else if (filtroExercicioInicio != null && filtroExercicioFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Exercício final deve ser informado.");
        } else if (filtroExercicioInicio == null && filtroExercicioFinal != null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Exercício inicial deve ser informado.");
        } else if (vencimentoInicial != null && vencimentoFinal != null) {
            if (vencimentoInicial.compareTo(vencimentoFinal) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Vencimento Final não pode ser maior que a Data de Vencimento Inicial.");
            }
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }


    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricaoLonga()));
        }
        return toReturn;
    }

    public boolean habilitarBotaoSalvar() {
        inicializarFiltros();
        return selecionado.getSituacao() == null || selecionado.getSituacao().equals(SituacaoProcessoDebito.EM_ABERTO);
    }

    public boolean naoProcessado() {
        return selecionado.getSituacao() != null && selecionado.getSituacao().equals(SituacaoProcessoDebito.EM_ABERTO) && selecionado.getId() != null;
    }

    public boolean habilitarBotaoEstornar() {
        return selecionado.getSituacao() != null
            && selecionado.getSituacao().equals(SituacaoProcessoDebito.FINALIZADO)
            && selecionado.getId() != null
            && selecionado.getValido();
    }

    public void setaInscricaoCadastro() {
        cadastro = null;
        filtroContribuinte = null;
        setSituacaoEscolhidaCadastroImobiliario(true);
        setSituacaoEscolhidaCadastroRural(true);
    }

    public SituacaoCadastralCadastroEconomico getSituacaoEscolhidaCadastroEconomico() {
        return situacaoEscolhidaCadastroEconomico;
    }

    public void setSituacaoEscolhidaCadastroEconomico(SituacaoCadastralCadastroEconomico situacaoEscolhidaCadastroEconomico) {
        this.situacaoEscolhidaCadastroEconomico = situacaoEscolhidaCadastroEconomico;
    }

    public Boolean getSituacaoEscolhidaCadastroImobiliario() {
        return situacaoEscolhidaCadastroImobiliario;
    }

    public void setSituacaoEscolhidaCadastroImobiliario(Boolean situacaoEscolhidaCadastroImobiliario) {
        this.situacaoEscolhidaCadastroImobiliario = situacaoEscolhidaCadastroImobiliario;
    }

    public Boolean getSituacaoEscolhidaCadastroRural() {
        return situacaoEscolhidaCadastroRural;
    }

    public void setSituacaoEscolhidaCadastroRural(Boolean situacaoEscolhidaCadastroRural) {
        this.situacaoEscolhidaCadastroRural = situacaoEscolhidaCadastroRural;
    }

    public SituacaoCadastralPessoa getSituacaoEscolhidaCadastroPessoa() {
        return situacaoEscolhidaCadastroPessoa;
    }

    public void setSituacaoEscolhidaCadastroPessoa(SituacaoCadastralPessoa situacaoEscolhidaCadastroPessoa) {
        this.situacaoEscolhidaCadastroPessoa = situacaoEscolhidaCadastroPessoa;
    }

    public List<SelectItem> getSituacoesCadastroImobiliario() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, "Todos"));
        retorno.add(new SelectItem(true, "Ativo"));
        retorno.add(new SelectItem(false, "Inativo"));
        return retorno;
    }

    public List<SelectItem> getSituacoesCadastroEconomico() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, "Todos"));
        for (SituacaoCadastralCadastroEconomico sit : SituacaoCadastralCadastroEconomico.values()) {
            retorno.add(new SelectItem(sit, sit.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getSituacoesCadastroRural() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, "Todos"));
        retorno.add(new SelectItem(true, "Ativo"));
        retorno.add(new SelectItem(false, "Inativo"));
        return retorno;
    }

    public List<SelectItem> getSituacoesPessoa() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, "Todos"));
        for (SituacaoCadastralPessoa sit : SituacaoCadastralPessoa.values()) {
            retorno.add(new SelectItem(sit, sit.getDescricao()));
        }
        return retorno;
    }

    public List<CadastroImobiliario> completarCadastroImobiliario(String parte) {
        return jdbcCadastroImobiliarioDAO.lista(parte.trim(), getSituacaoEscolhidaCadastroImobiliario());
    }

    public List<CadastroEconomico> completarCadastroEconomico(String parte) {
        if (situacaoEscolhidaCadastroEconomico == null) {
            return cadastroEconomicoFacade.listaCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJPorSituacao(parte, SituacaoCadastralCadastroEconomico.values());
        }
        return cadastroEconomicoFacade.listaCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJPorSituacao(parte, situacaoEscolhidaCadastroEconomico);
    }

    public List<CadastroRural> completarCadastroRural(String parte) {
        return jdbcCadastroRuralDAO.lista(parte.trim(), getSituacaoEscolhidaCadastroRural());
    }

    public List<SituacaoCadastralPessoa> getSituacoesDisponiveis() {
        return Lists.newArrayList(getSituacaoEscolhidaCadastroPessoa());
    }

    public String buscarInscricaoDoCadastro(ParcelaValorDivida parcela) {
        return processoDebitoFacade.getConsultaDebitoFacade().buscarNumeroTipoCadastroPorParcela(parcela);
    }

    public String retornarSituacaoDaDivida(ParcelaValorDivida parcela) {
        if (parcela == null) {
            return " - ";
        }
        if (parcela.isDividaAtiva()) {
            return DIVIDA_ATIVA;
        }
        if (parcela.isDividaAtivaAjuizada()) {
            return DIVIDA_ATIVA_AJUIZADA;
        }
        return DO_EXERCICIO;
    }

    public String recuperarUltimaSituacao(ParcelaValorDivida parcela) {
        SituacaoParcelaValorDivida ultimaSituacao = processoDebitoFacade.getConsultaDebitoFacade().getUltimaSituacao(parcela);
        return ultimaSituacao.getSituacaoParcela().getDescricao();
    }

    public BigDecimal recuperarSaldoUltimaSituacao(ParcelaValorDivida parcela) {
        SituacaoParcelaValorDivida ultimaSituacao = processoDebitoFacade.getConsultaDebitoFacade().getUltimaSituacao(parcela);
        return ultimaSituacao.getSaldo();
    }

    public BigDecimal recuperarValorTotal(ParcelaValorDivida parcela) {
        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, parcela.getId());
        consulta.executaConsulta();
        List<ResultadoParcela> resultadoParcelas = consulta.getResultados();
        return resultadoParcelas.get(0).getValorTotal();
    }

    public SelectItem[] retornaTipoDeSituacaoDaDivida() {
        SelectItem retorno[] = new SelectItem[4];
        retorno[0] = new SelectItem("", "TODOS");
        retorno[1] = new SelectItem(DIVIDA_ATIVA, DIVIDA_ATIVA);
        retorno[2] = new SelectItem(DIVIDA_ATIVA_AJUIZADA, DIVIDA_ATIVA_AJUIZADA);
        retorno[3] = new SelectItem(DO_EXERCICIO, DO_EXERCICIO);
        return retorno;
    }

    public SituacaoCadastroEconomico getSituacaoCadastroEconomico() {
        if (cadastro != null) {
            return cadastroEconomicoFacade.recuperarUltimaSituacaoDoCadastroEconomico((CadastroEconomico) cadastro);
        } else {
            return new SituacaoCadastroEconomico();
        }
    }

    public Testada getTestadaPrincipal() {
        if (cadastro != null) {
            return loteFacade.recuperarTestadaPrincipal(((CadastroImobiliario) cadastro).getLote());
        } else {
            return new Testada();
        }
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return cadastroImobiliarioFacade.listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public ConverterAutoComplete getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, cadastroImobiliarioFacade);
        }
        return converterCadastroImobiliario;
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return cadastroEconomicoFacade.buscarCadastrosPorInscricaoOrCpfCnpjOrNome(parte.trim());
    }

    public ConverterAutoComplete getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, cadastroEconomicoFacade);
        }
        return converterCadastroEconomico;
    }

    public List<CadastroRural> completaCadastroRural(String parte) {
        return cadastroRuralFacade.listaFiltrandoPorCodigo(parte.trim());
    }

    public ConverterAutoComplete getConverterCadastroRural() {
        if (converterCadastroRural == null) {
            converterCadastroRural = new ConverterAutoComplete(CadastroRural.class, cadastroRuralFacade);
        }
        return converterCadastroRural;
    }

    public List<ContratoRendasPatrimoniais> completaContratolRendasPatrimonial(String parte) {
        return contratoRendasPatrimoniaisFacade.listaFiltrando(parte.trim(), "numeroContrato");
    }

    public ConverterAutoComplete getConverterContratoRendasPatrimoniais() {
        if (converterCadastroRendasPatrimoniais == null) {
            converterCadastroRendasPatrimoniais = new ConverterAutoComplete(ContratoRendasPatrimoniais.class, contratoRendasPatrimoniaisFacade);
        }
        return converterCadastroRendasPatrimoniais;
    }

    public List<Pessoa> recuperaPessoasCadastro() {
        List<Pessoa> retorno = new ArrayList<Pessoa>();
        if (cadastro instanceof CadastroImobiliario) {
            CadastroImobiliario cadastroIm = cadastroImobiliarioFacade.recuperar(cadastro.getId());
            for (Propriedade p : cadastroIm.getPropriedade()) {
                retorno.add(p.getPessoa());
            }
        }
        if (cadastro instanceof CadastroRural) {
            CadastroRural cadastroRu = cadastroRuralFacade.recuperar(cadastro.getId());
            for (PropriedadeRural p : cadastroRu.getPropriedade()) {
                retorno.add(p.getPessoa());
            }
        }
        if (cadastro instanceof CadastroEconomico) {
            CadastroEconomico cadastroEco = cadastroEconomicoFacade.recuperar(cadastro.getId());
            for (SociedadeCadastroEconomico sociedadeCadastroEconomico : cadastroEco.getSociedadeCadastroEconomico()) {
                retorno.add(sociedadeCadastroEconomico.getSocio());
            }
        }
        return retorno;
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, processoDebitoFacade.getAtoLegalFacade());
        }
        return converterAtoLegal;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return processoDebitoFacade.getAtoLegalFacade().listaFiltrando(parte.trim(), "nome");
    }

    public void alterarSituacaoParcela() {
        if (cadastro != null || filtroContribuinte != null) {
            if (!selecionado.getTipo().equals(TipoProcessoDebito.REATIVACAO)) {
                situacaoParcela = SituacaoParcela.EM_ABERTO;
            } else {
                situacaoParcela = SituacaoParcela.SUSPENSAO;
            }
            FacesUtil.executaJavaScript("dialogo.show()");
        } else {
            FacesUtil.addCampoObrigatorio("Informe o cadastro!");
        }
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        this.setCadastro((CadastroEconomico) obj);
    }

    public void imprimir() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeRelatorio("Processo de " + selecionado.getTipo().getDescricao() + " de Débitos");
            dto.adicionarParametro("USUARIO", processoDebitoFacade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "PREFEITURA DE RIO BRANCO");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA DE FINANÇAS");
            dto.adicionarParametro("TITULO", "Processo de " + selecionado.getTipo().getDescricao() + " de Débitos");
            dto.adicionarParametro("ID", selecionado.getId());
            dto.setApi("tributario/processo-debito/");
            ReportService.getInstance().gerarRelatorio(processoDebitoFacade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException onpe) {
            FacesUtil.printAllFacesMessages(onpe.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }
}
