/*
 * Codigo gerado automaticamente em Wed Nov 09 08:42:27 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.consultaentidade.*;
import br.com.webpublico.controlerelatorio.SolicitacaoCompraRelatorioControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroHistoricoProcessoLicitatorio;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioAdministrativo;
import br.com.webpublico.entidadesauxiliares.GrupoContaDespesa;
import br.com.webpublico.entidadesauxiliares.VOSolicitacaoMaterialDocumentoOfical;
import br.com.webpublico.entidadesauxiliares.administrativo.SolicitacaoMaterialVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SolicitacaoMaterialFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.TabChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean(name = "solicitacaoMaterialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaSolicitacaoDeCompra", pattern = "/solicitacao-de-compra/novo/", viewId = "/faces/administrativo/licitacao/solicitacaomaterial/edita.xhtml"),
    @URLMapping(id = "editarSolicitacaoDeCompra", pattern = "/solicitacao-de-compra/editar/#{solicitacaoMaterialControlador.id}/", viewId = "/faces/administrativo/licitacao/solicitacaomaterial/edita.xhtml"),
    @URLMapping(id = "verSolicitacaoDeCompra", pattern = "/solicitacao-de-compra/ver/#{solicitacaoMaterialControlador.id}/", viewId = "/faces/administrativo/licitacao/solicitacaomaterial/visualizar.xhtml"),
    @URLMapping(id = "listarSolicitacaoDeCompra", pattern = "/solicitacao-de-compra/listar/", viewId = "/faces/administrativo/licitacao/solicitacaomaterial/lista.xhtml"),
})
public class SolicitacaoMaterialControlador extends PrettyControlador<SolicitacaoMaterial> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(SolicitacaoMaterialControlador.class);
    private static final Integer QUANTIDADE_MINIMA_PROPOSTA_COTACAO = 3;
    private static final Integer QUANTIDADE_MINIMA_FORNECEDORES_COTACAO = 3;
    @EJB
    private SolicitacaoMaterialFacade facade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ConverterAutoComplete converterSolicitante;
    private SolicitacaoReposicaoEstoque solicitacaoReposicaoEstoque;
    private UnidadeSolicitacaoMaterial unidadeSolicitacaoMaterialSelecionada;
    private ItemCriterioTecnica itemCriterioTecnicaSelecionado;
    private PontuacaoTecnica pontuacaoTecnicaSelecionado;
    private List<FornecedorCotacao> fornecedores;
    private List<ItemCotacao> itensCotacaoJustificaveis;
    private String dataObra;
    private Set<SubTipoObjetoCompra> subTipos;
    private ParticipanteIRP participanteIRP;
    private List<SolicitacaoMaterialVO> solicitacoesAprovadas;
    private List<SolicitacaoMaterialVO> solicitacoesAguardandoAvaliacao;
    private List<SolicitacaoMaterialVO> solicitacoesRejeitadas;
    private List<FiltroConsultaEntidade> filtros;
    private List<FieldConsultaEntidade> fieldsPesquisa;
    private ConsultaEntidadeController.ConverterFieldConsultaEntidade converterFieldConsulta;
    private FiltroRelatorioAdministrativo filtroRelatorio;
    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;
    private TipoDoctoOficial tipoDoctoOficial;
    private LeiLicitacao leiLicitacao;
    private List<LeiLicitacao> leis;
    private List<ModalidadeLicitacao> modalidades;
    private HashMap<TipoDoctoOficial, Integer> mapUltimaVersaoPorTipo;
    private List<VOSolicitacaoMaterialDocumentoOfical> voDocumentosOficiais;
    private SolicitacaoCompraDotacao dotacao;
    private SolicitacaoCompraDotacaoItem itemDotacao;
    private DespesaORC despesaORC;
    private TipoStatusSolicitacao tipoStatusSolicitacao;
    private ConverterAutoComplete converterFonteDespesaORC;
    private Map<TipoObjetoCompra, List<ItemSolicitacao>> mapItensPorTipoObjetoCompra;
    private Map<ParticipanteIRP, List<ItemParticipanteIRP>> mapItensParticipanteIrp;
    private Boolean quantidadeMinimaFornecedor;
    private Boolean quantidadeMinimaProposta;

    private Boolean bloquearEdicao;

    public SolicitacaoMaterialControlador() {
        super(SolicitacaoMaterial.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novaSolicitacaoDeCompra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setDataSolicitacao(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioCriacao(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setTipoNaturezaDoProcedimento(TipoNaturezaDoProcedimentoLicitacao.NAO_APLICAVEL);
        setTipoStatusSolicitacao(TipoStatusSolicitacao.AGUARDANDO_AVALIACAO);
        recuperarValorTotalDoLoteAoPegarDaSessao();
        selecionado.setOrcamentoSigiloso(false);
        buscarLeiLicitacao();
        mapUltimaVersaoPorTipo = new HashMap<>();
        quantidadeMinimaFornecedor = false;
        quantidadeMinimaProposta = false;
        montarVoDocumentosOficiais();
    }

    @URLAction(mappingId = "verSolicitacaoDeCompra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        leiLicitacao = selecionado.getAmparoLegal().getLeiLicitacao();
        setTipoStatusSolicitacao(facade.getStatusAtualDaSolicitacao(selecionado));
        setHierarquiaOrganizacional(facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalVigentePorUnidade(
            selecionado.getUnidadeOrganizacional(),
            TipoHierarquiaOrganizacional.ADMINISTRATIVA,
            facade.getSistemaFacade().getDataOperacao()));
        atribuirGrupoContaDespesaAoItensSolicitacao();
    }

    @URLAction(mappingId = "editarSolicitacaoDeCompra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaVerEditar();
        carregarInformacaoEditar();
        buscarLeiLicitacao();
        leiLicitacao = selecionado.getAmparoLegal().getLeiLicitacao();
        mapUltimaVersaoPorTipo = new HashMap<>();
        atualizarMapUltimaVersaoPorTipo();
        montarVoDocumentosOficiais();
        mapItensPorTipoObjetoCompra = facade.preencherMapItensPorTipoObjetoCompra(selecionado);
        setTipoStatusSolicitacao(facade.getStatusAtualDaSolicitacao(selecionado));
        buscarModalidades();
        bloquearEdicao = isSolicitacaoAprovada() || facade.temVinculoComProcessoDeCompra(selecionado);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-de-compra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            criarAvaliacaoSolicitacao();
            if (!mostrarCampoJustificativaPresencial()) {
                selecionado.setJustificativaPresencial(null);
            }
            if (this.solicitacaoReposicaoEstoque == null) {
                definirCamposContratacaoesQuandoNulas();
                selecionado = facade.salvarRetornando(selecionado);
                FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
                redirecionarParaVer();
            } else {
                selecionado.setUnidadeOrganizacional(solicitacaoReposicaoEstoque.getUnidadeOrganizacional());
                selecionado = facade.getSolicitacaoReposicaoEstoqueFacade().salvarSolicitaoMaterialESolicitacaoReposicaoEstoque(selecionado, solicitacaoReposicaoEstoque);
                FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
                redirecionarParaVer();
            }
            gerarNotificacaoNovaAvaliacaoSolicitacaoMaterialAguardando();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void definirCamposContratacaoesQuandoNulas() {
        if (Strings.isNullOrEmpty(selecionado.getContratacoesCorrelatas())) {
            selecionado.setContratacoesCorrelatas("Não há previsão para contratações correlatas.");
        }
        if (Strings.isNullOrEmpty(selecionado.getContratacoesIndependentes())) {
            selecionado.setContratacoesIndependentes("Não há previsão para contratações interdependentes.");
        }
    }

    private void buscarLeiLicitacao() {
        leis = facade.getAmparoLegalFacade().buscarLeiLicitacaoAmparoLegalVigente();
        if (isOperacaoNovo() && leis.size() == 1) {
            leiLicitacao = leis.get(0);
        }
    }


    public List<UnidadeOrganizacional> completaUnidadesOrganizacionaisAdministrativasDoUsuario(String parte) {
        return facade.getUnidadeOrganizacionalFacade().listaUnidadesOrganizacionalUsuarioCorrente(facade.getSistemaFacade().getUsuarioCorrente(), parte.trim());
    }

    public List<UnidadeOrganizacional> completaUnidadeOrganizacional(String parte) {
        List<UnidadeOrganizacional> retorno = new ArrayList<>();
        for (HierarquiaOrganizacional hierarquiaOrganizacional : facade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaOrganizacionalAdministrativa(parte.trim())) {
            Util.adicionarObjetoEmLista(retorno, hierarquiaOrganizacional.getSubordinada());
        }
        return retorno;
    }

    public List<UnidadeGestora> completaUnidadeGestora(String parte) {
        return facade.getUnidadeGestoraFacade().listaFiltrandoPorExercicio(facade.getSistemaFacade().getExercicioCorrente(), parte.trim());
    }

    public void desabilitarAndRemoverJustificativaCotacao() {
        removerJustificativa();
    }

    public void removerJustificativa() {
        if (selecionado.getJustificativaCotacao() != null) {
            if (!selecionado.getJustificativaCotacao().trim().isEmpty()) {
                selecionado.setJustificativaCotacao(null);
            }
        }
    }

    public boolean isObrigatorioJustificarCotacao() {
        return quantidadeMinimaFornecedor || quantidadeMinimaProposta;
    }

    public void validarJustificativaCotacao() {
        if (!isSolicitacaoAprovada()) {
            if (isObrigatorioJustificarCotacao()) {
                ValidacaoException ex = new ValidacaoException();

                if (selecionado.getJustificativaCotacao() == null || selecionado.getJustificativaCotacao().trim().isEmpty()) {
                    ex.adicionarMensagemDeCampoObrigatorio("Favor informar a justificativa para esta cotação, utilizando o campo 'Justificativa da Cotação'");
                }
                ex.lancarException();
            }
        }
    }

    public void validarItensAndFornecedores() {
        if (selecionado.getCotacao().getFormularioCotacao().getTipoSolicitacao().isCompraServico()) {
            if (isObrigatorioJustificarCotacao()) {
                FacesUtil.atualizarComponente("formulario-justificativa");
                FacesUtil.executaJavaScript("dlgJustificativa.show();");
            } else {
                if (selecionado.getCotacao().getFormularioCotacao().isItemCotacaoPorFornecedor()) {
                    FacesUtil.executaJavaScript("dialogCriterioProcessamentoPrecoItens.show();");
                } else {
                    carregarLotesAndItens();
                }
            }
        } else if (selecionado.getCotacao().getFormularioCotacao().getTipoSolicitacao().isObraServicoEngenharia()) {
            carregarLotesAndItens();
            FacesUtil.atualizarComponente("Formulario:tb-view:tbLotes");
            FacesUtil.atualizarComponente("Formulario:tb-view:panelcadastro");
        }
    }

    private void buscarCotacaoAndFornecedores() {
        selecionado.setCotacao(facade.getCotacaoFacade().recuperar(selecionado.getCotacao().getId()));
        fornecedores = Lists.newArrayList();
        if (!selecionado.getCotacao().getFornecedores().isEmpty()) {
            for (Pessoa fornecedor : selecionado.getCotacao().getFornecedores()) {
                FornecedorCotacao fc = new FornecedorCotacao();
                fc.setFornecedor(fornecedor);
                fornecedores.add(fc);
            }
        }
    }

    private void validarSalvar() {
        validarCampos();
        validarLotesSolicitacao();
        validarJustificativaCotacao();
        validarRegraDeNegocio();
        validarDotacaoOrcamentaria();
        facade.getConfiguracaoLicitacaoFacade().validarAnexoObrigatorio(selecionado.getDetentorDocumentoLicitacao(), selecionado.getTipoAnexo());
    }

    private void gerarNotificacaoNovaAvaliacaoSolicitacaoMaterialAguardando() {
        if (isOperacaoNovo()) {
            for (AvaliacaoSolicitacaoDeCompra avaliacao : selecionado.getAvaliacoes()) {
                facade.gerarNotificacaoNovaAvaliacaoSolicitacaoMaterialAguardando(avaliacao);
            }
        }
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    private void validarLotesSolicitacao() {
        ValidacaoException ve = new ValidacaoException();
        for (LoteSolicitacaoMaterial loteSolicitacaoMaterial : selecionado.getLoteSolicitacaoMaterials()) {
            validarDescontoMinimo(ve, loteSolicitacaoMaterial);
        }
        ve.lancarException();
    }

    private void validarDescontoMinimo(ValidacaoException ve, LoteSolicitacaoMaterial loteSolicitacaoMaterial) {
        if (selecionado.getModalidadeLicitacao().isPregao() && selecionado.getTipoAvaliacao().isMaiorDesconto()) {
            if (selecionado.getTipoApuracao().isPorLote()) {
                if (loteSolicitacaoMaterial.getPercentualDescontoMinimo() == null ||
                    loteSolicitacaoMaterial.getPercentualDescontoMinimo().compareTo(BigDecimal.ZERO) <= 0 ||
                    loteSolicitacaoMaterial.getPercentualDescontoMinimo().compareTo(BigDecimal.valueOf(100)) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo 'Desconto Mínimo (%)' do lote " + loteSolicitacaoMaterial +
                        " deve ser informado com um valor maior que 0 e menor que 100(cem).");
                }
            } else {
                for (ItemSolicitacao itemSolicitacao : loteSolicitacaoMaterial.getItensSolicitacao()) {
                    if (itemSolicitacao.getPercentualDescontoMinimo() == null ||
                        itemSolicitacao.getPercentualDescontoMinimo().compareTo(BigDecimal.ZERO) <= 0 ||
                        itemSolicitacao.getPercentualDescontoMinimo().compareTo(BigDecimal.valueOf(100)) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O campo 'Desconto Mínimo (%)' do item " + itemSolicitacao +
                            " deve ser informado com um valor maior que 0 e menor que 100.");
                    }
                }
            }
        }
    }

    private void validarCampos() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoSolicitacao() != null && selecionado.getSubTipoObjetoCompra() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Subtipo Objeto de Compra é obrigatório.");
        }
        if (selecionado.getTipoSolicitacao() != null
            && TipoSolicitacao.OBRA_SERVICO_DE_ENGENHARIA.equals(selecionado.getTipoSolicitacao())
            && selecionado.getDataOrcamentoObra() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data do Orçamento da Obra é obrigatório.");
        }
        if (selecionado.hasNaturezaProcecimento() && selecionado.getTipoNaturezaDoProcedimento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo " + selecionado.getModalidadeLicitacao().getLabelTipoModalidade() + " deve ser informado.");
        }
        if (mostrarCampoJustificativaPresencial() && (selecionado.getJustificativaPresencial() == null || selecionado.getJustificativaPresencial().isEmpty())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Justificativa Presencial deve ser informado.");
        }
        ve.lancarException();
    }

    private void criarAvaliacaoSolicitacao() {
        if (isOperacaoNovo()) {
            List<AvaliacaoSolicitacaoDeCompra> avaliacoes = Lists.newArrayList();
            avaliacoes.add(criarObjetoAvaliacao(TipoStatusSolicitacao.AGUARDANDO_AVALIACAO));
            if (selecionado.isDispensaDeLicitacao() || selecionado.getModalidadeLicitacao().isCredenciamento()) {
                avaliacoes.add(criarObjetoAvaliacao(TipoStatusSolicitacao.APROVADA));
            }
            selecionado.setAvaliacoes(avaliacoes);
        }
    }

    private AvaliacaoSolicitacaoDeCompra criarObjetoAvaliacao(TipoStatusSolicitacao situacao) {
        AvaliacaoSolicitacaoDeCompra novaAvaliacao = new AvaliacaoSolicitacaoDeCompra(selecionado, situacao);
        novaAvaliacao.setDataAvaliacao(DataUtil.getDataComHoraAtual(facade.getSistemaFacade().getDataOperacao()));
        novaAvaliacao.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        definirMotivoAvaliacao(novaAvaliacao);
        return novaAvaliacao;
    }

    private void definirMotivoAvaliacao(AvaliacaoSolicitacaoDeCompra avaliacao) {
        if (isOperacaoNovo()) {
            avaliacao.setMotivo(SolicitacaoMaterial.getMsgRegistroNovo());
        }
        if (isOperacaoEditar()) {
            avaliacao.setMotivo(SolicitacaoMaterial.getMsgRegistroAlterado());
        }
    }

    public void definirInstrumentoConvocatorio() {
        if (selecionado.getModalidadeLicitacao() != null && selecionado.getModoDisputa() != null) {
            selecionado.setInstrumentoConvocatorio(InstrumentoConvocatorio.getTipoInstrumentoSolicitacaoCompra(selecionado.getModalidadeLicitacao(), selecionado.getModoDisputa()));
        }
    }

    public FiltroRelatorioAdministrativo getFiltroRelatorio() {
        FiltroRelatorioAdministrativo filtroRelatorio = new FiltroRelatorioAdministrativo();
        filtroRelatorio.setHierarquiaAdministrativa(hierarquiaOrganizacional);
        filtroRelatorio.setIdObjeto(selecionado.getId());
        filtroRelatorio.setFiltros(selecionado.getNumero().toString());
        return filtroRelatorio;
    }


    private void validarRegraDeNegocio() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.hasLotes()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A solicitação deve ter pelo menos um lote adicionado. Verifique na aba Cotação.");
        }
        ve.lancarException();
        for (LoteSolicitacaoMaterial lote : selecionado.getLoteSolicitacaoMaterials()) {
            if (!lote.hasItens()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O lote número: " + lote.getNumero() + " está sem item adicionado. Por favor adicione o(s) item(ns) deste lote.");
            }
        }
        if (selecionado.isTecnicaEPreco()) {
            Util.validarCampos(selecionado.getCriterioTecnicaSolicitacao());

            if (selecionado.getCriterioTecnicaSolicitacao() != null
                && (selecionado.getCriterioTecnicaSolicitacao().getItens() == null || selecionado.getCriterioTecnicaSolicitacao().getItens().isEmpty())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório adicionar pelo menos um item de cretério técnica!");
            }

            Util.validarCampos(selecionado.getCriterioPrecoSolicitacao());
            Util.validarCampos(selecionado.getClassificacaoFinalSolicitacao());
        }
        ve.lancarException();
    }

    private void validarDotacaoOrcamentaria() {
        if (!Util.isListNullOrEmpty(selecionado.getDotacoes())) {
            ValidacaoException ve = new ValidacaoException();
            Map<ItemSolicitacao, BigDecimal> map = new HashMap<>();
            selecionado.getLoteSolicitacaoMaterials().stream().flatMap(lote -> lote.getItensSolicitacao().stream()).forEach(item -> {
                map.put(item, BigDecimal.ZERO);
            });

            selecionado.getDotacoes().stream().flatMap(dot -> dot.getItens().stream()).forEach(item -> {
                BigDecimal qtdeMap = map.get(item.getItemSolicitacao());
                qtdeMap = qtdeMap.add(item.getQuantidade());
                map.put(item.getItemSolicitacao(), qtdeMap);
            });
            map.entrySet().stream().filter(entry -> entry.getValue().compareTo(entry.getKey().getQuantidade()) != 0)
                .map(entry -> "O item " + entry.getKey().getNumero() + " precisa ter sua quantidade total distribuída na dotação orçamentária . ")
                .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);
            ve.lancarException();
        }
    }


    @Override
    public void cancelar() {
        if (solicitacaoReposicaoEstoque != null) {
            Web.poeNaSessao(solicitacaoReposicaoEstoque);
            FacesUtil.redirecionamentoInterno("/solicitacao-de-reposicao-de-estoque/novo/");
        } else {
            super.cancelar();
        }
    }

    public void recuperaVerEditar() {
        selecionado = facade.recuperar(selecionado.getId());
        setHierarquiaOrganizacional(facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalVigentePorUnidade(
            selecionado.getUnidadeOrganizacional(),
            TipoHierarquiaOrganizacional.ADMINISTRATIVA,
            facade.getSistemaFacade().getDataOperacao()));

        if (isSolicitacaoAprovada()) {
            if (validaVinculoComProcessoDeCompra()) {
                FacesUtil.addAtencao("Não é permitido alterações. A Solicitação " + selecionado.getDescricao() + " está vinculada a uma ou mais Cotações e a um ou mais Processo de Compra!");
            } else {
                if (validaVinculoComProcessoDeCompra()) {
                    FacesUtil.addAtencao("Não é permitido alterações. A Solicitação " + selecionado.getDescricao() + " está vinculada a um ou mais Processo de Compra!");
                }
            }
        }
        buscarSubtipoObjetoCompraDaConfiguracaoTipoObjetoCompra();
    }

    public boolean validaVinculoComProcessoDeCompra() {
        return facade.temVinculoComProcessoDeCompra(selecionado);
    }

    public void listenerModalidade() {
        selecionado.setTipoNaturezaDoProcedimento(selecionado.hasNaturezaProcecimento() ? null : TipoNaturezaDoProcedimentoLicitacao.NAO_APLICAVEL);
        if (selecionado.getModalidadeLicitacao() != null && selecionado.getModalidadeLicitacao().isCredenciamento()) {
            selecionado.setTipoNaturezaDoProcedimento(TipoNaturezaDoProcedimentoLicitacao.CREDENCIAMENTO);
        }
        if (!leiLicitacao.isLei8666()) {
            selecionado.setAmparoLegal(null);
        }
        selecionado.setTipoAvaliacao(null);
        selecionado.setTipoApuracao(null);
        selecionado.setCriterioTecnicaSolicitacao(null);
        selecionado.setCriterioPrecoSolicitacao(null);
        selecionado.setClassificacaoFinalSolicitacao(null);
        if (selecionado.getModalidadeLicitacao() != null && (selecionado.getModalidadeLicitacao().isManifestacaoInteresse()
            || selecionado.getModalidadeLicitacao().isPreQualificacao()
            || selecionado.getModalidadeLicitacao().isDialogCompetitivo())) {
            FacesUtil.addAtencao("O sistema ainda não atende a(o) " + selecionado.getModoSolicitacao().getDescricao() + " de " + selecionado.getModalidadeLicitacao().getDescricao() + ".");
            selecionado.setModalidadeLicitacao(null);
        }
        limparUnidadesParticipantes();
        definirInstrumentoConvocatorio();
        atribuirAmparoLegalNaSolicitacao();
    }

    public void listenerLeiLicitacao() {
        limparCamposPrevisaoPrecedimentoLegal();
        buscarModalidades();
    }

    public void buscarModalidades() {
        if (leiLicitacao != null && selecionado.getModoSolicitacao() != null) {
            modalidades = Lists.newArrayList();
            if (leiLicitacao.isLei8666()) {
                modalidades = ModalidadeLicitacao.getModalidadeSolicitacaoCompra(selecionado.getModoSolicitacao());
                atribuirAmparoLegalNaSolicitacao();
            } else {
                List<ModalidadeLicitacao> modalidadesAmparoLegal = facade.getAmparoLegalFacade().buscarModalidadesPorLei(leiLicitacao);
                modalidades = modalidadesAmparoLegal.stream().filter(mod -> mod.getModoSolicitacaoCompra().equals(selecionado.getModoSolicitacao())).collect(Collectors.toList());
            }
        }
    }

    private void atribuirAmparoLegalNaSolicitacao() {
        List<AmparoLegal> amparos = facade.getAmparoLegalFacade().buscarPorLeiAndModalidade("", leiLicitacao, selecionado.getModalidadeLicitacao());
        if (amparos.size() == 1) {
            selecionado.setAmparoLegal(amparos.get(0));
        }
    }

    public void listenerNaturezaProcedimento() {
        if (selecionado.hasNaturezaProcecimento()) {
            FacesUtil.atualizarComponente("Formulario:tb-view");
            FacesUtil.executaJavaScript("setaFoco('Formulario:tb-view:tipoAvaliacao')");
            if (selecionado.getModalidadeLicitacao().isInexigibilidade() && selecionado.getTipoNaturezaDoProcedimento().isCredenciamento()) {
                FacesUtil.addAtencao("O sistema ainda não atende " + selecionado.getModalidadeLicitacao().getDescricao() + " com natureza "
                    + selecionado.getTipoNaturezaDoProcedimento().getDescricao() + ".");
                selecionado.setTipoNaturezaDoProcedimento(TipoNaturezaDoProcedimentoLicitacao.NORMAL);
            }
        }
    }

    public boolean mostrarCampoJustificativaPresencial() {
        return selecionado.hasNaturezaProcecimento() && leiLicitacao != null && leiLicitacao.isLei14133() && selecionado.getModalidadeLicitacao().isPregao() && selecionado.getTipoNaturezaDoProcedimento() != null &&
            (selecionado.getTipoNaturezaDoProcedimento().isNaturezaProcedimentoPresencialComRegistroPreco() || selecionado.getTipoNaturezaDoProcedimento().isPresencial());
    }

    private void limparUnidadesParticipantes() {
        selecionado.setUnidadesParticipantes(new ArrayList<UnidadeSolicitacaoMaterial>());
    }

    public void iniciarCriterioAvaliacaoPrecoItem() {
        if (!isSolicitacaoAprovada()) {
            if (validarFormularioIRP()) return;
        }
        buscarCotacaoAndFornecedores();
        preencherItensJustiticaveis();
        if (selecionado.getCotacao().getFormularioCotacao().getTipoSolicitacao().isCompraServico()) {
            quantidadeMinimaFornecedor = ((fornecedores.isEmpty()) || (fornecedores.size() < QUANTIDADE_MINIMA_FORNECEDORES_COTACAO));
            quantidadeMinimaProposta = !getItensCotacaoJustificaveis().isEmpty();
        }
        validarItensAndFornecedores();
        if (habilitarTipoObra()) {
            int mes = DataUtil.getMes(selecionado.getCotacao().getDataCotacao());
            Integer ano = DataUtil.getAno(selecionado.getCotacao().getDataCotacao());
            String mesString = StringUtil.preencheString(String.valueOf(mes), 2, '0');
            dataObra = mesString + "/" + ano;
            montarDataOrcamentoObra();
        }
    }

    private void preencherItensJustiticaveis() {
        if (selecionado.getCotacao() == null || selecionado.getCotacao().getFormularioCotacao().getTipoApuracaoLicitacao().isPorItem()) {
            itensCotacaoJustificaveis = Lists.newArrayList();
            Integer quantidadeProposta;
            List<ItemCotacao> itens = facade.getCotacaoFacade().buscarItemCotacao(selecionado.getCotacao());

            for (ItemCotacao itemCotacao : itens) {
                if (itemCotacao.getObjetoCompra().getGrupoObjetoCompra().getTipoCotacao().isFornecedor()) {

                    quantidadeProposta = facade.getCotacaoFacade().getQuantidadePropostaValorCotacao(null, itemCotacao);
                    if (quantidadeProposta.compareTo(QUANTIDADE_MINIMA_PROPOSTA_COTACAO) < 0) {
                        itensCotacaoJustificaveis.add(itemCotacao);
                    }
                }
            }
        }
    }

    private boolean validarFormularioIRP() {
        if (getProcessoIrp()) {
            boolean controle = false;
            if (!selecionado.getModalidadeLicitacao().isPregao()
                && !selecionado.getModalidadeLicitacao().isRDC()) {
                controle = true;
            }
            if (selecionado.getTipoNaturezaDoProcedimento() != null
                && !selecionado.getTipoNaturezaDoProcedimento().isNaturezaProcedimentoPresencialComRegistroPreco()
                && !selecionado.getTipoNaturezaDoProcedimento().isNaturezaProcedimentoEletronicoComRegistroPreco()
                && !selecionado.getTipoNaturezaDoProcedimento().isNaturezaProcedimentoRDCComRegistroPreco()) {
                controle = true;
            }
            if (controle) {
                FacesUtil.addOperacaoNaoPermitida("A cotação selecionada é de um formulário de Intenção de Registro de Preço, portanto a modalidade deve ser Pregão ou RDC com Registro de Preço.");
                selecionado.setCotacao(null);
                return true;
            }
        }
        return false;
    }

    private void carregarInformacaoEditar() {
        quantidadeMinimaFornecedor = false;
        quantidadeMinimaProposta = false;
        iniciarCriterioAvaliacaoPrecoItem();
        atribuirGrupoContaDespesaAoItensSolicitacao();
    }

    private void atribuirGrupoContaDespesaAoItensSolicitacao() {
        for (LoteSolicitacaoMaterial lote : selecionado.getLoteSolicitacaoMaterials()) {
            for (ItemSolicitacao item : lote.getItensSolicitacao()) {
                atribuirGrupoContaDespesa(item);
            }
        }
    }

    public void calculaValorDoLote(LoteSolicitacaoMaterial lsm, Collection<ItemSolicitacao> listaDeItens, SolicitacaoMaterial sm, Collection<LoteSolicitacaoMaterial> listaDeLotes) {
        BigDecimal valorAcumulado = BigDecimal.ZERO;
        BigDecimal valorItem = BigDecimal.ZERO;
        lsm.setValor(BigDecimal.ZERO);
        for (ItemSolicitacao item : listaDeItens) {
            if ((SolicitacaoMaterial) selecionado == null) {
                if (item.getPreco() == null) {
                    item.setPreco(BigDecimal.ZERO);
                }
                if (item.getLoteSolicitacaoMaterial().getId().equals(lsm.getId())) {
                    valorItem = valorItem.add(item.getPreco().multiply(item.getQuantidade()));
                    valorAcumulado = valorAcumulado.add(valorItem);
                    valorItem = BigDecimal.ZERO;
                }
            } else {
                if (item.getLoteSolicitacaoMaterial().getNumero().equals(lsm.getNumero())) {
                    valorItem = valorItem.add(item.getPreco().multiply(item.getQuantidade()));
                    valorAcumulado = valorAcumulado.add(valorItem);
                    valorItem = BigDecimal.ZERO;
                }
            }
        }
        lsm.setValor(valorAcumulado);

        //se o valor do lote for zero, quer dizer q não tem mais itens associados com ele e pode ser excluído
        if (lsm.getValor().equals(BigDecimal.ZERO)) {
            if ((SolicitacaoMaterial) selecionado != null) {
                selecionado.getLoteSolicitacaoMaterials().remove(lsm);
            }
        }
        calculaTotalSolicitacaoMaterial(sm, listaDeLotes);

    }

    public void calculaTotalSolicitacaoMaterial(SolicitacaoMaterial sm, Collection<LoteSolicitacaoMaterial> listaDeLotes) {
        sm.setValor(BigDecimal.ZERO);
        BigDecimal valorLote = BigDecimal.ZERO;
        for (LoteSolicitacaoMaterial lote : listaDeLotes) {
            if (lote.getSolicitacaoMaterial().equals(sm)) {
                valorLote = valorLote.add(lote.getValor());
            }
        }
        sm.setValor(valorLote);
    }

    public void removeItem(ItemSolicitacao item) {
        int index = selecionado.getLoteSolicitacaoMaterials().indexOf(item.getLoteSolicitacaoMaterial());
        selecionado.getLoteSolicitacaoMaterials().get(index).getItensSolicitacao().remove(item);

        if (selecionado.getLoteSolicitacaoMaterials().get(index).getItensSolicitacao().isEmpty()) {
            selecionado.getLoteSolicitacaoMaterials().remove(index);
            calculaTotalSolicitacaoMaterial(selecionado, selecionado.getLoteSolicitacaoMaterials());
        } else {
            calculaValorDoLote(item.getLoteSolicitacaoMaterial(), selecionado.getLoteSolicitacaoMaterials().get(index).getItensSolicitacao(), selecionado, selecionado.getLoteSolicitacaoMaterials());
        }
    }

    public List<SelectItem> getTipoPrazo() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPrazo object : TipoPrazo.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Boolean permiteExibirPanelMotivoStatus() {
        if (TipoStatusSolicitacao.REJEITADA.equals(getStatusAtualSolicitacao())) {
            selecionado.setMotivo(facade.recuperaMotivoRejeicao(selecionado));
            return true;
        }
        return false;
    }

    public List<ServicoCompravel> completaServico(String parte) {
        return facade.getServicoCompravelFacade().listaFiltrando(parte.trim(), "descricao", "codigo");
    }

    public ConverterAutoComplete getConverterSolicitante() {
        if (converterSolicitante == null) {
            converterSolicitante = new ConverterAutoComplete(PessoaFisica.class, facade.getPessoaFisicaFacade());
        }
        return converterSolicitante;
    }

    public List<PessoaFisica> completaSolicitante(String parte) {
        return facade.getPessoaFisicaFacade().listaFiltrando(parte.trim(), 50, "nome", "cpf");
    }


    public List<SelectItem> getListaTipoSolicitacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoSolicitacao object : TipoSolicitacao.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    private void recuperarValorTotalDoLoteAoPegarDaSessao() {
        if (selecionado.getLoteSolicitacaoMaterials() != null && !selecionado.getLoteSolicitacaoMaterials().isEmpty()) {
            for (LoteSolicitacaoMaterial lote : selecionado.getLoteSolicitacaoMaterials()) {
                lote.setValor(valorTotalDoLote(lote));
            }
        }
    }

    public UnidadeSolicitacaoMaterial getUnidadeSolicitacaoMaterialSelecionada() {
        return unidadeSolicitacaoMaterialSelecionada;
    }

    public void setUnidadeSolicitacaoMaterialSelecionada(UnidadeSolicitacaoMaterial unidadeSolicitacaoMaterialSelecionada) {
        this.unidadeSolicitacaoMaterialSelecionada = unidadeSolicitacaoMaterialSelecionada;
    }

    public ItemCriterioTecnica getItemCriterioTecnicaSelecionado() {
        return itemCriterioTecnicaSelecionado;
    }

    public void setItemCriterioTecnicaSelecionado(ItemCriterioTecnica itemCriterioTecnicaSelecionado) {
        this.itemCriterioTecnicaSelecionado = itemCriterioTecnicaSelecionado;
    }

    public PontuacaoTecnica getPontuacaoTecnicaSelecionado() {
        return pontuacaoTecnicaSelecionado;
    }

    public void setPontuacaoTecnicaSelecionado(PontuacaoTecnica pontuacaoTecnicaSelecionado) {
        this.pontuacaoTecnicaSelecionado = pontuacaoTecnicaSelecionado;
    }

    public String getMotivoRejeitado() {
        if (TipoStatusSolicitacao.REJEITADA.equals(getStatusAtualSolicitacao())) {
            return facade.recuperaMotivoRejeicao(selecionado);
        }
        return "";
    }

    public List<SelectItem> getLeisLicitacao() {
        return Util.getListSelectItemSemCampoVazio(leis.toArray());
    }

    public List<SelectItem> getTiposObjetoCompra() {
        if (selecionado.getTipoSolicitacao() != null) {
            if (selecionado.getTipoSolicitacao().isObraServicoEngenharia()) {
                return Util.getListSelectItem(TipoObjetoCompra.getTiposObraEServicoEngenharia());
            }
            return Util.getListSelectItem(Arrays.asList(TipoObjetoCompra.values()));
        }
        return Lists.newArrayList();
    }

    public List<SelectItem> getSubtiposObjetoCompra() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (subTipos != null && !subTipos.isEmpty()) {
            for (SubTipoObjetoCompra object : subTipos) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getModosSolicitacao() {
        return Util.getListSelectItem(ModoSolicitacaoCompra.values());
    }

    public List<SelectItem> getModalidades() {
        if (leiLicitacao != null && selecionado.getModoSolicitacao() != null) {
            return Util.getListSelectItem(modalidades);
        }
        return Lists.newArrayList();
    }

    public List<SelectItem> getNaturezasProcedimento() {
        if (selecionado.getModalidadeLicitacao() != null) {
            return Util.getListSelectItem(TipoNaturezaDoProcedimentoLicitacao.getTiposNaturezaProcedimento(selecionado.getModalidadeLicitacao()));
        }
        return Lists.newArrayList();
    }

    public List<SelectItem> getTiposAvaliacao() {
        if (leiLicitacao != null && leiLicitacao.isLei8666()) {
            return Util.getListSelectItem(TipoAvaliacaoLicitacao.getAvaliacoesLei8666(selecionado.getModalidadeLicitacao(), selecionado.getTipoNaturezaDoProcedimento()));
        }
        if (isLei14133ModalidadeDispensa()) {
            if (selecionado.getModoDisputa() != null) {
                return Util.getListSelectItem(TipoAvaliacaoLicitacao.getAvaliacoesLei14133ModalidadeDispensa(selecionado.getModoDisputa()));
            }
            return Lists.newArrayList();
        }
        return Util.getListSelectItem(TipoAvaliacaoLicitacao.getAvaliacoes(selecionado.getModalidadeLicitacao()));
    }

    public List<SelectItem> getModosDisputa() {
        if (isLei14133ModalidadeDispensa()) {
            return Util.getListSelectItem(ModoDisputa.modosDisputaLei14133ModalidadeDispensa(selecionado.getTipoNaturezaDoProcedimento()));
        }
        if (selecionado.getTipoAvaliacao() != null) {
            return Util.getListSelectItem(ModoDisputa.modosDisputa(selecionado.getTipoAvaliacao()));
        }

        return Lists.newArrayList();
    }

    public List<SelectItem> getTiposApuracao() {
        return Util.getListSelectItem(TipoApuracaoLicitacao.values());
    }

    public boolean hasCotacaoSelecionada() {
        return selecionado.getCotacao() != null;
    }

    public Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    public boolean isSolicitacaoAprovada() {
        return selecionado.getId() != null && getStatusAtualSolicitacao() != null && getStatusAtualSolicitacao().isAprovada();
    }

    public TipoStatusSolicitacao getStatusAtualSolicitacao() {
        return tipoStatusSolicitacao;
    }

    public void setTipoStatusSolicitacao(TipoStatusSolicitacao tipoStatusSolicitacao) {
        this.tipoStatusSolicitacao = tipoStatusSolicitacao;
    }

    private BigDecimal valorTotalDoLote(LoteSolicitacaoMaterial lote) {
        BigDecimal totalLote = BigDecimal.ZERO;
        for (ItemSolicitacao item : lote.getItensSolicitacao()) {
            if (item.getPreco() == null) {
                item.setPreco(BigDecimal.ZERO);
            }
            totalLote = totalLote.add(item.getPreco().multiply(item.getQuantidade()));
        }
        return totalLote;
    }

    public void removerLote(ActionEvent event) {
        LoteSolicitacaoMaterial lote = (LoteSolicitacaoMaterial) event.getComponent().getAttributes().get("lote");
        if (lote != null) {
            selecionado.getLoteSolicitacaoMaterials().remove(lote);
            calculaTotalSolicitacaoMaterial(selecionado, selecionado.getLoteSolicitacaoMaterials());
        }
    }

    public void novaUnidadeParticipante() {
        this.unidadeSolicitacaoMaterialSelecionada = new UnidadeSolicitacaoMaterial();
        this.unidadeSolicitacaoMaterialSelecionada.setSolicitacaoMaterial(selecionado);
    }

    public void confirmarUnidadeParticipante() {
        if (podeConfirmarUnidadeParticipante()) {
            selecionado.setUnidadesParticipantes(Util.adicionarObjetoEmLista(selecionado.getUnidadesParticipantes(), unidadeSolicitacaoMaterialSelecionada));
            cancelarUnidadeParticipante();
        }
    }

    public void cancelarUnidadeParticipante() {
        this.unidadeSolicitacaoMaterialSelecionada = null;
    }

    private boolean podeConfirmarUnidadeParticipante() {
        if (!unidadeParticipanteInformada()) {
            return false;
        }
        if (unidadeParticipanteJaAdicionada()) {
            return false;
        }
        return true;
    }

    private boolean unidadeParticipanteInformada() {
        if (unidadeSolicitacaoMaterialSelecionada.getUnidadeGestora() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo órgão/entidade participante é obrigatório.");
            return false;
        }
        return true;
    }

    private boolean unidadeParticipanteJaAdicionada() {
        if (selecionado.getUnidadesParticipantes() != null && !selecionado.getUnidadesParticipantes().isEmpty()) {
            for (UnidadeSolicitacaoMaterial unidadeSolicitacaoMaterial : selecionado.getUnidadesParticipantes()) {
                if (unidadeSolicitacaoMaterial.getUnidadeGestora().getDescricao().equals(unidadeSolicitacaoMaterialSelecionada.getUnidadeGestora().getDescricao())
                    && !selecionado.getUnidadesParticipantes().contains(unidadeSolicitacaoMaterialSelecionada)) {
                    FacesUtil.addOperacaoNaoPermitida("Esta unidade já foi adicionada.");
                    return true;
                }
            }
        }
        return false;
    }

    public void selecionarUnidadeParticipante(UnidadeSolicitacaoMaterial unidade) {
        this.unidadeSolicitacaoMaterialSelecionada = unidade;
    }

    public void removerUnidadeParticipante(UnidadeSolicitacaoMaterial unidade) {
        selecionado.getUnidadesParticipantes().remove(unidade);
    }

    private boolean validarConfirmacao(ValidadorEntidade obj) {
        if (!Util.validaCampos(obj)) {
            return false;
        }

        try {
            obj.validarConfirmacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return false;
        }
        return true;
    }

    public void instanciarCriterios() {
        if (selecionado.isTecnicaEPreco()) {
            criarCriterioTecnica();
            criarCriterioPreco();
            criarClassificaoFinal();
        }
    }

    private void criarClassificaoFinal() {
        selecionado.setClassificacaoFinalSolicitacao(new ClassificacaoFinalSolicitacao());
        selecionado.getClassificacaoFinalSolicitacao().setSolicitacaoMaterial(selecionado);
    }

    private void criarCriterioPreco() {
        selecionado.setCriterioPrecoSolicitacao(new CriterioPrecoSolicitacao());
        selecionado.getCriterioPrecoSolicitacao().setSolicitacaoMaterial(selecionado);
    }

    private void criarCriterioTecnica() {
        selecionado.setCriterioTecnicaSolicitacao(new CriterioTecnicaSolicitacao());
        selecionado.getCriterioTecnicaSolicitacao().setSolicitacaoMaterial(selecionado);
    }

    public void novoItemCriterioTecnicaQualitativo() {
        itemCriterioTecnicaSelecionado = new ItemCriterioTecnica();
        itemCriterioTecnicaSelecionado.setTipoItemCriterioTecnica(ItemCriterioTecnica.TipoItemCriterioTecnica.QUALITATIVO);
        itemCriterioTecnicaSelecionado.setCriterioTecnicaSolicitacao(selecionado.getCriterioTecnicaSolicitacao());
        itemCriterioTecnicaSelecionado.setPontos(new ArrayList<PontuacaoTecnica>());
        novaPontuacaoTecnica();
    }

    public void novoItemCriterioTecnicaQuantitativo() {
        itemCriterioTecnicaSelecionado = new ItemCriterioTecnica();
        itemCriterioTecnicaSelecionado.setTipoItemCriterioTecnica(ItemCriterioTecnica.TipoItemCriterioTecnica.QUANTITATIVO);
        itemCriterioTecnicaSelecionado.setCriterioTecnicaSolicitacao(selecionado.getCriterioTecnicaSolicitacao());
        itemCriterioTecnicaSelecionado.setPontos(new ArrayList<PontuacaoTecnica>());
        novaPontuacaoTecnica();
    }

    private void novaPontuacaoTecnica() {
        pontuacaoTecnicaSelecionado = new PontuacaoTecnica();
        pontuacaoTecnicaSelecionado.setItemCriterioTecnica(itemCriterioTecnicaSelecionado);
    }

    public void confirmarPontuacaoTecnica() {
        if (podeConfirmarPontuacaoTecnica()) {
            itemCriterioTecnicaSelecionado.setPontos(Util.adicionarObjetoEmLista(itemCriterioTecnicaSelecionado.getPontos(), pontuacaoTecnicaSelecionado));
            novaPontuacaoTecnica();
        }
    }

    private boolean podeConfirmarPontuacaoTecnica() {
        if (!validarConfirmacao(itemCriterioTecnicaSelecionado)) {
            return false;
        }
        if (!validarConfirmacao(pontuacaoTecnicaSelecionado)) {
            return false;
        }
        return true;
    }

    public void confirmarItemCriterioTecnica() {
        if (!podeConfirmarItemCriterioTecnica()) {
            return;
        }

        ItemCriterioTecnica.TipoItemCriterioTecnica tipo = itemCriterioTecnicaSelecionado.getTipoItemCriterioTecnica();
        selecionado.getCriterioTecnicaSolicitacao().setItens(Util.adicionarObjetoEmLista(selecionado.getCriterioTecnicaSolicitacao().getItens(), itemCriterioTecnicaSelecionado));
        cancelarItemCriterioTecnica();
        if (tipo.equals(ItemCriterioTecnica.TipoItemCriterioTecnica.QUALITATIVO)) {
            novoItemCriterioTecnicaQualitativo();
        }
        if (tipo.equals(ItemCriterioTecnica.TipoItemCriterioTecnica.QUANTITATIVO)) {
            novoItemCriterioTecnicaQuantitativo();
        }
    }

    public void cancelarItemCriterioTecnica() {
        itemCriterioTecnicaSelecionado = null;
    }

    public void fecharDialogItemCriterioTecnica() {
        if (itemCriterioTecnicaSelecionado.getPontos().isEmpty()
            && selecionado.getCriterioTecnicaSolicitacao().getItens() != null
            && selecionado.getCriterioTecnicaSolicitacao().getItens().contains(itemCriterioTecnicaSelecionado)) {
            FacesUtil.addOperacaoNaoPermitida("Por favor, adicione pelo menos uma pontuação a este item.");
            return;
        }
        cancelarItemCriterioTecnica();
        FacesUtil.executaJavaScript("dialogItemCriterioTecnica.hide()");
    }

    private boolean podeConfirmarItemCriterioTecnica() {
        if (!validarConfirmacao(itemCriterioTecnicaSelecionado)) {
            return false;
        }
        if (!descricaoJaExiste()) {
            return false;
        }
        return true;
    }

    private boolean descricaoJaExiste() {
        if (selecionado.getCriterioTecnicaSolicitacao().getItens() != null && !selecionado.getCriterioTecnicaSolicitacao().getItens().isEmpty()) {
            for (ItemCriterioTecnica item : selecionado.getCriterioTecnicaSolicitacao().getItens()) {
                if (itemCriterioTecnicaSelecionado.getDescricao().equals(item.getDescricao())
                    && !selecionado.getCriterioTecnicaSolicitacao().getItens().contains(itemCriterioTecnicaSelecionado)) {
                    FacesUtil.addOperacaoNaoPermitida("Já existe um item adicionado com esta descrição.");
                    return false;
                }
            }
        }
        return true;
    }

    public void selecionarPontuacaoTecnica(PontuacaoTecnica pontuacao) {
        pontuacaoTecnicaSelecionado = (PontuacaoTecnica) Util.clonarObjeto(pontuacao);
    }

    public void selecionarItemCriterioTecnica(ItemCriterioTecnica itemCriterio) {
        itemCriterioTecnicaSelecionado = (ItemCriterioTecnica) Util.clonarObjeto(itemCriterio);
    }

    public void editarItemCriterioTecnica(ItemCriterioTecnica item) {
        selecionarItemCriterioTecnica(item);
        novaPontuacaoTecnica();
    }

    public void removerPontuacaoTecnica(PontuacaoTecnica pontuacao) {
        itemCriterioTecnicaSelecionado.getPontos().remove(pontuacao);
    }

    public void removerItemCriterioTecnica(ItemCriterioTecnica itemCriterio) {
        selecionado.getCriterioTecnicaSolicitacao().getItens().remove(itemCriterio);
    }

    public boolean nenhumaPontuacaoAdicionadaAoItem() {
        try {
            return itemCriterioTecnicaSelecionado.getPontos().isEmpty();
        } catch (Exception ex) {
            return false;
        }
    }

    public List<SelectItem> getCriteriosProcessamento() {
        List<SelectItem> lista = new ArrayList<>();
        for (CriterioProcessamentoPrecoItemCotacao cppic : CriterioProcessamentoPrecoItemCotacao.values()) {
            if (CriterioProcessamentoPrecoItemCotacao.MAXIMO.equals(cppic)) {
                continue;
            }
            lista.add(new SelectItem(cppic, cppic.toString()));
        }
        return lista;
    }

    public void processarItensCotacao() {
        try {
            facade.validarCriterioProcessamentoItem(selecionado.getCriterio());
            carregarLotesAndItens();
            criarUnidadeSolicitacao();
            FacesUtil.executaJavaScript("dialogCriterioProcessamentoPrecoItens.hide()");
            FacesUtil.atualizarComponente("Formulario:tb-view:tbLotes");
            FacesUtil.atualizarComponente("Formulario:tb-view:panelcadastro");
            FacesUtil.atualizarComponente("Formulario:tb-view:panel-unidade-participante");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void criarUnidadeSolicitacao() {
        try {
            if (getProcessoIrp()) {
                List<ParticipanteIRP> participantes = facade.getParticipanteIRPFacade().buscarParticipantesIRPAprovado("", selecionado.getCotacao().getFormularioCotacao().getIntencaoRegistroPreco());

                selecionado.setUnidadesParticipantes(Lists.<UnidadeSolicitacaoMaterial>newArrayList());
                for (ParticipanteIRP participante : participantes) {
                    UnidadeSolicitacaoMaterial unidade = new UnidadeSolicitacaoMaterial();
                    unidade.setParticipanteIRP(participante);
                    unidade.setSolicitacaoMaterial(selecionado);
                    Util.adicionarObjetoEmLista(selecionado.getUnidadesParticipantes(), unidade);

                    if (participante.getGerenciador()) {
                        HierarquiaOrganizacional hoParticipante = facade.getParticipanteIRPFacade().buscarUnidadeParticipanteVigente(participante);
                        setHierarquiaOrganizacional(hoParticipante);
                        selecionado.setUnidadeOrganizacional(hoParticipante.getSubordinada());
                    }
                }
            }

        } catch (ExcecaoNegocioGenerica e) {
            selecionado.setCotacao(null);
            selecionado.getLoteSolicitacaoMaterials().clear();
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public Boolean getProcessoIrp() {
        return selecionado != null && selecionado.getCotacao() != null && selecionado.getCotacao().getFormularioCotacao().getFormularioIrp();
    }

    public Boolean isProcessoIrpComMaisDeUmaUnidade() {
        return getProcessoIrp()
            && !Util.isListNullOrEmpty(selecionado.getUnidadesParticipantes())
            && selecionado.getUnidadesParticipantes().size() > 1;
    }

    public void atribuirGrupoContaDespesa(ItemSolicitacao item) {
        if (item != null && item.getObjetoCompra() != null) {
            GrupoContaDespesa grupoContaDespesa = facade.getObjetoCompraFacade().criarGrupoContaDespesa(item.getObjetoCompra().getTipoObjetoCompra(), item.getObjetoCompra().getGrupoObjetoCompra());
            item.getObjetoCompra().setGrupoContaDespesa(grupoContaDespesa);
        }
    }

    public void selecionarParticipanteIrp(ParticipanteIRP participanteIRP) {
        this.participanteIRP = facade.getParticipanteIRPFacade().recuperarItens(participanteIRP.getId());
    }

    public void carregarLotesAndItens() {
        selecionado.limparLotes();
        selecionado.setCotacao(facade.getCotacaoFacade().recuperar(selecionado.getCotacao().getId()));

        for (LoteCotacao loteCotacao : selecionado.getCotacao().getLotes()) {
            LoteSolicitacaoMaterial loteSM = novoLoteSolicitacao(loteCotacao);

            for (ItemCotacao itemCotacao : loteCotacao.getItens()) {
                ItemSolicitacao itemSol = novoItemSolicitacao(loteSM, itemCotacao);
                loteSM.setValor(loteSM.getValor().add(itemSol.getPrecoTotal()));
                loteSM.getItensSolicitacao().add(itemSol);
            }
            selecionado.getLoteSolicitacaoMaterials().add(loteSM);
        }
        calculaTotalSolicitacaoMaterial(selecionado, selecionado.getLoteSolicitacaoMaterials());
        mapItensPorTipoObjetoCompra = facade.preencherMapItensPorTipoObjetoCompra(selecionado);
    }

    private ItemSolicitacao novoItemSolicitacao(LoteSolicitacaoMaterial loteSM, ItemCotacao itemCotacao) {
        ItemSolicitacao itemSol = new ItemSolicitacao();
        itemSol.setLoteSolicitacaoMaterial(loteSM);
        itemSol.setNumero(itemCotacao.getNumero());
        itemSol.setDescricaoComplementar(itemCotacao.getDescricaoComplementar());
        itemSol.setQuantidade(itemCotacao.getQuantidade());
        itemSol.setUnidadeMedida(itemCotacao.getUnidadeMedida());
        itemSol.setItemCotacao(itemCotacao);
        itemSol.setObjetoCompra(itemCotacao.getObjetoCompra());
        itemSol.setPcaObjetoCompra(getPcaObjetoCompraPorObjetoCompra(itemCotacao.getObjetoCompra()));

        definirPrecoItemSolicitacao(itemCotacao, itemSol);
        definirPrecoTotalItemSolicitacao(itemCotacao, itemSol);
        atribuirGrupoContaDespesa(itemSol);

        return itemSol;
    }

    private PlanoContratacaoAnualObjetoCompra getPcaObjetoCompraPorObjetoCompra(ObjetoCompra oc) {
        List<PlanoContratacaoAnualObjetoCompra> pcasObjs = facade.getPlanoContratacaoAnualObjetoCompraFacade().buscarPcaObjetosComprasPorExercicioEUndiadeAdm("", selecionado.getExercicio(), selecionado.getUnidadeOrganizacional(), oc);
        return (pcasObjs != null && !pcasObjs.isEmpty()) ? pcasObjs.get(0) : null;
    }

    private void definirPrecoTotalItemSolicitacao(ItemCotacao itemCotacao, ItemSolicitacao itemSolicitacao) {
        BigDecimal precoTotal = itemCotacao.getPrecoTotalItem(itemCotacao, itemSolicitacao.getPreco()).setScale(2, RoundingMode.HALF_EVEN);
        itemSolicitacao.setPrecoTotal(precoTotal);
    }

    private void definirPrecoItemSolicitacao(ItemCotacao itemCotacao, ItemSolicitacao itemSolicitacao) {
        if (itemCotacao.getObjetoCompra().isObjetoValorReferencia()) {
            itemSolicitacao.setPreco(itemCotacao.getValorUnitario());
        } else {
            String criterio = selecionado.getCriterio() != null ? selecionado.getCriterio().getCriterio() : CriterioProcessamentoPrecoItemCotacao.MINIMO.getCriterio();
            BigDecimal precoMedio = facade.getCotacaoFacade().recuperarPrecoDoItemPorCriterio(criterio, itemCotacao, itemCotacao.getUnidadeMedida().getMascaraValorUnitario().getQuantidadeCasasDecimais());
            BigDecimal precoItem = itemCotacao.getPrecoItem(itemCotacao, precoMedio);
            itemSolicitacao.setPreco(precoItem);
        }
    }

    private LoteSolicitacaoMaterial novoLoteSolicitacao(LoteCotacao loteCotacao) {
        LoteSolicitacaoMaterial loteSol = new LoteSolicitacaoMaterial();
        loteSol.setSolicitacaoMaterial(selecionado);
        loteSol.setNumero(loteCotacao.getNumero());
        loteSol.setDescricao(loteCotacao.getDescricao());
        loteSol.setValor(BigDecimal.ZERO);
        loteSol.setLoteCotacao(loteCotacao);
        return loteSol;
    }

    public void removerItemLote(LoteSolicitacaoMaterial loteSolicitacaoMaterial, ItemSolicitacao itemSolicitacao) {
        loteSolicitacaoMaterial.getItensSolicitacao().remove(itemSolicitacao);
        loteSolicitacaoMaterial.somarValorTotal();
        calculaTotalSolicitacaoMaterial(selecionado, selecionado.getLoteSolicitacaoMaterials());
        loteSolicitacaoMaterial.ordenarItensSolicitacao();
    }

    public List<Cotacao> completarCotacao(String parte) {
        try {
            validarBuscarCotacao();
            return facade.getCotacaoFacade().buscarCotacaoPorTipoSolicitacao(parte.trim(),
                facade.getSistemaFacade().getUsuarioCorrente(),
                selecionado.getTipoSolicitacao(),
                UtilRH.getDataOperacaoFormatada(),
                selecionado.getTipoApuracao());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        return Lists.newArrayList();
    }

    private void validarBuscarCotacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoSolicitacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de solicitação deve ser informado.");
        }
        if (selecionado.getTipoObjetoCompra() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de objeto de compra deve ser informado.");
        }
        if (selecionado.getSubTipoObjetoCompra() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo sub-tipo de objeto de compra deve ser informado.");
        }
        if (selecionado.getModalidadeLicitacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo modalidade deve ser informado.");
        }
        if (selecionado.getAmparoLegal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo amparo legal deve ser informado.");
        }
        if (selecionado.getTipoAvaliacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de avaliação deve ser informado.");
        }
        if (selecionado.getModoDisputa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo modo de disputa deve ser informado.");
        }
        if (selecionado.getTipoApuracao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de apuração deve ser informado.");
        }
        ve.lancarException();
    }

    public void limparCamposPrevisaoPrecedimentoLegal() {
        selecionado.setInstrumentoConvocatorio(null);
        selecionado.setModalidadeLicitacao(null);
        selecionado.setTipoNaturezaDoProcedimento(null);
        selecionado.setAmparoLegal(null);
        selecionado.setTipoApuracao(null);
        selecionado.setModoDisputa(null);
        selecionado.setTipoAvaliacao(null);
    }

    public void limparCotacao() {
        selecionado.setCotacao(null);
        selecionado.setJustificativa(null);
        selecionado.setJustificativaCotacao(null);
        selecionado.setLoteSolicitacaoMaterials(new ArrayList());
    }

    public List<FornecedorCotacao> getFornecedores() {
        return fornecedores;
    }

    public void setFornecedores(List<FornecedorCotacao> fornecedores) {
        this.fornecedores = fornecedores;
    }

    public List<ItemCotacao> getItensCotacaoJustificaveis() {
        if (itensCotacaoJustificaveis == null) {
            itensCotacaoJustificaveis = Lists.newArrayList();
        }
        return itensCotacaoJustificaveis;
    }

    public void setItensCotacaoJustificaveis(List<ItemCotacao> itensCotacaoJustificaveis) {
        this.itensCotacaoJustificaveis = itensCotacaoJustificaveis;
    }

    public void limparCotacaoPlanilhaOrcamentaria() {
        selecionado.setCotacao(null);
        selecionado.setLoteSolicitacaoMaterials(Lists.<LoteSolicitacaoMaterial>newArrayList());
        selecionado.setJustificativaCotacao(null);
        setItensCotacaoJustificaveis(Lists.newArrayList());
    }

    public void verificarSeExisteConfigurcaoObjetoCompra() {
        limparCotacaoPlanilhaOrcamentaria();
        buscarSubtipoObjetoCompraDaConfiguracaoTipoObjetoCompra();
    }

    private void buscarSubtipoObjetoCompraDaConfiguracaoTipoObjetoCompra() {
        try {
            subTipos = new HashSet<>();
            if (selecionado.getTipoObjetoCompra() != null && selecionado.getDataSolicitacao() != null) {
                List<ConfigTipoObjetoCompra> configuracoes = facade.getConfigTipoObjetoCompraFacade().buscarConfiguracoesVigente(selecionado.getDataSolicitacao(), selecionado.getTipoObjetoCompra());
                if (configuracoes.isEmpty()) {
                    FacesUtil.addOperacaoNaoPermitida("Configuração de tipo objeto de compra não encontrada para o tipo de objeto de compra: <b>" + selecionado.getTipoObjetoCompra() + "</b>.");
                }
                for (ConfigTipoObjetoCompra config : configuracoes) {
                    if (config.getSubtipoObjetoCompra() != null) {
                        subTipos.add(config.getSubtipoObjetoCompra());
                    }
                }
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public boolean habilitarTipoObra() {
        return selecionado != null && selecionado.getTipoSolicitacao() != null && TipoSolicitacao.OBRA_SERVICO_DE_ENGENHARIA.equals(selecionado.getTipoSolicitacao());
    }

    public void montarDataOrcamentoObra() {
        if (dataObra != null) {
            String[] parte = dataObra.split("/");
            String value = String.valueOf(parte[0]);
            if (Util.isMesValido(value)) {
                selecionado.setDataOrcamentoObra(DataUtil.getDateParse("01/" + dataObra));
            } else {
                setDataObra("");
                FacesUtil.addOperacaoNaoPermitida("O formato da data é inválida ");
            }
        }
    }

    public String getDataObra() {
        return dataObra;
    }

    public void setDataObra(String dataObra) {
        this.dataObra = dataObra;
    }

    public ParticipanteIRP getParticipanteIRP() {
        return participanteIRP;
    }

    public void setParticipanteIRP(ParticipanteIRP participanteIRP) {
        this.participanteIRP = participanteIRP;
    }

    @URLAction(mappingId = "listarSolicitacaoDeCompra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listar() {
        filtros = Lists.newArrayList();
        limparFiltros();
        montarFieldsPesquisa();
        pesquisarSolicitacoes();
    }

    public ConsultaEntidadeController.ConverterFieldConsultaEntidade getConverterFieldConsulta() {
        return converterFieldConsulta;
    }

    public void setConverterFieldConsulta(ConsultaEntidadeController.ConverterFieldConsultaEntidade
                                              converterFieldConsulta) {
        this.converterFieldConsulta = converterFieldConsulta;
    }

    public List<SolicitacaoMaterialVO> getSolicitacoesAprovadas() {
        return solicitacoesAprovadas;
    }

    public void setSolicitacoesAprovadas(List<SolicitacaoMaterialVO> solicitacoesAprovadas) {
        this.solicitacoesAprovadas = solicitacoesAprovadas;
    }

    public List<SolicitacaoMaterialVO> getSolicitacoesAguardandoAvaliacao() {
        return solicitacoesAguardandoAvaliacao;
    }

    public void setSolicitacoesAguardandoAvaliacao(List<SolicitacaoMaterialVO> solicitacoesAguardandoAvaliacao) {
        this.solicitacoesAguardandoAvaliacao = solicitacoesAguardandoAvaliacao;
    }

    public List<SolicitacaoMaterialVO> getSolicitacoesRejeitadas() {
        return solicitacoesRejeitadas;
    }

    public void setSolicitacoesRejeitadas(List<SolicitacaoMaterialVO> solicitacoesRejeitadas) {
        this.solicitacoesRejeitadas = solicitacoesRejeitadas;
    }

    public List<FiltroConsultaEntidade> getFiltros() {
        return filtros;
    }

    public void setFiltros(List<FiltroConsultaEntidade> filtros) {
        this.filtros = filtros;
    }

    public void addFiltro() {
        filtros.add(new FiltroConsultaEntidade(new FieldConsultaEntidade()));
    }

    public void removerFiltro(FiltroConsultaEntidade filtro) {
        filtros.remove(filtro);
        if (filtros.isEmpty()) {
            addFiltro();
        }
    }

    public void limparFiltros() {
        filtros.clear();
        addFiltro();
    }

    public List<SelectItem> getPesquisaveis() {
        return Util.getListSelectItem(fieldsPesquisa, false);
    }

    private void montarFieldsPesquisa() {
        fieldsPesquisa = Lists.newArrayList();
        fieldsPesquisa.add(new FieldConsultaEntidade("Solicitação de Compra.Tipo de Solicitação", "obj.tipoSolicitacao", "br.com.webpublico.enums.TipoSolicitacao", TipoCampo.ENUM, TipoAlinhamento.ESQUERDA));
        fieldsPesquisa.add(new FieldConsultaEntidade("Solicitação de Compra.Lei de Licitação", "al.leiLicitacao", "br.com.webpublico.enums.LeiLicitacao", TipoCampo.ENUM, TipoAlinhamento.ESQUERDA));
        fieldsPesquisa.add(new FieldConsultaEntidade("Solicitação de Compra.Subtipo de Objeto de Compra", "obj.subTipoObjetoCompra", "br.com.webpublico.enums.SubTipoObjetoCompra", TipoCampo.ENUM, TipoAlinhamento.ESQUERDA));
        fieldsPesquisa.add(new FieldConsultaEntidade("Solicitação de Compra.Data de Solicitação", "obj.dataSolicitacao", null, TipoCampo.DATE, TipoAlinhamento.ESQUERDA));
        fieldsPesquisa.add(new FieldConsultaEntidade("Solicitação de Compra.Número", "obj.numero", null, TipoCampo.INTEGER, TipoAlinhamento.ESQUERDA));
        fieldsPesquisa.add(new FieldConsultaEntidade("Solicitação de Compra.Exercício", "ex.ano", null, TipoCampo.INTEGER, TipoAlinhamento.ESQUERDA));
        fieldsPesquisa.add(new FieldConsultaEntidade("Solicitação de Compra.Descrição", "obj.descricao", null, TipoCampo.STRING, TipoAlinhamento.ESQUERDA));
        fieldsPesquisa.add(new FieldConsultaEntidade("Solicitação de Compra.Prazo de Entrega", "obj.prazoEntrega", null, TipoCampo.INTEGER, TipoAlinhamento.ESQUERDA));
        fieldsPesquisa.add(new FieldConsultaEntidade("Solicitação de Compra.Tipo do prazo de entrega", "obj.tipoPrazoEntrega", "br.com.webpublico.enums.TipoPrazo", TipoCampo.ENUM, TipoAlinhamento.ESQUERDA));
        fieldsPesquisa.add(new FieldConsultaEntidade("Solicitação de Compra.Modalidade da Licitação", "obj.modalidadeLicitacao", "br.com.webpublico.enums.ModalidadeLicitacao", TipoCampo.ENUM, TipoAlinhamento.ESQUERDA));
        fieldsPesquisa.add(new FieldConsultaEntidade("Solicitação de Compra.Motivo", "obj.motivo", null, TipoCampo.STRING, TipoAlinhamento.ESQUERDA));
        fieldsPesquisa.add(new FieldConsultaEntidade("Solicitação de Compra.Prazo de Execução", "obj.prazoExecucao", null, TipoCampo.INTEGER, TipoAlinhamento.ESQUERDA));
        fieldsPesquisa.add(new FieldConsultaEntidade("Solicitação de Compra.Tipo Prazo de Execução", "obj.tipoPrazoDeExecucao", "br.com.webpublico.enums.TipoPrazo", TipoCampo.ENUM, TipoAlinhamento.ESQUERDA));
        fieldsPesquisa.add(new FieldConsultaEntidade("Solicitação de Compra.Tipo Avaliação", "obj.tipoAvaliacao", "br.com.webpublico.enums.TipoAvaliacaoLicitacao", TipoCampo.ENUM, TipoAlinhamento.ESQUERDA));
        fieldsPesquisa.add(new FieldConsultaEntidade("Solicitação de Compra.Criado Por", "coalesce(pfUsu.nome, usu.login)", null, TipoCampo.STRING, TipoAlinhamento.ESQUERDA));
        fieldsPesquisa.add(new FieldConsultaEntidade("Solicitação de Compra.Data do Orçamento da Obra", "obj.dataOrcamentoObra", null, TipoCampo.DATE, TipoAlinhamento.ESQUERDA));
        fieldsPesquisa.add(new FieldConsultaEntidade("Unidade Administrativa.Código", "vwadm.codigo", null, TipoCampo.STRING, TipoAlinhamento.ESQUERDA));
        fieldsPesquisa.add(new FieldConsultaEntidade("Unidade Administrativa.Descrição", "vwadm.descricao", null, TipoCampo.STRING, TipoAlinhamento.ESQUERDA));
        converterFieldConsulta = new ConsultaEntidadeController.ConverterFieldConsultaEntidade(fieldsPesquisa);
    }

    public void redirecionarParaNovo() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "novo/");
    }

    public void redirecionarParVerOrEditar(Long idSolicitacaoMaterial, String verOrEditar) {
        Web.setCaminhoOrigem(getCaminhoPadrao() + "listar/");
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + verOrEditar + "/" + idSolicitacaoMaterial + "/");
    }

    public void pesquisarSolicitacoes() {
        String condicao = getCondicaoPesquisa();
        solicitacoesAprovadas = facade.buscarSolicitacoesVO(TipoStatusSolicitacao.APROVADA, condicao);
        solicitacoesAguardandoAvaliacao = facade.buscarSolicitacoesVO(TipoStatusSolicitacao.AGUARDANDO_AVALIACAO, condicao);
        solicitacoesRejeitadas = facade.buscarSolicitacoesVO(TipoStatusSolicitacao.REJEITADA, condicao);
    }

    protected String getCondicaoPesquisa() {
        String retorno = "";
        for (FiltroConsultaEntidade filtro : filtros) {
            if (filtro.getField() != null && filtro.getField().getValor() != null && filtro.getField().getTipo() != null && filtro.getOperacao() != null) {
                if (TipoCampo.ENUM.equals(filtro.getField().getTipo()) || TipoCampo.STRING.equals(filtro.getField().getTipo())) {
                    if (Operador.LIKE.equals(filtro.getOperacao()) || Operador.NOT_LIKE.equals(filtro.getOperacao())) {
                        retorno += " and " + filtro.getField().getValor() + " " + filtro.getOperacao().getOperador() + " '%" + filtro.getValor() + "%' ";
                    } else {
                        retorno += " and " + filtro.getField().getValor() + " " + filtro.getOperacao().getOperador() + " '" + filtro.getValor() + "' ";
                    }
                } else if (TipoCampo.DATE.equals(filtro.getField().getTipo())) {
                    retorno += " and " + filtro.getField().getValor() + " " + filtro.getOperacao().getOperador() + " to_date('" + DataUtil.getDataFormatada((Date) filtro.getValor()) + "', 'dd/MM/yyyy') ";
                } else {
                    retorno += " and " + filtro.getField().getValor() + " " + filtro.getOperacao().getOperador() + " " + filtro.getValor();
                }
            }
        }
        return retorno;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        if (hierarquiaOrganizacional != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        }
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public void gerarRelatorio(SolicitacaoMaterial solicitacao) {
        if (solicitacao != null) {
            selecionado = solicitacao;
            setHierarquiaOrganizacional(facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                selecionado.getUnidadeOrganizacional(),
                facade.getSistemaFacade().getDataOperacao()));
            filtroRelatorio = getFiltroRelatorio();
            gerarRelatorio("PDF");
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            filtroRelatorio = getFiltroRelatorio();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeRelatorio("Solicitação de Compra");
            dto.adicionarParametro("MODULO", "Administrativo");
            dto.adicionarParametro("DATA_OPERACAO", DataUtil.getDataFormatada(facade.getSistemaFacade().getDataOperacao()));
            dto.adicionarParametro("CONDICAO", SolicitacaoCompraRelatorioControlador.montarCondicao(filtroRelatorio));
            dto.adicionarParametro("FILTROS", filtroRelatorio.getFiltros());
            dto.adicionarParametro("NOMERELATORIO", dto.getNomeRelatorio());
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("SECRETARIA", filtroRelatorio.getHierarquiaAdministrativa().toString());
            dto.setApi("administrativo/solicitacao-compra-itens/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void novoFiltroHistoricoProcesso() {
        filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.SOLICITACAO_COMPRA);
    }

    public void onTabChange(TabChangeEvent event) {
        String tab = event.getTab().getId();
        if ("tab-historico".equals(tab)) {
            novoFiltroHistoricoProcesso();
        }
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroHistoricoProcesso() {
        return filtroHistoricoProcesso;
    }

    public void setFiltroHistoricoProcesso(FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso) {
        this.filtroHistoricoProcesso = filtroHistoricoProcesso;
    }

    public TipoDoctoOficial getTipoDoctoOficial() {
        return tipoDoctoOficial;
    }

    public void setTipoDoctoOficial(TipoDoctoOficial tipoDoctoOficial) {
        this.tipoDoctoOficial = tipoDoctoOficial;
    }

    public List<TipoDoctoOficial> completarTiposDoctosOficiais(String parte) {
        return facade.getTipoDoctoOficialFacade().buscarTiposDoctoOficialPorModulos(parte, Lists.newArrayList(ModuloTipoDoctoOficial.SOLICITACAO_MATERIAL, ModuloTipoDoctoOficial.TR, ModuloTipoDoctoOficial.DFD));
    }

    public void adicionarDocumentoOficial() throws AtributosNulosException, UFMException {
        try {
            validarAdicionarDocumentoOficial();
            SolicitacaoMaterialDocumentoOficial smdo = facade.salvarNovoDocumentoOficial(tipoDoctoOficial, selecionado);
            Util.adicionarObjetoEmLista(selecionado.getDocumentosOficiais(), smdo);
            mapUltimaVersaoPorTipo.put(tipoDoctoOficial, smdo.getVersao());
            adicionarVoDocumentosOficiais(smdo);
            tipoDoctoOficial = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void imprimirDocumentoOficial(SolicitacaoMaterialDocumentoOficial smdo) {
        facade.getDocumentoOficialFacade().emiteDocumentoOficial(smdo.getDocumentoOficial().getConteudo());
    }

    public void editarDocumentoOficial(SolicitacaoMaterialDocumentoOficial smdo) {
        Web.setCaminhoOrigem("/solicitacao-de-compra/editar/" + selecionado.getId() + "/");
        FacesUtil.redirecionamentoInterno("/documento-oficial/editar/" + smdo.getDocumentoOficial().getId() + "/");
        Web.getSessionMap().put("esperaRetorno", true);
    }

    public void removerDocumentoOficial(SolicitacaoMaterialDocumentoOficial smdo) {
        selecionado.getDocumentosOficiais().remove(smdo);
        facade.getDocumentoOficialFacade().removerExcluindoSolicitacaoMaterialDocumentoOficial(smdo);
        mapUltimaVersaoPorTipo.put(smdo.getDocumentoOficial().getModeloDoctoOficial().getTipoDoctoOficial(), smdo.getVersao() - 1);
        removerVoDocumentoOficial(smdo);
    }

    private void validarAdicionarDocumentoOficial() {
        ValidacaoException ve = new ValidacaoException();
        if (isSolicitacaoAprovada()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A solicitação deve estar aguardando avaliação ou rejeitada para que seja possível adicionar novos documentos ofíciais.");
        }
        if (tipoDoctoOficial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Documento Oficial deve ser informado.");
        }
        ve.lancarException();
    }

    public LeiLicitacao getLeiLicitacao() {
        return leiLicitacao;
    }

    public void setLeiLicitacao(LeiLicitacao leiLicitacao) {
        this.leiLicitacao = leiLicitacao;
    }

    public List<AmparoLegal> completarAmparoLegal(String parte) {
        if (leiLicitacao != null && selecionado.getModalidadeLicitacao() != null) {
            List<AmparoLegal> amparos = facade.getAmparoLegalFacade().buscarPorLeiAndModalidade(parte.trim(), leiLicitacao, selecionado.getModalidadeLicitacao());
            if (!Util.isListNullOrEmpty(amparos)) {
                return amparos;
            }
            FacesUtil.addAtencao("Nenhum amparo legal encontrado para a modalidade " + selecionado.getModalidadeLicitacao() + ".");
        }
        return Collections.emptyList();
    }

    public List<SelectItem> getGrausDePrioridades() {
        return Util.getListSelectItem(TipoGrauPrioridade.values(), false);
    }

    public List<PlanoContratacaoAnualObjetoCompra> completarPcaObjetosComprasPorExercicioEUndiadeAdm(String parte) {
        return facade.getPlanoContratacaoAnualObjetoCompraFacade().buscarPcaObjetosComprasPorExercicioEUndiadeAdm(parte, selecionado.getExercicio(), selecionado.getUnidadeOrganizacional(), null);
    }

    private void atualizarMapUltimaVersaoPorTipo() {
        mapUltimaVersaoPorTipo.clear();
        for (SolicitacaoMaterialDocumentoOficial smDocOficial : selecionado.getDocumentosOficiais()) {
            TipoDoctoOficial tipo = smDocOficial.getDocumentoOficial().getModeloDoctoOficial().getTipoDoctoOficial();
            if (!mapUltimaVersaoPorTipo.containsKey(tipo)) {
                mapUltimaVersaoPorTipo.put(tipo, 0);
            }
            if (smDocOficial.getVersao().compareTo(mapUltimaVersaoPorTipo.get(tipo)) > 0) {
                mapUltimaVersaoPorTipo.put(tipo, smDocOficial.getVersao());
            }
        }
    }

    public boolean isUltimaVersaoDocumentoOficial(SolicitacaoMaterialDocumentoOficial smdo) {
        return mapUltimaVersaoPorTipo.get(smdo.getDocumentoOficial().getModeloDoctoOficial().getTipoDoctoOficial()).compareTo(smdo.getVersao()) == 0;
    }

    private void adicionarVoDocumentosOficiais(SolicitacaoMaterialDocumentoOficial smdo) {
        boolean hasTipoAdicionado = false;
        for (VOSolicitacaoMaterialDocumentoOfical vo : voDocumentosOficiais) {
            if (vo.getTipoDoctoOficial().equals(tipoDoctoOficial)) {
                vo.getDocumentosOficiais().add(smdo);
                hasTipoAdicionado = true;
            }
        }
        if (!hasTipoAdicionado) {
            voDocumentosOficiais.add(new VOSolicitacaoMaterialDocumentoOfical(tipoDoctoOficial, Lists.newArrayList(smdo)));
        }
    }

    private void removerVoDocumentoOficial(SolicitacaoMaterialDocumentoOficial smdo) {
        VOSolicitacaoMaterialDocumentoOfical voVazio = null;
        for (VOSolicitacaoMaterialDocumentoOfical vo : voDocumentosOficiais) {
            vo.getDocumentosOficiais().remove(smdo);
            if (vo.getDocumentosOficiais().isEmpty()) {
                voVazio = vo;
            }
        }
        if (voVazio != null) {
            voDocumentosOficiais.remove(voVazio);
        }
    }

    private void montarVoDocumentosOficiais() {
        voDocumentosOficiais = Lists.newArrayList();
        HashMap<TipoDoctoOficial, List<SolicitacaoMaterialDocumentoOficial>> mapDocs = new HashMap<>();
        if (!selecionado.getDocumentosOficiais().isEmpty()) {
            for (SolicitacaoMaterialDocumentoOficial documento : selecionado.getDocumentosOficiais()) {
                TipoDoctoOficial tipo = documento.getDocumentoOficial().getModeloDoctoOficial().getTipoDoctoOficial();
                if (!mapDocs.containsKey(tipo)) {
                    mapDocs.put(tipo, Lists.newArrayList());
                }
                mapDocs.get(tipo).add(documento);
            }

            for (Map.Entry<TipoDoctoOficial, List<SolicitacaoMaterialDocumentoOficial>> map : mapDocs.entrySet()) {
                voDocumentosOficiais.add(new VOSolicitacaoMaterialDocumentoOfical(map.getKey(), map.getValue()));
            }
        }
    }

    public UnidadeOrganizacional getUnidadeDotacao() {
        if (getProcessoIrp() && participanteIRP != null) {
            HierarquiaOrganizacional hoAdmPart = facade.getParticipanteIRPFacade().buscarUnidadeParticipanteVigente(participanteIRP);
            if (hoAdmPart != null) {
                return hoAdmPart.getSubordinada();
            }
        }
        return hierarquiaOrganizacional.getSubordinada();
    }

    public List<ParticipanteIRP> completarUnidadeParticipanteIrp(String parte) {
        return facade.getParticipanteIRPFacade().buscarParticipantesIRPAprovado(parte.trim(), selecionado.getCotacao().getFormularioCotacao().getIntencaoRegistroPreco());
    }

    public boolean hasSolicitacaoDotacao() {
        return dotacao != null;
    }

    public void novaDotacao() {
        try {
            validarNovaDotacao();
            dotacao = new SolicitacaoCompraDotacao();
            setDespesaORC(null);
            if (getProcessoIrp() && !Util.isListNullOrEmpty(selecionado.getUnidadesParticipantes())) {
                setParticipanteIRP(selecionado.getUnidadesParticipantes().get(0).getParticipanteIRP());
                mapItensParticipanteIrp = Maps.newHashMap();
                for (UnidadeSolicitacaoMaterial unid : selecionado.getUnidadesParticipantes()) {
                    List<ItemParticipanteIRP> itens = facade.getParticipanteIRPFacade().buscarItensUnidadeParticipante(unid.getParticipanteIRP());
                    mapItensParticipanteIrp.put(unid.getParticipanteIRP(), Lists.newArrayList(itens));
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarNovaDotacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCotacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo cotação deve ser informado.");
        }
        ve.lancarException();
    }

    public void processarQuantidadeItemDotacao(SolicitacaoCompraDotacaoItem item) {
        try {
            validarQuantidadeItemDotacao(item);
        } catch (ValidacaoException ve) {
            item.setQuantidade(BigDecimal.ZERO);
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarQuantidadeItemDotacao(SolicitacaoCompraDotacaoItem item) {
        ValidacaoException ve = new ValidacaoException();
        if (item.getQuantidade() == null || item.getQuantidade().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade do item " + item + " deve ser informada com um valor maior que zero(0).");
        }
        if (item.getQuantidade().compareTo(getQuantidadeDisponivelItemDotacao(item)) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade do item  " + item + " ultrapassa a quantidade disponível de "
                + Util.formataQuandoDecimal(getQuantidadeDisponivelItemDotacao(item), item.getItemSolicitacao().getUnidadeMedida().getMascaraQuantidade()) + ".");
        }
        ve.lancarException();
    }

    public BigDecimal getQuantidadeDisponivelItemDotacao(SolicitacaoCompraDotacaoItem itemDotacao) {
        BigDecimal quantidadeUtilizada = getQuantidadeUtilizadaItemDotacao(itemDotacao);

        if (getProcessoIrp()) {
            ItemParticipanteIRP itemPartIrp = getItemParticipanteIrp(itemDotacao.getItemSolicitacao());
            BigDecimal quantidadeSolicitacao = itemPartIrp != null ? itemPartIrp.getQuantidade() : itemDotacao.getItemSolicitacao().getQuantidade();
            return quantidadeSolicitacao.subtract(quantidadeUtilizada);
        }
        return itemDotacao.getItemSolicitacao().getQuantidade().subtract(quantidadeUtilizada);
    }

    public BigDecimal getQuantidadeUtilizadaItemDotacao(SolicitacaoCompraDotacaoItem itemDotacao) {
        return selecionado.getDotacoes().stream()
            .flatMap(dot -> dot.getItens().stream())
            .filter(item -> item.getItemSolicitacao().equals(itemDotacao.getItemSolicitacao()))
            .filter(item -> !item.equals(itemDotacao))
            .map(SolicitacaoCompraDotacaoItem::getQuantidade)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void cancelarDotacao() {
        dotacao = null;
    }

    public void adicionarDotacao() {
        try {
            validarDotacao();
            dotacao.setSolicitacaoCompra(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getDotacoes(), dotacao);
            cancelarDotacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarDotacao() {
        ValidacaoException ve = new ValidacaoException();
        if (despesaORC == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo dotação orçamentária deve ser informado.");
        }
        if (dotacao.getFonteDespesaORC() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo conta de destinação de recurso deve ser informado.");
        }
        dotacao.getItens().stream()
            .filter(item -> !item.hasQuantidade())
            .map(item -> "A quantidade deve ser informada para o item " + item.getItemSolicitacao().getNumero() + ".")
            .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);

        dotacao.getItens().forEach(this::validarQuantidadeItemDotacao);
        ve.lancarException();
    }

    public void editarDotacao(SolicitacaoCompraDotacao dotacao) {
        this.dotacao = dotacao;
        setDespesaORC(dotacao.getFonteDespesaORC().getDespesaORC());
    }

    public void removerDotacao(SolicitacaoCompraDotacao dotacao) {
        selecionado.getDotacoes().remove(dotacao);
    }

    public void criarItemDotacao() {
        dotacao.setItens(Lists.newArrayList());
        TipoObjetoCompra tipoObjetoConfiguracao = facade.getConfigTipoObjetoCompraFacade().getTipoObjetoCompra(facade.getSistemaFacade().getDataOperacao(), despesaORC.getContaDeDespesa());
        if (tipoObjetoConfiguracao != null) {
            List<ItemSolicitacao> itens = mapItensPorTipoObjetoCompra.get(tipoObjetoConfiguracao);
            for (ItemSolicitacao item : itens) {
                novoItemDotacao(item);
            }
        } else {
            for (Map.Entry<TipoObjetoCompra, List<ItemSolicitacao>> entry : mapItensPorTipoObjetoCompra.entrySet()) {
                if (!entry.getKey().isPermanenteOrConsumo()) {
                    for (ItemSolicitacao item : entry.getValue()) {
                        novoItemDotacao(item);
                    }
                }
            }
        }
    }

    private void novoItemDotacao(ItemSolicitacao itemSol) {
        if (getProcessoIrp()) {
            ItemParticipanteIRP itemPartIrp = getItemParticipanteIrp(itemSol);
            if (itemPartIrp != null) {
                SolicitacaoCompraDotacaoItem itemDot = new SolicitacaoCompraDotacaoItem();
                itemDot.setSolicitacaoCompraDotacao(dotacao);
                itemDot.setItemSolicitacao(itemSol);
                itemDot.setQuantidade(itemPartIrp.getQuantidade());
                Util.adicionarObjetoEmLista(dotacao.getItens(), itemDot);
            }
        } else {
            SolicitacaoCompraDotacaoItem itemDot = new SolicitacaoCompraDotacaoItem();
            itemDot.setSolicitacaoCompraDotacao(dotacao);
            itemDot.setItemSolicitacao(itemSol);
            itemDot.setQuantidade(itemSol.getQuantidade());
            Util.adicionarObjetoEmLista(dotacao.getItens(), itemDot);
        }
    }

    private ItemParticipanteIRP getItemParticipanteIrp(ItemSolicitacao itemSol) {
        List<ItemParticipanteIRP> itensPartIrp = mapItensParticipanteIrp.get(participanteIRP);
        for (ItemParticipanteIRP itemIrp : itensPartIrp) {
            if (facade.getParticipanteIRPFacade().isMesmoItemObjetoCompra(itemSol.getDescricaoComplementar(), itemSol.getObjetoCompra(),
                itemIrp.getItemIntencaoRegistroPreco().getDescricaoComplementar(), itemIrp.getItemIntencaoRegistroPreco().getObjetoCompra())) {
                return itemIrp;
            }
        }
        return null;
    }

    public List<SelectItem> getFonteDespesaOrcamentaria() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        if (despesaORC != null) {
            List<FonteDespesaORC> list = facade.getFonteDespesaORCFacade().completaFonteDespesaORC("", despesaORC);
            for (FonteDespesaORC fonte : list) {
                toReturn.add(new SelectItem(fonte, fonte.getProvisaoPPAFonte().getDestinacaoDeRecursos().toString()));
            }
        }
        return toReturn;
    }

    public List<TipoObjetoCompra> getTiposObjetoCompraConfiguracao() {
        if (mapItensPorTipoObjetoCompra != null && !mapItensPorTipoObjetoCompra.isEmpty()) {
            return Lists.newArrayList(mapItensPorTipoObjetoCompra.keySet());
        }
        return null;
    }

    public ConverterAutoComplete getConverterFonteDespesaORC() {
        if (converterFonteDespesaORC == null) {
            converterFonteDespesaORC = new ConverterAutoComplete(FonteDespesaORC.class, facade.getFonteDespesaORCFacade());
        }
        return converterFonteDespesaORC;
    }

    public boolean isPermitidoEdicao() {
        return !isSolicitacaoAprovada() && !hasCotacaoSelecionada();
    }

    public List<VOSolicitacaoMaterialDocumentoOfical> getVoDocumentosOficiais() {
        return voDocumentosOficiais;
    }

    public void setVoDocumentosOficiais(List<VOSolicitacaoMaterialDocumentoOfical> voDocumentosOficiais) {
        this.voDocumentosOficiais = voDocumentosOficiais;
    }

    public SolicitacaoCompraDotacao getDotacao() {
        return dotacao;
    }

    public void setDotacao(SolicitacaoCompraDotacao dotacao) {
        this.dotacao = dotacao;
    }

    public SolicitacaoCompraDotacaoItem getItemDotacao() {
        return itemDotacao;
    }

    public void setItemDotacao(SolicitacaoCompraDotacaoItem itemDotacao) {
        this.itemDotacao = itemDotacao;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public Boolean getQuantidadeMinimaFornecedor() {
        return quantidadeMinimaFornecedor;
    }

    public void setQuantidadeMinimaFornecedor(Boolean quantidadeMinimaFornecedor) {
        this.quantidadeMinimaFornecedor = quantidadeMinimaFornecedor;
    }

    public Boolean getQuantidadeMinimaProposta() {
        return quantidadeMinimaProposta;
    }

    public void setQuantidadeMinimaProposta(Boolean quantidadeMinimaProposta) {
        this.quantidadeMinimaProposta = quantidadeMinimaProposta;
    }

    public Boolean getBloquearEdicao() {
        return bloquearEdicao;
    }

    public void setBloquearEdicao(Boolean bloquearEdicao) {
        this.bloquearEdicao = bloquearEdicao;
    }

    public Boolean isLei14133ModalidadeDispensa() {
        try {
            return leiLicitacao.isLei14133() && selecionado.isDispensaDeLicitacao();
        } catch (NullPointerException npe) {
            return Boolean.FALSE;
        }
    }
}
