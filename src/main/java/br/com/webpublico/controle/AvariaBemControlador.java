package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AvariaBemFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 03/11/14
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "avariaBemControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoAvariaBemMovel", pattern = "/ajuste-de-perda-bem-movel/novo/", viewId = "/faces/administrativo/patrimonio/avaria/movel/edita.xhtml"),
    @URLMapping(id = "editarAvariaBemMovel", pattern = "/ajuste-de-perda-bem-movel/editar/#{avariaBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/avaria/movel/edita.xhtml"),
    @URLMapping(id = "listarAvariaBemMovel", pattern = "/ajuste-de-perda-bem-movel/listar/", viewId = "/faces/administrativo/patrimonio/avaria/movel/lista.xhtml"),
    @URLMapping(id = "verAvariaBemMovel", pattern = "/ajuste-de-perda-bem-movel/ver/#{avariaBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/avaria/movel/visualizar.xhtml"),

    @URLMapping(id = "novoAvariaBemImovel", pattern = "/ajuste-de-perda-bem-imovel/novo/", viewId = "/faces/administrativo/patrimonio/avaria/imovel/edita.xhtml"),
    @URLMapping(id = "editarAvariaBemImovel", pattern = "/ajuste-de-perda-bem-imovel/editar/#{avariaBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/avaria/imovel/edita.xhtml"),
    @URLMapping(id = "listarAvariaBemImovel", pattern = "/ajuste-de-perda-bem-imovel/listar/", viewId = "/faces/administrativo/patrimonio/avaria/imovel/lista.xhtml"),
    @URLMapping(id = "verAvariaBemImovel", pattern = "/ajuste-de-perda-bem-imovel/ver/#{avariaBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/avaria/imovel/visualizar.xhtml"),

    @URLMapping(id = "novoAvariaBemIntangivel", pattern = "/ajuste-de-perda-bem-intangivel/novo/", viewId = "/faces/administrativo/patrimonio/avaria/intangivel/edita.xhtml"),
    @URLMapping(id = "editarAvariaBemIntangivel", pattern = "/ajuste-de-perda-bem-intangivel/editar/#{avariaBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/avaria/intangivel/edita.xhtml"),
    @URLMapping(id = "listarAvariaBemIntangivel", pattern = "/ajuste-de-perda-bem-intangivel/listar/", viewId = "/faces/administrativo/patrimonio/avaria/intangivel/lista.xhtml"),
    @URLMapping(id = "verAvariaBemIntangivel", pattern = "/ajuste-de-perda-bem-intangivel/ver/#{avariaBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/avaria/intangivel/visualizar.xhtml")
})
public class AvariaBemControlador extends PrettyControlador<AvariaBem> implements Serializable, CRUD {

    @EJB
    private AvariaBemFacade facade;
    private HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria;
    private AssistenteMovimentacaoBens assistenteMovimentacao;

    public AvariaBemControlador() {
        super(AvariaBem.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        switch (selecionado.getTipoBem()) {
            case MOVEIS: {
                return "/ajuste-de-perda-bem-movel/";
            }
            case IMOVEIS: {
                return "/ajuste-de-perda-bem-imovel/";
            }
            case INTANGIVEIS: {
                return "/ajuste-de-perda-bem-intangivel/";
            }
        }
        return "/ajuste-de-perda-bem-movel/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoAvariaBemMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoMovel() {
        super.novo();
        try {
            inicializarSelecionado();
            selecionado.setTipoBem(TipoBem.MOVEIS);
            iniciarAssistenteMovimentacao();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "novoAvariaBemImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoImovel() {
        super.novo();
        inicializarSelecionado();
        selecionado.setTipoBem(TipoBem.IMOVEIS);
    }

    @URLAction(mappingId = "novoAvariaBemIntangivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoIntangivel() {
        super.novo();
        inicializarSelecionado();
        selecionado.setTipoBem(TipoBem.INTANGIVEIS);
    }

    @URLAction(mappingId = "verAvariaBemMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verMovel() {
        super.ver();
        selecionado.setTipoBem(TipoBem.MOVEIS);
    }

    @URLAction(mappingId = "verAvariaBemImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verImovel() {
        super.ver();
        selecionado.setTipoBem(TipoBem.IMOVEIS);
    }

    @URLAction(mappingId = "verAvariaBemIntangivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verIntangivel() {
        super.ver();
        selecionado.setTipoBem(TipoBem.INTANGIVEIS);
    }

    @URLAction(mappingId = "editarAvariaBemMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarMovel() {
        super.editar();
        selecionado.setTipoBem(TipoBem.MOVEIS);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + getId());
    }

    @URLAction(mappingId = "editarAvariaBemImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarImovel() {
        super.editar();
        selecionado.setTipoBem(TipoBem.IMOVEIS);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + getId());
    }

