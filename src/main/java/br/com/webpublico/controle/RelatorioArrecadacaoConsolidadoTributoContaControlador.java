package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.RelatorioArrecadacaoConsolidadoTributoConta;
import br.com.webpublico.entidadesauxiliares.RelatorioArrecadacaoConsolidadoTributoContaAtributos;
import br.com.webpublico.entidadesauxiliares.VOItemLoteBaixa;
import br.com.webpublico.entidadesauxiliares.VOLoteBaixa;
import br.com.webpublico.enums.SituacaoLoteBaixa;
import br.com.webpublico.enums.TipoAgrupamentoMapaConsolidado;
import br.com.webpublico.exception.ContaReceitaException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 27/03/15
 * Time: 15:18
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "relatorioArrecadacaoConsolidadoTributoContaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioArrecadacaoConsolidadoTributoConta",
        pattern = "/tributario/arrecadacao/relatorios/mapa-arrecadacao-consolidado-tributo-conta/",
        viewId = "/faces/tributario/arrecadacao/relatorios/mapaarrecadacaotributoconta.xhtml")
})
public class RelatorioArrecadacaoConsolidadoTributoContaControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioArrecadacaoConsolidadoTributoContaControlador.class);
    @EJB
    private LoteBaixaFacade loteBaixaFacade;
    @EJB
    private RelatorioArrecadacaoConsolidadoTributoContaFacade relatorioArrecadacaoConsolidadoTributoContaFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private TributoFacade tributoFacade;
    @EJB
    private DAMFacade damFacade;
    private Filtro filtro;
    private BancoContaConfTributario[] contasSelecionadas;
    private Map<Long, List<VOItemLoteBaixa>> mapaItensLoteBaixaPorLoteBaixa;
    private List<VOLoteBaixa> lotes;
    private boolean renderizaComplemento;
    private ContaReceita contaReceita;
    private Future<RelatorioArrecadacaoConsolidadoTributoConta> future;
    private Boolean concluiRelatorio;
    private AssistenteBarraProgresso assistenteBarraProgresso;


    @URLAction(mappingId = "novoRelatorioArrecadacaoConsolidadoTributoConta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new Filtro();
        this.lotes = Lists.newArrayList();
    }

    public Boolean getConcluiRelatorio() {
        return concluiRelatorio;
    }

    public void setConcluiRelatorio(Boolean concluiRelatorio) {
        this.concluiRelatorio = concluiRelatorio;
    }

    public Future<RelatorioArrecadacaoConsolidadoTributoConta> getFuture() {
        return future;
    }

    public void setFuture(Future<RelatorioArrecadacaoConsolidadoTributoConta> future) {
        this.future = future;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public void imprimeRelatorio() {
        concluiRelatorio = false;

        if (!filtro.validaFiltrosMapa()) {
            return;
        }
        if (contaReceita != null) {
            filtro.getContasReceita().add(contaReceita);
        }
        assistenteBarraProgresso = new AssistenteBarraProgresso("Gerando o Mapa de Arrecadação", mapaItensLoteBaixaPorLoteBaixa.size());
        try {
            future = relatorioArrecadacaoConsolidadoTributoContaFacade.montarRelatorio(assistenteBarraProgresso, mapaItensLoteBaixaPorLoteBaixa, filtro);
            FacesUtil.executaJavaScript("iniciarGeracaoRelatorio()");
        } catch (ContaReceitaException e) {
            logger.error("Erro ao imprimeRelatorio: {}", e);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível emitir o relatório!");
        }

    }

    public void consultarAndamentoEmissaoRelatorio() {
        if (future != null && future.isDone()) {
            concluiRelatorio = true;
        }
    }

    public void imprimirRelatorio() {
        try {
            RelatorioArrecadacaoConsolidadoTributoConta dados = future.get();
            filtrarDadosPorTributoFiltrado(dados);
            ImprimeRelatoriosArrecadacao imprimeRelatoriosArrecadacao = new ImprimeRelatoriosArrecadacao();
            imprimeRelatoriosArrecadacao.imprimeMapaConsolidadoTributoConta(filtro, dados);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filtrarDadosPorTributoFiltrado(RelatorioArrecadacaoConsolidadoTributoConta dados) {
        if (filtro.getTributosSelecionados() != null && filtro.getTributosSelecionados().size() > 0) {
            List<RelatorioArrecadacaoConsolidadoTributoContaAtributos> arrecadacoesFiltrada = Lists.newArrayList();
            for (RelatorioArrecadacaoConsolidadoTributoContaAtributos arrecadacao : dados.getArrecadacoes()) {
                if (filtro.getIdsTributosSelecionados().contains(arrecadacao.getTributo().getId())) {
                    arrecadacoesFiltrada.add(arrecadacao);
                }
            }
            dados.setArrecadacoes(arrecadacoesFiltrada);

            List<RelatorioArrecadacaoConsolidadoTributoContaAtributos> acrescimosFiltrada = Lists.newArrayList();
            for (RelatorioArrecadacaoConsolidadoTributoContaAtributos acrescimo : dados.getAcrescimos()) {
                if (filtro.getIdsTributosSelecionados().contains(acrescimo.getTributo().getId())) {
                    acrescimosFiltrada.add(acrescimo);
                }
            }
            dados.setAcrescimos(acrescimosFiltrada);

            List<RelatorioArrecadacaoConsolidadoTributoContaAtributos> descontosFiltrada = Lists.newArrayList();
            for (RelatorioArrecadacaoConsolidadoTributoContaAtributos desconto : dados.getDescontos()) {
                if (filtro.getIdsTributosSelecionados().contains(desconto.getTributo().getId())) {
                    descontosFiltrada.add(desconto);
                }
            }
            dados.setDescontos(descontosFiltrada);
        }
    }

    public List<SelectItem> getTiposAgrupamento() {
        return Util.getListSelectItem(Arrays.asList(TipoAgrupamentoMapaConsolidado.values()));
    }

    public Filtro getFiltro() {
        return filtro;
    }

    public void setFiltro(Filtro filtro) {
        this.filtro = filtro;
    }

    public BancoContaConfTributario[] getContasSelecionadas() {
        return contasSelecionadas;
    }

    public void setContasSelecionadas(BancoContaConfTributario[] contasSelecionadas) {
        this.contasSelecionadas = contasSelecionadas;
    }

    public Map<Long, List<VOItemLoteBaixa>> getMapaItensLoteBaixaPorLoteBaixa() {
        return mapaItensLoteBaixaPorLoteBaixa;
    }

    public void setMapaItensLoteBaixaPorLoteBaixa(Map<Long, List<VOItemLoteBaixa>> mapaItensLoteBaixaPorLoteBaixa) {
        this.mapaItensLoteBaixaPorLoteBaixa = mapaItensLoteBaixaPorLoteBaixa;
    }

    public boolean isRenderizaComplemento() {
        return renderizaComplemento;
    }

    public void setRenderizaComplemento(boolean renderizaComplemento) {
        this.renderizaComplemento = renderizaComplemento;
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
        renderizaComplemento = false;
        concluiRelatorio = false;

        if (filtro.getTributosSelecionados().isEmpty() &&
            filtro.getTributo() != null && filtro.getTributo().getId() != null) {
            filtro.addTributo();
        }

        if (filtro.validaFiltrosMapa()) {
            List<String> situacoes = Lists.newArrayList();
            situacoes.add(SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name());
            situacoes.add(SituacaoLoteBaixa.BAIXADO.name());
            List<VOItemLoteBaixa> itensLoteBaixa = loteBaixaFacade.buscarItensLoteBaixa(filtro.getDataInicial(),
                filtro.getDataFinal(),
                situacoes,
                filtro.getExercicio(),
                filtro.getSubContas());
            if (itensLoteBaixa != null && itensLoteBaixa.size() > 0) {
                mapaItensLoteBaixaPorLoteBaixa = gerarMapaItensLoteBaixaParcelaPorLoteBaixa(itensLoteBaixa);

                renderizaComplemento = true;
            } else {
                FacesUtil.addAtencao("Nenhum lote encontrado com os filtros utilizados.");
            }
        }
    }

    private Map<Long, List<VOItemLoteBaixa>> gerarMapaItensLoteBaixaParcelaPorLoteBaixa(List<VOItemLoteBaixa> itensLoteBaixa) {
        Map<Long, List<VOItemLoteBaixa>> toReturn = Maps.newHashMap();
        lotes.clear();
        Long idLote;
        if (itensLoteBaixa != null) {
            for (VOItemLoteBaixa itemLoteBaixa : itensLoteBaixa) {
                idLote = itemLoteBaixa.getIdLoteBaixa();
                if (toReturn.get(idLote) == null) {
                    toReturn.put(idLote, Lists.<VOItemLoteBaixa>newArrayList());
                    lotes.add(VOLoteBaixa.toVO(itemLoteBaixa));
                }
                toReturn.get(idLote).add(itemLoteBaixa);
            }
        }
        return toReturn;
    }

    public int getTamanhoListaLotes() {
        return mapaItensLoteBaixaPorLoteBaixa.size();
    }

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public class Filtro {
        private TipoAgrupamentoMapaConsolidado tipoAgrupamento;
        private Date dataInicial;
        private Date dataFinal;
        private BancoContaConfTributario conta;
        private List<SubConta> subContas;
        private List<Tributo> tributosSelecionados;
        private List<ContaReceita> contasReceita;
        private Tributo tributo;
        private Exercicio exercicio;
        private List<Tributo> tributos;

        public Filtro() {
            this.subContas = Lists.newArrayList();
            this.tributosSelecionados = Lists.newArrayList();
            this.contasReceita = Lists.newArrayList();
            this.tributos = null;
        }

        public List<Tributo> getTributos() {
            if (tributos == null) {
                tributos = tributoFacade.listaTributosOrdenadoPorDescricao();
            }
            return tributos;
        }

        public void setTributos(List<Tributo> tributos) {
            this.tributos = tributos;
        }

        public TipoAgrupamentoMapaConsolidado getTipoAgrupamento() {
            return tipoAgrupamento;
        }

        public void setTipoAgrupamento(TipoAgrupamentoMapaConsolidado tipoAgrupamento) {
            this.tipoAgrupamento = tipoAgrupamento;
        }

        public Date getDataInicial() {
            return dataInicial;
        }

        public void setDataInicial(Date dataInicial) {
            this.dataInicial = dataInicial;
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

        public Tributo getTributo() {
            return tributo;
        }

        public void setTributo(Tributo tributo) {
            this.tributo = tributo;
        }

        public List<ContaReceita> getContasReceita() {
            return contasReceita;
        }

        public void setContasReceita(List<ContaReceita> contasReceita) {
            this.contasReceita = contasReceita;
        }

        public Exercicio getExercicio() {
            return exercicio;
        }

        public void setExercicio(Exercicio exercicio) {
            this.exercicio = exercicio;
        }

        public List<Tributo> getTributosSelecionados() {
            return tributosSelecionados;
        }

        public void setTributosSelecionados(List<Tributo> tributosSelecionados) {
            this.tributosSelecionados = tributosSelecionados;
        }

        public List<Long> getIdsTributosSelecionados() {
            List<Long> idsTributos = Lists.newArrayList();
            if (tributosSelecionados != null) {
                for (Tributo tributo : tributosSelecionados) {
                    idsTributos.add(tributo.getId());
                }
            }
            return idsTributos;
        }

        public List<SelectItem> getDividas() {
            List<SelectItem> toReturn = new ArrayList<>();
            List<Divida> dividas = dividaFacade.listaDividasOrdenadoPorDescricao();
            for (Divida divida : dividas) {
                toReturn.add(new SelectItem(divida, divida.getDescricao()));
            }
            return toReturn;
        }

        public List<SelectItem> getTributosPorTipo() {
            List<SelectItem> toReturn = new ArrayList<>();
            List<Tributo> tributos = getTributos();
            for (Tributo tributo : tributos) {
                toReturn.add(new SelectItem(tributo, tributo.getCodigo() + " - " + tributo.getDescricao()));
            }
            return toReturn;
        }

        private boolean validarTributo() {
            boolean valida = true;
            if (tributo == null || tributo.getId() == null) {
                FacesUtil.addError("Atenção", "Selecione um tributo para adicionar");
                valida = false;
            } else if (tributosSelecionados.contains(tributo)) {
                FacesUtil.addError("Atenção", "Esse Tributo já foi selecionado.");
                valida = false;
            }
            return valida;
        }

        public void addTributo() {
            if (validarTributo()) {
                tributosSelecionados.add(tributo);
                tributo = new Tributo();
            }
        }

        public void removerTributo(Tributo tributo) {
            if (tributosSelecionados.contains(tributo)) {
                tributosSelecionados.remove(tributo);
            }
        }

        public void adicionarTributosAcrescimos(Tributo tributo) {
            if (!tributosSelecionados.contains(tributo.getTributoJuros())) {
                tributosSelecionados.add(tributo.getTributoJuros());
            }
            if (!tributosSelecionados.contains(tributo.getTributoMulta())) {
                tributosSelecionados.add(tributo.getTributoMulta());
            }
            if (!tributosSelecionados.contains(tributo.getTributoCorrecaoMonetaria())) {
                tributosSelecionados.add(tributo.getTributoCorrecaoMonetaria());
            }
        }

        public boolean validaFiltrosMapa() {
            boolean valida = true;
            if (tipoAgrupamento == null) {
                FacesUtil.addCampoObrigatorio("Informe o Tipo de Agrupamento.");
                valida = false;
            }
            if (dataInicial == null) {
                FacesUtil.addCampoObrigatorio("Informe a Data de Início do Intervalo.");
                valida = false;
            }
            if (dataFinal == null) {
                FacesUtil.addCampoObrigatorio("Informe a Data de Término do Intervalo.");
                valida = false;
            }
            if (dataInicial != null && dataFinal != null) {
                if (dataInicial.getTime() > dataFinal.getTime()) {
                    FacesUtil.addOperacaoNaoPermitida("A data de início deve ser menor que a data final do intervalo.");
                    valida = false;
                }
            }

            filtro.getSubContas().clear();
            for (BancoContaConfTributario bancoConta : contasSelecionadas) {
                filtro.getSubContas().add(bancoConta.getSubConta());
            }
            return valida;
        }
    }

    public List<VOLoteBaixa> getLotes() {
        return lotes;
    }

    public void setLotes(List<VOLoteBaixa> lotes) {
        this.lotes = lotes;
    }

    public void corrigirDAMs() {
        List<Long> idsDams = Lists.newArrayList();
        for (VOLoteBaixa lote : lotes) {
            List<VOItemLoteBaixa> itensLoteBaixa = loteBaixaFacade.buscarVOItensLoteBaixaDoLote(lote.getId());
            for (VOItemLoteBaixa itemLoteBaixa : itensLoteBaixa) {
                idsDams.add(itemLoteBaixa.getIdDam());
            }
        }
        logger.debug("Serão analisados " + idsDams.size() + " dams!");
        for (Long idDam : idsDams) {
            DAM dam = damFacade.recuperar(idDam);
            if (dam != null) {
                loteBaixaFacade.corrigirTributosDAM(dam);
            }
        }
        logger.debug("Acabou");
    }
}
