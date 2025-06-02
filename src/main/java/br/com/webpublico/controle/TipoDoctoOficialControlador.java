/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoDoctoOficialFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author leonardo
 */
@ManagedBean(name = "tipoDoctoOficialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoTipoDoctoOficial", pattern = "/tipo-de-documento-oficial/novo/", viewId = "/faces/tributario/certidao/tipodocumento/edita.xhtml"),
    @URLMapping(id = "listaTipoDoctoOficial", pattern = "/tipo-de-documento-oficial/listar/", viewId = "/faces/tributario/certidao/tipodocumento/lista.xhtml"),
    @URLMapping(id = "verTipoDoctoOficial", pattern = "/tipo-de-documento-oficial/ver/#{tipoDoctoOficialControlador.id}/", viewId = "/faces/tributario/certidao/tipodocumento/visualizar.xhtml"),
    @URLMapping(id = "editarTipoDoctoOficial", pattern = "/tipo-de-documento-oficial/editar/#{tipoDoctoOficialControlador.id}/", viewId = "/faces/tributario/certidao/tipodocumento/edita.xhtml"),

    //url chamadas na tela de parametros de petição de DA
    @URLMapping(id = "novoTipoDoctoOficialPeticaoDAPF", pattern = "/tipo-de-documento-oficial/novo-pessoa-fisica/", viewId = "/faces/tributario/certidao/tipodocumento/edita.xhtml"),
    @URLMapping(id = "novoTipoDoctoOficialPeticaoDAPJ", pattern = "/tipo-de-documento-oficial/novo-pessoa-juridica/", viewId = "/faces/tributario/certidao/tipodocumento/edita.xhtml"),
    @URLMapping(id = "novoTipoDoctoOficialPeticaoDACadastroImobiliario", pattern = "/tipo-de-documento-oficial/novo-cadastro-imobiliario/", viewId = "/faces/tributario/certidao/tipodocumento/edita.xhtml"),
    @URLMapping(id = "novoTipoDoctoOficialPeticaoDACadastroEconomico", pattern = "/tipo-de-documento-oficial/novo-cadastro-economico/", viewId = "/faces/tributario/certidao/tipodocumento/edita.xhtml"),
    @URLMapping(id = "novoTipoDoctoOficialPeticaoDACadastroRural", pattern = "/tipo-de-documento-oficial/novo-cadastro-rural/", viewId = "/faces/tributario/certidao/tipodocumento/edita.xhtml"),
})
public class TipoDoctoOficialControlador extends PrettyControlador<TipoDoctoOficial> implements Serializable, CRUD {

    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    private ConverterAutoComplete converterGrupo;
    private ConverterAutoComplete converterTributo;
    private ConverterAutoComplete converterFinalidade;
    private ConverterAutoComplete converterUsuarioSistema;
    private TipoDoctoFinalidade tipoDoctoFinalidade;
    private AtributoDoctoOficial atributoDoctoOficial;
    private UsuarioTipoDocto usuarioTipoDocto;
    private ModeloDoctoOficial modeloDoctoOficial;
    private List<SelectItem> listaTags;
    private List<TextoFixo> textoFixos;
    private TextoFixo textoFixoSelecionado;
    private AssinaturaTipoDoctoOficial assinatura;

