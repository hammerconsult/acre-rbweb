package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.EmendaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
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
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "alterar-emenda", pattern = "/alterar-emenda/", viewId = "/faces/financeiro/emenda/emenda-cad/alterar-emenda.xhtml")
})
public class AlteracaoEmendaControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(AlteracaoEmendaControlador.class);
    @EJB
    private EmendaFacade emendaFacade;
    private Emenda selecionado;
    private List<SuplementacaoEmenda> suplementacoes;
    private List<SuplementacaoEmenda> suplementacoesClones;
    private List<AnulacaoEmenda> anulacoes;
    private List<AnulacaoEmenda> anulacoesClones;
    private List<BeneficiarioEmenda> beneficiarios;
    private List<BeneficiarioEmenda> beneficiariosClones;
    private List<ResponsavelBenefEmenda> responsaveisClone;
    private List<HistoricoEmenda> historicos;
    private List<HistoricoAlteracaoEmenda> historicosAlteracoes;
    private SuplementacaoEmenda suplementacaoEmenda;
    private AnulacaoEmenda anulacaoEmenda;
    private BeneficiarioEmenda beneficiarioEmenda;
    private ResponsavelBenefEmenda responsavelBenefEmenda;
    private HierarquiaOrganizacional orcamentariaSuplementacao;
    private HierarquiaOrganizacional orcamentariaAnulacao;
    private List<HierarquiaOrganizacional> orcamentariasUsuario;
    private ConfiguracaoEmenda configuracaoEmenda;
    private String motivo;
    private String objetoResumido;
    private String descricaoDetalhada;
    private HistoricoAlteracaoEmenda historicoAnexo;

    @URLAction(mappingId = "alterar-emenda", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        selecionado = null;
        suplementacoes = Lists.newArrayList();
        suplementacoesClones = Lists.newArrayList();
        anulacoes = Lists.newArrayList();
        anulacoesClones = Lists.newArrayList();
        beneficiarios = Lists.newArrayList();
        beneficiariosClones = Lists.newArrayList();
        responsaveisClone = Lists.newArrayList();
        historicos = Lists.newArrayList();
        historicosAlteracoes = Lists.newArrayList();
        orcamentariaSuplementacao = null;
        orcamentariaAnulacao = null;
        motivo = "";
        historicoAnexo = new HistoricoAlteracaoEmenda();
        recuperarOrcamentariasUsuario();
        buscarConfiguracaoEmenda();
    }

    private void buscarConfiguracaoEmenda() {
        configuracaoEmenda = emendaFacade.getConfiguracaoEmenda();
        if (configuracaoEmenda == null) {
            FacesUtil.addAtencao("Não foi encontrado Configuração de Emenda para o exercício " + emendaFacade.getSistemaFacade().getExercicioCorrente().getAno() +
                ". Para cadastrar uma nova emenda, é necessário que seja efetuado a Configuração da Emenda.");
        }
    }

    private void recuperarOrcamentariasUsuario() {
        List<HierarquiaOrganizacional> orcamentaria = emendaFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", emendaFacade.getSistemaFacade().getUsuarioCorrente(), emendaFacade.getSistemaFacade().getExercicioCorrente(), emendaFacade.getSistemaFacade().getDataOperacao(), "ORCAMENTARIA", 3);
        orcamentariasUsuario = Lists.newArrayList();
        orcamentariasUsuario.add(new HierarquiaOrganizacional(null, null, "", ""));
        if (orcamentaria != null && !orcamentaria.isEmpty()) {
            orcamentariasUsuario.addAll(orcamentaria);
        }
    }

    public void salvar() {
        try {
            validarCampos();
            montarHistorico();
            selecionado.setObjetoResumido(objetoResumido);
            selecionado.setDescricaoDetalhada(descricaoDetalhada);
            emendaFacade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada("A Emenda foi alterada com sucesso.");
            novo();
            FacesUtil.atualizarComponente("Formulario");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao salvar o registro: " + ex.getMessage());
            logger.error("Erro ao salvar a alteração da emenda: {}", ex);
        }
    }

    public void cancelar() {
        selecionado = null;
    }

    public BigDecimal getTotalSuplementacoes() {
        BigDecimal total = BigDecimal.ZERO;
        if (suplementacoes != null && !suplementacoes.isEmpty()) {
            for (SuplementacaoEmenda sup : suplementacoes) {
                total = total.add(sup.getValor());
            }
        }
        return total;
    }

    public BigDecimal getTotalAnulacoes() {
        BigDecimal total = BigDecimal.ZERO;
        if (anulacoes != null && !anulacoes.isEmpty()) {
            for (AnulacaoEmenda anul : anulacoes) {
                total = total.add(anul.getValor());
            }
        }
        return total;
    }

    public void adicionarSuplementacao() {
        try {
            validarSuplementacao();
            Util.adicionarObjetoEmLista(suplementacoes, suplementacaoEmenda);
            suplementacaoEmenda = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarSuplementacao() {
        Util.validarCampos(suplementacaoEmenda);
        ValidacaoException ve = new ValidacaoException();
        boolean hasRepetida = false;
        for (SuplementacaoEmenda sup : suplementacoes) {
            if (!sup.equals(suplementacaoEmenda)
                && suplementacaoEmenda.getAcaoPPA().equals(sup.getAcaoPPA())
                && suplementacaoEmenda.getSubAcaoPPA().equals(sup.getSubAcaoPPA())
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
            Util.adicionarObjetoEmLista(anulacoes, anulacaoEmenda);
            anulacaoEmenda = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarAnulacao() {
        Util.validarCampos(anulacaoEmenda);
        ValidacaoException ve = new ValidacaoException();
        boolean hasRepetida = false;
        for (AnulacaoEmenda anul : anulacoes) {
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

    private void montarHistorico() {
        recuperarSelecionado();
        StringBuilder historico = new StringBuilder();
        montarHistoricoSuplementacao(historico);
        montarHistoricoAnulacao(historico);
        montarHistoricoBeneficiario(historico);
        HistoricoAlteracaoEmenda historicoAlteracaoEmenda = new HistoricoAlteracaoEmenda();
        historicoAlteracaoEmenda.setDataAlteracao(new Date());
        historicoAlteracaoEmenda.setEmenda(selecionado);
        historicoAlteracaoEmenda.setUsuarioSistema(emendaFacade.getSistemaFacade().getUsuarioCorrente());
        historicoAlteracaoEmenda.setHistorico(historico.toString());
        historicoAlteracaoEmenda.setMotivo(motivo);
        if (historicoAnexo.getDetentorArquivoComposicao() != null && historicoAnexo.getDetentorArquivoComposicao().getArquivoComposicao() != null) {
            historicoAlteracaoEmenda.setDetentorArquivoComposicao(historicoAnexo.getDetentorArquivoComposicao());
            historicoAnexo = null;
        }
        for (SuplementacaoEmenda suplementacao : suplementacoes) {
            Util.adicionarObjetoEmLista(selecionado.getSuplementacaoEmendas(), suplementacao);
        }
        for (AnulacaoEmenda anulacao : anulacoes) {
            Util.adicionarObjetoEmLista(selecionado.getAnulacaoEmendas(), anulacao);
        }
        for (BeneficiarioEmenda beneficiario : beneficiarios) {
            selecionado.getBeneficiarioEmendas().clear();
            Util.adicionarObjetoEmLista(selecionado.getBeneficiarioEmendas(), beneficiario);

        }
        Util.adicionarObjetoEmLista(selecionado.getHistoricosAlteracoes(), historicoAlteracaoEmenda);
    }

    private void montarHistoricoSuplementacao(StringBuilder historico) {
        for (SuplementacaoEmenda suplementacaoEmenda : suplementacoes) {
            for (SuplementacaoEmenda suplementacaoClone : suplementacoesClones) {
                if (suplementacaoClone.equals(suplementacaoEmenda)) {
                    if (!suplementacaoClone.getUnidadeOrganizacional().equals(suplementacaoEmenda.getUnidadeOrganizacional())) {
                        historico.append(" Alteração no Acréscimo, Unidade Orçamentária Original: <b>").append(buscarHierarquiaOrcamentaria(suplementacaoClone.getUnidadeOrganizacional())).append(
                            "</b> para a nova Unidade Orçamentária: <b>").append(buscarHierarquiaOrcamentaria(suplementacaoEmenda.getUnidadeOrganizacional())).append("</b> </br>");
                    }
                    if (!suplementacaoClone.getAcaoPPA().equals(suplementacaoEmenda.getAcaoPPA())) {
                        historico.append(" Alteração no Acréscimo, Sub-Projeto/Atividade Original: <b>").append(suplementacaoClone.getAcaoPPA()).append(
                            "</b> para o novo Sub-Projeto/Atividade: <b>").append(suplementacaoEmenda.getAcaoPPA()).append("</b> </br>");
                    }
                    if (!suplementacaoClone.getSubAcaoPPA().equals(suplementacaoEmenda.getSubAcaoPPA())) {
                        historico.append(" Alteração no Acréscimo, Projeto/Atividade Original: <b>").append(suplementacaoClone.getSubAcaoPPA()).append(
                            "</b> para o novo Projeto/Atividade: <b>").append(suplementacaoEmenda.getSubAcaoPPA()).append("</b> </br>");
                    }
                    if (!suplementacaoClone.getConta().equals(suplementacaoEmenda.getConta())) {
                        historico.append(" Alteração no Acréscimo, Conta de Despesa Original: <b>").append(suplementacaoClone.getConta()).append(
                            "</b> para a nova Conta de Despesa: <b>").append(suplementacaoEmenda.getConta()).append("</b> </br>");
                    }
                    if (!suplementacaoClone.getDestinacaoDeRecursos().equals(suplementacaoEmenda.getDestinacaoDeRecursos())) {
                        historico.append(" Alteração no Acréscimo, Fonte de Recurso Original: <b>").append(suplementacaoClone.getContaAsDestinacao().getFonteDeRecursos()).append(
                            "</b> para a nova Fonte de Recurso: <b>").append(suplementacaoEmenda.getContaAsDestinacao().getFonteDeRecursos()).append("</b> </br>");
                    }
                }
            }
        }
    }

    private void montarHistoricoAnulacao(StringBuilder historico) {
        for (AnulacaoEmenda anulacao : anulacoes) {
            for (AnulacaoEmenda anulacaoClone : anulacoesClones) {
                if (anulacaoClone.equals(anulacao)) {
                    if (!anulacaoClone.getUnidadeOrganizacional().equals(anulacao.getUnidadeOrganizacional())) {
                        historico.append(" Alteração no Cancelamento, Unidade Orçamentária Original: <b>").append(buscarHierarquiaOrcamentaria(anulacaoClone.getUnidadeOrganizacional())).append(
                            "</b> para a nova Unidade Orçamentária: <b>").append(buscarHierarquiaOrcamentaria(anulacao.getUnidadeOrganizacional())).append("</b> </br>");
                    }
                    if (!anulacaoClone.getAcaoPPA().equals(anulacao.getAcaoPPA())) {
                        historico.append(" Alteração no Cancelamento, Sub-Projeto/Atividade Original: <b>").append(anulacaoClone.getAcaoPPA()).append(
                            "</b> para o novo Sub-Projeto/Atividade: <b>").append(anulacao.getAcaoPPA()).append("</b> </br>");
                    }
                    if (!anulacaoClone.getSubAcaoPPA().equals(anulacao.getSubAcaoPPA())) {
                        historico.append(" Alteração no Cancelamento, Projeto/Atividade Original: <b>").append(anulacaoClone.getSubAcaoPPA()).append(
                            "</b> para o novo Projeto/Atividade: <b>").append(anulacao.getSubAcaoPPA()).append("</b> </br>");
                    }
                    if (!anulacaoClone.getConta().equals(anulacao.getConta())) {
                        historico.append(" Alteração no Cancelamento, Conta de Despesa Original: <b>").append(anulacaoClone.getConta()).append(
                            "</b> para a nova Conta de Despesa: <b>").append(anulacao.getConta()).append("</b> </br>");
                    }
                    if (!anulacaoClone.getDestinacaoDeRecursos().equals(anulacao.getDestinacaoDeRecursos())) {
                        historico.append(" Alteração no Cancelamento, Fonte de Recurso Original: <b>").append(anulacaoClone.getContaAsDestinacao().getFonteDeRecursos()).append(
                            "</b> para a nova Fonte de Recurso: <b>").append(anulacao.getContaAsDestinacao().getFonteDeRecursos()).append("</b> </br>");
                    }
                }
            }
        }
    }

    private void montarHistoricoBeneficiario(StringBuilder historico) {
        for (BeneficiarioEmenda beneficiario : beneficiarios) {
            for (BeneficiarioEmenda beneficiarioClone : beneficiariosClones) {
                if (beneficiario.equals(beneficiarioClone)) {
                    if (!beneficiario.getPessoa().equals(beneficiarioClone.getPessoa())) {
                        historico.append(" Alteração na Entidade, Entidade Original: <b>").append(beneficiarioClone.getPessoa()).append(
                            "</b> para a nova Entidade: <b>").append(beneficiario.getPessoa()).append("</b> </br>");
                    }
                    for (ResponsavelBenefEmenda responsavel : beneficiario.getResponsavelBeneficiarioEmendas()) {
                        for (ResponsavelBenefEmenda responsavelClone : responsaveisClone) {
                            if (responsavel.equals(responsavelClone) && !responsavel.getPessoa().equals(responsavelClone.getPessoa())) {
                                historico.append(" Alteração no Responsável da Entidade, Responsável Original: <b>").append(responsavelClone.getPessoa()).append(
                                    "</b> para o novo Responsável: <b>").append(responsavel.getPessoa()).append("</b> </br>");
                            }
                        }
                    }
                }
            }
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Emenda deve ser informado.");
        }
        ve.lancarException();
    }

    public List<Emenda> completarEmendas(String filtro) {
        return emendaFacade.buscarEmendasAprovadasPorExercicioAndVereador(filtro);
    }

    public List<AcaoPPA> completarProjetosAtividadesSuplementacao(String parte) {
        return buscarAcoesPPAPorUnidade(parte, suplementacaoEmenda.getUnidadeOrganizacional());
    }

    public List<AcaoPPA> completarProjetosAtividadesAnulacao(String parte) {
        return buscarAcoesPPAPorUnidade(parte, anulacaoEmenda.getUnidadeOrganizacional());
    }

    private List<AcaoPPA> buscarAcoesPPAPorUnidade(String parte, UnidadeOrganizacional unidadeOrganizacional) {
        if (unidadeOrganizacional == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Unidade Organizacional deve ser informado.");
            return Lists.newArrayList();
        }
        List<AcaoPPA> acoes = emendaFacade.getProvisaoPPAFacade().getProjetoAtividadeFacade().buscarAcoesPPAPorExercicioUnidade(parte.trim(), emendaFacade.getSistemaFacade().getExercicioCorrente(), unidadeOrganizacional, null);
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

    public List<SubAcaoPPA> completarSubProjetosAtividadesAnulacao(String parte) {
        if (anulacaoEmenda.getAcaoPPA() != null) {
            return emendaFacade.getProvisaoPPAFacade().getSubProjetoAtividadeFacade().buscarSubPorProjetoAtividades(parte.trim(), anulacaoEmenda.getAcaoPPA());
        }
        return Lists.newArrayList();
    }

    public List<Conta> completarContasDeDespesaSuplementacao(String parte) {
        if (suplementacaoEmenda.getAcaoPPA() != null && suplementacaoEmenda.getSubAcaoPPA() != null) {
            return emendaFacade.getContaFacade().buscarContasDeDespesaPorAcaoSubacaoAndUnidade(parte.trim(), suplementacaoEmenda.getSubAcaoPPA(), suplementacaoEmenda.getAcaoPPA(), suplementacaoEmenda.getUnidadeOrganizacional(), emendaFacade.getSistemaFacade().getExercicioCorrente(), "");
        }
        return Lists.newArrayList();
    }

    public List<Conta> completarContasDeDespesaAnulacao(String parte) {
        if (anulacaoEmenda.getAcaoPPA() != null && anulacaoEmenda.getSubAcaoPPA() != null) {
            return emendaFacade.getContaFacade().buscarContasDeDespesaPorAcaoSubacaoAndUnidade(parte.trim(), anulacaoEmenda.getSubAcaoPPA(), anulacaoEmenda.getAcaoPPA(), anulacaoEmenda.getUnidadeOrganizacional(), emendaFacade.getSistemaFacade().getExercicioCorrente(), "");
        }
        return Lists.newArrayList();
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

    public List<SelectItem> buscarFontes() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        List<ContaDeDestinacao> contas = emendaFacade.getProvisaoPPAFacade().getContaFacade().buscarContasDeDestinacaoPorCodigoOrDescricao("", emendaFacade.getSistemaFacade().getExercicioCorrente());
        for (Conta contaDestinacao : contas) {
            retorno.add(new SelectItem(contaDestinacao, ((ContaDeDestinacao) contaDestinacao).getFonteDeRecursos().toString()));
        }
        return retorno;
    }


    public void clonarEmenda() {
        if (selecionado != null) {
            recuperarSelecionado();
            objetoResumido = selecionado.getObjetoResumido();
            descricaoDetalhada = selecionado.getDescricaoDetalhada();
            suplementacoes = Lists.newArrayList();
            suplementacoesClones = Lists.newArrayList();
            anulacoes = Lists.newArrayList();
            anulacoesClones = Lists.newArrayList();
            beneficiarios = Lists.newArrayList();
            beneficiariosClones = Lists.newArrayList();
            responsaveisClone = Lists.newArrayList();
            historicos = Lists.newArrayList();
            historicosAlteracoes = Lists.newArrayList();
            for (SuplementacaoEmenda suplementacao : selecionado.getSuplementacaoEmendas()) {
                suplementacoes.add((SuplementacaoEmenda) Util.clonarObjeto(suplementacao));
                suplementacoesClones.add((SuplementacaoEmenda) Util.clonarObjeto(suplementacao));
            }
            for (AnulacaoEmenda anulacao : selecionado.getAnulacaoEmendas()) {
                anulacoes.add((AnulacaoEmenda) Util.clonarObjeto(anulacao));
                anulacoesClones.add((AnulacaoEmenda) Util.clonarObjeto(anulacao));
            }
            for (HistoricoEmenda historico : selecionado.getHistoricos()) {
                historicos.add((HistoricoEmenda) Util.clonarObjeto(historico));
            }
            for (HistoricoAlteracaoEmenda historico : selecionado.getHistoricosAlteracoes()) {
                historicosAlteracoes.add((HistoricoAlteracaoEmenda) Util.clonarObjeto(historico));
            }
            for (BeneficiarioEmenda beneficiario : selecionado.getBeneficiarioEmendas()) {
                clonarBeneficiario(beneficiario, beneficiarios, null);
                clonarBeneficiario(beneficiario, beneficiariosClones, responsaveisClone);
            }
        }
    }

    private void recuperarSelecionado() {
        selecionado = emendaFacade.recuperar(selecionado.getId());
    }

    private void clonarBeneficiario(BeneficiarioEmenda beneficiario, List<BeneficiarioEmenda> beneficiarios, List<ResponsavelBenefEmenda> responsaveis) {
        BeneficiarioEmenda beneficiarioEmenda = (BeneficiarioEmenda) Util.clonarObjeto(beneficiario);
        for (ResponsavelBenefEmenda responsavelBeneficiarioEmenda : beneficiario.getResponsavelBeneficiarioEmendas()) {
            ResponsavelBenefEmenda respBenef = (ResponsavelBenefEmenda) Util.clonarObjeto(responsavelBeneficiarioEmenda);
            respBenef.setBeneficiarioEmenda(beneficiarioEmenda);
            if (responsaveis != null) {
                Util.adicionarObjetoEmLista(responsaveis, respBenef);
            } else {
                Util.adicionarObjetoEmLista(beneficiarioEmenda.getResponsavelBeneficiarioEmendas(), respBenef);
            }
        }
        beneficiarios.add(beneficiarioEmenda);
    }

    public void editarSuplementacaoEmenda(SuplementacaoEmenda suplementacao) {
        orcamentariaSuplementacao = buscarHierarquiaOrcamentaria(suplementacao.getUnidadeOrganizacional());
        suplementacaoEmenda = (SuplementacaoEmenda) Util.clonarObjeto(suplementacao);
    }

    public void editarAnulacaoEmenda(AnulacaoEmenda anulacao) {
        orcamentariaAnulacao = buscarHierarquiaOrcamentaria(anulacao.getUnidadeOrganizacional());
        anulacaoEmenda = (AnulacaoEmenda) Util.clonarObjeto(anulacao);
    }

    public void adicionarResponsavelNoBeneficiario() {
        Util.adicionarObjetoEmLista(beneficiarios, beneficiarioEmenda);
        beneficiarioEmenda = null;
    }

    public void adicionarResponsavel() {
        try {
            validarResponsavel();
            responsavelBenefEmenda.setBeneficiarioEmenda(beneficiarioEmenda);
            Util.adicionarObjetoEmLista(beneficiarioEmenda.getResponsavelBeneficiarioEmendas(), responsavelBenefEmenda);
            responsavelBenefEmenda = null;
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

    private HierarquiaOrganizacional buscarHierarquiaOrcamentaria(UnidadeOrganizacional unidadeOrganizacional) {
        return emendaFacade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade("ORCAMENTARIA", unidadeOrganizacional, selecionado.getDataEmenda());
    }

    public void atualizarUnidadeSuplementacao() {
        if (orcamentariaSuplementacao != null) {
            suplementacaoEmenda.setUnidadeOrganizacional(orcamentariaSuplementacao.getSubordinada());
        } else {
            suplementacaoEmenda.setUnidadeOrganizacional(null);
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

    public void atualizarUnidadeAnulacao() {
        if (orcamentariaAnulacao != null) {
            anulacaoEmenda.setUnidadeOrganizacional(orcamentariaAnulacao.getSubordinada());
        } else {
            anulacaoEmenda.setUnidadeOrganizacional(null);
        }
    }

    public void adicionarBeneficiario() {
        try {
            validarBeneficiario();
            beneficiarioEmenda.setEmenda(selecionado);
            Util.adicionarObjetoEmLista(beneficiarios, beneficiarioEmenda);
            beneficiarioEmenda = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarBeneficiario() {
        Util.validarCampos(beneficiarioEmenda);
        ValidacaoException ve = new ValidacaoException();
        boolean hasRepetido = false;
        for (BeneficiarioEmenda benef : beneficiarios) {
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

    public void atribuirBeneficiarioNoResponsavel(BeneficiarioEmenda beneficiario) {
        beneficiarioEmenda = beneficiario;
        responsavelBenefEmenda = null;
    }

    public void editarBeneficiarioEmenda(BeneficiarioEmenda beneficiario) {
        beneficiarioEmenda = (BeneficiarioEmenda) Util.clonarObjeto(beneficiario);
        for (ResponsavelBenefEmenda responsavelBeneficiarioEmenda : beneficiario.getResponsavelBeneficiarioEmendas()) {
            ResponsavelBenefEmenda respBenef = (ResponsavelBenefEmenda) Util.clonarObjeto(responsavelBeneficiarioEmenda);
            respBenef.setBeneficiarioEmenda(beneficiarioEmenda);
            Util.adicionarObjetoEmLista(beneficiarioEmenda.getResponsavelBeneficiarioEmendas(), respBenef);
        }
    }

    public void editarResponsavelBeneficiarioEmenda(ResponsavelBenefEmenda responsavel) {
        responsavelBenefEmenda = (ResponsavelBenefEmenda) Util.clonarObjeto(responsavel);
    }

    public Emenda getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Emenda selecionado) {
        this.selecionado = selecionado;
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

    public HierarquiaOrganizacional getOrcamentariaAnulacao() {
        return orcamentariaAnulacao;
    }

    public void setOrcamentariaAnulacao(HierarquiaOrganizacional orcamentariaAnulacao) {
        this.orcamentariaAnulacao = orcamentariaAnulacao;
    }

    public List<HierarquiaOrganizacional> getOrcamentariasUsuario() {
        return orcamentariasUsuario;
    }

    public void setOrcamentariasUsuario(List<HierarquiaOrganizacional> orcamentariasUsuario) {
        this.orcamentariasUsuario = orcamentariasUsuario;
    }

    public List<SuplementacaoEmenda> getSuplementacoes() {
        return suplementacoes;
    }

    public void setSuplementacoes(List<SuplementacaoEmenda> suplementacoes) {
        this.suplementacoes = suplementacoes;
    }

    public List<AnulacaoEmenda> getAnulacoes() {
        return anulacoes;
    }

    public void setAnulacoes(List<AnulacaoEmenda> anulacoes) {
        this.anulacoes = anulacoes;
    }

    public List<BeneficiarioEmenda> getBeneficiarios() {
        return beneficiarios;
    }

    public void setBeneficiarios(List<BeneficiarioEmenda> beneficiarios) {
        this.beneficiarios = beneficiarios;
    }

    public List<HistoricoEmenda> getHistoricosOrdenados() {
        Collections.sort(historicos, new Comparator<HistoricoEmenda>() {
            @Override
            public int compare(HistoricoEmenda o1, HistoricoEmenda o2) {
                return o1.getDataAlteracao().compareTo(o2.getDataAlteracao());
            }
        });
        return historicos;
    }

    public List<HistoricoAlteracaoEmenda> getHistoricosAlteracoesOrdenados() {
        Collections.sort(historicosAlteracoes, new Comparator<HistoricoAlteracaoEmenda>() {
            @Override
            public int compare(HistoricoAlteracaoEmenda o1, HistoricoAlteracaoEmenda o2) {
                return o1.getDataAlteracao().compareTo(o2.getDataAlteracao());
            }
        });
        return historicosAlteracoes;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public HistoricoAlteracaoEmenda getHistoricoAnexo() {
        return historicoAnexo;
    }

    public void setHistoricoAnexo(HistoricoAlteracaoEmenda historicoAnexo) {
        this.historicoAnexo = historicoAnexo;
    }

    public String getObjetoResumido() {
        return objetoResumido;
    }

    public void setObjetoResumido(String objetoResumido) {
        this.objetoResumido = objetoResumido;
    }

    public String getDescricaoDetalhada() {
        return descricaoDetalhada;
    }

    public void setDescricaoDetalhada(String descricaoDetalhada) {
        this.descricaoDetalhada = descricaoDetalhada;
    }
}
