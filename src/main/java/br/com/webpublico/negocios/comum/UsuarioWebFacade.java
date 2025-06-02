package br.com.webpublico.negocios.comum;

import br.com.webpublico.dte.dto.RegisterDteDTO;
import br.com.webpublico.dte.facades.ConfiguracaoDteFacade;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.entidadesauxiliares.comum.UsuarioPortalWebDTO;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.exception.UsuarioWebEmailNaoExistenteException;
import br.com.webpublico.exception.UsuarioWebProblemasCadastroException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.nfse.domain.*;
import br.com.webpublico.nfse.domain.dtos.*;
import br.com.webpublico.nfse.domain.dtos.enums.PermissaoUsuarioEmpresaNfse;
import br.com.webpublico.nfse.domain.template.*;
import br.com.webpublico.nfse.enums.TipoTemplateNfse;
import br.com.webpublico.nfse.exceptions.NfseOperacaoNaoPermitidaException;
import br.com.webpublico.nfse.facades.*;
import br.com.webpublico.nfse.util.RandomUtil;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.solicitacaodispositivo.AuthoritiesConstants;
import br.com.webpublico.util.*;
import br.com.webpublico.ws.model.WSPessoa;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Stateless
public class UsuarioWebFacade extends AbstractFacade<UsuarioWeb> {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioWebFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @EJB
    private NfseAuthorityFacade authorityService;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private ConfiguracaoNfseFacade configuracaoNfseFacade;
    @EJB
    private ConviteUsuarioNfseFacade conviteUsuarioNfseFacade;
    @EJB
    private TemplateNfseFacade templateNfseFacade;
    @EJB
    private UserNfseCadastroEconomicoFacade userNfseCadastroEconomicoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoDteFacade configuracaoDteFacade;
    @EJB
    private NfseAuthorityFacade nfseAuthorityFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;

    @Override
    public UsuarioWeb recuperar(Object id) {
        UsuarioWeb recuperar = super.recuperar(id);
        Hibernate.initialize(recuperar.getUserNfseCadastroEconomicos());
        Hibernate.initialize(recuperar.getUserNfseHistoricosAtivacao());
        Hibernate.initialize(recuperar.getUserDteCadastrosEconomicos());
        Hibernate.initialize(recuperar.getTermosAdesaoDte());
        Hibernate.initialize(recuperar.getBloqueiosEmissaoNfse());
        return recuperar;
    }

    public UsuarioWebFacade() {
        super(UsuarioWeb.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public UsuarioWeb buscarNfseUserPorEmail(String email) {
        Query q = em.createNativeQuery(" select * from UsuarioWeb where EMAIL = :email", UsuarioWeb.class);
        q.setParameter("email", email);
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (UsuarioWeb) resultList.get(0);
        }
        return null;
    }

    public UsuarioWeb buscarUsuarioWebPorLogin(String login) {
        Query q = em.createNativeQuery(" select * from UsuarioWeb " +
            " where regexp_replace(login, '[^0-9]') = regexp_replace(:login, '[^0-9]') ", UsuarioWeb.class);
        q.setParameter("login", login);
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            UsuarioWeb nfseUser = (UsuarioWeb) resultList.get(0);
            Hibernate.initialize(nfseUser.getUserNfseCadastroEconomicos());
            return nfseUser;
        }
        return null;
    }

