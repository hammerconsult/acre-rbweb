/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controle.forms.FormLogradouroControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoLogradouro;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author terminal4
 */
@ManagedBean(name = "logradouroControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoLogradouro", pattern = "/tributario/cadastromunicipal/logradouro/novo/",
        viewId = "/faces/tributario/cadastromunicipal/logradouro/edita.xhtml"),

    @URLMapping(id = "editarLogradouro", pattern = "/tributario/cadastromunicipal/logradouro/editar/#{logradouroControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/logradouro/edita.xhtml"),

    @URLMapping(id = "listarLogradouro", pattern = "/tributario/cadastromunicipal/logradouro/listar/",
        viewId = "/faces/tributario/cadastromunicipal/logradouro/lista.xhtml"),

    @URLMapping(id = "verLogradouro", pattern = "/tributario/cadastromunicipal/logradouro/ver/#{logradouroControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/logradouro/visualizar.xhtml")})
public class LogradouroControlador extends PrettyControlador<Logradouro> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(LogradouroControlador.class);

    protected ConverterGenerico converterTipoLogradouro;
    protected ConverterGenerico converterSetor;
    protected List lista;
    private SituacaoLogradouro situacaoLogradouro;
    @EJB
    private LogradouroFacade facade;
    @EJB
    private SetorFacade setorFacade;
    @EJB
    private TipoLogradouroFacade tipoLogradouroFacade;
    @EJB
    private CEPFacade cepFacade;
    @EJB
    private LogradouroFacade logradouroFacade;

    public LogradouroControlador() {
        super(Logradouro.class);
        metadata = new EntidadeMetaData(Logradouro.class);
    }

    public SituacaoLogradouro getSituacaoLogradouro() {
        return situacaoLogradouro;
    }

    public void setSituacaoLogradouro(SituacaoLogradouro situacaoLogradouro) {
        this.situacaoLogradouro = situacaoLogradouro;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/cadastromunicipal/logradouro/";
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoLogradouro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        FormLogradouroControlador f = (FormLogradouroControlador) Web.pegaDaSessao(FormLogradouroControlador.class);
        if (f == null) {
            super.novo();
            selecionado.setCodigo(geraProximoCodigoLogradouro());
        } else {
            getSessionData(f);
        }
    }

    @URLAction(mappingId = "editarLogradouro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void selecionar() {
        FormLogradouroControlador f = (FormLogradouroControlador) Web.pegaDaSessao(FormLogradouroControlador.class);
        if (f == null) {
            super.editar();
            situacaoLogradouro = selecionado.getSituacao();
        } else {
            getSessionData(f);
        }
    }

    private void getSessionData(FormLogradouroControlador f) {
        selecionado = f.logradouro;
        operacao = f.operacao;

        TipoLogradouro tl = (TipoLogradouro) Web.pegaDaSessao(TipoLogradouro.class);
        if (tl != null && tl.getId() != null) {
            selecionado.setTipoLogradouro(tl);
        }
    }


    @URLAction(mappingId = "verLogradouro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    /**
     * Seleciona a Origem da tela e O destinho da  ,mesma
     */
    public void novoTipoLogradouro() {
        Web.navegacao(getCaminhoDeVolta(),
            "/tributario/cadastromunicipal/tipologradouro/novo/",
            novoFomularioInicializado()
        );
    }

    public void novoBairro() {
        Web.navegacao(getCaminhoDeVolta(),
            "/tributario/cadastromunicipal/bairro/novo/",
            novoFomularioInicializado()
        );
    }

    private String getCaminhoDeVolta() {
        String caminhoDeVolta;
        if (novoCadastro()) {
            caminhoDeVolta = "/tributario/cadastromunicipal/logradouro/novo/";
        } else {
            caminhoDeVolta = "/tributario/cadastromunicipal/logradouro/editar/" + selecionado.getId() + "/";
        }
        return caminhoDeVolta;
    }

    private FormLogradouroControlador novoFomularioInicializado() {
        FormLogradouroControlador fmlg = new FormLogradouroControlador();
        fmlg.logradouro = selecionado;
        fmlg.operacao = operacao;
        return fmlg;
    }

    public List<Setor> completaSetor(String parte) {
        return setorFacade.listaFiltrando(parte.trim(), "nome");
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
            valida = false;
            FacesUtil.addWarn("Atenção!", "O Código é um campo obrigatório");
        } else if (logradouroFacade.existeLogradouroPorCodigo(selecionado.getId(), selecionado.getCodigo())) {
            valida = false;
            FacesUtil.addWarn("Atenção!", "O Código " + selecionado.getCodigoString() + " já existe.");
        }
        if (selecionado.getTipoLogradouro() == null) {
            valida = false;
            FacesUtil.addWarn("Atenção!", "O Tipo de Logradouro é um campo obrigatório.");
        }
        if (selecionado.getNome() == null || selecionado.getNome().trim().equals("")) {
            valida = false;
            FacesUtil.addWarn("Atenção!", "O Nome Atual é um campo obrigatório.");
        } else if (logradouroFacade.existeLogradouroPorNomeSituacao(selecionado.getId(), selecionado.getNome(), SituacaoLogradouro.ATIVO, selecionado.getSetor())) {
            valida = false;
            FacesUtil.addWarn("Atenção!", "O Nome do logradouro já existe para outro registro ativo.");
        }
        if (selecionado.getSituacao() == null) {
            valida = false;
            FacesUtil.addWarn("Atenção!", "A Situação é um campo obrigatório.");
        }
        if (selecionado.getSetor() == null) {
            valida = false;
            FacesUtil.addWarn("Atenção!", "O Setor é um campo obrigatório.");
        }
        if (operacao.equals(Operacoes.EDITAR) && (situacaoLogradouro.equals(SituacaoLogradouro.INATIVO) && selecionado.getSituacao().equals(SituacaoLogradouro.INATIVO))) {
            valida = false;
            FacesUtil.addWarn("Atenção!", "Não é permitido alterar um registro com situação inativo.");
        }

        return valida;
    }

    public List<SelectItem> getTipoLogradouros() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoLogradouro object : tipoLogradouroFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacoesLogradouro() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (SituacaoLogradouro obj : SituacaoLogradouro.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public LogradouroFacade getFacade() {
        return facade;
    }

    public ConverterGenerico getConverterTipoLogradouro() {
        if (converterTipoLogradouro == null) {
            converterTipoLogradouro = new ConverterGenerico(TipoLogradouro.class, tipoLogradouroFacade);
        }
        return converterTipoLogradouro;
    }

    public Converter getConverterSetor() {
        if (converterSetor == null) {
            converterSetor = new ConverterGenerico(Setor.class, setorFacade);
        }
        return converterSetor;
    }

    public boolean novoCadastro() {
        return operacao.equals(Operacoes.NOVO);
    }

    public void uploadArquivos(FileUploadEvent file) {
        try {
            Arquivo arq = new Arquivo();
            arq.setNome(file.getFile().getFileName());
            arq.setMimeType(logradouroFacade.getArquivoFacade().getMimeType(file.getFile().getFileName()));
            arq.setDescricao(new Date().toString());
            arq.setTamanho(file.getFile().getSize());

            LogradouroArquivo logradouroArquivo = new LogradouroArquivo();
            logradouroArquivo.setArquivo(logradouroFacade.getArquivoFacade().novoArquivoMemoria(arq, file.getFile().getInputstream()));
            logradouroArquivo.setLogradouro(selecionado);

            selecionado.getArquivos().add(logradouroArquivo);
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    public void removerArquivo(ActionEvent evento) {
        LogradouroArquivo arq = (LogradouroArquivo) evento.getComponent().getAttributes().get("objeto");
        selecionado.getArquivos().remove(arq);
    }

    public Long geraProximoCodigoLogradouro() {
        return logradouroFacade.proximoCodigo();
    }

    public List<SelectItem> getSetores() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Setor obj : logradouroFacade.getSetorFacade().listaSetoresOrdenadosPorCodigo()) {
            if (obj.getDistrito() != null) {
                toReturn.add(new SelectItem(obj, obj.toString() + " - " + obj.getDistrito().getNome()));
            } else {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
        }
        return toReturn;
    }
}
