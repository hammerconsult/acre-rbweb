/*
 * Codigo gerado automaticamente em Mon Sep 12 10:22:37 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoPlanejamentoPublico;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Stateless
public class ConfiguracaoPlanejamentoPublicoFacade extends AbstractFacade<ConfiguracaoPlanejamentoPublico> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoPlanejamentoPublicoFacade() {
        super(ConfiguracaoPlanejamentoPublico.class);
    }

    public ConfiguracaoPlanejamentoPublico retornaUltima(){
        String hql = "select a from ConfiguracaoPlanejamentoPublico a where a.desde = (select max(b.desde) from ConfiguracaoPlanejamentoPublico b)";
        Query query = em.createQuery(hql);
        query.setMaxResults(1);
        if(!query.getResultList().isEmpty()){

            return (ConfiguracaoPlanejamentoPublico) query.getSingleResult();

        }

        return null;
    }

        public ConfiguracaoPlanejamentoPublico listaConfiguracaoPlanejamentoPublicoFiltrandoVigencia(Date desde) {
        //busca a maior vigencia, sendo menor ou igual ao filtro da tela
        String sql = "SELECT CONF.*, CONFMOD.DESDE, CONFMOD.DTYPE "
                + " FROM CONFPLANEJAMENTOPUBLICO CONF"
                + " INNER JOIN CONFIGURACAOMODULO CONFMOD ON CONF.ID = CONFMOD.ID "
                + " WHERE CONF.ID = (SELECT MAX(CONF2.ID) "
                + "                  FROM CONFIGURACAOMODULO CONF2"
                + "                  WHERE CONF2.DESDE <=  TO_DATE(:parametro, 'dd/MM/yyyy') AND CONF2.ID IN  (SELECT C.ID FROM CONFPLANEJAMENTOPUBLICO C)) ORDER BY CONF.ID DESC";
        Query q = em.createNativeQuery(sql, ConfiguracaoPlanejamentoPublico.class);
        q.setParameter("parametro", new SimpleDateFormat("dd/MM/yyyy").format(desde));
        q.setMaxResults(1);
        List<ConfiguracaoPlanejamentoPublico> lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return new ConfiguracaoPlanejamentoPublico();
        }
        return lista.get(0);
    }
//
//    public ConfiguracaoPlanejamentoPublico listaConfiguracaoEventoContabilFiltrandoVigencia(Date desde) {
//        //busca a maior vigencia, sendo menor ou igual ao filtro da tela
//        //System.out.println("entrono configuracaoplanejamento");
//        try {
//            String hql = " from ConfiguracaoPlanejamentoPublico conf "
//                    + "  where conf.desde = (select max(conf2.desde) from ConfiguracaoPlanejamentoPublico conf2 "
//                    + " where conf2.desde<= to_date(:parametro,'dd/MM/yyyy')) ";
//            Query q = em.createQuery(hql);
//            q.setParameter("parametro", desde, TemporalType.DATE);
//            q.setMaxResults(1);
//            //System.out.println("resultado do listaConfiguracaoEventoContavil::: " + q.getResultList());
//            return (ConfiguracaoPlanejamentoPublico) q.getSingleResult();
//        } catch (Exception e) {
//            return null;
//        }
//    }
}
