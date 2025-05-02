/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ImportaMovimentoFinanceiro;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.StatusCompetencia;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.exception.ImportarMovimentoFinanceiroEConsigException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.*;

/**
 * @author andre
 */
@ManagedBean
@SessionScoped
@URLMappings(mappings = {
    @URLMapping(id = "inicializa-movimento-financeiro", pattern = "/movimento-financeiro/novo/", viewId = "/faces/rh/administracaodepagamento/importamovimentofinanceiro/edita.xhtml"),
    @URLMapping(id = "listar-movimento-financeiro", pattern = "/movimento-financeiro/listar/", viewId = "/faces/rh/administracaodepagamento/importamovimentofinanceiro/lista.xhtml"),
    @URLMapping(id = "acompanhar-movimento-financeiro", pattern = "/movimento-financeiro/acompanhamento/", viewId = "/faces/rh/administracaodepagamento/importamovimentofinanceiro/log.xhtml"),
    @URLMapping(id = "editar-movimento-financeiro", pattern = "/movimento-financeiro/editar/#{importaMovimentoFinanceiroControlador.id}/", viewId = "/faces/rh/administracaodepagamento/importamovimentofinanceiro/edita.xhtml"),
    @URLMapping(id = "ver-movimento-financeiro", pattern = "/movimento-financeiro/ver/#{importaMovimentoFinanceiroControlador.id}/", viewId = "/faces/rh/administracaodepagamento/importamovimentofinanceiro/visualizar.xhtml")
})
public class ImportaMovimentoFinanceiroControlador extends PrettyControlador<ArquivoEconsig> implements Serializable, CRUD {

    private UploadedFile file;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private LancamentoFPFacade lancamentoFPFacade;
    @EJB
    private ArquivoEconsigFacade arquivoEconsigFacade;
    @EJB
    private MotivoRejeicaoFacade motivoRejeicaoFacade;
    @EJB
    private EntidadeConsignatariaFacade entidadeConsignatariaFacade;
    @EJB
    private CompetenciaFPFacade competenciaFPFacade;
    private ImportaMovimentoFinanceiro importaMovimentoFinanceiro;
    private Mes mes;
    private Integer ano;
    private Boolean desabilitarFileUpload;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;

