package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.ProtocoloImpressao;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltrosPesquisaProtocolo;
import br.com.webpublico.entidadesauxiliares.HierarquiaOrganizacionalDTO;
import br.com.webpublico.entidadesauxiliares.TramitacaoProtocolo;
import br.com.webpublico.entidadesauxiliares.VoTramite;
import br.com.webpublico.entidadesauxiliares.administrativo.FiltroListaProtocolo;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.TramiteFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.component.tabview.TabView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarProtocolosUnidadeExtintas", pattern = "/tramite/unidades-extintas/", viewId = "/faces/tributario/cadastromunicipal/protocolo/unidades-extintas.xhtml"),
    @URLMapping(id = "tramitarProtocolo", pattern = "/tramite/tramitar/#{protocoloControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/protocolo/edita.xhtml"),
    @URLMapping(id = "movimentarProtocolo", pattern = "/tramite/movimentar/#{protocoloControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/protocolo/edita.xhtml"),
    @URLMapping(id = "listarTramite", pattern = "/tramite/listar/", viewId = "/faces/tributario/cadastromunicipal/protocolo/lista.xhtml"),
    @URLMapping(id = "aceitarProtocolo", pattern = "/tramite/aceitar/#{protocoloControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/protocolo/visualizar.xhtml"),
})
public class ProtocoloControlador extends PrettyControlador<Tramite> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ProtocoloControlador.class);
    @EJB
    private TramiteFacade facade;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;
    private TramitacaoProtocolo tramitacaoProtocolo;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private Integer indice;
    private int inicioListaTramitesUnidadesExtintas = 0;
    private int quantidadeTotalDeTramitesRecebidos = 0;
    private int maximoRegistrosTabela = 10;
    private FiltrosPesquisaProtocolo filtrosTramitesParaIncorporar;
    private FiltrosPesquisaProtocolo filtrosTramitesPendentes;
    private FiltrosPesquisaProtocolo filtrosTramitesPendentesSelecionados;
    private FiltrosPesquisaProtocolo filtrosTramitesRecebidos;
    private FiltrosPesquisaProtocolo filtrosTramitesRecebidosSelecionados;
    private FiltrosPesquisaProtocolo filtrosProcessosPendentes;
    private FiltrosPesquisaProtocolo filtrosProcessosAceitos;
    private FiltrosPesquisaProtocolo filtrosTramitesExternosPendentes;
    private FiltrosPesquisaProtocolo filtrosTramitesExternosRecebidos;
    private ArvoreTramite arvorePrincipal;

    public ProtocoloControlador() {
        super(Tramite.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "listarTramite", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void iniciaPesquisa() {
        tramitacaoProtocolo = new TramitacaoProtocolo();
        filtrosProcessosPendentes = new FiltrosPesquisaProtocolo();
        filtrosProcessosAceitos = new FiltrosPesquisaProtocolo();
        filtrosProcessosPendentes.setFiltro(new FiltroListaProtocolo("", facade.getSistemaFacade().getExercicioCorrente().getAno().toString(), "", getUsuarioCorrente()));

        filtrosTramitesPendentes = new FiltrosPesquisaProtocolo();
        filtrosTramitesRecebidos = new FiltrosPesquisaProtocolo();
        filtrosTramitesPendentes.setFiltro(new FiltroListaProtocolo("", facade.getSistemaFacade().getExercicioCorrente().getAno().toString(), "", getUsuarioCorrente()));

        filtrosTramitesExternosPendentes = new FiltrosPesquisaProtocolo();
        filtrosTramitesExternosRecebidos = new FiltrosPesquisaProtocolo();
        filtrosTramitesExternosPendentes.setFiltro(new FiltroListaProtocolo("", facade.getSistemaFacade().getExercicioCorrente().getAno().toString(), "", getUsuarioCorrente()));
        filtrosTramitesPendentesSelecionados = new FiltrosPesquisaProtocolo();

        filtrosTramitesRecebidosSelecionados = new FiltrosPesquisaProtocolo();

        tramitacaoProtocolo.setTramitesSelecionados(Lists.<VoTramite>newArrayList());
        popularArvore();
        tramitacaoProtocolo.setTipoEncaminhamento(TipoProcessoTramite.INTERNO);
        tramitacaoProtocolo.setEncaminhamentoMultiplo(Boolean.FALSE);

        pesquisarTramitesPendentesERecebidos();
    }

    @URLAction(mappingId = "listarProtocolosUnidadeExtintas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listarProtocolosUnidadesExtintas() {
        tramitacaoProtocolo = new TramitacaoProtocolo();
        popularArvore();
        tramitacaoProtocolo.setTramitesUnidadesExtintas(Lists.<VoTramite>newArrayList());
        filtrosTramitesPendentesSelecionados = new FiltrosPesquisaProtocolo();
        tramitacaoProtocolo.setNumeroPesquisaProtocolo("");
        tramitacaoProtocolo.setAnoPesquisaProtocolo("");
        tramitacaoProtocolo.setSolicitantePesquisaProtocolo("");
        tramitacaoProtocolo.setTipoEncaminhamento(TipoProcessoTramite.INTERNO);

    }

    @URLAction(mappingId = "movimentarProtocolo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void movimentar() {
        editar();
        tramitacaoProtocolo.setMovimentar(Boolean.TRUE);
    }

    @URLAction(mappingId = "tramitarProtocolo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        instanciarTramitacao();
        indice = tramitacaoProtocolo.getProcesso().getTramites().size() - 1;
        definirObservacaoParaTramiteAnterior();
        recuperaArvoreHierarquiaOrganizacionalOrc();
        instanciarFiltrosParaIncorporar();
        recuperarSubAssunto();
        Collections.sort(selecionado.getProcesso().getRotas());
    }

    public Date getDataOperacao(){
        return facade.getSistemaFacade().getDataOperacao();
    }

    private void recuperarSubAssunto() {
        if (!tramitacaoProtocolo.getProcesso().getProtocolo()) {
            SubAssunto subAssunto = facade.getProcessoFacade().getSubAssuntoFacade().recuperar(tramitacaoProtocolo.getProcesso().getSubAssunto().getId());
            if (subAssunto != null) {
                tramitacaoProtocolo.setSubAssunto(subAssunto);
            }
        }
    }

    private void definirObservacaoParaTramiteAnterior() {
        if (selecionado.getAceito()) {
            if (Strings.isNullOrEmpty(selecionado.getObservacoes())) {
                Tramite tramiteAnterior = null;
                if (tramitacaoProtocolo.getProcesso().getTramites().size() > 1) {
                    for (Tramite t : tramitacaoProtocolo.getProcesso().getTramites()) {
                        if (t.getIndice() == (selecionado.getIndice() - 1)) {
                            tramiteAnterior = t;
                        }
                    }
                }
                if (tramiteAnterior != null) {
                    selecionado.setObservacoes(tramiteAnterior.getObservacoes());
                } else {
                    selecionado.setObservacoes(tramitacaoProtocolo.getProcesso().getObservacoes());
                }
            }
        }
    }

    private void instanciarTramitacao() {
        tramitacaoProtocolo = new TramitacaoProtocolo();
        tramitacaoProtocolo.setProcesso(facade.getProcessoFacade().recuperar(selecionado.getProcesso().getId()));
        selecionado.setProcesso(tramitacaoProtocolo.getProcesso());
        if (SituacaoProcesso.ARQUIVADO.equals(tramitacaoProtocolo.getProcesso().getSituacao())) {
            tramitacaoProtocolo.getProcesso().setSituacao(SituacaoProcesso.EMTRAMITE);
            tramitacaoProtocolo.getProcesso().setTramiteFinalizador(null);
            selecionado.setStatus(false);
        }
        tramitacaoProtocolo.setMovimentar(Boolean.FALSE);
        tramitacaoProtocolo.setDocumentosProcesso(facade.getProcessoFacade().listaDocumentosProcesso(tramitacaoProtocolo.getProcesso()));
        tramitacaoProtocolo.setEncaminhamentoMultiplo(Boolean.FALSE);

        tramitacaoProtocolo.setNovoTramite(new Tramite());
        definirConteudoEmail();
        tramitacaoProtocolo.setTramites(Lists.<Tramite>newArrayList());
        tramitacaoProtocolo.setHierarquiaOrganizacional(new HierarquiaOrganizacional());
        tramitacaoProtocolo.setUnidadesEncaminhamentosMultiplos(Lists.<UnidadeOrganizacional>newArrayList());
        tramitacaoProtocolo.setTipoEncaminhamento(TipoProcessoTramite.INTERNO);
        tramitacaoProtocolo.setDestinoExterno("");
        tramitacaoProtocolo.setSituacaoAnteriorDoTramite(selecionado.getSituacaoTramite());
        tramitacaoProtocolo.setMotivoIncorporacao("");
        tramitacaoProtocolo.setProcessoRota(new ProcessoRota());
    }

    @URLAction(mappingId = "aceitarProtocolo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void alterarAbas(TabChangeEvent evt) {
        int aba = ((TabView) evt.getComponent()).getActiveIndex();
        switch (aba) {
            case 0: {
                pesquisarTramitesPendentesERecebidos();
                break;
            }
            case 1: {
                pesquisarProcessosPendentesEAceitos();
                break;
            }
            case 2: {
                pesquisarTramitesExternosPendentesERecebidos();
                break;
            }
        }
    }

    public List<ProcessoEnglobado> getTramitesIncorporados() {
        List<ProcessoEnglobado> tramitesIncorporados = Lists.newArrayList();
        for (ProcessoEnglobado processoEnglobado : selecionado.getProcesso().getProcessosEnglobados()) {
            Util.adicionarObjetoEmLista(tramitesIncorporados, processoEnglobado);
        }
        return tramitesIncorporados;
    }

    public void buscarTramitesUnidadeExtintas() {
        FiltroListaProtocolo filtro = new FiltroListaProtocolo(
            tramitacaoProtocolo.getNumeroPesquisaProtocolo(),
            tramitacaoProtocolo.getAnoPesquisaProtocolo(),
            tramitacaoProtocolo.getSolicitantePesquisaProtocolo(),
            getUsuarioCorrente());
        filtro.setStatus(true);
        filtro.setGestor(true);
        filtro.setProtocolo(true);

        List<VoTramite> tramitesUnidadeExtintas = facade.buscarProtocolosEmUnidadesExtintas(
            filtro,
            inicioListaTramitesUnidadesExtintas,
            maximoRegistrosTabela);
        tramitacaoProtocolo.setTramitesUnidadesExtintas(tramitesUnidadeExtintas);
    }

    public void pesquisarTramitesPendentesERecebidos() {
        filtrosTramitesPendentes.limparPaginas();
        buscarTramitesPendentes();
        montarPaginasTramitesPendentes();
        pesquisarTramitesRecebidos();
    }

    public void pesquisarTramitesRecebidos() {
        filtrosTramitesRecebidos.limparPaginas();
        buscarTramitesRecebidos();
        montarPaginasTramitesRecebidos();
    }

    public void pesquisarProcessosPendentesEAceitos() {
        filtrosProcessosPendentes.limparPaginas();
        buscarProcessosPendentes();
        montarPaginasProcessosPendentes();
        pesquisarProcessosAceitos();
    }

    private void montarPaginasProcessosPendentes() {
        atribuirFiltrosProcessosPendentes();
        filtrosProcessosPendentes.montarPaginas(facade.quantidadeTotalDeTramitesPendentes(filtrosProcessosPendentes.getFiltro()));
    }

    public void proximosProcessosPendentes() {
        filtrosProcessosPendentes.proximos();
        buscarProcessosPendentes();
    }

    public void ultimosProcessosPendentes() {
        filtrosProcessosPendentes.irParaUltimo();
        buscarProcessosPendentes();
    }

    public void buscarPrimeirosProcessosPendentes() {
        filtrosProcessosPendentes.irParaPrimeiro();
        buscarProcessosPendentes();
    }

    public void anterioresProcessosPendentes() {
        filtrosProcessosPendentes.anteriores();
        buscarProcessosPendentes();
    }

    public void alterouPaginaProcessosPendentes() {
        try {
            filtrosProcessosPendentes.alterarPagina();
            buscarProcessosPendentes();
        } catch (ValidacaoException ve) {
            filtrosProcessosPendentes.resetarPaginas();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            filtrosProcessosPendentes.resetarPaginas();
        }
    }

    public void pesquisarProcessosAceitos() {
        filtrosProcessosAceitos.limparPaginas();
        buscarProcessosAceitos();
        montarPaginasProcessosAceitos();
    }

    private void montarPaginasProcessosAceitos() {
        FiltroListaProtocolo filtro = getFiltroProcessosAceitos();
        filtrosProcessosAceitos.montarPaginas(facade.quantidadeTotalDeTramitesAceitos(filtro));
    }

    public void proximosProcessosAceitos() {
        filtrosProcessosAceitos.proximos();
        buscarProcessosAceitos();
    }

    public void ultimosProcessosAceitos() {
        filtrosProcessosAceitos.irParaUltimo();
        buscarProcessosAceitos();
    }

    public void buscarPrimeirosProcessosAceitos() {
        filtrosProcessosAceitos.irParaPrimeiro();
        buscarProcessosAceitos();
    }

    public void anterioresProcessosAceitos() {
        filtrosProcessosAceitos.anteriores();
        buscarProcessosAceitos();
    }

    public void alterouPaginaProcessosAceitos() {
        try {
            filtrosProcessosAceitos.alterarPagina();
            buscarProcessosAceitos();
        } catch (ValidacaoException ve) {
            filtrosProcessosAceitos.resetarPaginas();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            filtrosProcessosAceitos.resetarPaginas();
        }
    }

    public void pesquisarTramitesExternosPendentesERecebidos() {
        filtrosTramitesExternosPendentes.limparPaginas();
        buscarTramitesExternosPendentes();
        montarPaginasTramitesExternosPendentes();
        pesquisarTramitesExternosRecebidos();
    }

    public void pesquisarTramitesExternosRecebidos() {
        filtrosTramitesExternosRecebidos.limparPaginas();
        buscarTramitesExternosRecebidos();
        montarPaginasTramitesExternosRecebidos();
    }

    private void montarPaginasTramitesExternosRecebidos() {
        atribuirFiltrosTramitesExternosRecebidos();
        filtrosTramitesExternosRecebidos.montarPaginas(facade.quantidadeTotalTramitesExternosRecebidos(filtrosTramitesExternosPendentes.getFiltro()));
    }

    public void proximosTramitesExternosRecebidos() {
        filtrosTramitesExternosRecebidos.proximos();
        buscarTramitesExternosRecebidos();
    }

    public void ultimosTramitesExternosRecebidos() {
        filtrosTramitesExternosRecebidos.irParaUltimo();
        buscarTramitesExternosRecebidos();
    }

    public void buscarPrimeirosTramitesExternosRecebidos() {
        filtrosTramitesExternosRecebidos.irParaPrimeiro();
        buscarTramitesExternosRecebidos();
    }

    public void anterioresTramitesExternosRecebidos() {
        filtrosTramitesExternosRecebidos.anteriores();
        buscarTramitesExternosRecebidos();
    }

    public void alterouPaginaTramitesExternosRecebidos() {
        try {
            filtrosTramitesExternosRecebidos.alterarPagina();
            buscarTramitesExternosRecebidos();
        } catch (ValidacaoException ve) {
            filtrosTramitesExternosRecebidos.resetarPaginas();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            filtrosTramitesExternosRecebidos.resetarPaginas();
        }
    }

    private void montarPaginasTramitesExternosPendentes() {
        atribuirFiltroTramitesExternosPendentes();
        filtrosTramitesExternosPendentes.montarPaginas(facade.quantidadeTotalTramitesExternosPendentes(filtrosTramitesExternosPendentes.getFiltro()));
    }

    public void proximosTramitesExternosPendentes() {
        filtrosTramitesExternosPendentes.proximos();
        buscarTramitesExternosPendentes();
    }

    public void ultimosTramitesExternosPendentes() {
        filtrosTramitesExternosPendentes.irParaUltimo();
        buscarTramitesExternosPendentes();
    }

    public void buscarPrimeirosTramitesExternosPendentes() {
        filtrosTramitesExternosPendentes.irParaPrimeiro();
        buscarTramitesExternosPendentes();
    }

    public void anterioresTramitesExternosPendentes() {
        filtrosTramitesExternosPendentes.anteriores();
        buscarTramitesExternosPendentes();
    }

    public void alterouPaginaTramitesExternosPendentes() {
        try {
            filtrosTramitesExternosPendentes.alterarPagina();
            buscarTramitesExternosPendentes();
        } catch (ValidacaoException ve) {
            filtrosTramitesExternosPendentes.resetarPaginas();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            filtrosTramitesExternosPendentes.resetarPaginas();
        }
    }

    public void pesquisarTramitesParaIncorporar() {
        filtrosTramitesParaIncorporar.limparPaginas();
        buscarTramitesParaIncorporar();
        montarPaginasTramitesParaIncorporar();
    }

    private void montarPaginasTramitesParaIncorporar() {
        atribuirFiltrosIncorporarTramites();
        filtrosTramitesParaIncorporar.montarPaginas(facade.quantidadeDeTramitesPorUsuarioAndUnidade(filtrosTramitesParaIncorporar.getFiltro()));
    }

    private void atribuirFiltrosIncorporarTramites() {
        Tramite ultimoTramite = facade.recuperar(tramitacaoProtocolo.getProcesso().buscarUltimoTramite().getId());
        filtrosTramitesParaIncorporar.getFiltro().setUsuarioSistema(getUsuarioCorrente());
        filtrosTramitesParaIncorporar.getFiltro().setUnidadeOrganizacional(ultimoTramite.getUnidadeOrganizacional());
        filtrosTramitesParaIncorporar.getFiltro().setTramite(selecionado);
        filtrosTramitesParaIncorporar.getFiltro().setProtocolo(true);
        filtrosTramitesParaIncorporar.getFiltro().setGestor(true);
        filtrosTramitesParaIncorporar.getFiltro().setStatus(true);
        filtrosTramitesParaIncorporar.getFiltro().setAceito(true);
    }

    public void proximosTramitesParaIncorporar() {
        filtrosTramitesParaIncorporar.proximos();
        buscarTramitesParaIncorporar();
    }

    public void ultimosTramitesParaIncorporar() {
        filtrosTramitesParaIncorporar.irParaUltimo();
        buscarTramitesParaIncorporar();
    }

    public void buscarPrimeirosTramitesParaIncorporar() {
        filtrosTramitesParaIncorporar.irParaPrimeiro();
        buscarTramitesParaIncorporar();
    }

    public void anterioresTramitesParaIncorporar() {
        filtrosTramitesParaIncorporar.anteriores();
        buscarTramitesParaIncorporar();
    }

    public void alterouPaginaTramitesParaIncorporar() {
        try {
            filtrosTramitesParaIncorporar.alterarPagina();
            buscarTramitesParaIncorporar();
        } catch (ValidacaoException ve) {
            filtrosTramitesParaIncorporar.resetarPaginas();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            filtrosTramitesParaIncorporar.resetarPaginas();
        }
    }

    public void salvaTramite() {
        try {
            validarSalvarTramite();
            definirStatusAoSalvarTramite();
            tramitacaoProtocolo = facade.salvaTramite(selecionado, this.tramitacaoProtocolo);
            selecionado = tramitacaoProtocolo.getSelecionado();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
            enviarEmailAoSalvarTramite();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
            logger.error(e.getMessage());
        }
    }

    private void definirStatusAoSalvarTramite() {
        if (!selecionado.getStatus()) {
            selecionado.setDataConclusao(new Date());
            tramitacaoProtocolo.setNovoTramite(new Tramite());
            HierarquiaOrganizacional hierarquia;
            ProcessoRota itemRota;
            try {
                itemRota = tramitacaoProtocolo.getProcesso().getRotas().get(selecionado.getIndice() + 1);
                hierarquia = atribuirHierarquiaAdministrativa(itemRota);
                criarProcessoRota(hierarquia, itemRota);
                for (Tramite t : tramitacaoProtocolo.getProcesso().getTramites()) {
                    if (t.getIndice() == (selecionado.getIndice() + 1)) {
                        t.setStatus(true);
                    }
                }
            } catch (IndexOutOfBoundsException e) {
            }
        }
    }

    private void enviarEmailAoSalvarTramite() throws Exception {
        if (selecionado.getSituacaoTramite() != null && selecionado.getSituacaoTramite().getEnviaEmail()) {
            ConfiguracaoEmail configuracaoEmail = configuracaoEmailFacade.recuperarUtilmo();
            validarEnvioEmail(configuracaoEmail, tramitacaoProtocolo);
            String titulo = tramitacaoProtocolo.getProcesso().getProtocolo() ? "PROTOCOLO" : "PROCESSO";
            EmailService.getInstance().enviarEmail(tramitacaoProtocolo.getProcesso().getPessoa().getEmail().trim(),
                titulo + " - " + tramitacaoProtocolo.getProcesso().getNumero() + "/" + tramitacaoProtocolo.getProcesso().getAno(),
                tramitacaoProtocolo.getConteudoEmail());
        }
    }

    private void validarEnvioEmail(ConfiguracaoEmail configuracaoEmail, TramitacaoProtocolo tramitacaoProtocolo) {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoEmail == null) {
            ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "Configuração para envio de e-mail não encontrada.");
        }
        if (Strings.isNullOrEmpty(tramitacaoProtocolo.getProcesso().getPessoa().getEmail())) {
            ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "E-mail não enviado. A pessoa do processo não possui e-mail em seu cadastro.");
        }
        ve.lancarException();
    }

    private void validarSalvarTramite() {
        ValidacaoException ve = new ValidacaoException();
        if (!tramitacaoProtocolo.getProcesso().getProtocolo() && Strings.isNullOrEmpty(selecionado.getResponsavelParecer())) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo responsável pelo parecer deve ser informado.");
        }
        if (tramitacaoProtocolo.getProcesso().getProtocolo() && Strings.isNullOrEmpty(selecionado.getResponsavel())) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo responsável pelo parecer deve ser informado.");
        }
        if (selecionado.getSituacaoTramite() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo situação do parecer deve ser informado.");
        }
        ve.lancarException();
    }

    public void iniciarEncaminhamentoProtocolo() {
        try {
            validarEncaminhamentoProtocolo();
            RequestContext.getCurrentInstance().execute("confirmEncaminhar.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarEncaminhamentoProtocolo() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(tramitacaoProtocolo.getMotivoTramiteSelecionado())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo deve ser informado.");
        }
        validarEncaminhamentoInterno(ve);
        validarEncaminhamentoExterno(ve);
        ve.lancarException();
    }

    private void validarEncaminhamentoInterno(ValidacaoException ve) {
        if (getInterno()) {
            if (!tramitacaoProtocolo.getEncaminhamentoMultiplo()) {
                if (tramitacaoProtocolo.getHierarquiaOrganizacional() == null && tramitacaoProtocolo.getTramites().isEmpty() && tramitacaoProtocolo.getUnidadesEncaminhamentosMultiplos().isEmpty()) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade de Destino deve ser informado.");
                }
                ve.lancarException();
                if (facade.getUsuarioSistemaFacade().buscarUsuariosDaUnidadeOrganizacionalProtocolo(tramitacaoProtocolo.getHierarquiaOrganizacional().getSubordinada()).isEmpty()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível encaminhar um protocolo para uma unidade organizacional que não possua usuários cadastrados!");
                }
            } else {
                if (tramitacaoProtocolo.getUnidadesEncaminhamentosMultiplos().isEmpty()) {
                    ve.adicionarMensagemDeCampoObrigatorio("Favor Adicionar uma unidade de destino!");
                }
                ve.lancarException();
                for (UnidadeOrganizacional uni : tramitacaoProtocolo.getUnidadesEncaminhamentosMultiplos()) {
                    if (facade.getUsuarioSistemaFacade().buscarUsuariosDaUnidadeOrganizacionalProtocolo(uni).isEmpty()) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível encaminhar um protocolo para uma unidade organizacional (" + uni.getDescricao() + ") que não possua usuários cadastrados!");
                    }
                }
            }
        }
    }

    private void validarEncaminhamentoExterno(ValidacaoException ve) {
        if (getExterno() && Strings.isNullOrEmpty(tramitacaoProtocolo.getDestinoExterno())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Destino Externo deve ser informado.");
        }
    }

    public void arquivar() {
        try {
            validarMotivo();
            FacesUtil.executaJavaScript("confirmArquivar.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void encerrar() {
        try {
            validarMotivo();
            FacesUtil.executaJavaScript("confirmEncerrar.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void incorporar() {
        try {
            tramitacaoProtocolo.setMotivoIncorporacao("");
            FacesUtil.executaJavaScript("consultarTramites.show()");
            FacesUtil.atualizarComponente("formConsultar");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void incorporarTramites() {
        try {
            validarMotivoParaIncorporar();
            Tramite selecionadoSalvo = facade.incorporarTramites(selecionado, tramitacaoProtocolo, filtrosTramitesParaIncorporar);
            if (selecionadoSalvo != null) {
                selecionado = selecionadoSalvo;
            }
            FacesUtil.executaJavaScript("consultarTramites.hide()");
            FacesUtil.redirecionamentoInterno(getUrlAtual());
            FacesUtil.addOperacaoRealizada("Trâmite incorporado com sucesso.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void limparFiltrosTramite() {
        filtrosTramitesParaIncorporar.limparPaginas();
        FacesUtil.executaJavaScript("consultarTramites.hide()");
    }

    private void validarMotivo() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(tramitacaoProtocolo.getMotivoTramiteSelecionado())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo deve ser informado");
        }
        ve.lancarException();
    }

    private void validarMotivoParaIncorporar() {
        ValidacaoException ve = new ValidacaoException();
        if (tramitacaoProtocolo.getMotivoIncorporacao() == null || "".equals(tramitacaoProtocolo.getMotivoIncorporacao())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo da incorporação deve ser informado!");
        }
        if (filtrosTramitesParaIncorporar.getTramitesAuxiliares().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum trâmite selecionado para ser incorporado.");
        }
        ve.lancarException();
    }

    public void visualizarProcesso(ProcessoEnglobado processoEnglobado) {
        FacesUtil.redirecionamentoInterno("/protocolo/verconsulta/" + processoEnglobado.getEnglobado().getId());
    }

    private UsuarioSistema getUsuarioCorrente() {
        return facade.getSistemaFacade().getUsuarioCorrente();
    }

    public void encaminharTramiteProtocolo() {
        try {
            facade.encaminharProcesso(selecionado, tramitacaoProtocolo, indice);
            FacesUtil.executaJavaScript("confirmEncaminhar.hide()");
            if (tramitacaoProtocolo.getEncaminhamentoMultiplo()) {
                FacesUtil.executaJavaScript("imprimeGuiaMultiplo.show()");
            } else {
                FacesUtil.executaJavaScript("imprimeGuia.show()");
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            redireciona();
            descobrirETratarException(e);
        }
    }

    private void validarEncaminhamentosMultiplos() {
        ValidacaoException ve = new ValidacaoException();
        if (tramitacaoProtocolo.getHierarquiaOrganizacional() == null && getInterno()) {
            ve.adicionarMensagemDeCampoObrigatorio("o campo unidade de destino deve ser informado.");
        }
        if ((tramitacaoProtocolo.getDestinoExterno() == null || tramitacaoProtocolo.getDestinoExterno().isEmpty()) && getExterno()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo destino externo deve ser informado.");
        }
        if (Strings.isNullOrEmpty(tramitacaoProtocolo.getMotivoTramiteSelecionado())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo motivo deve ser informado.");
        }
        ve.lancarException();
    }

    public void encaminharTramitesSelecionadosUnidadesExtintas() {
        try {
            validarEncaminhamentosMultiplos();
            facade.encaminharTramitesSelecionadosUnidadesExtintas(tramitacaoProtocolo);
            FacesUtil.executaJavaScript("confirmEncaminhar.hide()");
            FacesUtil.executaJavaScript("imprimeGuia.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void encaminharTramitesSelecionados() {
        try {
            validarEncaminhamentosMultiplos();
            facade.encaminharTramitesSelecionados(filtrosTramitesRecebidosSelecionados, tramitacaoProtocolo);
            FacesUtil.executaJavaScript("confirmEncaminhar.hide()");
            FacesUtil.executaJavaScript("imprimeGuia.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void imprimirGuiaEncaminhamento(boolean multiplo) {
        try {
            Tramite tra = selecionado.getProcesso().getTramites().get(selecionado.getProcesso().getTramites().size() - 1);
            ProtocoloImpressao guia = new ProtocoloImpressao();
            guia.gerarGuiaRecebimentoTramite(tra, multiplo);
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void imprimirGuiaEncaminhamentoDosTramitesUnidadeExtintas() {
        try {
            List<Tramite> tramiteUltimo = new ArrayList<>();
            for (VoTramite tramite : tramitacaoProtocolo.getTramitesSelecionados()) {
                Processo processoRecuperado = facade.getProcessoFacade().recuperar(tramite.getProcesso().getId());
                Tramite tra = processoRecuperado.getTramites().get(processoRecuperado.getTramites().size() - 1);
                tramiteUltimo.add(tra);
            }
            ProtocoloImpressao guia = new ProtocoloImpressao();
            guia.imprimirVariosTramitesEmUnicoArquivo(tramiteUltimo);
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void imprimirGuiaEncaminhamentoDosTramites() {
        try {
            List<Tramite> tramiteUltimo = new ArrayList<>();
            for (VoTramite tramiteSelecionado : filtrosTramitesRecebidosSelecionados.getTramites()) {
                Processo processoRecuperado = facade.getProcessoFacade().recuperar(tramiteSelecionado.getProcesso().getId());
                Tramite tra = processoRecuperado.getTramites().get(processoRecuperado.getTramites().size() - 1);
                tramiteUltimo.add(tra);
            }
            ProtocoloImpressao guia = new ProtocoloImpressao();
            guia.imprimirVariosTramitesEmUnicoArquivo(tramiteUltimo);
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void imprimirGuiaMultiplaEncaminhamentoDosTramites() {
        try {
            List<Tramite> tramiteUltimo = new ArrayList<>();
            for (VoTramite tramiteSelecionado : filtrosTramitesRecebidosSelecionados.getTramites()) {
                Processo processoRecuperado = facade.getProcessoFacade().recuperar(tramiteSelecionado.getProcesso().getId());
                Tramite tra = processoRecuperado.getTramites().get(processoRecuperado.getTramites().size() - 1);
                tramiteUltimo.add(tra);
            }
            ProtocoloImpressao guia = new ProtocoloImpressao();
            guia.gerarGuiaRecebimentoMultiplo(tramiteUltimo, tramitacaoProtocolo.getHierarquiaOrganizacional() != null ? tramitacaoProtocolo.getHierarquiaOrganizacional().getSubordinada().toString() : tramitacaoProtocolo.getDestinoExterno());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void adicionarUnidadeOrganizacional() {
        try {
            validarUnidade();
            Util.adicionarObjetoEmLista(tramitacaoProtocolo.getUnidadesEncaminhamentosMultiplos(), tramitacaoProtocolo.getHierarquiaOrganizacional().getSubordinada());
            tramitacaoProtocolo.setHierarquiaOrganizacional(new HierarquiaOrganizacional());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getAllMensagens());
        }
    }

    private void validarUnidade() {
        ValidacaoException ve = new ValidacaoException();
        if (tramitacaoProtocolo.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade de Destino deve ser informado.");
        }
        ve.lancarException();
        for (UnidadeOrganizacional unidadeOrganizacional : tramitacaoProtocolo.getUnidadesEncaminhamentosMultiplos()) {
            if (unidadeOrganizacional.equals(tramitacaoProtocolo.getHierarquiaOrganizacional().getSubordinada())) {
                ve.adicionarMensagemDeCampoObrigatorio("A Unidade de Destino selecionada já está adicionada.");
            }
        }
        ve.lancarException();
    }

    public void removeUnidadeOrganizacional(UnidadeOrganizacional uni) {
        if (uni != null) {
            tramitacaoProtocolo.getUnidadesEncaminhamentosMultiplos().remove(uni);
        }
    }

    public void encerrarProtocolo() {
        try {
            FacesUtil.executaJavaScript("confirmEncerrar.hide()");
            validarTramiteMaisNovo(tramitacaoProtocolo.getProcesso(), selecionado.getIndice());
            selecionado.setMotivo(tramitacaoProtocolo.getMotivoTramiteSelecionado());
            selecionado.setObservacoes(tramitacaoProtocolo.getObservacaoTramiteSelecionado());
            facade.encerrarProtocolo(selecionado, tramitacaoProtocolo);
            FacesUtil.addOperacaoRealizada("O Protocolo foi encerrado com sucesso!");
            if (tramitacaoProtocolo.isMovimentar()) {
                FacesUtil.redirecionamentoInterno("/protocolo/listar/");
            } else {
                FacesUtil.redirecionamentoInterno("/tramite/listar/");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.addOperacaoNaoPermitida(ve.getMessage());
        }
    }

    private void validarTramiteMaisNovo(Processo processo, Integer indice) {
        ValidacaoException ve = new ValidacaoException();
        if (facade.getProcessoFacade().hasTramiteMaisNovo(processo, indice)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Existe um trâmite mais recente para o " + (processo.getProtocolo() ? "Protocolo" : "Processo")
                + " " + processo.getNumero() + "/" + processo.getAno() + "!");
        }
        ve.lancarException();
    }

    public void atribuirNullMotivoObservacaoTramitesRecebidosSelecionados() {
        for (VoTramite voTramite : filtrosTramitesRecebidosSelecionados.getTramites()) {
            voTramite.setMotivo(null);
            voTramite.setObservacoes(null);
        }
        tramitacaoProtocolo.setMotivoTramiteSelecionado(null);
        tramitacaoProtocolo.setObservacaoTramiteSelecionado(null);
    }

    public void arquivarProtocolosSelecionados() {
        try {
            validaCampoEncerramentoArquivamentoSelecionados();
            facade.arquivarProtocolosSelecionados(filtrosTramitesRecebidosSelecionados, tramitacaoProtocolo);
            FacesUtil.executaJavaScript("arquivarSelecionados.hide()");
            FacesUtil.redirecionamentoInterno(getUrlAtual());
            FacesUtil.addOperacaoRealizada("O(s) Trâmite(s) foi(ram) arquivado(s) com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
        FacesUtil.executaJavaScript("aguarde.hide()");
    }

    public void arquivarProtocolo() {
        try {
            validarTramiteMaisNovo(tramitacaoProtocolo.getProcesso(), selecionado.getIndice());
            FacesUtil.executaJavaScript("confirmArquivar.hide()");
            selecionado.setObservacoes(tramitacaoProtocolo.getObservacaoTramiteSelecionado());
            selecionado.setMotivo(tramitacaoProtocolo.getMotivoTramiteSelecionado());
            facade.arquivarProtocolo(selecionado);
            FacesUtil.addOperacaoRealizada("O Protocolo foi arquivado com sucesso!");
            if (tramitacaoProtocolo.isMovimentar()) {
                FacesUtil.redirecionamentoInterno("/protocolo/listar/");
            } else {
                FacesUtil.redirecionamentoInterno("/tramite/listar/");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public List<HierarquiaOrganizacionalDTO> buscarUnidadesDoUsuarioLogadoGestorProtocolo() {
        return facade.getHierarquiaOrganizacionalFacade().buscarHierarquiaOndeUsuarioEhGestorProtocolo(getUsuarioCorrente(), facade.getSistemaFacade().getDataOperacao());
    }

    public void removeFileUpload(ActionEvent event) {
        ProcessoArquivo arq = (ProcessoArquivo) event.getComponent().getAttributes().get("remove");
        tramitacaoProtocolo.getProcesso().getArquivos().remove(arq);
    }

    private HierarquiaOrganizacional atribuirHierarquiaAdministrativa(ProcessoRota itemRota) {
        HierarquiaOrganizacional hierarquia;
        hierarquia = facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), itemRota.getUnidadeOrganizacional(), selecionado.getProcesso().getDataRegistro());
        if (hierarquia != null) {
            tramitacaoProtocolo.setHierarquiaOrganizacional(hierarquia);
        }
        return hierarquia;
    }

    private void criarProcessoRota(HierarquiaOrganizacional hierarquia, ProcessoRota itemRota) {
        Tramite novoTramite = tramitacaoProtocolo.getNovoTramite();
        novoTramite.setUnidadeOrganizacional(hierarquia.getSubordinada());
        novoTramite.setDataRegistro(new Date());
        novoTramite.setProcesso(tramitacaoProtocolo.getProcesso());
        novoTramite.setIndice(tramitacaoProtocolo.getProcesso().getTramites().size());
        novoTramite.setUsuarioTramite(getUsuarioCorrente());
        novoTramite.setPrazo(itemRota.getPrazo());
        novoTramite.setTipoPrazo(itemRota.getTipoPrazo());
        novoTramite.setOrigem(selecionado.getUnidadeOrganizacional());
        FacesUtil.executaJavaScript("dlgEncaminharProcesso.show()");
        FacesUtil.atualizarComponente("formEncaminharProcesso");
    }

    public void novoTramiteProcesso() {
        try {
            ProcessoRota rota = tramitacaoProtocolo.getProcessoRota();
            Util.validarCampos(rota);
            rota.setIndice(tramitacaoProtocolo.getProcesso().getRotas().size());
            rota.setDataRegistro(new Date());
            rota.setProcesso(tramitacaoProtocolo.getProcesso());
            rota.setTramitado(false);
            Util.adicionarObjetoEmLista(tramitacaoProtocolo.getProcesso().getRotas(), rota);
            tramitacaoProtocolo.setProcessoRota(new ProcessoRota());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<SelectItem> getSituacaoProcesso() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (SituacaoProcesso object : SituacaoProcesso.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getProcessos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (Processo object : facade.getProcessoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getNumero().toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoProcessoTramites() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoProcessoTramite object : TipoProcessoTramite.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacaoTramite() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (SituacaoTramite object : facade.getSituacaoTramiteFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    private void organizarOrigens() {
        UnidadeOrganizacional origem = null;
        for (Tramite t : tramitacaoProtocolo.getProcesso().getTramites()) {
            if (origem == null) {
                t.setOrigem(tramitacaoProtocolo.getProcesso().getUoCadastro());
            } else {
                t.setOrigem(origem);
            }
            origem = t.getUnidadeOrganizacional();
        }
    }

    public void subirRota(ProcessoRota processoRota) {
        int indiceAtual = tramitacaoProtocolo.getProcesso().getRotas().indexOf(processoRota);
        tramitacaoProtocolo.getProcesso().getRotas().remove(processoRota);
        int novoIndice = indiceAtual - 1;
        tramitacaoProtocolo.getProcesso().getRotas().add(novoIndice, processoRota);

        ordenarIndiceRota();
    }

    private void ordenarIndiceRota() {
        Iterator<ProcessoRota> itens = tramitacaoProtocolo.getProcesso().getRotas().iterator();
        while (itens.hasNext()) {
            ProcessoRota proximo = itens.next();
            int i = tramitacaoProtocolo.getProcesso().getRotas().indexOf(proximo) - 1;
            proximo.setIndice(i + 1);
            Util.adicionarObjetoEmLista(tramitacaoProtocolo.getProcesso().getRotas(), proximo);
        }
    }

    public void descerRota(ProcessoRota processoRota) {
        int indiceAtual = tramitacaoProtocolo.getProcesso().getRotas().indexOf(processoRota);
        tramitacaoProtocolo.getProcesso().getRotas().remove(processoRota);
        int novoIndice = indiceAtual + 1;
        tramitacaoProtocolo.getProcesso().getRotas().add(novoIndice, processoRota);
        ordenarIndiceRota();
    }

    public boolean disabledBotaoAlteraParaBaixo(ProcessoRota rota) {
        if (rota.getTramitado()) {
            return true;
        }
        List<ProcessoRota> rotasNaoTramitadas = tramitacaoProtocolo.getProcesso().getRotas();
        if (rotasNaoTramitadas.size() == 1) {
            return true;
        }
        int indice = rotasNaoTramitadas.indexOf(rota);
        if (indice == -1) {
            return true;
        }
        if (indice != (rotasNaoTramitadas.size() - 1)) {
            return false;
        }
        return true;
    }

    private List<ProcessoRota> getRotasNaoTramitadas() {
        if (tramitacaoProtocolo.getProcesso().getRotas().size() == 1) {
            return tramitacaoProtocolo.getProcesso().getRotas();
        }
        List<ProcessoRota> rotasNaoTramitadas = Lists.newArrayList();
        for (ProcessoRota rota : tramitacaoProtocolo.getProcesso().getRotas()) {
            if (!rota.getTramitado()) {
                rotasNaoTramitadas.add(rota);
            }
        }
        return rotasNaoTramitadas;
    }

    public boolean disabledBotaoAlteraParaCima(ProcessoRota rota) {
        try {
            if (rota.getTramitado()) {
                return true;
            }
            List<ProcessoRota> rotasNaoTramitadas = tramitacaoProtocolo.getProcesso().getRotas();
            if (rotasNaoTramitadas.size() == 1) {
                return true;
            }
            int indice = rotasNaoTramitadas.indexOf(rota);
            ProcessoRota processoAtual = tramitacaoProtocolo.getProcesso().getRotas().get(indice - 1);
            if (processoAtual.getTramitado()) {
                return true;
            }
            if (indice != 0) {
                return false;
            }
        } catch (ArrayIndexOutOfBoundsException a) {
            return true;
        }
        return true;
    }

    public void removerRota(ProcessoRota processoRota) {
        tramitacaoProtocolo.getProcesso().getRotas().remove(processoRota);
        for (ProcessoRota rota : tramitacaoProtocolo.getProcesso().getRotas()) {
            rota.setIndice(tramitacaoProtocolo.getProcesso().getRotas().indexOf(rota));
        }
    }


    private void validarCamposAceiteTramite() {
        ValidacaoException ve = new ValidacaoException();
        if ("".equals(selecionado.getResponsavel()) || selecionado.getResponsavel() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Responsável é obrigatório!");
        }
        if (tramitacaoProtocolo.getDataAceite() == null || tramitacaoProtocolo.getHoraAceite() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Os campos Data e Hora do aceite são obrigatórios!");
        } else {
            Date dataAceiteComHora = DataUtil.juntarDataEHora(tramitacaoProtocolo.getDataAceite(), tramitacaoProtocolo.getHoraAceite());
            if (dataAceiteComHora.compareTo(new Date()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de aceite do protocolo " + selecionado.getProcesso().getNumero() + "/" + selecionado.getProcesso().getAno() + " deve ser anterior à data do servidor: " + Util.formatterDataHora.format(new Date()));
            } else if (selecionado.getDataRegistro().compareTo(dataAceiteComHora) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(buscarMensagemDataDeAceite(selecionado));
            } else {
                facade.validarDataDeAceite(selecionado, dataAceiteComHora, ve, false, buscarMensagemDataDeAceite(selecionado));
            }
        }
        ve.lancarException();
    }

    public void imprimirGuia() {
        try {
            ProtocoloImpressao guia = new ProtocoloImpressao();
            guia.gerarGuiaRecebimentoTramite(selecionado, false);
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalAdministrativaComUsuarios(String parte) {
        return facade.getUsuarioSistemaFacade().filtrandoHierarquiaOrganizacionalAdministrativaComUsuarios(parte.trim(), facade.getSistemaFacade().getDataOperacao());
    }

    public List<UsuarioSistema> buscarUsuariosDaUnidadeOrganizacionalProtocolo() {
        if (tramitacaoProtocolo != null && tramitacaoProtocolo.getHierarquiaOrganizacional() != null && tramitacaoProtocolo.getHierarquiaOrganizacional().getSubordinada() != null) {
            return facade.getUsuarioSistemaFacade().buscarUsuariosDaUnidadeOrganizacionalProtocolo(tramitacaoProtocolo.getHierarquiaOrganizacional().getSubordinada());
        }
        return Lists.newArrayList();
    }

    public void limparListaUnidadesDestino() {
        if (tramitacaoProtocolo.getUnidadesEncaminhamentosMultiplos() != null && !tramitacaoProtocolo.getUnidadesEncaminhamentosMultiplos().isEmpty()) {
            tramitacaoProtocolo.getUnidadesEncaminhamentosMultiplos().clear();
        }
    }

    public void setaUnidade() {
        if (tramitacaoProtocolo.getHierarquiaOrganizacional().getSubordinada() != null) {
            selecionado.setUnidadeOrganizacional(tramitacaoProtocolo.getHierarquiaOrganizacional().getSubordinada());
        } else {
            FacesUtil.addError("Atenção", "Selecione para filtrar!");
        }
    }

    public void definirConteudoEmail() {
        if (selecionado.getSituacaoTramite() != null) {
            tramitacaoProtocolo.setConteudoEmail(selecionado.getSituacaoTramite().getConteudoEmail());
        } else {
            tramitacaoProtocolo.setConteudoEmail(null);
        }
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, facade.getHierarquiaOrganizacionalFacade());
        }
        return converterHierarquiaOrganizacional;
    }

    public void imprimeTramiteProcesso() {
        try {
            facade.getProcessoFacade().geraDocumento(TipoDocumentoProtocolo.TRAMITE_PROCESSO, tramitacaoProtocolo.getProcesso(), selecionado, tramitacaoProtocolo.getProcesso().getPessoa());
            selecionado.setProtocoloImpresso(true);
            FacesUtil.atualizarComponente("salvaTramite");
            FacesUtil.atualizarComponente("salvaTramiteSemImprimir");
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    private void buscarTramitesPendentes() {
        atribuirFiltrosTramitesPendentes();
        List<VoTramite> voTramites = facade.buscarTramitesPendentesUnidadeProcessoProtocolo(
            filtrosTramitesPendentes.getFiltro(),
            filtrosTramitesPendentes.getInicioConsulta(),
            filtrosTramitesPendentes.getQuantidadeRegistros().getQuantidade());
        filtrosTramitesPendentes.setTramites(voTramites);
    }

    private void atribuirFiltrosTramitesPendentes() {
        filtrosTramitesPendentes.getFiltro().setAceito(false);
        filtrosTramitesPendentes.getFiltro().setStatus(true);
        filtrosTramitesPendentes.getFiltro().setGestor(true);
        filtrosTramitesPendentes.getFiltro().setProtocolo(true);
        filtrosTramitesPendentes.getFiltro().setDataOperacao(getDataOperacao());
    }

    private void montarPaginasTramitesPendentes() {
        atribuirFiltrosTramitesPendentes();
        filtrosTramitesPendentes.montarPaginas(facade.quantidadeTotalDeTramitesPendentes(filtrosTramitesPendentes.getFiltro()));
    }

    public void proximosTramitePendente() {
        filtrosTramitesPendentes.proximos();
        buscarTramitesPendentes();
    }

    public void ultimosTramitePendente() {
        filtrosTramitesPendentes.irParaUltimo();
        buscarTramitesPendentes();
    }

    public void buscarPrimeirosTramitePendente() {
        filtrosTramitesPendentes.irParaPrimeiro();
        buscarTramitesPendentes();
    }

    public void anterioresTramitePendente() {
        filtrosTramitesPendentes.anteriores();
        buscarTramitesPendentes();
    }

    public void alterouPaginaTramitesPendentes() {
        try {
            filtrosTramitesPendentes.alterarPagina();
            buscarTramitesPendentes();
        } catch (ValidacaoException ve) {
            filtrosTramitesPendentes.resetarPaginas();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            filtrosTramitesPendentes.resetarPaginas();
        }
    }


    public void buscarTramitesRecebidos() {
        atribuirFiltrosTramitesRecebidos();
        List<VoTramite> tramites = facade.buscarProtocoloAceitoRecebidoPorUsuario(
            filtrosTramitesRecebidos.getFiltro(),
            filtrosTramitesRecebidos.getInicioConsulta(),
            filtrosTramitesRecebidos.getQuantidadeRegistros().getQuantidade());
        filtrosTramitesRecebidos.setTramites(tramites);
    }

    private void atribuirFiltrosTramitesRecebidos() {
        filtrosTramitesRecebidos.setFiltro(new FiltroListaProtocolo(
            filtrosTramitesPendentes.getFiltro().getNumero(),
            filtrosTramitesPendentes.getFiltro().getAno(),
            filtrosTramitesPendentes.getFiltro().getSolicitante(),
            getUsuarioCorrente()));


        filtrosTramitesRecebidos.getFiltro().setDataOperacao(getDataOperacao());
        filtrosTramitesRecebidos.getFiltro().setAceito(true);
        filtrosTramitesRecebidos.getFiltro().setStatus(true);
        filtrosTramitesRecebidos.getFiltro().setGestor(true);
        filtrosTramitesRecebidos.getFiltro().setProtocolo(true);
    }


    private void montarPaginasTramitesRecebidos() {
        atribuirFiltrosTramitesRecebidos();
        Integer quantidadePaginas = facade.quantidadeTotalDeTramitesAceitos(filtrosTramitesRecebidos.getFiltro());
        filtrosTramitesRecebidos.montarPaginas(quantidadePaginas);
    }

    public void proximosTramitesPendentesSelecionados() {
        filtrosTramitesPendentesSelecionados.proximos();
        buscarTramitesPendentesSelecionados();
    }

    public void ultimosTramitesPendentesSelecionados() {
        filtrosTramitesPendentesSelecionados.irParaUltimo();
        buscarTramitesPendentesSelecionados();
    }

    public void buscarPrimeirosTramitesPendentesSelecionados() {
        filtrosTramitesPendentesSelecionados.irParaPrimeiro();
        buscarTramitesPendentesSelecionados();
    }

    public void anterioresTramitesPendentesSelecionados() {
        filtrosTramitesPendentesSelecionados.anteriores();
        buscarTramitesPendentesSelecionados();
    }

    public void alterouPaginaTramitesPendentesSelecionados() {
        try {
            filtrosTramitesPendentesSelecionados.alterarPagina();
            buscarTramitesPendentesSelecionados();
        } catch (ValidacaoException ve) {
            filtrosTramitesPendentesSelecionados.resetarPaginas();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            filtrosTramitesPendentesSelecionados.resetarPaginas();
        }
    }

    public void pesquisarTramitesPendentesSelecionados() {
        filtrosTramitesPendentesSelecionados.limparPaginas();
        filtrosTramitesPendentesSelecionados.setQuantidadeTotal(filtrosTramitesPendentesSelecionados.getTramites().size());
        buscarTramitesPendentesSelecionados();
        montarPaginasTramitesPendentesSelecionados();
    }

    private void buscarTramitesPendentesSelecionados() {
        try {
            List<VoTramite> tramitesSelecionados = filtrosTramitesPendentesSelecionados.getTramites().subList(
                filtrosTramitesPendentesSelecionados.getInicioConsulta(),
                filtrosTramitesPendentesSelecionados.getInicioConsulta() + filtrosTramitesPendentesSelecionados.getQuantidadeRegistros().getQuantidade());

            filtrosTramitesPendentesSelecionados.getTramitesAuxiliares().clear();
            filtrosTramitesPendentesSelecionados.getTramitesAuxiliares().addAll(tramitesSelecionados);
        } catch (IndexOutOfBoundsException iobe) {
            List<VoTramite> tramitesSelecionados = filtrosTramitesPendentesSelecionados.getTramites().subList(filtrosTramitesPendentesSelecionados.getInicioConsulta(),
                filtrosTramitesPendentesSelecionados.getTramites().size());
            filtrosTramitesPendentesSelecionados.getTramitesAuxiliares().clear();
            filtrosTramitesPendentesSelecionados.getTramitesAuxiliares().addAll(tramitesSelecionados);
        }
    }

    private void montarPaginasTramitesPendentesSelecionados() {
        filtrosTramitesPendentesSelecionados.montarPaginas(filtrosTramitesPendentesSelecionados.getQuantidadeTotal());
    }

    public void proximosTramitesRecebidosSelecionados() {
        filtrosTramitesRecebidosSelecionados.proximos();
        buscarTramitesRecebidosSelecionados();
    }

    public void ultimosTramitesRecebidosSelecionados() {
        filtrosTramitesRecebidosSelecionados.irParaUltimo();
        buscarTramitesRecebidosSelecionados();
    }

    public void buscarPrimeirosTramitesRecebidosSelecionados() {
        filtrosTramitesRecebidosSelecionados.irParaPrimeiro();
        buscarTramitesRecebidosSelecionados();
    }

    public void anterioresTramitesRecebidosSelecionados() {
        filtrosTramitesRecebidosSelecionados.anteriores();
        buscarTramitesRecebidosSelecionados();
    }

    public void alterouPaginaTramitesRecebidosSelecionados() {
        try {
            filtrosTramitesRecebidosSelecionados.alterarPagina();
            buscarTramitesRecebidosSelecionados();
        } catch (ValidacaoException ve) {
            filtrosTramitesRecebidosSelecionados.resetarPaginas();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            filtrosTramitesRecebidosSelecionados.resetarPaginas();
        }
    }

    public void pesquisarTramitesRecebidosSelecionados() {
        filtrosTramitesRecebidosSelecionados.limparPaginas();
        filtrosTramitesRecebidosSelecionados.setQuantidadeTotal(filtrosTramitesRecebidosSelecionados.getTramites().size());
        buscarTramitesRecebidosSelecionados();
        montarPaginasTramitesRecebidosSelecionados();
    }

    private void buscarTramitesRecebidosSelecionados() {
        try {
            filtrosTramitesRecebidosSelecionados.setTramitesAuxiliares(
                filtrosTramitesRecebidosSelecionados.getTramites().subList(filtrosTramitesRecebidosSelecionados.getInicioConsulta(),
                    filtrosTramitesRecebidosSelecionados.getInicioConsulta() + filtrosTramitesRecebidosSelecionados.getQuantidadeRegistros().getQuantidade()));
        } catch (IndexOutOfBoundsException iobe) {
            filtrosTramitesRecebidosSelecionados.setTramitesAuxiliares(filtrosTramitesRecebidosSelecionados.getTramites()
                .subList(filtrosTramitesRecebidosSelecionados.getInicioConsulta(), filtrosTramitesRecebidosSelecionados.getTramites().size()));
        }
    }

    private void montarPaginasTramitesRecebidosSelecionados() {
        filtrosTramitesRecebidosSelecionados.montarPaginas(filtrosTramitesRecebidosSelecionados.getQuantidadeTotal());
    }

    public void proximosTramitesRecebidos() {
        filtrosTramitesRecebidos.proximos();
        buscarTramitesRecebidos();
    }

    public void ultimosTramitesRecebidos() {
        filtrosTramitesRecebidos.irParaUltimo();
        buscarTramitesRecebidos();
    }

    public void buscarPrimeirosTramitesRecebidos() {
        filtrosTramitesRecebidos.irParaPrimeiro();
        buscarTramitesRecebidos();
    }

    public void anterioresTramitesRecebidos() {
        filtrosTramitesRecebidos.anteriores();
        buscarTramitesRecebidos();
    }

    public void alterouPaginaTramitesRecebidos() {
        try {
            filtrosTramitesRecebidos.alterarPagina();
            buscarTramitesRecebidos();
        } catch (ValidacaoException ve) {
            filtrosTramitesRecebidos.resetarPaginas();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            filtrosTramitesRecebidos.resetarPaginas();
        }
    }

    public void buscarProcessosPendentes() {
        atribuirFiltrosProcessosPendentes();
        filtrosProcessosPendentes.setTramites(facade.buscarTramitesPendentesUnidadeProcessoProtocolo(
            filtrosProcessosPendentes.getFiltro(),
            filtrosProcessosPendentes.getInicioConsulta(),
            filtrosProcessosPendentes.getQuantidadeRegistros().getQuantidade()));
    }

    private void atribuirFiltrosProcessosPendentes() {
        filtrosProcessosPendentes.getFiltro().setAceito(false);
        filtrosProcessosPendentes.getFiltro().setStatus(true);
        filtrosProcessosPendentes.getFiltro().setGestor(true);
        filtrosProcessosPendentes.getFiltro().setProtocolo(false);
    }

    public void buscarProcessosAceitos() {
        FiltroListaProtocolo filtro = getFiltroProcessosAceitos();
        filtrosProcessosAceitos.setTramites(facade.buscarProtocoloAceitoRecebidoPorUsuario(filtro, filtrosProcessosAceitos.getInicioConsulta(), filtrosProcessosAceitos.getQuantidadeRegistros().getQuantidade()));
    }

    private FiltroListaProtocolo getFiltroProcessosAceitos() {
        FiltroListaProtocolo filtro = new FiltroListaProtocolo(
            filtrosProcessosPendentes.getFiltro().getNumero(),
            filtrosProcessosPendentes.getFiltro().getAno(),
            filtrosProcessosPendentes.getFiltro().getSolicitante(),
            getUsuarioCorrente());
        filtro.setAceito(true);
        filtro.setStatus(true);
        filtro.setGestor(true);
        filtro.setProtocolo(false);
        return filtro;
    }

    public FiltrosPesquisaProtocolo getFiltrosTramitesParaIncorporar() {
        return filtrosTramitesParaIncorporar;
    }

    public void setFiltrosTramitesParaIncorporar(FiltrosPesquisaProtocolo filtrosTramitesParaIncorporar) {
        this.filtrosTramitesParaIncorporar = filtrosTramitesParaIncorporar;
    }

    public void buscarTramitesParaIncorporar() {
        atribuirFiltrosIncorporarTramites();
        List<VoTramite> tramites = facade.buscarTramitesPorUsuarioAndUnidade(filtrosTramitesParaIncorporar.getFiltro(),
            filtrosTramitesParaIncorporar.getInicioConsulta(),
            filtrosTramitesParaIncorporar.getQuantidadeRegistros().getQuantidade());
        filtrosTramitesParaIncorporar.setTramites(tramites);
    }

    public void buscarTramitesExternosPendentes() {
        atribuirFiltroTramitesExternosPendentes();
        filtrosTramitesExternosPendentes.setTramites(facade.buscarTramitesExternoPendentes(
            filtrosTramitesExternosPendentes.getFiltro(),
            filtrosTramitesExternosPendentes.getInicioConsulta(),
            filtrosTramitesExternosPendentes.getQuantidadeRegistros().getQuantidade()));
    }

    private void atribuirFiltroTramitesExternosPendentes() {
        filtrosTramitesExternosPendentes.getFiltro().setUsuarioSistema(getUsuarioCorrente());
        filtrosTramitesExternosPendentes.getFiltro().setAceito(false);
        filtrosTramitesExternosPendentes.getFiltro().setStatus(true);
        filtrosTramitesExternosPendentes.getFiltro().setGestor(true);
        filtrosTramitesExternosPendentes.getFiltro().setProtocolo(true);
    }

    public void buscarTramitesExternosRecebidos() {
        atribuirFiltrosTramitesExternosRecebidos();
        filtrosTramitesExternosRecebidos.setTramites(facade.buscarTramitesExternoRecebidos(
            filtrosTramitesExternosPendentes.getFiltro(),
            filtrosTramitesExternosRecebidos.getInicioConsulta(),
            filtrosTramitesExternosRecebidos.getQuantidadeRegistros().getQuantidade()));
    }

    private void atribuirFiltrosTramitesExternosRecebidos() {
        filtrosTramitesExternosPendentes.getFiltro().setUsuarioSistema(getUsuarioCorrente());
        filtrosTramitesExternosPendentes.getFiltro().setAceito(true);
        filtrosTramitesExternosPendentes.getFiltro().setGestor(true);
        filtrosTramitesExternosPendentes.getFiltro().setStatus(true);
        filtrosTramitesExternosPendentes.getFiltro().setProtocolo(true);
    }

    public List<SelectItem> getTiposPrazo() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoPrazo object : TipoPrazo.values()) {
            if (object.equals(TipoPrazo.DIAS) || object.equals(TipoPrazo.HORAS)) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    private void recuperaArvoreHierarquiaOrganizacionalOrc() {
        tramitacaoProtocolo.setUnidadeSemUsuarioCadastrado(facade.getUsuarioSistemaFacade().listaUnidadeSemUsuarioCadastrado());
        tramitacaoProtocolo.setRootOrc(facade.getSingletonHO().getArvoreAdministrativa(facade.getSistemaFacade().getDataOperacao()));
    }

    public void selecionarHierarquiaDaArvore() {
        if (tramitacaoProtocolo.getSelectedNode() != null) {
            tramitacaoProtocolo.setHierarquiaOrganizacional((HierarquiaOrganizacional) tramitacaoProtocolo.getSelectedNode().getData());
            if (facade.getUsuarioSistemaFacade().buscarUsuariosDaUnidadeOrganizacionalProtocolo(tramitacaoProtocolo.getHierarquiaOrganizacional().getSubordinada()).isEmpty()) {
                FacesUtil.addError("Atenção", "Não é possível encaminhar um protocolo para uma unidade organizacional que não possua usuários cadastrados!");
                tramitacaoProtocolo.setHierarquiaOrganizacional(null);
            }
        }
    }

    public void uploadArquivo(FileUploadEvent file) {
        try {
            Arquivo arq = new Arquivo();
            arq.setNome(file.getFile().getFileName());
            arq.setMimeType(facade.getArquivoFacade().getMimeType(file.getFile().getFileName()));
            arq.setDescricao(new Date().toString());
            arq.setTamanho(file.getFile().getSize());

            ProcessoArquivo proArq = new ProcessoArquivo();
            proArq.setArquivo(facade.getArquivoFacade().novoArquivoMemoria(arq, file.getFile().getInputstream()));
            proArq.setProcesso(tramitacaoProtocolo.getProcesso());

            if (tramitacaoProtocolo.getProcesso().getArquivos() == null) {
                tramitacaoProtocolo.getProcesso().setArquivos(new ArrayList<ProcessoArquivo>());
            }
            tramitacaoProtocolo.getProcesso().getArquivos().add(proArq);
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    public Boolean getInterno() {
        return TipoProcessoTramite.INTERNO.equals(tramitacaoProtocolo.getTipoEncaminhamento());
    }

    public Boolean getExterno() {
        return TipoProcessoTramite.EXTERNO.equals(tramitacaoProtocolo.getTipoEncaminhamento());
    }

    public boolean podeExcluirRota(ProcessoRota processoRota) {
        return processoRota.getTramitado();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tramite/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public String styleNaoTemUsuario(UnidadeOrganizacional unidade) {
        if (tramitacaoProtocolo.getUnidadeSemUsuarioCadastrado().contains(unidade)) {
            return "color: blue";
        }
        return "color: #000000";
    }

    public void buscarProximosProtocolosUnidadesExtintas() {
        inicioListaTramitesUnidadesExtintas += maximoRegistrosTabela;
        buscarTramitesUnidadeExtintas();
    }

    public boolean hasProtocolosUnidadesExtintas() {
        if (tramitacaoProtocolo.getTramitesUnidadesExtintas() == null) {
            buscarTramitesUnidadeExtintas();
        }
        return tramitacaoProtocolo.getTramitesUnidadesExtintas().size() >= maximoRegistrosTabela;
    }

    public Boolean hasAnteriorProtocolosUnidadesExtintas() {
        return inicioListaTramitesUnidadesExtintas > 0;
    }

    public void anteriorProtocoloUnidadeExtintas() {
        tramitacaoProtocolo.setTramitesSelecionados(Lists.<VoTramite>newArrayList());
        inicioListaTramitesUnidadesExtintas -= maximoRegistrosTabela;
        if (inicioListaTramitesUnidadesExtintas < 0) {
            inicioListaTramitesUnidadesExtintas = 0;
        }
        buscarTramitesUnidadeExtintas();
    }

    public void adicionarNaListaTramitesSelecionados(VoTramite tramite) {
        if (tramitacaoProtocolo.getTramitesSelecionados() == null) {
            tramitacaoProtocolo.setTramitesSelecionados(Lists.<VoTramite>newArrayList());
        }
        if (!tramitacaoProtocolo.getTramitesSelecionados().contains(tramite)) {
            tramitacaoProtocolo.getTramitesSelecionados().add(tramite);
        }
    }

    public void adicionarTramitesParaIncorporar(VoTramite tramite) {
        filtrosTramitesParaIncorporar.getTramitesAuxiliares().add(tramite);
    }

    public void adicionarTramitesRecebidosSelecionados(VoTramite tramite) {
        filtrosTramitesRecebidosSelecionados.getTramites().add(tramite);
    }

    public void removeDaListaTramitesSelecionados(VoTramite tramite) {
        VoTramite remover = null;
        for (VoTramite t : tramitacaoProtocolo.getTramitesSelecionados()) {
            if (t.getId().equals(tramite.getId())) {
                remover = t;
                break;
            }
        }
        if (remover != null) {
            tramitacaoProtocolo.getTramitesSelecionados().remove(remover);
        }
    }

    public void removerTramiteRecebidoSelecionado(VoTramite tramite) {
        filtrosTramitesRecebidosSelecionados.getTramites().remove(tramite);
        pesquisarTramitesRecebidosSelecionados();
    }

    public void removerTramiteParaIncorporar(VoTramite tramite) {
        filtrosTramitesParaIncorporar.getTramitesAuxiliares().remove(tramite);
    }

    public void removerTramitePendenteSelecionado(VoTramite tramite) {
        filtrosTramitesPendentesSelecionados.getTramites().remove(tramite);
        pesquisarTramitesPendentesSelecionados();
    }

    public boolean hasTramiteRecebidoSelecionado(VoTramite tramite) {
        return filtrosTramitesRecebidosSelecionados.getTramites().contains(tramite);
    }

    public boolean tramiteNaListaDeSelecionados(VoTramite tramite) {
        if (tramitacaoProtocolo.getTramitesSelecionados() == null) {
            tramitacaoProtocolo.setTramitesSelecionados(Lists.<VoTramite>newArrayList());
        }
        return tramitacaoProtocolo.getTramitesSelecionados().contains(tramite);
    }

    public boolean hasTramiteParaIncorporar(VoTramite tramite) {
        return filtrosTramitesParaIncorporar.getTramitesAuxiliares().contains(tramite);
    }

    public void abrirDialogArquivarProtocolosSelecionados() {
        try {
            validarTramitesRecebidosSelecionados();
            pesquisarTramitesRecebidosSelecionados();
            FacesUtil.executaJavaScript("arquivarSelecionados.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarTramitesRecebidosSelecionados() {
        ValidacaoException ve = new ValidacaoException();
        if (filtrosTramitesRecebidosSelecionados.getTramites().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum Protocolo selecionado.");
        }
        ve.lancarException();
    }

    public void abrirDialogEncerrarProtocolosSelecionados() {
        try {
            validarTramitesRecebidosSelecionados();
            pesquisarTramitesRecebidosSelecionados();
            RequestContext.getCurrentInstance().execute("encerrarSelecionados.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void abrirDialogEncaminharProtocolosSelecionados() {
        try {
            validarTramitesRecebidosSelecionados();
            pesquisarTramitesRecebidosSelecionados();
            FacesUtil.executaJavaScript("encaminhar.show()");
            FacesUtil.atualizarComponente("formEncaminhar");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void abreDialogEncaminharProtocolosSelecionados() {
        if (tramitacaoProtocolo.getTramitesUnidadesExtintas().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("Nenhum Protocolo pesquisado.");
        } else if (tramitacaoProtocolo.getTramitesSelecionados().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("Nenhum Protocolo selecionado.");
        } else {
            RequestContext.getCurrentInstance().execute("encaminharSelecionados.show()");
        }
    }

    private void validaCampoEncerramentoArquivamentoSelecionados() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(tramitacaoProtocolo.getMotivoTramiteSelecionado())) {
            boolean motivoVazio = false;
            for (VoTramite voTramite : filtrosTramitesRecebidosSelecionados.getTramites()) {
                if (Strings.isNullOrEmpty(voTramite.getMotivo())) {
                    motivoVazio = true;
                    break;
                }
            }
            if (motivoVazio) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo motivo deve ser informado.");
            }
        }
        ve.lancarException();
    }


    public void encerrarProtocolosSelecionados() {
        try {
            validaCampoEncerramentoArquivamentoSelecionados();
            facade.encerrarProtocolosSelecionados(filtrosTramitesRecebidosSelecionados, tramitacaoProtocolo);
            FacesUtil.executaJavaScript("encerrarSelecionados.hide()");
            FacesUtil.redirecionamentoInterno(getUrlAtual());
            FacesUtil.addOperacaoRealizada("O(s) Trâmite(s) foi(ram) encerrado(s) com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
        FacesUtil.executaJavaScript("aguarde.hide()");
    }

    public void encerrarProtocolosSelecionadosUnidadesExtintas() {
        try {
            validaCampoEncerramentoArquivamentoSelecionados();
            facade.encerrarProtocolosSelecionadosUnidadesExtintas(tramitacaoProtocolo);
            FacesUtil.executaJavaScript("encerrarSelecionados.hide()");
            FacesUtil.redirecionamentoInterno(getUrlAtual());
            FacesUtil.addOperacaoRealizada("O(s) Trâmite(s) foi(ram) encerrado(s) com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
        FacesUtil.executaJavaScript("aguarde.hide()");
    }

    public void setarObservacaoNosTramitesSelecionados() {
        if (tramitacaoProtocolo.getTramitesSelecionados() != null) {
            for (VoTramite tr : tramitacaoProtocolo.getTramitesSelecionados()) {
                tr.setObservacoes(tramitacaoProtocolo.getObservacaoTramiteSelecionado());
            }
        }
    }

    public void setarMotivoNosTramitesSelecionados() {
        if (tramitacaoProtocolo.getTramitesSelecionados() != null) {
            for (VoTramite tr : tramitacaoProtocolo.getTramitesSelecionados()) {
                tr.setMotivo(tramitacaoProtocolo.getMotivoTramiteSelecionado());
            }
        }
    }

    public void atribuirObservacaoNosTramitesSelecionados() {
        if (filtrosTramitesRecebidosSelecionados.getTramites() != null) {
            for (VoTramite tr : filtrosTramitesRecebidosSelecionados.getTramites()) {
                tr.setObservacoes(tramitacaoProtocolo.getObservacaoTramiteSelecionado());
            }
        }
    }

    public void atribuirMotivoNosTramitesSelecionados() {
        if (filtrosTramitesRecebidosSelecionados.getTramites() != null) {
            for (VoTramite tr : filtrosTramitesRecebidosSelecionados.getTramites()) {
                tr.setMotivo(tramitacaoProtocolo.getMotivoTramiteSelecionado());
            }
        }
    }

    public void atribuirResponsavelEhDataParaTodos() {
        try {
            ValidacaoException ve = new ValidacaoException();
            if (Strings.isNullOrEmpty(tramitacaoProtocolo.getResponsavelAll())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo responsável do aceite deve ser informado.");
            }
            if (tramitacaoProtocolo.getDataAceite() == null || tramitacaoProtocolo.getHoraAceite() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Os campos data e hora do aceite devem ser informados.");
            }
            ve.lancarException();

            for (VoTramite voTramite : filtrosTramitesPendentesSelecionados.getTramites()) {
                voTramite.setResponsavel(tramitacaoProtocolo.getResponsavelAll());
                voTramite.setDataAceite(DataUtil.juntarDataEHora(tramitacaoProtocolo.getDataAceite(), tramitacaoProtocolo.getHoraAceite()));
            }
            tramitacaoProtocolo.setResponsavelAll(null);
            atribuirDataAtualDoAceite();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private String buscarMensagemDataDeAceite(Tramite tramite) {
        String mensagem = "";
        if (tramite.getProcesso().getTramites().size() > 1) {
            Tramite ultimoTramite = tramite.getProcesso().getTramites().get(tramite.getProcesso().getTramites().size() - 1);
            mensagem = "O campo Data de Aceite deve ser posterior ou igual à Data de Envio do último trâmite: " + Util.formatterDataHora.format(ultimoTramite.getDataRegistro());
        } else {
            mensagem = "O campo Data de Aceite deve ser posterior ou igual a Data de Cadastro: " + Util.formatterDataHora.format(tramite.getProcesso().getDataRegistro());
        }
        return mensagem;
    }

    public void selecionarTodosTramitePendenteProtocolo() {
        atribuirFiltrosTramitesPendentes();
        filtrosTramitesPendentesSelecionados.setTramites(facade.buscarTramitesPendentesUnidadeProcessoProtocolo(filtrosTramitesPendentes.getFiltro(), 0, 0));
        filtrosTramitesPendentesSelecionados.setQuantidadeTotal(filtrosTramitesPendentesSelecionados.getTramites().size());
    }

    public void selecionarTodosTramitesRecebidosProtocolo() {
        filtrosTramitesPendentes.getFiltro().setAceito(true);
        filtrosTramitesPendentes.getFiltro().setStatus(true);
        filtrosTramitesPendentes.getFiltro().setGestor(true);
        filtrosTramitesPendentes.getFiltro().setProtocolo(true);
        filtrosTramitesRecebidosSelecionados.setTramites(facade.buscarProtocoloAceitoRecebidoPorUsuario(filtrosTramitesPendentes.getFiltro(), 0, 0));
        quantidadeTotalDeTramitesRecebidos = filtrosTramitesRecebidosSelecionados.getTramites().size();
    }

    public void selecionarTramitePendenteProtocolo(VoTramite tramite) {
        Util.adicionarObjetoEmLista(filtrosTramitesPendentesSelecionados.getTramites(), tramite);
    }

    public void demarcarTramitePendenteProtocolo(VoTramite tramite) {
        filtrosTramitesPendentesSelecionados.getTramites().remove(tramite);
    }

    public boolean hasTodosTramitesPendentesSelecionados() {
        return filtrosTramitesPendentesSelecionados.getTramites().size() - filtrosTramitesPendentesSelecionados.getQuantidadeTotal() == 0 && filtrosTramitesPendentesSelecionados.getQuantidadeTotal() != 1;
    }

    public boolean hasTodosTramitesRecebidosSelecionados() {
        int quantidadeTramites = filtrosTramitesRecebidosSelecionados.getTramites().size();
        if (quantidadeTramites > 0) {
            return (quantidadeTramites - quantidadeTotalDeTramitesRecebidos) == 0;
        }
        return Boolean.FALSE;
    }

    public boolean hasTramitePendenteSelecionado(VoTramite tramite) {
        return filtrosTramitesPendentesSelecionados.getTramites().contains(tramite);
    }

    public void dialogAceitarTodosTramitePendente() {
        if (filtrosTramitesPendentesSelecionados.getTramites().isEmpty()) {
            FacesUtil.addAtencao("Selecione ao menos um protocolo pendente para aceitá-lo.");
            return;
        }
        atribuirDataAtualDoAceite();
        pesquisarTramitesPendentesSelecionados();
        FacesUtil.executaJavaScript("dialogAceitarProtocoloPendente.show()");
    }

    public void aceitarTramiteProtocolo() {
        try {
            validarCamposAceiteTramite();
            facade.aceitarTramiteProtocolo(selecionado, tramitacaoProtocolo);
            FacesUtil.executaJavaScript("dialogAceitaTramite.hide()");
            FacesUtil.executaJavaScript("imprimeGuia.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }


    public void aceitarTodosTramitePendente() {
        try {
            validarAceiteTodosSelecionados();
            facade.aceitarTodosTramitePendente(filtrosTramitesPendentesSelecionados);
            FacesUtil.executaJavaScript("dialogAceitarProtocoloPendente.hide();");
            pesquisarTramitesPendentesERecebidos();
            FacesUtil.atualizarComponente("Formulario");
            FacesUtil.addOperacaoRealizada(" O(s) Trâmite(s) foi(ram) aceito(s) com sucesso.");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    private void validarAceiteTodosSelecionados() {
        ValidacaoException ve = new ValidacaoException();
        for (VoTramite tramite : filtrosTramitesPendentesSelecionados.getTramites()) {
            if (SituacaoProcesso.CANCELADO.name().equals(tramite.getProcesso().getSituacao().name())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possivel realizar tramitê de um protocolo cancelado");
            }
            if (tramite.getResponsavel() == null || tramite.getResponsavel().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o responsável do protocolo " + tramite.getProcesso().getNumero() + "/" + tramite.getProcesso().getAno());
            }
            if (tramite.getDataAceite() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe a data do aceite do protocolo " + tramite.getProcesso().getNumero() + "/" + tramite.getProcesso().getAno());
            } else if (tramite.getDataAceite().compareTo(new Date()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de aceite do protocolo " + tramite.getProcesso().getNumero() + "/" + tramite.getProcesso().getAno() + " deve ser anterior à data do servidor: " + Util.formatterDataHora.format(new Date()));
            } else {
                Tramite tramiteRecuperado = facade.recuperar(tramite.getId());
                facade.validarDataDeAceite(tramiteRecuperado, tramite.getDataAceite(), ve, false, buscarMensagemDataDeAceite(tramiteRecuperado));
            }
        }
        ve.lancarException();
    }

    private Date buscarDataRegistroOrDataAceite(Tramite tramite) {
        Date maiorData = null;
        for (Tramite tramiteAnterior : tramite.getProcesso().getTramites()) {
            if (maiorData == null || (tramiteAnterior.getDataRegistro().compareTo(maiorData) > 0)) {
                maiorData = tramiteAnterior.getDataRegistro();
            }
        }
        if (maiorData == null) {
            maiorData = tramite.getDataRegistro();
        }
        return maiorData;
    }

    public void desmarcarTodosTramitePendenteProtocolo() {
        filtrosTramitesPendentesSelecionados.getTramites().clear();
    }

    public void desmarcarTodosTramitesRecebidosProtocolo() {
        filtrosTramitesRecebidosSelecionados.getTramites().clear();
    }

    public void cancelarAceiteProtocoloPendente() {
        for (VoTramite tr : filtrosTramitesPendentesSelecionados.getTramites()) {
            tr.setResponsavel(null);
            tr.setDataAceite(null);
        }
    }

    public FiltrosPesquisaProtocolo getFiltrosTramitesPendentesSelecionados() {
        return filtrosTramitesPendentesSelecionados;
    }

    public void setFiltrosTramitesPendentesSelecionados(FiltrosPesquisaProtocolo
                                                            filtrosTramitesPendentesSelecionados) {
        this.filtrosTramitesPendentesSelecionados = filtrosTramitesPendentesSelecionados;
    }

    public FiltrosPesquisaProtocolo getFiltrosTramitesRecebidosSelecionados() {
        return filtrosTramitesRecebidosSelecionados;
    }

    public void setFiltrosTramitesRecebidosSelecionados(FiltrosPesquisaProtocolo
                                                            filtrosTramitesRecebidosSelecionados) {
        this.filtrosTramitesRecebidosSelecionados = filtrosTramitesRecebidosSelecionados;
    }

    public void atribuirDataAtualDoAceite() {
        tramitacaoProtocolo.setDataAceite(null);
        tramitacaoProtocolo.setHoraAceite(null);
    }

    public void atribuirHoraAtualDoAceite() {
        tramitacaoProtocolo.setDataAceite(new Date());
        tramitacaoProtocolo.setHoraAceite(new Date());
    }

    public void replicarMotivoParaOsTramites() {
        if (filtrosTramitesRecebidosSelecionados.getTramites() != null && !filtrosTramitesRecebidosSelecionados.getTramites().isEmpty()) {
            for (VoTramite tramite : filtrosTramitesRecebidosSelecionados.getTramites()) {
                tramite.setMotivo(tramitacaoProtocolo.getMotivoTramiteSelecionado());
            }
        }
    }

    public void replicarObservacaoParaOsTramites() {
        if (filtrosTramitesRecebidosSelecionados.getTramites() != null && !filtrosTramitesRecebidosSelecionados.getTramites().isEmpty()) {
            for (VoTramite tramite : filtrosTramitesRecebidosSelecionados.getTramites()) {
                tramite.setObservacoes(tramitacaoProtocolo.getObservacaoTramiteSelecionado());
            }
        }
    }

    public class ArvoreTramite {
        private TreeNode arvore;

        public ArvoreTramite() {
            arvore = createDocuments(new HierarquiaOrganizacionalDTO(null, null, "01", facade.getSistemaFacade().getMunicipio().toUpperCase()));
        }

        public TreeNode createDocuments(HierarquiaOrganizacionalDTO hierarquiaOrganizacional) {
            try {
                TreeNode arvore = new DefaultTreeNode();
                TreeNode inicio = new DefaultTreeNode(hierarquiaOrganizacional, arvore);
                List<HierarquiaOrganizacionalDTO> hierarquias = buscarUnidadesDoUsuarioLogadoGestorProtocolo();
                construirArvore(hierarquias, inicio);
                return arvore;
            } catch (NullPointerException nu) {
                return new DefaultTreeNode(hierarquiaOrganizacional, null);
            }
        }

        public void construirArvore(List<HierarquiaOrganizacionalDTO> hierarquias, TreeNode pai) {
            for (HierarquiaOrganizacionalDTO hierarquia : hierarquias) {
                TreeNode node = new DefaultTreeNode(hierarquia, pai);
            }
        }

        public TreeNode getArvore() {
            return arvore;
        }

        public void setArvore(TreeNode arvore) {
            this.arvore = arvore;
        }

    }

    private void instanciarFiltrosParaIncorporar() {
        filtrosTramitesParaIncorporar = new FiltrosPesquisaProtocolo();
        filtrosTramitesParaIncorporar.setFiltro(new FiltroListaProtocolo("", facade.getSistemaFacade().getExercicioCorrente().getAno().toString(), "", getUsuarioCorrente()));
    }

    public TramitacaoProtocolo getTramitacaoProtocolo() {
        return tramitacaoProtocolo;
    }

    public void setTramitacaoProtocolo(TramitacaoProtocolo tramitacaoProtocolo) {
        this.tramitacaoProtocolo = tramitacaoProtocolo;
    }

    public FiltrosPesquisaProtocolo getFiltrosTramitesPendentes() {
        return filtrosTramitesPendentes;
    }

    public void setFiltrosTramitesPendentes(FiltrosPesquisaProtocolo filtrosTramitesPendentes) {
        this.filtrosTramitesPendentes = filtrosTramitesPendentes;
    }

    public FiltrosPesquisaProtocolo getFiltrosTramitesRecebidos() {
        return filtrosTramitesRecebidos;
    }

    public void setFiltrosTramitesRecebidos(FiltrosPesquisaProtocolo filtrosTramitesRecebidos) {
        this.filtrosTramitesRecebidos = filtrosTramitesRecebidos;
    }

    public FiltrosPesquisaProtocolo getFiltrosProcessosPendentes() {
        return filtrosProcessosPendentes;
    }

    public void setFiltrosProcessosPendentes(FiltrosPesquisaProtocolo filtrosProcessosPendentes) {
        this.filtrosProcessosPendentes = filtrosProcessosPendentes;
    }

    public FiltrosPesquisaProtocolo getFiltrosProcessosAceitos() {
        return filtrosProcessosAceitos;
    }

    public void setFiltrosProcessosAceitos(FiltrosPesquisaProtocolo filtrosProcessosAceitos) {
        this.filtrosProcessosAceitos = filtrosProcessosAceitos;
    }

    public FiltrosPesquisaProtocolo getFiltrosTramitesExternosPendentes() {
        return filtrosTramitesExternosPendentes;
    }

    public void setFiltrosTramitesExternosPendentes(FiltrosPesquisaProtocolo filtrosTramitesExternosPendentes) {
        this.filtrosTramitesExternosPendentes = filtrosTramitesExternosPendentes;
    }

    public FiltrosPesquisaProtocolo getFiltrosTramitesExternosRecebidos() {
        return filtrosTramitesExternosRecebidos;
    }

    public void setFiltrosTramitesExternosRecebidos(FiltrosPesquisaProtocolo filtrosTramitesExternosRecebidos) {
        this.filtrosTramitesExternosRecebidos = filtrosTramitesExternosRecebidos;
    }

    public ArvoreTramite getArvorePrincipal() {
        return arvorePrincipal;
    }

    public void setArvorePrincipal(ArvoreTramite arvorePrincipal) {
        this.arvorePrincipal = arvorePrincipal;
    }

    public void popularArvore() {
        if (Util.isNull(getArvorePrincipal())) {
            this.arvorePrincipal = new ArvoreTramite();
        }
        tramitacaoProtocolo.setArvoreTramite(arvorePrincipal);

    }
}
