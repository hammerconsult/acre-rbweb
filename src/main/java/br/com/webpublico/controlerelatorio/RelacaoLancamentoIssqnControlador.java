package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.NaturezaJuridica;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.AbstractFiltroRelacaoLancamentoDebito;
import br.com.webpublico.entidadesauxiliares.FiltroRelacaoLancamentoISSQN;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.NaturezaJuridicaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.tributario.interfaces.EnumComDescricao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.tributario.FiltroRelacaoLancamentoISSQNDTO;
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
 * Date: 28/04/15
 * Time: 10:29
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaRelacaoLancamentoIssqn", pattern = "/tributario/issqn/relacao-lancamento-issqn/", viewId = "/faces/tributario/issqn/relatorios/relacaolancamentoissqn.xhtml")})
public class RelacaoLancamentoIssqnControlador {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private NaturezaJuridicaFacade naturezaJuridicaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private DividaFacade dividaFacade;
    private FiltroRelacaoLancamentoISSQN filtroRelacaoLancamentoISSQN;
    private List<SelectItem> issqn;
    private List<SelectItem> classificacoesAtividades;
    private List<SelectItem> naturezasJuridicas;

    public FiltroRelacaoLancamentoISSQN getFiltroLancamento() {
        return filtroRelacaoLancamentoISSQN;
    }

    @URLAction(mappingId = "novaRelacaoLancamentoIssqn", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        instanciarFiltroLancamento();
    }

    protected void instanciarFiltroLancamento() {
        filtroRelacaoLancamentoISSQN = new FiltroRelacaoLancamentoISSQN();
        Exercicio exercicioVigente = exercicioFacade.getExercicioCorrente();
        filtroRelacaoLancamentoISSQN.setExercicioDividaInicial(exercicioVigente.getAno());
        filtroRelacaoLancamentoISSQN.setExercicioDividaFinal(exercicioVigente.getAno());
    }

    public void gerarRelatorio(String tipoRelatorioExtensai) {
        try {
            validarFiltros();
            RelatorioDTO dto = instanciarRelatorioDto(tipoRelatorioExtensai);
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

        if (filtroRelacaoLancamentoISSQN.getExercicioDividaInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Inicial da Dívida deve ser informado.");
        }
        if (filtroRelacaoLancamentoISSQN.getExercicioDividaFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Final da Dívida deve ser informado.");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoISSQN.getDataLancamentoInicial(),
            filtroRelacaoLancamentoISSQN.getDataLancamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Lançamento Inicial deve ser menor ou igual a Data de Lançamento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoISSQN.getDataVencimentoInicial(),
            filtroRelacaoLancamentoISSQN.getDataVencimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Vencimento Inicial deve ser menor ou igual a Data de Vencimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoISSQN.getDataMovimentoInicial(),
            filtroRelacaoLancamentoISSQN.getDataMovimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Movimento Inicial deve ser menor ou igual a Data de Movimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoISSQN.getDataPagamentoInicial(),
            filtroRelacaoLancamentoISSQN.getDataPagamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Pagamento Inicial deve ser menor ou igual a Data de Pagamento Final");
        }
        ve.lancarException();
    }

    private RelatorioDTO instanciarRelatorioDto(String tipoRelatorioExtensai) {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome());
        dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Finanças");
        dto.setNomeRelatorio(montarNomeRelatorio());
        dto.setNomeParametroBrasao("BRASAO");
        dto.setTipoRelatorio(br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO.valueOf(tipoRelatorioExtensai));
        preencherFiltroIssqn(dto);
        dto.setApi("tributario/lancamento-issqn/");
        return dto;
    }

    public List<Divida> completarDividas(String parte) {
        return dividaFacade.listaDividasAtivas(parte.trim(), null);
    }

    protected String montarNomeRelatorio() {
        if (filtroRelacaoLancamentoISSQN.getTipoRelatorio().equals(AbstractFiltroRelacaoLancamentoDebito.TipoRelatorio.RESUMIDO)) {
            return "Relação de Lançamento de I.S.S.Q.N Resumido";
        }
        return "Relação de Lançamento de I.S.S.Q.N Detalhado";
    }

