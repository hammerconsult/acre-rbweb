package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ParametroPatrimonio;
import br.com.webpublico.entidades.ResponsavelPatrimonio;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ParametroPatrimonioFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by mga on 02/01/2018.
 */
@ManagedBean(name = "responsavelBensMoveisPorUnidadeAdministrativaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoResponsavelBensMoveisUnidade", pattern = "/responsavel-bens-moveis-unidade-administrativa/novo/", viewId = "/faces/administrativo/patrimonio/resp-bens-moveis-unidade-adm/edita.xhtml"),
    @URLMapping(id = "editarResponsavelBensMoveisUnidade", pattern = "/responsavel-bens-moveis-unidade-administrativa/editar/#{responsavelBensMoveisPorUnidadeAdministrativaControlador.id}/", viewId = "/faces/administrativo/patrimonio/resp-bens-moveis-unidade-adm/edita.xhtml"),
    @URLMapping(id = "listarResponsavelBensMoveisUnidade", pattern = "/responsavel-bens-moveis-unidade-administrativa/listar/", viewId = "/faces/administrativo/patrimonio/resp-bens-moveis-unidade-adm/lista.xhtml"),
    @URLMapping(id = "verResponsavelBensMoveisUnidade", pattern = "/responsavel-bens-moveis-unidade-administrativa/ver/#{responsavelBensMoveisPorUnidadeAdministrativaControlador.id}/", viewId = "/faces/administrativo/patrimonio/resp-bens-moveis-unidade-adm/visualizar.xhtml")
})
public class ResponsavelBensMoveisPorUnidadeAdministrativaControlador extends PrettyControlador<ResponsavelPatrimonio> implements Serializable, CRUD {

    @EJB
    private ParametroPatrimonioFacade facade;
    private ParametroPatrimonio parametroPatrimonio;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public ResponsavelBensMoveisPorUnidadeAdministrativaControlador() {
        super(ResponsavelPatrimonio.class);
    }

    @URLAction(mappingId = "novoResponsavelBensMoveisUnidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        if (facade.naoExisteParametro()) {
            redireciona();
            FacesUtil.addAtencao("Para cadastrar um novo responsável, é necessário que já tenha um parâmetro do patimônio cadastrado no sistema.");
        } else {
            recuperarParametroPatrimonio();
        }
    }

    @URLAction(mappingId = "verResponsavelBensMoveisUnidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        selecionado = facade.recuperarResonsavelPorUnidade(getId());
        recuperarHierarquiaPorUnidade();
    }

    @URLAction(mappingId = "editarResponsavelBensMoveisUnidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        selecionado = facade.recuperarResonsavelPorUnidade(getId());
        recuperarHierarquiaPorUnidade();
        recuperarParametroPatrimonio();
    }

    private void recuperarHierarquiaPorUnidade() {
        this.hierarquiaOrganizacional = facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(
            facade.getSistemaFacade().getDataOperacao(),
            selecionado.getUnidadeOrganizacional(),
            TipoHierarquiaOrganizacional.ADMINISTRATIVA);
    }

    private void recuperarParametroPatrimonio() {
        parametroPatrimonio = facade.getParametroPatrimonio();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/responsavel-bens-moveis-unidade-administrativa/";
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
            validarCamposControle();
            adicionarResponsavelAoParametroPatrimonio();
            facade.salvarResponsavel(selecionado);
            FacesUtil.addOperacaoRealizada("Responsável salvo com sucesso.");
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao salvar o responsável do patrimônio: {}", e);
            descobrirETratarException(e);
        }
    }

    public void encerrarVigencia() {
        try {
            validarEncerrarVigencia();
            facade.salvarResponsavel(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void cancelarEncerrarVigencia() {
        selecionado.setFimVigencia(null);
    }

    private void validarEncerrarVigencia() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getFimVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo fim de vigência deve ser informado.");
        }
        ve.lancarException();
        if (selecionado.getFimVigencia().before(selecionado.getInicioVigencia())) {
            ve.adicionarMensagemDeCampoObrigatorio("O fim de vigência dever ser igual ou posterior ao início de vigência.");
        }
        ve.lancarException();
    }

    private void adicionarResponsavelAoParametroPatrimonio() {
        selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        selecionado.setParametroPatrimonio(parametroPatrimonio);
    }

    private void validarCamposControle() {
        ValidacaoException ve = new ValidacaoException();
        if (parametroPatrimonio == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Parâmetro do Patrimônio não encontrado para esta operação");
        }
        selecionado.realizarValidacoes();
        ve.lancarException();
        if (selecionado.getInicioVigencia().before(DataUtil.dataSemHorario(facade.getSistemaFacade().getDataOperacao()))) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "O início de vigência deve ser igual ou posterior a data atual.");
        }
        if (selecionado.getFimVigencia() != null) {
            if (selecionado.getFimVigencia().before(DataUtil.dataSemHorario(facade.getSistemaFacade().getDataOperacao()))) {
                ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "O fim de vigência deve ser igual ou posterior a data atual.");
            }
            if (selecionado.getFimVigencia().before(selecionado.getInicioVigencia())) {
                ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "O fim de vigência deve ser igual ou posterior ao início de vigência.");
            }
        }
        if (facade.verificarSeExisteResponsavelUnidade(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um responsável vigente para a unidade: " + hierarquiaOrganizacional + ".");
        }
        ve.lancarException();
    }

    @Override
    public void excluir() {
        facade.removerResponsavel(selecionado);
        FacesUtil.addOperacaoRealizada("Responsável excluído com sucesso.");
        redireciona();
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        if (hierarquiaOrganizacional != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        }
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}