    @URLAction(mappingId = "editarAvariaBemIntangivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarIntangivel() {
        super.editar();
        selecionado.setTipoBem(TipoBem.INTANGIVEIS);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + getId());
    }

    private ConfigMovimentacaoBem recuperarConfiguracaoMovimentacaoBem() {
        return facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataLancamento(), OperacaoMovimentacaoBem.REDUCAO_VALOR_RECUPERAVEL);
    }

    private void validarRegrasConfiguracaoMovimentacaoBem() {
        recuperarConfiguracaoMovimentacaoBem();
        if (assistenteMovimentacao.getConfigMovimentacaoBem() != null) {
            this.assistenteMovimentacao.getConfigMovimentacaoBem().validarDatasMovimentacao(selecionado.getDataLancamento(), facade.getSistemaFacade().getDataOperacao(), operacao);
        }
    }

    private void inicializarSelecionado() {
        selecionado.setResponsavel(facade.getBemFacade().getSistemaFacade().getUsuarioCorrente());
        selecionado.setDataLancamento(facade.getSistemaFacade().getDataOperacao());
    }

    public List<Bem> completarBem(String parte) {
        if (selecionado.getHierarquiaOrganizacional() == null) {
            return new ArrayList();
        }
        List<Bem> bens = facade.getBemFacade().buscarBemAjustePerdas(parte, selecionado.getTipoBem(), selecionado.getHierarquiaOrganizacional());
        if (bens.isEmpty()) {
            FacesUtil.addAtencao("Nenhum bem encontrado para a unidade administrativa " + selecionado.getHierarquiaOrganizacional().toString() + ".");
        }
        return bens;
    }

    private void verificarSeBemPossuiBloqueioParaMovimentacao() {
        ValidacaoException ve = new ValidacaoException();
        FiltroPesquisaBem filtro = new FiltroPesquisaBem(selecionado.getTipoBem(), selecionado);
        filtro.setHierarquiaAdministrativa(selecionado.getHierarquiaOrganizacional());
        filtro.setBem(selecionado.getBem());
        filtro.setDataOperacao(selecionado.getDataLancamento());

        if (!facade.getBemFacade().verificarBemBloqueado(assistenteMovimentacao.getConfigMovimentacaoBem(), filtro)){
            EventoBem eventoBem = facade.getBemFacade().recuperarUltimoEventoBem(selecionado.getBem());
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O bem: " + selecionado.getBem()
                + " foi movimentado no(a) " + eventoBem.getTipoEventoBem().getDescricao()
                + " em: " + DataUtil.getDataFormatada(eventoBem.getDataOperacao())
                + " e encontra-se na situação de " + eventoBem.getSituacaoEventoBem().getDescricao() + ".");

        }
        ve.lancarException();
    }

    public boolean habilitaSelecaoBem() {
        return selecionado != null && selecionado.getTipoBem() != null && selecionado.getHierarquiaOrganizacional() != null;
    }

    public void processaSelecaoBem() {
        try {
            iniciarAssistenteMovimentacao();
            if (selecionado.getBem() != null) {
                if (assistenteMovimentacao.getConfigMovimentacaoBem() == null) {
                    recuperarConfiguracaoMovimentacaoBem();
                }
                verificarSeBemPossuiBloqueioParaMovimentacao();
                EstadoBem estadoBem = facade.getBemFacade().recuperarUltimoEstadoDoBem(selecionado.getBem());
                selecionado.setBem(facade.getBemFacade().recuperar(selecionado.getBem().getId()));
                setHierarquiaOrganizacionalOrcamentaria(facade.getHierarquiaOrganizacionalFacade().retornarHierarquiaOrcamentariaPelaUnidadeOrcamentaria(
                    estadoBem.getDetentoraOrcamentaria(),
                    selecionado.getDataDoLancamento()));
                selecionado.setEstadoInicial(estadoBem);
                selecionado.setEstadoResultante(estadoBem);
            } else {
                selecionado.setEstadoInicial(null);
                selecionado.setEstadoResultante(null);
            }
        } catch (ValidacaoException ve) {
            selecionado.setBem(null);
            FacesUtil.atualizarComponente("Formulario:tabGeral:acBem");
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void validarAjustePerda() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValorAvaria().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Valor da Avaria não pode ser menor ou igual a 0(zero).");
        }
        avariaBem();
        Bem bemParaValidacao = facade.getBemFacade().recuperar(selecionado.getBem().getId());
        if (bemParaValidacao.getValorLiquido().compareTo(selecionado.getValorAvaria()) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da Avaria deve ser superior ou igual ao valor liquido do bem (" + new MoneyConverter().getAsString(null, null, bemParaValidacao.getValorLiquido()) + ").");
        }
        validarRegrasConfiguracaoMovimentacaoBem();
        ve.lancarException();
    }

    private void avariaBem() {
        EstadoBem estadoBemResultante = facade.getBemFacade().criarNovoEstadoResultanteAPartirDoEstadoInicial(selecionado.getEstadoInicial());
        estadoBemResultante.setValorAcumuladoDeAjuste(estadoBemResultante.getValorAcumuladoDeAjuste().add(selecionado.getValorAvaria()));
        selecionado.setEstadoResultante(estadoBemResultante);
    }

    @Override
    public void salvar() {
        try {
            iniciarAssistenteMovimentacao();
            validarAjustePerda();
            bloquearMovimentacaoBens();
            if (isOperacaoNovo()) {
                facade.salvarNovo(selecionado);
            } else {
                facade.salvar(selecionado);
            }
            desbloquearMovimentacaoSingleton();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (MovimentacaoBemException ex) {
            if (!isOperacaoNovo()) {
                redireciona();
            }
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            descobrirETratarException(e);
        }
    }

    private void iniciarAssistenteMovimentacao() {
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getDataLancamento(), operacao);
        assistenteMovimentacao.setSelecionado(selecionado);
        assistenteMovimentacao.getBensSelecionados().add(selecionado.getBem());
        assistenteMovimentacao.setUnidadeOrganizacional(selecionado.getUnidadeOrganizacional());
        ConfigMovimentacaoBem configMovimentacaoBem = recuperarConfiguracaoMovimentacaoBem();
        assistenteMovimentacao.setConfigMovimentacaoBem(configMovimentacaoBem);
    }

    private void desbloquearMovimentacaoSingleton() {
        facade.getSingletonBloqueioPatrimonio().desbloquearMovimentacaoSingleton(assistenteMovimentacao, AvariaBem.class);
    }

    private void bloquearMovimentacaoBens() {

        facade.getSingletonBloqueioPatrimonio().bloquearBem(selecionado.getBem(), assistenteMovimentacao);
        FacesUtil.executaJavaScript("aguarde.hide()");
        FacesUtil.atualizarComponente("Formulario");
        assistenteMovimentacao.lancarMensagemBensBloqueioSingleton();
    }

    public void estornarAvariaBem(AvariaBem avariaBem) {
        try {
            iniciarAssistenteMovimentacao();
            bloquearMovimentacaoBens();
            facade.estornarAvariaBem(avariaBem);
            desbloquearMovimentacaoSingleton();
            FacesUtil.addOperacaoRealizada("Estorno realizado com sucesso.");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (MovimentacaoBemException ex) {
            if (!isOperacaoNovo()) {
                redireciona();
            }
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            descobrirETratarException(e);
        }
    }

    public void limparCamposUnidadeAdministrativa() {
        selecionado.setHierarquiaOrganizacional(null);
        selecionado.setBem(null);
        setHierarquiaOrganizacionalOrcamentaria(null);
    }

    public void limparCampoUnidadeOrcamentaria() {
        setHierarquiaOrganizacionalOrcamentaria(null);
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalOrcamentaria() {
        return hierarquiaOrganizacionalOrcamentaria;
    }

    public void setHierarquiaOrganizacionalOrcamentaria(HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria) {
        this.hierarquiaOrganizacionalOrcamentaria = hierarquiaOrganizacionalOrcamentaria;
    }

    public void limparCampos() {
        selecionado.setBem(null);
    }

    public String getDescricaoEventoContabil() {
        return facade.buscarDescricaoEventoContabil(selecionado);
    }

    public String getDescricaoTipoOperacao() {
        return facade.buscarDescricaoTipoOperacao(selecionado);
    }

    public AssistenteMovimentacaoBens getAssistenteMovimentacao() {
        return assistenteMovimentacao;
    }

    public void setAssistenteMovimentacao(AssistenteMovimentacaoBens assistenteMovimentacao) {
        this.assistenteMovimentacao = assistenteMovimentacao;
    }
}
