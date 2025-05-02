package br.com.webpublico.controle;


import br.com.webpublico.entidadesauxiliares.VoExportacaoCertidaoDividaAtiva;
import br.com.webpublico.negocios.CertidaoDividaAtivaFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.AsyncExecutor;
import br.com.webpublico.util.ExcelUtil;
import br.com.webpublico.util.FacesUtil;
import com.beust.jcommander.internal.Lists;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ManagedBean(name = "exportacaoCertidaoDividaAtivaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-exportacao-cda",
        pattern = "/tributario/exportacao-certidao-divida-ativa/",
        viewId = "/faces/tributario/dividaativa/certidaodividaativa/exportacaocda.xhtml"),

})
public class ExportacaoCertidaoDividaAtivaControlador {

    private static final Logger log = LoggerFactory.getLogger(ExportacaoCertidaoDividaAtivaControlador.class);
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private StreamedContent streamedContent;
    private List<String> titulos;
    private CompletableFuture<List<VoExportacaoCertidaoDividaAtiva>> futureDados;
    private ExcelUtil excelUtil;

    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;

    public ExportacaoCertidaoDividaAtivaControlador() {
    }

    @URLAction(mappingId = "nova-exportacao-cda", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        excelUtil = new ExcelUtil();
        titulos = Lists.newArrayList("Exercício", "Número", "CPF/CNPJ", "Nome/Razão Social", "Inscrição Cadastral",
            "Situação", "Valor Atualizado", "Quantidade de Parcelamentos");
        assistenteBarraProgresso = new AssistenteBarraProgresso();
        initAssistente();
    }

    private void initAssistente() {
        assistenteBarraProgresso.setDescricaoProcesso("...");
        assistenteBarraProgresso.zerarContadoresProcesso();
        streamedContent = null;
    }

    public StreamedContent getStreamedContent() {
        return streamedContent;
    }

    public void setStreamedContent(StreamedContent streamedContent) {
        this.streamedContent = streamedContent;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public void iniciarExportacao() {
        initAssistente();
        assistenteBarraProgresso.setExecutando(true);
        assistenteBarraProgresso.setUsuarioSistema(certidaoDividaAtivaFacade.getSistemaFacade().getUsuarioCorrente());
        futureDados = AsyncExecutor.getInstance().execute(assistenteBarraProgresso, () -> {
            try {
                return certidaoDividaAtivaFacade.buscarDadosExportacaoCertidaoDividaAtiva(assistenteBarraProgresso);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        FacesUtil.executaJavaScript("poolConsulta.start()");
    }

    public void acompanharExportacao() {
        if (futureDados.isDone()) {
            FacesUtil.executaJavaScript("poolConsulta.stop()");
            FacesUtil.executaJavaScript("rcFinalizarExportacao()");
        }
    }

    public void finalizarExportacao() {
        try {
            List<VoExportacaoCertidaoDividaAtiva> dados = futureDados.get();
            List<Object[]> linhas = Lists.newArrayList();
            for (VoExportacaoCertidaoDividaAtiva vo : dados) {
                Object[] linha = new Object[8];
                linha[0] = vo.getExercicio();
                linha[1] = vo.getNumero();
                linha[2] = vo.getCpfCnpj();
                linha[3] = vo.getNomeRazaoSocial();
                linha[4] = vo.getInscricaoCadastral();
                linha[5] = vo.getSituacao();
                linha[6] = vo.getValorAtualizado();
                linha[7] = vo.getQuantidadeParcelamentos();
                linhas.add(linha);
            }
            excelUtil.gerarExcel("Certidões de Dívida Ativa", "CertidaoDividaAtiva",
                titulos, linhas, "");
            streamedContent = excelUtil.fileDownload();
            assistenteBarraProgresso.setExecutando(false);
        } catch (Exception e) {
            log.error("Erro ao exportar as certidões de dívida ativa: {}", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao exportar as certidões de dívida ativa: " + e.getMessage());
        }
    }
}
