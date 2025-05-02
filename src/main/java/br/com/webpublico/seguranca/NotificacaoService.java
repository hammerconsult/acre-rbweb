package br.com.webpublico.seguranca;

import br.com.webpublico.entidades.Notificacao;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.message.RabbitMQService;
import br.com.webpublico.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;

@Service
public class NotificacaoService implements Serializable {
    protected static final Logger logger = LoggerFactory.getLogger(NotificacaoService.class);

    @PersistenceContext
    protected transient EntityManager em;
    private final String NOTIFICAR_QUEUE_NAME = "publicarNotificacao";
    private final String LER_QUEUE_NAME = "lerNotificacao";
    private final String RECARREGAR_QUEUE_NAME = "recarregarNotificacao";
    private final String notificarQueue = Strings.isNullOrEmpty(System.getenv(NOTIFICAR_QUEUE_NAME)) ? NOTIFICAR_QUEUE_NAME : System.getenv(NOTIFICAR_QUEUE_NAME);
    private final String lerQueue = Strings.isNullOrEmpty(System.getenv(LER_QUEUE_NAME)) ? LER_QUEUE_NAME : System.getenv(LER_QUEUE_NAME);
    private final String recarregarQueue = Strings.isNullOrEmpty(System.getenv(RECARREGAR_QUEUE_NAME)) ? RECARREGAR_QUEUE_NAME : System.getenv(RECARREGAR_QUEUE_NAME);

