package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.administrativo.frotas.ParametroFrotas;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoAquisicaoObjetoFrota;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EquipamentoFacade;
import br.com.webpublico.negocios.SistemaFacade;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 08/09/14
 * Time: 13:49
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "equipamentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "equipamentoNovo", pattern = "/frota/equipamento/novo/", viewId = "/faces/administrativo/frota/equipamentos/edita.xhtml"),
    @URLMapping(id = "equipamentoListar", pattern = "/frota/equipamento/listar/", viewId = "/faces/administrativo/frota/equipamentos/lista.xhtml"),
    @URLMapping(id = "equipamentoEditar", pattern = "/frota/equipamento/editar/#{equipamentoControlador.id}/", viewId = "/faces/administrativo/frota/equipamentos/edita.xhtml"),
    @URLMapping(id = "equipamentoVer", pattern = "/frota/equipamento/ver/#{equipamentoControlador.id}/", viewId = "/faces/administrativo/frota/equipamentos/visualizar.xhtml"),
})
public class EquipamentoControlador extends PrettyControlador<Equipamento> implements Serializable, CRUD {

    @EJB
    private EquipamentoFacade facade;
    private ProgramaRevisaoEquipamento programaRevisaoEquipamento;
    private UnidadeObjetoFrota unidadeObjetoFrota;
    private ConverterAutoComplete converterProgramaRevisao;
    private ConverterAutoComplete converterEquipamento;
    @EJB
    private SistemaFacade sistemaFacade;

