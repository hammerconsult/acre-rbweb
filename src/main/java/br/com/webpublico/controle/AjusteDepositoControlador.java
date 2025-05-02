/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoAjusteDeposito;
import br.com.webpublico.enums.TipoAjuste;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AjusteDepositoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @author claudio
 */

@ManagedBean(name = "ajusteDepositoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-ajuste-deposito", pattern = "/ajuste-deposito/novo/", viewId = "/faces/financeiro/extraorcamentario/ajustedeposito/edita.xhtml"),
    @URLMapping(id = "edita-ajuste-deposito", pattern = "/ajuste-deposito/editar/#{ajusteDepositoControlador.id}/", viewId = "/faces/financeiro/extraorcamentario/ajustedeposito/edita.xhtml"),
    @URLMapping(id = "listar-ajuste-deposito", pattern = "/ajuste-deposito/listar/", viewId = "/faces/financeiro/extraorcamentario/ajustedeposito/lista.xhtml"),
    @URLMapping(id = "ver-ajuste-deposito", pattern = "/ajuste-deposito/ver/#{ajusteDepositoControlador.id}/", viewId = "/faces/financeiro/extraorcamentario/ajustedeposito/visualizar.xhtml")
})
public class AjusteDepositoControlador extends PrettyControlador<AjusteDeposito> implements Serializable, CRUD {

    @EJB
    private AjusteDepositoFacade facade;
    private BigDecimal saldoContaExtra;
    private List<ReceitaExtra> receitasExtras;
    private ReceitaExtra[] receitasExtrasSelecionadas;
    private SubConta contaFinanceira;

