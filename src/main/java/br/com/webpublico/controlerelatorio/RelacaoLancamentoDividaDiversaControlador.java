package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.TipoDividaDiversa;
import br.com.webpublico.entidadesauxiliares.AbstractFiltroRelacaoLancamentoDebito;
import br.com.webpublico.entidadesauxiliares.FiltroRelacaoLancamentoDividaDiversa;
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
import br.com.webpublico.webreportdto.dto.tributario.FiltroRelacaoLancamentoDividaDiversasDTO;
import br.com.webpublico.webreportdto.dto.tributario.SituacaoParcelaWebreportDTO;
import br.com.webpublico.webreportdto.dto.tributario.TipoRelatorioDTO;
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

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 05/05/17
 * Time: 09:00
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaRelacaoLancamentoDividaDiversa", pattern = "/tributario/dividas-diversas/relacao-lancamento-dividas-diversas/",
        viewId = "/faces/tributario/taxasdividasdiversas/relatorios/relacaolancamentodividadiversa.xhtml")})
public class RelacaoLancamentoDividaDiversaControlador {

    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private DividaFacade dividaFacade;
    private List<SelectItem> dividasDividasDiversas;

    private FiltroRelacaoLancamentoDividaDiversa filtroRelacaoLancamentoDividaDiversa;

    public FiltroRelacaoLancamentoDividaDiversa getFiltroLancamento() {
        return filtroRelacaoLancamentoDividaDiversa;
    }

    @URLAction(mappingId = "novaRelacaoLancamentoDividaDiversa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        instanciarFiltroLancamento();
    }

    private void instanciarFiltroLancamento() {
        filtroRelacaoLancamentoDividaDiversa = new FiltroRelacaoLancamentoDividaDiversa();
        Exercicio exercicioVigente = exercicioFacade.getExercicioCorrente();
        filtroRelacaoLancamentoDividaDiversa.setExercicioDividaInicial(exercicioVigente.getAno());
        filtroRelacaoLancamentoDividaDiversa.setExercicioDividaFinal(exercicioVigente.getAno());
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

        if (filtroRelacaoLancamentoDividaDiversa.getExercicioDividaInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Inicial da Dívida deve ser informado.");
        }
        if (filtroRelacaoLancamentoDividaDiversa.getExercicioDividaFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Final da Dívida deve ser informado");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoDividaDiversa.getDataLancamentoInicial(),
            filtroRelacaoLancamentoDividaDiversa.getDataLancamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Lançamento Inicial deve ser menor ou igual a Data de Lançamento Final");
            ;
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoDividaDiversa.getDataVencimentoInicial(),
            filtroRelacaoLancamentoDividaDiversa.getDataVencimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Vencimento Inicial deve ser menor ou igual a Data de Vencimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoDividaDiversa.getDataMovimentoInicial(),
            filtroRelacaoLancamentoDividaDiversa.getDataMovimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Movimento Inicial deve ser menor ou igual a Data de Movimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoDividaDiversa.getDataPagamentoInicial(),
            filtroRelacaoLancamentoDividaDiversa.getDataPagamentoFinal())) {
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
        preencherFiltroDividasDiversas(dto);
        dto.setApi("tributario/lancamento-dividas-diversas/");
        return dto;
    }

    public List<Divida> completarDividas(String parte) {
        return dividaFacade.listaDividasAtivas(parte.trim(), null);
    }

    private String montarNomeRelatorio() {
        if (filtroRelacaoLancamentoDividaDiversa.getTipoRelatorio().equals(AbstractFiltroRelacaoLancamentoDebito.TipoRelatorio.RESUMIDO)) {
            return "Relação de Lançamento de Dívidas Diversas Resumido";
        }
        return "Relação de Lançamento de Dívidas Diversas Detalhado";
    }

