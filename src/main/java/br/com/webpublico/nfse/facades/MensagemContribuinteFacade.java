package br.com.webpublico.nfse.facades;

import br.com.webpublico.entidades.ArquivoComposicao;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.TipoNotaFiscalServico;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.UsuarioSistemaFacade;
import br.com.webpublico.nfse.domain.MensagemContribuinte;
import br.com.webpublico.nfse.domain.MensagemContribuinteUsuario;
import br.com.webpublico.nfse.domain.dtos.FiltroMensagemContribuinte;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class MensagemContribuinteFacade extends AbstractFacade<MensagemContribuinte> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;

    public MensagemContribuinteFacade() {
        super(MensagemContribuinte.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public UsuarioSistema getUsuarioCorrente() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    @Override
    public MensagemContribuinte recuperar(Object id) {
        MensagemContribuinte mensagemContribuinte = super.recuperar(id);
        Hibernate.initialize(mensagemContribuinte.getDocumentos());
        if (mensagemContribuinte.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(mensagemContribuinte.getDetentorArquivoComposicao().getArquivosComposicao());
            for (ArquivoComposicao arquivoComposicao : mensagemContribuinte.getDetentorArquivoComposicao().getArquivosComposicao()) {
                Hibernate.initialize(arquivoComposicao.getArquivo().getPartes());
            }
        }
        return mensagemContribuinte;
    }


    public Integer contarMensagemContribuinteUsuarios(MensagemContribuinte mensagemContribuinte) {
        return ((Number) em.createNativeQuery(" select count(1) from mensagemcontribusuario mcu " +
                " where mcu.mensagemcontribuinte_id = :idMensagemContribuinte ")
            .setParameter("idMensagemContribuinte", mensagemContribuinte.getId())
            .getSingleResult()).intValue();
    }

    @Override
    public void remover(MensagemContribuinte entity) {
        removerUsuarios(entity);
        super.remover(entity);
    }

    private void removerUsuarios(MensagemContribuinte entity) {
        List<MensagemContribuinteUsuario> usuarios = buscarMensagemContribuinteUsuarios(entity, 0, 99999);
        while (!usuarios.isEmpty()) {
            for (MensagemContribuinteUsuario mensagemContribuinteUsuario : usuarios) {
                em.remove(mensagemContribuinteUsuario);
            }
            usuarios = buscarMensagemContribuinteUsuarios(entity, 0, 99999);
        }
    }

    private String createSqlQueryCadastrosEconomico(String select, FiltroMensagemContribuinte filtroMensagemContribuinte) {
        String sql = select +
            "   from cadastroeconomico ce " +
            "  inner join cadastro c on c.id = ce.id " +
            "  inner join enquadramentofiscal ef on ef.cadastroeconomico_id = ce.id and ef.fimvigencia is null " +
            "  inner join situacaocadastroeconomico sce on sce.id = (select max(rel.situacaocadastroeconomico_id) " +
            "                                                           from ce_situacaocadastral rel " +
            "                                                        where rel.cadastroeconomico_id = ce.id) " +
            " where sce.situacaoCadastral in (:ativo) and ef.tiponotafiscalservico = :eletronica ";
        if (filtroMensagemContribuinte.getTipoIssqn() != null) {
            sql += " and ef.tipoissqn = :tipoIssqn ";
        }
        if (!Strings.isNullOrEmpty(filtroMensagemContribuinte.getInscricaoInicial())) {
            sql += " and ce.inscricaocadastral >= :inscricaoInicial ";
        }
        if (!Strings.isNullOrEmpty(filtroMensagemContribuinte.getInscricaoFinal())) {
            sql += " and ce.inscricaocadastral <= :inscricaoFinal  ";
        }
        if (filtroMensagemContribuinte.getEnviaRPS() != null) {
            sql += " and case when ce.chaveacesso is not null then 1 else 0 end = :enviaRPS ";
        }
        if (filtroMensagemContribuinte.getEnviadaPor() != null
            || filtroMensagemContribuinte.getDataEnvioInicial() != null
            || filtroMensagemContribuinte.getDataEnvioFinal() != null) {
            sql += " and exists (select 1 " +
                "                   from mensagemcontribuinte mc " +
                "                  inner join mensagemcontribusuario mcu on mcu.mensagemcontribuinte_id = mc.id" +
                "                where mcu.cadastroeconomico_id = ce.id ";
            if (filtroMensagemContribuinte.getEnviadaPor() != null) {
                sql += " and mc.enviadapor_id = :enviadaPor ";
            }
            if (filtroMensagemContribuinte.getDataEnvioInicial() != null) {
                sql += " and mc.emitidaem >= :dataEnvioInicial ";
            }
            if (filtroMensagemContribuinte.getDataEnvioFinal() != null) {
                sql += " and mc.emitidaem <= :dataEnvioFinal ";
            }
            sql += ") ";
        }
        sql += " order by ce.inscricaocadastral ";
        return sql;
    }

    private static void setParametersQueryCadastrosEcnomico(FiltroMensagemContribuinte filtroMensagemContribuinte, Query q) {
        q.setParameter("ativo", Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name(), SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name()));
        q.setParameter("eletronica", TipoNotaFiscalServico.ELETRONICA.name());
        if (filtroMensagemContribuinte.getTipoIssqn() != null) {
            q.setParameter("tipoIssqn", filtroMensagemContribuinte.getTipoIssqn().name());
        }
        if (!Strings.isNullOrEmpty(filtroMensagemContribuinte.getInscricaoInicial())) {
            q.setParameter("inscricaoInicial", filtroMensagemContribuinte.getInscricaoInicial());
        }
        if (!Strings.isNullOrEmpty(filtroMensagemContribuinte.getInscricaoFinal())) {
            q.setParameter("inscricaoFinal", filtroMensagemContribuinte.getInscricaoFinal());
        }
        if (filtroMensagemContribuinte.getEnviaRPS() != null) {
            q.setParameter("enviaRPS", filtroMensagemContribuinte.getEnviaRPS());
        }
        if (filtroMensagemContribuinte.getEnviadaPor() != null) {
            q.setParameter("enviadaPor", filtroMensagemContribuinte.getEnviadaPor().getId());
        }
        if (filtroMensagemContribuinte.getDataEnvioInicial() != null) {
            q.setParameter("dataEnvioInicial", filtroMensagemContribuinte.getDataEnvioInicial());
        }
        if (filtroMensagemContribuinte.getDataEnvioFinal() != null) {
            q.setParameter("dataEnvioFinal", filtroMensagemContribuinte.getDataEnvioFinal());
        }
    }

    public Integer contarCadastrosEconomico(FiltroMensagemContribuinte filtroMensagemContribuinte) {
        String sql = createSqlQueryCadastrosEconomico(" select count(1) ", filtroMensagemContribuinte);
        Query q = em.createNativeQuery(sql);
        setParametersQueryCadastrosEcnomico(filtroMensagemContribuinte, q);
        return ((Number) q.getSingleResult()).intValue();
    }

    public List<CadastroEconomico> buscarCadastrosEconomico(FiltroMensagemContribuinte filtroMensagemContribuinte,
                                                            int firstResult, int maxResult) {
        String sql = createSqlQueryCadastrosEconomico(" select c.responsavelpelocadastro_id, c.migracaochave, ce.* ", filtroMensagemContribuinte);
        Query q = em.createNativeQuery(sql, CadastroEconomico.class);
        setParametersQueryCadastrosEcnomico(filtroMensagemContribuinte, q);
        if (maxResult > 0) {
            q.setFirstResult(firstResult);
            q.setMaxResults(maxResult);
        }
        return q.getResultList();
    }

    public List<MensagemContribuinteUsuario> buscarMensagemContribuinteUsuarios(MensagemContribuinte mensagemContribuinte,
                                                                                int firstResult, int maxResult) {
        List<MensagemContribuinteUsuario> usuarios = em.createNativeQuery(" select mcu.* from mensagemcontribusuario mcu " +
                " where mcu.mensagemcontribuinte_id = :idMensagemContribuinte ", MensagemContribuinteUsuario.class)
            .setParameter("idMensagemContribuinte", mensagemContribuinte.getId())
            .setFirstResult(firstResult)
            .setMaxResults(maxResult)
            .getResultList();
        if (usuarios != null) {
            for (MensagemContribuinteUsuario usuario : usuarios) {
                Hibernate.initialize(usuario.getDocumentos());
            }
        }
        return usuarios;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public MensagemContribuinte enviarMensagens(AssistenteBarraProgresso assistenteBarraProgresso,
                                                MensagemContribuinte mensagemContribuinte,
                                                FiltroMensagemContribuinte filtroMensagemContribuinte,
                                                List<CadastroEconomico> cadastrosSelecionados) {
        assistenteBarraProgresso.setDescricaoProcesso("Enviando mensagem ao(s) contribuinte(s).");
        mensagemContribuinte = em.merge(mensagemContribuinte);
        List<CadastroEconomico> cadastros = mensagemContribuinte.getEnviarTodosUsuarios() ?
            buscarCadastrosEconomico(filtroMensagemContribuinte, 0, 0) :
            cadastrosSelecionados;
        assistenteBarraProgresso.setTotal(cadastros.size());
        for (CadastroEconomico cadastroEconomico : cadastros) {
            MensagemContribuinteUsuario mensagemContribuinteUsuario = new MensagemContribuinteUsuario();
            mensagemContribuinteUsuario.setMensagemContribuinte(mensagemContribuinte);
            mensagemContribuinteUsuario.setCadastroEconomico(cadastroEconomico);
            em.persist(mensagemContribuinteUsuario);
            assistenteBarraProgresso.conta();
        }
        assistenteBarraProgresso.setDescricaoProcesso("Finalizando...");
        assistenteBarraProgresso.setTotal(0);
        return mensagemContribuinte;
    }
}
