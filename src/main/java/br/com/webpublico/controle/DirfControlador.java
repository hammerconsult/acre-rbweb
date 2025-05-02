/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Controlador
 *
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.dirf.DirfUsuario;
import br.com.webpublico.entidades.rh.dirf.DirfVinculoFP;
import br.com.webpublico.entidadesauxiliares.DependenciasDirf;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoDirf", pattern = "/dirf/novo/", viewId = "/faces/rh/administracaodepagamento/dirf/edita.xhtml"),
    @URLMapping(id = "editarDirf", pattern = "/dirf/editar/#{dirfControlador.id}/", viewId = "/faces/rh/administracaodepagamento/dirf/edita.xhtml"),
    @URLMapping(id = "listarDirf", pattern = "/dirf/listar/", viewId = "/faces/rh/administracaodepagamento/dirf/lista.xhtml"),
    @URLMapping(id = "verDirf", pattern = "/dirf/ver/#{dirfControlador.id}/", viewId = "/faces/rh/administracaodepagamento/dirf/visualizar.xhtml"),
    @URLMapping(id = "logDirf", pattern = "/dirf/acompanhamento/", viewId = "/faces/rh/administracaodepagamento/dirf/log.xhtml")
})
public class DirfControlador extends PrettyControlador<Dirf> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(DirfControlador.class);

    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private DirfFacade dirfFacade;
    private ConverterGenerico exercicioConverter;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    private ConverterAutoComplete contratoFPConverter;
    private ConverterAutoComplete converterEntidade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hoFacade;
    private DependenciasDirf dependenciasDirf;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private TipoEmissaoDirf tipoEmissaoDirf;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    private Boolean hasPermissaoGerarIndividual = false;
    private DirfVinculoFP vinculoParaDirf;

    public DirfControlador() {
        super(Dirf.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/dirf/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void selecionar(ActionEvent evento) {
        selecionado = (Dirf) evento.getComponent().getAttributes().get("objeto");
        try {
            esteSelecionado().setArquivo(arquivoFacade.recuperaDependencias(esteSelecionado().getArquivo().getId()));
        } catch (Exception e) {
        }
    }

    @Override
    @URLAction(mappingId = "novoDirf", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        selecionado = new Dirf();
        operacao = Operacoes.NOVO;
        selecionado.setExercicio(UtilRH.getExercicio());
        vinculoParaDirf = new DirfVinculoFP();
//        verificarPermissaoGerarIndividual(); //TODO corrigir para as tratativas de gerar DIRF individual
    }

    private void verificarPermissaoGerarIndividual() {
        ConfiguracaoRH configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigente();
        configuracaoRH = configuracaoRHFacade.recuperar(configuracaoRH.getId());
        if (configuracaoRH.getItemDirfUsuario() != null && !configuracaoRH.getItemDirfUsuario().isEmpty()) {
            UsuarioSistema usuarioSistema = sistemaFacade.getUsuarioCorrente();
            for (DirfUsuario dirfUsuario : configuracaoRH.getItemDirfUsuario()) {
                if (dirfUsuario.getUsuarioSistema().equals(usuarioSistema)) {
                    hasPermissaoGerarIndividual = true;
                    break;
                }
            }
        }
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return afastamentoFacade.listaFiltrandoContratoFPVigente(parte.trim());
    }

    public DependenciasDirf getDependenciasDirf() {
        return dependenciasDirf;
    }

    public void setDependenciasDirf(DependenciasDirf dependenciasDirf) {
        this.dependenciasDirf = dependenciasDirf;
    }

    public ConverterAutoComplete getContratoFPConverter() {
        if (contratoFPConverter == null) {
            contratoFPConverter = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return contratoFPConverter;
    }

    public ConverterGenerico getExercicioConverter() {
        if (exercicioConverter == null) {
            exercicioConverter = new ConverterGenerico(Exercicio.class, exercicioFacade);
        }
        return exercicioConverter;
    }

    @Override
    public AbstractFacade getFacede() {
        return dirfFacade;
    }

    public Dirf esteSelecionado() {
        return (Dirf) selecionado;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Exercicio e : exercicioFacade.buscarExerciciosAnterioresAnoAtual(sistemaFacade.getExercicioCorrente().getAno())) {
            toReturn.add(new SelectItem(e, "" + e.getAno()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposDirf() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoDirf tipoDirf : TipoDirf.values()) {
            toReturn.add(new SelectItem(tipoDirf, tipoDirf.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getEntidades() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (UnidadeOrganizacional uo : unidadeOrganizacionalFacade.retornaEntidades()) {
            toReturn.add(new SelectItem(uo, "" + uo.getDescricao()));
        }
        return toReturn;
    }

    public StreamedContent recuperarArquivoParaDownload() {
        StreamedContent s = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte a : esteSelecionado().getArquivo().getPartes()) {
            try {
                buffer.write(a.getDados());
            } catch (IOException ex) {
                logger.error("Erro: ", ex);
            }
        }
        InputStream is = new ByteArrayInputStream(buffer.toByteArray());
        s = new DefaultStreamedContent(is, esteSelecionado().getArquivo().getMimeType(), esteSelecionado().getArquivo().getNome());
        return s;
    }

    public void geraTxt() throws FileNotFoundException, IOException {
        String conteudo = "<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>"
            + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
            + " <html>"
            + " <head>"
            + " <style type=\"text/css\">@page{size: A4 portrait;}</style>"
            + " <title>"
            + " < META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-1\">"
            + " </title>"
            + " </head>"
            + " <body>"
            + "<p style='font-size : 15px;'><b><u>" + esteSelecionado() + "<u></b></p>"
            + "<p style='font-size : 15px;'><b><u>USUÁRIO RESPONSÁVEL: " + sistemaFacade.getUsuarioCorrente().getLogin() + "<u></b></p>"
            + "<p style='font-size : 10px;'>"
            + dependenciasDirf.getSomenteStringDoLog()
            + "</p>"
            + " </body>"
            + " </html>";
        String nome = "Log da geração do arquivo DIRF - " + esteSelecionado();
        nome = nome.replace(" ", "_");
        Util.downloadPDF(nome, conteudo, FacesContext.getCurrentInstance());
    }

    public void validarParametrosParaGeracao() {
        if (!Util.validaCampos(selecionado)) {
            return;
        }
        try {
            if (selecionado.getTipoDirf().equals(TipoDirf.RH)) {
                dirfFacade.jaExisteArquivoGeradoParaExercicio(selecionado);
            } else if (selecionado.getTipoDirf().equals(TipoDirf.CONTABIL)) {
                dirfFacade.validarArquivoContabil(selecionado);
            }
            gerarDirf();
        } catch (RuntimeException coe) {
            FacesUtil.executaJavaScript("dialogArquivoJaExistente.show()");
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.atualizarComponente("form-arquivo-existente");
            return;
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
            return;
        }
    }

    public void gerarDirf() {
        Web.poeNaSessao("DIRF", selecionado);
        Web.poeNaSessao("OPERACAO-DIRF", operacao);
        if (selecionado.getTipoDirf().equals(TipoDirf.RH)) {
            if (selecionado.getItemDirfVinculoFP() == null || selecionado.getItemDirfVinculoFP().isEmpty()) {
                Dirf dirf = dirfFacade.getDirf(selecionado.getExercicio(), selecionado.getEntidade());
                if (dirf != null) {
                    dirfFacade.remover(dirf);
                }
            }
        }
        FacesUtil.redirecionamentoInterno("/dirf/acompanhamento/");
    }

    @URLAction(mappingId = "logDirf", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void log() {
        try {
            Dirf d = (Dirf) Web.pegaDaSessao("DIRF");
            Operacoes op = (Operacoes) Web.pegaDaSessao("OPERACAO-DIRF");

            if (d != null && op != null) {
                dependenciasDirf = new DependenciasDirf();
                dependenciasDirf.iniciarProcesso();
                dependenciasDirf.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
                dependenciasDirf.setDescricaoProcesso("Declaração de Imposto de Renda Retido na Fonte (DIRF) " + selecionado.getTipoDirf().getDescricao() + "/" + selecionado.getExercicio());
                selecionado = d;
                operacao = op;
                selecionado.setDataOperacao(UtilRH.getDataOperacao());
                dirfFacade.gerarArquivo(operacao, selecionado, dependenciasDirf, d.getTipoDirf().name().equals(TipoDirf.CONTABIL.name()), d.getTipoEmissaoDirf());
            } else if (dependenciasDirf.getParado()) {
                FacesUtil.redirecionamentoInterno("/dirf/listar/");
            }
        } catch (RuntimeException re) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), re.getMessage());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    @Override
    @URLAction(mappingId = "editarDirf", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        selecionado.setArquivo(arquivoFacade.recuperaDependencias(esteSelecionado().getArquivo().getId()));
    }

    @Override
    @URLAction(mappingId = "verDirf", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public List<SelectItem> buscarEntidadesParaDeclaracao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Entidade e : entidadeFacade.buscarEntidadesParaDeclaracoes(CategoriaDeclaracaoPrestacaoContas.DIRF, selecionado.getPrimeiroDia(), selecionado.getUltimoDia())) {
            toReturn.add(new SelectItem(e, e.getNome() + " - " + e.getSigla()));
        }
        return toReturn;
    }

    public List<SelectItem> tipoEmissao() {
        List<SelectItem> tipo = new ArrayList<>();
        for (TipoEmissaoDirf emissaoDirf : TipoEmissaoDirf.values()) {
            tipo.add(new SelectItem(emissaoDirf, emissaoDirf.toString()));
        }
        return Util.ordenaSelectItem(tipo);
    }


    public List<HierarquiaOrganizacional> orgaosVigentesNaDataDaDirf() {
        return hierarquiaOrganizacionalFacade.getHierarquiasVigentesNoPeriodoNoNivel(selecionado.getPrimeiroDia(), selecionado.getUltimoDia(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, 2);
    }

    public boolean orgaoEstaEmEstabelecimentoSelecionado(HierarquiaOrganizacional ho) {
        return entidadeFacade.entidadePossuiUnidadeParaDeclaracao(ho, selecionado.getEntidade(), CategoriaDeclaracaoPrestacaoContas.DIRF, selecionado.getPrimeiroDia(), selecionado.getUltimoDia());
    }

    public ConverterAutoComplete getConverterEntidade() {
        if (converterEntidade == null) {
            converterEntidade = new ConverterAutoComplete(Entidade.class, entidadeFacade);
        }
        return converterEntidade;
    }

    public String mensagemArquivoExistente() {
        String retorno = "Já existe um arquivo gerado para a entidade: <b>" + selecionado.getEntidade() + "</b> em <b>" + selecionado.getExercicio().getAno() + "</b> do tipo <b>" + selecionado.getTipoDirf().getDescricao() + "</b>.";
        return retorno;
    }

    public TipoEmissaoDirf getTipoEmissaoDirf() {
        return tipoEmissaoDirf;
    }

    public void setTipoEmissaoDirf(TipoEmissaoDirf tipoEmissaoDirf) {
        this.tipoEmissaoDirf = tipoEmissaoDirf;
    }

    public Boolean getHasPermissaoGerarIndividual() {
        return hasPermissaoGerarIndividual;
    }

    public void setHasPermissaoGerarIndividual(Boolean hasPermissaoGerarIndividual) {
        this.hasPermissaoGerarIndividual = hasPermissaoGerarIndividual;
    }

    public DirfVinculoFP getVinculoParaDirf() {
        return vinculoParaDirf;
    }

    public void setVinculoParaDirf(DirfVinculoFP vinculoParaDirf) {
        this.vinculoParaDirf = vinculoParaDirf;
    }

    public void adicionarVinculoFP() {
        try {
            validarVinculoFP();
            selecionado.getItemVinculoFP().add(vinculoParaDirf);
            vinculoParaDirf = new DirfVinculoFP();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarVinculoFP() {
        ValidacaoException ve = new ValidacaoException();
        if (vinculoParaDirf.getVinculoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Servidor não informado.");
        }
        ve.lancarException();
    }


    public void removerVinculoFP(DirfVinculoFP dirfVinculoFP) {
        selecionado.getItemVinculoFP().remove(vinculoParaDirf);
    }

}
