package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.ConfigMovimentacaoBem;
import br.com.webpublico.entidades.administrativo.patrimonio.ManutencaoBemMovelEntrada;
import br.com.webpublico.entidades.administrativo.patrimonio.ManutencaoBemMovelRetorno;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.administrativo.patrimonio.ManutencaoBensMoveisRetornoFacade;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by zaca on 15/05/17.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-manutencao-moveis-retorno",
        pattern = "/patrimonio/manutencao-bem-moveis-retorno/novo/",
        viewId = "/faces/administrativo/patrimonio/manutencaobemmoveisretorno/editar.xhtml"),
    @URLMapping(id = "editar-manutencao-moveis-retorno",
        pattern = "/patrimonio/manutencao-bem-moveis-retorno/editar/#{manutencaoBensMoveisRetornoControlador.id}/",
        viewId = "/faces/administrativo/patrimonio/manutencaobemmoveisretorno/editar.xhtml"),
    @URLMapping(id = "ver-manutencao-moveis-retorno",
        pattern = "/patrimonio/manutencao-bem-moveis-retorno/ver/#{manutencaoBensMoveisRetornoControlador.id}/",
        viewId = "/faces/administrativo/patrimonio/manutencaobemmoveisretorno/visualizar.xhtml"),
    @URLMapping(id = "listar-manutencao-moveis-retorno",
        pattern = "/patrimonio/manutencao-bem-moveis-retorno/listar/",
        viewId = "/faces/administrativo/patrimonio/manutencaobemmoveisretorno/listar.xhtml")
})
public class ManutencaoBensMoveisRetornoControlador extends PrettyControlador<ManutencaoBemMovelRetorno> implements CRUD {

    @EJB
    private ManutencaoBensMoveisRetornoFacade facade;
    private AssistenteMovimentacaoBens assistenteMovimentacao;

    @Override
    public String getCaminhoPadrao() {
        return "/patrimonio/manutencao-bem-moveis-retorno/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public ManutencaoBensMoveisRetornoControlador() {
        super(ManutencaoBemMovelRetorno.class);
    }

    @URLAction(mappingId = "novo-manutencao-moveis-retorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        try {
            super.novo();
            selecionado.setRetornoEm(getDataOperacao());
            selecionado.setValorManutencao(BigDecimal.ZERO);
            recuperarConfiguracaoMovimentacaoBem();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private ConfigMovimentacaoBem recuperarConfiguracaoMovimentacaoBem() {
        return facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getRetornoEm(), OperacaoMovimentacaoBem.MANUTENCAO_BEM_RETORNO);
    }

    @URLAction(mappingId = "editar-manutencao-moveis-retorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-manutencao-moveis-retorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            iniciarAssistenteMovimentacao();
            validarSelecionado();
            validarMovimentacaoBemComDataRetroativa();
            bloquearMovimentacaoBens();
            selecionado = facade.salvarRetornoManutencao(selecionado, assistenteMovimentacao.getConfigMovimentacaoBem());
            desbloquearMovimentacaoSingleton();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } catch (MovimentacaoBemException ex) {
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
        assistenteMovimentacao.setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
        assistenteMovimentacao.getBensSelecionados().add(selecionado.getBem());
        ConfigMovimentacaoBem configMovimentacaoBem = recuperarConfiguracaoMovimentacaoBem();
        assistenteMovimentacao.setConfigMovimentacaoBem(configMovimentacaoBem);
    }

    private void desbloquearMovimentacaoSingleton() {
        facade.getSingletonBloqueioPatrimonio().desbloquearBem(selecionado.getBem());
    }

    private void bloquearMovimentacaoBens() {

        facade.getSingletonBloqueioPatrimonio().bloquearBem(selecionado.getBem(), assistenteMovimentacao);
        FacesUtil.executaJavaScript("aguarde.hide()");
        FacesUtil.atualizarComponente("Formulario");
        assistenteMovimentacao.lancarMensagemBensBloqueioSingleton();
    }

    private void validarSelecionado() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getBemMovelEntrada() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Bem deve ser informado.");
        }
        ve.lancarException();
        if (selecionado.getRetornoEm() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Retorno da Manutenção deve ser informado.");
        }
        if (StringUtils.isEmpty(selecionado.getManutencaoRealizada())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Manutenção Realizada deve ser informado.");
        }
        ve.lancarException();
        if (selecionado.getBemMovelEntrada().getInicioEm() != null && selecionado.getRetornoEm().before(selecionado.getBemMovelEntrada().getInicioEm())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de retorno deve ser superior ou igual a data de início da manutenção.");
        }
        if (!facade.getManutencaoBensMoveisEntradaFacade().isManutencaoEmRetorno(selecionado.getBemMovelEntrada())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Já existe um retorno para a manutenção: " + selecionado.getBemMovelEntrada() + ".");
        }
        ve.lancarException();
    }

    public List<ManutencaoBemMovelEntrada> buscarBensMoveis(String filter) {
        return facade.buscarBensEmManutencao(filter, TipoBem.MOVEIS);
    }

    public void buscarEntradaManutencaoPorBem() {
        try {
            iniciarAssistenteMovimentacao();
            if (selecionado.getBem() != null) {
                if (assistenteMovimentacao.getConfigMovimentacaoBem() == null) {
                    recuperarConfiguracaoMovimentacaoBem();
                }
                ManutencaoBemMovelEntrada entrada = facade.buscarEntradaManutencaoBemPorBem(selecionado.getBem(), TipoBem.MOVEIS);
                if (entrada == null) {
                    FacesUtil.addOperacaoRealizada("Não foi possível localizar a entrada desse bem em manutenção.");
                } else {
                    validarMovimentacaoBemComDataRetroativa();
                    selecionado.setBemMovelEntrada(entrada);
                    selecionado.setManutencaoRealizada(entrada.getManutencaoProposta());
                }
            }

        } catch (ValidacaoException ve) {
            selecionado.setBem(null);
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            selecionado.setBem(null);
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void validarMovimentacaoBemComDataRetroativa() {
        if (selecionado.getBem() != null && assistenteMovimentacao.getConfigMovimentacaoBem() != null && assistenteMovimentacao.getConfigMovimentacaoBem().getValidarMovimentoRetroativo()) {
            String msgValidacao = facade.getConfigMovimentacaoBemFacade().validarMovimentoComDataRetroativaBem(selecionado.getBem().getId(), assistenteMovimentacao.getConfigMovimentacaoBem().getOperacaoMovimentacaoBem().getDescricao(), selecionado.getRetornoEm());
            if (!Strings.isNullOrEmpty(msgValidacao)) {
                ValidacaoException ve = new ValidacaoException();
                ve.adicionarMensagemDeOperacaoNaoPermitida(msgValidacao);
                selecionado.setBem(null);
                ve.lancarException();
            }
        }
        if (assistenteMovimentacao.getConfigMovimentacaoBem() != null) {
            assistenteMovimentacao.getConfigMovimentacaoBem().validarDatasMovimentacao(selecionado.getRetornoEm(), getDataOperacao(), operacao);
        }
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    public AssistenteMovimentacaoBens getAssistenteMovimentacao() {
        return assistenteMovimentacao;
    }

    public void setAssistenteMovimentacao(AssistenteMovimentacaoBens assistenteMovimentacao) {
        this.assistenteMovimentacao = assistenteMovimentacao;
    }
}
