package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConsultaDeViagensOtt;
import br.com.webpublico.entidades.ViagemOperadoraTecnologiaTransporte;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * @author octavio
 */
@Stateless
public class ConsultaDeViagensOttFacade extends AbstractFacade<ConsultaDeViagensOtt> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ConsultaDeViagensOttFacade() {
        super(ConsultaDeViagensOtt.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ViagemOperadoraTecnologiaTransporte> buscarViagensPorOtt(ConsultaDeViagensOtt consulta) {
        String sql = "SELECT V.* FROM VIAGEMOTT V "
            + "INNER JOIN OPERADORATRANSPORTE O ON V.OPERADORATECTRANSPORTE_ID = O.ID "
            + "WHERE + V.DATACHAMADAS BETWEEN :dataInicio AND :dataFim "
            + "AND O.ID = :ottId ";

        Query q = em.createNativeQuery(sql, ViagemOperadoraTecnologiaTransporte.class);
        q.setParameter("dataInicio", consulta.getDataInicialViagem());
        q.setParameter("dataFim", consulta.getDataFinalViagem());
        q.setParameter("ottId", consulta.getOperadoraTecTransporte().getId());

        List<ViagemOperadoraTecnologiaTransporte> viagens = q.getResultList();
        if (viagens != null) {
            return viagens;
        }
        return new ArrayList<ViagemOperadoraTecnologiaTransporte>();
    }
}
