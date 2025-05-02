package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.OrigemDoctoFiscalLiquidacaoVO;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DoctoFiscalLiquidacaoFacade;
import br.com.webpublico.reinf.eventos.EventoReinfDTO;
import br.com.webpublico.reinf.eventos.TipoArquivoReinf;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "edita-docto-fiscal-liquidacao", pattern = "/documento-fiscal/editar/#{doctoFiscalLiquidacaoControlador.id}/", viewId = "/faces/financeiro/docto-fiscal-liquidacao/edita.xhtml"),
    @URLMapping(id = "ver-docto-fiscal-liquidacao", pattern = "/documento-fiscal/ver/#{doctoFiscalLiquidacaoControlador.id}/", viewId = "/faces/financeiro/docto-fiscal-liquidacao/visualizar.xhtml"),
    @URLMapping(id = "listar-docto-fiscal-liquidacao", pattern = "/documento-fiscal/listar/", viewId = "/faces/financeiro/docto-fiscal-liquidacao/lista.xhtml")
})
public class DoctoFiscalLiquidacaoControlador extends PrettyControlador<DoctoFiscalLiquidacao> implements Serializable, CRUD {
    @EJB
    private DoctoFiscalLiquidacaoFacade facade;
    private Boolean isContaDespesaEventoReinf;
    private LiquidacaoDoctoFiscal liquidacaoDoctoFiscal;
    private Boolean hasPagamentoNaoEstornado;
    private ConfiguracaoContabil configuracaoContabil;
    private OrigemDoctoFiscalLiquidacaoVO origemDocumento;
    private RevisaoAuditoria ultimaRevisaoLiquidacaoDoctoFiscal;
    private EventoReinfDTO eventoR2010Atual;
    private EventoReinfDTO eventoR4020Atual;

    @URLAction(mappingId = "ver-docto-fiscal-liquidacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarCamposEditarEVer();
    }

    @URLAction(mappingId = "edita-docto-fiscal-liquidacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarCamposEditarEVer();
        eventoR2010Atual = facade.buscarEventoAtualReinfDoDoctoFiscalPorTipo(selecionado, TipoArquivoReinf.R2010V2);
        eventoR4020Atual = facade.buscarEventoAtualReinfDoDoctoFiscalPorTipo(selecionado, TipoArquivoReinf.R4020);
    }