    public UsuarioWeb buscarNfseUserPorLoginSemPessoa(String login) {
        Query q = em.createNativeQuery(" select * from UsuarioWeb where LOGIN = :login and pessoa_id is null ", UsuarioWeb.class);
        q.setParameter("login", login);
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            UsuarioWeb nfseUser = (UsuarioWeb) resultList.get(0);
            Hibernate.initialize(nfseUser.getUserNfseCadastroEconomicos());
            return nfseUser;
        }
        return null;
    }

    public UsuarioWeb buscarNfseUserPorKey(String key) {
        Query q = em.createNativeQuery(" select * from UsuarioWeb where activation_key = :key", UsuarioWeb.class);
        q.setParameter("key", key);
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (UsuarioWeb) resultList.get(0);
        }
        return null;
    }

    public UsuarioWeb buscarNfseUserPorResetKey(String key) {
        Query q = em.createNativeQuery(" select * from UsuarioWeb where reset_key = :key", UsuarioWeb.class);
        q.setParameter("key", key);
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (UsuarioWeb) resultList.get(0);
        }
        return null;
    }

    public UsuarioWeb recuperarUsuarioPorLogin(String login) {

        Query q = em.createQuery("select user from UsuarioWeb user where lower(user.login) = :login");
        q.setParameter("login", StringUtil.removeCaracteresEspeciaisSemEspaco(login).trim().toLowerCase());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            UsuarioWeb user = (UsuarioWeb) q.getSingleResult();
            if (user == null) {
                throw new NfseOperacaoNaoPermitidaException("Usuário não encontrado");
            }
            user.atribuirEmpresa();
            if (user.getUserNfseCadastroEconomico() != null) {
                user.getUserNfseCadastroEconomico().setCadastroEconomico(cadastroEconomicoFacade.recuperar(user.getUserNfseCadastroEconomico().getCadastroEconomico().getId()));
            }
            return user;
        }
        return null;

    }

    public UsuarioWeb efetuarLogin(String login) {
        UsuarioWeb user = recuperarUsuarioPorLogin(login);
        if (user == null) {
            throw new NfseOperacaoNaoPermitidaException("Usuário não encontrado");
        }
        return user;
    }

    public void atribuirEmpresa(UsuarioWeb user) {
        user.setUltimoAcesso(new Date());
        user.atribuirEmpresa();
        salvarRetornando(user);
    }

    public void tratarPermissoesFiscalTributario(UsuarioWeb user) {
        user = recuperar(user.getId());
        if (user.getPessoa() instanceof PessoaFisica) {
            UsuarioSistema usuarioSistema = usuarioSistemaFacade.usuarioSistemaDaPessoa(user.getPessoa());
            if (usuarioSistema != null && usuarioSistema.isFiscalTributario()) {
                if (!user.isAdmin())
                    user.getNfseUserAuthorities().add(new NfseUserAuthority(user, authorityService.buscarPorTipo(AuthoritiesConstants.ADMIN)));
            } else {
                if (user.isAdmin()) {
                    user.setUserNfseCadastroEconomico(null);
                    user.getNfseUserAuthorities().remove(user.getAdminAuthotity());
                }
            }
            salvarRetornando(user);
        }
    }

    public void tratarPermissoesContador(UsuarioWeb user) throws Exception {
        user = recuperar(user.getId());
        List<CadastroEconomico> cadastrosEconomicosContador = cadastroEconomicoFacade.buscarCadastroEconomicoContador(user.getLogin());
        if (cadastrosEconomicosContador == null) {
            cadastrosEconomicosContador = Lists.newArrayList();
        }
        adicionarAcessoAutomatico(user, UserNfseCadastroEconomico.AdicionadoPor.VIA_SISTEMA_CONTADOR, cadastrosEconomicosContador);
        removerAcessoAutomatico(user, UserNfseCadastroEconomico.AdicionadoPor.VIA_SISTEMA_CONTADOR, cadastrosEconomicosContador);
    }

    public void tratarPermissoesSocioAdministrador(UsuarioWeb user) throws Exception {
        user = recuperar(user.getId());
        List<CadastroEconomico> cadastrosEconomicosSocioAdministrador
            = cadastroEconomicoFacade.buscarCadastroEconomicoSocioAdministrador(user.getLogin());
        if (cadastrosEconomicosSocioAdministrador == null) {
            cadastrosEconomicosSocioAdministrador = Lists.newArrayList();
        }
        adicionarAcessoAutomatico(user, UserNfseCadastroEconomico.AdicionadoPor.VIA_SISTEMA_SOCIO_ADMINISTRADOR, cadastrosEconomicosSocioAdministrador);
        removerAcessoAutomatico(user, UserNfseCadastroEconomico.AdicionadoPor.VIA_SISTEMA_SOCIO_ADMINISTRADOR, cadastrosEconomicosSocioAdministrador);
    }

    private void removerAcessoAutomatico(UsuarioWeb user, UserNfseCadastroEconomico.AdicionadoPor adicionadoPor, List<CadastroEconomico> cadastrosPermitidos) {
        List<UserNfseCadastroEconomico> removerUserCe = Lists.newArrayList();
        if (user.getUserNfseCadastroEconomicos() != null && !user.getUserNfseCadastroEconomicos().isEmpty()) {
            for (UserNfseCadastroEconomico userNfseCadastroEconomico : user.getUserNfseCadastroEconomicos()) {
                if (adicionadoPor.equals(userNfseCadastroEconomico.getAdicionadoPor())) {
                    if (!cadastrosPermitidos.contains(userNfseCadastroEconomico.getCadastroEconomico())) {
                        removerUserCe.add(userNfseCadastroEconomico);
                        List<UserNfseCadastroEconomico> filhos = userNfseCadastroEconomicoFacade.buscarUserNfseCadastroEconomicoPorUserResponsavel(user);
                        if (filhos != null && !filhos.isEmpty()) {
                            removerUserCe.addAll(filhos);
                        }
                    }
                }
            }
        }
        if (removerUserCe != null && !removerUserCe.isEmpty()) {
            for (UserNfseCadastroEconomico userNfseCadastroEconomico : removerUserCe) {
                UsuarioWeb usuarioNfse = userNfseCadastroEconomico.getUsuarioNfse();
                usuarioNfse.setUserNfseCadastroEconomico(null);
                usuarioNfse.getUserNfseCadastroEconomicos().remove(userNfseCadastroEconomico);
                salvarRetornando(usuarioNfse);
            }
        }

    }

    private void adicionarAcessoAutomatico(UsuarioWeb user, UserNfseCadastroEconomico.AdicionadoPor adicionadoPor, List<CadastroEconomico> cadastrosPermitidos) {
        if (cadastrosPermitidos != null) {
            for (CadastroEconomico cadastroEconomico : cadastrosPermitidos) {
                UserNfseCadastroEconomico userNfseCadastroEconomico = null;
                if (user.getUserNfseCadastroEconomicos() != null && !user.getUserNfseCadastroEconomicos().isEmpty()) {
                    List<UserNfseCadastroEconomico> collect = Lists.newArrayList();
                    for (UserNfseCadastroEconomico userNfseCE : user.getUserNfseCadastroEconomicos()) {
                        if (userNfseCE.getCadastroEconomico().equals(cadastroEconomico)) {
                            collect.add(userNfseCE);
                        }
                    }
                    if (!collect.isEmpty()) {
                        userNfseCadastroEconomico = collect.get(0);
                    }
                }
                if (userNfseCadastroEconomico == null) {
                    userNfseCadastroEconomico = new UserNfseCadastroEconomico();
                    userNfseCadastroEconomico.setUsuarioNfse(user);
                    userNfseCadastroEconomico.setCadastroEconomico(cadastroEconomico);
                }
                userNfseCadastroEconomico.setContador(adicionadoPor.equals(UserNfseCadastroEconomico.AdicionadoPor.VIA_SISTEMA_CONTADOR));
                userNfseCadastroEconomico.setAdicionadoPor(adicionadoPor);
                userNfseCadastroEconomico.adicionarTodasPermissoes();
                update(userNfseCadastroEconomico);
            }
        }
    }

    public List<UsuarioWeb> verificarUsuarioExistentePorEmail(UsuarioWeb usuario) {
        StringBuilder sqlEmail = new StringBuilder();
        sqlEmail.append("SELECT * ");
        sqlEmail.append(" FROM USUARIOWEB ");
        sqlEmail.append("WHERE EMAIL = :email ");
        if (usuario.getId() != null) {
            sqlEmail.append("AND ID <> :id");
        }
        Query q = em.createNativeQuery(sqlEmail.toString(), UsuarioWeb.class);
        q.setParameter("email", usuario.getEmail());
        if (usuario.getId() != null) {
            q.setParameter("id", usuario.getId());
        }
        return q.getResultList();
    }

    public List<UsuarioWeb> verificarUsuarioExistentePorLogin(UsuarioWeb usuario) {
        StringBuilder sqlUsuario = new StringBuilder();
        sqlUsuario.append("SELECT * ");
        sqlUsuario.append(" FROM USUARIOWEB ");
        sqlUsuario.append("WHERE LOGIN = :login ");
        if (usuario.getId() != null) {
            sqlUsuario.append("AND ID <> :id");
        }
        Query q = em.createNativeQuery(sqlUsuario.toString(), UsuarioWeb.class);
        q.setParameter("login", usuario.getLogin());
        if (usuario.getId() != null) {
            q.setParameter("id", usuario.getId());
        }
        return q.getResultList();
    }

    public Pessoa buscarPessoaPorLoginUsuario(String login) {
        Query q = em.createQuery(" select user.pessoa from UsuarioWeb  user where lower(user.login) = lower(:login)");
        q.setParameter("login", StringUtil.removeCaracteresEspeciaisSemEspaco(login).trim());
        if (!q.getResultList().isEmpty()) {
            Pessoa p = (Pessoa) q.getResultList().get(0);
            Hibernate.initialize(p.getEnderecos());
            Hibernate.initialize(p.getTelefones());
            if (p instanceof PessoaFisica)
                Hibernate.initialize(((PessoaFisica) p).getDocumentosPessoais());
            return p;
        }
        return null;
    }

    public UsuarioWeb salvarRetornando(UsuarioWeb user) {
        removerRegistroMongodbNfse(user);
        return em.merge(user);
    }


    public UsuarioWeb registrarUsuario(RegisterNfseDTO registerDTO) throws NfseOperacaoNaoPermitidaException, ValidacaoException {
        UsuarioWeb usuarioWeb = new UsuarioWeb();
        usuarioWeb.setLogin(registerDTO.getDadosPessoais().getCpfCnpj());
        usuarioWeb.setPassword(registerDTO.getPassword());
        usuarioWeb.setEmail(registerDTO.getEmail());
        usuarioWeb.setPessoa(pessoaFacade.buscarPessoaAtivasPorCpfOrCnpj(registerDTO.getDadosPessoais().getCpfCnpj()));

        usuarioWeb.setActivated(true);
        usuarioWeb.setActivationKey(RandomUtil.generateActivationKey());

        List<NfseUserAuthority> authorities = Lists.newArrayList();
        authorities.add(new NfseUserAuthority(usuarioWeb, authorityService.buscarPorTipo(AuthoritiesConstants.USER)));
        authorities.add(new NfseUserAuthority(usuarioWeb, authorityService.buscarPorTipo(AuthoritiesConstants.CONTRIBUINTE)));
        usuarioWeb.setNfseUserAuthorities(authorities);

        Pessoa pessoa = null;
        if (registerDTO.getInserirPessoa()) {
            pessoa = pessoaFacade.criarPessoaDoUsuario(registerDTO);
        } else {
            pessoa = pessoaFacade.buscarPessoaAtivasPorCpfOrCnpj(StringUtil.retornaApenasNumeros(registerDTO.getDadosPessoais().getCpfCnpj()));
        }
        usuarioWeb.setPessoa(pessoa);

        return salvarRetornando(usuarioWeb);
    }

    public UsuarioWeb registrarUsuarioDTE(RegisterDteDTO registerDTO) throws NfseOperacaoNaoPermitidaException, ValidacaoException {
        UsuarioWeb usuarioWeb = new UsuarioWeb();
        List<NfseUserAuthority> authorities = Lists.newArrayList();
        usuarioWeb.setLogin(registerDTO.getPrestador().getPessoa().getDadosPessoais().getCpfCnpj());
        usuarioWeb.setPassword(registerDTO.getPassword());
        usuarioWeb.setEmail(registerDTO.getEmail());
        usuarioWeb.setActivated(false);
        usuarioWeb.setActivationKey(RandomUtil.generateActivationKey());
        authorities.add(new NfseUserAuthority(usuarioWeb, authorityService.buscarPorTipo(AuthoritiesConstants.USER)));
        authorities.add(new NfseUserAuthority(usuarioWeb, authorityService.buscarPorTipo(AuthoritiesConstants.CONTRIBUINTE)));
        usuarioWeb.setNfseUserAuthorities(authorities);

        usuarioWeb.setPessoa(pessoaFacade.recuperar(registerDTO.getPrestador().getPessoa().getId()));

        usuarioWeb.setUserDteCadastrosEconomicos(new ArrayList<UserDteCadastroEconomico>());
        UserDteCadastroEconomico userDteCadastroEconomico = new UserDteCadastroEconomico();
        userDteCadastroEconomico.setUsuarioWeb(usuarioWeb);
        userDteCadastroEconomico.setCadastroEconomico(cadastroEconomicoFacade.recuperar(registerDTO.getPrestador().getId()));
        usuarioWeb.getUserDteCadastrosEconomicos().add(userDteCadastroEconomico);

        usuarioWeb.setUserDteCadastroEconomico(userDteCadastroEconomico);

        return em.merge(usuarioWeb);
    }


    @Asynchronous
    public void enviarEmailAtivacaoUsuario(UsuarioWeb user) {
        TemplateNfse templateEmail = templateNfseFacade.buscarPorTipo(TipoTemplateNfse.ATIVACAO_CADASTRO);

        String conteudo = new TrocaTagNfseEmailAtivacaoUsuario(user, configuracaoNfseFacade.recuperarConfiguracao())
            .trocarTags(templateEmail.getConteudo());

        enviarEmail(user.getEmail(), "Ativação de Cadastro para acesso ao sistema WP ISS", conteudo);
    }

    @Asynchronous
    public void enviarEmailAtivacaoUsuarioDTE(UsuarioWeb user) {
        TemplateNfse templateEmail = templateNfseFacade.buscarPorTipo(TipoTemplateNfse.ATIVACAO_CADASTRO_DTE);

        String conteudo = new TrocaTagNfseEmailAtivacaoUsuarioDTE(user, configuracaoDteFacade.recuperarConfiguracao())
            .trocarTags(templateEmail.getConteudo());

        enviarEmail(user.getEmail(), "Ativação de Cadastro para acesso ao sistema WP DT-e", conteudo);
    }

    @Asynchronous
    public void convidarUsuario(ConviteUsuarioNfseDTO dto) throws ValidacaoException {

        TemplateNfse templateEmail = templateNfseFacade.buscarPorTipo(TipoTemplateNfse.EMAIL_SOLICITADO_ACESSO_SEM_CADASTRO);
        CadastroEconomico cadastro = cadastroEconomicoFacade.recuperar(dto.getPrestadorServico().getId());

        ConviteUsuarioNfse convite = new ConviteUsuarioNfse();
        convite.setCadastroEconomico(cadastro);
        convite.setConvidadoEm(new Date());
        convite.setDadosPessoais(new DadosPessoaisNfse(dto.getDadosPessoais()));
        conviteUsuarioNfseFacade.salvar(convite);

        String conteudo = new TrocaTagNfseEmailSolicitacaoAcessoSemCadastro(cadastro, dto.getDadosPessoais(), configuracaoNfseFacade.recuperarConfiguracao())
            .trocarTags(templateEmail.getConteudo());

        enviarEmail(dto.getDadosPessoais().getEmail(), "Convite para acesso ao sistema WP ISS", conteudo);
    }

    public void enviarEmailResetarSenha(UsuarioWeb user) {
        TemplateNfse templateEmail = templateNfseFacade.buscarPorTipo(TipoTemplateNfse.SOLICITACAO_ALTERACAO_SENHA);
        String conteudo = new TrocaTagNfseEmailSolicitacaoAlteracaoSenha(user, configuracaoNfseFacade.recuperarConfiguracao())
            .trocarTags(templateEmail.getConteudo());
        try {
            enviarEmail(user.getEmail(), "Alteração de senha para acesso ao sistema WP ISS", conteudo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NfseOperacaoNaoPermitidaException("Erro ao enviar o e-mail!");
        }
    }

    public void enviarEmailResetarSenhaDte(UsuarioWeb usuarioWeb) {
        TemplateNfse templateEmail = templateNfseFacade.buscarPorTipo(TipoTemplateNfse.SOLICITACAO_ALTERACAO_SENHA_DTE);
        String conteudo = new TrocaTagEmailSolicitacaoAlteracaoSenhaDte(usuarioWeb, configuracaoDteFacade.recuperarConfiguracao())
            .trocarTags(templateEmail.getConteudo());
        try {
            enviarEmail(usuarioWeb.getEmail(), "Alteração de senha para acesso ao sistema WP DT-e", conteudo);
        } catch (Exception e) {
            throw new NfseOperacaoNaoPermitidaException("Erro ao enviar o e-mail!");
        }
    }

    public void enviarEmail(String mail, String titulo, String conteudo) {
        EmailService.getInstance().enviarEmail(mail, titulo, conteudo);
    }


    public UsuarioWeb ativarRegistro(String key) throws ValidacaoException {
        UsuarioWeb user = buscarNfseUserPorKey(key);
        if (user != null) {
            user.setActivated(true);
            user.setActivationKey(null);
            vincularConviteDoUsuario(user);
            return salvarRetornando(user);
        }
        return null;
    }

    private void vincularConviteDoUsuario(UsuarioWeb user) throws ValidacaoException {
        List<ConviteUsuarioNfse> convites = buscarTodosConvitesDoUsuario(user.getLogin());
        for (ConviteUsuarioNfse convite : convites) {
            adicionarCadastroEconomicoAoUsuario(user, convite.getCadastroEconomico(), Lists.newArrayList(PermissaoUsuarioEmpresaNfse.values()));
            conviteUsuarioNfseFacade.remover(convite);
        }
    }

    public UsuarioWeb completePasswordReset(String newPassword, String key) {
        UsuarioWeb user = buscarNfseUserPorResetKey(key);
        if (user == null) {
            return null;
        }
        user.setPassword(newPassword);
        user.setResetKey(null);
        user.setResetDate(null);
        return salvarRetornando(user);
    }

    public ImagemUsuarioNfseDTO buscarImagemDoUsuario(String login) {
        Pessoa pessoa = buscarPessoaPorLoginUsuario(login);
        if (pessoa == null || pessoa.getArquivo() == null) {
            return null;
        }
        ImagemUsuarioNfseDTO imagem = new ImagemUsuarioNfseDTO(pessoa.getArquivo().toArquivoDTO().getConteudo(),
            (PessoaNfseDTO) pessoa.toNfseDto(), pessoa.getId());
        return imagem;
    }

    public void notificarSolicitacaoAcesso(UserNfseCadastroEconomico userCadastro) {
        Notificacao not = new Notificacao();
        not.setTitulo("Solicitação de Acesso à empresa");
        not.setGravidade(Notificacao.Gravidade.ERRO);
        not.setDescricao("O Usuário " + userCadastro.getUsuarioNfse().getPessoa().getNomeCpfCnpj()
            + " solicita um novo acesso à empresa " +
            userCadastro.getCadastroEconomico()
            + "  no sistema de ISS Online");

        not.setLink("/usuario-web/editar/" + userCadastro.getUsuarioNfse().getId() + "/");
        not.setTipoNotificacao(TipoNotificacao.COMUNICACAO_NFS_ELETRONICA);
        NotificacaoService.getService().notificar(not);

        try {
            TemplateNfse templateEmail = templateNfseFacade.buscarPorTipo(TipoTemplateNfse.SOLICITACAO_ACESSO_EMPRESA);
            CadastroEconomico cadastroEconomico = cadastroEconomicoFacade.recuperar(userCadastro.getCadastroEconomico().getId());
            String conteudo = new TrocaTagNfseEmailSolicitacaoAcessoEmpresa(cadastroEconomico, userCadastro.getUsuarioNfse(), configuracaoNfseFacade.recuperarConfiguracao())
                .trocarTags(templateEmail.getConteudo());
            enviarEmail(userCadastro.getUsuarioNfse().getEmail(), "Solicitação de acesso à empresa no sistema WP ISS", conteudo);
        } catch (Exception e) {
            logger.error("Não foi possível enviar o email de solicitação de acesso à empresa ", e);
        }
    }

    public void permitirAcesso(UserNfseCadastroEconomico userCadastro) {
        userCadastro.setSituacao(UserNfseCadastroEconomico.Situacao.APROVADO);
        update(userCadastro);
        try {
            TemplateNfse templateEmail = templateNfseFacade.buscarPorTipo(TipoTemplateNfse.PERMISSAO_ACESSO_EMPRESA);
            CadastroEconomico cadastroEconomico = cadastroEconomicoFacade.recuperar(userCadastro.getCadastroEconomico().getId());
            String conteudo = new TrocaTagNfseEmailPermissaoAcessoEmpresa(cadastroEconomico, userCadastro.getUsuarioNfse(), configuracaoNfseFacade.recuperarConfiguracao())
                .trocarTags(templateEmail.getConteudo());
            enviarEmail(userCadastro.getUsuarioNfse().getEmail(), "Aprovação de acesso à empresa no sistema WP ISS", conteudo);
        } catch (Exception e) {
            logger.error("Não foi possível enviar o email de solicitação de acesso à empresa ", e);
        }
    }

    public void trocarEmpresaLogada(Long idPrestador, String login) {
        UsuarioWeb user = recuperarUsuarioPorLogin(login);
        CadastroEconomico cadastroEconomico = cadastroEconomicoFacade.recuperar(idPrestador);
        UserNfseCadastroEconomico cadastroUsuario = null;
        for (UserNfseCadastroEconomico c : user.getUserNfseCadastroEconomicos()) {
            if (c.getCadastroEconomico().getId().equals(idPrestador)) {
                cadastroUsuario = c;
                break;
            }
        }

        if (user.isAdmin()) {
            if (cadastroUsuario == null) {
                cadastroUsuario = new UserNfseCadastroEconomico();
            }
            cadastroUsuario.setCadastroEconomico(cadastroEconomico);
            cadastroUsuario.setUsuarioNfse(user);
            cadastroUsuario.setSituacao(UserNfseCadastroEconomico.Situacao.APROVADO);
            cadastroUsuario.setPermissoes(Lists.newArrayList(PermissaoUsuarioEmpresaNfse.values()));
            user.getUserNfseCadastroEconomicos().add(cadastroUsuario);
            user.setUserNfseCadastroEconomico(cadastroUsuario);
            salvarRetornando(user);

        } else if (cadastroUsuario != null) {
            user.setUserNfseCadastroEconomico(cadastroUsuario);
            salvarRetornando(user);
        }

    }


    public List<PrestadorUsuarioNfseDTO> buscarNfseUserPorEmpresa(Long empresaId, boolean apenasInativos) {
        String sql = "select distinct usuario.id, " +
            " usuario.EMAIL, " +
            " pf.NOME, " +
            " pf.CPF, " +
            " usuCad.situacao, " +
            " coalesce(usuCad.contador, 0) as contador," +
            " usuCad.funcao" +
            " from UsuarioWeb usuario " +
            " inner join PESSOAFISICA pf on pf.id = usuario.PESSOA_ID " +
            " INNER JOIN UserNfseCadastroEconomico usuCad on usuCad.usuarioNfse_id = usuario.id " +
            " where usuCad.cadastroEconomico_id = :empresaId " +
            " and not exists (select 1 from nfse_user_authority au " +
            "  inner join nfse_authority nau on nau.id = au.nfseauthority_id " +
            "  where au.user_id = usuario.id and nau.name = :admin) ";
        if (apenasInativos) {
            sql += " and usuCad.situacao = :pendente";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("empresaId", empresaId);
        q.setParameter("admin", AuthoritiesConstants.ADMIN);
        if (apenasInativos) {
            q.setParameter("pendente", UserNfseCadastroEconomico.Situacao.PENDENTE.name());
        }
        List<PrestadorUsuarioNfseDTO> dtos = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            PrestadorUsuarioNfseDTO dto = new PrestadorUsuarioNfseDTO();
            dto.setId(((Number) obj[0]).longValue());
            dto.setEmail((String) obj[1]);
            dto.setNome((String) obj[2]);
            dto.setLogin((String) obj[3]);
            dto.setPermitido(UserNfseCadastroEconomico.Situacao.APROVADO.name().equals(obj[4].toString()));
            dto.setContador(((BigDecimal) obj[5]).compareTo(BigDecimal.ZERO) != 0);
            dto.setDescricao((String) obj[6]);
            dtos.add(dto);
        }

        for (PrestadorUsuarioNfseDTO userNfseDTO : dtos) {
            String sqlPermissoes = "select permissao.permissao " +
                " from PERMISSAOCMCNFSE permissao" +
                " inner join UserNfseCadastroEconomico usuCad on usuCad.id = permissao.USERNFSECADASTROECONOMICO_ID " +
                " where usuCad.usuarioNfse_id = :idUsuario and usuCad.cadastroEconomico_id = :empresaId";
            Query qPermissoes = em.createNativeQuery(sqlPermissoes);
            qPermissoes.setParameter("idUsuario", userNfseDTO.getId());
            qPermissoes.setParameter("empresaId", empresaId);
            userNfseDTO.setRoles(qPermissoes.getResultList());
        }
        return dtos;
    }


    public UserNfseCadastroEconomico buscarNfseUserPorEmpresaAndLogin(Long empresaId, String login) {
        String sql = "select usuCad from UserNfseCadastroEconomico usuCad where usuCad.usuarioNfse.login = :login and usuCad.cadastroEconomico.id = :empresaId ";
        Query q = em.createQuery(sql);
        q.setParameter("empresaId", empresaId);
        q.setParameter("login", login);
        if (!q.getResultList().isEmpty()) {
            return (UserNfseCadastroEconomico) q.getResultList().get(0);
        }
        return null;
    }


    public List<UserNfseDTO> buscarNfseUserPorCpf(String filtro) {
        String sql = "select usuario.id, usuario.EMAIL, pf.NOME, pf.CPF, usuario.activated " +
            " from UsuarioWeb usuario " +
            " inner join PESSOAFISICA pf on pf.id = usuario.PESSOA_ID " +
            " where replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') like replace(replace(replace(:filtro,'.',''),'-',''),'/','') " +
            "or lower(pf.nome) like :filtro " +
            "or lower(usuario.EMAIL) like :filtro ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        List<Object[]> result = q.getResultList();
        return popularUserDto(result);
    }

    private List<UserNfseDTO> popularUserDto(List<Object[]> result) {
        List<UserNfseDTO> dtos = Lists.newArrayList();
        for (Object[] obj : result) {
            UserNfseDTO dto = new RegisterNfseDTO();
            dto.setId(((Number) obj[0]).longValue());
            dto.setEmail((String) obj[1]);
            dto.setNome((String) obj[2]);
            dto.setLogin(StringUtil.retornaApenasNumeros((String) obj[3]));
            dto.setActivated(((BigDecimal) obj[4]).compareTo(BigDecimal.ZERO) != 0);
            dtos.add(dto);
        }
        return dtos;
    }


    public List<ConviteUsuarioNfse> buscarTodosConvitesDoUsuario(String cpfCnpj) {
        String hql = "select distinct convite from ConviteUsuarioNfse convite where convite.dadosPessoais.cpfCnpj = :cpfCnpj";
        return em.createQuery(hql).setParameter("cpfCnpj", cpfCnpj).getResultList();
    }


    public void ativarOuDesativarUsuario(Long empresaId, String login, String loginResponsavel) {
        UsuarioWeb userResponsavel = null;
        if (loginResponsavel != null && !loginResponsavel.isEmpty())
            userResponsavel = buscarUsuarioWebPorLogin(loginResponsavel);

        UserNfseCadastroEconomico userNfseCadastroEconomico = buscarNfseUserPorEmpresaAndLogin(empresaId, login);

        if (UserNfseCadastroEconomico.Situacao.APROVADO.equals(userNfseCadastroEconomico.getSituacao())) {
            userNfseCadastroEconomico.setSituacao(UserNfseCadastroEconomico.Situacao.PENDENTE);
        } else {
            userNfseCadastroEconomico.setSituacao(UserNfseCadastroEconomico.Situacao.APROVADO);
        }
        userNfseCadastroEconomico.setUsuarioResponsavel(userResponsavel);

        update(userNfseCadastroEconomico);
    }

    private void update(UserNfseCadastroEconomico userNfseCadastroEconomico) {
        em.merge(userNfseCadastroEconomico);
    }

    public void adicionarOuRemoverRecursoDosUsuarios(PrestadorUsuarioNfseDTO dto) {
        UserNfseCadastroEconomico userNfseCadastroEconomico = buscarNfseUserPorEmpresaAndLogin(dto.getPrestador().getId(), StringUtil.retornaApenasNumeros(dto.getLogin()));
        userNfseCadastroEconomico.setPermissoes(new ArrayList<PermissaoUsuarioEmpresaNfse>());
        userNfseCadastroEconomico.setFuncao(dto.getDescricao());
        userNfseCadastroEconomico.setContador(!dto.isContador());
        for (String role : dto.getRoles()) {
            Util.adicionarObjetoEmLista(userNfseCadastroEconomico.getPermissoes(), PermissaoUsuarioEmpresaNfse.valueOf(role));
        }
        update(userNfseCadastroEconomico);
    }

    public UsuarioWeb adicionarCadastroEconomicoAoUsuario(UsuarioWeb usuario,
                                                          CadastroEconomico cadastroEconomico,
                                                          ArrayList<PermissaoUsuarioEmpresaNfse> permissaoUsuarioEmpresaNfses) {
        return adicionarCadastroEconomicoAoUsuario(usuario, cadastroEconomico, permissaoUsuarioEmpresaNfses, null, UserNfseCadastroEconomico.Situacao.APROVADO);
    }

    public UsuarioWeb adicionarCadastroEconomicoAoUsuario(UsuarioWeb usuario,
                                                          CadastroEconomico cadastroEconomico,
                                                          List<PermissaoUsuarioEmpresaNfse> permissaoUsuarioEmpresaNfses,
                                                          UsuarioWeb responsavel, UserNfseCadastroEconomico.Situacao situacao) {
        UserNfseCadastroEconomico cadastro = new UserNfseCadastroEconomico();
        cadastro.setCadastroEconomico(cadastroEconomico);
        cadastro.setUsuarioNfse(usuario);
        cadastro.setPermissoes(permissaoUsuarioEmpresaNfses);
        cadastro.setSituacao(situacao);
        cadastro.setUsuarioResponsavel(responsavel);
        usuario.getUserNfseCadastroEconomicos().add(cadastro);
        return usuario;
    }

    public boolean hasEscritorioContabilidadeDaPessoa(Pessoa pessoa) {
        Query q = em.createNativeQuery("select ec.id from EscritorioContabil ec " +
            " inner join CadastroEconomico ce on ce.escritorioContabil_id = ec.id " +
            " where ec.pessoa_id = :idPessoa or ec.responsavel_id = :idPessoa");
        q.setParameter("idPessoa", pessoa.getId());
        return !q.getResultList().isEmpty();
    }

    public UsuarioWeb criarHistoricoAtivacaoInativacao(UsuarioWeb nfseUser, UsuarioSistema usuarioSistema) {
        UserNfseHistoricoAtivacao historico = new UserNfseHistoricoAtivacao();
        historico.setUsuarioNfse(nfseUser);
        historico.setDataHistorico(new Date());
        historico.setTipo(nfseUser.isActivated());
        historico.setUsuarioSistema(usuarioSistema);
        nfseUser.getUserNfseHistoricosAtivacao().add(historico);
        return salvarRetornando(nfseUser);
    }

    public Boolean hasAcessoEmpresa(String login, String inscricaoMunicipal) {
        UsuarioWeb user = buscarUsuarioWebPorLogin(login);
        CadastroEconomico cadastroEconomico = cadastroEconomicoFacade.buscarCadastroEconomicoPorInscricaoMunicipal(inscricaoMunicipal);
        return user.hasPermissao(cadastroEconomico);
    }

    public void changePassword(String login, String newPassword) {
        UsuarioWeb usuarioWeb = buscarUsuarioWebPorLogin(login);
        usuarioWeb.setPassword(newPassword);
        salvarRetornando(usuarioWeb);
    }

    public UsuarioWeb buscarUsuarioWebByIdPessoa(Long idPessoa) {
        String sql = "select u.* from UsuarioWeb u where u.pessoa_id = :idPessoa";
        List<UsuarioWeb> usuarios = em.createNativeQuery(sql, UsuarioWeb.class)
            .setParameter("idPessoa", idPessoa).getResultList();
        if (!usuarios.isEmpty()) {
            return usuarios.get(0);
        }
        return null;
    }

    public String encripitografarSenha(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    public void criarUsuarioWeb(CadastroEconomico cadastroEconomico,
                                String password,
                                Boolean ativo,
                                Boolean todasPermissoes,
                                Boolean enviaEmail) {
        UsuarioWeb newUser = new UsuarioWeb();

        newUser.setLogin(StringUtil.retornaApenasNumeros(cadastroEconomico.getPessoa().getCpf_Cnpj()));
        newUser.setPassword(encripitografarSenha(password));
        newUser.setEmail(cadastroEconomico.getPessoa().getEmail());
        newUser.setActivated(ativo);
        newUser.setActivationKey(RandomUtil.generateActivationKey());

        List<NfseUserAuthority> authorities = Lists.newArrayList();
        authorities.add(new NfseUserAuthority(newUser, authorityService.buscarPorTipo(AuthoritiesConstants.USER)));
        authorities.add(new NfseUserAuthority(newUser, authorityService.buscarPorTipo(AuthoritiesConstants.CONTRIBUINTE)));
        newUser.setNfseUserAuthorities(authorities);

        newUser.setPessoa(cadastroEconomico.getPessoa());

        UserNfseCadastroEconomico usuarioCadastro = new UserNfseCadastroEconomico();
        usuarioCadastro.setCadastroEconomico(cadastroEconomico);
        usuarioCadastro.setUsuarioNfse(newUser);
        usuarioCadastro.setSituacao(UserNfseCadastroEconomico.Situacao.PENDENTE);
        usuarioCadastro.setPermissoes(new ArrayList<PermissaoUsuarioEmpresaNfse>());

        if (todasPermissoes) {
            for (PermissaoUsuarioEmpresaNfse permissaoUsuarioEmpresaNfse : PermissaoUsuarioEmpresaNfse.values()) {
                usuarioCadastro.getPermissoes().add(permissaoUsuarioEmpresaNfse);
            }
        }

        newUser.setUserNfseCadastroEconomicos(new ArrayList());
        newUser.getUserNfseCadastroEconomicos().add(usuarioCadastro);

        newUser = salvarRetornando(newUser);

        if (enviaEmail)
            enviarEmailAtivacaoUsuario(newUser);
    }

    public void removerRegistroMongodbNfse(Pessoa pessoa) {
        AsyncExecutor.getInstance().execute(new AssistenteBarraProgresso(sistemaFacade.getUsuarioCorrente(),
                "Removendo pessoa do cache da nfs-e", 0),
            () -> {
                UsuarioWeb usuarioWeb = buscarUsuarioWebByIdPessoa(pessoa.getId());
                if (usuarioWeb != null) {
                    removerRegistroMongodbNfse(usuarioWeb);
                }
                return null;
            });
    }

    private void removerRegistroMongodbNfse(UsuarioWeb usuarioWeb) {
        if (usuarioWeb.getId() == null) return;
        AssistenteBarraProgresso assistente = new AssistenteBarraProgresso(sistemaFacade.getUsuarioCorrente(),
            "Removendo usuário " + usuarioWeb.getLogin() + " do cache da nfs-e", 0);
        AsyncExecutor.getInstance().execute(assistente, ()-> {
            try {
                ConfiguracaoNfse configuracaoNfse = configuracaoNfseFacade.recuperarConfiguracao();
                if (configuracaoNfse.getUrlRest() != null) {
                    RestTemplate restTemplate = Util.getRestTemplateWithConnectTimeout();
                    restTemplate.delete(configuracaoNfse.getUrlRest() + "/api/publico/user/remove-mongo/" + usuarioWeb.getId());
                }
            } catch (Exception e) {
                logger.error("Erro ao remover o usuário web do mongodb da nfs-e. {}", e.getMessage());
                logger.debug("Detalhes do erro ao remover o usuário web do mongodb da nfs-e. ", e);
            }
            return null;
        });
    }

    public void updateUserDteCadastroEconomico(UserDteCadastroEconomico userDteCadastroEconomico) {
        em.merge(userDteCadastroEconomico);
    }

    public List<UsuarioWeb> buscarUsuariosDte(CadastroEconomico cadastroEconomico) {
        String sql = " select uw.* from usuarioweb uw " +
            " inner join userdtecadastroeconomico uw_ce on uw_ce.usuarioweb_id = uw.id " +
            " where uw_ce.cadastroeconomico_id = :id_cadastro ";
        Query q = em.createNativeQuery(sql, UsuarioWeb.class);
        q.setParameter("id_cadastro", cadastroEconomico);
        return q.getResultList();
    }

    public List<UsuarioWeb> buscarTodosUsuariosAtivos() {
        String hql = " select new UsuarioWeb(u.id) from UsuarioWeb u where u.activated = :ativo ";
        return em.createQuery(hql)
            .setParameter("ativo", Boolean.TRUE)
            .getResultList();
    }

    public void atribuirUltimoAcesso(String cpfCnpj) {
        em.createNativeQuery("update usuarioweb set ultimoacesso = current_date where login = :login ")
            .setParameter("login", cpfCnpj)
            .executeUpdate();
    }

    public void atribuirSenhaCriotografada(String cpfCnpj, String senha) {
        em.createNativeQuery("update usuarioweb set password = :senha where login = :login ")
            .setParameter("login", cpfCnpj).setParameter("senha", senha)
            .executeUpdate();
    }

    public List<CadastroEconomico> buscarCadastroEconomicoPorUsuarioNfse(UsuarioWeb usuarioWeb, String parte) {
        return cadastroEconomicoFacade.buscarCadastroEconomicoPorUsuarioNfse(usuarioWeb, parte);
    }

    public void removerTermoDeUsoDoUsuario(Pessoa pessoa) {
        if (pessoa.getId() != null) {
            em.createNativeQuery("delete from TermoUsoAppServidor where usuario_id = (select id from UsuarioWeb where pessoa_id = :idPessoa)")
                .setParameter("idPessoa", pessoa.getId()).executeUpdate();
        }
    }

    public Pessoa criarLoginPortalWebSeNaoExistir(String email, Pessoa pessoa) {
        UsuarioWeb usuarioWeb = buscarUsuarioWebByIdPessoa(pessoa.getId());
        if (usuarioWeb == null) {
            pessoa = atualizarEmailPessoa(email, pessoa);
            String senha = Util.gerarSenhaUsuarioPortalWeb();
            usuarioWeb = criaUsuarioWeb(senha, pessoa);
            pessoa.getUsuariosWeb().add(usuarioWeb);
            em.persist(usuarioWeb);
            String corpoEmail = criarEmailNovoUsuario(senha, pessoa);
            enviarEmail(email, "Acesso à Área do Contribuinte", corpoEmail);
        } else {
            String corpoEmail = criarEmailUsuarioJaExistente(pessoa);
            enviarEmail(email, "Acesso à Área do Contribuinte", corpoEmail);
        }
        return pessoa;
    }

    private String criarEmailUsuarioJaExistente(Pessoa pessoa) {
        StringBuilder usuarioExistente = new StringBuilder();
        usuarioExistente.append("Recebemos sua solicitação para acesso ao Portal do Contribuinte da Prefeitura Municipal de Rio Branco.").append("<br/>");
        usuarioExistente.append("Informamos que seu usuário já foi cadastrado, caso não lembre a sua senha acesso o link abaixo e siga os procedimentos.").append("<br/>");
        usuarioExistente.append("Esqueci minha Senha!.").append("<br/>");
        usuarioExistente.append("Caso não tenha efetuado o cadastro do seu usuário, dirija-se até um centro de atendimento para regularizar seu Cadastro Único.").append("<br/>");
        StringBuilder email = new StringBuilder(montarEmailPortalWeb(pessoa.getNome(), pessoa.getCpf_Cnpj(), usuarioExistente.toString()));
        return email.toString();
    }

    public String montarEmailPortalWeb(String nome, String cpfCnpj, String detalhe) {
        StringBuilder sb = new StringBuilder();
        sb.append("Comunicado.").append("<br/>");
        if (nome != null) {
            sb.append("Olá ").append("<b>").append(nome).append(" (").append(cpfCnpj).append(")").append("</b>").append("<br/><br/>");
        }
        sb.append(detalhe);
        sb.append(getParteFinalDoEmail());
        return sb.toString();
    }

    public String getParteFinalDoEmail() {
        String rodapePadrao = configuracaoTributarioFacade.recuperarRodapeEmailPortal(false);
        return "<br/><br/>" + rodapePadrao;
    }

    public String criarEmailNovoUsuario(String senha, Pessoa pessoa) {
        return criarEmailNovoUsuario(senha, pessoa.getNome(), pessoa.getCpf_Cnpj(),
            pessoa.isPessoaJuridica(), pessoa.isPessoaFisica(), false);
    }

    private String criarEmailNovoUsuario(String senha, String nome, String cpfCnpj, boolean isPessoaJuridica, boolean isPessoaFisica, boolean isAplicativo) {
        StringBuilder novoUsuario = new StringBuilder();
        novoUsuario.append("Recebemos sua solicitação para acesso ao <strong>Portal do Cidadão</> da Prefeitura Municipal de Rio Branco.").append("<br/>");
        novoUsuario.append("Para a sua segurança, <strong>Acesse à Área do contribuinte</> do sistema no endereço <a href=\"http://portalcidadao.riobranco.ac.gov.br\">http://portalcidadao.riobranco.ac.gov.br</a>, " + (isAplicativo ? " ou aplicativo mobile, " : ""));
        novoUsuario.append("efetue seu login com a senha informada abaixo e altere sua senha de acesso por uma combinação de sua preferência.").append("<br/>");
        novoUsuario.append("<br/>");
        novoUsuario.append("Login: ").append(StringUtil.retornaApenasNumeros(cpfCnpj)).append("<br/>");
        novoUsuario.append("Senha temporária: ").append(senha).append("<br/>");
        novoUsuario.append("<br/>");
        novoUsuario.append("Caso você não tenha solicitado estas informações, por favor entre em contato com o suporte.").append("<br/>");
        String cpf_cnpj = cpfCnpj;
        if (isPessoaJuridica) {
            cpf_cnpj = Util.formatarCnpj(cpf_cnpj);
        } else if (isPessoaFisica) {
            cpf_cnpj = Util.formatarCpf(cpf_cnpj);
        }
        StringBuilder textoEmail = new StringBuilder(montarEmailPortalWeb(nome, cpf_cnpj, novoUsuario.toString()));
        return textoEmail.toString();
    }

    private UsuarioWeb criaUsuarioWeb(String senha, Pessoa pessoa) {
        return criaUsuarioWeb(senha, pessoa, null);
    }

    private UsuarioWeb criaUsuarioWeb(String senha, Pessoa pessoa, CadastroEconomico cadastroEconomico) {
        UsuarioWeb usuarioWeb = new UsuarioWeb();
        usuarioWeb.setPessoa(pessoa);
        usuarioWeb.setLogin(StringUtil.retornaApenasNumeros(pessoa.getCpf_Cnpj()));
        usuarioWeb.setEmail(pessoa.getEmail());
        usuarioWeb.setPassword(encripitografarSenha(senha));
        usuarioWeb.setPasswordTemporary(true);
        usuarioWeb.setPrimeiraSenha(senha);
        usuarioWeb.setActivated(true);
        usuarioWeb.setActivationKey(RandomUtil.generateActivationKey());

        List<NfseUserAuthority> authorities = Lists.newArrayList();
        authorities.add(new NfseUserAuthority(usuarioWeb, nfseAuthorityFacade.buscarPorTipo(AuthoritiesConstants.USER)));
        authorities.add(new NfseUserAuthority(usuarioWeb, nfseAuthorityFacade.buscarPorTipo(AuthoritiesConstants.CONTRIBUINTE)));
        usuarioWeb.setNfseUserAuthorities(authorities);

        if (cadastroEconomico != null) {
            usuarioWeb.setUserNfseCadastroEconomicos(Lists.newArrayList());

            UserNfseCadastroEconomico userNfseCadastroEconomico = new UserNfseCadastroEconomico();
            userNfseCadastroEconomico.setUsuarioNfse(usuarioWeb);
            userNfseCadastroEconomico.setCadastroEconomico(cadastroEconomico);
            userNfseCadastroEconomico.setSituacao(UserNfseCadastroEconomico.Situacao.APROVADO);
            userNfseCadastroEconomico.setPermissoes(Lists.newArrayList(PermissaoUsuarioEmpresaNfse.values()));

            usuarioWeb.setUserNfseCadastroEconomicos(Lists.newArrayList(userNfseCadastroEconomico));
        }
        return usuarioWeb;
    }

    private Pessoa atualizarEmailPessoa(String email, Pessoa pessoa) {
        pessoa.setEmail(email);
        return em.merge(pessoa);
    }

    private String criarEmailMaisDeUmCPF() {
        StringBuilder usuarioExistente = new StringBuilder();
        usuarioExistente.append("Recebemos sua solicitação para acesso ao Portal do Contribuinte da Prefeitura Municipal de Rio Branco.").append("<br/>");
        usuarioExistente.append("Informamos que encontramos problemas em no seu cadastro em nossos sistemas.").append("<br/>");
        usuarioExistente.append("Por favor dirija-se até um centro de atendimento para regularizar seu Cadastro Único.").append("<br/>");
        StringBuilder email = new StringBuilder(montarEmailPortalWeb("", "", usuarioExistente.toString()));
        return email.toString();
    }


    private String criarEmailPessoaNaoExistente() {
        StringBuilder usuarioExistente = new StringBuilder();
        usuarioExistente.append("Recebemos sua solicitação para acesso ao Portal do Contribuinte da Prefeitura Municipal de Rio Branco.").append("<br/>");
        usuarioExistente.append("Informamos que não encontramos nenhum cadastro em nossos sistemas com o seu CPF/CNPJ.").append("<br/>");
        usuarioExistente.append("Por favor dirija-se até um centro de atendimento para regularizar seu Cadastro Único.").append("<br/>");
        StringBuilder email = new StringBuilder(montarEmailPortalWeb(null, null, usuarioExistente.toString()));
        return email.toString();
    }



    public String criarLoginPortalWebAndPessoaSeNaoExistir(UsuarioPortalWebDTO dto) {
        PessoaFisica pf = new PessoaFisica();
        pf.setNome(dto.getNome());
        pf.setCpf(dto.getCpf());
        pf.setMae(dto.getNomeMae());
        pf.setEmail(dto.getEmail());
        pf.setDataNascimento(dto.getNascimento());
        pf.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);
        PessoaFisica merge = em.merge(pf);

        String senha = Util.gerarSenhaUsuarioPortalWeb();
        UsuarioWeb usuarioWeb = criaUsuarioWeb(senha, merge);
        em.persist(usuarioWeb);

        String corpoEmail = criarEmailNovoUsuario(senha, dto.getNome(), dto.getCpf(), false, true, true);
        return corpoEmail;
    }

    private UsuarioWeb getUsuarioPorPessoa(List<Pessoa> pessoas) {
        for (Pessoa pessoa : pessoas) {
            UsuarioWeb usuarioWeb = buscarUsuarioWebByIdPessoa(pessoa.getId());
            if (usuarioWeb != null) {
                return usuarioWeb;
            }
        }
        return null;
    }


    public UsuarioPortalWebDTO criarLoginPortalWeb(UsuarioPortalWebDTO dto) throws UsuarioWebProblemasCadastroException {
        List<Pessoa> pessoas = pessoaFacade.listaTodasPessoasPorCPFCNPJ(dto.getCpf());
        UsuarioWeb usuario = null;
        usuario = getUsuarioPorPessoa(pessoas);
        if (usuario == null) {
            usuario = buscarUsuarioWebPorLogin(StringUtil.retornaApenasNumeros(dto.getCpf()));
        }

        if (usuario == null && pessoas.isEmpty()) {
            String corpoEmail = criarLoginPortalWebAndPessoaSeNaoExistir(dto);
            enviarEmail(dto.getEmail(), "Acesso a Área Exclusiva", corpoEmail);
            return dto;
        }
        if (usuario != null && pessoas.isEmpty()) {
            throw new UsuarioWebProblemasCadastroException("Usuario já cadastrado na base de dados. Por favor, vá para a área de login.");

        }
        if (usuario != null) {
            Pessoa pessoa = pessoas.get(0);
            usuario.setPessoa(pessoa);
            if (pessoa.getEmail() != null) {
                salvarRetornando(usuario);
            }
            throw new UsuarioWebProblemasCadastroException("Usuario já cadastrado na base de dados. Por favor, vá para a área de login.");
        }

        if (pessoas.size() == 1) {
            Pessoa pessoa = pessoas.get(0);
            if (pessoa.getEmail() == null) {
                pessoa.setEmail(dto.getEmail());
            }
            if (pessoa.getEmail() == null) {
                throw new UsuarioWebEmailNaoExistenteException("Seu cadastro de pessoa está sem email vinculado, por favor, vá à um local de atendimento para regularizar seu cadastro.");
            }
            criarLoginPortalWebSeNaoExistir(pessoa.getEmail(), pessoa);
            dto.setPessoa(new WSPessoa(pessoa));
            return dto;
        }
        String corpoEmail = criarEmailMaisDeUmCPF();
        enviarEmail(dto.getEmail(), "Acesso a Área Exclusiva", corpoEmail);
        return null;
    }

    public WSPessoa criarLoginPortalWeb(String email, String cpf) {
        List<Pessoa> pessoas = pessoaFacade.listaTodasPessoasPorCPFCNPJ(cpf);
        if (pessoas == null || pessoas.isEmpty()) {
            String corpoEmail = criarEmailPessoaNaoExistente();
            enviarEmail(email, "Acesso a Área do Contribuinte", corpoEmail);
        } else if (pessoas.size() == 1) {
            Pessoa pessoa = criarLoginPortalWebSeNaoExistir(email, pessoas.get(0));
            return new WSPessoa(pessoa);
        } else if (pessoas.size() > 1) {
            String corpoEmail = criarEmailMaisDeUmCPF();
            enviarEmail(email, "Acesso a Área do Contribuinte", corpoEmail);
        }
        return null;
    }

    public String criarNovaSenhaeEnviarPorEmail(Pessoa pessoa) {
        UsuarioWeb usuarioWeb = buscarUsuarioWebByIdPessoa(pessoa.getId());
        return criarNovaSenhaeEnviarPorEmail(usuarioWeb);
    }

    public String criarNovaSenhaeEnviarPorEmail(UsuarioWeb usuarioWeb) {
        String senha = "";
        if (!usuarioWeb.isPasswordTemporary()) {
            senha = Util.gerarSenhaUsuarioPortalWeb();
            usuarioWeb.setPassword(encripitografarSenha(senha));
            usuarioWeb.setUltimoAcesso(null);
            em.merge(usuarioWeb);
        } else {
            senha = usuarioWeb.getPrimeiraSenha();
        }
        return enviarEmailComSenhaCriada(usuarioWeb, senha);
    }

    public String enviarEmailComSenhaCriada(UsuarioWeb usuarioWeb, String senha) {
        String corpoEmail = criarEmailNovoUsuario(senha, usuarioWeb.getPessoa());
        enviarEmail(usuarioWeb.getEmail(), "Acesso à Área do Contribuinte", corpoEmail);
        return "Foi enviado uma nova senha temporária para o e-mail " + usuarioWeb.getEmail() +
            ", caso esse não seja seu email, por favor, dirija-se a uma central de atendimento para regularizar seu cadastro.";
    }

    public UsuarioWeb criarUsuarioWebParaCadastroEconomico(CadastroEconomico cadastroEconomico) {
        if (Strings.isNullOrEmpty(cadastroEconomico.getPessoa().getEmail())) return null;
        UsuarioWeb usuarioWeb = buscarUsuarioWebByIdPessoa(cadastroEconomico.getPessoa().getId());
        if (usuarioWeb == null) {
            String senha = Util.gerarSenhaUsuarioPortalWeb();
            usuarioWeb = criaUsuarioWeb(senha, cadastroEconomico.getPessoa(), cadastroEconomico);
            usuarioWeb = em.merge(usuarioWeb);
            String corpoEmail = criarEmailNovoUsuario(senha, cadastroEconomico.getPessoa());
            enviarEmail(usuarioWeb.getEmail(), "Acesso à Área do Contribuinte", corpoEmail);
        }
        return usuarioWeb;
    }
}
