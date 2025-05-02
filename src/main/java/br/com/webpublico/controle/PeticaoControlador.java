/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoCadastroDoctoOficial;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.PeticaoFacade;
import br.com.webpublico.negocios.VaraCivelFacade;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gustavo
 */
@ManagedBean(name = "peticaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaPeticao", pattern = "/tributario/peticao/novo/", viewId = "/faces/tributario/dividaativa/peticao/emite.xhtml"),
    @URLMapping(id = "listarPeticao", pattern = "/tributario/peticao/listar", viewId = "/faces/tributario/dividaativa/peticao/lista.xhtml"),

    @URLMapping(id = "novoParametroPeticao", pattern = "/tributario/peticao/parametros/novo/", viewId = "/faces/tributario/dividaativa/parametrospeticao/edita.xhtml"),
    @URLMapping(id = "listaParametroPeticao", pattern = "/tributario/peticao/parametros/listar/", viewId = "/faces/tributario/dividaativa/parametrospeticao/lista.xhtml"),
    @URLMapping(id = "editarParametroPeticao", pattern = "/tributario/peticao/parametros/editar/#{peticaoControlador.id}/", viewId = "/faces/tributario/dividaativa/parametrospeticao/edita.xhtml"),
})
public class PeticaoControlador extends PrettyControlador<Peticao> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(PeticaoControlador.class);

    @EJB
    private PeticaoFacade peticaoFacade;
    @EJB
    private VaraCivelFacade varaCivelFacade;
    private Peticao selecionado;
    private GeradoPor geradoPor;
    private Long certidaoInicial;
    private Long certidaoFinal;
    private String cadastroInicial;
    private String cadastroFinal;
    private TipoCadastroTributario tipoCadastroTributario;
    private Pessoa pessoa;
    private ConverterAutoComplete converterCadastroImobiliario;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterCadastroEconomico;
    private ConverterAutoComplete converterCadastroRendasPatrimoniais;
    private ConverterAutoComplete converterCadastroRural;
    private ConverterAutoComplete converterCertidaoDividaAtiva;
    private ConverterExercicio converterExercicio;
    private ConverterGenerico converterVaraCivel;
    private ConverterAutoComplete converterTipoDocOficial;
    private ParametrosPeticao parametrosPeticao;
    private List<CertidaoDividaAtiva> certidoes;
    private CertidaoDividaAtiva[] certidaoSelecionada;
    private List<Peticao> peticoes;
    private Date dataEmissao;
    private Exercicio exercicio;
    private TipoDoctoOficial tipoDoctoOficial;
    private String caminho;
    public Integer opcao;

    public CertidaoDividaAtiva[] getCertidaoSelecionada() {
        return certidaoSelecionada;
    }

    public void setCertidaoSelecionada(CertidaoDividaAtiva[] certidaoSelecionada) {
        this.certidaoSelecionada = certidaoSelecionada;
    }

    public Integer getOpcao() {
        return opcao;
    }

    public void setOpcao(Integer opcao) {
        this.opcao = opcao;
    }

    public PeticaoControlador() {
        super(Peticao.class);
    }

    public ParametrosPeticao getParametrosPeticao() {
        return parametrosPeticao;
    }

    public void setParametrosPeticao(ParametrosPeticao parametrosPeticao) {
        this.parametrosPeticao = parametrosPeticao;
    }

    public TipoDoctoOficial getTipoDoctoOficial() {
        return tipoDoctoOficial;
    }

    public void setTipoDoctoOficial(TipoDoctoOficial tipoDoctoOficial) {
        this.tipoDoctoOficial = tipoDoctoOficial;
    }

    @Override
    public Peticao getSelecionado() {
        return selecionado;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    @Override
    public void setSelecionado(Peticao selecionado) {
        this.selecionado = selecionado;
    }

    public String getCadastroInicial() {
        return cadastroInicial;
    }

    public void setCadastroInicial(String cadastroInicial) {
        this.cadastroInicial = cadastroInicial;
    }

    public String getCadastroFinal() {
        return cadastroFinal;
    }

    public void setCadastroFinal(String cadastroFinal) {
        this.cadastroFinal = cadastroFinal;
    }

    public Long getCertidaoFinal() {
        return certidaoFinal;
    }

    public void setCertidaoFinal(Long certidaoFinal) {
        this.certidaoFinal = certidaoFinal;
    }

    public Long getCertidaoInicial() {
        return certidaoInicial;
    }

    public void setCertidaoInicial(Long certidaoInicial) {
        this.certidaoInicial = certidaoInicial;
    }

    public GeradoPor getGeradoPor() {
        return geradoPor;
    }

    public void setGeradoPor(GeradoPor geradoPor) {
        this.geradoPor = geradoPor;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public void salvar() {
    }

    public List<CertidaoDividaAtiva> getCertidoes() {
        return certidoes;
    }

    public List<ItemInscricaoDividaAtiva> itensInscricaoDividaAtiva(CertidaoDividaAtiva certidao) {
        return peticaoFacade.recuperaItemInscricaoPelaCertidao(certidao);
    }

    public BigDecimal valorPorItem(ItemInscricaoDividaAtiva item) {
        return peticaoFacade.recuperaParcelaValorDividaPelaCertidao(item);
    }

    public void setCertidoes(List<CertidaoDividaAtiva> certidoes) {
        this.certidoes = certidoes;
    }

    public List<Peticao> getPeticoes() {
        return peticoes;
    }

    public void setPeticoes(List<Peticao> peticoes) {
        this.peticoes = peticoes;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/peticao/";
    }

    public void getCaminhoPadraoParametro() {
        redireciona();
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getCodigo();
    }

    @Override
    public AbstractFacade getFacede() {
        return peticaoFacade;
    }

    @URLAction(mappingId = "novaPeticao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        selecionado = new Peticao();
        selecionado.setCodigo(peticaoFacade.retornaUltimoCodigoLong());
        cadastroInicial = "1";
        cadastroFinal = "999999999999999999";
        certidaoInicial = null;
        certidaoFinal = null;
        dataEmissao = null;
        exercicio = null;
        tipoCadastroTributario = null;
        certidoes = new ArrayList<>();
        certidaoSelecionada = null;
        peticoes = new ArrayList<>();
        geradoPor = GeradoPor.NUMERO_CERTIDAO;
        tipoDoctoOficial = new TipoDoctoOficial();
    }

    public String emitir() {
        if (validaCampos()) {
            try {
                peticoes = peticaoFacade.geraPeticoes(certidaoSelecionada, dataEmissao, exercicio, getSistemaControlador(), selecionado);
                FacesUtil.addInfo("Petição gerada com sucesso!", "");
                return "lista.xhtml";
            } catch (ExcecaoNegocioGenerica ex) {
                FacesUtil.addError("Erro ao salvar", ex.getMessage());
            } catch (UFMException ex) {
                FacesUtil.addError("Erro ao salvar", ex.getMessage());
            } catch (AtributosNulosException ex) {
                FacesUtil.addError("Erro ao salvar", ex.getMessage());
            }
        } else {
            return "";
        }
        return "";
    }

    public void filtrar() {
        certidaoSelecionada = null;
        if ((certidaoInicial == null && certidaoFinal == null && pessoa == null) && tipoCadastroTributario == null) {
            FacesUtil.addError("Não foi possível continuar", "Informe ao menos um filtro");
        } else {
            certidoes = peticaoFacade.buscaCertidoes(cadastroInicial, cadastroFinal, certidaoInicial, certidaoFinal, pessoa, tipoCadastroTributario, opcao);
        }
    }

    private boolean validaCamposParametro() {
        boolean retorno = true;
        if (parametrosPeticao.getDataCriacao() == null) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar", "A data de criação deve ser informada");
        }
        if (parametrosPeticao.getDoctoPessoaFisica() == null) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar", "O tipo de documento para pessoa física deve ser informado");
        }
        if (parametrosPeticao.getDoctoPessoaJuridica() == null) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar", "O tipo de documento para pessoa jurídica deve ser informado");
        }
        if (parametrosPeticao.getDoctoEconomico() == null) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar", "O tipo de documento para cadastro Econômico deve ser informado");
        }
        if (parametrosPeticao.getDoctoRural() == null) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar", "O tipo de documento para cadastro Rural deve ser informado");
        }
        if (parametrosPeticao.getDoctoImobiliario() == null) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar", "O tipo de documento para cadastro Imobiliário deve ser informado");
        }
        if (parametrosPeticao.getFuncionarioResponsavel() == null) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar", "O Funcionário responsável pela assinatura deve ser informado");
        }
        return retorno;
    }


    public enum GeradoPor {

        NUMERO_CERTIDAO("Número da Certidão"),
        CONTRIBUINTE("Contribuinte"),
        NUMERO_CADASTRO("Número do Cadastro");
        private String descricao;

        public String getDescricao() {
            return descricao;
        }

        private GeradoPor(String descricao) {
            this.descricao = descricao;
        }
    }

    public GeradoPor getGeradoPorNumeroCertidao() {
        return GeradoPor.NUMERO_CERTIDAO;
    }

    public GeradoPor getGeradoPorNumeroCadastro() {
        return GeradoPor.NUMERO_CADASTRO;
    }

    public GeradoPor getGeradoPorContribuinte() {
        return GeradoPor.CONTRIBUINTE;
    }

    public void setaInscricaoCadastro() {
        cadastroInicial = "1";
        cadastroFinal = "999999999999999999";
    }

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getVaraCivels() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (VaraCivel vc : varaCivelFacade.lista()) {
            retorno.add(new SelectItem(vc, vc.getNome()));

        }
        return retorno;
    }

    public ConverterAutoComplete getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, peticaoFacade.getCadastroImobiliarioFacade());
        }
        return converterCadastroImobiliario;
    }

    public ConverterAutoComplete getConverterCertidaoDividaAtiva() {
        if (converterCertidaoDividaAtiva == null) {
            converterCertidaoDividaAtiva = new ConverterAutoComplete(CertidaoDividaAtiva.class, peticaoFacade.getCertidaoDividaAtivaFacade());
        }
        return converterCertidaoDividaAtiva;
    }

    public Converter getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(peticaoFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public Converter getConverterVaraCivel() {
        if (converterVaraCivel == null) {
            converterVaraCivel = new ConverterGenerico(VaraCivel.class, varaCivelFacade);
        }
        return converterVaraCivel;
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return peticaoFacade.getCadastroEconomicoFacade().buscarCadastrosPorInscricaoOrCpfCnpjOrNome(parte.trim());
    }

    public List<Exercicio> completaExercicio(String parte) {
        return peticaoFacade.getExercicioFacade().listaFiltrandoEspecial(parte.trim());
    }

    public List<CertidaoDividaAtiva> completaCertidaoDivida(String parte) {
        return peticaoFacade.getCertidaoDividaAtivaFacade().listaFiltrandoCertidaoDividaAtiva(parte.trim());
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return peticaoFacade.getCadastroImobiliarioFacade().listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public List<Pessoa> completaPessoa(String parte) {
        return peticaoFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public List<PessoaFisica> completaFuncionarios(String parte) {
        return peticaoFacade.getCertidaoDividaAtivaFacade().completaPessoaFisica(parte.trim());
    }

    public ConverterAutoComplete getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, peticaoFacade.getCadastroEconomicoFacade());
        }
        return converterCadastroEconomico;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, peticaoFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public List<CadastroRural> completaCadastroRural(String parte) {
        return peticaoFacade.getCadastroRuralFacade().listaFiltrandoPorCodigo(parte.trim());
    }

    public ConverterAutoComplete getConverterCadastroRural() {
        if (converterCadastroRural == null) {
            converterCadastroRural = new ConverterAutoComplete(CadastroRural.class, peticaoFacade.getCadastroRuralFacade());
        }
        return converterCadastroRural;
    }

    public List<ContratoRendasPatrimoniais> completaContratolRendasPatrimonial(String parte) {
        return peticaoFacade.getContratoRendasPatrimoniaisFacade().listaFiltrando(parte.trim(), "numeroContrato");
    }

    public ConverterAutoComplete getConverterContratoRendasPatrimoniais() {
        if (converterCadastroRendasPatrimoniais == null) {
            converterCadastroRendasPatrimoniais = new ConverterAutoComplete(ContratoRendasPatrimoniais.class, peticaoFacade.getContratoRendasPatrimoniaisFacade());
        }
        return converterCadastroRendasPatrimoniais;
    }

    public void removeCDA(CertidaoDividaAtiva certidao) {
        certidoes.remove(certidao);
    }

    public void removeTodasCDA() {
        certidoes = new ArrayList<>();
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficial(String parte) {
        return peticaoFacade.completaTipoDoctoOficial(parte.trim());
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialDoParametroPessoaFisica(String parte) {
        return peticaoFacade.completaTipoDoctoOficialPorTipo(parte.trim(), TipoCadastroDoctoOficial.PESSOAFISICA);
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialDoParametroPessoaJuridica(String parte) {
        return peticaoFacade.completaTipoDoctoOficialPorTipo(parte.trim(), TipoCadastroDoctoOficial.PESSOAJURIDICA);
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialDoParametroImobiliario(String parte) {
        return peticaoFacade.completaTipoDoctoOficialPorTipo(parte.trim(), TipoCadastroDoctoOficial.CADASTROIMOBILIARIO);
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialDoParametroEconomico(String parte) {
        return peticaoFacade.completaTipoDoctoOficialPorTipo(parte.trim(), TipoCadastroDoctoOficial.CADASTROECONOMICO);
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialDoParametroRural(String parte) {
        return peticaoFacade.completaTipoDoctoOficialPorTipo(parte.trim(), TipoCadastroDoctoOficial.CADASTRORURAL);
    }

    public ConverterAutoComplete converterTipoDoctoOficial() {
        if (converterTipoDocOficial == null) {
            converterTipoDocOficial = new ConverterAutoComplete(TipoDoctoOficial.class, peticaoFacade.getDocumentoOficialFacade().getTipoDoctoOficialFacade());
        }
        return converterTipoDocOficial;
    }

    public List<Peticao> recuperaPeticoes() {
        return peticaoFacade.recuperaPeticoes();
    }

    public List<Peticao> recuperaPeticoesEmAberto() {
        return peticaoFacade.recuperaPeticoesEmAberto();
    }

    public void imprimir(Peticao peticao) {
        try {
            peticaoFacade.geraDocumentoOficialdaPeticao(peticao, getSistemaControlador());
        } catch (Exception e) {
            FacesUtil.addError("Erro ao salvar", e.getMessage());
        }
    }

    public void imprimirNovo(Peticao peticao) {
        peticaoFacade.imprimeNovo(peticao);
    }

    public boolean validaCampos() {
        boolean retorno = true;
        if (dataEmissao == null) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "A data de emissão deve ser informada");
        }
        if (exercicio == null || exercicio.getId() == null) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "O Exercicio deve ser informado");
        }
        if (selecionado.getVaraCivel() == null) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe a Vara Cível");
        }
        if (certidaoSelecionada == null || certidaoSelecionada.length < 1) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Selecione uma petição para ser emitida.");
        }
        return retorno;
    }

    public List<ParametrosPeticao> getListaParametros() {
        return peticaoFacade.listaParametros();
    }


    @URLAction(mappingId = "editarParametroPeticao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarParametro() {
        operacao = Operacoes.EDITAR;
        parametrosPeticao = (ParametrosPeticao) Web.pegaDaSessao(ParametrosPeticao.class);
        if (parametrosPeticao == null) {
            parametrosPeticao = (ParametrosPeticao) peticaoFacade.recuperarParametrosPeticao(super.getId());
        }
    }


    @URLAction(mappingId = "novoParametroPeticao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoParametro() {
        try {
            operacao = Operacoes.NOVO;
            parametrosPeticao = (ParametrosPeticao) Web.pegaDaSessao(ParametrosPeticao.class);
            if (parametrosPeticao == null) {
                parametrosPeticao = new ParametrosPeticao();
            }
        } catch (Exception e) {
            FacesUtil.addError("Houve um erro inesperado!", "Não foi possível criar um(a) " + parametrosPeticao.getClass().getSimpleName());
            logger.error("Erro: ", e);
        }
    }

    public ModuloTipoDoctoOficial getModulo() {
        return ModuloTipoDoctoOficial.PETICAODIVIDAATIVA;
    }

    public TipoCadastroDoctoOficial getTipoPessoaFisica() {
        return TipoCadastroDoctoOficial.PESSOAFISICA;
    }

    public TipoCadastroDoctoOficial getTipoPessoaJuridica() {
        return TipoCadastroDoctoOficial.PESSOAJURIDICA;
    }

    public TipoCadastroDoctoOficial getTipoImobiliario() {
        return TipoCadastroDoctoOficial.CADASTROIMOBILIARIO;
    }

    public TipoCadastroDoctoOficial getTipoEconomico() {
        return TipoCadastroDoctoOficial.CADASTROECONOMICO;
    }

    public TipoCadastroDoctoOficial getTipoRural() {
        return TipoCadastroDoctoOficial.CADASTRORURAL;
    }

    public String salvarParametro() {
        try {
            if (validaCamposParametro()) {
                peticaoFacade.salvar(parametrosPeticao);
                FacesUtil.addInfo("Salvo com sucesso!", "");
            } else {
                return "";
            }
        } catch (Exception e) {
            FacesUtil.addError("Erro ao salvar", "ocorreu um erro ao salvar " + e.getMessage() + e.getCause());
            return "";
        }
        return "";
    }


    public String getCaminho() {
        return caminho;
    }

    public String caminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public void limpaFiltros() {
        if (geradoPor.equals(GeradoPor.NUMERO_CADASTRO)) {
            pessoa = null;
            certidaoInicial = null;
            certidaoFinal = null;
        }
        if (geradoPor.equals(GeradoPor.CONTRIBUINTE)) {
            cadastroInicial = null;
            cadastroFinal = null;
            certidaoInicial = null;
            certidaoFinal = null;
        }
        if (geradoPor.equals(GeradoPor.NUMERO_CERTIDAO)) {
            pessoa = null;
            cadastroInicial = null;
            cadastroFinal = null;
        }
    }

    public BigDecimal recuperaValorTotalCDA(CertidaoDividaAtiva certidao) {
        return peticaoFacade.getValorTotalCertidao(certidao);
    }

    public void copiarCadastroInicialParaCadastroFinal() {
        this.cadastroFinal = this.cadastroInicial;
    }

    public void novoTipoDoctoOficialPF() {
        Web.navegacao("/tributario/peticao/parametros/novo/", "/tipo-de-documento-oficial/novo-pessoa-fisica/", parametrosPeticao);
    }

    public void novoTipoDoctoOficialPJ() {
        Web.navegacao("/tributario/peticao/parametros/novo/", "/tipo-de-documento-oficial/novo-pessoa-juridica/", parametrosPeticao);
    }

    public void novoTipoDoctoOficialCadastroImobiliario() {
        Web.navegacao("/tributario/peticao/parametros/novo/", "/tipo-de-documento-oficial/novo-cadastro-imobiliario/", parametrosPeticao);
    }

    public void novoTipoDoctoOficialCadastroEconomico() {
        Web.navegacao("/tributario/peticao/parametros/novo/", "/tipo-de-documento-oficial/novo-cadastro-economico/", parametrosPeticao);
    }

    public void novoTipoDoctoOficialCadastroRural() {
        Web.navegacao("/tributario/peticao/parametros/novo/", "/tipo-de-documento-oficial/novo-cadastro-rural/", parametrosPeticao);
    }

    public String redirecionaCancelarParametro() {
        return "lista";
    }
}




