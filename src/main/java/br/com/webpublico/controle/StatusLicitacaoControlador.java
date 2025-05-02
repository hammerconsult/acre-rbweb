/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.StatusLicitacaoException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.StatusLicitacaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author renato
 */
@ManagedBean(name = "statusLicitacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-status-licitacao", pattern = "/status-licitacao/novo/", viewId = "/faces/administrativo/licitacao/statuslicitacao/edita.xhtml"),
    @URLMapping(id = "editar-status-licitacao", pattern = "/status-licitacao/editar/#{statusLicitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/statuslicitacao/edita.xhtml"),
    @URLMapping(id = "ver-status-licitacao", pattern = "/status-licitacao/ver/#{statusLicitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/statuslicitacao/visualizar.xhtml"),
    @URLMapping(id = "listar-status-licitacao", pattern = "/status-licitacao/listar/", viewId = "/faces/administrativo/licitacao/statuslicitacao/lista.xhtml")
})
public class StatusLicitacaoControlador extends PrettyControlador<StatusLicitacao> implements Serializable, CRUD {

    @EJB
    private StatusLicitacaoFacade facade;
    private ConverterAutoComplete converterVeiculoPublicao;
    private List<ItemProcessoDeCompra> listaItensDaLicitacao;
    private List<ItemPregao> itensPregaoNaoCotados;
    private ConverterAutoComplete converterLicitacao;

    private static final String STATUSFRACASSADA_CERTAME = "Não é possível vincular o status de 'Fracassada' para esta licitação, pois possui item ou lote com status vencedor ou classificado no mapa comparativo.";
    private static final String STATUSFRACASSADA_MAPACOMPARATIVOTECPRECO = "Não é possível vincular o status de 'Fracassada' para esta licitação, pois possui item ou lote com status vencedor ou classificado no mapa comparativo técnica e preço.";