    public ImportaMovimentoFinanceiroControlador() {
        super(ArquivoEconsig.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return arquivoEconsigFacade;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    @URLAction(mappingId = "inicializa-movimento-financeiro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        importaMovimentoFinanceiro = new ImportaMovimentoFinanceiro();
        selecionado = new ArquivoEconsig();
        super.novo();
        setDesabilitarFileUpload(Boolean.TRUE);
        mes = getMesAtual();
        ano = null;


    }

    public void onComplete() {
        FacesUtil.executaJavaScript("dialog.show();");
    }

    public void importaArquivo(FileUploadEvent arquivo) throws FileNotFoundException, IOException {
        try {
            if (importaMovimentoFinanceiro.getFile() == null) {
                if (verificaArquivoValidoPelaPrimeiraLinha(arquivo.getFile())) {
                    importaMovimentoFinanceiro.setFile(arquivo.getFile());
                    importaMovimentoFinanceiro.setAno(getAno());
                    importaMovimentoFinanceiro.setMes(mes.getNumeroMes());
                    StringWriter stringWriter = new StringWriter();
                    InputStream inputStream = arquivo.getFile().getInputstream();
                    IOUtils.copy(arquivo.getFile().getInputstream(), stringWriter);
                    selecionado.setConteudoArquivo(stringWriter.toString());


                    arquivoEconsigFacade.processaArquivo(importaMovimentoFinanceiro);
                    FacesUtil.redirecionamentoInterno("/movimento-financeiro/acompanhamento/");
                }
            }
        } catch (ImportarMovimentoFinanceiroEConsigException e) {
            FacesUtil.addError("Erro ao processar arquivo.", "Erro: " + e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Erro ao processar arquivo.", "Erro: " + e);
        }
    }

    private boolean verificaArquivoValidoPelaPrimeiraLinha(UploadedFile uploadedFile) {
        try {

            boolean validacao = false;
            if (uploadedFile == null) {
                validacao = true;
            }
            InputStreamReader streamReader2 = null;

            streamReader2 = new InputStreamReader(uploadedFile.getInputstream());

            BufferedReader reader2 = new BufferedReader(streamReader2);
            String linha;
            while ((linha = reader2.readLine()) != null) {
                if (linha == null) {
                    validacao = true;
                }
                if (linha != null && linha.length() < 57) {
                    validacao = true;
                    break;
                }
                break;
            }
            if (validacao) {
                FacesUtil.addError("Atenção", "Arquivo inválido, por favor selecione o arquivo correto.");
                return false;
            }
            return true;

        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public MotivoRejeicao getMotivoRejeicao(List<MotivoRejeicao> lista, Integer codigo) {
        for (MotivoRejeicao mr : lista) {
            if (mr.getCodigo().equals(codigo)) {
                return mr;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String peixe = "00406140024660011300000000100P201207201207I08112006061545";
        //System.out.println("Consignatária: " + peixe.substring(0, 3));
        //System.out.println("Verba: " + peixe.substring(3, 7));
        //System.out.println("Matricula: " + peixe.substring(7, 14));
        //System.out.println("Contrato: " + peixe.substring(14, 16));
        //System.out.println("Orgão: " + peixe.substring(16, 18));
        //System.out.println("Valor: " + peixe.substring(18, 29));
        String valor = peixe.substring(18, 27) + "." + peixe.substring(27, 29);
        //System.out.println("Valores:  " + valor);
        //System.out.println("Tipo: " + peixe.substring(29, 30));
        //System.out.println("Data Inicial: " + peixe.substring(30, 36));
        String dataInicial = peixe.substring(36, 42);
        //System.out.println("Data Final: " + peixe.substring(36, 42));
        String dataFinal = peixe.substring(36, 42);
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.YEAR, Integer.parseInt(dataInicial.substring(0, 4)));
        ca.set(Calendar.MONTH, Integer.parseInt(dataInicial.substring(4, 6)) - 1);
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMinimum(Calendar.DAY_OF_MONTH));
        //System.out.println(ca.getTime());

        Calendar caf = Calendar.getInstance();
        caf.set(Calendar.YEAR, Integer.parseInt(dataFinal.substring(0, 4)));
        caf.set(Calendar.MONTH, Integer.parseInt(dataFinal.substring(4, 6)) - 1);
        caf.set(Calendar.DAY_OF_MONTH, caf.getActualMaximum(Calendar.DAY_OF_MONTH));
        //System.out.println(caf.getTime());


        //System.out.println("Situação: " + peixe.substring(42, 43));
        //System.out.println("Data Inclusão: " + peixe.substring(43, 57));
        String dataInclusao = peixe.substring(43, 57);
        //System.out.println("DATA INCLUSAO");
        //System.out.println("DIA: " + dataInclusao.substring(0, 2));
        //System.out.println("Mes: " + dataInclusao.substring(2, 4));
        //System.out.println("ANO: " + dataInclusao.substring(4, 8));
        //System.out.println("Hora: " + dataInclusao.substring(8, 10));
        //System.out.println("Min: " + dataInclusao.substring(10, 12));
        //System.out.println("Segundo: " + dataInclusao.substring(12, 14));
    }


    @URLAction(mappingId = "ver-movimento-financeiro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        //To change body of overridden methods use File | Settings | File Templates.
//        selecionado = new ArquivoEconsig();
    }

    @URLAction(mappingId = "editar-movimento-financeiro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        //Por causa da tela de processamento.


    }

    public int getContador() {
        return importaMovimentoFinanceiro.getContador();
    }

    public List<LancamentoFP> getLancamentos() {
        return importaMovimentoFinanceiro.getLancamentos();
    }

    public List<String> getLinhaProblema() {
        return importaMovimentoFinanceiro.getLinhaProblema();
    }

    @Override
    public void salvar() {
        try {
            if (importaMovimentoFinanceiro.getLancamentos().isEmpty()) {
                FacesUtil.addWarn("Nenhum Lançamento foi salvo", "a lista está vazia");
                return;
            }
            if (!arquivoEconsigFacade.existeArquivoProcessado(selecionado.getMes(), selecionado.getAno())) {
                FacesUtil.addWarn("Atenção", "O registro já foi salvo para " + selecionado.getMes() + " e " + selecionado.getAno());
                return;
            }
            Integer contadorRejeitados = 0;
            for (LancamentoFP la : importaMovimentoFinanceiro.getLancamentos()) {
                if (la.getMotivoRejeicao() != null) {
                    contadorRejeitados++;
                }
                selecionado.getArquivoEconsigItens().add(new ArquivoEconsigItens(la.getLinhaArquivo(), la, selecionado));
            }
            for (ErroEconsig erroEconsig : importaMovimentoFinanceiro.getErrosEconsig()) {
                erroEconsig.setArquivoEconsig(selecionado);
            }
            selecionado.setErrosEconsig(importaMovimentoFinanceiro.getErrosEconsig());
            contadorRejeitados = importaMovimentoFinanceiro.getErrosEconsig().size();
            arquivoEconsigFacade.salvar(selecionado, importaMovimentoFinanceiro, contadorRejeitados);
            FacesUtil.addInfo("Salvo com sucesso", "Foram salvos " + importaMovimentoFinanceiro.getContadorOk() + " arquivos");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            return;
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Atenção", "Erro: " + e.getMessage());

        }
        redireciona();

    }

//    public void salvarTudo() {
//        try {
//            if (importaMovimentoFinanceiro.getLancamentos().isEmpty()) {
//                FacesUtil.addWarn("Nenhum item foi salvo", "a lista está vazia");
//                return;
//            }
//            for (LancamentoFP la : importaMovimentoFinanceiro.getLancamentos()) {
//                lancamentoFPFacade.salvarNovo(la);
//                selecionado.getArquivoEconsigItens().add(new ArquivoEconsigItens(la.getLinhaArquivo(), la, selecionado));
//            }
//            selecionado.setAno(ano);
//            selecionado.setMes(mes);
//            selecionado.setDataRegistro(new Date());
//            selecionado.setTotal(importaMovimentoFinanceiro.getContadorTotal());
//            selecionado.setQuantidadeOk(importaMovimentoFinanceiro.getContadorOk());
//            selecionado.setQuantidadeRejeitados(importaMovimentoFinanceiro.getContadorProblema());
//
//            arquivoEconsigFacade.salvarNovo(selecionado);
//            FacesUtil.addInfo("Salvo com sucesso", "Foram salvos " + importaMovimentoFinanceiro.getContadorOk() + " arquivos");
//            novo();
//
//        } catch (Exception e) {
//            FacesUtil.addInfo("Problema ao salvar.", "Problema ao salvar, Erro:" + e);
//        }
//
//    }

    public Integer getContadorOk() {
        return importaMovimentoFinanceiro.getContadorOk();
    }

    public Integer getTotalArquivosSeremSalvos() {
        return importaMovimentoFinanceiro.getTotalArquivosSeremSalvos();
    }

    public Integer getContadorProblema() {
        return importaMovimentoFinanceiro.getContadorProblema();
    }

    public Integer getContadorRejeitados() {
        return importaMovimentoFinanceiro.getContadorRejeitados();
    }

    public Integer getContadorTotal() {
        return importaMovimentoFinanceiro.getContadorTotal();
    }

    public Integer getConsignatariaNaoEncontrado() {
        return importaMovimentoFinanceiro.getConsignatariaNaoEncontrado();
    }

    public Integer getEventoEConsigNaoRelac() {
        return importaMovimentoFinanceiro.getEventoEConsigNaoRelac();
    }

    public Integer getEventoNaoEncontrado() {
        return importaMovimentoFinanceiro.getEventoNaoEncontrado();
    }

    public Integer getSecretariaInexiste() {
        return importaMovimentoFinanceiro.getSecretariaInexiste();
    }

    public Integer getServidorNaoPertence() {
        return importaMovimentoFinanceiro.getServidorNaoPertence();
    }

    public boolean isLiberado() {
        return importaMovimentoFinanceiro.isLiberaCaixaDialogo();
    }

    public boolean mostraMensagemSalvar() {
        if (getPercentual() == 100) {
            onComplete();
        }
        return importaMovimentoFinanceiro.isPodeMostrarMensagem();
    }

    public double getPercentual() {
        return (importaMovimentoFinanceiro.getContador() * 100) / (importaMovimentoFinanceiro.getContadorTotal() == 0 ? 0.001 : importaMovimentoFinanceiro.getContadorTotal());
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (Mes m : Mes.values()) {
            SelectItem si = new SelectItem(m, m.getDescricao());
            retorno.add(si);
        }
        return retorno;
    }

    public Boolean getDesabilitarFileUpload() {
        return desabilitarFileUpload;
    }

    public void setDesabilitarFileUpload(Boolean desabilitarFileUpload) {
        this.desabilitarFileUpload = desabilitarFileUpload;
    }

    public Mes getMesAtual() {
        Date dataOperacao = UtilRH.getDataOperacao();
        Calendar c = Calendar.getInstance();
        c.setTime(dataOperacao);
        return Mes.values()[c.get(Calendar.MONTH)];
    }

    public void verificarSeExisteCompetenciaAberta() {
        if (ano == null || mes == null) {
            desabilitarFileUpload = Boolean.TRUE;
            return;
        }

        CompetenciaFP c = competenciaFPFacade.recuperarCompetenciaPorTipoMesAno(mes, ano, TipoFolhaDePagamento.NORMAL);

        if (c == null) {
            FacesUtil.addError("Operação não permitida.", "Não foi localizada competência para " + mes + "/" + ano + "! Por favor, informe o mês e ano de competência correto!");
            desabilitarFileUpload = Boolean.TRUE;
            return;
        }

        if (c.getStatusCompetencia().equals(StatusCompetencia.EFETIVADA)) {
            FacesUtil.addError("Operação não permitida.", "A competência " + mes + "/" + ano + " da folha normal já está efetivada! Por favor, informe o mês e ano de competência correto!");
            desabilitarFileUpload = Boolean.TRUE;
            return;
        }
        List<ArquivoEconsig> arquivosEconsig = arquivoEconsigFacade.buscarByMonthAndYear(mes, ano);
        if (arquivosEconsig != null && !arquivosEconsig.isEmpty()) {
            FacesUtil.addError("Operação não permitida.", "Já existe um registro para " + mes + "/" + ano + " das movimentações do e-consig. Por favor, exclua o registro e refaça a operação.");
            desabilitarFileUpload = Boolean.TRUE;
            return;
        }

        desabilitarFileUpload = Boolean.FALSE;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/movimento-financeiro/";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void excluir() {
        try {
            CompetenciaFP c = competenciaFPFacade.recuperarCompetenciaPorTipoMesAno(selecionado.getMes(), selecionado.getAno(), TipoFolhaDePagamento.NORMAL);

            if (c == null) {
                FacesUtil.addError("Operação não permitida.", "Não foi localizada competência para " + selecionado.getMes() + "/" + selecionado.getAno() + "! Por favor, informe o mês e ano de competência correto!");
                return;
            }

            if (c.getStatusCompetencia().equals(StatusCompetencia.EFETIVADA)) {
                FacesUtil.addError("Operação não permitida.", "A competência " + selecionado.getMes() + "/" + selecionado.getAno() + " da folha normal já está efetivada! Por favor, informe o mês e ano de competência correto!");
                return;
            }
            arquivoEconsigFacade.remover(selecionado);
            redireciona();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoExcluir()));
            selecionado = null;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void cancelarProcessamento() {
        importaMovimentoFinanceiro.cancelar();
    }

    public void gerarRelatorio() {
        try {
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            HashMap parameters = new HashMap();
            String nomeArquivo = "RelatorioImportacaoMovimentoFinanceiro.jasper";
            parameters.put("IMAGEM", abstractReport.getCaminhoImagem() + "/Brasao_de_Rio_Branco.gif");
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            parameters.put("USER", contratoFPFacade.getSistemaFacade().getUsuarioCorrente().getNome());
            parameters.put("NOMERELATORIO", "RELATÓRIO DE IMPORTACAO DO ARQUIVO DE MOVIMENTO FINANCEIRO - ECONSIG");
            parameters.put("MES", selecionado.getMes().getDescricao());
            parameters.put("ANO", selecionado.getAno().toString());
            parameters.put("DIA_OPERACAO", selecionado.getDataRegistro());
            parameters.put("MODULO", "RECURSOS HUMANOS");
            parameters.put("ARQ_ID", selecionado.getId());
            parameters.put("QTD_REJEITADOS", selecionado.getQuantidadeRejeitados());

            abstractReport.setGeraNoDialog(true);
            abstractReport.gerarRelatorio(nomeArquivo, parameters);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }
}
