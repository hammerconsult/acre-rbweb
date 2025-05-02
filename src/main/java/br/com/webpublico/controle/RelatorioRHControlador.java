package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItensResultado;
import br.com.webpublico.entidadesauxiliares.ObjetoPesquisa;
import br.com.webpublico.entidadesauxiliares.ObjetoResultado;
import br.com.webpublico.entidadesauxiliares.rh.RelatorioRH;
import br.com.webpublico.enums.TipoEventoFP;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rh", pattern = "/relatorio/rh/gerencial/", viewId = "/faces/rh/estatisticas/informacoes/relatorio.xhtml")

})
public class RelatorioRHControlador extends SuperControladorCRUD implements Serializable {
    @EJB
    private ComparadorDeFollhasFacade comparadorDeFollhasFacade;
    private ObjetoResultado objetoResultado;
    private ObjetoPesquisa objetoPesquisa;
    private List<ObjetoResultado> objetoResultados;
    private Map<RecursoFP, List<RecursoDoVinculoFP>> recursos;
    private List<ItensResultado> itensResultados;
    private ConverterAutoComplete converterEventoFP;
    @EJB
    private EventoFPFacade eventoFPFacade;
    private ComparadorWeb comparadorWeb;
    @EJB
    private RecursoFPFacade recursoFPFacade;
    @EJB
    private RecursoDoVinculoFPFacade recursoDoVinculoFPFacade;
    @EJB
    private GrupoRecursoFPFacade grupoRecursoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    Map<EventoFP, Map<TipoEventoFP, BigDecimal>> mapaValores;
    Map<Class, List<VinculoFP>> vinculos;
    Future<RelatorioRH> relatorioRHFuture;
    RelatorioRH relatorioRH;
    private TotaisRelatorioRH totaisRelatorioRH;

    private List<ComparadorFolha> comparadorFolhas = new LinkedList<>();

    @URLAction(mappingId = "relatorio-rh", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        objetoPesquisa = new ObjetoPesquisa();
        objetoResultados = new LinkedList<>();
        itensResultados = new LinkedList<>();
        comparadorWeb = new ComparadorWeb();
        recursos = Maps.newHashMap();
        vinculos = Maps.newHashMap();
        mapaValores = Maps.newHashMap();
        vinculos = Maps.newHashMap();
        totaisRelatorioRH = new TotaisRelatorioRH();
        relatorioRH = new RelatorioRH();
    }


