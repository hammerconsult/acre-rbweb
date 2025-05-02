package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ReconhecimentoDividaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.ConverterMascaraUnidadeMedida;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-reconhecimento-divida", pattern = "/reconhecimento-divida/novo/", viewId = "/faces/financeiro/reconhecimento-divida/edita.xhtml"),
    @URLMapping(id = "editar-reconhecimento-divida", pattern = "/reconhecimento-divida/editar/#{reconhecimentoDividaControlador.id}/", viewId = "/faces/financeiro/reconhecimento-divida/edita.xhtml"),
    @URLMapping(id = "listar-reconhecimento-divida", pattern = "/reconhecimento-divida/listar/", viewId = "/faces/financeiro/reconhecimento-divida/lista.xhtml"),
    @URLMapping(id = "ver-reconhecimento-divida", pattern = "/reconhecimento-divida/ver/#{reconhecimentoDividaControlador.id}/", viewId = "/faces/financeiro/reconhecimento-divida/visualizar.xhtml")
})
public class ReconhecimentoDividaControlador extends PrettyControlador<ReconhecimentoDivida> implements Serializable, CRUD {

    @EJB
    private ReconhecimentoDividaFacade facade;
    private ItemReconhecimentoDivida itemReconhecimentoDivida;
    private ItemReconhecimentoDividaDotacao itemReconhecimentoDividaDotacao;
    private PublicacaoReconhecimentoDivida publicacaoReconhecimentoDivida;
    private DoctoHabilitacaoReconhecimentoDivida doctoHabilitacaoReconhecimentoDivida;
    private ObjetoCompra objetoCompra;
    private ObjetoDeCompraEspecificacao objetoDeCompraEspecificacaoVisualizar;
    private ParecerReconhecimentoDivida parecerReconhecimentoDivida;
    private List<Empenho> empenhos;
    private List<EmpenhoEstorno> estornosDeEmpenhos;
    private List<Liquidacao> liquidacoes;
    private List<LiquidacaoEstorno> estornosDeLiquidacoes;
    private List<Pagamento> pagamentos;
    private List<PagamentoEstorno> estornosDePagamentos;
    private ConfiguracaoContabil configuracaoContabil;

    public ReconhecimentoDividaControlador() {
        super(ReconhecimentoDivida.class);
    }

    @URLAction(mappingId = "novo-reconhecimento-divida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataReconhecimento(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUnidadeAdministrativa(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setUnidadeOrcamentaria(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        cancelarItem();
        cancelarDocumento();
        itemReconhecimentoDividaDotacao = null;
        publicacaoReconhecimentoDivida = null;
        buscarExecucoesOrcamentarias();
    }

    @URLAction(mappingId = "ver-reconhecimento-divida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        buscarExecucoesOrcamentarias();
    }

    @URLAction(mappingId = "editar-reconhecimento-divida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        cancelarItem();
        cancelarDocumento();
        itemReconhecimentoDividaDotacao = null;
        publicacaoReconhecimentoDivida = null;
        buscarExecucoesOrcamentarias();
    }

    public List<SituacaoCadastralPessoa> getSituacoesDisponiveis() {
        return Lists.newArrayList(SituacaoCadastralPessoa.ATIVO);
    }

    private void buscarExecucoesOrcamentarias() {
        if (selecionado.getId() == null) {
            empenhos = Lists.newArrayList();
            estornosDeEmpenhos = Lists.newArrayList();
            liquidacoes = Lists.newArrayList();
            estornosDeLiquidacoes = Lists.newArrayList();
            pagamentos = Lists.newArrayList();
            estornosDePagamentos = Lists.newArrayList();
        } else {
            empenhos = facade.buscarEmpenhosPorReconhecimentoDivida(selecionado);
            estornosDeEmpenhos = facade.buscarEstornosDeEmpenhosPorReconhecimentoDivida(selecionado);
            liquidacoes = facade.buscarLiquidacoesPorReconhecimentoDivida(selecionado);
            estornosDeLiquidacoes = facade.buscarEstornosDeLiquidacoesPorReconhecimentoDivida(selecionado);
            pagamentos = facade.buscarPagamentosPorReconhecimentoDivida(selecionado);
            estornosDePagamentos = facade.buscarEstornosDePagamentosPorReconhecimentoDivida(selecionado);
        }
    }

