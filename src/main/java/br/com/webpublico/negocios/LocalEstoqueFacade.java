/*
 * Codigo gerado automaticamente em Thu May 19 11:14:21 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.LocalEstoqueControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.EnderecoLocalEstoque;
import br.com.webpublico.entidadesauxiliares.FormularioLocalEstoque;
import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Stateless
public class LocalEstoqueFacade extends AbstractFacade<LocalEstoque> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private MaterialFacade materialFacade;
    @EJB
    private LoteMaterialFacade loteMaterialFacade;
    @EJB
    private EstoqueFacade estoqueFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;

    public LocalEstoqueFacade() {
        super(LocalEstoque.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<LocalEstoque> lista() {
        Query q = em.createQuery(" from LocalEstoque le order by codigo");
        return q.getResultList();
    }

    @Override
    public LocalEstoque recuperar(Object id) {
        if (id == null) {
            return null;
        }
        LocalEstoque le = super.recuperar(id);
        le.getListaGestorLocalEstoque().size();
        le.getListaLocalEstoqueMaterial().size();
        le.getListaLocalEstoqueOrcamentario().size();
        return le;
    }

    public LocalEstoque recuperarSemDependencia(Object id) {
        if (id == null) {
            return null;
        }
        return super.recuperar(id);
    }

    public LocalEstoque recuperarComDependenciaGestor(Object id) {
        if (id == null) {
            return null;
        }
        LocalEstoque le = super.recuperar(id);
        Hibernate.initialize(le.getListaGestorLocalEstoque());
        return le;
    }

    public LocalEstoque recuperarLocalEstoqueMateriais(Object id) {
        LocalEstoque le = super.recuperar(id);
        Hibernate.initialize(le.getListaLocalEstoqueMaterial());
        return le;
    }

    public LocalEstoque recuperarLocalDeEstoqueSemMateriais(Object id) {
        if (id == null) {
            return null;
        }
        LocalEstoque le = super.recuperar(id);
        le.getListaGestorLocalEstoque().size();
        le.getListaLocalEstoqueOrcamentario().size();
        return le;
    }


    @SuppressWarnings("unchecked")
    public List<LocalEstoqueMaterial> recuperarLocalEstoqueMaterial(LocalEstoqueControlador.FiltroLocalEstoqueMaterial filtro) {
        Criteria criteria = getCriteria(filtro);
        criteria.setFirstResult(filtro.getPrimeiroRegistro());
        criteria.setMaxResults(filtro.getQuantidadeRegistro());
        return criteria.list();
    }

    public int quantidadeMaterial(LocalEstoqueControlador.FiltroLocalEstoqueMaterial filtro) {
        Criteria criteria = getCriteria(filtro);
        criteria.setProjection(Projections.rowCount());
        return ((Number) criteria.uniqueResult()).intValue();
    }


    private Criteria getCriteria(LocalEstoqueControlador.FiltroLocalEstoqueMaterial filtro) {
        Session session = em.unwrap(Session.class);
        Criteria criteria = session.createCriteria(LocalEstoqueMaterial.class);
        criteria.add(Restrictions.eq("localEstoque", filtro.getLocalEstoque()));
        if (filtro.getMaterialExclusao() != null && !filtro.getMaterialExclusao().isEmpty()) {
            List<Material> materiais = Lists.newArrayList();
            for (LocalEstoqueMaterial localEstoqueMaterial : filtro.getMaterialExclusao()) {
                materiais.add(localEstoqueMaterial.getMaterial());
            }
            int partes = materiais.size() > 1000 ? (materiais.size() / 1000) : materiais.size();
            List<List<Material>> listaParte = Lists.partition(materiais, partes);
            for (List<Material> itens : listaParte) {
                criteria.add(Restrictions.not(Restrictions.in("material", itens)));
            }
        }
        return criteria;
    }


    public void salvarNovo(LocalEstoque entity, List<LocalEstoqueMaterial> localEstoqueMaterial) {
        entity.setListaLocalEstoqueMaterial(localEstoqueMaterial);
        super.salvarNovo(entity);
    }

    public void salvar(LocalEstoque entity, List<LocalEstoqueMaterial> novoLocalEstoqueMaterial, List<LocalEstoqueMaterial> removerLocalEstoqueMaterial) {
        for (LocalEstoqueMaterial removidos : removerLocalEstoqueMaterial) {
            em.remove(em.find(LocalEstoqueMaterial.class, removidos.getId()));
        }
        for (LocalEstoqueMaterial adicionados : novoLocalEstoqueMaterial) {
            em.persist(adicionados);
        }
        super.salvar(entity);
    }

    public List<LocalEstoque> buscarLocaisEstoquePorUnidade(String filter, UnidadeOrganizacional unidade) {
        String sql = " select DISTINCT estoque.* from LOCALESTOQUE estoque " +
            " left join LOCALESTOQUEORCAMENTARIO orc on estoque.ID = orc.LOCALESTOQUE_ID" +
            " where ( estoque.descricao like :filter or estoque.CODIGO like :filter ) ";

        if (unidade != null) {
            sql += " and  estoque.UNIDADEORGANIZACIONAL_ID = :adm ";

        }

        Query q = em.createNativeQuery(sql, LocalEstoque.class);
        q.setParameter("filter", "%" + filter.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        if (unidade != null) {
            q.setParameter("adm", unidade.getId());
        }
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }

        return Lists.newArrayList();

    }

    public String recuperaMascaraDoCodigoLocalEstoque() {
        String hqlDaMascara = "select conf from ConfiguracaoAdministrativa conf where parametro = 'MASCARA_CODIGO_LOCAL_ESTOQUE' and validoate is null";
        Query consultaDaMascara = em.createQuery(hqlDaMascara);
        try {
            ConfiguracaoAdministrativa conf = (ConfiguracaoAdministrativa) consultaDaMascara.getSingleResult();
            return conf.getValor();
        } catch (Exception e) {
            return null;
        }
    }

    public String recuperaCodigoLocalEstoque() {
        String hql = "from LocalEstoque a where a.codigo = (select max(b.codigo) from LocalEstoque b)";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        try {
            String codigoInteiro = ((LocalEstoque) q.getSingleResult()).getCodigo();
            return codigoInteiro;
        } catch (Exception e) {
            return null;
        }
    }

    private int recuperaNivel(String codigoDoPai) {
        int nivel = 0;
        for (int i = 0; i < codigoDoPai.length(); i++) {
            char c = codigoDoPai.charAt(i);
            if (c == '.') {
                nivel++;
            }
        }
        return nivel;
    }

    public String gerarCodigoFilho(LocalEstoque localEstoque) {
        int nivel = recuperaNivel(localEstoque.getCodigo());
        String maiorCodigoDosFilhos = recuperaMaiorCodigoDosFilhos(localEstoque);
        String maiorCodigo[] = maiorCodigoDosFilhos.split("\\.");

        String mascara = recuperaMascaraDoCodigoLocalEstoque();
        String mascaraAtePrimeiroPonto[] = mascara.split("\\.");
        String mascaraDoCodigoDoNivel = mascaraAtePrimeiroPonto[nivel + 1].replaceAll("#", "9");
        String proximoNumero = null;
        Boolean codigoEstaDeAcordoComMascar = Boolean.FALSE;
        try {
            Integer numero = Integer.parseInt(maiorCodigo[nivel + 1]);
            numero++;
            String zeros = "0";
            for (int i = 0; i <= mascaraDoCodigoDoNivel.length(); i++) {
                zeros += "0";
            }
            proximoNumero = zeros + numero;
        } catch (Exception e) {
            proximoNumero = (mascaraDoCodigoDoNivel.replaceAll("9", "0")) + "1";
        } finally {
            do {
                if (proximoNumero.length() != mascaraDoCodigoDoNivel.length()) {
                    proximoNumero = proximoNumero.substring(1, proximoNumero.length());
                } else {
                    codigoEstaDeAcordoComMascar = Boolean.TRUE;
                }
            } while (!codigoEstaDeAcordoComMascar);
        }
        return localEstoque.getCodigo() + "." + proximoNumero;
    }

    public String geraCodigoNovo() {

        String codigoInteiro = null;
        String codigoRetorno = null;

        try {
            codigoInteiro = recuperaCodigoLocalEstoque();
            String quebrado[] = codigoInteiro.split("\\.");
            String zeros = recuperaQuantidadeDeZeros(quebrado, 0);
            int numero = Integer.parseInt(quebrado[0]);
            numero++;
            codigoRetorno = zeros + numero;
            Boolean codigoEstaDeAcordoComMasca = Boolean.FALSE;

            do {
                if (codigoRetorno.length() != quebrado[0].length()) {
                    codigoRetorno = codigoRetorno.substring(1, codigoRetorno.length());
                } else {
                    codigoEstaDeAcordoComMasca = Boolean.TRUE;
                }
            } while (!codigoEstaDeAcordoComMasca);

        } catch (Exception e) {
            String maskQuebrada[] = recuperaMascaraDoCodigoLocalEstoque().split("\\.");
            String zeros = recuperaQuantidadeDeZeros(maskQuebrada, 0);
            codigoRetorno = zeros + "1";
        }

        return codigoRetorno;
    }

    private String recuperaQuantidadeDeZeros(String[] quebrado, int nivel) {
        String zeros = "";

        for (int i = 0; i < quebrado[nivel].length() - 1; i++) {
            char c = quebrado[nivel].charAt(i);
            char p = quebrado[nivel].charAt(i + 1);

            if (c == '0' || c == '#') {
                zeros += "0";
            }
            if ((p == '9') && (quebrado[nivel].indexOf(p) == quebrado[nivel].length() - 1)) {
                zeros = zeros.substring(0, zeros.length() - 1);
            }
        }
        return zeros;
    }

    public LocalEstoque buscaLocalFilhoDoLocalDeEstoque(LocalEstoque localEstoque) {
        String hql = "select filho " +
            "           from LocalEstoque filho"
            + "        where superior = :pai";
        Query consulta = em.createQuery(hql);
        consulta.setParameter("pai", localEstoque);
        consulta.setMaxResults(1);
        try {
            return (LocalEstoque) consulta.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validaCodigoDoLocalRepetido(LocalEstoque localEstoque) {
        String hql = " from LocalEstoque local where local.codigo = :codigo";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("codigo", localEstoque.getCodigo());

        List<LocalEstoque> lista = q.getResultList();

        if (lista.contains(localEstoque)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public String recuperaMaiorCodigoDosFilhos(LocalEstoque localEstoquePai) {
        String hql = "select codigo from LocalEstoque where codigo = (select max(codigo) from LocalEstoque where superior_id = :localEstoquePai)";
        Query consulta = em.createQuery(hql);
        consulta.setParameter("localEstoquePai", localEstoquePai);
        consulta.setMaxResults(1);
        try {
            return (String) consulta.getSingleResult();
        } catch (Exception e) {
            return new String();
        }
    }

    public List<LocalEstoqueMaterial> recuperaListaLocalEstoqueMaterial(LocalEstoque local) {
        return em.createQuery("from LocalEstoqueMaterial lem where lem.localEstoque = :param").setParameter("param", local).getResultList();
    }

    public List<LocalEstoqueMaterial> buscarLocaisEstoqueMaterial(Material material) {
        String sql = "select lem.* from localestoquematerial lem " +
            "         where lem.MATERIAL_ID = :idMaterial";
        Query q = em.createNativeQuery(sql, LocalEstoqueMaterial.class);
        q.setParameter("idMaterial", material.getId());
        try {
            return q.getResultList();
        } catch (NoResultException ex) {
            return Lists.newArrayList();
        }
    }

    public List<LocalEstoqueMaterial> buscarListaLocalEstoqueMaterialEspecifico(LocalEstoque local, List<LocalEstoqueMaterial> localEstoqueMaterial) {
        String hql = "from LocalEstoqueMaterial lem where lem.localEstoque = :local";
        if (localEstoqueMaterial != null && !localEstoqueMaterial.isEmpty()) {
            hql += " and lem not in :localEstoque";
        }
        Query q = em.createQuery(hql);
        q.setParameter("local", local);
        if (localEstoqueMaterial != null && !localEstoqueMaterial.isEmpty()) {
            q.setParameter("localEstoque", localEstoqueMaterial);
        }
        return q.getResultList();
    }


    public boolean verificarEstoque(LocalEstoque localEstoque) {
        String sql = " SELECT e.* " +
            " FROM ESTOQUE E " +
            " INNER JOIN LOCALESTOQUEORCAMENTARIO LEO ON LEO.ID = E.LOCALESTOQUEORCAMENTARIO_ID " +
            " INNER JOIN LOCALESTOQUE L ON L.ID = LEO.LOCALESTOQUE_ID " +
            " WHERE " +
            "  E.DATAESTOQUE = (SELECT max(E1.DATAESTOQUE) " +
            "                     FROM ESTOQUE E1 " +
            "                   WHERE E1.DATAESTOQUE <= SYSDATE " +
            "                   AND E1.MATERIAL_ID = E.MATERIAL_ID " +
            "                   and E1.LOCALESTOQUEORCAMENTARIO_ID = leo.id " +
            "                  ) " +
            "  AND E.FISICO > 0 " +
            "  and L.ID = :localEstoque";
        Query q = em.createNativeQuery(sql);
        q.setParameter("localEstoque", localEstoque.getId());
        return !q.getResultList().isEmpty();
    }

    public boolean materialPossuiVinculoComHierarquiaDoLocalEstoque(Material material, LocalEstoque localEstoque) throws ExcecaoNegocioGenerica {
        if (material == null) {
            throw new ExcecaoNegocioGenerica("Material nulo.");
        }

        if (localEstoque == null) {
            throw new ExcecaoNegocioGenerica("Local de Estoque nulo.");
        }

        String sql = "WITH PAI (ID, CODIGO, DESCRICAO, FECHADOEM, SUPERIOR_ID, UNIDADEORGANIZACIONAL_ID, TIPOESTOQUE) AS " +
            "  (SELECT LE_PAI.ID, " +
            "          LE_PAI.CODIGO, " +
            "          LE_PAI.DESCRICAO, " +
            "          LE_PAI.FECHADOEM, " +
            "          LE_PAI.SUPERIOR_ID, " +
            "          LE_PAI.UNIDADEORGANIZACIONAL_ID, " +
            "          LE_PAI.TIPOESTOQUE " +
            "   FROM LOCALESTOQUE LE_PAI " +
            "   WHERE LE_PAI.ID = :local_pai  " +
            "   UNION ALL SELECT LE_FILHO.ID, " +
            "                    LE_FILHO.CODIGO, " +
            "                    LE_FILHO.DESCRICAO, " +
            "                    LE_FILHO.FECHADOEM, " +
            "                    LE_FILHO.SUPERIOR_ID, " +
            "                    LE_FILHO.UNIDADEORGANIZACIONAL_ID, " +
            "                    LE_FILHO.TIPOESTOQUE " +
            "   FROM LOCALESTOQUE LE_FILHO " +
            "   INNER JOIN PAI P ON P.ID = LE_FILHO.SUPERIOR_ID) " +
            " SELECT MATERIAL.* " +
            " FROM PAI LOCALESTOQUE " +
            " INNER JOIN LOCALESTOQUEMATERIAL MATERIAL ON MATERIAL.LOCALESTOQUE_ID = LOCALESTOQUE.ID " +
            "   WHERE LOCALESTOQUE.FECHADOEM IS NULL " +
            "       AND MATERIAL.MATERIAL_ID = :mat";

        Query consulta = em.createNativeQuery(sql);

        consulta.setParameter("mat", material.getId());
        consulta.setParameter("local_pai", localEstoque.getId());

        return !consulta.getResultList().isEmpty();
    }

    public boolean materialPossuiVinculoComLocalEstoque(Material material, LocalEstoque localEstoque) {
        if (material == null) {
            throw new RuntimeException("Material nulo.");
        }

        if (localEstoque == null) {
            throw new RuntimeException("Local de Estoque nulo.");
        }

        String sql = " SELECT LEM.* " +
            " FROM LocalEstoque LE " +
            "    INNER JOIN LOCALESTOQUEMATERIAL LEM ON LEM.LOCALESTOQUE_ID = LE.ID " +
            " WHERE LEM.MATERIAL_ID = :material   " +
            "  and LE.id = :local and le.FECHADOEM IS NULL";

        Query consulta = em.createNativeQuery(sql);

        consulta.setParameter("material", material.getId());
        consulta.setParameter("local", localEstoque.getId());
        return !consulta.getResultList().isEmpty();
    }

    public boolean materialPossuiVinculoComLocalEstoqueOrcamentario(Material material, LocalEstoque localEstoque) {
        if (material == null) {
            throw new RuntimeException("Material nulo.");
        }
        if (localEstoque == null) {
            throw new RuntimeException("Local de Estoque nulo.");
        }
        String sql = " SELECT LEM.* " +
            " FROM LocalEstoque LE " +
            "    INNER JOIN LOCALESTOQUEMATERIAL LEM ON LEM.LOCALESTOQUE_ID = LE.ID " +
            "    inner join estoque est on est.MATERIAL_ID = lem.MATERIAL_ID " +
            " WHERE LEM.MATERIAL_ID = :material   " +
            "  and LE.id = :local and le.FECHADOEM IS NULL";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("material", material.getId());
        consulta.setParameter("local", localEstoque.getId());
        return !consulta.getResultList().isEmpty();
    }

    public List<LocalEstoque> completarLocalEstoqueAbertos(String parte) {
        String sql = " select obj.* from localestoque obj " +
            "           where (lower(obj.descricao) like :parte " +
            "                   or lower(replace(obj.codigo,'.','')) like :parte " +
            "                   or obj.codigo like :parte) " +
            "                  and obj.fechadoem is null " +
            "           order by obj.codigo desc ";
        Query consulta = em.createNativeQuery(sql, LocalEstoque.class);
        consulta.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        consulta.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        try {
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<LocalEstoque> buscarPorCodigoOrDescricao(String filtro) {
        String sql = " select obj.* from localestoque obj where (lower(obj.descricao) like :filtro " +
            "   or lower(replace(obj.codigo,'.','')) like :filtro or obj.codigo like :filtro) order by obj.codigo";
        Query q = em.createNativeQuery(sql, LocalEstoque.class);
        q.setParameter("filtro", "%" + filtro.toLowerCase().trim() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<LocalEstoque> completarLocalEstoqueAbertos(String parte, TipoEstoque tipoEstoque, UnidadeOrganizacional unidadeOrganizacional) {
        String hql = "select le " +
            "       from LocalEstoque le " +
            "      inner join le.unidadeOrganizacional unidade "
            + "    where (lower(le.descricao) like :parte " +
            "             or lower(le.codigo) like :parte)" +
            "        and le.fechadoEm is null" +
            "        and le.tipoEstoque = :tipo";
        hql += unidadeOrganizacional != null ? " and unidade.id = :und" : "";

        Query consulta = em.createQuery(hql);
        consulta.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        consulta.setParameter("tipo", tipoEstoque);
        if (unidadeOrganizacional != null) {
            consulta.setParameter("und", unidadeOrganizacional.getId());
        }

        try {
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<LocalEstoque> completarLocalEstoqueAbertos(String parte, UnidadeOrganizacional unidadeOrganizacional) {
        String hql = "select le " +
            "       from LocalEstoque le " +
            "      inner join le.unidadeOrganizacional unidade "
            + "    where (lower(le.descricao) like :parte " +
            "             or lower(le.codigo) like :parte)" +
            "        and le.fechadoEm is null";

        hql += unidadeOrganizacional != null ? " and unidade.id = :und" : "";

        Query consulta = em.createQuery(hql);
        consulta.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        if (unidadeOrganizacional != null) {
            consulta.setParameter("und", unidadeOrganizacional.getId());
        }

        try {
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<LocalEstoque> recuperaLocaisEstoquePontasDaUnidadeOrganizacional(String parte, UnidadeOrganizacional unidadeOrganizacional) {
        String hql = "select le from LocalEstoque le "
            + " inner join le.unidadeOrganizacional uo"
            + " where (lower(le.descricao) like :parte or lower(le.codigo) like :parte)"
            + " and not exists (select l from LocalEstoque l where l.superior = le )"
            + " and uo = :unidade";
        Query consulta = em.createQuery(hql);
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        consulta.setParameter("unidade", unidadeOrganizacional);
        try {
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<LocalEstoque> buscarLocaisEstoquePorUnidadesDistribuidoras(String parte, List<Long> unidadesDistribuidoras) {
        String hql = "select le from LocalEstoque le "
            + " inner join le.unidadeOrganizacional uo"
            + " where (lower(le.descricao) like :parte or lower(le.codigo) like :parte)"
            + " and uo.id in :unidade" +
            "   and le.superior is null";
        Query consulta = em.createQuery(hql);
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        consulta.setParameter("unidade", unidadesDistribuidoras);
        try {
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Boolean isMovimentacaoPermitida(LocalEstoque origem, LocalEstoque destino) {
        try {
            origem = recarregar(origem);
            destino = recarregar(destino);
            if (origem.isDoTipo(TipoEstoque.MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO)) {
                return tratarOrigemAlmoxarifado(destino);
            } else if (origem.isDoTipo(TipoEstoque.PRODUTOS_ELABORACAO_PRINCIPAL)) {
                return tratarOrigemElaboracao(destino);
            } else if (origem.isDoTipo(TipoEstoque.MATERIA_PRIMA_PRINCIPAL)) {
                return tratarOrigemMateriaPrima(destino);
            } else if (origem.isDoTipo(TipoEstoque.PRODUTOS_ACABADOS_PRINCIPAL)) {
                return tratarOrigemPA(destino);
            }
        } catch (NullPointerException ex) {
            throw new NullPointerException("Verifique se todos os campos foram informados.");
        } catch (Exception ex) {
            throw new NullPointerException("Verifique se todos os campos foram informados corretamente.");
        }

        return Boolean.FALSE;
    }

    private Boolean tratarOrigemAlmoxarifado(LocalEstoque destino) {
        if (destino.isDoTipo(TipoEstoque.MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO)
            || destino.isDoTipo(TipoEstoque.MATERIA_PRIMA_PRINCIPAL)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    private Boolean tratarOrigemMateriaPrima(LocalEstoque destino) {
        if (destino.isDoTipo(TipoEstoque.MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO)
            || destino.isDoTipo(TipoEstoque.MATERIA_PRIMA_PRINCIPAL)
            || destino.isDoTipo(TipoEstoque.PRODUTOS_ELABORACAO_PRINCIPAL)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    private Boolean tratarOrigemElaboracao(LocalEstoque destino) {
        if (destino.isDoTipo(TipoEstoque.PRODUTOS_ELABORACAO_PRINCIPAL)
            || destino.isDoTipo(TipoEstoque.PRODUTOS_ACABADOS_PRINCIPAL)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    private Boolean tratarOrigemPA(LocalEstoque destino) {
        if (destino.isDoTipo(TipoEstoque.PRODUTOS_ACABADOS_PRINCIPAL)
            || destino.isDoTipo(TipoEstoque.MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public List<LocalEstoque> completarLocaisEstoquePrimeiroNivel(String parte) {
        String sql = "SELECT LE.*" +
            "       FROM LOCALESTOQUE LE" +
            "      WHERE LE.SUPERIOR_ID IS NULL" +
            "        AND (LE.CODIGO LIKE :parte" +
            "             OR LOWER(LE.DESCRICAO) LIKE :parte)";

        Query q = em.createNativeQuery(sql, LocalEstoque.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");

        return q.getResultList();
    }

    public List<LocalEstoque> completarLocaisEstoquePrimeiroNivel(String parte, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = "SELECT LE.*" +
            "       FROM LOCALESTOQUE LE" +
            "      WHERE LE.SUPERIOR_ID IS NULL" +
            "        AND (LE.CODIGO LIKE :parte" +
            "             OR LOWER(LE.DESCRICAO) LIKE :parte)" +
            "        AND LE.UNIDADEORGANIZACIONAL_ID = :unidade" +
            "        and le.fechadoEm is null";

        Query q = em.createNativeQuery(sql, LocalEstoque.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("unidade", unidadeOrganizacional.getId());

        return q.getResultList();
    }

    public List<ReservaEstoque> recuperarReservasAguardandoDoLocalDeEstoque(LocalEstoque localEstoque, Long idMaterial) {
        try {
            String sql = "SELECT RE.* " +
                "       FROM RESERVAESTOQUE RE " +
                " INNER JOIN LOCALESTOQUE LE            ON LE.ID = RE.LOCALESTOQUE_ID " +
                " LEFT JOIN ITEMENTRADAMATERIAL IEM ON IEM.ID = RE.ITEMENTRADAMATERIAL_ID " +
                " LEFT JOIN ITEMAPROVACAOMATERIAL  IAM ON IAM.ID = RE.ITEMAPROVACAOMATERIAL_ID " +
                " LEFT JOIN ITEMREQUISICAOMATERIAL IRM ON IRM.ID = IAM.ITEMREQUISICAOMATERIAL_ID " +
                " INNER JOIN MATERIAL M                 ON M.ID = COALESCE(IRM.MATERIALAPROVADO_ID, IEM.MATERIAL_ID) " +
                "      WHERE RE.STATUSRESERVAESTOQUE = :status_reserva " +
                "        AND M.ID = :material_id " +
                "        AND LE.ID = :local_id";
            Query q = em.createNativeQuery(sql, ReservaEstoque.class);
            q.setParameter("status_reserva", ReservaEstoque.StatusReservaEstoque.AGUARDANDO.name());
            q.setParameter("material_id", idMaterial);
            q.setParameter("local_id", localEstoque.getId());
            return q.getResultList();
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    private List<ReservaEstoque> recuperarReservasAguardandoDaHierarquiaDoLocalDeEstoqueDesconsiderandoReservasDaAprovacao(AprovacaoMaterial am, Material m) {
        String sql = "SELECT RE.* " +
            "       FROM RESERVAESTOQUE RE " +
            " INNER JOIN LOCALESTOQUE LE ON LE.ID = RE.LOCALESTOQUE_ID " +
            " INNER JOIN ITEMAPROVACAOMATERIAL IAM ON IAM.ID = RE.ITEMAPROVACAOMATERIAL_ID " +
            " INNER JOIN ITEMREQUISICAOMATERIAL IRM ON IRM.ID = IAM.ITEMREQUISICAOMATERIAL_ID " +
            " LEFT JOIN ITEMENTRADAMATERIAL IEM ON IEM.ID = RE.ITEMENTRADAMATERIAL_ID " +
            " LEFT JOIN ITEMAPROVACAOMATERIAL IAM ON IAM.ID = RE.ITEMAPROVACAOMATERIAL_ID " +
            " LEFT JOIN ITEMREQUISICAOMATERIAL IRM ON IRM.ID = IAM.ITEMREQUISICAOMATERIAL_ID " +
            " INNER JOIN MATERIAL M ON M.ID = IRM.MATERIALAPROVADO_ID " +
            "      WHERE RE.STATUSRESERVAESTOQUE = :status_reserva " +
            "        AND M.ID = :material_id " +
            "        AND LE.ID = :local_id " ;
        if (am.getId() != null) {
            sql += " and iam.aprovacaomaterial_id != :aprovacao_id ";
        }
        Query q = em.createNativeQuery(sql, ReservaEstoque.class);
        q.setParameter("status_reserva", ReservaEstoque.StatusReservaEstoque.AGUARDANDO.name());
        q.setParameter("material_id", m.getId());
        q.setParameter("local_id", am.getRequisicaoMaterial().getLocalEstoqueOrigem().getId());
        if (am.getId() != null) {
            q.setParameter("aprovacao_id", am.getId());
        }
        return q.getResultList();
    }

    public ConsolidadorDeEstoque getNovoConsolidadorSemReservaEstoque(LocalEstoque localEstoque, Set<Material> materiais, Date dataOperacao) {
        return getNovoConsolidador(localEstoque, materiais, dataOperacao, false);
    }

    public ConsolidadorDeEstoque getNovoConsolidadorComReservaEstoque(LocalEstoque localEstoque, Set<Material> materiais, Date dataOperacao) {
        return getNovoConsolidador(localEstoque, materiais, dataOperacao, true);
    }

    public ConsolidadorDeEstoque getNovoConsolidador(LocalEstoque localEstoque, Set<Material> materiais, Date dataOperacao, Boolean consideraReserva) {
        ConsolidadorDeEstoque consolidador = new ConsolidadorDeEstoque();
        List<ReservaEstoque> reservas = Lists.newArrayList();
        for (Material material : materiais) {
            LocalEstoque localEstoquePai = recuperaRaiz(localEstoque);
            List<Estoque> estoques = estoqueFacade.recuperarEstoquesDaHierarquiaDoLocalDeEstoque(localEstoquePai, material, dataOperacao, false);
            for (Estoque e : estoques) {
                Hibernate.initialize(e.getLotesMaterial());
            }
            if (consideraReserva) {
                reservas = recuperarReservasAguardandoDoLocalDeEstoque(localEstoquePai, material.getId());
            }
            consolidador.consolidar(material, estoques, reservas);
        }
        return consolidador;
    }

    public ConsolidadorDeEstoque getNovoConsolidadorDesconsiderandoReservasDaAprovacao(AprovacaoMaterial am, List<ItemAprovacaoMaterial> itensAvaliacao) {
        ConsolidadorDeEstoque consolidador = new ConsolidadorDeEstoque();

        for (ItemAprovacaoMaterial item : itensAvaliacao) {
            List<Estoque> estoques = estoqueFacade.recuperarEstoquesDaHierarquiaDoLocalDeEstoque(
                am.getRequisicaoMaterial().getLocalEstoqueOrigem(),
                item.getMaterial(),
                sistemaFacade.getDataOperacao(),
                false);
            for (Estoque e : estoques) {
                Hibernate.initialize(e.getLotesMaterial().size());
            }
            List<ReservaEstoque> reservas = recuperarReservasAguardandoDaHierarquiaDoLocalDeEstoqueDesconsiderandoReservasDaAprovacao(am, item.getMaterial());
            consolidador.consolidar(item.getMaterial(), estoques, reservas);
        }
        return consolidador;
    }

    public LocalEstoque recuperaRaiz(LocalEstoque localEstoque) {
        if (localEstoque.getSuperior() == null) {
            return localEstoque;
        }

        while (localEstoque.getSuperior() != null) {
            localEstoque = localEstoque.getSuperior();
        }

        if (localEstoque != null && localEstoque.getId() != null) {
            localEstoque = recuperar(localEstoque.getId());
        }

        return localEstoque;
    }

    public List<LocalEstoque> buscarLocalEstoqueAbertosDaHierarquiaDoLocalDeEstoque(LocalEstoque localPai) {
        String sql = "WITH PAI (ID, CODIGO, DESCRICAO, FECHADOEM, SUPERIOR_ID, UNIDADEORGANIZACIONAL_ID) AS   " +
            " (SELECT LE_PAI.ID, LE_PAI.CODIGO, LE_PAI.DESCRICAO, LE_PAI.FECHADOEM, LE_PAI.SUPERIOR_ID, LE_PAI.UNIDADEORGANIZACIONAL_ID " +
            "    FROM LOCALESTOQUE LE_PAI   " +
            "   WHERE LE_PAI.ID = :local_pai " +
            " UNION ALL   " +
            "  SELECT LE_FILHO.ID, LE_FILHO.CODIGO, LE_FILHO.DESCRICAO, LE_FILHO.FECHADOEM, LE_FILHO.SUPERIOR_ID, LE_FILHO.UNIDADEORGANIZACIONAL_ID  " +
            "    FROM LOCALESTOQUE LE_FILHO   " +
            " INNER JOIN PAI P ON P.ID = LE_FILHO.SUPERIOR_ID)    " +
            "     SELECT LOCALESTOQUE.*   " +
            "       FROM PAI LOCALESTOQUE " +
            "      WHERE LOCALESTOQUE.FECHADOEM IS NULL ";
        Query q = em.createNativeQuery(sql, LocalEstoque.class);
        q.setParameter("local_pai", localPai.getId());
        return q.getResultList();
    }

    public List<LocalEstoque> recuperarLocalEstoqueAbertosDaHierarquiaDoLocalDeEstoque(LocalEstoque localPai, Material material, UsuarioSistema usuarioCorrente) {
        String sql = "WITH PAI (ID, CODIGO, DESCRICAO, FECHADOEM, SUPERIOR_ID, UNIDADEORGANIZACIONAL_ID, TIPOESTOQUE, UTILIZARPIN, CADASTROIMOBILIARIO_ID) AS     " +
            "(  " +
            "  SELECT " +
            "    LE_PAI.ID, " +
            "    LE_PAI.CODIGO, " +
            "    LE_PAI.DESCRICAO, " +
            "    LE_PAI.FECHADOEM, " +
            "    LE_PAI.SUPERIOR_ID, " +
            "    LE_PAI.UNIDADEORGANIZACIONAL_ID, " +
            "    LE_PAI.TIPOESTOQUE, " +
            "    LE_PAI.UTILIZARPIN, " +
            "    LE_PAI.CADASTROIMOBILIARIO_ID " +
            "  FROM " +
            "    LOCALESTOQUE LE_PAI " +
            "  WHERE " +
            "    LE_PAI.ID = :local_pai " +
            "  UNION ALL " +
            "  SELECT " +
            "    LE_FILHO.ID, " +
            "    LE_FILHO.CODIGO, " +
            "    LE_FILHO.DESCRICAO, " +
            "    LE_FILHO.FECHADOEM, " +
            "    LE_FILHO.SUPERIOR_ID, " +
            "    LE_FILHO.UNIDADEORGANIZACIONAL_ID, " +
            "    LE_FILHO.TIPOESTOQUE, " +
            "    LE_FILHO.UTILIZARPIN, " +
            "    LE_FILHO.CADASTROIMOBILIARIO_ID " +
            "  FROM " +
            "    LOCALESTOQUE LE_FILHO " +
            "  INNER JOIN PAI P " +
            "  ON " +
            "    P.ID = LE_FILHO.SUPERIOR_ID " +
            ") " +
            "SELECT " +
            "  LOCALESTOQUE.* " +
            "FROM " +
            "  PAI LOCALESTOQUE " +
            "INNER JOIN LOCALESTOQUEMATERIAL MATERIAL ON MATERIAL.LOCALESTOQUE_ID = LOCALESTOQUE.ID  " +
            "INNER JOIN GESTORLOCALESTOQUE GESTOR ON GESTOR.LOCALESTOQUE_ID = LOCALESTOQUE.ID  " +
            "WHERE " +
            "  LOCALESTOQUE.FECHADOEM IS NULL  " +
            "  AND MATERIAL.MATERIAL_ID = :mat" +
            "  AND GESTOR.USUARIOSISTEMA_ID = :usuario ";
        try {
            Query q = em.createNativeQuery(sql, LocalEstoque.class);
            q.setParameter("local_pai", localPai.getId());
            q.setParameter("mat", material.getId());
            q.setParameter("usuario", usuarioCorrente.getId());
            return q.getResultList();
        } catch (NullPointerException | NoResultException e) {
            return new ArrayList<>();
        }
    }

    public LocalEstoqueOrcamentario getLocalEstoqueOrcamentario(Map<LocalEstoque, Map<UnidadeOrganizacional, LocalEstoqueOrcamentario>> mapa,
                                                                LocalEstoque localEstoque, UnidadeOrganizacional unidadeOrcamentaria, Date dataMovimento) {
        LocalEstoqueOrcamentario localEstoqueOrc;
        try {
            localEstoqueOrc = mapa.get(localEstoque).get(unidadeOrcamentaria);
            if (localEstoqueOrc == null) {
                localEstoqueOrc = recuperarOuCriarNovoLocalEstoqueOrcamentario(localEstoque, unidadeOrcamentaria, dataMovimento);
                mapa.get(localEstoque).put(unidadeOrcamentaria, localEstoqueOrc);
            }
        } catch (NullPointerException ex) {
            localEstoqueOrc = recuperarOuCriarNovoLocalEstoqueOrcamentario(localEstoque, unidadeOrcamentaria, dataMovimento);
            Map<UnidadeOrganizacional, LocalEstoqueOrcamentario> mapaLocal = new HashMap<>();
            mapaLocal.put(unidadeOrcamentaria, localEstoqueOrc);
            mapa.put(localEstoque, mapaLocal);
        }
        return localEstoqueOrc;
    }

    public LocalEstoqueOrcamentario recuperarLocalEstoqueOrcamentario(LocalEstoque le, UnidadeOrganizacional unidadeOrcamentaria, Date dataOperacao) throws ExcecaoNegocioGenerica, ValidacaoException {
        if (hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(dataOperacao, unidadeOrcamentaria, TipoHierarquiaOrganizacional.ORCAMENTARIA) == null) {
            throw new ValidacaoException("Não foi possível recuperar a hierarquia organizacional da unidade organizacional " + unidadeOrcamentaria + ". Motivos: a unidade não é orçamentária ou não está vigênte.");
        }
        try {
            return (LocalEstoqueOrcamentario) em.createQuery("from LocalEstoqueOrcamentario where localEstoque = :local and unidadeOrcamentaria = :unidade").setParameter("local", le).setParameter("unidade", unidadeOrcamentaria).getSingleResult();
        } catch (NoResultException ex) {
            throw new ExcecaoNegocioGenerica("Não existem estoques contabilizados na unidade orçamentária " + unidadeOrcamentaria + ", no local de estoque " + le + ".");
        }
    }

    public LocalEstoqueOrcamentario recuperarOuCriarNovoLocalEstoqueOrcamentario(LocalEstoque le, UnidadeOrganizacional unidadeOrcamentaria, Date dataOperacao) throws ExcecaoNegocioGenerica, ValidacaoException {
        LocalEstoqueOrcamentario leo;
        try {
            leo = recuperarLocalEstoqueOrcamentario(le, unidadeOrcamentaria, dataOperacao);
        } catch (ExcecaoNegocioGenerica ex) {
            leo = new LocalEstoqueOrcamentario(le, unidadeOrcamentaria);
            leo = em.merge(leo);
        }
        return leo;
    }

    public List<LocalEstoque> recuperarFilhos(LocalEstoque localEstoque) {
        if (localEstoque == null || localEstoque.getId() == null) {
            return new ArrayList<>();
        }
        String sql = "select l.* " +
            "       from localestoque l " +
            "     where l.superior_id = :superior" +
            "     order by l.codigo";
        Query q = em.createNativeQuery(sql, LocalEstoque.class);
        q.setParameter("superior", localEstoque.getId());
        return q.getResultList();
    }

    public List<FormularioLocalEstoque> recuperarLocaisEstoquesPermitidosMovimentacoes(LocalEstoque localEstoquePai, List<Long> idsMateriais, GrupoMaterial grupo, UsuarioSistema usuarioCorrente, UnidadeOrganizacional orcamentaria, Date dataAtual) {
        StringBuilder sb = new StringBuilder();
        sb.append("  WITH PAI (ID, CODIGO, DESCRICAO, FECHADOEM, SUPERIOR_ID, UNIDADEORGANIZACIONAL_ID) AS       ");
        sb.append("    (SELECT LE_PAI.ID, LE_PAI.CODIGO, LE_PAI.DESCRICAO, LE_PAI.FECHADOEM, LE_PAI.SUPERIOR_ID, LE_PAI.UNIDADEORGANIZACIONAL_ID      ");
        sb.append("       FROM LOCALESTOQUE LE_PAI       ");
        sb.append("      WHERE LE_PAI.ID = :PAI   ");
        sb.append("  UNION ALL       ");
        sb.append("     SELECT LE_FILHO.ID, LE_FILHO.CODIGO, LE_FILHO.DESCRICAO, LE_FILHO.FECHADOEM, LE_FILHO.SUPERIOR_ID, LE_FILHO.UNIDADEORGANIZACIONAL_ID       ");
        sb.append("       FROM LOCALESTOQUE LE_FILHO       ");
        sb.append(" INNER JOIN PAI P ON P.ID = LE_FILHO.SUPERIOR_ID)   ");
        sb.append("     SELECT DISTINCT");
        sb.append("            LE.CODIGO,");
        sb.append("            LE.DESCRICAO,");
        sb.append("            LE.TIPOESTOQUE,");
        sb.append("            SUM(E.FISICO) AS FISICO,");
        sb.append("            0 AS DESEJADA,");
        sb.append("            SUM(E.FINANCEIRO) AS FINANCEIRO,");
        sb.append("            M.CONTROLEDELOTE,  ");
        sb.append("            LE.ID AS LOCAL,   ");
        sb.append("            UM.MASCARAQUANTIDADE AS MASCARA_QTDE, ");
        sb.append("            UM.MASCARAVALORUNITARIO AS MASCARA_VALOR, ");
        sb.append("            LEO.UNIDADEORCAMENTARIA_ID AS ID_UNIDADE_ORC ");
        sb.append("       FROM ESTOQUE E   ");
        sb.append(" INNER JOIN MATERIAL M ON M.ID = E.MATERIAL_ID");
        sb.append(" INNER JOIN UNIDADEMEDIDA UM ON M.UNIDADEMEDIDA_ID = UM.ID");
        sb.append(" INNER JOIN GRUPOMATERIAL GM ON M.GRUPO_ID = GM.ID");
        sb.append(" INNER JOIN LOCALESTOQUEORCAMENTARIO LEO ON LEO.ID = E.LOCALESTOQUEORCAMENTARIO_ID   ");
        sb.append(" INNER JOIN LOCALESTOQUE LE ON LE.ID = LEO.LOCALESTOQUE_ID   ");
        sb.append(" INNER JOIN GESTORLOCALESTOQUE GESTOR ON GESTOR.LOCALESTOQUE_ID = LE.ID ");
        sb.append(" INNER JOIN PAI P ON P.ID = LE.ID   ");
        sb.append("      WHERE trunc(:DATAATUAL)  BETWEEN trunc(GESTOR.INICIOVIGENCIA) AND COALESCE(trunc(GESTOR.FIMVIGENCIA), trunc(:DATAATUAL)) ");
        sb.append("        AND LE.FECHADOEM IS NULL   ");
        sb.append("        AND E.FISICO >= 0 ");
        sb.append("        AND E.BLOQUEADO = 0   ");
        sb.append("        AND E.DATAESTOQUE = (SELECT MAX(E2.DATAESTOQUE)   ");
        sb.append("                               FROM ESTOQUE E2   ");
        sb.append("                              WHERE E2.LOCALESTOQUEORCAMENTARIO_ID = E.LOCALESTOQUEORCAMENTARIO_ID   ");
        sb.append("                                AND E2.MATERIAL_ID = E.MATERIAL_ID  ");
        sb.append("                                AND trunc(E2.DATAESTOQUE) <= trunc(:DATAATUAL))");
        sb.append("        AND GESTOR.USUARIOSISTEMA_ID = :USUARIOCORRENTE ");
        if (!Util.isListNullOrEmpty(idsMateriais)) {
            sb.append("    AND E.MATERIAL_ID in (:idsMateriais) ");
        }
        if (grupo != null) {
            sb.append("    AND GM.ID = :GRUPOMATERIAL_ID   ");
        }
        if (orcamentaria != null) {
            sb.append("    AND LEO.UNIDADEORCAMENTARIA_ID = :ORCAMENTARIA ");
        }
        sb.append(" GROUP BY LE.CODIGO, LE.DESCRICAO, LE.TIPOESTOQUE, 5, M.CONTROLEDELOTE, LE.ID, UM.MASCARAQUANTIDADE, UM.MASCARAVALORUNITARIO, LEO.UNIDADEORCAMENTARIA_ID ");
        sb.append(" ORDER BY LE.CODIGO");
        Query q = em.createNativeQuery(sb.toString());
        try {
            q.setParameter("DATAATUAL", DataUtil.dataSemHorario(dataAtual));
            q.setParameter("PAI", localEstoquePai.getId());
            if (!Util.isListNullOrEmpty(idsMateriais)) {
                q.setParameter("idsMateriais", idsMateriais);
            }
            if (grupo != null) {
                q.setParameter("GRUPOMATERIAL_ID", grupo.getId());
            }
            q.setParameter("USUARIOCORRENTE", usuarioCorrente.getId());
            if (orcamentaria != null) {
                q.setParameter("ORCAMENTARIA", orcamentaria.getId());
            }
            return FormularioLocalEstoque.preencherFormularioApartirDaConsulta(q.getResultList());
        } catch (NoResultException | NullPointerException ex) {
            return new ArrayList<>();
        }
    }

    public LoteMaterialFacade getLoteMaterialFacade() {
        return loteMaterialFacade;
    }

    public MaterialFacade getMaterialFacade() {
        return materialFacade;
    }

    public List<LocalEstoque> completaLocaisDeEstoquePorUsuarioGestor(String filtro, UsuarioSistema usuarioSistema, Date date) {
        String sql = "SELECT " +
            " LE.*" +
            " FROM LOCALESTOQUE LE" +
            " INNER JOIN GESTORLOCALESTOQUE GE ON GE.LOCALESTOQUE_ID = LE.ID" +
            " WHERE :data BETWEEN GE.INICIOVIGENCIA AND COALESCE(GE.FIMVIGENCIA, :data)" +
            " AND GE.USUARIOSISTEMA_ID = :usu" +
            " AND LE.FECHADOEM IS NULL " +
            " AND (LOWER(LE.DESCRICAO) LIKE :parte " +
            " OR LE.CODIGO LIKE :parte)" +
            " ORDER BY LE.CODIGO";
        Query q = em.createNativeQuery(sql, LocalEstoque.class);
        q.setParameter("usu", usuarioSistema.getId());
        q.setParameter("data", date, TemporalType.DATE);
        q.setParameter("parte", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<LocalEstoque> completaLocaisDeEstoquePorUsuarioGestorAndUnidadeAdministrativa(String filtro, UsuarioSistema usuarioSistema, Date date, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = "SELECT " +
            " LE.*" +
            " FROM LOCALESTOQUE LE" +
            " INNER JOIN GESTORLOCALESTOQUE GE ON GE.LOCALESTOQUE_ID = LE.ID" +
            " WHERE :data BETWEEN GE.INICIOVIGENCIA AND COALESCE(GE.FIMVIGENCIA, :data)" +
            " AND GE.USUARIOSISTEMA_ID = :usu" +
            " AND LE.FECHADOEM IS NULL " +
            " AND (LOWER(LE.DESCRICAO) LIKE :parte " +
            " OR LE.CODIGO LIKE :parte)" +
            " and LE.UNIDADEORGANIZACIONAL_ID = :und" +
            " ORDER BY LE.CODIGO";
        Query q = em.createNativeQuery(sql, LocalEstoque.class);
        q.setParameter("usu", usuarioSistema.getId());
        q.setParameter("data", date, TemporalType.DATE);
        q.setParameter("parte", "%" + filtro.trim().toLowerCase() + "%");
        q.setParameter("und", unidadeOrganizacional.getId());
        return q.getResultList();
    }

    public Long count(String sql) {
        try {
            Query query = em.createNativeQuery(sql);
            query.setParameter("dataOperacao", DataUtil.dataSemHorario(sistemaFacade.getDataOperacao()));
            return ((BigDecimal) query.getSingleResult()).longValue();
        } catch (NoResultException nre) {
            return 0L;
        }
    }

    public String getCodigoLocalEstoque(Long idLocalEstoque) {
        String sql = " select le.codigo from LOCALESTOQUE le where le.id = :idLocalEstoque ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLocalEstoque", idLocalEstoque);
        return (String) q.getSingleResult();
    }

    public EnderecoLocalEstoque recuperarEnderecoLocalEstoque(Long id) {
        String sql = "" +
            " select " +
            "       trim(coalesce(tp.descricao,'')) || ' ' || trim(coalesce(logradouro.nome,''))," +
            "   trim(ci.numero)," +
            "   trim(ci.complementoendereco), " +
            "   trim(bairro.descricao), " +
            "   trim(face.cep)" +
            " from cadastroimobiliario ci " +
            "   inner join lote lote on lote.id = ci.lote_id " +
            "   left join quadra quadra on quadra.id = lote.quadra_id " +
            "   left join setor setor on setor.id = quadra.setor_id " +
            "   left join loteamento loteamento on loteamento.id = quadra.loteamento_id " +
            "   left join testada testadas on testadas.lote_id = lote.id " +
            "   left join face face on face.id = testadas.face_id " +
            "   left join logradourobairro logbairro on logbairro.id = face.logradourobairro_id " +
            "   left join logradouro logradouro on logradouro.id = logbairro.logradouro_id " +
            "   left join tipologradouro tp on tp.id = logradouro.tipologradouro_id " +
            "   left join bairro bairro on bairro.id = logbairro.bairro_id " +
            " where ci.id = :id ";
        Query q = em.createNativeQuery(sql).setParameter("id", id);
        if (!q.getResultList().isEmpty()) {
            Object[] ob = (Object[]) q.getResultList().get(0);
            EnderecoLocalEstoque end = new EnderecoLocalEstoque();
            end.setLogradouro((String) ob[0]);
            end.setNumero((String) ob[1]);
            end.setComplemento((String) ob[2]);
            end.setBairro((String) ob[3]);
            end.setCep((String) ob[4]);
            return end;
        }
        return null;
    }

    public void associarMaterialAoLocalEstoque(Map<LocalEstoque, Set<Material>> map) {
        for (Map.Entry<LocalEstoque, Set<Material>> entry : map.entrySet()) {
            LocalEstoque localEstoque = recuperarLocalEstoqueMateriais(entry.getKey().getId());
            List<Material> materiaisLe = materialFacade.buscarMateriaisLocalEstoque(localEstoque);

            for (Material materialMap : entry.getValue()) {
                if (!materiaisLe.contains(materialMap)) {
                    LocalEstoqueMaterial localEstoqueMaterial = new LocalEstoqueMaterial();
                    localEstoqueMaterial.setLocalEstoque(localEstoque);
                    localEstoqueMaterial.setMaterial(materialMap);
                    em.merge(localEstoqueMaterial);
                }
            }
        }
    }

    public LocalEstoqueOrcamentario buscarLocalEstoqueOrcamentario(LocalEstoque localEstoque, UnidadeOrganizacional unidade) {
        String sql = "select leo.* from localestoqueorcamentario leo where leo.localestoque_id = :idLocalEstoque " +
            "          and leo.unidadeorcamentaria_id = :idUnidade";
        Query q = em.createNativeQuery(sql, LocalEstoqueOrcamentario.class);
        q.setParameter("idLocalEstoque", localEstoque.getId());
        q.setParameter("idUnidade", unidade.getId());
        try {
            return (LocalEstoqueOrcamentario) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public EstoqueFacade getEstoqueFacade() {
        return estoqueFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public ConfiguracaoDeRelatorioFacade getConfiguracaoDeRelatorioFacade() {
        return configuracaoDeRelatorioFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }
}
