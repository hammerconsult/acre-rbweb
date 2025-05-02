package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssociacaoDocumentoLiquidacaoEntradaPorCompra;
import br.com.webpublico.entidadesauxiliares.FiltroAssociacaoDocumentoFiscalLiquidacao;
import br.com.webpublico.entidadesauxiliares.VOLiquidacaoDocumentoFiscal;
import br.com.webpublico.enums.SituacaoEntradaMaterial;
import br.com.webpublico.enums.administrativo.SituacaoDocumentoFiscalEntradaMaterial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AssociacaoDocumentoLiquidacaoEntradaCompraFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMapping(id = "associacao-docto-liquidacao-entrada-compra", pattern = "/associacao-documento-liquidacao-entrada-compra/", viewId = "/faces/financeiro/orcamentario/associacao-documento-liquidacao-entrada-compra/edita.xhtml")

public class AssociacaoDocumentoLiquidacaoEntradaCompraControlador implements Serializable {

    @EJB
    private AssociacaoDocumentoLiquidacaoEntradaCompraFacade facade;
    private AssociacaoDocumentoLiquidacaoEntradaPorCompra selecionado;
    private LocalEstoque localEstoque;
    private SituacaoEntradaMaterial novaSituacao;

    @URLAction(mappingId = "associacao-docto-liquidacao-entrada-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        novaAssociacao();
    }

