package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.controlerelatorio.RelatorioMapaArrecadacaoConsolidadoControlador;
import br.com.webpublico.controlerelatorio.RelatorioReceitaDiariaControlador;
import br.com.webpublico.entidades.LoteBaixa;
import br.com.webpublico.entidades.LoteBaixaDoArquivo;
import br.com.webpublico.entidades.SubConta;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.entidadesauxiliares.MapaArrecadacao;
import br.com.webpublico.entidadesauxiliares.RelatorioArrecadacaoConsolidadoTributoConta;
import br.com.webpublico.entidadesauxiliares.RelatorioMapaArrecadacao;
import br.com.webpublico.entidadesauxiliares.RelatorioReceitaDiariaArrecadacao;
import br.com.webpublico.enums.TipoAgrupamentoMapaConsolidado;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ImprimeRelatoriosArrecadacao extends AbstractReport {

    private static final Logger logger = LoggerFactory.getLogger(ImprimeRelatoriosArrecadacao.class);

    public void imprimePagamentoPorLote(LoteBaixa lote) {
        try {
            validarLote(lote);
            String condicao = " where lotebaixa.id = " + lote.getId();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("WHERE", condicao);
            dto.setNomeRelatorio("RELATÓRIO-DE-ARRECAÇÃO-POR-LOTE");
            dto.setApi("tributario/pagamento-por-lote/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarLote(LoteBaixa lote) {
        ValidacaoException ve = new ValidacaoException();
        if (lote == null || lote.getId() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Lote informado é nulo ou ainda não foi salvo");
        }
        ve.lancarException();
    }

    public void imprimeMapaArrecadacao(LoteBaixa lote) {
        if (lote == null || lote.getId() == null) {
            throw new ExcecaoNegocioGenerica("O Lote informado é nulo ou ainda não foi salvo");
        } else {
            String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
            subReport += "/report/";
            StringBuilder where = new StringBuilder();
            String caminhoBrasao = getCaminhoImagem();
            where.append("where").append(" lotebaixa.id = ").append(lote.getId());
            HashMap parameters = new HashMap();
            parameters.put("USUARIO", SistemaFacade.obtemLogin());
            parameters.put("BRASAO", caminhoBrasao);
            parameters.put("WHERE", where.toString());
            parameters.put("SUBREPORT_DIR", subReport);
            try {
                gerarRelatorio("MapaArrecadacao.jasper", parameters);
            } catch (Exception ex) {
                logger.error("Erro: ", ex);
            }
        }
    }

    public void imprimeMapaArrecadacao(List<LoteBaixa> lotes,
                                       RelatorioMapaArrecadacaoConsolidadoControlador.FiltroRelatorioMapaConsolidado filtro,
                                       LoteBaixaDoArquivo loteBaixaDoArquivo,
                                       List<MapaArrecadacao> listaArrecadacao,
                                       List<MapaArrecadacao> listaAcrescimos,
                                       List<MapaArrecadacao> listaDescontos,
                                       String arquivoJasper) {
        String linha1Filtros = "";
        String linha2Filtros = "";
        String linha3Filtros = "";
        String linha4Filtros = "";
        String linha5Filtros = "";
        String linha6Filtros = "";
        List<RelatorioMapaArrecadacao> listaRelatorio = Lists.newArrayList();
        for (MapaArrecadacao arrecadacao : listaArrecadacao) {
            RelatorioMapaArrecadacao grupo = criaRelatorio(arrecadacao);
            if (!listaRelatorio.contains(grupo)) {
                listaRelatorio.add(grupo);
            }
            listaRelatorio.get(listaRelatorio.indexOf(grupo)).getArrecadacoes().add(arrecadacao);
        }

        for (MapaArrecadacao acrescimo : listaAcrescimos) {
            RelatorioMapaArrecadacao grupo = criaRelatorio(acrescimo);
            if (!listaRelatorio.contains(grupo)) {
                listaRelatorio.add(grupo);
            }
            listaRelatorio.get(listaRelatorio.indexOf(grupo)).getAcrescimos().add(acrescimo);
        }
        for (MapaArrecadacao desconto : listaDescontos) {
            RelatorioMapaArrecadacao grupo = criaRelatorio(desconto);
            if (!listaRelatorio.contains(grupo)) {
                listaRelatorio.add(grupo);
            }
            listaRelatorio.get(listaRelatorio.indexOf(grupo)).getDescontos().add(desconto);
        }

        Collections.sort(listaRelatorio);
        try {
            if (lotes.size() > 1) {
                linha1Filtros = "Data de Pagamento: "
                    + new SimpleDateFormat("dd/MM/yyyy").format(filtro.getDataInicio())
                    + " até "
                    + new SimpleDateFormat("dd/MM/yyyy").format(filtro.getDataFinal());
                if (!filtro.getSubContas().isEmpty() && filtro.getSubContas().size() == 1) {
                    linha2Filtros = "Banco: " + filtro.getSubContas().get(0).getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco()
                        + " - " + filtro.getSubContas().get(0).getContaBancariaEntidade().getAgencia().getBanco().getDescricao();
                    linha3Filtros = "Conta: " + filtro.getSubContas().get(0).getContaBancariaEntidade().getNumeroConta()
                        + "-" + filtro.getSubContas().get(0).getContaBancariaEntidade().getDigitoVerificador();
                }
                linha4Filtros = "";
                linha5Filtros = "";
                linha6Filtros = "";
            } else {
                LoteBaixa unico = lotes.get(0);

                linha1Filtros = "Data de Pagamento: " + new SimpleDateFormat("dd/MM/yyyy").format(unico.getDataPagamento());
                linha2Filtros = "Data de Movimento: " + new SimpleDateFormat("dd/MM/yyyy").format(unico.getDataFinanciamento());
                if (unico.getSubConta() != null) {
                    linha3Filtros = "Agente:" + unico.getSubConta().getCodigo() + " - " + unico.getSubConta().getDescricao();
                    linha4Filtros = "Banco: " + unico.getBanco().getNumeroBanco()
                        + " Conta: " + unico.getSubConta().getContaBancariaEntidade().getNumeroConta()
                        + "-" + unico.getSubConta().getContaBancariaEntidade().getDigitoVerificador();
                } else {
                    linha4Filtros = unico.getBanco().getNumeroBanco();
                }

                linha5Filtros = "Forma de Pagamento: " + unico.getFormaPagamento().getDescricao();
                linha6Filtros = "Lote: " + unico.getCodigoLote() + " ";
                if (loteBaixaDoArquivo != null) {
                    linha6Filtros += "Arquivo: " + loteBaixaDoArquivo.getNumero();
                }

            }
            HashMap parameters = new HashMap();

            AbstractReport report = AbstractReport.getAbstractReport();
            parameters.put("LINHA1FILTROS", linha1Filtros);
            parameters.put("LINHA2FILTROS", linha2Filtros);
            parameters.put("LINHA3FILTROS", linha3Filtros);
            parameters.put("LINHA4FILTROS", linha4Filtros);
            parameters.put("LINHA5FILTROS", linha5Filtros);
            parameters.put("LINHA6FILTROS", linha6Filtros);
            parameters.put("BRASAO", report.getCaminhoImagem());
            parameters.put("SUBREPORT_DIR", report.getCaminho());
            parameters.put("USUARIO", SistemaFacade.obtemLogin());

            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(listaRelatorio);
            JasperPrint jasperPrint = JasperFillManager.fillReport(report.getServletContext().getRealPath("/WEB-INF/report/" + arquivoJasper), parameters, ds);
            report.escreveNoResponse(arquivoJasper, preparaExportaReport(jasperPrint).toByteArray());
        } catch (Exception ex) {
            logger.error("Erro ao imprimir Mapa Arrecadacao: {]", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private RelatorioMapaArrecadacao criaRelatorio(MapaArrecadacao arrecadacao) {
        return new RelatorioMapaArrecadacao(arrecadacao.getPagamento(), arrecadacao.getAgenteArrecadador());
    }

    public void imprimePagamentoPorLoteDetalhado(LoteBaixa lote) {
        if (lote == null || lote.getId() == null) {
            throw new ExcecaoNegocioGenerica("O Lote informado é nulo ou ainda não foi salvo");
        } else {
            String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
            subReport += "/report/";
            StringBuilder where = new StringBuilder();
            String caminhoBrasao = getCaminhoImagem();
            where.append("where").append(" lotebaixa.id = ").append(lote.getId());
            HashMap parameters = new HashMap();
            parameters.put("USUARIO", SistemaFacade.obtemLogin());
            parameters.put("BRASAO", caminhoBrasao);
            parameters.put("WHERE", where.toString());
            parameters.put("SUBREPORT_DIR", subReport);
            try {
                gerarRelatorio("RelacaoConferenciaDetalhadaArrecadacao.jasper", parameters);
            } catch (JRException ex) {
                logger.error("Erro: ", ex);
            } catch (IOException ex) {
                logger.error("Erro: ", ex);
            }
        }
    }

    public void imprimeMapaConsolidadoTributoConta(RelatorioArrecadacaoConsolidadoTributoContaControlador.Filtro filtros,
                                                   RelatorioArrecadacaoConsolidadoTributoConta dados) {
        try {
            StringBuilder filtro = new StringBuilder();
            if (filtros.getExercicio() != null) {
                filtro.append("Exercício do Débito: ").append(filtros.getExercicio().getAno()).append(";");
            }
            filtro.append(" Intervalo de datas : ");
            filtro.append(Util.dateToString(filtros.getDataInicial()));
            filtro.append(" - ");
            filtro.append(Util.dateToString(filtros.getDataFinal()));
            filtro.append(";");
            if (filtros.getSubContas() != null) {
                filtro.append(" Banco e Conta Bancária: ");
                if (filtros.getSubContas().isEmpty()) {
                    filtro.append("TODAS");
                } else {
                    for (SubConta subConta : filtros.getSubContas()) {
                        filtro.append(subConta);
                        filtro.append(", ");
                    }
                }
                filtro.append("; ");
            }
            if (filtros.getIdsTributosSelecionados() != null) {
                filtro.append(" Tributos: ");
                if (filtros.getTributosSelecionados().isEmpty()) {
                    filtro.append("TODOS");
                } else {
                    for (Tributo tributo : filtros.getTributosSelecionados()) {
                        filtro.append(tributo.getCodigo());
                        filtro.append(", ");
                    }
                }
                filtro.append("; ");
            }

            List<RelatorioArrecadacaoConsolidadoTributoConta> relatorio = Lists.newArrayList();
            relatorio.add(dados);

            HashMap parameters = new HashMap();
            AbstractReport report = AbstractReport.getAbstractReport();
            parameters.put("BRASAO", report.getCaminhoImagem());
            parameters.put("SUBREPORT_DIR", report.getCaminho());
            parameters.put("USUARIO", SistemaFacade.obtemLogin());
            parameters.put("FILTROS", filtro.toString());

            String arquivoJasper = nomeJasperMapaConsolidado(filtros.getTipoAgrupamento());
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(relatorio);
            JasperPrint jasperPrint = JasperFillManager.fillReport(report.getServletContext().getRealPath("/WEB-INF/report/" + arquivoJasper), parameters, ds);
            report.escreveNoResponse(arquivoJasper, preparaExportaReport(jasperPrint).toByteArray());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public String nomeJasperMapaConsolidado(TipoAgrupamentoMapaConsolidado tipoAgrupamento) {
        if (tipoAgrupamento == null) {
            return "";
        }
        switch (tipoAgrupamento) {
            case TRIBUTO: {
                return "RelatorioArrecadacaoConsolidadoTributo.jasper";
            }
            case CONTA_RECEITA: {
                return "RelatorioArrecadacaoConsolidadoContaReceita.jasper";
            }
            case BANCO_CONTA_RECEITA_EXERCICIO:
            case BANCO_CONTA_RECEITA: {
                return "RelatorioArrecadacaoConsolidadoBancoContaReceita.jasper";
            }
            case BANCO_CONTA_RECEITA_DATA: {
                return "RelatorioArrecadacaoConsolidadoBancoContaReceitaData.jasper";
            }
            default: {
                return "";
            }
        }
    }
}
