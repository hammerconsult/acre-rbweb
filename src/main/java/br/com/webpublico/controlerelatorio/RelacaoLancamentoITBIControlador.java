package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AbstractFiltroRelacaoLancamentoDebito;
import br.com.webpublico.entidadesauxiliares.FiltroRelacaoLancamentoITBI;
import br.com.webpublico.entidadesauxiliares.FiltroRelacaoLancamentoITBIImovel;
import br.com.webpublico.entidadesauxiliares.RelatorioRelacaoLancamentoTributario;
import br.com.webpublico.enums.SituacaoITBI;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoITBI;
import br.com.webpublico.enums.TipoImovel;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.LogradouroFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.tributario.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
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
    @URLMapping(id = "novaRelacaoLancamentoITBI", pattern = "/tributario/itbi/relacao-lancamento-itbi/",
        viewId = "/faces/tributario/itbi/relatorio/relacaolancamentoitbi.xhtml"),
    @URLMapping(id = "novaRelacaoLancamentoITBIImovel", pattern = "/tributario/itbi/relacao-lancamento-itbi-imovel/",
        viewId = "/faces/tributario/itbi/relatorio/relacaolancamentoitbiimovel.xhtml")
})
public class RelacaoLancamentoITBIControlador {

    private TipoRelatorioItbi tipoRelatorioItbi;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    ExercicioFacade exercicioFacade;
    @EJB
    LogradouroFacade logradouroFacade;
    private List<SelectItem> tiposImovel;

    private FiltroRelacaoLancamentoITBI filtroRelacaoLancamentoITBI;
    private FiltroRelacaoLancamentoITBIImovel filtroRelacaoLancamentoITBIImovel;

    public AbstractFiltroRelacaoLancamentoDebito getFiltroLancamento() {
        if (TipoRelatorioItbi.LANCAMENTO.equals(tipoRelatorioItbi)) {
            return filtroRelacaoLancamentoITBI;
        }
        return filtroRelacaoLancamentoITBIImovel;

    }

    @URLAction(mappingId = "novaRelacaoLancamentoITBI", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        instanciarFiltroLancamento();
        tipoRelatorioItbi = TipoRelatorioItbi.LANCAMENTO;
    }

    @URLAction(mappingId = "novaRelacaoLancamentoITBIImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoImovel() {
        instanciarFiltroLancamento();
        tipoRelatorioItbi = TipoRelatorioItbi.IMOVEL;
    }

    protected void instanciarFiltroLancamento() {
        filtroRelacaoLancamentoITBI = new FiltroRelacaoLancamentoITBI();
        filtroRelacaoLancamentoITBIImovel = new FiltroRelacaoLancamentoITBIImovel();
        Exercicio exercicioVigente = exercicioFacade.getExercicioCorrente();
        filtroRelacaoLancamentoITBI.setExercicioDividaInicial(exercicioVigente.getAno());
        filtroRelacaoLancamentoITBI.setExercicioDividaFinal(exercicioVigente.getAno());
        filtroRelacaoLancamentoITBIImovel.setExercicioDividaInicial(exercicioVigente.getAno());
        filtroRelacaoLancamentoITBIImovel.setExercicioDividaFinal(exercicioVigente.getAno());
    }

