package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LancamentoFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidadesauxiliares.EventoFPRejeitado;
import br.com.webpublico.entidadesauxiliares.RejeitadosEconsig;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.joda.time.DateTime;
import org.joda.time.YearMonth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by peixe on 15/09/2015.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "verbas-econsig", pattern = "/relatorio/verbas-econsig/",
        viewId = "/faces/rh/relatorios/rotinas-anuais-mensais/relatorio-verbas-econsig.xhtml")
})

public class RelatorioVerbasEconsigControlador extends AbstractReport implements Serializable, ValidadorEntidade {
    private static final Logger log = LoggerFactory.getLogger(RelatorioVerbasEconsigControlador.class);
    private static final String NOME_RELATORIO = "RelatorioVerbasEconsig.jasper";

    private Mes mes;
    private Integer ano;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private VinculoFP vinculoFP;
    @EJB
    private LancamentoFPFacade lancamentoFPFacade;
    private ConverterAutoComplete converterHierarquia;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterContratoFP;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    private TipoFiltro tipoFiltro;
    private YearMonth mesAnoInicial;
    private YearMonth mesAnoFinal;
    private Boolean somenteRejeitados;

    public RelatorioVerbasEconsigControlador() {
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public TipoFiltro getTipoFiltro() {
        return tipoFiltro;
    }

    public void setTipoFiltro(TipoFiltro tipoFiltro) {
        this.tipoFiltro = tipoFiltro;
    }

    public Boolean getSomenteRejeitados() {
        return somenteRejeitados == null ? false : somenteRejeitados;
    }

    public void setSomenteRejeitados(Boolean somenteRejeitados) {
        this.somenteRejeitados = somenteRejeitados;
    }

    public YearMonth getMesAnoInicial() {
        return mesAnoInicial;
    }

    public void setMesAnoInicial(YearMonth mesAnoInicial) {
        this.mesAnoInicial = mesAnoInicial;
    }

    public YearMonth getMesAnoFinal() {
        return mesAnoFinal;
    }

    public void setMesAnoFinal(YearMonth mesAnoFinal) {
        this.mesAnoFinal = mesAnoFinal;
    }

    public void limparComponentes() {
        vinculoFP = null;
        hierarquiaOrganizacional = null;
    }


    public Converter getConverterHierarquia() {
        if (converterHierarquia == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquia;
    }

    public Converter getConverterYearMonth() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
                try {
                    String[] array = s.split("/");
                    log.debug("{} ", array[0]);
                    log.debug("{} ", array[1]);
                    YearMonth mesAno = new YearMonth(Integer.parseInt(array[1]), Integer.parseInt(array[0]));
                    return mesAno;
                } catch (Exception e) {
                    log.error("não foi possível converter a string");
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
                YearMonth mesAno = (YearMonth) o;
                return mesAno.getMonthOfYear() + "/" + mesAno.getYear();
            }
        };
    }

