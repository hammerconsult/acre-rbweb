package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemFormularioCompraVO;
import br.com.webpublico.entidadesauxiliares.LoteFormularioCompraVO;
import br.com.webpublico.entidadesauxiliares.ValidacaoObjetoCompraEspecificacao;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ObjetoCompraFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
public class FormularioCompraControlador implements Serializable {

    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    private List<LoteFormularioCompraVO> lotesVO;
    private LoteFormularioCompraVO loteFormularioVO;
    private ItemFormularioCompraVO itemFormularioVO;
    private Object selecionado;
    private TipoMovimentoProcessoLicitatorio tipoProcesso;
    private Boolean editandoItem;

    public void preRenderComponente(List<LoteFormularioCompraVO> lotes, Object selecionado, TipoMovimentoProcessoLicitatorio tipoProcesso) {
        this.lotesVO = lotes;
        this.selecionado = selecionado;
        this.tipoProcesso = tipoProcesso;
    }

    public void novoLote() {
        try {
            validarNovoLote();
            loteFormularioVO = new LoteFormularioCompraVO();
            loteFormularioVO.setNumero(getProximoNumeroLote());
            loteFormularioVO.setDescricao("Lote " + loteFormularioVO.getNumero());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarLote() {
        loteFormularioVO = null;
    }

    public void adicionarLote() {
        try {
            validarCamposObrigatoriosLote();

            boolean editandoLote = lotesVO.stream().anyMatch(loteExistente -> loteExistente.getNumero().equals(loteFormularioVO.getNumero()));
            if (!editandoLote) {
                Util.adicionarObjetoEmLista(lotesVO, loteFormularioVO);
                criarLoteDoSelecionado();
            }
            novoItem(loteFormularioVO);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    public void editarLote(LoteFormularioCompraVO lote) {
        try {
            loteFormularioVO = lote;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerLote(LoteFormularioCompraVO loteVO) {
        try {
            validarRemocaoLote(loteVO);
            lotesVO.remove(loteVO);
            removerLoteDoSelecionado(loteVO);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void removerLoteDoSelecionado(LoteFormularioCompraVO loteVO) {
        switch (tipoProcesso) {
            case IRP:
                getIrp().getLotes().remove(loteVO.getLoteIRP());
                break;
            case FORMULARIO_COTACAO:
                getFormularioCotacao().getLotesFormulario().remove(loteVO.getLoteFormulario());
                break;
            case COTACAO:
                getCotacao().getLotes().remove(loteVO.getLoteCotacao());
                break;
        }
    }

    private void criarLoteDoSelecionado() {
        switch (tipoProcesso) {
            case IRP:
                LoteIntencaoRegistroPreco loteIRP = LoteIntencaoRegistroPreco.fromVO(loteFormularioVO, getIrp());
                loteFormularioVO.setLoteIRP(loteIRP);
                Util.adicionarObjetoEmLista(getIrp().getLotes(), loteIRP);
                break;
            case FORMULARIO_COTACAO:
                LoteFormularioCotacao loteFormulario = LoteFormularioCotacao.fromVO(loteFormularioVO, getFormularioCotacao());
                loteFormularioVO.setLoteFormulario(loteFormulario);
                Util.adicionarObjetoEmLista(getFormularioCotacao().getLotesFormulario(), loteFormulario);
                break;
            case COTACAO:
                LoteCotacao loteCotacao = LoteCotacao.fromVO(loteFormularioVO, getCotacao());
                loteFormularioVO.setLoteCotacao(loteCotacao);
                Util.adicionarObjetoEmLista(getCotacao().getLotes(), loteCotacao);
                break;
        }
    }

    public void editarItem(ItemFormularioCompraVO item) {
        try {
            itemFormularioVO = item;
            validarEdicaoOrRemocaoItem();
            editandoItem = true;
            if (!item.getTipoBeneficio().isParticipacaoExclusiva()) {
                if (item.hasQuantidadeSemBeneficio() && item.hasQuantidadeCotaReservada()) {
                    item.setQuantidade(item.getQuantidadeSemBenecifio().add(item.getQuantidadeCotaReservadaME()));
                }

                Map<ItemFormularioCompraVO, ItemFormularioCompraVO> map = ItemFormularioCompraVO.getMapItemSemBeneficioItemCotaReservada(lotesVO);
                ItemFormularioCompraVO itemCotaReservada = map.get(item);
                if (itemCotaReservada == null) {
                    item.setQuantidadeCotaReservadaME(BigDecimal.ZERO);
                    item.setQuantidadeSemBenecifio(item.getQuantidade());
                }
            }
        } catch (ValidacaoException ve) {
            novoItem(loteFormularioVO);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerItem(ItemFormularioCompraVO itemVO) {
        try {
            loteFormularioVO.getItens().remove(itemVO);
            removerItemLoteDoSelecionado(itemVO);
            novoItem(loteFormularioVO);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void removerItemLoteDoSelecionado(ItemFormularioCompraVO itemVO) {
        switch (tipoProcesso) {
            case IRP:
                validarEdicaoOrRemocaoItem();
                loteFormularioVO.getLoteIRP().getItens().remove(itemVO.getItemIRP());
                break;
            case FORMULARIO_COTACAO:
                loteFormularioVO.getLoteFormulario().getItensLoteFormularioCotacao().remove(itemVO.getItemFormulario());
                break;
            case COTACAO:
                loteFormularioVO.getLoteCotacao().getItens().remove(itemVO.getItemCotacao());
                break;
        }
    }


    public void atribuirTipoBeneficio() {
        itemFormularioVO.setTipoBeneficio(itemFormularioVO.getExclusivoME() ? TipoBeneficio.PARTICIPACAO_EXCLUSIVA : TipoBeneficio.SEM_BENEFICIO);
        itemFormularioVO.setQuantidade(BigDecimal.ZERO);
        itemFormularioVO.setQuantidadeSemBenecifio(BigDecimal.ZERO);
        itemFormularioVO.setQuantidadeCotaReservadaME(BigDecimal.ZERO);
        itemFormularioVO.setValorTotal(BigDecimal.ZERO);
    }

    public void novoItem(LoteFormularioCompraVO lote) {
        try {
            loteFormularioVO = lote;
            validarCamposObrigatoriosLote();
            itemFormularioVO = new ItemFormularioCompraVO();
            itemFormularioVO.setLoteFormularioCompraVO(loteFormularioVO);
            itemFormularioVO.setNumero(getProximoNumeroItem());
            if (loteFormularioVO.getTipoControle() != null) {
                itemFormularioVO.setTipoControle(loteFormularioVO.getTipoControle());
            }
            editandoItem = false;
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    public void validarRemocaoLote(LoteFormularioCompraVO loteVO) {
        ValidacaoException ve = new ValidacaoException();
        if (loteVO.getLoteIRP() != null
            && loteVO.getLoteIRP().getId() != null
            && getIrp().hasMaisDeUmParticipante()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A irp já possui participantes além do gerenciador, não será permitido remover este lote.");
        }

        ve.lancarException();
    }

    public void validarEdicaoOrRemocaoItem() {
        ValidacaoException ve = new ValidacaoException();
        if (itemFormularioVO.getItemIRP() != null
            && itemFormularioVO.getItemIRP().getId() != null
            && getIrp().hasMaisDeUmParticipante()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A irp já possui participantes além do gerenciador, não será permitido editar este item.");
        }
        if (itemFormularioVO.getItemCotacao() != null
            && itemFormularioVO.getItemCotacao().getObjetoCompra().isObjetoValorReferencia()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido editar um item com o tipo de cotação <b>\"Valor de Referência\"</b>. Sua edição é permitida somente no formulário de cotação.");
        }
        ve.lancarException();
    }

    private Integer getProximoNumeroItem() {
        return lotesVO.stream()
            .flatMap(lote -> lote.getItens().stream()).map(ItemFormularioCompraVO::getNumero).max(Integer::compare).orElse(0) + 1;
    }


    public void validarCamposObrigatoriosLote() {
        ValidacaoException ex = new ValidacaoException();
        Util.validarCamposObrigatorios(loteFormularioVO, ex);
        if (loteFormularioVO.getNumero() == null) {
            ex.adicionarMensagemDeCampoObrigatorio("O campo número deve ser informado.");
        }
        if (loteFormularioVO.getDescricao() == null) {
            ex.adicionarMensagemDeCampoObrigatorio("O campo descrição deve ser informado.");
        }
        if (isApuracaoPorLote() && loteFormularioVO.getTipoControle() == null) {
            ex.adicionarMensagemDeCampoObrigatorio("O campo tipo de controle deve ser informado");
        }
        ex.lancarException();

        if (hasLoteVO()) {
            lotesVO.stream()
                .filter(lote -> !lote.equals(loteFormularioVO) && lote.getDescricao().trim().equalsIgnoreCase(loteFormularioVO.getDescricao().trim()))
                .map(lote -> "Já foi adicionado um lote com a descrição '" + loteFormularioVO.getDescricao() + "'. ")
                .forEach(ex::adicionarMensagemDeOperacaoNaoPermitida);
        }
        ex.lancarException();
    }

    public void calcularPercentualCotaReseravadaME() {
        BigDecimal qtdeSemBeneficio = itemFormularioVO.hasQuantidadeSemBeneficio() ? itemFormularioVO.getQuantidadeSemBenecifio() : BigDecimal.ZERO;
        BigDecimal qtdeCotaRes = itemFormularioVO.hasQuantidadeCotaReservada() ? itemFormularioVO.getQuantidadeCotaReservadaME() : BigDecimal.ZERO;
        BigDecimal percentual = BigDecimal.ZERO;

        if (qtdeCotaRes.compareTo(BigDecimal.ZERO) > 0) {
            percentual = qtdeCotaRes.divide(qtdeSemBeneficio, 8, RoundingMode.HALF_EVEN).multiply(ItemFormularioCompraVO.CEM).setScale(2, RoundingMode.HALF_EVEN);
            itemFormularioVO.setPercentualCotaReservada(percentual);
        }

        if (percentual.compareTo(ItemFormularioCompraVO.MAX_PERCENTUAL_COTA_RESERVADA) > 0) {
            FacesUtil.addOperacaoNaoPermitida("O percentual para cota reservada ME/EPP deve ser no máximo " + ItemFormularioCompraVO.MAX_PERCENTUAL_COTA_RESERVADA + "%.");
            itemFormularioVO.setPercentualCotaReservada(ItemFormularioCompraVO.MAX_PERCENTUAL_COTA_RESERVADA);

            qtdeCotaRes = qtdeSemBeneficio.multiply(itemFormularioVO.getPercentualCotaReservada().divide(ItemFormularioCompraVO.CEM, 2, RoundingMode.HALF_EVEN));
            itemFormularioVO.setQuantidadeCotaReservadaME(qtdeCotaRes);
        }
        BigDecimal qtdeTotal = qtdeSemBeneficio.add(qtdeCotaRes);
        itemFormularioVO.setQuantidade(qtdeTotal);
    }

    public void adicionarItem() {
        try {
            validarCamposObrigatoriosItem();
            validarDuplicidadeItem();
            atribuirQuantidadeUmParaControlePorValor();
            criarItemCotaReservadaME();
            Util.adicionarObjetoEmLista(loteFormularioVO.getItens(), itemFormularioVO);
            loteFormularioVO.calcularValorTotal();
            criarItemDoSelecionado();
            novoItem(loteFormularioVO);
            Collections.sort(loteFormularioVO.getItens());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void atribuirQuantidadeUmParaControlePorValor() {
        BigDecimal quantidade = itemFormularioVO.getTipoBeneficio().isSemBeneficio() ? itemFormularioVO.getQuantidadeSemBenecifio() : itemFormularioVO.getQuantidade();
        itemFormularioVO.setQuantidade(itemFormularioVO.isTipoControlePorValor() ? BigDecimal.ONE : quantidade);
    }

    private void criarItemCotaReservadaME() {
        Map<ItemFormularioCompraVO, ItemFormularioCompraVO> map = ItemFormularioCompraVO.getMapItemSemBeneficioItemCotaReservada(lotesVO);
        ItemFormularioCompraVO itemCotaRes = map.get(itemFormularioVO);
        if (itemCotaRes != null) {
            loteFormularioVO.getItens().remove(itemCotaRes);
        }

        if (itemFormularioVO.hasQuantidadeCotaReservada()) {
            ItemFormularioCompraVO novoItemCotaReserv = new ItemFormularioCompraVO();
            novoItemCotaReserv.setLoteFormularioCompraVO(loteFormularioVO);
            novoItemCotaReserv.setDescricaoComplementar(itemFormularioVO.getDescricaoComplementar());
            novoItemCotaReserv.setObjetoCompra(itemFormularioVO.getObjetoCompra());
            novoItemCotaReserv.setTipoControle(itemFormularioVO.getTipoControle());
            novoItemCotaReserv.setUnidadeMedida(itemFormularioVO.getUnidadeMedida());
            novoItemCotaReserv.setQuantidade(novoItemCotaReserv.isTipoControlePorValor() ? BigDecimal.ONE : itemFormularioVO.getQuantidadeCotaReservadaME());
            novoItemCotaReserv.setItemReferencialCotaReserv(itemFormularioVO);
            novoItemCotaReserv.setNumero(itemFormularioVO.getNumero() + 1);
            novoItemCotaReserv.setTipoBeneficio(TipoBeneficio.COTA_RESERVADA);
            Util.adicionarObjetoEmLista(loteFormularioVO.getItens(), novoItemCotaReserv);
        }
    }

    private void atribuirGrupoContaDespesa(ItemFormularioCompraVO item) {
        if (item != null && item.getObjetoCompra() != null) {
            item.getObjetoCompra().setGrupoContaDespesa(objetoCompraFacade.criarGrupoContaDespesa(item.getObjetoCompra().getTipoObjetoCompra(), item.getObjetoCompra().getGrupoObjetoCompra()));
        }
    }

    private void validarCamposObrigatoriosItem() {
        ValidacaoException ex = new ValidacaoException();
        if (itemFormularioVO.getNumero() == null) {
            ex.adicionarMensagemDeCampoObrigatorio("O campo número deve ser informado.");
        }
        if (itemFormularioVO.getTipoControle() == null) {
            ex.adicionarMensagemDeCampoObrigatorio("O campo tipo de controle deve ser informado.");
        }
        if (itemFormularioVO.getTipoBeneficio() == null) {
            ex.adicionarMensagemDeCampoObrigatorio("O campo tipo de benefício deve ser informado.");
        }
        if (itemFormularioVO.getObjetoCompra() == null) {
            ex.adicionarMensagemDeCampoObrigatorio("O campo objeto de compra deve ser informado.");
        }
        if (itemFormularioVO.getUnidadeMedida() == null) {
            ex.adicionarMensagemDeCampoObrigatorio("O campo unidade de medida deve ser informado.");
        }
        ex.lancarException();

        if (itemFormularioVO.getNumero() <= 0) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("O campo Número deve ser maior que 0(zero).");
        }
        if (itemFormularioVO.isTipoControlePorQuantidade() && !itemFormularioVO.hasQuantidadeTotal()) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("O campo quantidade deve ser informado com um valor maior que 0(zero).");
        }
        if (itemFormularioVO.isTipoControlePorValor()
            && isApuracaoPorItem()
            && (itemFormularioVO.getValorTotal() == null || itemFormularioVO.getValorTotal().compareTo(BigDecimal.ZERO) <= 0)) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("O campo valor do item deve ser informado com um valor maior que 0(zero).");
        }
        ex.lancarException();
        if (itemFormularioVO.getObjetoCompra().getGrupoContaDespesa() != null
            && itemFormularioVO.getObjetoCompra().getGrupoContaDespesa().getIdGrupo() == null
            && itemFormularioVO.getObjetoCompra().getTipoObjetoCompra().isPermanenteOrConsumo()) {
            itemFormularioVO.getObjetoCompra().getGrupoContaDespesa().lancarMensagens();
        }
    }

    private void validarDuplicidadeItem() {
        ValidacaoException ex = new ValidacaoException();
        if (loteFormularioVO.hasItens())

            loteFormularioVO.getItens().forEach(itemLista -> {
                validarNumeroRepetidoItem(itemFormularioVO, ex);
                ex.lancarException();

                if (!itemLista.equals(itemFormularioVO)) {
                    ValidacaoObjetoCompraEspecificacao validacao = new ValidacaoObjetoCompraEspecificacao();
                    validacao.setObjetoCompraSelecionado(itemFormularioVO.getObjetoCompra());
                    validacao.setObjetoCompraEmLista(itemLista.getObjetoCompra());

                    validacao.setEspeficicacaoSelecionada(itemFormularioVO.getDescricaoComplementar());
                    validacao.setEspeficicacaoEmLista(itemLista.getDescricaoComplementar());

                    validacao.setTipoBeneficioSelecionado(itemFormularioVO.getTipoBeneficio());
                    validacao.setTipoBeneficioEmLista(itemLista.getTipoBeneficio());

                    validacao.setNumeroLote(itemLista.getLoteFormularioCompraVO().getNumero());
                    validacao.setNumeroItem(itemLista.getNumero());
                    objetoCompraFacade.validarObjetoCompraAndEspecificacaoIguais(validacao);
                }
                ex.lancarException();
            });
    }

    public void renumerarAndOrdenarItensEmTela() {
        List<ItemFormularioCompraVO> itens = Lists.newArrayList();
        lotesVO.forEach(lote -> itens.addAll(lote.getItens()));

        for (ItemFormularioCompraVO item : itens) {
            int i = itens.indexOf(item);
            item.setNumero(i + 1);
        }
        renumerarAndOrdenarItensDoSelecionado();
    }

    private void renumerarAndOrdenarItensDoSelecionado() {
        switch (tipoProcesso) {
            case IRP:
                List<ItemIntencaoRegistroPreco> itensIRP = new ArrayList<>();
                for (LoteIntencaoRegistroPreco lote : getIrp().getLotes()) {
                    itensIRP.addAll(lote.getItens());
                }

                for (ItemIntencaoRegistroPreco item : itensIRP) {
                    int i = itensIRP.indexOf(item);
                    item.setNumero(i + 1);
                }
                break;
            case FORMULARIO_COTACAO:
                List<ItemLoteFormularioCotacao> itensFormulario = new ArrayList<>();
                for (LoteFormularioCotacao lote : getFormularioCotacao().getLotesFormulario()) {
                    itensFormulario.addAll(lote.getItensLoteFormularioCotacao());
                }

                for (ItemLoteFormularioCotacao item : itensFormulario) {
                    int i = itensFormulario.indexOf(item);
                    item.setNumero(i + 1);
                }
                break;
            case COTACAO:
                List<ItemCotacao> itensCotacao = new ArrayList<>();
                for (LoteCotacao lote : getCotacao().getLotes()) {
                    itensCotacao.addAll(lote.getItens());
                }

                for (ItemCotacao item : itensCotacao) {
                    int i = itensCotacao.indexOf(item);
                    item.setNumero(i + 1);
                }
                break;
        }
    }

    public void alterarNumeroItemList(ItemFormularioCompraVO item) {
        try {
            ValidacaoException ve = new ValidacaoException();
            validarNumeroRepetidoItem(item, ve);
        } catch (ValidacaoException ve) {
            item.setNumero(null);
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void validarNumeroRepetidoItem(ItemFormularioCompraVO item, ValidacaoException ve) {
        for (LoteFormularioCompraVO lote : lotesVO) {
            if (lote.getItens().stream().anyMatch(itemList -> !itemList.equals(item) && itemList.getNumero().equals(item.getNumero()))) {
                ve.adicionarMensagemDeCampoObrigatorio("Já existe um item adicionado com o número " + item.getNumero() + " para o lote nº " + lote.getNumero() + ".");
            }
        }
        ve.lancarException();
    }

    public void confirmarItens() {
        itemFormularioVO = null;
        cancelarLote();
        FacesUtil.atualizarComponente("pn-lotes-proc");
    }

    private void criarItemDoSelecionado() {
        Map<ItemFormularioCompraVO, ItemFormularioCompraVO> mapItemSemBenefItemCotaRes = ItemFormularioCompraVO.getMapItemSemBeneficioItemCotaReservada(lotesVO);

        loteFormularioVO.getItens()
            .stream()
            .filter(itemVO -> !itemVO.getTipoBeneficio().isCotaReservadaME())
            .forEach(itemVO -> {
                switch (tipoProcesso) {
                    case IRP:
                        LoteIntencaoRegistroPreco loteIRP = loteFormularioVO.getLoteIRP();

                        ItemIntencaoRegistroPreco itemIrp = ItemIntencaoRegistroPreco.fromVO(itemVO, loteIRP);
                        itemVO.setItemIRP(itemIrp);
                        Util.adicionarObjetoEmLista(loteIRP.getItens(), itemIrp);

                        ItemFormularioCompraVO itemCotaReservadaVO = mapItemSemBenefItemCotaRes.get(itemVO);
                        if (itemCotaReservadaVO != null) {
                            ItemIntencaoRegistroPreco novoItemCotaReservadaIrp = ItemIntencaoRegistroPreco.fromVO(itemCotaReservadaVO, loteIRP);
                            novoItemCotaReservadaIrp.setItemReferencialCotaReserv(itemIrp);

                            itemCotaReservadaVO.setItemIRP(novoItemCotaReservadaIrp);
                            Util.adicionarObjetoEmLista(loteIRP.getItens(), novoItemCotaReservadaIrp);
                        }
                        loteIRP.setValorTotal(loteFormularioVO.getValorTotal());
                        Util.adicionarObjetoEmLista(getIrp().getLotes(), loteIRP);
                        break;
                    case FORMULARIO_COTACAO:
                        LoteFormularioCotacao loteFormulario = loteFormularioVO.getLoteFormulario();

                        ItemLoteFormularioCotacao itemFormulario = ItemLoteFormularioCotacao.fromVO(itemVO, loteFormulario);
                        itemVO.setItemFormulario(itemFormulario);
                        Util.adicionarObjetoEmLista(loteFormulario.getItensLoteFormularioCotacao(), itemFormulario);

                        ItemFormularioCompraVO itemCotaResFormularioVO = mapItemSemBenefItemCotaRes.get(itemVO);
                        if (itemCotaResFormularioVO != null) {
                            ItemLoteFormularioCotacao itemCotaResFormulario = ItemLoteFormularioCotacao.fromVO(itemCotaResFormularioVO, loteFormulario);
                            itemCotaResFormulario.setItemReferencialCotaReserv(itemFormulario);

                            itemCotaResFormularioVO.setItemFormulario(itemCotaResFormulario);
                            Util.adicionarObjetoEmLista(loteFormulario.getItensLoteFormularioCotacao(), itemCotaResFormulario);
                        }
                        loteFormulario.setValorTotal(loteFormularioVO.getValorTotal());
                        Util.adicionarObjetoEmLista(getFormularioCotacao().getLotesFormulario(), loteFormulario);
                        break;
                    case COTACAO:
                        LoteCotacao loteCotacao = loteFormularioVO.getLoteCotacao();

                        ItemCotacao itemCotacao = ItemCotacao.fromVO(itemVO, loteCotacao);
                        itemVO.setItemCotacao(itemCotacao);
                        Util.adicionarObjetoEmLista(loteCotacao.getItens(), itemCotacao);

                        ItemFormularioCompraVO itemCotaResCotacaoVO = mapItemSemBenefItemCotaRes.get(itemVO);
                        if (itemCotaResCotacaoVO != null) {
                            ItemCotacao itemCotaResFormulario = ItemCotacao.fromVO(itemCotaResCotacaoVO, loteCotacao);
                            itemCotaResFormulario.setItemReferencialCotaReserv(itemCotacao);

                            itemCotaResCotacaoVO.setItemCotacao(itemCotaResFormulario);
                            Util.adicionarObjetoEmLista(loteCotacao.getItens(), itemCotaResFormulario);
                        }
                        loteCotacao.setValor(loteFormularioVO.getValorTotal());
                        Util.adicionarObjetoEmLista(getCotacao().getLotes(), loteCotacao);
                        break;
                }
            });
    }

    public void validarNovoLote() {
        switch (tipoProcesso) {
            case IRP:
                getIrp().validarCamposObrigatorios();
                break;
            case FORMULARIO_COTACAO:
                getFormularioCotacao().validarCamposObrigatorios();
                break;
            case COTACAO:
                getCotacao().validarCamposObrigatorios();
                break;
        }
    }

    public TipoApuracaoLicitacao getTipoApuracaoProcesso() {
        if (tipoProcesso != null) {
            switch (tipoProcesso) {
                case IRP:
                    return getIrp().getTipoApuracaoLicitacao();
                case FORMULARIO_COTACAO:
                    return getFormularioCotacao().getTipoApuracaoLicitacao();
                case COTACAO:
                    if (getCotacao().getFormularioCotacao() != null) {
                        return getCotacao().getFormularioCotacao().getTipoApuracaoLicitacao();
                    }
            }
        }
        return null;
    }

    public TipoSolicitacao getTipoObjeto() {
        if (tipoProcesso != null) {
            switch (tipoProcesso) {
                case IRP:
                    return getIrp().getTipoObjeto();
                case FORMULARIO_COTACAO:
                    return getFormularioCotacao().getTipoSolicitacao();
                case COTACAO:
                    if (getCotacao().getFormularioCotacao() != null) {
                        return getCotacao().getFormularioCotacao().getTipoSolicitacao();
                    }
            }
        }
        return null;
    }

    public void selecionarObjetoCompraConsultaEntidade(Long idObjeto) {
        if (idObjeto != null) {
            itemFormularioVO.setObjetoCompra(objetoCompraFacade.recuperar(idObjeto));
            atribuirGrupoContaDespesa(itemFormularioVO);

            TabelaEspecificacaoObjetoCompraControlador controlador = (TabelaEspecificacaoObjetoCompraControlador) Util.getControladorPeloNome("tabelaEspecificacaoObjetoCompraControlador");
            controlador.recuperarObjetoCompra(itemFormularioVO.getObjetoCompra());

            FacesUtil.executaJavaScript("dlgPesquisaObjComp.hide()");
            FacesUtil.atualizarComponente("Formulario:tab-view-geral:pn-lotes");
        }
    }

    public void limparEspecificacao() {
        itemFormularioVO.setDescricaoComplementar(null);
    }

    public void selecionarEspecificacao(ActionEvent evento) {
        ObjetoDeCompraEspecificacao especificacao = (ObjetoDeCompraEspecificacao) evento.getComponent().getAttributes().get("objeto");
        itemFormularioVO.setDescricaoComplementar(especificacao.getTexto());
    }

    public List<SelectItem> getTiposControle() {
        return Util.getListSelectItemSemCampoVazio(TipoControleLicitacao.values());
    }

    public IntencaoRegistroPreco getIrp() {
        return (IntencaoRegistroPreco) selecionado;
    }

    public FormularioCotacao getFormularioCotacao() {
        return (FormularioCotacao) selecionado;
    }

    public Cotacao getCotacao() {
        return (Cotacao) selecionado;
    }

    public Integer getProximoNumeroLote() {
        return hasLotes() ? (lotesVO.size() + 1) : 1;
    }

    public boolean isApuracaoPorLote() {
        return getTipoApuracaoProcesso() != null && getTipoApuracaoProcesso().isPorLote();
    }

    public boolean isApuracaoPorItem() {
        return getTipoApuracaoProcesso() != null && getTipoApuracaoProcesso().isPorItem();
    }

    public boolean hasLotes() {
        return !Util.isListNullOrEmpty(lotesVO);
    }

    public boolean hasLoteVO() {
        return loteFormularioVO != null;
    }

    public boolean hasItemLoteVO() {
        return itemFormularioVO != null;
    }

    public boolean habilitarItensDefinicaoValorUnitario() {
        if (!tipoProcesso.isCotacao()){
            return false;
        }
        return (getTipoObjeto() != null && getTipoObjeto().isObraServicoEngenharia())
            || (getCotacao().getFormularioCotacao() != null && getCotacao().getFormularioCotacao().isItemCotacaoPorValorReferencia());
    }

    public void calcularValorTotalItem(ItemFormularioCompraVO itemVO) {
        if (habilitarItensDefinicaoValorUnitario()
            && (itemVO.getValorUnitario() == null || itemVO.getValorUnitario().compareTo(BigDecimal.ZERO) <= 0)) {
            itemVO.setValorUnitario(BigDecimal.ZERO);
            itemVO.getItemCotacao().setValorUnitario(BigDecimal.ZERO);
            FacesUtil.addOperacaoNaoPermitida("O campo 'Valor Unitário (R$) deve ser informado com um valor maior que 0(zero).'");
            return;
        }
        if (itemVO.isTipoControlePorQuantidade()
            && getTipoApuracaoProcesso().isPorItem()
            && (itemVO.getQuantidade() == null || itemVO.getQuantidade().compareTo(BigDecimal.ZERO) <= 0)) {
            itemVO.setValorUnitario(BigDecimal.ZERO);
            itemVO.getItemCotacao().setValorUnitario(BigDecimal.ZERO);
            FacesUtil.addOperacaoNaoPermitida("O campo quantidade deve ser informado com um valor maior que 0(zero).");
            return;
        }
        itemVO.calcularValorTotal();
        itemVO.getItemCotacao().setQuantidade(itemVO.getQuantidade());
        itemVO.getItemCotacao().setValorUnitario(itemVO.getValorUnitario());
        itemVO.getItemCotacao().setValorTotal(itemVO.getValorTotal());

        itemVO.getLoteFormularioCompraVO().calcularValorTotal();
        itemVO.getLoteFormularioCompraVO().getLoteCotacao().setValor(itemVO.getLoteFormularioCompraVO().getValorTotal());
    }

    public List<LoteFormularioCompraVO> getLotesVO() {
        return lotesVO;
    }

    public void setLotesVO(List<LoteFormularioCompraVO> lotesVO) {
        this.lotesVO = lotesVO;
    }

    public LoteFormularioCompraVO getLoteFormularioVO() {
        return loteFormularioVO;
    }

    public void setLoteFormularioVO(LoteFormularioCompraVO loteFormularioVO) {
        this.loteFormularioVO = loteFormularioVO;
    }

    public ItemFormularioCompraVO getItemFormularioVO() {
        return itemFormularioVO;
    }

    public void setItemFormularioVO(ItemFormularioCompraVO itemFormularioVO) {
        this.itemFormularioVO = itemFormularioVO;
    }

    public Object getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Object selecionado) {
        this.selecionado = selecionado;
    }

    public TipoMovimentoProcessoLicitatorio getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoMovimentoProcessoLicitatorio tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public Boolean getEditandoItem() {
        return editandoItem;
    }

    public void setEditandoItem(Boolean editandoItem) {
        this.editandoItem = editandoItem;
    }
}
