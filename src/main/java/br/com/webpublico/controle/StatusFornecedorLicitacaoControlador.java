package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemStatusFornecedorLicitacaoVo;
import br.com.webpublico.entidadesauxiliares.LoteStatusFornecedorLicitacao;
import br.com.webpublico.entidadesauxiliares.StatusFornecedorLicitacaoVo;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.StatusLicitacaoException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.StatusFornecedorLicitacaoFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.collections.CollectionUtils;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 21/02/14
 * Time: 10:41
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "statusFornecedorLicitacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-adjudicacao-homologacao-licitacao", pattern = "/adjudicacao-homologacao-licitacao/novo/", viewId = "/faces/administrativo/licitacao/adjudicacaohomologacao/edita.xhtml"),
    @URLMapping(id = "editar-adjudicacao-homologacao-licitacao", pattern = "/adjudicacao-homologacao-licitacao/editar/#{statusFornecedorLicitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/adjudicacaohomologacao/edita.xhtml"),
    @URLMapping(id = "ver-adjudicacao-homologacao-licitacao", pattern = "/adjudicacao-homologacao-licitacao/ver/#{statusFornecedorLicitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/adjudicacaohomologacao/visualizar.xhtml"),
    @URLMapping(id = "listar-adjudicacao-homologacao-licitacao", pattern = "/adjudicacao-homologacao-licitacao/listar/", viewId = "/faces/administrativo/licitacao/adjudicacaohomologacao/lista.xhtml")
})
public class StatusFornecedorLicitacaoControlador extends PrettyControlador<StatusFornecedorLicitacao> implements Serializable, CRUD {

    @EJB
    private StatusFornecedorLicitacaoFacade facade;
    private Licitacao licitacao;
    private TipoExibicaoItens tipoExibicaoItens;
    private List<LicitacaoFornecedor> participantesLicitacao;
    private List<LoteStatusFornecedorLicitacao> lotesStatus;
    private List<ItemStatusFornecedorLicitacaoVo> itensStatus;
    private List<StatusFornecedorLicitacaoVo> statusFornecedores;
    private List<LoteStatusFornecedorLicitacao> lotesAdjudicadosHomologadosOutrosProcessos;
    private List<ItemStatusFornecedorLicitacaoVo> itensAdjudicadosHomologadosOutrosProcessos;

