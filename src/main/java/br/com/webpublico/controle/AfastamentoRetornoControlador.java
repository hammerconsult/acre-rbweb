/*
 * Codigo gerado automaticamente em Mon Sep 05 15:28:59 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ClasseAfastamento;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "afastamentoRetornoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoRetornoAfastamento", pattern = "/retorno-afastamento/novo/", viewId = "/faces/rh/administracaodepagamento/afastamentoretorno/edita.xhtml"),
        @URLMapping(id = "editarRetornoAfastamento", pattern = "/retorno-afastamento/editar/#{afastamentoRetornoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/afastamentoretorno/edita.xhtml"),
        @URLMapping(id = "listarRetornoAfastamento", pattern = "/retorno-afastamento/listar/", viewId = "/faces/rh/administracaodepagamento/afastamentoretorno/lista.xhtml"),
        @URLMapping(id = "verRetornoAfastamento", pattern = "/retorno-afastamento/ver/#{afastamentoRetornoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/afastamentoretorno/visualizar.xhtml")
})
public class AfastamentoRetornoControlador extends PrettyControlador<Afastamento> implements Serializable, CRUD {

    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterContratoFP;
    @EJB
    private TipoAfastamentoFacade tipoAfastamentoFacade;
    private ConverterAutoComplete converterTipoAfastamento;
    private ContratoFP contratoFP;
    @EJB
    private CIDFacade CIDFacade;
    private ConverterAutoComplete converterCid;
    private Afastamento afastamentoVigente;
    @EJB
    private MedicoFacade medicoFacade;
    private ConverterAutoComplete converterMedico;
    @EJB
    private ConcessaoFeriasLicencaFacade concessaoFeriasLicencaFacade;
    @EJB
    private CedenciaContratoFPFacade cedenciaContratoFPFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;

    public AfastamentoRetornoControlador() {
        super(Afastamento.class);
    }

    public AfastamentoFacade getFacade() {
        return afastamentoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return afastamentoFacade;
    }

    public List<SelectItem> getContratoFP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (ContratoFP object : contratoFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public List<SelectItem> getTipoAfastamento() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoAfastamento object : tipoAfastamentoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public Converter getConverterTipoAfastamento() {
        if (converterTipoAfastamento == null) {
            converterTipoAfastamento = new ConverterAutoComplete(TipoAfastamento.class, tipoAfastamentoFacade);
        }
        return converterTipoAfastamento;
    }

    public Converter getConverterCid() {
        if (converterCid == null) {
            converterCid = new ConverterAutoComplete(CID.class, CIDFacade);
        }
        return converterCid;
    }

    public Converter getConverterMedico() {
        if (converterMedico == null) {
            converterMedico = new ConverterAutoComplete(Medico.class, medicoFacade);
        }

        return converterMedico;
    }

    public AfastamentoFacade getAfastamentoFacade() {
        return afastamentoFacade;
    }

    public void setAfastamentoFacade(AfastamentoFacade afastamentoFacade) {
        this.afastamentoFacade = afastamentoFacade;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public void setContratoFPFacade(ContratoFPFacade contratoFPFacade) {
        this.contratoFPFacade = contratoFPFacade;
    }

    public TipoAfastamentoFacade getTipoAfastamentoFacade() {
        return tipoAfastamentoFacade;
    }

    public void setTipoAfastamentoFacade(TipoAfastamentoFacade tipoAfastamentoFacade) {
        this.tipoAfastamentoFacade = tipoAfastamentoFacade;
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return afastamentoFacade.listaFiltrandoContratoFPVigente(parte.trim());
    }

    public List<CID> completaCids(String parte) {
        return CIDFacade.listaFiltrando(parte.trim(), "descricao", "codigoDaCid");
    }

    public List<TipoAfastamento> completaTipoAfastamento(String parte) {
        return tipoAfastamentoFacade.buscarTiposAfastamentoPorDescricaoCodigoAtivo(parte.trim());
    }

    public List<Medico> completaMedico(String parte) {
        return medicoFacade.listaFiltrandoMedico(parte);
    }

    public void preencherCarencia(SelectEvent a) {
        if (a.getObject() != null) {
            TipoAfastamento t = (TipoAfastamento) a.getObject();

            if (t.getClasseAfastamento().equals(ClasseAfastamento.AFASTAMENTO)) {
                selecionado.setDiasMaximoPermitido(t.getDiasMaximoPermitido());

                if (t.getCarencia() != null) {
                    ((Afastamento) selecionado).setCarencia(t.getCarencia().intValue());
                } else {
                    ((Afastamento) selecionado).setCarencia(null);
                }
            } else {
                ((Afastamento) selecionado).setCarencia(null);
            }
        }
    }

    @URLAction(mappingId = "novoRetornoAfastamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataCadastro(new Date());
    }

    @URLAction(mappingId = "verRetornoAfastamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarRetornoAfastamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionado.setRetornoInformado(true);
    }

    @Override
    public void salvar() {
        selecionado.setRetornoInformado(true);
        if (Util.validaCampos(selecionado)) {
            try {
                afastamentoFacade.salvarAfastamentoRetorno(selecionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com sucesso!", ""));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Ocorreu um erro: ", e.getMessage()));
            }
            redireciona();
        }

    }

    private boolean temAfastamentoVigenteNoMesmoPeriodo(Afastamento afastamentoVigente, Afastamento af) {
        if (afastamentoVigente == null) {
            return false;
        }

        if (estaEntreDatas(af.getInicio(), afastamentoVigente.getInicio(), afastamentoVigente.getTermino())) {
            return true;
        }

        if (estaEntreDatas(af.getTermino(), afastamentoVigente.getInicio(), afastamentoVigente.getTermino())) {
            return true;
        }
        return false;
    }

    private boolean estaEntreDatas(Date dataBase, Date inicio, Date termino) {
        return dataBase.after(inicio) && dataBase.before(termino);
    }

    public void sugeriDataFinal() {
        if (((Afastamento) selecionado).getInicio() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(((Afastamento) selecionado).getInicio());
            if (selecionado.getDiasMaximoPermitido() != null && selecionado.getDiasMaximoPermitido() > 0) {
                calendar.add(Calendar.DAY_OF_MONTH, selecionado.getDiasMaximoPermitido() - 1);
                ((Afastamento) selecionado).setTermino(calendar.getTime());
            }
//            validaData();
        }
    }

    public void validaData() {
        if (((Afastamento) selecionado).getInicio() != null && ((Afastamento) selecionado).getTermino() != null) {
            if (((Afastamento) selecionado).getInicio().after(((Afastamento) selecionado).getTermino())) {
                FacesUtil.addWarn("Atenção!", "A Data de Término não pode ser anterior a Data de Início.");
                sugeriDataFinal();
            } else {
                if (quantidadeDiasEntreDatas().compareTo(selecionado.getDiasMaximoPermitido() != null ? selecionado.getDiasMaximoPermitido() : 0) > 0) {
                    FacesUtil.addWarn("Atenção!", "O intervalo entre a data de início e término ultrapassa o número máximo de dias permitido.");
                    sugeriDataFinal();
                }
            }
        }
    }

    private Integer quantidadeDiasEntreDatas() {
        DateTime inicio = new DateTime(((Afastamento) selecionado).getInicio());
        DateTime fim = new DateTime(((Afastamento) selecionado).getTermino());

        return Days.daysBetween(inicio, fim).getDays();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/retorno-afastamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
