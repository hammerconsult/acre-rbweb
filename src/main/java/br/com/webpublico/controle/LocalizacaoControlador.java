/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.OrdenaPontosComerciais;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoUtilizacaoRendasPatrimoniais;
import br.com.webpublico.enums.TipoVinculoImobiliario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.LocalizacaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
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
import java.util.Collections;
import java.util.List;

@ManagedBean(name = "localizacaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novaLocalizacao", pattern = "/localizacao/novo/", viewId = "/faces/tributario/rendaspatrimoniais/localizacao/edita.xhtml"),
        @URLMapping(id = "editarLocalizacao", pattern = "/localizacao/editar/#{localizacaoControlador.id}/", viewId = "/faces/tributario/rendaspatrimoniais/localizacao/edita.xhtml"),
        @URLMapping(id = "listarLocalizacao", pattern = "/localizacao/listar/", viewId = "/faces/tributario/rendaspatrimoniais/localizacao/lista.xhtml"),
        @URLMapping(id = "verLocalizacao", pattern = "/localizacao/ver/#{localizacaoControlador.id}/", viewId = "/faces/tributario/rendaspatrimoniais/localizacao/visualizar.xhtml")
})
public class LocalizacaoControlador extends PrettyControlador<Localizacao> implements Serializable, CRUD {

    public static final String RENDAS_PATRIMONIAIS = "RENDAS PATRIMONIAIS";
    @EJB
    private LocalizacaoFacade localizacaoFacade;
    private PontoComercial pontoComercial;
    private PontoComercial pontoComercialAlterado;
    private ConverterGenerico converterPontoComercial;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterBairro;
    private ConverterAutoComplete converterCI;
    private ConverterAutoComplete converterLogradouro;
    private ConverterGenerico converterExercicio;
    private ConverterAutoComplete converterSecretaria;
    private ConverterAutoComplete converterAtividadePonto;
    private ItemPontoComercial itemPontoComercial;
    private ConverterAutoComplete converterTipoPontoComercial;
    private Exercicio exercicio;
    private ConverterGenerico converterLotacaoVistoriadora;
    private ConverterAutoComplete converterIndiceEconomico;
    private ConverterAutoComplete converterParametroDescontoRendaPatrimonial;
    private HierarquiaOrganizacional hierarquiaSelecionada;
    private Boolean editandoPontoComercial;

