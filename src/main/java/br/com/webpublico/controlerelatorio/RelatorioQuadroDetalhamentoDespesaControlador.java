package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.relatoriofacade.RelatorioQuadroDetalhamentoDespesaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
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
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 26/11/13
 * Time: 08:33
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-quadro-detalhamento-despesa", pattern = "/relatorio/qdd/quadro-detalhamento-despesa/", viewId = "/faces/financeiro/relatorio/relatorioqddexecucao.xhtml")
})
@ManagedBean
public class RelatorioQuadroDetalhamentoDespesaControlador implements Serializable {

    @EJB
    private RelatorioQuadroDetalhamentoDespesaFacade relatorioQuadroDetalhamentoDespesaFacade;
    private Conta conta;
    private AcaoPPA acaoPPA;
    private FonteDeRecursos fonteDeRecursos;
    private Funcao funcao;
    private SubFuncao subFuncao;
    private List<HierarquiaOrganizacional> listaUnidades;
    private ConverterAutoComplete converterAcaoPPA;
    private ConverterAutoComplete converterConta;
    private ConverterAutoComplete converterFuncao;
    private ConverterAutoComplete converterSubFuncao;
    private ConverterAutoComplete converterFonteDeRecurso;
    @Enumerated(EnumType.STRING)
    private Ordenacao ordenacao;

