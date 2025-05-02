package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DAMResultadoParcela;
import br.com.webpublico.enums.NaturezaDocumentoMarcaFogo;
import br.com.webpublico.enums.SituacaoMarcaFogo;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoEmissaoMarcaFogo;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoMarcaFogo",
        pattern = "/tributario/marca-fogo/novo/",
        viewId = "/faces/tributario/marcafogo/edita.xhtml"),
    @URLMapping(id = "listarMarcaFogo",
        pattern = "/tributario/marca-fogo/listar/",
        viewId = "/faces/tributario/marcafogo/lista.xhtml"),
    @URLMapping(id = "editarMarcaFogo",
        pattern = "/tributario/marca-fogo/editar/#{marcaFogoControlador.id}/",
        viewId = "/faces/tributario/marcafogo/edita.xhtml"),
    @URLMapping(id = "verMarcaFogo",
        pattern = "/tributario/marca-fogo/ver/#{marcaFogoControlador.id}/",
        viewId = "/faces/tributario/marcafogo/visualizar.xhtml")
})
public class MarcaFogoControlador extends PrettyControlador<MarcaFogo> implements CRUD {

    @EJB
    private MarcaFogoFacade marcaFogoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ParametroMarcaFogoFacade parametroMarcaFogoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    private ParametroMarcaFogo parametroMarcaFogo;
    private AnexoMarcaFogo anexoMarcaFogo;
    private String imgSrc;
    private CadastroRuralMarcaFogo cadastroRuralMarcaFogo;

    public MarcaFogoControlador() {
        super(MarcaFogo.class);
        cadastroRuralMarcaFogo = new CadastroRuralMarcaFogo();
    }

