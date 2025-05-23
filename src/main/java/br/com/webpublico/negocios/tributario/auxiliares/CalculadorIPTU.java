package br.com.webpublico.negocios.tributario.auxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.NivelOcorrencia;
import br.com.webpublico.enums.TipoOcorrencia;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.negocios.tributario.dao.JdbcCalculoIptuDAO;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.SessionContext;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CalculadorIPTU {

    private static final Logger logger = LoggerFactory.getLogger(CalculadorIPTU.class);

    private final String CONSTRUCAO = "construcao", CADASTRO = "cadastro", LOTE = "lote",
        AREA_CONSTRUIDA_LOTE = "areaConstruidaLote";
    private final String CALCULADOR = "calculador", DAO = "jdbcCalculoIptuDAO", JS = "javascript";
    private final List<CalculoIPTU> calculados;
    private transient final ApplicationContext ap;
    private final JdbcCalculoIptuDAO calculoDAO;
    private final ScriptEngine engine;
    private final List<ServicoUrbano> servicos;
    private final Map<EventoCalculo, EventoCalculo> descontos;
    private final Map<EventoCalculo, Double> cacheEventoValor;
    private final Map<String, EventoCalculo> cacheEventos;
    private ProcessoCalculoIPTU processo;
    private Exercicio exercicio;
    private ConfiguracaoTributario configuracaoTributario;
    private List<Pontuacao> pontuacoes;
    private Double ufmrb;
    public CadastroImobiliario cadastro;
    public Construcao construcao;
    public Lote lote;
    private List<AtributoGenerico> atributos;
    private int contadorDeCalculos;
    private long subDebito;
    private CalculoIPTU calculo;
    private AssistenteCalculadorIPTU assistente;


    public CalculadorIPTU() {
        this.ap = ContextLoader.getCurrentWebApplicationContext();
        this.calculoDAO = (JdbcCalculoIptuDAO) this.ap.getBean(DAO);
        this.engine = new ScriptEngineManager().getEngineByName(JS);
        this.calculados = Lists.newLinkedList();
        this.descontos = new HashMap<>();
        this.atributos = Lists.newLinkedList();
        this.contadorDeCalculos = 0;
        this.servicos = Lists.newLinkedList(this.calculoDAO.todosServicos());
        cacheEventoValor = new HashMap<>();
        cacheEventos = new HashMap<>();
    }

    public static List<String> getMetodosDisponiveis() {
        List<String> metodos = Lists.newArrayList();
        for (Method m : CalculadorIPTU.class.getDeclaredMethods()) {
            metodos.add("calculador." + m.getName());
        }
        for (Field f : CadastroImobiliario.class.getDeclaredFields()) {
            metodos.add("cadastro." + f.getName());
        }
        for (Field f : Construcao.class.getDeclaredFields()) {
            metodos.add("construcao." + f.getName());
        }
        for (Field f : Lote.class.getDeclaredFields()) {
            metodos.add("lote." + f.getName());
        }
        return metodos;
    }

    public List<CalculoIPTU> getCalculados() {
        return calculados;
    }

    private void descobreSeparaDescontos() {
        for (EventoConfiguradoIPTU evento : processo.getConfiguracaoEventoIPTU().getEventos()) {
            if (evento.getEventoCalculo().getTipoLancamento().equals(EventoCalculo.TipoLancamento.DESCONTO)) {
                descontos.put(evento.getEventoCalculo().getDescontoSobre(), evento.getEventoCalculo());
            }
        }
    }

    public void calcularIPTU(List<CadastroImobiliario> cadastros, AssistenteCalculadorIPTU assistente, SessionContext sctx) {
        this.assistente = assistente;
        this.processo = assistente.getProcessoCalculoIPTU();
        exercicio = processo.getExercicio();
        this.pontuacoes = Lists.newLinkedList(this.calculoDAO.todasPontuacoes(exercicio.getAno()));
        this.ufmrb = calculoDAO.ufmVigente(exercicio.getAno());
        descobreSeparaDescontos();
        for (CadastroImobiliario cadastro : cadastros) {
            if (sctx != null && sctx.wasCancelCalled()) {
                break;
            }
            try {
                this.subDebito = calculoDAO.quantidadeCalculosEfetivados(cadastro.getId(), exercicio.getAno());
                this.subDebito = subDebito + 1;
                this.cadastro = cadastro;
                calcularIPTU();
                contarCalculo();
            } finally {
                assistente.conta();
            }
        }
        persiste();
    }

    public void atualizarValoresBCI(CadastroImobiliarioFacade.AtualizadorBCI atualizadorBCI) {
        exercicio = atualizadorBCI.getExercicio();
        this.configuracaoTributario = atualizadorBCI.getConfiguracaoTributario();
        this.pontuacoes = Lists.newLinkedList(this.calculoDAO.todasPontuacoes(atualizadorBCI.getExercicio().getAno()));
        this.ufmrb = calculoDAO.ufmVigente(atualizadorBCI.getExercicio().getAno());
        List<CadastroImobiliario> atualizados = Lists.newArrayList();
        for (CadastroImobiliario cadastro : atualizadorBCI.getCadastros()) {
            if (!atualizadorBCI.isProcessando()) {
                atualizadorBCI.setAtualizando(false);
                break;
            }
            try {
                this.cadastro = cadastro;
                atualizaBCI(cadastro);
                atualizados.add(cadastro);
                if (atualizados.size() == 50 || atualizadorBCI.getCadastros().size() == 1) {
                    calculoDAO.persisteEventosDoBci(atualizados);
                    atualizados.clear();
                }
            } catch (Exception e) {
                logger.error("{}", e.getMessage());
            } finally {
                atualizadorBCI.contaLinha();
            }
        }
        if (!atualizados.isEmpty()) {
            calculoDAO.persisteEventosDoBci(atualizados);
        }
        atualizadorBCI.finaliza();
    }

    private void contarCalculo() {
        contadorDeCalculos++;
        if (contadorDeCalculos >= 100) {
            persiste();
            contadorDeCalculos = 0;
        }
    }

    private void persiste() {
        if (!calculados.isEmpty() && assistente.isPersisteCalculo()) {
            calculoDAO.persisteTudo(calculados);
            calculados.clear();
        }
    }

    private void atualizaBCI(CadastroImobiliario cadastro) {
        if (cadastro.getLote() != null && cadastro.getLote().getId() != null) {
            recuperaLote();
            for (Construcao cons : recuperaConstrucaoPorCadastro()) {
                cadastro.getEventosCalculoBCI().clear();
                this.construcao = cons;
                cacheEventoValor.clear();
                adicionarAtributos();
                criarContexto();
                for (EventoConfiguradoBCI eventoConfiguradoBCI : configuracaoTributario.getEventosBCI()) {
                    if (eventoConfiguradoBCI.getIncidencia().equals(EventoConfiguradoBCI.Incidencia.CONSTRUCAO) && !cons.isDummy()) {
                        processoEventoAtualizacaoConstrucao(eventoConfiguradoBCI);
                    }
                }
                for (EventoConfiguradoBCI eventoConfiguradoBCI : configuracaoTributario.getEventosBCI()) {
                    if (eventoConfiguradoBCI.getIncidencia().equals(EventoConfiguradoBCI.Incidencia.CADASTRO)) {
                        processoEventoAtualizacaoBCI(eventoConfiguradoBCI);
                    }
                }


            }
        }
    }

    private void processoEventoAtualizacaoBCI(EventoConfiguradoBCI eventoConfiguradoBCI) {
        EventoCalculoBCI eventoCalculoBCI = new EventoCalculoBCI();
        eventoCalculoBCI.setCadastroImobiliario(cadastro);
        eventoCalculoBCI.setEventoCalculo(eventoConfiguradoBCI);
        try {
            Double valor = executaFormula(eventoConfiguradoBCI.getEventoCalculo());
            eventoCalculoBCI.setValor(valor != null ? BigDecimal.valueOf(valor) : BigDecimal.ZERO);
        } catch (Exception e) {
            logger.error("Exception : {}", e.getMessage());
            eventoCalculoBCI.setValor(BigDecimal.ZERO);
        }
        cadastro.getEventosCalculoBCI().add(eventoCalculoBCI);
    }

    private void processoEventoAtualizacaoConstrucao(EventoConfiguradoBCI eventoConfiguradoBCI) {
        EventoCalculoConstrucao eventoCalculoConstrucao = new EventoCalculoConstrucao();
        eventoCalculoConstrucao.setConstrucao(construcao);
        eventoCalculoConstrucao.setEventoCalculo(eventoConfiguradoBCI);
        try {
            eventoCalculoConstrucao.setValor(BigDecimal.valueOf(executaFormula(eventoConfiguradoBCI.getEventoCalculo())));
        } catch (Exception e) {
            e.printStackTrace();
            eventoCalculoConstrucao.setValor(BigDecimal.ZERO);
        }
        cadastro.getEventosCalculoConstrucoes().add(eventoCalculoConstrucao);
    }

    private void calcularIPTU() {
        if (cadastro.getLote() != null && cadastro.getLote().getId() != null) {
            recuperaLote();

            calculo = new CalculoIPTU();
            calculo.setProcessoCalculoIPTU(processo);
            calculo.setSubDivida(subDebito);
            recuperarIsencao();

            for (Construcao construcao : recuperaConstrucaoPorCadastro()) {
                this.construcao = construcao;

                adicionarAtributos();
                criarContexto();
                processarCalculo();
                gerarCarne();
            }
            calculados.add(calculo);
            processo.getCalculosIPTU().add(calculo);
        }
    }

    private void recuperarIsencao() {
        calculo.setIsencaoCadastroImobiliario(calculoDAO.isencaoPorCadastroExercicio(cadastro.getId(), exercicio));
    }

    private void adicionarAtributos() {
        atributos.clear();
        atributos.addAll(calculoDAO.atributosConstrucao(construcao.getId()));
        atributos.addAll(calculoDAO.atributosLote(lote.getId()));
        atributos.addAll(calculoDAO.atributosImovel(cadastro.getId()));
    }

    private void criarContexto() {
        engine.getBindings(ScriptContext.ENGINE_SCOPE).clear();
        engine.put(CALCULADOR, this);
        engine.put(CADASTRO, cadastro);
        engine.put(LOTE, lote);
        engine.put(CONSTRUCAO, construcao);
        for (AtributoGenerico atr : atributos) {
            engine.put(atr.getIdentificacao(), atr.getValor());
        }
        for (Pontuacao p : pontuacoes) {
            engine.put(p.getIdentificacao(), p);
        }
        for (ServicoUrbano s : servicos) {
            engine.put(s.getIdentificacao(), s);
        }
    }

    public boolean atributoAtivo(String identificacao) {
        for (AtributoGenerico atr : atributos) {
            if (atr.getIdentificacao().equals(identificacao) && atr.isAtivo()) {
                return true;
            }
        }
        return false;
    }

    private void processarCalculo() {
        cacheEventoValor.clear();
        try {
            calculo.setCadastroImobiliario(cadastro);
            for (EventoConfiguradoIPTU evento : processo.getConfiguracaoEventoIPTU().getEventos()) {
                processarEvento(evento);
            }
        } catch (Exception e) {
            geraOcorrencia("Erro ao processar o calculo", e.getMessage());
            logger.trace("", e);
        }
    }

    private void processarEvento(EventoConfiguradoIPTU evento) {
        Double resultado = 0.0;
        BigDecimal percentualIsencao = buscarPercentualIsencao(evento);
        resultado = executaFormula(evento.getEventoCalculo());
        criarItemCalculo(evento, resultado, percentualIsencao);
    }

    private void criarItemCalculo(EventoConfiguradoIPTU evento, Double resultado, BigDecimal percentualIsencao) {
        resultado = resultado != null ? resultado : 0;
        ItemCalculoIPTU item = new ItemCalculoIPTU();
        item.setIsento(false);
        if (calculo.getIsencaoCadastroImobiliario() != null) {
            if (evento.getEventoCalculo().getTipoLancamento().equals(EventoCalculo.TipoLancamento.IMPOSTO)
                && !calculo.getIsencaoCadastroImobiliario().getLancaImposto()) {
                item.setIsento(percentualIsencao.compareTo(BigDecimal.valueOf(100)) == 0);
            }
            if (evento.getEventoCalculo().getTipoLancamento().equals(EventoCalculo.TipoLancamento.TAXA)
                && !calculo.getIsencaoCadastroImobiliario().getLancaTaxa()) {
                item.setIsento(percentualIsencao.compareTo(BigDecimal.valueOf(100)) == 0);
            }
        }
        if (evento.getEventoCalculo().getEventoImunidade() != null) {
            Double imune = executaFormula(evento.getEventoCalculo().getEventoImunidade());
            item.setImune(imune != null && imune.intValue() == 1);
            if (item.getImune()) {
                item.setIsento(true);
            }
        }
        item.setCalculoIPTU(calculo);
        item.setConstrucao(construcao);
        if (item.getIsento() || item.getImune()) {
            item.setValorReal(BigDecimal.valueOf(resultado));
        } else {
            BigDecimal valorIsento = BigDecimal.valueOf(resultado).multiply(percentualIsencao).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            item.setValorReal(BigDecimal.valueOf(resultado).subtract(valorIsento));
        }
        item.setValorEfetivo(item.getValorReal());
        item.setEvento(evento);
        if (evento.getEventoCalculo().getTipoLancamento().getLancaValor()) {
            if (!item.getIsento() && !item.getImune()) {
                calculo.setValorEfetivo(calculo.getValorEfetivo().add(item.getValorReal()));
            }
            calculo.setValorReal(calculo.getValorReal().add(item.getValorReal()));
        }
        calculo.getItensCalculo().add(item);
    }

    private BigDecimal buscarPercentualIsencao(EventoConfiguradoIPTU evento) {
        BigDecimal percentual = BigDecimal.ZERO;
        if (calculo.getIsencaoCadastroImobiliario() != null) {
            if (evento.getEventoCalculo().getTipoLancamento().equals(EventoCalculo.TipoLancamento.IMPOSTO) && !calculo.getIsencaoCadastroImobiliario().getLancaImposto()) {
                percentual = calculo.getIsencaoCadastroImobiliario().getPercentual();
            }
            if (evento.getEventoCalculo().getTipoLancamento().equals(EventoCalculo.TipoLancamento.TAXA) && !calculo.getIsencaoCadastroImobiliario().getLancaTaxa()) {
                percentual = calculo.getIsencaoCadastroImobiliario().getPercentual();
            }
        }
        return percentual;
    }

    private Double executaFormula(EventoCalculo evento) {
        try {
            if (evento != null && !cacheEventoValor.containsKey(evento)) {
                String formula = "function calculoiptu(){ return " + evento.getRegra() + ";} \n\n calculoiptu();";
                Double resultado = (Double) engine.eval(formula);
                Double retorno = aplicaDesconto(resultado, evento);
                cacheEventoValor.put(evento, retorno);
                criaMemorias(evento, retorno);
            }
        } catch (ScriptException se) {
            logger.error("ScriptException {}", se.getMessage());
        } catch (Exception ex) {
            logger.error("Erro ao executar a fórmula do evento de cálculo [{}]. {}", evento.getDescricao(), ex.getMessage());
            logger.debug("Detalhes do erro ao executar a fórmula do evento de cálculo [{}].", evento.getDescricao(), ex);
        }
        return cacheEventoValor.get(evento);
    }

    public Double aplicaDesconto(Double valor, EventoCalculo evento) throws ScriptException {
        if (descontos.containsKey(evento)) {
            return valor - (valor * executaFormula(descontos.get(evento)));
        } else {
            return valor;
        }
    }

    public void criaMemorias(EventoCalculo evento, Double valor) {
        if (processo != null) {
            MemoriaCaluloIPTU memoria = new MemoriaCaluloIPTU();
            memoria.setCalculoIPTU(calculo);
            memoria.setEvento(evento);
            memoria.setValor(valor != null ? BigDecimal.valueOf(valor) : BigDecimal.ZERO);
            memoria.setConstrucao(construcao);
            calculo.getMemorias().add(memoria);
        }
    }

    public double executaEvento(String identificacao, String key, Object valor) throws ScriptException {
        Object antigo = engine.get(key);
        engine.put(key, valor);
        cacheEventoValor.clear();
        double retorno = executaEvento(identificacao);
        engine.put(key, antigo);
        return retorno;
    }

    public double executaEvento(String identificacao) throws ScriptException {
        try {
            if (cacheEventos.get(identificacao) == null) {
                cacheEventos.put(identificacao, calculoDAO.eventoPorIdentificacao(identificacao));
            }
            return executaFormula(cacheEventos.get(identificacao));
        } catch (Exception e) {
            return 0.0;
        }
    }

    public Object getConxto(String key) {
        return engine.get(key);
    }

    private void recuperaLote() {
        try {
            lote = calculoDAO.lotePorId(cadastro.getLote().getId());
        } catch (Exception ex) {
            logger.error("Erro ao buscar o lote: ", ex);
        }
    }

    public List<Construcao> recuperaConstrucaoPorCadastro() {
        List<Construcao> retorno = calculoDAO.construcaoPorIdCadastro(cadastro.getId());
        if (retorno.isEmpty()) {
            retorno.add(criaConstrucaoDummy());
        }
        return retorno;
    }

    private Construcao criaConstrucaoDummy() {
        return Construcao.getConstrucaoDummy(cadastro);
    }

    public double pontuacaoConstrucao(Pontuacao pontuacao) {
        if (construcao.isDummy()) {
            return 0.0;
        }
        return calculoDAO.pontos(pontuacao, construcao);
    }

    public double pontuacaoLote(Pontuacao pontuacao) {
        return calculoDAO.pontos(pontuacao, lote);
    }

    public double pontuacaoCadastro(Pontuacao pontuacao) {
        return calculoDAO.pontos(pontuacao, cadastro);
    }

    public double arredondaValor(Double valor) {
        return Math.round(valor * 100) / 100.0;
    }

    public double areaConstruidaDoLote() {
        if (construcao.isDummy()) {
            return cadastro.getAreaTotalConstruida() != null ? cadastro.getAreaTotalConstruida().doubleValue() : 0.0;
        }
        if (engine.get(AREA_CONSTRUIDA_LOTE) == null) {
            engine.put(AREA_CONSTRUIDA_LOTE, calculoDAO.areaConstruidaLote(Integer.parseInt(lote.getCodigoLote()),
                Integer.parseInt(lote.getCodigoQuadra()), Integer.parseInt(lote.getCodigoSetor())));
        }
        return (double) engine.get(AREA_CONSTRUIDA_LOTE);
    }

    public double areaLoteParaImpostoPredial() {
        Double areaLotePredial = areaConstruidaDoLote() * 20;
        if (areaLotePredial > lote.getAreaLote()) {
            return lote.getAreaLote();
        } else {
            return areaLotePredial;
        }
    }

    public double areaLoteParaImpostoTerritorial() {
        Double areaLotePredial = areaLoteParaImpostoPredial();
        if (areaLotePredial > lote.getAreaLote()) {
            return 0.0;
        } else {
            return lote.getAreaLote() - areaLotePredial;
        }
    }

//    public double fracaoIdeal() {
//        if (construcao.isDummy()) {
//            return lote.getAreaLote();
//        }
//        Double retorno;
//        retorno = fracaoIdealPorPorcentagem(areaConstruidaDoLote());
//        double areaLoteParaImpostoPredial = areaLoteParaImpostoPredial();
//        retorno = retorno * areaLoteParaImpostoPredial;
//        return retorno;
//    }

    public double fracaoIdeal() {
        if (construcao.isDummy()) {
            return lote.getAreaLote();
        }
        Double retorno;
        if (engine.get("tipo_imovel").equals("1")) {
            retorno = fracaoIdealParaApartamento();
        } else {
            retorno = fracaoIdealPorPorcentagem();
        }
        return retorno * areaLoteParaImpostoPredial();

    }

    public double fracaoIdealParaApartamento() {
        try {
            BigDecimal retorno;
            BigDecimal areaTotalConstruidaNoLote = cadastro.getAreaTotalConstruida();
            BigDecimal areaLote = BigDecimal.valueOf(lote.getAreaLote());
            BigDecimal fracaoIdeal = areaLote.multiply(BigDecimal.valueOf(construcao.getAreaConstruida()).divide(areaTotalConstruidaNoLote, 4, BigDecimal.ROUND_HALF_EVEN));
            fracaoIdeal = (fracaoIdeal.divide(areaLote));
            retorno = fracaoIdeal;
            return retorno.doubleValue();
        } catch (Exception e) {
            logger.debug("", e);
            return 0D;
        }
    }

    public double fracaoIdealPorPorcentagem() {
        Double retorno;
        Double areaTotalConstruidaNoLote = areaConstruidaDoLote();
        Double areaLote = lote.getAreaLote();
        Double fracaoIdeal = (areaLote * construcao.getAreaConstruida()) / areaTotalConstruidaNoLote;
        fracaoIdeal = (fracaoIdeal / areaLote);
        retorno = fracaoIdeal;
        return retorno;
    }

    public double fracaoIdealExcedente() {
        if (construcao.isDummy()) {
            return 0.0;
        }
        if ((areaConstruidaDoLote() * 20) < lote.getAreaLote() && !engine.get("tipo_imovel").equals("1")) {
            return construcao.getAreaConstruida() / (areaConstruidaDoLote() * 20) * areaLoteParaImpostoPredial();
        }
        return 0.0;
    }

    public double valorM2Construido() {
        double retorno = pontuacaoConstrucao((Pontuacao) engine.get("pontuacao_tipo_imovel"));
        return retorno;
    }

    public double fatorQualidadeConstrucao() {
        double retorno = pontuacaoConstrucao((Pontuacao) engine.get("pontuacao_qualidade_construcao"));
        return retorno;
    }

    public double fatorCorrecaoTerreno() {
        return pontuacaoLote((Pontuacao) getConxto("pontuacao_topografia"))
            * pontuacaoLote((Pontuacao) getConxto("pontuacao_pedologia"))
            * pontuacaoLote((Pontuacao) getConxto("pontuacao_situacao"));
    }

    public double ufmrb() {
        return ufmrb;
    }

    public double valorM2Terreno() {
        return calculoDAO.valorFaceDoLote(lote, exercicio);
    }

    public boolean isResidencial() {
        return !construcao.isDummy() && engine.get("utilizacao_imovel").equals("1");
    }

    public double valorVenal() {
        return arredondaValor(
            fracaoIdeal()
                * valorM2Terreno()
                * ufmrb()
                * fatorCorrecaoTerreno() * 1.0);
    }

    public boolean contemServico(ServicoUrbano servicoUrbano) {
        return calculoDAO.faceContemServico(lote, servicoUrbano);
    }

    private void geraOcorrencia(String mensagem, String detalhesTecnicos) {
        OcorrenciaCalculoIPTU ocorrenciaCalculoIPTU = new OcorrenciaCalculoIPTU();
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.addMessagem(TipoOcorrencia.CALCULO, NivelOcorrencia.ERRO, mensagem, detalhesTecnicos);
        ocorrenciaCalculoIPTU.setOcorrencia(ocorrencia);
        ocorrenciaCalculoIPTU.setCadastroImobiliario(calculo.getCadastroImobiliario());
        if (!construcao.isDummy()) {
            ocorrenciaCalculoIPTU.setConstrucao(calculo.getConstrucao());
        }
        ocorrenciaCalculoIPTU.setCalculoIptu(calculo);
        calculo.getOcorrenciaCalculoIPTUs().add(ocorrenciaCalculoIPTU);
    }

    public void gerarCarne() {
        try {
            CarneIPTU carne = new CarneIPTU();
            carne.setCalculo(calculo);
            calculo.getCarnes().add(carne);
            carne.setFracaoIdeal(BigDecimal.valueOf(executaEvento("fracaoIdeal")));
            carne.setAreaConstruida(BigDecimal.valueOf(construcao.getAreaConstruida()));
            carne.setVlrM2Terreno(BigDecimal.valueOf(executaEvento("valorM2Terreno")));
            carne.setVlrM2Construido(BigDecimal.valueOf((executaEvento("valorM2Construcao"))));
            carne.setUfmrb(BigDecimal.valueOf((ufmrb)));
            carne.setFatorCorrecao(BigDecimal.valueOf((executaEvento("fatorCorrecao"))));
            carne.setFatorPeso(BigDecimal.valueOf((executaEvento("fatorQualidadeConstrucao"))));
            carne.setVlrVenalTerreno(BigDecimal.valueOf((executaEvento("valorVenalTerreno"))));
            carne.setVlrVenalEdificacao(BigDecimal.valueOf((executaEvento("valorVenalConstrucao"))));
            carne.setVlrVenalExcedente(BigDecimal.valueOf((executaEvento("valorVenalExcedente"))));
            carne.setAreaExcedente(BigDecimal.valueOf((executaEvento("areaExcedente"))));
            carne.setVlrM2Excedente(BigDecimal.valueOf(executaEvento("valorM2Terreno")));
            carne.setAliquota(BigDecimal.valueOf(executaEvento("aliquota")).multiply(BigDecimal.valueOf(100)));
            carne.setConstrucao(construcao);
        } catch (Exception e) {
            logger.trace("", e);
        }
    }

    public void imprime(Object o) {
        logger.debug("JS -> " + o);
    }

}
