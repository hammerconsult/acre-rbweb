package br.com.webpublico.controlerelatorio.administrativo;

import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LocalEstoque;
import br.com.webpublico.entidades.Material;
import br.com.webpublico.enums.SituacaoRequisicaoMaterial;
import br.com.webpublico.enums.TipoRequisicaoMaterial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.GrupoMaterialFacade;
import br.com.webpublico.negocios.LocalEstoqueFacade;
import br.com.webpublico.negocios.MaterialFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-requisicao-material-por-situacao", pattern = "/administrativo/requisicao-material-por-situacao/", viewId = "/faces/administrativo/materiais/relatorios/relatorio-requisicao-material-por-situacao.xhtml")
})
public class RelatorioRequisicaoMaterialPorSituacaoControlador {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    @EJB
    private MaterialFacade materialFacade;
    private String filtros;
    private Date dataInicial;
    private Date dataFinal;
    private HierarquiaOrganizacional hierarquiaAdm;
    private HierarquiaOrganizacional hierarquiaOrc;
    private LocalEstoque localEstoque;
    private GrupoMaterial grupoMaterial;
    private Material material;
    private TipoRequisicaoMaterial tipoRequisicaoMaterial;
    private SituacaoRequisicaoMaterial situacaoRequisicaoMaterial;

    @URLAction(mappingId = "relatorio-requisicao-material-por-situacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparHierarquiaAdmELocalEstoque();
        grupoMaterial = null;
        material = null;
        tipoRequisicaoMaterial = null;
        situacaoRequisicaoMaterial = null;
        dataInicial = sistemaFacade.getDataOperacao();
        dataFinal = sistemaFacade.getDataOperacao();
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            String nomeRelatorio = "RELATÓRIO DE REQUISIÇÕES DE MATERIAIS POR SITUAÇÃO";
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Materiais");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("condicao", montarCondicaoEFiltros());
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/requisicao-material-por-situacao/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e){
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (localEstoque == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Local de Estoque deve ser informado.");
        }
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Data Inicial do Período deve ser informada.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Data Final do Período deve ser informada.");
        }
        ve.lancarException();
        if (dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Final deve ser superior a Data Inicial.");
        }
        ve.lancarException();
    }

    private String montarCondicaoEFiltros() {
        String condicao = " and trunc(rm.DATAREQUISICAO) between to_date('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy') and to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy') ";
        filtros = "Período: " + DataUtil.getDataFormatada(dataInicial) + " à " + DataUtil.getDataFormatada(dataFinal);
        condicao += " and leOrigem.ID = " + localEstoque.getId();
        filtros += " - Local de Estoque: " + localEstoque.toString();
        if (hierarquiaAdm != null) {
            condicao += " and vwAdm.ID = " + hierarquiaAdm.getId();
            filtros += " - Unidade Administrativa: " + hierarquiaAdm.toString();
        }
        if (hierarquiaOrc != null) {
            condicao += " and leOrcOrigem.unidadeOrcamentaria_id = " + hierarquiaOrc.getSubordinada().getId();
            filtros += " - Unidade Orçamentária: " + hierarquiaOrc.toString();
        }
        if (grupoMaterial != null) {
            condicao += " and gm.ID = " + grupoMaterial.getId();
            filtros += " - Grupo Material: " + grupoMaterial.toString();
        }
        if (material != null) {
            condicao += " and mat.ID = " + material.getId();
            filtros += " - Material: " + material.toString();
        }
        if (tipoRequisicaoMaterial != null) {
            condicao += " and rm.TIPOREQUISICAO = '" + tipoRequisicaoMaterial.name() + "'";
            filtros += " - Tipo de Requisição: " + tipoRequisicaoMaterial.getDescricao();
        }
        if (situacaoRequisicaoMaterial != null) {
            condicao += " and rm.TIPOSITUACAOREQUISICAO = '" + situacaoRequisicaoMaterial.name() + "'";
            filtros += " - Situação da Requisição: " + situacaoRequisicaoMaterial.getDescricao();
        }
        return condicao;
    }

    public void limparHierarquiaAdmELocalEstoque() {
        hierarquiaAdm = null;
        localEstoque = null;
    }

    public List<LocalEstoque> completarLocalEstoque(String filtro) {
        if (hierarquiaAdm != null) {
            return localEstoqueFacade.completarLocaisEstoquePrimeiroNivel(filtro, hierarquiaAdm.getSubordinada());
        }
        return localEstoqueFacade.completarLocaisEstoquePrimeiroNivel(filtro);
    }

    public List<GrupoMaterial> completarGrupoMaterialPorDescricaoOrCodigo(String parte) {
        return grupoMaterialFacade.listaFiltrandoGrupoDeMaterial(parte.trim());
    }

    public List<SelectItem> getTiposDeRequisicao() {
        return Util.getListSelectItem(TipoRequisicaoMaterial.values());
    }

    public List<SelectItem> getSituacoesDaRequisicao() {
        return Util.getListSelectItem(SituacaoRequisicaoMaterial.values());
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

    public HierarquiaOrganizacional getHierarquiaAdm() {
        return hierarquiaAdm;
    }

    public void setHierarquiaAdm(HierarquiaOrganizacional hierarquiaAdm) {
        this.hierarquiaAdm = hierarquiaAdm;
    }

    public HierarquiaOrganizacional getHierarquiaOrc() {
        return hierarquiaOrc;
    }

    public void setHierarquiaOrc(HierarquiaOrganizacional hierarquiaOrc) {
        this.hierarquiaOrc = hierarquiaOrc;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public TipoRequisicaoMaterial getTipoRequisicaoMaterial() {
        return tipoRequisicaoMaterial;
    }

    public void setTipoRequisicaoMaterial(TipoRequisicaoMaterial tipoRequisicaoMaterial) {
        this.tipoRequisicaoMaterial = tipoRequisicaoMaterial;
    }

    public SituacaoRequisicaoMaterial getSituacaoRequisicaoMaterial() {
        return situacaoRequisicaoMaterial;
    }

    public void setSituacaoRequisicaoMaterial(SituacaoRequisicaoMaterial situacaoRequisicaoMaterial) {
        this.situacaoRequisicaoMaterial = situacaoRequisicaoMaterial;
    }
}
