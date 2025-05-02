package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.relatoriofacade.RelatorioQDDSuperFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
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
import java.util.List;

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-qdd-saldo-programa-acao", pattern = "/relatorio/qdd-saldo-orcamentario-disponivel-programa-acao/", viewId = "/faces/financeiro/relatorio/relatorioqddsaldoprogramaacao.xhtml")
})
@ManagedBean
public class RelatorioQuadroDetalhamentoDespesaProgramaAcaoControlador extends RelatorioQDDSuperControlador implements Serializable {

    @EJB
    private RelatorioQDDSuperFacade relatorioQDDSuperFacade;
    private ConverterAutoComplete converterSubProjetoAtividade;
    private AcaoPrincipal acaoPrincipal;
    private ProgramaPPA programaPPAInicial;
    private ProgramaPPA programaPPAFinal;

    public RelatorioQuadroDetalhamentoDespesaProgramaAcaoControlador() {
    }

    @URLAction(mappingId = "relatorio-qdd-saldo-programa-acao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        super.limparCampos();
    }

    public void gerarRelatorio() {
        try {
            validarDataDeReferencia();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getNomeUsuarioLogado(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("DATAREFERENCIA", getDataReferenciaFormatada());
            dto.adicionarParametro("ANO_EXERCICIO", buscarExercicioPelaDataDeReferencia().getAno().toString());
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.adicionarParametro("TIPORELATORIO", detalhadoResumido.name());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("detalharConta", detalharConta);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/quadro-detalhamento-despesa-programa-acao/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    @Override
    public String getNomeRelatorio() {
        return "QDD-SALDO-ORCAMENTARIO-DISPONIVEL-POR-PROGRAMA-E-ACAO " + DataUtil.getDataFormatada(dataReferencia, "ddMMyyyy");
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
        if (acaoPrincipal != null) {
            parametros.add(new ParametrosRelatorios( " ap.id ", ":IdAcaoPrincipal", null , OperacaoRelatorio.IGUAL, acaoPrincipal.getId(), null, 1, false));
        }
        if (programaPPAInicial != null && programaPPAFinal != null) {
            parametros.add(new ParametrosRelatorios( " to_number(prog.codigo) ", ":programaInicial", ":programaFinal", OperacaoRelatorio.BETWEEN, programaPPAInicial.getCodigo(), programaPPAFinal.getCodigo(), 1, false));
        }
        if (programaPPAInicial != null && programaPPAFinal == null) {
            parametros.add(new ParametrosRelatorios(" to_number(prog.codigo) ", ":programaInicial", null, OperacaoRelatorio.MAIOR_IGUAL, programaPPAInicial.getCodigo(), null, 1, false));
        }
        if (programaPPAInicial == null && programaPPAFinal != null) {
            parametros.add(new ParametrosRelatorios( " to_number(prog.codigo) ", ":programaFinal", null , OperacaoRelatorio.MENOR_IGUAL, programaPPAFinal.getCodigo(), null, 1, false));
        }
        return parametros;
    }

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataReferencia().getId(), null, 0, false));
    }

    public List<AcaoPrincipal> completarAcoesPrincipais(String parte) {
        return relatorioQDDSuperFacade.getAcaoPrincipalFacade().buscarAcoesPrincipais(parte);
    }

    public List<ProgramaPPA> completarProgramasPPA(String parte) {
        return relatorioQDDSuperFacade.getProgramaPPAFacade().buscarProgramasPpa(parte);
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String parte) {
        return relatorioQDDSuperFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public List<SubAcaoPPA> completarSubProjetosAtividades(String parte) {
        return relatorioQDDSuperFacade.getSubProjetoAtividadeFacade().buscarSubProjetoAtividadePorExercicio(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public List<Funcao> completarFuncoes(String parte) {
        return relatorioQDDSuperFacade.getFuncaoFacade().listaFiltrandoFuncao(parte.trim());
    }

    public List<SubFuncao> completarSubFuncoes(String parte) {
        return relatorioQDDSuperFacade.getSubFuncaoFacade().listaFiltrandoSubFuncao(parte.trim());
    }

    public List<Conta> completarContasDespesas(String parte) {
        return relatorioQDDSuperFacade.getContaFacade().listaFiltrandoContaDespesa(parte, getSistemaControlador().getExercicioCorrente());
    }

    public ConverterAutoComplete getConverterSubProjetoAtividade() {
        if (converterSubProjetoAtividade == null) {
            converterSubProjetoAtividade = new ConverterAutoComplete(SubAcaoPPA.class, relatorioQDDSuperFacade.getSubProjetoAtividadeFacade());
        }
        return converterSubProjetoAtividade;
    }

    public void atualizarDetalharConta() {
        if (conta == null) {
            detalharConta = true;
        }
    }

    public AcaoPrincipal getAcaoPrincipal() {
        return acaoPrincipal;
    }

    public void setAcaoPrincipal(AcaoPrincipal acaoPrincipal) {
        this.acaoPrincipal = acaoPrincipal;
    }

    public ProgramaPPA getProgramaPPAInicial() {
        return programaPPAInicial;
    }

    public void setProgramaPPAInicial(ProgramaPPA programaPPAInicial) {
        this.programaPPAInicial = programaPPAInicial;
    }

    public ProgramaPPA getProgramaPPAFinal() {
        return programaPPAFinal;
    }

    public void setProgramaPPAFinal(ProgramaPPA programaPPAFinal) {
        this.programaPPAFinal = programaPPAFinal;
    }

}
