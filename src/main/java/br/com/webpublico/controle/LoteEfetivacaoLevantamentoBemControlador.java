package br.com.webpublico.controle;

import br.com.webpublico.controle.administrativo.patrimonio.RelatorioDeLevantamentosEfetivadosControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoPatrimonio;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 04/12/13
 * Time: 16:05
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "loteEfetivacaoLevantamentoBemControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaEfetivacao", pattern = "/efetivacao-de-levantamento-de-bem/novo/", viewId = "/faces/administrativo/patrimonio/efetivacaolevantamentobem/edita.xhtml"),
    @URLMapping(id = "editarEfetivacao", pattern = "/efetivacao-de-levantamento-de-bem/editar/#{loteEfetivacaoLevantamentoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaolevantamentobem/edita.xhtml"),
    @URLMapping(id = "listarEfetivacao", pattern = "/efetivacao-de-levantamento-de-bem/listar/", viewId = "/faces/administrativo/patrimonio/efetivacaolevantamentobem/lista.xhtml"),
    @URLMapping(id = "verEfetivacao", pattern = "/efetivacao-de-levantamento-de-bem/ver/#{loteEfetivacaoLevantamentoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaolevantamentobem/visualizar.xhtml")
})
public class LoteEfetivacaoLevantamentoBemControlador extends PrettyControlador<LoteEfetivacaoLevantamentoBem> implements Serializable, CRUD {
    @EJB
    private LoteEfetivacaoLevantamentoBemFacade loteEfetivacaoLevantamentoBemFacade;
    @EJB
    private LevantamentoBensPatrimoniaisFacade levantamentoFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SingletonGeradorCodigoPatrimonio singletonGeradorCodigoPatrimonio;
    @EJB
    private GrupoObjetoCompraGrupoBemFacade grupoObjetoCompraGrupoBemFacade;
    @EJB
    private SaldoGrupoBemMovelFacade saldoGrupoBemMovelFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    private SistemaControlador sistemaControlador;
    private PesquisaLevantamento pesquisaLevantamento;
    private List<LevantamentoBemPatrimonial> levantamentosEncontrados;
    private Set<LevantamentoBemPatrimonial> levantamentosInconsistentes;
    private HierarquiaOrganizacional hierarquiaOrcamentaria;
    private HashMap<GrupoBem, BigDecimal> mapaSaldoValorAcumuladoOriginalItem;
    private HashMap<GrupoBem, BigDecimal> mapaSaldoValorAcumuladoDepreciacao;
    private HashMap<GrupoBem, BigDecimal> mapaValorTotalDosLevantamentosPorGrupo;
    private Map<GrupoBem, BigDecimal> mapaValorDepreciadoDoGrupo;
    private List<Future> futuresEfetivacao;
    private Future<List<LevantamentoBemPatrimonial>> futurePesquisaLevantamentos;
    private List<Future> futureLevantamentosEstorno;
    private boolean futureEfetivacaoConcluida;
    private boolean futureEstornoConcluida;
    private AssistenteBarraProgressoLoteEfetivacaoBem assistenteBarraProgresso;
    private LazyDataModel<EfetivacaoLevantamentoBem> model;
    private FiltroEfetivacaoLevantamentoBem filtro;
    private RelatorioDeLevantamentosEfetivadosControlador relatorioDeLevantamentosEfetivadosControlador;

    public LoteEfetivacaoLevantamentoBemControlador() {
        super(LoteEfetivacaoLevantamentoBem.class);
        sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        relatorioDeLevantamentosEfetivadosControlador = (RelatorioDeLevantamentosEfetivadosControlador) Util.getControladorPeloNome("relatorioDeLevantamentosEfetivadosControlador");
    }

    @URLAction(mappingId = "novaEfetivacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarSelecionado();
        inicializarAtributos();
    }

    public void inicializarAtributos() {
        pesquisaLevantamento = new PesquisaLevantamento();
        levantamentosEncontrados = new ArrayList<>();
        levantamentosInconsistentes = new HashSet<>();
        futuresEfetivacao = Lists.newArrayList();
        futureLevantamentosEstorno = Lists.newArrayList();
        filtro = new FiltroEfetivacaoLevantamentoBem();
    }

    private void inicializarSelecionado() {
        selecionado.setDataEfetivacao(sistemaControlador.getDataOperacao());
        selecionado.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
    }

    @URLAction(mappingId = "verEfetivacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        inicializarAtributos();
        criarItensPaginacao();
        this.hierarquiaOrcamentaria = hierarquiaOrganizacionalFacade.retornarHierarquiaOrcamentariaPelaUnidadeOrcamentaria(selecionado.getUnidadeOrcamentaria(), selecionado.getDataEfetivacao());
    }

