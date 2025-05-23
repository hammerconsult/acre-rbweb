package br.com.webpublico.seguranca;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModuloSistema;
import br.com.webpublico.seguranca.service.GrupoRecursoService;
import br.com.webpublico.seguranca.service.RecursoSistemaService;
import br.com.webpublico.seguranca.service.UsuarioSistemaService;
import br.com.webpublico.util.Util;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.*;

@Service
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SingletonRecursosSistema implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(SingletonRecursosSistema.class);
    @PersistenceContext
    protected transient EntityManager entityManager;
    @Autowired
    private UsuarioSistemaService usuarioSistemaService;
    @Autowired
    private GrupoRecursoService grupoRecursoService;
    @Autowired
    private RecursoSistemaService recursoSistemaService;
    @Autowired
    private SingletonMetricas singletonMetricas;
    private List<RecursoSistema> recursos;
    private List<GrupoRecurso> grupos;
    private Map<GrupoRecurso, List<RecursoSistema>> recursosDoGrupo;
    private List<Fase> fases;
    private Map<String, UsuarioSistema> usuarios;
    private List<Chamado> chamados;
    private Map<UsuarioSistema, List<ConfiguracaoChamadoUsuario>> configuracaoChamados;
    private Map<UsuarioSistema, Set<PermissaoAcessoRecurso>> recursosDoUsuario;
    private Map<UsuarioSistema, LocalTime> ultimoAcessoUsuario;
    private Map<UsuarioSistema, String> ultimaPaginaUsuario;
    private Map<String, LinkedHashMap> mapaVersaoSistema = Maps.newHashMap();


    @PostConstruct
    public void initialize() {
        usuarios = Maps.newHashMap();
        recursosDoGrupo = Maps.newHashMap();
        configuracaoChamados = Maps.newHashMap();
        recursosDoUsuario = Maps.newHashMap();
        ultimoAcessoUsuario = Maps.newHashMap();
        ultimaPaginaUsuario = Maps.newHashMap();
    }

    public List<RecursoSistema> getRecursos() {
        if (recursos == null || recursos.isEmpty()) {
            recursos = recursoSistemaService.listaRecursos();
        }
        return recursos;
    }

    private List<PermissaoAcessoRecurso> getPermissoesDoUsuario(UsuarioSistema usuarioSistema) {
        if (recursosDoUsuario.get(usuarioSistema) == null) {
            recursosDoUsuario.put(usuarioSistema, Sets.<PermissaoAcessoRecurso>newHashSet());
            adicionarRecursosDoGrupoDeUsuario(usuarioSistema);
            adicionarGruposDeRecursosEspeciaisDoUsuario(usuarioSistema);
            adicionarRecursosEspeciaisDoUsuario(usuarioSistema);


        }
        return Lists.newArrayList(recursosDoUsuario.get(usuarioSistema));
    }

    private void adicionarRecursosEspeciaisDoUsuario(UsuarioSistema usuarioSistema) {
        for (RecursosUsuario ru : usuarioSistema.getRecursosUsuario()) {
            PermissaoAcessoRecurso permissao = PermissaoAcessoRecurso.recursoUsuarioToPermissao(ru);
            removerPermissaoAnteriorAdicionarNova(usuarioSistema, permissao);
        }
    }


    private void adicionarGruposDeRecursosEspeciaisDoUsuario(UsuarioSistema usuarioSistema) {
        for (GrupoRecursosUsuario grupoRec : usuarioSistema.getGrupoRecursosUsuario()) {
            for (RecursoSistema rec : grupoRec.getGrupoRecurso().getRecursos()) {
                PermissaoAcessoRecurso permissao = PermissaoAcessoRecurso.grupoRecursoToPermissao(rec, grupoRec);
                removerPermissaoAnteriorAdicionarNova(usuarioSistema, permissao);
            }
        }
    }

    private void adicionarRecursosDoGrupoDeUsuario(UsuarioSistema usuarioSistema) {
        for (GrupoUsuario grupoUsuario : usuarioSistema.getGrupos()) {
            for (ItemGrupoUsuario itemGrupoUsuario : grupoUsuario.getItens()) {
                if (!grupoUsuario.isPermitidoAcesso(itemGrupoUsuario.getGrupoRecurso())) {
                    continue;
                }
                for (RecursoSistema rec : itemGrupoUsuario.getGrupoRecurso().getRecursos()) {
                    recursosDoUsuario.get(usuarioSistema).add(PermissaoAcessoRecurso.grupoUsuarioToPermissao(rec, itemGrupoUsuario));
                }
            }
        }
    }

    private void removerPermissaoAnteriorAdicionarNova(UsuarioSistema usuarioSistema, PermissaoAcessoRecurso permissao) {
        if (recursosDoUsuario.get(usuarioSistema).contains(permissao)) {
            recursosDoUsuario.get(usuarioSistema).remove(permissao);
        }
        recursosDoUsuario.get(usuarioSistema).add(permissao);
    }


    public List<Chamado> getChamados() {
        if (chamados == null || chamados.isEmpty()) {
            carregarChamados();
        }
        return chamados;
    }

    public List<ConfiguracaoChamadoUsuario> getConfiguracaoChamado(UsuarioSistema usuarioCorrente) {
        if (configuracaoChamados.containsKey(usuarioCorrente)) {
            return configuracaoChamados.get(usuarioCorrente);
        } else {
            List<ConfiguracaoChamadoUsuario> configs = carregarConfiguracoesChamados(usuarioCorrente);
            configuracaoChamados.put(usuarioCorrente, configs);
            return configs;
        }
    }

    public List<GrupoRecurso> getGrupos() {
        if (grupos == null || grupos.isEmpty()) {
            grupos = grupoRecursoService.listaGrupo();
        }
        return grupos;
    }

    public Map<GrupoRecurso, List<RecursoSistema>> getRecursosDoGrupo() {
        return recursosDoGrupo;
    }

    public void limparFases() {
        fases = null;
        initialize();
    }

    public List<Fase> getFases() {
        if (fases == null || fases.isEmpty()) {
            fases = recursoSistemaService.listaFases();
        }
        return fases;
    }


    public RecursoSistema getRecursoPorUri(String uri) {
        for (RecursoSistema rec : getRecursos()) {
            if (rec.comparaUri(uri)) {
                return rec;
            }
        }
        return null;
    }

    public UsuarioSistema getUsuarioPorCpf(String cpf) {
        if (usuarios.get(cpf) == null) {
            UsuarioSistema usu = usuarioSistemaService.findOneByCpf(cpf);
            usuarios.put(cpf, usu);
        }
        return usuarios.get(cpf);
    }

    public Map<String, UsuarioSistema> getUsuarios() {
        return usuarios;
    }

    private void carregarChamados() {
        String sql = "select distinct c from Chamado c " +
            "left join fetch c.respostas resp";
        Query consulta = entityManager.createQuery(sql, Chamado.class);
        chamados = (List<Chamado>) consulta.getResultList();
    }

    public void limparChamados(ModuloSistema modulo, NivelChamado nivel) {
        //Só limpar os chamados dos usuários que tem o modulo e nivel iguais
        chamados.clear();
    }

    public RecursoSistemaService getRecursoSistemaService() {
        return recursoSistemaService;
    }

    public SingletonMetricas getSingletonMetricas() {
        return singletonMetricas;
    }

    List<ConfiguracaoChamadoUsuario> carregarConfiguracoesChamados(UsuarioSistema usuarioSistema) {
        try {
            String sql = "SELECT configuser.* FROM configuracaochamado config " +
                "INNER JOIN configuracaochamadousuario configuser ON config.id = configuser.configucao_id " +
                "INNER JOIN usuariosistema u ON configuser.usuario_id = u.id AND u.id = :idUsuario ";
            Query consulta = entityManager.createNativeQuery(sql, ConfiguracaoChamadoUsuario.class);
            consulta.setParameter("idUsuario", usuarioSistema.getId());
            return consulta.getResultList();
        } catch (Exception e) {
            logger.error("Erro no método carregarConfiguracoesChamados", e);
        }
        return new ArrayList<>();
    }


    void guardarUltimoAcesso(UsuarioSistema usuarioSistema, LocalTime now, String url) {

        ultimoAcessoUsuario.put(usuarioSistema, now);
        ultimaPaginaUsuario.put(usuarioSistema, url);
    }

    public Map<UsuarioSistema, LocalTime> getUltimoAcessoUsuario() {
        return ultimoAcessoUsuario;
    }

    public Map<UsuarioSistema, String> getUltimaPaginaUsuario() {
        return ultimaPaginaUsuario;
    }

    public void limparUsuario(UsuarioSistema usuario) {
        try {
            limparUsuario(usuario.getPessoaFisica().getCpfSemFormatacao());
        } catch (Exception e) {
            logger.error("Não foi possível limpar o usuario " + usuario);
        }
    }

    public void limparUsuario(String cpf) {
        try {
            UsuarioSistema usuario = getUsuarios().remove(cpf);
            recursosDoUsuario.remove(usuario);
        } catch (Exception e) {
            logger.error("Não foi possível limpar o usuario " + cpf);
        }
    }


    Boolean hasRecursoParaUsuario(RecursoSistema recursoSistema, UsuarioSistema usuarioSistema) {
        PermissaoAcessoRecurso permissao = PermissaoAcessoRecurso.recursoToPermissao(recursoSistema);
        List<PermissaoAcessoRecurso> permissoes = getPermissoesDoUsuario(usuarioSistema);
        return permissoes.contains(permissao) && permissoes.get(permissoes.indexOf(permissao)).leitura;
    }

    Boolean hasPermissaoEditar(RecursoSistema recursoSistema, UsuarioSistema usuarioSistema) {
        PermissaoAcessoRecurso permissao = PermissaoAcessoRecurso.recursoToPermissao(recursoSistema);
        List<PermissaoAcessoRecurso> permissoes = getPermissoesDoUsuario(usuarioSistema);
        return permissoes.contains(permissao) && permissoes.get(permissoes.indexOf(permissao)).escrita;
    }

    Boolean hasPermissaoExcluir(RecursoSistema recursoSistema, UsuarioSistema usuarioSistema) {
        PermissaoAcessoRecurso permissao = PermissaoAcessoRecurso.recursoToPermissao(recursoSistema);
        List<PermissaoAcessoRecurso> permissoes = getPermissoesDoUsuario(usuarioSistema);
        return permissoes.contains(permissao) && permissoes.get(permissoes.indexOf(permissao)).exlcusao;
    }

    public Set<RecursoSistema> getTodosRecursosDoUsuario(UsuarioSistema usuarioSistema) {
        Set<RecursoSistema> recursos = Sets.newHashSet();
        for (PermissaoAcessoRecurso permissaoAcessoRecurso : getPermissoesDoUsuario(usuarioSistema)) {
            if (permissaoAcessoRecurso.leitura) {
                recursos.add(permissaoAcessoRecurso.recursoSistema);
            }
        }
        return recursos;
    }

    public static class PermissaoAcessoRecurso {
        protected RecursoSistema recursoSistema;
        Boolean leitura, escrita, exlcusao;

        PermissaoAcessoRecurso(RecursoSistema recursoSistema, Boolean leitura, Boolean escrita, Boolean exlcusao) {
            this.recursoSistema = recursoSistema;
            this.leitura = leitura;
            this.escrita = escrita;
            this.exlcusao = exlcusao;
        }

        static PermissaoAcessoRecurso recursoToPermissao(RecursoSistema recursoSistema) {
            return new PermissaoAcessoRecurso(recursoSistema, false, false, false);
        }

        static PermissaoAcessoRecurso grupoRecursoToPermissao(RecursoSistema recursoSistema, GrupoRecursosUsuario grupoRec) {
            return new PermissaoAcessoRecurso(recursoSistema, grupoRec.getLeitura(), grupoRec.getEscrita(), grupoRec.getExclusao());
        }

        static PermissaoAcessoRecurso grupoUsuarioToPermissao(RecursoSistema recursoSistema, ItemGrupoUsuario itemGrupoUsuario) {
            return new PermissaoAcessoRecurso(recursoSistema, itemGrupoUsuario.getLeitura(), itemGrupoUsuario.getEscrita(), itemGrupoUsuario.getExclusao());
        }

        static PermissaoAcessoRecurso recursoUsuarioToPermissao(RecursosUsuario ru) {
            return new PermissaoAcessoRecurso(ru.getRecursoSistema(), ru.getLeitura(), ru.getEscrita(), ru.getExclusao());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof PermissaoAcessoRecurso) {
                PermissaoAcessoRecurso other = (PermissaoAcessoRecurso) obj;
                if (this.recursoSistema != null) {
                    return Objects.equal(this.recursoSistema.getCaminho(), other.recursoSistema.getCaminho());
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            return recursoSistema != null ? recursoSistema.hashCode() : 0;
        }
    }

    public LinkedHashMap getVersaoNoChamados(String versao) {
        if (mapaVersaoSistema.get(versao) == null) {
            LinkedHashMap forObject = Util.getRestTemplate().getForObject("https://chamados.webpublico.com.br/api/release-by-param?versao=" + versao + "&sistemaId=195750", LinkedHashMap.class);
            mapaVersaoSistema.put(versao, forObject);
        }
        return mapaVersaoSistema.get(versao);
    }
}
