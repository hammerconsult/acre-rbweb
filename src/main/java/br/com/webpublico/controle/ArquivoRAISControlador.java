package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AuxiliarAndamentoRais;
import br.com.webpublico.entidadesauxiliares.RegistroRAISTipo2;
import br.com.webpublico.enums.CategoriaDeclaracaoPrestacaoContas;
import br.com.webpublico.enums.IndicadorRetificacao;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
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
import java.util.*;
import java.util.concurrent.Future;

/**
 * @author Claudio
 */
@ManagedBean(name = "arquivoRAISControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRais", pattern = "/arquivo-rais/novo/", viewId = "/faces/rh/administracaodepagamento/arquivorais/edita.xhtml"),
    @URLMapping(id = "visualizarRais", pattern = "/arquivo-rais/ver/#{arquivoRAISControlador.id}/", viewId = "/faces/rh/administracaodepagamento/arquivorais/visualizar.xhtml"),
    @URLMapping(id = "listarRais", pattern = "/arquivo-rais/listar/", viewId = "/faces/rh/administracaodepagamento/arquivorais/lista.xhtml"),
    @URLMapping(id = "logRais", pattern = "/arquivo-rais/acompanhamento/", viewId = "/faces/rh/administracaodepagamento/arquivorais/log.xhtml")})
public class ArquivoRAISControlador extends PrettyControlador<ArquivoRAIS> implements Serializable, CRUD {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ArquivoRAISFacade arquivoRAISFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private EntidadeDPContasFacade entidadeDPContasFacade;
    @EJB
    private ItemEntidadeDPContasFacade itemEntidadeDPContasFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private ConverterAutoComplete converterPessoaFisica;
    private StreamedContent fileDownload;
    private File arquivo;
    private AuxiliarAndamentoRais auxiliarAndamentoRais;
    private List<Future<AuxiliarAndamentoRais>> futuresConteudo;
    private Future<AuxiliarAndamentoRais> futureCabecalho;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Future<AssistenteBarraProgresso> futureGravarArquivo;

