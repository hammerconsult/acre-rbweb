package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.ImprimeRelatoriosArrecadacao;
import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoLoteBaixa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.AsyncExecutor;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@ViewScoped
@ManagedBean(name = "relatorioMapaArrecadacaoConsolidadoControlador")
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioMapaArrecadacaoConsolidado",
        pattern = "/tributario/arrecadacao/relatorios/mapa-arrecadacao-consolidado/",
        viewId = "/faces/tributario/arrecadacao/relatorios/mapaarrecadacao.xhtml")
})
public class RelatorioMapaArrecadacaoConsolidadoControlador {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioMapaArrecadacaoConsolidadoControlador.class);
    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    @EJB
    private LoteBaixaFacade loteBaixaFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private EnquadramentoTributoExercicioFacade enquadramentoTributoExercicioFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private List<LoteBaixa> listaLotes;
    private boolean renderizaComplemento;
    private FiltroRelatorioMapaConsolidado filtro;
    private BancoContaConfTributario[] contasSelecionadas;
    private ContaReceita contaReceita;
    private Exercicio exercicio;
    private LoteBaixaFacade.AssistenteMapaArrecadacao assistente;
    private CompletableFuture<LoteBaixaFacade.AssistenteMapaArrecadacao> future;
    private Boolean concluiRelatorio;

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public boolean isRenderizaComplemento() {
        return renderizaComplemento;
    }

    public void setRenderizaComplemento(boolean renderizaComplemento) {
        this.renderizaComplemento = renderizaComplemento;
    }

    public List<LoteBaixa> getListaLotes() {
        return listaLotes;
    }

    public void setListaLotes(List<LoteBaixa> listaLotes) {
        this.listaLotes = listaLotes;
    }

    public FiltroRelatorioMapaConsolidado getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroRelatorioMapaConsolidado filtro) {
        this.filtro = filtro;
    }

    public BancoContaConfTributario[] getContasSelecionadas() {
        return contasSelecionadas;
    }

    public void setContasSelecionadas(BancoContaConfTributario[] contasSelecionadas) {
        this.contasSelecionadas = contasSelecionadas;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Boolean getConcluiRelatorio() {
        return concluiRelatorio;
    }

    public void setConcluiRelatorio(Boolean concluiRelatorio) {
        this.concluiRelatorio = concluiRelatorio;
    }

    public LoteBaixaFacade.AssistenteMapaArrecadacao getAssistente() {
        return assistente;
    }

    public void setAssistente(LoteBaixaFacade.AssistenteMapaArrecadacao assistente) {
        this.assistente = assistente;
    }

    @URLAction(mappingId = "novoRelatorioMapaArrecadacaoConsolidado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroRelatorioMapaConsolidado();
        renderizaComplemento = false;
        exercicio = getSistemaControlador().getExercicioCorrente();
    }

    public List<BancoContaConfTributario> getContas() {
        List<BancoContaConfTributario> lista = loteBaixaFacade.recuperaContasConfiguracao();
        Collections.sort(lista, new Comparator<BancoContaConfTributario>() {
            @Override
            public int compare(BancoContaConfTributario o1, BancoContaConfTributario o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        return lista;
    }

    public void pesquisalistaLotes() {
        try {
            filtro.validaFiltrosMapa();
            if (contaReceita != null) {
                addContaReceita();
            }

            List<String> situacoes = Lists.newArrayList();
            situacoes.add(SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name());
            situacoes.add(SituacaoLoteBaixa.BAIXADO.name());
            listaLotes = loteBaixaFacade.buscarItensLotesPorIntervaloEContaESituacao(filtro.getDataInicio(),
                filtro.getDataFinal(),
                filtro.getSubContas(),
                situacoes);
            renderizaComplemento = true;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void gerarRelatorio() {
        if (!listaLotes.isEmpty()) {
            concluiRelatorio = false;

            try {
                filtro.validaFiltrosMapa();
                if (contaReceita != null) {
                    filtro.getContasReceita().add(contaReceita);
                }

                assistente = new LoteBaixaFacade.AssistenteMapaArrecadacao(sistemaFacade.getUsuarioCorrente(),
                    "Gerando o Mapa de Arrecadação Consolidado", listaLotes.size());
                future = AsyncExecutor.getInstance().execute(assistente,
                    () -> loteBaixaFacade.gerarMapaArrecadacaoConsolidado(assistente, listaLotes, filtro));

                FacesUtil.executaJavaScript("iniciarGeracaoRelatorio()");
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        }
    }

    public void imprimirRelatorio() {
        try {
            ImprimeRelatoriosArrecadacao imprime = new ImprimeRelatoriosArrecadacao();
            imprime.imprimeMapaArrecadacao(listaLotes, filtro, null,
                assistente.getListaArrecadacao(), assistente.getListaAcrescimos(), assistente.getListaDescontos(),
                "MapaArrecadacao.jasper");
        } catch (Exception e) {
            logger.error("Erro ao emitir o mapa {}", e);
        }
    }

    public void geraImprimeMapaHonorarios() {
        if (!listaLotes.isEmpty()) {
            loteBaixaFacade.geraImprimeMapaHonorarios(listaLotes, filtro, null);
        }
    }

    public void consultarAndamentoEmissaoRelatorio() {
        if (future != null && future.isDone()) {
            concluiRelatorio = true;
        }
    }

    public int getTamanhoListaLotes() {
        return listaLotes.size();
    }

    public void addContaReceita() {
        if (contaReceita != null && !filtro.getContasReceita().contains(contaReceita)) {
            filtro.getContasReceita().add(contaReceita);
        }
    }

    public void removeContaReceita() {
        if (filtro.getContasReceita().contains(contaReceita)) {
            filtro.getContasReceita().remove(contaReceita);
        }
    }

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public List<Conta> completaReceitaLOA(String parte) {
        return contaFacade.listaFiltrandoReceitaAnalitica(parte.trim(), exercicio != null ? exercicio : getSistemaControlador().getExercicioCorrente());
    }

    public class FiltroRelatorioMapaConsolidado {
        private Date dataInicio;
        private Date dataFinal;
        private Boolean agrupaPorData;
        private Boolean agrupaPorAgenteArrecadador;
        private BancoContaConfTributario conta;
        private List<SubConta> subContas;
        private List<ContaReceita> contasReceita;

        public FiltroRelatorioMapaConsolidado() {
            this.subContas = Lists.newArrayList();
            this.contasReceita = Lists.newArrayList();
        }

        public Date getDataInicio() {
            return dataInicio;
        }

        public void setDataInicio(Date dataInicio) {
            this.dataInicio = dataInicio;
        }

        public Date getDataFinal() {
            return dataFinal;
        }

        public void setDataFinal(Date dataFinal) {
            this.dataFinal = dataFinal;
        }

        public BancoContaConfTributario getConta() {
            return conta;
        }

        public void setConta(BancoContaConfTributario conta) {
            this.conta = conta;
        }

        public List<SubConta> getSubContas() {
            return subContas;
        }

        public void setSubContas(List<SubConta> subContas) {
            this.subContas = subContas;
        }

        public List<ContaReceita> getContasReceita() {
            return contasReceita;
        }

        public void setContasReceita(List<ContaReceita> contasReceita) {
            this.contasReceita = contasReceita;
        }

        public Boolean getAgrupaPorData() {
            return agrupaPorData != null ? agrupaPorData : false;
        }

        public void setAgrupaPorData(Boolean agrupaPorData) {
            this.agrupaPorData = agrupaPorData;
        }

        public Boolean getAgrupaPorAgenteArrecadador() {
            return agrupaPorAgenteArrecadador != null ? agrupaPorAgenteArrecadador : false;
        }

        public void setAgrupaPorAgenteArrecadador(Boolean agrupaPorAgenteArrecadador) {
            this.agrupaPorAgenteArrecadador = agrupaPorAgenteArrecadador;
        }

        public void validaFiltrosMapa() {
            ValidacaoException ve = new ValidacaoException();
            if (dataInicio == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe a Data de Início do Intervalo.");
            }
            if (dataFinal == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe a Data de Término do Intervalo.");
            }
            if (dataInicio != null && dataFinal != null) {
                if (dataInicio.getTime() > dataFinal.getTime()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A data de início deve ser menor que a data final do intervalo.");
                }
            }
            filtro.getSubContas().clear();
            for (BancoContaConfTributario bancoConta : contasSelecionadas) {
                filtro.getSubContas().add(bancoConta.getSubConta());
            }
            ve.lancarException();
        }
    }

}
