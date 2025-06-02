/*
 * Codigo gerado automaticamente em Mon Mar 19 14:45:35 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.PropostaConcessaoDiariaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "propostaConcessaoDiariaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novodiaria", pattern = "/diaria/novo/", viewId = "/faces/financeiro/concessaodediarias/propostaconcessaodediaria/edita.xhtml"),
    @URLMapping(id = "editardiaria", pattern = "/diaria/editar/#{propostaConcessaoDiariaControlador.id}/", viewId = "/faces/financeiro/concessaodediarias/propostaconcessaodediaria/edita.xhtml"),
    @URLMapping(id = "verdiaria", pattern = "/diaria/ver/#{propostaConcessaoDiariaControlador.id}/", viewId = "/faces/financeiro/concessaodediarias/propostaconcessaodediaria/visualizar.xhtml"),
    @URLMapping(id = "listardiaria", pattern = "/diaria/listar/", viewId = "/faces/financeiro/concessaodediarias/propostaconcessaodediaria/lista.xhtml"),
    @URLMapping(id = "nova-diaria-colaborador", pattern = "/diaria/novo-diaria-termo-colaborador/#{propostaConcessaoDiariaControlador.id}/", viewId = "/faces/financeiro/concessaodediarias/propostaconcessaodediaria/edita.xhtml")
})
public class PropostaConcessaoDiariaControlador extends PrettyControlador<PropostaConcessaoDiaria> implements Serializable, CRUD {

    public static final int ZERO_HORA = 0;
    public static final int SEIS_HORAS = 6;
    public static final int DEZOITO_HORAS = 18;
    public static final int VINTE_QUATRO_HORAS = 24;

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private PropostaConcessaoDiariaFacade propostaConcessaoDiariaFacade;
    private Cidade cidadeOrigem;
    private Cidade cidadeDestino;
    private ConverterAutoComplete converterFonteDespesaORC;
    private ConverterAutoComplete converterContaCorrente;
    private ConverterAutoComplete converterDespesaORC;
    private ConverterGenerico converterClasseDiaria;
    private DiariaPrestacaoConta diariaPrestacaoContaselecionada;
    private List<ValorIndicadorEconomico> valoresIndicadorEconomico;
    private IndicadorEconomico indicadorEconomico;
    private BigDecimal valorIndicador;
    private Boolean geraReserva;
    private Boolean liberaReserva;
    private DiariaArquivo arquivo;
    private ViagemDiaria viagemDiaria;
    private PropostaConcessaoDiaria selecionadoClone;
    private BigDecimal valorDaDiaria;

    public PropostaConcessaoDiariaControlador() {
        super(PropostaConcessaoDiaria.class);
    }

    @URLAction(mappingId = "novodiaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        diariaPrestacaoContaselecionada = new DiariaPrestacaoConta();
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        selecionado.setTipoProposta(TipoProposta.CONCESSAO_DIARIA);
        selecionado.setDataLancamento(sistemaControlador.getDataOperacao());
        geraReserva = false;
        liberaReserva = true;
        valoresIndicadorEconomico = Lists.newArrayList();
        valorIndicador = BigDecimal.ZERO;
        cancelarViagemDiaria();
        valorDaDiaria = BigDecimal.ZERO;
        inicializarArquivo(false);
    }

    @URLAction(mappingId = "nova-diaria-colaborador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoDiariaColaborador() {
        novo();
        selecionado.setTipoProposta(TipoProposta.COLABORADOR_EVENTUAL);
        TermoColaboradorEventual termoColaboradorEventual = propostaConcessaoDiariaFacade.getTermoColaboradorEventualFacade().recuperar(super.getId());
        selecionado.setPessoaFisica(termoColaboradorEventual.getPessoa());
    }

    @URLAction(mappingId = "verdiaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        recuperarEditaVer();
    }

    @URLAction(mappingId = "editardiaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditaVer();
        valorDaDiaria = BigDecimal.ZERO;
    }

    public void instanciarViagemDiaria() {
        viagemDiaria = new ViagemDiaria();
        viagemDiaria.setTipoViagem(selecionado.getTipoViagem());
        cidadeDestino = null;
        cidadeOrigem = null;
    }

    public void cancelarViagemDiaria() {
        viagemDiaria = null;
    }

    public void editarViagemDiaria(ViagemDiaria viagem) {
        viagemDiaria = (ViagemDiaria) Util.clonarObjeto(viagem);
        cidadeDestino = null;
        cidadeOrigem = null;
        calcularValorDiaria();
    }

    public void limparViagens() {
        selecionado.getViagens().clear();
    }

    public void removerViagemDiaria(ViagemDiaria viagem) {
        selecionado.getViagens().remove(viagem);
    }

    public void adicionarViagemDiaria() {
        try {
            validarViagemDiaria();
            calcularDiaria(viagemDiaria);
            viagemDiaria.setPropostaConcessaoDiaria(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getViagens(), viagemDiaria);
            cancelarViagemDiaria();
            if (cidadeOrigem != null && cidadeDestino != null) {
                selecionado.setEtinerarioExec(selecionado.getEtinerarioExec() + "\n " + "Origem: " + cidadeOrigem.toString() + ", Destino: " + cidadeDestino.toString());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<PropostaConcessaoDiaria> completarDiarias(String filtro) {
        return propostaConcessaoDiariaFacade.listaFiltrandoDiariasPorUnidadePessoaTipoESituacao(filtro.trim(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), TipoProposta.CONCESSAO_DIARIA, SituacaoDiaria.DEFERIDO);
    }

    public List<SelectItem> getTiposDeDespesasCusteadas() {
        return Util.getListSelectItem(TipoDespesaCusteadaTerceiro.values());
    }

    private void validarViagemDiaria() {
        viagemDiaria.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        if (viagemDiaria.isDiariaInternacional() && indicadorEconomico == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Índice Econômico deve ser informado.");
        }
        if (Strings.isNullOrEmpty(viagemDiaria.getHoraInicialSaida())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo hora inicial de saída deve ser informado.");
        }
        if (Strings.isNullOrEmpty(viagemDiaria.getMinutoInicialSaida())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo minuto inicial de saida deve ser informado.");
        }
        if (Strings.isNullOrEmpty(viagemDiaria.getHoraFinalSaida())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo hora final de saída deve ser informado.");
        }
        if (Strings.isNullOrEmpty(viagemDiaria.getMinutoFinalSaida())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo minuto final de saida deve ser informado.");
        }
        if (Strings.isNullOrEmpty(viagemDiaria.getHoraInicialRetorno())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo hora inicial de retorno deve ser informado.");
        }
        if (Strings.isNullOrEmpty(viagemDiaria.getMinutoInicialRetorno())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo minuto inicial de retorno deve ser informado.");
        }
        if (Strings.isNullOrEmpty(viagemDiaria.getHoraFinalRetorno())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo hora final de retorno deve ser informado.");
        }
        if (Strings.isNullOrEmpty(viagemDiaria.getMinutoFinalRetorno())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo minuto final de retorno deve ser informado.");
        }
        ve.lancarException();
        validarDatas(viagemDiaria.getDataInicialRetorno(), viagemDiaria.getDataInicialSaida(), ve, "A data inicial de retorno deve ser superior a data inicial de saída.");
        validarDatas(viagemDiaria.getDataFinalRetorno(), viagemDiaria.getDataFinalSaida(), ve, "A data final de retorno deve ser superior a data final de saída.");
        validarHoras(viagemDiaria.getDataInicialSaida(), viagemDiaria.getDataFinalSaida(), viagemDiaria.getHoraInicialSaida(), viagemDiaria.getHoraFinalSaida(), ve, "A hora inicial de saída deve ser inferior à hora final de saída.");
        validarHoras(viagemDiaria.getDataInicialRetorno(), viagemDiaria.getDataFinalRetorno(), viagemDiaria.getHoraInicialRetorno(), viagemDiaria.getHoraFinalRetorno(), ve, "A hora inicial de retorno deve ser inferior à hora final de retorno.");

        ve.lancarException();
    }

    private void validarDatas(Date dataInicial, Date dataFinal, ValidacaoException ve, String mensagem) {
        if (Util.getDataHoraMinutoSegundoZerado(dataInicial).compareTo(Util.getDataHoraMinutoSegundoZerado(dataFinal)) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(mensagem);
        }
    }

    private void validarHoras(Date dataInicial, Date dataFinal, String horaInicial, String horaFinal, ValidacaoException ve, String mensagem) {
        if (dataInicial.compareTo(dataFinal) == 0 && Integer.valueOf(horaInicial).compareTo(Integer.valueOf(horaFinal)) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(mensagem);
        }
    }

    public void inicializarArquivo(Boolean despesaCusteadaTerceiro) {
        arquivo = new DiariaArquivo();
        arquivo.setPropostaConcessaoDiaria(selecionado);
        arquivo.setDespesaCusteadaTerceiro(despesaCusteadaTerceiro);
    }

    public void redirecionarVerArquivo(DiariaArquivo arquivo) {
        FacesUtil.redirecionamentoInterno("/arquivos/" + arquivo.getArquivo().getNome() + "?id=" + arquivo.getArquivo().getId());
    }

    public void recuperarEditaVer() {
        diariaPrestacaoContaselecionada = new DiariaPrestacaoConta();
        geraReserva = false;
        liberaReserva = true;
        cancelarViagemDiaria();
        selecionadoClone = (PropostaConcessaoDiaria) Util.clonarObjeto(selecionado);
    }

    public List<AtoLegal> completarAtoLegal(String parte) {
        return propostaConcessaoDiariaFacade.getAtoLegalFacade().buscarAtosLegaisPorExercicioAndNumeroOrNome(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<Cidade> completaCidadeOrigem(String parte) {
        return propostaConcessaoDiariaFacade.getCidadeFacade().listaFiltrandoCidade(parte.trim());
    }

    public List<Cidade> completaCidadeDestino(String parte) {
        return propostaConcessaoDiariaFacade.getCidadeFacade().listaFiltrandoCidade(parte.trim());
    }

    public void atribuirNullDespesa() {
        selecionado.setDespesaORC(null);
        selecionado.setFonteDespesaORC(null);
    }

    public List<Empenho> getEmpenhos() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaEmpenhoPorProposta(selecionado);
        } else {
            return Lists.newArrayList();
        }
    }

    public List<EmpenhoEstorno> getEmpenhosEstorno() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaEmpenhoEstornoPorProposta(selecionado);
        } else {
            return Lists.newArrayList();
        }
    }

    public List<Liquidacao> getLiquidacoes() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaLiquidacaoPorProposta(selecionado);
        } else {
            return Lists.newArrayList();
        }
    }

    public List<LiquidacaoEstorno> getLiquidacoesEstorno() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaLiquidacaoEstornoPorProposta(selecionado);
        } else {
            return Lists.newArrayList();
        }
    }

    public List<Pagamento> getPagamentos() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaPagamentoPorProposta(selecionado);
        } else {
            return Lists.newArrayList();
        }
    }

    public List<PagamentoEstorno> getPagamentosEstorno() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaPagamentoEstornoPorProposta(selecionado);
        } else {
            return Lists.newArrayList();
        }
    }

    public List<DiariaContabilizacao> getDiariasContabilizacaoBaixaNormal() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaDiariaContabilizacaoPorPropostaTipoEOperacao(OperacaoDiariaContabilizacao.BAIXA, TipoLancamento.NORMAL, selecionado);
        } else {
            return Lists.newArrayList();
        }
    }

    public List<DiariaContabilizacao> getDiariasContabilizacaoBaixaEstorno() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaDiariaContabilizacaoPorPropostaTipoEOperacao(OperacaoDiariaContabilizacao.BAIXA, TipoLancamento.ESTORNO, selecionado);
        } else {
            return Lists.newArrayList();
        }
    }

    public List<DiariaContabilizacao> getDiariasContabilizacaoInscricaoNormal() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaDiariaContabilizacaoPorPropostaTipoEOperacao(OperacaoDiariaContabilizacao.INSCRICAO, TipoLancamento.NORMAL, selecionado);
        } else {
            return Lists.newArrayList();
        }
    }

    public List<DiariaContabilizacao> getDiariasContabilizacaoInscricaoEstorno() {
        if (!isOperacaoNovo()) {
            return propostaConcessaoDiariaFacade.listaDiariaContabilizacaoPorPropostaTipoEOperacao(OperacaoDiariaContabilizacao.INSCRICAO, TipoLancamento.ESTORNO, selecionado);
        } else {
            return Lists.newArrayList();
        }
    }

    public void cancelarDeferimento() {
        cancelar();
        FacesUtil.addOperacaoRealizada(" O Processo de deferimento da diária:  " + metadata.getNomeEntidade() + " " + selecionado.toString() + " foi cancelado com sucesso.");
    }

    @Override
    public void salvar() {
        try {
            validarDiaria();
            validarDiariaNormalDadosPassagem();
            calcularValorDiariaTotal();
            propostaConcessaoDiariaFacade.getHierarquiaOrganizacionalFacade().validaVigenciaHIerarquiaAdministrativaOrcamentaria(selecionado.getUnidadeOrganizacionalAdm(), selecionado.getUnidadeOrganizacional(), selecionado.getDataLancamento());
            if (isOperacaoNovo()) {
                propostaConcessaoDiariaFacade.salvarNovo(selecionado);
                if (!TipoProposta.CONCESSAO_DIARIACAMPO.equals(selecionado.getTipoProposta())) {
                    FacesUtil.executaJavaScript("geraSol.show()");
                    FacesUtil.atualizarComponente("Formulario");
                } else {
                    FacesUtil.addOperacaoRealizada(" A " + metadata.getNomeEntidade() + " " + selecionado.toString() + " foi salva com sucesso.");
                    redireciona();
                }
            } else {
                propostaConcessaoDiariaFacade.salvar(selecionado);
                FacesUtil.addOperacaoRealizada(" A " + metadata.getNomeEntidade() + " " + selecionado.toString() + " foi alterada com sucesso.");
                redireciona();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            FacesUtil.atualizarComponente(":Formulario:tabView:calculados");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            FacesUtil.atualizarComponente(":Formulario:tabView:calculados");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarDiariaPorTipoProposta() {
        ValidacaoException ve = new ValidacaoException();
        if (TipoProposta.COLABORADOR_EVENTUAL.equals(selecionado.getTipoProposta())) {
            if (Strings.isNullOrEmpty(selecionado.getRazao())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Razão deve ser informado.");
            }
            if (selecionado.getDeclaracao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Declaração deve ser informado.");
            }
            if (selecionado.getQualificacaoColaborador() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Qualificação do Colaborador deve ser informado.");
            }
        }
        ve.lancarException();
    }

    private void validarDiariaNormalDadosPassagem() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCompradaPeloMunicipio() && TipoProposta.CONCESSAO_DIARIA.equals(selecionado.getTipoProposta())) {
            if (selecionado.getFornecedor() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Fornecedor deve ser informado.");
            }
            if (Strings.isNullOrEmpty(selecionado.getNumeroContrato())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Número de Contrato deve ser informado.");
            }
            if (selecionado.getValorPassagem().compareTo(BigDecimal.ZERO) == 0) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Valor da Passagem deve ser maior que zero(0).");
            }
            if (selecionado.getArquivos().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("É obrigatório ter pelo menos um Arquivo anexado.");
            }
        }
        ve.lancarException();
    }

    public void definirContaBancariaComoNull() {
        try {
            validarDiariasParaPessoa();
            selecionado.setClasseCredor(null);
            selecionado.setContaCorrenteBanc(null);
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabView:cc_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            selecionado.setPessoaFisica(null);
            selecionado.setClasseCredor(null);
        }
    }


    public void validarPessoa() {
        try {
            validarDiariasParaPessoa();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            selecionado.setPessoaFisica(null);
            selecionado.setClasseCredor(null);
        }
    }

    private void validarDiariasParaPessoa() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getPessoaFisica() != null && selecionado.getConfiguracaoDiaria() != null &&
            propostaConcessaoDiariaFacade.hasDiariasAComprovarPorPessoa(selecionado, selecionado.getConfiguracaoDiaria().getQuantidadeDiasCorridos(), selecionado.getConfiguracaoDiaria().getQuantidadeDiasUteis())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Existe(m) diária(s) que estão com a situação " + SituacaoPropostaConcessaoDiaria.A_COMPROVAR.getDescricao() + " para a pessoa selecionada.");
        }
        ve.lancarException();
    }

    public void adicionarDemonstrativoGastos() {
        try {
            diariaPrestacaoContaselecionada.realizarValidacoes();
            diariaPrestacaoContaselecionada.setPropostaConcessaoDiaria(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getDiariaPrestacaoContas(), diariaPrestacaoContaselecionada);
            diariaPrestacaoContaselecionada = new DiariaPrestacaoConta();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerDemonstrativoGastos(DiariaPrestacaoConta diaria) {
        selecionado.getDiariaPrestacaoContas().remove(diaria);
    }

    public void editarDemonstrativoGastos(DiariaPrestacaoConta diariaPrestacaoConta) {
        diariaPrestacaoContaselecionada = (DiariaPrestacaoConta) Util.clonarObjeto(diariaPrestacaoConta);
    }

    public List<PessoaFisica> completarPessoasFisicas(String parte) {
        if (TipoProposta.CONCESSAO_DIARIA.equals(selecionado.getTipoProposta())) {
            return propostaConcessaoDiariaFacade.getPessoaFacade().listaFiltrandoPessoaComVinculoVigenteEPorTipoClasse(parte.trim(), TipoClasseCredor.DIARIA_CIVIL);
        } else if (TipoProposta.CONCESSAO_DIARIACAMPO.equals(selecionado.getTipoProposta())) {
            return propostaConcessaoDiariaFacade.getPessoaFacade().listaFiltrandoPessoaComVinculoVigenteEPorTipoClasse(parte.trim(), TipoClasseCredor.DIARIA_CAMPO);
        } else {
            return propostaConcessaoDiariaFacade.getPessoaFacade().listaFiltrandoPessoaComVinculoVigenteEPorTipoClasse(parte.trim(), TipoClasseCredor.DIARIA_COLABORADOR);
        }
    }

    public List<Pessoa> completarFornecedores(String parte) {
        return propostaConcessaoDiariaFacade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public List<ContaCorrenteBancPessoa> completarContasCorrentes(String parte) {
        return propostaConcessaoDiariaFacade.getContaCorrenteBancPessoaFacade().listaContasBancariasPorPessoa(selecionado.getPessoaFisica(), parte.trim());
    }

    public List<SelectItem> getMeiosDeTransporte() {
        return Util.getListSelectItemSemCampoVazio(MeioDeTransporte.values(), false);
    }

    public List<ClasseCredor> completarClassesCredor(String parte) {
        if (TipoProposta.CONCESSAO_DIARIA.equals(selecionado.getTipoProposta())) {
            return propostaConcessaoDiariaFacade.getClasseCredorFacade().listaFiltrandoPorPessoaPorTipoClasse(parte.trim(), selecionado.getPessoaFisica(), TipoClasseCredor.DIARIA_CIVIL);
        } else if (TipoProposta.CONCESSAO_DIARIACAMPO.equals(selecionado.getTipoProposta())) {
            return propostaConcessaoDiariaFacade.getClasseCredorFacade().listaFiltrandoPorPessoaPorTipoClasse(parte.trim(), selecionado.getPessoaFisica(), TipoClasseCredor.DIARIA_CAMPO);
        } else {
            return propostaConcessaoDiariaFacade.getClasseCredorFacade().listaFiltrandoPorPessoaPorTipoClasse(parte.trim(), selecionado.getPessoaFisica(), TipoClasseCredor.DIARIA_COLABORADOR);
        }
    }

    public List<DespesaORC> completarDespesasORC(String parte) {
        if (TipoProposta.CONCESSAO_DIARIA.equals(selecionado.getTipoProposta())) {
            return propostaConcessaoDiariaFacade.getDespesaORCFacade().listaContaDespesaDaDiaria(parte.trim(), TipoContaDespesa.DIARIA_CIVIL, selecionado.getExercicio(), selecionado.getUnidadeOrganizacional());
        } else if (TipoProposta.CONCESSAO_DIARIACAMPO.equals(selecionado.getTipoProposta())) {
            return propostaConcessaoDiariaFacade.getDespesaORCFacade().listaContaDespesaDaDiaria(parte.trim(), TipoContaDespesa.DIARIA_CAMPO, selecionado.getExercicio(), selecionado.getUnidadeOrganizacional());
        } else {
            return propostaConcessaoDiariaFacade.getDespesaORCFacade().listaContaDespesaDaDiaria(parte.trim(), TipoContaDespesa.COLABORADOR_EVENTUAL, selecionado.getExercicio(), selecionado.getUnidadeOrganizacional());
        }
    }

    public List<SelectItem> getConfiguracoesDiaria() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, " "));
        for (ConfiguracaoDiaria c : propostaConcessaoDiariaFacade.getConfiguracaoDiariaFacade().listaConfiguracaoDiaria(selecionado.getDataLancamento())) {
            toReturn.add(new SelectItem(c, c.getLei().toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getClassesDiarias() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, " "));
        if (selecionado.getConfiguracaoDiaria() != null) {
            for (ClasseDiaria c : propostaConcessaoDiariaFacade.getConfiguracaoDiariaFacade().listaClassDiariaConfiguracaoAtivaPorConfiguracao(selecionado.getDataLancamento(), selecionado.getConfiguracaoDiaria())) {
                toReturn.add(new SelectItem(c, c.toStringClasseDiaria()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getTiposDeViagem() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (TipoViagem tipoViagem : TipoViagem.values()) {
            if (TipoProposta.CONCESSAO_DIARIACAMPO.equals(selecionado.getTipoProposta())) {
                if (TipoViagem.ESTADUAL.equals(tipoViagem)) {
                    toReturn.add(new SelectItem(tipoViagem, tipoViagem.getDescricao()));
                }
            } else {
                toReturn.add(new SelectItem(tipoViagem, tipoViagem.getDescricao()));
            }

        }
        return toReturn;
    }

    private void valorDaDiaria(ViagemDiaria viagemDiaria) {
        if (selecionado.getPropostaConcessaoDiaria() != null) {
            valorDaDiaria(viagemDiaria, selecionado.getPropostaConcessaoDiaria());
        } else {
            valorDaDiaria(viagemDiaria, selecionado);
        }
    }

    private void valorDaDiaria(ViagemDiaria viagemDiaria, PropostaConcessaoDiaria selecionado) {
        if (TipoProposta.CONCESSAO_DIARIACAMPO.equals(selecionado.getTipoProposta())) {
            valorDaDiaria = selecionado.getClasseDiaria().getValorEstadual();
        } else {
            if (TipoViagem.ESTADUAL.equals(viagemDiaria.getTipoViagem())) {
                valorDaDiaria = selecionado.getClasseDiaria().getValorEstadual();
            } else if (TipoViagem.INTERNACIONAL.equals(viagemDiaria.getTipoViagem())) {
                valorDaDiaria = selecionado.getClasseDiaria().getValorInternacional();
                if (valorIndicador != null) {
                    valorDaDiaria = valorDaDiaria.multiply(valorIndicador);
                }
            } else {
                valorDaDiaria = selecionado.getClasseDiaria().getValorNacional();
            }
        }
        if (selecionado.getTipoDespesaCusteadaTerceiro() != null) {
            valorDaDiaria = valorDaDiaria.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
        }
    }

    public boolean canGerarSolicitacaoDeEmpenho() {
        if (selecionado != null && selecionado.getFonteDespesaORC() != null) {
            BigDecimal saldo = propostaConcessaoDiariaFacade.getFonteDespesaORCFacade().saldoRealPorFonte(selecionado.getFonteDespesaORC(), UtilRH.getDataOperacao(), selecionado.getUnidadeOrganizacional());
            return selecionado.getValor().compareTo(saldo) <= 0;
        }
        return true;
    }

    public void validarSaldoFonteDespesaOrc(FacesContext context, UIComponent component, Object value) {
        FonteDespesaORC fd = (FonteDespesaORC) value;
        if (fd.getId() != null) {
            BigDecimal saldo = propostaConcessaoDiariaFacade.getFonteDespesaORCFacade().saldoRealPorFonte(fd, UtilRH.getDataOperacao(), selecionado.getUnidadeOrganizacional());
            if (selecionado.getValor().compareTo(saldo) <= 0) {
                liberaReserva = false;
            }
        }
    }

    public List<FonteDespesaORC> completarFontesDeDespesaORC(String parte) {
        if (selecionado.getDespesaORC().getId() != null) {
            return propostaConcessaoDiariaFacade.getFonteDespesaORCFacade().completaFonteDespesaORC(parte.trim(), selecionado.getDespesaORC());
        } else {
            return Lists.newArrayList();
        }
    }

    public ConverterAutoComplete getConverterFonteDespesaORC() {
        if (converterFonteDespesaORC == null) {
            converterFonteDespesaORC = new ConverterAutoComplete(FonteDespesaORC.class, propostaConcessaoDiariaFacade.getFonteDespesaORCFacade());
        }
        return converterFonteDespesaORC;
    }

    public List<SelectItem> getTipos() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(TipoProposta.CONCESSAO_DIARIA, TipoProposta.CONCESSAO_DIARIA.getDescricao()));
        toReturn.add(new SelectItem(TipoProposta.CONCESSAO_DIARIACAMPO, TipoProposta.CONCESSAO_DIARIACAMPO.getDescricao()));
        toReturn.add(new SelectItem(TipoProposta.COLABORADOR_EVENTUAL, TipoProposta.COLABORADOR_EVENTUAL.getDescricao()));
        return toReturn;
    }

    public BigDecimal getTotalPrestacaoContas() {
        BigDecimal total = BigDecimal.ZERO;
        for (DiariaPrestacaoConta dpc : selecionado.getDiariaPrestacaoContas()) {
            total = total.add(dpc.getValor());
        }
        return total;
    }

    public DiariaPrestacaoConta getDiariaPrestacaoContaselecionada() {
        return diariaPrestacaoContaselecionada;
    }

    public void setDiariaPrestacaoContaselecionada(DiariaPrestacaoConta diariaPrestacaoContaselecionada) {
        this.diariaPrestacaoContaselecionada = diariaPrestacaoContaselecionada;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Boolean getGeraReserva() {
        return geraReserva;
    }

    public void setGeraReserva(Boolean geraReserva) {
        this.geraReserva = geraReserva;
    }

    public Boolean getLiberaReserva() {
        return liberaReserva;
    }

    public void setLiberaReserva(Boolean liberaReserva) {
        this.liberaReserva = liberaReserva;
    }

    private void validarDeferirDiaria() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getAtoLegal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ato Legal deve ser informado.");
        }
        if (selecionado.getResponsavel() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Responsável deve ser informado.");
        }
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Valor deve ser maior que zero(0).");
        }
        if (selecionado.getAprovado()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma solicitação de empenho para esta diária.");
        }
        ve.lancarException();
    }

    public void calcularValorDiariaAoAbrirDialogDeferir() {
        try {
            validarDiaria();
            calcularValorDiariaTotal();
            FacesUtil.executaJavaScript("geraSol.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void calcularValorDiariaTotal() {
        BigDecimal totalViagem = BigDecimal.ZERO;
        for (ViagemDiaria viagem : selecionado.getViagens()) {
            totalViagem = totalViagem.add(viagem.getValorTotal());
        }
        selecionado.setValor(totalViagem);
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            Arquivo a = new Arquivo();
            a.setMimeType(event.getFile().getContentType());
            a.setNome(event.getFile().getFileName());
            a.setDescricao(event.getFile().getFileName());
            a.setTamanho(event.getFile().getSize());
            a.setInputStream(event.getFile().getInputstream());
            a = propostaConcessaoDiariaFacade.getArquivoFacade().novoArquivoMemoria(a);

            arquivo.setArquivo(a);
        } catch (Exception ex) {
            logger.error("Ocorreu um erro ao importar o arquivo: {}", ex);
        }
    }

    public void adicionarArquivo() {
        try {
            Util.validarCampos(arquivo);
            if (arquivo.getDespesaCusteadaTerceiro()) {
                Util.adicionarObjetoEmLista(selecionado.getArquivosCusteadosTeceiros(), arquivo);
            } else {
                Util.adicionarObjetoEmLista(selecionado.getArquivos(), arquivo);
            }
            inicializarArquivo(arquivo.getDespesaCusteadaTerceiro());
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        }
    }

    public void removerArquivo(DiariaArquivo arquivo) {
        selecionado.getArquivos().remove(arquivo);
    }

    public void deferirDiaria() {
        try {
            validarDeferirDiaria();
            calcularValorDiariaTotal();
            selecionado.setSituacao(SituacaoDiaria.DEFERIDO);
            RequestContext.getCurrentInstance().execute("geraSol.hide()");
            propostaConcessaoDiariaFacade.geraSolicitacaoEmpenho(selecionado, selecionado.getFonteDespesaORC(), geraReserva);
            if (geraReserva) {
                FacesUtil.addOperacaoRealizada(" A " + metadata.getNomeEntidade() + " " + selecionado.toString() + " foi deferida com reserva de dotação orçamentária no valor de " + Util.formataValor(selecionado.getValor()));
            } else {
                FacesUtil.addOperacaoRealizada(" A " + metadata.getNomeEntidade() + " " + selecionado.toString() + " foi deferida com sucesso.");
            }
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void indeferirDiaria() {
        try {
            propostaConcessaoDiariaFacade.indeferirDiaria(selecionado);
            FacesUtil.addOperacaoRealizada(" A " + metadata.getNomeEntidade() + " " + selecionado.toString() + " foi Indeferida com sucesso.");
            redireciona();
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public List<SelectItem> getIndicadoresEnconomicos() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (IndicadorEconomico indicador : propostaConcessaoDiariaFacade.getIndicadorEconomicoFacade().buscarIndicadorEnconomicoPorPeriodicidadeAndTipoIndicador(PeriodicidadeIndicador.DIARIA, TipoIndicador.MOEDA)) {
            toReturn.add(new SelectItem(indicador, indicador.getNome() + " (" + indicador.getSigla() + ")"));
            if (isOperacaoEditar()) {
                indicadorEconomico = indicador;
            }
        }
        return toReturn;
    }

    public void definirIndicadorEconomicoComoNull() {
        if (viagemDiaria.isDiariaInterestadual()
            || viagemDiaria.isDiariaEstadual()) {
            indicadorEconomico = null;
        }
    }

    public List<ValorIndicadorEconomico> definirValoresIndicadorEconomico() {
        try {
            if (indicadorEconomico != null) {
                valoresIndicadorEconomico = propostaConcessaoDiariaFacade.getIndicadorEconomicoFacade().burcarValorIndicadorPorIndicadorEconomico(indicadorEconomico);
            }
            for (ValorIndicadorEconomico vie : valoresIndicadorEconomico) {
                if (Util.getDataHoraMinutoSegundoZerado(selecionado.getDataLancamento()).equals(Util.getDataHoraMinutoSegundoZerado(vie.getInicioVigencia()))) {
                    valorIndicador = new BigDecimal(vie.getValor());
                    break;
                } else {
                    valorIndicador = null;
                }
            }
            if (valorIndicador == null) {
                FacesUtil.addAtencao("Nenhum Indicador Econômico vigente encontrado na data de lançamento da diária. Consulte o cadastro de Indicador Econômico para regularizar.");
            }
            FacesUtil.executaJavaScript("painelInfoValores.show()");
            return valoresIndicadorEconomico;
        } catch (Exception ex) {
            indicadorEconomico = null;
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
        return null;
    }

    public Boolean renderizarValoresIndicador() {
        return indicadorEconomico != null;
    }

    public List<SelectItem> getSituacoes() {
        return Util.getListSelectItemSemCampoVazio(SituacaoCadastralContabil.values(), false);
    }

    private boolean isDataInicioAndFimEqual(ViagemDiaria viagemDiaria) {
        return viagemDiaria.getDataInicialSaida() != null &&
            viagemDiaria.getDataFinalRetorno() != null &&
            viagemDiaria.getDataInicialSaida().compareTo(viagemDiaria.getDataFinalRetorno()) == 0;
    }

    private boolean isPartidaAndRetornoEntreHoras(ViagemDiaria viagemDiaria) {
        Integer partida = Integer.parseInt(viagemDiaria.getHoraInicialSaida());
        Integer chegada = Integer.parseInt(viagemDiaria.getHoraFinalRetorno());
        return partida.compareTo(ZERO_HORA) >= 0 && partida.compareTo(SEIS_HORAS) <= 0 && chegada.compareTo(DEZOITO_HORAS) >= 0;
    }

    public void calcularValorDiaria() {
        try {
            validarViagemDiaria();
            valorDaDiaria(viagemDiaria);
            calcularDiaria(viagemDiaria);
        } catch (ValidacaoException ve) {
            atualizarComponenteCalculados();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida("Não foi encontrada uma cotação do dolar vigente para esta data");
        }
    }

    public List<DiariaArquivo> buscarArquivosDiaria() {
        if (arquivo != null && arquivo.getDespesaCusteadaTerceiro()) {
            return selecionado.getArquivosCusteadosTeceiros();
        } else {
            return selecionado.getArquivos();
        }
    }

    private void atualizarComponenteCalculados() {
        FacesUtil.atualizarComponente(":Formulario:calculados");
    }

    private void validarDiaria() {
        selecionado.realizarValidacoes();
        validarDiariaPorTipoProposta();
        validarDespesasCusteadas();
        validarDiariaDeCampo();
        validarJustificativa();
    }

    private void validarJustificativa() {
        if (isOperacaoEditar()) {
            ValidacaoException ve = new ValidacaoException();
            if (selecionado.getJustificativa().isEmpty() &&
                (isNotNullAndNotEquals(selecionado.getInicioExec(), selecionadoClone.getInicioExec()) ||
                    isNotNullAndNotEquals(selecionado.getFimExec(), selecionadoClone.getFimExec()) ||
                    isNotNullAndNotEquals(selecionado.getHoraChegadaExecutado(), selecionadoClone.getHoraChegadaExecutado()) ||
                    isNotNullAndNotEquals(selecionado.getMinutoChegadaExecutado(), selecionadoClone.getMinutoChegadaExecutado()) ||
                    isNotNullAndNotEquals(selecionado.getHoraSaidaExecutado(), selecionadoClone.getHoraSaidaExecutado()) ||
                    isNotNullAndNotEquals(selecionado.getMinutoSaidaExecutado(), selecionadoClone.getMinutoSaidaExecutado()))) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Justificativa é obrigatório pois houve alteração na data/hora de saída/retorno na aba Relatório de Viagem.");
            }
            ve.lancarException();
        }
    }

    private boolean isNotNullAndNotEquals(Object primeiroCampo, Object segundoCampo) {
        return (primeiroCampo == null && segundoCampo != null) ||
            (primeiroCampo != null && segundoCampo == null) ||
            (primeiroCampo != null && !primeiroCampo.equals(segundoCampo));
    }

    private void validarDiariaDeCampo() {
        ValidacaoException ve = new ValidacaoException();
        if (TipoProposta.CONCESSAO_DIARIACAMPO.equals(selecionado.getTipoProposta())) {
            Integer quantidadeDiarias = propostaConcessaoDiariaFacade.buscarQuantidadeDiariasMaiorQueParametro(selecionado);
            Integer quantidadeDiariasParametro = propostaConcessaoDiariaFacade.getConfiguracaoContabilFacade().configuracaoContabilVigente().getQuantidadeDiariasDeCampo();
            if (quantidadeDiarias >= quantidadeDiariasParametro) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe(m) " + quantidadeDiarias + " diária(s) de campo para a pessoa " + selecionado.getPessoaFisica() + " no mês " + DataUtil.getMes(selecionado.getDataLancamento()));
            }
        }
        ve.lancarException();
    }

    private void validarDespesasCusteadas() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoDespesaCusteadaTerceiro() != null && selecionado.getArquivosCusteadosTeceiros().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar pelo menos 1(um) anexo na aba 'Despesas Custeadas por Terceiros'.");
        }
        ve.lancarException();
    }

    private void calcularDiaria(ViagemDiaria viagemDiaria) {
        viagemDiaria.setTotalIntegral(BigDecimal.ZERO);
        viagemDiaria.setTotalValorMeia(BigDecimal.ZERO);
        if (isDataInicioAndFimEqual(viagemDiaria)) {
            if (isPartidaAndRetornoEntreHoras(viagemDiaria)) {
                viagemDiaria.setQuantidadeDiarias(1D);
            } else {
                viagemDiaria.setQuantidadeDiarias(0.5D);
            }
        } else {
            Date saida = criarDataSaida(viagemDiaria);
            Date chegada = criarDataChegada(viagemDiaria);
            Integer diffHours = DataUtil.horasEntre(saida, chegada);
            if (diffHours < VINTE_QUATRO_HORAS) {
                viagemDiaria.setQuantidadeDiarias(1D);
            } else {
                viagemDiaria.setQuantidadeDiarias(getDias(viagemDiaria));
                if (DataUtil.diferencaDiasInteira(viagemDiaria.getDataInicialRetorno(), viagemDiaria.getDataFinalRetorno()) == 0) {
                    viagemDiaria.setQuantidadeDiarias(viagemDiaria.getQuantidadeDiarias() + 0.5D);
                }
            }
        }
        viagemDiaria.setTotalIntegral(valorDaDiaria.multiply(BigDecimal.valueOf(viagemDiaria.getQuantidadeDeDias())).setScale(2, RoundingMode.HALF_EVEN));
        if (viagemDiaria.hasMeiaDiaria()) {
            viagemDiaria.setTotalValorMeia(valorDaDiaria.multiply(new BigDecimal("0.5")).setScale(2, RoundingMode.HALF_EVEN));
        }
    }

    private Date criarDataChegada(ViagemDiaria viagemDiaria) {
        Calendar chegada = Calendar.getInstance();
        chegada.setTime(viagemDiaria.getDataFinalRetorno());
        chegada.set(Calendar.HOUR_OF_DAY, Integer.parseInt(viagemDiaria.getHoraFinalRetorno()));
        chegada.set(Calendar.MINUTE, Integer.parseInt(viagemDiaria.getMinutoFinalRetorno()));
        return chegada.getTime();
    }

    private Date criarDataSaida(ViagemDiaria viagemDiaria) {
        Calendar saida = Calendar.getInstance();
        saida.setTime(viagemDiaria.getDataInicialSaida());
        saida.set(Calendar.HOUR_OF_DAY, Integer.parseInt(viagemDiaria.getHoraInicialSaida()));
        saida.set(Calendar.MINUTE, Integer.parseInt(viagemDiaria.getMinutoInicialSaida()));
        return saida.getTime();
    }

    public Double getDias(ViagemDiaria viagem) {
        return ((Integer) DataUtil.diferencaDiasInteira(viagem.getDataInicialSaida(), viagem.getDataFinalRetorno())).doubleValue();
    }


    public Boolean renderizarBotaoDeferir() {
        return !TipoProposta.CONCESSAO_DIARIACAMPO.equals(selecionado.getTipoProposta())
            && selecionado.getId() != null
            && SituacaoDiaria.ABERTO.equals(selecionado.getSituacao());
    }


    public Boolean renderizarBotaoIndeferir() {
        return (selecionado.getId() != null
            && SituacaoDiaria.DEFERIDO.equals(selecionado.getSituacao())
            && hasVinculoDiariaComEmpenho());
    }

    private Boolean hasVinculoDiariaComEmpenho() {
        return !propostaConcessaoDiariaFacade.verificarVinculoDiariaComEmpenho(selecionado);
    }

    public BigDecimal getSomaEstornoPagamentos() {
        BigDecimal estornos = new BigDecimal(BigInteger.ZERO);
        for (PagamentoEstorno pe : getPagamentosEstorno()) {
            estornos = estornos.add(pe.getValor());
        }
        return estornos;
    }

    public BigDecimal getSomaPagamentos() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (Pagamento p : getPagamentos()) {
            valor = valor.add(p.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaLiquidacoes() {
        BigDecimal liq = new BigDecimal(BigInteger.ZERO);
        for (Liquidacao l : getLiquidacoes()) {
            liq = liq.add(l.getValor());
        }
        return liq;
    }

    public BigDecimal getSomaEstornoLiquidacoes() {
        BigDecimal liq = new BigDecimal(BigInteger.ZERO);
        for (LiquidacaoEstorno le : getLiquidacoesEstorno()) {
            liq = liq.add(le.getValor());
        }
        return liq;
    }

    public BigDecimal getSomaEmpenhos() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (Empenho e : getEmpenhos()) {
            valor = valor.add(e.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaEstornoEmpenhos() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (EmpenhoEstorno ee : getEmpenhosEstorno()) {
            valor = valor.add(ee.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaBaixasNormal() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (DiariaContabilizacao dc : getDiariasContabilizacaoBaixaNormal()) {
            valor = valor.add(dc.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaBaixasEstorno() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (DiariaContabilizacao dc : getDiariasContabilizacaoBaixaEstorno()) {
            valor = valor.add(dc.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaInscricoesNormal() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (DiariaContabilizacao dc : getDiariasContabilizacaoInscricaoNormal()) {
            valor = valor.add(dc.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaInscricoesEstorno() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (DiariaContabilizacao dc : getDiariasContabilizacaoInscricaoEstorno()) {
            valor = valor.add(dc.getValor());
        }
        return valor;
    }

    public boolean isDiariaColaboradorEventual() {
        return TipoProposta.COLABORADOR_EVENTUAL.equals(selecionado.getTipoProposta());
    }

    public boolean isDiariaNormal() {
        return TipoProposta.CONCESSAO_DIARIA.equals(selecionado.getTipoProposta());
    }

    public void uploadArquivoDeclaracao(FileUploadEvent event) {
        try {
            Arquivo arquivo = new Arquivo();
            arquivo.setMimeType(propostaConcessaoDiariaFacade.getArquivoFacade().getMimeType(event.getFile().getFileName()));
            arquivo.setNome(event.getFile().getFileName());
            arquivo.setTamanho(event.getFile().getSize());
            arquivo.setDescricao(event.getFile().getFileName());
            arquivo.setInputStream(event.getFile().getInputstream());

            selecionado.setDeclaracao(arquivo);
        } catch (Exception ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Erro ao anexar arquivo: " + ex.getMessage());
        }
    }

    public void uploadArquivoQualificacao(FileUploadEvent event) {
        try {
            Arquivo arquivo = new Arquivo();
            arquivo.setMimeType(propostaConcessaoDiariaFacade.getArquivoFacade().getMimeType(event.getFile().getFileName()));
            arquivo.setNome(event.getFile().getFileName());
            arquivo.setTamanho(event.getFile().getSize());
            arquivo.setDescricao(event.getFile().getFileName());
            arquivo.setInputStream(event.getFile().getInputstream());

            selecionado.setQualificacaoColaborador(arquivo);

        } catch (Exception ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Erro ao anexar arquivo: " + ex.getMessage());
        }
    }

    public void visualizarArquivo(Arquivo arquivo) {
        FacesUtil.redirecionamentoInterno("/arquivos?id=" + arquivo.getId());
    }

    public void atribuirCidadeOrigemNoItinerario() {
        if (cidadeOrigem != null) {
            viagemDiaria.setItinerario("Origem: " + cidadeOrigem.toString());
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabView:cidadeDestino_input')");
        }
        if (cidadeOrigem != null && cidadeDestino != null) {
            viagemDiaria.setItinerario("Origem: " + cidadeOrigem.toString() + ", Destino: " + cidadeDestino.toString());
        }
    }

    public void atribuirCidadeDestinoNoItinerario() {
        if (cidadeDestino != null && cidadeOrigem != null) {
            viagemDiaria.setItinerario("Origem: " + cidadeOrigem.toString() + ", Destino: " + cidadeDestino.toString());
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabView:etinerario')");
        } else {
            FacesUtil.addAtencao("Selecione a Cidade de Origem para completar o Itinerário.");
        }
    }

    public void gerarRelatorioDiaria() {
        try {
            RelatorioDTO dto = montarDtoSemNomeRelatorio();
            dto.adicionarParametro("isRelatorioViagem", false);
            dto.setNomeRelatorio(getNomeArquivoParaImprimir());
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorioViagem() {
        try {
            RelatorioDTO dto = montarDtoSemNomeRelatorio();
            dto.adicionarParametro("isRelatorioViagem", true);
            dto.setNomeRelatorio("RELATORIO-VIAGEM");
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private RelatorioDTO montarDtoSemNomeRelatorio() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USER", sistemaControlador.getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "Muncípio de Rio Branco - AC");
        dto.adicionarParametro("MODULO", "Execução Orçamentária");
        dto.adicionarParametro("idDiaria", selecionado.getId());
        dto.setApi("contabil/relatorio-diaria/");
        return dto;
    }

    private String getNomeArquivoParaImprimir() {
        return TipoProposta.CONCESSAO_DIARIA.equals(selecionado.getTipoProposta()) ? "RELATORIO-DIARIA - DIARIA-CIVIL" : "RELATORIO-DIARIA - COLABORADOR-EVENTUAL";
    }

    public void atribuirFocoNoCampoFonteRecurso() {
        FacesUtil.executaJavaScript("setaFoco('Formulario:tabView:completaFonte_input')");
    }

    public ConverterGenerico getConverterClasseDiaria() {
        if (converterClasseDiaria == null) {
            converterClasseDiaria = new ConverterGenerico(ClasseDiaria.class, propostaConcessaoDiariaFacade.getConfiguracaoDiariaFacade());
        }
        return converterClasseDiaria;
    }

    public ConverterAutoComplete getConverterContaCorrente() {
        if (converterContaCorrente == null) {
            converterContaCorrente = new ConverterAutoComplete(ContaCorrenteBancPessoa.class, propostaConcessaoDiariaFacade.getContaCorrenteBancPessoaFacade());
        }
        return converterContaCorrente;
    }

    public ConverterAutoComplete getConverterContaDespesa() {
        if (converterDespesaORC == null) {
            converterDespesaORC = new ConverterAutoComplete(DespesaORC.class, propostaConcessaoDiariaFacade.getDespesaORCFacade());
        }
        return converterDespesaORC;
    }

    public IndicadorEconomico getIndicadorEconomico() {
        return indicadorEconomico;
    }

    public void setIndicadorEconomico(IndicadorEconomico indicadorEconomico) {
        this.indicadorEconomico = indicadorEconomico;
    }

    public List<ValorIndicadorEconomico> getValoresIndicadorEconomico() {
        return valoresIndicadorEconomico;
    }

    public void setValoresIndicadorEconomico(List<ValorIndicadorEconomico> valoresIndicadorEconomico) {
        this.valoresIndicadorEconomico = valoresIndicadorEconomico;
    }

    public BigDecimal getValorIndicador() {
        return valorIndicador;
    }

    public void setValorIndicador(BigDecimal valorIndicador) {
        this.valorIndicador = valorIndicador;
    }

    public BigDecimal calcularTotalDaDespesa() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (ViagemDiaria viagem : selecionado.getViagens()) {
            retorno = retorno.add(viagem.getValorTotal());
        }
        return retorno;
    }

    public BigDecimal calcularTotalIntegral() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (ViagemDiaria viagem : selecionado.getViagens()) {
            retorno = retorno.add(viagem.getTotalIntegral());
        }
        return retorno;
    }


    public BigDecimal calcularTotalValorMeia() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (ViagemDiaria viagem : selecionado.getViagens()) {
            retorno = retorno.add(viagem.getTotalValorMeia());
        }
        return retorno;
    }

    public void removerDeclaracao() {
        selecionado.setDeclaracao(null);
    }

    public void removerQualificacao() {
        selecionado.setQualificacaoColaborador(null);
    }

    public Cidade getCidadeOrigem() {
        return cidadeOrigem;
    }

    public void setCidadeOrigem(Cidade cidadeOrigem) {
        this.cidadeOrigem = cidadeOrigem;
    }

    public Cidade getCidadeDestino() {
        return cidadeDestino;
    }

    public void setCidadeDestino(Cidade cidadeDestino) {
        this.cidadeDestino = cidadeDestino;
    }

    public DiariaArquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(DiariaArquivo arquivo) {
        this.arquivo = arquivo;
    }

    public ViagemDiaria getViagemDiaria() {
        return viagemDiaria;
    }

    public void setViagemDiaria(ViagemDiaria viagemDiaria) {
        this.viagemDiaria = viagemDiaria;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/diaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return propostaConcessaoDiariaFacade;
    }
}
