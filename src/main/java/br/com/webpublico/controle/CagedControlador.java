package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DependenciasDirf;
import br.com.webpublico.enums.CategoriaDeclaracaoPrestacaoContas;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Leonardo
 */
@ManagedBean(name = "cagedControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoCaged", pattern = "/arquivo-caged/novo/", viewId = "/faces/rh/administracaodepagamento/arquivocaged/edita.xhtml"),
    @URLMapping(id = "visualizarCaged", pattern = "/arquivo-caged/ver/#{cagedControlador.id}/", viewId = "/faces/rh/administracaodepagamento/arquivocaged/visualizar.xhtml"),
    @URLMapping(id = "listarCaged", pattern = "/arquivo-caged/listar/", viewId = "/faces/rh/administracaodepagamento/arquivocaged/lista.xhtml"),
    @URLMapping(id = "logCaged", pattern = "/arquivo-caged/acompanhamento/", viewId = "/faces/rh/administracaodepagamento/arquivocaged/log.xhtml")})
public class CagedControlador extends PrettyControlador<Caged> implements Serializable, CRUD {

    @EJB
    private CagedFacade cagedFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    private File arquivo;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    private Integer sequencia;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private DefaultStreamedContent fileDownload;
    private ConverterAutoComplete converterPessoaFisica;
    private ConverterGenerico converterExercicio;
    @EJB
    private ExercicioFacade exercicioFacade;
    private DependenciasDirf dependenciasDirf;

    public CagedControlador() {
        super(Caged.class);
    }

    public DependenciasDirf getDependenciasDirf() {
        return dependenciasDirf;
    }

    public void setDependenciasDirf(DependenciasDirf dependenciasDirf) {
        this.dependenciasDirf = dependenciasDirf;
    }

    public ConverterGenerico getConverterEntidade() {
        return new ConverterGenerico(Entidade.class, entidadeFacade);
    }

