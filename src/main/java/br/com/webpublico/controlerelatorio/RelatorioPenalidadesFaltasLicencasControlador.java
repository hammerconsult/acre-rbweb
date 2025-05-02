package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 27/02/14
 * Time: 17:33
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "gerarRelatorioPenalidadesFaltasLicencas", pattern = "/relatorio/penalidades-faltas-licencas/", viewId = "/faces/rh/relatorios/relatoriopenalidadesfaltaslicencas.xhtml")
})
public class RelatorioPenalidadesFaltasLicencasControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ContratoFP contratoFP;
    private Date dataInicial;
    private Date dataFinal;
    private String filtros;
    private static final Logger logger = LoggerFactory.getLogger(RelatorioPenalidadesFaltasLicencasControlador.class);

    public RelatorioPenalidadesFaltasLicencasControlador() {

    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return contratoFPFacade.recuperaContratoMatricula(parte.trim());
    }

    @URLAction(mappingId = "gerarRelatorioPenalidadesFaltasLicencas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        dataInicial = null;
        dataFinal = null;
        contratoFP = null;
        filtros = "";
    }

    private String gerarSql() {
        StringBuilder stb = new StringBuilder();
        filtros = "";
        if (contratoFP != null) {
            stb.append(" where vinc.id = ").append(contratoFP.getId());
            filtros += " Servidor: " + contratoFP.getMatriculaFP().getPessoa().getNome();
        }
        return stb.toString();
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.setApi("rh/relatorio-penalidade-faltas-licenca-e-hora-extra/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar relatório. ", e);
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        String sql = gerarSql();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("RELATÓRIO DE PENALIDADES FALTAS LICENÇAS FÉRIAS E HORA EXTRA");
        dto.adicionarParametro("SQL", Strings.isNullOrEmpty(sql) ? "" : sql);
        dto.adicionarParametro("FILTROS", filtros);
        dto.adicionarParametro("DATAINICIAL", DataUtil.getDataFormatada(dataInicial));
        dto.adicionarParametro("DATAFINAL", DataUtil.getDataFormatada(dataFinal));
        dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
        dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getLogin(), false);
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
        dto.adicionarParametro("NOMERELATORIO", "DEPARTAMENTO DE RECURSOS HUMANOS");
        return dto;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        if (dataInicial != null && dataFinal != null && dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Final deve ser maior que a Data Inicial.");
        }
        if (contratoFP == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Servidor deve ser informado.");
        } else {
            if (contratoFP.getFinalVigencia() != null && dataInicial != null && contratoFP.getFinalVigencia().before(dataInicial)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Servidor informado não possui contrato vigente entre os períodos informados.");
            }
        }

        ve.lancarException();
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }
}
