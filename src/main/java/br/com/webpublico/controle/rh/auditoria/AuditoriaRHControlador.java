package br.com.webpublico.controle.rh.auditoria;

import br.com.webpublico.controle.Web;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidadesauxiliares.rh.auditoria.AssistenteAuditoriaRH;
import br.com.webpublico.entidadesauxiliares.rh.auditoria.ObjetoAuditoriaRH;
import br.com.webpublico.enums.TipoRevisaoAuditoria;
import br.com.webpublico.enums.rh.TipoAuditoriaRH;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.rh.auditoria.AuditoriaRHFacade;
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
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author octavio
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "ver-auditoria-rh", pattern = "/auditoria/consultar/", viewId = "/faces/rh/auditoria/visualizar.xhtml")
})
public class AuditoriaRHControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(AuditoriaRHControlador.class);
    @EJB
    private AuditoriaRHFacade auditoriaRHFacade;
    private AssistenteAuditoriaRH assistente;
    private List<TreeMap<ObjetoAuditoriaRH, Object>> atributos;
    private List<TreeMap<ObjetoAuditoriaRH, Object>> atributosVer;
    private Long idAuditoria;
    private Long idRevisao;
    private Class classe;

    @URLAction(mappingId = "ver-auditoria-rh", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verAuditoriaRH() {
        assistente = new AssistenteAuditoriaRH();
        atributos = Lists.newArrayList();
        atributosVer = Lists.newArrayList();
    }

    public AssistenteAuditoriaRH getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteAuditoriaRH assistente) {
        this.assistente = assistente;
    }

    public List<TreeMap<ObjetoAuditoriaRH, Object>> getAtributos() {
        return atributos;
    }

    public void setAtributos(List<TreeMap<ObjetoAuditoriaRH, Object>> atributos) {
        this.atributos = atributos;
    }

    public List<TreeMap<ObjetoAuditoriaRH, Object>> getAtributosVer() {
        return atributosVer;
    }

    public void setAtributosVer(List<TreeMap<ObjetoAuditoriaRH, Object>> atributosVer) {
        this.atributosVer = atributosVer;
    }

    public Long getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(Long idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public Long getIdRevisao() {
        return idRevisao;
    }

    public void setIdRevisao(Long idRevisao) {
        this.idRevisao = idRevisao;
    }

    public Class getClasse() {
        return classe;
    }

    public void setClasse(Class classe) {
        this.classe = classe;
    }

    public List<SelectItem> tiposAuditoriaRH() {
        return Util.getListSelectItem(TipoAuditoriaRH.values());
    }

    public List<SelectItem> tiposRevisaoAuditoria() {
        return Util.getListSelectItem(TipoRevisaoAuditoria.values());
    }

    public List<UsuarioSistema> completarUsuarioSistema(String parte) {
        return auditoriaRHFacade.getUsuarioSistemaFacade().completarUsuarioSistemaPeloNomePessoaFisica(parte.trim());
    }

    public List<VinculoFP> completarContrato(String parte) {
        return auditoriaRHFacade.getContratoFPFacade().buscaContratoFiltrandoAtributosVinculoMatriculaFP(parte.trim());
    }

    public void buscarAuditoria(Boolean isVerAudRelacionadas, Long idAuditoria) {
        try {
            if (assistente != null) {
                assistente.setIdAuditoria(null);
                validarBuscarAuditoria(assistente);
                if (isVerAudRelacionadas) {
                    atributosVer.clear();
                    assistente.setIdAuditoria(idAuditoria);
                    atributosVer = auditoriaRHFacade.consultar(assistente);
                } else {
                    atributos.clear();
                    atributos = auditoriaRHFacade.consultar(assistente);
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao buscar auditoria: ", e);
        }
    }

    public void limparCampos() {
        assistente = new AssistenteAuditoriaRH();
        atributos = Lists.newArrayList();
        atributosVer = Lists.newArrayList();
    }

    public void visualizarAuditoriasAssociadas(Map<ObjetoAuditoriaRH, Object> atributo) {
        Long idAuditoria = Long.valueOf(atributo.get(new ObjetoAuditoriaRH(1)).toString());
        buscarAuditoria(Boolean.TRUE, idAuditoria);

        FacesUtil.executaJavaScript("dialogRevAssociadas.show();");
        FacesUtil.executaJavaScript("aguarde.hide()");
    }

    public void visualizarRevisao(Map<ObjetoAuditoriaRH, Object> atributo) {
        Long idAud = Long.valueOf(atributo.get(new ObjetoAuditoriaRH(1)).toString());
        Long idRev = Long.valueOf(atributo.get(new ObjetoAuditoriaRH(2)).toString());

        this.classe = assistente.getTipoAuditoriaRH().getClasse();
        if (TipoAuditoriaRH.CONCESSAO_LICENCA.equals(getAssistente().getTipoAuditoriaRH())) {
            Web.poeNaSessao("CONCESSAO", TipoAuditoriaRH.CONCESSAO_LICENCA);
        } else if (TipoAuditoriaRH.CONCESSAO_FERIAS.equals(getAssistente().getTipoAuditoriaRH())) {
            Web.poeNaSessao("CONCESSAO", TipoAuditoriaRH.CONCESSAO_FERIAS);
        }
        this.idAuditoria = idAud;
        this.idRevisao = idRev;
    }

    private void validarBuscarAuditoria(AssistenteAuditoriaRH assistente) {
        ValidacaoException ve = new ValidacaoException();
        if (assistente != null) {
            if (assistente.getTipoAuditoriaRH() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe a funcionalidade.");
            }
            if (assistente.getTipoRevisao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o tipo de revisão.");
            }
            if (assistente.getDataInicial() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe a data inicial.");
            }
            if (assistente.getDataInicial() != null && assistente.getDataFinal() != null && assistente.getDataInicial().after(assistente.getDataFinal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data inicial não pode ser posterio a data final.");
            }
        }
        ve.lancarException();
    }
}
