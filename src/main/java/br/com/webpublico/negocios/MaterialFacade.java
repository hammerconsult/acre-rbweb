package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.StatusMaterial;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class MaterialFacade extends AbstractFacade<Material> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ModeloFacade modeloFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private AssociacaoGrupoObjetoCompraGrupoMaterialFacade associacaoFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private DerivacaoObjetoCompraComponenteFacade derivacaoObjetoCompraComponenteFacade;

    public MaterialFacade() {
        super(Material.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Material recuperar(Object id) {
        Material entity = super.recuperar(id);
        Hibernate.initialize(entity.getEfetivacoes());
        return entity;
    }

    public Material buscaMaterialPorCodigo(Long codigo) {
        String sql = "select m.* from material m where m.codigo = :filtro and m.statusMaterial = :status";
        Query q = em.createNativeQuery(sql, Material.class);
        q.setParameter("filtro", codigo);
        q.setParameter("status", StatusMaterial.DEFERIDO.name());
        try {
            return (Material) q.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    public void validarExclusao(Material entity) {
        if (verificarSeMaterialTemEntradaNoEstoque(entity)) {
            throw new ValidacaoException("O material não poderá ser excluído, pois o mesmo possui movimentação de estoque.");
        }
    }

    public void validarClassificacao(Material entity) {
        if (entity.getPerecibilidade() != null
            && !entity.getPerecibilidade().isNaoSeAplica()
            && !entity.getControleDeLote()) {
            throw new ValidacaoException("Quando a classificação do material for diferente de 'Não se Aplica', o controle por lote deve ser informado.");
        }
    }

    public List<Material> completarMaterialNaHierarquiaDoLocalEstoquePorDataAndComEstoque(LocalEstoque localEstoque, GrupoMaterial grupoMaterial, String filtro, List<StatusMaterial> situacoes) {
        String sql =
            " WITH PAI (ID, CODIGO, DESCRICAO, FECHADOEM, SUPERIOR_ID, UNIDADEORGANIZACIONAL_ID) AS       " +
                "  (SELECT LE_PAI.ID, LE_PAI.CODIGO, LE_PAI.DESCRICAO, LE_PAI.FECHADOEM, LE_PAI.SUPERIOR_ID, LE_PAI.UNIDADEORGANIZACIONAL_ID      " +
                "     FROM LOCALESTOQUE LE_PAI       " +
                "    WHERE LE_PAI.ID = :LOCAL_PAI   " +
                " UNION ALL       " +
                "   SELECT LE_FILHO.ID, LE_FILHO.CODIGO, LE_FILHO.DESCRICAO, LE_FILHO.FECHADOEM, LE_FILHO.SUPERIOR_ID, LE_FILHO.UNIDADEORGANIZACIONAL_ID       " +
                "     FROM LOCALESTOQUE LE_FILHO       " +
                " INNER JOIN PAI P ON P.ID = LE_FILHO.SUPERIOR_ID)   " +
                "   SELECT DISTINCT M.*   " +
                "     FROM ESTOQUE E   " +
                " INNER JOIN MATERIAL M                   ON M.ID = E.MATERIAL_ID " +
                " INNER JOIN LOCALESTOQUEORCAMENTARIO LEO ON LEO.ID = E.LOCALESTOQUEORCAMENTARIO_ID   " +
                " INNER JOIN LOCALESTOQUE LE              ON LE.ID = LEO.LOCALESTOQUE_ID   " +
                " INNER JOIN PAI P                        ON P.ID = LE.ID   " +
                "    WHERE LE.FECHADOEM IS NULL   " +
                "      AND E.FISICO > 0   " +
                "      AND E.BLOQUEADO = 0   " +
                "      AND M.StatusMaterial in (:situacoes)   " +
                "      AND E.DATAESTOQUE = (SELECT MAX(E2.DATAESTOQUE)   " +
                "                             FROM ESTOQUE E2   " +
                "                            WHERE E2.LOCALESTOQUEORCAMENTARIO_ID = E.LOCALESTOQUEORCAMENTARIO_ID   " +
                "                              AND E2.MATERIAL_ID = E.MATERIAL_ID  " +
                "                              AND trunc(E2.DATAESTOQUE) <= trunc(:DATA_OPERACAO))" +
                "     AND (LOWER(TRIM(M.CODIGO)) LIKE :FILTRO " +
                "       OR lower(translate(M.descricao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:FILTRO,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') )" +
                "           order by (case when to_char(m.codigo) = :FILTRO then 1 " +
                "                          when to_char(m.codigo) like :FILTRO then 2 " +
                "                          else 3 end), m.codigo, m.descricao, m.id desc ";
        sql += grupoMaterial != null ? " and m.grupo_id = :idGrupo " : "";
        Query q = em.createNativeQuery(sql, Material.class);
        q.setParameter("LOCAL_PAI", localEstoque.getId());
        q.setParameter("FILTRO", "%" + filtro.toLowerCase().trim() + "%");
        q.setParameter("DATA_OPERACAO", DataUtil.dataSemHorario(sistemaFacade.getDataOperacao()));
        q.setParameter("situacoes", Util.getListOfEnumName(situacoes));
        if (grupoMaterial != null) {
            q.setParameter("idGrupo", grupoMaterial.getId());
        }
        return q.getResultList();
    }

    public List<Material> buscarMateriaisPorObjetoCompra(ObjetoCompra objetoCompra) {
        String hql = " from Material m "
            + "          where m.objetoCompra = :objeto " +
            "           and m.statusMaterial in :statusValidos "
            + "         order by m.codigo";
        Query q = em.createQuery(hql);
        q.setParameter("objeto", objetoCompra);
        q.setParameter("statusValidos", Lists.newArrayList(StatusMaterial.AGUARDANDO, StatusMaterial.DEFERIDO, StatusMaterial.INDEFERIDO));
        return q.getResultList();
    }

    public List<Material> completarMaterialPermitidoNaHierarquiaDoLocalEstoque(GrupoMaterial grupoMaterial, LocalEstoque localEstoque, String parte, List<StatusMaterial> situacoes) {
        String sql = " " +
            "  WITH PAI (ID, CODIGO, DESCRICAO, FECHADOEM, SUPERIOR_ID, UNIDADEORGANIZACIONAL_ID) " +
            "  AS ( " +
            "    SELECT " +
            "      LE_PAI.ID, " +
            "      LE_PAI.CODIGO, " +
            "      LE_PAI.DESCRICAO, " +
            "      LE_PAI.FECHADOEM, " +
            "      LE_PAI.SUPERIOR_ID, " +
            "      LE_PAI.UNIDADEORGANIZACIONAL_ID " +
            "    FROM LOCALESTOQUE LE_PAI " +
            "    WHERE LE_PAI.ID = :local_pai_id " +
            "  UNION ALL " +
            "    SELECT " +
            "      LE_FILHO.ID, " +
            "      LE_FILHO.CODIGO, " +
            "      LE_FILHO.DESCRICAO, " +
            "      LE_FILHO.FECHADOEM, " +
            "      LE_FILHO.SUPERIOR_ID, " +
            "      LE_FILHO.UNIDADEORGANIZACIONAL_ID " +
            "     FROM LOCALESTOQUE LE_FILHO " +
            "       INNER JOIN PAI P ON P.ID = LE_FILHO.SUPERIOR_ID) " +
            " SELECT DISTINCT M.* FROM MATERIAL M " +
            "   INNER JOIN LOCALESTOQUEMATERIAL LEM ON LEM.MATERIAL_ID = M.ID " +
            "   INNER JOIN PAI ON PAI.ID = LEM.LOCALESTOQUE_ID " +
            " WHERE (M.CODIGO LIKE :filtro OR LOWER(M.DESCRICAO) LIKE :filtro ) " +
            " AND M.statusMaterial in (:situacoes) " +
            "           order by (case when to_char(m.codigo) = :filtro then 1 " +
            "                          when to_char(m.codigo) like :filtro then 2 " +
            "                          else 3 end), m.codigo, m.descricao, m.id desc ";
        sql += grupoMaterial != null ? " and m.grupo_id = :idGrupo " : "";
        Query q = em.createNativeQuery(sql, Material.class);
        q.setParameter("local_pai_id", localEstoque.getId());
        q.setParameter("filtro", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("situacoes", Util.getListOfEnumName(situacoes));
        if (grupoMaterial != null) {
            q.setParameter("idGrupo", grupoMaterial.getId());
        }
        return q.getResultList();
    }

    public boolean verificarSeMaterialTemEntradaNoEstoque(Material material) {
        String sql = "select e from Estoque e where e.material  = :material";
        Query q = em.createQuery(sql);
        q.setParameter("material", material);
        return !q.getResultList().isEmpty();
    }

    public List<Material> buscarMaterialPorGrupoMaterial(GrupoMaterial grupoMaterial, String parte, List<StatusMaterial> situacoes) {
        String sql = " select m.* from material m         " +
            "            where m.grupo_id = :idGrupo      " +
            "            and m.statusMaterial in (:situacoes) " +
            "            and (m.codigo like :parte         " +
            "              or m.descricao like :parte)    " +
            "           order by (case when to_char(m.codigo) = :parte then 1 " +
            "                          when to_char(m.codigo) like :parte then 2 " +
            "                          else 3 end), m.codigo, m.descricao, m.id desc ";
        Query q = em.createNativeQuery(sql, Material.class);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setParameter("idGrupo", grupoMaterial.getId());
        q.setParameter("situacoes", Util.getListOfEnumName(situacoes));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<Material> buscarMateriaisPorMarca(Marca marca) {
        String hql = " from Material m where m.marca = :param";
        Query q = em.createQuery(hql);
        q.setParameter("param", marca);
        return q.getResultList();
    }

    public List<Material> buscarMateriaisPorModelo(Modelo modelo) {
        String hql = " from Material m where m.modelo = :param";
        Query q = em.createQuery(hql);
        q.setParameter("param", modelo);
        return q.getResultList();
    }

    public List<Material> buscarMateriaisPorDescricaoAndStatusDeferido(String descricao) {
        String sql = " select m.* from material m " +
            " where m.statusMaterial = :statusDeferido " +
            "   and regexp_like(upper(m.descricao), :descricao) ";
        Query q = em.createNativeQuery(sql, Material.class);
        q.setParameter("statusDeferido", StatusMaterial.DEFERIDO.name());
        q.setParameter("descricao", descricao.trim().replace(" ", "|").toUpperCase());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<Material> buscarMateriaisPorCodigoOuDescricao(String parte, List<StatusMaterial> situacoes) {
        String sql = " select m.* from material m " +
            "           where m.statusMaterial in (:situacoes) " +
            "             and (to_char(m.codigo) like :parte  " +
            "              or lower(translate(m.descricao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:parte,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) " +
            "           order by (case when to_char(m.codigo) = :parte then 1 " +
            "                          when to_char(m.codigo) like :parte then 2 " +
            "                          else 3 end), " +
            "                          m.codigo, m.descricao, m.id desc ";
        Query q = em.createNativeQuery(sql, Material.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("situacoes", Util.getListOfEnumName(situacoes));
        q.setMaxResults(100);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<Material> buscarTodosMateriais() {
        String sql = " select m.* from material m ";
        Query q = em.createNativeQuery(sql, Material.class);
        return q.getResultList();
    }

    public List<Material> buscarMateriaisPorLocalDeEstoqueECodigoOuDescricao(String parte, LocalEstoque localEstoque, List<StatusMaterial> situacoes) {
        String sql = " select m.* " +
            " from material m " +
            "    inner join localestoquematerial lem on lem.material_id = m.id " +
            " where m.statusMaterial in (:situacoes)  " +
            "   and lem.localestoque_id = :idLocal " +
            "   and (to_char(m.codigo) like :parte  " +
            "    or lower(translate(m.descricao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:parte,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) " +
            "           order by (case when to_char(m.codigo) = :parte then 1 " +
            "                          when to_char(m.codigo) like :parte then 2 " +
            "                          else 3 end), m.codigo, m.descricao, m.id desc ";
        Query q = em.createNativeQuery(sql, Material.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("idLocal", localEstoque.getId());
        q.setParameter("situacoes", Util.getListOfEnumName(situacoes));
        q.setMaxResults(100);
        return q.getResultList();
    }

    public List<Material> buscarMateriaisLocalEstoque(LocalEstoque localEstoque) {
        String sql = " select mat.* from material mat " +
            "inner join localestoquematerial lem on lem.material_id = mat.id " +
            "where lem.localestoque_id = :idLocal ";
        Query q = em.createNativeQuery(sql, Material.class);
        q.setParameter("idLocal", localEstoque.getId());
        return q.getResultList();
    }

    public List<Material> buscarMateriaisEntrada(EntradaMaterial entrada) {
        String sql = " select mat.* from itementradamaterial item " +
            "          inner join material mat on mat.id = item.material_id " +
            "         where item.entradamaterial_id = :idEntrada ";
        Query q = em.createNativeQuery(sql, Material.class);
        q.setParameter("idEntrada", entrada.getId());
        return q.getResultList();
    }

    @Override
    public void remover(Material entity) {
        validarExclusao(entity);
        List<LocalEstoqueMaterial> locaisEstoque = localEstoqueFacade.buscarLocaisEstoqueMaterial(entity);
        for (LocalEstoqueMaterial lem : locaisEstoque) {
            if (!localEstoqueFacade.materialPossuiVinculoComLocalEstoqueOrcamentario(entity, lem.getLocalEstoque())) {
                em.remove(lem);
            }
        }
        super.remover(entity);
    }

    @Override
    public Material salvarRetornando(Material entity) {
        validarClassificacao(entity);
        if (entity.getCodigo() == null) {
            entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(Material.class, "codigo"));
        }
        if (entity.getStatusMaterial().isIndeferido()) {
            entity.setStatusMaterial(StatusMaterial.AGUARDANDO);
        }

        EfetivacaoMaterial novoEfetivacao = novaEfetivacaoMaterial(entity);
        entity = em.merge(entity);

        if (novoEfetivacao != null) {
            novoEfetivacao.setMaterial(entity);
            criarNotificacaoCadastroMaterial(novoEfetivacao);
        }
        return entity;
    }

    public EfetivacaoMaterial novaEfetivacaoMaterial(Material entity) {
        EfetivacaoMaterial ultimaEfetivacao = entity.getUltimaEfetivacao();
        boolean podeCriarEfetivacao = false;

        if (Util.isListNullOrEmpty(entity.getEfetivacoes())) {
            podeCriarEfetivacao = true;
        } else if (ultimaEfetivacao != null) {
            StatusMaterial statusAtual = entity.getStatusMaterial();
            StatusMaterial ultimoStatus = ultimaEfetivacao.getSituacao();

            if (!statusAtual.equals(ultimoStatus)) {
                podeCriarEfetivacao = true;
            }
        }

        if (podeCriarEfetivacao) {
            EfetivacaoMaterial efetivacaoMaterial = new EfetivacaoMaterial();
            efetivacaoMaterial.setMaterial(entity);
            efetivacaoMaterial.setDataRegistro(new Date());
            efetivacaoMaterial.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            efetivacaoMaterial.setUnidadeAdministrativa(sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente());
            efetivacaoMaterial.setSituacao(entity.getStatusMaterial());
            entity.getEfetivacoes().add(efetivacaoMaterial);
            return efetivacaoMaterial;
        }
        return null;
    }

    public EfetivacaoMaterial recuperarEfetivacao(Material material) {
        String sql = "select em.* from efetivacaomaterial em inner join material m on m.efetivacaomaterial_id = em.id where m.id = :idMaterial";
        Query q = em.createNativeQuery(sql, EfetivacaoMaterial.class);
        q.setParameter("idMaterial", material.getId());
        try {
            return (EfetivacaoMaterial) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public void criarNotificacaoCadastroMaterial(EfetivacaoMaterial efetivacao) {
        Material material = efetivacao.getMaterial();
        String obs = !Util.isStringNulaOuVazia(efetivacao.getObservacao()) ? efetivacao.getObservacao() + "." : "";
        String msg = "O material nº " + material.getCodigoDescricao() + ", cadastrado por " + efetivacao.getUsuarioSistema().getNome() + ", "
            + hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), efetivacao.getUnidadeAdministrativa(), sistemaFacade.getDataOperacao())
            + ", está " + material.getStatusMaterial().getDescricao() + ". " + obs;

        String link = "/material/ver/" + material.getId() + "/";
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao(msg);
        notificacao.setLink(link);
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTitulo(TipoNotificacao.OCORRENCIA_CADASTRO_MATERIAL.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.OCORRENCIA_CADASTRO_MATERIAL);
        NotificacaoService.getService().notificar(notificacao);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public AssociacaoGrupoObjetoCompraGrupoMaterialFacade getAssociacaoFacade() {
        return associacaoFacade;
    }

    public LocalEstoqueFacade getLocalEstoqueFacade() {
        return localEstoqueFacade;
    }

    public ModeloFacade getModeloFacade() {
        return modeloFacade;
    }

    public DerivacaoObjetoCompraComponenteFacade getDerivacaoObjetoCompraComponenteFacade() {
        return derivacaoObjetoCompraComponenteFacade;
    }
}
