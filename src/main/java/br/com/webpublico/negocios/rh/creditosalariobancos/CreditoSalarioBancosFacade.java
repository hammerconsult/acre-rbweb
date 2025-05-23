package br.com.webpublico.negocios.rh.creditosalariobancos;


import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.creditodesalario.*;
import br.com.webpublico.entidadesauxiliares.ItemRelatorioConferenciaCreditoSalario;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorioConferenciaCreditoSalario;
import br.com.webpublico.entidadesauxiliares.rh.creditosalario.DependenciasCreditoSalario;
import br.com.webpublico.entidadesauxiliares.rh.creditosalario.LoteCreditoSalario;
import br.com.webpublico.entidadesauxiliares.rh.creditosalario.VinculoCreditoSalario;
import br.com.webpublico.enums.ModalidadeConta;
import br.com.webpublico.enums.TipoGeracaoCreditoSalario;
import br.com.webpublico.enums.rh.creditosalario.BancoCreditoSalario;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.annotation.Campo;
import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exportador.ExportadorTamanhoFixo;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.arquivo.CNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.arquivo.FileCNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.arquivo.HeaderCNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.arquivo.TrailerCNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.bancodobrasil.BancoDoBrasilHeaderCNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.bancodobrasil.BancoDoBrasilHeaderLoteCNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.bradesco.BradescoHeaderCNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.exception.CreditoSalarioException;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.exception.CreditoSalarioExistenteException;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.lote.HeaderLoteCNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.lote.TrailerLoteCNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.segmento.SegmentoACNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.segmento.SegmentoBCNAB240;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static br.com.webpublico.util.StringUtil.cortaOuCompletaDireita;
import static br.com.webpublico.util.StringUtil.cortaOuCompletaEsquerda;

@Stateless
public class CreditoSalarioBancosFacade extends AbstractFacade<CreditoSalario> {

    @PersistenceContext
    protected EntityManager em;
    static final String CODIGO_TED = "03";

    @EJB
    private CreditoSalarioFacade creditoSalarioFacade;
    @EJB
    private ItemCreditoSalarioFacade itemCreditoSalarioFacade;

    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    @EJB
    private EnderecoCorreioFacade enderecoCorreioFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    private ConfiguracaoRH configuracaoRH;
    private String identificador;

