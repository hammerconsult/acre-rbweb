package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoProvimento;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidadesauxiliares.ItemConsignacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 28/03/14
 * Time: 16:19
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "monitoramento-rh-lancamentos", pattern = "/monitoramento/rh/", viewId = "/faces/rh/estatisticas/informacoes/monitoramento.xhtml")
})
public class MonitoramentoRHControlador extends SuperControladorCRUD implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(MonitoramentoRHControlador.class);

    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private LancamentoFPFacade lancamentoFPFacade;

    @EJB
    private FaltasFacade faltasFacade;
    @EJB
    private HoraExtraFacade horaExtraFacade;
    @EJB
    private ConcessaoFeriasLicencaFacade concessaoFeriasLicencaFacade;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;

    @EJB
    private DependenteFacade dependenteFacade;
    @EJB
    private CargoConfiancaFacade cargoConfiancaFacade;
    @EJB
    private FuncaoGratificadaFacade funcaoGratificadaFacade;
    private List<ItemConsignacao> itemConsignacoes;

    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private PensaoAlimenticiaFacade pensaoAlimenticiaFacade;

    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade;
    @EJB
    private ComparadorDeFollhasFacade comparadorDeFollhasFacade;

    private Map<Date, Map<TipoLancamento, Integer>> lancamentos;

    private Date inicio;
    private Date fim;
    private boolean econsig;
    @EJB
    private CedenciaContratoFPFacade cedenciaContratoFP;
    @EJB
    private RetornoFuncaoGratificadaFacade retornoFuncaoGratificadaFacade;
    @EJB
    private SextaParteFacade sextaParteFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private JustificativaFaltasFacade justificativaFaltasFacade;
    @EJB
    private PenalidadeFPFacade penalidadeFPFacade;
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;

    public boolean isEconsig() {
        return econsig;
    }

    public void setEconsig(boolean econsig) {
        this.econsig = econsig;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public void verificarQuantidadeDeLancamentos() {
        try {
            logger.debug("iniciando a contagem dos lançamentos...");
            inicializar();
            validarCamposObrigatorios();
            contarTodosLancamentos();
        } catch (ValidacaoException ve) {
            logger.error("Erro ao tentar ao tentar executar verificarQuantidadeDeLancamentos", ve);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ve) {
            logger.error("Erro ao tentar ao tentar executar verificarQuantidadeDeLancamentos", ve);
            FacesUtil.addOperacaoNaoRealizada("Erro ao tentar verificar quantidade de lançamentos: " + ve.getMessage());
        }

    }

    private void inicializar() {
        lancamentos = new LinkedHashMap<>();
    }

    private void validarCamposObrigatorios() {
        ValidacaoException validacaoException = new ValidacaoException();
        if (inicio == null) {
            validacaoException.adicionarMensagemDeCampoObrigatorio("Início do intervalo é obrigatório.");
        }
        if (fim == null) {
            validacaoException.adicionarMensagemDeCampoObrigatorio("Fim do intervalo é obrigatório.");
        }
        if (inicio != null && fim != null && inicio.after(fim)) {
            validacaoException.adicionarMensagemDeCampoObrigatorio("Início do intervalo não deve ser maior que o fim.");
        }
        if (validacaoException.temMensagens()) {
            throw validacaoException;
        }
    }

    private void contarTodosLancamentos() {
        Date inicioTemp = inicio;
        while (inicioTemp.before(fim)) {
            for (TipoLancamento tipoLancamento : TipoLancamento.values()) {
                inicializarCollectionPorChave(tipoLancamento, lancamentos, inicioTemp);
                switch (tipoLancamento) {
                    case SERVIDOR_LANCAMENTOFP:
                        processarVinculosEmLancamentoFP(tipoLancamento, inicioTemp);
                        break;
                    case LANCAMENTOFP:
                        processarLancamentoFP(tipoLancamento, inicioTemp);
                        break;
                    case FALTAS:
                        processarFaltas(tipoLancamento, inicioTemp);
                        break;
                    case NOMEACAO:
                        processarNomeacoes(tipoLancamento, inicioTemp);
                        break;
                    case ACESSO_CC:
                        processarAcessoCC(tipoLancamento, inicioTemp);
                        break;
                    case RETORNO_CC:
                        processarRetornoCC(tipoLancamento, inicioTemp);
                        break;
                    case ACESSO_FG:
                        processarFG(tipoLancamento, inicioTemp);
                        break;
                    case RETORNO_FG:
                        processarRetornoFG(tipoLancamento, inicioTemp);
                        break;
                    case CEDENCIA:
                        processarCedencias(tipoLancamento, inicioTemp);
                        break;
                    case SEXTA_PARTE:
                        processarSextaParte(tipoLancamento, inicioTemp);
                        break;
                    case PROGRESSAO:
                        processarProgressaoPCS(tipoLancamento, inicioTemp);
                        break;
                    case PROMOCAO:
                        processarPromocao(tipoLancamento, inicioTemp);
                        break;
                    case FERIAS:
                        processarFerias(tipoLancamento, inicioTemp);
                        break;
                    case JUSTIFICATIVA_FALTAS:
                        processarJustificativaFaltas(tipoLancamento, inicioTemp);
                        break;
                    case PENALIDADE:
                        processarPenalidades(tipoLancamento, inicioTemp);
                        break;
                    case HORA_EXTA:
                        processarHoraExtra(tipoLancamento, inicioTemp);
                        break;
                    case EXONERACAO_RESCISAO:
                        processarExoneracaoRescisao(tipoLancamento, inicioTemp);
                        break;
                    case ENQUADRAMENTO:
                        processarEnquadramento(tipoLancamento, inicioTemp);
                        break;
                }
            }
            inicioTemp = new DateTime(inicioTemp).plusDays(1).toDate();
        }
    }

    private void processarExoneracaoRescisao(TipoLancamento tipoLancamento, Date inicioTemp) {
        Integer totalLancamentos = exoneracaoRescisaoFacade.buscarQuantidadeRescisoesPorData(inicioTemp);
        incluirLancamentos(tipoLancamento, inicioTemp, totalLancamentos);
    }

    private void processarHoraExtra(TipoLancamento tipoLancamento, Date inicioTemp) {
        Integer totalLancamentos = horaExtraFacade.buscarQuantidadeDeHoraExtra(inicioTemp);
        incluirLancamentos(tipoLancamento, inicioTemp, totalLancamentos);
    }

    private void processarPenalidades(TipoLancamento tipoLancamento, Date inicioTemp) {
        Integer totalLancamentos = penalidadeFPFacade.buscarQuantidadeDePenalidadesPorData(inicioTemp);
        incluirLancamentos(tipoLancamento, inicioTemp, totalLancamentos);
    }

    private void processarJustificativaFaltas(TipoLancamento tipoLancamento, Date inicioTemp) {
        Integer totalLancamentos = justificativaFaltasFacade.buscarQuantidadeDeJustificativaFaltasPorData(inicioTemp);
        incluirLancamentos(tipoLancamento, inicioTemp, totalLancamentos);
    }

    private void processarFerias(TipoLancamento tipoLancamento, Date inicioTemp) {
        Integer totalLancamentos = concessaoFeriasLicencaFacade.buscarQuantidadeDeFeriasPorData(inicioTemp);
        incluirLancamentos(tipoLancamento, inicioTemp, totalLancamentos);
    }

    private void processarEnquadramento(TipoLancamento tipoLancamento, Date inicioTemp) {
        Integer totalLancamentos = provimentoFPFacade.buscarQuantidadeDeProvimentoPorTipoProvimento(inicioTemp, TipoProvimento.ENQUADRAMENTO);
        incluirLancamentos(tipoLancamento, inicioTemp, totalLancamentos);
    }

    private void processarPromocao(TipoLancamento tipoLancamento, Date inicioTemp) {
        Integer totalLancamentos = provimentoFPFacade.buscarQuantidadeDeProvimentoPorTipoProvimento(inicioTemp, TipoProvimento.PROMOCAO);
        incluirLancamentos(tipoLancamento, inicioTemp, totalLancamentos);
    }

    private void processarProgressaoPCS(TipoLancamento tipoLancamento, Date inicioTemp) {
        Integer totalLancamentos = provimentoFPFacade.buscarQuantidadeDeProvimentoPorTipoProvimento(inicioTemp, TipoProvimento.PROVIMENTO_PROGRESSAO);
        incluirLancamentos(tipoLancamento, inicioTemp, totalLancamentos);
    }

    private void processarSextaParte(TipoLancamento tipoLancamento, Date inicioTemp) {
        Integer totalLancamentos = sextaParteFacade.buscarQuantidadeDeServidoresRegistradosSextaParte(inicioTemp);
        incluirLancamentos(tipoLancamento, inicioTemp, totalLancamentos);
    }

    private void processarRetornoCC(TipoLancamento tipoLancamento, Date inicio) {
        Integer totalLancamentos = cargoConfiancaFacade.buscarQuantidadeDeServidoresRetornoAcessoCC(inicio);
        incluirLancamentos(tipoLancamento, inicio, totalLancamentos);
    }

    private void processarRetornoFG(TipoLancamento tipoLancamento, Date inicioTemp) {
        Integer totalLancamentos = retornoFuncaoGratificadaFacade.buscarQuantidadeDeServidoresRetornoAcessoFG(inicioTemp);
        incluirLancamentos(tipoLancamento, inicioTemp, totalLancamentos);
    }

    private void processarCedencias(TipoLancamento tipoLancamento, Date inicioTemp) {
        Integer totalLancamentos = cedenciaContratoFP.buscarQuantidadeDeServidoresComCedencia(inicioTemp);
        incluirLancamentos(tipoLancamento, inicioTemp, totalLancamentos);
    }

    private void processarFG(TipoLancamento tipoLancamento, Date inicioTemp) {
        Integer totalLancamentos = funcaoGratificadaFacade.buscarQuantidadeDeServidoresNomeadosFG(inicioTemp);
        incluirLancamentos(tipoLancamento, inicioTemp, totalLancamentos);
    }

    private void processarAcessoCC(TipoLancamento tipoLancamento, Date inicioTemp) {
        Integer totalLancamentos = cargoConfiancaFacade.buscarQuantidadeDeServidoresNomeadosCC(inicioTemp);
        incluirLancamentos(tipoLancamento, inicioTemp, totalLancamentos);
    }

    private void processarNomeacoes(TipoLancamento tipoLancamento, Date inicioTemp) {
        Integer totalLancamentos = contratoFPFacade.buscarQuantidadeDeServidoresNomeados(inicioTemp);
        incluirLancamentos(tipoLancamento, inicioTemp, totalLancamentos);
    }

    private void inicializarCollection(TipoLancamento tipoLancamento, Map<TipoLancamento, Map<Date, Integer>> lancamentos) {
        if (lancamentos.get(tipoLancamento) == null) {
            lancamentos.put(tipoLancamento, new LinkedHashMap<Date, Integer>());
        }
    }

    private void inicializarCollectionPorChave(TipoLancamento tipoLancamento, Map<Date, Map<TipoLancamento, Integer>> lancamentos, Date inicioTemp) {
        if (lancamentos.get(inicioTemp) == null) {
            lancamentos.put(inicioTemp, new LinkedHashMap<TipoLancamento, Integer>());
        }
    }

    private void processarVinculosEmLancamentoFP(TipoLancamento tipoLancamento, Date inicio) {
        Integer totalLancamentos = lancamentoFPFacade.buscarQuantidadeDeServidoresComLancamentosPorData(inicio, econsig);
        incluirLancamentos(tipoLancamento, inicio, totalLancamentos);
    }

    private void incluirLancamentos(TipoLancamento tipoLancamento, Date inicio, Integer totalLancamentos) {
        lancamentos.get(inicio).put(tipoLancamento, totalLancamentos);
    }

    private void processarFaltas(TipoLancamento tipoLancamento, Date inicio) {
        Integer totalLancamentos = faltasFacade.buscarQuantidadeDeFaltasPorData(inicio);
        incluirLancamentos(tipoLancamento, inicio, totalLancamentos);
    }

    private void processarLancamentoFP(TipoLancamento tipoLancamento, Date inicio) {
        Integer totalLancamentos = lancamentoFPFacade.buscarQuantidadeDeLancamentosPorData(inicio, econsig);
        incluirLancamentos(tipoLancamento, inicio, totalLancamentos);
    }

    public Map<Date, Map<TipoLancamento, Integer>> getLancamentos() {
        return lancamentos;
    }

    public void setLancamentos(Map<Date, Map<TipoLancamento, Integer>> lancamentos) {
        this.lancamentos = lancamentos;
    }


    public List<TipoLancamento> getLancamentosHeaderData() {
        List<TipoLancamento> headerTable = new LinkedList<>();
        if (!lancamentos.isEmpty()) {
            for (Map.Entry<Date, Map<TipoLancamento, Integer>> dateIntegerEntry : lancamentos.entrySet()) {
                for (Map.Entry<TipoLancamento, Integer> keys : dateIntegerEntry.getValue().entrySet()) {
                    headerTable.add(keys.getKey());
                }
                return headerTable;
            }
        }
        return headerTable;
    }

    public List<Date> getLancamentosData() {
        List<Date> valueTable = new LinkedList<>();
        if (!lancamentos.isEmpty()) {
            for (Map.Entry<Date, Map<TipoLancamento, Integer>> lanc : lancamentos.entrySet()) {
                valueTable.add(lanc.getKey());
            }
        }
        return valueTable;
    }




    @Override
    public AbstractFacade getFacede() {
        return contratoFPFacade;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @URLAction(mappingId = "monitoramento-rh-lancamentos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        DateTime hoje = DateTime.now();
        fim = hoje.toDate();
        inicio = hoje.minusDays(3).toDate();
        econsig = false;
        inicializar();
    }


    public Converter getConverterContratoFP() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
                return vinculoFPFacade.recuperar(VinculoFP.class, s);

            }

            @Override
            public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
                if (value == null) {
                    return null;
                } else {
                    if (value instanceof Long) {
                        return String.valueOf(value);
                    } else {
                        try {
                            return value.toString();
                        } catch (Exception e) {
                            return String.valueOf(value);
                        }
                    }
                }
            }

        };

    }

    public enum TipoLancamento {
        SERVIDOR_LANCAMENTOFP("Servidores em Lançamento FP"),
        LANCAMENTOFP("Lançamento FP"),
        NOMEACAO("Nomeação"),
        ACESSO_CC("Acesso a CC"),
        RETORNO_CC("Retorno de CC"),
        ACESSO_FG("Acesso a FG"),
        RETORNO_FG("Retorno de FG"),
        CEDENCIA("Cedências"),
        SEXTA_PARTE("Sexta Parte"),
        PROGRESSAO("Progressão"),
        PROMOCAO("Promoção"),
        FERIAS("Férias"),
        FALTAS("Faltas"),
        JUSTIFICATIVA_FALTAS("Justificativa de Faltas"),
        PENALIDADE("Penalidade"),
        HORA_EXTA("Hora Extra"),
        EXONERACAO_RESCISAO("Rescisão"),
        ENQUADRAMENTO("Enquadramento");

        private String descricao;

        TipoLancamento(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
