package br.com.webpublico.controle;

import br.com.webpublico.entidades.AcidenteTrabalho;
import br.com.webpublico.entidades.CID;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.Medico;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AcidenteTrabalhoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
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
 * Created by israeleriston on 11/11/15.
 */
@ManagedBean(name = "acidenteTrabalhoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "listarAcidenteTrabalho",
                pattern = "/acidente-trabalho/listar/",
                viewId = "/faces/rh/administracaodepagamento/acidentetrabalho/lista.xhtml"),
        @URLMapping(id = "novoAcidenteTrabalho",
                pattern = "/acidente-trabalho/novo/",
                viewId = "/faces/rh/administracaodepagamento/acidentetrabalho/edita.xhtml"),
        @URLMapping(id = "editaAcidenteTrabalho",
                pattern = "/acidente-trabalho/editar/#{acidenteTrabalhoControlador.id}/",
                viewId = "/faces/rh/administracaodepagamento/acidentetrabalho/edita.xhtml"),
        @URLMapping(id = "verAcidenteTrabalho",
                pattern = "/acidente-trabalho/ver/#{acidenteTrabalhoControlador.id}/",
                viewId = "/faces/rh/administracaodepagamento/acidentetrabalho/visualizar.xhtml")
})
public class AcidenteTrabalhoControlador extends PrettyControlador<AcidenteTrabalho> implements Serializable, CRUD {
    @EJB
    private AcidenteTrabalhoFacade acidenteTrabalhoFacade;
    private ConverterAutoComplete converterAutoCompleteContrato;
    private ConverterAutoComplete converterAutoCompleteCid;
    private ConverterAutoComplete converterAutoCompleteMedico;
    private Boolean isMostrarPanelPessoal;
    private Boolean isMostrarAfastamento;


    @Override
    public String getCaminhoPadrao() {
        return "/acidente-trabalho/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return acidenteTrabalhoFacade;
    }

    public AcidenteTrabalhoControlador() {
        super(AcidenteTrabalho.class);
    }

    @Override
    @URLAction(mappingId = "novoAcidenteTrabalho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        isMostrarPanelPessoal = false;
        isMostrarAfastamento = false;
        selecionado.setDuracaoTratamento(null);


    }

    @Override
    @URLAction(mappingId = "editaAcidenteTrabalho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        isMostrarPanelPessoal = true;
        if (selecionado.getIsAfastamentoTrabalho().equals(Boolean.TRUE)) {
            isMostrarAfastamento = true;
        } else {
            isMostrarAfastamento = false;
        }
    }

