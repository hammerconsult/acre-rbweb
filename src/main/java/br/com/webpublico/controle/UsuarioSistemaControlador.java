package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.AutorizacaoUsuarioRH;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.TipoAutorizacaoRH;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BloqueioDesbloqueioUsuarioFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.UsuarioSistemaFacade;
import br.com.webpublico.seguranca.SingletonRecursosSistema;
import br.com.webpublico.seguranca.UsuarioSistemaAuthenticationSuccessHandler;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.*;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * @author fabio
 */
@ManagedBean(name = "usuarioSistemaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoUsuarioSistema", pattern = "/admin/usuariosistema/novo/", viewId = "/faces/admin/controleusuario/usuariosistema/edita.xhtml"),
    @URLMapping(id = "editarUsuarioSistema", pattern = "/admin/usuariosistema/editar/#{usuarioSistemaControlador.id}/", viewId = "/faces/admin/controleusuario/usuariosistema/edita.xhtml"),
    @URLMapping(id = "duplicarUsuarioSistema", pattern = "/admin/usuariosistema/duplicar/#{usuarioSistemaControlador.id}/", viewId = "/faces/admin/controleusuario/usuariosistema/edita.xhtml"),
    @URLMapping(id = "listarUsuarioSistema", pattern = "/admin/usuariosistema/listar/", viewId = "/faces/admin/controleusuario/usuariosistema/lista.xhtml"),
    @URLMapping(id = "verUsuarioSistema", pattern = "/admin/usuariosistema/ver/#{usuarioSistemaControlador.id}/", viewId = "/faces/admin/controleusuario/usuariosistema/visualizar.xhtml")
})
public class UsuarioSistemaControlador extends PrettyControlador<UsuarioSistema> implements Serializable, CRUD {

    public static final PasswordEncoder PASSWORD_ENCODER = new ShaPasswordEncoder();
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private BloqueioDesbloqueioUsuarioFacade bloqueioDesbloqueioUsuarioFacade;
    private GrupoUsuario grupoUsuario;
    private GrupoRecurso grupoRecurso;
    private RecursoSistema recursoSistema;
    private ConverterAutoComplete converterGrupoUsuario;
    private ConverterGenerico converterGrupoRecurso;
    private ConverterGenerico converterLotacao;
    private ConverterAutoComplete converterRecursoSistema;
    private ConverterAutoComplete converterPessoaFisica;
    private ConverterAutoComplete converterUnidadeOrganizacional;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private String senha2;
    private String senhaAntiga;
    private String repetesenha;
    private HierarquiaOrganizacional hierarquiaOrgParticipante;
    private HierarquiaOrganizacional hierarquiaOrgOrc;
    private Arquivo arquivoSelecionado;
    private UploadedFile file;
    private String nomeUsuarioCorrente;
    private VigenciaTribUsuario vigenciaTribUsuario;
    private TipoUsuarioTribUsuario tipoUsuarioTribUsuario;
    private LotacaoTribUsuario lotacaoTribUsuario;
    private AutorizacaoTributarioUsuario autorizacaoTributarioUsuario;
    private Boolean mostraPainel;
    private Boolean informarSenha;
    private Boolean alteraSenhaViaLogin;
    private TreeNode rootOrc;
    private TreeNode selectedNode;
    private List<HierarquiaOrganizacional> hierarquias;
    private List<HierarquiaOrganizacional> hierarquiasOrc;
    private String filtroUnidadeAdministrativa;
    private String filtroUnidadeOrcamentaria;
    private AutorizacaoUsuarioRH autorizacaoUsuarioRH;
    private SingletonRecursosSistema singletonRecursosSistema;

    private List<HierarquiaUnidadeUsuario> hierarquiasAdministrativasUsuario;
    private List<HierarquiaUnidadeUsuario> hierarquiasAdministrativasUsuarioPesquisa;
    private List<HierarquiaUnidadeUsuario> hierarquiasOrcamentariasUsuario;
    private List<HierarquiaUnidadeUsuario> hierarquiasOrcamentariasUsuarioPesquisa;
    private HierarquiaUnidadeUsuario hierarquiaAdministrativa;
    private AssinaturaUsuarioSistema assinatura;
    private Map<Long, HierarquiaOrganizacional> mapHierarquias;
    private Boolean apenasUnidadesVigentes;
    private Map<HierarquiaOrganizacional, String> descricaoVigenciaHierarquias;
    private Integer tempoMaximoInatividade;

    public UsuarioSistemaControlador() {
        super(UsuarioSistema.class);
    }

    @PostConstruct
    public void init() {
        singletonRecursosSistema = (SingletonRecursosSistema) Util.getSpringBeanPeloNome("singletonRecursosSistema");
    }

    public TreeNode getRootOrc() {
        return rootOrc;
    }

    public void setRootOrc(TreeNode rootOrc) {
        this.rootOrc = rootOrc;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public Converter getConverterGrupoUsuario() {
        if (this.converterGrupoUsuario == null) {
            this.converterGrupoUsuario = new ConverterAutoComplete(GrupoUsuario.class, usuarioSistemaFacade.getGrupoUsuarioFacade());
        }
        return this.converterGrupoUsuario;
    }

    public ConverterAutoComplete getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, usuarioSistemaFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterHierarquiaOrganizacional;
    }

    public ConverterGenerico getConverterGrupoRecurso() {
        if (this.converterGrupoRecurso == null) {
            this.converterGrupoRecurso = new ConverterGenerico(GrupoRecurso.class, usuarioSistemaFacade.getGrupoRecursoFacade());
        }
        return this.converterGrupoRecurso;
    }

    public ConverterGenerico getConverterLotacao() {
        if (this.converterLotacao == null) {
            this.converterLotacao = new ConverterGenerico(LotacaoVistoriadora.class, usuarioSistemaFacade.getLotacaoVistoriadoraFacade());
        }
        return this.converterLotacao;
    }

    public Converter getConverterRecursoSistema() {
        if (this.converterRecursoSistema == null) {
            this.converterRecursoSistema = new ConverterAutoComplete(RecursoSistema.class, usuarioSistemaFacade.getRecursoSistemaFacade());
        }
        return this.converterRecursoSistema;
    }

    public Converter getConverterPessoaFisica() {
        if (this.converterPessoaFisica == null) {
            this.converterPessoaFisica = new ConverterAutoComplete(PessoaFisica.class, usuarioSistemaFacade.getPessoaFisicaFacade());
        }
        return this.converterPessoaFisica;
    }

