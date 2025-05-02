package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoFaleConosco;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.seguranca.NotificacaoService;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class FaleConoscoWebFacade extends AbstractFacade<FaleConoscoWeb> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private RecursoSistemaFacade recursoSistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FaleConoscoWebFacade() {
        super(FaleConoscoWeb.class);
    }

    @Override
    public FaleConoscoWeb recuperar(Object id) {
        FaleConoscoWeb entity = super.recuperar(id);
        Hibernate.initialize(entity.getOcorrencias());
        Hibernate.initialize(entity.getRespostas());
        if (entity.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(entity.getDetentorArquivoComposicao().getArquivosComposicao());
            for (ArquivoComposicao ac : entity.getDetentorArquivoComposicao().getArquivosComposicao()) {
                Hibernate.initialize(ac.getArquivo().getPartes());
            }
        }
        entity.setHierarquiaAdministrativa(hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), entity.getUnidadeAdministrativa(), sistemaFacade.getDataOperacao()));
        entity.setHierarquiaOrcametaria(hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), entity.getUnidadeOrcamentaria(), sistemaFacade.getDataOperacao()));
        return entity;
    }

    public FaleConoscoWebOcorrencia recuperarOcorrenciaAnexos(FaleConoscoWebOcorrencia ocorrencia) {
        FaleConoscoWebOcorrencia entity = em.find(FaleConoscoWebOcorrencia.class, ocorrencia.getId());
        if (entity.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(entity.getDetentorArquivoComposicao().getArquivosComposicao());
            for (ArquivoComposicao ac : entity.getDetentorArquivoComposicao().getArquivosComposicao()) {
                Hibernate.initialize(ac.getArquivo().getPartes());
            }
        }
        return entity;
    }

    public FaleConoscoWebResposta recuperarRespostaAnexos(FaleConoscoWebResposta ocorrencia) {
        FaleConoscoWebResposta entity = em.find(FaleConoscoWebResposta.class, ocorrencia.getId());
        if (entity.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(entity.getDetentorArquivoComposicao().getArquivosComposicao());
            for (ArquivoComposicao ac : entity.getDetentorArquivoComposicao().getArquivosComposicao()) {
                Hibernate.initialize(ac.getArquivo().getPartes());
            }
        }
        return entity;
    }

    public FaleConoscoWeb recuperarComDependenciaRespostas(Object id) {
        FaleConoscoWeb entity = super.recuperar(id);
        Hibernate.initialize(entity.getRespostas());
        return entity;
    }

    public FaleConoscoWeb salvarNovoFaleConosco(FaleConoscoWeb entity) {
        criarPrimeiraResposta(entity);
        criarPrimeiraOcorrencia(entity);
        entity.atribuirTitulo();
        entity = em.merge(entity);
        return entity;
    }

    public void salvarOcorrencia(FaleConoscoWebOcorrencia ocorrencia, FaleConoscoWeb entity) {
        em.merge(ocorrencia);
        marcarPerguntaComoVisulizada(entity);
    }

    public FaleConoscoWeb salvarResposta(FaleConoscoWebResposta respostaAtual, FaleConoscoWeb entity, FaleConoscoWebResposta respostaOrigem, Boolean suporte) {
        if (suporte) {
            criarNotificacao(entity, respostaAtual, respostaOrigem);
        }
        if (entity.getVisualizadoEm() == null) {
            respostaOrigem.setVisualizadaEm(new Date());
            marcarPerguntaComoVisulizada(entity);
        }else {
            marcarRespostaComoVisulizada(respostaOrigem);
            em.merge(respostaAtual);
        }
        return entity;
    }

    public void salvarRespostaUsuario(FaleConoscoWebResposta respostaAtual, FaleConoscoWebResposta respostaOrigem) {
        marcarRespostaComoVisulizada(respostaOrigem);
        em.merge(respostaAtual);
    }

    private void criarPrimeiraOcorrencia(FaleConoscoWeb entity) {
        FaleConoscoWebOcorrencia ocorrencia = new FaleConoscoWebOcorrencia();
        ocorrencia.setUsuario(entity.getUsuario());
        ocorrencia.setDataOcorrencia(entity.getDataLancamento());
        ocorrencia.setSituacao(SituacaoFaleConosco.ENVIADA);
        ocorrencia.setFaleConoscoWeb(entity);
        ocorrencia.setConteudo(entity.getConteudo());
        if (entity.getDetentorArquivoComposicao() != null) {
            ocorrencia.setDetentorArquivoComposicao(entity.getDetentorArquivoComposicao());
        }
        entity.getOcorrencias().add(ocorrencia);
    }

    private void criarPrimeiraResposta(FaleConoscoWeb entity) {
        FaleConoscoWebResposta resposta = new FaleConoscoWebResposta();
        resposta.setUsuario(entity.getUsuario());
        resposta.setEnviadaEm(entity.getDataLancamento());
        resposta.setFaleConoscoWeb(entity);
        resposta.setConteudo(entity.getConteudo());
        if (entity.getDetentorArquivoComposicao() != null) {
            resposta.setDetentorArquivoComposicao(entity.getDetentorArquivoComposicao());
        }
        entity.getRespostas().add(resposta);
    }

    public void criarNotificacao(FaleConoscoWeb entity, FaleConoscoWebResposta resposta, FaleConoscoWebResposta respostaOrigem) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao(resposta.getConteudoReduzido());
        notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
        notificacao.setTitulo(TipoNotificacao.FALE_CONOSCO_WEB.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.FALE_CONOSCO_WEB);
        notificacao.setUsuarioSistema(respostaOrigem.getUsuario());
        notificacao.setLink("/fale-conosco-usuario/ver/" + entity.getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    public void marcarPerguntaComoVisulizada(FaleConoscoWeb entity) {
        if (entity.getVisualizadoEm() == null) {
            entity.setVisualizadoEm(new Date());
            em.merge(entity);
        }
    }

    public void marcarRespostaComoVisulizada(FaleConoscoWebResposta resposta) {
        resposta.setVisualizadaEm(new Date());
        em.merge(resposta);
    }

    public List<FaleConoscoWeb> buscarPorUsuario(UsuarioSistema usuarioSistema, String filtro) {
        String sql = "select * from faleconoscoweb " +
            "         where usuario_id = :idUsuario " +
            "         and (lower(titulo) like :filtro or lower(conteudo) like :filtro or lower(recurso) like :filtro) " +
            "         order by datalancamento desc ";
        Query q = em.createNativeQuery(sql, FaleConoscoWeb.class);
        q.setParameter("idUsuario", usuarioSistema.getId());
        q.setParameter("filtro", "%" + filtro.toLowerCase().trim() + "%");
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public Boolean hasMensagemRespondida(UsuarioSistema usuarioSistema) {
        String sql = " select resp.* from faleconoscowebresposta resp " +
            "           where resp.usuario_id = :idUsuario " +
            "           and resp.visualizadaem is not null " +
            "           and resp.enviadaem = (select max(fl.enviadaem) from faleconoscowebresposta fl where resp.usuario_id = fl.usuario_id) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idUsuario", usuarioSistema.getId());
        return !q.getResultList().isEmpty();
    }

    public RecursoSistema recuperarRecursoSistema(String caminho) {
        return recursoSistemaFacade.recuperarRecursoSistemaPorCaminho(caminho.trim());
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
