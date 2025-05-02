/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * @author peixe
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(
        id = "relatorioImpressaoQRCodeServidor",
        pattern = "/relatorio/impressao-qrcode-servidor/",
        viewId = "/faces/rh/relatorios/relatorioimpressaoqrcodeservidor.xhtml")
})
public class RelatorioImpressaoQRCodeServidorControlador {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioImpressaoQRCodeServidorControlador.class);

    @EJB
    private ContratoFPFacade contratoFPFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ContratoFP contratoFP;
    private Cargo cargo;

    public RelatorioImpressaoQRCodeServidorControlador() {
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    @URLAction(mappingId = "relatorioImpressaoQRCodeServidor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.contratoFP = null;
        this.hierarquiaOrganizacional = null;
        this.cargo = null;
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeRelatorio("Impressão de QRCode dos Servidores");
        dto.adicionarParametro("USUARIO", contratoFPFacade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
        dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE IMPRESSÃO DE QRCODE DOS SERVIDORES");
        dto.adicionarParametro("LINK_QRCODE", contratoFPFacade.gerarLinkQRCode());
        dto.adicionarParametro("COMPLEMENTO_WHERE", montarComplementoWhere());
        dto.adicionarParametro("FILTROS", montarFiltros());
        dto.setApi("rh/impressao-qrcode-servidor/");
        return dto;
    }

    private String montarComplementoWhere() {
        StringBuilder complementoWhere = new StringBuilder();
        if (contratoFP != null) {
            complementoWhere.append(" and c.id = ").append(contratoFP.getId());
        }
        if (hierarquiaOrganizacional != null) {
            complementoWhere.append(" and v.unidadeorganizacional_id = ").append(hierarquiaOrganizacional.getSubordinada().getId());
        }
        if (cargo != null) {
            complementoWhere.append(" and c.cargo_id = ").append(cargo.getId());
        }
        return complementoWhere.toString();
    }

    private String montarFiltros() {
        String juncao = "";
        StringBuilder filtros = new StringBuilder();
        if (contratoFP != null) {
            filtros.append(juncao).append(" Servidor: ").append(contratoFP);
            juncao = ",";
        }
        if (hierarquiaOrganizacional != null) {
            filtros.append(juncao).append(" Orgão: ").append(hierarquiaOrganizacional);
            juncao = ",";
        }
        if (cargo != null) {
            filtros.append(juncao).append(" Cargo: ").append(cargo);
        }
        if (filtros.toString().isEmpty()) {
            filtros.append(" Nenhum.");
        }
        return filtros.toString();
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = montarRelatorioDTO();
            ReportService.getInstance().gerarRelatorio(contratoFPFacade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }
}