    public void cancelarDocumento() {
        doctoHabilitacaoReconhecimentoDivida = null;
    }

    public void cancelarItem() {
        itemReconhecimentoDivida = null;
    }

    public void instanciarDocumento() {
        doctoHabilitacaoReconhecimentoDivida = new DoctoHabilitacaoReconhecimentoDivida();
        doctoHabilitacaoReconhecimentoDivida.setReconhecimentoDivida(selecionado);
    }

    public void instanciarItem() {
        itemReconhecimentoDivida = new ItemReconhecimentoDivida();
        itemReconhecimentoDivida.setReconhecimentoDivida(selecionado);
    }

    public boolean canExcluir() {
        return selecionado.getSituacaoReconhecimentoDivida().isEmElaboracao() || selecionado.getSituacaoReconhecimentoDivida().isRejeitado();
    }

    public boolean isAprovado() {
        return selecionado.getSituacaoReconhecimentoDivida().isAprovado();
    }

    public boolean canRejeitarOrAprovar() {
        return isGestorControleInterno() && selecionado.getSituacaoReconhecimentoDivida().isAguardandoAprovacao();
    }

    public boolean canEnviarParaAguardando() {
        return selecionado.getSituacaoReconhecimentoDivida().isEmElaboracao() || selecionado.getSituacaoReconhecimentoDivida().isRejeitado();
    }

    public boolean canHabilitarAnexos() {
        return (isGestorControleInterno() && !isAprovado()) || canEnviarParaAguardando();
    }

