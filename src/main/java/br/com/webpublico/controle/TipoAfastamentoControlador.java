/*
 * Codigo gerado automaticamente em Mon Sep 05 09:12:19 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.esocial.TipoAcidenteTransito;
import br.com.webpublico.enums.rh.esocial.TipoAfastamentoESocial;
import br.com.webpublico.enums.rh.previdencia.TipoSituacaoBBPrev;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ManagedBean(name = "tipoAfastamentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoTipoAfastamento", pattern = "/tipo-de-afastamento/novo/", viewId = "/faces/rh/administracaodepagamento/tipoafastamento/edita.xhtml"),
    @URLMapping(id = "listaTipoAfastamento", pattern = "/tipo-de-afastamento/listar/", viewId = "/faces/rh/administracaodepagamento/tipoafastamento/lista.xhtml"),
    @URLMapping(id = "verTipoAfastamento", pattern = "/tipo-de-afastamento/ver/#{tipoAfastamentoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipoafastamento/visualizar.xhtml"),
    @URLMapping(id = "editarTipoAfastamento", pattern = "/tipo-de-afastamento/editar/#{tipoAfastamentoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipoafastamento/edita.xhtml"),
})
public class TipoAfastamentoControlador extends PrettyControlador<TipoAfastamento> implements Serializable, CRUD {

    @EJB
    private TipoAfastamentoFacade tipoAfastamentoFacade;
    @EJB
    private MovimentoSEFIPFacade movimentoSEFIPFacade;
    @EJB
    private MotivoAfastamentoRaisFacade motivoAfastamentoRaisFacade;
    @EJB
    private TipoPrevidenciaFPFacade tipoPrevidenciaFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    private ConverterAutoComplete converterMovimento;
    private ConverterGenerico converterMotivoAfastamentoRais;
    private FaixaReferenciaFP faixaReferenciaFPSelecionada;
    private TipoPrevidenciaFP tipoPrevidenciaFP;
    private TipoAfastamentoTipoPrevidenciaFP tipoAfastamentoTipoPrevidenciaFP;
    private List<TipoAfastamentoTipoPrevidenciaFP> tipoAfastamentoTipoPrevidenciaFPList;

    public TipoAfastamentoControlador() {
        super(TipoAfastamento.class);
        tipoAfastamentoTipoPrevidenciaFPList = new ArrayList<>();
    }

    public Converter getConverterMovimento() {
        if (converterMovimento == null) {
            converterMovimento = new ConverterAutoComplete(MovimentoSEFIP.class, movimentoSEFIPFacade);
        }
        return converterMovimento;
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoAfastamentoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-de-afastamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public FaixaReferenciaFP getFaixaReferenciaFPSelecionada() {
        return faixaReferenciaFPSelecionada;
    }

    public void setFaixaReferenciaFPSelecionada(FaixaReferenciaFP faixaReferenciaFPSelecionada) {
        this.faixaReferenciaFPSelecionada = faixaReferenciaFPSelecionada;
    }

    public List<SelectItem> getClasseAfastamento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (ClasseAfastamento object : ClasseAfastamento.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<MovimentoSEFIP> completaMovimentosAfastamento(String parte) {
        return movimentoSEFIPFacade.completaMovimentoSEFIP(TipoMovimentoSEFIP.AFASTAMENTO_SEFIP, parte.trim());
    }

    public List<MovimentoSEFIP> completaMovimentosRetorno(String parte) {
        return movimentoSEFIPFacade.completaMovimentoSEFIP(TipoMovimentoSEFIP.RETORNO_SEFIP, parte.trim());
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        ClasseAfastamento classeAfastamento = selecionado.getClasseAfastamento();
        if (operacao == Operacoes.NOVO) {
            Long novoCodigo = tipoAfastamentoFacade.retornaUltimoCodigoLong();
            if (!novoCodigo.equals(selecionado.getCodigo())) {
                FacesUtil.addInfo("Atenção!", "O Código " + getSelecionado().getCodigo() + " já está sendo usado e foi gerado o novo código " + novoCodigo + " !");
                selecionado.setCodigo(novoCodigo.intValue());
            }
        }
        if (tipoAfastamentoFacade.existeCodigo(selecionado)) {
            ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "O Código informado já está cadastrado em outro tipo de afastamento !");
        }

        if ((classeAfastamento == ClasseAfastamento.FALTA_INJUSTIFICADA) || (classeAfastamento == ClasseAfastamento.FALTA_JUSTIFICADA)) {
            if (selecionado.getCarencia() != null) {
                ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "Se a classe do afastamento for uma falta, não pode haver carência !");
            }
        }
        if (isAcidenteTransito() && selecionado.getTipoAcidenteTransito() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Para o Tipo Afastamento E-Social escolhido é necessário informar o Tipo Acidente de Trânsito");
        }
        ve.lancarException();
    }

    public List<SelectItem> listaMotivosAfastamento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (MotivoAfastamentoRais motivo : motivoAfastamentoRaisFacade.lista()) {
            toReturn.add(new SelectItem(motivo, motivo.toString()));
        }
        return toReturn;
    }

    public Converter getConverterMotivoAfastamentoRais() {
        if (converterMotivoAfastamentoRais == null) {
            converterMotivoAfastamentoRais = new ConverterGenerico(MotivoAfastamentoRais.class, motivoAfastamentoRaisFacade);
        }
        return converterMotivoAfastamentoRais;
    }

    public List<SelectItem> getSexo() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Sexo object : Sexo.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    @URLAction(mappingId = "novoTipoAfastamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        Long novoCodigo = tipoAfastamentoFacade.retornaUltimoCodigoLong();
        selecionado.setCodigo(novoCodigo.intValue());
        selecionado.setMaximoPerdaPeriodoAquisitivo(0);
        selecionado.setAlongarPeriodoAquisitivo(false);
        selecionado.setAtivo(Boolean.TRUE);
    }

    @URLAction(mappingId = "verTipoAfastamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarTipoPrevidencias();
    }

    @URLAction(mappingId = "editarTipoAfastamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarTipoPrevidencias();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex){
            logger.error("Erro ao salvar o tipo de afastamento {}", ex);
            descobrirETratarException(ex);
        }
    }

    public List<ReferenciaFP> completaReferenciaFP(String parte) {
        return tipoAfastamentoFacade.getReferenciaFPFacade().listaFiltrando(parte, "codigo", "descricao");
    }

    public boolean renderizaInfoReferenciaFp() {
        try {
            if (selecionado.getReferenciaFP() != null) {
                if (selecionado.getReferenciaFP().getFaixasReferenciasFPs() != null && !selecionado.getReferenciaFP().getFaixasReferenciasFPs().isEmpty()) {
                    return true;
                }
            }
            return false;
        } catch (Exception npe) {
            return false;
        }
    }

    public void inicializaListasDaReferenciaFP() {
        selecionado.setReferenciaFP(tipoAfastamentoFacade.getReferenciaFPFacade().recuperar(selecionado.getReferenciaFP().getId()));
    }

    public void carregaFaixaReferencaFP(FaixaReferenciaFP frfp) {
        faixaReferenciaFPSelecionada = frfp;
        faixaReferenciaFPSelecionada = tipoAfastamentoFacade.getFaixaReferenciaFPFacade().recuperar(faixaReferenciaFPSelecionada.getId());
    }

    public void cancelaFaixaReferenciaFP() {
        faixaReferenciaFPSelecionada = null;
    }

    public List<SelectItem> getTipoAfastamentoESocial() {
        return Util.getListSelectItem(TipoAfastamentoESocial.values());
    }

    public List<SelectItem> getTipoAcidenteTransito() {
        return Util.getListSelectItem(TipoAcidenteTransito.values());
    }

    public List<SelectItem> getTiposSituacoesBBPrev() {
        return Util.getListSelectItem(TipoSituacaoBBPrev.values());
    }

    public boolean isAcidenteTransito() {
        return TipoAfastamentoESocial.ACIDENTE_DOENCA_TRABALHO.equals(selecionado.getTipoAfastamentoESocial()) ||
            TipoAfastamentoESocial.ACIDENTE_DOENCA_NAO_RELACIONADA_TRABALHO.equals(selecionado.getTipoAfastamentoESocial());
    }

    public void limparAcidenteDeTransito() {
        if (!isAcidenteTransito()) {
            selecionado.setTipoAcidenteTransito(null);
        }
    }

    public void novoTipoPrevidencia() {
        tipoAfastamentoTipoPrevidenciaFP = new TipoAfastamentoTipoPrevidenciaFP();
    }

    public void confirmarTipoPrevidencia() {
        try {
        validarTipoPrevidencia();
        validarTipoPrevidenciaJaAdicionado(tipoPrevidenciaFP);
        recuperarObjeto();
        tipoAfastamentoTipoPrevidenciaFP.setTipoAfastamento(selecionado);
        tipoAfastamentoTipoPrevidenciaFP.setTipoPrevidenciaFP(tipoPrevidenciaFP);
        tipoAfastamentoTipoPrevidenciaFPList.add(tipoAfastamentoTipoPrevidenciaFP);
        selecionado.setTipoAfastamentoTipoPrevidenciaFPList(tipoAfastamentoTipoPrevidenciaFPList);
        cancelarTipoPrevidencia();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void cancelarTipoPrevidencia() {
        tipoAfastamentoTipoPrevidenciaFP = null;
        tipoPrevidenciaFP = null;
    }

    private void validarTipoPrevidenciaJaAdicionado(TipoPrevidenciaFP tipoPrevidenciaFP) {
        ValidacaoException ve = new ValidacaoException();

        if (tipoAfastamentoTipoPrevidenciaFPList != null && !tipoAfastamentoTipoPrevidenciaFPList.isEmpty()) {
            boolean jaAdicionado = tipoAfastamentoTipoPrevidenciaFPList.stream()
                .anyMatch(tipoAfast -> tipoPrevidenciaFP.getId().equals(tipoAfast.getTipoPrevidenciaFP().getId()));

            if (jaAdicionado) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Tipo de Previdência <b>" + tipoPrevidenciaFP + "</b> já está adicionado.");
                ve.lancarException();
            }
        }
    }

    private void validarTipoPrevidencia() {
        ValidacaoException ve = new ValidacaoException();
        if (tipoPrevidenciaFP == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Tipo de Previdência deve ser informado.");
        }
        ve.lancarException();
    }

    public void removerTipoPrevidencia(TipoAfastamentoTipoPrevidenciaFP tipoAfastamentoTipoPrevidenciaFP) {
        tipoAfastamentoTipoPrevidenciaFPList.remove(tipoAfastamentoTipoPrevidenciaFP);
        selecionado.setTipoAfastamentoTipoPrevidenciaFPList(tipoAfastamentoTipoPrevidenciaFPList);
    }

    public void recuperarTipoPrevidencias() {
        tipoAfastamentoTipoPrevidenciaFPList =  tipoAfastamentoFacade.recuperarTipoAfastamentoTipoPrevidenciaFPPorTipoAfastamento(selecionado.getId());
    }

    public TipoPrevidenciaFP getTipoPrevidenciaFP() {
        return tipoPrevidenciaFP;
    }

    public void setTipoPrevidenciaFP(TipoPrevidenciaFP tipoPrevidenciaFP) {
        this.tipoPrevidenciaFP = tipoPrevidenciaFP;
    }

    public TipoAfastamentoTipoPrevidenciaFP getTipoAfastamentoTipoPrevidenciaFP() {
        return tipoAfastamentoTipoPrevidenciaFP;
    }

    public void setTipoAfastamentoTipoPrevidenciaFP(TipoAfastamentoTipoPrevidenciaFP tipoAfastamentoTipoPrevidenciaFP) {
        this.tipoAfastamentoTipoPrevidenciaFP = tipoAfastamentoTipoPrevidenciaFP;
    }

    public List<TipoAfastamentoTipoPrevidenciaFP> getTipoAfastamentoTipoPrevidenciaFPList() {
        return tipoAfastamentoTipoPrevidenciaFPList;
    }

    public void setTipoAfastamentoTipoPrevidenciaFPList(List<TipoAfastamentoTipoPrevidenciaFP> tipoAfastamentoTipoPrevidenciaFPList) {
        this.tipoAfastamentoTipoPrevidenciaFPList = tipoAfastamentoTipoPrevidenciaFPList;
    }

    public List<SelectItem> getItemTipoPrevidenciaFp() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPrevidenciaFP object : tipoPrevidenciaFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

}
