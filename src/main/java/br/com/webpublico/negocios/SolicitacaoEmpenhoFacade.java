/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.SolicitacaoEmpenhoVo;
import br.com.webpublico.enums.*;
import br.com.webpublico.seguranca.service.QueryReprocessamentoService;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author venon
 */
@Stateless
public class SolicitacaoEmpenhoFacade extends AbstractFacade<SolicitacaoEmpenho> {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ReservaFonteDespesaOrcFacade reservaFonteDespesaOrcFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public SolicitacaoEmpenhoFacade() {
        super(SolicitacaoEmpenho.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(SolicitacaoEmpenho entity) {
        super.salvarNovo(entity);
    }

    /**
     * @param valor                  notNull
     * @param unidadeOrganizacional
     * @param tipoEmpenho
     * @param historicoContabil
     * @param fonteDespesaOrc
     * @param despesaOrc             notNull
     * @param data                   notNull
     * @param contaDespesaDesdobrada notNull
     * @param complementoHistorico
     * @param fornecedor             notNull
     * @param reserva                campo boolean que permite a geração ou não da reserva ao
     *                               criar a solicitação
     * @param origem                 notNull
     * @return SolicitacaoEmpenho
     * @throws ExcecaoNegocioGenerica
     */
    public SolicitacaoEmpenho geraSolicitacaoEmpenho(BigDecimal valor, UnidadeOrganizacional unidadeOrganizacional, TipoEmpenho tipoEmpenho,
                                                     HistoricoContabil historicoContabil, FonteDespesaORC fonteDespesaOrc, DespesaORC despesaOrc, Date data,
                                                     Conta contaDespesaDesdobrada, String complementoHistorico, Pessoa fornecedor, Boolean reserva,
                                                     OrigemReservaFonte origem, ClasseCredor classe, OrigemSolicitacaoEmpenho origemSolicitacaoEmpenho, String numeroOrigem) throws ExcecaoNegocioGenerica {
        try {
            SolicitacaoEmpenho se = new SolicitacaoEmpenho();
            se.setComplementoHistorico(complementoHistorico);
            se.setContaDespesaDesdobrada(contaDespesaDesdobrada);
            se.setDataSolicitacao(data);
            se.setDespesaORC(despesaOrc);
            se.setFonteDespesaORC(fonteDespesaOrc);
            se.setHistoricoContabil(historicoContabil);
            se.setTipoEmpenho(tipoEmpenho);
            se.setUnidadeOrganizacional(unidadeOrganizacional);
            se.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            se.setValor(valor);
            se.setFornecedor(fornecedor);
            se.setClasseCredor(classe);
            se.setGerarReserva(reserva);
            salvarNovo(se);
            se.setOrigemSolicitacaoEmpenho(origemSolicitacaoEmpenho);
            if (reserva) {
                reservaFonteDespesaOrcFacade.gerarReservaSolicitacaoEmpenho(se, origem, numeroOrigem);
            }
            return se;
        } catch (ExcecaoNegocioGenerica e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Não foi possível gerar solicitação de empenho");
        }
    }

    public SolicitacaoEmpenho gerarSolicitacaoEmpenhoSalvando(BigDecimal valor, UnidadeOrganizacional unidadeOrganizacional, TipoEmpenho tipoEmpenho,
                                                              HistoricoContabil historicoContabil, FonteDespesaORC fonteDespesaOrc, DespesaORC despesaOrc, Date data, Conta contaDespesaDesdobrada,
                                                              String complementoHistorico, Pessoa fornecedor, Boolean reserva, OrigemReservaFonte origem, ClasseCredor classe,
                                                              Contrato contrato, OrigemSolicitacaoEmpenho origemSolicitacaoEmpenho, String numeroOrigem) throws ExcecaoNegocioGenerica {
        try {
            SolicitacaoEmpenho se = new SolicitacaoEmpenho();
            se.setComplementoHistorico(complementoHistorico);
            se.setContaDespesaDesdobrada(contaDespesaDesdobrada);
            se.setDataSolicitacao(data);
            se.setDespesaORC(despesaOrc);
            se.setFonteDespesaORC(fonteDespesaOrc);
            se.setHistoricoContabil(historicoContabil);
            se.setTipoEmpenho(tipoEmpenho);
            se.setUnidadeOrganizacional(unidadeOrganizacional);
            se.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            se.setValor(valor);
            se.setFornecedor(fornecedor);
            se.setClasseCredor(classe);
            se.setGerarReserva(reserva);
            se.setContrato(contrato);
            se.setOrigemSolicitacaoEmpenho(origemSolicitacaoEmpenho);
            salvarNovo(se);
            if (reserva) {
                reservaFonteDespesaOrcFacade.gerarReservaSolicitacaoEmpenho(se, origem, numeroOrigem);
            }
            return se;
        } catch (ExcecaoNegocioGenerica e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Não foi possível gerar solicitação de empenho");
        }
    }

    public SolicitacaoEmpenho gerarSolicitacaoEmpenhoSalvando(SolicitacaoEmpenhoVo vo) {
        try {
            SolicitacaoEmpenho solicitacaoEmpenho = gerarSolicitacaoEmpenho(vo);
            return salvarRetornando(solicitacaoEmpenho);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Não foi possível gerar Solicitação de Empenho!");
        }
    }

    public SolicitacaoEmpenho gerarSolicitacaoEmpenho(SolicitacaoEmpenhoVo vo) {
        SolicitacaoEmpenho novaSol = new SolicitacaoEmpenho();
        novaSol.setComplementoHistorico(vo.getComplementoHistorico());
        novaSol.setContaDespesaDesdobrada(vo.getContaDespesaDesdobrada());
        novaSol.setDataSolicitacao(vo.getDataSolicitacao());
        novaSol.setDespesaORC(vo.getDespesaOrc());
        novaSol.setFonteDespesaORC(vo.getFonteDespesaOrc());
        novaSol.setHistoricoContabil(vo.getHistoricoContabil());
        novaSol.setTipoEmpenho(vo.getTipoEmpenho());
        novaSol.setUnidadeOrganizacional(vo.getUnidadeOrganizacional());
        novaSol.setUsuarioSistema(vo.getUsuarioSistema());
        novaSol.setValor(vo.getValor());
        novaSol.setFornecedor(vo.getFornecedor());
        novaSol.setClasseCredor(vo.getClasseCredor());
        novaSol.setGerarReserva(vo.getGerarReserva());
        novaSol.setContrato(vo.getContrato());
        novaSol.setOrigemSolicitacaoEmpenho(vo.getOrigemSocilicitacao());
        return novaSol;
    }

    public List<SolicitacaoEmpenho> listaSolicitacoesPendentes() {
        String hql = "from SolicitacaoEmpenho where empenho is null order by id desc";
        Query q = em.createQuery(hql);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public List<SolicitacaoEmpenho> buscarSolicitacoesPendentesPorUnidade(UnidadeOrganizacional unidadeOrganizacional) {
        String hql = "  from SolicitacaoEmpenho " +
            "           where empenho is null " +
            "             and unidadeOrganizacional = :unidade " +
            "             and estornada = :estornada " +
            "           order by dataSolicitacao desc ";
        Query q = em.createQuery(hql);
        q.setParameter("unidade", unidadeOrganizacional);
        q.setParameter("estornada", Boolean.FALSE);
        List<SolicitacaoEmpenho> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return Lists.newArrayList();
        } else {
            return resultado;
        }
    }

    public List<SolicitacaoEmpenho> getSolicitacoesEmpenhoComLicitacao() {
        String hql = "select new SolicitacaoEmpenho(se.id, se.dataSolicitacao, se.valor) from SolicitacaoEmpenho se"
            + " left join se.requisicaoDeCompra rc";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public SolicitacaoEmpenho getSolicitacaoEmpenhoDaRequisicaoDeCompra(RequisicaoDeCompra rc) {
        String hql = "select new SolicitacaoEmpenho(se.id, se.dataSolicitacao,se.valor) from RequisicaoDeCompra rc"
            + " inner join rc.solicitacaoEmpenho se"
            + " where rc = :rc";
        Query q = em.createQuery(hql);
        q.setParameter("rc", rc);
        q.setMaxResults(1);
        try {
            return (SolicitacaoEmpenho) q.getSingleResult();
        } catch (RuntimeException e) {
            return null;
        }
    }

    public DiariaCivilSolicitacaoEmpenho recuperarDiariaCivilSolicitacaoEmpenhoApartirSolicitacaoEmpenho(SolicitacaoEmpenho se) {
        Query consulta = em.createQuery("select dcse from DiariaCivilSolicitacaoEmpenho dcse where dcse.solicitacaoEmpenho.id = :id");
        consulta.setParameter("id", se.getId());
        consulta.setMaxResults(1);
        try {
            DiariaCivilSolicitacaoEmpenho dcse = (DiariaCivilSolicitacaoEmpenho) consulta.getSingleResult();
            Hibernate.initialize(dcse.getPropostaConcessaoDiaria().getDesdobramentos());
            return dcse;
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<SolicitacaoEmpenho> buscarSolicitacoesPendentesPorUnidade(UnidadeOrganizacional unidadeOrganizacional, String filtro) {
        String hql = "from SolicitacaoEmpenho where empenho is null and unidadeOrganizacional = :unidade and lower(complementoHistorico) like :filtro order by id";
        Query q = em.createQuery(hql);
        q.setParameter("unidade", unidadeOrganizacional);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public List<SolicitacaoEmpenho> buscarSolicitacoesPendentesPorUnidadeAndContrato(UnidadeOrganizacional unidadeOrganizacional, Contrato contrato) {
        String sql = "select * from SolicitacaoEmpenho where empenho_id is null and unidadeOrganizacional_id = :unidade and contrato_id = :contrato " +
            " union all  " +
            " select sol.* from SolicitacaoEmpenho sol where sol.empenho_id is null and sol.unidadeOrganizacional_id = :unidade  " +
            " and sol.id in ( " +
            " select execemp.SOLICITACAOEMPENHO_ID from execucaocontratoempenho execemp " +
            " inner join execucaocontrato exec on exec.id = EXECEMP.EXECUCAOCONTRATO_ID  " +
            " where exec.contrato_id = :contrato " +
            " )";
        Query q = em.createNativeQuery(sql);
        q.setParameter("unidade", unidadeOrganizacional.getId());
        q.setParameter("contrato", contrato.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public DividaPublicaSolicitacaoEmpenho recuperarDividaPublicaSolicitacaoEmpenhoApartirSolicitacaoEmpenho(SolicitacaoEmpenho se) {
        Query consulta = em.createQuery("select dpse from DividaPublicaSolicitacaoEmpenho dpse where dpse.solicitacaoEmpenho.id = :id");
        consulta.setParameter("id", se.getId());
        consulta.setMaxResults(1);
        try {
            return (DividaPublicaSolicitacaoEmpenho) consulta.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public ConvenioDespSolicEmpenho recuperarConvenioDespesaSolicitacaoEmpenhoApartirSolicitacaoEmpenho(SolicitacaoEmpenho se) {
        Query consulta = em.createQuery(" select cdse from ConvenioDespSolicEmpenho cdse where cdse.solicitacaoEmpenho.id = :id ");
        consulta.setParameter("id", se.getId());
        consulta.setMaxResults(1);
        try {
            return (ConvenioDespSolicEmpenho) consulta.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public SolicitacaoEmpenho getSolicitacaoEmpenhoPorDiaria(PropostaConcessaoDiaria diaria) {
        String sql = " select sol.* " +
            " from solicitacaoempenho sol " +
            " inner join diariacivilsolicemp dcse on dcse.solicitacaoempenho_id = sol.id " +
            " inner join propostaconcessaodiaria dia on dcse.propostaconcessaodiaria_id = dia.id " +
            " where dia.id = :idDiaria and sol.empenho_id is null ";
        Query q = em.createNativeQuery(sql, SolicitacaoEmpenho.class);
        q.setParameter("idDiaria", diaria.getId());
        q.setMaxResults(1);
        List<SolicitacaoEmpenho> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return null;
    }

    public SolicitacaoEmpenho recuperarSolicitacaoEmpenhoPorEmpenho(Empenho empenho) {
        String sql = " select sol.*            " +
            " from solicitacaoempenho sol      " +
            " where sol.EMPENHO_ID = :idEmpenho ";
        Query q = em.createNativeQuery(sql, SolicitacaoEmpenho.class);
        q.setParameter("idEmpenho", empenho.getId());
        q.setMaxResults(1);
        try {
            return (SolicitacaoEmpenho) q.getSingleResult();
        } catch (NoResultException ne) {
            return null;
        }
    }

    public void removerSolicitacaoEmpenho(SolicitacaoEmpenho solicitacaoEmpenho) {
        String sql = " delete from diariacivilsolicemp dia                 " +
            " where dia.solicitacaoempenho_id  = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", solicitacaoEmpenho.getId());
        q.executeUpdate();

        sql = "  delete from solicitacaoempenho sol " +
            "    where sol.id = :idSolicitacao  ";
        q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", solicitacaoEmpenho.getId());
        q.executeUpdate();
    }

    public BigDecimal buscarValorReservadorPorFonteDespesaOrc(FonteDespesaORC fonteDespesaORC, Date dataInicial, Date dataFinal) {
        String sql = "select sum(valor) from (" +
            "select coalesce(sum(sol.valor), 0) as valor" +
            " from solicitacaoempenho sol" +
            " inner join despesaorc desp on sol.despesaorc_id = desp.id" +
            " inner join fontedespesaorc fontdesp on sol.fontedespesaorc_id = fontdesp.id" +
            " inner join provisaoppafonte pro on fontdesp.provisaoppafonte_id = pro.id" +
            " inner join contadedestinacao cd on pro.destinacaoderecursos_id = cd.id" +
            " inner join fontederecursos fr on cd.fontederecursos_id = fr.id" +
            " where trunc(sol.DATASOLICITACAO) between to_date(:dataInicial,'dd/MM/yyyy') and to_date(:dataFinal,'dd/MM/yyyy')" +
            " and sol.GERARRESERVA = 1 " +
            " and sol.EMPENHO_ID is null" +
            " and fontdesp.id = :fontedespesaorc" +
            " and sol.contrato_id is null" +
            "" +
            " union all" +
            "" +
            "" +
            " SELECT coalesce(sum(s.valor), 0)  as valor " +
            "  FROM ALTERACAOORC ALT  " +
            " inner join anulacaoorc s on alt.id = s.alteracaoorc_id  " +
            " inner join FONTEDESPESAORC fontdesp on s.FONTEDESPESAORC_ID = fontdesp.id  " +
            "  WHERE trunc(alt.DATAALTERACAO) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy')  " +
            "  and alt.status = :em_analise " +
            "  and fontdesp.id = :fontedespesaorc " +
            "" +
            " union all " +
            "" +
            " select coalesce(sum(res.valor), 0) as valor" +
            "  from ReservaDotacao res " +
            " INNER JOIN FONTEDESPESAORC fontdesp ON res.FONTEDESPESAORC_ID = fontdesp.ID  " +
            " where trunc(res.dataReserva) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy')" +
            " and fontdesp.id = :fontedespesaorc" +
            " and res.origemReservaFonte <> :origem" +
            "" +
            " union all " +
            "" +
            " select coalesce(sum(sol.valor), 0)  * -1 as valor " +
            "  from CancelamentoReservaDotacao crd  " +
            " inner join SOLCANCELARESERVADOTACAO sol on crd.solicitacao_id = sol.id  " +
            " INNER JOIN FONTEDESPESAORC fontdesp ON sol.FONTEDESPESAORC_ID = fontdesp.ID   " +
            " where trunc(crd.data) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy')" +
            " and fontdesp.id = :fontedespesaorc" +
            " and sol.origemReservaFonte <> :origem" +
            " )";
        Query q = em.createNativeQuery(sql);
        q.setParameter("fontedespesaorc", fonteDespesaORC.getId());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        q.setParameter("origem", OrigemReservaFonte.LICITACAO.name());
        q.setParameter("em_analise", StatusAlteracaoORC.EM_ANALISE.name());
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal buscarValorReservadorPorLicitacaoPorFonteDespesaOrc(FonteDespesaORC fonteDespesaORC, Date dataInicial, Date dataFinal) {
        String sqlReservadoPorLicitacaoNormal = QueryReprocessamentoService.getService().getReprocessamentoOrcamentarioReservadoPorLicitacaoNormal();
        String sqlReservadoPorLicitacaoEstorno = QueryReprocessamentoService.getService().getReprocessamentoOrcamentarioReservadoPorLicitacaoEstorno();
        UnidadeOrganizacional unidadeOrganizacional = fonteDespesaORC.getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional();

        sqlReservadoPorLicitacaoNormal = sqlReservadoPorLicitacaoNormal.replace("$UnidadeOrganizacional", " and prov.unidadeorganizacional_id = :unidadeOrganizacional ");
        sqlReservadoPorLicitacaoNormal = sqlReservadoPorLicitacaoNormal.replace("$DespesaOrc", " and desp.id = :despesaOrc ");
        sqlReservadoPorLicitacaoNormal = sqlReservadoPorLicitacaoNormal.replace("$FonteDesp", "  and fontdesp.id = :fonteDesp");

        sqlReservadoPorLicitacaoEstorno = sqlReservadoPorLicitacaoEstorno.replace("$UnidadeOrganizacional", " and prov.unidadeorganizacional_id = :unidadeOrganizacional ");
        sqlReservadoPorLicitacaoEstorno = sqlReservadoPorLicitacaoEstorno.replace("$DespesaOrc", " and desp.id = :despesaOrc ");
        sqlReservadoPorLicitacaoEstorno = sqlReservadoPorLicitacaoEstorno.replace("$FonteDesp", "  and fontdesp.id = :fonteDesp");

        BigDecimal retorno = BigDecimal.ZERO;
        String sqlReservadoNormal = "select coalesce(sum(dados.reservadoPorLicitacao), 0) as valor, tipo_lancamento from ("
            + sqlReservadoPorLicitacaoNormal
            + " union all "
            + sqlReservadoPorLicitacaoEstorno
            + " ) dados group by tipo_lancamento ";
        Query q = em.createNativeQuery(sqlReservadoNormal);
        q.setParameter("fonteDesp", fonteDespesaORC.getId());
        q.setParameter("despesaOrc", fonteDespesaORC.getDespesaORC().getId());
        q.setParameter("unidadeOrganizacional", unidadeOrganizacional.getId());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        q.setParameter("EXERCICIO_ID", fonteDespesaORC.getDespesaORC().getExercicio().getId());

        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            for (Object o : resultList) {
                Object[] obj = (Object[]) o;
                if (TipoLancamento.NORMAL.name().equals(obj[1])) {
                    retorno = retorno.add((BigDecimal) obj[0]);
                }
                if (TipoLancamento.ESTORNO.name().equals(obj[1])) {
                    retorno = retorno.subtract((BigDecimal) obj[0]);
                }
            }
        }
        return retorno;
    }
}
