package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.SolicitacaoCadastroPessoa;
import br.com.webpublico.entidades.comum.TipoTemplateEmail;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.comum.UsuarioWebFacade;
import br.com.webpublico.negocios.tributario.services.IntegracaoRedeSimService;
import br.com.webpublico.nfse.domain.NfseUserAuthority;
import br.com.webpublico.nfse.facades.NfseAuthorityFacade;
import br.com.webpublico.nfse.util.RandomUtil;
import br.com.webpublico.solicitacaodispositivo.AuthoritiesConstants;
import br.com.webpublico.tributario.dto.EventoRedeSimDTO;
import br.com.webpublico.tributario.dto.HorarioFuncionamentoDTO;
import br.com.webpublico.tributario.dto.PessoaJuridicaDTO;
import br.com.webpublico.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ManagedBean(name = "pessoaTributarioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaPessoaFisicaTributario",
        pattern = "/tributario/configuracoes/pessoa/novapessoafisica/",
        viewId = "/faces/tributario/cadastromunicipal/pessoafisicatributario/edita.xhtml"),
    @URLMapping(id = "novaPessoaJuridicaTributario",
        pattern = "/tributario/configuracoes/pessoa/novapessoajuridica/",
        viewId = "/faces/tributario/cadastromunicipal/pessoafisicatributario/editaJuridica.xhtml"),
    @URLMapping(id = "editarPessoaFisicaTributario",
        pattern = "/tributario/configuracoes/pessoa/editarpessoafisica/#{pessoaTributarioControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/pessoafisicatributario/edita.xhtml"),
    @URLMapping(id = "editarPessoaJuridicaTributario",
        pattern = "/tributario/configuracoes/pessoa/editarpessoajuridica/#{pessoaTributarioControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/pessoafisicatributario/editaJuridica.xhtml"),
    @URLMapping(id = "verPessoaFisicaTributario",
        pattern = "/tributario/configuracoes/pessoa/verpessoafisica/#{pessoaTributarioControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/pessoafisicatributario/visualizar.xhtml"),
    @URLMapping(id = "verRetroativoPessoaFisicaTributario",
        pattern = "/tributario/configuracoes/pessoa/ver-pessoa-fisica-retroativo/#{pessoaTributarioControlador.id}/revisao/#{pessoaTributarioControlador.idRevisao}/",
        viewId = "/faces/tributario/cadastromunicipal/pessoafisicatributario/visualizar.xhtml"),
    @URLMapping(id = "verPessoaJuridicaTributario",
        pattern = "/tributario/configuracoes/pessoa/verpessoajuridica/#{pessoaTributarioControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/pessoafisicatributario/visualizarJuridica.xhtml"),
    @URLMapping(id = "verRetroativoPessoaJuridicaTributario",
        pattern = "/tributario/configuracoes/pessoa/ver-pessoa-juridica-retroativo/#{pessoaTributarioControlador.id}/revisao/#{pessoaTributarioControlador.idRevisao}/",
        viewId = "/faces/tributario/cadastromunicipal/pessoafisicatributario/visualizarJuridica.xhtml"),
    @URLMapping(id = "listarPessoaFisicaTributario",
        pattern = "/tributario/configuracoes/pessoa/listarpessoafisica/",
        viewId = "/faces/tributario/cadastromunicipal/pessoafisicatributario/listapessoafisica.xhtml"),
    @URLMapping(id = "listarPessoaJuridicaTributario",
        pattern = "/tributario/configuracoes/pessoa/listarpessoajuridica/",
        viewId = "/faces/tributario/cadastromunicipal/pessoafisicatributario/listapessoajuridica.xhtml")
})
public class PessoaTributarioControlador extends PrettyControlador<Pessoa> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(PessoaTributarioControlador.class);

    protected ConverterGenerico converterEscritorioContabil;
    protected ConverterGenerico converterNivelEscolaridade;
    protected ConverterGenerico converterUf;
    @EJB
    private PessoaFacade pessoaFacade;
    private ConverterGenerico converterCidade;
    private List<RG> rgs;
    private RG rg;
    private Habilitacao habilitacao;
    private List<Habilitacao> habilitacoes;
    private Telefone telefone = new Telefone();
    private EnderecoCorreio endereco = new EnderecoCorreio();
    private Endereco enderecoAux;
    private Telefone telefoneAux;
    private CEPLogradouro logradouro;
    private String complemento;
    private String numero;

    private HtmlInputText textocep;
    private List<CEPLogradouro> logradouros;
    private br.com.webpublico.util.PerfilEnumConverter perfilEnumConveter = new PerfilEnumConverter();
    private Boolean mostraPainelEspecialidades;
    private HierarquiaOrganizacional unidadeOrganizacionalSelecionada;
    private FileUploadEvent fileUploadEvent;
    private Arquivo arquivo;
    private StreamedContent imagemFoto;
    private boolean operacaoRBTrans;
    private ConverterAutoComplete converterPessoa;
    private boolean skip;
    private ConverterGenerico converterProfissao;
    @EJB
    private ProfissaoFacade profissaoFacade;
    @EJB
    private IntegracaoRedeSimFacade integracaoRedeSimFacade;
    private PessoaCNAE cnae;
    private SociedadePessoaJuridica socio;
    private RepresentanteLegalPessoa representante;
    private JuntaComercialPessoaJuridica juntaComercial;
    private FilialPessoaJuridica filial;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private AlteracaoCmcFacade alteracaoCmcFacade;
    private ConverterAutoComplete converterNaturezaJuridica;
    private boolean alterandoCNAE;
    private boolean alterandoSocio;

    private Dependente dependente;
    private Long idRevisao;
    private Date dataRetroativa;
    private EventoRedeSimDTO eventoRedeSimDTO;
    private PessoaJuridicaDTO pessoaJuridicaDTO;
    private CadastroEconomico cadastroEconomico;
    private UsuarioWeb usuarioWeb;
    private SituacaoCadastroEconomico situacaoCadastroEconomico;
    private List<CadastroEconomico> cadastroEconomicos;
    @EJB
    private NfseAuthorityFacade nfseAuthorityFacade;
    @EJB
    private UsuarioWebFacade usuarioWebFacade;
    private boolean alterouEmailUsuarioWeb = false;
    private Boolean mei;
    private List<HorarioFuncionamento> horariosFuncionamento;
    private List<HorarioFuncionamento> horariosFuncionamentoFiltrado;
    private SolicitacaoCadastroPessoa solicitacaoCadastroPessoa;

    private String codigoPesquisaHorario;
    private String descricaoPesquisaHorario;
    private Boolean permiteEditar;
    private Boolean permiteAprovar;

    private IntegracaoRedeSimService integracaoRedeSimService;

    public PessoaTributarioControlador() {
        super();
        integracaoRedeSimService = (IntegracaoRedeSimService) Util.getSpringBeanPeloNome("integracaoRedeSimService");
    }

    @Override
    public AbstractFacade getFacede() {
        return pessoaFacade;
    }

    public Date getDataRetroativa() {
        return dataRetroativa;
    }

    public Converter getConverterProfissao() {
        if (converterProfissao == null) {
            converterProfissao = new ConverterGenerico(Profissao.class, profissaoFacade);
        }
        return converterProfissao;
    }


    public boolean isAlterandoCNAE() {
        return alterandoCNAE;
    }

    public void setAlterandoCNAE(boolean alterandoCNAE) {
        this.alterandoCNAE = alterandoCNAE;
    }

    public boolean isAlterandoSocio() {
        return alterandoSocio;
    }

    public void setAlterandoSocio(boolean alterandoSocio) {
        this.alterandoSocio = alterandoSocio;
    }

    public UsuarioWeb getUsuarioWeb() {
        return usuarioWeb;
    }

    public void setUsuarioWeb(UsuarioWeb usuarioWeb) {
        this.usuarioWeb = usuarioWeb;
    }

    public String getCodigoPesquisaHorario() {
        return codigoPesquisaHorario;
    }

    public void setCodigoPesquisaHorario(String codigoPesquisaHorario) {
        this.codigoPesquisaHorario = codigoPesquisaHorario;
    }

    public String getDescricaoPesquisaHorario() {
        return descricaoPesquisaHorario;
    }

    public void setDescricaoPesquisaHorario(String descricaoPesquisaHorario) {
        this.descricaoPesquisaHorario = descricaoPesquisaHorario;
    }

    public List<HorarioFuncionamento> getHorariosFuncionamentoFiltrado() {
        if (horariosFuncionamentoFiltrado == null) {
            horariosFuncionamentoFiltrado = getHorarios();
        }
        return horariosFuncionamentoFiltrado;
    }

    public void limparPesquisaHorarioFuncionamento() {
        this.codigoPesquisaHorario = "";
        this.descricaoPesquisaHorario = "";
        this.horariosFuncionamentoFiltrado = getHorarios();
    }

    public void filtrarHorariosFuncionamento() {
        if (getHorarios() != null && !getHorarios().isEmpty()) {
            horariosFuncionamentoFiltrado = Lists.newArrayList();
            for (HorarioFuncionamento horario : getHorarios()) {
                if (!Strings.isNullOrEmpty(codigoPesquisaHorario) && horario.getCodigo().toString().toLowerCase().contains(codigoPesquisaHorario.toLowerCase()) && Strings.isNullOrEmpty(descricaoPesquisaHorario)) {
                    horariosFuncionamentoFiltrado.add(horario);
                } else if (!Strings.isNullOrEmpty(descricaoPesquisaHorario) && horario.getDescricao().toLowerCase().contains(descricaoPesquisaHorario.toLowerCase()) && Strings.isNullOrEmpty(codigoPesquisaHorario)) {
                    horariosFuncionamentoFiltrado.add(horario);
                } else if (!Strings.isNullOrEmpty(codigoPesquisaHorario) && horario.getCodigo().toString().toLowerCase().contains(codigoPesquisaHorario.toLowerCase()) &&
                    (!Strings.isNullOrEmpty(descricaoPesquisaHorario) && horario.getDescricao().toLowerCase().contains(descricaoPesquisaHorario.toLowerCase()))) {
                    horariosFuncionamentoFiltrado.add(horario);
                } else if (Strings.isNullOrEmpty(codigoPesquisaHorario) && Strings.isNullOrEmpty(descricaoPesquisaHorario)) {
                    horariosFuncionamentoFiltrado.add(horario);
                }
            }
        }
    }

    public void abrirPesquisaHorarioFuncionamento() {
        limparPesquisaHorarioFuncionamento();
        FacesUtil.atualizarComponente("formHorarioFuncionamento");
        FacesUtil.executaJavaScript("dialogoHorarioFuncionamento.show()");
    }

    @URLAction(mappingId = "novaPessoaFisicaTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaPessoaFisica() {
        super.updateMetaData(PessoaFisica.class);
        super.novo();
        PessoaFisica pf = (PessoaFisica) selecionado;
        pf.getPerfis().add(PerfilEnum.PERFIL_TRIBUTARIO);
        pf.setTelefones(new ArrayList<Telefone>());
        pf.setEnderecos(new ArrayList<EnderecoCorreio>());
        pf.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);
        telefone = new Telefone();
        endereco = new EnderecoCorreio();
        rg = new RG();
        rg.setDataRegistro(SistemaFacade.getDataCorrente());
        rg.setPessoaFisica(pf);
        habilitacao = new Habilitacao();
        habilitacoes = new ArrayList<Habilitacao>();
        telefoneAux = new Telefone();
        pf.setContaCorrenteBancPessoas(new ArrayList<ContaCorrenteBancPessoa>());
        unidadeOrganizacionalSelecionada = new HierarquiaOrganizacional();
        arquivo = new Arquivo();
        fileUploadEvent = null;
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", null);
        carregaNovaImagem();
        limparDependente();
        usuarioWeb = new UsuarioWeb();
    }

    @URLAction(mappingId = "novaPessoaJuridicaTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaPessoaJuridica() {
        super.updateMetaData(PessoaJuridica.class);
        super.novo();
        PessoaJuridica pj = (PessoaJuridica) selecionado;
        pj.getPerfis().add(PerfilEnum.PERFIL_TRIBUTARIO);
        pj.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);
        pj.setTelefones(new ArrayList<Telefone>());
        pj.setEnderecos(new ArrayList<EnderecoCorreio>());
        telefone = new Telefone();
        endereco = new EnderecoCorreio();
        telefoneAux = new Telefone();
        arquivo = new Arquivo();
        pj.setContaCorrenteBancPessoas(new ArrayList<ContaCorrenteBancPessoa>());
        unidadeOrganizacionalSelecionada = new HierarquiaOrganizacional();
        cnae = new PessoaCNAE();
        socio = new SociedadePessoaJuridica();
        representante = new RepresentanteLegalPessoa();
        juntaComercial = new JuntaComercialPessoaJuridica();
        filial = new FilialPessoaJuridica();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", null);
        carregaNovaImagem();
        usuarioWeb = new UsuarioWeb();
        limparPesquisaHorarioFuncionamento();
    }

    private String getCaminhoImagens() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String caminho = ((ServletContext) facesContext.getExternalContext().getContext()).getRealPath("/css/images/");
        caminho += "/";
        return caminho;
    }

    public void removeClasse(ActionEvent evt) {
        ClasseCredorPessoa ccp = (ClasseCredorPessoa) evt.getComponent().getAttributes().get("objeto");
        selecionado.getClasseCredorPessoas().remove(ccp);
    }

    public void removeClassificacao(ActionEvent evt) {
        PessoaClassificacaoCredor pcc = (PessoaClassificacaoCredor) evt.getComponent().getAttributes().get("objeto");
        selecionado.getClassificacaoCredores().remove(pcc);
    }

    public Boolean liberaContaPrincipal() {
        Boolean controle = true;
        if (selecionado != null) {
            for (ContaCorrenteBancPessoa ccbf : selecionado.getContaCorrenteBancPessoas()) {
                if (ccbf.getPrincipal()) {
                    controle = false;
                }
            }
        }
        return controle;
    }

    public Boolean habilitarEnderecoPrincipal() {
        if (selecionado != null) {
            for (EnderecoCorreio ec : selecionado.getEnderecos()) {
                if (ec.getPrincipal()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Boolean habilitarTelefonePrincipal() {
        if (selecionado != null) {
            for (Telefone fone : selecionado.getTelefones()) {
                if (fone.getPrincipal()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void addDocumentoPessoa(DocumentoPessoal dp) {
        if (dp != null) {
            PessoaFisica pf = (PessoaFisica) selecionado;
            pf.getDocumentosPessoais().add(dp);
        }
    }

    public Habilitacao getHabilitacao() {
        return habilitacao;
    }

    public void setHabilitacao(Habilitacao habilitacao) {
        this.habilitacao = habilitacao;
    }

    public Telefone getTelefone() {
        return telefone;
    }

    public void setTelefone(Telefone telefone) {
        this.telefone = telefone;
    }

    public EnderecoCorreio getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoCorreio endereco) {
        this.endereco = endereco;
    }

    public PessoaCNAE getCnae() {
        return cnae;
    }

    public List<PessoaCNAE> getCnaes() {
        return ((PessoaJuridica) selecionado).getPessoaCNAE();
    }

    public List<PessoaCNAE> getCnaesAtivos() {
        return ((PessoaJuridica) selecionado).getPessoaCNAEAtivos();
    }

    public List<PessoaHorarioFuncionamento> getHorariosFuncionamento() {
        return ((PessoaJuridica) selecionado).getHorariosFuncionamento();
    }

    public List<PessoaHorarioFuncionamento> getHorariosFuncionamentoAtivos() {
        return ((PessoaJuridica) selecionado).getHorariosFuncionamentoAtivos();
    }

    public HorarioFuncionamentoDTO getHorarioFuncionamentoPessoaDTO() {
        if (getPessoaRedeSim() != null && getPessoaRedeSim().getHorariosFuncionamento() != null &&
            !getPessoaRedeSim().getHorariosFuncionamento().isEmpty()) {
            return getPessoaRedeSim().getHorariosFuncionamento().get(0);
        }
        return null;
    }

    public List<PessoaCNAE> getCnaesInativos() {
        return ((PessoaJuridica) selecionado).getPessoaCNAEInativos();
    }

    public void setCnae(PessoaCNAE cnae) {
        this.cnae = cnae;
    }

    public SociedadePessoaJuridica getSocio() {
        return socio;
    }

    public void setSocio(SociedadePessoaJuridica socio) {
        this.socio = socio;
    }

    public RepresentanteLegalPessoa getRepresentante() {
        return representante;
    }

    public void setRepresentante(RepresentanteLegalPessoa representante) {
        this.representante = representante;
    }

    public JuntaComercialPessoaJuridica getJuntaComercial() {
        return juntaComercial;
    }

    public void setJuntaComercial(JuntaComercialPessoaJuridica juntaComercial) {
        this.juntaComercial = juntaComercial;
    }

    public FilialPessoaJuridica getFilial() {
        return filial;
    }

    public void setFilial(FilialPessoaJuridica filial) {
        this.filial = filial;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public CEPLogradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(CEPLogradouro logradouro) {
        this.logradouro = logradouro;
    }

    public List<CEPLogradouro> getLogradouros() {
        return logradouros;
    }

    public void setLogradouros(List<CEPLogradouro> logradouros) {
        this.logradouros = logradouros;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public HtmlInputText getTextocep() {
        return textocep;
    }

    public void setTextocep(HtmlInputText textocep) {
        this.textocep = textocep;
    }

    public List<SelectItem> getTiposEnderecos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoEndereco t : TipoEndereco.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public List<Habilitacao> getHabilitacoes() {
        return habilitacoes;
    }

    public void setHabilitacoes(List<Habilitacao> habilitacoes) {
        this.habilitacoes = habilitacoes;
    }

    public Endereco getEnderecoAux() {
        return enderecoAux;
    }

    public void setEnderecoAux(Endereco enderecoAux) {
        this.enderecoAux = enderecoAux;
    }

    public Telefone getTelefoneAux() {
        return telefoneAux;
    }

    public void setTelefoneAux(Telefone telefoneAux) {
        this.telefoneAux = telefoneAux;
    }

    public List<EnderecoCorreio> getEnderecos() {
        return selecionado.getEnderecos();
    }

    public CidadeControlador getCidadeControlador() {
        return (CidadeControlador) Util.getControladorPeloNome("cidadeControlador");
    }

    public List<CadastroEconomico> getCadastroEconomicos() {
        return cadastroEconomicos;
    }

    public void setCadastroEconomicos(List<CadastroEconomico> cadastroEconomicos) {
        this.cadastroEconomicos = cadastroEconomicos;
    }

    public void adicionarFone() {
        try {
            validarTelefone();
            telefone.setPessoa(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getTelefones(), telefone);
            limparTelefone();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void adicionarDependente() {
        try {
            validarDependente();
            verificarDependenteEmOutraPessoa();
            dependente.setResponsavel((PessoaFisica) selecionado);
            Util.adicionarObjetoEmLista(((PessoaFisica) selecionado).getDependentes(), dependente);
            limparDependente();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void verificarDependenteEmOutraPessoa() {
        ValidacaoException ve = new ValidacaoException();
        if (!pessoaFacade.validarDependenteEmOutraPessoa(selecionado, dependente)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Esse dependente já pertence a outra Pessoa!");
        }
        ve.lancarException();
    }

    public void limparDependente() {
        dependente = new Dependente();
    }

    private void validarDependente() {
        ValidacaoException ve = new ValidacaoException();
        if (dependente.getGrauDeParentesco() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Grau de Parentesco é obrigatório!");
        }
        if (dependente.getDependente() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Dependente é obrigatório!");
        }
        ve.lancarException();
        for (Dependente dp : ((PessoaFisica) selecionado).getDependentes()) {
            if (dp.getDependente().equals(dependente.getDependente()) && !dependente.equals(dp)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Dependente selecionado já está adicionado!");
            }
        }
        ve.lancarException();
    }

    private void validarTelefone() {
        ValidacaoException ve = new ValidacaoException();
        if (this.telefone.getTelefone().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o número do telefone");
        }
        ve.lancarException();
    }

    public void limparTelefone() {
        telefone = new Telefone();
    }

    public void adicionarRg() {
        if (rg != null) {
            PessoaFisica pf = (PessoaFisica) selecionado;
            rg.setDataRegistro(new Date());
            rg.setPessoaFisica(pf);

            int index = -1;
            for (DocumentoPessoal documentoPessoal : pf.getDocumentosPessoais()) {
                if (documentoPessoal instanceof RG && index < 0) {
                    index = pf.getDocumentosPessoais().indexOf(documentoPessoal);
                }
            }
            if (index >= 0) {
                pf.getDocumentosPessoais().remove(pf.getDocumentosPessoais().get(index));
            }
            if (pf.getDocumentosPessoais().contains(rg)) {
                pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(rg), rg);
            } else {
                pf.getDocumentosPessoais().add(rg);
            }

        }
    }

    public void removeRg(ActionEvent evento) {
        PessoaFisica pf = (PessoaFisica) selecionado;
        pf.getDocumentosPessoais().remove((RG) evento.getComponent().getAttributes().get("removeRg"));
        rgs.remove(0);
        rg = new RG();
    }

    public void adicionarHabilitacao() {
        try {
            validarHabilitacao();
            PessoaFisica pf = (PessoaFisica) selecionado;
            habilitacao.setDataRegistro(new Date());
            habilitacao.setPessoaFisica(pf);
            Util.adicionarObjetoEmLista(habilitacoes, habilitacao);
            Util.adicionarObjetoEmLista(pf.getDocumentosPessoais(), habilitacao);
            limparHabilitacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarHabilitacao() {
        ValidacaoException ve = new ValidacaoException();
        if (this.habilitacao.getNumero().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número é obrigatório.");
        }
        if (this.habilitacao.getCategoria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Categoria é obrigatório.");
        }
        if (this.habilitacao.getValidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Validade é obrigatório.");
        }
        ve.lancarException();
    }

    public void removerHabilitacao(Habilitacao habilitacao) {
        ((PessoaFisica) selecionado).getDocumentosPessoais().remove(habilitacao);
        habilitacoes.remove(habilitacao);
    }

    private void validarEndereco() {
        ValidacaoException ve = new ValidacaoException();
        if (endereco.getCep() == null || "".equals(endereco.getCep().trim())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo CEP é obrigatório.");
        }
        if (endereco.getLocalidade() == null || "".equals(endereco.getLocalidade().trim())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Cidade é obrigatório.");
        }
        if (endereco.getBairro() == null || "".equals(endereco.getBairro().trim())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Bairro é obrigatório.");
        }
        if (endereco.getLogradouro() == null || "".equals(endereco.getLogradouro().trim())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Logradouro é obrigatório.");
        }
        ve.lancarException();
        if (!selecionado.getEnderecos().contains(endereco)) {
            for (EnderecoCorreio enderecoCorreio : selecionado.getEnderecos()) {
                if (endereco.getTipoEndereco().equals(enderecoCorreio.getTipoEndereco()) && !TipoEndereco.OUTROS.equals(endereco.getTipoEndereco())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O tipo de endereço " + endereco.getTipoEndereco().getDescricao() + " já foi adicionado!");
                    ve.lancarException();
                }
            }
            if (endereco.getPrincipal() && selecionado.getEnderecoPrincipal() == null) {
                selecionado.setEnderecoPrincipal(endereco);
            }
        }
    }

    public void adicionarEndereco() {
        try {
            validarEndereco();
            Util.adicionarObjetoEmLista(selecionado.getEnderecos(), endereco);
            limparEndereco();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void alterarEndereco(EnderecoCorreio endereco) {
        this.endereco = (EnderecoCorreio) Util.clonarObjeto(endereco);
    }

    public void limparEndereco() {
        endereco = new EnderecoCorreio();
    }

    public void removerDependente(Dependente dependente) {
        ((PessoaFisica) selecionado).getDependentes().remove(dependente);
    }

    public void alterarDependente(Dependente dependente) {
        this.dependente = (Dependente) Util.clonarObjeto(dependente);
    }

    public void alterarTelefone(Telefone telefone) {
        this.telefone = (Telefone) Util.clonarObjeto(telefone);
    }

    public void alterarHabilitacao(Habilitacao habilitacao) {
        this.habilitacao = (Habilitacao) Util.clonarObjeto(habilitacao);
    }

    public void removeFone(ActionEvent evento) {
        Telefone vfone = (Telefone) evento.getComponent().getAttributes().get("vfone");
        if (selecionado.getTelefonePrincipal() != null && selecionado.getTelefonePrincipal().equals(vfone)) {
            selecionado.setTelefonePrincipal(null);
        }
        selecionado.getTelefones().remove(vfone);
    }

    public void removeEndereco(ActionEvent evento) {
        EnderecoCorreio vendereco = (EnderecoCorreio) evento.getComponent().getAttributes().get("vendereco");
        if (selecionado.getEnderecoPrincipal() != null && selecionado.getEnderecoPrincipal().equals(vendereco)) {
            selecionado.setEnderecoPrincipal(null);
        }
        selecionado.getEnderecos().remove(vendereco);
    }

    public List<SelectItem> getGrausDeParentesco() {
        return Util.getListSelectItem(pessoaFacade.getGrauDeParentescoFacade().lista());
    }

    public List<PessoaFisica> completarDependentes(String parte) {
        return pessoaFacade.getPessoaFisicaFacade().listaFiltrandoTodasPessoasByNomeAndCpf(parte.trim());
    }

    public List<SelectItem> getTiposFone() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoTelefone object : TipoTelefone.values()) {
            toReturn.add(new SelectItem(object, object.getTipoFone()));
        }
        return toReturn;
    }

    public List<SelectItem> getCategoriasHabilitacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (CategoriaHabilitacao object : CategoriaHabilitacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoSituacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoSituacaoMilitar object : TipoSituacaoMilitar.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getCategoria() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (CategoriaCertificadoMilitar object : CategoriaCertificadoMilitar.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public void setaEndereco(ActionEvent evento) {
        setEnderecoAux((Endereco) evento.getComponent().getAttributes().get("alteraEndereco"));
    }

    public void painelNaTela() {
        if (((PessoaFisica) selecionado).getNivelEscolaridade() == null) {
            mostraPainelEspecialidades = false;
        } else {
            mostraPainelEspecialidades = true;
        }
    }

    public Boolean getMostraPainelEspecialidades() {
        return mostraPainelEspecialidades;
    }

    public void setMostraPainelEspecialidades(Boolean mostraPainelEspecialidades) {
        this.mostraPainelEspecialidades = mostraPainelEspecialidades;
    }

    public void setaTelefone(ActionEvent evento) {
        setTelefoneAux((Telefone) evento.getComponent().getAttributes().get("alteraFone"));
    }

    public ConverterGenerico getConverterCidade() {
        if (converterCidade == null) {
            converterCidade = new ConverterGenerico(Cidade.class, pessoaFacade.getCidadeFacade());
        }
        return converterCidade;
    }

    public List<SelectItem> getEscritorioContabil() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();

        for (EscritorioContabil object : pessoaFacade.getEscritorioContabilFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getPessoa().getNome()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterEscritorioContabil() {
        if (converterEscritorioContabil == null) {
            converterEscritorioContabil = new ConverterGenerico(EscritorioContabil.class, pessoaFacade.getEscritorioContabilFacade());
        }
        return converterEscritorioContabil;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (this.converterPessoa == null) {
            this.converterPessoa = new ConverterAutoComplete(Pessoa.class, pessoaFacade);
        }
        return this.converterPessoa;
    }

    public List<Pessoa> completarPessoaFisica(String parte) {
        return pessoaFacade.listaPessoasFisicas(parte.toLowerCase().trim());
    }

    public List<Pessoa> completarPessoaJuridica(String parte) {
        return pessoaFacade.listaPessoasJuridicas(parte.toLowerCase().trim());
    }

    public List<Pessoa> completaPessoa(String parte) {
        return pessoaFacade.listaTodasPessoas(parte.trim());
    }

    public List<Cidade> completaNaturalidade(String parte) {
        return pessoaFacade.getCidadeFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<SelectItem> getCidades() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Cidade object : pessoaFacade.getCidadeFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public List<SelectItem> getNivelEscolaridade() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (NivelEscolaridade object : pessoaFacade.getNivelEscolaridadeFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getEstados() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (UF object : pessoaFacade.getUfFacade().listaUFNaoNula()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterNivelEscolaridade() {
        if (converterNivelEscolaridade == null) {
            converterNivelEscolaridade = new ConverterGenerico(NivelEscolaridade.class, pessoaFacade.getNivelEscolaridadeFacade());
        }
        return converterNivelEscolaridade;
    }

    public List<SelectItem> getSexo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Sexo object : Sexo.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRacaCor() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (RacaCor object : RacaCor.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getEstadoCivil() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (EstadoCivil object : EstadoCivil.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterUf() {
        if (converterUf == null) {
            converterUf = new ConverterGenerico(UF.class, pessoaFacade.getUfFacade());
        }
        return converterUf;
    }

    public List<SelectItem> getUf() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (UF object : pessoaFacade.getUfFacade().listaUFNaoNula()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacaoConta() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (SituacaoConta object : SituacaoConta.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoSanguineo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoSanguineo object : TipoSanguineo.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public String onFlowProcess(FlowEvent event) {
        if (skip) {
            skip = false;   //reset in case user goes back
            return "confirm";
        } else {
            return event.getNewStep();
        }
    }

    @Override
    public void editar() {
        super.editar();
        if (selecionado.getPerfis() != null) {
            if (!selecionado.getPerfis().contains(PerfilEnum.PERFIL_TRIBUTARIO)) {
                selecionado.getPerfis().add(PerfilEnum.PERFIL_TRIBUTARIO);
            }
        }
        enderecoAux = new Endereco();
        telefoneAux = new Telefone();
        telefone = new Telefone();
        endereco = new EnderecoCorreio();
        habilitacao = new Habilitacao();
        habilitacoes = new ArrayList<>();
        enderecoAux = new Endereco();
        if (selecionado.getArquivo() != null) {
            arquivo = selecionado.getArquivo();
        } else {
            arquivo = new Arquivo();
        }
    }

    @URLAction(mappingId = "editarPessoaFisicaTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarPessoaFisica() {
        this.updateMetaData(PessoaFisica.class);
        editar();
        selecionado = pessoaFacade.recuperarPF(selecionado.getId());
        if (SituacaoCadastralPessoa.AGUARDANDO_CONFIRMACAO_CADASTRO.equals(selecionado.getSituacaoCadastralPessoa())) {
            redirecionarParaVerOrEditar(selecionado.getId(), "verpessoafisica");
        } else {
            recuperarDadosPessoaFisica();
            cadastroEconomicos = cadastroEconomicoFacade.buscarCadastroEconomicoPorCpfComSituacao(selecionado.getCpf_Cnpj());
        }
    }

    @URLAction(mappingId = "editarPessoaJuridicaTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarPessoaJuridica() {
        this.updateMetaData(PessoaJuridica.class);
        editar();
        selecionado = pessoaFacade.recuperarPJ(selecionado.getId());
        recuperarDadosPessoaJuridica();
        cadastroEconomico = cadastroEconomicoFacade.buscarCadastroEconomicoPorCnpjRedeSim(selecionado.getCpf_Cnpj());
        cadastroEconomicos = cadastroEconomicoFacade.buscarCadastroEconomicoPorCnpjComSituacao(selecionado.getCpf_Cnpj());
        limparPesquisaHorarioFuncionamento();
    }

    private void recuperarDadosPessoaJuridica() {
        cnae = new PessoaCNAE();
        socio = new SociedadePessoaJuridica();
        representante = new RepresentanteLegalPessoa();
        juntaComercial = new JuntaComercialPessoaJuridica();
        filial = new FilialPessoaJuridica();
        carregaFoto();
        usuarioWeb = new UsuarioWeb();
        Collections.sort(selecionado.getPessoaCNAE());
        Collections.sort(((PessoaJuridica) selecionado).getHistoricosIntegracaoRedeSim());
    }

    @URLAction(mappingId = "verPessoaFisicaTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verPessoaFisica() {
        this.updateMetaData(PessoaFisica.class);
        editar();
        selecionado = pessoaFacade.recuperarPF(selecionado.getId());
        recuperarDadosPessoaFisica();
        cadastroEconomicos = cadastroEconomicoFacade.buscarCadastroEconomicoPorCpfComSituacao(selecionado.getCpf_Cnpj());
        if (SituacaoCadastralPessoa.AGUARDANDO_CONFIRMACAO_CADASTRO.equals(selecionado.getSituacaoCadastralPessoa())) {
            solicitacaoCadastroPessoa = pessoaFacade.buscarPorKeyAndTipo(selecionado.getKey(), TipoSolicitacaoCadastroPessoa.TRIBUTARIO);
        }
    }

    @URLAction(mappingId = "verPessoaJuridicaTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verPessoaJuridica() {
        editarPessoaJuridica();
    }

    @URLAction(mappingId = "verRetroativoPessoaFisicaTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verRetroativoPessoaFisica() {
        selecionarPorRevisaoPessoaFisica(getId(), idRevisao);
    }

    @URLAction(mappingId = "verRetroativoPessoaJuridicaTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verRetroativoPessoaJuridica() {
        selecionarPorRevisaoJuridica(getId(), idRevisao);
    }

    public void selecionarPorRevisaoPessoaFisica(Long id, Long idRevisao) {
        try {
            RevisaoAuditoria rev = (RevisaoAuditoria) pessoaFacade.getAuditoriaFacade().recuperar(RevisaoAuditoria.class, idRevisao);
            dataRetroativa = rev.getDataHora();
            PessoaFisica pessoa = pessoaFacade.recuperarPessoaFisicaPorRevisao(id, idRevisao);
            if (pessoa != null) {
                selecionado = pessoa;
            }
            recuperarDadosPessoaFisica();
        } catch (Exception ex) {
            FacesUtil.addError("Erro", "Não foi possível recuperar os dados de auditoria da pessoa!");
        }
    }

    public void selecionarPorRevisaoJuridica(Long id, Long idRevisao) {
        try {
            PessoaJuridica pessoa = pessoaFacade.recuperarPessoaJuridicaPorRevisao(id, idRevisao);
            if (pessoa != null) {
                selecionado = pessoa;
            }
            recuperarDadosPessoaJuridica();
        } catch (Exception ex) {
            FacesUtil.addError("Erro", "Não foi possível recuperar os dados de auditoria da pessoa!");
        }
    }


    private void recuperarDadosPessoaFisica() {
        fileUploadEvent = null;
        habilitacoes = Lists.newArrayList();

        if (selecionado instanceof PessoaFisica) {
            rg = verificaRG();
            if (rg == null) {
                rg = new RG();
                rg.setDataRegistro(new Date());
                rg.setPessoaFisica((PessoaFisica) selecionado);
                addDocumentoPessoa(rg);
            }
            for (DocumentoPessoal doc : ((PessoaFisica) selecionado).getDocumentosPessoais()) {
                if (doc instanceof Habilitacao) {
                    habilitacoes.add((Habilitacao) doc);
                }
            }
            carregaFoto();
        }
        limparDependente();
        usuarioWeb = new UsuarioWeb();
    }

    public RG verificaRG() {
        return ((PessoaFisica) selecionado).getRg();
    }

    public TituloEleitor verificaTitulo() {
        PessoaFisica pf = (PessoaFisica) selecionado;
        for (DocumentoPessoal documento : pf.getDocumentosPessoais()) {
            if (documento instanceof TituloEleitor) {
                return (TituloEleitor) documento;
            }
        }
        return null;
    }

    public SituacaoMilitar verificaMilitar() {
        PessoaFisica pf = (PessoaFisica) selecionado;
        for (DocumentoPessoal documento : pf.getDocumentosPessoais()) {
            if (documento instanceof SituacaoMilitar) {
                return (SituacaoMilitar) documento;
            }
        }
        return null;
    }

    public CertidaoNascimento verificaCertidaoNascimento() {
        PessoaFisica pf = (PessoaFisica) selecionado;
        for (DocumentoPessoal documento : pf.getDocumentosPessoais()) {
            if (documento instanceof CertidaoNascimento) {
                return (CertidaoNascimento) documento;
            }
        }
        return null;
    }

    public CertidaoCasamento verificaCertidaoCasamento() {
        PessoaFisica pf = (PessoaFisica) selecionado;
        for (DocumentoPessoal documento : pf.getDocumentosPessoais()) {
            if (documento instanceof CertidaoCasamento) {
                return (CertidaoCasamento) documento;
            }
        }
        return null;
    }

    public RG getRg() {
        return rg;
    }

    public void setRg(RG rg) {
        this.rg = rg;
    }

    public List<RG> getRgs() {
        return rgs;
    }

    public void setRgs(List<RG> rgs) {
        this.rgs = rgs;
    }

    private boolean validaEmail(String email) {
        if (email.isEmpty()) {
            return true;
        } else {
            Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
            Matcher m = p.matcher(email);
            return m.matches();
        }
    }

    public List<SelectItem> tiposEnderecos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoEndereco t : TipoEndereco.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public List<CEPUF> getListaUF() {
        return pessoaFacade.getConsultaCepFacade().listaUf();
    }

    public void setaCidade(SelectEvent e) {
        endereco.setLocalidade((String) e.getObject());
    }

    public List<String> completaCidade(String parte) {
        String l = "";
        if (endereco.getUf() != null) {
            l = endereco.getUf();
        }
        return pessoaFacade.getConsultaCepFacade().listaLocalidadesString(l, parte.trim());
    }

    public List<String> completaLogradouro(String parte) {
        String l = "";
        if (endereco.getLocalidade() != null) {
            l = endereco.getLocalidade();
        }
        return pessoaFacade.getConsultaCepFacade().listaLogradourosString(l, parte.trim());
    }

    public List<String> completaBairro(String parte) {
        String l = "";
        if (endereco.getLocalidade() != null) {
            l = endereco.getLocalidade();
        }
        return pessoaFacade.getConsultaCepFacade().listaBairrosString(l, parte.trim());
    }

    public List<String> completaCEP(String parte) {
        return pessoaFacade.getConsultaCepFacade().listaCEPString(parte.trim());
    }

    public void atualizaLogradouros() {
        pessoaFacade.getConsultaCepFacade().atualizarLogradouros(endereco);
    }

    @Override
    public void salvar() {
        try {
            adicionarRg();
            ValidaPessoa.validarPerfilTributario(selecionado);
            validarCampos();
            if (alterouEmailUsuarioWeb) {
                usuarioWebFacade.removerTermoDeUsoDoUsuario(selecionado);
                enviarEmailParaUsuariosNovos(selecionado, alterouEmailUsuarioWeb);
            }
            if (isOperacaoNovo()) {
                selecionado.adicionarHistoricoSituacaoCadastral();
                selecionado = pessoaFacade.salvarNovo(selecionado, arquivo, fileUploadEvent);
            } else {
                selecionado = pessoaFacade.salvar(selecionado, arquivo, fileUploadEvent, null);
            }
            carregaFoto();
            if (isSessao()) {
                Web.poeNaSessao(selecionado);
            }
            if (cadastroEconomico != null && cadastroEconomico.getId() != null) {
                if (alteracaoCmcFacade.hasAlteracaoCadastro(cadastroEconomico.getId())) {
                    cadastroEconomicoFacade.criarNotificacaoDeCalculoDeAlvara(cadastroEconomico, isMei());
                }
            }
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso!");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao salvar a pessoa: {}", ex);
            FacesUtil.addOperacaoNaoRealizada("Existem registros com referências que não podem ser removidas!");
        }
    }

    public PerfilEnumConverter getPerfilEnumConveter() {
        return perfilEnumConveter;
    }

    public void setPerfilEnumConveter(PerfilEnumConverter perfilEnumConveter) {
        this.perfilEnumConveter = perfilEnumConveter;
    }

    public void atalhoCidade() {
        getCidadeControlador().novo();
        getCidadeControlador().setSessao("/tributario/cadastromunicipal/pessoafisica/edita");
    }

    public List<SelectItem> getTipoRepresentantes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoRepresentanteLegal t : TipoRepresentanteLegal.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacaoCadastral() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        for (SituacaoCadastralPessoa p : SituacaoCadastralPessoa.values()) {
            lista.add(new SelectItem(p, p.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> getTipoDeficiencia() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoDeficiencia object : TipoDeficiencia.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    @Override
    public void cancelar() {
        if (selecionado instanceof PessoaFisica) {
            carregaFoto();
        }
        Web.getEsperaRetorno();
        redireciona();
    }

    @Override
    public void redireciona() {
        CRUD c = (CRUD) this;
        navegarEmbora(selecionado, c.getCaminhoPadrao());
    }

    public void navegarEmbora(Object selecionado, String caminhoPadrao) {
        String origem = Web.getCaminhoOrigem();
        if (!origem.isEmpty()) {
            if (Web.getEsperaRetorno()) {
                Web.poeNaSessao(selecionado);
            }
            FacesUtil.redirecionamentoInterno(origem);
        } else {
            if (selecionado instanceof PessoaFisica) {
                origem = caminhoPadrao + "listarpessoafisica/";
            } else if (selecionado instanceof PessoaJuridica) {
                origem = caminhoPadrao + "listarpessoajuridica/";
            }
            FacesUtil.redirecionamentoInterno(origem);
        }
    }

    public void preencheNomeReduzido() {
        if (!(selecionado instanceof PessoaJuridica)) {
            return;
        }
        PessoaJuridica pj = (PessoaJuridica) selecionado;
        if (pj.getNomeReduzido() == null || (pj.getNomeReduzido().trim().isEmpty()) || (pj.getNomeReduzido().trim().equalsIgnoreCase(pj.getRazaoSocial().trim()))) {
            pj.setNomeReduzido(pj.getRazaoSocial());
        }
    }

    public HierarquiaOrganizacional getUnidadeOrganizacionalSelecionada() {
        return unidadeOrganizacionalSelecionada;
    }

    public void setUnidadeOrganizacionalSelecionada(HierarquiaOrganizacional unidadeOrganizacionalSelecionada) {
        this.unidadeOrganizacionalSelecionada = unidadeOrganizacionalSelecionada;
    }

    public boolean isOperacaoRBTrans() {
        return operacaoRBTrans;
    }

    public void setOperacaoRBTrans(boolean operacaoRBTrans) {
        this.operacaoRBTrans = operacaoRBTrans;
    }

    public boolean mostraImagem() {
        try {
            return ((StreamedContent) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("imagem-foto")).getName().trim().length() <= 0;
        } catch (Exception e) {
            return true;
        }
    }

    public void uploadArquivo(FileUploadEvent event) {
        try {
            fileUploadEvent = event;
            imagemFoto = new DefaultStreamedContent(event.getFile().getInputstream(), "image/png", event.getFile().getFileName());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", imagemFoto);
        } catch (IOException ex) {
            logger.error("Erro: ", ex);
        }
    }

    public FileUploadEvent getFileUploadEvent() {
        return fileUploadEvent;
    }

    public void setFileUploadEvent(FileUploadEvent fileUploadEvent) {
        this.fileUploadEvent = fileUploadEvent;
    }

    public StreamedContent getImagemFoto() {
        return imagemFoto;
    }

    public void setImagemFoto(StreamedContent imagemFoto) {
        this.imagemFoto = imagemFoto;
    }

    public void carregaFoto() {
        Arquivo arq = selecionado.getArquivo();
        if (arq != null) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                for (ArquivoParte a : pessoaFacade.getArquivoFacade().recuperaDependencias(arq.getId()).getPartes()) {
                    buffer.write(a.getDados());
                }

                InputStream teste = new ByteArrayInputStream(buffer.toByteArray());
                imagemFoto = new DefaultStreamedContent(teste, arq.getMimeType(), arq.getNome());
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", imagemFoto);
            } catch (Exception ex) {
                logger.error("Erro: ", ex);
            }
        }
    }

    public List<SelectItem> getTiposInscricao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(TipoInscricao.CNPJ, TipoInscricao.CNPJ.getDescricao()));
        toReturn.add(new SelectItem(TipoInscricao.CEI, TipoInscricao.CEI.getDescricao()));
        return toReturn;
    }

    public String visualizaPessoa() {
        if (selecionado instanceof PessoaJuridica) {
            return "visualizarJuridica";
        } else {
            return "visualizar";
        }
    }

    public String editaPessoa() {
        if (selecionado instanceof PessoaFisica) {
            carregaFoto();
            return "edita";
        } else if (selecionado instanceof PessoaJuridica) {
            return "editaJuridica";
        }
        return "";
    }

    public void validarCpf() {
        if (!Util.valida_CpfCnpj(((PessoaFisica) selecionado).getCpf())) {
            FacesUtil.addWarn("Atenção!", "O CPF digitado é inválido!");
        } else if (isOperacaoNovo() && pessoaFacade.hasOutraPessoaComMesmoCpf((PessoaFisica) selecionado, false)) {
            FacesUtil.addAtencao(montarDescricaoValidacaoCpf());
        }
    }

    public void validarCnpj() {
        if (!Util.valida_CpfCnpj(((PessoaJuridica) selecionado).getCnpj())) {
            FacesUtil.addWarn("Atenção!", "O CNPJ digitado é inválido!");
        } else if (isOperacaoNovo() && pessoaFacade.hasOutraPessoaComMesmoCnpj((PessoaJuridica) selecionado, false)) {
            FacesUtil.addAtencao(montarDescricaoValidacaoCnpj());
        }
    }

    public String montarDescricaoValidacaoCpf() {
        String descricao = "";
        List<PessoaFisica> pfs = pessoaFacade.getPessoasComMesmoCPF((PessoaFisica) selecionado);
        descricao += "O CPF " + selecionado.getCpf_Cnpj() + " já pertence a ";
        for (PessoaFisica pf : pfs) {
            descricao += pf.getNome() + " (" + pf.getSituacaoCadastralPessoa() + ")";
            descricao += ", ";
        }
        return descricao.substring(0, descricao.length() - 2);
    }

    public String montarDescricaoValidacaoCnpj() {
        String descricao = "";
        List<PessoaJuridica> pjs = pessoaFacade.buscarPessoasJuridicasComMesmoCnpj((PessoaJuridica) selecionado);
        descricao += "O CNPJ " + selecionado.getCpf_Cnpj() + " já pertence a ";
        for (PessoaJuridica pj : pjs) {
            descricao += pj.getNome() + " (" + pj.getSituacaoCadastralPessoa() + ")";
            descricao += ", ";
        }
        return descricao.substring(0, descricao.length() - 2);
    }

    public List<SelectItem> classesPessoa() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (ClassePessoa cp : ClassePessoa.values()) {
            lista.add(new SelectItem(cp, cp.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> listaServico() {
        List<SelectItem> toReturn = new ArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (Profissao profissao : profissaoFacade.lista()) {
            toReturn.add(new SelectItem(profissao, profissao.getDescricao()));
        }
        return toReturn;
    }

    public List<PessoaFisica> getListaEspecial() {
        return pessoaFacade.getPessoaFisicaFacade().listaFiltrandoPorTipoPerfil("", PerfilEnum.PERFIL_ESPECIAL);
    }

    public List<SelectItem> getModalidades() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (ModalidadeConta object : ModalidadeConta.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public void limparHabilitacao() {
        this.habilitacao = new Habilitacao();
    }

    public void carregaNovaImagem() {
        try {
            File f = new File(getCaminhoImagens() + "semfoto.jpg");
            InputStream ip = new FileInputStream(f);
            imagemFoto = new DefaultStreamedContent(ip);
        } catch (Exception e) {
            FacesUtil.addMessageError("", "Não foi possível carregar a imagem");
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (!Util.valida_CpfCnpj(String.valueOf(selecionado.getCpf_Cnpj()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("CPF/CNPJ inválido!");
        } else {
            if (selecionado instanceof PessoaFisica) {
                if (isOperacaoNovo() && pessoaFacade.hasOutraPessoaComMesmoCpf((PessoaFisica) selecionado, false)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(montarDescricaoValidacaoCpf());
                }
            } else if (selecionado instanceof PessoaJuridica) {
                if (isOperacaoNovo() && pessoaFacade.hasOutraPessoaComMesmoCnpj((PessoaJuridica) selecionado, false)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(montarDescricaoValidacaoCnpj());
                }
                if (!((PessoaJuridica) selecionado).getSociedadePessoaJuridica().isEmpty()) {
                    if (getTotalSocios().compareTo(CEM) != 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A proporção total dos sócios deve ser equivalente a 100%.");
                    }
                }
            }
        }
        if (selecionado instanceof PessoaFisica && (rg.getOrgaoEmissao() == null || rg.getOrgaoEmissao().isEmpty())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão Emissor é obrigatório!");
        }
        if (operacaoRBTrans && imagemFoto == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione uma imagem!");
        }
        ve.lancarException();
    }

    public void excluir() {
        try {
            if (selecionado instanceof PessoaFisica) {
                pessoaFacade.getPessoaFisicaFacade().remover((PessoaFisica) selecionado);
            } else {
                pessoaFacade.getPessoaJuridicaFacade().remover((PessoaJuridica) selecionado);
            }
            FacesUtil.addInfo("Excluído com sucesso!", "O registro selecionado foi excluído com sucesso!");
            redireciona();
        } catch (Exception e) {
            FacesUtil.addError("Não é possível excluir essa pessoa!", "Essa pessoa já foi utilizada no sistema!");
        }
    }

    public String caminhoVisualizar() {
        if (selecionado instanceof PessoaFisica) {
            return "visualizar";
        } else if (selecionado instanceof PessoaJuridica) {
            return "visualizarJuridica";
        }
        return "visualizar";
    }

    public String hrefVerPessoa(Pessoa pessoa) {
        if (pessoa instanceof PessoaJuridica) {
            return getCaminhoPadrao() + "verpessoajuridica/" + pessoa.getId() + "/";
        }
        return getCaminhoPadrao() + "verpessoafisica/" + pessoa.getId() + "/";
    }

    public String hrefEditarPessoa(Pessoa pessoa) {
        if (pessoa instanceof PessoaJuridica) {
            return getCaminhoPadrao() + "editarpessoajuridica/" + pessoa.getId() + "/";
        }
        return getCaminhoPadrao() + "editarpessoafisica/" + pessoa.getId() + "/";
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/configuracoes/pessoa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getTipoCnae() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (EconomicoCNAE.TipoCnae object : EconomicoCNAE.TipoCnae.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public void enviarNovaSenhaPortal(UsuarioWeb usuarioWeb) {
        FacesUtil.addInfo("Enviado!", pessoaFacade.getPortalContribunteFacade().getUsuarioWebFacade().criarNovaSenhaeEnviarPorEmail(usuarioWeb));
    }


    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public void adicionarCNAE() {
        try {
            validarCnae(cnae);
            cnae.setDataregistro(getSistemaControlador().getDataOperacao());
            cnae.setPessoa((PessoaJuridica) selecionado);
            ((PessoaJuridica) selecionado).setPessoaCNAE(Util.adicionarObjetoEmLista(((PessoaJuridica) selecionado).getPessoaCNAE(), cnae));
            alterandoCNAE = false;
            cnae = new PessoaCNAE();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarCnae() {
        alterandoCNAE = false;
        cnae = new PessoaCNAE();
    }

    public void cancelarSocio() {
        alterandoSocio = false;
        socio = new SociedadePessoaJuridica();
    }

    public void adicionarSocio() {
        try {
            validarSocios(socio);
            socio.setDataRegistro(getSistemaControlador().getDataOperacao());
            socio.setPessoaJuridica((PessoaJuridica) selecionado);
            ((PessoaJuridica) selecionado).setSociedadePessoaJuridica(Util.adicionarObjetoEmLista(((PessoaJuridica) selecionado).getSociedadePessoaJuridica(), socio));
            alterandoSocio = false;
            socio = new SociedadePessoaJuridica();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarRepresentanteLegal() {
        try {
            validarRepresentanteLegal(representante);
            representante.setPessoa(selecionado);
            selecionado.getRepresentantesLegal().add(representante);
            representante = new RepresentanteLegalPessoa();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarJuntaComercial() {
        try {
            validarJuntaComercial(juntaComercial);
            juntaComercial.setPessoaJuridica((PessoaJuridica) selecionado);
            ((PessoaJuridica) selecionado).getJuntaComercial().add(juntaComercial);
            juntaComercial = new JuntaComercialPessoaJuridica();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarFilial() {
        try {
            validarFilial(filial);
            filial.setPessoaJuridica((PessoaJuridica) selecionado);
            ((PessoaJuridica) selecionado).getFiliais().add(filial);
            filial = new FilialPessoaJuridica();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<SociedadePessoaJuridica> getSociedadePessoaJuridica() {
        return ((PessoaJuridica) selecionado).getSociedadePessoaJuridica();
    }

    public void removerSocios(SociedadePessoaJuridica sociedadePessoaJuridica) {
        if (sociedadePessoaJuridica != null) {
            ((PessoaJuridica) selecionado).getSociedadePessoaJuridica().remove(sociedadePessoaJuridica);
        }
    }

    private void validarRemocaoCnae(PessoaCNAE pessoaCNAE) {
        ValidacaoException ve = new ValidacaoException();
        if (!Operacoes.NOVO.equals(operacao)) {
            EconomicoCNAE economicoCNAE = cadastroEconomicoFacade.buscarEconomicoCnaeDaPessoaCnae(pessoaCNAE);
            if (economicoCNAE != null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Esse CNAE já está sendo utilizado no Cadatro Econômico!");
            }
        }
        ve.lancarException();
    }

    private void validarCnae(PessoaCNAE cnae) {
        ValidacaoException ve = new ValidacaoException();
        if (cnae.getCnae() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o CNAE!");
        } else {
            for (PessoaCNAE ec : ((PessoaJuridica) selecionado).getPessoaCNAE()) {
                if (ec.getCnae().equals(cnae.getCnae()) && !alterandoCNAE) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Esse CNAE já foi adicionado!");
                    break;
                }
            }
        }
        if (cnae.getInicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Inicio!");
        }
        if (cnae.getTipo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo!");
        } else {
            if (EconomicoCNAE.TipoCnae.Primaria.equals(cnae.getTipo())) {
                for (PessoaCNAE ec : ((PessoaJuridica) selecionado).getPessoaCNAE()) {
                    if (EconomicoCNAE.TipoCnae.Primaria.equals(ec.getTipo()) && !ec.equals(cnae)
                        && CNAE.Situacao.ATIVO.equals(ec.getCnae().getSituacao())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um CNAE primário!");
                        break;
                    }
                }
            }
        }
        ve.lancarException();
    }

    private void validarSocios(SociedadePessoaJuridica socio) {
        ValidacaoException ve = new ValidacaoException();
        if (socio.getSocio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um sócio!");
        }
        if (socio.getProporcao() == null || socio.getProporcao() < 0.0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A proporção dever ser maior que zero!");
        } else {
            BigDecimal total = new BigDecimal(BigInteger.ZERO);
            for (SociedadePessoaJuridica soc : ((PessoaJuridica) selecionado).getSociedadePessoaJuridica()) {
                if (!soc.equals(socio) && ((soc.getDataFim() == null) || (soc.getDataFim().after(getSistemaControlador().getDataOperacao())))) {
                    total = total.add(new BigDecimal(soc.getProporcao()));
                }
            }
            final BigDecimal diferencaPermitida = new BigDecimal(0.01);
            total = total.add(new BigDecimal(socio.getProporcao())).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal diferenca = CEM.subtract(total).setScale(2, BigDecimal.ROUND_HALF_UP);
            if (total.compareTo(CEM) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A proporção total (" + new DecimalFormat("#,##0.00").format(total) + "%) ultrapassa os 100%");
            } else {
                if (diferenca.compareTo(diferencaPermitida.setScale(2, BigDecimal.ROUND_HALF_UP)) == 0 || diferenca.compareTo(diferencaPermitida.multiply(BigDecimal.valueOf(-1)).setScale(2, BigDecimal.ROUND_HALF_UP)) == 0) {
                    socio.setProporcao(socio.getProporcao() + diferenca.doubleValue());
                }
            }
        }
        if (socio.getDataInicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data de inicio!");
        }
        ve.lancarException();
    }

    private void validarRepresentanteLegal(RepresentanteLegalPessoa representante) {
        ValidacaoException ve = new ValidacaoException();
        if (representante.getRepresentante() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um Representante Legal!");
        }
        if (representante.getTipoRepresentanteLegal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o tipo do Representante Legal!");
        }
        if (representante.getDataInicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data de início!");
        }
        if (representante.getDataInicio() != null && representante.getDataFim() != null &&
            representante.getDataFim().compareTo(representante.getDataInicio()) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data final não pode ser maior que a data inicial!");
        }
        ve.lancarException();
    }

    private void validarJuntaComercial(JuntaComercialPessoaJuridica juntaComercialPessoaJuridica) {
        ValidacaoException ve = new ValidacaoException();
        if (juntaComercialPessoaJuridica.getNumeroProtocoloJuntaComercial() == null ||
            juntaComercialPessoaJuridica.getNumeroProtocoloJuntaComercial().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o número do protocolo da junta comercial!");
        }
        ve.lancarException();
    }

    private void validarFilial(FilialPessoaJuridica filialPessoaJuridica) {
        ValidacaoException ve = new ValidacaoException();
        if (filialPessoaJuridica.getFilial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a filial!");
        }
        ve.lancarException();
    }

    public void removerRepresentanteLegal(RepresentanteLegalPessoa representanteLegalPessoaJuridica) {
        if (representanteLegalPessoaJuridica != null) {
            ((PessoaJuridica) selecionado).getRepresentantesLegal().remove(representanteLegalPessoaJuridica);
        }
    }

    public void removerJuntaComercial(JuntaComercialPessoaJuridica juntaComercialPessoaJuridica) {
        if (juntaComercialPessoaJuridica != null) {
            ((PessoaJuridica) selecionado).getJuntaComercial().remove(juntaComercialPessoaJuridica);
        }
    }

    public void removerFilial(FilialPessoaJuridica filialPessoaJuridica) {
        if (filialPessoaJuridica != null) {
            ((PessoaJuridica) selecionado).getFiliais().remove(filialPessoaJuridica);
        }
    }

    public void removerCnae(PessoaCNAE pessoaCNAE) {
        if (pessoaCNAE != null) {
            try {
                validarRemocaoCnae(pessoaCNAE);
                ((PessoaJuridica) selecionado).getPessoaCNAE().remove(pessoaCNAE);
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        }
    }

    public void removerHorarioFuncionamento(PessoaHorarioFuncionamento pessoaHorarioFuncionamento) {
        selecionado.getHorariosFuncionamento().remove(pessoaHorarioFuncionamento);
    }

    public List<SelectItem> getNaturezasJuridica() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        List<NaturezaJuridica> listaNaturezaJuridica = cadastroEconomicoFacade.getNaturezaJuridicaFacade().listaNaturezaJuridicaPorTipo(TipoNaturezaJuridica.JURIDICA);
        for (NaturezaJuridica naturezaJuridica : listaNaturezaJuridica) {
            toReturn.add(new SelectItem(naturezaJuridica, naturezaJuridica.toString()));
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterNaturezaJuridica() {
        if (converterNaturezaJuridica == null) {
            converterNaturezaJuridica = new ConverterAutoComplete(NaturezaJuridica.class, cadastroEconomicoFacade.getNaturezaJuridicaFacade());
        }
        return converterNaturezaJuridica;
    }

    public void alterarCnae(PessoaCNAE pessoaCNAE) {
        if (pessoaCNAE.getHorarioFuncionamento() != null) {
            pessoaCNAE.setHorarioFuncionamento(cadastroEconomicoFacade.getHorarioFuncionamentoFacade().recuperar(pessoaCNAE.getHorarioFuncionamento().getId()));
        }
        alterandoCNAE = true;
        cnae = (PessoaCNAE) Util.clonarObjeto(pessoaCNAE);
    }

    public void alterarSocio(SociedadePessoaJuridica sociedadePessoaJuridica) {
        alterandoSocio = true;
        socio = (SociedadePessoaJuridica) Util.clonarObjeto(sociedadePessoaJuridica);
    }

    public void selecionarHorario(HorarioFuncionamento horario) {
        try {
            validarHorarioDeFuncionamento(horario);
            PessoaHorarioFuncionamento pessoaHorarioFuncionamento = new PessoaHorarioFuncionamento();
            pessoaHorarioFuncionamento.setPessoa(selecionado);
            pessoaHorarioFuncionamento.setHorarioFuncionamento(horario);
            pessoaHorarioFuncionamento.setAtivo(true);
            Util.adicionarObjetoEmLista(selecionado.getHorariosFuncionamento(), pessoaHorarioFuncionamento);
            FacesUtil.executaJavaScript("dialogoHorarioFuncionamento.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarHorarioDeFuncionamento(HorarioFuncionamento horario) {
        ValidacaoException ve = new ValidacaoException();
        for (PessoaHorarioFuncionamento pessoaHorarioFuncionamento : selecionado.getHorariosFuncionamentoAtivos()) {
            if (pessoaHorarioFuncionamento.getHorarioFuncionamento().equals(horario)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O horário selecionado já está adicionado.");
            }
        }
        ve.lancarException();
    }

    public List<HorarioFuncionamento> getHorarios() {
        if (horariosFuncionamento == null) {
            horariosFuncionamento = cadastroEconomicoFacade.getHorarioFuncionamentoFacade().listaComItens();
        }
        return horariosFuncionamento;
    }

    public BigDecimal getTotalSocios() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (SociedadePessoaJuridica soc : ((PessoaJuridica) selecionado).getSociedadePessoaJuridica()) {
            if ((soc.getDataFim() == null) || (soc.getDataFim().after(getSistemaControlador().getDataOperacao()))) {
                if (soc.getProporcao() != null) {
                    total = total.add(new BigDecimal(soc.getProporcao()));
                }
            }
        }
        return total.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void criarUsuarioWeb() {
        try {
            List<NfseUserAuthority> authorities = Lists.newArrayList();
            validarNovoUsuarioPortal();
            UsuarioWeb usuarioWebSemPessoa = usuarioWebFacade.buscarNfseUserPorLoginSemPessoa(StringUtil.retornaApenasNumeros(selecionado.getCpf_Cnpj()));
            if (usuarioWebSemPessoa != null) {
                usuarioWebSemPessoa.setEmail(this.usuarioWeb.getEmail());
                this.usuarioWeb = usuarioWebSemPessoa;
            } else {
                this.usuarioWeb.setPessoa(selecionado);
                this.usuarioWeb.setLogin(StringUtil.retornaApenasNumeros(selecionado.getCpf_Cnpj()));
                authorities.add(new NfseUserAuthority(this.usuarioWeb, nfseAuthorityFacade.buscarPorTipo(AuthoritiesConstants.USER)));
                authorities.add(new NfseUserAuthority(this.usuarioWeb, nfseAuthorityFacade.buscarPorTipo(AuthoritiesConstants.CONTRIBUINTE)));
                this.usuarioWeb.setNfseUserAuthorities(authorities);
            }
            this.usuarioWeb.setActivated(true);
            this.usuarioWeb.setActivationKey(RandomUtil.generateActivationKey());
            this.usuarioWeb.setPassword(usuarioWebFacade.encripitografarSenha(this.usuarioWeb.getLogin()));
            this.usuarioWeb.setPessoa(selecionado);
            this.alterouEmailUsuarioWeb = true;
            selecionado.getUsuariosWeb().add(this.usuarioWeb);
            this.usuarioWeb = new UsuarioWeb();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void alterarUsuarioWeb() {
        try {
            if (Strings.isNullOrEmpty(usuarioWeb.getEmail())) {
                new ValidacaoException("Informe um e-mail").lancarException();
            }
            selecionado.getUsuariosWeb().get(0).setEmail(usuarioWeb.getEmail());
            usuarioWeb = null;
            alterouEmailUsuarioWeb = true;
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    public boolean isDesabilitarBotaoAdicionarEmail() {
        return selecionado.getUsuariosWeb().isEmpty();
    }

    public boolean isDesabilitarBotaoAlterarEmail() {
        return !selecionado.getUsuariosWeb().isEmpty() && selecionado.getUsuariosWeb().get(0).equals(usuarioWeb);
    }

    public void removerEmailPortal(UsuarioWeb usuarioWeb) {
        selecionado.getUsuariosWeb().remove(usuarioWeb);
    }

    public void alterarEmailUsuarioPortalWeb(UsuarioWeb usuarioWeb) {
        this.usuarioWeb = usuarioWeb;
    }

    private void validarNovoUsuarioPortal() {
        ValidacaoException ve = new ValidacaoException();
        if (usuarioWeb == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível adicionar um usuário à pessoa!");
        } else {
            if (Strings.isNullOrEmpty(usuarioWeb.getEmail())) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o email para login no portal!");
            }
            for (UsuarioWeb usuario : selecionado.getUsuariosWeb()) {
                if (usuario.getEmail().equals(usuario.getEmail())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Esse email já existe para essa pessoa!");
                    break;
                }
            }
            if (Strings.isNullOrEmpty(selecionado.getCpf_Cnpj())) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o " + (selecionado instanceof PessoaFisica ? "CPF" : "CNPJ") + " da Pessoa");
            } else {
                UsuarioWeb usuarioWeb = usuarioWebFacade.recuperarUsuarioPorLogin(StringUtil.retornaApenasNumeros(selecionado.getCpf_Cnpj()));
                if (usuarioWeb != null && usuarioWeb.getPessoa() != null && !usuarioWeb.getPessoa().equals(selecionado)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um usuário do portal criado para esse " + (selecionado instanceof PessoaFisica ? "CPF:" : "CNPJ:") + selecionado.getCpf_Cnpj());
                }
            }

        }
        ve.lancarException();
    }

    public void enviarEmailParaUsuariosNovos(Pessoa pessoa, boolean alterouEmail) {
        for (UsuarioWeb usuario : pessoa.getUsuariosWeb()) {
            if (usuario.getId() == null || alterouEmail) {
                String senha = Util.gerarSenhaUsuarioPortalWeb();
                usuario.setPassword(usuarioWebFacade.encripitografarSenha(senha));
                FacesUtil.addInfo("Enviado!", pessoaFacade.getPortalContribunteFacade().getUsuarioWebFacade().enviarEmailComSenhaCriada(usuario, senha));
            }
        }
    }

    public Dependente getDependente() {
        return dependente;
    }

    public void setDependente(Dependente dependente) {
        this.dependente = dependente;
    }

    public Long getIdRevisao() {
        return idRevisao;
    }

    public void setIdRevisao(Long idRevisao) {
        this.idRevisao = idRevisao;
    }

    public boolean isRetroativo() {
        return idRevisao != null;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public SituacaoCadastroEconomico getSituacaoCadastroEconomico() {
        return situacaoCadastroEconomico;
    }

    public void setSituacaoCadastroEconomico(SituacaoCadastroEconomico situacaoCadastroEconomico) {
        this.situacaoCadastroEconomico = situacaoCadastroEconomico;
    }

    public void sincronizarComRedeSim() {
        try {
            eventoRedeSimDTO = integracaoRedeSimFacade.buscarRedeSimPorCnpj(selecionado.getCpf_Cnpj(), cadastroEconomicoFacade.getSistemaFacade().getUsuarioBancoDeDados(), true);
            cadastroEconomico = cadastroEconomicoFacade.buscarCadastroEconomicoPorCnpjRedeSim(selecionado.getCpf_Cnpj());
            if (eventoRedeSimDTO == null) {
                FacesUtil.addOperacaoNaoRealizada("Não foi possível encontrar as informações do CNPJ: " + selecionado.getCpf_Cnpj() + " na base da REDESIM");
            } else {
                FacesUtil.atualizarComponente("FormularioRedeSim");
                FacesUtil.executaJavaScript("mostrarDialogRedeSim()");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao comunicar com a RedeSim: " + ex.getMessage());
        }
    }

    public void confirmarIntegracaoRedeSim() {
        cadastroEconomico = cadastroEconomicoFacade.buscarCadastroEconomicoPorCnpj(selecionado.getCpf_Cnpj());
        integrarRedeSim(cadastroEconomico == null && pessoaComEnderecoDeRioBranco());
        FacesUtil.addOperacaoRealizada("Sincronização realizada com sucesso!");
    }

    private boolean pessoaComEnderecoDeRioBranco() {
        EnderecoCorreio enderecoCorreio = selecionado.getEnderecoPrincipal();
        if (enderecoCorreio == null && !selecionado.getEnderecos().isEmpty()) {
            enderecoCorreio = selecionado.getEnderecos().get(0);
        }
        return enderecoCorreio != null && enderecoCorreio.getLocalidade().equalsIgnoreCase("RIO BRANCO") && enderecoCorreio.getUf().equalsIgnoreCase("AC");
    }

    public void integrarRedeSim(boolean criaNovoCadastro) {
        try {
            EventoRedeSim eventoRedeSim = buscarOrCriarEventoRedeSim(eventoRedeSimDTO);
            integracaoRedeSimService.integrarEventoRedeSim(eventoRedeSim, getSistemaControlador().getUsuarioCorrente(),
                cadastroEconomicoFacade.getSistemaFacade().getUsuarioBancoDeDados(), criaNovoCadastro, false);
            recarregarPagina();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            logger.error("Erro ao confirmar integração com a redesim", e);
            FacesUtil.addErrorPadrao(e);
        }
    }

    private EventoRedeSim buscarOrCriarEventoRedeSim(EventoRedeSimDTO eventoRedeSimDTO) {
        String codigo = selecionado.getId() + "-" + selecionado.getAsPessoaJuridica().getJuntaComercial().size();
        String identificador = "Sincronização Manual";

        EventoRedeSim evento = new EventoRedeSim();
        evento.setCodigo(codigo);
        evento.setVersao(eventoRedeSimDTO.getVersao());
        evento.setIdentificador(identificador);
        evento.setCnpj(Util.formatarCnpj(eventoRedeSimDTO.getPessoaDTO().getCnpj()));
        evento.setTipoEvento(EventoRedeSim.TipoEvento.ATUALIZACAO);
        evento.setDescricao(selecionado.getNomeCpfCnpj());
        evento.setSituacao(EventoRedeSim.Situacao.PROCESSADO);
        try {
            evento.setSituacaoEmpresa(eventoRedeSimDTO.getPessoaDTO().getDescricaoSituacaoRFB());
            evento.setConteudo(new ObjectMapper().writeValueAsString(eventoRedeSimDTO.getPessoaDTO()));
        } catch (JsonProcessingException e) {
            evento.setConteudo(null);
        }
        evento = integracaoRedeSimFacade.salvarEventoRedeSim(evento);
        return evento;
    }

    public void carregarDadosHistoricoRedeSim(HistoricoAlteracaoRedeSim historico) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        if (historico.getConteudo() != null) {
            PessoaJuridicaDTO pessoaJuridicaDTO = mapper.readValue(historico.getConteudo(), PessoaJuridicaDTO.class);
            if (pessoaJuridicaDTO != null) {
                this.pessoaJuridicaDTO = pessoaJuridicaDTO;
                FacesUtil.executaJavaScript("mostrarDialogHistoricoRedeSim()");
                FacesUtil.atualizarComponente("formHistoricoRedeSim");
            }
        } else {
            FacesUtil.addOperacaoNaoPermitida("Esse histórico não contém um conteúdo da sincronização, tente um outro histórico!");
        }
    }

    public void exibirRevisaoAuditoria(HistoricoAlteracaoRedeSim hitorico) {
        Long idRevtype = pessoaFacade.recuperarIdRevisaoPessoaJuridicaHistoricoRedeSim(hitorico);
        Web.getSessionMap().put("pagina-anterior-auditoria-listar", getCaminhoPadrao() + "verpessoajuridica/" + selecionado.getId() + "/");
        FacesUtil.redirecionamentoInterno("/auditoria/ver/" + idRevtype + "/" + hitorico.getPessoaJuridica().getId() + "/" + PessoaJuridica.class.getSimpleName());
    }

    public void salvarCadastroEconomico() {
        if (cadastroEconomico.validarAlteracaoCnaeCadastroEconomico()) {
            try {
                cadastroEconomico = cadastroEconomicoFacade.salvarRetornado(cadastroEconomico);
            } catch (Exception ex) {
                logger.error("Erro ao salvar o Cadastro Economico! {}", ex);
            }
        } else {
            FacesUtil.addOperacaoNaoPermitida("Deve existir um CNAE Primário selecionado");
        }
    }

    public void navegarParaCadastro() {
        if (cadastroEconomico != null && cadastroEconomico.getId() != null) {
            if (alteracaoCmcFacade.hasAlteracaoCadastro(cadastroEconomico.getId())) {
                cadastroEconomicoFacade.criarNotificacaoDeCalculoDeAlvara(cadastroEconomico, isMei());
            }
            FacesUtil.redirecionamentoInterno("/tributario/cadastroeconomico/ver/" + cadastroEconomico.getId() + "/");
        } else {
            recarregarPagina();
        }
    }

    public void recarregarPagina() {
        if (cadastroEconomico != null && cadastroEconomico.getId() != null) {
            if (alteracaoCmcFacade.hasAlteracaoCadastro(cadastroEconomico.getId())) {
                cadastroEconomicoFacade.criarNotificacaoDeCalculoDeAlvara(cadastroEconomico, isMei());
            }
        }
        FacesUtil.redirecionamentoInterno("/tributario/configuracoes/pessoa/verpessoajuridica/" + selecionado.getId());
    }

    public PessoaJuridicaDTO getPessoaRedeSim() {
        return eventoRedeSimDTO != null ? eventoRedeSimDTO.getPessoaDTO() : null;
    }


    public boolean contemCnaePessoaJuridica(PessoaCNAE pessoaCNAE) {
        if (getCnaesAtivos() != null && pessoaCNAE != null && cadastroEconomico != null) {
            for (EconomicoCNAE economicoCNAE : cadastroEconomico.getEconomicoCNAEAtivos()) {
                if (economicoCNAE.getCnae() != null && economicoCNAE.getCnae().equals(pessoaCNAE.getCnae()) &&
                    economicoCNAE.getTipo() != null && economicoCNAE.getTipo().equals(pessoaCNAE.getTipo())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removerCnaePessoaJuridica(PessoaCNAE pessoaCNAE) {
        if (cadastroEconomico != null) {
            EconomicoCNAE cnaeParaRemover = null;
            for (EconomicoCNAE economicoCNAE : cadastroEconomico.getEconomicoCNAEAtivos()) {
                if (pessoaCNAE.getCnae().equals(economicoCNAE.getCnae())) {
                    cnaeParaRemover = economicoCNAE;
                    break;
                }
            }
            if (cnaeParaRemover != null) {
                cadastroEconomico.getEconomicoCNAE().remove(cnaeParaRemover);
            }
        }
    }

    public void adicionarCnaePessoaJuridica(PessoaCNAE pessoaCNAE) {
        EconomicoCNAE cnaeParaAdicionar = new EconomicoCNAE();
        cnaeParaAdicionar.setCadastroEconomico(cadastroEconomico);
        cnaeParaAdicionar.setCnae(pessoaCNAE.getCnae());
        cnaeParaAdicionar.setInicio(pessoaCNAE.getInicio());
        cnaeParaAdicionar.setFim(pessoaCNAE.getFim());
        cnaeParaAdicionar.setDataregistro(pessoaCNAE.getDataregistro());
        cnaeParaAdicionar.setHorarioFuncionamento(pessoaCNAE.getHorarioFuncionamento());
        cnaeParaAdicionar.setTipo(pessoaCNAE.getTipo());
        cnaeParaAdicionar.setExercidaNoLocal(pessoaCNAE.getExercidaNoLocal());
        cadastroEconomico.getEconomicoCNAE().add(cnaeParaAdicionar);
    }

    public void verCadastroEconomico(CadastroEconomico ce) {
        if (ce != null) {
            Web.navegacao(getUrlAtual(), "/tributario/cadastroeconomico/ver/" + ce.getId() + "/");
        }
    }

    public void buscarPessoaJuridicaNaRedeSim() {
        if (Strings.isNullOrEmpty(selecionado.getCpf_Cnpj())) {
            FacesUtil.addAtencao("Informe um CNPJ válido!");
            return;
        }
        try {
            eventoRedeSimDTO = integracaoRedeSimFacade.buscarRedeSimPorCnpj(selecionado.getCpf_Cnpj(), cadastroEconomicoFacade.getSistemaFacade().getUsuarioBancoDeDados(), false);
            if (eventoRedeSimDTO != null) {
                selecionado = integracaoRedeSimFacade.atualizarDadosPessoaJuridicaSemSalvar((PessoaJuridica) selecionado, eventoRedeSimDTO,
                    cadastroEconomicoFacade.getSistemaFacade().getUsuarioCorrente());
                permiteEditar = pessoaFacade.hasPermissaoEditar();
            } else {
                FacesUtil.addOperacaoNaoRealizada("Não foi localizado pessoa com esse CNPJ na RedeSim.");
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao comunicar com a RedeSim, verifique se as configurações estão corretas ou se a integração está no ar!");
            logger.error("Erro ao buscar dados da redesim: ", ex);
        }
    }

    public PessoaJuridicaDTO getPessoaJuridicaDTO() {
        return pessoaJuridicaDTO;
    }

    public void setPessoaJuridicaDTO(PessoaJuridicaDTO pessoaJuridicaDTO) {
        this.pessoaJuridicaDTO = pessoaJuridicaDTO;
    }

    public boolean isMei() {
        if (cadastroEconomico != null && cadastroEconomico.getId() != null) {
            if (mei == null) {
                EnquadramentoFiscal enquadramentoFiscal = cadastroEconomicoFacade.buscarEnquadramentoFiscalVigente(cadastroEconomico);
                mei = (enquadramentoFiscal != null && enquadramentoFiscal.isMei());
            }
            return mei;
        }
        return false;
    }

    public boolean hasPermissaoEditar() {
        if (permiteEditar == null) {
            permiteEditar = pessoaFacade.hasPermissaoEditar() || selecionado.getId() == null;
        }
        return permiteEditar;
    }

    public boolean hasPermissaoAprovar() {
        if (permiteAprovar == null) {
            permiteAprovar = pessoaFacade.hasPermissaoAprovarRejeitar() || selecionado.getId() == null;
        }
        return permiteAprovar;
    }

    public void aprovarCadastro() {
        pessoaFacade.confirmarAprovacaoSolicitacaoCadastroPessoa(selecionado);
        FacesUtil.addOperacaoRealizada("Cadastro Aprovado com sucesso!");
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listarpessoafisica/");
    }

    public SolicitacaoCadastroPessoa getSolicitacaoCadastroPessoa() {
        return solicitacaoCadastroPessoa;
    }

    public void setSolicitacaoCadastroPessoa(SolicitacaoCadastroPessoa solicitacaoCadastroPessoa) {
        this.solicitacaoCadastroPessoa = solicitacaoCadastroPessoa;
    }

    public void rejeitarCadastro() {
        if (solicitacaoCadastroPessoa != null) {
            solicitacaoCadastroPessoa.setDataRejeicao(new Date());
            solicitacaoCadastroPessoa.setUsuarioRejeicao(getSistemaControlador().getUsuarioCorrente());
            FacesUtil.executaJavaScript("dialogRejeicaoCadastro.show()");
        } else {
            FacesUtil.addOperacaoNaoPermitida("Não foi localizado o formulário de solicitação de cadastro dessa pessoa!");
        }
    }

    private void validarRejeicaoCadastroPessoa() {
        ValidacaoException ve = new ValidacaoException();
        if (solicitacaoCadastroPessoa.getDataRejeicao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor Informar a Data da rejeição!");
        }
        if (solicitacaoCadastroPessoa.getMotivoRejeicao() == null || "".equals(solicitacaoCadastroPessoa.getMotivoRejeicao().trim())) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor Informar o Motivo da rejeição!");
        }
        ve.lancarException();
    }

    public void confirmarRejeicaoCadastroPessoa() {
        try {
            validarRejeicaoCadastroPessoa();
            pessoaFacade.confirmarRejeicaoSolicitacaoCadastroPessoa(selecionado, solicitacaoCadastroPessoa, TipoTemplateEmail.REJEICAO_FORMULARIO_CADASTRO);
            FacesUtil.addOperacaoRealizada("Envio de rejeição enviado para " + solicitacaoCadastroPessoa.getEmail());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }
}

