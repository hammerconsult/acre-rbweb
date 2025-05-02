package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 16/10/15
 * Time: 16:24
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-planejamento-projeto", pattern = "/relatorio/planejamento-projeto/", viewId = "/faces/financeiro/relatorio/relatorio-planejamento-projeto.xhtml"),
    @URLMapping(id = "relatorio-planejamento-atividade", pattern = "/relatorio/planejamento-atividade/", viewId = "/faces/financeiro/relatorio/relatorio-planejamento-atividade.xhtml")
})
@ManagedBean
public class RelatorioPlanejamentoProjetoAtividadeControlador implements Serializable {

    @EJB
    private SubProjetoAtividadeFacade subProjetoAtividadeFacade;
    @EJB
    private FuncaoFacade funcaoFacade;
    @EJB
    private SubFuncaoFacade subFuncaoFacade;
    @EJB
    private AcaoPrincipalFacade acaoPrincipalFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private List<HierarquiaOrganizacional> unidades;
    private Funcao funcao;
    private SubFuncao subFuncao;
    private SubAcaoPPA subAcaoPPA;
    private AcaoPrincipal acaoPrincipal;
    private Enum tipoRelatorio;

    public RelatorioPlanejamentoProjetoAtividadeControlador() {
    }

    @URLAction(mappingId = "relatorio-planejamento-projeto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposProjeto() {
        iniciarVariaveis();
        this.tipoRelatorio = TipoRelatorio.PROJETO;
    }

    private void iniciarVariaveis() {
        unidades = Lists.newArrayList();
        funcao = null;
        subAcaoPPA = null;
        subFuncao = null;
        acaoPrincipal = null;
    }

    @URLAction(mappingId = "relatorio-planejamento-atividade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposAtividade() {
        iniciarVariaveis();
        this.tipoRelatorio = TipoRelatorio.ATIVIDADE;
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.adicionarParametro("NOME_RELATORIO", TipoRelatorio.PROJETO.equals(tipoRelatorio) ? "Relatório de Projetos" : "Relatório de Atividades");
            dto.adicionarParametro("NOME_COLUNA_PROJETOATIVIDADE", TipoRelatorio.PROJETO.equals(tipoRelatorio) ? "Projeto" : "Atividade");
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.setNomeRelatorio(getNomeRelatorioaAoImprimir());
            dto.setApi("contabil/planejamento-projeto-atividade/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeRelatorioaAoImprimir() {
        if (TipoRelatorio.PROJETO.equals(tipoRelatorio)) {
            return "RELATORIO-PROJETO" + "-" + sistemaFacade.getExercicioCorrente().toString();
        } else {
            return "RELATORIO-ATIVIDADE" + "-" + sistemaFacade.getExercicioCorrente().toString();
        }
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(null, ":ID_EXERCICIO", null, OperacaoRelatorio.IGUAL, sistemaFacade.getExercicioCorrente().getId(), null, 0, false));
        parametros.add(new ParametrosRelatorios(null, ":DATA_OPERACAO", null, OperacaoRelatorio.IGUAL, DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()), null, 0, true));
        if (subAcaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" sa.id ", ":idSubAcao", null, OperacaoRelatorio.IGUAL, subAcaoPPA.getId(), null, 1, false));
        }
        if (acaoPrincipal != null) {
            parametros.add(new ParametrosRelatorios(" acp.id ", ":idAcaoPPA", null, OperacaoRelatorio.IGUAL, acaoPrincipal.getId(), null, 1, false));
        }
        if (funcao != null) {
            parametros.add(new ParametrosRelatorios(" f.id ", ":idFuncao", null, OperacaoRelatorio.IGUAL, funcao.getId(), null, 1, false));
        }
        if (subFuncao != null) {
            parametros.add(new ParametrosRelatorios(" sf.id ", ":idSubfuncao", null, OperacaoRelatorio.IGUAL, subFuncao.getId(), null, 1, false));
        }
        if (TipoRelatorio.PROJETO.equals(tipoRelatorio)) {
            parametros.add(new ParametrosRelatorios(" tac.codigo ", ":tipoAcao", null, OperacaoRelatorio.IGUAL, "1", null, 1, false));
        } else {
            parametros.add(new ParametrosRelatorios(" tac.codigo ", ":tipoAcao", null, OperacaoRelatorio.IGUAL, "2", null, 1, false));
        }
        montarParametrosUnidades(parametros);
        return parametros;
    }

    private void montarParametrosUnidades(List<ParametrosRelatorios> parametros) {
        List<Long> idsUnidades = new ArrayList<>();
        if (!unidades.isEmpty()) {
            for (HierarquiaOrganizacional unidade : unidades) {
                idsUnidades.add(unidade.getSubordinada().getId());
            }
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else {
            List<HierarquiaOrganizacional> undsUsuarios = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaFacade.getUsuarioCorrente(), sistemaFacade.getExercicioCorrente(), sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional unidade : undsUsuarios) {
                idsUnidades.add(unidade.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        }
    }

    public List<SubAcaoPPA> completarSubProjetoAtividade(String parte) {
        return subProjetoAtividadeFacade.buscarSubProjetoAtividadePorExercicio(parte.trim(), sistemaFacade.getExercicioCorrente());
    }

    public List<Funcao> completarFuncao(String parte) {
        return funcaoFacade.listaFiltrandoFuncao(parte.trim());
    }

    public List<SubFuncao> completarSubFuncao(String parte) {
        return subFuncaoFacade.listaFiltrandoSubFuncao(parte.trim());
    }

    public List<AcaoPrincipal> completarAcaoPrincipal(String parte) {
        return acaoPrincipalFacade.buscarAcaoPPAPorExercicio(parte.trim(), sistemaFacade.getExercicioCorrente());
    }

    private enum TipoRelatorio {
        PROJETO("Projeto"),
        ATIVIDADE("Atividade");
        private String descricao;

        TipoRelatorio(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public List<HierarquiaOrganizacional> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<HierarquiaOrganizacional> unidades) {
        this.unidades = unidades;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public SubFuncao getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(SubFuncao subFuncao) {
        this.subFuncao = subFuncao;
    }

    public SubAcaoPPA getSubAcaoPPA() {
        return subAcaoPPA;
    }

    public void setSubAcaoPPA(SubAcaoPPA subAcaoPPA) {
        this.subAcaoPPA = subAcaoPPA;
    }

    public AcaoPrincipal getAcaoPrincipal() {
        return acaoPrincipal;
    }

    public void setAcaoPrincipal(AcaoPrincipal acaoPrincipal) {
        this.acaoPrincipal = acaoPrincipal;
    }
}
