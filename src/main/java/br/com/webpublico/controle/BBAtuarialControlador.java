package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AuxiliarAndamentoBBAtuarial;
import br.com.webpublico.entidadesauxiliares.AuxiliarConteudoBBAtuarial;
import br.com.webpublico.entidadesauxiliares.WorkBookBBAtuarial;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.*;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@ManagedBean(name = "bbAtuarialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-bb-atuarial", pattern = "/bb-atuarial/novo/", viewId = "/faces/rh/administracaodepagamento/bbatuarial/edita.xhtml"),
    @URLMapping(id = "log-bb-atuarial", pattern = "/bb-atuarial/acompanhamento/", viewId = "/faces/rh/administracaodepagamento/bbatuarial/log.xhtml")
})
public class BBAtuarialControlador extends PrettyControlador<BBAtuarial> implements Serializable, CRUD {

    private static final Long TIPO_REGIME_ESTATUTARIO = 2L;
    private static final String TIPO_PREVIDENCIA_RBPREV = "11";
    @EJB
    private BBAtuarialFacade bbAtuarialFacade;
    private FileOutputStream fout = null;
    private HSSFWorkbook pastaDeTrabalho;
    private HSSFSheet sheet;
    private HSSFRow row;
    private transient FileInputStream fis = null;
    private File file = null;
    private List<VinculoFP> vinculos;
    @EJB
    private DocumentoPessoalFacade documentoPessoaFacade;
    private Map<String, File> files;
    private File zipFile;
    private List<String> erros;
    private String arquivoDoMomento;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacadeOLD;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private DependenteVinculoFPFacade dependenteVinculoFPFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    private Entidade entidade;
    private ConverterGenerico entidadeConverter;

    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ResponsavelUnidadeFacade responsavelUnidadeFacade;
    private ContratoFP responsavelUnidade;
    private PessoaJuridica pessoaJuridica;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    private DecimalFormat df = new DecimalFormat("#,###,##0.00");
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private AverbacaoTempoServicoFacade averbacaoTempoServicoFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    private Integer ano;
    @EJB
    private ContratoVinculoDeContratoFacade contratoVinculoDeContratoFacade;
    @EJB
    private TipoDependenteFacade tipoDependenteFacade;

    private AuxiliarAndamentoBBAtuarial auxiliarAndamentoBBAtuarial;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    @EJB
    private PensionistaFacade pensionistaFacade;
    @EJB
    private DependenteFacade dependenteFacade;
    @EJB
    private VinculoDeContratoFPFacade vinculoDeContratoFPFacade;
    private List<Future<AuxiliarAndamentoBBAtuarial>> futures;
    private StreamedContent fileDownload;

    public BBAtuarialControlador() {
        super(BBAtuarial.class);
    }

    @Override
    public BBAtuarialFacade getFacede() {
        return bbAtuarialFacade;
    }

    public List<TipoArquivoAtuarial> getBbAtuarialTipoArquivos() {
        return Arrays.asList(TipoArquivoAtuarial.values());
    }

    public List<String> getErros() {
        return erros;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
        if (entidade != null) {
            pessoaJuridica = pessoaJuridicaFacade.recuperar(entidade.getPessoaJuridica().getId());
        }
    }