    public CreditoSalarioBancosFacade() {
        super(CreditoSalario.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoRHFacade getConfiguracaoRHFacade() {
        return configuracaoRHFacade;
    }

    @Override
    public CreditoSalario recuperar(Object id) {
        CreditoSalario creditoSalario = getEntityManager().find(CreditoSalario.class, id);
        Hibernate.initialize(creditoSalario.getItensCreditoSalario());
        Hibernate.initialize(creditoSalario.getItemLogCreditoSalario());
        for (ItemCreditoSalario itemCreditoSalario : creditoSalario.getItensCreditoSalario()) {
            if (itemCreditoSalario.getArquivo() != null) {
                Hibernate.initialize(itemCreditoSalario.getArquivo().getArquivosComposicao());
            }
            Hibernate.initialize(itemCreditoSalario.getItensVinculoCreditoSalario());
        }
        return creditoSalario;
    }


    public Integer buscarUltimoNumeroSequencial() {
        String hql = "select coalesce(max(c.sequencial),0) from ItemCreditoSalario c ";
        Query q = em.createQuery(hql);
        Integer numeroSequencia = 0;
        if (!q.getResultList().isEmpty()) {
            numeroSequencia = (Integer) q.getResultList().get(0);
        }
        return numeroSequencia + 1;
    }

    public List<VinculoFP> buscarContratoPorNomeOrUnidadeOrNumeroOrMatriculaFP(String parte, Date dataVigencia) {
        String sql = " SELECT v.id, M.MATRICULA " +
            " ||'/' " +
            " ||V.NUMERO " +
            " ||' - ' " +
            " ||PF.NOME " +
            " ||' ' " +
            " || " +
            " CASE WHEN V.ID = A.ID THEN ' - APOSENTADO' ELSE '' END " +
            " ||' ' " +
            " || " +
            " CASE WHEN V.ID = P.ID THEN ' - PENSIONISTA' ELSE '' END " +
            " FROM VINCULOFP V " +
            " INNER JOIN MATRICULAFP M " +
            " ON M.id = V.MATRICULAFP_ID " +
            " INNER JOIN PESSOAFISICA PF " +
            " ON PF.id = M.PESSOA_ID " +
            " INNER JOIN UNIDADEORGANIZACIONAL UO " +
            " ON UO.ID = M.UNIDADEMATRICULADO_ID " +
            " LEFT JOIN APOSENTADORIA A ON A.ID = V.ID " +
            " LEFT JOIN PENSIONISTA P ON P.ID = V.ID " +
            "WHERE ((lower(pf.nome) like :filtro) " +
            "or (lower(UO.DESCRICAO) like :filtro) " +
            "or (lower(V.NUMERO) LIKE :filtro) " +
            "or (lower(m.matricula) like :filtro)) " +
            (dataVigencia != null ? " and to_date(:dataVigencia, 'dd/mm/yyyy') between v.iniciovigencia and coalesce(v.finalvigencia, to_date(:dataVigencia, 'dd/mm/yyyy')) " : " ") +
            "order by TO_NUMBER(M.MATRICULA)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("filtro", "%" + parte.toLowerCase().trim() + "%");
        if (dataVigencia != null) {
            q.setParameter("dataVigencia", DataUtil.getDataFormatada(dataVigencia));
        }
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        List resultList = q.getResultList();
        List<VinculoFP> retorno = Lists.newArrayList();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;
            Number idVinculo = (Number) objeto[0];
            String descricaoVinculo = (String) objeto[1];
            VinculoFP vinculoFP = new VinculoFP(idVinculo.longValue(), descricaoVinculo);
            retorno.add(vinculoFP);
        }
        return retorno;
    }

    public List<BeneficioPensaoAlimenticia> buscarBeneficiarioPensaoAlimenticiaVigentePorVinculo(VinculoFP vinculoFP, Date data) {
        String hql = "select beneficio " +
            " from PensaoAlimenticia pensao " +
            " inner join pensao.beneficiosPensaoAlimenticia beneficio " +
            " where pensao.vinculoFP.id = :instituidor " +
            " and to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between to_date(to_char(beneficio.inicioVigencia,'mm/yyyy'),'mm/yyyy') " +
            "       and to_date(to_char(coalesce(beneficio.finalVigencia, :data),'mm/yyyy'),'mm/yyyy')";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("instituidor", vinculoFP.getId());
        q.setParameter("data", data);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }


    public List<CreditoSalario> buscarCreditoSalarioExistente(CreditoSalario creditoSalario) {
        String sql = "select c.* from CreditoSalario c " +
            " inner join contabancariaentidade contaBancariaEntidade on c.contabancariaentidade_id = contaBancariaEntidade.id " +
            " inner join folhadepagamento folha on c.folhadepagamento_id = folha.id " +
            "where contaBancariaEntidade.id = :contaBancaria " +
            " and folha.id = :folhaPagamento " +
            " and c.tipogeracaocreditosalario = :tipoGeracaoCreditoSalario ";
        Query q = em.createNativeQuery(sql, CreditoSalario.class);
        q.setParameter("contaBancaria", creditoSalario.getContaBancariaEntidade().getId());
        q.setParameter("folhaPagamento", creditoSalario.getFolhaDePagamento().getId());
        q.setParameter("tipoGeracaoCreditoSalario", creditoSalario.getTipoGeracaoCreditoSalario().name());
        return q.getResultList();
    }

    private boolean isTipoGeracaoServidor(CreditoSalario selecionado) {
        return TipoGeracaoCreditoSalario.SERVIDORES.name().equals(selecionado.getTipoGeracaoCreditoSalario().name());
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public DependenciasCreditoSalario gerarCreditoSalario(CreditoSalario creditoSalario, DependenciasCreditoSalario dependenciasCreditoSalario, List<GrupoRecursoFP> gruposRecursoFP, List<VinculoFP> vinculosFPS, List<HierarquiaOrganizacional> hierarquiaOrganizacionals, List<RecursoFP> recursos, BeneficioPensaoAlimenticia[] beneficiarios, Boolean omitirMatricula) throws Exception {
        creditoSalario.setGrupos(gruposRecursoFP);
        creditoSalario.setRecursos(recursos);
        creditoSalario.setHierarquiaOrganizacionals(hierarquiaOrganizacionals);
        criarFiltrosCreditoSalario(creditoSalario);
        creditoSalario.setVinculoFPS(vinculosFPS);
        creditoSalario.setBeneficiarios(beneficiarios);
        dependenciasCreditoSalario.setTotalCadastros(0);
        gerarLogs(creditoSalario, dependenciasCreditoSalario);
        buscarVinculosParaCreditoSalario(creditoSalario, dependenciasCreditoSalario, omitirMatricula);
        List<ItemRelatorioConferenciaCreditoSalario> itensRelatorio = Lists.newLinkedList();
        ParametrosRelatorioConferenciaCreditoSalario parametrosRelatorioGeral = montarParametrosRelatorioConferenciaCreditoSalario(creditoSalario);
        for (Map.Entry<GrupoRecursoFP, Set<VinculoCreditoSalario>> mapVinculos : dependenciasCreditoSalario.getVinculos().entrySet()) {
            logger.debug("Grupo {} total {}", mapVinculos.getKey(), mapVinculos.getValue().size());
            gerarCreditoSalarioIndividual(creditoSalario, dependenciasCreditoSalario, mapVinculos.getKey(), mapVinculos.getValue(), creditoSalario.getNumeroRemessa(), itensRelatorio, parametrosRelatorioGeral);
            creditoSalario.setNumeroRemessa(creditoSalario.getNumeroRemessa() + 1);
        }
        parametrosRelatorioGeral.getItens().addAll(itensRelatorio);
        gravarArquivoRelatorioGeral(creditoSalario, dependenciasCreditoSalario, parametrosRelatorioGeral);
        creditoSalario = creditoSalarioFacade.salvarRetornando(creditoSalario);
        dependenciasCreditoSalario.pararProcessamento();
        return dependenciasCreditoSalario;
    }

    public void gerarCreditoSalarioIndividual(CreditoSalario creditoSalario, DependenciasCreditoSalario dependenciasCreditoSalario, GrupoRecursoFP gruposRecursoFP, Set<VinculoCreditoSalario> vinculosFPS, Integer sequencialRemessa, List<ItemRelatorioConferenciaCreditoSalario> itensRelatorio, ParametrosRelatorioConferenciaCreditoSalario parametrosRelatorioGeral) throws Exception {
        try {
            logger.debug("GERAR INDIVIDUAL");
            identificador = null;
            //Cabecalho
            List<CNAB240> cnabs = Lists.newLinkedList();
            dependenciasCreditoSalario.setContadorLinha(0);
            cnabs.add(montarHeaderCNAB240(creditoSalario, creditoSalario.getBancoCreditoSalario(), dependenciasCreditoSalario, sequencialRemessa));
            List<LoteCreditoSalario> lotes = criarLotes(creditoSalario, gruposRecursoFP, vinculosFPS);//TODO verificar necessidade vários lotes
            List<VinculoFPCreditoSalario> vinculosCreditoSalario = Lists.newLinkedList();

            ParametrosRelatorioConferenciaCreditoSalario parametros = montarParametrosRelatorioConferenciaCreditoSalario(creditoSalario);
            creditoSalario.setParametrosRelatorioConferenciaCreditoSalario(parametros);
            List<ItemRelatorioConferenciaCreditoSalario> itens = Lists.newLinkedList();
            BigDecimal valorTotalCredito = new BigDecimal(BigInteger.ZERO);
            for (int i = 0; i < lotes.size(); i++) {
                LoteCreditoSalario lote = lotes.get(i);
                lote.setPosicao(i + 1);

                //Lotes, segmento A e B
                HeaderLoteCNAB240 loteCNAB = montarHeaderLoteCNAB240(creditoSalario, creditoSalario.getBancoCreditoSalario(), dependenciasCreditoSalario, lote);
                cnabs.add(loteCNAB);

                try {
                    processarSegmentos(creditoSalario, dependenciasCreditoSalario, cnabs, vinculosCreditoSalario, loteCNAB, lote, lote.getVinculosFPS(), itens, itensRelatorio);
                } catch (CreditoSalarioException e) {
                    logger.error("Iconsistência encontrada: ", e);
                    dependenciasCreditoSalario.getLogIncosistencia().add(e.getMessage());
                    dependenciasCreditoSalario.incrementarContadorProcesso();
                }
                valorTotalCredito = valorTotalCredito.add(lote.getValorLote());
                TrailerLoteCNAB240 trailerLoteCNAB240 = montarTrailerLoteCNAB240(creditoSalario, creditoSalario.getBancoCreditoSalario(), dependenciasCreditoSalario, lote);
                cnabs.add(trailerLoteCNAB240);
            }
            creditoSalario.getParametrosRelatorioConferenciaCreditoSalario().setTotalCredito(creditoSalario.getParametrosRelatorioConferenciaCreditoSalario().getTotalCredito().add(valorTotalCredito));
            creditoSalario.getParametrosRelatorioConferenciaCreditoSalario().setQuantidadeRegistros(creditoSalario.getParametrosRelatorioConferenciaCreditoSalario().getQuantidadeRegistros() + itens.size());
            creditoSalario.getParametrosRelatorioConferenciaCreditoSalario().getItens().addAll(itens);
            parametrosRelatorioGeral.getTotalCredito().add(creditoSalario.getParametrosRelatorioConferenciaCreditoSalario().getTotalCredito());
            parametrosRelatorioGeral.setQuantidadeRegistros(parametrosRelatorioGeral.getQuantidadeRegistros() + creditoSalario.getParametrosRelatorioConferenciaCreditoSalario().getQuantidadeRegistros());

            //Trailer arquivo
            cnabs.add(montarTrailerCNAB240(creditoSalario, creditoSalario.getBancoCreditoSalario(), dependenciasCreditoSalario, lotes));
            //logger.debug("Detalhado: {}", dependenciasCreditoSalario.getLogDetalhado());
            gravarArquivo(cnabs, creditoSalario, gruposRecursoFP, vinculosCreditoSalario, dependenciasCreditoSalario);
            dependenciasCreditoSalario.setCreditoSalario(creditoSalario);
        } catch (Exception e) {
            logger.error("Erro: ", e);
            dependenciasCreditoSalario.pararProcessamento();
        } finally {
            logger.debug("Terminando a geração do crédito de salário.");
        }
    }

    private List<LoteCreditoSalario> criarLotes(CreditoSalario creditoSalario, GrupoRecursoFP gruposRecursoFP, Set<VinculoCreditoSalario> vinculosFPS) {
        Map<String, Set<VinculoCreditoSalario>> vinculosFPSMesmoBanco = Maps.newLinkedHashMap();
        List<LoteCreditoSalario> lotes = Lists.newLinkedList();
        for (VinculoCreditoSalario vinculosFP : vinculosFPS) {
            ContaCorrenteBancaria conta = vinculosFP.getConta();
            if (conta != null && conta.getModalidadeConta() != null && isMesmoBanco(creditoSalario, vinculosFP)) {
                adicionarMapLoteVinculo(conta.getModalidadeConta().getFormaLancamento(), vinculosFP, vinculosFPSMesmoBanco);//01 ou 05(conta poupança) crédito em conta corrente
            } else {
                adicionarMapLoteVinculo(CODIGO_TED, vinculosFP, vinculosFPSMesmoBanco); //03 TED em conta
            }
        }
        for (Map.Entry<String, Set<VinculoCreditoSalario>> mapMesmoVinculo : vinculosFPSMesmoBanco.entrySet()) {
            LoteCreditoSalario lote = new LoteCreditoSalario();
            lote.setFormaLancamento(mapMesmoVinculo.getKey());
            lote.setVinculosFPS(mapMesmoVinculo.getValue());
            lote.setGruposRecursoFP(gruposRecursoFP);
            lotes.add(lote);
        }

        return lotes;
    }

    private boolean isMesmoBanco(CreditoSalario creditoSalario, VinculoCreditoSalario vinculosFP) {
        return creditoSalario.getBancoCreditoSalario().getCodigo().equals(vinculosFP.getConta().getBanco().getNumeroBanco());
    }

    private void adicionarMapLoteVinculo(String s, VinculoCreditoSalario vinculosFP, Map<String, Set<VinculoCreditoSalario>> vinculosFPSMesmoBanco) {
        if (vinculosFPSMesmoBanco.containsKey(s)) {
            vinculosFPSMesmoBanco.get(s).add(vinculosFP);
        } else {
            vinculosFPSMesmoBanco.put(s, Sets.newHashSet(vinculosFP));
        }
    }

    private void processarSegmentos(CreditoSalario creditoSalario, DependenciasCreditoSalario dependenciasCreditoSalario, List<CNAB240> cnabs, List<VinculoFPCreditoSalario> vinculosCreditoSalario, HeaderLoteCNAB240 lote, LoteCreditoSalario loteCreditoSalario, Set<VinculoCreditoSalario> vinculosFPS, List<ItemRelatorioConferenciaCreditoSalario> itens, List<ItemRelatorioConferenciaCreditoSalario> itensRelatorio) throws CreditoSalarioException {


        for (VinculoCreditoSalario vinculo : vinculosFPS) {
            try {
                logger.debug("Processando pessoas, [{}]", vinculo.getVinculoFP());
                VinculoFPCreditoSalario itemVinculo = new VinculoFPCreditoSalario();
                itemVinculo.setVinculoFP(vinculo.getVinculoFP());
                if (creditoSalario.isTipoArquivoPensionista()) {
                    itemVinculo.setPessoaFisica(vinculo.getPessoa());
                }
                itemVinculo.setValorLiquido(vinculo.getValor());
                definirIdentificador(creditoSalario, itemVinculo);




                cnabs.add(montarSegmentoA(creditoSalario, creditoSalario.getBancoCreditoSalario(), vinculo, lote, dependenciasCreditoSalario, loteCreditoSalario, itemVinculo));
                cnabs.add(montarSegmentoB(creditoSalario, creditoSalario.getBancoCreditoSalario(), vinculo, lote, dependenciasCreditoSalario, loteCreditoSalario));
                vinculosCreditoSalario.add(itemVinculo);
                dependenciasCreditoSalario.incrementarContadorProcesso();

                ItemRelatorioConferenciaCreditoSalario itemRelatorio = montarItemRelatorioConferenciaCreditoSalario(vinculo);
                itens.add(itemRelatorio);
                itensRelatorio.add(itemRelatorio);
            } catch (CreditoSalarioException e) {
                logger.error("Inconsistência encontrada: ", e);
                dependenciasCreditoSalario.getLogIncosistencia().add(e.getMessage());
                dependenciasCreditoSalario.incrementarContadorProcesso();
            } catch (Exception e) {
                logger.error("Inconsistência encontrada: ", e);
                dependenciasCreditoSalario.getLogIncosistencia().add(e.getMessage());
                dependenciasCreditoSalario.incrementarContadorProcesso();
                adicionarLogCreditoSalario(creditoSalario, vinculo, loteCreditoSalario, e.getMessage());
            }
        }


    }

    private void adicionarLogCreditoSalario(CreditoSalario creditoSalario, VinculoCreditoSalario vinculo, LoteCreditoSalario loteCreditoSalario, String mensagem) {
        LogCreditoSalario log = new LogCreditoSalario();
        log.setCreditoSalario(creditoSalario);
        log.setVinculoFP(vinculo != null ? vinculo.getVinculoFP() : null);
        log.setValorAReceber(vinculo != null ? vinculo.getValor() : null);
        log.setContaCorrenteBancaria(vinculo != null ? vinculo.getConta() : null);
        log.setOrgao(loteCreditoSalario.getGruposRecursoFP().getHierarquiaOrganizacional().toString());
        log.setLog(mensagem);
        creditoSalario.getItemLogCreditoSalario().add(log);
    }

    private void definirIdentificador(CreditoSalario creditoSalario, VinculoFPCreditoSalario itemVinculo) {
        FolhaDePagamento folha = creditoSalario.getFolhaDePagamento();
        String identificador = folha.getAno().toString();
        identificador += cortaOuCompletaEsquerda(String.valueOf(folha.getMes().getNumeroMes()), 2, "0");
        identificador += cortaOuCompletaEsquerda(folha.getTipoFolhaDePagamento().getCodigo().toString(), 2, "0");
        identificador += RandomStringUtils.randomAlphanumeric(12);
        itemVinculo.setIdentificador(identificador);
    }

    private ParametrosRelatorioConferenciaCreditoSalario montarParametrosRelatorioConferenciaCreditoSalario(CreditoSalario creditoSalario) {
        ParametrosRelatorioConferenciaCreditoSalario parametros = new ParametrosRelatorioConferenciaCreditoSalario();
        parametros.setOrgao(creditoSalario.getHierarquiaOrganizacional().toString());
        parametros.setConta(creditoSalario.getContaBancariaEntidade().toString());
        parametros.setAgencia(creditoSalario.getContaBancariaEntidade().getAgencia().toStringAgencia());
        parametros.setBanco(creditoSalario.getContaBancariaEntidade().getAgencia().getBanco().toString());
        return parametros;
    }

    private void criarFiltrosCreditoSalario(CreditoSalario creditoSalario) {
        List<FiltroCreditoSalario> filtros = Lists.newLinkedList();
        for (GrupoRecursoFP grupoRecursoFP : creditoSalario.getGrupos()) {
            filtros.add(new FiltroCreditoSalario(creditoSalario, grupoRecursoFP, null, null));
        }
        for (RecursoFP recursoFp : creditoSalario.getRecursos()) {
            filtros.add(new FiltroCreditoSalario(creditoSalario, null, null, recursoFp));
        }
        for (HierarquiaOrganizacional hierarquia : creditoSalario.getHierarquiaOrganizacionals()) {
            filtros.add(new FiltroCreditoSalario(creditoSalario, null, hierarquia.getSubordinada(), null));
        }
        creditoSalario.setFiltroCreditoSalarios(filtros);
    }

    public List<VinculoCreditoSalario> buscarVinculosParaCreditoSalario(CreditoSalario creditoSalario, Boolean omitirMatricula) {
        List<VinculoCreditoSalario> retorno = new ArrayList<>();
        for (Object[] obj : folhaDePagamentoFacade.buscarVinculosParaCreditoSalario(creditoSalario, omitirMatricula)) {
            VinculoFP vinculo = vinculoFPFacade.recuperarSimples((((BigDecimal) obj[0]).longValue()));
            VinculoCreditoSalario vinculoCreditoSalario = new VinculoCreditoSalario();
            vinculoCreditoSalario.setIdVinculo(((BigDecimal) obj[0]).longValue());
            vinculoCreditoSalario.setMatricula((String) obj[1]);
            vinculoCreditoSalario.setVinculoFP(vinculo);
            vinculoCreditoSalario.setPessoa(vinculo.getMatriculaFP().getPessoa().getAsPessoaFisica());
            vinculoCreditoSalario.setValor((BigDecimal) obj[3]);
            vinculoCreditoSalario.setGrupo((String) obj[4]);
            vinculoCreditoSalario.setRecursoFPId(((BigDecimal) obj[5]).longValue());
            vinculoCreditoSalario.setUnidadeId(((BigDecimal) obj[6]).longValue());
            vinculoCreditoSalario.setGrupoId(((BigDecimal) obj[7]).longValue());
            retorno.add(vinculoCreditoSalario);
        }
        return retorno;
    }


    private void buscarVinculosParaCreditoSalario(CreditoSalario creditoSalario, DependenciasCreditoSalario dependenciasCreditoSalario, Boolean omitirMatricula) {
        List<VinculoCreditoSalario> vinculos = Lists.newLinkedList();
        if (TipoGeracaoCreditoSalario.SERVIDORES.equals(creditoSalario.getTipoGeracaoCreditoSalario())) {
            vinculos.addAll(buscarVinculosParaCreditoSalario(creditoSalario, omitirMatricula));
        } else {
            vinculos.addAll(folhaDePagamentoFacade.buscarPensionistaParaCreditoSalario(creditoSalario, omitirMatricula));
        }
        for (VinculoCreditoSalario vinculo : vinculos) {
            VinculoFP recuperado = vinculoFPFacade.recuperarSimples(vinculo.getIdVinculo());
            vinculo.setVinculoFP(recuperado);

            if (vinculo.getConta() == null && recuperado.getContaCorrente() != null) {
                vinculo.setConta(recuperado.getContaCorrente());
            }
            if ((vinculo.getConta() != null && (vinculo.getConta().getSituacao() == null || !vinculo.getConta().getSituacao().isAtivo())) || (vinculo.getConta() == null && recuperado.getContaCorrente() == null)) {
                vinculo.setConta(pessoaFisicaFacade.buscarContaCorrenteDaPessoa(recuperado, vinculo.getPessoa(), creditoSalario));
            }
        }
        Collections.sort(vinculos, new Comparator<VinculoCreditoSalario>() {
            @Override
            public int compare(VinculoCreditoSalario o1, VinculoCreditoSalario o2) {
                if (o1.getPessoa() != null && o2.getPessoa() != null) {
                    return o1.getPessoa().getNome().compareTo(o2.getPessoa().getNome());
                } else {
                    return 0;
                }
            }
        });

        if (creditoSalario.getVinculoFPS() != null && !creditoSalario.getVinculoFPS().isEmpty()) {
            List<Long> vinculoFPIds = creditoSalario.getVinculoFPS().stream().map(VinculoFP::getId).collect(Collectors.toList());
            creditoSalarioFacade.getGrupoRecursoFPFacade().buscarGrupoRecursoFPsPorVinculoFPs(vinculoFPIds).forEach(grupo -> {
                if (!creditoSalario.getGrupos().contains(grupo)) {
                    creditoSalario.getGrupos().add(grupo);
                }
            });
        }

        Map<GrupoRecursoFP, Set<VinculoCreditoSalario>> vinculosMap = filtrarVinculosCreditoSalario(Sets.newLinkedHashSet(vinculos), creditoSalario);
        dependenciasCreditoSalario.setVinculos(vinculosMap);
        Integer totalRegistros = 0;
        for (Map.Entry<GrupoRecursoFP, Set<VinculoCreditoSalario>> grupoRecursoFPSetEntry : vinculosMap.entrySet()) {
            totalRegistros = totalRegistros + grupoRecursoFPSetEntry.getValue().size();
        }
        dependenciasCreditoSalario.setTotalCadastros(totalRegistros);
    }

    private Map<GrupoRecursoFP, Set<VinculoCreditoSalario>> filtrarVinculosCreditoSalario(Set<VinculoCreditoSalario> vinculos, CreditoSalario creditoSalario) {
        Map<GrupoRecursoFP, Set<VinculoCreditoSalario>> mapVinculosFiltrados = Maps.newLinkedHashMap();


        for (VinculoCreditoSalario vinculo : vinculos) {
            for (GrupoRecursoFP grupoRecursoFP : creditoSalario.getGrupos()) {
                if (vinculo.getGrupoId().equals(grupoRecursoFP.getId())) {
                    adicionarMapVinculo(grupoRecursoFP, vinculo, mapVinculosFiltrados);
                }
            }
        }
        for (VinculoCreditoSalario vinculo : vinculos) {
            for (RecursoFP recursoFp : creditoSalario.getRecursos()) {
                if (vinculo.getRecursoFPId().equals(recursoFp.getId())) {
                    adicionarMapVinculo(null, vinculo, mapVinculosFiltrados);
                }
            }
        }
        for (VinculoCreditoSalario vinculo : vinculos) {
            for (HierarquiaOrganizacional hierarquia : creditoSalario.getHierarquiaOrganizacionals()) {
                if (vinculo.getUnidadeId().equals(hierarquia.getSubordinada().getId())) {
                    adicionarMapVinculo(null, vinculo, mapVinculosFiltrados);
                }
            }
        }
        return mapVinculosFiltrados;
    }

    private void adicionarMapVinculo(GrupoRecursoFP grupoRecursoFP, VinculoCreditoSalario vinculo, Map<GrupoRecursoFP, Set<VinculoCreditoSalario>> mapVinculosFiltrados) {
        if (mapVinculosFiltrados.containsKey(grupoRecursoFP)) {
            mapVinculosFiltrados.get(grupoRecursoFP).add(vinculo);
        } else {
            mapVinculosFiltrados.put(grupoRecursoFP, Sets.newHashSet(vinculo));
        }
    }

    private void gerarLogs(CreditoSalario creditoSalario, DependenciasCreditoSalario assistente) {
        assistente.getLogDetalhado().append("Banco:").append(creditoSalario.getBancoCreditoSalario().getDescricao()).append("\n");
        assistente.getLogDetalhado().append("Hora:").append(new Date()).append("\n");
        //assistente.getLogDetalhado().append("Usuario:").append(sistemaService.getUsuarioCorrente()).append("\n");
    }

    public HeaderCNAB240 montarHeaderCNAB240(CreditoSalario creditoSalario, BancoCreditoSalario banco, DependenciasCreditoSalario assistente, Integer sequencialRemessa) {
        Entidade entidade = creditoSalario.getContaBancariaEntidade().getEntidade();
        HeaderCNAB240 e = banco.getNewInstanceHeader();
        e.setBanco(banco.getCodigo());
        e.setLote("0000");
        e.setRegistro("0");
        e.setUsoExlcusivo(StringUtil.cortaOuCompletaEsquerda("", 9, " "));
        e.setTipoInscricao("2");//CNPJ
        e.setNumeroInscricao(entidade.getPessoaJuridica().getCnpjSemFormatacao());
        e.setCodigoConvenio(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getCodigoDoConvenio(), 20, "0"));
        e.setAgenciaMantenedora(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getAgencia().getNumeroAgencia(), 5, "0"));
        e.setDigitoAgenciaMantenedora(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getAgencia().getDigitoVerificador(), 1, "X"));
        e.setNumeroConta(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getNumeroConta(), 12, "0"));
        e.setDigitoVerificador(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getDigitoVerificador(), 1, "0"));
        e.setDigitoVerificadorAgenciaConta(cortaOuCompletaEsquerda(" ", 1, " "));//Todo verificar onde pegar essa informação
        e.setNomeEmpresa(cortaOuCompletaDireita(StringUtil.removeCaracteresEspeciaisSemEspaco(entidade.getNome()), 30, " "));
        e.setNomeBanco(cortaOuCompletaDireita(StringUtil.removeCaracteresEspeciaisSemEspaco(creditoSalario.getContaBancariaEntidade().getAgencia().getBanco().getDescricao()), 30, " "));
        e.setUsoExlcusivo2(cortaOuCompletaDireita("", 10, " "));

        e.setCodigoRemessa("1");//remessa(1) ou retorno(2)
        Date data = new Date();
        e.setDataGeracao(DataUtil.getDataFormatada(data, "ddMMyyyy"));
        e.setHoraGeracao(DataUtil.getDataFormatada(data, "HHmmss"));
        e.setSequencial(cortaOuCompletaEsquerda(sequencialRemessa + "", 6, "0"));
        e.setVersaoLayout(banco.getVersao());
        e.setDensidade(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getDensidade() != null ? creditoSalario.getContaBancariaEntidade().getDensidade() : "0", 5, "0"));//TODO verificar

        e.setReservadoBanco(cortaOuCompletaEsquerda(" ", 20, " "));
        identificador = RandomStringUtils.randomAlphanumeric(20).toUpperCase();
        e.setReservadoEmpresa(identificador);
        e.setUsoExlcusivo3(cortaOuCompletaEsquerda(" ", 29, " "));//TODO implementar identificação no arquivo.

        preencherParticularidadesHeaderBanco(e, creditoSalario, banco);
        gerarLogDetalhado(e, assistente);
        assistente.incrementarContadorLinha();
        return e;
    }

    private void preencherParticularidadesHeaderBanco(HeaderCNAB240 e, CreditoSalario creditoSalario, BancoCreditoSalario banco) {
        switch (banco) {
            case BANCO_BRASIL:
                preencherParticularidadeHeaderBancoBrasil((BancoDoBrasilHeaderCNAB240) e, creditoSalario, banco);
                break;
            case BRADESCO:
                preencherParticularidadeHeaderBancoBradesco((BradescoHeaderCNAB240) e, banco, creditoSalario);
                break;
            case CAIXA:
                preencherParticularidadeHeaderCaixa(e, creditoSalario, banco);
                break;
            case SICREDI:
                preencherParticularidadeHeaderSicredi(e, creditoSalario, banco);
                break;
            case ITAU:
                preencherParticularidadeHeaderItau(e, creditoSalario, banco);
                break;
            default:
                break;
        }
    }

    private void preencherParticularidadeHeaderBancoBrasil(BancoDoBrasilHeaderCNAB240 header, CreditoSalario creditoSalario, BancoCreditoSalario banco) {
        StringBuilder convenioBB = new StringBuilder();
        convenioBB.append(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getCodigoDoConvenio(), 9, "0"));
        convenioBB.append(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getCarteiraCedente(), 4, "0"));
        convenioBB.append(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getNumeroCarteiraCobranca(), 2, " "));
        convenioBB.append(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getVariacaoCarteiraCobranca(), 3, " "));
        convenioBB.append(cortaOuCompletaEsquerda("", 2, " "));
        header.setCodigoConvenio(convenioBB.toString());
    }

    private void preencherParticularidadeHeaderBancoBradesco(BradescoHeaderCNAB240 header, BancoCreditoSalario banco, CreditoSalario creditoSalario) {
        header.setCodigoConvenio(cortaOuCompletaDireita(creditoSalario.getContaBancariaEntidade().getCodigoDoConvenio(), 20, " "));
        header.setDensidade(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getDensidade() != null ? creditoSalario.getContaBancariaEntidade().getDensidade() : "0", 5, "0"));
        header.setVersaoLayout(banco.getVersao());
    }

    private void preencherParticularidadeHeaderCaixa(HeaderCNAB240 e, CreditoSalario creditoSalario, BancoCreditoSalario banco) {
        String codigoConvenio = cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getCodigoDoConvenio(), 6, "0");
        String parametroTransmissaoBanco = creditoSalario.getContaBancariaEntidade().getParametroTransmissao();
        String parametroTransmissao = (parametroTransmissaoBanco != null && !parametroTransmissaoBanco.isEmpty()) ? cortaOuCompletaEsquerda(parametroTransmissaoBanco, 2, "0") : "02";
        String ambienteCliente = getCreditoSalarioProducao() ? "P" : "T";
        String ambienteCaixa = " ";
        String origemAplicativo = "   ";
        String numeroVersao = "0000";
        String filler = "   ";
        e.setCodigoConvenio(codigoConvenio + parametroTransmissao + ambienteCliente + ambienteCaixa + origemAplicativo + numeroVersao + filler);
        e.setUsoExlcusivo3(cortaOuCompletaEsquerda("", 14, " ") + "000" + cortaOuCompletaEsquerda("", 12, " "));

        e.setNumeroConta(Strings.isNullOrEmpty(creditoSalario.getContaBancariaEntidade().getContaBancaria()) ?
            cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getNumeroConta(), 12, "0")
            : cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getContaBancaria(), 4, "0") +
            cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getNumeroConta(), 8, "0"));
    }

    private Boolean getCreditoSalarioProducao() {
        if (getConfiguracaoRH() == null) {
            return false;
        }
        return getConfiguracaoRH().getCreditoSalarioProducao();
    }

    private ConfiguracaoRH getConfiguracaoRH() {
        if (configuracaoRH == null) {
            configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigente(new Date());
        }
        return configuracaoRH;
    }

    private void preencherParticularidadeHeaderSicredi(HeaderCNAB240 e, CreditoSalario creditoSalario, BancoCreditoSalario banco) {
        e.setDigitoVerificador(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getDigitoVerificador(), 1, " "));
    }

    private void preencherParticularidadeHeaderItau(HeaderCNAB240 e, CreditoSalario creditoSalario, BancoCreditoSalario banco) {
        e.setUsoExlcusivo(StringUtil.cortaOuCompletaEsquerda("081", 9, " "));
        e.setCodigoConvenio(cortaOuCompletaEsquerda("", 20, " "));
        e.setDigitoAgenciaMantenedora(cortaOuCompletaEsquerda("", 1, " "));
        e.setDigitoVerificador(cortaOuCompletaEsquerda("", 1, " "));
        e.setDigitoVerificadorAgenciaConta(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getDigitoVerificador(), 1, " "));
        e.setNomeBanco(cortaOuCompletaDireita(StringUtil.removeAcentuacao(creditoSalario.getContaBancariaEntidade().getAgencia().getBanco().getDescricao()), 30, ""));
        e.setSequencial(cortaOuCompletaEsquerda("", 6, "0"));
        e.setVersaoLayout(cortaOuCompletaEsquerda("", 3, "0"));
    }

    private TrailerLoteCNAB240 montarTrailerLoteCNAB240(CreditoSalario creditoSalario, BancoCreditoSalario banco, DependenciasCreditoSalario dependenciasCreditoSalario, LoteCreditoSalario lote) {
        TrailerLoteCNAB240 trailerLote = banco.getNewInstanceTrailerLote();
        trailerLote.setBanco(banco.getCodigo());
        trailerLote.setLote(cortaOuCompletaEsquerda(lote.getPosicao() + "", 4, "0"));
        trailerLote.setRegistro("5");
        trailerLote.setUsoExclusivo(cortaOuCompletaEsquerda(" ", 9, " "));
        trailerLote.setQuantidadeLote(cortaOuCompletaEsquerda((lote.getContadorLinhaLote() + 1) + "", 6, "0"));
        trailerLote.setSomatoriaValores(cortaOuCompletaEsquerda(getValorFormatado(lote.getValorLote()), 18, "0"));
        trailerLote.setSomatoriaQuantidade(cortaOuCompletaEsquerda("0", 18, "0"));
        trailerLote.setNumeroAvisoDebito(cortaOuCompletaEsquerda("0", 6, "0"));
        trailerLote.setUsoExclusivo2(cortaOuCompletaEsquerda(" ", 165, " "));
        trailerLote.setOcorrencias(cortaOuCompletaEsquerda(" ", 10, " "));
        gerarLogDetalhado(trailerLote, dependenciasCreditoSalario);
        dependenciasCreditoSalario.incrementarContadorLinha();
        return trailerLote;
    }

    private String getValorFormatado(BigDecimal valor) {
        return StringUtil.retornaApenasNumeros(new DecimalFormat("#,###,##0.00").format(valor != null ? valor : BigDecimal.ZERO));
    }

    private SegmentoACNAB240 montarSegmentoA(CreditoSalario creditoSalario, BancoCreditoSalario banco, VinculoCreditoSalario vinculo, HeaderLoteCNAB240 lote, DependenciasCreditoSalario dependenciasCreditoSalario, LoteCreditoSalario loteCreditoSalario, VinculoFPCreditoSalario itemVinculo) throws CreditoSalarioException {
        SegmentoACNAB240 segmentoA = banco.getNewInstanceSegamentoA();

        validarVinculoCpfAndValores(creditoSalario, vinculo, loteCreditoSalario, dependenciasCreditoSalario);


        segmentoA.setBanco(banco.getCodigo());
        segmentoA.setLote(lote.getLote()); //Sequencial do lote
        segmentoA.setRegistro("3");
        segmentoA.setNumeroRegistro(cortaOuCompletaEsquerda(loteCreditoSalario.getContadorLinhaLote() + "", 5, "0")); //sequencial do registro dentro do lote
        segmentoA.setSegmento("A");
        segmentoA.setTipoMovimento("0");
        segmentoA.setCodigoMovimento("00");
        segmentoA.setCamara(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getCamaraCompensacao() != null ? creditoSalario.getContaBancariaEntidade().getCamaraCompensacao() : "0", 3, "0"));
        segmentoA.setBancoFavorecido(cortaOuCompletaEsquerda(vinculo.getConta().getBanco().getNumeroBanco(), 3, "0"));
        segmentoA.setCodigoAgenciaFavorecido(cortaOuCompletaEsquerda(vinculo.getConta().getAgencia().getNumeroAgencia(), 5, "0"));
        segmentoA.setDigitoAgenciaFavorecido(cortaOuCompletaEsquerda(vinculo.getConta().getAgencia().getDigitoVerificador(), 1, "X"));
        segmentoA.setNumeroContaFavorecido(cortaOuCompletaEsquerda(vinculo.getConta().getNumeroContaSemCaracteres(), 12, "0"));
        segmentoA.setDigitoContaFavorecido(cortaOuCompletaEsquerda(vinculo.getConta().getDigitoVerificador(), 1, "X"));
        segmentoA.setDigitoContaCorrente(cortaOuCompletaEsquerda(" ", 1, " "));
        segmentoA.setNomeFavorecido(cortaOuCompletaDireita(StringUtil.removeCaracteresEspeciaisSemEspaco(vinculo.getPessoa().getNome()), 30, " "));
        segmentoA.setNumeroCredito(cortaOuCompletaEsquerda(itemVinculo.getIdentificador(), 20, " "));
        segmentoA.setDataPagamento(DataUtil.getDataFormatada(creditoSalario.getDataCredito(), "ddMMyyyy"));
        segmentoA.setTipoMoeda("BRL");
        segmentoA.setQuantidadeMoeda(cortaOuCompletaEsquerda("0", 15, "0"));
        segmentoA.setValorPagamento(cortaOuCompletaEsquerda(getValorFormatado(vinculo.getValor()), 15, "0"));
        segmentoA.setNossoNumeroCredito(cortaOuCompletaEsquerda(" ", 20, " "));
        segmentoA.setDataRealCredito(DataUtil.getDataFormatada(creditoSalario.getDataCredito(), "ddMMyyyy"));
        segmentoA.setValorRealCredito(cortaOuCompletaEsquerda(getValorFormatado(vinculo.getValor()), 15, "0"));
        segmentoA.setInformacao(cortaOuCompletaDireita(" ", 40, " "));
        segmentoA.setCodigoFinalidadeDoc(cortaOuCompletaEsquerda(" ", 2, " "));
        segmentoA.setCodigoFinalidadeTED(cortaOuCompletaEsquerda(" ", 5, " "));
        segmentoA.setCodigoFinalidadeComplementar(cortaOuCompletaEsquerda(" ", 2, " "));
        segmentoA.setUsoExclusivo(cortaOuCompletaEsquerda(" ", 3, " "));
        segmentoA.setAviso("0");
        segmentoA.setOcorrencias(cortaOuCompletaEsquerda(" ", 10, " "));
        preencherParticularidadesSegmentoA(segmentoA, vinculo, banco, creditoSalario, itemVinculo);
        loteCreditoSalario.acrescentarValorNoLote(vinculo.getValor());
        gerarLogDetalhado(segmentoA, dependenciasCreditoSalario);
        dependenciasCreditoSalario.incrementarContadorLinha();
        loteCreditoSalario.incrementarContadorLinhaLote();
        return segmentoA;
    }

    private void validarVinculoCpfAndValores(CreditoSalario selecionado, VinculoCreditoSalario vinculo, LoteCreditoSalario loteCreditoSalario, DependenciasCreditoSalario dependenciasCreditoSalario) throws CreditoSalarioException {
        if (vinculo.getIdVinculo() == null) {
            adicionarLogCreditoSalario(selecionado, vinculo, loteCreditoSalario, "Vinculo: " + vinculo.getPessoa().getNome() + "-" + vinculo.getMatricula() + " não possui id no banco");
            throw new CreditoSalarioException("Vinculo: " + vinculo.getPessoa().getNome() + "-" + vinculo.getMatricula() + " não possui id no banco");
        }
        if (vinculo.getConta() == null) {
            adicionarLogCreditoSalario(selecionado, vinculo, loteCreditoSalario, "Vinculo: " + vinculo.getPessoa().getNome() + "-" + vinculo.getMatricula() + " não possui conta associada.");
            throw new CreditoSalarioException("Vinculo: " + vinculo.getPessoa().getNome() + "-" + vinculo.getMatricula() + " não possui conta associada.");
        }
        if (vinculo.getConta() != null && Long.parseLong(vinculo.getConta().getNumeroContaSemCaracteres()) == 0) {
            adicionarLogCreditoSalario(selecionado, vinculo, loteCreditoSalario, "Vinculo: " + vinculo.getPessoa().getNome() + "-" + vinculo.getMatricula() + " não possui número da conta informado.");
            throw new CreditoSalarioException("Vinculo: " + vinculo.getPessoa().getNome() + "-" + vinculo.getMatricula() + " não possui número da conta informado.");
        }
        PessoaFisica pessoa = vinculo.getVinculoFP().getMatriculaFP().getPessoa();
        if (pessoa != null && pessoa.getCpf() == null) {
            adicionarLogCreditoSalario(selecionado, vinculo, loteCreditoSalario, "Servidor sem número de CPF. Vinculo:" + vinculo.getVinculoFP());
            throw new CreditoSalarioException("CPF inválido, verifique os detalhes no log da geração do arquivo. Vinculo:" + vinculo.getVinculoFP());
        }

        if (vinculo.getValor() == null) {
            adicionarLogCreditoSalario(selecionado, vinculo, loteCreditoSalario, "Valor do saldo a creditar vazio. Por favor, verifique a ficha financeira. Vinculo: " + vinculo.getVinculoFP());
            throw new CreditoSalarioException("Valor inválido, verifique os detalhes no log da geração do arquivo");
        }
        if (vinculo.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            adicionarLogCreditoSalario(selecionado, vinculo, loteCreditoSalario, "Valor do saldo a creditar zerado. Por favor, verifique a ficha financeira. Vinculo: " + vinculo.getVinculoFP());
            throw new CreditoSalarioException("Saldo a creditar zerado, verifique os detalhes no log da geração do arquivo. Vinculo: " + vinculo.getVinculoFP());
        }
        if (TipoGeracaoCreditoSalario.SERVIDORES.equals(selecionado.getTipoGeracaoCreditoSalario()) && !isMesmoBanco(selecionado, vinculo)) {
            adicionarLogCreditoSalario(selecionado, vinculo, loteCreditoSalario, "OBSERVAÇAO: Vínculo " + vinculo.getVinculoFP() + " está diferente do banco de geração do crédito de salário. Banco do vínculo: " + vinculo.getConta().getBanco() + ". Ele foi incluído no arquivo em um lote marcado como DOC/TED.");
            dependenciasCreditoSalario.getLogIncosistencia().add("OBSERVAÇAO: Vínculo " + vinculo.getVinculoFP() + " está diferente do banco de geração do crédito de salário. Banco do vínculo: " + vinculo.getConta().getBanco() + ". Ele foi incluído no arquivo em um lote marcado como DOC/TED.");
        }
    }

    private void preencherParticularidadesSegmentoA(SegmentoACNAB240 e, VinculoCreditoSalario vinculo, BancoCreditoSalario banco, CreditoSalario creditoSalario, VinculoFPCreditoSalario itemVinculo) {
        switch (banco) {
            case CAIXA:
                preencherParticularidadesSegmentoACaixa(e, vinculo, creditoSalario);
                break;
            case SICREDI:
                preencherParticularidadesSegmentoASicredi(e, vinculo, creditoSalario);
                break;
            case ITAU:
                preencherParticularidadesSegmentoAItau(e, vinculo, creditoSalario);
                break;
            case BRADESCO:
                preencherParticularidadesSegmentoABradesco(e, vinculo, itemVinculo);
                break;
            default:
                break;
        }
    }


    private void preencherParticularidadesSegmentoACaixa(SegmentoACNAB240 segmentoA, VinculoCreditoSalario vinculo, CreditoSalario creditoSalario) {
        if (ModalidadeConta.CONTA_SALARIO.equals(vinculo.getConta().getModalidadeConta())) {
            segmentoA.setNumeroContaFavorecido(cortaOuCompletaEsquerda(Strings.isNullOrEmpty(vinculo.getConta().getContaBancaria()) ? vinculo.getConta().getModalidadeConta().getCodigoCaixa()
                : vinculo.getConta().getContaBancaria(), 4, "0") + cortaOuCompletaEsquerda(vinculo.getConta().getNumeroContaSemCaracteres(), 8, "0"));
        }
        String numeroDocumentoAtribEmpresa = cortaOuCompletaEsquerda(vinculo.getVinculoFP().getId() + "", 6, "0");
        String filler = cortaOuCompletaEsquerda("", 13, " ");
        String tipoConta = vinculo.getConta().getModalidadeConta().getCodigoCaixaTED();
        segmentoA.setNumeroCredito(numeroDocumentoAtribEmpresa + filler + tipoConta);
        String numeroDocumentoAtribBanco = cortaOuCompletaEsquerda("", 9, "0");
        filler = cortaOuCompletaEsquerda("", 3, " ");
        String quantidadeParcela = "01";
        String indicadorBloqueio = "N";
        String indicadorFormaPag = "1";
        String diaVencimento = cortaOuCompletaEsquerda(String.valueOf(DataUtil.getDia(creditoSalario.getDataCredito())), 2, "0");
        String numeroParcela = "00";
        segmentoA.setNossoNumeroCredito(numeroDocumentoAtribBanco + filler + quantidadeParcela + indicadorBloqueio + indicadorFormaPag + diaVencimento + numeroParcela);
        segmentoA.setDataRealCredito(cortaOuCompletaEsquerda("", 8, "0"));
        segmentoA.setValorRealCredito(cortaOuCompletaEsquerda("", 15, "0"));
        segmentoA.setCodigoFinalidadeDoc(cortaOuCompletaEsquerda("", 2, "0"));
    }

    private void preencherParticularidadesSegmentoASicredi(SegmentoACNAB240 segmentoA, VinculoCreditoSalario vinculo, CreditoSalario creditoSalario) {
        segmentoA.setDigitoAgenciaFavorecido(cortaOuCompletaEsquerda(vinculo.getConta().getAgencia().getDigitoVerificador(), 1, " "));
        segmentoA.setDigitoContaFavorecido(cortaOuCompletaEsquerda(vinculo.getConta().getDigitoVerificador(), 1, " "));
    }

    private void preencherParticularidadesSegmentoAItau(SegmentoACNAB240 segmentoA, VinculoCreditoSalario vinculo, CreditoSalario creditoSalario) {
        String codigoISPB = cortaOuCompletaEsquerda("", 8, "0");
        segmentoA.setQuantidadeMoeda(codigoISPB + "0000000");

        String nossoNumero = cortaOuCompletaEsquerda("", 15, " ");
        segmentoA.setNossoNumeroCredito(nossoNumero + "     ");

        String conteudo = cortaOuCompletaEsquerda("", 18, " ");
        conteudo += cortaOuCompletaEsquerda("", 2, " ");
        conteudo += cortaOuCompletaEsquerda("", 6, "0");
        conteudo += cortaOuCompletaEsquerda("", 14, "0");
        segmentoA.setInformacao(conteudo);

        conteudo = cortaOuCompletaEsquerda("", 5, "0");
        segmentoA.setCodigoFinalidadeComplementar(conteudo.substring(0, 2));
        segmentoA.setUsoExclusivo(conteudo.substring(2, 5));

        //Itau inverte a posição do digito da conta.
        segmentoA.setDigitoContaFavorecido(cortaOuCompletaEsquerda("", 1, " "));
        segmentoA.setDigitoContaCorrente(cortaOuCompletaEsquerda(vinculo.getConta().getDigitoVerificador(), 1, "X"));
    }

    private void preencherParticularidadesSegmentoABradesco(SegmentoACNAB240 segmentoA, VinculoCreditoSalario vinculo, VinculoFPCreditoSalario itemVinculo) {
        segmentoA.setBancoFavorecido(cortaOuCompletaEsquerda(vinculo.getConta().getBanco().getNumeroBanco(), 3, "0"));
        segmentoA.setNumeroCredito(cortaOuCompletaEsquerda(itemVinculo.getIdentificador() + "", 20, " "));
    }

    private SegmentoBCNAB240 montarSegmentoB(CreditoSalario creditoSalario, BancoCreditoSalario banco, VinculoCreditoSalario vinculo, HeaderLoteCNAB240 lote, DependenciasCreditoSalario dependenciasCreditoSalario, LoteCreditoSalario loteCreditoSalario) {
        SegmentoBCNAB240 segmentoB = banco.getNewInstanceSegamentoB();
        Entidade entidade = creditoSalario.getContaBancariaEntidade().getEntidade();
        PessoaFisica pessoa = vinculo.getPessoa();
        EnderecoCorreio endereco = pessoa.getEnderecoPorPrioridade();

        segmentoB.setBanco(banco.getCodigo());
        segmentoB.setLote(lote.getLote());
        segmentoB.setRegistro("3");
        segmentoB.setNumeroRegistro(cortaOuCompletaEsquerda(loteCreditoSalario.getContadorLinhaLote() + "", 5, "0"));
        segmentoB.setSegmento("B");
        segmentoB.setUsoExclusivo(cortaOuCompletaEsquerda(" ", 3, " "));
        segmentoB.setTipoInscricao("1");

        segmentoB.setNumeroInscricao(cortaOuCompletaEsquerda(pessoa.getCpfSemFormatacao(), 14, "0"));
        segmentoB.setLogradourro(cortaOuCompletaDireita(StringUtil.removeCaracteresEspeciaisSemEspaco(endereco != null ? endereco.getLogradouro() : ""), 30, " "));
        segmentoB.setNumeroFavorecido(cortaOuCompletaEsquerda(StringUtil.removeCaracteresEspeciaisSemEspaco(endereco != null ? endereco.getNumero() : ""), 5, "0"));
        segmentoB.setComplemento(cortaOuCompletaDireita(StringUtil.removeCaracteresEspeciaisSemEspaco(endereco != null ? endereco.getComplemento() : ""), 15, " "));
        segmentoB.setBairro(cortaOuCompletaDireita(StringUtil.removeCaracteresEspeciaisSemEspaco(endereco != null ? endereco.getBairro() : ""), 15, " "));
        segmentoB.setCidade(cortaOuCompletaDireita(StringUtil.removeCaracteresEspeciaisSemEspaco(endereco != null ? endereco.getLocalidade() : ""), 20, " "));
        segmentoB.setCep(cortaOuCompletaDireita(endereco != null ? endereco.getCepSemFormatacao() : "", 5, " "));
        segmentoB.setComplementoCep(cortaOuCompletaEsquerda(endereco != null ? endereco.getCepSemFormatacao() : "", 3, " "));
        segmentoB.setSiglaEstado(cortaOuCompletaDireita(endereco != null ? endereco.getUf() : "", 2, " "));
        segmentoB.setDataVencimento(DataUtil.getDataFormatada(creditoSalario.getDataCredito(), "ddMMyyyy"));
        segmentoB.setValorDocumento(cortaOuCompletaEsquerda("0", 15, "0"));
        segmentoB.setValorAbatimento(cortaOuCompletaEsquerda("0", 15, "0"));
        segmentoB.setValorDesconto(cortaOuCompletaEsquerda("0", 15, "0"));
        segmentoB.setValorMora(cortaOuCompletaEsquerda("0", 15, "0"));
        segmentoB.setValorMulta(cortaOuCompletaEsquerda("0", 15, "0"));
        segmentoB.setCodigoDocumentoFavorecido(cortaOuCompletaEsquerda(vinculo.getIdVinculo() + "", 15, " "));
        segmentoB.setAvisoFavorecido("0");
        segmentoB.setCodigoUnidadeCentralizado(cortaOuCompletaEsquerda("0", 6, "0"));
        segmentoB.setCodigoISPB(cortaOuCompletaEsquerda("0", 8, "0"));

        preencherParticularidadesSegmentoB(segmentoB, vinculo, lote, banco);
        gerarLogDetalhado(segmentoB, dependenciasCreditoSalario);
        dependenciasCreditoSalario.incrementarContadorLinha();
        loteCreditoSalario.incrementarContadorLinhaLote();
        return segmentoB;
    }

    private void preencherParticularidadesSegmentoB(SegmentoBCNAB240 e, VinculoCreditoSalario vinculo, HeaderLoteCNAB240 lote, BancoCreditoSalario banco) {
        switch (banco) {
            case CAIXA:
                preencherParticularidadesSegmentoBCaixa(e, vinculo, lote);
                break;
            case ITAU:
                preencherParticularidadesSegmentoBItau(e, vinculo, lote);
                break;
            case BRADESCO:
                preencherParticularidadesSegmentoBBradesco(e, vinculo);
                break;
            default:
                break;
        }
    }

    private void preencherParticularidadesSegmentoBCaixa(SegmentoBCNAB240 segmentoB, VinculoCreditoSalario vinculo, HeaderLoteCNAB240 lote) {
        if (!lote.getLancamento().equals("10")) {
            segmentoB.setCodigoDocumentoFavorecido(cortaOuCompletaEsquerda("", 15, " "));
        }
        segmentoB.setAvisoFavorecido(" ");
        segmentoB.setCodigoUnidadeCentralizado(cortaOuCompletaEsquerda("", 6, " "));
        segmentoB.setCodigoISPB(cortaOuCompletaEsquerda("", 8, " "));
    }

    private void preencherParticularidadesSegmentoBItau(SegmentoBCNAB240 segmentoB, VinculoCreditoSalario vinculo, HeaderLoteCNAB240 lote) {
        String conteudo = cortaOuCompletaEsquerda(vinculo.getPessoa().getEmail() == null ? "" : vinculo.getPessoa().getEmail(), 113, " ");
        segmentoB.setDataVencimento(conteudo.substring(0, 8));
        segmentoB.setValorDocumento(conteudo.substring(8, 23));
        segmentoB.setValorAbatimento(conteudo.substring(23, 38));
        segmentoB.setValorDesconto(conteudo.substring(38, 53));
        segmentoB.setValorMora(conteudo.substring(53, 68));
        segmentoB.setValorMulta(conteudo.substring(68, 83));
        segmentoB.setCodigoDocumentoFavorecido(conteudo.substring(83, 98));
        segmentoB.setAvisoFavorecido(conteudo.substring(98, 99));
        segmentoB.setCodigoUnidadeCentralizado(conteudo.substring(99, 105));
        segmentoB.setCodigoISPB(conteudo.substring(105, 113));
    }

    private void preencherParticularidadesSegmentoBBradesco(SegmentoBCNAB240 segmentoB, VinculoCreditoSalario vinculo) {
        PessoaFisica pessoa = vinculo.getVinculoFP().getMatriculaFP().getPessoa();
        EnderecoCorreio endereco = enderecoCorreioFacade.buscarUltimoEnderecoDaPessoa(pessoa.getId(), true);

        if (endereco == null) {
            endereco = enderecoCorreioFacade.buscarUltimoEnderecoDaPessoa(pessoa.getId(), false);
        }

        String cep = endereco != null ? endereco.getCepSemFormatacao() : "";
        segmentoB.setCep(cortaOuCompletaDireita(cep, 5, "0"));
        segmentoB.setComplementoCep(cortaOuCompletaEsquerda(cep, 3, "0"));
    }

    private CNAB240 montarTrailerCNAB240(CreditoSalario creditoSalario, BancoCreditoSalario bancoCreditoSalario, DependenciasCreditoSalario dependenciasCreditoSalario, List<LoteCreditoSalario> lotes) {
        TrailerCNAB240 trailer = bancoCreditoSalario.getNewInstanceTrailer();
        trailer.setBanco(bancoCreditoSalario.getCodigo());
        trailer.setLote("9999");
        trailer.setRegistro("9");
        trailer.setUsoExlcusivo(cortaOuCompletaEsquerda(" ", 9, " "));
        trailer.setQuantidadeLotes(cortaOuCompletaEsquerda(lotes.size() + "", 6, "0"));
        trailer.setQuantidadeRegistros(cortaOuCompletaEsquerda(dependenciasCreditoSalario.getContadorLinha() + "", 6, "0")); //TODO verificar quantidade total de registros na linha
        trailer.setQuantidadeContas(cortaOuCompletaEsquerda("0", 6, "0"));
        trailer.setUsoExlcusivo2(cortaOuCompletaEsquerda(" ", 205, " "));
        gerarLogDetalhado(trailer, dependenciasCreditoSalario);
        dependenciasCreditoSalario.incrementarContadorLinha();
        return trailer;
    }

    public HeaderLoteCNAB240 montarHeaderLoteCNAB240(CreditoSalario creditoSalario, BancoCreditoSalario banco, DependenciasCreditoSalario assistente, LoteCreditoSalario loteProcessando) {
        HeaderLoteCNAB240 lote = banco.getNewInstanceHeaderLote();
        Entidade entidade = creditoSalario.getContaBancariaEntidade().getEntidade();
        lote.setBanco(banco.getCodigo());
        lote.setLote(cortaOuCompletaEsquerda(loteProcessando.getPosicao() + "", 4, "0"));
        lote.setRegistro("1");
        lote.setOperacao("C");
        lote.setServico(creditoSalario.getTipoGeracaoCreditoSalario().getTipoServico()); //30 - Pagamentos de salario para "Servidores" e 98 - Outros para Pensionistas
        lote.setLancamento(loteProcessando.getFormaLancamento()); //TODO verificar se mesmo banco ou se é DOC/TED -> '03', Credito em Conta -> '01'
        lote.setVersaoLayout("046"); //Versão do layout
        lote.setUsoExclusivo(" ");
        lote.setTipoInscricao("2"); //CNPJ
        lote.setNumeroEmpresa(entidade.getPessoaJuridica().getCnpjSemFormatacao());
        lote.setCodigoBanco(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getCodigoDoConvenio(), 20, "0"));
        lote.setAgenciaConta(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getAgencia().getNumeroAgencia(), 5, "0"));
        lote.setDigitoAgencia(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getAgencia().getDigitoVerificador(), 1, "X"));
        lote.setNumeroConta(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getNumeroConta(), 12, "0"));
        lote.setDigitoConta(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getDigitoVerificador(), 1, "0"));
        lote.setDigitoAgConta(cortaOuCompletaEsquerda(" ", 1, " "));
        lote.setNomeEmpresa(cortaOuCompletaDireita(StringUtil.removeCaracteresEspeciaisSemEspaco(entidade.getNome()), 30, " "));
        lote.setMensagem(cortaOuCompletaDireita("", 40, " "));
        PessoaJuridica pessoaJuridica = recuperarPessoaJuridica(entidade.getPessoaJuridica());
        EnderecoCorreio endereco = pessoaJuridica.getEnderecoPrincipal();
        lote.setLogradoudo(cortaOuCompletaDireita(endereco != null ? endereco.getLogradouro() : " ", 30, " "));
        lote.setNumeroLocal(cortaOuCompletaEsquerda(endereco != null && endereco.getNumero() != null && !endereco.getNumero().isEmpty() ? endereco.getNumero() : "0", 5, "0"));
        lote.setComplemento(cortaOuCompletaEsquerda(endereco != null ? endereco.getComplemento() : " ", 15, " "));
        lote.setCidade(cortaOuCompletaDireita(endereco != null && endereco.getLocalidade() != null ? endereco.getLocalidade() : " ", 20, " "));
        lote.setCep(cortaOuCompletaDireita(endereco != null ? StringUtil.removeCaracteresEspeciaisSemEspaco(endereco.getCep()) : "0", 5, "0"));
        lote.setComplementoCep(cortaOuCompletaEsquerda("0", 3, "0"));
        lote.setEstado(cortaOuCompletaEsquerda(endereco != null ? endereco.getUf() : " ", 2, " "));
        lote.setFormaPagamento(cortaOuCompletaEsquerda(" ", 2, " "));
        lote.setUsoExclusivo2(cortaOuCompletaDireita(" ", 6, " "));
        lote.setCodigoOcorrencia(cortaOuCompletaEsquerda(" ", 10, " "));
        preencherParticularidadesHeaderLote(lote, creditoSalario, banco);
        gerarLogDetalhado(lote, assistente);
        assistente.incrementarContadorLinha();
        return lote;
    }

    public PessoaJuridica recuperarPessoaJuridica(Pessoa pessoa) {
        return pessoaJuridicaFacade.recuperar(pessoa.getId());
    }

    private void preencherParticularidadesHeaderLote(HeaderLoteCNAB240 e, CreditoSalario creditoSalario, BancoCreditoSalario banco) {
        switch (banco) {
            case BANCO_BRASIL:
                preencherParticularidadeHeaderLoteBancoBrasil((BancoDoBrasilHeaderLoteCNAB240) e, creditoSalario, banco);
                break;
            case CAIXA:
                preencherParticularidadeHeaderLoteCaixa(e, creditoSalario, banco);
                break;
            case SICREDI:
                preencherParticularidadeHeaderLoteSicredi(e, creditoSalario, banco);
                break;
            case ITAU:
                preencherParticularidadeHeaderLoteItau(e, creditoSalario, banco);
                break;
            case BRADESCO:
                preencherParticularidadeHeaderLoteBradesco(e, creditoSalario);
                break;
            default:
                break;
        }
    }

    private void preencherParticularidadeHeaderLoteBancoBrasil(BancoDoBrasilHeaderLoteCNAB240 header, CreditoSalario creditoSalario, BancoCreditoSalario banco) {
        StringBuilder codigoBanco = new StringBuilder();
        header.setVersaoLayout("046");
        codigoBanco.append(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getCodigoDoConvenio(), 9, "0"));
        codigoBanco.append(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getCarteiraCedente(), 4, "0"));
        codigoBanco.append(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getNumeroCarteiraCobranca(), 2, "0"));
        codigoBanco.append(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getVariacaoCarteiraCobranca(), 3, "0"));
        codigoBanco.append(cortaOuCompletaEsquerda(getCreditoSalarioProducao() ? "" : "TS", 2, " "));
        header.setCodigoBanco(codigoBanco.toString());
    }

    private void preencherParticularidadeHeaderLoteCaixa(HeaderLoteCNAB240 lote, CreditoSalario creditoSalario, BancoCreditoSalario banco) {
        String codigoBanco = cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getCodigoDoConvenio(), 6, "0");
        String tipoCompromisso = (Strings.isNullOrEmpty(creditoSalario.getContaBancariaEntidade().getTipoDeCompromisso()) ? "02" :
            cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getTipoDeCompromisso(), 2, "0"));
        String codigoCompromisso = (Strings.isNullOrEmpty(creditoSalario.getContaBancariaEntidade().getCodigoDoCompromisso()) ? "0001" :
            cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getCodigoDoCompromisso(), 4, "0"));
        String parametroTransmissaoBanco = creditoSalario.getContaBancariaEntidade().getParametroTransmissao();
        String parametroCompromisso = (parametroTransmissaoBanco != null && !parametroTransmissaoBanco.isEmpty()) ? cortaOuCompletaEsquerda(parametroTransmissaoBanco, 2, "0") : "02";

        String filler = cortaOuCompletaEsquerda("", 6, " ");
        lote.setCodigoBanco(codigoBanco + tipoCompromisso + codigoCompromisso + parametroCompromisso + filler);
        lote.setFormaPagamento(cortaOuCompletaEsquerda("", 2, " "));

        lote.setNumeroConta(Strings.isNullOrEmpty(creditoSalario.getContaBancariaEntidade().getContaBancaria()) ?
            cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getNumeroConta(), 12, "0")
            : cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getContaBancaria(), 4, "0") +
            cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getNumeroConta(), 8, "0"));
    }

    private void preencherParticularidadeHeaderLoteSicredi(HeaderLoteCNAB240 lote, CreditoSalario creditoSalario, BancoCreditoSalario banco) {
        lote.setVersaoLayout("042");
        lote.setDigitoAgencia(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getAgencia().getDigitoVerificador(), 1, " "));
    }

    private void preencherParticularidadeHeaderLoteItau(HeaderLoteCNAB240 lote, CreditoSalario creditoSalario, BancoCreditoSalario banco) {
        lote.setMensagem(cortaOuCompletaDireita(atribuirTipoDeFolha(creditoSalario), 40, " "));
        lote.setVersaoLayout("040");
        lote.setCodigoBanco(cortaOuCompletaEsquerda("", 20, " "));
        lote.setDigitoAgencia(cortaOuCompletaEsquerda("", 1, " "));
        lote.setDigitoAgConta(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getDigitoVerificador(), 1, " "));
        lote.setDigitoConta(cortaOuCompletaEsquerda("", 1, " "));
    }


    private String atribuirTipoDeFolha(CreditoSalario creditoSalario) {
        switch (creditoSalario.getFolhaDePagamento().getTipoFolhaDePagamento()) {
            case NORMAL:
                return "01";
            case COMPLEMENTAR:
                return "03";
            case SALARIO_13:
                return "04";
            case FERIAS:
                return "07";
            case RESCISAO:
                return "08";
            case RESCISAO_COMPLEMENTAR:
                return "09";
            default:
                return "10";
        }
    }

    private void preencherParticularidadeHeaderLoteBradesco(HeaderLoteCNAB240 lote, CreditoSalario creditoSalario) {
        lote.setVersaoLayout(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getVersaoLayoutLoteCreditoSalario(), 3, "0"));
        lote.setCodigoBanco(cortaOuCompletaDireita(creditoSalario.getContaBancariaEntidade().getCodigoDoConvenio(), 20, " "));
        lote.setFormaPagamento(cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getIndicativoFormaPagamentoServ(), 2, "0"));
    }

    public void gerarLogDetalhado(CNAB240 e, DependenciasCreditoSalario assistente) {
        if (!assistente.getGerarLogDetalhado()) {
            return;
        }
        List<Field> fieldsUpTo = Util.getFieldsUpTo(e.getClass(), Object.class);
        Collections.sort(fieldsUpTo, new Comparator<Field>() {
            @Override
            public int compare(Field o1, Field o2) {
                if (o1 != null && o2 != null && o1.isAnnotationPresent(Campo.class) && o2.isAnnotationPresent(Campo.class)) {
                    Campo c1 = o1.getAnnotation(Campo.class);
                    Campo c2 = o2.getAnnotation(Campo.class);
                    return c1.posicao() - c2.posicao();
                }
                return 1;
            }
        });
        int totalPosicoes = 0;
        StringBuilder linha = new StringBuilder();
        for (Field field : fieldsUpTo) {

            if (field.isAnnotationPresent(Campo.class)) {
                field.setAccessible(true);
                try {
                    StringBuilder sb = new StringBuilder();
                    Object o = field.get(e);
                    if (o == null) {
                        logger.error("Campo Null");
                    }
                    int tamanhoConteudo = o != null ? o.toString().length() : 0;

                    totalPosicoes += tamanhoConteudo;
                    Campo campo = field.getAnnotation(Campo.class);
                    int tamanhoEsperado = campo.tamanho();
                    sb.append(e.getClass().getSimpleName())
                        .append(" Posição -> [")
                        .append(campo.posicao())
                        .append("] campo [")
                        .append(field.getName())
                        .append("] com conteúdo [")
                        .append(o).append("] com ")
                        .append(o != null ? o.toString().length() : "0")
                        .append(" posições");
                    if (tamanhoEsperado != tamanhoConteudo) {
                        sb.append(" +++ ATENÇÃO esperava [").append(tamanhoEsperado).append("] posições");
                        assistente.getLogIncosistencia().add(sb.toString());
                    }
                    sb.append("\n");
                    assistente.getLogDetalhado().append(sb);
                    linha.append(o != null ? o.toString() : "");
                } catch (IllegalAccessException e1) {
                    logger.error("erro: ", e1);
                }
            }
        }
        assistente.getLogDetalhado().append("Total de posições na linha [").append(totalPosicoes).append("] \n");
        if (totalPosicoes != 240) {
            assistente.getLogIncosistencia().add(linha.toString() + " não possui 240 caracteres");
        }
        assistente.getLogDetalhado().append("========================================= \n");
    }

    private ItemRelatorioConferenciaCreditoSalario montarItemRelatorioConferenciaCreditoSalario(VinculoCreditoSalario vinculo) {
        ItemRelatorioConferenciaCreditoSalario itemRelatorio = new ItemRelatorioConferenciaCreditoSalario();
        itemRelatorio.setValor(vinculo.getValor());
        itemRelatorio.setNumeroBanco(vinculo.getConta() != null ? vinculo.getConta().getAgencia().getBanco().getNumeroBanco() : "");
        itemRelatorio.setDigitoAgencia(vinculo.getConta() != null && vinculo.getConta().getAgencia().getDigitoVerificador() != null ? vinculo.getConta().getAgencia().getDigitoVerificador() : "");
        itemRelatorio.setNumeroAgencia(vinculo.getConta() != null ? vinculo.getConta().getAgencia().getNumeroAgencia() : "");
        itemRelatorio.setDigitoConta(vinculo.getConta() != null ? vinculo.getConta().getDigitoVerificador() : "");
        itemRelatorio.setNumeroConta(vinculo.getConta() != null ? vinculo.getConta().getNumeroContaSemCaracteres() : "");
        itemRelatorio.setMatriculaServidor(vinculo.getMatricula());
        itemRelatorio.setNumeroContrato(vinculo.getVinculoFP().getNumero());
        itemRelatorio.setCpfServidor(vinculo.getPessoa().getCpf());
        itemRelatorio.setNomeServidor(vinculo.getPessoa().getNome());
        return itemRelatorio;
    }

    private void gravarArquivo(List<CNAB240> cnabs, CreditoSalario creditoSalario, GrupoRecursoFP gruposRecursoFP, List<VinculoFPCreditoSalario> vinculosCreditoSalario, DependenciasCreditoSalario assistente) throws Exception {
        ItemCreditoSalario item = new ItemCreditoSalario();
        item.setCreditoSalario(creditoSalario);
        item.setSequencial(creditoSalario.getNumeroRemessa());
        item.setLogDetalhado(assistente.getLogDetalhado().toString());
        item.setErros(assistente.getTodosErros());
        item.setGrupoRecursoFP(gruposRecursoFP);
        assistente.getLogIncosistenciaGeral().addAll(assistente.getLogIncosistencia());
        assistente.reiniciarLogs();
        if(identificador!= null) {
            item.setIdentificador(identificador);
            identificador = null;
        }

        File exportar = exportar(cnabs, creditoSalario, gruposRecursoFP);
        ArquivoComposicao arquivoComposicao = criarArquivoComposicao(exportar, new DetentorArquivoComposicao());

        ByteArrayOutputStream relatorio = getRelatorioEmBytes(assistente, creditoSalario, null);
        ArquivoComposicao itensRelatorio = criarArquivoComposicaoRelatorio(relatorio, creditoSalario, item, gruposRecursoFP, new DetentorArquivoComposicao());

        DetentorArquivoComposicao arquivoCNAB = new DetentorArquivoComposicao();
        arquivoComposicao.setDetentorArquivoComposicao(arquivoCNAB);
        itensRelatorio.setDetentorArquivoComposicao(arquivoCNAB);

        arquivoCNAB.getArquivosComposicao().add(arquivoComposicao);
        arquivoCNAB.getArquivosComposicao().add(itensRelatorio);

        item.setArquivo(arquivoCNAB);
        for (VinculoFPCreditoSalario itemVinculoCreditoSalario : vinculosCreditoSalario) {
            itemVinculoCreditoSalario.setItemCreditoSalario(item);
            item.setValorLiquido((item.getValorLiquido() != null ? item.getValorLiquido() : BigDecimal.ZERO).add(itemVinculoCreditoSalario.getValorLiquido()));
        }
        item.setItensVinculoCreditoSalario(vinculosCreditoSalario);
        item = itemCreditoSalarioFacade.salvarMerge(item);
    }

    private void gravarArquivoRelatorioGeral(CreditoSalario creditoSalario, DependenciasCreditoSalario assistente, ParametrosRelatorioConferenciaCreditoSalario parametrosRelatorioGeral) throws Exception {
        ByteArrayOutputStream relatorio = getRelatorioEmBytes(assistente, creditoSalario, parametrosRelatorioGeral);
        Arquivo arquivo = gerarArquivoDoRelatorioGeral(relatorio, creditoSalario);
        creditoSalario.setArquivoRelatorio(arquivo);
    }

    public File exportar(List<CNAB240> bradescoHeaderCNAB240s, CreditoSalario creditoSalario, GrupoRecursoFP gruposRecursoFP) {
        try {
            FileCNAB240 arquivo = new FileCNAB240();
            List<? extends CNAB240> headers = bradescoHeaderCNAB240s;
            for (CNAB240 header : headers) {
                arquivo.addLinha(header);
            }
            ExportadorTamanhoFixo<FileCNAB240> exportador = new ExportadorTamanhoFixo<>();
            String nome = getNomeArquivo(creditoSalario, gruposRecursoFP);
            File file = new File(nome);
            exportador.exportar(arquivo, file);

            return file;
        } catch (Exception e) {
            logger.error("Erro: ", e);
            return null;
        }
    }

    public ArquivoComposicao criarArquivoComposicao(File file, DetentorArquivoComposicao detentorArquivoComposicao) throws Exception {
        Arquivo gerado = new Arquivo();
        gerado.setInputStream(new FileInputStream(file));
        gerado.setNome(file.getName());
        gerado.setTamanho(file.length());

        ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
        arquivoComposicao.setArquivo(arquivoFacade.novoArquivoMemoria(gerado, new FileInputStream(file)));
        arquivoComposicao.setDataUpload(new Date());
        arquivoComposicao.setFile(file);

        return arquivoComposicao;
    }

    private String getNomeArquivo(CreditoSalario selecionado, GrupoRecursoFP gruposRecursoFP) {
        if (selecionado != null && selecionado.getContaBancariaEntidade() != null) {
            String dataGeracao = new SimpleDateFormat("ddMMyyyy").format(new Date());
            String codigoConvenio = selecionado.getContaBancariaEntidade().getCodigoDoConvenio();
            String numeroRemessa = StringUtil.cortaOuCompletaEsquerda(selecionado.getNumeroRemessa() + "", 6, "0");
            String nomeArquivoGeral = selecionado.getBancoCreditoSalario().name() + dataGeracao + "." + codigoConvenio + "." + numeroRemessa + ".REM";
            String numeroRemessaItau = StringUtil.cortaOuCompletaEsquerda(selecionado.getNumeroRemessa() + "", 4, "0");
            String nomeArquivoItau = selecionado.getBancoCreditoSalario().name() + numeroRemessaItau + ".REM";
            nomeArquivoGeral = nomeArquivoGeral.replaceAll("/", "");
            if (BancoCreditoSalario.ITAU.name().equals(selecionado.getBancoCreditoSalario().name())) {
                return nomeArquivoItau;
            } else if (BancoCreditoSalario.BRADESCO.equals(selecionado.getBancoCreditoSalario())) {
                return montarNomeArquivoBradesco(selecionado, numeroRemessa);
            } else {
                return nomeArquivoGeral;
            }
        }
        return "";
    }

    private String montarNomeArquivoBradesco(CreditoSalario selecionado, String numeroRemessa) {
        String codigoBanco = selecionado.getBancoCreditoSalario().getCodigo();
        String dataGeracao = DataUtil.getDataFormatada(new Date(), "yyyyMM");
        String codigoTipoFolha = cortaOuCompletaEsquerda(selecionado.getFolhaDePagamento().getTipoFolhaDePagamento().getCodigo().toString(), 2, "0");

        return codigoBanco + "_" + dataGeracao + codigoTipoFolha + numeroRemessa + ".REM";
    }

    public ByteArrayOutputStream getRelatorioEmBytes(DependenciasCreditoSalario assistente, CreditoSalario selecionado, ParametrosRelatorioConferenciaCreditoSalario parametrosRelatorioGeral) throws JRException, IOException {
        HashMap parameter = new HashMap();
        AbstractReport abstractReport = AbstractReport.getAbstractReport();
        abstractReport.setGeraNoDialog(true);
        String jasper = "RelatorioConferenciaCreditoSalario.jasper";
        String nomeRelatorio = "Conferência do Arquivo Crédito Salário";
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(parametrosRelatorioGeral != null ? Lists.newArrayList(parametrosRelatorioGeral) : getRelatorio(selecionado));

        parameter.put("IMAGEM", assistente.getCaminhoImagem());
        parameter.put("MUNICIPIO", selecionado.getContaBancariaEntidade().getEntidade().getNome());
        parameter.put("MODULO", "Recursos Humanos");
        parameter.put("SECRETARIA", "Departamento de Recursos Humanos");
        parameter.put("NOMERELATORIO", nomeRelatorio);
        parameter.put("USUARIO", assistente.getUsuarioSistema().getNome());
        parameter.put("SUBREPORT_DIR", assistente.getCaminhoRelatorio());
        parameter.put("COMPETENCIA", "Competência: " + selecionado.getCompetenciaFP().toString());
        parameter.put("FOLHADEPAGAMENTO", selecionado.getFolhaDePagamento().toString());
        return abstractReport.gerarBytesEmPdfDeRelatorioComDadosEmCollectionViewSemCabecalhoPadrao(assistente.getCaminhoRelatorio(), jasper, parameter, dataSource);

    }

    private List<ParametrosRelatorioConferenciaCreditoSalario> getRelatorio(CreditoSalario selecionado) {
        return Lists.newArrayList(selecionado.getParametrosRelatorioConferenciaCreditoSalario());
    }

    public ArquivoComposicao criarArquivoComposicaoRelatorio(ByteArrayOutputStream bytes, CreditoSalario selecionado, ItemCreditoSalario itemCreditoSalario, GrupoRecursoFP gruposRecursoFP, DetentorArquivoComposicao detentorArquivoComposicao) throws Exception {
        Arquivo arquivo = gerarArquivoDoRelatorio(bytes, selecionado, itemCreditoSalario, gruposRecursoFP);

        ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
        arquivoComposicao.setArquivo(arquivo);
        arquivoComposicao.setDataUpload(new Date());
        return arquivoComposicao;
    }

    private Arquivo gerarArquivoDoRelatorio(ByteArrayOutputStream bytes, CreditoSalario selecionado, ItemCreditoSalario itemCreditoSalario, GrupoRecursoFP gruposRecursoFP) throws Exception {
        Arquivo arquivo = new Arquivo();
        arquivo.setDescricao("CONFERÊNCIA INDIVIDUAL DE CRÉDITO");
        arquivo.setNome("Relatório Conferência " + gruposRecursoFP.getNome().replaceAll("\\/", "_") + " " + selecionado.getNumeroRemessa() + ".pdf");
        arquivo.setMimeType("application/pdf");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes.toByteArray());
        arquivo.setTamanho(Long.valueOf(byteArrayInputStream.available()));
        return arquivoFacade.novoArquivoMemoria(arquivo, byteArrayInputStream);
    }

    private Arquivo gerarArquivoDoRelatorioGeral(ByteArrayOutputStream bytes, CreditoSalario selecionado) throws Exception {
        Arquivo arquivo = new Arquivo();
        arquivo.setDescricao("CONFERÊNCIA GERAL DE CRÉDITO");
        arquivo.setNome("Relatório Conferência de Crédito Salário " + selecionado.getNumeroRemessa() + ".pdf");
        arquivo.setMimeType("application/pdf");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes.toByteArray());
        arquivo.setTamanho(Long.valueOf(byteArrayInputStream.available()));
        return arquivoFacade.retornaArquivoSalvo(arquivo, byteArrayInputStream);
    }

    public void validarCreditoSalario(CreditoSalario creditoSalario) throws Exception {
        realizarValidacao(creditoSalario);
    }

    public void realizarValidacao(CreditoSalario selecionado) throws CreditoSalarioExistenteException {
        verificarArquivoExistente(selecionado);
    }

    public void verificarArquivoExistente(CreditoSalario creditoSalario) throws CreditoSalarioExistenteException {
        List<CreditoSalario> existentes = buscarCreditoSalarioExistente(creditoSalario);
        if (existentes != null && !existentes.isEmpty()) {
            String mensagem = "";
            for (CreditoSalario existente : existentes) {
                mensagem += " Na conta bancária: " + existente.getContaBancariaEntidade().getNumeroContaComDigitoVerificador() + " e folha: " + existente.getFolhaDePagamento() + ". ";
            }

            throw new CreditoSalarioExistenteException("Já existe(m) arquivo(s) gerado(s) " + mensagem);
        }
    }

    public ItemCreditoSalario buscarItemCreditoSalarioPorIdentificador(String identificador) {
        String sql = "select item.* from ITEMCREDITOSALARIO item " +
            " where item.IDENTIFICADOR = :identificador ";
        Query q = em.createNativeQuery(sql, ItemCreditoSalario.class);
        q.setParameter("identificador", identificador);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (ItemCreditoSalario) resultList.get(0);
        }
        return null;
    }

}
