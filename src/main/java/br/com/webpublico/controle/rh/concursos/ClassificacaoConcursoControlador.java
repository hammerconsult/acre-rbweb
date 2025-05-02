package br.com.webpublico.controle.rh.concursos;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.concursos.*;
import br.com.webpublico.entidadesauxiliares.contabil.BarraProgressoAssistente;
import br.com.webpublico.enums.rh.concursos.MetodoAvaliacao;
import br.com.webpublico.enums.rh.concursos.StatusClassificacaoInscricao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.concursos.*;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean(name = "classificacaoConcursoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-classificacao-concurso", pattern = "/concursos/classificacao/novo/", viewId = "/faces/rh/concursos/classificacao/edita.xhtml"),
    @URLMapping(id = "editar-classificacao-concurso", pattern = "/concursos/classificacao/editar/#{classificacaoConcursoControlador.id}/", viewId = "/faces/rh/concursos/classificacao/edita.xhtml"),
    @URLMapping(id = "importacao-simples", pattern = "/concursos/importacao/", viewId = "/faces/rh/concursos/classificacao/importacao.xhtml"),
    @URLMapping(id = "ver-classificacao-concurso", pattern = "/concursos/classificacao/ver/#{classificacaoConcursoControlador.id}/", viewId = "/faces/rh/concursos/classificacao/edita.xhtml"),
    @URLMapping(id = "listar-classificacao-concurso", pattern = "/concursos/classificacao/listar/", viewId = "/faces/rh/concursos/classificacao/lista.xhtml")
})
public class ClassificacaoConcursoControlador extends PrettyControlador<ClassificacaoConcurso> implements Serializable, CRUD {

    protected static final Logger logger = LoggerFactory.getLogger(ClassificacaoConcursoControlador.class);
    @EJB
    private ClassificacaoConcursoFacade classificacaoConcursoFacade;
    @EJB
    private ConcursoFacade concursoFacade;
    @EJB
    private CargoConcursoFacade cargoConcursoFacade;
    @EJB
    private FaseConcursoFacade faseConcursoFacade;
    @EJB
    private ProvaConcursoFacade provaConcursoFacade;
    @EJB
    private InscricaoConcursoFacade inscricaoConcursoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private Concurso concursoSelecionado;
    private BarraProgressoAssistente barraProgressoAssistente;
    private Future<CargoConcurso> futureCargoConcurso;
    private List<ClassificacaoInscricao> inscricoesNaoAtualizadas;
    private List<ClassificacaoInscricao> classificacoes;

    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;


    public Concurso getConcursoSelecionado() {
        return concursoSelecionado;
    }

    public void setConcursoSelecionado(Concurso concursoSelecionado) {
        this.concursoSelecionado = concursoSelecionado;
    }

