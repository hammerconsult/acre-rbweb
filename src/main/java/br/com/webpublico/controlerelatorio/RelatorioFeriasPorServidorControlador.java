/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.relatoriofacade.rh.RelatorioFeriasPorServidorFacade;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author boy
 */
@ManagedBean(name = "relatorioFeriasPorServidorControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioFeriasPorServidor", pattern = "/relatorio/ferias-por-servidor/", viewId = "/faces/rh/relatorios/relatoriodeferiasporservidor.xhtml")
})
public class RelatorioFeriasPorServidorControlador implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(RelatorioFeriasPorServidorControlador.class);

    @EJB
    private RelatorioFeriasPorServidorFacade feriasPorServidorFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Integer mes;
    private Integer ano;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private VinculoFP vinculoFP;
    private TipoPeriodoAquisitivo tipoPeriodoAquisitivo;
    public static final String ARQUIVO_JASPER = "RelatorioDeFeriasPorServidor.jasper";
    public static final String BRASAO_DE_RIO_BRANCO = "Brasao_de_Rio_Branco.gif";

    public RelatorioFeriasPorServidorControlador() {

    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacional(String parte) {
        return feriasPorServidorFacade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaOrganizacionalTipo(parte.trim(), "ADMINISTRATIVA", new Date());
    }

    public List<VinculoFP> completarContrato(String s) {
        return feriasPorServidorFacade.getContratoFPFacade().buscaContratoFiltrandoAtributosVinculoMatriculaFP(s.trim());
    }

    public List<TipoPeriodoAquisitivo> getTiposPeriodoAquisitivo() {
        List<TipoPeriodoAquisitivo> toReturn = new ArrayList<>();
        for (TipoPeriodoAquisitivo tipo : TipoPeriodoAquisitivo.values()) {
            toReturn.add(tipo);
        }
        return toReturn;
    }

    @URLAction(mappingId = "novoRelatorioFeriasPorServidor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        mes = null;
        ano = null;
        hierarquiaOrganizacional = null;
        vinculoFP = null;
        tipoPeriodoAquisitivo = TipoPeriodoAquisitivo.FERIAS;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês é obrigatório.");
        }
        if (ano == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano é obrigatório.");
        }
        if (hierarquiaOrganizacional == null && vinculoFP == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar um Órgão ou um Servidor para gerar o relatório.");
        }
        ve.lancarException();
        if (mes < 1 || mes > 12) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar um Mês entre 01 (Janeiro) e 12 (Dezembro).");
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.setApi("rh/relatorio-ferias-por-servidor/");
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar relatório de Férias por Servidor: {} ", e);
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("RELATÓRIO DE FÉRIAS POR SERVIDOR  ");
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getLogin(), false);
        dto.adicionarParametro("NOME_RELATORIO", "RELATÓRIO DE " + tipoPeriodoAquisitivo.getDescricao().toUpperCase() + " POR SERVIDOR");
        dto.adicionarParametro("TIPO_PERIODO_AQUISITIVO", tipoPeriodoAquisitivo.getToDto());
        dto.adicionarParametro("ANO", getAno());
        dto.adicionarParametro("MES", getMes());
        dto.adicionarParametro("SERVIDOR_ID", getIdServidor());
        dto.adicionarParametro("CODIGO_HIERARQUIA", buscarCodigoHierarquia());
        dto.adicionarParametro("IS_SERVIDOR", isServidor());
        return dto;
    }


    private String buscarCodigoHierarquia() {
        try {
            return hierarquiaOrganizacional.getCodigoSemZerosFinais();
        } catch (NullPointerException ex) {
            return null;
        }
    }

    private boolean isServidor() {
        return getVinculoFP() != null;
    }

    private Long getIdServidor() {
        return vinculoFP != null ? getVinculoFP().getId() : 0;
    }

    public TipoPeriodoAquisitivo getTipoPeriodoAquisitivo() {
        return tipoPeriodoAquisitivo;
    }

    public void setTipoPeriodoAquisitivo(TipoPeriodoAquisitivo tipoPeriodoAquisitivo) {
        this.tipoPeriodoAquisitivo = tipoPeriodoAquisitivo;
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

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

}