    public void enviarParaAguardandoAprovacao() {
        try {
            validarCampos();
            selecionado.getHistoricos().add(new HistoricoReconhecimentoDivida(selecionado, facade.getSistemaFacade().getUsuarioCorrente(), new Date(), selecionado.getSituacaoReconhecimentoDivida(), SituacaoReconhecimentoDivida.AGUARDANDO_APROVACAO));
            selecionado.setSituacaoReconhecimentoDivida(SituacaoReconhecimentoDivida.AGUARDANDO_APROVACAO);
            if (isOperacaoNovo()) {
                selecionado = facade.salvarNovoRetornando(selecionado);
            } else {
                facade.salvar(selecionado);
            }
            gerarNotificacaoEnviadoParaAnalise();
            FacesUtil.addOperacaoRealizada("Registro enviado para Aguardando Aprovação com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public void enviarParaRejeitado() {
        try {
            validarCampos();
            selecionado.getHistoricos().add(new HistoricoReconhecimentoDivida(selecionado, facade.getSistemaFacade().getUsuarioCorrente(), new Date(), selecionado.getSituacaoReconhecimentoDivida(), SituacaoReconhecimentoDivida.REJEITADO));
            selecionado.setSituacaoReconhecimentoDivida(SituacaoReconhecimentoDivida.REJEITADO);
            facade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada("Registro rejeitado com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public void enviarParaAprovado() {
        try {
            validarCampos();
            validarCamposAprovacao();
            selecionado.getHistoricos().add(new HistoricoReconhecimentoDivida(selecionado, facade.getSistemaFacade().getUsuarioCorrente(), new Date(), selecionado.getSituacaoReconhecimentoDivida(), SituacaoReconhecimentoDivida.APROVADO));
            selecionado.setSituacaoReconhecimentoDivida(SituacaoReconhecimentoDivida.APROVADO);
            facade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada("Registro aprovado com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarCamposAprovacao() {
        ValidacaoException ve = new ValidacaoException();
        boolean hasParecerControleInterno = false;
        for (ParecerReconhecimentoDivida reconhecimentoDivida : selecionado.getPareceres()) {
            if (reconhecimentoDivida.getTipoParecer().isParecerDoControleInterno()) {
                hasParecerControleInterno = true;
                break;
            }
        }
        if (!hasParecerControleInterno) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para aprovar, é necessário que tenha um Parecer do tipo <b>" + TipoParecerReconhecimentoDivida.PARECER_DO_CONTROLE_INTERNO + "</b>.");
        }
        ve.lancarException();
    }

    private void validarCampos() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDocumentosHabilitacao().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Por favor, informe pelo menos 1(uma) certidão.");
        }
        if (selecionado.getItens().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Por favor, informe pelo menos 1(um) item.");
        }
        if (selecionado.getPareceres().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Por favor, informe pelo menos 1(um) parecer.");
        }
        if (selecionado.getPublicacoes().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Por favor, informe pelo menos 1(uma) publicação.");
        }
        for (ItemReconhecimentoDivida item : selecionado.getItens()) {
            if (item.getDotacoes().isEmpty()){
                ve.adicionarMensagemDeOperacaoNaoPermitida("O item " + item.getObjetoCompra().getCodigo() + " não possui reserva de dotação. Faça a reseva para continuar.");
            }
        }
        ve.lancarException();
        boolean hasTipoCGM = false;
        boolean hasTipoPGM = false;
        boolean hasTipoTermo = false;
        for (ParecerReconhecimentoDivida reconhecimentoDivida : selecionado.getPareceres()) {
            if (reconhecimentoDivida.getTipoParecer().isComunicadoInstauracaoPADCGM()) {
                hasTipoCGM = true;
            }
            if (reconhecimentoDivida.getTipoParecer().isParecerDaPGM()) {
                hasTipoPGM = true;
            }
            if (reconhecimentoDivida.getTipoParecer().isTermoDeReconhecimento()) {
                hasTipoTermo = true;
            }
        }
        if (!hasTipoCGM || !hasTipoPGM || !hasTipoTermo) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário que tenha pareceres com os tipos: <b>" + TipoParecerReconhecimentoDivida.COMUNICADO_INSTAURACAO_PAD_CGM + "</b>, <b>" +
                TipoParecerReconhecimentoDivida.PARECER_DA_PGM + "</b> e <b>" + TipoParecerReconhecimentoDivida.TERMO_DE_RECONHECIMENTO_DE_DIVIDA + "</b>");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                selecionado.getHistoricos().add(new HistoricoReconhecimentoDivida(selecionado, facade.getSistemaFacade().getUsuarioCorrente(), new Date(), null, SituacaoReconhecimentoDivida.EM_ELABORACAO));
                facade.salvarNovoRetornando(selecionado);
            } else {
                facade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public void redirecionarParaReconhecimentoDivida(ReconhecimentoDivida reconhecimentoDivida) {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + reconhecimentoDivida.getId() + "/");
    }

    public boolean isGestorControleInterno() {
        try {
            return facade.isGestorControleInterno();
        } catch (Exception ex) {
            logger.error("Erro ao verificar se o usuário é gestor do controle interno: {}", ex);
            return false;
        }
    }


    public void adicionarItem() {
        try {
            validarItem();
            buscarSubtiposObjetoCompraDaConfiguracaoTipoObjetoCompra(itemReconhecimentoDivida);
            itemReconhecimentoDivida.setValorTotal(itemReconhecimentoDivida.getValorUnitario().multiply(itemReconhecimentoDivida.getQuantidade()).setScale(2, RoundingMode.HALF_EVEN));
            Util.adicionarObjetoEmLista(selecionado.getItens(), itemReconhecimentoDivida);
            cancelarItem();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarItem() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(itemReconhecimentoDivida, ve);
        for (ItemReconhecimentoDivida item : selecionado.getItens()) {
            if (!item.equals(itemReconhecimentoDivida) && item.getObjetoCompra().equals(itemReconhecimentoDivida.getObjetoCompra())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O objeto de compra selecionado já está adicionado.");
            }
        }
        ve.lancarException();
    }

    public void editarItem(ItemReconhecimentoDivida item) {
        itemReconhecimentoDivida = (ItemReconhecimentoDivida) Util.clonarObjeto(item);
    }

    public void selecionarItem(ItemReconhecimentoDivida item) {
        itemReconhecimentoDivida = (ItemReconhecimentoDivida) Util.clonarObjeto(item);
        itemReconhecimentoDividaDotacao = new ItemReconhecimentoDividaDotacao();
        itemReconhecimentoDividaDotacao.setItemReconhecimentoDivida(itemReconhecimentoDivida);
    }

    public void removerItemReconhecimentoDividaDotacao(ItemReconhecimentoDividaDotacao itemDotacao) {
        itemReconhecimentoDivida.getDotacoes().remove(itemDotacao);
    }

    public void removerItem(ItemReconhecimentoDivida item) {
        selecionado.getItens().remove(item);
    }

    public void recuperarObjetoDeCompra() {
        if (itemReconhecimentoDivida.getObjetoCompra() != null && itemReconhecimentoDivida.getObjetoCompra().getId() != null) {
            objetoCompra = facade.getObjetoCompraFacade().recuperar(itemReconhecimentoDivida.getObjetoCompra().getId());
        }
    }

    public void adicionarDocumento() {
        try {
            validarDocumento();
            Util.adicionarObjetoEmLista(selecionado.getDocumentosHabilitacao(), doctoHabilitacaoReconhecimentoDivida);
            cancelarDocumento();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarDocumento() {
        ValidacaoException ve = new ValidacaoException();
        if (doctoHabilitacaoReconhecimentoDivida == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Por favor, selecione um documento.");
        }
        ve.lancarException();
        for (DoctoHabilitacaoReconhecimentoDivida documento : selecionado.getDocumentosHabilitacao()) {
            if (!documento.equals(doctoHabilitacaoReconhecimentoDivida) && doctoHabilitacaoReconhecimentoDivida.getDoctoHabilitacao().equals(documento.getDoctoHabilitacao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Documento selecionado já está adicionado.");
                break;
            }
        }
        ve.lancarException();
    }

    public void removerDocumento(DoctoHabilitacaoReconhecimentoDivida documento) {
        selecionado.getDocumentosHabilitacao().remove(documento);
    }

    public void editarDocumento(DoctoHabilitacaoReconhecimentoDivida documento) {
        doctoHabilitacaoReconhecimentoDivida = (DoctoHabilitacaoReconhecimentoDivida) Util.clonarObjeto(documento);
    }

    public ConverterMascaraUnidadeMedida getConverterMascaraUnidadeMedida() {
        if (itemReconhecimentoDivida != null && itemReconhecimentoDivida.getUnidadeMedida() != null && itemReconhecimentoDivida.getUnidadeMedida().getMascaraQuantidade() != null) {
            return new ConverterMascaraUnidadeMedida(itemReconhecimentoDivida.getUnidadeMedida().getMascaraQuantidade().getMascara());
        }
        return new ConverterMascaraUnidadeMedida(TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL.getMascara());
    }

    public List<SelectItem> getSubtiposObjetoCompra(ItemReconhecimentoDivida item) {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        if (item.getSubTiposObjetoCompra() != null && !item.getSubTiposObjetoCompra().isEmpty()) {
            for (SubTipoObjetoCompra object : item.getSubTiposObjetoCompra()) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    private void buscarSubtiposObjetoCompraDaConfiguracaoTipoObjetoCompra(ItemReconhecimentoDivida item) {
        try {
            Set<SubTipoObjetoCompra> subTipos = new HashSet<>();
            if (item.getObjetoCompra().getTipoObjetoCompra() != null && selecionado.getDataReconhecimento() != null) {
                List<ConfigTipoObjetoCompra> configuracoes = facade.getConfigTipoObjetoCompraFacade().buscarConfiguracoesVigente(
                    selecionado.getDataReconhecimento(),
                    item.getObjetoCompra().getTipoObjetoCompra());
                if (configuracoes != null && !configuracoes.isEmpty()) {
                    for (ConfigTipoObjetoCompra config : configuracoes) {
                        if (config.getSubtipoObjetoCompra() != null) {
                            subTipos.add(config.getSubtipoObjetoCompra());
                        }
                    }
                    item.setSubTiposObjetoCompra(Lists.newArrayList(subTipos));
                    if (item.getSubTiposObjetoCompra().size() == 1 && item.getSubTipoObjetoCompra() == null) {
                        item.setSubTipoObjetoCompra(item.getSubTiposObjetoCompra().get(0));
                    }
                }
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void definirDependenciasParaCriarItemReconhecimentoDividaDotacao(ItemReconhecimentoDivida item) {
        try {
            itemReconhecimentoDivida = item;
            validarSubTipoObjetoCompra(itemReconhecimentoDivida);
            inserirItemReconhecimentoDividaDotacao();
            FacesUtil.executaJavaScript("dlgItemReconhecimentoDotacao.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void inserirItemReconhecimentoDividaDotacao() {
        itemReconhecimentoDividaDotacao = new ItemReconhecimentoDividaDotacao();
        itemReconhecimentoDividaDotacao.setItemReconhecimentoDivida(itemReconhecimentoDivida);
        itemReconhecimentoDividaDotacao.setValorReservado(itemReconhecimentoDivida.getValorParaReservar());
    }

    private void validarSubTipoObjetoCompra(ItemReconhecimentoDivida item) {
        ValidacaoException ve = new ValidacaoException();
        if (item.getSubTipoObjetoCompra() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo subtipo de objeto de compra deve ser informado.");
        }
        ve.lancarException();
    }

    public void alterarPublicacao(PublicacaoReconhecimentoDivida publicacao) {
        publicacaoReconhecimentoDivida = (PublicacaoReconhecimentoDivida) Util.clonarObjeto(publicacao);
    }

    public void removerPublicacao(PublicacaoReconhecimentoDivida publicacao) {
        selecionado.getPublicacoes().remove(publicacao);
    }

    public void cancelarPublicacao() {
        publicacaoReconhecimentoDivida = null;
    }

    public void novaPublicacao() {
        publicacaoReconhecimentoDivida = new PublicacaoReconhecimentoDivida();
        publicacaoReconhecimentoDivida.setReconhecimentoDivida(selecionado);
    }

    public void confirmarPublicacao() {
        try {
            validarAdicaoPublicacao();
            Util.adicionarObjetoEmLista(selecionado.getPublicacoes(), publicacaoReconhecimentoDivida);
            cancelarPublicacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarAdicaoPublicacao() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(publicacaoReconhecimentoDivida, ve);
        ve.lancarException();
    }

    public void alterarParecer(ParecerReconhecimentoDivida parecer) {
        parecerReconhecimentoDivida = (ParecerReconhecimentoDivida) Util.clonarObjeto(parecer);
    }

    public void removerParecer(ParecerReconhecimentoDivida parecer) {
        selecionado.getPareceres().remove(parecer);
    }

    public void cancelarParecer() {
        parecerReconhecimentoDivida = null;
    }

    public void novoParecer() {
        parecerReconhecimentoDivida = new ParecerReconhecimentoDivida();
        parecerReconhecimentoDivida.setReconhecimentoDivida(selecionado);
    }

    public void confirmarParecer() {
        try {
            validarParecer();
            Util.adicionarObjetoEmLista(selecionado.getPareceres(), parecerReconhecimentoDivida);
            cancelarParecer();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarParecer() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(parecerReconhecimentoDivida, ve);
        if (parecerReconhecimentoDivida.getDetentorArquivoComposicao() == null || parecerReconhecimentoDivida.getDetentorArquivoComposicao().getArquivoComposicao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Arquivo deve ser informado.");

        }
        ve.lancarException();
    }

    public boolean hasDotacaoAdiconadaParaItem(ItemReconhecimentoDivida item) {
        return item != null && item.getDotacoes() != null && !item.getDotacoes().isEmpty();
    }

    public List<Pessoa> completarPessoasComVinculoVigente(String parte) {
        return facade.buscarPessoasComVinculoVigente(parte);
    }

    public List<ObjetoCompra> completarObjetosDeCompra(String filtro) {
        return facade.buscarObjetosDeCompra(filtro);
    }

    public List<VeiculoDePublicacao> completarVeiculosDePublicacao(String parte) {
        return facade.getVeiculoDePublicacaoFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<DoctoHabilitacao> completarDocumentosObrigatorios(String filtro) {
        return facade.getDoctoHabilitacaoFacade().buscarDocumentosVigentes(filtro, selecionado.getDataReconhecimento());
    }

    public List<Contrato> completarContratos(String parte) {
        return facade.getContratoFacade().buscarContratos(parte.trim());
    }

    public List<SelectItem> getTiposDePublicacaoLicitacao() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        retorno.add(new SelectItem(TipoPublicacaoLicitacao.TERMO_RECONHECIMENTO_DIVIDA, TipoPublicacaoLicitacao.TERMO_RECONHECIMENTO_DIVIDA.getDescricao()));
        retorno.add(new SelectItem(TipoPublicacaoLicitacao.OUTROS, TipoPublicacaoLicitacao.OUTROS.getDescricao()));
        return retorno;
    }

    public List<SelectItem> getTiposParecerReconhecimentoDivida() {
        return Util.getListSelectItem(TipoParecerReconhecimentoDivida.values());
    }

    public List<FonteDespesaORC> buscarFonteDespesaORCPorCodigoOrDescricao(String codigoOrDescricao) {
        if (itemReconhecimentoDividaDotacao.getDespesaORC() != null) {
            return facade.getFonteDespesaORCFacade().completaFonteDespesaORC(codigoOrDescricao.trim(),
                itemReconhecimentoDividaDotacao.getDespesaORC());
        }
        return Lists.newArrayList();
    }

    public BigDecimal getSaldoOrcamentario() {
        BigDecimal saldo = BigDecimal.ZERO;
        if (itemReconhecimentoDividaDotacao != null
            && itemReconhecimentoDividaDotacao.getDespesaORC() != null
            && itemReconhecimentoDividaDotacao.getFonteDespesaORC() != null) {
            saldo = facade.buscarSaldoFonteDespesaORC(itemReconhecimentoDividaDotacao);
        }
        return saldo;
    }

    public void confirmarItemReconhecimentoDividaDotacao() {
        try {
            validarItemReconhecimentoDividaDotacao();
            Util.adicionarObjetoEmLista(itemReconhecimentoDivida.getDotacoes(), itemReconhecimentoDividaDotacao);
            cancelarItemReconhecimentoDividaDotacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarItemReconhecimentoDividaDotacao() {
        ValidacaoException ve = new ValidacaoException();
        if (itemReconhecimentoDividaDotacao.getDespesaORC() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo elemento de despesa deve ser informado.");
        }
        if (itemReconhecimentoDividaDotacao.getFonteDespesaORC() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo fonte de despesa orçamentária deve ser informado.");
        }
        if (itemReconhecimentoDividaDotacao.getValorReservado() == null ||
            itemReconhecimentoDividaDotacao.getValorReservado().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O valor da reserva deve ser informado, com um valor maior que zero(0).");
        }
        if (itemReconhecimentoDividaDotacao.getValorReservado() != null &&
            itemReconhecimentoDividaDotacao.getValorReservado().compareTo(itemReconhecimentoDivida.getValorParaReservar()) > 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O valor da reserva ultrapassa o valor à ser reservado.");
        }
        ve.lancarException();
    }

    public void cancelarItemReconhecimentoDividaDotacao() {
        itemReconhecimentoDivida = null;
        itemReconhecimentoDividaDotacao = null;
        FacesUtil.executaJavaScript("dlgItemReconhecimentoDotacao.hide()");
    }


    @Override
    public String getCaminhoPadrao() {
        return "/reconhecimento-divida/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public void visualizarEspecificacao(ObjetoDeCompraEspecificacao especificacao) {
        objetoDeCompraEspecificacaoVisualizar = especificacao;
    }

    public ItemReconhecimentoDivida getItemReconhecimentoDivida() {
        return itemReconhecimentoDivida;
    }

    public void setItemReconhecimentoDivida(ItemReconhecimentoDivida itemReconhecimentoDivida) {
        this.itemReconhecimentoDivida = itemReconhecimentoDivida;
    }

    public DoctoHabilitacaoReconhecimentoDivida getDoctoHabilitacaoReconhecimentoDivida() {
        return doctoHabilitacaoReconhecimentoDivida;
    }

    public void setDoctoHabilitacaoReconhecimentoDivida(DoctoHabilitacaoReconhecimentoDivida doctoHabilitacaoReconhecimentoDivida) {
        this.doctoHabilitacaoReconhecimentoDivida = doctoHabilitacaoReconhecimentoDivida;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public ObjetoDeCompraEspecificacao getObjetoDeCompraEspecificacaoVisualizar() {
        return objetoDeCompraEspecificacaoVisualizar;
    }

    public void setObjetoDeCompraEspecificacaoVisualizar(ObjetoDeCompraEspecificacao objetoDeCompraEspecificacaoVisualizar) {
        this.objetoDeCompraEspecificacaoVisualizar = objetoDeCompraEspecificacaoVisualizar;
    }

    public ItemReconhecimentoDividaDotacao getItemReconhecimentoDividaDotacao() {
        return itemReconhecimentoDividaDotacao;
    }

    public void setItemReconhecimentoDividaDotacao(ItemReconhecimentoDividaDotacao itemReconhecimentoDividaDotacao) {
        this.itemReconhecimentoDividaDotacao = itemReconhecimentoDividaDotacao;
    }

    public PublicacaoReconhecimentoDivida getPublicacaoReconhecimentoDivida() {
        return publicacaoReconhecimentoDivida;
    }

    public void setPublicacaoReconhecimentoDivida(PublicacaoReconhecimentoDivida publicacaoReconhecimentoDivida) {
        this.publicacaoReconhecimentoDivida = publicacaoReconhecimentoDivida;
    }

    public ParecerReconhecimentoDivida getParecerReconhecimentoDivida() {
        return parecerReconhecimentoDivida;
    }

    public void setParecerReconhecimentoDivida(ParecerReconhecimentoDivida parecerReconhecimentoDivida) {
        this.parecerReconhecimentoDivida = parecerReconhecimentoDivida;
    }

    public List<Empenho> getEmpenhos() {
        return empenhos;
    }

    public void setEmpenhos(List<Empenho> empenhos) {
        this.empenhos = empenhos;
    }

    public List<EmpenhoEstorno> getEstornosDeEmpenhos() {
        return estornosDeEmpenhos;
    }

    public void setEstornosDeEmpenhos(List<EmpenhoEstorno> estornosDeEmpenhos) {
        this.estornosDeEmpenhos = estornosDeEmpenhos;
    }

    public List<Liquidacao> getLiquidacoes() {
        return liquidacoes;
    }

    public void setLiquidacoes(List<Liquidacao> liquidacoes) {
        this.liquidacoes = liquidacoes;
    }

    public List<LiquidacaoEstorno> getEstornosDeLiquidacoes() {
        return estornosDeLiquidacoes;
    }

    public void setEstornosDeLiquidacoes(List<LiquidacaoEstorno> estornosDeLiquidacoes) {
        this.estornosDeLiquidacoes = estornosDeLiquidacoes;
    }

    public List<Pagamento> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<Pagamento> pagamentos) {
        this.pagamentos = pagamentos;
    }

    public List<PagamentoEstorno> getEstornosDePagamentos() {
        return estornosDePagamentos;
    }

    public void setEstornosDePagamentos(List<PagamentoEstorno> estornosDePagamentos) {
        this.estornosDePagamentos = estornosDePagamentos;
    }

    public BigDecimal getSomaEmpenhos() {
        BigDecimal valor = BigDecimal.ZERO;
        if (empenhos != null && !empenhos.isEmpty()) {
            for (Empenho e : empenhos) {
                valor = valor.add(e.getValor());
            }
        }
        return valor;
    }

    public BigDecimal getSomaEstornoEmpenhos() {
        BigDecimal valor = BigDecimal.ZERO;
        if (estornosDeEmpenhos != null && !estornosDeEmpenhos.isEmpty()) {
            for (EmpenhoEstorno ee : estornosDeEmpenhos) {
                valor = valor.add(ee.getValor());
            }
        }
        return valor;
    }

    public BigDecimal getSomaLiquidacoes() {
        BigDecimal liq = BigDecimal.ZERO;
        if (liquidacoes != null && !liquidacoes.isEmpty()) {
            for (Liquidacao l : liquidacoes) {
                liq = liq.add(l.getValor());
            }
        }
        return liq;
    }

    public BigDecimal getSomaEstornoLiquidacoes() {
        BigDecimal liq = BigDecimal.ZERO;
        if (estornosDeLiquidacoes != null && !estornosDeLiquidacoes.isEmpty()) {
            for (LiquidacaoEstorno le : estornosDeLiquidacoes) {
                liq = liq.add(le.getValor());
            }
        }
        return liq;
    }

    public BigDecimal getSomaPagamentos() {
        BigDecimal valor = BigDecimal.ZERO;
        if (pagamentos != null && !pagamentos.isEmpty()) {
            for (Pagamento p : pagamentos) {
                valor = valor.add(p.getValor());
            }
        }
        return valor;
    }

    public BigDecimal getSomaEstornoPagamentos() {
        BigDecimal estornos = BigDecimal.ZERO;
        if (estornosDePagamentos != null && !estornosDePagamentos.isEmpty()) {
            for (PagamentoEstorno pe : estornosDePagamentos) {
                estornos = estornos.add(pe.getValor());
            }
        }
        return estornos;
    }

    private void gerarNotificacaoEnviadoParaAnalise() {
        List<UsuarioSistema> usuariosControleInterno = facade.getUsuarioSistemaFacade().buscarUsuariosGestoresInternos(selecionado.getUnidadeOrcamentaria(), selecionado.getDataReconhecimento());
        if (usuariosControleInterno != null && !usuariosControleInterno.isEmpty()) {
            List<Notificacao> notificacoes = Lists.newArrayList();
            for (UsuarioSistema usuarioSistema : usuariosControleInterno) {
                Notificacao notificacao = new Notificacao();
                notificacao.setDescricao("Nº " + selecionado.getNumero() + ", enviado em " + DataUtil.getDataFormatadaDiaHora(new Date()) + ", pelo usuário: " + facade.getSistemaFacade().getUsuarioCorrente().toString());
                notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
                notificacao.setTitulo("Reconhecimento de dívida do exercício enviado para análise");
                notificacao.setTipoNotificacao(TipoNotificacao.RECONHECIMENTO_DIVIDA);
                notificacao.setUsuarioSistema(usuarioSistema);
                notificacao.setLink("/reconhecimento-divida/ver/" + selecionado.getId() + "/");
                notificacoes.add(notificacao);
            }
            NotificacaoService.getService().notificar(notificacoes);
        }
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("idReconhecimento", selecionado.getId());
            dto.setNomeRelatorio("Relatório de Reconhecimento de Dívida do Exercício");
            dto.setApi("contabil/reconhecimento-divida/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatório: {}", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public String getFiltroDotacaoPadrao() {
        if (configuracaoContabil == null) {
            configuracaoContabil = facade.getConfiguracaoContabilFacade().configuracaoContabilVigente();
        }
        return configuracaoContabil.getDotacaoPadraoReconhecimentoDiv();
    }
}
