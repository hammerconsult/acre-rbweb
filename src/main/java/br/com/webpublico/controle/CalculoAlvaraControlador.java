package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CalculoAlvaraFacade;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoCalculoAlvara", pattern = "/calculo-alvara/novo/", viewId = "/faces/tributario/alvara/calculo/edita.xhtml"),
    @URLMapping(id = "editaCalculoAlvara", pattern = "/calculo-alvara/editar/#{calculoAlvaraControlador.id}/", viewId = "/faces/tributario/alvara/calculo/edita.xhtml"),
    @URLMapping(id = "calcularAlvaraNotificacao", pattern = "/calculo-alvara/notificacao/#{calculoAlvaraControlador.idCadastro}/", viewId = "/faces/tributario/alvara/calculo/edita.xhtml"),
    @URLMapping(id = "verCalculoAlvara", pattern = "/calculo-alvara/ver/#{calculoAlvaraControlador.id}/", viewId = "/faces/tributario/alvara/calculo/visualizar.xhtml"),
    @URLMapping(id = "listarCalculoAlvara", pattern = "/calculo-alvara/listar/", viewId = "/faces/tributario/alvara/calculo/lista.xhtml"),
    @URLMapping(id = "emissaoAlvara", pattern = "/alvara/emissao/", viewId = "/faces/tributario/alvara/emissao/edita.xhtml")
})
public class CalculoAlvaraControlador extends PrettyControlador<ProcessoCalculoAlvara> implements CRUD {

    @EJB
    private CalculoAlvaraFacade calculoAlvaraFacade;
    private final Map<CalculoAlvara, String> estilosCalculo;
    private ConfiguracaoTributario configuracao;
    private EnquadramentoFiscal enquadramentoFiscal;
    private UsuarioSistema usuarioEstorno;
    private VOAlvara alvaraSelecionado;
    private Alvara alvara;
    private ReciboImpressaoAlvara recibo;
    private String motivoEstorno;
    private String protocoloRedeSim;
    private String emails;
    private String msgEmail;
    private BigDecimal valor;
    private Date dataEstorno;
    private TipoAlvara tipoAlvara;
    private List<EconomicoCNAE> cnaes;
    private List<EconomicoCNAE> cnaesMesmoCodigo;
    private List<EconomicoCNAE> cnaesSelecionados;
    private List<Telefone> telefones;
    private List<DAMResultadoParcela> parcelas;
    private List<DAMResultadoParcela> parcelasCanceladas;
    private List<VOAlvara> alvarasEmissao;
    private Future<AssistenteEfetivarProcesso> futureAssistenteEfetivarProcesso;
    private AssistenteEfetivarProcesso assistenteEfetivarProcesso;
    private Alvara alvaraRedeSim;
    private Map<Long, BigDecimal> mapaUfmBigDecimalCalculos;
    private List<CalculoAlvara> calculosAnteriores;
    private List<CalculoAlvara> calculosAtuais;
    private Boolean isAtualizacao;
    private Boolean gerarTaxaLocalizacao;
    private Long idCadastro;
    private VOConfirmaTaxaDeLocalizacao confirmaTaxaDeLocalizacao;
    private EnderecoCalculoAlvara enderecoAtualizado;
    private Boolean primeiroCalculo;

    private Map<Long, AgrupadorVORevisaoCalculoLocalizacao> alteracoesPorCalculoLocalizacao;
    private AgrupadorVORevisaoCalculoLocalizacao alteracaoSelecionada;

    public CalculoAlvaraControlador() {
        super(ProcessoCalculoAlvara.class);
        selecionado = new ProcessoCalculoAlvara();
        estilosCalculo = Maps.newHashMap();
    }

    public Long getIdCadastro() {
        return idCadastro;
    }

    public void setIdCadastro(Long idCadastro) {
        this.idCadastro = idCadastro;
    }

    @Override
    public AbstractFacade getFacede() {
        return calculoAlvaraFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/calculo-alvara/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public EnquadramentoFiscal getEnquadramentoFiscal() {
        return enquadramentoFiscal;
    }

    public void setEnquadramentoFiscal(EnquadramentoFiscal enquadramentoFiscal) {
        this.enquadramentoFiscal = enquadramentoFiscal;
    }

    public List<EconomicoCNAE> getCnaes() {
        return cnaes;
    }

    public void setCnaes(List<EconomicoCNAE> cnaes) {
        this.cnaes = cnaes;
    }

    public Boolean getPrimeiroCalculo() {
        if (primeiroCalculo == null) {
            primeiroCalculo = false;
        }
        return primeiroCalculo;
    }

    public void setPrimeiroCalculo(Boolean primeiroCalculo) {
        this.primeiroCalculo = primeiroCalculo;
    }

    public List<Telefone> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<Telefone> telefones) {
        this.telefones = telefones;
    }

    public List<EconomicoCNAE> getCnaesMesmoCodigo() {
        return cnaesMesmoCodigo;
    }

    public void setCnaesMesmoCodigo(List<EconomicoCNAE> cnaesMesmoCodigo) {
        this.cnaesMesmoCodigo = cnaesMesmoCodigo;
    }

    public String getMotivoEstorno() {
        return motivoEstorno;
    }

    public void setMotivoEstorno(String motivoEstorno) {
        this.motivoEstorno = motivoEstorno;
    }

    public UsuarioSistema getUsuarioEstorno() {
        return usuarioEstorno;
    }

    public void setUsuarioEstorno(UsuarioSistema usuarioEstorno) {
        this.usuarioEstorno = usuarioEstorno;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public List<DAMResultadoParcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<DAMResultadoParcela> parcelas) {
        this.parcelas = parcelas;
    }

    public List<DAMResultadoParcela> getParcelasCanceladas() {
        return parcelasCanceladas;
    }

    public void setParcelasCanceladas(List<DAMResultadoParcela> parcelasCanceladas) {
        this.parcelasCanceladas = parcelasCanceladas;
    }

    public VOAlvara getAlvaraSelecionado() {
        return alvaraSelecionado;
    }

    public void setAlvaraSelecionado(VOAlvara alvaraSelecionado) {
        this.alvaraSelecionado = alvaraSelecionado;
    }

    public Alvara getAlvara() {
        return alvara;
    }

    public void setAlvara(Alvara alvara) {
        this.alvara = alvara;
    }

    public ReciboImpressaoAlvara getRecibo() {
        return recibo;
    }

    public void setRecibo(ReciboImpressaoAlvara recibo) {
        this.recibo = recibo;
    }

    public List<VOAlvara> getAlvarasEmissao() {
        return alvarasEmissao;
    }

    public void setAlvarasEmissao(List<VOAlvara> alvarasEmissao) {
        this.alvarasEmissao = alvarasEmissao;
    }

    public AssistenteEfetivarProcesso getAssistenteEfetivarProcesso() {
        return assistenteEfetivarProcesso;
    }

    public void setAssistenteEfetivarProcesso(AssistenteEfetivarProcesso assistenteEfetivarProcesso) {
        this.assistenteEfetivarProcesso = assistenteEfetivarProcesso;
    }

    public TipoAlvara getTipoAlvara() {
        return tipoAlvara;
    }

