package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AquisicaoEstornoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by mga on 24/04/2018.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-aquisicao-estorno", pattern = "/estorno-aquisicao/bem-movel/novo/", viewId = "/faces/administrativo/patrimonio/aquisicao-estorno/movel/edita.xhtml"),
    @URLMapping(id = "edita-aquisicao-estorno", pattern = "/estorno-aquisicao/bem-movel/editar/#{aquisicaoEstornoControlador.id}/", viewId = "/faces/administrativo/patrimonio/aquisicao-estorno/movel/edita.xhtml"),
    @URLMapping(id = "listar-aquisicao-estorno", pattern = "/estorno-aquisicao/bem-movel/listar/", viewId = "/faces/administrativo/patrimonio/aquisicao-estorno/movel/lista.xhtml"),
    @URLMapping(id = "ver-aquisicao-estorno", pattern = "/estorno-aquisicao/bem-movel/ver/#{aquisicaoEstornoControlador.id}/", viewId = "/faces/administrativo/patrimonio/aquisicao-estorno/movel/visualizar.xhtml"),
})

public class AquisicaoEstornoControlador extends PrettyControlador<AquisicaoEstorno> implements Serializable, CRUD {

    @EJB
    private AquisicaoEstornoFacade facade;
    private Future<AquisicaoEstorno> futureSalvar;
    private Future<List<ItemAquisicao>> futurePesquisar;
    private List<DoctoFiscalLiquidacao> documentoFiscais;
    private List<ItemAquisicao> itensAquisicao;
    private LazyDataModel<ItemAquisicaoEstorno> modelItensAquisicaoEstorno;
    private LazyDataModel<ItemAquisicao> modelItensAquisicao;
    private AssistenteMovimentacaoBens assistenteBarraProgresso;
    private ConfigMovimentacaoBem configMovimentacaoBem;

