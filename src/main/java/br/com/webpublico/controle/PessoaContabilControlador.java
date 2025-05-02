/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.RelatorioPessoaFContabilControlador;
import br.com.webpublico.controlerelatorio.RelatorioPessoaJContabilControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.SolicitacaoCadastroPessoa;
import br.com.webpublico.entidades.comum.TipoTemplateEmail;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.VOSolicitacaoCadastroPessoa;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jaimaum
 */
@ManagedBean(name = "pessoaContabilControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-pessoa-fisica", pattern = "/pessoa-fisica/novo/", viewId = "/faces/financeiro/pessoacredor/edita.xhtml"),
    @URLMapping(id = "editar-pessoa-fisica", pattern = "/pessoa-fisica/editar/#{pessoaContabilControlador.id}/", viewId = "/faces/financeiro/pessoacredor/edita.xhtml"),
    @URLMapping(id = "ver-pessoa-fisica", pattern = "/pessoa-fisica/ver/#{pessoaContabilControlador.id}/", viewId = "/faces/financeiro/pessoacredor/visualizar.xhtml"),
    @URLMapping(id = "listar-pessoa-fisica", pattern = "/pessoa-fisica/listar/", viewId = "/faces/financeiro/pessoacredor/listaFisica.xhtml"),
    @URLMapping(id = "novo-pessoa-juridica", pattern = "/pessoa-juridica/novo/", viewId = "/faces/financeiro/pessoacredor/editaJuridica.xhtml"),
    @URLMapping(id = "editar-pessoa-juridica", pattern = "/pessoa-juridica/editar/#{pessoaContabilControlador.id}/", viewId = "/faces/financeiro/pessoacredor/editaJuridica.xhtml"),
    @URLMapping(id = "ver-pessoa-juridica", pattern = "/pessoa-juridica/ver/#{pessoaContabilControlador.id}/", viewId = "/faces/financeiro/pessoacredor/visualizarJuridica.xhtml"),
    @URLMapping(id = "listar-pessoa-juridica", pattern = "/pessoa-juridica/listar/", viewId = "/faces/financeiro/pessoacredor/listaJuridica.xhtml")
})
public class PessoaContabilControlador extends PrettyControlador<Pessoa> implements Serializable, CRUD {

    public static final String CODIGO_CLASSE_CREDOR_PRESTADOR_SERVIDOR = "7";
    public static final String CODIGO_CLASSE_CREDOR_LOCACAO_VEICULO = "49";
    public static final String CODIGO_CLASSE_CREDOR_LOCACAO_MAQUINAS = "50";
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ContaCorrenteBancariaFacade contaCorrenteBancariaFacade;
    private ConverterGenerico converterNivelEscolaridade;
    private ConverterGenerico converterNacionalidade;
    private ConverterGenerico converterUf;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private ConverterAutoComplete converterClasseCredor;
    private ConverterAutoComplete converterNaturalidade;
    private ConverterAutoComplete converterCidadeTitulo;
    private ConverterAutoComplete converterAgencia;
    private ConverterAutoComplete converterContaCorrenteBancaria;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ClasseCredor classeCredor;
    private SituacaoMilitar situacaoMilitar;
    private Habilitacao habilitacao;
    private EnderecoCorreio endereco;
    private ContaCorrenteBancaria contaCorrenteBancaria;
    private CEPLogradouro logradouro;
    private CertidaoNascimento certidaoNascimento;
    private CertidaoCasamento certidaoCasamento;
    private CarteiraTrabalho carteiraTrabalho;
    private RG rg;
    private ContaCorrenteBancPessoa contaCorrenteBancPessoa;
    private Banco banco;
    private HierarquiaOrganizacional unidadeOrganizacionalSelecionada;
    private ClasseCredorPessoa classeCredorSelecionada;
    private TituloEleitor tituloEleitor;
    private Telefone telefone;
    private String mudaPanelContaCorrenteBancaria;
    private CEPLogradouro cep;
    private List<CarteiraTrabalho> carteiraTrabalhos;
    private List<Habilitacao> habilitacoes;
    private List<CEPLogradouro> logradouros;
    private Pessoa pessoaFiltro;
    private List<VOSolicitacaoCadastroPessoa> solicitacoesPendentes;
    private SolicitacaoCadastroPessoa solicitacaoCadastroPessoa;
    private ConfiguracaoPortalContribuinte configuracaoPortalContribuinte;
    private boolean canAprovarRejeitarSolicitacao = false;

    public PessoaContabilControlador() {
        super(Pessoa.class);
    }

    public PessoaFacade getFacade() {
        return pessoaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return pessoaFacade;
    }

    public String retornaCaminhoPadraoPF() {
        return "/pessoa-fisica/";
    }

    public String retornaCaminhoPadraoPJ() {
        return "/pessoa-juridica/";
    }

    @Override
    public String getCaminhoPadrao() {
        if (selecionado instanceof PessoaJuridica) {
            return "/pessoa-juridica/";
        } else {
            return "/pessoa-fisica/";
        }
    }

    public String getCaminhoVisualizarPessoa(Pessoa p) {
        if (p instanceof PessoaJuridica) {
            return "/pessoa-juridica/ver/" + p.getId() + "/";
        } else {
            return "/pessoa-fisica/ver/" + p.getId() + "/";
        }
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-pessoa-juridica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoJuridico() {
        paramentrosIniciaisPJ();
    }

    @URLAction(mappingId = "novo-pessoa-fisica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoFisico() {
        paramentrosIniciaisPF();
    }

    @URLAction(mappingId = "editar-pessoa-fisica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarFisica() {
        super.editar();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "ver-pessoa-fisica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verFisica() {
        super.ver();
        recuperarEditarVer();
        if (SituacaoCadastralPessoa.AGUARDANDO_CONFIRMACAO_CADASTRO.equals(selecionado.getSituacaoCadastralPessoa())) {
            solicitacaoCadastroPessoa = pessoaFacade.buscarPorKeyAndTipo(selecionado.getKey(), TipoSolicitacaoCadastroPessoa.CONTABIL);
            atualizarCanAprovarRejeitarSolicitacaoCadastro();
        }
    }

    @URLAction(mappingId = "editar-pessoa-juridica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarJuridica() {
        super.editar();
        recuperarEditarVer();
        instanciarContaBancariaPessoa();
    }

    @URLAction(mappingId = "ver-pessoa-juridica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verJuridica() {
        super.ver();
        recuperarEditarVer();
        if (SituacaoCadastralPessoa.AGUARDANDO_CONFIRMACAO_CADASTRO.equals(selecionado.getSituacaoCadastralPessoa())) {
            solicitacaoCadastroPessoa = pessoaFacade.buscarPorKeyAndTipo(selecionado.getKey(), TipoSolicitacaoCadastroPessoa.CONTABIL);
            atualizarCanAprovarRejeitarSolicitacaoCadastro();
        }
    }

    public void paramentrosIniciaisPJ() {
        selecionado = new PessoaJuridica();
        operacao = Operacoes.NOVO;
        setMudaPanelContaCorrenteBancaria("novaConta");
        selecionado.getPerfis().add(PerfilEnum.PERFIL_CREDOR);
        selecionado.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);
        selecionado.setTelefones(new ArrayList<Telefone>());
        selecionado.setEnderecos(new ArrayList<EnderecoCorreio>());
        telefone = new Telefone();
        endereco = new EnderecoCorreio();
        contaCorrenteBancaria = new ContaCorrenteBancaria();
        selecionado.setContaCorrenteBancPessoas(new ArrayList<ContaCorrenteBancPessoa>());
        instanciarContaBancariaPessoa();
        unidadeOrganizacionalSelecionada = null;
        classeCredorSelecionada = new ClasseCredorPessoa();
        cep = new CEPLogradouro();
        solicitacoesPendentes = pessoaFacade.getSolicitacaoCadastroPessoaFacade()
            .buscarPessoasJuridicasComSolicitacaoCadastroPessoaPorTipoESituacao(
                TipoSolicitacaoCadastroPessoa.CONTABIL,
                SituacaoCadastralPessoa.AGUARDANDO_CONFIRMACAO_CADASTRO
            );
    }