    @Override
    public AbstractFacade getFacede() {
        return comparadorDeFollhasFacade;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void iniciarBusca() throws ExecutionException, InterruptedException {
        relatorioRHFuture = comparadorDeFollhasFacade.contabilizarVinculos(objetoPesquisa);
        relatorioRH = relatorioRHFuture.get();
        mapaValores = relatorioRHFuture.get().getValores();
        vinculos = relatorioRHFuture.get().getVinculos();
    }

    private DateTime getReferencia() {
        DateTime referencia = DataUtil.criarDataComMesEAno(objetoPesquisa.getMes(), objetoPesquisa.getAno());
        referencia = referencia.withDayOfMonth(referencia.dayOfMonth().getMaximumValue());
        return referencia;
    }

    private boolean validaObjetoPesquisa() {
        if (objetoPesquisa == null) return false;
        if (objetoPesquisa.getAno() == null) return false;
        if (objetoPesquisa.getMes() == null) return false;
        return true;
    }



    public List<EventoFP> eventoFPs(String parte) {
        return eventoFPFacade.listaFiltrandoEventosAtivos(parte.trim());
    }

    public Converter getConverterEventoFP() {
        if (converterEventoFP == null) {
            converterEventoFP = new ConverterAutoComplete(EventoFP.class, eventoFPFacade);
        }
        return converterEventoFP;
    }

    public void listarValoresCalculados() throws ExecutionException, InterruptedException {
        logger.debug("chamando a listagem...");
        calcularTotais();
    }

    public Map<EventoFP, Map<TipoEventoFP, BigDecimal>> getValores() throws ExecutionException, InterruptedException {
        if (mapaValores != null) {
            Map<EventoFP, Map<TipoEventoFP, BigDecimal>> valores = Maps.newHashMap();
            valores = mapaValores;
            return valores;
        }
        return null;
    }

    public List<EventoFP> getEventosCalculados() throws ExecutionException, InterruptedException {
        List<EventoFP> eventos = Lists.newArrayList();
        logger.debug("chamando eventos calculados");
        if (mapaValores != null) {
            Map<EventoFP, Map<TipoEventoFP, BigDecimal>> valores = Maps.newHashMap();
            valores = mapaValores;
            for (Map.Entry<EventoFP, Map<TipoEventoFP, BigDecimal>> eventoFPMapEntry : valores.entrySet()) {
                logger.debug("Evento Controller: {} ", eventoFPMapEntry.getKey());
                eventos.add(eventoFPMapEntry.getKey());
            }
        }
        return eventos;
    }

    public void setEventosCalculados(List<EventoFP> eventos) {

    }

    public Map<EventoFP, Map<TipoEventoFP, BigDecimal>> getMapaValores() {
        return mapaValores;
    }

    public ObjetoPesquisa getObjetoPesquisa() {
        return objetoPesquisa;
    }

    public void setObjetoPesquisa(ObjetoPesquisa objetoPesquisa) {
        this.objetoPesquisa = objetoPesquisa;
    }

    public Map<RecursoFP, List<RecursoDoVinculoFP>> getRecursos() {
        return recursos;
    }

    public Map<Class, List<VinculoFP>> getVinculos() {
        return vinculos;
    }

    public void setVinculos(Map<Class, List<VinculoFP>> vinculos) {
        this.vinculos = vinculos;
    }

    public TotaisRelatorioRH getTotaisRelatorioRH() {
        return totaisRelatorioRH;
    }

    public void setTotaisRelatorioRH(TotaisRelatorioRH totaisRelatorioRH) {
        this.totaisRelatorioRH = totaisRelatorioRH;
    }

    public void setRecursos(Map<RecursoFP, List<RecursoDoVinculoFP>> recursos) {
        this.recursos = recursos;
    }

    public String getCaminhoBrasao() {
        String imagem = FacesUtil.geraUrlImagemDir();
        imagem += "/img/Brasao_de_Rio_Branco.gif";
        return imagem;
    }

    public void calcularTotais() {
        Map<EventoFP, Map<TipoEventoFP, BigDecimal>> valores = Maps.newHashMap();
       /* if (mapaValores != null) {
            for (Map.Entry<EventoFP, Map<TipoEventoFP, BigDecimal>> valorEvento : mapaValores.entrySet()) {
                for (Map.Entry<TipoEventoFP, BigDecimal> tipoValor : valorEvento.getValue().entrySet()) {
                    if (TipoEventoFP.VANTAGEM.equals(tipoValor.getKey())) {
                        totaisRelatorioRH.setTotalVantagem(totaisRelatorioRH.getTotalVantagem().add(tipoValor.getValue()));
                    }
                    if (TipoEventoFP.DESCONTO.equals(tipoValor.getKey())) {
                        totaisRelatorioRH.setTotalDesconto(totaisRelatorioRH.getTotalDesconto().add(tipoValor.getValue()));
                    }
                }
            }
        }*/
        if (relatorioRH != null) {
            for (Map.Entry<Class, Map<VinculoFP, Map<EventoFP, Map<TipoEventoFP, BigDecimal>>>> classeVinculo : relatorioRH.getVinculosValor().entrySet()) {
                for (Map.Entry<VinculoFP, Map<EventoFP, Map<TipoEventoFP, BigDecimal>>> vinculoVerba : classeVinculo.getValue().entrySet()) {
                    for (Map.Entry<EventoFP, Map<TipoEventoFP, BigDecimal>> eventoTipo : vinculoVerba.getValue().entrySet()) {
                        for (Map.Entry<TipoEventoFP, BigDecimal> tipoValor : eventoTipo.getValue().entrySet()) {
                            if (ContratoFP.class.equals(classeVinculo.getKey())) {
                                if (TipoEventoFP.VANTAGEM.equals(tipoValor.getKey())) {
                                    totaisRelatorioRH.setTotalVantagem(totaisRelatorioRH.getTotalVantagem().add(tipoValor.getValue()));
                                }
                                if (TipoEventoFP.DESCONTO.equals(tipoValor.getKey())) {
                                    totaisRelatorioRH.setTotalDesconto(totaisRelatorioRH.getTotalDesconto().add(tipoValor.getValue()));
                                }
                            }
                            if (Aposentadoria.class.equals(classeVinculo.getKey())) {
                                if (TipoEventoFP.VANTAGEM.equals(tipoValor.getKey())) {
                                    totaisRelatorioRH.setTotalVantagemAposentadoria(totaisRelatorioRH.getTotalVantagemAposentadoria().add(tipoValor.getValue()));
                                }
                                if (TipoEventoFP.DESCONTO.equals(tipoValor.getKey())) {
                                    totaisRelatorioRH.setTotalDescontoAposentadoria(totaisRelatorioRH.getTotalDescontoAposentadoria().add(tipoValor.getValue()));
                                }
                            }
                            if (Pensionista.class.equals(classeVinculo.getKey())) {
                                if (TipoEventoFP.VANTAGEM.equals(tipoValor.getKey())) {
                                    totaisRelatorioRH.setTotalVantagemPensionista(totaisRelatorioRH.getTotalVantagemPensionista().add(tipoValor.getValue()));
                                }
                                if (TipoEventoFP.DESCONTO.equals(tipoValor.getKey())) {
                                    totaisRelatorioRH.setTotalDescontoPensionista(totaisRelatorioRH.getTotalDescontoPensionista().add(tipoValor.getValue()));
                                }
                            }
                            if (Beneficiario.class.equals(classeVinculo.getKey())) {
                                if (TipoEventoFP.VANTAGEM.equals(tipoValor.getKey())) {
                                    totaisRelatorioRH.setTotalVantagemBeneficiario(totaisRelatorioRH.getTotalVantagemBeneficiario().add(tipoValor.getValue()));
                                }
                                if (TipoEventoFP.DESCONTO.equals(tipoValor.getKey())) {
                                    totaisRelatorioRH.setTotalDescontoBeneficiario(totaisRelatorioRH.getTotalDescontoBeneficiario().add(tipoValor.getValue()));
                                }
                            }
                            if (Sets.newHashSet("603", "607", "622", "633", "659").contains(eventoTipo.getKey().getCodigo())) {
                                if (TipoEventoFP.DESCONTO.equals(tipoValor.getKey())) {
                                    totaisRelatorioRH.setTotalVantagemPensaoAlimenticia(totaisRelatorioRH.getTotalVantagemPensaoAlimenticia().add(tipoValor.getValue()));
                                }
                                if (TipoEventoFP.VANTAGEM.equals(tipoValor.getKey())) {
                                    totaisRelatorioRH.setTotalDescontoPensaoAlimenticia(totaisRelatorioRH.getTotalDescontoPensaoAlimenticia().add(tipoValor.getValue()));
                                }
                            }
                        }
                    }

                }

            }

        }
    }


    public class TotaisRelatorioRH {
        private BigDecimal totalVantagem;
        private BigDecimal totalDesconto;

        private BigDecimal totalVantagemPensionista;
        private BigDecimal totalDescontoPensionista;

        private BigDecimal totalVantagemPensaoAlimenticia;
        private BigDecimal totalDescontoPensaoAlimenticia;

        private BigDecimal totalVantagemAposentadoria;
        private BigDecimal totalDescontoAposentadoria;

        private BigDecimal totalVantagemBeneficiario;
        private BigDecimal totalDescontoBeneficiario;


        public TotaisRelatorioRH() {
            totalVantagem = BigDecimal.ZERO;
            totalDesconto = BigDecimal.ZERO;
            totalVantagemPensionista = BigDecimal.ZERO;
            totalDescontoPensionista = BigDecimal.ZERO;
            totalVantagemPensaoAlimenticia = BigDecimal.ZERO;
            totalDescontoPensaoAlimenticia = BigDecimal.ZERO;

            totalVantagemAposentadoria = BigDecimal.ZERO;
            totalDescontoAposentadoria = BigDecimal.ZERO;

            totalVantagemBeneficiario = BigDecimal.ZERO;
            totalDescontoBeneficiario = BigDecimal.ZERO;
        }

        public BigDecimal getTotalVantagem() {
            return totalVantagem;
        }

        public void setTotalVantagem(BigDecimal totalVantagem) {
            this.totalVantagem = totalVantagem;
        }

        public BigDecimal getTotalDesconto() {
            return totalDesconto;
        }

        public void setTotalDesconto(BigDecimal totalDesconto) {
            this.totalDesconto = totalDesconto;
        }

        public BigDecimal getTotalVantagemPensionista() {
            return totalVantagemPensionista;
        }

        public void setTotalVantagemPensionista(BigDecimal totalVantagemPensionista) {
            this.totalVantagemPensionista = totalVantagemPensionista;
        }

        public BigDecimal getTotalDescontoPensionista() {
            return totalDescontoPensionista;
        }

        public void setTotalDescontoPensionista(BigDecimal totalDescontoPensionista) {
            this.totalDescontoPensionista = totalDescontoPensionista;
        }

        public BigDecimal getTotalVantagemPensaoAlimenticia() {
            return totalVantagemPensaoAlimenticia;
        }

        public void setTotalVantagemPensaoAlimenticia(BigDecimal totalVantagemPensaoAlimenticia) {
            this.totalVantagemPensaoAlimenticia = totalVantagemPensaoAlimenticia;
        }

        public BigDecimal getTotalDescontoPensaoAlimenticia() {
            return totalDescontoPensaoAlimenticia;
        }

        public void setTotalDescontoPensaoAlimenticia(BigDecimal totalDescontoPensaoAlimenticia) {
            this.totalDescontoPensaoAlimenticia = totalDescontoPensaoAlimenticia;
        }

        public BigDecimal getTotalVantagemAposentadoria() {
            return totalVantagemAposentadoria;
        }

        public void setTotalVantagemAposentadoria(BigDecimal totalVantagemAposentadoria) {
            this.totalVantagemAposentadoria = totalVantagemAposentadoria;
        }

        public BigDecimal getTotalDescontoAposentadoria() {
            return totalDescontoAposentadoria;
        }

        public void setTotalDescontoAposentadoria(BigDecimal totalDescontoAposentadoria) {
            this.totalDescontoAposentadoria = totalDescontoAposentadoria;
        }

        public BigDecimal getTotalVantagemBeneficiario() {
            return totalVantagemBeneficiario;
        }

        public void setTotalVantagemBeneficiario(BigDecimal totalVantagemBeneficiario) {
            this.totalVantagemBeneficiario = totalVantagemBeneficiario;
        }

        public BigDecimal getTotalDescontoBeneficiario() {
            return totalDescontoBeneficiario;
        }

        public void setTotalDescontoBeneficiario(BigDecimal totalDescontoBeneficiario) {
            this.totalDescontoBeneficiario = totalDescontoBeneficiario;
        }
    }

}
