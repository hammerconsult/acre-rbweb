/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author andre
 */
@ManagedBean(name = "relatorioProgramacaoFeriasControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioProgramacaoFerias", pattern = "/relatorio/programacao-ferias/", viewId = "/faces/rh/relatorios/relatorioprogramacaoferias.xhtml")
})
public class RelatorioProgramacaoFeriasControlador implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(RelatorioProgramacaoFeriasControlador.class);

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private List<HierarquiaOrganizacional> hierarquias;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterContratoFP;
    private ContratoFP contratoFP;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private boolean relatorioComQuebra;
    private Date data;

    public RelatorioProgramacaoFeriasControlador() {
        relatorioComQuebra = false;
        hierarquias = Lists.newArrayList();
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> getHierarquias() {
        return hierarquias;
    }

    public void setHierarquias(List<HierarquiaOrganizacional> hierarquias) {
        this.hierarquias = hierarquias;
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public boolean isRelatorioComQuebra() {
        return relatorioComQuebra;
    }

    public void setRelatorioComQuebra(boolean relatorioComQuebra) {
        this.relatorioComQuebra = relatorioComQuebra;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    @URLAction(mappingId = "novoRelatorioProgramacaoFerias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparFiltros() {
        contratoFP = null;
        hierarquiaOrganizacional = null;
        relatorioComQuebra = false;
        data = null;
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.setApi("rh/relatorio-programacao-ferias/");
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar relatório de Programação de Férias : {} ", e);
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("RELATÓRIO DE PROGRAMAÇÃO DE FÉRIAS");
        dto.adicionarParametro("WHERE", montarWhere());
        dto.adicionarParametro("EXERCICIO", sistemaControlador.getExercicioCorrente().getAno().toString());
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("DATAOPERACAO", UtilRH.getDataOperacao());
        dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getLogin(), false);
        dto.adicionarParametro("IS_RELATORIO_QUEBRA",relatorioComQuebra);
        return dto;
    }

    private String montarWhere() {
        String where = "";

        if (contratoFP != null) {
            where += " and cfp.id = " + contratoFP.getId();
        }

        if (hierarquias != null && !hierarquias.isEmpty()) {
            where += " and (";
            for (HierarquiaOrganizacional hierarquia : hierarquias) {
                where += " ho.codigo like '" + hierarquia.getCodigoSemZerosFinais() + "%'";
                where += " or";
            }

            where = where.substring(0, where.length() - 2);
            where += ")";
        }
        if (data != null) {
            where += " and trunc(sf.datainicio) >= to_date('" + new SimpleDateFormat("dd/MM/yyyy").format(data) + "', 'dd/MM/yyyy')";
        }
        return where;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquias == null || hierarquias.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos um órgão.");
        }
        ve.lancarException();
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaOrganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return contratoFPFacade.recuperaContratoMatricula(parte.trim());
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    private void validarHierarquia() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquias.contains(hierarquiaOrganizacional)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Órgão já foi adicionado;");
        }
        ve.lancarException();
    }

    public void adicionarHierarquia() {
        try {
            validarHierarquia();
            hierarquias.add(hierarquiaOrganizacional);
            hierarquiaOrganizacional = new HierarquiaOrganizacional();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerHierarquia(HierarquiaOrganizacional hierarquiaOrganizacional) {
        if (getHierarquias().contains(hierarquiaOrganizacional)) {
            hierarquias.remove(hierarquiaOrganizacional);
        }
    }
}
