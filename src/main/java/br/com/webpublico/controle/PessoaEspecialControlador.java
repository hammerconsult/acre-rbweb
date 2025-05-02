/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlInputText;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ManagedBean(name = "pessoaEspecialControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoPessoaEspecial", pattern = "/pessoaespecial/cpf/", viewId = "/faces/admin/pessoa/cpf.xhtml"),
        @URLMapping(id = "novoPessoaEspecialCpf", pattern = "/pessoaespecial/novo/#{pessoaEspecialControlador.cpf}/", viewId = "/faces/admin/pessoa/edita.xhtml"),
        @URLMapping(id = "editarPessoaEspecial", pattern = "/pessoaespecial/editar/#{pessoaEspecialControlador.id}/", viewId = "/faces/admin/pessoa/edita.xhtml"),
        @URLMapping(id = "listarPessoaEspecial", pattern = "/pessoaespecial/listar/", viewId = "/faces/admin/pessoa/listaespecial.xhtml"),
        @URLMapping(id = "verPessoaEspecial", pattern = "/pessoaespecial/ver/#{pessoaEspecialControlador.id}/", viewId = "/faces/admin/pessoa/visualizar.xhtml")
})
public class PessoaEspecialControlador extends PrettyControlador<PessoaFisica> implements Serializable, CRUD {

    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    private ConverterAutoComplete converterClasseCredor;
    private ConverterGenerico converterCidade;
    private ConverterAutoComplete converterNaturalidade;
    private ConverterAutoComplete converterUnidadeExterna;
    private ConverterGenerico converterUf;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private EnderecoCorreio endereco;
    private CEPLogradouro logradouro;
    private RG rg;
    private HierarquiaOrganizacional unidadeOrganizacionalSelecionada;
    private ClasseCredorPessoa classeCredorSelecionada;
    private Telefone telefone;
    private List<RG> rgs;
    private List<CEPLogradouro> logradouros;
    private List<ClasseCredorPessoa> listaClasseCredorPessoa;
    private String complemento;
    private String numero;
    private HtmlInputText textocep;
    private br.com.webpublico.util.PerfilEnumConverter perfilEnumConveter = new PerfilEnumConverter();
    private String cpf;
    private String filtro;
    private List<EnderecoCorreio> enderecos;
    private List<Telefone> telefones;



