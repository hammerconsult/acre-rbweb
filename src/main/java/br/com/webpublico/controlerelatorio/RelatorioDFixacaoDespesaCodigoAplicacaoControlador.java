package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ExtensaoFonteRecursoFacade;
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
import java.util.List;

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-d-fixacao-despesa-codigo-aplicacao", pattern = "/relatorio/demonstrativo-fixacao-despesa-codigo-aplicacao/", viewId = "/faces/financeiro/relatorio/relatoriodfixacaodespesacodigoaplicacao.xhtml")
})
@ManagedBean
public class RelatorioDFixacaoDespesaCodigoAplicacaoControlador extends RelatorioQDDSuperControlador {

    @EJB
    private ExtensaoFonteRecursoFacade extensaoFonteRecursoFacade;
    private ExtensaoFonteRecurso extensaoFonteRecurso;
    private ContaDeDestinacao contaDeDestinacao;

    public RelatorioDFixacaoDespesaCodigoAplicacaoControlador() {
    }

    @URLAction(mappingId = "relatorio-d-fixacao-despesa-codigo-aplicacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        super.limparCamposGeral();
        contaDeDestinacao = null;
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("DATAREFERENCIA", getDataReferenciaFormatada());
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.adicionarParametro("TIPORELATORIO", detalhadoResumido.name());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/d-fixacao-despesa-codigo-aplicacao/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    @Override
    public String getNomeRelatorio() {
        return "DEMONSTRATIVO-FIXAÇÃO-DESPESA-CÓDIGO-APLICAÇÃO " + DataUtil.getDataFormatada(dataReferencia, "ddMMyyyy");
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        adicionarParametrosGerais(parametros);
        filtrosParametrosUnidade(parametros);
        adicionarParametros(parametros);
        return parametros;
    }

    private void adicionarParametros(List<ParametrosRelatorios> parametros) {
        if (programaPPA != null) {
            parametros.add(new ParametrosRelatorios(" prog.id ", ":idPrograma", null, OperacaoRelatorio.IGUAL, programaPPA.getId(), null, 1, false));
        }
        if (acaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" ac.id ", ":idAcao", null, OperacaoRelatorio.IGUAL, acaoPPA.getId(), null, 1, false));
        }
        if (tipoAcaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" tpa.id ", ":idTipoAcao", null, OperacaoRelatorio.IGUAL, tipoAcaoPPA.getId(), null, 1, false));
        }
        if (extensaoFonteRecurso != null) {
            parametros.add(new ParametrosRelatorios(" extfr.id ", ":idExtensao", null, OperacaoRelatorio.IGUAL, extensaoFonteRecurso.getId(), null, 1, false));
        }
        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" cd.id ", ":idContaDestinacao", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 1, false));
        }
    }

    public List<FonteDeRecursos> completarFonteDeRecursos(String parte) {
        return relatorioContabilSuperFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public List<SubAcaoPPA> completarSubProjetoAtividade(String parte) {
        return relatorioContabilSuperFacade.getSubProjetoAtividadeFacade().buscarSubProjetoAtividadePorExercicio(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public List<Funcao> completarFuncao(String parte) {
        return relatorioContabilSuperFacade.getFuncaoFacade().listaFiltrandoFuncao(parte.trim());
    }

    public List<SubFuncao> completarSubFuncao(String parte) {
        return relatorioContabilSuperFacade.getSubFuncaoFacade().listaFiltrandoSubFuncao(parte.trim());
    }

    public List<Conta> completarContaDespesa(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoContaDespesa(parte, getSistemaControlador().getExercicioCorrente());
    }

    public List<Conta> completarContasDeDestinacao(String filtro) {
        return relatorioContabilSuperFacade.getContaFacade().buscarContasDeDestinacaoDeRecursos(filtro, getSistemaFacade().getExercicioCorrente());
    }

    public List<AcaoPPA> completarProjetosAtividade(String filtro) {
        return relatorioContabilSuperFacade.getProjetoAtividadeFacade().buscarAcoesPPAPorExercicio(filtro, getExercicioCorrente());
    }

    public List<ProgramaPPA> completarProgramas(String filtro) {
        return relatorioContabilSuperFacade.getProgramaPPAFacade().buscarProgramasPorExercicio(filtro, getExercicioCorrente());
    }

    public List<TipoAcaoPPA> completarTiposAcoesPPA(String filtro) {
        return relatorioContabilSuperFacade.getTipoAcaoPPAFacade().listaFiltrando(filtro.trim(), "descricao");
    }

    public List<ExtensaoFonteRecurso> completarExtensoes(String filtro) {
        return extensaoFonteRecursoFacade.listaFiltrando(filtro.trim(), "codigo", "descricao");
    }

    public ExtensaoFonteRecurso getExtensaoFonteRecurso() {
        return extensaoFonteRecurso;
    }

    public void setExtensaoFonteRecurso(ExtensaoFonteRecurso extensaoFonteRecurso) {
        this.extensaoFonteRecurso = extensaoFonteRecurso;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }
}
