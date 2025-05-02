package br.com.webpublico.controle;


import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.OrigemCotacao;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import br.com.webpublico.enums.administrativo.OpcaoRelatorioCotacao;
import br.com.webpublico.enums.administrativo.SituacaoCotacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CotacaoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.TabChangeEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean(name = "cotacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaCotacao", pattern = "/licitacao/cotacao/novo/", viewId = "/faces/administrativo/licitacao/cotacao/edita.xhtml"),
    @URLMapping(id = "editarCotacao", pattern = "/licitacao/cotacao/editar/#{cotacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/cotacao/edita.xhtml"),
    @URLMapping(id = "listarCotacao", pattern = "/licitacao/cotacao/listar/", viewId = "/faces/administrativo/licitacao/cotacao/lista.xhtml"),
    @URLMapping(id = "verCotacao", pattern = "/licitacao/cotacao/ver/#{cotacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/cotacao/visualizar.xhtml"),
    @URLMapping(id = "duplicarCotacao", pattern = "/licitacao/cotacao/duplicar/", viewId = "/faces/administrativo/licitacao/cotacao/edita.xhtml")
})
public class CotacaoControlador extends PrettyControlador<Cotacao> implements Serializable, CRUD {

    @EJB
    private CotacaoFacade facade;
    private ItemCotacao itemCotacaoSelecionado;
    private FornecedorCotacao fornecedorCotacaoSelecionado;
    private List<FornecedorCotacao> fornecedores;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private OpcaoRelatorioCotacao opcaoRelatorioCotacao;
    private List<LoteFormularioCompraVO> lotesVO;
    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;
    private List<ContratoCotacao> contratos;
    private List<ContratoCotacao> contratosFiltrados;
    private ContratoCotacao contratoSelecionado;
    private Boolean hasSolicitacao;

    public CotacaoControlador() {
        super(Cotacao.class);
    }

    @Override
    @URLAction(mappingId = "novaCotacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        inicializarAtributosDaTela();
        selecionado.setDataCotacao(facade.getSistemaFacade().getDataOperacao());
        selecionado.setSituacao(SituacaoCotacao.EM_ELABORACAO);
        lotesVO = Lists.newArrayList();
    }

    @Override
    @URLAction(mappingId = "verCotacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        recuperarObjeto();
        operacao = Operacoes.VER;
        inicializarAtributosDaTela();
        atribuirHierarquiaDaUnidade();
        fornecedores = getFornecedoresDaCotacao();
        opcaoRelatorioCotacao = OpcaoRelatorioCotacao.COM_FORNECEDOR;
        popularFormularioCompraVO();
    }

