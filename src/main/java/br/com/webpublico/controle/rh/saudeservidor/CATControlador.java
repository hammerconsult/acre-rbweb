package br.com.webpublico.controle.rh.saudeservidor;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.CID;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.Medico;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.saudeservidor.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.esocial.IniciativaCAT;
import br.com.webpublico.enums.rh.esocial.LateralidadeParteAtingida;
import br.com.webpublico.enums.rh.esocial.OrgaoClasse;
import br.com.webpublico.esocial.comunicacao.enums.TipoInscricaoESocial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.negocios.rh.saudeservidor.*;
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
import java.util.List;

/**
 * @Author peixe on 13/09/2016  18:04.
 */
@ManagedBean(name = "cATControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoCAT", pattern = "/cat/novo/", viewId = "/faces/rh/saudeservidor/cat/edita.xhtml"),
    @URLMapping(id = "editarCAT", pattern = "/cat/editar/#{cATControlador.id}/", viewId = "/faces/rh/saudeservidor/cat/edita.xhtml"),
    @URLMapping(id = "listarCAT", pattern = "/cat/listar/", viewId = "/faces/rh/saudeservidor/cat/lista.xhtml"),
    @URLMapping(id = "verCAT", pattern = "/cat/ver/#{cATControlador.id}/", viewId = "/faces/rh/saudeservidor/cat/visualizar.xhtml")
})
public class CATControlador extends PrettyControlador<CAT> implements Serializable, CRUD {
    @EJB
    private CATFacade catFacade;
    @EJB
    private CIDFacade CIDFacade;
    @EJB
    private MedicoFacade medicoFacade;

    @EJB
    private NaturezaLesaoFacade naturezaLesaoFacade;

    @EJB
    private ParteCorpoFacade parteCorpoFacade;
    @EJB
    private AgenteAcidenteTrabalhoFacade agenteAcidenteTrabalhoFacade;
    @EJB
    private AgenteDoencaProfissionalFacade agenteDoencaProfissionalFacade;
    @EJB
    private SituacaoAcidenteFacade situacaoAcidenteFacade;
    @EJB
    private UFFacade ufFacade;

    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;

    @EJB
    private ContratoFPFacade contratoFPFacade;

    public CATControlador() {
        super(CAT.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cat/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return catFacade;
    }

    @URLAction(mappingId = "novoCAT", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verCAT", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarCAT", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public List<SelectItem> getTiposAcidente() {
        return Util.getListSelectItem(TipoAcidenteCAT.values());
    }

    public List<SelectItem> getTiposCAT() {
        return Util.getListSelectItem(TipoCAT.values());
    }

    public List<SelectItem> getOrigensCat() {
        return Util.getListSelectItem(OrigemCAT.values());
    }

    public List<SelectItem> getLocaisAcidente() {
        return Util.getListSelectItem(LocalAcidente.values(), false);
    }

    public List<SelectItem> getTipoLogradouros() {
        return Util.getListSelectItem(TipoLogradouroEnderecoCorreio.values(), false);
    }

    public List<SelectItem> getUf() {
        return Util.getListSelectItem(ufFacade.lista());
    }

    public List<SelectItem> getTipoInscricaoEsocial() {
        return Util.getListSelectItem(TipoInscricaoESocial.getTipoInscricaoEventoS2210(), false);
    }

    public List<SelectItem> getLateralidadeParteAtingida() {
        return Util.getListSelectItem(LateralidadeParteAtingida.values(), false);
    }

    public List<SelectItem> getLesoes() {
        return Util.getListSelectItem(Lesoes.values());
    }

    public List<SelectItem> getPrazos() {
        return Util.getListSelectItem(TipoPrazo.values());
    }

    public List<SelectItem> getOrgaoClasse() {
        return Util.getListSelectItem(OrgaoClasse.values());
    }

    public List<SelectItem> getIniciativaCAT() {
        return Util.getListSelectItem(IniciativaCAT.values());
    }

    public List<CID> buscarCids(String parte) {
        return CIDFacade.listaFiltrando(parte.trim(), "descricao", "codigoDaCid");
    }


    public List<Medico> buscarMedicos(String parte) {
        return medicoFacade.listaFiltrandoMedico(parte);
    }

    public List<NaturezaLesao> buscarNaturezaLesao(String parte) {
        return naturezaLesaoFacade.listaFiltrando(parte, "descricao");
    }

    public List<ParteCorpo> buscarPartesCorpo(String parte) {
        return parteCorpoFacade.buscarFiltrandoPorCodigoAndDescricao(parte.trim());
    }

    public List<AgenteAcidenteTrabalho> buscarAgentesAcidenteTrabalho(String parte) {
        return agenteAcidenteTrabalhoFacade.buscarFiltrandoPorCodigoAndDescricao(parte);
    }

    public List<AgenteDoencaProfissional> buscarAgentesDoencaProfissional(String parte) {
        return agenteDoencaProfissionalFacade.buscarFiltrandoPorCodigoAndDescricao(parte);
    }

    public List<SituacaoAcidente> buscarSituacoesAcidente(String parte) {
        return situacaoAcidenteFacade.buscarFiltrandoPorCodigoAndDescricao(parte);
    }

    private void validarEvento() {
        ValidacaoException ve = new ValidacaoException();
        List<ContratoFP> contratos = contratoFPFacade.listaContratosVigentesPorPessoaFisica(selecionado.getColaborador());
        if (contratos == null || contratos.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado Contrato vigente para a pessoa " + selecionado.getColaborador());
        } else {
            ConfiguracaoEmpregadorESocial config = contratoFPFacade.buscarEmpregadorPorVinculoFP(contratos.get(0));
            if (config == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrada Configuração do Empregador do E-Social para o envio.");
            }
        }
        ve.lancarException();
    }


    public void enviarEventoS2210() {
        try {
            validarEvento();
            List<ContratoFP> contratos = contratoFPFacade.listaContratosVigentesPorPessoaFisica(selecionado.getColaborador());
            ConfiguracaoEmpregadorESocial config = contratoFPFacade.buscarEmpregadorPorVinculoFP(contratos.get(0));
            catFacade.enviarEventoS2210(config, selecionado, contratos.get(0));
            FacesUtil.addOperacaoRealizada("Evento enviado com sucesso! ");
            redireciona();

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro: ", e);
            FacesUtil.addError("Erro ao enviar o evento S2210", e.getMessage());
        }
    }


}