    public Caged getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Caged selecionado) {
        this.selecionado = selecionado;
    }

    public File getArquivo() {
        return arquivo;
    }

    public void setArquivo(File arquivo) {
        this.arquivo = arquivo;
    }

    public Integer getSequencia() {
        return sequencia;
    }

    public void setSequencia(Integer sequencia) {
        this.sequencia = sequencia;
    }

    public void setFileDownload(DefaultStreamedContent fileDownload) {
        this.fileDownload = fileDownload;
    }

    public void gerarCaged() {
        if (!validaCamposLocal()) {
            return;
        }

        if (cagedFacade.jaExisteCagedPara(selecionado.getEntidade(), selecionado.getMes(), selecionado.getExercicio())) {
            FacesUtil.executaJavaScript("dialogArquivoJaExistente.show()");
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.atualizarComponente("form-arquivo-existente");
            return;
        }


        gerarArquivo();
    }

    public void gerarArquivo() {
        selecionado.setGeradoEm(new Date());
        selecionado.setUsuarioSistemaLogado(sistemaFacade.getUsuarioCorrente());
        Web.poeNaSessao("CAGED", selecionado);
        FacesUtil.redirecionamentoInterno("/arquivo-caged/acompanhamento/");
    }

    private boolean validaCamposLocal() {
        boolean retorno = true;
        if (selecionado.getEntidade() == null || selecionado.getEntidade().getId() == null) {
            FacesUtil.addAtencao("Informe a entidade.");
            retorno = false;
        }
        if (selecionado.getMes() == null) {
            FacesUtil.addAtencao("Informe o mês de referência.");
            retorno = false;
        }
        if (selecionado.getExercicio() == null || selecionado.getExercicio().getAno() == null) {
            FacesUtil.addAtencao("Informe o ano de referência.");
            retorno = false;
        }
        if (selecionado.getPessoaFisica() == null || selecionado.getPessoaFisica().getId() == null) {
            FacesUtil.addAtencao("Informe a pessoa responsável.");
            retorno = false;
        }
        return retorno;
    }

    public void limparCampos() {
        this.selecionado = new Caged();
        this.arquivo = null;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, " "));
        for (Mes mes : Mes.values()) {
            retorno.add(new SelectItem(mes, mes.getDescricao()));
        }
        return retorno;
    }

    public StreamedContent fileDownload() throws FileNotFoundException, IOException {
        arquivo = new File("CAGED.txt");
        FileOutputStream fos = new FileOutputStream(arquivo);
        fos.write(selecionado.getConteudoArquivo().getBytes());
        fos.close();

        InputStream stream = new FileInputStream(arquivo);
        fileDownload = new DefaultStreamedContent(stream, "text/plain", "CAGED" + recuperaCompetencia(selecionado) + ".TXT");
        arquivo = null;
        return fileDownload;
    }

    public DefaultStreamedContent getFileDownload() {
        return fileDownload;
    }

    @Override
    public AbstractFacade getFacede() {
        return cagedFacade;
    }

    @URLAction(mappingId = "novoCaged", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        definirMesCaged();
        definirAnoCaged();
    }

    @URLAction(mappingId = "visualizarCaged", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        return contratoFPFacade.listaPessoasComContratosVigentesAno(parte.trim(), DataUtil.montaData(01, 01, selecionado.getExercicio().getAno()).getTime());
    }

    public Converter getConverterPessoaFisica() {
        if (converterPessoaFisica == null) {
            converterPessoaFisica = new ConverterAutoComplete(PessoaFisica.class, pessoaFacade);
        }
        return converterPessoaFisica;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (Exercicio ex : this.exercicioFacade.listaExerciciosAtual()) {
            lista.add(new SelectItem(ex, ex.toString()));
        }
        return lista;
    }

    public ConverterGenerico getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterGenerico(Exercicio.class, exercicioFacade);
        }
        return converterExercicio;
    }

    public void geraTxt() throws IOException {
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
            + "<p style='font-size : 15px;'><b><u> Ref. " + StringUtil.removeCaracteresEspeciaisSemEspaco(selecionado.toString()) + "<u></b></p>"
            + "<p style='font-size : 15px;'><b><u>Data Processamento: " + Util.formatterDataHora.format(selecionado.getGeradoEm()) + "<u></b></p>"
            + "<p style='font-size : 15px;'><b><u>USUÁRIO RESPONSÁVEL: " + sistemaFacade.getUsuarioCorrente().getLogin() + "<u></b></p>"
            + "<p style='font-size : 10px;'>"
            + dependenciasDirf.getSomenteStringDoLog()
            + "</p>"
            + " </body>"
            + " </html>";
        String nome = "Log geração arquivo CAGED - " + selecionado;
        nome = nome.replace(" ", "_");
        Util.downloadPDF(nome, conteudo, FacesContext.getCurrentInstance());
    }

    public void gerarTxtIncosistencia() throws FileNotFoundException, IOException {
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
            + "<p style='font-size : 15px;'><b><u> Ref. " + StringUtil.removeCaracteresEspeciaisSemEspaco(selecionado.toString()) + "<u></b></p>"
            + "<p style='font-size : 15px;'><b><u>Data Processamento: " + Util.formatterDataHora.format(selecionado.getGeradoEm()) + "<u></b></p>"
            + "<p style='font-size : 15px;'><b><u>USUÁRIO RESPONSÁVEL: " + sistemaFacade.getUsuarioCorrente().getLogin() + "<u></b></p>"
            + "<p style='font-size : 10px;'>"
            + "<p style='font-size : 15px;'><b>FUNCIONÁRIOS COM DADOS INCOSISTENTES: </b></p>"
            + "<p style='font-size : 10px;'>"
            + dependenciasDirf.getStringLogIncosistencia()
            + "</p>"
            + " </body>"
            + " </html>";
        String nome = "Log geração incosistência CAGED - " + selecionado;
        nome = nome.replace(" ", "_");
        Util.downloadPDF(nome, conteudo, FacesContext.getCurrentInstance());
    }

    @URLAction(mappingId = "logCaged", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void log() {
        try {
            Caged c = (Caged) Web.pegaDaSessao("CAGED");
            if (c != null) {
                dependenciasDirf = new DependenciasDirf();
                dependenciasDirf.iniciarProcesso();
                dependenciasDirf.setDescricaoProcesso("Geração do Arquivo Caged");
                dependenciasDirf.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
                selecionado = c;
                cagedFacade.gerarArquivo(selecionado, dependenciasDirf);
            } else {
                FacesUtil.redirecionamentoInterno("/arquivo-caged/listar/");
            }

        } catch (RuntimeException re) {
            logger.debug(re.getMessage());
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), re.getMessage());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void selecionar(ActionEvent evento) {
        selecionado = (Caged) evento.getComponent().getAttributes().get("objeto");
    }

    private String recuperaCompetencia(Caged caged) {
        String mes = (caged.getMes().getNumeroMes() >= 9 ? "" : "0") + (caged.getMes().getNumeroMes());
        String ano = "" + caged.getExercicio().getAno();
        return mes + ano;
    }


    @Override
    public String getCaminhoPadrao() {
        return "/arquivo-caged/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public String mensagemArquivoExistente() {
        String retorno = "Já existe um arquivo gerado para a entidade: <b>" + selecionado.getEntidade() + "</b> em <b>" + selecionado.getMes() + "/" + selecionado.getExercicio().getAno() + "</b>.";
        return retorno;
    }

    private void definirMesCaged() {
        selecionado.setMes(Mes.getMesToInt(DataUtil.getMes(UtilRH.getDataOperacao())));
    }

    private void definirAnoCaged() {
        selecionado.setExercicio(UtilRH.getExercicio());
    }

    public List<SelectItem> buscarEntidadesParaDeclaracao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        try {
            for (Entidade e : entidadeFacade.buscarEntidadesParaDeclaracoes(CategoriaDeclaracaoPrestacaoContas.CAGED, selecionado.getPrimeiroDiaDoMes(), selecionado.getUltimoDiaDoMes())) {
                toReturn.add(new SelectItem(e, e.getNome() + " - " + e.getSigla()));
            }
        } catch (NullPointerException npe) {
            return toReturn;
        }
        return toReturn;
    }

    public boolean orgaoEstaEmEstabelecimentoSelecionado(HierarquiaOrganizacional ho) {
        return entidadeFacade.entidadePossuiUnidadeParaDeclaracao(ho, selecionado.getEntidade(), CategoriaDeclaracaoPrestacaoContas.SEFIP, selecionado.getPrimeiroDiaDoMes(), selecionado.getUltimoDiaDoMes());
    }

    public List<HierarquiaOrganizacional> orgaosVigentesNaDataDoSefip() {
        return hierarquiaOrganizacionalFacade.getHierarquiasVigentesNoPeriodoNoNivel(selecionado.getPrimeiroDiaDoMes(), selecionado.getUltimoDiaDoMes(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, 2);
    }
}