    @URLAction(mappingId = "relatorio-quadro-detalhamento-despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        listaUnidades = new ArrayList<>();
        conta = null;
        acaoPPA = null;
        fonteDeRecursos = null;
        funcao = null;
        subFuncao = null;
        ordenacao = Ordenacao.DESCRICAO;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.setNomeParametroBrasao("IMAGEM");
            dto.setNomeRelatorio(getNomeRelatorioAoImprimir());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("NOMERELATORIO", dto.getNomeRelatorio());
            dto.adicionarParametro("USER", relatorioQuadroDetalhamentoDespesaFacade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.adicionarParametro("ORDENACAO", ordenacao.name());
            dto.adicionarParametro("EXERCICIO", relatorioQuadroDetalhamentoDespesaFacade.getSistemaFacade().getExercicioCorrente().getAno().toString());
            dto.setApi("contabil/qdd-execucao/");
            ReportService.getInstance().gerarRelatorio(relatorioQuadroDetalhamentoDespesaFacade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametrosUnidades(parametros);
        parametrosGerais(parametros);
        return parametros;
    }

    private String getNomeRelatorioAoImprimir() {
        return "QDD-EXECUÇÃO";
    }

    private void parametrosGerais(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":DATA", null, null, DataUtil.getDataFormatada(relatorioQuadroDetalhamentoDespesaFacade.getSistemaFacade().getDataOperacao()), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":EXERCICIO_ID", null, null, relatorioQuadroDetalhamentoDespesaFacade.getSistemaFacade().getExercicioCorrente().getId(), null, 0, false));

        if (this.acaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" ac.id ", ":idAcao", null, OperacaoRelatorio.IGUAL, acaoPPA.getId(), null, 1, false));
        }
        if (this.conta != null) {
            parametros.add(new ParametrosRelatorios(" c.id ", ":idConta", null, OperacaoRelatorio.IGUAL, conta.getId(), null, 1, false));
        }
        if (funcao != null) {
            parametros.add(new ParametrosRelatorios(" func.id ", ":idFuncao", null, OperacaoRelatorio.IGUAL, funcao.getId(), null, 1, false));
        }
        if (subFuncao != null) {
            parametros.add(new ParametrosRelatorios(" sub.id ", ":idSubacao", null, OperacaoRelatorio.IGUAL, subFuncao.getId(), null, 1, false));
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fte.id ", ":idFonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 2, false));
        }
    }

    private void parametrosUnidades(List<ParametrosRelatorios> parametros) {
        List<Long> idsUnidades = Lists.newArrayList();
        if (!listaUnidades.isEmpty()) {
            for (HierarquiaOrganizacional hierarquia : listaUnidades) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
            }
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":unds", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else {
            List<HierarquiaOrganizacional> unidadesDoUsuario = relatorioQuadroDetalhamentoDespesaFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", relatorioQuadroDetalhamentoDespesaFacade.getSistemaFacade().getUsuarioCorrente(), relatorioQuadroDetalhamentoDespesaFacade.getSistemaFacade().getExercicioCorrente(), relatorioQuadroDetalhamentoDespesaFacade.getSistemaFacade().getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquia : unidadesDoUsuario) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":unds", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        }
    }

    public List<SelectItem> getListaOrdenacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Ordenacao od : Ordenacao.values()) {
            toReturn.add(new SelectItem(od, od.getDescricao()));
        }
        return toReturn;
    }

    public List<AcaoPPA> completarProjetoAtividade(String parte) {
        return relatorioQuadroDetalhamentoDespesaFacade.getAcaoFacade().buscarAcoesPPAPorExercicio(parte.trim(), relatorioQuadroDetalhamentoDespesaFacade.getSistemaFacade().getExercicioCorrente());
    }

    public List<Conta> completarContaDespesa(String parte) {
        return relatorioQuadroDetalhamentoDespesaFacade.getContaFacade().listaFiltrandoDespesaCriteria(parte, relatorioQuadroDetalhamentoDespesaFacade.getSistemaFacade().getExercicioCorrente());
    }

    public List<FonteDeRecursos> completarFonteDeRecursos(String parte) {
        return relatorioQuadroDetalhamentoDespesaFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), relatorioQuadroDetalhamentoDespesaFacade.getSistemaFacade().getExercicioCorrente());
    }

    public List<Funcao> completarFuncao(String parte) {
        return relatorioQuadroDetalhamentoDespesaFacade.getFuncaoFacade().listaFiltrandoFuncao(parte.trim());
    }

    public List<SubFuncao> completarSubFuncao(String parte) {
        return relatorioQuadroDetalhamentoDespesaFacade.getSubFuncaoFacade().listaFiltrandoSubFuncao(parte.trim());
    }

    public ConverterAutoComplete getConverterFonteDeRecurso() {
        if (converterFonteDeRecurso == null) {
            converterFonteDeRecurso = new ConverterAutoComplete(FonteDeRecursos.class, relatorioQuadroDetalhamentoDespesaFacade.getFonteDeRecursosFacade());
        }
        return converterFonteDeRecurso;
    }

    public ConverterAutoComplete getConverterSubFuncao() {
        if (converterSubFuncao == null) {
            converterSubFuncao = new ConverterAutoComplete(SubFuncao.class, relatorioQuadroDetalhamentoDespesaFacade.getSubFuncaoFacade());
        }
        return converterSubFuncao;
    }

    public ConverterAutoComplete getConverterFuncao() {
        if (converterFuncao == null) {
            converterFuncao = new ConverterAutoComplete(Funcao.class, relatorioQuadroDetalhamentoDespesaFacade.getFuncaoFacade());
        }
        return converterFuncao;
    }

    public ConverterAutoComplete getConverterAcaoPPA() {
        if (converterAcaoPPA == null) {
            converterAcaoPPA = new ConverterAutoComplete(AcaoPPA.class, relatorioQuadroDetalhamentoDespesaFacade.getAcaoFacade());
        }
        return converterAcaoPPA;
    }

    public ConverterAutoComplete getConverterConta() {
        if (converterConta == null) {
            converterConta = new ConverterAutoComplete(ContaDespesa.class, relatorioQuadroDetalhamentoDespesaFacade.getContaFacade());
        }
        return converterConta;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public AcaoPPA getAcaoPPA() {
        return acaoPPA;
    }

    public void setAcaoPPA(AcaoPPA acaoPPA) {
        this.acaoPPA = acaoPPA;
    }

    public SubFuncao getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(SubFuncao subFuncao) {
        this.subFuncao = subFuncao;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public Ordenacao getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(Ordenacao ordenacao) {
        this.ordenacao = ordenacao;
    }

    private enum Ordenacao {

        DESCRICAO("Por Apliação Programada"),
        CODIGO("Por Código Reduzido");
        private String descricao;

        private Ordenacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }
}
