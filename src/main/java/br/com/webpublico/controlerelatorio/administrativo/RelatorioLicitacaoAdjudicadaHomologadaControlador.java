package br.com.webpublico.controlerelatorio.administrativo;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.LicitacaoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.UsuarioSistemaFacade;
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
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-licitacao-adjudicada-homologada", pattern = "/relatorios/licitacao-adjudicada-homologada/", viewId = "/faces/administrativo/relatorios/licitacao-adjudicada-homologada.xhtml"),
})
public class RelatorioLicitacaoAdjudicadaHomologadaControlador {
    private static final Logger logger = LoggerFactory.getLogger(RelatorioLicitacaoAdjudicadaHomologadaControlador.class);

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    private String filtrosUtilizados;
    private Date dataInicial;
    private Date dataFinal;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private Licitacao licitacao;
    private UsuarioSistema usuarioSistema;
    private boolean mostrarItens;

    @URLAction(mappingId = "relatorio-licitacao-adjudicada-homologada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtrosUtilizados = "";
        dataInicial = sistemaFacade.getDataOperacao();
        dataFinal = sistemaFacade.getDataOperacao();
        hierarquiaAdministrativa = null;
        licitacao = null;
        usuarioSistema = null;
        mostrarItens = false;
    }

    public List<Licitacao> completarLicitacoes(String parte) {
        return licitacaoFacade.buscarLicitacoesPorModalidadeAndNumeroOrDescricaoOrExercico(parte);
    }

    public List<UsuarioSistema> completarUsuarios(String parte) {
        return usuarioSistemaFacade.buscarTodosUsuariosPorLoginOuNome(parte);
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarFiltrosObrigatorios();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("MODULO", "Compras e Licitações");
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("MOSTRAR_ITENS", mostrarItens);
            dto.adicionarParametro("dataFinal", DataUtil.getDataFormatada(dataFinal));
            dto.adicionarParametro("condicao", montarCondicaoEFiltrosUtilizados());
            dto.adicionarParametro("FILTROS", filtrosUtilizados);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("administrativo/licitacao-adjudicada-homologada/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar relatório. ", e);
        }
    }

    private void validarFiltrosObrigatorios() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Final deve ser superior a Data Inicial");
        }
        ve.lancarException();
    }

    private String getNomeRelatorio() {
        return "Relatório de Licitações Adjudicadas/Homologadas";
    }

    private String montarCondicaoEFiltrosUtilizados() {
        String condicao = " where trunc(lic.emitidaEm) between to_date('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy') and to_date('"+ DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy') ";
        filtrosUtilizados = "Período: " + DataUtil.getDataFormatada(dataInicial) + " à " + DataUtil.getDataFormatada(dataFinal);
        if (hierarquiaAdministrativa != null) {
            condicao += " and vwAdm.SUBORDINADA_ID = " + hierarquiaAdministrativa.getSubordinada().getId();
            filtrosUtilizados = " - Unidade Administrativa: " + hierarquiaAdministrativa.toString();
        }
        if (licitacao != null) {
            condicao += " and lic.ID = " + licitacao.getId();
            filtrosUtilizados = " - Licitação: " + licitacao.toString();
        }
        if (usuarioSistema != null) {
            condicao += " and sfl.USUARIOSISTEMA_ID = " + usuarioSistema.getId();
            filtrosUtilizados = " - Usuário: " + usuarioSistema.toString();
        }
        return condicao;
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

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public boolean isMostrarItens() {
        return mostrarItens;
    }

    public void setMostrarItens(boolean mostrarItens) {
        this.mostrarItens = mostrarItens;
    }
}