    private void preencherFiltroIssqn(RelatorioDTO relatorioDTO) {
        FiltroRelacaoLancamentoISSQNDTO filtroDto = new FiltroRelacaoLancamentoISSQNDTO();
        filtroDto.setInscricaoInicial(filtroRelacaoLancamentoISSQN.getInscricaoInicial());
        filtroDto.setInscricaoFinal(filtroRelacaoLancamentoISSQN.getInscricaoFinal());
        filtroDto.setExercicioDividaInicial(filtroRelacaoLancamentoISSQN.getExercicioDividaInicial());
        filtroDto.setExercicioDividaFinal(filtroRelacaoLancamentoISSQN.getExercicioDividaFinal());
        filtroDto.setDataLancamentoInicial(filtroRelacaoLancamentoISSQN.getDataLancamentoInicial());
        filtroDto.setDataLancamentoFinal(filtroRelacaoLancamentoISSQN.getDataLancamentoFinal());
        filtroDto.setDataPagamentoInicial(filtroRelacaoLancamentoISSQN.getDataPagamentoInicial());
        filtroDto.setDataPagamentoFinal(filtroRelacaoLancamentoISSQN.getDataPagamentoFinal());
        filtroDto.setDataMovimentoInicial(filtroRelacaoLancamentoISSQN.getDataMovimentoInicial());
        filtroDto.setDataMovimentoFinal(filtroRelacaoLancamentoISSQN.getDataMovimentoFinal());
        filtroDto.setDataVencimentoInicial(filtroRelacaoLancamentoISSQN.getDataVencimentoInicial());
        filtroDto.setDataVencimentoFinal(filtroRelacaoLancamentoISSQN.getDataVencimentoFinal());
        filtroDto.setIdsDivida(filtroRelacaoLancamentoISSQN.inDividas());
        filtroDto.setMesReferencia(filtroRelacaoLancamentoISSQN.getMesReferencia());
        filtroDto.setTipoISS(filtroRelacaoLancamentoISSQN.inTipoIss());
        filtroDto.setTiposISS(filtroRelacaoLancamentoISSQN.filtroTipoIss());
        filtroDto.setClassificacaoAtividade(filtroRelacaoLancamentoISSQN.inClassificacaoAtividade());
        filtroDto.setClassificacaoAtividadeDescricao(filtroRelacaoLancamentoISSQN.filtroClassificacaoAtividade());
        filtroDto.setIdNaturezaJuridica(filtroRelacaoLancamentoISSQN.getNaturezaJuridica() != null ? filtroRelacaoLancamentoISSQN.getNaturezaJuridica().getId() : null);
        filtroDto.setNaturezaJuridica(filtroRelacaoLancamentoISSQN.filtroNaturezaJuridica());
        filtroDto.setGrauDeRisco((String) adicionarFiltroEnum(filtroRelacaoLancamentoISSQN.getGrauDeRisco()));
        filtroDto.setDescricaoGrauDeRisco((String) adicionarDescricaoEnum(filtroRelacaoLancamentoISSQN.getGrauDeRisco()));
        filtroDto.setMei(filtroRelacaoLancamentoISSQN.getMei());
        filtroDto.setTotalLancadoInicial(filtroRelacaoLancamentoISSQN.getTotalLancadoInicial());
        filtroDto.setTotalLancadoFinal(filtroRelacaoLancamentoISSQN.getTotalLancadoFinal());
        filtroDto.setIdsContribuintes((filtroRelacaoLancamentoISSQN.getContribuintes() != null && !filtroRelacaoLancamentoISSQN.getContribuintes().isEmpty()) ?Util.montarIdsList(filtroRelacaoLancamentoISSQN.getContribuintes()) : null);
        filtroDto.setContribuintes(montarNomeContribuintes(filtroRelacaoLancamentoISSQN.getContribuintes()));
        filtroDto.setSituacoes(montarSituacoesParcelaWebreport());
        filtroDto.setTotalLancadoInicial(filtroRelacaoLancamentoISSQN.getTotalLancadoInicial());
        filtroDto.setTotalLancadoFinal(filtroRelacaoLancamentoISSQN.getTotalLancadoFinal());
        filtroDto.setTipoRelatorio(TipoRelatorioDTO.valueOf(filtroRelacaoLancamentoISSQN.getTipoRelatorio().name()));
        filtroDto.setSomenteTotalizador(filtroRelacaoLancamentoISSQN.getSomenteTotalizador());

        relatorioDTO.adicionarParametro("filtroDto", filtroDto);
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

    public List<SelectItem> montarGrausDeRisco() {
        return Util.getListSelectItem(GrauDeRiscoDTO.values());
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
        for (SituacaoParcela situacao : filtroRelacaoLancamentoISSQN.getSituacoes()) {
            list.add(SituacaoParcelaWebreportDTO.valueOf(situacao.name()));
        }
        SituacaoParcelaWebreportDTO[] situacoes = new SituacaoParcelaWebreportDTO[list.size()];
        return list.toArray(situacoes);
    }

    public List<SelectItem> montarIssqn() {
        if (issqn == null) {
            issqn = Lists.newArrayList();
            issqn.add(new SelectItem(null, "     "));
            for (Divida divida : dividaFacade.buscarDividasDeDividasDiversas("")) {
                issqn.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return issqn;
    }

    public List<SituacaoParcela> montarSituacoesParcela() {
        return SituacaoParcela.getValues();
    }

    public List<SelectItem> tiposDeIssqn() {
        List<SelectItem> tipos = Lists.newArrayList();
        tipos.add(new SelectItem(null, "     "));
        for (TipoCalculoISS tipo : TipoCalculoISS.values()) {
            tipos.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return tipos;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (Mes mes : Mes.values()) {
            lista.add(new SelectItem(mes.getNumeroMes(), mes.getDescricao()));
        }
        return lista;
    }
}
