package br.com.webpublico.seguranca.service;

import br.com.webpublico.entidades.*;
import br.com.webpublico.seguranca.SingletonRecursosSistema;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Service
public class UsuarioSistemaService extends AbstractCadastroService<UsuarioSistema> implements UserDetailsService {

    @Autowired
    private SingletonRecursosSistema singletonRecursosSistema;


    public EntityManager getEntityManager() {
        return entityManager;
    }

    public List<String> getRecursosSistemaDiretosDoUsuario(UsuarioSistema us) {
        List<String> retorno = Lists.newArrayList();
        us = singletonRecursosSistema.getUsuarioPorCpf(us.getCpf());
        for (RecursosUsuario ru : us.getRecursosUsuario()) {
            retorno.add(ru.getRecursoSistema().getCaminho());
        }

        return retorno;
    }

    /**
     * Método utilizado para recuperar todos os recursos do sistema atribuidos pelo grupo de recursos
     *
     * @param us Usuário para o qual se deseja recuperar todos os recursos do sistema
     * @return Lista de urls para os quais determinado usuário possui acesso
     */
    public List<String> getRecursosSistemaDosGruposDeRecursoDoUsuario(UsuarioSistema us) {
        List<String> retorno = Lists.newArrayList();
        us = singletonRecursosSistema.getUsuarioPorCpf(us.getCpf());
        for (GrupoRecursosUsuario gr : us.getGrupoRecursosUsuario()) {
            for (RecursoSistema rec : gr.getGrupoRecurso().getRecursos()) {
                retorno.add(rec.getCaminho());
            }
        }
        return retorno;

    }

    /**
     * Método utilizado para recuperar todos os recursos do sistema atribuidos pelo grupo de usuários
     *
     * @param us Usuário para o qual se deseja recuperar todos os recursos do sistema
     * @return Lista de urls para os quais determinado usuário possui acesso
     */
    public List<String> getRecursosSistemaDosGruposDeUsuariosDoUsuario(UsuarioSistema us) {
        List<String> retorno = Lists.newArrayList();
        us = singletonRecursosSistema.getUsuarioPorCpf(us.getCpf());
        for (GrupoUsuario gr : us.getGrupos()) {
            for (ItemGrupoUsuario item : gr.getItens()) {
                for (RecursoSistema rec : item.getGrupoRecurso().getRecursos()) {
                    retorno.add(rec.getCaminho());
                }
            }
        }
        return retorno;
    }

    @Transactional()
    public UsuarioSistema findOneByCpf(String cpf) {
        UsuarioSistema usuarioPorLogin = null;
        String sql = " select us.* from UsuarioSistema us " +
            "inner join PessoaFisica pf on pf.id = us.pessoafisica_id WHERE pf.cpf = :cpf";
        Query q = entityManager.createNativeQuery(sql, UsuarioSistema.class);
        q.setParameter("cpf", StringUtil.retornaApenasNumeros(cpf));
        List<UsuarioSistema> retorno = q.getResultList();

        if (retorno != null && !retorno.isEmpty()) {
            usuarioPorLogin = retorno.get(0);
            if (retorno.size() > 1) {
                for (UsuarioSistema usuarioSistema : retorno) {
                    if (!usuarioSistema.getExpira()) {
                        usuarioPorLogin = usuarioSistema;
                    }
                }
            }

            Hibernate.initialize(usuarioPorLogin.getGrupos());
            for (GrupoUsuario grupoUsuario : usuarioPorLogin.getGrupos()) {
                Hibernate.initialize(grupoUsuario.getItens());
                for (ItemGrupoUsuario itemGrupoUsuario : grupoUsuario.getItens()) {
                    Hibernate.initialize(itemGrupoUsuario.getGrupoRecurso().getRecursos());
                }
                Hibernate.initialize(grupoUsuario.getPeriodos());
            }
            Hibernate.initialize(usuarioPorLogin.getRecursosUsuario());
            Hibernate.initialize(usuarioPorLogin.getGrupoRecursosUsuario());
            for (GrupoRecursosUsuario grupoRecursosUsuario : usuarioPorLogin.getGrupoRecursosUsuario()) {
                Hibernate.initialize(grupoRecursosUsuario.getGrupoRecurso().getRecursos());
            }
            Hibernate.initialize(usuarioPorLogin.getVigenciaTribUsuarios());
            Hibernate.initialize(usuarioPorLogin.getUsuarioUnidadeOrganizacional());
        }
        return usuarioPorLogin;

    }


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Query q = entityManager.createQuery("select us from UsuarioSistema us WHERE us.login = :login");
        q.setParameter("login", username);
        if(!q.getResultList().isEmpty()){
            UserDetails userDetails = (UserDetails) q.getResultList().get(0);
            return userDetails;
        }
        return null;
    }

    public List<UsuarioSistema> getUsuariosPorPerfilEspecialComInatividadeExcedida(Date dataOperacao) {
        String sql = "select u.* from usuariosistema u " +
            "where u.expira = 0 " +
            "and exists (select 1 from bloqueiodesbloqueiousuario bdu " +
            "    where to_date(:dataOperacao, 'dd/mm/yyyy') between bdu.iniciovigencia and coalesce(bdu.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            "    and bdu.tempomaximoinatividade is not null) " +
            "and u.ultimoacesso + coalesce((select bdu.tempomaximoinatividade from bloqueiodesbloqueiousuario bdu " +
            "    where to_date(:dataOperacao, 'dd/mm/yyyy') between bdu.iniciovigencia and coalesce(bdu.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) fetch first 1 rows only), 0) < to_date(:dataOperacao, 'dd/mm/yyyy') ";
        Query q = entityManager.createNativeQuery(sql, UsuarioSistema.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        List<UsuarioSistema> retorno = q.getResultList();
        return retorno;
    }
}
