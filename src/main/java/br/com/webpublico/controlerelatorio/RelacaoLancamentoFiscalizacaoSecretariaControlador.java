package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.SecretariaFiscalizacao;
import br.com.webpublico.entidadesauxiliares.AbstractFiltroRelacaoLancamentoDebito;
import br.com.webpublico.entidadesauxiliares.FiltroRelacaoLancamentoFiscalizacaoSecretaria;
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
import br.com.webpublico.webreportdto.dto.tributario.FiltroRelacaoLancamentoFiscalizacaoSecretariaDTO;
import br.com.webpublico.webreportdto.dto.tributario.SecretariaFiscalizacaoDTO;
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
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 10/05/17
 * Time: 15:29
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaRelacaoLancamentoFiscalizacaoSecretaria",
        pattern = "/tributario/fiscalizacao-secretaria/relacao-lancamento-fiscalizacao-secretaria/",
        viewId = "/faces/tributario/fiscalizacaosecretaria/relatorios/relacaolancamentofiscalizacaosecretaria.xhtml")})
public class RelacaoLancamentoFiscalizacaoSecretariaControlador {

    private FiltroRelacaoLancamentoFiscalizacaoSecretaria filtroRelacaoLancamentoFiscalizacaoSecretaria;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private DividaFacade dividaFacade;

    public FiltroRelacaoLancamentoFiscalizacaoSecretaria getFiltroLancamento() {
        return filtroRelacaoLancamentoFiscalizacaoSecretaria;
    }

    @URLAction(mappingId = "novaRelacaoLancamentoFiscalizacaoSecretaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        instanciarFiltroLancamento();
    }

    protected void instanciarFiltroLancamento() {
        filtroRelacaoLancamentoFiscalizacaoSecretaria = new FiltroRelacaoLancamentoFiscalizacaoSecretaria();
        Exercicio exercicioVigente = exercicioFacade.getExercicioCorrente();
        filtroRelacaoLancamentoFiscalizacaoSecretaria.setExercicioDividaInicial(exercicioVigente.getAno());
        filtroRelacaoLancamentoFiscalizacaoSecretaria.setExercicioDividaFinal(exercicioVigente.getAno());
    }

    public void gerarRelatorio() {
        try {
            validarFiltros();
            RelatorioDTO dto = instanciarRelatorioDto();
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

        if (filtroRelacaoLancamentoFiscalizacaoSecretaria.getExercicioDividaInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Inicial da Dívida deve ser informado.");
        }
        if (filtroRelacaoLancamentoFiscalizacaoSecretaria.getExercicioDividaFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Final da Dívida deve ser informado.");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoFiscalizacaoSecretaria.getDataLancamentoInicial(),
            filtroRelacaoLancamentoFiscalizacaoSecretaria.getDataLancamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Lançamento Inicial deve ser menor ou igual a Data de Lançamento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoFiscalizacaoSecretaria.getDataVencimentoInicial(),
            filtroRelacaoLancamentoFiscalizacaoSecretaria.getDataVencimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Vencimento Inicial deve ser menor ou igual a Data de Vencimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoFiscalizacaoSecretaria.getDataMovimentoInicial(),
            filtroRelacaoLancamentoFiscalizacaoSecretaria.getDataMovimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Movimento Inicial deve ser menor ou igual a Data de Movimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoFiscalizacaoSecretaria.getDataPagamentoInicial(),
            filtroRelacaoLancamentoFiscalizacaoSecretaria.getDataPagamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Pagamento Inicial deve ser menor ou igual a Data de Pagamento Final");
        }
        ve.lancarException();
    }

    private RelatorioDTO instanciarRelatorioDto() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
        dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Finanças");
        dto.setNomeRelatorio(montarNomeRelatorio());
        dto.setNomeParametroBrasao("BRASAO");
        preencherFiltroFiscalizacaoSecretaria(dto);
        dto.setApi("tributario/lancamento-fiscalizacao-secretaria/");
        return dto;
    }

    public List<Divida> completarDividas(String parte) {
        return dividaFacade.listaDividasAtivas(parte.trim(), null);
    }

    private String montarNomeRelatorio() {
        if (filtroRelacaoLancamentoFiscalizacaoSecretaria.getTipoRelatorio().equals(AbstractFiltroRelacaoLancamentoDebito.TipoRelatorio.RESUMIDO)) {
            return "Relação de Lançamento de Fiscalização de Secretaria Resumido";
        }
        return "Relação de Lançamento de Fiscalização de Secretaria Detalhado";
    }

