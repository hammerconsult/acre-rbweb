package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Claudio
 * Date: 19/07/13
 * Time: 14:39
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "provimentoReversaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoProvimentoReversao", pattern = "/provimentoreversao/novo/", viewId = "/faces/rh/administracaodepagamento/provimentoreversao/edita.xhtml"),
    @URLMapping(id = "editarProvimentoReversao", pattern = "/provimentoreversao/editar/#{provimentoReversaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/provimentoreversao/edita.xhtml"),
    @URLMapping(id = "listarProvimentoReversao", pattern = "/provimentoreversao/listar/", viewId = "/faces/rh/administracaodepagamento/provimentoreversao/lista.xhtml"),
    @URLMapping(id = "verProvimentoReversao", pattern = "/provimentoreversao/ver/#{provimentoReversaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/provimentoreversao/visualizar.xhtml")
})
public class ProvimentoReversaoControlador extends PrettyControlador<ProvimentoReversao> implements Serializable, CRUD {

    @EJB
    private ProvimentoReversaoFacade provimentoReversaoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterAposentadoria;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    private ConverterAutoComplete converterAtoLegal;
    private Arquivo arquivo;
    private Arquivo marcadoRemover;
    private FileUploadEvent fileUploadEvent;
    private UploadedFile file;
    private UploadedFile fileSelecionado;
    private StreamedContent fileDownload;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    @EJB
    private SindicatoVinculoFPFacade sindicatoVinculoFPFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private AssociacaoVinculoFPFacade associacaoVinculoFPFacade;
    @EJB
    private TipoAposentadoriaFacade tipoAposentadoriaFacade;
    private ContratoFP novoContratoFP;

