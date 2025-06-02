package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.administracaodepagamento.EventoFPTipoFolha;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoFP;
import br.com.webpublico.enums.rh.TipoCalculo;
import br.com.webpublico.interfaces.EntidadePagavelRH;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Future;

/**
 * @author mga
 */
public class DetalheProcessamentoFolha implements Serializable {

    //    public List<String> logDoProcesso = new ArrayList<>();
//    public static Integer porcentagem = 0;
//    public static boolean cancelar = false;
//    private TreeMap<String, Double> estatisticas;
    private Integer contadorGerados;
    private Integer contadorNaoGerados;
    private List<String> logDoProcesso = new ArrayList<String>();
    private Double totalCadastros;
    private boolean liberaCalculo = true;
    private StringBuilder logSucesso;
    private StringBuilder logs;
    private Integer contadorCalculoFolha;
    private Map<VinculoFP, String> erros;
    private Map<Integer, MotivoRejeicao> motivosRejeicao;
    private Integer quantidadeMesesRetroativos;
    private DetalhesCalculoRH detalhesCalculoRH;
    private Integer mes;
    private Integer ano;
    private Integer contadorTest = 0;
    private transient Map<String, List<BloqueioEventoFP>> bloqueioVerbaMap = new LinkedHashMap<>();
    private transient Map<String, List<BloqueioBeneficio>> bloqueioBeneficioMap = new LinkedHashMap<>();
    private transient Map<Long, Boolean> servidoresComFolhaEfetivada = new HashMap<>();
    private Map<ConfiguracaoEmpregadorESocial, List<HierarquiaOrganizacional>> empregadores;
    private Map<EntidadePagavelRH, String> vinculoOrgao;
    private transient Map<String, List<ItemBaseFP>> cacheItensBaseFP = new HashMap<>();
    private transient Map<String, List<EventoFPTipoFolha>> eventoFPTiposFolha = new HashMap<>();
    private List<EventoFP> eventosAutomaticos;
    private List<EventoFP> eventosPensaoAlimenticia;
    private List<EventoFP> eventosImpostoDeRenda;
    private List<EventoFP> eventosBloqueados;
    private List<EventoFP> eventosBloqueadosPorTela;

    private List<String> codigosBasesPensaoAlimenticia;
    private EventoFP eventoFPAdiantamento13Salario;

    private transient Future<DetalheProcessamentoFolha> tarefa1;
    private transient Future<DetalheProcessamentoFolha> tarefa2;
    private transient Future<DetalheProcessamentoFolha> tarefa3;
    private transient Future<DetalheProcessamentoFolha> tarefa4;
    private transient Future<DetalheProcessamentoFolha> tarefa5;
    private TipoCalculo tipoCalculo;
    private ItemCalendarioFP itemCalendarioFP;
    private boolean calculandoRetroativo = false;
    private ConfiguracaoFP configuracaoFP;

    public DetalheProcessamentoFolha() {
        liberaCalculo = true;
        contadorCalculoFolha = 0;
        contadorGerados = 0;
        contadorNaoGerados = 0;
        contadorTest = 0;
        totalCadastros = 0.0;
        erros = new HashMap<>();
        logSucesso = new StringBuilder("");
        logs = new StringBuilder("");
        quantidadeMesesRetroativos = 1;
        detalhesCalculoRH = new DetalhesCalculoRH();
        motivosRejeicao = new LinkedHashMap<>();
        servidoresComFolhaEfetivada = new HashMap<>();
        eventosPensaoAlimenticia = Lists.newArrayList();
        eventosImpostoDeRenda = Lists.newArrayList();
        eventosBloqueados = Lists.newArrayList();
        eventosBloqueadosPorTela = Lists.newArrayList();
        codigosBasesPensaoAlimenticia = Lists.newArrayList();
        eventosAutomaticos = Lists.newArrayList();

    }

    public ConfiguracaoFP getConfiguracaoFP() {
        return configuracaoFP;
    }

    public void setConfiguracaoFP(ConfiguracaoFP configuracaoFP) {
        this.configuracaoFP = configuracaoFP;
    }

    public List<EventoFP> getEventosPensaoAlimenticia() {
        return eventosPensaoAlimenticia;
    }

    public void setEventosPensaoAlimenticia(List<EventoFP> eventosPensaoAlimenticia) {
        this.eventosPensaoAlimenticia = eventosPensaoAlimenticia;
    }

    public List<EventoFP> getEventosImpostoDeRenda() {
        return eventosImpostoDeRenda;
    }

