package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.FiltroQDDOCA;
import br.com.webpublico.entidades.contabil.FiltroQDDOCAFuncao;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoOCA;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.relatoriofacade.RelatorioQDDSuperFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 07/04/14
 * Time: 16:24
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-qdd-oca", pattern = "/relatorio/qdd-oca/", viewId = "/faces/financeiro/relatorio/relatorioqddoca.xhtml")
})
@ManagedBean
public class RelatorioQuadroDetalhamentoDespesaOCA extends RelatorioQDDSuperControlador implements Serializable {

    @EJB
    private RelatorioQDDSuperFacade relatorioQDDSuperFacade;
    private ConverterAutoComplete converterSubProjetoAtividade;
    private ContaDeDestinacao contaDeDestinacao;
    private TipoOCA filtroQddOcatipoOCA;
    private TipoOCA tipoOCA;
    private FiltroQDDOCAFuncao filtroQDDOCAFuncao;
    private List<FiltroQDDOCA> filtrosQddOca;

    public RelatorioQuadroDetalhamentoDespesaOCA() {
    }

    @URLAction(mappingId = "relatorio-qdd-oca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        super.limparCampos();
        contaDeDestinacao = null;
        filtroQDDOCAFuncao = null;
        filtroQddOcatipoOCA = null;
        buscarFiltrosQddOca();
    }

    private void novoFiltroQDDOCA(TipoOCA tipoOCA, Exercicio exercicio) {
        FiltroQDDOCA fQddOca = new FiltroQDDOCA();
        fQddOca.setTipoOCA(tipoOCA);
        fQddOca.setExercicio(exercicio);
        fQddOca = relatorioQDDSuperFacade.salvarFiltroQDDOCA(fQddOca);
        filtrosQddOca.add(fQddOca);
    }

    public void buscarFiltrosQddOca() {
        Exercicio exercicioDataReferencia = buscarExercicioPelaDataReferencia();
        filtrosQddOca = relatorioQDDSuperFacade.buscarFiltrosQddOcaPorExercicio(exercicioDataReferencia);
        if (filtrosQddOca == null || filtrosQddOca.isEmpty()) {
            filtrosQddOca = Lists.newArrayList();
            novoFiltroQDDOCA(TipoOCA.EXCLUSIVO, exercicioDataReferencia);
            novoFiltroQDDOCA(TipoOCA.NAO_EXCLUSIVO, exercicioDataReferencia);
        }
    }

