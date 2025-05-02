/*
 * Codigo gerado automaticamente em Mon Mar 05 14:54:30 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.PeriodoAquisitivoFL;
import br.com.webpublico.entidades.SugestaoFerias;
import br.com.webpublico.enums.StatusPeriodoAquisitivo;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SugestaoFeriasFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "consultaProgramacaoFeriasControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaConsultaProgramacaoFerias", pattern = "/consulta/programacao-ferias/novo/", viewId = "/faces/rh/administracaodepagamento/consultaprogramacaoferias/edita.xhtml"),
})
public class ConsultaProgramacaoFeriasControlador implements Serializable {

    @EJB
    private SugestaoFeriasFacade sugestaoFeriasFacade;
    private ConverterAutoComplete converterHierarquia;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ContratoFP contratoFP;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private List<SugestaoFerias> sugestoesFerias;
    private Date inicioDasFerias;
    private Date finalDasFerias;
    private StatusPeriodoAquisitivo situacaoFerias;
    private Boolean aprovadas;
    private Boolean programadas;

    private List<PeriodoAquisitivoFL> periodos;

    public ConsultaProgramacaoFeriasControlador() {
    }

    public Boolean getProgramadas() {
        return programadas;
    }

    public void setProgramadas(Boolean programadas) {
        this.programadas = programadas;
    }

    public List<PeriodoAquisitivoFL> getPeriodos() {
        return periodos;
    }

    public void setPeriodos(List<PeriodoAquisitivoFL> periodos) {
        this.periodos = periodos;
    }

    public Boolean getAprovadas() {
        return aprovadas;
    }

    public void setAprovadas(Boolean aprovadas) {
        this.aprovadas = aprovadas;
    }

    public Date getInicioDasFerias() {
        return inicioDasFerias;
    }

    public String getInicioDasFeriasString() {
        try {
            return new SimpleDateFormat("dd/MM/yyyy").format(inicioDasFerias);
        } catch (Exception e) {
            return "";
        }
    }

    public void setInicioDasFerias(Date inicioDasFerias) {
        this.inicioDasFerias = inicioDasFerias;
    }

    public Date getFinalDasFerias() {
        return finalDasFerias;
    }

    public String getFinalDasFeriasString() {
        try {
            return new SimpleDateFormat("dd/MM/yyyy").format(finalDasFerias);
        } catch (Exception e) {
            return "";
        }
    }

    public void setFinalDasFerias(Date finalDasFerias) {
        this.finalDasFerias = finalDasFerias;
    }

    public StatusPeriodoAquisitivo getSituacaoFerias() {
        return situacaoFerias;
    }

    public void setSituacaoFerias(StatusPeriodoAquisitivo situacaoFerias) {
        this.situacaoFerias = situacaoFerias;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaOrganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquia == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquia;
    }

    public List<ContratoFP> completaContrato(String parte) {
        if (hierarquiaOrganizacional == null) {
            return contratoFPFacade.recuperaContratoMatriculaSql(parte.trim());
        } else {
            return contratoFPFacade.recuperaContratoVigenteMatriculaPorLocalDeTrabalhoRecursiva(parte.trim(), hierarquiaOrganizacional, UtilRH.getDataOperacao());
        }
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public void setaUnidadeOrganizacional(SelectEvent item) {
        hierarquiaOrganizacional = (HierarquiaOrganizacional) item.getObject();

    }

    public void limpar() {
        FacesUtil.redirecionamentoInterno("/consulta/programacao-ferias/novo/");
    }

    public List<SugestaoFerias> getSugestoesFerias() {
        return sugestoesFerias;
    }

    public void setSugestoesFerias(List<SugestaoFerias> sugestoesFerias) {
        this.sugestoesFerias = sugestoesFerias;
    }

    private void validarConsulta() throws ExcecaoNegocioGenerica {
        Boolean validou = Boolean.TRUE;
        if (hierarquiaOrganizacional == null && contratoFP == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "É obrigatório informar o 'Local de Trabalho' ou 'Servidor', por favor informe um desses campos.");
            validou = Boolean.FALSE;
        }

        if (inicioDasFerias == null && programadas != null && programadas.equals(Boolean.TRUE)) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O campo 'A partir de' é obrigatório, por favor informe uma data.");
            validou = Boolean.FALSE;
        }

        if (inicioDasFerias != null && finalDasFerias != null && finalDasFerias.compareTo(inicioDasFerias) < 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O período inicial deve ser anterior ao período final.");
            validou = Boolean.FALSE;
        }

        if (!validou) {
            throw new ExcecaoNegocioGenerica("");
        }
    }

    public void filtrar() {
        try {
            validarConsulta();
            periodos = sugestaoFeriasFacade.recuperarPeriodosAquisitivos(contratoFP, hierarquiaOrganizacional, inicioDasFerias, finalDasFerias, situacaoFerias, aprovadas, programadas);
        } catch (ExcecaoNegocioGenerica e) {
        }
    }

    public String getCaminhoPadrao() {
        return "/aprovacao-ferias/";
    }

    public List<SelectItem> situacoesFerias() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, "Todas"));
        for (StatusPeriodoAquisitivo sp : StatusPeriodoAquisitivo.values()) {
            if (sp.equals(StatusPeriodoAquisitivo.CONCEDIDO) || sp.equals(StatusPeriodoAquisitivo.NAO_CONCEDIDO)) {
                lista.add(new SelectItem(sp, sp.getDescricao()));
            }
        }
        return lista;
    }

    public List<SelectItem> statusFerias() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, "Todos"));
        toReturn.add(new SelectItem(Boolean.TRUE, "Aprovadas"));
        toReturn.add(new SelectItem(Boolean.FALSE, "Não Aprovadas"));
        return toReturn;
    }

    public List<SelectItem> programadasNaoProgramadas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, "Todas"));
        toReturn.add(new SelectItem(Boolean.TRUE, "Programadas"));
        toReturn.add(new SelectItem(Boolean.FALSE, "Não Programadas"));
        return toReturn;
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        if (hierarquiaOrganizacional == null) {
            return contratoFPFacade.recuperaContratoMatricula(parte.trim());
        } else {
            return contratoFPFacade.recuperaContratoVigenteMatriculaPorLocalDeTrabalhoRecursiva(parte.trim(), hierarquiaOrganizacional, UtilRH.getDataOperacao());
        }
    }

    @URLAction(mappingId = "novaConsultaProgramacaoFerias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaProgramacao() {
        contratoFP = null;
        hierarquiaOrganizacional = null;
        inicioDasFerias = new Date();
    }

    public String getTextoInformacao() {
        return "<ul>" +
            "<li>Ao informar somente o local de trabalho, sistema irá recuperar todos os contratos vigentes naquela unidade.</li>" +
            "</ul>";
    }
}