    @URLAction(mappingId = "editarEfetivacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarObjeto();
        inicializarAtributos();
        criarItensPaginacao();
        this.hierarquiaOrcamentaria = hierarquiaOrganizacionalFacade.retornarHierarquiaOrcamentariaPelaUnidadeOrcamentaria(selecionado.getUnidadeOrcamentaria(), selecionado.getDataEfetivacao());
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgressoLoteEfetivacaoBem assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public LazyDataModel<EfetivacaoLevantamentoBem> getModel() {
        return model;
    }

    public void setModel(LazyDataModel<EfetivacaoLevantamentoBem> model) {
        this.model = model;
    }

    public boolean isFutureEfetivacaoConcluida() {
        boolean terminou = true;
        futureEfetivacaoConcluida = false;
        if (futuresEfetivacao == null) {
            return false;
        }
        for (Future future : futuresEfetivacao) {
            if (!future.isDone()) {
                terminou = false;
            }
        }

        if (terminou && !futuresEfetivacao.isEmpty()) {
            if (AssistenteBarraProgressoLoteEfetivacaoBem.ProcessoEfetivacaoLevantamentoBens.CRIANDO_EFETIVACAO.equals(assistenteBarraProgresso.getProcessoEfetivacaoLevantamentoBens())) {
                salvarLoteEfetivacaoLevantamentoBem();
                return false;
            }
            if (AssistenteBarraProgressoLoteEfetivacaoBem.ProcessoEfetivacaoLevantamentoBens.PERSISTINDO_EFETIVACOES.equals(assistenteBarraProgresso.getProcessoEfetivacaoLevantamentoBens())) {
                criarEventoDepreciacaoBem();
                return false;
            }
            futureEfetivacaoConcluida = true;
            singletonGeradorCodigoPatrimonio.reset();
            singletonGeradorCodigoPatrimonio.desbloquear(LoteEfetivacaoLevantamentoBem.class);
        }
        return futureEfetivacaoConcluida;
    }

    public void setFutureEfetivacaoConcluida(boolean futureEfetivacaoConcluida) {
        this.futureEfetivacaoConcluida = futureEfetivacaoConcluida;
    }

    private void salvarLoteEfetivacaoLevantamentoBem() {

        int partes = assistenteBarraProgresso.getLote().getEfetivacoes().size() > 100 ? (assistenteBarraProgresso.getLote().getEfetivacoes().size() / 4) : assistenteBarraProgresso.getLote().getEfetivacoes().size();
        List<List<EfetivacaoLevantamentoBem>> efetivacoesParticionadas = Lists.partition(assistenteBarraProgresso.getLote().getEfetivacoes(), partes);
        assistenteBarraProgresso.zerarContadores();
        assistenteBarraProgresso.setDescricaoProcesso("Persistindo Efetivações...");
        assistenteBarraProgresso.setTotal(assistenteBarraProgresso.getLote().getEfetivacoes().size());
        assistenteBarraProgresso.getLote().setEfetivacoes(Lists.<EfetivacaoLevantamentoBem>newArrayList());
        for (List<EfetivacaoLevantamentoBem> efetivacoesParticionada : efetivacoesParticionadas) {
            futuresEfetivacao.add(loteEfetivacaoLevantamentoBemFacade.salvarLoteEfetivacaoLevantamentoBem(assistenteBarraProgresso,
                efetivacoesParticionada, assistenteBarraProgresso.getLote()));
        }
        assistenteBarraProgresso.setProcessoEfetivacaoLevantamentoBens(AssistenteBarraProgressoLoteEfetivacaoBem.ProcessoEfetivacaoLevantamentoBens.PERSISTINDO_EFETIVACOES);
    }

    private void criarEventoDepreciacaoBem() {
        assistenteBarraProgresso.setProcessoEfetivacaoLevantamentoBens(AssistenteBarraProgressoLoteEfetivacaoBem.ProcessoEfetivacaoLevantamentoBens.REGISTRANDO_DEPRESIACAO);
        assistenteBarraProgresso.setDescricaoProcesso("Preparando Efetivações para Depreciação...");
        List<Long> idsEfetivacao = loteEfetivacaoLevantamentoBemFacade.recuperarIdsEfetivacao(assistenteBarraProgresso.getLote());
        int partes = idsEfetivacao.size() > 100 ? (idsEfetivacao.size() / 4) : idsEfetivacao.size();
        List<List<Long>> idsParticionados = Lists.partition(idsEfetivacao, partes);
        assistenteBarraProgresso.zerarContadores();
        assistenteBarraProgresso.setDescricaoProcesso("Registrando depreciações das efetivações...");
        assistenteBarraProgresso.setTotal(idsEfetivacao.size());
        for (List<Long> ids : idsParticionados) {
            futuresEfetivacao.add(loteEfetivacaoLevantamentoBemFacade.criarEventoDepreciacaoBem(assistenteBarraProgresso,
                assistenteBarraProgresso.getLote(), ids));
        }
    }

    @Override
    public void salvar() {
        try {
            validarLoteEfetivacaoBem();
            validarCodigoPatrimonial();
            singletonGeradorCodigoPatrimonio.bloquearMovimento(LoteEfetivacaoLevantamentoBem.class);
            singletonGeradorCodigoPatrimonio.inicializarReset();

            assistenteBarraProgresso = new AssistenteBarraProgressoLoteEfetivacaoBem();

            int partes = levantamentosEncontrados.size() > 100 ? (levantamentosEncontrados.size() / 4) : levantamentosEncontrados.size();
            List<List<LevantamentoBemPatrimonial>> levantamentosParticionados = Lists.partition(levantamentosEncontrados, partes);
            if (selecionado.getCodigo() == null) {
                selecionado.setCodigo(singletonGeradorCodigo.getProximoCodigo(LoteEfetivacaoLevantamentoBem.class, "codigo"));
            }
            selecionado = loteEfetivacaoLevantamentoBemFacade.salvarLoteEfetivacaoLevantamento(selecionado);
            selecionado.setEfetivacoes(Lists.<EfetivacaoLevantamentoBem>newArrayList());
            assistenteBarraProgresso.setTotal(levantamentosEncontrados.size());

            for (List<LevantamentoBemPatrimonial> levantamentosParticionado : levantamentosParticionados) {
                futuresEfetivacao.add(loteEfetivacaoLevantamentoBemFacade.efetivarLoteLevantamentoBem(assistenteBarraProgresso,
                    selecionado, levantamentosParticionado));
            }
            FacesUtil.executaJavaScript("openDialog(dlgEfetivacao)");
            FacesUtil.executaJavaScript("acompanharEfetivacao()");
        } catch (ValidacaoException ve) {
            singletonGeradorCodigoPatrimonio.reset();
            singletonGeradorCodigoPatrimonio.desbloquear(LoteEfetivacaoLevantamentoBem.class);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            singletonGeradorCodigoPatrimonio.reset();
            singletonGeradorCodigoPatrimonio.desbloquear(LoteEfetivacaoLevantamentoBem.class);
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    /**
     * @throws ExcecaoNegocioGenerica verificar se existe código patrimonial somente se houver criação de bem,
     *                                a criação é na rotina assincrona realizada depois das validacoes
     */
    private void validarCodigoPatrimonial() throws ExcecaoNegocioGenerica {
        Entidade entidade = loteEfetivacaoLevantamentoBemFacade.getEntidadeFacade().recuperarEntidadePorUnidadeOrcamentaria(selecionado.getUnidadeOrcamentaria());
        ParametroPatrimonio parametroPatrimonio = loteEfetivacaoLevantamentoBemFacade.getParametroPatrimonioFacade().recuperarParametroPatrimonio();
        EntidadeSequenciaPropria sequenciaPropria = validarAtributosIniciais(entidade, parametroPatrimonio, TipoBem.MOVEIS);
        Long codigoBemOperacional = null;
        Long codigoBemInservivel = null;

        for (LevantamentoBemPatrimonial levantamento : levantamentosEncontrados) {
            if (levantamento.getBem() == null || levantamento.getBem().getId() == null || Strings.isNullOrEmpty(levantamento.getBem().getIdentificacao())) {
                if (EstadoConservacaoBem.OPERACIONAL.equals(levantamento.getEstadoConservacaoBem())) {
                    if (codigoBemOperacional == null) {
                        codigoBemOperacional = singletonGeradorCodigoPatrimonio.getProximoCodigo(entidade, TipoBem.MOVEIS, parametroPatrimonio);
                    }
                    codigoBemOperacional = codigoBemOperacional + 1;
                }
                if (EstadoConservacaoBem.INSERVIVEL.equals(levantamento.getEstadoConservacaoBem())) {
                    if (codigoBemInservivel == null) {
                        codigoBemInservivel = singletonGeradorCodigoPatrimonio.getProximoCodigoInservivel(entidade, TipoBem.MOVEIS, parametroPatrimonio);
                    }
                    codigoBemInservivel = codigoBemInservivel + 1;
                }
            }
        }
        if (codigoBemInservivel != null) {
            if (codigoBemInservivel >= parametroPatrimonio.getFaixaFinalParaInsevivel()) {
                throw new ExcecaoNegocioGenerica("A faixa de registros patrimoniais definida no parâmetro do patrimonio para bens inservivel chegou ao fim.");
            }
        }
        if (codigoBemOperacional != null) {
            if (codigoBemOperacional > Long.parseLong(sequenciaPropria.getFaixaFinal())) {
                throw new ExcecaoNegocioGenerica("A faixa para geração do código de identificação patrimônial definida no parâmetro do patrimônio para a entidade " + entidade.getNome() + " já chegou ao fim, defina uma faixa maior.");
            }
        }
        singletonGeradorCodigoPatrimonio.resetarMapas();
        singletonGeradorCodigoPatrimonio.desbloquear(LoteEfetivacaoLevantamentoBem.class);
    }

    private EntidadeSequenciaPropria validarAtributosIniciais(Entidade entidade, ParametroPatrimonio parametroPatrimonio, TipoBem tipoBem) {
        if (entidade == null) {
            throw new ExcecaoNegocioGenerica("A entidade não pode ser vazia. ");
        }

        if (loteEfetivacaoLevantamentoBemFacade.getParametroPatrimonioFacade().naoExisteParametro()) {
            throw new ExcecaoNegocioGenerica("Não existe parâmetro definido para o patrimônio. ");
        }
        EntidadeSequenciaPropria sequenciaPropria;
        if (tipoBem != null) {
            sequenciaPropria = loteEfetivacaoLevantamentoBemFacade.getParametroPatrimonioFacade().recuperarSequenciaPropriaPorTipoGeracao(entidade, tipoBem);
        } else {
            sequenciaPropria = loteEfetivacaoLevantamentoBemFacade.getParametroPatrimonioFacade().recuperarSequenciaPropria(entidade);
        }

        if (sequenciaPropria == null) {
            throw new ExcecaoNegocioGenerica("A entidade " + entidade.getNome() + " não possui uma sequência de geração do código de identificação patrimônial definida no parâmetro do patrimônio. ");
        }

        if (parametroPatrimonio.getFaixaInicialParaInsevivel() == null && parametroPatrimonio.getFaixaFinalParaInsevivel() == null) {
            throw new ExcecaoNegocioGenerica("Não foi definido no parâmetro do patrimonio uma faixa para bens inservíveis.");
        }
        return sequenciaPropria;
    }

    private void preencherInformacoes() {
        selecionado.getInformacoes().clear();
        for (GrupoBem grupoBem : retornarListaGrupoDoMapa()) {
            EfetivacaoLevantamentoInformacoes info = new EfetivacaoLevantamentoInformacoes();
            info.setGrupoBem(grupoBem);
            info.setLoteEfetivacao(selecionado);
            info.setValorLevantamentos(mapaValorTotalDosLevantamentosPorGrupo.get(grupoBem));
            info.setValorBens(getSaldoBemMovelPrincipal(grupoBem));
            info.setValorContabil(getSaldoBemMovelPrincipalContabil(grupoBem));
            info.setValorDepreciacaoBens(valorDepreciacaoBensPorGrupoeOrcamentaria(grupoBem));
            info.setValorDepreciacaoContabil(valorDepreciacaoContabilPorGrupoeOrcamentaria(grupoBem));
            if (mapaSaldoValorAcumuladoDepreciacao != null) {
                info.setValorDepreciacaoLevant(mapaSaldoValorAcumuladoDepreciacao.get(grupoBem));
            }
            selecionado.getInformacoes().add(info);
        }

        List<Object[]> objects1 = loteEfetivacaoLevantamentoBemFacade.gruposEfetivados(TipoBem.MOVEIS, hierarquiaOrcamentaria.getSubordinada(), selecionado.getDataEfetivacao(), retornarListaGrupoDoMapa());
        for (Object[] objects : objects1) {
            EfetivacaoLevantamentoInformacoes info = new EfetivacaoLevantamentoInformacoes();
            info.setGrupoBem(loteEfetivacaoLevantamentoBemFacade.buscarGrupoBemPorId(((BigDecimal) objects[0]).longValue()));
            info.setLoteEfetivacao(selecionado);
            info.setValorLevantamentos((BigDecimal) objects[4]);
            info.setValorBens((BigDecimal) objects[5]);
            info.setValorContabil((BigDecimal) objects[3]);
            info.setConsulta(Boolean.TRUE);
            info.setValorDepreciacaoLevant(BigDecimal.ZERO);
            info.setValorDepreciacaoBens((BigDecimal) objects[7]);
            info.setValorDepreciacaoContabil((BigDecimal) objects[6]);
            selecionado.getInformacoes().add(info);
            if (mapaValorTotalDosLevantamentosPorGrupo == null) {
                mapaValorTotalDosLevantamentosPorGrupo = Maps.newHashMap();
            }
            mapaValorTotalDosLevantamentosPorGrupo.put(info.getGrupoBem(), BigDecimal.ZERO);
        }
    }


    private void validarDadosObrigatorios() {
        ValidacaoException ve = new ValidacaoException();

        Util.validarCamposObrigatorios(selecionado, ve);

        if (selecionado.getUnidadeOrganizacional() == null && selecionado.getUnidadeOrcamentaria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione uma unidade organizacional e/ou uma unidade orçametária.");
        }

        ve.lancarException();
    }

    private void validarLoteEfetivacaoBem() {
        validarDadosObrigatorios();
        validarValoresEfetivacaoLevantamento();
        loteEfetivacaoLevantamentoBemFacade.validarAssociacaoDosGruposObjetoCompraComGrupoBem(levantamentosEncontrados, selecionado.getDataEfetivacao());
        levantamentoFacade.verificarLevantamentosQuePrecisamDeAprovacao(levantamentosEncontrados);
    }

    public Map<GrupoBem, BigDecimal> getMapaValorDepreciadoDoGrupo() {
        return mapaValorDepreciadoDoGrupo;
    }

    public void setMapaValorDepreciadoDoGrupo(Map<GrupoBem, BigDecimal> mapaValorDepreciadoDoGrupo) {
        this.mapaValorDepreciadoDoGrupo = mapaValorDepreciadoDoGrupo;
    }

    public HashMap<GrupoBem, BigDecimal> getMapaSaldoValorAcumuladoDepreciacao() {
        return mapaSaldoValorAcumuladoDepreciacao;
    }

    public void setMapaSaldoValorAcumuladoDepreciacao(HashMap<GrupoBem, BigDecimal> mapaSaldoValorAcumuladoDepreciacao) {
        this.mapaSaldoValorAcumuladoDepreciacao = mapaSaldoValorAcumuladoDepreciacao;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/efetivacao-de-levantamento-de-bem/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return loteEfetivacaoLevantamentoBemFacade;
    }

    public void limparDadosDaDepreciacao() {
        mapaSaldoValorAcumuladoDepreciacao = null;
        mapaSaldoValorAcumuladoOriginalItem = null;
        mapaValorTotalDosLevantamentosPorGrupo = null;

    }

    public void pesquisar() {
        try {
            if (hierarquiaOrcamentaria == null) {
                FacesUtil.addCampoObrigatorio("Informe uma unidade orçamentária para pesquisar.");
                return;
            }
            limparListasEMapas();
            futurePesquisaLevantamentos = levantamentoFacade.recuperarLevantamentosBem(hierarquiaOrcamentaria, Boolean.FALSE, sistemaControlador.getDataOperacao());
        } catch (ExcecaoNegocioGenerica ex) {
            exibirMensagemCampoUnidadeOrganizacionalObrigatorio();
        } catch (ValidacaoException ve) {
            limparListasEMapas();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void concluirPesquisa() throws ExecutionException, InterruptedException {
        if (futurePesquisaLevantamentos != null && futurePesquisaLevantamentos.isDone()) {
            FacesUtil.executaJavaScript("terminarPesquisa()");
        }
    }

    public void validarLevantamentos() throws ExecutionException, InterruptedException {
        levantamentosEncontrados = futurePesquisaLevantamentos.get();
        if (levantamentosEncontrados.isEmpty()) {
            FacesUtil.addAtencao("Nenhum levantamento de bens encontrado para efetivação.");
        } else {
            separarLevantamentosInconsistentes(levantamentosEncontrados);
            atribuirValorDepreciacao();
            preencherInformacoes();
        }
        FacesUtil.atualizarComponente("Formulario:panelResultado");
    }

    private void separarLevantamentosInconsistentes(List<LevantamentoBemPatrimonial> levantamentosEncontrados) {
        Iterator<LevantamentoBemPatrimonial> iterator = levantamentosEncontrados.iterator();
        while (iterator.hasNext()) {
            LevantamentoBemPatrimonial levantamento = iterator.next();
            if (levantamento.getGrupoBem() == null) {
                iterator.remove();
                levantamento.setErro("Grupo patrimonial não informado.");
                levantamentosInconsistentes.add(levantamento);
            }
        }
    }

    private void limparListasEMapas() {
        selecionado.setEfetivacoes(null);
        levantamentosEncontrados = Lists.newArrayList();
        mapaValorDepreciadoDoGrupo = Maps.newHashMap();
        mapaSaldoValorAcumuladoOriginalItem = Maps.newHashMap();
        mapaSaldoValorAcumuladoDepreciacao = Maps.newHashMap();
        mapaValorTotalDosLevantamentosPorGrupo = Maps.newHashMap();
    }

    private void atribuirValorDepreciacao() {
        if (mapaValorTotalDosLevantamentosPorGrupo == null || mapaValorTotalDosLevantamentosPorGrupo.isEmpty()) {
            loteEfetivacaoLevantamentoBemFacade.preencherValorTotalLevantamentosPorGrupo(mapaValorTotalDosLevantamentosPorGrupo, levantamentosEncontrados);
        }
        for (LevantamentoBemPatrimonial levantamentosEncontrado : levantamentosEncontrados) {
            levantamentosEncontrado.setDepreciacao(loteEfetivacaoLevantamentoBemFacade.calcularDepreciacaoLevantamento(selecionado,
                levantamentosEncontrado,
                mapaSaldoValorAcumuladoDepreciacao,
                mapaSaldoValorAcumuladoOriginalItem,
                mapaValorTotalDosLevantamentosPorGrupo));
        }
    }

    private void exibirMensagemCampoUnidadeOrganizacionalObrigatorio() {
        FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O campo Unidade Organizacional é obrigatório para realizar a pesquisa.");
    }

    public PesquisaLevantamento getPesquisaLevantamento() {
        return pesquisaLevantamento;
    }

    public void setPesquisaLevantamento(PesquisaLevantamento pesquisaLevantamento) {
        this.pesquisaLevantamento = pesquisaLevantamento;
    }

    public List<LevantamentoBemPatrimonial> getLevantamentosEncontrados() {
        return levantamentosEncontrados;
    }

    public void setLevantamentosEncontrados(List<LevantamentoBemPatrimonial> levantamentosEncontrados) {
        this.levantamentosEncontrados = levantamentosEncontrados;
    }

    public List<LevantamentoBemPatrimonial> getLevantamentosInconsistentes() {
        List<LevantamentoBemPatrimonial> retorno = new ArrayList<>();
        retorno.addAll(levantamentosInconsistentes);
        return retorno;
    }

    public void setLevantamentosInconsistentes(Set<LevantamentoBemPatrimonial> levantamentosInconsistentes) {
        this.levantamentosInconsistentes = levantamentosInconsistentes;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        selecionado.setUnidadeOrcamentaria(hierarquiaOrcamentaria != null ? hierarquiaOrcamentaria.getSubordinada() : null);
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public void buscarEfetivacoesParaEstorno() {
        assistenteBarraProgresso = new AssistenteBarraProgressoLoteEfetivacaoBem();
        assistenteBarraProgresso.setDataAtual(sistemaFacade.getDataOperacao());
        assistenteBarraProgresso.setProcessoEfetivacaoLevantamentoBens(AssistenteBarraProgressoLoteEfetivacaoBem.ProcessoEfetivacaoLevantamentoBens.PESQUISANDO_PARA_ESTORNO);
        FacesUtil.executaJavaScript("openDialog(dlgEfetivacaoEstorno)");
        FacesUtil.executaJavaScript("comecarPesquisaEstorno()");
        List<Long> ids = loteEfetivacaoLevantamentoBemFacade.recuperarIdsEfetivacao(selecionado);
        int partes = ids.size() > 100 ? (ids.size() / 4) : ids.size();
        List<List<Long>> idsParticionados = Lists.partition(ids, partes);
        assistenteBarraProgresso.setDescricaoProcesso("Validando Efetivações de Levantamento para Estorno...");
        assistenteBarraProgresso.setTotal(ids.size());
        for (List<Long> idsParticionado : idsParticionados) {
            futureLevantamentosEstorno.add(loteEfetivacaoLevantamentoBemFacade.buscarEfetivacoesParaEstorno(idsParticionado, assistenteBarraProgresso));
        }
    }

    public void estornarEfetivacoes() {
        FacesUtil.executaJavaScript("acompanharEfetivacaoEstorno()");
        futureEstornoConcluida = false;
        assistenteBarraProgresso.setDescricaoProcesso("Estornando Efetivações de Levantamento de Bens Móveis...");
        assistenteBarraProgresso.setProcessoEfetivacaoLevantamentoBens(AssistenteBarraProgressoLoteEfetivacaoBem.ProcessoEfetivacaoLevantamentoBens.EFETIVANDO_ESTORNO);
        assistenteBarraProgresso.zerarContadores();

        List<Long> ids = loteEfetivacaoLevantamentoBemFacade.recuperarIdsEfetivacao(selecionado);
        int partes = ids.size() > 100 ? (ids.size() / 4) : ids.size();
        List<List<Long>> idsParticionados = Lists.partition(ids, partes);
        selecionado.setSituacao(SituacaoEventoBem.ESTORNADO);
        loteEfetivacaoLevantamentoBemFacade.atualizarSituacaoLoteEstornado(selecionado);
        assistenteBarraProgresso.setTotal(ids.size());
        futureLevantamentosEstorno = Lists.newArrayList();
        for (List<Long> id : idsParticionados) {
            futureLevantamentosEstorno.add(loteEfetivacaoLevantamentoBemFacade.estornarEfetivacaoLevantamentoBem(id, assistenteBarraProgresso));
        }
    }

    public boolean isFutureEstornoConcluida() {
        boolean terminou = true;
        if (futureLevantamentosEstorno == null) {
            return false;
        }
        for (Future future : futureLevantamentosEstorno) {
            if (!future.isDone()) {
                terminou = false;
            }
        }

        if (terminou && !futureLevantamentosEstorno.isEmpty()) {
            if (assistenteBarraProgresso.getMensagensValidacaoFacesUtil() != null && !assistenteBarraProgresso.getMensagensValidacaoFacesUtil().isEmpty()) {
                ValidacaoException ve = new ValidacaoException();
                for (FacesMessage mensagemValidacao : assistenteBarraProgresso.getMensagensValidacaoFacesUtil()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(mensagemValidacao.getDetail());
                }
                FacesUtil.executaJavaScript("closeDialog(dlgEfetivacaoEstorno)");
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            } else {
                futureLevantamentosEstorno = null;
                FacesUtil.executaJavaScript("terminarPesquisaEstorno()");
                if (AssistenteBarraProgressoLoteEfetivacaoBem.ProcessoEfetivacaoLevantamentoBens.EFETIVANDO_ESTORNO.equals(assistenteBarraProgresso.getProcessoEfetivacaoLevantamentoBens())) {
                    FacesUtil.atualizarComponente("Formulario");
                    FacesUtil.executaJavaScript("closeDialog(dlgEfetivacaoEstorno)");
                    FacesUtil.addOperacaoRealizada("Efetivações estornadas com sucesso.");
                    redireciona();
                }
            }
            return true;
        }
        return false;
    }

    public void setFutureEstornoConcluida(boolean futureEstornoConcluida) {
        this.futureEstornoConcluida = futureEstornoConcluida;
    }

    public GrupoBem getGrupoBem(GrupoObjetoCompra grupoObjetoCompra) {
        try {
            return grupoObjetoCompraGrupoBemFacade.recuperarAssociacaoDoGrupoObjetoCompra(grupoObjetoCompra, sistemaControlador.getDataOperacao()).getGrupoBem();
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public List<SelectItem> retornaHierarquiaOrcamentaria() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (HierarquiaOrganizacional obj : hierarquiaOrganizacionalFacade.retornaHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(sistemaControlador.getUsuarioCorrente(), sistemaControlador.getDataOperacao())) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.buscarFiltrandoHierarquiaOndeUsuarioEhGestorPatrimonio(
            sistemaControlador.getUsuarioCorrente(),
            sistemaControlador.getDataOperacao(), parte.trim());
    }

    public BigDecimal valorDepreciacaoContabilPorGrupoeOrcamentaria(GrupoBem grupoBem) {
        try {
            return loteEfetivacaoLevantamentoBemFacade.saldoGrupoBemDepreciacaoContabil(hierarquiaOrcamentaria.getSubordinada(), grupoBem, selecionado.getDataEfetivacao());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal valorDepreciacaoBensPorGrupoeOrcamentaria(GrupoBem grupoBem) {
        try {
            return loteEfetivacaoLevantamentoBemFacade.saldoGrupoBemDepreciacao(hierarquiaOrcamentaria.getSubordinada(), grupoBem, selecionado.getDataEfetivacao());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getSomaValorBem() {
        BigDecimal total = BigDecimal.ZERO;
        for (LevantamentoBemPatrimonial levantamento : levantamentosEncontrados) {
            total = total.add(levantamento.getValorBem());
        }
        return total;
    }

    public List<GrupoBem> retornarListaGrupoDoMapa() {
        List<GrupoBem> retorno = new ArrayList<>();
        try {
            for (GrupoBem grupoBem : mapaValorTotalDosLevantamentosPorGrupo.keySet()) {
                retorno.add(grupoBem);
            }
            return retorno;
        } catch (NullPointerException nu) {
            return retorno;
        }
    }

    public String getTotalGeralLevantamento() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (EfetivacaoLevantamentoInformacoes informacoes : selecionado.getInformacoes()) {
            retorno = retorno.add(informacoes.getValorLevantamentos());
        }
        return Util.formataValor(retorno);
    }

    public String getTotalGeralContabil() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (EfetivacaoLevantamentoInformacoes info : selecionado.getInformacoes()) {
            retorno = retorno.add(info.getValorContabil());
        }
        return Util.formataValor(retorno);
    }

    public String getTotalGeralBensPrincipal() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (EfetivacaoLevantamentoInformacoes informacoes : selecionado.getInformacoes()) {
            retorno = retorno.add(informacoes.getValorBens());
        }
        return Util.formataValor(retorno);
    }

    public String getTotalLevantamentosMaisBensPrincipal() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (EfetivacaoLevantamentoInformacoes informacoes : selecionado.getInformacoes()) {
            retorno = retorno.add(informacoes.getBensMaisLevantamentos());
        }
        return Util.formataValor(retorno);
    }

    public String getTotalGeralDepreciacaoContabil() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (EfetivacaoLevantamentoInformacoes informacoes : selecionado.getInformacoes()) {
            retorno = retorno.add(informacoes.getValorDepreciacaoContabil());
        }
        return Util.formataValor(retorno);
    }

    public String getTotalGeralDepreciacaoBens() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (EfetivacaoLevantamentoInformacoes informacoes : selecionado.getInformacoes()) {
            retorno = retorno.add(informacoes.getValorDepreciacaoBens());
        }
        return Util.formataValor(retorno);
    }

    public String getTotalGeralDepreciacaoLevantamento() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (EfetivacaoLevantamentoInformacoes informacoes : selecionado.getInformacoes()) {
            if (informacoes.getValorDepreciacaoLevant() != null) {
                retorno = retorno.add(informacoes.getValorDepreciacaoLevant());
            }
        }
        return Util.formataValor(retorno);
    }

    public String getTotalGeralDepreciacaoDiferenca() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (EfetivacaoLevantamentoInformacoes informacoes : selecionado.getInformacoes()) {
            retorno = retorno.add(informacoes.getValorDepreciacaoContabil().subtract(informacoes.getDepreciacaoBensMaisDepreciacaoLevantamentos()));
        }
        return Util.formataValor(retorno);
    }

    public String getTotalGeralRateio() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (LevantamentoBemPatrimonial lev : levantamentosEncontrados) {
            retorno = retorno.add(lev.getDepreciacao());
        }
        return Util.formataValor(retorno);
    }


    public String getTotalGeralDiferenca() {
        BigDecimal ab = BigDecimal.ZERO;
        BigDecimal contabil = BigDecimal.ZERO;
        for (EfetivacaoLevantamentoInformacoes informacoes : selecionado.getInformacoes()) {
            ab = ab.add(informacoes.getBensMaisLevantamentos());
        }

        for (EfetivacaoLevantamentoInformacoes info : selecionado.getInformacoes()) {
            contabil = contabil.add(info.getValorContabil());
        }
        return Util.formataValor(contabil.subtract(ab));
    }

    public BigDecimal getSaldoBemMovelPrincipalContabil(GrupoBem grupoBem) {
        try {
            return saldoGrupoBemMovelFacade.recuperarUltimoSaldoGrupoBem(grupoBem, hierarquiaOrcamentaria.getSubordinada(), DataUtil.dataSemHorario(sistemaControlador.getDataOperacao()), NaturezaTipoGrupoBem.ORIGINAL).multiply(BigDecimal.valueOf(-1));
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getSaldoBemMovelPrincipal(GrupoBem grupoBem) {
        try {
            return loteEfetivacaoLevantamentoBemFacade.totalPorGrupoPatrimonialAndOrcamentaria(grupoBem, hierarquiaOrcamentaria.getSubordinada(), selecionado.getDataEfetivacao());
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal buscarValorDepreciadoDoLevantamento(EfetivacaoLevantamentoBem ef) {
        return bemFacade.valorDepreciadoEfetivacaoBem(ef);
    }

    public void validarValoresEfetivacaoLevantamento() {
        ValidacaoException ve = new ValidacaoException();

        if (hierarquiaOrcamentaria != null) {
            for (EfetivacaoLevantamentoInformacoes informacoes : selecionado.getInformacoes()) {
                if (informacoes.isGrupoInconsistentePrincipal()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da coluna (d) deve ser igual a coluna (a) para o grupo patrimonial " + informacoes.getGrupoBem().getDescricao());
                }

                if (informacoes.isGrupoComDepreciacaoContabilSuperiorAoValorContabil()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da coluna (a) não pode ser inferior ao valor da coluna (f) para o grupo patrimonial " + informacoes.getGrupoBem().getDescricao());
                }

                if (informacoes.isGrupoComDepreciacaoContabilSuperiorAoValorContabil()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da coluna (c) não pode ser inferior ao valor da coluna (h) para o grupo patrimonial " + informacoes.getGrupoBem().getDescricao());
                }

                if (informacoes.isGrupoInconsistenteDepreciacao()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Valores inconsistentes para depreciação no grupo bem " + informacoes.getGrupoBem());
                }
            }
        }

        ve.lancarException();
    }

    public BigDecimal calcularValorTotalDoBemEfetivado() {
        return loteEfetivacaoLevantamentoBemFacade.calcularValorTotalDoBemEfetivado(selecionado);
    }

    public BigDecimal calcularValorTotalDaDepreciacaoEfetivada() {
        return loteEfetivacaoLevantamentoBemFacade.calcularValorTotalDaDepreciacaoEfetivada(selecionado);
    }

    public void navegarParaVisualizacao() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + assistenteBarraProgresso.getLote().getId());
    }

    private void criarItensPaginacao() {
        model = new LazyDataModel<EfetivacaoLevantamentoBem>() {
            @Override
            public List<EfetivacaoLevantamentoBem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                filtro.setLote(selecionado);
                filtro.setPrimeiroRegistro(first);
                filtro.setQuantidadeRegistro(pageSize);
                setRowCount(loteEfetivacaoLevantamentoBemFacade.quantidadeEfetivacoes(filtro));
                return loteEfetivacaoLevantamentoBemFacade.recuperarEfetivacaoLevantamentoBem(filtro);
            }
        };
    }

    public class FiltroEfetivacaoLevantamentoBem {
        private int primeiroRegistro;
        private int quantidadeRegistro;
        private LoteEfetivacaoLevantamentoBem lote;

        public FiltroEfetivacaoLevantamentoBem() {
        }

        public int getPrimeiroRegistro() {
            return primeiroRegistro;
        }

        public void setPrimeiroRegistro(int primeiroRegistro) {
            this.primeiroRegistro = primeiroRegistro;
        }

        public int getQuantidadeRegistro() {
            return quantidadeRegistro;
        }

        public void setQuantidadeRegistro(int quantidadeRegistro) {
            this.quantidadeRegistro = quantidadeRegistro;
        }

        public LoteEfetivacaoLevantamentoBem getLote() {
            return lote;
        }

        public void setLote(LoteEfetivacaoLevantamentoBem lote) {
            this.lote = lote;
        }
    }

    public class PesquisaLevantamento {
        private ObjetoCompra objetoCompra;
        private GrupoObjetoCompra grupoObjetoCompra;
        private List<GrupoBem> grupoBem;

        public PesquisaLevantamento() {
            this.grupoBem = Lists.newArrayList();
        }

        public ObjetoCompra getObjetoCompra() {
            return objetoCompra;
        }

        public void setObjetoCompra(ObjetoCompra objetoCompra) {
            this.objetoCompra = objetoCompra;
        }

        public GrupoObjetoCompra getGrupoObjetoCompra() {
            return grupoObjetoCompra;
        }

        public void setGrupoObjetoCompra(GrupoObjetoCompra grupoObjetoCompra) {
            this.grupoObjetoCompra = grupoObjetoCompra;
        }

        public List<GrupoBem> getGrupoBem() {
            return grupoBem;
        }

        public void setGrupoBem(List<GrupoBem> grupoBem) {
            this.grupoBem = grupoBem;
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            relatorioDeLevantamentosEfetivadosControlador.gerarRelatorioLevantamentosEfetivados(selecionado, tipoRelatorioExtensao);
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar relatório", e);
        }
    }
}
