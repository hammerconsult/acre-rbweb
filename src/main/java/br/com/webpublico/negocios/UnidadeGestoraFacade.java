/*
 * Codigo gerado automaticamente em Tue Apr 12 17:35:39 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.UnidadeGestoraPortal;
import br.com.webpublico.enums.CategoriaDeclaracaoPrestacaoContas;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoUnidadeGestora;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class UnidadeGestoraFacade extends AbstractFacade<UnidadeGestora> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UnidadeGestoraFacade() {
        super(UnidadeGestora.class);
    }

    @Override
    public UnidadeGestora recuperar(Object id) {
        UnidadeGestora ug = super.recuperar(id);
        Hibernate.initialize(ug.getUnidadeGestoraUnidadesOrganizacionais());
        for (UnidadeGestoraUnidadeOrganizacional unidade : ug.getUnidadeGestoraUnidadesOrganizacionais()) {
            unidade.setHierarquiaOrganizacional(hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), unidade.getUnidadeOrganizacional(), sistemaFacade.getDataOperacao()));
        }
        return ug;
    }

    public UnidadeGestora recuperarSemAtualizarHierarquias(Object id) {
        UnidadeGestora ug = super.recuperar(id);
        Hibernate.initialize(ug.getUnidadeGestoraUnidadesOrganizacionais());
        return ug;
    }

    public List<UnidadeGestoraPortal> listaUnidadePorAnoParaPortal(int ano, String codigo) {
        Exercicio exercicio = exercicioFacade.getExercicioPorAno(ano);
        if (exercicio != null) {
            List<UnidadeGestoraPortal> retorno = Lists.newArrayList();
            List<UnidadeGestora> unidades = Lists.newArrayList();
            if (codigo == null || codigo.isEmpty()) {
                unidades = listaTodasPorExercicioSemCamara(exercicio);
            } else {
                unidades = listaFiltrandoPorExercicio(exercicio, codigo);
            }

            for (UnidadeGestora uni : unidades) {
                UnidadeGestoraPortal uniPortal = new UnidadeGestoraPortal();
                uniPortal.setId(uni.getId());
                uniPortal.setCodigo(uni.getCodigo());
                uniPortal.setDescricao(uni.getDescricao());
                retorno.add(uniPortal);
            }
            return retorno;
        }
        return null;

    }


    public List<UnidadeGestora> listaFiltrandoPorExercicio(Exercicio ex, String parte) {
        String hql = "from UnidadeGestora uo " +
            " where uo.exercicio = :exercicio  " +
            " and (uo.codigo like :parte or lower(uo.descricao) like :parte) " +
            " order by uo.codigo ";
        Query q = em.createQuery(hql, UnidadeGestora.class);
        q.setParameter("exercicio", ex);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return (ArrayList<UnidadeGestora>) q.getResultList();
        }
    }

    public List<UnidadeGestora> listaTodasPorExercicioSemCamara(Exercicio ex) {
        String hql = "from UnidadeGestora uo " +
            " where uo.exercicio = :exercicio  " +
            " and (uo.codigo <> '001') " +
            " order by uo.codigo ";
        Query q = em.createQuery(hql, UnidadeGestora.class);
        q.setParameter("exercicio", ex);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return (ArrayList<UnidadeGestora>) q.getResultList();
        }
    }

    public UnidadeGestora getUnidadeGestoraDo(CategoriaDeclaracaoPrestacaoContas cat) {
        String hql = " select ug from DeclaracaoPrestacaoContas dpc" +
            " inner join dpc.unidadeGestora ug" +
            " where dpc.categoriaDeclaracaoPrestacaoContas = :cat";
        Query q = em.createQuery(hql);
        q.setParameter("cat", cat);
        q.setMaxResults(1);
        try {
            return (UnidadeGestora) q.getSingleResult();
        } catch (NoResultException npe) {
            return null;
        }
    }

    public List<UnidadeGestora> buscarUnidadeGestoraPorTipoAndExercicio(String parte, Exercicio exercicio, TipoUnidadeGestora tipo) {
        String sql = " select ug.* from unidadegestora ug " +
            "          where ug.exercicio_id = :idExercicio" +
            "          and (ug.codigo like :parte or lower(ug.descricao) like :parte) " +
            "          and ug.tipounidadegestora = :tipo  ";
        Query consulta = em.createNativeQuery(sql, UnidadeGestora.class);
        consulta.setParameter("idExercicio", exercicio.getId());
        consulta.setParameter("tipo", tipo.name());
        consulta.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return consulta.getResultList();
    }

    public UnidadeGestora recuperarPorUnidadeOrcamentariaAndTipo(Exercicio exercicio, TipoUnidadeGestora tipo, List<HierarquiaOrganizacional> unidades) {
        try {
            String sql = " select distinct ug.* from unidadegestora ug " +
                "            inner join UGESTORAUORGANIZACIONAL unid on unid.unidadegestora_id = ug.id " +
                "          where unid.unidadeorganizacional_id in (:idsUnidade) " +
                "           and ug.exercicio_id = :idExercicio " +
                "           and ug.tipounidadegestora = :tipo  ";
            Query consulta = em.createNativeQuery(sql, UnidadeGestora.class);
            consulta.setParameter("idExercicio", exercicio.getId());
            consulta.setParameter("idsUnidade", geIdsUnidades(unidades));
            consulta.setParameter("tipo", tipo.name());
            return (UnidadeGestora) consulta.getSingleResult();
        } catch (NonUniqueResultException e) {
            String unidade = getDescricaoUnidade(unidades);
            throw new ExcecaoNegocioGenerica("Existe mais de uma unidade gestora administrativa para a(s) unidade(s) orçamentária(s) correspondente(s) " + unidade + ".");
        } catch (NoResultException e) {
            String unidade = getDescricaoUnidade(unidades);
            throw new ExcecaoNegocioGenerica("Não existe unidade gestora administrativa para a(s) unidade(s) orçamentária(s) correspondente(s) " + unidade + ".");
        }
    }

    private String getDescricaoUnidade(List<HierarquiaOrganizacional> unidades) {
        String unidade = "";
        for (HierarquiaOrganizacional ho : unidades) {
            unidade += ho.getCodigo() + ", ";
        }
        unidade = unidade.substring(0, unidade.length() - 2);
        return unidade;
    }

    public List<Long> geIdsUnidades(List<HierarquiaOrganizacional> unidades) {
        List<Long> ids = Lists.newArrayList();
        for (HierarquiaOrganizacional unidade : unidades) {
            ids.add(unidade.getSubordinada().getId());
        }
        return ids;
    }

    public List<UnidadeGestoraUnidadeOrganizacional> buscarUnidadeGestoraPorUnidadeOrcamentariaAndTipo(UnidadeGestora unidadeGestora, UnidadeOrganizacional unidade) {
        String sql = "  select unid.* from unidadegestora ug " +
            "           inner join ugestorauorganizacional unid on unid.unidadegestora_id = ug.id " +
            "           where ug.tipounidadegestora = :tipo " +
            "           and unid.unidadeorganizacional_id = :idUnidade" +
            "           and ug.exercicio_id = :idExercicio ";
        sql += unidadeGestora.getId() != null ? " and ug.id <> :idUg " : " ";
        Query consulta = em.createNativeQuery(sql, UnidadeGestoraUnidadeOrganizacional.class);
        consulta.setParameter("idExercicio", unidadeGestora.getExercicio().getId());
        if (unidadeGestora.getId() != null) {
            consulta.setParameter("idUg", unidadeGestora.getId());
        }
        consulta.setParameter("idUnidade", unidade.getId());
        consulta.setParameter("tipo", unidadeGestora.getTipoUnidadeGestora().name());
        return consulta.getResultList();
    }

    public List<UnidadeGestora> completaUnidadeGestoraDasUnidadeDoUsuarioLogadoVigentes(String parte, UsuarioSistema usuarioSistema, Date date, Exercicio e, TipoUnidadeGestora tipoUG) {
        String sql = " select distinct ges.* from usuariosistema usuario " +
            " inner join USUARIOUNIDADEORGANIZACIO usu_unid on usuario.id = usu_unid.usuariosistema_id " +
            "        and usuario.id = :usuario " +
            " inner join unidadeorganizacional uo_adm on usu_unid.unidadeorganizacional_id = uo_adm.id " +
            " inner join hierarquiaorganizacional ho_adm on uo_adm.id = ho_adm.subordinada_id " +
            "        and ho_adm.tipohierarquiaorganizacional = 'ADMINISTRATIVA' " +
            "        and to_date(:data,'dd/mm/yyyy') between ho_adm.inicioVigencia and coalesce(ho_adm.fimvigencia,to_date(:data,'dd/mm/yyyy')) " +
            " inner join HIERARQUIAORGRESP resp on ho_adm.id = resp.hierarquiaorgadm_id " +
            " INNER JOIN hierarquiaorganizacional ho_orc ON resp.hierarquiaorgorc_id = ho_orc.id " +
            "        and ho_orc.tipohierarquiaorganizacional = 'ORCAMENTARIA' " +
            "        and to_date(:data,'dd/mm/yyyy') between ho_orc.inicioVigencia and coalesce(ho_orc.fimvigencia,to_date(:data,'dd/mm/yyyy')) " +
            " inner join unidadeorganizacional uo_orc on ho_orc.subordinada_id = uo_orc.id " +
            " inner join UGESTORAUORGANIZACIONAL uo_ges on uo_orc.id = uo_ges.unidadeorganizacional_id " +
            " inner join unidadegestora ges on uo_ges.unidadegestora_id = ges.id " +
            " where (ges.codigo like :parte or lower(ges.descricao) like :parte)" +
            " and ges.exercicio_id = :exe";
            sql += tipoUG != null ? " and ges.tipounidadegestora = :tipoUG " : "";
        sql += " order by ges.codigo, ges.descricao";
        Query consulta = em.createNativeQuery(sql, UnidadeGestora.class);
        consulta.setParameter("usuario", usuarioSistema.getId());
        consulta.setParameter("exe", e.getId());
        consulta.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        consulta.setParameter("data", DataUtil.getDataFormatada(date));
        if (tipoUG !=null) {
            consulta.setParameter("tipoUG", tipoUG.name());
        }
        return consulta.getResultList();
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public PessoaJuridicaFacade getPessoaJuridicaFacade() {
        return pessoaJuridicaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
