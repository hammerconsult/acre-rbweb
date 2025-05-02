/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.EnquadramentoFuncional;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.TipoProvimento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-mudanca-de-letra", pattern = "/relatorio/mudanca-letra-progressao-atraso-progressao-iminente/", viewId = "/faces/rh/relatorios/relatoriomudancaletraprogressao.xhtml")
})
public class RelatorioMudancaLetraProgressaoControlador extends AbstractReport implements Serializable {

    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private Date dataInicial;
    private Date dataFinal;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private EnquadramentoFuncional enquadramentoFuncional;
    private ContratoFP contrato;
    private List<HierarquiaOrganizacional> grupoHierarquia;
    private HierarquiaOrganizacional[] hoSelecionadas;

    public RelatorioMudancaLetraProgressaoControlador() {
    }

    public List<HierarquiaOrganizacional> getGrupoHierarquia() {
        return grupoHierarquia;
    }

    public void setGrupoHierarquia(List<HierarquiaOrganizacional> grupoHierarquia) {
        this.grupoHierarquia = grupoHierarquia;
    }

    public HierarquiaOrganizacional[] getHoSelecionadas() {
        return hoSelecionadas;
    }

    public void setHoSelecionadas(HierarquiaOrganizacional[] hoSelecionadas) {
        this.hoSelecionadas = hoSelecionadas;
    }

    public void gerarRelatorio() throws JRException, IOException {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDto();
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: " + ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorio(String tipoRelatorio) throws JRException, IOException {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDto();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: " + ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDto() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("MODULO", "RECURSOS HUMANOS");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
        dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
        dto.adicionarParametro("dataOperacao", UtilRH.getDataOperacaoFormatada());
        dto.adicionarParametro("condicao", filtro());
        dto.setNomeRelatorio(getNomeRelatorio());
        dto.setApi("rh/mudanca-letra-progressao/");
        return dto;
    }

    private String getNomeRelatorio() {
        return "RELATÓRIO DE MUDANÇA DE LETRA PROGRESSÃO EM ATRASO OU PROGRESSÃO IMINENTE";
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if ((dataInicial != null && dataFinal != null) && dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial deve ser menor que a Data Final");
        }
        if (hoSelecionadas.length == 0 && contrato == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório selecionar um Servidor ou Órgão(s).");
        }
        ve.lancarException();
    }

    @URLAction(mappingId = "relatorio-mudanca-de-letra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        recuperarListaHO();
        hierarquiaOrganizacionalSelecionada = null;
        dataInicial = null;
        dataFinal = null;
        contrato = null;
        hoSelecionadas = null;
    }

    public List<HierarquiaOrganizacional> completaHierarquia(String parte) {
        List<HierarquiaOrganizacional> hos = new ArrayList<>();
        hos.addAll(hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao()));
        return hos;
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        if (hierarquiaOrganizacionalSelecionada == null) {
            return contratoFPFacade.recuperaContratoMatricula(parte.trim());
        } else {
            return contratoFPFacade.recuperaContratoVigenteMatriculaPorLocalDeTrabalhoRecursiva(parte.trim(), hierarquiaOrganizacionalSelecionada, UtilRH.getDataOperacao());
        }
    }

    public ContratoFP getContrato() {
        return contrato;
    }

    public void setContrato(ContratoFP contrato) {
        this.contrato = contrato;
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

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public EnquadramentoFuncional getEnquadramentoFuncional() {
        return enquadramentoFuncional;
    }

    public void setEnquadramentoFuncional(EnquadramentoFuncional enquadramentoFuncional) {
        this.enquadramentoFuncional = enquadramentoFuncional;
    }

    private String getMontaGrupoHO() {
        String retorno = " AND ( ";
        for (HierarquiaOrganizacional ho : hoSelecionadas) {
            retorno += " ho.codigo like ";
            retorno += "'%" + ho.getCodigoSemZerosFinais() + "%'";
            retorno += " or ";
        }
        retorno = retorno.substring(0, retorno.length() - 3);

        retorno += ") ";
        return retorno;
    }

    public String filtro() {
        String filtro = "";
        if (hoSelecionadas.length > 0) {
            filtro += getMontaGrupoHO();
        }
        if (contrato != null) {
            filtro += " and contrato.id_vinculo = " + contrato.getId();
        }
        if (dataInicial != null && dataFinal != null) {
            filtro += " and eq.iniciovigencia between to_date('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy') and to_date('" + DataUtil.getDataFormatada(dataFinal) + "','dd/MM/yyyy')";
        }
        filtro += " and tipoprovimento.CODIGO = " + TipoProvimento.PROVIMENTO_PROGRESSAO +
            "       and eq.INICIOVIGENCIA = (select max(enquadramento.INICIOVIGENCIA) from ENQUADRAMENTOFUNCIONAL enquadramento " +
            "                  inner join PROVIMENTOFP prov on enquadramento.PROVIMENTOFP_ID = prov.id " +
            "                  inner join TIPOPROVIMENTO tp on prov.TIPOPROVIMENTO_ID = tp.id " +
            "                  where enquadramento.CONTRATOSERVIDOR_ID = contrato.id_vinculo " +
            "                  and enquadramento.PROVIMENTOFP_ID = prov.id and tp.id = tipoprovimento.id)";

        return filtro;
    }

    public void recuperarListaHO() {
        grupoHierarquia = hierarquiaOrganizacionalFacade.listaTodasPorNivel("", "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), getSistemaFacade().getDataOperacao());
    }
}
