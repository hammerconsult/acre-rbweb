package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Situacao;
import br.com.webpublico.enums.TipoEndereco;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "agenciaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoAgencia", pattern = "/agencia/novo/", viewId = "/faces/tributario/cadastromunicipal/agencia/edita.xhtml"),
        @URLMapping(id = "editarAgencia", pattern = "/agencia/editar/#{agenciaControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/agencia/edita.xhtml"),
        @URLMapping(id = "listarAgencia", pattern = "/agencia/listar/", viewId = "/faces/tributario/cadastromunicipal/agencia/lista.xhtml"),
        @URLMapping(id = "verAgencia", pattern = "/agencia/ver/#{agenciaControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/agencia/visualizar.xhtml")
})
public class AgenciaControlador extends PrettyControlador<Agencia> implements Serializable, CRUD {

    @EJB
    private AgenciaFacade agenciaFacade;
    @EJB
    private ConsultaCepFacade consultaCepFacade;
    @EJB
    private BancoFacade bancoFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private CidadeFacade cidadeFacade;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private UFFacade uFFacade;
    protected transient Converter converterLogradouro;
    protected ConverterAutoComplete converterBanco;
    protected transient Converter converterCidade;
    protected ConverterAutoComplete converterBairro;
    private Cidade cidade;
    private TipoEndereco tipoEndereco;
    private HtmlInputText textocep;
    private String cep;
    private List<CEPLogradouro> logradouros;
    private CEPLogradouro logradouro;
    private EnderecoCorreio endereco;

