package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioServidoresAtivosPorSecretaria;
import br.com.webpublico.entidadesauxiliares.rh.RelatorioDescontoPrevidenciarioVO;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Claudio
 * Date: 11/06/14
 * Time: 08:21
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "gerarRelatorioDescontoPrevidenciario", pattern = "/relatorio/relatorio-desconto-previdenciario/gerar/", viewId = "/faces/rh/relatorios/relatoriodescontoprevidenciario.xhtml")
})
public class RelatorioDescontoPrevidenciario implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioDescontoPrevidenciario.class);
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private TipoPrevidenciaFPFacade tipoPrevidenciaFPFacade;
    @EJB
    private RecursoFPFacade recursoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private VinculoFP vinculoFP;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private TipoPrevidenciaFP tipoPrevidenciaFP;
    private List<TipoPrevidenciaFP> tiposPrevidenciaFP;
    private Boolean todosOrgaos;
    private String matriculaInicial;
    private String matriculaFinal;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private ConverterGenerico converterTipoPrevidenciaFP;
    private List<RecursoFP> grupoRecurso;
    private RecursoFP[] recursoSelecionados;
    private RelatorioDescontoPrevidenciarioVO filtroRelatorio;
    private String filtro;

    public RelatorioDescontoPrevidenciario() {

    }

    public Boolean getTodosOrgaos() {
        return todosOrgaos;
    }

    public void setTodosOrgaos(Boolean todosOrgaos) {
        this.todosOrgaos = todosOrgaos;
    }

    public TipoPrevidenciaFP getTipoPrevidenciaFP() {
        return tipoPrevidenciaFP;
    }

    public void setTipoPrevidenciaFP(TipoPrevidenciaFP tipoPrevidenciaFP) {
        this.tipoPrevidenciaFP = tipoPrevidenciaFP;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public String getMatriculaInicial() {
        return matriculaInicial;
    }

    public void setMatriculaInicial(String matriculaInicial) {
        this.matriculaInicial = matriculaInicial;
    }

    public String getMatriculaFinal() {
        return matriculaFinal;
    }

    public void setMatriculaFinal(String matriculaFinal) {
        this.matriculaFinal = matriculaFinal;
    }

    public List<RecursoFP> getGrupoRecurso() {
        return grupoRecurso;
    }

    public void setGrupoRecurso(List<RecursoFP> grupoRecurso) {
        this.grupoRecurso = grupoRecurso;
    }

    public RecursoFP[] getRecursoSelecionados() {
        return recursoSelecionados;
    }

    public void setRecursoSelecionados(RecursoFP[] recursoSelecionados) {
        this.recursoSelecionados = recursoSelecionados;
    }

    public List<HierarquiaOrganizacional> completaHierarquiasOrganizacionais(String parte) {
        return hierarquiaOrganizacionalFacade.filtraPorNivel(parte.trim(), "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());

    }

    public List<TipoPrevidenciaFP> getTiposPrevidenciaFP() {
        tiposPrevidenciaFP = tipoPrevidenciaFPFacade.lista();
        return tiposPrevidenciaFP;
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public ConverterGenerico getConverterTipoPrevidenciaFP() {
        if (converterTipoPrevidenciaFP == null) {
            converterTipoPrevidenciaFP = new ConverterGenerico(TipoPrevidenciaFP.class, tipoPrevidenciaFPFacade);
        }
        return converterTipoPrevidenciaFP;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    @URLAction(mappingId = "gerarRelatorioDescontoPrevidenciario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        vinculoFP = null;
        hierarquiaOrganizacional = null;
        tipoPrevidenciaFP = null;
        filtroRelatorio = new RelatorioDescontoPrevidenciarioVO();


    }

    public void limparCampos() {
        hierarquiaOrganizacional = null;
        tipoPrevidenciaFP = null;
        matriculaInicial = null;
        matriculaFinal = null;
        filtroRelatorio = null;
        grupoRecurso = new ArrayList<>();
        recursoSelecionados = new RecursoFP[]{};

    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validaCampos();
            DateTime dateTime = montarParametrosDatas();
            String sql = gerarSql();
            filtroRelatorio.montaParametrosMeses();
            String codigoOrgao[] = hierarquiaOrganizacional.getCodigoSemZerosFinais().split("\\.");
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeParametroBrasao("BRASAO");
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("SQL", sql);
            dto.adicionarParametro("MES", (filtroRelatorio.getMes() - 1));
            dto.adicionarParametro("ANO", filtroRelatorio.getAno());
            dto.adicionarParametro("DATAVIGENCIA", DataUtil.getDataFormatada(dateTime.toDate()));
            dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            dto.adicionarParametro("HIERARQUIAORGANIZACIONAL", hierarquiaOrganizacional.toString());
            dto.adicionarParametro("ORGAO", " and rec.codigo like '" + codigoOrgao[codigoOrgao.length - 1] + "%'");
            dto.adicionarParametro("TIPOPREVIDENCIA", tipoPrevidenciaFP.getCodigo());
            dto.adicionarParametro("FILTROS", filtro);
            dto.adicionarParametro("FILTRO_ITENS", RelatorioDescontoPrevidenciarioVO.descontoPrevidenciarioToDto(filtroRelatorio));
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO");
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("GRUPO", getMontaGrupoRecurso());
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getPessoaFisica() != null ? sistemaFacade.getUsuarioCorrente().getPessoaFisica().getNome() : sistemaFacade.getUsuarioCorrente().getLogin());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("rh/relatorio-desconto-previdenciario/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("gerarRelatorio {}", e);
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private DateTime montarParametrosDatas() {
        DateTime dateTime = new DateTime(new Date());
        DateTime dateMes = new DateTime(getSistemaFacade().getDataOperacao());
        dateTime = dateTime.withMonthOfYear(filtroRelatorio.getMes());
        dateTime = dateTime.withYear(filtroRelatorio.getAno());
        if (dateMes.getMonthOfYear() == dateTime.getMonthOfYear()) {
            dateTime.withDayOfMonth(dateMes.getDayOfMonth());
        } else {
            dateTime = dateTime.withDayOfMonth(dateTime.dayOfMonth().getMaximumValue());
        }
        return dateTime;
    }

    private String getNomeRelatorio() {
        return "RELATÓRIO DE DESCONTOS PREVIDENCIÁRIOS";
    }

    public void validaCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroRelatorio.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês é obrigatório.");
        } else if (filtroRelatorio.getMes() < 1 || filtroRelatorio.getMes() > 12) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar um mês entre 01 (Janeiro) e 12 (Dezembro).");
        }
        if (filtroRelatorio.getAno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano é obrigatório.");
        }
        if (tipoPrevidenciaFP == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Previdência é obrigatório.");
        }
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão é obrigatório.");
        }
        if (matriculaInicial != null && matriculaFinal != null) {
            if (matriculaFinal.compareTo(getMatriculaInicial()) < 0) {
                ve.adicionarMensagemDeCampoObrigatorio("A Matrícula Inicial não pode ser maior que a Matrícula Final.");
            }
        }
        if (this.hierarquiaOrganizacional != null && this.recursoSelecionados.length == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o(s) recurso(s).");
        }
        ve.lancarException();
    }

    private String getMontaGrupoRecurso() {
        String retorno = " and rec.id in ( ";
        for (RecursoFP grupo : recursoSelecionados) {
            retorno += "'" + grupo.getId() + "',";
        }
        retorno = retorno.substring(0, retorno.length() - 1);
        retorno += ") ";
        return retorno;
    }

    private String gerarSql() {
        StringBuilder stb = new StringBuilder();
        String juncao = " AND ";
        filtro = "";

        filtro += "Tipo de Previdência: " + tipoPrevidenciaFP.getDescricao();

        if (!matriculaInicial.equals("") && !matriculaFinal.equals("")) {
            stb.append(juncao).append(" mat.matricula BETWEEN ").append(matriculaInicial).append(juncao).append(matriculaFinal);
        }
        return stb.toString();
    }

    public void carregaListaRecursos() {
        if (hierarquiaOrganizacional != null) {
            grupoRecurso = recursoFPFacade.retornaRecursoFPDo2NivelDeHierarquia(hierarquiaOrganizacional, sistemaFacade.getDataOperacao());
        }
    }

    public RelatorioDescontoPrevidenciarioVO getFiltroRelatorio() {
        return filtroRelatorio;
    }

    public void setFiltroRelatorio(RelatorioDescontoPrevidenciarioVO filtroRelatorio) {
        this.filtroRelatorio = filtroRelatorio;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