    public Converter getConverterUnidadeOrganizacional() {
        if (this.converterUnidadeOrganizacional == null) {
            this.converterUnidadeOrganizacional = new ConverterAutoComplete(UnidadeOrganizacional.class, usuarioSistemaFacade.getUnidadeOrganizacionalFacade());
        }
        return this.converterUnidadeOrganizacional;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalOrcamentaria(String parte) {
        if (!selecionado.getUsuarioUnidadeOrganizacional().isEmpty()) {
            return usuarioSistemaFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorIdDeUnidades(getSistemaControlador().getDataOperacao(), selecionado, parte.trim(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        }
        return new ArrayList<>();
    }

    @Override
    public UsuarioSistema getSelecionado() {
        return selecionado;
    }

    @Override
    public void setSelecionado(UsuarioSistema selecionado) {
        this.selecionado = selecionado;
    }

    public HierarquiaOrganizacional getHierarquiaOrgParticipante() {
        return hierarquiaOrgParticipante;
    }

    public void setHierarquiaOrgParticipante(HierarquiaOrganizacional hierarquiaOrgParticipante) {
        this.hierarquiaOrgParticipante = hierarquiaOrgParticipante;
    }

    public VigenciaTribUsuario getVigenciaTribUsuario() {
        return vigenciaTribUsuario;
    }

    public void setVigenciaTribUsuario(VigenciaTribUsuario vigenciaTribUsuario) {
        this.vigenciaTribUsuario = vigenciaTribUsuario;
    }

    public LotacaoTribUsuario getLotacaoTribUsuario() {
        return lotacaoTribUsuario;
    }

    public void setLotacaoTribUsuario(LotacaoTribUsuario lotacaoTribUsuario) {
        this.lotacaoTribUsuario = lotacaoTribUsuario;
    }

    public AutorizacaoUsuarioRH getAutorizacaoUsuarioRH() {
        return autorizacaoUsuarioRH;
    }

    public void setAutorizacaoUsuarioRH(AutorizacaoUsuarioRH autorizacaoUsuarioRH) {
        this.autorizacaoUsuarioRH = autorizacaoUsuarioRH;
    }

    public AutorizacaoTributarioUsuario getAutorizacaoTributarioUsuario() {
        return autorizacaoTributarioUsuario;
    }

    public void setAutorizacaoTributarioUsuario(AutorizacaoTributarioUsuario autorizacaoTributarioUsuario) {
        this.autorizacaoTributarioUsuario = autorizacaoTributarioUsuario;
    }

    public TipoUsuarioTribUsuario getTipoUsuarioTribUsuario() {
        return tipoUsuarioTribUsuario;
    }

    public void setTipoUsuarioTribUsuario(TipoUsuarioTribUsuario tipoUsuarioTribUsuario) {
        this.tipoUsuarioTribUsuario = tipoUsuarioTribUsuario;
    }

    public void setaLoginNaSenha() {
        if ("".equals(senha2)) {
            senha2 = selecionado.getCpf();
            selecionado.setSenha(senha2);
        }
    }

    @URLAction(mappingId = "novoUsuarioSistema", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        grupoUsuario = new GrupoUsuario();
        grupoRecurso = new GrupoRecurso();
        recursoSistema = new RecursoSistema();
        hierarquiaOrgParticipante = new HierarquiaOrganizacional();
        vigenciaTribUsuario = new VigenciaTribUsuario();
        tipoUsuarioTribUsuario = new TipoUsuarioTribUsuario();
        lotacaoTribUsuario = new LotacaoTribUsuario();
        autorizacaoTributarioUsuario = new AutorizacaoTributarioUsuario();
        setSenha2("");
        setSenhaAntiga("");
        setRepetesenha("");
        arquivoSelecionado = new Arquivo();
        mostraPainel = false;
        informarSenha = false;
        rootOrc = (TreeNode) Web.pegaDaSessao(TreeNode.class);
        autorizacaoUsuarioRH = new AutorizacaoUsuarioRH();
        assinatura = new AssinaturaUsuarioSistema();
        if (rootOrc == null) {
            recuperaArvoreHierarquiaOrganizacionalOrc();
        }
        buscarUnidadesSemFiltro();
        mapHierarquias = new HashMap<>();
        apenasUnidadesVigentes = true;
        descricaoVigenciaHierarquias = new HashMap<>();
    }

    @URLAction(mappingId = "duplicarUsuarioSistema", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void duplicar() {
        editar();
        UsuarioSistema clone = new UsuarioSistema();
        clone.setExpira(selecionado.getExpira());
        clone.setDataExpiracao(selecionado.getDataExpiracao());
        clone.setValidaStatusRh(selecionado.getValidaStatusRh());
        for (GrupoUsuario g : selecionado.getGrupos()) {
            clone.getGrupos().add(g);
        }
        for (GrupoRecursosUsuario gu : selecionado.getGrupoRecursosUsuario()) {
            GrupoRecursosUsuario gru = new GrupoRecursosUsuario();
            gru.setUsuarioSistema(clone);
            gru.setGrupoRecurso(gu.getGrupoRecurso());
            gru.setEscrita(gu.getEscrita());
            gru.setExclusao(gu.getExclusao());
            gru.setLeitura(gu.getLeitura());
            clone.getGrupoRecursosUsuario().add(gru);
        }
        for (RecursosUsuario ru : selecionado.getRecursosUsuario()) {
            RecursosUsuario ru_novo = new RecursosUsuario();
            ru_novo.setUsuarioSistema(clone);
            ru_novo.setRecursoSistema(ru.getRecursoSistema());
            ru_novo.setEscrita(ru.getEscrita());
            ru_novo.setExclusao(ru.getExclusao());
            ru_novo.setLeitura(ru.getLeitura());
            clone.getRecursosUsuario().add(ru_novo);
        }
        for (UsuarioUnidadeOrganizacional uu : selecionado.getUsuarioUnidadeOrganizacional()) {
            UsuarioUnidadeOrganizacional uni = new UsuarioUnidadeOrganizacional();
            uni.setUsuarioSistema(clone);
            uni.setUnidadeOrganizacional(uu.getUnidadeOrganizacional());
            uni.setGestorLicitacao(uu.getGestorLicitacao());
            uni.setGestorMateriais(uu.getGestorMateriais());
            uni.setGestorProtocolo(uu.getGestorProtocolo());
            uni.setGestorPatrimonio(uu.getGestorPatrimonio());
            clone.getUsuarioUnidadeOrganizacional().add(uni);
        }
        for (UsuarioUnidadeOrganizacionalOrcamentaria uo : selecionado.getUsuarioUnidadeOrganizacionalOrc()) {
            UsuarioUnidadeOrganizacionalOrcamentaria uni = new UsuarioUnidadeOrganizacionalOrcamentaria();
            uni.setUsuarioSistema(clone);
            uni.setUnidadeOrganizacional(uo.getUnidadeOrganizacional());
            clone.getUsuarioUnidadeOrganizacionalOrc().add(uni);
        }
        this.selecionado = null;
        this.selecionado = clone;
    }

    @URLAction(mappingId = "editarUsuarioSistema", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        grupoUsuario = new GrupoUsuario();
        grupoRecurso = new GrupoRecurso();
        recursoSistema = new RecursoSistema();
        hierarquiaOrgParticipante = new HierarquiaOrganizacional();
        vigenciaTribUsuario = new VigenciaTribUsuario();
        tipoUsuarioTribUsuario = new TipoUsuarioTribUsuario();
        lotacaoTribUsuario = new LotacaoTribUsuario();
        autorizacaoTributarioUsuario = new AutorizacaoTributarioUsuario();
        setSenha2("");
        setSenhaAntiga("");
        setRepetesenha("");
        arquivoSelecionado = new Arquivo();
        mostraPainel = true;
        rootOrc = (TreeNode) Web.pegaDaSessao(TreeNode.class);
        autorizacaoUsuarioRH = new AutorizacaoUsuarioRH();
        assinatura = new AssinaturaUsuarioSistema();
        if (rootOrc == null) {
            recuperaArvoreHierarquiaOrganizacionalOrc();
        }
        buscarUnidadesSemFiltro();
        mapHierarquias = new HashMap<>();
        apenasUnidadesVigentes = true;
        descricaoVigenciaHierarquias = new HashMap<>();
    }

    private void buscarUnidadesSemFiltro() {
        filtroUnidadeAdministrativa = "";
        popularUnidadesAdministrativasDoUsuario();
        filtroUnidadeOrcamentaria = "";
        popularUnidadesOrcamentariasDoUsuario();
    }

    @URLAction(mappingId = "verUsuarioSistema", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        buscarUnidadesSemFiltro();
        mapHierarquias = new HashMap<>();
    }

    public void cancelarAlteraSenha() {
        UsuarioSistema us = usuarioSistemaFacade.recuperar(getSistemaControlador().getUsuarioCorrente().getId());
        if (us.loginIgualSenha(Seguranca.md5(us.getCpf()))) {
            FacesUtil.redirecionamentoInterno("/logout");
        } else {
            FacesUtil.redirecionamentoInterno("/");
        }

    }

    public void mudaSenha() {
        if ("".equals(senhaAntiga.trim())) {
            FacesUtil.addError("Atenção", "A senha do usuário deve ser informada.");
        } else if ("".equals(senha2.trim())) {
            FacesUtil.addError("Atenção", "A nova senha do usuário deve ser informada.");
        } else if ("".equals(repetesenha.trim())) {
            FacesUtil.addError("Atenção", "A confirmação da nova senha do usuário deve ser informada.");
        } else {
            String s = Seguranca.md5(senhaAntiga);
            if (!senha2.equals(repetesenha)) {
                FacesUtil.addError("Atenção", "A senha não confere com a senha repetida!");
            } else if (getSistemaControlador().getUsuarioCorrente().isAutenticacaoCorreta(s)) {
                selecionado = usuarioSistemaFacade.recuperar(getSistemaControlador().getUsuarioCorrente().getId());

                selecionado.setSenha(PASSWORD_ENCODER.encodePassword(Seguranca.md5(senha2), getSistemaControlador().getUsuarioCorrente().getSalt()));
                this.usuarioSistemaFacade.salvar(selecionado);
                usuarioSistemaFacade.executarDependenciasSalvarUsuario(selecionado);
            } else {
                FacesUtil.addError("Atenção", "A senha atual não confere!");
            }
            if (alteraSenhaViaLogin != null && alteraSenhaViaLogin) {
                alteraSenhaViaLogin = false;
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(UsuarioSistemaAuthenticationSuccessHandler.KEY_ALTERA_SENHA);
            }
        }
    }

    public String getRepetesenha() {
        return repetesenha;
    }

    public void setRepetesenha(String repetesenha) {
        this.repetesenha = repetesenha;
    }

    public String getSenha2() {
        return senha2;
    }

    public void setSenha2(String senha2) {
        this.senha2 = senha2;
    }

    public String getSenhaAntiga() {
        return senhaAntiga;
    }

    public void setSenhaAntiga(String senhaAntiga) {
        this.senhaAntiga = senhaAntiga;
    }

    public GrupoUsuario getGrupoUsuario() {
        return grupoUsuario;
    }

    public void setGrupoUsuario(GrupoUsuario grupoUsuario) {
        this.grupoUsuario = grupoUsuario;
    }

    public GrupoRecurso getGrupoRecurso() {
        return grupoRecurso;
    }

    public void setGrupoRecurso(GrupoRecurso grupoRecurso) {
        this.grupoRecurso = grupoRecurso;
    }

    public RecursoSistema getRecursoSistema() {
        return recursoSistema;
    }

    public void setRecursoSistema(RecursoSistema recursoSistema) {
        this.recursoSistema = recursoSistema;
    }

    public List<UsuarioSistema> getLista() {
        return this.usuarioSistemaFacade.lista();
    }

    public AssinaturaUsuarioSistema getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(AssinaturaUsuarioSistema assinatura) {
        this.assinatura = assinatura;
    }

    public String getNomeUsuarioCorrente() {
        if ("".equals(nomeUsuarioCorrente) || nomeUsuarioCorrente == null) {
            if (getSistemaControlador().getUsuarioCorrente().getPessoaFisica() != null) {
                nomeUsuarioCorrente = getSistemaControlador().getUsuarioCorrente().getPessoaFisica().getNome() + " - " +
                    getSistemaControlador().getUsuarioCorrente().getLogin() + " - " + getSistemaControlador().getUsuarioCorrente().getCpf();
            } else {
                nomeUsuarioCorrente = getSistemaControlador().getUsuarioCorrente().getLogin();
            }
        }
        return nomeUsuarioCorrente;
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            try {
                selecionado.setLogin(selecionado.getLogin().replaceAll("\\s+", ""));
                if (this.selecionado.getId() == null) {
                    if (!"".equals(selecionado.getSenha().trim())) {
                        String senhaInformada = selecionado.getSenha();
                        selecionado.setSenha(PASSWORD_ENCODER.encodePassword(Seguranca.md5(senhaInformada), selecionado.getSalt()));
                    }
                    this.usuarioSistemaFacade.salvarNovo(selecionado);
                } else {
                    selecionado = this.usuarioSistemaFacade.salvarRetornando(selecionado);
                    usuarioSistemaFacade.executarDependenciasSalvarUsuario(selecionado);
                    atualizarUsuarioComAcessoSelecionado();
                }
                FacesUtil.addInfo("", "Salvo com sucesso!");
                if (!this.getSistemaControlador().getUsuarioCorrente().getId().equals(selecionado.getId())) {
                    redireciona();
                }
            } catch (Exception e) {
                FacesUtil.addError("Não foi possível continuar!", "Erro ao salvar " + e.getMessage());
            }
        }
    }

    private void atualizarUsuarioComAcessoSelecionado() {
        UsuarioSistema usuarioSistemaComAcesso = singletonRecursosSistema.getUltimoAcessoUsuario().keySet().stream().filter(usuarioSistema -> usuarioSistema.getId().equals(selecionado.getId())).findFirst().orElse(null);
        if (usuarioSistemaComAcesso != null) {
            usuarioSistemaComAcesso.setTempoMaximoInativoMinutos(selecionado.getTempoMaximoInativoMinutos());
        }
    }

    public void excluirSelecionado() {
        try {
            this.usuarioSistemaFacade.remover(selecionado);
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível continuar!", "Este registro não pode ser excluído porque possui dependências.");
        }
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (usuarioSistemaFacade.jaExisteUsuarioComPessoa(selecionado)) {
            FacesUtil.addError("Atenção", "A Pessoa informada já existe em outro usuário.");
            retorno = false;
        }
        if (usuarioSistemaFacade.jaExisteUsuarioComLogin(selecionado)) {
            FacesUtil.addError("Atenção", "O Login informado já existe em outro usuário.");
            retorno = false;
        }
        if (this.selecionado.getLogin() == null) {
            FacesUtil.addError("Atenção", "Informe o Login do Usuário.");
            retorno = false;
        }
        if (selecionado.getId() == null) {
            if (this.selecionado.getSenha() == null || "".equals(this.selecionado.getSenha().trim())) {
                FacesUtil.addError("Atenção", "Informe a Senha do Usuário.");
                retorno = false;
            }
            if (selecionado.getSenha() != null && !selecionado.getSenha().equals(getSenha2())) {
                FacesUtil.addWarn("Senhas Diferentes!", "Digite a mesma senha nos dois campos!");
                retorno = false;
            }
        }
        if (this.selecionado.getPessoaFisica() == null) {
            FacesUtil.addError("Atenção", "Informe a Pessoa do Usuário.");
            retorno = false;
        }
        if (selecionado.getUsuarioUnidadeOrganizacional().isEmpty()) {
            FacesUtil.addError("Unidade Organizacional!", "Favor Informar pelo menos uma Unidade Organizacional!");
            retorno = false;
        }
        return retorno;
    }

    public void addGrupo() {
        if (grupoUsuario != null) {
            boolean jaExiste = false;
            for (GrupoUsuario gru : selecionado.getGrupos()) {
                if (gru.equals(grupoUsuario)) {
                    jaExiste = true;
                }
            }
            if (!jaExiste) {
                selecionado.getGrupos().add(grupoUsuario);
                grupoUsuario = new GrupoUsuario();
            } else {
                FacesUtil.addError("Atenção!", "O grupo de usuário já foi adicionado, informe outro.");
            }
        } else {
            FacesUtil.addError("Atenção!", "Selecione um grupo de usuário para adicionar.");
        }
    }

    public void addGrupoRecurso() {
        if (grupoRecurso != null) {
            boolean erro = false;
            for (GrupoRecursosUsuario g : selecionado.getGrupoRecursosUsuario()) {
                if (g.getGrupoRecurso().equals(grupoRecurso)) {
                    erro = true;
                }
            }
            if (erro) {
                FacesUtil.addError("Atenção", "Grupos de Recurso ja existe!");
            } else {
                GrupoRecursosUsuario gru = new GrupoRecursosUsuario();
                gru.setGrupoRecurso(grupoRecurso);
                gru.setUsuarioSistema(selecionado);
                gru.setLeitura(true);
                gru.setEscrita(true);
                gru.setExclusao(true);
                selecionado.getGrupoRecursosUsuario().add(gru);
                grupoRecurso = new GrupoRecurso();
            }
        } else {
            FacesUtil.addError("Atenção!", "Selecione um grupo de recurso para adicionar.");
        }
    }

    public void addRecursoSistema() {
        if (recursoSistema != null) {
            boolean jaExiste = false;
            for (RecursosUsuario recursosUsuario : selecionado.getRecursosUsuario()) {
                if (recursosUsuario.getRecursoSistema().equals(recursoSistema)) {
                    jaExiste = true;
                }
            }
            if (!jaExiste) {
                RecursosUsuario rec = new RecursosUsuario();
                rec.setRecursoSistema(recursoSistema);
                rec.setUsuarioSistema(selecionado);
                rec.setLeitura(true);
                rec.setEscrita(true);
                rec.setExclusao(true);
                selecionado.getRecursosUsuario().add(rec);
                recursoSistema = new RecursoSistema();
            } else {
                FacesUtil.addError("Ja Existe!", "Este Recurso de Sistema ja foi incluído!");
            }
        }
    }

    public void removeGrupo(GrupoUsuario grupo) {
        selecionado.getGrupos().remove(grupo);
    }

    public void removeGrupoRecurso(GrupoRecursosUsuario gru) {
        selecionado.getGrupoRecursosUsuario().remove(gru);
    }

    public void removeRecursoSistema(RecursosUsuario rec) {
        selecionado.getRecursosUsuario().remove(rec);
    }

    public void removerHierarquiaAdministrativa(HierarquiaUnidadeUsuario hierarquiaAdministrativa) {
        hierarquiasAdministrativasUsuario.remove(hierarquiaAdministrativa);
        hierarquiasAdministrativasUsuarioPesquisa.remove(hierarquiaAdministrativa);
        removeUnidadeOrganizacional(hierarquiaAdministrativa.getUsuarioUnidadeOrganizacional());
    }

    public void removerHierarquiaOrcamentaria(HierarquiaUnidadeUsuario hierarquiaOrcamentaria) {
        hierarquiasOrcamentariasUsuario.remove(hierarquiaOrcamentaria);
        hierarquiasOrcamentariasUsuarioPesquisa.remove(hierarquiaOrcamentaria);
        removeUnidadeOrganizacionalOrc(hierarquiaOrcamentaria.getUsuarioUnidadeOrganizacionalOrcamentaria());
    }

    public void removeUnidadeOrganizacional(UsuarioUnidadeOrganizacional uo) {
        selecionado.getUsuarioUnidadeOrganizacional().remove(uo);
        HierarquiaOrganizacional hie;

        if (mapHierarquias.containsKey(uo.getUnidadeOrganizacional().getId())) {
            hie = mapHierarquias.get(uo.getUnidadeOrganizacional().getId());
        } else {
            hie = usuarioSistemaFacade.getHierarquiaOrganizacionalFacade().recuperaHierarquiaOrganizacionalPelaUnidade(uo.getUnidadeOrganizacional().getId());
            mapHierarquias.put(uo.getUnidadeOrganizacional().getId(), hie);
        }
        hierarquias.remove(hie);

        if (hierarquias.isEmpty()) {
            selecionado.setUsuarioUnidadeOrganizacionalOrc(new ArrayList<UsuarioUnidadeOrganizacionalOrcamentaria>());
        } else {
            List<UsuarioUnidadeOrganizacionalOrcamentaria> lista = new ArrayList<>();
            Iterator<HierarquiaOrganizacionalResponsavel> iterator = hie.getHierarquiaOrganizacionalResponsavels().iterator();
            while (iterator.hasNext()) {
                HierarquiaOrganizacionalResponsavel hieR = iterator.next();
                HierarquiaOrganizacional hierarquia = usuarioSistemaFacade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade("ORCAMENTARIA", hieR.getHierarquiaOrganizacionalOrc().getSubordinada(), getSistemaControlador().getDataOperacao());
                if (hierarquia != null) {
                    if (percorreHierarquiaResponsavelParaRemoverOrcamentarias(hierarquia, selecionado.getUsuarioUnidadeOrganizacional())) {
                        lista = removeHierarquiaOrcamentariaVinculadaComAdministrativaDoUsuario(hieR, selecionado.getUsuarioUnidadeOrganizacionalOrc(), lista);
                    }
                }
            }
            selecionado.getUsuarioUnidadeOrganizacionalOrc().removeAll(lista);
        }
    }

    private Boolean percorreHierarquiaResponsavelParaRemoverOrcamentarias(HierarquiaOrganizacional hor, List<UsuarioUnidadeOrganizacional> hieResponsavel) {
        for (UsuarioUnidadeOrganizacional undResponsavel : hieResponsavel) {
            HierarquiaOrganizacional hie;
            if (mapHierarquias.containsKey(undResponsavel.getUnidadeOrganizacional().getId())) {
                hie = mapHierarquias.get(undResponsavel.getUnidadeOrganizacional().getId());
            } else {
                hie = usuarioSistemaFacade.getHierarquiaOrganizacionalFacade().recuperaHierarquiaOrganizacionalPelaUnidade(undResponsavel.getUnidadeOrganizacional().getId());
                mapHierarquias.put(undResponsavel.getUnidadeOrganizacional().getId(), hie);
            }

            if (hie.getId() != null) {
                for (HierarquiaOrganizacionalResponsavel hieResp : hie.getHierarquiaOrganizacionalResponsavels()) {
                    if (hor.getSubordinada().equals(hieResp.getHierarquiaOrganizacionalOrc().getSubordinada())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public HierarquiaOrganizacional buscarHierarquiaAdministrativaVigente(UnidadeOrganizacional unidadeOrganizacional) {
        return usuarioSistemaFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalVigentePorUnidade(unidadeOrganizacional, TipoHierarquiaOrganizacional.ADMINISTRATIVA, usuarioSistemaFacade.getSistemaFacade().getDataOperacao());
    }

    public HierarquiaOrganizacional buscarHierarquiaOrcamentariaVigente(UnidadeOrganizacional unidadeOrganizacional) {
        return usuarioSistemaFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalVigentePorUnidade(unidadeOrganizacional, TipoHierarquiaOrganizacional.ORCAMENTARIA, usuarioSistemaFacade.getSistemaFacade().getDataOperacao());
    }

    public void popularUnidadesAdministrativasDoUsuario() {
        hierarquiasAdministrativasUsuario = Lists.newArrayList();
        hierarquiasAdministrativasUsuarioPesquisa = Lists.newArrayList();
        for (UsuarioUnidadeOrganizacional usuarioUnidadeOrganizacional : selecionado.getUsuarioUnidadeOrganizacional()) {
            HierarquiaUnidadeUsuario hierarquiaUnidadeUsuario = new HierarquiaUnidadeUsuario(usuarioUnidadeOrganizacional);
            if (hierarquiaUnidadeUsuario.getHierarquiaOrganizacional() != null) {
                hierarquiasAdministrativasUsuario.add(hierarquiaUnidadeUsuario);
                hierarquiasAdministrativasUsuarioPesquisa.add(hierarquiaUnidadeUsuario);
            }
        }
        ordenarHierarquiasPorCodigo(hierarquiasAdministrativasUsuarioPesquisa);
    }

    public void popularUnidadesOrcamentariasDoUsuario() {
        hierarquiasOrcamentariasUsuario = Lists.newArrayList();
        hierarquiasOrcamentariasUsuarioPesquisa = Lists.newArrayList();
        for (UsuarioUnidadeOrganizacionalOrcamentaria usuarioUnidadeOrganizacionalOrcamentaria : selecionado.getUsuarioUnidadeOrganizacionalOrc()) {
            HierarquiaUnidadeUsuario hierarquiaUnidadeUsuario = new HierarquiaUnidadeUsuario(usuarioUnidadeOrganizacionalOrcamentaria);
            if (hierarquiaUnidadeUsuario.getHierarquiaOrganizacional() != null) {
                hierarquiasOrcamentariasUsuario.add(hierarquiaUnidadeUsuario);
                hierarquiasOrcamentariasUsuarioPesquisa.add(hierarquiaUnidadeUsuario);
            }
        }
        ordenarHierarquiasPorCodigo(hierarquiasOrcamentariasUsuarioPesquisa);
    }

    public void filtrarUnidadeOrcamentariaUsuario() {
        hierarquiasOrcamentariasUsuarioPesquisa = Lists.newArrayList();
        filtrarUnidadesDoUsuario(hierarquiasOrcamentariasUsuarioPesquisa, hierarquiasOrcamentariasUsuario, filtroUnidadeOrcamentaria);
    }

    public void filtrarUnidadeAdministrativaUsuario() {
        hierarquiasAdministrativasUsuarioPesquisa = Lists.newArrayList();
        filtrarUnidadesDoUsuario(hierarquiasAdministrativasUsuarioPesquisa, hierarquiasAdministrativasUsuario, filtroUnidadeAdministrativa);
    }

    private void filtrarUnidadesDoUsuario(List<HierarquiaUnidadeUsuario> hierarquiasPesquisa, List<HierarquiaUnidadeUsuario> hierarquiasDoUsuario, String filtro) {
        for (HierarquiaUnidadeUsuario hierarquiaUnidadeUsuario : hierarquiasDoUsuario) {
            if (hierarquiaUnidadeUsuario.getHierarquiaOrganizacional() != null &&
                (hierarquiaUnidadeUsuario.getHierarquiaOrganizacional().getDescricao().toLowerCase().contains(filtro.trim().toLowerCase()) ||
                    hierarquiaUnidadeUsuario.getHierarquiaOrganizacional().getCodigo().contains(filtro.trim()))) {
                hierarquiasPesquisa.add(hierarquiaUnidadeUsuario);
            }
        }
        ordenarHierarquiasPorCodigo(hierarquiasPesquisa);
    }

    private void ordenarHierarquiasPorCodigo(List<HierarquiaUnidadeUsuario> hierarquias) {
        Collections.sort(hierarquias, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                HierarquiaUnidadeUsuario s1 = (HierarquiaUnidadeUsuario) o1;
                HierarquiaUnidadeUsuario s2 = (HierarquiaUnidadeUsuario) o2;

                if (s1.getHierarquiaOrganizacional() != null && s2.getHierarquiaOrganizacional() != null) {
                    return s1.getHierarquiaOrganizacional().getCodigo().compareTo(s2.getHierarquiaOrganizacional().getCodigo());
                } else {
                    return 0;
                }
            }
        });
    }

    public List<HierarquiaUnidadeUsuario> getHierarquiasAdministrativasUsuarioPesquisa() {
        return hierarquiasAdministrativasUsuarioPesquisa;
    }

    public void setHierarquiasAdministrativasUsuarioPesquisa(List<HierarquiaUnidadeUsuario> hierarquiasAdministrativasUsuarioPesquisa) {
        this.hierarquiasAdministrativasUsuarioPesquisa = hierarquiasAdministrativasUsuarioPesquisa;
    }

    public String getFiltroUnidadeOrcamentaria() {
        return filtroUnidadeOrcamentaria;
    }

    public void setFiltroUnidadeOrcamentaria(String filtroUnidadeOrcamentaria) {
        this.filtroUnidadeOrcamentaria = filtroUnidadeOrcamentaria;
    }

    public List<HierarquiaUnidadeUsuario> getHierarquiasOrcamentariasUsuario() {
        return hierarquiasOrcamentariasUsuario;
    }

    public void setHierarquiasOrcamentariasUsuario(List<HierarquiaUnidadeUsuario> hierarquiasOrcamentariasUsuario) {
        this.hierarquiasOrcamentariasUsuario = hierarquiasOrcamentariasUsuario;
    }

    public List<HierarquiaUnidadeUsuario> getHierarquiasOrcamentariasUsuarioPesquisa() {
        return hierarquiasOrcamentariasUsuarioPesquisa;
    }

    public void setHierarquiasOrcamentariasUsuarioPesquisa(List<HierarquiaUnidadeUsuario> hierarquiasOrcamentariasUsuarioPesquisa) {
        this.hierarquiasOrcamentariasUsuarioPesquisa = hierarquiasOrcamentariasUsuarioPesquisa;
    }

    private List<UsuarioUnidadeOrganizacionalOrcamentaria> removeHierarquiaOrcamentariaVinculadaComAdministrativaDoUsuario(HierarquiaOrganizacionalResponsavel hor, List<UsuarioUnidadeOrganizacionalOrcamentaria> lista, List<UsuarioUnidadeOrganizacionalOrcamentaria> listaAux) {
        for (UsuarioUnidadeOrganizacionalOrcamentaria uuoo : lista) {
            if (uuoo.getUnidadeOrganizacional().equals(hor.getHierarquiaOrganizacionalOrc().getSubordinada())) {
                listaAux.add(uuoo);
                return listaAux;
            }
        }
        return listaAux;
    }

    public void removeUnidadeOrganizacionalOrc(UsuarioUnidadeOrganizacionalOrcamentaria uo) {
        selecionado.getUsuarioUnidadeOrganizacionalOrc().remove(uo);
    }

    public List<GrupoUsuario> completaGrupoUsuario(String parte) {
        return usuarioSistemaFacade.getGrupoUsuarioFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<GrupoRecurso> completaGrupoRecurso(String parte) {
        return usuarioSistemaFacade.getGrupoRecursoFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<RecursoSistema> completaRecursoSistema(String parte) {
        return usuarioSistemaFacade.getRecursoSistemaFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        List<PessoaFisica> listaPes = usuarioSistemaFacade.getPessoaFisicaFacade().listaFiltrandoPorTipoPerfil(parte, PerfilEnum.PERFIL_RH);
        listaPes.addAll(usuarioSistemaFacade.getPessoaFisicaFacade().listaFiltrandoPorTipoPerfil(parte, PerfilEnum.PERFIL_ESPECIAL));
        return listaPes;
    }

    public List<UsuarioSistema> completaUsuarioPeloLogin(String parte) {
        return usuarioSistemaFacade.listaFiltrando(parte.trim(), "login");
    }

    public void repeteSenha() {
        setSenha2(selecionado.getSenha());
    }

    public List<SelectItem> getPessoaFisica() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (PessoaFisica object : usuarioSistemaFacade.getPessoaFisicaFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public List<SelectItem> getGrupos() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (GrupoUsuario object : usuarioSistemaFacade.getGrupoUsuarioFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    private void validarHierarquiaOrganizacionalParticipante() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrgParticipante == null || hierarquiaOrgParticipante.getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione uma unidade organizacional para adicionar.");
        }
        ve.lancarException();
    }


    private void validarHierarquiasResponsaveis(HierarquiaOrganizacional hierarquiaAdm) {
        ValidacaoException ve = new ValidacaoException();
        List<HierarquiaOrganizacionalResponsavel> hierarquiaOrganizacionalOrcamentaria = hierarquiaAdm.getHierarquiaOrganizacionalResponsavels();
        if (hierarquiaOrganizacionalOrcamentaria.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A unidade " + hierarquiaAdm.getSubordinada() + " não possui vinculo com uma unidade Orçamentária.");
        } else {
            for (HierarquiaOrganizacionalResponsavel hie : hierarquiaOrganizacionalOrcamentaria) {
                usuarioSistemaFacade.getHierarquiaOrganizacionalFacade().validaVigenciaHIerarquiaAdministrativaOrcamentaria(hierarquiaAdm.getSubordinada(), hie.getHierarquiaOrganizacionalOrc().getSubordinada(), getSistemaControlador().getDataOperacao());
            }
        }
        for (UsuarioUnidadeOrganizacional uuo : selecionado.getUsuarioUnidadeOrganizacional()) {
            if (uuo.getUnidadeOrganizacional().equals(hierarquiaAdm.getSubordinada())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A unidade " + hierarquiaAdm.getSubordinada() + " já está adicionada.");
            }
        }
        ve.lancarException();
    }

    private Boolean validarHierarquiasResponsaveis(HierarquiaOrganizacional hierarquiaAdm, ValidacaoException ve) {
        for (UsuarioUnidadeOrganizacional uuo : selecionado.getUsuarioUnidadeOrganizacional()) {
            if (uuo.getUnidadeOrganizacional().equals(hierarquiaAdm.getSubordinada())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A unidade " + hierarquiaAdm.getSubordinada() + " já está adicionada.");
                return true;
            }
        }
        return false;
    }

    public void validarHierarquiaAdministrativa() {
        try {
            validarHierarquiaOrganizacionalParticipante();
            if (hasFilhasParaMarcarPelaHierarquia(hierarquiaOrgParticipante)) {
                FacesUtil.executaJavaScript("unidades.show()");
            } else {
                salvarParticipante(false);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void salvarParticipante(Boolean adicionarHierarquiasFilhas) {
        try {
            validarHierarquiaOrganizacionalParticipante();
            hierarquiaOrgParticipante = usuarioSistemaFacade.getHierarquiaOrganizacionalFacade().recuperar(hierarquiaOrgParticipante);
            validarHierarquiasResponsaveis(hierarquiaOrgParticipante);
            adicionarHierarquiaParticipante(hierarquiaOrgParticipante);
            if (adicionarHierarquiasFilhas) {
                List<HierarquiaOrganizacional> filhasDaHierarquiaSelecionada = usuarioSistemaFacade.getHierarquiaOrganizacionalFacade().buscarHierarquiasFilhasDeUmaHierarquiaOrganizacionalAdministrativaVigenteOuNaoVigente(hierarquiaOrgParticipante, getSistemaControlador().getDataOperacao(), apenasUnidadesVigentes);
                for (HierarquiaOrganizacional hierarquiaFilha : filhasDaHierarquiaSelecionada) {
                    adicionarHierarquiaParticipante(hierarquiaFilha);
                }
            }
            hierarquias = Util.adicionarObjetoEmLista(hierarquias, hierarquiaOrgParticipante);
            hierarquiaOrgParticipante = new HierarquiaOrganizacional();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
        filtroUnidadeAdministrativa = "";
        filtrarUnidadeAdministrativaUsuario();
        filtroUnidadeOrcamentaria = "";
        filtrarUnidadeOrcamentariaUsuario();
    }

    public void salvarTodosParticipantes() {
        try {
            hierarquias = usuarioSistemaFacade.getHierarquiaOrganizacionalFacade().buscarHierarquiaAdministrativaComResponsavelVigente(getSistemaControlador().getDataOperacao());
            ValidacaoException ve = new ValidacaoException();
            for (HierarquiaOrganizacional hierarquiaAdministrativa : hierarquias) {
                hierarquiaAdministrativa = usuarioSistemaFacade.getHierarquiaOrganizacionalFacade().recuperar(hierarquiaAdministrativa);
                if (!validarHierarquiasResponsaveis(hierarquiaAdministrativa, ve)) {
                    adicionarHierarquiaParticipante(hierarquiaAdministrativa);
                    Util.adicionarObjetoEmLista(hierarquias, hierarquiaAdministrativa);
                }
            }
            if (ve.temMensagens()) {
                for (FacesMessage msg : ve.getMensagens()) {
                    FacesUtil.addOperacaoNaoPermitida(msg.getDetail());
                }
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
        filtroUnidadeAdministrativa = "";
        filtrarUnidadeAdministrativaUsuario();
        filtroUnidadeOrcamentaria = "";
        filtrarUnidadeOrcamentariaUsuario();
    }

    private void adicionarHierarquiaParticipante(HierarquiaOrganizacional hierarquiaOrganizacional) {
        UsuarioUnidadeOrganizacional uu = new UsuarioUnidadeOrganizacional();
        uu.setUsuarioSistema(selecionado);
        uu.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        uu.setGestorProtocolo(Boolean.FALSE);
        uu.setGestorLicitacao(Boolean.FALSE);
        uu.setGestorMateriais(Boolean.FALSE);
        uu.setGestorPatrimonio(Boolean.FALSE);
        if (!hasUnidadeAdministrativaAdicionada(uu)) {
            Util.adicionarObjetoEmLista(selecionado.getUsuarioUnidadeOrganizacional(), uu);
            Util.adicionarObjetoEmLista(hierarquiasAdministrativasUsuario, new HierarquiaUnidadeUsuario(uu));
            hierarquiaOrganizacional = usuarioSistemaFacade.getHierarquiaOrganizacionalFacade().recuperar(hierarquiaOrganizacional);
            for (HierarquiaOrganizacionalResponsavel hie : hierarquiaOrganizacional.getHierarquiaOrganizacionalResponsavels()) {
                UsuarioUnidadeOrganizacionalOrcamentaria uuo = new UsuarioUnidadeOrganizacionalOrcamentaria();
                uuo.setUsuarioSistema(selecionado);
                uuo.setUnidadeOrganizacional(hie.getHierarquiaOrganizacionalOrc().getSubordinada());
                if (!hasUnidadeOrcamentariaAdicionada(uuo)) {
                    Util.adicionarObjetoEmLista(selecionado.getUsuarioUnidadeOrganizacionalOrc(), uuo);
                    Util.adicionarObjetoEmLista(hierarquiasOrcamentariasUsuario, new HierarquiaUnidadeUsuario(uuo));
                    Util.adicionarObjetoEmLista(hierarquiasOrc, hie.getHierarquiaOrganizacionalOrc());
                }
            }
        }
    }

    private Boolean hasUnidadeAdministrativaAdicionada(UsuarioUnidadeOrganizacional usuarioUnidadeOrganizacional) {
        for (UsuarioUnidadeOrganizacional uuo : selecionado.getUsuarioUnidadeOrganizacional()) {
            if (uuo.getUnidadeOrganizacional().equals(usuarioUnidadeOrganizacional.getUnidadeOrganizacional())) {
                return true;
            }
        }
        return false;
    }

    private Boolean hasUnidadeOrcamentariaAdicionada(UsuarioUnidadeOrganizacionalOrcamentaria uuo) {
        for (UsuarioUnidadeOrganizacionalOrcamentaria uuoo : selecionado.getUsuarioUnidadeOrganizacionalOrc()) {
            if (uuoo.getUnidadeOrganizacional().equals(uuo.getUnidadeOrganizacional())) {
                return true;
            }
        }
        return false;
    }

    public void salvarOrgOrc() {
        if (hierarquiaOrgOrc != null) {

            try {
                usuarioSistemaFacade.getHierarquiaOrganizacionalFacade().validaVigenciaHIerarquiaAdministrativaOrcamentaria(hierarquiaOrgOrc.getSubordinada(), hierarquiaOrgOrc.getSubordinada(), getSistemaControlador().getDataOperacao());
            } catch (ExcecaoNegocioGenerica e) {
                FacesUtil.addError("Erro ao adicionar!", e.getMessage());
                return;
            }

            if (hierarquiaOrgOrc.getSubordinada() != null) {
                UsuarioUnidadeOrganizacionalOrcamentaria uu = new UsuarioUnidadeOrganizacionalOrcamentaria();
                uu.setUsuarioSistema(selecionado);
                uu.setUnidadeOrganizacional(hierarquiaOrgOrc.getSubordinada());
                Boolean podeAdd = true;
                for (UsuarioUnidadeOrganizacionalOrcamentaria usuOrc : selecionado.getUsuarioUnidadeOrganizacionalOrc()) {
                    if (usuOrc.getUnidadeOrganizacional().equals(uu.getUnidadeOrganizacional()) && usuOrc.getUsuarioSistema().equals(uu.getUsuarioSistema())) {
                        podeAdd = false;
                        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "A Unidade Organizacional Orçamentária selecionada já existe na lista!");
                        break;
                    }
                }
                if (podeAdd) {
                    selecionado.getUsuarioUnidadeOrganizacionalOrc().add(uu);
                    hierarquiasOrc = Util.adicionarObjetoEmLista(hierarquiasOrc, hierarquiaOrgOrc);
                }
            }


            hierarquiaOrgOrc = null;
        } else {
            FacesUtil.addError("Atenção!", "Selecione uma unidade organizacional para adicionar.");
        }
    }

    public void removerTodosParticipantes() {
        selecionado.setUsuarioUnidadeOrganizacional(new ArrayList<UsuarioUnidadeOrganizacional>());
        selecionado.setUsuarioUnidadeOrganizacionalOrc(new ArrayList<UsuarioUnidadeOrganizacionalOrcamentaria>());
        hierarquias = Lists.newArrayList();
        hierarquiasOrc = Lists.newArrayList();
        hierarquiasAdministrativasUsuarioPesquisa = Lists.newArrayList();
        hierarquiasOrcamentariasUsuarioPesquisa = Lists.newArrayList();
        hierarquiasOrcamentariasUsuario = Lists.newArrayList();
        hierarquiasAdministrativasUsuario = Lists.newArrayList();
    }

    public List<UsuarioUnidadeOrganizacional> getListaUsuarioUnidadesOrg() {
        return selecionado.getUsuarioUnidadeOrganizacional();
    }

    public void alterarSenha() {
        senhaAntiga = "";
        senha2 = "";
        repetesenha = "";
    }

    public void handleFileUpload(FileUploadEvent event) {
        file = event.getFile();
        arquivoSelecionado.setDescricao(file.getFileName());
        adicionarArquivo();
    }

    public void adicionarArquivo() {
        ArquivoUsuarioSistema arquivoUsuarioSistema = new ArquivoUsuarioSistema();
        arquivoUsuarioSistema.setArquivo(arquivoSelecionado);
        arquivoUsuarioSistema.setUsuarioSistema(selecionado);
        arquivoUsuarioSistema.setFile((Object) file);
        arquivoSelecionado.setMimeType(file.getContentType());
        arquivoSelecionado.setNome(file.getFileName());
        selecionado.getArquivoUsuarioSistemas().add(arquivoUsuarioSistema);
        arquivoSelecionado = new Arquivo();
    }

    public void removeArquivo(ArquivoUsuarioSistema arquivoUsuarioSistema) {
        if (arquivoUsuarioSistema.getId() != null) {
            arquivoUsuarioSistema.setExcluido(true);
        } else {
            selecionado.getArquivoUsuarioSistemas().remove(arquivoUsuarioSistema);
        }
    }

    public Arquivo getArquivoSelecionado() {
        return arquivoSelecionado;
    }

    public void setArquivoSelecionado(Arquivo arquivoSelecionado) {
        this.arquivoSelecionado = arquivoSelecionado;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<ArquivoUsuarioSistema> getArquivos() {
        List<ArquivoUsuarioSistema> lista = new ArrayList<>();

        for (ArquivoUsuarioSistema a : selecionado.getArquivoUsuarioSistemas()) {
            if (!a.getExcluido()) {
                lista.add(a);
            }
        }

        return lista;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public StreamedContent getArquivoStream(ArquivoUsuarioSistema arquivoUsuarioSistema) throws IOException {
        UploadedFile download = (UploadedFile) arquivoUsuarioSistema.getFile();
        return new DefaultStreamedContent(download.getInputstream(), download.getContentType(), download.getFileName());
    }

    public void limpaCampos() {
        senhaAntiga = "";
        senha2 = "";
        repetesenha = "";
    }

    public List<SelectItem> getGruposRecurso() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (GrupoRecurso object : usuarioSistemaFacade.getGrupoRecursoFacade().listaOrdenado()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposUsuarioTributario() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoUsuarioTributario object : TipoUsuarioTributario.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getLotacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (LotacaoVistoriadora object : usuarioSistemaFacade.getLotacaoVistoriadoraFacade().listaOrdenado()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getAutorizacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Selecione uma autorização."));
        for (AutorizacaoTributario object : AutorizacaoTributario.values()) {
            if (object.getLiberado()) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getAutorizacoesRH() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Selecione uma autorização."));
        for (TipoAutorizacaoRH object : TipoAutorizacaoRH.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public void addTipoUsuario() {
        if (tipoUsuarioTribUsuario != null && tipoUsuarioTribUsuario.getTipoUsuarioTributario() != null) {
            Boolean jaExiste = false;
            for (TipoUsuarioTribUsuario tipo : vigenciaTribUsuario.getTipoUsuarioTribUsuarios()) {
                if (tipo.getTipoUsuarioTributario().equals(tipoUsuarioTribUsuario.getTipoUsuarioTributario())) {
                    jaExiste = true;
                }
            }
            if (!jaExiste) {
                tipoUsuarioTribUsuario.setVigenciaTribUsuario(vigenciaTribUsuario);
                vigenciaTribUsuario.getTipoUsuarioTribUsuarios().add(tipoUsuarioTribUsuario);
                tipoUsuarioTribUsuario = new TipoUsuarioTribUsuario();
            } else {
                FacesUtil.addWarn("Atenção", "Tipo de Usuário já existe!");
            }
        } else {
            FacesUtil.addError("Atenção!", "Selecione um tipo de usuário para adicionar.");
        }
    }

    public void removeTipoUsuario(TipoUsuarioTribUsuario tipo) {
        if (tipo != null) {
            vigenciaTribUsuario.getTipoUsuarioTribUsuarios().remove(tipo);
        }
    }

    public void addLotacao() {
        if (lotacaoTribUsuario != null && lotacaoTribUsuario.getLotacao() != null) {
            Boolean jaExiste = false;
            for (LotacaoTribUsuario lot : vigenciaTribUsuario.getLotacaoTribUsuarios()) {
                if (lot.getLotacao().equals(lotacaoTribUsuario.getLotacao())) {
                    jaExiste = true;
                }
            }
            if (!jaExiste) {
                lotacaoTribUsuario.setVigenciaTribUsuario(vigenciaTribUsuario);
                vigenciaTribUsuario.getLotacaoTribUsuarios().add(lotacaoTribUsuario);
                lotacaoTribUsuario = new LotacaoTribUsuario();
            } else {
                FacesUtil.addWarn("Atenção", "A lotação já existe!");
            }
        } else {
            FacesUtil.addError("Atenção!", "Selecione uma lotação para adicionar.");
        }
    }

    public void removeLotacao(LotacaoTribUsuario lotacao) {
        if (lotacao != null) {
            vigenciaTribUsuario.getLotacaoTribUsuarios().remove(lotacao);
        }
    }

    private void validarAutorizacaoRH() {
        ValidacaoException ve = new ValidacaoException();
        if (autorizacaoUsuarioRH.getTipoAutorizacaoRH() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo de Autorização do RH.");
        }
        if (autorizacaoUsuarioRH.getTipoAutorizacaoRH() != null) {
            for (AutorizacaoUsuarioRH autorizacao : selecionado.getAutorizacaoUsuarioRH()) {
                if (autorizacaoUsuarioRH.getTipoAutorizacaoRH().equals(autorizacao.getTipoAutorizacaoRH())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Essa aturorização já foi adicionada.");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    public void adicionarAutorizacaoRH() {
        try {
            validarAutorizacaoRH();
            autorizacaoUsuarioRH.setUsuarioSistema(selecionado);
            selecionado.getAutorizacaoUsuarioRH().add(autorizacaoUsuarioRH);
            autorizacaoUsuarioRH = new AutorizacaoUsuarioRH();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerAutorizacaoRH(AutorizacaoUsuarioRH autorizacao) {
        selecionado.getAutorizacaoUsuarioRH().remove(autorizacao);
    }

    public void addAutorizacao() {
        if (autorizacaoTributarioUsuario != null && autorizacaoTributarioUsuario.getAutorizacao() != null) {
            Boolean jaExiste = false;
            for (AutorizacaoTributarioUsuario aut : vigenciaTribUsuario.getAutorizacaoTributarioUsuarios()) {
                if (aut.getAutorizacao().equals(autorizacaoTributarioUsuario.getAutorizacao())) {
                    jaExiste = true;
                }
            }
            if (!jaExiste) {
                autorizacaoTributarioUsuario.setVigenciaTribUsuario(vigenciaTribUsuario);
                vigenciaTribUsuario.getAutorizacaoTributarioUsuarios().add(autorizacaoTributarioUsuario);
                autorizacaoTributarioUsuario = new AutorizacaoTributarioUsuario();
            } else {
                FacesUtil.addWarn("Atenção", "A autorização já existe!");
            }
        } else {
            FacesUtil.addError("Atenção!", "Selecione uma autorização para adicionar.");
        }
    }

    public void removeAutorizacao(AutorizacaoTributarioUsuario autorizacao) {
        if (autorizacao != null) {
            vigenciaTribUsuario.getAutorizacaoTributarioUsuarios().remove(autorizacao);
        }
    }

    public void addVigencia() {
        if (!vigenciaTribUsuario.getTipoUsuarioTribUsuarios().isEmpty()) {
            if (vigenciaTribUsuario != null) {
                for (VigenciaTribUsuario v : selecionado.getVigenciaTribUsuarios()) {
                    if (v.getVigenciaFinal() == null) {
                        v.setVigenciaFinal(getSistemaControlador().getDataOperacao());
                    }
                }
                vigenciaTribUsuario.setUsuarioSistema(selecionado);
                if (vigenciaTribUsuario.getVigenciaInicial() == null) {
                    vigenciaTribUsuario.setVigenciaInicial(getSistemaControlador().getDataOperacao());
                }
                vigenciaTribUsuario.setVigenciaFinal(null);
                Util.adicionarObjetoEmLista(selecionado.getVigenciaTribUsuarios(), vigenciaTribUsuario);
                vigenciaTribUsuario = null;
            }
        } else {
            FacesUtil.addWarn("Atenção", "Informe pelo menos 1 tipo de usuário!");
        }
    }

    public void removerVigencia(VigenciaTribUsuario vigencia) {
        if (vigencia != null) {
            selecionado.getVigenciaTribUsuarios().remove(vigencia);
        }
    }

    public void editarVigencia(VigenciaTribUsuario vigencia) {
        if (vigencia != null) {
            this.vigenciaTribUsuario = (VigenciaTribUsuario) Util.clonarObjeto(vigencia);
        }
    }

    public List<TipoUsuarioTribUsuario> listaTipoUsuarioTribUsuario(VigenciaTribUsuario vigencia) {
        return usuarioSistemaFacade.listaTipoUsuarioTribUsuario(vigencia);
    }

    public List<LotacaoTribUsuario> listaLotacaoTribUsuario(VigenciaTribUsuario vigencia) {
        return usuarioSistemaFacade.listaLotacaoTribUsuario(vigencia);
    }

    public String perfilPessoaSelecionada() {
        if (selecionado.getPessoaFisica() != null) {
            PessoaFisica pes = usuarioSistemaFacade.getPessoaFisicaFacade().recuperar(selecionado.getPessoaFisica().getId());
            for (PerfilEnum p : pes.getPerfis()) {
                if (p == PerfilEnum.PERFIL_ESPECIAL) {
                    return "Perfil Especial";
                }
            }
            return "Perfil Recursos Humanos";
        }
        return "";
    }

    public void setaPessoa(SelectEvent evt) {
        PessoaFisica p = (PessoaFisica) evt.getObject();
        selecionado.setPessoaFisica(p);
        mostraPainel = true;
        setaLoginNaSenha();
    }

    public Boolean getMostraPainel() {
        return mostraPainel;
    }

    public void setMostraPainel(Boolean mostraPainel) {
        this.mostraPainel = mostraPainel;
    }

    public Boolean getInformarSenha() {
        return informarSenha;
    }

    public void setInformarSenha(Boolean informarSenha) {
        this.informarSenha = informarSenha;
    }

    public void mudaInformarSenha() {
        informarSenha = true;
    }

    public void redefinirSenha() {
        selecionado.setSenha(PASSWORD_ENCODER.encodePassword(Seguranca.md5(selecionado.getCpf()), selecionado.getSalt()));
        FacesUtil.addInfo("Alteração efetuada com sucesso!", "A senha do usuário " + selecionado.getNome() + " foi redefina para a senha padrão, a mesma deve ser alterada no primeiro acesso.");
        if (selecionado.getPessoaFisica() != null) {
            usuarioSistemaFacade.getPessoaFisicaFacade().getPessoaFacade().enviarEmailParaPessoa(selecionado.getPessoaFisica(), montaEmailTrocaSenha(), "WebPublico - Comunicado de troca de senha");
        }
        this.usuarioSistemaFacade.salvar(selecionado);
        usuarioSistemaFacade.executarDependenciasSalvarUsuario(selecionado);
        if (selecionado.getId().equals(usuarioSistemaFacade.getSistemaControlador().getUsuarioCorrente().getId())) {
            FacesUtil.redirecionamentoInterno("/logout");
        } else {
            redireciona();
        }
    }

    public boolean getRedefineSenha() {
        if (alteraSenhaViaLogin == null) {
            alteraSenhaViaLogin = false;
        }
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        if (sessionMap != null) {
            if (sessionMap.get(UsuarioSistemaAuthenticationSuccessHandler.KEY_ALTERA_SENHA) != null) {
                alteraSenhaViaLogin = (Boolean) sessionMap.get(UsuarioSistemaAuthenticationSuccessHandler.KEY_ALTERA_SENHA);
            }
        }
        return alteraSenhaViaLogin;

    }

    public String montaEmailTrocaSenha() {
        StringBuilder sb = new StringBuilder();
        sb.append("Recuperação de Senha").append("\n");
        if (selecionado.getPessoaFisica() != null) {
            sb.append(selecionado.getPessoaFisica()).append(", \n");
        }
        sb.append("Recebemos sua solicitação para envio de uma nova senha de acesso ao Sistema WebPublico.").append("\n");
        sb.append("Para a sua segurança, acesse o sistema, efetue seu login com a senha informada abaixo e altere sua senha de acesso por uma combinação de sua preferência.").append("\n");
        sb.append("\n");
        sb.append("Seu Login: ").append(selecionado.getLogin()).append("\n");
        sb.append("Senha temporária: ").append(selecionado.getLogin()).append("\n");
        sb.append("\n");
        sb.append("Caso você não tenha solicitado estas informações, por favor entre em contato com o suporte").append("\n");
        sb.append("\n");
        sb.append("Atenciosamente,").append("\n");
        sb.append("Suporte WebPúblico").append("\n");
        sb.append("Este é um e-mail automático. Por favor não o responda");
        return sb.toString();
    }

    @Override
    public AbstractFacade getFacede() {
        return usuarioSistemaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/admin/usuariosistema/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public boolean editando() {
        return selecionado.getId() != null;
    }

    public void recuperaArvoreHierarquiaOrganizacionalOrc() {
        rootOrc = new DefaultTreeNode(" ", null);
        try {
            hierarquias = usuarioSistemaFacade.getHierarquiaOrganizacionalFacade().getListaDeHierarquiaAdministrativaOrdenadaPorCodigo(getSistemaControlador().getDataOperacao());
            if (!hierarquias.isEmpty()) {
                arvoreMontadaOrc(rootOrc, hierarquias);
            }
            rootOrc.isExpanded();

        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addError("Erro..", e.getMessage());
        }
    }

    public void arvoreMontadaOrc(TreeNode raiz, List<HierarquiaOrganizacional> listaHO) {
        TreeNode no = null;
        for (HierarquiaOrganizacional hierarquiaOrganizacional : listaHO) {
            if (hierarquiaOrganizacional.getSuperior() == null) {
                TreeNode treeNode = new DefaultTreeNode(hierarquiaOrganizacional, rootOrc);
                treeNode.isExpanded();
                no = treeNode;
            } else {
                TreeNode treeNode = new DefaultTreeNode(hierarquiaOrganizacional, getPaiOrc(hierarquiaOrganizacional, no));
                treeNode.isExpanded();
            }
        }
    }

    public TreeNode getPaiOrc(HierarquiaOrganizacional ho, TreeNode root) {
        try {
            boolean acho = false;
            HierarquiaOrganizacional no = (HierarquiaOrganizacional) root.getData();
            if (no.getSubordinada().equals(ho.getSuperior())) {
                acho = true;
                return root;
            }
            if (!acho) {
                for (TreeNode treeNodeDaVez : root.getChildren()) {
                    TreeNode treeNode = getPai(ho, treeNodeDaVez);
                    HierarquiaOrganizacional hoRecuparado = (HierarquiaOrganizacional) treeNode.getData();
                    if (hoRecuparado.getSubordinada().equals(ho.getSuperior())) {
                        return treeNode;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }

    public TreeNode getPai(HierarquiaOrganizacional ho, TreeNode root) {
        boolean acho = false;
        HierarquiaOrganizacional no = (HierarquiaOrganizacional) root.getData();
        try {
            if (no.getSubordinada().equals(ho.getSuperior())) {
                acho = true;
                return root;
            }
        } catch (NullPointerException e) {
        }
        if (!acho) {
            for (TreeNode treeNodeDaVez : root.getChildren()) {
                TreeNode treeNode = getPai(ho, treeNodeDaVez);
                HierarquiaOrganizacional hoRecuparado = (HierarquiaOrganizacional) treeNode.getData();
                try {
                    if (hoRecuparado.getSubordinada().equals(ho.getSuperior())) {
                        return treeNode;
                    }
                } catch (NullPointerException e) {
                }
            }
        }
        return root;
    }

    public void selecionarHierarquiaDaArvore() {
        if (selectedNode != null) {
            hierarquiaOrgParticipante = (HierarquiaOrganizacional) selectedNode.getData();
        }
    }

    public List<HierarquiaOrganizacional> getHierarquias() {
        return hierarquias;
    }

    public void setHierarquias(List<HierarquiaOrganizacional> hierarquias) {
        this.hierarquias = hierarquias;
    }

    public String getHierarquiaOrcDaUnidade(UnidadeOrganizacional unidadeOrganizacional) {
        if (hierarquias != null) {
            for (HierarquiaOrganizacional hierarquia : hierarquias) {
                if (hierarquia.getSubordinada() != null) {
                    if (hierarquia.getSubordinada().equals(unidadeOrganizacional)) {
                        if (hierarquia.getHierarquiaOrganizacionalOrcamentaria() != null) {
                            return hierarquia.getHierarquiaOrganizacionalOrcamentaria().getSubordinada().getDescricao();
                        }
                    }
                }
            }
        }
        return " - ";
    }

    public void escolherUnidade(HierarquiaUnidadeUsuario hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public boolean hasFilhasParaMarcarPelaHierarquiaUsuario(HierarquiaUnidadeUsuario hierarquiaAdministrativa) {
        return !usuarioSistemaFacade.getHierarquiaOrganizacionalFacade().getHierarquiasFilhasDe(hierarquiaAdministrativa.getHierarquiaOrganizacional().getSubordinada(), getSistemaControlador().getDataOperacao(), TipoHierarquiaOrganizacional.ADMINISTRATIVA).isEmpty();
    }

    private boolean hasFilhasParaMarcarPelaHierarquia(HierarquiaOrganizacional hierarquiaOrganizacional) {
        return !usuarioSistemaFacade.getHierarquiaOrganizacionalFacade().getHierarquiasFilhasVigentesOuNaoVigentes(hierarquiaOrganizacional.getSubordinada(), getSistemaControlador().getDataOperacao(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, apenasUnidadesVigentes).isEmpty();
    }

    public void alterarUnidadesGestorProtocolo(Boolean atribuirGestor) {
        for (HierarquiaUnidadeUsuario hierarquiaAdministrativaFilho : hierarquiasAdministrativasUsuarioPesquisa) {
            hierarquiaAdministrativaFilho.getUsuarioUnidadeOrganizacional().setGestorProtocolo(atribuirGestor);
        }
    }

    public void alterarUnidadesGestorLicitacao(Boolean atribuirGestor) {
        for (HierarquiaUnidadeUsuario hierarquiaAdministrativaFilho : hierarquiasAdministrativasUsuarioPesquisa) {
            hierarquiaAdministrativaFilho.getUsuarioUnidadeOrganizacional().setGestorLicitacao(atribuirGestor);
        }
    }

    public void alterarUnidadesGestorMateriais(Boolean atribuirGestor) {
        for (HierarquiaUnidadeUsuario hierarquiaAdministrativaFilho : hierarquiasAdministrativasUsuarioPesquisa) {
            hierarquiaAdministrativaFilho.getUsuarioUnidadeOrganizacional().setGestorMateriais(atribuirGestor);
        }
    }

    public void alterarUnidadesGestorPatrimonio(Boolean atribuirGestor) {
        for (HierarquiaUnidadeUsuario hierarquiaAdministrativaFilho : hierarquiasAdministrativasUsuarioPesquisa) {
            hierarquiaAdministrativaFilho.getUsuarioUnidadeOrganizacional().setGestorPatrimonio(atribuirGestor);
        }
    }

    public void alterarUnidadesGestorOrcamento(Boolean atribuirGestor) {
        for (HierarquiaUnidadeUsuario hierarquiaAdministrativaFilho : hierarquiasAdministrativasUsuarioPesquisa) {
            hierarquiaAdministrativaFilho.getUsuarioUnidadeOrganizacional().setGestorOrcamento(atribuirGestor);
        }
    }

    public void alterarUnidadesGestorFinanceiro(Boolean atribuirGestor) {
        for (HierarquiaUnidadeUsuario hierarquiaAdministrativaFilho : hierarquiasAdministrativasUsuarioPesquisa) {
            hierarquiaAdministrativaFilho.getUsuarioUnidadeOrganizacional().setGestorFinanceiro(atribuirGestor);
        }
    }

    public void alterarUnidadesGestorControleInterno(Boolean atribuirGestor) {
        for (HierarquiaUnidadeUsuario hierarquiaAdministrativaFilho : hierarquiasAdministrativasUsuarioPesquisa) {
            hierarquiaAdministrativaFilho.getUsuarioUnidadeOrganizacional().setGestorControleInterno(atribuirGestor);
        }
    }

    public void alterarFilhosGestorProtocolo() {
        for (HierarquiaUnidadeUsuario hierarquiaAdministrativaFilho : hierarquiasAdministrativasUsuarioPesquisa) {
            if (hierarquiaAdministrativaFilho.getHierarquiaOrganizacional().getCodigo().startsWith(hierarquiaAdministrativa.getHierarquiaOrganizacional().getCodigoSemZerosFinais())) {
                hierarquiaAdministrativaFilho.getUsuarioUnidadeOrganizacional().setGestorProtocolo(hierarquiaAdministrativa.getUsuarioUnidadeOrganizacional().getGestorProtocolo());
            }
        }
    }

    public void alterarFilhosGestorLicitacao() {
        for (HierarquiaUnidadeUsuario hierarquiaAdministrativaFilho : hierarquiasAdministrativasUsuarioPesquisa) {
            if (hierarquiaAdministrativaFilho.getHierarquiaOrganizacional().getCodigo().startsWith(hierarquiaAdministrativa.getHierarquiaOrganizacional().getCodigoSemZerosFinais())) {
                hierarquiaAdministrativaFilho.getUsuarioUnidadeOrganizacional().setGestorLicitacao(hierarquiaAdministrativa.getUsuarioUnidadeOrganizacional().getGestorLicitacao());
            }
        }
    }

    public void alterarFilhosGestorMateriais() {
        for (HierarquiaUnidadeUsuario hierarquiaAdministrativaFilho : hierarquiasAdministrativasUsuarioPesquisa) {
            if (hierarquiaAdministrativaFilho.getHierarquiaOrganizacional().getCodigo().startsWith(hierarquiaAdministrativa.getHierarquiaOrganizacional().getCodigoSemZerosFinais())) {
                hierarquiaAdministrativaFilho.getUsuarioUnidadeOrganizacional().setGestorMateriais(hierarquiaAdministrativa.getUsuarioUnidadeOrganizacional().getGestorMateriais());
            }
        }
    }

    public void alterarFilhosGestorPatrimonio() {
        for (HierarquiaUnidadeUsuario hierarquiaAdministrativaFilho : hierarquiasAdministrativasUsuarioPesquisa) {
            if (hierarquiaAdministrativaFilho.getHierarquiaOrganizacional().getCodigo().startsWith(hierarquiaAdministrativa.getHierarquiaOrganizacional().getCodigoSemZerosFinais())) {
                hierarquiaAdministrativaFilho.getUsuarioUnidadeOrganizacional().setGestorPatrimonio(hierarquiaAdministrativa.getUsuarioUnidadeOrganizacional().getGestorPatrimonio());
            }
        }
    }

    public void alterarFilhosGestorOrcamento() {
        for (HierarquiaUnidadeUsuario hierarquiaAdministrativaFilho : hierarquiasAdministrativasUsuarioPesquisa) {
            if (hierarquiaAdministrativaFilho.getHierarquiaOrganizacional().getCodigo().startsWith(hierarquiaAdministrativa.getHierarquiaOrganizacional().getCodigoSemZerosFinais())) {
                hierarquiaAdministrativaFilho.getUsuarioUnidadeOrganizacional().setGestorOrcamento(hierarquiaAdministrativa.getUsuarioUnidadeOrganizacional().getGestorOrcamento());
            }
        }
    }

    public void alterarFilhosGestorFinanceiro() {
        for (HierarquiaUnidadeUsuario hierarquiaAdministrativaFilho : hierarquiasAdministrativasUsuarioPesquisa) {
            if (hierarquiaAdministrativaFilho.getHierarquiaOrganizacional().getCodigo().startsWith(hierarquiaAdministrativa.getHierarquiaOrganizacional().getCodigoSemZerosFinais())) {
                hierarquiaAdministrativaFilho.getUsuarioUnidadeOrganizacional().setGestorFinanceiro(hierarquiaAdministrativa.getUsuarioUnidadeOrganizacional().getGestorFinanceiro());
            }
        }
    }

    public void alterarFilhosGestorControleInterno() {
        for (HierarquiaUnidadeUsuario hierarquiaAdministrativaFilho : hierarquiasAdministrativasUsuarioPesquisa) {
            if (hierarquiaAdministrativaFilho.getHierarquiaOrganizacional().getCodigo().startsWith(hierarquiaAdministrativa.getHierarquiaOrganizacional().getCodigoSemZerosFinais())) {
                hierarquiaAdministrativaFilho.getUsuarioUnidadeOrganizacional().setGestorControleInterno(hierarquiaAdministrativa.getUsuarioUnidadeOrganizacional().getGestorControleInterno());
            }
        }
    }

    public HierarquiaOrganizacional getHierarquiaOrgOrc() {
        return hierarquiaOrgOrc;
    }

    public void setHierarquiaOrgOrc(HierarquiaOrganizacional hierarquiaOrgOrc) {
        this.hierarquiaOrgOrc = hierarquiaOrgOrc;
    }

    public List<UsuarioSistema> completarUsuarioSistemaPeloNomePessoaFisica(String filtro) {
        return usuarioSistemaFacade.completarUsuarioSistemaPeloNomePessoaFisica(filtro);
    }

    public List<UsuarioSistema> completaUsuarioSistema(String parte) {
        return usuarioSistemaFacade.completaUsuarioSistemaAtivoPeloLogin(parte);
    }

    public List<UsuarioSistema> buscarTodosUsuariosPorLoginOuNomeOuCpf(String parte) {
        return usuarioSistemaFacade.buscarTodosUsuariosPorLoginOuNomeOuCpf(parte);
    }

    public List<UsuarioSistema> completarUsuarioSistemaFiscalTributario(String parte) {
        return usuarioSistemaFacade.buscarFiscalTributarioPorLoginOrNome(parte);
    }

    public String getFiltroUnidadeAdministrativa() {
        return filtroUnidadeAdministrativa;
    }

    public void setFiltroUnidadeAdministrativa(String filtroUnidadeAdministrativa) {
        this.filtroUnidadeAdministrativa = filtroUnidadeAdministrativa;
    }

    public List<HierarquiaUnidadeUsuario> getHierarquiasAdministrativasUsuario() {
        return hierarquiasAdministrativasUsuario;
    }

    public void setHierarquiasAdministrativasUsuario(List<HierarquiaUnidadeUsuario> hierarquiasAdministrativasUsuario) {
        this.hierarquiasAdministrativasUsuario = hierarquiasAdministrativasUsuario;
    }

    public Boolean getApenasUnidadesVigentes() {
        return apenasUnidadesVigentes;
    }

    public void setApenasUnidadesVigentes(Boolean apenasUnidadesVigentes) {
        this.apenasUnidadesVigentes = apenasUnidadesVigentes;
    }

    public Map<HierarquiaOrganizacional, String> getDescricaoVigenciaHierarquias() {
        return descricaoVigenciaHierarquias;
    }

    public void setDescricaoVigenciaHierarquias(Map<HierarquiaOrganizacional, String> descricaoVigenciaHierarquias) {
        this.descricaoVigenciaHierarquias = descricaoVigenciaHierarquias;
    }

    public void logarComo() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesUtil.getRequestContextPath()
                + "/j_spring_security_switch_user?j_username=" + selecionado.getLogin());
        } catch (IOException e) {
            logger.error("Não foi possível redirecionar ", e);
        }
    }

    public class HierarquiaUnidadeUsuario {
        private UsuarioUnidadeOrganizacional usuarioUnidadeOrganizacional;
        private UsuarioUnidadeOrganizacionalOrcamentaria usuarioUnidadeOrganizacionalOrcamentaria;
        private HierarquiaOrganizacional hierarquiaOrganizacional;
        private Boolean marcado;

        public HierarquiaUnidadeUsuario(UsuarioUnidadeOrganizacional usuarioUnidadeOrganizacional) {
            this.usuarioUnidadeOrganizacional = usuarioUnidadeOrganizacional;
            this.hierarquiaOrganizacional = buscarHierarquiaAdministrativaVigente(usuarioUnidadeOrganizacional.getUnidadeOrganizacional());
            this.marcado = Boolean.FALSE;
        }

        public HierarquiaUnidadeUsuario(UsuarioUnidadeOrganizacionalOrcamentaria usuarioUnidadeOrganizacionalOrcamentaria) {
            this.usuarioUnidadeOrganizacionalOrcamentaria = usuarioUnidadeOrganizacionalOrcamentaria;
            this.hierarquiaOrganizacional = buscarHierarquiaOrcamentariaVigente(usuarioUnidadeOrganizacionalOrcamentaria.getUnidadeOrganizacional());
        }

        public HierarquiaOrganizacional getHierarquiaOrganizacional() {
            return hierarquiaOrganizacional;
        }

        public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
            this.hierarquiaOrganizacional = hierarquiaOrganizacional;
        }

        public UsuarioUnidadeOrganizacional getUsuarioUnidadeOrganizacional() {
            return usuarioUnidadeOrganizacional;
        }

        public void setUsuarioUnidadeOrganizacional(UsuarioUnidadeOrganizacional usuarioUnidadeOrganizacional) {
            this.usuarioUnidadeOrganizacional = usuarioUnidadeOrganizacional;
        }

        public UsuarioUnidadeOrganizacionalOrcamentaria getUsuarioUnidadeOrganizacionalOrcamentaria() {
            return usuarioUnidadeOrganizacionalOrcamentaria;
        }

        public void setUsuarioUnidadeOrganizacionalOrcamentaria(UsuarioUnidadeOrganizacionalOrcamentaria usuarioUnidadeOrganizacionalOrcamentaria) {
            this.usuarioUnidadeOrganizacionalOrcamentaria = usuarioUnidadeOrganizacionalOrcamentaria;
        }

        public Boolean getMarcado() {
            return marcado != null ? marcado : Boolean.FALSE;
        }

        public void setMarcado(Boolean marcado) {
            this.marcado = marcado;
        }
    }

    public void selecionarUnidadesAdministrativas(Boolean trueOrFalse) {
        for (HierarquiaUnidadeUsuario hierarquiaAdministrativaFilho : hierarquiasAdministrativasUsuarioPesquisa) {
            hierarquiaAdministrativaFilho.setMarcado(trueOrFalse);
        }
    }

    public void selecionarFilhosUnidadesAdministrativas() {
        for (HierarquiaUnidadeUsuario hierarquiaAdministrativaFilho : hierarquiasAdministrativasUsuarioPesquisa) {
            if (hierarquiaAdministrativaFilho.getHierarquiaOrganizacional().getCodigo().startsWith(hierarquiaAdministrativa.getHierarquiaOrganizacional().getCodigoSemZerosFinais())) {
                hierarquiaAdministrativaFilho.setMarcado(hierarquiaAdministrativa.getMarcado());
            }
        }
    }

    public void removerUnidadesAdministrativas() {
        List<HierarquiaUnidadeUsuario> hierarquiasAdmsMarcadas = Lists.newArrayList();
        for (HierarquiaUnidadeUsuario hierarquiaAdministrativaFilho : hierarquiasAdministrativasUsuario) {
            if (hierarquiaAdministrativaFilho.getMarcado()) {
                hierarquiasAdmsMarcadas.add(hierarquiaAdministrativaFilho);
            }
        }

        for (HierarquiaUnidadeUsuario hierarquiaAdministrativaFilho : hierarquiasAdmsMarcadas) {
            removerHierarquiaAdministrativa(hierarquiaAdministrativaFilho);
        }
    }

    public void adicionarAssinatura() {
        try {
            validarAssinatura();
            assinatura.setUsuarioSistema(selecionado);
            selecionado.getAssinaturas().add(assinatura);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } finally {
            assinatura = new AssinaturaUsuarioSistema();
            FacesUtil.atualizarComponente("Formulario:tabViewUsuario:panelAssinatura");
            FacesUtil.atualizarComponente("Formulario:tabViewUsuario:tabelaAssinaturas");
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    private void validarAssinatura() {
        ValidacaoException ve = new ValidacaoException();

        if (assinatura.getDetentorArquivoComposicao().getArquivoComposicao() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione um arquivo para adicionar.");
        }

        ve.lancarException();
    }

    public void excluirAssinatura(AssinaturaUsuarioSistema assinaturaUsuarioSistema) {
        selecionado.getAssinaturas().remove(assinaturaUsuarioSistema);
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalAdiministrativa(String parte) {
        List<HierarquiaOrganizacional> hierarquias = usuarioSistemaFacade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaHorganizacionalAdministrativaVigenteOuNaoVigente(parte, getSistemaControlador().getDataOperacao(), apenasUnidadesVigentes);
        if (!apenasUnidadesVigentes) {
            for (HierarquiaOrganizacional h : hierarquias) {
                if (!descricaoVigenciaHierarquias.containsKey(h)) {
                    descricaoVigenciaHierarquias.put(h, DataUtil.isVigente(h.getInicioVigencia(), h.getFimVigencia(), getSistemaControlador().getDataOperacao()) ? "" : " (Não vigente)");
                }
            }
        }
        return hierarquias;
    }

    public String getUltimoAcessoFormatado() {
        return DataUtil.getDataFormatadaDiaHora(selecionado.getUltimoAcesso());
    }

    public Integer getTempoMaximoInatividade() {
        if (tempoMaximoInatividade == null) {
            List<BloqueioDesbloqueioUsuario> bloqueioDesbloqueioUsuario = bloqueioDesbloqueioUsuarioFacade.buscarBloqueiosVigentes(new Date());
            if (bloqueioDesbloqueioUsuario == null) {
                return null;
            }
            tempoMaximoInatividade = bloqueioDesbloqueioUsuario.get(0).getTempoMaximoInatividade();
        }
        return tempoMaximoInatividade;
    }
}
