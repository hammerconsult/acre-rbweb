/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteDetentorArquivoComposicao;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Heinz
 */
@ManagedBean(name = "parametrosITBIControle")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParametrosITBI", pattern = "/parametrosITBI/novo/", viewId = "/faces/tributario/itbi/parametrositbi/edita.xhtml"),
    @URLMapping(id = "editarParametrosITBI", pattern = "/parametrosITBI/editar/#{parametrosITBIControle.id}/", viewId = "/faces/tributario/itbi/parametrositbi/edita.xhtml"),
    @URLMapping(id = "listarParametrosITBI", pattern = "/parametrosITBI/listar/", viewId = "/faces/tributario/itbi/parametrositbi/lista.xhtml"),
    @URLMapping(id = "verParametrosITBI", pattern = "/parametrosITBI/ver/#{parametrosITBIControle.id}/", viewId = "/faces/tributario/itbi/parametrositbi/visualizar.xhtml")
})
public class ParametrosITBIControle extends PrettyControlador<ParametrosITBI> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ParametrosITBIControle.class);

    @EJB
    private ParametroITBIFacade parametroITBIFacade;
    private ConverterAutoComplete converterParametrosBCE;
    private ParametrosFuncionarios parametrosFuncionarios;
    @EJB
    private DividaFacade dividaFacede;
    @EJB
    private ExercicioFacade exercicioFacade;
    private ConverterExercicio converterExercicio;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FaixaValorParcelamentoFacade faixaValorParcelamentoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterPessoa;
    private ConverterGenerico converterFaixaValorParcelamento;
    private FaixaValorParcelamento faixa;
    private MoneyConverter moneyConverter;
    private AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ParametrosFuncionariosFacade parametrosFuncionariosFacade;
    private ConverterAutoComplete converterParametrosFuncionarios;
    private DefaultStreamedContent imagemAssinatura;
    private Boolean editandoFuncionario;
    private FuncaoParametrosITBI funcaoParametrosITBI;
    private ParametrosITBIDocumento documento;


    public ParametrosITBIControle() {
        super(ParametrosITBI.class);
    }

    public List<PessoaFisica> completaPessoa(String parte) {
        return contratoFPFacade.listaPessoasComContratosVigentes(parte);
    }

    public List<SelectItem> getSelectItemsTipoBaseCalculo() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoBaseCalculo object : TipoBaseCalculo.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getSelectItemsVerificarDebitosDoImovel() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (VerificarDebitosDoImovel object : VerificarDebitosDoImovel.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getSelectItemsVencimentoDolandoDeAvaliacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (VencimentoLaudoDeAvaliacao object : VencimentoLaudoDeAvaliacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getSelectItemsTipoITBI() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoITBI object : TipoITBI.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }


    public List<SelectItem> getTributos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Tributo object : parametroITBIFacade.getConfiguracaoTributarioFacade().getTributoFacade().tributosOrdenadosPorDescricao()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public boolean getEditandoFuncionario() {
        return editandoFuncionario != null ? editandoFuncionario : false;
    }

    public void setEditandoFuncionario(boolean editandoFuncionario) {
        this.editandoFuncionario = editandoFuncionario;
    }

    public void adicionarFuncionario() {
        try {
            validarFuncionario();
            parametrosFuncionarios.setParametros(selecionado);
            selecionado.getListaFuncionarios().add(parametrosFuncionarios);
            parametrosFuncionarios = new ParametrosFuncionarios();
            funcaoParametrosITBI = new FuncaoParametrosITBI();
            this.editandoFuncionario = false;
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        }
    }

    private void validarCamposObrigatoriosFuncionario() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(parametrosFuncionarios, ve);
        ve.lancarException();
    }

    private void validarFuncionario() {
        ValidacaoException ex = new ValidacaoException();
        try {
            validarCamposObrigatoriosFuncionario();
        } catch (ValidacaoException e) {
            ex = e;
        }
        if (selecionado.getFuncionario(parametrosFuncionarios.getPessoa()) != null) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("O funcionário " + parametrosFuncionarios.getPessoa().getNome() + " já foi adicionado.");
        }
        ex.lancarException();
    }

    public void removeItem(ActionEvent event) {
        ParametrosFuncionarios aRemover = (ParametrosFuncionarios) event.getComponent().getAttributes().get("remove");
        boolean remove = selecionado.getListaFuncionarios().remove(aRemover);
        if (!remove) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Não foi possível remover o funcionário da lista.");
        }
    }

    public void adicionarFaixaNaLista() {
        if (validarFaixaDeValor()) {
            faixa.setParametrosITBI(selecionado);
            selecionado.getListaDeFaixaValorParcelamento().add(faixa);
            faixa = new FaixaValorParcelamento();
        }
    }

    public void removerFaixa(ActionEvent event) {
        FaixaValorParcelamento aRemover = (FaixaValorParcelamento) event.getComponent().getAttributes().get("remove");
        boolean remove = selecionado.getListaDeFaixaValorParcelamento().remove(aRemover);
        if (!remove) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Não foi possível remover o funcionário da lista.");
        }
    }

    public boolean validarFaixaDeValor() {
        if (Util.validaCampos(faixa)) {
            boolean retorno = true;
            if (faixa.getValorInicial().compareTo(BigDecimal.ZERO) <= 0) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O campo Valor Inicial deve ser superior a zero (0).");
                retorno = false;
            }

            if (faixa.getValorFinal().compareTo(BigDecimal.ZERO) <= 0) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O campo Valor Final deve ser superior a zero (0).");
                retorno = false;
            }

            if (faixa.getValorFinal().compareTo(faixa.getValorInicial()) <= 0) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O campo Valor Final deve ser superior ao campo Valor Inicial.");
                retorno = false;
            }

            if (faixa.getQtdParcela() <= 0) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O campo Quantidade Máxima de Parcelas deve ser superior a zero (0).");
                retorno = false;
            }

            if (selecionado.faixaEstaContidaEmOutraFaixa(faixa)) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Os valores informados para esta faixa estão contidos em outra faixa já informada.");
                retorno = false;
            }

            if (selecionado.faixaContemOutraFaixa(faixa)) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Os valores informados para esta faixa irão conter outra faixa já informada.");
                retorno = false;
            }
            return retorno;
        } else {
            return false;
        }
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    private boolean validaCampos() {
        boolean retorno = true;

        if (selecionado.getExercicio() != null && selecionado.getTipoITBI() != null) {
            ParametrosITBI parametroVigente = parametroITBIFacade.getParametroVigente(selecionado.getExercicio(), selecionado.getTipoITBI());
            if (parametroVigente != null && !parametroVigente.getId().equals(selecionado.getId())) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Já existe um Parâmetro de ITBI do tipo " + selecionado.getTipoITBI().getDescricao() + " cadastrado para o exercício de " + selecionado.getExercicio().getAno() + ".");
                return false;
            }
        }

        if (selecionado.getPercentualReajuste() != null && selecionado.getPercentualReajuste().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O campo Percentual Reajuste (%) deve ser maior que zero.");
            retorno = false;
        }

        if (selecionado.getDiaFixoVencimento() != null && selecionado.getDiaFixoVencimento() <= 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O campo Vencimento da Primeira Parcela (em Dias a partir da data de lançamento) deve ser maior que zero.");
            retorno = false;
        }

        if (selecionado.getVencLaudoAvaliacaoEmDias() != null && selecionado.getVencLaudoAvaliacaoEmDias() <= 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O campo Vencimento do Laudo de Avaliação (em Dias) deve ser maior que zero.");
            retorno = false;
        }

        if (selecionado.getListaDeFaixaValorParcelamento().isEmpty()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Informe pelo menos uma Faixa de Valor de Parcelamento.");
            retorno = false;
        }

        if (selecionado.getListaFuncionarios().isEmpty()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Informe pelo menos um Funcionário.");
            retorno = false;
        } else {
            if (selecionado.getPrimeiroFuncionarioVigente(sistemaFacade.getDataOperacao()) == null) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Informe pelo menos um Funcionário vigente.");
                retorno = false;
            }
        }

        return retorno;
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    @Override
    public AbstractFacade getFacede() {
        return parametroITBIFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parametrosITBI/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoParametrosITBI", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarAtributos();
    }

    private void inicializarAtributos() {
        this.parametrosFuncionarios = new ParametrosFuncionarios();
        this.funcaoParametrosITBI = new FuncaoParametrosITBI();
        this.faixa = new FaixaValorParcelamento();
        this.editandoFuncionario = false;
        assistenteDetentorArquivoComposicao = new AssistenteDetentorArquivoComposicao(parametrosFuncionarios, sistemaFacade.getDataOperacao());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-assinatura-digital", null);
        criarNovoDocumento();
    }

    @URLAction(mappingId = "verParametrosITBI", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarParametrosITBI", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializarAtributos();
    }

    public ParametrosFuncionarios getParametrosFuncionarios() {
        return parametrosFuncionarios;
    }

    public void setParametrosFuncionarios(ParametrosFuncionarios parametrosFuncionarios) {
        this.parametrosFuncionarios = parametrosFuncionarios;
    }

    public FuncaoParametrosITBI getFuncaoParametrosITBI() {
        return funcaoParametrosITBI;
    }

    public void setFuncaoParametrosITBI(FuncaoParametrosITBI funcaoParametrosITBI) {
        this.funcaoParametrosITBI = funcaoParametrosITBI;
    }

    public ParametrosITBIDocumento getDocumento() {
        return documento;
    }

    public void setDocumento(ParametrosITBIDocumento documento) {
        this.documento = documento;
    }

    public ConverterExercicio getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(exercicioFacade);
        }
        return converterExercicio;
    }

    public Converter getConverterFaixaValorParcelamento() {
        if (converterFaixaValorParcelamento == null) {
            converterFaixaValorParcelamento = new ConverterGenerico(FaixaValorParcelamento.class, faixaValorParcelamentoFacade);
        }
        return converterFaixaValorParcelamento;
    }

    public Converter getConverterParametrosBCE() {
        if (converterParametrosBCE == null) {
            converterParametrosBCE = new ConverterAutoComplete(ParametrosITBI.class, parametroITBIFacade);
        }
        return converterParametrosBCE;
    }

    public Converter getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, pessoaFisicaFacade);
        }
        return converterPessoa;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public void setExercicioFacade(ExercicioFacade exercicioFacade) {
        this.exercicioFacade = exercicioFacade;
    }

    public FaixaValorParcelamento getFaixa() {
        return faixa;
    }

    public void setFaixa(FaixaValorParcelamento faixa) {
        this.faixa = faixa;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public void editarParametroFuncionario(ParametrosFuncionarios funcionarios) {
        if (funcionarios != null) {
            this.parametrosFuncionarios = (ParametrosFuncionarios) Util.clonarObjeto(funcionarios);
            if (parametrosFuncionarios.getDetentorArquivoComposicao() != null) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                for (ArquivoParte parte : parametrosFuncionarios.getDetentorArquivoComposicao().getArquivosComposicao().get(0).getArquivo().getPartes()) {
                    try {
                        buffer.write(parte.getDados());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                String nomeArquivo = parametrosFuncionarios.getDetentorArquivoComposicao().getArquivosComposicao().get(0).getArquivo().getNome();
                imagemAssinatura = new DefaultStreamedContent(new ByteArrayInputStream(buffer.toByteArray()), "image/png/jpg", nomeArquivo);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-assinatura-digital", imagemAssinatura);
            }
            if (this.parametrosFuncionarios != null) {
                this.funcaoParametrosITBI = (FuncaoParametrosITBI) Util.clonarObjeto(this.parametrosFuncionarios.getFuncaoParametrosITBI());
            }
            this.editandoFuncionario = true;
        }
    }

    public void salvarEdicaoParametroFuncionarios() {
        try {
            validarCamposObrigatoriosFuncionario();
            parametrosFuncionarios.setFuncaoParametrosITBI(funcaoParametrosITBI);
            this.editandoFuncionario = false;
            Util.adicionarObjetoEmLista(selecionado.getListaFuncionarios(), parametrosFuncionarios);
            parametrosFuncionarios = new ParametrosFuncionarios();
            funcaoParametrosITBI = new FuncaoParametrosITBI();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarEdicaoParametroFuncionarios() {
        this.parametrosFuncionarios = new ParametrosFuncionarios();
        this.funcaoParametrosITBI = new FuncaoParametrosITBI();
        this.editandoFuncionario = false;
    }

    public void encerraVigencia(ParametrosFuncionarios funcionarios) {
        funcionarios.setVigenciaFinal(new Date());
    }

    public AssistenteDetentorArquivoComposicao getAssistenteDetentorArquivoComposicao() {
        return assistenteDetentorArquivoComposicao;
    }

    public void setAssistenteDetentorArquivoComposicao(AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao) {
        this.assistenteDetentorArquivoComposicao = assistenteDetentorArquivoComposicao;
    }

    public void uploadImagemAssinatura(FileUploadEvent event) {
        try {
            assistenteDetentorArquivoComposicao = new AssistenteDetentorArquivoComposicao(parametrosFuncionarios, sistemaFacade.getDataOperacao());
            byte[] bytes = new byte[1000000];
            event.getFile().getInputstream().read(bytes);
            InputStream inputStream = new ByteArrayInputStream(bytes);
            imagemAssinatura = new DefaultStreamedContent(inputStream, "image/png/jpg", event.getFile().getFileName());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-assinatura-digital", imagemAssinatura);
            assistenteDetentorArquivoComposicao.removerArquivos();
            assistenteDetentorArquivoComposicao.handleFileUpload(event);
        } catch (IOException ex) {
            logger.error("Erro: ", ex);
        } catch (Exception e) {
            FacesUtil.addError("Erro", e.getMessage());
        }
    }

    public DefaultStreamedContent getImagemAssinatura() {
        return imagemAssinatura;
    }

    public ConverterAutoComplete getConverterParametrosFuncionarios() {
        if (converterParametrosFuncionarios == null) {
            converterParametrosFuncionarios = new ConverterAutoComplete(ParametrosFuncionarios.class, parametrosFuncionariosFacade);
        }
        return converterParametrosFuncionarios;
    }

    public List<FuncaoParametrosITBI> buscarFuncoesParametroITBI(String parte) {
        return parametroITBIFacade.getFuncaoParametroITBIFacade().buscarFuncoes(parte);
    }

    public void adicionarDocumento() {
        try {
            validarDocumento();
            documento.setParametrosITBI(selecionado);
            selecionado.getDocumentos().add(documento);
            criarNovoDocumento();
        } catch (ValidacaoException va) {
            FacesUtil.printAllFacesMessages(va.getMensagens());
        }
    }

    public void validarDocumento() {
        ValidacaoException exception = new ValidacaoException();
        Util.validarCampos(documento);
        for (ParametrosITBIDocumento parametrosITBIDocumento : selecionado.getDocumentos()) {
            if (parametrosITBIDocumento.getNaturezaDocumento().equals(documento.getNaturezaDocumento()) &&
                parametrosITBIDocumento.getDescricao().toLowerCase().trim().equals(documento.getDescricao().trim().toLowerCase())) {
                exception.adicionarMensagemDeOperacaoNaoPermitida("Já existe um documento adicionado com essa mesma descrição");
            }
        }
        if (exception.temMensagens()) {
            throw exception;
        }
    }

    private void criarNovoDocumento() {
        documento = new ParametrosITBIDocumento();
    }

    public void removerDocumento(ParametrosITBIDocumento documento) {
        selecionado.getDocumentos().remove(documento);
    }

    public void editarDocumento(ParametrosITBIDocumento documento) {
        this.documento = documento;
        removerDocumento(this.documento);
    }

    public List<SelectItem> getNaturezasDocumento() {
        return Util.getListSelectItem(ParametrosITBIDocumento.NaturezaDocumento.values());
    }
}
