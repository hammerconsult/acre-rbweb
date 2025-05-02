/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.Faltas;
import br.com.webpublico.entidadesauxiliares.rh.VwFalta;
import br.com.webpublico.entidadesauxiliares.rh.relatorio.RelatorioConferenciaFaltas;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.FaltasFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Claudio
 */
@ManagedBean(name = "conferirFaltasServidorControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "consultarFaltasServidor", pattern = "/faltas-servidor/consultar/", viewId = "/faces/rh/administracaodepagamento/faltas/conferirfaltasservidor.xhtml"),
})
public class ConferirFaltasServidorControlador extends PrettyControlador<Faltas> implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ConferirFaltasServidorControlador.class);
    @EJB
    private FaltasFacade faltasFacade;
    private Date dataInicial;
    private Date dataFinal;
    private ContratoFP contratoFP;
    private ConverterAutoComplete converterContratoFP;
    private Boolean mostraPainelFaltas;
    private List<VwFalta> faltasPorContrato;
    @EJB
    private ContratoFPFacade contratoFPFacade;

    public ConferirFaltasServidorControlador() {
        super(Faltas.class);
        limpaCampos();
    }

    @Override
    public AbstractFacade getFacede() {
        return faltasFacade;
    }

    @URLAction(mappingId = "consultarFaltasServidor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        mostraPainelFaltas = false;
        contratoFP = null;
        dataFinal = null;
        dataInicial = null;
        faltasPorContrato = Lists.newLinkedList();
    }

    public List<VwFalta> getFaltasPorContrato() {
        return faltasPorContrato;
    }

    public void setFaltasPorContrato(List<VwFalta> faltasPorContrato) {
        this.faltasPorContrato = faltasPorContrato;
    }

    public Boolean getMostraPainelFaltas() {
        return mostraPainelFaltas;
    }

    public void setMostraPainelFaltas(Boolean mostraPainelFaltas) {
        this.mostraPainelFaltas = mostraPainelFaltas;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return contratoFPFacade.recuperaContratoVigenteMatricula(parte.trim());
    }

    public void validarCampos() {
        ValidacaoException val = new ValidacaoException();

        if (contratoFP == null || contratoFP.getId() == null) {
            val.adicionarMensagemDeCampoObrigatorio("Selecione o Servidor.");
        }

        if (dataInicial == null) {
            val.adicionarMensagemDeCampoObrigatorio("A data inicial deve ser informada.");
        }

        if (dataFinal == null) {
            val.adicionarMensagemDeCampoObrigatorio("A data final deve ser informada.");
        }

        if (dataInicial != null && dataFinal != null && dataInicial.after(dataFinal)) {
            val.adicionarMensagemDeCampoObrigatorio("A data inicial não pode ser posterior a data final");
        }
        val.lancarException();
    }

    public void consultar() {
        try {
            validarCampos();
            faltasPorContrato = faltasFacade.buscarFaltasPorContratoAndPeriodo(contratoFP, DataUtil.dateToLocalDate(dataInicial), DataUtil.dateToLocalDate(dataFinal));
        } catch (ValidacaoException val) {
            FacesUtil.printAllFacesMessages(val.getAllMensagens());
        } catch (Exception e) {
            logger.error("Erro ao consultar faltas ", e);
        }
    }

    public Converter getConverterData() {
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

    public void imprimir() {
        try {
            String arquivoJasper = "RelacaoFaltas.jasper";
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(montarRelatorio());
            HashMap parameters = new HashMap();
            AbstractReport report = AbstractReport.getAbstractReport();
            parameters.put("USUARIO", faltasFacade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica().getNome());
            parameters.put("MODULO", "Recursos Humanos");
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            parameters.put("NOMERELATORIO", "RELATÓRIO DE RELAÇÃO DE FALTAS");
            parameters.put("IMAGEM", report.getCaminhoImagem());
            parameters.put("SUBREPORT_DIR", report.getCaminho());

            report.setGeraNoDialog(true);
            report.gerarRelatorioComDadosEmCollection(arquivoJasper, parameters, ds);
        } catch (Exception ex) {
            logger.error("Erro ao emitir relatório: " + ex.getMessage());
        }
    }

    private List<RelatorioConferenciaFaltas> montarRelatorio() {
        List<RelatorioConferenciaFaltas> faltasRelatorio = Lists.newArrayList();
        RelatorioConferenciaFaltas faltaRelatorio = new RelatorioConferenciaFaltas();
        faltaRelatorio.setNome(contratoFP.getMatriculaFP().getPessoa().getNome());
        faltaRelatorio.setMatricula(contratoFP.getMatriculaFP().getMatricula() + "/" + contratoFP.getNumero());
        faltaRelatorio.setOrgao(contratoFP.getUnidadeOrganizacional().toString());
        int totalFaltas = 0;
        for (VwFalta listaDeFalta : faltasPorContrato) {
            Faltas obj = new Faltas();
            obj.setInicio(listaDeFalta.getInicio());
            obj.setFim(listaDeFalta.getFim());
            obj.setTipoDaFalta(listaDeFalta.getTipoFalta().getDescricao());
            obj.setDias(listaDeFalta.getTotalFaltas());
            faltaRelatorio.getFaltas().add(obj);
            totalFaltas = totalFaltas + listaDeFalta.getTotalFaltas();
        }
        faltaRelatorio.setTotalFaltas(totalFaltas);
        faltasRelatorio.add(faltaRelatorio);
        return faltasRelatorio;
    }
}
