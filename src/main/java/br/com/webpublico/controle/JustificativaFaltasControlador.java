package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 28/08/13
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "justificativaFaltasControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoJustificativaFaltas", pattern = "/justificativa-faltas/novo/", viewId = "/faces/rh/administracaodepagamento/justificativafaltas/edita.xhtml"),
    @URLMapping(id = "editarJustificativaFaltas", pattern = "/justificativa-faltas/editar/#{justificativaFaltasControlador.id}/", viewId = "/faces/rh/administracaodepagamento/justificativafaltas/edita.xhtml"),
    @URLMapping(id = "listarJustificativaFaltas", pattern = "/justificativa-faltas/listar/", viewId = "/faces/rh/administracaodepagamento/justificativafaltas/lista.xhtml"),
    @URLMapping(id = "verJustificativaFaltas", pattern = "/justificativa-faltas/ver/#{justificativaFaltasControlador.id}/", viewId = "/faces/rh/administracaodepagamento/justificativafaltas/visualizar.xhtml")
})
public class JustificativaFaltasControlador extends PrettyControlador<JustificativaFaltas> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(JustificativaFaltasControlador.class);

    @EJB
    private JustificativaFaltasFacade justificativaFaltasFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private FaltasFacade faltasFacade;
    private ContratoFP contratoFP;
    private ConverterAutoComplete converterContratoFP;
    @EJB
    private CIDFacade cidFacade;
    private ConverterAutoComplete converterCid;
    @EJB
    private MedicoFacade medicoFacade;
    private ConverterAutoComplete converterMedico;
    private ConverterGenerico converterFalta;

    public JustificativaFaltasControlador() {
        super(JustificativaFaltas.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/justificativa-faltas/";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getUrlKeyValue() {
        return (selecionado).getId();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AbstractFacade getFacede() {
        return justificativaFaltasFacade;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "novoJustificativaFaltas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();

    }

    @URLAction(mappingId = "verJustificativaFaltas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarJustificativaFaltas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        novo();
        super.editar();
        JustificativaFaltas jus = selecionado;
        contratoFP = jus.getFaltas().getContratoFP();


    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return contratoFPFacade.recuperaContratosVigentesComFaltasInjustificadas(parte.trim());
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public Converter getConverterFalta() {
        if (converterFalta == null) {
            converterFalta = new ConverterGenerico(Faltas.class, faltasFacade);
        }
        return converterFalta;
    }

    public List<CID> completaCids(String parte) {
        return cidFacade.listaFiltrando(parte.trim(), "descricao", "codigoDaCid");
    }

    public List<SelectItem> getFaltas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        if (operacao.equals(Operacoes.EDITAR)) {
            toReturn.add(new SelectItem(selecionado.getFaltas(), selecionado.getFaltas().getDescricao()));
            return toReturn;
        }
        toReturn.add(new SelectItem(null, ""));
        if (contratoFP != null) {
            for (Faltas object : faltasFacade.buscarFaltasComDiaAJustificar(contratoFP)) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public Converter getConverterCid() {
        if (converterCid == null) {
            converterCid = new ConverterAutoComplete(CID.class, cidFacade);
        }
        return converterCid;
    }

    public List<Medico> completaMedico(String parte) {
        return medicoFacade.listaFiltrandoMedico(parte);
    }

    public Converter getConverterMedico() {
        if (converterMedico == null) {
            converterMedico = new ConverterAutoComplete(Medico.class, medicoFacade);
        }
        return converterMedico;
    }

    public Converter getConverterDataCalculo() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                try {
                    if (value != null && !value.equals("")) {

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date dataMesAnoFinal = new Date();
                        value = "01/" + value;
                        dataMesAnoFinal = sdf.parse(value);
                        Calendar c = GregorianCalendar.getInstance();
                        c.setTime(dataMesAnoFinal);
                        Integer ultimoDiaMes = c.getActualMinimum(Calendar.DAY_OF_MONTH);
                        c.set(Calendar.DAY_OF_MONTH, ultimoDiaMes);
                        return c.getTime();
                    }
                } catch (Exception ex) {
                    logger.error("Erro: ", ex);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro do conversão de data, favor insira um valor aceitável"));
                    return null;
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String dataMesAnoFinal = sdf.format(value);
                StringBuilder builder = new StringBuilder(dataMesAnoFinal);
                builder.replace(0, 3, "");
                return builder.toString();
            }
        };
    }


    @Override
    public boolean validaRegrasEspecificas() {
        JustificativaFaltas jus = selecionado;
        if (jus.getFinalVigencia() == null || jus.getInicioVigencia() == null) {
            FacesUtil.addWarn("Atenção", "Preencha as data de inicio e término de vigência corretamente.");
            return false;
        }
        if (jus.getFaltas() == null) {
            FacesUtil.addWarn("Atenção", "a data de término é menor que a data de início.");
            return false;
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(jus.getFaltas().getInicio(), jus.getInicioVigencia())) {
            FacesUtil.addWarn("Atenção", "a data de início é menor que a data de início da data da falta.");
            return false;
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(jus.getFinalVigencia(), jus.getFaltas().getFim())) {
            FacesUtil.addWarn("Atenção", "a data de término é maior que a data de término da data da falta.");
            return false;
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(jus.getInicioVigencia(), jus.getFinalVigencia())) {
            FacesUtil.addWarn("Atenção", "a data de término é menor que a data de início.");
            return false;
        }

        return true;
    }

    public void aplicaData() {
        JustificativaFaltas jus = selecionado;
        if (jus.getFaltas() != null) {
            jus.setInicioVigencia(jus.getFaltas().getInicio());
            jus.setFinalVigencia(jus.getFaltas().getFim());
        }
    }

    private void validarQuantidadeDiasAfastamento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getFaltas() != null) {
            int diasRestantesParaJustificar = selecionado.getFaltas().getDias() - Integer.parseInt(faltasFacade.buscarQuantidadeDiasJustificados(selecionado.getFaltas(), selecionado).toString());
            if (selecionado.getDias() > diasRestantesParaJustificar) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade de dias para serem justificados são de no máximo " + diasRestantesParaJustificar + " dias.");
            }
            List<JustificativaFaltas> justificativas = justificativaFaltasFacade.recuperarJustificativaDeFaltasPorFalta(selecionado.getFaltas(), selecionado);
            if (justificativas != null) {
                for (JustificativaFaltas justificativa : justificativas) {
                    if ((selecionado.getInicioVigencia().compareTo(justificativa.getInicioVigencia()) <= 0 && selecionado.getFinalVigencia().compareTo(justificativa.getInicioVigencia()) >= 0)
                        || (selecionado.getInicioVigencia().compareTo(justificativa.getInicioVigencia()) >= 0 && selecionado.getInicioVigencia().compareTo(justificativa.getFinalVigencia()) <= 0)) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Existe período conflitante de Justificativa de Faltas, justificativa de " + DataUtil.getDataFormatada(justificativa.getInicioVigencia()) + " a " + DataUtil.getDataFormatada(justificativa.getFinalVigencia()));
                    }
                }
            }
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarQuantidadeDiasAfastamento();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }
}
