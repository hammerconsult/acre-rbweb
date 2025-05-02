package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EmendaFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 16/06/15
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "emendaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-emenda", pattern = "/planejamento/emenda/novo/", viewId = "/faces/financeiro/emenda/emenda-cad/edita.xhtml"),
    @URLMapping(id = "editar-emenda", pattern = "/planejamento/emenda/editar/#{emendaControlador.id}/", viewId = "/faces/financeiro/emenda/emenda-cad/edita.xhtml"),
    @URLMapping(id = "ver-emenda", pattern = "/planejamento/emenda/ver/#{emendaControlador.id}/", viewId = "/faces/financeiro/emenda/emenda-cad/visualizar.xhtml"),
    @URLMapping(id = "listar-emenda", pattern = "/planejamento/emenda/listar/", viewId = "/faces/financeiro/emenda/emenda-cad/lista.xhtml")
})
public class EmendaControlador extends PrettyControlador<Emenda> implements Serializable, CRUD {

    @EJB
    private EmendaFacade emendaFacade;
    private ConverterAutoComplete converterSubProjetoAtividade;
    private SuplementacaoEmenda suplementacaoEmenda;
    private AnulacaoEmenda anulacaoEmenda;
    private BeneficiarioEmenda beneficiarioEmenda;
    private ResponsavelBenefEmenda responsavelBenefEmenda;
    private HierarquiaOrganizacional orcamentariaSuplementacao;
    private HierarquiaOrganizacional orcamentariaAnulacao;
    private List<HierarquiaOrganizacional> orcamentariasUsuario;
    private ConfiguracaoEmenda configuracaoEmenda;
    private String motivoRejeicao;

    public EmendaControlador() {
        super(Emenda.class);
    }

    @URLAction(mappingId = "novo-emenda", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(emendaFacade.getSistemaFacade().getExercicioCorrente());
        validarLoaEfetivadaParaExercicioCorrente();
        buscarConfiguracaoEmenda();
        selecionado.setModalidadeEmenda(ModalidadeEmenda.DIRETA);
        parametrosIniciais();
        verificarVereador();
        motivoRejeicao = "";
    }

    private void buscarConfiguracaoEmenda() {
        configuracaoEmenda = emendaFacade.getConfiguracaoEmenda();
        if (configuracaoEmenda == null) {
            FacesUtil.addAtencao("Não foi encontrado Configuração de Emenda para o exercício " + emendaFacade.getSistemaFacade().getExercicioCorrente().getAno() +
                ". Para cadastrar uma nova emenda, é necessário que seja efetuado a Configuração da Emenda.");
            redireciona();
        }
    }

    @URLAction(mappingId = "editar-emenda", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        buscarConfiguracaoEmenda();
        recuperarEditaVer();
        novaSuplementacaoEmenda();
        novaAnulacaoEmenda();
        novoBeneficiario();
        atualizarDespesa();
        motivoRejeicao = "";
    }

    @URLAction(mappingId = "ver-emenda", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        motivoRejeicao = "";
    }

    public void atualizarDespesa() {
        if (selecionado.getModalidadeEmenda().isIndireta()) {
            if (configuracaoEmenda.getContasDeDespesaIndiretas().isEmpty()) {
                FacesUtil.addAtencao("Não existe configuração de conta de despesa indireta para a emenda.");
            } else {
                Conta contaDespesa = null;
                for (ConfiguracaoEmendaConta configuracaoEmendaConta : configuracaoEmenda.getContasDeDespesaIndiretas()) {
                    if (configuracaoEmendaConta.getContaDespesa().getExercicio().equals(selecionado.getExercicio())) {
                        contaDespesa = configuracaoEmendaConta.getContaDespesa();
                        break;
                    }
                }
                if (contaDespesa == null) {
                    FacesUtil.addAtencao("Não existe configuração de conta de despesa indireta para a emenda no exercício " + selecionado.getExercicio().getAno() + ".");
                } else {
                    suplementacaoEmenda.setConta(contaDespesa);
                }
            }
            suplementacaoEmenda.setDestinacaoDeRecursos(buscarDestinacaoPelaConfiguracao());
            anulacaoEmenda.setDestinacaoDeRecursos(buscarDestinacaoPelaConfiguracao());
        } else {
            selecionado.getBeneficiarioEmendas().removeAll(selecionado.getBeneficiarioEmendas());
        }
    }

