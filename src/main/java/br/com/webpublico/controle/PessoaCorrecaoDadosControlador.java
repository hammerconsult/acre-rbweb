package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Fabio
 */
@ManagedBean(name = "pessoaCorrecaoDadosControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaPessoaFisicaCorrecao", pattern = "/pessoa-correcao-de-dados/nova-fisica/", viewId = "/faces/tributario/cadastromunicipal/pessoacorrecaodados/edita.xhtml"),
    @URLMapping(id = "novaPessoaJuridicaCorrecao", pattern = "/pessoa-correcao-de-dados/nova-juridica/", viewId = "/faces/tributario/cadastromunicipal/pessoacorrecaodados/editaJuridica.xhtml"),
    @URLMapping(id = "editarPessoaFisicaCorrecao", pattern = "/pessoa-correcao-de-dados/editar-fisica/#{pessoaCorrecaoDadosControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/pessoacorrecaodados/edita.xhtml"),
    @URLMapping(id = "editarPessoaJuridicaCorrecao", pattern = "/pessoa-correcao-de-dados/editar-juridica/#{pessoaCorrecaoDadosControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/pessoacorrecaodados/editaJuridica.xhtml"),
    @URLMapping(id = "verPessoaFisicaCorrecao", pattern = "/pessoa-correcao-de-dados/ver-fisica/#{pessoaCorrecaoDadosControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/pessoacorrecaodados/visualizar.xhtml"),
    @URLMapping(id = "verPessoaJuridicaCorrecao", pattern = "/pessoa-correcao-de-dados/ver-juridica/#{pessoaCorrecaoDadosControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/pessoacorrecaodados/visualizarJuridica.xhtml"),
    @URLMapping(id = "listarPessoaCorrecao", pattern = "/pessoa-correcao-de-dados/listar/", viewId = "/faces/tributario/cadastromunicipal/pessoacorrecaodados/lista.xhtml")
})
public class PessoaCorrecaoDadosControlador extends PrettyControlador<Pessoa> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(PessoaCorrecaoDadosControlador.class);

    @EJB
    private PessoaFacade pessoaFacade;
    private ConverterGenerico converterCidade;
    private ConverterAutoComplete converterCep;
    protected ConverterGenerico converterUf;
    private List<RG> rgs;
    private RG rg;
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
    private FileUploadEvent fileUploadEvent;
    private Arquivo arquivo;
    private StreamedContent imagemFoto;

    public PessoaCorrecaoDadosControlador() {
        super();
    }

    @Override
    public AbstractFacade getFacede() {
        return pessoaFacade;
    }

    @URLAction(mappingId = "novaPessoaFisicaCorrecao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
        telefoneAux = new Telefone();
        pf.setContaCorrenteBancPessoas(new ArrayList<ContaCorrenteBancPessoa>());
        arquivo = new Arquivo();
        fileUploadEvent = null;
        carregaNovaImagem();
    }

    @URLAction(mappingId = "novaPessoaJuridicaCorrecao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
    }

    public List<SelectItem> getTipoEmpresas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoEmpresa t : TipoEmpresa.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    private String getCaminhoImagens() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String caminho = ((ServletContext) facesContext.getExternalContext().getContext()).getRealPath("/css/images/");
        caminho += "/";
        return caminho;
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
        Boolean retorno = true;
        if (selecionado != null) {
            for (Telefone fone : selecionado.getTelefones()) {
                if (fone.getPrincipal()) {
                    retorno = false;
                }
            }
        }
        return retorno;
    }

    public void addDocumentoPessoa(DocumentoPessoal dp) {
        PessoaFisica pf = (PessoaFisica) selecionado;
        pf.getDocumentosPessoais().add(dp);
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

    public List<Telefone> getTelefones() {
        return this.selecionado.getTelefones();
    }

    public List<EnderecoCorreio> getEnderecos() {
        return selecionado.getEnderecos();
    }

    public void novoFone() {
        if (validaTelefone()) {
            telefone.setPessoa((PessoaFisica) selecionado);
            selecionado.getTelefones().add(telefone);
            telefone = new Telefone();
        }
    }

    private boolean validaTelefone() {
        boolean retorno = true;
        if (this.telefone.getTelefone().trim().isEmpty()) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O número do telefone deve ser informado!");
            retorno = false;
        }
        return retorno;
    }

    public void limparTelefones() {
        telefone = new Telefone();
    }

    public void novoFonePJ() {
        if (validaTelefone()) {
            telefone.setPessoa((PessoaJuridica) selecionado);
            selecionado.getTelefones().add(telefone);
            telefone = new Telefone();
        }
    }

    public boolean novoRg() {
        boolean retorno = true;
        if (rg != null && !"".equals(rg.getNumero())) {
            PessoaFisica pf = (PessoaFisica) selecionado;
            rg.setDataRegistro(new Date());
            rg.setPessoaFisica(pf);
            if (pf.getDocumentosPessoais().contains(rg)) {
                pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(rg), rg);
            } else {
                pf.getDocumentosPessoais().add(rg);
            }

        }
        return retorno;
    }

    public void removeRg(ActionEvent evento) {
        PessoaFisica pf = (PessoaFisica) selecionado;
        pf.getDocumentosPessoais().remove((RG) evento.getComponent().getAttributes().get("removeRg"));
        rgs.remove(0);
        rg = new RG();
    }

    private boolean validaEndereco() {
        boolean retorno = true;
        if (endereco == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Erro ao adicionar um endereço, tente novamente!");
            endereco = new EnderecoCorreio();
            retorno = false;
        } else {
            if (endereco.getLocalidade() == null || "".equals(endereco.getLocalidade().trim())) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe a cidade");
                retorno = false;
            }
            if (endereco.getBairro() == null || "".equals(endereco.getBairro().trim())) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe o bairro");
                retorno = false;
            }
            if (endereco.getLogradouro() == null || "".equals(endereco.getLocalidade().trim())) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe o logradouro");
                retorno = false;
            }
        }
        return retorno;
    }

    public void novoEndereco() {
        if (validaEndereco()) {
            selecionado.getEnderecos().add(endereco);
            endereco = new EnderecoCorreio();
        }
    }

    public void limparCamposEndereco() {
        endereco = new EnderecoCorreio();
    }

    public void removeFone(ActionEvent evento) {
        selecionado.getTelefones().remove((Telefone) evento.getComponent().getAttributes().get("vfone"));
    }

    public void removeEndereco(ActionEvent evento) {
        selecionado.getEnderecos().remove((EnderecoCorreio) evento.getComponent().getAttributes().get("vendereco"));
    }

    public PessoaFacade getFacade() {
        return pessoaFacade;
    }

    public List<SelectItem> getTiposFone() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoTelefone object : TipoTelefone.values()) {
            toReturn.add(new SelectItem(object, object.getTipoFone()));
        }
        return toReturn;
    }

    public void setaEndereco(ActionEvent evento) {
        setEnderecoAux((Endereco) evento.getComponent().getAttributes().get("alteraEndereco"));
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

    public List<SelectItem> getCidades() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Cidade object : pessoaFacade.getCidadeFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public List<SelectItem> getEstados() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (UF object : pessoaFacade.getUfFacade().listaUFNaoNula()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public List<SelectItem> getSexo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Sexo object : Sexo.values()) {
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

    @Override
    public void editar() {
        super.editar();
        //System.out.println("Selecionado...: " + selecionado);
        if (selecionado.getPerfis() != null) {
            if (!selecionado.getPerfis().contains(PerfilEnum.PERFIL_TRIBUTARIO)) {
                selecionado.getPerfis().add(PerfilEnum.PERFIL_TRIBUTARIO);
            }
        }
        enderecoAux = new Endereco();
        telefoneAux = new Telefone();
        telefone = new Telefone();
        endereco = new EnderecoCorreio();
        enderecoAux = new Endereco();
    }

    @URLAction(mappingId = "editarPessoaFisicaCorrecao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarPessoaFisica() {
        this.updateMetaData(PessoaFisica.class);
        editar();
        selecionado = pessoaFacade.recuperarPF(selecionado.getId());
        arquivo = new Arquivo();
        fileUploadEvent = null;
        if (((PessoaFisica) selecionado).getArquivo() != null) {
            arquivo = ((PessoaFisica) selecionado).getArquivo();
        }

        if (selecionado instanceof PessoaFisica) {
            if (verificaRG() == null) {
                rg = new RG();
                rg.setDataRegistro(new Date());
                rg.setPessoaFisica((PessoaFisica) selecionado);
                addDocumentoPessoa(rg);
            }

            for (DocumentoPessoal doc : ((PessoaFisica) selecionado).getDocumentosPessoais()) {
                if (doc instanceof RG) {
                    rg = (RG) doc;
                }
            }
            carregaFoto();
        }
    }

    @URLAction(mappingId = "editarPessoaJuridicaCorrecao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarPessoaJuridica() {
        this.updateMetaData(PessoaJuridica.class);
        editar();
        selecionado = pessoaFacade.recuperarPJ(selecionado.getId());
    }

    @URLAction(mappingId = "verPessoaFisicaCorrecao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verPessoaFisica() {
        editarPessoaFisica();
    }

    @URLAction(mappingId = "verPessoaJuridicaCorrecao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verPessoaJuridica() {
        editarPessoaJuridica();
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

    public List<SelectItem> tiposEnderecos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoEndereco t : TipoEndereco.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public List<CEPUF> completaUF(String parte) {
        return pessoaFacade.getConsultaCepFacade().consultaUf(parte.trim());
    }

    public void setaUf(SelectEvent e) {
        endereco.setUf((String) e.getObject());
    }

    public void setaCidade(SelectEvent e) {
        endereco.setLocalidade((String) e.getObject());
    }

    public List<CEPLocalidade> completaCidade(String parte) {
        String l = "";
        if (endereco.getUf() != null) {
            l = endereco.getUf();
        }
        return pessoaFacade.getConsultaCepFacade().consultaLocalidades(l, parte.trim());
    }

    public List<CEPLogradouro> completaLogradouro(String parte) {
        String l = "";
        if (endereco.getLocalidade() != null) {
            l = endereco.getLocalidade();
        }
        return pessoaFacade.getConsultaCepFacade().consultaLogradouros(l, parte.trim());
    }

    public List<CEPBairro> completaBairro(String parte) {
        String l = "";
        if (endereco.getLocalidade() != null) {
            l = endereco.getLocalidade();
        }
        return pessoaFacade.getConsultaCepFacade().consultaBairros(l, parte.trim());
    }


    public ConverterAutoComplete getConverterCep() {
        if (converterCep == null) {
            converterCep = new ConverterAutoComplete(CEPLogradouro.class, pessoaFacade.getCepLogradouroFacade());
        }
        return converterCep;
    }


    public List<CEPLogradouro> completaCEPs(String parte) {
        return pessoaFacade.getConsultaCepFacade().consultaLogradouroPorParteCEP(parte.trim());
    }

    public List<CEPLogradouro> completaCEP(String parte) {
        String logradouro = "";
        String cidade = "";
        if (endereco.getLogradouro() != null) {
            logradouro = endereco.getLogradouro();
        }
        if (endereco.getLocalidade() != null) {
            cidade = endereco.getLocalidade();
        }
        return pessoaFacade.getConsultaCepFacade().consultaCEPs(logradouro, cidade, parte.trim());
    }

    public void atualizarLogradouros() {
        pessoaFacade.getConsultaCepFacade().atualizarLogradouros(endereco);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/pessoa-correcao-de-dados/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        boolean validouRg = true;
        if (selecionado instanceof PessoaFisica) {
            validouRg = novoRg();
        }
        if (validaCampos() && validouRg) {
            if (operacao == Operacoes.NOVO) {
                selecionado.adicionarHistoricoSituacaoCadastral();
                if (selecionado instanceof PessoaJuridica) {
                    pessoaFacade.salvarNovo(selecionado);
                } else {
                    pessoaFacade.salvarNovo(((PessoaFisica) selecionado), arquivo, fileUploadEvent);
                }
            } else {
                if (selecionado instanceof PessoaJuridica) {
                    pessoaFacade.salvar(selecionado, null);
                } else {
                    pessoaFacade.salvar(((PessoaFisica) selecionado), arquivo, fileUploadEvent, null);
                }
            }

            if (selecionado instanceof PessoaFisica) {
                carregaFoto();
            }
            FacesUtil.addInfo("Salvo com Sucesso!", "");
            redireciona();
        }
    }

    public PerfilEnumConverter getPerfilEnumConveter() {
        return perfilEnumConveter;
    }

    public void setPerfilEnumConveter(PerfilEnumConverter perfilEnumConveter) {
        this.perfilEnumConveter = perfilEnumConveter;
    }

    public List<SelectItem> getSituacaoCadastral() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        for (SituacaoCadastralPessoa p : SituacaoCadastralPessoa.values()) {
            lista.add(new SelectItem(p, p.getDescricao()));
        }
        return lista;
    }

    @Override
    public void cancelar() {
        carregaFoto();
        super.cancelar();
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

    private int inicio = 0;

    public void carregaFoto() {
        if (selecionado instanceof PessoaFisica) {
            Arquivo arq = ((PessoaFisica) selecionado).getArquivo();
            if (arq != null) {
                try {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                    for (ArquivoParte a : pessoaFacade.getArquivoFacade().recuperaDependencias(arq.getId()).getPartes()) {
                        buffer.write(a.getDados());
                    }

                    InputStream teste = new ByteArrayInputStream(buffer.toByteArray());
                    imagemFoto = new DefaultStreamedContent(teste, arq.getMimeType());
                } catch (Exception ex) {
                    logger.error("Erro: ", ex);
                }
            }
        }
    }

    public List<SelectItem> getTiposInscricao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(TipoInscricao.CNPJ, TipoInscricao.CNPJ.getDescricao()));
        toReturn.add(new SelectItem(TipoInscricao.CEI, TipoInscricao.CEI.getDescricao()));
        return toReturn;
    }

    public List<SelectItem> classesPessoa() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (ClassePessoa cp : ClassePessoa.values()) {
            lista.add(new SelectItem(cp, cp.getDescricao()));
        }
        return lista;
    }

    public List<PessoaFisica> getListaEspecial() {
        return pessoaFacade.getPessoaFisicaFacade().listaFiltrandoPorTipoPerfil("", PerfilEnum.PERFIL_ESPECIAL);
    }

    public void carregaNovaImagem() {
        try {
            File f = new File(getCaminhoImagens() + "semfoto.jpg");
            InputStream ip = new FileInputStream(f);
            imagemFoto = new DefaultStreamedContent(ip);
        } catch (Exception e) {
            FacesUtil.addError("", "Não foi possível carregar a imagem");
        }
    }

    public Boolean validaCampos() {
        boolean valida = true;
        if (!"".equals(selecionado.getCpf_Cnpj()) && selecionado.getCpf_Cnpj() != null) {
            if (!Util.valida_CpfCnpj(String.valueOf(selecionado.getCpf_Cnpj()))) {
                if (selecionado instanceof PessoaFisica) {
                    FacesUtil.addError("Impossível Salvar", "CPF " + selecionado.getCpf_Cnpj() + " inválido!");
                    valida = false;
                } else if (selecionado instanceof PessoaJuridica) {
                    FacesUtil.addError("Impossível Salvar", "CNPJ " + selecionado.getCpf_Cnpj() + " inválido!");
                    valida = false;
                }
            } else {
                if (selecionado instanceof PessoaFisica) {
                    if (pessoaFacade.hasOutraPessoaComMesmoCpf((PessoaFisica) selecionado, false)) {
                        FacesUtil.addError("Impossível Salvar", "Já existe um registro com este número de CPF!");
                        valida = false;
                    }
                } else if (selecionado instanceof PessoaJuridica) {
                    if (pessoaFacade.hasOutraPessoaComMesmoCnpj((PessoaJuridica) selecionado, false)) {
                        FacesUtil.addError("Impossível Salvar", "Já existe um registro com este número de CNPJ!");
                        valida = false;
                    }
                }
            }
        }
        if ("".equals(selecionado.getNome()) || selecionado.getNome() == null) {
            if (selecionado instanceof PessoaFisica) {
                FacesUtil.addError("Impossível Salvar", "Favor informar o nome!");
            } else if (selecionado instanceof PessoaJuridica) {
                FacesUtil.addError("Impossível Salvar", "Favor informar a razão social!");
            }
            valida = false;
        }
        return valida;
    }

    public void excluir() {
        try {
            if (selecionado instanceof PessoaFisica) {
                pessoaFacade.getPessoaFisicaFacade().remover((PessoaFisica) selecionado);
            } else {
                pessoaFacade.getPessoaJuridicaFacade().remover((PessoaJuridica) selecionado);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Excluído com sucesso!", "O registro selecionado foi excluído com sucesso!"));
            redireciona();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não é possível excluir essa pessoa!", ""));
        }
    }

    public void validaCpfRh() {
        if (!((PessoaFisica) selecionado).getCpf().equals("")) {
            if (!validaCpf(((PessoaFisica) selecionado).getCpf())) {
                FacesUtil.addWarn("Atenção!", "O CPF digitado é inválido");
            } else if (!pessoaFacade.hasOutraPessoaComMesmoCpf((PessoaFisica) selecionado, false)) {
                FacesUtil.addWarn("Atenção!", "O CPF digitado já pertence a outra pessoa física !");
            }
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

    public String hrefVerPessoa(String cpfCnpj) {
        if (cpfCnpj != null && cpfCnpj.length() <= 14) {
            return "../ver-fisica/";
        } else {
            return "../ver-juridica/";
        }
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
}
