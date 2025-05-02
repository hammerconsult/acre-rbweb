package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;

/**
 * Created by Desenvolvimento on 13/03/2017.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioLevantamentoImovelComparativo",
        pattern = "/relatorio-levantamento-bens-imoveis-comparativo/",
        viewId = "/faces/administrativo/patrimonio/relatorios/imovel/relatorioLevantamentoBemImovelComparativo.xhtml")})
public class RelatorioLevantamentoImovelComparativoControlador {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioLevantamentoImovelComparativoControlador.class);
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private Date dtinicial;
    private Date dtFinal;
    private Date dtReferencia;
    private HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria;
    private GrupoBem grupoBem;

    @URLAction(mappingId = "novoRelatorioLevantamentoImovelComparativo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioLevantamentoImovelComparativo() {
        limparCampos();
    }

    public void limparCampos() {
        dtinicial = null;
        dtFinal = null;
        hierarquiaOrganizacionalOrcamentaria = null;
        grupoBem = null;
        dtReferencia = new Date();
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalOrcamentaria() {
        return hierarquiaOrganizacionalOrcamentaria;
    }

    public void setHierarquiaOrganizacionalOrcamentaria(HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria) {
        this.hierarquiaOrganizacionalOrcamentaria = hierarquiaOrganizacionalOrcamentaria;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }


    public Date getDtinicial() {
        return dtinicial;
    }


    public void setDtinicial(Date dtinicial) {
        this.dtinicial = dtinicial;
    }


    public Date getDtFinal() {
        return dtFinal;
    }


    public void setDtFinal(Date dtFinal) {
        this.dtFinal = dtFinal;
    }


    public Date getDtReferencia() {
        return dtReferencia;
    }


    public void setDtReferencia(Date dtReferencia) {
        this.dtReferencia = dtReferencia;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltrosRelatorioLevantamentoImovelComparativo();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            String nomeRelatorio = "RELATÓRIO DE LEVANTAMENTO DE BENS IMÓVEIS POR GRUPO PATRIMONIAL COMPARATIVO";
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE " + sistemaFacade.getMunicipio().toUpperCase());
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.adicionarParametro("SECRETARIA", getNomeSecretaria());
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("DATAATUAL", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            dto.adicionarParametro("DATAREFERENCIA", DataUtil.getDataFormatada(getDtReferencia()));
            dto.adicionarParametro("condicao", montarCondicaoEFiltros(dto));
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/levantamento-imovel-comparativo/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
            logger.error("Erro ao gerar relatorio ", ex);
        }
    }

    private String montarCondicaoEFiltros(RelatorioDTO dto) {
        StringBuffer sql = new StringBuffer();
        StringBuffer filtros = new StringBuffer();

        if (getHierarquiaOrganizacionalOrcamentaria() != null && getHierarquiaOrganizacionalOrcamentaria().getCodigo() != null) {
            sql.append(" and ( s.unidade = ").append(getHierarquiaOrganizacionalOrcamentaria().getSubordinada().getId()).append(" or s.unidade is null )");
            filtros.append(" Unidade Orçamentária: ").append(getHierarquiaOrganizacionalOrcamentaria().toString()).append(". ");
        }

        if (getDtinicial() != null) {
            sql.append(" and (s.dataaquisicao >= to_date('").append(DataUtil.getDataFormatada(getDtinicial())).append("', 'dd/MM/yyyy') or s.dataaquisicao is null )");
            filtros.append(" Data de Aquisição Inicial: ").append(DataUtil.getDataFormatada(getDtinicial())).append(". ");
        }

        if (getDtFinal() != null) {
            sql.append(" and (s.dataaquisicao <= to_date('").append(DataUtil.getDataFormatada(getDtFinal())).append("', 'dd/MM/yyyy') or s.dataaquisicao is null )");
            filtros.append(" Data de Aquisição Final: ").append(DataUtil.getDataFormatada(getDtFinal())).append(". ");
        }

        if (getGrupoBem() != null) {
            filtros.append(" Grupo Patrimonial ").append(getGrupoBem().toString()).append(". ");
            sql.append(" and GRUPOBEM.id = ").append(getGrupoBem().getId());
        }

        if (getDtReferencia() != null) {
            filtros.append(" Data de Referência: ").append(DataUtil.getDataFormatada(getDtReferencia()));
        }

        dto.adicionarParametro("FILTROS", filtros.toString());
        return sql.toString();
    }


    private void validarFiltrosRelatorioLevantamentoImovelComparativo() {
        ValidacaoException ve = new ValidacaoException();
        if (getHierarquiaOrganizacionalOrcamentaria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione a unidade orçamentária.");
        }
        if (getDtReferencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data de referência contábil.");
        }

        if (getDtinicial() != null && getDtFinal() != null) {
            if (getDtinicial().after(getDtFinal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de aquisição inicial deve ser anterior a data final de aquisição.");
            }
        }
        ve.lancarException();
    }

    private String getNomeSecretaria() {
        String nome = "";
        if (hierarquiaOrganizacionalOrcamentaria != null) {
            HierarquiaOrganizacional hierarquiaAdministrativaDaOrcamentaria = hierarquiaOrganizacionalFacade.recuperarAdministrativaDaOrcamentariaVigente(this.hierarquiaOrganizacionalOrcamentaria, sistemaFacade.getDataOperacao());
            HierarquiaOrganizacional secretaria = hierarquiaOrganizacionalFacade.recuperarSecretariaAdministrativaVigente(hierarquiaAdministrativaDaOrcamentaria, sistemaFacade.getDataOperacao());
            if (secretaria != null) {
                nome = secretaria.getDescricao().toUpperCase();
            }
        }
        return nome;
    }
}
