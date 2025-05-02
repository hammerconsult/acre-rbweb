package br.com.webpublico.controle.rh.saudeservidor;


import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.saudeservidor.AgenteNocivo;
import br.com.webpublico.entidades.rh.saudeservidor.RegistroAmbiental;
import br.com.webpublico.entidades.rh.saudeservidor.RiscoOcupacional;
import br.com.webpublico.enums.rh.esocial.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.UFFacade;
import br.com.webpublico.negocios.rh.saudeservidor.RiscoOcupacionalFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

@ManagedBean(name = "riscoOcupacionalControlador")
@ViewScoped
@URLMappings(
    mappings = {
        @URLMapping(id = "listarRiscoOcupacional", pattern = "/risco-ocupacional/listar/", viewId = "/faces/rh/saudeservidor/risco-ocupacional/lista.xhtml"),
        @URLMapping(id = "novoRiscoOcupacional", pattern = "/risco-ocupacional/novo/", viewId = "/faces/rh/saudeservidor/risco-ocupacional/edita.xhtml"),
        @URLMapping(id = "editarRiscoOcupacional", pattern = "/risco-ocupacional/editar/#{riscoOcupacionalControlador.id}/", viewId = "/faces/rh/saudeservidor/risco-ocupacional/edita.xhtml"),
        @URLMapping(id = "verRiscoOcupacional", pattern = "/risco-ocupacional/ver/#{riscoOcupacionalControlador.id}/", viewId = "/faces/rh/saudeservidor/risco-ocupacional/visualizar.xhtml")
    }
)
public class RiscoOcupacionalControlador extends PrettyControlador<RiscoOcupacional> implements CRUD {

    @EJB
    private RiscoOcupacionalFacade riscoOcupacionalFacade;

    private AgenteNocivo agenteNocivo;

    private RegistroAmbiental registroAmbiental;

    @EJB
    private UFFacade ufFacade;

    public RiscoOcupacionalControlador() {
        super(RiscoOcupacional.class);
        agenteNocivo = new AgenteNocivo();
        registroAmbiental = new RegistroAmbiental();
    }

    public AgenteNocivo getAgenteNocivo() {
        return agenteNocivo;
    }

    public void setAgenteNocivo(AgenteNocivo agenteNocivo) {
        this.agenteNocivo = agenteNocivo;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/risco-ocupacional/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public RegistroAmbiental getRegistroAmbiental() {
        return registroAmbiental;
    }

    public void setRegistroAmbiental(RegistroAmbiental registroAmbiental) {
        this.registroAmbiental = registroAmbiental;
    }

    @Override
    public AbstractFacade getFacede() {
        return riscoOcupacionalFacade;
    }

    @Override
    @URLAction(mappingId = "verRiscoOcupacional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarRiscoOcupacional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "novoRiscoOcupacional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    public List<SelectItem> getTipoEstabelecimento() {
        return Util.getListSelectItem(LocalRiscoOcupacional.values(), false);
    }

    public List<SelectItem> getTipoAvaliacaoAgenteNocivo() {
        return Util.getListSelectItem(TipoAvaliacaoAgenteNocivo.values(), false);
    }

    public List<SelectItem> getCodigoAgenteNocivo() {
        return Util.getListSelectItem(CodigoAgenteNocivo.values(), false);
    }

    public List<SelectItem> getTipoUnidadeMedida() {
        return Util.getListSelectItem(TipoUnidadeMedida.values(), false);
    }

    public List<SelectItem> getUtilizaEPC() {
        return Util.getListSelectItem(UtilizaEPC.values(), false);
    }

    public List<SelectItem> getUtilizaEPI() {
        return Util.getListSelectItem(UtilizaEPI.values(), false);
    }

    public List<SelectItem> getTipoResponsavelRegistroAmbiental() {
        return Util.getListSelectItem(TipoResponsavelAmbiental.values(), false);
    }

    public List<SelectItem> getUF() {
        return Util.getListSelectItem(ufFacade.lista(), false);
    }


    public void adicionarAgenteNocivo() {
        try {
            validarAgenteNocivo();
            agenteNocivo.setRiscoOcupacional(selecionado);
            selecionado.getItemAgenteNovico().add(agenteNocivo);
            agenteNocivo = new AgenteNocivo();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void adicionarResponsavelAmbiental() {
        try {
            validarRegistroAmbiental();
            registroAmbiental.setRiscoOcupacional(selecionado);
            selecionado.getItemRegistroAmbiental().add(registroAmbiental);
            registroAmbiental = new RegistroAmbiental();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    private void validarAgenteNocivo() {
        ValidacaoException ve = new ValidacaoException();
        if (agenteNocivo.getCodigoAgenteNocivo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Código do Agente Nocivo.");
        }
        if (Strings.isNullOrEmpty(agenteNocivo.getDescricaoAgenteNocivo())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Descrição do Agente Nocivo.");
        }
        if (agenteNocivo.getTipoAvaliacaoAgenteNocivo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo de avaliação do agente nocivo.");
        }
        if (agenteNocivo.getIntensidadeConcentracao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Intensidade, concentração ou dose da exposição do trabalhador.");
        }
        if (agenteNocivo.getLimiteTolerancia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Limite de tolerância calculado.");
        }
        if (agenteNocivo.getTipoUnidadeMedida() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Dose ou unidade de medida.");
        }
        if (Strings.isNullOrEmpty(agenteNocivo.getTecnicaMedicao())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Técnica utilizada para medição da intensidade ou concentração.");
        }
        if (agenteNocivo.getUtilizaEPC() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe se Utiliza EPC.");
        }
        if (agenteNocivo.getUtilizaEPI() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe se Utiliza EPI.");
        }
        ve.lancarException();
    }

    private void validarRegistroAmbiental() {
        ValidacaoException ve = new ValidacaoException();
        if (registroAmbiental.getResponsavel() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o responsável.");
        }
        if (registroAmbiental.getTipoResponsavelAmbiental() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Órgão de classe.");
        }
        if (Strings.isNullOrEmpty(registroAmbiental.getDescricaoClasse())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Descrição (sigla) do órgão de classe.");
        }
        if (Strings.isNullOrEmpty(registroAmbiental.getNumeroInscricaoClasse())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Número de inscrição no órgão de classe.");
        }
        if (registroAmbiental.getUf() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a UF.");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarAgenteNocivoAndResponsaveis();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void validarAgenteNocivoAndResponsaveis() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getItemAgenteNovico() == null || selecionado.getItemAgenteNovico().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi informado nenhum Agente(s) nocivo(s) ao(s) qual(is) o trabalhador está exposto.");
        }
        if (selecionado.getItemRegistroAmbiental() == null || selecionado.getItemAgenteNovico().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi informado nenhuma Informações relativas ao responsável pelos registros ambientais;");
        }
        ve.lancarException();
    }

    public void removerAgenteNovico(AgenteNocivo agente) {
        selecionado.getItemAgenteNovico().remove(agente);
    }

    public void editarAgenteNovico(AgenteNocivo agente) {
        agenteNocivo = agente;
        selecionado.getItemAgenteNovico().remove(agente);
    }

    public void removerRegistroAmbiental(RegistroAmbiental registro) {
        selecionado.getItemRegistroAmbiental().remove(registro);
    }

    public void editarRegistroAmbiental(RegistroAmbiental registro) {
        registroAmbiental = registro;
        selecionado.getItemRegistroAmbiental().remove(registroAmbiental);
    }
}
