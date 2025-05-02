package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoSaldoReceitaORC;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Fabio
 */
@Stateless
public class SaldoReceitaORCFacade extends AbstractFacade<SaldoReceitaORC> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private String mensagemExcecaoValorNegativo;

    public SaldoReceitaORCFacade() {
        super(SaldoReceitaORC.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    private SaldoReceitaORC getUltimoSaldoPorDataUnidadeContaFonte(Date data, UnidadeOrganizacional unidade, Conta conta, FonteDeRecursos fonte) {
        String hql = "select saldo from SaldoReceitaORC saldo "
                + " where trunc(saldo.data) <= to_date(:data, 'dd/mm/yyyy') "
                + " and saldo.unidadeOrganizacional = :unidadeOrganizacional "
                + " and saldo.contaReceita = :contaReceita "
                + " and saldo.fonteDeRecursos = :fonte "
                + " order by saldo.data desc";
        Query q = em.createQuery(hql);
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("unidadeOrganizacional", unidade);
        q.setParameter("contaReceita", conta);
        q.setParameter("fonte", fonte);

        if (q.getResultList() != null && q.getResultList().size() > 0) {
            SaldoReceitaORC saldo = (SaldoReceitaORC) q.getResultList().get(0);
            if (!saldo.getData().equals(data)) {
                SaldoReceitaORC novoSaldo = (SaldoReceitaORC) Util.clonarObjeto(saldo);
                novoSaldo.setId(null);
                novoSaldo.setData(data);
                return novoSaldo;
            } else {
                return saldo;
            }
        }
        return null;
    }

    // + ReceitaLOA
    // - EstornoReceitaLOA
    // + AlteracaoORC (Somente 'Previsão Adicional da Receita') (Adicional +) (Anulação -)
    // - EstornoAlteracaoORC (NÃO EXISTE A ENTIDADE AINDA)
    // + LancamentoReceitaOrc
    // - ReceitaORCEstorno
    public void geraSaldo(TipoSaldoReceitaORC tipo, Date dataLancamento, UnidadeOrganizacional unidade, Conta contaReceita, FonteDeRecursos fonte, BigDecimal valor) throws ExcecaoNegocioGenerica {
        SaldoReceitaORC saldo = getUltimoSaldoPorDataUnidadeContaFonte(dataLancamento, unidade, contaReceita, fonte);
        if (saldo == null) {
            saldo = new SaldoReceitaORC();
            saldo.setContaReceita(contaReceita);
            saldo.setData(dataLancamento);
            saldo.setFonteDeRecursos(fonte);
            saldo.setUnidadeOrganizacional(unidade);
        }
        saldo = alteraValorSaldo(tipo, saldo, valor);
        salvarSaldo(saldo);
        gerarSaldosFuturosAData(tipo, dataLancamento, unidade, contaReceita, fonte, valor);
    }

    private SaldoReceitaORC alteraValorSaldo(TipoSaldoReceitaORC tipo, SaldoReceitaORC saldo, BigDecimal valor) {
        if (tipo.equals(TipoSaldoReceitaORC.RECEITALOA)) {
            saldo.setPrevisaoInicial(saldo.getPrevisaoInicial().add(valor));
        } else if (tipo.equals(TipoSaldoReceitaORC.ESTORNORECEITALOA)) {
            saldo.setPrevisaoInicial(saldo.getPrevisaoInicial().subtract(valor));
        } else if (tipo.equals(TipoSaldoReceitaORC.ALTERACAOORC_ADICIONAL)) {
            saldo.setPrevisaoAdicional(saldo.getPrecisaoAdicional().add(valor));
        } else if (tipo.equals(TipoSaldoReceitaORC.ALTERACAOORC_ANULACAO)) {
            saldo.setAnulacaoPrevisao(saldo.getAnulacaoPrevisao().add(valor));
        } else if (tipo.equals(TipoSaldoReceitaORC.ESTORNOALTERACAOORC)) {
            saldo.setAnulacaoPrevisao(saldo.getAnulacaoPrevisao().add(valor));
        } else if (tipo.equals(TipoSaldoReceitaORC.LANCAMENTORECEITAORC)) {
            saldo.setArrecadacao(saldo.getArrecadacao().add(valor));
        } else if (tipo.equals(TipoSaldoReceitaORC.RECEITAORCESTORNO)) {
            saldo.setArrecadacao(saldo.getArrecadacao().subtract(valor));
        }
        return saldo;
    }

    private void salvarSaldo(SaldoReceitaORC saldo) {
        if (saldo.getId() == null) {
            em.persist(saldo);
        } else {
            saldo = em.merge(saldo);
        }
    }

    private List<SaldoReceitaORC> recuperaSaldosFuturos(Date data, UnidadeOrganizacional unidadeOrganizacional, FonteDeRecursos fonte, Conta contaDeReceita) {
        String hql = "select saldo from SaldoReceitaORC saldo "
                + " where trunc(saldo.data) > to_date(:data, 'dd/mm/yyyy') "
                + " and saldo.unidadeOrganizacional = :unidadeOrganizacional "
                + " and saldo.contaReceita = :contaReceita "
                + " and saldo.fonteDeRecursos = :fonte "
                + " order by saldo.data desc";
        Query q = em.createQuery(hql, SaldoReceitaORC.class);
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("unidadeOrganizacional", unidadeOrganizacional);
        q.setParameter("contaReceita", contaDeReceita);
        q.setParameter("fonte", fonte);

        try {
            return (List<SaldoReceitaORC>) q.getResultList();
        } catch (Exception e) {
            return new ArrayList<SaldoReceitaORC>();
        }
    }

    private void gerarSaldosFuturosAData(TipoSaldoReceitaORC tipo, Date dataLancamento, UnidadeOrganizacional unidade, Conta contaReceita, FonteDeRecursos fonte, BigDecimal valor) {
        List<SaldoReceitaORC> recuperaSaldosFuturos = recuperaSaldosFuturos(dataLancamento, unidade, fonte, contaReceita);
        for (SaldoReceitaORC saldoReceitaORC : recuperaSaldosFuturos) {
            saldoReceitaORC = alteraValorSaldo(tipo, saldoReceitaORC, valor);
            salvarSaldo(saldoReceitaORC);
        }
    }
}
