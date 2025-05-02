package br.com.webpublico.negocios;

import br.com.webpublico.entidades.LotacaoVistoriadora;
import br.com.webpublico.entidades.TipoVistoria;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class LotacaoVistoriadoraFacade extends AbstractFacade<LotacaoVistoriadora> implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LotacaoVistoriadoraFacade() {
        super(LotacaoVistoriadora.class);
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    @Override
    public LotacaoVistoriadora recuperar(Object id) {
        LotacaoVistoriadora lv = em.find(LotacaoVistoriadora.class, id);
        return lv;
    }

    public boolean codigoJaExiste(LotacaoVistoriadora selecionado) {
        if (selecionado == null || selecionado.getCodigo() == null || selecionado.getCodigo().intValue() <= 0) {
            return false;
        }
        String hql = "from LotacaoVistoriadora lc where lc.codigo = :codigo";
        if (selecionado.getId() != null) {
            hql += " and lc != :lotacao";
        }
        Query q = em.createQuery(hql);
        q.setParameter("codigo", selecionado.getCodigo());
        if (selecionado.getId() != null) {
            q.setParameter("lotacao", selecionado);
        }
        return !q.getResultList().isEmpty();
    }

    public boolean descricaoJaExiste(LotacaoVistoriadora selecionado) {
        if (selecionado == null || selecionado.getDescricao() == null || selecionado.getDescricao().trim().isEmpty()) {
            return false;
        }
        String hql = "from LotacaoVistoriadora lc where lower(lc.descricao) = :descricao";
        if (selecionado.getId() != null) {
            hql += " and lc != :lotacao";
        }
        Query q = em.createQuery(hql);
        q.setParameter("descricao", selecionado.getDescricao().toLowerCase().trim());
        if (selecionado.getId() != null) {
            q.setParameter("lotacao", selecionado);
        }
        return !q.getResultList().isEmpty();
    }

    public List<LotacaoVistoriadora> getLotacaoPorTipoVistoria(TipoVistoria tipoVistoria, String cnaesSelecionados, UsuarioSistema usuarioSistema) {
        if (tipoVistoria == null || tipoVistoria.getId() == null) {
            return new ArrayList<LotacaoVistoriadora>();
        } else {
            StringBuilder sql = new StringBuilder();
            sql.append("select distinct lv.* from lotacaovistoriadora lv ");
            sql.append(" inner join lotacaotipovistoriacnae ltvc on ltvc.lotacao_id = lv.id ");
            sql.append(" inner join tipovistoriacnae tvc on tvc.id = ltvc.tipovistoria_id ");
            sql.append(" inner join tipovistoria tv on tv.id = tvc.tipovistoria_id ");
            sql.append(" inner join lotacaotribusuario ltu on ltu.lotacao_id = lv.id ");
            sql.append(" inner join vigenciatribusuario vig on vig.id = ltu.vigenciatribusuario_id ");
            sql.append(" where tvc.tipovistoria_id = :tipoVistoria ");
            sql.append(" and vig.usuariosistema_id = :usuario ");

            if (!cnaesSelecionados.trim().isEmpty()) {
                sql.append(" and tvc.cnae_id in (").append(cnaesSelecionados).append(")");
            }

            Query q = em.createNativeQuery(sql.toString(), LotacaoVistoriadora.class);
            q.setParameter("tipoVistoria", tipoVistoria.getId());
            q.setParameter("usuario", usuarioSistema.getId());

            return q.getResultList();
        }
    }

    public Long sugereCodigo() {
        Query q = em.createNativeQuery("select coalesce(max(codigo), 0) + 1 as codigo from LotacaoVistoriadora lv");
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        return resultado.longValue();
    }

    public List<LotacaoVistoriadora> listaFiltrandoCodigoDescricao(String s) {
        Query q = em.createQuery(" from LotacaoVistoriadora l where lower(cast(l.codigo as string)) like :filtro"
                + " or lower(l.descricao) like :filtro");
        q.setParameter("filtro", "%" + s.trim().toLowerCase() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<LotacaoVistoriadora> listaPorUsuarioSistema(UsuarioSistema usuario) {
        Query q = em.createQuery(" select trib.lotacao from LotacaoTribUsuario trib "
                + " inner join trib.vigenciaTribUsuario vigenciaTrib "
                + " inner join vigenciaTrib.usuarioSistema usuario"
                + " where usuario = :usuarioSistema ");
        q.setParameter("usuarioSistema", usuario);
        return q.getResultList();
    }

    public List<LotacaoVistoriadora> listaPorUsuarioSistemaVigente(UsuarioSistema usuario, Date dataVigencia) {
        Query q = em.createQuery(" select trib.lotacao from LotacaoTribUsuario trib "
                + " inner join trib.vigenciaTribUsuario vigenciaTrib "
                + " inner join vigenciaTrib.usuarioSistema usuario"
                + " where usuario = :usuarioSistema "
                + " and trunc(vigenciaTrib.vigenciaInicial) <= :dataVigencia "
                + " and :dataVigencia <= trunc(coalesce(vigenciaTrib.vigenciaFinal,:dataVigencia)) ");
        q.setParameter("usuarioSistema", usuario);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataVigencia));
        return q.getResultList();
    }

    public List<LotacaoVistoriadora> listaOrdenado() {
        String hql = "from LotacaoVistoriadora order by descricao";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }
}
