package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.nfse.domain.*;
import br.com.webpublico.nfse.enums.MotivoCancelamentoNota;
import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.nfse.enums.TipoCancelamento;
import br.com.webpublico.nfse.enums.TipoDocumentoNfse;
import br.com.webpublico.nfse.facades.CancelamentoNfseFacade;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.nfse.facades.ServicoDeclaradoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.OptimisticLockException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


@ManagedBean(name = "cancelamentNfseControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-cancelamento-nfse", pattern = "/nfse/cancelamento-nfse/novo/",
        viewId = "/faces/tributario/nfse/cancelamento/edita.xhtml"),
    @URLMapping(id = "listar-cancelamento-nfse", pattern = "/nfse/cancelamento-nfse/listar/",
        viewId = "/faces/tributario/nfse/cancelamento/lista.xhtml"),
    @URLMapping(id = "ver-cancelamento-nfse", pattern = "/nfse/cancelamento-nfse/ver/#{cancelamentNfseControlador.id}/",
        viewId = "/faces/tributario/nfse/cancelamento/visualizar.xhtml"),
})
public class CancelamentoNfseControlador extends PrettyControlador<CancelamentoDeclaracaoPrestacaoServico> implements Serializable, CRUD {

    @EJB
    private CancelamentoNfseFacade facade;
    @EJB
    private ServicoDeclaradoFacade servicoDeclaradoFacade;
    private ServicoDeclarado servicoDeclarado;
    private ServicoDeclarado servicoDeclaradoSubstituido;
    private NotaFiscal notaFiscal;
    private NotaFiscal notaFiscalSubstituta;
    @EJB
    private NotaFiscalFacade notaFiscalFacade;
    private ConverterAutoComplete converterServico;
    private CadastroEconomico prestador;
    private Integer numero;
    private Integer numeroSubstituta;
    private ConfiguracaoNfse configuracaoNfse;

