package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AbstractFiltroRelacaoLancamentoDebito;
import br.com.webpublico.entidadesauxiliares.FiltroRelacaoLancamentoAlvara;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoAlvara;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.NaturezaJuridicaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.TipoAutonomoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.tributario.interfaces.EnumComDescricao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.tributario.FiltroRelacaoLancamentoAlvaraDTO;
import br.com.webpublico.webreportdto.dto.tributario.SituacaoParcelaWebreportDTO;
import br.com.webpublico.webreportdto.dto.tributario.TipoCobrancaTributarioDTO;
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

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaRelacaoLancamentoAlvara", pattern = "/tributario/alvara/relacao-lancamento-alvara/", viewId = "/faces/tributario/alvara/relatorios/relacaolancamentoalvara.xhtml")})
public class RelacaoLancamentoAlvaraControlador {
    private static final Logger logger = LoggerFactory.getLogger(RelacaoLancamentoAlvaraControlador.class);

    @EJB
    private NaturezaJuridicaFacade naturezaJuridicaFacade;
    @EJB
    private TipoAutonomoFacade tipoAutonomoFacade;
    private FiltroRelacaoLancamentoAlvara filtroRelacaoLancamentoAlvara;
    private CNAE cnae;
    private List<SelectItem> tiposAlvaras;
    private List<SelectItem> classificacoesAtividades;
    private List<SelectItem> naturezasJuridicas;
    private List<SelectItem> tiposAutonomos;

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }

    public FiltroRelacaoLancamentoAlvara getFiltroLancamento() {
        return filtroRelacaoLancamentoAlvara;
    }

    public void setFiltroLancamento(FiltroRelacaoLancamentoAlvara filtroRelacaoLancamentoAlvara) {
        this.filtroRelacaoLancamentoAlvara = filtroRelacaoLancamentoAlvara;
    }

    @URLAction(mappingId = "novaRelacaoLancamentoAlvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        instanciarFiltroLancamento();
    }

    protected void instanciarFiltroLancamento() {
        filtroRelacaoLancamentoAlvara = new FiltroRelacaoLancamentoAlvara();
        Exercicio exercicioVigente = SistemaService.getInstance().getExercicioCorrente();
        filtroRelacaoLancamentoAlvara.setExercicioDividaInicial(exercicioVigente.getAno());
        filtroRelacaoLancamentoAlvara.setExercicioDividaFinal(exercicioVigente.getAno());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltros();
            RelatorioDTO dto = instanciarRelatorioDto();
            dto.setTipoRelatorio(br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            ReportService.getInstance().gerarRelatorio(SistemaService.getInstance().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            logger.error("Erro ao gerar relatório. ", e);
        }
    }

    private RelatorioDTO instanciarRelatorioDto() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
        dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Finanças");
        dto.setNomeRelatorio(montarNomeRelatorio());
        dto.setNomeParametroBrasao("BRASAO");
        enviarFiltroLancamentoAlvara(dto);
        dto.setApi("tributario/lancamento-alvara/");
        return dto;
    }

    public List<SelectItem> montarTiposAlvara() {
        if (tiposAlvaras == null) {
            tiposAlvaras = Lists.newArrayList();
            tiposAlvaras.add(new SelectItem(null, "     "));
            for (TipoAlvara tipo : TipoAlvara.values()) {
                tiposAlvaras.add(new SelectItem(tipo, tipo.getDescricaoSimples()));
            }
        }
        return tiposAlvaras;
    }

    public List<SelectItem> montarClassificacoesAtividades() {
        if (classificacoesAtividades == null) {
            classificacoesAtividades = Lists.newArrayList();
            classificacoesAtividades.add(new SelectItem(null, "     "));
            for (ClassificacaoAtividade classificacaoAtividade : ClassificacaoAtividade.values()) {
                classificacoesAtividades.add(new SelectItem(classificacaoAtividade, classificacaoAtividade.getDescricao()));
            }
        }
        return classificacoesAtividades;
    }

    public List<SelectItem> montarNaturezasJuridicas() {
        if (naturezasJuridicas == null) {
            naturezasJuridicas = Lists.newArrayList();
            naturezasJuridicas.add(new SelectItem(null, "     "));
            for (NaturezaJuridica naturezaJuridica : naturezaJuridicaFacade.lista()) {
                naturezasJuridicas.add(new SelectItem(naturezaJuridica, naturezaJuridica.getDescricao()));
            }
        }
        return naturezasJuridicas;
    }

    public List<SelectItem> montarTiposAutonomos() {
        if (tiposAutonomos == null) {
            tiposAutonomos = Lists.newArrayList();
            tiposAutonomos.add(new SelectItem(null, "     "));
            for (TipoAutonomo tipoAutonomo : tipoAutonomoFacade.lista()) {
                tiposAutonomos.add(new SelectItem(tipoAutonomo, tipoAutonomo.getDescricao()));
            }
        }
        return tiposAutonomos;
    }

    public List<SelectItem> montarGrausDeRisco() {
        return Util.getListSelectItem(GrauDeRiscoDTO.values());
    }

    public List<SituacaoParcela> montarSItuacoesParcela() {
        return SituacaoParcela.getValues();
    }

    public void adicionarCnae() {
        try {
            CNAE.validarParaAdicaoEmLista(cnae, filtroRelacaoLancamentoAlvara.getCnaes());
            filtroRelacaoLancamentoAlvara.getCnaes().add(cnae);
            cnae = new CNAE();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void removerCnae(CNAE cnaeRemover) {
        if (filtroRelacaoLancamentoAlvara.getCnaes().contains(cnaeRemover)) {
            filtroRelacaoLancamentoAlvara.getCnaes().remove(cnaeRemover);
        }
    }

    private String montarNomeRelatorio() {
        if (filtroRelacaoLancamentoAlvara.getTipoRelatorio().equals(AbstractFiltroRelacaoLancamentoDebito.TipoRelatorio.RESUMIDO)) {
            return "Relação de Lançamento de Alvará Resumido";
        }
        return "Relação de Lançamento de Alvará Detalhado";
    }

    public void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();

        if (filtroRelacaoLancamentoAlvara.getExercicioDividaInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Inicial da Dívida deve ser informado.");
        }
        if (filtroRelacaoLancamentoAlvara.getExercicioDividaFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Final da Dívida deve ser informado.");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoAlvara.getDataLancamentoInicial(),
            filtroRelacaoLancamentoAlvara.getDataLancamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Lançamento Inicial deve ser menor ou igual a Data de Lançamento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoAlvara.getDataVencimentoInicial(),
            filtroRelacaoLancamentoAlvara.getDataVencimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Vencimento Inicial deve ser menor ou igual a Data de Vencimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoAlvara.getDataMovimentoInicial(),
            filtroRelacaoLancamentoAlvara.getDataMovimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Movimento Inicial deve ser menor ou igual a Data de Movimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoAlvara.getDataPagamentoInicial(),
            filtroRelacaoLancamentoAlvara.getDataPagamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Pagamento Inicial deve ser menor ou igual a Data de Pagamento Final");
        }
        ve.lancarException();
    }

    private void enviarFiltroLancamentoAlvara(RelatorioDTO dto) {
        FiltroRelacaoLancamentoAlvaraDTO filtroDto = new FiltroRelacaoLancamentoAlvaraDTO();
        filtroDto.setTipoRelatorio(TipoRelatorioDTO.valueOf(filtroRelacaoLancamentoAlvara.getTipoRelatorio().name()));
        filtroDto.setSomenteTotalizador(filtroRelacaoLancamentoAlvara.getSomenteTotalizador());
        filtroDto.setTotalizadoresPorBairro(filtroRelacaoLancamentoAlvara.getTotalizadoresPorBairro());
        filtroDto.setTotalizadoresPorSecretaria(filtroRelacaoLancamentoAlvara.getTotalizadoresPorSecretaria());
        filtroDto.setInscricaoInicial(filtroRelacaoLancamentoAlvara.getInscricaoInicial());
        filtroDto.setInscricaoFinal(filtroRelacaoLancamentoAlvara.getInscricaoFinal());
        filtroDto.setTipoAlvara((String) adicionarFiltroEnum(filtroRelacaoLancamentoAlvara.getTipoAlvara()));
        filtroDto.setProvisorio(filtroRelacaoLancamentoAlvara.getProvisorio());
        filtroDto.setClassificacaoAtividade((String) adicionarFiltroEnum(filtroRelacaoLancamentoAlvara.getClassificacaoAtividade()));
        filtroDto.setGrauDeRisco((String) adicionarFiltroEnum(filtroRelacaoLancamentoAlvara.getGrauDeRisco()));
        filtroDto.setDescricaoGrauDeRisco((String) adicionarDescricaoEnum(filtroRelacaoLancamentoAlvara.getGrauDeRisco()));
        filtroDto.setIdTipoAutonomo(filtroRelacaoLancamentoAlvara.getTipoAutonomo() != null ? filtroRelacaoLancamentoAlvara.getTipoAutonomo().getId() : null);
        filtroDto.setTipoAutonomo(filtroRelacaoLancamentoAlvara.getTipoAutonomo() != null ? filtroRelacaoLancamentoAlvara.getTipoAutonomo().getDescricao() : null);
        filtroDto.setMei(filtroRelacaoLancamentoAlvara.getMei());
        filtroDto.setCodigosCnae(filtroRelacaoLancamentoAlvara.getListaCodigoCnaes());
        filtroDto.setIdsCnae(filtroRelacaoLancamentoAlvara.getListaIdCnaes());
        filtroDto.setIdsDivida(filtroRelacaoLancamentoAlvara.inDividas());
        filtroDto.setDividas(filtroRelacaoLancamentoAlvara.filtroDividas());
        filtroDto.setExercicioDividaInicial(filtroRelacaoLancamentoAlvara.getExercicioDividaInicial());
        filtroDto.setExercicioDividaFinal(filtroRelacaoLancamentoAlvara.getExercicioDividaFinal());
        filtroDto.setDataVencimentoInicial(filtroRelacaoLancamentoAlvara.getDataVencimentoInicial());
        filtroDto.setDataVencimentoFinal(filtroRelacaoLancamentoAlvara.getDataVencimentoFinal());
        filtroDto.setDataPagamentoInicial(filtroRelacaoLancamentoAlvara.getDataPagamentoInicial());
        filtroDto.setDataPagamentoFinal(filtroRelacaoLancamentoAlvara.getDataPagamentoFinal());
        filtroDto.setDataMovimentoInicial(filtroRelacaoLancamentoAlvara.getDataMovimentoInicial());
        filtroDto.setDataMovimentoFinal(filtroRelacaoLancamentoAlvara.getDataMovimentoFinal());
        filtroDto.setDataLancamentoInicial(filtroRelacaoLancamentoAlvara.getDataLancamentoInicial());
        filtroDto.setDataLancamentoFinal(filtroRelacaoLancamentoAlvara.getDataLancamentoFinal());
        filtroDto.setTotalLancadoInicial(filtroRelacaoLancamentoAlvara.getTotalLancadoInicial());
        filtroDto.setTotalLancadoFinal(filtroRelacaoLancamentoAlvara.getTotalLancadoFinal());
        filtroDto.setTipoCobrancaTributario(filtroRelacaoLancamentoAlvara.getTipoCobrancaTributario() != null ? TipoCobrancaTributarioDTO.valueOf(filtroRelacaoLancamentoAlvara.getTipoCobrancaTributario().name()) : null);
        filtroDto.setEmitirHabitese(filtroRelacaoLancamentoAlvara.isEmitirHabitese());
        filtroDto.setIdUsuario(SistemaService.getInstance().getUsuarioCorrente().getId());
        filtroDto.setSituacoes(montarSituacoesParcela());
        filtroDto.setIdsContribuintes(Util.montarIdsList(filtroRelacaoLancamentoAlvara.getContribuintes()));
        filtroDto.setContribuintes(montarNomeContribuintes(filtroRelacaoLancamentoAlvara.getContribuintes()));
        dto.adicionarParametro("filtroDto", filtroDto);
    }

    private String montarNomeContribuintes(List<Pessoa> contribuintes) {
        List<String> nomes = Lists.newArrayList();
        String nomesContribuintes = "";
        if(contribuintes != null) {
            for (Pessoa contribuinte : contribuintes) {
                nomes.add(contribuinte.getNomeCpfCnpj());
            }
            nomesContribuintes = Joiner.on(", ").join(nomes);
        }

        return nomesContribuintes;
    }

    private Object adicionarFiltroEnum(Enum e) {
        try {
            return e.name();
        } catch (Exception ex) {
            return null;
        }
    }

    private Object adicionarDescricaoEnum(EnumComDescricao e) {
        try {
            return e.getDescricao();
        } catch (Exception ex) {
            return null;
        }
    }

    private SituacaoParcelaWebreportDTO[] montarSituacoesParcela() {
        List<SituacaoParcelaWebreportDTO> list = Lists.newArrayList();
        for (SituacaoParcela situacao : filtroRelacaoLancamentoAlvara.getSituacoes()) {
            list.add(SituacaoParcelaWebreportDTO.valueOf(situacao.name()));
        }
        SituacaoParcelaWebreportDTO[] situacoes = new SituacaoParcelaWebreportDTO[list.size()];
        return list.toArray(situacoes);
    }
}
