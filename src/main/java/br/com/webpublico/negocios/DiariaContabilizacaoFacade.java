/*
 * Codigo gerado automaticamente em Thu Nov 22 14:40:47 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class DiariaContabilizacaoFacade extends SuperFacadeContabil<DiariaContabilizacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PropostaConcessaoDiariaFacade propostaConcessaoDiariaFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ConfigDiariaDeCampoFacade configDiariaDeCampoFacade;
    @EJB
    private ConfigDiariaCivilFacade configDiariaCivilFacade;
    @EJB
    private ConfigSuprimentoDeFundosFacade configSuprimentoDeFundosFacade;
    @EJB
    private ConfigLiquidacaoFacade configLiquidacaoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DiariaContabilizacaoFacade() {
        super(DiariaContabilizacao.class);
    }

    public PropostaConcessaoDiariaFacade getPropostaConcessaoDiariaFacade() {
        return propostaConcessaoDiariaFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public ConfigDiariaDeCampoFacade getConfigDiariaDeCampoFacade() {
        return configDiariaDeCampoFacade;
    }

    public ConfigDiariaCivilFacade getConfigDiariaCivilFacade() {
        return configDiariaCivilFacade;
    }

    public ConfigSuprimentoDeFundosFacade getConfigSuprimentoDeFundosFacade() {
        return configSuprimentoDeFundosFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public void setClasseCredorFacade(ClasseCredorFacade classeCredorFacade) {
        this.classeCredorFacade = classeCredorFacade;
    }

    @Override
    public DiariaContabilizacao recuperar(Object id) {
        DiariaContabilizacao dc = em.find(DiariaContabilizacao.class, id);
        dc.getDesdobramentoDiaria().size();
        return dc;
    }

    public BigDecimal saldoInscricaoPorDiaria(PropostaConcessaoDiaria pcc) {
        String sql = "SELECT COALESCE(SUM (VALOR),0) AS INSCRICAO"
            + "   FROM DIARIACONTABILIZACAO WHERE OPERACAODIARIACONTABILIZACAO = 'INSCRICAO' AND TIPOLANCAMENTO = 'NORMAL' AND PROPOSTACONCESSAODIARIA_ID = :diaria";
        Query q = em.createNativeQuery(sql);
        q.setParameter("diaria", pcc.getId());
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal saldoEstornoInscricaoPorDiaria(PropostaConcessaoDiaria pcc) {
        String sql = "SELECT COALESCE(SUM (VALOR),0) AS INSCRICAO"
            + "   FROM DIARIACONTABILIZACAO WHERE OPERACAODIARIACONTABILIZACAO = 'INSCRICAO' AND TIPOLANCAMENTO = 'ESTORNO' AND PROPOSTACONCESSAODIARIA_ID = :diaria";
        Query q = em.createNativeQuery(sql);
        q.setParameter("diaria", pcc.getId());
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal saldoBaixaPorDiaria(PropostaConcessaoDiaria pcc) {
        String sql = "SELECT COALESCE(SUM (VALOR),0) AS INSCRICAO"
            + "   FROM DIARIACONTABILIZACAO WHERE OPERACAODIARIACONTABILIZACAO = 'BAIXA' AND TIPOLANCAMENTO = 'NORMAL' AND PROPOSTACONCESSAODIARIA_ID = :diaria";
        Query q = em.createNativeQuery(sql);
        q.setParameter("diaria", pcc.getId());
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal saldoEstornoBaixaPorDiaria(PropostaConcessaoDiaria pcc) {
        String sql = "SELECT COALESCE(SUM (VALOR),0) AS INSCRICAO"
            + "   FROM DIARIACONTABILIZACAO WHERE OPERACAODIARIACONTABILIZACAO = 'BAIXA' AND TIPOLANCAMENTO = 'ESTORNO'  AND PROPOSTACONCESSAODIARIA_ID = :diaria";
        Query q = em.createNativeQuery(sql);
        q.setParameter("diaria", pcc.getId());
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal saldoApropriacaoPorDiaria(PropostaConcessaoDiaria pcc) {
        String sql = "SELECT COALESCE(SUM (VALOR),0) AS INSCRICAO"
            + "   FROM DIARIACONTABILIZACAO WHERE OPERACAODIARIACONTABILIZACAO = 'APROPRIACAO' AND TIPOLANCAMENTO = 'NORMAL'  AND PROPOSTACONCESSAODIARIA_ID = :diaria";
        Query q = em.createNativeQuery(sql);
        q.setParameter("diaria", pcc.getId());
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal saldoEstornoApropriacaoPorDiaria(PropostaConcessaoDiaria pcc) {
        String sql = "SELECT COALESCE(SUM (VALOR),0) AS INSCRICAO"
            + "   FROM DIARIACONTABILIZACAO WHERE OPERACAODIARIACONTABILIZACAO = 'APROPRIACAO' AND TIPOLANCAMENTO = 'ESTORNO'  AND PROPOSTACONCESSAODIARIA_ID = :diaria";
        Query q = em.createNativeQuery(sql);
        q.setParameter("diaria", pcc.getId());
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal retornaSaldoTotalDiaria(PropostaConcessaoDiaria pcc) {
        BigDecimal saldo = BigDecimal.ZERO;
        saldo = saldoInscricaoPorDiaria(pcc).subtract(saldoEstornoInscricaoPorDiaria(pcc)).subtract(saldoBaixaPorDiaria(pcc)).add(saldoEstornoBaixaPorDiaria(pcc));
        return saldo;
    }

    public BigDecimal retornaSaldoTotalSuprimentoFundo(PropostaConcessaoDiaria pcc) {
        BigDecimal saldo = BigDecimal.ZERO;
        saldo = saldoInscricaoPorDiaria(pcc).subtract(saldoEstornoInscricaoPorDiaria(pcc)).subtract(saldoApropriacaoPorDiaria(pcc)).add(saldoEstornoApropriacaoPorDiaria(pcc)).subtract(saldoBaixaPorDiaria(pcc)).add(saldoEstornoBaixaPorDiaria(pcc));
        return saldo;
    }

    public List<PropostaConcessaoDiaria> listaDiariasPorAgrupador(String numero) {
        String sql = "SELECT PCD.* "
            + " FROM DIARIADECAMPO DC"
            + " INNER JOIN DIARIACAMPO_PROPOSTA DCP ON DCP.DIARIACAMPO_ID = DC.ID "
            + " INNER JOIN PROPOSTACONCESSAODIARIA PCD ON DCP.PROPOSTA_ID = PCD.ID"
            + " WHERE DC.CODIGO = :num";
        Query q = em.createNativeQuery(sql, PropostaConcessaoDiaria.class);
        q.setParameter("num", numero);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    public List<PropostaConcessaoDiaria> listaFiltrandoConcessaoDiariaPorUnidadeOrganizacionalouTipo(String parte, TipoProposta tp, UsuarioSistema usu, Date data) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT DISTINCT prop.* FROM VWHIERARQUIAADMINISTRATIVA VW "
            + " inner join HIERARQUIAORGANIZACIONAL hadm on hadm.id=VW.ID "
            + " inner join vwhierarquiaorcamentaria vworc on hadm.hierarquiaorc_id = vworc.id "
            + " INNER JOIN UNIDADEORGANIZACIONAL U ON U.ID = VWORC.SUBORDINADA_ID "
            + " inner join usuariounidadeorganizacio usuund on usuund.unidadeorganizacional_id = vw.subordinada_id "
            + " INNER JOIN USUARIOSISTEMA USU ON USUUND.USUARIOSISTEMA_ID = USU.ID "
            + " inner join propostaconcessaodiaria prop on PROP.UNIDADEORGANIZACIONAL_ID = u.id "
            + " where to_date(:data, 'dd/MM/yyyy') between vw.iniciovigencia  and coalesce(vw.fimvigencia, to_date(:data, 'dd/MM/yyyy'))"
            + " and to_date(:data, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:data, 'dd/MM/yyyy'))");
        sql.append(" and upper(prop.codigo) like :str  ");
        sql.append(" and usu.id = :usu ");
        sql.append(" and prop.TipoProposta = :tipo ");
        sql.append(" order by PROP.id desc");

        Query q = getEntityManager().createNativeQuery(sql.toString(), PropostaConcessaoDiaria.class);
        q.setParameter("usu", usu.getId());
        q.setParameter("str", "%" + parte.trim().toUpperCase() + "%");
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("tipo", tp.name());
        return q.getResultList();
    }

    @Override
    public void salvar(DiariaContabilizacao entity) {
        super.salvar(entity);
    }

    public void contabilizarDiariaCivil(DiariaContabilizacao entity) {

        ConfigDiariaCivil configDiariaCivil = configDiariaCivilFacade.recuperaConfiguracaoExistente(entity);
        if (configDiariaCivil != null && configDiariaCivil.getEventoContabil() != null) {
            entity.setEventoContabil(configDiariaCivil.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Diárias.");
        }
        entity.gerarHistoricos();

        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
        parametroEvento.setDataEvento(entity.getDataDiaria());
        parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(entity.getEventoContabil());
        parametroEvento.setExercicio(entity.getExercicio());
        parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
        parametroEvento.setIdOrigem(entity.getId().toString());

        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(entity.getValor());
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);

        List<ObjetoParametro> objetos = Lists.newArrayList();
        objetos.add(new ObjetoParametro(entity, item));
        objetos.add(new ObjetoParametro(entity.getPropostaConcessaoDiaria().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(), item));
        objetos.add(new ObjetoParametro(entity.getPropostaConcessaoDiaria().getClasseCredor(), item));
        item.setObjetoParametros(objetos);

        parametroEvento.getItensParametrosEvento().add(item);
        contabilizacaoFacade.contabilizar(parametroEvento);
    }

    public void contabilizarDiariaDeCampo(DiariaContabilizacao entity) {

        ConfigDiariaDeCampo configDiariaDeCampo = configDiariaDeCampoFacade.recuperaConfiguracaoExistente(entity);
        if (configDiariaDeCampo != null && configDiariaDeCampo.getEventoContabil() != null) {
            entity.setEventoContabil(configDiariaDeCampo.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Diárias.");
        }
        entity.gerarHistoricos();

        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
        parametroEvento.setDataEvento(entity.getDataDiaria());
        parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(entity.getEventoContabil());
        parametroEvento.setExercicio(entity.getExercicio());
        parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
        parametroEvento.setIdOrigem(entity.getId().toString());

        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(entity.getValor());
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);

        List<ObjetoParametro> objetos = Lists.newArrayList();
        objetos.add(new ObjetoParametro(entity, item));
        objetos.add(new ObjetoParametro(entity.getPropostaConcessaoDiaria().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(), item));
        objetos.add(new ObjetoParametro(entity.getPropostaConcessaoDiaria().getClasseCredor(), item));
        item.setObjetoParametros(objetos);
        parametroEvento.getItensParametrosEvento().add(item);

        contabilizacaoFacade.contabilizar(parametroEvento);

    }

    public void contabilizarSuprimentoDeFundos(DiariaContabilizacao entity) {
        ConfigSuprimentoDeFundos configSuprimentoDeFundos = configSuprimentoDeFundosFacade.recuperaConfiguracaoExistente(entity);

        if (configSuprimentoDeFundos != null && configSuprimentoDeFundos.getEventoContabil() != null) {
            entity.setEventoContabil(configSuprimentoDeFundos.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Suprimento de Fundos.");
        }
        entity.gerarHistoricos();

        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
        parametroEvento.setDataEvento(entity.getDataDiaria());
        parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(entity.getEventoContabil());
        parametroEvento.setExercicio(entity.getExercicio());
        parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
        parametroEvento.setIdOrigem(entity.getId().toString());

        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(entity.getValor());
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);
        item.setOperacaoClasseCredor(classeCredorFacade.recuperaOperacaoAndVigenciaClasseCredor(entity.getPropostaConcessaoDiaria().getPessoaFisica(), entity.getPropostaConcessaoDiaria().getClasseCredor(), entity.getDataDiaria()));

        List<ObjetoParametro> objetos = Lists.newArrayList();
        objetos.add(new ObjetoParametro(entity, item));
        objetos.add(new ObjetoParametro(entity.getPropostaConcessaoDiaria().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(), item));
        objetos.add(new ObjetoParametro(entity.getPropostaConcessaoDiaria().getClasseCredor(), item));
        item.setObjetoParametros(objetos);

        parametroEvento.getItensParametrosEvento().add(item);
        contabilizacaoFacade.contabilizar(parametroEvento);

    }

    public void contabilizarSuprimentoDeFundosOperacaoApropriacao(DiariaContabilizacao entity, DesdobramentoDiaria desdobramento) {

        if (!Hibernate.isInitialized(entity.getDesdobramentoDiaria())) {
            desdobramento.setDiariaContabilizacao(recuperar(entity.getId()));
        }
        Conta conta = desdobramento.getDesdobramento();
        ConfigSuprimentoDeFundos configuracao = configSuprimentoDeFundosFacade.recuperaConfiguracaoExistente(entity);

        if (configuracao != null && configuracao.getEventoContabil() != null) {
            entity.setEventoContabil(configuracao.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica(" Evento Contábil não encontrado para a operação 'Apropriação' selecionada na contabilização de Suprimento de Fundos.");
        }
        entity.gerarHistoricos();

        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
        parametroEvento.setDataEvento(entity.getDataDiaria());
        parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(entity.getEventoContabil());
        parametroEvento.setExercicio(conta.getExercicio());
        parametroEvento.setClasseOrigem(DesdobramentoDiaria.class.getSimpleName());
        parametroEvento.setIdOrigem(desdobramento.getId().toString());

        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(desdobramento.getValor());
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);
        item.setOperacaoClasseCredor(classeCredorFacade.recuperaOperacaoAndVigenciaClasseCredor(entity.getPropostaConcessaoDiaria().getPessoaFisica(), entity.getPropostaConcessaoDiaria().getClasseCredor(), entity.getDataDiaria()));

        List<ObjetoParametro> objetos = Lists.newArrayList();
        objetos.add(new ObjetoParametro(entity, item));
        objetos.add(new ObjetoParametro(conta, item));

        Preconditions.checkNotNull(desdobramento.getDiariaContabilizacao().getPropostaConcessaoDiaria().getClasseCredor(), "A Classe credor do suprimento de fundo não foi preenchida.");
        objetos.add(new ObjetoParametro(desdobramento.getDiariaContabilizacao().getPropostaConcessaoDiaria().getClasseCredor(), item));
        item.setObjetoParametros(objetos);

        parametroEvento.getItensParametrosEvento().add(item);

        contabilizacaoFacade.contabilizar(parametroEvento);
    }

    public int setaNumeroContabizacaoDiaria() {
        Query q = getEntityManager().createNativeQuery(" select max(to_number(coalesce(numero,'0'))) as numero from diariacontabilizacao " +
            " where numero is not null " +
            " and (tipoProposta = 'CONCESSAO_DIARIA' or tipoProposta = 'CONCESSAO_DIARIACAMPO')");
        q.setMaxResults(1);
        BigDecimal setanumero = (BigDecimal) q.getSingleResult();
        try {
            return setanumero.intValue() + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    public int setaNumeroContabizacaoSuprimento() {
        try {
            Query q = getEntityManager().createNativeQuery(" select max(to_number(coalesce(numero,'0'))) as numero from diariacontabilizacao " +
                " where numero is not null " +
                " and tipoProposta = 'SUPRIMENTO_FUNDO'");
            q.setMaxResults(1);
            BigDecimal setanumero = (BigDecimal) q.getSingleResult();
            return setanumero.intValue() + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    private void trataSituacaoDiaria(DiariaContabilizacao entity, PropostaConcessaoDiaria p) {
        BigDecimal saldoTotalDiaria = retornaSaldoTotalDiaria(entity.getPropostaConcessaoDiaria());
        if (TipoLancamento.NORMAL.equals(entity.getTipoLancamento())) {
            if (OperacaoDiariaContabilizacao.INSCRICAO.equals(entity.getOperacaoDiariaContabilizacao())) {
                saldoTotalDiaria = saldoTotalDiaria.add(entity.getValor());

                if ((saldoTotalDiaria.compareTo(BigDecimal.ZERO) == 0) && ((saldoInscricaoPorDiaria(entity.getPropostaConcessaoDiaria()).subtract(saldoEstornoInscricaoPorDiaria(entity.getPropostaConcessaoDiaria()))).compareTo(entity.getPropostaConcessaoDiaria().getValor()) == 0)) {
                    p.setSituacaoDiaria(SituacaoPropostaConcessaoDiaria.COMPROVADO);
                } else {
                    p.setSituacaoDiaria(SituacaoPropostaConcessaoDiaria.A_COMPROVAR);
                }
            } else {
                saldoTotalDiaria = saldoTotalDiaria.subtract(entity.getValor());

                if (TipoProposta.SUPRIMENTO_FUNDO.equals(p.getTipoProposta()) &&
                    saldoTotalDiaria.compareTo(BigDecimal.ZERO) == 0 &&
                    saldoInscricaoPorDiaria(entity.getPropostaConcessaoDiaria()).subtract(saldoEstornoInscricaoPorDiaria(entity.getPropostaConcessaoDiaria())).compareTo(propostaConcessaoDiariaFacade.getValorEmpenhado(p)) == 0) {

                    p.setSituacaoDiaria(SituacaoPropostaConcessaoDiaria.COMPROVADO);
                } else if ((saldoTotalDiaria.compareTo(BigDecimal.ZERO) == 0) && ((saldoInscricaoPorDiaria(entity.getPropostaConcessaoDiaria()).subtract(saldoEstornoInscricaoPorDiaria(entity.getPropostaConcessaoDiaria()))).compareTo(entity.getPropostaConcessaoDiaria().getValor()) == 0)) {

                    p.setSituacaoDiaria(SituacaoPropostaConcessaoDiaria.COMPROVADO);
                } else {
                    p.setSituacaoDiaria(SituacaoPropostaConcessaoDiaria.A_COMPROVAR);
                }
            }
        } else {
            saldoTotalDiaria = OperacaoDiariaContabilizacao.INSCRICAO.equals(entity.getOperacaoDiariaContabilizacao())
                ? saldoTotalDiaria.subtract(entity.getValor())
                : saldoTotalDiaria.add(entity.getValor());

            if (saldoTotalDiaria.compareTo(BigDecimal.ZERO) == 0 &&
                (saldoInscricaoPorDiaria(entity.getPropostaConcessaoDiaria()).subtract(saldoEstornoInscricaoPorDiaria(entity.getPropostaConcessaoDiaria()))).compareTo(entity.getPropostaConcessaoDiaria().getValor()) == 0) {
                p.setSituacaoDiaria(SituacaoPropostaConcessaoDiaria.COMPROVADO);
            } else {
                p.setSituacaoDiaria(SituacaoPropostaConcessaoDiaria.A_COMPROVAR);
            }
        }
    }

    public void salvarNovo(DiariaContabilizacao entity, Boolean contabilizar) throws ExcecaoNegocioGenerica {
        if (reprocessamentoLancamentoContabilSingleton.isCalculando()) {
            throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        } else {
            PropostaConcessaoDiaria p = propostaConcessaoDiariaFacade.recuperar(entity.getPropostaConcessaoDiaria().getId());

            if (TipoProposta.CONCESSAO_DIARIA.equals(entity.getTipoProposta())) {
                processarDiariaCivil(entity, p, contabilizar);
            } else if (TipoProposta.CONCESSAO_DIARIACAMPO.equals(entity.getTipoProposta())) {
                processarDiariaDeCampo(entity, p, contabilizar);
            } else if (TipoProposta.SUPRIMENTO_FUNDO.equals(entity.getTipoProposta())) {
                processarSuprimentoDeFundo(entity, p, contabilizar);
            }
            gerarNumeroDiariaContabilizacao(entity);
            em.merge(p);
        }
    }

    private void gerarNumeroDiariaContabilizacao(DiariaContabilizacao entity) {
        if (entity.getTipoProposta().equals(TipoProposta.SUPRIMENTO_FUNDO)) {
            entity.setNumero(setaNumeroContabizacaoSuprimento() + "");
        } else if (entity.getTipoProposta().equals(TipoProposta.CONCESSAO_DIARIA) || entity.getTipoProposta().equals(TipoProposta.CONCESSAO_DIARIACAMPO)) {
            entity.setNumero(setaNumeroContabizacaoDiaria() + "");
        }
    }

    private void processarSuprimentoDeFundo(DiariaContabilizacao entity, PropostaConcessaoDiaria p, Boolean contabilizar) {
        trataSituacaoDiaria(entity, p);
        if (entity.getId() == null) {
            entity.setNumero(getUltimoNumeroSuplimentoFundos());
            entity = em.merge(entity);
        } else {
            entity.gerarHistoricos();
            entity = em.merge(entity);
        }
        em.merge(p);
        if (contabilizar) {
            geraContabilizacaoSuprimentoDeFundos(entity);
        }
    }

    private void processarDiariaDeCampo(DiariaContabilizacao entity, PropostaConcessaoDiaria p, Boolean contabilizar) {
        trataSituacaoDiaria(entity, p);
        if (entity.getId() == null) {
            entity.setNumero(getUltimoNumeroDiariaCampo());
            entity = em.merge(entity);
        } else {
            entity.gerarHistoricos();
            entity = em.merge(entity);
        }
        em.merge(p);
        if (contabilizar) {
            contabilizarDiariaDeCampo(entity);
        }
    }

    private void processarDiariaCivil(DiariaContabilizacao entity, PropostaConcessaoDiaria p, Boolean contabilizar) {
        trataSituacaoDiaria(entity, p);
        if (entity.getId() == null) {
            entity.setNumero(getUltimoNumeroDiariaCivil());
            entity = em.merge(entity);
        } else {
            entity.gerarHistoricos();
            entity = em.merge(entity);
        }
        em.merge(p);
        if (contabilizar) {
            contabilizarDiariaCivil(entity);
        }
    }

    public void geraContabilizacaoSuprimentoDeFundos(DiariaContabilizacao entity) {
        if (entity.getOperacaoDiariaContabilizacao().equals(OperacaoDiariaContabilizacao.APROPRIACAO)) {
            entity = recuperar(entity.getId());
            for (DesdobramentoDiaria desdobramento : entity.getDesdobramentoDiaria()) {
                contabilizarSuprimentoDeFundosOperacaoApropriacao(entity, desdobramento);
            }
        } else {
            contabilizarSuprimentoDeFundos(entity);
        }
    }

    public String getUltimoNumeroDiariaCampo() {
        String sql = "SELECT MAX(TO_NUMBER(D.NUMERO))+1 AS ULTIMONUMERO FROM DIARIACONTABILIZACAO D " +
            " WHERE (D.TIPOPROPOSTA = 'CONCESSAO_DIARIACAMPO') ";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    public String getUltimoNumeroDiariaCivil() {
        String sql = "SELECT MAX(TO_NUMBER(D.NUMERO))+1 AS ULTIMONUMERO FROM DIARIACONTABILIZACAO D " +
            " WHERE (D.TIPOPROPOSTA = 'CONCESSAO_DIARIA') ";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    public String getUltimoNumeroSuplimentoFundos() {
        String sql = "SELECT MAX(TO_NUMBER(D.NUMERO))+1 AS ULTIMONUMERO FROM DIARIACONTABILIZACAO D " +
            " WHERE (D.TIPOPROPOSTA = 'SUPRIMENTO_FUNDO' )";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public SingletonGeradorCodigoContabil getSingletonGeradorCodigoContabil() {
        return singletonGeradorCodigoContabil;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        DiariaContabilizacao diaria = (DiariaContabilizacao) entidadeContabil;
        if (diaria.getPropostaConcessaoDiaria().getTipoProposta().equals(TipoProposta.CONCESSAO_DIARIA)) {
            contabilizarDiariaCivil(diaria);
        } else if (diaria.getPropostaConcessaoDiaria().getTipoProposta().equals(TipoProposta.CONCESSAO_DIARIACAMPO)) {
            contabilizarDiariaDeCampo(diaria);
        } else if (diaria.getPropostaConcessaoDiaria().getTipoProposta().equals(TipoProposta.SUPRIMENTO_FUNDO)) {
            geraContabilizacaoSuprimentoDeFundos(diaria);
        }
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(DiariaContabilizacao.class, filtros);
        consulta.incluirJoinsComplementar(" inner join propostaConcessaoDiaria prop on obj.propostaConcessaoDiaria_id = prop.id");
        consulta.incluirJoinsOrcamentoDespesa(" prop.fontedespesaorc_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "obj.dataDiaria");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "obj.dataDiaria");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.PESSOA, "prop.pessoaFisica_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CLASSE, "prop.classecredor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        return Arrays.asList(consulta);
    }
}
