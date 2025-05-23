package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CardapioRequisicaoCompraFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-cardapio-requisicao", pattern = "/cardapio-requisicao-compra/novo/", viewId = "/faces/administrativo/materiais/alimentacao-escolar/cardapio-requisicao/edita.xhtml"),
    @URLMapping(id = "editar-cardapio-requisicao", pattern = "/cardapio-requisicao-compra/editar/#{cardapioRequisicaoCompraControlador.id}/", viewId = "/faces/administrativo/materiais/alimentacao-escolar/cardapio-requisicao/edita.xhtml"),
    @URLMapping(id = "listar-cardapio-requisicao", pattern = "/cardapio-requisicao-compra/listar/", viewId = "/faces/administrativo/materiais/alimentacao-escolar/cardapio-requisicao/lista.xhtml"),
    @URLMapping(id = "ver-cardapio-requisicao", pattern = "/cardapio-requisicao-compra/ver/#{cardapioRequisicaoCompraControlador.id}/", viewId = "/faces/administrativo/materiais/alimentacao-escolar/cardapio-requisicao/visualizar.xhtml"),
})
public class CardapioRequisicaoCompraControlador extends PrettyControlador<CardapioRequisicaoCompra> implements Serializable, CRUD {

    @EJB
    private CardapioRequisicaoCompraFacade facade;
    private CardapioRequisicaoCompraVO selecionadoVO;
    private List<GuiaDistribuicaoVO> guiasDistribuicaoVo;

    public CardapioRequisicaoCompraControlador() {
        super(CardapioRequisicaoCompra.class);
    }

    @Override
    @URLAction(mappingId = "novo-cardapio-requisicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionadoVO = new CardapioRequisicaoCompraVO();
        selecionadoVO.setOperacao(Operacoes.NOVO);
    }

    @Override
    @URLAction(mappingId = "ver-cardapio-requisicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        selecionado.setRequisicaoCompra(facade.recuperarRequisicaoCompra(selecionado.getRequisicaoCompra()));
        Collections.sort(selecionado.getGuiasDistribuicao());
        guiasDistribuicaoVo = facade.buscarGuiaDistribuicaoVO(
            selecionado,
            SituacaoRequisicaoMaterial.NAO_AVALIADA, SituacaoRequisicaoMaterial.TRANSFERENCIA_TOTAL_CONCLUIDA);
    }

    @Override
    @URLAction(mappingId = "editar-cardapio-requisicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        selecionadoVO = new CardapioRequisicaoCompraVO();
        selecionadoVO.setCardapio(selecionado.getCardapio());
        selecionadoVO.setOperacao(Operacoes.EDITAR);
        if (!selecionado.getRequisicaoCompra().isEmElaboracao()) {
            FacesUtil.addOperacaoNaoPermitida("Já foi realizado a entrada por compra para a requisição " + selecionado.getRequisicaoCompra().getNumero() + ".");
            redirecionarParaVerOrEditar(selecionado.getId(), "ver");
        }
        recuperarLocalEstoquePai();
        selecionado.setRequisicaoCompra(facade.recuperarRequisicaoCompra(selecionado.getRequisicaoCompra()));
        gerarGuiaDistribuicao();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cardapio-requisicao-compra/";
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
    public void salvar() {
        try {
            validarSalvar();
            facade.salvarCardapioRequisicao(selecionadoVO);
            FacesUtil.addOperacaoRealizada("Requisições Geradas com Sucesso.");
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarSalvar() {
        validarCardapio();
        ValidacaoException ve = new ValidacaoException();
        if (Util.isListNullOrEmpty(selecionadoVO.getRequisicoes())) {
            ve.adicionarMensagemDeCampoObrigatorio("Para continuar, é necessário gerar as guias de distribuição materiais.");
        }
        ve.lancarException();
        validarDadosRequisicaoCompra();
    }

    private void validarCardapio() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionadoVO.getCardapio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo cardápio deve ser informado.");
        }
        if (selecionadoVO.getLocalEstoquePai() == null){
            ve.adicionarMensagemDeCampoObrigatorio("Local de estoque pai não encontrado para a geração das guias de distribuição.");
        }
        ve.lancarException();
    }

    public List<Cardapio> completarCardapio(String parte) {
        return facade.getCardapioFacade().buscarCardapio(parte.trim());
    }

    public void limparDadosCardapio() {
        limparListas();
        selecionadoVO.setCardapio(null);
    }

    public void limparListas() {
        selecionadoVO.setItensRequisicao(Lists.newArrayList());
        selecionadoVO.setRequisicoes(Lists.newArrayList());
    }

    private void validarDadosRequisicaoCompra() {
        ValidacaoException ve = new ValidacaoException();
        boolean controle = false;
        for (RequisicaoCompraGuiaVO req : selecionadoVO.getRequisicoes()) {
            controle = req.getItensRequisicao().stream().anyMatch(RequisicaoCompraGuiaItemVO::hasQuantidadeRequisicao);
            if (controle) break;
        }
        if (!controle) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar, Faça a distribuição das guias materiais.");
        }
        ve.lancarException();

        for (RequisicaoCompraGuiaVO reqVo : selecionadoVO.getRequisicoes()) {
            RequisicaoDeCompra requisicao = reqVo.getRequisicaoCompra();
            if (reqVo.getItensRequisicao().stream().anyMatch(RequisicaoCompraGuiaItemVO::hasQuantidadeRequisicao)) {
                if (requisicao.getDataRequisicao() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo data da requisição deve ser informado.");
                }
                if (Strings.isNullOrEmpty(requisicao.getDescricao())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo descrição da requisição deve ser informado.");
                }
                if (Strings.isNullOrEmpty(requisicao.getDescricao())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo local de entrega da requisição deve ser informado.");
                }
            }
        }
        ve.lancarException();
    }


