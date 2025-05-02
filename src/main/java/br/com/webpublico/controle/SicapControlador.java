package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.arquivos.TipoSicapArquivo;
import br.com.webpublico.entidadesauxiliares.DependenciasDirf;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.rh.EntidadeDeConfiguracaoSemVigenciaException;
import br.com.webpublico.exception.rh.SemConfiguracaoDeFaltaInjustificada;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.GrupoRecursoFPFacade;
import br.com.webpublico.negocios.SicapFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.*;

/**
 * Criado por Mateus
 * Data: 24/05/2016.
 */
@ManagedBean(name = "sicapControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-sicap", pattern = "/arquivo/sicap/novo/", viewId = "/faces/rh/administracaodepagamento/arquivosicap/edita.xhtml"),
    @URLMapping(id = "log-sicap", pattern = "/arquivo/sicap/acompanhamento/", viewId = "/faces/rh/administracaodepagamento/arquivosicap/log.xhtml"),
    @URLMapping(id = "ver-sicap", pattern = "/arquivo/sicap/ver/#{sicapControlador.id}/", viewId = "/faces/rh/administracaodepagamento/arquivosicap/visualizar.xhtml"),
    @URLMapping(id = "editar-sicap", pattern = "/arquivo/sicap/editar/#{sicapControlador.id}/", viewId = "/faces/rh/administracaodepagamento/arquivosicap/edita.xhtml"),
    @URLMapping(id = "lista-sicap", pattern = "/arquivo/sicap/listar/", viewId = "/faces/rh/administracaodepagamento/arquivosicap/lista.xhtml")
})
public class SicapControlador extends PrettyControlador<Sicap> implements Serializable, CRUD {

    @EJB
    private SicapFacade sicapFacade;
    private Sicap sicapExistente;
    private DependenciasDirf dependenciasDirf;
    private SicapTipoArquivo[] modalidades;
    private List<GrupoRecursoFP> gruposRecursoFPs;
    @EJB
    private GrupoRecursoFPFacade grupoRecursoFPFacade;
    private TipoFiltro tipoFiltro;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    private List<FolhaDePagamento> folhaDePagamentos;
    private List<FolhaDePagamento> folhaDePagamentosSelecionadas;
    private boolean mostrarTabelaFolhaRescisao = false;

    public SicapControlador() {
        super(Sicap.class);
    }

    public List<SelectItem> getListaMeses() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Mes obj : Mes.values()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasDaEntidade() {
        List<HierarquiaOrganizacional> hierarquias = sicapFacade.getEntidadeFacade().buscarHierarquiasDaEntidade(selecionado.getEntidade(), CategoriaDeclaracaoPrestacaoContas.SICAP, selecionado.getPrimeiroDiaDoMes(), selecionado.getUltimoDiaDoMes());
        Collections.sort(hierarquias);
        return hierarquias;
    }

    public List<SelectItem> getListaEntidadesParaDeclaracao() {
        try {
            return Util.getListSelectItem(sicapFacade.getEntidadeFacade().buscarEntidadesParaDeclaracoes(CategoriaDeclaracaoPrestacaoContas.SICAP, selecionado.getPrimeiroDiaDoMes(), selecionado.getUltimoDiaDoMes()));
        } catch (NullPointerException ex) {
            logger.error("Erro!", ex);
        }
        return new ArrayList<>();
    }

    public List<SicapTipoArquivo> getListaSicapTipoArquivo() {
        return Arrays.asList(SicapTipoArquivo.values());
    }

    @URLAction(mappingId = "novo-sicap", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        gruposRecursoFPs = Lists.newLinkedList();
        folhaDePagamentosSelecionadas = Lists.newArrayList();
        definirOpcaoFiltro();
        validarEntidadeNaoVigente();
    }

