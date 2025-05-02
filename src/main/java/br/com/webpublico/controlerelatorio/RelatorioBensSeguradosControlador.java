package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.Pessoa;
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
 * Data: 05/05/2017.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-bens-moveis-segurados", pattern = "/relatorio/bens-moveis-segurados/", viewId = "/faces/administrativo/patrimonio/relatorios/relatoriobensmoveissegurados.xhtml"),
    @URLMapping(id = "relatorio-bens-imoveis-segurados", pattern = "/relatorio/bens-imoveis-segurados/", viewId = "/faces/administrativo/patrimonio/relatorios/relatoriobensimoveissegurados.xhtml")
})
public class RelatorioBensSeguradosControlador extends AbstractReport implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private HierarquiaOrganizacional hierarquiaOrcamentaria;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private Date dataReferencia;
    private Date dataAquisicaoInicial;
    private Date dataAquisicaoFinal;
    private Date dataVencimentoApolice;
    private Pessoa seguradora;
    private String apolice;
    private String filtro;
    private TipoBem tipoBem;

    @URLAction(mappingId = "relatorio-bens-moveis-segurados", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposMoveis() {
        tipoBem = TipoBem.MOVEIS;
        limparCampos();
    }

    private void limparCampos() {
        dataReferencia = getSistemaFacade().getDataOperacao();
        dataAquisicaoFinal = null;
        dataAquisicaoInicial = null;
        dataVencimentoApolice = null;
        seguradora = null;
        apolice = "";
        filtro = "";
        hierarquiaAdministrativa = null;
        hierarquiaOrcamentaria = null;
    }

    @URLAction(mappingId = "relatorio-bens-imoveis-segurados", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
            dto.setApi("administrativo/bens-segurados/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio("Não foi possível gerar o relatório ");
        }
    }

    private String getNomeRelatorio() {
        return "RELATÓRIO DE BENS " + tipoBem.getDescricao().toUpperCase() + " SEGURADOS";
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
        if (dataVencimentoApolice != null) {
            sb.append(clausula).append(" seg.vencimento = to_date('").append(DataUtil.getDataFormatada(dataVencimentoApolice)).append("','dd/mm/yyyy')");
            filtro += " Data de Vencimento da Apólice:" + DataUtil.getDataFormatada(dataVencimentoApolice) + "; ";
        }
        if (seguradora != null) {
            sb.append(clausula).append(" seg.pessoa_id = ").append(seguradora.getId());
            filtro += " Seguradora:" + seguradora.getNome() + "; ";
        }
        if (apolice != null && !apolice.trim().isEmpty()) {
            sb.append(clausula).append(" lower(seg.apolice) like '%").append(apolice.toLowerCase()).append("%'");
            filtro += " Apólice: " + (apolice.trim().length() > 70 ? apolice.trim().substring(0, 70) : apolice.trim()) + "; ";
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

    public String getApolice() {
        return apolice;
    }

    public void setApolice(String apolice) {
        this.apolice = apolice;
    }

    public Pessoa getSeguradora() {
        return seguradora;
    }

    public void setSeguradora(Pessoa seguradora) {
        this.seguradora = seguradora;
    }

    public Date getDataVencimentoApolice() {
        return dataVencimentoApolice;
    }

    public void setDataVencimentoApolice(Date dataVencimentoApolice) {
        this.dataVencimentoApolice = dataVencimentoApolice;
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
            HierarquiaOrganizacional secretaria = hierarquiaOrganizacionalFacade
                .recuperarSecretariaAdministrativaVigente(hierarquiaOrganizacionalFacade
                    .recuperarAdministrativaDaOrcamentariaVigente(hierarquiaOrcamentaria, dataReferencia), dataReferencia);
            if (secretaria != null) {
                nome = secretaria.getSubordinada().getDescricao().toUpperCase();
            }
        }

        return nome;
    }
}