    public EquipamentoControlador() {
        super(Equipamento.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/frota/equipamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public ProgramaRevisaoEquipamento getProgramaRevisaoEquipamento() {
        return programaRevisaoEquipamento;
    }

    public void setProgramaRevisaoEquipamento(ProgramaRevisaoEquipamento programaRevisaoEquipamento) {
        this.programaRevisaoEquipamento = programaRevisaoEquipamento;
    }

    public void limparDadosDoBem() {
        selecionado.setBem(null);
    }

    private void inicializarAtributosDaTela() {
        carregarUnidadeOrganizacional();
    }

    private void carregarUnidadeOrganizacional() {
        if (selecionado != null && selecionado.getUnidadeOrganizacional() != null) {
            selecionado.setHierarquiaOrganizacional(facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(
                facade.getSistemaFacade().getDataOperacao(),
                selecionado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
        }
    }

    @URLAction(mappingId = "equipamentoNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setHorasUso(BigDecimal.ZERO);
        selecionado.setHorasUsoAquisicao(BigDecimal.ZERO);
        inicializarAtributosDaTela();
    }

    @URLAction(mappingId = "equipamentoVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "equipamentoEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializarAtributosDaTela();
    }

    public List<SelectItem> tipos() {
        return Util.getListSelectItem(Arrays.asList(TipoAquisicaoObjetoFrota.values()));
    }

    public void processarMudancaDeTipo() {
        try {
            selecionado.setBem(null);
            selecionado.setContrato(null);
            selecionado.setDataAquisicao(null);
            selecionado.setDescricao("");
            if (isTipoAquisicaoObjetoFrotaProprio()) {
                verificarSeExisteParametroFrotasComGrupoPatrimonial();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void processaSelecaoContrato() {
        if (selecionado.getContrato() != null) {
            selecionado.setDataAquisicao(selecionado.getContrato().getInicioVigencia());
        } else {
            selecionado.setDataAquisicao(null);
        }
    }

    public List<Bem> completarBem(String parte) {
        List<Bem> list = Lists.newArrayList();
        if (selecionado.getHierarquiaOrganizacional() != null) {
            list = facade.getBemFacade().buscarBensUnidadeAdministrativaPorGrupoPatrimonialVigenteNoParametroFrota(parte.trim(), selecionado.getHierarquiaOrganizacional(), selecionado.getTipoObjetoFrota());
            if (list.isEmpty()) {
                FacesUtil.addOperacaoNaoPermitida("Nenhum bem encontrado para a Órgão/Entidade/Fundo: " + selecionado.getHierarquiaOrganizacional() + ".");
                return list;
            }
        }
        return list;
    }

    private void verificarSeExisteParametroFrotasComGrupoPatrimonial() {
        ValidacaoException ve = new ValidacaoException();
        if (!facade.getParametroFrotasFacade().verificarSeExisteConfiguracaoVigenteParaGrupoPatrimonial(TipoObjetoFrota.EQUIPAMENTO)) {
            ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "Não foi encontrada/não está vigente, nenhuma Configuração de Grupo Patrimonial do Tipo Equipamento/Máquina no Parâmetro do Frotas.");
        }
        ve.lancarException();
    }

    public List<Equipamento> completaEquipamentos(String parte) {
        return facade.buscarEquipamentosPorAnoOrDescricaoOrIdentificacaoOrNumeroProcessoContratoPorUnidadeQuePessoaTemPermissao(parte,sistemaFacade.getUsuarioCorrente());
    }

    @Override
    public void salvar() {
        try {
            validarDadosDoEquipamento();
            atribuirDataDeAquisicaoAoVeiculo();
            facade.salvarNovo(selecionado);
            FacesUtil.addOperacaoRealizada("Registro Salvo com Sucesso!");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
        }
    }

    private void atribuirDataDeAquisicaoAoVeiculo() {
        if (isTipoAquisicaoObjetoFrotaProprio()) {
            if (selecionado.getBem() != null && selecionado.getBem().getDataAquisicao() != null) {
                selecionado.setDataAquisicao(selecionado.getBem().getDataAquisicao());
            }
        }
    }

    public boolean isTipoAquisicaoObjetoFrotaProprio() {
        return TipoAquisicaoObjetoFrota.PROPRIO.equals(selecionado.getTipoAquisicaoObjetoFrota());
    }

    public boolean isTipoAquisicaoObjetoFrotaAlugado() {
        return TipoAquisicaoObjetoFrota.ALUGADO.equals(selecionado.getTipoAquisicaoObjetoFrota());
    }


    private void validarRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        if (isTipoAquisicaoObjetoFrotaProprio()
            && (selecionado.getBem() == null || selecionado.getBem().getId() == null)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Bem deve ser informado.");
        }
        if (isTipoAquisicaoObjetoFrotaAlugado()
            && (selecionado.getContrato() == null || selecionado.getContrato().getId() == null)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Contrato deve ser informado.");
        }
        if (selecionado.getHorasUso().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A(s) hora(s) de uso informada deve ser maior que zero.");
        }
        if (selecionado.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão/Entidade/Fundo deve ser informado.");
        } else {
            selecionado.setUnidadeOrganizacional(selecionado.getHierarquiaOrganizacional().getSubordinada());
            if (facade.identificacaoRegistrada(selecionado)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um equipamento cadastrado com a identificação: " + selecionado.getIdentificacao() + ".");
            }
        }
        ve.lancarException();
    }

    public void processaSelecaoCedido() {
        selecionado.setCedidoPor(null);
        selecionado.setTipoAquisicaoObjetoFrota(null);
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalFrotasFilhas(String parte) {
        if (selecionado.getHierarquiaOrganizacional() != null) {
            return facade.getHierarquiaOrganizacionalFacade().listaHierarquiasFilhasDeUmaHierarquiaOrganizacionalAdministrativa(
                selecionado.getHierarquiaOrganizacional(),
                facade.getSistemaFacade().getUsuarioCorrente(),
                facade.getSistemaFacade().getDataOperacao(),
                parte.trim());
        }
        return new ArrayList();
    }

    private void validaInformacoesProgramacaoRevisaoEquipamento(ProgramaRevisaoEquipamento programaRevisaoEquipamento) {
        ValidacaoException ve = new ValidacaoException();
        if (programaRevisaoEquipamento.getPrazoEmSegundos().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo 'Vencimento por Horas' deve ser informado com um valor maior que 0(zero).");
        }

        if (programaRevisaoEquipamento.getPrazoEmMeses() <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Vencimento por Prazo' deve ser informado com um valor maior que 0(zero).");
        }

        ve.lancarException();
    }

    public void adicionarProgramaRevisaoEquipamento() {
        try {
            validaInformacoesProgramacaoRevisaoEquipamento(programaRevisaoEquipamento);
            if (selecionado.getRevisoesEquipamento() == null) {
                selecionado.setRevisoesEquipamento(new ArrayList());
            }
            programaRevisaoEquipamento.setEquipamento(selecionado);
            selecionado.getRevisoesEquipamento().add(programaRevisaoEquipamento);
            organizaSequenciaProgramaEquipamento();
            cancelarProgramaRevisaoEquipamento();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void removeProgramaRevisaoEquipamento(ProgramaRevisaoEquipamento programaRevisaoEquipamento) {
        selecionado.getRevisoesEquipamento().remove(programaRevisaoEquipamento);
        organizaSequenciaProgramaEquipamento();
    }

    public void inicializarProgramaRevisaoEquipamento() {
        programaRevisaoEquipamento = new ProgramaRevisaoEquipamento();
        programaRevisaoEquipamento.setSequencia(0);
        programaRevisaoEquipamento.setPrazoEmMeses(0);
        programaRevisaoEquipamento.setPrazoEmSegundos(BigDecimal.ZERO);
    }

    public void cancelarProgramaRevisaoEquipamento() {
        programaRevisaoEquipamento = null;
    }

    private void organizaSequenciaProgramaEquipamento() {
        int tamanho = selecionado.getRevisoesEquipamento().size();
        for (int i = 0; i < tamanho; i++) {
            selecionado.getRevisoesEquipamento().get(i).setSequencia(i + 1);
        }
    }

    private void validarDadosDoEquipamento() {
        ValidacaoException ve = new ValidacaoException();

        if (selecionado.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão/Entidade/Fundo deve ser informado.");
        }

        if (Strings.isNullOrEmpty(selecionado.getIdentificacao().trim())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Identificação deve ser informado.");
        }

        if (!selecionado.isCedido() && isTipoAquisicaoObjetoFrotaProprio() && selecionado.getBem() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Bem Patrimonial deve ser informado.");
        }

        if (!selecionado.isCedido() && isTipoAquisicaoObjetoFrotaAlugado() && selecionado.getContrato() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Contrato deve ser informado.");
        }

        if ((selecionado.isCedido() || isTipoAquisicaoObjetoFrotaAlugado()) && Strings.isNullOrEmpty(selecionado.getDescricao())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Descrição deve ser informado.");
        }

        if ((!selecionado.isCedido()) && selecionado.getTipoAquisicaoObjetoFrota() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Frota deve ser informado.");
        }
        if (selecionado.getAno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }

        if (selecionado.getHorasUso() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Hora(s) de Uso deve ser informado.");
        }
        if (selecionado.getHorasUsoAquisicao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Hora(s) de Uso deve ser informado.");
        }
        if (selecionado.getUnidades().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O equipamento não possui unidade adicionada.");
        }
        ve.lancarException();
        validarRegrasEspecificas();
    }

    public void verificarVencimentoDasRevisoes(Equipamento equipamento) {
        ParametroFrotas parametroFrotas = facade.getParametroFrotasFacade().buscarParametroFrotas();
        if (parametroFrotas == null) {
            return;
        }

        equipamento = facade.recuperarEquipamentoAndRevisoes(equipamento.getId());

        long diasProximoAVencer = parametroFrotas.getDiasDaRevisaoAVencer();
        BigDecimal horasProximoAVencer = parametroFrotas.getSegundosRevisaoAVencer();
        Date dataAquisicao = equipamento.getDataAquisicao();
        Date dataAtual = facade.getSistemaFacade().getDataOperacao();
        long diasDeUso = Util.diferencaDeDiasEntreData(dataAquisicao, dataAtual);

        if (equipamento.getRevisoesEquipamento() != null) {
            for (ProgramaRevisaoEquipamento programaRevisao : equipamento.getRevisoesEquipamento()) {
                if (!programaRevisao.getRevisaoRealizada()) {
                    boolean isParaNotificarRevisaoVencendo = false;

                    long diferencaPrazoDias = ((programaRevisao.getPrazoEmMeses()) * 30) - diasDeUso;
                    if (diferencaPrazoDias > 0 && diferencaPrazoDias <= diasProximoAVencer) {
                        isParaNotificarRevisaoVencendo = true;
                    }

                    BigDecimal diferencaPrazoHoras = equipamento.getHorasUsoAquisicao().add(programaRevisao.getPrazoEmSegundos()).subtract(equipamento.getHoraPercorrida().getHoraAtual());
                    if (diferencaPrazoHoras.compareTo(BigDecimal.ZERO) > 0 && diferencaPrazoHoras.compareTo(horasProximoAVencer) <= 0) {
                        isParaNotificarRevisaoVencendo = true;
                    }

                    if (isParaNotificarRevisaoVencendo) {
                        FacesUtil.addAtencao("O equipamento " + programaRevisao.getEquipamento() + " possui revisão(ões) próxima(s) do vencimento.");
                        return;
                    }
                }
            }
        }
    }

    public void novaUnidade() {
        unidadeObjetoFrota = new UnidadeObjetoFrota();
        unidadeObjetoFrota.setObjetoFrota(selecionado);
    }

    public void cancelarUnidade() {
        unidadeObjetoFrota = null;
    }

    public void adicionarUnidade() {
        try {
            Util.validarCampos(unidadeObjetoFrota);
            validarVigencia();
            selecionado.setHierarquiaOrganizacional(unidadeObjetoFrota.getHierarquiaOrganizacional());
            selecionado.setUnidadeOrganizacionalResp(null);
            selecionado.setHierarquiaOrganizacionalResponsavel(null);
            Util.adicionarObjetoEmLista(selecionado.getUnidades(), unidadeObjetoFrota);
            if (isTipoAquisicaoObjetoFrotaProprio()) {
                limparDadosDoBem();
            }
            cancelarUnidade();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarVigencia() {
        ValidacaoException ve = new ValidacaoException();
        if (unidadeObjetoFrota.getFimVigencia() != null && unidadeObjetoFrota.getFimVigencia().before(unidadeObjetoFrota.getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O fim de vigência deve ser posterior ao início de vigência.");
        }
        ve.lancarException();
        for (UnidadeObjetoFrota unidadeAdicionada : selecionado.getUnidades()) {
            if (!unidadeAdicionada.equals(unidadeObjetoFrota)) {
                unidadeAdicionada.validarVigencia(unidadeObjetoFrota);
            }
        }
    }

    public void editarUnidade(UnidadeObjetoFrota obj) {
        this.unidadeObjetoFrota = obj;
    }

    public void removerUnidade(UnidadeObjetoFrota obj) {
        for (UnidadeObjetoFrota unidade : selecionado.getUnidades()) {
            if (!unidade.equals(obj)) {
                if (unidade.getFimVigencia() == null || Util.getDataHoraMinutoSegundoZerado(unidade.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(facade.getSistemaFacade().getDataOperacao())) >= 0) {
                    selecionado.setHierarquiaOrganizacional(unidade.getHierarquiaOrganizacional());
                }
            } else {
                selecionado.setHierarquiaOrganizacional(null);
                selecionado.setUnidadeOrganizacional(null);
            }
        }
        limparDadosDoBem();
        selecionado.setUnidadeOrganizacionalResp(null);
        selecionado.setHierarquiaOrganizacionalResponsavel(null);
        selecionado.getUnidades().remove(obj);
    }

    public ConverterAutoComplete getConverterProgramaRevisao() {
        if (converterProgramaRevisao == null) {
            converterProgramaRevisao = new ConverterAutoComplete(ProgramaRevisaoEquipamento.class, facade.getProgramaRevisaoEquipamentoFacade());
        }
        return converterProgramaRevisao;
    }

    public ConverterAutoComplete getConverterEquipamento() {
        if (converterEquipamento == null) {
            converterEquipamento = new ConverterAutoComplete(Equipamento.class, facade);
        }
        return converterEquipamento;
    }

    public UnidadeObjetoFrota getUnidadeObjetoFrota() {
        return unidadeObjetoFrota;
    }

    public void setUnidadeObjetoFrota(UnidadeObjetoFrota unidadeObjetoFrota) {
        this.unidadeObjetoFrota = unidadeObjetoFrota;
    }
}
