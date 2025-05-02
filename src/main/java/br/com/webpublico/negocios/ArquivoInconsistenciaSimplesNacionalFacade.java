package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemArquivoSimplesNacionalProcessado;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Desenvolvimento on 19/07/2016.
 */
@Stateless
public class ArquivoInconsistenciaSimplesNacionalFacade extends AbstractFacade<ArquivoInconsistenciaSimplesNacional> {

    protected static final Logger logger = LoggerFactory.getLogger(ArquivoInconsistenciaSimplesNacionalFacade.class);

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ParametroSimplesNacionalFacade parametroSimplesNacionalFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private ArquivoInconsistenciaSimplesNacionalFacade arquivoInconsistenciaSimplesNacionalFacade;
    @EJB
    private ArquivoFacade arquivoFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ArquivoInconsistenciaSimplesNacionalFacade() {
        super(ArquivoInconsistenciaSimplesNacional.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ParametroSimplesNacionalFacade getParametroSimplesNacionalFacade() {
        return parametroSimplesNacionalFacade;
    }

    @Override
    public ArquivoInconsistenciaSimplesNacional recuperar(Object id) {
        ArquivoInconsistenciaSimplesNacional arquivo = em.find(ArquivoInconsistenciaSimplesNacional.class, id);
        Hibernate.initialize(arquivo.getDetentorArquivoImportacao().getArquivosComposicao());
        Hibernate.initialize(arquivo.getArquivosExportados());
        return arquivo;
    }

    public ArquivoInconsistenciaSimplesNacional recuperarArquivoComItens(ArquivoInconsistenciaSimplesNacional arquivo) {
        arquivo = recuperar(arquivo.getId());
        Hibernate.initialize(arquivo.getListaItensArquivoSimplesNacional());
        return arquivo;
    }

    public ArquivoInconsistenciaSimplesNacional salvarArquivo(ArquivoInconsistenciaSimplesNacional arquivo) {
        arquivo.setCodigo(singletonGeradorCodigo.getProximoCodigo(ArquivoInconsistenciaSimplesNacional.class, "codigo"));
        return salvarRetornando(arquivo);
    }

    public List<String> lerArquivoImportado(ArquivoInconsistenciaSimplesNacional arquivoSimplesNacional) throws IOException {
        arquivoSimplesNacional = recuperar(arquivoSimplesNacional.getId());
        ArquivoComposicao arquivoComposicao = arquivoSimplesNacional.getDetentorArquivoImportacao().getArquivosComposicao().get(0);
        arquivoSimplesNacional.setArquivoProcessado(arquivoComposicao);
        Arquivo arquivo = arquivoComposicao.getArquivo();
        arquivo.montarImputStream();
        Scanner scanner = new Scanner(arquivo.getInputStream()).useDelimiter("\\n");
        List<String> cnpjs = Lists.newArrayList();
        while (scanner.hasNext()) {
            String linha = scanner.next();
            if (linha.length() > 0) {
                String cnpj = linha.substring(0, 14);
                if (!cnpjs.contains(cnpj)) {
                    cnpjs.add(cnpj);
                }
            }
        }
        return cnpjs;
    }

    public List<ResultadoParcela> consultarDebitosVencidos(ParametroSimplesNacional parametroSimplesNacional,
                                                           Pessoa pessoa) {
        List<Long> idsDividas = montarIdsDividas(parametroSimplesNacional.getDividas());
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IN, idsDividas);
        consultaParcela.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, pessoa.getId());
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR, DataUtil.dataSemHorario(new Date()));
        consultaParcela.executaConsulta();
        List<ResultadoParcela> retorno = Lists.newArrayList(consultaParcela.getResultados());
        removerParcelasComValorMenorParametro(parametroSimplesNacional, retorno);
        return retorno;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<Void> registrarCNPJsArquivoSimplesNacional(AssistenteBarraProgresso assistente,
                                                       ArquivoInconsistenciaSimplesNacional arquivo,
                                                       List<String> cnpjs) {

        for (String cnpj : cnpjs) {
            ItemArquivoSimplesNacional itemArquivoSimplesNacional = new ItemArquivoSimplesNacional();
            itemArquivoSimplesNacional.setArquivo(arquivo);
            itemArquivoSimplesNacional.setCnpj(cnpj);
            arquivoInconsistenciaSimplesNacionalFacade.inserirItemArquivoSimplesNacional(itemArquivoSimplesNacional);

            assistente.contar(1);
        }

        return new AsyncResult<>(null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void inserirItemArquivoSimplesNacional(ItemArquivoSimplesNacional itemArquivoSimplesNacional) {
        em.persist(itemArquivoSimplesNacional);
    }

    private void removerParcelasComValorMenorParametro(ParametroSimplesNacional parametroSimplesNacional,
                                                       List<ResultadoParcela> resultadoParcelas) {
        BigDecimal valorURMRB = recuperarValorUFMRB(parametroSimplesNacional);

        if(valorURMRB != null) {
            List<ResultadoParcela> parcelasRemover = Lists.newArrayList();
            for (ResultadoParcela resultadoParcela : resultadoParcelas) {
                if(resultadoParcela.getValorTotal().compareTo(valorURMRB) < 0) {
                    parcelasRemover.add(resultadoParcela);
                }
            }
            resultadoParcelas.removeAll(parcelasRemover);
        }
    }

    private BigDecimal recuperarValorUFMRB(ParametroSimplesNacional parametroSimplesNacional) {
        try {
            return moedaFacade.converterToReal(parametroSimplesNacional.getValorUFMRB());
        } catch (UFMException e) {
            logger.error("Erro ao converter UFMRB. ", e);
        }
        return null;
    }

    private List<Long> montarIdsDividas(List<ParamSimplesNacionalDivida> dividas) {
        List<Long> ids = Lists.newArrayList();

        for (ParamSimplesNacionalDivida parametroDivida : dividas) {
            ids.add(parametroDivida.getDivida().getId());
        }

        return ids;
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<List<ItemArquivoSimplesNacionalProcessado>> buscarItensArquivoProcessados(AssistenteBarraProgresso assistente,
                                                                                            ParametroSimplesNacional parametroSimplesNacional,
                                                                                            List<ItemArquivoSimplesNacional> itens) {
        List<ItemArquivoSimplesNacionalProcessado> retorno = Lists.newArrayList();
        for (ItemArquivoSimplesNacional itemArquivoSimplesNacional : itens) {
            ItemArquivoSimplesNacionalProcessado itemArquivoSimplesNacionalProcessado =
                criarDadosArquivoSimplesNacional(parametroSimplesNacional, itemArquivoSimplesNacional);

            retorno.add(itemArquivoSimplesNacionalProcessado);

            assistente.contar(1);
        }
        return new AsyncResult<>(retorno);
    }

    public ItemArquivoSimplesNacionalProcessado criarDadosArquivoSimplesNacional(ParametroSimplesNacional parametroSimplesNacional,
                                                                                 ItemArquivoSimplesNacional itemArquivoSimplesNacional) {
        ItemArquivoSimplesNacionalProcessado itemArquivoSimplesNacionalProcessado = new ItemArquivoSimplesNacionalProcessado();
        itemArquivoSimplesNacionalProcessado.setId(itemArquivoSimplesNacional.getId());
        itemArquivoSimplesNacionalProcessado.setCnpj(itemArquivoSimplesNacional.getCnpj());
        Pessoa pessoa = pessoaFacade.recuperaPessoaJuridicaPorCNPJ(itemArquivoSimplesNacional.getCnpj(), false);
        itemArquivoSimplesNacionalProcessado.setTemPessoa(pessoa != null);
        CadastroEconomico cadastroEconomico = cadastroEconomicoFacade.buscarCadastroEconomicoAtivoPorPessoa(pessoa, false);
        itemArquivoSimplesNacionalProcessado.setTemCmc(cadastroEconomico != null);
        Boolean temDebitosVencidos = Boolean.FALSE;
        if (pessoa != null) {
            itemArquivoSimplesNacionalProcessado.setRazaoSocial(pessoa.getNome());
            List<ResultadoParcela> parcelas = consultarDebitosVencidos(parametroSimplesNacional, pessoa);
            temDebitosVencidos = !parcelas.isEmpty();
        }
        itemArquivoSimplesNacionalProcessado.setTemDebitosVencidos(temDebitosVencidos);
        return itemArquivoSimplesNacionalProcessado;
    }

    @Asynchronous
    public Future<Void> gerarArquivoExportacao(AssistenteBarraProgresso assistente,
                                               ArquivoInconsistenciaSimplesNacional arquivo,
                                               List<ItemArquivoSimplesNacionalProcessado> itensArquivoProcessado,
                                               ArquivoExportadoSimplesNacional.TipoExportacao tipoExportacao) throws Exception {
        assistente.setDescricaoProcesso("Gravando arquivo exportado.");
        assistente.setTotal(0);
        byte[] bytes = gerarBytesArquivoTxt(tipoExportacao, itensArquivoProcessado);
        gravarArquivoExportado(assistente, arquivo, tipoExportacao, bytes);

        assistente.setDescricaoProcesso("Atualizando arquivo importado do simples nacional.");
        assistente.setTotal(0);
        atualizarItensProcessados(itensArquivoProcessado, tipoExportacao);
        return new AsyncResult<>(null);
    }

    public void atualizarItensProcessados(List<ItemArquivoSimplesNacionalProcessado> itensArquivoProcessado,
                                          ArquivoExportadoSimplesNacional.TipoExportacao tipoExportacao) {
        for (ItemArquivoSimplesNacionalProcessado itemProcessado : itensArquivoProcessado) {
            ItemArquivoSimplesNacional itemArquivoSimplesNacional = em.find(ItemArquivoSimplesNacional.class, itemProcessado.getId());
            if (tipoExportacao.equals(ArquivoExportadoSimplesNacional.TipoExportacao.INI)
                || tipoExportacao.equals(ArquivoExportadoSimplesNacional.TipoExportacao.ADC)) {
                em.createNativeQuery(" update itemarquivosimplesnacional set enviouinconsistencia = 1 where id = :id ")
                    .setParameter("id", itemProcessado.getId())
                    .executeUpdate();
            } else {
                em.createNativeQuery(" update itemarquivosimplesnacional set enviouregularizacao = 1 where id = :id ")
                    .setParameter("id", itemProcessado.getId())
                    .executeUpdate();
            }
        }
    }

    public void gravarArquivoExportado(AssistenteBarraProgresso assistente,
                                       ArquivoInconsistenciaSimplesNacional arquivoInconsistenciaSimplesNacional,
                                       ArquivoExportadoSimplesNacional.TipoExportacao tipoExportacao,
                                       byte[] bytes) throws Exception {
        ArquivoExportadoSimplesNacional arquivoExportadoSimplesNacional = new ArquivoExportadoSimplesNacional();
        arquivoExportadoSimplesNacional.setArquivoInconsistenciaSimplesNacional(arquivoInconsistenciaSimplesNacional);
        arquivoExportadoSimplesNacional.setDataExportacao(assistente.getDataAtual());
        arquivoExportadoSimplesNacional.setUsuarioExportacao(assistente.getUsuarioSistema());
        arquivoExportadoSimplesNacional.setTipoExportacao(tipoExportacao);

        Arquivo arquivo = arquivoFacade.novoArquivoMemoria(new Arquivo(), new ByteArrayInputStream(bytes));
        arquivo.setNome("ArquivoInconsistencia." + tipoExportacao.name());
        arquivo.setDescricao(arquivo.getNome());
        arquivo.setMimeType("text/plain");

        arquivoExportadoSimplesNacional.setArquivoExportacao(arquivo);
        em.persist(arquivoExportadoSimplesNacional);
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<List<ItemArquivoSimplesNacionalProcessado>> buscarItensArquivoProcessadosExportacaoArquivo(AssistenteBarraProgresso assistenteBarraProgresso,
                                                                                                             ParametroSimplesNacional parametroSimplesNacional,
                                                                                                             List<ItemArquivoSimplesNacional> itensArquivo,
                                                                                                             ArquivoExportadoSimplesNacional.TipoExportacao tipoExportacao) {
        List<ItemArquivoSimplesNacionalProcessado> itensArquivoProcessado = Lists.newArrayList();
        for (ItemArquivoSimplesNacional itemArquivo : itensArquivo) {
            ItemArquivoSimplesNacionalProcessado itemArquivoSimplesNacionalProcessado = criarDadosArquivoSimplesNacional(parametroSimplesNacional, itemArquivo);
            if ((tipoExportacao.equals(ArquivoExportadoSimplesNacional.TipoExportacao.INI)
                || tipoExportacao.equals(ArquivoExportadoSimplesNacional.TipoExportacao.ADC))) {
                if (itemArquivoSimplesNacionalProcessado.getTemDebitosVencidos()) {
                    itensArquivoProcessado.add(itemArquivoSimplesNacionalProcessado);
                }
            } else {
                if (!itemArquivoSimplesNacionalProcessado.getTemDebitosVencidos()) {
                    itensArquivoProcessado.add(itemArquivoSimplesNacionalProcessado);
                }
            }
            assistenteBarraProgresso.conta();
        }
        return new AsyncResult<>(itensArquivoProcessado);
    }

    private byte[] gerarBytesArquivoTxt(ArquivoExportadoSimplesNacional.TipoExportacao tipoExportacao,
                                        List<ItemArquivoSimplesNacionalProcessado> itensArquivoProcessado) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write((tipoExportacao.name() + "00000000000" + "\r\n").getBytes(StandardCharsets.UTF_8));
            for (ItemArquivoSimplesNacionalProcessado item : itensArquivoProcessado) {
                outputStream.write((item.getCnpj() + "\r\n").getBytes(StandardCharsets.UTF_8));
            }
            outputStream.write(("99999999999999").getBytes(StandardCharsets.UTF_8));
            return outputStream.toByteArray();
        }
    }

    public List<ItemArquivoSimplesNacional> buscarItensArquivoExportacaoArquivo(ArquivoInconsistenciaSimplesNacional arquivo,
                                                                                ArquivoExportadoSimplesNacional.TipoExportacao tipoExportacao) {
        arquivo = recuperarArquivoComItens(arquivo);
        switch (tipoExportacao) {
            case ADC: {
                return arquivo.getListaItensArquivoSimplesNacional().stream()
                    .filter(i -> !i.getEnviouInconsistencia())
                    .collect(Collectors.toList());
            }
            case EXC: {
                return arquivo.getListaItensArquivoSimplesNacional().stream()
                    .filter(i -> i.getEnviouInconsistencia())
                    .collect(Collectors.toList());
            }
        }
        return arquivo.getListaItensArquivoSimplesNacional();
    }

    @Override
    public void remover(ArquivoInconsistenciaSimplesNacional entity) {
        entity = recuperarArquivoComItens(entity);
        for (ItemArquivoSimplesNacional itemArquivoSimplesNacional : entity.getListaItensArquivoSimplesNacional()) {
            em.remove(itemArquivoSimplesNacional);
        }
        em.remove(entity);
    }
}
