/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ClasseDoAtributo;
import br.com.webpublico.enums.TipoProprietario;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author claudio
 */

@ManagedBean(name = "cadastroRuralControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoCadastroRural", pattern = "/tributario/cadastro-rural/novo/", viewId = "/faces/tributario/cadastromunicipal/cadastrorural/edita.xhtml"),
        @URLMapping(id = "editarCadastroRural", pattern = "/tributario/cadastro-rural/editar/#{cadastroRuralControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/cadastrorural/edita.xhtml"),
        @URLMapping(id = "listarCadastroRural", pattern = "/tributario/cadastro-rural/listar/", viewId = "/faces/tributario/cadastromunicipal/cadastrorural/lista.xhtml"),
        @URLMapping(id = "verCadastroRural", pattern = "/tributario/cadastro-rural/ver/#{cadastroRuralControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/cadastrorural/visualizar.xhtml"),
})

public class CadastroRuralControlador extends PrettyControlador<CadastroRural> implements Serializable, CRUD {

    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private TipoAreaRuralFacade tipoAreaRuralFacade;
    private String caminho;
    private List<CadastroRural> lista;
    private PropriedadeRural propriedadeRural;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterCadastroRural;
    private ConverterGenerico converterTipoArea;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private EnderecoFacade enderecoFacade;
    private EnderecoCorreio enderecos;
    private SistemaControlador sistemaControlador;

    public CadastroRuralControlador() {
        super(CadastroRural.class);
    }


    public EnderecoCorreio getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(EnderecoCorreio enderecos) {
        this.enderecos = enderecos;
    }

    public PropriedadeRural getPropriedadeRural() {
        if (propriedadeRural == null) {
            propriedadeRural = new PropriedadeRural();
        }
        return propriedadeRural;
    }