    public void gerarRelatorioITBI() {
        try {
            validarFiltros();
            RelatorioDTO dto = instanciarRelatorioITBIDto();
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

    public void gerarRelatorioITBIImovel() {
        try {
            validarFiltros();
            RelatorioDTO dto = instanciarRelatorioITBIImovelDto();
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

        if (filtroRelacaoLancamentoITBI.getExercicioDividaInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Inicial da Dívida deve ser informado.");
        }
        if (filtroRelacaoLancamentoITBI.getExercicioDividaFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Final da Dívida deve ser informado.");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoITBI.getDataLancamentoInicial(),
            filtroRelacaoLancamentoITBI.getDataLancamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Lançamento Inicial deve ser menor ou igual a Data de Lançamento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoITBI.getDataVencimentoInicial(),
            filtroRelacaoLancamentoITBI.getDataVencimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Vencimento Inicial deve ser menor ou igual a Data de Vencimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoITBI.getDataMovimentoInicial(),
            filtroRelacaoLancamentoITBI.getDataMovimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Movimento Inicial deve ser menor ou igual a Data de Movimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoITBI.getDataPagamentoInicial(),
            filtroRelacaoLancamentoITBI.getDataPagamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Pagamento Inicial deve ser menor ou igual a Data de Pagamento Final");
        }
        ve.lancarException();
    }

    private RelatorioDTO instanciarRelatorioITBIDto() {
        RelatorioDTO dto = montarParametros();
        preencherFiltroItbi(dto);
        dto.setApi(montarUrl());
        return dto;
    }

    private RelatorioDTO instanciarRelatorioITBIImovelDto() {
        RelatorioDTO dto = montarParametros();
        preencherFiltroItbiImovel(dto);
        dto.setApi(montarUrl());
        return dto;
    }

    private RelatorioDTO montarParametros() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome());
        dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Finanças");
        dto.setNomeRelatorio(montarNomeRelatorio());
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("TIPORELATORIO", tipoRelatorioItbi != null ? tipoRelatorioItbi.name() : "");
        return dto;
    }

    public String montarUrl() {
        if (TipoRelatorioItbi.LANCAMENTO.equals(tipoRelatorioItbi)) {
            return "tributario/itbi/relacao-lancamento-itbi/";
        } else {
            return "tributario/itbi/relacao-lancamento-itbi-imovel/";
        }
    }

    protected String montarNomeRelatorio() {
        if (TipoRelatorioItbi.LANCAMENTO.equals(tipoRelatorioItbi)) {
            if (AbstractFiltroRelacaoLancamentoDebito.TipoRelatorio.RESUMIDO.equals(filtroRelacaoLancamentoITBI.getTipoRelatorio())) {
                return "RelacaoLancamentoITBIResumido";
            }
            return "RelacaoLancamentoITBIDetalhado";
        } else {
            if (AbstractFiltroRelacaoLancamentoDebito.TipoRelatorio.RESUMIDO.equals(filtroRelacaoLancamentoITBIImovel.getTipoRelatorio())) {
                return "RelacaoLancamentoITBIImovelResumido";
            }
            return "RelacaoLancamentoITBIImovelDetalhado";
        }
    }

    private void preencherFiltroItbi(RelatorioDTO relatorioDTO) {
        FiltroRelacaoLancamentoITBIDTO filtroDto = new FiltroRelacaoLancamentoITBIDTO();
        filtroDto.setExercicioDividaInicial(filtroRelacaoLancamentoITBI.getExercicioDividaInicial());
        filtroDto.setExercicioDividaFinal(filtroRelacaoLancamentoITBI.getExercicioDividaFinal());
        filtroDto.setDataLancamentoInicial(filtroRelacaoLancamentoITBI.getDataLancamentoInicial());
        filtroDto.setDataLancamentoFinal(filtroRelacaoLancamentoITBI.getDataLancamentoFinal());
        filtroDto.setDataVencimentoInicial(filtroRelacaoLancamentoITBI.getDataVencimentoInicial());
        filtroDto.setDataVencimentoFinal(filtroRelacaoLancamentoITBI.getDataVencimentoFinal());
        filtroDto.setDataPagamentoInicial(filtroRelacaoLancamentoITBI.getDataPagamentoInicial());
        filtroDto.setDataPagamentoFinal(filtroRelacaoLancamentoITBI.getDataPagamentoFinal());
        filtroDto.setDataMovimentoInicial(filtroRelacaoLancamentoITBI.getDataMovimentoInicial());
        filtroDto.setDataMovimentoFinal(filtroRelacaoLancamentoITBI.getDataMovimentoFinal());
        filtroDto.setQuantidadeParcelas(filtroRelacaoLancamentoITBI.getQuantidadeParcelas());
        TipoImovel tipoImovel = filtroRelacaoLancamentoITBI.getTipoImovel();
        if (tipoImovel != null) {
            filtroDto.setTipoImovelDTO(TipoImovelDTO.valueOf(tipoImovel.name()));
        }
        filtroDto.setSetorInicial(filtroRelacaoLancamentoITBI.getSetorInicial());
        filtroDto.setSetorFinal(filtroRelacaoLancamentoITBI.getSetorFinal());
        filtroDto.setQuadraInicial(filtroRelacaoLancamentoITBI.getQuadraInicial());
        filtroDto.setQuadraFinal(filtroRelacaoLancamentoITBI.getQuadraFinal());
        Bairro bairro = filtroRelacaoLancamentoITBI.getBairro();
        if (bairro != null) {
            filtroDto.setIdBairro(bairro.getId());
        }
        Logradouro logradouro = filtroRelacaoLancamentoITBI.getLogradouro();
        if (logradouro != null) {
            filtroDto.setIdLogradouro(logradouro.getId());
        }
        filtroDto.setInscricaoInicial(filtroRelacaoLancamentoITBI.getInscricaoInicial());
        filtroDto.setInscricaoFinal(filtroRelacaoLancamentoITBI.getInscricaoFinal());
        filtroDto.setSituacoes(montarSituacoesParcelaWebreport());
        filtroDto.setTotalLancadoInicial(filtroRelacaoLancamentoITBI.getTotalLancadoInicial());
        filtroDto.setTotalLancadoFinal(filtroRelacaoLancamentoITBI.getTotalLancadoFinal());
        filtroDto.setTipoRelatorio(TipoRelatorioDTO.valueOf(filtroRelacaoLancamentoITBI.getTipoRelatorio().name()));
        filtroDto.setSomenteTotalizador(filtroRelacaoLancamentoITBI.getSomenteTotalizador());

        relatorioDTO.adicionarParametro("filtroDto", filtroDto);
    }

    private void preencherFiltroItbiImovel(RelatorioDTO relatorioDTO) {
        FiltroRelacaoLancamentoITBIImovelDTO filtroDto = new FiltroRelacaoLancamentoITBIImovelDTO();
        filtroDto.setExercicioDividaInicial(filtroRelacaoLancamentoITBIImovel.getExercicioDividaInicial());
        filtroDto.setExercicioDividaFinal(filtroRelacaoLancamentoITBIImovel.getExercicioDividaFinal());
        filtroDto.setDataLancamentoInicial(filtroRelacaoLancamentoITBIImovel.getDataLancamentoInicial());
        filtroDto.setDataLancamentoFinal(filtroRelacaoLancamentoITBIImovel.getDataLancamentoFinal());
        filtroDto.setDataVencimentoInicial(filtroRelacaoLancamentoITBIImovel.getDataVencimentoInicial());
        filtroDto.setDataVencimentoFinal(filtroRelacaoLancamentoITBIImovel.getDataVencimentoFinal());
        filtroDto.setDataPagamentoInicial(filtroRelacaoLancamentoITBIImovel.getDataPagamentoInicial());
        filtroDto.setDataPagamentoFinal(filtroRelacaoLancamentoITBIImovel.getDataPagamentoFinal());
        filtroDto.setDataMovimentoInicial(filtroRelacaoLancamentoITBIImovel.getDataMovimentoInicial());
        filtroDto.setDataMovimentoFinal(filtroRelacaoLancamentoITBIImovel.getDataMovimentoFinal());
        filtroDto.setInscricaoInicial(filtroRelacaoLancamentoITBIImovel.getInscricaoInicial());
        filtroDto.setInscricaoFinal(filtroRelacaoLancamentoITBIImovel.getInscricaoFinal());
        filtroDto.setValorVenalImovelInicial(filtroRelacaoLancamentoITBIImovel.getValorVenalImovelInicial());
        filtroDto.setValorVenalImovelFinal(filtroRelacaoLancamentoITBIImovel.getValorVenalImovelFinal());
        filtroDto.setValorVenalTerrenoInicial(filtroRelacaoLancamentoITBIImovel.getValorVenalTerrenoInicial());
        filtroDto.setValorVenalTerrenoFinal(filtroRelacaoLancamentoITBIImovel.getValorVenalTerrenoFinal());

        TipoImovel tipoImovel = filtroRelacaoLancamentoITBIImovel.getTipoImovel();
        if (tipoImovel != null) {
            filtroDto.setTipoImovelDTO(TipoImovelDTO.valueOf(tipoImovel.name()));
        }

        filtroDto.setSetorInicial(filtroRelacaoLancamentoITBIImovel.getSetorInicial());
        filtroDto.setSetorFinal(filtroRelacaoLancamentoITBIImovel.getSetorFinal());
        filtroDto.setQuadraInicial(filtroRelacaoLancamentoITBIImovel.getQuadraInicial());
        filtroDto.setQuadraFinal(filtroRelacaoLancamentoITBIImovel.getQuadraFinal());
        Bairro bairro = filtroRelacaoLancamentoITBIImovel.getBairro();
        if (bairro != null) {
            filtroDto.setIdBairro(bairro.getId());
        }
        Logradouro logradouro = filtroRelacaoLancamentoITBIImovel.getLogradouro();
        if (logradouro != null) {
            filtroDto.setIdLogradouro(logradouro.getId());
        }
        Pessoa proprietario = filtroRelacaoLancamentoITBIImovel.getProprietario();
        if (proprietario != null) {
            filtroDto.setIdProprietario(proprietario.getId());
        }
        filtroDto.setBaseCalculoInicial(filtroRelacaoLancamentoITBIImovel.getBaseCalculoInicial());
        filtroDto.setBaseCalculoFinal(filtroRelacaoLancamentoITBIImovel.getBaseCalculoFinal());
        TipoITBI tipoITBI = filtroRelacaoLancamentoITBIImovel.getTipoITBI();
        if (tipoITBI != null) {
            filtroDto.setTipoITBIDTO(TipoITBIDTO.valueOf(tipoITBI.name()));
        }
        filtroDto.setNumeroLaudoInicial(filtroRelacaoLancamentoITBIImovel.getNumeroLaudoInicial());
        filtroDto.setNumeroLaudoFinal(filtroRelacaoLancamentoITBIImovel.getNumeroLaudoFinal());
        Pessoa adquirente = filtroRelacaoLancamentoITBIImovel.getAdquirente();
        if (adquirente != null) {
            filtroDto.setIdAdquirente(adquirente.getId());
        }
        Pessoa transmitente = filtroRelacaoLancamentoITBIImovel.getTransmitente();
        if (transmitente != null) {
            filtroDto.setIdTransmitente(transmitente.getId());
        }
        SituacaoITBI situacao = filtroRelacaoLancamentoITBIImovel.getSituacaoItbi();
        if (situacao != null) {
            filtroDto.setSituacaoItbi(SituacaoITBIDTO.valueOf(situacao.name()));
        }
        filtroDto.setValorItbiInicial(filtroRelacaoLancamentoITBIImovel.getValorItbiInicial());
        filtroDto.setValorItbiFinal(filtroRelacaoLancamentoITBIImovel.getValorItbiFinal());
        filtroDto.setSituacoes(montarSituacoesParcelaWebreport());
        filtroDto.setTotalLancadoInicial(filtroRelacaoLancamentoITBI.getTotalLancadoInicial());
        filtroDto.setTotalLancadoFinal(filtroRelacaoLancamentoITBI.getTotalLancadoFinal());
        filtroDto.setTipoRelatorio(TipoRelatorioDTO.valueOf(filtroRelacaoLancamentoITBI.getTipoRelatorio().name()));
        filtroDto.setSomenteTotalizador(filtroRelacaoLancamentoITBI.getSomenteTotalizador());

        relatorioDTO.adicionarParametro("filtroDto", filtroDto);
    }

    private SituacaoParcelaWebreportDTO[] montarSituacoesParcelaWebreport() {
        List<SituacaoParcelaWebreportDTO> list = Lists.newArrayList();
        for (SituacaoParcela situacao : filtroRelacaoLancamentoITBI.getSituacoes()) {
            list.add(SituacaoParcelaWebreportDTO.valueOf(situacao.name()));
        }
        SituacaoParcelaWebreportDTO[] situacoes = new SituacaoParcelaWebreportDTO[list.size()];
        return list.toArray(situacoes);
    }

    public List<SituacaoParcela> montarSituacoesParcela() {
        return SituacaoParcela.getValues();
    }

    protected RelatorioRelacaoLancamentoTributario.RelacaoLancamentoTributario getRelacaoLancamentoTributario() {
        return RelatorioRelacaoLancamentoTributario.RelacaoLancamentoTributario.ITBI;
    }

    public void processarSelecaoBairro() {
        filtroRelacaoLancamentoITBI.setLogradouro(null);
    }

    public List<Logradouro> completarLogradouro(String parte) {
        if (filtroRelacaoLancamentoITBI.getBairro() != null) {
            return logradouroFacade.completaLogradourosPorBairro(parte, filtroRelacaoLancamentoITBI.getBairro());
        } else {
            return logradouroFacade.listaLogradourosAtivos(parte);
        }
    }

    public List<SelectItem> getTiposImovel() {
        if (tiposImovel == null) {
            tiposImovel = Lists.newArrayList();
            tiposImovel.add(new SelectItem(null, "     "));
            for (TipoImovel tipo : TipoImovel.values()) {
                tiposImovel.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return tiposImovel;
    }

    public List<SelectItem> getTiposItbi() {
        List<SelectItem> tipos = Lists.newArrayList();
        tipos.add(new SelectItem(null, ""));
        for (TipoITBI tipo : TipoITBI.values()) {
            tipos.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return tipos;
    }

    public List<SelectItem> getSituacoesItbi() {
        List<SelectItem> situacoes = Lists.newArrayList();
        situacoes.add(new SelectItem(null, ""));
        for (SituacaoITBI situacao : SituacaoITBI.values()) {
            situacoes.add(new SelectItem(situacao, situacao.getDescricao()));
        }
        return situacoes;
    }

    public enum TipoRelatorioItbi {
        LANCAMENTO,
        IMOVEL;
    }
}
