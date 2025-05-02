package br.com.webpublico.negocios;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.relatoriofacade.RelatorioTransparenciaFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Locale;

@Stateless
public class TransparenciaFacade extends AbstractReport {

    private static final Logger logger = LoggerFactory.getLogger(TransparenciaFacade.class);
    private final String separator = System.getProperty("file.separator");
    private final String report = Util.getApplicationPath("/WEB-INF/") + separator + "report" + separator;
    private final String img = Util.getApplicationPath("/img/") + separator;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private RelatorioTransparenciaFacade relatorioTransparenciaFacade;
    @EJB
    private UnidadeGestoraFacade unidadeGestoraFacade;

    public UnidadeGestoraFacade getUnidadeGestoraFacade() {
        return unidadeGestoraFacade;
    }

    public ByteArrayOutputStream gerarRelatorioCompras(Mes mes, Exercicio exercicio, UnidadeGestora unidadeGestora, String arquivoJasper) throws IOException, JRException {
        if (mes != null && exercicio != null) {
            HashMap parameters = new HashMap();
            parametrosComuns(mes, exercicio, parameters, unidadeGestora);
            parameters.put("SUBREPORT_DIR", report);
            parameters.put("DATAINICIAL", getDataInicial(mes, exercicio));
            parameters.put("DATAFINAL", getDataFinal(mes, exercicio));
            if (unidadeGestora != null && unidadeGestora.getCodigo().equals("001")) {
                parameters.put("UG", unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao());
                parameters.put("NOMERELATORIO", "Relatório de Despesas Detalhadas");
            } else {
                parameters.put("NOMERELATORIO", "Relatório de Compras");
            }
            parameters.put("ELEMENTODESPESA", relatorioTransparenciaFacade.recuperaRelatorioComprasElementoDespesa(parameters, unidadeGestora != null));
            parameters.put("ANULACOES", relatorioTransparenciaFacade.recuperaRelatorioComprasAnulacoes(parameters, unidadeGestora != null));
            parameters.put("RESUMOGERAL", relatorioTransparenciaFacade.recuperaRelatorioComprasResumoGeral(parameters, unidadeGestora != null));
            parameters.put("RESUMOORGAO", relatorioTransparenciaFacade.recuperaRelatorioComprasResumoOrgao(parameters, unidadeGestora != null));
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(relatorioTransparenciaFacade.recuperaRelatorioComprasLiquidacao(parameters, unidadeGestora != null));
            return gerarBytesEmPdfDeRelatorioComDadosEmCollectionView(getCaminho(), arquivoJasper, parameters, ds);
        }

        return null;
    }