    public void setEventosImpostoDeRenda(List<EventoFP> eventosImpostoDeRenda) {
        this.eventosImpostoDeRenda = eventosImpostoDeRenda;
    }

    public Map<Long, Boolean> getServidoresComFolhaEfetivada() {
        return servidoresComFolhaEfetivada;
    }

    public void setServidoresComFolhaEfetivada(Map<Long, Boolean> servidoresComFolhaEfetivada) {
        this.servidoresComFolhaEfetivada = servidoresComFolhaEfetivada;
    }

    public StringBuilder getLogs() {
        return logs;
    }

    public void setLogs(StringBuilder logs) {
        this.logs = logs;
    }

    public Map<String, List<BloqueioBeneficio>> getBloqueioBeneficioMap() {
        return bloqueioBeneficioMap;
    }

    public void setBloqueioBeneficioMap(Map<String, List<BloqueioBeneficio>> bloqueioBeneficioMap) {
        this.bloqueioBeneficioMap = bloqueioBeneficioMap;
    }

    public ItemCalendarioFP getItemCalendarioFP() {
        return itemCalendarioFP;
    }

    public void setItemCalendarioFP(ItemCalendarioFP itemCalendarioFP) {
        this.itemCalendarioFP = itemCalendarioFP;
    }

    public TipoCalculo getTipoCalculo() {
        return tipoCalculo;
    }

    public void setTipoCalculo(TipoCalculo tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }

    public Integer getContadorTest() {
        return contadorTest;
    }

    public EventoFP getEventoFPAdiantamento13Salario() {
        return eventoFPAdiantamento13Salario;
    }

    public void setEventoFPAdiantamento13Salario(EventoFP eventoFPAdiantamento13Salario) {
        this.eventoFPAdiantamento13Salario = eventoFPAdiantamento13Salario;
    }

    public List<String> getCodigosBasesPensaoAlimenticia() {
        return codigosBasesPensaoAlimenticia;
    }

    public void setCodigosBasesPensaoAlimenticia(List<String> codigosBasesPensaoAlimenticia) {
        this.codigosBasesPensaoAlimenticia = codigosBasesPensaoAlimenticia;
    }

    public Map<String, List<ItemBaseFP>> getCacheItensBaseFP() {
        return cacheItensBaseFP;
    }

    public void setCacheItensBaseFP(Map<String, List<ItemBaseFP>> cacheItensBaseFP) {
        this.cacheItensBaseFP = cacheItensBaseFP;
    }

    public synchronized void addItensBase(String codigo, List<ItemBaseFP> itens) {
        if (!cacheItensBaseFP.containsKey(codigo)) {
            cacheItensBaseFP.put(codigo, itens);
        }
    }

    public List<EventoFP> getEventosAutomaticos() {
        return eventosAutomaticos;
    }

    public void setEventosAutomaticos(List<EventoFP> eventosAutomaticos) {
        this.eventosAutomaticos = eventosAutomaticos;
    }

    public Map<String, List<EventoFPTipoFolha>> getEventoFPTiposFolha() {
        return eventoFPTiposFolha;
    }

    public void setEventoFPTiposFolha(Map<String, List<EventoFPTipoFolha>> eventoFPTiposFolha) {
        this.eventoFPTiposFolha = eventoFPTiposFolha;
    }

    public synchronized void setContadorTest(Integer contadorTest) {
        this.contadorTest = contadorTest;
    }

    public synchronized void countContadorTest() {
        this.contadorTest++;
    }

    public boolean isDone() {
        return ((getTarefa1() != null ? getTarefa1().isDone() : true)
            && (getTarefa2() != null ? getTarefa2().isDone() : true)
            && (getTarefa3() != null ? getTarefa3().isDone() : true)
            && (getTarefa4() != null ? getTarefa4().isDone() : true)
            && (getTarefa5() != null ? getTarefa5().isDone() : true));
    }

    public void cancelTasks() {
        if (getTarefa1() != null) getTarefa1().cancel(true);
        if (getTarefa2() != null) getTarefa2().cancel(true);
        if (getTarefa3() != null) getTarefa3().cancel(true);
        if (getTarefa4() != null) getTarefa4().cancel(true);
        if (getTarefa5() != null) getTarefa5().cancel(true);
    }

    public Map<Integer, MotivoRejeicao> getMotivosRejeicao() {
        return motivosRejeicao;
    }

    public void setMotivosRejeicao(Map<Integer, MotivoRejeicao> motivosRejeicao) {
        this.motivosRejeicao = motivosRejeicao;
    }