    private Conta buscarDestinacaoPelaConfiguracao() {
        if (configuracaoEmenda.getFontesDeRecurso().isEmpty()) {
            FacesUtil.addAtencao("Não existe configuração de fonte de recursos para a emenda.");
        } else {
            Conta destinacao = null;
            for (ConfiguracaoEmendaFonte configuracaoEmendaFonte : configuracaoEmenda.getFontesDeRecurso()) {
                if (configuracaoEmendaFonte.getContaDeDestinacao().getExercicio().equals(selecionado.getExercicio())) {
                    destinacao = configuracaoEmendaFonte.getContaDeDestinacao();
                    break;
                }
            }
            if (destinacao == null) {
                FacesUtil.addAtencao("Não existe configuração de fonte de recursos para a emenda no exercício " + selecionado.getExercicio().getAno() + ".");
            } else {
                return destinacao;
            }
        }
        return null;
    }

    private List<Conta> buscarContaDeDespesaDiretaPelaConfiguracao() {
        if (configuracaoEmenda.getContasDeDespesaDiretas().isEmpty()) {
            FacesUtil.addAtencao("Não existe configuração de conta de despesa direta para a emenda.");
        } else {
            List<Conta> contasDeDespesa = Lists.newArrayList();
            for (ConfiguracaoEmendaConta configuracaoEmendaConta : configuracaoEmenda.getContasDeDespesaDiretas()) {
                if (configuracaoEmendaConta.getContaDespesa().getExercicio().equals(selecionado.getExercicio())) {
                    Util.adicionarObjetoEmLista(contasDeDespesa, configuracaoEmendaConta.getContaDespesa());
                }
            }
            if (contasDeDespesa.isEmpty()) {
                FacesUtil.addAtencao("Não existe configuração de conta de despesa direta para a emenda no exercício " + selecionado.getExercicio().getAno() + ".");
            } else {
                return contasDeDespesa;
            }
        }
        return Lists.newArrayList();
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarAcrescimoAoSalvar();
            if (isOperacaoNovo()) {
                HistoricoEmenda historicoEmenda = gerarHistorico();
                historicoEmenda.setStatusNovoPrefeitura(StatusEmenda.ABERTO);
                historicoEmenda.setStatusNovoCamara(StatusEmenda.ABERTO);
                Util.adicionarObjetoEmLista(selecionado.getHistoricos(), historicoEmenda);
                emendaFacade.salvarNovo(selecionado);
            } else {
                if (!selecionado.getStatusCamara().isAberto() || !selecionado.getStatusPrefeitura().isAberto()) {
                    HistoricoEmenda historicoEmenda = gerarHistorico();
                    if (!selecionado.getStatusPrefeitura().isAberto()) {
                        historicoEmenda.setStatusAnteriorPrefeitura(selecionado.getStatusPrefeitura());
                        historicoEmenda.setStatusNovoPrefeitura(StatusEmenda.ABERTO);
                        selecionado.setStatusPrefeitura(StatusEmenda.ABERTO);
                    }

                    if (!selecionado.getStatusCamara().isAberto()) {
                        historicoEmenda.setStatusAnteriorCamara(selecionado.getStatusCamara());
                        historicoEmenda.setStatusNovoCamara(StatusEmenda.ABERTO);
                        selecionado.setStatusCamara(StatusEmenda.ABERTO);
                    }
                    Util.adicionarObjetoEmLista(selecionado.getHistoricos(), historicoEmenda);
                }
                emendaFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void verificarVereador() {
        if (selecionado.getVereador() != null && emendaFacade.hasLimiteAtingidoParaVereador(selecionado.getVereador(), configuracaoEmenda)) {
            FacesUtil.addAtencao("O vereador selecionado já possui " + configuracaoEmenda.getQuantidadeMaximaEmenda() + " emenda(s) feita(s) no exercício " + emendaFacade.getSistemaFacade().getExercicioCorrente().getAno() + ".");
            redireciona();
        }
    }

    public boolean canUsuarioAprovarOrReprovarCamara() {
        return emendaFacade.canUsuarioAprovarOrReprovar(TipoAprovacaoEmendaUsuario.CAMARA);
    }

    public boolean canUsuarioAprovarOrReprovarPrefeitura() {
        return emendaFacade.canUsuarioAprovarOrReprovar(TipoAprovacaoEmendaUsuario.PREFEITURA);
    }

    public void aprovarPelaCamara() {
        notificarDespesasExistentesNaFixacao();
        atualizarStatusCamara(StatusEmenda.APROVADO);
    }

    public void reprovarPelaCamara() {
        atualizarStatusCamara(StatusEmenda.REJEITADO);
    }

    private void atualizarStatusCamara(StatusEmenda status) {
        try {
            Util.validarCampos(selecionado);
            validarRejeicao(status);
            HistoricoEmenda historicoEmenda = gerarHistorico();
            if (StatusEmenda.REJEITADO.equals(status)) {
                historicoEmenda.setMotivoRejeicaoCamara(motivoRejeicao);
            }
            historicoEmenda.setStatusNovoCamara(status);
            historicoEmenda.setStatusAnteriorCamara(selecionado.getStatusCamara());
            Util.adicionarObjetoEmLista(selecionado.getHistoricos(), historicoEmenda);
            selecionado.setStatusCamara(status);
            movimentarAndRedirecionar(status, "câmara");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            logger.error("Erro ao alterar status pela camara: {} ", ex);
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Erro ao alterar status pela camara: {} ", ex);
            descobrirETratarException(ex);
        }
    }

    private void validarRejeicao(StatusEmenda status) {
        if (StatusEmenda.REJEITADO.equals(status) && Strings.isNullOrEmpty(motivoRejeicao)) {
            throw new ValidacaoException("O campo Motivo da Rejeição deve ser informado.");
        }
    }

    private HistoricoEmenda gerarHistorico() {
        HistoricoEmenda historicoEmenda = new HistoricoEmenda();
        historicoEmenda.setDataAlteracao(new Date());
        historicoEmenda.setUsuarioSistema(emendaFacade.getSistemaFacade().getUsuarioCorrente());
        historicoEmenda.setEmenda(selecionado);
        return historicoEmenda;
    }

    public void aprovarPelaPrefeitura() {
        notificarDespesasExistentesNaFixacao();
        atualizarStatusPrefeitura(StatusEmenda.APROVADO);
    }

    public void reprovarPelaPrefeitura() {
        atualizarStatusPrefeitura(StatusEmenda.REJEITADO);
    }

    private void atualizarStatusPrefeitura(StatusEmenda status) {
        try {
            Util.validarCampos(selecionado);
            validarRejeicao(status);
            HistoricoEmenda historicoEmenda = gerarHistorico();
            if (StatusEmenda.REJEITADO.equals(status)) {
                historicoEmenda.setMotivoRejeicaoPrefeitura(motivoRejeicao);
            }
            historicoEmenda.setStatusNovoPrefeitura(status);
            historicoEmenda.setStatusAnteriorPrefeitura(selecionado.getStatusCamara());
            Util.adicionarObjetoEmLista(selecionado.getHistoricos(), historicoEmenda);
            selecionado.setStatusPrefeitura(status);
            movimentarAndRedirecionar(status, "prefeitura");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            logger.error("Erro ao alterar status pela prefeitura: {} ", ex);
        } catch (Exception ex) {
            logger.error("Erro ao alterar status pela prefeitura: {} ", ex);
            descobrirETratarException(ex);
        }
    }

    private void movimentarAndRedirecionar(StatusEmenda status, String camaraOrPrefeitura) {
        if (selecionado.getStatusPrefeitura().isAprovado() && selecionado.getStatusCamara().isAprovado()) {
            emendaFacade.movimentarSuplementacoesAnulacoes(selecionado);
        } else {
            emendaFacade.salvar(selecionado);
        }
        FacesUtil.addOperacaoRealizada("O registro foi " + status.getDescricao() + " pela " + camaraOrPrefeitura + " com sucesso.");
        redireciona();
    }

    private void redirecionar() {
        FacesUtil.executaJavaScript("dialogGerarEmenda.show()");
    }

    private void validarAcrescimoAoSalvar() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getSuplementacaoEmendas().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Emenda deve possuir pelo menos um Acréscimo à Programação.");
        }
        ve.lancarException();
        BigDecimal totalSalvoPorVereador = emendaFacade.buscarValorTotalEmendaPorVereadorEExercicio(selecionado);
        if (configuracaoEmenda.getValorMaximoPorVereador().compareTo(totalSalvoPorVereador.add(selecionado.getTotalEmenda())) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da emenda <b>(" + Util.formataValor(selecionado.getTotalEmenda()) +
                ")</b> mais o valor de outras emendas ja geradas pelo vereador <b>(" + Util.formataValor(totalSalvoPorVereador) +
                ")</b> é superior ao valor máximo disponível <b>(" + Util.formataValor(configuracaoEmenda.getValorMaximoPorVereador()) + ")</b> para o vereador.");
        }
        ve.lancarException();
        notificarDespesasExistentesNaFixacao();
    }