    public ArquivoRAISControlador() {
        super(ArquivoRAIS.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return this.arquivoRAISFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/arquivo-rais/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public AuxiliarAndamentoRais getAuxiliarAndamentoRais() {
        return auxiliarAndamentoRais;
    }

    public void setAuxiliarAndamentoRais(AuxiliarAndamentoRais auxiliarAndamentoRais) {
        this.auxiliarAndamentoRais = auxiliarAndamentoRais;
    }

    @URLAction(mappingId = "novoRais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "visualizarRais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
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
            + "<p style='font-size : 15px;'><b><u> Ref. " + StringUtil.removeCaracteresEspeciaisSemEspaco(selecionado.toString()) + "<u></b></p>"
            + "<p style='font-size : 15px;'><b><u>Data Processamento: " + Util.formatterDataHora.format(selecionado.getGeradoEm()) + "<u></b></p>"
            + "<p style='font-size : 15px;'><b><u>USUÁRIO RESPONSÁVEL: " + sistemaFacade.getUsuarioCorrente().getLogin() + "<u></b></p>"
            + "<p style='font-size : 10px;'>"
            + auxiliarAndamentoRais.getSomenteStringDoLog()
            + "</p>"
            + " </body>"
            + " </html>";
        String nome = "Log geração RAIS - " + selecionado;
        nome = nome.replace(" ", "_");
        Util.downloadPDF(nome, conteudo, FacesContext.getCurrentInstance());
    }

    public List<ContratoFP> buscarServidores(String parte) {
        HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacade.recuperaHierarquiaOrganizacional(selecionado.getEntidade());
        return arquivoRAISFacade.getContratosParaGeracaoRais(selecionado, ho, parte);
    }

    public void validarParametrosParaGeracao() {
        try {
            Util.validarCampos(selecionado);
            arquivoRAISFacade.jaExisteArquivoGeradoParaExercicio(selecionado);
            gerarRais();
        } catch (ExcecaoNegocioGenerica g) {
            FacesUtil.executaJavaScript("dialogArquivoJaExistente.show()");
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.atualizarComponente("form-arquivo-existente");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void gerarRais() {
        selecionado.setGeradoEm(new Date());
        selecionado.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        Web.poeNaSessao("RAIS", selecionado);
        FacesUtil.redirecionamentoInterno("/arquivo-rais/acompanhamento/");
    }

    public void selecionar(ActionEvent evento) {
        selecionado = (ArquivoRAIS) evento.getComponent().getAttributes().get("objeto");
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (Exercicio ex : this.exercicioFacade.listaExerciciosAtual()) {
            DateTime.Property year = new DateTime().year();
            Integer ano = Integer.parseInt(year.getAsText());

            if (ano.compareTo(ex.getAno()) >= 0) {
                lista.add(new SelectItem(ex, ex.toString()));
            }
        }
        return lista;
    }

    public ConverterGenerico getConverterExercicio() {

        return new ConverterGenerico(Exercicio.class, exercicioFacade);

    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        return contratoFPFacade.listaPessoasComContratosVigentesAno(parte.trim(), selecionado.getUltimoDia());
    }

    public Converter getConverterPessoaFisica() {
        if (converterPessoaFisica == null) {
            converterPessoaFisica = new ConverterAutoComplete(PessoaFisica.class, pessoaFacade);
        }
        return converterPessoaFisica;
    }

    public List<SelectItem> getIndicadoresRetificacao() {
        List<SelectItem> lista = new ArrayList<>();
        for (IndicadorRetificacao ir : IndicadorRetificacao.values()) {
            lista.add(new SelectItem(ir, ir.getDescricao()));
        }
        return lista;
    }

    public Boolean validaCampos() {
        boolean retorno = Boolean.TRUE;
        if (selecionado.getEntidade() == null) {
            retorno = Boolean.FALSE;
            FacesUtil.addOperacaoNaoPermitida("Informe a Entidade.");
        }
        if (selecionado.getEntidade().getPessoaJuridica() == null) {
            retorno = Boolean.FALSE;
            FacesUtil.addOperacaoNaoPermitida("A Entidade selecionada não possui Pessoa Jurídica.");
        }
        if (selecionado.getExercicio() == null || selecionado.getExercicio().getId() == null) {
            retorno = Boolean.FALSE;
            FacesUtil.addOperacaoNaoPermitida("Informe a Exercício.");
        }
        if (selecionado.getPessoaFisica() == null || selecionado.getPessoaFisica().getId() == null) {
            retorno = Boolean.FALSE;
            FacesUtil.addOperacaoNaoPermitida("Informe o Responsável.");
        }
        if (selecionado.getIndicadorRetificacao() == null) {
            retorno = Boolean.FALSE;
            FacesUtil.addOperacaoNaoPermitida("Informe o Indicador de Retificação.");
        }
        return retorno;
    }

    public StreamedContent fileDownload() throws FileNotFoundException, IOException {
        try {
            arquivo = new File("RAIS.txt");
            FileOutputStream fos = new FileOutputStream(arquivo);
            fos.write(selecionado.getConteudo().getBytes());
            fos.close();

            InputStream stream = new FileInputStream(arquivo);
            fileDownload = new DefaultStreamedContent(stream, "text/plain", "RAIS" + selecionado.getExercicio() + ".txt");
            arquivo = null;
            return fileDownload;
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String mensagemArquivoExistente() {
        String retorno = "Já existe um arquivo gerado para a entidade: <b>" + selecionado.getEntidade() + "</b> em <b>" + selecionado.getExercicio().getAno() + "</b>.";
        return retorno;
    }

    public ConverterAutoComplete getConverterEntidade() {
        return new ConverterAutoComplete(Entidade.class, entidadeFacade);
    }

    public List<SelectItem> recuperarEntidadesParaDeclaracao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Entidade e : entidadeFacade.buscarEntidadesParaDeclaracoes(CategoriaDeclaracaoPrestacaoContas.RAIS, selecionado.getPrimeiroDia(), selecionado.getUltimoDia())) {
            toReturn.add(new SelectItem(e, e.getNome() + " - " + e.getSigla()));
        }
        return toReturn;
    }

    public boolean orgaoEstaEmEstabelecimentoSelecionado(HierarquiaOrganizacional ho) {
        return entidadeFacade.entidadePossuiUnidadeParaDeclaracao(ho, selecionado.getEntidade(), CategoriaDeclaracaoPrestacaoContas.RAIS, selecionado.getPrimeiroDia(), selecionado.getUltimoDia());
    }

    public List<HierarquiaOrganizacional> orgaosVigentesNaDataDoSefip() {
        return hierarquiaOrganizacionalFacade.getHierarquiasVigentesNoPeriodoNoNivel(selecionado.getPrimeiroDia(), selecionado.getUltimoDia(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, 2);
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public void abortar() {
//        futureCabecalhoRodape.cancel(true);
        for (Future f : futuresConteudo) {
            f.cancel(true);
        }
        auxiliarAndamentoRais.pararProcessamento();
        FacesUtil.atualizarComponente("Formulario:panelGeral");
    }

    public void iniciarGeracaoRais() {
        try {
            logger.error("Começou a geração da RAIS");
            List<Long> itens;
            if (selecionado.isIndicadorRetifica() && !selecionado.getServidores().isEmpty()) {
                itens = selecionado.idsServidores();
            } else if (selecionado.isIndicadorRetifica()) {
                itens = arquivoRAISFacade.getContratosParaGeracaoRais(auxiliarAndamentoRais.getArquivoRAIS());
            } else {
                itens = arquivoRAISFacade.getContratosParaGeracaoRais(auxiliarAndamentoRais.getArquivoRAIS());
            }

            itens = new ArrayList(new HashSet(itens));

            auxiliarAndamentoRais.setTotal(itens.size());
            auxiliarAndamentoRais.setItens(itens);

            futuresConteudo = Lists.newArrayList();

            futureCabecalho = arquivoRAISFacade.getCabecalhoArquivoRais(auxiliarAndamentoRais);
            int partes = auxiliarAndamentoRais.getItens().size() > 50 ? (auxiliarAndamentoRais.getItens().size() / 4) : auxiliarAndamentoRais.getItens().size();
            List<List<Long>> idsParticionados = Lists.partition(auxiliarAndamentoRais.getItens(), partes);
            for (List<Long> ids : idsParticionados) {
                futuresConteudo.add(arquivoRAISFacade.getConteudoArquivoRais(auxiliarAndamentoRais, ids));
            }

        } catch (RuntimeException re) {
            logger.error(re.getMessage());
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), re.getMessage());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    @URLAction(mappingId = "logRais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void log() {
        auxiliarAndamentoRais = new AuxiliarAndamentoRais();
        auxiliarAndamentoRais.iniciarProcesso();
        auxiliarAndamentoRais.setTotal(1);
        auxiliarAndamentoRais.setCalculados(0);
        auxiliarAndamentoRais.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        auxiliarAndamentoRais.setArquivoRAIS((ArquivoRAIS) Web.pegaDaSessao("RAIS"));
        this.auxiliarAndamentoRais.getLog().add(Util.dateHourToString(new Date()) + " - RECUPERANDO SERVIDORES PARA GERAÇÃO DO ARQUIVO...<br/>");
        selecionado = auxiliarAndamentoRais.getArquivoRAIS();
        assistenteBarraProgresso = new AssistenteBarraProgresso();

        if (auxiliarAndamentoRais.getArquivoRAIS() == null) {
            FacesUtil.redirecionamentoInterno("/arquivo-rais/listar/");
        }
    }

    public void concluirGeracaoRais() {
        enumerarSequencias();
        auxiliarAndamentoRais.pararProcessamento();
        assistenteBarraProgresso = new AssistenteBarraProgresso();
        assistenteBarraProgresso.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        assistenteBarraProgresso.setDescricaoProcesso("Exportação do arquivo RAIS");
        futureGravarArquivo = arquivoRAISFacade.definirConteudoArquivo(assistenteBarraProgresso, selecionado, auxiliarAndamentoRais);
        FacesUtil.executaJavaScript("aguarde.hide()");
    }

    public void iniciarConclussaoArquivo() {
        FacesUtil.executaJavaScript("concluirGeracaoArquivo()");
    }

    public void verificarTermino() {
        if (futureGravarArquivo != null && futureGravarArquivo.isDone()) {
            salvarArquivoRais();
            FacesUtil.executaJavaScript("termina()");
            FacesUtil.atualizarComponente("Formulario");
        }
    }


    public void verificarTerminoGeracaoArquivo() {
        if ((futureCabecalho != null && futureCabecalho.isDone()) && (futureConteudoPronta())) {
            FacesUtil.executaJavaScript("terminaGeracaoArquivo()");
            FacesUtil.executaJavaScript("aguarde.show()");
            logger.error("Terminou a geração da RAIS");
        }
    }

    private boolean futureConteudoPronta() {
        if (futuresConteudo != null) {
            boolean terminou = true;
            for (Future<AuxiliarAndamentoRais> future : futuresConteudo) {
                if (!future.isDone()) {
                    terminou = false;
                }
            }
            return terminou;
        }
        return false;

    }

    private void salvarArquivoRais() {
        selecionado = arquivoRAISFacade.salvarRetornando(selecionado);
    }


    private void enumerarSequencias() {
        int sequenciaRegistro = 3;
        for (RegistroRAISTipo2 registroRAISTipo2 : auxiliarAndamentoRais.getRegistrosRAISTipo2()) {
            registroRAISTipo2.setSequenciaRegistro(sequenciaRegistro + "");
            sequenciaRegistro++;
        }
        Collections.sort(auxiliarAndamentoRais.getRegistrosRAISTipo2(), new Comparator<RegistroRAISTipo2>() {
            @Override
            public int compare(RegistroRAISTipo2 o1, RegistroRAISTipo2 o2) {
                return new Integer(o1.getSequenciaRegistro()).compareTo(new Integer(o2.getSequenciaRegistro()));
            }
        });
        auxiliarAndamentoRais.getRegistroRAISTipo9().setSequenciaRegistro(sequenciaRegistro + "");
        auxiliarAndamentoRais.getRegistroRAISTipo9().setTotalRegistrosTipo2(auxiliarAndamentoRais.getRegistrosRAISTipo2().size() + "");

    }

    public StreamedContent getFileDownload() {
        return fileDownload;
    }

    public void setFileDownload(StreamedContent fileDownload) {
        this.fileDownload = fileDownload;
    }

    public void adicionarServidorNaLista() {
        try {
            selecionado.adicionarServidorNaLista();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @Override
    public void cancelar() {
        Web.limpaNavegacao();
        super.cancelar();
    }
}
