package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LoteMaterial;
import br.com.webpublico.entidades.Material;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;


@ManagedBean(name = "relatorioConsumoPorUnidade")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorio", pattern = "/relatorio-consumo-por-unidade/novo/", viewId = "/faces/administrativo/materiais/relatorios/relatorioconsumoporunidade.xhtml")
})
public class RelatorioConsumoPorUnidade implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @Limpavel(Limpavel.NULO)
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @Limpavel(Limpavel.NULO)
    private Date dataDeInicio;
    @Limpavel(Limpavel.NULO)
    private Date dataDeFim;
    @Limpavel(Limpavel.NULO)
    private GrupoMaterial grupoMaterial;
    @Limpavel(Limpavel.NULO)
    private LoteMaterial loteMaterial;
    @Limpavel(Limpavel.NULO)
    private Material material;
    private String filtros;

    public Date getDataDeFim() {
        return dataDeFim;
    }

    public void setDataDeFim(Date dataDeFim) {
        this.dataDeFim = dataDeFim;
    }

    public Date getDataDeInicio() {
        return dataDeInicio;
    }

    public void setDataDeInicio(Date dataDeInicio) {
        this.dataDeInicio = dataDeInicio;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public LoteMaterial getLoteMaterial() {
        return loteMaterial;
    }

    public void setLoteMaterial(LoteMaterial loteMaterial) {
        this.loteMaterial = loteMaterial;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    @URLAction(mappingId = "novoRelatorio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        Util.limparCampos(this);
    }


    public void gerarRelatorioAnalitico() {
        try {
            validarCampos();
            limparFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            atribuirParametrosPadroes(dto);
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE CONSUMO POR UNIDADE - ANÁLITICO");
            dto.setNomeRelatorio("RELATÓRIO DE CONSUMO POR UNIDADE - ANÁLITICO");
            dto.setApi("administrativo/consumo-por-unidade/analitico/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorioSintetico() {
        try {
            validarCampos();
            limparFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            atribuirParametrosPadroes(dto);
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE CONSUMO POR UNIDADE - SINTÉTICO");
            dto.setNomeRelatorio("RELATÓRIO DE CONSUMO POR UNIDADE - SINTÉTICO");
            dto.setApi("administrativo/consumo-por-unidade/sintetico/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void atribuirFiltros(RelatorioDTO dto) {
        filtros = filtros.substring(0, filtros.length() - 1);
        dto.adicionarParametro("FILTROS", filtros);
    }

    private void atribuirParametrosPadroes(RelatorioDTO dto) {
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("MODULO", "Materiais");
        dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getPessoaFisica().getNome(), false);
        dto.adicionarParametro("SECRETARIA", hierarquiaOrganizacional.getSubordinada().getEntidade() != null ?
            hierarquiaOrganizacional.getSubordinada().getEntidade().toString() : hierarquiaOrganizacional.getSubordinada().toString());
        dto.adicionarParametro("condicao", montarCondicao());
        dto.adicionarParametro("dataReferencia", sistemaFacade.getDataOperacao());
        atribuirFiltros(dto);
    }

    private void limparFiltros() {
        filtros = "";
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Digite ou informe a hierarquia organizacional para gerar o relatório.");
        }
        if (dataDeInicio == null || dataDeFim == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Digite ou informe as datas para gerar o relatório.");
        }
        ve.lancarException();
        if (dataDeFim.before(dataDeInicio)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data de fim não pode ser antes da data de inicio.");
        }
        ve.lancarException();
    }

    private String montarCondicao() {
        StringBuilder condicao = new StringBuilder();
        if (material != null) {
            condicao.append(" and m.id = ").append(material.getId());
            filtros += " Material: " + material.getCodigo() + " - " + material.getDescricao() + " -";
        }

        if (loteMaterial != null) {
            condicao.append(" and lm.id = ").append(loteMaterial.getId());
            filtros += " Lote material: " + loteMaterial.getIdentificacao() + " -";
        }

        if (dataDeInicio != null) {
            condicao.append(" and trunc(sm.datasaida) >= to_date('").append(DataUtil.getDataFormatada(dataDeInicio)).append("', 'dd/MM/yyyy') ");
            filtros += " Data de Início: " + DataUtil.getDataFormatada(dataDeInicio) + " -";
        }

        if (dataDeFim != null) {
            condicao.append(" and trunc(sm.datasaida) <= to_date('").append(DataUtil.getDataFormatada(dataDeFim)).append("', 'dd/MM/yyyy' )");
            filtros += " Data de Final: " + DataUtil.getDataFormatada(dataDeFim) + " -";
        }

        if (grupoMaterial != null) {
            condicao.append(" and gm.id = ").append(grupoMaterial.getId());
            filtros += " Grupo material: " + grupoMaterial.getCodigo() + " - " + grupoMaterial.getDescricao() + " -";
        }

        if (hierarquiaOrganizacional != null) {
            condicao.append(" and VW.SUBORDINADA_ID = ").append(hierarquiaOrganizacional.getSubordinada().getId());
            filtros += " Unidade Administrativa: " + hierarquiaOrganizacional.getSubordinada().getDescricao() + " -";
        }
        return condicao.toString();
    }

}
