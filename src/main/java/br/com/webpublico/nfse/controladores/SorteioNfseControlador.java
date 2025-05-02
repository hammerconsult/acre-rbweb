package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.CampanhaNfse;
import br.com.webpublico.nfse.domain.CupomCampanhaNfse;
import br.com.webpublico.nfse.domain.PremioSorteioNfse;
import br.com.webpublico.nfse.domain.SorteioNfse;
import br.com.webpublico.nfse.domain.dtos.FiltroCupomSorteioNfse;
import br.com.webpublico.nfse.domain.dtos.RelatorioResultadoSorteioNfse;
import br.com.webpublico.nfse.domain.dtos.ResultadoSorteioDTO;
import br.com.webpublico.nfse.facades.SorteioNfseFacade;
import br.com.webpublico.nfse.util.ImprimeRelatorioNfse;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.beust.jcommander.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by William on 19/01/2019.
 */
@ManagedBean(name = "sorteioNfseControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(
        id = "sorteio-nfse-novo",
        pattern = "/nfse/sorteio/novo/",
        viewId = "/faces/tributario/nfse/sorteio/edita.xhtml"),
    @URLMapping(
        id = "sorteio-nfse-listar",
        pattern = "/nfse/sorteio/listar/",
        viewId = "/faces/tributario/nfse/sorteio/lista.xhtml"),
    @URLMapping(
        id = "sorteio-nfse-editar",
        pattern = "/nfse/sorteio/editar/#{sorteioNfseControlador.id}/",
        viewId = "/faces/tributario/nfse/sorteio/edita.xhtml"),
    @URLMapping(
        id = "sorteio-nfse-ver",
        pattern = "/nfse/sorteio/ver/#{sorteioNfseControlador.id}/",
        viewId = "/faces/tributario/nfse/sorteio/visualizar.xhtml"),
})
public class SorteioNfseControlador extends PrettyControlador<SorteioNfse> implements Serializable, CRUD {

    @EJB
    private SorteioNfseFacade facade;

    private PremioSorteioNfse premio;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Future<List<CupomCampanhaNfse>> futuresAssistente;
    private FiltroCupomSorteioNfse filtroCupom;
    private String numeroSorteado;
    private List<ResultadoSorteioDTO> resultadosSorteio;
    private List<CampanhaNfse> campanhas;

