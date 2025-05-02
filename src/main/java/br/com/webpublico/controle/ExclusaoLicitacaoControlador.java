package br.com.webpublico.controle;

import br.com.webpublico.entidades.ExclusaoLicitacao;
import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.entidadesauxiliares.RelacionamentoLicitacao;
import br.com.webpublico.enums.ModalidadeLicitacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ExclusaoLicitacaoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
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
    @URLMapping(id = "novaExclusaoLicitacao", pattern = "/exclusao-licitacao/novo/", viewId = "/faces/administrativo/licitacao/exclusao-licitacao/edita.xhtml"),
    @URLMapping(id = "listarExclusaoLicitacao", pattern = "/exclusao-licitacao/listar/", viewId = "/faces/administrativo/licitacao/exclusao-licitacao/lista.xhtml"),
    @URLMapping(id = "verExclusaoLicitacao", pattern = "/exclusao-licitacao/ver/#{exclusaoLicitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/exclusao-licitacao/visualizar.xhtml")
})
public class ExclusaoLicitacaoControlador extends PrettyControlador<ExclusaoLicitacao> implements Serializable, CRUD {

    @EJB
    private ExclusaoLicitacaoFacade facade;

    public ExclusaoLicitacaoControlador() {
        super(ExclusaoLicitacao.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/exclusao-licitacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novaExclusaoLicitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataExclusao(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setModalidadeLicitacao(null);
    }

    @URLAction(mappingId = "verExclusaoLicitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarRegrasEspecificas();
            selecionado = facade.salvarRetornando(selecionado);
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

    public void recuperarDadosLicitacao() {
        if (selecionado.getLicitacao() != null) {
            selecionado.setLicitacao(facade.getLicitacaoFacade().recuperar(selecionado.getLicitacao().getId()));
            selecionado.setIdLicitacao(selecionado.getLicitacao().getId());
            selecionado.setRelacionamentos(facade.buscarRelacionamentosLicitacao(selecionado.getLicitacao()));
            selecionado.gerarHistorico();
        }
    }

    public boolean hasRelacionamentos() {
        return selecionado.getRelacionamentos() != null && !selecionado.getRelacionamentos().isEmpty();
    }

    public void limparDadosLicitacao() {
        selecionado.getRelacionamentos().clear();
        selecionado.setLicitacao(null);
        selecionado.setHistorico(null);
    }

    public List<Licitacao> completarLicitacoes(String parte) {
        if (selecionado.getModalidadeLicitacao() != null) {
            return facade.getLicitacaoFacade().buscarLicitacoesPorModalidadeAndNumeroOrDescricaoOrExercico(parte.trim(), selecionado.getModalidadeLicitacao());
        }
        return Lists.newArrayList();
    }

    public List<RelacionamentoLicitacao.Legenda> getLegendas() {
        return Arrays.asList(RelacionamentoLicitacao.Legenda.values());
    }

    public List<SelectItem> getModalidades() {
        return Util.getListSelectItem(ModalidadeLicitacao.getModalidadesLicitacao());
    }

    private void validarRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        for (RelacionamentoLicitacao rel : selecionado.getRelacionamentos()) {
            if (rel.isContrato()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Exclusão não Permitida!", "Essa licitação possui contrato vinculado. " +
                    "Para mais detalhes verifique a aba 'Relaciomentos'.");
                break;
            }
        }
        ve.lancarException();
    }

    public void exibirRevisao(Long idLicitacao) {
        Long idRevtype = facade.getLicitacaoFacade().recuperarIdRevisaoAuditoriaLicitacao(idLicitacao);
        Web.getSessionMap().put("pagina-anterior-auditoria-listar", getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        FacesUtil.redirecionamentoInterno("/auditoria/ver/" + idRevtype + "/" + idLicitacao + "/" + Licitacao.class.getSimpleName());
    }
}