    public StatusFornecedorLicitacaoControlador() {
        super(StatusFornecedorLicitacao.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/adjudicacao-homologacao-licitacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "nova-adjudicacao-homologacao-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setDataSituacao(facade.getSistemaFacade().getDataOperacao());
        inicializarListas();
    }

    private void inicializarListas() {
        inicializarListasItensAndLotes();
        statusFornecedores = Lists.newArrayList();
        participantesLicitacao = Lists.newArrayList();
    }

    @URLAction(mappingId = "ver-adjudicacao-homologacao-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        inicializarListas();
        licitacao = selecionado.getLicitacaoFornecedor().getLicitacao();
        tipoExibicaoItens = TipoExibicaoItens.POR_ADJUDICACAO_HOMOLOGACAO;
        preencherLoteAndItensAdjudicadosHomologadosRealizadas();
        preencherLoteAndItensDesteStatusFornecedor();
    }

    @URLAction(mappingId = "editar-adjudicacao-homologacao-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        licitacao = selecionado.getLicitacaoFornecedor().getLicitacao();
        try {
            facade.getLicitacaoFacade().verificarStatusLicitacao(licitacao);
        } catch (StatusLicitacaoException se) {
            FacesUtil.printAllFacesMessages(se.getMensagens());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
    }

    @Override
    public void salvar() {
        try {
            validarCamposObrigatorios();
            validarIntencaoDeRecursoLicitacaoModalidadePregao();
            validarDataLicitacao();
            validarItensHabilitadosToAdjudicacaoOrHomologacao();
            facade.getLicitacaoFacade().verificarStatusLicitacao(licitacao);
            facade.salvarRetornando(selecionado, itensStatus);
            FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar());
            redireciona();
        } catch (StatusLicitacaoException se) {
            redireciona();
            FacesUtil.printAllFacesMessages(se.getMensagens());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void preencherLoteAndItensAdjudicadosHomologadosRealizadas() {
        List<StatusFornecedorLicitacao> listStatus = facade.buscarPorFornecedor(selecionado.getLicitacaoFornecedor());
        for (StatusFornecedorLicitacao statusFornecedor : listStatus) {
            criarLoteAndItensOutrosStatusFornecedorVo(statusFornecedor);
        }
        ordernarStatusFornecedor();
    }

    private void criarLoteAndItensOutrosStatusFornecedorVo(StatusFornecedorLicitacao statusFornecedor) {
        StatusFornecedorLicitacaoVo novoStatusVo = new StatusFornecedorLicitacaoVo(statusFornecedor);

        HashMap<LoteProcessoDeCompra, List<ItemStatusFornecedorLicitacao>> map = criarMapItensStatusFornPorLote(statusFornecedor);
        SituacaoItemProcessoDeCompra situacao = SituacaoItemProcessoDeCompra.getSituacaoPorStatusFornecedor(statusFornecedor.getTipoSituacao());
        for (Map.Entry<LoteProcessoDeCompra, List<ItemStatusFornecedorLicitacao>> entry : map.entrySet()) {
            LoteStatusFornecedorLicitacao novoLote = new LoteStatusFornecedorLicitacao(entry.getKey().getNumero(), entry.getKey().getDescricao(), situacao);

            for (ItemStatusFornecedorLicitacao itemStatus : entry.getValue()) {
                if (itemStatus.getItemProcessoCompra().getLoteProcessoDeCompra().equals(entry.getKey())) {
                    ItemStatusFornecedorLicitacaoVo novoItem = new ItemStatusFornecedorLicitacaoVo(itemStatus.getItemProcessoCompra(), novoLote, itemStatus.getValorUnitario(), itemStatus.getSituacao());
                    novoLote.getItens().add(novoItem);
                }
            }
            Collections.sort(novoLote.getItens());
            novoStatusVo.getLotes().add(novoLote);
            statusFornecedores.add(novoStatusVo);
        }
    }

    private void ordernarStatusFornecedor() {
        Collections.sort(statusFornecedores);
        for (StatusFornecedorLicitacaoVo status : statusFornecedores) {
            Collections.sort(status.getLotes());
            for (LoteStatusFornecedorLicitacao lote : status.getLotes()) {
                Collections.sort(lote.getItens());
            }
        }
    }

    private void preencherLoteAndItensDesteStatusFornecedor() {
        HashMap<LoteProcessoDeCompra, List<ItemStatusFornecedorLicitacao>> map = criarMapItensStatusFornPorLote(selecionado);

        for (Map.Entry<LoteProcessoDeCompra, List<ItemStatusFornecedorLicitacao>> entry : map.entrySet()) {
            SituacaoItemProcessoDeCompra situacao = SituacaoItemProcessoDeCompra.getSituacaoPorStatusFornecedor(selecionado.getTipoSituacao());
            LoteStatusFornecedorLicitacao novoLote = new LoteStatusFornecedorLicitacao(entry.getKey().getNumero(), entry.getKey().getDescricao(), situacao);

            for (ItemStatusFornecedorLicitacao itemStatus : entry.getValue()) {
                if (itemStatus.getItemProcessoCompra().getLoteProcessoDeCompra().equals(entry.getKey())) {
                    ItemStatusFornecedorLicitacaoVo novoItem = new ItemStatusFornecedorLicitacaoVo(itemStatus.getItemProcessoCompra(), novoLote, itemStatus.getValorUnitario(), itemStatus.getSituacao());
                    novoLote.getItens().add(novoItem);
                }
            }
            Collections.sort(novoLote.getItens());
            itensStatus.addAll(novoLote.getItens());
            lotesStatus.add(novoLote);
        }
        Collections.sort(lotesStatus);
    }

    public void processarMudancaTipoExibicaoItens() {
        switch (tipoExibicaoItens) {
            case POR_LOTE:
                if (lotesAdjudicadosHomologadosOutrosProcessos == null || lotesAdjudicadosHomologadosOutrosProcessos.isEmpty()) {
                    lotesAdjudicadosHomologadosOutrosProcessos = Lists.newArrayList();
                    for (StatusFornecedorLicitacaoVo status : statusFornecedores) {
                        lotesAdjudicadosHomologadosOutrosProcessos.addAll(status.getLotes());
                    }
                    Collections.sort(lotesAdjudicadosHomologadosOutrosProcessos);
                }
                break;
            case POR_ITEM:
                if (itensAdjudicadosHomologadosOutrosProcessos == null || itensAdjudicadosHomologadosOutrosProcessos.isEmpty()) {
                    itensAdjudicadosHomologadosOutrosProcessos = Lists.newArrayList();

                    Map<ItemStatusFornecedorLicitacaoVo, List<SituacaoItemProcessoDeCompra>> map = criarMapSituacoesPorItem();
                    for (Map.Entry<ItemStatusFornecedorLicitacaoVo, List<SituacaoItemProcessoDeCompra>> entry : map.entrySet()) {
                        ItemStatusFornecedorLicitacaoVo novoItem = null;
                        ItemStatusFornecedorLicitacaoVo itemMap = entry.getKey();
                        for (SituacaoItemProcessoDeCompra situacao : entry.getValue()) {
                            if (situacao.isAdjudicado()) {
                                novoItem = new ItemStatusFornecedorLicitacaoVo(
                                    itemMap.getItemProcessoCompra(), itemMap.getLoteStatus(),
                                    itemMap.getValorUnitario(), situacao);
                                novoItem.setItemProcessoCompraAdjudicado(itemMap.getItemProcessoCompra());
                            }
                            if (situacao.isHomologado()) {
                                novoItem.setItemProcessoCompraHomologado(itemMap.getItemProcessoCompra());
                            }
                        }
                        itensAdjudicadosHomologadosOutrosProcessos.add(novoItem);
                    }
                    Collections.sort(itensAdjudicadosHomologadosOutrosProcessos);
                }
                break;
        }
    }

    private Map<ItemStatusFornecedorLicitacaoVo, List<SituacaoItemProcessoDeCompra>> criarMapSituacoesPorItem() {
        Map<ItemStatusFornecedorLicitacaoVo, List<SituacaoItemProcessoDeCompra>> map = new HashMap<>();
        for (StatusFornecedorLicitacaoVo status : statusFornecedores) {
            for (LoteStatusFornecedorLicitacao lote : status.getLotes()) {
                for (ItemStatusFornecedorLicitacaoVo item : lote.getItens()) {
                    if (!map.containsKey(item)) {
                        map.put(item, new ArrayList<SituacaoItemProcessoDeCompra>());
                        map.get(item).add(item.getSituacao());
                    } else {
                        map.get(item).add(item.getSituacao());
                    }
                }
            }
        }
        return map;
    }


    private void validarItensHabilitadosToAdjudicacaoOrHomologacao() {
        if (getLicitacao().isPregao()) {
            if (!CollectionUtils.isEmpty(itensStatus)) {
                ValidacaoException ve = new ValidacaoException();
                for (ItemStatusFornecedorLicitacaoVo item : itensStatus) {
                    if (!isItemHabilitado(selecionado, item.getItemProcessoCompra())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O Item " + item.getItemProcessoCompra().getDescricao() + " não está habilitado para homologar/adjudicar ");
                    }
                }
                ve.lancarException();
            }
        }
    }

    private Boolean isItemHabilitado(StatusFornecedorLicitacao selecionado, ItemProcessoDeCompra itemProcessoCompra) {
        return facade.getLicitacaoFacade().isItemVencidoPorFornecedorStatusPregao(licitacao,
            selecionado.getLicitacaoFornecedor().getEmpresa(),
            TipoClassificacaoFornecedor.getHabilitados(),
            getSituacaoItemProcessoConformeOperacao(), itemProcessoCompra);
    }

    public void definirNumeroStatusFornecedorLicitacao() {
        selecionado.setNumero(facade.recuperaUltimoNumero(licitacao));
        buscarFornecedoresVencedoresLicitacao();
    }

    private void validarIntencaoDeRecursoLicitacaoModalidadePregao() {
        ValidacaoException ex = new ValidacaoException();

        if (licitacao != null) {
            Pregao pregao = facade.getLicitacaoFacade().licitacaDePregao(licitacao);
            if (pregao != null) {
                for (IntencaoDeRecurso intencaoDeRecurso : pregao.getListaDeIntencaoDeRecursos()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(intencaoDeRecurso.getDataIntencao());
                    calendar.add(Calendar.DAY_OF_MONTH, 3);
                    if (calendar.getTime().after(new Date())) {
                        ex.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A Licitação possui intenção de recurso até a data de " + DataUtil.getDataFormatada(calendar.getTime()));
                        break;
                    }
                }
            }
        }
        ex.lancarException();
    }

    private void validarCamposObrigatorios() {
        Util.validarCampos(selecionado);
        ValidacaoException ex = new ValidacaoException();
        if (licitacao == null) {
            ex.adicionarMensagemDeCampoObrigatorio("O campo Licitação deve ser informado.");
        }
        if (!hasItemSelecionado()) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("Selecione ao menos um item para realizar a operação!");
        }
        ex.lancarException();
    }

    private void validarDataLicitacao() {
        ValidacaoException ex = new ValidacaoException();
        if (selecionado.getDataSituacao().before(Util.getDataHoraMinutoSegundoZerado(licitacao.getAbertaEm()))) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("A data da operação da Adjudicação/Homologação não pode ser anterior a data de abertura da Licitação (" + DataUtil.getDataFormatada(licitacao.getAbertaEm()) + ")!");
        }
        ex.lancarException();
    }

    public boolean hasItemSelecionado() {
        boolean selecionado = false;
        for (ItemStatusFornecedorLicitacaoVo item : itensStatus) {
            if (item.getSelecionado()) {
                selecionado = true;
                break;
            }
        }
        return selecionado;
    }

    public List<Licitacao> completarLicitacoes(String filtro) {
        if (selecionado != null && selecionado.getTipoSituacao() != null) {
            return facade.getLicitacaoFacade().buscarLicitacoesPendentesDeAdjudicacaoHomologacao(filtro.trim(), selecionado.getTipoSituacao());
        }
        return new ArrayList<>();
    }

    public void buscarFornecedoresVencedoresLicitacao() {
        if (licitacao != null) {
            participantesLicitacao = facade.getLicitacaoFacade().getFornecedoresVencedoresHabilitados(licitacao);
        }
    }

    public void criarLoteAndItensStatus() {
        try {
            inicializarListasItensAndLotes();
            if (selecionado.getLicitacaoFornecedor() != null) {
                List<ItemPropostaFornecedor> itens = buscarItensLicitacaoFornecedorVencedorPorSituacao(getSituacaoItemProcessoConformeOperacao());
                validarItensProposta(itens);

                HashMap<LotePropostaFornecedor, List<ItemPropostaFornecedor>> map = criarMapItensPropostaPorLote(itens);

                for (Map.Entry<LotePropostaFornecedor, List<ItemPropostaFornecedor>> entry : map.entrySet()) {
                    SituacaoItemProcessoDeCompra situacaoLote = SituacaoItemProcessoDeCompra.getSituacaoPorStatusFornecedor(selecionado.getTipoSituacao());
                    LoteStatusFornecedorLicitacao novoLote = new LoteStatusFornecedorLicitacao(entry.getKey().getNumeroLote(), entry.getKey().getDescricaoLote(), situacaoLote);

                    for (ItemPropostaFornecedor itemForn : entry.getValue()) {
                        BigDecimal valorUnitario = facade.getItemPropostaFornecedorFacade().getValorUnitarioVencedorDoItemPropostaFornecedor(itemForn);
                        ItemStatusFornecedorLicitacaoVo item = new ItemStatusFornecedorLicitacaoVo(itemForn.getItemProcessoDeCompra(), novoLote, valorUnitario, itemForn.getItemProcessoDeCompra().getSituacaoItemProcessoDeCompra());
                        novoLote.getItens().add(item);
                    }
                    Collections.sort(novoLote.getItens());
                    itensStatus.addAll(novoLote.getItens());
                    lotesStatus.add(novoLote);
                }
                Collections.sort(lotesStatus);
                verificarItensNaoHabilitadosDoFornecedor();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void inicializarListasItensAndLotes() {
        lotesStatus = Lists.newArrayList();
        itensStatus = Lists.newArrayList();
    }

    private HashMap<LotePropostaFornecedor, List<ItemPropostaFornecedor>> criarMapItensPropostaPorLote(List<ItemPropostaFornecedor> itens) {
        HashMap<LotePropostaFornecedor, List<ItemPropostaFornecedor>> retorno = new HashMap<>();
        for (ItemPropostaFornecedor item : itens) {
            if (!retorno.containsKey(item.getLotePropostaFornecedor())) {
                retorno.put(item.getLotePropostaFornecedor(), new ArrayList<ItemPropostaFornecedor>());
                retorno.get(item.getLotePropostaFornecedor()).add(item);
            } else {
                retorno.get(item.getLotePropostaFornecedor()).add(item);
            }
        }
        return retorno;
    }

    private HashMap<LoteProcessoDeCompra, List<ItemStatusFornecedorLicitacao>> criarMapItensStatusFornPorLote(StatusFornecedorLicitacao statusFornecedor) {
        HashMap<LoteProcessoDeCompra, List<ItemStatusFornecedorLicitacao>> retorno = new HashMap<>();
        List<ItemStatusFornecedorLicitacao> itens = facade.buscarItens(statusFornecedor);
        for (ItemStatusFornecedorLicitacao item : itens) {
            LoteProcessoDeCompra loteProcessoCompra = item.getItemProcessoCompra().getLoteProcessoDeCompra();
            if (!retorno.containsKey(loteProcessoCompra)) {
                retorno.put(loteProcessoCompra, new ArrayList<ItemStatusFornecedorLicitacao>());
                retorno.get(loteProcessoCompra).add(item);
            } else {
                retorno.get(loteProcessoCompra).add(item);
            }
        }
        return retorno;
    }

    private void validarItensProposta(List<ItemPropostaFornecedor> itens) {
        ValidacaoException ve = new ValidacaoException();
        if (itens == null || itens.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Itens não encontrados para o registro de " + selecionado.getTipoSituacao().getDescricaoFuncional() + " do fornecedor: " + selecionado.getLicitacaoFornecedor() + ".");
        }
        ve.lancarException();
    }

    private void verificarItensNaoHabilitadosDoFornecedor() {
        List<ItemPropostaFornecedor> itensNaoHabilitados = facade.getLicitacaoFacade().getItensVencidosPeloFornecedorPorStatusNoPregao(selecionado.getLicitacaoFornecedor().getLicitacao(), selecionado.getLicitacaoFornecedor().getEmpresa(), TipoClassificacaoFornecedor.getNaoHabilitados(), Arrays.asList(SituacaoItemProcessoDeCompra.values()));
        if (!itensNaoHabilitados.isEmpty()) {
            FacesUtil.addAtencao("Foram encontrados itens não habilitados para este fornecedor: " + selecionado.getLicitacaoFornecedor() + ".");
        }
    }

    public List<ItemPropostaFornecedor> buscarItensLicitacaoFornecedorVencedorPorSituacao(SituacaoItemProcessoDeCompra situacaoItemProcessoDeCompra) {
        return facade.buscarItensVencidosFornecedorPorStatus(licitacao,
            selecionado.getLicitacaoFornecedor().getEmpresa(),
            TipoClassificacaoFornecedor.getHabilitados(), situacaoItemProcessoDeCompra);
    }

    private SituacaoItemProcessoDeCompra getSituacaoItemProcessoConformeOperacao() {
        if (TipoSituacaoFornecedorLicitacao.ADJUDICADA.equals(selecionado.getTipoSituacao())) {
            return SituacaoItemProcessoDeCompra.AGUARDANDO;
        }
        if (TipoSituacaoFornecedorLicitacao.HOMOLOGADA.equals(selecionado.getTipoSituacao())) {
            return SituacaoItemProcessoDeCompra.ADJUDICADO;
        }
        return SituacaoItemProcessoDeCompra.HOMOLOGADO;
    }

    public List<SelectItem> getTiposSituacao() {
        return Util.getListSelectItem(Arrays.asList(TipoSituacaoFornecedorLicitacao.values()));
    }

    public List<SelectItem> getTiposExibicaoItens() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoExibicaoItens object : TipoExibicaoItens.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public void desmarcarTodosOsItens() {
        for (ItemStatusFornecedorLicitacaoVo item : itensStatus) {
            item.setSelecionado(Boolean.FALSE);
            item.getLoteStatus().setSelecionado(Boolean.FALSE);
        }
    }

    public void marcarTodosOsItens() {
        for (ItemStatusFornecedorLicitacaoVo item : itensStatus) {
            item.setSelecionado(Boolean.TRUE);
            item.getLoteStatus().setSelecionado(Boolean.TRUE);
        }
    }

    public boolean todosOsItensMarcados() {
        if (itensStatus != null && !itensStatus.isEmpty()) {
            for (ItemStatusFornecedorLicitacaoVo item : itensStatus) {
                if (!item.getSelecionado()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void mascarItensDoLote(LoteStatusFornecedorLicitacao lote) {
        for (ItemStatusFornecedorLicitacaoVo item : lote.getItens()) {
            if (item.getLoteStatus().equals(lote)) {
                item.setSelecionado(Boolean.TRUE);
            }
        }
        lote.setSelecionado(Boolean.TRUE);
    }

    public void desmarcarItensDoLote(LoteStatusFornecedorLicitacao lote) {
        for (ItemStatusFornecedorLicitacaoVo item : lote.getItens()) {
            if (item.getLoteStatus().equals(lote)) {
                item.setSelecionado(Boolean.FALSE);
            }
        }
        lote.setSelecionado(Boolean.FALSE);
    }

    public void limparCampos() {
        inicializarListas();
        licitacao = null;
        selecionado.setLicitacaoFornecedor(null);
        selecionado.setNumero(null);
        selecionado.setMotivo(null);
    }

    @Override
    public void validarExclusaoEntidade() {
        ValidacaoException ve = new ValidacaoException();
        Licitacao licitacao = facade.getLicitacaoFacade().recuperar(selecionado.getLicitacaoFornecedor().getLicitacao().getId());
        if (!licitacao.isEmAndamento()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível excluir pois a Licitação não está Em Andamento.");
        }
        ve.lancarException();

        List<ItemStatusFornecedorLicitacao> itens = facade.buscarItensPorStatus(selecionado.getLicitacaoFornecedor().getLicitacao(),
            selecionado.getLicitacaoFornecedor().getEmpresa(),
            SituacaoItemProcessoDeCompra.HOMOLOGADO);
        if (!itens.isEmpty() && selecionado.isAdjudicada()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível excluir a Adjudicação antes de excluir a Homologação.");
        }
        ve.lancarException();
    }

    public enum TipoExibicaoItens {
        POR_ADJUDICACAO_HOMOLOGACAO("Por Adjudicação/Homologação"),
        POR_LOTE("Por Lote"),
        POR_ITEM("Por Item");
        private String descricao;

        TipoExibicaoItens(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public boolean isTipoExibicaoAdjudicacaoHomologacao() {
        return TipoExibicaoItens.POR_ADJUDICACAO_HOMOLOGACAO.equals(tipoExibicaoItens);
    }

    public boolean isTipoExibicaoPorLote() {
        return TipoExibicaoItens.POR_LOTE.equals(tipoExibicaoItens);
    }

    public boolean isTipoExibicaoPorItem() {
        return TipoExibicaoItens.POR_ITEM.equals(tipoExibicaoItens);
    }

    public List<LoteStatusFornecedorLicitacao> getLotesStatus() {
        return lotesStatus;
    }

    public void setLotesStatus(List<LoteStatusFornecedorLicitacao> lotesStatus) {
        this.lotesStatus = lotesStatus;
    }

    public List<ItemStatusFornecedorLicitacaoVo> getItensStatus() {
        return itensStatus;
    }

    public void setItensStatus(List<ItemStatusFornecedorLicitacaoVo> itensStatus) {
        this.itensStatus = itensStatus;
    }

    public List<LicitacaoFornecedor> getParticipantesLicitacao() {
        return participantesLicitacao;
    }

    public void setParticipantesLicitacao(List<LicitacaoFornecedor> participantesLicitacao) {
        this.participantesLicitacao = participantesLicitacao;
    }

    public List<StatusFornecedorLicitacaoVo> getStatusFornecedores() {
        return statusFornecedores;
    }

    public void setStatusFornecedores(List<StatusFornecedorLicitacaoVo> statusFornecedores) {
        this.statusFornecedores = statusFornecedores;
    }

    public TipoExibicaoItens getTipoExibicaoItens() {
        return tipoExibicaoItens;
    }

    public void setTipoExibicaoItens(TipoExibicaoItens tipoExibicaoItens) {
        this.tipoExibicaoItens = tipoExibicaoItens;
    }

    public List<LoteStatusFornecedorLicitacao> getLotesAdjudicadosHomologadosOutrosProcessos() {
        return lotesAdjudicadosHomologadosOutrosProcessos;
    }

    public void setLotesAdjudicadosHomologadosOutrosProcessos(List<LoteStatusFornecedorLicitacao> lotesAdjudicadosHomologadosOutrosProcessos) {
        this.lotesAdjudicadosHomologadosOutrosProcessos = lotesAdjudicadosHomologadosOutrosProcessos;
    }

    public List<ItemStatusFornecedorLicitacaoVo> getItensAdjudicadosHomologadosOutrosProcessos() {
        return itensAdjudicadosHomologadosOutrosProcessos;
    }

    public void setItensAdjudicadosHomologadosOutrosProcessos(List<ItemStatusFornecedorLicitacaoVo> itensAdjudicadosHomologadosOutrosProcessos) {
        this.itensAdjudicadosHomologadosOutrosProcessos = itensAdjudicadosHomologadosOutrosProcessos;
    }
}
