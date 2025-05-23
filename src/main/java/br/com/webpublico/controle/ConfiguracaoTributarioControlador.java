/*
 * Codigo gerado automaticamente em Tue Nov 08 14:43:58 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteParametroAlvara;
import br.com.webpublico.entidadesauxiliares.bi.TipoExportacaoArquivoBI;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.tributario.TipoWebService;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoTributarioFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.TabChangeEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean(name = "configuracaoTributarioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaConfiguracaoTributario", pattern = "/configuracao-tributario/novo/", viewId = "/faces/tributario/configuracaotributario/edita.xhtml"),
    @URLMapping(id = "editarConfiguracaoTributario", pattern = "/configuracao-tributario/editar/#{configuracaoTributarioControlador.id}/", viewId = "/faces/tributario/configuracaotributario/edita.xhtml"),
    @URLMapping(id = "listarConfiguracaoTributario", pattern = "/configuracao-tributario/listar/", viewId = "/faces/tributario/configuracaotributario/lista.xhtml")
})
public class ConfiguracaoTributarioControlador extends PrettyControlador<ConfiguracaoTributario> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    private ConverterGenerico converterCidade, converterEvento, converterConta, converterEstado, converterDivida, converterTributo, converterLotacaoVistoriadora, converterTipoDoctoOficial, converterValorPossivel;
    private ConverterAutoComplete converterPessoa, multaConverter, converterIrregularidade;
    private UF estado;
    private BancoContaConfTributario conta;
    private List<SubConta> subContas;
    private EventoConfiguradoBCI eventoBCI;
    private ConfiguracaoIrregularidadesDoAlvara configuracaoIrregularidadesDoAlvara;
    private List<ValorPossivel> valoresPossiveisAtributo;
    private ConfiguracaoWebService configuracaoWebService;
    private AssistenteParametroAlvara assistente;
    private int activeIndex;
    private ConfiguracaoTributarioBIDivida configuracaoTributarioBIDivida;
    private List<ConfiguracaoTributarioBIDivida> dividasPorTipoExportacao;
    private LotacaoVistoriadora lotacaoVistoriadoraRendasSelecionada;

    public ConfiguracaoTributarioControlador() {
        super(ConfiguracaoTributario.class);
    }

    public EventoConfiguradoBCI getEventoBCI() {
        return eventoBCI;
    }

    public void setEventoBCI(EventoConfiguradoBCI eventoBCI) {
        this.eventoBCI = eventoBCI;
    }

    public ConfiguracaoIrregularidadesDoAlvara getConfiguracaoIrregularidadesDoAlvara() {
        return configuracaoIrregularidadesDoAlvara;
    }

    public void setConfiguracaoIrregularidadesDoAlvara(ConfiguracaoIrregularidadesDoAlvara configuracaoIrregularidadesDoAlvara) {
        this.configuracaoIrregularidadesDoAlvara = configuracaoIrregularidadesDoAlvara;
    }

    public ConfiguracaoWebService getConfiguracaoWebService() {
        return configuracaoWebService;
    }

    public void setConfiguracaoWebService(ConfiguracaoWebService configuracaoWebService) {
        this.configuracaoWebService = configuracaoWebService;
    }

    public LotacaoVistoriadora getLotacaoVistoriadoraRendasSelecionada() {
        return lotacaoVistoriadoraRendasSelecionada;
    }

    public void setLotacaoVistoriadoraRendasSelecionada(LotacaoVistoriadora lotacaoVistoriadoraRendasSelecionada) {
        this.lotacaoVistoriadoraRendasSelecionada = lotacaoVistoriadoraRendasSelecionada;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public ConfiguracaoTributarioBIDivida getConfiguracaoTributarioBIDivida() {
        return configuracaoTributarioBIDivida;
    }

    public void setConfiguracaoTributarioBIDivida(ConfiguracaoTributarioBIDivida configuracaoTributarioBIDivida) {
        this.configuracaoTributarioBIDivida = configuracaoTributarioBIDivida;
    }

    public List<ConfiguracaoTributarioBIDivida> getDividasPorTipoExportacao() {
        return dividasPorTipoExportacao;
    }

    public void setDividasPorTipoExportacao(List<ConfiguracaoTributarioBIDivida> dividasPorTipoExportacao) {
        this.dividasPorTipoExportacao = dividasPorTipoExportacao;
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoTributarioFacade;
    }

    public List<ConfiguracaoTributario> getLista() {
        return configuracaoTributarioFacade.lista();
    }

    public ConverterGenerico getConverterTributo() {
        if (converterTributo == null) {
            converterTributo = new ConverterGenerico(Tributo.class, configuracaoTributarioFacade.getTributoFacade());
        }
        return converterTributo;
    }

    public ConverterGenerico getConverterEvento() {
        if (converterEvento == null) {
            converterEvento = new ConverterGenerico(EventoCalculo.class, configuracaoTributarioFacade.getEventoCalculoFacade());
        }
        return converterEvento;
    }

    public ConverterGenerico getConverterConta() {
        if (converterConta == null) {
            converterConta = new ConverterGenerico(SubConta.class, configuracaoTributarioFacade.getSubContaFacade());
        }
        return converterConta;
    }

    public Converter getConverterMulta() {
        if (multaConverter == null) {
            multaConverter = new ConverterAutoComplete(MultaFiscalizacao.class, configuracaoTributarioFacade.getMultaFiscalizacaoFacade());
        }
        return multaConverter;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, configuracaoTributarioFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public ConverterGenerico getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterGenerico(Divida.class, configuracaoTributarioFacade.getDividaFacade());
        }
        return converterDivida;
    }

    public ConverterGenerico getConverterEstado() {
        if (converterEstado == null) {
            converterEstado = new ConverterGenerico(UF.class, configuracaoTributarioFacade.getEstadoFacade());
        }
        return converterEstado;
    }

    public UF getEstado() {
        return estado;
    }

    public void setEstado(UF estado) {
        this.estado = estado;
    }

    public ConfiguracaoTributarioFacade getFacade() {
        return configuracaoTributarioFacade;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return configuracaoTributarioFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public List<MultaFiscalizacao> completaMulta(String parte) {
        return configuracaoTributarioFacade.getMultaFiscalizacaoFacade().listaFiltrando(parte.trim());
    }

    public BancoContaConfTributario getConta() {
        return conta;
    }

    public void setConta(BancoContaConfTributario conta) {
        this.conta = conta;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-tributario/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novaConfiguracaoTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        estado = new UF();
        conta = new BancoContaConfTributario();
        subContas = configuracaoTributarioFacade.getSubContaFacade().lista();
        eventoBCI = new EventoConfiguradoBCI();
        configuracaoWebService = new ConfiguracaoWebService();
        configuracaoIrregularidadesDoAlvara = new ConfiguracaoIrregularidadesDoAlvara();
        if (selecionado.getConfiguracaoTributarioBI() == null) {
            selecionado.setConfiguracaoTributarioBI(new ConfiguracaoTributarioBI());
        }
    }

    @URLAction(mappingId = "editarConfiguracaoTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        if (selecionado.getCidade() != null) {
            estado = selecionado.getCidade().getUf();
        }
        conta = new BancoContaConfTributario();
        subContas = configuracaoTributarioFacade.getSubContaFacade().lista();
        eventoBCI = new EventoConfiguradoBCI();
        configuracaoWebService = new ConfiguracaoWebService();
        configuracaoIrregularidadesDoAlvara = new ConfiguracaoIrregularidadesDoAlvara();
        carregaValoresPossivelDoAtributo();
        assistente = new AssistenteParametroAlvara();
        if (selecionado.getConfiguracaoTributarioBI() == null) {
            selecionado.setConfiguracaoTributarioBI(new ConfiguracaoTributarioBI());
        }
    }

    private void carregaValoresPossivelDoAtributo() {
        if (selecionado.getConfiguracaoPatrimonioLote() != null && selecionado.getConfiguracaoPatrimonioLote().getAtributo() != null) {
            valoresPossiveisAtributo = configuracaoTributarioFacade.getAtributoFacade().recuperar(selecionado.getConfiguracaoPatrimonioLote().getAtributo().getId()).getValoresPossiveis();
        }
    }

    @Override
    public void depoisDeSalvar() {
        configuracaoTributarioFacade.limparCache();
    }

    @Override
    public void salvarPorOperacao() {
        getFacade().salvar(selecionado, dividasPorTipoExportacao);
    }

    public List<SelectItem> getCidade() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        if (estado != null && estado.getId() != null) {
            for (Cidade object : configuracaoTributarioFacade.getCidadeFacade().listaCidade(estado)) {
                toReturn.add(new SelectItem(object, object.getNome()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getEstados() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (UF object : configuracaoTributarioFacade.getEstadoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public List<SelectItem> getDividas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Divida object : configuracaoTributarioFacade.getDividaFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getCodigo() + " - " + object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getDividasPorTipoCadastro() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Divida object : configuracaoTributarioFacade.getDividaFacade().listaDividasPorTipoCadastro(TipoCadastroTributario.ECONOMICO)) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getDividasImobiliario() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Divida object : configuracaoTributarioFacade.getDividaFacade().listaDividasPorTipoCadastro(TipoCadastroTributario.IMOBILIARIO)) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getDividasEconomico() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Divida object : configuracaoTributarioFacade.getDividaFacade().listaDividasPorTipoCadastro(TipoCadastroTributario.ECONOMICO)) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getDividasRural() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Divida object : configuracaoTributarioFacade.getDividaFacade().listaDividasPorTipoCadastro(TipoCadastroTributario.RURAL)) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getDividasPessoa() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Divida object : configuracaoTributarioFacade.getDividaFacade().listaDividasPorTipoCadastro(TipoCadastroTributario.PESSOA)) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTributos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Tributo object : configuracaoTributarioFacade.getTributoFacade().tributosOrdenadosPorDescricao()) {
            toReturn.add(new SelectItem(object, object.getCodigo() + " - " + object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposVerificacaoDebitoAlvara() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoVerificacaoDebitoAlvara tipo : TipoVerificacaoDebitoAlvara.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterCidade() {
        if (converterCidade == null) {
            converterCidade = new ConverterGenerico(Cidade.class, configuracaoTributarioFacade.getCidadeFacade());
        }
        return converterCidade;
    }

    public List<Irregularidade> completaIrregularidade(String parte) {
        return configuracaoTributarioFacade.getIrregularidadeFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterIrregularidade() {
        if (converterIrregularidade == null) {
            converterIrregularidade = new ConverterAutoComplete(Irregularidade.class, configuracaoTributarioFacade.getIrregularidadeFacade());
        }
        return converterIrregularidade;
    }

    public List<SelectItem> getTiposAlvara() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoAlvara object : TipoAlvara.values()) {
            if (!TipoAlvara.LOCALIZACAO_FUNCIONAMENTO.equals(object)) {
                toReturn.add(new SelectItem(object, object.getDescricaoSimples()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Mes object : Mes.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    private boolean validaCampos() {
        boolean resultado = true;
        if (selecionado.getNumDigitosDistrito() == null || selecionado.getNumDigitosDistrito() <= 0) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O número de dígitos do distrito deve ser maior que zero");
        }
        if (selecionado.getNumDigitosSetor() == null || selecionado.getNumDigitosSetor() <= 0) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O número de dígitos do setor deve ser maior que zero");
        }
        if (selecionado.getNumDigitosQuadra() == null || selecionado.getNumDigitosQuadra() <= 0) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O número de dígitos da quadra deve ser maior que zero");
        }
        if (selecionado.getNumDigitosLote() == null || selecionado.getNumDigitosLote() <= 0) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O número de dígitos do lote deve ser maior que zero");
        }
        if (selecionado.getNumDigitosUnidade() == null || selecionado.getNumDigitosUnidade() <= 0) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O número de dígitos da unidade deve ser maior que zero");
        }
        if (selecionado.getDividaMultaAcessoria() == null) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe a Dívida da Multa Acessória por Atraso na Declaração de ISS");
        }
        if (selecionado.getTributoMultaAcessoria() == null) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe o Tributo da Multa Acessória por Atraso na Declaração de ISS");
        }
        if (selecionado.getMultaAcessoriaComMovimento() == null) {
        } else if (selecionado.getMultaAcessoriaComMovimento().compareTo(BigDecimal.ZERO) < 0 || selecionado.getMultaAcessoriaComMovimento().compareTo(BigDecimal.ZERO) == 0) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe o valor da Multa Acessória com Movimento onde deve ser maior que zero");
        }
        if (selecionado.getMultaAcessoriaSemMovimento() == null) {
        } else if (selecionado.getMultaAcessoriaSemMovimento().compareTo(BigDecimal.ZERO) < 0 || selecionado.getMultaAcessoriaSemMovimento().compareTo(BigDecimal.ZERO) == 0) {
            resultado = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe o valor da Multa Acessória sem Movimento onde deve ser maior que zero");
        }
        return resultado;
    }

    public ConverterGenerico getConverterLotacaoVistoriadora() {
        if (converterLotacaoVistoriadora == null) {
            converterLotacaoVistoriadora = new ConverterGenerico(LotacaoVistoriadora.class, configuracaoTributarioFacade.getLotacaoVistoriadoraFacade());
        }
        return converterLotacaoVistoriadora;
    }

    public void setConverterLotacaoVistoriadora(ConverterGenerico converterLotacaoVistoriadora) {
        this.converterLotacaoVistoriadora = converterLotacaoVistoriadora;
    }

    public ConverterGenerico getConverterTipoDoctoOficial() {
        if (converterTipoDoctoOficial == null) {
            converterTipoDoctoOficial = new ConverterGenerico(TipoDoctoOficial.class, configuracaoTributarioFacade.getTipoDoctoOficialFacade());
        }
        return converterTipoDoctoOficial;
    }

    public void setConverterTipoDoctoOficial(ConverterGenerico converterTipoDoctoOficial) {
        this.converterTipoDoctoOficial = converterTipoDoctoOficial;
    }

    public List<SelectItem> getLotacaoVistoriadoras() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (LotacaoVistoriadora object : configuracaoTributarioFacade.getLotacaoVistoriadoraFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoDoctoOficialRendas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoDoctoOficial object : configuracaoTributarioFacade.getTipoDoctoOficialFacade().tipoDoctoPorModulo("", ModuloTipoDoctoOficial.CONTRATO_RENDAS_PATRIMONIAIS)) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoDoctoOficialBaixaCmc() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoDoctoOficial object : configuracaoTributarioFacade.getTipoDoctoOficialFacade().tipoDoctoPorModulo("", ModuloTipoDoctoOficial.CERTIDAO_BAIXA_ATIVIDADE)) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoDoctoOficialCEASA() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoDoctoOficial object : configuracaoTributarioFacade.getTipoDoctoOficialFacade().tipoDoctoPorModulo("", ModuloTipoDoctoOficial.CONTRATO_CEASA)) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getContas() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (subContas != null) {
            for (SubConta object : subContas) {
                toReturn.add(new SelectItem(object, getDescricaoConta(object)));
            }
        }
        return toReturn;
    }

    public void addConta() {
        if (validaConta()) {
            conta.setConfiguracao(selecionado);
            selecionado.getBancosArrecadacao().add(conta);
            conta = new BancoContaConfTributario();
        }
    }

    public String getDescricaoConta(SubConta subConta) {
        StringBuilder sb = new StringBuilder("");
        if (subConta != null) {
            if (subConta.getContaBancariaEntidade() != null) {
                if (subConta.getContaBancariaEntidade().getAgencia() != null) {
                    if (subConta.getContaBancariaEntidade().getAgencia().getBanco() != null) {
                        sb.append(subConta.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco()).append(" - ");
                        sb.append(subConta.getContaBancariaEntidade().getAgencia().getBanco().getDescricao()).append(" - ");
                    }
                    sb.append(subConta.getContaBancariaEntidade().getAgencia().getNumeroAgencia()).append("-");
                    sb.append(subConta.getContaBancariaEntidade().getAgencia().getDigitoVerificador()).append(" - ");
                    sb.append(subConta.getContaBancariaEntidade().getAgencia().getNomeAgencia()).append(" - ");
                }
                sb.append(subConta.getContaBancariaEntidade().getNumeroConta()).append(" - ");
                sb.append(subConta.getContaBancariaEntidade().getDigitoVerificador()).append(" - ");
                sb.append(subConta.getContaBancariaEntidade().getNomeConta()).append(" - ");
            }
            sb.append(subConta.getCodigo());
        }
        return sb.toString().replace("null", " ");
    }

    public boolean validaConta() {
        boolean valida = true;
        if (conta.getSubConta() == null) {
            valida = false;
            FacesUtil.addError("Impossivel Continuar", "Selecione uma conta antes de precionar o botão para adiciona-la");
        } else {
            for (BancoContaConfTributario bancoConta : selecionado.getBancosArrecadacao()) {
                if (bancoConta.getSubConta().equals(conta.getSubConta())) {
                    valida = false;
                    FacesUtil.addError("Impossivel Continuar", "A Conta selecionada já foi adicionada a esta configuração");
                }
            }
        }
        return valida;
    }

    public void addEvento() {
        if (validaEvento()) {
            eventoBCI.setConfiguracaoTributario(selecionado);
            selecionado.getEventosBCI().add(eventoBCI);
            eventoBCI = new EventoConfiguradoBCI();
        }
    }

    private boolean validaEvento() {
        if (eventoBCI.getEventoCalculo() != null) {
            return true;
        }
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Informe um Evendo para adicionar as configurações do BCI");
        return false;
    }

    public void removeEvento(EventoConfiguradoBCI evento) {
        selecionado.getEventosBCI().remove(evento);
    }

    public List<EventoCalculo> getEventos() {
        return configuracaoTributarioFacade.getEventoCalculoFacade().listaEventosVigentes();
    }

    public List<SelectItem> getIdentificadoresEvento() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (EventoConfiguradoBCI.Identificacao identificacao : EventoConfiguradoBCI.Identificacao.values()) {
            toReturn.add(new SelectItem(identificacao, identificacao.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getIncidenciaEvento() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (EventoConfiguradoBCI.Incidencia incidencia : EventoConfiguradoBCI.Incidencia.values()) {
            toReturn.add(new SelectItem(incidencia, incidencia.getDescricao()));
        }
        return toReturn;
    }

    public void addIrregularidade() {
        if (configuracaoIrregularidadesDoAlvara.getIrregularidade() == null) {
            FacesUtil.addError("Atenção!", "Informe a irregularidade.");
        } else {
            configuracaoIrregularidadesDoAlvara.setConfiguracaoTributario(selecionado);
            selecionado.getIrregularidadesDoAlvara().add(configuracaoIrregularidadesDoAlvara);
            configuracaoIrregularidadesDoAlvara = new ConfiguracaoIrregularidadesDoAlvara();
        }
    }

    public void removeIrregularidade(ConfiguracaoIrregularidadesDoAlvara irregularidade) {
        selecionado.getIrregularidadesDoAlvara().remove(irregularidade);
        configuracaoIrregularidadesDoAlvara = new ConfiguracaoIrregularidadesDoAlvara();
    }

    public List<SelectItem> getAtributos() {
        return Util.getListSelectItem(configuracaoTributarioFacade.getAtributoFacade().listaAtributosPorClasse(ClasseDoAtributo.LOTE));
    }

    public void carregaListaAtributosLote() {
        selecionado.getConfiguracaoPatrimonioLote().getItensConfiguracaoPatrimonioLote().clear();
        carregaValoresPossivelDoAtributo();
        for (PatrimonioDoLote patrimonioDoLote : PatrimonioDoLote.values()) {
            ItemConfiguracaoPatrimonioLote item = new ItemConfiguracaoPatrimonioLote();
            item.setConfiguracaoPatrimonioLote(selecionado.getConfiguracaoPatrimonioLote());
            item.setPatrimonioDoLote(patrimonioDoLote);
            selecionado.getConfiguracaoPatrimonioLote().getItensConfiguracaoPatrimonioLote().add(item);
        }
    }

    public ConverterGenerico getConverterValorPossivel() {
        if (converterValorPossivel == null) {
            converterValorPossivel = new ConverterGenerico(ValorPossivel.class, configuracaoTributarioFacade.getAtributoFacade().getValorPossivelFacade());
        }
        return converterValorPossivel;
    }

    public List<SelectItem> getValoresPossiveis() {
        return Util.getListSelectItem(valoresPossiveisAtributo);
    }

    public List<SelectItem> getTipoWebService() {
        return Util.getListSelectItem(TipoWebService.values());
    }

    public void alterarConfiguracaoWebService(ConfiguracaoWebService config) {
        configuracaoWebService = (ConfiguracaoWebService) Util.clonarEmNiveis(config, 1);

    }

    public void removerConfiguracaoWebService(ConfiguracaoWebService config) {
        selecionado.getItemConfiguracaoWebService().remove(config);

    }

    public void adicionarConfiguracaoWebService() {
        try {
            validarConfiguracaoWebService(configuracaoWebService);
            configuracaoWebService.setConfiguracaoTributario(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getItemConfiguracaoWebService(), configuracaoWebService);
            configuracaoWebService = new ConfiguracaoWebService();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarConfiguracaoWebService(ConfiguracaoWebService configuracaoWebService) {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoWebService.getTipo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo.");
        }
        if (configuracaoWebService.getChave().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Chave.");
        }
        if (configuracaoWebService.getUrl().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a URL.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void removerDividaTributo(ConfiguracaoAlvara configuracao) {
        selecionado.getConfiguracaoAlvara().remove(configuracao);
    }

    public void editarDividaTributo(ConfiguracaoAlvara configuracao) {
        assistente = new AssistenteParametroAlvara();
        assistente.setCriadoEm(configuracao.getCriadoEm());
        assistente.setTributoSelecionado(configuracao.getTributo());
        assistente.setDividaSelecionada(configuracao.getDivida());
        assistente.setTipoAlvara(configuracao.getTipoAlvara());
        assistente.setTipoPrimeiroCalculo(configuracao.getTipoPrimeiroCalculo());
        assistente.setTipoCalculoAlvaraSelecionado(configuracao.getTipoCalculoAlvara());
        assistente.setTipoCalculoRenovAlvaraSelecionado(configuracao.getTipoCalculoRenovacaoAlvara());
        assistente.setValorUFMFixoPrimeiroCalculo(configuracao.getValorUFMFixoPrimeiroCalculo());
        assistente.setValorUFMFixoCalculoSelecionado(configuracao.getValorUFMFixoCalculoAlvara());
        assistente.setValorUFMFixoCalcRenovacaoSelecionado(configuracao.getValorUFMFixoCalcRenovAlvara());
        assistente.setDiasVencDebito(configuracao.getDiasVencDebito());
    }

    private void validarTributoDividaAlvara() {
        ValidacaoException ve = new ValidacaoException();
        if (assistente.getDividaSelecionada() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione uma Dívida.");
        }
        if (assistente.getTributoSelecionado() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um Tributo.");
        }
        if (assistente.getTipoAlvara() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um Tipo de Alvará.");
        }
        if (assistente.getTipoCalculoAlvaraSelecionado() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um Tipo de Cálculo de Alvará.");
        } else {
            if (TipoCalculoAlvara.FIXO_UFMRB.equals(assistente.getTipoCalculoAlvaraSelecionado())) {
                if (assistente.getValorUFMFixoCalculoSelecionado() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Valor Fixo Cálculo Alvará deve ser informado.");
                }
            }
        }
        if (assistente.getTipoCalculoRenovAlvaraSelecionado() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um Tipo de Cálculo de Renovação do Alvará.");
        } else {
            if (TipoCalculoAlvara.FIXO_UFMRB.equals(assistente.getTipoCalculoRenovAlvaraSelecionado())) {
                if (assistente.getValorUFMFixoCalculoSelecionado() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Valor Fixo Cálculo de Renovação Alvará deve ser informado.");
                }
            }
        }
        if (assistente.getTipoPrimeiroCalculo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um Tipo para o Primeiro Cálculo.");
        } else {
            if (TipoCalculoAlvara.FIXO_UFMRB.equals(assistente.getTipoPrimeiroCalculo())) {
                if (assistente.getValorUFMFixoPrimeiroCalculo() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Valor Fixo do Primeiro Cálculo deve ser informado.");
                }
            }
        }
        if (assistente.getDiasVencDebito() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Dias Para Vencimento do Débito deve ser informado.");
        } else {
            if (assistente.getDiasVencDebito().compareTo(0) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Dias Para Vencimento do Débito deve ser maior que zero(0).");
            }
        }

        ve.lancarException();
    }

    public String montarTipoCalculoAlvara(TipoCalculoAlvara tipoCalculoAlvara, BigDecimal valorUFMRB) {
        if (tipoCalculoAlvara != null) {
            if (TipoCalculoAlvara.FIXO_UFMRB.equals(tipoCalculoAlvara)) {
                return tipoCalculoAlvara.getDescricao() + "\n" +
                    "Valor Fixo: " + (valorUFMRB != null ? Util.reaisToString(valorUFMRB) : Util.reaisToString(BigDecimal.ZERO));
            }
            return tipoCalculoAlvara.getDescricao();
        }
        return "";
    }

    public void adicionarTributoDividaAlvara() {
        try {
            validarTributoDividaAlvara();
            ConfiguracaoAlvara configuracaoAlvara = new ConfiguracaoAlvara();
            configuracaoAlvara.setConfiguracaoTributario(selecionado);
            configuracaoAlvara.setTributo(assistente.getTributoSelecionado());
            configuracaoAlvara.setDivida(assistente.getDividaSelecionada());
            configuracaoAlvara.setTipoAlvara(assistente.getTipoAlvara());
            configuracaoAlvara.setTipoCalculoAlvara(assistente.getTipoCalculoAlvaraSelecionado());
            configuracaoAlvara.setTipoCalculoRenovacaoAlvara(assistente.getTipoCalculoRenovAlvaraSelecionado());
            configuracaoAlvara.setTipoPrimeiroCalculo(assistente.getTipoPrimeiroCalculo());
            configuracaoAlvara.setValorUFMFixoCalculoAlvara(assistente.getValorUFMFixoCalculoSelecionado());
            configuracaoAlvara.setValorUFMFixoCalcRenovAlvara(assistente.getValorUFMFixoCalcRenovacaoSelecionado());
            configuracaoAlvara.setValorUFMFixoPrimeiroCalculo(assistente.getValorUFMFixoPrimeiroCalculo());
            configuracaoAlvara.setDiasVencDebito(assistente.getDiasVencDebito());
            if (assistente.getCriadoEm() != null) {
                configuracaoAlvara.setCriadoEm(assistente.getCriadoEm());
            }
            Util.adicionarObjetoEmLista(selecionado.getConfiguracaoAlvara(), configuracaoAlvara);
            limparCampos();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            logger.error("adicionarTributoAlvaraAmbiental {}", e);
        }
    }

    public void removerParametroAlvara(ParametroValorAlvaraAmbiental paramentroAmbiental) {
        selecionado.getParametrosValorAlvaraAmbiental().remove(paramentroAmbiental);
    }

    private void validarParametroAlvara() {
        ValidacaoException ve = new ValidacaoException();
        if (assistente.getParametroAlvara().getTipoLicencaAmbiental() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um Tipo de Licença Ambiental.");
        }
        for (ParametroValorAlvaraAmbiental parametro : selecionado.getParametrosValorAlvaraAmbiental()) {
            if (!parametro.equals(assistente.getParametroAlvara()) && parametro.getTipoLicencaAmbiental().equals(assistente.getParametroAlvara().getTipoLicencaAmbiental())) {
                ve.adicionarMensagemDeCampoObrigatorio("O Tipo de Licença Ambiental selecionado já está adicionado.");
                break;
            }
        }
        ve.lancarException();
    }

    public void adicionarParametroAlvaraAmbiental() {
        try {
            validarParametroAlvara();
            assistente.getParametroAlvara().setConfiguracaoTributario(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getParametrosValorAlvaraAmbiental(), assistente.getParametroAlvara());
            limparCampos();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            logger.error("adicionarParametroAlvaraAmbiental {}", e);
        }
    }

    private void limparCampos() {
        assistente = new AssistenteParametroAlvara();
        assistente.setDividaSelecionada(new Divida());
        assistente.setTributoSelecionado(new Tributo());
        assistente.setTipoAlvara(null);
    }

    public List<SelectItem> getTipoCalculoAlvara() {
        return Util.getListSelectItem(TipoCalculoAlvara.values(), false);
    }

    public List<SelectItem> getTipoLicencaAmbiental() {
        return Util.getListSelectItem(TipoLicencaAmbiental.values(), false);
    }

    public void alterarParametroAlvara(ParametroValorAlvaraAmbiental parametro) {
        assistente.setParametroAlvara((ParametroValorAlvaraAmbiental) Util.clonarObjeto(parametro));
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public void setConfiguracaoTributarioFacade(ConfiguracaoTributarioFacade configuracaoTributarioFacade) {
        this.configuracaoTributarioFacade = configuracaoTributarioFacade;
    }

    public AssistenteParametroAlvara getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteParametroAlvara assistente) {
        this.assistente = assistente;
    }

    public void onTabChange(TabChangeEvent event) {
        switch (event.getTab().getId()) {
            case "tabArquivoBI": {
                inicializarAbaTributarioBI();
            }
        }
    }

    private void inicializarAbaTributarioBI() {
        configuracaoTributarioBIDivida = new ConfiguracaoTributarioBIDivida();
        if (dividasPorTipoExportacao == null) {
            dividasPorTipoExportacao = selecionado.getConfiguracaoTributarioBI().getId() != null ? configuracaoTributarioFacade
                .buscarTodasDividasTipoExportacaoBI(selecionado.getConfiguracaoTributarioBI()) : Lists.newArrayList();
        }
        FacesUtil.atualizarComponente("Formulario:tabView:opArquivoBI");
    }

    public List<ConfiguracaoTributarioBIDivida> getDividasPorTipoExportacaoTabela() {
        return dividasPorTipoExportacao != null ? dividasPorTipoExportacao.stream()
            .filter(conf -> !conf.getExcluido())
            .collect(Collectors.toList()) : Lists.newArrayList();
    }

    public void validarDividaTipoExportacaoBI() {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoTributarioBIDivida.getTipoExportacaoArquivoBI() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Exportação deve ser informado.");
        }
        if (configuracaoTributarioBIDivida.getDivida() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Dívida deve ser informado.");
        }
        ve.lancarException();
        if (dividasPorTipoExportacao.stream().anyMatch(i ->
            !i.getExcluido()
                && i.getTipoExportacaoArquivoBI().equals(configuracaoTributarioBIDivida.getTipoExportacaoArquivoBI())
                && i.getDivida().equals(configuracaoTributarioBIDivida.getDivida()))) {
            ve.adicionarMensagemDeCampoObrigatorio("A dívida " + configuracaoTributarioBIDivida.getDivida() +
                " já está adicionada para o Tipo de Exportação " +
                configuracaoTributarioBIDivida.getTipoExportacaoArquivoBI() + ".");
        }
        ve.lancarException();
    }

    public void adicionarDividaTipoExportacaoBI() {
        try {
            validarDividaTipoExportacaoBI();
            dividasPorTipoExportacao.add(configuracaoTributarioBIDivida);
            configuracaoTributarioBIDivida = new ConfiguracaoTributarioBIDivida();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void removerDividaTipoExportacaoBI(ConfiguracaoTributarioBIDivida configuracaoTributarioBIDivida) {
        configuracaoTributarioBIDivida.setExcluido(Boolean.TRUE);
    }

    public List<SelectItem> getTiposExportacaoArquivoBI() {
        List<TipoExportacaoArquivoBI> lista = Arrays.stream(TipoExportacaoArquivoBI.values())
            .filter(i -> i.getModuloSistema().equals(ModuloSistema.TRIBUTARIO))
            .collect(Collectors.toList());
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoExportacaoArquivoBI tipoExportacaoArquivoBI : lista) {
            toReturn.add(new SelectItem(tipoExportacaoArquivoBI, tipoExportacaoArquivoBI.getDescricao()));
        }
        return toReturn;
    }

    public void adicionarLotacaoRendas() {
        try {
            if (lotacaoVistoriadoraRendasSelecionada == null) return;
            ValidacaoException ve = new ValidacaoException();
            for (LotacaoVistoriadoraConfigTribRendas lotacao : selecionado.getRendasLotacoesVistoriadoras()) {
                if (lotacaoVistoriadoraRendasSelecionada.getId().equals(lotacao.getLotacaoVistoriadora().getId())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Lotação " + lotacao.getLotacaoVistoriadora().getDescricao() + " já foi adicionada.");
                }
            }
            ve.lancarException();
            LotacaoVistoriadoraConfigTribRendas novaLotacao = new LotacaoVistoriadoraConfigTribRendas(selecionado);
            novaLotacao.setLotacaoVistoriadora(lotacaoVistoriadoraRendasSelecionada);
            selecionado.getRendasLotacoesVistoriadoras().add(novaLotacao);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        } finally {
            lotacaoVistoriadoraRendasSelecionada = null;
        }
    }

    public void removerLotacaoRendas(LotacaoVistoriadora lotacao) {
        try {
            selecionado.getRendasLotacoesVistoriadoras().removeIf(l -> l.getLotacaoVistoriadora().getId().equals(lotacao.getId()));
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }
}
