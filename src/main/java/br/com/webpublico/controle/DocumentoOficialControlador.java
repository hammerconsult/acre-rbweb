/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.AssinaturaDocumentoOficial;
import br.com.webpublico.entidades.DocumentoOficial;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Seguranca;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Date;

/**
 * @author claudio
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "assinar-documento-oficial", pattern = "/documento-oficial/assinar/#{documentoOficialControlador.id}/", viewId = "/faces/tributario/certidao/documentooficial/assinar.xhtml"),
    @URLMapping(id = "listar-assinatura-documento-oficial", pattern = "/documento-oficial/listar-assinatura/", viewId = "/faces/tributario/certidao/documentooficial/lista-assinatura.xhtml"),
    @URLMapping(id = "editar-documento-oficial", pattern = "/documento-oficial/editar/#{documentoOficialControlador.id}/", viewId = "/faces/tributario/certidao/documentooficial/edita.xhtml")
})
public class DocumentoOficialControlador extends PrettyControlador<DocumentoOficial> implements Serializable, CRUD {

    @EJB
    private DocumentoOficialFacade facade;
    @EJB
    private ConfiguracaoAssinaturaFacade configuracaoAssinaturaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private AssinaturaDocumentoOficial assinatura;
    private String senhaInformada;


    public DocumentoOficialControlador() {
        super(DocumentoOficial.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/documento-oficial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "assinar-documento-oficial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void prepararAssinatura() {
        super.ver();
        this.assinatura = configuracaoAssinaturaFacade.buscarAssinaturaPorDoctoOficialUsuario(selecionado, sistemaFacade.getUsuarioCorrente());
    }

    @URLAction(mappingId = "editar-documento-oficial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void redireciona() {
        FacesUtil.redirecionamentoInterno("/documento-oficial/listar-assinatura/");
    }

    public void salvarRetornandoOrigem() {
        try {
            if (validaRegrasParaSalvar()) {
                if (Operacoes.NOVO.equals(operacao)) {
                    getFacede().salvarNovo(selecionado);
                } else {
                    getFacede().salvar(selecionado);
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
                redireciona(Web.getCaminhoOrigem());
            }
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void cancelarRetornandoOrigem() {
        Web.getEsperaRetorno();
        redireciona(Web.getCaminhoOrigem());
    }

    public void gerarDocumento() {
        try {
            if (facade.isPermitirImpressao(selecionado)) {
                facade.emiteDocumentoOficial(selecionado.getConteudo());
            }
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public void assinarDocumento() {
        try {
            if (this.assinatura != null) {
                validarSenhaUsuario();
                validarAssinaturaUsuario(assinatura);
                facade.assinarDocumento(assinatura);
            }
            FacesUtil.addOperacaoRealizada("Documento assinado com sucesso.");
            redireciona();
        } catch (Exception e) {
            logger.error("Erro ao assinar o documento ", e);
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private void validarSenhaUsuario() {
        if (senhaInformada != null && !senhaInformada.isEmpty()) {
            UsuarioSistema usuarioCorrente = sistemaFacade.getUsuarioCorrente();
            String senha = UsuarioSistemaControlador.PASSWORD_ENCODER.encodePassword(Seguranca.md5(senhaInformada), usuarioCorrente.getSalt());
            if (!senha.equals(usuarioCorrente.getSenha())) {
                throw new ExcecaoNegocioGenerica("Senha incorreta.");
            }
        } else {
            throw new ExcecaoNegocioGenerica("Campo senha é deve ser informado.");
        }
    }

    private void validarAssinaturaUsuario(AssinaturaDocumentoOficial assinatura) {
        if (assinatura.getDataLimite() != null &&
            DataUtil.getDataHoraMinutoSegundoZerado(assinatura.getDataLimite()).before(DataUtil.getDataHoraMinutoSegundoZerado(new Date()))) {
            throw new ExcecaoNegocioGenerica("Já passou o prazo de assinatura.");
        }
    }

    public AssinaturaDocumentoOficial getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(AssinaturaDocumentoOficial assinatura) {
        this.assinatura = assinatura;
    }

    public String getSenhaInformada() {
        return senhaInformada;
    }

    public void setSenhaInformada(String senhaInformada) {
        this.senhaInformada = senhaInformada;
    }
}
