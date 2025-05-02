/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DePara;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.hibernate.exception.SQLGrammarException;

/**
 *
 * @author reidocrime
 */
@Stateless
public class DeParaFacade extends AbstractFacade<DePara> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DeParaFacade() {
        super(DePara.class);
    }

    @Override
    public DePara recuperar(Object id) {
        DePara toReturn = getEntityManager().find(DePara.class, id);
        toReturn.getDeParaItems().size();
        return toReturn;
    }

    public List<HierarquiaOrganizacional> listaSimplesFiltrandoPorDataTipo(Date dataVigente, TipoHierarquiaOrganizacional tipo, DePara dePara) throws ExcecaoNegocioGenerica {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT H.* FROM HIERARQUIAORGANIZACIONAL H ");
            sql.append(" WHERE H.TIPOHIERARQUIAORGANIZACIONAL = :tipo ");
            sql.append(" AND to_date(:dataVigente,'dd/MM/yyyy') ");
            sql.append(" BETWEEN trunc(H.INICIOVIGENCIA) ");
            sql.append(" and coalesce(trunc(H.FIMVIGENCIA), to_date(:dataVigente,'dd/MM/yyyy')) ");
            if (tipo.equals(TipoHierarquiaOrganizacional.ADMINISTRATIVA) && dePara.getId() != null) {
                sql.append(" and H.codigo not in(SELECT CODIGOADMANTIGA FROM DEPARAITEM");
                sql.append(" where DEPARA_ID =:deParaId) ");
            } else if (tipo.equals(TipoHierarquiaOrganizacional.ORCAMENTARIA) && dePara.getId() != null) {
                sql.append(" and H.codigo not in(SELECT CODIGOORCANTIGA FROM DEPARAITEM ");
                sql.append(" where DEPARA_ID =:deParaId) ");
            }
            sql.append(" order by h.codigo");
            Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);

            q.setParameter("dataVigente", DataUtil.getDataFormatada(dataVigente));
            q.setParameter("tipo", tipo.name());
            if (tipo != null && dePara.getId() != null) {
                q.setParameter("deParaId", dePara.getId());
            }

            if (q.getResultList().isEmpty()) {
                throw new ExcecaoNegocioGenerica("Não existe nenhuma Hierarquia Vigente na Data " + Util.dateToString(dataVigente));
            }

            return q.getResultList();

        } catch (SQLGrammarException sql) {
            throw new ExcecaoNegocioGenerica("Erro de sql " + sql.getMessage() + "  SQL: " + sql.getSQL());
        } catch (IllegalArgumentException il) {
            throw new ExcecaoNegocioGenerica("Erro argumetno invalido " + il.getMessage());
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }

    }
}