    public AquisicaoEstornoControlador() {
        super(AquisicaoEstorno.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/estorno-aquisicao/bem-movel/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "nova-aquisicao-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        try {
            super.novo();
            selecionado.setUsuario(facade.getSistemaFacade().getUsuarioCorrente());
            selecionado.setDataEstorno(getDataOperacao());
            selecionado.setTipoBem(TipoBem.MOVEIS);
            inicializarListas();
            recuperarConfiguracaoMovimentacaoBem();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void inicializarListas() {
        documentoFiscais = Lists.newArrayList();
        itensAquisicao = Lists.newArrayList();
    }

    @URLAction(mappingId = "edita-aquisicao-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        selecionado = facade.recuperar(getId());
        FacesUtil.addAtencao("Esta aquisição já foi estornada, não será possível realizar alterações.");
        redirecionarParaVer();
    }

    @URLAction(mappingId = "ver-aquisicao-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        operacao = Operacoes.VER;
        inicializarListas();
        selecionado = facade.recuperar(getId());
        buscarDocumentosFiscais();
        criarItensPaginacaoItensAquisicao();
        criarItensPaginacaoItensAquisicaoEstorno();
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarRegrasConfiguracaoMovimentacaoBem();
            facade.getSingletonGeradorCodigoPatrimonio().bloquearMovimento(Aquisicao.class);
            iniciarAssistenteBarraProgresso();
            futureSalvar = facade.salvarEstorno(selecionado, itensAquisicao, assistenteBarraProgresso);
            FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
            FacesUtil.executaJavaScript("acompanharSalvar()");
        } catch (ValidacaoException ex) {
            facade.getSingletonGeradorCodigoPatrimonio().desbloquear(Aquisicao.class);
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            facade.getSingletonGeradorCodigoPatrimonio().desbloquear(Aquisicao.class);
            descobrirETratarException(e);
        } finally {
            assistenteBarraProgresso.zerarContadoresProcesso();
            assistenteBarraProgresso.setExecutando(false);
            facade.getSingletonGeradorCodigoPatrimonio().desbloquear(Aquisicao.class);
        }
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataEstorno(), OperacaoMovimentacaoBem.ESTORNO_AQUISICAO_BEM);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
        }
    }

    private void validarRegrasConfiguracaoMovimentacaoBem() {
        recuperarConfiguracaoMovimentacaoBem();
        if (configMovimentacaoBem != null) {
            configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataEstorno(), getDataOperacao(), operacao);
        }
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    public void buscarDadosAquisicao() {
        try {
            if (selecionado.getAquisicao() != null) {
                itensAquisicao = Lists.newArrayList();
                recuperarConfiguracaoMovimentacaoBem();
                buscarDocumentosFiscais();
                iniciarAssistenteBarraProgresso();
                futurePesquisar = facade.getEfetivacaoAquisicaoFacade().buscarItensAquisicao(selecionado.getAquisicao(), assistenteBarraProgresso);
                FacesUtil.executaJavaScript("iniciarPesquisa()");
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void buscarDocumentosFiscais() {
        documentoFiscais = facade.getEfetivacaoAquisicaoFacade().buscarDocumentoFiscal(selecionado.getAquisicao());
    }

    public void acompanharPesquisa() {
        if (futurePesquisar != null && futurePesquisar.isDone()) {
            FacesUtil.executaJavaScript("terminarPesquisa()");
        }
    }

    public void finalizarPesquisa() throws ExecutionException, InterruptedException {
        itensAquisicao.addAll(futurePesquisar.get());
        for (ItemAquisicao itemAquisicao : itensAquisicao) {
            EventoBem eventoBem = facade.getBemFacade().recuperarUltimoEventoBem(itemAquisicao.getBem());
            if (eventoBem != null && !TipoEventoBem.ITEMAQUISICAO.equals(itemAquisicao.getTipoEventoBem())) {
                FacesUtil.addAtencao("A aquisição a ser estorna possui bens que sofreram movimentações posterior a aquisição. " +
                    "Caso continue o bem ficará com valor negativo, pois o original será estornado no processo.");
                break;
            }
        }
        if (!assistenteBarraProgresso.getMensagens().isEmpty()) {
            for (String msg : assistenteBarraProgresso.getMensagens()) {
                FacesUtil.addOperacaoNaoPermitida(msg);
            }
            selecionado.setAquisicao(null);
            inicializarListas();
        }
        FacesUtil.atualizarComponente("Formulario");
    }

    public BigDecimal getValorTotalOriginalAquisicao() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (selecionado.getAquisicao() != null) {
            valorTotal = valorTotal.add(facade.getEfetivacaoAquisicaoFacade().getValorOriginal(selecionado.getAquisicao()));
        }
        return valorTotal;
    }

    public BigDecimal getValorTotalLiquidadoAquisicao() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (selecionado.getAquisicao() != null) {
            valorTotal = valorTotal.add(facade.getEfetivacaoAquisicaoFacade().getValorLiquidado(selecionado.getAquisicao()));
        }
        return valorTotal;
    }


    public BigDecimal getValorTotalDocumentosFiscais() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (!getDocumentoFiscais().isEmpty()) {
            for (DoctoFiscalLiquidacao docto : getDocumentoFiscais()) {
                valorTotal = valorTotal.add(docto.getTotal());
            }
        }
        return valorTotal;
    }

    private void iniciarAssistenteBarraProgresso() {
        assistenteBarraProgresso = new AssistenteMovimentacaoBens(getDataOperacao(), operacao);
        assistenteBarraProgresso.zerarContadoresProcesso();
        assistenteBarraProgresso.setTotal(getItensAquisicao().size());
        assistenteBarraProgresso.setSelecionado(selecionado);
        assistenteBarraProgresso.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        assistenteBarraProgresso.setDataAtual(facade.getSistemaFacade().getDataOperacao());
        assistenteBarraProgresso.setExecutando(true);
        if (configMovimentacaoBem != null) {
            assistenteBarraProgresso.setConfigMovimentacaoBem(configMovimentacaoBem);
        }
    }

    private void criarItensPaginacaoItensAquisicaoEstorno() {
        modelItensAquisicaoEstorno = new LazyDataModel<ItemAquisicaoEstorno>() {
            @Override
            public List<ItemAquisicaoEstorno> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                setRowCount(facade.quantidadeItensItemAquisicaoEstorno(selecionado));
                return facade.recuperarItemAquisicaoEstornoCriteria(first, pageSize, selecionado);
            }
        };
    }

    private void criarItensPaginacaoItensAquisicao() {
        modelItensAquisicao = new LazyDataModel<ItemAquisicao>() {
            @Override
            public List<ItemAquisicao> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                setRowCount(facade.quantidadeItensAquisicao(selecionado.getAquisicao()));
                return facade.recuperarItemAquisicaoCriteria(first, pageSize, selecionado.getAquisicao());
            }
        };
    }

    public List<Aquisicao> completarAquisicao(String parte) {
        return facade.getEfetivacaoAquisicaoFacade().buscarAquisicaoFinalizadaSemLiquidacaoPorTipoBem(parte.trim(), selecionado.getTipoBem());
    }

    public void consultarFutureSalvar() {
        if (futureSalvar != null && futureSalvar.isDone()) {
            try {
                AquisicaoEstorno aquisicaoEstornoSalva = futureSalvar.get();
                if (aquisicaoEstornoSalva != null) {
                    selecionado = aquisicaoEstornoSalva;
                    FacesUtil.executaJavaScript("finalizarSalvar()");
                }
            } catch (Exception ex) {
                FacesUtil.addOperacaoNaoRealizada("Não foi possível salvar o estorno de aquisição " + ex.getMessage());
            }
        }
    }

    public void finalizarProcesssoSalvar() {
        futureSalvar = null;
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        redirecionarParaVer();
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public String formatarValor(BigDecimal valor) {
        return Util.formataValor(valor);
    }

    public void limparDadosAquisicao() {
        selecionado.setAquisicao(null);
        inicializarListas();
    }

    public AssistenteMovimentacaoBens getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteMovimentacaoBens assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public List<DoctoFiscalLiquidacao> getDocumentoFiscais() {
        return documentoFiscais;
    }

    public void setDocumentoFiscais(List<DoctoFiscalLiquidacao> documentoFiscais) {
        this.documentoFiscais = documentoFiscais;
    }

    public List<ItemAquisicao> getItensAquisicao() {
        return itensAquisicao;
    }

    public void setItensAquisicao(List<ItemAquisicao> itensAquisicao) {
        this.itensAquisicao = itensAquisicao;
    }

    public LazyDataModel<ItemAquisicaoEstorno> getModelItensAquisicaoEstorno() {
        return modelItensAquisicaoEstorno;
    }

    public void setModelItensAquisicaoEstorno(LazyDataModel<ItemAquisicaoEstorno> modelItensAquisicaoEstorno) {
        this.modelItensAquisicaoEstorno = modelItensAquisicaoEstorno;
    }

    public LazyDataModel<ItemAquisicao> getModelItensAquisicao() {
        return modelItensAquisicao;
    }

    public void setModelItensAquisicao(LazyDataModel<ItemAquisicao> modelItensAquisicao) {
        this.modelItensAquisicao = modelItensAquisicao;
    }
}
