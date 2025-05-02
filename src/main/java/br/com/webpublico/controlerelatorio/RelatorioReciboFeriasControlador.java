package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.MatriculaFP;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.MatriculaFPFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 01/02/2017.
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-recibo-ferias", pattern = "/relatorio/recibo-ferias/", viewId = "/faces/rh/relatorios/relatorioreciboferias.xhtml")
})
public class RelatorioReciboFeriasControlador implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private MatriculaFPFacade matriculaFPFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    private MatriculaFP matriculaFP;
    private Integer mes;
    private Integer ano;
    private static final Logger logger = LoggerFactory.getLogger(RelatorioReciboFeriasControlador.class);


    public RelatorioReciboFeriasControlador() {
    }

    @URLAction(mappingId = "relatorio-recibo-ferias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        matriculaFP = null;
        mes = null;
        ano = null;
    }

    public List<MatriculaFP> completarMatriculaFP(String parte) {
        return matriculaFPFacade.recuperaMatriculaFiltroPessoa(parte.trim());
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.setApi("rh/relatorio-recibo-ferias/");
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar Relatório. ", e);
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("RECIBO DE FÉRIAS");
        dto.adicionarParametro("MES_ANO", getMesAno());
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
        dto.adicionarParametro("TEXTO_RECIBO_FERIAS", configuracaoRHFacade.buscarConfiguracaoRelatorio().getTextoReciboFerias());
        dto.adicionarParametro("MES", (mes - 1));
        dto.adicionarParametro("ANO", ano);
        dto.adicionarParametro("MATRICULA_ID", matriculaFP.getId());
        dto.adicionarParametro("DATA_OPERACAO", sistemaControlador.getDataOperacao().getTime());
        return dto;
    }


    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês é obrigatório.");
        } else if (mes < 1 || mes > 12) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Favor informar um Mês entre 01 (Janeiro) e 12 (Dezembro).");
        }
        if (ano == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano é obrigatório.");
        }
        if (matriculaFP == null || matriculaFP.getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Matrícula é obrigatório.");
        }

        ve.lancarException();
    }

    private String getMesAno() {
        return (Mes.getMesToInt(mes).getDescricao() + "/" + ano);
    }

    public MatriculaFP getMatriculaFP() {
        return matriculaFP;
    }

    public void setMatriculaFP(MatriculaFP matriculaFP) {
        this.matriculaFP = matriculaFP;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
