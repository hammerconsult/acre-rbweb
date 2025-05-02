/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ParametroParcelamentoFacade;
import br.com.webpublico.negocios.TipoDoctoOficialFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "parametroParcelamentoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoParametroParcelamento", pattern = "/parametro-de-parcelamento/novo/", viewId = "/faces/tributario/contacorrente/parametrosparcelamento/edita.xhtml"),
        @URLMapping(id = "editarParametroParcelamento", pattern = "/parametro-de-parcelamento/editar/#{parametroParcelamentoControlador.id}/", viewId = "/faces/tributario/contacorrente/parametrosparcelamento/edita.xhtml"),
    @URLMapping(id = "duplicarParametroParcelamento", pattern = "/parametro-de-parcelamento/duplicar/#{parametroParcelamentoControlador.id}/", viewId = "/faces/tributario/contacorrente/parametrosparcelamento/edita.xhtml"),
        @URLMapping(id = "listarParametroParcelamento", pattern = "/parametro-de-parcelamento/listar/", viewId = "/faces/tributario/contacorrente/parametrosparcelamento/lista.xhtml"),
        @URLMapping(id = "verParametroParcelamento", pattern = "/parametro-de-parcelamento/ver/#{parametroParcelamentoControlador.id}/", viewId = "/faces/tributario/contacorrente/parametrosparcelamento/visualizar.xhtml")
})
public class ParametroParcelamentoControlador extends PrettyControlador<ParamParcelamento> implements Serializable, CRUD {

    @EJB
    private ParametroParcelamentoFacade paramParcelamentoFacade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    private ConverterAutoComplete converterDivida, converterTributo, converterAtoLegal, converterExercicio;
    private ParamParcelamentoDivida itemDivida;
    private ParamParcelamentoTributo itemTributo;
    private ParamParcelamentoFaixa itemFaixa;
    private boolean editandoFaixa;
    private boolean editandoDescontos;
    private ConverterAutoComplete converterTipoDocumento;

    private Exercicio exercicioTransferencia;
    private Date inicioVigenciaTransferencia;
    private Date fimVigenciaTransferencia;
    private Exercicio exercicioInicialDividaTransferencia;
    private Exercicio exercicioFimDividaTransferencia;

    public ParametroParcelamentoControlador() {
        super(ParamParcelamento.class);
    }

    public ParamParcelamentoDivida getItemDivida() {
        return itemDivida;
    }

    public void setItemDivida(ParamParcelamentoDivida itemDivida) {
        this.itemDivida = itemDivida;
    }

    public ParamParcelamentoFaixa getItemFaixa() {
        return itemFaixa;
    }

    public void setItemFaixa(ParamParcelamentoFaixa itemFaixa) {
        this.itemFaixa = itemFaixa;
    }

    public ParamParcelamentoTributo getItemTributo() {
        return itemTributo;
    }

    public void setItemTributo(ParamParcelamentoTributo itemTributo) {
        this.itemTributo = itemTributo;
    }

    public boolean getEditandoDescontos() {
        return editandoDescontos;
    }

    public void setEditandoDescontos(boolean editandoDescontos) {
        this.editandoDescontos = editandoDescontos;
    }

    public boolean getEditandoFaixa() {
        return editandoFaixa;
    }

    public void setEditandoFaixa(boolean editandoFaixa) {
        this.editandoFaixa = editandoFaixa;
    }

    public Exercicio getExercicioTransferencia() {
        return exercicioTransferencia;
    }

    public void setExercicioTransferencia(Exercicio exercicioTransferencia) {
        this.exercicioTransferencia = exercicioTransferencia;
    }

    public Date getInicioVigenciaTransferencia() {
        return inicioVigenciaTransferencia;
    }

    public void setInicioVigenciaTransferencia(Date inicioVigenciaTransferencia) {
        this.inicioVigenciaTransferencia = inicioVigenciaTransferencia;
    }

    public Date getFimVigenciaTransferencia() {
        return fimVigenciaTransferencia;
    }

    public void setFimVigenciaTransferencia(Date fimVigenciaTransferencia) {
        this.fimVigenciaTransferencia = fimVigenciaTransferencia;
    }