    public ClassificacaoConcursoControlador() {
        super(ClassificacaoConcurso.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return classificacaoConcursoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/concursos/classificacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-classificacao-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inscricoesNaoAtualizadas = Lists.newLinkedList();
        classificacoes = Lists.newLinkedList();
    }

    @URLAction(mappingId = "importacao-simples", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoImportacaoSimples() {
        novo();
    }

    private void instanciarAssistente() {
        barraProgressoAssistente = new BarraProgressoAssistente();
    }

    @URLAction(mappingId = "ver-classificacao-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        carregarParametrosIniciais();
    }

    @URLAction(mappingId = "editar-classificacao-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        carregarParametrosIniciais();
    }

    private void carregarParametrosIniciais() {
        concursoSelecionado = concursoFacade.recuperarConcursoComCriteriosDeDesempate(selecionado.getCargo().getConcurso().getId());
        Collections.sort(selecionado.getCargo().getClassificacaoConcurso().getInscricoes());
    }

    public ConverterAutoComplete getConverterCargoConcurso() {
        return new ConverterAutoComplete(CargoConcurso.class, cargoConcursoFacade);
    }

    public Converter getConverterConcurso() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {

                try {
                    return concursoFacade.buscarConcursosComCargosAndInscricoesAndCriteriosDesempate(Long.parseLong(value));
                } catch (Exception ex) {
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                String resultado = null;
                if (value == null) {
                    return null;
                } else {
                    if (value instanceof Long) {
                        resultado = String.valueOf(value);
                    } else {
                        try {
                            return "" + ((Concurso) value).getId();
                        } catch (Exception e) {
                            resultado = String.valueOf(value);
                        }
                    }
                }
                return resultado;
            }
        };
    }

    public List<SelectItem> getConcursos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, "Selecione um Concurso..."));
        for (Concurso c : concursoFacade.getUltimosConcursosQuePossuemFases()) {
            toReturn.add(new SelectItem(c, "" + c));
        }
        return toReturn;
    }

    public List<SelectItem> getCargos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        if (concursoSelecionado == null) {
            toReturn.add(new SelectItem(null, ""));
            return toReturn;
        }
        toReturn.add(new SelectItem(null, "Selecione um Cargo..."));
        for (CargoConcurso c : concursoSelecionado.getCargos()) {
            toReturn.add(new SelectItem(c, "" + c));
        }
        return toReturn;
    }

    public void limparCargo() {
        selecionado.setCargo(null);
    }

    private void validarCamposObrigatorios() {
        ValidacaoException ve = new ValidacaoException();
        if (concursoSelecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Concurso deve ser informado.");
        }
        if (selecionado.getCargo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Cargo deve ser informado.");
        }
        ve.lancarException();
    }

    public void gerarOrAtualizarClassificacao() {
        try {
            validarCamposObrigatorios();
            selecionado.setCargo(classificacaoConcursoFacade.gerarOrAtualizarClassificacaoDesteCargo(selecionado.getCargo()));
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica eng) {
            FacesUtil.addOperacaoNaoRealizada(eng.getMessage());
        }
    }

    public String recuperarBigDecimalNoPadraoDasNotas(BigDecimal media) {
        DecimalFormat formato = new DecimalFormat("#0.00");
        formato.setMaximumFractionDigits(2);
        DecimalFormatSymbols padrao = new DecimalFormatSymbols();
        padrao.setDecimalSeparator('.');
        formato.setDecimalFormatSymbols(padrao);
        return formato.format(media);
    }

    public List<ProvaConcurso> buscarProvasAvaliadasDoConcursoAndCargo() {
        if (concursoSelecionado == null || selecionado.getCargo() == null) {
            return null;
        }
        return provaConcursoFacade.buscarProvasDoConcursoAndCargo(concursoSelecionado, selecionado.getCargo());
    }

    public String buscarNotaParaApresentacao(InscricaoConcurso inscricaoConcurso, ProvaConcurso prova) {
        BigDecimal nota = provaConcursoFacade.buscarNotaDoCanditadoNaProva(inscricaoConcurso, prova);
        if (prova.getMetodoAvaliacao().equals(MetodoAvaliacao.POR_OBJETIVO)) {
            return nota.compareTo(BigDecimal.TEN) == 0 ? "<font style='color : green;'>APROVADO</font>" : "<font style='color : #cd0a0a;'>NÃO APROVADO</font>";
        }
        return recuperarBigDecimalNoPadraoDasNotas(nota);
    }

    public String recuperarStatusParaApresentacao(ClassificacaoInscricao classificacaoInscricao) {
        if (classificacaoInscricao.getStatus() == null) {
            return "";
        }
        switch (classificacaoInscricao.getStatus()) {
            case CLASSIFICADO:
                return "<font style='color : green;'>" + StatusClassificacaoInscricao.CLASSIFICADO.getDescricao() + "</font>";
            case FILA_DE_ESPERA:
                return "<font style='color : blue;'>" + StatusClassificacaoInscricao.FILA_DE_ESPERA.getDescricao() + "</font>";
            case DESCLASSIFICADO:
                return "<font style='color : #cd0a0a;'>" + StatusClassificacaoInscricao.DESCLASSIFICADO.getDescricao() + "</font>";
        }
        return "";
    }

    public String converterBooleanParaSimOuNao(Boolean valor) {
        return valor == null ? "Não" : valor ? "Sim" : "Não";
    }

    public String recuperarCorDoTexto(Boolean valor) {
        return valor == null ? "Não" : valor ? "verde" : "vermelho-escuro";
    }

    public String recuperarIdadePorExtenso(ClassificacaoInscricao classificacaoInscricao) {
        ObjetoData objData = DataUtil.getAnosMesesDias(classificacaoInscricao.getInscricaoConcurso().getDataNascimento(), UtilRH.getDataOperacao());
        return "\"" + objData.getAnos() + " ano(s), " + objData.getMeses() + " mes(es) e " + objData.getDias() + " dia(s).\"";
    }

    public void buscarClassificacaoJaExistente() {
        if (selecionado.getCargo().getClassificacaoConcurso() == null) {
            gerarOrAtualizarClassificacao();
            return;
        }
        selecionado = classificacaoConcursoFacade.buscarClassificacaoComClassificados(selecionado.getCargo().getClassificacaoConcurso().getId());
        Collections.sort(selecionado.getInscricoes());
    }

    public void importar(FileUploadEvent event) {
        try {
            UploadedFile file = event.getFile();
            org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(file.getInputstream());

            instanciarAssistente();
            if (selecionado.getCargo().getClassificacaoConcurso() == null) {
                selecionado.setCargo(classificacaoConcursoFacade.criarNovaClassificacaoParaOcargo(selecionado.getCargo()));
            } else {
                selecionado.getCargo().setClassificacaoConcurso(classificacaoConcursoFacade.recuperar(selecionado.getCargo().getClassificacaoConcurso().getId()));
            }
            selecionado.getCargo().setConcurso(concursoFacade.recuperar(selecionado.getCargo().getConcurso().getId()));

            futureCargoConcurso = classificacaoConcursoFacade.importarClassificacoes(selecionado.getCargo(), barraProgressoAssistente, workbook);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("erro: ", ex);
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao importar as classificações: " + ex.getMessage());
        }

    }

    public void importarPessoasSemConcurso(FileUploadEvent event) {
        try {
            UploadedFile file = event.getFile();
            org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(file.getInputstream());
            instanciarAssistente();
            classificacaoConcursoFacade.importarClassificacoesSemConcurso(classificacoes, barraProgressoAssistente, workbook);
            barraProgressoAssistente.finaliza();
            FacesUtil.atualizarComponente("Formulario");
            FacesUtil.executaJavaScript("dialogBarraProgresso.hide()");
            FacesUtil.executaJavaScript("pollClassificacao.stop()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("erro: ", ex.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao importar as classificações: " + ex.getMessage());
        }

    }


    private List<String[]> buscarClassificacoes(FileUploadEvent event) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(event.getFile().getInputstream());
        BufferedReader reader = new BufferedReader(streamReader);
        String line;
        String separador = ",";
        List<String[]> retorno = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            retorno.add(line.split(separador));
        }
        return retorno;
    }


    public void consultarProcesso() {
        if (futureCargoConcurso != null && futureCargoConcurso.isDone()) {
            barraProgressoAssistente.finaliza();
            selecionado = classificacaoConcursoFacade.getCargoConcurso().getClassificacaoConcurso();
            Collections.sort(selecionado.getInscricoes());
            FacesUtil.atualizarComponente("Formulario");
            FacesUtil.executaJavaScript("dialogBarraProgresso.hide()");
            FacesUtil.executaJavaScript("pollClassificacao.stop()");
        }
    }

    public void imprimirLogErros() {
        try {
            String conteudo = getCabecalhoHTMLParaLogsPDF() + getComplementoConteudoHTMLParaLogPDF();
            Util.geraPDF("Inscrições Inválidas", conteudo, FacesContext.getCurrentInstance());
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
            + " <td><img src='" + FacesUtil.geraUrlImagemDir() + "/img/escudo.png" + "' alt='Smiley face' height='80' width='75' /></td>   "
            + " <td><b> PREFEITURA MUNICIPAL DE RIO BRANCO <br/>\n"
            + "         MUNICÍPIO DE RIO BRANCO<br/>\n"
            + "         LOG DE IMPORTAÇÂO DE CLASSIFICAÇÕES </b></td>\n"
            + "</tr>" + "</table>"
            + "</div>\n"

            + "<div style='border: 1px solid black;text-align: left; margin-top: -1px; padding : 3px;'>\n";
    }

    private String getComplementoConteudoHTMLParaLogPDF() {
        String conteudo = "<div style='text-align: center'>"
            + "<b>Mensagens de Erro </b>"
            + "</div>\n"
            + getMensagensLog()
            + "</div>"
            + " </body>"
            + " </html>";
        return conteudo;
    }

    private String getMensagensLog() {
        String retorno = "";
        for (String mensagemErro : barraProgressoAssistente.getMensagensComErro()) {
            retorno += mensagemErro + "<br/>";
        }
        return retorno;
    }

    @Override
    public void salvar() {
        try {
            validarSelecionado();
            for (ClassificacaoInscricao ci : selecionado.getInscricoes()) {
                ci.setInscricaoConcurso(inscricaoConcursoFacade.salvarRetornando(ci.getInscricaoConcurso()));
            }
            getFacede().salvar(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarSelecionado() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getInscricoes().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário haver uma classificação para poder salvar. Verifique as informações e tente novamente.");
        }
        ve.lancarException();
    }

    public BarraProgressoAssistente getBarraProgressoAssistente() {
        return barraProgressoAssistente;
    }

    public void setBarraProgressoAssistente(BarraProgressoAssistente barraProgressoAssistente) {
        this.barraProgressoAssistente = barraProgressoAssistente;
    }

    public Future<CargoConcurso> getFutureCargoConcurso() {
        return futureCargoConcurso;
    }

    public void setFutureCargoConcurso(Future<CargoConcurso> futureCargoConcurso) {
        this.futureCargoConcurso = futureCargoConcurso;
    }

    public List<ClassificacaoInscricao> getInscricoesNaoAtualizadas() {
        return inscricoesNaoAtualizadas;
    }

    public void setInscricoesNaoAtualizadas(List<ClassificacaoInscricao> inscricoesNaoAtualizadas) {
        this.inscricoesNaoAtualizadas = inscricoesNaoAtualizadas;
    }

    public List<ClassificacaoInscricao> getClassificacoes() {
        return classificacoes;
    }

    public void setClassificacoes(List<ClassificacaoInscricao> classificacoes) {
        this.classificacoes = classificacoes;
    }

    public List<CampoImportacao> getCampoImportacoes() {
        return Arrays.asList(CampoImportacao.values());
    }

    public void atualizarDadosPessoais() {
        List<ClassificacaoInscricao> inscricoesNaoAtualizadas = pessoaFisicaFacade.atualizarDadosPessoaisViaConcurso(selecionado);
        this.inscricoesNaoAtualizadas = inscricoesNaoAtualizadas;
        ValidacaoException val = new ValidacaoException();
        if (!inscricoesNaoAtualizadas.isEmpty()) {

            for (ClassificacaoInscricao inscricoesNaoAtualizada : this.inscricoesNaoAtualizadas) {
                logger.debug("{}", inscricoesNaoAtualizada);
                val.adicionarMensagemDeOperacaoNaoRealizada(inscricoesNaoAtualizada.toString());
            }
            FacesUtil.printAllFacesMessages(val.getAllMensagens());
        }

    }

    public void atualizarDadosPessoaisViaImportacaoSimples() {
        if (classificacoes != null && !classificacoes.isEmpty()) {
            List<ClassificacaoInscricao> inscricoesNaoAtualizadas = pessoaFisicaFacade.atualizarDadosPessoaisViaConcursoImportacaoSimples(classificacoes);
            this.inscricoesNaoAtualizadas = inscricoesNaoAtualizadas;
            if (!inscricoesNaoAtualizadas.isEmpty()) {
                for (ClassificacaoInscricao inscricoesNaoAtualizada : this.inscricoesNaoAtualizadas) {
                    logger.debug("{}", inscricoesNaoAtualizada);
                }
            }
        }

    }

    public enum CampoImportacao {
        INSCRICAO("Inscrição"),
        SEXO("Sexo"),
        NOME("Nome"),
        NOME_MAE("Nome da Mãe"),
        IDENTIDADE("Identidade"),
        CPF("CPF"),
        NASCIMENTO("Nascimento"),
        ENDERECO("Endereço"),
        CEP("CEP"),
        TELEFONE("Telefone"),
        CELULAR("Celular"),
        DEFICIENTE_FISICO("Deficiente Físico"),
        RACA("Raça"),
        EMAIL("Email"),
        POSICAO("Posicação"),
        STATUS("Status");

        private String descricao;

        CampoImportacao(String descricao) {
            this.descricao = descricao;
        }

        CampoImportacao() {
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public class OrdemCampo {
        private Integer ordem;
        private CampoImportacao campoImportacao;

        public Integer getOrdem() {
            return ordem;
        }

        public void setOrdem(Integer ordem) {
            this.ordem = ordem;
        }

        public CampoImportacao getCampoImportacao() {
            return campoImportacao;
        }

        public void setCampoImportacao(CampoImportacao campoImportacao) {
            this.campoImportacao = campoImportacao;
        }
    }
}
