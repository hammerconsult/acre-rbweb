package br.com.webpublico.negocios;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.dirf.DirfInfoComplementares;
import br.com.webpublico.entidades.rh.dirf.DirfPessoa;
import br.com.webpublico.entidades.rh.dirf.DirfVinculoFP;
import br.com.webpublico.entidades.rh.dirf.DirfVinculoFPPessoa;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.entidadesauxiliares.rh.ComprovanteRendimentosCedula;
import br.com.webpublico.entidadesauxiliares.rh.ComprovanteRendimentosIsentos;
import br.com.webpublico.entidadesauxiliares.rh.ComprovanteRendimentosPagosConferencia;
import br.com.webpublico.entidadesauxiliares.rh.ComprovanteRendimentosTributaveis;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by peixe on 20/08/2015.
 */
@Stateless
public class ComprovanteRendimentosFacade extends AbstractReport implements Serializable {

    public static final String COMPROVANTE_RENDIMENTOS_PAGOS_JASPER = "ComprovanteRendimentosPagos.jasper";
    public static final String COMPROVANTE_RENDIMENTOS_CEDULA_JASPER = "ComprovanteRendimentosPagosCedulaC.jasper";
    public static final String COMPROVANTE_RENDIMENTOS_CONFERENCIA_JASPER = "ComprovanteRendimentosPagosConferencia.jasper";
    public static final String RENDIMENTO_DO_TRABALHO_ASSALARIADO = "Rendimento do trabalho assalariado";
    public static final String PENSAO_JUDICIAL = "Pensão Judicial";

    private static final Logger logger = LoggerFactory.getLogger(ComprovanteRendimentosFacade.class);
    private static final long MODULO_EXPORTACAO_CODIGO = 3;
    private static final long GRUPO_EXPORTACAO_CODIGO_SALARIO_FAMILIA = 16;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private GrupoExportacaoFacade grupoExportacaoFacade;
    @EJB
    private ModuloExportacaoFacade moduloExportacaoFacade;
    @EJB
    private PensaoAlimenticiaFacade pensaoAlimenticiaFacade;
    @EJB
    private FuncoesFolhaFacade funcoesFolhaFacade;
    @EJB
    private DirfVinculoFPFacade dirfVinculoFPFacade;

    private List<ComprovanteRendimentosPagosConferencia> rendimentosPagos = Lists.newLinkedList();

    protected EntityManager getEntityManager() {
        return em;
    }

    public RelatorioDTO montarRelatorioDTO(AssistenteComprovanteRendimentos assistente) {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("BRASAO_FAZENDA" , "images/");
        return montarAssistente(assistente.getVinculos(), assistente.getAnoCalendario(), dto);
    }

    public RelatorioDTO montarAssistente(List<VinculoFP> vinculos, Exercicio anoCalendario, RelatorioDTO dto) {
        rendimentosPagos.clear();
        List<ComprovanteRendimentosIRRFFonte> rendimentosIRRFFontes = Lists.newLinkedList();
        preencherComprovanteRendimentoIRRFFonte(vinculos, rendimentosIRRFFontes, anoCalendario);
        if (!rendimentosIRRFFontes.isEmpty() || !rendimentosPagos.isEmpty()){
            dto.adicionarParametro("rendimentosIRRFFontes", AssistenteComprovanteRendimentos.comprovanteRendimentosListDTO(rendimentosIRRFFontes));
            dto.adicionarParametro("rendimentosPagos", AssistenteComprovanteRendimentos.comprovanteRendimentosPagosListDTO(rendimentosPagos));
            return dto;
        }
        return null;
    }

    public byte[] gerarRelatorioPortal(Long idVinculo, Integer ano) throws IOException, JRException {
        Long idPessoa = pessoaFacade.buscarPessoaFisicaPorVinculoFP(idVinculo);
        Pessoa pessoa = pessoaFacade.recuperarSimples(idPessoa);

        List<VinculoFP> vinculos = buscarVinculosPessoaVigentePorAno(pessoa, getExercicioFacade().getExercicioPorAno(ano));
        if (vinculos != null && !vinculos.isEmpty()) {
            ByteArrayOutputStream bytes = montarRelatorio(vinculos, getExercicioFacade().getExercicioPorAno(ano));
            return bytes != null ? bytes.toByteArray() : null;
        }
        return null;
    }