    private void notificarDespesasExistentesNaFixacao() {
        if (selecionado.getModalidadeEmenda().isDireta()) {
            for (SuplementacaoEmenda acrescimo : selecionado.getSuplementacaoEmendas()) {
                notificarDespesaExistenteNaFixacao(acrescimo);
            }
        }
    }

    public void notificarDespesaExistenteNaFixacao(SuplementacaoEmenda acrescimo) {
        if (!acrescimo.getContaPrevistaLoa() && acrescimo.getConta() != null) {
            ProvisaoPPADespesa provisaoPPADespesa = emendaFacade.getSuplementacaoEmendaFacade().buscarDespesaOrcamento(acrescimo);
            if (provisaoPPADespesa != null) {
                FacesUtil.addAtencao("Atenção! A conta de despesa " + provisaoPPADespesa.getContaDeDespesa() + " está prevista na LOA. Ao aprovar a Emenda, não será gerada nova conta de despesa na fixação da despesa.");
            }
        }
    }


    public Pessoa getPessoaResponsavel() {
        try {
            return responsavelBenefEmenda.getPessoa();
        } catch (Exception e) {
            return null;
        }
    }

    public Pessoa getPessoaBeneficiario() {
        try {
            return beneficiarioEmenda.getPessoa();
        } catch (Exception e) {
            return null;
        }
    }

