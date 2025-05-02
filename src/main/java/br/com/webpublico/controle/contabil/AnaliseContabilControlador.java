package br.com.webpublico.controle.contabil;


import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.conciliacaocontabil.AnaliseMovimentoContabil;
import br.com.webpublico.entidadesauxiliares.ReprocessamentoSaldoGrupoBens;
import br.com.webpublico.entidadesauxiliares.ReprocessamentoSubConta;
import br.com.webpublico.entidadesauxiliares.SaldoDividaPublicaReprocessamento;
import br.com.webpublico.entidadesauxiliares.contabil.ExtratoMovimentoDespesaORC;
import br.com.webpublico.entidadesauxiliares.contabil.analise.*;
import br.com.webpublico.enums.OperacaoORC;
import br.com.webpublico.enums.TipoOperacaoORC;
import br.com.webpublico.enums.contabil.SituacaoMovimentoContabil;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AnaliseContabilFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.contabil.singleton.SingletonFuturesContabil;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.itextpdf.io.IOException;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static br.com.webpublico.negocios.contabil.singleton.SingletonFuturesContabil.KEY_FUTURE_ANALISE_CONTABIL;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {@URLMapping(id = "novo-validar-saldo-contabil", pattern = "/analise-contabil/", viewId = "/faces/financeiro/analise-contabil/edita.xhtml")})
public class AnaliseContabilControlador {

    private static final Logger logger = LoggerFactory.getLogger(AnaliseContabilControlador.class);
    @EJB
    private AnaliseContabilFacade facade;
    @EJB
    private SingletonFuturesContabil singletonFuturesContabil;
    private AssistenteAnaliseContabil selecionado;
    private List<CompletableFuture> futures;
    private List<String> tiposSelecionados;
    private AnaliseGerarSaldoOrcamentario analiseGerarSaldoOrcamentario;

    @URLAction(mappingId = "novo-validar-saldo-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        selecionado = new AssistenteAnaliseContabil();
        selecionado.setDataInicial(facade.getSistemaFacade().getDataOperacao());
        selecionado.setDataFinal(facade.getSistemaFacade().getDataOperacao());
        tiposSelecionados = Lists.newArrayList();
        for (TipoAnaliseContabil tipo : TipoAnaliseContabil.values()) {
            tiposSelecionados.add(tipo.name());
        }
        verificarFuturesEmExecucao();
    }

    private void verificarFuturesEmExecucao() {
        futures = singletonFuturesContabil.buscarFutures(KEY_FUTURE_ANALISE_CONTABIL);
        if (futures != null && !futures.isEmpty()) {
            iniciarImportacao();
        }
    }

    private static void iniciarImportacao() {
        FacesUtil.executaJavaScript("iniciarImportacao()");
    }

    public AssistenteAnaliseContabil getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(AssistenteAnaliseContabil selecionado) {
        this.selecionado = selecionado;
    }

    public Boolean isMostrarGerarSaldoOrcamentario() {
        return selecionado.getGerarSaldo() && tiposSelecionados.contains(TipoAnaliseContabil.ORCAMENTARIO.name());
    }

    public void exibirMensagens() {
        for (TipoAssistenteAnaliseContabil tipo : selecionado.getTipos()) {
            for (String mensagem : tipo.getMensagens()) {
                FacesUtil.addError("Erro ao gerar saldo " + tipo.getTipoAnaliseContabil(), mensagem);
            }
        }
    }

