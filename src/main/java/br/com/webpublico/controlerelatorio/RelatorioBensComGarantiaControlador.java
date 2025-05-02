package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 16/05/2017.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-bens-moveis-com-garantia", pattern = "/relatorio/bens-moveis-com-garantia/", viewId = "/faces/administrativo/patrimonio/relatorios/relatoriobensmoveiscomgarantia.xhtml"),
    @URLMapping(id = "relatorio-bens-imoveis-com-garantia", pattern = "/relatorio/bens-imoveis-com-garantia/", viewId = "/faces/administrativo/patrimonio/relatorios/relatoriobensimoveiscomgarantia.xhtml")
})
public class RelatorioBensComGarantiaControlador extends AbstractReport implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private HierarquiaOrganizacional hierarquiaOrcamentaria;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private Date dataReferencia;
    private Date dataAquisicaoInicial;
    private Date dataAquisicaoFinal;
    private Date dataVencimentoGarantia;
    private String garantia;
    private String filtro;
    private TipoBem tipoBem;

    @URLAction(mappingId = "relatorio-bens-moveis-com-garantia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposMoveis() {
        tipoBem = TipoBem.MOVEIS;
        limparCampos();
    }

    private void limparCampos() {
        dataReferencia = getSistemaFacade().getDataOperacao();
        dataAquisicaoFinal = null;
        dataAquisicaoInicial = null;
        dataVencimentoGarantia = null;
        garantia = "";
        filtro = "";
        hierarquiaAdministrativa = null;
        hierarquiaOrcamentaria = null;
    }

    @URLAction(mappingId = "relatorio-bens-imoveis-com-garantia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposImoveis() {
        tipoBem = TipoBem.IMOVEIS;
        limparCampos();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("SECRETARIA", montarNomeSecretaria());
            dto.adicionarParametro("tipoBemDTO", tipoBem.getToDto());
            dto.adicionarParametro("dataReferencia", DataUtil.getDataFormatada(dataReferencia));
            dto.adicionarParametro("condicao", montarFiltrosAndCondicao());
            dto.adicionarParametro("FILTROS", filtro.trim());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("administrativo/bens-com-garantia/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        }catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio("Não foi possível gerar o relatório ");
        }
    }

    private String getNomeRelatorio() {
        return "RELATÓRIO DE BENS " + tipoBem.getDescricao().toUpperCase() + " COM GARANTIA";
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaAdministrativa == null && hierarquiaOrcamentaria == null) {
            ve.adicionarMensagemDeCampoObrigatorio("É obrigatório informar a unidade administrativa ou a unidade orçamentária.");
        }
        if (dataReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Referência deve ser informado.");
        }
        if (dataAquisicaoInicial != null && dataAquisicaoFinal != null) {
            if (dataAquisicaoInicial.after(dataAquisicaoFinal)) {
                ve.adicionarMensagemDeOperacaoNaoRealizada("O campo Data de Aquisição Inicial deve ser anterior ou igual ao campo Data de Aquisição Final.");
            }
        }
        ve.lancarException();
    }

    private String montarFiltrosAndCondicao() {
        StringBuilder sb = new StringBuilder();
        String clausula = " and ";
        filtro = " Data de Referência:" + DataUtil.getDataFormatada(dataReferencia) + "; ";
        if (hierarquiaAdministrativa != null) {
            sb.append(clausula).append(" vwadm.subordinada_id = ").append(hierarquiaAdministrativa.getSubordinada().getId());
            filtro += " Unidade Administrativa: " + hierarquiaAdministrativa.getSubordinada().getDescricao() + "; ";
        }
        if (hierarquiaOrcamentaria != null) {
            sb.append(clausula).append(" vworc.subordinada_id = ").append(hierarquiaOrcamentaria.getSubordinada().getId());
            filtro += " Unidade Orçamentária: " + hierarquiaOrcamentaria.getSubordinada().getDescricao() + "; ";
        }
        if (dataAquisicaoInicial != null) {
            sb.append(clausula).append(" bem.dataaquisicao >= to_date('").append(DataUtil.getDataFormatada(dataAquisicaoInicial)).append("','dd/mm/yyyy')");
            filtro += " Data de Aquisição Inicial:" + DataUtil.getDataFormatada(dataAquisicaoInicial) + "; ";
        }
        if (dataAquisicaoFinal != null) {
            sb.append(clausula).append(" bem.dataaquisicao <= to_date('").append(DataUtil.getDataFormatada(dataAquisicaoFinal)).append("','dd/mm/yyyy')");
            filtro += " Data de Aquisição Final:" + DataUtil.getDataFormatada(dataAquisicaoFinal) + "; ";
        }
        if (dataVencimentoGarantia != null) {
            sb.append(clausula).append(" gar.datavencimento = to_date('").append(DataUtil.getDataFormatada(dataVencimentoGarantia)).append("','dd/mm/yyyy')");
            filtro += " Data de Vencimento da Garantia:" + DataUtil.getDataFormatada(dataVencimentoGarantia) + "; ";
        }
        if (garantia != null && !garantia.trim().isEmpty()) {
            sb.append(clausula).append(" lower(gar.descricao) like '%").append(garantia.toLowerCase()).append("%'");
            filtro += " Garantia: " + (garantia.trim().length() > 70 ? garantia.trim().substring(0, 70) : garantia.trim()) + "; ";
        }
        return sb.toString();
    }

    public List<HierarquiaOrganizacional> completarHierarquiaAdministrativaOndeUsuarioLogadoGestorPatrimonio(String filtro) {
        if (dataReferencia != null) {
            return hierarquiaOrganizacionalFacade.buscarHierarquiaOrganizacionalAdministrativaDoUsuario(filtro,
                null,
                dataReferencia,
                getSistemaFacade().getUsuarioCorrente(),
                null,
                Boolean.TRUE);
        }
        return Lists.newArrayList();
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentariaOndeUsuarioLogadoGestorPatrimonio(String filtro) {
        if (dataReferencia != null) {
            return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(filtro,
                null,
                dataReferencia,
                getSistemaFacade().getUsuarioCorrente(),
                null);
        }
        return Lists.newArrayList();
    }

    public void limparHierarquias() {
        hierarquiaAdministrativa = null;
        hierarquiaOrcamentaria = null;
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

    public String getGarantia() {
        return garantia;
    }

    public void setGarantia(String garantia) {
        this.garantia = garantia;
    }

    public Date getDataVencimentoGarantia() {
        return dataVencimentoGarantia;
    }

    public void setDataVencimentoGarantia(Date dataVencimentoGarantia) {
        this.dataVencimentoGarantia = dataVencimentoGarantia;
    }

    public Date getDataAquisicaoFinal() {
        return dataAquisicaoFinal;
    }

    public void setDataAquisicaoFinal(Date dataAquisicaoFinal) {
        this.dataAquisicaoFinal = dataAquisicaoFinal;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public Date getDataAquisicaoInicial() {
        return dataAquisicaoInicial;
    }

    public void setDataAquisicaoInicial(Date dataAquisicaoInicial) {
        this.dataAquisicaoInicial = dataAquisicaoInicial;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public String montarNomeSecretaria() {
        String nome = "";
        if (hierarquiaAdministrativa != null) {
            HierarquiaOrganizacional secretaria = hierarquiaOrganizacionalFacade.recuperarSecretariaAdministrativaVigente(hierarquiaAdministrativa, dataReferencia);
            if (secretaria != null) {
                nome = secretaria.getSubordinada().getDescricao().toUpperCase();
            }
        } else if (hierarquiaOrcamentaria != null) {
            HierarquiaOrganizacional secretaria = hierarquiaOrganizacionalFacade.recuperarSecretariaAdministrativaVigente(
                hierarquiaOrganizacionalFacade.recuperarAdministrativaDaOrcamentariaVigente(hierarquiaOrcamentaria, dataReferencia), dataReferencia);
            if (secretaria != null) {
                nome = secretaria.getSubordinada().getDescricao().toUpperCase();
            }
        }

        return nome;
    }
}