    public Map<String, List<BloqueioEventoFP>> getBloqueioVerbaMap() {
        return bloqueioVerbaMap;
    }

    public void setBloqueioVerbaMap(Map<String, List<BloqueioEventoFP>> bloqueioVerbaMap) {
        this.bloqueioVerbaMap = bloqueioVerbaMap;
    }

    public Future<DetalheProcessamentoFolha> getTarefa1() {
        return tarefa1;
    }

    public void setTarefa1(Future<DetalheProcessamentoFolha> tarefa1) {
        this.tarefa1 = tarefa1;
    }

    public Future<DetalheProcessamentoFolha> getTarefa2() {
        return tarefa2;
    }

    public void setTarefa2(Future<DetalheProcessamentoFolha> tarefa2) {
        this.tarefa2 = tarefa2;
    }

    public Future<DetalheProcessamentoFolha> getTarefa3() {
        return tarefa3;
    }

    public void setTarefa3(Future<DetalheProcessamentoFolha> tarefa3) {
        this.tarefa3 = tarefa3;
    }

    public Future<DetalheProcessamentoFolha> getTarefa4() {
        return tarefa4;
    }

    public void setTarefa4(Future<DetalheProcessamentoFolha> tarefa4) {
        this.tarefa4 = tarefa4;
    }

    public Future<DetalheProcessamentoFolha> getTarefa5() {
        return tarefa5;
    }

    public void setTarefa5(Future<DetalheProcessamentoFolha> tarefa5) {
        this.tarefa5 = tarefa5;
    }

    public Integer getMes() {
        return mes;
    }

    public synchronized void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public synchronized void setAno(Integer ano) {
        this.ano = ano;
    }

    public Double getPorcentagemDoCalculo() {
        if (contadorCalculoFolha == null || totalCadastros == null) {
            return 0d;
        }
        return (contadorCalculoFolha.doubleValue() / totalCadastros) * 100;
    }

    public Integer getContadorGerados() {
        return contadorGerados;
    }

    public synchronized void contaContadorGerado() {
        contadorGerados++;
    }

    public synchronized void setContadorGerados(Integer contadorGerados) {
        this.contadorGerados = contadorGerados;
    }

    public Integer getContadorNaoGerados() {
        return contadorNaoGerados;
    }

    public synchronized void setContadorNaoGerados(Integer contadorNaoGerados) {
        this.contadorNaoGerados = contadorNaoGerados;
    }

    public List<String> getLogDoProcesso() {
        return logDoProcesso;
    }

    public void setLogDoProcesso(List<String> logDoProcesso) {
        this.logDoProcesso = logDoProcesso;
    }

    public Double getTotalCadastros() {
        return totalCadastros;
    }

    public void setTotalCadastros(Double totalCadastros) {
        this.totalCadastros = totalCadastros;
    }

    public synchronized boolean isLiberaCalculo() {
        return liberaCalculo;
    }

    public void setLiberaCalculo(boolean liberaCalculo) {
        this.liberaCalculo = liberaCalculo;
    }

    public StringBuilder getLogSucesso() {
        return logSucesso;
    }

    public void setLogSucesso(StringBuilder logSucesso) {
        this.logSucesso = logSucesso;
    }

    public Integer getContadorCalculoFolha() {
        return contadorCalculoFolha;
    }

    public synchronized void setContadorCalculoFolha(Integer contadorCalculoFolha) {
        this.contadorCalculoFolha = contadorCalculoFolha;
    }

    public List<EventoFP> getEventosBloqueados() {
        return eventosBloqueados;
    }

    public void setEventosBloqueados(List<EventoFP> eventosBloqueados) {
        this.eventosBloqueados = eventosBloqueados;
    }

    public List<EventoFP> getEventosBloqueadosPorTela() {
        return eventosBloqueadosPorTela;
    }

    public void setEventosBloqueadosPorTela(List<EventoFP> eventosBloqueadosPorTela) {
        this.eventosBloqueadosPorTela = eventosBloqueadosPorTela;
    }

    public synchronized void contaCalculo() {
        contadorCalculoFolha++;
    }

    public synchronized void finalizaCalculo() {
        liberaCalculo = true;
        logSucesso = new StringBuilder();
        gerarMessagens();

    }

    synchronized boolean podeCalcular() {
        return liberaCalculo;
    }

    public synchronized void bloqueiaCalculo() {
        liberaCalculo = false;
    }

    public Map<VinculoFP, String> getErros() {
        return erros;
    }

    public void setErros(Map<VinculoFP, String> erros) {
        this.erros = erros;
    }