    public List<SelectItem> entidades() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Entidade object : entidadeFacade.listaEntidadesPorTipo(TipoEntidade.FUNDO_PUBLICO)) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public ConverterGenerico getEntidadeConverter() {
        if (entidadeConverter == null) {
            entidadeConverter = new ConverterGenerico(Entidade.class, entidadeFacade);
        }
        return entidadeConverter;
    }

    public ConverterAutoComplete getResponsavelConverter() {
        return new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
    }

    public ContratoFP getResponsavelUnidade() {
        return responsavelUnidade;
    }

    public void setResponsavelUnidade(ContratoFP responsavelUnidade) {
        this.responsavelUnidade = responsavelUnidade;
    }

    @URLAction(mappingId = "novo-bb-atuarial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataRegistro(new Date());
        selecionado.setDataReferencia(new Date());
        selecionado.setUsuarioSistema(usuarioLogado());
        vinculos = new ArrayList<>();
        files = new HashMap<>();
        erros = new ArrayList<>();
        auxiliarAndamentoBBAtuarial = new AuxiliarAndamentoBBAtuarial();
    }

    public void confirmarGeracaoArquivo() {
        if (!validaGeracaoArquivo()) {
            return;
        }

        auxiliarAndamentoBBAtuarial = new AuxiliarAndamentoBBAtuarial();
        auxiliarAndamentoBBAtuarial.iniciarProcesso();
        auxiliarAndamentoBBAtuarial.setBbAtuarial(selecionado);
        carregarIdsArquivosSelecionados();
        if (tudoEmBranco()) {
            FacesUtil.addOperacaoNaoRealizada("Não foram localizados registros para serem processados na data informada. Por favor, verifique as informações e tente novamente.");
            auxiliarAndamentoBBAtuarial.pararProcessamento();
            return;
        }

        Web.poeNaSessao("BB-ATUARIAL", selecionado);
        Web.poeNaSessao("AUX-BB-ATUARIAL", auxiliarAndamentoBBAtuarial);
        FacesUtil.redirecionamentoInterno("/bb-atuarial/acompanhamento/");
    }

    @URLAction(mappingId = "log-bb-atuarial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void log() {
        auxiliarAndamentoBBAtuarial = (AuxiliarAndamentoBBAtuarial) Web.pegaDaSessao("AUX-BB-ATUARIAL");
        selecionado = (BBAtuarial) Web.pegaDaSessao("BB-ATUARIAL");
        if (auxiliarAndamentoBBAtuarial == null || selecionado == null) {
            FacesUtil.redirecionamentoInterno("/bb-atuarial/novo/");
            return;
        }
        auxiliarAndamentoBBAtuarial.setBbAtuarial(selecionado);
    }

    private void carregarIdsArquivosSelecionados() {
        auxiliarAndamentoBBAtuarial.setTotal(0);
        for (String obj : selecionado.getTiposArquivoBBAtuarial()) {
            TipoArquivoAtuarial tipoArquivoAtuarial = TipoArquivoAtuarial.valueOf(obj);
            if (tipoArquivoAtuarial.equals(TipoArquivoAtuarial.SERVIDORES_ATIVOS)) {
                selecionado.setIdsServidoresAtivos(contratoFPFacade.buscarServidoresAtivosArquivoAtuarial(selecionado.getDataReferencia()));
                auxiliarAndamentoBBAtuarial.setTotal(auxiliarAndamentoBBAtuarial.getTotal() + selecionado.getIdsServidoresAtivos().size());

//                selecionado.setIdsServidoresAtivos(selecionado.getIdsServidoresAtivos().subList(0,50));
//                auxiliarAndamentoBBAtuarial.setTotal(50);


                auxiliarAndamentoBBAtuarial.criarWorkBookServidoresAtivos();
            }
            if (tipoArquivoAtuarial.equals(TipoArquivoAtuarial.APOSENTADOS)) {
                selecionado.setIdsAposentados(aposentadoriaFacade.buscarAposentadoriasArquivoAtuarial(selecionado.getDataReferencia()));
                auxiliarAndamentoBBAtuarial.setTotal(auxiliarAndamentoBBAtuarial.getTotal() + selecionado.getIdsAposentados().size());
                auxiliarAndamentoBBAtuarial.criarWorkBookAposentados();
            }
//            if (tipoArquivoAtuarial.equals(TipoArquivoAtuarial.DEPENDENTES)) {
//                selecionado.setIdsDependentes(dependenteFacade.recuperarDependentesBBAtuarial(selecionado.getDataReferencia()));
//                auxiliarAndamentoBBAtuarial.setTotal(auxiliarAndamentoBBAtuarial.getTotal() + selecionado.getIdsDependentes().size());
//                auxiliarAndamentoBBAtuarial.criarWorkBookDependentes();
//            }
            if (tipoArquivoAtuarial.equals(TipoArquivoAtuarial.PENSIONISTAS)) {
                selecionado.setIdsPensionistas(pensionistaFacade.buscarPensionistasArquivoAtuarial(selecionado.getDataReferencia()));
                auxiliarAndamentoBBAtuarial.setTotal(auxiliarAndamentoBBAtuarial.getTotal() + selecionado.getIdsPensionistas().size());
                auxiliarAndamentoBBAtuarial.criarWorkBookPensionistas();
            }
        }
    }

    public void iniciarGeracaoBBAtuarial() {
        try {
            futures = Lists.newArrayList();

            String agora = Util.dateHourToString(new Date());
            auxiliarAndamentoBBAtuarial.getLog().add(agora + " - AGUARDE... RECUPERANDO INFORMAÇÕES PARA GERAÇÃO DO ARQUIVO" + "<br/>");

            // SERVIDORES ATIVOS
            if (!CollectionUtils.isEmpty(selecionado.getIdsServidoresAtivos())) {
                for (List<Long> quebra : bbAtuarialFacade.particionarEmDez(selecionado.getIdsServidoresAtivos())) {
                    futures.add(bbAtuarialFacade.getConteudoArquivoServidoresAtivos(auxiliarAndamentoBBAtuarial, quebra));
                }
            }

            // APOSENTADOS
            if (!CollectionUtils.isEmpty(selecionado.getIdsAposentados())) {
                for (List<Long> quebra : bbAtuarialFacade.particionarEmDez(selecionado.getIdsAposentados())) {
                    futures.add(bbAtuarialFacade.getConteudoArquivoAposentados(auxiliarAndamentoBBAtuarial, quebra));
                }
            }

            // DEPENDENTES
            if (!CollectionUtils.isEmpty(selecionado.getIdsDependentes())) {
                for (List<Long> quebra : bbAtuarialFacade.particionarEmDez(selecionado.getIdsDependentes())) {
                    futures.add(bbAtuarialFacade.getConteudoArquivoDependentes(auxiliarAndamentoBBAtuarial, quebra));
                }
            }

            // PENSIONISTAS
            if (!CollectionUtils.isEmpty(selecionado.getIdsPensionistas())) {
                for (List<Long> quebra : bbAtuarialFacade.particionarEmDez(selecionado.getIdsPensionistas())) {
                    futures.add(bbAtuarialFacade.getConteudoArquivoPensionistas(auxiliarAndamentoBBAtuarial, quebra));
                }
            }
        } catch (RuntimeException re) {
            logger.debug(re.getMessage());
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), re.getMessage());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    private boolean tudoEmBranco() {
        return CollectionUtils.isEmpty(selecionado.getIdsServidoresAtivos()) && CollectionUtils.isEmpty(selecionado.getIdsAposentados()) && CollectionUtils.isEmpty(selecionado.getIdsPensionistas()) && CollectionUtils.isEmpty(selecionado.getIdsDependentes());
    }

