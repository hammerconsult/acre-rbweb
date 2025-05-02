/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ProcessoFiscalizacao;
import br.com.webpublico.enums.StatusProcessoFiscalizacao;
import br.com.webpublico.util.Util;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author claudio
 */
@Stateless
public class EncerramentoProcessoFiscalizacaoFacade extends AbstractFacade<ProcessoFiscalizacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EncerramentoProcessoFiscalizacaoFacade() {
        super(ProcessoFiscalizacao.class);
    }


    public List<ProcessoFiscalizacao> listaProcessosPagos() {
        String sql = "SELECT pf.* FROM processofiscalizacao pf "
                + " INNER JOIN situacaoProcessoFiscal spf ON spf.processofiscalizacao_id = pf.id "
                + " WHERE spf.processoFiscalizacao_id = pf.id AND spf.id = "
                + " (SELECT max(sit.id) FROM SituacaoProcessoFiscal sit WHERE sit.processoFiscalizacao_id = pf.id) "
                + " AND spf.statusprocessofiscalizacao = 'PAGO' ";
        Query q = em.createNativeQuery(sql, ProcessoFiscalizacao.class);
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<ProcessoFiscalizacao>();
        }
    }

    public List<ProcessoFiscalizacao> filtrar(ProcessoFiscalizacao filtroProcessoInicial, ProcessoFiscalizacao filtroProcessoFinal, Date filtroDataProcessoInicial, Date filtroDataProcessoFinal, Date filtroDataPagamentoInicial, Date filtroDataPagamentoFinal, StatusProcessoFiscalizacao filtroStatusProcessoFiscalizacao) {
        String sql = "select distinct pf from ProcessoFiscalizacao pf "
                + " join fetch pf.autoInfracaoFiscalizacao auto"
                + " join pf.situacoesProcessoFiscalizacao spf ";

        String juncao = " where ";

        if (filtroStatusProcessoFiscalizacao != null) {
            sql += juncao + " spf = (select max(sit) from SituacaoProcessoFiscal sit "
                    + " where sit.processoFiscalizacao = pf) "
                    + " and spf.statusProcessoFiscalizacao = :filtroStatusProcessoFiscalizacao ";
            juncao = " and ";
        }

        if (filtroProcessoInicial != null && filtroProcessoInicial.getId() != null && filtroProcessoFinal == null) {
            sql += juncao + " pf.id >= :filtroProcessoInicial ";
            juncao = " and ";
        } else if (filtroProcessoFinal != null && filtroProcessoFinal.getId() != null && filtroProcessoInicial == null) {
            sql += juncao + " pf.id <= :filtroProcessoFinal ";
            juncao = " and ";
        } else if (filtroProcessoFinal != null && filtroProcessoFinal.getId() != null && filtroProcessoInicial != null && filtroProcessoInicial.getId() != null) {
            sql += juncao + " pf.id between :filtroProcessoInicial and :filtroProcessoFinal ";
            juncao = " and ";
        }

        if (filtroDataProcessoInicial != null && filtroDataProcessoFinal == null) {
            sql += juncao + " pf.dataCadastro >= :filtroDataProcessoInicial ";
            juncao = " and ";
        } else if (filtroDataProcessoFinal != null && filtroDataProcessoInicial == null) {
            sql += juncao + " pf.dataCadastro <= :filtroDataProcessoFinal ";
            juncao = " and ";
        } else if (filtroDataProcessoInicial != null && filtroDataProcessoFinal != null) {
            sql += juncao + " pf.dataCadastro between :filtroDataProcessoInicial and :filtroDataProcessoFinal ";
            juncao = " and ";
        }

        if (filtroDataPagamentoInicial != null) {


            sql += juncao + " pf in (select calculo.processo from CalculoProcFiscalizacao calculo where calculo in "
                    + " (select item.parcelaValorDivida.valorDivida.calculo from ItemLoteBaixa item where item.loteBaixa.dataPagamento >= :filtroDataPagamentoInicial)) ";
//

            juncao = " and ";
        }
        if (filtroDataPagamentoFinal != null) {
            sql += juncao + " pf in (select calculo.processo from CalculoProcFiscalizacao calculo where calculo in "
                    + " (select item.parcelaValorDivida.valorDivida.calculo from ItemLoteBaixa item where item.loteBaixa.dataPagamento <= :filtroDataPagamentoFinal)) ";
            juncao = " and ";
        }

        Query q = em.createQuery(sql);

        if (filtroStatusProcessoFiscalizacao != null) {
            q.setParameter("filtroStatusProcessoFiscalizacao", filtroStatusProcessoFiscalizacao);
        }
        if (filtroProcessoInicial != null && filtroProcessoInicial.getId() != null) {
            q.setParameter("filtroProcessoInicial", filtroProcessoInicial.getId());
        }
        if (filtroProcessoFinal != null && filtroProcessoFinal.getId() != null) {
            q.setParameter("filtroProcessoFinal", filtroProcessoFinal.getId());
        }

        if (filtroDataProcessoInicial != null) {
            q.setParameter("filtroDataProcessoInicial", filtroDataProcessoInicial);
        }
        if (filtroDataProcessoFinal != null) {
            q.setParameter("filtroDataProcessoFinal", filtroDataProcessoFinal);
        }

        if (filtroDataPagamentoInicial != null) {
            q.setParameter("filtroDataPagamentoInicial", filtroDataPagamentoInicial);
        }
        if (filtroDataPagamentoFinal != null) {
            q.setParameter("filtroDataPagamentoFinal", filtroDataPagamentoFinal);
        }

        new Util().imprimeSQL(sql, q);

        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<ProcessoFiscalizacao>();
        }
    }


    @Override
    public ProcessoFiscalizacao recuperar(Object id) {
        ProcessoFiscalizacao processoFiscalizacao = em.find(ProcessoFiscalizacao.class, id);
        processoFiscalizacao.getRecursoFiscalizacaos().size();
        processoFiscalizacao.getSituacoesProcessoFiscalizacao().size();
        processoFiscalizacao.getTermoGeralFiscalizacao().size();
        return processoFiscalizacao;

    }

}

