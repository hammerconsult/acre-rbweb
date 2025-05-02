/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.CategoriaConta;
import br.com.webpublico.enums.EsferaOrcamentaria;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-receita-loa-wizard", pattern = "/receita-loa/passo-a-passo/novo/", viewId = "/faces/financeiro/ppa/receitaloawizard/edita.xhtml"),
    @URLMapping(id = "lista-receita-loa-wizard", pattern = "/receita-loa/passo-a-passo/listar/", viewId = "/faces/financeiro/ppa/receitaloawizard/lista.xhtml"),
    @URLMapping(id = "editar-receita-loa-wizard", pattern = "/receita-loa/passo-a-passo/editar/#{receitaLOAWizardControlador.id}/", viewId = "/faces/financeiro/ppa/receitaloawizard/edita.xhtml"),
    @URLMapping(id = "nova-receita-loa-wizard", pattern = "/receita-loa/passo-a-passo/contas/#{receitaLOAWizardControlador.id}/nova-conta/", viewId = "/faces/financeiro/ppa/receitaloawizard/conta.xhtml"),
    @URLMapping(id = "editar-conta-receita-loa-wizard", pattern = "/receita-loa/passo-a-passo/contas/#{receitaLOAWizardControlador.id}/editar-conta/#{receitaLOAWizardControlador.idConta}/", viewId = "/faces/financeiro/ppa/receitaloawizard/conta.xhtml"),
    @URLMapping(id = "ver-receita-loa-wizard", pattern = "/receita-loa/passo-a-passo/ver/#{receitaLOAWizardControlador.id}/", viewId = "/faces/financeiro/ppa/receitaloawizard/visualizar.xhtml"),
    @URLMapping(id = "contas-receita-loa-wizard", pattern = "/receita-loa/passo-a-passo/contas/#{receitaLOAWizardControlador.id}/", viewId = "/faces/financeiro/ppa/receitaloawizard/contas.xhtml"),
    @URLMapping(id = "fontes-receita-loa-wizard", pattern = "/receita-loa/passo-a-passo/contas/#{receitaLOAWizardControlador.id}/fonte/#{receitaLOAWizardControlador.idConta}/", viewId = "/faces/financeiro/ppa/receitaloawizard/fontes.xhtml"),
    @URLMapping(id = "ver-conta-receita-loa-wizard", pattern = "/receita-loa/passo-a-passo/contas/#{receitaLOAWizardControlador.id}/ver-conta/#{receitaLOAWizardControlador.idConta}/", viewId = "/faces/financeiro/ppa/receitaloawizard/conta-visualizar.xhtml")
})
public class ReceitaLOAWizardControlador extends PrettyControlador<LOA> implements Serializable, CRUD {

    @EJB
    private LOAFacade loaFacade;
    @EJB
    private ReceitaLOAFacade receitaLOAFacade;
    @EJB
    private ContaFacade contaDeReceitaFacade;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    private ConverterAutoComplete converterContaDeReceita;
    private ConverterAutoComplete converterContaDeReceitaFonte;
    private ConverterAutoComplete converterUnidadeOrganizacional;
    private Long idConta;
    private ReceitaLOAFonte receitaLOAFonte;
    private ReceitaLOA receitaLOA;
    private MoneyConverter moneyConverter;
    private PercentualConverter percentualConverter;
    private HierarquiaOrganizacional hierarquiaOrgResponsavel;
    private SistemaControlador sistemaControlador;
    private String codigoReceita = "";
    private String filtroReceita;