    public void salvar() {
        try {
            validarCamposObrigatorios();
            validarRegrasEspecificas();
            facade.salvarAssociacao(selecionado);
            FacesUtil.addOperacaoRealizada("Documento fiscal associado com sucesso!");
            novo();
            FacesUtil.atualizarComponente("Formulario");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void distribuirValorProporcionalAoGrupo() {
        try {
            validarDistribuicaoItens();
            facade.distribuirValorLiquidadoItenDoctoItemEntrada(selecionado);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    public void validarDistribuicaoItens() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.hasDocumentoEntradaSelecionado()) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um documento fiscal da entrada por compra para liberar o valor para ditribuir entre os itens.");
        }
        ve.lancarException();
    }

    public String getValorTotalLiquidadoDocumentoFiscalLiquidacaoAsString() {
        return "Valor Liquidado: " + Util.formataValor(selecionado.getValorTotalLiquidadoDocumentoFiscalLiquidacao());
    }

    public void novaAssociacao() {
        selecionado = new AssociacaoDocumentoLiquidacaoEntradaPorCompra();
        selecionado.setTipoAssociacao(AssociacaoDocumentoLiquidacaoEntradaPorCompra.TipoAssociacao.ASSOCIAR_DOCUMENTO);
    }

    public void limparDadosEntradaCompra() {
        selecionado.setDocumentosFiscaisLiquidacao(Lists.<VOLiquidacaoDocumentoFiscal>newArrayList());
        selecionado.setDocumentosFiscaisEntrada(Lists.<DoctoFiscalEntradaCompra>newArrayList());
        selecionado.setFiltros(Lists.<FiltroAssociacaoDocumentoFiscalLiquidacao>newArrayList());
        selecionado.setItensDocumentoEntrada(Lists.<ItemDoctoItemEntrada>newArrayList());
        selecionado.setDocumentoFiscalEntrada(null);
        selecionado.setEntradaCompraMaterial(null);
    }

    public List<SelectItem> getTipos() {
        return Util.getListSelectItemSemCampoVazio(AssociacaoDocumentoLiquidacaoEntradaPorCompra.TipoAssociacao.values());
    }

    public void cancelar() {
        FacesUtil.redirecionamentoInterno("/documento-fiscal/listar/");
    }

    public List<EntradaCompraMaterial> completarEntradaCompraMaterial(String parte) {
        SituacaoDocumentoFiscalEntradaMaterial[] situacoes = new SituacaoDocumentoFiscalEntradaMaterial[0];
        boolean isGerarNumeroSituacao = false;
        if (selecionado.isAssociarDocumento()) {
            situacoes = new SituacaoDocumentoFiscalEntradaMaterial[]{SituacaoDocumentoFiscalEntradaMaterial.AGUARDANDO_LIQUIDACAO};
        } else if (selecionado.isDesbloquearGrupo()) {
            situacoes = new SituacaoDocumentoFiscalEntradaMaterial[]{SituacaoDocumentoFiscalEntradaMaterial.LIQUIDADO, SituacaoDocumentoFiscalEntradaMaterial.LIQUIDADO_PARCIALMENTE};
        } else if (selecionado.isGerarNumeroSituacao()) {
            isGerarNumeroSituacao = true;
            situacoes = new SituacaoDocumentoFiscalEntradaMaterial[]{SituacaoDocumentoFiscalEntradaMaterial.LIQUIDADO};
        }
        return facade.getEntradaMaterialFacade().buscarEntradaCompraMaterialPorLocalEstoque(parte.trim(), localEstoque, isGerarNumeroSituacao, situacoes);
    }

    public void buscarDocumentoFiscalEntradaCompra() {
        try {
            selecionado.setDocumentoFiscalEntrada(null);
            List<DoctoFiscalEntradaCompra> documentos = facade.getEntradaMaterialFacade().buscarDoctoFiscalEntradaCompra(selecionado.getEntradaCompraMaterial());
            if (documentos.isEmpty()) {
                FacesUtil.addOperacaoNaoPermitida("Nenhum documento fiscal encontrado para a entrada por compra " + selecionado.getEntradaCompraMaterial().toString() + ".");
                return;
            }
            selecionado.setDocumentosFiscaisEntrada(documentos);
            setNovaSituacaoEntrada();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void buscarDocumentoFiscalNaoIntegradoComLiquidacao() {
        try {
            if (selecionado.hasDocumentoEntradaSelecionado()) {
                selecionado.setDocumentosFiscaisLiquidacao(facade.buscarDocumentoFiscalNaoIntegradoComLiquidacao(getCondicaoSqlDocumentoLiquidacao()));
            }
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void onRowSelectDocumento(SelectEvent event) {
        try {
            selecionado.setDocumentoFiscalEntrada((DoctoFiscalEntradaCompra) event.getObject());
            if (selecionado.isAssociarDocumento()) {
                DoctoFiscalLiquidacao docto = selecionado.getDocumentoFiscalEntrada().getDoctoFiscalLiquidacao();
                adicionarFiltrosPesquisaDocumentoLiquidacao(docto);
            }
            if (selecionado.isDesbloquearGrupo()) {
                selecionado.setItensDocumentoEntrada(facade.getEntradaMaterialFacade().buscarItemDoctoItemEntrada(selecionado.getDocumentoFiscalEntrada().getDoctoFiscalLiquidacao(), null));
                selecionado.setValorTotalLiquidadoDocumentoFiscalLiquidacao(facade.getDoctoFiscalLiquidacaoFacade().buscarValorLiquidado(selecionado.getDocumentoFiscalEntrada().getDoctoFiscalLiquidacao()));
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private void adicionarFiltrosPesquisaDocumentoLiquidacao(DoctoFiscalLiquidacao docto) {
        selecionado.setFiltros(Lists.<FiltroAssociacaoDocumentoFiscalLiquidacao>newArrayList());
        FiltroAssociacaoDocumentoFiscalLiquidacao fornecedor = new FiltroAssociacaoDocumentoFiscalLiquidacao("Fornecedor", selecionado.getEntradaCompraMaterial().getRequisicaoDeCompra().getFornecedor().toString(), " where emp.fornecedor_id = " + selecionado.getEntradaCompraMaterial().getRequisicaoDeCompra().getFornecedor().getId());
        fornecedor.setBloqueado(true);
        selecionado.getFiltros().add(fornecedor);

        selecionado.getFiltros().add(new FiltroAssociacaoDocumentoFiscalLiquidacao("Tipo de Documento", docto.getTipoDocumentoFiscal().getDescricao(), " and docto.tipodocumentofiscal_id = " + docto.getTipoDocumentoFiscal().getId()));

        if (docto.getChaveDeAcesso() != null) {
            selecionado.getFiltros().add(new FiltroAssociacaoDocumentoFiscalLiquidacao("Chave de Acesso", docto.getChaveDeAcesso(), " and trim(docto.chavedeacesso) = '" + docto.getChaveDeAcesso().trim() + "'"));
        }
        selecionado.getFiltros().add(new FiltroAssociacaoDocumentoFiscalLiquidacao("Data de Emissão", DataUtil.getDataFormatada(docto.getDataDocto()),
            " and trunc(docto.datadocto) = to_date('" + DataUtil.getDataFormatada(docto.getDataDocto()) + "', 'dd/MM/yyyy')"));
        if (docto.getDataAtesto() != null) {
            selecionado.getFiltros().add(new FiltroAssociacaoDocumentoFiscalLiquidacao("Data de Atesto", DataUtil.getDataFormatada(docto.getDataAtesto()),
                " and trunc(docto.dataatesto) = to_date('" + DataUtil.getDataFormatada(docto.getDataAtesto()) + "', 'dd/MM/yyyy')"));
        }
        selecionado.getFiltros().add(new FiltroAssociacaoDocumentoFiscalLiquidacao("Número", docto.getNumero(), " and docto.numero = '" + docto.getNumero() + "'"));
        selecionado.getFiltros().add(new FiltroAssociacaoDocumentoFiscalLiquidacao("Série", docto.getSerie(), " and docto.serie = '" + docto.getSerie() + "'"));
        selecionado.getFiltros().add(new FiltroAssociacaoDocumentoFiscalLiquidacao("Valor (R$)", Util.formataValorSemCifrao(docto.getValor()), " and docto.valor = " + docto.getValor()));
    }

    public boolean getDocumentoLiquidacaoSelecionado() {
        return selecionado.getDocumentosFiscaisLiquidacao().stream().anyMatch(VOLiquidacaoDocumentoFiscal::getSelecionado);
    }

    public String getCondicaoSqlDocumentoLiquidacao() {
        return selecionado.getFiltros().stream()
            .filter(FiltroAssociacaoDocumentoFiscalLiquidacao::getSelecionado)
            .map(FiltroAssociacaoDocumentoFiscalLiquidacao::getCondicao)
            .collect(Collectors.joining());
    }

    public void setNovaSituacaoEntrada() {
        if (selecionado.isGerarNumeroSituacao()) {
            boolean isTodosDoctosLiquidados = selecionado.getDocumentosFiscaisEntrada().stream().allMatch(doc -> doc.getSituacao().isLiquidado());

            if (isTodosDoctosLiquidados) {
                novaSituacao = SituacaoEntradaMaterial.ATESTO_DEFINITIVO_LIQUIDADO;
            }
        }
    }

    private void validarRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.hasDocumentoEntradaSelecionado() && (selecionado.isAssociarDocumento() || selecionado.isDesbloquearGrupo())) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um documento fiscal da entrada por compra para buscar o documento de integração da liquidação.");
        }
        if (selecionado.isAssociarDocumento()) {
            if (!selecionado.hasDocumentosFiscaisLiquidacao()) {
                ve.adicionarMensagemDeCampoObrigatorio("Nenhum documento de integração com a liquidação encontrado para o documento da entrada por compra.");
            } else if (!getDocumentoLiquidacaoSelecionado()) {
                ve.adicionarMensagemDeCampoObrigatorio("Selecione um documento fiscal da liquidação para continuar a integração.");
            }
        } else if (selecionado.isDesbloquearGrupo()){
            for (ItemDoctoItemEntrada item : selecionado.getItensDocumentoEntrada()) {
                if (item.getValorLiquidado() == null || item.getValorLiquidado().compareTo(BigDecimal.ZERO) == 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi distribuido o valor liquidado do documento para o item " + item.getItemEntradaMaterial().getNumeroItem() + ".");
                }
            }
        }
        ve.lancarException();
    }

    private void validarCamposObrigatorios() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.hasEntradaPorCompraSelecionada()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Entrada por Compra deve ser informado.");
        }

        if (selecionado.isAssociarDocumento() || selecionado.isDesbloquearGrupo()) {
            if (!selecionado.hasDocumentoEntradaSelecionado()) {
                ve.adicionarMensagemDeCampoObrigatorio("É necessário selecionar um documento.");
            }
        }
        ve.lancarException();
    }

    public List<LocalEstoque> completarLocalEstoque(String parte) {
        return facade.getLocalEstoqueFacade().completarLocalEstoqueAbertos(parte.trim());
    }

    public AssociacaoDocumentoLiquidacaoEntradaPorCompra getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(AssociacaoDocumentoLiquidacaoEntradaPorCompra selecionado) {
        this.selecionado = selecionado;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public SituacaoEntradaMaterial getNovaSituacao() {
        return novaSituacao;
    }

    public void setNovaSituacao(SituacaoEntradaMaterial novaSituacao) {
        this.novaSituacao = novaSituacao;
    }
}
