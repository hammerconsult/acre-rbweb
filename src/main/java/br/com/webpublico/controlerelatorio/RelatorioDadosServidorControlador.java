package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.MatriculaFPFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.codec.binary.Base64;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.*;
import java.util.List;

/**
 * @author WebPublico07
 */
@ManagedBean(name = "relatorioDadosServidorControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioDadosServidor", pattern = "/relatorio/dados-pessoais-servidor/", viewId = "/faces/rh/relatorios/relatoriodadosservidor.xhtml")
})
public class RelatorioDadosServidorControlador extends AbstractReport implements Serializable {

    private MatriculaFP matriculaFP;
    @EJB
    private MatriculaFPFacade matriculaFPFacade;
    private ConverterAutoComplete converterMatriculaFP;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;

    public RelatorioDadosServidorControlador() {
        geraNoDialog = true;
    }

    @URLAction(mappingId = "novoRelatorioDadosServidor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        matriculaFP = null;
    }

    public MatriculaFP getMatriculaFP() {
        return matriculaFP;
    }

    public void setMatriculaFP(MatriculaFP matriculaFP) {
        this.matriculaFP = matriculaFP;
    }

    public void gerarRelatorio() throws JRException, IOException {
        try {
            validaCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao gerar o relatório de Dados Pessoais do Servidor: {}", e);
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public void gerarRelatorio(String tipoRelatorioDadosServidor) throws JRException, IOException {
        try {
            validaCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioDadosServidor));
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao gerar o relatório de Dados Pessoais do Servidor: {}", e);
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        String fotoServidor = converterEmBase64(matriculaFP.getPessoa(), false);
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("RELATÓRIO DE DADOS PESSOAIS DO SERVIDOR");
        dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
        dto.adicionarParametro("MATRICULA", matriculaFP.getMatricula());
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("USUARIO", usuarioLogado() + "");
        dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE DADOS PESSOAIS DO SERVIDOR");
        dto.adicionarParametro("FOTO_SERVIDOR", fotoServidor);
        dto.setApi("rh/relatorio-dados-servidor/");
        return dto;
    }

    public String converterEmBase64(PessoaFisica pessoaFisica, boolean isConcatenarComExtensaoEBase64) {
        try {
            Arquivo arq = pessoaFacade.recuperaFotoPessoaFisica(pessoaFisica);
            List<ArquivoParte> arquivoPartes = arquivoFacade.recuperaDependencias(arq.getId()).getPartes();
            if (arquivoPartes != null && !arquivoPartes.isEmpty()) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                for (ArquivoParte arquivoParte : arquivoPartes) {
                    buffer.write(arquivoParte.getDados());
                }
                Base64 codec = new Base64();
                if(isConcatenarComExtensaoEBase64) {
                    return "data:" + arq.getMimeType() + ";base64," + new String(codec.encode(buffer.toByteArray()));
                }
                return new String(codec.encode(buffer.toByteArray()));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "";
    }

    public void validaCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (matriculaFP == null || matriculaFP.getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Matrícula é obrigatório.");
        }
        ve.lancarException();
    }

    public List<MatriculaFP> completaMatriculaFP(String parte) {
        return matriculaFPFacade.recuperaMatriculaFiltroPessoa(parte.trim());
    }

    public Converter getConverterMatriculaFP() {
        if (converterMatriculaFP == null) {
            converterMatriculaFP = new ConverterAutoComplete(MatriculaFP.class, matriculaFPFacade);
        }
        return converterMatriculaFP;
    }
}