    public void paramentrosIniciaisPF() {
        selecionado = new PessoaFisica();
        operacao = Operacoes.NOVO;
        selecionado.getPerfis().add(PerfilEnum.PERFIL_CREDOR);
        selecionado.setTelefones(new ArrayList<Telefone>());
        selecionado.setEnderecos(new ArrayList<EnderecoCorreio>());
        selecionado.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);
        selecionado.setContaCorrenteBancPessoas(new ArrayList<ContaCorrenteBancPessoa>());
        setMudaPanelContaCorrenteBancaria("novaConta");
        telefone = new Telefone();
        endereco = new EnderecoCorreio();
        rg = new RG();
        tituloEleitor = new TituloEleitor();
        tituloEleitor.setDataRegistro(new Date());
        tituloEleitor.setPessoaFisica((PessoaFisica) selecionado);
        carteiraTrabalho = new CarteiraTrabalho();
        carteiraTrabalhos = new ArrayList<>();
        habilitacao = new Habilitacao();
        habilitacoes = new ArrayList<>();
        situacaoMilitar = new SituacaoMilitar();
        situacaoMilitar.setPessoaFisica((PessoaFisica) selecionado);
        certidaoNascimento = new CertidaoNascimento();
        certidaoNascimento.setPessoaFisica((PessoaFisica) selecionado);
        certidaoCasamento = new CertidaoCasamento();
        certidaoCasamento.setPessoaFisica((PessoaFisica) selecionado);
        contaCorrenteBancaria = new ContaCorrenteBancaria();
        instanciarContaBancariaPessoa();
        unidadeOrganizacionalSelecionada = null;
        classeCredorSelecionada = new ClasseCredorPessoa();
        cep = new CEPLogradouro();
        solicitacoesPendentes = pessoaFacade.getSolicitacaoCadastroPessoaFacade()
            .buscarPessoasFisicasComSolicitacaoCadastroPessoaPorTipoESituacao(
                TipoSolicitacaoCadastroPessoa.CONTABIL,
                SituacaoCadastralPessoa.AGUARDANDO_CONFIRMACAO_CADASTRO
            );
    }

    public void recuperarEditarVer() {
        selecionado = pessoaFacade.recuperar(super.getId());

        if (!selecionado.getPerfis().contains(PerfilEnum.PERFIL_CREDOR)) {
            selecionado.getPerfis().add(PerfilEnum.PERFIL_CREDOR);
        }
        setMudaPanelContaCorrenteBancaria("novaConta");
        cancelarContaBancariaPessoaFisica();
        contaCorrenteBancaria = new ContaCorrenteBancaria();
        telefone = new Telefone();
        endereco = new EnderecoCorreio();
        habilitacao = new Habilitacao();
        habilitacoes = new ArrayList<>();
        carteiraTrabalho = new CarteiraTrabalho();
        carteiraTrabalhos = new ArrayList<>();
        if (selecionado.getUnidadeOrganizacional() != null) {
            unidadeOrganizacionalSelecionada = pessoaFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(sistemaControlador.getDataOperacao(), selecionado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        }
        classeCredorSelecionada = new ClasseCredorPessoa();
        cep = new CEPLogradouro();
        if (selecionado instanceof PessoaFisica) {
            recuperaDadosPessoaFisica();
        }
        if (selecionado instanceof PessoaJuridica) {
            recuperaDadosPessoaJuridica();
        }
        banco = null;
    }

    private void recuperaDadosPessoaJuridica() {

        selecionado = pessoaFacade.recuperarPJ(selecionado.getId());
        setaPessoaJuridicaContaCorrenteBancaria();
    }

    private void recuperaDadosPessoaFisica() {

        selecionado = pessoaFacade.recuperarPF(selecionado.getId());

        if (verificaRG() == null) {
            rg = new RG();
            rg.setDataRegistro(sistemaControlador.getDataOperacao());
            rg.setDataRegistro(sistemaControlador.getDataOperacao());
            rg.setPessoaFisica((PessoaFisica) selecionado);
        }
        if (verificaTitulo() == null) {
            tituloEleitor = new TituloEleitor();
            tituloEleitor.setDataRegistro(sistemaControlador.getDataOperacao());
            tituloEleitor.setPessoaFisica((PessoaFisica) selecionado);
        }
        if (verificaMilitar() == null) {
            situacaoMilitar = new SituacaoMilitar();
            situacaoMilitar.setDataRegistro(sistemaControlador.getDataOperacao());
            situacaoMilitar.setPessoaFisica((PessoaFisica) selecionado);
        }
        if (verificaCertidaoNascimento() == null) {
            certidaoNascimento = new CertidaoNascimento();
            certidaoNascimento.setDataRegistro(sistemaControlador.getDataOperacao());
            certidaoNascimento.setPessoaFisica((PessoaFisica) selecionado);
        }
        if (verificaCertidaoCasamento() == null) {
            certidaoCasamento = new CertidaoCasamento();
            certidaoCasamento.setDataRegistro(sistemaControlador.getDataOperacao());
            certidaoCasamento.setPessoaFisica((PessoaFisica) selecionado);
        }

        PessoaFisica pf = (PessoaFisica) selecionado;
        for (DocumentoPessoal doc : pf.getDocumentosPessoais()) {
            if (doc instanceof RG) {
                rg = (RG) doc;
            } else if (doc instanceof TituloEleitor) {
                tituloEleitor = (TituloEleitor) doc;
            } else if (doc instanceof CarteiraTrabalho) {
                carteiraTrabalhos.add((CarteiraTrabalho) doc);
            } else if (doc instanceof Habilitacao) {
                habilitacoes.add((Habilitacao) doc);
            } else if (doc instanceof SituacaoMilitar) {
                situacaoMilitar = (SituacaoMilitar) doc;
            } else if (doc instanceof CertidaoNascimento) {
                certidaoNascimento = (CertidaoNascimento) doc;
            } else if (doc instanceof CertidaoCasamento) {
                certidaoCasamento = (CertidaoCasamento) doc;
            }
        }
        setaPessoaFisicaContaCorrenteBancaria();
    }

    public void limparContaCorrenteBancaria() {
        contaCorrenteBancaria = new ContaCorrenteBancaria();
    }

    public RG verificaRG() {
        PessoaFisica pf = (PessoaFisica) selecionado;
        for (DocumentoPessoal documento : pf.getDocumentosPessoais()) {
            if (documento instanceof RG) {
                return (RG) documento;
            }
        }
        return null;
    }

    private Boolean validaVigenciaClassePessoa() {
        Boolean retorno = true;
        for (ClasseCredorPessoa classesCredores : selecionado.getClasseCredorPessoas()) {
            if (classesCredores.getDataFimVigencia() != null) {
                if (classesCredores.getClasseCredor().equals(classeCredorSelecionada.getClasseCredor()) && classeCredorSelecionada.getDataInicioVigencia().before(classesCredores.getDataFimVigencia())) {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Já existe uma Classe de Pessoa vigente, com início de vigência em: " + DataUtil.getDataFormatada(classeCredorSelecionada.getDataInicioVigencia()));
                    retorno = false;
                    break;
                }
                if (classesCredores.getClasseCredor().equals(classeCredorSelecionada.getClasseCredor()) && classeCredorSelecionada.getDataInicioVigencia().compareTo(classesCredores.getDataFimVigencia()) == 0) {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Já existe uma Classe de Pessoa vigente, com início de vigência em: " + DataUtil.getDataFormatada(classeCredorSelecionada.getDataInicioVigencia()));
                    retorno = false;
                    break;
                }
            } else {
                if (classesCredores.getClasseCredor().equals(classeCredorSelecionada.getClasseCredor())) {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Classe de Pessoa já foi adicionada na lista.");
                    retorno = false;
                    break;
                }
            }
        }
        return retorno;
    }

    public Boolean validaAdicionarClassePessoa() {
        Boolean retoro = false;
        if (classeCredorSelecionada.getClasseCredor() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Classe de Pessoa é obrigatório para adicionar.");
            retoro = true;
        }
        if (classeCredorSelecionada.getOperacaoClasseCredor() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Operação é obrigatório para adicionar.");
            retoro = true;
        }
        if (classeCredorSelecionada.getDataInicioVigencia() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Início de Vigência é obrigatório para adicionar.");
            retoro = true;
        }
        return retoro;
    }

    public Boolean validaMesmaClassePessoa() {
        Boolean retorno = true;
        if (validaAdicionarClassePessoa()) {
            retorno = false;
        } else {
            retorno = validaVigenciaClassePessoa();
        }
        return retorno;
    }

    public void adicionaClassePessoa() {
        if (validaMesmaClassePessoa()) {
            classeCredorSelecionada.setPessoa(selecionado);
            selecionado.getClasseCredorPessoas().add(classeCredorSelecionada);
            classeCredorSelecionada = new ClasseCredorPessoa();
        }
    }

    public void validaDataEncerraVigencia(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Date data = (Date) value;
        if (data.compareTo(classeCredorSelecionada.getDataInicioVigencia()) < 0) {
            message.setDetail("A data Fim de Vigência deve ser maior que a dada de Início de Vigência");
            message.setSummary("Operação não Permitida! ");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public void encerrarVigencia() {
        try {
            selecionado.setClasseCredorPessoas(Util.adicionarObjetoEmLista(selecionado.getClasseCredorPessoas(), classeCredorSelecionada));
            classeCredorSelecionada = new ClasseCredorPessoa();
            FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Vigência encerrada com sucesso!");
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Erro: " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    public void cancelaEncerrarVigencia() {
        classeCredorSelecionada = new ClasseCredorPessoa();
    }

    public boolean podeEditar(ClasseCredorPessoa classeCredorPessoa) {
        if (classeCredorPessoa.getDataFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(classeCredorPessoa.getDataFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao())) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public void setaClasseCredor(ClasseCredorPessoa ccp) {
        classeCredorSelecionada = ccp;
    }

    public void validaCpf() {

        if (!validaCpf(((PessoaFisica) selecionado).getCpf())) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O CPF digitado é inválido.");
        }

    }

    public void removeClasse(ClasseCredorPessoa ccp) {
        selecionado.getClasseCredorPessoas().remove(ccp);
    }

    public Boolean liberaContaPrincipal() {
        Boolean controle = false;
        if (selecionado != null) {
            for (ContaCorrenteBancPessoa ccbf : selecionado.getContaCorrenteBancPessoas()) {
                if (ccbf.getPrincipal() != null && ccbf.getSituacao().isAtivo()) {
                    if (ccbf.getPrincipal()) {
                        controle = true;
                    }
                }
            }
        }
        return controle;
    }

    public Boolean liberaEnderecoPrincipal() {
        Boolean controle = false;
        if (selecionado != null) {
            for (EnderecoCorreio ec : selecionado.getEnderecos()) {
                if (ec.getPrincipal() != null) {
                    if (ec.getPrincipal()) {
                        controle = true;
                    }
                }
            }
        }
        return controle;
    }

    public Boolean liberaTelefonePrincipal() {
        Boolean retorno = false;
        if (selecionado != null) {
            for (Telefone fone : selecionado.getTelefones()) {
                if (fone.getPrincipal() != null) {
                    if (fone.getPrincipal()) {
                        retorno = true;
                    }
                }
            }
        }
        return retorno;
    }

    public void adicionarTelefonePF() {
        if (telefone.getTelefone().isEmpty()) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Telefone é obrigatório para adicionar.");
        } else {
            telefone.setPessoa((PessoaFisica) selecionado);
            selecionado.setTelefones(Util.adicionarObjetoEmLista(selecionado.getTelefones(), telefone));
            telefone = new Telefone();
        }
    }

    public void adicionarTelefonePJ() {
        if (telefone.getTelefone().isEmpty()) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Telefone é obrigatório para adicionar.");
        } else {
            telefone.setPessoa((PessoaJuridica) selecionado);
            selecionado.setTelefones(Util.adicionarObjetoEmLista(selecionado.getTelefones(), telefone));
            telefone = new Telefone();
        }
    }

    public void novoRg() {
        if (rg != null) {
            if (rg.getNumero() != null || !rg.getNumero().trim().isEmpty()) {
                PessoaFisica pf = (PessoaFisica) selecionado;
                rg.setDataRegistro(sistemaControlador.getDataOperacao());
                rg.setPessoaFisica(pf);
                if (pf.getDocumentosPessoais().contains(rg)) {
                    pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(rg), rg);

                } else {
                    pf.getDocumentosPessoais().add(rg);
                }
            }
        }
    }

    public void validarRG() {
        ValidacaoException ve = new ValidacaoException();
        if (rg.getNumero() == null || rg.getNumero().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Número do RG deve ser informado! ");
        }
        if (rg.getDataemissao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Data de Emissão do RG deve ser informado! ");
        }
        if (rg.getOrgaoEmissao() == null || rg.getOrgaoEmissao().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Órgão Emissor do RG deve ser informado!");
        }
        ve.lancarException();
    }

    public void novoCertidaoNascimento() {
        PessoaFisica pf = (PessoaFisica) selecionado;
        certidaoNascimento.setDataRegistro(new Date());
        certidaoNascimento.setPessoaFisica(pf);
        if (pf.getDocumentosPessoais().contains(certidaoNascimento)) {
            pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(certidaoNascimento), certidaoNascimento);
        } else {
            pf.getDocumentosPessoais().add(certidaoNascimento);
        }
    }

    public void novoCertidaoCasamento() {
        if (certidaoCasamento != null) {
            PessoaFisica pf = (PessoaFisica) selecionado;
            certidaoCasamento.setDataRegistro(sistemaControlador.getDataOperacao());
            certidaoCasamento.setPessoaFisica(pf);
            if (pf.getDocumentosPessoais().contains(certidaoCasamento)) {
                pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(certidaoCasamento), certidaoCasamento);
            } else {
                pf.getDocumentosPessoais().add(certidaoCasamento);
            }
        }
    }

    public void novoTitulo() {
        if (tituloEleitor != null && !tituloEleitor.getNumero().isEmpty()) {
            PessoaFisica pf = (PessoaFisica) selecionado;
            tituloEleitor.setDataRegistro(sistemaControlador.getDataOperacao());
            tituloEleitor.setPessoaFisica(pf);
            if (pf.getDocumentosPessoais().contains(tituloEleitor)) {
                pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().lastIndexOf(tituloEleitor), tituloEleitor);
            } else {
                pf.getDocumentosPessoais().add(tituloEleitor);
            }
        }
    }

    public void adicionarHabilitacao() {
        PessoaFisica pf = (PessoaFisica) selecionado;
        if (habilitacao.getNumero().isEmpty()) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Número é obrigatório para adicionar.");
        } else {
            habilitacao.setDataRegistro(sistemaControlador.getDataOperacao());
            habilitacao.setPessoaFisica(pf);
            habilitacoes.add(habilitacao);
        }
        if (pf.getDocumentosPessoais().contains(habilitacao)) {
            pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(habilitacao), habilitacao);
        } else {
            pf.getDocumentosPessoais().add(habilitacao);
            habilitacao = new Habilitacao();
        }
    }

    public void removeHabilitacao(ActionEvent evento) {
        PessoaFisica pf = (PessoaFisica) selecionado;
        pf.getDocumentosPessoais().remove((Habilitacao) evento.getComponent().getAttributes().get("removeHabilitacao"));
        habilitacoes.remove((Habilitacao) evento.getComponent().getAttributes().get("removeHabilitacao"));
        habilitacao = new Habilitacao();
    }

    public void removeTrabalho(ActionEvent evento) {
        PessoaFisica pf = (PessoaFisica) selecionado;
        pf.getDocumentosPessoais().remove((CarteiraTrabalho) evento.getComponent().getAttributes().get("removeTrabalho"));
        carteiraTrabalhos.remove((CarteiraTrabalho) evento.getComponent().getAttributes().get("removeTrabalho"));
        carteiraTrabalho = new CarteiraTrabalho();
    }

    public void novoMilitar() {
        if (situacaoMilitar != null && !situacaoMilitar.getCertificadoMilitar().isEmpty()) {
            PessoaFisica pf = (PessoaFisica) selecionado;
            situacaoMilitar.setDataRegistro(sistemaControlador.getDataOperacao());
            situacaoMilitar.setPessoaFisica((PessoaFisica) selecionado);
            if (pf.getDocumentosPessoais().contains(situacaoMilitar)) {
                pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(situacaoMilitar), situacaoMilitar);
            } else {
                pf.getDocumentosPessoais().add(situacaoMilitar);
            }
        }
    }

    public void adicionarCarteiraTrabalho() {
        PessoaFisica pf = (PessoaFisica) selecionado;
        if (validaCarteiraTrabalho()) {
            carteiraTrabalho.setDataRegistro(sistemaControlador.getDataOperacao());
            carteiraTrabalho.setPessoaFisica(pf);
            carteiraTrabalhos.add(carteiraTrabalho);
            if (pf.getDocumentosPessoais().contains(carteiraTrabalho)) {
                pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(carteiraTrabalho), carteiraTrabalho);
            } else {
                pf.getDocumentosPessoais().add(carteiraTrabalho);
                carteiraTrabalho = new CarteiraTrabalho();
            }
        }
    }

    private boolean validaCarteiraTrabalho() {
        boolean retorno = true;
        if (carteiraTrabalho.getPisPasep().isEmpty()) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo PIS/PASEP deve ser informado para adicionar.");
            return false;
        }
        if (!ArquivoPisPasepFacade.isValid(carteiraTrabalho.getPisPasep())) {
            FacesUtil.addOperacaoNaoPermitida("O PIS/PASEP é informado é inválido.");
            retorno = false;
        }
        if (!pessoaFacade.validaPisPasep((PessoaFisica) selecionado, carteiraTrabalho.getPisPasep())) {
            FacesUtil.addOperacaoNaoPermitida("Já existe um PIS/PASEP com cadastrado com esse número.");
            retorno = false;
        }
        for (CarteiraTrabalho ct : carteiraTrabalhos) {
            if (ct.getPisPasep().equals(carteiraTrabalho.getPisPasep())) {
                FacesUtil.addOperacaoNaoPermitida("Já existe um PIS/PASEP com esse número na lista.");
                retorno = false;
            }
        }

        return retorno;
    }

    public void pisValido() {
        if (!ArquivoPisPasepFacade.isValid(carteiraTrabalho.getPisPasep())) {
            FacesUtil.addOperacaoNaoPermitida("O PIS/PASEP é informado é inválido!");
            return;
        }
        if (ArquivoPisPasepFacade.identificaNIT(carteiraTrabalho.getPisPasep())) {
            FacesUtil.addOperacaoNaoPermitida("O PIS/PASEP informado é um número de NIT, por favor informe outro número de PIS/PASEP.");
            this.carteiraTrabalho.setPisPasep("");
            return;
        }
    }

    public void validarPISPASEPaoSalvar() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado instanceof PessoaFisica) {
            for (ClasseCredorPessoa classeCredorPessoa : selecionado.getClasseCredorPessoas()) {
                if (classeCredorPessoa.getClasseCredor().getCodigo().equals(CODIGO_CLASSE_CREDOR_PRESTADOR_SERVIDOR)
                    || classeCredorPessoa.getClasseCredor().getCodigo().equals(CODIGO_CLASSE_CREDOR_LOCACAO_VEICULO)
                    || classeCredorPessoa.getClasseCredor().getCodigo().equals(CODIGO_CLASSE_CREDOR_LOCACAO_MAQUINAS)) {
                    if (carteiraTrabalhos.isEmpty()) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório adicionar uma carteira de trabalho informando o campo PIS/PASEP para continuar.");
                    }
                }
            }
        }
        ve.lancarException();
    }

    public void adicionarEndereco() {
        Boolean controle = true;
        if (endereco.getCep() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo CEP é obrigatório para adicionar.");
            controle = false;
        }
        if (endereco.getUf().trim().equals("")) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Estado é obrigatório para adicionar.");
            controle = false;
        }
        if (endereco.getLocalidade().trim().equals("")) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Cidade é obrigatório para adicionar.");
            controle = false;
        }
        if (endereco.getBairro().trim().equals("")) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Bairro é obrigatório para adicionar.");
            controle = false;
        }
        if (endereco.getLogradouro().trim().equals("")) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Logradouro é obrigatório para adicionar.");
            controle = false;
        }
        if (controle) {
            selecionado.setEnderecos(Util.adicionarObjetoEmLista(selecionado.getEnderecos(), endereco));
            cep = new CEPLogradouro();
            endereco = new EnderecoCorreio();
        }
    }

    public void removeFone(ActionEvent evento) {
        Telefone t = (Telefone) evento.getComponent().getAttributes().get("vfone");
        t.setPessoa(null);
        selecionado.getTelefones().remove(t);
    }

    public void removeEndereco(ActionEvent evento) {
        selecionado.getEnderecos().remove((EnderecoCorreio) evento.getComponent().getAttributes().get("vendereco"));
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return pessoaFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte, "3", "ORCAMENTARIA", sistemaControlador.getDataOperacao());
    }

    public List<ClasseCredor> completaClasseCredor(String parte) {
        return pessoaFacade.getClasseCredorFacade().listaFiltrandoDescricao(parte.trim());
    }

    public List<SelectItem> getTiposEnderecos() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoEndereco t : TipoEndereco.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaOperacao() {
        List<SelectItem> listaDest = new ArrayList<>();
        listaDest.add(new SelectItem(null, ""));
        for (OperacaoClasseCredor ope : OperacaoClasseCredor.values()) {
            listaDest.add(new SelectItem(ope, ope.getDescricao()));
        }
        return listaDest;
    }

    public List<SelectItem> getTiposInscricao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(TipoInscricao.CNPJ, TipoInscricao.CNPJ.getDescricao()));
        toReturn.add(new SelectItem(TipoInscricao.CEI, TipoInscricao.CEI.getDescricao()));
        return toReturn;
    }

    public List<CEPLocalidade> completaCidade(String parte) {
        String l = "";
        if (endereco != null) {
            if (endereco.getUf() != null) {
                l = endereco.getUf();
            }
        }
        return pessoaFacade.getConsultaCepFacade().consultaLocalidades(l, parte.trim());
    }

    public List<CEPLogradouro> completaLogradouro(String parte) {
        String l = "";
        if (endereco != null) {
            if (endereco.getLocalidade() != null) {
                l = endereco.getLocalidade();
            }
        }
        return pessoaFacade.getConsultaCepFacade().consultaLogradouros(l, parte.trim());
    }

    public List<CEPBairro> completaBairro(String parte) {
        String l = "";
        if (endereco != null) {
            if (endereco.getLocalidade() != null) {
                l = endereco.getLocalidade();
            }
        }
        return pessoaFacade.getConsultaCepFacade().consultaBairros(l, parte.trim());
    }

    public List<String> completaCEP(String parte) {
        return pessoaFacade.getConsultaCepFacade().consultaLogradouroPorParteCEPByString(parte.trim());
    }

    public List<SelectItem> getListaCategoria() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (CategoriaHabilitacao ch : CategoriaHabilitacao.values()) {
            toReturn.add(new SelectItem(ch, ch.name()));
        }
        return toReturn;
    }

    public List<ContaCorrenteBancaria> completaContaCorrenteBancarias(String parte) {

        if (pessoaFiltro != null) {
            List<ContaCorrenteBancaria> contaCorrenteBancarias = contaCorrenteBancariaFacade.listaContasPorPessoaFiltrandoPorCodigo(pessoaFiltro, parte.trim());
            if (contaCorrenteBancarias != null && !contaCorrenteBancarias.isEmpty()) {
                return contaCorrenteBancarias;
            } else
                FacesUtil.addOperacaoNaoRealizada("A pessoa selecionada não possui conta bancária!");
        }
        return null;
    }

    public List<Pessoa> completarPessoa(String filtro) {

        return pessoaFacade.listaTodasPessoas(filtro.trim());
    }


    public List<SelectItem> getTiposFone() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoTelefone object : TipoTelefone.values()) {
            toReturn.add(new SelectItem(object, object.getTipoFone()));
        }
        return toReturn;
    }

    public List<SelectItem> getNacionalidades() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Nacionalidade object : pessoaFacade.getNacionalidadeFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoSituacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoSituacaoMilitar object : TipoSituacaoMilitar.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getCategoria() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (CategoriaCertificadoMilitar object : CategoriaCertificadoMilitar.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public void setaTelefone(ActionEvent evento) {
        if (telefone.getTelefone().isEmpty() || telefone.getPessoaContato() == null || telefone.getTipoFone() == null) {
            telefone.setTelefone("");
            telefone.setPessoaContato(selecionado.getNome());
            telefone.setTipoFone(TipoTelefone.RESIDENCIAL);
            telefone = ((Telefone) evento.getComponent().getAttributes().get("alteraFone"));
        } else {
            telefone = ((Telefone) evento.getComponent().getAttributes().get("alteraFone"));
        }
    }

    public void editaEndereco(ActionEvent evento) {
        endereco = (EnderecoCorreio) evento.getComponent().getAttributes().get("endereco");
    }

    public void editarContaCorrenteBancaria(ContaCorrenteBancPessoa contaCorrenteBancPessoa) {
        try {
            identificarContaSalarioOrContaNSGD(contaCorrenteBancPessoa);
            this.contaCorrenteBancPessoa = (ContaCorrenteBancPessoa) Util.clonarObjeto(contaCorrenteBancPessoa);
            contaCorrenteBancaria = contaCorrenteBancPessoa.getContaCorrenteBancaria();
            banco = contaCorrenteBancaria.getAgencia().getBanco();
            mudaPanelContaCorrenteBancaria = "novaConta";
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabViewGeral:banco_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public ConverterGenerico getConverterNacionalidade() {
        if (converterNacionalidade == null) {
            converterNacionalidade = new ConverterGenerico(Nacionalidade.class, pessoaFacade.getNacionalidadeFacade());
        }
        return converterNacionalidade;
    }

    public ConverterAutoComplete getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterAutoComplete(ClasseCredor.class, pessoaFacade.getClasseCredorFacade());
        }
        return converterClasseCredor;
    }

    public ConverterAutoComplete getConverterContaCorrenteBancaria() {
        if (converterContaCorrenteBancaria == null) {
            converterContaCorrenteBancaria = new ConverterAutoComplete(ContaCorrenteBancaria.class, contaCorrenteBancariaFacade);
        }
        return converterContaCorrenteBancaria;
    }

    public ConverterAutoComplete getConverterNaturalidade() {
        if (converterNaturalidade == null) {
            converterNaturalidade = new ConverterAutoComplete(Cidade.class, pessoaFacade.getCidadeFacade());
        }
        return converterNaturalidade;
    }

    public ConverterAutoComplete getConverterCidadeTitulo() {
        if (converterCidadeTitulo == null) {
            converterCidadeTitulo = new ConverterAutoComplete(Cidade.class, pessoaFacade.getCidadeFacade());
        }
        return converterCidadeTitulo;
    }

    public ConverterAutoComplete getConverterAgencia() {
        if (converterAgencia == null) {
            converterAgencia = new ConverterAutoComplete(Agencia.class, pessoaFacade.getAgenciaFacade());
        }
        return converterAgencia;
    }

    public ConverterGenerico getConverterUf() {
        if (converterUf == null) {
            converterUf = new ConverterGenerico(UF.class, pessoaFacade.getUfFacade());
        }
        return converterUf;
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, pessoaFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterHierarquiaOrganizacional;
    }

    public ConverterGenerico getConverterNivelEscolaridade() {
        if (converterNivelEscolaridade == null) {
            converterNivelEscolaridade = new ConverterGenerico(NivelEscolaridade.class, pessoaFacade.getNivelEscolaridadeFacade());
        }
        return converterNivelEscolaridade;
    }

    public List<Cidade> completaNaturalidade(String parte) {
        return pessoaFacade.getCidadeFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<SelectItem> getCidades() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Cidade object : pessoaFacade.getCidadeFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public List<SelectItem> getNivelEscolaridade() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (NivelEscolaridade object : pessoaFacade.getNivelEscolaridadeFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getEstados() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (UF object : pessoaFacade.getUfFacade().listaUFNaoNula()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public List<SelectItem> getSexo() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Sexo object : Sexo.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRacaCor() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (RacaCor object : RacaCor.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getEstadoCivil() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (EstadoCivil object : EstadoCivil.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getUf() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (UF object : pessoaFacade.getUfFacade().listaUFNaoNula()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacaoConta() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (SituacaoConta object : SituacaoConta.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoSanguineo() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoSanguineo object : TipoSanguineo.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<Agencia> completa(String parte) {
        return pessoaFacade.getAgenciaFacade().listaFiltrando(parte.trim(), "nomeAgencia", "numeroAgencia");
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

    private boolean validaEmail(String email) {
        if (email.isEmpty()) {
            return true;
        } else {
            Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
            Matcher m = p.matcher(email);
            return m.matches();
        }
    }

    public boolean validaCpf(String s_aux) {
        if (s_aux.isEmpty()) {
            return true;
        } else {
            s_aux = s_aux.replace(".", "");
            s_aux = s_aux.replace("-", "");
//------- Rotina para CPF
            if (s_aux.length() == 11) {
                int d1, d2;
                int digito1, digito2, resto;
                int digitoCPF;
                String nDigResult;
                d1 = d2 = 0;
                digito1 = digito2 = resto = 0;
                for (int n_Count = 1; n_Count < s_aux.length() - 1; n_Count++) {
                    digitoCPF = Integer.valueOf(s_aux.substring(n_Count - 1, n_Count)).intValue();
//--------- Multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4 e assim por diante.
                    d1 = d1 + (11 - n_Count) * digitoCPF;
//--------- Para o segundo digito repita o procedimento incluindo o primeiro digito calculado no passo anterior.
                    d2 = d2 + (12 - n_Count) * digitoCPF;
                }
                ;
//--------- Primeiro resto da divisão por 11.
                resto = (d1 % 11);
//--------- Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
                if (resto < 2) {
                    digito1 = 0;
                } else {
                    digito1 = 11 - resto;
                }
                d2 += 2 * digito1;
//--------- Segundo resto da divisão por 11.
                resto = (d2 % 11);
//--------- Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
                if (resto < 2) {
                    digito2 = 0;
                } else {
                    digito2 = 11 - resto;
                }
//--------- Digito verificador do CPF que está sendo validado.
                String nDigVerific = s_aux.substring(s_aux.length() - 2, s_aux.length());
//--------- Concatenando o primeiro resto com o segundo.
                nDigResult = String.valueOf(digito1) + String.valueOf(digito2);
//--------- Comparar o digito verificador do cpf com o primeiro resto + o segundo resto.
                return nDigVerific.equals(nDigResult);
            } //-------- Rotina para CNPJ
            else {
                return false;
            }
        }
    }

    public void setaUf(SelectEvent e) {
        endereco.setUf((String) e.getObject());
    }

    public void setaCidade(SelectEvent e) {
        endereco.setLocalidade((String) e.getObject());
    }

    public void atualizaLogradouros() {
        pessoaFacade.getConsultaCepFacade().atualizarLogradouros(endereco);
    }

    public void setaEnderecoComoPrincipal() {
        for (EnderecoCorreio enderecoCorreio : selecionado.getEnderecos()) {
            if (enderecoCorreio.getPrincipal()) {
                selecionado.setEnderecoPrincipal(enderecoCorreio);
            }
        }
        if (selecionado.getEnderecoPrincipal() == null) {
            if (!selecionado.getEnderecos().isEmpty()) {
                selecionado.setEnderecoPrincipal(selecionado.getEnderecos().get(0));
                selecionado.getEnderecos().get(0).setPrincipal(Boolean.TRUE);
            }
        }
    }

    public void setaTelefonePrincipal() {
        for (Telefone fone : selecionado.getTelefones()) {
            if (fone.getPrincipal()) {
                selecionado.setTelefonePrincipal(fone);
            }
        }
        if (selecionado.getTelefonePrincipal() == null) {
            if (!selecionado.getTelefones().isEmpty()) {
                selecionado.setTelefonePrincipal(selecionado.getTelefones().get(0));
                selecionado.getTelefones().get(0).setPrincipal(Boolean.TRUE);
            }
        }
    }

    private void setarEnderecoTelefoneContaCorrenteComoPrincipal() {
        setaEnderecoComoPrincipal();
        setaTelefonePrincipal();
    }

    @Override
    public void salvar() {
        try {
            setarEnderecoTelefoneContaCorrenteComoPrincipal();
            if (unidadeOrganizacionalSelecionada != null) {
                selecionado.setUnidadeOrganizacional(unidadeOrganizacionalSelecionada.getSubordinada());
            } else {
                selecionado.setUnidadeOrganizacional(null);
            }
            validarCampos();
            validarPF();
            validarPISPASEPaoSalvar();
            if (selecionado.getId() == null) {
                selecionado.adicionarHistoricoSituacaoCadastral();
                selecionado = pessoaFacade.salvarPessoaContabil(selecionado);
                FacesUtil.executaJavaScript("dialogImprimirFicha.show()");
            } else {
                pessoaFacade.salvarPessoaContabil(selecionado);
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " A pessoa " + selecionado + " foi alterada com sucesso.");
                redirecionaParaLista();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica eg) {
            FacesUtil.addOperacaoNaoRealizada(eg.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }


    private void validarPF() {
        if (selecionado instanceof PessoaFisica) {
            validarRG();
            novoRg();
            novoTitulo();
            novoMilitar();
            novoCertidaoNascimento();
            novoCertidaoCasamento();
        }
    }

    public void validarContaCorrenteBancaria() {
        ValidacaoException ve = new ValidacaoException();
        if (mudaPanelContaCorrenteBancaria.equals("contaExistente")) {
            if (pessoaFiltro == null) {
                ve.adicionarMensagemDeCampoObrigatorio(" O campo Pessoa é obrigatório para adicionar.");
            } else if (contaCorrenteBancaria == null) {
                ve.adicionarMensagemDeCampoObrigatorio(" O campo Conta Corrente Bancária é obrigatório ao adicionar.");
            }
        } else {
            if (contaCorrenteBancaria.getAgencia() == null) {
                ve.adicionarMensagemDeCampoObrigatorio(" O campo Agência é obrigatório para adicionar.");
            } else if (contaCorrenteBancaria.getNumeroConta().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio(" O campo Número da Conta é obrigatório para adicionar.");
            } else if (contaCorrenteBancaria.getDigitoVerificador().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio(" O campo Dígito Verificador é obrigatório para adicionar.");
            } else if (contaCorrenteBancaria.getModalidadeConta() == null) {
                ve.adicionarMensagemDeCampoObrigatorio(" O campo Tipo da Conta é obrigatório para adicionar.");
            }
        }
        for (ContaCorrenteBancPessoa conta : selecionado.getContaCorrenteBancPessoas()) {
            if (conta.getContaCorrenteBancaria().equals(contaCorrenteBancaria) && !contaCorrenteBancPessoa.equals(conta)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" A Conta Corrente Bancária: <b>" + contaCorrenteBancaria + "</b>, já foi adicionada na lista.");
            }
        }
        ve.lancarException();
    }

    public void cancelarContaBancariaPessoaFisica() {
        contaCorrenteBancPessoa = null;
    }

    public void adicionarContaBancariaPessoaFisica() {
        try {
            validarContaCorrenteBancaria();
            contaCorrenteBancPessoa.setContaCorrenteBancaria(contaCorrenteBancaria);
            contaCorrenteBancPessoa.setPessoa(selecionado);
            contaCorrenteBancPessoa.setDataRegistro(sistemaControlador.getDataOperacao());
            selecionado.setContaCorrenteBancPessoas(Util.adicionarObjetoEmLista(selecionado.getContaCorrenteBancPessoas(), contaCorrenteBancPessoa));
            cancelarContaBancariaPessoaFisica();
            contaCorrenteBancaria = new ContaCorrenteBancaria();
            banco = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void instanciarContaBancariaPessoa() {
        contaCorrenteBancPessoa = new ContaCorrenteBancPessoa();
    }

    public void adicionarContaBancariaPessoaJuridica() {
        try {
            validarContaCorrenteBancaria();
            contaCorrenteBancPessoa.setContaCorrenteBancaria(contaCorrenteBancaria);
            contaCorrenteBancPessoa.setPessoa(selecionado);
            contaCorrenteBancPessoa.setDataRegistro(sistemaControlador.getDataOperacao());
            if (!SituacaoConta.ATIVO.equals(contaCorrenteBancaria.getSituacao())) {
                contaCorrenteBancPessoa.setPrincipal(false);
            }

            Util.adicionarObjetoEmLista(selecionado.getContaCorrenteBancPessoas(), contaCorrenteBancPessoa);
            instanciarContaBancariaPessoa();
            contaCorrenteBancaria = new ContaCorrenteBancaria();
            setaPessoaJuridicaContaCorrenteBancaria();
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabViewGeral:agencia_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removeContaCorrenteBancPF(ActionEvent evento) {
        try {
            ContaCorrenteBancPessoa c = (ContaCorrenteBancPessoa) evento.getComponent().getAttributes().get("objeto");
            identificarContaSalarioOrContaNSGD(c);
            selecionado.getContaCorrenteBancPessoas().remove(c);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removeContaCorrenteBancPJ(ActionEvent evento) {
        try {
            ContaCorrenteBancPessoa c = (ContaCorrenteBancPessoa) evento.getComponent().getAttributes().get("objeto");
            identificarContaSalarioOrContaNSGD(c);
            selecionado.getContaCorrenteBancPessoas().remove(c);
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabViewGeral:agencia_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    public List<SelectItem> getTipoEmpresas() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoEmpresa t : TipoEmpresa.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> tiposEnderecos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoEndereco t : TipoEndereco.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    private void identificarContaSalarioOrContaNSGD(ContaCorrenteBancPessoa conta) {
        ValidacaoException ve = new ValidacaoException();
        if (ModalidadeConta.CONTA_SALARIO.equals(conta.getModalidade())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("<b>Conta Salário</b> só pode ser excluída e editada na Pessoa Perfil RH.");
        }
        ve.lancarException();
    }


    public List<CEPUF> completaUF(String parte) {
        return pessoaFacade.getConsultaCepFacade().consultaUf(parte.trim());
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (!Util.valida_CpfCnpj(String.valueOf(selecionado.getCpf_Cnpj()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" CPF ou CNPJ inválido.");
        } else {
            if (selecionado instanceof PessoaFisica) {
                if (pessoaFacade.hasOutraPessoaComMesmoCpf((PessoaFisica) selecionado, false)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(" Já existe um registro com este número de CPF.");
                }
            } else if (selecionado instanceof PessoaJuridica) {
                if (pessoaFacade.hasOutraPessoaComMesmoCnpj((PessoaJuridica) selecionado, false)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(" Já existe um registro com este número de CNPJ.");
                }
            }
        }
        ValidaPessoa.validar(selecionado, PerfilEnum.PERFIL_CREDOR, ve);
        ve.lancarException();
    }

    public void executaListenter() {
        preencheNomeReduzido();
        setaPessoaJuridicaContaCorrenteBancaria();
    }

    public void preencheNomeReduzido() {

        PessoaJuridica pj = (PessoaJuridica) selecionado;
        pj.setNomeReduzido(pj.getRazaoSocial());
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
            return "edita";
        } else if (selecionado instanceof PessoaJuridica) {
            return "editaJuridica";
        }
        return "";
    }

    public void setaPessoaFisicaContaCorrenteBancaria() {
        PessoaFisica pf = (PessoaFisica) selecionado;
        if (contaCorrenteBancPessoa != null) {
            contaCorrenteBancPessoa.setPessoa(pf);
        }
    }

    public void setaPessoaJuridicaContaCorrenteBancaria() {
        PessoaJuridica pj = (PessoaJuridica) selecionado;
        if (contaCorrenteBancPessoa != null) {
            contaCorrenteBancPessoa.setPessoa(pj);
        }
    }

    public List<SelectItem> classesPessoa() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (ClassePessoa cp : ClassePessoa.values()) {
            lista.add(new SelectItem(cp, cp.getDescricao()));
        }
        return lista;
    }

    public List<Banco> completarBancos(String filtro) {
        return pessoaFacade.getBancoFacade().listaBancoPorCodigoOuNome(filtro.trim());
    }

    public List<Agencia> completarAgencias(String parte) {
        if (banco != null) {
            return pessoaFacade.getAgenciaFacade().listaFiltrandoPorBanco(parte.trim(), banco);
        } else {
            return pessoaFacade.getAgenciaFacade().listaFiltrandoAtributosAgenciaBanco(parte.trim());
        }

    }

    public void limparAgencia() {
        contaCorrenteBancaria.setAgencia(null);
    }

    public boolean isBancoCaixa() {
        if (banco != null) {
            return "104".equals(banco.getNumeroBanco());
        } else {
            return false;
        }
    }

    public boolean isBancoBrasil() {
        if (banco != null) {
            return "001".equals(banco.getNumeroBanco());
        } else {
            return false;
        }
    }

    public List<SelectItem> getModalidades() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        toReturn.add(new SelectItem(ModalidadeConta.CONTA_CORRENTE, ModalidadeConta.CONTA_CORRENTE.getDescricao()));
        toReturn.add(new SelectItem(ModalidadeConta.CONTA_POUPANCA, ModalidadeConta.CONTA_POUPANCA.getDescricao()));
        if (isBancoCaixa() || isBancoBrasil()) {
            toReturn.add(new SelectItem(ModalidadeConta.CONTA_CAIXA_FACIL, ModalidadeConta.CONTA_CAIXA_FACIL.getDescricao()));
        }
        if (selecionado instanceof PessoaFisica) {
            toReturn.add(new SelectItem(ModalidadeConta.CONTA_SALARIO, ModalidadeConta.CONTA_SALARIO.getDescricao()));
            toReturn.add(new SelectItem(ModalidadeConta.CONTA_SALARIO_NSGD, ModalidadeConta.CONTA_SALARIO_NSGD.getDescricao()));
        } else {
            toReturn.add(new SelectItem(ModalidadeConta.ENTIDADES_PUBLICAS, ModalidadeConta.ENTIDADES_PUBLICAS.getDescricao()));
            toReturn.add(new SelectItem(ModalidadeConta.CONTA_POUPANCA_PESSOA_JURIDICA, ModalidadeConta.CONTA_POUPANCA_PESSOA_JURIDICA.getDescricao()));
            toReturn.add(new SelectItem(ModalidadeConta.CONTA_SALARIO_NSGD, ModalidadeConta.CONTA_SALARIO_NSGD.getDescricao()));
        }
        return toReturn;
    }

    public Integer maxLengthNumeroConta() {
        return contaCorrenteBancaria != null && ModalidadeConta.CONTA_SALARIO_NSGD.equals(contaCorrenteBancaria.getModalidadeConta()) ? 12 : 10;
    }

    public void geraFichaPessoa() {
        try {
            List<ParametrosRelatorios> parametros = Lists.newArrayList();
            parametros.add(new ParametrosRelatorios("p.id", ":idPessoa", null, OperacaoRelatorio.IGUAL, selecionado.getId(), null, 1, false));
            if (selecionado instanceof PessoaFisica) {
                RelatorioPessoaFContabilControlador.gerarRelatorio(parametros, sistemaControlador.getUsuarioCorrente());
            } else {
                RelatorioPessoaJContabilControlador.gerarRelatorio(parametros, sistemaControlador.getUsuarioCorrente());
            }
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void redirecionaParaLista() {
        if (selecionado instanceof PessoaFisica) {
            FacesUtil.redirecionamentoInterno("/pessoa-fisica/listar/");
        } else {
            FacesUtil.redirecionamentoInterno("/pessoa-juridica/listar/");
        }
    }

    public void cancelaImprimirFicha() {
        FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " A pessoa " + selecionado + " foi salva com sucesso.");
        redirecionaParaLista();
    }

    @Override
    public RevisaoAuditoria getUltimaRevisao() {
        if (ultimaRevisao == null) {
            ultimaRevisao = buscarUltimaAuditoria();
            RevisaoAuditoria revisaoAuditoria = buscarUltimaAuditoria(selecionado.isPessoaFisica() ? PessoaFisica.class : PessoaJuridica.class, selecionado.getId());
            if (ultimaRevisao == null || (revisaoAuditoria != null && revisaoAuditoria.getDataHora().after(ultimaRevisao.getDataHora()))) {
                ultimaRevisao = revisaoAuditoria;
            }
        }
        return ultimaRevisao;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public SituacaoMilitar getSituacaoMilitar() {
        return situacaoMilitar;
    }

    public void setSituacaoMilitar(SituacaoMilitar situacaoMilitar) {
        this.situacaoMilitar = situacaoMilitar;
    }

    public Habilitacao getHabilitacao() {
        return habilitacao;
    }

    public void setHabilitacao(Habilitacao habilitacao) {
        this.habilitacao = habilitacao;
    }

    public EnderecoCorreio getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoCorreio endereco) {
        this.endereco = endereco;
    }

    public ContaCorrenteBancaria getContaCorrenteBancaria() {
        return contaCorrenteBancaria;
    }

    public void setContaCorrenteBancaria(ContaCorrenteBancaria contaCorrenteBancaria) {
        this.contaCorrenteBancaria = contaCorrenteBancaria;
    }

    public CEPLogradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(CEPLogradouro logradouro) {
        this.logradouro = logradouro;
    }

    public CertidaoNascimento getCertidaoNascimento() {
        return certidaoNascimento;
    }

    public void setCertidaoNascimento(CertidaoNascimento certidaoNascimento) {
        this.certidaoNascimento = certidaoNascimento;
    }

    public CertidaoCasamento getCertidaoCasamento() {
        return certidaoCasamento;
    }

    public void setCertidaoCasamento(CertidaoCasamento certidaoCasamento) {
        this.certidaoCasamento = certidaoCasamento;
    }

    public CarteiraTrabalho getCarteiraTrabalho() {
        return carteiraTrabalho;
    }

    public void setCarteiraTrabalho(CarteiraTrabalho carteiraTrabalho) {
        this.carteiraTrabalho = carteiraTrabalho;
    }

    public RG getRg() {
        return rg;
    }

    public void setRg(RG rg) {
        this.rg = rg;
    }

    public ContaCorrenteBancPessoa getContaCorrenteBancPessoa() {
        return contaCorrenteBancPessoa;
    }

    public void setContaCorrenteBancPessoa(ContaCorrenteBancPessoa contaCorrenteBancPessoa) {
        this.contaCorrenteBancPessoa = contaCorrenteBancPessoa;
    }

    public HierarquiaOrganizacional getUnidadeOrganizacionalSelecionada() {
        return unidadeOrganizacionalSelecionada;
    }

    public void setUnidadeOrganizacionalSelecionada(HierarquiaOrganizacional unidadeOrganizacionalSelecionada) {
        this.unidadeOrganizacionalSelecionada = unidadeOrganizacionalSelecionada;
    }

    public ClasseCredorPessoa getClasseCredorSelecionada() {
        return classeCredorSelecionada;
    }

    public void setClasseCredorSelecionada(ClasseCredorPessoa classeCredorSelecionada) {
        this.classeCredorSelecionada = classeCredorSelecionada;
    }

    public TituloEleitor getTituloEleitor() {
        return tituloEleitor;
    }

    public void setTituloEleitor(TituloEleitor tituloEleitor) {
        this.tituloEleitor = tituloEleitor;
    }

    public Telefone getTelefone() {
        return telefone;
    }

    public void setTelefone(Telefone telefone) {
        this.telefone = telefone;
    }

    public String getMudaPanelContaCorrenteBancaria() {
        return mudaPanelContaCorrenteBancaria;
    }

    public void setMudaPanelContaCorrenteBancaria(String mudaPanelContaCorrenteBancaria) {
        this.mudaPanelContaCorrenteBancaria = mudaPanelContaCorrenteBancaria;
    }

    public CEPLogradouro getCep() {
        return cep;
    }

    public void setCep(CEPLogradouro cep) {
        this.cep = cep;
    }

    public List<CarteiraTrabalho> getCarteiraTrabalhos() {
        return carteiraTrabalhos;
    }

    public void setCarteiraTrabalhos(List<CarteiraTrabalho> carteiraTrabalhos) {
        this.carteiraTrabalhos = carteiraTrabalhos;
    }

    public List<Habilitacao> getHabilitacoes() {
        return habilitacoes;
    }

    public void setHabilitacoes(List<Habilitacao> habilitacoes) {
        this.habilitacoes = habilitacoes;
    }

    public List<CEPLogradouro> getLogradouros() {
        return logradouros;
    }

    public void setLogradouros(List<CEPLogradouro> logradouros) {
        this.logradouros = logradouros;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public Pessoa getPessoaFiltro() {
        return pessoaFiltro;
    }

    public void setPessoaFiltro(Pessoa pessoaFiltro) {
        this.pessoaFiltro = pessoaFiltro;
    }

    public List<VOSolicitacaoCadastroPessoa> getSolicitacoesPendentes() {
        return solicitacoesPendentes;
    }

    public void setSolicitacoesPendentes(List<VOSolicitacaoCadastroPessoa> solicitacoesPendentes) {
        this.solicitacoesPendentes = solicitacoesPendentes;
    }

    public SolicitacaoCadastroPessoa getSolicitacaoCadastroPessoa() {
        return solicitacaoCadastroPessoa;
    }

    public void setSolicitacaoCadastroPessoa(SolicitacaoCadastroPessoa solicitacaoCadastroPessoa) {
        this.solicitacaoCadastroPessoa = solicitacaoCadastroPessoa;
    }

    public void aprovarCadastro() {
        try {
            validarCampos();
            validarPISPASEPaoSalvar();
            pessoaFacade.confirmarAprovacaoSolicitacaoCadastroPessoa(selecionado);
            FacesUtil.addOperacaoRealizada("Cadastro Aprovado sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex.getCause());
        }
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

    public void confirmarRejeicaoCadastroPessoa() {
        try {
            validarRejeicaoCadastroPessoa();
            pessoaFacade.confirmarRejeicaoSolicitacaoCadastroPessoa(selecionado, solicitacaoCadastroPessoa, TipoTemplateEmail.REJEICAO_CADASTRO_CREDOR);
            FacesUtil.addOperacaoRealizada("Envio de rejeição enviado para " + solicitacaoCadastroPessoa.getEmail());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
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

    private void atualizarCanAprovarRejeitarSolicitacaoCadastro() {
        canAprovarRejeitarSolicitacao = false;
        if (solicitacaoCadastroPessoa != null) {
            DAM damPago = pessoaFacade.getSolicitacaoCadastroPessoaFacade().buscarDamPorKeySolicitacaoESituacoesDaParcela(solicitacaoCadastroPessoa.getKey(), SituacaoParcela.getSituacoesPagas());
            if (getConfiguracaoPortalContribuinte().getPermissoesAprovacaoCredores() != null &&
                !getConfiguracaoPortalContribuinte().getPermissoesAprovacaoCredores().isEmpty() &&
                damPago != null) {
                for (UsuarioPermissaoAprovar usuarioPermitido : getConfiguracaoPortalContribuinte().getPermissoesAprovacaoCredores()) {
                    if (usuarioPermitido.getUsuarioSistema().equals(sistemaControlador.getUsuarioCorrente())) {
                        canAprovarRejeitarSolicitacao = true;
                        break;
                    }
                }
            }
        }
    }

    public boolean isDamPago(String key) {
        return pessoaFacade.getSolicitacaoCadastroPessoaFacade().buscarDamPorKeySolicitacaoESituacoesDaParcela(key, SituacaoParcela.getSituacoesPagas()) != null;
    }

    public ConfiguracaoPortalContribuinte getConfiguracaoPortalContribuinte() {
        if (configuracaoPortalContribuinte == null) {
            configuracaoPortalContribuinte = pessoaFacade.getConfiguracaoPortalContribuinteFacade().buscarUtilmo();
        }
        return configuracaoPortalContribuinte;
    }

    public void setConfiguracaoPortalContribuinte(ConfiguracaoPortalContribuinte configuracaoPortalContribuinte) {
        this.configuracaoPortalContribuinte = configuracaoPortalContribuinte;
    }

    public boolean getCanAprovarRejeitarSolicitacao() {
        return canAprovarRejeitarSolicitacao;
    }

    public void setCanAprovarRejeitarSolicitacao(boolean canAprovarRejeitarSolicitacao) {
        this.canAprovarRejeitarSolicitacao = canAprovarRejeitarSolicitacao;
    }
}

