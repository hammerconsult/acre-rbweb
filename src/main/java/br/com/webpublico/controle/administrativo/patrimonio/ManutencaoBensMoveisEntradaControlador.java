package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.ConfigMovimentacaoBem;
import br.com.webpublico.entidades.administrativo.patrimonio.ManutencaoBemMovelEntrada;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.enums.administrativo.TipoManutencao;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.administrativo.patrimonio.ManutencaoBensMoveisEntradaFacade;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.Date;
import java.util.List;

/**
 * Created by zaca on 12/05/17.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-manutencao-moveis",
        pattern = "/patrimonio/manutencao-bem-moveis-entrada/novo/",
        viewId = "/faces/administrativo/patrimonio/manutencaobemmoveis/editar.xhtml"),

    @URLMapping(id = "editar-manutencao-moveis",
        pattern = "/patrimonio/manutencao-bem-moveis-entrada/editar/#{manutencaoBensMoveisEntradaControlador.id}/",
        viewId = "/faces/administrativo/patrimonio/manutencaobemmoveis/editar.xhtml"),

    @URLMapping(id = "ver-manutencao-moveis",
        pattern = "/patrimonio/manutencao-bem-moveis-entrada/ver/#{manutencaoBensMoveisEntradaControlador.id}/",
        viewId = "/faces/administrativo/patrimonio/manutencaobemmoveis/visualizar.xhtml"),

    @URLMapping(id = "listar-manutencao-moveis",
        pattern = "/patrimonio/manutencao-bem-moveis-entrada/listar/",
        viewId = "/faces/administrativo/patrimonio/manutencaobemmoveis/listar.xhtml")
})
public class ManutencaoBensMoveisEntradaControlador extends PrettyControlador<ManutencaoBemMovelEntrada> implements CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ManutencaoBensMoveisEntradaControlador.class);

    @EJB
    private ManutencaoBensMoveisEntradaFacade facade;
    private AssistenteMovimentacaoBens assistenteMovimentacao;

    public ManutencaoBensMoveisEntradaControlador() {
        super(ManutencaoBemMovelEntrada.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/patrimonio/manutencao-bem-moveis-entrada/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novo-manutencao-moveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        try {
            super.novo();
            selecionado.setTipoManutencao(TipoManutencao.PREVENTIVA);
            selecionado.setInicioEm(getDataOperacao());
            iniciarAssistenteMovimentacao();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "editar-manutencao-moveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-manutencao-moveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            realizarValidacao();
            bloquearMovimentacaoBens();
            selecionado = facade.salvarManutencao(selecionado, assistenteMovimentacao.getConfigMovimentacaoBem());
            desbloquearMovimentacaoSingleton();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
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
        assistenteMovimentacao.setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
        assistenteMovimentacao.getBensSelecionados().add(selecionado.getBem());
        assistenteMovimentacao.setConfigMovimentacaoBem(recuperarConfiguracaoMovimentacaoBem());
    }

    private void desbloquearMovimentacaoSingleton() {
        facade.getSingletonBloqueioPatrimonio().desbloquearMovimentacaoSingleton(assistenteMovimentacao, ManutencaoBemMovelEntrada.class);
    }

    private void bloquearMovimentacaoBens() {
        if (isOperacaoNovo()) {
            facade.getSingletonBloqueioPatrimonio().bloquearBem(selecionado.getBem(), assistenteMovimentacao);
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.atualizarComponente("Formulario");
            assistenteMovimentacao.lancarMensagemBensBloqueioSingleton();
        } else {
            facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(ManutencaoBemMovelEntrada.class, facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente(), assistenteMovimentacao);
        }
    }

    public void verificarSeBemPossuiBloqueioParaMovimentacao() {
        try {
            FiltroPesquisaBem filtro = new FiltroPesquisaBem(TipoBem.MOVEIS, selecionado);
            filtro.setBem(selecionado.getBem());
            filtro.setDataOperacao(selecionado.getInicioEm());
            iniciarAssistenteMovimentacao();
            facade.getConfigMovimentacaoBemFacade().verificarSeBemPossuiBloqueioParaMovimentacao(filtro, assistenteMovimentacao.getConfigMovimentacaoBem());
            facade.getSingletonBloqueioPatrimonio().verificarBloqueioBemLancandoException(selecionado.getBem(), assistenteMovimentacao);

        } catch (MovimentacaoBemException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ve) {
            selecionado.setBem(null);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            selecionado.setBem(null);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private ConfigMovimentacaoBem recuperarConfiguracaoMovimentacaoBem() {
        return facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataOperacao(), OperacaoMovimentacaoBem.MANUTENCAO_BEM_REMESSA);
    }

    private void validarDataLancamentoAndDataOperacaoBem() {
        if (assistenteMovimentacao.getConfigMovimentacaoBem() != null) {
            assistenteMovimentacao.getConfigMovimentacaoBem().validarDatasMovimentacao(selecionado.getInicioEm(), getDataOperacao(), operacao);
        }
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    private void realizarValidacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getBem() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Bem deve ser informado.");
        }
        if (selecionado.getTipoManutencao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" o campo Tipo de Manutenção deve ser informado.");
        }
        if (!ve.temMensagens() && facade.isBemEmManutencao(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Já existe uma manutenção em andamento do tipo " + selecionado.getTipoManutencao().getDescricao() + " para o bem " + selecionado.getBem());
        }
        if (StringUtils.isEmpty(selecionado.getManutencaoProposta())) {
            ve.adicionarMensagemDeCampoObrigatorio(" o campo Manutenção a Realizar deve ser informado.");
        }
        if (selecionado.getInicioEm() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Início da Manutenção deve ser informado.");
        }
        ve.lancarException();
        validarDataLancamentoAndDataOperacaoBem();
    }

    public List<Bem> completarBensMoveis(String filter) {
        return facade.getBemFacade().buscarBensPorTipoBem(filter, TipoBem.MOVEIS);
    }

    public List<SelectItem> getTiposManutencao() {
        return Util.getListSelectItem(TipoManutencao.values());
    }

    public Boolean isSeguroVigente() {
        return this.selecionado.getBem() != null
            && this.selecionado.getBem().getSeguradora() != null
            && this.selecionado.getBem().getSeguradora().getVencimento() != null
            && this.selecionado.getBem().getSeguradora().getVencimento().compareTo(getDataOperacao()) >= 0;
    }


    public Boolean isGarantiaVigente() {
        return this.selecionado.getBem() != null
            && this.selecionado.getBem().getGarantia() != null
            && this.selecionado.getBem().getGarantia().getDataVencimento() != null
            && this.selecionado.getBem().getGarantia().getDataVencimento().compareTo(getDataOperacao()) >= 0;
    }

    public Boolean isManutencaoEmRetorno() {
        return selecionado.getId() != null && !facade.isManutencaoEmRetorno(selecionado);
    }

    public AssistenteMovimentacaoBens getAssistenteMovimentacao() {
        return assistenteMovimentacao;
    }

    public void setAssistenteMovimentacao(AssistenteMovimentacaoBens assistenteMovimentacao) {
        this.assistenteMovimentacao = assistenteMovimentacao;
    }
}
