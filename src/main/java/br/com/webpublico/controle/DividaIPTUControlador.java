package br.com.webpublico.controle;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ProcessoCalculoIPTU;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.auxiliares.AssistenteEfetivacaoIPTU;
import br.com.webpublico.util.ConverterExercicio;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@ManagedBean(name = "dividaIPTUControlador")
@SessionScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaEfetivacaoIPTU", pattern = "/iptu/efetivacao/",
        viewId = "/faces/tributario/efetivacaocalculo/edita.xhtml"),
    @URLMapping(id = "novaEfetivacaoIPTUviaCalculo", pattern = "/iptu/efetivacao/#{dividaIPTUControlador.idProcessoIPTU}/",
        viewId = "/faces/tributario/efetivacaocalculo/editaviacalculo.xhtml"),
    @URLMapping(id = "logIPTU", pattern = "/iptu/log/",
        viewId = "/faces/tributario/efetivacaocalculo/log.xhtml")
})
public class DividaIPTUControlador implements Serializable {


    private final Logger log = LoggerFactory.getLogger(DividaIPTUControlador.class);
    @EJB
    private ProcessoCalculoIPTUFacade processoCalculoIPTUFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private GeraValorDividaIPTU geraDebito;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private ConverterExercicio converterExercicio;
    private EfetivacaoIPTU efetivacaoIPTU;
    private List<ProcessoCalculoIPTU> source;
    private AssistenteEfetivacaoIPTU assistente;
    private Long idProcessoIPTU;
    private BigDecimal meioUfm;
    private Boolean efetivados, naoEfetivados;

    public Boolean getEfetivados() {
        return efetivados;
    }

    public void setEfetivados(Boolean efetivados) {
        this.efetivados = efetivados;
    }

    public Boolean getNaoEfetivados() {
        return naoEfetivados;
    }

    public void setNaoEfetivados(Boolean naoEfetivados) {
        this.naoEfetivados = naoEfetivados;
    }

    public Long getIdProcessoIPTU() {
        return idProcessoIPTU;
    }

    public void setIdProcessoIPTU(Long idProcessoIPTU) {
        this.idProcessoIPTU = idProcessoIPTU;
    }

    public EfetivacaoIPTU getEfetivacaoIPTU() {
        return efetivacaoIPTU;
    }

    public void setEfetivacaoIPTU(EfetivacaoIPTU efetivacaoIPTU) {
        this.efetivacaoIPTU = efetivacaoIPTU;
    }

    public List<ProcessoCalculoIPTU> getSource() {
        return source;
    }

    public void setSource(List<ProcessoCalculoIPTU> source) {
        this.source = source;
    }