    public AgenciaControlador() {
        super(Agencia.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/agencia/";
    }

    @Override
    public AbstractFacade getFacede() {
        return agenciaFacade;
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<String> completaCEP(String parte) {
        return agenciaFacade.getConsultaCepFacade().consultaLogradouroPorParteCEPByString(parte.trim());
    }

    @URLAction(mappingId = "novoAgencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setEnderecoCorreio(new EnderecoCorreio());
        endereco = new EnderecoCorreio();
    }

    @URLAction(mappingId = "verAgencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "editarAgencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    public void recuperaEditaVer() {
        selecionado = agenciaFacade.recuperar(selecionado.getId());
        endereco = selecionado.getEnderecoCorreio();
        if(endereco == null){
            endereco = new EnderecoCorreio();
        }
    }


    public void setCidade(Cidade cidade) {
        selecionado.getEnderecoCorreio().setLocalidade(cidade.getNome());
        this.cidade = cidade;
    }

    public ConverterAutoComplete getConverterBairro() {
        if (converterBairro == null) {
            converterBairro = new ConverterAutoComplete(Bairro.class, bairroFacade);
        }
        return converterBairro;
    }

    public void setConverterBairro(ConverterAutoComplete converterBairro) {
        this.converterBairro = converterBairro;
    }

    public List<SelectItem> getBanco() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Banco object : bancoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Situacao object : Situacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getlistaTipoEndereco() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(TipoEndereco.COMERCIAL, TipoEndereco.COMERCIAL.getDescricao()));
        toReturn.add(new SelectItem(TipoEndereco.COBRANCA, TipoEndereco.COBRANCA.getDescricao()));
        toReturn.add(new SelectItem(TipoEndereco.CORRESPONDENCIA, TipoEndereco.CORRESPONDENCIA.getDescricao()));
        toReturn.add(new SelectItem(TipoEndereco.RESIDENCIAL, TipoEndereco.RESIDENCIAL.getDescricao()));
        toReturn.add(new SelectItem(TipoEndereco.RURAL, TipoEndereco.RURAL.getDescricao()));
        toReturn.add(new SelectItem(TipoEndereco.OUTROS, TipoEndereco.OUTROS.getDescricao()));
        return toReturn;
    }

    public List<Banco> completaBanco(String parte) {
        return bancoFacade.listaBancoPorCodigoOuNome(parte.trim());
    }

    public HtmlInputText getTextocep() {
        return textocep;
    }

    public void setTextocep(HtmlInputText textocep) {
        this.textocep = textocep;
    }

    public void setaUf(SelectEvent e) {
        String uf = (String) e.getObject();
        if (uf != null) {
            getSelecionado().getEnderecoCorreio().setUf(uf);
        }
    }

    public void setaCidade(SelectEvent e) {
        String cidade = (String) e.getObject();
        if (cidade != null) {
            getSelecionado().getEnderecoCorreio().setLocalidade(cidade);
        }
    }

    public void setaBairro(SelectEvent e) {
        String bairro = (String) e.getObject();
        if (bairro != null) {
            getSelecionado().getEnderecoCorreio().setBairro(bairro);
        }
    }

    public void setaLogradouro(SelectEvent e) {
        String logradouro = (String) e.getObject();
        if (logradouro != null) {
            getSelecionado().getEnderecoCorreio().setLogradouro(logradouro);
        }
    }

    public List<SelectItem> getTiposEnderecos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoEndereco t : TipoEndereco.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterBanco() {
        if (converterBanco == null) {
            converterBanco = new ConverterAutoComplete(Banco.class, bancoFacade);
        }
        return converterBanco;
    }

    public List<Cidade> completaCidade(String parte) {
        if (selecionado.getEnderecoCorreio().getUf() == null || "".equals(selecionado.getEnderecoCorreio().getUf())) {
            FacesUtil.addInfo("Atenção!", "O Estado não foi selecionado !");
            return null;
        } else {
            return cidadeFacade.consultaCidade(selecionado.getEnderecoCorreio().getUf(), parte.trim());
        }
    }

    public List<Logradouro> completaLogradouro(String parte) {
        if (selecionado.getEnderecoCorreio().getLocalidade() == null || "".equals(selecionado.getEnderecoCorreio().getLocalidade())) {
            FacesUtil.addInfo("Atenção!", "O Bairro não foi selecionado !");
            return null;
        } else {
            return logradouroFacade.consultaLogradouros(selecionado.getEnderecoCorreio().getBairro(), parte.trim());
        }
    }

    public List<Bairro> completaBairro(String parte) {
        if (selecionado.getEnderecoCorreio().getLocalidade() == null || "".equals(selecionado.getEnderecoCorreio().getLocalidade())) {
            FacesUtil.addInfo("Atenção!", "A Cidade não foi selecionada !");
            return null;
        } else {
            return bairroFacade.consultaBairros(selecionado.getEnderecoCorreio().getLocalidade(), parte.trim());
        }
    }

    public void atualizaLogradouros() {
        agenciaFacade.getConsultaCepFacade().atualizarLogradouros(endereco);
    }

    public void salvar() {
        try {
            if (Util.validaCampos(selecionado)) {
                selecionado.setEnderecoCorreio(endereco);
                if (operacao.equals(Operacoes.NOVO)) {
                    agenciaFacade.salvarNovo(selecionado);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso."));
                } else {
                    agenciaFacade.salvar(selecionado);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro alterado com sucesso."));
                }
                redireciona();
            }

        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operação não Realizada! ", ex.getMessage()));
        }
    }

    public CEPLogradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(CEPLogradouro logradouro) {
        this.logradouro = logradouro;
    }

    public AgenciaFacade getFacade() {
        return agenciaFacade;
    }

    public List<CEPLogradouro> getLogradouros() {
        return logradouros;
    }

    public void setLogradouros(List<CEPLogradouro> logradouros) {
        this.logradouros = logradouros;
    }

    public EnderecoCorreio getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoCorreio endereco) {
        this.endereco = endereco;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public TipoEndereco getTipoEndereco() {
        return tipoEndereco;
    }

    public void setTipoEndereco(TipoEndereco tipoEndereco) {
        this.tipoEndereco = tipoEndereco;
    }

    public Cidade getCidade() {
        return cidade;
    }
}