    public LocalizacaoControlador() {
        super(Localizacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return localizacaoFacade;
    }

    public ItemPontoComercial getItemPontoComercial() {
        if (itemPontoComercial == null) {
            itemPontoComercial = new ItemPontoComercial();
        }
        return itemPontoComercial;
    }

    public void setItemPontoComercial(ItemPontoComercial itemPontoComercial) {
        this.itemPontoComercial = itemPontoComercial;
    }


    public PontoComercial getPontoComercialAlterado() {
        return pontoComercialAlterado;
    }

    public void setPontoComercialAlterado(PontoComercial pontoComercialAlterado) {
        this.pontoComercialAlterado = pontoComercialAlterado;
    }

    public Converter getConverterPontoComercial() {
        if (converterPontoComercial == null) {
            converterPontoComercial = new ConverterGenerico(TipoPontoComercial.class, localizacaoFacade.getTipoPontoComercialFacade());
        }
        return converterPontoComercial;
    }

    public Converter getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(PessoaFisica.class, localizacaoFacade.getPessoaFisicaFacade());
        }
        return converterPessoa;
    }

    public PontoComercial getPontoComercial() {
        if (pontoComercial == null) {
            pontoComercial = new PontoComercial();
        }
        return pontoComercial;
    }

    public void setPontoComercial(PontoComercial pontoComercial) {
        this.pontoComercial = pontoComercial;
    }

    public HierarquiaOrganizacional getHierarquiaSelecionada() {
        return hierarquiaSelecionada;
    }

    public void setHierarquiaSelecionada(HierarquiaOrganizacional hierarquiaSelecionada) {
        this.hierarquiaSelecionada = hierarquiaSelecionada;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/localizacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novaLocalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setCodigo(localizacaoFacade.retornaUltimoCodigoLong());
        pontoComercial = new PontoComercial();
        exercicio = new Exercicio();
        selecionado.setTipoVinculoImobiliario(TipoVinculoImobiliario.VINCULACAO_INDIVIDUALIZADA);
        editandoPontoComercial = false;
    }

    public Converter getConverterBairro() {
        if (converterBairro == null) {
            converterBairro = new ConverterAutoComplete(Bairro.class, localizacaoFacade.getBairroFacade());
        }
        return converterBairro;
    }

    public Converter getConverterCI() {
        if (converterCI == null) {
            converterCI = new ConverterAutoComplete(CadastroImobiliario.class, localizacaoFacade.getCadastroImobiliarioFacade());
        }
        return converterCI;
    }

    public Converter getConverterLogradouro() {
        if (converterLogradouro == null) {
            converterLogradouro = new ConverterAutoComplete(Logradouro.class, localizacaoFacade.getLogradouroFacade());
        }
        return converterLogradouro;
    }

    public Converter getConverterSecretaria() {
        if (converterSecretaria == null) {
            converterSecretaria = new ConverterAutoComplete(HierarquiaOrganizacional.class, localizacaoFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterSecretaria;
    }

    public Converter getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterGenerico(Exercicio.class, localizacaoFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public List<PessoaFisica> completaPessoaFisicas(String parte) {
        return localizacaoFacade.getPessoaFisicaFacade().listaPessoaPorMatriculaFP(parte.trim());
    }

    public List<HierarquiaOrganizacional> completaSecretaria(String parte) {
        return localizacaoFacade.getHierarquiaOrganizacionalFacade().listaFiltrandoPorOrgaoAndTipoAdministrativa(parte.trim());
    }

    public List<CadastroImobiliario> completaCadastroImobiliarios(String parte) {
        return localizacaoFacade.getCadastroImobiliarioFacade().listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public List<Logradouro> completaLogradouros(String parte) {
        return localizacaoFacade.getLogradouroFacade().listarPorBairro(selecionado.getBairro(), parte.trim());
    }

    public List<Bairro> completaBairros(String parte) {
        return localizacaoFacade.getBairroFacade().listaFiltrando(parte.trim(), "descricao");
    }

    @URLAction(mappingId = "editarLocalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        carregarHierarquiaSelecionada();
        pontoComercial = new PontoComercial();
        if (!selecionado.getPontosComerciais().isEmpty()) {
            Collections.sort(selecionado.getPontosComerciais(), new OrdenaPontosComerciais());
        }
        localizacaoFacade.carregarDisponibilidadePontosComerciais(selecionado, RENDAS_PATRIMONIAIS);
        exercicio = new Exercicio();
        editandoPontoComercial = false;
    }

    @URLAction(mappingId = "verLocalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    private void carregarHierarquiaSelecionada() {
        if (selecionado.getSecretaria() != null) {
            hierarquiaSelecionada = localizacaoFacade.getHierarquiaOrganizacionalFacade()
                .getHierarquiaOrganizacionalVigentePorUnidade(selecionado.getSecretaria(),
                    TipoHierarquiaOrganizacional.ADMINISTRATIVA, getSistemaControlador().getDataOperacao());
        }
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            if (selecionado.getId() == null) {
                selecionado.setCodigo(localizacaoFacade.retornaUltimoCodigoLong());
            }
            if (hierarquiaSelecionada != null) {
                selecionado.setSecretaria(hierarquiaSelecionada.getSubordinada());
            }
            super.salvar();
        }
    }

    private boolean validaCampos() {
        boolean resultado = true;
        if (selecionado.getCodigo() == null) {
            resultado = false;
            FacesUtil.addError("Atenção!", "Informe o Código.");
        }
        if (selecionado.getDescricao() == null || selecionado.getDescricao().isEmpty()) {
            resultado = false;
            FacesUtil.addError("Atenção!", "Informe a Descrição.");
        }

        if (selecionado.getLotacaoVistoriadora() == null) {
            resultado = false;
            FacesUtil.addError("Atenção!", "Informe a Lotação.");
        }

        if (selecionado.getTipoOcupacaoLocalizacao() == null) {
            resultado = false;
            FacesUtil.addError("Atenção!", "Informe o Tipo de Ocupação.");
        }

        if (selecionado.getIndiceEconomico() == null) {
            resultado = false;
            FacesUtil.addError("Atenção!", "Informe o Índice Econômico.");
        }

        if (selecionado.getPontosComerciais() == null || selecionado.getPontosComerciais().isEmpty()) {
            resultado = false;
            FacesUtil.addError("Atenção!", "Informe o Pontos Comercial.");
        }

        if (selecionado.getTipoVinculoImobiliario() == null){
            resultado = false;
            FacesUtil.addError("Atenção!", "Informe o tipo de Vinculação Imobiliária.");
        }

        return resultado;
    }

    public List<SelectItem> getTipoPontosComerciais() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoPontoComercial tpc : localizacaoFacade.getTipoPontoComercialFacade().lista()) {
            toReturn.add(new SelectItem(tpc, tpc.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoPontosComerciaisOrdemAlfabetica() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoPontoComercial ponto : localizacaoFacade.getTipoPontoComercialFacade().listaOrdemAlfabetica()) {
            retorno.add(new SelectItem(ponto, ponto.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTipoVinculoImobiliario() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoVinculoImobiliario vinculo : TipoVinculoImobiliario.values()) {
            retorno.add(new SelectItem(vinculo, vinculo.getDescricao().toUpperCase()));
        }
        return retorno;
    }

    public List<SelectItem> getTipoOcupacoes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoUtilizacaoRendasPatrimoniais to : TipoUtilizacaoRendasPatrimoniais.values()) {
            toReturn.add(new SelectItem(to, to.getDescricao().toUpperCase()));
        }
        return toReturn;
    }

    public Converter getConverterAtividadePonto() {
        if (converterAtividadePonto == null) {
            converterAtividadePonto = new ConverterAutoComplete(AtividadePonto.class, localizacaoFacade.getAtividadePontoFacade());
        }
        return converterAtividadePonto;
    }

    public List<AtividadePonto> completaAtividadePonto(String parte) {
        return localizacaoFacade.getAtividadePontoFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public Converter getConverterTipoPontoComercial() {
        if (converterTipoPontoComercial == null) {
            converterTipoPontoComercial = new ConverterAutoComplete(TipoPontoComercial.class, localizacaoFacade.getTipoPontoComercialFacade());
        }
        return converterTipoPontoComercial;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> listaExercicios = new ArrayList<SelectItem>();
        listaExercicios.add(new SelectItem(null, " "));
        for (Exercicio ex : localizacaoFacade.getExercicioFacade().listaExerciciosAtualFuturos()) {
            listaExercicios.add(new SelectItem(ex, ex.getAno().toString()));
        }
        return listaExercicios;
    }

    public void addPontoComercial() {
        try {
            validarCamposPontoComercial();
            if (editandoPontoComercial) {
                selecionado.getPontosComerciais().set(selecionado.getPontosComerciais().indexOf(pontoComercial), pontoComercial);
            } else {
                pontoComercial.setLocalizacao(selecionado);
                selecionado.getPontosComerciais().add(pontoComercial);
            }
            Collections.sort(selecionado.getPontosComerciais(), new OrdenaPontosComerciais());
            pontoComercial = new PontoComercial();
            editandoPontoComercial = false;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarEdicaoPontoComercial() {
        pontoComercial = new PontoComercial();
        pontoComercialAlterado = new PontoComercial();
        editandoPontoComercial = false;
    }

    public boolean isEditandoPontoComercial() {
        return editandoPontoComercial;
    }

    public void removerPontoComercial(PontoComercial ponto) {
        if (ponto != null && ponto.getId() != null) {
            if (!utilizandoPontoComercialEmAlgumContrato(ponto)) {
                selecionado.getPontosComerciais().remove(ponto);
            } else {
                FacesUtil.addOperacaoNaoPermitida("Não é possível excluir o ponto comercial. Há um Contrato de Rendas Patrimoniais vinculado a esta Localização.");
            }
        } else {
            selecionado.getPontosComerciais().remove(ponto);
        }
    }

    public void editarPontoComercial(PontoComercial ponto) {
        cancelarEdicaoPontoComercial();
        pontoComercial = (PontoComercial) Util.clonarObjeto(ponto);
        pontoComercialAlterado = (PontoComercial) Util.clonarObjeto(ponto);
        editandoPontoComercial = true;
    }

    private void validarCamposPontoComercial() {
        ValidacaoException ve = new ValidacaoException();

        if (pontoComercial.getTipoPontoComercial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo de Ponto Comercial.");
        }

        if (pontoComercial.getValorMetroQuadrado() == null || pontoComercial.getValorMetroQuadrado().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Valor do m².");
        }

        if (pontoComercial.getNumeroBox().isEmpty() || pontoComercial.getNumeroBox() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Número do Box.");
        }

        if (pontoComercial.getArea() == null || pontoComercial.getArea().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Área do Box.");
        }

        if (jaExisteBoxNaLocalizacao()) {
            ve.adicionarMensagemDeCampoObrigatorio("Box já cadastrado com o Número e Tipo de Ponto Comercial informado para essa Localização.");
        }

        if (!editandoPontoComercial) {
            if (TipoVinculoImobiliario.VINCULACAO_INDIVIDUALIZADA.equals(selecionado.getTipoVinculoImobiliario())) {
                if (pontoComercial.getCadastroImobiliario() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("Na vinculação Individualizada, é necessário selecionar um cadastro imobiliário.");
                }
            }
        }

        if (TipoVinculoImobiliario.VINCULACAO_TOTALIZADA.equals(selecionado.getTipoVinculoImobiliario())){
            if (pontoComercial.getCadastroImobiliario() == null) {
                pontoComercial.setCadastroImobiliario(selecionado.getCadastroImobiliario());
            }
        }

        ve.lancarException();
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<SelectItem> getLotacaoVistoriadoras() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));

        ConfiguracaoTributario configuracao = localizacaoFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        for (LotacaoVistoriadoraConfigTribRendas rendasLotacaoVistoriadora : configuracao.getRendasLotacoesVistoriadoras()) {
            toReturn.add(new SelectItem(rendasLotacaoVistoriadora.getLotacaoVistoriadora(), rendasLotacaoVistoriadora.getLotacaoVistoriadora().toString()));
        }

        if (configuracao.getCeasaLotacaoVistoriadora() != null) {
            toReturn.add(new SelectItem(configuracao.getCeasaLotacaoVistoriadora(), configuracao.getCeasaLotacaoVistoriadora().toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterLotacaoVistoriadora() {
        if (converterLotacaoVistoriadora == null) {
            converterLotacaoVistoriadora = new ConverterGenerico(LotacaoVistoriadora.class, localizacaoFacade.getLotacaoVistoriadoraFacade());
        }
        return converterLotacaoVistoriadora;
    }

    public Converter getConverterIndiceEconomico() {
        if (converterIndiceEconomico == null) {
            converterIndiceEconomico = new ConverterAutoComplete(IndiceEconomico.class, localizacaoFacade.getIndiceEconomicoFacade());
        }
        return converterIndiceEconomico;
    }

    public List<IndiceEconomico> completaIndiceEconomico(String parte) {
        return localizacaoFacade.getIndiceEconomicoFacade().listaFiltrando(parte.toLowerCase().trim(), "descricao");
    }

    public List<TipoPontoComercial> completaTipoPontoComercial(String parte) {
        return localizacaoFacade.getTipoPontoComercialFacade().listaFiltrandoCodigoDescricao(parte.trim());
    }

    public Converter getConverterParametroDescontoRendaPatrimonial() {
        if (converterParametroDescontoRendaPatrimonial == null) {
            converterParametroDescontoRendaPatrimonial = new ConverterAutoComplete(ParametroDescontoRendaPatrimonial.class, localizacaoFacade.getParametroDescontoRendaPatrimonialFacade());
        }
        return converterParametroDescontoRendaPatrimonial;
    }

    public List<ParametroDescontoRendaPatrimonial> completaParametroDescontoRendaPatrimonial(String parte) {
        return localizacaoFacade.getParametroDescontoRendaPatrimonialFacade().listaFiltrandoCodigoDescricao(parte);
    }

    private boolean utilizandoPontoComercialEmAlgumContrato(PontoComercial ponto) {
        if (selecionado.getLotacaoVistoriadora().getDescricao().equals(RENDAS_PATRIMONIAIS)) {
            return localizacaoFacade.getContratoRendasPatrimoniaisFacade().hasPontosEmOutroContratoRendasPatrimoniais(ponto);
        }
        return localizacaoFacade.getContratoRendasPatrimoniaisFacade().existePontosEmOutroContratoCEASA(ponto);
    }

    private boolean jaExisteBoxNaLocalizacao() {
        for (PontoComercial pontoComercial : selecionado.getPontosComerciais()) {
            if (pontoComercial.getNumeroBox().equalsIgnoreCase(this.pontoComercial.getNumeroBox())
                && pontoComercial.getTipoPontoComercial().equals(this.pontoComercial.getTipoPontoComercial())
                && !this.pontoComercial.equals(pontoComercial)) {
                return true;
            }
        }
        return false;
    }

    public void definirAreaPontoComercial(PontoComercial pontoComercial){
        if (pontoComercial.getCadastroImobiliario() != null && pontoComercial.getDisponivel()){
            if (pontoComercial.getCadastroImobiliario().getAreaTotalConstruida() != null) {
                pontoComercial.setArea(pontoComercial.getCadastroImobiliario().getAreaTotalConstruida());
            }
        }
    }

    public boolean isVinculacaoIndividualizada(){
        return TipoVinculoImobiliario.VINCULACAO_INDIVIDUALIZADA.equals(selecionado.getTipoVinculoImobiliario());
    }

    public boolean isVinculacaoTotalizada(){
        return TipoVinculoImobiliario.VINCULACAO_TOTALIZADA.equals(selecionado.getTipoVinculoImobiliario());
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        if (obj instanceof CadastroImobiliario) {
            selecionado.setCadastroImobiliario((CadastroImobiliario) obj);
        }
    }

    public void limparCadastroImobiliario() {
        if (selecionado.isIndividualizada()) {
            selecionado.setCadastroImobiliario(null);
        }
    }
}