    private void recuperarEditaVer() {
        recuperarOrcamentariasUsuario();
        novaSuplementacaoEmenda();
        novaAnulacaoEmenda();
        novoBeneficiario();
    }

    private void parametrosIniciais() {
        selecionado.setDataEmenda(emendaFacade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(emendaFacade.getSistemaFacade().getUsuarioCorrente());
        Vereador vereador = emendaFacade.getVereadorFacade().buscarVereadorPorUsuarioAndExercicio(selecionado.getUsuarioSistema(), selecionado.getExercicio());
        if (vereador == null) {
            FacesUtil.addOperacaoNaoPermitida("O usuário logado não é um vereador, portanto não é possível realizar uma nova emenda.");
            redireciona();
        } else {
            selecionado.setVereador(vereador);
        }
        recuperarOrcamentariasUsuario();
        novaSuplementacaoEmenda();
        novaAnulacaoEmenda();
        novoBeneficiario();
    }

    public boolean canEditar() {
        return !selecionado.getStatusCamara().isAprovado() || !selecionado.getStatusPrefeitura().isAprovado();
    }

    public List<SelectItem> buscarFontesSuplementacao() {
        Conta conta = buscarDestinacaoPelaConfiguracao();
        List<SelectItem> retorno = Lists.newArrayList();
        if (conta != null) {
            List<Conta> contas = emendaFacade.getProvisaoPPAFacade().getContaFacade().listaFiltrandoDestinacaoRecursos(conta.getCodigo(), emendaFacade.getSistemaFacade().getExercicioCorrente());
            for (Conta contaDestinacao : contas) {
                retorno.add(new SelectItem(contaDestinacao, ((ContaDeDestinacao) contaDestinacao).getFonteDeRecursos().toString()));
            }
        }
        return retorno;
    }

    public List<SelectItem> buscarFontesCancelamento() {
        Conta conta = buscarDestinacaoPelaConfiguracao();
        List<SelectItem> retorno = Lists.newArrayList();
        if (conta != null) {
            List<Conta> contas;
            if (anulacaoEmenda.getAcaoPPA() != null && anulacaoEmenda.getSubAcaoPPA() != null && anulacaoEmenda.getConta() != null) {
                contas = emendaFacade.getContaFacade().buscarContasDeDestinacaoPorContaAcaoSubacaoAndUnidade(conta.getCodigo(), anulacaoEmenda.getConta(), anulacaoEmenda.getSubAcaoPPA(), anulacaoEmenda.getAcaoPPA(), anulacaoEmenda.getUnidadeOrganizacional(), emendaFacade.getSistemaFacade().getExercicioCorrente());
            } else {
                contas = emendaFacade.getProvisaoPPAFacade().getContaFacade().listaFiltrandoDestinacaoRecursos(conta.getCodigo(), emendaFacade.getSistemaFacade().getExercicioCorrente());
            }
            for (Conta contaDestinacao : contas) {
                retorno.add(new SelectItem(contaDestinacao, ((ContaDeDestinacao) contaDestinacao).getFonteDeRecursos().toString()));
            }
        }
        return retorno;
    }

    private void novaSuplementacaoEmenda() {
        suplementacaoEmenda = new SuplementacaoEmenda();
        orcamentariaSuplementacao = null;
    }

    private void novaAnulacaoEmenda() {
        anulacaoEmenda = new AnulacaoEmenda();
        orcamentariaAnulacao = null;
        if (selecionado.getModalidadeEmenda().isIndireta()) {
            anulacaoEmenda.setDestinacaoDeRecursos(buscarDestinacaoPelaConfiguracao());
        }
    }

    public void adicionarResponsavelNoBeneficiario() {
        Util.adicionarObjetoEmLista(selecionado.getBeneficiarioEmendas(), beneficiarioEmenda);
        beneficiarioEmenda = null;
        novoBeneficiario();
    }

    private void novoBeneficiario() {
        beneficiarioEmenda = new BeneficiarioEmenda();
    }

    private void novoResponsavel() {
        responsavelBenefEmenda = new ResponsavelBenefEmenda();
    }

    public void editarSuplementacaoEmenda(SuplementacaoEmenda suplementacao) {
        suplementacaoEmenda = (SuplementacaoEmenda) Util.clonarObjeto(suplementacao);
        orcamentariaSuplementacao = emendaFacade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade("ORCAMENTARIA", this.suplementacaoEmenda.getUnidadeOrganizacional(), selecionado.getDataEmenda());
    }

    public void editarAnulacaoEmenda(AnulacaoEmenda anulacao) {
        anulacaoEmenda = (AnulacaoEmenda) Util.clonarObjeto(anulacao);
        orcamentariaAnulacao = emendaFacade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade("ORCAMENTARIA", this.anulacaoEmenda.getUnidadeOrganizacional(), selecionado.getDataEmenda());
    }

    public void editarBeneficiarioEmenda(BeneficiarioEmenda beneficiario) {
        beneficiarioEmenda = beneficiario;
    }

    public void removerSuplementacaoEmenda(SuplementacaoEmenda suplementacao) {
        selecionado.getSuplementacaoEmendas().remove(suplementacao);
    }

    public void removerAnulacaoEmenda(AnulacaoEmenda anulacao) {
        selecionado.getAnulacaoEmendas().remove(anulacao);
    }

    public void removerBeneficiarioEmenda(BeneficiarioEmenda beneficiario) {
        selecionado.getBeneficiarioEmendas().remove(beneficiario);
    }

    public void removerResponsavelEmenda(ResponsavelBenefEmenda responsavel) {
        beneficiarioEmenda.getResponsavelBeneficiarioEmendas().remove(responsavel);
    }

    public List<AcaoPPA> completarProjetosAtividadesSuplementacao(String parte) {
        return buscarAcoesPPAPorUnidade(parte, suplementacaoEmenda.getUnidadeOrganizacional(), configuracaoEmenda.getCodigoPrograma());
    }

    public List<AcaoPPA> completarProjetosAtividadesAnulacao(String parte) {
        return buscarAcoesPPAPorUnidade(parte, anulacaoEmenda.getUnidadeOrganizacional(), null);
    }

    private List<AcaoPPA> buscarAcoesPPAPorUnidade(String parte, UnidadeOrganizacional unidadeOrganizacional, String codigoPrograma) {
        if (unidadeOrganizacional == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Unidade Organizacional deve ser informado.");
            return Lists.newArrayList();
        }
        List<AcaoPPA> acoes = emendaFacade.getProvisaoPPAFacade().getProjetoAtividadeFacade().buscarAcoesPPAPorExercicioUnidade(parte.trim(), emendaFacade.getSistemaFacade().getExercicioCorrente(), unidadeOrganizacional, codigoPrograma);
        if (acoes.isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("Nenhum Projeto/Atividade cadastrado para a unidade: " + unidadeOrganizacional + ".");
            return Lists.newArrayList();
        }
        return acoes;
    }

    public List<SubAcaoPPA> completarSubProjetosAtividadesSuplementacao(String parte) {
        if (suplementacaoEmenda.getAcaoPPA() != null) {
            return emendaFacade.getProvisaoPPAFacade().getSubProjetoAtividadeFacade().buscarSubPorProjetoAtividades(parte.trim(), suplementacaoEmenda.getAcaoPPA());
        }
        return Lists.newArrayList();
    }

    public List<Pessoa> completarPessoasJuridicas(String filtro) {
        return emendaFacade.getPessoaFacade().listaPessoasJuridicas(filtro, SituacaoCadastralPessoa.ATIVO);
    }

    public List<SubAcaoPPA> completarSubProjetosAtividadesAnulacao(String parte) {
        if (anulacaoEmenda.getAcaoPPA() != null) {
            return emendaFacade.getProvisaoPPAFacade().getSubProjetoAtividadeFacade().buscarSubPorProjetoAtividades(parte.trim(), anulacaoEmenda.getAcaoPPA());
        }
        return Lists.newArrayList();
    }

    public List<Conta> completarContasDeDespesaSuplementacao(String parte) {
        List<Conta> contas = buscarContaDeDespesaDiretaPelaConfiguracao();
        if (!contas.isEmpty()) {
            if (suplementacaoEmenda.getContaPrevistaLoa()) {
                if (suplementacaoEmenda.getAcaoPPA() != null && suplementacaoEmenda.getSubAcaoPPA() != null) {
                    return emendaFacade.getContaFacade().buscarContasDeDespesaPorAcaoSubacaoAndUnidade(parte.trim(), suplementacaoEmenda.getSubAcaoPPA(), suplementacaoEmenda.getAcaoPPA(), suplementacaoEmenda.getUnidadeOrganizacional(), emendaFacade.getSistemaFacade().getExercicioCorrente(), montarParametroContas(contas));
                }
            } else {
                return emendaFacade.getContaFacade().buscarContasDeDespesaFilhasDe(parte.trim(), montarParametroContas(contas), emendaFacade.getSistemaFacade().getExercicioCorrente());
            }
        }
        return Lists.newArrayList();
    }

    private String montarParametroContas(List<Conta> contasDeDespesa) {
        StringBuilder retorno = new StringBuilder();
        retorno.append(" and (");
        String or = "";
        for (Conta conta : contasDeDespesa) {
            retorno.append(or).append(" c.codigo like '").append(conta.getCodigoSemZerosAoFinal()).append("%' ");
            or = " or ";
        }
        retorno.append(") ");
        return retorno.toString();
    }

    public List<Conta> completarContasDeDespesaAnulacao(String parte) {
        if (anulacaoEmenda.getAcaoPPA() != null && anulacaoEmenda.getSubAcaoPPA() != null) {
            return emendaFacade.getContaFacade().buscarContasDeDespesaPorAcaoSubacaoAndUnidade(parte.trim(), anulacaoEmenda.getSubAcaoPPA(), anulacaoEmenda.getAcaoPPA(), anulacaoEmenda.getUnidadeOrganizacional(), emendaFacade.getSistemaFacade().getExercicioCorrente(), "");
        }
        return Lists.newArrayList();
    }

    public void atualizarDescricao() {
        suplementacaoEmenda.setDescricaoSubProjetoAtividade(suplementacaoEmenda.getPessoaJuridica() != null ? suplementacaoEmenda.getPessoaJuridica().getRazaoSocial() : "");
    }

    public List<SelectItem> getModalidades() {
        return Util.getListSelectItemSemCampoVazio(ModalidadeEmenda.values());
    }

    public List<SelectItem> getEsferasOrcamentarias() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (EsferaOrcamentaria eo : EsferaOrcamentaria.values()) {
            if (!EsferaOrcamentaria.ORCAMENTOGERAL.equals(eo)) {
                toReturn.add(new SelectItem(eo, eo.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getTiposEmendas() {
        return Util.getListSelectItem(TipoEmenda.values(), false);
    }

    private void recuperarOrcamentariasUsuario() {
        List<HierarquiaOrganizacional> orcamentaria = emendaFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", emendaFacade.getSistemaFacade().getUsuarioCorrente(), emendaFacade.getSistemaFacade().getExercicioCorrente(), emendaFacade.getSistemaFacade().getDataOperacao(), "ORCAMENTARIA", 3);
        orcamentariasUsuario = Lists.newArrayList();
        orcamentariasUsuario.add(new HierarquiaOrganizacional(null, null, "", ""));
        if (orcamentaria != null && !orcamentaria.isEmpty()) {
            orcamentariasUsuario.addAll(orcamentaria);
        }
    }

    public void adicionarSuplementacao() {
        try {
            validarSuplementacao();
            suplementacaoEmenda.setEmenda(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getSuplementacaoEmendas(), suplementacaoEmenda);
            novaSuplementacaoEmenda();
            atualizarDespesa();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarSuplementacao() {
        Util.validarCampos(suplementacaoEmenda);
        ValidacaoException ve = new ValidacaoException();
        boolean hasRepetida = false;
        for (SuplementacaoEmenda sup : selecionado.getSuplementacaoEmendas()) {
            if (!sup.equals(suplementacaoEmenda)
                && suplementacaoEmenda.getAcaoPPA().equals(sup.getAcaoPPA())
                && ((selecionado.getModalidadeEmenda().isIndireta() && suplementacaoEmenda.getPessoaJuridica().equals(sup.getPessoaJuridica()))
                || (selecionado.getModalidadeEmenda().isDireta() && suplementacaoEmenda.getSubAcaoPPA().equals(sup.getSubAcaoPPA())))
                && suplementacaoEmenda.getConta().equals(sup.getConta())
                && suplementacaoEmenda.getDestinacaoDeRecursos().equals(sup.getDestinacaoDeRecursos())
                && suplementacaoEmenda.getUnidadeOrganizacional().equals(sup.getUnidadeOrganizacional())) {
                hasRepetida = true;
                break;
            }
        }
        if (hasRepetida) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um acréscimo à programação idêntico adicionado na lista.");
        }
        ve.lancarException();
    }


    public void adicionarAnulacao() {
        try {
            validarAnulacao();
            anulacaoEmenda.setEmenda(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getAnulacaoEmendas(), anulacaoEmenda);
            novaAnulacaoEmenda();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarAnulacao() {
        Util.validarCampos(anulacaoEmenda);
        ValidacaoException ve = new ValidacaoException();
        boolean hasRepetida = false;
        for (AnulacaoEmenda anul : selecionado.getAnulacaoEmendas()) {
            if (!anul.equals(anulacaoEmenda) && anulacaoEmenda.getAcaoPPA().equals(anul.getAcaoPPA())
                && anulacaoEmenda.getSubAcaoPPA().equals(anul.getSubAcaoPPA())
                && anulacaoEmenda.getConta().equals(anul.getConta())
                && anulacaoEmenda.getDestinacaoDeRecursos().equals(anul.getDestinacaoDeRecursos())
                && anulacaoEmenda.getUnidadeOrganizacional().equals(anul.getUnidadeOrganizacional())) {
                hasRepetida = true;
                break;
            }
        }
        if (hasRepetida) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um cancelamento compensatórios idêntico adicionado na lista.");
        }
        ve.lancarException();
    }

    public void adicionarBeneficiario() {
        try {
            validarBeneficiario();
            beneficiarioEmenda.setEmenda(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getBeneficiarioEmendas(), beneficiarioEmenda);
            novoBeneficiario();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarBeneficiario() {
        Util.validarCampos(beneficiarioEmenda);
        ValidacaoException ve = new ValidacaoException();
        boolean hasRepetido = false;
        for (BeneficiarioEmenda benef : selecionado.getBeneficiarioEmendas()) {
            if (!benef.equals(beneficiarioEmenda) && beneficiarioEmenda.getPessoa().equals(benef.getPessoa())) {
                hasRepetido = true;
                break;
            }
        }
        if (hasRepetido) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma entidade idêntica adicionada na lista.");
        }
        ve.lancarException();
    }

    public void adicionarResponsavel() {
        try {
            validarResponsavel();
            responsavelBenefEmenda.setBeneficiarioEmenda(beneficiarioEmenda);
            Util.adicionarObjetoEmLista(beneficiarioEmenda.getResponsavelBeneficiarioEmendas(), responsavelBenefEmenda);
            novoResponsavel();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarResponsavel() {
        Util.validarCampos(responsavelBenefEmenda);
        ValidacaoException ve = new ValidacaoException();
        for (ResponsavelBenefEmenda resp : beneficiarioEmenda.getResponsavelBeneficiarioEmendas()) {
            if (!resp.equals(responsavelBenefEmenda)) {
                if (responsavelBenefEmenda.getPessoa().getId().equals(resp.getPessoa().getId())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um responsável idêntico adicionado na lista.");
                    break;
                }
            }
        }
        if (beneficiarioEmenda.getPessoa().getId().equals(responsavelBenefEmenda.getPessoa().getId())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Responsável deve ser diferente do Beneficiário.");
        }
        ve.lancarException();
    }


    public void atribuirBeneficiarioNoResponsavel(BeneficiarioEmenda beneficiario) {
        beneficiarioEmenda = beneficiario;
        novoResponsavel();
    }

    private LOA getLoaPorExecicio() {
        LOA loa = emendaFacade.getProjetoAtividadeFacade().getLoaFacade().listaUltimaLoaPorExercicio(emendaFacade.getSistemaFacade().getExercicioCorrente());
        if (loa != null) {
            return loa;
        }
        return null;
    }

    private void validarLoaEfetivadaParaExercicioCorrente() {
        LOA loa = getLoaPorExecicio();
        if (loa != null && loa.getEfetivada()) {
            FacesUtil.addOperacaoNaoPermitida("Não é possível realizar uma nova emenda. Pois a Loa para o exercício de " + emendaFacade.getSistemaFacade().getExercicioCorrente() + " já foi efetivida.");
            redireciona();
        }
    }

    public ConverterAutoComplete getConverterSubProjetoAtividade() {
        if (converterSubProjetoAtividade == null) {
            converterSubProjetoAtividade = new ConverterAutoComplete(SubAcaoPPA.class, emendaFacade.getSubProjetoAtividadeFacade());
        }
        return converterSubProjetoAtividade;
    }

    public SuplementacaoEmenda getSuplementacaoEmenda() {
        return suplementacaoEmenda;
    }

    public void setSuplementacaoEmenda(SuplementacaoEmenda suplementacaoEmenda) {
        this.suplementacaoEmenda = suplementacaoEmenda;
    }

    public AnulacaoEmenda getAnulacaoEmenda() {
        return anulacaoEmenda;
    }

    public void setAnulacaoEmenda(AnulacaoEmenda anulacaoEmenda) {
        this.anulacaoEmenda = anulacaoEmenda;
    }

    public BeneficiarioEmenda getBeneficiarioEmenda() {
        return beneficiarioEmenda;
    }

    public void setBeneficiarioEmenda(BeneficiarioEmenda beneficiarioEmenda) {
        this.beneficiarioEmenda = beneficiarioEmenda;
    }

    public ResponsavelBenefEmenda getResponsavelBenefEmenda() {
        return responsavelBenefEmenda;
    }

    public void setResponsavelBenefEmenda(ResponsavelBenefEmenda responsavelBenefEmenda) {
        this.responsavelBenefEmenda = responsavelBenefEmenda;
    }

    public HierarquiaOrganizacional getOrcamentariaSuplementacao() {
        return orcamentariaSuplementacao;
    }

    public void setOrcamentariaSuplementacao(HierarquiaOrganizacional orcamentariaSuplementacao) {
        this.orcamentariaSuplementacao = orcamentariaSuplementacao;
    }

    public List<HierarquiaOrganizacional> getOrcamentariasUsuario() {
        return orcamentariasUsuario;
    }

    public void setOrcamentariasUsuario(List<HierarquiaOrganizacional> orcamentariasUsuario) {
        this.orcamentariasUsuario = orcamentariasUsuario;
    }

    public HierarquiaOrganizacional getOrcamentariaAnulacao() {
        return orcamentariaAnulacao;
    }

    public void setOrcamentariaAnulacao(HierarquiaOrganizacional orcamentariaAnulacao) {
        this.orcamentariaAnulacao = orcamentariaAnulacao;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public Date getDataOperacao() {
        return emendaFacade.getSistemaFacade().getDataOperacao();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/planejamento/emenda/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return emendaFacade;
    }
}