    public List<HierarquiaOrganizacional> completarHierarquia(String parte) {
        List<HierarquiaOrganizacional> hos = new ArrayList<>();
        //hos.add(hierarquiaOrganizacionalFacade.getRaizHierarquia(UtilRH.getDataOperacao()));
        hos.addAll(hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao()));
        return hos;
    }

    public List<VinculoFP> completaContrato(String s) {
        return contratoFPFacade.buscaContratoFiltrandoAtributosVinculoMatriculaFP(s.trim());
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
        }
        return converterContratoFP;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Mes object : Mes.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public void imprimirRelatorio(Mes mes, Integer ano) {
        init();
        this.mes = mes;
        this.ano = ano;
        gerarRelatorioVerbasEconsig();
    }

    public void gerarRelatorioVerbasEconsig() {
        try {
            log.debug("Inciando a geração do relatório de verbas para o Mês {} e ano {} .", mesAnoInicial, mesAnoFinal);
            setGeraNoDialog(true);
            validarConfirmacao();
            gerarRelatorioRejeitados();
        } catch (ValidacaoException val) {
            FacesUtil.printAllFacesMessages(val.getAllMensagens());
        }

    }

    @URLAction(mappingId = "verbas-econsig", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void init() {
        DateTime hoje = new DateTime(new Date());
        mes = Mes.getMesToInt(hoje.getMonthOfYear());
        ano = hoje.getYear();
        somenteRejeitados = false;
        tipoFiltro = TipoFiltro.VINCULO;
    }

    private void gerarRelatorioRejeitados() {
        try {
            HashMap parameters = new HashMap();

            parameters.put("PREFEITURA", "PREFEITURA DE RIO BRANCO");
            parameters.put("NOMERELATORIO", "RELATÓRIO DE VERBAS DO E-CONSIG");
            parameters.put("SECRETARIA", "SECRETARIA MUNICIPAL DE GESTÃO E ADMINISTRAÇÃO DE PESSOAS");
            parameters.put("SUBREPORT_DIR", getCaminhoSubReport());
            parameters.put("BRASAO", getCaminhoImagem());
            parameters.put("MESINICIAL", getMesAnoFormatado(mesAnoInicial));
            parameters.put("MESFINAL", getMesAnoFormatado(mesAnoFinal));
            gerarRelatorioComDadosEmCollection(NOME_RELATORIO, parameters, new JRBeanCollectionDataSource(montarRejeitadosDoEconsig()));
        } catch (ValidacaoException e) {
            if (!e.getMensagens().isEmpty()) {
                FacesUtil.printAllFacesMessages(e.getMensagens());
            } else {
                FacesUtil.addAtencao(e.getMessage());
            }
        } catch (JRException e) {
            log.error("Erro: ", e);
            FacesUtil.addAtencao(e.getMessage());
        } catch (IOException e) {
            log.error("Erro: ", e);
            FacesUtil.addAtencao(e.getMessage());
        }
    }

    private String getMesAnoFormatado(YearMonth mesAno) {
        return mesAno.getMonthOfYear() + "/" + mesAno.getYear();
    }

    public List<RejeitadosEconsig> montarRejeitadosDoEconsig() {
        validarConfirmacao();
        List<LancamentoFP> lancamentosRejeitados = buscarLancamentos();
        if (lancamentosRejeitados == null || lancamentosRejeitados.isEmpty()) {
            throw new ValidacaoException("Não foi encontrado Lançamentos de verba do E-Consig no período selecionado.");
        }
        return gerarItemRejeitado(lancamentosRejeitados);
    }

    private List<LancamentoFP> buscarLancamentos() {
        List<LancamentoFP> lancamentoFPs = new LinkedList<>();
        if (vinculoFP == null && hierarquiaOrganizacional == null) {
            lancamentoFPs = lancamentoFPFacade.buscarLancamentosEconsigPorPeriodo(mesAnoInicial, mesAnoFinal, getSomenteRejeitados());
        }
        if (vinculoFP != null) {
            lancamentoFPs = lancamentoFPFacade.buscarLancamentosEconsigPorMesAndAnoAndVinculoFP(mesAnoInicial, mesAnoFinal, getSomenteRejeitados(), vinculoFP);
        }
        if (hierarquiaOrganizacional != null) {
            lancamentoFPs = lancamentoFPFacade.buscarLancamentosEconsigPorMesAndAnoAndOrgao(mesAnoInicial, mesAnoFinal, getSomenteRejeitados(), hierarquiaOrganizacional);
        }
        ordenarLancamentos(lancamentoFPs);
        return lancamentoFPs;
    }

    private void ordenarLancamentos(List<LancamentoFP> lancamentoFPs) {
        Collections.sort(lancamentoFPs, LancamentoFP.Comparators.MATRICULA_ASC);
    }


    public List<RejeitadosEconsig> gerarItemRejeitado(List<LancamentoFP> lancamentos) {
        List<RejeitadosEconsig> rejeitadosEconsigs = new LinkedList<>();
        Integer contatorRejeitados = 0;
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (LancamentoFP lancamento : lancamentos) {
            gerarItem(rejeitadosEconsigs, lancamento);
        }
        for (RejeitadosEconsig rejeitadosEconsig : rejeitadosEconsigs) {
            for (EventoFPRejeitado eventoFPRejeitado : rejeitadosEconsig.getEventosRejeitados()) {
                if (eventoFPRejeitado.getMotivoRejeicao() != null && !"".equals(eventoFPRejeitado.getMotivoRejeicao())) {
                    contatorRejeitados++;
                }
                valorTotal = valorTotal.add(eventoFPRejeitado.getValor() != null ? eventoFPRejeitado.getValor() : BigDecimal.ZERO);
            }
            rejeitadosEconsig.setTotalRejeitados(contatorRejeitados);
            rejeitadosEconsig.setValorTotal(valorTotal);
        }
        ordenarItens(rejeitadosEconsigs);
        return rejeitadosEconsigs;
    }

    private void ordenarItens(List<RejeitadosEconsig> rejeitadosEconsigs) {
        for (RejeitadosEconsig rejeitadosEconsig : rejeitadosEconsigs) {
            Collections.sort(rejeitadosEconsig.getEventosRejeitados(), EventoFPRejeitado.Comparators.EVENTO_VERBA);
        }
    }

    private void gerarItem(List<RejeitadosEconsig> lancamentosEconsig, LancamentoFP lancamento) {

        RejeitadosEconsig lancamentoEconsig = new RejeitadosEconsig();
        lancamentoEconsig.setVinculoFP(lancamento.getVinculoFP());
        lancamentoEconsig.setId(lancamento.getVinculoFP().getId());
        if (!lancamentosEconsig.contains(lancamentoEconsig)) {
            lancamentoEconsig.setId(lancamento.getVinculoFP().getId());
            lancamentoEconsig.setMes(mes.getDescricao());
            lancamentoEconsig.setAno(ano);
            lancamentoEconsig.setMatricula(lancamento.getVinculoFP().getMatriculaFP().getMatricula());
            lancamentoEconsig.setNome(lancamento.getVinculoFP().getMatriculaFP().getPessoa().getNome());
            lancamentoEconsig.setNumeroContrato(lancamento.getVinculoFP().getNumero());
            lancamentoEconsig.setVinculoFP(lancamento.getVinculoFP());
            definirItem(lancamento, lancamentoEconsig);
            lancamentosEconsig.add(lancamentoEconsig);
        } else {
            int index = lancamentosEconsig.indexOf(lancamentoEconsig);
            lancamentoEconsig = lancamentosEconsig.get(index);
            definirItem(lancamento, lancamentoEconsig);
        }
    }

    private EventoFPRejeitado definirItem(LancamentoFP lancamento, RejeitadosEconsig lancamentoEconsig) {
        EventoFPRejeitado eventoLancado = gerarItemEventoEconsig(lancamento);
        eventoLancado.setValor(lancamento.getQuantificacao());
        lancamentoEconsig.getEventosRejeitados().add(eventoLancado);
        return eventoLancado;
    }

    private EventoFPRejeitado gerarItemEventoEconsig(LancamentoFP lancamento) {
        EventoFPRejeitado eventoFPRejeitado = new EventoFPRejeitado();
        eventoFPRejeitado.setTipoLancamentoFP(lancamento.getTipoLancamentoFP());
        eventoFPRejeitado.setInicioVigencia(lancamento.getInicioVigencia());
        eventoFPRejeitado.setFinalVigencia(lancamento.getFinalVigencia());
        eventoFPRejeitado.setVerba(lancamento.getEventoFP().toString());
        eventoFPRejeitado.setMotivoRejeicao(lancamento.getMotivoRejeicao() != null ? lancamento.getMotivoRejeicao().toString() : "");
        return eventoFPRejeitado;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        ValidacaoException validacaoException = new ValidacaoException();
        if (mesAnoInicial == null) {
            validacaoException.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Preencha o Mês/Ano Inicial corretamente.");
        }
        if (mesAnoFinal == null) {
            validacaoException.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Preencha o Mês/Ano Final corretamente.");
        }
        validacaoException.lancarException();
        if (mesAnoInicial.isAfter(mesAnoFinal)) {
            validacaoException.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "O Mês/Ano Inicial deve ser menor que o Mês/Ano final.");
        }
        validacaoException.lancarException();
    }

    public enum TipoFiltro {
        VINCULO, ORGAO;
    }
}
