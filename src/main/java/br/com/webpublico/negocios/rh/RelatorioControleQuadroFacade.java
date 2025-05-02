/*
 * Codigo gerado automaticamente em Wed Jun 29 13:47:09 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.UtilRH;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Stateless
public class RelatorioControleQuadroFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public List<HierarquiaOrganizacional> filtrandoHierarquiaOrganizacionalAdministrativa(String parte) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT h.* FROM VWHIERARQUIAADMINISTRATIVA VW ");
        sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
        sql.append(" WHERE (upper(vw.descricao) like :str or VW.CODIGO like :str) ");
        sql.append(" and to_date(:data, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:data, 'dd/MM/yyyy')) ");
        sql.append(" and to_date(:data, 'dd/mm/yyyy') between trunc(h.iniciovigencia) and coalesce(trunc(h.fimvigencia), to_date(:data, 'dd/MM/yyyy')) ");
        sql.append(" order by h.codigo asc ");

        Query q = em.createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("str", parametro);
        q.setParameter("data", DataUtil.getDataFormatada(UtilRH.getDataOperacao()));
        return new ArrayList<>(new HashSet<HierarquiaOrganizacional>(q.getResultList()));
    }
}
