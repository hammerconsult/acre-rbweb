/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.controlerelatorio.ImprimeAlvara;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.ws.model.WSAlvara;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JRException;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static br.com.webpublico.enums.TipoAlvara.LOCALIZACAO;

/**
 * @author leonardo
 */
@Stateless
public class AlvaraFacade extends AbstractFacade<Alvara> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private TipoVistoriaFacade tipoVistoriaFacade;
    @EJB
    private VistoriaFacade vistoriaFacade;
    @EJB
    private EnderecoFacade enderecoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private CalculoAlvaraFacade calculoAlvaraFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private NaturezaJuridicaFacade naturezaJuridicaFacade;
    @EJB
    private RodapeAlvaraFacade rodapeAlvaraFacade;
    @EJB
    private ProcessoSuspensaoCassacaoAlvaraFacade processoSuspensaoCassacaoAlvaraFacade;

    public AlvaraFacade() {
        super(Alvara.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public TipoVistoriaFacade getTipoVistoriaFacade() {
        return tipoVistoriaFacade;
    }

    public VistoriaFacade getVistoriaFacade() {
        return vistoriaFacade;
    }

    public EnderecoFacade getEnderecoFacade() {
        return enderecoFacade;
    }

    @Override
    public Alvara recuperar(Object id) {
        Alvara alvara = em.find(Alvara.class, id);
        alvara.getRecibosImpressaoAlvara().size();
        alvara.getCnaeAlvaras().size();
        alvara.getHorariosDeFuncionamento().size();
        return alvara;
    }

    public boolean verificaExistenciaAlvara(CadastroEconomico cadastroEconomico) {
        String hql = "select av from Alvara av where av.cadastroEconomico = :ce";
        Query q = em.createQuery(hql);
        q.setParameter("ce", cadastroEconomico);
        if (q.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public Alvara salvarRetornando(Alvara alvara) {
        return em.merge(alvara);
    }

    public List<Alvara> buscarAlvarasPorInscricaoCadastral(String inscricaoCadastral, boolean vigente) {
        String sql = "select av.* from Alvara av " +
            " inner join cadastroeconomico ce on ce.id = av.cadastroEconomico_id " +
            " inner join exercicio e on e.id = av.exercicio_id " +
            " left join processocalculoalvara proccalcalv on proccalcalv.alvara_id = av.id " +
            " left join calculoalvarafunc calfunc on calfunc.alvara_id = av.id " +
            " left join calculoalvaralocalizacao calloc on calloc.alvara_id = av.id " +
            " left join calculoalvarasanitario calsan on calsan.alvara_id = av.id " +
            " where ce.inscricaocadastral = :inscricaoCadastral" +
            " and (coalesce(calfunc.situacaocalculoalvara, calloc.situacaocalculoalvara, calsan.situacaocalculoalvara) = :calculado or " +
            "  proccalcalv.situacaocalculoalvara in (:situacaoProcesso)) ";
        if (vigente) {
            sql += " and (trunc(av.inicioVigencia) <= trunc(:hoje) " +
                " and trunc(coalesce(av.finalVigencia, :hoje)) >= trunc(:hoje)) ";
        }
        sql += " order by e.ano desc, av.tipoAlvara desc";
        Query q = em.createNativeQuery(sql, Alvara.class);
        q.setParameter("inscricaoCadastral", inscricaoCadastral);
        q.setParameter("calculado", SituacaoCalculoAlvara.CALCULADO.name());
        q.setParameter("situacaoProcesso", Lists.newArrayList(SituacaoCalculoAlvara.EFETIVADO.name(), SituacaoCalculoAlvara.RECALCULADO.name()));
        if (vigente) {
            q.setParameter("hoje", new Date());
        }
        return q.getResultList();
    }

    public Alvara adicionaReciboImpressaoAlvara(Alvara alvara, ReciboImpressaoAlvara recibo) {
        recibo.setDataImpressao(new Date());
        recibo.setNomeResposavel(recibo.getNomeResposavel().toUpperCase());
        recibo.setSequencia(singletonGeradorCodigo.getProximoCodigo(ReciboImpressaoAlvara.class, "sequencia"));
        recibo.setAlvara(alvara);
        alvara.getRecibosImpressaoAlvara().add(recibo);
        return salvarRetornando(alvara);
    }

    public ReciboImpressaoAlvara ultimoReciboImpressaoDoAlvara(Alvara alvara) {
        String sql = " select rc.* from ReciboImpressaoAlvara rc where rc.alvara_id = :al and rownum = 1 order by id desc ";
        Query q = em.createNativeQuery(sql, ReciboImpressaoAlvara.class);
        q.setParameter("al", alvara.getId());
        return (ReciboImpressaoAlvara) q.getSingleResult();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<CadastroEconomico> buscarCadastroEconomicoParaGeracaoDeAlvara(FiltroProcessoRenovacaoAlvara filtro) {
        String sql = " select ce.id, " +
            " ce.inscricaocadastral, " +
            " ce.pessoa_id, " +
            " ce.areapublicidade, " +
            " (select ece.areaUtilizacao " +
            " from enderecocadastroeconomico ece " +
            " where ece.tipoendereco = :tipoEndereco " +
            " and ece.cadastroeconomico_id = ce.id " +
            " fetch first 1 rows only), " +
            " ce.abertura, " +
            " ce.classificacaoAtividade, " +
            " ce.naturezajuridica_id, " +
            " enquadramento.tipoissqn " +
            " from cadastroeconomico ce                                                                                                       " +
            " left join EnquadramentoFiscal enquadramento on enquadramento.cadastroEconomico_id = ce.id and enquadramento.fimVigencia is null ";

        sql = adicionarSql(sql, "inner join economicocnae e on ce.id = e.cadastroeconomico_id", !filtro.getCnaes().isEmpty());

        sql += " where ce.inscricaocadastral between :cmcInicial and :cmcFinal                                          " +
            " and (select situacao.situacaocadastral from ce_situacaocadastral sit                                      " +
            "      inner join situacaocadastroeconomico situacao on sit.situacaocadastroeconomico_id = situacao.id      " +
            "      where sit.cadastroeconomico_id = ce.id " +
            "      and situacao.id = (select max(s.id) from situacaocadastroeconomico s " +
            "                         inner join ce_situacaocadastral ces on ces.situacaocadastroeconomico_id = s.id    " +
            "                         where ces.cadastroeconomico_id = sit.cadastroeconomico_id)) in (:situacoesAtivas) ";

        sql = adicionarSql(sql, " and enquadramento.tipoIssqn = :mei ", filtro.isMei());
        sql = adicionarSql(sql, " and ce.naturezajuridica_id in (:listaIdsNaturezaJuridica) ", !filtro.getNaturezasJuridicas().isEmpty());
        sql = adicionarSql(sql, " and ce.tipoAutonomo_id in (:listaIdsTipoAutonomo) ", !filtro.getTiposAutonomos().isEmpty());
        sql = adicionarSql(sql, " and e.cnae_id in (:listaIdsCnae) ", !filtro.getCnaes().isEmpty());

        String juncao = "";
        sql = adicionarSql(sql, " and ( ", (filtro.isGrauDeRiscoBaixoA() || (filtro.isGrauDeRiscoBaixoB())));
        sql = adicionarSql(sql, " exists (select ec.id from economicocnae ec inner join cnae on cnae.id = ec.cnae_id " +
            " where ec.cadastroeconomico_id = ce.id and cnae.grauderisco = :grauDeRiscoBaixoA and cnae.situacao = :cnaeAtivo) ", filtro.isGrauDeRiscoBaixoA());
        sql = adicionarSql(sql, " or ", filtro.isGrauDeRiscoBaixoA());
        sql = adicionarSql(sql, juncao + " exists (select ec.id from economicocnae ec inner join cnae on cnae.id = ec.cnae_id " +
            " where ec.cadastroeconomico_id = ce.id and cnae.grauderisco = :grauDeRiscoBaixoB and cnae.situacao = :cnaeAtivo) ", filtro.isGrauDeRiscoBaixoB());
        sql = adicionarSql(sql, " ) ", (filtro.isGrauDeRiscoBaixoA() || (filtro.isGrauDeRiscoBaixoB())));

        sql += " and not exists (select ec.id from economicocnae ec inner join cnae on cnae.id = ec.cnae_id " +
            " where ec.cadastroeconomico_id = ce.id and cnae.grauderisco = :grauDeRiscoAlto and cnae.situacao = :cnaeAtivo) ";

        sql += " and not exists (select cal.id from " + filtro.getTipoAlvara().getTabelaSql() + " cal " +
            " inner join Alvara alv on alv.id = cal.alvara_id " +
            " where cal.cadastroeconomico_id = ce.id " +
            " and alv.tipoalvara = :tipoAlvara " +
            " and cal.situacaoCalculoAlvara = :situacaoAlvara ";

        sql = adicionarSql(sql, " and alv.vencimento >= :dataAtual ", !LOCALIZACAO.equals(filtro.getTipoAlvara()));
        sql = adicionarSql(sql, " and not exists (select 1 " + processoSuspensaoCassacaoAlvaraFacade.sqlAlvaraCassado("alv.id") + ")", true);

        sql += " )";
        sql = adicionarSql(sql, " and not exists (select 1 " + processoSuspensaoCassacaoAlvaraFacade.sqlEmissaoAlvaraSuspensa("ce.id") + ")", true);
        sql += " order by ce.inscricaoCadastral";

        Query q = em.createNativeQuery(sql);
        q.setParameter("cmcInicial", filtro.getCmcInicial());
        q.setParameter("cmcFinal", filtro.getCmcFinal());
        q.setParameter("situacoesAtivas", Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name(), SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name()));
        q.setParameter("tipoAlvara", filtro.getTipoAlvara().name());
        q.setParameter("situacaoAlvara", SituacaoCalculoAlvara.EFETIVADO.name());
        q.setParameter("grauDeRiscoAlto", GrauDeRiscoDTO.ALTO.name());
        q.setParameter("cnaeAtivo", CNAE.Situacao.ATIVO.name());
        q.setParameter("tipoEndereco", TipoEndereco.COMERCIAL.name());

        atribuirParametroQuery(q, "mei", TipoIssqn.MEI.name(), filtro.isMei());
        atribuirParametroQuery(q, "dataAtual", sistemaFacade.getDataOperacao(), !LOCALIZACAO.equals(filtro.getTipoAlvara()));
        atribuirParametroQuery(q, "grauDeRiscoBaixoA", GrauDeRiscoDTO.BAIXO_RISCO_A.name(), filtro.isGrauDeRiscoBaixoA());
        atribuirParametroQuery(q, "grauDeRiscoBaixoB", GrauDeRiscoDTO.BAIXO_RISCO_B.name(), filtro.isGrauDeRiscoBaixoB());
        atribuirParametroQuery(q, "listaIdsNaturezaJuridica", Util.montarIdsList(filtro.getNaturezasJuridicas()), !filtro.getNaturezasJuridicas().isEmpty());
        atribuirParametroQuery(q, "listaIdsTipoAutonomo", Util.montarIdsList(filtro.getTiposAutonomos()), !filtro.getTiposAutonomos().isEmpty());
        atribuirParametroQuery(q, "listaIdsCnae", Util.montarIdsList(filtro.getCnaes()), !filtro.getCnaes().isEmpty());

        List<Object[]> lista = q.getResultList();
        List<CadastroEconomico> retorno = Lists.newArrayList();
        for (Object[] obj : lista) {
            CadastroEconomico cadastro = new CadastroEconomico();
            cadastro.setId(((BigDecimal) obj[0]).longValue());
            cadastro.setInscricaoCadastral(obj[1] != null ? (String) obj[1] : null);
            cadastro.setPessoa(pessoaFacade.recuperarSimples(((BigDecimal) obj[2]).longValue()));
            cadastro.setAreaPublicidade(obj[3] != null ? ((BigDecimal) obj[3]).doubleValue() : 0);
            cadastro.setAbertura(obj[5] != null ? ((Date) obj[5]) : null);
            cadastro.setClassificacaoAtividade(obj[6] != null ? ClassificacaoAtividade.valueOf((String) obj[6]) : null);
            cadastro.setNaturezaJuridica(obj[7] != null ? naturezaJuridicaFacade.recuperar(((BigDecimal) obj[7]).longValue()) : null);

            EnquadramentoFiscal enquadramentoFiscal = new EnquadramentoFiscal();
            enquadramentoFiscal.setCadastroEconomico(cadastro);
            enquadramentoFiscal.setInicioVigencia(cadastro.getAbertura());
            enquadramentoFiscal.setTipoIssqn(obj[8] != null ? TipoIssqn.valueOf((String) obj[8]) : null);
            cadastro.getEnquadramentos().add(enquadramentoFiscal);

            cadastro.getEconomicoCNAE().size();
            cadastro.getListaBCECaracFuncionamento().size();
            cadastro.getEnquadramentos().size();
            cadastro.getLocalizacao().setAreaUtilizacao(obj[4] != null ? ((BigDecimal) obj[4]).doubleValue() : 0);
            retorno.add(cadastro);
        }
        return retorno;
    }

    private boolean[] alvaraSuspensoOuCassado(Long idAlvara, Long idCmc) {
        boolean alvaraSuspenso = processoSuspensaoCassacaoAlvaraFacade.emissaoAlvaraSuspensa(idCmc);
        boolean alvaraCassado = processoSuspensaoCassacaoAlvaraFacade.alvaraCassado(idAlvara);
        return new boolean[]{alvaraSuspenso, alvaraCassado};
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public ImpressaoAlvara imprimirAlvara(Alvara alvara, TipoAlvara tipoAlvara, Boolean isento, boolean retornando) throws IOException, JRException {
        ImprimeAlvara imprime = new ImprimeAlvara();
        Long idRevisaoEndereco = buscarUltimaRevisaoTabelaPelaData(alvara.getId());
        Query q = em.createNativeQuery(imprime.getSqlAlvara(tipoAlvara, true, (idRevisaoEndereco != null)));
        q.setParameter("idAlvara", alvara.getId());
        if (idRevisaoEndereco != null) {
            q.setParameter("idRevisao", idRevisaoEndereco);
        }
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            Object[] object = (Object[]) q.getResultList().get(0);
            ImpressaoAlvara impressao = new ImpressaoAlvara(object, alvaraSuspensoOuCassado(alvara.getId(), alvara.getCadastroEconomico().getId()));
            verificarCnaesNoAlvara(impressao.getIdAlvara(), impressao.getIdCadastroEconomico(), impressao.getDataCalculo());
            if (isento) {
                impressao = recuperarEnderecoLocalizacaoCMC(alvara, impressao);

                List<AlvaraCnaes> cnaesPrimarios = buscarAlvaraCnaePrimariaDoCMC(alvara.getCadastroEconomico());
                if (!cnaesPrimarios.isEmpty()) {
                    impressao.setAlvarasCnaesPrimarias(cnaesPrimarios);
                }
                List<AlvaraCnaes> cnaesSecundarios = buscarAlvaraCnaeSecundariaDoCMC(alvara.getCadastroEconomico());
                if (!cnaesSecundarios.isEmpty()) {
                    impressao.setAlvarasCnaesSecundarias(cnaesSecundarios);
                }
            } else {
                if (TipoAlvara.FUNCIONAMENTO.equals(tipoAlvara) || TipoAlvara.SANITARIO.equals(tipoAlvara)) {
                    impressao = recuperarEnderecoAlvaraPago(alvara, impressao, imprime, idRevisaoEndereco);
                }
                impressao.setAlvarasCnaesPrimarias(buscarAlvaraCnaePrimaria(alvara));
                impressao.setAlvarasCnaesSecundarias(buscarAlvaraCnaeSecundaria(alvara));
            }
            impressao.setObservacao(alvara.getObservacao());
            impressao.setRodapeAlvara(buscarRodapeAlvara(alvara.getId()));
            atribuirQrCodeAlvara(Lists.newArrayList(impressao));
            Connection connection = imprime.recuperaConexaoJDBC();
            try {
                if (!retornando) {
                    boolean cadastroSuspenso = processoSuspensaoCassacaoAlvaraFacade.emissaoAlvaraSuspensa(alvara.getCadastroEconomico().getId());
                    boolean alvaraCassado = processoSuspensaoCassacaoAlvaraFacade.alvaraCassado(alvara.getId());
                    imprime.imprimirAlvara(alvara, impressao, sistemaFacade.getLogin(),
                        configuracaoTributarioFacade.retornaUltimo().getMensagemRodapeAlvara(), connection);
                }
            } finally {
                AbstractReport.fecharConnection(connection);
            }
            return impressao;
        }
        return null;
    }

    private void atribuirCnaeExercidoNoLocal(ProcessoCalculoAlvara proc) {
        for (CNAEProcessoCalculoAlvara cnae : proc.getCnaes()) {
            String sql = "select coalesce(EXERCIDANOLOCAL, 0) " +
                "from ECONOMICOCNAE ec " +
                "where ec.CADASTROECONOMICO_ID = :idCadastro " +
                "  and ec.CNAE_ID = :idCnae";
            Query q = em.createNativeQuery(sql);
            q.setParameter("idCadastro", proc.getCadastroEconomico().getId());
            q.setParameter("idCnae", cnae.getCnae().getId());
            List<BigDecimal> result = q.getResultList();
            if (!result.isEmpty()) {
                cnae.setExercidaNoLocal(result.get(0).intValue() == 1);
            } else {
                cnae.setExercidaNoLocal(false);
            }
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public ImpressaoAlvara imprimirNovoAlvara(ProcessoCalculoAlvara processoCalculo, Boolean isento, boolean retornando) throws IOException, JRException {
        ImprimeAlvara imprimeAlvara = new ImprimeAlvara();
        Query q = em.createNativeQuery(imprimeAlvara.getNovoSqlAlvara(true));
        q.setParameter("idAlvara", processoCalculo.getAlvara().getId());
        List<Object[]> alvaras = q.getResultList();
        if (alvaras != null && !alvaras.isEmpty()) {
            atribuirCnaeExercidoNoLocal(processoCalculo);
            Object[] object = alvaras.get(0);
            ImpressaoAlvara impressao = new ImpressaoAlvara(object, alvaraSuspensoOuCassado(processoCalculo.getAlvara().getId(), processoCalculo.getAlvara().getCadastroEconomico().getId()));
            formatarCpfCnpj(impressao);
            verificarCnaesNoAlvara(impressao.getIdAlvara(), impressao.getIdCadastroEconomico(), impressao.getDataCalculo());
            impressao.setAlvaraCnaes(processoCalculo.recuperarCnaes());
            AlvaraEnderecos enderecoPrincipal = processoCalculo.buscarEnderecoPrincipal();
            if (enderecoPrincipal != null) {
                impressao.setAreaUtilizacaoEnderecoPrincipal(enderecoPrincipal.getAreaUtilizacao());
            }
            impressao.setEnderecosSecundarios(processoCalculo.buscarEnderecosSecundarios());
            impressao.setObservacao(processoCalculo.getAlvara().getObservacao());
            impressao.setRodapeAlvara(buscarRodapeAlvara(processoCalculo.getAlvara().getId()));
            impressao.setCabecalhoTiposAlvara(montarCabecalhoTiposAlvara(impressao));
            atribuirQrCodeAlvara(Lists.newArrayList(impressao));
            Connection connection = imprimeAlvara.recuperaConexaoJDBC();
            try {
                if (!retornando) {
                    imprimeAlvara.imprimirAlvara(processoCalculo.getAlvara(), impressao, sistemaFacade.getLogin(),
                        configuracaoTributarioFacade.retornaUltimo().getMensagemRodapeAlvara(), connection);
                }
            } finally {
                AbstractReport.fecharConnection(connection);
            }
            return impressao;
        }
        return null;
    }

    private void formatarCpfCnpj(ImpressaoAlvara impressao) {
        if (!Strings.isNullOrEmpty(impressao.getCpfCnpj())) {
            if (StringUtil.retornaApenasNumeros(impressao.getCpfCnpj()).length() == 11) {
                impressao.setCpfCnpj(Util.formatarCpf(impressao.getCpfCnpj()));
            } else {
                impressao.setCpfCnpj(Util.formatarCnpj(impressao.getCpfCnpj()));
            }
        }
    }

    private void verificarCnaesNoAlvara(Long idAlvara, Long idCadastroEconomico, Date dataCalculo) {
        String sql = "select id from CNAEAlvara where alvara_id = :idAlvara";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAlvara", idAlvara);
        if (q.getResultList().isEmpty()) {
            criarCnaeAlvara(idAlvara, idCadastroEconomico, dataCalculo);
        }
    }

    private void criarCnaeAlvara(Long idAlvara, Long idCadastroEconomico, Date dataCalculo) {
        String insert = "insert into CNAEAlvara (id, alvara_id, cnae_id, horariofuncionamento_id) " +
            "select hibernate_sequence.nextval, " + idAlvara + ", cnae_id, horariofuncionamento_id " +
            " from EconomicoCNAE where CADASTROECONOMICO_ID = " + idCadastroEconomico +
            " and inicio <= to_date('" + DataUtil.getDataFormatada(dataCalculo) + "','dd/MM/yyyy')";
        try {
            em.createNativeQuery(insert).executeUpdate();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private List<AlvaraCnaes> buscarAlvaraCnaePrimaria(Alvara alvara) {
        ImprimeAlvara imprime = new ImprimeAlvara();
        String sql = imprime.getAlvaraCnaes();
        Query q = em.createNativeQuery(sql);
        q.setParameter("alvaraId", alvara.getId());
        q.setParameter("tipo", EconomicoCNAE.TipoCnae.Primaria.name());
        q.setParameter("ativo", CNAE.Situacao.ATIVO.name());
        return montarListaCnaes(q);
    }

    private List<AlvaraCnaes> buscarAlvaraCnaePrimariaDoCMC(CadastroEconomico cadastroEconomico) {
        ImprimeAlvara imprime = new ImprimeAlvara();
        String sql = imprime.getCnaesDoCadastro();
        Query q = em.createNativeQuery(sql);
        q.setParameter("cadastroId", cadastroEconomico.getId());
        q.setParameter("tipo", EconomicoCNAE.TipoCnae.Primaria.name());
        q.setParameter("ativo", CNAE.Situacao.ATIVO.name());
        return montarListaCnaes(q);
    }

    private List<AlvaraCnaes> buscarAlvaraCnaeSecundaria(Alvara alvara) {
        ImprimeAlvara imprime = new ImprimeAlvara();
        String sql = imprime.getAlvaraCnaes();
        Query q = em.createNativeQuery(sql);
        q.setParameter("alvaraId", alvara.getId());
        q.setParameter("tipo", EconomicoCNAE.TipoCnae.Secundaria.name());
        q.setParameter("ativo", CNAE.Situacao.ATIVO.name());
        return montarListaCnaes(q);
    }

    private List<AlvaraCnaes> buscarAlvaraCnaeSecundariaDoCMC(CadastroEconomico cadastroEconomico) {
        ImprimeAlvara imprime = new ImprimeAlvara();
        String sql = imprime.getCnaesDoCadastro();
        Query q = em.createNativeQuery(sql);
        q.setParameter("cadastroId", cadastroEconomico.getId());
        q.setParameter("tipo", EconomicoCNAE.TipoCnae.Secundaria.name());
        q.setParameter("ativo", CNAE.Situacao.ATIVO.name());
        return montarListaCnaes(q);
    }

    private List<AlvaraCnaes> montarListaCnaes(Query q) {
        List<AlvaraCnaes> retorno = new ArrayList<>();
        if (!q.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                AlvaraCnaes ac = new AlvaraCnaes();
                ac.setCodigoCnae((String) obj[0]);
                ac.setDescricaoDetalhada((String) obj[1]);
                ac.setGrauDeRisco(obj[2] != null ? GrauDeRiscoDTO.valueOf((String) obj[2]).getDescricao() : "");
                retorno.add(ac);
            }
        }
        return retorno;
    }

    private List<AlvaraAtividadesLicenciadas> buscarAlvaraAtividadesLicenciadas(Alvara alvara) {
        ImprimeAlvara imprime = new ImprimeAlvara();
        String sql = imprime.getAtividadesLicenciadas();
        Query q = em.createNativeQuery(sql);
        q.setParameter("alvaraId", alvara.getId());
        List<AlvaraAtividadesLicenciadas> retorno = new ArrayList<>();
        if (!q.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                AlvaraAtividadesLicenciadas aal = new AlvaraAtividadesLicenciadas();
                aal.setAlvaraId(((BigDecimal) obj[0]).longValue());
                aal.setCodigoCnae((String) obj[1]);
                aal.setDescricaoDetalhada((String) obj[2]);
                aal.setHorarios((String) obj[3]);
                retorno.add(aal);
            }
        }
        return retorno;
    }

    private ImpressaoAlvara recuperarEnderecoAlvaraPago(Alvara alvara, ImpressaoAlvara impressao, ImprimeAlvara imprime, Long idRevisao) {
        ImpressaoAlvara impressaoEnderecoLocalizacaoPago = impressao;
        Alvara alvaraRecuperado = calculoAlvaraFacade.buscarAlvaraLocalizacaoPago(alvara.getCadastroEconomico(), alvara.getExercicio(), alvara);
        if (alvaraRecuperado != null) {
            Query q = em.createNativeQuery(imprime.getSQLEnderecoAlvaraLocalizacaoPago(idRevisao != null));
            q.setParameter("idAlvara", alvaraRecuperado.getId());
            if (idRevisao != null) {
                q.setParameter("idRevisao", idRevisao);
            }
            q.setMaxResults(1);
            if (!q.getResultList().isEmpty()) {
                Object[] endereco = (Object[]) q.getResultList().get(0);
                impressaoEnderecoLocalizacaoPago.setLogradouro((String) endereco[0]);
                impressaoEnderecoLocalizacaoPago.setNumero((String) endereco[1]);
                impressaoEnderecoLocalizacaoPago.setComplemento((String) endereco[2]);
                impressaoEnderecoLocalizacaoPago.setBairro((String) endereco[3]);
                impressaoEnderecoLocalizacaoPago.setCep((String) endereco[4]);
                impressaoEnderecoLocalizacaoPago.setAlvarasAtividadesLicenciadas(buscarAlvaraAtividadesLicenciadas(alvaraRecuperado));
                return impressaoEnderecoLocalizacaoPago;
            }
        } else {
            impressao.setAlvarasAtividadesLicenciadas(buscarAlvaraAtividadesLicenciadas(alvara));
        }
        return impressao;
    }

    private ImpressaoAlvara recuperarEnderecoLocalizacaoCMC(Alvara alvara, ImpressaoAlvara impressao) {
        impressao.setIdAlvara(alvara.getId());
        EnderecoCorreio localizacao = alvara.getCadastroEconomico().getLocalizacao().converterEnderecoCadastroEconomicoPrincipalParaEnderecoCorreio();
        if (localizacao != null && localizacao.getLogradouro() != null && localizacao.getBairro() != null) {
            impressao.setLogradouro((localizacao.getTipoLogradouro() != null ? localizacao.getTipoLogradouro().getDescricao() + " " : "") + localizacao.getLogradouro());
            impressao.setNumero(localizacao.getNumero());
            impressao.setComplemento(localizacao.getComplemento());
            impressao.setBairro(localizacao.getBairro());
            impressao.setCep(localizacao.getCep());
            impressao.setAlvarasAtividadesLicenciadas(buscarAlvaraAtividadesLicenciadas(alvara));
        }
        return impressao;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public ByteArrayOutputStream imprimirAlvaraPortal(Long idAlvara, TipoAlvara tipoAlvara, String caminho, String caminhoImagem) throws IOException, JRException {
        VOAlvara voAlvara = calculoAlvaraFacade.preencherVOAlvaraPorIdAlvara(idAlvara);
        boolean isento = (voAlvara != null && !voAlvara.getItens().isEmpty()) ? voAlvara.getItens().get(0).getIsento() : false;

        if (voAlvara != null) {
            Alvara alvara = recuperar(idAlvara);

            ImpressaoAlvara impressao;

            if (VOAlvara.TipoVoAlvara.CALCULO.equals(voAlvara.getTipoVoAlvara())) {
                impressao = imprimirAlvara(alvara, tipoAlvara, isento, true);
            } else {
                ProcessoCalculoAlvara proc = em.find(ProcessoCalculoAlvara.class, voAlvara.getId());
                impressao = imprimirNovoAlvara(proc, isento, true);
            }

            if (impressao != null) {
                ImprimeAlvara imprime = new ImprimeAlvara();
                Connection connection = imprime.recuperaConexaoJDBC();
                try {
                    ByteArrayOutputStream byteArrayOutputStream = imprime.imprimirAlvaraRetornando(alvara, impressao, sistemaFacade.getLogin(),
                        configuracaoTributarioFacade.retornaUltimo().getMensagemRodapeAlvara(), caminho, caminhoImagem, connection);
                    AbstractReport.fecharConnection(connection);
                    return byteArrayOutputStream;
                } catch (Exception e) {
                    AbstractReport.fecharConnection(connection);
                    return null;
                }
            }
        }
        return null;
    }


    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<ImpressaoAlvara> buscarAlvarasParaImpressaoEmLote(TipoAlvara tipoAlvara, Integer exercicio, String inscricaoInicial, String inscricaoFinal) {
        ImprimeAlvara imprime = new ImprimeAlvara();
        Query q = em.createNativeQuery(imprime.getSqlAlvara(tipoAlvara, false, false));
        q.setParameter("tipoAlvara", tipoAlvara.name());
        q.setParameter("ano", exercicio);
        q.setParameter("inscricaoInicial", inscricaoInicial);
        q.setParameter("inscricaoFinal", inscricaoFinal);
        List<ImpressaoAlvara> impressoes = Lists.newArrayList();
        List<Object[]> lista = q.getResultList();
        if (!lista.isEmpty()) {
            for (Object[] obj : lista) {
                ImpressaoAlvara impressaoAlvara = new ImpressaoAlvara(obj, new boolean[]{false, false});
                impressaoAlvara.setRodapeAlvara(buscarRodapeAlvara(impressaoAlvara.getIdAlvara()));

                boolean[] situacoes = alvaraSuspensoOuCassado(impressaoAlvara.getIdAlvara(), impressaoAlvara.getIdCadastroEconomico());
                impressaoAlvara.setAlvaraSuspenso(situacoes[0]);
                impressaoAlvara.setAlvaraCassado(situacoes[1]);

                impressoes.add(impressaoAlvara);
            }
        }
        return impressoes;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void imprimirAlvara(List<ImpressaoAlvara> impressoes, TipoAlvara tipoAlvara) throws IOException, JRException {
        ImprimeAlvara imprime = new ImprimeAlvara();
        Connection connection = imprime.recuperaConexaoJDBC();
        try {
            if (!impressoes.isEmpty()) {
                List jaspers = new ArrayList();

                atribuirQrCodeAlvara(impressoes);

                List<List<ImpressaoAlvara>> particoes = Lists.partition(impressoes, 500);
                for (List<ImpressaoAlvara> parte : particoes) {
                    jaspers.add(imprime.gerarBytesDoRelatorio(tipoAlvara, parte, sistemaFacade.getLogin(),
                        configuracaoTributarioFacade.retornaUltimo().getMensagemRodapeAlvara(), connection));
                }
                ByteArrayOutputStream byteArrayOutputStream = imprime.exportarJaspersParaPDF(jaspers);
                imprime.escreveNoResponse(tipoAlvara.getJasper(), byteArrayOutputStream.toByteArray());
            } else {
                throw new ExcecaoNegocioGenerica("Não foram localizados registros para os filtros informados.");
            }
        } finally {
            AbstractReport.fecharConnection(connection);
        }
    }

    private void atribuirQrCodeAlvara(List<ImpressaoAlvara> impressoes) {
        String urlPortal = configuracaoTributarioFacade.recuperarUrlPortal();
        for (ImpressaoAlvara impressao : impressoes) {
            impressao.setQrCode(Util.generateQRCodeImage(urlPortal + "/autenticidade-de-documentos/alvara/" + impressao.getIdAlvara() + "/", 300, 300));
            impressao.setUrlPortal(urlPortal + "/autenticidade-de-documentos/");
        }
    }

    public WSAlvara buscarAndMontarWsAlvaraPorId(Long id) {
        String sql = " select a.* from alvara a where a.id = :idAlvara ";
        Query q = em.createNativeQuery(sql, Alvara.class);
        q.setParameter("idAlvara", id);

        List<Alvara> alvaras = q.getResultList();
        Alvara alvara = null;
        if (alvaras != null && !alvaras.isEmpty()) {
            alvara = alvaras.get(0);
        }
        WSAlvara wsAlvara = null;
        if (alvara != null) {
            wsAlvara = new WSAlvara();
            wsAlvara.setTipo(alvara.getTipoAlvara() != null ? alvara.getTipoAlvara().getDescricao() : "");
            wsAlvara.setAno(alvara.getExercicio() != null ? alvara.getExercicio().getAno() : null);
            wsAlvara.setCmc(alvara.getCadastroEconomico() != null ? alvara.getCadastroEconomico().getInscricaoCadastral() : "");
            wsAlvara.setEmissao(alvara.getEmissao());
            wsAlvara.setVencimento(alvara.getVencimento());
        }
        return wsAlvara;
    }

    public void atribuirParametroQuery(Query q, String parametro, Object valor, boolean condicao) {
        if (condicao) {
            q.setParameter(parametro, valor);
        }
    }

    public String adicionarSql(String sql, String adicao, boolean condicao) {
        if (condicao) {
            sql += adicao;
        }
        return sql;
    }

    public Long buscarUltimaRevisaoTabelaPelaData(Long idAlvara) {
        try {
            if (idAlvara != null) {
                String sql = " select max(rev.id) from alvara_aud aud " +
                    " inner join calculoalvaralocalizacao calc on calc.alvara_id = aud.id " +
                    " inner join calculo c on calc.id = c.id " +
                    " inner join revisaoauditoria rev on aud.rev = rev.id " +
                    " where aud.id = :idAlvara " +
                    " and trunc(rev.datahora) <= trunc(c.datacalculo) ";


                Query q = em.createNativeQuery(sql);
                q.setParameter("idAlvara", idAlvara);

                List<BigDecimal> ids = q.getResultList();

                return (ids != null && !ids.isEmpty()) ? (ids.get(0) != null ? ids.get(0).longValue() : null) : null;
            }
        } catch (Exception e) {
            logger.error("Erro ao busca revisao. ", e);
            return null;
        }
        return null;
    }

    public String buscarRodapeAlvara(Long idAlvara) {
        return rodapeAlvaraFacade.buscarRodapeAlvara(idAlvara);
    }

    private String montarCabecalhoTiposAlvara(ImpressaoAlvara impressao) {
        try {
            List<AlvaraCnaes> cnaes = Lists.newArrayList();
            if (impressao != null) {
                if (impressao.getAlvaraCnaes() != null) {
                    cnaes.addAll(impressao.getAlvaraCnaes());
                } else {
                    if (impressao.getAlvarasCnaesPrimarias() != null) {
                        cnaes.addAll(impressao.getAlvarasCnaesPrimarias());
                    }
                    if (impressao.getAlvarasCnaesSecundarias() != null) {
                        cnaes.addAll(impressao.getAlvarasCnaesSecundarias());
                    }
                }
            }

            String cabecalhoTiposAlvara = "LOCALIZAÇÃO, FUNCIONAMENTO";
            if (cnaes.stream().noneMatch(c -> c.getExercidaNoLocal() && c.getInteresseDoEstado()) &&
                cnaes.stream().anyMatch(c -> c.getExercidaNoLocal() && c.getSanitario())) {
                cabecalhoTiposAlvara += ", SANITÁRIO";
            }
            return cabecalhoTiposAlvara;
        } catch (Exception e) {
            return "LOCALIZAÇÃO, FUNCIONAMENTO, SANITÁRIO";
        }
    }
}