    public ByteArrayOutputStream gerarRelatorioReceitas(Mes mes, Exercicio exercicio, UnidadeGestora unidadeGestora, String arquivoJasper) throws IOException, JRException {
        if (mes != null && exercicio != null) {
            HashMap parameters = new HashMap();
            parametrosComuns(mes, exercicio, parameters, unidadeGestora);
            JRBeanCollectionDataSource ds;
            if (unidadeGestora != null && unidadeGestora.getCodigo().equals("001")) {
                parameters.put("UG", unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao());
                arquivoJasper = "RelatorioAcompanhamentoLiberacaoFinanceira";
                parameters.put("MESFINAL", mes.getNumeroMesString() + "/" + exercicio.getAno());
                Integer mFinal = Integer.parseInt(mes.getNumeroMesString());
                if (mes.getNumeroMes() == 1) {
                    parameters.put("DATAGERACAO_1MES", mes.getNumeroMesString() + "/" + exercicio.getAno());
                    parameters.put("DATAGERACAO_2MES", null);
                    parameters.put("DATAGERACAO_3MES", null);
                    parameters.put("DATAGERACAO_4MES", null);

                    parameters.put("MESLIBERADO_1MES", DataUtil.retornaDescricaoMes((mFinal) < 10 ? "0" + (mFinal) + "" : (mFinal) + ""));
                    parameters.put("MESLIBERADO_2MES", DataUtil.retornaDescricaoMes((mFinal + 1) < 10 ? "0" + (mFinal + 1) + "" : (mFinal + 1) + ""));
                    parameters.put("MESLIBERADO_3MES", DataUtil.retornaDescricaoMes((mFinal + 2) < 10 ? "0" + (mFinal + 2) + "" : (mFinal + 2) + ""));
                    parameters.put("MESLIBERADO_4MES", DataUtil.retornaDescricaoMes((mFinal + 3) < 10 ? "0" + (mFinal + 3) + "" : (mFinal + 3) + ""));
                    arquivoJasper += "1Mes";
                } else if (mes.getNumeroMes() == 2) {
                    parameters.put("DATAGERACAO_1MES", (mes.getNumeroMes() - 1) < 10 ? "0" + (mes.getNumeroMes() - 1) + "/" + exercicio.getAno() : (mes.getNumeroMes() - 1) + "/" + exercicio.getAno());
                    parameters.put("DATAGERACAO_2MES", mes.getNumeroMesString() + "/" + exercicio.getAno());
                    parameters.put("DATAGERACAO_3MES", null);
                    parameters.put("DATAGERACAO_4MES", null);

                    parameters.put("MESLIBERADO_1MES", DataUtil.retornaDescricaoMes((mFinal - 1) < 10 ? "0" + (mFinal - 1) + "" : (mFinal - 1) + ""));
                    parameters.put("MESLIBERADO_2MES", DataUtil.retornaDescricaoMes((mFinal) < 10 ? "0" + (mFinal) + "" : (mFinal) + ""));
                    parameters.put("MESLIBERADO_3MES", DataUtil.retornaDescricaoMes((mFinal + 1) < 10 ? "0" + (mFinal + 1) + "" : (mFinal + 1) + ""));
                    parameters.put("MESLIBERADO_4MES", DataUtil.retornaDescricaoMes((mFinal + 2) < 10 ? "0" + (mFinal + 2) + "" : (mFinal + 2) + ""));
                    arquivoJasper += "2Mes";
                } else if (mes.getNumeroMes() == 3) {
                    parameters.put("DATAGERACAO_1MES", (mes.getNumeroMes() - 2) < 10 ? "0" + (mes.getNumeroMes() - 2) + "/" + exercicio.getAno() : (mes.getNumeroMes() - 2) + "/" + exercicio.getAno());
                    parameters.put("DATAGERACAO_2MES", (mes.getNumeroMes() - 1) < 10 ? "0" + (mes.getNumeroMes() - 1) + "/" + exercicio.getAno() : (mes.getNumeroMes() - 1) + "/" + exercicio.getAno());
                    parameters.put("DATAGERACAO_3MES", mes.getNumeroMesString() + "/" + exercicio.getAno());
                    parameters.put("DATAGERACAO_4MES", null);

                    parameters.put("MESLIBERADO_1MES", DataUtil.retornaDescricaoMes((mFinal - 2) < 10 ? "0" + (mFinal - 2) + "" : (mFinal - 2) + ""));
                    parameters.put("MESLIBERADO_2MES", DataUtil.retornaDescricaoMes((mFinal - 1) < 10 ? "0" + (mFinal - 1) + "" : (mFinal - 1) + ""));
                    parameters.put("MESLIBERADO_3MES", DataUtil.retornaDescricaoMes((mFinal) < 10 ? "0" + (mFinal) + "" : (mFinal) + ""));
                    parameters.put("MESLIBERADO_4MES", DataUtil.retornaDescricaoMes((mFinal + 1) < 10 ? "0" + (mFinal + 1) + "" : (mFinal + 1) + ""));
                    arquivoJasper += "3Mes";
                } else if (mes.getNumeroMes() >= 4) {
                    parameters.put("DATAGERACAO_1MES", (mes.getNumeroMes() - 3) < 10 ? "0" + (mes.getNumeroMes() - 3) + "/" + exercicio.getAno() : (mes.getNumeroMes() - 3) + "/" + exercicio.getAno());
                    parameters.put("DATAGERACAO_2MES", (mes.getNumeroMes() - 2) < 10 ? "0" + (mes.getNumeroMes() - 2) + "/" + exercicio.getAno() : (mes.getNumeroMes() - 2) + "/" + exercicio.getAno());
                    parameters.put("DATAGERACAO_3MES", (mes.getNumeroMes() - 1) < 10 ? "0" + (mes.getNumeroMes() - 1) + "/" + exercicio.getAno() : (mes.getNumeroMes() - 1) + "/" + exercicio.getAno());
                    parameters.put("DATAGERACAO_4MES", mes.getNumeroMesString() + "/" + exercicio.getAno());

                    parameters.put("MESLIBERADO_1MES", DataUtil.retornaDescricaoMes((mFinal - 3) < 10 ? "0" + (mFinal - 3) + "" : (mFinal - 3) + ""));
                    parameters.put("MESLIBERADO_2MES", DataUtil.retornaDescricaoMes((mFinal - 2) < 10 ? "0" + (mFinal - 2) + "" : (mFinal - 2) + ""));
                    parameters.put("MESLIBERADO_3MES", DataUtil.retornaDescricaoMes((mFinal - 1) < 10 ? "0" + (mFinal - 1) + "" : (mFinal - 1) + ""));
                    parameters.put("MESLIBERADO_4MES", DataUtil.retornaDescricaoMes((mFinal) < 10 ? "0" + (mFinal) + "" : (mFinal) + ""));
                }
                arquivoJasper += "Portal.jasper";
                ds = new JRBeanCollectionDataSource(relatorioTransparenciaFacade.recuperaRelatorioAcompanhamento(parameters, unidadeGestora != null, mes.getNumeroMes()));
            } else {
                parameters.put("DATAINICIAL", getDataInicial(mes, exercicio));
                parameters.put("DATAFINAL", getDataFinal(mes, exercicio));
                ds = new JRBeanCollectionDataSource(relatorioTransparenciaFacade.recuperaRelatorioReceitas(parameters, unidadeGestora != null));
            }
            return gerarBytesEmPdfDeRelatorioComDadosEmCollectionView(getCaminho(), arquivoJasper, parameters, ds);
        }

        return null;
    }

