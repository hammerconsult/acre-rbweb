package br.com.webpublico.negocios;

import br.com.webpublico.enums.EspecieTransporte;
import br.com.webpublico.enums.GrupoOcorrenciaFiscalizacaoRBTrans;
import br.com.webpublico.entidades.OcorrenciaFiscalizacaoRBTrans;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andre
 */
@Stateless
public class OcorrenciaFiscalizacaoRBTransFacade extends AbstractFacade<OcorrenciaFiscalizacaoRBTrans> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcorrenciaFiscalizacaoRBTransFacade() {
        super(OcorrenciaFiscalizacaoRBTrans.class);
    }

    public Long sugereCodigo() {
        Query q = em.createNativeQuery("select coalesce(max(codigo), 0) + 1 as codigo from OCORRENCIAFISCALIZARBTRANS");
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        return resultado.longValue();
    }

    public boolean codigoJaExiste(Object selecionado) {
        if (selecionado == null || ((OcorrenciaFiscalizacaoRBTrans) selecionado).getCodigo() == null || ((OcorrenciaFiscalizacaoRBTrans) selecionado).getCodigo().intValue() <= 0) {
            return false;
        }
        String hql = "from OcorrenciaFiscalizacaoRBTrans o where o.codigo = :codigo";
        if (((OcorrenciaFiscalizacaoRBTrans) selecionado).getId() != null) {
            hql += " and o != :ocorrencia";
        }
        Query q = em.createQuery(hql);
        q.setParameter("codigo", ((OcorrenciaFiscalizacaoRBTrans) selecionado).getCodigo());
        if (((OcorrenciaFiscalizacaoRBTrans) selecionado).getId() != null) {
            q.setParameter("ocorrencia", selecionado);
        }
        return !q.getResultList().isEmpty();
    }

    public List<OcorrenciaFiscalizacaoRBTrans> listaFiltrandoX(String s, int inicio, int max) {
        String sql = " select ocorrencia.* from OCORRENCIAFISCALIZARBTRANS ocorrencia"
                + " inner join Tributo tributo on tributo.id = ocorrencia.tributo_id"
                + " where lower(tributo.descricao) like :filtro"
                + " or lower(cast(ocorrencia.codigo as varchar(10))) like :filtro"
                + " or lower(ocorrencia.conduta) like :filtro "
                + " or lower(ocorrencia.descricao) like :filtro "
                + " or lower(cast(ocorrencia.valor as varchar(10))) like :filtro "
                + " or (ocorrencia.especieTransporte in (:listaEspecieTransporte)) "
                + " or (ocorrencia.especieTransporte in (:listaGrupos)) ";

        Query q = getEntityManager().createNativeQuery(sql, OcorrenciaFiscalizacaoRBTrans.class);

        List<String> listaEspecieTransporte = new ArrayList<String>();
        for (EspecieTransporte object : EspecieTransporte.values()) {
            if (object.getDescricao().toLowerCase().contains(s.trim().toLowerCase())) {
                listaEspecieTransporte.add(object.name());
            }
        }
        List<String> listaGrupoOcorrenciaFiscalizacaoRBTrans = new ArrayList<String>();
        for (GrupoOcorrenciaFiscalizacaoRBTrans object : GrupoOcorrenciaFiscalizacaoRBTrans.values()) {
            if (object.getDescricao().toLowerCase().contains(s.trim().toLowerCase())) {
                listaGrupoOcorrenciaFiscalizacaoRBTrans.add(object.name());
            }
        }
        if (listaEspecieTransporte.isEmpty()) {
            listaEspecieTransporte = null;
        }
        if (listaGrupoOcorrenciaFiscalizacaoRBTrans.isEmpty()) {
            listaGrupoOcorrenciaFiscalizacaoRBTrans = null;
        }

        q.setParameter("listaEspecieTransporte", listaEspecieTransporte);
        q.setParameter("listaGrupos", listaGrupoOcorrenciaFiscalizacaoRBTrans);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");

        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public List<OcorrenciaFiscalizacaoRBTrans> recuperaOcorrenciasPorEspecieTransporte(EspecieTransporte especie) {
        List<OcorrenciaFiscalizacaoRBTrans> toReturn = new ArrayList<OcorrenciaFiscalizacaoRBTrans>();
        String hql = "select ocorrencia from OcorrenciaFiscalizacaoRBTrans ocorrencia"
                + " where ocorrencia.especieTransporte = :especie";
        Query q = em.createQuery(hql);
        q.setParameter("especie", especie);
        if (!q.getResultList().isEmpty()) {
            toReturn = q.getResultList();
        }
        return toReturn;
    }

    @Override
    public OcorrenciaFiscalizacaoRBTrans recuperar(Object id) {
        OcorrenciaFiscalizacaoRBTrans oco = em.find(OcorrenciaFiscalizacaoRBTrans.class, id);
        oco.getOcorrenciasAutuacaoRBTrans().size();
        oco.getOcorrenciaFiscalizacao().size();


        return oco;
    }

    public Boolean existeOcorrenciaNoProcessoDeAutuacao(OcorrenciaFiscalizacaoRBTrans ocorrencia) {
        Query q = em.createNativeQuery("select ocorrenciafiscalizacao_id from ocorrenciaautuacaorbtrans"
                + " where ocorrenciafiscalizacao_id = :idOocrrencia");
        q.setParameter("idOocrrencia", ocorrencia.getId());
        return q.getResultList().isEmpty();

    }

    public List<OcorrenciaFiscalizacaoRBTrans> recuperaOcorrenciasPorTipo(String parte, EspecieTransporte et) {
        List<OcorrenciaFiscalizacaoRBTrans> toReturn = new ArrayList<>();
        String hql = "select ocorrencia from OcorrenciaFiscalizacaoRBTrans ocorrencia"
                + " where (ocorrencia.especieTransporte = :especieTransporte and ocorrencia.vigenciaFinal is null) "
                + " and (lower(to_char(ocorrencia.codigo)) like :filtro "
                + "      or lower(ocorrencia.grupo) like :filtro "
                + "      or lower(ocorrencia.artigo) like :filtro "
                + "      or lower(ocorrencia.conduta) like :filtro) ";

        Query q = em.createQuery(hql.toString());
        q.setParameter("filtro", "%" + parte.trim().toLowerCase().toString() + "%");
        q.setParameter("especieTransporte", et);
        toReturn = q.getResultList();

        return toReturn;
    }
}
