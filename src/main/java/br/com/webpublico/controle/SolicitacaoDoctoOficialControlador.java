/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ImprimeDemonstrativoDebitos;
import br.com.webpublico.entidadesauxiliares.ValidacaoCertidaoDoctoOficial;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.viewobjects.DataTableSolicitacaoDoctoOficial;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

@ManagedBean(name = "solicitacaoDoctoOficialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaSolicitacaoDoctoOficial", pattern = "/solicitacao-documento-oficial/novo/", viewId = "/faces/tributario/certidao/solicitacaodocumento/edita.xhtml"),
    @URLMapping(id = "editarSolicitacaoDoctoOficial", pattern = "/solicitacao-documento-oficial/editar/#{solicitacaoDoctoOficialControlador.id}/", viewId = "/faces/tributario/certidao/solicitacaodocumento/edita.xhtml"),
    @URLMapping(id = "listarSolicitacaoDoctoOficial", pattern = "/solicitacao-documento-oficial/listar/", viewId = "/faces/tributario/certidao/solicitacaodocumento/lista.xhtml"),
    @URLMapping(id = "verSolicitacaoDoctoOficial", pattern = "/solicitacao-documento-oficial/ver/#{solicitacaoDoctoOficialControlador.id}/", viewId = "/faces/tributario/certidao/solicitacaodocumento/visualizar.xhtml")
})
public class SolicitacaoDoctoOficialControlador extends PrettyControlador<SolicitacaoDoctoOficial> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(SolicitacaoDoctoOficialControlador.class);

    @EJB
    private SolicitacaoDoctoOficialFacade solicitacaoDoctoOficialFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    private ConverterAutoComplete converterTipoDoctoOficial;
    private ConverterAutoComplete converterCadastroImobiliario;
    private ConverterAutoComplete converterCadastroEconomico;
    private ConverterAutoComplete converterCadastroRural;
    private ConverterAutoComplete converterPessoaFisica;
    private ConverterAutoComplete converterPessoaJuridica;
    private ConverterAutoComplete converterFinalidade;
    private List<AtributoDoctoOficial> atributosPorTipoDocto;
    private DocumentoOficial documentoOficial;
    private Testada testada;
    private Lote lote;
    private List<Propriedade> propriedades;
    private List<PropriedadeRural> propriedadesRurais;
    private SituacaoCadastroEconomico situacaoCadastroEconomico;
    private List<ValorAtributoSolicitacao> valorAtributoSolicitacoes;
    private UsuarioSistema usuarioSistema;
    private ValidacaoCertidaoDoctoOficial validacaoCertidao;
    private List<ParcelaValorDivida> listaDebitos;
    private String numeroInicial, numeroFinal, cadastroInicial, cadastroFinal;
    private Date dataInicial, dataFinal;
    private TipoCadastroTributario tipoCadastro;
    private TipoDoctoOficial tipoDoctoOficial;
    private Pessoa pessoa;
    private String motivoSolicitacao;
    private String loginAutorizacao;
    private String senhaAutorizacao;
    private List<TipoDoctoOficial> tipoDoctosOficiais;
    private List<ResultadoParcela> resultadoConsulta;
    private String emails;
    private String mensagemEmail;
    private Boolean permiteImpressaoUsuario;
    private Boolean habilitaImpressaoDAM;

    public SolicitacaoDoctoOficialControlador() {
        super(SolicitacaoDoctoOficial.class);
        resultadoConsulta = Lists.newArrayList();
    }

    public List<ResultadoParcela> getResultadoConsulta() {
        return resultadoConsulta;
    }

    public TipoDoctoOficial getTipoDoctoOficial() {
        return tipoDoctoOficial;
    }

    public void setTipoDoctoOficial(TipoDoctoOficial tipoDoctoOficial) {
        this.tipoDoctoOficial = tipoDoctoOficial;
    }

    public TipoCadastroTributario getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastroTributario tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public String getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public String getCadastroInicial() {
        return cadastroInicial;
    }

    public void setCadastroInicial(String cadastroIncial) {
        this.cadastroInicial = cadastroIncial;
    }

    public String getCadastroFinal() {
        return cadastroFinal;
    }

    public void setCadastroFinal(String cadastroFinal) {
        this.cadastroFinal = cadastroFinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataIncial) {
        this.dataInicial = dataIncial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public boolean getDocumentoValido() {
        if (validacaoCertidao != null) {
            return validacaoCertidao.getValido();
        }
        return false;
    }

    public ValidacaoCertidaoDoctoOficial getValidacaoCertidao() {
        return validacaoCertidao;
    }

    public List<ParcelaValorDivida> getListaDebitos() {
        return listaDebitos;
    }

    public void setListaDebitos(List<ParcelaValorDivida> listaDebitos) {
        this.listaDebitos = listaDebitos;
    }

    public List<ValorAtributoSolicitacao> getValorAtributoSolicitacoes() {
        return valorAtributoSolicitacoes;
    }

    public void setValorAtributoSolicitacoes(List<ValorAtributoSolicitacao> valorAtributoSolicitacoes) {
        this.valorAtributoSolicitacoes = valorAtributoSolicitacoes;
    }

    public SituacaoCadastroEconomico getSituacaoCadastroEconomico() {
        return situacaoCadastroEconomico;
    }

    public void setSituacaoCadastroEconomico(SituacaoCadastroEconomico situacaoCadastroEconomico) {
        this.situacaoCadastroEconomico = situacaoCadastroEconomico;
    }

    public List<PropriedadeRural> getPropriedadesRurais() {
        return propriedadesRurais;
    }

    public void setPropriedadesRurais(List<PropriedadeRural> propriedadesRurais) {
        this.propriedadesRurais = propriedadesRurais;
    }

    public List<Propriedade> getPropriedades() {
        return propriedades;
    }

    public void setPropriedades(List<Propriedade> propriedades) {
        this.propriedades = propriedades;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public Testada getTestada() {
        return testada;
    }

    public void setTestada(Testada testada) {
        this.testada = testada;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-documento-oficial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return solicitacaoDoctoOficialFacade;
    }

    @URLAction(mappingId = "novaSolicitacaoDoctoOficial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setSituacaoSolicitacao(SituacaoSolicitacao.ABERTO);
        selecionado.setDataSolicitacao(new Date());
        atributosPorTipoDocto = Lists.newArrayList();
        propriedades = Lists.newArrayList();
        propriedadesRurais = Lists.newArrayList();
        valorAtributoSolicitacoes = Lists.newArrayList();
        validacaoCertidao = new ValidacaoCertidaoDoctoOficial(true);
    }


    public List<SituacaoCadastralCadastroEconomico> getSituacoesDisponiveis() {
        List<SituacaoCadastralCadastroEconomico> situacoes = new ArrayList<>();
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
        return situacoes;
    }


    public void recuperaAtributosPorSolicitacao() {
        valorAtributoSolicitacoes = solicitacaoDoctoOficialFacade.recuperaAtributosPorSolicitacao(selecionado);
    }

    public void recuperaCadastroEconomicoSolicitacao() {
        CadastroEconomico ce = solicitacaoDoctoOficialFacade.recuperarCadastroEconomico(selecionado);
        selecionado.setCadastroEconomico(ce);
        situacaoCadastroEconomico = selecionado.getCadastroEconomico().getSituacaoAtual();
    }

    public void recuperaCadastroImobiliarioSolicitacao() {
        CadastroImobiliario ci = solicitacaoDoctoOficialFacade.recuperarCadastroImobiliario(selecionado);
        selecionado.setCadastroImobiliario(ci);
        if (selecionado.getDataSolicitacao() != null) {
            propriedades = selecionado.getCadastroImobiliario().getPropriedadeVigenteNaData(selecionado.getDataSolicitacao());
        } else {
            propriedades = selecionado.getCadastroImobiliario().getPropriedadeVigente();
        }
        if (selecionado.getCadastroImobiliario() != null) {
            lote = solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getLoteFacade().recuperar(selecionado.getCadastroImobiliario().getLote().getId());
            testada = solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getLoteFacade().recuperarTestadaPrincipal(lote);
            if (testada != null) {
                testada.getFace().getLogradouroBairro();
            }
        }
    }

    public void recuperaCadastroRuralSolicitacao() {
        CadastroRural cr = solicitacaoDoctoOficialFacade.recuperarCadastroRural(selecionado);
        selecionado.setCadastroRural(cr);
        if (selecionado.getDataSolicitacao() != null) {
            propriedadesRurais = selecionado.getCadastroRural().getPropriedadesVigenteNaData(selecionado.getDataSolicitacao());
        } else {
            propriedadesRurais = selecionado.getCadastroRural().getPropriedadesAtuais();
        }
    }

    public void recuperaPessoaFisicaSolicitacao() {
        PessoaFisica pf = solicitacaoDoctoOficialFacade.recuperarPessoaFisica(selecionado);
        selecionado.setPessoaFisica(pf);
    }

    public void recuperaPessoaJuridicaSolicitacao() {
        PessoaJuridica pj = solicitacaoDoctoOficialFacade.recuperarPessoaJuridica(selecionado);
        selecionado.setPessoaJuridica(pj);
    }

    @URLAction(mappingId = "editarSolicitacaoDoctoOficial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        CalculoDoctoOficial calculo = recuperarCalculo();
        if (calculo != null) {
            pesquisar(calculo);
        }
        propriedades = Lists.newArrayList();
        propriedadesRurais = Lists.newArrayList();
        selecionado.setTipoDoctoOficial(solicitacaoDoctoOficialFacade.getTipoDoctoOficialFacade().recuperar(selecionado.getTipoDoctoOficial().getId()));
        usuarioSistema = getSistemaControlador().getUsuarioCorrente();
        if (selecionado.getCadastroEconomico() != null) {
            recuperaCadastroEconomicoSolicitacao();
        } else if (selecionado.getCadastroImobiliario() != null) {
            recuperaCadastroImobiliarioSolicitacao();
        } else if (selecionado.getCadastroRural() != null) {
            recuperaCadastroRuralSolicitacao();
        } else if (selecionado.getPessoaFisica() != null) {
            recuperaPessoaFisicaSolicitacao();
        } else if (selecionado.getPessoaJuridica() != null) {
            recuperaPessoaJuridicaSolicitacao();
        }
        recuperaAtributosPorSolicitacao();
    }

    @URLAction(mappingId = "verSolicitacaoDoctoOficial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            selecionado.setUsuarioSistema(this.usuarioSistema);
            if (selecionado.getId() == null) {
                selecionado.setDocumentoOficial(solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().geraDocumentoEmBranco(selecionado.getCadastro(), selecionado.getPessoa(), selecionado.getTipoDoctoOficial()));
            }
            selecionado = solicitacaoDoctoOficialFacade.salvarSolicitacao(selecionado);
            solicitacaoDoctoOficialFacade.calculaValorCertidao(selecionado);
            FacesUtil.addInfo("Salvo com sucesso!", "");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addFatal("Exceção do sistema!", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void excluir() {
        try {
            if (SituacaoSolicitacao.NAONECESSITAPAGAMENTO.getDescricao().equals(selecionado.getSituacaoSolicitacao())) {
                super.excluir();
            } else {
                FacesUtil.addError("Não foi possível continuar!", "O registro não pode ser excluído porque há um débito relacionado.");
            }
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível continuar!", "Este registro não pode ser excluído porque possui dependências.");
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataSolicitacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Data de Solicitação.");
        }
        if (selecionado.getTipoDoctoOficial() == null || selecionado.getTipoDoctoOficial().getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo de Documento.");
        } else {
            if (TipoCadastroDoctoOficial.CADASTROECONOMICO.equals(selecionado.getTipoDoctoOficial().getTipoCadastroDoctoOficial())
                && (selecionado.getCadastroEconomico() == null || selecionado.getCadastroEconomico().getId() == null)) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o Cadastro Econômico.");
            } else if (TipoCadastroDoctoOficial.CADASTROIMOBILIARIO.equals(selecionado.getTipoDoctoOficial().getTipoCadastroDoctoOficial())
                && (selecionado.getCadastroImobiliario() == null || selecionado.getCadastroImobiliario().getId() == null)) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o Cadastro Imobiliário.");
            } else if (TipoCadastroDoctoOficial.CADASTRORURAL.equals(selecionado.getTipoDoctoOficial().getTipoCadastroDoctoOficial())
                && (selecionado.getCadastroRural() == null || selecionado.getCadastroRural().getId() == null)) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o Cadastro Rural.");
            } else if (TipoCadastroDoctoOficial.PESSOAFISICA.equals(selecionado.getTipoDoctoOficial().getTipoCadastroDoctoOficial())
                && (selecionado.getPessoaFisica() == null || selecionado.getPessoaFisica().getId() == null)) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe a Pessoa Física.");
            } else if (TipoCadastroDoctoOficial.PESSOAJURIDICA.equals(selecionado.getTipoDoctoOficial().getTipoCadastroDoctoOficial())
                && (selecionado.getPessoaJuridica() == null || selecionado.getPessoaJuridica().getId() == null)) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe a Pessoa Jurídica.");
            } else if (solicitacaoDoctoOficialFacade.existeCertidaoVigente(selecionado)) {
                ve.adicionarMensagemDeCampoObrigatorio("Já existe uma " + selecionado.getTipoDoctoOficial().getDescricao() + " válida para o cadastro informado.");
            } else {
                for (ValorAtributoSolicitacao va : selecionado.getValoresAtributosSolicitacao()) {
                    if (va.getObrigatorio() && ("".equals(va.getValor()))) {
                        String msgAtributos = "O campo " + va.getAtributoDoctoOficial().getCampo() + " deve ser preenchido!";
                        ve.adicionarMensagemDeCampoObrigatorio(msgAtributos);
                    }
                }
            }
        }
        ve.lancarException();
    }

    public Converter getConverterTipoDoctoOficial() {
        if (converterTipoDoctoOficial == null) {
            converterTipoDoctoOficial = new ConverterAutoComplete(TipoDoctoOficial.class, solicitacaoDoctoOficialFacade.getTipoDoctoOficialFacade());
        }
        return converterTipoDoctoOficial;
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficial(String parte) {
        usuarioSistema = getSistemaControlador().getUsuarioCorrente();
        return solicitacaoDoctoOficialFacade.getTipoDoctoOficialFacade().tipoDoctoPorUsuario(parte.trim(), usuarioSistema);
    }

    public List<SelectItem> completaTipoDoctoOficial() {
        List<SelectItem> lista = Lists.newArrayList();
        if (tipoDoctosOficiais == null) {
            usuarioSistema = getSistemaControlador().getUsuarioCorrente();
            tipoDoctosOficiais = solicitacaoDoctoOficialFacade.getTipoDoctoOficialFacade().tipoDoctoPorUsuario(usuarioSistema);
        }
        lista.add(new SelectItem(null, ""));
        for (TipoDoctoOficial tipoDocto : tipoDoctosOficiais) {
            lista.add(new SelectItem(tipoDocto, tipoDocto.getDescricao() + " - " + tipoDocto.getTipoCadastroDoctoOficial().getDescricao()));
        }
        return lista;
    }

    public Converter getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getCadastroImobiliarioFacade());
        }
        return converterCadastroImobiliario;
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getCadastroImobiliarioFacade().listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public Converter getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getCadastroEconomicoFacade());
        }
        return converterCadastroEconomico;
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getCadastroEconomicoFacade().buscarCadastrosPorInscricaoOrCpfCnpjOrNome(parte.trim());
    }

    public Converter getConverterCadastroRural() {
        if (converterCadastroRural == null) {
            converterCadastroRural = new ConverterAutoComplete(CadastroRural.class, solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getCadastroRuralFacade());
        }
        return converterCadastroRural;
    }

    public List<CadastroRural> completaCadastroRural(String parte) {
        return solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getCadastroRuralFacade().listaFiltrando(parte.trim(), "nomePropriedade");
    }

    public Converter getConverterPessoaFisica() {
        if (converterPessoaFisica == null) {
            converterPessoaFisica = new ConverterAutoComplete(PessoaFisica.class, solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getPessoaFisicaFacade());
        }
        return converterPessoaFisica;
    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        return solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getPessoaFisicaFacade().listaFiltrandoTodasPessoasByNomeAndCpf(parte.trim());
    }

    public Converter getConverterPessoaJuridica() {
        if (converterPessoaJuridica == null) {
            converterPessoaJuridica = new ConverterAutoComplete(PessoaJuridica.class, solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getPessoaJuridicaFacade());
        }
        return converterPessoaJuridica;
    }

    public List<PessoaJuridica> completaPessoaJuridica(String parte) {
        return solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getPessoaJuridicaFacade().listaFiltrandoRazaoSocialCNPJ(parte.trim());
    }

    public Converter getConverterFinalidade() {
        if (converterFinalidade == null) {
            converterFinalidade = new ConverterAutoComplete(Finalidade.class, solicitacaoDoctoOficialFacade.getFinalidadeFacade());
        }
        return converterFinalidade;
    }

    public List<Finalidade> completaFinalidadePeloTipoDocto(String parte) {
        return solicitacaoDoctoOficialFacade.getTipoDoctoOficialFacade().listaFinalidadePeloTipoDocto(selecionado.getTipoDoctoOficial(), parte.trim());
    }

    public String calculaSituacaoSolicitacaoComEvento(DataTableSolicitacaoDoctoOficial objeto) {
        SolicitacaoDoctoOficial solicitacao = this.solicitacaoDoctoOficialFacade.recuperar(objeto.getId());
        SituacaoSolicitacao situacao;
        if (solicitacao.getTipoDoctoOficial().getValor() != null && solicitacao.getTipoDoctoOficial().getValor().compareTo(BigDecimal.ZERO) > 0) {
            if (solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getCalculoDoctoOficialFacade().parcelaPaga(solicitacao)) {
                situacao = SituacaoSolicitacao.PAGO;
            } else {
                situacao = SituacaoSolicitacao.AGUARDANDOPAGAMENTO;
            }
        } else {
            situacao = SituacaoSolicitacao.NAONECESSITAPAGAMENTO;
        }
        return situacao.getDescricao();
    }

    public void recuperaTipoDocto() {
        selecionado.setCadastroEconomico(null);
        selecionado.setCadastroImobiliario(null);
        selecionado.setCadastroRural(null);
        selecionado.setPessoaFisica(null);
        selecionado.setPessoaJuridica(null);
        recuperaAtributoPorTipoDocto();
        validacaoCertidao = new ValidacaoCertidaoDoctoOficial(true);
    }

    public void recuperaAtributoPorTipoDocto() {
        selecionado.getValoresAtributosSolicitacao().clear();
        this.atributosPorTipoDocto.clear();

        this.atributosPorTipoDocto.addAll(solicitacaoDoctoOficialFacade.getTipoDoctoOficialFacade().recuperaAtributoPorTipoDocto(selecionado.getTipoDoctoOficial()));
        ValorAtributoSolicitacao vlrAtt = null;
        for (AtributoDoctoOficial atributo : atributosPorTipoDocto) {
            if (atributo.getAtivo() != null && atributo.getAtivo()) {
                vlrAtt = new ValorAtributoSolicitacao();
                vlrAtt.setAtributoDoctoOficial(atributo);
                vlrAtt.setObrigatorio(atributo.getObrigatorio());
                vlrAtt.setSolicitacaoDoctoOficial(selecionado);
                selecionado.getValoresAtributosSolicitacao().add(vlrAtt);
            }
        }
    }

    public void imprimeDam() {
        CalculoDoctoOficial calculo = solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getCalculoDoctoOficialFacade().recuperaCalculo(selecionado);
        if (calculo != null) {
            try {
                geraGuiaPagamento(calculo);
            } catch (Exception ex) {
                FacesUtil.addError("Ocorreu um erro ao imprimir o DAM!", ex.getMessage());
                logger.error("Erro: ", ex);
            }
        } else {
            FacesUtil.addError("Não é possível Imprimir o DAM!", "Não foi encontrado o calculo para a solicitação!");
        }
    }

    public void geraGuiaPagamento(CalculoDoctoOficial calculo) throws JRException, IOException {
        try {
            ValorDivida valorDivida = solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getCalculoDoctoOficialFacade().retornaValorDividaDoCalculo(calculo);
            Date vencimento = valorDivida.getParcelaValorDividas().get(0).getVencimento();
            DAM dam = solicitacaoDoctoOficialFacade.getDamFacade().recuperaDAMpeloCalculo(calculo);
            if (dam == null || vencimento.before(new Date())) {
                dam = solicitacaoDoctoOficialFacade.getDamFacade().geraDAM(valorDivida.getParcelaValorDividas().get(0));
            }
            new ImprimeDAM().imprimirDamUnicoViaApi(dam);
        } catch (Exception ex) {
            FacesUtil.addError("Não foi possível continuar!", ex.getMessage());
        }
    }

    private CalculoDoctoOficial recuperarCalculo() {
        return solicitacaoDoctoOficialFacade.retornaCalculoDaSolicitacao(selecionado);
    }

    public boolean desabilitarImpressaoCertidao() {
        if (!permiteImpressaoUsuario()) return true;

        if (resultadoConsulta.isEmpty()) {
            return false;
        } else {
            for (ResultadoParcela parcela : resultadoConsulta) {
                if (parcela.isPago()) {
                    return false;
                }
            }
            return true;
        }

    }

    public boolean habilitaImpressaoDAM() {
        if (habilitaImpressaoDAM == null) {
            habilitaImpressaoDAM = false;
            CalculoDoctoOficial calculo = recuperarCalculo();
            if (calculo != null) {
                pesquisar(calculo);
                for (ResultadoParcela parcela : resultadoConsulta) {
                    if (parcela.getSituacao().equals(SituacaoParcela.EM_ABERTO.name())) {
                        habilitaImpressaoDAM = true;
                        break;
                    }
                }
            }
        }
        return habilitaImpressaoDAM;
    }


    public void pesquisar(CalculoDoctoOficial calculo) {
        resultadoConsulta.clear();
        ConsultaParcela consulta = new ConsultaParcela();
        adicionaParametro(consulta, calculo);
        consulta.executaConsulta();
        resultadoConsulta.addAll(consulta.getResultados());
    }

    public void adicionaParametro(ConsultaParcela consulta, CalculoDoctoOficial calculo) {
        consulta.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculo.getId());
    }

    public boolean isCadastroEconomico() {
        if (selecionado.getTipoDoctoOficial() != null) {
            return TipoCadastroDoctoOficial.CADASTROECONOMICO.equals(selecionado.getTipoDoctoOficial().getTipoCadastroDoctoOficial());
        }
        return false;
    }

    public boolean isCadastroImobiliario() {
        if (selecionado.getTipoDoctoOficial() != null) {
            return TipoCadastroDoctoOficial.CADASTROIMOBILIARIO.equals(selecionado.getTipoDoctoOficial().getTipoCadastroDoctoOficial());
        }
        return false;
    }

    public boolean isCadastroRural() {
        if (selecionado.getTipoDoctoOficial() != null) {
            return TipoCadastroDoctoOficial.CADASTRORURAL.equals(selecionado.getTipoDoctoOficial().getTipoCadastroDoctoOficial());
        }
        return false;
    }

    public boolean isPessoaFisica() {
        if (selecionado.getTipoDoctoOficial() != null) {
            return TipoCadastroDoctoOficial.PESSOAFISICA.equals(selecionado.getTipoDoctoOficial().getTipoCadastroDoctoOficial());
        }
        return false;
    }

    public boolean isPessoaJuridica() {
        if (selecionado.getTipoDoctoOficial() != null) {
            return TipoCadastroDoctoOficial.PESSOAJURIDICA.equals(selecionado.getTipoDoctoOficial().getTipoCadastroDoctoOficial());
        }
        return false;
    }

    public boolean permiteImpressaoUsuario() {
        if (permiteImpressaoUsuario == null) {
            UsuarioSistema usuarioSistema = getSistemaControlador().getUsuarioCorrente();
            selecionado.setTipoDoctoOficial(solicitacaoDoctoOficialFacade.getTipoDoctoOficialFacade().recuperar(selecionado.getTipoDoctoOficial().getId()));
            for (UsuarioTipoDocto utd : selecionado.getTipoDoctoOficial().getListaUsuarioTipoDocto()) {
                if (utd.getUsuarioSistema().equals(usuarioSistema)) {
                    permiteImpressaoUsuario = true;
                    break;
                }
            }
        }
        if (permiteImpressaoUsuario == null) {
            permiteImpressaoUsuario = false;
        }
        return permiteImpressaoUsuario;
    }

    private boolean podeGerarDocumento() {
        if (selecionado.getSituacaoSolicitacao() == null) {
            return true;
        }
        if (selecionado.getSituacaoSolicitacao().equals(SituacaoSolicitacao.CANCELADO)) {
            FacesUtil.addOperacaoNaoPermitida("A Solicitação de Documento Oficial está Cancelada, não é possível ser impresso.");
            return false;
        }
        return true;
    }

    public void gerarDocumento() {
        if (podeGerarDocumento()) {
            selecionado.getImpressaoDoctoOficial().add(criaImpressaoDoctoOficial(selecionado));
            try {
                selecionado.setDocumentoOficial(solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().gerarDocumentoSolicitacao(selecionado, selecionado.getDocumentoOficial(), selecionado.getCadastro(), selecionado.getPessoa(), selecionado.getTipoDoctoOficial(), getSistemaControlador()));
                if (!SituacaoSolicitacao.EMITIDO.equals(selecionado.getSituacaoSolicitacao())) {
                    selecionado.setSituacaoSolicitacao(SituacaoSolicitacao.EMITIDO);
                }
                solicitacaoDoctoOficialFacade.salvar(selecionado);
            } catch (Exception ex) {
                FacesUtil.addError("Impossível continuar", ex.getMessage());
                logger.error("Erro: ", ex);
            }
        }
    }

    public void recuperaCadastroRural(SelectEvent evento) {
        selecionado.setCadastroRural((CadastroRural) evento.getObject());
        carregarCadastroRural();
    }

    private void carregarCadastroRural() {
        try {
            validacaoCertidao = solicitacaoDoctoOficialFacade.documentoValido(selecionado);
            if (selecionado.getTipoDoctoOficial() != null) {
                if (TipoValidacaoDoctoOficial.CERTIDAONEGATIVA.equals(selecionado.getTipoDoctoOficial().getTipoValidacaoDoctoOficial())) {
                    if (!validacaoCertidao.getValido() && temPermissaoParaImprimirNaoPago()) {
                        FacesUtil.executaJavaScript("login.show()");
                    }
                }
            }
        } catch (Exception ex) {
            FacesUtil.addError("Impossível continuar", ex.getMessage());
            logger.error("Erro: ", ex);
            validacaoCertidao.setValido(false);
        }
        selecionado.setCadastroRural(solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getCadastroRuralFacade().recuperar(selecionado.getCadastroRural().getId()));
        propriedadesRurais = selecionado.getCadastroRural().getPropriedade();
    }

    public void recuperaCadastroEconomico(SelectEvent evento) {
        selecionado.setCadastroEconomico((CadastroEconomico) evento.getObject());
        carregarCadastroEconomico();
    }

    public void carregarCadastroEconomico() {
        try {
            validacaoCertidao = solicitacaoDoctoOficialFacade.documentoValido(selecionado);
            if (selecionado.getTipoDoctoOficial() != null) {
                if (TipoValidacaoDoctoOficial.CERTIDAONEGATIVA.equals(selecionado.getTipoDoctoOficial().getTipoValidacaoDoctoOficial())) {
                    if (!validacaoCertidao.getValido() && temPermissaoParaImprimirNaoPago()) {
                        FacesUtil.executaJavaScript("login.show()");
                    }
                }
            }
        } catch (Exception ex) {
            FacesUtil.addError("Impossível continuar", ex.getMessage());
            logger.error("Erro: ", ex);
            validacaoCertidao.setValido(false);
        }
        selecionado.setCadastroEconomico(solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getCadastroEconomicoFacade().recuperar(selecionado.getCadastroEconomico().getId()));
        situacaoCadastroEconomico = selecionado.getCadastroEconomico().getSituacaoAtual();
        FacesUtil.atualizarComponente("Formulario");
    }

    public void carregarPessoaJuridica() {
        try {
            validacaoCertidao = solicitacaoDoctoOficialFacade.documentoValido(selecionado);
            if (selecionado.getTipoDoctoOficial() != null) {
                if (TipoValidacaoDoctoOficial.CERTIDAONEGATIVA.equals(selecionado.getTipoDoctoOficial().getTipoValidacaoDoctoOficial())) {
                    if (!validacaoCertidao.getValido() && temPermissaoParaImprimirNaoPago()) {
                        FacesUtil.executaJavaScript("login.show()");
                    }
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            validacaoCertidao.setValido(false);
        } catch (Exception ex) {
            FacesUtil.addError("Impossível continuar", ex.getMessage());
            logger.error("Erro: ", ex);
            validacaoCertidao.setValido(false);
        }
        selecionado.setPessoaJuridica(solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getPessoaJuridicaFacade().recuperar(selecionado.getPessoaJuridica().getId()));
        FacesUtil.atualizarComponente("Formulario");
    }

    public void carregarPessoaFisica() {
        try {
            validacaoCertidao = solicitacaoDoctoOficialFacade.documentoValido(selecionado);
            if (selecionado.getTipoDoctoOficial() != null) {
                if (TipoValidacaoDoctoOficial.CERTIDAONEGATIVA.equals(selecionado.getTipoDoctoOficial().getTipoValidacaoDoctoOficial())) {
                    if (!validacaoCertidao.getValido() && temPermissaoParaImprimirNaoPago()) {
                        FacesUtil.executaJavaScript("login.show()");
                    }
                }
            }
            FacesUtil.atualizarComponente("Formulario");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            validacaoCertidao.setValido(false);
        } catch (Exception ex) {
            FacesUtil.addError("Impossível continuar", ex.getMessage());
            logger.error("Erro: ", ex);
            validacaoCertidao.setValido(false);
        }
        selecionado.setPessoaFisica(solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getPessoaFisicaFacade().recuperar(selecionado.getPessoaFisica().getId()));
        FacesUtil.atualizarComponente("Formulario");
    }

    public void recuperaCadastroImobiliario(SelectEvent evento) {
        selecionado.setCadastroImobiliario((CadastroImobiliario) evento.getObject());
        carregarCadastroImobiliario();
    }

    private void carregarCadastroImobiliario() {
        try {
            validacaoCertidao = solicitacaoDoctoOficialFacade.documentoValido(selecionado);
            if (selecionado.getTipoDoctoOficial() != null) {
                if (TipoValidacaoDoctoOficial.CERTIDAONEGATIVA.equals(selecionado.getTipoDoctoOficial().getTipoValidacaoDoctoOficial())) {
                    if (!validacaoCertidao.getValido() && temPermissaoParaImprimirNaoPago()) {
                        FacesUtil.executaJavaScript("login.show()");
                    }
                }
            }
        } catch (Exception ex) {
            FacesUtil.addError("Impossível continuar", ex.getMessage());
            logger.error("Erro: ", ex);
            validacaoCertidao.setValido(false);
        }
        selecionado.setCadastroImobiliario(solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getCadastroImobiliarioFacade().recuperar(selecionado.getCadastroImobiliario().getId()));
        propriedades = selecionado.getCadastroImobiliario().getPropriedadeVigente();
        if (selecionado.getCadastroImobiliario() != null) {
            lote = solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getLoteFacade().recuperar(selecionado.getCadastroImobiliario().getLote().getId());
            testada = solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().getLoteFacade().recuperarTestadaPrincipal(lote);
            if (testada != null) {
                testada.getFace().getLogradouroBairro();
            }
        }
    }

    private ImpressaoDoctoOficial criaImpressaoDoctoOficial(SolicitacaoDoctoOficial selecionado) {
        ImpressaoDoctoOficial impressao = new ImpressaoDoctoOficial();
        impressao.setDataImpressao(new Date());
        impressao.setUsuarioSistema(this.usuarioSistema);
        impressao.setSolicitacaoDoctoOficial(selecionado);
        return impressao;
    }

    public String getMotivoSolicitacao() {
        return motivoSolicitacao;
    }

    public void setMotivoSolicitacao(String motivoSolicitacao) {
        this.motivoSolicitacao = motivoSolicitacao;
    }

    public String getLoginAutorizacao() {
        return loginAutorizacao;
    }

    public void setLoginAutorizacao(String loginAutorizacao) {
        this.loginAutorizacao = loginAutorizacao;
    }

    public String getSenhaAutorizacao() {
        return senhaAutorizacao;
    }

    public void setSenhaAutorizacao(String senhaAutorizacao) {
        this.senhaAutorizacao = senhaAutorizacao;
    }

    public void setaNuloLoginSenha() {
        this.loginAutorizacao = "";
        this.senhaAutorizacao = "";
        this.motivoSolicitacao = "";
    }

    public boolean temPermissaoParaImprimirNaoPago() {
        return solicitacaoDoctoOficialFacade.getUsuarioSistemaFacade().validaAutorizacaoUsuario(getSistemaControlador().getUsuarioCorrente(), AutorizacaoTributario.SOLICITAR_CERTIDAO_NEGATIVA_COM_DEBITO);
    }

    public void logar() {
        if (motivoSolicitacao == null || "".equals(motivoSolicitacao)) {
            FacesUtil.addError("Atenção!", "Informe o motivo da solicitação da CND com débito!");
        } else if (solicitacaoDoctoOficialFacade.getUsuarioSistemaFacade().validaLoginSenha(loginAutorizacao, senhaAutorizacao)) {
            UsuarioSistema usuario = solicitacaoDoctoOficialFacade.getUsuarioSistemaFacade().findOneByLogin(loginAutorizacao);
            if (solicitacaoDoctoOficialFacade.getUsuarioSistemaFacade().validaAutorizacaoUsuario(usuario, AutorizacaoTributario.SOLICITAR_CERTIDAO_NEGATIVA_COM_DEBITO)) {
                FacesUtil.executaJavaScript("login.hide()");
                validacaoCertidao.setValido(true);
            } else {
                FacesUtil.executaJavaScript("login.hide()");
                FacesUtil.addError("Erro", "O usuário informado não possui autorização para realizar este procedimento.");
                setaNuloLoginSenha();
            }
        } else {
            FacesUtil.executaJavaScript("login.hide()");
            FacesUtil.addError("Erro", "Usuário ou senha incorretos.");
            setaNuloLoginSenha();
        }
    }

    public String getNomeClasse() {
        if (selecionado.getTipoDoctoOficial() != null && selecionado.getTipoDoctoOficial().getTipoCadastroDoctoOficial() != null) {
            switch (selecionado.getTipoDoctoOficial().getTipoCadastroDoctoOficial()) {
                case CADASTROIMOBILIARIO:
                    return CadastroImobiliario.class.getSimpleName();
                case CADASTROECONOMICO:
                    return CadastroEconomico.class.getSimpleName();
                case CADASTRORURAL:
                    return CadastroRural.class.getSimpleName();
                case PESSOAFISICA:
                    return PessoaFisica.class.getSimpleName();
                case PESSOAJURIDICA:
                    return PessoaJuridica.class.getSimpleName();
            }
        }
        return CadastroRural.class.getSimpleName();
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        switch (selecionado.getTipoDoctoOficial().getTipoCadastroDoctoOficial()) {
            case CADASTROIMOBILIARIO:
                selecionado.setCadastroImobiliario((CadastroImobiliario) obj);
                carregarCadastroImobiliario();
                break;
            case CADASTROECONOMICO:
                selecionado.setCadastroEconomico((CadastroEconomico) obj);
                carregarCadastroEconomico();
                break;
            case CADASTRORURAL:
                selecionado.setCadastroRural((CadastroRural) obj);
                carregarCadastroRural();
                break;
            case PESSOAFISICA:
                selecionado.setPessoaFisica((PessoaFisica) obj);
                carregarPessoaFisica();
                break;
            case PESSOAJURIDICA:
                selecionado.setPessoaJuridica((PessoaJuridica) obj);
                carregarPessoaJuridica();
                break;
        }
    }

    public ComponentePesquisaGenerico getComponentePesquisa() {
        if (selecionado.getTipoDoctoOficial() != null && selecionado.getTipoDoctoOficial().getTipoCadastroDoctoOficial() != null) {
            switch (selecionado.getTipoDoctoOficial().getTipoCadastroDoctoOficial()) {
                case CADASTROIMOBILIARIO:
                    return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroImobiliarioControlador");
                case CADASTROECONOMICO:
                    return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
                case CADASTRORURAL:
                    return (ComponentePesquisaGenerico) Util.getControladorPeloNome("componentePesquisaGenerico");
                case PESSOAFISICA:
                    return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pessoaFisicaTributarioPesquisaGenerica");
                case PESSOAJURIDICA:
                    return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pessoaJuridicaTributarioPesquisaGenerica");
            }
        }
        return (ComponentePesquisaGenerico) Util.getControladorPeloNome("componentePesquisaGenerico");
    }

    public void imprimeConsultaDebito() throws JRException, IOException {
        Pessoa pessoa;
        Cadastro cadastro = null;
        String filtros = "";
        if (isCadastroImobiliario()) {
            cadastro = selecionado.getCadastroImobiliario();
            filtros = TipoCadastroTributario.IMOBILIARIO.getDescricaoLonga() + ": " + selecionado.getCadastroImobiliario().getInscricaoCadastral();
        } else if (isCadastroEconomico()) {
            cadastro = selecionado.getCadastroEconomico();
            filtros = TipoCadastroTributario.ECONOMICO.getDescricaoLonga() + ": " + selecionado.getCadastroEconomico().getInscricaoCadastral();
        } else if (isCadastroRural()) {
            cadastro = selecionado.getCadastroRural();
            filtros = TipoCadastroTributario.RURAL.getDescricaoLonga() + ": " + selecionado.getCadastroRural().getCodigo();
        }

        if (cadastro != null) {
            List<Pessoa> pessoas = solicitacaoDoctoOficialFacade.getPessoaFacade().recuperaPessoasDoCadastro(cadastro);
            pessoa = pessoas.get(0);
        } else {
            if (isPessoaFisica()) {
                pessoa = selecionado.getPessoaFisica();
                filtros = TipoCadastroTributario.PESSOA.getDescricaoLonga() + ": " + selecionado.getPessoaFisica().getCpf();
            } else {
                pessoa = selecionado.getPessoaJuridica();
                filtros = TipoCadastroTributario.PESSOA.getDescricaoLonga() + ": " + selecionado.getPessoaJuridica().getCnpj();
            }
        }
        if (pessoa != null) {
            pessoa = solicitacaoDoctoOficialFacade.getPessoaFacade().recarregar(pessoa);
            EnderecoCorreio enderecoCorrespondencia = pessoa.getEnderecoDomicilioFiscal();

            List<ResultadoParcela> resultados = solicitacaoDoctoOficialFacade.buscarParcelaParaSolicitacaoDeCertidaoParaEmissao(selecionado);
            Collections.sort(resultados, ResultadoParcela.getComparatorByValuePadrao());

            ImprimeDemonstrativoDebitos imprime = new ImprimeDemonstrativoDebitos("RelatorioConsultaDebitos.jasper", new LinkedList<>(resultados));
            imprime.montarMapa();
            imprime.adicionarParametro("BRASAO", imprime.getCaminhoImagem());
            imprime.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
            imprime.adicionarParametro("PESSOA", pessoa.getNome());
            imprime.adicionarParametro("CPF_CNPJ", pessoa.getCpf_Cnpj());
            if (enderecoCorrespondencia != null) {
                imprime.adicionarParametro("ENDERECO", enderecoCorrespondencia.getEnderecoCompleto());
            }
            imprime.adicionarParametro("FILTROS", filtros);
            imprime.adicionarParametro("TOTALPORSITUACAO", imprime.getTotalPorSituacao());
            imprime.adicionarParametro("SUBREPORT_DIR", imprime.getCaminho());
            imprime.setGeraNoDialog(true);
            imprime.imprimeRelatorio();
        } else {
            System.out.println("Nao tem pessoa");
        }
    }

    public String getMensagemEmail() {
        return mensagemEmail;
    }

    public void setMensagemEmail(String mensagemEmail) {
        this.mensagemEmail = mensagemEmail;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public void prepararImpressaoParaEnvio() {
        if (podeGerarDocumento()) {
            emails = getEmailPessoaPrincipalSelecionada();
            mensagemEmail = null;
            FacesUtil.executaJavaScript("listaEmails.show()");
        }
    }

    private String getEmailPessoaPrincipalSelecionada() {
        if (selecionado.getCadastroImobiliario() != null) {
            for (Propriedade propriedade : propriedades) {
                if (propriedade.getPessoa().getEmail() != null && !propriedade.getPessoa().getEmail().isEmpty())
                    return propriedade.getPessoa().getEmail();
            }
        } else if (selecionado.getCadastroEconomico() != null) {
            return selecionado.getCadastroEconomico().getPessoa().getEmail();
        } else if (selecionado.getPessoaFisica() != null) {
            return selecionado.getPessoaFisica().getEmail();
        } else if (selecionado.getPessoaJuridica() != null) {
            return selecionado.getPessoaJuridica().getEmail();
        }
        return null;
    }

    public void enviarDocumentoPorEmail() {
        try {
            String[] emailsSeparados = validarAndSepararEmails();

            if (selecionado.getDocumentoOficial() == null || selecionado.getDocumentoOficial().getConteudo() == null) {
                selecionado.setDocumentoOficial(solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().gerarDocumentoSolicitacaoSemEmitir(selecionado, selecionado.getDocumentoOficial(), selecionado.getCadastro(), selecionado.getPessoa(), selecionado.getTipoDoctoOficial(), getSistemaControlador()));
                if (!SituacaoSolicitacao.EMITIDO.equals(selecionado.getSituacaoSolicitacao())) {
                    selecionado.setSituacaoSolicitacao(SituacaoSolicitacao.EMITIDO);
                }
            }
            selecionado.getImpressaoDoctoOficial().add(criaImpressaoDoctoOficial(selecionado));
            selecionado = solicitacaoDoctoOficialFacade.salvarRetornando(selecionado);

            enviarEmail(emailsSeparados);
        } catch (AddressException e) {
            FacesUtil.addOperacaoNaoRealizada("O e-mail informado é invalido!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addError("Impossível continuar", ex.getMessage());
            logger.error("Erro: ", ex);
        }
    }

    private String[] validarAndSepararEmails() throws AddressException {
        ValidacaoException ve = new ValidacaoException();
        String emailsQuebrados[] = null;
        if (emails == null || emails.trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo E-mail é obrigatório.");
        } else {
            emailsQuebrados = emails.split(Pattern.quote(","));
            for (String emailsQuebrado : emailsQuebrados) {
                InternetAddress emailAddr = new InternetAddress(emailsQuebrado);
                emailAddr.validate();
            }
        }
        if (mensagemEmail == null || mensagemEmail.trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mensagem é obrigatório.");
        }
        ve.lancarException();
        return emailsQuebrados;
    }

    private void enviarEmail(String[] emailsSeparados) {
        try {
            ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
            String prefeitura = "Prefeitura Municipal de " + configuracaoTributario.getCidade().getNome() + " / " + configuracaoTributario.getCidade().getUf();
            String assunto = prefeitura + " - " + selecionado.getTipoDoctoOficial().getDescricao();
            String mensagem = prefeitura + "<br/>" + selecionado.getTipoDoctoOficial().getDescricao() + " (" + selecionado.getCodigo() + ")" + "<br/><br/>" + mensagemEmail;
            solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().enviarEmailDocumentoOficial(emailsSeparados, selecionado.getDocumentoOficial(), assunto, mensagem);
            FacesUtil.addOperacaoRealizada("E-mail enviado com sucesso!");
            FacesUtil.executaJavaScript("listaEmails.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível enviar o e-mail");
            logger.error("Não foi possível enviar o e-mail: " + e);
        }
    }

    public boolean mostrarImprimirDemonstrativo() {
        if (selecionado.getTipoDoctoOficial() != null) {
            return selecionado.getTipoDoctoOficial().getTipoValidacaoDoctoOficial().isValidacaoDeDebito();
        }
        return false;
    }
}
