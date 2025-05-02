/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Controlador
 *
 */
package br.com.webpublico.controle;


import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.LocalHtmlEstaticoPortalContribuinte;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoPessoa;
import br.com.webpublico.enums.TipoSolicitacaoCadastroPessoa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoPortalContribuinteFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
@URLMappings(mappings = {
    @URLMapping(id = "editar",
        pattern = "/configuracao/portal-contribuinte/",
        viewId = "/faces/comum/configuracao-portal-contribuinte/edita.xhtml")

})

public class ConfiguracaoPortalContribuinteControlador extends PrettyControlador<ConfiguracaoPortalContribuinte> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoPortalContribuinteFacade configuracaoPortalContribuinteFacade;
    private DocumentoObrigatorioPortal documentoTributario;
    private DocumentoObrigatorioPortal documentoContabil;
    private List<DocumentoObrigatorioPortal> documentosPessoaTributario;
    private List<DocumentoObrigatorioPortal> documentosPessoaContabil;
    private UsuarioPermissaoAprovar usuarioPermissaoAprovar;
    private LocalHtmlEstaticoPortalContribuinte localHtmlEstaticoSelecionado;
    private HtmlEstaticoPortalContribuinte htmlEstaticoSelecionado;

    @Override
    public AbstractFacade getFacede() {
        return configuracaoPortalContribuinteFacade;
    }


    @Override
    public String getCaminhoPadrao() {
        return "/configuracao/portal-contribuinte/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "editar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        criarNovoDocumentoTributario();
        criarNovoDocumentoContabil();
        selecionado = configuracaoPortalContribuinteFacade.buscarUtilmo();
        if (selecionado.getId() == null) {
            setOperacao(Operacoes.NOVO);
        } else {
            setOperacao(Operacoes.EDITAR);
        }
        setLocalHtmlEstaticoSelecionado(null);
        setHtmlEstaticoSelecionado(null);
        atualizarDocumentosPorTipo();
        instanciarUsuarioPermissaoAprovar();
        FacesUtil.executaJavaScript("aguarde.hide()");
    }

    private void atualizarDocumentosPorTipo() {
        documentosPessoaTributario = Lists.newArrayList();
        documentosPessoaContabil = Lists.newArrayList();
        if (selecionado.getDocumentosObrigatorios() != null && !selecionado.getDocumentosObrigatorios().isEmpty()) {
            selecionado.getDocumentosObrigatorios().forEach(docObrigatorio -> {
                if (TipoSolicitacaoCadastroPessoa.CONTABIL.equals(docObrigatorio.getTipoSolicitacaoCadastroPessoa())) {
                    documentosPessoaContabil.add(docObrigatorio);
                } else {
                    documentosPessoaTributario.add(docObrigatorio);
                }
            });
        }
    }

    public void alterouLocalHtmlEstatico() {
        if (localHtmlEstaticoSelecionado != null) {
            boolean jaPossuiLocalComEnumSelecionado = false;
            for (HtmlEstaticoPortalContribuinte html : selecionado.getHtmls()) {
                if (html.getLocalHtmlEstaticoPortalContribuinte().equals(localHtmlEstaticoSelecionado)) {
                    jaPossuiLocalComEnumSelecionado = true;
                    htmlEstaticoSelecionado = html;
                    break;
                }
            }
            if (!jaPossuiLocalComEnumSelecionado) {
                HtmlEstaticoPortalContribuinte novoHtml = new HtmlEstaticoPortalContribuinte(selecionado, localHtmlEstaticoSelecionado);
                selecionado.getHtmls().add(novoHtml);
                htmlEstaticoSelecionado = novoHtml;
            }
        } else {
            htmlEstaticoSelecionado = null;
        }
    }

    public void adicionarNovoDocumentoContabil() {
        try {
            validarDocumento(documentoContabil, documentosPessoaContabil);
            documentosPessoaContabil.add(documentoContabil);
            selecionado.getDocumentosObrigatorios().add(documentoContabil);
            criarNovoDocumentoContabil();
        } catch (ValidacaoException va) {
            FacesUtil.printAllFacesMessages(va.getMensagens());
        }
    }

    public void adicionarNovoDocumentoTributario() {
        try {
            validarDocumento(documentoTributario, documentosPessoaTributario);
            documentosPessoaTributario.add(documentoTributario);
            selecionado.getDocumentosObrigatorios().add(documentoTributario);
            criarNovoDocumentoTributario();
        } catch (ValidacaoException va) {
            FacesUtil.printAllFacesMessages(va.getMensagens());
        }
    }

    public void validarDocumento(DocumentoObrigatorioPortal documento, List<DocumentoObrigatorioPortal> documentosObrigatorios) {
        ValidacaoException exception = new ValidacaoException();
        Util.validarCampos(documento);
        for (DocumentoObrigatorioPortal documentoObrigatorioPortal : documentosObrigatorios) {
            if (documentoObrigatorioPortal.getDescricao().toLowerCase().trim().equals(documento.getDescricao().trim().toLowerCase())
                && documentoObrigatorioPortal.getTipoPessoa().equals(documento.getTipoPessoa())) {
                exception.adicionarMensagemDeOperacaoNaoPermitida("Já existe um documento adicionado com essa mesma descrição");
            }
        }
        exception.lancarException();
    }

    private void criarNovoDocumentoTributario() {
        documentoTributario = new DocumentoObrigatorioPortal();
        documentoTributario.setConfiguracao(selecionado);
        documentoTributario.setTipoSolicitacaoCadastroPessoa(TipoSolicitacaoCadastroPessoa.TRIBUTARIO);
    }

    private void criarNovoDocumentoContabil() {
        documentoContabil = new DocumentoObrigatorioPortal();
        documentoContabil.setConfiguracao(selecionado);
        documentoContabil.setTipoSolicitacaoCadastroPessoa(TipoSolicitacaoCadastroPessoa.CONTABIL);
    }

    public void removerDocumento(DocumentoObrigatorioPortal documento) {
        documentosPessoaContabil.remove(documento);
        documentosPessoaTributario.remove(documento);
        selecionado.getDocumentosObrigatorios().remove(documento);
    }

    public List<SelectItem> getTiposPessoa() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (TipoPessoa tipo : TipoPessoa.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getHtmlsEstaticos() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (LocalHtmlEstaticoPortalContribuinte tipo : LocalHtmlEstaticoPortalContribuinte.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public DocumentoObrigatorioPortal getDocumentoTributario() {
        return documentoTributario;
    }

    public void setDocumentoTributario(DocumentoObrigatorioPortal documentoTributario) {
        this.documentoTributario = documentoTributario;
    }

    public DocumentoObrigatorioPortal getDocumentoContabil() {
        return documentoContabil;
    }

    public void setDocumentoContabil(DocumentoObrigatorioPortal documentoContabil) {
        this.documentoContabil = documentoContabil;
    }

    public List<DocumentoObrigatorioPortal> getDocumentosPessoaTributario() {
        return documentosPessoaTributario;
    }

    public void setDocumentosPessoaTributario(List<DocumentoObrigatorioPortal> documentosPessoaTributario) {
        this.documentosPessoaTributario = documentosPessoaTributario;
    }

    public LocalHtmlEstaticoPortalContribuinte getLocalHtmlEstaticoSelecionado() {
        return localHtmlEstaticoSelecionado;
    }

    public void setLocalHtmlEstaticoSelecionado(LocalHtmlEstaticoPortalContribuinte localHtmlEstaticoSelecionado) {
        this.localHtmlEstaticoSelecionado = localHtmlEstaticoSelecionado;
    }

    public HtmlEstaticoPortalContribuinte getHtmlEstaticoSelecionado() {
        return htmlEstaticoSelecionado;
    }

    public void setHtmlEstaticoSelecionado(HtmlEstaticoPortalContribuinte htmlEstaticoSelecionado) {
        this.htmlEstaticoSelecionado = htmlEstaticoSelecionado;
    }

    public List<DocumentoObrigatorioPortal> getDocumentosPessoaContabil() {
        return documentosPessoaContabil;
    }

    public void setDocumentosPessoaContabil(List<DocumentoObrigatorioPortal> documentosPessoaContabil) {
        this.documentosPessoaContabil = documentosPessoaContabil;
    }

    public List<Divida> completarDividas(String filtro) {
        return configuracaoPortalContribuinteFacade.getDividaFacade().listaFiltrandoDividas(filtro.trim(), "descricao");
    }

    public List<Tributo> completarTributos(String parte) {
        return configuracaoPortalContribuinteFacade.getTributoFacade().listaFiltrando(parte.trim(), "descricao");
    }

    private void instanciarUsuarioPermissaoAprovar() {
        usuarioPermissaoAprovar = new UsuarioPermissaoAprovar(selecionado, null);
    }

    public void removerUsuarioAprovacaoCredor(UsuarioPermissaoAprovar usuario) {
        selecionado.getPermissoesAprovacaoCredores().remove(usuario);
    }

    public void adicionarUsuarioAprovacaoCredor() {
        try {
            validarAdicionarUsuarioAprovacaoCredor();
            selecionado.getPermissoesAprovacaoCredores().add(usuarioPermissaoAprovar);
            instanciarUsuarioPermissaoAprovar();
        } catch (ValidacaoException va) {
            FacesUtil.printAllFacesMessages(va.getMensagens());
        }
    }

    private void validarAdicionarUsuarioAprovacaoCredor() {
        ValidacaoException ve = new ValidacaoException();
        if (usuarioPermissaoAprovar.getUsuarioSistema() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Usuário deve ser informado");
        }
        ve.lancarException();
    }

    public List<UsuarioSistema> completarUsuarios(String filtro) {
        return configuracaoPortalContribuinteFacade.getUsuarioSistemaFacade().buscarTodosUsuariosPorLoginOuNomeOuCpf(filtro);
    }

    public UsuarioPermissaoAprovar getUsuarioPermissaoAprovar() {
        return usuarioPermissaoAprovar;
    }

    public void setUsuarioPermissaoAprovar(UsuarioPermissaoAprovar usuarioPermissaoAprovar) {
        this.usuarioPermissaoAprovar = usuarioPermissaoAprovar;
    }

    @Override
    public void redireciona() {
        FacesUtil.redirecionamentoInterno("/configuracao/portal-contribuinte/");
    }
}
