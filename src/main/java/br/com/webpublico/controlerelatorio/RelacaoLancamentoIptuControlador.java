package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Logradouro;
import br.com.webpublico.entidadesauxiliares.AbstractFiltroRelacaoLancamentoDebito;
import br.com.webpublico.entidadesauxiliares.FiltroRelacaoLancamentoIptu;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoCobrancaTributario;
import br.com.webpublico.enums.TipoImovel;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ConfiguracaoTributarioFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.LogradouroFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
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
    @URLMapping(id = "novaRelacaoLancamentoIptu", pattern = "/tributario/iptu/relacao-lancamento-iptu/", viewId = "/faces/tributario/iptu/relatorio/relacaolancamentoiptu.xhtml")})
public class RelacaoLancamentoIptuControlador implements Serializable {

    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    private FiltroRelacaoLancamentoIptu filtroLancamento;
    private List<SelectItem> tiposImovel;
    private List<SelectItem> tiposCobranca;

    public void gerarRelatorio(String extensaoRelatorio) {
        try {
            validarFiltrosUtilizados();
            TipoRelatorioDTO tipoRelatorio = TipoRelatorioDTO.valueOf(extensaoRelatorio);
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.setTipoRelatorio(tipoRelatorio);
            dto.adicionarParametro("filtroRelacaoLancamentoIptuDTO", filtroLancamento.toDto());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("tributario/relacao-lancamento-iptu/" + (TipoRelatorioDTO.XLS.equals(tipoRelatorio) ? "excel/" : ""));
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    protected String getNomeRelatorio() {
        if (filtroLancamento.getTipoRelatorio().equals(AbstractFiltroRelacaoLancamentoDebito.TipoRelatorio.RESUMIDO)) {
            return "RelacaoLancamentoIptuResumido";
        }
        return "RelacaoLancamentoIptuDetalhado";
    }

    private void validarFiltrosUtilizados() {
        ValidacaoException ve = new ValidacaoException();
        AbstractFiltroRelacaoLancamentoDebito filtroLancamento = getFiltroLancamento();
        if (filtroLancamento.getExercicioDividaInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o Exercício Inicial!");
        }
        if (filtroLancamento.getExercicioDividaFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o Exercício Final!");
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
        if (filtroLancamento.getExercicioInicial() != null && filtroLancamento.getExercicioFinal() != null) {
            if (filtroLancamento.getExercicioInicial().getAno() > filtroLancamento.getExercicioFinal().getAno()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Ano do Processo de Débito Inícial deve ser menor ou igual ao Ano do Processo de Débito Final.");
            }
        }
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        if (configuracaoTributario.getTributoHabitese() == null) {
            if (filtroLancamento.isEmitirHabitese()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado tributo de habite-se configurado para o IPTU nas configurações do tributário.");
            }
        }
        ve.lancarException();
    }

    @URLAction(mappingId = "novaRelacaoLancamentoIptu", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtroLancamento = new FiltroRelacaoLancamentoIptu();
        Exercicio exercicioVigente = exercicioFacade.getExercicioCorrente();
        filtroLancamento.setExercicioDividaInicial(exercicioVigente.getAno());
        filtroLancamento.setExercicioDividaFinal(exercicioVigente.getAno());
        filtroLancamento.setInscricaoInicial("100000000000000");
        filtroLancamento.setInscricaoFinal("999999999999999");
    }

    public void processaSelecaoBairro() {
        filtroLancamento.setLogradouro(null);
    }

    public List<Logradouro> completaLogradouro(String parte) {
        if (filtroLancamento.getBairro() != null) {
            return logradouroFacade.completaLogradourosPorBairro(parte, filtroLancamento.getBairro());
        } else {
            return logradouroFacade.listaLogradourosAtivos(parte);
        }
    }

    public List<SituacaoParcela> getTodasAsSituacoes() {
        return SituacaoParcela.getValues();
    }

    public List<SelectItem> tiposCadastro() {
        return Util.getListSelectItem(TipoCadastroTributario.values());
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

    public List<SelectItem> getTiposCobranca() {
        if (tiposCobranca == null) {
            tiposCobranca = new ArrayList<>();
            tiposCobranca.add(new SelectItem(null, ""));
            for (TipoCobrancaTributario tipoCobranca : TipoCobrancaTributario.values()) {
                tiposCobranca.add(new SelectItem(tipoCobranca, tipoCobranca.getDescricao()));
            }
        }
        return tiposCobranca;
    }

    public FiltroRelacaoLancamentoIptu getFiltroLancamento() {
        return filtroLancamento;
    }

    public void setFiltroLancamento(FiltroRelacaoLancamentoIptu filtroLancamento) {
        this.filtroLancamento = filtroLancamento;
    }

    public void setTiposImovel(List<SelectItem> tiposImovel) {
        this.tiposImovel = tiposImovel;
    }

    public void setTiposCobranca(List<SelectItem> tiposCobranca) {
        this.tiposCobranca = tiposCobranca;
    }
}