    public SorteioNfseControlador() {
        super(SorteioNfse.class);
        filtroCupom = new FiltroCupomSorteioNfse();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/sorteio/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public PremioSorteioNfse getPremio() {
        return premio;
    }

    public void setPremio(PremioSorteioNfse premio) {
        this.premio = premio;
    }

    public List<ResultadoSorteioDTO> getResultadosSorteio() {
        return resultadosSorteio;
    }

    public void setResultadosSorteio(List<ResultadoSorteioDTO> resultadosSorteio) {
        this.resultadosSorteio = resultadosSorteio;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public Future<List<CupomCampanhaNfse>> getFuturesAssistente() {
        return futuresAssistente;
    }

    public void setFuturesAssistente(Future<List<CupomCampanhaNfse>> futuresAssistente) {
        this.futuresAssistente = futuresAssistente;
    }

    public FiltroCupomSorteioNfse getFiltroCupom() {
        return filtroCupom;
    }

    public void setFiltroCupom(FiltroCupomSorteioNfse filtroCupom) {
        this.filtroCupom = filtroCupom;
    }

    public SorteioNfseFacade getFacade() {
        return facade;
    }

    public void setFacade(SorteioNfseFacade facade) {
        this.facade = facade;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public String getNumeroSorteado() {
        return numeroSorteado;
    }

    public void setNumeroSorteado(String numeroSorteado) {
        this.numeroSorteado = numeroSorteado;
    }

    public List<CampanhaNfse> getCampanhas() {
        return campanhas;
    }

    public void setCampanhas(List<CampanhaNfse> campanhas) {
        this.campanhas = campanhas;
    }

    public void buscarCampanhasAbertas() {
        campanhas = facade.getCampanhaNfseFacade().buscarCampanhasAbertas();
    }

    @Override
    @URLAction(mappingId = "sorteio-nfse-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        buscarCampanhasAbertas();
    }

    @Override
    @URLAction(mappingId = "sorteio-nfse-editar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        buscarCampanhasAbertas();
    }

    @Override
    @URLAction(mappingId = "sorteio-nfse-ver", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public void editarPremio(PremioSorteioNfse premio) {
        this.premio = (PremioSorteioNfse) Util.clonarObjeto(premio);
    }

    public void removerPremio(PremioSorteioNfse premio) {
        selecionado.getPremios().remove(premio);
    }

    public void inserirPremio() {
        premio = new PremioSorteioNfse();
        premio.setSequencia(selecionado.proximaSequenciaPremio());
    }

    public void cancelarPremio() {
        premio = null;
    }

    public void adicionarPremio() {
        try {
            facade.validarPremio(selecionado, premio);
            premio.setSorteio(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getPremios(), premio);
            premio = null;
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
        }
    }

    public boolean isPremiado(CupomCampanhaNfse cupom) {
        return cupom.getPremio() != null;
    }


    public void imprimirResultadoSorteio() {
        try {
            List<RelatorioResultadoSorteioNfse> registros = facade.buscarDadosRelatorioResultadoSorteio(selecionado.getId());
            byte[] bytes = new ImprimeRelatorioNfse().
                gerarRelatorio("RelatorioResultadoSorteioNfse.jasper", registros, false);
            AbstractReport abstractReport = new AbstractReport();
            abstractReport.setGeraNoDialog(true);
            abstractReport.escreveNoResponse("premiadosCampanha", bytes);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    @Override
    public void salvar() {
        try {
            if (validaRegrasParaSalvar()) {
                selecionado = facade.salvarRetornando(selecionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
                redireciona(getCaminhoPadrao() + "ver/" + selecionado.getId());
            }
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void iniciarPesquisaCupons() {
        filtroCupom = new FiltroCupomSorteioNfse();
        filtroCupom.setIdCampanha(selecionado.getCampanha().getId());
        filtroCupom.setNumeroSorteio(selecionado.getNumero());
        filtroCupom.setInicioEmissaoNotaFiscal(selecionado.getInicioEmissaoNotaFiscal());
        filtroCupom.setFimEmissaoNotaFiscal(selecionado.getFimEmissaoNotaFiscal());
        FacesUtil.executaJavaScript("openDialogLarge(dialogCupons)");
    }

    public void filtrarCupons() {
        filtroCupom.setCupons(new ArrayList());
        filtroCupom.setIndexInicialDaPesquisa(0);
        filtroCupom.setQuantidadeTotal(0);
        pesquisarCupons();
    }

    public void pesquisarCupons() {
        try {

            filtroCupom = facade.buscarCupons(filtroCupom);
        } catch (Exception e) {
            logger.error("Erro ao buscar cupons do sorteio: {}", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao buscar os cupons do sorteio. " + e.getMessage());
        }

    }

    public void alterarQuantidadeDeRegistrosDaTabela(Long quantidade) {
        filtroCupom.setQuantidadePorPagina(quantidade.intValue());
        pesquisarCupons();
    }

    public boolean isPermitidoIrParaPaginaAnterior() {
        return filtroCupom.getIndexInicialDaPesquisa() > 0;
    }

    public void irParaPaginaAnterior() {
        filtroCupom.setIndexInicialDaPesquisa(filtroCupom.getIndexInicialDaPesquisa() - filtroCupom.getQuantidadePorPagina());
        if (filtroCupom.getIndexInicialDaPesquisa() < 0) {
            filtroCupom.setIndexInicialDaPesquisa(0);
        }
        pesquisarCupons();
    }

    public boolean isPermitidoIrParaPaginaPosterior() {
        return (filtroCupom.getIndexInicialDaPesquisa() + filtroCupom.getQuantidadePorPagina()) < filtroCupom.getQuantidadeTotal();
    }

    public void irParaPaginaPosterior() {
        filtroCupom.setIndexInicialDaPesquisa(filtroCupom.getIndexInicialDaPesquisa() + filtroCupom.getQuantidadePorPagina());
        pesquisarCupons();
    }

    public void limparFiltrosCupons() {
        filtroCupom = new FiltroCupomSorteioNfse();
    }


    @Override
    public void salvar(Redirecionar redirecionar) {
        try {
            selecionado.validarCamposObrigatorios();
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void iniciarSorteio() {
        numeroSorteado = null;
        resultadosSorteio = null;
        FacesUtil.executaJavaScript("openDialog(dialogSorteio)");
    }

    public void buscarResultadoSorteio() {
        if (Strings.isStringEmpty(numeroSorteado)) {
            FacesUtil.addCampoObrigatorio("O Número Sorteado deve ser informado.");
            return;
        }
        try {
            resultadosSorteio = facade.buscarResultadosSorteio(selecionado, numeroSorteado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void confirmarSorteio() {
        if (resultadosSorteio == null || resultadosSorteio.isEmpty()) {
            FacesUtil.addOperacaoNaoRealizada("Nenhum cupom premiado encontrado.");
            return;
        }
        selecionado.setNumeroSorteado(numeroSorteado);
        selecionado = facade.confirmarSorteio(selecionado, resultadosSorteio);
        FacesUtil.addOperacaoRealizada("Sorteio realizado com sucesso!");
        redireciona(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }


}
