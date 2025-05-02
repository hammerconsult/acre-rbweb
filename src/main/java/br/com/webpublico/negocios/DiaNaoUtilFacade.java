/*
 * Codigo gerado automaticamente em Thu Sep 15 16:00:55 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DiaNaoUtil;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Stateless
public class DiaNaoUtilFacade extends AbstractFacade<DiaNaoUtil> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DiaNaoUtilFacade() {
        super(DiaNaoUtil.class);
    }

    private String formataDataDDMMYYYY(Date data) {
        SimpleDateFormat sp = new SimpleDateFormat("dd/MM/yyyy");
        return sp.format(data);
    }

    public boolean verificaDiaNaoUtil(Date data) {

        Calendar calendario = Calendar.getInstance();
        calendario.setTime(data);
        int diaSemana = calendario.get(Calendar.DAY_OF_WEEK);

        if ((diaSemana == 1) || (diaSemana == 7)) {
            return false;
        } else {
            String hql = "from DiaNaoUtil WHERE to_char(dataNaoUtil,'dd/MM/yyyy')=:param";
            Query q = getEntityManager().createQuery(hql);
            q.setParameter("param", formataDataDDMMYYYY(data));
            return q.getResultList().isEmpty();
        }
    }
}