    @Override
    @URLAction(mappingId = "editarCotacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        try {
            super.editar();
            hasSolicitacao = selecionado.getSituacao().isConcluido() && facade.hasCotacaoUtilizadaEmSolicitacaoDeCompra(selecionado);
            inicializarAtributosDaTela();
            atribuirHierarquiaDaUnidade();
            fornecedores = getFornecedoresDaCotacao();
            popularFormularioCompraVO();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            redirecionarParaVer();
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/licitacao/cotacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public void salvarCotacao() {
        selecionado = facade.salvarCotacao(selecionado);
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        redirecionarParaVer();
    }

    @Override
    public void salvar() {
        try {
            validarCamposObrigatoriosGeral();
            if (isOperacaoEditar() && selecionado.getSituacao().isConcluido() && !facade.hasCotacaoUtilizadaEmSolicitacaoDeCompra(selecionado)) {
                FacesUtil.executaJavaScript("dlgSalvar.show()");
            } else {
                salvarCotacao();
            }
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
            descobrirETratarException(ex);
        }
    }

    public void concluir() {
        try {
            validarCamposDoSelecionado();
            facade.validarDocumentosPlanilhaOrcamentaria(selecionado);
            selecionado = facade.concluirCotacao(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionarParaVer();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
            descobrirETratarException(ex);
        }
    }

    @Override
    public void excluir() {
        try {
            validarCotacaoConcluidaUtilizadaEmSolicitacaoCompra("Exclusão não Permitida!");
            super.excluir();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void popularFormularioCompraVO() {
        setLotesVO(LoteFormularioCompraVO.popularFormularioCompraVOFromCotacao(selecionado.getLotes()));
    }

    private void validarCotacaoConcluidaUtilizadaEmSolicitacaoCompra(String sumaryMsg) {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getSituacao().isConcluido() && facade.hasCotacaoUtilizadaEmSolicitacaoDeCompra(selecionado)) {
            ve.adicionarMensagemWarn(sumaryMsg, "A Cotação/Planilha Orçamentária já está Concluída e utilizada em uma Solicitação de Compra.");
        }
        ve.lancarException();
    }

    private void atribuirHierarquiaDaUnidade() {
        hierarquiaOrganizacional = facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataCotacao(), selecionado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    private void inicializarAtributosDaTela() {
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        contratos = new ArrayList<>();
    }

    private List<FornecedorCotacao> getFornecedoresDaCotacao() {
        HashMap<String, FornecedorCotacao> fornecedoresCotacao = new HashMap<String, FornecedorCotacao>();
        for (ValorCotacao fornecedor : selecionado.getValorCotacao()) {
            String chaveHashMap = fornecedor.getFornecedor().getId() + fornecedor.getOrigemCotacao().name();
            if (fornecedoresCotacao.get(chaveHashMap) == null) {
                FornecedorCotacao fc = new FornecedorCotacao();
                if (fornecedor.getOrigemCotacao().isOrigemBancoPrecoREWEB()) {
                    fc.setContrato(fornecedor.getContrato());
                } else if (fornecedor.getOrigemCotacao().isOrigemBancoExterno()) {
                    fc.setUnidadeExterna(fornecedor.getUnidadeExterna());
                }
                fc.setFornecedor(fornecedor.getFornecedor());
                fc.setOrigemCotacao(fornecedor.getOrigemCotacao());
                fc.setObservacao(fornecedor.getObservacao());
                fornecedoresCotacao.put(chaveHashMap, fc);
            }
        }
        return new ArrayList<>(fornecedoresCotacao.values());
    }

    private void validarCamposDoSelecionado() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(selecionado, ve);

        if (!hasLotes()) {
            ve.adicionarMensagemDeCampoObrigatorio("Ao menos um lote deve ser adicionado.");
        }
        ve.lancarException();
        lotesVO.stream().filter(loteVO -> !loteVO.hasItens())
            .map(loteVO -> "O lote " + loteVO + " não possui nenhum item vinculado.")
            .forEach(ve::adicionarMensagemDeCampoObrigatorio);

        if (selecionado.getFormularioCotacao().getTipoSolicitacao().isCompraServico()) {
            validarCotacao();
        } else if (selecionado.getFormularioCotacao().getTipoSolicitacao().isObraServicoEngenharia()) {
            validarPlanilhaOrcamentaria();
        }
        ve.lancarException();
    }

    public boolean hasLotes() {
        return !Util.isListNullOrEmpty(lotesVO);
    }

    private void validarPlanilhaOrcamentaria() {
        ValidacaoException ve = new ValidacaoException();
        for (LoteFormularioCompraVO loteVO : lotesVO) {
            for (ItemFormularioCompraVO itemVO : loteVO.getItens()) {
                validarItemPlanilhaOrcamentaria(itemVO, ve);
            }
        }
        ve.lancarException();
    }

    private void validarItemPlanilhaOrcamentaria(ItemFormularioCompraVO itemVO, ValidacaoException ve) {
        if (itemVO.getValorUnitario() == null || itemVO.getValorUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O item " + itemVO + " referênte ao lote " + itemVO.getLoteFormularioCompraVO().getNumero() + " não pode ser nulo ou menor que 0(zero).");
        }
    }

    private void validarCotacao() {
        ValidacaoException ve = new ValidacaoException();
        if (!isTodosOsLotesPossuemItens()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Todos os lote devem conter pelo menos um item.");
        }
        if (selecionado.getFormularioCotacao().isItemCotacaoPorFornecedor()) {
            if (!isPossuiAoMenosUmFornecedor()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A cotação deve conter ao menos um fornecedor.");
            } else {
                validarCotacoesDeTodosOsFornecedores();
            }
            validarCadaItemContemAoMenosUmaCotacao();
        }
        if (selecionado.getFormularioCotacao().isItemCotacaoPorValorReferencia()) {
            validarItensLotePorValorReferencia();
        }
        ve.lancarException();
    }

    private void validarItensLotePorValorReferencia() {
        ValidacaoException ve = new ValidacaoException();
        if (hasLotes()) {
            lotesVO.stream().flatMap(loteVO -> loteVO.getItens().stream())
                .filter(itemVO -> itemVO.getObjetoCompra().isObjetoValorReferencia()
                    && (itemVO.getValorUnitario() == null || itemVO.getValorUnitario().compareTo(BigDecimal.ZERO) <= 0))
                .map(itemVO -> "O valor unitário do item " + itemVO.getNumero() + " referente ao lote " + itemVO.getLoteFormularioCompraVO().getNumero()
                    + ", deve ser informado com um valor maior que 0(zero).")
                .forEach(ve::adicionarMensagemDeCampoObrigatorio);
        }
        ve.lancarException();
    }

    private void validarCotacoesDeTodosOsFornecedores() {
        for (FornecedorCotacao fornecedorCotacao : fornecedores) {
            validarCotacoesFornecedor(fornecedorCotacao);
        }
    }

    private void validarCadaItemContemAoMenosUmaCotacao() {
        ValidacaoException ve = new ValidacaoException();
        selecionado.getLotes().stream().flatMap(lote -> lote.getItens().stream())
            .filter(item -> item.getObjetoCompra().getGrupoObjetoCompra().getTipoCotacao().isFornecedor() && !item.isPossuiCotacao())
            .map(item -> "O item " + item + " não foi cotado por nenhum fornecedor.")
            .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);
        ve.lancarException();
    }

    private boolean isTodosOsLotesPossuemItens() {
        return selecionado.getLotes().stream().allMatch(LoteCotacao::hasItens);
    }

    private boolean isPossuiAoMenosUmFornecedor() {
        return fornecedores != null && !fornecedores.isEmpty();
    }

    public List<Pessoa> completaPessoa(String prm) {
        return facade.getFornecedorFacade().listaTodosFornecedoresExcetoUnidadeExterna(prm.trim());
    }

    public List<Contrato> completaContrato(String prm) {
        List<Long> objetoDeCompraIds = obterIdsObjetoDeCompra();
        return facade.getContratoFacade().buscarContratosVigentesNosUltimosDozeMesesParaCotacao(prm, objetoDeCompraIds);
    }

    public void carregarLotesEItensDaCotacao() {
        selecionado.getFornecedores().clear();
        lotesVO.clear();
        selecionado.setFormularioCotacao(facade.getFormularioCotacaoFacade().recuperar(selecionado.getFormularioCotacao().getId()));
        selecionado.setUnidadeOrganizacional(selecionado.getFormularioCotacao().getUnidadeOrganizacional());
        atribuirHierarquiaDaUnidade();

        selecionado.getFormularioCotacao().getLotesFormulario().forEach(loteFormulario -> {
            LoteFormularioCompraVO loteVO = LoteFormularioCompraVO.fromFormulario(loteFormulario);
            LoteCotacao loteCot = LoteCotacao.fromVO(loteVO, selecionado);
            loteVO.setLoteCotacao(loteCot);

            loteFormulario.getItensLoteFormularioCotacao().forEach(itemFormulario -> {
                ItemFormularioCompraVO itemVO = ItemFormularioCompraVO.fromFormulario(itemFormulario, loteVO);
                ItemCotacao itemCot = ItemCotacao.fromVO(itemVO, loteCot);
                itemVO.setItemCotacao(itemCot);

                GrupoContaDespesa grupoContaDespesa = facade.getObjetoCompraFacade().criarGrupoContaDespesa(itemVO.getObjetoCompra().getTipoObjetoCompra(), itemVO.getObjetoCompra().getGrupoObjetoCompra());
                itemVO.getObjetoCompra().setGrupoContaDespesa(grupoContaDespesa);
                itemVO.calcularValorTotal();

                loteVO.setTipoControle(itemVO.getTipoControle());

                Util.adicionarObjetoEmLista(loteVO.getItens(), itemVO);
                Util.adicionarObjetoEmLista(loteCot.getItens(), itemCot);
            });
            Util.adicionarObjetoEmLista(lotesVO, loteVO);
            Util.adicionarObjetoEmLista(selecionado.getLotes(), loteCot);
        });
        if (lotesVO.stream()
            .flatMap(lote -> lote.getItens().stream())
            .anyMatch(item -> item.getObjetoCompra().isObjetoValorReferencia())) {
            FacesUtil.addAtencao("Esse formulário de cotação contém Grupo de Objeto de Compra do tipo \"Referência\", no qual não será possível informar valor por fornecedor para ele na Cotação.");
        }
    }

    public boolean podeAdicionarNovoLote() {
        return selecionado.getFormularioCotacao() != null && !selecionado.getFormularioCotacao().getFormularioIrp();
    }

    public void novoFornecedor() {
        fornecedorCotacaoSelecionado = new FornecedorCotacao();
    }

    public void confirmarUnidadeExterna() {
        fornecedorCotacaoSelecionado.setFornecedor(fornecedorCotacaoSelecionado.getUnidadeExterna().getPessoaJuridica());
        fornecedorCotacaoSelecionado.setUnidadeExterna(fornecedorCotacaoSelecionado.getUnidadeExterna());
        adicionarFornecedorNaLista();
    }

    public void confirmarFornecedor() {
        if (podeConfirmarFornecedor()) {
            if (OrigemCotacao.BANCO_DE_PRECO_RBWEB.equals(fornecedorCotacaoSelecionado.getOrigemCotacao())) {
                ContratoCotacao contratoCotacao = new ContratoCotacao();
                contratoCotacao.setContrato(fornecedorCotacaoSelecionado.getContrato());
                List<ItemContrato> itens = facade.getContratoFacade().buscarItensContrato(fornecedorCotacaoSelecionado.getContrato());
                for (ItemContrato item : itens) {
                    ContratoCotacaoObjetoCompra objeto = new ContratoCotacaoObjetoCompra();
                    objeto.setObjetoCompra(item.getItemAdequado().getObjetoCompra());
                    objeto.setPreco(item.getValorUnitario());
                    contratoCotacao.setObjetosCompra(Lists.newArrayList());
                    contratoCotacao.getObjetosCompra().add(objeto);
                }

                fornecedorCotacaoSelecionado.setFornecedor(fornecedorCotacaoSelecionado.getContrato().getContratado());
                fornecedorCotacaoSelecionado.setContratoCotacao(contratoCotacao);
                adicionarFornecedorNaLista();
            } else if (OrigemCotacao.FORNECEDOR.equals(fornecedorCotacaoSelecionado.getOrigemCotacao())) {
                adicionarFornecedorNaLista();
            } else if (OrigemCotacao.BANCO_DE_PRECO_EXTERNO.equals(fornecedorCotacaoSelecionado.getOrigemCotacao())) {
                confirmarUnidadeExterna();
            }
        }
    }

    public void adicionarFornecedorNaLista() {
        fornecedores = Util.adicionarObjetoEmLista(fornecedores, fornecedorCotacaoSelecionado);
        cancelarFornecedor();
    }

    private boolean podeConfirmarFornecedor() {
        if (fornecedorCotacaoSelecionado.getOrigemCotacao() == null) {
            FacesUtil.addCampoObrigatorio("O campo origem da cotação deve ser informado!");
            return false;
        }
        if (OrigemCotacao.FORNECEDOR.equals(fornecedorCotacaoSelecionado.getOrigemCotacao()) && fornecedorCotacaoSelecionado.getFornecedor() == null) {
            FacesUtil.addCampoObrigatorio("O campo fornecedor deve ser informado!");
            return false;
        }
        if (OrigemCotacao.BANCO_DE_PRECO_RBWEB.equals(fornecedorCotacaoSelecionado.getOrigemCotacao()) && fornecedorCotacaoSelecionado.getContrato() == null) {
            FacesUtil.addCampoObrigatorio("O campo contrato deve ser informado!");
            return false;
        }
        if (OrigemCotacao.BANCO_DE_PRECO_EXTERNO.equals(fornecedorCotacaoSelecionado.getOrigemCotacao()) && fornecedorCotacaoSelecionado.getUnidadeExterna() == null) {
            FacesUtil.addCampoObrigatorio("O campo unidade Externa deve ser informado!");
            return false;
        }
        if (fornecedores != null) {
            List<FornecedorCotacao> fornecedoresFiltrados = Lists.newArrayList();
            if (OrigemCotacao.FORNECEDOR.equals(fornecedorCotacaoSelecionado.getOrigemCotacao())) {
                fornecedoresFiltrados.addAll(fornecedores.stream().filter(fornecedor -> OrigemCotacao.FORNECEDOR.equals(fornecedor.getOrigemCotacao())).collect(Collectors.toList()));

                for (FornecedorCotacao fc : fornecedoresFiltrados) {
                    if (fc.getFornecedor().equals(fornecedorCotacaoSelecionado.getFornecedor())) {
                        FacesUtil.addCampoObrigatorio("O fornecedor informado já está adicionado!");
                        return false;
                    }
                }
            }
            if (OrigemCotacao.BANCO_DE_PRECO_RBWEB.equals(fornecedorCotacaoSelecionado.getOrigemCotacao())) {
                fornecedoresFiltrados.addAll(fornecedores.stream().filter(fornecedor -> OrigemCotacao.BANCO_DE_PRECO_RBWEB.equals(fornecedor.getOrigemCotacao())).collect(Collectors.toList()));

                for (FornecedorCotacao fc : fornecedoresFiltrados) {
                    if (fc.getContrato().equals(fornecedorCotacaoSelecionado.getContrato())) {
                        FacesUtil.addCampoObrigatorio("O fornecedor informado já está adicionado!");
                        return false;
                    }
                }
            }
            if (OrigemCotacao.BANCO_DE_PRECO_EXTERNO.equals(fornecedorCotacaoSelecionado.getOrigemCotacao())) {
                fornecedoresFiltrados.addAll(fornecedores.stream().filter(fornecedor -> OrigemCotacao.BANCO_DE_PRECO_EXTERNO.equals(fornecedor.getOrigemCotacao())).collect(Collectors.toList()));

                for (FornecedorCotacao fc : fornecedoresFiltrados) {
                    if (fc.getUnidadeExterna().equals(fornecedorCotacaoSelecionado.getUnidadeExterna())) {
                        FacesUtil.addCampoObrigatorio("O fornecedor informado já está adicionado!");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void cancelarFornecedor() {
        fornecedorCotacaoSelecionado = null;
    }

    public void removerFornecedor(FornecedorCotacao fc) {
        fornecedores.remove(fc);
        removerValorCotacaoDoFornecedor(fc);
        cancelarFornecedor();
    }

    private void removerValorCotacaoDoFornecedor(FornecedorCotacao fc) {
        for (LoteCotacao lote : selecionado.getLotes()) {
            for (ItemCotacao item : lote.getItens()) {
                ValorCotacao valorCotacaoDoFornecedor = item.getValorCotacaoDoFornecedor(fc.getFornecedor());
                if (valorCotacaoDoFornecedor != null) {
                    item.getValoresCotacao().remove(valorCotacaoDoFornecedor);
                }
            }
        }
    }

    public void selecionarFornecedor(FornecedorCotacao fc) {
        fornecedorCotacaoSelecionado = fc;
        for (LoteCotacao lote : selecionado.getLotes()) {
            for (ItemCotacao item : lote.getItens()) {
                if (item.getObjetoCompra().getGrupoObjetoCompra().getTipoCotacao().isFornecedor()
                    && !item.isFornecedorCotou(fornecedorCotacaoSelecionado.getFornecedor())) {
                    ValorCotacao valor = new ValorCotacao();
                    valor.setItemCotacao(item);
                    valor.setOrigemCotacao(fornecedorCotacaoSelecionado.getOrigemCotacao());
                    valor.setFornecedor(fornecedorCotacaoSelecionado.getFornecedor());
                    valor.setObservacao(fornecedorCotacaoSelecionado.getObservacao());

                    if (OrigemCotacao.BANCO_DE_PRECO_RBWEB.equals(fornecedorCotacaoSelecionado.getOrigemCotacao())) {
                        if (fornecedorCotacaoSelecionado.getContratoCotacao() != null) {
                            for (ContratoCotacaoObjetoCompra oc : fornecedorCotacaoSelecionado.getContratoCotacao().getObjetosCompra()) {
                                if (oc.getObjetoCompra().equals(item.getObjetoCompra())) {
                                    valor.getItemCotacao().setValorUnitario(oc.getPreco());
                                }
                            }

                            valor.setContrato(fornecedorCotacaoSelecionado.getContrato());
                        }
                    } else if (OrigemCotacao.BANCO_DE_PRECO_EXTERNO.equals(fornecedorCotacaoSelecionado.getOrigemCotacao())) {
                        valor.setUnidadeExterna(fornecedorCotacaoSelecionado.getUnidadeExterna());
                    }
                    item.setValoresCotacao(Util.adicionarObjetoEmLista(item.getValoresCotacao(), valor));

                }
            }
        }
    }

    public void confirmarCotacaoDoFornecedor() {
        try {
            validarCotacoesFornecedor(fornecedorCotacaoSelecionado);
            FacesUtil.executaJavaScript("dialogLancamento.hide()");
            cancelarFornecedor();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    private void validarCotacoesFornecedor(FornecedorCotacao fornecedorCotacao) throws ValidacaoException {
        validarValorCotacoesFornecedor(fornecedorCotacao);
        validarMinimoCotacaoFornecedor(fornecedorCotacao);
    }

    private void validarMinimoCotacaoFornecedor(FornecedorCotacao fornecedorCotacao) {
        List<ValorCotacao> valoresCotacaoDoFornecedor = getValoresCotacaoDoFornecedor(fornecedorCotacao);
        if (valoresCotacaoDoFornecedor != null && !valoresCotacaoDoFornecedor.isEmpty()) {
            for (ValorCotacao valorCotacao : valoresCotacaoDoFornecedor) {
                if (valorCotacao.getPreco() != null && valorCotacao.getPreco().compareTo(BigDecimal.ZERO) > 0) {
                    return;
                }
            }
        }
        ValidacaoException ve = new ValidacaoException();
        ve.adicionarMensagemDeOperacaoNaoPermitida("O fornecedor " + fornecedorCotacao.getFornecedor() +
            " não cotou nenhum item. Por favor realize a cotação de ao menos um item, ou remova o fornecedor. ");
        ve.lancarException();
    }

    private void validarValorCotacoesFornecedor(FornecedorCotacao fornecedorCotacao) {
        ValidacaoException ve = new ValidacaoException();
        List<ValorCotacao> valoresCotacaoPorFornecedor = getValoresCotacaoDoFornecedor(fornecedorCotacao);
        if (valoresCotacaoPorFornecedor != null && !valoresCotacaoPorFornecedor.isEmpty()) {
            for (ValorCotacao valor : valoresCotacaoPorFornecedor) {
                if (valor.getMarca() != null && !valor.getMarca().isEmpty() &&
                    (valor.getPreco() == null || valor.getPreco().compareTo(BigDecimal.ZERO) <= 0)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O item " + valor.getItemCotacao().getObjetoCompra().getDescricao() +
                        " possui marca informada, porem não possui o preço. Por favor informe o preço para este item, ou remova a marca caso não queira cota-lo. ");
                }
                if (valor.getPreco() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Preço deve ser informado para o item " + valor.getItemCotacao().getObjetoCompra().getDescricao() + ".");
                }
                if (valor.getPreco().compareTo(BigDecimal.ZERO) < 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O item " + valor.getItemCotacao().getObjetoCompra().getDescricao() + " não pode ficar negativo.");
                }
                ve.lancarException();
            }
        }
        ve.lancarException();
    }

    public void cancelarCotacaoDoFornecedor() {
        removerValorCotacaoDoFornecedor(fornecedorCotacaoSelecionado);
        cancelarFornecedor();
    }

    public ItemCotacao getItemCotacaoSelecionado() {
        return itemCotacaoSelecionado;
    }

    public void setItemCotacaoSelecionado(ItemCotacao itemCotacaoSelecionado) {
        this.itemCotacaoSelecionado = itemCotacaoSelecionado;
    }

    public List<FornecedorCotacao> getFornecedores() {
        return fornecedores;
    }

    public void setFornecedores(List<FornecedorCotacao> fornecedores) {
        this.fornecedores = fornecedores;
    }

    public FornecedorCotacao getFornecedorCotacaoSelecionado() {
        return fornecedorCotacaoSelecionado;
    }

    public void setFornecedorCotacaoSelecionado(FornecedorCotacao fornecedorCotacaoSelecionado) {
        this.fornecedorCotacaoSelecionado = fornecedorCotacaoSelecionado;
    }

    public List<ValorCotacao> getValoresCotacaoDoFornecedor(FornecedorCotacao fornecedorCotacao) {
        List<ValorCotacao> valores = Lists.newArrayList();
        if (fornecedorCotacao != null) {
            for (LoteCotacao loteCotacao : selecionado.getLotes()) {
                for (ItemCotacao itemCotacao : loteCotacao.getItens()) {
                    if (itemCotacao.getObjetoCompra().getGrupoObjetoCompra().getTipoCotacao().isFornecedor()) {
                        ValorCotacao valorCotacao = itemCotacao.getValorCotacaoDoFornecedor(fornecedorCotacao.getFornecedor());
                        if (valorCotacao != null) {
                            valores.add(valorCotacao);
                        }
                    }
                }
            }
        }
        return valores;
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

    public boolean renderizarAbaFornecedores() {
        if (selecionado.getFormularioCotacao() == null) {
            return false;
        }
        return selecionado.getFormularioCotacao().getTipoSolicitacao().isCompraServico() &&
            selecionado.getFormularioCotacao().isItemCotacaoPorFornecedor();
    }

    public OpcaoRelatorioCotacao getOpcaoRelatorioCotacao() {
        return opcaoRelatorioCotacao;
    }

    public void setOpcaoRelatorioCotacao(OpcaoRelatorioCotacao opcaoRelatorioCotacao) {
        this.opcaoRelatorioCotacao = opcaoRelatorioCotacao;
    }

    public List<SelectItem> getOpcoes() {
        return Util.getListSelectItemSemCampoVazio(OpcaoRelatorioCotacao.values());
    }

    public void gerarRelatorio(Cotacao cotacao) {
        if (cotacao != null) {
            selecionado = cotacao;
            setOpcaoRelatorioCotacao(OpcaoRelatorioCotacao.COM_FORNECEDOR);
            hierarquiaOrganizacional = facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                selecionado.getUnidadeOrganizacional(),
                facade.getSistemaFacade().getDataOperacao());
            gerarRelatorio("PDF");
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCamposRelatorio();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("ID_COTACAO", selecionado.getId());
            dto.adicionarParametro("opcaoRelatorioCotacao", opcaoRelatorioCotacao.getToDto());
            dto.adicionarParametro("SECRETARIA", hierarquiaOrganizacional.toString());
            dto.adicionarParametro("MODULO", "Administrativo");
            dto.setNomeRelatorio(OpcaoRelatorioCotacao.PRECOS_MINIMO_MEDIO_MEDIANO_MAXIMO.equals(opcaoRelatorioCotacao) ? "Relatório Detalhado da Cotação" : "Relatório de Cotação " + opcaoRelatorioCotacao.getDescricao());
            dto.setApi("administrativo/cotacao/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCamposRelatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (opcaoRelatorioCotacao == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Relatório deve ser informado.");
        }
        ve.lancarException();
    }

    @URLAction(mappingId = "duplicarCotacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void duplicar() {
        selecionado = (Cotacao) Web.pegaDaSessao("duplicarCotacao");
        atribuirHierarquiaDaUnidade();
        if (selecionado != null) {
            selecionado.setId(null);

            selecionado.setSituacao(SituacaoCotacao.EM_ELABORACAO);
            selecionado.setNumero(null);
            selecionado.setDataCotacao(facade.getSistemaFacade().getDataOperacao());
            selecionado.setValidadeCotacao(null);

            List<LoteCotacao> lotes = Lists.newArrayList();
            List<LoteCotacao> lotesDoSelecionado = selecionado.getLotes();

            for (LoteCotacao lote : lotesDoSelecionado) {
                LoteCotacao novoLote = (LoteCotacao) Util.clonarObjeto(lote);
                if (novoLote != null) {
                    novoLote.setCotacao(selecionado);
                    novoLote.setId(null);
                    novoLote.setItens(Lists.<ItemCotacao>newArrayList());
                }
                for (ItemCotacao item : lote.getItens()) {
                    ItemCotacao novoItem = (ItemCotacao) Util.clonarObjeto(item);
                    if (novoItem != null) {
                        novoItem.setValoresCotacao(Lists.<ValorCotacao>newArrayList());
                        novoItem.setId(null);
                        novoItem.setLoteCotacao(novoLote);
                        if (novoLote != null) {
                            novoLote.getItens().add(novoItem);
                        }
                    }
                }
                lotes.add(novoLote);
            }

            selecionado.setLotes(lotes);
            fornecedores = null;
        }
    }

    public void redirecionarParaDuplicar() {
        Date dataAtual = new Date();
        if (dataAtual.after(selecionado.getValidadeCotacao())) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "duplicar/");
            Web.poeNaSessao("duplicarCotacao", selecionado);
        } else {
            FacesUtil.addOperacaoNaoPermitida("Operação não Permitida! Não é permitido duplicar uma cotação vigente, a mesma pode ser utilizada na solicitação de compra");
        }
    }

    public void validarCamposObrigatoriosGeral() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(selecionado, ve);
        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        if (selecionado.getDataCotacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A data da cotação/planilha orçamentária deve ser informada.");
        }
        if (selecionado.getValidadeCotacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A validade da cotação/planilha orçamentária deve ser informada.");
        }
        if (selecionado.getFormularioCotacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Formulário deve ser informado.");
        }
        if (selecionado.getUnidadeOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Administrativa não deve ser nulo.");
        }
        if (selecionado.getDescricao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Descrição deve ser preenchido.");
        }
        ve.lancarException();
    }

    private void novoFiltroHistoricoProcesso() {
        filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.COTACAO);
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

    public List<SelectItem> listarOrigensCotacao() {
        return Util.getListSelectItem(Arrays.asList(OrigemCotacao.values()));
    }

    public List<UnidadeExterna> completaUnidadeExterna(String parte) {
        return facade.getUnidadeExternaFacade().listaUnidadeExternaDiferenteDeEsferaGovernoPrivada(parte.trim());
    }

    public List<ContratoCotacao> getContratos() {
        return contratos;
    }

    public void setContratos(List<ContratoCotacao> contratos) {
        this.contratos = contratos;
    }

    public List<ContratoCotacao> getContratosFiltrados() {
        return contratosFiltrados;
    }

    public void setContratosFiltrados(List<ContratoCotacao> contratosFiltrados) {
        this.contratosFiltrados = contratosFiltrados;
    }

    public ContratoCotacao getContratoSelecionado() {
        return contratoSelecionado;
    }

    public void setContratoSelecionado(ContratoCotacao contratoSelecionado) {
        this.contratoSelecionado = contratoSelecionado;
    }

    public void buscarContratoCotacao() {
        List<Long> objetoDeCompraIds = obterIdsObjetoDeCompra();
        List<Contrato> contratoes = facade.getContratoFacade().buscarContratosVigentesNosUltimosDozeMesesParaCotacao("", objetoDeCompraIds);
        contratos.clear();
        for (Contrato contrato : contratoes) {
            ContratoCotacao contratoCotacao = new ContratoCotacao();
            contratoCotacao.setContrato(contrato);
            List<ItemContrato> itens = facade.getContratoFacade().buscarItensContrato(contrato);
            for (ItemContrato item : itens) {
                ContratoCotacaoObjetoCompra objeto = new ContratoCotacaoObjetoCompra();
                objeto.setObjetoCompra(item.getItemAdequado().getObjetoCompra());
                objeto.setPreco(item.getValorUnitario());
                contratoCotacao.setObjetosCompra(Lists.newArrayList());
                contratoCotacao.getObjetosCompra().add(objeto);
            }
            contratos.add(contratoCotacao);
        }
    }

    private List<Long> obterIdsObjetoDeCompra() {
        List<Long> objetoDeCompraIds = new ArrayList<>();
        selecionado.getLotes().forEach(loteCotacao -> {
            loteCotacao.getItens().forEach(itemCotacao -> {

                objetoDeCompraIds.add(itemCotacao.getObjetoCompra().getId());
            });
        });
        return objetoDeCompraIds;
    }

    public void confirmarContrato() {
        novoFornecedor();
        if (contratoSelecionado != null) {
            fornecedorCotacaoSelecionado.setContrato(contratoSelecionado.getContrato());
            fornecedorCotacaoSelecionado.setContratoCotacao(contratoSelecionado);
            fornecedorCotacaoSelecionado.setFornecedor(contratoSelecionado.getContrato().getContratado());
            fornecedorCotacaoSelecionado.setOrigemCotacao(OrigemCotacao.BANCO_DE_PRECO_RBWEB);

            cancelarContrato();
        } else {
            FacesUtil.addCampoObrigatorio("O campo contrato deve ser informado!");
        }
    }

    public void cancelarContrato() {
        setContratoSelecionado(null);
        FacesUtil.executaJavaScript("dialogPesquisaContrato.hide()");
    }

    public Boolean getHasSolicitacao() {
        return hasSolicitacao;
    }

    public void setHasSolicitacao(Boolean hasSolicitacao) {
        this.hasSolicitacao = hasSolicitacao;
    }

    public List<LoteFormularioCompraVO> getLotesVO() {
        return lotesVO;
    }

    public void setLotesVO(List<LoteFormularioCompraVO> lotesVO) {
        this.lotesVO = lotesVO;
    }
}