    public static NotificacaoService getService() {
        return (NotificacaoService) Util.getSpringBeanPeloNome("notificacaoService");
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Future<List<Notificacao>> marcarNotificaoComoLidaPorIdDoLink(Long idLink) {
        String sql = "SELECT n.* from notificacao n WHERE  n.link like :link and VISUALIZADO = 0";
        Query q = em.createNativeQuery(sql, Notificacao.class);
        q.setParameter("link", "%" + idLink + "%");
        List<Notificacao> resultList = q.getResultList();
        for (Notificacao notificacao : resultList) {
            marcarComoLida(notificacao);
        }
        return new AsyncResult(Lists.newArrayList());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void removerNotificacaoPorIdLink(Long idLink) {
        String sql = "SELECT n.* from notificacao n WHERE  n.link like :link";
        Query q = em.createNativeQuery(sql, Notificacao.class);
        q.setParameter("link", "%" + idLink + "%");
        List<Notificacao> resultList = q.getResultList();
        for (Notificacao notificacao : resultList) {
            marcarComoLida(notificacao);
            em.remove(notificacao);
        }
    }


    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void marcarComoLida(TipoNotificacao tipo) {
        String sql = "SELECT n.* from notificacao n WHERE  n.TIPONOTIFICACAO = :tipo";
        Query q = em.createNativeQuery(sql, Notificacao.class);
        q.setParameter("tipo", tipo.name());
        List<Notificacao> resultList = q.getResultList();
        for (Notificacao notificacao : resultList) {
            marcarComoLida(notificacao);
        }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void marcarComoLida(List<Notificacao> notificacoes) {
        for (Notificacao notificacao : notificacoes) {
            marcarComoLida(notificacao);
        }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void marcarComoLida(Notificacao notificacao) {
        try {
            RabbitMQService.getService().basicPublish(lerQueue, new ObjectMapper().writeValueAsBytes(new NotificacaoDTO(notificacao)));
        } catch (Exception e) {
            Util.loggingError(this.getClass(), "Não foi possível notificar", e);
        }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notificar(List<Notificacao> notificacoes) {
        for (Notificacao notificacao : notificacoes) {
            notificar(notificacao);
        }
    }


    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notificar(Notificacao notificacao) {
        try {
            RabbitMQService.getService().basicPublish(notificarQueue, new ObjectMapper().writeValueAsBytes(new NotificacaoDTO(notificacao)));
        } catch (Exception e) {
            Util.loggingError(this.getClass(), "Não foi possível notificar", e);
        }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notificar(String titulo, String descricao, String link, Notificacao.Gravidade gravidade, TipoNotificacao tipoNotificacao) {

        notificar(new Notificacao(titulo, descricao, link, gravidade, tipoNotificacao));
    }

    public List<UsuarioSistema> buscarUsuarioSistemaPorTipoDeNotificacao(TipoNotificacao tipoNotificacao) {
        String sql = "SELECT DISTINCT usuario.*" +
            " FROM USUARIOSISTEMA USUARIO" +
            " INNER JOIN GRUPOUSUARIOSISTEMA grupousuario ON grupousuario.USUARIOSISTEMA_ID = usuario.id" +
            " INNER JOIN GRUPOUSUARIO GRUPO ON GRUPO.ID = GRUPOUSUARIO.GRUPOUSUARIO_ID" +
            " INNER JOIN GRUPOUSUARIONOTIFICACAO GRUPONOTIFICACAO ON GRUPONOTIFICACAO.GRUPOUSUARIO_ID = GRUPO.ID" +
            "   WHERE  gruponotificacao.TIPONOTIFICACAO  = :tipoNotificacao";
        Query q = em.createNativeQuery(sql, UsuarioSistema.class);
        q.setParameter("tipoNotificacao", tipoNotificacao.name());
        try {
            return q.getResultList();
        } catch (NoResultException no) {
            return Lists.newArrayList();
        }
    }

    public List<Notificacao> buscarNotificacoesPorLinkAndTipoNaoVisualizado(String link, TipoNotificacao tiponotificacao) {
        String sql = "SELECT n.* from notificacao n WHERE n.visualizado = 0 and n.link like :link and n.tiponotificacao = :tiponotificacao ";
        Query q = em.createNativeQuery(sql, Notificacao.class);
        q.setParameter("link", link + "%");
        q.setParameter("tiponotificacao", tiponotificacao.name());
        return q.getResultList();
    }

    public void recarregarTodasNotificacoes() {
        try {
            RabbitMQService.getService().basicPublish(recarregarQueue, new ObjectMapper().writeValueAsBytes(new NotificacaoDTO()));
        } catch (IOException e) {
            Util.loggingError(this.getClass(), "Não foi possível recarregar as notificoes", e);
        }
    }

    public static class NotificacaoDTO implements Serializable {
        Long id;
        String titulo;
        String descricao;
        String url;
        Notificacao.Gravidade gravidade;
        TipoNotificacao tipoNotificacao;
        Long usuarioId;
        Long unidadeId;

        public NotificacaoDTO() {
        }


        public NotificacaoDTO(Notificacao not) {
            this();
            this.id = not.getId();
            this.titulo = not.getTitulo();
            this.descricao = not.getDescricao();
            this.url = not.getLink();
            this.gravidade = not.getGravidade();
            this.tipoNotificacao = not.getTipoNotificacao();
            if (not.getUsuarioSistema() != null)
                this.usuarioId = not.getUsuarioSistema().getId();
            if (not.getUnidadeOrganizacional() != null)
                this.unidadeId = not.getUnidadeOrganizacional().getId();
        }


        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Notificacao.Gravidade getGravidade() {
            return gravidade;
        }

        public void setGravidade(Notificacao.Gravidade gravidade) {
            this.gravidade = gravidade;
        }

        public TipoNotificacao getTipoNotificacao() {
            return tipoNotificacao;
        }

        public void setTipoNotificacao(TipoNotificacao tipoNotificacao) {
            this.tipoNotificacao = tipoNotificacao;
        }

        public Long getUsuarioId() {
            return usuarioId;
        }

        public void setUsuarioId(Long usuarioId) {
            this.usuarioId = usuarioId;
        }

        public Long getUnidadeId() {
            return unidadeId;
        }

        public void setUnidadeId(Long unidadeId) {
            this.unidadeId = unidadeId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NotificacaoDTO that = (NotificacaoDTO) o;
            return Objects.equals(id, that.id) &&
                Objects.equals(titulo, that.titulo) &&
                Objects.equals(descricao, that.descricao) &&
                Objects.equals(url, that.url) &&
                gravidade == that.gravidade &&
                tipoNotificacao == that.tipoNotificacao &&
                Objects.equals(usuarioId, that.usuarioId) &&
                Objects.equals(unidadeId, that.unidadeId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, titulo, descricao, url, gravidade, tipoNotificacao, usuarioId, unidadeId);
        }
    }

}