    @Override
    @URLAction(mappingId = "verAcidenteTrabalho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        if (isValido()) {
            super.salvar();
        }


    }

    public List<ContratoFP> completarContratoFP(String filtro) {
        return acidenteTrabalhoFacade.getContratoFPFacade().recuperaContrato(filtro.trim());
    }

    public List<SelectItem> falecimentos() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(false, "Não"));
        retorno.add(new SelectItem(true, "Sim"));
        return retorno;
    }


    public List<SelectItem> registroPoliciais() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(false, "Não"));
        retorno.add(new SelectItem(true, "Sim"));
        return retorno;
    }

    public List<SelectItem> internacoes() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(false, "Não"));
        retorno.add(new SelectItem(true, "Sim"));
        return retorno;
    }


    public List<SelectItem> afastamentos() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(false, "Não"));
        retorno.add(new SelectItem(true, "Sim"));
        return retorno;
    }


    public List<CID> completarCid(String filter) {
        return acidenteTrabalhoFacade.getCidFacade().buscarTodosCid(filter.trim());
    }

    public List<Medico> completarMedico(String filter) {
        return acidenteTrabalhoFacade.getMedicoFacade().listaFiltrandoMedico(filter.trim());
    }

    public void mostrarPanelPessoal() {
        isMostrarPanelPessoal = true;
    }

    public void mostrarAfastamento() {
        if (selecionado.getIsAfastamentoTrabalho().equals(Boolean.TRUE)) {
            isMostrarAfastamento = true;
        } else {
            isMostrarAfastamento = false;
        }
    }


    public ConverterAutoComplete getConverterAutoCompleteContrato() {
        if (converterAutoCompleteContrato == null) {
            converterAutoCompleteContrato = new ConverterAutoComplete(AcidenteTrabalho.class, acidenteTrabalhoFacade.getContratoFPFacade());
        }
        return converterAutoCompleteContrato;
    }

    public void setConverterAutoCompleteContrato(ConverterAutoComplete converterAutoCompleteContrato) {
        this.converterAutoCompleteContrato = converterAutoCompleteContrato;
    }

    public ConverterAutoComplete getConverterAutoCompleteCid() {
        if (converterAutoCompleteCid == null) {
            converterAutoCompleteCid = new ConverterAutoComplete(CID.class, acidenteTrabalhoFacade.getCidFacade());
        }
        return converterAutoCompleteCid;
    }

    public void setConverterAutoCompleteCid(ConverterAutoComplete converterAutoCompleteCid) {
        this.converterAutoCompleteCid = converterAutoCompleteCid;
    }

    public ConverterAutoComplete getConverterAutoCompleteMedico() {
        if (converterAutoCompleteMedico == null) {
            converterAutoCompleteMedico = new ConverterAutoComplete(Medico.class, acidenteTrabalhoFacade.getMedicoFacade());
        }
        return converterAutoCompleteMedico;
    }

    public void setConverterAutoCompleteMedico(ConverterAutoComplete converterAutoCompl_eteMedico) {
        this.converterAutoCompleteMedico = converterAutoCompleteMedico;
    }

    public Boolean getIsMostrarPanelPessoal() {
        return isMostrarPanelPessoal;
    }

    public void setIsMostrarPanelPessoal(Boolean isMostrarPanelPessoal) {
        this.isMostrarPanelPessoal = isMostrarPanelPessoal;
    }

    public Boolean getIsMostrarAfastamento() {
        return isMostrarAfastamento;
    }

    public void setIsMostrarAfastamento(Boolean isMostrarAfastamento) {
        this.isMostrarAfastamento = isMostrarAfastamento;
    }

    private Boolean isValido() {
        Boolean isValido = true;
        if (selecionado.getContratoFP() == null || selecionado.getContratoFP().toString().isEmpty()) {
            isValido = false;
            FacesUtil.addCampoObrigatorio("O campo Servidor deve ser informado! ");
        }
        if (selecionado.getUltimoDiaTrabalhado() == null) {
            isValido = false;
            FacesUtil.addCampoObrigatorio("O campo Ultimo Dia Trabalhado deve ser informado! ");
        }
        if (selecionado.getLocal().trim().isEmpty() || selecionado.getLocal() == null) {
            isValido = false;
            FacesUtil.addCampoObrigatorio("O campo Local do Acidente deve ser informado! ");
        }
        if (selecionado.getOcorridoEm() == null) {
            isValido = false;
            FacesUtil.addCampoObrigatorio("O campo Data da Ocorrência deve ser informado! ");
        }
        if (selecionado.getAtendidoEm() == null) {
            isValido = false;
            FacesUtil.addCampoObrigatorio("O campo Data do Atendimento deve ser informado! ");
        }
        if (selecionado.getParteCorpoAtingido().trim().isEmpty() || selecionado.getParteCorpoAtingido() == null) {
            isValido = false;
            FacesUtil.addCampoObrigatorio("O campo Parte do Corpo Atingida deve ser informado! ");
        }
        if (selecionado.getAgenteCausador().trim().isEmpty() || selecionado.getAgenteCausador() == null) {
            isValido = false;
            FacesUtil.addCampoObrigatorio("O campo Agente Causador deve ser informado! ");
        }
        if (selecionado.getUnidadeSaude().trim().isEmpty() || selecionado.getUnidadeSaude() == null) {
            isValido = false;
            FacesUtil.addCampoObrigatorio("O campo Unidade de Saúde deve ser informado!");
        }
        if (selecionado.getPrimeiraTestemunha().trim().isEmpty() || selecionado.getPrimeiraTestemunha() == null) {
            isValido = false;
            FacesUtil.addCampoObrigatorio("O campo Primeira Testemunha deve ser informado! ");
        }
        if (selecionado.getSegundaTestemunha().trim().isEmpty() || selecionado.getSegundaTestemunha() == null) {
            isValido = false;
            FacesUtil.addCampoObrigatorio("o campo Segunda Testemunha deve ser informado! ");
        }
        if (selecionado.getHorario() == null) {
            isValido = false;
            FacesUtil.addCampoObrigatorio("O campo Horário do Atendimento deve ser informado!");
        }
        if (selecionado.getIsAfastamentoTrabalho().equals(true) && selecionado.getDuracaoTratamento() == null) {
            isValido = false;
            FacesUtil.addCampoObrigatorio("O campo Duração Provável de Tratamento deve ser informado! ");
        }
        if (selecionado.getAcidente().getCid() == null) {
            isValido = false;
            FacesUtil.addCampoObrigatorio("O campo CID deve ser informado! ");
        }
        if (selecionado.getAcidente().getMedico() == null) {
            isValido = false;
            FacesUtil.addCampoObrigatorio("O campo Médico deve ser informado! ");
        }
        if (selecionado.getAcidente().getNaturezaLesao().trim().isEmpty() || selecionado.getAcidente().getNaturezaLesao() == null) {
            isValido = false;
            FacesUtil.addCampoObrigatorio("O campo Natureza da Lesão deve ser informado! ");
        }
        if (selecionado.getAcidente().getSituacaoGeradora().trim().isEmpty() || selecionado.getAcidente().getSituacaoGeradora() == null) {
            isValido = false;
            FacesUtil.addCampoObrigatorio("O campo Situação Geradora deve ser informado!");
        }
        if (selecionado.getAcidente().getDiagnostico().trim().isEmpty() || selecionado.getAcidente().getDiagnostico() == null) {
            isValido = false;
            FacesUtil.addCampoObrigatorio("O campo Diagnóstico deve ser informado! ");
        }
        if (selecionado.getAcidente().getObservacao().trim().isEmpty() || selecionado.getAcidente().getObservacao() == null) {
            isValido = false;
            FacesUtil.addCampoObrigatorio("O campo Observação deve ser informado! ");
        }
        return isValido;
    }
}

