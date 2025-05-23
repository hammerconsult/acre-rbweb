package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.enums.TipoCalculoRBTRans;
import br.com.webpublico.enums.TipoDamRbtrans;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Filipe
 * On 14, Maio, 2019,
 * At 08:49
 */

@Stateless
public class ConsultaDamRBTransFacade {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ConsultaDamRBTransFacade() {
    }

    protected EntityManager getEntityManager() {
        return em;
    }

    public LinkedList<ParcelaValorDivida> buscarDamsRBTrans(Exercicio anoDebito, TipoDamRbtrans tipoDamRbtrans, TipoPermissaoRBTrans tipo, Integer digitoInicial, Integer digitoFinal) {
        String sql =
            " select pvd.id " +
                " from PARCELAVALORDIVIDA pvd " +
                " INNER JOIN valordivida vd on vd.id = pvd.VALORDIVIDA_ID " +
                " inner join SITUACAOPARCELAVALORDIVIDA spvd on spvd.id = pvd.SITUACAOATUAL_ID " +
                " INNER JOIN calculo cal ON cal.id = vd.calculo_id " +
                " INNER JOIN calculopessoa cp on cp.calculo_id = cal.id " +
                " INNER JOIN CADASTROECONOMICO ce ON ce.id = cal.CADASTRO_ID " +
                " INNER JOIN permissionario per ON per.CADASTROECONOMICO_ID = ce.id " +
                " INNER JOIN PERMISSAOTRANSPORTE pt ON pt.id = per.PERMISSAOTRANSPORTE_ID " +
                " INNER JOIN exercicio ex ON ex.id = vd.EXERCICIO_ID " +
                " INNER JOIN pessoa p ON cp.PESSOA_ID = p.id " +
                " INNER JOIN PESSOAFISICA pf ON ce.PESSOA_ID              = pf.ID " +
                " where spvd.SITUACAOPARCELA = 'EM_ABERTO' " +
                "  and pt.TIPOPERMISSAORBTRANS in (:tipo) " +
                "  and ex.ano = :anoDebito " +
                ((digitoInicial != null) ?
                    "  AND pt.NUMERO  >= :digitoInicial  " : "") +
                ((digitoFinal != null) ?
                    "  AND pt.NUMERO  <= :digitoFinal " : "") +
                ((tipoDamRbtrans == null) ?
                    " and cal.TIPOCALCULO in (:selecionaCalculo) " +
                        "  and (exists (select ivd.id from ItemValorDivida ivd where ivd.VALORDIVIDA_ID = vd.ID " +
                        "               and ivd.TRIBUTO_ID in (select distinct tributo_id from TAXATRANSITO " +
                        "                                       where TIPOCALCULORBTRANS in (:calculoRbtrans))) " +
                        "    or cal.TIPOCALCULO = :iss) " : "") +
                ((tipoDamRbtrans != null) ?
                    ((tipoDamRbtrans.name().equals(TipoDamRbtrans.RENOVACAO_PERMISSAO.name())) ?
                        "  and (exists (select ivd.id from ItemValorDivida ivd where ivd.VALORDIVIDA_ID = vd.ID " +
                            "               and ivd.TRIBUTO_ID in (select distinct tributo_id from TAXATRANSITO " +
                            "                                       where TIPOCALCULORBTRANS = :renovacao))) " : "") +
                        ((tipoDamRbtrans.name().equals(TipoDamRbtrans.VISTORIA_VEICULO.name())) ?
                            "  and (exists (select ivd.id from ItemValorDivida ivd where ivd.VALORDIVIDA_ID = vd.ID " +
                                "               and ivd.TRIBUTO_ID in (select distinct tributo_id from TAXATRANSITO " +
                                "                                       where TIPOCALCULORBTRANS = :vistoria))) " : "") +
                        ((tipoDamRbtrans.name().equals(TipoDamRbtrans.ISS.name())) ?
                            " and cal.TIPOCALCULO = :issFixo " : "") : "") +
                " ORDER BY  TRIM(pf.NOME) asc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("anoDebito", anoDebito.getAno());
        q.setParameter("tipo", tipo.name());

        if (tipoDamRbtrans == null) {
            q.setParameter("calculoRbtrans", Lists.newArrayList(TipoCalculoRBTRans.RENOVACAO_PERMISSAO.name(), TipoCalculoRBTRans.VISTORIA_VEICULO.name()));
            q.setParameter("selecionaCalculo", Lists.newArrayList(Calculo.TipoCalculo.ISS.name(), Calculo.TipoCalculo.RB_TRANS.name()));
            q.setParameter("iss", Calculo.TipoCalculo.ISS.name());
        }
        if (tipoDamRbtrans != null) {
            if (tipoDamRbtrans.name().equals(TipoDamRbtrans.RENOVACAO_PERMISSAO.name())) {
                q.setParameter("renovacao", TipoCalculoRBTRans.RENOVACAO_PERMISSAO.name());
            }
            if (tipoDamRbtrans.name().equals(TipoDamRbtrans.VISTORIA_VEICULO.name())) {
                q.setParameter("vistoria", TipoCalculoRBTRans.VISTORIA_VEICULO.name());
            }
            if (tipoDamRbtrans.name().equals(TipoDamRbtrans.ISS.name())) {
                q.setParameter("issFixo", (Calculo.TipoCalculo.ISS.name()));
            }
        }

        if (digitoInicial != null) {
            q.setParameter("digitoInicial", digitoInicial);
        }
        if (digitoFinal != null) {
            q.setParameter("digitoFinal", digitoFinal);
        }
        q.setParameter("anoDebito", anoDebito.getAno());
        q.setParameter("iss", Calculo.TipoCalculo.ISS.name());
        LinkedList<ParcelaValorDivida> retorno = Lists.newLinkedList();
        List<BigDecimal> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            for (BigDecimal obj : resultado) {
                ParcelaValorDivida pvd = em.find(ParcelaValorDivida.class, ((Number) obj).longValue());
                Hibernate.initialize(pvd.getItensParcelaValorDivida());
                retorno.add(pvd);
            }
        }
        return retorno;
    }
}
