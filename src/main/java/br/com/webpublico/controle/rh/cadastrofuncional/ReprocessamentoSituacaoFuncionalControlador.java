/*
 * Codigo gerado automaticamente em Mon Nov 14 16:00:21 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle.rh.cadastrofuncional;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.SituacaoContratoFPBkp;
import br.com.webpublico.entidades.rh.cadastrofuncional.ReprocessamentoSituacaoFuncional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.cadastrofuncional.ReprocessamentoSituacaoFuncionalExecutor;
import br.com.webpublico.negocios.rh.cadastrofuncional.ReprocessamentoSituacaoFuncionalFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean(name = "reprocessamentoSituacaoFuncionalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-reprocessamento-situacao-funcional", pattern = "/reprocessamento-situacao-funcional/novo/", viewId = "/faces/rh/administracaodepagamento/reprocessamento-situacao-funcional/edita.xhtml"),
    @URLMapping(id = "lista-reprocessamento-situacao-funcional", pattern = "/reprocessamento-situacao-funcional/listar/", viewId = "/faces/rh/administracaodepagamento/reprocessamento-situacao-funcional/lista.xhtml"),
    @URLMapping(id = "ver-reprocessamento-situacao-funcional", pattern = "/reprocessamento-situacao-funcional/ver/#{reprocessamentoSituacaoFuncionalControlador.id}/", viewId = "/faces/rh/administracaodepagamento/reprocessamento-situacao-funcional/visualizar.xhtml"),
    @URLMapping(id = "consultar-reprocessamento-situacao-funcional", pattern = "/reprocessamento-situacao-funcional/consultar/", viewId = "/faces/rh/administracaodepagamento/reprocessamento-situacao-funcional/consultar.xhtml")
})
public class ReprocessamentoSituacaoFuncionalControlador extends PrettyControlador<ReprocessamentoSituacaoFuncional> implements Serializable, CRUD {

    @EJB
    private ReprocessamentoSituacaoFuncionalFacade reprocessamentoSituacaoFuncionalFacade;
    @EJB
    private SituacaoContratoFPFacade situacaoContratoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    private VinculoFP vinculoFP;
    private List<VinculoFP> itemVinculoFP;

    private Boolean somenteAtivos = true;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Future<AssistenteBarraProgresso> future;

    public ReprocessamentoSituacaoFuncionalControlador() {
        super(ReprocessamentoSituacaoFuncional.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return reprocessamentoSituacaoFuncionalFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/reprocessamento-situacao-funcional/";
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> completarHierarquias(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    @Override
    @URLAction(mappingId = "novo-reprocessamento-situacao-funcional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setResponsavel(sistemaFacade.getUsuarioCorrente());
        itemVinculoFP = Lists.newArrayList();
    }

    public Boolean getSomenteAtivos() {
        return somenteAtivos;
    }

    public void setSomenteAtivos(Boolean somenteAtivos) {
        this.somenteAtivos = somenteAtivos;
    }

    @Override
    @URLAction(mappingId = "ver-reprocessamento-situacao-funcional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public List<VinculoFP> getItemVinculoFP() {
        return itemVinculoFP;
    }

    public void setItemVinculoFP(List<VinculoFP> itemVinculoFP) {
        this.itemVinculoFP = itemVinculoFP;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public void reprocessarSituacaoes() {
        try {
            assistenteBarraProgresso = new AssistenteBarraProgresso();

            if (itemVinculoFP != null && !itemVinculoFP.isEmpty()) {
                assistenteBarraProgresso.setDescricaoProcesso("Reprocessando Situações Funcionais...");
                assistenteBarraProgresso.zerarContadoresProcesso();
                assistenteBarraProgresso.setTotal(itemVinculoFP.size());
                future = new ReprocessamentoSituacaoFuncionalExecutor(situacaoContratoFPFacade, reprocessamentoSituacaoFuncionalFacade).execute(itemVinculoFP, selecionado, assistenteBarraProgresso);
                FacesUtil.executaJavaScript("reprocessarSituacaoes()");
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void verificarTermino() {
        FacesUtil.executaJavaScript("aguarde.hide()");
        if (future != null && future.isDone()) {
            FacesUtil.executaJavaScript("termina()");
            future = null;
            selecionado.setDataProcessamento(new Date());
            super.salvar();
        }
    }

    @Override
    public void salvar() {
        try {
            validarReprocessamentoSituacoes();
            reprocessarSituacaoes();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarReprocessamentoSituacoes() {
        ValidacaoException ve = new ValidacaoException();
        if (itemVinculoFP == null || itemVinculoFP.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado servidores para reprocessar.");
        }
        ve.lancarException();
    }

    public void adicionarVinculo() {
        if (vinculoFP != null) {
            itemVinculoFP.add(vinculoFP);
            vinculoFP = null;
        }
        FacesUtil.atualizarComponente("Formulario");
    }

    public void buscar() {
        try {
            validarBuscaServidores();
            itemVinculoFP = reprocessamentoSituacaoFuncionalFacade.getVinculoPPPorLotacao(hierarquiaOrganizacional, somenteAtivos);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    private void validarBuscaServidores() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Órgão para buscar os servidores.");
        }
        ve.lancarException();
    }

    public List<SituacaoContratoFPBkp> getSituacoesAnteriores() {
        if (vinculoFP != null) {
            return reprocessamentoSituacaoFuncionalFacade.getSituacoesAnteriores(vinculoFP);
        }
        return null;
    }
}
