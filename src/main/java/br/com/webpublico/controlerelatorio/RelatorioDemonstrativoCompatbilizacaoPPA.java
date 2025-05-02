package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.enums.LogoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.AcaoPrincipalFacade;
import br.com.webpublico.negocios.PPAFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.relatoriofacade.RelatorioContabilSuperFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 25/10/13
 * Time: 15:30
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-demonstrativo-comp-ppa", pattern = "/relatorio/ppa/demonstrativo-compatbilizacao-ppa-lei-orcamentaria-anual/", viewId = "/faces/financeiro/relatorio/relatoriodemonstrativocompatbilizacaoppa.xhtml"),
    @URLMapping(id = "relatorio-demonstrativo-comp-ppa-novo", pattern = "/relatorio/ppa/demonstrativo-compatbilizacao-ppa-lei-orcamentaria-anual-novo/", viewId = "/faces/financeiro/relatorio/relatoriodemonstrativocompatbilizacaoppa-novo.xhtml")
})
public class RelatorioDemonstrativoCompatbilizacaoPPA implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioDemonstrativoCompatbilizacaoPPA.class);
    @EJB
    private RelatorioContabilSuperFacade facade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    @EJB
    private PPAFacade ppaFacade;
    @EJB
    private AcaoPrincipalFacade acaoPrincipalFacade;
    private AcaoPPA acaoPPA;
    private PPA ppa;
    private AcaoPrincipal acaoPrincipal;
    private ProgramaPPA programaPPA;
    private Apresentacao apresentacao;
    private TipoRelatorio tipoRelatorio;
    private LogoRelatorio logoRelatorio;

    @URLAction(mappingId = "relatorio-demonstrativo-comp-ppa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        iniciarParametros();
    }

    @URLAction(mappingId = "relatorio-demonstrativo-comp-ppa-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposNovo() {
        iniciarParametros();
    }

    private void iniciarParametros() {
        apresentacao = Apresentacao.COM_DESPESA;
        tipoRelatorio = TipoRelatorio.LOA;
        logoRelatorio = LogoRelatorio.PREFEITURA;
        acaoPPA = null;
        acaoPrincipal = null;
        programaPPA = null;
    }

    public List<SelectItem> getApresentacoes() {
        return Util.getListSelectItemSemCampoVazio(Apresentacao.values(), false);
    }

    public List<SelectItem> getTiposDeRelatorio() {
        return Util.getListSelectItemSemCampoVazio(TipoRelatorio.values(), false);
    }

    public List<ProgramaPPA> completarProgramaPPA(String parte) {
        if (ppa != null) {
            return facade.getProgramaPPAFacade().buscarProgramasPorPPA(parte.trim(), ppa);
        }
        return Lists.newArrayList();
    }

    public List<SelectItem> getpPAs() {
        List<SelectItem> toreturn = Lists.newArrayList();
        toreturn.add(new SelectItem(null, "Selecione um PPA"));
        if (facade.getSistemaFacade().getExercicioCorrente() != null) {
            for (PPA object : ppaFacade.listaTodosPpaExericicioCombo(facade.getSistemaFacade().getExercicioCorrente())) {
                toreturn.add(new SelectItem(object, object.toString()));
            }
        } else {
            toreturn = null;
        }
        return toreturn;
    }


    public List<AcaoPPA> completarAcaoPPA(String parte) {
        return facade.getProjetoAtividadeFacade().listaProjetoAtividadePorAcaoPrincipal(acaoPrincipal, parte.trim());
    }

    public List<AcaoPrincipal> completarAcaoPrincipal(String parte) {
        return acaoPrincipalFacade.listaAcaoPPAPorPrograma(programaPPA, parte.trim());
    }

    public void definirNullParaAcaoPrincipal() {
        acaoPrincipal = null;
        acaoPPA = null;
    }

    public void definirNullParaAcao() {
        acaoPPA = null;
    }

    public void definirNullParaPrograma() {
        programaPPA = null;
    }

    public void gerarRelatorio() {
        gerarRelatorio(false);
    }

    public void gerarRelatorioNovo() {
        gerarRelatorio(true);
    }

    public void gerarRelatorio(boolean isRelatorioNovo) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            montarDtoSemApi(isRelatorioNovo, dto);
            dto.setApi("contabil/relatorio-demonstrativo-comp-ppa/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
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

    public void montarDtoSemApi(boolean isRelatorioNovo, RelatorioDTO dto) {
        dto.setNomeParametroBrasao("IMAGEM");
        if (LogoRelatorio.PREFEITURA.equals(logoRelatorio)) {
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("ISCAMARA", Boolean.FALSE);
        } else {
            dto.adicionarParametro("MUNICIPIO", "CÂMARA MUNICIPAL DE RIO BRANCO");
            dto.adicionarParametro("ISCAMARA", Boolean.TRUE);
        }
        dto.adicionarParametro("FILTROS", gerarFiltroSql());
        dto.adicionarParametro("USER", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.adicionarParametro("MODULO", "Planejamento Público");
        dto.adicionarParametro("EXERCICIO_ANO", getExercicio().getAno().toString());
        dto.adicionarParametro("NOME_RELATORIO", getNomeRelatorio(isRelatorioNovo));
        dto.adicionarParametro("ORCADO_EXECUTADO", TipoRelatorio.ORCADO_EXECUTADO.equals(tipoRelatorio));
        dto.adicionarParametro("NOVO_RELATORIO", Boolean.TRUE);
        dto.adicionarParametro("PPA_ID", ppa.getId());
        dto.adicionarParametro("EXERCICIO_ID", getExercicio().getId());
        dto.adicionarParametro("FILTROS", gerarFiltroSql());
        dto.adicionarParametro("ORCADO_EXECUTADO", TipoRelatorio.ORCADO_EXECUTADO.equals(tipoRelatorio));
        dto.adicionarParametro("SEM_DESPESA", apresentacao.isSemDespesa());
        dto.setNomeRelatorio(getNomeRelatorio(isRelatorioNovo));
    }

    public String getNomeRelatorio(boolean isRelatorioNovo) {
        return "Demonstrativo da Compatibilização entre o Plano Plurianual e a Lei Orçamentária Anual " + (isRelatorioNovo ? " II" : "");
    }

    public List<SelectItem> getLogosRelatorios() {
        return Util.getListSelectItemSemCampoVazio(LogoRelatorio.values());
    }

    public StreamedContent gerarExcel(boolean isNovo) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            montarDtoSemApi(isNovo, dto);
            dto.setApi("contabil/relatorio-demonstrativo-comp-ppa/excel/");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
            ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
            ResponseEntity<byte[]> responseEntity = new RestTemplate().exchange(configuracao.getUrl() + dto.getApi() + "gerar", HttpMethod.POST, request, byte[].class);
            byte[] bytes = responseEntity.getBody();
            InputStream stream = new ByteArrayInputStream(bytes);
            return new DefaultStreamedContent(stream, "application/xls", getNomeRelatorio(isNovo) + ".xls");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatório em excel: {}", ex);
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro durante a exportação para Excel: " + ex.getMessage());
        }
        return null;
    }

    private Exercicio getExercicio() {
        if (ppa != null) {
            List<ProgramaPPA> programas = facade.getProgramaPPAFacade().buscarProgramasPorPPA("", ppa);
            for (ProgramaPPA programa : programas) {
                if (programa.getExercicio() != null && !programa.getExercicio().toString().trim().isEmpty()) {
                    return programa.getExercicio();
                }

            }
        }
        return facade.getSistemaFacade().getExercicioCorrente();
    }

    private void validarCampos() {
        ValidacaoException ex = new ValidacaoException();
        if (ppa == null)
            ex.adicionarMensagemDeOperacaoNaoPermitida("O campo ppa deve ser informado.");

        ex.lancarException();
    }

    private String gerarFiltroSql() {
        StringBuilder stb = new StringBuilder();
        if (programaPPA != null) {
            stb.append(" AND PROG.ID = ").append(programaPPA.getId());
        }
        if (acaoPrincipal != null) {
            stb.append(" AND acao.ACAOPRINCIPAL_ID = ").append(acaoPrincipal.getId());
        }
        if (acaoPPA != null) {
            stb.append(" AND acao.ID = ").append(acaoPPA.getId());
        }
        return stb.toString();
    }

    private enum TipoRelatorio {
        LOA("LOA"),
        ORCADO_EXECUTADO("Orçado Executado");
        private String descricao;

        TipoRelatorio(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    private enum Apresentacao {
        COM_DESPESA("Com Despesa"),
        SEM_DESPESA("Sem Despesa");

        private String descricao;

        Apresentacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public boolean isSemDespesa() {
            return SEM_DESPESA.equals(this);
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public AcaoPPA getAcaoPPA() {
        return acaoPPA;
    }

    public void setAcaoPPA(AcaoPPA acaoPPA) {
        this.acaoPPA = acaoPPA;
    }

    public AcaoPrincipal getAcaoPrincipal() {
        return acaoPrincipal;
    }

    public void setAcaoPrincipal(AcaoPrincipal acaoPrincipal) {
        this.acaoPrincipal = acaoPrincipal;
    }

    public ProgramaPPA getProgramaPPA() {
        return programaPPA;
    }

    public void setProgramaPPA(ProgramaPPA programaPPA) {
        this.programaPPA = programaPPA;
    }

    public Apresentacao getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(Apresentacao apresentacao) {
        this.apresentacao = apresentacao;
    }

    public TipoRelatorio getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorio tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public PPA getPpa() {
        return ppa;
    }

    public void setPpa(PPA ppa) {
        this.ppa = ppa;
    }

    public LogoRelatorio getLogoRelatorio() {
        return logoRelatorio;
    }

    public void setLogoRelatorio(LogoRelatorio logoRelatorio) {
        this.logoRelatorio = logoRelatorio;
    }
}