    public StatusLicitacaoControlador() {
        super(StatusLicitacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novo-status-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setResponsavel(facade.getLicitacaoFacade().getSistemaFacade().getUsuarioCorrente());
        listaItensDaLicitacao = new ArrayList<>();
    }


    @URLAction(mappingId = "ver-status-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        listaItensDaLicitacao = new ArrayList<>();
        recuperarStatusDaLicitacao();
    }

    @URLAction(mappingId = "editar-status-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        listaItensDaLicitacao = new ArrayList<>();
        recuperarStatusDaLicitacao();
        try {
            facade.getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
        } catch (StatusLicitacaoException se) {
            FacesUtil.printAllFacesMessages(se.getMensagens());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
    }

    public boolean hasItensNaoCotadosPregao() {
        return itensPregaoNaoCotados != null && !itensPregaoNaoCotados.isEmpty();
    }

    @Override
    public void salvar() {
        ValidacaoException exception = new ValidacaoException();
        try {
            validarCamposObrigatorios(exception);
            validarCamposAoPublicarLicitacao();
            recuperarListasLicitacao();
            validarAlteracaoStatusLicitacao(exception);
            if (!getStatusAtualDeserta()) {
                facade.getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
            }
            validarCamposToStatusHomologacaoAndAdjudicacao(exception);
            realizarOperacaoSalvar();
            FacesUtil.addOperacaoRealizada("Registro Salvo com Sucesso!");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            if (hasItensNaoCotadosPregao()) {
                FacesUtil.executaJavaScript("dlgItensPregao.show()");
                FacesUtil.atualizarComponente("formItensPregao");
                return;
            }
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    @Override
    public void validarExclusaoEntidade() {
        ValidacaoException ve = new ValidacaoException();
        List<Contrato> contratos = facade.getContratoFacade().buscarContratoLicitacao(selecionado.getLicitacao());
        if (contratos != null && !contratos.isEmpty()) {
            for (Contrato contrato : contratos) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido excluir o status para licitação que possui vínculo com contrato: " + contrato + ".");
            }
        }
        ve.lancarException();

        StatusLicitacao status = facade.recuperarUltimoStatusLicitacao(selecionado.getLicitacao());
        if (TipoSituacaoLicitacao.ANDAMENTO.equals(selecionado.getTipoSituacaoLicitacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido excluir o status andamento, pois ele foi gerado automaticamente pelo sistema para nova licitação.");
        }

        ve.lancarException();
        if (TipoSituacaoLicitacao.ADJUDICADA.equals(selecionado.getTipoSituacaoLicitacao()) && status.isTipoHomologada()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para excluir o status de adjudicada é necessário excluir o status de homologada primeiro.");
        }
        ve.lancarException();
    }

    private boolean getStatusAtualDeserta() {
        return TipoSituacaoLicitacao.DESERTA.equals(selecionado.getLicitacao().getStatusAtualLicitacao().getTipoSituacaoLicitacao());
    }

    private void realizarOperacaoSalvar() {
        if (isOperacaoNovo()) {
            facade.salvarNovo(selecionado);
        } else {
            facade.salvar(selecionado);
        }
        facade.getLicitacaoFacade().salvarPortal(selecionado.getLicitacao());
    }

    public List<Licitacao> buscarLicitacaoPorNumeroModalideOrNumeroLicitacao(String filter) {
        return facade.getLicitacaoFacade().buscarLicitacaoPorNumeroModalidadeOrNumeroLicitacao(filter);
    }

    public ConverterAutoComplete getConverterLicitacao() {
        if (converterLicitacao == null) {
            converterLicitacao = new ConverterAutoComplete(Licitacao.class, facade.getLicitacaoFacade());
        }
        return converterLicitacao;
    }

    public void setConverterLicitacao(ConverterAutoComplete converterLicitacao) {
        this.converterLicitacao = converterLicitacao;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/status-licitacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    private void recuperarStatusDaLicitacao() {
        selecionado.getLicitacao().setListaDeStatusLicitacao(facade.getLicitacaoFacade().recuperarListaDeStatus(selecionado.getLicitacao()));
    }

    public List<SelectItem> getTipoSituacaoLicitacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem("", null));
        if (selecionado.getLicitacao() != null && !getStatusAtualDeserta()) {
            for (TipoSituacaoLicitacao object : TipoSituacaoLicitacao.values()) {
                if (!object.equals(TipoSituacaoLicitacao.CANCELADA)) {
                    toReturn.add(new SelectItem(object, object.getDescricao()));
                }
            }
        } else {
            toReturn.add(new SelectItem(TipoSituacaoLicitacao.ANDAMENTO, TipoSituacaoLicitacao.ANDAMENTO.getDescricao()));
        }
        return Util.ordenaSelectItem(toReturn);
    }

    public List<VeiculoDePublicacao> completarVeiculoPublicacao(String parte) {
        return facade.getVeiculoDePublicacaoFacade().listaFiltrando(parte.trim(), "nome");
    }

    public ConverterAutoComplete getConverterVeiculoPublicacao() {
        if (converterVeiculoPublicao == null) {
            converterVeiculoPublicao = new ConverterAutoComplete(VeiculoDePublicacao.class, facade.getVeiculoDePublicacaoFacade());
        }
        return converterVeiculoPublicao;
    }

    private void validarCamposObrigatorios(ValidacaoException ex) {
        Util.validarCamposObrigatorios(selecionado, ex);
    }

    private void validarCamposAoPublicarLicitacao() {
        ValidacaoException ve = new ValidacaoException();
        if (TipoSituacaoLicitacao.HOMOLOGADA.equals(selecionado.getTipoSituacaoLicitacao())) {
            if (selecionado.getDataPublicacao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo data de publicação deve ser informado.");
            }
            if (selecionado.getVeiculoDePublicacao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo veículo de publicação deve ser informado.");
            }
            if (selecionado.getNumeroEdicao() == null || selecionado.getNumeroEdicao().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo número da edição deve ser informado.");
            }
            if (selecionado.getPaginaPublicacao() == null || selecionado.getPaginaPublicacao().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo página de publicação deve ser informado.");
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private void recuperarListasLicitacao() {
        selecionado.getLicitacao().setListaDeStatusLicitacao(facade.getLicitacaoFacade().recuperarListaDeStatus(selecionado.getLicitacao()));
        selecionado.getLicitacao().setPareceres(facade.getLicitacaoFacade().recuperarListaDeParecer(selecionado.getLicitacao()));
        selecionado.getLicitacao().setListaDeDocumentos(facade.getLicitacaoFacade().recuperaListaDeDocumentos(selecionado.getLicitacao()));
        selecionado.getLicitacao().setDocumentosProcesso(facade.getLicitacaoFacade().recuperaDoctosHabLicitacao(selecionado.getLicitacao()));
        selecionado.getLicitacao().setPublicacoes(facade.getLicitacaoFacade().recuperarListaDePublicacoes(selecionado.getLicitacao()));
        selecionado.getLicitacao().setFornecedores(facade.getLicitacaoFacade().recuperarListaDeLicitacaoFornecedor(selecionado.getLicitacao()));
    }

    public void validarAlteracaoStatusLicitacao(ValidacaoException ex) {
        Licitacao licitacao = selecionado.getLicitacao();

        if (selecionado.getTipoSituacaoLicitacao() == null) {
            ex.adicionarMensagemDeCampoObrigatorio("O Status da licitação não possui status.");
        }

        if (selecionado.getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.HOMOLOGADA)) {
            if (!licitacao.getStatusAtualLicitacao().getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.ADJUDICADA)) {
                ex.adicionarMensagemDeOperacaoNaoPermitida("Para Homologar esta licitação, é necessário que a mesma esteja Adjudicada.");
            }
        }

        if (selecionado.getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.ADJUDICADA)) {
            if (!licitacao.getStatusAtualLicitacao().getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.ANDAMENTO)) {
                ex.adicionarMensagemDeOperacaoNaoPermitida("Para Adjudicar esta licitação, é necessário que a mesma esteja em Andamento.");
            }
        }

        if ((isStatusLicitacaoAdjucada(selecionado)
            || isStatusLicitacaoHomologada(selecionado))
            && !isLicitacaoHomologadaOuAdjudicadaEPregaoOuMapaApurados()) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("Para adjudicar ou homologar uma licitação, o pregão ou mapa comparativo a ela vinculado deve estar apurado.");
        }

        if (selecionado.getLicitacao().getStatusAtualLicitacao().getTipoSituacaoLicitacao().equals(selecionado.getTipoSituacaoLicitacao())) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("Esta licitação já se encontra na situação " + selecionado.getTipoSituacaoLicitacao().getDescricao() + ".");
        }

        if (selecionado.isTipoDeserta()
            && (licitacao.getFornecedores() != null
            && !licitacao.getFornecedores().isEmpty()))
            ex.adicionarMensagemDeOperacaoNaoPermitida("Não foi possível vincular o status de 'Deserta' para esta licitação, pois existe fornecedor vinculado a esta licitação.");

        if (selecionado.isTipoFracassada()) {
            validarMapaComparativoTecnicaPreco(ex);
            if (facade.getLicitacaoFacade().isLicitacaoPossuiCertame(licitacao)) {
                Certame certame = buscarCertamePertencenteLicitacao(licitacao);
                validarCertame(ex, certame);
            } else if (licitacao.getFornecedores() == null || licitacao.getFornecedores().isEmpty()) {
                ex.adicionarMensagemDeOperacaoNaoPermitida("Não foi possível vincular o Status de 'Fracassada' para esta licitação, é necessário informar pelo menos um fornecedor para esta licitação.");
            }
        }

        if (!validarStatusItemLicitacao(ex)) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("Para adjudicar a licitação, é necessário que a mesma tenha todos seus itens habilitados estejam homologados.");
        }
        validarStatusLicitacaoAdjudicada(ex);
        ex.lancarException();
    }

    private void validarStatusLicitacaoAdjudicada(ValidacaoException ex) {
        if (selecionado.getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.ADJUDICADA)) {
            if (!facade.getStatusFornecedorLicitacaoFacade().isStatusLicitacaoAdjudicado(selecionado.getLicitacao())) {
                ex.adicionarMensagemDeOperacaoNaoPermitida("Para alterar o status da licitação para " +
                    " adjudicada é necessário realizar a operação de adjudicação em " + Util.linkBlack("/adjudicacao-homologacao-licitacao/listar/", "Adjudicação e Homologação"));
            }

        }
    }

    public String linkCancelarItensPregao() {
        Pregao pregao = itensPregaoNaoCotados.get(0).getPregao();
        if (pregao.getLicitacao().getProcessoDeCompra().getSolicitacaoMaterial().getTipoApuracao().isPorItem()) {
            return "Para adjudicar a licitação é necessário cancelar os itens não cotados no pregão. " + Util.linkBlack("/pregao/por-item/cancelar-item/" + pregao.getId() + "/", "Clique no link para cancelar os itens no pregão.");
        }
        return "";
    }

    private void validarMapaComparativoTecnicaPreco(ValidacaoException ex) {
        if (facade.getMapaComparativoTecnicaPrecoFacade().isVerificarMapaComparativoPossuiItemClassificadoOrVencedor(selecionado.getLicitacao())) {
            ex.adicionarMensagemDeOperacaoNaoPermitida(STATUSFRACASSADA_MAPACOMPARATIVOTECPRECO);
        }
    }

    private void validarCertame(ValidacaoException ex, Certame certame) {
        if (certame.getListaItemCertame() != null && !certame.getListaItemCertame().isEmpty()) {
            validarItensCertame(ex, certame);
        }
    }

    private void validarItensCertame(ValidacaoException ex, Certame certame) {
        for (ItemCertame itemCertame : certame.getListaItemCertame()) {
            if (itemCertame != null)
                if (itemCertame.isVencedor() || itemCertame.isClassificado()) {
                    ex.adicionarMensagemDeOperacaoNaoPermitida(STATUSFRACASSADA_CERTAME);
                    break;
                }
        }
    }

    private Certame buscarCertamePertencenteLicitacao(Licitacao licitacao) {
        return facade.getCertameFacade().recuperarCertameDaLicitacao(licitacao);
    }


    public Boolean isObjetoNulo(Object objeto) {
        if (objeto == null) {
            return true;
        }

        if (objeto.toString().trim().length() <= 0) {
            return true;
        }

        return false;
    }

    public void validarCamposToStatusHomologacaoAndAdjudicacao(ValidacaoException ex) {
        Licitacao licitacao = selecionado.getLicitacao();
        String situacao = selecionado.getTipoSituacaoLicitacao().getDescricao();

        if (!selecionado.getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.HOMOLOGADA)
            && !selecionado.getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.ADJUDICADA))
            return;


        if (isObjetoNulo(licitacao.getEmitidaEm()))
            ex.adicionarMensagemDeOperacaoNaoPermitida("A Data de emissão do edital deve ser informada para mudar o status da licitação para " + situacao + ".");


        if (isObjetoNulo(licitacao.getAbertaEm()))
            ex.adicionarMensagemDeOperacaoNaoPermitida("A Data de abertura deve ser informada para mudar o status da licitação para " + situacao + ".");


        if (isObjetoNulo(licitacao.getValorMaximo()))
            ex.adicionarMensagemDeOperacaoNaoPermitida("O valor máximo deve ser informado para mudar o status da licitação para " + situacao + ".");


        if (isObjetoNulo(licitacao.getLocalDeEntrega()))
            ex.adicionarMensagemDeOperacaoNaoPermitida("O local de entrega deve ser informado para mudar o status da licitação para " + situacao + ".");


        if (isObjetoNulo(licitacao.getRegimeDeExecucao()))
            ex.adicionarMensagemDeOperacaoNaoPermitida("O regime de execução deve ser informado para mudar o status da licitação para " + situacao + ".");


        if (isObjetoNulo(licitacao.getPeriodoDeExecucao()))
            ex.adicionarMensagemDeOperacaoNaoPermitida("O período de execução deve ser informado para mudar o status da licitação para " + situacao + ".");


        if (isObjetoNulo(licitacao.getFormaDePagamento()))
            ex.adicionarMensagemDeOperacaoNaoPermitida("A forma de pagamento deve ser informado para mudar o status da licitação para " + situacao + ".");


        if (licitacao.getDocumentosProcesso() == null || licitacao.getDocumentosProcesso().size() <= 0)
            ex.adicionarMensagemDeOperacaoNaoPermitida("Informe ao menos um documento obrigatório para mudar o status da licitação para " + situacao + ".");


        if (licitacao.getPareceres() == null || licitacao.getPareceres().size() <= 0)
            ex.adicionarMensagemDeOperacaoNaoPermitida("Informe ao menos um parecer para mudar o status da licitação para " + situacao + ".");


        if (licitacao.getPublicacoes() == null || licitacao.getPublicacoes().size() <= 0)
            ex.adicionarMensagemDeOperacaoNaoPermitida("Informe ao menos uma publicação para mudar o status da licitação para " + situacao + ".");


        if (licitacao.getFornecedores() == null || licitacao.getFornecedores().size() <= 0)
            ex.adicionarMensagemDeOperacaoNaoPermitida("Informe ao menos um fornecedor para mudar o status da licitação para " + situacao + ".");


        if (ex.temMensagens())
            throw ex;
    }


    public boolean isStatusLicitacaoHomologada(StatusLicitacao sl) {
        try {
            return sl.getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.HOMOLOGADA);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isStatusLicitacaoAdjucada(StatusLicitacao sl) {
        return sl.getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.ADJUDICADA);
    }

    public boolean isStatusLicitacaoAnulada(StatusLicitacao sl) {
        return sl.getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.ANULADA);
    }

    private void recuperarItensDaLicitacao() {
        if (selecionado.getLicitacao() != null) {
            listaItensDaLicitacao = facade.getLicitacaoFacade().recuperaItensProcessoDeCompra(selecionado.getLicitacao());
        }
    }

    private boolean validarStatusItemLicitacao(ValidacaoException ex) {
        if (selecionado.isTipoAdjudicada()) {
            itensPregaoNaoCotados = Lists.newArrayList();
            for (ItemProcessoDeCompra item : listaItensDaLicitacao) {
                if (selecionado.getLicitacao().isPregao()) {
                    ItemPregao itemPregao;
                    if (selecionado.getLicitacao().isTipoApuracaoPrecoItem()) {
                        itemPregao = facade.getPregaoFacade().getItemPregaoDoItemProcessoDeCompra(item);
                    } else {
                        itemPregao = facade.getPregaoFacade().getItemPregaoDoLoteProcessoDeCompra(item.getLoteProcessoDeCompra());
                    }
                    if (itemPregao == null) {
                        ex.adicionarMensagemDeOperacaoNaoPermitida("Por favor terminar todo o tramite para que seja possível Adjudicar/Homologar a licitação. ");
                        return true;
                    }
                    if (itemPregao.getTipoStatusItemPregao() == null) {
                        itensPregaoNaoCotados.add(itemPregao);
                    }
                    if (TipoStatusItemPregao.DESERTO.equals(itemPregao.getApenasTipoStatusItemPregao())
                        || TipoStatusItemPregao.PREJUDICADO.equals(itemPregao.getApenasTipoStatusItemPregao())
                        || TipoStatusItemPregao.CANCELADO.equals(itemPregao.getApenasTipoStatusItemPregao())
                        || TipoStatusItemPregao.DECLINADO.equals(itemPregao.getApenasTipoStatusItemPregao())
                        || TipoClassificacaoFornecedor.INABILITADO.equals(itemPregao.getStatusFornecedorVencedor())) {
                        continue;
                    }
                }
                if (!SituacaoItemProcessoDeCompra.HOMOLOGADO.equals(item.getSituacaoItemProcessoDeCompra())
                    && !SituacaoItemProcessoDeCompra.FRUSTRADO.equals(item.getSituacaoItemProcessoDeCompra())) {
                    return false;
                }
            }
        }
        return true;
    }

    private void atribuirListas() {
        recuperarStatusDaLicitacao();
        selecionado.getLicitacao().setPareceres(facade.getLicitacaoFacade().recuperarListaDeParecer(selecionado.getLicitacao()));
        selecionado.getLicitacao().setListaDeDocumentos(facade.getLicitacaoFacade().recuperaListaDeDocumentos(selecionado.getLicitacao()));
        selecionado.getLicitacao().setDocumentosProcesso(facade.getLicitacaoFacade().recuperaDoctosHabLicitacao(selecionado.getLicitacao()));
        selecionado.getLicitacao().setPublicacoes(facade.getLicitacaoFacade().recuperarListaDePublicacoes(selecionado.getLicitacao()));
        selecionado.getLicitacao().setFornecedores(facade.getLicitacaoFacade().recuperarListaDeLicitacaoFornecedor(selecionado.getLicitacao()));
    }

    public Boolean objetoEstaNulo(Object objeto) {
        if (objeto == null) {
            return Boolean.TRUE;
        }

        if (objeto.toString().trim().length() <= 0) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    private boolean isLicitacaoHomologadaOuAdjudicadaEPregaoOuMapaApurados() {
        if (selecionado.getLicitacao().getId() == null) {
            return true;
        }

        Pregao pregao = facade.getLicitacaoFacade().licitacaoPossuiVinculoComPregao(selecionado.getLicitacao());

        if (pregao != null) {
            carregaListaDeItemPregao(pregao);
            if (!pregao.isApurado()) {
                return false;
            }
        }

        Certame certame = facade.getLicitacaoFacade().licitacaoPossuiVinculoComMapaComparativo(selecionado.getLicitacao());

        if (certame != null) {
            if (!certame.isApurado()) {
                return false;
            }
        }
        return true;
    }

    private void carregaListaDeItemPregao(Pregao pregao) {
        pregao.setListaDeItemPregao(facade.getLicitacaoFacade().getPregaoFacade().recuperarListaDeItemPregao(pregao));
    }

    public void selecionouLicitacao(SelectEvent evento) {
        Licitacao licitacao = (Licitacao) evento.getObject();
        licitacao.setListaDeStatusLicitacao(facade.getLicitacaoFacade().recuperarListaDeStatus(licitacao));
        if (licitacao != null) {
            Long numero = (licitacao.getStatusAtualLicitacao().getNumero() + 1);
            selecionado.setNumero(numero);
            selecionado.setLicitacao(licitacao);
            recuperarItensDaLicitacao();
        }
    }

    public String getStatusAtualLicitacao() {
        try {
            return selecionado.getLicitacao().getStatusAtualLicitacao().getTipoSituacaoLicitacao().getDescricao();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isCampoDesabilitadoPorStatusCorrenteLicitacao() {
        StatusLicitacao status = null;
        try {
            Licitacao licitacao = facade.getLicitacaoFacade().recuperar(selecionado.getLicitacao().getId());
            status = licitacao.getStatusAtualLicitacao();
        } catch (Exception e) {
            return false;
        }
        if (status != null)
            if (isStatusCorrenteLicitacaoAnulada(status)
                || isStatusCorrenteLicitacaoRevogada(status)
                || isStatusCorrenteLicitacaoFracassada(status)) {
                return true;
            }

        return false;
    }

    private boolean isStatusCorrenteLicitacaoFracassada(StatusLicitacao status) {
        return status.getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.FRACASSADA);
    }

    private boolean isStatusCorrenteLicitacaoRevogada(StatusLicitacao status) {
        return status.getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.REVOGADA);
    }

    public boolean isStatusCorrenteLicitacaoDeserta(StatusLicitacao status) {
        return status.getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.DESERTA);
    }

    private boolean isStatusCorrenteLicitacaoAnulada(StatusLicitacao status) {
        return status.getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.ANULADA);
    }

    public void limparCamposLicitacao() {
        selecionado.setLicitacao(null);
        selecionado.setTipoSituacaoLicitacao(null);
        selecionado.setNumero(null);
    }

    public List<ItemPregao> getItensPregaoNaoCotados() {
        return itensPregaoNaoCotados;
    }

    public void setItensPregaoNaoCotados(List<ItemPregao> itensPregaoNaoCotados) {
        this.itensPregaoNaoCotados = itensPregaoNaoCotados;
    }
}