    private void definirPensaoJudicial(ComprovanteRendimentosIRRFFonte comprovanteRendimentosIRRFFonte, VinculoFP vinculoFP, Exercicio anoCalendario) {
        List<PensaoJudicialRetidoFonte> pensoesJudiciais = new LinkedList<>();
        List<BeneficioPensaoAlimenticia> beneficioPensoes = pensaoAlimenticiaFacade.buscarBeneficiarioPensaoAlimenticiaPorAnoAndVinculo(vinculoFP.getContratoFP(), anoCalendario);
        Integer contador = 1;

        for (BeneficioPensaoAlimenticia beneficioPensao : beneficioPensoes) {
            PensaoJudicialRetidoFonte pensaoRetidoFonte = exportarObjetoRelatorio(contador, vinculoFP, beneficioPensao, anoCalendario);

            if (!isExistsBeneficiarioAdicionado(pensoesJudiciais, pensaoRetidoFonte)) {
                pensoesJudiciais.add(pensaoRetidoFonte);
                contador++;
            } else {
                buscarAndAtualizarValorPensao(pensoesJudiciais, pensaoRetidoFonte);
            }

        }
        if (comprovanteRendimentosIRRFFonte.getPensoesJudiciais() == null) {
            comprovanteRendimentosIRRFFonte.setPensoesJudiciais(Lists.<PensaoJudicialRetidoFonte>newArrayList());
        }
        comprovanteRendimentosIRRFFonte.getPensoesJudiciais().addAll(pensoesJudiciais);
    }

    public Boolean isExistsBeneficiarioAdicionado(List<PensaoJudicialRetidoFonte> pensoesFonte, PensaoJudicialRetidoFonte pensaoAlimenticiaRetidoFonte) {
        if (pensoesFonte == null || pensoesFonte.isEmpty()) {
            return false;
        }

        for (PensaoJudicialRetidoFonte pensao : pensoesFonte) {
            if (pensao.getPessoaFisica().equals(pensaoAlimenticiaRetidoFonte.getPessoaFisica())) {
                return true;
            }
        }
        return false;
    }

    private void buscarAndAtualizarValorPensao(List<PensaoJudicialRetidoFonte> pensoesFonte, PensaoJudicialRetidoFonte pensao) {
        carregarValorAndEventoFPsToBeneficiario(pensoesFonte, pensao);
    }

    private void carregarValorAndEventoFPsToBeneficiario(List<PensaoJudicialRetidoFonte> pensoesFonte, PensaoJudicialRetidoFonte pensao) {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (PensaoJudicialRetidoFonte selecionado : pensoesFonte) {
            if (selecionado.getPessoaFisica().equals(pensao.getPessoaFisica())
                && !selecionado.hasEventoFP(pensao.getEventosFps())) {
                selecionado.adicionarEventos(pensao.getEventosFps());
                definirTotalAgrupadoPorEventoFpToBeneficiario(pensao, valorTotal, selecionado);
            }
        }
    }

    private void definirTotalAgrupadoPorEventoFpToBeneficiario(PensaoJudicialRetidoFonte pensao, BigDecimal valorTotal, PensaoJudicialRetidoFonte selecionado) {
        valorTotal = getValorTotal(valorTotal, selecionado.getValor());
        valorTotal = getValorTotal(valorTotal, pensao.getValor());
        if (valorTotal != null && valorTotal.compareTo(BigDecimal.ZERO) > 0) {
            selecionado.setValor(valorTotal);
        }
    }


    private PensaoJudicialRetidoFonte exportarObjetoRelatorio(Integer contador, VinculoFP vinculo, BeneficioPensaoAlimenticia beneficiario, Exercicio e) {
        PensaoJudicialRetidoFonte pensao = new PensaoJudicialRetidoFonte();
        pensao.setDescricao(contador + ". " + beneficiario.getPessoaFisicaBeneficiario().getNome());
        pensao.setTipo(beneficiario.getEventoFP().getDescricao());
        pensao.setPessoaFisica(beneficiario.getPessoaFisicaBeneficiario());
        pensao.setCpf("");
        pensao.setInstituidor(vinculo);
        pensao.adicionarEvento(beneficiario.getEventoFP());
        EventoTotalItemFicha evento = fichaFinanceiraFPFacade.recuperarTotalItemFichaPorVinculoFPAnoEvento(vinculo, e.getAno(), beneficiario.getEventoFP());
        if (evento != null) {
            pensao.setValor(evento.getTotal());
        }
        return pensao;
    }

