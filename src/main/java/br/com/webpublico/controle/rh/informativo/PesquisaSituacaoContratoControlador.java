package br.com.webpublico.controle.rh.informativo;

import br.com.webpublico.entidadesauxiliares.BarraProgressoItens;
import br.com.webpublico.entidadesauxiliares.rh.VOSituacaoContratoFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.negocios.rh.informativo.PesquisaSituacaoContratoFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.ExcelUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.StreamedContent;
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
 * @author octavio
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "verSituacoesFuncionaisInconsistentes", pattern = "/informativo/consultar-situacoes-contratofp-inconsistente/", viewId = "/faces/rh/informativo/visualizar.xhtml")
})
public class PesquisaSituacaoContratoControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(PesquisaSituacaoContratoControlador.class);
    @EJB
    private PesquisaSituacaoContratoFacade facade;
    private TipoSituacaoFuncionaisIncosistente tipo;
    private BarraProgressoItens assistente;
    private Future<Map<String, List<VOSituacaoContratoFP>>> futureSituacoes;
    private Set<VOSituacaoContratoFP> situacoesInconsistentes;
    private Map<String, List<VOSituacaoContratoFP>> situacoes;

    @URLAction(mappingId = "verSituacoesFuncionaisInconsistentes", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verSituacoesFuncionaisInconsistentes() {
        situacoesInconsistentes = Sets.newLinkedHashSet();
        situacoes = Maps.newHashMap();
    }

    public TipoSituacaoFuncionaisIncosistente getTipo() {
        return tipo;
    }

    public void setTipo(TipoSituacaoFuncionaisIncosistente tipo) {
        this.tipo = tipo;
    }

    public Set<VOSituacaoContratoFP> getSituacoesInconsistentes() {
        return situacoesInconsistentes;
    }

    public void setSituacoesInconsistentes(Set<VOSituacaoContratoFP> situacoesInconsistentes) {
        this.situacoesInconsistentes = situacoesInconsistentes;
    }

    public BarraProgressoItens getAssistente() {
        return assistente;
    }

    public void setAssistente(BarraProgressoItens assistente) {
        this.assistente = assistente;
    }

    public List<SelectItem> tiposSituacoesInconsistentes() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        List<TipoSituacaoFuncionaisIncosistente> tipos = Lists.newArrayList(TipoSituacaoFuncionaisIncosistente.values());
        ordenarTipos(tipos);

        for (TipoSituacaoFuncionaisIncosistente valor : tipos) {
            retorno.add(new SelectItem(valor, valor.toString()));
        }

        return retorno;
    }

    public void buscarSituacoesAssincrono() {
        try {
            assistente = new BarraProgressoItens();
            futureSituacoes = facade.buscarSituacoesAssincrono(assistente);

            abrirDialogProgressBar("dialogProgressBar.show()");
            executarPoll("pollBuscarSituacoes.start()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void abrirDialogProgressBar(String dialog) {
        FacesUtil.executaJavaScript(dialog);
    }

    private void executarPoll(String idPoll) {
        FacesUtil.executaJavaScript(idPoll);
    }

    public void abortar() {
        if (futureSituacoes != null) {
            futureSituacoes.cancel(true);
            assistente.finaliza();
        }
    }

    public void finalizarBarraProgressao() {
        if (!assistente.getCalculando()) {
            FacesUtil.executaJavaScript("dialogProgressBar.hide()");
            FacesUtil.atualizarComponente("Formulario");
            try {
                if (futureSituacoes.get() != null) {
                    situacoes = futureSituacoes.get();
                }
            } catch (Exception e) {
                FacesUtil.addError("Erro ao buscar situações...", e.getMessage());
            }
        }
    }

    private void ordenarTipos(List lista) {
        Collections.sort(lista, new Comparator<TipoSituacaoFuncionaisIncosistente>() {
            @Override
            public int compare(TipoSituacaoFuncionaisIncosistente o1, TipoSituacaoFuncionaisIncosistente o2) {
                return ComparisonChain.start().compare(o1.getCodigo(), o2.getCodigo()).result();
            }
        });
    }

    public void buscarSituacoes() {
        try {
            validarTipo();
            situacoesInconsistentes = Sets.newLinkedHashSet();
            switch (tipo) {
                case PRIMEIRA_SITUACAOFUNCIONAL_DIFERENTE_DATAEXERCICIO:
                    adicionarPrimeiraSituacaoDiferenteDataExercicio();

                    break;
                case DATAFINAL_MENOR_DATAINICIAL:
                    aidiconarSituacaoDataFinalMenorDataInicial();

                    break;
                case SEM_INICIOVIGENCIA:
                    adicionarSituacaoSemInicioVigencia();

                    break;
                case SEM_INICIO_E_FINALVIGENCIA:
                    adicionarSituacaoSemInicioAndFinalVigencia();

                    break;
                case SEM_FINALVIGENCIA:
                    adicionarSituacaoSemFinalVigencia();
                    break;

                case SEM_REGISTRO:
                    adicionarSituacaoComDatasVagasEntrePeriodos();

                    break;
                case DATAFINAL_IGUAL_DATAINICIAL:
                    adicionarSituacaoDataFinalIgualDataInicial();

                    break;
                case DATAS_IGUAIS:
                    adicionarSituacaoDatasIguais();

                    break;
                case DATAINICIAL_IGUAL:
                    adicionarSituacaoDataInicialIgual();

                    break;
                case ENTRE_PERIODO:
                    adicionarSituacaoPeriodoEntrePeriodos();

                    break;
                case ENTRE_PERIODO_SEM_FINALVIGENCIA:
                    adicionarSituacaoPeriodoEntrePeriodosSemFinalVigencia();

                    break;

                case DATAFINAL_MAIOR_DATAATUAL:
                    adicionarSituacaoFinalVigenciaMaiorQueDataAtual();

                    break;
            }

            validarSituacoes();

            if (!situacoesInconsistentes.isEmpty()) {
                FacesUtil.executaJavaScript("dialogExcel.show()");
            } else {
                tipo = null;
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao buscar situações. Detalhes: " + e.getMessage());
            logger.error("Erro ao buscar situações: ", e.getMessage());
        }
    }

    private void adicionarSituacaoPeriodoEntrePeriodosSemFinalVigencia() {
        for (Map.Entry<String, List<VOSituacaoContratoFP>> mapaSituacao : situacoes.entrySet()) {
            for (VOSituacaoContratoFP original : mapaSituacao.getValue()) {
                for (VOSituacaoContratoFP comparacao : mapaSituacao.getValue()) {

                    if (!original.getId().equals(comparacao.getId()) && hasPeriodoEntrePeriodosSemFinalVigencia(original, comparacao)) {
                        adicionarSituacoes(original, comparacao);
                    }

                }
            }
        }
    }

    private void adicionarSituacaoPeriodoEntrePeriodos() {
        for (Map.Entry<String, List<VOSituacaoContratoFP>> mapaSituacao : situacoes.entrySet()) {
            for (VOSituacaoContratoFP original : mapaSituacao.getValue()) {
                for (VOSituacaoContratoFP comparacao : mapaSituacao.getValue()) {

                    if (!original.getId().equals(comparacao.getId()) && hasPeriodoEntrePeriodos(original, comparacao)) {
                        adicionarSituacoes(original, comparacao);
                    }

                }
            }
        }
    }

    private void adicionarSituacaoDataInicialIgual() {
        for (Map.Entry<String, List<VOSituacaoContratoFP>> mapaSituacao : situacoes.entrySet()) {
            for (VOSituacaoContratoFP original : mapaSituacao.getValue()) {
                for (VOSituacaoContratoFP comparacao : mapaSituacao.getValue()) {

                    if (!original.getId().equals(comparacao.getId()) && hasDataInicialIgual(original, comparacao)) {
                        adicionarSituacoes(original, comparacao);
                    }

                }
            }
        }
    }

    private void adicionarSituacaoDatasIguais() {
        for (Map.Entry<String, List<VOSituacaoContratoFP>> mapaSituacao : situacoes.entrySet()) {
            for (VOSituacaoContratoFP original : mapaSituacao.getValue()) {
                for (VOSituacaoContratoFP comparacao : mapaSituacao.getValue()) {

                    if (!original.getId().equals(comparacao.getId()) && hasDatasIguais(original, comparacao)) {
                        adicionarSituacoes(original, comparacao);
                    }

                }
            }
        }
    }

    private void adicionarSituacaoDataFinalIgualDataInicial() {
        for (Map.Entry<String, List<VOSituacaoContratoFP>> mapaSituacao : situacoes.entrySet()) {
            for (VOSituacaoContratoFP original : mapaSituacao.getValue()) {
                for (VOSituacaoContratoFP comparacao : mapaSituacao.getValue()) {

                    if (!original.getId().equals(comparacao.getId()) && hasDataFinalIgualDataInicial(original, comparacao)) {
                        adicionarSituacoes(original, comparacao);
                    }

                }
            }
        }
    }

    private void adicionarSituacaoComDatasVagasEntrePeriodos() {
        for (Map.Entry<String, List<VOSituacaoContratoFP>> mapaSituacao : situacoes.entrySet()) {
            ordenarPorData(mapaSituacao);

            for (int i = 1; i < mapaSituacao.getValue().size(); i++) {
                if (mapaSituacao.getValue().get(i - 1).getDataFinalSituacao() != null &&
                    !mapaSituacao.getValue().get(i - 1).getDataInicioSituacao().after(mapaSituacao.getValue().get(i - 1).getDataFinalSituacao())) {
                    int dias = DataUtil.diasEntreDate(mapaSituacao.getValue().get(i - 1).getDataFinalSituacao(),
                        mapaSituacao.getValue().get(i).getDataInicioSituacao());

                    if (dias > 2) {
                        adicionarSituacoes(mapaSituacao.getValue().get(i - 1), mapaSituacao.getValue().get(i));
                    }
                }
            }
        }
    }

    private void adicionarSituacaoSemFinalVigencia() {
        for (Map.Entry<String, List<VOSituacaoContratoFP>> mapaSituacao : situacoes.entrySet()) {
            List<VOSituacaoContratoFP> inconsistentes = Lists.newArrayList();
            for (VOSituacaoContratoFP situacao : mapaSituacao.getValue()) {
                if (situacao.getDataFinalSituacao() == null && situacao.getDataInicioSituacao() != null) {
                    inconsistentes.add(situacao);
                }
            }
            if (!inconsistentes.isEmpty() && inconsistentes.size() > 1) {
                situacoesInconsistentes.addAll(inconsistentes);
            }
        }
    }

    private void adicionarSituacaoSemInicioAndFinalVigencia() {
        for (Map.Entry<String, List<VOSituacaoContratoFP>> mapaSituacao : situacoes.entrySet()) {
            for (VOSituacaoContratoFP situacao : mapaSituacao.getValue()) {
                if (situacao.getDataInicioSituacao() == null && situacao.getDataFinalSituacao() == null) {
                    situacoesInconsistentes.add(situacao);
                }
            }
        }
    }

    private void adicionarSituacaoFinalVigenciaMaiorQueDataAtual() {
        for (Map.Entry<String, List<VOSituacaoContratoFP>> mapaSituacao : situacoes.entrySet()) {
            for (VOSituacaoContratoFP situacao : mapaSituacao.getValue()) {
                if (situacao.getDataFinalSituacao() != null && situacao.getDataFinalSituacao().after(new Date())) {
                    situacoesInconsistentes.add(situacao);
                }
            }
        }
    }

    private void adicionarSituacaoSemInicioVigencia() {
        for (Map.Entry<String, List<VOSituacaoContratoFP>> mapaSituacao : situacoes.entrySet()) {
            for (VOSituacaoContratoFP situacao : mapaSituacao.getValue()) {
                if (situacao.getDataInicioSituacao() == null && situacao.getDataFinalSituacao() != null) {
                    situacoesInconsistentes.add(situacao);
                }
            }
        }
    }

    private void aidiconarSituacaoDataFinalMenorDataInicial() {
        for (Map.Entry<String, List<VOSituacaoContratoFP>> mapaSituacao : situacoes.entrySet()) {
            for (VOSituacaoContratoFP situacao : mapaSituacao.getValue()) {
                if ((situacao.getDataFinalSituacao() != null) && (situacao.getDataFinalSituacao().before(situacao.getDataInicioSituacao()))) {
                    situacoesInconsistentes.add(situacao);
                }
            }
        }
    }

    private void adicionarPrimeiraSituacaoDiferenteDataExercicio() {
        for (Map.Entry<String, List<VOSituacaoContratoFP>> mapaSituacao : situacoes.entrySet()) {
            VOSituacaoContratoFP primeiraSituacao = buscarMenorData(mapaSituacao);
            if (!primeiraSituacao.getDataInicioContrato().equals(primeiraSituacao.getDataInicioSituacao())) {
                situacoesInconsistentes.add(primeiraSituacao);
            }
        }
    }

    private void adicionarSituacoes(VOSituacaoContratoFP original, VOSituacaoContratoFP comparacao) {
        if (original != null) {
            situacoesInconsistentes.add(original);
        }
        if (comparacao != null) {
            situacoesInconsistentes.add(comparacao);
        }
    }

    private boolean hasElementosNulos(VOSituacaoContratoFP original, VOSituacaoContratoFP comparacao) {
        return original != null && comparacao != null && original.getDataInicioSituacao() != null
            && original.getDataFinalSituacao() != null && comparacao.getDataInicioSituacao() != null
            && comparacao.getDataFinalSituacao() != null;
    }

    private boolean hasDataInicialMenorFinal(VOSituacaoContratoFP original, VOSituacaoContratoFP comparacao) {
        return !original.getDataInicioSituacao().after(original.getDataFinalSituacao()) &&
            !comparacao.getDataInicioSituacao().after(comparacao.getDataFinalSituacao());
    }


    private boolean hasDataFinalIgualDataInicial(VOSituacaoContratoFP original, VOSituacaoContratoFP comparacao) {
        if (hasElementosNulos(original, comparacao) && hasDataInicialMenorFinal(original, comparacao)) {
            return (original.getDataFinalSituacao().equals(comparacao.getDataInicioSituacao()) && comparacao.getDataFinalSituacao().after(original.getDataInicioSituacao())) ||
                (original.getDataFinalSituacao().after(comparacao.getDataInicioSituacao()) && original.getDataFinalSituacao().before(comparacao.getDataFinalSituacao())) ||
                (original.getDataFinalSituacao().after(comparacao.getDataInicioSituacao()) && original.getDataFinalSituacao().equals(comparacao.getDataFinalSituacao()));
        }
        return false;
    }

    private boolean hasDatasIguais(VOSituacaoContratoFP original, VOSituacaoContratoFP comparacao) {
        if (hasElementosNulos(original, comparacao) && hasDataInicialMenorFinal(original, comparacao)) {
            return original.getDataInicioSituacao().equals(comparacao.getDataInicioSituacao()) &&
                original.getDataFinalSituacao().equals(comparacao.getDataFinalSituacao());
        }
        return false;
    }

    private boolean hasDataInicialIgual(VOSituacaoContratoFP original, VOSituacaoContratoFP comparacao) {
        if (hasElementosNulos(original, comparacao) && hasDataInicialMenorFinal(original, comparacao)) {
            return original.getDataInicioSituacao().equals(comparacao.getDataInicioSituacao()) &&
                !original.getDataFinalSituacao().equals(comparacao.getDataFinalSituacao());
        }
        return false;
    }

    private boolean hasPeriodoEntrePeriodos(VOSituacaoContratoFP original, VOSituacaoContratoFP comparacao) {
        if (hasElementosNulos(original, comparacao) && hasDataInicialMenorFinal(original, comparacao)) {
            return original.getDataInicioSituacao().before(comparacao.getDataInicioSituacao()) &&
                original.getDataFinalSituacao().after(comparacao.getDataFinalSituacao());
        }
        return false;
    }

    private boolean hasPeriodoEntrePeriodosSemFinalVigencia(VOSituacaoContratoFP original, VOSituacaoContratoFP comparacao) {
        if (original.getDataInicioSituacao() != null && original.getDataFinalSituacao() != null &&
            !original.getDataInicioSituacao().after(original.getDataFinalSituacao()) &&
            comparacao.getDataInicioSituacao() != null && comparacao.getDataFinalSituacao() == null) {

            return (original.getDataInicioSituacao().before(comparacao.getDataInicioSituacao()) &&
                original.getDataFinalSituacao().after(comparacao.getDataInicioSituacao())) ||
                original.getDataInicioSituacao().after(comparacao.getDataInicioSituacao()) ||
                original.getDataInicioSituacao().equals(comparacao.getDataInicioSituacao()) ||
                original.getDataFinalSituacao().equals(comparacao.getDataInicioSituacao());

        }
        return false;
    }


    private void ordenarPorData(Map.Entry<String, List<VOSituacaoContratoFP>> mapaSituacao) {
        Collections.sort(mapaSituacao.getValue(), new Comparator<VOSituacaoContratoFP>() {
            @Override
            public int compare(VOSituacaoContratoFP o1, VOSituacaoContratoFP o2) {
                return ComparisonChain.start().compare(o1.getDataInicioSituacao(), o2.getDataInicioSituacao()).result();
            }
        });
    }

    private VOSituacaoContratoFP buscarMenorData(Map.Entry<String, List<VOSituacaoContratoFP>> str) {
        return Collections.min(str.getValue(), new Comparator<VOSituacaoContratoFP>() {
            @Override
            public int compare(VOSituacaoContratoFP o1, VOSituacaoContratoFP o2) {
                return ComparisonChain.start().compare(o1.getDataInicioSituacao(), o2.getDataInicioSituacao()).result();
            }
        });
    }

    public StreamedContent gerarExcel() {
        try {
            List<String> titulos = Lists.newArrayList();
            titulos.add("Matrícula");
            titulos.add("Nome");
            titulos.add("Início Contrato");
            titulos.add("Final Contrato");
            titulos.add("Situação");
            titulos.add("Início Situação");
            titulos.add("Final Situação");

            List<Object[]> objetos = Lists.newArrayList();

            if (situacoesInconsistentes != null && !situacoesInconsistentes.isEmpty()) {
                for (VOSituacaoContratoFP situacao : situacoesInconsistentes) {
                    Object[] valor = new Object[titulos.size()];

                    valor[0] = situacao.getMatricula();
                    valor[1] = situacao.getNome();
                    valor[2] = DataUtil.getDataFormatada(situacao.getDataInicioContrato());
                    valor[3] = DataUtil.getDataFormatada(situacao.getDataFinalContrato());
                    valor[4] = situacao.getSituacao();
                    valor[5] = DataUtil.getDataFormatada(situacao.getDataInicioSituacao());
                    valor[6] = DataUtil.getDataFormatada(situacao.getDataFinalSituacao());

                    objetos.add(valor);
                }

                Object[] quebraLinha = new Object[1];
                quebraLinha[0] = "";
                objetos.add(quebraLinha);

                Object[] totalizador = new Object[1];
                totalizador[0] = "Total de Registros";
                objetos.add(totalizador);

                Object[] valorTotalizador = new Object[1];
                valorTotalizador[0] = situacoesInconsistentes.size();
                objetos.add(valorTotalizador);
            }

            ExcelUtil excel = new ExcelUtil();
            excel.gerarExcel(tipo != null ? tipo.getDescricao() : " ",
                "Inconsistencia - " + (tipo != null ? tipo.getCodigo() : ""),
                titulos, objetos, " ", true);

            return excel.fileDownload();
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
        return null;
    }

    private void validarTipo() {
        ValidacaoException ve = new ValidacaoException();
        if (tipo == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o tipo de inconsistência.");
        }
        ve.lancarException();
    }

    private void validarSituacoes() {
        ValidacaoException ve = new ValidacaoException();
        if (situacoesInconsistentes != null && situacoesInconsistentes.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrada nenhuma situação para o tipo informado.");
        }
        ve.lancarException();
    }

    public enum TipoSituacaoFuncionaisIncosistente implements EnumComDescricao {
        PRIMEIRA_SITUACAOFUNCIONAL_DIFERENTE_DATAEXERCICIO("Primeira Situação funcional do servidor com data inicial de vigência" +
            " diferente da data de exercício.", 1),
        DATAFINAL_MENOR_DATAINICIAL("Situação com data final menor que inicial.", 2),
        SEM_INICIOVIGENCIA("Situação sem início vigência.", 3),
        SEM_INICIO_E_FINALVIGENCIA("Situação sem início e final de vigência.", 4),
        SEM_FINALVIGENCIA("Mais de uma situção sem final de vigência.", 5),
        SEM_REGISTRO("Período de pelo menos um dia vago entre situações.", 6),
        DATAFINAL_IGUAL_DATAINICIAL("Período com data final igual a data inicial ou com data final entre outro periodo," +
            " ou com início entre e final igual ao final de outro período.", 7),
        DATAS_IGUAIS("Período com datas de inicio e fim iguais.", 8),
        DATAINICIAL_IGUAL("Período com datas de inicio iguas e finais diferentes.", 9),
        ENTRE_PERIODO("Período contido em outros períodos.", 10),
        ENTRE_PERIODO_SEM_FINALVIGENCIA("Período sem final de vigência com outro período contido nele.", 11),
        DATAFINAL_MAIOR_DATAATUAL("Período com data final maior que a data atual.", 12);

        String descricao;
        private Integer codigo;

        TipoSituacaoFuncionaisIncosistente(String descricao, Integer codigo) {
            this.descricao = descricao;
            this.codigo = codigo;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public Integer getCodigo() {
            return codigo;
        }

        public void setCodigo(Integer codigo) {
            this.codigo = codigo;
        }

        @Override
        public String toString() {
            return getCodigo() + " - " + getDescricao();
        }
    }

}
