package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.spc.dto.DadosExclusaoSpcDTO;
import br.com.webpublico.entidadesauxiliares.spc.dto.DadosInclusaoSpcDTO;
import br.com.webpublico.entidadesauxiliares.spc.dto.RetornoMensagemDTO;
import br.com.webpublico.entidadesauxiliares.spc.enums.TipoPessoa;
import br.com.webpublico.enums.tributario.TipoWebService;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.LeitorWsConfig;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.singletons.SingletonGeradorCodigoPorExercicio;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import org.hibernate.Hibernate;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class ProcessoCobrancaSPCFacade extends AbstractFacade<ProcessoCobrancaSPC> {

    private static final String AUTH_HEADER_NAME = "API-KEY";
    private static final String API_KEY = "fcc5f16f-452b-478e-8803-6d3d33fc68bf";

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigoPorExercicio singletonGeradorCodigoPorExercicio;
    @EJB
    private ParametroCobrancaSPCFacade parametroCobrancaSPCFacade;
    @EJB
    private LeitorWsConfig leitorWsConfig;
    @EJB
    private PessoaFacade pessoaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProcessoCobrancaSPCFacade() {
        super(ProcessoCobrancaSPC.class);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ParametroCobrancaSPCFacade getParametroCobrancaSPCFacade() {
        return parametroCobrancaSPCFacade;
    }

    @Override
    public ProcessoCobrancaSPC recuperar(Object id) {
        ProcessoCobrancaSPC processoCobrancaSPC = super.recuperar(id);
        if (processoCobrancaSPC != null) {
            Hibernate.initialize(processoCobrancaSPC.getItens());
            for (ItemProcessoCobrancaSPC itemProcesso : processoCobrancaSPC.getItens()) {
                itemProcesso.setResultadoParcela(recuperarResultadoParcela(itemProcesso.getParcelaValorDivida()));
            }
            if (processoCobrancaSPC.getDetentorArquivoComposicao() != null) {
                Hibernate.initialize(processoCobrancaSPC.getDetentorArquivoComposicao().getArquivosComposicao());
            }
        }
        return processoCobrancaSPC;
    }

    private ResultadoParcela recuperarResultadoParcela(ParcelaValorDivida parcelaValorDivida) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.PARCELA_ID,
            br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IGUAL,
            parcelaValorDivida.getId());
        List<ResultadoParcela> resultados = consultaParcela.executaConsulta().getResultados();
        return !resultados.isEmpty() ? resultados.get(0) : null;
    }

    @Override
    public void preSave(ProcessoCobrancaSPC entidade) {
        entidade.realizarValidacoes();
        if (entidade.getCodigo() == null) {
            entidade.setCodigo(singletonGeradorCodigoPorExercicio.getProximoCodigoDoExercicio(ProcessoCobrancaSPC.class,
                "codigo", "exercicio_id", entidade.getExercicio()));
        }
    }

    public ProcessoCobrancaSPC processar(ProcessoCobrancaSPC processoCobrancaSPC) {
        ConfiguracaoWebService configuracaoWebService = getConfiguracaoWebService();
        ParametroCobrancaSPC parametroCobrancaSPC = getParametroCobrancaSPC();

        processoCobrancaSPC = recuperar(processoCobrancaSPC.getId());

        for (ItemProcessoCobrancaSPC itemProcessoCobrancaSPC : processoCobrancaSPC.getItens()) {
            if (ItemProcessoCobrancaSPC.StatusSPC.AGUARDANDO_ENVIO.equals(itemProcessoCobrancaSPC.getStatusSPC())
                || ItemProcessoCobrancaSPC.StatusSPC.FALHA_INCLUSAO.equals(itemProcessoCobrancaSPC.getStatusSPC())) {
                inclusaoItemProcessoCobrancaSPC(configuracaoWebService, parametroCobrancaSPC, itemProcessoCobrancaSPC);
            }
        }

        if (processoCobrancaSPC.getItens().stream().allMatch(i -> i.getStatusSPC().equals(ItemProcessoCobrancaSPC.StatusSPC.INCLUIDO))) {
            processoCobrancaSPC.setSituacao(ProcessoCobrancaSPC.Situacao.PROCESSADO);
        } else if (processoCobrancaSPC.getItens().stream().anyMatch(i -> i.getStatusSPC().equals(ItemProcessoCobrancaSPC.StatusSPC.FALHA_INCLUSAO))) {
            processoCobrancaSPC.setSituacao(ProcessoCobrancaSPC.Situacao.PROCESSADO_INCONSISTENCIA);
        }
        return salvarRetornando(processoCobrancaSPC);
    }

    private void inclusaoItemProcessoCobrancaSPC(ConfiguracaoWebService configuracaoWebService,
                                                 ParametroCobrancaSPC parametroCobrancaSPC,
                                                 ItemProcessoCobrancaSPC itemProcessoCobrancaSPC) {
        try {
            DadosInclusaoSpcDTO dto = criarDadosInclusaoSPCDTO(parametroCobrancaSPC, itemProcessoCobrancaSPC);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add(AUTH_HEADER_NAME, API_KEY);

            HttpEntity<DadosInclusaoSpcDTO> httpEntity = new HttpEntity<>(dto, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<RetornoMensagemDTO> exchange = restTemplate.exchange(configuracaoWebService.getUrl() + "/inclusao-spc",
                HttpMethod.POST, httpEntity, RetornoMensagemDTO.class);
            if (exchange.getBody().getOk()) {
                itemProcessoCobrancaSPC.setStatusSPC(ItemProcessoCobrancaSPC.StatusSPC.INCLUIDO);
                inserirLogEnvioSPC(itemProcessoCobrancaSPC, LogEnvioSPC.TipoLog.INCLUSAO, "Incluído com sucesso.");
            } else {
                itemProcessoCobrancaSPC.setStatusSPC(ItemProcessoCobrancaSPC.StatusSPC.FALHA_INCLUSAO);
                inserirLogEnvioSPC(itemProcessoCobrancaSPC, LogEnvioSPC.TipoLog.INCLUSAO, exchange.getBody().getMensagem());
            }
        } catch (Exception e) {
            logger.error("Erro ao comunicar inclusão no spc. Erro: {}", e.getMessage());
            logger.debug("Detalhes do erro ao comunicar inclusão no spc.", e);
            itemProcessoCobrancaSPC.setStatusSPC(ItemProcessoCobrancaSPC.StatusSPC.FALHA_INCLUSAO);
            inserirLogEnvioSPC(itemProcessoCobrancaSPC, LogEnvioSPC.TipoLog.INCLUSAO, e.getMessage());
        }
    }

    public void inserirLogEnvioSPC(ItemProcessoCobrancaSPC itemProcessoCobrancaSPC,
                                   LogEnvioSPC.TipoLog tipoLog,
                                   String log) {
        LogEnvioSPC logEnvioSPC = new LogEnvioSPC();
        logEnvioSPC.setItemProcessoCobrancaSPC(itemProcessoCobrancaSPC);
        logEnvioSPC.setDataRegistro(new Date());
        logEnvioSPC.setTipoLog(tipoLog);
        logEnvioSPC.setLog(log);
        em.persist(logEnvioSPC);
    }

    private DadosInclusaoSpcDTO criarDadosInclusaoSPCDTO(ParametroCobrancaSPC parametroCobrancaSPC,
                                                         ItemProcessoCobrancaSPC itemProcessoCobrancaSPC) {
        Pessoa contribuinte = itemProcessoCobrancaSPC.getProcessoCobrancaSPC().getContribuinte();
        contribuinte = pessoaFacade.recuperar(contribuinte.getId());

        DadosInclusaoSpcDTO dto = new DadosInclusaoSpcDTO();

        dto.setNumeroContrato(itemProcessoCobrancaSPC.getParcelaValorDivida().getId().toString());
        dto.setTipoPessoa(contribuinte.isPessoaFisica() ? TipoPessoa.F : TipoPessoa.J);
        dto.setCpfCnpj(contribuinte.getCpf_Cnpj());
        dto.setNomeRazaoSocial(contribuinte.getNome());
        dto.setDataLancamento(itemProcessoCobrancaSPC.getParcelaValorDivida().getValorDivida().getCalculo().getDataCalculo());
        dto.setDataVencimento(itemProcessoCobrancaSPC.getParcelaValorDivida().getVencimento());
        dto.setValorDebito(itemProcessoCobrancaSPC.getParcelaValorDivida().getValor());
        dto.setIdNaturezaInclusao(parametroCobrancaSPC.getCodigoNatureza());
        dto.setCodigoTipoDevedor(parametroCobrancaSPC.getCodigoTipoDevedor());

        EnderecoCorreio endereco = contribuinte.getEnderecoPorPrioridade();

        dto.setBairro(endereco.getBairro());
        dto.setLogradouro(endereco.getLogradouro());
        dto.setNumero(endereco.getNumero());
        dto.setComplemento(endereco.getComplemento());
        dto.setCep(endereco.getCep());
        dto.setCidade(endereco.getLocalidade());
        dto.setUf(endereco.getUf());

        return dto;
    }

    public List<LogEnvioSPC> buscarLogsEnvioSPC(ItemProcessoCobrancaSPC item) {
        Query q = em.createNativeQuery(" select l.* from logenviospc l " +
            " where l.itemprocessocobrancaspc_id = :id " +
            " order by l.dataregistro ", LogEnvioSPC.class);
        q.setParameter("id", item.getId());
        return q.getResultList();
    }

    public ProcessoCobrancaSPC estornar(ProcessoCobrancaSPC processoCobrancaSPC) {
        ConfiguracaoWebService configuracaoWebService = getConfiguracaoWebService();
        ParametroCobrancaSPC parametroCobrancaSPC = getParametroCobrancaSPC();

        processoCobrancaSPC = recuperar(processoCobrancaSPC.getId());

        for (ItemProcessoCobrancaSPC itemProcessoCobrancaSPC : processoCobrancaSPC.getItens()) {
            if (ItemProcessoCobrancaSPC.StatusSPC.INCLUIDO.equals(itemProcessoCobrancaSPC.getStatusSPC())
                || ItemProcessoCobrancaSPC.StatusSPC.FALHA_EXCLUSAO.equals(itemProcessoCobrancaSPC.getStatusSPC())) {
                exclusaoItemProcessoCobrancaSPC(configuracaoWebService, parametroCobrancaSPC, itemProcessoCobrancaSPC);
            }
        }

        definirSituacaoEstorno(processoCobrancaSPC);

        return salvarRetornando(processoCobrancaSPC);
    }

    private static void definirSituacaoEstorno(ProcessoCobrancaSPC processoCobrancaSPC) {
        if (processoCobrancaSPC.getItens().stream().allMatch(i -> i.getStatusSPC().equals(ItemProcessoCobrancaSPC.StatusSPC.EXCLUIDO))) {
            processoCobrancaSPC.setSituacao(ProcessoCobrancaSPC.Situacao.ESTORNADO);
        } else if (processoCobrancaSPC.getItens().stream().anyMatch(i -> i.getStatusSPC().equals(ItemProcessoCobrancaSPC.StatusSPC.FALHA_EXCLUSAO))) {
            processoCobrancaSPC.setSituacao(ProcessoCobrancaSPC.Situacao.ESTORNADO_INCONSISTENCIA);
        }
    }

    private @NotNull ParametroCobrancaSPC getParametroCobrancaSPC() {
        ParametroCobrancaSPC parametroCobrancaSPC = parametroCobrancaSPCFacade.recuperarUltimo();
        if (parametroCobrancaSPC == null) {
            throw new ValidacaoException("Nenhum parâmetro de cobrança pelo SPC foi encontrado.");
        }
        return parametroCobrancaSPC;
    }

    private @NotNull ConfiguracaoWebService getConfiguracaoWebService() {
        ConfiguracaoWebService configuracaoWebService = leitorWsConfig.getConfiguracaoPorTipoDaKeyCorrente(TipoWebService.SPC);
        if (configuracaoWebService == null) {
            throw new ValidacaoException("Nenhuma configuração de webservice para comunicação com o SPC foi encontrada.");
        }
        return configuracaoWebService;
    }

    private void exclusaoItemProcessoCobrancaSPC(ConfiguracaoWebService configuracaoWebService,
                                                 ParametroCobrancaSPC parametroCobrancaSPC,
                                                 ItemProcessoCobrancaSPC itemProcessoCobrancaSPC) {
        try {
            DadosExclusaoSpcDTO dto = criarDadosExclusaoSPCDTO(parametroCobrancaSPC, itemProcessoCobrancaSPC);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add(AUTH_HEADER_NAME, API_KEY);

            HttpEntity<DadosExclusaoSpcDTO> httpEntity = new HttpEntity<>(dto, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<RetornoMensagemDTO> exchange = restTemplate.exchange(configuracaoWebService.getUrl() + "/exclusao-spc",
                HttpMethod.POST, httpEntity, RetornoMensagemDTO.class);
            if (exchange.getBody().getOk()) {
                itemProcessoCobrancaSPC.setStatusSPC(ItemProcessoCobrancaSPC.StatusSPC.EXCLUIDO);
                em.merge(itemProcessoCobrancaSPC);
                inserirLogEnvioSPC(itemProcessoCobrancaSPC, LogEnvioSPC.TipoLog.EXCLUSAO, "Excluído com sucesso.");
            } else {
                itemProcessoCobrancaSPC.setStatusSPC(ItemProcessoCobrancaSPC.StatusSPC.FALHA_EXCLUSAO);
                em.merge(itemProcessoCobrancaSPC);
                inserirLogEnvioSPC(itemProcessoCobrancaSPC, LogEnvioSPC.TipoLog.EXCLUSAO, exchange.getBody().getMensagem());
            }
        } catch (Exception e) {
            logger.error("Erro ao comunicar exclusão no spc. Erro: {}", e.getMessage());
            logger.debug("Detalhes do erro ao comunicar exclusão no spc.", e);
            itemProcessoCobrancaSPC.setStatusSPC(ItemProcessoCobrancaSPC.StatusSPC.FALHA_EXCLUSAO);
            em.merge(itemProcessoCobrancaSPC);
            inserirLogEnvioSPC(itemProcessoCobrancaSPC, LogEnvioSPC.TipoLog.EXCLUSAO, e.getMessage());
        }
    }

    private DadosExclusaoSpcDTO criarDadosExclusaoSPCDTO(ParametroCobrancaSPC parametroCobrancaSPC,
                                                         ItemProcessoCobrancaSPC itemProcessoCobrancaSPC) {
        Pessoa contribuinte = itemProcessoCobrancaSPC.getProcessoCobrancaSPC().getContribuinte();

        DadosExclusaoSpcDTO dto = new DadosExclusaoSpcDTO();

        dto.setNumeroContrato(itemProcessoCobrancaSPC.getParcelaValorDivida().getId().toString());
        dto.setTipoPessoa(contribuinte.isPessoaFisica() ? TipoPessoa.F : TipoPessoa.J);
        dto.setCpfCnpj(contribuinte.getCpf_Cnpj());
        dto.setDataVencimento(itemProcessoCobrancaSPC.getParcelaValorDivida().getVencimento());
        dto.setIdMotivoExclusao(parametroCobrancaSPC.getCodigoMotivoExclusao());
        return dto;
    }

    public List<ItemProcessoCobrancaSPC> buscarItensVinculadoIdParcela(Long idParcela) {
        Query query = em.createNativeQuery(" select i.* from itemprocessocobrancaspc i " +
            " where i.statusspc = :status " +
            "   and i.parcelavalordivida_id = :id " +
            " union " +
            " select i.* from itemprocessocobrancaspc i " +
            " where i.parcelavalordivida_id in (select ipp.parcelavalordivida_id " +
            "                                      from itemprocessoparcelamento ipp " +
            "                                     inner join processoparcelamento pp on pp.id = ipp.processoparcelamento_id " +
            "                                     inner join valordivida vd on vd.calculo_id = pp.id " +
            "                                     inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
            "                                   where pvd.id = :id)", ItemProcessoCobrancaSPC.class);
        query.setParameter("status", ItemProcessoCobrancaSPC.StatusSPC.INCLUIDO.name());
        query.setParameter("id", idParcela);
        return query.getResultList();
    }

    public void estornarParcelaPaga(Long idParcela) {
        try {
            List<ItemProcessoCobrancaSPC> itensProcesso = buscarItensVinculadoIdParcela(idParcela);
            if (itensProcesso != null) {
                for (ItemProcessoCobrancaSPC itemProcessoCobrancaSPC : itensProcesso) {
                    ConfiguracaoWebService configuracaoWebService = getConfiguracaoWebService();
                    ParametroCobrancaSPC parametroCobrancaSPC = getParametroCobrancaSPC();
                    exclusaoItemProcessoCobrancaSPC(configuracaoWebService, parametroCobrancaSPC, itemProcessoCobrancaSPC);
                    ProcessoCobrancaSPC processoCobrancaSPC = recuperar(itemProcessoCobrancaSPC.getProcessoCobrancaSPC().getId());
                    definirSituacaoEstorno(processoCobrancaSPC);
                    salvar(processoCobrancaSPC);
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao estornar parcela paga no spc. Erro: {}", e.getMessage());
            logger.debug("Detalhes do erro ao estornar parcela paga no spc.", e);
        }
    }
}
