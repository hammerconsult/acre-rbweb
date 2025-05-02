package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relacao-pagamentos-realizados-no-periodo", pattern = "/relatorio/pagamentos-realizados-no-periodo/", viewId = "/faces/financeiro/relatorio/relacao-pagamentos-realizados-no-periodo.xhtml")})
@ManagedBean
public class RelacaoPagamentosRealizadosNoPeriodoControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private PessoaFacade pessoaFacade;

    public RelacaoPagamentosRealizadosNoPeriodoControlador() {
    }

    @URLAction(mappingId = "relacao-pagamentos-realizados-no-periodo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        super.limparCamposGeral();
        apresentacao = ApresentacaoRelatorio.CONSOLIDADO;
    }

    public String getCaminhoRelatorio() {
        return "/relatorio/pagamentos-realizados-no-periodo/";
    }

    public void gerarRelatorio(String  tipoRelatorioExtensao) {
        try {
            validarDatasSemVerificarExercicioLogado();

            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("MODULO", "Contábil");
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("FILTRO", filtros);
            dto.adicionarParametro("apresentacao", apresentacao.getToDto());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/relacao-pagamentos-realizados-no-periodo/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e){
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        filtros = " Período: " + getDataInicialFormatada() + " a " + getDataFinalFormatada() + " -";
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 0, true));
        filtrosParametrosUnidade(parametros);
        filtrosParametrosGerais(parametros);
        return parametros;
    }

    public void filtrosParametrosGerais(List<ParametrosRelatorios> parametros) {
        if (pessoa != null) {
            parametros.add(new ParametrosRelatorios(" P.ID ", ":fornecedorId", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtros += " Pessoa: " + pessoa + " -";
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fonte.id ", ":fonteCodigo", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtros += " Fonte de Recursos: " + fonteDeRecursos.getCodigo() + " -";
        }
        if (mes != null) {
            parametros.add(new ParametrosRelatorios(" to_number(to_char(l.DATALIQUIDACAO, 'MM')) ", ":competencia", null, OperacaoRelatorio.IGUAL, mes.getNumeroMes(), null, 1, false));
            filtros += " Competência: " + mes.getDescricao() + " -";
        }
        if (apresentacao.isApresentacaoUnidadeGestora()) {
            adicionarExercicio(parametros);
        }
        atualizaFiltrosGerais();
    }

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataFinal().getId(), null, 0, false));
    }

    public List<SelectItem> getApresentacoes() {
        return Util.getListSelectItemSemCampoVazio(ApresentacaoRelatorio.values(), false);
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (Mes mes : Mes.values()) {
            if (!Mes.EXERCICIO.equals(mes)) {
                retorno.add(new SelectItem(mes, mes.getDescricao()));
            }
        }
        return retorno;
    }


    public List<Pessoa> completarPessoas(String filtro) {
        return pessoaFacade.listaTodasPessoas(filtro.trim());
    }

    @Override
    public String getNomeRelatorio() {
        return "RELACAO-PAGAMENTOS-REALIZADOS-NO-PERIODO";
    }
}
