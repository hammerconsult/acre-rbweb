package br.com.webpublico.negocios.rh.rotinasanuaismensais;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ArquivoComposicao;
import br.com.webpublico.entidades.MatriculaFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.creditodesalario.ItemCreditoSalario;
import br.com.webpublico.entidades.rh.creditodesalario.caixa.ItemRetornoCreditoSalario;
import br.com.webpublico.entidades.rh.creditodesalario.caixa.RetornoCaixaOcorrencias;
import br.com.webpublico.entidades.rh.creditodesalario.caixa.RetornoCreditoSalario;
import br.com.webpublico.entidadesauxiliares.DependenciasDirf;
import br.com.webpublico.enums.CodigoOcorrenciaRetornoArquivoCaixa;
import br.com.webpublico.enums.TipoRegistroCnab240;
import br.com.webpublico.enums.rh.creditosalario.BancoCreditoSalario;
import br.com.webpublico.enums.rh.creditosalario.FormaLancamentoCreditoSalario;
import br.com.webpublico.enums.rh.creditosalario.TipoOperacaoCreditoSalario;
import br.com.webpublico.enums.rh.creditosalario.TipoServicoCreditoSalario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.creditosalariobancos.CreditoSalarioBancosFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.beust.jcommander.internal.Lists;
import com.google.common.base.Strings;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class ArquivoRetornoCreditoSalarioFacade extends AbstractFacade<RetornoCreditoSalario> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private CreditoSalarioBancosFacade creditoSalarioBancosFacade;

    public ArquivoRetornoCreditoSalarioFacade() {
        super(RetornoCreditoSalario.class);
    }

    public VinculoFPFacade getVinculoFPFacade() {
        return vinculoFPFacade;
    }

    public void setVinculoFPFacade(VinculoFPFacade vinculoFPFacade) {
        this.vinculoFPFacade = vinculoFPFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RetornoCreditoSalario salvarNovo(RetornoCreditoSalario entity, UploadedFile uploadedFile, File pdfFile) throws Exception {
        try {
            Arquivo arquivo = criarEntidadeArquivo(uploadedFile);
            ArquivoComposicao arquivoComposicao = criarArquivoComposicao(entity, arquivo);
            ArquivoComposicao arquivoComposicaoLog = gerarArquivoComposicaoLog(pdfFile, entity);
            entity.getDetentorArquivoComposicao().getArquivosComposicao().add(arquivoComposicao);
            entity.getDetentorArquivoComposicao().getArquivosComposicao().add(arquivoComposicaoLog);
            return salvarRetornando(entity);
        } catch (ValidacaoException ex) {
            throw new ValidacaoException("Não foi possível salvar o retorno de crédito de salário da Caixa Econômica Federal, Erro: " + ex.getMessage());
        }
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public Future<AssistenteBarraProgresso> atualizarFichaFinanceira(AssistenteBarraProgresso assistenteBarraProgresso, List<Long> idFicha) {
        for (Long fichaFinanceiraFP : idFicha) {
            em.createNativeQuery("update FichaFinanceiraFP set creditoSalarioPago = 1 where id = " + fichaFinanceiraFP)
                .executeUpdate();
            assistenteBarraProgresso.conta();
        }
        return new AsyncResult<>(assistenteBarraProgresso);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public Future<Set<Long>> buscarFichaFinanceira(List<ItemRetornoCreditoSalario> itensRetorno, AssistenteBarraProgresso assistenteBarraProgresso) {
        Set<Long> fichas = new HashSet<>();
        for (ItemRetornoCreditoSalario itemRetornoCreditoSalario : itensRetorno) {
            Long fichaRecuperada = buscarFichaFinanceiraParaPagar(itemRetornoCreditoSalario);
            if (fichaRecuperada != null) {
                fichas.add(fichaRecuperada);
            }
            assistenteBarraProgresso.conta();
        }
        return new AsyncResult<>(fichas);
    }

    private ArquivoComposicao criarArquivoComposicao(RetornoCreditoSalario entity, Arquivo arquivo) {
        ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
        arquivoComposicao.setArquivo(arquivo);
        arquivoComposicao.setDataUpload(entity.getDataRegistro());
        arquivoComposicao.setDetentorArquivoComposicao(entity.getDetentorArquivoComposicao());
        return arquivoComposicao;
    }

    @Override
    public RetornoCreditoSalario recuperar(Object id) {
        RetornoCreditoSalario retornoCredito = em.find(RetornoCreditoSalario.class, id);
        if (retornoCredito.getDetentorArquivoComposicao() != null) {
            retornoCredito.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        for (ArquivoComposicao arquivoComposicao : retornoCredito.getDetentorArquivoComposicao().getArquivosComposicao()) {
            arquivoComposicao.getArquivo().getPartes().size();
        }

        return retornoCredito;
    }

    private Arquivo criarEntidadeArquivo(UploadedFile uploadFile) throws IOException {
        Arquivo arquivo = new Arquivo();
        arquivo.setNome(uploadFile.getFileName());
        arquivo.setDescricao(uploadFile.getFileName());
        arquivo.setTamanho(uploadFile.getSize());
        arquivo.setMimeType(uploadFile.getContentType());
        arquivo.setInputStream(uploadFile.getInputstream());
        try {
            arquivo = arquivoFacade.novoArquivoMemoria(arquivo, arquivo.getInputStream());
        } catch (Exception ex) {
            logger.error("Erro:", ex);
        }

        return arquivo;
    }

    public StreamedContent recuperarArquivoPorDescricao(RetornoCreditoSalario retornoCredSal) {
        String sql = " select arquivo.ID " +
            " from retornocreditosalario retorno " +
            " inner join detentorarquivocomposicao detentor on retorno.DETENTORARQUIVOCOMPOSICAO_ID = detentor.ID " +
            " inner join arquivocomposicao arquivocomp on detentor.id = arquivocomp.DETENTORARQUIVOCOMPOSICAO_ID " +
            " inner join arquivo on arquivo.id = arquivocomp.ARQUIVO_ID " +
            " where retorno.ID = :retornoCaixa ";
//            " and upper(ARQUIVO.DESCRICAO) like upper(:descricaoArquivo) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("retornoCaixa", retornoCredSal.getId());
//        q.setParameter("descricaoArquivo", "%" + descricaoArquivo + "%");
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            BigDecimal id = (BigDecimal) q.getResultList().get(0);
            Arquivo arquivo = arquivoFacade.recupera(id.longValue());
            try {
                return arquivoFacade.montarArquivoParaDownloadPorArquivo(arquivo);
            } catch (Exception e) {
                logger.error("Erro ao recuperar o Arquivo pela descricao: ", e.getMessage());
            }
        }
        return null;
    }

    private ArquivoComposicao gerarArquivoComposicaoLog(File pdfFile, RetornoCreditoSalario entity) throws Exception {
        Arquivo arquivo = new Arquivo();

        FileInputStream fis = new FileInputStream(pdfFile);
        arquivo.setInputStream(fis);
        arquivo = arquivoFacade.novoArquivoMemoria(arquivo);

        arquivo.setMimeType("application/pdf");
        arquivo.setNome(DataUtil.getDataFormatada(new Date(), "ddMMyyyy") + "_RetornoCaixaLog.pdf");
        arquivo.setDescricao(DataUtil.getDataFormatada(new Date(), "ddMMyyyy") + "_RetornoCaixaLog");
        arquivo.setTamanho(pdfFile.getTotalSpace());

        return getArquivoComposicao(pdfFile, arquivo, entity);
    }

    private ArquivoComposicao getArquivoComposicao(File file, Arquivo arquivo, RetornoCreditoSalario entity) {
        ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
        arquivoComposicao.setArquivo(arquivo);
        arquivoComposicao.setDataUpload(new Date());
        arquivoComposicao.setFile(file);
        arquivoComposicao.setDetentorArquivoComposicao(entity.getDetentorArquivoComposicao());
        return arquivoComposicao;
    }

    public StreamedContent recuperarArquivoSelecionadoParaDownload(Arquivo arquivoSelecionado) {
        return arquivoFacade.montarArquivoParaDownloadPorArquivo(arquivoSelecionado);
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    private Long buscarFichaFinanceiraParaPagar(ItemRetornoCreditoSalario itemRetorno) {
        return fichaFinanceiraFPFacade.recuperaIdFichaFinanceiraFPPorVinculoFPMesAno(itemRetorno.getVinculoFP(), itemRetorno.getIdentificadorFicha());
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public Future<AssistenteBarraProgresso> processarConteudoArquivo(RetornoCreditoSalario selecionado, Map<String, VinculoFP> vinculoPorCpf,
                                                                     DependenciasDirf dependenciasDirf, ItemRetornoCreditoSalario itemRetorno, UploadedFile uploadedFile,
                                                                     Boolean isOperacaoNovo, AssistenteBarraProgresso assistenteBarraProgresso) {
        try {
            InputStreamReader streamReader = new InputStreamReader(uploadedFile.getInputstream());
            BufferedReader reader = new BufferedReader(streamReader);
            String line;
            ItemRetornoCreditoSalario anterior = null;
            List<CodigoOcorrenciaRetornoArquivoCaixa> ocorrenciasSeguimentoA = new ArrayList<>();
            Integer loteServico = null;
            List<CodigoOcorrenciaRetornoArquivoCaixa> ocorrenciasRetornoCreditoSalario = Lists.newArrayList();
            assistenteBarraProgresso.setDescricaoProcesso("Realizando a Leitura dos Registros do Retorno.");
            BigDecimal valorLiquido = BigDecimal.ZERO;

            String identificador = null;
            while ((line = reader.readLine()) != null) {
                VinculoFP vinculo = null;
                if (line.isEmpty()) {
                    assistenteBarraProgresso.conta();
                    continue;
                }
                TipoRegistroCnab240 tipoRegistroCnab240 = itemRetorno.getTipoRegistroCnab240(line);
                String cpf = itemRetorno.getCPFCnb240(line, tipoRegistroCnab240);
                vinculo = vinculoPorCpf.get(cpf);

                String identificadorRecuperado = itemRetorno.getIdentificadorFichaFinanceira(line, tipoRegistroCnab240);

                if (!Strings.isNullOrEmpty(identificadorRecuperado)) {
                    identificador = identificadorRecuperado;
                }
                if (TipoRegistroCnab240.SEGMENTO_A.equals(tipoRegistroCnab240)) {
                    String ocorrencias = itemRetorno.getOcorrencias(line);
                    StringBuilder valor = new StringBuilder(StringUtil.removeZerosEsquerda(itemRetorno.getValor(line)));
                    valor.insert(valor.length() - 2, '.');
                    valorLiquido = new BigDecimal(valor.toString());
                    ocorrenciasRetornoCreditoSalario.addAll(buscarOcorrencias(ocorrencias));
                    loteServico = Integer.parseInt(StringUtil.removeZerosEsquerda(itemRetorno.getLoteServico(line)));
                }
                if (vinculo == null) {
                    if (TipoRegistroCnab240.HEADER_ARQUIVO.equals(tipoRegistroCnab240)) {
                        try {
                            String identificarLote = itemRetorno.getIdentificadorLote(line);
                            String ocorrencias = itemRetorno.getOcorrencias(line);
                            String codigoBanco = line.substring(0, 3);
                            if (!ocorrencias.trim().isEmpty()) {
                                selecionado.setOcorrenciasHeaderArquivo(ocorrencias);
                            }
                            ItemCreditoSalario itemCreditoSalario = creditoSalarioBancosFacade.buscarItemCreditoSalarioPorIdentificador(identificarLote);
                            if (itemCreditoSalario != null) {
                                selecionado.setExercicio(exercicioFacade.getExercicioPorAno(itemCreditoSalario.getCreditoSalario().getFolhaDePagamento().getAno()));
                                selecionado.setMes(itemCreditoSalario.getCreditoSalario().getFolhaDePagamento().getMes());
                                selecionado.setTipoFolhaDePagamento(itemCreditoSalario.getCreditoSalario().getFolhaDePagamento().getTipoFolhaDePagamento());
                                selecionado.setVersaoFolha(itemCreditoSalario.getCreditoSalario().getFolhaDePagamento().getVersao());
                                for (BancoCreditoSalario value : BancoCreditoSalario.values()) {
                                    if (value.getCodigo().equals(codigoBanco)) {
                                        selecionado.setBancoCreditoSalario(value);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            logger.error("nao foi possivel recuperar informacoes da folha de pagamento do header");
                        }
                    }
                    if (TipoRegistroCnab240.HEADER_LOTE.equals(tipoRegistroCnab240)) {
                        selecionado.setLoteServicoHeader(Integer.parseInt(line.substring(3, 7)));
                        selecionado.setTipoOperacao(TipoOperacaoCreditoSalario.valueOf(line.substring(8, 9)));
                        for (TipoServicoCreditoSalario value : TipoServicoCreditoSalario.values()) {
                            if (value.getCodigo().equals(line.substring(9, 11))) {
                                selecionado.setTipoServico(value);
                            }
                        }
                        for (FormaLancamentoCreditoSalario value : FormaLancamentoCreditoSalario.values()) {
                            if (value.getCodigo().equals(line.substring(11, 13))) {
                                selecionado.setFormaLancamento(value);
                            }
                        }
                        String ocorrencias = line.substring(230, 240);
                        if (!Strings.isNullOrEmpty(ocorrencias.trim())) {
                            selecionado.setOcorrenciasHeaderLote(ocorrencias);
                        }
                    }
                    if (TipoRegistroCnab240.TRAILER_LOTE.equals(tipoRegistroCnab240)) {
                        selecionado.setLoteServicoTrailler(Integer.parseInt(line.substring(3, 7)));
                        selecionado.setQuantidadeRegistroLote(Integer.parseInt(line.substring(17, 23)));
                        StringBuilder valor = new StringBuilder(StringUtil.removeZerosEsquerda(line.substring(23, 41)));
                        valor.insert(valor.length() - 2, '.');
                        selecionado.setTotalValores(new BigDecimal(valor.toString()));
                        String ocorrencias = line.substring(230, 240);
                        if (!Strings.isNullOrEmpty(ocorrencias.trim())) {
                            selecionado.setOcorrenciasTraillerLote(ocorrencias);
                        }
                    }
                    if (TipoRegistroCnab240.TRAILER_ARQUIVO.equals(tipoRegistroCnab240)) {
                        selecionado.setQuantidadeLotes(Integer.parseInt(line.substring(17, 23)));
                        selecionado.setQuantidadeRegistros(Integer.parseInt(line.substring(23, 29)));
                    }
                    continue;
                }
                if (isOperacaoNovo) {
                    preencherRetornoCreditoQuandoHeader(selecionado, line, tipoRegistroCnab240, itemRetorno, uploadedFile);
                    if (!TipoRegistroCnab240.SEGMENTO_A.equals(tipoRegistroCnab240) && !TipoRegistroCnab240.SEGMENTO_B.equals(tipoRegistroCnab240)) {
                        continue;
                    }
                    ItemRetornoCreditoSalario itemRetornoCreditoSalario = preencherItem(selecionado, line, tipoRegistroCnab240, vinculo, ocorrenciasRetornoCreditoSalario, identificador, valorLiquido, loteServico);
                    valorLiquido = BigDecimal.ZERO;
                    loteServico = null;
                    if (!ocorrenciasRetornoCreditoSalario.isEmpty()) {
                        ocorrenciasSeguimentoA = ocorrenciasRetornoCreditoSalario;
                        ocorrenciasRetornoCreditoSalario = Lists.newArrayList();
                    }
                    anterior = getItemAnterior(selecionado, anterior, tipoRegistroCnab240, vinculo, itemRetornoCreditoSalario);
                    ocorrenciasSeguimentoA = getOcorrenciasLog(ocorrenciasSeguimentoA, tipoRegistroCnab240, itemRetornoCreditoSalario, dependenciasDirf);
                    identificador = null;
                }
                assistenteBarraProgresso.conta();
            }
            adicionarLogTipoRodape(dependenciasDirf, "Termino do processamento do Arquivo de Retorno");
        } catch (IOException e) {
            logger.debug("Erro ao processar o arquivo de retorno, Erro: " + e.getMessage());
            adicionarLogTipoErro(dependenciasDirf, "Erro ao processar o arquivo de retorno, Erro: " + e.getMessage());
        } finally {
            adicionarLogsPorTipoAoInicioDoCorpo(dependenciasDirf, DependenciasDirf.TipoLog.ERRO);
        }
        return new AsyncResult<>(assistenteBarraProgresso);
    }

    public List<CodigoOcorrenciaRetornoArquivoCaixa> buscarOcorrencias(String ocorrencias) {
        List<String> codigoOcorrencia = Lists.newArrayList();
        List<CodigoOcorrenciaRetornoArquivoCaixa> retorno = Lists.newArrayList();
        if (!Strings.isNullOrEmpty(ocorrencias.trim())) {
            if (!Strings.isNullOrEmpty(ocorrencias.substring(0, 2).trim())) {
                codigoOcorrencia.add(ocorrencias.substring(0, 2).trim());
            }
            if (!Strings.isNullOrEmpty(ocorrencias.substring(2, 4).trim())) {
                codigoOcorrencia.add(ocorrencias.substring(2, 4).trim());
            }
            if (!Strings.isNullOrEmpty(ocorrencias.substring(4, 6).trim())) {
                codigoOcorrencia.add(ocorrencias.substring(4, 6).trim());
            }
            if (!Strings.isNullOrEmpty(ocorrencias.substring(6, 8).trim())) {
                codigoOcorrencia.add(ocorrencias.substring(6, 8).trim());
            }
            if (!Strings.isNullOrEmpty(ocorrencias.substring(8, 10).trim())) {
                codigoOcorrencia.add(ocorrencias.substring(8, 10).trim());
            }
        }

        for (String cod : codigoOcorrencia) {
            if (cod.length() == 2) {
                if ("00".equals(cod)) {
                    retorno.add(CodigoOcorrenciaRetornoArquivoCaixa.CREDITO_DEBITO_EFETIVADO);
                } else if ("01".equals(cod)) {
                    retorno.add(CodigoOcorrenciaRetornoArquivoCaixa.INSUFICIENCIA_FUNDOS);
                } else if ("02".equals(cod)) {
                    retorno.add(CodigoOcorrenciaRetornoArquivoCaixa.CREDITO_DEBITO_CANCELADO_PAGADOR_CREDOR);
                } else if ("03".equals(cod)) {
                    retorno.add(CodigoOcorrenciaRetornoArquivoCaixa.DEBITO_AUTORIZADO_PELA_AGENCIA);
                } else {
                    try {
                        retorno.add(CodigoOcorrenciaRetornoArquivoCaixa.valueOf(cod));
                    } catch (Exception e) {
                        logger.error("Não foi possível recuperar ocorrência para o código informado.");
                    }
                }
            }
        }
        return retorno;
    }

    public Long recuperarVinculosFP(String cpf, RetornoCreditoSalario selecionado, DependenciasDirf dependenciasDirf) {
        if (cpf != null) {
            return getVinculoFP(cpf, selecionado, dependenciasDirf);
        }
        return null;
    }

    private Long getVinculoFP(String cpf, RetornoCreditoSalario selecionado, DependenciasDirf dependenciasDirf) {
        Date dataOperacao = new Date();
        if (selecionado.getMes() != null && selecionado.getExercicio() != null) {
            dataOperacao = DataUtil.montaData(1, selecionado.getMes().getNumeroMes(), selecionado.getExercicio().getAno()).getTime();
        }
        Long vinculoFP = pessoaFisicaFacade.buscarPessoaPeloCPFComVinculoVigente(cpf, dataOperacao);
        if (vinculoFP != null) {
            return vinculoFP;
        } else if (pessoaFisicaFacade.buscarIdDePessoaPorCpf(cpf) != null) {
            return pessoaFisicaFacade.buscarPessoaPeloCPFComVinculoVigente(cpf, null);
        }
        adicionarLogTipoErro(dependenciasDirf, "Não foi possível localizar servidor com o CPF: " + cpf);
        logger.debug("Nao foi encontrado nenhuma pessoa com o CPF: {}", cpf);
        return null;
    }

    private Date criarDataOperacao(RetornoCreditoSalario selecionado) {
        return DataUtil.montaData(DataUtil.ultimoDiaDoMes(selecionado.getMes().getNumeroMes()), selecionado.getMes().getNumeroMes(), selecionado.getExercicio().getAno()).getTime();
    }

    private void adicionarLogTipoErro(DependenciasDirf dependenciasDirf, String mensagem) {
        dependenciasDirf.adicionarLog(DependenciasDirf.TipoLog.ERRO, mensagem);
    }

    private void preencherRetornoCreditoQuandoHeader(RetornoCreditoSalario selecionado, String line, TipoRegistroCnab240 tipoRegistroCnab240,
                                                     ItemRetornoCreditoSalario itemRetorno, UploadedFile uploadedFile) {
        if (TipoRegistroCnab240.HEADER_ARQUIVO.equals(tipoRegistroCnab240)) {
            String sequencial = itemRetorno.getSequencial(line, tipoRegistroCnab240);
            String numeroConvenio = itemRetorno.getNumeroConvenio(line, tipoRegistroCnab240);
            preencherRetornoCreditoSalario(selecionado, sequencial, numeroConvenio, uploadedFile);
        }
    }

    private void preencherRetornoCreditoSalario(RetornoCreditoSalario selecionado, String sequencial, String numeroConvenio, UploadedFile uploadedFile) {
        if (sequencial != null && numeroConvenio != null) {
            selecionado.setNomeArquivo(uploadedFile.getFileName());
            selecionado.setSeguencial(Long.parseLong(sequencial));
            selecionado.setNumeroConvenio(numeroConvenio);
        }
    }

    private ItemRetornoCreditoSalario preencherItem(RetornoCreditoSalario selecionado, String line, TipoRegistroCnab240 tipoRegistroCnab240, VinculoFP vinculo,
                                                    List<CodigoOcorrenciaRetornoArquivoCaixa> ocorrenciasSeguimentoA, String identificadorFicha, BigDecimal valorLiquido, Integer loteServico) {
        ItemRetornoCreditoSalario itemAtual = new ItemRetornoCreditoSalario();
        itemAtual.setIndice(selecionado.getItemRetornoCreditoSalario().isEmpty() ? 1 : selecionado.getItemRetornoCreditoSalario().size() + 1);
        itemAtual.setLinhaArquivo(line);
        itemAtual.setTipoRegistro(tipoRegistroCnab240);
        itemAtual.setRetornoCreditoSalario(selecionado);
        itemAtual.setVinculoFP(vinculo);
        itemAtual.setIdentificadorFicha(identificadorFicha);
        itemAtual.setValorLiquido(valorLiquido);
        itemAtual.setLoteServico(loteServico);
        Util.adicionarObjetoEmLista(selecionado.getItemRetornoCreditoSalario(), itemAtual);
        guardarOcorrencias( ocorrenciasSeguimentoA, itemAtual);
        return itemAtual;
    }

    private void guardarOcorrencias(List<CodigoOcorrenciaRetornoArquivoCaixa> ocorrenciasSeguimentoA, ItemRetornoCreditoSalario itemAtual) {
        for (CodigoOcorrenciaRetornoArquivoCaixa ocorrencia : ocorrenciasSeguimentoA) {
            preencherRetornoOcorrencias(itemAtual, ocorrencia);
        }
    }

    private void preencherRetornoOcorrencias(ItemRetornoCreditoSalario item, CodigoOcorrenciaRetornoArquivoCaixa codigoOcorrenciaRetornoArquivoCaixa) {
        RetornoCaixaOcorrencias retornoCaixaOcorrencias = new RetornoCaixaOcorrencias();
        retornoCaixaOcorrencias.setItemRetornoCreditoSalario(item);
        retornoCaixaOcorrencias.setOcorrencia(codigoOcorrenciaRetornoArquivoCaixa);
        Util.adicionarObjetoEmLista(item.getRetornoCaixaOcorrencias(), retornoCaixaOcorrencias);
    }

    private ItemRetornoCreditoSalario getItemAnterior(RetornoCreditoSalario selecionado, ItemRetornoCreditoSalario anterior, TipoRegistroCnab240 tipoRegistroCnab240,
                                                      VinculoFP vinculo, ItemRetornoCreditoSalario itemRetornoCreditoSalario) {
        if (TipoRegistroCnab240.SEGMENTO_A.equals(tipoRegistroCnab240)) {
            anterior = itemRetornoCreditoSalario;
        }
        if (TipoRegistroCnab240.SEGMENTO_B.equals(tipoRegistroCnab240) && anterior != null) {
            int i = selecionado.getItemRetornoCreditoSalario().indexOf(anterior);
            selecionado.getItemRetornoCreditoSalario().get(i).setVinculoFP(vinculo);
            anterior = null;
        }
        return anterior;
    }

    private List<CodigoOcorrenciaRetornoArquivoCaixa> getOcorrenciasLog(List<CodigoOcorrenciaRetornoArquivoCaixa> ocorrenciasSeguimentoA,
                                                                        TipoRegistroCnab240 tipoRegistroCnab240, ItemRetornoCreditoSalario itemRetornoCreditoSalario,
                                                                        DependenciasDirf dependenciasDirf) {
        if (TipoRegistroCnab240.SEGMENTO_B.equals(tipoRegistroCnab240) && !ocorrenciasSeguimentoA.isEmpty()) {
            MatriculaFP matriculaFP = itemRetornoCreditoSalario.getVinculoFP().getMatriculaFP();
            String mensagem = "Matricula: " + matriculaFP.getMatricula() +
                " Nome: " + matriculaFP.getPessoa().getNome() +
                " </br>Ocorrência(s):</br>";
            for (CodigoOcorrenciaRetornoArquivoCaixa ocorrencia : ocorrenciasSeguimentoA) {
                mensagem += " - " + ocorrencia.getDescricao() + " </br>";
            }
            if (Boolean.TRUE.equals(CodigoOcorrenciaRetornoArquivoCaixa.isTodasOcorrenciasSucesso(ocorrenciasSeguimentoA))) {
                adicionarLogTipoCorpo(dependenciasDirf, mensagem);
            } else {
                adicionarLogTipoErro(dependenciasDirf, mensagem);
            }
            ocorrenciasSeguimentoA = new ArrayList<>();
        }
        return ocorrenciasSeguimentoA;
    }

    public void adicionarLogTipoCabecalho(DependenciasDirf dependenciasDirf, String mensagem) {
        dependenciasDirf.adicionarLog(DependenciasDirf.TipoLog.CABECALHO, mensagem);
    }

    public void adicionarLogTipoCorpo(DependenciasDirf dependenciasDirf, String mensagem) {
        dependenciasDirf.adicionarLog(DependenciasDirf.TipoLog.CORPO, mensagem);
    }

    public void adicionarLogTipoRodape(DependenciasDirf dependenciasDirf, String mensagem) {
        dependenciasDirf.adicionarLog(DependenciasDirf.TipoLog.RODAPE, mensagem);
    }

    public void adicionarLogsPorTipoAoInicioDoCorpo(DependenciasDirf dependenciasDirf, DependenciasDirf.TipoLog tipoLog) {
        if (!Optional.ofNullable(dependenciasDirf.getLogGeral().get(tipoLog)).orElse(Lists.newArrayList()).isEmpty()) {
            dependenciasDirf.getLogGeral().get(DependenciasDirf.TipoLog.CORPO)
                .addAll(0, dependenciasDirf.getLogGeral().get(tipoLog));
        }
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Future<List<ItemRetornoCreditoSalario>> buscarItensRetorno(RetornoCreditoSalario retorno) {
        String sql = "select * from ItemRetornoCreditoSalario " +
            " where RETORNOCREDITOSALARIO_ID = :idRetorno";
        Query q = em.createNativeQuery(sql, ItemRetornoCreditoSalario.class);
        q.setParameter("idRetorno", retorno.getId());
        List<ItemRetornoCreditoSalario> item = q.getResultList();
        return new AsyncResult<>(item);
    }

    public List<RetornoCaixaOcorrencias> buscarOcorrencias(ItemRetornoCreditoSalario item) {
        String sql = "select ocorrencias.* from  ITEMRETORNOCREDITOSALARIO item " +
            "    inner join RETORNOCAIXAOCORRENCIAS ocorrencias on item.ID = ocorrencias.ITEMRETORNOCREDITOSALARIO_ID " +
            "    where item.id = :id ";
        Query q = em.createNativeQuery(sql, RetornoCaixaOcorrencias.class);
        q.setParameter("id", item.getId());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return new ArrayList<>();
    }


}