    public TipoDoctoOficialControlador() {
        super(TipoDoctoOficial.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoDoctoOficialFacade;
    }

    public List<SelectItem> getListaTags() {
        return listaTags;
    }

    public void setListaTags(List<SelectItem> listaTags) {
        this.listaTags = listaTags;
    }

    public ModeloDoctoOficial getModeloDoctoOficial() {
        return modeloDoctoOficial;
    }

    public void setModeloDoctoOficial(ModeloDoctoOficial modeloDoctoOficial) {
        this.modeloDoctoOficial = modeloDoctoOficial;
    }

    public UsuarioTipoDocto getUsuarioTipoDocto() {
        return usuarioTipoDocto;
    }

    public void setUsuarioTipoDocto(UsuarioTipoDocto usuarioTipoDocto) {
        this.usuarioTipoDocto = usuarioTipoDocto;
    }

    public AtributoDoctoOficial getAtributoDoctoOficial() {
        return atributoDoctoOficial;
    }

    public void setAtributoDoctoOficial(AtributoDoctoOficial atributoDoctoOficial) {
        this.atributoDoctoOficial = atributoDoctoOficial;
    }

    public TipoDoctoOficialFacade getTipoDoctoOficialFacade() {
        return tipoDoctoOficialFacade;
    }

    public void setTipoDoctoOficialFacade(TipoDoctoOficialFacade tipoDoctoOficialFacade) {
        this.tipoDoctoOficialFacade = tipoDoctoOficialFacade;
    }

    public Converter getConverterGrupo() {
        if (this.converterGrupo == null) {
            this.converterGrupo = new ConverterAutoComplete(GrupoDoctoOficial.class, tipoDoctoOficialFacade.getGrupoDoctoOficialFacade());
        }
        return this.converterGrupo;
    }

    public Converter getConverterTributo() {
        if (this.converterTributo == null) {
            this.converterTributo = new ConverterAutoComplete(Tributo.class, tipoDoctoOficialFacade.getTributoFacade());
        }
        return this.converterTributo;
    }

    public Converter getConverterUsuarioSistema() {
        if (this.converterUsuarioSistema == null) {
            this.converterUsuarioSistema = new ConverterAutoComplete(UsuarioSistema.class, tipoDoctoOficialFacade.getUsuarioSistemaFacade());
        }
        return this.converterUsuarioSistema;
    }

    public TipoDoctoFinalidade getTipoDoctoFinalidade() {
        return tipoDoctoFinalidade;
    }

    public void setTipoDoctoFinalidade(TipoDoctoFinalidade tipoDoctoFinalidade) {
        this.tipoDoctoFinalidade = tipoDoctoFinalidade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-de-documento-oficial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoTipoDoctoOficial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        if (selecionado.getCodigo() == null) {
            this.selecionado.setCodigo(tipoDoctoOficialFacade.ultimoCodigoMaisUm());
        }
        tipoDoctoFinalidade = (TipoDoctoFinalidade) Web.pegaDaSessao(TipoDoctoFinalidade.class);
        if (tipoDoctoFinalidade == null) {
            tipoDoctoFinalidade = new TipoDoctoFinalidade();
        }
        Finalidade finalidade = (Finalidade) Web.pegaDaSessao(Finalidade.class);
        if (finalidade != null) {
            tipoDoctoFinalidade.setFinalidade(finalidade);
        }
        atributoDoctoOficial = new AtributoDoctoOficial();
        atributoDoctoOficial.setObrigatorio(Boolean.FALSE);
        usuarioTipoDocto = new UsuarioTipoDocto();
        assinatura = new AssinaturaTipoDoctoOficial();
        listaTags = new ArrayList<SelectItem>();
        novoModeloDoctoOficial();
        recuperaTags();
    }

    private void novoModeloDoctoOficial() {
        modeloDoctoOficial = new ModeloDoctoOficial(selecionado);
        modeloDoctoOficial.setSequencia(1);
        modeloDoctoOficial.setVigenciaInicial(new Date());
    }

    //usado para a petição de divida ativa
    @URLAction(mappingId = "novoTipoDoctoOficialPeticaoDAPF", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void documentoOficialPessoaFisica() {
        novo();
        selecionado.setTipoCadastroDoctoOficial(TipoCadastroDoctoOficial.PESSOAFISICA);
        selecionado.setModuloTipoDoctoOficial(ModuloTipoDoctoOficial.PETICAODIVIDAATIVA);
    }

    @URLAction(mappingId = "novoTipoDoctoOficialPeticaoDAPJ", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void documentoOficialPessoaJuridica() {
        novo();
        selecionado.setTipoCadastroDoctoOficial(TipoCadastroDoctoOficial.PESSOAJURIDICA);
        selecionado.setModuloTipoDoctoOficial(ModuloTipoDoctoOficial.PETICAODIVIDAATIVA);
    }

    @URLAction(mappingId = "novoTipoDoctoOficialPeticaoDACadastroImobiliario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void documentoOficialCadastroImobiliario() {
        novo();
        selecionado.setTipoCadastroDoctoOficial(TipoCadastroDoctoOficial.CADASTROIMOBILIARIO);
        selecionado.setModuloTipoDoctoOficial(ModuloTipoDoctoOficial.PETICAODIVIDAATIVA);
    }

    @URLAction(mappingId = "novoTipoDoctoOficialPeticaoDACadastroEconomico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void documentoOficialCadastroEconomico() {
        novo();
        selecionado.setTipoCadastroDoctoOficial(TipoCadastroDoctoOficial.CADASTROECONOMICO);
        selecionado.setModuloTipoDoctoOficial(ModuloTipoDoctoOficial.PETICAODIVIDAATIVA);
    }

    @URLAction(mappingId = "novoTipoDoctoOficialPeticaoDACadastroRural", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void documentoOficialCadastroRural() {
        novo();
        selecionado.setTipoCadastroDoctoOficial(TipoCadastroDoctoOficial.CADASTRORURAL);
        selecionado.setModuloTipoDoctoOficial(ModuloTipoDoctoOficial.PETICAODIVIDAATIVA);
    }

    public void novoTipoDocto(TipoCadastroDoctoOficial tipo, ModuloTipoDoctoOficial modulo) {
        novo();
        this.selecionado.setTipoCadastroDoctoOficial(tipo);
        this.selecionado.setModuloTipoDoctoOficial(modulo);
        recuperaTags();
    }

    public void salvar() {
        try {
            validarCampos();
            if (selecionado.getTributo() == null || selecionado.getTributo().getId() == null) {
                limparValores();
            }
            try {
                selecionado.adicionarModeloDoctoOficial(modeloDoctoOficial);
                if (isOperacaoNovo()) {
                    this.tipoDoctoOficialFacade.salvarNovo(selecionado);
                } else {
                    this.tipoDoctoOficialFacade.salvar(selecionado);
                }
                FacesUtil.addInfo("", "Salvo com sucesso!");
                redireciona();
            } catch (Exception e) {
                FacesUtil.addError("Não foi possível continuar!", e.getMessage());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<SelectItem> getTiposSequencia() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, "Utilizar Sequência do Grupo"));
        for (TipoSequenciaDoctoOficial object : TipoSequenciaDoctoOficial.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposValidacoes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoValidacaoDoctoOficial object : TipoValidacaoDoctoOficial.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getModulosTiposDoctoOficial() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (ModuloTipoDoctoOficial object : ModuloTipoDoctoOficial.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCadastroDoctoOficial object : TipoCadastroDoctoOficial.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<GrupoDoctoOficial> completaGrupoDoctoOficial(String parte) {
        return tipoDoctoOficialFacade.getGrupoDoctoOficialFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public List<Tributo> completaTributo(String parte) {
        return tipoDoctoOficialFacade.getTributoFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public List<UsuarioSistema> completaUsuarioSistema(String parte) {
        return tipoDoctoOficialFacade.getUsuarioSistemaFacade().listaFiltrando(parte.trim(), "login");
    }

    public List<TipoDoctoOficial> getLista() {
        return this.tipoDoctoOficialFacade.lista();
    }

    @Override
    @URLAction(mappingId = "verTipoDoctoOficial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTipoDoctoOficial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        tipoDoctoFinalidade = new TipoDoctoFinalidade();
        atributoDoctoOficial = new AtributoDoctoOficial();
        usuarioTipoDocto = new UsuarioTipoDocto();
        assinatura = new AssinaturaTipoDoctoOficial();
        recuperaTags();
        novoModeloDoctoOficial();
        if (selecionado.hasModeloDoctoOficial()) {
            modeloDoctoOficial = selecionado.getListaModeloDoctoOficial().get(0);
        }
    }

    public void excluirSelecionado() {
        try {
            this.tipoDoctoOficialFacade.remover(selecionado);
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível continuar!", "Este registro não pode ser excluído porque possui dependências.");
        }
    }

    public List<Finalidade> completaFinalidade(String parte) {
        return tipoDoctoOficialFacade.getFinalidadeFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public Converter getConverterFinalidade() {
        if (this.converterFinalidade == null) {
            this.converterFinalidade = new ConverterAutoComplete(Finalidade.class, tipoDoctoOficialFacade.getFinalidadeFacade());
        }
        return this.converterFinalidade;
    }

    public void addFinalidade() {
        if (validaFinalidade()) {
            tipoDoctoFinalidade.setTipoDoctoOficial(selecionado);
            selecionado.getTipoDoctoFinalidades().add(tipoDoctoFinalidade);
            tipoDoctoFinalidade = new TipoDoctoFinalidade();
        }
    }

    public void removeFinalidade(TipoDoctoFinalidade tipoDocto) {
        selecionado.getTipoDoctoFinalidades().remove(tipoDocto);
    }

    public void addUsuarioSistema() {
        if (validaUsuarioSistema()) {
            usuarioTipoDocto.setTipoDoctoOficial(selecionado);
            selecionado.getListaUsuarioTipoDocto().add(usuarioTipoDocto);
            usuarioTipoDocto = new UsuarioTipoDocto();
        }
    }

    public void removeUsuarioSistema(UsuarioTipoDocto usuario) {
        selecionado.getListaUsuarioTipoDocto().remove(usuario);
    }

    public void addModeloDocto() {
        Integer seq = 0;
        if (selecionado.getListaModeloDoctoOficial().size() > 0) {
            ModeloDoctoOficial ultimoModelo = selecionado.getListaModeloDoctoOficial().get(selecionado.getListaModeloDoctoOficial().size() - 1);
            for (ModeloDoctoOficial mod : selecionado.getListaModeloDoctoOficial()) {
                if (mod.getVigenciaFinal() == null) {
                    mod.setVigenciaFinal(new Date());
                }
            }
            seq = ultimoModelo.getSequencia();
        }
        String conteudo = modeloDoctoOficial.getConteudo();
        modeloDoctoOficial = new ModeloDoctoOficial();
        modeloDoctoOficial.setSequencia(seq + 1);
        modeloDoctoOficial.setTipoDoctoOficial(selecionado);
        modeloDoctoOficial.setVigenciaInicial(new Date());
        modeloDoctoOficial.setVigenciaFinal(null);
        modeloDoctoOficial.setConteudo(conteudo);
        selecionado.getListaModeloDoctoOficial().add(modeloDoctoOficial);
    }

    public void removeModeloDocto(ModeloDoctoOficial modelo) {
        selecionado.getListaModeloDoctoOficial().remove(modelo);
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCodigo() == null || selecionado.getCodigo() <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um Código maior que zero.");
        } else if (selecionado.getId() == null && tipoDoctoOficialFacade.existeCodigo(selecionado.getCodigo())) {
            selecionado.setCodigo(tipoDoctoOficialFacade.ultimoCodigoMaisUm());
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Código informado já está em uso em outro registro. O sistema gerou um novo código. Por favor, pressione o botão SALVAR novamente.");
        } else if (selecionado.getId() != null && tipoDoctoOficialFacade.existeCodigoTipoDoctoOficial(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Código informado já existe.");
        }
        if (selecionado.getDescricao() == null || "".equals(selecionado.getDescricao())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Descrição.");
        }
        if (selecionado.getGrupoDoctoOficial() == null || selecionado.getGrupoDoctoOficial().getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Grupo do Documento.");
        }
        if (selecionado.getTipoCadastroDoctoOficial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo de Cadastro.");
        }
        if (selecionado.getTipoValidacaoDoctoOficial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo de Validação.");
        }
        if (selecionado.getModuloTipoDoctoOficial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Módulo do Tipo de Documento.");
        }
        if (!(selecionado.getTributo() == null || selecionado.getTributo().getId() == null)) {
            if (selecionado.getValor() == null || selecionado.getValor().compareTo(BigDecimal.ZERO) < 1) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o Valor do Tributo.");
            }
            if (selecionado.getValidadeDam() == null || selecionado.getValidadeDam() <= 0) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe a Validade do DAM.");
            }
        }
        if (selecionado.getValidadeDocto() == null || selecionado.getValidadeDocto() <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Validade do Documento.");
        }
        if (modeloDoctoOficial.getConteudo() == null || modeloDoctoOficial.getConteudo().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Modelo do Tipo de Documento.");
        }
        if ((selecionado.getModuloTipoDoctoOficial() != null && ModuloTipoDoctoOficial.FISCALIZACAO.equals(selecionado.getModuloTipoDoctoOficial())) &&
            (selecionado.getTipoCadastroDoctoOficial() != null && !TipoCadastroDoctoOficial.CADASTROECONOMICO.equals(selecionado.getTipoCadastroDoctoOficial()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Módulo 'Fiscalização' só é permitido para o Tipo de Cadastro de 'Cadastro Econômico'.");
        }
        ve.lancarException();
    }

    public void addAtributo() {
        if (validaAtributo()) {
            atributoDoctoOficial.setAtivo(Boolean.TRUE);
            atributoDoctoOficial.setTipoDoctoOficial(selecionado);
            atributoDoctoOficial.setTag(removeAcentos(atributoDoctoOficial.getCampo()).replaceAll(" ", "_").toUpperCase());
            atributoDoctoOficial.setTag(removeAcentos(atributoDoctoOficial.getTag()).replaceAll("/", "_"));
            atributoDoctoOficial.setTag(removeAcentos(atributoDoctoOficial.getTag()).replaceAll("\\\\", "_"));
            selecionado.getListaAtributosDoctoOficial().add(atributoDoctoOficial);
            SelectItem itemListaTag = new SelectItem(atributoDoctoOficial.getTag());
            listaTags.add(itemListaTag);
            atributoDoctoOficial = new AtributoDoctoOficial();
            atributoDoctoOficial.setObrigatorio(Boolean.FALSE);
        }
    }

    public String removeAcentos(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = str.replaceAll("[^\\p{ASCII}]", "");
        return str;
    }

    public void removeAtributo(AtributoDoctoOficial atributo) {
        selecionado.getListaAtributosDoctoOficial().remove(atributo);
        recuperaTags();
    }

    private boolean validaAtributo() {
        boolean retorno = true;

        if (atributoDoctoOficial.getCampo().isEmpty()) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe um valor para o Campo.");
        } else if (verificaDuplicidadeAtributo()) {
            FacesUtil.addError("Não foi possível continuar!", "O atributo " + atributoDoctoOficial.getCampo() + " já foi adicionado.");
            retorno = false;
        }
        return retorno;
    }

    private boolean verificaDuplicidadeAtributo() {
        for (AtributoDoctoOficial atributoDoctoOficial : selecionado.getListaAtributosDoctoOficial()) {
            if (this.atributoDoctoOficial.getCampo().equalsIgnoreCase(atributoDoctoOficial.getCampo())) {
                return true;
            }
        }
        return false;
    }

    private boolean validaFinalidade() {
        boolean retorno = true;

        if (tipoDoctoFinalidade.getFinalidade() == null || tipoDoctoFinalidade.getFinalidade().getId() == null) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Selecione uma Finalidade.");
        } else if (verificaDuplicidadeFinalidade()) {
            FacesUtil.addError("Não foi possível continuar!", "A finalidade " + tipoDoctoFinalidade.getFinalidade().getDescricao() + " já foi selecionada.");
            retorno = false;
        }
        return retorno;
    }

    private boolean verificaDuplicidadeFinalidade() {
        for (TipoDoctoFinalidade tipoDoctoFinalidade : selecionado.getTipoDoctoFinalidades()) {
            if (this.tipoDoctoFinalidade.getFinalidade().getId().equals(tipoDoctoFinalidade.getFinalidade().getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean validaUsuarioSistema() {
        boolean retorno = true;

        if (usuarioTipoDocto.getUsuarioSistema() == null || usuarioTipoDocto.getUsuarioSistema().getId() == null) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Selecione o(s) usuário(s) autorizados.");
        } else if (verificaDuplicidadeUsuarioSistema()) {
            FacesUtil.addError("Não foi possível continuar!", "O usuário " + usuarioTipoDocto.getUsuarioSistema().getLogin() + " já foi selecionado.");
            retorno = false;
        }
        return retorno;
    }

    private boolean verificaDuplicidadeUsuarioSistema() {
        for (UsuarioTipoDocto usuarioTipoDocto : selecionado.getListaUsuarioTipoDocto()) {
            if (this.usuarioTipoDocto.getUsuarioSistema().getId().equals(usuarioTipoDocto.getUsuarioSistema().getId())) {
                return true;
            }
        }
        return false;
    }

    public void recuperaTags() {
        listaTags = new ArrayList<SelectItem>();
        listaTags.addAll(getRecuperaTagsSolicitacao());

        if (selecionado.getModuloTipoDoctoOficial() != null && selecionado.getModuloTipoDoctoOficial().isPrecisaTagsTipoCadastro()) {
            if (selecionado.getTipoCadastroDoctoOficial() != null) {
                listaTags.addAll(recuperaTagsDoModeloTipo(selecionado.getTipoCadastroDoctoOficial()));
            }
        }
        if (selecionado.getModuloTipoDoctoOficial() != null) {
            recuperaTagsPeloModulo();
        }
        for (AtributoDoctoOficial atributo : selecionado.getListaAtributosDoctoOficial()) {
            if (atributo.getAtivo() != null && atributo.getAtivo()) {
                SelectItem si = new SelectItem(atributo.getTag());
                listaTags.add(si);
            }
        }
        for (AssinaturaTipoDoctoOficial assinatura : selecionado.getAssinaturas()) {
            if (assinatura.getUsuarioSistema() != null && assinatura.isVigente() &&
                tipoDoctoOficialFacade.getUsuarioSistemaFacade().buscarAssinaturaUsuarioSistema(assinatura.getUsuarioSistema().getId()) != null) {
                String tag = StringUtil.removeCaracteresEspeciaisSemEspaco(assinatura.getUsuarioSistema().getLogin());
                SelectItem si = new SelectItem(tag);
                listaTags.add(si);
            }
        }
    }

    public void recuperaTagsPeloModulo() {
        if (selecionado.getTipoCadastroDoctoOficial() != null && selecionado.getModuloTipoDoctoOficial() != null) {
            listaTags.addAll(recuperaTagsDoModeloModulo(selecionado.getModuloTipoDoctoOficial()));
        }
    }

    public List<SelectItem> recuperaTagsDoModeloTipo(TipoCadastroDoctoOficial tipo) {
        if (tipo.equals(TipoCadastroDoctoOficial.CADASTROECONOMICO)) {
            return getRecuperaTagsCadastroEconomico();
        }
        if (tipo.equals(TipoCadastroDoctoOficial.CADASTROIMOBILIARIO)) {
            return getRecuperaTagsCadastroImobiliario();
        }
        if (tipo.equals(TipoCadastroDoctoOficial.CADASTRORURAL)) {
            return getRecuperaTagsCadastroRural();
        }
        if (tipo.equals(TipoCadastroDoctoOficial.PESSOAFISICA)) {
            return getRecuperaTagsPessoaFisica();
        }
        if (tipo.equals(TipoCadastroDoctoOficial.PESSOAJURIDICA)) {
            return getRecuperaTagsPessoaJuridica();
        }
        if (tipo.equals(TipoCadastroDoctoOficial.NENHUM)) {
            return Lists.newArrayList();
        }
        return null;
    }

    public List<SelectItem> recuperaTagsDoModeloModulo(ModuloTipoDoctoOficial modulo) {
        switch (modulo) {
            case FISCALIZACAO: {
                return getRecuperaTagsFiscalizacao();
            }
            case CERTIDADIVIDAATIVA: {
                return getRecuperaTagsCertidaoDividaAtiva();
            }
            case PETICAODIVIDAATIVA: {
                return getRecuperaTagsPeticaoDividaAtiva();
            }
            case FISCALIZACAORBTRANS: {
                return getRecuperaTagsFiscalizacaoRBTrans();
            }
            case RBTRANS: {
                return getRecuperaTagsRBTrans();
            }
            case RBTRANS_CERTIFICADO_OTT: {
                return getRecuperarTagsOTTRBTrans();
            }
            case RBTRANS_CERTIFICADO_CONDUTOR_OTT: {
                return getRecuperarTagsCondutorOTTRBTrans();
            }
            case TERMOADVERTENCIA: {
                return getRecuperaTagsTermoAdvertencia();
            }
            case AUTOINFRACAO: {
                return getRecuperaTagsAutoInfracaoFiscalizacao();
            }
            case PARECER_FISCALIZACAO: {
                return getRecuperaTagsParecerRecursoFiscalizacao();
            }
            case TERMO_GERAIS: {
                return new ArrayList<>();
            }
            case TERMODIVIDAATIVA: {
                return getRecuperaTagsTermoDividaAtiva();
            }
            case PROTOCOLO: {
                return getRecuperaTagsProtocolo();
            }
            case CONTRATO_RENDAS_PATRIMONIAIS: {
                return getRecuperaTagsContratoRendasPatrimoniais();
            }
            case CONTRATO_CEASA: {
                return getRecuperaTagsContratoCEASA();
            }
            case ISENCAO_IPTU: {
                return getRecuperaTagsIsencaoIPTU();
            }
            case COBRANCAADMINISTRATIVA: {
                return getRecuperaTagsCobrancaAdministrativa();
            }
            case TERMO_PARCELAMENTO: {
                return getRecuperaTagsProcessoParcelamento();
            }
            case SOLICITACAO_SEPULTAMENTO: {
                return getTagsAuxilioFuneral();
            }
            case DECLARACAO_BENEFICIOS_EVENTUAIS: {
                return getTagsAuxilioFuneral();
            }
            case DECLARACAO_SOLICITANTE_BENEFICIARIO: {
                return getTagsAuxilioFuneral();
            }
            case REQUISICAO_FUNERAL: {
                return getTagsAuxilioFuneral();
            }
            case SOLICITACAO: {
                return new ArrayList<>();
            }
            case CERTIDAO_BAIXA_ATIVIDADE: {
                return getTagsCertidaoBaixaAtividade();
            }
            case SUBVENCAO: {
                return getTagsSubvencao();
            }
            case MONITORAMENTO_FISCAL: {
                return getTagsOrdemGeralMonitormanto();
            }
            case BLOQUEIO_OUTORGA: {
                return getTagsBloqueioOutorga();
            }
            case ALVARA_CONSTRUCAO:
            case ALVARA_IMEDIATO: {
                List<SelectItem> tags = getTagsAlvaraConstrucao();
                tags.addAll(getTagsProcessoRegularizacaoConstrucao());
                return tags;
            }
            case HABITESE_CONSTRUCAO: {
                List<SelectItem> tags = getTagsHabiteseConstrucao();
                tags.addAll(getTagsProcessoRegularizacaoConstrucao());
                return tags;
            }
            case PROCESSO_PROTESTO: {
                return getTagsProcessoDeProtesto();
            }
            case LICENCA_ETR: {
                return getTagsLicencaETR();
            }
            case NOTA_EMPENHO:
                return getTagsNotaOrcamentaria();

            case NOTA_RESTO_EMPENHO:
                return getTagsNotaOrcamentaria();

            case NOTA_ESTORNO_EMPENHO:
                return getTagsNotaOrcamentaria();

            case NOTA_RESTO_ESTORNO_EMPENHO:
                return getTagsNotaOrcamentaria();

            case NOTA_LIQUIDACAO:
                return getTagsNotaOrcamentaria();

            case NOTA_RESTO_LIQUIDACAO:
                return getTagsNotaOrcamentaria();

            case NOTA_ESTORNO_LIQUIDACAO:
                return getTagsNotaOrcamentaria();

            case NOTA_RESTO_ESTORNO_LIQUIDACAO:
                return getTagsNotaOrcamentaria();

            case NOTA_PAGAMENTO:
                return getTagsNotaOrcamentaria();

            case NOTA_RESTO_PAGAMENTO:
                return getTagsNotaOrcamentaria();

            case NOTA_ESTORNO_PAGAMENTO:
                return getTagsNotaOrcamentaria();

            case NOTA_RESTO_ESTORNO_PAGAMENTO:
                return getTagsNotaOrcamentaria();

            case NOTA_RECEITA_EXTRA:
                return getTagsNotaOrcamentaria();

            case NOTA_RECEITA_EXTRA_PAGAMENTO:
                return getTagsNotaOrcamentaria();

            case NOTA_PAGAMENTO_EXTRA:
                return getTagsNotaOrcamentaria();

            case NOTA_RECEITA_EXTRA_ESTORNO:
                return getTagsNotaOrcamentaria();

            case NOTA_PAGAMENTO_EXTRA_ESTORNO:
                return getTagsNotaOrcamentaria();

            case RECIBO_REINF:
                return getTagsReciboReinf();

            case NOTA_OBRIGACAO_A_PAGAR_ESTORNO:
                return getTagsNotaOrcamentaria();

            case SOLICITACAO_MATERIAL:
                return getTagsSolicitacaoMaterial();

            case NOTA_OBRIGACAO_A_PAGAR:
                return getTagsNotaOrcamentaria();

            case TR:
                return getTagsSolicitacaoMaterial();

            case DFD:
                return getTagsSolicitacaoMaterial();

            case CERTIDAO_MARCA_FOGO:
                return getTagsMarcaFogo();

            case DOCUMENTO_LICENCIAMENTO_AMBIENTAL:
                return getTagsLicenciamentoAmbiental();

        }
        return new ArrayList<>();
    }

    private List<SelectItem> getTagsSolicitacaoMaterial() {
        return Util.getListSelectItemSemCampoVazio(TipoModeloDoctoOficial.TagsSolicitacaoMaterial.values());
    }

    private List<SelectItem> getTagsReciboReinf() {
        return Util.getListSelectItemSemCampoVazio(TipoModeloDoctoOficial.TagsReciboReinf.values());
    }

    private List<SelectItem> getTagsNotaOrcamentaria() {
        return Util.getListSelectItem(TipoModeloDoctoOficial.TagsNotaOrcamentaria.values());
    }

    public List<SelectItem> getTagsHabiteseConstrucao() {
        return Util.getListSelectItemSemCampoVazio(TipoModeloDoctoOficial.TagsHabiteseConstrucao.values());
    }

    public List<SelectItem> getTagsProcessoDeProtesto() {
        return Util.getListSelectItemSemCampoVazio(TipoModeloDoctoOficial.TagsProcessoDeProtesto.values());
    }

    public List<SelectItem> getTagsLicencaETR() {
        return Util.getListSelectItemSemCampoVazio(TipoModeloDoctoOficial.TagsLicencaETR.values());
    }

    public List<SelectItem> getTagsProcessoRegularizacaoConstrucao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsProcessoRegularizacaoConstrucao object : TipoModeloDoctoOficial.TagsProcessoRegularizacaoConstrucao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTagsAlvaraConstrucao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsAlvaraConstrucao object : TipoModeloDoctoOficial.TagsAlvaraConstrucao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTagsBloqueioOutorga() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsBloqueioOutorga object : TipoModeloDoctoOficial.TagsBloqueioOutorga.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTagsMarcaFogo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsMarcaFogo object : TipoModeloDoctoOficial.TagsMarcaFogo.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTagsLicenciamentoAmbiental() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsLicenciamentoAmbiental object : TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperaTagsCadastroEconomico() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsCadastroEconomico object : TipoModeloDoctoOficial.TagsCadastroEconomico.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperaTagsSolicitacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        if (selecionado.getTipoValidacaoDoctoOficial() != null && selecionado.getTipoValidacaoDoctoOficial().equals(TipoValidacaoDoctoOficial.SEMVALIDACAO)) {
            for (TipoModeloDoctoOficial.TagsGeraisSemValidacao object : TipoModeloDoctoOficial.TagsGeraisSemValidacao.values()) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
            return toReturn;
        }
        for (TipoModeloDoctoOficial.TagsGeraisSemValidacao object : TipoModeloDoctoOficial.TagsGeraisSemValidacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        for (TipoModeloDoctoOficial.TagsGerais object : TipoModeloDoctoOficial.TagsGerais.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }

        if (selecionado.getTipoValidacaoDoctoOficial() != null && selecionado.getTipoValidacaoDoctoOficial().equals(TipoValidacaoDoctoOficial.CERTIDAO_POSITIVA_BENS_IMOVEIS)) {
            for (TipoModeloDoctoOficial.TagsCertidaoBensImoveis object : TipoModeloDoctoOficial.TagsCertidaoBensImoveis.values()) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperaTagsCertidaoDividaAtiva() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsDividaAtiva object : TipoModeloDoctoOficial.TagsDividaAtiva.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperaTagsTermoAdvertencia() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsTermoDeAdvertencia object : TipoModeloDoctoOficial.TagsTermoDeAdvertencia.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperaTagsTermoDividaAtiva() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsTermoDividaAtiva object : TipoModeloDoctoOficial.TagsTermoDividaAtiva.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperaTagsProtocolo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsProtocolo object : TipoModeloDoctoOficial.TagsProtocolo.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperaTagsAutoInfracaoFiscalizacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao object : TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperaTagsParecerRecursoFiscalizacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsParecerRecursoFiscalizacao object : TipoModeloDoctoOficial.TagsParecerRecursoFiscalizacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperaTagsPeticaoDividaAtiva() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsPeticaoDividaAtiva object : TipoModeloDoctoOficial.TagsPeticaoDividaAtiva.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperaTagsFiscalizacaoRBTrans() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans object : TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperaTagsRBTrans() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsRBTrans object : TipoModeloDoctoOficial.TagsRBTrans.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperarTagsOTTRBTrans() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (TipoModeloDoctoOficial.TagsCertificadoOTTRBTrans object : TipoModeloDoctoOficial.TagsCertificadoOTTRBTrans.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperarTagsCondutorOTTRBTrans() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (TipoModeloDoctoOficial.TagsCondutorOTTRBTrans object : TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperaTagsFiscalizacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsFiscalizacao object : TipoModeloDoctoOficial.TagsFiscalizacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperaTagsCadastroImobiliario() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsCadastroImobiliario object : TipoModeloDoctoOficial.TagsCadastroImobiliario.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperaTagsCadastroRural() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsCadastroRural object : TipoModeloDoctoOficial.TagsCadastroRural.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperaTagsPessoaFisica() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsPessoaFisica object : TipoModeloDoctoOficial.TagsPessoaFisica.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperaTagsPessoaJuridica() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsPessoaJuridica object : TipoModeloDoctoOficial.TagsPessoaJuridica.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public void recuperaTributo(SelectEvent evento) {
        selecionado.setTributo((Tributo) evento.getObject());
    }

    public void limparValores() {
        if (selecionado.getTributo() == null || selecionado.getTributo().getId() == null) {
            selecionado.setValor(null);
            selecionado.setValidadeDam(null);
        }
    }

    public boolean renderizaExcluir(AtributoDoctoOficial atributo) {
        if (atributo != null && atributo.getId() != null) {
            return tipoDoctoOficialFacade.existeAtributoNaSolicitacao(atributo);
        } else {
            return false;
        }

    }

    public void adicionarCabecalho() {
        String caminhoDaImagem = geraUrlImagemDir() + "img/Brasao_de_Rio_Branco.gif";
        String conteudo =
            "<table style=\"width: 100%;\">"
                + "<tbody>"
                + "<tr>"
                + "<td style=\"text-align: left;\" align=\"right\"><img src=\"../../../../" + caminhoDaImagem + "\" alt=\"PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO\" /></td>"
                + "<td><span style=\"font-size: small;\">PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO</span><br /><span style=\"font-size: small;\">Secretaria Municipal de Finan&ccedil;as</span> <strong><br /></strong></td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + "<br>&nbsp;<br><br>"
                + "<br><br><br> *** INSIRA SEU TEXTO AQUI *** ";
        RequestContext.getCurrentInstance().execute("insertAtCursor('" + conteudo + "')");
    }

    public void adicionaRodape() {
        String conteudo =
            "<div id=\"footer\">"
                + "<table style=\"width: 100%;\">"
                + "<tbody>"
                + "<tr>"
                + "<td>*** INSIRA SEU TEXTO AQUI ***</td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + "<div>";
        RequestContext.getCurrentInstance().execute("insertAtCursor('" + conteudo + "')");
    }

    public String geraUrlImagemDir() {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        final StringBuffer url = request.getRequestURL();
        String test = url.toString();
        String[] quebrado = test.split("/");
        StringBuilder b = new StringBuilder();
        b.append("/").append(quebrado[3]).append("/");
        return b.toString();
    }

    public void adicionaDtaHojeAno() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_ANO.name() + "')");
    }

    public void adicionaDtaHojeMes() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_MES.name() + "')");
    }

    public void adicionaDtaHojeDiaExtenso() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_DIA_EXTENSO.name() + "')");
    }

    public void adicionaDtaHojeMesExtenso() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_MES_EXTENSO.name() + "')");
    }

    public void adicionaDtaHojeAnoExtenso() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_ANO_EXTENSO.name() + "')");
    }

    public void adicionaDtaHojeDia() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_DIA.name() + "')");
    }

    public void adicionaQuebraPagina() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsConfiguracaoDocumento.QUEBRA_PAGINA.name() + "')");
    }

    public void adicionaIP() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsConfiguracaoDocumento.IP.name() + "')");
    }

    public void adicionaUsuario() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsConfiguracaoDocumento.USUARIO.name() + "')");
    }

    public void adicionarStringNoEditor(String valor) {
        RequestContext.getCurrentInstance().execute("insertAtCursor('" + valor + "')");
    }

    public List<TextoFixo> getTextoFixos() {
        return textoFixos;
    }

    public void setTextoFixos(List<TextoFixo> textoFixos) {
        this.textoFixos = textoFixos;
    }

    public void selecionouTextoFixo(TextoFixo texto) {
        this.textoFixoSelecionado = texto;
    }

    public void recuperaTextoFixosDoTipoDeCadastro() {
        textoFixos = tipoDoctoOficialFacade.getTextoFixoFacade().buscarTextoFixoDoTipoDeCadastro(recuperaTipoCadastroTributario());
    }

    public TextoFixo getTextoFixoSelecionado() {
        return textoFixoSelecionado;
    }

    public void setTextoFixoSelecionado(TextoFixo textoFixoSelecionado) {
        this.textoFixoSelecionado = textoFixoSelecionado;
    }

    public AssinaturaTipoDoctoOficial getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(AssinaturaTipoDoctoOficial assinatura) {
        this.assinatura = assinatura;
    }

    public TipoCadastroTributario recuperaTipoCadastroTributario() {
        if (selecionado.getTipoCadastroDoctoOficial() == null) {
            return null;
        }
        if (selecionado.getTipoCadastroDoctoOficial().equals(TipoCadastroDoctoOficial.CADASTROECONOMICO)) {
            return TipoCadastroTributario.ECONOMICO;
        }
        if (selecionado.getTipoCadastroDoctoOficial().equals(TipoCadastroDoctoOficial.CADASTROIMOBILIARIO)) {
            return TipoCadastroTributario.IMOBILIARIO;
        }
        if (selecionado.getTipoCadastroDoctoOficial().equals(TipoCadastroDoctoOficial.CADASTRORURAL)) {
            return TipoCadastroTributario.RURAL;
        }
        if (selecionado.getTipoCadastroDoctoOficial().equals(TipoCadastroDoctoOficial.PESSOAFISICA)) {
            return TipoCadastroTributario.PESSOA;
        }
        if (selecionado.getTipoCadastroDoctoOficial().equals(TipoCadastroDoctoOficial.PESSOAJURIDICA)) {
            return TipoCadastroTributario.PESSOA;
        }
        return null;
    }

    private List<SelectItem> getRecuperaTagsContratoRendasPatrimoniais() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais object : TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    private List<SelectItem> getRecuperaTagsContratoCEASA() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsContratoCEASA object : TipoModeloDoctoOficial.TagsContratoCEASA.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    private List<SelectItem> getRecuperaTagsIsencaoIPTU() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsIsencaoIPTU object : TipoModeloDoctoOficial.TagsIsencaoIPTU.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    private List<SelectItem> getRecuperaTagsCobrancaAdministrativa() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsCobrancaAdministrativa object : TipoModeloDoctoOficial.TagsCobrancaAdministrativa.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    private List<SelectItem> getRecuperaTagsProcessoParcelamento() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsProcessoParcelamento object : TipoModeloDoctoOficial.TagsProcessoParcelamento.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    private List<SelectItem> getTagsAuxilioFuneral() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsAuxilioFuneral object : TipoModeloDoctoOficial.TagsAuxilioFuneral.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    private List<SelectItem> getTagsCertidaoBaixaAtividade() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsCertidaoBaixaAtividade object : TipoModeloDoctoOficial.TagsCertidaoBaixaAtividade.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    private List<SelectItem> getTagsSubvencao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDoctoOficial.TagsSubvencao object : TipoModeloDoctoOficial.TagsSubvencao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    private List<SelectItem> getTagsOrdemGeralMonitormanto() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (TipoModeloDoctoOficial.TagsOrdemGeralMonitoramento object : TipoModeloDoctoOficial.TagsOrdemGeralMonitoramento.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public void novaFinalidade() {
        Web.navegacao("/tipo-de-documento-oficial/novo/",
            "/tributario/configuracoes/certidoes/finalidade/novo/",
            selecionado, tipoDoctoFinalidade);
    }

    public void atualizaListaTags(AtributoDoctoOficial atributo) {
        SelectItem itemListaTag = new SelectItem(atributo.getTag());
        if (!atributo.getAtivo()) {
            SelectItem remover = null;
            for (SelectItem tag : listaTags) {
                if (tag.getValue().equals(atributo.getTag())) {
                    remover = tag;
                    break;
                }
            }
            if (remover != null) {
                listaTags.remove(remover);
            }
        } else {
            listaTags.add(itemListaTag);
        }
    }

    public List<TipoDoctoOficial> completarDocumentoLicenciamentoAmbiental(String parte) {
        return tipoDoctoOficialFacade.tipoDoctoPorModuloTipoCadastro(parte.trim(), ModuloTipoDoctoOficial.DOCUMENTO_LICENCIAMENTO_AMBIENTAL, TipoCadastroDoctoOficial.PESSOAFISICA, TipoCadastroDoctoOficial.PESSOAJURIDICA);
    }

    public void adicionarAssinatura() {
        try {
            validarAssinatura();
            assinatura.setTipoDoctoOficial(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getAssinaturas(), assinatura);
            assinatura = new AssinaturaTipoDoctoOficial();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    private void validarAssinatura() {
        ValidacaoException ve = new ValidacaoException();

        if (assinatura.getUsuarioSistema() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Usuário Sistema deve ser informado.");
        }
        if (assinatura.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Início de Vigência deve ser informado.");
        }
        if (assinatura.getInicioVigencia() != null && Util.getDataHoraMinutoSegundoZerado(assinatura.getInicioVigencia())
            .compareTo(Util.getDataHoraMinutoSegundoZerado(new Date())) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data Inicial de Vigência deve ser igual ou posterior a data atual.");
        }
        if (assinatura.getInicioVigencia() != null && assinatura.getFinalVigencia() != null &&
            assinatura.getInicioVigencia().compareTo(assinatura.getFinalVigencia()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data Final de Vigência deve ser posterior a data Inicial de Vigência.");
        }
        if (assinatura.getUsuarioSistema() != null) {
            for (AssinaturaTipoDoctoOficial assinaturaDocto : selecionado.getAssinaturas()) {
                if (assinaturaDocto.getUsuarioSistema() != null && assinaturaDocto.getUsuarioSistema().equals(assinatura.getUsuarioSistema())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Já foi adicionada assinatura para esse usuário.");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    public void removerAssinatura(AssinaturaTipoDoctoOficial assinatura) {
        if (assinatura != null) {
            selecionado.getAssinaturas().remove(assinatura);
        }
    }
    public List<TipoDoctoOficial> completarTermoImovel(String parte) {
        return tipoDoctoOficialFacade.tipoDoctoPorModuloTipoCadastro(parte.trim(), ModuloTipoDoctoOficial.TERMOADVERTENCIA, TipoCadastroDoctoOficial.CADASTROIMOBILIARIO);
    }

    public List<TipoDoctoOficial> completarTermoEconomico(String parte) {
        return tipoDoctoOficialFacade.tipoDoctoPorModuloTipoCadastro(parte.trim(), ModuloTipoDoctoOficial.TERMOADVERTENCIA, TipoCadastroDoctoOficial.CADASTROECONOMICO);
    }

    public List<TipoDoctoOficial> completarTermoRural(String parte) {
        return tipoDoctoOficialFacade.tipoDoctoPorModuloTipoCadastro(parte.trim(), ModuloTipoDoctoOficial.TERMOADVERTENCIA, TipoCadastroDoctoOficial.CADASTRORURAL);
    }

    public List<TipoDoctoOficial> completarTermoPf(String parte) {
        return tipoDoctoOficialFacade.tipoDoctoPorModuloTipoCadastro(parte.trim(), ModuloTipoDoctoOficial.TERMOADVERTENCIA, TipoCadastroDoctoOficial.PESSOAFISICA);
    }

    public List<TipoDoctoOficial> completarTermoPj(String parte) {
        return tipoDoctoOficialFacade.tipoDoctoPorModuloTipoCadastro(parte.trim(), ModuloTipoDoctoOficial.TERMOADVERTENCIA, TipoCadastroDoctoOficial.PESSOAJURIDICA);
    }

    public List<TipoDoctoOficial> completarAutoImovel(String parte) {
        return tipoDoctoOficialFacade.tipoDoctoPorModuloTipoCadastro(parte.trim(), ModuloTipoDoctoOficial.AUTOINFRACAO, TipoCadastroDoctoOficial.CADASTROIMOBILIARIO);
    }

    public List<TipoDoctoOficial> completarAutoEconomico(String parte) {
        return tipoDoctoOficialFacade.tipoDoctoPorModuloTipoCadastro(parte.trim(), ModuloTipoDoctoOficial.AUTOINFRACAO, TipoCadastroDoctoOficial.CADASTROECONOMICO);
    }

    public List<TipoDoctoOficial> completarAutoRural(String parte) {
        return tipoDoctoOficialFacade.tipoDoctoPorModuloTipoCadastro(parte.trim(), ModuloTipoDoctoOficial.AUTOINFRACAO, TipoCadastroDoctoOficial.CADASTRORURAL);
    }

    public List<TipoDoctoOficial> CompletaTiposDoctoOficialPorModuloLicencaETR(String parte) {
        return tipoDoctoOficialFacade.recuperaTiposDoctoOficialPorModulo(ModuloTipoDoctoOficial.LICENCA_ETR, parte);
    }

    public List<TipoDoctoOficial> completarAutoPf(String parte) {
        return tipoDoctoOficialFacade.tipoDoctoPorModuloTipoCadastro(parte.trim(), ModuloTipoDoctoOficial.AUTOINFRACAO, TipoCadastroDoctoOficial.PESSOAFISICA);
    }

    public List<TipoDoctoOficial> completarAutoPj(String parte) {
        return tipoDoctoOficialFacade.tipoDoctoPorModuloTipoCadastro(parte.trim(), ModuloTipoDoctoOficial.AUTOINFRACAO, TipoCadastroDoctoOficial.PESSOAJURIDICA);
    }

    public List<TipoDoctoOficial> completarTipoCertidaoMarcaFogo(String parte) {
        return tipoDoctoOficialFacade.tipoDoctoPorModuloTipoCadastro(parte.trim(),
            ModuloTipoDoctoOficial.CERTIDAO_MARCA_FOGO);
    }
}
