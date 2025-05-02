/*
 * Codigo gerado automaticamente em Thu Sep 27 09:50:10 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoAjusteDisponivel;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AjusteAtivoDisponivelFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "ajusteAtivoDisponivelControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-ajuste-ativo-disponivel", pattern = "/ajuste-ativo-disponivel/novo/", viewId = "/faces/financeiro/extraorcamentario/ajusteativodisponivel/edita.xhtml"),
    @URLMapping(id = "editar-ajuste-ativo-disponivel", pattern = "/ajuste-ativo-disponivel/editar/#{ajusteAtivoDisponivelControlador.id}/", viewId = "/faces/financeiro/extraorcamentario/ajusteativodisponivel/edita.xhtml"),
    @URLMapping(id = "ver-ajuste-ativo-disponivel", pattern = "/ajuste-ativo-disponivel/ver/#{ajusteAtivoDisponivelControlador.id}/", viewId = "/faces/financeiro/extraorcamentario/ajusteativodisponivel/visualizar.xhtml"),
    @URLMapping(id = "listar-ajuste-ativo-disponivel", pattern = "/ajuste-ativo-disponivel/listar/", viewId = "/faces/financeiro/extraorcamentario/ajusteativodisponivel/lista.xhtml")
})
public class AjusteAtivoDisponivelControlador extends PrettyControlador<AjusteAtivoDisponivel> implements Serializable, CRUD {

    @EJB
    private AjusteAtivoDisponivelFacade ajusteAtivoDisponivelFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterClasseCredor;
    private ConfigAjusteAtivoDisponivel configAjusteAtivoDisponivel;
    private BigDecimal saldoContaFinanceira;

    public AjusteAtivoDisponivelControlador() {
        super(AjusteAtivoDisponivel.class);
    }

    public AjusteAtivoDisponivelFacade getFacade() {
        return ajusteAtivoDisponivelFacade;
    }

    public ConfigAjusteAtivoDisponivel getConfigAjusteAtivoDisponivel() {
        return configAjusteAtivoDisponivel;
    }

    @Override
    public AbstractFacade getFacede() {
        return ajusteAtivoDisponivelFacade;
    }

    @URLAction(mappingId = "novo-ajuste-ativo-disponivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataAjuste(sistemaControlador.getDataOperacao());
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        configAjusteAtivoDisponivel = null;
        selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        if (ajusteAtivoDisponivelFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso!", ajusteAtivoDisponivelFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    @URLAction(mappingId = "ver-ajuste-ativo-disponivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-ajuste-ativo-disponivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
    }

    public void recuperarEditarVer() {
        selecionado = ajusteAtivoDisponivelFacade.recuperar(selecionado.getId());
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        hierarquiaOrganizacional.setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ORCAMENTARIA);
        hierarquiaOrganizacional.setSubordinada(selecionado.getUnidadeOrganizacional());
        hierarquiaOrganizacional = ajusteAtivoDisponivelFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataAjuste(), selecionado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
    }

    public void recuperaSaldoContaFinanceira() {
        BigDecimal saldo = BigDecimal.ZERO;
        if (selecionado.getContaDeDestinacao() != null && selecionado.getUnidadeOrganizacional() != null && selecionado.getContaFinanceira() != null && selecionado.getDataAjuste() != null) {
            selecionado.setFonteDeRecursos(selecionado.getContaDeDestinacao().getFonteDeRecursos());
            SaldoSubConta saldoSubConta = ajusteAtivoDisponivelFacade.getSaldoSubContaFacade().recuperaUltimoSaldoSubContaPorData(selecionado.getUnidadeOrganizacional(), selecionado.getContaFinanceira(), selecionado.getContaDeDestinacao(), selecionado.getDataAjuste());
            if (saldoSubConta != null && saldoSubConta.getId() != null) {
                saldo = saldoSubConta.getTotalCredito().subtract(saldoSubConta.getTotalDebito());
            }
        }
        saldoContaFinanceira = saldo;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        selecionado.realizarValidacoes();
        if (selecionado.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor deve ser maior que zero (0).");
        }
        if (selecionado.getTipoLancamento().equals(TipoLancamento.ESTORNO) && selecionado.getValor().compareTo(ajusteAtivoDisponivelFacade.valorSaldoAjuste(selecionado)) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor a ser estornado de " + "<b>" + Util.formataValor(selecionado.getValor()) + "</b>" + " é maior que o saldo disponível de " + "<b>" + Util.formataValor((ajusteAtivoDisponivelFacade.valorSaldoAjuste(selecionado))) + "</b>" + " para esta operação.");
        }
        if (selecionado.getEventoContabil() == null || selecionado.getEventoContabil().getId() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Nenhum evento contábil encontrado para este lançamento.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (operacao.equals(Operacoes.NOVO)) {
                ajusteAtivoDisponivelFacade.salvarNovo(selecionado);
            } else {
                ajusteAtivoDisponivelFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(" Registro salvo com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            ajusteAtivoDisponivelFacade.getSingletonConcorrenciaContabil().tratarExceptionConcorrenciaSaldos(ex);
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public List<SelectItem> getListaTipoLancamento() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoLancamento tl : TipoLancamento.values()) {
            toReturn.add(new SelectItem(tl, tl.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoAjusteDisponivel() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAjusteDisponivel tad : TipoAjusteDisponivel.values()) {
            toReturn.add(new SelectItem(tad, tad.toString()));
        }
        return toReturn;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return ajusteAtivoDisponivelFacade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public void setaPessoa(SelectEvent evt) {
        AjusteAtivoDisponivel aj = ((AjusteAtivoDisponivel) selecionado);
        Pessoa p = (Pessoa) evt.getObject();
        aj.setPessoa(p);
        aj.setClasseCredor(null);

    }

    public void buscarEvento() {
        selecionado.setFonteDeRecursos(null);
        selecionado.setContaDeDestinacao(null);
        try {
            if (selecionado.getTipoLancamento() != null
                && selecionado.getTipoAjusteDisponivel() != null) {
                configAjusteAtivoDisponivel = ajusteAtivoDisponivelFacade.getConfigAjusteDisponivelFacade().getUltimaConfiguracao(selecionado);
                selecionado.setEventoContabil(configAjusteAtivoDisponivel.getEventoContabil());
            }
        } catch (ExcecaoNegocioGenerica e) {
            logger.error(e.getMessage());
        }
    }

    public String getDescricaoEvento() {
        ConfigAjusteAtivoDisponivel configuracao;
        selecionado.setEventoContabil(null);
        try {
            if (selecionado.getTipoLancamento() != null && selecionado.getTipoAjusteDisponivel() != null
                && selecionado.getDataAjuste() != null) {
                configuracao = ajusteAtivoDisponivelFacade.getConfigAjusteDisponivelFacade().getUltimaConfiguracao(selecionado);
                selecionado.setEventoContabil(configuracao.getEventoContabil());
                return selecionado.getEventoContabil().toString();
            } else {
                return "Nenhum evento encontrado";
            }
        } catch (ExcecaoNegocioGenerica e) {
            return e.getMessage();
        }
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, ajusteAtivoDisponivelFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public List<ClasseCredor> completaClasseCredor(String parte) {
        return ajusteAtivoDisponivelFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte, selecionado.getPessoa());
    }

    public List<ContaDeDestinacao> completarContasDeDestinacao(String parte) {
        return ajusteAtivoDisponivelFacade.getContaFacade().buscarContasDeDestinacaoPorCodigoOrDescricao(parte, sistemaControlador.getExercicioCorrente());
    }

    public ConverterAutoComplete getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterAutoComplete(ClasseCredor.class, ajusteAtivoDisponivelFacade.getClasseCredorFacade());
        }
        return converterClasseCredor;
    }

    public List<UnidadeOrganizacional> completaUnidadeOrganizacional(String parte) {
        return ajusteAtivoDisponivelFacade.getUnidadeOrganizacionalFacade().listaUnidadesUsuarioCorrenteNivel3(sistemaControlador.getUsuarioCorrente(), parte.trim(), sistemaControlador.getDataOperacao());
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/ajuste-ativo-disponivel/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