    @Override
    public MarcaFogoFacade getFacede() {
        return marcaFogoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/marca-fogo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public AnexoMarcaFogo getAnexoMarcaFogo() {
        return anexoMarcaFogo;
    }

    public void setAnexoMarcaFogo(AnexoMarcaFogo anexoMarcaFogo) {
        this.anexoMarcaFogo = anexoMarcaFogo;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public CadastroRuralMarcaFogo getCadastroRuralMarcaFogo() {
        return cadastroRuralMarcaFogo;
    }

    public void setCadastroRuralMarcaFogo(CadastroRuralMarcaFogo cadastroRuralMarcaFogo) {
        this.cadastroRuralMarcaFogo = cadastroRuralMarcaFogo;
    }

    @URLAction(mappingId = "novoMarcaFogo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setSituacao(SituacaoMarcaFogo.ABERTO);
        selecionado.setDataLancamento(new Date());
        selecionado.setExercicio(exercicioFacade.getExercicioCorrente());
        selecionado.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        selecionado.setTipoCadastroTributario(TipoCadastroTributario.PESSOA);
        carregarParametro();
        carregarAnexosProcesso();
    }

    private void carregarParametro() {
        try {
            parametroMarcaFogo = parametroMarcaFogoFacade.buscarParametroPeloExercicio(selecionado.getExercicio());
            if (parametroMarcaFogo == null) {
                throw new ValidacaoException("Parâmetro de marca a fogo do exercício " + selecionado.getExercicio().getAno() + " não encontrado.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
        }
    }

    private void carregarAnexosProcesso() {
        if (parametroMarcaFogo == null) return;
        for (DocumentoMarcaFogo documento : parametroMarcaFogo.getDocumentosPorNatureza(NaturezaDocumentoMarcaFogo.PROCESSO)) {
            if (selecionado.getAnexos().stream().noneMatch(a -> a.getDocumentoMarcaFogo().equals(documento))) {
                AnexoMarcaFogo anexoMarcaFogo = new AnexoMarcaFogo();
                anexoMarcaFogo.setMarcaFogo(selecionado);
                anexoMarcaFogo.setDocumentoMarcaFogo(documento);
                selecionado.getAnexos().add(anexoMarcaFogo);
            }
        }
    }

    public void carregarAnexosProcurador() {
        if (!selecionado.getTemProcurador()) {
            selecionado.setProcurador(null);
            removerAnexosProcurador();
        } else {
            for (DocumentoMarcaFogo documento : parametroMarcaFogo.getDocumentosPorNatureza(NaturezaDocumentoMarcaFogo.PROCURADOR)) {
                if (selecionado.getAnexos().stream().noneMatch(a -> a.getDocumentoMarcaFogo().equals(documento))) {
                    AnexoMarcaFogo anexoMarcaFogo = new AnexoMarcaFogo();
                    anexoMarcaFogo.setMarcaFogo(selecionado);
                    anexoMarcaFogo.setDocumentoMarcaFogo(documento);
                    selecionado.getAnexos().add(anexoMarcaFogo);
                }
            }
        }
    }

    private void removerAnexosProcurador() {
        List<AnexoMarcaFogo> anexosParaRemover = Lists.newArrayList();
        for (AnexoMarcaFogo anexoMarcaFogo : selecionado.getAnexos()) {
            if (NaturezaDocumentoMarcaFogo.PROCURADOR.equals(anexoMarcaFogo.getDocumentoMarcaFogo().getNaturezaDocumento())) {
                anexosParaRemover.add(anexoMarcaFogo);
            }
        }
        selecionado.getAnexos().removeAll(anexosParaRemover);
    }

    @URLAction(mappingId = "editarMarcaFogo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        carregarFoto();
        carregarParametro();
    }

    @URLAction(mappingId = "verMarcaFogo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        carregarFoto();
    }

    public List<SelectItem> getTiposCadastroTributario() {
        return TipoCadastroTributario.asSelectItemList();
    }

    public void mudouTipoCadastro() {
        selecionado.setCadastro(null);
        selecionado.setPessoa(null);
    }

    public void uploadFoto(FileUploadEvent event) {
        try {
            Arquivo arquivo = arquivoFacade.criarArquivo(event.getFile());
            selecionado.setLogo(arquivo);
            carregarFoto();
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void carregarFoto() {
        imgSrc = null;
        if (selecionado.getLogo() == null) return;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            for (ArquivoParte a : selecionado.getLogo().getPartes()) {
                buffer.write(a.getDados());
            }
            String base64 = Util.getBase64Encode(buffer.toByteArray());
            imgSrc = "data:image/png;base64, " + base64;
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    public void trocarFoto() {
        selecionado.setLogo(null);
    }

    public String getExtensoesPermitidas() {
        String extensoesPermitidas = anexoMarcaFogo.getDocumentoMarcaFogo().getExtensoesPermitidas();
        extensoesPermitidas = extensoesPermitidas.replace(".", "");
        extensoesPermitidas = extensoesPermitidas.replace(",", "|");
        return "/(\\.|\\/)(" + extensoesPermitidas + ")$/";
    }

    public String getMensagemExtensoesPermitidas() {
        String extensoesPermitidas = anexoMarcaFogo.getDocumentoMarcaFogo().getExtensoesPermitidas();
        if (Strings.isNullOrEmpty(extensoesPermitidas)) return "";
        return "Extensão invalida, apenas as extenções (" + extensoesPermitidas + ") são permitidas.";
    }

    public void uploadDocumento(FileUploadEvent event) {
        try {
            Arquivo arquivo = arquivoFacade.criarArquivo(event.getFile());
            anexoMarcaFogo.setArquivo(arquivo);
            FacesUtil.executaJavaScript("dlgUploadDocumento.hide()");
            FacesUtil.atualizarComponente("formulario:tbView:opDocumentos");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void substituirDocumento(AnexoMarcaFogo anexoMarcaFogo) {
        setAnexoMarcaFogo(anexoMarcaFogo);
        this.anexoMarcaFogo.setArquivo(null);
        FacesUtil.executaJavaScript("dlgUploadDocumento.show()");
        FacesUtil.atualizarComponente("formUploadDocumento");
    }

    public void processar() {
        try {
            marcaFogoFacade.processar(sistemaFacade.getUsuarioCorrente(), selecionado);
            FacesUtil.addOperacaoRealizada("Registro processado com sucesso.");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    private void emitirCertidao() throws AtributosNulosException, UFMException {
        selecionado = marcaFogoFacade.gerarDocumentoOficial(selecionado);
        marcaFogoFacade.emitirDocumentoOficial(selecionado.getDocumentoOficial());
    }

    public void verificarGeracaoTaxa() {
        try {
            if (selecionado.hasDebitosPorTipoEmissao(TipoEmissaoMarcaFogo.SEGUNDA_VIA)) {
                marcaFogoFacade.verificarDebitoEmAberto(selecionado);
                if (selecionado.temDebitoComDocumentoAindaNaoImpresso(TipoEmissaoMarcaFogo.SEGUNDA_VIA)) {
                    emitirCertidao();
                } else {
                    FacesUtil.executaJavaScript("dlgConfirmaTaxa.show()");
                }
            } else {
                emitirCertidao();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public boolean canGerarSegundaVia() {
        if (!selecionado.getSituacao().equals(SituacaoMarcaFogo.PROCESSADO)) return false;
        List<DebitoMarcaFogo> debitosMarcaFogo = selecionado.getDebitoPorTipoEmissao(TipoEmissaoMarcaFogo.PRIMEIRA_VIA);
        for (DebitoMarcaFogo debito : debitosMarcaFogo) {
            if (debito.hasDebitoEmAberto()) return false;
        }
        debitosMarcaFogo = selecionado.getDebitoPorTipoEmissao(TipoEmissaoMarcaFogo.SEGUNDA_VIA);
        return debitosMarcaFogo.isEmpty();
    }

    public void gerarSegundaVia() {
        try {
            marcaFogoFacade.gerarSegundaVia(sistemaFacade.getUsuarioCorrente(), selecionado);
            FacesUtil.addOperacaoRealizada("Segunda via gerada com sucesso.");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public boolean lancamentoAberto() {
        return SituacaoMarcaFogo.ABERTO.equals(selecionado.getSituacao());
    }

    public void imprimirDAM() {
        try {
            List<DAM> dams = Lists.newArrayList();
            for (DAMResultadoParcela damResultadoParcela : selecionado.getListaDAMResultadoParcela()) {
                if (damResultadoParcela.getResultadoParcela().isEmAberto()) {
                    DAM dam = damFacade.gerarDAMUnicoViaApi(sistemaFacade.getUsuarioCorrente(), damResultadoParcela.getResultadoParcela());
                    dams.add(dam);
                }
            }
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            imprimeDAM.imprimirDamUnicoViaApi(dams);
            selecionado = marcaFogoFacade.recuperar(selecionado.getId());
        } catch (ValidacaoException onpe) {
            FacesUtil.printAllFacesMessages(onpe.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um problema no servidor, tente de novo, se o problema persistir entre em contato com o suporte.");
            logger.error("Erro ao emitir o de Marca a Fogo. {}", e.getMessage());
            logger.debug("Erro ao emitir o de Marca a Fogo.", e);
        }
    }

    public List<CadastroRural> completaCadastroRural(String parte) {
        return cadastroRuralFacade.completaCodigoNomeLocalizacaoIncra(parte.trim());
    }

    @Override
    public void salvar() {
        try {
            selecionado = marcaFogoFacade.salvarRetornando(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void adicionarCadastroRural() {
        try {
            selecionado.validarNovoCadastroRural(cadastroRuralMarcaFogo);
            cadastroRuralMarcaFogo.setMarcaFogo(selecionado);
            selecionado.getCadastrosRurais().add(cadastroRuralMarcaFogo);
            cadastroRuralMarcaFogo = new CadastroRuralMarcaFogo();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void removerCadastroRural(CadastroRuralMarcaFogo cadastroRuralMarcaFogo) {
        selecionado.getCadastrosRurais().remove(cadastroRuralMarcaFogo);
    }

    public void confirmaGeracaoTaxa(boolean confirmou) {
        try {
            if (confirmou) {
                gerarSegundaVia();
            } else {
                emitirCertidao();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }
}
