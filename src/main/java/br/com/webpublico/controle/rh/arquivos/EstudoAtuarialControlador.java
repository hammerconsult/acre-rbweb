package br.com.webpublico.controle.rh.arquivos;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controle.Web;
import br.com.webpublico.entidades.Aposentadoria;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.Dependente;
import br.com.webpublico.entidades.Pensionista;
import br.com.webpublico.entidades.rh.arquivos.EstudoAtuarial;
import br.com.webpublico.entidadesauxiliares.AssistenteDetentorArquivoComposicao;
import br.com.webpublico.entidadesauxiliares.AuxiliarAndamentoArquivoAtuarial;
import br.com.webpublico.enums.TipoArquivoAtuarial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.arquivos.EstudoAtuarialFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.*;


/**
 * Created by Buzatto on 18/05/2016.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-estudo-atuarial", pattern = "/estudo-atuarial/novo/", viewId = "/faces/rh/arquivos/estudoatuarial/edita.xhtml"),
    @URLMapping(id = "editar-estudo-atuarial", pattern = "/estudo-atuarial/editar/#{estudoAtuarialControlador.id}/", viewId = "/faces/rh/arquivos/estudoatuarial/edita.xhtml"),
    @URLMapping(id = "visualizar-estudo-atuarial", pattern = "/estudo-atuarial/ver/#{estudoAtuarialControlador.id}/", viewId = "/faces/rh/arquivos/estudoatuarial/visualizar.xhtml"),
    @URLMapping(id = "listar-estudo-atuarial", pattern = "/estudo-atuarial/listar/", viewId = "/faces/rh/arquivos/estudoatuarial/lista.xhtml"),
    @URLMapping(id = "acompanhar-estudo-atuarial", pattern = "/estudo-atuarial/acompanhamento/", viewId = "/faces/rh/arquivos/estudoatuarial/acompanhamento.xhtml")
})
public class EstudoAtuarialControlador extends PrettyControlador<EstudoAtuarial> implements CRUD, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(EstudoAtuarialControlador.class);

    @EJB
    private EstudoAtuarialFacade estudoAtuarialFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    @EJB
    private PensionistaFacade pensionistaFacade;
    @EJB
    private DependenteFacade dependenteFacade;

    private AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao;
    private AuxiliarAndamentoArquivoAtuarial auxiliarAndamentoArquivoAtuarial;
    private StreamedContent fileDownload;
    private File zipFile;

    public EstudoAtuarialControlador() {
        super(EstudoAtuarial.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/estudo-atuarial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return estudoAtuarialFacade;
    }

    @URLAction(mappingId = "novo-estudo-atuarial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializaAssistenteDetentorArquivo();
        selecionado.setDataRegistro(new Date());
        selecionado.setDataReferencia(new Date());
        selecionado.setInicioCompetencia(new Date());
        selecionado.setFinalCompetencia(new Date());
        selecionado.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        selecionado.setSequencia(estudoAtuarialFacade.buscarProximoNumero());
    }

    @URLAction(mappingId = "editar-estudo-atuarial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "visualizar-estudo-atuarial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializaAssistenteDetentorArquivo();
    }

    public void gerarArquivosEstudoAtuarial() {
        try {
            validarGeracao();
            carregarServidores();
            validarServidores();
            gerarArquivos();
        } catch (ValidacaoException ve) {
            if (ve.temMensagens()) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            } else {
                FacesUtil.addOperacaoNaoRealizada(ve.getMessage());
            }
        } catch (Exception ex) {
            logger.error("Erro ao gerar arquivos estudo atuarial! " + ex);
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar arquivos estudo atuarial! " + ex.getMessage());
        }
    }

    private void gerarArquivos() {
        Web.poeNaSessao("ESTUDO_ATUARIAL", selecionado);
        FacesUtil.redirecionamentoInterno("/estudo-atuarial/acompanhamento/");
    }

    @URLAction(mappingId = "acompanhar-estudo-atuarial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void acompanhamento() {
        try {
            EstudoAtuarial estudoAtuarial = (EstudoAtuarial) Web.pegaDaSessao("ESTUDO_ATUARIAL");

            if (estudoAtuarial != null) {
                auxiliarAndamentoArquivoAtuarial = new AuxiliarAndamentoArquivoAtuarial();
                auxiliarAndamentoArquivoAtuarial.iniciarProcesso();
                carregarCaminhoBrasao();
                selecionado = estudoAtuarial;

                estudoAtuarialFacade.gerarArquivos(selecionado, auxiliarAndamentoArquivoAtuarial);
            } else if (auxiliarAndamentoArquivoAtuarial.getParado()) {
                FacesUtil.redirecionamentoInterno("/estudo-atuarial/novo/");
            }
        } catch (RuntimeException re) {
            FacesUtil.addOperacaoNaoRealizada(re.getMessage());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void carregarCaminhoBrasao() {
        String imagem = FacesUtil.geraUrlImagemDir();
        imagem += "/img/escudo.png";
        auxiliarAndamentoArquivoAtuarial.setCaminhoBrasao(imagem);
    }

    public StreamedContent baixarArquivoZip() {
        try {
            FileInputStream fileInputStream = new FileInputStream(auxiliarAndamentoArquivoAtuarial.getZipFile());
            fileDownload = new DefaultStreamedContent(fileInputStream, "application/zip", "EstudoAtuarial.zip");
            return fileDownload;
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível gerar o arquivo ZIP do Estudo Atuarial, por favor, comunique o suporte técnico.");
        }
        return null;
    }

    public StreamedContent recuperarArquivoParaDownload() {
        return null;
    }

    public List<TipoArquivoAtuarial> getTiposArquivoAtuarial() {
        return Arrays.asList(TipoArquivoAtuarial.values());
    }

    public void redirecionarParaNovo() {
        Web.limpaNavegacao();
        FacesUtil.redirecionamentoInterno("/estudo-atuarial/novo/");
    }

    public void visualizar() {
        Web.limpaNavegacao();
        FacesUtil.redirecionamentoInterno("/estudo-atuarial/ver/" + selecionado.getId());
    }

    private void validarServidores() {
        if (!selecionado.temServidorParaSerProcessado()) {
            throw new ValidacaoException("Nenhum servidor encontrado na Competência <b>" + DataUtil.getDataFormatada(selecionado.getInicioCompetencia(), "MM/yyyy") + " a " + DataUtil.getDataFormatada(selecionado.getFinalCompetencia(), "MM/yyyy") +"</b>. Por favor, verifique os dados e tente novamente!");
        }
    }

    private void validarGeracao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getInicioCompetencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Competência Inicial deve ser informado!");
        } else {
            selecionado.setDataReferencia(selecionado.getInicioCompetencia());
        }

        if (selecionado.getFinalCompetencia().before(selecionado.getInicioCompetencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Competência Final deve ser posterior ao Competência Inicial!");
        }


        if (selecionado.getFinalCompetencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Competência Final deve ser informado!");
        }
        if (CollectionUtils.isEmpty(selecionado.getTiposArquivo())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o(s) arquivo(s) a ser(em) gerados!");
        }
        if (ve.temMensagens()) throw ve;
    }

    private void recuperarDependentes(List<Long> idsDependentes, HashMap<Long , Dependente> dependenteCache, Date competencia) {
        List<Dependente> dependentes = new ArrayList<>();
        for (Long idDependente : idsDependentes){
            Dependente dependente = dependenteCache.get(idDependente);
            if(dependente == null){
                dependente = dependenteFacade.recuperar(idDependente);
                if (dependente != null) {
                    dependenteCache.put(idDependente, dependente);
                    dependentes.add(dependente);
                }
            } else {
                dependentes.add(dependente);
            }
        }
        if(!dependentes.isEmpty()){
            selecionado.getDependentePorMes().put(competencia, dependentes);
        }
    }

    private void recuperarPensionistas(List<Long> idsAposentados, HashMap<Long , Pensionista> pensionistaCache, Date competencia, String tipoArquivo) {
        List<Pensionista> pensionistas = new ArrayList<>();
        for (Long idPensionista : idsAposentados){
            Pensionista pensionista = pensionistaCache.get(idPensionista);
            if(pensionista == null){
                 pensionista = pensionistaFacade.recuperarParaArquivoAtuarial(idPensionista);
                if (pensionista != null) {
                    pensionistaCache.put(idPensionista, pensionista);
                    pensionistas.add(pensionista);
                }
            } else {
                pensionistas.add(pensionista);
            }
        }
        if(!pensionistas.isEmpty()){
            if(tipoArquivo.equals(AuxiliarAndamentoArquivoAtuarial.PENSIONISTAS)){
                selecionado.getPensionistasPorMes().put(competencia, pensionistas);
            }
            if(tipoArquivo.equals(AuxiliarAndamentoArquivoAtuarial.PENSIONISTAS_FALECIDOS)){
                selecionado.getPensionistasFalecidosPorMes().put(competencia, pensionistas);
            }
        }

    }

    private void recuperarAposentados(List<Long> idsAposentados, HashMap<Long , Aposentadoria> aposentadoriaCache, Date competencia, String tipoArquivo) {
        List<Aposentadoria> aposentadorias = new ArrayList<>();
        for (Long idServidor : idsAposentados) {
            Aposentadoria aposentadoria = aposentadoriaCache.get(idServidor);
            if (aposentadoria == null) {
                aposentadoria = aposentadoriaFacade.recuperarParaArquivoAtuarial(idServidor);
                if (aposentadoria != null) {
                    aposentadoriaCache.put(idServidor, aposentadoria);
                    aposentadorias.add(aposentadoria);
                }
            } else {
                aposentadorias.add(aposentadoria);
            }
        }
        if (!aposentadorias.isEmpty()) {
            if (tipoArquivo.equals(AuxiliarAndamentoArquivoAtuarial.APOSENTADOS)) {
                selecionado.getAposentadoriaPorMes().put(competencia, aposentadorias);
            } else if (tipoArquivo.equals(AuxiliarAndamentoArquivoAtuarial.APOSENTADOS_FALECIDOS)) {
                selecionado.getAposentadosFalecidosPorMes().put(competencia, aposentadorias);
            }
        }
    }

    private void recuperarContratos(List<Long> idsContratos, HashMap<Long , ContratoFP> contratoFPCache, Date competencia, String tipoArquivo) {
        List<ContratoFP> contratoFPList = new ArrayList<>();
        for (Long idServidor : idsContratos){
            ContratoFP contratoFP = contratoFPCache.get(idServidor);
            if(contratoFP == null){
                contratoFP = contratoFPFacade.recuperarParaArquivoAtuarial(idServidor);
                if (contratoFP != null) {
                    contratoFPCache.put(idServidor, contratoFP);
                    contratoFPList.add(contratoFP);
                }
            } else {
                contratoFPList.add(contratoFP);
            }
        }
        if(!contratoFPList.isEmpty()) {
            if (tipoArquivo.equals(AuxiliarAndamentoArquivoAtuarial.ATIVOS)) {
                selecionado.getServidoresAtivosPorMes().put(competencia, contratoFPList);
            }
            if (tipoArquivo.equals(AuxiliarAndamentoArquivoAtuarial.ATIVOS_FALECIDOS_OU_EXONERADOS)) {
                selecionado.getServidoresAtivosFalecidosOuExonerados().put(competencia, contratoFPList);
            }
        }
    }

    private void carregarServidores() {

        Date inicio = selecionado.getInicioCompetencia();
        Date fim = selecionado.getFinalCompetencia();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inicio);


        HashMap<Long, ContratoFP> contratoFPCache = new HashMap<>();
        HashMap<Long, Aposentadoria> aposentadoriaCache = new HashMap<>();
        HashMap<Long, Pensionista> pensionistaCache = new HashMap<>();
        HashMap<Long, Dependente> dependenteCache = new HashMap<>();

        while (!calendar.getTime().after(fim)) {
            Date competencia = calendar.getTime();
            competencia = Util.getDataHoraMinutoSegundoZerado(Util.recuperaDataUltimoDiaDoMes(competencia));

            if (selecionado.temTipoServidoresAtivos()) {
                List<Long> idsContratosFP = contratoFPFacade.buscarServidoresAtivosArquivoAtuarial(competencia, selecionado.getExercicio());
                if (!idsContratosFP.isEmpty()) {
                    selecionado.getIdsServidoresAtivos().addAll(contratoFPFacade.buscarServidoresAtivosArquivoAtuarial(competencia, selecionado.getExercicio()));
                    recuperarContratos(idsContratosFP, contratoFPCache, competencia, AuxiliarAndamentoArquivoAtuarial.ATIVOS);
                }
            }

            if (selecionado.temTipoServidoresAposentados()) {
                List<Long> idsAposentados = aposentadoriaFacade.buscarAposentadoriasArquivoAtuarial(competencia);
                if (!idsAposentados.isEmpty()) {
                    selecionado.getIdsServidoresAposentados().addAll(idsAposentados);
                    recuperarAposentados(idsAposentados, aposentadoriaCache, competencia, AuxiliarAndamentoArquivoAtuarial.APOSENTADOS);
                }
            }

            if (selecionado.temTipoServidoresPensionistas()) {
                List<Long> idsPensinista = pensionistaFacade.buscarPensionistasArquivoAtuarial(competencia);
                if (!idsPensinista.isEmpty()) {
                    selecionado.getIdsServidoresPensionistas().addAll(idsPensinista);
                    recuperarPensionistas(idsPensinista, pensionistaCache, competencia, AuxiliarAndamentoArquivoAtuarial.PENSIONISTAS);
                }
            }

            if (selecionado.temTipoServidoresAtivosFalecidosOuExonerados()) {
                List<Long> idsServidoresFalecidos = contratoFPFacade.buscarServidoresComRegistroObito(competencia, selecionado.getExercicio());
                List<Long> idsServidoresExonerados = contratoFPFacade.buscarServidoresExonerados(competencia, selecionado.getExercicio());
                List<Long> idsServidores = new ArrayList<>();

                if (!idsServidoresFalecidos.isEmpty()) {
                    idsServidores.addAll(idsServidoresFalecidos);
                }
                if (!idsServidoresExonerados.isEmpty()) {
                    idsServidores.addAll(idsServidoresExonerados);
                }

                if (!idsServidores.isEmpty()) {
                    selecionado.getIdsServidoresAtivosFalecidosExonerados().addAll(idsServidores);
                    recuperarContratos(idsServidores, contratoFPCache, competencia, AuxiliarAndamentoArquivoAtuarial.ATIVOS_FALECIDOS_OU_EXONERADOS);
                }
            }

            if (selecionado.temTipoServidoresAposentadosFalecidos()) {
                List<Long> idsServidoresAposentadosFalecidos = aposentadoriaFacade.buscarAposentadosComRegistroObito(competencia);
                if(!idsServidoresAposentadosFalecidos.isEmpty()){
                    selecionado.getIdsServidoresAposentadosFalecidos().addAll(idsServidoresAposentadosFalecidos);
                    recuperarAposentados(idsServidoresAposentadosFalecidos, aposentadoriaCache, competencia, AuxiliarAndamentoArquivoAtuarial.APOSENTADOS_FALECIDOS);
                }
            }

            if (selecionado.temTipoServidoresPensionistasFalecidos()) {
                List<Long> idsServidoresPensionistasFalecidos = pensionistaFacade.buscarPensionistaComRegistroObito(competencia);
                if(!idsServidoresPensionistasFalecidos.isEmpty()){
                    selecionado.getIdsServidoresPensionistasFalecidos().addAll(idsServidoresPensionistasFalecidos);
                    recuperarPensionistas(idsServidoresPensionistasFalecidos, pensionistaCache, competencia, AuxiliarAndamentoArquivoAtuarial.PENSIONISTAS_FALECIDOS);
                }
            }

            if (selecionado.temTipoDependentes()) {
                List<Long> idsServidoresAtivos = contratoFPFacade.buscarServidoresAtivosArquivoAtuarial(competencia, selecionado.getExercicio());
                List<Long> idsDependentes = new ArrayList<>();
                for (Long id : idsServidoresAtivos) {
                    ContratoFP contrato = contratoFPFacade.recuperarPessoaParaArquivoAtuarial(id);
                    List<Long> dependentesEncontrados = dependenteFacade.buscarDependentesVinculoFPArquivoAtuarial(contrato.getMatriculaFP().getPessoa(), competencia);
                    if (dependentesEncontrados != null && !dependentesEncontrados.isEmpty()) {
                        idsDependentes.addAll(dependentesEncontrados);
                    }
                }

                if (!idsDependentes.isEmpty()) {
                    selecionado.getIdsDependentes().addAll(idsDependentes);
                    recuperarDependentes(idsDependentes, dependenteCache, competencia);
                }
            }

            calendar.add(Calendar.MONTH, 1);
        }
    }

    private void inicializaAssistenteDetentorArquivo() {
        assistenteDetentorArquivoComposicao = new AssistenteDetentorArquivoComposicao(selecionado, sistemaFacade.getDataOperacao());
    }

    public AssistenteDetentorArquivoComposicao getAssistenteDetentorArquivoComposicao() {
        return assistenteDetentorArquivoComposicao;
    }

    public void setAssistenteDetentorArquivoComposicao(AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao) {
        this.assistenteDetentorArquivoComposicao = assistenteDetentorArquivoComposicao;
    }

    public AuxiliarAndamentoArquivoAtuarial getAuxiliarAndamentoArquivoAtuarial() {
        return auxiliarAndamentoArquivoAtuarial;
    }

    public void setAuxiliarAndamentoArquivoAtuarial(AuxiliarAndamentoArquivoAtuarial auxiliarAndamentoArquivoAtuarial) {
        this.auxiliarAndamentoArquivoAtuarial = auxiliarAndamentoArquivoAtuarial;
    }

    public StreamedContent getFileDownload() {
        return fileDownload;
    }

    public void setFileDownload(StreamedContent fileDownload) {
        this.fileDownload = fileDownload;
    }

    public File getZipFile() {
        return zipFile;
    }

    public void setZipFile(File zipFile) {
        this.zipFile = zipFile;
    }

    public void selecionar(ActionEvent evento) {
        selecionado = (EstudoAtuarial) evento.getComponent().getAttributes().get("objeto");
    }

    public void atualizarFinal() {
        if (auxiliarAndamentoArquivoAtuarial.getParado()) {
            FacesUtil.executaJavaScript("finalizandoProcessamento.hide()");
            FacesUtil.atualizarComponente("Formulario");
        } else {
            if (auxiliarAndamentoArquivoAtuarial.jaGerouArquivoZip()) {
                FacesUtil.executaJavaScript("finalizandoProcessamento.show()");
            }
        }
    }

    public boolean podeBaixarOuSalvarArquivo() {
        if(auxiliarAndamentoArquivoAtuarial == null){
            return false;
        }
        return auxiliarAndamentoArquivoAtuarial.getParado() == null || auxiliarAndamentoArquivoAtuarial.getParado() && auxiliarAndamentoArquivoAtuarial.getZipFile() != null;
    }
}
