package br.com.webpublico.controle.rh.rotinasanuaisemensais;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.rh.ContratoFPLote;
import br.com.webpublico.entidadesauxiliares.rh.ExportacaoPlanilhaContaCorrenteCaixa;
import br.com.webpublico.entidadesauxiliares.rh.ImportacaoContratoFP;
import br.com.webpublico.enums.DescansoSemanal;
import br.com.webpublico.enums.SituacaoVinculo;
import br.com.webpublico.enums.TipoCAGED;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.rotinasanuaismensais.ImportacaoContratoFPLoteFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@ManagedBean(name = "importacaoContratoFPLoteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "importacao-contratofp-lote", pattern = "/rh/importacao-contratofp-lote/", viewId = "/faces/rh/rotinasanuaisemensais/contratofp-lote/edita.xhtml")})
public class ImportacaoContratoFPLoteControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ImportacaoContratoFPLoteControlador.class);
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private ImportacaoContratoFPLoteFacade facade;
    private List<ContratoFPLote> contratosImportados;
    private ImportacaoContratoFP selecionado;
    @EJB
    private ConfiguracaoModalidadeContratoFPFacade configuracaoModalidadeContratoFPFacade;
    private ConverterGenerico converterJornadaDeTrabalho;
    private ConverterGenerico converterTipoRegime;
    private ConverterGenerico converterTipoPrevidencia;
    private ConverterGenerico converterCategoriaSEFIP;
    private ConverterGenerico converterTipoAdmissaoFGTS;
    private ConverterGenerico converterTipoAdmissaoSEFIP;
    private ConverterGenerico converterTipoAdmissaoRAIS;
    private ConverterGenerico converterMovimentoCAGED;
    private ConverterGenerico converterOcorrenciaSEFIP;
    private ConverterGenerico converterRetencaoIRRF;
    private ConverterGenerico converterVinculoEmpregaticio;
    private ConverterGenerico converterNaturezaRendimento;
    private ConverterGenerico converterHorarioDeTrabalho;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private RecursoFPFacade recursoFPFacade;
    @EJB
    private PlanoCargosSalariosFacade planoCargosSalariosFacade;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private MatriculaFPFacade matriculaFPFacade;
    @EJB
    private SingletonMatriculaFP singletonMatriculaFP;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    @EJB
    private CBOFacade cboFacade;

    private List<ContratoFP> contratoFPS;
    private List<ObservacaoImportacao> observacoes;


    @URLAction(mappingId = "importacao-contratofp-lote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        contratosImportados = Lists.newArrayList();
        selecionado = new ImportacaoContratoFP();
        contratoFPS = Lists.newLinkedList();
        observacoes = Lists.newLinkedList();
    }

    public ConverterGenerico getConverterJornadaDeTrabalho() {
        if (converterJornadaDeTrabalho == null) {
            converterJornadaDeTrabalho = new ConverterGenerico(JornadaDeTrabalho.class, configuracaoModalidadeContratoFPFacade.getJornadaDeTrabalhoFacade());
        }
        return converterJornadaDeTrabalho;
    }

    public ConverterGenerico getConverterTipoRegime() {
        if (converterTipoRegime == null) {
            converterTipoRegime = new ConverterGenerico(TipoRegime.class, configuracaoModalidadeContratoFPFacade.getTipoRegimeFacade());
        }
        return converterTipoRegime;
    }

    public ConverterGenerico getConverterTipoPrevidencia() {
        if (converterTipoPrevidencia == null) {
            converterTipoPrevidencia = new ConverterGenerico(TipoPrevidenciaFP.class, configuracaoModalidadeContratoFPFacade.getTipoPrevidenciaFPFacade());
        }
        return converterTipoPrevidencia;
    }

    public List<ContratoFP> getContratoFPS() {
        return contratoFPS;
    }

    public void setContratoFPS(List<ContratoFP> contratoFPS) {
        this.contratoFPS = contratoFPS;
    }

    public ConverterGenerico getConverterCategoriaSEFIP() {
        if (converterCategoriaSEFIP == null) {
            converterCategoriaSEFIP = new ConverterGenerico(CategoriaSEFIP.class, configuracaoModalidadeContratoFPFacade.getCategoriaSEFIPFacade());
        }
        return converterCategoriaSEFIP;
    }

    public ConverterGenerico getConverterTipoAdmissaoFGTS() {
        if (converterTipoAdmissaoFGTS == null) {
            converterTipoAdmissaoFGTS = new ConverterGenerico(TipoAdmissaoFGTS.class, configuracaoModalidadeContratoFPFacade.getTipoAdmissaoFGTSFacade());
        }
        return converterTipoAdmissaoFGTS;
    }

    public ConverterGenerico getConverterTipoAdmissaoSEFIP() {
        if (converterTipoAdmissaoSEFIP == null) {
            converterTipoAdmissaoSEFIP = new ConverterGenerico(TipoAdmissaoSEFIP.class, configuracaoModalidadeContratoFPFacade.getTipoAdmissaoSEFIPFacade());
        }
        return converterTipoAdmissaoSEFIP;
    }

    public ConverterGenerico getConverterTipoAdmissaoRAIS() {
        if (converterTipoAdmissaoRAIS == null) {
            converterTipoAdmissaoRAIS = new ConverterGenerico(TipoAdmissaoRAIS.class, configuracaoModalidadeContratoFPFacade.getTipoAdmissaoRAISFacade());
        }
        return converterTipoAdmissaoRAIS;
    }

    public ConverterGenerico getConverterMovimentoCAGED() {
        if (converterMovimentoCAGED == null) {
            converterMovimentoCAGED = new ConverterGenerico(MovimentoCAGED.class, configuracaoModalidadeContratoFPFacade.getMovimentoCAGEDFacade());
        }
        return converterMovimentoCAGED;
    }

    public ConverterGenerico getConverterOcorrenciaSEFIP() {
        if (converterOcorrenciaSEFIP == null) {
            converterOcorrenciaSEFIP = new ConverterGenerico(OcorrenciaSEFIP.class, configuracaoModalidadeContratoFPFacade.getOcorrenciaSEFIPFacade());
        }
        return converterOcorrenciaSEFIP;
    }

    public ConverterGenerico getConverterRetencaoIRRF() {
        if (converterRetencaoIRRF == null) {
            converterRetencaoIRRF = new ConverterGenerico(RetencaoIRRF.class, configuracaoModalidadeContratoFPFacade.getRetencaoIRRFFacade());
        }
        return converterRetencaoIRRF;
    }

    public ConverterGenerico getConverterVinculoEmpregaticio() {
        if (converterVinculoEmpregaticio == null) {
            converterVinculoEmpregaticio = new ConverterGenerico(VinculoEmpregaticio.class, configuracaoModalidadeContratoFPFacade.getVinculoEmpregaticioFacade());
        }
        return converterVinculoEmpregaticio;
    }

    public ConverterGenerico getConverterNaturezaRendimento() {
        if (converterNaturezaRendimento == null) {
            converterNaturezaRendimento = new ConverterGenerico(NaturezaRendimento.class, configuracaoModalidadeContratoFPFacade.getNaturezaRendimentoFacade());
        }
        return converterNaturezaRendimento;
    }

    public ConverterGenerico getConverterHorarioDeTrabalho() {
        if (converterHorarioDeTrabalho == null) {
            converterHorarioDeTrabalho = new ConverterGenerico(HorarioDeTrabalho.class, configuracaoModalidadeContratoFPFacade.getHorarioDeTrabalhoFacade());
        }
        return converterHorarioDeTrabalho;
    }

    public List<SelectItem> getJornadaDeTrabalho() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (JornadaDeTrabalho object : configuracaoModalidadeContratoFPFacade.getJornadaDeTrabalhoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getDescansoSemanals() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(""));
        for (DescansoSemanal obj : DescansoSemanal.values()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoRegime() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoRegime object : configuracaoModalidadeContratoFPFacade.getTipoRegimeFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (SituacaoVinculo object : SituacaoVinculo.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoPrevidenciaFp() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPrevidenciaFP object : configuracaoModalidadeContratoFPFacade.getTipoPrevidenciaFPFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getCategoriaSEFIP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (CategoriaSEFIP object : configuracaoModalidadeContratoFPFacade.getCategoriaSEFIPFacade().lista()) {
            String valor = object.toString();
            if (valor.length() > 70) {
                valor = valor.substring(0, 69);
            }
            toReturn.add(new SelectItem(object, valor));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoAdmissaoFGTS() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAdmissaoFGTS object : configuracaoModalidadeContratoFPFacade.getTipoAdmissaoFGTSFacade().lista()) {
            String valor = object.toString();
            if (valor.length() > 70) {
                valor = valor.substring(0, 69);
            }
            toReturn.add(new SelectItem(object, valor));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoAdmissaoSEFIPs() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAdmissaoSEFIP object : configuracaoModalidadeContratoFPFacade.getTipoAdmissaoSEFIPFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoAdmissaoRAIS() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAdmissaoRAIS object : configuracaoModalidadeContratoFPFacade.getTipoAdmissaoRAISFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getMovimentosCAGEDs() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (MovimentoCAGED object : configuracaoModalidadeContratoFPFacade.getMovimentoCAGEDFacade().listaMovimentoPorTipo(TipoCAGED.ADMISSAO)) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getOcorrenciasSEFIP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (OcorrenciaSEFIP obj : configuracaoModalidadeContratoFPFacade.getOcorrenciaSEFIPFacade().lista()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getRetencaoIRRF() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (RetencaoIRRF object : configuracaoModalidadeContratoFPFacade.getRetencaoIRRFFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getVinculosEmpregaticios() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (VinculoEmpregaticio object : configuracaoModalidadeContratoFPFacade.getVinculoEmpregaticioFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getNaturezaRendimentos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (NaturezaRendimento object : configuracaoModalidadeContratoFPFacade.getNaturezaRendimentoFacade().lista()) {
            String valor = object.toString();
            if (valor.length() > 70) {
                valor = valor.substring(0, 69);
            }
            toReturn.add(new SelectItem(object, valor));
        }
        return toReturn;
    }

    public List<SelectItem> getHorarioDeTrabalho() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (HorarioDeTrabalho object : configuracaoModalidadeContratoFPFacade.getHorarioDeTrabalhoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasOrganizacionais(String parte) {
        return hierarquiaOrganizacionalFacade.filtraPorNivel(parte.trim(), "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), sistemaFacade.getDataOperacao());
    }

    public List<SelectItem> getRecursosFP() {
        List<SelectItem> toRetorno = new ArrayList<>();
        List<RecursoFP> recursoFPs = Lists.newLinkedList();
        if (selecionado != null && selecionado.getHierarquiaOrganizacional() != null) {
            recursoFPs = recursoFPFacade.retornaRecursoFPDo2NivelDeHierarquia(selecionado.getHierarquiaOrganizacional(), sistemaFacade.getDataOperacao());
        } else {
            recursoFPs.add(new RecursoFP(1L, "0", "A Hierarquia Organizacional Administrativa não foi selecionada!"));
        }
        for (RecursoFP fp : recursoFPs) {
            toRetorno.add(new SelectItem(fp, fp.toString()));
        }
        return toRetorno;
    }


    public void limpar() {
        novo();
    }

    public void importar(FileUploadEvent event) {
        try {
            validarImportacaoArquivo();
            contratosImportados = Lists.newLinkedList();
            UploadedFile file = event.getFile();
            facade.importarPessoas(contratosImportados, file.getInputstream());
            logger.debug("Iniciando parametrização dos dados importados.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro durante a importação do arquivo: " + ex.getMessage());
        }
    }

    private void verificarPessoasAdicionadas(List<ExportacaoPlanilhaContaCorrenteCaixa> pessoas, List<ExportacaoPlanilhaContaCorrenteCaixa> pessoasExportacao) {
        List<ExportacaoPlanilhaContaCorrenteCaixa> lista = Lists.newLinkedList();

        for (ExportacaoPlanilhaContaCorrenteCaixa exportacaoPlanilhaContaCorrenteCaixa : pessoas) {
            if (!isCpfAdicionado(exportacaoPlanilhaContaCorrenteCaixa.getCpf(), pessoasExportacao)) {
                lista.add(exportacaoPlanilhaContaCorrenteCaixa);
            }
        }
        pessoasExportacao.addAll(lista);
        if (pessoasExportacao.isEmpty()) {
            pessoasExportacao.addAll(pessoas);
        }

    }

    private boolean isCpfAdicionado(String cpf, List<ExportacaoPlanilhaContaCorrenteCaixa> pessoas) {
        for (ExportacaoPlanilhaContaCorrenteCaixa pessoa : pessoas) {
            if (pessoa.getCpf().equals(cpf)) {
                return true;
            }
        }

        return false;
    }


    private void validarImportacaoArquivo() {

    }


    private String converterString(String descricao) {
        try {
            return Integer.valueOf(descricao).toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public List<ContratoFPLote> getContratosImportados() {
        return contratosImportados;
    }

    public void setContratosImportados(List<ContratoFPLote> contratosImportados) {
        this.contratosImportados = contratosImportados;
    }

    public enum CampoImportacao {
        NUMERO("Número"),
        MATRICULA("Matrícula"),
        NOME("Nome"),
        CONTRATO("Contrato"),
        CPF("CPF"),
        ADMISSAO("Admissão"),
        VIGENCIA("Admissão"),
        CARGO("Cargo"),
        CARGA_HORARIA("Carga Horária"),
        CODIGO_LOTACAO_FOLHA("Código Lotação Folha"),
        CODIGO_LOTACAO_EXERCICIO("Código da Lotação Exercício"),
        CODIGO("Código");

        private String descricao;

        CampoImportacao(String descricao) {
            this.descricao = descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public ImportacaoContratoFP getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ImportacaoContratoFP selecionado) {
        this.selecionado = selecionado;
    }

    public void limpa() {
        if (selecionado.getPlanoCargosSalarios() == null) {
            selecionado.setCategoriaPCSfilha(null);
        }
        if (selecionado.getCategoriaPCSfilha() == null) {
            selecionado.setProgressaoPCSPai(null);
        }
        if (selecionado.getProgressaoPCSPai() == null) {
            selecionado.setProgressaoPCS(null);
            selecionado.setCategoriaPCS(null);
        }
    }

    public List<SelectItem> getPlanos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (PlanoCargosSalarios object : planoCargosSalariosFacade.getPlanosPorQuadro(sistemaFacade.getDataOperacao())) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getCategoriaPCS() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado != null && selecionado.getPlanoCargosSalarios() != null) {

            for (CategoriaPCS object : categoriaPCSFacade.recuperaCategoriasNoEnquadramentoFuncionalVigente(selecionado.getPlanoCargosSalarios(), sistemaFacade.getDataOperacao())) {

                CategoriaPCS cat = categoriaPCSFacade.recuperar(object.getId());
                String nome = cat.getDescricao();
                if (cat.getFilhos().isEmpty()) {
                    while ((cat.getSuperior() != null) && (!cat.equals(cat.getSuperior()))) {
                        cat = cat.getSuperior();
                        nome = cat.getDescricao() + "/" + object.getCodigo() + " - " + nome;
                    }
                    toReturn.add(new SelectItem(object, nome));
                }
            }

        }

        return Util.ordenaSelectItem(toReturn);
    }

    public List<SelectItem> getProgressaoPCS() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado != null && selecionado.getPlanoCargosSalarios() != null && selecionado.getCategoriaPCSfilha() != null) {
            List<ProgressaoPCS> listaProgressao = progressaoPCSFacade.getRaizPorPlanoECategoriaVigenteNoEnquadramento(selecionado.getPlanoCargosSalarios(), selecionado.getCategoriaPCSfilha(), sistemaFacade.getDataOperacao());
            if (listaProgressao != null && !listaProgressao.isEmpty()) {
                UtilRH.ordenarProgressoes(listaProgressao);
                for (ProgressaoPCS object : listaProgressao) {
                    ProgressaoPCS prog = progressaoPCSFacade.recuperar(object.getId());
                    toReturn.add(new SelectItem(prog, (prog.getCodigo() != null ? prog.getCodigo() + " - " : "") + prog.getDescricao()));
                }
            }

        }
        return toReturn;
    }

    public List<SelectItem> getProgressaoPCSApenasFilhos() {

        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado != null && selecionado.getPlanoCargosSalarios() != null && selecionado.getProgressaoPCSPai() != null) {
            List<ProgressaoPCS> listaProgressao = progressaoPCSFacade.getFilhosDe(selecionado.getProgressaoPCSPai(), selecionado.getPlanoCargosSalarios());
            UtilRH.ordenarProgressoes(listaProgressao);
            for (ProgressaoPCS object : listaProgressao) {
                String nome = "";
                ProgressaoPCS prog = progressaoPCSFacade.recuperar(object.getId());
                nome = prog.getDescricao();
                toReturn.add(new SelectItem(object, nome));
            }

        }
        return toReturn;
    }

    public void buscarEnquadramento() {
        if (selecionado != null) {
            if (selecionado.getCategoriaPCSfilha() != null && selecionado.getProgressaoPCS() != null) {
                definirCategoriaPcs();
                Date dataParametro = sistemaFacade.getDataOperacao();
                EnquadramentoPCS enquadramento = enquadramentoPCSFacade.recuperaValor(selecionado.getCategoriaPCSfilha(), selecionado.getProgressaoPCS(), dataParametro);
                if (enquadramento != null && enquadramento.getId() != null) {
                    selecionado.setEnquadramento(enquadramento);
                } else {
                    selecionado.setEnquadramento(new EnquadramentoPCS());
                }
            }
        }
    }

    public List<CBO> completarCbo(String parte) {
        if (selecionado != null && selecionado.getCargo() == null) {
            FacesUtil.addOperacaoNaoPermitida("É necessário informar um cargo no campo anterior para poder listar os CBOs.");
            return null;
        }
        return cboFacade.listarFiltrandoPorCargo(parte.trim(), selecionado.getCargo());
    }

    public void limparCbo() {
        selecionado.setCbo(null);
    }

    public void iniciarCriacaoDosContratos() {
        try {
            validarCamposObrigatorios();
            criarContratos();
        } catch (ValidacaoException val) {
            FacesUtil.printAllFacesMessages(val.getAllMensagens());
        } catch (Exception e) {
            logger.error("Erro", e);
        }
    }

    private void criarContratos() {
        List<ContratoFP> contratos = Lists.newLinkedList();
        contratoFPS = Lists.newLinkedList();
        for (ContratoFPLote importado : contratosImportados) {
            ContratoFP contratoFP = new ContratoFP();
            try {
                validarContratoDaImportacao(importado);
                criarNovoContrato(contratos, contratoFP, importado);
            } catch (ValidacaoException val) {
                logger.debug("Validação " + val.getMessage());
                adicionarObservacaoImportacao(val, importado);
            }
        }

    }

    private void criarNovoContrato(List<ContratoFP> contratos, ContratoFP contratoFP, ContratoFPLote importado) {
        PessoaFisica pessoaFisica = importado.getPessoaFisica();
        preencherMatriculaContrato(contratoFP, pessoaFisica);
        preencherDadosBasicosContrato(contratoFP, importado);
        preencherPrevidencia(contratoFP, importado);
        preencherLotacaoFuncional(contratoFP, importado);
        preencherSituacaoFuncional(contratoFP, importado);
        preencherVinculoDeContrato(contratoFP, importado);
        preencherInformacaoDeDeclaracoes(contratoFP);
        preencherDadosCargo(contratoFP, importado);
        preencherRecursoDoVinculo(contratoFP, importado);
        preencherEnquadramentoFuncional(contratoFP, importado);
        contratos.add(contratoFP);
        contratoFPS.add(contratoFP);
    }

    private void preencherEnquadramentoFuncional(ContratoFP contratoFP, ContratoFPLote importado) {
        contratoFP.setEnquadramentos(Lists.<EnquadramentoFuncional>newLinkedList());
        EnquadramentoFuncional enquadramentoFuncional = new EnquadramentoFuncional();
        enquadramentoFuncional.setInicioVigencia(importado.getAdmissao() != null ? importado.getAdmissao() : selecionado.getInicioVigencia());
        enquadramentoFuncional.setContratoServidor(contratoFP);
        enquadramentoFuncional.setCategoriaPCS(selecionado.getCategoriaPCS());
        enquadramentoFuncional.setProgressaoPCS(selecionado.getProgressaoPCS());
        enquadramentoFuncional.setDataCadastro(sistemaFacade.getDataOperacao());
        contratoFP.getEnquadramentos().add(enquadramentoFuncional);
    }

    private void preencherRecursoDoVinculo(ContratoFP contratoFP, ContratoFPLote importado) {
        RecursoDoVinculoFP rec = new RecursoDoVinculoFP();
        rec.setInicioVigencia(importado.getAdmissao() != null ? importado.getAdmissao() : selecionado.getInicioVigencia());
        rec.setVinculoFP(contratoFP);
        String codigo = tratarCodigoRecursoFP(importado.getCodigoLotacaoFolha());
        RecursoFP recurso = recursoFPFacade.buscarRecursosPorCodigo(codigo);
        rec.setRecursoFP(recurso != null ? recurso : selecionado.getRecursoFP());
        contratoFP.getRecursosDoVinculoFP().add(rec);
    }

    private String tratarCodigoRecursoFP(String codigoLotacaoFolha) {
        codigoLotacaoFolha = StringUtil.retornaApenasNumeros(codigoLotacaoFolha);
        if (codigoLotacaoFolha.length() > 2) {
            codigoLotacaoFolha = codigoLotacaoFolha.substring(2, codigoLotacaoFolha.length());
        }
        return codigoLotacaoFolha;
    }

    private void preencherDadosCargo(ContratoFP contratoFP, ContratoFPLote importado) {
        contratoFP.setCargos(Lists.<ContratoFPCargo>newLinkedList());

        contratoFP.setCargo(selecionado.getCargo());
        ContratoFPCargo contratoFPCargo = new ContratoFPCargo();
        contratoFPCargo.setInicioVigencia(importado.getAdmissao() != null ? importado.getAdmissao() : selecionado.getInicioVigencia());
        contratoFPCargo.setCargo(selecionado.getCargo());
        contratoFPCargo.setCbo(selecionado.getCbo());
        contratoFPCargo.setContratoFP(contratoFP);
        contratoFP.getCargos().add(contratoFPCargo);
    }

    private void preencherInformacaoDeDeclaracoes(ContratoFP contratoFP) {
        contratoFP.setMovimentoCAGED(selecionado.getMovimentoCAGED());
        contratoFP.setNaturezaRendimento(selecionado.getNaturezaRendimento());
        contratoFP.setTipoAdmissaoFGTS(selecionado.getTipoAdmissaoFGTS());
        contratoFP.setRetencaoIRRF(selecionado.getRetencaoIRRF());
        contratoFP.setTipoAdmissaoRAIS(selecionado.getTipoAdmissaoRAIS());
        contratoFP.setSefip(selecionado.getSefip());
        contratoFP.setCategoriaSEFIP(selecionado.getCategoriaSEFIP());
        contratoFP.setTipoAdmissaoSEFIP(selecionado.getTipoAdmissaoSEFIP());
        contratoFP.setOcorrenciaSEFIP(selecionado.getOcorrenciaSEFIP());
        contratoFP.setVinculoEmpregaticio(selecionado.getVinculoEmpregaticio());
    }

    private void preencherVinculoDeContrato(ContratoFP contratoFP, ContratoFPLote importado) {
        ContratoVinculoDeContrato contratoVinculoDeContrato = new ContratoVinculoDeContrato();
        contratoVinculoDeContrato.setInicioVigencia(importado.getAdmissao() != null ? importado.getAdmissao() : selecionado.getInicioVigencia());
        contratoVinculoDeContrato.setVinculoDeContratoFP(selecionado.getVinculoDeContratoFP());
        contratoVinculoDeContrato.setContratoFP(contratoFP);
        contratoVinculoDeContrato.setDataRegistro(sistemaFacade.getDataOperacao());
        contratoFP.getContratoVinculoDeContratos().add(contratoVinculoDeContrato);
    }

    private void preencherSituacaoFuncional(ContratoFP contratoFP, ContratoFPLote importado) {
        SituacaoContratoFP situacaoContratoFP = new SituacaoContratoFP();
        situacaoContratoFP.setContratoFP(contratoFP);
        situacaoContratoFP.setInicioVigencia(importado.getAdmissao() != null ? importado.getAdmissao() : selecionado.getInicioVigencia());
        situacaoContratoFP.setSituacaoFuncional(situacaoFuncionalFacade.recuperaCodigo(SituacaoFuncional.ATIVO_PARA_FOLHA));
        contratoFP.getSituacaoFuncionals().add(situacaoContratoFP);
    }

    private void preencherLotacaoFuncional(ContratoFP contratoFP, ContratoFPLote importado) {

        HorarioContratoFP horarioContratoFP = new HorarioContratoFP();
        horarioContratoFP.setInicioVigencia(importado.getAdmissao() != null ? importado.getAdmissao() : selecionado.getInicioVigencia());
        horarioContratoFP.setHorarioDeTrabalho(selecionado.getHorarioDeTrabalho());

        LotacaoFuncional lotacaoFuncional = new LotacaoFuncional();
        lotacaoFuncional.setInicioVigencia(importado.getAdmissao() != null ? importado.getAdmissao() : selecionado.getInicioVigencia());
        List<HierarquiaOrganizacional> hierarquiaOrganizacionals = hierarquiaOrganizacionalFacade.filtrandoHierarquiaHorganizacionalTipo(importado.getCodigoLotacaoExercicio(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), sistemaFacade.getDataOperacao());
        if (hierarquiaOrganizacionals.isEmpty()) {
            lotacaoFuncional.setUnidadeOrganizacional(selecionado.getUnidadeOrganizacional());
        } else {
            lotacaoFuncional.setUnidadeOrganizacional(hierarquiaOrganizacionals.get(0).getSubordinada());
        }
        lotacaoFuncional.setHorarioContratoFP(horarioContratoFP);
        lotacaoFuncional.setDataRegistro(sistemaFacade.getDataOperacao());
        lotacaoFuncional.setVinculoFP(contratoFP);
        contratoFP.getLotacaoFuncionals().add(lotacaoFuncional);
    }


    private void preencherPrevidencia(ContratoFP contratoFP, ContratoFPLote importado) {
        PrevidenciaVinculoFP previdenciaVinculoFP = new PrevidenciaVinculoFP();
        previdenciaVinculoFP.setInicioVigencia(importado.getAdmissao() != null ? importado.getAdmissao() : selecionado.getInicioVigencia());
        previdenciaVinculoFP.setContratoFP(contratoFP);
        previdenciaVinculoFP.setTipoPrevidenciaFP(selecionado.getTipoPrevidenciaFP());
        contratoFP.getPrevidenciaVinculoFPs().add(previdenciaVinculoFP);
    }

    private void preencherDadosBasicosContrato(ContratoFP contratoFP, ContratoFPLote importado) {
        contratoFP.setModalidadeContratoFP(selecionado.getModalidadeContrato());
        contratoFP.setInicioVigencia(importado.getAdmissao() != null ? importado.getAdmissao() : selecionado.getInicioVigencia());
        contratoFP.setDataAdmissao(importado.getAdmissao() != null ? importado.getAdmissao() : selecionado.getInicioVigencia());
        contratoFP.setDataExercicio(importado.getAdmissao() != null ? importado.getAdmissao() : selecionado.getInicioVigencia());
        contratoFP.setDataNomeacao(importado.getAdmissao() != null ? importado.getAdmissao() : selecionado.getInicioVigencia());
        contratoFP.setDataRegistro(sistemaFacade.getDataOperacao());
        contratoFP.setJornadaDeTrabalho(selecionado.getJornadaDeTrabalho());
        contratoFP.setDescansoSemanal(selecionado.getDescansoSemanal());
        contratoFP.setTipoRegime(selecionado.getTipoRegime());
        contratoFP.setSituacaoVinculo(selecionado.getSituacaoVinculo());
        contratoFP.setAtoLegal(selecionado.getAtoLegal());
        contratoFP.setUnidadeOrganizacional(selecionado.getUnidadeOrganizacional());
    }

    private void preencherMatriculaContrato(ContratoFP contratoFP, PessoaFisica pessoaFisica) {
        Long id = matriculaFPFacade.buscarMatriculaDaPessoa(pessoaFisica.getId());
        if (id != null) {
            MatriculaFP mat = matriculaFPFacade.recuperar(id);
            contratoFP.setMatriculaFP(mat);
            List<ContratoFP> contratoSalvos = contratoFPFacade.recuperaContratosPorMatriculaFP(mat);
            if (!contratoSalvos.isEmpty()) {
                throw new ValidacaoException("Já existe um ContratoFP vigente para a matrícula " + mat);
            }
            contratoFP.setNumero(contratoFPFacade.retornaCodigo(mat));
        } else {
            MatriculaFP mat = new MatriculaFP();
            mat.setPessoa(pessoaFisica);
            mat.setUnidadeMatriculado(selecionado.getUnidadeOrganizacional());
            mat.setMatricula(singletonMatriculaFP.getProximaMatricula() + "");
            contratoFP.setMatriculaFP(mat);
            contratoFP.setNumero("1");
        }
    }

    private void adicionarObservacaoImportacao(ValidacaoException val, ContratoFPLote importado) {
        observacoes.add(new ObservacaoImportacao(importado, val.getMessage()));
    }

    private void validarContratoDaImportacao(ContratoFPLote importado) {
        if (importado.getPessoaFisica() == null) {
            throw new ValidacaoException("Pessoa Inexistente para o CPF " + importado.getCpf());
        }
    }

    private void validarCamposObrigatorios() {
        ValidacaoException val = new ValidacaoException();
        try {
            for (Field field : selecionado.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object v = field != null ? field.get(selecionado) : null;
                if (v == null) {
                    val.adicionarMensagemDeCampoObrigatorio("O campo " + field.getName() + " deve ser preenchido.");
                }
            }
        } catch (IllegalAccessException e) {
            logger.error("erro", e);
        }
        val.lancarException();
    }

    public void definirUnidade() {
        if (selecionado != null && selecionado.getHierarquiaOrganizacional() != null) {
            selecionado.setUnidadeOrganizacional(selecionado.getHierarquiaOrganizacional().getSubordinada());
        }
    }

    public void definirCategoriaPcs() {
        if (selecionado != null && selecionado.getCategoriaPCSfilha() != null) {
            selecionado.setCategoriaPCS(selecionado.getCategoriaPCSfilha());
        }
    }

    public void salvarContratos() {
        try {
            contratoFPFacade.salvarContratosEmLote(contratoFPS);
            FacesUtil.addOperacaoRealizada("Contratos Salvos com Sucesso");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            logger.error("Erro: ", e);
        }

    }

    public RecursoFP buscarRecursoFP(ContratoFP contratoFP) {
        for (RecursoDoVinculoFP recursoDoVinculoFP : contratoFP.getRecursosDoVinculoFP()) {
            return recursoDoVinculoFP.getRecursoFP();
        }
        return null;
    }

    public UnidadeOrganizacional buscarLotacaoFuncional(ContratoFP contratoFP) {
        for (LotacaoFuncional lotacao : contratoFP.getLotacaoFuncionals()) {
            return lotacao.getUnidadeOrganizacional();
        }
        return null;
    }

    public List<ObservacaoImportacao> getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(List<ObservacaoImportacao> observacoes) {
        this.observacoes = observacoes;
    }

    public class ObservacaoImportacao {
        private ContratoFPLote contrato;
        private String mensagem;

        public ObservacaoImportacao(ContratoFPLote contrato, String mensagem) {
            this.contrato = contrato;
            this.mensagem = mensagem;
        }

        public ContratoFPLote getContrato() {
            return contrato;
        }

        public void setContrato(ContratoFPLote contrato) {
            this.contrato = contrato;
        }

        public String getMensagem() {
            return mensagem;
        }

        public void setMensagem(String mensagem) {
            this.mensagem = mensagem;
        }
    }

}
