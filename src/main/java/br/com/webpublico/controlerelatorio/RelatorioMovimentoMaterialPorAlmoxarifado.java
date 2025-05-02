/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LocalEstoque;
import br.com.webpublico.entidades.Material;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author renato
 */
@ViewScoped
@ManagedBean
@URLMapping(
    id = "relatorio-mov-material",
    pattern = "/relatorio/movimentacao-material-por-almoxarifado/",
    viewId = "/faces/administrativo/relatorios/relatoriomovimentodematerialporalmoxarifado.xhtml")
public class RelatorioMovimentoMaterialPorAlmoxarifado implements Serializable {

    @EJB
    private MaterialFacade materialFacade;
    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    private LocalEstoque localEstoque;
    private Date dataInicio;
    private Date dataFinal;
    private GrupoMaterial grupoMaterial;
    private Material material;
    private HierarquiaOrganizacional administrativa;
    private HierarquiaOrganizacional orcamentaria;
    private StringBuilder filtro;

    @URLAction(mappingId = "relatorio-mov-material", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        Util.limparCampos(this);
    }

    public void limparCampos() {
        administrativa = null;
        orcamentaria = null;
        localEstoque = null;
        dataInicio = null;
        dataFinal = null;
        grupoMaterial = null;
        material = null;
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaAdministrativa(String filter) {
        return getHierarquiaOrganizacionalFacade().listaTodasHierarquiaHorganizacionalTipo(filter, TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), sistemaFacade.getDataOperacao());
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaOrcamentaria(String filter) {
        return getHierarquiaOrganizacionalFacade().listaTodasHierarquiaHorganizacionalTipo(filter, TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaFacade.getDataOperacao());
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public List<LocalEstoque> buscarLocalDeEstoque(String parte) {
        if (getAdministrativa() != null || (getAdministrativa() != null && getOrcamentaria() != null)) {
            return localEstoqueFacade.buscarLocaisEstoquePorUnidade(parte, getAdministrativa().getSubordinada());
        }
        if (getOrcamentaria() != null) {
            return localEstoqueFacade.buscarLocaisEstoquePorUnidade(parte, getOrcamentaria().getSubordinada());
        }
        FacesUtil.addAtencao("Por favor informe uma unidade administrativa ou orçamentaria ");
        return Lists.newArrayList();
    }

    public List<GrupoMaterial> completaGrupoMaterial(String parte) {
        return grupoMaterialFacade.listaFiltrando(parte.trim());
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            RelatorioDTO dto = getParameters(abstractReport);
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MODULO", "MATERIAIS");
            dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeRelatorio("RELATÓRIO DE MOVIMENTAÇÃO DE MATERIAL POR ALMOXARIFADO");
            dto.setApi("administrativo/movimento-material-por-almoxarifado/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private RelatorioDTO getParameters(AbstractReport abstractReport) {
        RelatorioDTO dto = new RelatorioDTO();
        setFiltro(new StringBuilder("Critério(s) Utilizado(s): "));

        String consulta = " where ";
        dto.adicionarParametro("idLocalEstoque", localEstoque.getId());
        dto.adicionarParametro("descricaolocalestoque", localEstoque.getCodigo() + " - " + localEstoque.getDescricao());
        dto.setNomeParametroBrasao("BRASAO");

        String inicio = DataUtil.getDataFormatada(dataInicio);
        String fim = DataUtil.getDataFormatada(dataFinal);

        dto.adicionarParametro("periodo", "De: " + inicio + " Até: " + fim);

        Boolean selecionouMaterialOuGrupoMaterial = false;

        if (getAdministrativa() != null) {
            consulta += " le.UNIDADEORGANIZACIONAL_ID =  " + getAdministrativa().getSubordinada().getId();
            consulta += " and";
            dto.adicionarParametro("ADM", getAdministrativa().getCodigo() + " - " + getAdministrativa().getDescricao());
            adicionarFiltro(" Unidade Administrativa: " + getAdministrativa().getCodigo() + " - " + getAdministrativa().getSubordinada().getDescricao());
        }

        if (getOrcamentaria() != null) {
            consulta += " le.UNIDADEORGANIZACIONAL_ID = " + getOrcamentaria().getSubordinada().getId();
            dto.adicionarParametro("ORC", getOrcamentaria().getCodigo() + " - " + getOrcamentaria().getDescricao());
            adicionarFiltro(" Unidade Orçamentária: " + getOrcamentaria().getCodigo() + " - " + getOrcamentaria().getSubordinada().getDescricao());
        }

        adicionarFiltro(" Local de Estoque: " + localEstoque.toString() + " - ");
        adicionarFiltro("Período: " + "De: " + inicio + " Até: " + fim);

        if (material != null) {
            dto.adicionarParametro("condicao", "m.id = " + material.getId().toString() + " and (e.dataestoque BETWEEN '" + inicio + "' and '" + fim + "')");
            dto.adicionarParametro("material", material.getCodigo() + " - " + material.getDescricao());
            adicionarFiltro(" Material: " + material.getCodigo() + " - " + material.getDescricao());
            selecionouMaterialOuGrupoMaterial = true;
        }
        if (grupoMaterial != null) {
            dto.adicionarParametro("condicao", "gm.id = " + grupoMaterial.getId().toString() + " and (e.dataestoque BETWEEN '" + inicio + "' and '" + fim + "')");
            dto.adicionarParametro("grupomaterial", grupoMaterial.getCodigo() + " - " + grupoMaterial.getDescricao());
            adicionarFiltro(" Grupo Material: " + grupoMaterial.getCodigo() + " - " + grupoMaterial.getDescricao());
            selecionouMaterialOuGrupoMaterial = true;
        }

        if (!selecionouMaterialOuGrupoMaterial) {
            dto.adicionarParametro("condicao", " trunc(e.dataestoque) BETWEEN to_date('" + inicio + "', 'dd/MM/yyyy') and to_date('" + fim + "', 'dd/MM/yyyy')");
        }

        consulta = consulta.length() > 0 && consulta.endsWith(" and") ? consulta.substring(0, consulta.length() - 3) : consulta;
        dto.adicionarParametro("clausulaWhere", consulta);
        dto.adicionarParametro("FILTRO", getFiltrosFormatado());
        return dto;
    }

    private void adicionarFiltro(String value) {
        getFiltro().append(value);
        getFiltro().append(", ");
    }

    private String getFiltrosFormatado() {
        String value = getFiltro().toString();
        value = value.length() > 0 ? value.substring(0, value.length() - 2) : value;
        return value;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();

        if (getAdministrativa() == null && getOrcamentaria() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Por favor informe uma unidade administrativa ou orçamentária.");
        }
        if (getLocalEstoque() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Por favor informe o local de estoque.");
        }
        if (getDataInicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Por favor informe a data inicial.");
        }
        if (getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Por favor informe a data final.");
        }
        if ((getDataInicio() != null && getDataFinal() != null) && getDataInicio().after(getDataFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("a data final não pode ser antes da data de inicial.");
        }
        ve.lancarException();
    }

    public HierarquiaOrganizacional getAdministrativa() {
        return administrativa;
    }

    public void setAdministrativa(HierarquiaOrganizacional administrativa) {
        this.administrativa = administrativa;
    }

    public HierarquiaOrganizacional getOrcamentaria() {
        return orcamentaria;
    }

    public void setOrcamentaria(HierarquiaOrganizacional orcamentaria) {
        this.orcamentaria = orcamentaria;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public StringBuilder getFiltro() {
        return filtro;
    }

    public void setFiltro(StringBuilder filtro) {
        this.filtro = filtro;
    }
}