//    public void gerarArquivosAntigo() {
//        if (!validaGeracaoArquivo()) {
//            return;
//        }
//
//        DateTime dateTime = new DateTime(selecionado.getDataReferencia());
//        ano = dateTime.getYear();
//
//        for (String obj : tiposSelecionados) {
//            TipoArquivoAtuarial bbAtuarialTipoArquivo = TipoArquivoAtuarial.valueOf(obj);
//            if (bbAtuarialTipoArquivo.equals(TipoArquivoAtuarial.SERVIDORES_ATIVOS)) {
//                arquivoDoMomento = "Arquivo (Servidor Ativos): ";
//                geraArquivoServidoresAtivos();
//            }
//            if (bbAtuarialTipoArquivo.equals(TipoArquivoAtuarial.APOSENTADOS)) {
//                arquivoDoMomento = "Arquivo (Benefícios Concedidos): ";
//                geraArquivoAposentados();
//            }
//            if (bbAtuarialTipoArquivo.equals(TipoArquivoAtuarial.DEPENDENTES)) {
//                arquivoDoMomento = "Arquivo (Dependentes): ";
//                geraArquivoDependentes();
//            }
//            if (bbAtuarialTipoArquivo.equals(TipoArquivoAtuarial.PENSIONISTAS)) {
//                arquivoDoMomento = "Arquivo (Pensionistas): ";
//                geraArquivoDependentes();
//            }
//        }
//        if (!files.isEmpty()) {
//            addToZip();
//            salvarArquivoBBAtuarial();
//            deletarArquivosTemporarios();
//        }
//    }

    private boolean validaGeracaoArquivo() {
        boolean valida = true;

        if (CollectionUtils.isEmpty(selecionado.getTiposArquivoBBAtuarial())) {
            FacesUtil.addOperacaoNaoPermitida("Selecione ao menos um arquivo que deseja gerar.");
            return false;
        }

        if (selecionado.getDataReferencia() == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("Informe a data de referência.");
        } else if (selecionado.getDataReferencia().after(new Date())) {
            valida = false;
            FacesUtil.addOperacaoNaoPermitida("A data de referência não pode ser posterior a data de hoje.");
        }

        return valida;
    }

    private boolean validaVinculo(VinculoFP vinculoFP) {
        boolean valida = true;

        if (vinculoFP.getMatriculaFP() == null) {
            erros.add(arquivoDoMomento + "O vínculo " + vinculoFP + " não possui Matrícula FP");
            valida = false;
        } else if (vinculoFP.getMatriculaFP().getPessoa() == null) {
            erros.add(arquivoDoMomento + "A Matrícula do vínculo " + vinculoFP + " não possui Pessoa");
            valida = false;
        } else {
            if (vinculoFP.getMatriculaFP().getPessoa().getNome() == null) {
                erros.add(arquivoDoMomento + "A pessoa do vínculo " + vinculoFP + " não possui nome");
                valida = false;
            }
            if (vinculoFP.getMatriculaFP().getPessoa().getCpf() == null) {
                erros.add(arquivoDoMomento + "A pessoa do vínculo" + vinculoFP + "não possui nome");
                valida = false;
            }
            if (vinculoFP.getMatriculaFP().getPessoa().getDataNascimento() == null) {
                erros.add(arquivoDoMomento + "A pessoa do vínculo " + vinculoFP + " não possui data de nascimento");
                valida = false;
            }
        }

        return valida;
    }

    private HSSFCell criaCell(HSSFRow row, Integer posicao) {
        return row.getCell(posicao) == null ? row.createCell(posicao) : row.getCell(posicao);
    }

    private HSSFCell criaCell(HSSFRow row, Integer posicao, HSSFCellStyle estilo) {
        HSSFCell celula = row.getCell(posicao) == null ? row.createCell(posicao) : row.getCell(posicao);
        celula.setCellStyle(estilo);
        return celula;
    }

    private HSSFRow criaRow(HSSFSheet sheet, Integer linha) {
        return sheet.getRow(linha) == null ? sheet.createRow(linha) : sheet.getRow(linha);
    }

    private void salvarArquivoBBAtuarial() {
        Arquivo arquivo = new Arquivo();
        arquivo.setMimeType("application/zip");
        arquivo.setNome(DataUtil.getDataFormatada(new Date(), "ddMMyyyy") + "_BBAtuarial.zip");
        arquivo.setDescricao(DataUtil.getDataFormatada(new Date(), "ddMMyyyy") + "_BBAtuarial");
        arquivo.setTamanho(zipFile.getTotalSpace());
        selecionado.setArquivo(arquivo);
        selecionado.setSequencia(bbAtuarialFacade.ultimoNumeroSequenciaMaisUm());

        selecionado = bbAtuarialFacade.salvarRetornando(selecionado, fis);
        FacesUtil.addInfo("", "Registro salvo com sucesso!");

        selecionado.setArquivo(arquivoFacade.recuperaDependencias(selecionado.getArquivo().getId()));
    }


    public void deletarArquivosTemporarios() {
        try {
            fis.close();
        } catch (IOException ioe) {
            FacesUtil.addError("Erro!", "Problema ao deletar os arquivo temporários, contacte um administrador.");
        }


        if (zipFile.exists()) {
            zipFile.delete();
            for (Map.Entry<String, File> m : files.entrySet()) {
                m.getValue().delete();
            }
//            fileDownload = null;
        }
    }

    private void geraArquivoServidoresAtivos() {
        try {
            vinculos.clear();
//            vinculos.addAll(contratoFPFacade.buscarServidoresAtivosArquivoAtuarial(TIPO_REGIME_ESTATUTARIO, selecionado.getDataReferencia()));

            if (vinculos == null || vinculos.isEmpty()) {
                FacesUtil.addError("", "O arquivo de Servidores Ativos não foi gerado, pois não foram encontrados vínculos vigentes na data informada!");
                return;
            }

            criarArquivo("SERVIDORES ATIVOS");
            criarCabecalhoServidoresAtivos();
            int linhaInicial = 2;

            HSSFRow linhaDosAnos = criaRow(sheet, 2);

            for (VinculoFP v : vinculos) {
                if (validaVinculo(v)) {
                    CertidaoCasamento cc = documentoPessoaFacade.recuperaCertidaoCasamento(v.getMatriculaFP().getPessoa());

                    row = criaRow(sheet, linhaInicial);
                    criaCell(row, 0).setCellValue(v.getMatriculaFP().getMatricula());
                    criaCell(row, 1).setCellValue(v.getMatriculaFP().getPessoa().getNome());
                    criaCell(row, 2).setCellValue(StringUtil.retornaApenasNumeros(v.getMatriculaFP().getPessoa().getCpf()));
                    criaCell(row, 3).setCellValue(DataUtil.getDataFormatada(v.getMatriculaFP().getPessoa().getDataNascimento()));
                    criaCell(row, 4).setCellValue(v.getMatriculaFP().getPessoa().getNivelEscolaridade() != null ? v.getMatriculaFP().getPessoa().getNivelEscolaridade().getOrdem().toString() : "");
                    criaCell(row, 5).setCellValue(v.getMatriculaFP().getPessoa().getSexo() != null ? v.getMatriculaFP().getPessoa().getSexo().getCodigo() : "");
                    criaCell(row, 6).setCellValue(v.getMatriculaFP().getPessoa().getPai() != null ? v.getMatriculaFP().getPessoa().getPai() : "");
                    criaCell(row, 7).setCellValue(v.getMatriculaFP().getPessoa().getMae() != null ? v.getMatriculaFP().getPessoa().getMae() : "");
                    criaCell(row, 8).setCellValue(v.getMatriculaFP().getPessoa().getEstadoCivil() != null ? v.getMatriculaFP().getPessoa().getEstadoCivil().getCodigoRPPS() : "");

                    if (cc != null) {
                        criaCell(row, 9).setCellValue(cc.getNomeConjuge() != null ? cc.getNomeConjuge() : "");
                        criaCell(row, 10).setCellValue(cc.getDataNascimentoConjuge() != null ? DataUtil.getDataFormatada(cc.getDataNascimentoConjuge()) : "");
                    } else {
                        criaCell(row, 9).setCellValue("");
                        criaCell(row, 10).setCellValue("");
                    }

                    List<Dependente> dependentes = dependenteFacade.getDependentesDe(v.getMatriculaFP().getPessoa(), selecionado.getDataReferencia());
                    criaCell(row, 11).setCellValue(!CollectionUtils.isEmpty(dependentes) ? dependentes.size() : 0);
                    criaCell(row, 12).setCellValue(!CollectionUtils.isEmpty(dependentes) && !CollectionUtils.isEmpty(dependentes.get(0).getDependentesVinculosFPs()) ? DataUtil.getDataFormatada(dependentes.get(0).getDependentesVinculosFPs().get(0).getInicioVigencia()) : "");


                    criaCell(row, 13).setCellValue(getTempoServico((ContratoFP) v));
                    criaCell(row, 14).setCellValue(DataUtil.getDataFormatada(((ContratoFP) v).getDataAdmissao()));
                    criaCell(row, 15).setCellValue(DataUtil.getDataFormatada(((ContratoFP) v).getDataAdmissao()));
                    VinculoDeContratoFP tipoVinculo = getTipoVinculo((ContratoFP) v);
                    criaCell(row, 16).setCellValue(tipoVinculo != null ? tipoVinculo.getCodigo().toString() : "");
                    criaCell(row, 17).setCellValue(lotacaoFuncional((ContratoFP) v));
                    criaCell(row, 18).setCellValue(((ContratoFP) v).getCargo() != null && ((ContratoFP) v).getCargo().getCodigoCarreira() != null ? ((ContratoFP) v).getCargo().getCodigoCarreira() : "");
                    criaCell(row, 19).setCellValue(((ContratoFP) v).getCargo() != null ? ((ContratoFP) v).getCargo().getCodigoDoCargo() : "");

                    EnquadramentoFuncional enquadramentoFuncional = enquadramentoFuncionalFacade.recuperaEnquadramentoFuncionalVigente((ContratoFP) v, selecionado.getDataReferencia());

                    BigDecimal salario = new BigDecimal("0");

                    if (enquadramentoFuncional != null && enquadramentoFuncional.getId() != null) {
                        criaCell(row, 20).setCellValue(enquadramentoFuncional.getCategoriaPCS() != null ? enquadramentoFuncional.getCategoriaPCS().getDescricao() : "");
                        enquadramentoFuncional.setCategoriaPCS(categoriaPCSFacade.recuperar(enquadramentoFuncional.getCategoriaPCS().getId()));
                        enquadramentoFuncional.setProgressaoPCS(progressaoPCSFacade.recuperar(enquadramentoFuncional.getProgressaoPCS().getId()));

                        EnquadramentoPCS enquadramentoPCS = enquadramentoPCSFacade.recuperaValor(enquadramentoFuncional.getCategoriaPCS(), enquadramentoFuncional.getProgressaoPCS(), enquadramentoFuncional.getInicioVigencia());
                        if (enquadramentoPCS != null) {
                            salario = salario.add(enquadramentoPCS.getVencimentoBase());
                        }
                    }

                    BigDecimal gratificacao = eventoFPFacade.getSomaDosEventosPorAno((ContratoFP) v, ano, "8", "1");

                    criaCell(linhaDosAnos, 21).setCellValue(ano);

                    criaRow(sheet, linhaInicial);
                    criaCell(row, 21).setCellValue(df.format(salario));
                    criaCell(row, 22).setCellValue(df.format(gratificacao));
                    criaCell(row, 23).setCellValue(df.format(salario.add(gratificacao)));

                    linhaInicial++;
                }
            }
            pastaDeTrabalho.write(fout);

            files.put("Servidores Ativos.xls", file);

            fout.close();
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
        }
    }

    private HSSFCellStyle criarFonteNegrito() {
        HSSFCellStyle style = pastaDeTrabalho.createCellStyle();
        HSSFFont fonte = pastaDeTrabalho.createFont();
        fonte.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(fonte);
        return style;
    }

    private void criarCabecalhoServidoresAtivos() {
        HSSFRow cabecalho = criaRow(sheet, 0);
        HSSFCellStyle estiloNegrito = criarFonteNegrito();
        criaCell(cabecalho, 0, estiloNegrito).setCellValue("MATRÍCULA");
        criaCell(cabecalho, 1, estiloNegrito).setCellValue("NOME");
        criaCell(cabecalho, 2, estiloNegrito).setCellValue("CPF");
        criaCell(cabecalho, 3, estiloNegrito).setCellValue("DATA NASCIMENTO");
        criaCell(cabecalho, 4, estiloNegrito).setCellValue("ESCOLARIDADE");
        criaCell(cabecalho, 5, estiloNegrito).setCellValue("SEXO");
        criaCell(cabecalho, 6, estiloNegrito).setCellValue("NOME DO PAI");
        criaCell(cabecalho, 7, estiloNegrito).setCellValue("NOME DA MÃE");
        criaCell(cabecalho, 8, estiloNegrito).setCellValue("ESTADO CIVIL");
        criaCell(cabecalho, 9, estiloNegrito).setCellValue("NOME DO CONJUGE");
        criaCell(cabecalho, 10, estiloNegrito).setCellValue("DATA NASCIMENTO CONJUGE");
        criaCell(cabecalho, 11, estiloNegrito).setCellValue("QUANTIDADE DEPENDENTES");
        criaCell(cabecalho, 12, estiloNegrito).setCellValue("DATA NASCIMENTO DEPENDENTE MAIS JOVEM");
        criaCell(cabecalho, 13, estiloNegrito).setCellValue("TM DE SERVIÇOS ANTERIOR");
        criaCell(cabecalho, 14, estiloNegrito).setCellValue("DATA DE ADMISSÃO");
        criaCell(cabecalho, 15, estiloNegrito).setCellValue("DATA CARGO");
        criaCell(cabecalho, 16, estiloNegrito).setCellValue("TIPO DE VÍNCULO");
        criaCell(cabecalho, 17, estiloNegrito).setCellValue("LOTAÇÃO");
        criaCell(cabecalho, 18, estiloNegrito).setCellValue("CARREIRA");
        criaCell(cabecalho, 19, estiloNegrito).setCellValue("CARGO");
        criaCell(cabecalho, 20, estiloNegrito).setCellValue("NÍVEL");
        criaCell(cabecalho, 21, estiloNegrito).setCellValue("VALOR BASE");
        criaCell(cabecalho, 22, estiloNegrito).setCellValue("GRATIFICAÇÃO");
        criaCell(cabecalho, 23, estiloNegrito).setCellValue("VALOR BRUTO");
    }

    private VinculoDeContratoFP getTipoVinculo(ContratoFP contratoFP) {
        ContratoVinculoDeContrato contratoVinculoDeContrato = contratoVinculoDeContratoFacade.recuperaContratoVinculoDeContratoVigente(contratoFP, selecionado.getDataReferencia());
        if (contratoVinculoDeContrato != null) {
            return contratoVinculoDeContrato.getVinculoDeContratoFP();
        }
        return null;
    }

    private void geraArquivoAposentados() {
        try {
            vinculos.clear();
//            vinculos.addAll(vinculoFPFacade.listaAposentadoriasVigentes(selecionado.getDataReferencia()));

            if (vinculos == null || vinculos.isEmpty()) {
                FacesUtil.addError("", "O arquivo de Aposentados não foi gerado, pois não foram encontrados aposentados vigentes!");
                return;
            }

            criarArquivo("APOSENTADOS");

            int linhaInicial = 4;

            for (VinculoFP v : vinculos) {
                if (validaVinculo(v)) {
                    CertidaoCasamento cc = documentoPessoaFacade.recuperaCertidaoCasamento(v.getMatriculaFP().getPessoa());

                    row = criaRow(sheet, linhaInicial);
                    criaCell(row, 0).setCellValue(v.getMatriculaFP().getMatricula());
                    criaCell(row, 1).setCellValue(v.getMatriculaFP().getPessoa().getNome());
                    criaCell(row, 2).setCellValue(v.getMatriculaFP().getPessoa().getCpf().replaceAll("\\.", "").replaceAll("-", ""));
                    criaCell(row, 3).setCellValue(DataUtil.getDataFormatada(v.getMatriculaFP().getPessoa().getDataNascimento()));
                    criaCell(row, 4).setCellValue(v.getMatriculaFP().getPessoa().getSexo() != null ? v.getMatriculaFP().getPessoa().getSexo().getCodigo() : "");
                    if (v instanceof Pensionista) {
                        criaCell(row, 5).setCellValue(((Pensionista) v).getGrauParentescoPensionista().getDescricao());
                    } else {
                        criaCell(row, 5).setCellValue("");
                    }
                    criaCell(row, 6).setCellValue(v.getMatriculaFP().getPessoa().getEstadoCivil() != null ? v.getMatriculaFP().getPessoa().getEstadoCivil().getCodigoRPPS() : "");
                    if (cc != null) {
                        criaCell(row, 7).setCellValue(cc.getNomeConjuge() != null ? cc.getNomeConjuge() : "");
                        criaCell(row, 8).setCellValue(cc.getDataNascimentoConjuge() != null ? DataUtil.getDataFormatada(cc.getDataNascimentoConjuge()) : "");
                    } else {
                        criaCell(row, 7).setCellValue("");
                        criaCell(row, 8).setCellValue("");
                    }

                    //tipo do beneficios - se for vinculoFP, auxilio ou pensionista (detalhado)
                    if (v instanceof Aposentadoria) {
                        criaCell(row, 9).setCellValue(((Aposentadoria) v).getTipoAposentadoria().getDescricao());
                        if (((Aposentadoria) v).getTipoReajusteAposentadoria().equals(TipoReajusteAposentadoria.PARIDADE)) {
                            criaCell(row, 12).setCellValue(df.format(getSomaItemFichaFinanceiraVerbaFixa(v)));
                        } else if (((Aposentadoria) v).getTipoReajusteAposentadoria().equals(TipoReajusteAposentadoria.MEDIA)) {
                            criaCell(row, 12).setCellValue(df.format(getValorItemAposentadoria(v)));
                        }

                    } else if (v instanceof Pensionista) {
                        criaCell(row, 9).setCellValue(((Pensionista) v).getTipoPensao() != null ? ((Pensionista) v).getTipoPensao().getDescricao() : "");
                        criaCell(row, 12).setCellValue(df.format(getSomaItemValorPensionista(v)));
                    }
                    criaCell(row, 10).setCellValue(DataUtil.getDataFormatada((v.getInicioVigencia())));
                    criaCell(row, 11).setCellValue(v.getFinalVigencia() != null ? DataUtil.getDataFormatada((v.getFinalVigencia())) : "");


                    linhaInicial++;
                }
            }
            pastaDeTrabalho.write(fout);

            files.put("Aposentados.xls", file);

            fout.close();

        } catch (Exception ex) {
            logger.debug(ex.getMessage());
        }
    }

    private void geraArquivoDependentes() {
        try {
            TipoDependente tipoDependente = tipoDependenteFacade.recuperarTipoDependentePorCodigo(TIPO_PREVIDENCIA_RBPREV);
            if (tipoDependente == null) {
                FacesUtil.addError("", "O arquivo de Dependentes não pode ser gerado, pois não foi encontrado o Tipo de Dependência 'Cód: 11 - PREVIDÊNCIA RBPREV'!");
                return;
            }
            List<DependenteVinculoFP> dependenteVinculoFPs = null;
            if (dependenteVinculoFPs == null || dependenteVinculoFPs.isEmpty()) {
                FacesUtil.addError("", "O arquivo de Dependentes não foi gerado, pois não foram encontrados dependentes vigentes!");
                return;
            }

            criarArquivo("DEPENDENTES");

            int linhaInicial = 4;

            for (DependenteVinculoFP dv : dependenteVinculoFPs) {
                row = criaRow(sheet, linhaInicial);

                /*criaCell(row, 0).setCellValue(dv.getVinculoFP().getMatriculaFP().getMatricula());
                if (dv.getVinculoFP() instanceof Aposentadoria) {
                    criaCell(row, 1).setCellValue("1");
                } else if (dv.getVinculoFP() instanceof Pensionista) {
                    criaCell(row, 1).setCellValue("2");
                } else if (dv.getVinculoFP() instanceof ContratoFP) {
                    criaCell(row, 1).setCellValue("0");
                }*/

                criaCell(row, 2).setCellValue(dv.getDependente().getDependente().getNome());
                criaCell(row, 3).setCellValue(dv.getDependente().getDependente().getCpf() != null ? dv.getDependente().getDependente().getCpf().replaceAll("\\.", "").replaceAll("-", "") : "");
                criaCell(row, 4).setCellValue(dv.getDependente().getDependente().getDeficienteFisico() ? "1" : "0");
                criaCell(row, 5).setCellValue(dv.getDependente().getGrauDeParentesco() != null ? dv.getDependente().getGrauDeParentesco().getCodigoEsocial() : "");
                criaCell(row, 6).setCellValue(DataUtil.getDataFormatada(dv.getDependente().getDependente().getDataNascimento()));
                criaCell(row, 7).setCellValue(dv.getDependente().getDependente().getNivelEscolaridade() != null &&
                    dv.getDependente().getDependente().getNivelEscolaridade().getGrauEscolaridadeDependente() != null ? dv.getDependente().getDependente().getNivelEscolaridade().getGrauEscolaridadeDependente().getCodigo() : "");
                criaCell(row, 8).setCellValue(dv.getDependente().getDependente().getSexo().getCodigo());

                linhaInicial++;
            }
            pastaDeTrabalho.write(fout);

            files.put("Dependentes.xls", file);

            fout.close();
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
        }
    }

    //    GERANDO PENSIONISTAS
    private void geraArquivoPensionistas(TipoArquivoAtuarial tipoArquivoAtuarial) {

        try {
            TipoDependente tipoDependente = tipoDependenteFacade.recuperarTipoDependentePorCodigo(TIPO_PREVIDENCIA_RBPREV);
            if (tipoDependente == null) {
                FacesUtil.addError("", "O arquivo de Dependentes não pode ser gerado, pois não foi encontrado o Tipo de Dependência 'Cód: 11 - PREVIDÊNCIA RBPREV'!");
                return;
            }
            List<DependenteVinculoFP> dependenteVinculoFPs = null;
            if (dependenteVinculoFPs == null || dependenteVinculoFPs.isEmpty()) {
                FacesUtil.addError("", "O arquivo de Pensionistas não foi gerado, pois não foram encontrados pensionistas vigentes!");
                return;
            }

            criarArquivo("PENSIONISTAS");

            int linhaInicial = 4;

//                for (DependenteVinculoFP dv : dependenteVinculoFPs) {
//                    row = criaRow(sheet, linhaInicial);
//
//                    criaCell(row, 0).setCellValue(dv.getVinculoFP().getMatriculaFP().getMatricula());
//                    if (dv.getVinculoFP() instanceof Aposentadoria) {
//                        criaCell(row, 1).setCellValue("1");
//                    } else if (dv.getVinculoFP() instanceof Pensionista) {
//                        criaCell(row, 1).setCellValue("2");
//                    } else if (dv.getVinculoFP() instanceof ContratoFP) {
//                        criaCell(row, 1).setCellValue("0");
//                    }
//
//                    criaCell(row, 2).setCellValue(dv.getDependente().getDependente().getNome());
//                    criaCell(row, 3).setCellValue(dv.getDependente().getDependente().getCpf() != null ? dv.getDependente().getDependente().getCpf().replaceAll("\\.", "").replaceAll("-", "") : "");
//                    criaCell(row, 4).setCellValue(dv.getDependente().getDependente().getDeficienteFisico() ? "1" : "0");
//                    criaCell(row, 5).setCellValue(dv.getDependente().getTipoParentescoRPPS() != null ? dv.getDependente().getTipoParentescoRPPS().getCodigo() : "");
//                    criaCell(row, 6).setCellValue(DataUtil.getDataFormatada(dv.getDependente().getDependente().getDataNascimento()));
//                    criaCell(row, 7).setCellValue(dv.getDependente().getDependente().getNivelEscolaridade() != null &&
//                            dv.getDependente().getDependente().getNivelEscolaridade().getGrauEscolaridadeDependente() != null ? dv.getDependente().getDependente().getNivelEscolaridade().getGrauEscolaridadeDependente().getCodigo() : "");
//                    criaCell(row, 8).setCellValue(dv.getDependente().getDependente().getSexo().getCodigo());
//
//                    linhaInicial++;
//                }
            pastaDeTrabalho.write(fout);

            files.put("Pensionistas.xls", file);

            fout.close();

        } catch (Exception ex) {
            logger.debug(ex.getMessage());
        }
    }

    private HSSFSheet criarSheet(HSSFWorkbook pasta, String nomeSheet) {
        return pasta.createSheet(nomeSheet);
    }

    private void criarArquivo(String nomePlanilha) {
        try {
            file = File.createTempFile(nomePlanilha, "xls");
            fout = new FileOutputStream(file);
            pastaDeTrabalho = new HSSFWorkbook();
            sheet = criarSheet(pastaDeTrabalho, nomePlanilha);
            row = null;
        } catch (IOException ioe) {
            FacesUtil.addError("Erro ao gerar o arquivo: " + nomePlanilha, "");
        }
    }

    public int getTempoServico(ContratoFP contrato) {
        int quantidadeDias = 0;
        if (contrato != null && contrato.getId() != null) {
            List<AverbacaoTempoServico> lista = averbacaoTempoServicoFacade.averbacaoDeAposentado(contrato);

            if (lista != null) {
                for (AverbacaoTempoServico obj : lista) {
                    DateTime inicio = new DateTime(obj.getInicioVigencia() != null ? obj.getInicioVigencia() : new Date());
                    DateTime fim = new DateTime(obj.getFinalVigencia() != null ? obj.getFinalVigencia() : new Date());

                    quantidadeDias += Days.daysBetween(inicio, fim).getDays();
                }
            }
        }
        return quantidadeDias;
    }

    public String lotacaoFuncional(ContratoFP contratoFP) {
        LotacaoFuncional lot = null;
        HierarquiaOrganizacional ho = null;
        if (contratoFP != null && contratoFP.getId() != null) {
            lot = lotacaoFuncionalFacade.buscarUltimaLotacaoVigentePorVinculoFP(contratoFP);
        }
        if (lot != null && lot.getUnidadeOrganizacional() != null) {
            ho = hierarquiaOrganizacionalFacadeOLD.hierarquiaDaUnidadeOrg(lot.getUnidadeOrganizacional());
        }

        if (ho != null && ho.getId() != null) {
            return ho.getCodigo() + " - " + lot.toString();
        }

        return "Não foi encontrada a Lotação Funcional";
    }

    private BigDecimal getSomaItemFichaFinanceiraVerbaFixa(VinculoFP vinculoFP) {
        //vinculoFP = aposentadoriaFacade.recuperar(vinculoFP.getId());
        BigDecimal valor = new BigDecimal("0");
        FichaFinanceiraFP fichaRecuperada = fichaFinanceiraFPFacade.recuperaFichaFinanceiraPorContratoFP(vinculoFP);
        if (fichaRecuperada != null) {
            fichaRecuperada = fichaFinanceiraFPFacade.recuperar(fichaRecuperada.getId());
            for (ItemFichaFinanceiraFP itemFichaFinanceiraFP : fichaRecuperada.getItemFichaFinanceiraFP()) {
                if (itemFichaFinanceiraFP.getEventoFP() != null && itemFichaFinanceiraFP.getEventoFP().getVerbaFixa() != null
                    && itemFichaFinanceiraFP.getEventoFP().getVerbaFixa() && itemFichaFinanceiraFP.getValor() != null) {
                    valor = valor.add(itemFichaFinanceiraFP.getValor());
                }
            }
        }
        return valor;
    }

    private BigDecimal getValorItemAposentadoria(VinculoFP vinculoFP) {
        BigDecimal valor = new BigDecimal("0");
        ItemAposentadoria item = aposentadoriaFacade.recuperaItemAposentadoriaVigente(vinculoFP, selecionado.getDataReferencia());
        if (item != null && item.getId() != null && item.getValor() != null) {
            valor = valor.add(item.getValor());
        }
        return valor;
    }

    private BigDecimal getSomaItemValorPensionista(VinculoFP v) {
        BigDecimal valor = new BigDecimal("0");
        ItemValorPensionista item = pensionistaFacade.recuperaItemValorPensionistaVigente((Pensionista) v);
        if (item != null && item.getId() != null && item.getValor() != null) {
            valor = valor.add(item.getValor());
        }
        return valor;
    }

    public UsuarioSistema usuarioLogado() {
        return sistemaFacade.getUsuarioCorrente();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/bb-atuarial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return contratoFPFacade.recuperarFiltrandoContratosVigentesEm(parte.trim(), UtilRH.getDataOperacao());
    }

    public AuxiliarAndamentoBBAtuarial getAuxiliarAndamentoBBAtuarial() {
        return auxiliarAndamentoBBAtuarial;
    }

    public void setAuxiliarAndamentoBBAtuarial(AuxiliarAndamentoBBAtuarial auxiliarAndamentoBBAtuarial) {
        this.auxiliarAndamentoBBAtuarial = auxiliarAndamentoBBAtuarial;
    }

    public void abortar() {
        for (Future f : futures) {
            f.cancel(true);
        }
        auxiliarAndamentoBBAtuarial.pararProcessamento();
        FacesUtil.atualizarComponente("Formulario:panelGeral");
    }

    public void concluirGeracaoBBAtuarial() throws IOException {
        if (auxiliarAndamentoBBAtuarial.getParado() == null || auxiliarAndamentoBBAtuarial.getParado()) {
            return;
        }

        gerarArquivoZIP();
        auxiliarAndamentoBBAtuarial.pararProcessamento();
        FacesUtil.atualizarComponente("Formulario:painelBotoes");
    }

    private ZipOutputStream escreverConteudoArquivo(FileInputStream fin, ZipOutputStream zout) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fin.read(buffer)) > 0) {
            zout.write(buffer, 0, length);
        }
        return zout;
    }

    private File gerarArquivosXLS() throws IOException {
        File zip = File.createTempFile("BBAtuarial", "zip");
        FileOutputStream fout = new FileOutputStream(zip);
        ZipOutputStream zout = new ZipOutputStream(fout);

        for (String obj : selecionado.getTiposArquivoBBAtuarial()) {
            TipoArquivoAtuarial tipoArquivoAtuarial = TipoArquivoAtuarial.valueOf(obj);
            if (tipoArquivoAtuarial.equals(TipoArquivoAtuarial.SERVIDORES_ATIVOS)) {
                criarSheetsAuxiliaresServidoresAtivos();
                auxiliarAndamentoBBAtuarial.criarArquivoServidoresAtivos();
                auxiliarAndamentoBBAtuarial.getWorkBookServidoresAtivos().getWorkBook().write(auxiliarAndamentoBBAtuarial.getOutputStreamServidoresAtivos());
                auxiliarAndamentoBBAtuarial.getOutputStreamServidoresAtivos().close();
                zout.putNextEntry(new ZipEntry("SERVIDORES_ATIVOS.xls"));
                FileInputStream fin = new FileInputStream(auxiliarAndamentoBBAtuarial.getFileServidoresAtivos());
                zout = escreverConteudoArquivo(fin, zout);
                fin.close();
            }
            if (tipoArquivoAtuarial.equals(TipoArquivoAtuarial.APOSENTADOS)) {
                criarSheetsAuxiliaresAposentados();
                auxiliarAndamentoBBAtuarial.criarArquivoAposentados();
                auxiliarAndamentoBBAtuarial.getWorkBookAposentados().getWorkBook().write(auxiliarAndamentoBBAtuarial.getOutputStreamAposentados());
                auxiliarAndamentoBBAtuarial.getOutputStreamAposentados().close();
                zout.putNextEntry(new ZipEntry("APOSENTADOS.xls"));
                FileInputStream fin = new FileInputStream(auxiliarAndamentoBBAtuarial.getFileAposentados());
                zout = escreverConteudoArquivo(fin, zout);
                fin.close();
            }
//            if (tipoArquivoAtuarial.equals(TipoArquivoAtuarial.DEPENDENTES)) {
//                criarSheetsAuxiliaresDependentes();
//                auxiliarAndamentoBBAtuarial.criarArquivoDependentes();
//                auxiliarAndamentoBBAtuarial.getWorkBookDependentes().getWorkBook().write(auxiliarAndamentoBBAtuarial.getOutputStreamDependentes());
//                auxiliarAndamentoBBAtuarial.getOutputStreamDependentes().close();
//                zout.putNextEntry(new ZipEntry("DEPENDENTES.xls"));
//                FileInputStream fin = new FileInputStream(auxiliarAndamentoBBAtuarial.getFileDependentes());
//                zout = escreverConteudoArquivo(fin, zout);
//                fin.close();
//            }
            if (tipoArquivoAtuarial.equals(TipoArquivoAtuarial.PENSIONISTAS)) {
                auxiliarAndamentoBBAtuarial.criarArquivoPensionistas();
                auxiliarAndamentoBBAtuarial.getWorkBookPensionistas().getWorkBook().write(auxiliarAndamentoBBAtuarial.getOutputStreamPensionistas());
                auxiliarAndamentoBBAtuarial.getOutputStreamPensionistas().close();
                zout.putNextEntry(new ZipEntry("PENSIONISTAS.xls"));
                FileInputStream fin = new FileInputStream(auxiliarAndamentoBBAtuarial.getFilePensionistas());
                zout = escreverConteudoArquivo(fin, zout);
                fin.close();
            }
        }
        zout.close();
        return zip;
    }

    private void gerarArquivoZIP() {
        try {
            zipFile = gerarArquivosXLS();
            FileInputStream fileInputStream = new FileInputStream(zipFile);
            fileDownload = new DefaultStreamedContent(fileInputStream, "application/zip", "BBAtuarial.zip");

            if (zipFile.exists()) {
//                zipFile.delete();
            }
        } catch (IOException ioe) {
            logger.debug(ioe.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Não foi possível gerar o arquivo ZIP do BB Atuarial, por favor, comunique o suporte técnico.");
        }
    }

    public StreamedContent fileDownload() throws FileNotFoundException, IOException {
        InputStream stream = new FileInputStream(zipFile);
        fileDownload = new DefaultStreamedContent(stream, "application/zip", "BBAtuarial.zip");
        return fileDownload;
    }

    public void addToZip() {
        try {
            zipFile = File.createTempFile("BBAtuarial", "zip");

            byte[] buffer = new byte[1024];

            FileOutputStream fout = new FileOutputStream(zipFile);

            ZipOutputStream zout = new ZipOutputStream(fout);

            for (Map.Entry<String, File> m : files.entrySet()) {

                //cria o objeto FileInputStream do File em questão
                FileInputStream fin = new FileInputStream(m.getValue());

                //coloca o nome do arquivo dentro do zip com o nome passado no parametro
                zout.putNextEntry(new ZipEntry(m.getKey()));

                //escreve o arquivo dentro do zip
                int length;
                while ((length = fin.read(buffer)) > 0) {
                    zout.write(buffer, 0, length);
                }

                //fecha o arquivo dentro do zip
                zout.closeEntry();

                //fecha o inputStream
                fin.close();
            }


            //fecha o arquivo zip
            zout.close();

            //joga o arquivo zip concluido para um FileInputStream para fazer download
            fis = new FileInputStream(zipFile);
            fileDownload = new DefaultStreamedContent(fis, "application/zip", "BBAtuarial.zip");

        } catch (IOException ioe) {
            FacesUtil.addError("Erro grave!", "Ocorreu um erro para gerar o arquivo ZIP do BB Atuarial, comunique o administrador.");
        }
    }

    public void criarSheetAuxiliarEm(WorkBookBBAtuarial workBook, String nomeSheet, List<AuxiliarConteudoBBAtuarial> conteudo) {
        HSSFSheet sheet = workBook.getWorkBook().createSheet(nomeSheet);

        HSSFCellStyle estiloNegrito = workBook.criarFonteNegrito();
        HSSFRow rowCabecalho = sheet.createRow(0);
        workBook.criarCell(rowCabecalho, 0, estiloNegrito).setCellValue("CÓDIGO");
        workBook.criarCell(rowCabecalho, 1, estiloNegrito).setCellValue("DESCRIÇÃO");

        Integer linha = 1;
        for (AuxiliarConteudoBBAtuarial ac : conteudo) {
            HSSFRow row = sheet.createRow(linha);
            try {
                workBook.criarCell(row, 0).setCellValue(Double.parseDouble(ac.getCodigo()));
            } catch (Exception e) {
                workBook.criarCell(row, 0).setCellValue(ac.getCodigo());
            }
            workBook.criarCell(row, 1).setCellValue(ac.getDescricao());
            linha++;
        }
    }

    public List<AuxiliarConteudoBBAtuarial> getConteudoSexoServidoresAtivos() {
        List<AuxiliarConteudoBBAtuarial> retorno = Lists.newArrayList();
        for (Sexo sexo : Sexo.values()) {
            AuxiliarConteudoBBAtuarial conteudo = new AuxiliarConteudoBBAtuarial();
            conteudo.setCodigo(sexo.getCodigo());
            conteudo.setDescricao(sexo.getDescricao());
            retorno.add(conteudo);
        }
        return retorno;
    }

    public List<AuxiliarConteudoBBAtuarial> getConteudoEstadoCivilServidoresAtivos() {
        List<AuxiliarConteudoBBAtuarial> retorno = Lists.newArrayList();
        for (EstadoCivil ec : EstadoCivil.values()) {
            AuxiliarConteudoBBAtuarial conteudo = new AuxiliarConteudoBBAtuarial();
            conteudo.setCodigo(ec.getCodigoRPPS());
            conteudo.setDescricao(ec.getDescricao());
            retorno.add(conteudo);
        }
        return retorno;
    }

    public List<AuxiliarConteudoBBAtuarial> getConteudoTipoDeVinculoServidoresAtivos() {
        List<AuxiliarConteudoBBAtuarial> retorno = Lists.newArrayList();
        for (VinculoDeContratoFP vc : vinculoDeContratoFPFacade.lista()) {
            AuxiliarConteudoBBAtuarial conteudo = new AuxiliarConteudoBBAtuarial();
            conteudo.setCodigo(vc.getCodigo() + "");
            conteudo.setDescricao(vc.getDescricao());
            retorno.add(conteudo);
        }
        return retorno;
    }

    public List<AuxiliarConteudoBBAtuarial> getConteudoEscolaridadeServidoresAtivos() {
        List<AuxiliarConteudoBBAtuarial> retorno = Lists.newArrayList();
        for (NivelEscolaridade ne : contratoFPFacade.recuperarEscolaridadeServidoresAtivosBBAtuarial(selecionado.getDataReferencia())) {
            AuxiliarConteudoBBAtuarial conteudo = new AuxiliarConteudoBBAtuarial();
            conteudo.setCodigo(ne.getOrdem() + "");
            conteudo.setDescricao(ne.getDescricao());
            retorno.add(conteudo);
        }
        return retorno;
    }

    public void criarSheetsAuxiliaresServidoresAtivos() {
        criarSheetAuxiliarEm(auxiliarAndamentoBBAtuarial.getWorkBookServidoresAtivos(), "ESCOLARIDADE", getConteudoEscolaridadeServidoresAtivos());
        criarSheetAuxiliarEm(auxiliarAndamentoBBAtuarial.getWorkBookServidoresAtivos(), "SEXO", getConteudoSexoServidoresAtivos());
        criarSheetAuxiliarEm(auxiliarAndamentoBBAtuarial.getWorkBookServidoresAtivos(), "ESTADO CIVIL", getConteudoEstadoCivilServidoresAtivos());
        criarSheetAuxiliarEm(auxiliarAndamentoBBAtuarial.getWorkBookServidoresAtivos(), "TIPO DE VINCULO", getConteudoTipoDeVinculoServidoresAtivos());
        auxiliarAndamentoBBAtuarial.criarSheetCargosServidoresAtivos(contratoFPFacade.recuperarCargosServidoresAtivosBBAtuarial(selecionado.getDataReferencia()));
    }

    public void criarSheetsAuxiliaresAposentados() {
        criarSheetAuxiliarEm(auxiliarAndamentoBBAtuarial.getWorkBookAposentados(), "SEXO", getConteudoSexoServidoresAtivos());
        criarSheetAuxiliarEm(auxiliarAndamentoBBAtuarial.getWorkBookAposentados(), "ESTADO CIVIL", getConteudoEstadoCivilServidoresAtivos());
    }

    public void criarSheetsAuxiliaresDependentes() {
        criarSheetAuxiliarEm(auxiliarAndamentoBBAtuarial.getWorkBookDependentes(), "SEXO", getConteudoSexoServidoresAtivos());
        criarSheetAuxiliarEm(auxiliarAndamentoBBAtuarial.getWorkBookDependentes(), "ESCOLARIDADE", getConteudoEscolaridadeServidoresAtivos());
    }

    public void redirecionarParaNovo() {
        FacesUtil.redirecionamentoInterno("/bb-atuarial/novo/");
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
            + "<p style='font-size : 15px;'><b><u>Data Processamento: " + Util.formatterDataHora.format(selecionado.getDataRegistro()) + "<u></b></p>"
            + "<p style='font-size : 15px;'><b><u>USUÁRIO RESPONSÁVEL: " + sistemaFacade.getUsuarioCorrente().getLogin() + "<u></b></p>"
            + "<p style='font-size : 10px;'>"
            + auxiliarAndamentoBBAtuarial.getSomenteStringDoLog()
            + "</p>"
            + " </body>"
            + " </html>";
        String nome = "Log geração BB Atuarial - " + selecionado;
        nome = nome.replace(" ", "_");
        Util.downloadPDF(nome, conteudo, FacesContext.getCurrentInstance());
    }

    public File getZipFile() {
        return zipFile;
    }

    public void setZipFile(File zipFile) {
        this.zipFile = zipFile;
    }

    public StreamedContent getFileDownload() {
        return fileDownload;
    }

    public void setFileDownload(StreamedContent fileDownload) {
        this.fileDownload = fileDownload;
    }

    public boolean tudoTerminado() {
        if (futures == null) {
            return false;
        }
        for (Future<AuxiliarAndamentoBBAtuarial> future : futures) {
            if (!future.isDone()) {
                return false;
            }
        }
        return true;
    }
}