    private void parametrosComuns(Mes mes, Exercicio exercicio, HashMap parameters, UnidadeGestora unidadeGestora) {
        parameters.put("FILTRO_RELATORIO", mes.getDescricao().toUpperCase());
        parameters.put("SQL", "");
        parameters.put("ANO_EXERCICIO", exercicio.getAno().toString());
        parameters.put("EXERCICIO_ID", exercicio.getId());
        parameters.put("MUNICIPIO", "Município de Rio Branco - AC");
        parameters.put("IMAGEM", getCaminhoImagem());
        if (unidadeGestora != null) {
            parameters.put("UNIDADEGESTORA", unidadeGestora.getId());
        }
    }


    public ByteArrayOutputStream gerarRelatorio(HashMap parameters, String arquivoJasper) {
        try {

            return geraRelatorio(report + arquivoJasper, parameters);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
        return null;
    }

    private ByteArrayOutputStream geraRelatorio(String arquivoJasper, HashMap parameters) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Connection con = AbstractReport.getAbstractReport().recuperaConexaoJDBC();
        try {
            parameters.put(JRParameter.REPORT_LOCALE, new Locale("pt", "BR"));
            JasperPrint jasperPrint = JasperFillManager.fillReport(arquivoJasper, parameters, con);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, bytes);
            exporter.exportReport();
        } catch (Exception ex) {
            logger.error("erro ", ex);
        } finally {
            AbstractReport.fecharConnection(con);
        }
        return bytes;
    }

    public String getDataInicial(Mes mes, Exercicio exercicio) {
        return "01/" + mes.getNumeroMesString() + "/" + exercicio.getAno();
    }

    public String getDataFinal(Mes mes, Exercicio exercicio) {
        return Util.getDiasMes(mes.getNumeroMes(), exercicio.getAno()) + "/" + mes.getNumeroMesString() + "/" + exercicio.getAno();
    }
}
