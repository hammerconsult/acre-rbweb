package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.DetalhadoResumido;
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
import java.util.Date;
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
    @URLMapping(id = "relatorio-qdd-saldo-orc", pattern = "/relatorio/qdd-saldo-orcamentario-disponivel/", viewId = "/faces/financeiro/relatorio/relatorioqddsaldoorc.xhtml")
})
@ManagedBean
public class RelatorioQuadroDetalhamentoDespesaSaldo extends RelatorioQDDSuperControlador implements Serializable {

    @EJB
    private RelatorioQDDSuperFacade relatorioQDDSuperFacade;
    private ConverterAutoComplete converterSubProjetoAtividade;
    private ContaDeDestinacao contaDeDestinacao;

    public RelatorioQuadroDetalhamentoDespesaSaldo() {
    }

    @URLAction(mappingId = "relatorio-qdd-saldo-orc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        super.limparCampos();
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
            dto.adicionarParametro("TIPORELATORIO", detalhadoResumido.name());
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("detalharConta", detalharConta);
            dto.setApi("contabil/qdd-saldo-orcamentario-disponivel/");
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

    public void gerarRelatorio(FonteDespesaORC fonteDespesaORC, Date data) {
        try {
            this.detalhadoResumido = DetalhadoResumido.DETALHADO;
            this.detalharConta = Boolean.TRUE;
            this.dataReferencia = data;
            this.subAcaoPPA = fonteDespesaORC.getProvisaoPPAFonte().getProvisaoPPADespesa().getSubAcaoPPA();
            this.conta = fonteDespesaORC.getContaDeDespesa();
            this.fonteDeRecursos = fonteDespesaORC.getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos();
            listaUnidades = Lists.newArrayList();
            gerarRelatorio();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    @Override
    public String getNomeRelatorio() {
        return "QDD-SALDO-ORCAMENTARIO-DISPONIVEL " + DataUtil.getDataFormatada(dataReferencia, "ddMMyyyy");
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        adicionarParametrosGerais(parametros);
        filtrosParametrosUnidade(parametros);
        return parametros;
    }

    @Override
    public List<ParametrosRelatorios> adicionarParametrosGerais(List<ParametrosRelatorios> parametros) {
        super.adicionarParametrosGerais(parametros);
        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" cd.id ", ":idCDestinacao", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 2, false));
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

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }
}