    public AjusteDepositoControlador() {
        super(AjusteDeposito.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/ajuste-deposito/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-ajuste-deposito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataAjuste(facade.getSistemaFacade().getDataOperacao());
        selecionado.setTipoAjuste(TipoAjuste.AUMENTATIVO);
        selecionado.setSituacao(SituacaoAjusteDeposito.ABERTO);
        selecionado.setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
        saldoContaExtra = BigDecimal.ZERO;
        if (facade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso!", facade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    @URLAction(mappingId = "edita-ajuste-deposito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-ajuste-deposito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void atribuirEvento() {
        selecionado.setEventoContabil(null);
        try {
            limparCamposAoAlterarTipoAjuste();
            if (selecionado.getTipoAjuste() != null
                && selecionado.getContaExtraorcamentaria() != null
                && selecionado.getContaExtraorcamentaria().getTipoContaExtraorcamentaria() != null) {
                EventoContabil eventoContabil = facade.recuperarEventoContabil(selecionado);
                if (eventoContabil != null) {
                    selecionado.setEventoContabil(eventoContabil);
                }
            }
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida("" + e.getMessage());
        }
    }

    public void recuperarSaldoContaExtraorcamentaria() {
        if (selecionado.getContaDeDestinacao() != null && selecionado.getUnidadeOrganizacional() != null && selecionado.getContaExtraorcamentaria() != null) {
            selecionado.setFonteDeRecurso(selecionado.getContaDeDestinacao().getFonteDeRecursos());
            SaldoExtraorcamentario saldoExtraorcamentario = facade.getSaldoExtraorcamentarioFacade().recuperaUltimoSaldoPorData(selecionado.getDataAjuste(), selecionado.getContaExtraorcamentaria(), selecionado.getContaDeDestinacao(), selecionado.getUnidadeOrganizacional());
            if (saldoExtraorcamentario != null && saldoExtraorcamentario.getId() != null) {
                saldoContaExtra = saldoExtraorcamentario.getValor();
            } else {
                saldoContaExtra = BigDecimal.ZERO;
            }
        }
    }

    public boolean renderizarSaldoExtra() {
        return selecionado.getContaExtraorcamentaria() != null && selecionado.getFonteDeRecurso() != null;
    }

    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                facade.salvarNovo(selecionado, contaFinanceira);
            } else {
                facade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarCampos() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor deve ser maior que zero (0).");
        }
        if (selecionado.getEventoContabil() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Nenhum evento contábil encontrado para este lançamento!");
        }
        ve.lancarException();
    }

    public List<TipoAjuste> getTiposAjuste() {
        List<TipoAjuste> lista = new ArrayList<TipoAjuste>();
        lista.addAll(Arrays.asList(TipoAjuste.values()));
        return lista;
    }

    public void buscarReceitaExtra() {
        try {
            validarBuscaReceitaExtra();
            receitasExtras = facade.getReceitaExtraFacade().buscarReceitaExtraAbertaPorContaExtraUnidadeFonteAndContaFinanceira(
                selecionado.getUnidadeOrganizacional(),
                selecionado.getContaExtraorcamentaria(),
                selecionado.getFonteDeRecurso(),
                contaFinanceira);
            FacesUtil.executaJavaScript("dlgReceitaExtra.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarBuscaReceitaExtra() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getContaExtraorcamentaria() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo conta extraorçamentária deve ser informado.");
        }
        if (selecionado.getFonteDeRecurso() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo fonte de recurso deve ser informado.");
        }
        if (contaFinanceira == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo conta financeira deve ser informado.");
        }
        ve.lancarException();
    }

    private void validarAdicionarReceita() {
        ValidacaoException ve = new ValidacaoException();
        if (receitasExtrasSelecionadas.length == 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Para continuar a operação selecione uma ou mais receita(s) extraorçamentária(s).");
        }
        ve.lancarException();
    }

    public void removerReceitaExtra(AjusteDepositoReceitaExtra receitaExtra) {
        selecionado.getReceitasExtras().remove(receitaExtra);
        selecionado.setValor(selecionado.getValorTotalReceitaExtra());
    }

    public void adicionarReceitaExtra() {
        try {
            validarAdicionarReceita();
            boolean receitaExisteNaLista;
            for (int i = 0; i < receitasExtrasSelecionadas.length; i++) {
                receitaExisteNaLista = verificarMesmaReceitaEmLista(receitasExtrasSelecionadas[i]);
                if (!receitaExisteNaLista) {
                    AjusteDepositoReceitaExtra receitaExtra = new AjusteDepositoReceitaExtra();
                    receitaExtra.setReceitaExtra(receitasExtrasSelecionadas[i]);
                    receitaExtra.setAjusteDeposito(selecionado);
                    Util.adicionarObjetoEmLista(selecionado.getReceitasExtras(), receitaExtra);
                }
            }
            selecionado.setValor(selecionado.getValorTotalReceitaExtra());
            FacesUtil.executaJavaScript("dlgReceitaExtra.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private boolean verificarMesmaReceitaEmLista(ReceitaExtra receitasExtrasSelecionada) {
        for (AjusteDepositoReceitaExtra receitaAdicionada : selecionado.getReceitasExtras()) {
            if (receitaAdicionada.getReceitaExtra().equals(receitasExtrasSelecionada)) {
                FacesUtil.addOperacaoNaoPermitida(" A receita extraorçamentária: " + receitaAdicionada.getReceitaExtra() + " já foi adicionada na lista.");
                return true;
            }
        }
        return false;
    }

    private void limparCamposAoAlterarTipoAjuste() {
        receitasExtras = null;
        receitasExtrasSelecionadas = null;
        if (selecionado.isAjusteDiminutivo()) {
            selecionado.setValor(BigDecimal.ZERO);
            selecionado.setReceitasExtras(Lists.<AjusteDepositoReceitaExtra>newArrayList());
        }
    }

    public List<SubConta> completarContaFinanceira(String parte) {
        return facade.getSubContaFacade().listaPorUnidadeOrganizacional(parte.trim(),
            selecionado.getUnidadeOrganizacional(),
            facade.getSistemaFacade().getExercicioCorrente(),
            facade.getSistemaFacade().getDataOperacao());
    }

    public List<Pessoa> completarPessoas(String parte) {
        return facade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public List<ClasseCredor> completarClassesCredor(String parte) {
        return facade.getClasseCredorFacade().buscarClassesPorPessoa(parte, selecionado.getPessoa());
    }

    public List<ContaDeDestinacao> completarContasDeDestinacao(String parte) {
        return facade.getContaExtraorcamentariaFacade().buscarContasDeDestinacaoPorCodigoOrDescricao(parte, facade.getSistemaFacade().getExercicioCorrente());
    }

    public List<Conta> completarContasExtraorcamentarias(String parte) {
        return facade.getContaExtraorcamentariaFacade().listaFiltrandoExtraorcamentario(parte.trim(), facade.getSistemaFacade().getExercicioCorrente());
    }

    public void limparCamposContaExtra() {
        selecionado.setEventoContabil(null);
        selecionado.setFonteDeRecurso(null);
        selecionado.setContaDeDestinacao(null);
        saldoContaExtra = BigDecimal.ZERO;
    }

    public void limparClasseCredor() {
        selecionado.setClasseCredor(null);
    }

    public ParametroEvento getParametroEvento() {
        return facade.getParametroEvento(selecionado);
    }

    public BigDecimal getSaldoContaExtra() {
        return saldoContaExtra;
    }

    public void setSaldoContaExtra(BigDecimal saldoContaExtra) {
        this.saldoContaExtra = saldoContaExtra;
    }

    public List<ReceitaExtra> getReceitasExtras() {
        return receitasExtras;
    }

    public void setReceitasExtras(List<ReceitaExtra> receitasExtras) {
        this.receitasExtras = receitasExtras;
    }

    public ReceitaExtra[] getReceitasExtrasSelecionadas() {
        return receitasExtrasSelecionadas;
    }

    public void setReceitasExtrasSelecionadas(ReceitaExtra[] receitasExtrasSelecionadas) {
        this.receitasExtrasSelecionadas = receitasExtrasSelecionadas;
    }

    public SubConta getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(SubConta contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
    }
}
