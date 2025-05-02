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

/**
 * Criado por Mateus
 * Data: 09/03/2017.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-dd-extensao-fonte-recursos", pattern = "/relatorio/dd-extensao-fonte-recursos/", viewId = "/faces/financeiro/relatorio/relatorioddextensaofonterecursos.xhtml")
})
@ManagedBean
public class RelatorioDDExtensaoFonteRecursoControlador extends RelatorioQDDSuperControlador {

    @EJB
    private ExtensaoFonteRecursoFacade extensaoFonteRecursoFacade;
    private ExtensaoFonteRecurso extensaoFonteRecurso;

    public RelatorioDDExtensaoFonteRecursoControlador() {
    }

    @URLAction(mappingId = "relatorio-dd-extensao-fonte-recursos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaFacade().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaFacade().getExercicioCorrente().getId());
            dto.adicionarParametro("DATAREFERENCIA", getDataReferenciaFormatada());
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.adicionarParametro("TIPORELATORIO", detalhadoResumido.name());
            dto.adicionarParametro("hasLoaEfetivadaNoExercicio", relatorioContabilSuperFacade.getLoaFacade().hasLoaEfetivadaNoExercicio(getSistemaFacade().getExercicioCorrente()));
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/dd-extensao-fonte-recursos/");
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
        return "DD-EXTENSAO-FONTE-RECURSOS " + DataUtil.getDataFormatada(dataReferencia, "ddMMyyyy");
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        adicionarParametrosGerais(parametros);
        filtrosParametrosUnidade(parametros);
        adicionarParametros(parametros);
        return parametros;
    }

    public List<ParametrosRelatorios> adicionarParametros(List<ParametrosRelatorios> parametros) {
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
        return parametros;
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
}
