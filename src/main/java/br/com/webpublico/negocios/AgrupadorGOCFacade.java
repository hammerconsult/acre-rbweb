package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AgrupadorGOC;
import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ArquivoParte;
import org.hibernate.Hibernate;
import org.primefaces.model.DefaultStreamedContent;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

@Stateless
public class AgrupadorGOCFacade extends AbstractFacade<AgrupadorGOC> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private GrupoObjetoCompraFacade grupoObjetoCompraFacade;
    @EJB
    private ArquivoFacade arquivoFacade;

    public AgrupadorGOCFacade() {
        super(AgrupadorGOC.class);
    }

    @Override
    public AgrupadorGOC recuperar(Object id) {
        AgrupadorGOC entity = super.recuperar(id);
        Hibernate.initialize(entity.getGrupos());
        if (entity.getArquivo() != null) {
            Hibernate.initialize(entity.getArquivo().getPartes());
        }
        return entity;
    }

    @Override
    public void remover(AgrupadorGOC entity) {
        arquivoFacade.removerArquivo(entity.getArquivo());
        super.remover(entity);
    }

    public DefaultStreamedContent carregarImagemGrupo(AgrupadorGOC entity) {
        Arquivo arq = entity.getArquivo();
        if (arq != null) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                for (ArquivoParte a : arquivoFacade.recuperaDependencias(arq.getId()).getPartes()) {
                    buffer.write(a.getDados());
                }
                InputStream is = new ByteArrayInputStream(buffer.toByteArray());
                return new DefaultStreamedContent(is, arq.getMimeType(), arq.getNome());
            } catch (Exception ex) {
                logger.error("Erro ao carregar imagem do grupo objeto de compra: ", ex);
            }
        }
        return null;
    }

    public List<AgrupadorGOC> buscarAgrupadorGOC(List<Long> idsGrupoObjetoCompra) {
        String sql = " select distinct agrup.* from agrupadorgoc agrup " +
            "               inner join agrupadorgocgrupo grupo on grupo.agrupadorgoc_id = agrup.id " +
            "             where grupo.grupoobjetocompra_id in (:idsGOC) ";
        Query q = em.createNativeQuery(sql, AgrupadorGOC.class);
        q.setParameter("idsGOC", idsGrupoObjetoCompra);
        return q.getResultList();
    }

    public List<Number> buscarGruposAgrupadorGOC(AgrupadorGOC agrupadorGOC) {
        String sql = " select grupo.grupoobjetocompra_id from agrupadorgocgrupo grupo  " +
            "          where grupo.agrupadorgoc_id = :idAgrupador ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAgrupador", agrupadorGOC.getId());
        return q.getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrupoObjetoCompraFacade getGrupoObjetoCompraFacade() {
        return grupoObjetoCompraFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }
}