    public void setPropriedadeRural(PropriedadeRural propriedadeRural) {
        this.propriedadeRural = propriedadeRural;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public CadastroRuralFacade getCadastroRuralFacade() {
        return cadastroRuralFacade;
    }

    public void setCadastroRuralFacade(CadastroRuralFacade cadastroRuralFacade) {
        this.cadastroRuralFacade = cadastroRuralFacade;
    }

    public List<CadastroRural> getLista() {
        return lista = cadastroRuralFacade.lista();
    }

    public void setLista(List<CadastroRural> lista) {
        this.lista = lista;
    }

    public CadastroRural getSelecionado() {
        if (selecionado == null) {
            selecionado = new CadastroRural();
        }
        return selecionado;
    }

    public void setSelecionado(CadastroRural selecionado) {
        this.selecionado = selecionado;
    }

    @Override
    public AbstractFacade getFacede() {
        return cadastroRuralFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/cadastro-rural/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @URLAction(mappingId = "novoCadastroRural", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado = new CadastroRural();
        enderecos = new EnderecoCorreio();
        selecionado.setCodigo(cadastroRuralFacade.ultimoCodigoMaisUm());
        propriedadeRural = new PropriedadeRural();
        sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        selecionado.setAtributos(cadastroRuralFacade.getAtributoFacade().novoMapaAtributoValorAtributo(ClasseDoAtributo.CADASTRO_RURAL));
    }


    @URLAction(mappingId = "verCadastroRural", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarCadastroRural", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        selecionado = cadastroRuralFacade.recuperar(selecionado.getId());
        enderecos = new EnderecoCorreio();
        sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public void salvar() {
        try {
            if (!validaCampos()) {
                return;
            }
            if (selecionado.getId() == null) {
                do {
                    selecionado.setCodigo(cadastroRuralFacade.ultimoCodigoMaisUm());
                } while (selecionado.getCodigo() <= cadastroRuralFacade.ultimoCodigo());
            }
            cadastroRuralFacade.salvar(selecionado);
            selecionado = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Salvo com sucesso!", ""));

            //lista = null;
            redireciona();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    private boolean validaCampos() {
        boolean retorno = true;

        if (selecionado.getNomePropriedade() == null || selecionado.getNomePropriedade().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "Informe o nome da propriedade."));
            retorno = false;
        }
        if (selecionado.getLocalizacaoLote() == null || selecionado.getLocalizacaoLote().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "Informe a localização do lote."));
            retorno = false;
        }
        if (selecionado.getAreaLote() == null || selecionado.getAreaLote().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "Informe a área do lote."));
            retorno = false;
        }
        if (selecionado.getTipoAreaRural() == null || selecionado.getTipoAreaRural().getId() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "Selecione o tipo de área."));
            retorno = false;
        }

        if (selecionado.getPropriedade() == null || selecionado.getPropriedade().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "O imóvel deve conter no mínimo 01 proprietário."));
            retorno = false;
        }

        return retorno;
    }

    public void excluirSelecionado() {
        try {
            cadastroRuralFacade.remover(selecionado);
            lista = null;
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "O Registro não pode ser excluido, o mesmo possui dependências."));
        }
    }

    public List<Pessoa> completaPessoa(String parte) {
        return cadastroRuralFacade.listaTodasPessoas(parte);
    }

    public Converter getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, pessoaFacade);
        }
        return converterPessoa;
    }

    public Converter getConverterCadastroRural() {
        if (converterCadastroRural == null) {
            converterCadastroRural = new ConverterAutoComplete(CadastroRural.class, cadastroRuralFacade);
        }
        return converterCadastroRural;
    }

    public List<SelectItem> getListaTipoAreaRural() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoAreaRural object : tipoAreaRuralFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoProprietarios() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoProprietario object : TipoProprietario.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterTipoArea() {
        if (converterTipoArea == null) {
            converterTipoArea = new ConverterGenerico(TipoAreaRural.class, tipoAreaRuralFacade);
        }
        return converterTipoArea;
    }

    public boolean isAlterando() {
        if (selecionado != null && selecionado.getId() != null) {
            return true;
        } else {
            return false;
        }
    }

    public BigDecimal getSomaProporcoes() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (PropriedadeRural p : getPropriedadesAtuais()) {
            total = total.add(new BigDecimal(p.getProporcao()));
        }
        return total;
    }

    public boolean validaNovoProprietario() {
        boolean retorno = true;
        if (propriedadeRural.getPessoa() == null || propriedadeRural.getPessoa().getId() == null) {
            retorno = false;
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível adicionar o proprietário", "Proprietário não informado!"));
        }
        if (propriedadeRural.getProporcao() == null || propriedadeRural.getProporcao() <= 0) {
            retorno = false;
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível adicionar o proprietário", "Proporção não informada ou menor igual a 0(zero)!"));
        }
        if (propriedadeRural.getTipoProprietario() == null) {
            retorno = false;
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível adicionar o proprietário", "Tipo de Proprietário não informado!"));
        }
        if (propriedadeRural.getInicioVigencia() == null) {
            retorno = false;
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível adicionar o proprietário", "Início da vigência não informado!"));
        }
        return retorno;
    }

    public boolean validaProporcao() {
        boolean retorno = true;
        BigDecimal valida = getSomaProporcoes();
        if (propriedadeRural.getProporcao() != null) {
            valida = valida.add(new BigDecimal(propriedadeRural.getProporcao()));
        }
        if (valida.compareTo(new BigDecimal(100)) > 0) {
            retorno = false;
            FacesContext.getCurrentInstance().addMessage("Formulario:proporcao", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ultrapassará 100%", "Ultrapassará 100%"));
        }
        return retorno;
    }

    public void novoProprietario(ActionEvent e) {
        if (isAlterando()) {
            FacesContext.getCurrentInstance().addMessage("Formulario:proprietarioTabela", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção! ", "Impossível adicionar um proprietário ao alterar um Cadastro Rural"));
        } else {
            if (validaNovoProprietario()) {
                if (validaProporcao()) {
                    propriedadeRural.setImovel(selecionado);
                    propriedadeRural.setDataRegistro(new Date());
                    selecionado.getPropriedade().add(propriedadeRural);
                    propriedadeRural = new PropriedadeRural();
                }
            }
        }
    }

    public void removeProprietario(ActionEvent evento) {
        if (isAlterando()) {
            FacesContext.getCurrentInstance().addMessage("Formulario:proprietarioTabela", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção! ", "Impossível remover um proprietário ao alterar um Cadastro Rural"));
        } else {
            PropriedadeRural p = (PropriedadeRural) evento.getComponent().getAttributes().get("removeProprietario");
            selecionado.getPropriedade().remove(p);
        }

    }

    public void selecionaProprietario(Propriedade p) {
        Pessoa proprietario = pessoaFacade.recuperar(p.getPessoa().getId());
        enderecos = enderecoFacade.retornaEnderecoPrincipalCorreio(proprietario);

    }

    public List<EnderecoCorreio> enderecos(Pessoa pessoa) {
        Pessoa proprietario = pessoaFacade.recuperar(pessoa.getId());
        return proprietario.getEnderecos();
    }

    public void colocaSelecionadoNaSecaoEvaiParaPessoaFisica() {
        Web.navegacao("/faces/tributario/cadastromunicipal/cadastrorural/edita.xhtml",
                "/faces/tributario/cadastromunicipal/pessoafisicatributario/edita.xhtml",
                selecionado);
    }
    public void colocaSelecionadoNaSecaoEvaiParaPessoaJuridica() {
        Web.navegacao("/faces/tributario/cadastromunicipal/cadastrorural/edita.xhtml",
                "/faces/tributario/cadastromunicipal/pessoafisicatributario/editaJuridica.xhtml",
                selecionado);

    }

    public List<PropriedadeRural> getPropriedadesAtuais() {
        return selecionado.getPropriedadesAtuais();
    }

    public List<PropriedadeRural> getPropriedadesDoHistorico() {
        return selecionado.getPropriedadesDoHistorico();
    }
}