    public Exercicio getExercicioInicialDividaTransferencia() {
        return exercicioInicialDividaTransferencia;
    }

    public void setExercicioInicialDividaTransferencia(Exercicio exercicioInicialDividaTransferencia) {
        this.exercicioInicialDividaTransferencia = exercicioInicialDividaTransferencia;
    }

    public Exercicio getExercicioFimDividaTransferencia() {
        return exercicioFimDividaTransferencia;
    }

    public void setExercicioFimDividaTransferencia(Exercicio exercicioFimDividaTransferencia) {
        this.exercicioFimDividaTransferencia = exercicioFimDividaTransferencia;
    }

    public Converter getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, paramParcelamentoFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public Converter getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterAutoComplete(Divida.class, paramParcelamentoFacade.getDividaFacade());
        }
        return converterDivida;
    }

    public Converter getConverterTributo() {
        if (converterTributo == null) {
            converterTributo = new ConverterAutoComplete(Tributo.class, paramParcelamentoFacade.getTributoFacade());
        }
        return converterTributo;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, paramParcelamentoFacade.getAtoLegalFacade());
        }
        return converterAtoLegal;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parametro-de-parcelamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return paramParcelamentoFacade;
    }

    @URLAction(mappingId = "novoParametroParcelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        editandoFaixa = false;
        editandoDescontos = false;
        itemDivida = new ParamParcelamentoDivida();
        itemFaixa = new ParamParcelamentoFaixa();
        itemTributo = new ParamParcelamentoTributo();
        selecionado.setCodigo(paramParcelamentoFacade.retornaUltimoCodigoLong());
    }

    @URLAction(mappingId = "editarParametroParcelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        editandoFaixa = false;
        editandoDescontos = false;
        itemDivida = new ParamParcelamentoDivida();
        itemFaixa = new ParamParcelamentoFaixa();
        itemTributo = new ParamParcelamentoTributo();
    }

    @URLAction(mappingId = "verParametroParcelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        editandoFaixa = false;
        editandoDescontos = false;
    }

    @URLAction(mappingId = "duplicarParametroParcelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void duplicar() {
        editar();
        ParamParcelamento clone = new ParamParcelamento();
        clone.setCodigo(paramParcelamentoFacade.retornaUltimoCodigoLong());
        clone.setDescricao("Cópia de " + selecionado.getDescricao());
        clone.setAtoLegal(selecionado.getAtoLegal());
        clone.setTipoCadastroTributario(selecionado.getTipoCadastroTributario());
        clone.setSituacaoDebito(selecionado.getSituacaoDebito());
        clone.setVigenciaInicio(new Date());
        clone.setVigenciaFim(null);
        clone.setQuantidadeReparcelamento(selecionado.getQuantidadeReparcelamento());
        clone.setParcelasInadimplencia(selecionado.getParcelasInadimplencia());
        clone.setTermoCadastro(selecionado.getTermoCadastro());
        clone.setTermoPessoaFisica(selecionado.getTermoPessoaFisica());
        clone.setTermoPessoaJuridica(selecionado.getTermoPessoaJuridica());
        clone.setDividaParcelamento(selecionado.getDividaParcelamento());
        clone.setIncluiValorOriginal(selecionado.getIncluiValorOriginal());
        clone.setIncluiJuros(selecionado.getIncluiJuros());
        clone.setIncluiMulta(selecionado.getIncluiMulta());
        clone.setIncluiCorrecao(selecionado.getIncluiCorrecao());
        clone.setExigePercentualEntrada(selecionado.getExigePercentualEntrada());
        clone.setValorPercentualEntrada(selecionado.getValorPercentualEntrada());
        clone.setValorMinimoParcelaUfm(selecionado.getValorMinimoParcelaUfm());
        clone.setInicioDesconto(selecionado.getInicioDesconto());
        clone.setFinalDesconto(selecionado.getFinalDesconto());
        clone.setTipoLancamentoDesconto(selecionado.getTipoLancamentoDesconto());
        clone.setTipoInadimplimento(selecionado.getTipoInadimplimento());

        for (ParamParcelamentoDivida divida : selecionado.getDividas()) {
            ParamParcelamentoDivida novaDivida = new ParamParcelamentoDivida();
            novaDivida.setParamParcelamento(clone);
            novaDivida.setDivida(divida.getDivida());
            novaDivida.setExercicioInicial(divida.getExercicioInicial());
            novaDivida.setExercicioFinal(divida.getExercicioFinal());
            clone.getDividas().add(novaDivida);
        }

        for (ParamParcelamentoFaixa faixa : selecionado.getFaixas()) {
            ParamParcelamentoFaixa novaFaixa = new ParamParcelamentoFaixa();
            novaFaixa.setParamParcelamento(clone);
            novaFaixa.setValorInicial(faixa.getValorInicial());
            novaFaixa.setValorFinal(faixa.getValorFinal());
            novaFaixa.setQuantidadeMaximaParcelas(faixa.getQuantidadeMaximaParcelas());
            clone.getFaixas().add(novaFaixa);
        }

        for (ParamParcelamentoTributo tributo : selecionado.getTributos()) {
            ParamParcelamentoTributo novoTributo = new ParamParcelamentoTributo();
            novoTributo.setParamParcelamento(clone);
            novoTributo.setNumeroParcelaInicial(tributo.getNumeroParcelaInicial());
            novoTributo.setNumeroParcelaFinal(tributo.getNumeroParcelaFinal());
            novoTributo.setPercDescontoImposto(tributo.getPercDescontoImposto());
            novoTributo.setPercDescontoTaxa(tributo.getPercDescontoTaxa());
            novoTributo.setPercentualCorrecaoMonetaria(tributo.getPercentualCorrecaoMonetaria());
            novoTributo.setPercentualHonorarios(tributo.getPercentualHonorarios());
            novoTributo.setPercentualJuros(tributo.getPercentualJuros());
            novoTributo.setPercentualMulta(tributo.getPercentualMulta());
            novoTributo.setVencimentoFinalParcela(tributo.getVencimentoFinalParcela());
            clone.getTributos().add(novoTributo);
        }

        this.selecionado = clone;
    }

    @Override
    public void salvar() {
        if (validaCampos(selecionado)) {
            super.salvar();
        }

    }

    private boolean validaCampos(ParamParcelamento selecionado) {
        Boolean resultado = true;
        if (selecionado.getCodigo() == null || selecionado.getCodigo() <= 0) {
            resultado = false;
            FacesUtil.addOperacaoNaoPermitida("O código deve ser maior que zero.");
        } else if (paramParcelamentoFacade.isCodigoEmUso(selecionado)) {
            resultado = false;
            FacesUtil.addOperacaoNaoPermitida("O Código informado já está em uso. O sistema gerou um novo código. Por favor, pressione o botão SALVAR novamente.");
        }
        if (selecionado.getDescricao() == null || selecionado.getDescricao().trim().length() <= 0) {
            resultado = false;
            FacesUtil.addOperacaoNaoPermitida("Informe a Descrição.");
        }
        if (selecionado.getAtoLegal() == null || selecionado.getAtoLegal().getId() == null) {
            resultado = false;
            FacesUtil.addOperacaoNaoPermitida("O Campo Ato Legal deve ser preenchido");
        }
        if (selecionado.getSituacaoDebito() == null) {
            resultado = false;
            FacesUtil.addOperacaoNaoPermitida("Informe a Situação do Débito à parcelar.");
        }
        if (selecionado.getDividaParcelamento() == null || selecionado.getDividaParcelamento().getId() == null) {
            resultado = false;
            FacesUtil.addOperacaoNaoPermitida("Informe a Dívida de Destino do parcelamento.");
        }
        if (!selecionado.getIncluiValorOriginal() && !selecionado.getIncluiMulta() && !selecionado.getIncluiJuros() && !selecionado.getIncluiCorrecao()) {
            resultado = false;
            FacesUtil.addOperacaoNaoPermitida("Informe o ao menos um Tipo de Parcelamento.");
        }
        if (selecionado.getVigenciaInicio() == null) {
            resultado = false;
            FacesUtil.addOperacaoNaoPermitida("Informe o Início da Vigência.");
        }
        if ((selecionado.getVigenciaFim() != null) && (selecionado.getVigenciaFim().before(selecionado.getVigenciaInicio()))) {
            resultado = false;
            FacesUtil.addOperacaoNaoPermitida("O Final da Vigência deve ser posterior ao Início da Vigência.");
        }
        if (selecionado.getTipoCadastroTributario() == null) {
            resultado = false;
            FacesUtil.addOperacaoNaoPermitida("Informe o Tipo de Cadastro.");
        }
        if (selecionado.getExigePercentualEntrada()) {
            if (selecionado.getValorPercentualEntrada() == null) {
                resultado = false;
                FacesUtil.addOperacaoNaoPermitida("Informe o Percentual de Entrada.");
            } else if (!paramParcelamentoFacade.percentualValido(selecionado.getValorPercentualEntrada())) {
                resultado = false;
                FacesUtil.addOperacaoNaoPermitida("O Percentual de Entrada deve ser maior que zero e menor que 100%.");
            }
        }
        if (selecionado.getDividas() == null || selecionado.getDividas().isEmpty()) {
            resultado = false;
            FacesUtil.addOperacaoNaoPermitida("Informe pelo menos uma Dívida de Origem para o parcelamento.");
        }
        if (selecionado.getFaixas() == null || selecionado.getFaixas().isEmpty()) {
            resultado = false;
            FacesUtil.addOperacaoNaoPermitida("Informe pelo menos uma Faixa de Valores.");
        }
        if (editandoFaixa) {
            resultado = false;
            FacesUtil.addOperacaoNaoPermitida("Confirme a edição das Faixas de Valores antes de salvar.");
        }
        if (editandoDescontos) {
            resultado = false;
            FacesUtil.addOperacaoNaoPermitida("Confirme a edição das Faixas de Descontos antes de salvar.");
        }

        return resultado;
    }

    public List<Tributo> completaTributo(String parte) {
        return paramParcelamentoFacade.getTributoFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public List<Divida> completaDivida(String parte) {
        return paramParcelamentoFacade.getDividaFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return paramParcelamentoFacade.getAtoLegalFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, " "));
        for (TipoCadastroTributario object : TipoCadastroTributario.values()) {
            retorno.add(new SelectItem(object, object.getDescricao()));
        }
        return retorno;
    }

    public List<Exercicio> completaExericio(String parte) {
        return paramParcelamentoFacade.getExercicioFacade().listaFiltrandoEspecial(parte.trim());
    }

    public List<SelectItem> getSituacoesDebitos() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, " "));
        for (SituacaoDebito object : SituacaoDebito.values()) {
            retorno.add(new SelectItem(object, object.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTiposInadimplentos() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        for (ParamParcelamento.TipoInadimplimento object : ParamParcelamento.TipoInadimplimento.values()) {
            retorno.add(new SelectItem(object, object.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTipoDistribuicaoEmolumentos() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, " "));
        for (TipoDistribuicaoEmolumento object : TipoDistribuicaoEmolumento.values()) {
            retorno.add(new SelectItem(object, object.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTipoTipoValorAdicionais() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, " "));
        for (TipoValorAdicional object : TipoValorAdicional.values()) {
            retorno.add(new SelectItem(object, object.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTipoCobrancaParcelamentos() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        for (TipoCobrancaParcelamento object : TipoCobrancaParcelamento.values()) {
            retorno.add(new SelectItem(object, object.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTipoLancamentosDesconto() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, " "));
        for (ParamParcelamento.TipoLancamentoDesconto object : ParamParcelamento.TipoLancamentoDesconto.values()) {
            retorno.add(new SelectItem(object, object.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTipoVigenciasDesconto() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, " "));
        for (ParamParcelamento.TipoVigenciaDesconto object : ParamParcelamento.TipoVigenciaDesconto.values()) {
            retorno.add(new SelectItem(object, object.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> buscarTiposVerificacaoCancelamentoAutomatico() {
        return Util.getListSelectItem(ParamParcelamento.TipoVerificacaoCancelamentoAutomatico.values(), false);
    }

    public List<SelectItem> buscarTiposSimNao() {
        return Util.getListSelectItem(TipoSimNao.values(), false);
    }

    public void zeraPercentualEntrada() {
        selecionado.setValorPercentualEntrada(BigDecimal.ZERO);
    }

    public void addItemDivida() {
        if (validaDivida(itemDivida)) {
            itemDivida.setParamParcelamento(selecionado);
            selecionado.getDividas().add(itemDivida);
            itemDivida = new ParamParcelamentoDivida();
        }
    }

    public boolean validaDivida(ParamParcelamentoDivida itemDivida) {
        boolean valida = true;
        if (itemDivida.getDivida() == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("Informe uma dívida de origem");
        } else {
            for (ParamParcelamentoDivida param : selecionado.getDividas()) {
                if (!param.equals(itemDivida) && param.getDivida().equals(itemDivida.getDivida())) {
                    FacesUtil.addOperacaoNaoPermitida("A Dívida informada já foi adicionada");
                    break;
                }
            }

        }
        if (itemDivida.getExercicioInicial() == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("Informe o exercício inicial para a dívida de origem");
        }
        if (itemDivida.getExercicioFinal() == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("Informe o exercício final para a dívida de origem");
        }
        if (itemDivida.getExercicioInicial() != null &&
                itemDivida.getExercicioFinal() != null &&
                itemDivida.getExercicioInicial().getAno().longValue() > itemDivida.getExercicioFinal().getAno().longValue()) {
            valida = false;
            FacesUtil.addOperacaoNaoPermitida("O Exercício Final deve ser maior ou igual ao Exercício Inicial.");
        }
        //System.out.println("valida " + valida);
        return valida;
    }

    public void addItemFaixa() {
        if (validaFaixa(itemFaixa)) {
            itemFaixa.setParamParcelamento(selecionado);
            selecionado.getFaixas().add(itemFaixa);
            itemFaixa = new ParamParcelamentoFaixa();
        }
    }

    private boolean validaFaixa(ParamParcelamentoFaixa itemFaixa) {
        boolean valida = true;
        if (itemFaixa.getQuantidadeMaximaParcelas() == null || itemFaixa.getQuantidadeMaximaParcelas() <= 0) {
            valida = false;
            FacesUtil.addOperacaoNaoPermitida("A Quantidade Máxima de Parcelas não pode ser negativa ou igual a 0 (ZERO).");
        }
        if (itemFaixa.getValorInicial() == null || itemFaixa.getValorInicial().compareTo(BigDecimal.ZERO) <= 0) {
            valida = false;
            FacesUtil.addOperacaoNaoPermitida("O Valor Inicial não pode ser negativo ou igual a 0 (ZERO).");

        }

        if (itemFaixa.getValorInicial() == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("Informe o Valor Final.");
        }

        if (itemFaixa.getValorFinal() == null) {
            valida = false;
            FacesUtil.addOperacaoNaoPermitida("Informe o Valor Final.");
        }

        if (itemFaixa.getValorFinal() != null && itemFaixa.getValorInicial() != null && itemFaixa.getValorFinal().compareTo(itemFaixa.getValorInicial()) <= 0) {
            valida = false;
            FacesUtil.addOperacaoNaoPermitida("O Valor Final deve ser maior que o Valor Inicial.");
        }

        return valida;
    }

    public void addItemTributo() {
        if (validaTributo(itemTributo)) {
            itemTributo.setParamParcelamento(selecionado);
            selecionado.getTributos().add(itemTributo);
            itemTributo = new ParamParcelamentoTributo();
        }
    }

    private Boolean validaTributo(ParamParcelamentoTributo itemTributo) {
        Boolean resultado = true;

        if (!paramParcelamentoFacade.percentualValido(itemTributo.getPercDescontoImposto())) {
            resultado = false;
            FacesUtil.addOperacaoNaoPermitida("O percentual de desconto do Imposto deve maior que zero e menor que 100%.");
        }
        if (!paramParcelamentoFacade.percentualValido(itemTributo.getPercDescontoTaxa())) {
            resultado = false;
            FacesUtil.addOperacaoNaoPermitida("O percentual de desconto da Taxa deve maior que zero e menor que 100%.");
        }
        if ((!paramParcelamentoFacade.percentualValido(itemTributo.getPercentualMulta()))) {
            resultado = false;
            FacesUtil.addOperacaoNaoPermitida("O valor da Multa deve ser informado.");
        }
        if ((!paramParcelamentoFacade.percentualValido(itemTributo.getPercentualJuros()))) {
            resultado = false;
            FacesUtil.addOperacaoNaoPermitida("O valor dos Juros deve ser informado.");
        }
        if ((!paramParcelamentoFacade.percentualValido((itemTributo.getPercentualCorrecaoMonetaria())))) {
            resultado = false;
            FacesUtil.addOperacaoNaoPermitida("O valor da Correção Monetária deve ser informado.");
        }
        if ((!paramParcelamentoFacade.percentualValido((itemTributo.getPercentualHonorarios())))) {
            resultado = false;
            FacesUtil.addOperacaoNaoPermitida("O valor dos Honorários.");
        }
        if (selecionado.getTipoLancamentoDesconto() == null) {
            resultado = false;
            FacesUtil.addCampoObrigatorio("Informe o Tipo de Aplicação de Desconto");
        } else if (selecionado.getTipoLancamentoDesconto().equals(ParamParcelamento.TipoLancamentoDesconto.NUMERO_PARCELA)) {
            if ((itemTributo.getNumeroParcelaInicial() == null || itemTributo.getNumeroParcelaFinal() == null)) {
                resultado = false;
                FacesUtil.addCampoObrigatorio("Informe o Número da Parcela Inicial e o Número da Parcela Final");
            } else if (itemTributo.getNumeroParcelaInicial() > itemTributo.getNumeroParcelaFinal()) {
                resultado = false;
                FacesUtil.addOperacaoNaoPermitida("O Número Inicial deve ser menor que o número final");
            }
        } else if (selecionado.getTipoLancamentoDesconto().equals(ParamParcelamento.TipoLancamentoDesconto.VENCIMENTO_FINAL)
                && (itemTributo.getVencimentoFinalParcela() == null)) {
            resultado = false;
            FacesUtil.addCampoObrigatorio("Informe a Data de Vencimento Final");
        }
        return resultado;
    }

    public void removeItemDivida(ActionEvent e) {
        itemDivida = (ParamParcelamentoDivida) e.getComponent().getAttributes().get("objeto");
        selecionado.getDividas().remove(itemDivida);
        itemDivida = new ParamParcelamentoDivida();
    }

    public void removeItemFaixa(ActionEvent e) {
        itemFaixa = (ParamParcelamentoFaixa) e.getComponent().getAttributes().get("objeto");
        selecionado.getFaixas().remove(itemFaixa);
        itemFaixa = new ParamParcelamentoFaixa();
    }

    public void removeItemTributo(ActionEvent e) {
        itemTributo = (ParamParcelamentoTributo) e.getComponent().getAttributes().get("objeto");
        selecionado.getTributos().remove(itemTributo);
        itemTributo = new ParamParcelamentoTributo();
    }

    public void editarDescontos() {
        editandoDescontos = true;
    }

    public void editarFaixas() {
        editandoFaixa = true;
    }

    public void confirmaEdicaoFaixas() {
        for (ParamParcelamentoFaixa faixa : selecionado.getFaixas()) {
            if (validaFaixa(faixa)) {
                continue;
            } else {
                return;
            }
        }
        editandoFaixa = false;
        FacesUtil.addOperacaoRealizada("As faixas foram salvas");

    }

    public void confirmaEdicaoDescontos() {
        for (ParamParcelamentoTributo faixa : selecionado.getTributos()) {
            if (validaTributo(faixa)) {
                continue;
            } else {
                return;
            }
        }
        editandoDescontos = false;
        FacesUtil.addOperacaoRealizada("Os descontos foram salvas");

    }

    public List<TipoDoctoOficial> completaTermoPessoaFisica(String parte) {
        return tipoDoctoOficialFacade.tipoDoctoPorModuloTipoCadastro(parte.trim(), ModuloTipoDoctoOficial.TERMO_PARCELAMENTO, TipoCadastroDoctoOficial.PESSOAFISICA);
    }

    public List<TipoDoctoOficial> completaTermoPessoaJuridica(String parte) {
        return tipoDoctoOficialFacade.tipoDoctoPorModuloTipoCadastro(parte.trim(), ModuloTipoDoctoOficial.TERMO_PARCELAMENTO, TipoCadastroDoctoOficial.PESSOAJURIDICA);
    }

    public List<TipoDoctoOficial> completaTermoCadastro(String parte) {
        if (selecionado.getTipoCadastroTributario() != null) {
            switch (selecionado.getTipoCadastroTributario()) {
                case IMOBILIARIO:
                    return tipoDoctoOficialFacade.tipoDoctoPorModuloTipoCadastro(parte.trim(), ModuloTipoDoctoOficial.TERMO_PARCELAMENTO, TipoCadastroDoctoOficial.CADASTROIMOBILIARIO);
                case RURAL:
                    return tipoDoctoOficialFacade.tipoDoctoPorModuloTipoCadastro(parte.trim(), ModuloTipoDoctoOficial.TERMO_PARCELAMENTO, TipoCadastroDoctoOficial.CADASTRORURAL);
                case ECONOMICO:
                    return tipoDoctoOficialFacade.tipoDoctoPorModuloTipoCadastro(parte.trim(), ModuloTipoDoctoOficial.TERMO_PARCELAMENTO, TipoCadastroDoctoOficial.CADASTROECONOMICO);
            }
        }
        return null;
    }

    public ConverterAutoComplete getConverterTipoDocumento() {
        if (converterTipoDocumento == null) {
            converterTipoDocumento = new ConverterAutoComplete(TipoDoctoOficial.class, tipoDoctoOficialFacade);
        }
        return converterTipoDocumento;
    }

    public void iniciarFiltrosTransferencia() {
        this.exercicioTransferencia = null;
        this.inicioVigenciaTransferencia = null;
        this.fimVigenciaTransferencia = null;
        this.exercicioInicialDividaTransferencia = null;
        this.exercicioFimDividaTransferencia = null;
    }

    private void validarTransferencia() {
        ValidacaoException ve = new ValidacaoException();
        if (this.exercicioTransferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício da transferência!");
        } else if (paramParcelamentoFacade.hasParametroNoExercicio(exercicioTransferencia)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe parâmetro adicionado no exercicio de " + this.exercicioTransferencia.getAno() + "!");
        }
        if (this.inicioVigenciaTransferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data de início de vigência para os novos parâmetros!");
        }
        if (this.fimVigenciaTransferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data de fim de vigência para os novos parâmetros!");
        }
        if (this.exercicioInicialDividaTransferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício incial da dívida de origem!");
        }
        if (this.exercicioFimDividaTransferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício final da dívida de origem!");
        }
        ve.lancarException();
    }

    public void transferirParametros() {
        try {
            validarTransferencia();
            List<ParamParcelamento> parametros = paramParcelamentoFacade.buscarParametrosVigentes(this.exercicioTransferencia);
            for (ParamParcelamento parametro : parametros) {
                ParamParcelamento novoParametro = new ParamParcelamento();
                novoParametro.setCodigo(paramParcelamentoFacade.retornaUltimoCodigoLong());
                novoParametro.setDescricao(parametro.getDescricao().replace(DataUtil.getAno(parametro.getVigenciaInicio()).toString(), this.exercicioTransferencia.getAno().toString()));
                novoParametro.setVigenciaInicio(this.inicioVigenciaTransferencia);
                novoParametro.setVigenciaFim(this.fimVigenciaTransferencia);
                novoParametro.setAtoLegal(parametro.getAtoLegal());
                novoParametro.setSituacaoDebito(parametro.getSituacaoDebito());
                novoParametro.setDividaParcelamento(parametro.getDividaParcelamento());
                novoParametro.setIncluiValorOriginal(parametro.getIncluiValorOriginal());
                novoParametro.setIncluiMulta(parametro.getIncluiMulta());
                novoParametro.setIncluiJuros(parametro.getIncluiJuros());
                novoParametro.setIncluiCorrecao(parametro.getIncluiCorrecao());
                novoParametro.setExigePercentualEntrada(parametro.getExigePercentualEntrada());
                novoParametro.setTipoCadastroTributario(parametro.getTipoCadastroTributario());
                novoParametro.setValorPercentualEntrada(parametro.getValorPercentualEntrada());
                novoParametro.setValorMinimoParcelaUfm(parametro.getValorMinimoParcelaUfm());
                novoParametro.setQuantidadeReparcelamento(parametro.getQuantidadeReparcelamento());
                novoParametro.setParcelasInadimplencia(parametro.getParcelasInadimplencia());
                novoParametro.setInadimplenciaSucessiva(parametro.getExigePercentualEntrada());
                novoParametro.setTipoInadimplimento(parametro.getTipoInadimplimento());
                novoParametro.setTipoLancamentoDesconto(parametro.getTipoLancamentoDesconto());
                novoParametro.setTipoVigenciaDesconto(parametro.getTipoVigenciaDesconto());
                novoParametro.setTermoCadastro(parametro.getTermoCadastro());
                novoParametro.setTermoPessoaFisica(parametro.getTermoPessoaFisica());
                novoParametro.setTermoPessoaJuridica(parametro.getTermoPessoaJuridica());
                novoParametro.setInicioDesconto(parametro.getInicioDesconto());
                novoParametro.setFinalDesconto(parametro.getFinalDesconto());
                novoParametro.setDiasVencidosCancelamento(parametro.getDiasVencidosCancelamento());
                novoParametro.setVerificaCancelamentoAutomatico(parametro.getVerificaCancelamentoAutomatico());
                novoParametro.setDiasVencimentoEntrada(parametro.getDiasVencimentoEntrada());
                novoParametro.setParcelamentoCpfCnpjInvalido(parametro.getParcelamentoCpfCnpjInvalido());

                for (ParamParcelamentoDivida divida : parametro.getDividas()) {
                    ParamParcelamentoDivida novaDivida = new ParamParcelamentoDivida();
                    novaDivida.setExercicioInicial(this.exercicioInicialDividaTransferencia);
                    novaDivida.setExercicioFinal(this.exercicioFimDividaTransferencia);
                    novaDivida.setDivida(divida.getDivida());
                    novaDivida.setParamParcelamento(novoParametro);
                    novoParametro.getDividas().add(novaDivida);
                }
                for (ParamParcelamentoFaixa faixa : parametro.getFaixas()) {
                    ParamParcelamentoFaixa novaFaixa = new ParamParcelamentoFaixa();
                    novaFaixa.setQuantidadeMaximaParcelas(faixa.getQuantidadeMaximaParcelas());
                    novaFaixa.setValorInicial(faixa.getValorInicial());
                    novaFaixa.setValorFinal(faixa.getValorFinal());
                    novaFaixa.setParamParcelamento(novoParametro);
                    novoParametro.getFaixas().add(novaFaixa);
                }
                for (ParamParcelamentoTributo tributo : parametro.getTributos()) {
                    ParamParcelamentoTributo novoTributo = new ParamParcelamentoTributo();
                    novoTributo.setNumeroParcelaInicial(tributo.getNumeroParcelaInicial());
                    novoTributo.setNumeroParcelaFinal(tributo.getNumeroParcelaFinal());
                    novoTributo.setVencimentoFinalParcela(tributo.getVencimentoFinalParcela());
                    novoTributo.setPercDescontoImposto(tributo.getPercDescontoImposto());
                    novoTributo.setPercDescontoTaxa(tributo.getPercDescontoTaxa());
                    novoTributo.setPercentualJuros(tributo.getPercentualJuros());
                    novoTributo.setPercentualMulta(tributo.getPercentualMulta());
                    novoTributo.setPercentualCorrecaoMonetaria(tributo.getPercentualCorrecaoMonetaria());
                    novoTributo.setPercentualHonorarios(tributo.getPercentualHonorarios());
                    novoTributo.setParamParcelamento(novoParametro);
                    novoParametro.getTributos().add(novoTributo);
                }
                paramParcelamentoFacade.salvarNovo(novoParametro);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao criar os parametros: {}", ex);
        }
    }

    @Override
    public List<ParamParcelamento> completarEstaEntidade(String parte) {
        return paramParcelamentoFacade.buscarParametroPorCodigoOrDescricao(parte);
    }
}
