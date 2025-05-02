/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteRendasPatrimoniais;
import br.com.webpublico.entidadesauxiliares.OrdenaResultadoParcelaPorVencimento;
import br.com.webpublico.entidadesauxiliares.ParcelaContratoRendaPatrimonial;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContratoRendasPatrimoniaisFacade;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@ManagedBean(name = "contratoRendasPatrimoniaisControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoContratoRendas", pattern = "/contrato-de-rendas-patrimoniais/novo/", viewId = "/faces/tributario/rendaspatrimoniais/contrato/edita.xhtml"),
    @URLMapping(id = "editarContratoRendas", pattern = "/contrato-de-rendas-patrimoniais/editar/#{contratoRendasPatrimoniaisControlador.id}/", viewId = "/faces/tributario/rendaspatrimoniais/contrato/edita.xhtml"),
    @URLMapping(id = "listarContratoRendas", pattern = "/contrato-de-rendas-patrimoniais/listar/", viewId = "/faces/tributario/rendaspatrimoniais/contrato/lista.xhtml"),
    @URLMapping(id = "verContratoRendas", pattern = "/contrato-de-rendas-patrimoniais/ver/#{contratoRendasPatrimoniaisControlador.id}/", viewId = "/faces/tributario/rendaspatrimoniais/contrato/visualizar.xhtml"),
    @URLMapping(id = "renovarContrato", pattern = "/contrato-de-rendas-patrimoniais/renovacao/", viewId = "/faces/tributario/rendaspatrimoniais/contrato/renovacao.xhtml")
})
public class ContratoRendasPatrimoniaisControlador extends PrettyControlador<ContratoRendasPatrimoniais> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ContratoRendasPatrimoniaisControlador.class);

    @EJB
    private ContratoRendasPatrimoniaisFacade contratoRendasPatrimoniaisFacade;
    private ParametroRendas parametroRendas;
    private Pessoa locatario;
    private PontoComercial pontoComercial;
    private ConverterAutoComplete converterLocalizacao;
    private ConverterAutoComplete converterPonto;
    private ConverterAutoComplete converterPessoa;
    private ConverterGenerico converterParametro;
    private boolean efetivouContrato;
    private boolean gerouDam;
    private Localizacao localizacao;
    private ConverterGenerico converterAtividadePonto;
    private ValorDivida valorDividaContrato;
    private ConverterAutoComplete converterLotacaoVistoriadora;
    private ConverterAutoComplete converterCnae;
    private PontoComercialContratoRendasPatrimoniais pontoComercialContratoRendasPatrimoniais;
    private ContratoRendasPatrimoniais contratoRenovado;
    private ConfiguracaoTributario configuracaoTributario;
    private List<ParcelaContratoRendaPatrimonial> parcelasRenovacao;
    private ContratoRendaCNAE contratoRendasCNAE;
    private Boolean contratoSalvo = false;
    private Boolean contratoEfetivado = false;
    private Boolean locatarioJaPossuiContrato = false;
    private List<ResultadoParcela> parcelas;
    private final List<ResultadoParcela> parcelasSelecionadas = Lists.newArrayList();
    private Integer quantidadeParcelasPagas;
    private ContratoRendasPatrimoniais contratoAlterado;
    private boolean mostraDemonstrativoDeParcelas = false;
    private List<ParcelaContratoRendaPatrimonial> parcelasAlteracaoContrato;
    private BigDecimal valorDoCalculoDoContrato;
    private List<Localizacao> localizacoesDisponiveisRenovacao;
    private List<Localizacao> localizacoesSelecionadas;
    private List<ContratoRendasPatrimoniais> contratosRenovacao;
    private AssistenteRendasPatrimoniais assistenteRendasPatrimoniais;
    private Future<AssistenteRendasPatrimoniais> future;
    private List<LotacaoVistoriadora> lotacoesUsuarioENasConfigsTributario;

    public ContratoRendasPatrimoniaisControlador() {
        super(ContratoRendasPatrimoniais.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return contratoRendasPatrimoniaisFacade;
    }

    public List<ParcelaContratoRendaPatrimonial> getParcelasRenovacao() {
        return parcelasRenovacao;
    }


    public void setParcelasRenovacao(List<ParcelaContratoRendaPatrimonial> parcelasRenovacao) {
        this.parcelasRenovacao = parcelasRenovacao;
    }

    public ContratoRendasPatrimoniais getContratoAlterado() {
        return contratoAlterado;
    }

    public void setContratoAlterado(ContratoRendasPatrimoniais contratoAlterado) {
        this.contratoAlterado = contratoAlterado;
    }

    public List<LotacaoVistoriadora> getLotacoesUsuarioENasConfigsTributario() {
        return lotacoesUsuarioENasConfigsTributario;
    }

    public void setLotacoesUsuarioENasConfigsTributario(List<LotacaoVistoriadora> lotacoesUsuarioENasConfigsTributario) {
        this.lotacoesUsuarioENasConfigsTributario = lotacoesUsuarioENasConfigsTributario;
    }

    public boolean isMostraDemonstrativoDeParcelas() {
        return mostraDemonstrativoDeParcelas;
    }

    public void setMostraDemonstrativoDeParcelas(boolean mostraDemonstrativoDeParcelas) {
        this.mostraDemonstrativoDeParcelas = mostraDemonstrativoDeParcelas;
    }

    public PontoComercial getPontoComercial() {
        return pontoComercial;
    }

    public void setPontoComercial(PontoComercial pontoComercial) {
        this.pontoComercial = pontoComercial;
    }

    public List<Localizacao> getLocalizacoesDisponiveisRenovacao() {
        return localizacoesDisponiveisRenovacao;
    }

    public void setLocalizacoesDisponiveisRenovacao(List<Localizacao> localizacoesDisponiveisRenovacao) {
        this.localizacoesDisponiveisRenovacao = localizacoesDisponiveisRenovacao;
    }

    public List<Localizacao> getLocalizacoesSelecionadas() {
        return localizacoesSelecionadas;
    }

    public void setLocalizacoesSelecionadas(List<Localizacao> localizacoesSelecionadas) {
        this.localizacoesSelecionadas = localizacoesSelecionadas;
    }

    public boolean isEfetivouContrato() {
        if (SituacaoContratoRendasPatrimoniais.ENCERRADO.equals(selecionado.getSituacaoContrato())) {
            return false;
        }
        return efetivouContrato;
    }

    public void setEfetivouContrato(boolean efetivouContrato) {
        this.efetivouContrato = efetivouContrato;
    }

    public boolean isAtivaBotaoEfetivar() {
        if (selecionado.getSituacaoContrato() == null || SituacaoContratoRendasPatrimoniais.ENCERRADO.equals(selecionado.getSituacaoContrato())) {
            return true;
        }
        return efetivouContrato;
    }

    public List<ResultadoParcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<ResultadoParcela> parcelas) {
        this.parcelas = parcelas;
    }

    public Integer getQuantidadeParcelasPagas() {
        if (quantidadeParcelasPagas == null) {
            quantidadeParcelasPagas = contratoRendasPatrimoniaisFacade.getQuantidadeParcelasPagas(selecionado);
        }
        return quantidadeParcelasPagas;
    }

    public ContratoRendaCNAE getContratoRendasCNAE() {
        return contratoRendasCNAE;
    }

    public void setContratoRendasCNAE(ContratoRendaCNAE contratoRendasCNAE) {
        this.contratoRendasCNAE = contratoRendasCNAE;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    public Boolean getLocatarioJaPossuiContrato() {
        return locatarioJaPossuiContrato;
    }

    public boolean isDesabilitaEfetivacao() {
        if (SituacaoContratoRendasPatrimoniais.ENCERRADO.equals(selecionado.getSituacaoContrato()) ||
            SituacaoContratoRendasPatrimoniais.RENOVADO.equals(selecionado.getSituacaoContrato()) ||
            SituacaoContratoRendasPatrimoniais.ATIVO.equals(selecionado.getSituacaoContrato())) {
            return false;
        } else {
            return efetivouContrato;
        }
    }

    public boolean isGerouDam() {
        return gerouDam;
    }

    public void setGerouDam(boolean gerouDam) {
        this.gerouDam = gerouDam;
    }

    public Pessoa getLocatario() {
        return locatario;
    }

    public void setLocatario(Pessoa locatario) {
        this.locatario = locatario;
    }

    public ParametroRendas getParametroRendas() {
        return parametroRendas;
    }

    public void setParametroRendas(ParametroRendas parametroRendas) {
        this.parametroRendas = parametroRendas;
    }

    public AssistenteRendasPatrimoniais getAssistenteRendasPatrimoniais() {
        return assistenteRendasPatrimoniais;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contrato-de-rendas-patrimoniais/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Boolean getContratoEfetivado() {
        return contratoEfetivado;
    }

    public Boolean getContratoSalvo() {
        return contratoSalvo;
    }

    public void setContratoSalvo(Boolean contratoSalvo) {
        this.contratoSalvo = contratoSalvo;
    }

    @URLAction(mappingId = "novoContratoRendas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        try {
            validarPermissaoUsuario();
            super.novo();
            List<LotacaoVistoriadora> lotacoesUsuarioLogado = Lists.newArrayList();
            lotacoesUsuarioENasConfigsTributario = Lists.newArrayList();
            configuracaoTributario = contratoRendasPatrimoniaisFacade.getConfiguracaoTributarioFacade().retornaUltimo();
            UsuarioSistema usuarioSistema = contratoRendasPatrimoniaisFacade.getUsuarioSistemaFacade().recuperar(contratoRendasPatrimoniaisFacade.getSistemaFacade().getUsuarioCorrente().getId());
            VigenciaTribUsuario vigenciaTribUsuario = usuarioSistema.getVigenciaTribUsuarioVigente();
            if (vigenciaTribUsuario != null) {
                lotacoesUsuarioLogado.addAll(vigenciaTribUsuario.getLotacaoTribUsuarios().stream().map(LotacaoTribUsuario::getLotacao).collect(Collectors.toList()));
                if (!lotacoesUsuarioLogado.isEmpty()) {
                    List<LotacaoVistoriadora> lotacoesConfigTrib = configuracaoTributario.getRendasLotacoesVistoriadoras().stream().map(LotacaoVistoriadoraConfigTribRendas::getLotacaoVistoriadora).collect(Collectors.toList());
                    if (lotacoesUsuarioLogado.size() == 1) {
                        if (lotacoesConfigTrib.contains(lotacoesConfigTrib.get(0))) {
                            selecionado.setLotacaoVistoriadora(lotacoesUsuarioLogado.get(0));
                        }
                    } else {
                        for (LotacaoVistoriadora lotacaoUsuario : lotacoesUsuarioLogado) {
                            for (LotacaoVistoriadora lotacaoConfigTrib : lotacoesConfigTrib) {
                                if (lotacaoUsuario.getId().equals(lotacaoConfigTrib.getId())) {
                                    lotacoesUsuarioENasConfigsTributario.add(lotacaoConfigTrib);
                                    break;
                                }
                            }
                        }
                        if (!lotacoesUsuarioENasConfigsTributario.isEmpty()) {
                            selecionado.setLotacaoVistoriadora(lotacoesUsuarioENasConfigsTributario.get(0));
                        }
                    }
                } else {
                    throw new ValidacaoException().adicionarMensagemDeOperacaoNaoPermitida("O usuário logado não possui acesso às lotações de rendas patrimoniais.");
                }
            }
            selecionado.setValorUfmDoContrato(contratoRendasPatrimoniaisFacade.getMoedaFacade().buscarValorUFMParaAno(getSistemaControlador().getExercicioCorrente().getAno()));
            contratoRendasCNAE = new ContratoRendaCNAE();
            this.parametroRendas = recuperaParametroRendas(getSistemaControlador().getDataOperacao());
            validarParametroRendas();
            defineParametrosRendasPatrimoniais();
            if (!isSessao()) {
                locatarioJaPossuiContrato = false;
            } else {
                Web.pegaTodosFieldsNaSessao(this);
            }
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getAllMensagens());
            redireciona();
        } catch (IllegalAccessException e) {
            logger.debug(e.getMessage());
        }
    }

    private void validarParametroRendas() {
        if (parametroRendas == null) {
            throw new ValidacaoException().adicionarMensagemDeOperacaoNaoPermitida("Parâmetro de Rendas Patrimoniais não encontrado para o exercício corrente.");
        }
        if (parametroRendas.getDataInicioContrato() == null || parametroRendas.getDataFimContrato() == null) {
            throw new ValidacaoException().adicionarMensagemDeOperacaoNaoPermitida("Não foram definidas as datas de início e término do contrato no Parâmetro de Rendas Patrimoniais!");
        } else if (parametroRendas.getDataInicioContrato().after(contratoRendasPatrimoniaisFacade.getSistemaFacade().getDataOperacao())) {
            throw new ValidacaoException().adicionarMensagemDeOperacaoNaoPermitida("A data corrente é menor que a data de parâmetro de início de contrato!");
        }
    }

    private void validarPermissaoUsuario() {
        if (!usuarioTemPermissao()) {
            throw new ValidacaoException("Não foi possível continuar! Você não tem autorização para cadastrar novos contratos.");
        }
    }

    private void defineParametrosRendasPatrimoniais() {
        if (this.parametroRendas != null) {
            sugereParametroRendas();
            locatario = null;
            selecionado.setUsuarioInclusao(contratoRendasPatrimoniaisFacade.getSistemaFacade().getUsuarioCorrente());
            efetivouContrato = false;
            gerouDam = false;
            localizacao = new Localizacao();
            selecionado.setDataInicio(getSistemaControlador().getDataOperacao());
            selecionado.setDataFim(this.parametroRendas.getDataFimContrato());
            selecionado.setPeriodoVigencia(contratoRendasPatrimoniaisFacade.mesVigente(selecionado.getDataInicio(), parametroRendas));
            pontoComercialContratoRendasPatrimoniais = new PontoComercialContratoRendasPatrimoniais();
        } else {
            visualizaDialogErro();
        }
    }

    @URLAction(mappingId = "editarContratoRendas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        pontoComercialContratoRendasPatrimoniais = new PontoComercialContratoRendasPatrimoniais();
        contratoRendasCNAE = new ContratoRendaCNAE();
        if (!selecionado.getPontoComercialContratoRendasPatrimoniais().isEmpty()) {
            localizacao = selecionado.getPontoComercialContratoRendasPatrimoniais().get(0).getPontoComercial().getLocalizacao();
        }
        sugereParametroRendas();
        atribuiValoresObjetos();
        locatarioJaPossuiContrato = false;
    }

    @URLAction(mappingId = "verContratoRendas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        if (recuperaParametroRendas(selecionado.getDataInicio()) != null) {
            recuperaParcelasDoCalculo();
        } else {
            visualizaDialogErro();
            redireciona();
        }

    }

    public void atribuiValoresObjetos() {
        this.valorDividaContrato = contratoRendasPatrimoniaisFacade.retornaValorDividaDoContrato(selecionado);
        locatario = selecionado.getLocatario();
        efetivouContrato = isContratoEfetivado();
        gerouDam = false;
    }

    @Override
    public void salvar() {
        if (validaCamposControlador()) {
            selecionado.setLocatario(locatario);
            selecionado.setDiaVencimento(parametroRendas.getDiaVencimentoParcelas().intValue());
            contratoSalvo = true;
            if (!selecionado.getPontoComercialContratoRendasPatrimoniais().isEmpty() &&
                selecionado.getPontoComercialContratoRendasPatrimoniais().get(0).getPontoComercial().getLocalizacao() != null) {
                selecionado.setTipoUtilizacao(selecionado.getPontoComercialContratoRendasPatrimoniais().get(0).getPontoComercial().getLocalizacao().getTipoOcupacaoLocalizacao());
            }
            if (validaCampos()) {
                try {
                    if (selecionado.getId() == null) {
                        selecionado = contratoRendasPatrimoniaisFacade.salvarContrato(selecionado);
                    } else {
                        contratoRendasPatrimoniaisFacade.salvar(selecionado);
                    }
                    contratoRendasPatrimoniaisFacade.gerarDebito(selecionado, contratoRendasPatrimoniaisFacade.getSistemaFacade().getExercicioCorrente(), BigDecimal.ZERO, null);
                    efetivouContrato = SituacaoContratoRendasPatrimoniais.ATIVO.equals(selecionado.getSituacaoContrato());
                    FacesUtil.addInfo("Contrato efetivado com sucesso!", "");
                } catch (Exception e) {
                    logger.debug("Erro ao gerar os débitos: {}", e);
                    FacesUtil.addError("Houve um problema ao executar esta operação: ", e.getMessage().toString());   // COMENTADO PQ ESTÁ DANDO EXCEPTION PELOS AJUSTES NA INTEGRAÇÃO TRIBUTÁRIO
                }
            }
        }
    }

    private boolean validaCampos() {
        boolean resultado = true;
        if (selecionado.getDataInicio() == null) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe a Data de Início do Contrato.");
        }
        if (selecionado.getLocatario() == null && selecionado.getLocatario().getId() == null) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe o Locatário.");
        }
        if (this.parametroRendas == null && this.parametroRendas.getId() == null) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe o Parâmetro para a Lotação informada.");
        }
        if (selecionado.getPontoComercialContratoRendasPatrimoniais().isEmpty()) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe o(s) Ponto(s) Comercial(ais) objetos do contrato.");
        }
        if (selecionado.getTipoUtilizacao() == null) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe o Tipo de Ponto Comercial.");
        }
        if (selecionado.getUsuarioInclusao() == null && selecionado.getUsuarioInclusao().getId() == null) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe o usuário.");
        }
        return resultado;
    }

    public ValorDivida getValorDividaContrato() {
        return valorDividaContrato;
    }

    public void setValorDividaContrato(ValorDivida valorDividaContrato) {
        this.valorDividaContrato = valorDividaContrato;
    }

    public List<SelectItem> getTipos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoUtilizacaoRendasPatrimoniais t : TipoUtilizacaoRendasPatrimoniais.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public Converter getConverterParametro() {
        if (converterParametro == null) {
            converterParametro = new ConverterGenerico(ParametroRendas.class, contratoRendasPatrimoniaisFacade.getParametroFacade());
        }
        return converterParametro;
    }

    public Converter getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, contratoRendasPatrimoniaisFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public Converter getConverterLocalizacao() {
        if (converterLocalizacao == null) {
            converterLocalizacao = new ConverterAutoComplete(Localizacao.class, contratoRendasPatrimoniaisFacade.getLocalizacaoFacade());
        }
        return converterLocalizacao;
    }

    public Converter getConverterPonto() {
        if (converterPonto == null) {
            converterPonto = new ConverterAutoComplete(PontoComercial.class, contratoRendasPatrimoniaisFacade.getPontoComercialFacade());
        }
        return converterPonto;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return contratoRendasPatrimoniaisFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public List<Localizacao> completaLocalizacao(String parte) {
        UsuarioSistema usuarioCorrente = contratoRendasPatrimoniaisFacade.getUsuarioSistemaFacade().recuperar(contratoRendasPatrimoniaisFacade.getSistemaFacade().getUsuarioCorrente().getId());

        if (usuarioCorrente.temEstaLotacao(selecionado.getLotacaoVistoriadora(), SistemaFacade.getDataCorrente())) {
            return contratoRendasPatrimoniaisFacade.getLocalizacaoFacade().listaFiltrandoPorLotacao(selecionado.getLotacaoVistoriadora(),
                parte.trim(), "descricao");
        } else {
            return Lists.newArrayList();
        }
    }

    public boolean podeImprimirDAM() {
        return isEfetivouContrato() && contratoRendasPatrimoniaisFacade.existemParcelasEmAberto(selecionado);
    }

    public List<PontoComercial> completaPonto(String parte) {
        List<PontoComercial> lista = Lists.newArrayList();
        if (localizacao == null || localizacao.getId() == null) {
            PontoComercial erro = new PontoComercial();
            erro.setNumeroBox("Selecione a Localização desejada.");
            lista.add(erro);
        } else {
            lista = contratoRendasPatrimoniaisFacade.getPontoComercialFacade().buscarPontosQueNaoEstaoEmContratoRendas(localizacao, parte.trim());
            if (lista.isEmpty()) {
                PontoComercial erro = new PontoComercial();
                erro.setNumeroBox("Ponto(s) Comercial(is) não encontrado(s) ou ocupado(s).");
                lista.add(erro);
            }
        }
        return lista;
    }

    public List<PontoComercial> completaPontosQueNaoEstaoEmContrato(String parte) {
        List<PontoComercial> lista = contratoRendasPatrimoniaisFacade.getPontoComercialFacade().buscarPontosQueNaoEstaoEmContratoRendas(localizacao, parte.trim());
        if (contratoAlterado != null) {
            for (PontoComercialContratoRendasPatrimoniais pontoComercial : selecionado.getPontoComercialContratoRendasPatrimoniais()) {
                boolean temPontoComercial = false;
                for (PontoComercialContratoRendasPatrimoniais pontoComercialNovo : contratoAlterado.getPontoComercialContratoRendasPatrimoniais()) {
                    if (pontoComercial.getPontoComercial().equals(pontoComercialNovo.getPontoComercial())) {
                        temPontoComercial = true;
                    }
                }
                if (!temPontoComercial) {
                    lista.add(pontoComercial.getPontoComercial());
                }
            }
        }
        if (lista.isEmpty()) {
            PontoComercial erro = new PontoComercial();
            erro.setNumeroBox("Ponto(s) Comercial(is) não encontrado(s) ou ocupado(s).");
            lista.add(erro);
        }
        return lista;
    }

    public List<SelectItem> getParametros() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();

        if (parametroRendas == null || parametroRendas.getId() == null) {
            toReturn.add(new SelectItem(null, " "));
        } else {
            toReturn.add(new SelectItem(parametroRendas, parametroRendas.toString()));
        }

        return toReturn;
    }

    public String getTipoPessoa() {
        String tipo = "";
        if (locatario != null && locatario.getId() != null) {
            if (locatario instanceof PessoaFisica) {
                tipo = "Fisica";
            } else if (locatario instanceof PessoaJuridica) {
                tipo = "Juridica";
            }
        }
        return tipo;
    }

    public List<EnderecoCorreio> getEnderecos() {
        if (locatario == null || locatario.getId() == null) {
            return new ArrayList<EnderecoCorreio>();
        } else {
            return contratoRendasPatrimoniaisFacade.getEnderecoFacade().enderecoPorPessoa(locatario);
        }
    }

    public List<Telefone> getTelefones() {
        if (locatario == null || locatario.getId() == null) {
            return new ArrayList<Telefone>();
        } else {
            return contratoRendasPatrimoniaisFacade.getPessoaFacade().telefonePorPessoa(locatario);
        }
    }

    public void imprimeContrato() {
        try {
            contratoRendasPatrimoniaisFacade.geraDocumento(selecionado, getSistemaControlador());
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
        }
    }

    public void verificarImpressaoDam() {
        try {
            validarImpressaoDam();
            FacesUtil.atualizarComponente("formConfirmaDam");
            FacesUtil.executaJavaScript("confirmaDam.show()");
        } catch (ValidacaoException onpe) {
            FacesUtil.printAllFacesMessages(onpe.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }

    }

    private void validarImpressaoDam() {
        ValidacaoException ve = new ValidacaoException();
        if (parcelasSelecionadas.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos uma parcela para impressão do dam.");
        }
        ve.lancarException();
    }

    public void imprimeDam(boolean agrupado) {
        try {
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            parcelasSelecionadas.sort(ResultadoParcela.getComparatorByValuePadrao());
            if (agrupado) {
                DAM dam = contratoRendasPatrimoniaisFacade.getDamFacade().gerarDAMCompostoViaApi(contratoRendasPatrimoniaisFacade.getSistemaFacade().getUsuarioCorrente(),
                    parcelasSelecionadas, calcularVencimentoDam());
                imprimeDAM.imprimirDamCompostoViaApi(dam);
            } else {
                String[] contrato;
                if (selecionado.getNumeroContrato().contains("-")) {
                    contrato = selecionado.getNumeroContrato().split("-");
                } else {
                    contrato = new String[]{"0"};
                }
                imprimeDAM.adicionarParametro("INSCRICAO_CONTRIBUINTE", selecionado.getNumeroContrato());
                imprimeDAM.adicionarParametro("RENOVACAO", contrato[contrato.length - 1]);
                List<DAM> dams = Lists.newArrayList();
                for (ResultadoParcela rp : parcelasSelecionadas) {
                    DAM dam = contratoRendasPatrimoniaisFacade.getDamFacade().gerarDAMUnicoViaApi(contratoRendasPatrimoniaisFacade.getSistemaFacade().getUsuarioCorrente(), rp);
                    dams.add(dam);
                }
                imprimeDAM.imprimirDamUnicoViaApi(dams);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ResourceAccessException rae) {
            logger.error("Erro ao acessar a api do dam.", rae);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível se conectar a api para geração do DAM.");
        } catch (Exception e) {
            logger.error("Erro ao imprimir o Dam Individual: {}", e);
            FacesUtil.addError("Não foi possível imprimir o DAM", "Ocorreu um problema no servidor, tente de novo, se o problema persistir entre em contato com o suporte");
        }
    }

    public void chamaDialogRenovacaoContrato() {
        if (!podeGerenciarContrato()) return;
        if (contratoRendasPatrimoniaisFacade.getMoedaFacade().recuperaValorVigenteUFM().equals(BigDecimal.ZERO)) {
            FacesUtil.addError("ATENÇÃO!", "Não existe UFM cadastrada para o exercício corrente.");
            return;
        }
        if (SituacaoContratoRendasPatrimoniais.ATIVO.equals(selecionado.getSituacaoContrato()) && selecionado.getDataFim().compareTo(getSistemaControlador().getDataOperacao()) >= 1) {
            FacesUtil.addError("ATENÇÃO!", "Não é possível renovar contrato que ainda está vigente.");
            return;
        }
        if (recuperaParametroRendas(getSistemaControlador().getDataOperacao()) == null) {
            FacesUtil.addOperacaoNaoPermitida("Parâmetro de Rendas Patrimoniais não encontrado! Por favor, verifique!");
            return;
        }

        if (SituacaoContratoRendasPatrimoniais.ENCERRADO.equals(selecionado.getSituacaoContrato()) ||
            SituacaoContratoRendasPatrimoniais.RENOVADO.equals(selecionado.getSituacaoContrato()) ||
            SituacaoContratoRendasPatrimoniais.ALTERADO.equals(selecionado.getSituacaoContrato()) ||
            SituacaoContratoRendasPatrimoniais.RESCINDIDO.equals(selecionado.getSituacaoContrato())) {
            FacesUtil.addError("ATENÇÃO!", "Não é possível renovar contrato que não é ATIVO.");
        } else {
            criarNovoContratoRenovado();
            calculaParcelas();
            FacesUtil.atualizarComponente("formRenovacao");
            FacesUtil.executaJavaScript("widgetDialogRenovacao.show()");
        }
    }

    private void criarNovoContratoRenovado() {
        contratoRenovado = new ContratoRendasPatrimoniais();
        ParametroRendas parametroRendasRenovacao = recuperaParametroRendas(getSistemaControlador().getDataOperacao());

        contratoRenovado.setOriginario(selecionado);
        String numeroSequencia = contratoRendasPatrimoniaisFacade.getSequenciaRenovacaoContrato(selecionado);
        String[] numeroContrato = selecionado.getNumeroContrato().split("-");
        contratoRenovado.setNumeroContrato(numeroContrato[0] + "-" + (numeroSequencia));
        contratoRenovado.setSequenciaContrato(contratoRendasPatrimoniaisFacade.getSequenciaContratoPorLocatario(selecionado));
        contratoRenovado.setDataInicio(parametroRendasRenovacao.getDataInicioContrato());
        contratoRenovado.setDiaVencimento(parametroRendasRenovacao.getDiaVencimentoParcelas().intValue());
        contratoRenovado.setLocatario(selecionado.getLocatario());
        contratoRenovado.setPeriodoVigencia(contratoRendasPatrimoniaisFacade.mesVigente(contratoRenovado.getDataInicio(), parametroRendasRenovacao));
        contratoRenovado.setLotacaoVistoriadora(selecionado.getLotacaoVistoriadora());
        contratoRenovado.setAtividadePonto(selecionado.getAtividadePonto());
        contratoRenovado.setIndiceEconomico((selecionado.getIndiceEconomico()));
        contratoRenovado.setDataFim(parametroRendasRenovacao.getDataFimContrato());
        contratoRenovado.setValorUfmDoContrato(contratoRendasPatrimoniaisFacade.getMoedaFacade()
            .buscarValorUFMParaAno(getSistemaControlador().getExercicioCorrente().getAno()));
        contratoRenovado.setDataOperacao(getSistemaControlador().getDataOperacao());
        copiarListaDeCnae(contratoRenovado);
        selecionado.setUsuarioOperacao(contratoRendasPatrimoniaisFacade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setDataOperacao(new Date());
        copiarPontosComerciais(selecionado, contratoRenovado);
        copiarPontosComerciais(selecionado, contratoRenovado);
        contratoRenovado.setValorPorMesRS(contratoRenovado.getValorPorMesUFM()
            .multiply(contratoRenovado.getValorUfmDoContrato()));
        contratoRenovado.setValorTotalUFM(contratoRenovado.getValorPorMesUFM()
            .multiply(new BigDecimal(contratoRenovado.getPeriodoVigencia())));
        contratoRenovado.setSituacaoContrato(SituacaoContratoRendasPatrimoniais.ATIVO);
        contratoRenovado.setTipoUtilizacao(selecionado.getTipoUtilizacao());
        contratoRenovado.setUsuarioInclusao(selecionado.getUsuarioInclusao());
        contratoRenovado.setQuantidadeParcelas(contratoRendasPatrimoniaisFacade.mesVigente(contratoRenovado.getDataInicio(), parametroRendasRenovacao));

        parametroRendas = parametroRendasRenovacao;
        FacesUtil.atualizarComponente("dialogRenovacao");
    }

    private void copiarPontosComerciais(ContratoRendasPatrimoniais contrato, ContratoRendasPatrimoniais contratoRenovado) {
        List<PontoComercialContratoRendasPatrimoniais> listaDePontosComercialRendasPatrimoniais = contratoRendasPatrimoniaisFacade.recuperarPontoDoContrato(contrato);
        for (PontoComercialContratoRendasPatrimoniais p : listaDePontosComercialRendasPatrimoniais) {
            PontoComercialContratoRendasPatrimoniais pontoNovo = new PontoComercialContratoRendasPatrimoniais();
            pontoNovo.setPontoComercial(p.getPontoComercial());
            pontoNovo.setArea(p.getPontoComercial().getArea());
            pontoNovo.setValorTotalContrato(p.getValorTotalContrato());
            pontoNovo.setContratoRendasPatrimoniais(contratoRenovado);

            pontoNovo.setValorMetroQuadrado(p.getPontoComercial().getValorMetroQuadrado());
            pontoNovo.setValorCalculadoMes(p.getPontoComercial().getArea().multiply(pontoNovo.getValorMetroQuadrado()));
            BigDecimal valor = pontoNovo.getValorCalculadoMes().multiply(BigDecimal.valueOf(contratoRenovado.getPeriodoVigencia()));
            pontoNovo.setValorTotalContrato(valor);

            contratoRenovado.setValorPorMesUFM(contratoRenovado.getValorPorMesUFM().add(pontoNovo.getValorCalculadoMes()));
            contratoRenovado.getPontoComercialContratoRendasPatrimoniais().add(pontoNovo);
        }
    }

    private void copiarListaDeCnae(ContratoRendasPatrimoniais contrato) {
        if (!selecionado.getContratoRendaCNAEs().isEmpty()) {
            for (ContratoRendaCNAE cnae : selecionado.getContratoRendaCNAEs()) {
                ContratoRendaCNAE contratoCnae = new ContratoRendaCNAE();
                contratoCnae.setCnae(cnae.getCnae());
                contratoCnae.setContratoRendasPatrimoniais(contrato);
                contrato.getContratoRendaCNAEs().add(contratoCnae);
            }
        }
    }

    private void validarRenovacaoContrato() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getPontoComercialContratoRendasPatrimoniais() == null || selecionado.getPontoComercialContratoRendasPatrimoniais().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O contrato não possui ponto comercial adicionado, não é possível continuar com a renovação.");
        }
        if ("".equals(selecionado.getMotivoOperacao().trim())) {
            ve.adicionarMensagemDeCampoObrigatorio("O motivo da operação é obrigatório.");
        }
        if (contratoRenovado.getQuantidadeParcelas() <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O contrato não possui parcelas para renovação!");
        }
        ve.lancarException();
    }

    public void renovarContrato() {
        try {
            validarRenovacaoContrato();

            selecionado.setSituacaoContrato(SituacaoContratoRendasPatrimoniais.RENOVADO);
            contratoRendasPatrimoniaisFacade.salvar(selecionado);
            contratoRenovado = contratoRendasPatrimoniaisFacade.salvarContrato(contratoRenovado);
            try {
                contratoRendasPatrimoniaisFacade.gerarDebito(contratoRenovado, contratoRendasPatrimoniaisFacade.getSistemaFacade().getExercicioCorrente(), BigDecimal.ZERO, null);
            } catch (Exception e) {
                logger.debug("Erro ao gerar os débitos: {}", e);
            }
            selecionado = contratoRenovado;
            efetivouContrato = isContratoEfetivado();
            FacesUtil.addInfo("Contrato renovado com sucesso!", "");
            RequestContext.getCurrentInstance().execute("widgetDialogRenovacao.hide()");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public ConverterGenerico getConverterAtividadePonto() {
        if (converterAtividadePonto == null) {
            converterAtividadePonto = new ConverterGenerico(AtividadePonto.class, contratoRendasPatrimoniaisFacade.getAtividadeFacade());
        }
        return converterAtividadePonto;
    }

    public List<SelectItem> getAtividadePontos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (AtividadePonto a : contratoRendasPatrimoniaisFacade.getAtividadeFacade().listaOrdenando("obj.descricao")) {
            toReturn.add(new SelectItem(a, a.getDescricao()));
        }
        return toReturn;
    }

    public BigDecimal retornaValorParcela() {
        this.valorDividaContrato = contratoRendasPatrimoniaisFacade.recuperaValordivida(this.valorDividaContrato);
        return this.valorDividaContrato.getParcelaValorDividas().get(0).getValor();
    }

    public BigDecimal retornaValorTotalContrato() {
        return this.valorDividaContrato.getValor();
    }

    public void efetivaContrato() {
        if (qtdeDeParcelasEhPermitido()) {
            if (validaEfetivacao()) {
                selecionado.setSituacaoContrato(SituacaoContratoRendasPatrimoniais.ATIVO);
                contratoEfetivado = true;
                salvar();
                RequestContext.getCurrentInstance().execute("widgetDialogEfetivacao.hide()");
                FacesUtil.redirecionamentoInterno("/contrato-de-rendas-patrimoniais/ver/" + selecionado.getId());
            }
        }
    }

    public boolean qtdeDeParcelasEhPermitido() {
        if (selecionado.getQuantidadeParcelas() <= 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "A quantidade de parcelas deve ser maior que zero!");
            return false;
        }
        return true;
    }

    public void chamaDialogEfetivacao() {
        if (validaCamposControlador()) {
            selecionado.setLocatario(locatario);
            selecionado.setIndiceEconomico(parametroRendas.getIndiceEconomico());
            selecionado.setDiaVencimento(parametroRendas.getDiaVencimentoParcelas().intValue());
            selecionado.setTipoUtilizacao(selecionado.getPontoComercialContratoRendasPatrimoniais().get(0).getPontoComercial().getLocalizacao().getTipoOcupacaoLocalizacao());
            selecionado.setQuantidadeParcelas(selecionado.getPeriodoVigencia());
            if (validaCampos()) {
                FacesUtil.atualizarComponente("formEfetivacao");
                FacesUtil.executaJavaScript("widgetDialogEfetivacao.show()");
            }
        }
    }

    public SituacaoContratoRendasPatrimoniais getSituacaoDoContratoOrinario(ContratoRendasPatrimoniais contrato) {
        if (contrato != null) {
            return contrato.getSituacaoContrato();
        }
        return null;
    }

    public void navegarParaProximoContrato() {
        Web.navegacao("", "/contrato-de-rendas-patrimoniais/ver/" + selecionado.getOriginario().getId() + "/");
    }

    public boolean validaCamposControlador() {
        boolean resultado = true;
        if (parametroRendas == null) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "Não existe um parâmetro vigente cadastrado.");
        }
        if (locatario == null) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe o locatário.");
        }
        if (selecionado.getDataInicio() == null) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe a data de início.");
        }
        if (this.localizacao == null) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe a localização.");
        }
        if (this.locatarioJaPossuiContrato) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "O locatário informado já possui um contrato ativo.");
        }
        if (selecionado.getAtividadePonto() == null) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe a atividade do ponto comercial.");
        }
        if (selecionado.getPontoComercialContratoRendasPatrimoniais().isEmpty()) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe ao menos um ponto comercial.");
        }
        return resultado;
    }

    public List<ParcelaContratoRendaPatrimonial> getSimulacaoParcelas() {
        List<ParcelaContratoRendaPatrimonial> lista = new ArrayList<ParcelaContratoRendaPatrimonial>();
        ParcelaContratoRendaPatrimonial parcela = null;
        if (parametroRendas != null) {
            if (validarQuantidadeParcela(selecionado)) {
                int qtdeParcelas = 0;

                if (selecionado.getQuantidadeParcelas() != null && selecionado.getQuantidadeParcelas() > 0) {
                    qtdeParcelas = selecionado.getQuantidadeParcelas();
                } else if (parametroRendas.getQuantidadeParcelas() != null) {
                    qtdeParcelas = parametroRendas.getQuantidadeParcelas();
                }

                if (selecionado.getDiaVencimento() != null && qtdeParcelas != 0) {
                    Calendar vencimento = selecionado.getPrimeiroVencimentoContrato(contratoRendasPatrimoniaisFacade.getFeriadoFacade());

                    BigDecimal valor = getSomaDoValorTotalContratoMultiplicado();
                    BigDecimal valorParcela = valor.divide(new BigDecimal(qtdeParcelas), 2, BigDecimal.ROUND_DOWN);
                    BigDecimal somaParcelas = valorParcela.multiply(new BigDecimal(qtdeParcelas));
                    BigDecimal primeiraParcela = valor.subtract(somaParcelas);
                    primeiraParcela = primeiraParcela.add(valorParcela);

                    for (int i = 1; i <= qtdeParcelas; i++) {
                        parcela = new ParcelaContratoRendaPatrimonial();
                        parcela.setParcela(i);
                        parcela.setVencimento(vencimento.getTime());
                        if (i == 1) {
                            parcela.setValor(primeiraParcela);
                        } else {
                            parcela.setValor(valorParcela);
                        }
                        lista.add(parcela);
                        selecionado.proximoVencimento(contratoRendasPatrimoniaisFacade.getFeriadoFacade(), vencimento);
                    }
                }
            }
        }
        return lista;
    }

    private boolean usuarioTemPermissao() {
        return contratoRendasPatrimoniaisFacade.getUsuarioSistemaFacade().validaAutorizacaoUsuario(getSistemaControlador().getUsuarioCorrente(), AutorizacaoTributario.PERMITIR_CADASTRAR_NOVOS_CONTRATOS_DE_RENDAS_PATRIMONIAIS);
    }

    public void calculaParcelas() {
        parcelasRenovacao = new ArrayList<>();
        if (contratoRenovado == null || contratoRenovado.getQuantidadeParcelas() == null || contratoRenovado.getQuantidadeParcelas() <= 0) {
        } else {
            if (contratoRenovado.getQuantidadeParcelas() > contratoRenovado.getPeriodoVigencia()) {
                FacesUtil.addError("Não foi possivel continuar!", "A parcela não pode exceder o período de vigência do contrato.");
            } else {
                ParcelaContratoRendaPatrimonial parcela;
                if (contratoRenovado.getDiaVencimento() != null && contratoRenovado.getQuantidadeParcelas() != null) {
                    Calendar vencimento = contratoRenovado.getPrimeiroVencimentoContrato(contratoRendasPatrimoniaisFacade.getFeriadoFacade());

                    BigDecimal valor = getSomaDoValorTotalContratoMultiplicadoRenovado();
                    BigDecimal valorparcela = valor.divide(new BigDecimal(contratoRenovado.getQuantidadeParcelas()), 2, BigDecimal.ROUND_DOWN);
                    BigDecimal somaparcela = valorparcela.multiply(new BigDecimal(contratoRenovado.getQuantidadeParcelas()));
                    BigDecimal primeiraparcela = valor.subtract(somaparcela);
                    primeiraparcela = primeiraparcela.add(valorparcela);
                    for (int i = 1; i <= contratoRenovado.getQuantidadeParcelas(); i++) {
                        parcela = new ParcelaContratoRendaPatrimonial();
                        parcela.setParcela(i);
                        parcela.setVencimento(vencimento.getTime());
                        if (i == 1) {
                            parcela.setValor(primeiraparcela);
                        } else {
                            parcela.setValor(valorparcela);
                        }
                        contratoRenovado.proximoVencimento(contratoRendasPatrimoniaisFacade.getFeriadoFacade(), vencimento);
                        parcelasRenovacao.add(parcela);
                    }
                }
            }
        }
    }

    public boolean isDesabilitaRenovarContrato() {
        if (contratoRenovado == null) {
        } else {
            if (contratoRenovado.getQuantidadeParcelas() == null || contratoRenovado.getQuantidadeParcelas() <= 0 || contratoRenovado.getQuantidadeParcelas() > contratoRenovado.getPeriodoVigencia()) {
                return true;
            }
        }
        return false;
    }

    public List<ParcelaContratoRendaPatrimonial> getSimulacaoParcelasRenovacao() {
        return parcelasRenovacao;
    }

    private boolean validaEfetivacao() {
        boolean resultado = true;
        if (selecionado.getQuantidadeParcelas() == null) {
            FacesUtil.addError("Não foi possível continuar!", "Informe a quantidade de parcelas.");
            resultado = false;
        } else if (selecionado.getQuantidadeParcelas().intValue() <= 0) {
            FacesUtil.addError("Não foi possível continuar!", "Informe a quantidade de parcelas.");
            resultado = false;
        } else if (this.parametroRendas.getQuantidadeParcelas() == null) {
            FacesUtil.addError("Não foi possível continuar!", "O Parâmetro da lotação deve conter a quantidade de parcelas informada.");
            resultado = false;
        } else if (selecionado.getQuantidadeParcelas().intValue() > this.parametroRendas.getQuantidadeParcelas().intValue()) {
            FacesUtil.addError("Não foi possível continuar!", "A quantidade de parcelas deve ser menor ou igual a quantidade de parcelas do parâmetro da lotação informada.");
            resultado = false;
        } else if (selecionado.getQuantidadeParcelas() > parametroRendas.getQuantidadeParcelas()) {
            FacesUtil.addError("Não foi possivel continuar!", "A parcela não pode ser superior ao definido no parâmetro.");
            resultado = false;
        }
        return resultado;
    }

    public List<LotacaoVistoriadora> getLotacaoVistoriadoras() {
        return contratoRendasPatrimoniaisFacade.getLotacaoVistoriadoraFacade().listaPorUsuarioSistema(selecionado.getUsuarioInclusao());
    }

    public List<LotacaoVistoriadora> completaLotacaoVistoriadora(String parte) {
        List<LotacaoVistoriadora> lista = new ArrayList<LotacaoVistoriadora>();
        parte = parte.trim().toLowerCase();

        for (LotacaoVistoriadora l : getLotacaoVistoriadoras()) {
            if ((l.getCodigo().toString().contains(parte)) || (l.getDescricao().toLowerCase().trim().contains(parte))) {
                lista.add(l);
            }
        }
        return lista;
    }

    public Converter getConverterLotacaoVistoriadora() {
        if (converterLotacaoVistoriadora == null) {
            converterLotacaoVistoriadora = new ConverterAutoComplete(LotacaoVistoriadora.class, contratoRendasPatrimoniaisFacade.getLotacaoVistoriadoraFacade());
        }
        return converterLotacaoVistoriadora;
    }

    public boolean isListaLotacaoMaiorQueUm() {
        return getLotacaoVistoriadoras().size() > 1;
    }

    public void sugereParametroRendas() {
        this.parametroRendas = recuperaParametroRendas(getSistemaControlador().getDataOperacao());
        if (parametroRendas != null) {
            if (selecionado.getQuantidadeParcelas() == null) {
                selecionado.setQuantidadeParcelas(selecionado.getPeriodoVigencia());
            }
            selecionado.setIndiceEconomico(this.parametroRendas.getIndiceEconomico());
        }
    }

    public PontoComercialContratoRendasPatrimoniais getPontoComercialContratoRendasPatrimoniais() {
        return pontoComercialContratoRendasPatrimoniais;
    }

    public void setPontoComercialContratoRendasPatrimoniais(PontoComercialContratoRendasPatrimoniais pontoComercialContratoRendasPatrimoniais) {
        this.pontoComercialContratoRendasPatrimoniais = pontoComercialContratoRendasPatrimoniais;
    }

    public void adicionaPontoComercial() {
        if (validarPontoComercial()) return;
        //valor total do contrato (área do box X valor do metro quadrado).
        BigDecimal valorCalculadoMes = pontoComercialContratoRendasPatrimoniais.getPontoComercial()
            .getArea().multiply(pontoComercialContratoRendasPatrimoniais.getPontoComercial().getValorMetroQuadrado());

        pontoComercialContratoRendasPatrimoniais.setValorCalculadoMes(valorCalculadoMes);
        BigDecimal valor;
        try {
            valor = valorCalculadoMes.multiply(BigDecimal.valueOf(selecionado.getPeriodoVigencia()));
        } catch (Exception e) {
            Double calculo = valorCalculadoMes.doubleValue() * parametroRendas.getQuantidadeMesesVigencia();
            valor = new BigDecimal(calculo);
        }

        pontoComercialContratoRendasPatrimoniais.setValorTotalContrato(valor);
        pontoComercialContratoRendasPatrimoniais.setContratoRendasPatrimoniais(selecionado);

        pontoComercialContratoRendasPatrimoniais.setArea(pontoComercialContratoRendasPatrimoniais.getPontoComercial().getArea());
        pontoComercialContratoRendasPatrimoniais.setValorMetroQuadrado(pontoComercialContratoRendasPatrimoniais
            .getPontoComercial().getValorMetroQuadrado());

        selecionado.getPontoComercialContratoRendasPatrimoniais().add(pontoComercialContratoRendasPatrimoniais);
        pontoComercialContratoRendasPatrimoniais = new PontoComercialContratoRendasPatrimoniais();
    }

    private boolean validarPontoComercial() {
        if (localizacao == null) {
            FacesUtil.addError("Não foi possível continuar!", "Selecione a localização!.");
            return true;
        }
        if (pontoComercialContratoRendasPatrimoniais.getPontoComercial() != null) {
            if (pontoComercialContratoRendasPatrimoniais.getPontoComercial().getValorMetroQuadrado().compareTo(BigDecimal.ZERO) <= 0) {
                FacesUtil.addError("Não foi possível continuar!", "O ponto comercial possui Valor do M² menor ou igual a Zero.");
                return true;
            }
            if (pontoComercialContratoRendasPatrimoniais.getPontoComercial().getArea() == null) {
                FacesUtil.addError("Não foi possível continuar!", "O ponto comercial não possui Área (M²) cadastrado.");
                return true;
            } else if (pontoComercialContratoRendasPatrimoniais.getPontoComercial().getArea().compareTo(BigDecimal.ZERO) <= 0) {
                FacesUtil.addError("Não foi possível continuar!", "O ponto comercial possui Área (M²) menor ou igual a Zero.");
                return true;
            }
        }

        if (parametroRendas == null || parametroRendas.getId() == null) {
            FacesUtil.addError("Não foi possível continuar!", "Para adicionar um Ponto Comercial, o parâmetro deve ser selecionado.");
            return true;
        } else if (pontoComercialContratoRendasPatrimoniais.getPontoComercial() == null || pontoComercialContratoRendasPatrimoniais.getPontoComercial().getId() == null) {
            FacesUtil.addError("Não foi possível continuar!", "Selecione o Ponto Comercial desejado.");
            return true;
        } else if (contratoRendasPatrimoniaisFacade.hasPontosEmOutroContratoVigenteRendasPatrimoniais(pontoComercialContratoRendasPatrimoniais.getPontoComercial(), selecionado)) {
            FacesUtil.addError("Não foi possível continuar!", "Existe Box que pertence a outro contrato vigente.");
            return true;
        } else {
            for (PontoComercialContratoRendasPatrimoniais p : selecionado.getPontoComercialContratoRendasPatrimoniais()) {
                if (p.getPontoComercial().equals(pontoComercialContratoRendasPatrimoniais.getPontoComercial())) {
                    FacesUtil.addError("Não foi possível continuar!", "Ponto Comercial já selecionado.");
                    return true;
                }
            }
        }
        return false;
    }

    public void removePontoComercial(ActionEvent e) {
        PontoComercialContratoRendasPatrimoniais p = (PontoComercialContratoRendasPatrimoniais) e.getComponent().getAttributes().get("objeto");
        p.setContratoRendasPatrimoniais(null);
        selecionado.getPontoComercialContratoRendasPatrimoniais().remove(p);
    }

    public ContratoRendasPatrimoniais getContratoRenovado() {
        return contratoRenovado;
    }

    public void setContratoRenovado(ContratoRendasPatrimoniais contratoRenovado) {
        this.contratoRenovado = contratoRenovado;
    }

    public boolean isContratoEfetivado() {
        return (SituacaoContratoRendasPatrimoniais.ATIVO.equals(selecionado.getSituacaoContrato())) || (SituacaoContratoRendasPatrimoniais.RENOVADO.equals(selecionado.getSituacaoContrato()));
    }

    public void setContratoEfetivado(Boolean contratoEfetivado) {
        this.contratoEfetivado = contratoEfetivado;
    }

    public BigDecimal getMultiplicaPeloUFMDoContrato(BigDecimal valor) {
        try {
            return valor.multiply(selecionado.getValorUfmDoContrato()).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getMultiplicaPeloUFMAtual(BigDecimal valor) {
        try {
            BigDecimal ufm = contratoRendasPatrimoniaisFacade.getMoedaFacade().recuperaValorUFMParaData(getSistemaControlador().getDataOperacao());
            return valor.multiply(ufm).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getMultiplicaPeloUFMContratoRenovado(BigDecimal valor) {
        try {
            BigDecimal ufm = contratoRendasPatrimoniaisFacade.getMoedaFacade().recuperaValorUFMParaData(contratoRenovado.getDataInicio());
            return valor.multiply(ufm).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getSomaDoValorTotalContratoMultiplicado() {
        try {
            return getMultiplicaPeloUFMDoContrato(selecionado.getSomaDoValorTotalContrato());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getSomaDoValorTotalContratoMultiplicadoAlterado() {
        try {
            return getMultiplicaPeloUFMAtual(contratoAlterado.getSomaDoValorTotalContrato());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getSomaDoValorTotalContratoMultiplicadoRenovado() {
        try {
            return getMultiplicaPeloUFMContratoRenovado(contratoRenovado.getValorTotalUFM());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getSomaDoValorCalculadoMesMultiplicado() {
        try {
            return getMultiplicaPeloUFMDoContrato(selecionado.getSomaDoValorCalculadoMes());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getSomaDoValorCalculadoMesMultiplicadoContratoAlterado() {
        try {
            return getMultiplicaPeloUFMAtual(contratoAlterado.getSomaDoValorCalculadoMes());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getSomaDoValorCalculadoMesMultiplicadoContratoRenovado() {
        try {
            return getMultiplicaPeloUFMContratoRenovado(selecionado.getSomaDoValorCalculadoMes());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorDoCalculoDoContrato() {
        return valorDoCalculoDoContrato != null ? valorDoCalculoDoContrato : BigDecimal.ZERO;
    }

    public void setValorDoCalculoDoContrato(BigDecimal valorDoCalculoDoContrato) {
        this.valorDoCalculoDoContrato = valorDoCalculoDoContrato;
    }

    public Integer getQuantidadeParcelas() {
        try {
            if (selecionado.getQuantidadeParcelas() != null) {
                return selecionado.getQuantidadeParcelas();
            } else {
                return parametroRendas.getQuantidadeParcelas();
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public void atualizaCamposPessoa() {
        if (locatario != null) {
            locatarioJaPossuiContrato = contratoRendasPatrimoniaisFacade.existeContratoVigenteDoLocatario(locatario);
            if (locatarioJaPossuiContrato) {
                FacesUtil.addError("Atenção!", "Esse locatário já possui um contrato vigente!");
            }
        }
        FacesUtil.atualizarComponente("Formulario");
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public ParametroRendas recuperaParametroRendas(Date dataOperacao) {
        try {
            LotacaoVistoriadora lotacao = contratoRendasPatrimoniaisFacade.getLotacaoVistoriadoraFacade().recuperar(selecionado.getLotacaoVistoriadora().getId());
            Calendar cal = Calendar.getInstance();
            cal.setTime(dataOperacao);
            Exercicio exercicio = contratoRendasPatrimoniaisFacade.getParametroRendasPatrimoniaisFacade().getExercicioFacade().getExercicioPorAno(cal.get(Calendar.YEAR));
            return contratoRendasPatrimoniaisFacade.getParametroRendasPatrimoniaisFacade().recuperaParametroRendasPorExercicioLotacaoVistoriadora(exercicio, lotacao);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean ativaOuDesativaAutomatico() {
        if (selecionado != null || selecionado.getNumeroContrato() == null) {
            return true;
        }
        return false;
    }

    public void visualizaDialogErro() {
        FacesUtil.addError("Aviso", "Não existe um parâmetro de rendas patrimoniais vigente cadastrado, verifique!");
    }

    public boolean desativaQuandoEncerrado() {
        return SituacaoContratoRendasPatrimoniais.ENCERRADO.equals(selecionado.getSituacaoContrato());
    }

    public boolean validarQuantidadeParcela(ContratoRendasPatrimoniais contrato) {
        return (contrato != null && contrato.getQuantidadeParcelas() != null && contrato.getQuantidadeParcelas() <= contrato.getPeriodoVigencia() && contrato.getQuantidadeParcelas() > 0);
    }

    public List<CNAE> completaCNAE(String parte) {
        return contratoRendasPatrimoniaisFacade.getcNAEFacade().listaFiltrando(parte.trim(), "descricaoDetalhada", "codigoCnae");
    }

    public Converter getConverterCNAE() {
        if (converterCnae == null) {
            converterCnae = new ConverterAutoComplete(CNAE.class, contratoRendasPatrimoniaisFacade.getcNAEFacade());
        }
        return converterCnae;
    }

    public void adicionaCNAE() {
        if (validaCnae(contratoRendasCNAE)) {
            contratoRendasCNAE.setContratoRendasPatrimoniais(selecionado);
            selecionado.getContratoRendaCNAEs().add(contratoRendasCNAE);
            contratoRendasCNAE = new ContratoRendaCNAE();
        }
    }

    private boolean validaCnae(ContratoRendaCNAE rendaCNAE) {
        boolean validacao = true;
        if (rendaCNAE.getCnae() == null) {
            FacesUtil.addError("Impossível continuar", "Informe o CNAE");
            return false;
        }
        for (ContratoRendaCNAE listacnae : selecionado.getContratoRendaCNAEs()) {
            if (listacnae.getCnae().equals(rendaCNAE.getCnae())) {
                FacesUtil.addError("Impossível continuar", "O CNAE já foi selecionado!");
                return false;
            }
        }
        return true;
    }

    public void removeCnae(ActionEvent e) {
        contratoRendasCNAE = (ContratoRendaCNAE) e.getComponent().getAttributes().get("objeto");
        selecionado.getContratoRendaCNAEs().remove(contratoRendasCNAE);
        contratoRendasCNAE = new ContratoRendaCNAE();
    }

    public void voltaParaLista() {
        redireciona();
    }

    public void poeNaSessao() throws IllegalAccessException {
        Web.poeTodosFieldsNaSessao(this);
    }

    public void rescindirContrato() {
        if (!podeGerenciarContrato()) return;
        selecionado.setTipoOperacao("Rescisão");
        if (validarRescisaoDeContrato()) {
            recuperaParcelasDoCalculo();
            if (temParcelaVencida()) {
                FacesUtil.executaJavaScript("dialogRescindirParcela.show()");
            } else {
                chamaDialogRescisao();
            }
        }
    }

    private boolean validarRescisaoDeContrato() {
        if (selecionado.getSituacaoContrato() != SituacaoContratoRendasPatrimoniais.ATIVO) {
            FacesUtil.addError("Atenção!", "Somente contratos ATIVOS podem ser rescindidos.");
            return false;
        }
        return true;
    }

    private void recuperaParcelasDoCalculo() {
        CalculoRendas calculo = contratoRendasPatrimoniaisFacade.recuperaCalculoRendas(selecionado);
        ConsultaParcela consulta = new ConsultaParcela();
        if (calculo != null) {
            valorDoCalculoDoContrato = calculo.getValorReal();
            parcelas = consulta.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculo.getId()).executaConsulta().getResultados();
            Collections.sort(parcelas, new OrdenaResultadoParcelaPorVencimento());
        } else {
            valorDoCalculoDoContrato = BigDecimal.ZERO;
            parcelas = Lists.newArrayList();
        }
    }

    private boolean temParcelaVencida() {
        for (ResultadoParcela parcela : parcelas) {
            if (parcela.getVencimento().before(getSistemaControlador().getDataOperacao())) {
                return true;
            }
        }
        return false;
    }

    public void chamaDialogRescisao() {
        selecionado.setDataOperacao(new Date());
        FacesUtil.atualizarComponente("confirmacaoRescisao");
        FacesUtil.executaJavaScript("dialogRescindirParcela.hide()");
        FacesUtil.executaJavaScript("dialogConfirmacaoRescisao.show()");
    }

    public void confirmarRescisaoDoContrato() {
        if (!selecionado.getMotivoOperacao().trim().equals("")) {
            selecionado.setUsuarioOperacao(contratoRendasPatrimoniaisFacade.getSistemaFacade().getUsuarioCorrente());
            selecionado.setSituacaoContrato(SituacaoContratoRendasPatrimoniais.RESCINDIDO);
            cancelarDamEParcelaNaoVencidas();
            contratoRendasPatrimoniaisFacade.salvar(selecionado);
            FacesUtil.executaJavaScript("dialogConfirmacaoRescisao.hide()");
            FacesUtil.redirecionamentoInterno("/contrato-de-rendas-patrimoniais/ver/" + selecionado.getId() + "/");
            FacesUtil.addInfo("Contrato rescindido com sucesso!", "");
        } else {
            FacesUtil.addError("Atenção!", "O motivo da rescisão do contrato é obrigatório.");
        }
    }

    public void cancelarDamEParcelaNaoVencidas() {
        for (ResultadoParcela parcela : parcelas) {
            if (SituacaoParcela.EM_ABERTO.equals(parcela.getSituacaoEnumValue())) {
                ParcelaValorDivida pvd = contratoRendasPatrimoniaisFacade.recuperaParcelaValorDivida(parcela.getIdParcela());
                contratoRendasPatrimoniaisFacade.getDamFacade().cancelarDamsDaParcela(pvd, contratoRendasPatrimoniaisFacade.getSistemaFacade().getUsuarioCorrente());
                contratoRendasPatrimoniaisFacade.cancelarParcela(pvd, parcela.getValorTotal());
            }
        }
    }

    public void encerrarContrato() {
        if (!podeGerenciarContrato()) return;
        selecionado.setTipoOperacao("Encerramento");
        if (podeEncerrarContrato()) {
            recuperaParcelasDoCalculo();
            if (temParcelaVencida()) {
                FacesUtil.executaJavaScript("dialogEncerramentoParcela.show()");
            } else {
                chamaDialogRescisao();
            }
        }
    }

    public boolean podeEncerrarContrato() {
        boolean valida = true;
        if (selecionado.getSituacaoContrato() != SituacaoContratoRendasPatrimoniais.ATIVO) {
            valida = false;
            FacesUtil.addError("Atenção!", " Somente contratos ATIVOS podem ser encerrados.");
        }
        if (getSistemaControlador().getDataOperacao().before(selecionado.getDataFim())) {
            valida = false;
            FacesUtil.addError("Atenção", " A data de término do contrato ainda não ocorreu e não é possível encerrá-lo. Se for o caso, proceda a rescisão do contrato.");

        }
        return valida;
    }

    public void chamaDialogEncerramento() {
        selecionado.setDataOperacao(new Date());
        FacesUtil.atualizarComponente("confirmacaoEncerramento");
        FacesUtil.executaJavaScript("dialogEncerramentoParcela.hide()");
        FacesUtil.executaJavaScript("dialogConfirmacaoEncerramento.show()");
    }

    public void confirmarEncerramentoDoContrato() {
        if (!selecionado.getMotivoOperacao().trim().equals("")) {
            selecionado.setUsuarioOperacao(contratoRendasPatrimoniaisFacade.getSistemaFacade().getUsuarioCorrente());
            selecionado.setSituacaoContrato(SituacaoContratoRendasPatrimoniais.ENCERRADO);
            cancelarDamEParcelaNaoVencidas();
            contratoRendasPatrimoniaisFacade.salvar(selecionado);
            FacesUtil.executaJavaScript("dialogConfirmacaoEncerramento.hide()");
            FacesUtil.atualizarComponente("Formulario");
            FacesUtil.addInfo("Contrato encerrado com sucesso!", "");
        } else {
            FacesUtil.addError("Atenção!", "O motivo do encerramento do contrato é obrigatório.");
        }
    }

    public void chamaDialogAlteracaoContrato() {
        if (!podeGerenciarContrato()) return;
        if (podeAlterarContrato()) {
            selecionado.setUsuarioOperacao(contratoRendasPatrimoniaisFacade.getSistemaFacade().getUsuarioCorrente());
            selecionado.setDataOperacao(new Date());
            criarNovoContratoAlterado();
            FacesUtil.atualizarComponente("alteracaoContrato");
            FacesUtil.executaJavaScript("alteracaoContrato.show()");

        }
    }

    public void criarNovoContratoAlterado() {
        contratoAlterado = new ContratoRendasPatrimoniais();
        contratoAlterado.setNumeroContrato(selecionado.getNumeroContrato());
        contratoAlterado.setDataOperacao(getSistemaControlador().getDataOperacao());
        contratoAlterado.setDataInicio(selecionado.getDataInicio());
        contratoAlterado.setTipoUtilizacao(selecionado.getTipoUtilizacao());
        contratoAlterado.setDiaVencimento(parametroRendas.getDiaVencimentoParcelas().intValue());
        contratoAlterado.setPeriodoVigencia(contratoRendasPatrimoniaisFacade.mesVigente(contratoAlterado.getDataInicio(), parametroRendas));
        contratoAlterado.setSituacaoContrato(SituacaoContratoRendasPatrimoniais.ATIVO);
        contratoAlterado.setUsuarioInclusao(contratoRendasPatrimoniaisFacade.getSistemaFacade().getUsuarioCorrente());
        contratoAlterado.setSequenciaContrato(selecionado.getSequenciaContrato());
        contratoAlterado.setOriginario(selecionado);
        contratoAlterado.setAtividadePonto(selecionado.getAtividadePonto());
        contratoAlterado.setLotacaoVistoriadora(selecionado.getLotacaoVistoriadora());
        contratoAlterado.setIndiceEconomico(selecionado.getIndiceEconomico());
        contratoAlterado.setValorUfmDoContrato(contratoRendasPatrimoniaisFacade.getMoedaFacade().buscarValorUFMParaAno(getSistemaControlador().getExercicioCorrente().getAno()));
        contratoAlterado.setDataFim(this.parametroRendas.getDataFimContrato());
        contratoAlterado.setQuantidadeParcelas(contratoRendasPatrimoniaisFacade.mesVigente(contratoAlterado.getDataInicio(), parametroRendas));
        contratoAlterado.setLocatario(selecionado.getLocatario());
        if (!selecionado.getPontoComercialContratoRendasPatrimoniais().isEmpty()) {
            localizacao = selecionado.getPontoComercialContratoRendasPatrimoniais().get(0).getPontoComercial().getLocalizacao();
        }
        copiarPontosComerciais(selecionado, contratoAlterado);
        copiarListaDeCnae(contratoAlterado);
        FacesUtil.atualizarComponente("idAlteracaoContrato");
    }

    public boolean soAlterouPessoa() {
        if (contratoAlterado.getPontoComercialContratoRendasPatrimoniais().size() != selecionado.getPontoComercialContratoRendasPatrimoniais().size()) {
            return false;
        }
        if (!contratoAlterado.getAtividadePonto().getId().equals(selecionado.getAtividadePonto().getId())) {
            return false;
        }
        for (int i = 0; i < contratoAlterado.getPontoComercialContratoRendasPatrimoniais().size(); i++) {
            PontoComercialContratoRendasPatrimoniais pontoContratoAlterado = contratoAlterado.getPontoComercialContratoRendasPatrimoniais().get(i);
            PontoComercialContratoRendasPatrimoniais pontoContratoSelecionado = selecionado.getPontoComercialContratoRendasPatrimoniais().get(i);
            if (!pontoContratoAlterado.getPontoComercial().getLocalizacao().getId().equals(pontoContratoSelecionado.getPontoComercial().getLocalizacao().getId())) {
                return false;
            } else if (!pontoContratoAlterado.getPontoComercial().getId().equals(pontoContratoSelecionado.getPontoComercial().getId())) {
                return false;
            } else if (getMultiplicaPeloUFMDoContrato(pontoContratoAlterado.getValorTotalContrato()).compareTo(getMultiplicaPeloUFMDoContrato(pontoContratoSelecionado.getValorTotalContrato())) != 0) {
                return false;
            }
        }
        return !contratoAlterado.getLocatario().getId().equals(selecionado.getLocatario().getId());
    }

    public void confirmarAlteracaoDeContrato() {
        try {
            validarConfirmarAlteracaoDeContrato();
            mostraDemonstrativoDeParcelas = true;
            calcularSimulacaoParcelasAlteracaoContrato();
            if (getSaldoDoContrato().compareTo(BigDecimal.ZERO) <= 0) {
                FacesUtil.addAtencao("O contrato será gerado sem parcelas pois o saldo do contrato é zero!");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarAlteracaoDeContrato() {
        mostraDemonstrativoDeParcelas = false;
        contratoAlterado.getPontoComercialContratoRendasPatrimoniais().clear();
    }

    public void salvarAlteracaoContrato() {
        cancelarDamEParcelaNaoVencidas();
        selecionado.setSituacaoContrato(SituacaoContratoRendasPatrimoniais.ALTERADO);
        contratoRendasPatrimoniaisFacade.salvar(selecionado);
        contratoAlterado = contratoRendasPatrimoniaisFacade.salvarContrato(contratoAlterado);
        try {
            Date primeiroVencimento = contratoAlterado.getPrimeiroVencimentoContratoAlterado(getQuantidadeParcelasPagas(),
                contratoRendasPatrimoniaisFacade.getFeriadoFacade()).getTime();
            contratoRendasPatrimoniaisFacade.gerarDebito(contratoAlterado,
                contratoRendasPatrimoniaisFacade.getSistemaFacade().getExercicioCorrente(), getSaldoDoContrato(),
                primeiroVencimento);
            FacesUtil.executaJavaScript("alteracaoContrato.hide()");
            redireciona();
            FacesUtil.addOperacaoRealizada("Contrato Alterado com Sucesso!");
        } catch (Exception e) {
            logger.debug("Erro ao gerar os débitos: {}", e);
        }
    }

    public void removePontoComercialContratoAlterado(ActionEvent e) {
        PontoComercialContratoRendasPatrimoniais p = (PontoComercialContratoRendasPatrimoniais) e.getComponent().getAttributes().get("objeto");
        p.setContratoRendasPatrimoniais(null);
        contratoAlterado.getPontoComercialContratoRendasPatrimoniais().remove(p);
    }

    public List<ParcelaContratoRendaPatrimonial> getParcelasAlteracaoContrato() {
        return parcelasAlteracaoContrato;
    }

    public void setParcelasAlteracaoContrato(List<ParcelaContratoRendaPatrimonial> parcelasAlteracaoContrato) {
        this.parcelasAlteracaoContrato = parcelasAlteracaoContrato;
    }

    public void calcularSimulacaoParcelasAlteracaoContrato() {
        List<ParcelaContratoRendaPatrimonial> lista = Lists.newArrayList();
        Calendar vencimento = contratoAlterado.getPrimeiroVencimentoContratoAlterado(getQuantidadeParcelasPagas(),
            contratoRendasPatrimoniaisFacade.getFeriadoFacade());

        int qtdeParcelas = contratoAlterado.getPeriodoVigencia() - getQuantidadeParcelasPagas();
        if (contratoAlterado.getQuantidadeParcelas() == null) {
            contratoAlterado.setQuantidadeParcelas(qtdeParcelas);
        } else {
            if (contratoAlterado.getQuantidadeParcelas() > qtdeParcelas) {
                contratoAlterado.setQuantidadeParcelas(qtdeParcelas);
            }
        }

        if (contratoAlterado.getQuantidadeParcelas() > 0) {
            BigDecimal valor = getSaldoDoContrato();
            BigDecimal valorParcela = valor.divide(new BigDecimal(contratoAlterado.getQuantidadeParcelas()), 2, RoundingMode.HALF_UP);
            BigDecimal somaParcelas = valorParcela.multiply(new BigDecimal(contratoAlterado.getQuantidadeParcelas()));
            BigDecimal primeiraParcela = valor.subtract(somaParcelas);
            primeiraParcela = primeiraParcela.add(valorParcela);

            for (int i = 1; i <= contratoAlterado.getQuantidadeParcelas(); i++) {
                ParcelaContratoRendaPatrimonial parcela = new ParcelaContratoRendaPatrimonial();
                parcela.setParcela(i);
                parcela.setVencimento(vencimento.getTime());
                if (i == 1) {
                    parcela.setValor(primeiraParcela);
                } else {
                    parcela.setValor(valorParcela);
                }
                lista.add(parcela);
                contratoAlterado.proximoVencimento(contratoRendasPatrimoniaisFacade.getFeriadoFacade(), vencimento);
            }
        }
        parcelasAlteracaoContrato = lista;
    }

    private void validarConfirmarAlteracaoDeContrato() {
        ValidacaoException ve = new ValidacaoException();
        if (contratoAlterado.getAtividadePonto() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe uma Atividade do Ponto Comercial!");
        }
        if (selecionado.getMotivoOperacao().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo da Operação é obrigatório!");
        }
        if (contratoAlterado.getLocatario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Locatário é obrigatório!");
        }
        if (contratoAlterado.getPontoComercialContratoRendasPatrimoniais().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O contrato não possui Ponto Comercial adicionado!");
        }
        ve.lancarException();
    }

    private boolean podeAlterarContrato() {
        boolean retorno = true;
        if (selecionado.getSituacaoContrato() != SituacaoContratoRendasPatrimoniais.ATIVO) {
            FacesUtil.addOperacaoNaoRealizada("Não é possível alterar contrato que não é ATIVO");
            retorno = false;
        }
        return retorno;
    }

    public void atualizaCamposPessoaContratoAlterado() {
        if (contratoAlterado.getLocatario() != null) {
            locatarioJaPossuiContrato = contratoRendasPatrimoniaisFacade.existeContratoVigenteDoLocatario(contratoAlterado.getLocatario());
            if (locatarioJaPossuiContrato) {
                FacesUtil.addError("Atenção!", "Esse locatário já possui um contrato vigente!");
                FacesUtil.executaJavaScript("alteracaoContrato.hide()");
            }
        }
    }

    public void adicionaPontoComercialContratoAlterado() {
        if (podeAdicionarPontoComercial()) {
            //valor total do contrato (área do box X valor do metro quadrado).
            BigDecimal valorCalculadoMes = pontoComercialContratoRendasPatrimoniais.getPontoComercial().getArea()
                .multiply(pontoComercialContratoRendasPatrimoniais.getPontoComercial().getValorMetroQuadrado());

            pontoComercialContratoRendasPatrimoniais.setValorCalculadoMes(valorCalculadoMes);
            BigDecimal valor;
            try {
                valor = valorCalculadoMes.multiply(BigDecimal.valueOf(selecionado.getPeriodoVigencia()));
            } catch (Exception e) {
                Double calculo = valorCalculadoMes.doubleValue() * parametroRendas.getQuantidadeMesesVigencia();
                valor = new BigDecimal(calculo);
            }

            pontoComercialContratoRendasPatrimoniais.setValorTotalContrato(valor);
            pontoComercialContratoRendasPatrimoniais.setContratoRendasPatrimoniais(contratoAlterado);

            pontoComercialContratoRendasPatrimoniais.setArea(pontoComercialContratoRendasPatrimoniais.getPontoComercial().getArea());
            pontoComercialContratoRendasPatrimoniais.setValorMetroQuadrado(pontoComercialContratoRendasPatrimoniais
                .getPontoComercial().getValorMetroQuadrado());

            contratoAlterado.getPontoComercialContratoRendasPatrimoniais().add(pontoComercialContratoRendasPatrimoniais);
            pontoComercialContratoRendasPatrimoniais = new PontoComercialContratoRendasPatrimoniais();
        }
    }

    private boolean podeAdicionarPontoComercial() {
        boolean retorno = true;
        if (pontoComercialContratoRendasPatrimoniais.getPontoComercial() != null) {
            if (pontoComercialContratoRendasPatrimoniais.getPontoComercial().getValorMetroQuadrado().compareTo(BigDecimal.ZERO) <= 0) {
                FacesUtil.addError("Não foi possível continuar!", "O ponto comercial possui Valor do M² menor ou igual a Zero.");
                retorno = false;
            }
            if (pontoComercialContratoRendasPatrimoniais.getPontoComercial().getArea() == null) {
                FacesUtil.addError("Não foi possível continuar!", "O ponto comercial não possui Área (M²) cadastrado.");
                retorno = false;
            } else if (pontoComercialContratoRendasPatrimoniais.getPontoComercial().getArea().compareTo(BigDecimal.ZERO) <= 0) {
                FacesUtil.addError("Não foi possível continuar!", "O ponto comercial possui Área (M²) menor ou igual a Zero.");
                retorno = false;
            }
        }

        if (parametroRendas == null || parametroRendas.getId() == null) {
            FacesUtil.addError("Não foi possível continuar!", "Para adicionar um Ponto Comercial, o Parâmetro deve ser selecionado.");
            retorno = false;
        }
        if (pontoComercialContratoRendasPatrimoniais.getPontoComercial() == null || pontoComercialContratoRendasPatrimoniais.getPontoComercial().getId() == null) {
            FacesUtil.addError("Não foi possível continuar!", "Selecione o Ponto Comercial desejado.");
            retorno = false;
        }
        if (pontoComercialContratoRendasPatrimoniais.getPontoComercial() != null &&
            contratoRendasPatrimoniaisFacade.hasPontosEmOutroContratoVigenteRendasPatrimoniais(pontoComercialContratoRendasPatrimoniais.getPontoComercial(), selecionado)) {
            FacesUtil.addError("Não foi possível continuar!", "Existe Box que pertence a outro contrato vigente.");
            retorno = false;
        } else {
            for (PontoComercialContratoRendasPatrimoniais p : contratoAlterado.getPontoComercialContratoRendasPatrimoniais()) {
                if (p.getPontoComercial().equals(pontoComercialContratoRendasPatrimoniais.getPontoComercial())) {
                    FacesUtil.addError("Não foi possível continuar!", "Ponto Comercial já selecionado.");
                    retorno = false;
                }
            }
        }
        return retorno;
    }

    public DAM recuperaDAM(Long parcela) {
        List<DAM> retorno = contratoRendasPatrimoniaisFacade.recuperaDAM(parcela);
        if (!retorno.isEmpty()) {
            return retorno.get(0);
        }
        return null;
    }

    @URLAction(mappingId = "renovarContrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void renovacaoAutomaticaDeContrato() {
        selecionado = new ContratoRendasPatrimoniais();
        localizacoesDisponiveisRenovacao = Lists.newArrayList();
        localizacoesSelecionadas = Lists.newArrayList();
        contratosRenovacao = Lists.newArrayList();
        recuperarParametro();
        selecionado.setUsuarioOperacao(contratoRendasPatrimoniaisFacade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setDataOperacao(new Date());

    }

    private void recuperarParametro() {
        parametroRendas = recuperaParametroRendas(getSistemaControlador().getDataOperacao());
    }

    public void pesquisarLocalizacoesAtivas() {
        if (!selecionado.getMotivoOperacao().trim().isEmpty()) {
            localizacoesSelecionadas.clear();
            contratosRenovacao.clear();
            if (temUfmParaAData()) {
                localizacoesDisponiveisRenovacao = contratoRendasPatrimoniaisFacade.getLocalizacaoFacade().buscarLocalizacoesRendasPatrimoniaisComAoMenosUmPontoAtivo();
                localizacoesDisponiveisRenovacao.sort((o1, o2) -> o2.getCodigo().compareTo(o1.getCodigo()));
                localizacoesSelecionadas.addAll(localizacoesDisponiveisRenovacao);
                for (Localizacao localizacao : localizacoesDisponiveisRenovacao) {
                    contratosRenovacao.addAll(contratoRendasPatrimoniaisFacade.recuperarContratosQuePodemSerRenovados(localizacao.getId(), getSistemaControlador().getDataOperacao()));
                }
                if (!contratosRenovacao.isEmpty()) {
                    FacesUtil.addOperacaoRealizada(contratosRenovacao.size() + " Contratos estão prontos para serem renovados, pressione o botão 'Renovar' para continuar.");
                } else {
                    FacesUtil.addOperacaoNaoRealizada("Não existe contratos a serem renovados.");
                }
            }
        } else {
            FacesUtil.addOperacaoNaoPermitida("O campo Motivo é obrigatório.");
        }
    }

    private boolean temUfmParaAData() {
        BigDecimal valor = contratoRendasPatrimoniaisFacade.getMoedaFacade().recuperaValorUFMParaData(getSistemaControlador().getDataOperacao());
        if (valor.compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        FacesUtil.addOperacaoNaoRealizada("Não existe valor de UFM para o exercício da operação.");
        return false;
    }

    public void lerListaDeContratosParaRenovacao() {
        if (localizacoesSelecionadas.isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("Selecione pelo menos uma localização para renovar os contratos.");
        } else {
            FacesUtil.executaJavaScript("iniciaRendas()");
            assistenteRendasPatrimoniais = criarAssistente();
            future = contratoRendasPatrimoniaisFacade.lerListaDeContratosParaRenovacao(assistenteRendasPatrimoniais);
        }
    }

    private AssistenteRendasPatrimoniais criarAssistente() {
        AssistenteRendasPatrimoniais assistente = new AssistenteRendasPatrimoniais();
        assistente.setExecutando(true);
        assistente.setDescricaoProcesso("Renovação de Contratos de Rendas Patrimoniais");
        assistente.setUsuarioSistema(Util.recuperarUsuarioCorrente());
        assistente.setExercicio(contratoRendasPatrimoniaisFacade.getSistemaFacade().getExercicioCorrente());
        assistente.setDataAtual(contratoRendasPatrimoniaisFacade.getSistemaFacade().getDataOperacao());
        assistente.setMotivo(selecionado.getMotivoOperacao());
        assistente.setParametroRendas(this.parametroRendas);

        List<ContratoRendasPatrimoniais> contratos = Lists.newArrayList();

        for (Localizacao localizacao : localizacoesSelecionadas) {
            contratos.addAll(contratosDisponiveisPorLocalizacao(localizacao.getId()));
        }

        assistente.setContratoRendasPatrimoniais(contratos);
        assistente.setTotal(contratos.size());

        return assistente;
    }

    public List<ContratoRendasPatrimoniais> contratosDisponiveisPorLocalizacao(Long idLocalizacao) {
        List<ContratoRendasPatrimoniais> retorno = Lists.newArrayList();
        for (ContratoRendasPatrimoniais contrato : contratosRenovacao) {
            for (PontoComercialContratoRendasPatrimoniais pontoRendas : contrato.getPontoComercialContratoRendasPatrimoniais()) {
                if (idLocalizacao.equals(pontoRendas.getPontoComercial().getLocalizacao().getId())) {
                    retorno.add(contrato);
                    break;
                }
            }
        }
        return retorno;
    }

    public boolean contemLocalizacaoSelecionada(Localizacao localizacao) {
        boolean contem = false;
        if (localizacao != null) {
            for (Localizacao loc : localizacoesSelecionadas) {
                if (loc.equals(localizacao)) {
                    contem = true;
                    break;
                }
            }
        }
        return contem;
    }

    public void adicionarRemoverLocalizacaoSelecionada(Localizacao localizacao, boolean adicionar) {
        if (adicionar) {
            localizacoesSelecionadas.add(localizacao);
        } else {
            localizacoesSelecionadas.remove(localizacao);
        }
    }

    public boolean todasLocalizacoesSelecionadas() {
        return localizacoesSelecionadas.equals(localizacoesDisponiveisRenovacao);
    }

    public void adicionarRemoverTodosSelecionados(boolean adicionar) {
        if (adicionar) {
            localizacoesSelecionadas.clear();
            localizacoesSelecionadas.addAll(localizacoesDisponiveisRenovacao);
        } else {
            localizacoesSelecionadas.clear();
        }
    }

    public void verificaTerminoRenovacao() {
        if (future != null && future.isDone()) {
            try {
                AssistenteRendasPatrimoniais assistenteRendasPatrimoniais = future.get();
                for (ProcessoCalculoRendas processo : assistenteRendasPatrimoniais.getProcessos()) {
                    contratoRendasPatrimoniaisFacade.gerarDebitoContrato(processo);
                }
                assistenteRendasPatrimoniais.setExecutando(false);
                FacesUtil.executaJavaScript("terminaRendas()");
            } catch (Exception e) {
                assistenteRendasPatrimoniais.setExecutando(false);
                logger.error("Erro ao gerar debitos: {}", e);
                assistenteRendasPatrimoniais.zerarContadoresProcesso();
            } finally {
                future = null;
            }
        }
    }

    public void encerrarFuture() {
        FacesUtil.executaJavaScript("terminaProcesso()");
    }

    public BigDecimal getValorTotalPagoNoContrato() {
        BigDecimal valorPago = BigDecimal.ZERO;
        if (parcelas != null && !parcelas.isEmpty()) {
            for (ResultadoParcela parcela : parcelas) {
                if (parcela.isPago()) {
                    valorPago = valorPago.add(parcela.getValorPago());
                }
            }
        }
        return valorPago;
    }

    public BigDecimal getValorOriginalVencidoContrato() {
        BigDecimal valorVencido = BigDecimal.ZERO;
        if (parcelas != null && !parcelas.isEmpty()) {
            for (ResultadoParcela parcela : parcelas) {
                if (parcela.isVencidoEEmAberto(new Date())) {
                    valorVencido = valorVencido.add(parcela.getValorOriginal());
                }
            }
        }
        return valorVencido;
    }

    public BigDecimal getValorPagoDoContrato() {
        BigDecimal valorPago = BigDecimal.ZERO;
        if (parcelas != null && !parcelas.isEmpty()) {
            for (ResultadoParcela parcela : parcelas) {
                if (parcela.isPago()) {
                    valorPago = valorPago.add(parcela.getValorOriginal());
                }
            }
        }
        return valorPago;
    }

    public BigDecimal getSaldoDoContrato() {
        BigDecimal saldo = getSomaDoValorTotalContratoMultiplicadoAlterado().subtract(getValorPagoDoContrato());
        if (saldo.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return saldo;
    }

    public boolean podeEfetivarAlteracaoContrato() {
        return !parcelasAlteracaoContrato.isEmpty() || getSaldoDoContrato().compareTo(BigDecimal.ZERO) == 0;
    }

    public void validarLotacaoContrato() {
        try {
            ValidacaoException ve = new ValidacaoException();
            if (selecionado.getLotacaoVistoriadora() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Selecione uma lotação vistoriadora.");
            }
            ve.lancarException();
            FacesUtil.executaJavaScript("widgetDialogSelecaoLotacao.hide()");
            FacesUtil.atualizarComponente("Formulario");
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getAllMensagens());
        }
    }

    public void adicionarLotacaoAoContrato(LotacaoVistoriadora lotacao) {
        selecionado.setLotacaoVistoriadora(lotacao);
        FacesUtil.atualizarComponente("Formulario");
    }

    public boolean lotacaoSelecionada(LotacaoVistoriadora lotacao) {
        if (selecionado.getLotacaoVistoriadora() == null) return false;
        return selecionado.getLotacaoVistoriadora().getId().equals(lotacao.getId());
    }

    public boolean podeGerenciarContrato() {
        return contratoRendasPatrimoniaisFacade.getUsuarioSistemaFacade().validaAutorizacaoUsuarioLogado(AutorizacaoTributario.GERENCIAR_CONTRATO_RENDAS_PATRIMONIAIS);
    }

    public boolean isTodosItensMarcados() {
        return !parcelas.isEmpty() && parcelas.stream().filter(ResultadoParcela::isEmAberto).count() == parcelasSelecionadas.size();
    }

    public boolean parcelaMarcada(ResultadoParcela parcela) {
        return parcelasSelecionadas.contains(parcela);
    }

    public void setTodosItensMarcados(boolean todosItensMarcados) {
        parcelasSelecionadas.clear();
        if (todosItensMarcados) {
            for (ResultadoParcela rp : parcelas) {
                if (rp.isEmAberto()) parcelasSelecionadas.add(rp);
            }
        }
    }

    public void marcarParcela(ResultadoParcela parcela) {
        parcelasSelecionadas.add(parcela);
    }

    public void desmarcarParcela(ResultadoParcela parcela) {
        parcelasSelecionadas.remove(parcela);
    }

    public BigDecimal valorDam() {
        return parcelasSelecionadas.stream().map(ResultadoParcela::getValorOriginal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean maisDeUmaParcelaSelecionada() {
        return parcelasSelecionadas.size() > 1;
    }

    public Date calcularVencimentoDam() {
        Calendar c = DataUtil.ultimoDiaMes(new Date());
        DataUtil.ultimoDiaUtil(c, contratoRendasPatrimoniaisFacade.getFeriadoFacade());
        return c.getTime();
    }
}