    public CancelamentoNfseControlador() {
        super(CancelamentoDeclaracaoPrestacaoServico.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public NotaFiscal getNotaFiscal() {
        return notaFiscal;
    }

    public NotaFiscal getNotaFiscalSubstituta() {
        return notaFiscalSubstituta;
    }

    public ServicoDeclaradoFacade getServicoDeclaradoFacade() {
        return servicoDeclaradoFacade;
    }

    public ServicoDeclarado getServicoDeclarado() {
        return servicoDeclarado;
    }

    public void setServicoDeclarado(ServicoDeclarado servicoDeclarado) {
        this.servicoDeclarado = servicoDeclarado;
    }

    public ServicoDeclarado getServicoDeclaradoSubstituido() {
        return servicoDeclaradoSubstituido;
    }

    public void setServicoDeclaradoSubstituido(ServicoDeclarado servicoDeclaradoSubstituido) {
        this.servicoDeclaradoSubstituido = servicoDeclaradoSubstituido;
    }

    public CadastroEconomico getPrestador() {
        return prestador;
    }

    public void setPrestador(CadastroEconomico prestador) {
        this.prestador = prestador;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getNumeroSubstituta() {
        return numeroSubstituta;
    }

    public void setNumeroSubstituta(Integer numeroSubstituta) {
        this.numeroSubstituta = numeroSubstituta;
    }

    @Override
    @URLAction(mappingId = "novo-cancelamento-nfse", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataCancelamento(new Date());
        selecionado.setTipoCancelamento(TipoCancelamento.MANUAL);
        selecionado.setFiscalResposavel(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setSituacaoFiscal(CancelamentoDeclaracaoPrestacaoServico.Situacao.DEFERIDO);
        selecionado.setTipoDocumento(CancelamentoDeclaracaoPrestacaoServico.TipoDocumento.NOTA_FISCAL);
        selecionado.setDataDeferimentoFiscal(new Date());
    }

    @Override
    @URLAction(mappingId = "ver-cancelamento-nfse", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        configuracaoNfse = facade.getConfiguracaoNfse();
        DeclaracaoPrestacaoServico declaracaoPrestacaoServico = selecionado.getDeclaracao();
        if (TipoDocumentoNfse.NFSE.equals(declaracaoPrestacaoServico.getTipoDocumento())) {
            notaFiscal = notaFiscalFacade.buscarNotaPorDeclaracaoPrestacaoServico(declaracaoPrestacaoServico);
            if (selecionado.getDeclaracaoSubstituta() != null) {
                notaFiscalSubstituta = notaFiscalFacade.buscarNotaPorDeclaracaoPrestacaoServico(selecionado.getDeclaracaoSubstituta());
            }
        } else {
            servicoDeclarado = servicoDeclaradoFacade.buscarPorDeclaracaoPrestacaoServico(declaracaoPrestacaoServico);
            if (selecionado.getDeclaracaoSubstituta() != null) {
                servicoDeclaradoSubstituido = servicoDeclaradoFacade.buscarPorDeclaracaoPrestacaoServico(selecionado.getDeclaracaoSubstituta());
            }
        }
    }

    public String getCaminhoPadrao() {
        return "/nfse/cancelamento-nfse/";
    }

    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void deferir() {
        try {
            facade.deferir(selecionado, facade.getSistemaFacade().getDataOperacao(),
                facade.getSistemaFacade().getUsuarioCorrente());
            FacesUtil.addOperacaoRealizada("Deferimento realizado com sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex);
        } catch (OptimisticLockException ole) {
            FacesUtil.addOperacaoNaoRealizada("O registro que você está alterando acabou de ser modificado por outro usuário.");
            FacesUtil.addOperacaoNaoRealizada("Verifique as informações e tente novamente.");
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void indeferir() {
        try {

            facade.indeferir(selecionado);
            ver();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex);
        } catch (
            OptimisticLockException ole) {
            FacesUtil.addOperacaoNaoRealizada("O registro que você está alterando acabou de ser modificado por outro usuário.");
            FacesUtil.addOperacaoNaoRealizada("Verifique as informações e tente novamente.");
        } catch (
            ExcecaoNegocioGenerica ex) {
            logger.error(ex.getMessage(), ex);
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public boolean canAvaliar() {
        if (!CancelamentoDeclaracaoPrestacaoServico.Situacao.EM_ANALISE.equals(selecionado.getSituacaoFiscal())) {
            return false;
        }
        return true;

    }

    public List<SelectItem> getMotivos() {
        return Util.getListSelectItem(MotivoCancelamentoNota.values());
    }

    public List<SelectItem> getTiposDocumento() {
        return Util.getListSelectItem(CancelamentoDeclaracaoPrestacaoServico.TipoDocumento.values());
    }

    public void validarServicoDeclaradoSubstitutido() {
        if (servicoDeclaradoSubstituido != null &&
            servicoDeclarado != null &&
            servicoDeclaradoSubstituido.getId().equals(servicoDeclarado.getId())) {
            servicoDeclaradoSubstituido =null;
            FacesUtil.addOperacaoNaoRealizada("O Serviço a ser substituído não pode ser o mesmo serviço a ser cancelado");
        }
    }


    public void buscarNotaFiscal() {
        try {
            validarFiltrosNotaFiscal();
            notaFiscal = facade.getNotaFiscalFacade().buscarNotaFiscal(prestador, numero);
            validarNotaFiscal();
        } catch (ValidacaoException ve) {
            notaFiscal = null;
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            notaFiscal = null;
            FacesUtil.addOperacaoNaoRealizada("Erro ao consultar a NFS-e");
        }
    }

    private void validarFiltrosNotaFiscal() {
        ValidacaoException ve = new ValidacaoException();
        if (prestador == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Prestador deve ser informado.");
        }
        if (numero == null || numero <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O N° da NFS-e deve ser informado.");
        }
        ve.lancarException();
    }


    private void validarNotaFiscal() {
        ValidacaoException ve = new ValidacaoException();
        if (notaFiscal == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhuma NFS-e encontrada com os dados informados.");
        } else {
            if (SituacaoNota.CANCELADA.equals(notaFiscal.getDeclaracaoPrestacaoServico().getSituacao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("NFS-e já está cancelada.");
            }
        }
        ve.lancarException();
    }

    public void buscarNotaFiscalSubstituta() {
        try {
            validarFiltrosNotaFiscalSubstituta();
            notaFiscalSubstituta = facade.getNotaFiscalFacade().buscarNotaFiscal(prestador, numeroSubstituta);
            validarNotaFiscalSubstituta();
        } catch (ValidacaoException ve) {
            notaFiscalSubstituta = null;
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            notaFiscalSubstituta = null;
            FacesUtil.addOperacaoNaoRealizada("Erro ao consultar a NFS-e");
        }
    }

    private void validarFiltrosNotaFiscalSubstituta() {
        ValidacaoException ve = new ValidacaoException();
        if (prestador == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Prestador deve ser informado.");
        }
        if (numeroSubstituta == null || numeroSubstituta <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O N° da NFS-e Substituta deve ser informado.");
        }
        ve.lancarException();
    }

    private void validarNotaFiscalSubstituta() {
        ValidacaoException ve = new ValidacaoException();
        if (notaFiscalSubstituta == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhuma NFS-e encontrada com os dados informados.");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            if (!facade.buscarCancelamentosDaDeclaracao(selecionado.getDeclaracao(), CancelamentoDeclaracaoPrestacaoServico.Situacao.EM_ANALISE).isEmpty()) {
                throw new ValidacaoException("Já existe outra solicitação de cancelamento em análise para essa mesma nota fiscal eletrônica.");
            }
            if (CancelamentoDeclaracaoPrestacaoServico.TipoDocumento.NOTA_FISCAL.equals(selecionado.getTipoDocumento())) {
                if (notaFiscal == null) {
                    throw new ValidacaoException("A Nota Fiscal deve ser informada.");
                }
                selecionado.setDeclaracao(notaFiscal.getDeclaracaoPrestacaoServico());
                if (notaFiscalSubstituta != null && notaFiscalSubstituta.getId() != null) {
                    selecionado.setDeclaracaoSubstituta(notaFiscalSubstituta.getDeclaracaoPrestacaoServico());
                }
                selecionado = facade.salvarCancelamentoManual(selecionado, notaFiscal);
            } else {
                if (servicoDeclarado == null) {
                    throw new ValidacaoException("A Nota Fiscal deve ser informada.");
                }
                selecionado.setDeclaracao(servicoDeclarado.getDeclaracaoPrestacaoServico());
                if (servicoDeclaradoSubstituido != null && servicoDeclaradoSubstituido.getId() != null) {
                    selecionado.setDeclaracaoSubstituta(servicoDeclaradoSubstituido.getDeclaracaoPrestacaoServico());
                }
                selecionado = facade.salvarCancelamentoManual(selecionado, servicoDeclarado);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void verNotaFiscal(NotaFiscal notaFiscal) {
        if (notaFiscal == null) {
            return;
        }
        FacesUtil.redirecionamentoInterno("/nfse/nfse/ver/" + notaFiscal.getId() + "/");
    }

    public void imprimirNotaFiscal(NotaFiscal notaFiscal) {
        try {
            if (notaFiscal == null) {
                return;
            }
            byte[] bytes = facade.getNotaFiscalFacade().gerarImpressaoNotaFiscalSistemaNfse(notaFiscal.getId());
            AbstractReport report = new AbstractReport();
            report.setGeraNoDialog(true);
            report.escreveNoResponse("NotaFiscalEletronica.jasper", bytes);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public List<ServicoDeclarado> completarServicoDeclarado(String parte) {
        if(prestador == null){
            return Lists.newArrayList();
        }
        return servicoDeclaradoFacade.buscarPorNumeroParcialAndSituacao(parte.trim(), prestador.getId(), SituacaoNota.EMITIDA, SituacaoNota.PAGA);
    }

    public ConverterAutoComplete getConverterServico() {
        if (converterServico == null) {
            converterServico = new ConverterAutoComplete(ServicoDeclarado.class, servicoDeclaradoFacade);
        }

        return converterServico;
    }
}