    public void carregarInformacaoComprovanteRendimentos(ComprovanteRendimentosPagosConferencia comprovanteConferencia, Integer ano, BigDecimal valorDecimoTerceiro) {
        definirAnoCalendarioAndExercicio(comprovanteConferencia, ano);
        carregarRendimentosPagosAndIsentos(comprovanteConferencia, valorDecimoTerceiro);
    }

    private void carregarRendimentosPagosAndIsentos(ComprovanteRendimentosPagosConferencia comprovanteConferencia, BigDecimal valorDecimoTerceiro) {
        List<ComprovanteRendimentosTributaveis> rendimentosTributaveis = Lists.newLinkedList();
        List<ComprovanteRendimentosIsentos> rendimentosIsentos = Lists.newLinkedList();

        for (Mes mes : Mes.values()) {
            buscarRendimentosToBeneficiarioPorEventoFp(comprovanteConferencia, rendimentosTributaveis, rendimentosIsentos, mes);
        }
        rendimentosTributaveis.add(new ComprovanteRendimentosTributaveis("Décimo Terceiro ", valorDecimoTerceiro));

        comprovanteConferencia.setRendimentosTributaveis(rendimentosTributaveis);
        comprovanteConferencia.setRendimentosIsentos(rendimentosIsentos);
    }

    private void buscarRendimentosToBeneficiarioPorEventoFp(ComprovanteRendimentosPagosConferencia comprovanteConferencia, List<ComprovanteRendimentosTributaveis> rendimentosTributaveis, List<ComprovanteRendimentosIsentos> rendimentosIsentos, Mes mes) {
        BigDecimal totalTributavel = BigDecimal.ZERO;
        BigDecimal totalIsento = BigDecimal.ZERO;
        if (comprovanteConferencia.getEventosFps() != null && !comprovanteConferencia.getEventosFps().isEmpty()) {
            for (EventoFP eventoFP : comprovanteConferencia.getEventosFps()) {
                BigDecimal valorTributavel = buscarRendimentosTributaveisBeneficiario(comprovanteConferencia, rendimentosTributaveis, mes, eventoFP);
                totalTributavel = getValorTotal(totalTributavel, valorTributavel);
                BigDecimal valorIsento = buscarRendimentosIsentosBeneficiario(comprovanteConferencia, rendimentosIsentos, mes, eventoFP);
                totalIsento = getValorTotal(totalIsento, valorIsento);

            }
            definirRendimentosTributaveis(rendimentosTributaveis, mes, totalTributavel);
            definirRendimentosIsentos(rendimentosIsentos, mes, totalIsento);

        }
    }