    public ProvimentoReversaoControlador() {
        super(ProvimentoReversao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return provimentoReversaoFacade;
    }

    public ContratoFP getNovoContratoFP() {
        return novoContratoFP;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public UploadedFile getFileSelecionado() {
        return fileSelecionado;
    }

    public void setFileSelecionado(UploadedFile fileSelecionado) {
        this.fileSelecionado = fileSelecionado;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public StreamedContent getFileDownload() {
        return fileDownload;
    }

    public void setFileDownload(StreamedContent fileDownload) {
        this.fileDownload = fileDownload;
    }

    @URLAction(mappingId = "novoProvimentoReversao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        fileUploadEvent = null;
        arquivo = new Arquivo();
        marcadoRemover = null;
        fileDownload = null;
    }

    @URLAction(mappingId = "verProvimentoReversao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        novoContratoFP = contratoFPFacade.recuperar(selecionado.getNovoContratoFP().getId());
        recuperaTipoAposentadoria();
        arquivo = new Arquivo();
        marcadoRemover = null;
        fileUploadEvent = null;
        fileDownload = null;
        if (selecionado.getLaudoProvimentoReversao().getArquivo() != null) {
            carregaArquivo();
        }
    }

    @URLAction(mappingId = "editarProvimentoReversao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        novoContratoFP = contratoFPFacade.recuperar(selecionado.getNovoContratoFP().getId());
        recuperaTipoAposentadoria();
        arquivo = new Arquivo();
        marcadoRemover = null;
        fileUploadEvent = null;
        fileDownload = null;
        if (selecionado.getLaudoProvimentoReversao().getArquivo() != null) {
            carregaArquivo();
        }
    }

    @Override
    public void excluir() {
        if (contratoFPFacade.podeExcluir(novoContratoFP, new Date())) {
            try {
                excluirRevertendoProvimentoReversao(selecionado, novoContratoFP);
            } catch (ExcecaoNegocioGenerica e) {
                FacesUtil.addError("", "Houve um erro ao tentar excluir o Provimento de Reversão. Entre em contato com o Administrador.");
            }
        } else {
            FacesUtil.addWarn("Atenção!", "Este registro não pode ser excluído pois já existe cálculo para o contrato.");
        }

    }

    public void excluirRevertendoProvimentoReversao(ProvimentoReversao provimentoReversao, ContratoFP contratoFP) throws ExcecaoNegocioGenerica {

        ContratoFP ultimoContratoFP = contratoFPFacade.recuperar(provimentoReversao.getAposentadoria().getContratoFP().getId());
        Aposentadoria ap = aposentadoriaFacade.recuperar(provimentoReversao.getAposentadoria().getId());

        //remove provimentoFP gerado para o novo contrato
        TipoProvimento tipoProvimento = tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.REVERSAO);

        ProvimentoFP provimentoFP = provimentoFPFacade.recuperaProvimento(contratoFP, tipoProvimento);
        provimentoFP.setVinculoFP(null);
        provimentoFPFacade.salvar(provimentoFP);
        provimentoFP = provimentoFPFacade.recuperar(provimentoFP.getId());

        contratoFP.setProvimentoFP(null);
        contratoFPFacade.salvar(contratoFP);
        contratoFP = contratoFPFacade.recuperar(contratoFP.getId());

        provimentoFPFacade.remover(provimentoFP);
        provimentoReversaoFacade.remover(provimentoReversao);
        contratoFPFacade.remover(contratoFP);

        //volta a vigencia da aposentadoria
        ap.setFinalVigencia(null);
        aposentadoriaFacade.salvar(ap);

        //volta fim da vigencia (null) do ultimo contrato
        ultimoContratoFP.setFinalVigencia(null);
        contratoFPFacade.salvar(ultimoContratoFP);

        FacesUtil.addInfo("", "Registro excluído com sucesso!");
        redireciona();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            if (operacao == Operacoes.NOVO) {
                try {
                    provimentoReversaoFacade.salvarNovo((ProvimentoReversao) selecionado, arquivo, fileUploadEvent);

                    if (selecionado.getId() != null) {
                        provimentoReversaoFacade.recuperar(selecionado.getId());
                    }

                    novoContratoFP = geraNovoContratoFP(selecionado);
                    geraNovoProvimentoFP(novoContratoFP);
                    associaNovoContratoAReversao(selecionado, novoContratoFP);

                    //encerra vigencia da aposentadoria
                    Aposentadoria aposentadoria = aposentadoriaFacade.recuperar(selecionado.getAposentadoria().getId());
                    aposentadoria.setFinalVigencia(new Date());
                    aposentadoriaFacade.salvar(aposentadoria);

                } catch (ExcecaoNegocioGenerica e) {
                    FacesUtil.addError("Entre em contato com o Administrador.", "Ocorreu um erro ao tentar salvar o provimento de reversão.");
                    return;
                }


            } else {
                provimentoReversaoFacade.salvar(selecionado, arquivo, fileUploadEvent, marcadoRemover);
            }

            selecionado = provimentoReversaoFacade.recuperarProvimentoVigente(selecionado.getAposentadoria());

            RequestContext.getCurrentInstance().update("FormularioDialog");
            RequestContext.getCurrentInstance().execute("dialogo.show();");

            FacesUtil.addInfo("", "Salvo com sucesso!");
        }
    }


    public boolean validaCampos() {

        boolean valido = Util.validaCampos(selecionado);
        if (valido) {
            if (selecionado.getFinalVigencia() != null && selecionado.getFinalVigencia().before(selecionado.getInicioVigencia())) {
                FacesUtil.addError("", "O Final de Vigência não pode ser anterior ao Início de Vigência!");
                valido = false;
            }

            if (selecionado.getTipoAposentadoria().getCodigo().equals("3")) {
                if (selecionado.getLaudoProvimentoReversao().getDataLaudo() == null) {
                    FacesUtil.addError("", "O campo Data do Laudo é obrigatório!");
                    valido = false;
                }
                if (selecionado.getLaudoProvimentoReversao().getDescricao() == null || selecionado.getLaudoProvimentoReversao().getDescricao().trim().isEmpty()) {
                    FacesUtil.addError("", "O campo Descrição do Laudo é obrigatório!");
                    valido = false;
                }
                if (file == null) {
                    FacesUtil.addError("", "O Arquivo do laudo é obrigatorio!");
                    valido = false;
                }

            } else {
                if (selecionado.getLaudoProvimentoReversao().getMotivo() == null || selecionado.getLaudoProvimentoReversao().getMotivo().trim().isEmpty()) {
                    FacesUtil.addError("", "O campo Motivo é obrigatório!");
                    valido = false;
                }
                if (arquivo == null) {
                    FacesUtil.addError("", "O Arquivo referente ao motivo é obrigatorio!");
                    valido = false;
                }
            }
        }

        return valido;
    }


    public List<Aposentadoria> completaAposentadoria(String parte) {
        return aposentadoriaFacade.recuperaAposentadoSemReversaoFiltrandoPessoaFisica(parte.trim());
    }

    public Converter getConverterAposentadoria() {
        if (converterAposentadoria == null) {
            converterAposentadoria = new ConverterAutoComplete(Aposentadoria.class, aposentadoriaFacade);
        }
        return converterAposentadoria;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoAtoLegal(parte.trim());
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public void uploadArquivo(FileUploadEvent evento) {
        fileUploadEvent = evento;
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException {
        fileUploadEvent = event;
        file = fileUploadEvent.getFile();
        fileSelecionado = file;
        if (selecionado.getLaudoProvimentoReversao().getArquivo() != null) {
            marcadoRemover = selecionado.getLaudoProvimentoReversao().getArquivo();
        }
        arquivo = new Arquivo();
        fileDownload = new DefaultStreamedContent(event.getFile().getInputstream(), event.getFile().getContentType(), event.getFile().getFileName());
        arquivo.setDescricao(file.getFileName());
    }

    public void carregaArquivo() {
        Arquivo arq = selecionado.getLaudoProvimentoReversao().getArquivo();
        if (arq != null) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                for (ArquivoParte a : provimentoReversaoFacade.getArquivoFacade().recuperaDependencias(arq.getId()).getPartes()) {
                    buffer.write(a.getDados());
                }

                InputStream inputStream = new ByteArrayInputStream(buffer.toByteArray());
                fileDownload = new DefaultStreamedContent(inputStream, arq.getNome(), arq.getMimeType());
            } catch (Exception ex) {
                logger.error("Erro: ", ex);
            }
        }
    }

    public void recuperaTipoAposentadoria() {
        if (isAposentadoriaValida(selecionado.getAposentadoria())) {
            if (selecionado.getAposentadoria() != null && ((ProvimentoReversao) selecionado).getAposentadoria().getId() != null) {
                selecionado.setTipoAposentadoria(tipoAposentadoriaFacade.recuperaTipoAposentadoria(((ProvimentoReversao) selecionado).getAposentadoria()));
            } else {
                selecionado.setTipoAposentadoria(null);
            }
        }
    }

    private boolean isAposentadoriaValida(Aposentadoria aposentadoria) {
        try {
            int IDADE_MAX_PROVIMENTO_REVERSAO = 70;
            int IDADE_ATUAL = Integer.parseInt(DataUtil.getIdade(aposentadoria.getDataNascimento()));

            if (IDADE_ATUAL >= IDADE_MAX_PROVIMENTO_REVERSAO) {
                FacesUtil.addOperacaoNaoPermitida("Provimento de reversão só é permitido para servidores com menos de " + IDADE_MAX_PROVIMENTO_REVERSAO + " anos de idade. Idade atual do servidor selecionado: " + IDADE_ATUAL + " anos.");
                cancelarAposentadoria();
                return false;
            }
        } catch (NullPointerException npe) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao recuperar a data de nascimento deste servidor.");
            cancelarAposentadoria();
            return false;
        }
        return true;
    }

