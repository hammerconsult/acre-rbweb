package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.ProcessoJudicial;
import br.com.webpublico.entidadesauxiliares.AbstractFiltroRelacaoLancamentoDebito;
import br.com.webpublico.entidadesauxiliares.FiltroRelacaoLancamentoDebitosExercicio;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.tributario.*;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaRelacaoLancamentoDebitosExercicio", pattern = "/tributario/relatorio/relacao-lancamento-debitos-exercicio/",
        viewId = "/faces/tributario/relatorio/relacaolancamentodebitosexercicio.xhtml")})
public class RelacaoLancamentoDebitosExercicioControlador {
    private final Logger logger = LoggerFactory.getLogger(RelacaoLancamentoDebitosExercicioControlador.class);

    private FiltroRelacaoLancamentoDebitosExercicio filtroLancamento;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private DividaFacade dividaFacade;
    private List<SelectItem> siDividas;

    public FiltroRelacaoLancamentoDebitosExercicio getFiltroLancamento() {
        return filtroLancamento;
    }

    public List<SelectItem> getSiDividas() {
        return siDividas;
    }

    @URLAction(mappingId = "novaRelacaoLancamentoDebitosExercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        instanciarFiltroLancamento();
    }

    protected void instanciarFiltroLancamento() {
        filtroLancamento = new FiltroRelacaoLancamentoDebitosExercicio();
        Exercicio exercicioVigente = exercicioFacade.getExercicioCorrente();
        filtroLancamento.setExercicioDividaInicial(exercicioVigente.getAno());
        filtroLancamento.setExercicioDividaFinal(exercicioVigente.getAno());
        siDividas = montarSelectItemDividas();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
            dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Finanças");
            dto.setNomeRelatorio(montarNomeRelatorio());
            dto.setTipoRelatorio(br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("BRASAO");
            preencherFiltroDebitosExercicio(dto);
            dto.setApi("tributario/lancamento-debitos-exercicio/" +
                (br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO.XLS.equals(dto.getTipoRelatorio()) ? "excel/" : ""));
            ReportService.getInstance().gerarRelatorio(SistemaService.getInstance().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            logger.error("Erro ao gerar relatorio. (WebReportRelatorioExistenteException) ", e);
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatorio. ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();

        if (filtroLancamento.getExercicioDividaInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Inicial deve ser informado.");
        }
        if (filtroLancamento.getExercicioDividaFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Final deve ser informado.");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroLancamento.getDataLancamentoInicial(),
            filtroLancamento.getDataLancamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Lançamento Inicial deve ser menor ou igual a Data de Lançamento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroLancamento.getDataVencimentoInicial(),
            filtroLancamento.getDataVencimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Vencimento Inicial deve ser menor ou igual a Data de Vencimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroLancamento.getDataMovimentoInicial(),
            filtroLancamento.getDataMovimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Movimento Inicial deve ser menor ou igual a Data de Movimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroLancamento.getDataPagamentoInicial(),
            filtroLancamento.getDataPagamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Pagamento Inicial deve ser menor ou igual a Data de Pagamento Final");
        }
        ve.lancarException();
    }

    private void preencherFiltroDebitosExercicio(RelatorioDTO relatorioDTO) {
        FiltroRelacaoLancamentoDebitosExercicioDTO filtroDto = new FiltroRelacaoLancamentoDebitosExercicioDTO();
        filtroDto.setExercicioDividaInicial(filtroLancamento.getExercicioDividaInicial());
        filtroDto.setExercicioDividaFinal(filtroLancamento.getExercicioDividaFinal());
        filtroDto.setDataLancamentoInicial(filtroLancamento.getDataLancamentoInicial());
        filtroDto.setDataLancamentoFinal(filtroLancamento.getDataLancamentoFinal());
        filtroDto.setDataPagamentoInicial(filtroLancamento.getDataPagamentoInicial());
        filtroDto.setDataPagamentoFinal(filtroLancamento.getDataPagamentoFinal());
        filtroDto.setDataMovimentoInicial(filtroLancamento.getDataMovimentoInicial());
        filtroDto.setDataMovimentoFinal(filtroLancamento.getDataMovimentoFinal());
        filtroDto.setDataVencimentoInicial(filtroLancamento.getDataVencimentoInicial());
        filtroDto.setDataVencimentoFinal(filtroLancamento.getDataVencimentoFinal());
        filtroDto.setIdsDivida(filtroLancamento.inDividas());
        TipoCadastroTributario tipoCadastroTributario = filtroLancamento.getTipoCadastroTributario();
        if (tipoCadastroTributario != null) {
            filtroDto.setTipoCadastroTributario(tipoCadastroTributario.toWebreport());
        }
        filtroDto.setInscricaoInicial(filtroLancamento.getInscricaoInicial());
        filtroDto.setInscricaoFinal(filtroLancamento.getInscricaoFinal());
        filtroDto.setIdsContribuintes(Util.montarIdsList(filtroLancamento.getContribuintes()));
        filtroDto.setContribuintes(montarNomeContribuintes(filtroLancamento.getContribuintes()));
        filtroDto.setSituacoes(montarSituacoesParcelaWebreport());
        filtroDto.setTotalLancadoInicial(filtroLancamento.getTotalLancadoInicial());
        filtroDto.setTotalLancadoFinal(filtroLancamento.getTotalLancadoFinal());
        filtroDto.setTipoRelatorio(TipoRelatorioDTO.valueOf(filtroLancamento.getTipoRelatorio().name()));
        filtroDto.setSomenteTotalizador(filtroLancamento.getSomenteTotalizador());

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
        SituacaoParcela[] situacoesParcela = filtroLancamento.getSituacoes();
        for (SituacaoParcela situacao : situacoesParcela) {
            list.add(SituacaoParcelaWebreportDTO.valueOf(situacao.name()));
        }
        SituacaoParcelaWebreportDTO[] situacoes = new SituacaoParcelaWebreportDTO[list.size()];
        return list.toArray(situacoes);
    }

    protected String montarNomeRelatorio() {
        if (filtroLancamento.getTipoRelatorio().equals(AbstractFiltroRelacaoLancamentoDebito.TipoRelatorio.RESUMIDO)) {
            return "Relação de Lançamento de Débito do Exercício (Resumido)";
        }
        return "Relação de Lançamento de Débito do Exercício (Detalhado)";
    }

    public List<SelectItem> montarSelectItemDividas() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, "     "));
        List<Divida> dividas = dividaFacade.buscarDividasDoExercicio("");
        for (Divida divida : dividas) {
            toReturn.add(new SelectItem(divida, divida.getDescricao()));
        }
        return toReturn;
    }

    public List<SituacaoParcela> montarSituacoesParcela() {
        return SituacaoParcela.getValues();
    }

    public List<SelectItem> montarTiposCadastro() {
        return Util.getListSelectItem(TipoCadastroTributario.values());
    }

}
