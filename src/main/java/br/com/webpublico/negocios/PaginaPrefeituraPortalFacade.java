/*
 * Codigo gerado automaticamente em Thu May 10 14:10:07 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.consultaentidade.TipoAlinhamento;
import br.com.webpublico.consultaentidade.TipoCampo;
import br.com.webpublico.controle.PaginaPortalControlador;
import br.com.webpublico.controle.portaltransparencia.entidades.PaginaPrefeituraPortal;
import br.com.webpublico.controle.portaltransparencia.entidades.TAGPortal;
import br.com.webpublico.controle.portaltransparencia.entidades.UsuarioPagina;
import br.com.webpublico.controle.portaltransparencia.entidades.UsuarioPaginaPortal;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Stateless
public class PaginaPrefeituraPortalFacade extends AbstractFacade<PaginaPrefeituraPortal> {

    public static final String PARAMETRO_WHERE = "$WHERE";

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private UsuarioPaginaPrefeituraPortalFacade usuarioPaginaPrefeituraPortalFacade;

    public PaginaPrefeituraPortalFacade() {
        super(PaginaPrefeituraPortal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void montarFieldsConsultaParaExportacaoEntidade(final PaginaPortalControlador.ConsultaPortal consultaPortal, PaginaPrefeituraPortal selecionado) {
        String consulta = consultaPortal.getConsulta();
        if (consulta.contains(PARAMETRO_WHERE)) {
            consulta = consulta.replace(PARAMETRO_WHERE, "");
        }
        consulta = consulta.replace("$" + TAGPortal.ANO.name(), Util.getAnoDaData(new Date()));
        if (selecionado.getPrefeitura() != null) {
            consulta = consulta.replace("$" + TAGPortal.PREFEITURAPORTAL_ID.name(), selecionado.getPrefeitura().getId().toString());
        }

        consulta += " OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY";
        JdbcPortalPrefeitura dao = (JdbcPortalPrefeitura) Util.getSpringBeanPeloNome("jdbcPortalPrefeitura");
        String sqlCount = consultaPortal.getCount();
        if (sqlCount.contains(PARAMETRO_WHERE)) {
            sqlCount = sqlCount.replace(PARAMETRO_WHERE, "");
        }
        sqlCount = sqlCount.replace("$" + TAGPortal.ANO.name(), Util.getAnoDaData(new Date()));
        if (selecionado.getPrefeitura() != null) {
            sqlCount = sqlCount.replace("$" + TAGPortal.PREFEITURAPORTAL_ID.name(), selecionado.getPrefeitura().getId().toString());
        }
        consultaPortal.setTotalRegistros(dao.getJdbcTemplate().queryForObject(sqlCount, Integer.class));
        consultaPortal.getFields().clear();
        dao.getJdbcTemplate()
            .queryForObject(consulta, new RowMapper<Object>() {
                @Override
                public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    for (int j = 1; j <= metaData.getColumnCount(); j++) {
                        PaginaPortalControlador.FieldConsultaPortal field = new PaginaPortalControlador.FieldConsultaPortal();
                        field.setColumnName(metaData.getColumnName(j));
                        field.setColumnValue(metaData.getColumnName(j));
                        field.setDescricao(metaData.getColumnLabel(j));
                        field.setTipo(TipoCampo.STRING);
                        field.setTipoAlinhamento(TipoAlinhamento.CENTRO);
                        if (!consultaPortal.getFields().contains(field)) {
                            field.setPosicao(consultaPortal.getFields().size() + 1);
                            consultaPortal.getFields().add(field);
                        }
                    }
                    return null;
                }
            });
    }

    @Repository(value = "jdbcPortalPrefeitura")
    public static class JdbcPortalPrefeitura extends JdbcDaoSupport implements Serializable {

        @Autowired
        public JdbcPortalPrefeitura(DataSource dataSource) {
            setDataSource(dataSource);
        }

    }

    public List<UsuarioPaginaPortal> buscarUsuarios(PaginaPrefeituraPortal pagina, UsuarioPaginaPortal usuario) {
        String sql = "select u.* from UsuarioPaginaPortal u where u.login is not null ";
        if (pagina != null) {
            sql += " and u.id in (select ug.usuario_id from UsuarioPagina ug where ug.pagina_id = :pagina) ";
        }
        if (usuario != null) {
            sql += " and u.id in (select ug.usuario_id from UsuarioPagina ug where ug.usuario_id = :usuario) ";
        }
        Query q = em.createNativeQuery(sql, UsuarioPaginaPortal.class);
        if (pagina != null) {
            q.setParameter("pagina", pagina.getId());
        }
        if (usuario != null) {
            q.setParameter("usuario", usuario.getId());
        }
        try {
            List<UsuarioPaginaPortal> resultList = (List<UsuarioPaginaPortal>) q.getResultList();
            return resultList;
        } catch (Exception e) {
            logger.error("Erro ao recuperar usuários da paginaportal ", e);
            return Lists.newArrayList();
        }
    }

    public void salvarUsuario(UsuarioPagina usuario) {
        if (usuario.getId() == null) {
            em.persist(usuario);
        } else {
            usuario = em.merge(usuario);
        }
    }

    public boolean hasPaginaComOrdemPorModulo(PaginaPrefeituraPortal pagina) {
        String sql = "select * from PAGINAPREFEITURAPORTAL where ordem = :ordem and modulo_id = :idModulo " + (pagina.getId() != null ? " and id <> :idPagina " : "");
        Query q = em.createNativeQuery(sql);
        q.setParameter("ordem", pagina.getOrdem());
        q.setParameter("idModulo", pagina.getModulo().getId());
        if (pagina.getId() != null) {
            q.setParameter("idPagina", pagina.getId());
        }
        List<Object[]> resultado = q.getResultList();
        return resultado != null && !resultado.isEmpty();
    }

    public List<PaginaPrefeituraPortal> buscarPaginas(String parte, Boolean isPaginaAnexoGeral) {
        String sql = " select pag.* from paginaprefeituraportal pag " +
            " where lower(pag.nome) like :filtro " +
            (isPaginaAnexoGeral != null ? (isPaginaAnexoGeral ? " and pag.anexogeral = 1 " : " and pag.anexogeral <> 1 ") : "");
        Query q = em.createNativeQuery(sql, PaginaPrefeituraPortal.class);
        q.setParameter("filtro", "%" + parte.toLowerCase().trim() + "%");
        return q.getResultList();
    }
}
