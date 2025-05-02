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
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author peixe
 */
@ManagedBean(name = "relatorioAvisoFeriasControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioAvisoFerias", pattern = "/relatorio/avisoferias/", viewId = "/faces/rh/relatorios/relatorioavisoferias.xhtml")
})
public class RelatorioAvisoFeriasControlador extends AbstractReport implements Serializable {

    private ConverterAutoComplete converterContratoFP;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ContratoFP contratoFP;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Date dataInicial;
    private Date dataFinal;
    private List<HierarquiaOrganizacional> hierarquias;

    public RelatorioAvisoFeriasControlador() {
        geraNoDialog = Boolean.TRUE;
        hierarquias = Lists.newArrayList();
    }

    @URLAction(mappingId = "novoRelatorioAvisoFerias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        contratoFP = null;
        hierarquiaOrganizacional = null;
        dataInicial = null;
        dataFinal = null;
        hierarquias = Lists.newArrayList();
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
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

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public List<HierarquiaOrganizacional> getHierarquias() {
        return hierarquias;
    }

    public void setHierarquias(List<HierarquiaOrganizacional> hierarquias) {
        this.hierarquias = hierarquias;
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return contratoFPFacade.recuperaContrato(parte.trim());
    }

    public void setHierarquiaOrganizacionalSelecionada(SelectEvent evt) {
        hierarquiaOrganizacional = (HierarquiaOrganizacional) evt.getObject();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("BRASAO");
            dto.setNomeRelatorio("Relatório de aviso de férias");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
            dto.adicionarParametro("NOMERELATORIO", "AVISO DE FÉRIAS");
            dto.adicionarParametro("CONDICAO", montarCondicoes());
            dto.adicionarParametro("DATA_OPERACAO", UtilRH.getDataOperacao());
            dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getNome(), false);
            dto.setApi("rh/relatorio-aviso-ferias/");
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar relatório: " + e);
        }
    }

    private String montarCondicoes() {
        String where = " ";
        if (contratoFP != null) {
            where += " and vinculo.id = " + contratoFP.getId();
        }
        if (hierarquias != null && !hierarquias.isEmpty() && contratoFP == null) {
            where += " and (";
            for (HierarquiaOrganizacional hierarquia : hierarquias) {
                where += " ho.codigo like '" + hierarquia.getCodigoSemZerosFinais() + "%'";
                where += " or";
            }
            where = where.substring(0, where.length() - 2);
            where += ")";
        }
        if (dataInicial != null) {
            where += " and cfl.datainicial >= to_date('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy')";
        }
        if (dataFinal != null) {
            where += " and cfl.datainicial <= to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy')";
        }
        return where;
    }

    public void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (contratoFP == null && hierarquias.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o servidor ou o Órgão.");
        }
        if ((dataInicial != null && dataFinal != null) && dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial deve pode ser maior que a Data Final.");
        }
        ve.lancarException();
    }

    private void validarHierarquia() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione um orgão.");
        } else if (hierarquias.contains(hierarquiaOrganizacional)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Órgão já foi adicionado.");
        }
        ve.lancarException();
    }

    public void removerHierarquia(HierarquiaOrganizacional hierarquiaOrganizacional) {
        if (getHierarquias().contains(hierarquiaOrganizacional)) {
            hierarquias.remove(hierarquiaOrganizacional);
        }
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

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaOrganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
