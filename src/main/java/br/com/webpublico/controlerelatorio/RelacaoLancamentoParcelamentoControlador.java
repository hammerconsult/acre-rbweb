package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ParamParcelamento;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.AbstractFiltroRelacaoLancamentoDebito;
import br.com.webpublico.entidadesauxiliares.FiltroRelacaoLancamentoParcelamento;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.ParametroParcelamentoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.tributario.FiltroRelacaoLancamentoParcelamentoDTO;
import br.com.webpublico.webreportdto.dto.tributario.SituacaoParcelaWebreportDTO;
import br.com.webpublico.webreportdto.dto.tributario.TipoRelatorioDTO;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 03/05/17
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaRelacaoLancamentoParcelamento", pattern = "/tributario/parcelamento/relacao-lancamento-parcelamento/",
        viewId = "/faces/tributario/contacorrente/parcelamento/relatorio/relacaolancamentoparcelamento.xhtml")})
public class RelacaoLancamentoParcelamentoControlador {

    private FiltroRelacaoLancamentoParcelamento filtroRelacaoLancamentoParcelamento;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ParametroParcelamentoFacade parametroParcelamentoFacade;
    @EJB
    private DividaFacade dividaFacade;

    public FiltroRelacaoLancamentoParcelamento getFiltroLancamento() {
        return filtroRelacaoLancamentoParcelamento;
    }

    @URLAction(mappingId = "novaRelacaoLancamentoParcelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        instanciarFiltroLancamento();
    }

