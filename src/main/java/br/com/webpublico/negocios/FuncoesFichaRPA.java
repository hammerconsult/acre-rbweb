/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.FichaRPA;
import br.com.webpublico.entidades.ItemBaseFP;
import br.com.webpublico.entidades.PrestadorServicos;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.FuncoesFolhaFacadeException;
import br.com.webpublico.interfaces.EntidadePagavelRH;
import br.com.webpublico.util.anotacoes.DescricaoMetodo;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static br.com.webpublico.util.DataUtil.localDateToDate;

/**
 * @author peixe
 */
@Stateless
public class FuncoesFichaRPA extends AbstractFacade<FichaRPA> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FuncoesFichaRPA() {
        super(FichaRPA.class);
    }

    @DescricaoMetodo("buscarValorBaseOutrosVinculos('codigoBase', ep, mes, ano, fichaRPA)")
    public double buscarValorBaseOutrosVinculos(String codigoBase, EntidadePagavelRH ep, Integer mes, Integer ano, FichaRPA ficha) throws Exception { //Para ser chamado do JavaScript
        boolean existeFicha = ficha != null && ficha.getId() != null;
        String sql =
        " select sum(case when(itemBase.operacaoformula = 'ADICAO') then itemficha.valor else -itemficha.valor end)" +
        "  from itemficharpa itemficha " +
        "  join ficharpa ficha on ficha.id = itemficha.ficharpa_id " +
        "  join prestadorservicos vinculo on vinculo.id = ficha.prestadorservicos_id " +
        "  join itembasefp itembase on itembase.eventofp_id = itemficha.eventofp_id " +
        "  join basefp base on base.id = itembase.basefp_id " +
        " where extract(month from ficha.dataservico) = :mes " +
        "  and extract(year from ficha.dataservico) = :ano " +
        "  and base.codigo = :codigo " +
        "  and vinculo.prestador_id = :pessoaId ";
        sql += existeFicha ? " and ficha.id <> :fichaId " : "";

        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        q.setParameter("codigo", codigoBase);
        q.setParameter("pessoaId", ((PrestadorServicos) ep).getPrestador().getId());

        if (existeFicha) {
            q.setParameter("fichaId", ficha.getId());
        }

        Object result = q.getSingleResult();

        if (result == null) {
            return 0.0;
        }

        BigDecimal d = (BigDecimal) result;
        return d.doubleValue();
    }

    @DescricaoMetodo("valorTotalEventoOutrosVinculos('codigoEventoFP', ep, mes, ano)")
    public Double valorTotalEventoOutrosVinculos(String codigo, EntidadePagavelRH ep, Integer mes, Integer ano) {
        if (ep.getPrimeiroContrato()) {
            return 0.0;
        }
        String sql =
        " select sum(coalesce(itemFicha.valor, 0)) " +
        "  from itemficharpa itemficha" +
        "  inner join eventofp evento on evento.id = itemficha.eventofp_id" +
        "  inner join ficharpa ficha on ficha.id = itemficha.ficharpa_id" +
        "  inner join prestadorservicos vinculo on vinculo.id = ficha.prestadorservicos_id " +
        " where extract(month from ficha.dataservico) = :mes " +
        "  and extract(year from ficha.dataservico) = :ano " +
        "  and evento.codigo = :evento " +
        "  and vinculo.prestador_id = :pessoaId ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        q.setParameter("evento", codigo);
        q.setParameter("pessoaId", ((PrestadorServicos) ep).getPrestador().getId());
        Object result = q.getSingleResult();

        if (result == null) {
            return 0.0;
        }

        BigDecimal d = (BigDecimal) result;
        return d.doubleValue();
    }

    public List<ItemBaseFP> buscarItensBase(String codigoBase) {
        List<ItemBaseFP> obterItens = Lists.newArrayList();
        List<ItemBaseFP> itemBaseFPS = obterItensBaseFP(codigoBase);
        obterItens = Lists.newArrayList(itemBaseFPS);
        return obterItens;
    }

    public Integer recuperaCategoriaeSocial(EntidadePagavelRH ep) {
        String sql = "select cat.codigo " +
            " from PrestadorServicos prest  " +
            "       inner join prest.categoriaTrabalhador cat  " +
            " where prest.id = :parametroEP";
        Query q = em.createQuery(sql);
        q.setParameter("parametroEP", ep.getId());
        try {
            if (!q.getResultList().isEmpty()) {
                Integer valor = (Integer) q.getSingleResult();
                return valor;
            }
            return null;
        } catch (NoResultException nr) {
            throw new FuncoesFolhaFacadeException("Não foi possível encontrar a categoria do e-social para " + ep, nr);
        } catch (RuntimeException nr) {
            throw new FuncoesFolhaFacadeException("Erro inesperado ao executar recuperaCategoriaeSocial" + ep, nr);
        }
    }

    public List<ItemBaseFP> obterItensBaseFP(String codigo) {
        String hql = " select item from ItemBaseFP item "
            + " inner join item.baseFP base "
            + " inner join fetch item.eventoFP e "
            + " where lower(base.codigo) = :codigo order by e.ordemProcessamento";
        List<ItemBaseFP> obterItens = Lists.newArrayList();
        try {
            Query q = em.createQuery(hql.toString());
            q.setParameter("codigo", codigo.toLowerCase().trim());
            obterItens = q.getResultList();
        } catch (RuntimeException re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar o método obterItensBaseFP", re);
        }
        return obterItens;
    }

    public EventoFP recuperaEventoPorCodigo(String codigo) {
        Query q = em.createQuery("select e from EventoFP e where e.codigo = :codigo");
        q.setParameter("codigo", codigo);
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        EventoFP e = (EventoFP) resultList.get(0);
        Hibernate.initialize(e.getTiposFolha());
        return e;
    }

    @DescricaoMetodo("obterNumeroDependentes(String [codigo], ep)")
    public int obterNumeroDependentes(String codigo, EntidadePagavelRH ep) {
        String sql = " SELECT COUNT(DEPENDENTESVINCULOFP.ID) FROM DEPENDENTE DEPENDENTE  "
            + "INNER JOIN PESSOAFISICA DEPENDENTEFISICA ON DEPENDENTE.DEPENDENTE_ID = DEPENDENTEFISICA.ID  "
            + "INNER JOIN DEPENDENTEVINCULOFP DEPENDENTESVINCULOFP ON DEPENDENTE.ID = DEPENDENTESVINCULOFP.DEPENDENTE_ID "
            + "INNER JOIN TIPODEPENDENTE TIPODEPENDENTE ON DEPENDENTESVINCULOFP.TIPODEPENDENTE_ID = TIPODEPENDENTE.ID "
            + "WHERE dependente.responsavel_id = :responsavel  "
            + "AND TIPODEPENDENTE.CODIGO = :codigo "
            + "  and to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between DEPENDENTESVINCULOFP.INICIOVIGENCIA AND coalesce(DEPENDENTESVINCULOFP.FINALVIGENCIA,to_date(to_char(:data,'mm/yyyy'),'mm/yyyy'))"
            + "AND  FLOOR (FLOOR (MONTHS_BETWEEN (:data, DEPENDENTEFISICA.DATANASCIMENTO)) / 12) <= TIPODEPENDENTE.IDADEMAXIMA ";


        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", codigo);
        q.setParameter("responsavel", ep.getMatriculaFP().getPessoa().getId());
        q.setParameter("data", localDateToDate(getDataReferenciaDateTime(ep)));
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return 0;
        }
        return ((BigDecimal) resultList.get(0)).intValue();

    }

    public LocalDate getDataReferenciaDateTime(EntidadePagavelRH ep) {
        LocalDate dataReferencia = LocalDate.now();
        dataReferencia = dataReferencia.withYear(ep.getAno());
        dataReferencia = dataReferencia.withMonth(Mes.getMesToInt(ep.getMes()).getNumeroMes());
        dataReferencia = dataReferencia.withDayOfMonth(dataReferencia.lengthOfMonth());
        return dataReferencia;
    }
}