    private void cancelarAposentadoria() {
        selecionado.setAposentadoria(null);
    }

    private ContratoFP geraNovoContratoFP(ProvimentoReversao provimentoReversao) {

        ContratoFP contratoNovo = setaDadosDoNovoContrato(provimentoReversao);

        return contratoFPFacade.salvaRetornando(contratoNovo);
    }

    public void associaNovoContratoAReversao(ProvimentoReversao provimentoReversao, ContratoFP contratoFP) throws ExcecaoNegocioGenerica {
        provimentoReversao = provimentoReversaoFacade.recuperarProvimentoVigente(provimentoReversao.getAposentadoria());
        provimentoReversao.setNovoContratoFP(contratoFP);
        provimentoReversaoFacade.salvar(provimentoReversao);
    }

    private void geraNovoProvimentoFP(ContratoFP contratoFP) {
        ProvimentoFP provimentoFP = new ProvimentoFP();
        provimentoFP.setVinculoFP(contratoFP);
        provimentoFP.setTipoProvimento(tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.REVERSAO));

        provimentoFP.setDataProvimento(contratoFP.getInicioVigencia());
        provimentoFP.setAtoLegal(contratoFP.getAtoLegal());
        provimentoFP.setSequencia(provimentoFPFacade.geraSequenciaPorVinculoFP(contratoFP));
        provimentoFPFacade.salvarNovo(provimentoFP);