    protected void instanciarFiltroLancamento() {
        filtroRelacaoLancamentoParcelamento = new FiltroRelacaoLancamentoParcelamento();
        Exercicio exercicioVigente = exercicioFacade.getExercicioCorrente();
        filtroRelacaoLancamentoParcelamento.setExercicioDividaInicial(exercicioVigente.getAno());
        filtroRelacaoLancamentoParcelamento.setExercicioDividaFinal(exercicioVigente.getAno());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltros();
            RelatorioDTO dto = instanciarRelatorioDto(tipoRelatorioExtensao);
            ReportService.getInstance().gerarRelatorio(SistemaService.getInstance().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();

        if (filtroRelacaoLancamentoParcelamento.getExercicioDividaInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Inicial da Dívida deve ser informado.");
        }
        if (filtroRelacaoLancamentoParcelamento.getExercicioDividaFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Final da Dívida deve ser informado.");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoParcelamento.getDataLancamentoInicial(),
            filtroRelacaoLancamentoParcelamento.getDataLancamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Lançamento Inicial deve ser menor ou igual a Data de Lançamento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoParcelamento.getDataVencimentoInicial(),
            filtroRelacaoLancamentoParcelamento.getDataVencimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Vencimento Inicial deve ser menor ou igual a Data de Vencimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoParcelamento.getDataMovimentoInicial(),
            filtroRelacaoLancamentoParcelamento.getDataMovimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Movimento Inicial deve ser menor ou igual a Data de Movimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoParcelamento.getDataPagamentoInicial(),
            filtroRelacaoLancamentoParcelamento.getDataPagamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Pagamento Inicial deve ser menor ou igual a Data de Pagamento Final");
        }
        ve.lancarException();
    }

    private RelatorioDTO instanciarRelatorioDto(String tipoRelatorioExtensao) {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setTipoRelatorio(br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
        dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
        dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Finanças");
        dto.setNomeRelatorio(montarNomeRelatorio());
        dto.setNomeParametroBrasao("BRASAO");
        preencherFiltroParcelamento(dto);
        dto.setApi("tributario/lancamento-parcelamento/");
        return dto;
    }

    protected String montarNomeRelatorio() {
        if (filtroRelacaoLancamentoParcelamento.getTipoRelatorio().equals(AbstractFiltroRelacaoLancamentoDebito.TipoRelatorio.RESUMIDO)) {
            return "Relação de Lançamento de Parcelamento de Dívida Ativa Resumido";
        }
        return "Relação de Lançamento de Parcelamento de Dívida Ativa Detalhado";
    }

    private void preencherFiltroParcelamento(RelatorioDTO relatorioDTO) {
        FiltroRelacaoLancamentoParcelamentoDTO filtroDto = new FiltroRelacaoLancamentoParcelamentoDTO();
        filtroDto.setExercicioDividaInicial(filtroRelacaoLancamentoParcelamento.getExercicioDividaInicial());
        filtroDto.setExercicioDividaFinal(filtroRelacaoLancamentoParcelamento.getExercicioDividaFinal());
        filtroDto.setExercicioDividaAtivaInicial(filtroRelacaoLancamentoParcelamento.getExercicioDividaAtivaInicial());
        filtroDto.setExercicioDividaAtivaFinal(filtroRelacaoLancamentoParcelamento.getExercicioDividaAtivaFinal());
        filtroDto.setDataLancamentoInicial(filtroRelacaoLancamentoParcelamento.getDataLancamentoInicial());
        filtroDto.setDataLancamentoFinal(filtroRelacaoLancamentoParcelamento.getDataLancamentoFinal());
        filtroDto.setDataVencimentoInicial(filtroRelacaoLancamentoParcelamento.getDataVencimentoInicial());
        filtroDto.setDataVencimentoFinal(filtroRelacaoLancamentoParcelamento.getDataVencimentoFinal());
        filtroDto.setDataPagamentoInicial(filtroRelacaoLancamentoParcelamento.getDataPagamentoInicial());
        filtroDto.setDataPagamentoFinal(filtroRelacaoLancamentoParcelamento.getDataPagamentoFinal());
        filtroDto.setDataMovimentoInicial(filtroRelacaoLancamentoParcelamento.getDataMovimentoInicial());
        filtroDto.setDataMovimentoFinal(filtroRelacaoLancamentoParcelamento.getDataMovimentoFinal());
        filtroDto.setIdsDivida(filtroRelacaoLancamentoParcelamento.inDividas());
        filtroDto.setNumeroParcelamentoInicial(filtroRelacaoLancamentoParcelamento.getNumeroParcelamentoInicial());
        filtroDto.setNumeroParcelamentoFinal(filtroRelacaoLancamentoParcelamento.getNumeroParcelamentoFinal());
        filtroDto.setExercicioCertidaoInicial(filtroRelacaoLancamentoParcelamento.getExercicioCertidaoInicial());
        filtroDto.setExercicioCertidaoFinal(filtroRelacaoLancamentoParcelamento.getExercicioCertidaoFinal());
        filtroDto.setNumeroCertidaoInicial(filtroRelacaoLancamentoParcelamento.getNumeroCertidaoInicial());
        filtroDto.setNumeroCertidaoFinal(filtroRelacaoLancamentoParcelamento.getNumeroCertidaoFinal());
        filtroDto.setAjuizado(filtroRelacaoLancamentoParcelamento.getAjuizado());
        TipoCadastroTributario tipoCadastroTributario = filtroRelacaoLancamentoParcelamento.getTipoCadastroTributario();
        if (tipoCadastroTributario != null) {
            filtroDto.setTipoCadastroTributarioDTO(tipoCadastroTributario.toWebreport());
        }
        filtroDto.setIdsContribuintes(Util.montarIdsList(filtroRelacaoLancamentoParcelamento.getContribuintes()));
        filtroDto.setContribuintes(montarNomeContribuintes(filtroRelacaoLancamentoParcelamento.getContribuintes()));
        filtroDto.setParamParcelamento(filtroRelacaoLancamentoParcelamento.inParametros());
        filtroDto.setParametrosParcelamento(filtroRelacaoLancamentoParcelamento.filtroParametros());
        filtroDto.setSituacoes(montarSituacoesParcelaWebreport());
        filtroDto.setTotalLancadoInicial(filtroRelacaoLancamentoParcelamento.getTotalLancadoInicial());
        filtroDto.setTotalLancadoFinal(filtroRelacaoLancamentoParcelamento.getTotalLancadoFinal());
        filtroDto.setTipoRelatorio(TipoRelatorioDTO.valueOf(filtroRelacaoLancamentoParcelamento.getTipoRelatorio().name()));
        filtroDto.setSomenteTotalizador(filtroRelacaoLancamentoParcelamento.getSomenteTotalizador());

        relatorioDTO.adicionarParametro("filtroDto", filtroDto);
    }

    private String montarNomeContribuintes(List<Pessoa> contribuintes) {
        List<String> nomes = Lists.newArrayList();
        String nomesContribuintes = "";
        if (contribuintes != null) {
            for (Pessoa contribuinte : contribuintes) {
                nomes.add(contribuinte.getNomeCpfCnpj());
            }
            nomesContribuintes = Joiner.on(", ").join(nomes);
        }
        return nomesContribuintes;
    }

    private SituacaoParcelaWebreportDTO[] montarSituacoesParcelaWebreport() {
        List<SituacaoParcelaWebreportDTO> list = Lists.newArrayList();
        for (SituacaoParcela situacao : filtroRelacaoLancamentoParcelamento.getSituacoes()) {
            list.add(SituacaoParcelaWebreportDTO.valueOf(situacao.name()));
        }
        SituacaoParcelaWebreportDTO[] situacoes = new SituacaoParcelaWebreportDTO[list.size()];
        return list.toArray(situacoes);
    }

    public List<SelectItem> montarParcelamentoDividaAtiva() {
        List<SelectItem> parcelamentoDividaAtiva = Lists.newArrayList();
        parcelamentoDividaAtiva.add(new SelectItem(null, "     "));
        for (Divida divida : dividaFacade.buscarDividasDeParcelamentoDeDividaAtivaOrdenadoPorDescricao()) {
            parcelamentoDividaAtiva.add(new SelectItem(divida, divida.getDescricao()));
        }
        return parcelamentoDividaAtiva;
    }

    public List<SituacaoParcela> montarSituacoesParcela() {
        return SituacaoParcela.getValues();
    }

    public List<SelectItem> montarTiposCadastro() {
        return Util.getListSelectItem(TipoCadastroTributario.values());
    }

    public List<Divida> completaDividas(String parte) {
        return dividaFacade.listaDividasAtivas(parte.trim(), null);
    }

    public List<SelectItem> getParametros() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        if (filtroRelacaoLancamentoParcelamento.getTipoCadastroTributario() != null) {
            for (ParamParcelamento param : parametroParcelamentoFacade.listaVigentesPorTipoCadastro(
                filtroRelacaoLancamentoParcelamento.getTipoCadastroTributario())) {
                retorno.add(new SelectItem(param, param.getDescricao()));
            }
        }
        return Util.ordenaSelectItem(retorno);
    }
}