    private void preencherFiltroFiscalizacaoSecretaria(RelatorioDTO relatorioDTO) {
        FiltroRelacaoLancamentoFiscalizacaoSecretariaDTO filtroDto = new FiltroRelacaoLancamentoFiscalizacaoSecretariaDTO();
        filtroDto.setExercicioDividaInicial(filtroRelacaoLancamentoFiscalizacaoSecretaria.getExercicioDividaInicial());
        filtroDto.setExercicioDividaFinal(filtroRelacaoLancamentoFiscalizacaoSecretaria.getExercicioDividaFinal());
        filtroDto.setDataLancamentoInicial(filtroRelacaoLancamentoFiscalizacaoSecretaria.getDataLancamentoInicial());
        filtroDto.setDataLancamentoFinal(filtroRelacaoLancamentoFiscalizacaoSecretaria.getDataLancamentoFinal());
        filtroDto.setDataVencimentoInicial(filtroRelacaoLancamentoFiscalizacaoSecretaria.getDataVencimentoInicial());
        filtroDto.setDataVencimentoFinal(filtroRelacaoLancamentoFiscalizacaoSecretaria.getDataVencimentoFinal());
        filtroDto.setDataPagamentoInicial(filtroRelacaoLancamentoFiscalizacaoSecretaria.getDataPagamentoInicial());
        filtroDto.setDataPagamentoFinal(filtroRelacaoLancamentoFiscalizacaoSecretaria.getDataPagamentoFinal());
        filtroDto.setDataMovimentoInicial(filtroRelacaoLancamentoFiscalizacaoSecretaria.getDataMovimentoInicial());
        filtroDto.setDataMovimentoFinal(filtroRelacaoLancamentoFiscalizacaoSecretaria.getDataMovimentoFinal());
        filtroDto.setIdsDivida(filtroRelacaoLancamentoFiscalizacaoSecretaria.inDividas());
        for (SecretariaFiscalizacao secretaria : filtroRelacaoLancamentoFiscalizacaoSecretaria.getSecretarias()) {
            SecretariaFiscalizacaoDTO secretariaFiscalizacaoDTO = new SecretariaFiscalizacaoDTO();
            secretariaFiscalizacaoDTO.setId(secretaria.getId());
            if ((secretaria.getUnidadeOrganizacional() != null)) {
                secretariaFiscalizacaoDTO.setDescricaoUnidade(secretaria.getUnidadeOrganizacional().getDescricao());
            }
        }
        TipoCadastroTributario tipoCadastroTributario = filtroRelacaoLancamentoFiscalizacaoSecretaria.getTipoCadastroTributario();
        if (tipoCadastroTributario != null) {
            filtroDto.setTipoCadastroTributarioDTO(tipoCadastroTributario.toWebreport());
        }
        filtroDto.setInscricaoInicial(filtroRelacaoLancamentoFiscalizacaoSecretaria.getInscricaoInicial());
        filtroDto.setInscricaoFinal(filtroRelacaoLancamentoFiscalizacaoSecretaria.getInscricaoFinal());
        filtroDto.setIdsContribuintes(Util.montarIdsList(filtroRelacaoLancamentoFiscalizacaoSecretaria.getContribuintes()));
        filtroDto.setContribuintes(montarNomeContribuintes(filtroRelacaoLancamentoFiscalizacaoSecretaria.getContribuintes()));
        filtroDto.setSituacoes(montarSituacoesParcelaWebreport());
        filtroDto.setTotalLancadoInicial(filtroRelacaoLancamentoFiscalizacaoSecretaria.getTotalLancadoInicial());
        filtroDto.setTotalLancadoFinal(filtroRelacaoLancamentoFiscalizacaoSecretaria.getTotalLancadoFinal());
        filtroDto.setTipoRelatorio(TipoRelatorioDTO.valueOf(filtroRelacaoLancamentoFiscalizacaoSecretaria.getTipoRelatorio().name()));
        filtroDto.setSomenteTotalizador(filtroRelacaoLancamentoFiscalizacaoSecretaria.getSomenteTotalizador());
        filtroDto.setSomenteTotalizador(filtroRelacaoLancamentoFiscalizacaoSecretaria.getTotalizadoresPorSecretaria());

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
        for (SituacaoParcela situacao : filtroRelacaoLancamentoFiscalizacaoSecretaria.getSituacoes()) {
            list.add(SituacaoParcelaWebreportDTO.valueOf(situacao.name()));
        }
        SituacaoParcelaWebreportDTO[] situacoes = new SituacaoParcelaWebreportDTO[list.size()];
        return list.toArray(situacoes);
    }

    public List<SelectItem> montarFiscalizacaoSecretaria() {
        List<SelectItem> fiscalizacaoSecretarias = Lists.newArrayList();
        fiscalizacaoSecretarias.add(new SelectItem(null, "     "));
        for (Divida divida : dividaFacade.buscarDividasDeFiscalizacaoSecretaria("")) {
            fiscalizacaoSecretarias.add(new SelectItem(divida, divida.getDescricao()));
        }
        return fiscalizacaoSecretarias;
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
}
