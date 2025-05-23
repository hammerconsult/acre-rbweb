/*
 * Codigo gerado automaticamente em Wed Jun 27 14:28:00 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.ReprocessamentoContabil;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class EventoContabilFacade extends AbstractFacade<EventoContabil> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HistoricoContabilFacade historicoContabilFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private CLPFacade clpFacade;
    @EJB
    private ClpHistoricoContabilFacade clpHistoricoContabilFacade;
    @EJB
    private SaldoContaContabilFacade saldoContaContabilFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;

    public EventoContabilFacade() {
        super(EventoContabil.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CLPFacade getClpFacade() {
        return clpFacade;
    }

    public ClpHistoricoContabilFacade getClpHistoricoContabilFacade() {
        return clpHistoricoContabilFacade;
    }

    public HistoricoContabilFacade getHistoricoContabilFacade() {
        return historicoContabilFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public void setFonteDeRecursosFacade(FonteDeRecursosFacade fonteDeRecursosFacade) {
        this.fonteDeRecursosFacade = fonteDeRecursosFacade;
    }

    @Override
    public EventoContabil recuperar(Object id) {
        EventoContabil ec = em.find(EventoContabil.class, id);
        Hibernate.initialize(ec.getItemEventoCLPs());
        for (ItemEventoCLP itemEventoCLP : ec.getItemEventoCLPs()) {
            Hibernate.initialize(itemEventoCLP.getClp().getLcps());
        }
        return ec;
    }

    public List<EventoContabil> listaEventoContabilPorTipoEvento(String parte, TipoEventoContabil tec) {
        String hql = "from EventoContabil where (lower (descricao) like :parte) and " + (tec != null ? " tipoEventoContabil = :param " : " tipoEventoContabil is not null ");
        Query q = em.createQuery(hql);
        if (tec != null) {
            q.setParameter("param", tec);
        }
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        List lista = q.getResultList();
        if (lista.isEmpty()) {
            return new ArrayList<>();
        } else {
            return lista;
        }
    }

    public List<EventoContabil> buscarEventosContabeisPorTipoEvento(String parte, TipoEventoContabil tec) {
        String hql = "select eve.* from EventoContabil eve where eve.tipoEventoContabil = :param " +
            " and (lower(eve.descricao) like :parte or eve.codigo like :parte) ";
        Query q = em.createNativeQuery(hql, EventoContabil.class);
        q.setParameter("param", tec.name());
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(30);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<EventoContabil> buscarEventosContabeisPorTiposEvento(String parte, List<String> tiposEventoContabil) {
        String hql = "select eve.* from EventoContabil eve where eve.tipoEventoContabil in (:tipos) " +
            " and (lower(eve.descricao) like :parte or eve.codigo like :parte) ";
        Query q = em.createNativeQuery(hql, EventoContabil.class);
        q.setParameter("tipos", tiposEventoContabil);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<EventoContabil> listaFiltrandoPorTipoEventoTipoLancamento(String parte, TipoEventoContabil tipoEventoContabil, TipoLancamento tipoLancamento) {
        String sql = "SELECT EC.* "
            + " FROM EVENTOCONTABIL EC"
            + " WHERE ((LOWER (EC.CODIGO) LIKE :parte) OR (LOWER (EC.DESCRICAO) LIKE :parte))"
            + " AND EC.TIPOLANCAMENTO = :tipoLancamento"
            + " AND EC.TIPOEVENTOCONTABIL = :tec"
            + " ORDER BY EC.CODIGO";
        Query q = em.createNativeQuery(sql, EventoContabil.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("tec", tipoEventoContabil.name());
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setMaxResults(10);
        List lista = q.getResultList();
        if (lista.isEmpty()) {
            return new ArrayList<>();
        } else {
            return lista;
        }
    }

    public List<EventoContabil> buscarFiltrandoPorTipoEventoAndTipoLancamento(String parte,
                                                                              TipoEventoContabil tipoEventoContabil,
                                                                              TipoLancamento tipoLancamento,
                                                                              Integer maxResult) {
        String sql = " SELECT EC.* "
            + " FROM EVENTOCONTABIL EC "
            + " WHERE ((LOWER (EC.CODIGO) LIKE :parte) OR (LOWER (EC.DESCRICAO) LIKE :parte)) "
            + " AND EC.TIPOLANCAMENTO = :tipoLancamento "
            + " AND EC.TIPOEVENTOCONTABIL = :tec "
            + " ORDER BY EC.CODIGO ";
        Query q = em.createNativeQuery(sql, EventoContabil.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("tec", tipoEventoContabil.name());
        q.setParameter("tipoLancamento", tipoLancamento.name());
        if (maxResult != null) {
            q.setMaxResults(maxResult);
        }
        List lista = q.getResultList();
        if (lista.isEmpty()) {
            return new ArrayList<EventoContabil>();
        }
        return lista;
    }

    @Override
    public List<EventoContabil> lista() {
        Query q = getEntityManager().createQuery("from EventoContabil e order by e.codigo");
        return q.getResultList();
    }

    public List<EventoContabil> buscarEventosContabeisPorCodigoOrDescricao(String parte) {
        String sql = "SELECT EC.* "
            + " FROM EVENTOCONTABIL EC"
            + " WHERE (LOWER (EC.CODIGO) LIKE :parte) OR (LOWER (EC.DESCRICAO) LIKE :parte)"
            + " ORDER BY EC.CODIGO";
        Query q = em.createNativeQuery(sql, EventoContabil.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<LancamentoContabil> consultaLancamentosContabilPorEvento(EventoContabil er, Date data) {
        String sql = "SELECT lc.* FROM lancamentocontabil lc "
            + "INNER JOIN lcp lcp ON lcp.id = lc.lcp_id "
            + "INNER JOIN clp clp ON clp.id = lcp.clp_id "
            + "INNER JOIN itemeventoclp ie ON ie.clp_id = clp.id "
            + "INNER JOIN eventocontabil e ON e.id = ie.eventocontabil_id "
            + "WHERE e.id = :evento "
            + " AND trunc(lc.dataLancamento) >= to_date(:data, 'dd/mm/yyyy') ";
        Query q = em.createNativeQuery(sql, LancamentoContabil.class);
        q.setParameter("evento", er.getId());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Este Evento Contábil não pode ser encerrado, pois existem lançamentos contábeis vinculadas a ele!");
        }
        return resultList;
    }

    public List<LancamentoContabil> consultaLancamentosContabilPorEventoECLP(EventoContabil er, CLP clp, Date data) {
        String sql = "SELECT lanc.* FROM lancamentocontabil lanc "
            + " INNER JOIN itemparametroevento item ON item.id=lanc.itemparametroevento_id"
            + " INNER JOIN parametroevento paramet ON paramet.id=item.parametroevento_id "
            + " INNER JOIN eventocontabil e ON e.id = paramet.eventocontabil_id AND e.id= :evento"
            + " INNER JOIN lcp lcp ON lcp.id = lanc.lcp_id"
            + " INNER JOIN clp clp ON clp.id = lcp.clp_id"
            + " WHERE clp.id = :clp "
            + " AND trunc(lanc.dataLancamento) >= to_date(:data, 'dd/mm/yyyy') ";
        Query q = em.createNativeQuery(sql, LancamentoContabil.class);
        q.setParameter("evento", er.getId());
        q.setParameter("clp", clp.getId());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Esta CLP não pode ser excluída, pois existem lançamentos contábeis vinculadas a ela e ao Evento Contabil!");
        }
        return resultList;
    }

    public List<Object[]> recuperaPeriodoReprocessamento(EventoContabil ev) {

        String sql = " SELECT "
            + " MIN(lc.datalancamento) KEEP (DENSE_RANK FIRST ORDER BY lc.datalancamento) AS menor_data, "
            + " max(lc.datalancamento) KEEP (DENSE_RANK LAST ORDER BY lc.datalancamento) AS maior_data "
            + " FROM lancamentocontabil lc "
            + "  INNER JOIN itemparametroevento item ON item.id= lc.itemparametroevento_id"
            + "  INNER JOIN parametroevento paramet ON paramet.id=item.parametroevento_id "
            + " INNER JOIN EVENTOCONTABIL E ON E.ID = paramet.EVENTOCONTABIL_ID "
            + " WHERE e.id = :evento";
        Query q = em.createNativeQuery(sql);
        q.setParameter("evento", ev.getId());
        List<Object[]> resultList = q.getResultList();

        return resultList;
    }

    public void geraEventosReprocessar(EventoContabil eventoSelecionado, Long idOrigem, String classeOrigem) {
        List<Object[]> lista = recuperaPeriodoReprocessamento(eventoSelecionado);

        for (Object[] object : lista) {
            if (object[0] != null && object[1] != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
                //System.out.println("Encontrou um periodoo no lancamento contabill...");
                try {

                    Date dataInicial = getDateFormatada(object[0]);
                    Date dataFim = getDateFormatada(object[1]);

                    EventosReprocessar eventosReprocessar = new EventosReprocessar();
                    eventosReprocessar.setDataInicial(DataUtil.retornaMenorData(dataInicial, dataFim));
                    eventosReprocessar.setDataFinal(DataUtil.retornaMaiorData(dataInicial, dataFim));
                    eventosReprocessar.setDataRegistro(new Date());
                    eventosReprocessar.setEventoContabil(eventoSelecionado);
                    eventosReprocessar.setClasseOrigem(classeOrigem);
                    eventosReprocessar.setIdOrigem(idOrigem);
                    eventosReprocessar.getEventoReprocessarUOs().add(new EventoReprocessarUO(eventosReprocessar, ((SistemaControlador) Util.getControladorPeloNome("sistemaControlador")).getUnidadeOrganizacionalOrcamentoCorrente()));
                    eventosReprocessar.setStatusEventoReprocessar(StatusEventoReprocessar.PENDENTE);
                    //Possivel verificação se o evento ja não existe para ser reprocessado no mesmo periodo....
                    salvarEventosReprocessar(eventosReprocessar);
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private Date getDateFormatada(Object o) throws ParseException {
        Date dataInicial;
        if (o == null) {
            return new Date();
        }
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Date date0 = inputFormat.parse(o.toString());
        String data0 = outputFormat.format(date0);
        dataInicial = outputFormat.parse(data0);
        return dataInicial;
    }

    public void excluirEvento(EventoContabil eve) throws Exception {
        eve = em.find(EventoContabil.class, eve.getId());
        em.remove(eve);
    }

    public void salvarEventosReprocessar(EventosReprocessar eventosReprocessar) {
        em.persist(eventosReprocessar);
    }

    public void meuSalvar(EventoContabil eventoSelecionado, EventoContabil eventoNaoAlterado) throws RuntimeException {
        verificaAlteracoesDoEvento(eventoSelecionado, eventoNaoAlterado);
        if (eventoSelecionado.getId() == null) {
            salvarNovo(eventoSelecionado);
        } else {
            salvar(eventoSelecionado);
        }
    }

    private void verificaAlteracoesDoEvento(EventoContabil eventoSelecionado, EventoContabil eventoNaoAlterado) throws RuntimeException {
        if (eventoSelecionado.getId() == null) {
            return;
        }
        boolean alteroEvento = false;
        try {
            if (!eventoNaoAlterado.getTipoEventoContabil().equals(eventoSelecionado.getTipoEventoContabil())) {
                alteroEvento = true;
            }
            if (!eventoNaoAlterado.getTipoLancamento().equals(eventoSelecionado.getTipoLancamento())) {
                alteroEvento = true;
            }
            if (!eventoNaoAlterado.getInicioVigencia().equals(eventoSelecionado.getInicioVigencia())) {
                alteroEvento = true;
            }
            if (eventoNaoAlterado.getClpHistoricoContabil() != null && eventoSelecionado.getClpHistoricoContabil() != null) {
                if (!eventoNaoAlterado.getClpHistoricoContabil().equals(eventoSelecionado.getClpHistoricoContabil())) {
                    alteroEvento = true;
                }
            }
            if (!eventoNaoAlterado.getItemEventoCLPs().equals(eventoSelecionado.getItemEventoCLPs())) {
                alteroEvento = true;
            }
            if (alteroEvento) {
                geraEventosReprocessar(eventoSelecionado, eventoSelecionado.getId(), EventoContabil.class.getSimpleName());
            }
        } catch (Exception e) {
//            throw new RuntimeException(e.getMessage());
        }
    }

    public void validaCodigoNomeVigenciaMesmoData(EventoContabil eventoContabil) {

        String sql = "SELECT * FROM EventoContabil "
            + "WHERE codigo= :codigo "
            + "AND descricao = :descricao "
            + "AND to_date(:data, 'dd/mm/yyyy') between trunc(iniciovigencia) and coalesce(trunc(fimvigencia), to_date(:data, 'dd/mm/yyyy')) ";
        Query q = em.createNativeQuery(sql, EventoContabil.class);
        q.setParameter("codigo", eventoContabil.getCodigo().trim());
        q.setParameter("descricao", eventoContabil.getDescricao().trim());
        q.setParameter("data", DataUtil.getDataFormatada(UtilRH.getDataOperacao()));
        q.setMaxResults(1);
        try {
            List<EventoContabil> lista = q.getResultList();
            if (!lista.isEmpty()) {
                if (eventoContabil.getId() != null) {
                    if (!lista.get(0).equals(eventoContabil)) {
                        throw new ExcecaoNegocioGenerica("Possui um Evento Contábil vigente cadastrado com o mesmo código, descrição e a data.");
                    }
                } else {
                    throw new ExcecaoNegocioGenerica("Possui um Evento Contábil vigente cadastrado com o mesmo código, descrição e a data.");
                }
            }
        } catch (NoResultException e) {
        }
    }

    public EventoContabil recuperarEventoPorCodigo(String codigo) {
        String hql = "from EventoContabil where codigo = :codigo order by id desc";
        Query q = em.createQuery(hql);
        q.setParameter("codigo", codigo);
        q.setMaxResults(1);
        try {
            return (EventoContabil) q.getSingleResult();
        } catch (NoResultException e) {
            throw new ExcecaoNegocioGenerica("Nenhum Evento encontrado com o código : " + codigo);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public void deletaLancamentosContabil(EventosReprocessar er, Boolean isReprocessamentoInicial) {
        try {
            Query q = getQuery(er);
            adicionaCondicaoQuery(er, q);

            List<LancamentoContabil> lista = (List<LancamentoContabil>) q.getResultList();

            if (lista != null) {
                reprocessamentoLancamentoContabilSingleton.adicionarLog("<b>Excluindo " + lista.size() + " Lançamentos contábeis do evento " + reprocessamentoLancamentoContabilSingleton.montarStringReprocessamentoEventoContabil(er) + ".... </b> ");
                for (LancamentoContabil lc : lista) {
                    if (!reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                        break;
                    }

                    alteraSaldos(lc);

                    Long idLc = lc.getId();
                    Long idItem = lc.getItemParametroEvento().getId();
                    Long idParametro = lc.getItemParametroEvento().getParametroEvento().getId();

                    String sqlRemover = "delete from lancamentocontabil where id = :id";
                    Query consulta = em.createNativeQuery(sqlRemover).setParameter("id", idLc);
                    consulta.executeUpdate();

//                    String sqlRemoverItem = "delete from itemparametroevento where id = :id";
//                    Query consultaItem = em.createNativeQuery(sqlRemoverItem).setParameter("id", idItem);
//                    consultaItem.executeUpdate();
//
//                    String sqlRemoverParamentroEvento = "delete from parametroevento where id = :id";
//                    Query consultaParametroEvento = em.createNativeQuery(sqlRemoverParamentroEvento).setParameter("id", idParametro);
//                    consultaParametroEvento.executeUpdate();


                }
            }
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }

    }

    private void adicionaCondicaoQuery(EventosReprocessar er, Query q) {
        if (!er.getEventoContabil().getTipoEventoContabil().equals(TipoEventoContabil.CREDITO_INICIAL)
            && !er.getEventoContabil().getTipoEventoContabil().equals(TipoEventoContabil.PREVISAO_INICIAL_RECEITA)) {

            q.setParameter("di", DataUtil.getDataFormatada(er.getDataInicial()));
            q.setParameter("df", DataUtil.getDataFormatada(er.getDataFinal()));
        } else {
            q.setParameter("exercicio", DataUtil.getAno(er.getDataInicial()));
        }
    }

    private Query getQuery(EventosReprocessar er) {
        String sql = "SELECT lc.* FROM lancamentocontabil lc "
            + " INNER JOIN itemparametroevento item ON item.id= lc.itemparametroevento_id"
            + " INNER JOIN parametroevento paramet ON paramet.id=item.parametroevento_id "
            + " INNER JOIN eventocontabil e ON e.id = paramet.eventocontabil_id "
            + " WHERE e.id = :evento ";

        if (!er.getEventoContabil().getTipoEventoContabil().equals(TipoEventoContabil.CREDITO_INICIAL)
            && !er.getEventoContabil().getTipoEventoContabil().equals(TipoEventoContabil.PREVISAO_INICIAL_RECEITA)) {

            sql += " AND trunc(lc.dataLancamento) BETWEEN to_date(:di,'dd/MM/yyyy') AND to_date(:df,'dd/MM/yyyy')";
        } else {
            sql += " AND extract (year from lc.dataLancamento) = :exercicio";
        }


        Query q = em.createNativeQuery(sql, LancamentoContabil.class);
        q.setParameter("evento", er.getEventoContabil().getId());
        return q;
    }

    private void alteraSaldos(LancamentoContabil lc) {

        saldoContaContabilFacade.geraSaldoContaContabil(lc.getDataLancamento(), lc.getContaCredito(), lc.getUnidadeOrganizacional(), TipoLancamentoContabil.CREDITO, lc.getValor(), lc.getTipoBalancete(), SomaSubtraiSaldoContaContabil.SUBTRAI);
        saldoContaContabilFacade.geraSaldoContaContabil(lc.getDataLancamento(), lc.getContaDebito(), lc.getUnidadeOrganizacional(), TipoLancamentoContabil.DEBITO, lc.getValor(), lc.getTipoBalancete(), SomaSubtraiSaldoContaContabil.SUBTRAI);

        if (lc.getContaAuxiliarCredito() != null) {
            saldoContaContabilFacade.geraSaldoContaContabil(lc.getDataLancamento(), lc.getContaAuxiliarCredito(), lc.getUnidadeOrganizacional(), TipoLancamentoContabil.CREDITO, lc.getValor(), lc.getTipoBalancete(), SomaSubtraiSaldoContaContabil.SUBTRAI);

        }
        if (lc.getContaAuxiliarDebito() != null) {
            saldoContaContabilFacade.geraSaldoContaContabil(lc.getDataLancamento(), lc.getContaAuxiliarDebito(), lc.getUnidadeOrganizacional(), TipoLancamentoContabil.DEBITO, lc.getValor(), lc.getTipoBalancete(), SomaSubtraiSaldoContaContabil.SUBTRAI);
        }
    }

    public String recuperarContaContabilPorParametroTag(ParametroEvento parametroEvento, TagOCC tagOCC, Date data) {
        String sql = "select contacontabil from ( " +
            "select tag.descricao as tag, tag.entidadeocc as entidade, cc.codigo || '-' || cc.descricao as contacontabil, c.id as origem from origemcontacontabil o " +
            "inner join occconta oc on o.id = oc.id " +
            "inner join conta cc on o.contacontabil_id = cc.id " +
            "inner join tagocc tag on tag.id = o.tagocc_id " +
            "inner join conta c on oc.contaorigem_id = c.id " +
            "where " +
            "to_date(:data,'dd/MM/yyyy') between trunc(o.iniciovigencia) and coalesce(trunc(o.fimvigencia), to_date(:data,'dd/MM/yyyy')) " +
            "union all " +
            "select tag.descricao as tag, tag.entidadeocc as entidade, cc.codigo || '-' || cc.descricao as contacontabil, s.id as origem from origemcontacontabil o " +
            "inner join tagocc tag on tag.id = o.tagocc_id " +
            "inner join conta cc on o.contacontabil_id = cc.id " +
            "inner join occbanco ob on o.id = ob.id " +
            "inner join subconta s on ob.subconta_id = s.id " +
            "where " +
            "to_date(:data,'dd/MM/yyyy') between trunc(o.iniciovigencia) and coalesce(trunc(o.fimvigencia), to_date(:data,'dd/MM/yyyy')) " +
            "union all " +
            "select tag.descricao as tag, tag.entidadeocc as entidade, cc.codigo || '-' || cc.descricao as contacontabil, bg.id as origem from origemcontacontabil o " +
            "inner join tagocc tag on tag.id = o.tagocc_id " +
            "inner join conta cc on o.contacontabil_id = cc.id " +
            "inner join occgrupobem ogb on o.id = ogb.id " +
            "inner join grupobem bg on bg.id = ogb.grupobem_id " +
            "where " +
            "to_date(:data,'dd/MM/yyyy') between trunc(o.iniciovigencia) and coalesce(trunc(o.fimvigencia), to_date(:data,'dd/MM/yyyy')) " +
            "union all " +
            "select tag.descricao as tag, tag.entidadeocc as entidade, cc.codigo || '-' || cc.descricao as contacontabil, gm.id as origem from origemcontacontabil o " +
            "inner join tagocc tag on tag.id = o.tagocc_id " +
            "inner join conta cc on o.contacontabil_id = cc.id " +
            "inner join occgrupomaterial ogm on o.id = ogm.id " +
            "inner join grupomaterial gm on ogm.grupomaterial_id = gm.id " +
            "where " +
            "to_date(:data,'dd/MM/yyyy') between trunc(o.iniciovigencia) and coalesce(trunc(o.fimvigencia), to_date(:data,'dd/MM/yyyy')) " +
            "union all " +
            "select tag.descricao as tag, tag.entidadeocc as entidade, cc.codigo || '-' || cc.descricao as contacontabil, cat.id as origem from origemcontacontabil o " +
            "inner join tagocc tag on tag.id = o.tagocc_id " +
            "inner join conta cc on o.contacontabil_id = cc.id " +
            "inner join occnaturezadividapublica od on o.id = od.id " +
            "inner join categoriadividapublica cat on od.categoriadividapublica_id = cat.id " +
            "where " +
            "to_date(:data,'dd/MM/yyyy') between trunc(o.iniciovigencia) and coalesce(trunc(o.fimvigencia), to_date(:data,'dd/MM/yyyy')) " +
            "union all " +
            "select tag.descricao as tag, tag.entidadeocc as entidade, cc.codigo || '-' || cc.descricao as contacontabil, t.id as origem from origemcontacontabil o " +
            "inner join tagocc tag on tag.id = o.tagocc_id " +
            "inner join conta cc on o.contacontabil_id = cc.id " +
            "inner join occtipopassivoatuarial ot on o.id = ot.id " +
            "inner join tipopassivoatuarial t on ot.tipopassivoatuarial_id = t.id " +
            "where " +
            "to_date(:data,'dd/MM/yyyy') between trunc(o.iniciovigencia) and coalesce(trunc(o.fimvigencia), to_date(:data,'dd/MM/yyyy'))" +
            " UNION ALL " +
            "  SELECT tag.descricao AS tag, " +
            "    tag.entidadeocc    AS entidade, " +
            "    cc.codigo " +
            "    || '-' " +
            "    || cc.descricao AS contacontabil, " +
            "    t.id            AS origem " +
            "  FROM origemcontacontabil o " +
            "  INNER JOIN tagocc tag  ON tag.id = o.tagocc_id " +
            "  INNER JOIN conta cc  ON o.contacontabil_id = cc.id " +
            "  INNER JOIN OCCCLASSEPESSOA  occ on o.id = occ.id " +
            "  inner join CLASSECREDOR t on occ.CLASSEPESSOA_ID = t.id " +
            "  WHERE to_date(:data,'dd/MM/yyyy') BETWEEN trunc(o.iniciovigencia) AND COALESCE(trunc(o.fimvigencia), to_date(:data,'dd/MM/yyyy')) " +
            ") " +
            "where " +
            "tag = :tag " +
            "and entidade = :entidade " +
            "and to_char(origem) in (:origem) ";
        try {
            Query q = em.createNativeQuery(sql);
            q.setParameter("tag", tagOCC.getDescricao());
            q.setParameter("entidade", tagOCC.getEntidadeOCC().name());
            q.setParameter("origem", montarParametroOrigem(parametroEvento));
            q.setParameter("data", DataUtil.getDataFormatada(data));
            return (String) q.getSingleResult();
        } catch (Exception e) {
            return "Conta não encontrada.";
        }
    }

    private List<String> montarParametroOrigem(ParametroEvento parametroEvento) {
        List<String> ids = Lists.newArrayList();
        List<ItemParametroEvento> itensParametrosEvento = parametroEvento.getItensParametrosEvento();
        for (ItemParametroEvento itemParametroEvento : itensParametrosEvento) {
            for (ObjetoParametro objetoParametro : itemParametroEvento.getObjetoParametros()) {
                ids.add(objetoParametro.getIdObjeto());
            }
        }
        return ids;
    }

    public List<EventoContabil> buscarEventoPorTipoBalanceteAndTipoEventoContabil(ReprocessamentoContabil reprocessamentoContabil) {
        if (reprocessamentoContabil.getTipoBalancetes() == null || reprocessamentoContabil.getTipoBalancetes().isEmpty()) {
            return Lists.newArrayList();
        }
        String sql = "select ec.* from eventocontabil ec"
            + " where ec.tipoBalancete " + montarStringTipoBalancete(reprocessamentoContabil.getTipoBalancetes())
            + " and ec.tipoEventoContabil " + montarStringTipoEventoContabil(reprocessamentoContabil.getTipoEventoContabils());
        Query q = em.createNativeQuery(sql, EventoContabil.class);
        try {
            List<EventoContabil> resultList = q.getResultList();
            return resultList;
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    private String montarStringTipoBalancete(List<TipoBalancete> tipos) {
        StringBuilder retorno = new StringBuilder();
        retorno.append(" in ( ");
        String virgula = "";
        for (TipoBalancete tipo : tipos) {
            retorno.append(virgula);
            retorno.append("'");
            retorno.append(tipo.name());
            retorno.append("'");
            virgula = ",";
        }
        String toString = retorno.toString();
        return toString + ")";
    }

    private String montarStringTipoEventoContabil(List<TipoEventoContabil> tipos) {
        StringBuilder retorno = new StringBuilder();
        retorno.append(" in ( ");
        String virgula = "";
        for (TipoEventoContabil tipo : tipos) {
            retorno.append(virgula);
            retorno.append("'");
            retorno.append(tipo.name());
            retorno.append("'");
            virgula = ",";
        }
        String toString = retorno.toString();
        return toString + ")";
    }
}