    public void setTipoAlvara(TipoAlvara tipoAlvara) {
        this.tipoAlvara = tipoAlvara;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public String getMsgEmail() {
        return msgEmail;
    }

    public void setMsgEmail(String msgEmail) {
        this.msgEmail = msgEmail;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<CalculoAlvara> getCalculosAnteriores() {
        return calculosAnteriores;
    }

    public void setCalculosAnteriores(List<CalculoAlvara> calculosAnteriores) {
        this.calculosAnteriores = calculosAnteriores;
    }

    public List<CalculoAlvara> getCalculosAtuais() {
        if (calculosAtuais == null) calculosAtuais = Lists.newArrayList();
        return calculosAtuais;
    }

    public void setCalculosAtuais(List<CalculoAlvara> calculosAtuais) {
        this.calculosAtuais = calculosAtuais;
    }

    public VOConfirmaTaxaDeLocalizacao getConfirmaTaxaDeLocalizacao() {
        return confirmaTaxaDeLocalizacao;
    }

    public void setConfirmaTaxaDeLocalizacao(VOConfirmaTaxaDeLocalizacao confirmaTaxaDeLocalizacao) {
        this.confirmaTaxaDeLocalizacao = confirmaTaxaDeLocalizacao;
    }

    public List<EnderecoCalculoAlvara> getEnderecosAtualizados() {
        return enderecoAtualizado != null ? Lists.newArrayList(enderecoAtualizado) : Lists.<EnderecoCalculoAlvara>newArrayList();
    }

    public AgrupadorVORevisaoCalculoLocalizacao getAlteracaoSelecionada() {
        return alteracaoSelecionada;
    }

    public void setAlteracaoSelecionada(AgrupadorVORevisaoCalculoLocalizacao alteracaoSelecionada) {
        this.alteracaoSelecionada = alteracaoSelecionada;
    }

    @URLAction(mappingId = "calcularAlvaraNotificacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoCalculoAlvaraNotificacao() {
        if (idCadastro != null) {
            selecionado = calculoAlvaraFacade.buscarProcessoCalculoAlvaraPorExercicio(idCadastro, null);
            if (selecionado == null) {
                novo();
                selecionado.setCadastroEconomico(calculoAlvaraFacade.recuperarCmc(idCadastro));
                preencherDadosCalculo();
            } else {
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            }
        }
    }

    @URLAction(mappingId = "novoCalculoAlvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarDados();
        selecionado.setDataLancamento(new Date());
        selecionado.setUsuario(calculoAlvaraFacade.recuperarUsuarioCorrente());
        selecionado.setSituacaoCalculoAlvara(SituacaoCalculoAlvara.EM_ABERTO);
        selecionado.setExercicio(calculoAlvaraFacade.recuperarExercicioCorrente());
    }

    @URLAction(mappingId = "editaCalculoAlvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        operacao = Operacoes.EDITAR;
        selecionado = calculoAlvaraFacade.recuperar(getId());
        isAtualizacao = false;
        gerarTaxaLocalizacao = null;
        if (!isAlvaraEmAberto(selecionado)) {
            FacesUtil.addOperacaoNaoPermitida("O Cálculo de Alvará selecionado já foi calculado. Portanto não pode mais ser alterado.");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } else {
            preencherEnquadramentoFiscal();
            preencherTelefones();
        }
    }

    @URLAction(mappingId = "verCalculoAlvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        selecionado = calculoAlvaraFacade.recuperar(getId());
        inicializarDadosVer();
    }

    @URLAction(mappingId = "emissaoAlvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void emitir() {
        alvarasEmissao = Lists.newArrayList();
        parcelas = Lists.newArrayList();
        configuracao = calculoAlvaraFacade.buscarConfiguracaoTriutarioVigente();
    }

    private void inicializarDadosVer() {
        preencherTelefones();
        parcelas = Lists.newArrayList();
        parcelasCanceladas = Lists.newArrayList();
        usuarioEstorno = calculoAlvaraFacade.recuperarUsuarioCorrente();
        dataEstorno = new Date();
        calculosAtuais = buscarCalculosVer();
        calculosAnteriores = selecionado.buscarCalculosCancelados();
        isAtualizacao = false;
        gerarTaxaLocalizacao = null;

        if (!canHabilitarBotaoEfetivarCalculo()) {
            selecionado = calculoAlvaraFacade.atualizarValorTotalCalculado(selecionado);
        }

        preencherEnquadramentoFiscal();
        ordenarCalculos(calculosAtuais);
        ordenarCalculos(calculosAnteriores);
        ordenarLogs();
        selecionado.setCnaes(ordenarCnaesSelecionaPorTipo(selecionado.getCnaes()));
        atualizarDadosCnae();
        preencherInformacoesDeAlteracaoDosCalculosDeLocalizacao();
    }

    private void preencherTelefones() {
        preencherTelefonesCmc();
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            List<ProcessoCalculoAlvara> processosPorExercicio = calculoAlvaraFacade.buscarProcessosCalculoAlvaraPorExercicio(
                selecionado.getCadastroEconomico().getId(), selecionado.getExercicio().getAno());

            ProcessoCalculoAlvara processoComAlvaraNaoCassado = null;

            for (ProcessoCalculoAlvara processoCalculoAlvara : processosPorExercicio) {
                if (processoCalculoAlvara.getAlvara() == null || !calculoAlvaraFacade.getProcessoSuspensaoCassacaoAlvaraFacade().alvaraCassado(processoCalculoAlvara.getAlvara().getId())) {
                    processoComAlvaraNaoCassado = processoCalculoAlvara;
                    break;
                }
            }

            if (processoComAlvaraNaoCassado != null
                && ((isOperacaoEditar() && !processoComAlvaraNaoCassado.getId().equals(selecionado.getId())) || isOperacaoNovo())) {
                FacesUtil.addOperacaoNaoPermitida("Já existe um cálculo de Alvará lançado para o CMC: " +
                    selecionado.getCadastroEconomico() + " no exercício de " + selecionado.getExercicio().getAno() + ".");
            } else {
                verificarTaxaDeLocalizacaoAntesDeCalcular();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao salvar Cálculo. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar Cálculo. Detalhes: " + e.getMessage());
        }
    }

    private void salvarAndRedirecionar(String mensagem) {
        selecionado = calculoAlvaraFacade.salvarRetornando(selecionado);
        redirecionar(mensagem);
    }

    private void redirecionar() {
        redirecionar("");
    }

    private void redirecionar(String mensagem) {
        if (!Strings.isNullOrEmpty(mensagem)) {
            FacesUtil.addOperacaoRealizada(mensagem);
        }
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    private void mostrarDialogCompensacaoAlvara() {
        List<ProcessoCalculoAlvara> processos = calculoAlvaraFacade.buscarProcessosEfetivadosNoExercicio(selecionado.getCadastroEconomico().getId(), selecionado.getExercicio().getId());
        if (processos.isEmpty()) return;
        Long idAlvaraProcessoAnterior = null;
        boolean temParcelasPagasProcessoAnterior = false;
        if (processos.size() > 1) {
            ProcessoCalculoAlvara processoCalculoAlvara = processos.get(processos.size() - 2);
            List<DAMResultadoParcela> parcelas = preencherDAMParcelas(processoCalculoAlvara.buscarCalculosAtuais(), Lists.newArrayList(), true);
            for (DAMResultadoParcela parcela : parcelas) {
                if (parcela.getResultadoParcela().isPago()) {
                    temParcelasPagasProcessoAnterior = true;
                    break;
                }
            }
            idAlvaraProcessoAnterior = processoCalculoAlvara.getAlvara().getId();
        }
        if (temParcelasPagasProcessoAnterior && calculoAlvaraFacade.getProcessoSuspensaoCassacaoAlvaraFacade().alvaraCassado(idAlvaraProcessoAnterior)) {
            FacesUtil.addWarn("Atenção!", "Existem valores pagos no alvará cassado, é necessário realizar a compensação dos valores para esse novo calculo gerado.");
        }
    }

    private void atribuirCodigoAndCnaes() {
        if (isOperacaoNovo()) {
            selecionado.setCodigo(calculoAlvaraFacade.montarProximoCodigoPorExercicio(selecionado.getExercicio()));
            selecionado.setCnaes(preencherCnaes());
        }
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        if (selecionado.getCadastroEconomico() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Cadastro Econômico deve ser informado.");
        } else {
            if (selecionado.getCadastroEconomico().getAreaUtilizacao() <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Área de Utilização do CMC: " +
                    selecionado.getCadastroEconomico().getCmcNomeCpfCnpj() + " é vazia ou " +
                    " menor/igual a zero. Portanto o cálculo não pode ser realizado.");
            }
            if (cnaes.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O CMC: " + selecionado.getCadastroEconomico().getCmcNomeCpfCnpj() +
                    " não possui CNAEs adicionados.");
            }
        }
        ve.lancarException();
    }

    private List<CNAEProcessoCalculoAlvara> preencherCnaes() {
        cnaes = ordenarCnaesPorTipo(cnaes);
        List<CNAEProcessoCalculoAlvara> cnaesCalculo = Lists.newLinkedList();
        for (EconomicoCNAE cnae : cnaes) {
            CNAEProcessoCalculoAlvara cnaeProcessoCalculoAlvara = new CNAEProcessoCalculoAlvara();
            cnaeProcessoCalculoAlvara.setProcessoCalculoAlvara(selecionado);
            cnaeProcessoCalculoAlvara.setCnae(cnae.getCnae());
            cnaeProcessoCalculoAlvara.setTipoCnae(cnae.getTipo());
            cnaeProcessoCalculoAlvara.setHorarioFuncionamento(cnae.getHorarioFuncionamento());
            cnaeProcessoCalculoAlvara.setExercidaNoLocal(cnae.getExercidaNoLocal());
            cnaeProcessoCalculoAlvara.setInicioAtividade(cnae.getInicio());
            cnaesCalculo.add(cnaeProcessoCalculoAlvara);
        }
        return cnaesCalculo;
    }

    public List<CadastroEconomico> buscarCMCs(String parte) {
        protocoloRedeSim = "";
        return calculoAlvaraFacade.buscarCmcPorRazaoSocialAndCnpj(parte);
    }

    private void inicializarDados() {
        cnaes = Lists.newArrayList();
        telefones = Lists.newArrayList();
    }

    public boolean isPessoaJuridica() {
        return (selecionado.getCadastroEconomico() != null && selecionado.getCadastroEconomico().getPessoa() != null) &&
            selecionado.getCadastroEconomico().getPessoa() instanceof PessoaJuridica;
    }

    public boolean isPessoaFisica() {
        return (selecionado.getCadastroEconomico() != null && selecionado.getCadastroEconomico().getPessoa() != null) &&
            selecionado.getCadastroEconomico().getPessoa() instanceof PessoaFisica;
    }

    public void preencherDadosCalculo() {
        if (selecionado.getCadastroEconomico() != null) {
            if (calculoAlvaraFacade.getProcessoSuspensaoCassacaoAlvaraFacade().emissaoAlvaraSuspensa(selecionado.getCadastroEconomico().getId())) {
                FacesUtil.addOperacaoNaoPermitida("A geração de alvará para o cadastro " + selecionado.getCadastroEconomico().getInscricaoCadastral() + " está suspensa.");
                selecionado.setCadastroEconomico(null);
                return;
            }
            preencherEnquadramentoFiscal();
            preencherCnaesVigentes();
            if (selecionado.getCadastroEconomico().getPessoa() != null) {
                preencherTelefones();
            }
            calculoAlvaraFacade.adicionarEnderecosCadastroEconomico(selecionado);
        }
        selecionado = calculoAlvaraFacade.adicionarInformacoesCmc(selecionado);
    }

    public void preencherEnquadramentoFiscal() {
        enquadramentoFiscal = calculoAlvaraFacade.buscarEnquadramentoFiscalVigente(selecionado.getCadastroEconomico());
        alvarasEmissao = Lists.newArrayList();
    }

    private void preencherCnaesVigentes() {
        cnaes = calculoAlvaraFacade.buscarCnaesVigentesCMC(selecionado.getCadastroEconomico().getId());
        cnaesMesmoCodigo = Lists.newArrayList();
        cnaesSelecionados = Lists.newArrayList();
        if (!cnaes.isEmpty()) {
            for (EconomicoCNAE cnae : cnaes) {
                int contador = 1;
                for (EconomicoCNAE economicoCNAE : cnaes) {
                    if (cnae.getCnae().getCodigoCnae().equalsIgnoreCase(economicoCNAE.getCnae().getCodigoCnae())
                        && !cnae.getId().equals(economicoCNAE.getId())
                        && !cnaesMesmoCodigo.contains(cnae)) {
                        contador++;
                    }
                    if (contador > 1) {
                        Util.adicionarObjetoEmLista(cnaesMesmoCodigo, cnae);
                        Util.adicionarObjetoEmLista(cnaesMesmoCodigo, economicoCNAE);
                        break;
                    }
                }
            }
            if (!cnaesMesmoCodigo.isEmpty()) {
                cnaes.removeAll(cnaesMesmoCodigo);
                cnaesMesmoCodigo = ordenarCnaesPorTipo(cnaesMesmoCodigo);
                FacesUtil.executaJavaScript(isOperacaoNovo() ? "dialogCnaesEscolha.show()" : "dialogCnaesEscolhaVer.show()");
                FacesUtil.atualizarComponente(isOperacaoNovo() ? "formDialogCnaesEscolha" : "formDialogCnaesEscolhaVer");
            }
        }
        if (cnaesMesmoCodigo.isEmpty()) {
            if (isOperacaoNovo()) {
                cnaes = ordenarCnaesPorTipo(cnaes);
                selecionado.setCnaes(preencherCnaes());
            } else if ((isAtualizacao != null && isAtualizacao) || isRecalculo()) {
                selecionado.setCnaes(preencherCnaes());
                atualizarAndSalvar();
            }
        }
    }

    private void preencherTelefonesCmc() {
        telefones = calculoAlvaraFacade.buscarTelefonesPessoa(selecionado.getCadastroEconomico().getPessoa());
    }

    public List<SelectItem> buscarTiposAlvara() {
        return Util.getListSelectItem(TipoAlvara.values(), false);
    }

    public boolean hasCnaeTabela(EconomicoCNAE cnae) {
        return cnaesSelecionados.contains(cnae);
    }

    public void adicionarCnaeEsolha(EconomicoCNAE cnae) {
        try {
            validarAdicionarCnae(cnae);
            cnaesSelecionados.add(cnae);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarAdicionarCnae(EconomicoCNAE c) {
        ValidacaoException ve = new ValidacaoException();
        for (EconomicoCNAE cnae : cnaesSelecionados) {
            if (cnae.getCnae().getCodigoCnae().equalsIgnoreCase(c.getCnae().getCodigoCnae())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já foi adicionado CNAE com código: " + c.getCnae().getCodigoCnae() + " a lista.");
                break;
            }
        }
        ve.lancarException();
    }

    public void removerCnaeEsolha(EconomicoCNAE cnae) {
        cnaesSelecionados.remove(cnae);
    }

    public void confirmarCnaesMesmoCodigo() {
        try {
            validarAdicionarCnaesMesmoCodigo();
            cnaes.addAll(cnaesSelecionados);
            cnaes = ordenarCnaesPorTipo(cnaes);
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.executaJavaScript(isOperacaoNovo() ? "dialogCnaesEscolha.hide()" : "dialogCnaesEscolhaVer.hide()");
            FacesUtil.atualizarComponente(isOperacaoNovo() ? "" : "Formulario:dadosCalculo");

            if (confirmaTaxaDeLocalizacao != null && !confirmaTaxaDeLocalizacao.getCnaesComMesmoCodigo().isEmpty()) {
                FacesUtil.executaJavaScript("dialogTaxaLocalizacao.show()");
                FacesUtil.atualizarComponente("formTaxaLocalizacao");
            } else {
                preencherCnaesQuandoMesmoCodigo();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } finally {
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    private void preencherCnaesQuandoMesmoCodigo() {
        if (isOperacaoVer()) {
            selecionado.setCnaes(preencherCnaes());
            if (isAtualizacao != null && isAtualizacao) {
                atualizarAndSalvar();
            } else {
                calcularAndSalvar(null);
            }
        }
    }

    private void validarAdicionarCnaesMesmoCodigo() {
        ValidacaoException ve = new ValidacaoException();
        if (!canConfirmarCnaes()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário adicionar um CNAE por código para continuar o processo.");
        }
        ve.lancarException();
    }

    private boolean canConfirmarCnaes() {
        List<String> codigosDisponiveis = Lists.newArrayList();
        List<String> codigosSelecionados = Lists.newArrayList();

        for (EconomicoCNAE mesmoCodigo : cnaesMesmoCodigo) {
            if (!codigosDisponiveis.contains(mesmoCodigo.getCnae().getCodigoCnae())) {
                codigosDisponiveis.add(mesmoCodigo.getCnae().getCodigoCnae());
            }
        }
        for (EconomicoCNAE cnaeSelecionado : cnaesSelecionados) {
            if (!codigosSelecionados.contains(cnaeSelecionado.getCnae().getCodigoCnae())) {
                codigosSelecionados.add(cnaeSelecionado.getCnae().getCodigoCnae());
            }
        }
        return codigosDisponiveis.size() == codigosSelecionados.size();
    }

    private List<EconomicoCNAE> ordenarCnaesPorTipo(List<EconomicoCNAE> cnaes) {
        List<EconomicoCNAE> cnaesPrimarios = Lists.newArrayList();
        List<EconomicoCNAE> cnaesSecundarios = Lists.newArrayList();

        for (EconomicoCNAE cnae : cnaes) {
            if (EconomicoCNAE.TipoCnae.Primaria.equals(cnae.getTipo())) {
                cnaesPrimarios.add(cnae);
            } else {
                cnaesSecundarios.add(cnae);
            }
        }
        ordenarCnaes(cnaesPrimarios);
        ordenarCnaes(cnaesSecundarios);

        cnaesPrimarios.addAll(cnaesSecundarios);
        return cnaesPrimarios;
    }

    private void ordenarCnaes(List<EconomicoCNAE> cnaes) {
        Collections.sort(cnaes, new Comparator<EconomicoCNAE>() {
            @Override
            public int compare(EconomicoCNAE o1, EconomicoCNAE o2) {
                return ComparisonChain.start().compare(Integer.valueOf(o1.getCnae().getCodigoCnae()), Integer.valueOf(o2.getCnae().getCodigoCnae())).result();
            }
        });
    }

    private List<CNAEProcessoCalculoAlvara> ordenarCnaesSelecionaPorTipo(List<CNAEProcessoCalculoAlvara> cnaes) {
        List<CNAEProcessoCalculoAlvara> cnaesPrimarios = Lists.newArrayList();
        List<CNAEProcessoCalculoAlvara> cnaesSecundarios = Lists.newArrayList();

        for (CNAEProcessoCalculoAlvara cnae : cnaes) {
            if (EconomicoCNAE.TipoCnae.Primaria.equals(cnae.getTipoCnae())) {
                cnaesPrimarios.add(cnae);
            } else {
                cnaesSecundarios.add(cnae);
            }
        }
        ordenarCnaesSelelcionado(cnaesPrimarios);
        ordenarCnaesSelelcionado(cnaesSecundarios);

        cnaesPrimarios.addAll(cnaesSecundarios);
        return cnaesPrimarios;
    }

    private void ordenarCnaesSelelcionado(List<CNAEProcessoCalculoAlvara> cnaes) {
        Collections.sort(cnaes, new Comparator<CNAEProcessoCalculoAlvara>() {
            @Override
            public int compare(CNAEProcessoCalculoAlvara o1, CNAEProcessoCalculoAlvara o2) {
                return ComparisonChain.start().compare(Integer.valueOf(o1.getCnae().getCodigoCnae()), Integer.valueOf(o2.getCnae().getCodigoCnae())).result();
            }
        });
    }

    public void verificarTaxaDeLocalizacaoAntesDeCalcular() {
        try {
            if (!canHabilitarBtnRecalculo()) return;
            primeiroCalculo = calculoAlvaraFacade.isPrimeiroCalculo(selecionado.getCadastroEconomico().getId());
            confirmaTaxaDeLocalizacao = calculoAlvaraFacade
                .preencherInfoConfirmacaoDeTaxaDeLocalizacao(selecionado);
            if (confirmaTaxaDeLocalizacao != null || primeiroCalculo) {
                FacesUtil.executaJavaScript("dialogTaxaLocalizacao.show()");
                FacesUtil.atualizarComponente("formTaxaLocalizacao");
            } else {
                if (isOperacaoNovo()) {
                    atribuirCodigoAndCnaes();
                }
                gerarTaxaLocalizacao = false;
                calcularAlvara();
            }
        } catch (Exception ex) {
            logger.error("Erro no verificarTaxaDeLocalizacaoAntesDeCalcular: {}", ex);
        }
    }

    public void verificarTaxaDeLocalizacaoAntesDeAtualizar() {
        isAtualizacao = true;
        confirmaTaxaDeLocalizacao = calculoAlvaraFacade
            .preencherInfoConfirmacaoDeTaxaDeLocalizacao(selecionado);
        gerarTaxaLocalizacao = false;
        for (CalculoAlvara calculo : getCalculosAtuais()) {
            if (TipoAlvara.LOCALIZACAO.equals(calculo.getTipoAlvara())) {
                gerarTaxaLocalizacao = true;
            }
        }
        if (confirmaTaxaDeLocalizacao != null) {
            FacesUtil.executaJavaScript("dialogTaxaLocalizacao.show()");
            FacesUtil.atualizarComponente("formTaxaLocalizacao");
        } else {
            atualizarCalculo();
        }
    }

    public void calcularAlvaraConfirmandoTaxaLocalizacao(boolean gerarTaxaLocalizacao) {
        try {
            this.gerarTaxaLocalizacao = gerarTaxaLocalizacao;
            if (cnaesMesmoCodigo != null && !cnaesMesmoCodigo.isEmpty()) {
                preencherCnaesQuandoMesmoCodigo();
            }
            if (isAtualizacao == null || !isAtualizacao) {
                if (isOperacaoNovo()) {
                    atribuirCodigoAndCnaes();
                }
                calcularAlvara();
            }
        } catch (Exception ex) {
            logger.error("Erro no calcularAlvaraConfirmandoTaxaLocalizacao: {}", ex);
        }
    }

    public void calcularAlvara() {
        try {
            if (selecionado.getId() != null) {
                selecionado = calculoAlvaraFacade.recuperar(selecionado.getId());
            }
            ValidacaoException ve = realizarValidacoesIniciais();
            NotificacaoService.getService().marcarNotificaoComoLidaPorIdDoLink(selecionado.getCadastroEconomico().getId());
            if (!isRecalculo()) {
                selecionado.setSituacaoCalculoAlvara(SituacaoCalculoAlvara.CALCULADO);
            } else if (isAlvaraRecalculado(selecionado) && (confirmaTaxaDeLocalizacao == null || confirmaTaxaDeLocalizacao.getCnaesComMesmoCodigo().isEmpty())) {
                selecionado.setSituacaoCalculoAlvara(SituacaoCalculoAlvara.EFETIVADO);
            }
            selecionado = calculoAlvaraFacade.adicionarInformacoesCmc(selecionado);
            if ((isAtualizacao != null && isAtualizacao) || isRecalculo()) {
                preencherCnaesVigentes();
            } else {
                calcularAndSalvar(ve);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao calcular Alvará. ", e);
        }
    }

    private void calcularAndSalvar(ValidacaoException ve) {
        try {
            if (enquadramentoFiscal.isMei()) {
                selecionado = calculoAlvaraFacade.corrigirCalculosDeMei(selecionado);
            }
            selecionado = calculoAlvaraFacade.calcularAlvara(selecionado, calculoAlvaraFacade.recuperarExercicioCorrente(), isRecalculo(), gerarTaxaLocalizacao, getConfiguracaoTributario());
            selecionado = calculoAlvaraFacade.corrigirCalculosDeLocalizacaoLancadosEmDuplicidade(selecionado);

            if (gerarTaxaLocalizacao != null && !gerarTaxaLocalizacao) {
                calculoAlvaraFacade.adicionarEnderecosCadastroEconomico(selecionado);
            }
            gerarTaxaLocalizacao = null;
            boolean alterou = selecionado.getId() != null && isAlterou();

            if (selecionado.getCalculosAlvara().isEmpty()) {
                selecionado.setSituacaoCalculoAlvara(SituacaoCalculoAlvara.EM_ABERTO);
                FacesUtil.addOperacaoNaoRealizada("Não foi possível realizar o cálculo do Alvará pois não foram encontradas " +
                    "configurações de alvará nas Configurações do Tributário.");
            } else {
                validarAndSalvarSelecionado(ve, alterou);
            }
        } catch (Exception ex) {
            logger.error("Erro no calcularAndSalvar: {}", ex);
        }
    }

    private void atualizarAndSalvar() {
        selecionado = calculoAlvaraFacade.finalizarRecalculos(selecionado);
        if (enquadramentoFiscal.isMei()) {
            selecionado = calculoAlvaraFacade.corrigirCalculosDeMei(selecionado);
        }
        selecionado = calculoAlvaraFacade.calcularAlvara(selecionado, calculoAlvaraFacade.recuperarExercicioCorrente(), true, gerarTaxaLocalizacao, getConfiguracaoTributario());
        selecionado = calculoAlvaraFacade.corrigirCalculosDeLocalizacaoLancadosEmDuplicidade(selecionado);
        if (gerarTaxaLocalizacao != null && !gerarTaxaLocalizacao) {
            calculoAlvaraFacade.adicionarEnderecosCadastroEconomico(selecionado);
        }
        gerarTaxaLocalizacao = null;
        selecionado = calculoAlvaraFacade.atualizarHorariosDeFuncionamento(selecionado);
        selecionado = calculoAlvaraFacade.atualizarValorTotalCalculado(selecionado);
        if (isRecalculo() && selecionado.buscarRecalculos().isEmpty()) {
            selecionado.setAlvara(calculoAlvaraFacade.criarOrAtualizarAlvara(selecionado));
        }
        salvarAndRedirecionar("Informações atualizadas com sucesso!");
    }

    public void atualizarCalculo() {
        try {
            ValidacaoException ve = realizarValidacoesIniciais();
            if (ve.temMensagens()) {
                ve.lancarException();
            }
            selecionado = calculoAlvaraFacade.adicionarInformacoesCmc(selecionado);
            preencherCnaesVigentes();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao atualizar calculo. ", e);
        }
    }

    private boolean isAlterou() {
        boolean alterou = false;
        for (CalculoAlvara calculoAlvara : selecionado.getCalculosAlvara()) {
            if (TipoControleCalculoAlvara.RECALCULO.equals(calculoAlvara.getControleCalculo())) {
                alterou = true;
                break;
            }
        }
        if (!alterou && (calculoAlvaraFacade.hasAlteracaoCnae(selecionado.getCadastroEconomico().getId()) ||
            calculoAlvaraFacade.hasAlteracaoEndereco(selecionado.getCadastroEconomico().getId()))) {

            alterou = true;
        }
        if (!alterou && (selecionado.getAlterouFuncionamento() || calculoAlvaraFacade.hasAlteracaoHorarioFuncionamento(selecionado)) ||
            calculoAlvaraFacade.hasAlteracaoCaracFuncionamento(selecionado.getCadastroEconomico(), selecionado.getId())) {
            alterou = true;
        }
        if (!alterou && !isAlvaraCalculado(selecionado) && !selecionado.getAlterouFuncionamento()) {
            if (selecionado.buscarCalculos().isEmpty()) {
                selecionado.setSituacaoCalculoAlvara(SituacaoCalculoAlvara.EFETIVADO);
            } else {
                selecionado.setSituacaoCalculoAlvara(SituacaoCalculoAlvara.RECALCULADO);
            }

            if (selecionado.getAlvara() != null &&
                !calculoAlvaraFacade.definirVencimentoAlvara(selecionado.getDataLancamento(), selecionado).equals(selecionado.getAlvara().getVencimento())) {
                alterou = true;
            } else {
                FacesUtil.addOperacaoNaoRealizada("Não foram encontradas alterações para realizar o recálculo.");
                redirecionar();
            }
        }
        return alterou;
    }

    private ValidacaoException realizarValidacoesIniciais() {
        ValidacaoException ve = new ValidacaoException();
        calculoAlvaraFacade.validarCalculo(selecionado, ve, true);
        return ve;
    }

    private boolean isRecalculo() {
        if (selecionado != null && selecionado.getCalculosAlvara() != null) {
            for (CalculoAlvara calculoAlvara : selecionado.getCalculosAlvara()) {
                if (calculoAlvara.getId() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private void validarAndSalvarSelecionado(ValidacaoException ve, boolean alterou) {
        if (alterou || isAlvaraCalculado(selecionado)) {
            if (ve == null || ve.getMensagens().isEmpty()) {
                if (selecionado != null) {
                    String mensagem = "Informações atualizadas com sucesso!";
                    if (!selecionado.getAlterouFuncionamento() || !selecionado.buscarRecalculos().isEmpty() || isAlvaraCalculado(selecionado)) {
                        mensagem = "Calculado com sucesso!";
                    } else if (selecionado.getAlterouFuncionamento() || calculoAlvaraFacade.hasAlteracaoHorarioFuncionamento(selecionado)) {
                        mensagem = "Horário(s) de Funcionamento atualizado(s) com sucesso!";
                    } else if (calculoAlvaraFacade.hasAlteracaoCaracFuncionamento(selecionado.getCadastroEconomico(), selecionado.getId())) {
                        mensagem = "Característica(s) de Funcionamento atualizada(s) com sucesso!";
                    }
                    if (selecionado.getAlvara() != null) {
                        selecionado.getAlvara().setVencimento(calculoAlvaraFacade.definirVencimentoAlvara(selecionado.getDataLancamento(), selecionado));
                        calculoAlvaraFacade.salvarAlvara(selecionado.getAlvara());
                    }
                    selecionado = calculoAlvaraFacade.atualizarHorariosDeFuncionamento(selecionado);
                    selecionado = calculoAlvaraFacade.atualizarValorTotalCalculado(selecionado);
                    redirecionar(mensagem);
                }
            } else {
                if (isOperacaoNovo()) {
                    selecionado.getCalculosAlvara().clear();
                }
                ve.lancarException();
            }
        }
    }

    private void ordenarCalculos(List<CalculoAlvara> calculos) {
        Collections.sort(calculos, new Comparator<CalculoAlvara>() {
            @Override
            public int compare(CalculoAlvara o1, CalculoAlvara o2) {
                if (isCalculoRecalculado(o1) || isCalculoRecalculado(o2)) {
                    if (o1.getId() != null && o2.getId() != null) {
                        return ComparisonChain.start().compare(o1.getId(), o2.getId()).compare(o1.getTipoAlvara().getOrdenacao(),
                            o2.getTipoAlvara().getOrdenacao(), Ordering.natural()).result();
                    }
                    return ComparisonChain.start().compare(o1.getTipoAlvara().getOrdenacao(),
                        o2.getTipoAlvara().getOrdenacao()).result();
                }
                return ComparisonChain.start().compare(o1.getDataCalculo(), o2.getDataCalculo()).compare(o1.getTipoAlvara().getOrdenacao(), o2.getTipoAlvara().getOrdenacao()).result();
            }
        });
    }

    public void efetivarCalculo() {
        try {
            validarEfetivacaoCalculo();
            assistenteEfetivarProcesso = new AssistenteEfetivarProcesso();
            assistenteEfetivarProcesso.setProcessoCalculoAlvara(selecionado);
            assistenteEfetivarProcesso.setExercicioCorrente(calculoAlvaraFacade.recuperarExercicioCorrente());
            futureAssistenteEfetivarProcesso = calculoAlvaraFacade.efetivarCalculo(assistenteEfetivarProcesso);

            abrirDialogProgressBar("dialogProgresEfetivar.show()");
            Thread.sleep(1000);
            executarPoll("pollEfetivar.start()");

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao efetivar calculo. ", e);
        }
    }

    public void acompanharEfetivacao() {
        if (futureAssistenteEfetivarProcesso.isDone()) {
            FacesUtil.executaJavaScript("pollEfetivar.stop()");
            futureAssistenteEfetivarProcesso = calculoAlvaraFacade.aposEfetivarCalculo(assistenteEfetivarProcesso);
            FacesUtil.executaJavaScript("pollAposEfetivar.start()");
        }
    }

    public void acompanharAposEfetivacao() {
        if (futureAssistenteEfetivarProcesso.isDone()) {
            FacesUtil.executaJavaScript("pollAposEfetivar.stop()");
            FacesUtil.executaJavaScript("dialogProgresEfetivar.hide()");
            FacesUtil.atualizarComponente("Formulario");
            FacesUtil.executaJavaScript("finalizarEfetivacao()");
        }
    }

    public void finalizarEfetivacao() {
        try {
            if (futureAssistenteEfetivarProcesso.get().getProcessoCalculoAlvara() != null) {
                FacesUtil.executaJavaScript("aguarde.show()");
                selecionado = futureAssistenteEfetivarProcesso.get().getProcessoCalculoAlvara();
                salvarAndRedirecionar(getMensagemEfetivadoComSucesso());
                mostrarDialogCompensacaoAlvara();
            }
        } catch (Exception e) {
            FacesUtil.addError("Erro ao recuperar as permissões do usuário..", e.getMessage());
        }
    }

    private ConfiguracaoTributario getConfiguracaoTributario() {
        if (configuracao == null) {
            configuracao = calculoAlvaraFacade.buscarConfiguracaoTriutarioVigente();
        }
        return configuracao;
    }

    private void validarEfetivacaoCalculo() {
        ValidacaoException ve = new ValidacaoException();
        if (getConfiguracaoTributario().getAnoCnaeAltoRisco() == null || getConfiguracaoTributario().getAnoCnaeBaixoRiscoB() == null || getConfiguracaoTributario().getAnoCnaeBaixoRiscoA() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Os anos de validade do Cartaz de Alvará das Configurações do Alvará devem ser preenchidos.");
        }
        ve.lancarException();
    }


    private String getMensagemEfetivadoComSucesso() {
        Boolean isento = calculoAlvaraFacade.isCalculoAlvaraIsento(selecionado.getCadastroEconomico());
        Boolean dispensaLicenciamento = selecionado.isDispensaLicenciamento();
        return "Cálculo efetivado com sucesso! " +
            (isento ? "Não foi gerado o DAM, pois o cálculo é isento. " : " O DAM já pode ser impresso. ") +
            (dispensaLicenciamento ? "Declaração de Dispensa enviada para a REDESIM. " : "");
    }

    private void abrirDialogProgressBar(String dialog) {
        FacesUtil.executaJavaScript(dialog);
    }

    private void executarPoll(String idPoll) {
        FacesUtil.executaJavaScript(idPoll);
    }

    public void abortar() {
        if (futureAssistenteEfetivarProcesso != null) {
            futureAssistenteEfetivarProcesso.cancel(true);
            assistenteEfetivarProcesso.getBarraProgressoItens().finaliza();
        }
    }

    public void buscarParcelasEmissao(VOAlvara voAlvara) {
        atribuirAlvaraSelecionado(voAlvara);
        buscarParcelasOriginadas(voAlvara, Lists.newArrayList(SituacaoParcela.PAGO, SituacaoParcela.EM_ABERTO));
    }

    public List<DAMResultadoParcela> buscarParcelasAtuais() {
        if (parcelas == null || parcelas.isEmpty()) {
            parcelas = preencherDAMParcelas(buscarCalculosAtuaisVer(), Lists.newArrayList(), true);
        }
        return parcelas;
    }

    public List<DAMResultadoParcela> buscarParcelasAnteriores() {
        if (parcelasCanceladas == null || parcelasCanceladas.isEmpty()) {
            parcelasCanceladas = preencherDAMParcelas(calculosAnteriores, Lists.<SituacaoParcela>newArrayList(), false);
        }
        return parcelasCanceladas;
    }

    private List<DAMResultadoParcela> preencherDAMParcelas(List<CalculoAlvara> calculoAlvaras, List<SituacaoParcela> situacoes, boolean isAtual) {
        ProcessoCalculoAlvara processoAux = new ProcessoCalculoAlvara();
        processoAux.setCalculosAlvara(calculoAlvaras);
        processoAux.setExercicio(selecionado.getExercicio());
        processoAux.setCadastroEconomico(selecionado.getCadastroEconomico());

        List<ResultadoParcela> parcelas = calculoAlvaraFacade.buscarParcelas(processoAux, situacoes);
        List<DAMResultadoParcela> damParcelas = Lists.newArrayList();
        for (ResultadoParcela parcela : parcelas) {
            DAMResultadoParcela damParcela = new DAMResultadoParcela();
            damParcela.setId(parcela.getIdParcela());
            damParcela.setResultadoParcela(parcela);
            damParcela.setDam(buscarDam(parcela.getIdParcela(), isAtual));

            CalculoAlvara calculoAlvara = calculoAlvaraFacade.recuperarCalculoAlvara(parcela.getIdCalculo());
            if (calculoAlvara != null && (isAtual ? (isCalculoAtual(calculoAlvara) || calculoAlvara.getControleCalculo().isAguardandoCancelamento())
                : isCalculoCancelado(calculoAlvara))) {
                damParcelas.add(damParcela);
            }
        }
        return damParcelas;
    }

    public void buscarParcelasOriginadas(Object calculoProcesso, List<SituacaoParcela> situacoes) {
        parcelas = Lists.newArrayList();

        if (canRenderizarTableParcelas()) {
            List<ResultadoParcela> parcelas = calculoAlvaraFacade.buscarParcelas(calculoProcesso, situacoes);

            for (ResultadoParcela parcela : parcelas) {
                DAMResultadoParcela damParcela = new DAMResultadoParcela();
                damParcela.setId(parcela.getIdParcela());
                damParcela.setResultadoParcela(parcela);
                damParcela.setDam(buscarDam(parcela.getIdParcela(), true));

                if (!this.parcelas.contains(damParcela))
                    this.parcelas.add(damParcela);
            }
        }
    }

    public DAM buscarDam(Long idParcela, boolean isAtual) {
        return calculoAlvaraFacade.buscarDAMPeloIdParcela(idParcela, isAtual);
    }

    public void imprimirDAM(Object calculo) {
        try {
            if (calculo != null) {
                atribuirAlvaraSelecionado((VOAlvara) Util.clonarObjeto(calculo));
                buscarParcelasOriginadas(calculo, Lists.newArrayList(SituacaoParcela.EM_ABERTO));
            } else {
                buscarParcelasOriginadas(selecionado, Lists.newArrayList(SituacaoParcela.EM_ABERTO));
            }

            List<ResultadoParcela> resultadoParcelas = percorrerParcelasAndAdicionarResultadoParcela();

            if (!resultadoParcelas.isEmpty()) {
                Date vencimento = calculoAlvaraFacade.definirVencimentoDAM(resultadoParcelas);
                DAM dam = calculoAlvaraFacade.gerarDam(resultadoParcelas, vencimento);

                if (dam != null) {
                    ImprimeDAM imprimeDAM = new ImprimeDAM();
                    imprimeDAM.setGeraNoDialog(true);
                    imprimeDAM.imprimirDamCompostoViaApi(dam, selecionado.getCadastroEconomico().getPessoa());
                }
            } else {
                FacesUtil.addError("Não Existe nenhum DAM gerado para esse Alvará!", "Verifique na tela de Consulta de débitos as parcelas referentes a esse alvará e gere novos DAMs a partir dela");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao imprimir DAM. ", e);
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private List<ResultadoParcela> percorrerParcelasAndAdicionarResultadoParcela() {
        List<ResultadoParcela> resultadoParcelas = Lists.newArrayList();
        if (parcelas != null && !parcelas.isEmpty()) {
            for (DAMResultadoParcela parcela : parcelas) {
                if (SituacaoParcela.EM_ABERTO.equals(parcela.getResultadoParcela().getSituacaoEnumValue())) {
                    resultadoParcelas.add(parcela.getResultadoParcela());
                }
            }
        }
        return resultadoParcelas;
    }

    public void prepararEnvioEmail() {
        if (selecionado != null && selecionado.getCadastroEconomico() != null && selecionado.getCadastroEconomico().getPessoa() != null) {
            valor = BigDecimal.ZERO;
            for (DAMResultadoParcela damParcela : parcelas) {
                if (SituacaoParcela.EM_ABERTO.equals(damParcela.getResultadoParcela().getSituacaoEnumValue())) {
                    valor = valor.add(damParcela.getResultadoParcela().getValorTotal());
                }
            }

            emails = selecionado.getCadastroEconomico().getPessoa().getEmail() != null ? selecionado.getCadastroEconomico().getPessoa().getEmail() : "";
            msgEmail = null;
            FacesUtil.executaJavaScript("listaEmails.show()");
            FacesUtil.atualizarComponente("formEmails");
        }
    }

    public void enviarDAMPorEmail() {
        try {
            if (selecionado != null && selecionado.getCadastroEconomico() != null && selecionado.getCadastroEconomico().getPessoa() != null) {
                String[] emailsSeparados = validarAndSepararEmails();

                if (parcelas == null || parcelas.isEmpty()) {
                    buscarParcelasOriginadas(selecionado, Lists.newArrayList(SituacaoParcela.EM_ABERTO));
                }

                calculoAlvaraFacade.enviarDamPorEmail(selecionado.getId(), msgEmail, emailsSeparados, percorrerParcelasAndAdicionarResultadoParcela());
                FacesUtil.executaJavaScript("listaEmails.hide()");
                FacesUtil.addOperacaoRealizada("E-mail enviado com sucesso!");
            }
        } catch (AddressException e) {
            FacesUtil.addOperacaoNaoRealizada("O e-mail informado é invalido!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            logger.error("Erro ao enviar e-mail. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao enviar e-mail. Detalhes: " + e.getMessage());
        }
    }

    public String[] validarAndSepararEmails() throws AddressException {
        ValidacaoException ve = new ValidacaoException();
        String[] emailsQuebrados = null;
        if (emails == null || emails.trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo E-mail deve ser informado.");
        } else {
            emailsQuebrados = emails.split(Pattern.quote(","));
            for (String emailsQuebrado : emailsQuebrados) {
                InternetAddress emailAddr = new InternetAddress(emailsQuebrado);
                emailAddr.validate();
            }
        }
        if (Strings.isNullOrEmpty(msgEmail)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mensagem deve ser informado.");
        }
        ve.lancarException();
        return emailsQuebrados;
    }

    public void enviarDAMParaRedeSim() {
        try {
            selecionado = calculoAlvaraFacade.enviarPdfDAMsRedeSim(selecionado);
            redirecionar();
        } catch (Exception e) {
            logger.error("Erro ao enviar PdfResultado. ", e);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível enviar os DAMs para RedeSim. Detalhes: " + e.getMessage());
        }
    }

    public void enviarDAMParaRedeSim(Long idProcessoCalculo) {
        try {
            if (idProcessoCalculo != null) {
                ProcessoCalculoAlvara processoCalculo = calculoAlvaraFacade.recuperarParaEnvioRedeSim(idProcessoCalculo);
                calculoAlvaraFacade.enviarPdfDAMsRedeSim(processoCalculo);
            }
        } catch (Exception e) {
            logger.error("Erro ao enviar PdfResultado. ", e);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível enviar os DAMs para RedeSim. Detalhes: " + e.getMessage());
        }
    }

    public void enviarAlvaraParaRedeSim(Long idProcessoCalculo) {
        try {
            if (idProcessoCalculo != null) {
                ProcessoCalculoAlvara processoCalculo = calculoAlvaraFacade.recuperarParaEnvioRedeSim(idProcessoCalculo);
                calculoAlvaraFacade.enviarAlvara(processoCalculo, getConfiguracaoTributario());
                FacesUtil.addOperacaoRealizada("Alvarás enviados com sucesso!");
            }
        } catch (Exception e) {
            logger.error("Erro ao enviar alvaras. ", e);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível enviar os alvarás para RedeSim. Detalhes: " + e.getMessage());
        }
    }

    public boolean canEmitirDamEmissao(VOAlvara voAlvara) {
        if (voAlvara != null) {
            if (!voAlvara.getItens().isEmpty() && voAlvara.getItens().get(0).getIsento()) {
                return false;
            } else {
                return canEstornarProcesso(voAlvara);
            }
        }
        return false;
    }

    public boolean canEstornarProcesso(Object objeto) {
        try {
            boolean hasParcelaNaoAberta = false;
            if ((objeto instanceof ProcessoCalculoAlvara && isAlvaraEfetivado(objeto) || isAlvaraRecalculado(objeto)) || objeto instanceof VOAlvara &&
                isAlvaraEfetivado(objeto) || !(objeto instanceof ProcessoCalculoAlvara) && isAlvaraCalculado(objeto)) {
                List<ResultadoParcela> parcelas = Lists.newArrayList();

                if (objeto instanceof ProcessoCalculoAlvara) {
                    for (CalculoAlvara calculoAlvara : ((ProcessoCalculoAlvara) objeto).buscarCalculos()) {
                        if (calculoAlvara.getId() != null) {
                            parcelas.addAll(calculoAlvaraFacade.buscarArvoreParcelamentoPartindoDoCalculo(calculoAlvara.getId()));
                        }
                    }
                } else {
                    for (VOAlvaraItens item : ((VOAlvara) objeto).getItens()) {
                        parcelas.addAll(calculoAlvaraFacade.buscarArvoreParcelamentoPartindoDoCalculo(item.getId()));
                    }
                    for (ResultadoParcela parcela : parcelas) {
                        if (!SituacaoParcela.EM_ABERTO.equals(parcela.getSituacaoEnumValue()) ||
                            (SituacaoParcela.EM_ABERTO.equals(parcela.getSituacaoEnumValue()) && parcela.getVencido())) {
                            hasParcelaNaoAberta = true;
                            break;
                        }
                    }
                    return !hasParcelaNaoAberta;
                }
                for (ResultadoParcela parcela : parcelas) {
                    if (!SituacaoParcela.EM_ABERTO.equals(parcela.getSituacaoEnumValue()) ||
                        (SituacaoParcela.EM_ABERTO.equals(parcela.getSituacaoEnumValue()) && parcela.getVencido())) {
                        hasParcelaNaoAberta = true;
                        break;
                    }
                }
                return !hasParcelaNaoAberta;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public void estornarCalculosAlvara() {
        try {
            validarEstorno();
            selecionado.setSituacaoCalculoAlvara(SituacaoCalculoAlvara.ESTORNADO);
            selecionado.setMotivoEstorno(motivoEstorno);
            selecionado.setUsuarioEstorno(usuarioEstorno);
            selecionado.setDataEstorno(dataEstorno);
            selecionado = calculoAlvaraFacade.estornarCalculosAlvara(selecionado);
            redirecionar("Estorno realizado com Sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao estornar processo. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao estornar processo. Detalhes: " + e.getMessage());
        }
    }

    private void validarEstorno() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(motivoEstorno)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo do Estorno deve ser informado.");
        }
        ve.lancarException();
    }

    public List<SituacaoCadastralCadastroEconomico> montarSituacoesDisponiveisCmc() {
        return Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR, SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
    }

    public void carregarProcessosParaEmissaoAlvara() {
        try {
            ValidacaoException ve = new ValidacaoException();
            validarPesquisa(ve);
            alvarasEmissao = calculoAlvaraFacade.definirEmissaoAlvara(selecionado.getCadastroEconomico(), tipoAlvara, getConfiguracaoTributario(), ve);
            ordenarAlvarasEmissao(alvarasEmissao);
            ve.lancarException();
            if (alvarasEmissao.isEmpty()) {
                FacesUtil.addOperacaoNaoRealizada("Nenhum registro encontrado para os filtros informados.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } finally {
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    private void ordenarAlvarasEmissao(List<VOAlvara> alvarasEmissao) {
        Collections.sort(alvarasEmissao, new Comparator<VOAlvara>() {
            @Override
            public int compare(VOAlvara o1, VOAlvara o2) {
                if (o2.getDataCalculo() != null && o1.getDataCalculo() != null) {
                    return ComparisonChain.start().compare(o2.getDataCalculo(), o1.getDataCalculo()).result();
                }
                return ComparisonChain.start().result();
            }
        });
    }

    private void validarPesquisa(ValidacaoException ve) {
        if (selecionado != null && selecionado.getCadastroEconomico() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Cadastro Econômico deve ser informado.");
        }
        ve.lancarException();
    }

    public void prepararImpressaoAlvara(VOAlvara voAlvara) {
        selecionarCalculo(voAlvara);
        if (TipoVerificacaoDebitoAlvara.PERMITIR_AVISAR.equals(getConfiguracaoTributario().getTipoVerificacaoDebitoAlvara()) && voAlvara.getEmitir()) {
            FacesUtil.executaJavaScript("avisoDebitoAlvara.show()");
            FacesUtil.atualizarComponente("FormularioAviso");
        } else {
            imprimirAlvara();
        }
    }

    public void atribuirAlvaraSelecionado(VOAlvara o) {
        alvaraSelecionado = (VOAlvara) Util.clonarObjeto(o);
    }

    public void imprimirAlvara() {
        try {
            preencherInformacoesRecibo();
            if (calculoAlvaraFacade.hasAutorizacaoImprimirAlvaraNaoPago()) {
                adicionarHistorico();
            }
            alvara = adicionarInformacoesAlvara();
            validarAlvara();
            CadastroEconomico cmc = calculoAlvaraFacade.getCadastroEconomicoFacade().recuperar(alvaraSelecionado.getIdCadastro());
            calculoAlvaraFacade.imprimirAlvara(alvara, recibo, alvaraSelecionado, calculoAlvaraFacade.isCalculoAlvaraIsento(cmc));
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao imprimir Alvará. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao imprimir alvará. Detalhes: " + e.getMessage());
        }
    }

    private void validarAlvara() {
        ValidacaoException ve = new ValidacaoException();
        if (alvara == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi localizado Alvará para esse Cálculo.");
        }
        ve.lancarException();
    }

    public void adicionarHistorico() {
        HistoricoImpressaoAlvara historico = calculoAlvaraFacade.adicionarProcessoOrCalculoHistorico(alvaraSelecionado);
        historico.setUsuarioSistema(calculoAlvaraFacade.recuperarUsuarioCorrente());
        historico.setDataImpressao(calculoAlvaraFacade.recuperarDataAtual());
        calculoAlvaraFacade.salvarHistorico(historico);
    }

    public Alvara adicionarInformacoesAlvara() {
        Alvara alvara = calculoAlvaraFacade.buscarAlvaraPorId(alvaraSelecionado.getIdAlvara());

        if (alvara != null) {
            alvara.setEmissao(alvara.getEmissao() != null ? alvara.getEmissao() : new Date());
            alvara.setAssinaturadigital((alvara.getAssinaturadigital() != null || !TipoSemAssinatura.SEM_ASSINATURA.getDescricao().equals(
                alvara.getAssinaturadigital())) ? alvara.getAssinaturadigital() : calculoAlvaraFacade.geraAssinaturaDigital(alvara));

            CadastroEconomico cmc = calculoAlvaraFacade.recuperarParaAlvara(alvaraSelecionado.getIdCadastro());

            alvara.setNomeRazaoSocial(alvara.getNomeRazaoSocial() != null ? alvara.getNomeRazaoSocial() :
                (cmc != null && cmc.getPessoa() != null ? cmc.getPessoa().getNome() : ""));

            return alvara;
        }
        return null;
    }

    public void selecionarCalculo(VOAlvara voAlvara) {
        atribuirAlvaraSelecionado((VOAlvara) Util.clonarObjeto(voAlvara));
        alvara = calculoAlvaraFacade.buscarAlvaraPorId(voAlvara.getIdAlvara());
    }

    public void atribuirAlvaraRedeSim(LogAlvaraRedeSim log) {
        if (log != null && log.getIdAlvara() != null) {
            alvaraRedeSim = calculoAlvaraFacade.buscarAlvaraPorId(log.getIdAlvara());
        }
    }

    public boolean desabilitarEmitirAlvara(VOAlvara voAlvara) {
        if (TipoVerificacaoDebitoAlvara.PERMITIR_AVISAR.equals(getConfiguracaoTributario().getTipoVerificacaoDebitoAlvara())) {
            return false;
        }
        return !voAlvara.getEmitir();
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        selecionado.setCadastroEconomico((CadastroEconomico) obj);
    }

    public ComponentePesquisaGenerico criarComponentePesquisa() {
        PesquisaCadastroEconomicoControlador componente = (PesquisaCadastroEconomicoControlador)
            Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
        List<SituacaoCadastralCadastroEconomico> situacao = Lists.newArrayList();
        situacao.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        situacao.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
        componente.setSituacao(situacao);
        return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
    }

    public List<ReciboImpressaoAlvara> montarRecibos() {
        Long idAlvara = null;
        if (alvaraSelecionado != null && alvaraSelecionado.getIdAlvara() != null) {
            idAlvara = alvaraSelecionado.getIdAlvara();
        } else if (alvaraRedeSim != null && alvaraRedeSim.getId() != null) {
            idAlvara = alvaraRedeSim.getId();
        }

        if (idAlvara != null) {
            Alvara alvara = calculoAlvaraFacade.buscarAlvaraPorId(idAlvara);
            if (alvara != null) {
                return calculoAlvaraFacade.buscarRecibosDoAlvaraPorId(alvara.getId());
            }
        }
        return Lists.newArrayList();
    }

    public boolean isCalculoRecalculado(CalculoAlvara calculoAlvara) {
        return calculoAlvara != null && !isAlvaraEstornado(selecionado) && TipoControleCalculoAlvara.RECALCULO.equals(calculoAlvara.getControleCalculo());
    }

    public boolean canRenderizarTableParcelas() {
        if (alvaraSelecionado != null) {
            return !isAlvaraEmAberto(alvaraSelecionado) && !isAlvaraCalculado(alvaraSelecionado);
        }
        return !isAlvaraEmAberto(selecionado) && !isAlvaraCalculado(selecionado);
    }

    public boolean canMostrarExcluir() {
        return isAlvaraEmAberto(selecionado) || isAlvaraCalculado(selecionado);
    }

    public boolean canRenderizarBtnAtualizarCalculo() {
        return canHabilitarBotaoEfetivarCalculo();
    }

    public boolean canRenderizarBtnRecalculo() {
        return isAlvaraEmAberto(selecionado) || isAlvaraEfetivado(selecionado) || isAlvaraRecalculado(selecionado);
    }

    public boolean canHabilitarBtnRecalculo() {
        if (isAlvaraEmAberto(selecionado)) return true;
        if (selecionado.getAlvara() != null && calculoAlvaraFacade.getProcessoSuspensaoCassacaoAlvaraFacade().alvaraCassado(selecionado.getAlvara().getId())) {
            return false;
        }
        if (selecionado.getAlvara() != null && calculoAlvaraFacade.getProcessoSuspensaoCassacaoAlvaraFacade().alvaraSuspenso(selecionado.getAlvara().getId())) {
            return false;
        }
        return !canHabilitarBotaoEfetivarCalculo();
    }

    public boolean canHabilitarBotaoEfetivarCalculo() {
        return !isAlvaraEstornado(selecionado) && (isAlvaraCalculado(selecionado) || !selecionado.buscarRecalculos().isEmpty() || hasCalculoParaCancelar());
    }

    public boolean hasImprimirDam() {
        boolean parcelaEmAberto = false;
        if (!isAlvaraEmAberto(selecionado)) {
            List<DAMResultadoParcela> damParcelas = buscarParcelasAtuais();
            for (DAMResultadoParcela parcela : damParcelas) {
                if (SituacaoParcela.EM_ABERTO.equals(parcela.getResultadoParcela().getSituacaoEnumValue())) {
                    parcelaEmAberto = true;
                    break;
                }
            }
        }
        return parcelaEmAberto;
    }

    public boolean hasImprimirDamRedeSim(LogAlvaraRedeSim log) {
        Date dataPgtoParcela = calculoAlvaraFacade.buscarDataPgtoParcela(selecionado.getId());
        return hasImprimirDam() && !(dataPgtoParcela != null && dataPgtoParcela.compareTo(log.getData()) >= 0);
    }

    public String buscarProtocoloRedeSIM() {
        if (Strings.isNullOrEmpty(protocoloRedeSim)) {
            protocoloRedeSim = calculoAlvaraFacade.buscarProtocoloRedeSIM(selecionado.getCadastroEconomico().getPessoa().getCpf_Cnpj());
        }
        return protocoloRedeSim;
    }

    public List<CalculoAlvara> buscarCalculosVer() {
        List<CalculoAlvara> calculos = Lists.newArrayList();
        calculos.addAll(selecionado.buscarCalculos());
        calculos.addAll(selecionado.buscarRecalculos());
        calculos = ordenarRecalculos(calculos);
        return calculos;
    }

    private List<CalculoAlvara> ordenarRecalculos(List<CalculoAlvara> calculosAlvara) {
        List<CalculoAlvara> recalculos = Lists.newArrayList();
        List<CalculoAlvara> calculos = Lists.newArrayList();

        for (CalculoAlvara calculoAlvara : calculosAlvara) {
            if (isCalculoRecalculado(calculoAlvara)) {
                recalculos.add(calculoAlvara);
            } else {
                calculos.add(calculoAlvara);
            }
        }
        ordenarCalculos(recalculos);
        ordenarCalculos(calculos);
        recalculos.addAll(calculos);

        return recalculos;
    }

    public List<CalculoAlvara> buscarCalculosAtuaisVer() {
        return selecionado.buscarCalculosAtuais();
    }

    public boolean isAlvaraEmAberto(Object selecionado) {
        if (selecionado instanceof ProcessoCalculoAlvara) {
            return SituacaoCalculoAlvara.EM_ABERTO.equals(((ProcessoCalculoAlvara) selecionado).getSituacaoCalculoAlvara());
        }
        return SituacaoCalculoAlvara.EM_ABERTO.equals(((VOAlvara) selecionado).getSituacaoCalculoAlvara());
    }

    private boolean isAlvaraCalculado(Object selecionado) {
        if (selecionado instanceof ProcessoCalculoAlvara) {
            return SituacaoCalculoAlvara.CALCULADO.equals(((ProcessoCalculoAlvara) selecionado).getSituacaoCalculoAlvara());
        }
        return SituacaoCalculoAlvara.CALCULADO.equals(((VOAlvara) selecionado).getSituacaoCalculoAlvara());
    }

    public boolean canImprimirDispensaLicenciamento() {
        return (isAlvaraRecalculado(selecionado) || isAlvaraEfetivado(selecionado)) &&
            selecionado.isDispensaLicenciamento();
    }

    private boolean isAlvaraEfetivado(Object selecionado) {
        if (selecionado instanceof ProcessoCalculoAlvara) {
            return SituacaoCalculoAlvara.EFETIVADO.equals(((ProcessoCalculoAlvara) selecionado).getSituacaoCalculoAlvara());
        }
        return SituacaoCalculoAlvara.EFETIVADO.equals(((VOAlvara) selecionado).getSituacaoCalculoAlvara());
    }

    private boolean isAlvaraRecalculado(Object selecionado) {
        if (selecionado instanceof ProcessoCalculoAlvara) {
            return SituacaoCalculoAlvara.RECALCULADO.equals(((ProcessoCalculoAlvara) selecionado).getSituacaoCalculoAlvara());
        }
        return SituacaoCalculoAlvara.RECALCULADO.equals(((VOAlvara) selecionado).getSituacaoCalculoAlvara());
    }

    private boolean isAlvaraEstornado(Object selecionado) {
        if (selecionado instanceof ProcessoCalculoAlvara) {
            return SituacaoCalculoAlvara.ESTORNADO.equals(((ProcessoCalculoAlvara) selecionado).getSituacaoCalculoAlvara());
        }
        return SituacaoCalculoAlvara.ESTORNADO.equals(((VOAlvara) selecionado).getSituacaoCalculoAlvara());
    }

    private boolean isCalculoAtual(CalculoAlvara calculoAlvara) {
        return TipoControleCalculoAlvara.CALCULO.equals(calculoAlvara.getControleCalculo());
    }


    private boolean isCalculoCancelado(CalculoAlvara calculoAlvara) {
        return TipoControleCalculoAlvara.CANCELADO.equals(calculoAlvara.getControleCalculo());
    }

    public void atualizarDadosCnae() {
        selecionado = calculoAlvaraFacade.atualizarDadosCnae(selecionado);
    }

    public void imprimirAlvaraRedeSim(LogAlvaraRedeSim logAlvaraRedeSim) {
        try {
            preencherInformacoesRecibo();
            Alvara alvara = calculoAlvaraFacade.buscarAlvaraPorId(logAlvaraRedeSim.getIdAlvara());
            if (alvara != null) {
                calculoAlvaraFacade.adicionaReciboImpressaoAlvara(alvara, recibo);
            }
            imprimirArquivoRedeSim(logAlvaraRedeSim.getArquivo().getDetentorArquivoComposicao().getArquivoComposicao());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao imprimir pdf rede sim. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao imprimir PDF Alvará. Detalhes: " + e.getMessage());
        }
    }

    public void imprimirArquivoRedeSim(ArquivoComposicao arquivoComposicao) {
        calculoAlvaraFacade.imprimirArquivoRedeSim(arquivoComposicao);
    }

    public void preencherInformacoesRecibo() {
        UsuarioSistema usuarioSistema = calculoAlvaraFacade.recuperarUsuarioCorrente();
        recibo = new ReciboImpressaoAlvara();
        recibo.setDataImpressao(new Date());

        if (usuarioSistema != null) {
            recibo.setUsuarioSistema(usuarioSistema);
            recibo.setNomeResposavel(usuarioSistema.getPessoaFisica().getNome());
            recibo.setCpfResposavel(usuarioSistema.getPessoaFisica().getCpf());
            recibo.setTelefoneResposavel(usuarioSistema.getPessoaFisica().getTelefonePrincipal() != null ? usuarioSistema.getPessoaFisica().getTelefonePrincipal().getTelefone() : null);
        }
    }

    public boolean isArquivoDAM(TipoArquivoLogAlvaraRedeSim tipoArquivo) {
        return TipoArquivoLogAlvaraRedeSim.DAM.equals(tipoArquivo);
    }

    public boolean isArquivoAlvara(TipoArquivoLogAlvaraRedeSim tipoArquivo) {
        return TipoArquivoLogAlvaraRedeSim.ALVARA.equals(tipoArquivo);
    }

    public boolean isArquivoBcm(TipoArquivoLogAlvaraRedeSim tipoArquivo) {
        return TipoArquivoLogAlvaraRedeSim.BCM.equals(tipoArquivo);
    }

    public boolean isArquivoDispensaLicenciamento(TipoArquivoLogAlvaraRedeSim tipoArquivo) {
        return TipoArquivoLogAlvaraRedeSim.DISPENSA_LICENCIAMENTO.equals(tipoArquivo);
    }

    public boolean isArquivoBcmOrDispensaLicenciamento(TipoArquivoLogAlvaraRedeSim tipoArquivo) {
        return isArquivoBcm(tipoArquivo) || isArquivoDispensaLicenciamento(tipoArquivo);
    }

    private void ordenarLogs() {
        Collections.sort(selecionado.getLogsRedeSim(), new Comparator<LogAlvaraRedeSim>() {
            @Override
            public int compare(LogAlvaraRedeSim o1, LogAlvaraRedeSim o2) {
                return ComparisonChain.start().compare(o1.getData(), o2.getData()).result();
            }
        });
    }

    public BigDecimal buscarValorUFMCalculo(Long id, BigDecimal valorEfetivo) {
        if (mapaUfmBigDecimalCalculos == null) {
            mapaUfmBigDecimalCalculos = Maps.newHashMap();
        }
        if (!mapaUfmBigDecimalCalculos.containsKey(id)) {
            BigDecimal valorUFM = BigDecimal.ZERO;
            try {
                valorUFM = calculoAlvaraFacade.converterToUFMParaExercicio(valorEfetivo, selecionado.getExercicio());
            } catch (UFMException e) {
                logger.error("Erro ao converter ufm. ", e);
            }
            mapaUfmBigDecimalCalculos.put(id, valorUFM);
        }
        BigDecimal ufm = mapaUfmBigDecimalCalculos.get(id);
        return ufm != null ? ufm : BigDecimal.ZERO;
    }

    public enum TipoSemAssinatura implements EnumComDescricao {
        SEM_ASSINATURA("SEM ASSINATURA NA ORIGEM");

        String descricao;

        TipoSemAssinatura(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }
    }

    private void verificarCalculosDeInteresseDoEstado() {
        if (SituacaoCalculoAlvara.RECALCULADO.equals(selecionado.getSituacaoCalculoAlvara())) {
            for (CalculoAlvara calculo : selecionado.buscarCalculosAtuais()) {
                if (calculoAlvaraFacade.hasCnaeInteresseDoEstadoComParcelaAberta(selecionado.getId(), calculo.getId())) {
                    calculo.setControleCalculo(TipoControleCalculoAlvara.AGUARDANDO_CANCELAMENTO);
                }
            }
        }
    }

    public void atribuirEstilosCalculo() {
        verificarCalculosDeInteresseDoEstado();
        for (CalculoAlvara calculo : selecionado.buscarCalculosAtuais()) {
            if (isCalculoRecalculado(calculo)) {
                estilosCalculo.put(calculo, "background-color: #8ce196");
            } else if (calculo.getControleCalculo().isAguardandoCancelamento()) {
                estilosCalculo.put(calculo, "background-color: #ff6354");
            } else {
                estilosCalculo.put(calculo, "");
            }
        }
    }

    public String getEstiloCalculo(CalculoAlvara calculo) {
        if (estilosCalculo.containsKey(calculo)) {
            return estilosCalculo.get(calculo);
        }
        return "";
    }

    public boolean hasCalculoParaCancelar() {
        if (selecionado != null) {
            for (CalculoAlvara calculo : selecionado.buscarCalculosAtuais()) {
                if (calculo.getControleCalculo().isAguardandoCancelamento()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void preencherInformacoesDeAlteracaoDosCalculosDeLocalizacao() {
        if (selecionado != null) {
            alteracoesPorCalculoLocalizacao = Maps.newHashMap();
            for (CalculoAlvara calculo : selecionado.buscarCalculosAtuais()) {
                AgrupadorVORevisaoCalculoLocalizacao wrapper = calculoAlvaraFacade
                    .preencherCnaesAndEnderecoPelaRevisao(selecionado.getCadastroEconomico().getId(), calculo.getId());
                alteracoesPorCalculoLocalizacao.put(calculo.getId(), wrapper);
            }
        }
    }

    public AgrupadorVORevisaoCalculoLocalizacao atribuirAlteracaoDoCalculo(Long idCalculo) {
        if (alteracoesPorCalculoLocalizacao != null && alteracoesPorCalculoLocalizacao.containsKey(idCalculo)) {
            alteracaoSelecionada = (AgrupadorVORevisaoCalculoLocalizacao) Util.clonarObjeto(alteracoesPorCalculoLocalizacao.get(idCalculo));
        }
        return alteracaoSelecionada;
    }

    public boolean isAlteracaoCnae(int index) {
        return alteracaoSelecionada.getAlteracoesCnae().size() > 1 && index == 0;
    }

    public boolean isAlteracaoEndereco(int index) {
        return alteracaoSelecionada.getAlteracoesEndereco().size() > 1 && index == 0;
    }

    public boolean canExibirMensagemAberturaExercicioCorrente() {
        if (selecionado != null && selecionado.getCadastroEconomico() != null) {
            boolean isAberturaNoExercicioCorrente = false;
            if (selecionado.getCadastroEconomico().getAbertura() != null) {
                isAberturaNoExercicioCorrente = selecionado.getCadastroEconomico().getAnoDeAbertura() == calculoAlvaraFacade.recuperarExercicioCorrente().getAno();
            }
            String complementoWhere = selecionado.getAlvara() != null ? " and al.id <> " + selecionado.getAlvara().getId() : "";
            Alvara alvara = calculoAlvaraFacade.recuperarUltimoAlvaraDoCmc(selecionado.getCadastroEconomico().getId(), complementoWhere);
            return isAberturaNoExercicioCorrente || alvara == null;
        }
        return false;
    }

    public String montarEnderecoVo(VOEndereco endereco) {
        try {
            String enderecoCompleto = "";
            if (endereco != null) {
                if (endereco.getTipoLogradouro() != null) {
                    enderecoCompleto += endereco.getTipoLogradouro().getDescricao() + " ";
                }
                enderecoCompleto += !StringUtils.isBlank(endereco.getLogradouro()) ? (endereco.getLogradouro() + " ") : "";
                enderecoCompleto += !StringUtils.isBlank(endereco.getNumero()) ? (", " + endereco.getNumero() + " ") : "";
                enderecoCompleto += !StringUtils.isBlank(endereco.getBairro()) ? ("- " + endereco.getBairro() + "") : "";
                enderecoCompleto += !StringUtils.isBlank(endereco.getCep()) ? (" (" + endereco.getCep() + ")") : "";
            }
            return enderecoCompleto.toUpperCase();
        } catch (Exception e) {
            logger.error("Erro ao montar endereco da pessoa. ", e);
            return "";
        }
    }

    public void imprimirDeclaracaoDispensaLicenciamento() {
        imprimirDeclaracaoDispensaLicenciamento(selecionado);
    }

    public void imprimirDeclaracaoDispensaLicenciamento(VOAlvara voAlvara) {
        ProcessoCalculoAlvara processoCalculoAlvara = calculoAlvaraFacade.recuperar(voAlvara.getId());
        imprimirDeclaracaoDispensaLicenciamento(processoCalculoAlvara);
    }

    public void imprimirDeclaracaoDispensaLicenciamento(ProcessoCalculoAlvara processoCalculoAlvara) {
        try {
            byte[] dados = calculoAlvaraFacade.gerarDeclaracaoDispensaLicenciamento(processoCalculoAlvara);
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            abstractReport.setGeraNoDialog(Boolean.TRUE);
            abstractReport.escreveNoResponse("Declaração de Dispensa de Licenciamento", dados);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    public void enviarDeclaracaoDispensaLicenciamentoRedeSim(VOAlvara voAlvara) {
        try {
            ProcessoCalculoAlvara processoCalculoAlvara = calculoAlvaraFacade.recuperar(voAlvara.getId());
            calculoAlvaraFacade.enviarDispensaLicenciamento(processoCalculoAlvara);
            FacesUtil.addOperacaoRealizada("Declaração de Dispensa de Licenciamento enviada para RedeSim com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            logger.error("Erro ao enviar declaração de dispensa de licenciamento a RedeSim. {}", e.getMessage());
            logger.debug("Detalhes do erro ao enviar declaração de dispensa de licenciamento a RedeSim.", e);
            FacesUtil.addErrorPadrao(e);
        }
    }
}
