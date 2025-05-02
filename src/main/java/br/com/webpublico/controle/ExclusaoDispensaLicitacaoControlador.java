package br.com.webpublico.controle;

import br.com.webpublico.entidades.DispensaDeLicitacao;
import br.com.webpublico.entidades.ExclusaoDispensaLicitacao;
import br.com.webpublico.entidadesauxiliares.RelacionamentoLicitacao;
import br.com.webpublico.enums.ModalidadeLicitacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ExclusaoDispensaLicitacaoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaExclusaoDispensaLicitacao", pattern = "/exclusao-dispensa-de-licitacao/novo/", viewId = "/faces/administrativo/licitacao/exclusao-dispensa-licitacao/edita.xhtml"),
    @URLMapping(id = "listarExclusaoDispensaLicitacao", pattern = "/exclusao-dispensa-de-licitacao/listar/", viewId = "/faces/administrativo/licitacao/exclusao-dispensa-licitacao/lista.xhtml"),
    @URLMapping(id = "verExclusaoDispensaLicitacao", pattern = "/exclusao-dispensa-de-licitacao/ver/#{exclusaoDispensaLicitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/exclusao-dispensa-licitacao/visualizar.xhtml")
})
public class ExclusaoDispensaLicitacaoControlador extends PrettyControlador<ExclusaoDispensaLicitacao> implements Serializable, CRUD {

    @EJB
    private ExclusaoDispensaLicitacaoFacade facade;
    private DispensaDeLicitacao dispensaDeLicitacao;

    private List<RelacionamentoLicitacao> relacionamentos;

    public ExclusaoDispensaLicitacaoControlador() {
        super(ExclusaoDispensaLicitacao.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/exclusao-dispensa-de-licitacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }


    @URLAction(mappingId = "novaExclusaoDispensaLicitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataExclusao(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
    }

    @URLAction(mappingId = "verExclusaoDispensaLicitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarRegrasEspecificas();
            selecionado = facade.salvarRetornando(selecionado, relacionamentos);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void recuperarDadosDispensaLicitacao() {

        if (Util.isNull(selecionado))
            novo();
        if (getDispensaDeLicitacao() != null) {
            setDispensaDeLicitacao(facade.getDispensaDeLicitacaoFacade().recuperar(getDispensaDeLicitacao().getId()));
            selecionado.setIdDispensa(getDispensaDeLicitacao().getId());
            setRelacionamentos(facade.buscarRelacionamentosDispensaLicitacao(getDispensaDeLicitacao()));
            gerarHistorico();
        }
    }


    public boolean hasRelacionamentos() {
        return getRelacionamentos() != null && !getRelacionamentos().isEmpty();
    }

    public void limparDadosDispensaLicitacao() {
        getRelacionamentos().clear();
        setDispensaDeLicitacao(null);
        selecionado.setHistorico(null);
    }

    public List<RelacionamentoLicitacao.Legenda> getLegendas() {
        return Arrays.asList(RelacionamentoLicitacao.Legenda.values());
    }

    public List<SelectItem> getModalidades() {
        return Util.getListSelectItem(ModalidadeLicitacao.getModalidadesLicitacao());
    }

    public List<DispensaDeLicitacao> completarDispensaDeLicitacao(String parte) {
        return facade.getDispensaDeLicitacaoFacade().buscarDispensaDeLicitacaoPorNumeroOrExercicioOrResumoAndUsuarioAndGestor(parte.trim(), facade.getSistemaFacade().getUsuarioCorrente(), true);
    }

    private void validarRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        for (RelacionamentoLicitacao rel : getRelacionamentos()) {
            if (rel.isContrato()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Exclusão não Permitida!", "Essa dispensa de licitação possui contrato vinculado. " +
                    "Para mais detalhes verifique a aba 'Relaciomentos'.");
                break;
            }
        }
        ve.lancarException();
    }

    public void gerarHistorico() {
        StringBuilder historico = new StringBuilder("<b>Dispensa De Licitação: </b> " + getDispensaDeLicitacao().toString());
        for (RelacionamentoLicitacao rel : getRelacionamentos()) {
            historico.append("<br/>").append("<b>").append(rel.getTipo().getDescricao().toUpperCase()).append(":</b> ").append(rel.getMovimento()).append(", <b>ID: </b> ").append(rel.getId());
        }
        selecionado.setHistorico(historico.toString());
    }

    public DispensaDeLicitacao getDispensaDeLicitacao() {
        return dispensaDeLicitacao;
    }

    public void setDispensaDeLicitacao(DispensaDeLicitacao dispensaDeLicitacao) {
        this.dispensaDeLicitacao = dispensaDeLicitacao;
    }

    public List<RelacionamentoLicitacao> getRelacionamentos() {
        return relacionamentos;
    }

    public void setRelacionamentos(List<RelacionamentoLicitacao> relacionamentos) {
        this.relacionamentos = relacionamentos;
    }

    public void exibirRevisao(Long idDispensa) {
        Long idRevtype = facade.getDispensaDeLicitacaoFacade().recuperarIdRevisaoAuditoriaDispensaLicitacao(idDispensa);
        Web.getSessionMap().put("pagina-anterior-auditoria-listar", getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        FacesUtil.redirecionamentoInterno("/auditoria/ver/" + idRevtype + "/" + idDispensa + "/" + DispensaDeLicitacao.class.getSimpleName());
    }

    public void direcionaParaDispensaLicitacao(Long idDispensa){

        FacesUtil.redirecionamentoInterno("/dispensa-licitacao/ver/" + idDispensa);
    }

}