    public DetalhesCalculoRH getDetalhesCalculoRH() {
        return detalhesCalculoRH;
    }

    public void setDetalhesCalculoRH(DetalhesCalculoRH detalhesCalculoRH) {
        this.detalhesCalculoRH = detalhesCalculoRH;
    }

    public synchronized void limpa() {
        contadorCalculoFolha = 0;
        contadorGerados = 0;
        contadorNaoGerados = 0;
        totalCadastros = 0.0;
        calculandoRetroativo = false;

    }

    public boolean isCalculandoRetroativo() {
        return calculandoRetroativo;
    }

    public void setCalculandoRetroativo(boolean calculandoRetroativo) {
        this.calculandoRetroativo = calculandoRetroativo;
    }

    public Integer getQuantidadeMesesRetroativos() {
        return quantidadeMesesRetroativos;
    }

    public void setQuantidadeMesesRetroativos(Integer quantidadeMesesRetroativos) {
        this.quantidadeMesesRetroativos = quantidadeMesesRetroativos;
    }

    public Map<ConfiguracaoEmpregadorESocial, List<HierarquiaOrganizacional>> getEmpregadores() {
        return empregadores;
    }

    public void setEmpregadores(Map<ConfiguracaoEmpregadorESocial, List<HierarquiaOrganizacional>> empregadores) {
        this.empregadores = empregadores;
    }

    public Map<EntidadePagavelRH, String> getVinculoOrgao() {
        return vinculoOrgao;
    }

    public void setVinculoOrgao(Map<EntidadePagavelRH, String> vinculoOrgao) {
        this.vinculoOrgao = vinculoOrgao;
    }

    public void gerarMessagens() {
        logSucesso.append(" <div style=\"text-align: left; font-size: small\">");
        logSucesso.append("<h2> LOG </h2>");
        logSucesso.append("Gerados: ").append(contadorCalculoFolha).append("<br/>");
        logSucesso.append("Não Gerados: ").append(totalCadastros - contadorCalculoFolha).append("<br/>");
//        for (String s : getOcorrencias()) {
//            logSucesso.append(s).append("; <br/>");
//        }
        logSucesso.append("<br/>");
        if (!erros.keySet().isEmpty()) {
            logSucesso.append("<h2> Informações/Erros: </h2>");
        }
        //aqui vai os logs de erro!
        for (VinculoFP vinculo : erros.keySet()) {
            logSucesso.append("<b>").append(vinculo).append(": </b> <br/>");
//            if (calculo.getConstrucao() != null) {
//                logSucesso.append("<b><i>").append(calculo.getConstrucao()).append("</i>: </b> <br/>");
//            }
//            for (String mensagem : getErros().get(vinculo)) {
            logSucesso.append(" ").append(erros.get(vinculo)).append("<br/>");
//            }
        }


        for (ItensDetalhesErrosCalculo item : getDetalhesCalculoRH().getItensDetalhesErrosCalculos()) {
            logSucesso.append("<br>").append(item.getErros()).append("</br>");
        }


        logSucesso.append("<br/>");
//        logSucesso.append("<h2> Estatisticas </h2><br/>");
//        for (String string : getEstatisticas().keySet()) {
//            logSucesso.append(string).append(" ").append(getEstatisticas().get(string)).append("; <br/>");
//        }
        logSucesso.append(logs);
        logSucesso.append("</div>");

    }

    public synchronized DateTime getDataReferencia() {
        DateTime ultimoDiaDoMesDeCalculo = new DateTime();
        ultimoDiaDoMesDeCalculo = ultimoDiaDoMesDeCalculo.withHourOfDay(0);
        ultimoDiaDoMesDeCalculo = ultimoDiaDoMesDeCalculo.withMillisOfSecond(0);
        ultimoDiaDoMesDeCalculo = ultimoDiaDoMesDeCalculo.withMinuteOfHour(0);
        ultimoDiaDoMesDeCalculo = ultimoDiaDoMesDeCalculo.withSecondOfMinute(0);
        ultimoDiaDoMesDeCalculo = ultimoDiaDoMesDeCalculo.withMonthOfYear(mes);
        ultimoDiaDoMesDeCalculo = ultimoDiaDoMesDeCalculo.withYear(ano);
        ultimoDiaDoMesDeCalculo = ultimoDiaDoMesDeCalculo.withDayOfMonth(ultimoDiaDoMesDeCalculo.dayOfMonth().getMaximumValue());
        return ultimoDiaDoMesDeCalculo;
    }

}