    private BigDecimal getValorTotal(BigDecimal total, BigDecimal valor) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) > 0) {
            total = total.add(valor);
        }
        return total;
    }

    private BigDecimal buscarRendimentosTributaveisBeneficiario(ComprovanteRendimentosPagosConferencia comprovanteConferencia, List<ComprovanteRendimentosTributaveis> rendimentosTributaveis, Mes mes, EventoFP eventoFP) {
        PessoaFisica pf = comprovanteConferencia.getInstituidor().getMatriculaFP().getPessoa();
        Integer ano = comprovanteConferencia.getAnoCalendario();

        List<TipoFolhaDePagamento> folhas = TipoFolhaDePagamento.getFolhasDePagamentoSemDecimoTerceiro();

        BigDecimal rendimentoMes = pensaoAlimenticiaFacade.buscarValorEventoFPsPorMes(pf, comprovanteConferencia.getInstituidor(), mes, ano, folhas, eventoFP);

        return rendimentoMes;

    }

    private void definirRendimentosTributaveis(List<ComprovanteRendimentosTributaveis> rendimentosTributaveis, Mes mes, BigDecimal totalMes) {
        if (totalMes != null && totalMes.compareTo(BigDecimal.ZERO) > 0) {
            rendimentosTributaveis.add(new ComprovanteRendimentosTributaveis(mes.getDescricao(), totalMes));
        } else {
            rendimentosTributaveis.add(new ComprovanteRendimentosTributaveis(mes.getDescricao(), BigDecimal.ZERO));

        }
    }

    private BigDecimal buscarRendimentosIsentosBeneficiario(ComprovanteRendimentosPagosConferencia comprovanteConferencia, List<ComprovanteRendimentosIsentos> rendimentosIsentos, Mes mes, EventoFP eventoFP) {
        ModuloExportacao moduloExportacao = moduloExportacaoFacade.recuperaModuloExportacaoPorCodigo(MODULO_EXPORTACAO_CODIGO);
        GrupoExportacao grupo = grupoExportacaoFacade.recuperaGrupoExportacaoPorCodigoEModuloExportacao(GRUPO_EXPORTACAO_CODIGO_SALARIO_FAMILIA, moduloExportacao);
        TipoFolhaDePagamento[] tipos = new TipoFolhaDePagamento[TipoFolhaDePagamento.getFolhasDePagamentoSemDecimoTerceiro().size()];
        tipos = TipoFolhaDePagamento.getFolhasDePagamentoSemDecimoTerceiro().toArray(tipos);

        ValorFinanceiroRH valorSalarioFamilia = fichaFinanceiraFPFacade.recuperarValorPorMesAndAnoPorVinculoFpEModuloExportacao(comprovanteConferencia.getInstituidor().getId(), mes, comprovanteConferencia.getAnoCalendario(), grupo, moduloExportacao, tipos);

        return valorSalarioFamilia.getTotalVantagem();
    }

    private void definirRendimentosIsentos(List<ComprovanteRendimentosIsentos> rendimentosIsentos, Mes mes, BigDecimal totalMes) {
        if (totalMes != null && totalMes.compareTo(BigDecimal.ZERO) > 0) {
            rendimentosIsentos.add(new ComprovanteRendimentosIsentos(mes.getDescricao(), totalMes));
        } else {
            rendimentosIsentos.add(new ComprovanteRendimentosIsentos(mes.getDescricao(), BigDecimal.ZERO));
        }
    }

    private void definirAnoCalendarioAndExercicio(ComprovanteRendimentosPagosConferencia comprovanteCoferencia, Integer ano) {
        Exercicio anoCalendario = getExercicioFacade().getExercicioPorAno(ano);
        Exercicio anoExercicio = getExercicioFacade().getExercicioPorAno(ano + 1);
        comprovanteCoferencia.setAnoCalendario(anoCalendario.getAno());
        comprovanteCoferencia.setAnoExercicio(anoExercicio.getAno());
    }

    public BigDecimal parametrizarValorParaAposentados(VinculoFP vinculoFP, Exercicio anoCalendario, BigDecimal valor, Mes mes) {
        vinculoFP.setMes(mes.getNumeroMes());
        vinculoFP.setAno(anoCalendario.getAno());
        FolhaDePagamento folha = new FolhaDePagamento();
        folha.setTipoFolhaDePagamento(TipoFolhaDePagamento.NORMAL);
        vinculoFP.setFolha(folha);
        ReferenciaFPFuncao referencia = funcoesFolhaFacade.obterReferenciaValorFP(vinculoFP, "25", mes.getNumeroMes(), anoCalendario.getAno());
        valor = valor.add(BigDecimal.valueOf(referencia.getValor()));
        return valor;
    }

    @Deprecated
    public ByteArrayOutputStream montarRelatorio(List<VinculoFP> vinculos, Exercicio anoCalendario) throws JRException, IOException {
        try {
            rendimentosPagos.clear();
            HashMap parameters = definirParameters();

            List<ComprovanteRendimentosIRRFFonte> rendimentosIRRFFontes = Lists.newLinkedList();
            List<ComprovanteRendimentosCedula> beneficiarios = Lists.newLinkedList();
            List<JasperPrint> jaspers = Lists.newArrayList();

            preencherComprovanteRendimentoIRRFFonte(vinculos, rendimentosIRRFFontes, anoCalendario);

            carregarJaspers(parameters, rendimentosIRRFFontes, beneficiarios, jaspers);

            definirLocaleAndPerfilApp(parameters);
            System.out.println("jaspers " + jaspers.size());
            return exportarJaspersParaPDF(jaspers);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<VinculoFP> buscarVinculosPessoaVigentePorAno(Pessoa pessoa, Exercicio anoCalendario) {
        return vinculoFPFacade.buscarTodosVinculosPorPessoaVigentesNoAno(pessoa.getId(), anoCalendario.getAno());
    }

    private HashMap definirParameters() {
        HashMap parameter = new HashMap();
        parameter.put("USUARIO", sistemaFacade.getLogin());
        return parameter;
    }

    private void preencherComprovanteRendimentoIRRFFonte(List<VinculoFP> vinculos, List<ComprovanteRendimentosIRRFFonte> rendimentosIRRFFontes, Exercicio anoCalendario) {
        if (vinculos != null && !vinculos.isEmpty()) {
            for (VinculoFP vinculo : vinculos) {
                DirfVinculoFP dirfVinculoFP = dirfVinculoFPFacade.recuperarDirfCedulaC(vinculo, anoCalendario);
                if (dirfVinculoFP != null) {
                    ComprovanteRendimentosIRRFFonte comprovanteRendimentosIRRFFonte = new ComprovanteRendimentosIRRFFonte();
                    definirPensaoJudicial(comprovanteRendimentosIRRFFonte, vinculo, anoCalendario);
                    comprovanteRendimentosIRRFFonte = getComprovanteRendimentosIRRFFonte(dirfVinculoFP, comprovanteRendimentosIRRFFonte, false, anoCalendario);
                    rendimentosIRRFFontes.add(comprovanteRendimentosIRRFFonte);
                }
            }
            for (VinculoFP vinculo : vinculos) {
                List<DirfPessoa> dirfPessoa = dirfVinculoFPFacade.recuperarDirfPessoaCedulaC(vinculo, anoCalendario);
                if (dirfPessoa != null) {
                    for (DirfPessoa pessoa : dirfPessoa) {
                        ComprovanteRendimentosIRRFFonte comprovanteRendimentosIRRFFonte = new ComprovanteRendimentosIRRFFonte();
                        comprovanteRendimentosIRRFFonte = getComprovanteRendimentosIRRFFonte(pessoa, comprovanteRendimentosIRRFFonte, true, anoCalendario);
                        rendimentosIRRFFontes.add(comprovanteRendimentosIRRFFonte);
                    }
                }
            }
        }
    }

    private void carregarJaspers(HashMap parameters, List<ComprovanteRendimentosIRRFFonte> rendimentosIRRFFontes, List<ComprovanteRendimentosCedula> beneficiarios, List<JasperPrint> jaspers) throws JRException {
        if (rendimentosIRRFFontes != null && !rendimentosIRRFFontes.isEmpty()) {
            jaspers.add(JasperFillManager.fillReport(Util.getApplicationPath("/WEB-INF/report/" + COMPROVANTE_RENDIMENTOS_PAGOS_JASPER), parameters, new JRBeanCollectionDataSource(rendimentosIRRFFontes)));
        }
        if (beneficiarios != null && !beneficiarios.isEmpty()) {
            jaspers.add(JasperFillManager.fillReport(Util.getApplicationPath("/WEB-INF/report/" + COMPROVANTE_RENDIMENTOS_CEDULA_JASPER), parameters, new JRBeanCollectionDataSource(beneficiarios)));
        }

        if (rendimentosPagos != null && !rendimentosPagos.isEmpty()) {
            jaspers.add(JasperFillManager.fillReport(Util.getApplicationPath("/WEB-INF/report/" + COMPROVANTE_RENDIMENTOS_CONFERENCIA_JASPER), parameters, new JRBeanCollectionDataSource(rendimentosPagos)));
        }
    }

    public String buscarCaminhoBrasao() {
        String separator = System.getProperty("file.separator");
        String img = Util.getApplicationPath("/img/") + separator;
        return img + "Brasao_ministerio_fazenda.png";
    }

    private ComprovanteRendimentosIRRFFonte getComprovanteRendimentosIRRFFonte(DirfVinculoFPPessoa dirfVinculoFPPessoa, ComprovanteRendimentosIRRFFonte comprovanteRendimentosIRRFFonte, boolean pensao, Exercicio anoCalendario) {

        comprovanteRendimentosIRRFFonte.setBrasao(buscarCaminhoBrasao());

        System.out.println("anoCalendario metodo getComprovanteRendimentosIRRFFonte " + anoCalendario);

        comprovanteRendimentosIRRFFonte.setAnoExercicio(anoCalendario.getAno() + 1);
        comprovanteRendimentosIRRFFonte.setAnoCalendario(anoCalendario.getAno());

        //1. Fonte Pagadora Pessoa Jurídica
        if (!pensao) {
            comprovanteRendimentosIRRFFonte.setFontePagadora(dirfVinculoFPPessoa.getFontePagadora() != null ? dirfVinculoFPPessoa.getFontePagadora().getNome() : "");
            comprovanteRendimentosIRRFFonte.setCnpjFontePagadora(dirfVinculoFPPessoa.getFontePagadora() != null ? dirfVinculoFPPessoa.getFontePagadora().getCnpj() : "");
            comprovanteRendimentosIRRFFonte.setPensao(false);
        } else {
            comprovanteRendimentosIRRFFonte.setFontePagadora(dirfVinculoFPPessoa.getVinculoFP().getMatriculaFP().getPessoa().getNome());
            comprovanteRendimentosIRRFFonte.setCnpjFontePagadora(dirfVinculoFPPessoa.getVinculoFP().getMatriculaFP().getPessoa().getCpf());
            comprovanteRendimentosIRRFFonte.setPensao(true);
        }


        //2. Pessoa Física Beneficiária dos Rendimentos
        comprovanteRendimentosIRRFFonte.setCpfBeneficiaria(dirfVinculoFPPessoa.getCpf());
        comprovanteRendimentosIRRFFonte.setNome(dirfVinculoFPPessoa.getNome());
        if (!pensao) {
            comprovanteRendimentosIRRFFonte.setNaturezaRendimento(RENDIMENTO_DO_TRABALHO_ASSALARIADO);
        } else {
            comprovanteRendimentosIRRFFonte.setNaturezaRendimento(PENSAO_JUDICIAL);
        }


        //3. Rendimentos Tributáveis, Deduções e Imposto Retido na Fonte
        comprovanteRendimentosIRRFFonte.setTotalRendimentos(dirfVinculoFPPessoa.getTotalRendimentos());
        comprovanteRendimentosIRRFFonte.setContribuicaoPrevidenciaria(dirfVinculoFPPessoa.getContribuicaoPrevidenciaria());
        comprovanteRendimentosIRRFFonte.setContribuicaoPreviFundoAposentadoria(dirfVinculoFPPessoa.getPensaoProventos());
        comprovanteRendimentosIRRFFonte.setPensaoAlimenticia(dirfVinculoFPPessoa.getPensaoAlimenticia());
        comprovanteRendimentosIRRFFonte.setImpostoRetido(dirfVinculoFPPessoa.getImpostoDeRendaRetido());

        //4. Rendimentos Isentos e não Tributáveis
        comprovanteRendimentosIRRFFonte.setParcelIsentaProvetosAposentadoria(dirfVinculoFPPessoa.getParcelaIsenta());
        comprovanteRendimentosIRRFFonte.setDiariasEAjudaCusto(dirfVinculoFPPessoa.getDiariasAjudasDeCusto());
        comprovanteRendimentosIRRFFonte.setPensoesProventosApoMolestiaGrave(dirfVinculoFPPessoa.getPensaoProventosPorAcidente());
        comprovanteRendimentosIRRFFonte.setLucroEDividendo(dirfVinculoFPPessoa.getLucroEDividendoApurado());
        comprovanteRendimentosIRRFFonte.setValoresPagosAoTitular(dirfVinculoFPPessoa.getValoresPagosAoTitular());
        comprovanteRendimentosIRRFFonte.setIndenizacaoPorRescisaoContrato(dirfVinculoFPPessoa.getIndenizacoesPorRescisao());
        comprovanteRendimentosIRRFFonte.setOutrosIsentos(dirfVinculoFPPessoa.getOutrosRendimentosIsentos());

        //5. Rendimentos sujeitos à tributação exclusiva (rendimento líquido)
        comprovanteRendimentosIRRFFonte.setDecimoTerceiro(dirfVinculoFPPessoa.getDecimoTerceiroSalario());
        comprovanteRendimentosIRRFFonte.setIrRetidoNaFonte13Salario(dirfVinculoFPPessoa.getImpostoSobreARendaRetidoNo13());
        comprovanteRendimentosIRRFFonte.setOutrosSujeitosTributacao(dirfVinculoFPPessoa.getOutrosSujeitosATributacao());

        //6. Rendimentos recebidos acumuladamente - Art. 12-A da Lei nº 7.713, de 1988 (sujeito à tributação exclusiva)
        comprovanteRendimentosIRRFFonte.setExclusaoDespesasAcaoJudicial(dirfVinculoFPPessoa.getExclusaoDespesasAcaoJudicial());
        comprovanteRendimentosIRRFFonte.setDeducaoContribuicaoPrev(dirfVinculoFPPessoa.getDeducaoContribuicaoPrev());
        comprovanteRendimentosIRRFFonte.setDeducaoPensaoAlimenticia(dirfVinculoFPPessoa.getDeducaoPensaoAlimenticia());
        comprovanteRendimentosIRRFFonte.setImpostoSobreARendaRetido(dirfVinculoFPPessoa.getImpostoSobreARendaRetido());
        comprovanteRendimentosIRRFFonte.setRendimentosIsentosDePensao(dirfVinculoFPPessoa.getRendimentosIsentosDePensao());


        // 8. RESPONSÁVEL PELAS INFORMAÇÕES
        comprovanteRendimentosIRRFFonte.setNomeResponsavel(dirfVinculoFPPessoa.getDirf() != null ? dirfVinculoFPPessoa.getDirf().getResponsavel().getMatriculaFP().getPessoa().getNome() : "Prefeitura Municipal");

        // 7. Informações complementares
        if (dirfVinculoFPPessoa.isDirfVinculo()) {

            dirfVinculoFPPessoa = dirfVinculoFPFacade.recuperarInformacoesComplementares(dirfVinculoFPPessoa.getId());

            if (dirfVinculoFPPessoa.getInformacoesComplementares() != null && !dirfVinculoFPPessoa.getInformacoesComplementares().isEmpty()) {
                rendimentosPagos = Lists.newLinkedList();
                for (DirfInfoComplementares informacoesComplementares : dirfVinculoFPPessoa.getInformacoesComplementares()) {
                    informacoesComplementares.setNomeBeneficiarioPensao(informacoesComplementares.getPessoaFisica().getNome());
                    informacoesComplementares.setCpfBeneficiarioPensao(informacoesComplementares.getPessoaFisica().getCpf());
                    informacoesComplementares.setNascimentoBeneficiarioPensao(informacoesComplementares.getPessoaFisica().getDataNascimento());

                    comprovanteRendimentosIRRFFonte.setNomeBeneficiarioPensao(informacoesComplementares.getNomeBeneficiarioPensao());
                    comprovanteRendimentosIRRFFonte.setCpfBeneficiarioPensao(informacoesComplementares.getCpfBeneficiarioPensao());
                    comprovanteRendimentosIRRFFonte.setNascimentoBeneficiarioPensao(informacoesComplementares.getNascimentoBeneficiarioPensao());
                    comprovanteRendimentosIRRFFonte.setValor(informacoesComplementares.getValor());

                    comprovanteRendimentosIRRFFonte.getInformacoesComplementares().add(informacoesComplementares);
                }
                for (PensaoJudicialRetidoFonte pensaoJudicial : comprovanteRendimentosIRRFFonte.getPensoesJudiciais()) {
                    carregarRendimentosConferencia(pensaoJudicial, comprovanteRendimentosIRRFFonte, dirfVinculoFPPessoa.getInformacoesComplementares(), anoCalendario);
                }
            }
        }
        return comprovanteRendimentosIRRFFonte;
    }

    private void definirBrasao(ComprovanteRendimentosPagosConferencia comprovanteConferencia) {
        comprovanteConferencia.setBrasao(buscarCaminhoBrasao());
    }

    private void definirInstituidorBeneficiarioConferencia(PensaoJudicialRetidoFonte pensaoJudicial, ComprovanteRendimentosPagosConferencia comprovanteConferencia) {
        comprovanteConferencia.setInstituidor(pensaoJudicial.getInstituidor());
        comprovanteConferencia.setEventosFps(pensaoJudicial.getEventosFps());
    }

    private void definirNaturezaRendimentoConferencia(ComprovanteRendimentosPagosConferencia comprovanteConferencia) {
        comprovanteConferencia.setNaturezaRendimento("PENSÃO JUDICIAL");
    }

    private void definirBeneficiarioToConferencia(PensaoJudicialRetidoFonte pensaoJudicial, ComprovanteRendimentosPagosConferencia comprovanteConferencia) {
        comprovanteConferencia.setBeneficiarioNome(pensaoJudicial.getPessoaFisica().getNome());
        comprovanteConferencia.setBeneficiarioCpf(pensaoJudicial.getPessoaFisica().getCpf());
    }

    private void definirResponsavelInformacao(ComprovanteRendimentosIRRFFonte comprovanteRendimentosIRRFFonte, ComprovanteRendimentosPagosConferencia comprovanteConferencia) {
        comprovanteConferencia.setResponsavelInformacao(comprovanteRendimentosIRRFFonte.getNomeResponsavel());
    }


    private void definirFontePagadoraBeneficiario(ComprovanteRendimentosIRRFFonte comprovanteRendimentosIRRFFonte, ComprovanteRendimentosPagosConferencia comprovanteConferencia) {
        comprovanteConferencia.setFontePagadora(comprovanteRendimentosIRRFFonte.getNome());
        comprovanteConferencia.setFonteCpf(comprovanteRendimentosIRRFFonte.getCpfBeneficiaria());

    }

    private void carregarInformacaoRendimentos(ComprovanteRendimentosPagosConferencia comprovanteCoferencia, Integer ano, BigDecimal valorDecimoTerceiro) {
        carregarInformacaoComprovanteRendimentos(comprovanteCoferencia, ano, valorDecimoTerceiro);
    }

    private void carregarRendimentosConferencia(PensaoJudicialRetidoFonte pensaoJudicial, ComprovanteRendimentosIRRFFonte comprovanteRendimentosIRRFFonte, List<DirfInfoComplementares> informacoesComplementares, Exercicio anoCalendario) {
        ComprovanteRendimentosPagosConferencia comprovanteConferencia = new ComprovanteRendimentosPagosConferencia();
        BigDecimal valorDecimoTerceiro = BigDecimal.ZERO;
        for (DirfInfoComplementares informacoesComplementare : informacoesComplementares) {
            if (informacoesComplementare.getPessoaFisica().equals(pensaoJudicial.getPessoaFisica())) {
                valorDecimoTerceiro = informacoesComplementare.getValorDecimo();
            }
        }
        definirBrasao(comprovanteConferencia);
        definirInstituidorBeneficiarioConferencia(pensaoJudicial, comprovanteConferencia);
        definirNaturezaRendimentoConferencia(comprovanteConferencia);
        definirBeneficiarioToConferencia(pensaoJudicial, comprovanteConferencia);
        definirResponsavelInformacao(comprovanteRendimentosIRRFFonte, comprovanteConferencia);
        definirFontePagadoraBeneficiario(comprovanteRendimentosIRRFFonte, comprovanteConferencia);
        carregarInformacaoRendimentos(comprovanteConferencia, anoCalendario.getAno(), valorDecimoTerceiro);
        rendimentosPagos.add(comprovanteConferencia);
    }

    public List<Exercicio> listaExerciciosPorDirfCodigo() {
        String sql = "select ex.* from Exercicio ex "
            + "inner join dirfCodigo on ex.ID = DIRFCODIGO.EXERCICIO_ID  "
            + " where ex.ano <= :ano "
            + " order by ex.ano desc ";
        Query q = em.createNativeQuery(sql, Exercicio.class);
        q.setParameter("ano", Calendar.getInstance().get(Calendar.YEAR));
        List<Exercicio> retorno = (List<Exercicio>) q.getResultList();
        if (!retorno.isEmpty()) {
            return retorno;
        }
        return null;
    }
}