    public ReceitaLOAWizardControlador() {
        super(LOA.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/receita-loa/passo-a-passo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return loaFacade;
    }

    @URLAction(mappingId = "novo-receita-loa-wizard", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @URLAction(mappingId = "ver-receita-loa-wizard", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-receita-loa-wizard", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
        validaValorFracionadoTabela();
    }

    @URLAction(mappingId = "contas-receita-loa-wizard", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void contasReceitaLOA() {
        recuperarEditarVer();
        recuperarSelecionado();
    }

    @URLAction(mappingId = "fontes-receita-loa-wizard", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void fontesReceitaLOA() {
        recuperarEditarVer();
        recuperarReceita();
        recuperarSelecionado();

    }

    @URLAction(mappingId = "ver-conta-receita-loa-wizard", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verConta() {
        super.ver();
        recuperarSelecionado();
        recuperarReceita();
    }

    @URLAction(mappingId = "editar-conta-receita-loa-wizard", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarConta() {
        recuperarEditarVer();
        recuperarSelecionado();
        recuperarReceita();
        validaValorFracionadoTabela();
        instanciarReceitaLoaFonte();
    }

    @URLAction(mappingId = "nova-receita-loa-wizard", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaConta() {
        recuperarEditarVer();
        receitaLOA.setLoa(selecionado);
        instanciarReceitaLoaFonte();
        recuperarSelecionado();
    }

    private void recuperarSelecionado() {
        selecionado = (LOA) Web.pegaDaSessao(LOA.class);
        if (selecionado == null) {
            selecionado = (LOA) getFacede().recuperar(super.getId());
        }
    }

    public void recuperarReceita() {
        if (receitaLOA != null) {
            receitaLOA = receitaLOAFacade.recuperar(idConta);
            recuperarHierarquiaOrgResponsavelDaEntidadeDaReceitaLOA();
        }
    }

    private void recuperarHierarquiaOrgResponsavelDaEntidadeDaReceitaLOA() {
        hierarquiaOrgResponsavel = receitaLOAFacade.getHierarquiaOrgResponsavelFacade().getHierarquiaOrganizacionalPorUnidade(sistemaControlador.getDataOperacao(), receitaLOA.getEntidade(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
    }

    public void recuperarEditarVer() {
        operacao = Operacoes.EDITAR;
        sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        filtroReceita = null;
        receitaLOA = new ReceitaLOA();
        receitaLOAFonte = new ReceitaLOAFonte();
        receitaLOA.setPrevisaoReceitaOrc(new ArrayList<PrevisaoReceitaOrc>());
        selecionado = loaFacade.recuperar(super.getId());
        hierarquiaOrgResponsavel = new HierarquiaOrganizacional();
        codigoReceita = "";
    }

    public void redirecionarParaContas() {
        Web.poeNaSessao(this.selecionado);
        FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "contas/" + selecionado.getId() + "/");
    }

    public void redirecionarNovaConta() {
        Web.poeNaSessao(this.selecionado);
        FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "contas/" + selecionado.getId() + "/nova-conta/");
    }

    public String redirecionarConta(Object o) {
        if (selecionado.getId() != null) {
            return FacesUtil.getRequestContextPath() + this.getCaminhoPadrao() + "contas/" + selecionado.getId() + "/editar-conta/" + Persistencia.getId(o) + "/";
        }
        Web.poeNaSessao(this.selecionado);
        return FacesUtil.getRequestContextPath() + this.getCaminhoPadrao() + "contas/0/editar-conta/" + Persistencia.getId(o) + "/";
    }

    public String redirecionarVerConta(ReceitaLOA o) {
        if (selecionado.getId() != null) {
            return FacesUtil.getRequestContextPath() + this.getCaminhoPadrao() + "contas/" + selecionado.getId() + "/ver-conta/" + Persistencia.getId(o) + "/";
        }
        Web.poeNaSessao(this.selecionado);
        return FacesUtil.getRequestContextPath() + this.getCaminhoPadrao() + "contas/0/ver-conta/" + Persistencia.getId(o) + "/";
    }

    public String redirecionarReceitaLOA(Object o) {
        if (selecionado == null) {
            LOA lo = loaFacade.recuperar(super.getId());
            selecionado = lo;
        } else {
            return this.getCaminhoPadrao() + "contas/" + selecionado.getId() + "/conta/" + Persistencia.getId(o) + "/";

        }
        Web.poeNaSessao(this.selecionado);
        return this.getCaminhoPadrao() + "contas/0/conta/" + Persistencia.getId(o) + "/";
    }

    public void redicionarInicio() {
        if (selecionado.getId() == null) {
            Web.poeNaSessao(selecionado);
            FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "novo/");
        } else {
            FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
        }
    }

    public void voltarRedirecionarContas() {
        Web.poeNaSessao(selecionado);
        FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "contas/" + selecionado.getId() + "/");
    }

    public ReceitaLOAFonte getReceitaLOAFonte() {
        return receitaLOAFonte;
    }

    public void setReceitaLOAFonte(ReceitaLOAFonte receitaLOAFonte) {
        this.receitaLOAFonte = receitaLOAFonte;
    }

    public ReceitaLOA getReceitaLOA() {
        return receitaLOA;
    }

    public void setReceitaLOA(ReceitaLOA receitaLOA) {
        this.receitaLOA = receitaLOA;
    }

    public List<SelectItem> getContaDeReceita() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Conta object : contaDeReceitaFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaEsferaOrcamentaria() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (EsferaOrcamentaria eo : EsferaOrcamentaria.values()) {
            if (!eo.name().equals("ORCAMENTOGERAL")) {
                toReturn.add(new SelectItem(eo, eo.getDescricao()));
            }
        }
        return toReturn;
    }

    public Boolean mostraArredondamento() {
        Boolean controle = true;
        for (ReceitaLOAFonte rlf : receitaLOA.getReceitaLoaFontes()) {
            if (rlf.getItem() != null) {
                if (rlf.getItem().equals(receitaLOAFonte.getItem())) {
                    if (rlf.getRounding()) {
                        controle = false;
                    }
                }
            } else {
                if (rlf.getRounding()) {
                    controle = false;
                }
            }
        }
        return controle;
    }

    public Converter getConverterContaDeReceita() {
        if (converterContaDeReceita == null) {
            converterContaDeReceita = new ConverterAutoComplete(Conta.class, contaDeReceitaFacade);
        }
        return converterContaDeReceita;
    }

    public Converter getConverterContaDeReceitaFonte() {
        if (converterContaDeReceitaFonte == null) {
            converterContaDeReceitaFonte = new ConverterAutoComplete(Conta.class, contaDeReceitaFacade);
        }
        return converterContaDeReceitaFonte;
    }

    public ConverterAutoComplete getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, receitaLOAFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterUnidadeOrganizacional;
    }

    public List<HierarquiaOrganizacional> completaUnidade(String parte) {
        return receitaLOAFacade.getHierarquiaOrgResponsavelFacade().filtraPorNivel(parte.toLowerCase(), "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaControlador.getDataOperacao());
    }

    private void validarValorFracionado() {
        BigDecimal valorReceitaLOAArredondado = receitaLOA.getValor();
        valorReceitaLOAArredondado = valorReceitaLOAArredondado.setScale(0, BigDecimal.ROUND_UP);
        if (receitaLOA.getValor().subtract(valorReceitaLOAArredondado).compareTo(BigDecimal.ZERO) != 0) {
            FacesUtil.addAtencao(" Foi adicionado um valor fracionado para esta Receita Loa.");
        }
    }

    public void validaValorFracionadoTabela() {
        LOA l = ((LOA) selecionado);
        BigDecimal totalValoresTabela = BigDecimal.ZERO;
        BigDecimal totalValoresTabelaArredondado = BigDecimal.ZERO;
        for (ReceitaLOA rl : l.getReceitaLOAs()) {
            totalValoresTabela = totalValoresTabela.add(rl.getValor());
        }
        totalValoresTabelaArredondado = totalValoresTabela;
        totalValoresTabelaArredondado = totalValoresTabelaArredondado.setScale(0, BigDecimal.ROUND_UP);
        if (totalValoresTabela.subtract(totalValoresTabelaArredondado).compareTo(BigDecimal.ZERO) != 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção! ", " Existe valores fracionados na tabela."));
        }
    }

    public Boolean getVerificaProvisaoDeLoaEfetivada() {
        if (!loaFacade.validaEfetivacaoLoa(sistemaControlador.getExercicioCorrente()).isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public String getVerificaMascaraReceitaLOA() {
        ConfiguracaoPlanejamentoPublico cpp = receitaLOAFacade.getConfiguracaoPlanejamentoPublicoFacade().retornaUltima();
        if (cpp.getMascaraCodigoReceitaLOA() == null) {
            return null;
        }
        return cpp.getMascaraCodigoReceitaLOA().replaceAll("#", "9");
    }

    public void calculaPorPercentual(PrevisaoReceitaOrc prev) {
        PrevisaoReceitaOrc p = prev;
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        valor = (p.getReceitaLOA().getValor().multiply(prev.getPercentual())).divide(new BigDecimal(100));
        p.setValorProgramado(valor);
        RequestContext context = RequestContext.getCurrentInstance();
    }

    public void calculaPorProgramado(PrevisaoReceitaOrc prev) {
        PrevisaoReceitaOrc p = prev;
        try {
            BigDecimal valor = new BigDecimal(BigInteger.ZERO);
            BigDecimal prog = p.getValorProgramado();
            BigDecimal saldo = p.getReceitaLOA().getValor();
            valor = (prog.multiply(new BigDecimal(100))).divide(saldo, 4, RoundingMode.HALF_UP);
            p.setPercentual(valor);
        } catch (Exception ex) {
            p.setValorProgramado(BigDecimal.ZERO);
        }
    }

    public BigDecimal somaTotaisPercentuais(ReceitaLOA rl) {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        if (rl != null) {
            for (PrevisaoReceitaOrc p : rl.getPrevisaoReceitaOrc()) {
                soma = soma.add(p.getPercentual());
            }
        }
        return soma;
    }

    public BigDecimal somaTotaisProgramados(ReceitaLOA rl) {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        if (rl != null) {
            for (PrevisaoReceitaOrc p : rl.getPrevisaoReceitaOrc()) {
                soma = soma.add(p.getValorProgramado());
            }
        }
        return soma;
    }

    public BigDecimal somaTotaisUtilizados(ReceitaLOA rl) {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        if (rl != null) {
            for (PrevisaoReceitaOrc p : rl.getPrevisaoReceitaOrc()) {
                soma = soma.add(p.getValorUtilizado());
            }
        }
        return soma;
    }

    public BigDecimal somaSaldosTotais(ReceitaLOA rl) {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        if (rl != null) {
            for (PrevisaoReceitaOrc p : rl.getPrevisaoReceitaOrc()) {
                soma = soma.add(p.getValorProgramado().subtract(p.getValorUtilizado()));
            }
        }
        return soma;
    }

    public BigDecimal getTotalFontesRecursos() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (ReceitaLOAFonte loaFonte : receitaLOA.getReceitaLoaFontes()) {
            total = total.add(loaFonte.getValor());
        }
        return total;
    }


    public void calculaSaldoCumulativo(ReceitaLOA rl) {
        BigDecimal saldoAnteriorCota = new BigDecimal(BigInteger.ZERO);
        for (PrevisaoReceitaOrc p : rl.getPrevisaoReceitaOrc()) {
            p.setSaldo((p.getValorProgramado().subtract(p.getValorUtilizado())).add(saldoAnteriorCota));
            saldoAnteriorCota = p.getSaldo();
        }
    }

    public BigDecimal calculaDiferenca(ReceitaLOA rl) {
        BigDecimal dif = new BigDecimal(BigInteger.ZERO);
        if (rl != null) {
            dif = rl.getValor().subtract(somaTotaisProgramados(rl));

        }
        return dif;
    }

    public void atualizaTabela(ReceitaLOA receita) {
        RequestContext context = RequestContext.getCurrentInstance();
        calculaSaldoCumulativo(receita);
//        context.update("@form");
    }

    public BigDecimal getPercentualPorConjuntoDeFontes(ReceitaLOAFonte receitaLOAFonte) {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (ReceitaLOAFonte rlf : receitaLOA.getReceitaLoaFontes()) {
            if (rlf.getItem().equals(receitaLOAFonte.getItem())) {
                soma = soma.add(rlf.getPercentual());
            }
        }
        return soma;
    }

    public Long setarConjuntoFontes(ReceitaLOAFonte receitaLOAFonte) {

        Long item = null;
        for (ReceitaLOAFonte rlf : receitaLOA.getReceitaLoaFontes()) {
            if (rlf.getPercentual().equals(receitaLOAFonte.getPercentual())) {
                item = rlf.getItem();
            }
        }
        return item;
    }


    public void setarUnidadeReceitaLOA() {
        if (hierarquiaOrgResponsavel != null) {
            receitaLOA.setEntidade(hierarquiaOrgResponsavel.getSubordinada());
        }
    }

    public void salvarConta() {
        try {
            validarReceitaLOA();
            validarRegrasEspecificasReceitaLoa();
            atribuirValoresParaReceitaLoa();
            validarValorFracionado();
            liberarReceitaLOA();
            receitaLOAFacade.salvarReceitaLoa(receitaLOA);
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
            redirecionarParaContas();
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void atribuirValoresParaReceitaLoa() throws ParseException {
        receitaLOA.setLoa(selecionado);
        receitaLOA.setSaldo(receitaLOA.getValor());
        Date data = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/" + sistemaControlador.getExercicioCorrenteAsInteger());
        receitaLOA.setDataRegistro(data);
        selecionado.getReceitaLOAs().add(receitaLOA);
    }

    private void validarReceitaLOA() {
        ValidacaoException ve = new ValidacaoException();
        if (receitaLOA.getOperacaoReceita() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Operação da Receita deve ser informado.");
        }
        if (receitaLOA.getContaDeReceita() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Conta de Receita deve ser informado.");
        }
        if (hierarquiaOrgResponsavel == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Unidade Organizacional deve ser informado.");
        }
        if (receitaLOA.getValor().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Valor não pode ser negavito.");
        }
        ve.lancarException();
    }

    private void validarRegrasEspecificasReceitaLoa() {
        ValidacaoException ve = new ValidacaoException();
        if (receitaLOA.getReceitaLoaFontes().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A receita loa não possui fontes de recursos em seu cadastro. Por favor adicionar fonte(s) para continuar.");
        }
        if (receitaLOA.getValor().compareTo(getTotalFontesRecursos()) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor tota das fontes de recursos de " + Util.formataValor(getTotalFontesRecursos()) + ", deve ter o mesmo valor de " + Util.formataValor(receitaLOA.getValor()) + " previsto na receita loa.");
        }
        if (receitaLOAFacade.verificaCodigoReceita(receitaLOA)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" A conta: " + receitaLOA.getContaDeReceita() + " e operação: " + receitaLOA.getOperacaoReceita().getDescricao() + ". Já foi utilizada para a unidade: " + hierarquiaOrgResponsavel);
        }
        for (ReceitaLOAFonte loaFonte : receitaLOA.getReceitaLoaFontes()) {
            BigDecimal percentualPorConjuntoFonte = getPercentualPorConjuntoDeFontes(loaFonte);
            if (percentualPorConjuntoFonte.compareTo(new BigDecimal(100)) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O percentual para o conjunto de fontes <b> " + loaFonte.getItem() + "</b> não atingiu o valor de 100%. Por favor, corrigir a porcentagem para continuar.");
            }
        }
        ve.lancarException();
    }

    public void addicionaReceitaFonteAssocia(ActionEvent e) {
        receitaLOA = (ReceitaLOA) e.getComponent().getAttributes().get("objetos");
        receitaLOAFonte.setReceitaLOA(receitaLOA);
        if (((LOA) selecionado).getEfetivada()) {
            receitaLOAFonte.setValor(new BigDecimal(BigInteger.ZERO));
        }
        receitaLOAFacade.geraPrevisoesReceitaOrc(receitaLOA);
    }

    public boolean validaLoaEfetivada(ReceitaLOA r) {
        return !(selecionado.getEfetivada()
            && r.getValor().compareTo(BigDecimal.ZERO) != 0);
    }

    public void setaFonteDespesa(SelectEvent evt) {
        ContaDeDestinacao cd = (ContaDeDestinacao) evt.getObject();
        receitaLOAFonte.setDestinacaoDeRecursos(cd);
    }

    private void validarReceitaLOAFonte() {

        if (receitaLOA.getContaDeReceita() == null) {
            throw new ExcecaoNegocioGenerica("O campo Conta de Receita é obrigatório para adicionar Fonte de Recurso a Receita Loa.");
        }
        if (hierarquiaOrgResponsavel == null) {
            throw new ExcecaoNegocioGenerica("O campo Unidade Organizacional é obrigatório para adicionar Fonte de Recurso a Receita Loa.");
        }
        if (receitaLOAFonte.getDestinacaoDeRecursos() == null) {
            throw new ExcecaoNegocioGenerica("O campo Conta de Destinação de Recurso é obrigatório.");
        }
        if ((receitaLOAFonte.getPercentual().compareTo(BigDecimal.ZERO) < 0)) {
            throw new ExcecaoNegocioGenerica("O campo Percentual deve ser maior que zero.");
        }
        if ((receitaLOAFonte.getPercentual().compareTo(BigDecimal.valueOf(100L)) > 0)) {
            throw new ExcecaoNegocioGenerica("O campo Percentual deve ser menor que 100%.");
        }
        if (receitaLOA.getLoa().getAprovacao() == null) {
            if (receitaLOA.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ExcecaoNegocioGenerica("O campo Valor deve ser maior que 0 (ZERO).");
            }
        }
        if (receitaLOAFonte.getItem() == null) {
            throw new ExcecaoNegocioGenerica("O campo Conjunto de Fontes deve ser informado.");
        }
        for (ReceitaLOAFonte loaFonte : receitaLOA.getReceitaLoaFontes()) {
            BigDecimal soma = verificaPorcentagensFontesAoAdicionarFonte().add(receitaLOAFonte.getPercentual());

            if (!(soma.compareTo(new BigDecimal(100)) <= 0)) {
                throw new ExcecaoNegocioGenerica("A porcentagem informada para o conjunto <b>" + receitaLOAFonte.getItem() + "</b> ultrapassa o limite de 100%.");
            }
            if (loaFonte.getItem() != null) {
                if (loaFonte.getDestinacaoDeRecursos().equals(receitaLOAFonte.getDestinacaoDeRecursos())
                    && !receitaLOAFonte.equals(loaFonte)
                    && loaFonte.getItem().equals(receitaLOAFonte.getItem())) {
                    throw new ExcecaoNegocioGenerica("A Conta de Destinação de Recurso " + loaFonte.getDestinacaoDeRecursos() + " já foi adicionada na lista para o conjunto <b> " + receitaLOAFonte.getItem() + "</b>.");
                }
            }
        }
    }

    public void alterarReceitasLoaFonte(ReceitaLOAFonte rlf) {
        receitaLOAFonte = rlf;
        receitaLOA.getReceitaLoaFontes().remove(rlf);
    }

    public boolean podeRemoverReceitaLoaFonte(ReceitaLOAFonte rlf) {
        if (rlf.getId() != null) {
            if (selecionado.getEfetivada()
                && rlf.getValor().compareTo(getTotalFontesRecursos()) == 0
                && receitaLOA.getConjuntos().contains(rlf.getItem())) {
                return false;
            }
        } else {
            return true;
        }
        return true;
    }

    public void instanciarReceitaLoaFonte() {
        receitaLOAFonte = new ReceitaLOAFonte();
        receitaLOAFonte.setReceitaLOA(receitaLOA);
        receitaLOAFonte.setDestinacaoDeRecursos(null);
        receitaLOAFonte.setPercentual(new BigDecimal("100"));
        receitaLOAFonte.setDateRegistro(sistemaControlador.getDataOperacao());
    }


    public void adicionarReceitaLOAFonte() {
        try {
            validarReceitaLOAFonte();
            receitaLOA.setReceitaLoaFontes(Util.adicionarObjetoEmLista(receitaLOA.getReceitaLoaFontes(), receitaLOAFonte));
            instanciarReceitaLoaFonte();
            ordenarReceitasLoasFontes();
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public BigDecimal somaTotaisPercentuais() {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (ReceitaLOAFonte rlf : receitaLOA.getReceitaLoaFontes()) {
            soma = soma.add(rlf.getPercentual());
        }
        return soma;
    }

    public void liberarReceitaLOA() throws ExcecaoNegocioGenerica {
        try {
            if (!receitaLOA.getReceitaLoaFontes().isEmpty()) {
                Boolean controle = true;
                for (ReceitaLOAFonte rlf : receitaLOA.getReceitaLoaFontes()) {
                    if (rlf.getRounding()) {
                        controle = false;
                    }
                }
                if (controle) {
                    receitaLOA.getReceitaLoaFontes().get(0).setRounding(true);
                }
            }
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao realizar a liberação da receita. A " + e.getMessage());
        }
    }

    public BigDecimal verificaPorcentagensFontes() {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (ReceitaLOAFonte rlf : receitaLOA.getReceitaLoaFontes()) {
            soma = soma.add(rlf.getPercentual());
        }
        return soma;
    }

    public BigDecimal verificaPorcentagensFontesAoAdicionarFonte() {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (ReceitaLOAFonte rlf : receitaLOA.getReceitaLoaFontes()) {
            if (rlf.getItem() != null) {
                if (rlf.getItem().equals(receitaLOAFonte.getItem())) {
                    soma = soma.add(rlf.getPercentual());
                }
            }
        }
        return soma;
    }

    public void recalculaValoresReceita() {
        if (validaAlteraValorReceitaLOA()) {
            calculaPrevisoes();
            calculaFontes();
            receitaLOA.setSaldo(receitaLOA.getValor());
            RequestContext.getCurrentInstance().execute("valorReceita.hide()");
        }
    }

    public void calculaPrevisoes() {
        for (PrevisaoReceitaOrc pro : receitaLOA.getPrevisaoReceitaOrc()) {
            pro.setValorProgramado((receitaLOA.getValor().multiply(pro.getPercentual()).divide(new BigDecimal(100))));
        }
    }

    public void calculaFontes() {
        for (ReceitaLOAFonte rlf : receitaLOA.getReceitaLoaFontes()) {
            rlf.setValor((receitaLOA.getValor().multiply(rlf.getPercentual()).divide(new BigDecimal(100))));
        }
    }

    public void alteraValorReceita(ActionEvent ae) {
        receitaLOA = (ReceitaLOA) ae.getComponent().getAttributes().get("alteraValor");
    }

    public Boolean validaAlteraValorReceitaLOA() {
        boolean retorno = true;
        if (receitaLOA.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            retorno = false;
            FacesUtil.addOperacaoNaoPermitida("O valor não deve ser negativo.");
            receitaLOA.setValor(BigDecimal.ZERO);
        }
        return retorno;
    }

    public void selecionaRouding(ReceitaLOAFonte rlf) {
        rlf.setRounding(Boolean.TRUE);
    }

    public String corPercentual() {
        if (somaTotaisPercentuais().compareTo(new BigDecimal("100")) == 0) {
            return "green";
        }
        return "red";
    }

    public BigDecimal getTotalReceitas() {
        BigDecimal somaTotal = new BigDecimal(BigInteger.ZERO);
        for (ReceitaLOA r : ((LOA) selecionado).getReceitaLOAs()) {
            if (!r.getContaDeReceita().getCodigo().startsWith("9")) {
                somaTotal = somaTotal.add(r.getValor());
            } else {
                somaTotal = somaTotal.subtract(r.getValor());
            }
        }
        return somaTotal;
    }

    public BigDecimal getRestoReceitas() {
        BigDecimal restaTotal = BigDecimal.ZERO;
        if (getTotalReceitas() != null && selecionado.getValorDaReceita() != null) {
            restaTotal = getTotalReceitas();
            restaTotal = restaTotal.subtract(selecionado.getValorDaReceita());
        }
        return restaTotal;
    }

    public void removerReceitasLoa(ReceitaLOA loa) {
        try {
            loa = receitaLOAFacade.recuperar(loa.getId());
            receitaLOAFacade.remover(loa);
            receitaLOA.getReceitaLoaFontes().clear();
            FacesUtil.addOperacaoRealizada(" Receita LOA excluída com sucesso.");
        } catch (Exception e) {
            FacesUtil.addError("Erro ao Excluir: ", e.getMessage());
        }
    }

    public void removerReceitasLoaFonte(ActionEvent evento) {
        ReceitaLOAFonte r = (ReceitaLOAFonte) evento.getComponent().getAttributes().get("objetos");
        receitaLOA.getReceitaLoaFontes().remove(r);

//        ((LOA) selecionado).getReceitaLOAs().get(((LOA) selecionado).getReceitaLOAs().indexOf(receitaLOA)).getReceitaLoaFontes().remove(r);
    }

    public List<Conta> completaFonteParaReceitas(String parte) {
        if (selecionado.getLdo() != null) {
            return contaDeReceitaFacade.listaFiltrandoDestinacaoDeRecursos(parte.trim(), selecionado.getLdo().getExercicio());
        }
        return new ArrayList<>();
    }

    public List<Conta> completaContaParaReceitas(String parte) {
        return contaDeReceitaFacade.listaFiltrandoReceita(parte.trim(), selecionado.getLdo().getExercicio());
    }

    public void validaCategoriaConta(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Conta c = (Conta) value;
        c = contaDeReceitaFacade.recuperar(c);
        if (c.getCategoria() != null) {
            if (c.getCategoria().equals(CategoriaConta.SINTETICA)) {
                message.setDetail(" Conta Sintética. Não pode ser utilizada.");
                message.setSummary("Operação não Permitida!");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public PercentualConverter getPercentualConverter() {
        if (percentualConverter == null) {
            percentualConverter = new PercentualConverter();
        }
        return percentualConverter;
    }

    public void salvar() {
        LOA loa = ((LOA) selecionado);
        try {
            //if (verificaLancamentosPrevisoesReceitaOrc(loa.getReceitaLOAs())) {
            if (operacao == Operacoes.NOVO) {
                this.getFacade().salvarNovo(loa);
            } else {
                this.getFacade().salvar(loa);
            }
            redireciona();
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
//            } else {
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", fraseErro));
////                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Verifique os lançamentos de Previsões de Receita Orçamentária"));
//                return "edita.xhtml";
//            }
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    //NÃO ESTÁ SENDO USADO
//    public Boolean verificaLancamentosPrevisoesReceitaOrc(List<ReceitaLOA> receitas) {
//        Boolean b = false;
//        fraseErro = "";
//        if (!receitas.isEmpty()) {
//            for (ReceitaLOA rl : receitas) {
//                if (rl.getReceitaLoaFontes().isEmpty()) {
//                    fraseErro = "A Receita LOA " + rl.getCodigoReduzido() + " não tem fonte vinculada!";
//                    break;
//                } else {
//                    Boolean z = (calculaDiferenca(rl).compareTo(BigDecimal.ZERO) == 0);
//                    if (calculaDiferenca(rl).compareTo(BigDecimal.ZERO) == 0) {
//                        b = true;
//                    }
////                    } else {
////                        fraseErro = "Verifique os lançamentos da Receita LOA " + rl.getCodigoReduzido();
////                        b = false;
////                        break;
////                    }
//                }
//            }
//        } else {
//            fraseErro = "Nenhuma previsão encontrada!";
//        }
//        return b;
//    }

    public void rouding(ActionEvent ae) {
        ReceitaLOAFonte receita = (ReceitaLOAFonte) ae.getComponent().getAttributes().get("objeto");
        for (ReceitaLOAFonte rlf : receitaLOA.getReceitaLoaFontes()) {
            if (rlf.equals(receita)) {
                rlf.setRounding(Boolean.TRUE);
            } else {
                rlf.setRounding(Boolean.FALSE);
            }
        }
    }

    public List<ReceitaLOA> getListaDaTabela() {
        List<ReceitaLOA> retorno = new ArrayList<>();
        for (ReceitaLOA daVez : ((LOA) selecionado).getReceitaLOAs()) {
            if (filtroReceita == null
                || filtroReceita.trim().isEmpty()
                || daVez.getContaDeReceita().getCodigo().contains(filtroReceita.trim())
                || daVez.getContaDeReceita().getDescricao().contains(filtroReceita.trim())
                || daVez.getEntidade().getDescricao().contains(filtroReceita.trim())) {
                retorno.add(daVez);
            }
        }

        return retorno;
    }

    public LOAFacade getFacade() {
        return loaFacade;
    }

    public LOAFacade getLoaFacade() {
        return loaFacade;
    }

    public void setLoaFacade(LOAFacade loaFacade) {
        this.loaFacade = loaFacade;
    }

    public HierarquiaOrganizacional getHierarquiaOrgResponsavel() {
        return hierarquiaOrgResponsavel;
    }

    public void setHierarquiaOrgResponsavel(HierarquiaOrganizacional hierarquiaOrgResponsavel) {
        this.hierarquiaOrgResponsavel = hierarquiaOrgResponsavel;
    }

    public String getCodigoReceita() {
        return codigoReceita;
    }

    public void setCodigoReceita(String codigoReceita) {
        this.codigoReceita = codigoReceita;
    }

    public String getFiltroReceita() {
        return filtroReceita;
    }

    public void setFiltroReceita(String filtroReceita) {
        this.filtroReceita = filtroReceita;
    }

    public Long getIdConta() {
        return idConta;
    }

    public void setIdConta(Long idConta) {
        this.idConta = idConta;
    }

    public Boolean getHabilitarCampos() {
        return sistemaControlador.getExercicioCorrente().equals(selecionado.getLdo().getExercicio());
    }

    public void atualizarOperacaoEDestinacaoComContaReceita() {
        if (receitaLOA != null && receitaLOA.getId() == null && receitaLOA.getContaDeReceita() != null) {
            receitaLOA.getReceitaLoaFontes().clear();
            List<ReceitaLOAFonte> fontesExAnterior = receitaLOAFacade.buscarReceitasLoasFontesPorExercicioEContaDeReceita(selecionado.getLdo().getExercicio().getAno() - 1, receitaLOA.getContaDeReceita());

            if (!fontesExAnterior.isEmpty()) {
                ReceitaLOA recLoaExAnterior = fontesExAnterior.get(0).getReceitaLOA();
                receitaLOA.setOperacaoReceita(recLoaExAnterior.getOperacaoReceita());
                receitaLOA.setEntidade(recLoaExAnterior.getEntidade());
                recuperarHierarquiaOrgResponsavelDaEntidadeDaReceitaLOA();

                HashSet<String> fontesAdicionadas = new HashSet<>();
                fontesExAnterior.forEach(fonte -> {
                    String chave = fonte.getDestinacaoDeRecursos().getCodigo() + fonte.getItem();
                    if (!fontesAdicionadas.contains(chave)) {
                        ReceitaLOAFonte novaFonte = new ReceitaLOAFonte();
                        novaFonte.setDestinacaoDeRecursos(fonte.getDestinacaoDeRecursos());
                        novaFonte.setPercentual(fonte.getPercentual());
                        novaFonte.setEsferaOrcamentaria(fonte.getEsferaOrcamentaria());
                        novaFonte.setItem(fonte.getItem());
                        novaFonte.setRounding(fonte.getRounding());
                        receitaLOA.getReceitaLoaFontes().add(novaFonte);
                        fontesAdicionadas.add(chave);
                    }
                });
            }
            ordenarReceitasLoasFontes();
        }
    }

    private void ordenarReceitasLoasFontes() {
        if (receitaLOA != null && !receitaLOA.getReceitaLoaFontes().isEmpty()) {
            receitaLOA.getReceitaLoaFontes().sort((o1, o2) -> {
                String chave1 = o1.getItem() + o1.getDestinacaoDeRecursos().getCodigo().trim().replace(".", "");
                String chave2 = o2.getItem() + o2.getDestinacaoDeRecursos().getCodigo().trim().replace(".", "");
                return Integer.valueOf(chave1).compareTo(Integer.valueOf(chave2));
            });
        }
    }
}