    public BigDecimal getQuantidadeDistribuida(ItemContrato itemContrato, RequisicaoCompraGuiaVO reqCompra) {
        BigDecimal qtdeDistribuida = BigDecimal.ZERO;
        for (GuiaDistribuicaoVO guia : reqCompra.getGuiasDistribuicao()) {
            qtdeDistribuida = qtdeDistribuida.add(guia.getItens().stream()
                .filter(itemGuia -> itemGuia.getItemReqCompra().getItemContrato().equals(itemContrato))
                .map(GuiaDistribuicaoItemVO::getQuantidade)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        return qtdeDistribuida;

    }

    public void atribuirQuantidadeDisponivelItemRequisicaoCompra(RequisicaoCompraGuiaItemVO itemReqVo, RequisicaoCompraGuiaVO reqCompra) {
        BigDecimal qtdeDistribuida = reqCompra != null
            ? getQuantidadeDistribuida(itemReqVo.getItemContrato(), reqCompra)
            : BigDecimal.ZERO;
        BigDecimal qtdeDisponivel = itemReqVo.getQuantidadeExecucao()
            .subtract(itemReqVo.getQuantidadeOutraRequisicao())
            .subtract(qtdeDistribuida);
        itemReqVo.setQuantidadeDisponivel(qtdeDisponivel);
    }

    public void movimentarItemGuiaAndItemRequisicaoCompra(GuiaDistribuicaoItemVO itemGuia, RequisicaoCompraGuiaVO reqVo, GuiaDistribuicaoVO guiaVo,
                                                          int indiceCont, int indiceGuia, int indiceItem) {
        atribuirQuantidadeDisponivelItemRequisicaoCompra(itemGuia.getItemReqCompra(), reqVo);
        if (itemGuia.hasQuantidade() && itemGuia.getItemReqCompra().getQuantidadeDisponivel().compareTo(BigDecimal.ZERO) < 0) {
            FacesUtil.addOperacaoNaoPermitida("A quantidade informada excede a quantidade disponível.");
            itemGuia.setQuantidade(BigDecimal.ZERO);
            atribuirQuantidadeDisponivelItemRequisicaoCompra(itemGuia.getItemReqCompra(), reqVo);
            return;
        }
        atribuirQuantidadeMovimentadaItemRequisicaoCompra(itemGuia, reqVo);

        GuiaDistribuicaoItemVO ultimoItemGuia = guiaVo.getItens().get(guiaVo.getItens().size() - 1);
        if (ultimoItemGuia.equals(itemGuia)) {
            indiceGuia = indiceGuia + 1;
            indiceItem = 0;
        } else {
            indiceItem = indiceItem + 1;
        }
        String s = "'Formulario:data-grid:" + indiceCont + ":tabela-guias:" + indiceGuia + ":tabela-itens:" + indiceItem + ":input-mask:input-masc-unid-med'";
        FacesUtil.executaJavaScript("setaFoco(" + s + ")");
    }

    public void atribuirQuantidadeMovimentadaItemRequisicaoCompra(GuiaDistribuicaoItemVO itemGuia, RequisicaoCompraGuiaVO reqVo) {
        BigDecimal quantidadeDistribuida = getQuantidadeDistribuida(itemGuia.getItemReqCompra().getItemContrato(), reqVo);
        itemGuia.getItemReqCompra().setQuantidadeRequisicao(quantidadeDistribuida);
    }

    public void recuperarLocalEstoquePai() {
        try {
            LocalEstoque localEstoque = facade.getCardapioFacade().buscarCardapioLocalEstoquePai(selecionadoVO.getCardapio());
            selecionadoVO.setLocalEstoquePai(localEstoque);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<SelectItem> getPerecibilidades() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, "Todas"));
        for (Perecibilidade obj : Perecibilidade.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    private void preencherQuantidadeExecutada() {
        for (RequisicaoCompraGuiaItemVO itemReqVo : selecionadoVO.getItensRequisicao()) {
            List<ExecucaoContratoItem> itensEx = selecionadoVO.getMapItemContratoItensExecucao().get(itemReqVo.getItemContrato());
            if (itensEx != null) {
                for (ExecucaoContratoItem itemEx : itensEx) {
                    itemReqVo.setQuantidadeExecucao(itemReqVo.getQuantidadeExecucao().add(itemEx.getQuantidadeUtilizada()));
                }
            }
        }
    }

    private void preencherMapItemContratoItensExecucao() {
        selecionadoVO.setMapItemContratoItensExecucao(Maps.newHashMap());
        for (RequisicaoCompraGuiaItemVO mat : selecionadoVO.getItensRequisicao()) {
            List<ExecucaoContratoItem> itensExecucao = facade.getContratoFacade().getExecucaoContratoFacade().buscarItensExecucaoContratoEmpenhado(mat.getItemContrato());
            selecionadoVO.getMapItemContratoItensExecucao().put(mat.getItemContrato(), Lists.newArrayList(itensExecucao));
        }
    }

    public void criarRequisicaoCompra() {
        selecionadoVO.setRequisicoes(Lists.newArrayList());

        Map<Contrato, List<RequisicaoCompraGuiaItemVO>> mapContrato = preencherMapContratoMateriais();
        for (Map.Entry<Contrato, List<RequisicaoCompraGuiaItemVO>> entry : mapContrato.entrySet()) {
            RequisicaoDeCompra novaReqCompra = isOperacaoNovo() ? novaRequicaoCompra(entry.getKey()) : selecionado.getRequisicaoCompra();
            RequisicaoCompraGuiaVO novaReqCompraVo = new RequisicaoCompraGuiaVO(novaReqCompra);

            if (selecionadoVO.getMaterialComSaldo()) {
                for (RequisicaoCompraGuiaItemVO itemReqVo : entry.getValue()) {
                    if (itemReqVo.hasQuantidadeDisponivel()) {
                        novaReqCompraVo.getItensRequisicao().add(itemReqVo);
                    }
                }
            } else {
                novaReqCompraVo.getItensRequisicao().addAll(entry.getValue());
            }
            selecionadoVO.getRequisicoes().add(novaReqCompraVo);
        }
    }

    public void gerarGuiaDistribuicao() {
        try {
            validarCardapio();
            limparListas();
            buscarMateriaisCardapio();
            preencherMapItemContratoItensExecucao();
            preencherQuantidadeExecutada();
            criarRequisicaoCompra();
            criarGuiaDistribuicao();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void buscarMateriaisCardapio() {
        if (selecionadoVO.getCardapio() != null) {
            selecionadoVO.setItensRequisicao(facade.criarRequisicaoCompraGuiaItemVO(getCondicaoBuscarMateriais()));
        }
    }

    private String getCondicaoBuscarMateriais() {
        HierarquiaOrganizacional hoAdm = facade.getCardapioFacade().getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            selecionadoVO.getCardapio().getUnidadeAdministrativa(),
            facade.getSistemaFacade().getDataOperacao());

        String condicao = " and card.id = " + selecionadoVO.getCardapio().getId();

        if (hoAdm != null) {
            condicao += " and vwha.codigo like '" + hoAdm.getCodigo().substring(0, 6) + "%'";
        }
        if (selecionadoVO.getPerecibilidade() != null) {
            condicao += " and mat.perecibilidade = '" + selecionadoVO.getPerecibilidade().name() + "'";
        }
        if (isOperacaoEditar()) {
            condicao += " and cont.id = " + selecionado.getRequisicaoCompra().getContrato().getId();
            condicao += " and crc.id = " + selecionado.getId();
        }
        return condicao;
    }

    private void criarGuiaDistribuicao() {
        try {
            for (RequisicaoCompraGuiaVO reqCompraVo : selecionadoVO.getRequisicoes()) {
                for (LocalEstoque leFilho : getLocaisEstoqueFilhos()) {
                    GuiaDistribuicaoVO novaGuiaDist = new GuiaDistribuicaoVO(leFilho);
                    novaGuiaDist.setEnderecoLocalEstoque(facade.getEnderecoLocalEstoque(leFilho));
                    atribuirRequisicaoMaterialGuiaVo(leFilho, novaGuiaDist);

                    for (RequisicaoCompraGuiaItemVO itemReqVo : reqCompraVo.getItensRequisicao()) {
                        GuiaDistribuicaoItemVO novoItemGuia = new GuiaDistribuicaoItemVO();
                        novoItemGuia.setItemReqCompra(itemReqVo);
                        Util.adicionarObjetoEmLista(novaGuiaDist.getItens(), novoItemGuia);

                        atribuirQuantidadeOutrasRequisicoes(leFilho, itemReqVo, novoItemGuia);
                    }
                    Util.adicionarObjetoEmLista(reqCompraVo.getGuiasDistribuicao(), novaGuiaDist);
                }
                Collections.sort(reqCompraVo.getGuiasDistribuicao());
                atualizarItemRequisicaoCompraEdicao(reqCompraVo);
            }

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void atribuirQuantidadeOutrasRequisicoes(LocalEstoque leFilho, RequisicaoCompraGuiaItemVO itemReqVo, GuiaDistribuicaoItemVO novoItemGuia) {
        if (isOperacaoNovo()) {
            BigDecimal qtdeUtilizadaItemReq = facade.getQuantidadeUtilizadaItemRequisicao(itemReqVo.getItemContrato(), itemReqVo.getItemRequisicaoCompra(), selecionado);
            itemReqVo.setQuantidadeOutraRequisicao(qtdeUtilizadaItemReq);
        } else {
            novoItemGuia.setQuantidade(facade.getQuantidadeItemGuia(itemReqVo.getMaterial(), itemReqVo.getItemContrato(), leFilho, selecionado));
            for (GuiaDistribuicaoRequisicao guia : selecionado.getGuiasDistribuicao()) {
                for (GuiaDistribuicaoReqItem itemGuia : guia.getItens()) {
                    if (itemGuia.getItemRequisicaoCompra().getItemContrato().equals(itemReqVo.getItemContrato())) {
                        BigDecimal qtdeUtilizadaItemReq = facade.getQuantidadeUtilizadaItemRequisicao(itemReqVo.getItemContrato(), itemGuia.getItemRequisicaoCompra(), selecionado);
                        itemReqVo.setQuantidadeOutraRequisicao(qtdeUtilizadaItemReq);
                    }
                }
            }
        }
    }

    private void atribuirRequisicaoMaterialGuiaVo(LocalEstoque leFilho, GuiaDistribuicaoVO novaGuiaDist) {
        Optional<GuiaDistribuicaoRequisicao> any = selecionado.getGuiasDistribuicao().stream().filter(guia -> guia.getRequisicaoMaterial().getLocalEstoqueDestino().equals(leFilho)).findAny();
        if (any.isPresent()) {
            GuiaDistribuicaoRequisicao guiaDist = any.get();
            novaGuiaDist.setRequisicaoMaterial(guiaDist.getRequisicaoMaterial());
            novaGuiaDist.setDescricao(guiaDist.getRequisicaoMaterial().getDescricao());
        }
    }

    private void atualizarItemRequisicaoCompraEdicao(RequisicaoCompraGuiaVO reqCompraVo) {
        if (isOperacaoEditar()) {
            reqCompraVo.getGuiasDistribuicao().stream()
                .flatMap(guia -> guia.getItens().stream())
                .forEach(itemGuia -> atribuirQuantidadeMovimentadaItemRequisicaoCompra(itemGuia, reqCompraVo));
        }
        reqCompraVo.getItensRequisicao().forEach(itemReqVo -> atribuirQuantidadeDisponivelItemRequisicaoCompra(itemReqVo, reqCompraVo));
    }

    private List<LocalEstoque> getLocaisEstoqueFilhos() {
        List<LocalEstoque> locaisEstoque = facade.getCardapioFacade().buscarCardapioLocalEstoqueFilhos(selecionadoVO.getCardapio());
        if (Util.isListNullOrEmpty(locaisEstoque)) {
            FacesUtil.addOperacaoNaoPermitida("Nenhum local de estoque encontrado para gerar as guias de distribuição.");
            return Lists.newArrayList();
        }
        if (selecionadoVO.getGuiaLocalEstoquePai() && !locaisEstoque.contains(selecionadoVO.getLocalEstoquePai())) {
            locaisEstoque.add(selecionadoVO.getLocalEstoquePai());
        }
        return locaisEstoque;
    }

    private Map<Contrato, List<RequisicaoCompraGuiaItemVO>> preencherMapContratoMateriais() {
        Map<Contrato, List<RequisicaoCompraGuiaItemVO>> mapContrato = Maps.newHashMap();
        for (RequisicaoCompraGuiaItemVO itemReqVo : selecionadoVO.getItensRequisicao()) {
            if (!mapContrato.containsKey(itemReqVo.getContrato())) {
                mapContrato.put(itemReqVo.getContrato(), Lists.newArrayList());
            }
            mapContrato.get(itemReqVo.getContrato()).add(itemReqVo);
        }
        return mapContrato;
    }

    private RequisicaoDeCompra novaRequicaoCompra(Contrato contrato) {
        RequisicaoDeCompra novaReq = new RequisicaoDeCompra();
        novaReq.setSituacaoRequisicaoCompra(SituacaoRequisicaoCompra.EM_ELABORACAO);
        novaReq.setTipoRequisicao(TipoRequisicaoCompra.CONTRATO);
        novaReq.setTipoObjetoCompra(TipoObjetoCompra.CONSUMO);
        novaReq.setDataRequisicao(facade.getSistemaFacade().getDataOperacao());
        novaReq.setContrato(contrato);
        novaReq.setDescricao("Requisição referente ao cardápio "
            + selecionadoVO.getCardapio().getNumero()
            + " - " + selecionadoVO.getCardapio().getProgramaAlimentacao().getNome() + " - " + selecionadoVO.getCardapio().getProgramaAlimentacao().getDescricao());
        return novaReq;
    }

    public boolean hasRequisicoes() {
        return !Util.isListNullOrEmpty(selecionadoVO.getRequisicoes());
    }

    public CardapioRequisicaoCompraVO getSelecionadoVO() {
        return selecionadoVO;
    }

    public void setSelecionadoVO(CardapioRequisicaoCompraVO selecionadoVO) {
        this.selecionadoVO = selecionadoVO;
    }

    public List<GuiaDistribuicaoVO> getGuiasDistribuicaoVo() {
        return guiasDistribuicaoVo;
    }

    public void setGuiasDistribuicaoVo(List<GuiaDistribuicaoVO> guiasDistribuicaoVo) {
        this.guiasDistribuicaoVo = guiasDistribuicaoVo;
    }
}
