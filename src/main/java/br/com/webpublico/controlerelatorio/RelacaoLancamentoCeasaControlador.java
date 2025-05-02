package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.AbstractFiltroRelacaoLancamentoDebito;
import br.com.webpublico.entidadesauxiliares.FiltroRelacaoLancamentoCeasa;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.tributario.FiltroRelacaoLancamentoCeasaDTO;
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
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 28/04/15
 * Time: 10:29
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaRelacaoLancamentoCeasa", pattern = "/tributario/ceasa/relacao-lancamento-ceasa/", viewId = "/faces/tributario/ceasa/relatorios/relacaolancamentoceasa.xhtml")})
public class RelacaoLancamentoCeasaControlador {

    private FiltroRelacaoLancamentoCeasa filtroRelacaoLancamentoCeasa;
    @EJB
    private ExercicioFacade exercicioFacade;

    public FiltroRelacaoLancamentoCeasa getFiltroLancamento() {
        return filtroRelacaoLancamentoCeasa;
    }

    @URLAction(mappingId = "novaRelacaoLancamentoCeasa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        instanciarFiltroLancamento();
    }


    protected void instanciarFiltroLancamento() {
        filtroRelacaoLancamentoCeasa = new FiltroRelacaoLancamentoCeasa();
        Exercicio exercicioVigente = exercicioFacade.getExercicioCorrente();
        filtroRelacaoLancamentoCeasa.setExercicioDividaInicial(exercicioVigente.getAno());
        filtroRelacaoLancamentoCeasa.setExercicioDividaFinal(exercicioVigente.getAno());
    }

    public void gerarRelatorio(String tiporelatorioExtensao) {
        try {
            validarFiltros();
            RelatorioDTO dto = instanciarRelatorioDto(tiporelatorioExtensao);
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

        if (filtroRelacaoLancamentoCeasa.getExercicioDividaInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Inicial da Dívida deve ser informado.");
        }
        if (filtroRelacaoLancamentoCeasa.getExercicioDividaFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Final da Dívida deve ser informado.");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoCeasa.getDataLancamentoInicial(),
            filtroRelacaoLancamentoCeasa.getDataLancamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Lançamento Inicial deve ser menor ou igual a Data de Lançamento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoCeasa.getDataVencimentoInicial(),
            filtroRelacaoLancamentoCeasa.getDataVencimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Vencimento Inicial deve ser menor ou igual a Data de Vencimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoCeasa.getDataMovimentoInicial(),
            filtroRelacaoLancamentoCeasa.getDataMovimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Movimento Inicial deve ser menor ou igual a Data de Movimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoCeasa.getDataPagamentoInicial(),
            filtroRelacaoLancamentoCeasa.getDataPagamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Pagamento Inicial deve ser menor ou igual a Data de Pagamento Final");
        }
        ve.lancarException();
    }

    private RelatorioDTO instanciarRelatorioDto(String tiporelatorioExtensao) {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setTipoRelatorio(br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO.valueOf(tiporelatorioExtensao));
        dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
        dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Finanças");
        dto.setNomeRelatorio(montarNomeRelatorio());
        dto.setNomeParametroBrasao("BRASAO");
        preencherFiltroRendas(dto);
        dto.setApi("tributario/lancamento-ceasa/");
        return dto;
    }

    private String montarNomeRelatorio() {
        if (filtroRelacaoLancamentoCeasa.getTipoRelatorio().equals(AbstractFiltroRelacaoLancamentoDebito.TipoRelatorio.RESUMIDO)) {
            return "Relação de Lançamento Ceasa Resumido";
        }
        return "Relação de Lançamento Ceasa Detalhado";
    }

    private void preencherFiltroRendas(RelatorioDTO relatorioDTO) {
        FiltroRelacaoLancamentoCeasaDTO filtroDto = new FiltroRelacaoLancamentoCeasaDTO();
        filtroDto.setExercicioDividaInicial(filtroRelacaoLancamentoCeasa.getExercicioDividaInicial());
        filtroDto.setExercicioDividaFinal(filtroRelacaoLancamentoCeasa.getExercicioDividaFinal());
        filtroDto.setDataLancamentoInicial(filtroRelacaoLancamentoCeasa.getDataLancamentoInicial());
        filtroDto.setDataLancamentoFinal(filtroRelacaoLancamentoCeasa.getDataLancamentoFinal());
        filtroDto.setDataVencimentoInicial(filtroRelacaoLancamentoCeasa.getDataVencimentoInicial());
        filtroDto.setDataVencimentoFinal(filtroRelacaoLancamentoCeasa.getDataVencimentoFinal());
        filtroDto.setDataPagamentoInicial(filtroRelacaoLancamentoCeasa.getDataPagamentoInicial());
        filtroDto.setDataPagamentoFinal(filtroRelacaoLancamentoCeasa.getDataPagamentoFinal());
        filtroDto.setDataMovimentoInicial(filtroRelacaoLancamentoCeasa.getDataMovimentoInicial());
        filtroDto.setDataMovimentoFinal(filtroRelacaoLancamentoCeasa.getDataMovimentoFinal());
        filtroDto.setCmcInicial(filtroRelacaoLancamentoCeasa.getCmcInicial());
        filtroDto.setCmcFinal(filtroRelacaoLancamentoCeasa.getCmcFinal());
        filtroDto.setNumeroContratoInicial(filtroRelacaoLancamentoCeasa.getNumeroContratoInicial());
        filtroDto.setNumeroContratoFinal(filtroRelacaoLancamentoCeasa.getNumeroContratoFinal());
        filtroDto.setLocalizacaoInicial(filtroRelacaoLancamentoCeasa.getLocalizacaoInicial());
        filtroDto.setLocalizacaoFinal(filtroRelacaoLancamentoCeasa.getLocalizacaoFinal());
        filtroDto.setNumeroBoxInicial(filtroRelacaoLancamentoCeasa.getNumeroBoxInicial());
        filtroDto.setNumeroBoxFinal(filtroRelacaoLancamentoCeasa.getNumeroBoxFinal());
        filtroDto.setIdsContribuintes(Util.montarIdsList(filtroRelacaoLancamentoCeasa.getContribuintes()));
        filtroDto.setContribuintes(montarNomeContribuintes(filtroRelacaoLancamentoCeasa.getContribuintes()));
        filtroDto.setSituacoes(montarSituacoesParcelaWebreport());
        filtroDto.setTipoRelatorio(TipoRelatorioDTO.valueOf(filtroRelacaoLancamentoCeasa.getTipoRelatorio().name()));
        filtroDto.setSomenteTotalizador(filtroRelacaoLancamentoCeasa.getSomenteTotalizador());

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
        for (SituacaoParcela situacao : filtroRelacaoLancamentoCeasa.getSituacoes()) {
            list.add(SituacaoParcelaWebreportDTO.valueOf(situacao.name()));
        }
        SituacaoParcelaWebreportDTO[] situacoes = new SituacaoParcelaWebreportDTO[list.size()];
        return list.toArray(situacoes);
    }

    public List<SituacaoParcela> montarSituacoesParcela() {
        return SituacaoParcela.getValues();
    }

}