    public void gerarRelatorio() {
        try {
            validarDataDeReferencia();
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("MUNICIPIO", abstractReport.montarNomeDoMunicipio());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.adicionarParametro("DATAREFERENCIA", getDataReferenciaFormatada());
            dto.adicionarParametro("ANO_EXERCICIO", buscarExercicioPelaDataDeReferencia().getAno().toString());
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("detalharConta", detalharConta);
            dto.setApi("contabil/qdd-oca/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    @Override
    public String getNomeRelatorio() {
        return "Quadro de Detalhamento das Despesas por Orçamento Criança e Adolescente - QDDOCA";
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        adicionarParametrosGerais(parametros);
        filtrosParametrosUnidade(parametros);
        return parametros;
    }

    @Override
    public List<ParametrosRelatorios> adicionarParametrosGerais(List<ParametrosRelatorios> parametros) {
        parametros.addAll(super.adicionarParametrosGerais(parametros));
        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" cd.id ", ":idCDestinacao", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 2, false));
        }
        if (tipoOCA != null) {
            parametros.add(new ParametrosRelatorios(" fQddOca.tipooca ", ":tipoOca", null, OperacaoRelatorio.IGUAL, tipoOCA.name(), null, 6, false));
        }
        return parametros;
    }

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataReferencia().getId(), null, 0, false));
    }

    public List<Conta> completarContasDeDestinacao(String filtro) {
        return relatorioQDDSuperFacade.getContaFacade().buscarContasDeDestinacaoDeRecursos(filtro, getSistemaControlador().getExercicioCorrente());
    }

    public List<FonteDeRecursos> completarFonteDeRecursos(String parte) {
        return relatorioQDDSuperFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public List<SubAcaoPPA> completarSubProjetoAtividade(String parte) {
        return relatorioQDDSuperFacade.getSubProjetoAtividadeFacade().buscarSubProjetoAtividadePorExercicio(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public List<Funcao> completarFuncao(String parte) {
        return relatorioQDDSuperFacade.getFuncaoFacade().listaFiltrandoFuncao(parte.trim());
    }

    public List<SubFuncao> completarSubFuncao(String parte) {
        return relatorioQDDSuperFacade.getSubFuncaoFacade().listaFiltrandoSubFuncao(parte.trim());
    }

    public List<Conta> completarContaDespesa(String parte) {
        return relatorioQDDSuperFacade.getContaFacade().listaFiltrandoContaDespesa(parte, getSistemaControlador().getExercicioCorrente());
    }

    public List<SelectItem> getTiposOcas() {
        return Util.getListSelectItem(TipoOCA.values());
    }

    public ConverterAutoComplete getConverterSubProjetoAtividade() {
        if (converterSubProjetoAtividade == null) {
            converterSubProjetoAtividade = new ConverterAutoComplete(SubAcaoPPA.class, relatorioQDDSuperFacade.getSubProjetoAtividadeFacade());
        }
        return converterSubProjetoAtividade;
    }

    public void validarFiltroContaDespesa() {
        if (conta == null) {
            detalharConta = true;
        }
    }

    public void novoFiltroQddOcaFuncao() {
        filtroQDDOCAFuncao = new FiltroQDDOCAFuncao();
    }

    public void limparFiltroQddOcaFuncao() {
        filtroQDDOCAFuncao = null;
        filtroQddOcatipoOCA = null;
    }

    public void adicionarFiltroQddOcaFuncao() {
        try {
            validarCamposFiltroQDDOCAFuncao();
            for (FiltroQDDOCA fQddOca : filtrosQddOca) {
                if (fQddOca.getTipoOCA().equals(filtroQddOcatipoOCA)) {
                    filtroQDDOCAFuncao.setFiltroQDDOCA(fQddOca);
                    filtroQDDOCAFuncao = relatorioQDDSuperFacade.salvarFiltroQDDOCAFuncao(filtroQDDOCAFuncao);
                    fQddOca.getFuncoes().add(filtroQDDOCAFuncao);
                }
            }
            limparFiltroQddOcaFuncao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerFiltroQddOcaFuncao(FiltroQDDOCAFuncao filtroQDDOCAFuncao) {
        for (FiltroQDDOCA fQddOca : filtrosQddOca) {
            fQddOca.getFuncoes().remove(filtroQDDOCAFuncao);
        }
        relatorioQDDSuperFacade.removerFiltroQDDOCAFuncao(filtroQDDOCAFuncao);
    }

    public void validarCamposFiltroQDDOCAFuncao() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroQddOcatipoOCA == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de O.C.A. deve ser Informado.");
        }
        if (filtroQDDOCAFuncao.getFuncao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Função deve ser Informado.");
        }
        if (filtroQDDOCAFuncao.getSubFuncao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo SubFunção deve ser Informado.");
        }
        ve.lancarException();
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public TipoOCA getTipoOCA() {
        return tipoOCA;
    }

    public void setTipoOCA(TipoOCA tipoOCA) {
        this.tipoOCA = tipoOCA;
    }

    public FiltroQDDOCAFuncao getFiltroQDDOCAFuncao() {
        return filtroQDDOCAFuncao;
    }

    public void setFiltroQDDOCAFuncao(FiltroQDDOCAFuncao filtroQDDOCAFuncao) {
        this.filtroQDDOCAFuncao = filtroQDDOCAFuncao;
    }

    public List<FiltroQDDOCA> getFiltrosQddOca() {
        return filtrosQddOca;
    }

    public void setFiltrosQddOca(List<FiltroQDDOCA> filtrosQddOca) {
        this.filtrosQddOca = filtrosQddOca;
    }

    public TipoOCA getFiltroQddOcatipoOCA() {
        return filtroQddOcatipoOCA;
    }

    public void setFiltroQddOcatipoOCA(TipoOCA filtroQddOcatipoOCA) {
        this.filtroQddOcatipoOCA = filtroQddOcatipoOCA;
    }
}
