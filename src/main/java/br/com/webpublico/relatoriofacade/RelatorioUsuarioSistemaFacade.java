package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidadesauxiliares.comum.*;
import br.com.webpublico.enums.DiaSemana;
import br.com.webpublico.enums.ModuloSistema;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.negocios.SistemaFacade;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class RelatorioUsuarioSistemaFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public UsuarioSistema getUsuarioCorrente() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public List<UsuarioSistemaVO> buscarUsuariosSistemasVO(String condicao) {
        String sql = "select distinct * from (" +
            getSelectUsuario() +
            getFromUsuarioGrupoRecurso() + condicao +
            " union all " +
            getSelectUsuario() +
            getFromUsuarioGrupoUsuario() + condicao +
            " union all " +
            getSelectUsuario() +
            getFromUsuarioRecursoUsuario() + condicao +
            ") order by nome ";
        Query q = em.createNativeQuery(sql);
        List<Object[]> resultado = q.getResultList();
        List<UsuarioSistemaVO> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                UsuarioSistemaVO us = new UsuarioSistemaVO();
                us.setId(((BigDecimal) obj[0]).longValue());
                us.setNome((String) obj[1]);
                us.setLogin((String) obj[2]);
                us.setCpf((String) obj[3]);
                us.setSituacao(obj[4] != null ? SituacaoCadastralPessoa.valueOf((String) obj[4]).getDescricao() : "");
                us.setIdPessoa(((BigDecimal) obj[5]).longValue());
                us.setRecursos(buscarRecursosVOPorUsuario(us.getId(), condicao));
                retorno.add(us);
            }
        }
        return retorno;
    }

    public List<RecursoSistemaVO> buscarRecursosVOPorUsuario(Long idUsuario, String condicao) {
        String sql = "select distinct * from (" +
            getSelectRecurso() +
            getFromUsuarioGrupoRecurso() + condicao +
            ((condicao != null && !condicao.isEmpty()) ? " and " : " where ") + " us.id = :idUsuario " +
            " union all " +
            getSelectRecurso() +
            getFromUsuarioGrupoUsuario() + condicao +
            ((condicao != null && !condicao.isEmpty()) ? " and " : " where ") + " us.id = :idUsuario " +
            " union all " +
            getSelectRecurso() +
            getFromUsuarioRecursoUsuario() + condicao +
            ((condicao != null && !condicao.isEmpty()) ? " and " : " where ") + " us.id = :idUsuario " +
            ") order by recurso";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idUsuario", idUsuario);
        List<Object[]> resultado = q.getResultList();
        List<RecursoSistemaVO> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                RecursoSistemaVO rec = new RecursoSistemaVO();
                rec.setNome((String) obj[0]);
                rec.setCaminho((String) obj[1]);
                rec.setId(((BigDecimal) obj[2]).longValue());
                retorno.add(rec);
            }
        }
        return retorno;
    }

    public List<GrupoUsuarioVO> buscarGruposUsuariosSistemasVO(String condicao) {
        String sql = "select distinct gu.id, trim(gu.nome) as nome " +
            getFromUsuarioGrupoUsuario() + condicao +
            "order by trim(gu.nome)";
        Query q = em.createNativeQuery(sql);
        List<Object[]> resultado = q.getResultList();
        List<GrupoUsuarioVO> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                GrupoUsuarioVO gu = new GrupoUsuarioVO();
                gu.setId(((BigDecimal) obj[0]).longValue());
                gu.setNome((String) obj[1]);
                gu.setPeriodos(buscarPeriodosPorGrupoUsuario(gu.getId()));
                retorno.add(gu);
            }
        }
        return retorno;
    }

    public List<PeriodoVO> buscarPeriodosPorGrupoUsuario(Long idGrupo) {
        String sql = " select distinct listagg(p.diadasemana, ' ')  within group (order by p.diadasemana) as diasDaSemana, " +
            "       to_char(p.inicio, 'HH24:mi:ss') as inicio, " +
            "       to_char(p.termino, 'HH24:mi:ss') as termino " +
            " from grupousuarioperiodo gp " +
            "    inner join periodo p on p.id = gp.periodo_id " +
            " where gp.grupousuario_id = :idGrupo " +
            " group by to_char(p.inicio, 'HH24:mi:ss'), to_char(p.termino, 'HH24:mi:ss') ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idGrupo", idGrupo);
        List<Object[]> resultado = q.getResultList();
        List<PeriodoVO> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                PeriodoVO periodo = new PeriodoVO();
                periodo.setPeriodo(DiaSemana.converterDiaDaSemanaNumeroParaSiglaDaSemana((String) obj[0]));
                periodo.setInicio((String) obj[1]);
                periodo.setTermino((String) obj[2]);
                retorno.add(periodo);
            }
        }
        return retorno;
    }

    public List<GrupoRecursoVO> buscarGruposRecursosSistemasVO(String condicao) {
        String sql = "select distinct gr.id, trim(gr.nome) as nome, gr.moduloSistema " +
            getFromUsuarioGrupoRecurso() + condicao +
            "order by trim(gr.nome)";
        Query q = em.createNativeQuery(sql);
        List<Object[]> resultado = q.getResultList();
        List<GrupoRecursoVO> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                GrupoRecursoVO gr = new GrupoRecursoVO();
                gr.setId(((BigDecimal) obj[0]).longValue());
                gr.setNome((String) obj[1]);
                gr.setModulo(obj[2] != null ? ModuloSistema.valueOf((String) obj[2]).getDescricao() : "");
                retorno.add(gr);
            }
        }
        return retorno;
    }

    private String getSelectUsuario() {
        return " select us.id," +
            "           pf.nome, " +
            "           us.login, " +
            "           formatacpfcnpj(pf.cpf) as cpf, " +
            "           ps.situacaocadastralpessoa as situacao, " +
            "           pf.id as idPessoa ";
    }

    private String getSelectRecurso() {
        return " select rs.nome as recurso, " +
            "            rs.caminho, " +
            "            rs.id ";
    }

    private String getFromUsuarioGrupoRecurso() {
        return "from usuariosistema us " +
            "    inner join pessoafisica pf on us.pessoafisica_id = pf.id " +
            "    inner join pessoa ps on ps.id = pf.id " +
            "    inner join gruporecursosusuario gru on gru.usuariosistema_id = us.id " +
            "    inner join gruporecurso gr on gr.id = gru.gruporecurso_id " +
            "    inner join gruporecursosistema grs on grs.gruporecurso_id = gr.id " +
            "    inner join recursosistema rs on rs.id = grs.recursosistema_id ";
    }

    private String getFromUsuarioGrupoUsuario() {
        return "from usuariosistema us " +
            "         inner join pessoafisica pf on us.pessoafisica_id = pf.id " +
            "         inner join pessoa ps on ps.id = pf.id " +
            "         inner join grupousuariosistema guu on guu.usuariosistema_id = us.id " +
            "         inner join grupousuario gu on gu.id = guu.grupousuario_id " +
            "         inner join itemgrupousuario igu on igu.grupousuario_id = gu.id " +
            "         inner join gruporecurso grgu on grgu.id = igu.gruporecurso_id " +
            "         inner join gruporecursosistema grsgu on grsgu.gruporecurso_id = grgu.id " +
            "         inner join recursosistema rs on rs.id = grsgu.recursosistema_id ";
    }

    private String getFromUsuarioRecursoUsuario() {
        return "from usuariosistema us " +
            "         inner join pessoafisica pf on us.pessoafisica_id = pf.id " +
            "         inner join pessoa ps on ps.id = pf.id " +
            "         inner join recursosusuario ru on ru.usuariosistema_id = us.id " +
            "         inner join recursosistema rs on rs.id = ru.recursosistema_id ";
    }
}
