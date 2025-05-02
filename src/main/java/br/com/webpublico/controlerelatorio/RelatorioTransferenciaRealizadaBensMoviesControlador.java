package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.LoteEfetivacaoTransferenciaBemFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by HardRock on 16/02/2017.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-transf-realizada", pattern = "/patrimonio/relatorio-transferencia-realizada-bens-moveis/", viewId = "/faces/administrativo/patrimonio/relatorios/relatorio-transf-realizada-bens-moveis.xhtml")})

public class RelatorioTransferenciaRealizadaBensMoviesControlador extends AbstractReport implements Serializable {

    @EJB
    private LoteEfetivacaoTransferenciaBemFacade facade;
    private StringBuilder filtros;
    private Date dataInicial;
    private Date dataFinal;
    private HierarquiaOrganizacional hierarquiaOrcamentaria;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private GrupoBem grupoPatrimonial;

    @URLAction(mappingId = "novo-relatorio-transf-realizada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        filtros = new StringBuilder();
        this.dataInicial = getSistemaFacade().getDataOperacao();
        this.dataFinal = getSistemaFacade().getDataOperacao();
        this.hierarquiaOrcamentaria = null;
        this.hierarquiaAdministrativa = null;
        this.grupoPatrimonial = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Patrimonial");
            dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE TRANSFERÊNCIA REALIZADA DE BENS MÓVEIS");
            dto.adicionarParametro("condicao", montarCondicoes());
            dto.adicionarParametro("FILTROS", filtros.toString());
            dto.setNomeRelatorio("RELATÓRIO-DE-TRANSFERÊNCIA-REALIZADA-DE-BENS-MÓVEIS");
            dto.setApi("administrativo/transf-realizada/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaAdministrativa == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Administrativa deve ser informado.");
        }
        if (this.dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (this.dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (this.dataFinal.before(this.dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Final deve ser superior ou igual ao campo Data Inicial.");
        }
        ve.lancarException();
    }

    private String getDataInicialFormatada() {
        return DataUtil.getDataFormatada(this.dataInicial);
    }

    private String getDataFinalFormatada() {
        return DataUtil.getDataFormatada(this.dataFinal);
    }

    public String montarCondicoes() {

        StringBuilder condicoes = new StringBuilder();
        filtros.setLength(0);
        filtros.append("Critérios Utilizado: ");
        filtros.append("\n");

        condicoes.append(" and grupo.tipobem = '").append(TipoBem.MOVEIS.name()).append("' ");

        if (this.dataInicial != null && this.dataFinal != null) {
            condicoes.append(" AND trunc(loteefetivacao.DATAEFETIVACAO) BETWEEN to_date('").append((getDataInicialFormatada())).append("', 'dd/MM/yyyy')")
                .append(" AND to_date('").append(getDataFinalFormatada()).append("', 'dd/MM/yyyy')");
            filtros.append("Período: ").append(getDataInicialFormatada()).append(" à ").append(getDataFinalFormatada());
        }
        if (this.hierarquiaAdministrativa != null) {
            filtros.append("\n");
            condicoes.append(" AND admOrigem.codigo like '").append(hierarquiaAdministrativa.getCodigoSemZerosFinais()).append("%'");
            filtros.append("Unidade Administrativa: ").append(hierarquiaAdministrativa.getCodigo()).append(" - ").append(hierarquiaAdministrativa.getSubordinada().getDescricao());
        }
        if (hierarquiaOrcamentaria != null) {
            filtros.append("\n");
            condicoes.append(" AND orcOrigem.subordinada_id = ").append(hierarquiaOrcamentaria.getSubordinada().getId());
            filtros.append("Unidade Orçamentária:  ").append(hierarquiaOrcamentaria.getCodigo()).append(" - ").append(hierarquiaOrcamentaria.getSubordinada().getDescricao());
        }
        if (this.grupoPatrimonial != null) {
            filtros.append("\n");
            condicoes.append(" AND grupo.ID = ").append(grupoPatrimonial.getId());
            filtros.append("Grupo Patrimonial: ").append(grupoPatrimonial);
        }
        return condicoes.toString();
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentaria(String parte) {
        if (hierarquiaAdministrativa != null) {
            return facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(hierarquiaAdministrativa.getSubordinada(), getSistemaFacade().getDataOperacao());
        }
        FacesUtil.addAtencao("O campo Unidade Administrativa deve ser informado para filtrar a Unidade Orçamentária.");
        return new ArrayList<>();
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

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public GrupoBem getGrupoPatrimonial() {
        return grupoPatrimonial;
    }

    public void setGrupoPatrimonial(GrupoBem grupoPatrimonial) {
        this.grupoPatrimonial = grupoPatrimonial;
    }

    public void definirHierarquiasComoNull() {
        hierarquiaOrcamentaria = null;
        hierarquiaAdministrativa = null;
    }
}