    public void novaPessoa() {
        selecionado = new PessoaFisica();
        operacao = Operacoes.NOVO;
        selecionado.setPerfis(new ArrayList<PerfilEnum>());
        selecionado.getPerfis().add(PerfilEnum.PERFIL_ESPECIAL);
        selecionado.setTelefones(new ArrayList<Telefone>());
        selecionado.setEnderecos(new ArrayList<EnderecoCorreio>());
        selecionado.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);
        selecionado.setClassePessoa(ClassePessoa.EXTRA);
        telefone = new Telefone();
        endereco = new EnderecoCorreio();
        rg = new RG();
        rg.setDataRegistro(SistemaFacade.getDataCorrente());
        rg.setPessoaFisica(selecionado);
        unidadeOrganizacionalSelecionada = new HierarquiaOrganizacional();
        classeCredorSelecionada = new ClasseCredorPessoa();
        listaClasseCredorPessoa = new ArrayList<ClasseCredorPessoa>();
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, pessoaFisicaFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterHierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        boolean temUnidade = false;
        List<HierarquiaOrganizacional> lista = pessoaFisicaFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte, "3", TipoHierarquiaOrganizacional.ORCAMENTARIA, getSistemaControlador().getExercicioCorrente());
        if (unidadeOrganizacionalSelecionada != null) {
            for (HierarquiaOrganizacional h : lista) {
                if (h.getSubordinada().equals(unidadeOrganizacionalSelecionada.getSubordinada())) {
                    temUnidade = true;
                }
            }
            if (temUnidade == true) {
                return lista;
            } else {
                String str = unidadeOrganizacionalSelecionada.getSubordinada().getDescricao() + unidadeOrganizacionalSelecionada.getCodigo();
                if (str.toLowerCase().contains(parte.toLowerCase())) {
                    lista.add(unidadeOrganizacionalSelecionada);
                }
                return lista;
            }
        } else {
            return lista;
        }
    }

    public void adicionaClasseCredor() {
        if (selecionado.getClasseCredorPessoas().contains(classeCredorSelecionada)) {
            FacesUtil.addWarn("A Classe Credora já existe na lista!", "");
        } else if (classeCredorSelecionada.getClasseCredor() == null) {
            FacesUtil.addError("O campo Classe do Credor é obrigatório!", "");
        } else {
            classeCredorSelecionada.setPessoa(selecionado);
            selecionado.getClasseCredorPessoas().add(classeCredorSelecionada);
            classeCredorSelecionada = new ClasseCredorPessoa();
        }
    }

    public void validaCpfRh() {
        if (!validaCpf(selecionado.getCpf())) {
            FacesUtil.addWarn("Atenção!", "CPF inválido.");
        } else if (pessoaFisicaFacade.getPessoaFacade().hasOutraPessoaComMesmoCpf(selecionado, false)) {
            FacesUtil.addWarn("Atenção!", "O CPF informado já pertence a outra pessoa.");
        }
    }

    public void removeClasse(ActionEvent evt) {
        ClasseCredorPessoa ccp = (ClasseCredorPessoa) evt.getComponent().getAttributes().get("objeto");
        selecionado.getClasseCredorPessoas().remove(ccp);
    }

    public List<ClasseCredor> completaClasseCredor(String parte) {
        return pessoaFisicaFacade.getClasseCredorFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public List<UnidadeExterna> completaUnidadeExterna(String parte) {
        return pessoaFisicaFacade.getUnidadeExternaFacade().listaFiltrandoPessoaJuridicaEsferaGoverno(parte.trim());
    }

    public ConverterAutoComplete getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterAutoComplete(ClasseCredor.class, pessoaFisicaFacade.getClasseCredorFacade());
        }
        return converterClasseCredor;
    }

    public boolean liberaContaPrincipal() {
        boolean controle = false;
        if (selecionado != null) {
            for (ContaCorrenteBancPessoa ccbf : selecionado.getContaCorrenteBancPessoas()) {
                if (ccbf.getPrincipal()) {
                    controle = true;
                }
            }
        }
        return controle;
    }

    public Boolean liberaEnderecoPrincipal() {
        Boolean controle = true;
        if (this.enderecos != null) {
            for (EnderecoCorreio ec : this.enderecos) {
                if (ec.getPrincipal()) {
                    controle = false;
                }
            }
        }
        return controle;
    }

    public Boolean liberaTelefonePrincipal() {
        Boolean retorno = false;
        if (selecionado != null && selecionado.getId() != null) {
            selecionado = pessoaFisicaFacade.recuperar(selecionado.getId());
            for (Telefone fone : this.getTelefones()) {
                if (fone.getPrincipal()) {
                    retorno = true;
                }
            }
        }
        return retorno;
    }

    public void addDocumentoPessoa(PessoaFisica pes, DocumentoPessoal dp) {
        pes.getDocumentosPessoais().add(dp);
    }

    public String getFiltro() {
        if (filtro == null) {
            filtro = "";
        }
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
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

    public List<Telefone> getTelefones() {
        return telefones;
    }

    public List<EnderecoCorreio> getEnderecos() {
        return enderecos;
    }

    public void novoFone() {
        if (validaTelefone()) {
           telefone.setPessoa(selecionado);
            telefones.add(telefone);

            telefone = new Telefone();
        }
    }

    private boolean validaTelefone() {
        boolean retorno = true;
        if (this.telefone.getTelefone().trim().isEmpty()) {
            FacesUtil.addError("Campo Obrigatório!", "Informe o número do telefone");
            retorno = false;
        }
        return retorno;
    }

    public void limparTelefones() {
        telefone = new Telefone();
    }


    public void novoRg() {
        if (rg != null && !rg.getNumero().isEmpty()) {
            rg.setDataRegistro(new Date());
            rg.setPessoaFisica(selecionado);
            if (selecionado.getDocumentosPessoais().contains(rg)) {
                selecionado.getDocumentosPessoais().set(selecionado.getDocumentosPessoais().indexOf(rg), rg);
            } else {
                selecionado.getDocumentosPessoais().add(rg);
            }
        }
    }

    public void removeRg(ActionEvent evento) {
        selecionado.getDocumentosPessoais().remove((RG) evento.getComponent().getAttributes().get("removeRg"));
        rgs.remove(0);
        rg = new RG();
    }

    public void novoEndereco() {
        if (validaEndereco()) {
            enderecos.add(endereco);
            endereco = new EnderecoCorreio();
        }
    }

    public void limparCamposEndereco() {
        endereco = new EnderecoCorreio();
    }

    public void removeFone(ActionEvent evento) {
        Telefone t = (Telefone) evento.getComponent().getAttributes().get("vfone");
        t.setPessoa(null);
        telefones.remove(t);
    }

    public void removeEndereco(ActionEvent evento) {
        enderecos.remove((EnderecoCorreio) evento.getComponent().getAttributes().get("vendereco"));
    }

    public PessoaEspecialControlador() {
        super(PessoaFisica.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return pessoaFisicaFacade;
    }

    public List<SelectItem> getTiposFone() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoTelefone object : TipoTelefone.values()) {
            toReturn.add(new SelectItem(object, object.getTipoFone()));
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

    public ConverterGenerico getConverterCidade() {
        if (converterCidade == null) {
            converterCidade = new ConverterGenerico(Cidade.class, pessoaFisicaFacade.getCidadeFacade());
        }
        return converterCidade;
    }

    public List<Cidade> completaNaturalidade(String parte) {
        return pessoaFisicaFacade.getCidadeFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<SelectItem> getCidades() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Cidade object : pessoaFisicaFacade.getCidadeFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public Converter getConverterNaturalidade() {
        if (converterNaturalidade == null) {
            converterNaturalidade = new ConverterAutoComplete(Cidade.class, pessoaFisicaFacade.getCidadeFacade());
        }
        return converterNaturalidade;
    }

    public Converter getConverterUnidadeExterna() {
        if (converterUnidadeExterna == null) {
            converterUnidadeExterna = new ConverterAutoComplete(UnidadeExterna.class, pessoaFisicaFacade.getUnidadeExternaFacade());
        }
        return converterUnidadeExterna;
    }

    public List<SelectItem> getEstados() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (UF object : pessoaFisicaFacade.getUfFacade().listaUFNaoNula()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterUf() {
        if (converterUf == null) {
            converterUf = new ConverterGenerico(UF.class, pessoaFisicaFacade.getUfFacade());
        }
        return converterUf;
    }

    public List<SelectItem> getUf() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (UF object : pessoaFisicaFacade.getUfFacade().listaUFNaoNula()) {
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

    @URLAction(mappingId = "verPessoaEspecial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarPessoaEspecial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionarPessoa(selecionado);
    }

    public void selecionarPessoa(PessoaFisica pes) {
        if (!pes.getPerfis().contains(PerfilEnum.PERFIL_ESPECIAL)) {
            pes.getPerfis().add(PerfilEnum.PERFIL_ESPECIAL);
        }
        telefone = new Telefone();
        endereco = new EnderecoCorreio();
        unidadeOrganizacionalSelecionada = new HierarquiaOrganizacional();
        unidadeOrganizacionalSelecionada.setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ORCAMENTARIA);
        unidadeOrganizacionalSelecionada.setSubordinada(pes.getUnidadeOrganizacional());
        unidadeOrganizacionalSelecionada = pessoaFisicaFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(pes.getUnidadeOrganizacional(), unidadeOrganizacionalSelecionada, getSistemaControlador().getExercicioCorrente());
        classeCredorSelecionada = new ClasseCredorPessoa();
        listaClasseCredorPessoa = new ArrayList<ClasseCredorPessoa>();
        pes = pessoaFisicaFacade.recuperar(pes.getId());
        enderecos = pes.getEnderecos();
        telefones = pes.getTelefones();

        if(enderecos == null){
            enderecos = Lists.newArrayList();
        }
        if (telefones == null){
            telefones = Lists.newArrayList();
        }
        if (verificaRG(pes) == null) {
            rg = new RG();
            rg.setDataRegistro(new Date());
            rg.setPessoaFisica(pes);
            addDocumentoPessoa(pes, rg);
        }
        for (DocumentoPessoal doc : pes.getDocumentosPessoais()) {
            if (doc instanceof RG) {
                rg = (RG) doc;
            }
        }

    }


    private boolean validaEndereco() {
        boolean retorno = true;
        if (endereco == null) {
            FacesUtil.addError("Operação não realizada!","Erro ao adicionar um endereço, tente novamente!");
            endereco = new EnderecoCorreio();
            retorno = false;
        } else {
            if (endereco.getLocalidade() == null || "".equals(endereco.getLocalidade().trim())) {
                FacesUtil.addError("Campo Obrigatório!", "Informe a cidade");
                retorno = false;
            }
            if (endereco.getBairro() == null || "".equals(endereco.getBairro().trim())) {
                FacesUtil.addError("Campo Obrigatório!", "Informe o bairro");
                retorno = false;
            }
            if (endereco.getLogradouro() == null || "".equals(endereco.getLocalidade().trim())) {
                FacesUtil.addError("Campo Obrigatório!", "Informe o logradouro");
                retorno = false;
            }
        }
        return retorno;
    }

    public RG verificaRG(PessoaFisica pes) {
        for (DocumentoPessoal documento : pes.getDocumentosPessoais()) {
            if (documento instanceof RG) {
                return (RG) documento;
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
        return pessoaFisicaFacade.getConsultaCepFacade().consultaUf(parte.trim());
    }

    public void setaUf(SelectEvent e) {
        endereco.setUf((String) e.getObject());
    }

    public void setaCidade(SelectEvent e) {
        endereco.setLocalidade((String) e.getObject());
    }

    public List<String> completaCidade(String parte) {
        String l = "";
        if (endereco.getUf() != null) {
            l = endereco.getUf();
        }
        return pessoaFisicaFacade.getConsultaCepFacade().listaLocalidadesString(l, parte.trim());
    }

    public List<String> completaLogradouro(String parte) {
        String l = "";
        if (endereco.getLocalidade() != null) {
            l = endereco.getLocalidade();
        }
        return pessoaFisicaFacade.getConsultaCepFacade().listaLogradourosString(l, parte.trim());
    }

    public List<String> completaBairro(String parte) {
        String l = "";
        if (endereco.getLocalidade() != null) {
            l = endereco.getLocalidade();
        }
        return pessoaFisicaFacade.getConsultaCepFacade().listaBairrosString(l, parte.trim());
    }

    public List<String> completaCEP(String parte) {
        return pessoaFisicaFacade.getConsultaCepFacade().listaCEPString(parte.trim());
    }

    public void atualizarLogradouros() {
        pessoaFisicaFacade.getConsultaCepFacade().atualizarLogradouros(endereco);
    }

    public List<String> getListaUF() {
        return pessoaFisicaFacade.getConsultaCepFacade().listaUfString();
    }

    @Override
    public void salvar() {
        try {
            selecionado.setEnderecos(enderecos);
            selecionado.setTelefones(telefones);
            novoRg();
            if(!selecionado.getPerfis().contains(PerfilEnum.PERFIL_ESPECIAL)){
                selecionado.getPerfis().add(PerfilEnum.PERFIL_ESPECIAL);
            }

            if (unidadeOrganizacionalSelecionada != null) {
                selecionado.setUnidadeOrganizacional(unidadeOrganizacionalSelecionada.getSubordinada());
            } else {
                selecionado.setUnidadeOrganizacional(null);
            }
            if (validaCampos()) {
                if (operacao == Operacoes.NOVO) {
                    selecionado.adicionarHistoricoSituacaoCadastral();
                    pessoaFisicaFacade.salvarNovo(selecionado);
                } else {
                    pessoaFisicaFacade.salvar(selecionado);
                }
                FacesUtil.addInfo("Salvo com sucesso!", "");
                redireciona();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    public PerfilEnumConverter getPerfilEnumConveter() {
        return perfilEnumConveter;
    }

    public void setPerfilEnumConveter(PerfilEnumConverter perfilEnumConveter) {
        this.perfilEnumConveter = perfilEnumConveter;
    }

    public List<SelectItem> getTipoEmpresas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoEmpresa t : TipoEmpresa.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public Boolean validaCampos() {
        boolean controle = true;
        if (!selecionado.getCpf_Cnpj().equals("")) {
            if (Util.valida_CpfCnpj(String.valueOf(selecionado.getCpf_Cnpj()))) {
                if (pessoaFisicaFacade.getPessoaFacade().hasOutraPessoaComMesmoCpf(selecionado, false)) {
                    FacesUtil.addError("Não foi possível salvar!", "Já existe uma pessoa com o CPF informado.");
                    controle = false;
                }
            } else {
                FacesUtil.addError("Não foi possível salvar!", "CPF/CNPJ inválido.");
                controle = false;
            }
        }
        return ValidaPessoa.valida(selecionado, PerfilEnum.PERFIL_ESPECIAL) && controle;
    }

    public List<PessoaFisica> getListaEspecial() {
        return pessoaFisicaFacade.listaFiltrandoPorTipoPerfil(getFiltro().trim(), PerfilEnum.PERFIL_ESPECIAL);
    }

    public HierarquiaOrganizacional getUnidadeOrganizacionalSelecionada() {
        return unidadeOrganizacionalSelecionada;
    }

    public void setUnidadeOrganizacionalSelecionada(HierarquiaOrganizacional unidadeOrganizacionalSelecionada) {
        this.unidadeOrganizacionalSelecionada = unidadeOrganizacionalSelecionada;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public List<ClasseCredorPessoa> getListaClasseCredorPessoa() {
        return listaClasseCredorPessoa;
    }

    public void setListaClasseCredorPessoa(List<ClasseCredorPessoa> listaClasseCredorPessoa) {
        this.listaClasseCredorPessoa = listaClasseCredorPessoa;
    }

    public ClasseCredorPessoa getClasseCredorSelecionada() {
        return classeCredorSelecionada;
    }

    public void setClasseCredorSelecionada(ClasseCredorPessoa classeCredorSelecionada) {
        this.classeCredorSelecionada = classeCredorSelecionada;
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @URLAction(mappingId = "novoPessoaEspecialCpf", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void avancar() {
        PessoaFisica pf = pessoaFisicaFacade.getPessoaFacade().recuperaPessoaFisicaPorCPF(cpf);
        if (pf != null) {
            selecionado = pf;
            selecionarPessoa(selecionado);
            FacesUtil.addWarn("Cadastro encontrado!", "Esse cadastro foi encontrado com o CPF informado.");
        } else {
            novaPessoa();
            selecionado.setCpf(cpf);
            this.enderecos = new ArrayList<>();
            this.telefones = new ArrayList<>();

        }
    }

    public String getEsferaGoverno() {
        if (selecionado.getUnidadeExterna() != null) {
            return selecionado.getUnidadeExterna().getEsferaGoverno().getNome();
        }
        return "";
    }

    @Override
    public String getCaminhoPadrao() {
        return "/pessoaespecial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void vaiParaNovo() {
        if (!"".equals(cpf)) {
            cpf = pessoaFisicaFacade.getPessoaFacade().limpaCpf(cpf);
            if (validaCpf(cpf)) {
                PessoaFisica pf = pessoaFisicaFacade.getPessoaFacade().recuperaPessoaFisicaPorCPF(cpf);
                if (pf != null) {
                    if (pf.getPerfis().contains(PerfilEnum.PERFIL_RH)) {
                        FacesUtil.addWarn("Pessoa encontrada no RH!", "Não é possível cadastrar uma pessoa do RH como Pessoa Especial. Por favor, cadastre o usuário desta pessoa diretamente no Cadastro de Usuários.");
                    } else {
                        Web.navegacao(getCaminhoPadrao()+"listar/", getCaminhoPadrao()+"novo/" + cpf + "/");
                        //FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "novo/" + cpf);
                    }
                } else {
                    Web.navegacao(getCaminhoPadrao()+"listar/", getCaminhoPadrao()+"novo/" + cpf + "/");
                    //FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "novo/" + cpf);
                }
            } else {
                FacesUtil.addError("Atenção", "Informe um CPF válido!");
            }
        } else {
            FacesUtil.addError("Atenção", "Informe o CPF!");
        }
    }
}