    public void gerarRelatorioPDF() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Html2Pdf.convert(getConteudoHtmlAnalise(), baos);
            byte[] bytes = baos.toByteArray();
            AbstractReport.poeRelatorioNaSessao(bytes);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar o relatório. Detalhes do erro: " + e.getMessage());
        }
    }

    public StreamedContent gerarRelatorioExcel() {
        try {
            List<String> titulos = Lists.newArrayList();
            titulos.add("Conta");
            titulos.add("Unidade");
            titulos.add("Valor");
            List<Object[]> objetos = Lists.newArrayList();
            for (ExtratoMovimentoDespesaORC dto : selecionado.getOrcamentario()) {
                Object[] obj = new Object[3];
                obj[0] = dto.getFonteDespesaORC().toString();
                obj[1] = dto.getHierarquiaOrganizacional();
                obj[2] = dto.getSaldoAtual();
                objetos.add(obj);
            }
            ExcelUtil excel = new ExcelUtil();
            excel.gerarExcelXLSX("Analise contábil", "analise-contabil", titulos, objetos, "", false);
            return excel.fileDownload();
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
        return null;
    }

    public void gerarSaldo() {
        try {
            if (selecionado.getHierarquiaOrganizacional() == null) {
                throw new ExcecaoNegocioGenerica("O Campo unidade organizacional deve ser informado.");
            }
            adicionarTiposAnaliseContabil();
            adicionarDespesaFonteOrcamentariaAleatoriamente();
            futures = Lists.newArrayList();
            List<TipoAssistenteAnaliseContabil> novos = Lists.newArrayList();
            for (int i = 1; i < selecionado.getQuantidadeConcorrencia(); i++) {
                for (TipoAssistenteAnaliseContabil tipo : selecionado.getTipos()) {
                    TipoAssistenteAnaliseContabil novoClone = (TipoAssistenteAnaliseContabil) Util.clonarObjeto(tipo);
                    novos.add(novoClone);
                }
            }
            selecionado.getTipos().addAll(novos);

            for (TipoAssistenteAnaliseContabil tipo : selecionado.getTipos()) {
                futures.add(AsyncExecutor.getInstance().execute(tipo, () -> {
                    try {
                        return facade.gerarSaldo(selecionado, tipo);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }));
            }
            iniciarImportacao();
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void adicionarDespesaFonteOrcamentariaAleatoriamente() {
        if (selecionado.getGerarSaldoOrcamentarios() != null && !selecionado.getGerarSaldoOrcamentarios().isEmpty()) {
            List<DespesaORC> despesaORCS = facade.getDespesaORCFacade().buscarTodasDespesasOrcPorExercicioAndUnidadeComMovimentacao(facade.getSistemaFacade().getExercicioCorrente(), selecionado.getHierarquiaOrganizacional(), selecionado.getDataInicial(), selecionado.getDataFinal(), selecionado.getBuscarSomenteContasComMovimentacao());

            int numberOfElements = selecionado.getQuantidadeRegistros();
            Random rand = new Random();
            List<DespesaORC> selecionadas = Lists.newArrayList();
            for (int i = 1; i < numberOfElements; i++) {
                int randomIndex = rand.nextInt(despesaORCS.size());
                DespesaORC randomElement = despesaORCS.get(randomIndex);
                selecionadas.add(randomElement);
            }

            for (DespesaORC despesaORC : selecionadas) {
                for (FonteDespesaORC fonteDespesaORC : despesaORC.getFonteDespesaORCs()) {
                    for (AnaliseGerarSaldoOrcamentario gerarSaldoOrcamentario : selecionado.getGerarSaldoOrcamentarios()) {
                        gerarSaldoOrcamentario.getFontes().add(new AnaliseGerarSaldoOrcamentarioFonte(fonteDespesaORC));
                    }
                }
            }
        }
    }

    public void salvarMovimentos() {
        try {
            adicionarTiposAnaliseContabil();
            futures = Lists.newArrayList();
            selecionado.setMovimentos(Lists.newArrayList());
            for (TipoAssistenteAnaliseContabil tipo : selecionado.getTipos()) {
                futures.add(AsyncExecutor.getInstance().execute(tipo, () -> {
                    try {
                        return facade.salvarHashMovimento(selecionado, tipo);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }));
            }
            iniciarImportacao();
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void iniciar() {
        try {
            adicionarTiposAnaliseContabil();
            criarMovimentoAnaliseContabil();
            futures = Lists.newArrayList();
            for (TipoAssistenteAnaliseContabil tipo : selecionado.getTipos()) {
                futures.add(AsyncExecutor.getInstance().execute(tipo, () -> {
                    try {
                        return facade.analiseContabil(selecionado, tipo);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }));
                for (CompletableFuture future : futures) {
                    singletonFuturesContabil.adicionarFutures(KEY_FUTURE_ANALISE_CONTABIL, future);
                }
            }
            iniciarImportacao();
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void criarMovimentoAnaliseContabil() {
        AnaliseMovimentoContabil analise = new AnaliseMovimentoContabil();
        analise.setSituacao(SituacaoMovimentoContabil.PENDENTE);
        analise.setDataInicial(selecionado.getDataInicial());
        analise.setDataFinal(selecionado.getDataFinal());
        selecionado.setAnalise(analise);
    }

    private void adicionarTiposAnaliseContabil() {
        selecionado.setTipos(Lists.newArrayList());
        for (String tipo : tiposSelecionados) {
            TipoAssistenteAnaliseContabil tipoAssistenteAnaliseContabil = new TipoAssistenteAnaliseContabil();
            tipoAssistenteAnaliseContabil.setTipoAnaliseContabil(TipoAnaliseContabil.valueOf(tipo));
            tipoAssistenteAnaliseContabil.setDescricaoProcesso("Analise movimentação contábil (" + tipo + ")");
            tipoAssistenteAnaliseContabil.setMensagens(Lists.<String>newArrayList());
            tipoAssistenteAnaliseContabil.setDataAtual(facade.getSistemaFacade().getDataOperacao());
            tipoAssistenteAnaliseContabil.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
            tipoAssistenteAnaliseContabil.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
            selecionado.getTipos().add(tipoAssistenteAnaliseContabil);
        }
    }

    public void finalizarImportacao() {
        if (futures != null) {
            try {
                for (Future<AssistenteAnaliseContabil> fut : futures) {
                    if (!fut.isDone()) {
                        return;
                    }
                }
                FacesUtil.executaJavaScript("terminarImportacao()");
                FacesUtil.atualizarComponente("Formulario");
                FacesUtil.addOperacaoRealizada("Validação realizada com sucesso");
                if (selecionado != null && selecionado.getAnalise() != null) {
                    facade.finalizarAnalise(selecionado.getAnalise());
                    for (CompletableFuture future : futures) {
                        singletonFuturesContabil.removerFutures(KEY_FUTURE_ANALISE_CONTABIL, future);
                    }
                }
            } catch (Exception e) {
                logger.error("Error no acompanhamento da validação de saldo contábil", e);
                FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            }

        }
    }

    public void abortar() {
        if (futures != null) {
            for (CompletableFuture<AssistenteAnaliseContabil> future : futures) {
                future.cancel(true);
                for (TipoAssistenteAnaliseContabil tipo : selecionado.getTipos()) {
                    tipo.setExecutando(false);
                }
            }
        }
    }

    public List<HierarquiaOrganizacional> completarUnidadeOrganizacional(String parte) {
        return facade.completarUnidadeOrganizacional(parte.trim());
    }

    public List<TipoAnaliseContabil> getTipos() {
        return Arrays.asList(TipoAnaliseContabil.values());
    }

    public void gerarPDFContabilSiconfi(AnaliseBalanceteContabilSiconfi analiseBalanceteContabil, Boolean exibirContaSiconfi) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (exibirContaSiconfi) {
                Html2Pdf.convert(getConteudoHtmlAnaliseContabilSiconfiPorContaAuxiliar(analiseBalanceteContabil), baos);
            } else {
                Html2Pdf.convert(getConteudoHtmlAnaliseContabilSiconfi(analiseBalanceteContabil), baos);
            }
            byte[] bytes = baos.toByteArray();
            AbstractReport.poeRelatorioNaSessao(bytes);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar o relatório. Detalhes do erro: " + e.getMessage());
        }
    }

    public void gerarPDFGrupoBemEstoque(AnaliseGrupoBemEstoque analise) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Html2Pdf.convert(getConteudoHtmlAnaliseGrupoBemEstoque(analise), baos);
            byte[] bytes = baos.toByteArray();
            AbstractReport.poeRelatorioNaSessao(bytes);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar o relatório. Detalhes do erro: " + e.getMessage());
        }
    }
    public void gerarPDFGrupoBemImovel(AnaliseGrupoBemImovel analise) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Html2Pdf.convert(getConteudoHtmlAnaliseGrupoBemImovel(analise), baos);
            byte[] bytes = baos.toByteArray();
            AbstractReport.poeRelatorioNaSessao(bytes);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar o relatório. Detalhes do erro: " + e.getMessage());
        }
    }

    public void gerarPDFGrupoBem(AnaliseGrupoBemMovel analise) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Html2Pdf.convert(getConteudoHtmlAnaliseGrupoBem(analise), baos);
            byte[] bytes = baos.toByteArray();
            AbstractReport.poeRelatorioNaSessao(bytes);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar o relatório. Detalhes do erro: " + e.getMessage());
        }
    }

    public void gerarPDFDividaPublica(AnaliseDividaPublica analiseDividaPublica) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Html2Pdf.convert(getConteudoHtmlAnaliseDividaPublica(analiseDividaPublica), baos);
            byte[] bytes = baos.toByteArray();
            AbstractReport.poeRelatorioNaSessao(bytes);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar o relatório. Detalhes do erro: " + e.getMessage());
        }
    }

    public void gerarPDFContabil(AnaliseBalanceteContabil analiseBalanceteContabil) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Html2Pdf.convert(getConteudoHtmlAnaliseContabil(analiseBalanceteContabil), baos);
            byte[] bytes = baos.toByteArray();
            AbstractReport.poeRelatorioNaSessao(bytes);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar o relatório. Detalhes do erro: " + e.getMessage());
        }
    }

    public void gerarPDFFinanceiro(AnaliseContaFinanceira analiseContaFinanceira) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Html2Pdf.convert(getConteudoHtmlAnaliseFinanceiro(analiseContaFinanceira), baos);
            byte[] bytes = baos.toByteArray();
            AbstractReport.poeRelatorioNaSessao(bytes);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar o relatório. Detalhes do erro: " + e.getMessage());
        }
    }

    private String getConteudoHtmlAnalise() {

        Integer quantidadeMovimentos = selecionado.getQuantidadeLancamentos();

        StringBuilder conteudo = new StringBuilder("");
        montaConteudoHTMLPadrao(conteudo);
        conteudo.append(" <hr noshade/>");
        conteudo.append(" <h3> Data inicial : " + DataUtil.getDataFormatada(selecionado.getDataInicial()) + " </h3> ");
        conteudo.append(" <h3> Data final : " + DataUtil.getDataFormatada(selecionado.getDataFinal()) + " </h3> ");
        conteudo.append(" <h3> Qtd de registros: " + quantidadeMovimentos + "</h3>");
        conteudo.append(" <hr noshade/>");

        conteudo.append("</div>");
        conteudo.append(" </body> </html>");
        return conteudo.toString();
    }

    private String getConteudoHtmlAnaliseContabilSiconfiPorContaAuxiliar(AnaliseBalanceteContabilSiconfi analiseBalanceteContabil) {

        StringBuilder listItens = new StringBuilder("");
        Integer quantidadeMovimentos = analiseBalanceteContabil.getLancamentos().size();
        listItens.append("<tr>");
        listItens.append("<td> Conta </td>");
        listItens.append("<td> Valor Débito</td>");
        listItens.append("<td> Valor Crédito</td>");
        listItens.append("<td> Saldo </td>");
        listItens.append("</tr>");

        Map<Conta, List<LancamentoContabil>> mapa = new HashMap<>();

        for (LancamentoContabil movimento : analiseBalanceteContabil.getLancamentos()) {
            adicionarContaMapa(movimento, mapa, movimento.getContaAuxiliarDebSiconfi());
            adicionarContaMapa(movimento, mapa, movimento.getContaAuxiliarCredSiconfi());
        }

        for (Map.Entry<Conta, List<LancamentoContabil>> contaListEntry : mapa.entrySet()) {
            Conta key = contaListEntry.getKey();
            List<LancamentoContabil> lista = contaListEntry.getValue();

            BigDecimal totalMovimentoCredito = new BigDecimal(0);
            BigDecimal totalMovimentoDebito = new BigDecimal(0);
            for (LancamentoContabil lancamentoContabil : lista) {
                if (lancamentoContabil.getContaAuxiliarCredSiconfi() != null && lancamentoContabil.getContaAuxiliarCredSiconfi().equals(key)) {
                    totalMovimentoCredito = totalMovimentoCredito.add(lancamentoContabil.getValor());
                }
                if (lancamentoContabil.getContaAuxiliarDebSiconfi() != null && lancamentoContabil.getContaAuxiliarDebSiconfi().equals(key)) {
                    totalMovimentoDebito = totalMovimentoDebito.add(lancamentoContabil.getValor());
                }
            }


            listItens.append("<tr>");
            listItens.append("<td> " + key.toString() + " </td>");
            listItens.append("<td> " + Util.getValorRemovendoRS(totalMovimentoDebito) + " </td>");
            listItens.append("<td> " + Util.getValorRemovendoRS(totalMovimentoCredito) + " </td>");
            listItens.append("<td> " + Util.getValorRemovendoRS(totalMovimentoCredito.subtract(totalMovimentoDebito)) + " </td>");
            listItens.append("</tr>");

        }

        StringBuilder conteudo = new StringBuilder("");
        montaConteudoHTMLPadrao(conteudo);
        conteudo.append(" <h3> Conta : " + analiseBalanceteContabil.getConta().toString() + " </h3> ");
        conteudo.append(" <h3> Unidade : " + analiseBalanceteContabil.getUnidadeOrganizacional().toString() + " </h3> ");
        conteudo.append(" <hr noshade/>");
        if (!listItens.toString().isEmpty()) {
            conteudo.append("<table style=\"width:100%;\" class=\"igualDataTable\">");
            conteudo.append(listItens);
            conteudo.append("</table>");
        }
        conteudo.append("<h3> Qtd de registros: " + mapa.size() + "</h3>");
        conteudo.append("</div>");
        conteudo.append(" </body> </html>");
        return conteudo.toString();
    }

    private static void adicionarContaMapa(LancamentoContabil movimento, Map<Conta, List<LancamentoContabil>> mapa, ContaAuxiliar contaAuxiliarCredito) {
        Conta conta = mapa.keySet().stream().filter(f -> f.getCodigo().equals(contaAuxiliarCredito.getCodigo())).findFirst().orElse(null);
        if (conta != null && mapa.get(conta) != null) {
            mapa.get(conta).add(movimento);
        } else {
            List<LancamentoContabil> lista = Lists.newArrayList();
            lista.add(movimento);
            mapa.put(contaAuxiliarCredito, lista);
        }
    }

    private String getConteudoHtmlAnaliseContabilSiconfi(AnaliseBalanceteContabilSiconfi analiseBalanceteContabil) {
        StringBuilder listItens = new StringBuilder("");

        BigDecimal totalMovimentoCredito = new BigDecimal(0);
        BigDecimal totalMovimentoDebito = new BigDecimal(0);
        Integer quantidadeMovimentos = analiseBalanceteContabil.getLancamentos().size();
        listItens.append("<tr>");
        listItens.append("<td> Data </td>");
        listItens.append("<td> Tipo </td>");
        listItens.append("<td> Conta Débito </td>");
        listItens.append("<td> Conta Crédito </td>");
        listItens.append("<td> Histórico </td>");
        listItens.append("<td> Valor </td>");
        listItens.append("</tr>");

        for (LancamentoContabil movimento : analiseBalanceteContabil.getLancamentos()) {
            listItens.append("<tr>");
            listItens.append("<td> " + DataUtil.getDataFormatada(movimento.getDataLancamento()) + " </td>");
            listItens.append("<td> " + movimento.getItemParametroEvento().getParametroEvento().getEventoContabil().getTipoEventoContabil().getDescricao() + " </td>");
            listItens.append("<td> " + movimento.getContaAuxiliarDebSiconfi().getCodigo() + " </td>");
            listItens.append("<td> " + movimento.getContaAuxiliarCredSiconfi().getCodigo() + " </td>");
            listItens.append("<td> " + movimento.getComplementoHistorico() + " </td>");
            listItens.append("<td> " + Util.getValorRemovendoRS(movimento.getValor()) + " </td>");
            listItens.append("</tr>");
            if (movimento.getContaCredito().equals(analiseBalanceteContabil.getConta())) {
                totalMovimentoCredito = totalMovimentoCredito.add(movimento.getValor());
            }
            if (movimento.getContaDebito().equals(analiseBalanceteContabil.getConta())) {
                totalMovimentoDebito = totalMovimentoDebito.add(movimento.getValor());
            }
        }
        StringBuilder conteudo = new StringBuilder("");
        montaConteudoHTMLPadrao(conteudo);
        conteudo.append(" <h3> Conta : " + analiseBalanceteContabil.getConta().toString() + " </h3> ");
        conteudo.append(" <h3> Unidade : " + analiseBalanceteContabil.getUnidadeOrganizacional().toString() + " </h3> ");
        conteudo.append(" <hr noshade/>");
        if (!listItens.toString().isEmpty()) {
            conteudo.append("<table style=\"width:100%;\" class=\"igualDataTable\">");
            conteudo.append(listItens);
            conteudo.append("</table>");
        }
        conteudo.append("<h3> Qtd de registros: " + quantidadeMovimentos + "</h3>");
        conteudo.append("<h3> Total de débito: " + Util.formataValor(totalMovimentoDebito) + "</h3>");
        conteudo.append("<h3> Total de crédito: " + Util.formataValor(totalMovimentoCredito) + "</h3>");
        conteudo.append("<h3> Saldo: " + Util.formataValor(totalMovimentoCredito.subtract(totalMovimentoDebito)) + "</h3>");
        conteudo.append("</div>");
        conteudo.append(" </body> </html>");
        return conteudo.toString();
    }

    private String getConteudoHtmlAnaliseGrupoBemEstoque(AnaliseGrupoBemEstoque analise) {
        StringBuilder listItens = new StringBuilder("");

        Integer quantidadeMovimentos = analise.getMovimentos().size();
        listItens.append("<tr>");
        listItens.append("<td> Data </td>");
        listItens.append("<td> Operação </td>");
        listItens.append("<td> Valor </td>");
        listItens.append("</tr>");

        for (ReprocessamentoSaldoGrupoBens movimento : analise.getMovimentos()) {
            listItens.append("<tr>");
            listItens.append("<td> " + DataUtil.getDataFormatada(movimento.getDataMovimento()) + " </td>");
            listItens.append("<td> " + movimento.getTipoOperacaoBensEstoque().getDescricao() + " </td>");
            listItens.append("<td> " + Util.getValorRemovendoRS(movimento.getValor()) + " </td>");
            listItens.append("</tr>");
        }
        StringBuilder conteudo = new StringBuilder("");
        montaConteudoHTMLPadrao(conteudo);
        conteudo.append(" <h3> Grupo : " + analise.getGrupoMaterial().toString() + " </h3> ");
        conteudo.append(" <h3> Unidade : " + analise.getUnidadeOrganizacional().toString() + " </h3> ");
        conteudo.append(" <h3> Tipo Grupo : " + analise.getTipoEstoque().getDescricao() + " </h3> ");
        conteudo.append(" <h3> Tipo Operação : " + analise.getTipoOperacaoBensEstoque().getDescricao() + " </h3> ");
        conteudo.append(" <hr noshade/>");
        if (!listItens.toString().isEmpty()) {
            conteudo.append("<table style=\"width:100%;\" class=\"igualDataTable\">");
            conteudo.append(listItens);
            conteudo.append("</table>");
        }
        conteudo.append("<h3> Qtd de registros: " + quantidadeMovimentos + "</h3>");
        conteudo.append("<h3> Total de crédito: " + Util.formataValor(analise.getTotalMovimentoCredito()) + "</h3>");
        conteudo.append("<h3> Total de débito: " + Util.formataValor(analise.getTotalMovimentoDebito()) + "</h3>");
        conteudo.append("<h3> Total : " + Util.formataValor(analise.getTotalMovimentoAtual()) + "</h3>");
        conteudo.append("</div>");
        conteudo.append(" </body> </html>");
        return conteudo.toString();
    }
    private String getConteudoHtmlAnaliseGrupoBemImovel(AnaliseGrupoBemImovel analise) {
        StringBuilder listItens = new StringBuilder("");

        Integer quantidadeMovimentos = analise.getMovimentos().size();
        listItens.append("<tr>");
        listItens.append("<td> Data </td>");
        listItens.append("<td> Tipo </td>");
        listItens.append("<td> Valor </td>");
        listItens.append("</tr>");

        for (ReprocessamentoSaldoGrupoBens movimento : analise.getMovimentos()) {
            listItens.append("<tr>");
            listItens.append("<td> " + DataUtil.getDataFormatada(movimento.getDataMovimento()) + " </td>");
            listItens.append("<td> " + analise.getTipoOperacaoBensImoveis().getDescricao()  + " </td>");
            listItens.append("<td> " + Util.getValorRemovendoRS(movimento.getValor()) + " </td>");
            listItens.append("</tr>");
        }
        StringBuilder conteudo = new StringBuilder("");
        montaConteudoHTMLPadrao(conteudo);
        conteudo.append(" <h3> Grupo : " + analise.getGrupoBem().toString() + " </h3> ");
        conteudo.append(" <h3> Unidade : " + analise.getUnidadeOrganizacional().toString() + " </h3> ");
        conteudo.append(" <h3> Tipo Grupo : " + analise.getTipoGrupo().getDescricao() + " </h3> ");
        conteudo.append(" <hr noshade/>");
        if (!listItens.toString().isEmpty()) {
            conteudo.append("<table style=\"width:100%;\" class=\"igualDataTable\">");
            conteudo.append(listItens);
            conteudo.append("</table>");
        }
        conteudo.append("<h3> Qtd de registros: " + quantidadeMovimentos + "</h3>");
        conteudo.append("<h3> Total de crédito: " + Util.formataValor(analise.getTotalMovimentoCredito()) + "</h3>");
        conteudo.append("<h3> Total de débito: " + Util.formataValor(analise.getTotalMovimentoDebito()) + "</h3>");
        conteudo.append("<h3> Total : " + Util.formataValor(analise.getTotalMovimentoAtual()) + "</h3>");
        conteudo.append("</div>");
        conteudo.append(" </body> </html>");
        return conteudo.toString();
    }

    private String getConteudoHtmlAnaliseGrupoBem(AnaliseGrupoBemMovel analise) {
        StringBuilder listItens = new StringBuilder("");

        Integer quantidadeMovimentos = analise.getMovimentos().size();
        listItens.append("<tr>");
        listItens.append("<td> Data </td>");
        listItens.append("<td> Tipo Operação </td>");
        listItens.append("<td> Valor </td>");
        listItens.append("</tr>");

        for (ReprocessamentoSaldoGrupoBens movimento : analise.getMovimentos()) {
            listItens.append("<tr>");
            listItens.append("<td> " + DataUtil.getDataFormatada(movimento.getDataMovimento()) + " </td>");
            listItens.append("<td> " + analise.getTipoOperacaoBensMoveis().getDescricao()  + " </td>");
            listItens.append("<td> " + Util.getValorRemovendoRS(movimento.getValor()) + " </td>");
            listItens.append("</tr>");
        }
        StringBuilder conteudo = new StringBuilder("");
        montaConteudoHTMLPadrao(conteudo);
        conteudo.append(" <h3> Grupo : " + analise.getGrupoBem().toString() + " </h3> ");
        conteudo.append(" <h3> Unidade : " + analise.getUnidadeOrganizacional().toString() + " </h3> ");
        conteudo.append(" <h3> Tipo Grupo : " + analise.getTipoGrupo().getDescricao() + " </h3> ");
        conteudo.append(" <hr noshade/>");
        if (!listItens.toString().isEmpty()) {
            conteudo.append("<table style=\"width:100%;\" class=\"igualDataTable\">");
            conteudo.append(listItens);
            conteudo.append("</table>");
        }
        conteudo.append("<h3> Qtd de registros: " + quantidadeMovimentos + "</h3>");
        conteudo.append("<h3> Total de crédito: " + Util.formataValor(analise.getTotalMovimentoCredito()) + "</h3>");
        conteudo.append("<h3> Total de débito: " + Util.formataValor(analise.getTotalMovimentoDebito()) + "</h3>");
        conteudo.append("<h3> Total : " + Util.formataValor(analise.getTotalMovimentoAtual()) + "</h3>");
        conteudo.append("</div>");
        conteudo.append(" </body> </html>");
        return conteudo.toString();
    }
    private String getConteudoHtmlAnaliseDividaPublica(AnaliseDividaPublica analiseDividaPublica) {

        StringBuilder listItens = new StringBuilder("");

        Integer quantidadeMovimentos = analiseDividaPublica.getMovimentos().size();
        listItens.append("<tr>");
        listItens.append("<td> Data </td>");
        listItens.append("<td> Tipo </td>");
        listItens.append("<td> Valor </td>");
        listItens.append("</tr>");

        for (SaldoDividaPublicaReprocessamento movimento : analiseDividaPublica.getMovimentos()) {
            listItens.append("<tr>");
            listItens.append("<td> " + DataUtil.getDataFormatada(movimento.getData()) + " </td>");
            listItens.append("<td> " + movimento.getOperacaoMovimentoDividaPublica() + " </td>");
            listItens.append("<td> " + Util.getValorRemovendoRS(movimento.getValor()) + " </td>");
            listItens.append("</tr>");
        }
        StringBuilder conteudo = new StringBuilder("");
        montaConteudoHTMLPadrao(conteudo);
        conteudo.append(" <h3> Dívida Pública : " + analiseDividaPublica.getDividapublica().toString() + " </h3> ");
        conteudo.append(" <h3> Unidade : " + analiseDividaPublica.getUnidadeOrganizacional().toString() + " </h3> ");
        conteudo.append(" <h3> Fonte : " + analiseDividaPublica.getContaDeDestinacao().getFonteDeRecursos() + " </h3> ");
        conteudo.append(" <hr noshade/>");
        if (!listItens.toString().isEmpty()) {
            conteudo.append("<table style=\"width:100%;\" class=\"igualDataTable\">");
            conteudo.append(listItens);
            conteudo.append("</table>");
        }
        conteudo.append("<h3> Qtd de registros: " + quantidadeMovimentos + "</h3>");
        conteudo.append("<h3> Total de Crédito: " + Util.formataValor(analiseDividaPublica.getTotalMovimentoCredito()) + "</h3>");
        conteudo.append("<h3> Total de Débito : " + Util.formataValor(analiseDividaPublica.getTotalMovimentoDebito()) + "</h3>");
        conteudo.append("</div>");
        conteudo.append(" </body> </html>");
        return conteudo.toString();
    }

    private String getConteudoHtmlAnaliseContabil(AnaliseBalanceteContabil analiseBalanceteContabil) {
        StringBuilder listItens = new StringBuilder("");

        BigDecimal totalMovimentoCredito = new BigDecimal(0);
        BigDecimal totalMovimentoDebito = new BigDecimal(0);
        Integer quantidadeMovimentos = analiseBalanceteContabil.getLancamentos().size();
        listItens.append("<tr>");
        listItens.append("<td> Data </td>");
        listItens.append("<td> Tipo </td>");
        listItens.append("<td> Histórico </td>");
        listItens.append("<td> Valor </td>");
        listItens.append("</tr>");

        for (LancamentoContabil movimento : analiseBalanceteContabil.getLancamentos()) {
            listItens.append("<tr>");
            listItens.append("<td> " + DataUtil.getDataFormatada(movimento.getDataLancamento()) + " </td>");
            listItens.append("<td> " + movimento.getItemParametroEvento().getParametroEvento().getEventoContabil().getTipoEventoContabil().getDescricao() + " </td>");
            listItens.append("<td> " + movimento.getComplementoHistorico() + " </td>");
            listItens.append("<td> " + Util.getValorRemovendoRS(movimento.getValor()) + " </td>");
            listItens.append("</tr>");
            if (movimento.getContaCredito().equals(analiseBalanceteContabil.getConta())) {
                totalMovimentoCredito = totalMovimentoCredito.add(movimento.getValor());
            }
            if (movimento.getContaDebito().equals(analiseBalanceteContabil.getConta())) {
                totalMovimentoDebito = totalMovimentoDebito.add(movimento.getValor());
            }
        }
        StringBuilder conteudo = new StringBuilder("");
        montaConteudoHTMLPadrao(conteudo);
        conteudo.append(" <h3> Conta : " + analiseBalanceteContabil.getConta().toString() + " </h3> ");
        conteudo.append(" <h3> Unidade : " + analiseBalanceteContabil.getUnidadeOrganizacional().toString() + " </h3> ");
        conteudo.append(" <hr noshade/>");
        if (!listItens.toString().isEmpty()) {
            conteudo.append("<table style=\"width:100%;\" class=\"igualDataTable\">");
            conteudo.append(listItens);
            conteudo.append("</table>");
        }
        conteudo.append("<h3> Qtd de registros: " + quantidadeMovimentos + "</h3>");
        conteudo.append("<h3> Total de débito: " + Util.formataValor(totalMovimentoDebito) + "</h3>");
        conteudo.append("<h3> Total de crédito: " + Util.formataValor(totalMovimentoCredito) + "</h3>");
        conteudo.append("<h3> Saldo: " + Util.formataValor(totalMovimentoCredito.subtract(totalMovimentoDebito)) + "</h3>");
        conteudo.append("</div>");
        conteudo.append(" </body> </html>");
        return conteudo.toString();
    }

    private String getConteudoHtmlAnaliseFinanceiro(AnaliseContaFinanceira analiseContaFinanceira) {
        StringBuilder listItens = new StringBuilder("");

        BigDecimal totalMovimentoCredito = new BigDecimal(0);
        BigDecimal totalMovimentoDebito = new BigDecimal(0);
        Integer quantidadeMovimentos = analiseContaFinanceira.getReprocessamentoSubContas().size();
        listItens.append("<tr>");
        listItens.append("<td> Data </td>");
        listItens.append("<td> Tipo </td>");
        listItens.append("<td> Histórico </td>");
        listItens.append("<td> Valor Débito </td>");
        listItens.append("<td> Valor Crédito </td>");
        listItens.append("</tr>");

        for (ReprocessamentoSubConta movimento : analiseContaFinanceira.getReprocessamentoSubContas()) {
            listItens.append("<tr>");
            listItens.append("<td> " + DataUtil.getDataFormatada(movimento.getDataMovimento()) + " </td>");
            listItens.append("<td> " + movimento.getTipoMovimento().getDescricao() + " </td>");
            listItens.append("<td> " + movimento.getHistorico() + " </td>");
            listItens.append("<td> " + Util.getValorRemovendoRS(movimento.getValorDebito()) + " </td>");
            listItens.append("<td> " + Util.getValorRemovendoRS(movimento.getValorCredito()) + " </td>");
            listItens.append("</tr>");
            totalMovimentoCredito = totalMovimentoCredito.add(movimento.getValorCredito());
            totalMovimentoDebito = totalMovimentoDebito.add(movimento.getValorDebito());
        }
        StringBuilder conteudo = new StringBuilder("");
        montaConteudoHTMLPadrao(conteudo);
        conteudo.append(" <h3> Conta Financeira : " + analiseContaFinanceira.getSubConta().toString() + " </h3> ");
        conteudo.append(" <h3> Unidade : " + analiseContaFinanceira.getUnidadeOrganizacional().toString() + " </h3> ");
        conteudo.append(" <h3> Fonte : " + analiseContaFinanceira.getContaDeDestinacao().getFonteDeRecursos().toString() + " </h3> ");
        conteudo.append(" <hr noshade/>");
        if (!listItens.toString().isEmpty()) {
            conteudo.append("<table style=\"width:100%;\" class=\"igualDataTable\">");
            conteudo.append(listItens);
            conteudo.append("</table>");
        }
        conteudo.append("<h3> Qtd de registros: " + quantidadeMovimentos + "</h3>");
        conteudo.append("<h3> Total de crédito: " + Util.formataValor(totalMovimentoCredito) + "</h3>");
        conteudo.append("<h3> Total de débito: " + Util.formataValor(totalMovimentoDebito) + "</h3>");
        conteudo.append("<h3> Saldo: " + Util.formataValor(totalMovimentoCredito.subtract(totalMovimentoDebito)) + "</h3>");
        conteudo.append("</div>");
        conteudo.append(" </body> </html>");
        return conteudo.toString();
    }

    private void montaConteudoHTMLPadrao(StringBuilder conteudo) {
        Date hoje = new Date();
        conteudo.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>");
        conteudo.append(" <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        conteudo.append(" <html>");
        conteudo.append(" <head>");
        conteudo.append(" <style type=\"text/css\"  media=\"all\">");
        conteudo.append(" @page{");
        conteudo.append(" size: A4 landscape; ");
        conteudo.append(" margin-top: 1.5in;");
        conteudo.append(" margin-bottom: 1.0in;");
        conteudo.append(" @bottom-center {");
        conteudo.append(" content: element(footer);");
        conteudo.append(" }");
        conteudo.append(" @top-center {");
        conteudo.append(" content: element(header);");
        conteudo.append(" }");
        conteudo.append("}");
        conteudo.append("#page-header {");
        conteudo.append(" display: block;");
        conteudo.append(" position: running(header);");
        conteudo.append(" }");
        conteudo.append(" #page-footer {");
        conteudo.append(" display: block;");
        conteudo.append(" position: running(footer);");
        conteudo.append(" }");
        conteudo.append(" .page-number:before {");
        conteudo.append("  content: counter(page) ");
        conteudo.append(" }");
        conteudo.append(" .page-count:before {");
        conteudo.append(" content: counter(pages);  ");
        conteudo.append("}");
        conteudo.append("</style>");
        conteudo.append(" <style type=\"text/css\">");
        conteudo.append(".igualDataTable{");
        conteudo.append("    border-collapse: collapse; /* CSS2 */");
        conteudo.append("    width: 100%;");
        conteudo.append("    ;");
        conteudo.append("}");
        conteudo.append(".igualDataTable th{");
        conteudo.append("    border: 0px solid #aaaaaa; ");
        conteudo.append("    height: 20px;");
        conteudo.append("    background: #ebebeb 50% 50% repeat-x;");
        conteudo.append("}");
        conteudo.append(".igualDataTable td{");
        conteudo.append("    padding: 2px;");
        conteudo.append("    border: 0px; ");
        conteudo.append("    height: 20px;");
        conteudo.append("}");
        conteudo.append(".igualDataTable thead td{");
        conteudo.append("    border: 1px solid #aaaaaa; ");
        conteudo.append("    background: #6E95A6 repeat-x scroll 50% 50%; ");
        conteudo.append("    border: 0px; ");
        conteudo.append("    text-align: center; ");
        conteudo.append("    height: 20px;");
        conteudo.append("}");
        conteudo.append(" .igualDataTable tr:nth-child(2n+1) {");
        conteudo.append(" background:lightgray;");
        conteudo.append(" }");
        conteudo.append("body{");
        conteudo.append("font-size: 8pt; font-family:\"Arial\", Helvetica, sans-serif");
        conteudo.append("}");
        conteudo.append("</style>");
        conteudo.append(" <title>");
        conteudo.append(" < META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-1\">");
        conteudo.append(" </title>");
        conteudo.append(" </head>");
        conteudo.append(" <body>");
        conteudo.append(" <div id=\"page-header\">");
        conteudo.append(" <table style='line-height:15%; width: 100%;'>");
        conteudo.append("<tr>");
        conteudo.append("<td >");
        conteudo.append(adicionaCabecalho());
        conteudo.append("</td >");
        conteudo.append("</tr>");
        conteudo.append(" </table>");
        conteudo.append(" </div>");
        conteudo.append(" <div id=\"page-footer\">");
        conteudo.append(" <hr noshade/>");
        conteudo.append(" <table style='line-height:15%; width: 100%;'>");
        conteudo.append("<tr>");
        conteudo.append("<td >");
        conteudo.append("<p>");
        conteudo.append("WebPúblico");
        conteudo.append("</p>");
        conteudo.append("</td>");
        conteudo.append("<td style='text-align: right'>");
        conteudo.append("<p>");
        conteudo.append("Usuário: ");
        conteudo.append(facade.getSistemaFacade().getLogin());
        conteudo.append(" - Emitido em ");
        conteudo.append(Util.formatterDiaMesAno.format(hoje));
        conteudo.append(" às ").append(Util.formatterHoraMinuto.format(hoje));
        conteudo.append("</p>");
        conteudo.append("</td>");
        conteudo.append("</tr>");
        conteudo.append(" </table>");
        conteudo.append(" </div>");
        conteudo.append(" <div id=\"page-content\">");
        conteudo.append(" <br/>");
    }

    public String adicionaCabecalho() {
        String caminhoDaImagem = facade.getDocumentoOficialFacade().geraUrlImagemDir() + "img/Brasao_de_Rio_Branco_novo.gif";
        String conteudo = "<table>" + "<tbody>" + "<tr>" + "<td style=\"text-align: center;\">" + "<img src=\"" + caminhoDaImagem + "\" width=\"54\" height=\"70\" alt=\"PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO\" />" + "</td>" + "<td style=\"line-height:100%; \">" + "<h2>Prefeitura do Município de Rio Branco</h2>" + "<h3>" + facade.getSistemaFacade().getSistemaService().getHierarquiAdministrativaCorrente().getDescricao() + " </h3>" + " <h3> Relatório de Analise Contábil - Data: " + DataUtil.getDataFormatada(selecionado.getDataFinal()) + "</h3>" + "</td>" + "</tr>" + "</tbody>" + "</table>";
        return conteudo;
    }

    public List<String> getTiposSelecionados() {
        return tiposSelecionados;
    }

    public void setTiposSelecionados(List<String> tiposSelecionados) {
        this.tiposSelecionados = tiposSelecionados;
    }

    public List<SelectItem> buscarOperacaoORC() {
        return Util.getListSelectItem(OperacaoORC.values(), true);
    }

    public List<SelectItem> buscarTipoOperacaoORC() {
        return Util.getListSelectItem(TipoOperacaoORC.values(), true);
    }

    public AnaliseGerarSaldoOrcamentario getAnaliseGerarSaldoOrcamentario() {
        return analiseGerarSaldoOrcamentario;
    }

    public void setAnaliseGerarSaldoOrcamentario(AnaliseGerarSaldoOrcamentario analiseGerarSaldoOrcamentario) {
        this.analiseGerarSaldoOrcamentario = analiseGerarSaldoOrcamentario;
    }

    public void novoGerarSaldoOrcamentario() {
        this.analiseGerarSaldoOrcamentario = new AnaliseGerarSaldoOrcamentario();
    }

    public void removerGerarSaldoOrcamentario(AnaliseGerarSaldoOrcamentario ob) {
        this.selecionado.getGerarSaldoOrcamentarios().remove(ob);
    }

    public void adicionarGerarSaldoOrcamentario() {
        try {
            if (selecionado.getHierarquiaOrganizacional() != null) {
                analiseGerarSaldoOrcamentario.setUnidade(selecionado.getHierarquiaOrganizacional().getSubordinada());
            }
            selecionado.setGerarSaldoOrcamentarios(Util.adicionarObjetoEmLista(selecionado.getGerarSaldoOrcamentarios(), analiseGerarSaldoOrcamentario));
            this.analiseGerarSaldoOrcamentario = null;
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida("Erro ao adicionar registro. " + e.getMessage());
        }
    }
}
