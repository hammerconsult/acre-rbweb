package br.com.webpublico.negocios.rh.executores;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.pccr.ModoGeracaoProgressao;
import br.com.webpublico.entidadesauxiliares.ProgressaoAutomatica;
import br.com.webpublico.enums.rh.InconsistenciaProgressao;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import org.joda.time.YearMonth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GerarProgressoesExecutor implements Callable<AssistenteBarraProgresso> {

    private final Logger logger = LoggerFactory.getLogger(GerarProgressoesExecutor.class);
    private final TipoProvimentoFacade tipoProvimentoFacade;
    private final GerarProgressoesFacade gerarProgressoesFacade;
    private final ContratoFPFacade contratoFPFacade;
    private final ProvimentoFPFacade provimentoFPFacade;
    private final EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    private final PlanoCargosSalariosFacade planoCargosSalariosFacade;
    private final EnquadramentoPCSFacade enquadramentoPCSFacade;
    private final VinculoFPFacade vinculoFPFacade;
    private final ProgressaoPCSFacade progressaoPCSFacade;
    private final AfastamentoFacade afastamentoFacade;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Date dataInicioVigencia;
    private ModoGeracaoProgressao modoGeracao;
    private PlanoCargosSalarios planoCargosSalarios;
    private CategoriaPCS categoriaPCS;
    private ProgressaoPCS progressaoPCS;
    private VinculoFP vinculoFP;
    private Boolean multiplasLetras;
    private Date competenciaReferencia;
    private Date dataDoSistema;
    private List<BigDecimal> listaContratos;
    private boolean alongouInicioProgressao;
    private boolean alongouInicioProgressaoMultiplasLetras;

    public GerarProgressoesExecutor(GerarProgressoesFacade gerarProgressoesFacade, TipoProvimentoFacade tipoProvimentoFacade,
                                    ContratoFPFacade contratoFPFacade, ProvimentoFPFacade provimentoFPFacade,
                                    EnquadramentoFuncionalFacade enquadramentoFuncionalFacade, PlanoCargosSalariosFacade planoCargosSalariosFacade,
                                    VinculoFPFacade vinculoFPFacade, ProgressaoPCSFacade progressaoPCSFacade, EnquadramentoPCSFacade enquadramentoPCSFacade,
                                    AfastamentoFacade afastamentoFacade) {
        this.gerarProgressoesFacade = gerarProgressoesFacade;
        this.tipoProvimentoFacade = tipoProvimentoFacade;
        this.contratoFPFacade = contratoFPFacade;
        this.provimentoFPFacade = provimentoFPFacade;
        this.enquadramentoFuncionalFacade = enquadramentoFuncionalFacade;
        this.planoCargosSalariosFacade = planoCargosSalariosFacade;
        this.vinculoFPFacade = vinculoFPFacade;
        this.progressaoPCSFacade = progressaoPCSFacade;
        this.enquadramentoPCSFacade = enquadramentoPCSFacade;
        this.afastamentoFacade = afastamentoFacade;
    }

    public Future<AssistenteBarraProgresso> execute(HierarquiaOrganizacional hierarquiaOrganizacional, Date dataInicioVigencia,
                                                    ModoGeracaoProgressao modoGeracao, PlanoCargosSalarios planoCargosSalarios,
                                                    CategoriaPCS categoriaPCS, ProgressaoPCS progressaoPCS, VinculoFP vinculoFP,
                                                    Boolean multiplasLetras, Date dataDoSistema, List<BigDecimal> listaContratos,
                                                    Date competenciaReferencia, AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
        this.dataInicioVigencia = dataInicioVigencia;
        this.modoGeracao = modoGeracao;
        this.planoCargosSalarios = planoCargosSalarios;
        this.categoriaPCS = categoriaPCS;
        this.progressaoPCS = progressaoPCS;
        this.vinculoFP = vinculoFP;
        this.multiplasLetras = multiplasLetras;
        this.dataDoSistema = dataDoSistema;
        this.listaContratos = listaContratos;
        this.competenciaReferencia = competenciaReferencia;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<AssistenteBarraProgresso> submit = executorService.submit(this);
        executorService.shutdown();
        return submit;
    }

    public AssistenteBarraProgresso call() throws Exception {
        try {
            assistenteBarraProgresso.setSelecionado(gerarProgressoesAutomaticas());
            return assistenteBarraProgresso;
        } catch (Exception ex) {
            logger.error("Erro ao gerar Progressões Automáticas {}", ex);
        }
        return null;
    }

    public List<ProgressaoAutomatica> gerarProgressoesAutomaticas() {
        List<ProgressaoAutomatica> toReturn = new ArrayList<>();
        TipoProvimento tipoProvimento = tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.PROVIMENTO_PROGRESSAO);

        for (BigDecimal idContrato : listaContratos) {
            ContratoFP contrato = contratoFPFacade.recuperarSomenteContrato(idContrato.longValue());
            ProvimentoFP provimentoFP = provimentoFPFacade.recuperaProvimentoByTipoAndDataProvimentoDesc(contrato, tipoProvimento);
            ProgressaoAutomatica pa = gerarProgressaoServidor(contrato, provimentoFP != null ? DataUtil.dateToLocalDate(provimentoFP.getDataProvimento()) : DataUtil.dateToLocalDate(contrato.getInicioVigencia()), dataInicioVigencia, modoGeracao, multiplasLetras);
            if (pa != null) {
                toReturn.add(pa);
            }
            assistenteBarraProgresso.conta();
        }
        return toReturn;
    }

    private ProgressaoAutomatica gerarProgressaoServidor(ContratoFP contrato, LocalDate inicio, Date dataInicioVigencia, ModoGeracaoProgressao modoGeracao, Boolean multiplasLetras) {
        alongouInicioProgressao = false;
        ProgressaoAutomatica toReturn = null;
        LocalDate dataOperacao = DataUtil.dateToLocalDate(dataDoSistema);

        EnquadramentoFuncional enquadramentoFuncionalDaProgressao = enquadramentoFuncionalFacade.recuperaEnquadramentoFuncionalPorContratoEDataProvimentoEDataVigencia(contrato, DataUtil.localDateToDate(inicio.plusDays(1)), dataDoSistema);
        EnquadramentoFuncional enquadramentoFuncionalUltimo = enquadramentoFuncionalFacade.recuperaEnquadramentoFuncionalPorContratoEDataProvimentoEDataVigencia(contrato, dataDoSistema, dataDoSistema);
        EnquadramentoFuncional enquadramentoFuncionalCorreto = getEnquadramentoCorreto(enquadramentoFuncionalDaProgressao, enquadramentoFuncionalUltimo);

        EnquadramentoFuncional enquadramentoFuncionalParaProgressaoAutomatica = buscarEnquadramentoQueConsideraParaProgressaoAutomatica(enquadramentoFuncionalCorreto, contrato);

        if (enquadramentoFuncionalParaProgressaoAutomatica != null) {
            Integer meses = getMesesProgressao(enquadramentoFuncionalCorreto);
            if (meses <= 0) {
                return null;
            }

            LocalDate fim = DataUtil.dateToLocalDate(enquadramentoFuncionalParaProgressaoAutomatica.getInicioVigencia());
            fim = fim.plusMonths(meses);
            fim = verificarAfastamentosNoPeriodo(contrato, DataUtil.dateToLocalDate(enquadramentoFuncionalParaProgressaoAutomatica.getInicioVigencia()), fim, false);

            Boolean atingiuLimiteProgresao = false;
            ProgressaoPCS progressaoPcsDeveriaEstar = null;
            if (modoGeracao.equals(ModoGeracaoProgressao.MODO_DATA_INICIOVIGENCIA_CONTRATO)) {

                Integer totalDiasTempoServico = vinculoFPFacade.getTotalDiasTempoServico(contrato, false, false, dataDoSistema);
                Integer numeroProgressaoDeveriaEstar = calcularProgressaoAtual(totalDiasTempoServico, meses);
                ProgressaoPCS progressaoPCSPai = enquadramentoFuncionalCorreto.getProgressaoPCS().getSuperior();
                progressaoPCSPai = progressaoPCSFacade.recuperar(progressaoPCSPai.getId());
                List<ProgressaoPCS> filhos = progressaoPCSPai.getFilhos();
                ordenarProgressoes(filhos);
                if (filhos.size() < numeroProgressaoDeveriaEstar) {
                    atingiuLimiteProgresao = true;
                    progressaoPcsDeveriaEstar = filhos.get(filhos.size() - 1);
                }

                if (progressaoPcsDeveriaEstar == null) {
                    for (ProgressaoPCS filho : filhos) {
                        if (filho.getOrdem() == numeroProgressaoDeveriaEstar) {
                            progressaoPcsDeveriaEstar = filho;
                            break;
                        }
                    }
                }
            }

            Date data = gerarProgressoesFacade.getDataRegularizado(enquadramentoFuncionalParaProgressaoAutomatica);
            if (data != null) {
                LocalDate dataR = DataUtil.dateToLocalDate(data);
                if (dataR != null && dataR.isAfter(inicio)) {
                    fim = dataR.plusMonths(meses);
                }
            }

            YearMonth anoMes = new YearMonth(DataUtil.localDateToDate(fim));
            YearMonth anoMesOp = new YearMonth(DataUtil.localDateToDate(dataOperacao));
            YearMonth anoMesCompetenciaReferencia = new YearMonth(competenciaReferencia);

            if (anoMes.isBefore(anoMesCompetenciaReferencia) || anoMes.isEqual(anoMesCompetenciaReferencia) || (anoMes.isAfter(anoMesCompetenciaReferencia) && alongouInicioProgressao)) {
                if (enquadramentoFuncionalCorreto != null) {
                    ProgressaoPCS proximaProgressao = null;
                    if (progressaoPcsDeveriaEstar == null) {
                        List<ProgressaoPCS> listProgPCS = progressaoPCSFacade.buscaProgressoesNoEnquadramentoPCS(enquadramentoFuncionalCorreto.getProgressaoPCS().getSuperior());

                        if (listProgPCS.contains(enquadramentoFuncionalCorreto.getProgressaoPCS())) {
                            Integer posicaoLista = 1;
                            Integer ultimaPosicaoLista = null;

                            if (modoGeracao.equals(ModoGeracaoProgressao.MODO_PADRAO_BASE_ULTIMO_ENQUADRAMENTO) && multiplasLetras) {
                                LocalDate fimEnquadramento = fim;
                                fimEnquadramento = fimEnquadramento.plusMonths(meses);
                                fimEnquadramento = verificarAfastamentosNoPeriodo(contrato, fim, fimEnquadramento, true);
                                while (new YearMonth(DataUtil.localDateToDate(fimEnquadramento)).isBefore(anoMesOp)) {
                                    posicaoLista += 1;
                                    fimEnquadramento = verificarAfastamentosNoPeriodo(contrato, fimEnquadramento, fimEnquadramento.plusMonths(meses), true);
                                }
                                if (posicaoLista > 1 && alongouInicioProgressaoMultiplasLetras) {
                                    alongouInicioProgressao = true;
                                }

                                Integer tamanhoLista = listProgPCS.indexOf(enquadramentoFuncionalCorreto.getProgressaoPCS()) + posicaoLista;
                                if (listProgPCS.size() < tamanhoLista) {
                                    atingiuLimiteProgresao = true;
                                    ultimaPosicaoLista = listProgPCS.size() - 1;
                                }
                            }

                            Integer pos = ultimaPosicaoLista != null ? ultimaPosicaoLista : listProgPCS.indexOf(enquadramentoFuncionalCorreto.getProgressaoPCS()) + posicaoLista;
                            if (pos < listProgPCS.size()) {
                                proximaProgressao = listProgPCS.get(pos);
                            }
                        }
                    } else {
                        proximaProgressao = progressaoPcsDeveriaEstar;
                    }

                    if (proximaProgressao != null) {
                        LocalDate dataFinalEnquadramento;
                        if (dataInicioVigencia != null) {
                            fim = DataUtil.dateToLocalDate(dataInicioVigencia).minusDays(1);
                            dataFinalEnquadramento = fim;
                        } else {
                            dataFinalEnquadramento = fim.minusDays(1);
                        }
                        Date dataFinalVigenciaEnquadramentoCorreto = enquadramentoFuncionalCorreto.getFinalVigencia();
                        enquadramentoFuncionalCorreto.setFinalVigencia(DataUtil.localDateToDate(dataFinalEnquadramento));
                        EnquadramentoFuncional novoEnquadramentoFuncional = getEnquadramento(enquadramentoFuncionalCorreto, proximaProgressao, dataInicioVigencia != null ? fim.plusDays(1) : fim);
                        EnquadramentoPCS pcs = enquadramentoPCSFacade.recuperaValor(enquadramentoFuncionalCorreto.getCategoriaPCS(), proximaProgressao, DataUtil.localDateToDate(dataOperacao));
                        novoEnquadramentoFuncional.setVencimentoBase(pcs != null ? pcs.getVencimentoBase() : BigDecimal.ZERO);

                        EnquadramentoPCS valorPcs = enquadramentoPCSFacade.recuperaValor(enquadramentoFuncionalCorreto.getCategoriaPCS(), enquadramentoFuncionalCorreto.getProgressaoPCS(), DataUtil.localDateToDate(dataOperacao));
                        enquadramentoFuncionalCorreto.setVencimentoBase(valorPcs != null ? valorPcs.getVencimentoBase() : BigDecimal.ZERO);
                        //TODO Não é permitido progredir para a mesma Progressão PCCR e nem retroagir a progressão diminuindo salário do servidor
                        if (!novoEnquadramentoFuncional.getProgressaoPCS().equals(enquadramentoFuncionalCorreto.getProgressaoPCS())
                            && novoEnquadramentoFuncional.getProgressaoPCS().getOrdem() > enquadramentoFuncionalCorreto.getProgressaoPCS().getOrdem()) {
                            if (enquadramentosConcomitantes(novoEnquadramentoFuncional, enquadramentoFuncionalCorreto) || enquadramentosConcomitantes(novoEnquadramentoFuncional, enquadramentoFuncionalParaProgressaoAutomatica)) {
                                toReturn = new ProgressaoAutomatica(novoEnquadramentoFuncional.getInicioVigencia(), novoEnquadramentoFuncional.getFinalVigencia(), enquadramentoFuncionalCorreto, novoEnquadramentoFuncional, enquadramentoFuncionalParaProgressaoAutomatica, atingiuLimiteProgresao, true, false, null, 0, InconsistenciaProgressao.PERIODOS_ENQUADRAMENTO_CONCOMITANTES, meses);
                            } else if (fim.plusYears(5).isBefore(dataOperacao)) {
                                toReturn = new ProgressaoAutomatica(novoEnquadramentoFuncional.getInicioVigencia(), novoEnquadramentoFuncional.getFinalVigencia(), enquadramentoFuncionalCorreto, novoEnquadramentoFuncional, enquadramentoFuncionalParaProgressaoAutomatica, atingiuLimiteProgresao, true, false, InconsistenciaProgressao.MAIS_5_ANOS_SEM_PROGRESSAO.getDescricao(), 0, InconsistenciaProgressao.MAIS_5_ANOS_SEM_PROGRESSAO, meses);
                            } else if (anoMes.isAfter(anoMesCompetenciaReferencia) && alongouInicioProgressao) {
                                toReturn = new ProgressaoAutomatica(novoEnquadramentoFuncional.getInicioVigencia(), novoEnquadramentoFuncional.getFinalVigencia(), enquadramentoFuncionalCorreto, novoEnquadramentoFuncional, enquadramentoFuncionalParaProgressaoAutomatica, atingiuLimiteProgresao, true, false, InconsistenciaProgressao.ALONGAMENTO_ENQUADRAMENTO_POR_AFASTAMENTO.getDescricao(), 0, InconsistenciaProgressao.ALONGAMENTO_ENQUADRAMENTO_POR_AFASTAMENTO, meses);
                            } else {
                                toReturn = new ProgressaoAutomatica(novoEnquadramentoFuncional.getInicioVigencia(), novoEnquadramentoFuncional.getFinalVigencia(), enquadramentoFuncionalCorreto, novoEnquadramentoFuncional, enquadramentoFuncionalParaProgressaoAutomatica, atingiuLimiteProgresao, false, false, null, 0, null, meses);
                            }
                            enquadramentoFuncionalCorreto.setFinalVigencia(dataFinalVigenciaEnquadramentoCorreto);
                        }
                    } else {
                        logger.debug("Servidor " + contrato + " já atingiu o limite de de progressões.");
                    }
                }
            }
        } else {
            logger.error("Servidor " + contrato + " não possui enquadramentos que possam ser considerados para a progressão.");
        }
        return toReturn;
    }

    private EnquadramentoFuncional getEnquadramentoCorreto(EnquadramentoFuncional eProvimento, EnquadramentoFuncional enquadramentoFuncionalVigente) {

        if (eProvimento != null && eProvimento.getId() != null && enquadramentoFuncionalVigente != null && enquadramentoFuncionalVigente.getId() != null) {
            if (eProvimento.getInicioVigencia().after(enquadramentoFuncionalVigente.getInicioVigencia())) {
                return eProvimento;
            } else {
                return enquadramentoFuncionalVigente;
            }

        }
        if (enquadramentoFuncionalVigente != null && enquadramentoFuncionalVigente.getId() != null) {
            return enquadramentoFuncionalVigente;
        }
        if (eProvimento != null && eProvimento.getId() != null) {
            return eProvimento;
        }
        return null;
    }

    private EnquadramentoFuncional buscarEnquadramentoQueConsideraParaProgressaoAutomatica(EnquadramentoFuncional enquadramentoFuncionalCorreto, ContratoFP contrato) {

        if (enquadramentoFuncionalCorreto != null && enquadramentoFuncionalCorreto.getConsideraParaProgAutomat()) {
            return enquadramentoFuncionalCorreto;
        }
        EnquadramentoFuncional enquadramentoFuncionalConsiderandoProgressao = enquadramentoFuncionalFacade.recuperaUltimoEnquadramentoFuncionalQueConsideraProgressaoAutomaticaPorContrato(contrato, dataDoSistema);
        if (enquadramentoFuncionalConsiderandoProgressao != null) {
            return enquadramentoFuncionalConsiderandoProgressao;
        }
        return null;
    }

    private Integer getMesesProgressao(EnquadramentoFuncional enquadramentoFuncionalUltimo) {
        if (enquadramentoFuncionalUltimo == null) {
            return 0;
        }
        PlanoCargosSalarios planoCargosSalarios = enquadramentoFuncionalUltimo.getCategoriaPCS().getPlanoCargosSalarios();
        CategoriaPCS categoriaPcs = enquadramentoFuncionalUltimo.getCategoriaPCS();
        ProgressaoPCS progressaoPCS = enquadramentoFuncionalUltimo.getProgressaoPCS();
        return planoCargosSalariosFacade.getMesesProgressaoPorPrioridade(planoCargosSalarios, categoriaPcs, progressaoPCS, dataDoSistema);
    }

    private Integer calcularProgressaoAtual(Integer diasTempoServico, Integer mesesProgressao) {
        BigDecimal mesesTempoServico = BigDecimal.valueOf(diasTempoServico / 30);
        BigDecimal totalProgressoesDeveriaTer = mesesTempoServico.divide(BigDecimal.valueOf(mesesProgressao), 2, RoundingMode.HALF_UP); //Total de vezes que o servidor deveria progredir.

        BigDecimal remainder = totalProgressoesDeveriaTer.remainder(BigDecimal.ONE);
        if (remainder.compareTo(BigDecimal.ZERO) > 0) {
            totalProgressoesDeveriaTer = totalProgressoesDeveriaTer.add(BigDecimal.ONE);
        }
        return Integer.valueOf(totalProgressoesDeveriaTer.intValue());
    }

    public EnquadramentoFuncional getEnquadramento(EnquadramentoFuncional enquadramentoFuncional, ProgressaoPCS progressaoPCS, LocalDate dateInicio) {
        EnquadramentoFuncional e = new EnquadramentoFuncional();
        e.setCategoriaPCS(enquadramentoFuncional.getCategoriaPCS());
        e.setProgressaoPCS(progressaoPCS);
        e.setContratoServidor(enquadramentoFuncional.getContratoServidor());
        e.setDataCadastro(new Date());
        e.setInicioVigencia(DataUtil.localDateToDate(dateInicio));
        e.setConsideraParaProgAutomat(Boolean.TRUE);
        return e;
    }

    private Boolean enquadramentosConcomitantes(EnquadramentoFuncional enquadramentoFuncionalNovo, EnquadramentoFuncional enquadramentoFuncionalAntigo) {
        return enquadramentoFuncionalNovo.getInicioVigencia().compareTo(enquadramentoFuncionalAntigo.getInicioVigencia()) <= 0 || enquadramentoFuncionalAntigo.getFinalVigencia() == null
            || enquadramentoFuncionalNovo.getInicioVigencia().compareTo(enquadramentoFuncionalAntigo.getFinalVigencia()) <= 0;
    }

    private void ordenarProgressoes(List<ProgressaoPCS> filhos) {
        Collections.sort(filhos, new Comparator<ProgressaoPCS>() {
            @Override
            public int compare(ProgressaoPCS o1, ProgressaoPCS o2) {
                if (o1.getOrdem() != null && o2.getOrdem() != null) {
                    return o1.getOrdem().compareTo(o2.getOrdem());
                }
                return 0;
            }
        });
    }

    private Integer tratarAtributoNuloWithZero(Integer objeto) {
        return (objeto != null) ? objeto : 0;
    }

    private LocalDate verificarAfastamentosNoPeriodo(ContratoFP contrato, java.time.LocalDate dataInicial, java.time.LocalDate dataEnquadramento, boolean multiplasLetras) {
        for (Afastamento afastamento : afastamentoFacade.buscarAfatamentosPorPeriodoQueNaoPermitemProgressao(contrato, DataUtil.localDateToDate(dataInicial), DataUtil.localDateToDate(dataEnquadramento))) {
            LocalDate inicio = DataUtil.dateToLocalDate(afastamento.getInicio());
            LocalDate fim = DataUtil.dateToLocalDate(afastamento.getTermino());
            if (inicio.isBefore(dataInicial)) {
                inicio = dataInicial;
            }
            if (fim.isAfter(dataEnquadramento)) {
                fim = dataEnquadramento;
            }
            int diasAfastado = DataUtil.diasEntre(inicio, fim);
            int diasCarencia = tratarAtributoNuloWithZero(afastamento.getTipoAfastamento().getCarenciaAlongamento());
            int total = diasAfastado - diasCarencia;
            if (total > 0) {
                dataEnquadramento = dataEnquadramento.plusDays(total);
                if (multiplasLetras) {
                    alongouInicioProgressaoMultiplasLetras = true;
                } else {
                    alongouInicioProgressao = true;
                }
            }
        }
        return dataEnquadramento;
    }
}