    private void validarEntidadeNaoVigente() {
        try {
            List<Entidade> entidades = sicapFacade.getEntidadeFacade().buscarEntidadesParaDeclaracoes(CategoriaDeclaracaoPrestacaoContas.SICAP, sicapFacade.getSistemaFacade().getDataOperacao(), sicapFacade.getSistemaFacade().getDataOperacao());
            if (entidades == null || entidades.isEmpty()) {
                throw new EntidadeDeConfiguracaoSemVigenciaException("Sem entidade do tipo " + CategoriaDeclaracaoPrestacaoContas.SICAP.getDescricao() + " vigente");
            }

        } catch (EntidadeDeConfiguracaoSemVigenciaException semConfEx) {
            String url = "<b><a href='" + FacesUtil.getRequestContextPath() + "/entidade-prestacao-contas/listar/' target='_blank'>Configuração de Entidades para Declarações e Prestações de Contas</a></b>";
            FacesUtil.addOperacaoNaoRealizada(semConfEx.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Verifique as informações em: " + url);

        }
    }

    private void definirOpcaoFiltro() {
        tipoFiltro = TipoFiltro.TODAS;
    }

    @URLAction(mappingId = "ver-sicap", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        selecionado.setArquivo(sicapFacade.getArquivoFacade().recuperaDependencias(selecionado.getArquivo().getId()));
    }

    @URLAction(mappingId = "editar-sicap", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "log-sicap", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void log() {
        try {
            Sicap sicap = (Sicap) Web.pegaDaSessao("SICAP");
            if (sicap != null) {
                dependenciasDirf = new DependenciasDirf();
                dependenciasDirf.iniciarProcesso();
                selecionado = sicap;
                selecionado.setDataGeracao(sicapFacade.getSistemaFacade().getDataOperacao());
                selecionado.setDataReferencia(DataUtil.getPrimeiroDiaMes(selecionado.getExercicio().getAno(), selecionado.getMes().getNumeroMesIniciandoEmZero()));
                sicapFacade.gerarArquivo(selecionado, dependenciasDirf);
            } else if (dependenciasDirf.getParado()) {
                redireciona();
            }
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
        }
    }

    public void visualizar() {
        Web.limpaNavegacao();
        FacesUtil.redirecionamentoInterno("/arquivo/sicap/ver/" + selecionado.getId());
    }

    public void removerArquivoExistenteAndCriarNovo() {
        try {
            sicapFacade.remover(sicapExistente);
            gerarSicap();
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao tentar remover o arquivo existente! Detalhe: " + ex.getMessage());
        }
    }

    public void gerarArquivo() {
        try {
            validarGeracaoArquivo();
            configurarGrupoRecursos();
            adicionarSicapFolhaDePagamento();
            buscarSicapExistente();
            if (sicapExistente != null) {
                FacesUtil.atualizarComponente("idConfirmarGeracaoPeriodico");
                FacesUtil.executaJavaScript("confirmarGeracaoPeriodico.show()");
            } else {
                gerarSicap();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    private void adicionarSicapFolhaDePagamento() {
        selecionado.getSicapFolhasPagamentos().clear();
        for (FolhaDePagamento p : folhaDePagamentosSelecionadas) {
            SicapFolhaPagamento sfp = new SicapFolhaPagamento();
            sfp.setFolhaDePagamento(p);
            sfp.setSicap(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getSicapFolhasPagamentos(), sfp);
        }
    }

    private void validarGeracaoArquivo() {
        ValidacaoException ve = new ValidacaoException();
        selecionado.realizarValidacoes();
        if (modalidades.length == 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Arquivos dever ser informado. Selecione oa menos uma modalidade para continuar a operação.");
        }
        ve.lancarException();
    }

    public void gerarSicap() {
        configurarTipoArquivo();
        configurarHierarquias();
        configurarGrupoRecursos();
        handleIdsFolha();
        Web.poeNaSessao("SICAP", selecionado);
        FacesUtil.redirecionamentoInterno("/arquivo/sicap/acompanhamento/");
    }

    private void configurarTipoArquivo() {
        selecionado.setSicapTipoArquivo(new ArrayList<TipoSicapArquivo>());
        for (SicapTipoArquivo modalidade : modalidades) {
            TipoSicapArquivo tipo = new TipoSicapArquivo();
            tipo.setSicap(selecionado);
            tipo.setSicapTipoArquivo(modalidade);
            selecionado.getSicapTipoArquivo().add(tipo);
        }
    }

    private void buscarSicapExistente() {
        configurarTipoArquivo();
        sicapExistente = sicapFacade.buscarSicapPeriodicoPorTipoArquivoAndMesAndExercicio(selecionado);
    }

    private void configurarHierarquias() {
        selecionado.setHierarquiasDaEntidade(new ArrayList<HierarquiaOrganizacional>());
        selecionado.setHierarquiasBloqueadas(new LinkedList<HierarquiaOrganizacional>());
        for (HierarquiaOrganizacional hierarquiaOrganizacional : buscarHierarquiasDaEntidade()) {
            selecionado.getHierarquiasDaEntidade().add(hierarquiaOrganizacional);
        }
        preencherHierarquiasBloqueadas();
    }

    private void configurarGrupoRecursos() {
        selecionado.setGrupos(Lists.<SicapGrupoRecursoFP>newLinkedList());
        for (GrupoRecursoFP gruposRecursoFP : gruposRecursoFPs) {
            if (gruposRecursoFP.estaSelecionado()) {
                selecionado.getGrupos().add(criarGrupo(gruposRecursoFP));
            }
        }
    }


    private SicapGrupoRecursoFP criarGrupo(GrupoRecursoFP gruposRecursoFP) {
        SicapGrupoRecursoFP sicapGrupo = new SicapGrupoRecursoFP(selecionado, gruposRecursoFP);
        return sicapGrupo;
    }

    private void preencherHierarquiasBloqueadas() {
        List<Entidade> entidades = sicapFacade.getEntidadeFacade().buscarEntidadesParaDeclaracoes(CategoriaDeclaracaoPrestacaoContas.SICAP, selecionado.getPrimeiroDiaDoMes(), selecionado.getUltimoDiaDoMes());
        for (Entidade entidade : entidades) {
            if (!entidade.equals(selecionado.getEntidade())) {
                List<HierarquiaOrganizacional> hosBloqueadas = sicapFacade.getEntidadeFacade().buscarHierarquiasDaEntidade(entidade, CategoriaDeclaracaoPrestacaoContas.SICAP, selecionado.getPrimeiroDiaDoMes(), selecionado.getUltimoDiaDoMes());
                for (HierarquiaOrganizacional hosBloqueada : hosBloqueadas) {
                    selecionado.getHierarquiasBloqueadas().add(hosBloqueada);

                }
            }
        }
    }

    public void addOrRemove(ValueChangeEvent value) {
        logger.debug("new valeu {}", value.getNewValue());
        logger.debug("old valeu {}", value.getOldValue());

    }

    public void selecionarArquivo(ActionEvent evento) {
        selecionado = (Sicap) evento.getComponent().getAttributes().get("objeto");
        try {
            selecionado.setArquivo(sicapFacade.getArquivoFacade().recuperaDependencias(selecionado.getArquivo().getId()));
        } catch (Exception e) {
        }
    }

    public void gerarLog() {
        try {
            String conteudo = getCabecalhoHTMLParaLogsPDF() + getComplementoConteudoHTMLParaLogPDF(DependenciasDirf.TipoLog.ERRO);
            Util.geraPDF("Sicap - Log-erros", conteudo, FacesContext.getCurrentInstance());
        } catch (Exception ex) {
            FacesUtil.addOperacaoRealizada("Ocorreu um erro ao gerar o log! Detalhe: " + ex.getMessage());
        }
    }

    private String getCabecalhoHTMLParaLogsPDF() {
        return "<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>"
            + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
            + " <html>"

            + " <head>\n"
            + " <style type=\"text/css\">@page{size: A4 portrait;}</style>"
            + " <title>"
            + " < META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-1\">"
            + " </title>"
            + " </head>\n"

            + " <body style='font-family: Arial, 'Helvetica Neue', Helvetica, sans-serif; font-size: 10px;'>"
            + "<div style='border: 1px solid black;text-align: left; padding : 3px;'>\n"
            + " <table>" + "<tr>"
            + " <td><img src='" + getCaminhoBrasao() + "' alt='Smiley face' height='80' width='75' /></td>   "
            + " <td><b> PREFEITURA MUNICIPAL DE RIO BRANCO <br/>\n"
            + "         MUNICÍPIO DE RIO BRANCO<br/>\n"
            + "         LOG ARQUIVO SICAP </b></td>\n"
            + "</tr>" + "</table>"
            + "</div>\n"

            + "<div style='border: 1px solid black;text-align: left; margin-top: -1px; padding : 3px;'>\n"
            + "Filtros:"
            + "<ul>"
            + "<li>Ref. <b>" + selecionado + "</b></li>"
            + "<li>Data Processamento: <b>" + Util.formatterDataHora.format(selecionado.getDataGeracao()) + "</b></li>"
            + "<li>Usuário Responsável: <b>" + sicapFacade.getSistemaFacade().getUsuarioCorrente().getLogin() + "</b></li>"
            + "</ul>\n";
    }

    private String getComplementoConteudoHTMLParaLogPDF(DependenciasDirf.TipoLog erro) {
        String conteudo = "<div style='text-align: center'>"
            + "<b>Mensagens de " + erro.getDescricao() + "</b>"
            + "</div>\n"
            + dependenciasDirf.recuperarSomenteStringDoLog(erro)
            + "</div>"
            + " </body>"
            + " </html>";
        return conteudo;
    }

    private String getCaminhoBrasao() {
        String imagem = FacesUtil.geraUrlImagemDir();
        imagem += "/img/escudo.png";
        return imagem;
    }

    public StreamedContent recuperarArquivoParaDownload() {
        StreamedContent s = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte a : selecionado.getArquivo().getPartes()) {
            try {
                buffer.write(a.getDados());
            } catch (IOException ex) {
                logger.error("Erro: ", ex);
            }
        }
        InputStream is = new ByteArrayInputStream(buffer.toByteArray());
        s = new DefaultStreamedContent(is, selecionado.getArquivo().getMimeType(), selecionado.getArquivo().getNome());
        return s;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/arquivo/sicap/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return sicapFacade;
    }

    public Sicap getSicapExistente() {
        return sicapExistente;
    }

    public void setSicapExistente(Sicap sicapExistente) {
        this.sicapExistente = sicapExistente;
    }

    public DependenciasDirf getDependenciasDirf() {
        return dependenciasDirf;
    }

    public void setDependenciasDirf(DependenciasDirf dependenciasDirf) {
        this.dependenciasDirf = dependenciasDirf;
    }

    public SicapTipoArquivo[] getModalidades() {
        return modalidades;
    }

    public void setModalidades(SicapTipoArquivo[] modalidades) {
        this.modalidades = modalidades;
    }

    public List<GrupoRecursoFP> getGruposRecursoFPs() {
        return gruposRecursoFPs;
    }

    public void setGruposRecursoFPs(List<GrupoRecursoFP> gruposRecursoFPs) {
        this.gruposRecursoFPs = gruposRecursoFPs;
    }

    public TipoFiltro getTipoFiltro() {
        return tipoFiltro;
    }

    public void setTipoFiltro(TipoFiltro tipoFiltro) {
        this.tipoFiltro = tipoFiltro;
    }

    public void removerTodosGrupos() {
        for (GrupoRecursoFP grupo : gruposRecursoFPs) {
            grupo.setSelecionado(false);
        }
    }

    public Boolean todosGrupoRecursoMarcados() {
        for (GrupoRecursoFP grupo : gruposRecursoFPs) {
            if (!grupo.getSelecionado())
                return false;
        }
        return true;
    }

    public void adicionarTodosGrupoRecursos() {
        for (GrupoRecursoFP grupo : gruposRecursoFPs) {
            grupo.setSelecionado(true);
        }
    }

    public void removerGrupoRecurso(ActionEvent ev) {
        GrupoRecursoFP g = (GrupoRecursoFP) ev.getComponent().getAttributes().get("grupoRecurso");
        g.setSelecionado(false);
    }

    public void adicionarGrupoRecurso(ActionEvent ev) {
        GrupoRecursoFP g = (GrupoRecursoFP) ev.getComponent().getAttributes().get("grupoRecurso");
        g.setSelecionado(true);
    }

    public void buscarGrupoRecursoFPPorHierarquia() {
        definirOpcaoFiltro();
        if (selecionado.getEntidade() != null) {
            gruposRecursoFPs = Lists.newLinkedList();
            for (HierarquiaOrganizacional hierarquiaOrganizacional : buscarHierarquiasDaEntidade()) {
                gruposRecursoFPs.addAll(grupoRecursoFPFacade.buscarGruposRecursoFPPorOrgaoAndDataOperacao(hierarquiaOrganizacional));
            }
        }
    }

    public List<SelectItem> getTiposFiltro() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoFiltro obj : TipoFiltro.values()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public void filtrarOpcoes() {
        logger.debug("Opcao {}", tipoFiltro);
        filtrarGruposPorOpcaoFiltro();
    }

    private void filtrarGruposPorOpcaoFiltro() {
        gruposRecursoFPs = Lists.newLinkedList();
        for (HierarquiaOrganizacional hierarquiaOrganizacional : buscarHierarquiasDaEntidade()) {
            for (GrupoRecursoFP grupoRecursoFP : grupoRecursoFPFacade.buscarGruposRecursoFPPorOrgaoAndDataOperacao(hierarquiaOrganizacional)) {
                if (podeAdicionarNoGrupo(grupoRecursoFP)) {
                    gruposRecursoFPs.add(grupoRecursoFP);
                }
            }


        }
    }

    private boolean podeAdicionarNoGrupo(GrupoRecursoFP grupoRecursoFP) {
        if (tipoFiltro != null) {
            if (TipoFiltro.TODAS.equals(tipoFiltro)) {
                return true;
            }
            if (TipoFiltro.APENAS_FUNDO.equals(tipoFiltro) && grupoRecursoFP.getFundo()) {
                return true;
            }
            if (TipoFiltro.APENAS_SUBFOLHA.equals(tipoFiltro) && !grupoRecursoFP.getFundo()) {
                return true;
            }
        }
        return false;
    }

    public void validarCamposParaBuscarFolha() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        }
        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercicio deve ser informado.");
        }
        ve.lancarException();
    }

    public void buscarFolhasRescisao() {
        try {
            boolean hasFolhaRescisao = false;
            if (modalidades != null) {
                for (SicapTipoArquivo s : modalidades) {
                    if (SicapTipoArquivo.RESCISAO.equals(s)) {
                        validarCamposParaBuscarFolha();
                        folhaDePagamentos = folhaDePagamentoFacade.buscarFolhaPorTipoMesAndExercicio(TipoFolhaDePagamento.RESCISAO, selecionado.getMes(), selecionado.getExercicio());
                        hasFolhaRescisao = true;
                    }
                }
                mostrarTabelaFolhaRescisao = hasFolhaRescisao;
                if (!mostrarTabelaFolhaRescisao) {
                    folhaDePagamentos = Lists.newArrayList();
                    folhaDePagamentosSelecionadas = Lists.newArrayList();
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<FolhaDePagamento> getFolhaDePagamentos() {
        return folhaDePagamentos;
    }

    public void setFolhaDePagamentos(List<FolhaDePagamento> folhaDePagamentos) {
        this.folhaDePagamentos = folhaDePagamentos;
    }

    public boolean isMostrarTabelaFolhaRescisao() {
        return mostrarTabelaFolhaRescisao;
    }

    public void setMostrarTabelaFolhaRescisao(boolean mostrarTabelaFolhaRescisao) {
        this.mostrarTabelaFolhaRescisao = mostrarTabelaFolhaRescisao;
    }

    public void removerTodasFolhasRescisao() {
        folhaDePagamentosSelecionadas.clear();
    }

    public Boolean todasFolhasRescisaoMarcadas() {
        for (FolhaDePagamento f : folhaDePagamentos) {
            if (!folhaDePagamentosSelecionadas.contains(f)) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public Boolean containsFolhaRescisao(FolhaDePagamento f) {
        return folhaDePagamentosSelecionadas.contains(f);
    }

    public void adicionarTodasFolhasRescisao() {
        folhaDePagamentosSelecionadas.clear();
        folhaDePagamentosSelecionadas.addAll(folhaDePagamentos);
    }

    public void removerFolhaRescisao(FolhaDePagamento f) {
        folhaDePagamentosSelecionadas.remove(f);
    }

    public void adicionarFolhaRescisao(FolhaDePagamento f) {
        if (!folhaDePagamentosSelecionadas.contains(f)) {
            folhaDePagamentosSelecionadas.add(f);
        }
    }

    public void handleIdsFolha() {
        if (folhaDePagamentosSelecionadas != null && !folhaDePagamentosSelecionadas.isEmpty()) {
            for (FolhaDePagamento f : folhaDePagamentosSelecionadas) {
                selecionado.getFolhasPagamentosRescisao().add(f.getId());
            }
        } else if (folhaDePagamentos != null && !folhaDePagamentos.isEmpty()) {
            for (FolhaDePagamento f : folhaDePagamentos) {
                selecionado.getFolhasPagamentosRescisao().add(f.getId());
            }
        }
    }

    public List<SelectItem> getTipoFolhaDepagamentoSicap() {
        return Util.getListSelectItemSemCampoVazio(TipoFolhaDePagamentoSicap.values(), false);
    }

    public enum TipoFiltro {
        TODAS("Todas"),
        APENAS_FUNDO("Sub Folha(Apenas Fundos)"),
        APENAS_SUBFOLHA("Sub Folhas(Que não São Fundos)");
        private String descricao;

        TipoFiltro(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