        provimentoFP = (ProvimentoFP) provimentoFPFacade.recuperar(ProvimentoFP.class, provimentoFP.getId());
        contratoFP = (ContratoFP) contratoFPFacade.recuperar(ContratoFP.class, contratoFP.getId());
        contratoFP.setProvimentoFP(provimentoFP);
        contratoFPFacade.salvar(contratoFP);
    }

    public ContratoFP setaDadosDoNovoContrato(ProvimentoReversao provimentoReversao) {

        ContratoFP contratoAntigo = contratoFPFacade.recuperar(provimentoReversao.getAposentadoria().getContratoFP().getId());
        ContratoFP contratoNovo = new ContratoFP();
        contratoNovo = (ContratoFP) Util.clonarObjeto(contratoAntigo);

        contratoNovo.setId(null);
        int numeroContrato = Integer.parseInt(contratoAntigo.getNumero());
        contratoNovo.setNumero(String.valueOf(numeroContrato + 1));
        contratoNovo.setInicioVigencia(provimentoReversao.getInicioVigencia());
        contratoNovo.setFinalVigencia(null);
        contratoNovo.getOpcaoValeTransporteFPs().clear();
        contratoNovo.getSindicatosVinculosFPs().clear();

        contratoNovo.getSituacaoFuncionals().clear();
        SituacaoContratoFP situacaoContratoFP = new SituacaoContratoFP();
        situacaoContratoFP.setContratoFP(contratoNovo);
        situacaoContratoFP.setSituacaoFuncional(situacaoFuncionalFacade.recuperaCodigo(1L)); //codigo 1 = Em Exercício
        situacaoContratoFP.setInicioVigencia(new Date());

        contratoAntigo = contratoFPFacade.recuperar(provimentoReversao.getAposentadoria().getContratoFP().getId());
        contratoAntigo.setFinalVigencia(contratoNovo.getInicioVigencia());

        contratoFPFacade.salvar(contratoAntigo);


        contratoNovo.getSituacaoFuncionals().add(situacaoContratoFP);
        SindicatoVinculoFP ultimoSindicato = sindicatoVinculoFPFacade.retornaUltimoSindicato(contratoAntigo);

        if (ultimoSindicato != null) {
            ultimoSindicato.setVinculoFP(contratoNovo);
            contratoNovo.getSindicatosVinculosFPs().add(ultimoSindicato);
        }

        contratoNovo.getLotacaoFuncionals().clear();
        LotacaoFuncional ultimaLotacaoFuncional = lotacaoFuncionalFacade.buscarUltimaLotacaoVigentePorVinculoFP(contratoAntigo);

        if (ultimaLotacaoFuncional != null) {
            ultimaLotacaoFuncional.setFinalVigencia(null);
            ultimaLotacaoFuncional.setVinculoFP(contratoNovo);
            contratoNovo.getLotacaoFuncionals().add(ultimaLotacaoFuncional);
        }

        contratoNovo.getAssociacaosVinculosFPs().clear();
        AssociacaoVinculoFP ultimaAssociacao = associacaoVinculoFPFacade.retornaUltimaAssociacao(contratoAntigo);

        if (ultimaAssociacao != null) {
            ultimaAssociacao.setVinculoFP(contratoNovo);
            contratoNovo.getAssociacaosVinculosFPs().add(ultimaAssociacao);
        }

        return contratoNovo;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/provimentoreversao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
