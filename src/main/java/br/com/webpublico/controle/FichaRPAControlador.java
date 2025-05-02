/*
 * Codigo gerado automaticamente em Wed Jan 04 11:23:36 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ExecutaScript;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.script.ExecutaScriptImpl;
import br.com.webpublico.script.ItemErroScript;
import br.com.webpublico.script.rh.ExecutaScriptEventoFPImpl;
import br.com.webpublico.script.rh.GeraBibliotecaJavaScriptFP;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

@ManagedBean(name = "fichaRPAControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoFichaRPA", pattern = "/rh/ficha-rpa/novo/", viewId = "/faces/rh/administracaodepagamento/ficharpa/edita.xhtml"),
    @URLMapping(id = "listaFichaRPA", pattern = "/rh/ficha-rpa/listar/", viewId = "/faces/rh/administracaodepagamento/ficharpa/lista.xhtml"),
    @URLMapping(id = "verFichaRPA", pattern = "/rh/ficha-rpa/ver/#{fichaRPAControlador.id}/", viewId = "/faces/rh/administracaodepagamento/ficharpa/visualizar.xhtml"),
    @URLMapping(id = "editarFichaRPA", pattern = "/rh/ficha-rpa/editar/#{fichaRPAControlador.id}/", viewId = "/faces/rh/administracaodepagamento/ficharpa/edita.xhtml"),
})
public class FichaRPAControlador extends PrettyControlador<FichaRPA> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(FichaRPAControlador.class);

    @EJB
    private FichaRPAFacade fichaRPAFacade;
    @EJB
    private PrestadorServicosFacade prestadorServicosFacade;
    private ConverterAutoComplete converterPrestadorServicos;
    @EJB
    private FuncoesFichaRPA funcoesFichaRPA;
    private GeraBibliotecaJavaScriptFP geraBibliotecaJavaScript;
    private ArrayList<ItemErroScript> itemErroScripts;
    private List<String> funcoesJavaScript;
    @EJB
    private FormulaPadraoFPFacade formulaPadraoFPFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private BaseFPFacade baseFacade;
    @EJB
    private FuncoesFolhaFacade funcoesFolhaFacade;
    private ConverterAutoComplete converterTomador;
    @EJB
    private TomadorDeServicoFacade tomadorFacade;
    private PercentualConverter percentualConverter;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    private EconomicoCNAE economicoCNAE;
    @EJB
    private PessoaFacade pessoaFacade;
    private ItemFichaRPA itemFichaRPA;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    private List<String> inconsitencias;
    private boolean habilitarSalvar;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private List<ItemFichaRPA> itensFicha;
    private List<ItemFichaRPA> itensFichaRPAExcluidos;

    public FichaRPAControlador() {
        super(FichaRPA.class);
    }

    public FichaRPAFacade getFacade() {
        return fichaRPAFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return fichaRPAFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/ficha-rpa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<PrestadorServicos> completaPrestadorServicos(String parte) {
        return prestadorServicosFacade.listaPrestadoresPorUsuarioFiltrando(parte.trim(), sistemaFacade.getUsuarioCorrente(), sistemaFacade.getDataOperacao());
    }

    @URLAction(mappingId = "novoFichaRPA", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setNumero(fichaRPAFacade.retornaUltimoCodigo());
        selecionado.setItemFichaRPAs(new ArrayList<ItemFichaRPA>());
        selecionado.setGeradoEm(sistemaFacade.getDataOperacao());
        inconsitencias = Lists.newArrayList();
        habilitarSalvar = false;
        itensFicha = Lists.newArrayList();
        itensFichaRPAExcluidos = Lists.newArrayList();
    }

    @URLAction(mappingId = "verFichaRPA", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public Converter getConverterPrestadorServicos() {
        if (converterPrestadorServicos == null) {
            converterPrestadorServicos = new ConverterAutoComplete(PrestadorServicos.class, prestadorServicosFacade);
        }
        return converterPrestadorServicos;
    }

    public List<TomadorDeServico> completaTomador(String parte) {
        return tomadorFacade.listaFiltrandoAtributosTomador(parte.trim(), "nomeFantasia", "razaoSocial");
    }

    public Converter getConverterTomador() {
        if (converterTomador == null) {
            converterTomador = new ConverterAutoComplete(TomadorDeServico.class, tomadorFacade);
        }
        return converterTomador;
    }

    public void gerarRPA(boolean validar) {
        long tempoInicial = System.currentTimeMillis();
        geraBibliotecaJavaScript = new GeraBibliotecaJavaScriptFP();
        itemErroScripts = new ArrayList<ItemErroScript>();
        funcoesJavaScript = new ArrayList<String>();
        try {
            validarCamposFichaRPA();
            if (!hasInconsistencias(validar)) {
                String msg = "";
                String novoCodigo = fichaRPAFacade.retornaUltimoCodigo();
                if (!novoCodigo.equals(selecionado.getNumero())) {
                    msg = "O Número " + selecionado.getNumero() + " já está sendo usado e foi gerado o novo número " + novoCodigo + " !";
                    selecionado.setNumero(novoCodigo);
                }
                selecionado.setValorLiquido(selecionado.getValor());
                String libJavaScript = geraBibliotecaJavaScript.gerar(eventoFPFacade.recuperarEventosAtivosRHPorTipoExecucao(TipoExecucaoEP.RPA), formulaPadraoFPFacade.lista(), baseFacade.buscarBasesComItens(), TipoExecucaoEP.RPA);
                try {
                    logger.debug("Biblioteca: {0}", libJavaScript);

                    java.time.LocalDate mesAno = DataUtil.dateToLocalDate(selecionado.getDataServico());
                    selecionado.getPrestadorServicos().setMes(mesAno.getMonthValue());
                    selecionado.getPrestadorServicos().setAno(mesAno.getYear());
                    FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador = new FolhaDePagamentoNovoCalculador(funcoesFolhaFacade, folhaDePagamentoFacade);
                    FolhaDePagamento folhaDePagamento = criarFolhaDePagamenetoFake(selecionado);
                    selecionado.getPrestadorServicos().setFolha(folhaDePagamento);
                    folhaDePagamentoNovoCalculador.setFolhaDePagamento(folhaDePagamento);
                    folhaDePagamentoNovoCalculador.setEp(selecionado.getPrestadorServicos());
                    selecionado = fichaRPAFacade.salvarRetornando(selecionado);
                    FichaRPACalculador fichaRPACalculador = new FichaRPACalculador(selecionado.getPrestadorServicos(), folhaDePagamentoNovoCalculador, funcoesFolhaFacade, selecionado, mesAno, funcoesFichaRPA);

                    ScriptEngine engine = fichaRPACalculador.getEngine();
                    engine.eval(libJavaScript);
                    Invocable jsInvoke = (Invocable) engine;

                    ExecutaScript executaScript = new ExecutaScriptImpl(jsInvoke);
                    ExecutaScriptEventoFPImpl instance = new ExecutaScriptEventoFPImpl(executaScript);
                    selecionado = fichaRPAFacade.gerarFichaRPA(selecionado.getPrestadorServicos(), selecionado, instance, itemErroScripts);
                    FacesUtil.addInfo("RPA Gerado", "O Recibo de Pagamento de Autônomo foi Gerado! " + msg);
                } catch (ScriptException ex) {
                    FacesUtil.addFatal("Erro ao avaliar fórmula dos eventos", "Impossível calcular rpa, por favor verifique os scripts dos eventos, mensagem da exeção gerada: " + ex.getMessage());
                    StringTokenizer stringToken = new StringTokenizer(libJavaScript, "\n");
                    int total = stringToken.countTokens();
                    for (int i = 0; i < total; i++) {
                        funcoesJavaScript.add(stringToken.nextElement().toString());
                    }
                    for (ItemErroScript item : itemErroScripts) {
                        FacesUtil.addFatal("Erro ao gerar RPA", "Erro ao executar geração do RPA: " + item.getNomeFunction() + " - " + item.getMensagem() + " - " + item.getMensagemException());
                    }
                } catch (RuntimeException re) {
                    for (ItemErroScript item : itemErroScripts) {
                        FacesUtil.addFatal("Erro ao tentar gerar o RPA", "Erro: " + item.getNomeFunction() + " - " + item.getMensagem() + " - " + item.getMensagemException());
                    }
                    FacesUtil.addFatal("Erro ao avaliar fórmula dos eventos", "Erro ao executar geração do RPA: " + re.getCause().getMessage());
                } catch (Exception e) {
                    FacesUtil.addFatal("Erro ao avaliar fórmula dos eventos", "Erro ao executar geração do RPA.");
                }
                logger.debug("Tempo de processamento do RPA: {0}", (System.currentTimeMillis() - tempoInicial));
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId());
            }
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private boolean hasInconsistencias(Boolean validar) {
        if (validar && selecionado.getDataServico() != null) {
            DateTime mesAno = new DateTime(selecionado.getDataServico());
            List<FichaRPA> fichaRPAS = fichaRPAFacade.buscarRPAsPorMesAndAno(selecionado.getPrestadorServicos(), mesAno.getMonthOfYear(), mesAno.getYear());
            if (fichaRPAS != null && !fichaRPAS.isEmpty()) {
                for (FichaRPA fichaRPA : fichaRPAS) {
                    if (selecionado.getId() != null && fichaRPA.getId().equals(selecionado.getId())) {
                        continue;
                    }
                    inconsitencias.add("Encontrada outra prestação de serviço em " + DataUtil.getDataFormatada(fichaRPA.getDataServico()) + " no valor de R$" + Util.reaisToString(fichaRPA.getValor()) + ". Valores de INSS e IRRF serão acumulados.");
                }

            }
            if (!inconsitencias.isEmpty()) {
                FacesUtil.executaJavaScript("dialogConfirmarRPA.show()");
                return true;
            }
        }
        return false;
    }

    private FolhaDePagamento criarFolhaDePagamenetoFake(FichaRPA selecionado) {
        FolhaDePagamento folha = new FolhaDePagamento();
        DateTime dateTime = new DateTime(selecionado.getDataServico());
        folha.setMes(Mes.getMesToInt(dateTime.getMonthOfYear()));
        folha.setAno(dateTime.getYear());
        folha.setTipoFolhaDePagamento(TipoFolhaDePagamento.NORMAL);
        return folha;
    }

    @URLAction(mappingId = "editarFichaRPA", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        if (!selecionado.getItemFichaRPAs().isEmpty()) {
            Collections.sort(selecionado.getItemFichaRPAs());
            recuperarItensFichaRPAParaExibir();
        }
        itensFichaRPAExcluidos = Lists.newArrayList();
        inconsitencias = Lists.newArrayList();
        habilitarSalvar = false;
    }

    private void recuperarItensFichaRPAParaExibir() {
        itensFicha = Lists.newArrayList();
        selecionado.getItemFichaRPAs().forEach(item -> {
            if (!TipoEventoFP.INFORMATIVO.equals(item.getEventoFP().getTipoEventoFP()) || (TipoEventoFP.INFORMATIVO.equals(item.getEventoFP().getTipoEventoFP()) && item.getEventoFP().getExibirNaFichaRPA())) {
                itensFicha.add(item);
            }
        });
    }

    public PercentualConverter getPercentualConverter() {
        if (percentualConverter == null) {
            percentualConverter = new PercentualConverter();
        }
        return percentualConverter;
    }

    public void novoItemFicha() {
        itemFichaRPA = new ItemFichaRPA();
    }

    public void adicionaEvento() {
        try {
            validarEvento();
            itemFichaRPA.setFichaRPA(selecionado);
            Util.adicionarObjetoEmLista(itensFicha, itemFichaRPA);
            cancelarEvento();
            habilitarSalvar = true;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarEvento() {
        this.itemFichaRPA = null;
    }

    public void selecionarEvento(ItemFichaRPA evento) {
        itemFichaRPA = (ItemFichaRPA) Util.clonarObjeto(evento);
    }

    public void removerEvento(ItemFichaRPA item) {
        if (itensFicha.contains(item)) {
            itensFicha.remove(item);
            itensFichaRPAExcluidos.add(item);
            habilitarSalvar = true;
        }
    }

    private void validarEvento() {
        ValidacaoException ve = new ValidacaoException();
        if (itemFichaRPA.getValor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor é obrigatório.");
        }
        if (itemFichaRPA.getValorBaseDeCalculo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor Base é obrigatório.");
        }
        if (itemFichaRPA.getValorReferencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor Referência é obrigatório.");
        }
        if (itemFichaRPA.getEventoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo evento é obrigatório.");
        }
        if (itemFichaRPA.getEventoFP() != null && !itemFichaRPA.getEventoFP().getAtivo()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não será possível utilizar essa verba pois ela está inativada.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public List<SelectItem> getEventosRPA() {
        return Util.getListSelectItem(eventoFPFacade.listaEventosRH(TipoExecucaoEP.RPA), false);
    }

    public EconomicoCNAE getEconomicoCNAE() {
        return economicoCNAE;
    }

    public void setEconomicoCNAE(EconomicoCNAE economicoCNAE) {
        this.economicoCNAE = economicoCNAE;
    }

    public void onRowSelect(SelectEvent event) {
        economicoCNAE = ((EconomicoCNAE) event.getObject());
    }

    public List<EconomicoCNAE> getListaEconomicoCNAE() {
        if (selecionado == null || selecionado.getPrestadorServicos() == null) {
            return new ArrayList<EconomicoCNAE>();
        } else {
            return cadastroEconomicoFacade.listaFiltrandoPessoa(selecionado.getPrestadorServicos().getPrestador());
        }
    }

    public ItemFichaRPA getItemFichaRPA() {
        return itemFichaRPA;
    }

    public void setItemFichaRPA(ItemFichaRPA itemFichaRPA) {
        this.itemFichaRPA = itemFichaRPA;
    }

    public List<String> getInconsitencias() {
        return inconsitencias;
    }

    public void setInconsitencias(List<String> inconsitencias) {
        this.inconsitencias = inconsitencias;
    }

    public boolean isHabilitarSalvar() {
        return habilitarSalvar;
    }

    public void setHabilitarSalvar(boolean habilitarSalvar) {
        this.habilitarSalvar = habilitarSalvar;
    }

    public List<ItemFichaRPA> getItensFicha() {
        return itensFicha;
    }

    public void setItensFicha(List<ItemFichaRPA> itensFicha) {
        this.itensFicha = itensFicha;
    }

    @Override
    public void salvar() {
        try {
            validarCamposFichaRPA();
            selecionado.setEconomicoCNAE(economicoCNAE);
            itensFicha.forEach(item -> {
                Util.adicionarObjetoEmLista(selecionado.getItemFichaRPAs(), item);
            });
            itensFichaRPAExcluidos.forEach(itemExcluido -> {
                selecionado.getItemFichaRPAs().remove(itemExcluido);
            });

            calcularValorLiquido();
            super.salvar(Redirecionar.VER);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCamposFichaRPA() {
        ValidacaoException ve = new ValidacaoException();
        inconsitencias = Lists.newArrayList();
        if (selecionado.getValor() == null || selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor da ficha RPA deve ser maior que zero(0).");
        }
        if (selecionado.getPrestadorServicos() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Prestador de serviços deve ser informado.");
        }
        if (selecionado.getGeradoEm() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de geração deve ser informado.");
        }
        if (selecionado.getDataServico() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data do Serviço deve ser informado.");
        }
        if (selecionado.getTomador() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tomador deve ser informado.");
        }
        if (Strings.isNullOrEmpty(selecionado.getDescricao())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Descrição deve ser informado.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void calcularValorLiquido() {
        BigDecimal desconto = BigDecimal.ZERO;
        for (ItemFichaRPA fichaRPA : selecionado.getItemFichaRPAs()) {
            if (TipoEventoFP.DESCONTO.equals(fichaRPA.getEventoFP().getTipoEventoFP())) {
                desconto = desconto.add(fichaRPA.getValor());
            }
        }
        selecionado.setValorLiquido(selecionado.getValor().subtract(desconto));
    }

    public BigDecimal getValorTotalVantagem() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemFichaRPA fichaRPA : selecionado.getItemFichaRPAs()) {
            if (fichaRPA.getEventoFP().getTipoEventoFP().equals(TipoEventoFP.VANTAGEM)) {
                total = total.add(fichaRPA.getValor());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalDesconto() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemFichaRPA fichaRPA : selecionado.getItemFichaRPAs()) {
            if (fichaRPA.getEventoFP().getTipoEventoFP().equals(TipoEventoFP.DESCONTO)) {
                total = total.add(fichaRPA.getValor());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalLiquido() {
        return getValorTotalVantagem().subtract(getValorTotalDesconto());
    }

    public HierarquiaOrganizacional getHierarquiaDaUnidade() {
        if (selecionado.getPrestadorServicos() != null && selecionado.getPrestadorServicos().getLotacao() != null) {
            return hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                selecionado.getPrestadorServicos().getLotacao(), sistemaFacade.getDataOperacao());
        }
        return null;
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeRelatorio("RELATÓRIO-FICHA-RPA");
            dto.adicionarParametro("NOMERELATORIO", "RECIBO DE PAGAMENTO DE AUTÔNOMO - RPA");
            dto.adicionarParametro("FICHA_ID", selecionado.getId());
            dto.setApi("rh/relatorio-ficha-rpa/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }
}
