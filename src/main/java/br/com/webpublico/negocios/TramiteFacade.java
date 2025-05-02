/*
 * Codigo gerado automaticamente em Fri Mar 11 14:04:10 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltrosPesquisaProtocolo;
import br.com.webpublico.entidadesauxiliares.TramitacaoProtocolo;
import br.com.webpublico.entidadesauxiliares.VoProcesso;
import br.com.webpublico.entidadesauxiliares.VoTramite;
import br.com.webpublico.entidadesauxiliares.administrativo.FiltroListaProtocolo;
import br.com.webpublico.enums.SituacaoProcesso;
import br.com.webpublico.enums.StatusProcessoEnglobado;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoProcessoTramite;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class TramiteFacade extends AbstractFacade<Tramite> {

    private static final Logger logger = LoggerFactory.getLogger(TramiteFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ProcessoFacade processoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private SingletonHierarquiaOrganizacional singletonHO;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private SituacaoTramiteFacade situacaoTramiteFacade;
    @EJB
    private AssuntoFacade assuntoFacade;

    public TramiteFacade() {
        super(Tramite.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Tramite> listaPendentesUnidade(UsuarioSistema usu) {
        StringBuilder sql = new StringBuilder("select tra.* from Tramite tra ")
            .append(" where tra.aceito = :aceito ")
            .append(" and tra.status = :status ")
            .append(" and tra.unidadeorganizacional_id in (select uo.id from usuariosistema usuSis ")
            .append(" inner join UsuarioUnidadeOrganizacio usuuni on ususis.id = usuuni.usuariosistema_id ")
            .append(" inner join unidadeorganizacional uo on usuuni.unidadeorganizacional_id = uo.id ")
            .append(" where ususis.id = :usuario_id ")
            .append(" and usuuni.gestorProtocolo = :gestor) ")
            .append(" order by tra.processo_id desc");
        Query q = em.createNativeQuery(sql.toString(), Tramite.class);
        q.setParameter("usuario_id", usu.getId());
        q.setParameter("aceito", "0");
        q.setParameter("status", "1");
        q.setParameter("gestor", "1");
        return q.getResultList();
    }

    public List<Tramite> listaPorProcesso(Processo pro) {
        String sql = "select t.* from tramite t where t.processo_id = :pro order by t.indice asc";
        Query q = em.createNativeQuery(sql, Tramite.class);
        q.setParameter("pro", pro.getId());
        return q.getResultList();

    }

    public Tramite buscarUltimoTramite(Long idProcesso) {
        String sql = "select tr.* " +
            " from tramite tr " +
            " where tr.processo_id = :idProcesso " +
            " and tr.indice = (select max(indice) from tramite tramite where tr.processo_id = tramite.processo_id) ";
        Query q = em.createNativeQuery(sql, Tramite.class);
        q.setParameter("idProcesso", idProcesso);
        try {
            return (Tramite) q.getSingleResult();
        } catch (NoResultException nre) {
            throw new ValidacaoException("Não foi possível recuperar o último trâmite do processo.");
        }
    }

    public List<Tramite> listaUsuario(UsuarioSistema usu) {
        StringBuilder sql = new StringBuilder("select tra.* from tramite tra ")
            .append(" where tra.aceito = :aceito ")
            .append(" and tra.status = :status ")
            .append(" and tra.unidadeorganizacional_id in (select uo.id from usuariosistema usuSis ")
            .append(" inner join UsuarioUnidadeOrganizacio usuuni on ususis.id = usuuni.usuariosistema_id ")
            .append(" inner join unidadeorganizacional uo on usuuni.unidadeorganizacional_id = uo.id ")
            .append(" where ususis.id = :usuario_id ")
            .append(" and usuuni.gestorProtocolo = :gestor) ")
            .append(" order by tra.processo_id desc ");
        Query q = em.createNativeQuery(sql.toString(), Tramite.class);
        q.setParameter("usuario_id", usu.getId());
        q.setParameter("aceito", "1");
        q.setParameter("status", "1");
        q.setParameter("gestor", "1");
        return q.getResultList();
    }

    @Override
    public Tramite recuperar(Object id) {
        Tramite t = em.find(Tramite.class, id);
        t.getProcesso().getTramites().size();
        t.getProcesso().getProcessosEnglobados().size();
        t.getProcesso().getHistoricos().size();
        return t;
    }

    public List<VoTramite> buscarProtocoloAceitoRecebidoPorUsuario(FiltroListaProtocolo filtro, int inicio, int max) {
        StringBuilder sql = new StringBuilder("");
        sql.append(" select distinct  ")
            .append("  tra.id, ")
            .append("  tra.dataaceite, ")
            .append("  tra.indice, ")
            .append("  tra.responsavel, ")
            .append("  tra.motivo, ")
            .append("  to_char(tra.observacoes) as observacao, ")
            .append("  ho.codigo || ' - ' || ho.descricao as destino, ")
            .append("  uni.id as unidadeid, ")
            .append("  pro.id as idprocesso, ")
            .append("  pro.numero, ")
            .append("  pro.ano, ")
            .append("  pro.dataregistro, ")
            .append("  coalesce(pf.nome, pj.razaosocial) as autor, ")
            .append("  pro.assunto, ")
            .append("  pro.situacao, ")
            .append("  sa.nome as nomesub, ")
            .append("  ass.nome")
            .append(" from Tramite tra ");
        Query q = getQueryProtocoloAceitos(filtro, inicio, max, sql);
        return montarVoTramites(q.getResultList());
    }

    public Integer quantidadeTotalDeTramitesAceitos(FiltroListaProtocolo filtro) {
        StringBuilder sql = new StringBuilder("select count(distinct tra.id) from Tramite tra ");
        Query q = getQueryProtocoloAceitos(filtro, 0, 0, sql);
        return ((BigDecimal) q.getSingleResult()).intValue();
    }

    private Query getQueryProtocoloAceitos(FiltroListaProtocolo filtro, int inicio, int max, StringBuilder sql) {
        sql.append("  inner join Processo pro on tra.processo_id = pro.id ")
            .append(" inner join UnidadeOrganizacional uni on uni.id = tra.unidadeorganizacional_id ")
            .append(" inner join hierarquiaorganizacional ho on ho.subordinada_id = uni.id ")
            .append("    and ho.tipohierarquiaorganizacional  = :tipoHierarquia ")
            .append("    and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))")
            .append(" left join subAssunto sa on sa.id = pro.subAssunto_id ")
            .append(" left join assunto ass on ass.id = sa.assunto_id ")
            .append(" left join pessoafisica pf on pf.id = pro.pessoa_id ")
            .append(" left join pessoajuridica pj on pj.id = pro.pessoa_id ")
            .append(" where tra.aceito = :aceito ")
            .append(" and tra.status = :status ")
            .append(" and tra.unidadeOrganizacional_id in (select uo.id from UsuarioSistema usuSis ")
            .append(" inner join UsuarioUnidadeOrganizacio usuuni on ususis.id = usuuni.usuarioSistema_id ")
            .append(" inner join UnidadeOrganizacional uo on usuuni.unidadeOrganizacional_id = uo.id ")
            .append(" where ususis.id = :usuario_id ")
            .append(" and usuuni.gestorProtocolo = :gestor)")
            .append(" and pro.protocolo = :protocolo ")
            .append(" and tra.indice = (select max(indice) from Tramite tramite where pro.id = tramite.processo_id) ");
        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            sql.append(" and pro.numero = :numero ");
        }
        if (!Strings.isNullOrEmpty(filtro.getAno())) {
            sql.append(" and pro.ano = :ano ");
        }
        if (!Strings.isNullOrEmpty(filtro.getSolicitante())) {
            sql.append(" and (pro.pessoa_id in(select pf.id from PessoaFisica pf where lower(pf.nome) like :solicitante or pf.cpf like :solicitante) ")
                .append(" or pro.pessoa_id in(select pj.id from PessoaJuridica pj where lower(pj.razaoSocial) like :solicitante or pj.cnpj like :solicitante))");
        }
        sql.append(" order by pro.id desc ");
        Query q = em.createNativeQuery(sql.toString());
        if (max != 0) {
            q.setMaxResults(max);
            q.setFirstResult(inicio);
        }
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("usuario_id", filtro.getUsuarioSistema().getId());
        q.setParameter("aceito", filtro.getAceito());
        q.setParameter("status", filtro.getStatus());
        q.setParameter("gestor", filtro.getGestor());
        q.setParameter("protocolo", filtro.getProtocolo());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(filtro.getDataOperacao()));
        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            q.setParameter("numero", filtro.getNumero().trim());
        }
        if (!Strings.isNullOrEmpty(filtro.getAno())) {
            q.setParameter("ano", filtro.getAno());
        }
        if (!Strings.isNullOrEmpty(filtro.getSolicitante())) {
            q.setParameter("solicitante", "%" + filtro.getSolicitante().toLowerCase() + "%");
        }
        return q;
    }

    public List<VoTramite> buscarTramitesPorUsuarioAndUnidade(FiltroListaProtocolo filtro, int inicio, int max) {
        StringBuilder sql = new StringBuilder("");
        sql.append(" select distinct  ")
            .append("  tra.id, ")
            .append("  tra.dataaceite, ")
            .append("  tra.indice, ")
            .append("  tra.responsavel, ")
            .append("  tra.motivo, ")
            .append("  to_char(tra.observacoes) as observacao, ")
            .append("  ho.codigo || ' - ' || ho.descricao as destino, ")
            .append("  tra.unidadeorganizacional_id as unidadeid, ")
            .append("  pro.id as idprocesso, ")
            .append("  pro.numero, ")
            .append("  pro.ano, ")
            .append("  pro.dataregistro, ")
            .append("  coalesce(pf.nome, pj.razaosocial) as autor, ")
            .append("  pro.assunto, ")
            .append("  pro.situacao, ")
            .append("  sa.nome as nomesub, ")
            .append("  ass.nome ");
        Query q = getSelectTramitesPorUsuarioAndUnidade(filtro, inicio, max, sql);
        return montarVoTramites(q.getResultList());
    }

    public Integer quantidadeDeTramitesPorUsuarioAndUnidade(FiltroListaProtocolo filtro) {
        StringBuilder sql = new StringBuilder("select count(distinct tra.id) ");
        Query q = getSelectTramitesPorUsuarioAndUnidade(filtro, 0, 0, sql);
        return ((BigDecimal) q.getSingleResult()).intValue();
    }

    private Query getSelectTramitesPorUsuarioAndUnidade(FiltroListaProtocolo filtro, int inicio, int max, StringBuilder sql) {
        sql.append(" from Tramite tra ")
            .append(" inner join Processo pro on tra.processo_id = pro.id ")
            .append(" inner join hierarquiaorganizacional ho on ho.subordinada_id = tra.unidadeorganizacional_id ")
            .append("    and ho.tipohierarquiaorganizacional  = :tipoHierarquia ")
            .append("    and sysdate between ho.iniciovigencia and coalesce(ho.fimvigencia, sysdate)")
            .append(" left join subAssunto sa on sa.id = pro.subAssunto_id ")
            .append(" left join assunto ass on ass.id = sa.assunto_id ")
            .append(" left join pessoafisica pf on pf.id = pro.pessoa_id ")
            .append(" left join pessoajuridica pj on pj.id = pro.pessoa_id ")
            .append(" where tra.aceito = :aceito ")
            .append(" and tra.status = :status ")
            .append(" and not exists(select 1 from ProcessoEnglobado proeng where proeng.englobado_id = pro.id) ")
            .append(" and tra.unidadeOrganizacional_id in (select uo.id from UsuarioSistema usuSis ")
            .append(" inner join UsuarioUnidadeOrganizacio usuuni on ususis.id = usuuni.usuarioSistema_id ")
            .append(" inner join UnidadeOrganizacional uo on usuuni.unidadeOrganizacional_id = uo.id ")
            .append(" where ususis.id = :usuario_id ")
            .append(" and usuuni.gestorProtocolo = :gestor)")
            .append(" and pro.protocolo = :protocolo ");
        if (filtro.getUnidadeOrganizacional() != null && filtro.getUnidadeOrganizacional().getId() != null) {
            sql.append(" and tra.unidadeOrganizacional_id = :idUnidade ");
        }
        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            sql.append(" and pro.numero = :numero ");
        }
        if (!Strings.isNullOrEmpty(filtro.getAno())) {
            sql.append(" and pro.ano = :ano ");
        }
        if (!Strings.isNullOrEmpty(filtro.getSolicitante())) {
            sql.append(" and (pro.pessoa_id in(select pf.id from PessoaFisica pf where lower(pf.nome) like :solicitante or pf.cpf like :solicitante) ")
                .append(" or pro.pessoa_id in(select pj.id from PessoaJuridica pj where lower(pj.razaoSocial) like :solicitante or pj.cnpj like :solicitante))");
        }
        if (filtro.getTramite().getId() != null) {
            sql.append(" and tra.id != :tramiteId ");
        }
        sql.append(" order by pro.id desc ");
        Query q = em.createNativeQuery(sql.toString());
        if (max != 0) {
            q.setMaxResults(max);
            q.setFirstResult(inicio);
        }
        q.setParameter("usuario_id", filtro.getUsuarioSistema().getId());
        q.setParameter("aceito", filtro.getAceito());
        q.setParameter("status", filtro.getStatus());
        q.setParameter("gestor", filtro.getGestor());
        q.setParameter("protocolo", filtro.getProtocolo());
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        if (filtro.getUnidadeOrganizacional() != null && filtro.getUnidadeOrganizacional().getId() != null) {
            q.setParameter("idUnidade", filtro.getUnidadeOrganizacional().getId());
        }
        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            q.setParameter("numero", filtro.getNumero().trim());
        }
        if (!Strings.isNullOrEmpty(filtro.getAno())) {
            q.setParameter("ano", filtro.getAno());
        }
        if (!Strings.isNullOrEmpty(filtro.getSolicitante())) {
            q.setParameter("solicitante", "%" + filtro.getSolicitante().toLowerCase() + "%");
        }
        if (filtro.getTramite().getId() != null) {
            q.setParameter("tramiteId", filtro.getTramite().getId());
        }
        return q;
    }

    public List<Tramite> buscarTramitesCompletosPorUsuarioProcessoProtocolo(UsuarioSistema usu, Boolean protocolo, String numero, String ano, String solicitante) {
        StringBuilder sql = new StringBuilder("select tra.* from Tramite tra ")
            .append(" inner join Processo pro on tra.processo_id = pro.id ")
            .append(" inner join UnidadeOrganizacional uni on uni.id = tra.unidadeorganizacional_id ")
            .append(" left join subAssunto sa on sa.id = pro.subAssunto_id ")
            .append(" left join assunto ass on ass.id = sa.assunto_id ")
            .append(" left join pessoafisica pf on pf.id = pro.pessoa_id ")
            .append(" left join pessoajuridica pj on pj.id = pro.pessoa_id ")
            .append(" where tra.aceito = :aceito ")
            .append(" and tra.status = :status ")
            .append(" and tra.unidadeOrganizacional_id in (select uo.id from UsuarioSistema usuSis ")
            .append(" inner join UsuarioUnidadeOrganizacio usuuni on ususis.id = usuuni.usuarioSistema_id ")
            .append(" inner join UnidadeOrganizacional uo on usuuni.unidadeOrganizacional_id = uo.id ")
            .append(" where ususis.id = :usuario_id ")
            .append(" and usuuni.gestorProtocolo = :gestor)")
            .append(" and pro.protocolo = :protocolo ");
        if (numero != null && !"".equals(numero)) {
            sql.append(" and pro.numero = :numero ");
        }
        if (ano != null && !"".equals(ano)) {
            sql.append(" and pro.ano = :ano ");
        }
        if (solicitante != null && !"".equals(solicitante.trim())) {
            sql.append(" and (pro.pessoa_id in(select pf.id from PessoaFisica pf where lower(pf.nome) like :solicitante or pf.cpf like :solicitante) ")
                .append(" or pro.pessoa_id in(select pj.id from PessoaJuridica pj where lower(pj.razaoSocial) like :solicitante or pj.cnpj like :solicitante))");
        }
        sql.append(" order by pro.id desc ");
        Query q = em.createNativeQuery(sql.toString(), Tramite.class);
        q.setParameter("usuario_id", usu.getId());
        q.setParameter("aceito", "1");
        q.setParameter("status", "1");
        q.setParameter("gestor", "1");
        if (protocolo) {
            q.setParameter("protocolo", 1);
        } else {
            q.setParameter("protocolo", 0);
        }
        if (numero != null && !"".equals(numero)) {
            q.setParameter("numero", numero);
        }
        if (ano != null && !"".equals(ano)) {
            q.setParameter("ano", ano);
        }
        if (solicitante != null && !"".equals(solicitante.trim())) {
            q.setParameter("solicitante", "%" + solicitante.toLowerCase() + "%");
        }
        return q.getResultList();
    }

    public List<VoTramite> buscarTramitesPendentesUnidadeProcessoProtocolo(FiltroListaProtocolo filtro, int inicio, int max) {
        StringBuilder sql = new StringBuilder("");
        sql.append(" select distinct  ")
            .append("  tra.id, ")
            .append("  tra.dataaceite, ")
            .append("  tra.indice, ")
            .append("  tra.responsavel, ")
            .append("  tra.motivo, ")
            .append("  to_char(tra.observacoes) as observacao, ")
            .append("  ho.codigo || ' - ' || ho.descricao as destino, ")
            .append("  uni.id as unidadeid, ")
            .append("  pro.id as idprocesso, ")
            .append("  pro.numero, ")
            .append("  pro.ano, ")
            .append("  pro.dataregistro, ")
            .append("  coalesce(pf.nome, pj.razaosocial) as autor, ")
            .append("  pro.assunto, ")
            .append("  pro.situacao, ")
            .append("  sa.nome as nomesub, ")
            .append("  ass.nome")
            .append(" from Tramite tra ");
        Query q = getQueryTramitesPendentes(filtro, inicio, max, sql);
        return montarVoTramites(q.getResultList());
    }

    public Integer quantidadeTotalDeTramitesPendentes(FiltroListaProtocolo filtro) throws ExcecaoNegocioGenerica {
        try {
            StringBuilder sql = new StringBuilder("select count(distinct tra.id) from Tramite tra  ");
            Query q = getQueryTramitesPendentes(filtro, 0, 0, sql);
            return ((BigDecimal) q.getSingleResult()).intValue();
        } catch (NoResultException nr) {
            throw new ExcecaoNegocioGenerica(nr.getMessage());
        }
    }

    private Query getQueryTramitesPendentes(FiltroListaProtocolo filtro, int inicio, int max, StringBuilder sql) {
        sql.append("  inner join Processo pro on tra.processo_id = pro.id ")
            .append(" inner join UnidadeOrganizacional uni on uni.id = tra.unidadeorganizacional_id ")
            .append(" inner join hierarquiaorganizacional ho on ho.subordinada_id = uni.id ")
            .append("    and ho.tipohierarquiaorganizacional  = :tipoHierarquia ")
            .append("    and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))")
            .append(" left join pessoafisica pf on pf.id = pro.pessoa_id ")
            .append(" left join pessoajuridica pj on pj.id = pro.pessoa_id ")
            .append(" left join subAssunto sa on sa.id = pro.subAssunto_id ")
            .append(" left join assunto ass on ass.id = sa.assunto_id ")
            .append(" where tra.aceito = :aceito ")
            .append(" and tra.status = :status ")
            .append(" and tra.unidadeOrganizacional_id in (select uo.id from UsuarioSistema usuSis ")
            .append(" inner join UsuarioUnidadeOrganizacio usuuni on ususis.id = usuuni.usuarioSistema_id ")
            .append(" inner join UnidadeOrganizacional uo on usuuni.unidadeOrganizacional_id = uo.id ")
            .append(" where ususis.id = :usuario_id ")
            .append(" and usuuni.gestorProtocolo = :gestor)")
            .append(" and pro.protocolo = :protocolo ")
            .append(" and pro.situacao <> :englobado ");
        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            sql.append(" and pro.numero = :numero ");
        }
        if (!Strings.isNullOrEmpty(filtro.getAno())) {
            sql.append(" and pro.ano = :ano ");
        }
        if (!Strings.isNullOrEmpty(filtro.getSolicitante())) {
            sql.append(" and (pro.pessoa_id in(select pf.id from PessoaFisica pf where lower(pf.nome) like :solicitante or pf.cpf like :solicitante) ")
                .append(" or pro.pessoa_id in(select pj.id from PessoaJuridica pj where lower(pj.razaoSocial) like :solicitante or pj.cnpj like :solicitante))");
        }
        sql.append(" order by pro.id desc ");
        Query q = em.createNativeQuery(sql.toString());
        if (max != 0) {
            q.setMaxResults(max);
            q.setFirstResult(inicio);
        }
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("usuario_id", filtro.getUsuarioSistema().getId());
        q.setParameter("aceito", filtro.getAceito());
        q.setParameter("status", filtro.getStatus());
        q.setParameter("gestor", filtro.getGestor());
        q.setParameter("englobado", SituacaoProcesso.INCORPORADO.name());
        q.setParameter("protocolo", filtro.getProtocolo());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(filtro.getDataOperacao()));

        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            q.setParameter("numero", filtro.getNumero().trim());
        }
        if (!Strings.isNullOrEmpty(filtro.getAno())) {
            q.setParameter("ano", filtro.getAno());
        }
        if (!Strings.isNullOrEmpty(filtro.getSolicitante())) {
            q.setParameter("solicitante", "%" + filtro.getSolicitante().toLowerCase() + "%");
        }
        return q;
    }

    public List<VoTramite> buscarProtocolosEmUnidadesExtintas(FiltroListaProtocolo filtro, int inicio, int max) throws ExcecaoNegocioGenerica {
        try {
            StringBuilder sql = new StringBuilder("");
            sql.append(" select distinct ")
                .append("    tra.id as idTramite,  ")
                .append("    tra.dataaceite,  ")
                .append("    uni.id as unidadeid,  ")
                .append("    tra.destinoexterno, ")
                .append("    tra.dataregistro as dataRegistroTramite, ")
                .append("    pro.id as idprocesso,  ")
                .append("    pro.numero,  ")
                .append("    pro.ano,  ")
                .append("    pro.dataregistro,  ")
                .append("    coalesce(pf.nome, pj.razaosocial) as autor,  ")
                .append("    pro.assunto,  ")
                .append("    pro.situacao ")
                .append("from tramite tra  ")
                .append("   inner join processo pro on tra.processo_id = pro.id  ")
                .append("   inner join unidadeorganizacional uni on uni.id = tra.unidadeorganizacional_id  ")
                .append("   left join pessoafisica pf on pf.id = pro.pessoa_id  ")
                .append("   left join pessoajuridica pj on pj.id = pro.pessoa_id  ")
                .append("   left join subassunto sa on sa.id = pro.subassunto_id  ")
                .append("   left join assunto ass on ass.id = sa.assunto_id  ")
                .append("where tra.status = :status  ")
                .append("   and pro.protocolo = :protocolo ")
                .append("   and uni.id not in (select uo.id from hierarquiaorganizacional ho  ")
                .append("   inner join unidadeorganizacional uo on ho.subordinada_id = uo.id  ")
                .append("   where :dataOperacao between ho.iniciovigencia  and coalesce(ho.fimvigencia, :dataOperacao)) ");
            if (!Strings.isNullOrEmpty(filtro.getNumero())) {
                sql.append(" and pro.numero = :numero ");
            }
            if (!Strings.isNullOrEmpty(filtro.getAno())) {
                sql.append(" and pro.ano = :ano ");
            }
            if (!Strings.isNullOrEmpty(filtro.getSolicitante())) {
                sql.append(" and (pro.pessoa_id in(select pf.id from PessoaFisica pf where lower(pf.nome) like :solicitante or pf.cpf like :solicitante) ")
                    .append(" or pro.pessoa_id in(select pj.id from PessoaJuridica pj where lower(pj.razaoSocial) like :solicitante or pj.cnpj like :solicitante))");
            }
            sql.append(" order by pro.id desc ");
            Query q = em.createNativeQuery(sql.toString());
            if (max != 0) {
                q.setMaxResults(max + 1);
                q.setFirstResult(inicio);
            }
            q.setParameter("status", filtro.getStatus());
            q.setParameter("protocolo", filtro.getProtocolo());
            q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
            if (!Strings.isNullOrEmpty(filtro.getNumero())) {
                q.setParameter("numero", filtro.getNumero().trim());
            }
            if (!Strings.isNullOrEmpty(filtro.getAno())) {
                q.setParameter("ano", filtro.getAno());
            }
            if (!Strings.isNullOrEmpty(filtro.getSolicitante())) {
                q.setParameter("solicitante", "%" + filtro.getSolicitante().toLowerCase() + "%");
            }
            return montarVoTramitesUnidadesExtintas(q.getResultList());
        } catch (NoResultException nr) {
            throw new ExcecaoNegocioGenerica(nr.getMessage());
        }
    }

    public List<VoTramite> buscarTramitesExternoRecebidos(FiltroListaProtocolo filtro, int inicio, int max) {
        StringBuilder sql = new StringBuilder("");
        sql.append(" select distinct  ")
            .append("  tra.id,  ")
            .append("  tra.destinoexterno, ")
            .append("  tra.dataaceite, ")
            .append("  pro.id as idprocesso, ")
            .append("  pro.numero, ")
            .append("  pro.ano, ")
            .append("  pro.dataregistro, ")
            .append("  coalesce(pf.cpf, pj.cnpj) || ' -  '|| coalesce(pf.nome, pj.razaosocial) as autor,   ")
            .append("  pro.assunto, ")
            .append("  pro.situacao ")
            .append(" from Tramite tra ");
        Query q = getQueryTramitesExternos(filtro, inicio, max, sql);
        return montarVoTramitesProcessoExterno(q.getResultList());
    }

    public Integer quantidadeTotalTramitesExternosRecebidos(FiltroListaProtocolo filtro) {
        StringBuilder sql = new StringBuilder(" select count(1) from Tramite tra ");
        Query q = getQueryTramitesExternos(filtro, 0, 0, sql);
        return ((BigDecimal) q.getSingleResult()).intValue();
    }

    public List<VoTramite> buscarTramitesExternoPendentes(FiltroListaProtocolo filtro, int inicio, int max) {
        StringBuilder sql = new StringBuilder("");
        sql.append(" select distinct  ")
            .append("  tra.id,  ")
            .append("  tra.destinoexterno, ")
            .append("  null as dataAceite, ")
            .append("  pro.id as idprocesso, ")
            .append("  pro.numero, ")
            .append("  pro.ano, ")
            .append("  pro.dataregistro, ")
            .append("  coalesce(pf.cpf, pj.cnpj) || ' -  '|| coalesce(pf.nome, pj.razaosocial) as autor,   ")
            .append("  pro.assunto, ")
            .append("  pro.situacao ")
            .append(" from Tramite tra ");
        Query q = getQueryTramitesExternos(filtro, inicio, max, sql);
        return montarVoTramitesProcessoExterno(q.getResultList());
    }

    public Integer quantidadeTotalTramitesExternosPendentes(FiltroListaProtocolo filtro) throws ExcecaoNegocioGenerica {
        StringBuilder sql = new StringBuilder("select count(1) from Tramite tra ");
        Query q = getQueryTramitesExternos(filtro, 0, 0, sql);
        return ((BigDecimal) q.getSingleResult()).intValue();
    }

    private Query getQueryTramitesExternos(FiltroListaProtocolo filtro, int inicio, int max, StringBuilder sql) {
        sql.append(" inner join Processo pro on tra.processo_id = pro.id ")
            .append(" inner join pessoa p on p.id = pro.pessoa_id ")
            .append(" left join pessoafisica pf on pf.id = p.id ")
            .append(" left join pessoajuridica pj on pj.id = p.id ")
            .append(" where tra.aceito = :aceito ")
            .append(" and tra.status = :status ")
            .append(" and tra.unidadeOrganizacional_id is null ")
            .append(" and tra.destinoExterno is not null  ")
            .append(" and tra.origem_id in (select uo.id from UsuarioSistema usuSis ")
            .append(" inner join UsuarioUnidadeOrganizacio usuuni on ususis.id = usuuni.usuarioSistema_id ")
            .append(" inner join UnidadeOrganizacional uo on usuuni.unidadeOrganizacional_id = uo.id ")
            .append(" where ususis.id = :usuario_id ")
            .append(" and usuuni.gestorProtocolo = :gestor)")
            .append(" and pro.protocolo = :protocolo ");
        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            sql.append(" and pro.numero = :numero ");
        }
        if (!Strings.isNullOrEmpty(filtro.getAno())) {
            sql.append(" and pro.ano = :ano ");
        }
        if (!Strings.isNullOrEmpty(filtro.getSolicitante())) {
            sql.append(" and (pro.pessoa_id in(select pf.id from PessoaFisica pf where lower(pf.nome) like :solicitante or pf.cpf like :solicitante) ")
                .append(" or pro.pessoa_id in(select pj.id from PessoaJuridica pj where lower(pj.razaoSocial) like :solicitante or pj.cnpj like :solicitante))");
        }
        sql.append(" order by pro.id desc ");
        Query q = em.createNativeQuery(sql.toString());
        if (max != 0) {
            q.setMaxResults(max);
            q.setFirstResult(inicio);
        }
        q.setParameter("usuario_id", filtro.getUsuarioSistema().getId());
        q.setParameter("aceito", filtro.getAceito());
        q.setParameter("status", filtro.getStatus());
        q.setParameter("gestor", filtro.getGestor());
        q.setParameter("protocolo", filtro.getProtocolo());
        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            q.setParameter("numero", filtro.getNumero().trim());
        }
        if (!Strings.isNullOrEmpty(filtro.getAno())) {
            q.setParameter("ano", filtro.getAno());
        }
        if (!Strings.isNullOrEmpty(filtro.getSolicitante())) {
            q.setParameter("solicitante", "%" + filtro.getSolicitante().toLowerCase() + "%");
        }
        return q;
    }

    private List<VoTramite> montarVoTramites(List<Object[]> lista) {
        List<VoTramite> retorno = Lists.newArrayList();
        for (Object[] obj : lista) {
            VoTramite tra = new VoTramite();
            tra.setId(((BigDecimal) obj[0]).longValue());
            tra.setDataAceite(obj[1] != null ? (Date) obj[1] : null);
            tra.setIndice(((BigDecimal) obj[2]).intValue());
            tra.setResponsavel((String) obj[3]);
            tra.setMotivo((String) obj[4]);
            tra.setObservacoes((String) obj[5]);
            tra.setDestino(obj[6] != null ? (String) obj[6] : null);
            tra.setUnidadeOrganizacional(getUnidadeOrganizacionalFacade().recuperar(((BigDecimal) obj[7]).longValue()));

            VoProcesso pro = new VoProcesso();
            pro.setId(((BigDecimal) obj[8]).longValue());
            pro.setNumero(((BigDecimal) obj[9]).intValue());
            pro.setAno(((BigDecimal) obj[10]).intValue());
            pro.setDataRegistro((Date) obj[11]);
            pro.setNomeAutorRequerente((String) obj[12]);
            pro.setAssunto((String) obj[13]);
            pro.setSituacao(SituacaoProcesso.valueOf((String) obj[14]));
            pro.setNomeAssuntoProcesso((obj[15] != null ? (String) obj[15] : "") + " - " + (obj[16] != null ? (String) obj[16] : ""));
            tra.setProcesso(pro);
            retorno.add(tra);
        }
        return retorno;
    }

    private List<VoTramite> montarVoTramitesUnidadesExtintas(List<Object[]> lista) {
        List<VoTramite> retorno = Lists.newArrayList();
        for (Object[] obj : lista) {
            VoTramite tra = new VoTramite();
            tra.setId(((BigDecimal) obj[0]).longValue());
            tra.setDataAceite(obj[1] != null ? (Date) obj[1] : null);
            tra.setUnidadeOrganizacional(obj[2] != null ? getUnidadeOrganizacionalFacade().recuperar(((BigDecimal) obj[2]).longValue()) : null);
            tra.setDestinoExterno(obj[3] != null ? (String) obj[3] : null);
            tra.setDataRegistro(obj[4] != null ? (Date) obj[4] : null);

            VoProcesso pro = new VoProcesso();
            pro.setId(((BigDecimal) obj[5]).longValue());
            pro.setNumero(((BigDecimal) obj[6]).intValue());
            pro.setAno(((BigDecimal) obj[7]).intValue());
            pro.setDataRegistro((Date) obj[8]);
            pro.setNomeAutorRequerente((String) obj[9]);
            pro.setAssunto((String) obj[10]);
            pro.setSituacao(SituacaoProcesso.valueOf((String) obj[11]));
            tra.setProcesso(pro);
            retorno.add(tra);
        }
        return retorno;
    }


    private List<VoTramite> montarVoTramitesProcessoExterno(List<Object[]> lista) {
        List<VoTramite> retorno = Lists.newArrayList();
        for (Object[] obj : lista) {
            VoTramite tra = new VoTramite();
            tra.setId(((BigDecimal) obj[0]).longValue());
            tra.setDestinoExterno((String) obj[1]);
            tra.setDataAceite((Date) obj[2]);

            VoProcesso pro = new VoProcesso();
            pro.setId(((BigDecimal) obj[3]).longValue());
            pro.setNumero(((BigDecimal) obj[4]).intValue());
            pro.setAno(((BigDecimal) obj[5]).intValue());
            pro.setDataRegistro((Date) obj[6]);
            pro.setNomeAutorRequerente((String) obj[7]);
            pro.setAssunto((String) obj[8]);
            pro.setSituacao(SituacaoProcesso.valueOf((String) obj[9]));

            tra.setProcesso(pro);
            retorno.add(tra);
        }
        return retorno;
    }

    public Tramite incorporarTramites(Tramite selecionado, TramitacaoProtocolo tramitacaoProtocolo, FiltrosPesquisaProtocolo filtroPesquisa) {
        Processo processoAtual = processoFacade.recuperar(tramitacaoProtocolo.getProcesso().getId());
        tramitacaoProtocolo.setProcesso(processoAtual);
        for (VoTramite voTramite : filtroPesquisa.getTramitesAuxiliares()) {
            Tramite tramiteSelecionado = recuperar(voTramite.getId());
            if (!hasTramiteEnglobado(tramiteSelecionado, selecionado)) {
                Processo processoIncorporado = processoFacade.recuperar(tramiteSelecionado.getProcesso().getId());
                processoIncorporado.setSituacao(SituacaoProcesso.INCORPORADO);

                String descricao = Processo.PROTOCOLO_INCORPORADO + " ao " + selecionado.getProcesso() + " - Motivo: " + tramitacaoProtocolo.getMotivoIncorporacao();
                String destinoExterno = getDestinoExternoHistorico(tramiteSelecionado, processoIncorporado.isProcessoExterno());
                UnidadeOrganizacional unidadeOrganizacional = getUnidadeOrganizacionalHistorico(tramiteSelecionado, processoIncorporado.isProcessoInterno());
                gerarHistoricoProcesso(processoIncorporado, descricao, unidadeOrganizacional, destinoExterno, getUsuarioCorrente());

                processoIncorporado = em.merge(processoIncorporado);

                ProcessoEnglobado processoEnglobado = criarProcessoEnglobado(selecionado, tramitacaoProtocolo, processoIncorporado);
                em.merge(processoEnglobado);
            }
        }
        selecionado.setStatus(true);
        return em.merge(selecionado);
    }

    private ProcessoEnglobado criarProcessoEnglobado(Tramite selecionado, TramitacaoProtocolo tramitacaoProtocolo, Processo processoIncorporado) {
        ProcessoEnglobado processoEnglobado = new ProcessoEnglobado();
        processoEnglobado.setProcesso(selecionado.getProcesso());
        processoEnglobado.setEnglobado(processoIncorporado);
        processoEnglobado.setMotivo(tramitacaoProtocolo.getMotivoIncorporacao());
        processoEnglobado.setStatus(StatusProcessoEnglobado.INCORPORADO);
        processoEnglobado.setDataRegistro(sistemaFacade.getDataOperacao());
        processoEnglobado.setUsuarioSistema(getUsuarioCorrente());
        return processoEnglobado;
    }

    public Tramite encaminharProcesso(Tramite selecionado, TramitacaoProtocolo tramitacaoProtocolo, Integer indice) {
        tramitacaoProtocolo.getProcesso().setTipoProcessoTramite(tramitacaoProtocolo.getTipoEncaminhamento());
        if (!tramitacaoProtocolo.getEncaminhamentoMultiplo()) {
            encaminhamentoUnico(selecionado, tramitacaoProtocolo);
        } else {
            encaminhamentoMultiplo(selecionado, tramitacaoProtocolo);
        }
        return selecionado;
    }

    private void encaminhamentoMultiplo(Tramite selecionado, TramitacaoProtocolo tramitacaoProtocolo) {
        int qtde = 1;
        Processo processo = processoFacade.recuperar(tramitacaoProtocolo.getProcesso().getId());
        processo.setTipoProcessoTramite(TipoProcessoTramite.INTERNO);
        selecionado.setStatus(false);
        selecionado.setDataConclusao(new Date());

        for (UnidadeOrganizacional unidadeOrganizacional : tramitacaoProtocolo.getUnidadesEncaminhamentosMultiplos()) {
            Tramite tramiteSelecionado = instanciarTramite(unidadeOrganizacional, tramitacaoProtocolo);

            String descricao = Processo.PROTOCOLO_ENCAMINHADO + " - Motivo: " + tramitacaoProtocolo.getMotivoTramiteSelecionado();
            if (qtde == 1) {
                tramiteSelecionado.setEncaminhamentoMultiplo(true);
                int tamanho = processo.getTramites().size();
                tramiteSelecionado.setIndice(tamanho);
                tramiteSelecionado.setProcesso(processo);
                gerarHistoricoProcesso(processo, descricao, unidadeOrganizacional, null, getUsuarioCorrente());
                atualizarProcessosEnglobados(selecionado.getProcesso().getProcessosEnglobados(), unidadeOrganizacional, selecionado, tramitacaoProtocolo);
                Util.adicionarObjetoEmLista(processo.getTramites(), tramiteSelecionado);
                alterarSituacaoProcessosEnglobados(selecionado.getProcesso().getProcessosEnglobados(), tramiteSelecionado, descricao, processo);
            } else {
                Processo novoProcesso = instanciarProcesso(tramitacaoProtocolo);
                adicionarProcessosEnglobados(novoProcesso, processo.getProcessosEnglobados(), unidadeOrganizacional, selecionado, tramitacaoProtocolo);
                adicionarDocumentos(novoProcesso, tramitacaoProtocolo);
                adicionarArquivos(novoProcesso, tramitacaoProtocolo);
                tramiteSelecionado.setProcesso(novoProcesso);
                Util.adicionarObjetoEmLista(novoProcesso.getTramites(), tramiteSelecionado);
                Util.adicionarObjetoEmLista(processo.getProcessosSubordinados(), novoProcesso);
                alterarSituacaoProcessosEnglobados(processo.getProcessosEnglobados(), tramiteSelecionado, descricao, processo);
            }
            qtde++;
        }
        salvarProcessoAndSelecionado(selecionado, processo);
    }

    private void alterarSituacaoProcessosEnglobados(List<ProcessoEnglobado> processosEnglobados, Tramite tramiteSelecionado, String historico, Processo processo) {
        for (ProcessoEnglobado englobado : processosEnglobados) {
            if (SituacaoProcesso.INCORPORADO.equals(englobado.getEnglobado().getSituacao())) {
                englobado.setEnglobado(processoFacade.recuperar(englobado.getEnglobado().getId()));
                adicionarTramitacao(englobado.getEnglobado(), tramiteSelecionado);
                englobado.getEnglobado().setUoCadastro(processo.getUoCadastro());

                UnidadeOrganizacional unidadeOrganizacional = getUnidadeOrganizacionalHistorico(tramiteSelecionado, englobado.getProcesso().isProcessoInterno());
                String destinoExterno = getDestinoExternoHistorico(tramiteSelecionado, englobado.getProcesso().isProcessoExterno());
                gerarHistoricoProcesso(englobado.getEnglobado(), historico, unidadeOrganizacional, destinoExterno, getUsuarioCorrente());
                processoFacade.salvarProcesso(englobado.getEnglobado());
            }
        }
    }

    private void adicionarTramitacao(Processo processoEnglobado, Tramite tramiteSelecionado) {
        Tramite novoTramite = (Tramite) Util.clonarObjeto(tramiteSelecionado);
        novoTramite.setProcesso(processoEnglobado);
        novoTramite.setIndice(processoEnglobado.buscarMaiorIndiceDoTramite() + 1);
        Util.adicionarObjetoEmLista(processoEnglobado.getTramites(), novoTramite);
    }


    private void alterarSituacaoProcessosEnglobadosAceito(Processo processo, Tramite tramite) {
        for (ProcessoEnglobado processoEnglobado : processo.getProcessosEnglobados()) {
            if (StatusProcessoEnglobado.INCORPORADO.equals(processoEnglobado.getStatus())) {
                processoEnglobado.setEnglobado(processoFacade.recuperar(processoEnglobado.getEnglobado().getId()));
                atualizarTramitesEnglobados(processoEnglobado.getEnglobado(), tramite);

                Processo processoEnglobadoRecuperado = processoFacade.recuperar(processoEnglobado.getEnglobado().getId());

                UnidadeOrganizacional unidadeOrganizacional = getUnidadeOrganizacionalHistorico(tramite, processoEnglobadoRecuperado.isProcessoInterno());
                String descricao = Processo.PROTOCOLO_ACEITO + ", Responsável: " + tramite.getResponsavel();
                String destinoExterno = getDestinoExternoHistorico(tramite, processoEnglobadoRecuperado.isProcessoExterno());
                gerarHistoricoProcesso(processoEnglobadoRecuperado, descricao, unidadeOrganizacional, destinoExterno, getUsuarioCorrente());
                processoFacade.salvarProcesso(processoEnglobadoRecuperado);
            }
        }
    }

    private void atualizarTramitesEnglobados(Processo processo, Tramite tramite) {
        ValidacaoException ve = new ValidacaoException();
        for (Tramite tramiteEnglobado : processo.getTramites()) {
            if (tramiteEnglobado.getUnidadeOrganizacional().equals(tramite.getUnidadeOrganizacional())) {
                Tramite tramiteRecuperado = recuperar(tramiteEnglobado.getId());
                if (validarDataDeAceite(tramiteRecuperado, tramite.getDataAceite(), ve, true, "O campo Data de Aceite deve ser posterior ou igual a Data de Cadastro.")) {
                    tramiteRecuperado.setUsuarioTramite(getUsuarioCorrente());
                    tramiteRecuperado.setAceito(true);
                    tramiteRecuperado.setStatus(true);
                    tramiteRecuperado.setDataAceite(tramite.getDataAceite());
                    tramiteRecuperado.setResponsavel(tramite.getResponsavel());
                    salvarTramite(tramiteRecuperado);
                }
            }
        }
        ve.lancarException();
    }

    public Boolean validarDataDeAceite(Tramite tramite, Date dataAceite, ValidacaoException ve,
                                       boolean isIncorporado, String mensagem) {
        String msgValidacao = "";
        if (isIncorporado) {
            msgValidacao = mensagem + " para o Tramite Incorporado.";
        } else {
            msgValidacao = mensagem;
        }
        Date dataRegistroOuAceite = buscarDataRegistroOrDataAceite(tramite);
        if (dataRegistroOuAceite != null && dataRegistroOuAceite.compareTo(dataAceite) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(msgValidacao);
            return false;
        }
        return true;
    }

    private Date buscarDataRegistroOrDataAceite(Tramite tramite) {
        Date maiorData = null;
        for (Tramite tramiteAnterior : tramite.getProcesso().getTramites()) {
            if (maiorData == null || (tramiteAnterior.getDataRegistro().compareTo(maiorData) > 0)) {
                maiorData = tramiteAnterior.getDataRegistro();
            }
        }
        if (maiorData == null) {
            maiorData = tramite.getDataRegistro();
        }
        return maiorData;
    }

    private Processo instanciarProcesso(TramitacaoProtocolo tramitacaoProtocolo) {
        Processo p = new Processo();
        p.setProcessoSuperior(tramitacaoProtocolo.getProcesso());
        p.setAssunto(tramitacaoProtocolo.getProcesso().getAssunto());
        p.setConfidencial(tramitacaoProtocolo.getProcesso().getConfidencial());
        p.setDataRegistro(tramitacaoProtocolo.getProcesso().getDataRegistro());
        p.setEncaminhamentoMultiplo(tramitacaoProtocolo.getEncaminhamentoMultiplo());
        p.setExercicio(sistemaFacade.getExercicioCorrente());
        p.setAno(sistemaFacade.getExercicioCorrente().getAno());
        p.setMotivo(tramitacaoProtocolo.getProcesso().getMotivo());
        p.setNumeroProcessoAntigo(tramitacaoProtocolo.getProcesso().getNumeroProcessoAntigo());
        p.setNumeroProcessoSAJ(tramitacaoProtocolo.getProcesso().getNumeroProcessoSAJ());
        p.setObjetivo(tramitacaoProtocolo.getProcesso().getObjetivo());
        p.setObservacoes(tramitacaoProtocolo.getProcesso().getObservacoes());
        p.setPessoa(tramitacaoProtocolo.getProcesso().getPessoa());
        p.setProtocolo(tramitacaoProtocolo.getProcesso().getProtocolo());
        p.setResponsavelCadastro(tramitacaoProtocolo.getProcesso().getResponsavelCadastro());
        p.setSenha(processoFacade.geraSenha());
        p.setSituacao(tramitacaoProtocolo.getProcesso().getSituacao());
        p.setTipoProcessoTramite(tramitacaoProtocolo.getProcesso().getTipoProcessoTramite());
        p.setUoCadastro(tramitacaoProtocolo.getProcesso().getUoCadastro());
        return p;
    }

    private Tramite instanciarTramite(UnidadeOrganizacional unidadeOrganizacional, TramitacaoProtocolo tramitacaoProtocolo) {
        Tramite tra = new Tramite();
        tra.setIndice(0);
        tra.setDataRegistro(new Date());
        tra.setUnidadeOrganizacional(unidadeOrganizacional);
        tra.setUsuarioTramite(getUsuarioCorrente());
        tra.setOrigem(tramitacaoProtocolo.getProcesso().getUoCadastro());
        tra.setStatus(true);
        tra.setAceito(false);
        tra.setSituacaoTramite(null);
        tra.setMotivo(tramitacaoProtocolo.getMotivoTramiteSelecionado());
        tra.setObservacoes(tramitacaoProtocolo.getObservacaoTramiteSelecionado());
        return tra;
    }

    private void adicionarArquivos(Processo novoProcesso, TramitacaoProtocolo tramitacaoProtocolo) {
        for (ProcessoArquivo arq : tramitacaoProtocolo.getProcesso().getArquivos()) {
            ProcessoArquivo novoArq = new ProcessoArquivo();
            novoArq.setArquivo(arq.getArquivo());
            novoArq.setProcesso(novoProcesso);
            Util.adicionarObjetoEmLista(novoProcesso.getArquivos(), novoArq);
        }
    }

    private void adicionarDocumentos(Processo novoProcesso, TramitacaoProtocolo tramitacaoProtocolo) {
        for (DocumentoProcesso documento : tramitacaoProtocolo.getProcesso().getDocumentoProcesso()) {
            DocumentoProcesso documentoProcesso = new DocumentoProcesso();
            documentoProcesso.setDataRegistro(documento.getDataRegistro());
            documentoProcesso.setDocumento(documento.getDocumento());
            documentoProcesso.setNumeroDocumento(documento.getNumeroDocumento());
            documentoProcesso.setProcesso(novoProcesso);
            Util.adicionarObjetoEmLista(novoProcesso.getDocumentoProcesso(), documentoProcesso);
        }
    }

    private void atualizarProcessosEnglobados(List<ProcessoEnglobado> processosEnglobados, UnidadeOrganizacional unidadeOrganizacional,
                                              Tramite selecionado, TramitacaoProtocolo tramitacaoProtocolo) {

        for (ProcessoEnglobado englobado : processosEnglobados) {
            englobado.setEnglobado(processoFacade.recuperar(englobado.getEnglobado().getId()));
            for (Tramite tramiteDoEnglobado : englobado.getEnglobado().getTramites()) {
                atualizarTramiteAndProcesso(tramiteDoEnglobado, englobado.getEnglobado(), unidadeOrganizacional, tramitacaoProtocolo, selecionado);
            }
        }
    }

    private void adicionarProcessosEnglobados(Processo processo, List<ProcessoEnglobado> processosEnglobados, UnidadeOrganizacional unidadeOrganizacional,
                                              Tramite selecionado, TramitacaoProtocolo tramitacaoProtocolo) {
        for (ProcessoEnglobado englobado : processosEnglobados) {
            englobado.setEnglobado(processoFacade.recuperar(englobado.getEnglobado().getId()));
            for (Tramite tramiteDoEnglobado : englobado.getEnglobado().getTramites()) {
                atualizarTramiteAndProcesso(tramiteDoEnglobado, englobado.getEnglobado(), unidadeOrganizacional, tramitacaoProtocolo, selecionado);
            }
            ProcessoEnglobado novoEnglobado = new ProcessoEnglobado();
            novoEnglobado.setEnglobado(englobado.getEnglobado());
            novoEnglobado.setProcesso(processo);
            Util.adicionarObjetoEmLista(processo.getProcessosEnglobados(), novoEnglobado);
        }
    }

    private void atualizarTramiteAndProcesso(Tramite tramite, Processo processo, UnidadeOrganizacional unidadeOrganizacional,
                                             TramitacaoProtocolo tramitacaoProtocolo, Tramite selecionado) {
        if (selecionado.getUnidadeOrganizacional() != null) {
            tramite.setOrigem(selecionado.getUnidadeOrganizacional());
        } else {
            tramite.setOrigem(selecionado.getOrigem());
        }
        tramite.setStatus(true);
        tramite.setAceito(false);
        if (getInterno(tramitacaoProtocolo)) {
            processo.setTipoProcessoTramite(TipoProcessoTramite.INTERNO);
            tramite.setUnidadeOrganizacional(unidadeOrganizacional);
        } else {
            processo.setTipoProcessoTramite(TipoProcessoTramite.EXTERNO);
            tramite.setDestinoExterno(tramitacaoProtocolo.getDestinoExterno());
        }
    }

    private Boolean getInterno(TramitacaoProtocolo tramitacaoProtocolo) {
        return TipoProcessoTramite.INTERNO.equals(tramitacaoProtocolo.getTipoEncaminhamento());
    }

    private void salvarProcessoAndSelecionado(Tramite selecionado, Processo processo) {
        try {
            Processo processoSalvo = processoFacade.salvarProcessoProtocolo(processo);
            processoSalvo = processoFacade.recuperar(processoSalvo.getId());
            selecionado.setProcesso(processoSalvo);
            salvarTramite(selecionado);
        } catch (Exception e) {
            logger.error("Erro ao salvar o processo: {}", e);
        }
    }

    private boolean hasTramiteEnglobado(Tramite tramiteSelecionado, Tramite selecionado) {
        for (ProcessoEnglobado processoEnglobado : selecionado.getProcesso().getProcessosEnglobados()) {
            if (processoEnglobado.getEnglobado().equals(tramiteSelecionado.getProcesso())) {
                return true;
            }
        }
        return false;
    }

    private String getDestinoExternoHistorico(Tramite tramiteSelecionado, Boolean isExterno) {
        return isExterno ? tramiteSelecionado.getDestinoExterno() : "";
    }

    private UnidadeOrganizacional getUnidadeOrganizacionalHistorico(Tramite tramiteSelecionado, Boolean isInterno) {
        return isInterno ? tramiteSelecionado.getUnidadeOrganizacional() : null;
    }

    private void gerarHistoricoProcesso(Processo processo, String descricao, UnidadeOrganizacional unidadeOrganizacional, String destinoExterno, UsuarioSistema usuarioSistema) {
        ProcessoHistorico processoHistorico;
        if (processo.isProcessoInterno()) {
            processoHistorico = processo.criarHistoricoProcesso(descricao, unidadeOrganizacional, usuarioSistema);
        } else {
            processoHistorico = processo.criarHistoricoProcessoExterno(descricao, destinoExterno, usuarioSistema);
        }
        processo.getHistoricos().add(processoHistorico);
    }

    public Tramite salvarTramite(Tramite tramite) {
        return em.merge(tramite);
    }

    public TramitacaoProtocolo salvaTramite(Tramite selecionado, TramitacaoProtocolo tramitacaoProtocolo) {
        atribuirProcessoRotaTramitado(tramitacaoProtocolo);
        Processo processoSalvo = processoFacade.salvarProcessoRetornando(tramitacaoProtocolo.getProcesso());
        Tramite tramiteSalvo = salvarTramite(selecionado);
        removerNotificacaoDoTramite(tramiteSalvo);
        em.merge(tramitacaoProtocolo.getNovoTramite());
        tramitacaoProtocolo.setProcesso(processoSalvo);
        tramitacaoProtocolo.setSelecionado(tramiteSalvo);
        return tramitacaoProtocolo;
    }

    private void removerNotificacaoDoTramite(Tramite selecionado) {
        if ((selecionado.getSituacaoTramite() != null
            && selecionado.getSituacaoTramite().getParaPrazo())
            || selecionado.getStatus().equals(Boolean.FALSE)) {
            processoFacade.removerNotificacaoDoTramite(selecionado);
        }
    }

    public void arquivarProtocolo(Tramite selecionado) {

        selecionado.setStatus(false);
        selecionado.setDataConclusao(new Date());

        Processo processoRecuperado = processoFacade.recuperar(selecionado.getProcesso().getId());
        processoRecuperado.setSituacao(SituacaoProcesso.ARQUIVADO);
        processoRecuperado.setTramiteFinalizador(selecionado);
        selecionado.setProcesso(processoRecuperado);

        String descricao = Processo.PROTOCOLO_ARQUIVADO + ", Motivo: " + selecionado.getMotivo();
        UnidadeOrganizacional unidadeOrganizacional = getUnidadeOrganizacionalHistorico(selecionado, processoRecuperado.isProcessoInterno());
        String destinoExterno = getDestinoExternoHistorico(selecionado, processoRecuperado.isProcessoExterno());
        gerarHistoricoProcesso(processoRecuperado, descricao, unidadeOrganizacional, destinoExterno, getUsuarioCorrente());

        arquivarProcessosEnglobados(selecionado, processoRecuperado);
        salvarProcessoAndSelecionado(selecionado, processoRecuperado);
    }

    private void arquivarProcessosEnglobados(Tramite tramiteSelecionado, Processo processo) {
        try {
            for (ProcessoEnglobado englobado : processo.getProcessosEnglobados()) {
                Processo processoRecuperado = processoFacade.recuperar(englobado.getEnglobado().getId());
                if (SituacaoProcesso.INCORPORADO.equals(processoRecuperado.getSituacao())) {
                    Tramite ultimoTramite = arquivarOrEncerrarProcessosEnglobados(tramiteSelecionado, processoRecuperado);
                    processoRecuperado = processoFacade.recuperar(englobado.getEnglobado().getId());
                    processoRecuperado.setTramiteFinalizador(ultimoTramite);

                    String descricao = Processo.PROTOCOLO_ARQUIVADO + " - Motivo: " + tramiteSelecionado.getMotivo();
                    UnidadeOrganizacional unidadeOrganizacional = getUnidadeOrganizacionalHistorico(ultimoTramite, processoRecuperado.isProcessoInterno());
                    String destinoExterno = getDestinoExternoHistorico(tramiteSelecionado, processoRecuperado.isProcessoExterno());
                    gerarHistoricoProcesso(processoRecuperado, descricao, unidadeOrganizacional, destinoExterno, getUsuarioCorrente());
                    processoFacade.salvar(processoRecuperado);
                }
            }
        } catch (Exception ex) {
            logger.error("Erro ao salvar o encerramento do processo: {}", ex);
        }
    }

    private Tramite arquivarOrEncerrarProcessosEnglobados(Tramite tramiteSelecionado, Processo processoRecuperado) {
        Tramite ultimoTramite = recuperar(processoRecuperado.buscarUltimoTramite().getId());
        ultimoTramite.setStatus(false);
        ultimoTramite.setDataConclusao(new Date());
        ultimoTramite.setMotivo(tramiteSelecionado.getMotivo());
        return salvarTramite(ultimoTramite);
    }

    public void arquivarProtocolosSelecionados(FiltrosPesquisaProtocolo filtrosTramitesRecebidosSelecionados, TramitacaoProtocolo tramitacaoProtocolo) {
        for (VoTramite voTramite : filtrosTramitesRecebidosSelecionados.getTramites()) {
            Tramite tramite = recuperar(voTramite.getId());
            validarTramiteMaisNovo(tramite.getProcesso(), tramite.getIndice());

            tramite.setMotivo(!Strings.isNullOrEmpty(voTramite.getMotivo()) ? voTramite.getMotivo() : tramitacaoProtocolo.getMotivoTramiteSelecionado());

            if (Strings.isNullOrEmpty(tramite.getObservacoes())) {
                tramite.setObservacoes(!Strings.isNullOrEmpty(voTramite.getObservacoes()) ? voTramite.getObservacoes() : tramitacaoProtocolo.getObservacaoTramiteSelecionado());
            }
            arquivarProtocolo(tramite);
        }
    }

    private void validarTramiteMaisNovo(Processo processo, Integer indice) {
        ValidacaoException ve = new ValidacaoException();
        if (processoFacade.hasTramiteMaisNovo(processo, indice)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Existe um trâmite mais recente para o " + (processo.getProtocolo() ? "Protocolo" : "Processo")
                + " " + processo.getNumero() + "/" + processo.getAno() + "!");
        }
        ve.lancarException();
    }

    public void encerrarProtocolosSelecionados(FiltrosPesquisaProtocolo filtrosTramitesRecebidosSelecionados, TramitacaoProtocolo tramitacaoProtocolo) {
        for (VoTramite voTramite : filtrosTramitesRecebidosSelecionados.getTramites()) {
            Tramite tramite = recuperar(voTramite.getId());
            validarTramiteMaisNovo(tramite.getProcesso(), tramite.getIndice());
            if (Strings.isNullOrEmpty(tramite.getMotivo())) {
                tramite.setMotivo(!Strings.isNullOrEmpty(voTramite.getMotivo()) ? voTramite.getMotivo() : tramitacaoProtocolo.getMotivoTramiteSelecionado());
            }
            if (Strings.isNullOrEmpty(tramite.getObservacoes())) {
                tramite.setObservacoes(!Strings.isNullOrEmpty(voTramite.getObservacoes()) ? voTramite.getObservacoes() : tramitacaoProtocolo.getObservacaoTramiteSelecionado());
            }
            encerrarProtocolo(tramite, tramitacaoProtocolo);
        }
    }

    public void encerrarProtocolo(Tramite tramiteSelecionado, TramitacaoProtocolo tramitacaoProtocolo) {
        Processo processoRecuperado = processoFacade.recuperar(tramiteSelecionado.getProcesso().getId());
        processoRecuperado.setSituacao(SituacaoProcesso.FINALIZADO);
        processoRecuperado.setTramiteFinalizador(tramiteSelecionado);

        tramiteSelecionado.setStatus(false);
        tramiteSelecionado.setDataConclusao(new Date());
        tramiteSelecionado.setProcesso(processoRecuperado);

        tramitacaoProtocolo.setProcesso(processoRecuperado);

        String descricao = Processo.PROTOCOLO_ENCERRADO + " - Motivo: " + tramitacaoProtocolo.getMotivoTramiteSelecionado();
        UnidadeOrganizacional unidadeOrganizacional = getUnidadeOrganizacionalHistorico(tramiteSelecionado, processoRecuperado.isProcessoInterno());
        String destinoExterno = getDestinoExternoHistorico(tramiteSelecionado, processoRecuperado.isProcessoExterno());
        gerarHistoricoProcesso(processoRecuperado, descricao, unidadeOrganizacional, destinoExterno, getUsuarioCorrente());

        encerrarProcessosEnglobados(tramiteSelecionado, tramitacaoProtocolo);
        salvarProcessoAndSelecionado(tramiteSelecionado, processoRecuperado);
    }

    private void encerrarProcessosEnglobados(Tramite tramiteSelecionado, TramitacaoProtocolo tramitacaoProtocolo) {
        for (ProcessoEnglobado englobado : tramitacaoProtocolo.getProcesso().getProcessosEnglobados()) {
            Processo processoEnglobado = processoFacade.recuperar(englobado.getEnglobado().getId());
            if (SituacaoProcesso.INCORPORADO.equals(processoEnglobado.getSituacao())) {
                Tramite ultimoTramite = arquivarOrEncerrarProcessosEnglobados(tramiteSelecionado, processoEnglobado);
                processoEnglobado = processoFacade.recuperar(englobado.getEnglobado().getId());
                processoEnglobado.setTramiteFinalizador(ultimoTramite);

                String descricao = Processo.PROTOCOLO_ENCERRADO + " - Motivo: " + ultimoTramite.getMotivo();
                UnidadeOrganizacional unidadeOrganizacional = getUnidadeOrganizacionalHistorico(ultimoTramite, processoEnglobado.isProcessoInterno());
                String destinoExterno = getDestinoExternoHistorico(ultimoTramite, processoEnglobado.isProcessoExterno());
                gerarHistoricoProcesso(processoEnglobado, descricao, unidadeOrganizacional, destinoExterno, getUsuarioCorrente());
                processoFacade.salvar(processoEnglobado);
            }
        }
    }

    public void encerrarProtocolosSelecionadosUnidadesExtintas(TramitacaoProtocolo tramitacaoProtocolo) {
        for (VoTramite voTramite : tramitacaoProtocolo.getTramitesSelecionados()) {
            Tramite tramite = recuperar(voTramite.getId());
            if (Strings.isNullOrEmpty(tramite.getMotivo())) {
                tramite.setMotivo(tramitacaoProtocolo.getMotivoTramiteSelecionado());
            }
            if (Strings.isNullOrEmpty(tramite.getObservacoes())) {
                tramite.setObservacoes(tramitacaoProtocolo.getObservacaoTramiteSelecionado());
            }
            encerrarProtocolo(tramite, tramitacaoProtocolo);
        }
    }

    public void aceitarTodosTramitePendente(FiltrosPesquisaProtocolo filtrosTramitesPendentesSelecionados) {
        for (VoTramite voTramite : filtrosTramitesPendentesSelecionados.getTramites()) {
            Tramite tramiteAceito = recuperar(voTramite.getId());
            Tramite tramiteRecuperado = recuperar(voTramite.getId());
            tramiteRecuperado.setUsuarioTramite(getUsuarioCorrente());
            tramiteRecuperado.setAceito(true);
            tramiteRecuperado.setStatus(true);
            tramiteRecuperado.setDataAceite(voTramite.getDataAceite());
            tramiteRecuperado.setResponsavel(voTramite.getResponsavel());

            Processo processo = processoFacade.recuperar(voTramite.getProcesso().getId());
            processo.setSituacao(SituacaoProcesso.EMTRAMITE);
            tramiteRecuperado.setProcesso(processo);

            String descricao = Processo.PROTOCOLO_ACEITO + ", Responsável: " + tramiteRecuperado.getResponsavel();
            UnidadeOrganizacional unidadeOrganizacional = getUnidadeOrganizacionalHistorico(tramiteRecuperado, processo.isProcessoInterno());
            String destinoExterno = getDestinoExternoHistorico(tramiteRecuperado, processo.isProcessoExterno());
            gerarHistoricoProcesso(processo, descricao, unidadeOrganizacional, destinoExterno, tramiteRecuperado.getUsuarioTramite());

            Util.adicionarObjetoEmLista(processo.getTramites(), tramiteRecuperado);
            processo = processoFacade.salvarProcesso(processo);

            alterarSituacaoProcessosEnglobadosAceito(processo, tramiteAceito);
        }
    }

    public void aceitarTramiteProtocolo(Tramite selecionado, TramitacaoProtocolo tramitacaoProtocolo) {
        if (!SituacaoProcesso.CANCELADO.name().equals(selecionado.getProcesso().getSituacao().name())) {
            selecionado.setDataAceite(DataUtil.juntarDataEHora(tramitacaoProtocolo.getDataAceite(), tramitacaoProtocolo.getHoraAceite()));
            selecionado.setUsuarioTramite(getUsuarioCorrente());
            selecionado.setAceito(true);
            selecionado.setStatus(true);
            tramitacaoProtocolo.setProcesso(processoFacade.recuperar(tramitacaoProtocolo.getProcesso().getId()));
            tramitacaoProtocolo.getProcesso().setSituacao(SituacaoProcesso.EMTRAMITE);

            String descricao = Processo.PROTOCOLO_ACEITO + ", Responsável: " + selecionado.getResponsavel();
            UnidadeOrganizacional unidadeOrganizacional = getUnidadeOrganizacionalHistorico(selecionado, tramitacaoProtocolo.getProcesso().isProcessoInterno());
            String destinoExterno = getDestinoExternoHistorico(selecionado, tramitacaoProtocolo.getProcesso().isProcessoExterno());
            gerarHistoricoProcesso(tramitacaoProtocolo.getProcesso(), descricao, unidadeOrganizacional, destinoExterno, getUsuarioCorrente());

            tramitacaoProtocolo.setProcesso(processoFacade.salvarProcesso(tramitacaoProtocolo.getProcesso()));
            salvar(selecionado);
            alterarSituacaoProcessosEnglobadosAceito(tramitacaoProtocolo.getProcesso(), selecionado);
        } else {
            throw new ValidacaoException("Não é possivel aceitar trâmite de um protocolo cancelado.");
        }
    }

    public void encaminharTramitesSelecionadosUnidadesExtintas(TramitacaoProtocolo tramitacaoProtocolo) {
        for (VoTramite voTramite : tramitacaoProtocolo.getTramitesSelecionados()) {
            Tramite tramiteSelecionado = recuperar(voTramite.getId());
            encaminhamentoUnico(tramiteSelecionado, tramitacaoProtocolo);
        }
    }

    public void encaminharTramitesSelecionados(FiltrosPesquisaProtocolo filtrosTramitesRecebidosSelecionados, TramitacaoProtocolo tramitacaoProtocolo) {
        for (VoTramite voTramite : filtrosTramitesRecebidosSelecionados.getTramites()) {
            Tramite tramiteSelecionado = recuperar(voTramite.getId());
            tramitacaoProtocolo.setMotivoTramiteSelecionado(!Strings.isNullOrEmpty(voTramite.getMotivo()) ? voTramite.getMotivo() : tramitacaoProtocolo.getMotivoTramiteSelecionado());
            tramitacaoProtocolo.setObservacaoTramiteSelecionado(!Strings.isNullOrEmpty(voTramite.getObservacoes()) ? voTramite.getObservacoes() : tramitacaoProtocolo.getObservacaoTramiteSelecionado());
            encaminhamentoUnico(tramiteSelecionado, tramitacaoProtocolo);
        }
    }

    private void encaminhamentoUnico(Tramite tramiteSelecionado, TramitacaoProtocolo tramitacaoProtocolo) {
        Processo processo = processoFacade.recuperar(tramiteSelecionado.getProcesso().getId());

        if (tramitacaoProtocolo.getProcesso() != null && tramitacaoProtocolo.getProcesso().getSituacao() != null) {
            processo.setSituacao(tramitacaoProtocolo.getProcesso().getSituacao());
        }
        processo.setEncaminhamentoMultiplo(Boolean.FALSE);
        if (SituacaoProcesso.GERADO.equals(processo.getSituacao())) {
            processo.setSituacao(SituacaoProcesso.EMTRAMITE);
        }
        Tramite novoTramite = criarNovoTramiteEncaminhamento(tramitacaoProtocolo, processo, tramiteSelecionado);
        atribuirValorTramiteEncaminhamentoUnico(tramiteSelecionado, novoTramite);

        processo.getTramites().add(novoTramite);
        processo = em.merge(processo);
        tramiteSelecionado.setProcesso(processo);

        String descricao = Processo.PROTOCOLO_ENCAMINHADO + " - Motivo: " + novoTramite.getMotivo();
        alterarSituacaoProcessosEnglobados(tramiteSelecionado.getProcesso().getProcessosEnglobados(), novoTramite, descricao, processo);

        UnidadeOrganizacional unidadeOrganizacional = processo.isProcessoInterno() ? tramitacaoProtocolo.getHierarquiaOrganizacional().getSubordinada() : null;
        String destinoExterno = getDestinoExternoHistorico(novoTramite, processo.isProcessoExterno());
        gerarHistoricoProcesso(tramiteSelecionado.getProcesso(), descricao, unidadeOrganizacional, destinoExterno, getUsuarioCorrente());
        salvarTramite(tramiteSelecionado);
    }

    private Tramite criarNovoTramiteEncaminhamento(TramitacaoProtocolo tramitacaoProtocolo, Processo processoRecuperado, Tramite tramiteSelecionado) {
        Tramite novoTramite = new Tramite();
        novoTramite.setDataRegistro(new Date());
        novoTramite.setIndice(processoRecuperado.getTramites().size());
        novoTramite.setStatus(true);
        novoTramite.setAceito(false);
        novoTramite.setUsuarioTramite(getUsuarioCorrente());
        novoTramite.setObservacoes(tramitacaoProtocolo.getObservacaoTramiteSelecionado());
        novoTramite.setMotivo(tramitacaoProtocolo.getMotivoTramiteSelecionado());
        if (getInterno(tramitacaoProtocolo)) {
            processoRecuperado.setTipoProcessoTramite(TipoProcessoTramite.INTERNO);
            novoTramite.setUnidadeOrganizacional(tramitacaoProtocolo.getHierarquiaOrganizacional().getSubordinada());
        } else {
            processoRecuperado.setTipoProcessoTramite(TipoProcessoTramite.EXTERNO);
            novoTramite.setDestinoExterno(tramitacaoProtocolo.getDestinoExterno());
        }
        novoTramite.setProcesso(processoRecuperado);
        if (tramiteSelecionado.getUnidadeOrganizacional() != null) {
            novoTramite.setOrigem(tramiteSelecionado.getUnidadeOrganizacional());
        } else {
            novoTramite.setOrigem(tramiteSelecionado.getOrigem());
        }
        return novoTramite;
    }

    private void atribuirValorTramiteEncaminhamentoUnico(Tramite tramiteSelecionado, Tramite novoTramite) {
        tramiteSelecionado.setStatus(false);
        tramiteSelecionado.setDataConclusao(new Date());
        if (tramiteSelecionado.getIndice() == 0) {
            tramiteSelecionado.setDataRegistro(tramiteSelecionado.getProcesso().getDataRegistro());
        }
        if (tramiteSelecionado.getDataAceite() == null) {
            tramiteSelecionado.setDataAceite(novoTramite.getDataRegistro());
        }
    }

    private void atribuirProcessoRotaTramitado(TramitacaoProtocolo tramitacaoProtocolo) {
        for (ProcessoRota rota : tramitacaoProtocolo.getProcesso().getRotas()) {
            if (rota.getIndice().equals(tramitacaoProtocolo.getNovoTramite().getIndice())) {
                rota.setTramitado(true);
            }
        }
    }

    private UsuarioSistema getUsuarioCorrente() {
        return processoFacade.getSistemaFacade().getUsuarioCorrente();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ProcessoFacade getProcessoFacade() {
        return processoFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public SingletonHierarquiaOrganizacional getSingletonHO() {
        return singletonHO;
    }

    public SituacaoTramiteFacade getSituacaoTramiteFacade() {
        return situacaoTramiteFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public AssuntoFacade getAssuntoFacade() {
        return assuntoFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }
}
