/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.AutorizacaoTributario;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoUsuarioTributario;
import br.com.webpublico.seguranca.SingletonRecursosSistema;
import br.com.webpublico.seguranca.menu.LeitorMenuFacade;
import br.com.webpublico.seguranca.service.UsuarioSistemaService;
import br.com.webpublico.singletons.SingletonAdministrativo;
import br.com.webpublico.singletons.SingletonMenu;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Seguranca;
import br.com.webpublico.util.Util;
import br.com.webpush.push.Push;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Munif
 */
@Stateless
public class UsuarioSistemaFacade extends AbstractFacade<UsuarioSistema> {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioSistemaFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private GrupoUsuarioFacade grupoUsuarioFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private GrupoRecursoFacade grupoRecursoFacade;
    @EJB
    private RecursoSistemaFacade recursoSistemaFacade;
    @EJB
    private ArquivoUsuarioSistemaFacade arquivoUsuarioSistemaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private LotacaoVistoriadoraFacade lotacaoVistoriadoraFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SingletonMenu singletonMenu;
    @EJB
    private SingletonAdministrativo singletonAdministrativo;
    private UsuarioSistemaService usuarioSistemaService;

    public UsuarioSistemaFacade() {
        super(UsuarioSistema.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioSistemaService getUsuarioSistemaService() {
        if (usuarioSistemaService == null) {
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            usuarioSistemaService = (UsuarioSistemaService) ap.getBean("usuarioSistemaService");
        }
        return usuarioSistemaService;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public List<UsuarioSistema> listaPorUnidadeOrganizacional(String parte, UnidadeOrganizacional uni) {
        String sql = "SELECT usr.* FROM USUARIOUNIDADEORG usr_org ";
        sql += " INNER JOIN USUARIOSISTEMA usr ON usr_org.USUARIOSISTEMA_ID=usr.ID ";
        sql += " INNER JOIN UNIDADEORGANIZACIONAL und ON usr_org.UNIDADEORGANIZACIONAL_ID=und.ID AND und.id=:paramuni ";
        sql += " INNER JOIN PESSOAFISICA p ON usr.PESSOAFISICA_ID=p.ID AND lower(p.NOME) LIKE :paramnome";

        Query q = getEntityManager().createNativeQuery(sql, UsuarioSistema.class);
        q.setParameter("paramuni", uni.getId());
        q.setParameter("paramnome", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }

    public UsuarioSistema recuperarAutorizacaoUsuarioRH(Object id) {
        UsuarioSistema us = em.find(UsuarioSistema.class, id);
        us.getAutorizacaoUsuarioRH().size();
        return us;
    }


    @Override
    public UsuarioSistema recuperar(Object id) {
        UsuarioSistema us = em.find(UsuarioSistema.class, id);
        us.getGrupoRecursosUsuario().size();
        us.getGrupos().size();
        for (GrupoUsuario grupo : us.getGrupos()) {
            grupo.getItens().size();
        }
        us.getArquivoUsuarioSistemas().size();
        us.getRecursosUsuario().size();
        us.getUsuarioUnidadeOrganizacional().size();
        us.getVigenciaTribUsuarios().size();
        us.getUsuarioUnidadeOrganizacionalOrc().size();
        us.getAutorizacaoUsuarioRH().size();
        us.getAssinaturas().size();
        for (AssinaturaUsuarioSistema assinatura : us.getAssinaturas()) {
            assinatura.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        for (VigenciaTribUsuario v : us.getVigenciaTribUsuarios()) {
            v.getLotacaoTribUsuarios().size();
            v.getTipoUsuarioTribUsuarios().size();
            v.getAutorizacaoTributarioUsuarios().size();
        }
        return us;
    }

    public UsuarioSistema recuperaDependenciasDeUsuario(Object id) {
        UsuarioSistema us = em.find(UsuarioSistema.class, id);
        for (GrupoUsuario obj : us.getGrupos()) {
            for (ItemGrupoUsuario item : obj.getItens()) {
                item.getGrupoRecurso().getRecursos().size();
            }
        }
        return us;
    }

    public UsuarioSistema recuperarDependenciasDasUnidadesPorTipo(Object id, TipoHierarquiaOrganizacional tipo) {
        UsuarioSistema us = em.find(UsuarioSistema.class, id);
        if (TipoHierarquiaOrganizacional.ADMINISTRATIVA.equals(tipo))
            Hibernate.initialize(us.getUsuarioUnidadeOrganizacional());
        if (TipoHierarquiaOrganizacional.ORCAMENTARIA.equals(tipo))
            Hibernate.initialize(us.getUsuarioUnidadeOrganizacionalOrc());
        return us;
    }

    public UsuarioSistema recuperarUsuarioComUnidadesAdmistrativas(Object id) {
        UsuarioSistema us = em.find(UsuarioSistema.class, id);
        us.getUsuarioUnidadeOrganizacional().size();
        return us;
    }


    public List<ArquivoUsuarioSistema> recuperaArquivosDoUsuario(UsuarioSistema usuarioSistema) {
        Query q = em.createQuery(" from ArquivoUsuarioSistema a "
            + " where a.usuarioSistema = :usuario "
            + " and a.excluido is false ");
        q.setParameter("usuario", usuarioSistema);
        return q.getResultList();
    }

    public UsuarioSistema recuperarSomenteUsuario(Object id) {
        UsuarioSistema us = em.find(UsuarioSistema.class, id);
        return us;
    }

    public GrupoUsuarioFacade getGrupoUsuarioFacade() {
        return grupoUsuarioFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public GrupoRecursoFacade getGrupoRecursoFacade() {
        return grupoRecursoFacade;
    }

    public void setGrupoRecursoFacade(GrupoRecursoFacade grupoRecursoFacade) {
        this.grupoRecursoFacade = grupoRecursoFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public void setContratoFPFacade(ContratoFPFacade contratoFPFacade) {
        this.contratoFPFacade = contratoFPFacade;
    }

    public ArquivoUsuarioSistemaFacade getArquivoUsuarioSistemaFacade() {
        return arquivoUsuarioSistemaFacade;
    }

    public void salvarArquivo(UploadedFile uploadedFile, Arquivo arq) {
        try {
            UploadedFile arquivoRecebido = uploadedFile;
            arq.setNome(arquivoRecebido.getFileName());
            arq.setMimeType(arquivoRecebido.getContentType()); //No prime 2 não funciona
            arq.setTamanho(arquivoRecebido.getSize());
            arquivoFacade.novoArquivo(arq, arquivoRecebido.getInputstream());
            arquivoRecebido.getInputstream().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public UsuarioSistema salvarRetornando(UsuarioSistema entity) {
        try {
            for (ArquivoUsuarioSistema a : entity.getArquivoUsuarioSistemas()) {
                if (a.getArquivo().getId() == null) {
                    if (!a.getExcluido()) {
                        salvarArquivo((UploadedFile) a.getFile(), a.getArquivo());
                    }
                }
            }
            entity = em.merge(entity);
            singletonMenu.limparMenusUsuario(entity);
        } catch (Exception ex) {
            logger.error("Problema ao alterar", ex);
        }
        return entity;
    }

    @Override
    public void salvarNovo(UsuarioSistema entity) {
        for (ArquivoUsuarioSistema a : entity.getArquivoUsuarioSistemas()) {
            if (a.getArquivo().getId() == null) {
                if (!a.getExcluido()) {
                    salvarArquivo((UploadedFile) a.getFile(), a.getArquivo());
                }
            }
        }
        em.persist(entity);
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public LotacaoVistoriadoraFacade getLotacaoVistoriadoraFacade() {
        return lotacaoVistoriadoraFacade;
    }

    public Boolean jaExisteUsuarioComPessoa(UsuarioSistema usuario) {
        String hql;
        if (usuario.getId() != null) {
            hql = "from UsuarioSistema u where u.expira is false and u.pessoaFisica = :pes and u.id != :usu";
        } else {
            hql = "from UsuarioSistema u where u.expira is false and u.pessoaFisica = :pes";
        }
        Query q = em.createQuery(hql);
        q.setParameter("pes", usuario.getPessoaFisica());
        if (usuario.getId() != null) {
            q.setParameter("usu", usuario.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public UsuarioSistema usuarioSistemaDaPessoa(Pessoa pessoaFisica) {
        String hql = "from UsuarioSistema u where u.expira is false and u.pessoaFisica = :pes";
        Query q = em.createQuery(hql);
        q.setParameter("pes", pessoaFisica);
        List<UsuarioSistema> retorno = q.getResultList();
        if (retorno.isEmpty()) {
            return null;
        }
        //System.out.println("Achou o usuario");
        return retorno.get(0);
    }

    public Boolean jaExisteUsuarioComLogin(UsuarioSistema usuario) {
        String hql;
        if (usuario.getId() != null) {
            hql = "from UsuarioSistema u where u.expira is false and u.login = :login and u.id != :usu";
        } else {
            hql = "from UsuarioSistema u where u.expira is false and u.login = :login";
        }
        Query q = em.createQuery(hql);
        q.setParameter("login", usuario.getLogin());
        if (usuario.getId() != null) {
            q.setParameter("usu", usuario.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public RecursoSistemaFacade getRecursoSistemaFacade() {
        return recursoSistemaFacade;
    }

    public void setRecursoSistemaFacade(RecursoSistemaFacade recursoSistemaFacade) {
        this.recursoSistemaFacade = recursoSistemaFacade;
    }

    public List<UnidadeOrganizacional> unidadesOrganizacionaisGestorProtocolo(UsuarioSistema usuarioCorrente) {
        List<UnidadeOrganizacional> lista = new ArrayList<>();
        for (UsuarioUnidadeOrganizacional uni : usuarioCorrente.getUsuarioUnidadeOrganizacional()) {
            if (uni.getGestorProtocolo()) {
                lista.add(uni.getUnidadeOrganizacional());
            }
        }
        return lista;
    }

    public List<UnidadeOrganizacional> unidadesOrganizacionaisGestorMateriais(UsuarioSistema usuarioCorrente) {
        List<UnidadeOrganizacional> lista = new ArrayList<>();
        for (UsuarioUnidadeOrganizacional uni : usuarioCorrente.getUsuarioUnidadeOrganizacional()) {
            if (uni.getGestorMateriais()) {
                lista.add(uni.getUnidadeOrganizacional());
            }
        }
        return lista;
    }

    public List<UnidadeOrganizacional> unidadesOrganizacionaisGestorLicitacao(UsuarioSistema usuarioCorrente) {
        List<UnidadeOrganizacional> lista = new ArrayList<>();
        for (UsuarioUnidadeOrganizacional uni : usuarioCorrente.getUsuarioUnidadeOrganizacional()) {
            if (uni.getGestorLicitacao()) {
                lista.add(uni.getUnidadeOrganizacional());
            }
        }
        return lista;
    }

    public List<TipoUsuarioTribUsuario> listaTipoUsuarioTribUsuario(VigenciaTribUsuario vigencia) {
        Query q = em.createQuery("from TipoUsuarioTribUsuario tipo where tipo.vigenciaTribUsuario = :vigencia");
        q.setParameter("vigencia", vigencia);
        return q.getResultList();
    }

    public List<LotacaoTribUsuario> listaLotacaoTribUsuario(VigenciaTribUsuario vigencia) {
        Query q = em.createQuery("from LotacaoTribUsuario lotacao where lotacao.vigenciaTribUsuario = :vigencia");
        q.setParameter("vigencia", vigencia);
        return q.getResultList();
    }

    public List<UsuarioSistema> listaUsuarioTributarioVigentePorTipo(TipoUsuarioTributario tipo) {
        String hql = "select v.usuarioSistema from VigenciaTribUsuario v "
            + " join v.tipoUsuarioTribUsuarios t "
            + " where (:data between v.vigenciaInicial and coalesce(v.vigenciaFinal, :data)) "
            + " and t.tipoUsuarioTributario = :tipo "
            + " and v.usuarioSistema.expira = false "
            + " order by v.usuarioSistema.pessoaFisica.nome";
        Query q = em.createQuery(hql);
        q.setParameter("data", new Date());
        q.setParameter("tipo", tipo);
        return q.getResultList();
    }

    public TipoUsuarioTribUsuario listaTipoUsuarioVigenteDoUsuarioPorTipo(UsuarioSistema usuario, TipoUsuarioTributario... tipos) {
        String hql = "select distinct t from VigenciaTribUsuario v "
            + " join v.tipoUsuarioTribUsuarios t "
            + " where (:data between v.vigenciaInicial and coalesce(v.vigenciaFinal, :data)) "
            + " and v.usuarioSistema = :usuario ";
        hql += " and ( ";
        for (TipoUsuarioTributario tipo : tipos) {
            hql += " t.tipoUsuarioTributario = '" + tipo.name() + "' or ";
        }
        hql = hql.substring(0, hql.length() - 3);
        hql += ")";
        Query q = em.createQuery(hql);
        q.setParameter("data", new Date());
        q.setParameter("usuario", usuario);
        List<TipoUsuarioTribUsuario> listaTipo = q.getResultList();
        if (!listaTipo.isEmpty()) {
            return listaTipo.get(0);
        }
        return null;
    }

    public List<UsuarioSistema> listaFiltrandoUsuarioSistemaPorTipo(String parte, TipoUsuarioTributario... tipos) {
        String hql = "select distinct u from UsuarioSistema u "
            + " join u.pessoaFisica pf "
            + " left join u.vigenciaTribUsuarios v "
            + " left join v.tipoUsuarioTribUsuarios t "
            + " where (:data between v.vigenciaInicial and coalesce(v.vigenciaFinal, :data)) "
            + " and (lower(u.login) like :parte "
            + " or lower(pf.nome) like :parte) ";
        hql += " and (";
        for (TipoUsuarioTributario tipo : tipos) {
            hql += " t.tipoUsuarioTributario = '" + tipo.name() + "' or ";
        }
        hql = hql.substring(0, hql.length() - 3);
        hql += ")";

        Query q = em.createQuery(hql);
        q.setParameter("data", new Date());
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<UsuarioSistema> listaFiltrandoUsuarioSistemaPorTipo2(String parte, TipoUsuarioTributario... tipos) {
        String tiposParam = "";
        for (TipoUsuarioTributario tipo : tipos) {
            tiposParam += "'" + tipo.name() + "',";
        }
        tiposParam = tiposParam.substring(0, tiposParam.length() - 1);

        String sql = "SELECT DISTINCT u.* FROM usuariosistema u "
            + " INNER JOIN vigenciatribusuario vigencia ON vigencia.usuariosistema_id = u.id "
            + " INNER JOIN pessoafisica p ON u.pessoafisica_id = p.id "
            + " WHERE vigencia.id IN(SELECT tipo.vigenciatribusuario_id "
            + "                      FROM tipousuariotribusuario tipo "
            + "                      WHERE tipo.tipousuariotributario IN (" + tiposParam + ")) "
            + " AND upper(u.login) LIKE :parte ";

        Query q = em.createNativeQuery(sql, UsuarioSistema.class);
        q.setParameter("parte", "%" + parte.toUpperCase().trim() + "%");
        return q.getResultList();
    }

    public List<UsuarioSistema> buscarUsuariosDaUnidadeOrganizacionalProtocolo(UnidadeOrganizacional unidadeOrganizacional) {
        List<UsuarioSistema> usuarios = singletonAdministrativo.getGestoresProtocoloPorUnidadeOrganizacional(unidadeOrganizacional);
        if (usuarios == null) {
            String sql = "SELECT distinct usu.* FROM USUARIOUNIDADEORGANIZACIO usuUni "
                + " INNER JOIN UsuarioSistema usu ON usu.id = usuUni.usuarioSistema_id "
                + " WHERE usuUni.gestorProtocolo = 1 AND usuUni.unidadeOrganizacional_id = :unidade "
                + "  and usu.expira = 0 ";
            Query q = em.createNativeQuery(sql, UsuarioSistema.class);
            q.setParameter("unidade", unidadeOrganizacional.getId());
            usuarios = q.getResultList();
            singletonAdministrativo.addGestoresProtocoloPorUnidadeOrganizacional(unidadeOrganizacional, usuarios);
        }
        return usuarios;
    }

    public List<UnidadeOrganizacional> listaUnidadeSemUsuarioCadastrado() {
        String sql = "SELECT uni.* FROM VwHierarquiaAdministrativa ho "
            + " INNER JOIN UnidadeOrganizacional uni ON uni.id = ho.subordinada_id "
            + " WHERE to_date(:data,'dd/MM/yyyy') BETWEEN ho.iniciovigencia AND coalesce(ho.fimVigencia,to_date(:data,'dd/MM/yyyy'))  "
            + " AND uni.id NOT IN ("
            + " SELECT uni.id FROM UsuarioUnidadeOrganizacio usuuni "
            + " INNER JOIN UnidadeOrganizacional uni ON uni.Id = usuuni.UnidadeOrganizacional_Id "
            + " WHERE usuuni.gestorProtocolo = 1)";
        Query q = em.createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("data", DataUtil.getDataFormatada(getSistemaControlador().getDataOperacao()));
        return q.getResultList();
    }


    public List<HierarquiaOrganizacional> filtrandoHierarquiaOrganizacionalAdministrativaComUsuarios(String parte, Date dataOperacao) {
        String sql = "SELECT h.* "
            + " FROM HierarquiaOrganizacional h "
            + " WHERE to_date(:data,'dd/MM/yyyy') BETWEEN h.iniciovigencia AND coalesce(h.fimVigencia, to_date(:data,'dd/MM/yyyy')) "
            + " AND exists (SELECT distinct 1 FROM usuariounidadeorganizacio uuo "
            + " inner join usuariosistema usu on uuo.usuarioSistema_id = usu.id "
            + " WHERE uuo.gestorprotocolo = 1 AND uuo.unidadeorganizacional_id = h.subordinada_id "
            + " and usu.expira = 0) "
            + " AND (lower(h.descricao) LIKE :parte or h.codigo like :parte or replace(h.codigo, '.', '') like :parte )"
            + " AND h.TIPOHIERARQUIAORGANIZACIONAL = :tipoHierarquia "
            + " ORDER BY h.codigo ASC ";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("data", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<UsuarioSistema> recuperarUsuariosGestoresDeMateriaisDaUnidade(UnidadeOrganizacional uo, String filtro) {
        String sql = "SELECT US.* " +
            "       FROM USUARIOSISTEMA US " +
            " INNER JOIN USUARIOUNIDADEORGANIZACIO UUO ON UUO.USUARIOSISTEMA_ID = US.ID" +
            " INNER JOIN PESSOAFISICA PF ON PF.ID = US.PESSOAFISICA_ID " +
            " INNER JOIN PESSOA PS ON PS.ID = PF.ID" +
            "      WHERE UUO.GESTORMATERIAIS = 1 " +
            "        AND (Lower(PF.NOME) LIKE :FILTRO or replace(replace(pf.cpf, '-', ''), '.', '') like :FILTRO)" +
            "        AND PS.SITUACAOCADASTRALPESSOA = :situacao " +
            "        AND UUO.UNIDADEORGANIZACIONAL_ID = :id_unidade" +
            " AND US.EXPIRA <> 1 " +
            " ORDER BY pf.nome";
        return em.createNativeQuery(sql, UsuarioSistema.class)
            .setParameter("id_unidade", uo.getId())
            .setParameter("situacao", SituacaoCadastralPessoa.ATIVO.name())
            .setParameter("FILTRO", "%" + filtro.toLowerCase().trim() + "%")
            .setMaxResults(50)
            .getResultList();


    }

    public UsuarioSistema findOneByLogin(String login) {
        String sql = " SELECT * FROM UsuarioSistema us WHERE us.login = :login";
        Query q = em.createNativeQuery(sql, UsuarioSistema.class);
        q.setParameter("login", login);
        List<UsuarioSistema> retorno = q.getResultList();
        if (retorno != null && !retorno.isEmpty()) {
            return retorno.get(0);
        }
        return null;
    }

    public boolean validaLoginSenha(String login, String senha) {
        UsuarioSistema usuarioSistema = findOneByLogin(login);
        if (usuarioSistema != null) {
            return (usuarioSistema.senhaValida(Seguranca.md5(senha)));
        }
        return false;
    }

    public boolean validaAutorizacaoUsuarioLogado(AutorizacaoTributario autorizacao) {
        UsuarioSistema usuarioLogado = sistemaFacade.getUsuarioCorrente();
        return validaAutorizacaoUsuario(usuarioLogado, autorizacao);
    }

    public boolean validaAutorizacaoUsuario(UsuarioSistema usuario, AutorizacaoTributario autorizacao) {
        String sql = "SELECT USU.* FROM USUARIOSISTEMA USU " +
            "INNER JOIN VIGENCIATRIBUSUARIO VIG ON VIG.USUARIOSISTEMA_ID = USU.ID " +
            "INNER JOIN AUTORIZACAOTRIBUSUARIO AUT ON AUT.VIGENCIATRIBUSUARIO_ID = VIG.ID " +
            "WHERE USU.ID = :usuario " +
            "AND AUT.AUTORIZACAO = :autorizacao " +
            "AND VIG.VIGENCIAFINAL IS NULL";
        Query q = em.createNativeQuery(sql);
        q.setParameter("usuario", usuario.getId());
        q.setParameter("autorizacao", autorizacao.name());
        return !q.getResultList().isEmpty();
    }

    public List<UsuarioSistema> completarUsuarioSistemaPeloNomePessoaFisica(String filtro) {
        String hql = " select us " +
            "      from UsuarioSistema us" +
            "   inner join us.pessoaFisica pf" +
            "   where (Lower(pf.nome) like :filtro)  " +
            "         and us.expira = :expira " +
            "         and us.senha is not null" +
            "         and pf.situacaoCadastralPessoa = :situacao" +
            "         order by pf.nome";
        Query q = em.createQuery(hql, UsuarioSistema.class);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q.setParameter("situacao", SituacaoCadastralPessoa.ATIVO);
        q.setParameter("expira", Boolean.FALSE);
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<UsuarioSistema> completaUsuarioSistemaAtivoPeloLogin(String filtro) {
        String sql = "SELECT "
            + "  USU.* "
            + "FROM USUARIOSISTEMA USU "
            + "INNER JOIN PESSOAFISICA PF ON PF.ID = USU.PESSOAFISICA_ID "
            + "INNER JOIN PESSOA P ON P.ID = PF.ID "
            + "WHERE P.SITUACAOCADASTRALPESSOA = :situacao "
            + "AND (USU.LOGIN LIKE :filtro "
            + "  OR LOWER(PF.NOME) LIKE :filtro) "
            + "AND USU.EXPIRA = :expira "
            + "AND USU.SENHA IS NOT NULL "
            + "ORDER BY USU.LOGIN ";

        Query q = em.createNativeQuery(sql, UsuarioSistema.class);
        q.setParameter("expira", Boolean.FALSE);
        q.setParameter("situacao", SituacaoCadastralPessoa.ATIVO.name());
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");

        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<UsuarioSistema> buscarFiscalTributarioPorLoginOrNome(String filtro) {
        String sql = " select usu.* " +
            "    from usuariosistema usu " +
            "   inner join pessoafisica pf on pf.id = usu.pessoafisica_id " +
            "   inner join pessoa p on p.id = pf.id " +
            " where p.situacaocadastralpessoa = :situacao " +
            "   and (usu.login like :filtro or lower(pf.nome) like :filtro) " +
            "   and coalesce(usu.expira, 1) = :expira " +
            "   and usu.senha is not null " +
            "   and exists (select vtu.id from vigenciatribusuario vtu " +
            "               inner join tipousuariotribusuario tu on tu.vigenciatribusuario_id = vtu.id " +
            "               where vtu.usuariosistema_id = usu.id " +
            "                 and current_date between vtu.vigenciainicial and coalesce(vtu.vigenciafinal, current_date) " +
            "                 and tu.tipousuariotributario = :fiscal_tributario) " +
            " order by usu.login ";
        Query q = em.createNativeQuery(sql, UsuarioSistema.class);
        q.setParameter("expira", Boolean.FALSE);
        q.setParameter("situacao", SituacaoCadastralPessoa.ATIVO.name());
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q.setParameter("fiscal_tributario", TipoUsuarioTributario.FISCAL_TRIBUTARIO.name());
        return q.getResultList();
    }

    public List<UsuarioSistema> buscarTodosUsuariosPorLoginOuNome(String filtro) {
        String sql = "SELECT "
            + "  USU.* "
            + "FROM USUARIOSISTEMA USU "
            + "INNER JOIN PESSOAFISICA PF ON PF.ID = USU.PESSOAFISICA_ID "
            + "INNER JOIN PESSOA P ON P.ID = PF.ID "
            + "WHERE USU.EXPIRA = :expira "
            + "  AND (USU.LOGIN LIKE :filtro OR LOWER(PF.NOME) LIKE :filtro) "
            + "ORDER BY USU.LOGIN ";

        Query q = em.createNativeQuery(sql, UsuarioSistema.class);
        q.setParameter("expira", Boolean.FALSE);
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<UsuarioSistema> buscarTodosUsuariosPorLoginOuNomeOuCpf(String filtro) {
        String sql = "SELECT "
            + "  USU.* "
            + " FROM USUARIOSISTEMA USU "
            + " INNER JOIN PESSOAFISICA PF ON PF.ID = USU.PESSOAFISICA_ID "
            + " where (USU.LOGIN LIKE :filtro OR LOWER(PF.NOME) LIKE :filtro) "
            + "   or replace(replace(replace(pf.cpf,'.',''),'-',''), '/', '') like :cpf "
            + "  ORDER BY USU.LOGIN ";

        Query q = em.createNativeQuery(sql, UsuarioSistema.class);
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        q.setParameter("cpf", "%" + filtro.toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%");
        q.setMaxResults(50);
        return q.getResultList();

    }

    public List<UsuarioSistema> completarUsuarioGestorPatrimonioDaUnidade(UnidadeOrganizacional unidade, String parte) {
        String sql = "SELECT us.* " +
            "FROM usuariosistema us " +
            "INNER JOIN usuariounidadeorganizacio uuo ON uuo.usuariosistema_id = us.id " +
            "INNER JOIN pessoafisica pf ON pf.id = us.pessoafisica_id " +
            "WHERE uuo.gestorpatrimonio = 1 " +
            "  AND uuo.unidadeorganizacional_id = :unidade_id " +
            "  AND (lower(us.login) LIKE :parte OR " +
            "       lower(pf.nome) LIKE :parte)";

        Query q = em.createNativeQuery(sql, UsuarioSistema.class);
        q.setParameter("unidade_id", unidade.getId());
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");

        return q.getResultList();
    }

    public void inativaUsuario(UsuarioSistema usuarioSistema) {
        usuarioSistema.setExpira(true);
        em.merge(usuarioSistema);
    }

    public List<UsuarioSistema> recuperarUsuariosPorHierarquiasOrcamentaris(List<HierarquiaOrganizacional> hierarquias, Date data) {
        return recuperarUsuariosPorHierarquiasOrcamentaris(hierarquias, data, false);
    }

    public List<UsuarioSistema> recuperarUsuariosPorHierarquiasOrcamentaris(List<HierarquiaOrganizacional> hierarquias, Date data, boolean somenteAtivos) {
        List<Long> listaIdHierarquias = new ArrayList<>();
        for (HierarquiaOrganizacional unidade : hierarquias) {
            listaIdHierarquias.add(unidade.getId());
        }

        String sql = "SELECT usuario.* FROM usuariosistema usuario " +
            " INNER JOIN USUARIOUNIDADEORGANIZACORC uo_usu ON usuario.id = uo_usu.usuariosistema_id " +
            " INNER JOIN unidadeorganizacional uo ON uo_usu.unidadeorganizacional_id = uo.id " +
            " INNER JOIN vwhierarquiaorcamentaria vw ON uo.id = vw.subordinada_id " +
            " AND to_date(:data,'dd/mm/yyyy') BETWEEN vw.iniciovigencia AND coalesce(vw.fimvigencia,to_date(:data,'dd/mm/yyyy'))" +
            " AND vw.id IN :idHierarquias";
        if (somenteAtivos) {
            sql += " and coalesce(usuario.expira,0) = :expira ";
        }
        Query consulta = em.createNativeQuery(sql, UsuarioSistema.class);
        consulta.setParameter("data", DataUtil.getDataFormatada(data));
        consulta.setParameter("idHierarquias", listaIdHierarquias);
        if (somenteAtivos) {
            consulta.setParameter("expira", Boolean.FALSE);
        }
        return consulta.getResultList();
    }

    public List<UsuarioSistema> recuperarUsuariosPorHierarquiasAdministrativas(List<HierarquiaOrganizacional> hierarquias, Date data, String parte) {
        return recuperarUsuariosPorHierarquiasAdministrativas(hierarquias, data, parte, false);
    }

    public List<UsuarioSistema> recuperarUsuariosPorHierarquiasAdministrativas(List<HierarquiaOrganizacional> hierarquias, Date data, String parte, boolean somenteAtivos) {
        if (hierarquias == null || hierarquias.size() <= 0) {
            return new ArrayList();
        }
        List<Long> listaIdHierarquias = new ArrayList<>();
        for (HierarquiaOrganizacional unidade : hierarquias) {
            listaIdHierarquias.add(unidade.getId());
        }

        String sql = "SELECT DISTINCT usuario.* FROM usuariosistema usuario " +
            " INNER JOIN pessoafisica pf ON pf.id = usuario.pessoafisica_id " +
            " INNER JOIN usuariounidadeorganizacio ua_usu ON usuario.id = ua_usu.usuariosistema_id " +
            " INNER JOIN unidadeorganizacional ua ON ua_usu.unidadeorganizacional_id = ua.id " +
            " INNER JOIN vwhierarquiaadministrativa vw ON ua.id = vw.subordinada_id " +
            " AND to_date(:data,'dd/mm/yyyy') BETWEEN vw.iniciovigencia AND coalesce(vw.fimvigencia,to_date(:data,'dd/mm/yyyy'))" +
            " AND vw.id IN :idHierarquias " +
            " WHERE (lower(usuario.login) LIKE :parte OR lower(pf.nome) LIKE :parte) ";
        if (somenteAtivos) {
            sql += " and coalesce(usuario.expira,0) = :expira ";
        }
        Query consulta = em.createNativeQuery(sql, UsuarioSistema.class);

        consulta.setParameter("data", DataUtil.getDataFormatada(data));
        consulta.setParameter("idHierarquias", listaIdHierarquias);
        consulta.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        if (somenteAtivos) {
            consulta.setParameter("expira", Boolean.FALSE);
        }
        return consulta.getResultList();
    }

    public Boolean isGestorFinanceiro(UsuarioSistema usuarioCorrente, UnidadeOrganizacional unidadeOrganizacionalAdministrativaCorrente, UnidadeOrganizacional unidadeOrcamentaria, Date data) {
        String sql = "SELECT u.* FROM USUARIOSISTEMA u"
            + " INNER JOIN USUARIOUNIDADEORGANIZACIO uo ON uo.USUARIOSISTEMA_ID=u.ID"
            + " INNER JOIN UNIDADEORGANIZACIONAL und ON und.ID=uo.UNIDADEORGANIZACIONAL_ID"
            + " INNER JOIN HIERARQUIAORGANIZACIONAL HO_ADM ON HO_ADM.SUBORDINADA_ID=UND.ID"
            + " INNER JOIN HIERARQUIAORGCORRE COR ON HO_ADM.id = cor.hierarquiaorgadm_id"
            + " INNER JOIN HIERARQUIAORGANIZACIONAL HO_ORC ON HO_ORC.ID = cor.hierarquiaorgorc_id"
            + " WHERE HO_ADM.TIPOHIERARQUIAORGANIZACIONAL ='ADMINISTRATIVA'"
            + " AND HO_ORC.TIPOHIERARQUIAORGANIZACIONAL ='ORCAMENTARIA'"
            + " AND to_date(:data, 'dd/mm/yyyy') BETWEEN HO_ADM.inicioVigencia AND coalesce(HO_ADM.fimVigencia,to_date(:data, 'dd/mm/yyyy'))"
            + " AND to_date(:data, 'dd/mm/yyyy') BETWEEN HO_ORC.inicioVigencia AND coalesce(HO_ORC.fimVigencia,to_date(:data, 'dd/mm/yyyy'))"
            + " AND u.id = :idUsuario"
            + " AND und.ID = :idUnidade"
            + " AND HO_ORC.SUBORDINADA_ID = :idUnidadeorc"
            + " AND uo.gestorFinanceiro = 1 ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("idUsuario", usuarioCorrente.getId());
        consulta.setParameter("idUnidade", unidadeOrganizacionalAdministrativaCorrente.getId());
        consulta.setParameter("idUnidadeorc", unidadeOrcamentaria.getId());
        consulta.setParameter("data", DataUtil.getDataFormatada(data));
        try {
            return !consulta.getResultList().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isGestorLicitacao(UsuarioSistema usuario, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = "SELECT DISTINCT u.* FROM USUARIOSISTEMA u " +
            "INNER JOIN USUARIOUNIDADEORGANIZACIO uo ON uo.USUARIOSISTEMA_ID=u.ID " +
            "INNER JOIN UNIDADEORGANIZACIONAL und ON und.ID=uo.UNIDADEORGANIZACIONAL_ID " +
            "INNER JOIN HIERARQUIAORGANIZACIONAL HO_ADM ON HO_ADM.SUBORDINADA_ID=UND.ID " +
            "WHERE HO_ADM.TIPOHIERARQUIAORGANIZACIONAL = :tipo " +
            "AND u.id = :idUsuario " +
            "AND und.ID = :idUnidade " +
            "AND uo.GESTORLICITACAO = 1";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipo", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("idUsuario", usuario.getId());
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        try {
            if (!q.getResultList().isEmpty()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean isGestorOrcamento(UsuarioSistema usuarioCorrente, UnidadeOrganizacional unidadeOrcamentaria, Date data) {
        String sql = "SELECT u.* FROM USUARIOSISTEMA u"
            + " INNER JOIN USUARIOUNIDADEORGANIZACIO uo ON uo.USUARIOSISTEMA_ID=u.ID"
            + " INNER JOIN UNIDADEORGANIZACIONAL und ON und.ID=uo.UNIDADEORGANIZACIONAL_ID"
            + " INNER JOIN HIERARQUIAORGANIZACIONAL HO_ADM ON HO_ADM.SUBORDINADA_ID=UND.ID"
            + " INNER JOIN HIERARQUIAORGCORRE COR ON HO_ADM.id = cor.hierarquiaorgadm_id"
            + " INNER JOIN HIERARQUIAORGANIZACIONAL HO_ORC ON HO_ORC.ID = cor.hierarquiaorgorc_id"
            + " WHERE HO_ADM.TIPOHIERARQUIAORGANIZACIONAL ='ADMINISTRATIVA'"
            + " AND HO_ORC.TIPOHIERARQUIAORGANIZACIONAL ='ORCAMENTARIA'"
            + " AND to_date(:data, 'dd/mm/yyyy') BETWEEN HO_ADM.inicioVigencia AND coalesce(HO_ADM.fimVigencia,to_date(:data, 'dd/mm/yyyy'))"
            + " AND to_date(:data, 'dd/mm/yyyy') BETWEEN HO_ORC.inicioVigencia AND coalesce(HO_ORC.fimVigencia,to_date(:data, 'dd/mm/yyyy'))"
            + " AND u.id = :idUsuario"
            + " AND HO_ORC.SUBORDINADA_ID = :idUnidadeorc"
            + " AND uo.gestorOrcamento = 1";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("idUsuario", usuarioCorrente.getId());
        consulta.setParameter("idUnidadeorc", unidadeOrcamentaria.getId());
        consulta.setParameter("data", DataUtil.getDataFormatada(data));
        try {
            if (!consulta.getResultList().isEmpty()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isGestorControleInterno(UsuarioSistema usuarioCorrente, UnidadeOrganizacional unidadeOrcamentaria, Date data) {
        String sql = "SELECT u.* FROM USUARIOSISTEMA u"
            + " INNER JOIN USUARIOUNIDADEORGANIZACIO uo ON uo.USUARIOSISTEMA_ID=u.ID"
            + " INNER JOIN UNIDADEORGANIZACIONAL und ON und.ID=uo.UNIDADEORGANIZACIONAL_ID"
            + " INNER JOIN HIERARQUIAORGANIZACIONAL HO_ADM ON HO_ADM.SUBORDINADA_ID=UND.ID"
            + " INNER JOIN HIERARQUIAORGCORRE COR ON HO_ADM.id = cor.hierarquiaorgadm_id"
            + " INNER JOIN HIERARQUIAORGANIZACIONAL HO_ORC ON HO_ORC.ID = cor.hierarquiaorgorc_id"
            + " WHERE HO_ADM.TIPOHIERARQUIAORGANIZACIONAL = :administrativa "
            + " AND HO_ORC.TIPOHIERARQUIAORGANIZACIONAL = :orcamentaria "
            + " AND to_date(:dataOperacao, 'dd/MM/yyyy') BETWEEN trunc(HO_ADM.inicioVigencia) AND coalesce(trunc(HO_ADM.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))"
            + " AND to_date(:dataOperacao, 'dd/MM/yyyy') BETWEEN trunc(HO_ORC.inicioVigencia) AND coalesce(trunc(HO_ORC.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))"
            + " AND u.id = :idUsuario"
            + " AND HO_ORC.SUBORDINADA_ID = :idUnidadeorc"
            + " AND uo.gestorControleInterno = 1";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("idUsuario", usuarioCorrente.getId());
        consulta.setParameter("idUnidadeorc", unidadeOrcamentaria.getId());
        consulta.setParameter("dataOperacao", DataUtil.getDataFormatada(data));
        consulta.setParameter("administrativa", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        consulta.setParameter("orcamentaria", TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
        return !consulta.getResultList().isEmpty();
    }

    public Boolean isGestorProtocolo(UsuarioSistema usuarioCorrente, UnidadeOrganizacional unidadeAdministrativa, Date data) {
        String sql = "select u.* from usuariosistema u"
            + " inner join usuariounidadeorganizacio uo on uo.usuariosistema_id = u.id"
            + " inner join hierarquiaorganizacional ho_adm on ho_adm.subordinada_id = uo.unidadeorganizacional_id "
            + " where ho_adm.tipohierarquiaorganizacional = :tipoHierarquia"
            + " and to_date(:data, 'dd/mm/yyyy') between trunc(ho_adm.iniciovigencia) and coalesce(trunc(ho_adm.fimvigencia),to_date(:data, 'dd/mm/yyyy'))"
            + " and u.id = :idUsuario "
            + " and ho_adm.subordinada_id = :idUnidade "
            + " and uo.gestorProtocolo = 1 ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idUsuario", usuarioCorrente.getId());
        q.setParameter("idUnidade", unidadeAdministrativa.getId());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        try {
            return !q.getResultList().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isGestorPatrimonio(UsuarioSistema usuario, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = "SELECT DISTINCT u.* FROM USUARIOSISTEMA u " +
            "INNER JOIN USUARIOUNIDADEORGANIZACIO uo ON uo.USUARIOSISTEMA_ID=u.ID " +
            "INNER JOIN UNIDADEORGANIZACIONAL und ON und.ID=uo.UNIDADEORGANIZACIONAL_ID " +
            "INNER JOIN HIERARQUIAORGANIZACIONAL HO_ADM ON HO_ADM.SUBORDINADA_ID=UND.ID " +
            "WHERE HO_ADM.TIPOHIERARQUIAORGANIZACIONAL = :tipo " +
            "AND u.id = :idUsuario " +
            "AND und.ID = :idUnidade " +
            "AND uo.gestorpatrimonio = :gestor ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipo", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("idUsuario", usuario.getId());
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        q.setParameter("gestor", Boolean.TRUE);
        try {
            return !q.getResultList().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public List<UsuarioUnidadeOrganizacional> buscarUnidadesDoUsuarioAdministrativaPorCodigoOrDescricao(UsuarioSistema usuarioCorrente, Date data, String filtro) {
        String sql = "SELECT uo.* FROM USUARIOSISTEMA u "
            + " INNER JOIN USUARIOUNIDADEORGANIZACIO uo ON uo.USUARIOSISTEMA_ID = u.ID "
            + " INNER JOIN UNIDADEORGANIZACIONAL und ON und.ID = uo.UNIDADEORGANIZACIONAL_ID "
            + " INNER JOIN vwhierarquiaadministrativa vwAdm ON vwAdm.SUBORDINADA_ID = UND.ID "
            + " WHERE to_date(:data, 'dd/mm/yyyy') BETWEEN vwAdm.inicioVigencia AND coalesce(vwAdm.fimVigencia,to_date(:data, 'dd/mm/yyyy'))"
            + " AND u.id = :idUsuario"
            + " AND (lower(vwAdm.descricao) like :filtro or vwAdm.codigo like :filtro)";
        Query consulta = em.createNativeQuery(sql, UsuarioUnidadeOrganizacional.class);
        consulta.setParameter("idUsuario", usuarioCorrente.getId());
        consulta.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        consulta.setParameter("data", DataUtil.getDataFormatada(data));
        if (!consulta.getResultList().isEmpty()) {
            return consulta.getResultList();
        }
        return Lists.newArrayList();
    }

    public void atualizarVersaoUsuario(UsuarioSistema usuarioSistema, String versao) {
        em.createNativeQuery("update usuarioSistema set ultimaVersao = :versao where id = :id")
            .setParameter("versao", versao)
            .setParameter("id", usuarioSistema.getId())
            .executeUpdate();
    }

    public void executarDependenciasSalvarUsuario(UsuarioSistema usuarioSistema) {
        if (usuarioSistema.getId() != null) {
            LeitorMenuFacade leitorMenuFacade = (LeitorMenuFacade) Util.getSpringBeanPeloNome("leitorMenuFacade");
            leitorMenuFacade.limparMenusUsuario(usuarioSistema);
            ((SingletonRecursosSistema) Util.getSpringBeanPeloNome("singletonRecursosSistema")).limparUsuario(usuarioSistema);
            Push.sendTo(usuarioSistema, "recarregarPagina");
            singletonAdministrativo.clearGestoresProtocoloPorUnidadeOrganizacional();
        }
    }

    public AssinaturaUsuarioSistema buscarAssinaturaUsuarioSistema(Long idUsuario) {
        String sql = " select aus.* from assinaturausuariosistema aus " +
            " where aus.usuariosistema_id = :idUsuario ";

        Query q = em.createNativeQuery(sql, AssinaturaUsuarioSistema.class);
        q.setParameter("idUsuario", idUsuario);

        List<AssinaturaUsuarioSistema> assinaturas = q.getResultList();
        return (assinaturas != null && !assinaturas.isEmpty()) ? assinaturas.get(0) : null;
    }

    public List<UsuarioSistema> buscarUsuariosGestoresInternos(UnidadeOrganizacional unidadeOrcamentaria, Date data) {
        String sql = "SELECT u.* FROM USUARIOSISTEMA u"
            + " INNER JOIN USUARIOUNIDADEORGANIZACIO uo ON uo.USUARIOSISTEMA_ID=u.ID"
            + " INNER JOIN UNIDADEORGANIZACIONAL und ON und.ID=uo.UNIDADEORGANIZACIONAL_ID"
            + " INNER JOIN HIERARQUIAORGANIZACIONAL HO_ADM ON HO_ADM.SUBORDINADA_ID=UND.ID"
            + " INNER JOIN HIERARQUIAORGCORRE COR ON HO_ADM.id = cor.hierarquiaorgadm_id"
            + " INNER JOIN HIERARQUIAORGANIZACIONAL HO_ORC ON HO_ORC.ID = cor.hierarquiaorgorc_id"
            + " WHERE HO_ADM.TIPOHIERARQUIAORGANIZACIONAL = :administrativa "
            + " AND HO_ORC.TIPOHIERARQUIAORGANIZACIONAL = :orcamentaria "
            + " AND to_date(:dataOperacao, 'dd/MM/yyyy') BETWEEN trunc(HO_ADM.inicioVigencia) AND coalesce(trunc(HO_ADM.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))"
            + " AND to_date(:dataOperacao, 'dd/MM/yyyy') BETWEEN trunc(HO_ORC.inicioVigencia) AND coalesce(trunc(HO_ORC.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))"
            + " AND HO_ORC.SUBORDINADA_ID = :idUnidadeorc"
            + " AND uo.gestorControleInterno = 1 ";
        Query q = em.createNativeQuery(sql, UsuarioSistema.class);
        q.setParameter("idUnidadeorc", unidadeOrcamentaria.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(data));
        q.setParameter("administrativa", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("orcamentaria", TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
        return q.getResultList();
    }

    public boolean isFiscalTributario() {
        UsuarioSistema usuarioCorrente = sistemaFacade.getUsuarioCorrente();
        usuarioCorrente = recuperar(usuarioCorrente.getId());
        return usuarioCorrente.isFiscalTributario();
    }

    public List<Menu> getItensParaContruirMenu(UsuarioSistema us) {
        return singletonMenu.getMenusRecursosGrupoRecursos(us);
    }

    public Set<RecursoSistema> getTodosRecursosDoUsuario(UsuarioSistema usuarioSistema) {
        return singletonMenu.getTodosRecursosDoUsuario(usuarioSistema);
    }

    public List<UsuarioSistema> buscarUsuarioSistemaEnviouMensagemContribuinte(String parte) {
        return em.createNativeQuery(" select us.* from usuariosistema us " +
                "   inner join pessoafisica pf on pf.id = us.pessoafisica_id " +
                "   inner join pessoa p on p.id = pf.id " +
                " where p.situacaocadastralpessoa = :situacao " +
                "   and exists (select 1 from mensagemcontribuinte mc " +
                "               where mc.enviadapor_id = us.id) " +
                "   and (lower(us.login) like :parte or lower(pf.nome) like :parte)", UsuarioSistema.class)
            .setParameter("situacao", SituacaoCadastralPessoa.ATIVO.name())
            .setParameter("parte", "%" + parte.trim().toLowerCase() + "%")
            .getResultList();
    }
}
