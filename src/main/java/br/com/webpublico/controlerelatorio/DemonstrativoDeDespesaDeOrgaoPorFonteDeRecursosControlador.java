package br.com.webpublico.controlerelatorio;

/**
 * Created with IntelliJ IDEA. User: RenatoRomanini Date: 27/10/13 Time: 16:49
 * To change this template use File | Settings | File Templates.
 */

import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.LogoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.FonteDeRecursosFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMapping(id = "demonstrativo-despesa-orgao-fonte", pattern = "/relatorio/demonstrativo-despesa-orgao-fonte/", viewId = "/faces/financeiro/relatorio/demonstrativo-despesa-orgao-fonte.xhtml")
public class DemonstrativoDeDespesaDeOrgaoPorFonteDeRecursosControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    private HierarquiaOrganizacional orgaoInicial;
    private HierarquiaOrganizacional orgaoFinal;
    private HierarquiaOrganizacional unidadeInicial;
    private HierarquiaOrganizacional unidadeFinal;
    private FonteDeRecursos fonteDeRecursos;
    private LogoRelatorio logoRelatorio;
    private Boolean mostraUsuario;

    @URLAction(mappingId = "demonstrativo-despesa-orgao-fonte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        orgaoInicial = null;
        orgaoFinal = null;
        unidadeInicial = null;
        unidadeFinal = null;
        fonteDeRecursos = null;
        logoRelatorio = LogoRelatorio.PREFEITURA;
        mostraUsuario = Boolean.FALSE;
    }

    public LogoRelatorio getLogoRelatorio() {
        return logoRelatorio;
    }

    public void setLogoRelatorio(LogoRelatorio logoRelatorio) {
        this.logoRelatorio = logoRelatorio;
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

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public List<FonteDeRecursos> completaFonteDeRecursos(String parte) {
        return fonteDeRecursosFacade.listaFiltrandoPorExercicio(parte.trim(), sistemaFacade.getExercicioCorrente());
    }

    public List<HierarquiaOrganizacional> completarHierarquiasOrganizacionais(String parte) {
        return hierarquiaOrganizacionalFacade.filtraPorNivel(parte, "2", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaFacade.getDataOperacao());
    }

    public List<HierarquiaOrganizacional> completarHierarquiasOrganizacionaisUnidades(String parte) {
        return hierarquiaOrganizacionalFacade.filtraPorNivel(parte, "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaFacade.getDataOperacao());
    }

    public List<SelectItem> getLogos() {
        return Util.getListSelectItemSemCampoVazio(LogoRelatorio.values());
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", mostraUsuario ? "Emitido por: " + sistemaFacade.getUsuarioCorrente().getNome() : "", false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("ANO_EXERCICIO", sistemaFacade.getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO_ID", sistemaFacade.getExercicioCorrente().getId());
            dto.adicionarParametro("ISCAMARA", LogoRelatorio.CAMARA.equals(logoRelatorio));
            dto.adicionarParametro("MUNICIPIO", LogoRelatorio.CAMARA.equals(logoRelatorio) ? "CÂMARA MUNICIPAL DE RIO BRANCO" : "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            dto.adicionarParametro("condicao", montarCondicao());
            dto.setNomeRelatorio("Demonstrativo de Despesa de Órgão por Fonte de Recursos");
            dto.setApi("contabil/demonstrativo-despesa-orgao-por-fonte/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String montarCondicao() {
        StringBuilder condicao = new StringBuilder();
        if (orgaoInicial != null && orgaoFinal != null) {
            condicao.append(" and ho_org.codigo between ").append(orgaoInicial.getCodigo()).append(" and ").append(orgaoFinal.getCodigo());
        }
        if (unidadeInicial != null && unidadeFinal != null) {
            condicao.append(" and ho.codigo between ").append(unidadeInicial.getCodigo()).append(" and ").append(unidadeFinal.getCodigo());
        }
        if (fonteDeRecursos != null) {
            condicao.append(" and font.id = ").append(fonteDeRecursos.getId());
        }
        return condicao.toString();
    }

    public Boolean getMostraUsuario() {
        return mostraUsuario;
    }

    public void setMostraUsuario(Boolean mostraUsuario) {
        this.mostraUsuario = mostraUsuario;
    }
}
