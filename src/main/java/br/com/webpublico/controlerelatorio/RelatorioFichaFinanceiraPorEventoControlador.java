package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.FichaFinanceiraFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidadesauxiliares.ObjetoPesquisa;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.EventoFPFacade;
import br.com.webpublico.negocios.FichaFinanceiraFPFacade;
import br.com.webpublico.negocios.VinculoFPFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Paschualleto
 * Date: 05/01/15
 * Time: 11:15
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "Relatorio-Ficha-Financeirao-Por-EventoFP", pattern = "/relatorio/ficha-financeira-por-enventofp/", viewId = "/faces/rh/relatorios/relatoriofichafinanceiraporeventofp.xhtml")
})
public class RelatorioFichaFinanceiraPorEventoControlador extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    private ConverterAutoComplete converterVinculoFP;
    private ConverterAutoComplete converterEventoFP;
    private VinculoFP vinculoFPSelecionado;
    private List<FichaFinanceiraFP> fichaFinanceiraFP;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private EventoFP eventoFPSelecionado;
    private List<EventoFP> eventosFPList;
    private String filtro;
    private Boolean todasVerbas;
    private String mesAnoInicial;
    private String mesAnoFinal;

    public RelatorioFichaFinanceiraPorEventoControlador() {
        eventosFPList = new LinkedList<>();
        geraNoDialog = Boolean.TRUE;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public VinculoFP getVinculoFPSelecionado() {
        return vinculoFPSelecionado;
    }

    public void setVinculoFPSelecionado(VinculoFP vinculoFPSelecionado) {
        this.vinculoFPSelecionado = vinculoFPSelecionado;
    }

    public List<FichaFinanceiraFP> getFichaFinanceiraFP() {
        return fichaFinanceiraFP;
    }

    public void setFichaFinanceiraFP(List<FichaFinanceiraFP> fichaFinanceiraFP) {
        this.fichaFinanceiraFP = fichaFinanceiraFP;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public EventoFP getEventoFPSelecionado() {
        return eventoFPSelecionado;
    }

    public void setEventoFPSelecionado(EventoFP eventoFPSelecionado) {
        this.eventoFPSelecionado = eventoFPSelecionado;
    }

    public Boolean getTodasVerbas() {
        return todasVerbas;
    }

    public void setTodasVerbas(Boolean todasVerbas) {
        this.todasVerbas = todasVerbas;
    }

    public List<EventoFP> getEventosFPList() {
        return eventosFPList;
    }

    public void setEventosFPList(List<EventoFP> eventosFPList) {
        this.eventosFPList = eventosFPList;
    }

    public String getMesAnoInicial() {
        return mesAnoInicial;
    }

    public void setMesAnoInicial(String mesAnoInicial) {
        this.mesAnoInicial = mesAnoInicial;
    }

    public String getMesAnoFinal() {
        return mesAnoFinal;
    }

    public void setMesAnoFinal(String mesAnoFinal) {
        this.mesAnoFinal = mesAnoFinal;
    }

    public void gerarRelatorio() throws JRException, IOException {
        try {
            if (validaCampos() && validaMes()) {
                String arquivoJasper = "RelatorioFichaFinanceiraPorEventoFP.jasper";
                String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
                subReport += "/report/";

                HashMap parameters = new HashMap();
                parameters.put("BRASAO", getCaminhoImagem());
                parameters.put("MODULO", "RECURSOS HUMANOS");
                parameters.put("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO");
                parameters.put("NOMERELATORIO", "RELATÓRIO DE FICHA FINANCEIRA POR EVENTO");
                parameters.put("DATAVIGENCIA", UtilRH.getDataOperacao());
                parameters.put("TIPOFOLHA", tipoFolhaDePagamento.name());
                parameters.put("SERVIDOR", vinculoFPSelecionado.getId());
                parameters.put("MESANOINICIAL", mesAnoInicial);
                parameters.put("MESANOFINAL", mesAnoFinal);
                parameters.put("SUBREPORT_DIR", subReport);
                if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
                    parameters.put("USUARIO", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
                } else {
                    parameters.put("USUARIO", sistemaControlador.getUsuarioCorrente().getLogin());
                }
                if (!todasVerbas) {
                    parameters.put("EVENTO", montaListaEvento());
                } else {
                    parameters.put("EVENTO", "");
                }
                gerarRelatorio(arquivoJasper, parameters);
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Ocorreu um erro ao gerar o relatório: " + ex.getMessage()));
        }
    }

    private String montaListaEvento() {
        String retorno = " and evento.id in (";
        for (EventoFP eventoFP : eventosFPList) {
            retorno += eventoFP.getId() + ",";
        }
        return retorno.substring(0, retorno.length() - 1) + ")";
    }

    public List<SelectItem> getTiposFolha() {
        List<SelectItem> retorno = new ArrayList<>();
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public Converter getConverterVinculoFP() {
        if (converterVinculoFP == null) {
            converterVinculoFP = new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
        }
        return converterVinculoFP;
    }

    public Converter getConverterEventoFP() {
        if (converterEventoFP == null) {
            converterEventoFP = new ConverterAutoComplete(EventoFP.class, eventoFPFacade);
        }
        return converterEventoFP;
    }

    public List<VinculoFP> completaContrato(String s) {
        return contratoFPFacade.buscaContratoFiltrandoAtributosVinculoMatriculaFP(s.trim());
    }

    public List<EventoFP> completaEventoFP(String parte) {
        return eventoFPFacade.listaFiltrandoEventosAtivos(parte.trim());
    }

    public Boolean validaCampos() {
        if (this.vinculoFPSelecionado == null) {
            FacesUtil.addCampoObrigatorio("Favor informar o campo Servidor.");
            return false;
        }

        if (this.tipoFolhaDePagamento == null) {
            FacesUtil.addCampoObrigatorio("Favor informar o campo Tipo Folha de Pagamento.");
            return false;
        }

        if (this.mesAnoInicial.isEmpty() || this.mesAnoFinal.isEmpty()) {
            FacesUtil.addCampoObrigatorio("Favor informar um intervalo de datas.");
            return false;
        }

        if (this.mesAnoInicial != null && this.mesAnoFinal != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
            try {
                DateTime d = new DateTime(sdf.parse(this.getMesAnoInicial()));
                DateTime d2 = new DateTime(sdf.parse(this.getMesAnoFinal()));
                d.withDayOfMonth(1);
                d2.withDayOfMonth(1);
                if (d2.isBefore(d)) {
                    FacesUtil.addOperacaoNaoPermitida("O Mês/Ano Inicial não pode ser maior que o Mês/Ano Final");
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (!todasVerbas && eventosFPList == null) {
            FacesUtil.addOperacaoNaoPermitida("Favor informar o campo Verba.");
            return false;
        }
        return true;
    }

    public Boolean validaMes() {
        if (this.getMesAnoInicial() != null) {
            int mesInicio = getMes(this.getMesAnoInicial());
            int mesFim = getMes(this.getMesAnoInicial());

            if (mesInicio < 1 || mesInicio > 12) {
                FacesUtil.addCampoObrigatorio("Favor informar um mês entre 01 (Janeiro) e 12 (Dezembro).");
                return false;
            }
            if (mesFim < 1 || mesFim > 12) {
                FacesUtil.addCampoObrigatorio("Favor informar um mês entre 01 (Janeiro) e 12 (Dezembro).");
                return false;
            }
        }
        return true;
    }

    public static int getMes(String s) {
        StringTokenizer st = new StringTokenizer(s, "/");
        int mes = Integer.parseInt((String) st.nextElement());
        return mes;
    }

    @URLAction(mappingId = "Relatorio-Ficha-Financeirao-Por-EventoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.vinculoFPSelecionado = null;
        this.mesAnoInicial = null;
        this.mesAnoFinal = null;
        this.eventoFPSelecionado = null;
        eventosFPList = new ArrayList<>();
        todasVerbas = Boolean.FALSE;
    }
}
