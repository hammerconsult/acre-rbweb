package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.LogoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoRelatorioAnexoDoisPlanejamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 13/10/13
 * Time: 19:30
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "anexo2-planejamento", pattern = "/relatorio/planejamento/lei4320/anexo2/", viewId = "/faces/financeiro/relatorio/relatorioanexo4despesa.xhtml")
})
@ManagedBean
public class RelatorioAnexoDoisDespesaPlanejamentoControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioAnexoDoisDespesaPlanejamentoControlador.class);
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private HierarquiaOrganizacional orgaoInicial;
    private HierarquiaOrganizacional orgaoFinal;
    private HierarquiaOrganizacional unidadeInicial;
    private HierarquiaOrganizacional unidadeFinal;
    private TipoRelatorioAnexoDoisPlanejamento tipoRelatorio;
    private LogoRelatorio logoRelatorio;
    private Boolean mostraUsuario;

    @URLAction(mappingId = "anexo2-planejamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        tipoRelatorio = TipoRelatorioAnexoDoisPlanejamento.GERAL;
        logoRelatorio = LogoRelatorio.PREFEITURA;
        mostraUsuario = false;
    }

    public List<HierarquiaOrganizacional> completarHierarquiasOrganizacionais(String parte) {
        return hierarquiaOrganizacionalFacade.filtraPorNivel(parte, "2", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaFacade.getDataOperacao());
    }

    public List<HierarquiaOrganizacional> completarHierarquiasOrganizacionaisUnidade(String parte) {
        return hierarquiaOrganizacionalFacade.filtraPorNivel(parte, "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaFacade.getDataOperacao());
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            Exercicio exercicio = sistemaFacade.getExercicioCorrente();
            RelatorioDTO dto = new RelatorioDTO();
            boolean isCamara = LogoRelatorio.CAMARA.equals(logoRelatorio);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", isCamara ? "CÂMARA MUNICIPAL DE RIO BRANCO" : "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("ISCAMARA", isCamara);
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
            dto.adicionarParametro("EXERCICIO_ID", exercicio.getId());
            dto.adicionarParametro("DATA", sistemaFacade.getDataOperacao().getTime());
            dto.adicionarParametro("TIPORELATORIO", tipoRelatorio.getToDto());
            dto.adicionarParametro("FILTROS", !TipoRelatorioAnexoDoisPlanejamento.GERAL.equals(tipoRelatorio) ? montarParametros() : "");
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.adicionarParametro("USER", mostraUsuario ? "Emitido por: " + sistemaFacade.getUsuarioCorrente().getNome() : "", false);
            dto.setApi("contabil/anexo-2-despesa-planejamento/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatório: {}", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (orgaoInicial == null && orgaoFinal != null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão Inicial deve ser informado.");
        }
        if (orgaoInicial != null && orgaoFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão Final deve ser informado.");
        }
        if (unidadeInicial == null && unidadeFinal != null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Inicial deve ser informado.");
        }
        if (unidadeInicial != null && unidadeFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Final deve ser informado.");
        }
        ve.lancarException();
        if (orgaoInicial != null && orgaoFinal != null && orgaoInicial.getCodigo().compareTo(orgaoFinal.getCodigo()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O código do Órgão Inicial deve ser menor que o código do Órgão Final.");
        }
        if (unidadeInicial != null && unidadeFinal != null && unidadeInicial.getCodigo().compareTo(unidadeFinal.getCodigo()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O código da Unidade Inicial deve ser menor que o código da Unidade Final.");
        }
        ve.lancarException();
    }

    private String getNomeRelatorio() {
        if (TipoRelatorioAnexoDoisPlanejamento.GERAL.equals(tipoRelatorio)) {
            return "ANEXO-02-NATUREZA-DA-DESPESA-CONSOLIDADO";
        } else if (TipoRelatorioAnexoDoisPlanejamento.ORGAO_UNIDADE.equals(tipoRelatorio)) {
            return "ANEXO-02-NATUREZA-DA-DESPESA-ORGAO-UNIDADE";
        } else if (TipoRelatorioAnexoDoisPlanejamento.ORGAO.equals(tipoRelatorio)) {
            return "ANEXO-02-NATUREZA-DA-DESPESA-ORGAO";
        }
        return "";
    }

    public List<SelectItem> getLogosRelatorio() {
        return Util.getListSelectItemSemCampoVazio(LogoRelatorio.values());
    }

    public String montarParametros() {
        String retorno = "";
        if (orgaoInicial != null && orgaoFinal != null) {
            retorno += " and vwsup.codigo between '" + orgaoInicial.getCodigo() + "' and '" + orgaoFinal.getCodigo() + "'";
        }
        if (TipoRelatorioAnexoDoisPlanejamento.ORGAO_UNIDADE.equals(tipoRelatorio) && unidadeInicial != null && unidadeFinal != null) {
            retorno += " and vwsub.codigo between '" + unidadeInicial.getCodigo() + "' and '" + unidadeFinal.getCodigo() + "'";
        }
        return retorno;
    }

    public HierarquiaOrganizacional getOrgaoInicial() {
        return orgaoInicial;
    }

    public void setOrgaoInicial(HierarquiaOrganizacional orgaoInicial) {
        this.orgaoInicial = orgaoInicial;
    }

    public HierarquiaOrganizacional getOrgaoFinal() {
        return orgaoFinal;
    }

    public void setOrgaoFinal(HierarquiaOrganizacional orgaoFinal) {
        this.orgaoFinal = orgaoFinal;
    }

    public HierarquiaOrganizacional getUnidadeInicial() {
        return unidadeInicial;
    }

    public void setUnidadeInicial(HierarquiaOrganizacional unidadeInicial) {
        this.unidadeInicial = unidadeInicial;
    }

    public HierarquiaOrganizacional getUnidadeFinal() {
        return unidadeFinal;
    }

    public void setUnidadeFinal(HierarquiaOrganizacional unidadeFinal) {
        this.unidadeFinal = unidadeFinal;
    }

    public TipoRelatorioAnexoDoisPlanejamento getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorioAnexoDoisPlanejamento tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public List<SelectItem> getTiposRelatorios() {
        return Util.getListSelectItemSemCampoVazio(TipoRelatorioAnexoDoisPlanejamento.values(), false);
    }

    public LogoRelatorio getLogoRelatorio() {
        return logoRelatorio;
    }

    public void setLogoRelatorio(LogoRelatorio logoRelatorio) {
        this.logoRelatorio = logoRelatorio;
    }

    public Boolean getMostraUsuario() {
        return mostraUsuario;
    }

    public void setMostraUsuario(Boolean mostraUsuario) {
        this.mostraUsuario = mostraUsuario;
    }
}