    private void preencherFiltroDividasDiversas(RelatorioDTO relatorioDTO) {
        FiltroRelacaoLancamentoDividaDiversasDTO filtroDto = new FiltroRelacaoLancamentoDividaDiversasDTO();
        filtroDto.setExercicioDividaInicial(filtroRelacaoLancamentoDividaDiversa.getExercicioDividaInicial());
        filtroDto.setExercicioDividaFinal(filtroRelacaoLancamentoDividaDiversa.getExercicioDividaFinal());
        filtroDto.setDataLancamentoInicial(filtroRelacaoLancamentoDividaDiversa.getDataLancamentoInicial());
        filtroDto.setDataLancamentoFinal(filtroRelacaoLancamentoDividaDiversa.getDataLancamentoFinal());
        filtroDto.setDataPagamentoInicial(filtroRelacaoLancamentoDividaDiversa.getDataPagamentoInicial());
        filtroDto.setDataPagamentoFinal(filtroRelacaoLancamentoDividaDiversa.getDataPagamentoFinal());
        filtroDto.setDataMovimentoInicial(filtroRelacaoLancamentoDividaDiversa.getDataMovimentoInicial());
        filtroDto.setDataMovimentoFinal(filtroRelacaoLancamentoDividaDiversa.getDataMovimentoFinal());
        filtroDto.setDataVencimentoInicial(filtroRelacaoLancamentoDividaDiversa.getDataVencimentoInicial());
        filtroDto.setDataVencimentoFinal(filtroRelacaoLancamentoDividaDiversa.getDataVencimentoFinal());
        filtroDto.setIdsDivida(filtroRelacaoLancamentoDividaDiversa.inDividas());
        filtroDto.setNumeroProcessoInicial(filtroRelacaoLancamentoDividaDiversa.getNumeroProcessoInicial());
        filtroDto.setNumeroProcessoFinal(filtroRelacaoLancamentoDividaDiversa.getNumeroProcessoFinal());
        TipoDividaDiversa tipoDividaDiversa = filtroRelacaoLancamentoDividaDiversa.getTipoDividaDiversa();
        if (tipoDividaDiversa != null) {
            filtroDto.setIdTipoDividaDiversa(tipoDividaDiversa.getId());
            filtroDto.setDescricaoTipoDividaDiversa(tipoDividaDiversa.getDescricao());
        }
        TipoCadastroTributario tipoCadastroTributario = filtroRelacaoLancamentoDividaDiversa.getTipoCadastroTributario();
        if (tipoCadastroTributario != null) {
            filtroDto.setTipoCadastroTributarioDTO(tipoCadastroTributario.toWebreport());
        }
        filtroDto.setInscricaoInicial(filtroRelacaoLancamentoDividaDiversa.getInscricaoInicial());
        filtroDto.setInscricaoFinal(filtroRelacaoLancamentoDividaDiversa.getInscricaoFinal());
        filtroDto.setIdsContribuintes(Util.montarIdsList(filtroRelacaoLancamentoDividaDiversa.getContribuintes()));
        filtroDto.setContribuintes(montarNomeContribuintes(filtroRelacaoLancamentoDividaDiversa.getContribuintes()));
        filtroDto.setSituacoes(montarSituacoesParcelaWebreport());
        filtroDto.setTotalLancadoInicial(filtroRelacaoLancamentoDividaDiversa.getTotalLancadoInicial());
        filtroDto.setTotalLancadoFinal(filtroRelacaoLancamentoDividaDiversa.getTotalLancadoFinal());
        filtroDto.setTipoRelatorio(TipoRelatorioDTO.valueOf(filtroRelacaoLancamentoDividaDiversa.getTipoRelatorio().name()));
        filtroDto.setSomenteTotalizador(filtroRelacaoLancamentoDividaDiversa.getSomenteTotalizador());

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
        for (SituacaoParcela situacao : filtroRelacaoLancamentoDividaDiversa.getSituacoes()) {
            list.add(SituacaoParcelaWebreportDTO.valueOf(situacao.name()));
        }
        SituacaoParcelaWebreportDTO[] situacoes = new SituacaoParcelaWebreportDTO[list.size()];
        return list.toArray(situacoes);
    }

    public List<SelectItem> montarDividasDividasDiversas() {
        if (dividasDividasDiversas == null) {
            dividasDividasDiversas = Lists.newArrayList();
            dividasDividasDiversas.add(new SelectItem(null, "     "));
            for (Divida divida : dividaFacade.buscarDividasDeDividasDiversas("")) {
                dividasDividasDiversas.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return dividasDividasDiversas;
    }

    public List<SelectItem> montarTiposCadastro() {
        return Util.getListSelectItem(TipoCadastroTributario.values());
    }

    public List<SituacaoParcela> montarSituacoesParcela() {
        return SituacaoParcela.getValues();
    }
}