    public ConverterExercicio getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(exercicioFacade);
        }
        return converterExercicio;
    }

    @URLAction(mappingId = "novaEfetivacaoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        efetivacaoIPTU = new EfetivacaoIPTU();
        efetivacaoIPTU.setDivida(configuracaoTributarioFacade.retornaUltimo().getDividaIPTU());
        if (efetivacaoIPTU.getDivida() == null) {
            FacesUtil.addError("Não foi configurada uma dívida para o IPTU", "Informe uma dívida nas configuração do tributário");
        }
        efetivacaoIPTU.setExercicio(exercicioFacade.getExercicioPorAno(Calendar.getInstance().get(Calendar.YEAR)));
        if (efetivacaoIPTU.getExercicio() == null) {
            FacesUtil.addError("Não foi cadastrado nenhum exercício para o ano de " + Calendar.getInstance().get(Calendar.YEAR), "Cadastre um novo exercicio para este ano");
        }
        efetivacaoIPTU.lancamentoFinal = new Date();
        naoEfetivados = true;

    }

    @URLAction(mappingId = "novaEfetivacaoIPTUviaCalculo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoEfetivacaoIPTU() {
        ProcessoCalculoIPTU processo = processoCalculoIPTUFacade.recuperar(idProcessoIPTU);
        efetivacaoIPTU = new EfetivacaoIPTU();
        efetivacaoIPTU.setDivida(configuracaoTributarioFacade.retornaUltimo().getDividaIPTU());
        if (efetivacaoIPTU.getDivida() == null) {
            FacesUtil.addError("Não foi configurada uma dívida para o IPTU", "Informe uma dívida nas configuração do tributário");
        }
        efetivacaoIPTU.setExercicio(processo.getExercicio());
        if (efetivacaoIPTU.getExercicio() == null) {
            FacesUtil.addError("Não foi cadastrado nenhum exercício para o ano de " + Calendar.getInstance().get(Calendar.YEAR), "Cadastre um novo exercicio para este ano");
        }
        efetivacaoIPTU.lancamentoFinal = new Date();
        processo.setOrdem(0);
        efetivacaoIPTU.getProcessos().add(processo);
        if (validarCampos()) {
            efetiva();
            FacesUtil.redirecionamentoInterno("/iptu/log");
        }
    }

    public void carregarProcessos() {
        if (validarCampos()) {
            source = processoCalculoIPTUFacade.listaProcessosPorDividaExercicioDataLancamento(efetivacaoIPTU);
            if (source.isEmpty()) {
                FacesUtil.addWarn("Nenhum Processo de Cálculo Encontrado", "Verifique que exista Processos de Cálculo para a Dívida e o Exercício selecionados antes de Continuar");
                source = Lists.newArrayList();
            } else {
                List<ProcessoCalculoIPTU> manter = Lists.newArrayList();
                for (ProcessoCalculoIPTU processo : source) {
                    verificarEfetivacao(processo);
                    if (efetivados && processo.getEfetivado() != null) {
                        manter.add(processo);
                    }
                    if (naoEfetivados && processo.getEfetivado() == null) {
                        manter.add(processo);
                    }
                }
                source = manter;
            }
            efetivacaoIPTU.setProcessos(new ArrayList<ProcessoCalculoIPTU>());
        }
    }

    public void validaDebitosDoExercicio() {
        if (validarCampos()) {
            FacesUtil.executaJavaScript("confirmaDebito.show()");
        }
    }

    public boolean validarCampos() {
        boolean valida = true;
        if (efetivacaoIPTU.getExercicio() == null) {
            FacesUtil.addError("Impossível continuar!", "Informe um exercício para filtrar os processos de calculo de I.P.T.U.");
            valida = false;
        }
        if (efetivacaoIPTU.getDivida() == null) {
            FacesUtil.addError("Impossível continuar!", "não foi encontrada nenhuma dívida de I.P.T.U. configurada, verifique as configurações do tributário");
            valida = false;
        }
        if ((naoEfetivados == null || !naoEfetivados) && (efetivados == null || !efetivados)) {
            efetivados = true;
            naoEfetivados = true;
        }
        try {
            for (ProcessoCalculoIPTU processoCalculoIPTU : efetivacaoIPTU.getProcessos()) {
                geraDebito.validaOpcoesPagamento(efetivacaoIPTU.getDivida(), processoCalculoIPTU.getExercicio());
            }
        } catch (Exception e) {
            FacesUtil.addError("Impossível continuar!", e.getMessage());
            valida = false;
        }
        return valida;
    }

    public String efetiva() {
        efetivaDivida();
        return "log";
    }

    @URLAction(mappingId = "logIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void log() {
        FacesUtil.executaJavaScript("iniciaEfetivacao();");
    }

    public void efetivaDivida() {
        try {
            assistente = new AssistenteEfetivacaoIPTU();
            assistente.setExercicio(efetivacaoIPTU.getExercicio());
            assistente.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            List<Long> ids = Lists.newArrayList();
            for (ProcessoCalculoIPTU processo : efetivacaoIPTU.getProcessos()) {
                List<Long> idsCalculoProcesso = processoCalculoIPTUFacade.listaIdCalculosPorProcesso(processo);
                StringBuilder idsCalculos = new StringBuilder();
                for (Long id : idsCalculoProcesso) {
                    idsCalculos.append(id).append(", ");
                }
                ids.addAll(idsCalculoProcesso);
            }
            int quantidadePorLista = ids.size() / 4;
            quantidadePorLista = quantidadePorLista > 1 ? quantidadePorLista : 1;

            List<List<Long>> quebrado = Lists.partition(ids, quantidadePorLista);
            assistente.inicializar(ids.size(), AssistenteEfetivacaoIPTU.TipoProcesso.EFETIVACAO);

            for (List<Long> quebra : quebrado) {
                geraDebito.geraDebito(quebra, assistente);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            FacesUtil.addError("Operação não Permitida", e.getMessage());
        }
    }

    public void adicionaTodosTarget() {
        List<ProcessoCalculoIPTU> processos = Lists.newLinkedList();
        for (ProcessoCalculoIPTU processo : source) {
            processos.add(processo);
        }
        for (ProcessoCalculoIPTU processo : processos) {
            source.remove(processo);
            adicionaTarget(processo);
        }
    }

    public void removeTodosTarget() {
        List<ProcessoCalculoIPTU> processos = Lists.newLinkedList();
        for (ProcessoCalculoIPTU processo : efetivacaoIPTU.getProcessos()) {
            processos.add(processo);
        }
        for (ProcessoCalculoIPTU processo : processos) {
            efetivacaoIPTU.getProcessos().remove(processo);
            removeTarget(processo);
        }
    }

    public void adicionaTarget(ProcessoCalculoIPTU processo) {
        processo.setOrdem(efetivacaoIPTU.getProcessos().size());
        efetivacaoIPTU.getProcessos().add(processo);
        source.remove(processo);
    }

    public void verificarEfetivacao(ProcessoCalculoIPTU processo) {
        if (meioUfm == null) {
            BigDecimal ufm = geraDebito.getMoedaFacade().recuperaValorVigenteUFM();
            meioUfm = ufm.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
        }
        processoCalculoIPTUFacade.verificarEfefetivacao(processo, meioUfm);
    }

    public void removeTarget(ProcessoCalculoIPTU processo) {
        efetivacaoIPTU.getProcessos().remove(processo);
        source.add(processo);
    }

    public void sobeTarget(ProcessoCalculoIPTU processo) {
        int indice = efetivacaoIPTU.getProcessos().indexOf(processo);
        if (indice > 0) {
            processo.setOrdem(indice - 1);
            ProcessoCalculoIPTU anterior = efetivacaoIPTU.getProcessos().get(indice - 1);
            anterior.setOrdem(indice);
        }
        ordenaTarget();
    }

    public void desceTarget(ProcessoCalculoIPTU processo) {
        int indice = efetivacaoIPTU.getProcessos().indexOf(processo);
        if (indice < efetivacaoIPTU.getProcessos().size() - 1) {
            processo.setOrdem(indice + 1);
            ProcessoCalculoIPTU proximo = efetivacaoIPTU.getProcessos().get(indice + 1);
            proximo.setOrdem(indice);
        }
        ordenaTarget();
    }

    public AssistenteEfetivacaoIPTU getAssistente() {
        return assistente;
    }

    public void ordenaTarget() {
        Collections.sort(efetivacaoIPTU.getProcessos(), new Comparator<ProcessoCalculoIPTU>() {
            @Override
            public int compare(ProcessoCalculoIPTU o1, ProcessoCalculoIPTU o2) {
                return o1.getOrdem().compareTo(o2.getOrdem());
            }
        });
    }

    public void consultaAndamentoEfetivacao() {
        if (assistente != null && !assistente.getProcessando()) {
            if (assistente.getTipo().equals(AssistenteEfetivacaoIPTU.TipoProcesso.CANCELAMENTO)) {
                efetiva();
            } else {
                FacesUtil.executaJavaScript("terminaEfetivacao();");
                FacesUtil.executaJavaScript("conclusao.show();");
            }
        }
    }

    public class EfetivacaoIPTU implements Serializable {

        private Divida divida;
        private Exercicio exercicio;
        private Date lancamentoInicial;
        private Date lancamentoFinal;
        private List<ProcessoCalculoIPTU> processos;

        public EfetivacaoIPTU() {
            processos = Lists.newArrayList();
        }

        public Divida getDivida() {
            return divida;
        }

        public void setDivida(Divida divida) {
            this.divida = divida;
        }

        public Exercicio getExercicio() {
            return exercicio;
        }

        public void setExercicio(Exercicio exercicio) {
            this.exercicio = exercicio;
        }

        public Date getLancamentoInicial() {
            return lancamentoInicial;
        }

        public void setLancamentoInicial(Date lancamentoInicial) {
            this.lancamentoInicial = lancamentoInicial;
        }

        public Date getLancamentoFinal() {
            return lancamentoFinal;
        }

        public void setLancamentoFinal(Date lancamentoFinal) {
            this.lancamentoFinal = lancamentoFinal;
        }

        public List<ProcessoCalculoIPTU> getProcessos() {
            return processos;
        }

        public void setProcessos(List<ProcessoCalculoIPTU> processos) {
            this.processos = processos;
        }
    }
}
