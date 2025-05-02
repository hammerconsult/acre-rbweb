package br.com.webpublico.controle.rh.arquivos;

import br.com.webpublico.entidadesauxiliares.BeneficiarioProvaDeVidaDTO;
import br.com.webpublico.entidadesauxiliares.rh.FiltroProvaDeVida;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.VinculoFPFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-exportacao-dados-prova-vida", pattern = "/exportacao-dados-prova-vida/", viewId = "/faces/rh/arquivos/provadevida/edita.xhtml"),
})
public class ExportacaoDadosProvaDeVidaControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ExportacaoDadosProvaDeVidaControlador.class);

    @EJB
    private VinculoFPFacade vinculoFPFacade;
    private FiltroProvaDeVida filtroProvaDeVida;
    private StreamedContent fileDownload;


    public ExportacaoDadosProvaDeVidaControlador() {
        filtroProvaDeVida = new FiltroProvaDeVida();
    }

    @URLAction(mappingId = "novo-exportacao-dados-prova-vida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtroProvaDeVida = new FiltroProvaDeVida();
    }

    public void gerarProvaDeVida() {
        try {
            fileDownload = null;
            filtroProvaDeVida.validarGeracao();
            List<BeneficiarioProvaDeVidaDTO> beneficiarios = carregarDados();
            if (beneficiarios.isEmpty()) {
                FacesUtil.addOperacaoNaoRealizada("Não há beneficiários disponíveis com os filtros informados para gerar o arquivo.");
            } else {
                gerarCSV(beneficiarios);
            }
        } catch (ValidacaoException ve) {
            if (ve.temMensagens()) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            } else {
                FacesUtil.addOperacaoNaoRealizada(ve.getMessage());
            }
        } catch (Exception ex) {
            logger.error("Erro ao gerar arquivos de prova de vida! " + ex);
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar arquivos de prova de vida! " + ex.getMessage());
        }
    }

    private List<BeneficiarioProvaDeVidaDTO> carregarDados() {
        List<BeneficiarioProvaDeVidaDTO>  beneficiarios = vinculoFPFacade.buscarBeneficiariosProvaDeVida(filtroProvaDeVida.getTipoFolhaDePagamento(), filtroProvaDeVida.getTipoBeneficiario(), filtroProvaDeVida.getAno(), Integer.parseInt(filtroProvaDeVida.getMes()), filtroProvaDeVida.isApenasAniversariantes());
        return beneficiarios;
    }

    private void gerarCSV(List<BeneficiarioProvaDeVidaDTO> beneficiarios) {
        try {
            File csvFile = new File("MUNICIPIO_RIO_BRANCO_PROVA_DE_VIDA.CSV");
            FileWriter writer = new FileWriter(csvFile);
            writer.append("cpf_sem_formatacao;nome_completo;data_nascimento_dd/mm/aaaa\n");
            for (BeneficiarioProvaDeVidaDTO beneficiario : beneficiarios) {
                writer.append(beneficiario.getCpf()).append(";")
                    .append(beneficiario.getNome()).append(";")
                    .append(beneficiario.getDataNascimento()).append("\n");
            }
            writer.flush();
            writer.close();

            FileInputStream fileInputStream = new FileInputStream(csvFile);
            fileDownload = new DefaultStreamedContent(fileInputStream, "text/csv", "MUNICIPIO_RIO_BRANCO_PROVA_DE_VIDA.CSV");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível gerar o arquivo CSV de Prova de Vida, por favor, comunique o suporte técnico.");
        }
    }

    public StreamedContent getFileDownload() {
        return fileDownload;
    }

    public void setFileDownload(StreamedContent fileDownload) {
        this.fileDownload = fileDownload;
    }

    public FiltroProvaDeVida getFiltroProvaDeVida() {
        return filtroProvaDeVida;
    }

    public void setFiltroProvaDeVida(FiltroProvaDeVida filtroProvaDeVida) {
        this.filtroProvaDeVida = filtroProvaDeVida;
    }

    public List<SelectItem> getTipoFolhaDePagamentos() {
        return Util.getListSelectItem(TipoFolhaDePagamento.values(), false);
    }

}
