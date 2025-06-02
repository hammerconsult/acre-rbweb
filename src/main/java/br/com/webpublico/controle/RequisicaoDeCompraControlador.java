/*
 * Codigo gerado automaticamente em Wed Feb 22 16:41:13 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.entidadesauxiliares.administrativo.FiscalContratoDTO;
import br.com.webpublico.entidadesauxiliares.contabil.LiquidacaoVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.administrativo.OpcaoRelatorioReqCompra;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ItemLicitatorioContrato;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.RequisicaoDeCompraFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean(name = "requisicaoDeCompraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-requisicao-compra", pattern = "/requisicao-compra/novo/", viewId = "/faces/administrativo/licitacao/requisicaodecompra/edita.xhtml"),
    @URLMapping(id = "editar-requisicao-compra", pattern = "/requisicao-compra/editar/#{requisicaoDeCompraControlador.id}/", viewId = "/faces/administrativo/licitacao/requisicaodecompra/edita.xhtml"),
    @URLMapping(id = "ver-requisicao-compra", pattern = "/requisicao-compra/ver/#{requisicaoDeCompraControlador.id}/", viewId = "/faces/administrativo/licitacao/requisicaodecompra/visualizar.xhtml"),
    @URLMapping(id = "listar-requisicao-compra", pattern = "/requisicao-compra/listar/", viewId = "/faces/administrativo/licitacao/requisicaodecompra/lista.xhtml")
})
public class RequisicaoDeCompraControlador extends PrettyControlador<RequisicaoDeCompra> implements Serializable, CRUD {

    @EJB
    private RequisicaoDeCompraFacade facade;
    private ItemRequisicaoCompraVO itemRequisicaoDesdobravel;
    private ItemRequisicaoCompraVO itemRequisicao;
    private ItemRequisicaoCompraExecucaoVO itemRequisicaoExecucao;
    private HierarquiaOrganizacional hierarquiaAdm;
    private Contrato contrato;
    private AtaRegistroPreco ataRegistroPreco;
    private DispensaDeLicitacao dispensaDeLicitacao;
    private ReconhecimentoDivida reconhecimentoDivida;
    private List<TipoContaDespesa> tiposContaDespesa;
    private List<ItemRequisicaoCompraDerivacao> itensDerivacao;
    private List<RequisicaoCompraExecucaoVO> execucoesVO;
    private List<ItemRequisicaoCompraVO> itensRequisicaoVO;
    private List<ItemRequisicaoCompraVO> itensRequisicaoDesdobradoVO;
    private List<ItemRequisicaoProcessoLicitatorio> itensProcesso;
    private List<DescontoItemRequisicaoCompra> descontosItemRequisicao;
    private Set<Pessoa> fornecedorSet;

    private DoctoFiscalEntradaCompra doctoFiscalEntradaCompra;
    private DoctoFiscalSolicitacaoAquisicao doctoFiscalSolicitacaoAquisicao;
    private ItemDoctoItemAquisicao itemDoctoSolicitacaoAquisicao;
    private List<SolicitacaoAquisicao> solicitacoesAquisicoes;
    private List<EntradaCompraMaterial> entradasComprasMateriais;
    private List<RequisicaoCompraEstorno> estornosRequisicaoCompra;
    private List<LiquidacaoVO> liquidacoesVO;
    private List<Contrato> contratos;

    private OpcaoRelatorioReqCompra opcaoRelatorioReqCompra;
    private Boolean hasPermissaoDesdobrarSemGrupo;
    private FiltroContratoRequisicaoCompra filtroContrato;
    private String origemContrato;
    private List<GestorContrato> gestores;
    private List<FiscalContratoDTO> fiscais;

    public RequisicaoDeCompraControlador() {
        super(RequisicaoDeCompra.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/requisicao-compra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    @URLAction(mappingId = "novo-requisicao-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataRequisicao(facade.getSistemaFacade().getDataOperacao());
        selecionado.setCriadoPor(facade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica());
        selecionado.setTipoContaDespesa(TipoContaDespesa.NAO_APLICAVEL);
        hasPermissaoDesdobrarSemGrupo = false;
        fiscais = Lists.newArrayList();
        gestores = Lists.newArrayList();
        itensDerivacao = Lists.newArrayList();
        itensRequisicaoVO = Lists.newArrayList();
        execucoesVO = Lists.newArrayList();
        fornecedorSet = Sets.newHashSet();
        novaPesquisaContratos();
    }

    @Override
    @URLAction(mappingId = "editar-requisicao-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        try {
            super.editar();
            itensRequisicaoVO = Lists.newArrayList();
            liquidacoesVO = Lists.newArrayList();
            validarDisponibilidadeMovimentacao();
            atribuirVariavelProcesso();
            buscarExecucoesEmpenhadas();
            selecionarRequisicaoExecucaoSalva();
            buscarItensProcesso();
            buscarTiposContaDespesaEmpenho();
            criarItemRequisicaoCompraVOAndItemRequisicaoExecucaoVO(itensProcesso);
            separarItensPorTipoContaDespesaEmpenho();
            preencherItemDesdobradoEdicao();
            preecherItemRequisicaoDerivacaoEdicao();
            hasPermissaoDesdobrarSemGrupo = facade.getConfiguracaoLicitacaoFacade().verificarUnidadeGrupoObjetoCompra(getUnidadeAdministrativa());
            Collections.sort(itensRequisicaoVO);
            fornecedorSet = Sets.newHashSet();
        } catch (ValidacaoException ve) {
            redirecionarParaRequisicaoCompra(selecionado);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @Override
    @URLAction(mappingId = "ver-requisicao-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        atribuirVariavelProcesso();
        preencherItemDesdobradoVisualizacao();
        preecherItemRequisicaoVOVisualizacao();
        buscarRecuperandoEntradasComprasMateriais();
        buscarRecuperandoSolicitacoesAquisicoes();
        setOpcaoRelatorioReqCompra(OpcaoRelatorioReqCompra.COM_DESCRICAO);
        estornosRequisicaoCompra = facade.getRequisicaoCompraEstornoFacade().buscarPorRequisicaoCompra(selecionado);
        liquidacoesVO = Lists.newArrayList();
        if (selecionado.isTipoContrato()) {
            origemContrato = facade.getContratoFacade().getOrigemContrato(contrato);
        }
    }

    private void preecherItemRequisicaoVOVisualizacao() {
        itensRequisicaoVO = Lists.newArrayList();

        for (ItemRequisicaoDeCompra itemReq : selecionado.getItens()) {
            ItemRequisicaoCompraVO itemReqVO = ItemRequisicaoCompraVO.toObjeto(itemReq);
            facade.recuperarGrupoContaDespesa(itemReq.getObjetoCompra());

            for (ItemRequisicaoCompraExecucao itemReqEx : itemReq.getItensRequisicaoExecucao()) {
                ItemRequisicaoCompraExecucaoVO itemReqExVO = ItemRequisicaoCompraExecucaoVO.toObjeto(itemReqEx, itemReqVO);
                boolean isAjusteValor = false;
                if (selecionado.isTipoContrato()) {
                    isAjusteValor = isItemExecucaoContratoAjusteValor(itemReqEx.getExecucaoContratoItem());
                } else if (selecionado.isTipoExecucaoProcesso()) {
                    isAjusteValor = isItemExecucaoProcessoAjusteValor(itemReqEx.getExecucaoProcessoItem());
                }
                itemReqExVO.setAjusteValor(isAjusteValor);

                RequisicaoCompraEmpenhoVO novoReqEmpVO = facade.novoEmpenhoVO(itemReqEx.getEmpenho(), selecionado.getTipoObjetoCompra());
                itemReqExVO.setRequisicaoEmpenhoVO(novoReqEmpVO);
                itemReqVO.getItensRequisicaoExecucao().add(itemReqExVO);
            }
            Collections.sort(itemReqVO.getItensRequisicaoExecucao());
            itensRequisicaoVO.add(itemReqVO);
        }
        Collections.sort(itensRequisicaoVO);
    }

    private void atribuirVariavelProcesso() {
        if (selecionado.isTipoContrato()) {
            contrato = selecionado.getContrato();
        }
        if (selecionado.isTipoAtaRegistroPreco()) {
            ataRegistroPreco = selecionado.getExecucaoProcesso().getAtaRegistroPreco();
        }
        if (selecionado.isTipoDispensaInexigibilidade()) {
            ExecucaoProcesso execucaoProcesso = selecionado.getExecucaoProcesso();
            dispensaDeLicitacao = execucaoProcesso.getDispensaLicitacao();
        }
        if (selecionado.isTipoReconhecimentoDivida()) {
            reconhecimentoDivida = selecionado.getReconhecimentoDivida();
        }
    }

    @Override
    public void salvar() {
        try {
            validarRegrasDeNegocio();
            atribuirDerivacaoComponenteNaEspecificacaoItemRequisicaoVO();
            criarItemRequisicaoCompraAoSalvar();
            criarRequisicaoCompraExecucaoAoSalvar();
            selecionado = facade.salvarRequisicao(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionarParaRequisicaoCompra(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
            logger.error("Erro ao salvar selecionado.: {} cause.: {} ", selecionado, ex.getCause());
        }
    }

    public void efetivar() {
        try {
            selecionado.setSituacaoRequisicaoCompra(SituacaoRequisicaoCompra.EFETIVADA);
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionarParaRequisicaoCompra(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
            logger.error("Erro ao efetivar a requisição de compra.: {} cause.: {} ", selecionado, ex.getCause());
        }
    }

    private void atribuirDerivacaoComponenteNaEspecificacaoItemRequisicaoVO() {
        itensRequisicaoVO.stream()
            .filter(ItemRequisicaoCompraVO::isDerivacaoObjetoCompra)
            .forEach(item -> item.setDescricaoComplementar(item.getDescricaoComplementar() + " " + item.getDerivacaoComponente().getDescricao()));
    }

    public boolean existeDescricaoPrazoEntrega() {
        return selecionado.getDescricaoPrazoEntrega() != null;
    }

    public void redirecionaOrigemContrato() {
        if (contrato.isDeLicitacao() || contrato.isDeAdesaoAtaRegistroPrecoInterna() || contrato.isDeIrp()) {
            FacesUtil.redirecionamentoInterno("/licitacao/ver/" + contrato.getLicitacao().getId() + "/");
            return;
        }
        if (contrato.isDeDispensaDeLicitacao()) {
            FacesUtil.redirecionamentoInterno("/dispensa-licitacao/ver/" + contrato.getDispensaDeLicitacao().getId() + "/");
            return;
        }
        if (contrato.isDeRegistroDePrecoExterno()) {
            FacesUtil.redirecionamentoInterno("/adesao-a-ata-de-registro-de-preco-externo/ver/" + contrato.getContratoRegistroPrecoExterno().getRegistroSolicitacaoMaterialExterno().getId() + "/");
            return;
        }
        if (contrato.isDeProcedimentosAuxiliares()) {
            FacesUtil.redirecionamentoInterno("/credenciamento/ver/" + contrato.getLicitacao().getId() + "/");
        }
    }

    public void redirecionarParaTipoRequisicao(RequisicaoDeCompra requisicao) {
        switch (requisicao.getTipoRequisicao()) {
            case CONTRATO:
                FacesUtil.redirecionamentoInterno("/contrato-adm/ver/" + requisicao.getContrato().getId() + "/");
                break;
            case RECONHECIMENTO_DIVIDA:
                FacesUtil.redirecionamentoInterno("/reconhecimento-divida/ver/" + requisicao.getReconhecimentoDivida().getId() + "/");
                break;
            case ATA_REGISTRO_PRECO:
                FacesUtil.redirecionamentoInterno("/ata-registro-preco/ver/" + requisicao.getAtaRegistroPreco().getId() + "/");
                break;
            case DISPENSA_LICITACAO_INEXIGIBILIDADE:
                FacesUtil.redirecionamentoInterno("/dispensa-licitacao/ver/" + requisicao.getDispensaLicitacao().getId() + "/");
                break;
        }
    }

    private void buscarRecuperandoEntradasComprasMateriais() {
        entradasComprasMateriais = Lists.newArrayList();
        facade.getEntradaMaterialFacade().buscarEntradasComprasMateriaisPorRequisicao(selecionado).forEach(
            ecm -> entradasComprasMateriais.add((EntradaCompraMaterial) facade.getEntradaMaterialFacade().recuperar(ecm.getId()))
        );
    }

    private void buscarRecuperandoSolicitacoesAquisicoes() {
        solicitacoesAquisicoes = Lists.newArrayList();
        List<SolicitacaoAquisicao> solicitacoesAquisicao = facade.getSolicitacaoAquisicaoFacade().buscarSolicitacoesPorRequisicao(selecionado);
        solicitacoesAquisicao.forEach(sol -> solicitacoesAquisicoes.add(facade.getSolicitacaoAquisicaoFacade().recuperarSolicitacaoAquisicaoComItensSolicitacaoPorItemDocto(sol))
        );
    }

    public void validarDisponibilidadeMovimentacao() {
        ValidacaoException ve = new ValidacaoException();
        if (!isOperacaoNovo() && facade.isRequisicaoCompraCardapio(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A requisição de compra de número " + selecionado.getNumero() + " está integrada com o módulo de alimentação escolar, portanto não é possível realizar alterações.");
        }
        if (!selecionado.isEmElaboracao() || facade.isRequisicaoCompraEntradaPorCompra(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A requisição de compra de número " + selecionado.getNumero() + " já está sendo utilizada, portanto não é possível realizar alterações.");
        }
        ve.lancarException();
    }

    @Override
    public void excluir() {
        try {
            validarDisponibilidadeMovimentacao();
            super.excluir();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
            logger.error("Erro ao excluir selecionado {} ", selecionado, ex.getCause());
        }
    }

    private void criarRequisicaoCompraExecucaoAoSalvar() {
        Set<Empenho> empenhosSet = selecionado.getItens()
            .stream()
            .flatMap(item -> item.getItensRequisicaoExecucao().stream())
            .map(ItemRequisicaoCompraExecucao::getEmpenho).collect(Collectors.toSet());

        selecionado.setExecucoes(Lists.<RequisicaoCompraExecucao>newArrayList());
        execucoesVO.stream().filter(RequisicaoCompraExecucaoVO::getSelecionado).forEach(exec -> {
            exec.getEmpenhosVO().stream().filter(RequisicaoCompraEmpenhoVO::getSelecionado).forEach(emp -> {
                if (empenhosSet.contains(emp.getEmpenho())) {
                    RequisicaoCompraExecucao novoReqExec = new RequisicaoCompraExecucao();
                    novoReqExec.setRequisicaoCompra(selecionado);
                    novoReqExec.setExecucaoContratoEmpenho(emp.getExecucaoContratoEmpenho());
                    novoReqExec.setExecucaoProcessoEmpenho(emp.getExecucaoProcessoEmpenho());
                    novoReqExec.setExecucaoReconhecimentoDiv(emp.getExecucaoReconhecimentoDivida());
                    Util.adicionarObjetoEmLista(selecionado.getExecucoes(), novoReqExec);
                }
            });
        });
    }

    private void criarItemRequisicaoCompraAoSalvar() {
        selecionado.setItens(Lists.<ItemRequisicaoDeCompra>newArrayList());
        itensRequisicaoVO.stream().filter(ItemRequisicaoCompraVO::hasQuantidade).forEach(itemVO -> {
            ItemRequisicaoDeCompra novoItemReq = ItemRequisicaoDeCompra.toVO(itemVO);
            criarEstruturaItensRequisicao(itemVO, novoItemReq);
        });

        itensRequisicaoVO.stream()
            .filter(ItemRequisicaoCompraVO::hasItensDesdobrados)
            .flatMap(itemReqVO -> itemReqVO.getItensDesdobrados().stream())
            .forEach(itemReqDesdVO -> {
                ItemRequisicaoDeCompra novoItemReq = ItemRequisicaoDeCompra.toVO(itemReqDesdVO);
                criarEstruturaItensRequisicao(itemReqDesdVO, novoItemReq);
            });
    }

    private void criarEstruturaItensRequisicao(ItemRequisicaoCompraVO itemVO, ItemRequisicaoDeCompra novoItemReq) {
        itemVO.getItensRequisicaoExecucao().forEach(itemReqExecVO -> {
            if (itemReqExecVO.hasQuantidade()) {
                ItemRequisicaoCompraExecucao novoItemReqProc = ItemRequisicaoCompraExecucao.toVO(itemReqExecVO, novoItemReq);
                Util.adicionarObjetoEmLista(novoItemReq.getItensRequisicaoExecucao(), novoItemReqProc);
            }
        });
        Util.adicionarObjetoEmLista(selecionado.getItens(), novoItemReq);
    }

    public void selecionarEspecificacao(ActionEvent evento) {
        ObjetoDeCompraEspecificacao especificacao = (ObjetoDeCompraEspecificacao) evento.getComponent().getAttributes().get("objeto");
        itemRequisicao.setDescricaoComplementar(especificacao.getTexto());
        atribuirGrupoAoItemRequisicao();
    }

    public void limparEspecificacao() {
        itemRequisicao.setDescricaoComplementar(null);
    }

    public void validarEspecificacao() {
        ValidacaoException ve = new ValidacaoException();
        if (Util.isStringNulaOuVazia(itemRequisicao.getDescricaoComplementar())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Especificação deve ser preenchido.");
        }
        ve.lancarException();
    }

    private void validarRegrasDeNegocio() {
        ValidacaoException ex = new ValidacaoException();
        validarCamposObrigatorio();
        validarItensRequisicao(ex);
        validarQuantidadeRequisitada(ex);
        validarDataRequisicao(ex);
        ex.lancarException();
    }

    private void validarCamposObrigatorio() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (isProcessoNulo()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo " + selecionado.getTipoRequisicao().getDescricao() + " deve ser informado.");

        } else if (!hasExecucaoSelecionada()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar, é necessário selecionar pelo menos uma execução para a requisição de compra.");
        }
        if (fornecedorSet.size() > 1) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É permitido selecionar execuções de um único fornecedor.");
        }
        ve.lancarException();
    }

    private boolean isProcessoNulo() {
        return contrato == null && ataRegistroPreco == null && dispensaDeLicitacao == null && reconhecimentoDivida == null;
    }

    private void validarItensRequisicao(ValidacaoException ex) {
        if (!hasItensRequisicao()) {
            ex.adicionarMensagemDeOperacaoNaoRealizada("A requisição de compra não encontrou itens de " + selecionado.getTipoObjetoCompra().getDescricao() + " disponíveis para continuar.");
        }
        if (!permiteDesdobrarItens() && !hasItemRequisicaoSelecionado()) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("Informe a quantidade desejada para ao menos um item para continuar.");
        }
        if (permiteDesdobrarItens() && itensRequisicaoVO.stream().noneMatch(ItemRequisicaoCompraVO::hasItensDesdobrados)) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("Informe o desdobramento para ao menos um item para continuar.");
        }
        ex.lancarException();
    }

    private void validarQuantidadeRequisitada(ValidacaoException ex) {
        for (ItemRequisicaoCompraVO itemRequisicao : itensRequisicaoVO) {
            if (!itemRequisicao.isDerivacaoObjetoCompra()) {
                for (ItemRequisicaoCompraExecucaoVO item : itemRequisicao.getItensRequisicaoExecucao()) {
                    if (item.getQuantidade() == null) {
                        ex.adicionarMensagemDeOperacaoNaoPermitida("Deve ser informada a quantidade do item " + item.getItemRequisicaoCompraVO().getDescricao() + ".");
                    }
                    if (item.getQuantidade() != null && item.getQuantidade().compareTo(BigDecimal.ZERO) < 0) {
                        ex.adicionarMensagemDeOperacaoNaoPermitida("A quantidade informada para o item " + item.getItemRequisicaoCompraVO().getDescricao() + " deve ser maior que 0 (zero).");
                    }
                    if (!permiteDesdobrarItens()) {
                        if (item.getQuantidade() != null
                            && item.getQuantidadeDisponivel() != null
                            && itemRequisicao.isControleQuantidade()
                            && item.getQuantidade().compareTo(item.getQuantidadeDisponivel()) > 0) {
                            ex.adicionarMensagemDeOperacaoNaoPermitida("A quantidade informada para o item " + item.getItemRequisicaoCompraVO().getDescricao() + " não pode ser maior que " + Util.formataValorSemCifrao(item.getQuantidadeDisponivel()) + ".");
                        }
                    }
                }
            }
        }
        ex.lancarException();
    }

    private void validarDataRequisicao(ValidacaoException ex) {
        Date menorData = facade.getEmpenhoFacade().buscarMenorDataEmpenhosPorRequisicaoDeCompra(getIdsEmpenhos());
        if (menorData == null) {
            switch (selecionado.getTipoRequisicao()) {
                case CONTRATO:
                    menorData = contrato.getDataAprovacao();
                    break;
                case RECONHECIMENTO_DIVIDA:
                    menorData = reconhecimentoDivida.getDataReconhecimento();
                    break;
                case ATA_REGISTRO_PRECO:
                    menorData = ataRegistroPreco.getDataInicio();
                    break;
                case DISPENSA_LICITACAO_INEXIGIBILIDADE:
                    menorData = dispensaDeLicitacao.getDataDaDispensa();
                    break;
                default:
                    menorData = new Date();
            }
        }
        if (DataUtil.dataSemHorario(menorData).after(DataUtil.dataSemHorario(selecionado.getDataRequisicao()))) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("A data da requisição " + DataUtil.getDataFormatada(selecionado.getDataRequisicao()) + " não pode ser anterior a data do empenho " + DataUtil.getDataFormatada(menorData) + ".");
        }
    }

    public List<Long> getIdsEmpenhos() {
        List<Long> idsEmpenho = Lists.newArrayList();
        execucoesVO.forEach(exec -> {
            exec.getEmpenhosVO().stream().filter(RequisicaoCompraEmpenhoVO::getSelecionado).forEach(emp -> idsEmpenho.add(emp.getEmpenho().getId()));
        });
        return idsEmpenho;
    }

    public TipoMascaraUnidadeMedida getMascaraQuantidadeItemDesdobrado() {
        if (selecionado.getTipoObjetoCompra() != null
            && selecionado.getTipoObjetoCompra().isMaterialConsumo()
            && itemRequisicao != null) {
            return itemRequisicao.getUnidadeMedida() != null
                ? itemRequisicao.getUnidadeMedida().getMascaraQuantidade()
                : TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
        }
        return TipoMascaraUnidadeMedida.ZERO_CASA_DECIMAL;
    }

    public TipoMascaraUnidadeMedida getMascaraValorUnitarioItemDesdobrado() {
        if (selecionado.getTipoObjetoCompra() != null
            && selecionado.getTipoObjetoCompra().isMaterialConsumo()
            && itemRequisicao != null) {
            return itemRequisicao.getUnidadeMedida() != null
                ? itemRequisicao.getUnidadeMedida().getMascaraValorUnitario()
                : TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
        }
        return TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
    }

    public void listenerCalcularValoresItemRequisicaoExecucao(ItemRequisicaoCompraExecucaoVO itemReqProcVO, int linhaItemReq, int linhaItemEx) {
        linhaItemEx = linhaItemEx + 1;
        ItemRequisicaoCompraVO itemReqVO = itemReqProcVO.getItemRequisicaoCompraVO();
        try {
            validarQuantidadeItemRequisicaoExecucao(itemReqProcVO);
            atribuirQuantidadeItemAjusteValor(itemReqVO, itemReqProcVO.getQuantidade());
            itemReqProcVO.calcularValorTotal();

            itemReqVO.setQuantidade(itemReqVO.getQuantidadeTotalItemExecucao());
            itemReqVO.calcularValoresItemRequisicaoAndItemExecucao();
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabelaItensReq:" + linhaItemReq + ":tabelaItemReqExec:" + linhaItemEx + ":qtdeItemExec')");
        } catch (ValidacaoException ve) {
            itemReqProcVO.setQuantidade(BigDecimal.ZERO);
            atribuirQuantidadeItemAjusteValor(itemReqVO, BigDecimal.ZERO);
            itemReqVO.setQuantidade(itemReqVO.getQuantidadeTotalItemExecucao());
            itemReqVO.calcularValoresItemRequisicaoAndItemExecucao();
            FacesUtil.atualizarComponente("Formulario:panelItens");
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private static void atribuirQuantidadeItemAjusteValor(ItemRequisicaoCompraVO itemReqVO, BigDecimal quantidade) {
        for (ItemRequisicaoCompraExecucaoVO itemReqExecVO : itemReqVO.getItensRequisicaoExecucao()) {
            if (itemReqExecVO.getAjusteValor()) {
                itemReqExecVO.setQuantidade(itemReqVO.getQuantidadeTotalItemExecucao());
                itemReqExecVO.calcularValorTotal();
            }
        }
    }

    public void validarQuantidadeItemRequisicaoExecucao(ItemRequisicaoCompraExecucaoVO itemReqExec) {
        ValidacaoException ve = new ValidacaoException();
        if (itemReqExec.hasQuantidade()) {
            if (itemReqExec.getQuantidade().compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo quantidade deve ser um valor maior que zero(0).");
            }
            ve.lancarException();
            ItemRequisicaoCompraVO itemReqVO = itemReqExec.getItemRequisicaoCompraVO();
            BigDecimal qtdeDisponivel = itemReqExec.getQuantidadeDisponivel();
            if (itemReqVO.isDerivacaoObjetoCompra()) {
                ItemRequisicaoCompraDerivacao itemReqDerivado = getItemRequisicaoCompraDerivado(itemReqVO);
                qtdeDisponivel = itemReqDerivado.getQuantidadeDisponivel().subtract(itemReqDerivado.getQuantidadeUtilizadaOutrosItens(itemReqVO));
            }

            if (itemReqExec.getItemRequisicaoCompraVO().isControleQuantidade()
                && itemReqExec.getQuantidade().compareTo(qtdeDisponivel) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade informada para o item: "
                    + itemReqExec.getItemRequisicaoCompraVO().getDescricao() + " deve ser menor que a quantidade disponível de " + qtdeDisponivel + ".");
            }
        }
        ve.lancarException();
    }

    public boolean isItemExecucaoContratoAjusteValor(ExecucaoContratoItem itemExecCont) {
        if (itemExecCont.getExecucaoContrato().getOrigem().isAditivo()) {
            return facade.getAlteracaoContratualFacade().isMovimentoAjusteValor(itemExecCont.getItemContrato().getId(), itemExecCont.getExecucaoContrato().getIdOrigem());
        }
        if (itemExecCont.getExecucaoContrato().getOrigem().isApostilamento()) {
            return facade.getAlteracaoContratualFacade().isMovimentoApostilamentoReajusteValorPreExecucao(itemExecCont.getItemContrato().getId(), itemExecCont.getExecucaoContrato().getIdOrigem());
        }
        return false;
    }

    public boolean isItemExecucaoProcessoAjusteValor(ExecucaoProcessoItem itemExecProc) {
        if (itemExecProc.getExecucaoProcesso().getOrigem().isAditivo()) {
            return facade.getAlteracaoContratualFacade().isMovimentoAjusteValor(itemExecProc.getItemProcessoCompra().getId(), itemExecProc.getExecucaoProcesso().getIdOrigem());
        }
        if (itemExecProc.getExecucaoProcesso().getOrigem().isApostilamento()) {
            return facade.getAlteracaoContratualFacade().isMovimentoApostilamentoReajusteValorPreExecucao(itemExecProc.getItemProcessoCompra().getId(), itemExecProc.getExecucaoProcesso().getIdOrigem());
        }
        return false;
    }

    public boolean hasItensRequisicao() {
        return !Util.isListNullOrEmpty(itensRequisicaoVO);
    }

    public boolean hasItensProcesso() {
        return itensProcesso != null && !itensProcesso.isEmpty();
    }

    public boolean hasMaisDeUmTipoContaDespesa() {
        return !Util.isListNullOrEmpty(tiposContaDespesa) && tiposContaDespesa.size() > 1;
    }

    public boolean hasExecucaoSelecionada() {
        return hasExecucoes() && execucoesVO.stream().anyMatch(RequisicaoCompraExecucaoVO::getSelecionado);
    }

    private boolean hasItemRequisicaoSelecionado() {
        return itensRequisicaoVO.stream().anyMatch(ItemRequisicaoCompraVO::hasQuantidade);
    }

    public boolean desabilitaAutocompleProcesso() {
        if (selecionado.getTipoRequisicao() == null || selecionado.getTipoObjetoCompra() == null) {
            return true;
        }
        return hasExecucaoSelecionada() || isOperacaoEditar();
    }

    public List<RequisicaoDeCompra> completarRequisicaoDeCompra(String parte) {
        return facade.recuperarFiltrando(parte.trim());
    }

    public void limparListaItensRequisicao() {
        itensRequisicaoVO.clear();
        itensProcesso = Lists.newArrayList();
        itensDerivacao = Lists.newArrayList();
    }

    public void separarItensPorTipoContaDespesaEmpenho() {
        if (selecionado.getTipoObjetoCompra().isPermanenteOrConsumo() && hasMaisDeUmTipoContaDespesa()) {
            selecionado.setItens(Lists.newArrayList());
            List<ItemRequisicaoProcessoLicitatorio> itensSeparados = Lists.newArrayList();
            itensProcesso.stream()
                .filter(item -> item.getTipoContaDespesa().equals(selecionado.getTipoContaDespesa()))
                .forEach(itensSeparados::add);

            criarItemRequisicaoCompraVOAndItemRequisicaoExecucaoVO(itensSeparados);
            Collections.sort(itensRequisicaoVO);
        }
    }

    public void buscarItens() {
        try {
            itensRequisicaoVO = Lists.newArrayList();
            validarCamposObrigatorio();
            buscarTiposContaDespesaEmpenho();
            buscarItensProcesso();
            criarItemRequisicaoCompraVOAndItemRequisicaoExecucaoVO(itensProcesso);
            FacesUtil.executaJavaScript("aguarde.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void buscarTiposContaDespesaEmpenho() {
        Set<TipoContaDespesa> tiposContaDespesaSet = Sets.newHashSet();
        selecionado.setTipoContaDespesa(null);
        execucoesVO.stream()
            .filter(RequisicaoCompraExecucaoVO::getSelecionado)
            .forEach(exReq -> tiposContaDespesaSet.addAll(facade.getEmpenhoFacade().buscarTiposContaDespesaExecucaoEmpenho(exReq.getId())));

        List<TipoContaDespesa> tiposPermititdos = TipoContaDespesa.getTiposContaDespesaPorTipoObjetoCompra(selecionado.getTipoObjetoCompra());
        tiposContaDespesa = Lists.newArrayList(tiposContaDespesaSet);
        tiposContaDespesa.removeIf(tipo -> !tiposPermititdos.contains(tipo));

        if (!Util.isListNullOrEmpty(tiposContaDespesa)) {
            selecionado.setTipoContaDespesa(tiposContaDespesa.get(0));
        }
    }

    public void buscarItensProcesso() {
        itensProcesso = Lists.newArrayList();
        if (hasExecucoes()) {
            execucoesVO
                .stream()
                .filter(RequisicaoCompraExecucaoVO::getSelecionado)
                .forEach(reqExVO -> itensProcesso.addAll(facade.buscarItensProcessoLicitatorio(reqExVO.getId())));
        }
    }

    public void preencherItemDesdobradoVisualizacao() {
        if (permiteDesdobrarItens()) {
            execucoesVO = Lists.newArrayList();
            itensRequisicaoDesdobradoVO = Lists.newArrayList();
            itensRequisicaoVO = Lists.newArrayList();

            for (RequisicaoCompraExecucao reqEx : selecionado.getExecucoes()) {

                RequisicaoCompraExecucaoVO reqExecVO = new RequisicaoCompraExecucaoVO();
                reqExecVO.setId(reqEx.getIdExecucao());
                reqExecVO.setSelecionado(true);
                reqExecVO.setTipoProcesso(selecionado.getTipoRequisicao());

                facade.buscarEmpenhoExecucao(reqExecVO, selecionado.getTipoObjetoCompra());
                reqExecVO.getEmpenhosVO().forEach(empVO -> empVO.setSelecionado(true));
                if (reqEx.getExecucaoContratoEmpenho() != null) {
                    reqExecVO.getEmpenhosVO().removeIf(empVO -> !empVO.getEmpenho().equals(reqEx.getExecucaoContratoEmpenho().getEmpenho()));
                }
                if (reqEx.getExecucaoProcessoEmpenho() != null) {
                    reqExecVO.getEmpenhosVO().removeIf(empVO -> !empVO.getEmpenho().equals(reqEx.getExecucaoProcessoEmpenho().getEmpenho()));
                }
                if (reqEx.getExecucaoReconhecimentoDiv() != null) {
                    reqExecVO.getEmpenhosVO().removeIf(empVO -> !empVO.getEmpenho().equals(reqEx.getExecucaoReconhecimentoDiv().getSolicitacaoEmpenho().getEmpenho()));
                }
                execucoesVO.add(reqExecVO);
            }
            buscarItensProcesso();
            criarItemRequisicaoCompraVOAndItemRequisicaoExecucaoVO(itensProcesso);
            preencherItemDesdobradoEdicao();
            itensRequisicaoDesdobradoVO = itensRequisicaoVO;
            Collections.sort(itensRequisicaoDesdobradoVO);
        }
    }

    public void preencherItemDesdobradoEdicao() {
        if (permiteDesdobrarItens()) {
            itensRequisicaoVO.forEach(itemReqVO -> {
                List<ItemRequisicaoDeCompra> itensDesdobrados = facade.buscarItensDesdobrado(itemReqVO.getItemContrato(), selecionado, itemReqVO.getNumeroItemProcesso());

                itensDesdobrados.forEach(itemReqDesd -> {
                    ItemRequisicaoCompraVO novoItemReqVO = ItemRequisicaoCompraVO.toObjeto(itemReqDesd);
                    novoItemReqVO.setRequisicaoDeCompra(selecionado);
                    facade.recuperarGrupoContaDespesa(novoItemReqVO.getObjetoCompra());

                    itemReqDesd.getItensRequisicaoExecucao().forEach(itemReqExecDesd -> {
                        ItemRequisicaoCompraExecucaoVO novoItemReqProcVO = ItemRequisicaoCompraExecucaoVO.toObjeto(itemReqExecDesd, novoItemReqVO);
                        RequisicaoCompraEmpenhoVO novoReqEmpVO = facade.novoEmpenhoVO(itemReqExecDesd.getEmpenho(), selecionado.getTipoObjetoCompra());
                        novoItemReqProcVO.setRequisicaoEmpenhoVO(novoReqEmpVO);

                        if (selecionado.isTipoContrato()) {
                            ExecucaoContratoItemDotacao itemDotExec = facade.getExecucaoContratoFacade().buscarItemDotacaoPorItemExecucao(
                                itemReqExecDesd.getExecucaoContratoItem(),
                                itemReqExecDesd.getEmpenho().getFonteDespesaORC());
                            if (itemDotExec != null) {
                                novoItemReqProcVO.setQuantidadeDisponivel(getQuantidadeDisponivelProcessoContrato(itemReqExecDesd.getExecucaoContratoItem(), itemDotExec, itemReqExecDesd));
                            }
                        }
                        if (selecionado.isTipoExecucaoProcesso()) {
                            ExecucaoProcessoFonteItem itemDotExec = facade.getExecucaoProcessoFacade().buscarItemFonte(
                                itemReqExecDesd.getExecucaoProcessoItem().getExecucaoProcesso(),
                                itemReqExecDesd.getEmpenho().getFonteDespesaORC(),
                                itemReqExecDesd.getExecucaoProcessoItem());
                            if (itemDotExec != null) {
                                novoItemReqProcVO.setQuantidadeDisponivel(getQuantidadeDisponivelProcessoSemContrato(itemReqExecDesd.getExecucaoProcessoItem(), itemDotExec, itemReqExecDesd));
                            }
                        }
                        novoItemReqVO.getItensRequisicaoExecucao().add(novoItemReqProcVO);
                    });
                    itemReqVO.getItensDesdobrados().add(novoItemReqVO);
                    itemReqVO.ordenarItensDesdobradosPorNumero();
                });
            });
        }
        itensRequisicaoDesdobradoVO = itensRequisicaoVO;
    }

    public void criarItemRequisicaoCompraVOAndItemRequisicaoExecucaoVO(List<ItemRequisicaoProcessoLicitatorio> itensProcesso) {
        if (selecionado.isTipoContrato()) {
            Map<ItemContrato, List<ExecucaoContratoItem>> map = criarMapItemContratoItemExecucao(itensProcesso);

            for (Map.Entry<ItemContrato, List<ExecucaoContratoItem>> entry : map.entrySet()) {
                ItemLicitatorioContrato itemAdequado = entry.getKey().getItemAdequado();

                ItemRequisicaoCompraVO novoItemReqVO = criarItemRequisicaoCompraVO(
                    itemAdequado.getItemProcessoCompra(), itemAdequado.getObjetoCompra(), itemAdequado.getNumeroItem(),
                    itemAdequado.getUnidadeMedida(), itemAdequado.getDescricaoComplementar(), entry.getKey().getTipoControle());

                for (ExecucaoContratoItem itemExecucao : entry.getValue()) {
                    List<ExecucaoContratoItemDotacao> itensDotExec = facade.getExecucaoContratoFacade().buscarItensDotacaoPorItemExecucao(itemExecucao);
                    for (ExecucaoContratoItemDotacao itemDot : itensDotExec) {
                        ItemRequisicaoCompraExecucaoVO novoItemReqExecVO = new ItemRequisicaoCompraExecucaoVO();
                        novoItemReqExecVO.setExecucaoContratoItem(itemExecucao);
                        novoItemReqExecVO.setExecucaoContratoItemDotacao(itemDot);
                        novoItemReqExecVO.setItemRequisicaoCompraVO(novoItemReqVO);
                        novoItemReqExecVO.setValorUnitario(itemExecucao.getValorUnitario());
                        novoItemReqExecVO.setAjusteValor(isItemExecucaoContratoAjusteValor(itemExecucao));

                        novoItemReqExecVO.setRequisicaoEmpenhoVO(getRequisicaoCompraEmpenhoVO(
                            novoItemReqVO.getObjetoCompra().getGrupoContaDespesa().getIdGrupo(),
                            itemDot.getExecucaoContratoTipoFonte().getFonteDespesaORC().getId(),
                            itemExecucao.getExecucaoContrato().getId()));

                        if (novoItemReqVO.isControleValor() || isLicitacaoMaiorDesconto()) {
                            atribuirValorDisponivelItemExecucaoDesdobrado(novoItemReqVO, novoItemReqExecVO);
                            novoItemReqExecVO.setValorTotal(novoItemReqExecVO.getValorTotalItemExecucao());

                        } else {
                            ItemRequisicaoCompraExecucao itemExecReqSalvo = facade.getItemRequisicaoExecucaoPorItemExecucao(itemExecucao.getId(), itemDot.getExecucaoContratoTipoFonte().getFonteDespesaORC().getId(), selecionado);
                            if (itemExecReqSalvo != null) {
                                novoItemReqExecVO.setQuantidade(itemExecReqSalvo.getQuantidade());
                            }
                            novoItemReqExecVO.setQuantidadeDisponivel(getQuantidadeDisponivelProcessoContrato(itemExecucao, itemDot, itemExecReqSalvo));
                            novoItemReqExecVO.calcularValorTotal();
                        }
                        if (novoItemReqExecVO.getRequisicaoEmpenhoVO() != null) {
                            Util.adicionarObjetoEmLista(novoItemReqVO.getItensRequisicaoExecucao(), novoItemReqExecVO);
                        }
                    }
                }
                novoItemReqVO.setValorUnitario(novoItemReqVO.getValorUnitarioTotalItemExecucao());
                novoItemReqVO.setQuantidade(isOperacaoEditar() ? novoItemReqVO.getQuantidadeTotalItemExecucao() : BigDecimal.ZERO);
                novoItemReqVO.setQuantidadeDisponivel(novoItemReqVO.getQuantidadeDisponivelItemExecucao());
                novoItemReqVO.calcularValorTotal();

                if (!Util.isListNullOrEmpty(novoItemReqVO.getItensRequisicaoExecucao())) {
                    Util.adicionarObjetoEmLista(itensRequisicaoVO, novoItemReqVO);
                }
            }
        } else if (selecionado.isTipoExecucaoProcesso()) {
            Map<ItemProcessoDeCompra, List<ExecucaoProcessoItem>> map = criarMapItemProcessoCompraItemExecucao(itensProcesso);

            for (Map.Entry<ItemProcessoDeCompra, List<ExecucaoProcessoItem>> entry : map.entrySet()) {
                ItemProcessoDeCompra itemProcComp = entry.getKey();
                ItemRequisicaoCompraVO novoItemReqVO = criarItemRequisicaoCompraVO(
                    itemProcComp, itemProcComp.getObjetoCompra(), itemProcComp.getNumero(), itemProcComp.getUnidadeMedida(),
                    itemProcComp.getDescricaoComplementar(), itemProcComp.getTipoControle());

                for (ExecucaoProcessoItem itemExec : entry.getValue()) {
                    List<ExecucaoProcessoFonteItem> itensDotExec = facade.getExecucaoProcessoFacade().buscarItensDotacaoPorItemExecucao(itemExec);
                    for (ExecucaoProcessoFonteItem itemDot : itensDotExec) {
                        ItemRequisicaoCompraExecucaoVO novoItemReqExecVO = new ItemRequisicaoCompraExecucaoVO();
                        novoItemReqExecVO.setExecucaoProcessoItem(itemExec);
                        novoItemReqExecVO.setExecucaoProcessoFonteItem(itemDot);
                        novoItemReqExecVO.setItemRequisicaoCompraVO(novoItemReqVO);
                        novoItemReqExecVO.setValorUnitario(itemExec.getValorUnitario());
                        novoItemReqExecVO.setAjusteValor(isItemExecucaoProcessoAjusteValor(itemExec));

                        novoItemReqExecVO.setRequisicaoEmpenhoVO(getRequisicaoCompraEmpenhoVO(
                            novoItemReqVO.getObjetoCompra().getGrupoContaDespesa().getIdGrupo(),
                            itemDot.getExecucaoProcessoFonte().getFonteDespesaORC().getId(),
                            itemExec.getExecucaoProcesso().getId()));

                        if (novoItemReqVO.isControleValor() || isLicitacaoMaiorDesconto()) {
                            atribuirValorDisponivelItemExecucaoDesdobrado(novoItemReqVO, novoItemReqExecVO);
                            novoItemReqExecVO.setValorTotal(novoItemReqExecVO.getValorTotalItemExecucao());

                        } else {
                            ItemRequisicaoCompraExecucao itemExecReqSalvo = facade.getItemRequisicaoExecucaoPorItemExecucao(itemExec.getId(), itemDot.getExecucaoProcessoFonte().getFonteDespesaORC().getId(), selecionado);
                            if (itemExecReqSalvo != null) {
                                novoItemReqExecVO.setQuantidade(itemExecReqSalvo.getQuantidade());
                            }
                            novoItemReqExecVO.setQuantidadeDisponivel(getQuantidadeDisponivelProcessoSemContrato(itemExec, itemDot, itemExecReqSalvo));
                            novoItemReqExecVO.calcularValorTotal();
                        }
                        if (novoItemReqExecVO.getRequisicaoEmpenhoVO() != null) {
                            Util.adicionarObjetoEmLista(novoItemReqVO.getItensRequisicaoExecucao(), novoItemReqExecVO);
                        }
                    }
                }
                novoItemReqVO.setValorUnitario(novoItemReqVO.getValorUnitarioTotalItemExecucao());
                novoItemReqVO.setQuantidade(isOperacaoEditar() ? novoItemReqVO.getQuantidadeTotalItemExecucao() : BigDecimal.ZERO);
                novoItemReqVO.setQuantidadeDisponivel(novoItemReqVO.getQuantidadeDisponivelItemExecucao());
                novoItemReqVO.calcularValorTotal();

                if (!Util.isListNullOrEmpty(novoItemReqVO.getItensRequisicaoExecucao())) {
                    Util.adicionarObjetoEmLista(itensRequisicaoVO, novoItemReqVO);
                }
            }
        } else {
            for (ItemRequisicaoProcessoLicitatorio itemProcVO : itensProcesso) {
                ItemReconhecimentoDivida itemRecDiv = itemProcVO.getItemReconhecimentoDivida();
                if (itemRecDiv.getObjetoCompra().getTipoObjetoCompra().equals(selecionado.getTipoObjetoCompra())) {

                    ItemRequisicaoCompraVO novoItemReqVO = criarItemRequisicaoCompraVO(
                        null, itemRecDiv.getObjetoCompra(), null, itemRecDiv.getUnidadeMedida(),
                        itemRecDiv.getDescricaoItem(), TipoControleLicitacao.QUANTIDADE);

                    List<ItemReconhecimentoDividaDotacao> itensDotExec = facade.getReconhecimentoDividaFacade().buscarItensDotacaoPorItemReconhecimento(itemRecDiv);
                    for (ItemReconhecimentoDividaDotacao itemDot : itensDotExec) {
                        ItemRequisicaoCompraExecucaoVO novoItemReqExecVO = new ItemRequisicaoCompraExecucaoVO();
                        novoItemReqExecVO.setItemReconhecimentoDivida(itemRecDiv);
                        novoItemReqExecVO.setItemReconhecimentoDividaDotacao(itemDot);
                        novoItemReqExecVO.setItemRequisicaoCompraVO(novoItemReqVO);
                        novoItemReqExecVO.setValorUnitario(itemRecDiv.getValorUnitario());

                        novoItemReqExecVO.setRequisicaoEmpenhoVO(getRequisicaoCompraEmpenhoVO(
                            novoItemReqVO.getObjetoCompra().getGrupoContaDespesa().getIdGrupo(),
                            itemDot.getFonteDespesaORC().getId(),
                            itemRecDiv.getReconhecimentoDivida().getId()));

                        ItemRequisicaoCompraExecucao itemExecReqSalvo = facade.getItemRequisicaoExecucaoPorItemExecucao(itemRecDiv.getId(), itemDot.getFonteDespesaORC().getId(), selecionado);
                        if (itemExecReqSalvo != null) {
                            novoItemReqExecVO.setQuantidade(itemExecReqSalvo.getQuantidade());
                        }
                        novoItemReqExecVO.setQuantidadeDisponivel(getQuantidadeDisponivelProcessoReconhecimentoDivida(itemRecDiv, itemDot, itemExecReqSalvo));
                        novoItemReqExecVO.calcularValorTotal();

                        if (novoItemReqExecVO.getRequisicaoEmpenhoVO() != null) {
                            Util.adicionarObjetoEmLista(novoItemReqVO.getItensRequisicaoExecucao(), novoItemReqExecVO);
                        }
                    }
                    novoItemReqVO.setValorUnitario(itemRecDiv.getValorUnitario());
                    novoItemReqVO.setQuantidade(isOperacaoEditar() ? novoItemReqVO.getQuantidadeTotalItemExecucao() : BigDecimal.ZERO);
                    novoItemReqVO.setQuantidadeDisponivel(novoItemReqVO.getQuantidadeDisponivelItemExecucao());
                    novoItemReqVO.calcularValorTotal();

                    if (!Util.isListNullOrEmpty(novoItemReqVO.getItensRequisicaoExecucao())) {
                        Util.adicionarObjetoEmLista(itensRequisicaoVO, novoItemReqVO);
                    }
                }
            }
        }
        Collections.sort(itensRequisicaoVO);
    }

    private RequisicaoCompraEmpenhoVO getRequisicaoCompraEmpenhoVO(Long idGrupo, Long idFonteDespesaOrc, Long idExecucao) {
        if (selecionado.getTipoObjetoCompra().isPermanenteOrConsumo()) {
            return execucoesVO
                .stream().filter(RequisicaoCompraExecucaoVO::getSelecionado)
                .flatMap(empVO -> empVO.getEmpenhosVO().stream())
                .filter(RequisicaoCompraEmpenhoVO::getSelecionado)
                .filter(empVO -> empVO.getIdExecucao().equals(idExecucao))
                .filter(empVO -> empVO.getEmpenho().getFonteDespesaORC().getId().equals(idFonteDespesaOrc))
                .filter(empVO -> empVO.getIdGrupo().equals(idGrupo))
                .findAny().orElse(null);
        }
        return execucoesVO
            .stream().filter(RequisicaoCompraExecucaoVO::getSelecionado)
            .flatMap(empVO -> empVO.getEmpenhosVO().stream())
            .filter(empVO -> empVO.getIdExecucao().equals(idExecucao))
            .filter(RequisicaoCompraEmpenhoVO::getSelecionado)
            .findAny().orElse(null);
    }

    public Map<ItemContrato, List<ExecucaoContratoItem>> criarMapItemContratoItemExecucao(List<ItemRequisicaoProcessoLicitatorio> itensProcesso) {
        Map<ItemContrato, List<ExecucaoContratoItem>> map = new HashMap<>();
        itensProcesso.forEach(item -> {
            ExecucaoContratoItem itemExecucao = item.getItemExecucaoContrato();
            ItemContrato itemCont = itemExecucao.getItemContrato();
            if (itemCont.getItemAdequado().getObjetoCompra().getTipoObjetoCompra().equals(selecionado.getTipoObjetoCompra())) {
                if (!map.containsKey(itemCont)) {
                    itemCont.getItemAdequado().getObjetoCompra().setGrupoContaDespesa(item.getObjetoCompra().getGrupoContaDespesa());
                    map.put(itemCont, Lists.newArrayList(itemExecucao));
                } else {
                    List<ExecucaoContratoItem> itensMap = map.get(itemCont);
                    itensMap.add(itemExecucao);
                    map.put(itemCont, itensMap);
                }
            }
        });
        return map;
    }

    public Map<ItemProcessoDeCompra, List<ExecucaoProcessoItem>> criarMapItemProcessoCompraItemExecucao(List<ItemRequisicaoProcessoLicitatorio> itensProcesso) {
        Map<ItemProcessoDeCompra, List<ExecucaoProcessoItem>> map = new HashMap<>();
        itensProcesso.forEach(item -> {
            ExecucaoProcessoItem itemExec = item.getItemExecucaoProcesso();
            ItemProcessoDeCompra itemProc = itemExec.getItemProcessoCompra();
            if (itemProc.getObjetoCompra().getTipoObjetoCompra().equals(selecionado.getTipoObjetoCompra())) {
                if (!map.containsKey(itemProc)) {
                    itemProc.getObjetoCompra().setGrupoContaDespesa(item.getObjetoCompra().getGrupoContaDespesa());
                    map.put(itemProc, Lists.newArrayList(itemExec));
                } else {
                    List<ExecucaoProcessoItem> itensMap = map.get(itemProc);
                    itensMap.add(itemExec);
                    map.put(itemProc, itensMap);
                }
            }
        });
        return map;
    }

    public boolean permiteDesdobrarItens() {
        return isLicitacaoMaiorDesconto() || isControlePorValor();
    }

    public void ajustarValorTotal(DescontoItemRequisicaoCompra item) {
        if (item.getDescontoTotal().compareTo(BigDecimal.ZERO) > 0) {
            itemRequisicao.setValorDescontoTotal(item.getDescontoTotal().setScale(2, RoundingMode.HALF_EVEN));

            BigDecimal descontoUnitarioCalculado = item.getDescontoTotal().divide(itemRequisicao.getQuantidade(), itemRequisicao.getScaleUnitario(), RoundingMode.FLOOR);
            item.setDescontoUnitario(descontoUnitarioCalculado);
            itemRequisicao.setValorDescontoUnitario(item.getDescontoUnitario());
            itemRequisicao.calcularValorTotal();

            BigDecimal valorUnitarioCalculado = itemRequisicao.getValorUnitarioDesdobrado().subtract(descontoUnitarioCalculado);
            itemRequisicao.setValorUnitario(removerCasasDecimaisComZero(valorUnitarioCalculado));
            itemRequisicao.calcularValorTotalItemEntradaPorCompra();
            if (itemRequisicao.hasDiferencaRequisicaoComEntrada()) {
                itemRequisicao.setArredondaValorTotal(true);
            }
            itemRequisicao.calcularValorTotalItemEntradaPorCompra();
        }
    }

    public void calcularValoresItemDesdobrado() {
        this.itemRequisicao.atribuirValorUnitario();
        this.itemRequisicao.setValorUnitario(removerCasasDecimaisComZero(itemRequisicao.getValorUnitario()));
        this.itemRequisicao.calcularValorTotal();
        this.itemRequisicao.calcularValorTotalItemEntradaPorCompra();
        updateItemRequisicaoDesdobravel();
    }

    private void updateItemRequisicaoDesdobravel() {
        FacesUtil.atualizarComponente("formDesdobrarItem:valorTotalDesdobrado");
        FacesUtil.atualizarComponente("formDesdobrarItem:gridAplicacaoDesconto");
        FacesUtil.atualizarComponente("formDesdobrarItem:valorDesconto");
        FacesUtil.atualizarComponente("formDesdobrarItem:valorDescontoTotal");
        FacesUtil.atualizarComponente("formDesdobrarItem:panelEntradaCompra");
        FacesUtil.atualizarComponente("formDesdobrarItem:panelDescontoNota");
        FacesUtil.atualizarComponente("formDesdobrarItem:msgDiferencaoEntrada");
    }

    public void selecionarDesconto(DescontoItemRequisicaoCompra item) {
        if (item.getSelecionado()) {
            desselecionarDesconto(item);
            itemRequisicao.setTipoDesconto(item.getTipoDesconto());
            ajustarValorTotal(item);
            updateItemRequisicaoDesdobravel();
        }
    }

    private void desselecionarDesconto(DescontoItemRequisicaoCompra item) {
        for (DescontoItemRequisicaoCompra desc : descontosItemRequisicao) {
            if (!item.getTipoDesconto().equals(desc.getTipoDesconto())) {
                desc.setSelecionado(false);
            }
        }
    }

    public void listenerValorUnitarioItemDesdobrado() {
        gerarTabelaValorDescontoItem();
        itemRequisicao.setTipoDesconto(selecionado.getTipoObjetoCompra().isMaterialPermanente()
            ? TipoDescontoItemRequisicao.ARREDONDAR
            : TipoDescontoItemRequisicao.NAO_ARREDONDAR);

        for (DescontoItemRequisicaoCompra itemDesc : descontosItemRequisicao) {
            if (itemDesc.getTipoDesconto().equals(itemRequisicao.getTipoDesconto())) {
                itemDesc.setSelecionado(true);
                selecionarDesconto(itemDesc);
            }
        }
        calcularValoresItemDesdobrado();
    }

    public void gerarTabelaValorDescontoItem() {
        descontosItemRequisicao = Lists.newArrayList();
        novaTabelaDescontoItem(TipoDescontoItemRequisicao.NAO_ARREDONDAR);
        novaTabelaDescontoItem(TipoDescontoItemRequisicao.ARREDONDAR);
        novaTabelaDescontoItem(TipoDescontoItemRequisicao.TRUNCAR);
        calcularValoresItemDesdobrado();
    }

    private void novaTabelaDescontoItem(TipoDescontoItemRequisicao tipoDesconto) {
        itemRequisicao.setTipoDesconto(tipoDesconto);
        DescontoItemRequisicaoCompra novoDesc = new DescontoItemRequisicaoCompra(
            tipoDesconto,
            itemRequisicao.getValorDescontoUnitarioPorTipoDesconto(tipoDesconto),
            itemRequisicao.getValorDescontoTotalPorTipoDesconto(tipoDesconto));
        if (!tipoDesconto.isNaoArredondar()) {
            novoDesc.setDescontoUnitario(removerCasasDecimaisComZero(novoDesc.getDescontoUnitario()));
        }
        novoDesc.setDescontoTotal(removerCasasDecimaisComZero(novoDesc.getDescontoTotal()));
        descontosItemRequisicao.add(novoDesc);
    }

    private BigDecimal removerCasasDecimaisComZero(BigDecimal valor) {
        if (valor.toPlainString().length() == 2 && valor.toPlainString().endsWith("0")) {
            return valor;
        }
        return valor.stripTrailingZeros();
    }

    public ItemRequisicaoCompraVO criarItemRequisicaoCompraVO(ItemProcessoDeCompra itemProcComp, ObjetoCompra objetoCompra, Integer numeroItem,
                                                              UnidadeMedida unidMed, String descricaoComp, TipoControleLicitacao tipoControle) {
        ItemRequisicaoCompraVO novoItemReq = new ItemRequisicaoCompraVO();
        novoItemReq.setRequisicaoDeCompra(selecionado);
        novoItemReq.setNumero(getProximoNumeroItemRequisicao());
        novoItemReq.setNumeroItemProcesso(numeroItem != null ? numeroItem : novoItemReq.getNumero());
        novoItemReq.setObjetoCompra(objetoCompra);
        novoItemReq.setUnidadeMedida(unidMed);
        novoItemReq.setDescricaoComplementar(descricaoComp);
        novoItemReq.setTipoControle(tipoControle);
        if (itemProcComp != null) {
            novoItemReq.setPercentualDesconto(facade.getItemPregaoFacade().getPercentualDescontoLanceVencedor(itemProcComp));
        }
        return novoItemReq;
    }

    public void desdobrarItem(ItemRequisicaoCompraExecucaoVO itemReqProcVO, ItemRequisicaoCompraVO itemReqVO) {
        itemRequisicaoDesdobravel = (ItemRequisicaoCompraVO) Util.clonarEmNiveis(itemReqVO, 1);
        itemRequisicaoExecucao = itemReqProcVO;
        novoItemRequisicaoCompraDesdobrado();
        FacesUtil.executaJavaScript("dlgDesdobrarItem.show()");
    }

    public void novoItemRequisicaoCompraDesdobrado() {
        if (selecionado.isTipoContrato()) {
            ItemLicitatorioContrato itemAdequado = itemRequisicaoDesdobravel.getItemContrato().getItemAdequado();
            itemRequisicao = criarItemRequisicaoCompraVO(
                itemAdequado.getItemProcessoCompra(), itemAdequado.getObjetoCompra(), itemAdequado.getNumeroItem(),
                itemAdequado.getUnidadeMedida(), itemAdequado.getDescricaoComplementar(), itemRequisicaoDesdobravel.getTipoControle());

        } else if (selecionado.isTipoExecucaoProcesso()) {
            ItemProcessoDeCompra itemProcComp = itemRequisicaoDesdobravel.getExecucaoProcessoItem().getItemProcessoCompra();
            itemRequisicao = criarItemRequisicaoCompraVO(
                itemProcComp, itemProcComp.getObjetoCompra(), itemProcComp.getNumero(), itemProcComp.getUnidadeMedida(),
                itemProcComp.getDescricaoComplementar(), itemProcComp.getTipoControle());
        }

        ItemRequisicaoCompraExecucaoVO novoItemReqProc = (ItemRequisicaoCompraExecucaoVO) Util.clonarObjeto(itemRequisicaoExecucao);
        novoItemReqProc.setItemRequisicaoCompraVO(itemRequisicao);
        itemRequisicao.getItensRequisicaoExecucao().add(novoItemReqProc);

        itemRequisicao.setObjetoCompra(null);
        itemRequisicao.setNumero(null);
        itemRequisicao.setValorUnitario(itemRequisicaoDesdobravel.isControleQuantidade() ? itemRequisicaoDesdobravel.getValorUnitario() : BigDecimal.ZERO);
        itemRequisicao.setNumero(getProximoNumeroItemDesdobrado());
        itemRequisicao.setValorUnitarioDesdobrado(itemRequisicaoDesdobravel.isControleQuantidade() ? itemRequisicaoDesdobravel.getValorUnitario() : BigDecimal.ZERO);
        atribuirGrupoObjetoCompra();
    }

    private int getProximoNumeroItemDesdobrado() {
        int numero = itensRequisicaoVO.stream().mapToInt(item -> item.getItensDesdobrados().size()).sum();
        return numero + 1;
    }

    private int getProximoNumeroItemRequisicao() {
        return itensRequisicaoVO.size() + 1;
    }

    private void atribuirGrupoObjetoCompra() {
        try {
            itemRequisicaoDesdobravel.setObjetoCompra(facade.getObjetoCompraFacade().recuperar(itemRequisicaoDesdobravel.getObjetoCompra().getId()));
            ObjetoCompra objetoCompraPai = itemRequisicaoDesdobravel.getObjetoCompra();
            objetoCompraPai.setGrupoContaDespesa(facade.getObjetoCompraFacade().criarGrupoContaDespesa(selecionado.getTipoObjetoCompra(), objetoCompraPai.getGrupoObjetoCompra()));
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private BigDecimal getQuantidadeDisponivelProcessoContrato(ExecucaoContratoItem itemExecucao, ExecucaoContratoItemDotacao itemExecDot, ItemRequisicaoCompraExecucao itemReqEx) {
        if (itemExecucao.isExecucaoPorQuantidade() && !isLicitacaoMaiorDesconto()) {
            BigDecimal qtdeOutrasRequisicoes = facade.getQuantidadeUtilizadaItemExecucaoContrato(itemExecucao, itemExecDot.getExecucaoContratoTipoFonte().getFonteDespesaORC(), itemReqEx);
            BigDecimal qtdeEstornadaExec = facade.getExecucaoContratoEstornoFacade().getQuantidadeEstornadaItemPorFonte(itemExecucao, itemExecDot.getExecucaoContratoTipoFonte().getFonteDespesaORC());
            BigDecimal qtdeExecucao = itemExecDot.getQuantidade();
            return qtdeExecucao.subtract(qtdeEstornadaExec).subtract(qtdeOutrasRequisicoes);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal getQuantidadeDisponivelProcessoSemContrato(ExecucaoProcessoItem itemExecucao, ExecucaoProcessoFonteItem itemExecDot, ItemRequisicaoCompraExecucao itemReqEx) {
        if (itemExecucao.isExecucaoPorQuantidade() && !isLicitacaoMaiorDesconto()) {
            BigDecimal qtdeOutrasRequisicoes = facade.getQuantidadeUtilizadaItemExecucaoProcesso(itemExecucao, itemExecDot.getExecucaoProcessoFonte().getFonteDespesaORC(), itemReqEx);
            BigDecimal qtdeEstornadaExec = facade.getExecucaoProcessoEstornoFacade().getQuantidadeEstornadaItemPorFonte(itemExecucao, itemExecDot.getExecucaoProcessoFonte().getFonteDespesaORC());
            BigDecimal qtdeExecucao = itemExecDot.getQuantidade();
            return qtdeExecucao.subtract(qtdeEstornadaExec).subtract(qtdeOutrasRequisicoes);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal getQuantidadeDisponivelProcessoReconhecimentoDivida(ItemReconhecimentoDivida itemRecDiv, ItemReconhecimentoDividaDotacao itemDot, ItemRequisicaoCompraExecucao itemReqEx) {
        BigDecimal qtdeOutrasRequisicoes = facade.getQuantidadeUtilizadaItemReconhecimentoDivida(itemRecDiv, itemDot.getFonteDespesaORC(), itemReqEx);
        BigDecimal qtdeExecucao = itemDot.getValorReservado().divide(itemDot.getItemReconhecimentoDivida().getValorUnitario(), 4, RoundingMode.HALF_EVEN);
        return qtdeExecucao.subtract(qtdeOutrasRequisicoes);
    }


    private void atribuirValorDisponivelItemExecucaoDesdobrado(ItemRequisicaoCompraVO itemDesdobravel, ItemRequisicaoCompraExecucaoVO itemReqExecVO) {
        BigDecimal valorExecucao = BigDecimal.ZERO;
        BigDecimal valorEstornadoExec = BigDecimal.ZERO;
        BigDecimal valorOutrasRequisicoes;
        BigDecimal valorDestaRequisicao;
        BigDecimal valorUtilizado = BigDecimal.ZERO;
        BigDecimal valorDisponivel;

        if (selecionado.isTipoContrato()) {
            ExecucaoContratoItem itemExec = itemReqExecVO.getExecucaoContratoItem();

            valorExecucao = itemExec.getValorTotal();
            FonteDespesaORC fonteDespesaORC = null;
            if (itemReqExecVO.getExecucaoContratoItemDotacao() != null) {
                fonteDespesaORC = itemReqExecVO.getExecucaoContratoItemDotacao().getExecucaoContratoTipoFonte().getFonteDespesaORC();
                valorExecucao = itemReqExecVO.getExecucaoContratoItemDotacao().getValorTotal();
            }

            valorOutrasRequisicoes = fonteDespesaORC != null
                ? facade.getValorUtilizadoItemExecucaoContrato(itemExec, itemReqExecVO.getExecucaoContratoItemDotacao().getExecucaoContratoTipoFonte().getFonteDespesaORC(), getIdsItemRequisicaoDesdobrados(itemDesdobravel))
                : facade.getValorUtilizadoItemExecucaoContrato(itemExec, null, getIdsItemRequisicaoDesdobrados(itemDesdobravel));

            valorDestaRequisicao = itemDesdobravel.getValorTotalItemDesdobrado();
            valorUtilizado = valorOutrasRequisicoes.add(valorDestaRequisicao);

            valorEstornadoExec = fonteDespesaORC != null
                ? facade.getExecucaoContratoEstornoFacade().getValorEstornadoItemPorFonte(itemExec, fonteDespesaORC)
                : facade.getExecucaoContratoEstornoFacade().getValorEstornadoItemPorFonte(itemExec, null);

        } else if (selecionado.isTipoExecucaoProcesso()) {
            ExecucaoProcessoItem itemExec = itemReqExecVO.getExecucaoProcessoItem();

            valorExecucao = itemExec.getValorTotal();
            FonteDespesaORC fonteDespesaORC = null;
            if (itemReqExecVO.getExecucaoProcessoFonteItem() != null) {
                fonteDespesaORC = itemReqExecVO.getExecucaoProcessoFonteItem().getExecucaoProcessoFonte().getFonteDespesaORC();
                valorExecucao = itemReqExecVO.getExecucaoProcessoFonteItem().getValorTotal();
            }

            valorOutrasRequisicoes = fonteDespesaORC != null
                ? facade.getValorUtilizadoItemExecucaoProcesso(itemExec, itemReqExecVO.getExecucaoProcessoFonteItem().getExecucaoProcessoFonte().getFonteDespesaORC(), getIdsItemRequisicaoDesdobrados(itemDesdobravel))
                : facade.getValorUtilizadoItemExecucaoProcesso(itemExec, null, getIdsItemRequisicaoDesdobrados(itemDesdobravel));

            valorDestaRequisicao = itemDesdobravel.getValorTotalItemDesdobrado();
            valorUtilizado = valorOutrasRequisicoes.add(valorDestaRequisicao);

            valorEstornadoExec = fonteDespesaORC != null
                ? facade.getExecucaoProcessoEstornoFacade().getValorEstornadoItemPorFonte(itemExec, fonteDespesaORC)
                : facade.getExecucaoProcessoEstornoFacade().getValorEstornadoItemPorFonte(itemExec, null);
        }
        valorDisponivel = valorExecucao.subtract(valorEstornadoExec).subtract(valorUtilizado);
        itemReqExecVO.setValorEstornado(valorEstornadoExec);
        itemReqExecVO.setValorUtilizado(valorUtilizado);
        itemReqExecVO.setValorDisponivel(valorDisponivel);
    }

    public BigDecimal getValorTotalRequisicao() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasItensRequisicao()) {
            for (ItemRequisicaoCompraVO item : itensRequisicaoVO) {
                if (item.hasItensDesdobrados()) {
                    item.setValorTotal(item.getValorTotalItemDesdobrado());
                }
                total = total.add(item.getValorTotal());
            }
        }
        return total;
    }

    public List<Long> getIdsItemRequisicaoDesdobrados(ItemRequisicaoCompraVO itemDesdobrado) {
        List<Long> ids = Lists.newArrayList();
        itemDesdobrado.getItensDesdobrados().forEach(item -> {
            if (item.getId() != null) {
                ids.add(item.getId());
            }
        });
        return ids;
    }

    public List<ObjetoCompra> completarObjetoCompra(String codigoOrDescricao) {
        if (selecionado.getTipoObjetoCompra().isMaterialConsumo()) {
            if (hasPermissaoDesdobrarSemGrupo) {
                return facade.getObjetoCompraFacade().buscarObjetoCompraNaoReferenciaPorTipoObjetoCompra(codigoOrDescricao.trim(), TipoObjetoCompra.CONSUMO);
            }
            if (itemRequisicaoDesdobravel.getObjetoCompra().getGrupoContaDespesa() != null && itemRequisicaoDesdobravel.getObjetoCompra().getGrupoContaDespesa().getIdGrupo() != null) {
                return facade.getObjetoCompraFacade().buscarObjetoCompraConsumoPorGrupoMaterial(
                    codigoOrDescricao.trim(),
                    itemRequisicaoDesdobravel.getObjetoCompra().getGrupoContaDespesa().getIdGrupo(),
                    selecionado.getDataRequisicao());
            }
        }
        if (selecionado.getTipoObjetoCompra().isMaterialPermanente()) {
            if (hasPermissaoDesdobrarSemGrupo) {
                return facade.getObjetoCompraFacade().buscarObjetoCompraNaoReferenciaPorTipoObjetoCompra(codigoOrDescricao.trim(), TipoObjetoCompra.PERMANENTE_MOVEL);
            }
            if (itemRequisicaoDesdobravel.getObjetoCompra().getGrupoContaDespesa() != null && itemRequisicaoDesdobravel.getObjetoCompra().getGrupoContaDespesa().getIdGrupo() != null) {
                return facade.getObjetoCompraFacade().buscarObjetoCompraPermanentePorGrupoPatrimonial(
                    codigoOrDescricao.trim(),
                    itemRequisicaoDesdobravel.getObjetoCompra().getGrupoContaDespesa().getIdGrupo(),
                    selecionado.getDataRequisicao());
            }
        }
        return Lists.newArrayList();
    }

    public UnidadeOrganizacional getUnidadeAdministrativa() {
        if (selecionado.isTipoContrato()) {
            return facade.getContratoFacade().buscarUnidadeVigenteContrato(contrato).getUnidadeAdministrativa();
        } else if (selecionado.isTipoAtaRegistroPreco()) {
            return ataRegistroPreco.getUnidadeOrganizacional();
        } else if (selecionado.isTipoDispensaInexigibilidade()) {
            return dispensaDeLicitacao.getProcessoDeCompra().getUnidadeOrganizacional();
        }
        return reconhecimentoDivida.getUnidadeAdministrativa();
    }

    public boolean isLicitacaoMaiorDesconto() {
        return selecionado.isLicitacaoMaiorDesconto(contrato, ataRegistroPreco);
    }

    public boolean isControlePorValor() {
        return hasItensProcesso() && itensProcesso.stream().anyMatch(ItemRequisicaoProcessoLicitatorio::isControleValor);
    }

    public void listenerObjetoCompra() {
        if (itemRequisicao.getObjetoCompra() != null) {
            itemRequisicao.setDescricaoComplementar(null);
            atribuirGrupoAoItemRequisicao();
            TabelaEspecificacaoObjetoCompraControlador controlador = (TabelaEspecificacaoObjetoCompraControlador) Util.getControladorPeloNome("tabelaEspecificacaoObjetoCompraControlador");
            controlador.recuperarObjetoCompra(itemRequisicao.getObjetoCompra());
        }
    }

    public void listenerTipoDesconto() {
        this.itemRequisicao.calcularValorTotal();
        this.itemRequisicao.calcularValorTotalItemEntradaPorCompra();
        updateItemRequisicaoDesdobravel();
    }

    public void copiarObjetoCompraDesdobrado() {
        try {
            validarCopiaObjetoCompraDesdobrado();
            itemRequisicao.setObjetoCompra(itemRequisicaoDesdobravel.getObjetoCompra());
            listenerObjetoCompra();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCopiaObjetoCompraDesdobrado() {
        ValidacaoException ve = new ValidacaoException();
        if (itemRequisicaoDesdobravel.getObjetoCompra().getReferencial()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido adicionar um objeto de compra do tipo referencial.");
        }
        if (!itemRequisicaoDesdobravel.getObjetoCompra().getAtivo()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido adicionar um objeto de compra inativo.");
        }
        ve.lancarException();
    }

    private void atribuirGrupoAoItemRequisicao() {
        itemRequisicao.getObjetoCompra().setGrupoContaDespesa(facade.getObjetoCompraFacade().criarGrupoContaDespesa(selecionado.getTipoObjetoCompra(), itemRequisicao.getObjetoCompra().getGrupoObjetoCompra()));
    }

    public void removerItemDesdobravel(ItemRequisicaoCompraVO itemReqVO) {
        itemRequisicao = itemReqVO;
        atribuirSaldoItemDesdobrado(true);
        itemRequisicaoDesdobravel.getItensDesdobrados().remove(itemReqVO);
    }

    private void atribuirSaldoItemDesdobrado(boolean isAdd) {
        BigDecimal valorCalculado = isAdd
            ? itemRequisicaoExecucao.getValorDisponivel().add(itemRequisicao.getValorTotal())
            : itemRequisicaoExecucao.getValorDisponivel().subtract(itemRequisicao.getValorTotal());
        itemRequisicaoExecucao.setValorDisponivel(valorCalculado);
    }

    public void editarItemDesdobrado(ItemRequisicaoCompraVO itemReqVO) {
        itemRequisicao = itemReqVO;
        atribuirSaldoItemDesdobrado(true);
        atribuirGrupoAoItemRequisicao();
        itemRequisicaoDesdobravel.setEditando(true);
        gerarTabelaValorDescontoItem();
        calcularValoresItemDesdobrado();

        for (DescontoItemRequisicaoCompra itemDesc : descontosItemRequisicao) {
            if (itemDesc.getTipoDesconto().equals(itemRequisicao.getTipoDesconto())) {
                BigDecimal valor = itemRequisicao.getValorDescontoUnitario().compareTo(BigDecimal.ZERO) > 0
                    ? itemRequisicao.getValorDescontoUnitario()
                    : itemRequisicao.getValorDescontoUnitarioPorTipoDesconto(itemRequisicao.getTipoDesconto());
                if (!itemDesc.getTipoDesconto().isNaoArredondar()) {
                    itemDesc.setDescontoUnitario(removerCasasDecimaisComZero(valor));
                }

                itemDesc.setDescontoTotal(removerCasasDecimaisComZero(itemRequisicao.getValorDescontoTotal().compareTo(BigDecimal.ZERO) > 0
                    ? itemRequisicao.getValorDescontoTotal()
                    : itemRequisicao.getValorDescontoTotalPorTipoDesconto(itemRequisicao.getTipoDesconto())));
                itemDesc.setSelecionado(true);
            }
        }
    }

    public void adicionarItemDesdobravel() {
        try {
            validarItensDesdobraveis();
            validarEspecificacao();

            for (ItemRequisicaoCompraExecucaoVO itemReqExec : itemRequisicao.getItensRequisicaoExecucao()) {
                itemReqExec.setQuantidade(itemRequisicao.getQuantidade());
                itemReqExec.setValorUnitario(itemRequisicao.getValorUnitario());
                itemReqExec.setValorTotal(itemRequisicao.getValorTotal());
            }
            Util.adicionarObjetoEmLista(itemRequisicaoDesdobravel.getItensDesdobrados(), itemRequisicao);
            itemRequisicaoExecucao.setValorUtilizado(itemRequisicaoExecucao.getValorUtilizado().add(itemRequisicao.getValorTotal()));
            atribuirSaldoItemDesdobrado(false);
            novoItemRequisicaoCompraDesdobrado();

            itemRequisicaoDesdobravel.setEditando(false);
            descontosItemRequisicao = Lists.newArrayList();
            FacesUtil.executaJavaScript("setaFoco('formDesdobrarItem:objetoDeCompra_input')");
            FacesUtil.atualizarComponente("formDesdobrarItem:formulario-item");
            FacesUtil.atualizarComponente("Formulario:gridGeral");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void confirmarItensDesdobrados() {
        itemRequisicao = null;
        itemRequisicaoDesdobravel.setEditando(false);
        descontosItemRequisicao = Lists.newArrayList();
        renumerarItensDesdobrados();
        FacesUtil.executaJavaScript("dlgDesdobrarItem.hide()");
        FacesUtil.atualizarComponente("Formulario:vl-total-requisicao");
    }

    public void renumerarItensDesdobrados() {
        itemRequisicaoDesdobravel.ordernarItensDesdobradosPorEmpenho();
        Iterator<ItemRequisicaoCompraVO> itens = itemRequisicaoDesdobravel.getItensDesdobrados().iterator();
        while (itens.hasNext()) {
            ItemRequisicaoCompraVO proximo = itens.next();
            int i = itemRequisicaoDesdobravel.getItensDesdobrados().indexOf(proximo);
            proximo.setNumero(i + 1);
            Util.adicionarObjetoEmLista(itemRequisicaoDesdobravel.getItensDesdobrados(), proximo);
        }
    }

    public void validarItensDesdobraveis() {
        ValidacaoException ex = new ValidacaoException();
        validarCamposObrigatoriosItemDesdobravel(ex);
        validarAssociacaoComObjetoCompraItemDesdobravel(ex);
        validarMesmoObjetoCompraItemDesdobravel();
        ex.lancarException();
    }

    private void validarMesmoObjetoCompraItemDesdobravel() {
        for (ItemRequisicaoCompraVO item : itensRequisicaoVO) {
            if (!item.equals(itemRequisicao)) {
                ValidacaoObjetoCompraEspecificacao validacaoObjetoCompraEspecificacao = new ValidacaoObjetoCompraEspecificacao();
                validacaoObjetoCompraEspecificacao.setObjetoCompraSelecionado(itemRequisicao.getObjetoCompra());
                validacaoObjetoCompraEspecificacao.setObjetoCompraEmLista(item.getObjetoCompra());
                validacaoObjetoCompraEspecificacao.setEspeficicacaoSelecionada(itemRequisicao.getDescricaoComplementar());
                validacaoObjetoCompraEspecificacao.setEspeficicacaoEmLista(item.getDescricaoComplementar());
                validacaoObjetoCompraEspecificacao.setNumeroItem(item.getNumero());
                facade.getObjetoCompraFacade().validarObjetoCompraAndEspecificacaoIguais(validacaoObjetoCompraEspecificacao);
            }
        }
    }

    private void validarAssociacaoComObjetoCompraItemDesdobravel(ValidacaoException ex) {
        ObjetoCompra objetoCompra = itemRequisicao.getObjetoCompra();
        if (objetoCompra.getTipoObjetoCompra().isMaterialConsumo()) {
            AssociacaoGrupoObjetoCompraGrupoMaterial associacao = facade.getAssocicaoGrupoMaterialFacade().buscarAssociacaoPorGrupoObjetoCompraVigente(
                objetoCompra.getGrupoObjetoCompra(),
                facade.getSistemaFacade().getDataOperacao());
            if (associacao == null) {
                ex.adicionarMensagemDeOperacaoNaoPermitida("Nenhuma associação encontrada com grupo de objeto de compra: " + objetoCompra.getGrupoObjetoCompra() + ".");
            }
        }
        if (objetoCompra.getTipoObjetoCompra().isMaterialPermanente()) {
            GrupoObjetoCompraGrupoBem associacao = facade.getAssocicaoGrupoBemFacade().recuperarAssociacaoDoGrupoObjetoCompra(objetoCompra.getGrupoObjetoCompra(), facade.getSistemaFacade().getDataOperacao());
            if (associacao == null) {
                ex.adicionarMensagemDeOperacaoNaoPermitida("Nenhuma associação encontrada com grupo de objeto de compra: " + objetoCompra.getGrupoObjetoCompra() + ".");
            }
        }
    }

    private void validarCamposObrigatoriosItemDesdobravel(ValidacaoException ex) {
        if (itemRequisicao.getObjetoCompra() == null) {
            ex.adicionarMensagemDeCampoObrigatorio("O campo objeto de compra deve ser informado.");
        }
        if ((itemRequisicao.getQuantidade() == null || itemRequisicao.getQuantidade().compareTo(BigDecimal.ZERO) <= 0)) {
            ex.adicionarMensagemDeCampoObrigatorio("O campo quantidade deve ser informada com um valor maior que 0(zero).");
        }
        if ((itemRequisicao.getValorUnitarioDesdobrado() == null || itemRequisicao.getValorUnitarioDesdobrado().compareTo(BigDecimal.ZERO) <= 0)) {
            ex.adicionarMensagemDeCampoObrigatorio("O campo valor unitário deve ser informado com um valor maior que 0(zero).");
        }
        if ((itemRequisicao.getValorUnitario() == null || itemRequisicao.getValorUnitario().compareTo(BigDecimal.ZERO) <= 0)) {
            ex.adicionarMensagemDeCampoObrigatorio("O campo valor unitário com desconto deve ser informado com um valor maior que 0(zero).");
        }
        ex.lancarException();
        if (itemRequisicao.getValorTotal().compareTo(itemRequisicaoExecucao.getValorDisponivel()) > 0) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("O valor total deve ser menor ou igual ao valor disponível de " + Util.formataValor(itemRequisicaoExecucao.getValorDisponivel()) + ".");
        }
        if (selecionado.getTipoObjetoCompra().isMaterialConsumo() && itemRequisicao.hasDiferencaRequisicaoComEntrada()) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("Divergência com Entrada por Compra", "O valor total da simulação da entrada por compra deve ser igual ao valor total com desconto do item requisição.");
        }
    }

    public void selecionarContrato(Contrato obj) {
        if (obj != null) {
            setContrato(obj);
            executarDependenciasAoSelecionarProcesso();
        }
    }

    public void executarDependenciasAoSelecionarProcesso() {
        try {
            registrarPrazoEstipuladoDeEntrega();
            validarContratoObras();
            buscarExecucoesEmpenhadas();
            hasPermissaoDesdobrarSemGrupo = facade.getConfiguracaoLicitacaoFacade().verificarUnidadeGrupoObjetoCompra(getUnidadeAdministrativa());
            if (execucoesVO.size() == 1) {
                RequisicaoCompraExecucaoVO reqExecVO = execucoesVO.get(0);
                if (reqExecVO != null) {
                    marcarExecucao(reqExecVO);
                    if (reqExecVO.getEmpenhosVO().size() == 1 && selecionado.getPrazoEntrega() != null)
                        FacesUtil.executaJavaScript("buscarItens()");
                }
            }
            itensRequisicaoVO.clear();
            buscarFiscaisAndGestorContrato();
            FacesUtil.executaJavaScript("aguarde.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void buscarFiscaisAndGestorContrato() {
        if (selecionado.isTipoContrato()) {
            fiscais = facade.getContratoFacade().buscarFiscaisContrato(contrato.getId());
            gestores = facade.getContratoFacade().buscarGestoresContrato(contrato.getId());
        }
    }

    public void registrarPrazoEstipuladoDeEntrega() {
        if (contrato != null && contrato.getObjetoAdequado().getSolicitacaoMaterial() != null) {
            selecionado.setPrazoEntrega(contrato.getObjetoAdequado().getSolicitacaoMaterial().getPrazoDeEntrega());
            selecionado.setTipoPrazoEntrega(contrato.getObjetoAdequado().getSolicitacaoMaterial().getTipoPrazoEntrega());

        } else if (selecionado.isTipoAtaRegistroPreco() && ataRegistroPreco != null) {
            selecionado.setPrazoEntrega(ataRegistroPreco.getLicitacao().getProcessoDeCompra().getSolicitacaoMaterial().getPrazoDeEntrega());
            selecionado.setTipoPrazoEntrega(ataRegistroPreco.getLicitacao().getProcessoDeCompra().getSolicitacaoMaterial().getTipoPrazoEntrega());

        } else if (selecionado.isTipoDispensaInexigibilidade() && dispensaDeLicitacao != null) {
            selecionado.setPrazoEntrega(dispensaDeLicitacao.getProcessoDeCompra().getSolicitacaoMaterial().getPrazoDeEntrega());
            selecionado.setTipoPrazoEntrega(dispensaDeLicitacao.getProcessoDeCompra().getSolicitacaoMaterial().getTipoPrazoEntrega());
        }
    }

    public boolean hasExecucoes() {
        return execucoesVO != null && !execucoesVO.isEmpty();
    }

    public void buscarExecucoesEmpenhadas() {
        try {
            execucoesVO = Lists.newArrayList();
            if (!isProcessoNulo()) {
                Long idProcesso;
                switch (selecionado.getTipoRequisicao()) {
                    case CONTRATO:
                        idProcesso = contrato.getId();
                        break;
                    case ATA_REGISTRO_PRECO:
                        idProcesso = ataRegistroPreco.getId();
                        break;
                    case DISPENSA_LICITACAO_INEXIGIBILIDADE:
                        idProcesso = dispensaDeLicitacao.getId();
                        break;
                    default:
                        idProcesso = reconhecimentoDivida.getId();
                        break;
                }
                execucoesVO = facade.buscarExecucoesEmpenhadas(idProcesso, selecionado.getTipoObjetoCompra());
                Collections.sort(execucoesVO);
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void selecionarRequisicaoExecucaoSalva() {
        execucoesVO.forEach(reqExecVO -> selecionado.getExecucoes().forEach(reqExecSalva -> {
            if (selecionado.isTipoContrato()
                && reqExecSalva.getExecucaoContratoEmpenho() != null
                && reqExecSalva.getExecucaoContrato().getId().equals(reqExecVO.getId())) {
                reqExecVO.setSelecionado(true);
                selecionarEmpenho(reqExecVO, reqExecSalva.getExecucaoContratoEmpenho().getEmpenho());
            }
            if (selecionado.isTipoExecucaoProcesso()
                && reqExecSalva.getExecucaoProcessoEmpenho() != null
                && reqExecSalva.getExecucaoProcesso().getId().equals(reqExecVO.getId())) {
                reqExecVO.setSelecionado(true);
                selecionarEmpenho(reqExecVO, reqExecSalva.getExecucaoProcessoEmpenho().getEmpenho());
            }
            if (selecionado.isTipoReconhecimentoDivida()
                && reqExecSalva.getExecucaoReconhecimentoDiv() != null
                && reqExecSalva.getReconhecimentoDivida().getId().equals(reqExecVO.getId())) {
                reqExecVO.setSelecionado(true);
                selecionarEmpenho(reqExecVO, reqExecSalva.getExecucaoReconhecimentoDiv().getSolicitacaoEmpenho().getEmpenho());
            }
        }));
    }

    private static void selecionarEmpenho(RequisicaoCompraExecucaoVO reqProcVO, Empenho empenho) {
        reqProcVO.getEmpenhosVO().stream()
            .filter(reqEmpVO -> reqEmpVO.getEmpenho().equals(empenho))
            .forEach(reqEmpVO -> reqEmpVO.setSelecionado(true));
    }

    public List<SelectItem> getTiposRequisicao() {
        return Util.getListSelectItem(Arrays.asList(TipoRequisicaoCompra.values()));
    }

    public List<SelectItem> getTiposPrazo() {
        return Util.getListSelectItemSemCampoVazio(TipoPrazo.values());
    }

    public void limparCamposProcesso() {
        setContrato(null);
        setReconhecimentoDivida(null);
        setAtaRegistroPreco(null);
        setDispensaDeLicitacao(null);
        execucoesVO = null;
        itensRequisicaoVO.clear();
        if (selecionado.isTipoContrato()) {
            FacesUtil.executaJavaScript("setaFoco('Formulario:autoComplete-contrato_input')");
        } else if (selecionado.isTipoAtaRegistroPreco()) {
            FacesUtil.executaJavaScript("setaFoco('Formulario:autoComplete-ata_input')");
        } else if (selecionado.isTipoDispensaInexigibilidade()) {
            FacesUtil.executaJavaScript("setaFoco('Formulario:autoComplete-dispensa_input')");
        } else {
            FacesUtil.executaJavaScript("setaFoco('Formulario:acRecDivida_input')");
        }
    }

    private void validarContratoObras() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.isTipoContrato() && getContrato().getObjetoAdequado().isObras()) {
            setContrato(facade.getContratoFacade().recuperar(contrato.getId()));
            if (contrato.isDeRegistroDePrecoExterno()) {
                contrato.getRegistroSolicitacaoMaterialExterno().setSolicitacaoMaterialExterno(facade.getSolicitacaoMaterialExternoFacade().recuperar(contrato.getRegistroSolicitacaoMaterialExterno().getSolicitacaoMaterialExterno().getId()));
            }
            if (!SubTipoObjetoCompra.CONSTRUCAO.equals(contrato.getObjetoAdequado().getSubTipoObjetoCompra())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Somente contratos de Obra e Serviço de Engenharia do tipo da obra: Construção poderão realizar a requisição de compra.");
            }
            ve.lancarException();
            BigDecimal valorTotalEmpenhadoDaObra = facade.valorTotalEmpenhadoDaObraPorContrato(contrato);
            if (valorTotalEmpenhadoDaObra.compareTo(contrato.getValorTotalExecucao()) < 0 ||
                valorTotalEmpenhadoDaObra.compareTo(contrato.getValorTotalExecucao()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Obra não foi totalmente empenhada, por favor empenhe todas as medições antes de fazer a requisição de compra");
            }
        }
        if (ve.temMensagens()) {
            setContrato(null);
            execucoesVO = null;
            itensRequisicaoVO.clear();
            throw ve;
        }
    }

    public void gerarRelatorioRequisicao(Long idRequisicao) {
        selecionado = facade.recuperarComDependenciasRequisicaoExecucao(idRequisicao);
        setOpcaoRelatorioReqCompra(OpcaoRelatorioReqCompra.COM_DESCRICAO);
        gerarRelatorio("PDF");
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            FacesUtil.executaJavaScript("dialogRelatorio.hide()");
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            String nomeRelatorio = "REQUISIÇÃO DE COMPRA";
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "ADMINISTRATIVO");
            dto.adicionarParametro("FILTROS", "Requisição de compra N°:" + selecionado.getNumero());
            dto.adicionarParametro("SECRETARIA", getHierarquiaAdm().toString().toUpperCase());
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("ID_REQUISICAO", selecionado.getId());
            dto.adicionarParametro("TIPO_REQUISICAO", selecionado.getTipoRequisicao().getDescricao());
            dto.adicionarParametro("ISCOMDESCPRODUTO", isRelatorioComDescricao());
            dto.adicionarParametro("MAIOR_DESCONTO", selecionado.isLicitacaoMaiorDesconto());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/requisicao-de-compra/");
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

    public void pesquisarContrato() {
        if (selecionado.getTipoObjetoCompra() == null) {
            contratos = Lists.newArrayList();
        }
        contratos = facade.getContratoFacade().buscarFiltrandoOndeUsuarioGestorLicitacao(
            filtroContrato,
            selecionado.getTipoObjetoCompra(),
            301);
    }

    public List<AtaRegistroPreco> completarAtaRegistroPreco(String parte) {
        return facade.getAtaRegistroPrecoFacade().buscarAtaRegistroPrecoComExecucao(parte.trim(), selecionado.getTipoObjetoCompra());
    }

    public List<DispensaDeLicitacao> completarDispensaLicitacao(String parte) {
        return facade.getDispensaDeLicitacaoFacade().buscarDispensaLicitacaoExecucaoProcessoPorNumeroOrExercicioOrResumo(parte.trim(), selecionado.getTipoObjetoCompra());
    }

    public List<Contrato> completarContrato(String parte) {
        if (selecionado.getTipoObjetoCompra() == null) {
            contratos = Lists.newArrayList();
        }
        filtroContrato.setParte(parte);
        return facade.getContratoFacade().buscarFiltrandoOndeUsuarioGestorLicitacao(
            filtroContrato,
            selecionado.getTipoObjetoCompra(),
            50);
    }

    public List<ReconhecimentoDivida> completarReconhecimentoDivida(String parte) {
        return facade.getReconhecimentoDividaFacade().buscarReconhecimentoDividaSolicitacaoConcluida(parte.trim());
    }

    public void novaPesquisaContratos() {
        contratos = Lists.newArrayList();
        filtroContrato = new FiltroContratoRequisicaoCompra();
    }

    public void redirecionarParaRequisicaoCompra(RequisicaoDeCompra requisicaoDeCompra) {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + requisicaoDeCompra.getId() + "/");
    }

    public void addFornecedorSet(RequisicaoCompraEmpenhoVO empVO) {
        if (empVO.getSelecionado()) {
            fornecedorSet.add(empVO.getEmpenho().getFornecedor());
        } else {
            fornecedorSet.remove(empVO.getEmpenho().getFornecedor());
        }
    }

    public void marcarExecucao(RequisicaoCompraExecucaoVO reqProcVO) {
        try {
            reqProcVO.setSelecionado(true);
            if (reqProcVO.getEmpenhosVO().size() == 1) {
                reqProcVO.getEmpenhosVO().forEach(emp -> emp.setSelecionado(reqProcVO.getSelecionado()));
                fornecedorSet.add(reqProcVO.getEmpenhosVO().get(0).getEmpenho().getFornecedor());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void desmarcarExecucao(RequisicaoCompraExecucaoVO reqExecucao) {
        reqExecucao.setSelecionado(false);
        reqExecucao.getEmpenhosVO().forEach(empVO -> {
            empVO.setSelecionado(false);
            fornecedorSet.remove(empVO.getEmpenho().getFornecedor());
        });
    }

    public List<Contrato> getContratos() {
        return contratos;
    }

    public void setContratos(List<Contrato> contratos) {
        this.contratos = contratos;
    }

    public FiltroContratoRequisicaoCompra getFiltroContrato() {
        return filtroContrato;
    }

    public void setFiltroContrato(FiltroContratoRequisicaoCompra filtroContrato) {
        this.filtroContrato = filtroContrato;
    }

    public List<RequisicaoCompraEstorno> getEstornosRequisicaoCompra() {
        return estornosRequisicaoCompra;
    }

    public void setEstornosRequisicaoCompra(List<RequisicaoCompraEstorno> estornosRequisicaoCompra) {
        this.estornosRequisicaoCompra = estornosRequisicaoCompra;
    }

    public ItemRequisicaoCompraVO getItemRequisicao() {
        return itemRequisicao;
    }

    public void setItemRequisicao(ItemRequisicaoCompraVO itemRequisicao) {
        this.itemRequisicao = itemRequisicao;
    }

    public ItemRequisicaoCompraVO getItemRequisicaoDesdobravel() {
        return itemRequisicaoDesdobravel;
    }

    public void setItemRequisicaoDesdobravel(ItemRequisicaoCompraVO itemRequisicaoDesdobravel) {
        this.itemRequisicaoDesdobravel = itemRequisicaoDesdobravel;
    }

    public List<RequisicaoCompraExecucaoVO> getExecucoesVO() {
        return execucoesVO;
    }

    public void setExecucoesVO(List<RequisicaoCompraExecucaoVO> execucoesVO) {
        this.execucoesVO = execucoesVO;
    }

    public List<DescontoItemRequisicaoCompra> getDescontosItemRequisicao() {
        return descontosItemRequisicao;
    }

    public void setDescontosItemRequisicao(List<DescontoItemRequisicaoCompra> descontosItemRequisicao) {
        this.descontosItemRequisicao = descontosItemRequisicao;
    }

    public OpcaoRelatorioReqCompra getOpcaoRelatorioReqCompra() {
        return opcaoRelatorioReqCompra;
    }

    public void setOpcaoRelatorioReqCompra(OpcaoRelatorioReqCompra opcaoRelatorioReqCompra) {
        this.opcaoRelatorioReqCompra = opcaoRelatorioReqCompra;
    }

    public List<SelectItem> getOpcoes() {
        return Util.getListSelectItemSemCampoVazio(OpcaoRelatorioReqCompra.values());
    }

    public Boolean isRelatorioComDescricao() {
        return opcaoRelatorioReqCompra.equals(OpcaoRelatorioReqCompra.COM_DESCRICAO);
    }

    public List<EntradaCompraMaterial> getEntradasComprasMateriais() {
        return entradasComprasMateriais;
    }

    public void setEntradasComprasMateriais(List<EntradaCompraMaterial> entradasComprasMateriais) {
        this.entradasComprasMateriais = entradasComprasMateriais;
    }

    public DoctoFiscalEntradaCompra getDoctoFiscalEntradaCompra() {
        return doctoFiscalEntradaCompra;
    }

    public void setDoctoFiscalEntradaCompra(DoctoFiscalEntradaCompra doctoFiscalEntradaCompra) {
        this.doctoFiscalEntradaCompra = doctoFiscalEntradaCompra;
    }

    public List<SolicitacaoAquisicao> getSolicitacoesAquisicoes() {
        return solicitacoesAquisicoes;
    }

    public void setSolicitacoesAquisicoes(List<SolicitacaoAquisicao> solicitacoesAquisicoes) {
        this.solicitacoesAquisicoes = solicitacoesAquisicoes;
    }

    public HierarquiaOrganizacional getHierarquiaAdm() {
        if (hierarquiaAdm == null) {
            atualizarHierarquiaAdm();
        }
        return hierarquiaAdm;
    }

    public void setHierarquiaAdm(HierarquiaOrganizacional hierarquiaAdm) {
        this.hierarquiaAdm = hierarquiaAdm;
    }

    private void atualizarHierarquiaAdm() {
        if (selecionado.getId() != null && selecionado.getDataRequisicao() != null) {
            UnidadeOrganizacional unidadeAdm = facade.getUnidadeAdministrativa(selecionado, selecionado.getDataRequisicao());
            hierarquiaAdm = facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), unidadeAdm, selecionado.getDataRequisicao());
        }
    }

    public DoctoFiscalSolicitacaoAquisicao getDoctoFiscalSolicitacaoAquisicao() {
        return doctoFiscalSolicitacaoAquisicao;
    }

    public void setDoctoFiscalSolicitacaoAquisicao(DoctoFiscalSolicitacaoAquisicao doctoFiscalSolicitacaoAquisicao) {
        this.doctoFiscalSolicitacaoAquisicao = doctoFiscalSolicitacaoAquisicao;
    }

    public ItemDoctoItemAquisicao getItemDoctoSolicitacaoAquisicao() {
        return itemDoctoSolicitacaoAquisicao;
    }

    public void setItemDoctoSolicitacaoAquisicao(ItemDoctoItemAquisicao itemDoctoSolicitacaoAquisicao) {
        this.itemDoctoSolicitacaoAquisicao = itemDoctoSolicitacaoAquisicao;
    }

    public void cancelarDocumentoAquisicao() {
        doctoFiscalSolicitacaoAquisicao = null;
        itemDoctoSolicitacaoAquisicao = null;
    }

    public List<LiquidacaoVO> getLiquidacoesVO() {
        return liquidacoesVO;
    }

    public void setLiquidacoesVO(List<LiquidacaoVO> liquidacoesVO) {
        this.liquidacoesVO = liquidacoesVO;
    }

    public List<FiscalContratoDTO> getFiscais() {
        return fiscais;
    }

    public List<GestorContrato> getGestores() {
        return gestores;
    }

    public void buscarLiquidacoesVOPorDocumentoFiscalEntrada(DoctoFiscalLiquidacao doctoFiscalLiquidacao) {
        liquidacoesVO = facade.buscarLiquidacoesVOPorDocumentoFiscal(doctoFiscalLiquidacao);
    }

    public String getDescricaoTipsObjetoCompra() {
        if (hasPermissaoDesdobrarSemGrupo) {
            return "O desdobramento do item original para novos objetos de compra com grupos diferentes do empenho implicará em transferência contábil automatica entre os grupos ao salvar a liquidação.";
        }
        return "A pesquisa irá trazer os objetos de compra não referenciais, contendo o mesmo grupo do objeto de compra do item que está sendo desdobrado.";
    }

    public void gerarTermoDeResponsabilidadeAquisicao(Long idAquisicao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            String nomeRelatorio = "TERMO DE RESPONSABILIDADE";
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("SECRETARIA", "SETOR PATRIMONIAL");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("condicao", " WHERE AQUISICAO.ID = " + idAquisicao);
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/termo-responsabilidade-aquisicao/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            FacesUtil.addErroAoGerarRelatorio("Ocorreu um erro ao gerar o relatório: " + ex.getMessage());
        }
    }

    public void gerarRelatorioEntradaPorCompra(Long idEntradaMateria) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            String nomeRelatorio = "RELATÓRIO DE ENTRADA DE MATERIAIS POR COMPRA";
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOME_RELATORIO", nomeRelatorio);
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("MODULO", "Materiais");
            dto.adicionarParametro("idEntradaMaterial", idEntradaMateria);
            dto.adicionarParametro("TIPO_REQUISICAO", selecionado.getTipoRequisicao().getDescricao());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/entrada-compra-material/");
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

    public void redirecionarContrato() {
        String requestContext = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        String url = "window.open('" + requestContext + "/contrato-adm/ver/" + contrato.getId() + "/', '_blank');";
        RequestContext.getCurrentInstance().execute(url);
    }

    public List<Licitacao> completarLicitacao(String parte) {
        return facade.getLicitacaoFacade().buscarLicitacaoHomologadaPregaoRealizado(parte.trim());
    }

    public List<ObjetoCompra> completarObjetoCompraPorTipo(String parte) {
        return facade.getObjetoCompraFacade().buscarObjetoCompraPorCodigoOrDescricaoAndTipoObjetoCompra(parte, selecionado.getTipoObjetoCompra());
    }

    private void preecherItemRequisicaoDerivacaoEdicao() {
        itensDerivacao = Lists.newArrayList();
        if (selecionado.getTipoRequisicao().isContrato()) {
            buscarItensRequisicaoDerivacao();

            itensDerivacao.forEach(itemReqDeriv -> {
                List<ItemRequisicaoDeCompra> itensReqComponente = facade.buscarItemRequisicaoDerivacaoComponente(itemReqDeriv.getItemRequisicaoOriginal().getItemContrato(), selecionado, itemReqDeriv.getDerivacaoObjetoCompra());

                itensReqComponente.forEach(itemReqComp -> {
                    ItemRequisicaoCompraVO novoItemReqCompVO = ItemRequisicaoCompraVO.toObjeto(itemReqComp);
                    novoItemReqCompVO.setRequisicaoDeCompra(selecionado);

                    itemReqComp.getItensRequisicaoExecucao().forEach(itemReqEx -> {
                        ItemRequisicaoCompraExecucaoVO novoItemReqProcVO = ItemRequisicaoCompraExecucaoVO.toObjeto(itemReqEx, novoItemReqCompVO);
                        RequisicaoCompraEmpenhoVO novoReqEmpVO = facade.novoEmpenhoVO(itemReqEx.getEmpenho(), selecionado.getTipoObjetoCompra());
                        novoItemReqProcVO.setRequisicaoEmpenhoVO(novoReqEmpVO);
                        ExecucaoContratoItemDotacao itemDotExec = facade.getExecucaoContratoFacade().buscarItemDotacaoPorItemExecucao(itemReqEx.getExecucaoContratoItem(), itemReqEx.getEmpenho().getFonteDespesaORC());
                        if (itemDotExec != null) {
                            novoItemReqProcVO.setQuantidadeDisponivel(getQuantidadeDisponivelProcessoContrato(itemReqEx.getExecucaoContratoItem(), itemDotExec, itemReqEx));
                        }
                        novoItemReqCompVO.getItensRequisicaoExecucao().add(novoItemReqProcVO);
                    });
                    itemReqDeriv.getItensRequisicaoComponente().add(novoItemReqCompVO);
                });
                itemReqDeriv.setQuantidadDisponivel(itemReqDeriv.getQuantidadeUtilizada());
                itensRequisicaoVO.remove(itemReqDeriv.getItemRequisicaoOriginal());

                itemReqDeriv.getItensRequisicaoComponente()
                    .stream()
                    .flatMap(itemD -> itemD.getItensRequisicaoExecucao().stream())
                    .forEach(itemC -> itemC.setQuantidadeDisponivel(itemReqDeriv.getQuantidadeDisponivel()));
            });
            itensDerivacao.forEach(itemDeriv -> itensRequisicaoVO.addAll(itemDeriv.getItensRequisicaoComponente()));
        }
        Collections.sort(itensRequisicaoVO);
    }

    private void buscarItensRequisicaoDerivacao() {
        for (ItemRequisicaoCompraVO itemReqOriginal : itensRequisicaoVO) {
            DerivacaoObjetoCompra derivacaoObjComp = facade.buscarDerivacaoObjetoCompraItemRequisicao(itemReqOriginal.getItemContrato(), selecionado);
            if (derivacaoObjComp != null) {
                ItemRequisicaoCompraDerivacao novoItemDerivacao = novoItemRequisicaoCompraDerivacao(itemReqOriginal);
                novoItemDerivacao.setDerivacaoObjetoCompra(derivacaoObjComp);
                itensDerivacao.add(novoItemDerivacao);
            }
        }
    }

    public void derivarObjeto(ItemRequisicaoCompraVO itemReqList) {
        List<DerivacaoObjetoCompraComponente> componentes = facade.getDerivacaoObjetoCompraComponenteFacade().buscarComponentesPorDerivacao(itemReqList.getObjetoCompra().getDerivacaoObjetoCompra());
        if (!Util.isListNullOrEmpty(componentes)) {
            ItemRequisicaoCompraDerivacao novoItemDerivacao = novoItemRequisicaoCompraDerivacao(itemReqList);

            for (DerivacaoObjetoCompraComponente compDeriv : componentes) {
                novoItemRequisicaoDerivacaoComponente(compDeriv, itemReqList, novoItemDerivacao);
            }
            itensDerivacao.add(novoItemDerivacao);
            itensRequisicaoVO.addAll(novoItemDerivacao.getItensRequisicaoComponente());
        }
        itensRequisicaoVO.remove(itemReqList);
        Collections.sort(itensRequisicaoVO);
    }

    private void novoItemRequisicaoDerivacaoComponente(DerivacaoObjetoCompraComponente compDeriv, ItemRequisicaoCompraVO itemReqParam, ItemRequisicaoCompraDerivacao novoItemDerivacao) {
        ItemRequisicaoCompraVO novoItemReq = new ItemRequisicaoCompraVO();
        novoItemReq.setRequisicaoDeCompra(selecionado);
        novoItemReq.setObjetoCompra(itemReqParam.getObjetoCompra());
        novoItemReq.setUnidadeMedida(itemReqParam.getUnidadeMedida());
        novoItemReq.setNumero(itemReqParam.getNumero());
        novoItemReq.setNumeroItemProcesso(itemReqParam.getNumeroItemProcesso());
        novoItemReq.setQuantidadeDisponivel(itemReqParam.getQuantidadeDisponivel());
        novoItemReq.setValorUnitario(itemReqParam.getValorUnitario());
        novoItemReq.setDescricaoComplementar(itemReqParam.getDescricaoComplementar());
        novoItemReq.setDerivacaoComponente(compDeriv);
        novoItemReq.setTipoControle(itemReqParam.getTipoControle());

        for (ItemRequisicaoCompraExecucaoVO itemEx : itemReqParam.getItensRequisicaoExecucao()) {
            ItemRequisicaoCompraExecucaoVO novoItemReqEx = new ItemRequisicaoCompraExecucaoVO();
            novoItemReqEx.setExecucaoContratoItem(itemEx.getExecucaoContratoItem());
            novoItemReqEx.setItemRequisicaoCompraVO(novoItemReq);
            novoItemReqEx.setRequisicaoEmpenhoVO(itemEx.getRequisicaoEmpenhoVO());
            novoItemReqEx.setQuantidade(itemReqParam.getQuantidade());
            novoItemReqEx.setQuantidadeDisponivel(itemEx.getQuantidadeDisponivel());
            novoItemReqEx.setValorUnitario(novoItemReq.getValorUnitario());
            novoItemReqEx.calcularValorTotal();
            novoItemReq.getItensRequisicaoExecucao().add(novoItemReqEx);
        }
        novoItemDerivacao.getItensRequisicaoComponente().add(novoItemReq);
    }

    private ItemRequisicaoCompraDerivacao novoItemRequisicaoCompraDerivacao(ItemRequisicaoCompraVO itemReqParam) {
        ItemRequisicaoCompraDerivacao novoItemDerivacao = new ItemRequisicaoCompraDerivacao();
        novoItemDerivacao.setItemRequisicaoOriginal(itemReqParam);
        novoItemDerivacao.setQuantidadDisponivel(itemReqParam.getQuantidadeDisponivel());
        return novoItemDerivacao;
    }

    public void removerItemRequisicaoDerivado(ItemRequisicaoCompraVO itemReqVO) {
        ItemRequisicaoCompraDerivacao itemReqDerivado = getItemRequisicaoCompraDerivado(itemReqVO);
        if (itemReqDerivado == null) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao derivar o item requisição " + itemReqVO);
            return;
        }
        itensRequisicaoVO.remove(itemReqVO);
        itemReqDerivado.getItensRequisicaoComponente().remove(itemReqVO);

        if (itemReqDerivado.getItensRequisicaoComponente().isEmpty()) {
            itemReqDerivado.getItemRequisicaoOriginal().setQuantidadeDisponivel(itemReqDerivado.getQuantidadeDisponivel());
            itemReqDerivado.getItemRequisicaoOriginal().getItensRequisicaoExecucao().forEach(itemEx -> itemEx.setQuantidadeDisponivel(itemReqDerivado.getQuantidadeDisponivel()));
            itensRequisicaoVO.add(itemReqDerivado.getItemRequisicaoOriginal());
            itensDerivacao.remove(itemReqDerivado);
        }
        Collections.sort(itensRequisicaoVO);
    }

    public ItemRequisicaoCompraDerivacao getItemRequisicaoCompraDerivado(ItemRequisicaoCompraVO itemReq) {
        Optional<ItemRequisicaoCompraDerivacao> first = itensDerivacao
            .stream().filter(itemD -> itemD.getItemRequisicaoOriginal().getItemContrato().getId().equals(itemReq.getItemContrato().getId()))
            .findFirst();
        return first.orElse(null);
    }

    public boolean isItemDesdobradoDoItemExecucao(ItemRequisicaoCompraVO itemReqVO) {
        return itemRequisicaoExecucao != null
            && itemRequisicaoExecucao.getIdItemExecucao() != null
            && itemReqVO != null
            && itemRequisicaoExecucao.getIdItemExecucao().equals(itemReqVO.getIdItemExecucao());
    }

    public boolean hasItensDerivacao() {
        return !Util.isListNullOrEmpty(itensDerivacao);
    }

    public AtaRegistroPreco getAtaRegistroPreco() {
        return ataRegistroPreco;
    }

    public void setAtaRegistroPreco(AtaRegistroPreco ataRegistroPreco) {
        this.ataRegistroPreco = ataRegistroPreco;
    }

    public DispensaDeLicitacao getDispensaDeLicitacao() {
        return dispensaDeLicitacao;
    }

    public void setDispensaDeLicitacao(DispensaDeLicitacao dispensaDeLicitacao) {
        this.dispensaDeLicitacao = dispensaDeLicitacao;
    }

    public List<TipoContaDespesa> getTiposContaDespesa() {
        return tiposContaDespesa;
    }

    public void setTiposContaDespesa(List<TipoContaDespesa> tiposContaDespesa) {
        this.tiposContaDespesa = tiposContaDespesa;
    }

    public void selecionarObjetoCompraConsultaEntidade(Long idObjeto) {
        if (idObjeto != null) {
            itemRequisicao.setObjetoCompra(facade.getObjetoCompraFacade().recuperar(idObjeto));
            atribuirGrupoAoItemRequisicao();

            if (!hasPermissaoDesdobrarSemGrupo
                && !itemRequisicaoDesdobravel.getObjetoCompra().getGrupoContaDespesa().getIdGrupo().equals(itemRequisicao.getObjetoCompra().getGrupoContaDespesa().getIdGrupo())) {
                FacesUtil.addOperacaoNaoPermitida("Não é permitido selecionar um objeto de compra com o grupo diferente do objeto de compra de referência.");
                FacesUtil.addOperacaoNaoPermitida("Grupo de Referência: " + itemRequisicaoDesdobravel.getObjetoCompra().getGrupoContaDespesa().getGrupo());
                FacesUtil.addOperacaoNaoPermitida("Grupo do Objeto Selecionado: " + itemRequisicao.getObjetoCompra().getGrupoContaDespesa().getGrupo());
                itemRequisicao.setObjetoCompra(null);
            }
            if (itemRequisicao.getObjetoCompra() != null) {
                TabelaEspecificacaoObjetoCompraControlador controlador = (TabelaEspecificacaoObjetoCompraControlador) Util.getControladorPeloNome("tabelaEspecificacaoObjetoCompraControlador");
                controlador.recuperarObjetoCompra(itemRequisicao.getObjetoCompra());
            }
            FacesUtil.executaJavaScript("dlgPesquisaObj.hide()");
            FacesUtil.atualizarComponente("formDesdobrarItem:formulario-item");
            FacesUtil.atualizarComponente("formDesdobrarItem:pn-unidade-medida");
        }
    }

    public String getOrigemContrato() {
        return origemContrato;
    }

    public void setOrigemContrato(String origemContrato) {
        this.origemContrato = origemContrato;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public ReconhecimentoDivida getReconhecimentoDivida() {
        return reconhecimentoDivida;
    }

    public void setReconhecimentoDivida(ReconhecimentoDivida reconhecimentoDivida) {
        this.reconhecimentoDivida = reconhecimentoDivida;
    }

    public List<ItemRequisicaoCompraVO> getItensRequisicaoVO() {
        return itensRequisicaoVO;
    }

    public void setItensRequisicaoVO(List<ItemRequisicaoCompraVO> itensRequisicaoVO) {
        this.itensRequisicaoVO = itensRequisicaoVO;
    }

    public ItemRequisicaoCompraExecucaoVO getItemRequisicaoExecucao() {
        return itemRequisicaoExecucao;
    }

    public void setItemRequisicaoExecucao(ItemRequisicaoCompraExecucaoVO itemRequisicaoExecucao) {
        this.itemRequisicaoExecucao = itemRequisicaoExecucao;
    }

    public List<ItemRequisicaoCompraVO> getItensRequisicaoDesdobradoVO() {
        return itensRequisicaoDesdobradoVO;
    }

    public void setItensRequisicaoDesdobradoVO(List<ItemRequisicaoCompraVO> itensRequisicaoDesdobradoVO) {
        this.itensRequisicaoDesdobradoVO = itensRequisicaoDesdobradoVO;
    }

    public void setFiscais(List<FiscalContratoDTO> fiscais) {
        this.fiscais = fiscais;
    }
}
