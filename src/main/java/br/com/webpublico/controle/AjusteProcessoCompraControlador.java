package br.com.webpublico.controle;


import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AjusteProcessoCompraFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-ajuste-processo", pattern = "/ajuste-processo-compra/novo/", viewId = "/faces/administrativo/licitacao/ajuste-processo-compra/edita.xhtml"),
    @URLMapping(id = "listar-ajuste-processo", pattern = "/ajuste-processo-compra/listar/", viewId = "/faces/administrativo/licitacao/ajuste-processo-compra/lista.xhtml"),
    @URLMapping(id = "ver-ajuste-processo", pattern = "/ajuste-processo-compra/ver/#{ajusteProcessoCompraControlador.id}/", viewId = "/faces/administrativo/licitacao/ajuste-processo-compra/visualizar.xhtml")
})
public class AjusteProcessoCompraControlador extends PrettyControlador<AjusteProcessoCompra> implements Serializable, CRUD {

    @EJB
    private AjusteProcessoCompraFacade facade;
    private ConverterAutoComplete converterFornecedorDispensaLicitacao;
    private AjusteProcessoCompraVO ajusteVo;
    private FiltroHistoricoProcessoLicitatorio filtro;
    private List<AjusteProcessoCompraVO> ajustes;

    public AjusteProcessoCompraControlador() {
        super(AjusteProcessoCompra.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/ajuste-processo-compra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-ajuste-processo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        ajusteVo = new AjusteProcessoCompraVO();
        ajusteVo.setDataLancamento(facade.getSistemaFacade().getDataOperacao());
        ajusteVo.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        ajusteVo.setTipoAjuste(TipoAjusteProcessoCompra.SUBSTITUIR_CONTROLE_ITEM);
        ajusteVo.setTipoProcesso(TipoMovimentoProcessoLicitatorio.LICITACAO);
        ajusteVo.setTipoControle(TipoControleLicitacao.VALOR);
    }

    @URLAction(mappingId = "ver-ajuste-processo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        ajusteVo = facade.buscarAjusteVO(selecionado.getId());
    }

    public void salvar() {
        try {
            validarCamposObrigatorio();
            validarRegrasEspecificas();
            selecionado = facade.salvarRetornando(ajusteVo);
            redirecionarParaAjuste(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao salvar ajuste processo de compra.", e);
            descobrirETratarException(e);
        }
    }

    public void listenerProcesso() {
        try {
            ajusteVo.limparDadosFornecedor();
            validarLicitacaoMaiorDesconto();
            recuperarDadosProcesso();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void recuperarDadosProcesso() {
        switch (ajusteVo.getTipoAjuste()) {
            case SUBSTITUIR_CONTROLE_ITEM:
                ajusteVo.setItensSubstituicaoTipoControle(facade.buscarItensSubstituicaoTipoControle(ajusteVo, Lists.newArrayList()));
                break;
            case SUBSTITUIR_OBJETO_COMPRA:
                buscarObjetosCompraDoProcesso();
                break;
            case INCLUIR_PROPOSTA_FORNECEDOR:
                novaPropostaFornecedor();
                break;
            case EDITAR_PROPOSTA_FORNECEDOR:
                buscarPropostasFornecedor();
                break;
            case INCLUIR_FORNECEDOR:
                novoFornecedor();
                break;
            case EDITAR_FORNECEDOR:
                buscarFornecedores();
                break;
            case SUBSTITUIR_FORNECEDOR:
                buscarContratos();
                break;
        }
    }

    public void buscarContratos() {
        ajusteVo.setContratos(facade.buscarContratos(ajusteVo));
    }

    public void listenerTipoProcesso() {
        ajusteVo.limparDadosGerais();
    }

    public void recalcularValoresMovimentos(ItemAjusteProcessoCompraVO item) {
        Map<Long, BigDecimal> mapValorItem = Maps.newHashMap();
        int qtdeParticipantes = 0;
        for (MovimentoAjusteProcessoCompraVO itemMov : item.getMovimentos()) {
            itemMov.setTipoControle(item.getSelecionado() ? TipoControleLicitacao.VALOR : itemMov.getTipoControleOriginal());
            itemMov.setQuantidade(item.getSelecionado() ? BigDecimal.ONE : itemMov.getQuantidadeOriginal());
            itemMov.setValorUnitario(item.getSelecionado() ? itemMov.getValorTotal() : itemMov.getValorUnitarioOriginal());

            if (itemMov.getTipoProcesso().isSolicitacaoCompra()) {
                mapValorItem.put(item.getNumeroItem(), itemMov.getValorUnitario());
            }
            if (itemMov.getTipoProcesso().isParticipanteIrp()) {
                qtdeParticipantes++;
            }
        }

        for (MovimentoAjusteProcessoCompraVO itemIrp : item.getMovimentos()) {
            BigDecimal valorItem = mapValorItem.get(item.getNumeroItem());
            if (itemIrp.getTipoProcesso().isParticipanteIrp()) {
                if (item.getSelecionado()) {
                    valorItem = qtdeParticipantes > 1 ? valorItem.divide(new BigDecimal(qtdeParticipantes), 2, RoundingMode.HALF_EVEN) : valorItem;
                }
                itemIrp.setValorTotal(valorItem);
                itemIrp.setValorUnitario(itemIrp.getValorTotal());
            }
        }
    }

    public boolean hasItemSelecionado() {
        return hasItensTipoControle() && ajusteVo.getItensSubstituicaoTipoControle().stream().anyMatch(ItemAjusteProcessoCompraVO::getSelecionado);
    }

    public void aplicarTipoControleParaTodosItens() {
        for (ItemAjusteProcessoCompraVO item : ajusteVo.getItensSubstituicaoTipoControle()) {
            if (item.getTipoControle().isTipoControlePorQuantidade()) {
                item.setSelecionado(true);
                recalcularValoresMovimentos(item);
            }
        }
    }

    public void buscarObjetosCompraDoProcesso() {
        try {
            ajusteVo.setObjetosCompraDoProcesso(Lists.newArrayList());
            if (ajusteVo.hasObjetoCompraSelecionado()) {
                ajusteVo.getObjetoCompraSelecionado().setMovimentos(Lists.newArrayList());
                ajusteVo.setObjetoCompraSelecionado(null);
            }
            List<ItemAjusteProcessoCompraVO> objetos = facade.buscarObjetosCompraPorProcesso(ajusteVo);
            ajusteVo.setObjetosCompraDoProcesso(objetos);
            Collections.sort(ajusteVo.getObjetosCompraDoProcesso());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public boolean hasItensTipoControle() {
        return !Util.isListNullOrEmpty(ajusteVo.getItensSubstituicaoTipoControle());
    }

    public void redirecionarParaProcesso() {
        FacesUtil.redirecionamentoInterno(ajusteVo.getTipoProcesso().getUrl() + ajusteVo.getIdProcesso() + "/");
    }

    public void redirecionarParaAjuste(AjusteProcessoCompra ajuste) {
        selecionado = ajuste;
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public List<ObjetoCompra> completarObjetoCompra(String parte) {
        return facade.getObjetoCompraFacade().buscarObjetoCompraPorSituacao(parte.trim(), SituacaoObjetoCompra.DEFERIDO);
    }

    public List<Licitacao> completarLicitacao(String parte) {
        if (isTipoAjusteAlteracaoFornecedor()) {
            return facade.getLicitacaoFacade().buscarLicitacaoPorTipoApuracao(parte.trim(), TipoApuracaoLicitacao.ITEM);
        }
        return facade.getLicitacaoFacade().buscarLicitacaoPorModalidade(parte.trim(), ModalidadeLicitacao.modalidadesLicitacao);
    }

    public List<Licitacao> completarCredenciamento(String parte) {
        return facade.getLicitacaoFacade().buscarLicitacaoPorModalidade(parte.trim(), ModalidadeLicitacao.CREDENCIAMENTO);
    }

    public List<DispensaDeLicitacao> completarDispensaLicitacao(String parte) {
        if (isTipoAjusteAlteracaoFornecedor()) {
            return facade.getDispensaDeLicitacaoFacade().buscarDispensaDeLicitacaoPorNumeroOrExercicioOrResumoAndUsuarioAndGestor(parte.trim(), facade.getSistemaFacade().getUsuarioCorrente(), Boolean.TRUE);
        }
        return facade.getDispensaDeLicitacaoFacade().buscarDispensaDeLicitacaoPorNumeroOrExercicioOrResumo(parte.trim());
    }

    public List<RegistroSolicitacaoMaterialExterno> completarRegistroPrecoExterno(String parte) {
        return facade.getRegistroSolicitacaoMaterialExternoFacade().buscarRegistroPrecoExterno(parte.trim());
    }

    public List<SolicitacaoMaterial> completarSolicitacaoCompra(String parte) {
        return facade.getSolicitacaoMaterialFacade().buscarSolicitacaoMaterial(parte.trim());
    }

    public List<Cotacao> completarCotacao(String parte) {
        return facade.getCotacaoFacade().buscarCotacao(parte.trim());
    }

    public List<FormularioCotacao> completarFormularioCotacao(String parte) {
        return facade.getFormularioCotacaoFacade().buscarFormularioCotacao(parte.trim());
    }

    public List<IntencaoRegistroPreco> completarIRP(String parte) {
        return facade.getIntencaoRegistroPrecoFacade().buscarIRP(parte.trim());
    }

    public List<SelectItem> getTiposAjustesProcesso() {
        return Util.getListSelectItemSemCampoVazio(TipoAjusteProcessoCompra.values(), false);
    }

    public List<SelectItem> getTiposProcesso() {
        if (ajusteVo.getTipoAjuste() != null) {
            return Util.getListSelectItemSemCampoVazio(TipoMovimentoProcessoLicitatorio.getTiposProcessoPorTipoAjuste(ajusteVo.getTipoAjuste()).toArray(), false);
        }
        return Lists.newArrayList();
    }

    public void atualizarValorUnitario(MovimentoAjusteProcessoCompraVO item) {
        item.setValorUnitario(item.getValorTotal());
    }

    public AjusteProcessoCompraVO getAjusteVo() {
        return ajusteVo;
    }

    public void setAjusteVo(AjusteProcessoCompraVO ajusteVo) {
        this.ajusteVo = ajusteVo;
    }

    public void salvarLance() {
        try {
            validarSalvarLance();
            facade.salvarLance(ajusteVo.getItemPregao());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao salvar lance pregão", e);
            descobrirETratarException(e);
        }
    }

    private void validarSalvarLance() {
        ValidacaoException ve = new ValidacaoException();
        boolean lanceGerado = false;
        for (LancePregao lancePregao : ajusteVo.getRodadaPregao().getListaDeLancePregao()) {
            lanceGerado = lancePregao.getId() == null;
        }
        if (!lanceGerado) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Antes de salvar, é necessário gerar o(s) lance(s) no pregão para a proposta do fornecedor.");
        }
        ve.lancarException();
    }

    public List<PropostaFornecedor> completarPropostaFornecedor(String parte) {
        if (ajusteVo.getIdProcesso() != null) {
            return facade.getPropostaFornecedorFacade().buscarPorLicitacao(ajusteVo.getIdProcesso(), parte.trim());
        }
        return Lists.newArrayList();
    }

    public List<LicitacaoFornecedor> completarFornecedorLicitacao(String parte) {
        if (ajusteVo.getIdProcesso() != null) {
            return facade.getParticipanteLicitacaoFacade().buscarPorLicitacao(ajusteVo.getIdProcesso(), parte.trim());
        }
        return Lists.newArrayList();
    }

    public List<FornecedorDispensaDeLicitacao> completarFornecedoresDispensa(String parte) {
        return facade.getFornecedorDispensaDeLicitacaoFacade().buscarFornecedoresDispensaLicitacao(ajusteVo.getDispensaLicitacao(), parte.trim());
    }

    public void listenerIncluirFornecedor() {
        ajusteVo.setRepresentanteLicitacao(null);
        if (isFornecedorPessoaJuridica()) {
            ajusteVo.setRepresentanteLicitacao(new RepresentanteLicitacao());
        }
    }

    public void listenerEditarFornecedor() {
        if (hasFornecedor()) {
            ajusteVo.setRepresentanteLicitacao(ajusteVo.getLicitacaoFornecedor().getRepresentante());
            if (ajusteVo.getRepresentanteLicitacao() == null) {
                listenerIncluirFornecedor();
            }
        }
    }

    public void listenerNovaPropostaFornecedor() {
        try {
            validarNovaProposta();
            if (ajusteVo.getLicitacaoFornecedor() != null) {
                ajusteVo.getPropostaFornecedor().setRepresentante(ajusteVo.getLicitacaoFornecedor().getRepresentante());
                ajusteVo.getPropostaFornecedor().setInstrumentoRepresentacao(ajusteVo.getLicitacaoFornecedor().getInstrumentoRepresentacao());
                ajusteVo.getPropostaFornecedor().setFornecedor(ajusteVo.getLicitacaoFornecedor().getEmpresa());
                ajusteVo.getPropostaFornecedor().setLicitacaoFornecedor(ajusteVo.getLicitacaoFornecedor());
                facade.getPropostaFornecedorFacade().criarLotesItensProposta(ajusteVo.getPropostaFornecedor());
                ajusteVo.setLotesProposta(ajusteVo.getPropostaFornecedor().getLotes());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarNovaProposta() {
        ValidacaoException ve = new ValidacaoException();
        if (facade.getPropostaFornecedorFacade().fornecedorJaFezPropostaParaEstaLicitacao(ajusteVo.getLicitacaoFornecedor().getEmpresa(), ajusteVo.getLicitacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O fornecedor selecionado já possui proposta para a licitação: " + ajusteVo.getLicitacao() + ".");
        }
        LicitacaoFornecedor fornecedor = facade.getParticipanteLicitacaoFacade().buscarLicitacaoFornecedor(ajusteVo.getLicitacao(), ajusteVo.getLicitacaoFornecedor().getEmpresa());
        if (fornecedor == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A licitação não possui fornecedor com a pessoa " + ajusteVo.getPropostaFornecedor().getFornecedor());
        }
        ve.lancarException();
    }

    public void listenerEditarPropostaFornecedor() {
        ajusteVo.setPropostaFornecedor(facade.getPropostaFornecedorFacade().recuperarComDependenciasItens(ajusteVo.getPropostaFornecedor().getId()));
        ajusteVo.setLotesProposta(ajusteVo.getPropostaFornecedor().getLotes());
        ajusteVo.setLicitacaoFornecedor(facade.getParticipanteLicitacaoFacade().buscarLicitacaoFornecedor(ajusteVo.getLicitacao(), ajusteVo.getPropostaFornecedor().getFornecedor()));

        for (LotePropostaFornecedor lote : ajusteVo.getLotesProposta()) {
            for (ItemPropostaFornecedor item : lote.getItens()) {
                List<ItemPregao> itensPregao = facade.getItemPregaoFacade().buscarItemPregaoPorItemProcessoCompra(ajusteVo.getLicitacao(), Lists.newArrayList(item.getItemProcessoDeCompra().getId()), ajusteVo.getPropostaFornecedor());
                if (itensPregao != null && itensPregao.size() == 1) {
                    item.setItemPregao(itensPregao.get(0));
                }
            }
        }
    }

    private void buscarFornecedores() {
        if (ajusteVo.getIdProcesso() != null) {
            List<LicitacaoFornecedor> fornecedoresLicitacao = facade.getParticipanteLicitacaoFacade().buscarPorLicitacao(ajusteVo.getIdProcesso(), "");
            ajusteVo.setFornecedoresLicitacao(fornecedoresLicitacao);
        }
    }

    private void buscarPropostasFornecedor() {
        if (ajusteVo.getIdProcesso() != null) {
            ajusteVo.setPropostas(facade.getPropostaFornecedorFacade().buscarPorLicitacao(ajusteVo.getIdProcesso(), ""));
        }
    }

    public void novoFornecedor() {
        try {
            ajusteVo.setLicitacaoFornecedor(new LicitacaoFornecedor());
            ajusteVo.getLicitacaoFornecedor().setCodigo(facade.getParticipanteLicitacaoFacade().getMaiorCodigoLicitacaoFornecedor(ajusteVo.getFornecedoresLicitacao()));
            ajusteVo.getLicitacaoFornecedor().setLicitacao(ajusteVo.getLicitacao());
            buscarFornecedores();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCamposObrigatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (ajusteVo.getTipoAjuste() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de ajuste deve ser informado.");
        }
        if (ajusteVo.getTipoProcesso() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de processo deve ser informado.");
        }
        if (ajusteVo.isProcessoNulo()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo " + selecionado.getTipoProcesso().getDescricao() + " deve ser informado. ");
        }
        if (Strings.isNullOrEmpty(ajusteVo.getMotivo())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo motivo deve ser informado.");
        }
        ve.lancarException();
    }

    private void validarLicitacaoMaiorDesconto() {
        ValidacaoException ve = new ValidacaoException();
        if (ajusteVo.getTipoProcesso().isLicitacao() && ajusteVo.getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Processo não atende licitações de maior desconto.");
        }
        ve.lancarException();
    }

    private void validarRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        switch (ajusteVo.getTipoAjuste()) {
            case SUBSTITUIR_CONTROLE_ITEM:
                if (!hasItemSelecionado()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione ao menos um item para continuar.");
                }
                break;
            case SUBSTITUIR_OBJETO_COMPRA:
                if (Util.isListNullOrEmpty(ajusteVo.getObjetosCompraSubstituicao())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A lista de objetos para substituição está vazia.");
                }
                break;
            case INCLUIR_FORNECEDOR:
            case EDITAR_FORNECEDOR:
                if (!hasFornecedor()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum alteração referente ao fornecedor foi realizada.");
                } else if (ajusteVo.getLicitacaoFornecedor().getEmpresa() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo fornecedor deve ser informado. ");
                } else {
                    ajusteVo.getFornecedoresLicitacao().stream()
                        .filter(part -> !part.equals(ajusteVo.getLicitacaoFornecedor()))
                        .filter(part -> part.getEmpresa().equals(ajusteVo.getLicitacaoFornecedor().getEmpresa()))
                        .map(part -> "Fornecedor já cadastrado para esta licitação.")
                        .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);
                }
                ve.lancarException();

                if (isFornecedorPessoaJuridica()) {
                    Util.validarCampos(ajusteVo.getRepresentanteLicitacao());
                    if (Strings.isNullOrEmpty(ajusteVo.getLicitacaoFornecedor().getInstrumentoRepresentacao())) {
                        ve.adicionarMensagemDeCampoObrigatorio("O campo instrumento de representação deve ser informado. ");
                    }
                }
                break;
            case INCLUIR_PROPOSTA_FORNECEDOR:
            case EDITAR_PROPOSTA_FORNECEDOR:
                Util.validarCampos(ajusteVo.getPropostaFornecedor());
                if (!hasFornecedor()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum alteração referente a proposta foi realizada.");
                }
                break;
            case SUBSTITUIR_FORNECEDOR:
                if (ajusteVo.getLicitacaoFornecedor() == null && ajusteVo.getFornecedorDispensaLicitacao() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo fornecedor 'de' deve ser informado.");
                }
                if (ajusteVo.getFornecedorSubstituicao() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo fornecedor 'para' deve ser informado.");
                }
                ve.lancarException();

                Pessoa fornecedor = ajusteVo.getTipoProcesso().isLicitacao() ? ajusteVo.getLicitacaoFornecedor().getEmpresa() : ajusteVo.getFornecedorDispensaLicitacao().getPessoa();
                if (fornecedor.equals(ajusteVo.getFornecedorSubstituicao())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido subsituição para o mesmo fornecedor.");
                }
                break;
        }
        ve.lancarException();
    }

    public void novaPropostaFornecedor() {
        try {
            ajusteVo.setPropostaFornecedor(new PropostaFornecedor());
            ajusteVo.getPropostaFornecedor().setLicitacao(ajusteVo.getLicitacao());
            ajusteVo.getPropostaFornecedor().setDataProposta(ajusteVo.getLicitacao().getAbertaEm());
            ajusteVo.getPropostaFornecedor().setLicitacaoFornecedor(ajusteVo.getLicitacaoFornecedor());
            buscarPropostasFornecedor();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarAndVerificarCpfRepresentante() {
        try {
            validarCpf();
            verificarExistenciaRepresentante();
            ajusteVo.getLicitacaoFornecedor().setRepresentante(ajusteVo.getRepresentanteLicitacao());
        } catch (ValidacaoException ve) {
            ajusteVo.getRepresentanteLicitacao().setNome(null);
            ajusteVo.getRepresentanteLicitacao().setCpf(null);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCpf() {
        ValidacaoException ve = new ValidacaoException();
        if (StringUtils.isEmpty(ajusteVo.getRepresentanteLicitacao().getCpf())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo CPF deve ser informado.");
        } else if (!Util.validarCpf(ajusteVo.getRepresentanteLicitacao().getCpf())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O CPF digitado é inválido.");
        }
        ve.lancarException();
    }

    private void verificarExistenciaRepresentante() {
        String cpf = ajusteVo.getRepresentanteLicitacao().getCpf();
        RepresentanteLicitacao representante = facade.getLicitacaoFacade().getRepresentanteLicitacaoFacade().buscarRepresentanteLicitacaoPorCpf(cpf);
        if (representante.getId() != null) {
            ajusteVo.setRepresentanteLicitacao(representante);
        } else {
            ajusteVo.setRepresentanteLicitacao(representante);
            ajusteVo.getRepresentanteLicitacao().setCpf(cpf);
        }
    }

    public void selecionarItemPregao(ItemPregao item) {
        ajusteVo.setItemPregao(facade.getPregaoFacade().buscarRodadasAndLancesPregao(item));
        Collections.sort(ajusteVo.getItemPregao().getListaDeRodadaPregao());
        ajusteVo.setRodadaPregao(ajusteVo.getItemPregao().getListaDeRodadaPregao().get(item.getListaDeRodadaPregao().size() - 1));
        if (ajusteVo.getRodadaPregao() != null) {
            for (RodadaPregao rodada : ajusteVo.getItemPregao().getListaDeRodadaPregao()) {
                ajusteVo.getItemPregao().getPregao().atribuirValorLanceRodadaAnterior(ajusteVo.getItemPregao(), rodada);

                for (LancePregao lancePregao : rodada.getListaDeLancePregao()) {
                    List<ItemPropostaFornecedor> itensProposta = facade.getPropostaFornecedorFacade().recuperarItemPropostaFornecedorPorItemProcessoCompra(ajusteVo.getItemPregao().getItemPregaoItemProcesso().getItemProcessoDeCompra());
                    facade.getPregaoFacade().atribuirValorPrimeiraPropostaNoLance(lancePregao, itensProposta);
                }
            }
            FacesUtil.executaJavaScript("dlgLancePregao.show()");
        }
    }

    public void gerarLanceParaNovaProposta() {
        try {
            for (RodadaPregao rod : ajusteVo.getItemPregao().getListaDeRodadaPregao()) {
                validarGeracaoLance(rod);
                LancePregao lance = new LancePregao();
                lance.setRodadaPregao(rod);
                lance.setStatusLancePregao(StatusLancePregao.DECLINADO);
                lance.setPropostaFornecedor(selecionado.getPropostaFornecedor());
                lance.setValor(BigDecimal.ZERO);
                lance.setPercentualDesconto(BigDecimal.ZERO);
                lance.setValorPropostaInicial(getValorPropostaItemAlteracao());
                Util.adicionarObjetoEmLista(rod.getListaDeLancePregao(), lance);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarGeracaoLance(RodadaPregao rodadaPregao) {
        ValidacaoException ve = new ValidacaoException();
        for (LancePregao lance : rodadaPregao.getListaDeLancePregao()) {
            if (lance.getPropostaFornecedor().equals(selecionado.getPropostaFornecedor())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A rodada já possui um lance para o proposta " + selecionado.getPropostaFornecedor());
                break;
            }
        }
        ve.lancarException();
    }

    private BigDecimal getValorPropostaItemAlteracao() {
        List<AjusteProcessoCompraItem> itens = facade.buscarItensAjustes(selecionado.getId());
        for (AjusteProcessoCompraItem item : itens) {
            if (item.getItemPropostaFornecedor().getItemProcessoDeCompra().equals(ajusteVo.getItemPregao().getItemPregaoItemProcesso().getItemProcessoDeCompra())) {
                return selecionado.getLicitacaoFornecedor().getLicitacao().getTipoAvaliacao().isMaiorDesconto()
                    ? item.getItemPropostaFornecedor().getPercentualDesconto()
                    : item.getItemPropostaFornecedor().getPreco();
            }
        }
        return BigDecimal.ZERO;
    }

    public void carregarInformacaoProduto(ItemPropostaFornecedor item) {
        ajusteVo.setItemPropostaFornecedor(item);
        if (ajusteVo.getItemPropostaFornecedor().getItemProcessoDeCompra() != null && item.getItemProcessoDeCompra().getItemSolicitacaoMaterial() != null) {
            ajusteVo.getItemPropostaFornecedor().setDescricaoProduto(ajusteVo.getItemPropostaFornecedor().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricao());
        }
    }

    public void selecionarItem(ItemPropostaFornecedor item) {
        if (item.getObjetoCompra().getTipoObjetoCompra().isServico()) {
            item.setSelecionado(item.hasPreco() || item.hasPercentualDesconto());
            return;
        }
        item.setSelecionado(!Strings.isNullOrEmpty(item.getMarca()) && (item.hasPreco() || item.hasPercentualDesconto()));
    }

    public void selecionarLote(LotePropostaFornecedor loteProposta) {
        try {
            validarFornecedor();
            loteProposta.getItens().forEach(this::selecionarItem);
            ajusteVo.setLotePropostaFornecedor(loteProposta);
            FacesUtil.executaJavaScript("dlgProposta.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarFornecedor() {
        ValidacaoException ve = new ValidacaoException();
        if (ajusteVo.getPropostaFornecedor().getFornecedor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo fornecedor deve ser informado antes de realizar os lançamentos.");
        }
        ve.lancarException();
    }

    public void confirmaInformacaoProduto() {
        ValidacaoException exception = new ValidacaoException();
        try {
            Util.validarQuantidadeCaracter(ajusteVo.getItemPropostaFornecedor(), exception);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
        if (!exception.temMensagens())
            FacesUtil.executaJavaScript("dlgInfoProduto.hide()");
    }

    public void cancelarInformacaoProduto() {
        ajusteVo.getItemPropostaFornecedor().setModelo("");
        ajusteVo.getItemPropostaFornecedor().setDescricaoProduto("");
    }

    public void cancelarProposta() {
        for (LotePropostaFornecedor lote : ajusteVo.getLotesProposta()) {
            if (ajusteVo.getLotePropostaFornecedor().equals(lote)) {
                for (ItemPropostaFornecedor item : lote.getItens()) {
                    if (item.getItemPregao() == null) {
                        item.setMarca(null);
                        item.setModelo(null);
                        item.setDescricaoProduto(null);
                        item.setPreco(BigDecimal.ZERO);
                        ajusteVo.getLotePropostaFornecedor().setValor(BigDecimal.ZERO);
                    }
                }
            }
        }
        FacesUtil.executaJavaScript("dlgProposta.hide()");
    }

    public void confirmarProposta() {
        try {
            validarProposta();
            atualizaValorLote();
            Util.adicionarObjetoEmLista(ajusteVo.getLotesProposta(), ajusteVo.getLotePropostaFornecedor());
            FacesUtil.executaJavaScript("dlgProposta.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarProposta() {
        ValidacaoException ve = new ValidacaoException();
        for (ItemPropostaFornecedor item : ajusteVo.getLotePropostaFornecedor().getItens()) {
            facade.getPropostaFornecedorFacade().validarConfirmacaoItemProposta(ajusteVo.getLicitacao(), item, ve);
        }
        ve.lancarException();
    }

    public void atualizaValorLote() {
        BigDecimal valor = BigDecimal.ZERO;
        for (ItemPropostaFornecedor ipf : ajusteVo.getLotePropostaFornecedor().getItens()) {
            if (ipf.hasPreco()) {
                if (ipf.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getItemCotacao().getTipoControle().isTipoControlePorQuantidade()) {
                    valor = valor.add(ipf.getPrecoTotal());
                } else {
                    valor = valor.add(ipf.getPreco());
                }
            }
        }
        ajusteVo.getLotePropostaFornecedor().setValor(valor);
    }

    public boolean isTipoAjusteAlteracaoFornecedor() {
        return ajusteVo.getTipoAjuste() != null && ajusteVo.getTipoAjuste().isTipoAjusteAlteracaoFornecedor();
    }

    public boolean isMaiorDesconto() {
        return ajusteVo.getLicitacao() != null && ajusteVo.getLicitacao().getTipoAvaliacao().isMaiorDesconto();
    }

    public boolean isApuracaoPorItem() {
        return ajusteVo.getLicitacao() != null && ajusteVo.getLicitacao().getTipoApuracao().isPorItem();
    }

    public boolean hasLotes() {
        return ajusteVo.getLotesProposta() != null && !ajusteVo.getLotesProposta().isEmpty();
    }

    public boolean hasItensPregao() {
        return ajusteVo.getItensPregao() != null && !ajusteVo.getItensPregao().isEmpty();
    }

    public boolean hasPropostas() {
        return ajusteVo.getPropostas() != null && !ajusteVo.getPropostas().isEmpty();
    }

    public void cancelarLanceParaNovaProposta() {
        ajusteVo.setItemPregao(null);
    }

    public boolean hasFornecedor() {
        return ajusteVo.getLicitacaoFornecedor() != null;
    }

    public boolean hasPropostaFornecedor() {
        return ajusteVo.getPropostaFornecedor() != null;
    }

    public boolean isFornecedorPessoaJuridica() {
        return hasFornecedor() && ajusteVo.getLicitacaoFornecedor().getEmpresa() != null && ajusteVo.getLicitacaoFornecedor().getEmpresa().isPessoaJuridica();
    }

    public boolean isPropostaFornecedorPessoaFisica() {
        return hasPropostaFornecedor() && ajusteVo.getPropostaFornecedor().getFornecedor() != null && ajusteVo.getPropostaFornecedor().getFornecedor().isPessoaFisica();
    }

    public boolean isIncluirFornecedor() {
        return ajusteVo.getTipoAjuste() != null && ajusteVo.getTipoAjuste().isIncluirFornecedor();
    }

    public boolean isEditarFornecedor() {
        return ajusteVo.getTipoAjuste() != null && ajusteVo.getTipoAjuste().isEditarFornecedor();
    }

    public boolean isIncluirProposta() {
        return ajusteVo.getTipoAjuste() != null && ajusteVo.getTipoAjuste().isIncluirProposta();
    }

    public boolean isEditarProposta() {
        return ajusteVo.getTipoAjuste() != null && ajusteVo.getTipoAjuste().isEditarProposta();
    }

    public boolean isSubstituirFornecedor() {
        return ajusteVo.getTipoAjuste() != null && ajusteVo.getTipoAjuste().isSubstituirFornecedor();
    }

    public boolean isIncluirOrEditarProposta() {
        return isIncluirProposta() || isEditarProposta();
    }

    public boolean isIncluirOrEditarFornecedor() {
        return isIncluirFornecedor() || isEditarFornecedor();
    }

    public ConverterAutoComplete getConverterFornecedorDispensaLicitacao() {
        if (converterFornecedorDispensaLicitacao == null) {
            converterFornecedorDispensaLicitacao = new ConverterAutoComplete(FornecedorDispensaDeLicitacao.class, facade.getFornecedorDispensaDeLicitacaoFacade());
        }
        return converterFornecedorDispensaLicitacao;
    }

    public void redirecionarContrato(Contrato contrato) {
        FacesUtil.redirecionamentoInterno("/contrato-adm/ver/" + contrato.getId() + "/");
    }


    public void selecionarEspecificacao(ActionEvent evento) {
        ObjetoDeCompraEspecificacao especificacao = (ObjetoDeCompraEspecificacao) evento.getComponent().getAttributes().get("objeto");
        ajusteVo.getObjetoCompraSelecionado().setEspecificacaoPara(especificacao.getTexto());
    }

    public void limparEspecificacao() {
        ajusteVo.getObjetoCompraSelecionado().setEspecificacaoPara(null);
    }

    private void validarAdicionarObjetoCompra() {
        ValidacaoException ve = new ValidacaoException();
        if (!ajusteVo.hasObjetoCompraSelecionado()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo objeto compra deve ser informado.");
        }
        ve.lancarException();
        if (ajusteVo.getObjetoCompraSelecionado().getUnidadeMedidaPara() == null
            && ajusteVo.getObjetoCompraSelecionado().getQuantidadePara() == null
            && ajusteVo.getObjetoCompraSelecionado().getObjetoCompraPara() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Nenhum alteração encontrada para adicionar o objeto para substituição.");

        }
        if (ajusteVo.getObjetoCompraSelecionado().getQuantidadeDe() != null
            && ajusteVo.getObjetoCompraSelecionado().getQuantidadeDe().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo quantidade deve ser maior que zero(0).");
        }
        if (ajusteVo.getObjetoCompraSelecionado().getObjetoCompraPara() != null
            && ajusteVo.getObjetoCompraSelecionado().getObjetoCompra().equals(ajusteVo.getObjetoCompraSelecionado().getObjetoCompraPara())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido substituição para objetos de compra iguais.");
        }
        ve.lancarException();
        validarObjetoCompraPara();
    }

    public void adicionarObjetoCompra() {
        try {
            validarAdicionarObjetoCompra();
            validarGrupoContaDespesaObjetoCompra();
            Util.adicionarObjetoEmLista(ajusteVo.getObjetosCompraSubstituicao(), ajusteVo.getObjetoCompraSelecionado());
            ajusteVo.setObjetoCompraSelecionado(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerObjetoCompra(ItemAjusteProcessoCompraVO obj) {
        ajusteVo.getObjetosCompraSubstituicao().remove(obj);
    }

    public void editarObjetoCompra(ItemAjusteProcessoCompraVO obj) {
        obj.setSelecionado(true);
        ajusteVo.setObjetoCompraSelecionado((ItemAjusteProcessoCompraVO) Util.clonarObjeto(obj));
    }

    public void selecionarObjetoCompra(ItemAjusteProcessoCompraVO obj) {
        try {
            cancelarObjetoCompra();
            ajusteVo.setObjetoCompraSelecionado(obj);
            ajusteVo.setObjetoCompra(obj.getObjetoCompra());
            ajusteVo.getObjetoCompra().setGrupoContaDespesa(facade.getGrupoContaDespesa(ajusteVo.getObjetoCompra()));

            List<MovimentoAjusteProcessoCompraVO> movimentos = facade.buscarMovimentosObjetoCompra(ajusteVo, ajusteVo.getObjetoCompraSelecionado().getObjetoCompra(), ajusteVo.getObjetoCompraSelecionado().getNumeroItem());
            ajusteVo.getObjetoCompraSelecionado().setMovimentos(movimentos);

            validarGrupoContaDespesaObjetoCompra();
            verificarOrigemSubstituicaoObjetoCompra();
            FacesUtil.executaJavaScript("setaFoco('Formulario:oc-para_input')");
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public boolean hasObjetoCompraSelecionado() {
        return ajusteVo.getObjetoCompraSelecionado() != null;
    }

    public void validarGrupoContaDespesaObjetoCompra() {
        ValidacaoException ve = new ValidacaoException();
        if (ajusteVo.getObjetoCompraSelecionado().getGrupoContaDespesaDe() != null && !ajusteVo.getObjetoCompraSelecionado().getGrupoContaDespesaDe().hasContasDespesa()
            && ajusteVo.getObjetoCompraSelecionado().getObjetoCompra().getTipoObjetoCompra().isPermanenteOrConsumo()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhuma conta de despesa configurada para o grupo do objeto de compra: " + ajusteVo.getObjetoCompraSelecionado().getObjetoCompra() + ".");
        }
        ve.lancarException();
    }

    public void verificarOrigemSubstituicaoObjetoCompra() {
        MovimentoAjusteProcessoCompraVO movimentoVO = ajusteVo.getObjetoCompraSelecionado().getMovimentos()
            .stream()
            .filter(mov -> ajusteVo.getIdProcesso().equals(mov.getIdProcesso()))
            .findFirst().orElse(null);

        if (movimentoVO != null) {

            MovimentoAjusteProcessoCompraVO ultimoMov = ajusteVo.getObjetoCompraSelecionado().getMovimentos().stream()
                .filter(mov -> !mov.getTipoProcesso().isExecucaoContrato() && !mov.getTipoProcesso().isRequisicaoCompra() && !mov.getTipoProcesso().isContrato())
                .findFirst().orElse(null);

            if (ultimoMov != null && !ultimoMov.equals(movimentoVO)) {
                String complementoTexto = ultimoMov.getTipoProcesso().isContrato()
                    ? "Sendo assim, o contrato também terá seu objeto substituído."
                    : "Esse movimento deve ser a origem da substituição.";

                String mensagem = "<b>" + movimentoVO.getTipoProcesso().getDescricao() + "</b>"
                    + " e o objeto de compra " + ajusteVo.getObjetoCompraSelecionado().getObjetoCompra().getCodigo()
                    + " estão inseridos no movimento <b>" + ultimoMov.getTipoProcesso().getDescricao() + "</b> nº " + ultimoMov.getNumero()
                    + ". " + complementoTexto;

                ajusteVo.setMensagem(mensagem);
                ajusteVo.setMovimentoVO(ultimoMov);
                FacesUtil.executaJavaScript("dlgMovimentoOrigem.show()");
                FacesUtil.atualizarComponente("form-dlg-mov");
            }
        }
    }

    public void confirmarMovimentoOrigemSubstituicaoObjetoCompra() {
        try {
            if (ajusteVo.getTipoProcesso().isSolicitacaoCompra()
                || ajusteVo.getTipoProcesso().isCotacao()
                || ajusteVo.getTipoProcesso().isFormularioCotacao()
                || ajusteVo.getTipoProcesso().isIrp()) {

                ajusteVo.setTipoProcesso(ajusteVo.getMovimentoVO().getTipoProcesso());
                facade.recuperarObjetoProcesso(ajusteVo.getMovimentoVO().getTipoProcesso(), ajusteVo.getMovimentoVO().getIdProcesso(), ajusteVo);
            }
            FacesUtil.executaJavaScript("dlgMovimentoOrigem.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void listenerObjetoCompraPara() {
        try {
            ItemAjusteProcessoCompraVO objetoCompraSelecionado = ajusteVo.getObjetoCompraSelecionado();
            objetoCompraSelecionado.setGrupoContaDespesaPara(null);
            objetoCompraSelecionado.setEspecificacaoPara(null);
            if (objetoCompraSelecionado.hasObjetoCompraPara()) {
                objetoCompraSelecionado.setGrupoContaDespesaPara(facade.getGrupoContaDespesa(objetoCompraSelecionado.getObjetoCompraPara()));
                validarObjetoCompraPara();

                TabelaEspecificacaoObjetoCompraControlador controlador = (TabelaEspecificacaoObjetoCompraControlador) Util.getControladorPeloNome("tabelaEspecificacaoObjetoCompraControlador");
                controlador.recuperarObjetoCompra(objetoCompraSelecionado.getObjetoCompraPara());

                if (!ajusteVo.getTipoProcesso().isAdesaoExterna()) {
                    DotacaoSolicitacaoMaterial dotSolMat = facade.getDotacaoSolicitacaoMaterialFacade().recuperarDotacaoProcessoLicitatorio(ajusteVo.getIdProcesso());
                    ajusteVo.setTipoObjetoCompraDiferenteDaReservaInicialProcesso(dotSolMat != null && !objetoCompraSelecionado.getObjetoCompra().getTipoObjetoCompra().equals(objetoCompraSelecionado.getObjetoCompraPara().getTipoObjetoCompra()));
                }

            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarObjetoCompraPara() {
        ValidacaoException ve = new ValidacaoException();
        for (MovimentoAjusteProcessoCompraVO mov : ajusteVo.getObjetoCompraSelecionado().getMovimentos()) {
            if (mov.getTipoProcesso().isRequisicaoCompra()) {
                facade.recuperarObjetoProcesso(mov.getTipoProcesso(), mov.getIdProcesso(), ajusteVo);

                RequisicaoDeCompra requisicao = ajusteVo.getRequisicaoDeCompra();
                if (requisicao != null && requisicao.getSituacaoRequisicaoCompra().isEfetivada())
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido de/para com requisição de compra efetivada. Req: " + requisicao.getNumero() + ".");
            }
            if (mov.getTipoProcesso().isExecucaoContrato()) {
                if (ajusteVo.getObjetoCompraSelecionado().getObjetoCompraPara() != null
                    && !ajusteVo.getObjetoCompraSelecionado().getObjetoCompra().getTipoObjetoCompra().equals(ajusteVo.getObjetoCompraSelecionado().getObjetoCompraPara().getTipoObjetoCompra())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido de/para com Tipos de Objeto diferentes em processos com execução.");
                    break;
                }

                GrupoContaDespesa grupoContaDespesaPara = ajusteVo.getObjetoCompraSelecionado().getGrupoContaDespesaPara();
                GrupoContaDespesa grupoContaDespesaDe = ajusteVo.getObjetoCompraSelecionado().getGrupoContaDespesaDe();

                if (grupoContaDespesaPara != null
                    && !Util.isListNullOrEmpty(grupoContaDespesaPara.getContasDespesa())
                    && mov.getContaDesdobrada() != null) {

                    if (grupoContaDespesaPara.getContasDespesa()
                        .stream()
                        .noneMatch(conta -> conta.getCodigo().equals(mov.getContaDesdobrada().getCodigo()))) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido de/para com desdobramento diferente do empenho em processos com execução.");
                        break;
                    }
                }
                if (grupoContaDespesaPara != null
                    && grupoContaDespesaDe != null
                    && grupoContaDespesaDe.getIdGrupo() != null
                    && grupoContaDespesaPara.getIdGrupo() != null
                    && !grupoContaDespesaDe.getIdGrupo().equals(grupoContaDespesaPara.getIdGrupo())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido de/para com grupos diferentes em processos com execução.");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    public boolean hasProcessoComPregaoPorLote() {
        if (ajusteVo.hasObjetoCompraSelecionado()) {
            for (MovimentoAjusteProcessoCompraVO movimento : ajusteVo.getObjetoCompraSelecionado().getMovimentos()) {
                if (movimento.getTipoProcesso().isLicitacao()) {
                    Licitacao licitacao = facade.getLicitacaoFacade().recuperarSomenteLicitacao(movimento.getIdProcesso());
                    return licitacao != null && licitacao.getTipoApuracao().isPorLote();
                }
            }
        }
        return false;
    }

    public boolean hasMovimentoContrato() {
        if (ajusteVo.hasObjetoCompraSelecionado()) {
            for (MovimentoAjusteProcessoCompraVO movimento : ajusteVo.getObjetoCompraSelecionado().getMovimentos()) {
                if (movimento.getTipoProcesso().isContrato()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void cancelarObjetoCompra() {
        if (ajusteVo.hasObjetoCompraSelecionado()) {
            ajusteVo.getObjetoCompraSelecionado().setObjetoCompraPara(null);
            ajusteVo.getObjetoCompraSelecionado().setEspecificacaoPara(null);
            ajusteVo.getObjetoCompraSelecionado().setGrupoContaDespesaPara(null);
            ajusteVo.getObjetoCompraSelecionado().setUnidadeMedidaPara(null);
            ajusteVo.getObjetoCompraSelecionado().setQuantidadePara(null);
        }
    }

    public void novoComponente(FiltroHistoricoProcessoLicitatorio filtro) {
        this.filtro = filtro;
        if (this.filtro != null && Util.isListNullOrEmpty(ajustes)) {
            buscarAjustesComponente();
        }
    }

    public void buscarAjustesComponente() {
        ajustes = facade.buscarAjustesVO(filtro.getIdMovimento());
        if (!ajustes.isEmpty()) {
            FacesUtil.executaJavaScript("atualizaTabelaAjuste()");
        }
        filtro = null;
    }

    public List<AjusteProcessoCompraVO> getAjustes() {
        return ajustes;
    }

    public void setAjustes(List<AjusteProcessoCompraVO> ajustes) {
        this.ajustes = ajustes;
    }
}
