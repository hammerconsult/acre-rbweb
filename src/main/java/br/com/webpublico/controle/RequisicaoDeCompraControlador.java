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
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.RequisicaoDeCompraFacade;
import br.com.webpublico.negocios.SolicitacaoAquisicaoFacade;
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
    @EJB
    private SolicitacaoAquisicaoFacade solicitacaoAquisicaoFacade;
    private String filtroParte;
    private FiltroContratoRequisicaoCompra filtroContrato;
    private List<Contrato> contratos;
    private List<RequisicaoCompraEstorno> estornosRequisicaoCompra;
    private List<ItemRequisicaoCompraDesdobravel> itensDesdobraveis;
    private List<ItemProcessoLicitacao> itensProcesso;
    private List<RequisicaoExecucaoExecucaoVO> execucoesRequisicao;
    private List<DescontoItemRequisicaoCompra> descontosItemRequisicao;
    private ItemRequisicaoCompraDesdobravel itemDesdobravel;
    private ItemRequisicaoDeCompra itemRequisicao;
    private Integer indiceAba;
    private List<EntradaCompraMaterial> entradasComprasMateriais;
    private DoctoFiscalEntradaCompra doctoFiscalEntradaCompra;
    private List<SolicitacaoAquisicao> solicitacoesAquisicoes;
    private HierarquiaOrganizacional hierarquiaAdm;
    private DoctoFiscalSolicitacaoAquisicao doctoFiscalSolicitacaoAquisicao;
    private ItemDoctoItemAquisicao itemDoctoSolicitacaoAquisicao;
    private List<LiquidacaoVO> liquidacoesVO;
    private OpcaoRelatorioReqCompra opcaoRelatorioReqCompra;
    private Boolean hasPermissaoDesdobrarSemGrupo;
    private List<FiscalContratoDTO> fiscais;
    private List<GestorContrato> gestores;
    private List<TipoContaDespesa> tiposContaDespesa;
    private AtaRegistroPreco ataRegistroPreco;
    private DispensaDeLicitacao dispensaDeLicitacao;
    private List<ItemRequisicaoCompraDerivacao> itensDerivacao;
    private String origemContrato;

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
        indiceAba = 0;
        fiscais = Lists.newArrayList();
        gestores = Lists.newArrayList();
        itensDerivacao = Lists.newArrayList();
    }

    @Override
    @URLAction(mappingId = "editar-requisicao-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        try {
            super.editar();
            validarDisponibilidadeMovimentacao();
            definirAtaOrDispensaExecucaoProcesso();
            buscarExecucoesEmpenhadas();
            selecionarRequisicaoExecucaoSalva();
            buscarItensProcesso();
            if (!permiteDesdobrarItens()) {
                selecionado.setItens(Lists.newArrayList());
                buscarTiposContaDespesaEmpenho();
                criarItensRequisicaoCompraSemDesdobrar(itensProcesso);
                separarItensPorTipoContaDespesaEmpenho();
            }
            preencherItemDesdobravel();
            preecherItemRequisicaoDerivacaoEdicao();
            hasPermissaoDesdobrarSemGrupo = facade.getConfiguracaoLicitacaoFacade().verificarUnidadeGrupoObjetoCompra(getUnidadeAdministrativa());
            Collections.sort(selecionado.getItens());
            liquidacoesVO = Lists.newArrayList();
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
        buscarItensProcessoVisualizar();
        preencherItemDesdobravel();
        estornosRequisicaoCompra = facade.getRequisicaoCompraEstornoFacade().buscarPorRequisicaoCompra(selecionado);
        buscarRecuperandoEntradasComprasMateriais();
        buscarRecuperandoSolicitacoesAquisicoes();
        Collections.sort(selecionado.getItens());
        for (ItemRequisicaoDeCompra item : selecionado.getItens()) {
            Collections.sort(item.getItensRequisicaoExecucao());
        }
        setOpcaoRelatorioReqCompra(OpcaoRelatorioReqCompra.COM_DESCRICAO);
        liquidacoesVO = Lists.newArrayList();
        if (selecionado.isTipoContrato()) {
            origemContrato = facade.getContratoFacade().getOrigemContrato(selecionado.getContrato());
        }
    }

    private void definirAtaOrDispensaExecucaoProcesso() {
        if (selecionado.isTipoAtaRegistroPreco()) {
            ataRegistroPreco = selecionado.getExecucaoProcesso().getAtaRegistroPreco();
        }
        if (selecionado.isTipoDispensaInexigibilidade()) {
            dispensaDeLicitacao = selecionado.getExecucaoProcesso().getDispensaLicitacao();
        }
    }


    @Override
    public void salvar() {
        try {
            validarRegrasDeNegocio();
            removerItemComValorZerado();
            criarRequisicaoCompraExecucao();
            atribuirDerivacaoComponenteNaEspecificacaoItemRequisicao();
            selecionado = facade.salvarRequisicao(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionarParaRequisicaoCompra(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
            logger.error(" erro ao salvar selecionado.: {} cause.: {} ", selecionado, ex.getCause());
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
            logger.error(" erro ao concluir requisição de compra.: {} cause.: {} ", selecionado, ex.getCause());
        }
    }

    private void atribuirDerivacaoComponenteNaEspecificacaoItemRequisicao() {
        selecionado.getItens().stream()
            .filter(ItemRequisicaoDeCompra::isDerivacaoObjetoCompra)
            .forEach(item -> item.setDescricaoComplementar(item.getDescricaoComplementar() + " " + item.getDerivacaoComponente().getDescricao()));
    }

    public boolean existeDescricaoPrazoEntrega() {
        if (selecionado.getDescricaoPrazoEntrega() == null) {
            return false;
        }
        return true;
    }

    public void redirecionaOrigemContrato() {
        if (selecionado.getContrato().isDeLicitacao() || selecionado.getContrato().isDeAdesaoAtaRegistroPrecoInterna() || selecionado.getContrato().isDeIrp()) {
            FacesUtil.redirecionamentoInterno("/licitacao/ver/" + selecionado.getContrato().getLicitacao().getId() + "/");
            return;
        }
        if (selecionado.getContrato().isDeDispensaDeLicitacao()) {
            FacesUtil.redirecionamentoInterno("/dispensa-licitacao/ver/" + selecionado.getContrato().getDispensaDeLicitacao().getId() + "/");
            return;
        }
        if (selecionado.getContrato().isDeRegistroDePrecoExterno()) {
            FacesUtil.redirecionamentoInterno("/adesao-a-ata-de-registro-de-preco-externo/ver/" + selecionado.getContrato().getContratoRegistroPrecoExterno().getRegistroSolicitacaoMaterialExterno().getId() + "/");
            return;
        }
        if (selecionado.getContrato().isDeProcedimentosAuxiliares()) {
            FacesUtil.redirecionamentoInterno("/credenciamento/ver/" + selecionado.getContrato().getLicitacao().getId() + "/");
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
        solicitacaoAquisicaoFacade.buscarSolicitacoesPorRequisicao(selecionado).forEach(
            sol -> solicitacoesAquisicoes.add(solicitacaoAquisicaoFacade.recuperarSolicitacaoAquisicaoComItensSolicitacaoPorItemDocto(sol))
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

    private void removerItemComValorZerado() {
        Iterator<ItemRequisicaoDeCompra> iterator = selecionado.getItens().iterator();
        while (iterator.hasNext()) {
            ItemRequisicaoDeCompra next = iterator.next();
            if (!next.hasQuantidade()) {
                iterator.remove();
            }
        }
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

    private void criarRequisicaoCompraExecucao() {
        if (!selecionado.getTipoRequisicao().isReconhecimentoDivida()) {
            selecionado.setExecucoes(Lists.<RequisicaoCompraExecucao>newArrayList());
            for (RequisicaoExecucaoExecucaoVO exec : execucoesRequisicao) {
                if (exec.getSelecionado()) {
                    RequisicaoCompraExecucao reqExec = new RequisicaoCompraExecucao();
                    reqExec.setRequisicaoCompra(selecionado);
                    if (selecionado.isTipoContrato()) {
                        reqExec.setExecucaoContrato(exec.getExecucaoContrato());
                    } else {
                        reqExec.setExecucaoProcesso(exec.getExecucaoProcesso());
                    }
                    Util.adicionarObjetoEmLista(selecionado.getExecucoes(), reqExec);
                }
            }
        }
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
        validarItensDaRequisicaoVazia(ex);
        validarQuantidadeRequisitada(ex);
        validarDataRequisicao(ex);
        ex.lancarException();
    }

    private void validarCamposObrigatorio() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        switch (selecionado.getTipoRequisicao()) {
            case CONTRATO:
                if (selecionado.getContrato() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo contrato deve ser informado.");
                }
                if (!hasExecucaoSelecionada()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione uma execução para continuar.");
                }
                break;
            case ATA_REGISTRO_PRECO:
                if (ataRegistroPreco == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo ata registro de preço deve ser informado.");
                }
                break;
            case DISPENSA_LICITACAO_INEXIGIBILIDADE:
                if (dispensaDeLicitacao == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo dispensa/inexigibilidade deve ser informado.");
                }
                break;
            case RECONHECIMENTO_DIVIDA:
                if (selecionado.getReconhecimentoDivida() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo reconhecimento de dívida deve ser informado.");
                }
                break;
        }
        ve.lancarException();
    }

    private void validarItensDaRequisicaoVazia(ValidacaoException ex) {
        if (!selecionado.hasItens()) {
            ex.adicionarMensagemDeOperacaoNaoRealizada("A requisição de compra não possui itens adicionados.");
        }
        if (indiceAba == 2) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("Existem itens desdobrados que ainda não foram adicionados a requisição de compra. Clique no botão 'Confirmar Itens' para concluir a operação.");
        }
        if (!hasItemRequisicaoSelecionado()) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("Informe a quantidade desejada para ao menos um item antes de continuar.");
        }
        ex.lancarException();
    }

    private void validarQuantidadeRequisitada(ValidacaoException ex) {
        for (ItemRequisicaoDeCompra itemRequisicao : selecionado.getItens()) {
            if (!itemRequisicao.isDerivacaoObjetoCompra()) {
                for (ItemRequisicaoCompraExecucao item : itemRequisicao.getItensRequisicaoExecucao()) {
                    if (item.getQuantidade() == null) {
                        ex.adicionarMensagemDeOperacaoNaoPermitida("Deve ser informada a quantidade do item " + item.getItemRequisicaoCompra().getDescricao() + ".");
                    }
                    if (item.getQuantidade() != null && item.getQuantidade().compareTo(BigDecimal.ZERO) < 0) {
                        ex.adicionarMensagemDeOperacaoNaoPermitida("A quantidade informada para o item " + item.getItemRequisicaoCompra().getDescricao() + " deve ser maior que 0 (zero).");
                    }
                    if (!permiteDesdobrarItens()) {
                        if (item.getQuantidade() != null
                            && item.getQuantidadeDisponivel() != null
                            && itemRequisicao.isControleQuantidade()
                            && item.getQuantidade().compareTo(item.getQuantidadeDisponivel()) > 0) {
                            ex.adicionarMensagemDeOperacaoNaoPermitida("A quantidade informada para o item " + item.getItemRequisicaoCompra().getDescricao() + " não pode ser maior que " + Util.formataValorSemCifrao(item.getQuantidadeDisponivel()) + ".");
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
            menorData = selecionado.getDataProcesso();
        }
        if (DataUtil.dataSemHorario(menorData).after(DataUtil.dataSemHorario(selecionado.getDataRequisicao()))) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("A data da requisição " + DataUtil.getDataFormatada(selecionado.getDataRequisicao()) + " não pode ser anterior a data do empenho " + DataUtil.getDataFormatada(menorData) + ".");
        }
    }

    public List<Long> getIdsEmpenhos() {
        List<Long> ids = Lists.newArrayList();
        List<Empenho> empenhos = Lists.newArrayList();

        if (selecionado.isTipoContrato()) {
            execucoesRequisicao.forEach(reqExec -> {
                empenhos.addAll(facade.getExecucaoContratoFacade().buscarEmpenhosExecucao(reqExec.getExecucaoContrato()));
            });
        } else if (selecionado.isTipoExecucaoProcesso()) {
            execucoesRequisicao.forEach(reqExec -> {
                empenhos.addAll(facade.getExecucaoProcessoFacade().buscarEmpenhosExecucao(reqExec.getExecucaoProcesso()));
            });
        } else {
            empenhos.addAll(facade.getReconhecimentoDividaFacade().buscarEmpenhosPorReconhecimentoDivida(selecionado.getReconhecimentoDivida()));
        }
        empenhos.forEach(emp -> {
            ids.add(emp.getId());
        });
        return ids;
    }

    public TipoMascaraUnidadeMedida getMascaraQuantidadeItemDesdobrado() {
        if (selecionado.getTipoObjetoCompra().isMaterialConsumo() && itemRequisicao != null) {
            return itemRequisicao.getUnidadeMedida() != null
                ? itemRequisicao.getUnidadeMedida().getMascaraQuantidade()
                : TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
        }
        return TipoMascaraUnidadeMedida.ZERO_CASA_DECIMAL;
    }

    public TipoMascaraUnidadeMedida getMascaraValorUnitarioItemDesdobrado() {
        if (selecionado.getTipoObjetoCompra().isMaterialConsumo() && itemRequisicao != null) {
            return itemRequisicao.getUnidadeMedida() != null
                ? itemRequisicao.getUnidadeMedida().getMascaraValorUnitario()
                : TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
        }
        return TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
    }

    public void calcularValoresItemRequisicao(ItemRequisicaoDeCompra itemRequisicao) {
        try {
            validarQuantidadeItemRequisicao(itemRequisicao);
            itemRequisicao.calcularValoresItemRequisicaoAndItemExecucao();
        } catch (ValidacaoException ve) {
            itemRequisicao.setQuantidade(itemRequisicao.isDerivacaoObjetoCompra() ? BigDecimal.ZERO : itemRequisicao.getQuantidadeDisponivel());
            itemRequisicao.calcularValoresItemRequisicaoAndItemExecucao();
            FacesUtil.atualizarComponente("Formulario:panelItens");
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarQuantidadeItemRequisicao(ItemRequisicaoDeCompra itemRequisicao) {
        ValidacaoException ve = new ValidacaoException();
        if (itemRequisicao.getQuantidade() != null && itemRequisicao.getQuantidade().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo quantidade deve ser um valor maior que zero(0).");
        }
        ve.lancarException();
        BigDecimal qtdeDisponivel = itemRequisicao.getQuantidadeDisponivel();
        if (itemRequisicao.isDerivacaoObjetoCompra()) {
            ItemRequisicaoCompraDerivacao itemReqDerivado = getItemRequisicaoCompraDerivado(itemRequisicao);
            qtdeDisponivel = itemReqDerivado.getItemRequisicaoOriginal().getQuantidadeDisponivel().subtract(itemReqDerivado.getQuantidadeUtilizadaOutrosItens(itemRequisicao));
        }
        if (itemRequisicao.isControleQuantidade() && itemRequisicao.getQuantidade().compareTo(qtdeDisponivel) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade informada para o item " + itemRequisicao.getDescricao() + " não pode ser maior que " + qtdeDisponivel + ".");
        }
        ve.lancarException();
    }


    public void calcularValoresItemRequisicaoExecucao(ItemRequisicaoCompraExecucao itemReqExec) {
        if (itemReqExec.hasQuantidade() && itemReqExec.hasValorUnitario()) {
            ItemRequisicaoDeCompra itemRequisicao = itemReqExec.getItemRequisicaoCompra();
            try {
                validarQuantidadeItemRequisicaoExecucao(itemReqExec);
                atribuirQuantidadeItemAjusteValor(itemRequisicao, itemReqExec.getQuantidade());
                itemReqExec.calcularValorTotal();
                itemRequisicao.setQuantidade(itemRequisicao.getQuantidadeTotalItemExecucao());
                itemRequisicao.setValorUnitario(itemRequisicao.getValorUnitarioTotalItemExecucao());
                itemRequisicao.calcularValoresItemRequisicaoAndItemExecucao();
            } catch (ValidacaoException ve) {
                itemReqExec.setQuantidade(BigDecimal.ZERO);
                atribuirQuantidadeItemAjusteValor(itemRequisicao, BigDecimal.ZERO);
                itemRequisicao.setQuantidade(itemRequisicao.getQuantidadeTotalItemExecucao());
                itemRequisicao.calcularValoresItemRequisicaoAndItemExecucao();
                FacesUtil.atualizarComponente("Formulario:panelItens");
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        }
    }

    private static void atribuirQuantidadeItemAjusteValor(ItemRequisicaoDeCompra itemRequisicao, BigDecimal itemReqExec) {
        for (ItemRequisicaoCompraExecucao itemReqExAjusteValor : itemRequisicao.getItensRequisicaoExecucao()) {
            if (itemReqExAjusteValor.getAjusteValor()) {
                itemReqExAjusteValor.setQuantidade(itemReqExec);
                itemReqExAjusteValor.calcularValorTotal();
            }
        }
    }

    public void validarQuantidadeItemRequisicaoExecucao(ItemRequisicaoCompraExecucao itemReqExec) {
        ValidacaoException ve = new ValidacaoException();
        if (itemReqExec.getQuantidade().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo quantidade deve ser um valor maior que zero(0).");
        }
        ve.lancarException();
        if (itemReqExec.getItemRequisicaoCompra().isControleQuantidade() && itemReqExec.getQuantidade().compareTo(itemReqExec.getQuantidadeDisponivel()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade informada para o item " + itemReqExec.getItemRequisicaoCompra().getDescricao() + " não pode ser maior que " + itemReqExec.getQuantidadeDisponivel() + ".");
        }
        ve.lancarException();
    }

    public boolean isItemExecucaoAjusteValor(ItemRequisicaoCompraExecucao itemReqExec) {
        if (itemReqExec.getExecucaoContratoItem().getExecucaoContrato().getOrigem().isAditivo()) {
            return facade.getAlteracaoContratualFacade().isMovimentoAjusteValor(itemReqExec.getExecucaoContratoItem().getItemContrato(), itemReqExec.getExecucaoContratoItem().getExecucaoContrato().getIdOrigem());
        }
        if (itemReqExec.getExecucaoContratoItem().getExecucaoContrato().getOrigem().isApostilamento()) {
            return facade.getAlteracaoContratualFacade().isMovimentoApostilamentoReajusteValorPreExecucao(itemReqExec.getExecucaoContratoItem().getItemContrato(), itemReqExec.getExecucaoContratoItem().getExecucaoContrato().getIdOrigem());
        }
        return false;
    }

    public boolean hasItensProcesso() {
        return itensProcesso != null && !itensProcesso.isEmpty();
    }

    public boolean hasMaisDeUmTipoContaDespesa() {
        return !Util.isListNullOrEmpty(tiposContaDespesa) && tiposContaDespesa.size() > 1;
    }

    public boolean hasExecucaoSelecionada() {
        if (hasExecucoes()) {
            for (RequisicaoExecucaoExecucaoVO requisicaoExecucaoExecucaoVO : execucoesRequisicao) {
                if (requisicaoExecucaoExecucaoVO.getSelecionado()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasItemRequisicaoSelecionado() {
        return selecionado.getItens().stream().anyMatch(ItemRequisicaoDeCompra::hasQuantidade);
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
        selecionado.getItens().clear();
        itensProcesso = Lists.newArrayList();
        itensDesdobraveis = Lists.newArrayList();
        itensDerivacao = Lists.newArrayList();
    }

    public void limparListaItensRequisicaoExecucao() {
        for (ItemRequisicaoDeCompra itemRequisicao : itemDesdobravel.getItensRequisicao()) {
            itemRequisicao.getItensRequisicaoExecucao().clear();
        }
    }

    public void separarItensPorTipoContaDespesaEmpenho() {
        if (selecionado.getTipoObjetoCompra().isPermanenteOrConsumo() && hasMaisDeUmTipoContaDespesa()) {
            selecionado.setItens(Lists.newArrayList());
            List<ItemProcessoLicitacao> itensSeparados = Lists.newArrayList();
            itensProcesso.stream()
                .filter(item -> item.getTipoContaDespesa().equals(selecionado.getTipoContaDespesa()))
                .forEach(itensSeparados::add);

            criarItensRequisicaoCompraSemDesdobrar(itensSeparados);
            Collections.sort(selecionado.getItens());
        }
    }

    public void carregarItens() {
        try {
            validarCamposObrigatorio();
            validarRequisicaoExecucao();
            buscarTiposContaDespesaEmpenho();
            buscarItensProcesso();
            if (permiteDesdobrarItens()) {
                preencherItemDesdobravel();
                indiceAba = 1;
                hasPermissaoDesdobrarSemGrupo = facade.getConfiguracaoLicitacaoFacade().verificarUnidadeGrupoObjetoCompra(getUnidadeAdministrativa());
                FacesUtil.atualizarComponente("Formulario:panelItens");
            } else {
                selecionado.setItens(Lists.<ItemRequisicaoDeCompra>newArrayList());
                itensDesdobraveis = Lists.newArrayList();
                criarItensRequisicaoCompraSemDesdobrar(itensProcesso);
                validarListaItensVazia();
            }
            Collections.sort(selecionado.getItens());
            FacesUtil.executaJavaScript("aguarde.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void buscarTiposContaDespesaEmpenho() {
        Set<TipoContaDespesa> tiposContaDespesaSet = Sets.newHashSet();

        selecionado.setTipoContaDespesa(null);
        if (selecionado.getTipoRequisicao().isReconhecimentoDivida()) {
            tiposContaDespesaSet.addAll(facade.getEmpenhoFacade().buscarTiposContaDespesaExecucaoEmpenho(selecionado.getReconhecimentoDivida().getId()));
        } else {
            execucoesRequisicao.stream()
                .filter(RequisicaoExecucaoExecucaoVO::getSelecionado)
                .forEach(exReq -> tiposContaDespesaSet.addAll(facade.getEmpenhoFacade().buscarTiposContaDespesaExecucaoEmpenho(getIdProcesso(exReq))));
        }
        List<TipoContaDespesa> tiposPermititdos = TipoContaDespesa.getTiposContaDespesaPorTipoObjetoCompra(selecionado.getTipoObjetoCompra());
        tiposContaDespesa = Lists.newArrayList(tiposContaDespesaSet);
        tiposContaDespesa.removeIf(tipo -> !tiposPermititdos.contains(tipo));

        if (!Util.isListNullOrEmpty(tiposContaDespesa)) {
            selecionado.setTipoContaDespesa(tiposContaDespesa.get(0));
        }
    }

    public void buscarItensProcesso() {
        itensProcesso = Lists.newArrayList();
        if (selecionado.getTipoRequisicao().isReconhecimentoDivida()) {
            itensProcesso.addAll(facade.buscarItensProcesso(selecionado.getReconhecimentoDivida().getId()));
        } else {
            if (hasExecucoes()) {
                for (RequisicaoExecucaoExecucaoVO reqExec : execucoesRequisicao) {
                    if (reqExec.getSelecionado()) {
                        itensProcesso.addAll(facade.buscarItensProcesso(getIdExecucao(reqExec)));
                    }
                }
            }
        }
    }

    private void buscarItensProcessoVisualizar() {
        itensProcesso = Lists.newArrayList();
        for (RequisicaoCompraExecucao ex : selecionado.getExecucoes()) {
            Long idProcesso;
            if (selecionado.isTipoContrato()) {
                idProcesso = ex.getExecucaoContrato().getId();
            } else if (selecionado.isTipoExecucaoProcesso()) {
                idProcesso = ex.getExecucaoProcesso().getId();
            } else {
                idProcesso = selecionado.getReconhecimentoDivida().getId();
            }
            itensProcesso.addAll(facade.buscarItensProcesso(idProcesso));
        }
    }

    private Long getIdExecucao(RequisicaoExecucaoExecucaoVO reqExec) {
        Long idProcesso;
        if (selecionado.isTipoContrato()) {
            idProcesso = reqExec.getExecucaoContrato().getId();
        } else if (selecionado.isTipoExecucaoProcesso()) {
            idProcesso = reqExec.getExecucaoProcesso().getId();
        } else {
            idProcesso = selecionado.getReconhecimentoDivida().getId();
        }
        return idProcesso;
    }

    private Long getIdProcesso(RequisicaoExecucaoExecucaoVO reqExec) {
        Long idProcesso;
        if (selecionado.isTipoContrato()) {
            idProcesso = reqExec.getExecucaoContrato().getId();
        } else if (selecionado.isTipoExecucaoProcesso()) {
            idProcesso = reqExec.getExecucaoProcesso().getId();
        } else {
            idProcesso = selecionado.getReconhecimentoDivida().getId();
        }
        return idProcesso;
    }

    public void preencherItemDesdobravel() {
        if (permiteDesdobrarItens()) {
            itensDesdobraveis = Lists.newArrayList();
            for (ItemProcessoLicitacao item : itensProcesso) {
                if (item.getObjetoCompra().getTipoObjetoCompra().equals(selecionado.getTipoObjetoCompra())) {
                    ItemRequisicaoCompraDesdobravel itemDesdobravel;
                    if (selecionado.isTipoContrato()) {
                        itemDesdobravel = new ItemRequisicaoCompraDesdobravel(item.getItemExecucaoContrato());
                    } else {
                        itemDesdobravel = new ItemRequisicaoCompraDesdobravel(item.getItemExecucaoProcesso());
                    }
                    if (!isOperacaoNovo()) {
                        atribuirItensRequisicaoAoItemDesdobravelReferente(itemDesdobravel);
                    }
                    itemDesdobravel.setValorDisponivel(getValorDisponivelProcessoItemDesdobrado(itemDesdobravel));
                    Util.adicionarObjetoEmLista(itensDesdobraveis, itemDesdobravel);
                }
            }
            itensDesdobraveis.stream().map(ItemRequisicaoCompraDesdobravel::getItensRequisicao).forEach(Collections::sort);
        }
    }

    private void atribuirItensRequisicaoAoItemDesdobravelReferente(ItemRequisicaoCompraDesdobravel itemDesdobravel) {
        for (ItemRequisicaoDeCompra itemRequisicao : selecionado.getItens()) {
            if (selecionado.isTipoContrato()) {
                if (itemRequisicao.getItemContrato().equals(itemDesdobravel.getExecucaoContratoItem().getItemContrato())) {
                    itemDesdobravel.getItensRequisicao().add(itemRequisicao);
                }
            } else {
                if (itemRequisicao.getExecucaoProcessoItem().equals(itemDesdobravel.getExecucaoProcessoItem())) {
                    itemDesdobravel.getItensRequisicao().add(itemRequisicao);
                }
            }
        }
    }

    public void criarItensRequisicaoCompraSemDesdobrar(List<ItemProcessoLicitacao> itensProcesso) {
        if (selecionado.isTipoContrato()) {
            Map<ItemContrato, List<ExecucaoContratoItem>> map = criarMapItemContratoItemExecucao(itensProcesso);
            for (Map.Entry<ItemContrato, List<ExecucaoContratoItem>> entry : map.entrySet()) {
                ItemRequisicaoDeCompra novoItemReq = criarItemRequisicaoCompraPorContrato(entry.getKey(), BigDecimal.ZERO);

                BigDecimal quantidadeTotal = BigDecimal.ZERO;
                for (ExecucaoContratoItem itemExecucao : entry.getValue()) {
                    if (itemExecucao.getItemContrato().getItemAdequado().getObjetoCompra().getTipoObjetoCompra().equals(selecionado.getTipoObjetoCompra())) {
                        ItemRequisicaoCompraExecucao novoItemReqExecucao = criarItemRequisicaoCompraExecucao(itemExecucao, novoItemReq);
                        Util.adicionarObjetoEmLista(novoItemReq.getItensRequisicaoExecucao(), novoItemReqExecucao);
                        quantidadeTotal = quantidadeTotal.add(itemExecucao.getQuantidadeUtilizada());
                    }
                }
                if (!novoItemReq.hasMaisDeUmItemRequisicaoExecucao()) {
                    ItemRequisicaoCompraExecucao itemReqExecucao = novoItemReq.getItensRequisicaoExecucao().get(0);
                    novoItemReq.setValorUnitario(itemReqExecucao.getValorUnitario());
                }
                novoItemReq.setQuantidadeDisponivel(novoItemReq.getQuantidadeDisponivelItemExecucao());
                Util.adicionarObjetoEmLista(selecionado.getItens(), novoItemReq);
            }
        } else if (selecionado.isTipoExecucaoProcesso()) {
            for (ItemProcessoLicitacao item : itensProcesso) {
                ExecucaoProcessoItem itemExecProc = item.getItemExecucaoProcesso();
                if (itemExecProc.getObjetoCompra().getTipoObjetoCompra().equals(selecionado.getTipoObjetoCompra())) {
                    ItemRequisicaoDeCompra novoItemReq = criarItemRequisicaoCompraPorExecucaoProcesso(itemExecProc, itemExecProc.getValorUnitario());

                    ItemRequisicaoDeCompra itemReqJaAdicionado = getItemRequisicaoJaAdicionado(itemExecProc);
                    if (itemReqJaAdicionado != null) {
                        itemReqJaAdicionado.setQuantidadeDisponivel(itemReqJaAdicionado.getQuantidadeDisponivel().add(novoItemReq.getQuantidadeDisponivel()));
                        novoItemReq = itemReqJaAdicionado;
                    }
                    Util.adicionarObjetoEmLista(selecionado.getItens(), novoItemReq);
                }
            }
        } else {
            for (ItemProcessoLicitacao item : itensProcesso) {
                ItemReconhecimentoDivida itemRecDivida = item.getItemReconhecimentoDivida();
                if (itemRecDivida.getObjetoCompra().getTipoObjetoCompra().equals(selecionado.getTipoObjetoCompra())) {
                    ItemRequisicaoDeCompra itemRequisicao = criarItemRequisicaoCompraReconhecimentoDivida(itemRecDivida);
                    Util.adicionarObjetoEmLista(selecionado.getItens(), itemRequisicao);
                }
            }
        }
    }

    private ItemRequisicaoDeCompra getItemRequisicaoJaAdicionado(ExecucaoProcessoItem itemExecProc) {
        for (ItemRequisicaoDeCompra itemReqAdicionado : selecionado.getItens()) {
            if (itemReqAdicionado.getExecucaoProcessoItem().getItemProcessoCompra().equals(itemExecProc.getItemProcessoCompra())) {
                return itemReqAdicionado;
            }
        }
        return null;

    }

    public Map<ItemContrato, List<ExecucaoContratoItem>> criarMapItemContratoItemExecucao(List<ItemProcessoLicitacao> itensProcesso) {
        Map<ItemContrato, List<ExecucaoContratoItem>> map = new HashMap<>();
        for (ItemProcessoLicitacao item : itensProcesso) {
            ExecucaoContratoItem itemExecucao = item.getItemExecucaoContrato();
            if (itemExecucao.getItemContrato().getItemAdequado().getObjetoCompra().getTipoObjetoCompra().equals(selecionado.getTipoObjetoCompra())) {
                if (!map.containsKey(itemExecucao.getItemContrato())) {
                    map.put(itemExecucao.getItemContrato(), Lists.<ExecucaoContratoItem>newArrayList(itemExecucao));
                } else {
                    List<ExecucaoContratoItem> itensMap = map.get(itemExecucao.getItemContrato());
                    itensMap.add(itemExecucao);
                    map.put(itemExecucao.getItemContrato(), itensMap);
                }
            }
        }
        return map;
    }

    public boolean permiteDesdobrarItens() {
        return isMaiorDesconto() || isControlePorValor();
    }

    public String getTituloTabelaItensDesdobrado() {
        return isMaiorDesconto() ? "Itens Maior Desconto" : "Itens Controle por Valor";
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
        FacesUtil.atualizarComponente("Formulario:tabView:valorTotalDesdobrado");
        FacesUtil.atualizarComponente("Formulario:tabView:gridAplicacaoDesconto");
        FacesUtil.atualizarComponente("Formulario:tabView:valorDesconto");
        FacesUtil.atualizarComponente("Formulario:tabView:valorDescontoTotal");
        FacesUtil.atualizarComponente("Formulario:tabView:panelEntradaCompra");
        FacesUtil.atualizarComponente("Formulario:tabView:panelDescontoNota");
        FacesUtil.atualizarComponente("Formulario:tabView:msgDiferencaoEntrada");
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

    private void validarListaItensVazia() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.hasItens()) {
            ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "A requisição não carregou os itens para o tipo de objeto de compra: " + selecionado.getTipoObjetoCompra().getDescricao() + ".");
        }
        ve.lancarException();
    }

    public ItemRequisicaoDeCompra criarItemRequisicaoCompraReconhecimentoDivida(ItemReconhecimentoDivida item) {
        ItemRequisicaoDeCompra itemRequisicaoDeCompra = new ItemRequisicaoDeCompra();
        itemRequisicaoDeCompra.setRequisicaoDeCompra(selecionado);
        itemRequisicaoDeCompra.setItemReconhecimentoDivida(item);
        itemRequisicaoDeCompra.setNumero(getProximoNumeroItemRequisicao());
        itemRequisicaoDeCompra.setObjetoCompra(item.getObjetoCompra());
        itemRequisicaoDeCompra.setUnidadeMedida(item.getUnidadeMedida());
        itemRequisicaoDeCompra.setValorUnitario(item.getValorUnitario());
        itemRequisicaoDeCompra.setQuantidadeDisponivel(getQuantidadeDisponivelProcessoReconhecimentoDivida(itemRequisicaoDeCompra));
        itemRequisicaoDeCompra.setQuantidade(itemRequisicaoDeCompra.getQuantidadeDisponivel());
        itemRequisicaoDeCompra.calcularValorTotal();
        return itemRequisicaoDeCompra;
    }

    public ItemRequisicaoDeCompra criarItemRequisicaoCompraPorExecucaoProcesso(ExecucaoProcessoItem itemExecProc, BigDecimal valorUnitario) {
        ItemRequisicaoDeCompra novoItemReq = new ItemRequisicaoDeCompra();
        novoItemReq.setRequisicaoDeCompra(selecionado);
        novoItemReq.setExecucaoProcessoItem(itemExecProc);
        novoItemReq.setNumero(itemExecProc.getItemProcessoCompra().getNumero());
        if (!permiteDesdobrarItens()) {
            novoItemReq.setObjetoCompra(itemExecProc.getObjetoCompra());
        }
        novoItemReq.setUnidadeMedida(itemExecProc.getItemProcessoCompra().getItemSolicitacaoMaterial().getUnidadeMedida());
        novoItemReq.setDescricaoComplementar(itemExecProc.getItemProcessoCompra().getDescricaoComplementar());
        novoItemReq.setValorUnitario(permiteDesdobrarItens() ? BigDecimal.ZERO : valorUnitario);

        BigDecimal percentualDesconto = selecionado.isTipoAtaRegistroPreco()
            ? facade.getItemPregaoFacade().getPercentualDescontoLanceVencedor(itemExecProc.getItemProcessoCompra())
            : BigDecimal.ZERO;
        novoItemReq.setPercentualDesconto(percentualDesconto);

        if (isOperacaoEditar()) {
            ItemRequisicaoDeCompra itemReqSalvo = facade.getItemRequisicaoPorItemExecucaoProcesso(itemExecProc, selecionado);
            novoItemReq.setQuantidade(itemReqSalvo != null ? itemReqSalvo.getQuantidade() : BigDecimal.ZERO);
            novoItemReq.setValorUnitario(itemReqSalvo != null ? itemReqSalvo.getValorUnitario() : BigDecimal.ZERO);
        }

        novoItemReq.setQuantidadeDisponivel(getQuantidadeDisponivelProcessoAta(itemExecProc, novoItemReq));
        novoItemReq.calcularValorTotal();
        return novoItemReq;
    }

    public ItemRequisicaoDeCompra criarItemRequisicaoCompraPorContrato(ItemContrato itemContrato, BigDecimal valorUnitario) {
        ItemRequisicaoDeCompra novoItemReq = new ItemRequisicaoDeCompra();
        novoItemReq.setRequisicaoDeCompra(selecionado);
        novoItemReq.setItemContrato(itemContrato);
        novoItemReq.setNumero(itemContrato.getItemAdequado().getNumeroItem());
        if (!permiteDesdobrarItens()) {
            novoItemReq.setObjetoCompra(itemContrato.getItemAdequado().getObjetoCompra());
        }
        novoItemReq.setUnidadeMedida(itemContrato.getItemAdequado().getUnidadeMedida());
        novoItemReq.setDescricaoComplementar(itemContrato.getItemAdequado().getDescricaoComplementar());
        novoItemReq.setValorUnitario(permiteDesdobrarItens() ? BigDecimal.ZERO : valorUnitario);

        BigDecimal percentualDesconto = selecionado.getContrato().isProcessoLicitatorio()
            ? facade.getItemPregaoFacade().getPercentualDescontoLanceVencedor(itemContrato.getItemAdequado().getItemProcessoCompra())
            : BigDecimal.ZERO;
        novoItemReq.setPercentualDesconto(percentualDesconto);

        if (isOperacaoEditar()) {
            ItemRequisicaoDeCompra itemReqSalvo = facade.getItemRequisicaoPorItemContrato(itemContrato, selecionado);
            novoItemReq.setQuantidade(itemReqSalvo != null ? itemReqSalvo.getQuantidade() : BigDecimal.ZERO);
            novoItemReq.setValorUnitario(itemReqSalvo != null ? itemReqSalvo.getValorUnitario() : BigDecimal.ZERO);
        }
        novoItemReq.calcularValorTotal();
        return novoItemReq;
    }

    public ItemRequisicaoCompraExecucao criarItemRequisicaoCompraExecucao(ExecucaoContratoItem itemExecucao, ItemRequisicaoDeCompra itemRequisicao) {
        ItemRequisicaoCompraExecucao novoItemReqExecucao = new ItemRequisicaoCompraExecucao();
        novoItemReqExecucao.setExecucaoContratoItem(itemExecucao);
        novoItemReqExecucao.setItemRequisicaoCompra(itemRequisicao);
        novoItemReqExecucao.setValorUnitario(permiteDesdobrarItens() ? itemRequisicao.getValorUnitario() : itemExecucao.getValorUnitario());
        novoItemReqExecucao.setQuantidade(permiteDesdobrarItens() ? itemRequisicao.getQuantidade() : BigDecimal.ZERO);
        novoItemReqExecucao.setAjusteValor(isItemExecucaoAjusteValor(novoItemReqExecucao));

        if (!novoItemReqExecucao.getAjusteValor()) {
            novoItemReqExecucao.setQuantidadeDisponivel(getQuantidadeDisponivelProcessoContrato(itemExecucao, novoItemReqExecucao));
        }
        if (isOperacaoEditar()) {
            ItemRequisicaoCompraExecucao itemExecReqSalvo = facade.getItemRequisicaoExecucaoPorItemExecucao(itemExecucao, selecionado);
            if (itemExecReqSalvo != null) {
                novoItemReqExecucao.setQuantidade(permiteDesdobrarItens() ? itemRequisicao.getQuantidade() : itemExecReqSalvo.getQuantidade());
                if (!novoItemReqExecucao.getAjusteValor()) {
                    novoItemReqExecucao.setQuantidadeDisponivel(getQuantidadeDisponivelProcessoContrato(itemExecucao, itemExecReqSalvo));
                }
            }
        }

        novoItemReqExecucao.calcularValorTotal();
        return novoItemReqExecucao;
    }

    private int getProximoNumeroItemRequisicao() {
        return selecionado.getItens().size() + 1;
    }

    public void desdobrarItem(ItemRequisicaoCompraDesdobravel item) {
        itemDesdobravel = item;
        novoItemRequisicaoCompraDesdobravel();
    }

    public void novoItemRequisicaoCompraDesdobravel() {
        if (selecionado.isTipoContrato()) {
            ExecucaoContratoItem itemExecucaoContrato = itemDesdobravel.getExecucaoContratoItem();
            itemRequisicao = criarItemRequisicaoCompraPorContrato(itemExecucaoContrato.getItemContrato(), itemExecucaoContrato.getValorUnitario());
        }
        if (selecionado.isTipoExecucaoProcesso()) {
            ExecucaoProcessoItem itemExecucao = itemDesdobravel.getExecucaoProcessoItem();
            itemRequisicao = criarItemRequisicaoCompraPorExecucaoProcesso(itemExecucao, itemExecucao.getValorUnitario());
        }
        itemRequisicao.setNumero(getProximoNumeroItemRequisicao());
        itemRequisicao.setValorUnitarioDesdobrado(itemDesdobravel.getTipoControle().isTipoControlePorQuantidade() ? itemDesdobravel.getValorUnitario() : BigDecimal.ZERO);
        itemDesdobravel.setValorDisponivel(getValorDisponivelProcessoItemDesdobrado(itemDesdobravel));
        atribuirGrupoObjetoCompra();
    }

    private void atribuirGrupoObjetoCompra() {
        try {
            itemDesdobravel.setObjetoCompra(facade.getObjetoCompraFacade().recuperar(itemDesdobravel.getObjetoCompra().getId()));
            ObjetoCompra objetoCompraPai = itemDesdobravel.getObjetoCompra();
            objetoCompraPai.setGrupoContaDespesa(facade.getObjetoCompraFacade().criarGrupoContaDespesa(selecionado.getTipoObjetoCompra(), objetoCompraPai.getGrupoObjetoCompra()));
            indiceAba = 2;
        } catch (ExcecaoNegocioGenerica e) {
            indiceAba = 1;
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private BigDecimal getQuantidadeDisponivelProcessoAta(ExecucaoProcessoItem exItemProc, ItemRequisicaoDeCompra
        itemRequisicao) {
        BigDecimal quantidadeOutrasRequisicoes = facade.getQuantidadeEmRequisicaoItemExecucaoProcesso(exItemProc, itemRequisicao);
        BigDecimal qtdeEstornadaExec = facade.getExecucaoProcessoEstornoFacade().getQuantidadeEstornada(exItemProc);
        return exItemProc.getQuantidade().subtract(qtdeEstornadaExec).subtract(quantidadeOutrasRequisicoes);
    }

    private BigDecimal getQuantidadeDisponivelProcessoContrato(ExecucaoContratoItem itemExecucao, ItemRequisicaoCompraExecucao itemRequisicaoExecucao) {
        BigDecimal quantidadeUtilizada = facade.getQuantidadeUtilizadaItemExecucaoContrato(itemExecucao, itemRequisicaoExecucao);
        BigDecimal qtdeEstornadaExec = facade.getExecucaoContratoEstornoFacade().getQuantidadeEstornada(itemExecucao);
        return itemExecucao.getQuantidadeUtilizada().subtract(qtdeEstornadaExec).subtract(quantidadeUtilizada);
    }

    public BigDecimal getQuantidadeDisponivelProcessoReconhecimentoDivida(ItemRequisicaoDeCompra itemRequisicao) {
        BigDecimal quantidadeUtilizada = facade.getQuantidadeUtilizadaItemReconhecimentoDivida(itemRequisicao.getItemReconhecimentoDivida(), itemRequisicao);
        return itemRequisicao.getItemReconhecimentoDivida().getQuantidade().subtract(quantidadeUtilizada);
    }

    private BigDecimal getValorDisponivelProcessoItemDesdobrado(ItemRequisicaoCompraDesdobravel itemDesdobravel) {
        if (selecionado.isTipoContrato()) {
            ExecucaoContratoItem itemExec = itemDesdobravel.getExecucaoContratoItem();

            BigDecimal valorOutrasRequisicoes = facade.getValorUtilizadoItemExecucaoContrato(itemExec, getIdsItemRequisicaoDesdobrados(itemDesdobravel));
            BigDecimal valorDestaRequisicao = itemDesdobravel.getValorTotalItemDesdobravel();
            itemDesdobravel.setValorUtilizado(valorOutrasRequisicoes.add(valorDestaRequisicao));

            itemDesdobravel.setValorEstornadoExecucao(facade.getExecucaoContratoEstornoFacade().getValorEstornado(itemExec));
            return itemExec.getValorTotal().subtract(itemDesdobravel.getValorEstornado()).subtract(itemDesdobravel.getValorUtilizado());
        }
        ExecucaoProcessoItem itemExecProc = itemDesdobravel.getExecucaoProcessoItem();

        BigDecimal valorOutrasRequisicoes = facade.getValorUtilizadoItemExecucaoProcesso(itemExecProc, getIdsItemRequisicaoDesdobrados(itemDesdobravel));
        BigDecimal valorDestaRequisicao = itemDesdobravel.getValorTotalItemDesdobravel();
        itemDesdobravel.setValorUtilizado(valorOutrasRequisicoes.add(valorDestaRequisicao));
        itemDesdobravel.setValorEstornadoExecucao(facade.getExecucaoProcessoEstornoFacade().getValorEstornado(itemExecProc));
        return itemExecProc.getValorTotal().subtract(itemDesdobravel.getValorEstornado()).subtract(itemDesdobravel.getValorUtilizado());
    }

    public List<Long> getIdsItemRequisicaoDesdobrados(ItemRequisicaoCompraDesdobravel itemDesdobrado) {
        List<Long> ids = Lists.newArrayList();
        itemDesdobrado.getItensRequisicao().forEach(item -> {
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
            if (itemDesdobravel.getObjetoCompra().getGrupoContaDespesa() != null && itemDesdobravel.getObjetoCompra().getGrupoContaDespesa().getIdGrupo() != null) {
                return facade.getObjetoCompraFacade().buscarObjetoCompraConsumoPorGrupoMaterial(
                        codigoOrDescricao.trim(),
                        itemDesdobravel.getObjetoCompra().getGrupoContaDespesa().getIdGrupo(),
                        selecionado.getDataRequisicao());
            }
        }
        if (selecionado.getTipoObjetoCompra().isMaterialPermanente()) {
            if (hasPermissaoDesdobrarSemGrupo) {
                return facade.getObjetoCompraFacade().buscarObjetoCompraNaoReferenciaPorTipoObjetoCompra(codigoOrDescricao.trim(), TipoObjetoCompra.PERMANENTE_MOVEL);
            }
            if (itemDesdobravel.getObjetoCompra().getGrupoContaDespesa() != null && itemDesdobravel.getObjetoCompra().getGrupoContaDespesa().getIdGrupo() != null) {
                return facade.getObjetoCompraFacade().buscarObjetoCompraPermanentePorGrupoPatrimonial(
                        codigoOrDescricao.trim(),
                        itemDesdobravel.getObjetoCompra().getGrupoContaDespesa().getIdGrupo(),
                        selecionado.getDataRequisicao());
            }
        }
        return Lists.newArrayList();
    }

    public UnidadeOrganizacional getUnidadeAdministrativa() {
        if (selecionado.isTipoContrato()) {
            return facade.getContratoFacade().buscarUnidadeVigenteContrato(selecionado.getContrato()).getUnidadeAdministrativa();
        } else if (selecionado.isTipoAtaRegistroPreco()) {
            return ataRegistroPreco.getUnidadeOrganizacional();
        } else if (selecionado.isTipoDispensaInexigibilidade()) {
            return dispensaDeLicitacao.getProcessoDeCompra().getUnidadeOrganizacional();
        }
        return selecionado.getReconhecimentoDivida().getUnidadeAdministrativa();
    }

    public boolean isMaiorDescontoPorQuantidade() {
        if (isMaiorDesconto() && hasItensDesdobraveis()) {
            for (ItemRequisicaoCompraDesdobravel item : itensDesdobraveis) {
                return item.getTipoControle().isTipoControlePorQuantidade();
            }
        }
        return false;
    }

    public boolean hasItensDesdobraveis(){
        return !Util.isListNullOrEmpty(itensDesdobraveis);
    }

    public boolean isMaiorDesconto() {
        return selecionado.isLicitacaoMaiorDesconto();
    }

    public boolean isControlePorValor() {
        if (hasItensProcesso()) {
            for (ItemProcessoLicitacao item : itensProcesso) {
                if (item.isControleValor()) {
                    return true;
                }
            }
        }
        return false;
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
            itemRequisicao.setObjetoCompra(itemDesdobravel.getObjetoCompra());
            listenerObjetoCompra();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCopiaObjetoCompraDesdobrado() {
        ValidacaoException ve = new ValidacaoException();
        if (itemDesdobravel.getObjetoCompra().getReferencial()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido adicionar um objeto de compra do tipo referencial.");
        }
        if (!itemDesdobravel.getObjetoCompra().getAtivo()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido adicionar um objeto de compra inativo.");
        }
        ve.lancarException();
    }

    private void atribuirGrupoAoItemRequisicao() {
        itemRequisicao.getObjetoCompra().setGrupoContaDespesa(facade.getObjetoCompraFacade().criarGrupoContaDespesa(selecionado.getTipoObjetoCompra(), itemRequisicao.getObjetoCompra().getGrupoObjetoCompra()));
    }

    public void removerItemDesdobravel(ItemRequisicaoDeCompra itemRequisicao) {
        selecionado.getItens().remove(itemRequisicao);
        itemDesdobravel.setValorDisponivel(itemDesdobravel.getValorDisponivel().add(itemRequisicao.getValorTotal()));
        itemDesdobravel.getItensRequisicao().remove(itemRequisicao);
        renumerarItensDesdobraveis();
        indiceAba = 2;
    }

    public void editarItemDesdobrado(ItemRequisicaoDeCompra item) {
        itemRequisicao = (ItemRequisicaoDeCompra) Util.clonarObjeto(item);
        itemDesdobravel.setValorDisponivel(itemDesdobravel.getValorDisponivel().add(item.getValorTotal()));
        atribuirGrupoAoItemRequisicao();
        itemDesdobravel.setEditando(true);
        gerarTabelaValorDescontoItem();

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

        indiceAba = 2;
    }

    public void adicionarItemDesdobravel() {
        try {
            validarItensDesdobraveis();
            validarEspecificacao();
            Util.adicionarObjetoEmLista(itemDesdobravel.getItensRequisicao(), itemRequisicao);
            limparListaItensRequisicaoExecucao();
            for (ItemRequisicaoDeCompra itemRequisicao : itemDesdobravel.getItensRequisicao()) {
                if (selecionado.isTipoContrato()) {
                    ItemRequisicaoCompraExecucao itemReqExecucao = criarItemRequisicaoCompraExecucao(itemDesdobravel.getExecucaoContratoItem(), itemRequisicao);
                    Util.adicionarObjetoEmLista(itemRequisicao.getItensRequisicaoExecucao(), itemReqExecucao);
                }
                if (!itemRequisicao.isControleQuantidade()) {
                    itemRequisicao.setQuantidadeDisponivel(itemRequisicao.getQuantidade());
                }
                Util.adicionarObjetoEmLista(selecionado.getItens(), itemRequisicao);
            }
            novoItemRequisicaoCompraDesdobravel();
            itemDesdobravel.setEditando(false);
            descontosItemRequisicao = Lists.newArrayList();
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabView:objetoDeCompra_input')");
            FacesUtil.atualizarComponente("Formulario:tabView:formulario-item");
            FacesUtil.atualizarComponente("Formulario:gridGeral");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarItemDesdobravel() {
        itemRequisicao = null;
        indiceAba = 1;
        itemDesdobravel.setEditando(false);
        descontosItemRequisicao = Lists.newArrayList();
    }

    public void renumerarItensDesdobraveis() {
        Iterator<ItemRequisicaoDeCompra> itens = selecionado.getItens().iterator();
        while (itens.hasNext()) {
            ItemRequisicaoDeCompra proximo = itens.next();
            int i = selecionado.getItens().indexOf(proximo);
            proximo.setNumero(i + 1);
            Util.adicionarObjetoEmLista(selecionado.getItens(), proximo);
        }
    }

    public void validarItensDesdobraveis() {
        ValidacaoException ex = new ValidacaoException();
        validarCamposObrigatoriosItemDesdobravel(ex);
        validarAssociacaoComObjetoCompraItemDesdobravel(ex);
        validarMesmoObjetoCompraItemDesdobravel(ex);
        ex.lancarException();
    }

    private void validarMesmoObjetoCompraItemDesdobravel(ValidacaoException ex) {
        for (ItemRequisicaoDeCompra item : selecionado.getItens()) {
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
            AssociacaoGrupoObjetoCompraGrupoMaterial associacao = facade.getAssocicaoGrupoMaterial().buscarAssociacaoPorGrupoObjetoCompraVigente(
                objetoCompra.getGrupoObjetoCompra(),
                facade.getSistemaFacade().getDataOperacao());
            if (associacao == null) {
                ex.adicionarMensagemDeOperacaoNaoPermitida("Nenhuma associação encontrada com grupo de objeto de compra: " + objetoCompra.getGrupoObjetoCompra() + ".");
            }
        }
        if (objetoCompra.getTipoObjetoCompra().isMaterialPermanente()) {
            GrupoObjetoCompraGrupoBem associacao = facade.getAssocicaoGrupoBem().recuperarAssociacaoDoGrupoObjetoCompra(objetoCompra.getGrupoObjetoCompra(), facade.getSistemaFacade().getDataOperacao());
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
        if (itemRequisicao.getValorTotal().compareTo(itemDesdobravel.getValorDisponivel()) > 0) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("O valor total deve ser menor que o valor disponível de " + Util.formataValor(itemDesdobravel.getValorDisponivel()) + ".");
        }
        if (selecionado.getTipoObjetoCompra().isMaterialConsumo() && itemRequisicao.hasDiferencaRequisicaoComEntrada()) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("Divergência com Entrada por Compra", "O valor total da simulação da entrada por compra deve ser igual ao valor total com desconto do item requisição.");
        }
    }

    public void validarRequisicaoExecucao() {
        ValidacaoException ve = new ValidacaoException();
        if ((selecionado.isTipoContrato() || selecionado.isTipoExecucaoProcesso()) && !hasExecucaoSelecionada()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar, é necessário selecionar pelo menos uma execução a requisição de compra.");
        }
        ve.lancarException();
    }

    public void selecionarContrato(Contrato obj) {
        if (obj != null) {
            selecionado.setContrato(obj);
            executarDependenciasAoSelecionarProcesso();
        }
    }

    public void executarDependenciasAoSelecionarProcesso() {
        try {
            registrarPrazoEstipuladoDeEntrega();
            execucoesRequisicao = Lists.newArrayList();
            validarContratoObras();


            buscarExecucoesEmpenhadas();
            hasPermissaoDesdobrarSemGrupo = facade.getConfiguracaoLicitacaoFacade().verificarUnidadeGrupoObjetoCompra(getUnidadeAdministrativa());
            if (execucoesRequisicao.size() == 1) {
                marcarExecucao(execucoesRequisicao.get(0));
                FacesUtil.executaJavaScript("carregarItens()");
            }
            selecionado.getItens().clear();
            if (selecionado.isTipoContrato()) {
                fiscais = facade.getContratoFacade().buscarFiscaisContrato(selecionado.getContrato().getId());
                gestores = facade.getContratoFacade().buscarGestoresContrato(selecionado.getContrato().getId());
            }
            FacesUtil.executaJavaScript("aguarde.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void registrarPrazoEstipuladoDeEntrega() {
        if (selecionado.getContrato() != null && selecionado.getContrato().getObjetoAdequado().getSolicitacaoMaterial() != null) {
            selecionado.setPrazoEntrega(selecionado.getContrato().getObjetoAdequado().getSolicitacaoMaterial().getPrazoDeEntrega());
            selecionado.setTipoPrazoEntrega(selecionado.getContrato().getObjetoAdequado().getSolicitacaoMaterial().getTipoPrazoEntrega());

        } else if (selecionado.isTipoAtaRegistroPreco() && ataRegistroPreco != null) {
            selecionado.setPrazoEntrega(ataRegistroPreco.getLicitacao().getProcessoDeCompra().getSolicitacaoMaterial().getPrazoDeEntrega());
            selecionado.setTipoPrazoEntrega(ataRegistroPreco.getLicitacao().getProcessoDeCompra().getSolicitacaoMaterial().getTipoPrazoEntrega());

        } else if (selecionado.isTipoDispensaInexigibilidade() && dispensaDeLicitacao != null) {
            selecionado.setPrazoEntrega(dispensaDeLicitacao.getProcessoDeCompra().getSolicitacaoMaterial().getPrazoDeEntrega());
            selecionado.setTipoPrazoEntrega(dispensaDeLicitacao.getProcessoDeCompra().getSolicitacaoMaterial().getTipoPrazoEntrega());
        }
    }

    public boolean hasExecucoes() {
        return execucoesRequisicao != null && !execucoesRequisicao.isEmpty();
    }

    public void buscarExecucoesEmpenhadas() {
        if (!selecionado.getTipoRequisicao().isReconhecimentoDivida()) {
            Long idProcesso = selecionado.isTipoContrato() ? selecionado.getContrato().getId()
                : selecionado.isTipoAtaRegistroPreco() ? ataRegistroPreco.getId() : dispensaDeLicitacao.getId();
            execucoesRequisicao = facade.buscarExecucoesEmpenhadas(idProcesso);
        }
    }

    public void selecionarRequisicaoExecucaoSalva() {
        if (isOperacaoEditar() && !selecionado.getTipoRequisicao().isReconhecimentoDivida()) {
            execucoesRequisicao.forEach(execDisponivel -> selecionado.getExecucoes().forEach(execSalva -> {
                if (selecionado.isTipoContrato() && execSalva.getExecucaoContrato().getId().equals(execDisponivel.getExecucaoContrato().getId())) {
                    execDisponivel.setSelecionado(true);
                }
                if (selecionado.isTipoExecucaoProcesso() && execSalva.getExecucaoProcesso().getId().equals(execDisponivel.getExecucaoProcesso().getId())) {
                    execDisponivel.setSelecionado(true);
                }
            }));
        }
    }

    public List<SelectItem> getTiposRequisicao() {
        return Util.getListSelectItem(Arrays.asList(TipoRequisicaoCompra.values()));
    }

    public List<SelectItem> getTiposPrazo() {
        return Util.getListSelectItemSemCampoVazio(TipoPrazo.values());
    }

    public void limparCamposProcesso() {
        selecionado.setContrato(null);
        selecionado.setReconhecimentoDivida(null);
        setAtaRegistroPreco(null);
        setDispensaDeLicitacao(null);
        execucoesRequisicao = null;
        selecionado.getItens().clear();
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
        if (selecionado.isTipoContrato() && selecionado.getContrato().getObjetoAdequado().isObras()) {
            selecionado.setContrato(facade.getContratoFacade().recuperar(selecionado.getContrato().getId()));
            if (selecionado.getContrato().isDeRegistroDePrecoExterno()) {
                selecionado.getContrato().getRegistroSolicitacaoMaterialExterno().setSolicitacaoMaterialExterno(facade.getSolicitacaoMaterialExternoFacade().recuperar(selecionado.getContrato().getRegistroSolicitacaoMaterialExterno().getSolicitacaoMaterialExterno().getId()));
            }
            if (!SubTipoObjetoCompra.CONSTRUCAO.equals(selecionado.getContrato().getObjetoAdequado().getSubTipoObjetoCompra())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Somente contratos de Obra e Serviço de Engenharia do tipo da obra: Construção poderão realizar a requisição de compra.");
            }
            ve.lancarException();
            BigDecimal valorTotalEmpenhadoDaObra = facade.valorTotalEmpenhadoDaObraPorContrato(selecionado.getContrato());
            if (valorTotalEmpenhadoDaObra.compareTo(selecionado.getContrato().getValorTotalExecucao()) < 0 ||
                valorTotalEmpenhadoDaObra.compareTo(selecionado.getContrato().getValorTotalExecucao()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Obra não foi totalmente empenhada, por favor empenhe todas as medições antes de fazer a requisição de compra");
            }
        }
        if (ve.temMensagens()) {
            selecionado.setContrato(null);
            setAtaRegistroPreco(null);
            setDispensaDeLicitacao(null);
            execucoesRequisicao = null;
            selecionado.getItens().clear();
            throw ve;
        }
    }

    public void gerarRelatorioRequisicao(Long idRequisicao) {
        selecionado = facade.recuperarSemDependencias(idRequisicao);
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
            filtroParte.trim(),
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
        return facade.getContratoFacade().buscarFiltrandoOndeUsuarioGestorLicitacao(
            parte.trim(),
            null,
            selecionado.getTipoObjetoCompra(),
            50);
    }

    public List<ReconhecimentoDivida> completarReconhecimentoDivida(String parte) {
        return facade.getReconhecimentoDividaFacade().buscarReconhecimentoDividaSolicitacaoConcluida(parte.trim());
    }

    public void novaPesquisaContratos() {
        contratos = Lists.newArrayList();
        filtroParte = "";
        filtroContrato = new FiltroContratoRequisicaoCompra();
    }

    public void redirecionarParaRequisicaoCompra(RequisicaoDeCompra requisicaoDeCompra) {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + requisicaoDeCompra.getId() + "/");
    }

    public void marcarExecucao(RequisicaoExecucaoExecucaoVO requisicaoExecucaoExecucaoVO) {
        try {
            if (!hasItensProcesso()){
                buscarItensProcesso();
            }
            if (permiteDesdobrarItens() || selecionado.getTipoRequisicao().isAtaRegistroPreco()) {
                for (RequisicaoExecucaoExecucaoVO execucao : execucoesRequisicao) {
                    execucao.setSelecionado(false);
                }
            }
            requisicaoExecucaoExecucaoVO.setSelecionado(true);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void desmarcarExecucao(RequisicaoExecucaoExecucaoVO reqExecucao) {
        reqExecucao.setSelecionado(false);
    }

    public List<Contrato> getContratos() {
        return contratos;
    }

    public void setContratos(List<Contrato> contratos) {
        this.contratos = contratos;
    }

    public String getFiltroParte() {
        return filtroParte;
    }

    public void setFiltroParte(String filtroParte) {
        this.filtroParte = filtroParte;
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

    public ItemRequisicaoDeCompra getItemRequisicao() {
        return itemRequisicao;
    }

    public void setItemRequisicao(ItemRequisicaoDeCompra itemRequisicao) {
        this.itemRequisicao = itemRequisicao;
    }

    public Integer getIndiceAba() {
        return indiceAba;
    }

    public void setIndiceAba(Integer indiceAba) {
        this.indiceAba = indiceAba;
    }

    public List<ItemRequisicaoCompraDesdobravel> getItensDesdobraveis() {
        return itensDesdobraveis;
    }

    public void setItensDesdobraveis(List<ItemRequisicaoCompraDesdobravel> itensDesdobraveis) {
        this.itensDesdobraveis = itensDesdobraveis;
    }

    public ItemRequisicaoCompraDesdobravel getItemDesdobravel() {
        return itemDesdobravel;
    }

    public void setItemDesdobravel(ItemRequisicaoCompraDesdobravel itemDesdobravel) {
        this.itemDesdobravel = itemDesdobravel;
    }

    public List<RequisicaoExecucaoExecucaoVO> getExecucoesRequisicao() {
        return execucoesRequisicao;
    }

    public void setExecucoesRequisicao(List<RequisicaoExecucaoExecucaoVO> execucoesRequisicao) {
        this.execucoesRequisicao = execucoesRequisicao;
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
        String url = "window.open('" + requestContext + "/contrato-adm/ver/" + selecionado.getContrato().getId() + "/', '_blank');";
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
            selecionado.getItens().forEach(itemReqOriginal -> {
                DerivacaoObjetoCompra derivacaoObjComp = facade.buscarDerivacaoObjetoCompraItemRequisicao(itemReqOriginal.getItemContrato(), selecionado);
                if (derivacaoObjComp != null) {
                    ItemRequisicaoCompraDerivacao novoItemDerivacao = novoItemRequisicaoCompraDerivacao(itemReqOriginal);
                    novoItemDerivacao.setDerivacaoObjetoCompra(derivacaoObjComp);
                    itensDerivacao.add(novoItemDerivacao);
                }
            });

            itensDerivacao.forEach(itemDeriv -> {
                List<ItemRequisicaoDeCompra> itensReqComponente = facade.buscarItemRequisicaoDerivacaoComponente(itemDeriv.getItemRequisicaoOriginal().getItemContrato(), selecionado, itemDeriv.getDerivacaoObjetoCompra());
                itensReqComponente.forEach(itemReqComp -> itemReqComp.setDescricaoComplementar(itemReqComp.getItemContrato().getItemAdequado().getDescricaoComplementar()));

                itemDeriv.setItensRequisicaoComponente(itensReqComponente);
                selecionado.getItens().remove(itemDeriv.getItemRequisicaoOriginal());
            });

            itensDerivacao.forEach(itemDeriv -> itemDeriv.setQuantidadDisponivel(itemDeriv.getQuantidadeUtilizada()));
            itensDerivacao.forEach(itemDeriv -> itemDeriv.getItemRequisicaoOriginal().setQuantidadeDisponivel(itemDeriv.getQuantidadeDisponivel()));

            itensDerivacao.forEach(itemDeriv -> {
                itemDeriv.getItensRequisicaoComponente().forEach(itemComp -> {
                    itemComp.setQuantidadeDisponivel(itemDeriv.getQuantidadeDisponivel());
                    itemComp.getItensRequisicaoExecucao().forEach(itemEx -> itemEx.setQuantidadeDisponivel(itemComp.getQuantidadeDisponivel()));
                });
            });
            itensDerivacao.forEach(itemDeriv -> selecionado.getItens().addAll(itemDeriv.getItensRequisicaoComponente()));
        }
        Collections.sort(selecionado.getItens());
    }

    public void preecherItemRequisicaoDerivacaoNovo(ItemRequisicaoDeCompra itemReqList) {
        if (selecionado.getTipoRequisicao().isContrato()) {
            List<DerivacaoObjetoCompraComponente> componentes = facade.getDerivacaoObjetoCompraComponenteFacade().buscarComponentesPorDerivacao(itemReqList.getObjetoCompra().getDerivacaoObjetoCompra());
            if (!Util.isListNullOrEmpty(componentes)) {
                ItemRequisicaoCompraDerivacao novoItemDerivacao = novoItemRequisicaoCompraDerivacao(itemReqList);

                for (DerivacaoObjetoCompraComponente compDeriv : componentes) {
                    novoItemRequisicaoDerivacaoComponente(compDeriv, itemReqList, novoItemDerivacao);
                }
                itensDerivacao.add(novoItemDerivacao);
                selecionado.getItens().addAll(novoItemDerivacao.getItensRequisicaoComponente());
            }
            selecionado.getItens().remove(itemReqList);
        }
        Collections.sort(selecionado.getItens());
    }

    private void novoItemRequisicaoDerivacaoComponente(DerivacaoObjetoCompraComponente compDeriv, ItemRequisicaoDeCompra itemReqParam, ItemRequisicaoCompraDerivacao novoItemDerivacao) {
        ItemRequisicaoDeCompra novoItemReq = new ItemRequisicaoDeCompra();
        novoItemReq.setRequisicaoDeCompra(selecionado);
        novoItemReq.setItemContrato(itemReqParam.getItemContrato());
        novoItemReq.setItemReconhecimentoDivida(itemReqParam.getItemReconhecimentoDivida());
        novoItemReq.setExecucaoProcessoItem(itemReqParam.getExecucaoProcessoItem());
        novoItemReq.setObjetoCompra(itemReqParam.getObjetoCompra());
        novoItemReq.setUnidadeMedida(itemReqParam.getUnidadeMedida());
        novoItemReq.setNumero(itemReqParam.getNumero());
        novoItemReq.setQuantidadeDisponivel(itemReqParam.getQuantidadeDisponivel());
        novoItemReq.setValorUnitario(itemReqParam.getValorUnitario());
        novoItemReq.setDescricaoComplementar(itemReqParam.getDescricaoComplementar());
        novoItemReq.setDerivacaoComponente(compDeriv);

        for (ItemRequisicaoCompraExecucao itemEx : itemReqParam.getItensRequisicaoExecucao()) {
            ItemRequisicaoCompraExecucao novoItemReqEx = new ItemRequisicaoCompraExecucao();
            novoItemReqEx.setExecucaoContratoItem(itemEx.getExecucaoContratoItem());
            novoItemReqEx.setItemRequisicaoCompra(novoItemReq);
            novoItemReqEx.setQuantidade(itemReqParam.getQuantidade());
            novoItemReqEx.setQuantidadeDisponivel(itemEx.getQuantidadeDisponivel());
            novoItemReqEx.setValorUnitario(novoItemReq.getValorUnitario());
            novoItemReqEx.calcularValorTotal();
            novoItemReq.getItensRequisicaoExecucao().add(novoItemReqEx);
        }
        novoItemDerivacao.getItensRequisicaoComponente().add(novoItemReq);
    }

    private ItemRequisicaoCompraDerivacao novoItemRequisicaoCompraDerivacao(ItemRequisicaoDeCompra itemReqParam) {
        ItemRequisicaoCompraDerivacao novoItemDerivacao = new ItemRequisicaoCompraDerivacao();
        novoItemDerivacao.setItemRequisicaoOriginal(itemReqParam);
        novoItemDerivacao.setQuantidadDisponivel(itemReqParam.getQuantidadeDisponivel());
        return novoItemDerivacao;
    }

    public void removerItemRequisicaoDerivado(ItemRequisicaoDeCompra itemReq) {
        ItemRequisicaoCompraDerivacao itemReqDerivado = getItemRequisicaoCompraDerivado(itemReq);
        if (itemReqDerivado == null) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao derivar o item requisição " + itemReq);
            return;
        }
        selecionado.getItens().remove(itemReq);
        itemReqDerivado.getItensRequisicaoComponente().remove(itemReq);

        if (itemReqDerivado.getItensRequisicaoComponente().isEmpty()) {
            itemReqDerivado.getItemRequisicaoOriginal().setQuantidadeDisponivel(itemReqDerivado.getQuantidadeDisponivel());
            itemReqDerivado.getItemRequisicaoOriginal().getItensRequisicaoExecucao().forEach(itemEx -> itemEx.setQuantidadeDisponivel(itemReqDerivado.getQuantidadeDisponivel()));
            selecionado.getItens().add(itemReqDerivado.getItemRequisicaoOriginal());
            itensDerivacao.remove(itemReqDerivado);
        }
        Collections.sort(selecionado.getItens());
    }

    public ItemRequisicaoCompraDerivacao getItemRequisicaoCompraDerivado(ItemRequisicaoDeCompra itemReq) {
        Optional<ItemRequisicaoCompraDerivacao> first = itensDerivacao.stream().filter(itemD -> itemD.getItemRequisicaoOriginal().getIdItemProcesso().equals(itemReq.getIdItemProcesso())).findFirst();
        return first.orElse(null);
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

    public Boolean getHasPermissaoDesdobrarSemGrupo() {
        return hasPermissaoDesdobrarSemGrupo;
    }

    public void setHasPermissaoDesdobrarSemGrupo(Boolean hasPermissaoDesdobrarSemGrupo) {
        this.hasPermissaoDesdobrarSemGrupo = hasPermissaoDesdobrarSemGrupo;
    }

    public void selecionarObjetoCompraConsultaEntidade(Long idObjeto) {
        if (idObjeto != null) {
            itemRequisicao.setObjetoCompra(facade.getObjetoCompraFacade().recuperar(idObjeto));
            atribuirGrupoAoItemRequisicao();

            if (!hasPermissaoDesdobrarSemGrupo
                && !itemDesdobravel.getObjetoCompra().getGrupoContaDespesa().getIdGrupo().equals(itemRequisicao.getObjetoCompra().getGrupoContaDespesa().getIdGrupo())) {
                FacesUtil.addOperacaoNaoPermitida("Não é permitido selecionar um objeto de compra com o grupo diferente do objeto de compra de referência.");
                FacesUtil.addOperacaoNaoPermitida("Grupo de Referência: " + itemDesdobravel.getObjetoCompra().getGrupoContaDespesa().getGrupo());
                FacesUtil.addOperacaoNaoPermitida("Grupo do Objeto Selecionado: " + itemRequisicao.getObjetoCompra().getGrupoContaDespesa().getGrupo());
                itemRequisicao.setObjetoCompra(null);
            }
            if (itemRequisicao.getObjetoCompra() != null) {
                TabelaEspecificacaoObjetoCompraControlador controlador = (TabelaEspecificacaoObjetoCompraControlador) Util.getControladorPeloNome("tabelaEspecificacaoObjetoCompraControlador");
                controlador.recuperarObjetoCompra(itemRequisicao.getObjetoCompra());
            }
            FacesUtil.executaJavaScript("dlgPesquisaObj.hide()");
            FacesUtil.atualizarComponente("Formulario:tabView:formulario-item");
            FacesUtil.atualizarComponente("Formulario:tabView:pn-unidade-medida");
        }
    }

    public String getOrigemContrato() {
        return origemContrato;
    }

    public void setOrigemContrato(String origemContrato) {
        this.origemContrato = origemContrato;
    }
}
