/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Bairro;
import br.com.webpublico.entidades.Logradouro;
import br.com.webpublico.entidades.LogradouroBairro;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BairroFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;

/**
 * @author Usuario
 */
@ManagedBean(name = "bairroControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoBairro", pattern = "/tributario/cadastromunicipal/bairro/novo/",
        viewId = "/faces/tributario/cadastromunicipal/bairro/edita.xhtml"),

    @URLMapping(id = "editarBairro", pattern = "/tributario/cadastromunicipal/bairro/editar/#{bairroControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/bairro/edita.xhtml"),

    @URLMapping(id = "listarBairro", pattern = "/tributario/cadastromunicipal/bairro/listar/",
        viewId = "/faces/tributario/cadastromunicipal/bairro/lista.xhtml"),

    @URLMapping(id = "verBairro", pattern = "/tributario/cadastromunicipal/bairro/ver/#{bairroControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/bairro/visualizar.xhtml")})
public class BairroControlador extends PrettyControlador<Bairro> implements Serializable, CRUD {

    @EJB
    private BairroFacade bairroFacade;
    private Converter converterLogradouro;
    private LogradouroBairro logradouroBairro;
    private boolean ativo;

    public BairroControlador() {
        super(Bairro.class);
        metadata = new EntidadeMetaData(Bairro.class);
    }

    public BairroFacade getFacade() {
        return bairroFacade;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public AbstractFacade getFacede() {
        return bairroFacade;
    }

    public Converter getConverterLogradouro() {
        if (converterLogradouro == null) {
            converterLogradouro = new ConverterAutoComplete(Logradouro.class, bairroFacade.getLogradouroFacade());
        }
        return converterLogradouro;
    }

    public List<Logradouro> completaLogradouro(String parte) {
        return bairroFacade.getLogradouroFacade().listaLogradourosAtivos(parte.trim());
    }

    public LogradouroBairro getLogradouroBairro() {
        return logradouroBairro;
    }

    public void setLogradouroBairro(LogradouroBairro logradouroBairro) {
        this.logradouroBairro = logradouroBairro;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/cadastromunicipal/bairro/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoBairro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        selecionado = (Bairro) Web.pegaDaSessao(Bairro.class);
        if (selecionado == null) {
            super.novo();
            selecionado.setCodigo(bairroFacade.ultimoNumeroMaisUm());
        }
        logradouroBairro = new LogradouroBairro();
    }

    @URLAction(mappingId = "verBairro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarBairro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        selecionado = (Bairro) Web.pegaDaSessao(Bairro.class);
        if (selecionado == null) {
            super.editar();
            ativo = selecionado.getAtivo();
        }
        logradouroBairro = new LogradouroBairro();
    }

    public void adicionaLogradouro() {
        if (validaLogradouro()) {
            logradouroBairro.setBairro(selecionado);
            selecionado.getLogradouros().add(logradouroBairro);
            logradouroBairro = new LogradouroBairro();
        }

    }

    public void removerLogradouro(ActionEvent evento) {
        try {
            LogradouroBairro log = (LogradouroBairro) evento.getComponent().getAttributes().get("objeto");
            selecionado.getLogradouros().remove(log);
        } catch (Exception e) {
            FacesUtil.addError("Operação Não Permitida", e.getMessage());
        }
    }


    public boolean validaLogradouro() {
        boolean valida = true;
        if (logradouroBairro.getLogradouro() == null) {
            valida = false;
            FacesUtil.addWarn("Atenção!", "O Logradouro é um campo obrigatório.");
        }
        if (logradouroBairro.getNumeroInicial() == null) {
            valida = false;
            FacesUtil.addWarn("Atenção!", "O início do intervalo da numeração é um campo obrigatório.");
        }
        if (logradouroBairro.getNumeroFinal() == null) {
            valida = false;
            FacesUtil.addWarn("Atenção!", "O final do intervalo da numeração é um campo obrigatório.");
        }

        if (logradouroBairro.getCep() == null || logradouroBairro.getCep().trim().equals("")) {
            valida = false;
            FacesUtil.addWarn("Atenção!", "O CEP é um campo obrigatório.");
        }

        return valida;
    }

    @Override
    public void salvar() {
        if (validaGeral()) {
            super.salvar();
        }
    }

    private boolean validaGeral() {
        boolean valida = true;

        if (selecionado.getCodigo() == null) {
            FacesUtil.addError("Atenção", "O Código é um campo obrigatório!");
            valida = false;
        }
//        else if (bairroFacade.existeBairroPorCodigo(selecionado.getId(), selecionado.getCodigo())) {
//            FacesUtil.addError("Atenção", "O Código " + selecionado.getCodigo() + " já existe.");
//            valida = false;
//        }

        if (selecionado.getDescricao().trim().equals("")) {
            valida = false;
            FacesUtil.addWarn("Atenção!", "O Nome é um campo obrigatório");
        }
//        else if (bairroFacade.existeBairroPorNome(selecionado.getId(), selecionado.getDescricao())) {
//            valida = false;
//            FacesUtil.addWarn("Atenção!", "Já existe um bairro ativo com o nome inserido.");
//        }

        if (selecionado.getId() != null && (!ativo && !selecionado.getAtivo())) {
            valida = false;
            FacesUtil.addWarn("Atenção!", "Não é possível alterar um registro inativo.");
        }

        return valida;
    }

    public List<Bairro> completaBairro(String parte) {
        return bairroFacade.completaBairro(parte);
    }

}
