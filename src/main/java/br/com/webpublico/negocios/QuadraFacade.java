/*
 * Codigo gerado automaticamente em Tue Mar 01 14:19:07 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.Loteamento;
import br.com.webpublico.entidades.Quadra;
import br.com.webpublico.entidades.Setor;
import br.com.webpublico.util.StringUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class QuadraFacade extends AbstractFacade<Quadra> {

    @EJB
    private LoteamentoFacade loteamentoFacade;
    @EJB
    private SetorFacade setorFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public QuadraFacade() {
        super(Quadra.class);
    }

    public LoteamentoFacade getLoteamentoFacade() {
        return loteamentoFacade;
    }

    public SetorFacade getSetorFacade() {
        return setorFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public List<Quadra> recuperaPorLoteamento(Loteamento l) {
        String hql = "from Quadra q where q.loteamento =:loteamento";
        Query q = em.createQuery(hql);
        q.setParameter("loteamento", l);
        return q.getResultList();
    }

    public boolean existeNumeroQuadraSetor(Quadra quadra) {
        String hql = "select quadra from Quadra quadra where quadra.codigo = :numero and quadra.setor= :setor";
        if (quadra.getId() != null) {
            hql += " and quadra.id <> :id";
        }
        Query q = em.createQuery(hql);
        q.setParameter("numero", quadra.getCodigo());
        q.setParameter("setor", quadra.getSetor());
        if (quadra.getId() != null) {
            q.setParameter("id", quadra.getId());
        }

        if (!q.getResultList().isEmpty()) {
            Quadra resultado = (Quadra) q.getResultList().get(0);

            if (resultado != null) {
                return true;
            }
        }
        return false;
    }

    public List<Quadra> buscarPorSetor(Setor s, String parte) {
        String hql = "from Quadra q where q.setor =:setor and (q.codigo = :parte or lower(q.descricao) like :parte) " +
            " order by to_number(q.codigo)";
        Query q = em.createQuery(hql);
        q.setParameter("setor", s);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(30);
        return q.getResultList();
    }

    public List<Quadra> recuperaPorSetor(Setor s) {
        String hql = "from Quadra q where q.setor =:setor order by q.descricao";
        Query q = em.createQuery(hql);
        q.setParameter("setor", s);
        q.setMaxResults(30);
        return q.getResultList();
    }

    public Quadra recuperaQuadraPorSetor(Setor s, String codigo) {
        String hql = "from Quadra q where q.setor = :setor and q.codigo = :codigo";
        Query q = em.createQuery(hql);
        q.setParameter("setor", s);
        q.setParameter("codigo", codigo);
        if (q.getResultList().size() > 0) {
            return (Quadra) q.getResultList().get(0);
        } else {
            return null;
        }
    }

    public List<Quadra> recuperaPorLoteamentoSetor(Loteamento l, Setor s) {
        List<Quadra> lista = new ArrayList<Quadra>();
        String hql = "from Quadra q where q.loteamento =:loteamento and q.setor = :setor";
        Query q = em.createQuery(hql);
        q.setParameter("loteamento", l);
        q.setParameter("setor", s);
        if (q.getResultList().size() > 0) {
            lista = q.getResultList();
        }
        return lista;
    }

//    public List<Quadra> listaFiltrandoPorSetorOuCidade(String parte, Setor setor, Cidade cidade, int tipoQuadra) {
//        String sql = "from Quadra q ";
//        String juncao = " where ";
//
//        if (tipoQuadra != 0) {
//            if (tipoQuadra == 1) {
//                sql += juncao + " q.descricaoLoteamento like :descricaoLoteamento ";
//                juncao = " and ";
//            } else if (tipoQuadra == 2) {
//                sql += juncao + " q.descricao like :descricao ";
//                juncao = " and ";
//            } else {
//                sql += juncao + " q.descricao like :descricao or q.descricaoLoteamento like :descricaoLoteamento ";
//                juncao = " and ";
//            }
//        }
//        if (setor != null && setor.getId() != null) {
//            sql += juncao + "  q.setor = :setor ";
//            juncao = " and ";
//        }
//        if (cidade != null && cidade.getId() != null) {
//            sql += juncao + "  q.setor.cidade = :cidade ";
//        }
//        ////System.out.println("SQL -> " + sql);
//        Query q = em.createQuery(sql);
//        if (setor != null && setor.getId() != null) {
//            q.setParameter("setor", setor);
//        }
//        if (cidade != null && cidade.getId() != null) {
//            q.setParameter("cidade", cidade);
//        }
//        if (tipoQuadra != 0) {
//            if (tipoQuadra == 1) {
//                q.setParameter("descricaoLoteamento", "%" + parte.trim() + "%");
//            } else if (tipoQuadra == 2) {
//                q.setParameter("descricao", "%" + parte.trim() + "%");
//            } else {
//                q.setParameter("descricaoLoteamento", "%" + parte.trim() + "%");
//                q.setParameter("descricao", "%" + parte.trim() + "%");
//            }
//        }
//        return q.getResultList();
//    }

    public List<Quadra> listaFiltrandoPorSetor(String parte, Setor setor, int tipoQuadra) {
        String sql = "from Quadra q ";
        String juncao = " where ";

        if (tipoQuadra != 0) {
            if (tipoQuadra == 1) {
                sql += juncao + " q.descricaoLoteamento like :descricaoLoteamento ";
                juncao = " and ";
            } else if (tipoQuadra == 2) {
                sql += juncao + " q.descricao like :descricao ";
                juncao = " and ";
            } else {
                sql += juncao + " q.descricao like :descricao or q.descricaoLoteamento like :descricaoLoteamento ";
                juncao = " and ";
            }
        }
        if (setor != null && setor.getId() != null) {
            sql += juncao + "  q.setor = :setor ";
        }
        Query q = em.createQuery(sql);
        if (setor != null && setor.getId() != null) {
            q.setParameter("setor", setor);
        }
        if (tipoQuadra != 0) {
            if (tipoQuadra == 1) {
                q.setParameter("descricaoLoteamento", "%" + parte.trim() + "%");
            } else if (tipoQuadra == 2) {
                q.setParameter("descricao", "%" + parte.trim() + "%");
            } else {
                q.setParameter("descricaoLoteamento", "%" + parte.trim() + "%");
                q.setParameter("descricao", "%" + parte.trim() + "%");
            }
        }
        return q.getResultList();
    }

    public List<Quadra> listaPorSetorOuCidade(Setor setor, Cidade cidade) {
        if ((setor == null || setor.getId() == null) && (cidade == null || cidade.getId() == null)) {
            return this.lista();
        } else {
            String sql = "from Quadra q where 1=1 ";

            if (setor != null && setor.getId() != null) {
                sql += " and q.setor = :setor ";
            }
            if (cidade != null && cidade.getId() != null) {
                sql += " and q.setor.cidade = :cidade";
            }
            Query q = em.createQuery(sql);
            if (setor != null && setor.getId() != null) {
                q.setParameter("setor", setor);
            }
            if (cidade != null && cidade.getId() != null) {
                q.setParameter("cidade", cidade);
            }

            return q.getResultList();
        }
    }

    public boolean validaDescricao(Quadra quadra) {
        String hql = " from Quadra q where q.descricao = :des and q.setor = :setor";
        if (quadra.getId() != null) {
            hql += " and q != :q";
        }
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("des", quadra.getDescricao());
        q.setParameter("setor", quadra.getSetor());
        if (quadra.getId() != null) {
            q.setParameter("q", quadra);
        }
        return (q.getResultList().isEmpty());
    }

    public boolean validaDescricaoLoteamento(Quadra quadra) {
        String hql = " from Quadra q where q.descricaoLoteamento = :des and q.loteamento = :setor";
        if (quadra.getId() != null) {
            hql += " and q != :q";
        }
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("des", quadra.getDescricaoLoteamento());
        q.setParameter("setor", quadra.getLoteamento());
        if (quadra.getId() != null) {
            q.setParameter("q", quadra);
        }
        return (q.getResultList().isEmpty());
    }

    public Quadra getQuadraPorCodigo(Integer codigo) {
        String sql = "select * from quadra where codigo = :codigo";
        Query q = em.createNativeQuery(sql, Quadra.class);
        q.setParameter("codigo", codigo.toString());
        if (!q.getResultList().isEmpty()) {
            return (Quadra) q.getResultList().get(0);
        }
        return null;

    }

    public Quadra getQuadraPorCodigoSetor(Integer codigo, Integer setor) {
        String sql = "from Quadra where codigo = :codigo and setor.codigo = :setor";
        Query q = em.createQuery(sql);
        q.setParameter("codigo", StringUtil.cortaOuCompletaEsquerda(codigo.toString(),4,"0"));
        q.setParameter("setor", StringUtil.cortaOuCompletaEsquerda(setor.toString(),3,"0"));
        if (!q.getResultList().isEmpty()) {
            return (Quadra) q.getResultList().get(0);
        }
        return null;
    }

    public List<Quadra> buscarQuadraPorSetorInicialAndSetorFinal(Setor setorInicial, Setor setorFinal, String parte) {
        String sql = " from Quadra q " +
            " where (q.setor.codigo between :setorInicial and :setorFinal) " +
            "   and (q.codigo like :parte) " +
            " order by q.setor.codigo, q.codigo ";
        Query q = em.createQuery(sql);
        q.setParameter("setorInicial", setorInicial.getCodigo());
        q.setParameter("setorFinal", setorFinal.getCodigo());
        q.setParameter("parte", "%" + parte.trim() + "%");
        return q.getResultList();
    }
}
