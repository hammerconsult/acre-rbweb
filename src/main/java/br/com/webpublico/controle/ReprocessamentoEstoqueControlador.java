package br.com.webpublico.controle;


import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LocalEstoque;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamentoEstoque;
import br.com.webpublico.entidadesauxiliares.ItemReprocessamentoEstoque;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ReprocessamentoEstoqueFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-reprocessamento-estoque", pattern = "/reprocessamento-estoque/", viewId = "/faces/administrativo/materiais/reprocessamento-estoque/edita.xhtml")
})
public class ReprocessamentoEstoqueControlador implements Serializable {
    protected static final Logger logger = LoggerFactory.getLogger(ReprocessamentoEstoqueControlador.class);

    @EJB
    private ReprocessamentoEstoqueFacade facade;
    private List<Future<AssistenteReprocessamentoEstoque>> futureList;
    private AssistenteReprocessamentoEstoque assistente;
    private AssistenteBarraProgresso assistenteBarraProgresso;

    public ReprocessamentoEstoqueControlador() {
    }

    @URLAction(mappingId = "novo-reprocessamento-estoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        assistente = new AssistenteReprocessamentoEstoque();
        Date dataInicial = DataUtil.montaData(1, 0, 2019).getTime();
        assistente.setDataInicial(dataInicial);
        assistente.setDataFinal(new Date());

        List<Long> idsMateriais = (List<Long>) Web.pegaDaSessao("IDS_MATERIAIS");
        if (!Util.isListNullOrEmpty(idsMateriais)) {
            assistente.setIdObjeto((Long) Web.pegaDaSessao("ID_OBJETO"));
            assistente.setReprocessamentoSessao(true);
            assistente.setReprocessamento(true);
            for (Long id : idsMateriais) {
                assistente.setIdMaterial(id);
                List<ItemReprocessamentoEstoque> itens = facade.buscarItensEntradaSaidaMaterial(assistente, null);
                assistente.getItens().addAll(itens);
            }
        }
    }

    public void buscarMovimentos() {
        try {
            assistente.validarDatas();
            assistente.setReprocessamento(false);
            futureList = Lists.newArrayList();
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            futureList.add(facade.buscarMovimentos(assistente, assistenteBarraProgresso));
            FacesUtil.executaJavaScript("dialogProgressBar.show()");
            FacesUtil.atualizarComponente("formDialogProgressBar");
            FacesUtil.executaJavaScript("iniciarProcesso()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            cancelarFuture();
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
            cancelarFuture();
        }
    }

    public void reprocessarMovimentos() {
        try {
            if (hasItens()) {
                futureList = Lists.newArrayList();
                assistente.validarDatas();
                assistente.setReprocessamento(true);
                assistenteBarraProgresso = new AssistenteBarraProgresso("Gerando estoque...", assistente.getItens().size());
                futureList.add(facade.reprocessar(assistente, assistenteBarraProgresso, assistente.getItens()));
                FacesUtil.executaJavaScript("dialogProgressBar.show()");
                FacesUtil.atualizarComponente("formDialogProgressBar");
                FacesUtil.executaJavaScript("iniciarProcesso()");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            cancelarFuture();
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
            cancelarFuture();
        }
    }

    private void cancelarFuture() {
        if (!Util.isListNullOrEmpty(futureList)) {
            for (Future<AssistenteReprocessamentoEstoque> future : futureList) {
                future.cancel(true);
            }
        }
        FacesUtil.executaJavaScript("aguarde.hide()");
        FacesUtil.executaJavaScript("dialogProgressBar.hide()");
    }

    public boolean hasItens() {
        return assistente != null && !assistente.getItens().isEmpty();
    }

    public Boolean acompanharProcesso() {
        boolean terminou = false;
        if (!Util.isListNullOrEmpty(futureList)) {
            terminou = true;
            for (Future<AssistenteReprocessamentoEstoque> future : futureList) {
                if (!future.isDone()) {
                    terminou = false;
                    break;
                }
            }
        }
        if (terminou) {
            FacesUtil.executaJavaScript("terminarProcesso()");
        }
        return terminou;
    }

    public void finalizarProcesso() {
        try {
            if (assistente.getReprocessamento()) {
                assistente.getItens().clear();
                if (assistente.getReprocessamentoSessao()) {
                    FacesUtil.addOperacaoRealizada("Exclusão e reprocessamento realizado com sucesso.");
                    FacesUtil.redirecionamentoInterno("/exclusao-materiais/ver/" + assistente.getIdObjeto() + "/");
                    return;
                }
                FacesUtil.addOperacaoRealizada("Reprocessamento realizado com sucesso.");
                novo();
            }
            FacesUtil.atualizarComponente("Formulario");
            FacesUtil.executaJavaScript("dialogProgressBar.hide()");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public List<LocalEstoque> completarLocalEstoque(String s) {
        if (assistente.getHierarquiaAdm() != null) {
            return facade.getLocalEstoqueFacade().buscarLocaisEstoquePorUnidade(
                s.trim(),
                assistente.getHierarquiaAdm().getSubordinada());
        }
        return facade.getLocalEstoqueFacade().buscarPorCodigoOrDescricao(s.trim());
    }

    public List<HierarquiaOrganizacional> completarHierarquiaAdministrativa(String s) {
        return facade.getHierarquiaOrganizacionalFacade().buscarHierarquiaFiltrandoTipoAdministrativaAndHierarquiaSemRaiz(s.trim());
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentaria(String s) {
        if (assistente.getHierarquiaAdm() != null) {
            return facade.getHierarquiaOrganizacionalFacade().buscarFiltrandoHierarquiaOrcamentariaPorUnidadeAdministrativa(
                s.trim(),
                assistente.getHierarquiaAdm().getSubordinada(),
                facade.getSistemaFacade().getDataOperacao());
        }
        return facade.getHierarquiaOrganizacionalFacade().buscarHierarquiaOrganizacionalOrcamentariaPorCodigoOrDescricao(s.trim());
    }

    public AssistenteReprocessamentoEstoque getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteReprocessamentoEstoque assistente) {
        this.assistente = assistente;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }
}
