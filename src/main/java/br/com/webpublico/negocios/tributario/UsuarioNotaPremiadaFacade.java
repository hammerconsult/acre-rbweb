package br.com.webpublico.negocios.tributario;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConsultaCepFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.nfse.domain.NotaPremiadaPermissao;
import br.com.webpublico.nfse.domain.UsuarioNotaPremiada;
import br.com.webpublico.nfse.domain.UsuarioNotaPremiadaPermissao;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;


@Stateless
public class UsuarioNotaPremiadaFacade extends AbstractFacade<UsuarioNotaPremiada> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ConsultaCepFacade consultaCepFacade;

    public UsuarioNotaPremiadaFacade() {
        super(UsuarioNotaPremiada.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ConsultaCepFacade getConsultaCepFacade() {
        return consultaCepFacade;
    }

    public UsuarioNotaPremiada buscarUsuarioPorLogin(String login) {
        Query q = em.createNativeQuery(" select * from usuarionotapremiada " +
            " where login = :login", UsuarioNotaPremiada.class);
        q.setParameter("login", login);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (UsuarioNotaPremiada) q.getResultList().get(0);
        }
        return null;
    }

    @Override
    public UsuarioNotaPremiada salvarRetornando(UsuarioNotaPremiada entity) {
        entity.validarCamposObrigatorios();
        validarDados(entity);
        entity.setPassword(encripitografarSenha(entity.getSenha()));
        if (entity.getId() == null && entity.getPermissoes().isEmpty()) {
            List<UsuarioNotaPremiadaPermissao> permissoes = Lists.newArrayList();
            permissoes.add(new UsuarioNotaPremiadaPermissao(entity, NotaPremiadaPermissao.ROLE_USER));
            entity.setPermissoes(permissoes);
        }
        return super.salvarRetornando(entity);
    }

    private void validarDados(UsuarioNotaPremiada entity) {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNotEmpty(entity.getLogin())) {
            entity.getLogin().replaceAll("\\s+", "");
            UsuarioNotaPremiada usuarioNotaPremiada = buscarUsuarioPorLogin(entity.getLogin());
            if (usuarioNotaPremiada != null && (entity.getId() == null || !entity.getId().equals(usuarioNotaPremiada.getId()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Usuário já cadastrado!");
            }
        }
        ve.lancarException();

    }

    public String encripitografarSenha(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    public List<UsuarioNotaPremiada> buscarParticipantes() {
        String sql = " select un.* " +
            "   from usuarionotapremiada un " +
            " where un.participandoprograma = 1 " +
            "   and un.ativo = 1 ";
        Query query = em.createNativeQuery(sql, UsuarioNotaPremiada.class);
        return query.getResultList();
    }
}