    private void recuperarCamposEditarEVer() {
        atualizarIsContaDespesaEventoReinf();
        recuperarLiquidacaoDoctoFiscal();
        atualizarHasPagamentoNaoEstornado();
        buscarConfiguracaoContabil();
        origemDocumento = facade.buscarOrigemDoctoFiscalLiquidacaoVO(selecionado);
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (liquidacaoDoctoFiscal != null && !hasPagamentoNaoEstornado) {
                liquidacaoDoctoFiscal.setDoctoFiscalLiquidacao(selecionado);
                facade.salvar(liquidacaoDoctoFiscal, isContaDespesaEventoReinf, configuracaoContabil);
            } else {
                facade.salvar(selecionado);
            }
            redireciona();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoDocumentoFiscal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Documento deve ser informado.");
        }
        if (selecionado.getTipoDocumentoFiscal() != null && selecionado.getTipoDocumentoFiscal().getObrigarChaveDeAcesso() && Strings.isNullOrEmpty(selecionado.getChaveDeAcesso())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Chave de Acesso deve ser informado.");
        }
        if (selecionado.getDataDocto() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Emissão deve ser informado.");
        }
        if (Strings.isNullOrEmpty(selecionado.getNumero())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número deve ser informado.");
        }
        ve.lancarException();
    }

    public DoctoFiscalLiquidacaoControlador() {
        super(DoctoFiscalLiquidacao.class);
    }

    public List<TipoDocumentoFiscal> completarTiposDocumentos(String parte) {
        return facade.getTipoDocumentoFiscalFacade().buscarTiposDeDocumentosAtivos(parte.trim());
    }

    public List<SelectItem> getUfs() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (UF obj : facade.getUfFacade().lista()) {
            toReturn.add(new SelectItem(obj, obj.getNome()));
        }
        return toReturn;
    }

    private void atualizarIsContaDespesaEventoReinf() {
        isContaDespesaEventoReinf = facade.isDocumentoFiscalLiquidacaoReinf(selecionado);
    }

    private void recuperarLiquidacaoDoctoFiscal() {
        liquidacaoDoctoFiscal = facade.recuperarLiquidacaoDoctoFiscalPorDoctoFiscalLiquidacao(selecionado);
        if (liquidacaoDoctoFiscal != null) {
            if (liquidacaoDoctoFiscal.getContaExtra() == null) {
                liquidacaoDoctoFiscal.setContaExtra(buscarContaPadraoInss());
            }
            if (liquidacaoDoctoFiscal.getContaExtraIrrf() == null) {
                liquidacaoDoctoFiscal.setContaExtraIrrf(buscarContaPadraoIrrf());
            }
            if (!liquidacaoDoctoFiscal.getOptanteSimplesNacional() && liquidacaoDoctoFiscal.getValorBaseCalculoIrrf() == null) {
                liquidacaoDoctoFiscal.setValorBaseCalculoIrrf(selecionado.getValor());
            }
        }
    }

    public void buscarConfiguracaoContabil() {
        configuracaoContabil = facade.getConfiguracaoContabilFacade().configuracaoContabilVigente();
    }

    public void atualizarHasPagamentoNaoEstornado() {
        hasPagamentoNaoEstornado = facade.hasPagamentoNaoEstornadoParaDoctoFiscalLiquidacao(selecionado);
    }

    public Boolean getContaDespesaEventoReinf() {
        return isContaDespesaEventoReinf == null ? Boolean.FALSE : isContaDespesaEventoReinf;
    }

    public void setContaDespesaEventoReinf(Boolean contaDespesaEventoReinf) {
        isContaDespesaEventoReinf = contaDespesaEventoReinf;
    }

    public LiquidacaoDoctoFiscal getLiquidacaoDoctoFiscal() {
        return liquidacaoDoctoFiscal;
    }

    public void setLiquidacaoDoctoFiscal(LiquidacaoDoctoFiscal liquidacaoDoctoFiscal) {
        this.liquidacaoDoctoFiscal = liquidacaoDoctoFiscal;
    }

    public Boolean getHasPagamentoNaoEstornado() {
        return hasPagamentoNaoEstornado == null ? Boolean.FALSE : hasPagamentoNaoEstornado;
    }

    public void setHasPagamentoNaoEstornado(Boolean hasPagamentoNaoEstornado) {
        this.hasPagamentoNaoEstornado = hasPagamentoNaoEstornado;
    }

    public ContaExtraorcamentaria buscarContaPadraoInss() {
        return facade.buscarContaPadraoInssExercicioCorrente();
    }

    public ContaExtraorcamentaria buscarContaPadraoIrrf() {
        return facade.buscarContaPadraoIrrfExercicioCorrente();
    }

    public OrigemDoctoFiscalLiquidacaoVO getOrigemDocumento() {
        return origemDocumento;
    }

    public void setOrigemDocumento(OrigemDoctoFiscalLiquidacaoVO origemDocumento) {
        this.origemDocumento = origemDocumento;
    }

    public void verRevisaoLiquidacaoDoctoFiscal() {
        Web.getSessionMap().put("pagina-anterior-auditoria-listar", PrettyContext.getCurrentInstance().getRequestURL().toString());
        FacesUtil.redirecionamentoInterno("/auditoria/detalhar/" + ultimaRevisaoLiquidacaoDoctoFiscal.getIdEntidade() + "/" + ultimaRevisaoLiquidacaoDoctoFiscal.getClasseEntidade().getSimpleName() + "/");
    }

    private void buscarUltimaAuditoriaLiquidacaoDoctoFiscal() {
        if (liquidacaoDoctoFiscal != null) {
            try {
                ultimaRevisaoLiquidacaoDoctoFiscal = buscarUltimaAuditoria(LiquidacaoDoctoFiscal.class, liquidacaoDoctoFiscal.getId());
            } catch (Exception ex) {
                logger.error("Houve um erro inesperado!", "Não foi possível recuperar a ultima auditoria da " + LiquidacaoDoctoFiscal.class.getSimpleName() + " e id  " + liquidacaoDoctoFiscal.getId());
                logger.error("{}", ex);
            }
        }
    }

    public RevisaoAuditoria getUltimaRevisaoLiquidacaoDoctoFiscal() {
        if (ultimaRevisaoLiquidacaoDoctoFiscal == null) buscarUltimaAuditoriaLiquidacaoDoctoFiscal();
        return ultimaRevisaoLiquidacaoDoctoFiscal;
    }

    public void setUltimaRevisaoLiquidacaoDoctoFiscal(RevisaoAuditoria ultimaRevisaoLiquidacaoDoctoFiscal) {
        this.ultimaRevisaoLiquidacaoDoctoFiscal = ultimaRevisaoLiquidacaoDoctoFiscal;
    }

    public EventoReinfDTO getEventoR2010Atual() {
        return eventoR2010Atual;
    }

    public void setEventoR2010Atual(EventoReinfDTO eventoR2010Atual) {
        this.eventoR2010Atual = eventoR2010Atual;
    }

    public EventoReinfDTO getEventoR4020Atual() {
        return eventoR4020Atual;
    }

    public void setEventoR4020Atual(EventoReinfDTO eventoR4020Atual) {
        this.eventoR4020Atual = eventoR4020Atual;
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/documento-fiscal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
