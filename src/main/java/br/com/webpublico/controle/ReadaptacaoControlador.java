package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OpcaoReadaptacao;
import br.com.webpublico.enums.StatusReadaptacao;
import br.com.webpublico.enums.TipoReadaptacao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

@ManagedBean(name = "readaptacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoReadaptacao", pattern = "/readaptacao/novo/", viewId = "/faces/rh/administracaodepagamento/readaptacao/edita.xhtml"),
    @URLMapping(id = "editarReadaptacao", pattern = "/readaptacao/editar/#{readaptacaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/readaptacao/edita.xhtml"),
    @URLMapping(id = "listarReadaptacao", pattern = "/readaptacao/listar/", viewId = "/faces/rh/administracaodepagamento/readaptacao/lista.xhtml"),
    @URLMapping(id = "verReadaptacao", pattern = "/readaptacao/ver/#{readaptacaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/readaptacao/visualizar.xhtml")
})
public class ReadaptacaoControlador extends PrettyControlador<Readaptacao> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ReadaptacaoControlador.class);

    @EJB
    private ReadaptacaoFacade readaptacaoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterContratoServidor;
    private ConverterAutoComplete converterCid;
    private ConverterAutoComplete converterCargo;
    private EnquadramentoFuncional enquadramentoFuncional;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;

    private UploadedFile uploadedFile;
    private Map arquivos;
    private StreamedContent fileDownload;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private CIDFacade cidFacade;
    private Arquivo marcadoRemover;
    @EJB
    private CargoFacade cargoFacade;
    private List<OpcaoReadaptacao> opcoes;
    private VigenciaReadaptacao vigenciaReadaptacao;


    public ReadaptacaoControlador() {
        super(Readaptacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return readaptacaoFacade;
    }

    @URLAction(mappingId = "novoReadaptacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        enquadramentoFuncional = null;
        opcoes = new ArrayList<>();
        vigenciaReadaptacao = new VigenciaReadaptacao();
        arquivos = new HashMap();
    }

    @URLAction(mappingId = "verReadaptacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarReadaptacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        buscaEnquadramentosPorServidor();
        opcoes = new ArrayList<>();
        opcoes.addAll(selecionado.getOpcoes());
        vigenciaReadaptacao = new VigenciaReadaptacao();
    }

    public List<SelectItem> getTiposDeReadaptacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoReadaptacao object : TipoReadaptacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public OpcaoReadaptacao[] getValoresOpcaoReadaptacao() {
        return OpcaoReadaptacao.values();
    }

    public List<ContratoFP> completaContratoServidor(String parte) {
        return contratoFPFacade.recuperaContratoEstatutarioComEnquadramento(parte.trim());
    }

    public List<CID> completaCid(String parte) {
        return cidFacade.listaFiltrando(parte.trim(), "descricao", "codigoDaCid");
    }

    public List<Cargo> completaCargo(String parte) {
        return cargoFacade.buscarCargosVigentesPorUnidadeOrganizacionalAndUsuario(parte.trim());
    }

    public Converter getConverterContratoServidor() {

        if (converterContratoServidor == null) {
            converterContratoServidor = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoServidor;
    }

    public Converter getConverterCid() {
        if (converterCid == null) {
            converterCid = new ConverterAutoComplete(CID.class, cidFacade);
        }
        return converterCid;
    }

    public Converter getConverterCargo() {
        if (converterCargo == null) {
            converterCargo = new ConverterAutoComplete(Cargo.class, cargoFacade);
        }
        return converterCargo;
    }

    @Override
    public void salvar() {

        for (OpcaoReadaptacao opcaoReadaptacao : opcoes) {
            selecionado.getOpcoes().add(opcaoReadaptacao);
        }

        super.salvar();
    }

    public EnquadramentoFuncional getEnquadramentoFuncional() {
        return enquadramentoFuncional;
    }

    public void setEnquadramentoFuncional(EnquadramentoFuncional enquadramentoFuncional) {
        this.enquadramentoFuncional = enquadramentoFuncional;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/readaptacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void buscaEnquadramentosPorServidor() {
        EnquadramentoFuncional eq = enquadramentoFuncionalFacade.recuperaEnquadramentoFuncionalVigentePorContratoServidor(this.selecionado.getContratoFP(), null);
        if (eq != null) {
            enquadramentoFuncional = eq;
        }
    }

    public void carregaArquivo(VigenciaReadaptacao vigencia) {
        if (vigencia.getArquivoLaudo() != null) {
            Arquivo arq = vigencia.getArquivoLaudo();
            if (arq != null) {
                try {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    for (ArquivoParte a : arquivoFacade.recuperaDependencias(arq.getId()).getPartes()) {
                        buffer.write(a.getDados());
                    }
                    InputStream teste = new ByteArrayInputStream(buffer.toByteArray());
                    fileDownload = new DefaultStreamedContent(teste, arq.getNome(), arq.getMimeType());
                } catch (Exception ex) {
                    logger.error("Erro: ", ex);
                }
            }
        }
    }

    public void handleFileUpload(FileUploadEvent event) throws Exception {
        uploadedFile = event.getFile();
        Arquivo arquivo = new Arquivo();
        arquivo = arquivoFacade.novoArquivoMemoria(arquivo, uploadedFile.getInputstream());
        arquivo.setNome(uploadedFile.getFileName());
        arquivo.setDescricao(uploadedFile.getFileName());
        arquivo.setTamanho(uploadedFile.getSize());
        arquivo.setMimeType(uploadedFile.getContentType());
        vigenciaReadaptacao.setArquivoLaudo(arquivo);
        fileDownload = new DefaultStreamedContent(event.getFile().getInputstream(), event.getFile().getContentType(), event.getFile().getFileName());
    }

    public void limparOpcoes() {
        opcoes.clear();
        selecionado.getOpcoes().clear();
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public StreamedContent teste(VigenciaReadaptacao vigencia) {
        carregaArquivo(vigencia);
        return fileDownload;
    }

    public void setFileDownload(StreamedContent fileDownload) {
        this.fileDownload = fileDownload;
    }

    public Arquivo getMarcadoRemover() {
        return marcadoRemover;
    }

    public void setMarcadoRemover(Arquivo marcadoRemover) {
        this.marcadoRemover = marcadoRemover;
    }

    public List<OpcaoReadaptacao> getOpcoes() {
        return opcoes;
    }

    public void setOpcoes(List<OpcaoReadaptacao> opcoes) {
        this.opcoes = opcoes;
    }

    public Converter getConverterOpcaoReadaptacao() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
                for (OpcaoReadaptacao ordem : OpcaoReadaptacao.values()) {
                    if (ordem.getDescricao().equals(string)) {
                        return ordem;
                    }
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext fc, UIComponent uic, Object o) {
                return ((OpcaoReadaptacao) o).getDescricao();
            }
        };
    }

    public VigenciaReadaptacao getVigenciaReadaptacao() {
        return vigenciaReadaptacao;
    }

    public void setVigenciaReadaptacao(VigenciaReadaptacao vigenciaReadaptacao) {
        this.vigenciaReadaptacao = vigenciaReadaptacao;
    }

    public void adicionaVigencia() {
        if (validaCamposVigenciaReadaptacao()) {
            vigenciaReadaptacao.setReadaptacao(this.selecionado);
            this.selecionado.getVigenciaReadaptacoes().add(vigenciaReadaptacao);
            vigenciaReadaptacao = new VigenciaReadaptacao();
            ordenarListaVigenciaReadaptacoes();
            modificaStatusReadaptacao();
        }
    }

    public void modificaStatusReadaptacao() {

        if (selecionado.getTipoReadaptacao() != null) {

            if (selecionado.getTipoReadaptacao().equals(TipoReadaptacao.TEMPORARIO)) {
                if (!selecionado.getVigenciaReadaptacoes().isEmpty()) {
                    VigenciaReadaptacao vigencia = selecionado.getVigenciaReadaptacoes().get(0);

                    if (vigencia.getFinalVigencia() != null) {

                        this.selecionado.setStatusReadaptacao(StatusReadaptacao.CESSADA);
                    } else {

                        this.selecionado.setStatusReadaptacao(StatusReadaptacao.PRORROGADA);
                    }
                }
            } else {
                this.selecionado.setStatusReadaptacao(StatusReadaptacao.PERMANENTE);
            }
            RequestContext.getCurrentInstance().update(":Formulario:status");
        } else {
            this.selecionado.setStatusReadaptacao(null);
        }
    }

    public void ordenarListaVigenciaReadaptacoes() {
        Collections.sort(selecionado.getVigenciaReadaptacoes(), new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                VigenciaReadaptacao v1 = (VigenciaReadaptacao) o1;
                VigenciaReadaptacao v2 = (VigenciaReadaptacao) o2;
                if (v1.getInicioVigencia().after(v2.getInicioVigencia())) {
                    return -1;
                }
                if (v1.getInicioVigencia().before(v2.getInicioVigencia())) {
                    return 1;
                }
                return 0;
            }
        });
    }

    private boolean validaCamposVigenciaReadaptacao() {
        boolean retorno = true;
        if (selecionado.getTipoReadaptacao() != null) {
            if (vigenciaReadaptacao.getInicioVigencia() == null) {
                FacesUtil.addError("Atenção!", "Informe a data da avaliação.");
                retorno = false;
            }
            if (vigenciaReadaptacao.getNumeroLaudo() == null || vigenciaReadaptacao.getNumeroLaudo().trim().equals("")) {
                FacesUtil.addError("Atenção!", "Informe o número do laudo.");
                retorno = false;
            }
            if (vigenciaReadaptacao.getDescricaoLaudo() == null || vigenciaReadaptacao.getDescricaoLaudo().trim().equals("")) {
                FacesUtil.addError("Atenção!", "Informe a descrição do laudo.");
                retorno = false;
            }
            if (DataUtil.verificaDataFinalMaiorQueDataInicial(vigenciaReadaptacao.getInicioVigencia(), vigenciaReadaptacao.getFinalVigencia())) {
                FacesUtil.addError("Atenção!", "A data de avaliação é menor que a data de final de vigência.");
                retorno = false;
            }
            if (uploadedFile == null) {
                FacesUtil.addError("Atenção!", "Selecione o arquivo do laudo.");
                retorno = false;
            }
            if (selecionado.getTipoReadaptacao().equals(TipoReadaptacao.TEMPORARIO) && vigenciaReadaptacao.getInicioVigencia() != null) {
                if (vigenciaReadaptacao.getDataReavaliacao() != null && vigenciaReadaptacao.getDataReavaliacao().compareTo(vigenciaReadaptacao.getInicioVigencia()) <= 0) {
                    FacesUtil.addError("Atenção!", "A data da reavaliação é menor que a data da avaliação.");
                    retorno = false;
                }

            }
        } else {
            FacesUtil.addError("Atenção!", "Por favor informe o Tipo de Readaptação.");
            retorno = false;
        }
        return retorno;
    }

    public void removeVigenciaReadaptacao(VigenciaReadaptacao vigencia) {
        this.selecionado.getVigenciaReadaptacoes().remove(vigencia);
    }

    public boolean getDesabilitaBotaoAdicionar() {
        return selecionado != null && selecionado.getTipoReadaptacao() != null && selecionado.getTipoReadaptacao().equals(TipoReadaptacao.PERMANENTE) && selecionado.getVigenciaReadaptacoes().size() >= 1;
    }

}
