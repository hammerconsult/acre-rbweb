/*
 * Codigo gerado automaticamente em Tue Mar 15 15:00:49 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.*;
import br.com.webpublico.nfse.domain.dtos.ConsultaReceita;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ManagedBean
@SessionScoped
public class PessoaJuridicaControlador extends SuperControladorCRUD implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(PessoaJuridicaControlador.class);

    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    @EJB
    private NacionalidadeFacade nacionalidadeFacade;
    protected ConverterGenerico converterNacionalidade;
    private Telefone telefone = new Telefone();
    private Telefone telefoneAux;
    private EnderecoCorreio endereco = new EnderecoCorreio();
    private ContaCorrenteBancaria contaCorrenteBancaria = new ContaCorrenteBancaria();
    private ContaCorrenteBancaria contaCorrenteBancariaAux;
    private ConverterAutoComplete converterAgencia;
    private CEPLogradouro logradouro;
    private String complemento;
    private String numero;
    private String cep;
    private HtmlInputText textocep;
    private HtmlInputText textoAutoComplete;
    @EJB
    private AgenciaFacade agenciaFacade;
    @EJB
    private ConsultaCepFacade consultaCepFacade;
    private List<CEPLogradouro> logradouros;

    @Override
    public void novo() {
        super.novo();
        ((PessoaJuridica) selecionado).setTelefones(new ArrayList<Telefone>());
        ((PessoaJuridica) selecionado).setEnderecos(new ArrayList<EnderecoCorreio>());
        ((PessoaJuridica) selecionado).setClassePessoa(ClassePessoa.EXTRA);
//        ((PessoaJuridica) selecionado).setContasCorrentesBancarias(new ArrayList<ContaCorrenteBancaria>());
        logradouro = new CEPLogradouro();
        telefone = new Telefone();
        telefoneAux = new Telefone();
        endereco = new EnderecoCorreio();
        contaCorrenteBancaria = new ContaCorrenteBancaria();
        contaCorrenteBancariaAux = new ContaCorrenteBancaria();
    }

    public Boolean liberaTelefonePrincipal() {
        PessoaJuridica pj = ((PessoaJuridica) selecionado);
        for (Telefone fone : pj.getTelefones()) {
            //System.out.println("PRINCIPAL:" + fone.getPrincipal());
            if (fone.getPrincipal()) {
                //System.out.println("PRINCIPAL:" + fone.getPrincipal());
                return false;
            }
        }
        return true;
    }

    public CEPLogradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(CEPLogradouro logradouro) {
        this.logradouro = logradouro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public List<CEPLogradouro> getLogradouros() {
        return logradouros;
    }

    public void setLogradouros(List<CEPLogradouro> logradouros) {
        this.logradouros = logradouros;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public List<Telefone> getTelefones() {
        return ((PessoaJuridica) selecionado).getTelefones();
    }

    public List<EnderecoCorreio> getEnderecos() {
        return ((PessoaJuridica) selecionado).getEnderecos();
    }

    //    public List<ContaCorrenteBancaria> getContas() {
//        return ((PessoaJuridica) selecionado).getContasCorrentesBancarias();
//    }
    public void novoFone() {
        telefone.setPessoa((PessoaJuridica) selecionado);
        ((PessoaJuridica) selecionado).getTelefones().add(telefone);
        telefone = new Telefone();
    }

    //    public void novaConta() {
//        ((PessoaJuridica) selecionado).getContasCorrentesBancarias().add(contaCorrenteBancaria);
//        contaCorrenteBancaria = new ContaCorrenteBancaria();
//    }
    public void novoEndereco() {
        ((PessoaJuridica) selecionado).getEnderecos().add(endereco);
        logradouros = new ArrayList<CEPLogradouro>();
        endereco = new EnderecoCorreio();
    }

    public void removeFone(ActionEvent evento) {
        ((PessoaJuridica) selecionado).getTelefones().remove((Telefone) evento.getComponent().getAttributes().get("removeTelefone"));
    }

    //    public void removeConta(ActionEvent evento) {
//        ((PessoaJuridica) selecionado).getContasCorrentesBancarias().remove((ContaCorrenteBancaria) evento.getComponent().getAttributes().get("removeConta"));
//    }
    public void removeEndereco(ActionEvent evento) {
        ((PessoaJuridica) selecionado).getEnderecos().remove((EnderecoCorreio) evento.getComponent().getAttributes().get("removeEndereco"));
    }

    public PessoaJuridicaControlador() {
        metadata = new EntidadeMetaData(PessoaJuridica.class);
    }

    public PessoaJuridicaFacade getFacade() {
        return pessoaJuridicaFacade;
    }

    public ContaCorrenteBancaria getContaCorrenteBancaria() {
        return contaCorrenteBancaria;
    }

    public void setContaCorrenteBancaria(ContaCorrenteBancaria contaCorrenteBancaria) {
        this.contaCorrenteBancaria = contaCorrenteBancaria;
    }

    @Override
    public AbstractFacade getFacede() {
        return pessoaJuridicaFacade;
    }

    public EnderecoCorreio getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoCorreio endereco) {
        this.endereco = endereco;
    }

    public Telefone getTelefone() {
        return telefone;
    }

    public void setTelefone(Telefone telefone) {
        this.telefone = telefone;
    }

    public ContaCorrenteBancaria getContaCorrenteBancariaAux() {
        return contaCorrenteBancariaAux;
    }

    public void setContaCorrenteBancariaAux(ContaCorrenteBancaria contaCorrenteBancariaAux) {
        this.contaCorrenteBancariaAux = contaCorrenteBancariaAux;
    }

    public Telefone getTelefoneAux() {
        return telefoneAux;
    }

    public void setTelefoneAux(Telefone telefoneAux) {
        this.telefoneAux = telefoneAux;
    }

    public List<SelectItem> getNacionalidade() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Nacionalidade object : nacionalidadeFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public HtmlInputText getTextocep() {
        return textocep;
    }

    public void setTextocep(HtmlInputText textocep) {
        this.textocep = textocep;
    }

    public ConverterGenerico getConverterNacionalidade() {
        if (converterNacionalidade == null) {
            converterNacionalidade = new ConverterGenerico(Nacionalidade.class, nacionalidadeFacade);
        }
        return converterNacionalidade;
    }

    public List<SelectItem> getSituacaoConta() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (SituacaoConta object : SituacaoConta.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposFone() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoTelefone object : TipoTelefone.values()) {
            toReturn.add(new SelectItem(object, object.getTipoFone()));
        }
        return toReturn;
    }

    public Converter getConverterAgencia() {
        if (converterAgencia == null) {
            converterAgencia = new ConverterAutoComplete(Agencia.class, agenciaFacade);
        }
        return converterAgencia;
    }

    public List<Agencia> completaAgencia(String parte) {
        return agenciaFacade.listaFiltrando(parte.trim(), "nomeAgencia", "numeroAgencia");
    }

    @Override
    public void selecionar(ActionEvent evento) {
        operacao = Operacoes.EDITAR;
        Pessoa p = (Pessoa) evento.getComponent().getAttributes().get("objeto");
        selecionado = pessoaJuridicaFacade.recuperar(p.getId());
        telefoneAux = new Telefone();
        contaCorrenteBancariaAux = new ContaCorrenteBancaria();
    }

    public void setaTelefone(ActionEvent evento) {
        setTelefoneAux((Telefone) evento.getComponent().getAttributes().get("alteraFone"));
    }

    //    public void setaEndereco(ActionEvent evento) {
//        setEnderecoAux((EnderecoCorreio) evento.getComponent().getAttributes().get("alteraEndereco"));
//    }
    public void setaContaCorrenteBancaria(ActionEvent evento) {
        setContaCorrenteBancariaAux((ContaCorrenteBancaria) evento.getComponent().getAttributes().get("alteraContaCorrenteBancaria"));
    }

    private boolean validaEmail(String email) {
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public boolean valida_CpfCnpj(String s_aux) {
        s_aux = s_aux.replace(".", "");
        s_aux = s_aux.replace("-", "");
        s_aux = s_aux.replace("/", "");

        if (s_aux.length() == 14) {
            int soma = 0, aux, dig;
            String cnpj_calc = s_aux.substring(0, 12);
            char[] chr_cnpj = s_aux.toCharArray();
//--------- Primeira parte
            for (int i = 0; i < 4; i++) {
                if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9) {
                    soma += (chr_cnpj[i] - 48) * (6 - (i + 1));
                }
            }
            for (int i = 0; i < 8; i++) {
                if (chr_cnpj[i + 4] - 48 >= 0 && chr_cnpj[i + 4] - 48 <= 9) {
                    soma += (chr_cnpj[i + 4] - 48) * (10 - (i + 1));
                }
            }
            dig = 11 - (soma % 11);
            cnpj_calc += (dig == 10 || dig == 11)
                ? "0" : Integer.toString(dig);
//--------- Segunda parte
            soma = 0;
            for (int i = 0; i < 5; i++) {
                if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9) {
                    soma += (chr_cnpj[i] - 48) * (7 - (i + 1));
                }
            }
            for (int i = 0; i < 8; i++) {
                if (chr_cnpj[i + 5] - 48 >= 0 && chr_cnpj[i + 5] - 48 <= 9) {
                    soma += (chr_cnpj[i + 5] - 48) * (10 - (i + 1));
                }
            }
            dig = 11 - (soma % 11);
            cnpj_calc += (dig == 10 || dig == 11)
                ? "0" : Integer.toString(dig);
            return s_aux.equals(cnpj_calc);
        } else {
            return false;
        }
    }

    @Override
    public Boolean validaCampos() {
        Boolean retorno = super.validaCampos();
        if (valida_CpfCnpj(((PessoaJuridica) selecionado).getCnpj()) == false) {
            FacesContext.getCurrentInstance().addMessage("Formulario:cnpj", new FacesMessage(FacesMessage.SEVERITY_ERROR, "CNPJ inválida!", "Por favor, verificar CNPJ."));
            retorno = false;
        }
//        if (((PessoaJuridica) selecionado).getEnderecos().isEmpty()) {
//            FacesContext.getCurrentInstance().addMessage("Formulario:logradouro", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nenhum Endereco detectado!", "Deve existir pelo menos um!"));
//            retorno = false;
//        }
        if (!((PessoaJuridica) selecionado).getEmail().isEmpty()) {
            if (validaEmail(((PessoaJuridica) selecionado).getEmail()) == false) {
                FacesContext.getCurrentInstance().addMessage("Formulario:email", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email inválido!", "Por Favor verifique!"));
                retorno = false;
            }
        }
        if (pessoaJuridicaFacade.getPessoaFacade().hasOutraPessoaComMesmoCnpj((PessoaJuridica) selecionado, false)) {
            FacesContext.getCurrentInstance().addMessage("Formulario:cpf", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível Salvar", "Já existe um registro com este número de CPF"));
            retorno = false;
        }
        return retorno;
    }

    public List<CEPUF> completaUF(String parte) {
        return consultaCepFacade.consultaUf(parte.trim());
    }

    public void setaUf(SelectEvent e) {
        endereco.setUf((String) e.getObject());
    }

    public void setaCidade(SelectEvent e) {
        endereco.setLocalidade((String) e.getObject());
    }

    public List<CEPLocalidade> completaCidade(String parte) {
        return consultaCepFacade.consultaLocalidades(endereco.getUf(), parte.trim());
    }

    public List<CEPLogradouro> completaLogradouro(String parte) {
        return consultaCepFacade.consultaLogradouros(endereco.getLocalidade(), parte.trim());
    }

    public List<CEPBairro> completaBairro(String parte) {
        return consultaCepFacade.consultaBairros(endereco.getLocalidade(), parte.trim());
    }

    public void atualizaLogradouros() {
        cep = textocep.getValue().toString();
        logradouros = consultaCepFacade.consultaLogradouroPorCEP(cep);
        logger.debug("entrou" + logradouros);

        if (!logradouros.isEmpty()) {
            logger.debug("existe registro");
            logradouro = logradouros.get(0);
            logger.debug(logradouro.getNome() + " logradourodocepe");
            endereco.setBairro(logradouro.getBairro().getNome());
            endereco.setCep(logradouro.getCep());
            endereco.setLogradouro(logradouro.getNome());
            endereco.setUf(logradouro.getLocalidade().getCepuf().getNome());
            endereco.setLocalidade(logradouro.getLocalidade().getNome());
        }
    }

    public List<SelectItem> getTiposEnderecos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoEndereco t : TipoEndereco.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public HtmlInputText getTextoAutoComplete() {
        return textoAutoComplete;
    }

    public void setTextoAutoComplete(HtmlInputText textoAutoComplete) {
        this.textoAutoComplete = textoAutoComplete;
    }

    Map<String, String> mapConsultaReceita = Maps.newHashMap();

    public List<String> getAtributosConsultaReceita() {
        return Lists.newArrayList(mapConsultaReceita.keySet());
    }

    public Map<String, String> getMapConsultaReceita() {
        return mapConsultaReceita;
    }

    public void buscarPessoaJuridicaNaReceita(String cnpj) {
        if (Strings.isNullOrEmpty(cnpj)) {
            FacesUtil.addAtencao("Informe um CNPJ válido");
            return;
        }
        mapConsultaReceita = Maps.newHashMap();
        ConsultaReceita consultaReceita = pessoaJuridicaFacade.buscarPessoaJuridicaNaReceita(cnpj);
        if (consultaReceita != null) {
            for (Field field : consultaReceita.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object valor = field.get(consultaReceita);
                    String get = valor.toString();
                    if (valor instanceof List) {
                        get = "";
                        for (Object obj : (List) valor) {
                            get += obj.toString() + "<br/>";
                        }
                    }

                    if (field.isAnnotationPresent(Etiqueta.class)) {
                        mapConsultaReceita.put(field.getAnnotation(Etiqueta.class).value(), get);
                    } else {
                        mapConsultaReceita.put(field.getName(), get);
                    }
                } catch (Exception e) {
                    logger.error("Não foi possível acessar o field ", e);
                }
            }
            FacesUtil.executaJavaScript("$('#modalDadosPjReceitaFederal').modal('show');");
        } else {
            FacesUtil.addAtencao("Não foi possível localizar os dados com o CNPJ '" + cnpj + "'");
        }
    }
}
