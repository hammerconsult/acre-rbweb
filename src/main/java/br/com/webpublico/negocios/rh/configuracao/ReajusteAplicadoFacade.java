package br.com.webpublico.negocios.rh.configuracao;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.rh.previdencia.ReajusteAplicado;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.DataUtil;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

/**
 * Created by Buzatto on 30/03/2016.
 */
@Stateless
public class ReajusteAplicadoFacade extends AbstractFacade<ReajusteAplicado> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ReajusteAplicadoFacade() {
        super(ReajusteAplicado.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReajusteAplicado buscarPorExercicioEDataReferenciaEInicioVigencia(Exercicio exercicioAplicacao, Exercicio exercicioReferencia, Date dataReferencia, Date inicioVigenciaReajustes) {
        try {
            String sql = "select ra.* " +
                "from ReajusteAplicado ra " +
                "where ra.exercicio_id = :ex " +
                "  and ra.exercicioReferencia_id = :exReferencia ";
            if (dataReferencia != null) {
                sql += "  and trunc(ra.dataReferencia) = to_date(:dataReferencia, 'dd/MM/yyyy') ";
            }
            if (inicioVigenciaReajustes != null) {
                sql += "  and trunc(ra.iniciovigenciareajustes) = to_date(:inicioVigenciaReajustes, 'dd/MM/yyyy') ";
            }
            Query q = em.createNativeQuery(sql, ReajusteAplicado.class);
            q.setParameter("ex", exercicioAplicacao);
            q.setParameter("exReferencia", exercicioReferencia);
            if (dataReferencia != null) {
                q.setParameter("dataReferencia", DataUtil.getDataFormatada(dataReferencia));
            }
            if (inicioVigenciaReajustes != null) {
                q.setParameter("inicioVigenciaReajustes", DataUtil.getDataFormatada(inicioVigenciaReajustes));
            }
            return (ReajusteAplicado) q.getSingleResult();
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Nenhum Reajuste Aplicado encontrado para o exercício de aplicação " + exercicioAplicacao.getAno() + " e exercício de referência " + exercicioReferencia.getAno());
        }
    }
}
