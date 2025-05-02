package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.DAM;
import br.com.webpublico.entidades.NFSAvulsa;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Service
public class ServiceNotaAvulsa {

    private static final Logger logger = LoggerFactory.getLogger(ServiceNotaAvulsa.class);
    @PersistenceContext
    protected transient EntityManager em;
    @Autowired
    JdbcDividaAtivaDAO dividaAtivaDAO;

    @Transactional
    public void cancelarNotasVencidasNaoPagas() {
        try {
            Query nativeQuery = em.createNativeQuery("select nota.id as notaid, pvd.id as parcelaid, d.id as damid\n" +
                "   from nfsavulsa nota\n" +
                "  inner join calculonfsavulsa calculo on calculo.nfsavulsa_id = nota.id\n" +
                "  inner join valordivida vd on vd.calculo_id = calculo.id\n" +
                "  inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id\n" +
                "  inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id\n" +
                "  inner join dam d on d.id = (select max(sd.id)\n" +
                "                                 from dam sd\n" +
                "                                inner join itemdam id on id.dam_id = sd.id\n" +
                "                              where id.parcela_id = pvd.id\n" +
                "                                and sd.situacao = :aberto)\n" +
                "where spvd.situacaoparcela = :em_aberto\n" +
                "  and d.vencimento <= (select current_date - coalesce(qtdediascancelarnfsa,5)\n" +
                "                          from configuracaotributario\n" +
                "                       where rownum = 1)");
            nativeQuery.setParameter("aberto", DAM.Situacao.ABERTO.name());
            nativeQuery.setParameter("em_aberto", SituacaoParcela.EM_ABERTO.name());
            List<Object[]> resultList = nativeQuery.getResultList();
            Date hoje = new Date();
            for (int i = 0; i < resultList.size(); i++) {
                Object[] obj = resultList.get(i);
                long idNota = ((Number) obj[0]).longValue();
                long idParcela = ((Number) obj[1]).longValue();
                long idDam = ((Number) obj[2]).longValue();
                em.createNativeQuery("update nfsavulsa set situacao = :cancelada where id = :id")
                    .setParameter("id", idNota).setParameter("cancelada", NFSAvulsa.Situacao.CANCELADA.name())
                    .executeUpdate();
                em.createNativeQuery("insert into nfsavulsacancelamento (id, nfsavulsa_id, motivo, data, usuariocancelamento_id)\n" +
                        "values (hibernate_sequence.nextval, :id, 'Cancelamento automático pelo não recolhimento do imposto.', current_date, null)")
                    .setParameter("id", idNota).executeUpdate();
                dividaAtivaDAO.persistirSituacaoParcelaValorDividaComMesmaReferencia(idParcela, SituacaoParcela.CANCELAMENTO, hoje);
                em.createNativeQuery("update dam set situacao = :cancelado where id = :id")
                    .setParameter("id", idDam).setParameter("cancelado", DAM.Situacao.CANCELADO.name())
                    .executeUpdate();
            }
        } catch (Exception ex) {
            logger.error("erro ao cancelar parcelas da nota", ex);
        }
    }

}
