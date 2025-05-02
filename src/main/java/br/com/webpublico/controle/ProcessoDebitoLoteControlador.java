package br.com.webpublico.controle;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.Divida;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoProcessoDebito;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ProcessoDebitoLoteFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(
        id = "novoProcessoDebitoLote",
        pattern = "/tributario/conta-corrente/processo-de-debitos-lote/novo/",
        viewId = "/faces/tributario/contacorrente/processodebitolote/lista.xhtml"),
})
public class ProcessoDebitoLoteControlador {

    private TipoProcessoDebito tipoProcessoDebito;
    private String protocolo;
    private AtoLegal atoLegal;
    private String motivo;
    private TipoCadastroTributario tipoCadastro;
    private UploadedFile uploadedFile;
    private Divida divida;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Future future;
    @EJB
    private ProcessoDebitoLoteFacade facade;

    public TipoProcessoDebito getTipoProcessoDebito() {
        return tipoProcessoDebito;
    }

    public void setTipoProcessoDebito(TipoProcessoDebito tipoProcessoDebito) {
        this.tipoProcessoDebito = tipoProcessoDebito;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public TipoCadastroTributario getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastroTributario tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    @URLAction(mappingId = "novoProcessoDebitoLote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        tipoProcessoDebito = TipoProcessoDebito.ISENCAO;
    }

    public List<SelectItem> getTiposProcessoDebito() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(TipoProcessoDebito.ISENCAO.name(), TipoProcessoDebito.ISENCAO.getDescricao()));
        return toReturn;
    }

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(TipoCadastroTributario.IMOBILIARIO.name(), TipoCadastroTributario.IMOBILIARIO.getDescricao()));
        return toReturn;
    }

    public void handlePlanilhaCadastros(FileUploadEvent event) {
        uploadedFile = event.getFile();
    }

    public void removerPlanilhaCadastros() {
        uploadedFile = null;
    }

    public List<Divida> completarDivida(String parte) {
        if (tipoCadastro != null) {
            return facade.getDividaFacade().listaDividasDoTipoCadastro(parte, tipoCadastro);
        }
        return Lists.newArrayList();
    }

    public void validarDados() {
        ValidacaoException ve = new ValidacaoException();
        if (tipoProcessoDebito == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo Processo de Débito deve ser informado.");
        }
        if (Strings.isNullOrEmpty(motivo)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo ou Fundamentação Legal deve ser informado.");
        }
        if (tipoCadastro == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Cadastro deve ser informado.");
        }
        if (uploadedFile == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione a Planilha de Cadastros.");
        }
        if (divida == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Dívida deve ser informado.");
        }
        ve.lancarException();
    }

    public void lancar() {
        try {
            validarDados();
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            future = facade.lancar(assistenteBarraProgresso, facade.getSistemaFacade().getExercicioCorrente(),
                facade.getSistemaFacade().getUsuarioCorrente(), tipoProcessoDebito, protocolo, atoLegal, motivo,
                tipoCadastro, uploadedFile.getInputstream(), divida);
            FacesUtil.executaJavaScript("openDialog(dialogAcompanhamento)");
            FacesUtil.executaJavaScript("pollLancamento.start()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void acompanharLancamento() {
        if (future.isDone() || future.isCancelled()) {
            FacesUtil.executaJavaScript("pollLancamento.stop()");
            FacesUtil.redirecionamentoInterno("/tributario/conta-corrente/processo-de-debitos-lote/novo/");
            FacesUtil.addInfo("Informação!", "Processo(s) de " + tipoProcessoDebito.getDescricao() + " gerado(s) com sucesso.");
        }
    }
}
