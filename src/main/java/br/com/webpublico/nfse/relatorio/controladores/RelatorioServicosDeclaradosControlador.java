package br.com.webpublico.nfse.relatorio.controladores;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.enums.TipoRelatorioApresentacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.domain.dtos.FiltroRelatorioServicosDeclaradosDTO;
import br.com.webpublico.nfse.enums.Exigibilidade;
import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.nfse.facades.ServicoDeclaradoFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-servicos-declarados",
        pattern = "/nfse/relatorio/servicosdeclarados/",
        viewId = "/faces/tributario/nfse/relatorio/servicos-declarados.xhtml")
})
public class RelatorioServicosDeclaradosControlador implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(RelatorioServicosDeclaradosControlador.class);

    @EJB
    private ServicoDeclaradoFacade servicoDeclaradoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private FiltroRelatorioServicosDeclaradosDTO filtro;
    private SituacaoNota situacaoNota;
    private Future<JasperPrint> futureJasperPrint;
    private Future<XSSFWorkbook> futureExcel;
    private JasperPrint jasperPrint;
    private StreamedContent streamedContent;
    private boolean excel = false;
    private AbstractReport report;

    @URLAction(mappingId = "novo-relatorio-servicos-declarados", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroRelatorioServicosDeclaradosDTO();
        filtro.setDataInicial(DataUtil.getPrimeiroDiaAno(new Date()));
        filtro.setDataFinal(new Date());
        filtro.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        situacaoNota = null;

    }

    public StreamedContent getStreamedContent() {
        return streamedContent;
    }

    public void gerarExcel() {
        try {
            this.excel = true;
            filtro.validarCamposPorPeriodo();
            futureExcel = servicoDeclaradoFacade.consultarServicos(filtro);
            FacesUtil.executaJavaScript("iniciarRelatorio()");
        } catch (ValidacaoException onpe) {
            FacesUtil.printAllFacesMessages(onpe);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorio() {
        try {
            this.excel = false;
            report = AbstractReport.getAbstractReport();
            filtro.validarCamposPorPeriodo();
            HashMap parameters = new HashMap();
            parameters.put("SUBREPORT_DIR", report.getCaminhoSubReport());
            parameters.put("BRASAO", report.getCaminhoImagem());
            parameters.put("MUNICIPIO", "Municipio de Rio Branco");
            parameters.put("TITULO", "Secretaria Municipal de Finanças");
            parameters.put("USUARIO", SistemaFacade.obtemLogin());
            parameters.put("FILTROS", filtro.montarDescricaoFiltros());
            parameters.put("TIPOAPRESENTACAO", filtro.getTipoRelatorioApresentacao().name());
            parameters.put("SOMENTE_TOTALIZADOR", filtro.getSomenteTotalizador());
            futureJasperPrint = servicoDeclaradoFacade.gerarRelatorioServicosDeclarados(parameters,
                filtro, report.getCaminho());
            FacesUtil.executaJavaScript("iniciarRelatorio()");
        } catch (ValidacaoException onpe) {
            FacesUtil.printAllFacesMessages(onpe);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public FiltroRelatorioServicosDeclaradosDTO getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroRelatorioServicosDeclaradosDTO filtro) {
        this.filtro = filtro;
    }

    public SituacaoNota getSituacaoNota() {
        return situacaoNota;
    }

    public void setSituacaoNota(SituacaoNota situacaoNota) {
        this.situacaoNota = situacaoNota;
    }

    public List<SelectItem> getExigibilidades() {
        return Util.getListSelectItem(Exigibilidade.values());
    }

    public List<SelectItem> getTiposApresentacao() {
        return Util.getListSelectItemSemCampoVazio(TipoRelatorioApresentacao.values());
    }

    public List<SelectItem> getTiposAgrupamento() {
        return Util.getListSelectItemSemCampoVazio(FiltroRelatorioServicosDeclaradosDTO.TipoAgrupamento.values());
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        if (!filtro.hasSituacao(SituacaoNota.EMITIDA)) {
            retorno.add(new SelectItem(SituacaoNota.EMITIDA.name(), SituacaoNota.EMITIDA.getDescricao()));
        }
        if (!filtro.hasSituacao(SituacaoNota.PAGA)) {
            retorno.add(new SelectItem(SituacaoNota.PAGA.name(), SituacaoNota.PAGA.getDescricao()));
        }
        if (!filtro.hasSituacao(SituacaoNota.CANCELADA)) {
            retorno.add(new SelectItem(SituacaoNota.CANCELADA.name(), SituacaoNota.CANCELADA.getDescricao()));
        }
        return retorno;
    }

    public void adicionarSituacao() {
        if (filtro.getSituacoes() == null) {
            filtro.setSituacoes(new ArrayList());
        }
        filtro.getSituacoes().add(situacaoNota);
        situacaoNota = null;
    }

    public void removerSituacao(SituacaoNota situacaoNota) {
        filtro.getSituacoes().remove(situacaoNota);
    }

    public JasperPrint getJasperPrint() {
        return jasperPrint;
    }

    public void consultarAndamentoRelatorio() throws ExecutionException, InterruptedException {
        if (excel && (futureExcel == null || !futureExcel.isDone())) {
            return;
        }
        if (!excel && (futureJasperPrint == null || !futureJasperPrint.isDone())) {
            return;
        }
        if (excel) {
            criarPlanilha();
            futureExcel = null;
        } else {
            jasperPrint = futureJasperPrint.get();
            futureJasperPrint = null;
        }
        FacesUtil.executaJavaScript("finalizarRelatorio()");
    }

    private void criarPlanilha() {
        try {
            XSSFWorkbook xssfSheets = futureExcel.get();
            File file = File.createTempFile("RelatorioServicosDeclarados", ".xls");
            FileOutputStream fout = new FileOutputStream(file);
            xssfSheets.write(fout);
            InputStream stream = Files.newInputStream(file.toPath());
            streamedContent = new DefaultStreamedContent(stream, "application/xls", "RelatorioServicosDEclarados.xls");
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public boolean isExcel() {
        return excel;
    }


    public void baixar() {
        try {
            report.setGeraNoDialog(true);
            report.gerarRelatorio(jasperPrint);
            jasperPrint = null;
        } catch (Exception e) {
            logger.error("Erro ao gerar relatorio em excel", e);
        }
    }

}
