/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.negocios.NFSAvulsaFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Gustavo
 */
@ManagedBean(name = "nFSAvulsaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listaNotaFiscalAvulsa",
        pattern = "/tributario/notafiscal-avulsa/emissao/listar/",
        viewId = "/faces/tributario/notafiscalavulsa/emissao/lista.xhtml"),
    @URLMapping(id = "novoNotaFiscalAvulsa",
        pattern = "/tributario/notafiscal-avulsa/emissao/novo/",
        viewId = "/faces/tributario/notafiscalavulsa/emissao/edita.xhtml"),
    @URLMapping(id = "editarNotaFiscalAvulsa",
        pattern = "/tributario/notafiscal-avulsa/emissao/editar/#{nFSAvulsaControlador.id}/",
        viewId = "/faces/tributario/notafiscalavulsa/emissao/edita.xhtml"),
    @URLMapping(id = "verNotaFiscalAvulsa",
        pattern = "/tributario/notafiscal-avulsa/emissao/ver/#{nFSAvulsaControlador.id}/",
        viewId = "/faces/tributario/notafiscalavulsa/emissao/visualiza.xhtml"),
    @URLMapping(id = "verEmissaoNotaFiscalAvulsaEspecial",
        pattern = "/tributario/nota-fiscal-avulsa/emissao-usuario-especial/ver/#{nFSAvulsaControlador.id}/",
        viewId = "/faces/tributario/notafiscalavulsa/emissao/visualizaespecial.xhtml"),
    @URLMapping(id = "listarEmissaoNotaFiscalAvulsaEspecial",
        pattern = "/tributario/nota-fiscal-avulsa/emissao-usuario-especial/listar/",
        viewId = "/faces/tributario/notafiscalavulsa/emissao/listaparausuarioespecial.xhtml"),
    @URLMapping(id = "novoEmissaoNotaFiscalAvulsaEspecial",
        pattern = "/tributario/nota-fiscal-avulsa/emissao-usuario-especial/novo/",
        viewId = "/faces/tributario/notafiscalavulsa/emissao/editaparausuarioespecial.xhtml"),
    @URLMapping(id = "editarEmissaoNotaFiscalAvulsaEspecial",
        pattern = "/tributario/nota-fiscal-avulsa/emissao-usuario-especial/editar/#{nFSAvulsaControlador.id}/",
        viewId = "/faces/tributario/notafiscalavulsa/emissao/editaparausuarioespecial.xhtml")})
public class NFSAvulsaControlador extends PrettyControlador<NFSAvulsa> implements Serializable, CRUD {

    @EJB
    private NFSAvulsaFacade nFSAvulsaFacade;
    private ConverterAutoComplete converterPessoa, converterCadastroEconomico;
    private NFSAvulsaItem item;
    private Pessoa filtroPrestador;
    private Pessoa filtroTomador;
    private String numeroInicial;
    private String numeroFinal;
    private Date emissaoInicial;
    private Date emissaoFinal;
    private CadastroEconomico cadastroEconomico;
    private Exercicio inicialExercicio;
    private Exercicio finalExercicio;
    private int inicio = 0;
    private int maximoRegistrosTabela;
    private ConfiguracaoTributario configuracaoTributario;
    private boolean usuarioEspecial;
    private List<NFSAvulsa> lista;
    private List<EconomicoCNAE> listaEconomicoCNAEs;
    private String loginAutorizacao;
    private String senhaAutorizacao;
    private String motivo;
    private PessoaFisica pessoaFisica;
    private PessoaJuridica pessoaJuridica;
    private boolean apareceLogin = false;
    private boolean prestador = false;
    private boolean bloqueiaPrestador = false;
    private boolean bloqueiaTomador = false;
    private List<ResultadoParcela> parcelas;

    public NFSAvulsaControlador() {
        super(NFSAvulsa.class);
    }

    public boolean isApareceLogin() {
        return apareceLogin;
    }

    public void setApareceLogin(boolean apareceLogin) {
        this.apareceLogin = apareceLogin;
    }

    public boolean isPrestador() {
        return prestador;
    }

    public void setPrestador(boolean prestador) {
        this.prestador = prestador;
    }

    public boolean isBloqueiaPrestador() {
        return bloqueiaPrestador;
    }

    public void setBloqueiaPrestador(boolean bloqueiaPrestador) {
        this.bloqueiaPrestador = bloqueiaPrestador;
    }

    public boolean isBloqueiaTomador() {
        return bloqueiaTomador;
    }

    public void setBloqueiaTomador(boolean bloqueiaTomador) {
        this.bloqueiaTomador = bloqueiaTomador;
    }

    @Override
    public AbstractFacade getFacede() {
        return nFSAvulsaFacade;
    }

    public NFSAvulsaItem getItem() {
        return item;
    }

    public void setItem(NFSAvulsaItem item) {
        this.item = item;
    }

    public Date getEmissaoFinal() {
        return emissaoFinal;
    }

    public void setEmissaoFinal(Date emissaoFinal) {
        this.emissaoFinal = emissaoFinal;
    }

    public Date getEmissaoInicial() {
        return emissaoInicial;
    }

    public void setEmissaoInicial(Date emissaoInicial) {
        this.emissaoInicial = emissaoInicial;
    }

    public Pessoa getFiltroPrestador() {
        return filtroPrestador;
    }

    public void setFiltroPrestador(Pessoa filtroPrestador) {
        this.filtroPrestador = filtroPrestador;
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }


    public Pessoa getFiltroTomador() {
        return filtroTomador;
    }

    public void setFiltroTomador(Pessoa filtroTomador) {
        this.filtroTomador = filtroTomador;
    }

    public String getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public Converter getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, nFSAvulsaFacade.getCadastroEconomicoFacade());
        }
        return converterCadastroEconomico;
    }

    public Converter getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, nFSAvulsaFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return nFSAvulsaFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public String getEnderecoPrestador() {
        return nFSAvulsaFacade.getEnderecoPrestador(selecionado);
    }

    public String getEnderecoTomador() {
        return nFSAvulsaFacade.getEnderecoTomador(selecionado);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/notafiscal-avulsa/emissao/";
    }

    public void novaNFAPessoaEspecial() {
        Web.navegacao("/tributario/nota-fiscal-avulsa/emissao-usuario-especial/listar/", "/tributario/nota-fiscal-avulsa/emissao-usuario-especial/novo/");
    }


    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoNotaFiscalAvulsa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        pessoaFisica = (PessoaFisica) Web.pegaDaSessao(PessoaFisica.class);
        pessoaJuridica = (PessoaJuridica) Web.pegaDaSessao(PessoaJuridica.class);
        if (isSessao()) {
            try {
                Web.pegaTodosFieldsNaSessao(this);
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage());
            }
        } else {
            inicializaVariaveis();
        }
        parcelas = Lists.newArrayList();
    }

    @URLAction(mappingId = "novoEmissaoNotaFiscalAvulsaEspecial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoParaUsuarioEspecial() {
        super.novo();
        usuarioEspecial = true;
        inicializaVariaveis();
    }

    @URLAction(mappingId = "listaNotaFiscalAvulsa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposEmissao() {
        Util.limparCampos(this);
    }

    private void inicializaVariaveis() {
        configuracaoTributario = nFSAvulsaFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        UsuarioSistema usuario = nFSAvulsaFacade.getSistemaFacade().getUsuarioCorrente();
        selecionado.setUsuario(usuario);
        item = new NFSAvulsaItem();
        listaEconomicoCNAEs = new ArrayList<>();
        inicialExercicio = new Exercicio();
        finalExercicio = new Exercicio();
        selecionado.setExercicio(getSistemaControlador().getExercicioCorrente());
        selecionado.setValorIss(configuracaoTributario.getAliquotaISSQN());
        if (selecionado.getValorIss() == null) {
            FacesUtil.addWarn("Atenção!", "Verifique se nas Configurações do Tributário foi informada a alíquota de ISS para Nota Fiscal Avulsa!");
        }
        selecionado.setInicioVigencia(getSistemaControlador().getDataOperacao());
        selecionado.setDataNota(getSistemaControlador().getDataOperacao());
        if (selecionado.getTipoPrestadorNF() == null || selecionado.getTipoTomadorNF() == null) {
            selecionado.setTipoPrestadorNF(TipoTomadorPrestadorNF.PESSOA);
            selecionado.setTipoTomadorNF(TipoTomadorPrestadorNF.ECONOMICO);
        }
    }

    public void atualizaListaCNAEs(CadastroEconomico cadastro) {
        if (cadastro != null && cadastro.getId() != null) {
            cadastroEconomico = nFSAvulsaFacade.getCadastroEconomicoFacade().recuperar(cadastro.getId());
            listaEconomicoCNAEs = cadastroEconomico.getEconomicoCNAE();
        }
        this.cadastroEconomico = cadastro;
    }


    @URLAction(mappingId = "editarNotaFiscalAvulsa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        if (isSessao()) {
            try {
                Web.pegaTodosFieldsNaSessao(this);
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage());
            }
        } else {
            configuracaoTributario = nFSAvulsaFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        }
    }

    @URLAction(mappingId = "verNotaFiscalAvulsa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        buscarParcelas();
    }

    @URLAction(mappingId = "verEmissaoNotaFiscalAvulsaEspecial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verUsuarioEspecial() {
        editarUsuarioEspecial();
        parcelas = Lists.newArrayList();
        operacao = Operacoes.VER;

    }

    @URLAction(mappingId = "editarEmissaoNotaFiscalAvulsaEspecial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarUsuarioEspecial() {
        super.editar();
        configuracaoTributario = nFSAvulsaFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        usuarioEspecial = true;
        if (!podeImprimir()) {
            FacesUtil.addWarn("Não foi possível emitir a nota fiscal!", "O prestador informado é um C.M.C. que NÃO É AUTÔNOMO!");
        }
    }


    public void atualizaCmcPrestador() {
        atualizaListaCNAEs(selecionado.getCmcPrestador());
        if (!podeImprimir()) {
            if (!usuarioTemPermissao()) {
                FacesUtil.addWarn("ATENÇÃO!", "O C.M.C. informado NÃO É AUTÔNOMO! Você não tem autorização para emitir Nota Fiscal Avulsa para C.M.C. que não é autônomo.");
                selecionado.setCmcPrestador(null);
                listaEconomicoCNAEs = new ArrayList<>();
                FacesUtil.atualizarComponente("Formulario");
                return;
            }
            apareceLogin = true;
            FacesUtil.atualizarComponente("dialogLogin");
        }
        FacesUtil.atualizarComponente("Formulario");
    }

    public void ehPrestador() {
        prestador = true;
    }

    public void ehTomador() {
        prestador = false;
    }

    public void logar() {
        if (motivo == null || "".equals(motivo)) {
            FacesUtil.addError("Atenção!", "Informe o motivo.");
        } else if (nFSAvulsaFacade.getUsuarioSistemaFacade().validaLoginSenha(loginAutorizacao, senhaAutorizacao)) {
            UsuarioSistema usuario = nFSAvulsaFacade.getUsuarioSistemaFacade().findOneByLogin(loginAutorizacao);
            if (nFSAvulsaFacade.getUsuarioSistemaFacade().validaAutorizacaoUsuario(usuario, AutorizacaoTributario.EMITIR_NOTA_FISCAL_AVULSA_CMC_NAO_AUTONOMO)) {
                FacesUtil.executaJavaScript("login.hide()");
                selecionado.setMotivo(motivo);
                apareceLogin = false;
            } else {
                FacesUtil.executaJavaScript("login.hide()");
                FacesUtil.addWarn("ATENÇÃO", "O usuário informado não possui autorização para realizar este procedimento.");
                cancelaLancamento();

            }
        } else {
            FacesUtil.addError("Não foi possível continuar!", "Usuário ou senha incorretos.");
        }
    }

    public void cancelaLancamento() {
        selecionado.setCmcPrestador(null);
        listaEconomicoCNAEs = new ArrayList<>();
        FacesUtil.atualizarComponente("Formulario");
    }

    private boolean usuarioTemPermissao() {
        return nFSAvulsaFacade.getUsuarioSistemaFacade().validaAutorizacaoUsuario(getSistemaControlador().getUsuarioCorrente(), AutorizacaoTributario.EMITIR_NOTA_FISCAL_AVULSA_CMC_NAO_AUTONOMO);
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }


    public void atualizaCmcTomador(SelectEvent e) {
        Pessoa pessoa = (Pessoa) e.getObject();
        selecionado.setCmcTomador(nFSAvulsaFacade.getCadastroEconomicoFacade().recuperarCadastroEconomicoDaPessoa(pessoa));
    }

    public void atualizaPessoaTomador(SelectEvent e) {
        CadastroEconomico ce = (CadastroEconomico) e.getObject();
        selecionado.setTomador(nFSAvulsaFacade.getPessoaFacade().recuperar(ce.getPessoa().getId()));
        selecionado.setCmcTomador(ce);
    }

    public void addItem() {
        try {
            validarItem();
            String message = nFSAvulsaFacade.defineAliquotaISS(item, selecionado);
            if (!Strings.isNullOrEmpty(message)) {
                FacesUtil.addWarn("Atenção", message);
            }
            BigDecimal valor = ((item.getAliquotaIss()).multiply(item.getValorTotal())).divide(new BigDecimal("100"));
            item.setValorIss(valor);
            if (!selecionado.getItens().contains(item)) {
                item.setNFSAvulsa(selecionado);
                selecionado.addItem(item);
                FacesUtil.addInfo("Item adicionado com sucesso!", "");
            } else {
                selecionado.getItens().set(selecionado.getItens().indexOf(item), item);
                FacesUtil.addInfo("Item alterado com sucesso!", "");
            }
            item = new NFSAvulsaItem();
            item.setUnidade("UND");
            bloqueiaPrestador = true;
            bloqueiaTomador = true;
            FacesUtil.atualizarComponente("Formulario");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }


    public BigDecimal getTotalParcial() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            for (NFSAvulsaItem it : selecionado.getItens()) {
                total = total.add(it.getValorTotal());
            }
        }
        return total;
    }

    public BigDecimal getTotalUnitario() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            for (NFSAvulsaItem it : selecionado.getItens()) {
                total = total.add(it.getValorUnitario());
            }
        }
        return total;
    }

    public BigDecimal getTotalAliquota() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            for (NFSAvulsaItem it : selecionado.getItens()) {
                total = total.add(it.getAliquotaIss());
            }
        }
        return total;
    }

    public BigDecimal getTotalIss() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            for (NFSAvulsaItem it : selecionado.getItens()) {
                total = total.add(it.getValorIss());
            }
        }
        return total;
    }

    public BigDecimal totalNota(NFSAvulsa nota) {
        BigDecimal total = BigDecimal.ZERO;
        if (nota != null) {
            nota = nFSAvulsaFacade.recuperar(nota.getId());
            for (NFSAvulsaItem it : nota.getItens()) {
                total = total.add(it.getValorTotal());
            }
        }
        return total;
    }

    public BigDecimal totalIss(NFSAvulsa nota) {
        BigDecimal total = BigDecimal.ZERO;
        if (nota != null) {
            nota = nFSAvulsaFacade.recuperar(nota.getId());
            for (NFSAvulsaItem it : nota.getItens()) {
                total = total.add(it.getValorIss());
            }
        }
        return total;
    }

    public void confirmar() {
        try {
            validarCampos();
            String mensagem = "";
            selecionado.setValorServicos(getTotalParcial());
            selecionado.setValorTotalIss(getTotalIss());
            selecionado.setSituacao(NFSAvulsa.Situacao.ABERTA);
            selecionado = nFSAvulsaFacade.gerarNfsAvulsaAndDebito(selecionado);
            buscarParcelas();
            if (getPodeEmitirDamDaNota()) {
                mensagem = "A emissão da Nota Fiscal será liberada após o pagamento da DAM!";
            } else {
                mensagem = "A Nota Fiscal está liberada para ser emitida!";
            }
            FacesUtil.addInfo("Operação realizada com sucesso!", mensagem);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Não foi possível continuar!", "Ocorreu um erro ao salvar: " + e.getMessage());
        }
    }

    public boolean getPodeEmitirDamDaNota() {
        if (NFSAvulsa.Situacao.CANCELADA.equals(selecionado.getSituacao())) {
            return false;
        }
        if (NFSAvulsa.Situacao.ALTERADA.equals(selecionado.getSituacao())) {
            return false;
        }
        if (selecionado.getValorTotalIss().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (!parcelas.isEmpty()) {
            for (ResultadoParcela parcela : parcelas) {
                if (parcela.getSituacaoEnumValue().isPago() || parcela.isCancelado() || parcela.isVencido(new Date())) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    public void emiteDAM() {
        try {
            CalculoNFSAvulsa calculoRecuperado = nFSAvulsaFacade.recuperaCalculoPelaNota(selecionado);
            if (calculoRecuperado != null) {
                List<DAM> damsRecuperados = nFSAvulsaFacade.getDAMFacade().recuperaDAMsPeloCalculo(calculoRecuperado);
                if (damsRecuperados.isEmpty()) {
                    nFSAvulsaFacade.getDAMFacade().geraDAM(calculoRecuperado);
                    damsRecuperados = nFSAvulsaFacade.getDAMFacade().recuperaDAMsPeloCalculo(calculoRecuperado);
                }
                if (!damsRecuperados.isEmpty()) {
                    ImprimeDAM imprimeDAM = parametrosImprimeDam();
                    imprimeDAM.imprimirDamUnicoViaApi(damsRecuperados);
                }
            }
        } catch (Exception ex) {
            FacesUtil.addError("Não foi possível imprimir o DAM!", ex.getMessage());
        }
    }

    private ImprimeDAM parametrosImprimeDam() throws Exception {
        ImprimeDAM damParametro = new ImprimeDAM();
        damParametro.adicionarParametro("NOTAFISCAL", selecionado.getNumero());
        damParametro.setGeraNoDialog(true);
        return damParametro;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getPrestador() == null && selecionado.getCmcPrestador() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o prestador.");
        }
        if (selecionado.getTomador() == null && selecionado.getCmcTomador() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o tomador.");
        }
        if (selecionado.getPrestador() != null && selecionado.getTomador() != null) {
            if (selecionado.getPrestador().equals(selecionado.getTomador())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O tomador deve ser diferente do prestador.");
            }
        }
        if (selecionado.getCmcPrestador() != null && selecionado.getCmcTomador() != null) {
            if (selecionado.getCmcPrestador().equals(selecionado.getCmcTomador())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O tomador deve ser diferente do prestador.");
            }
        }
        if (selecionado.getItens().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos um serviço.");
        }
        if (selecionado.getDataNota() == null || DataUtil.dataSemHorario(selecionado.getDataNota()).after(DataUtil.dataSemHorario(getSistemaControlador().getDataOperacao()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data da Nota Fiscal deve ser menor ou igual à data do sistema!");
        }
        ve.lancarException();
    }

    public List<EconomicoCNAE> getListaEconomicoCNAEs() {
        return listaEconomicoCNAEs;
    }

    public void setListaEconomicoCNAEs(List<EconomicoCNAE> listaEconomicoCNAEs) {
        this.listaEconomicoCNAEs = listaEconomicoCNAEs;
    }

    public void emitirNota() throws JRException, IOException {
        try {
            selecionado = nFSAvulsaFacade.emitirNota(selecionado);
        } catch (Exception ex) {
            logger.error("Erro ao emitir a nota fiscal avulsa: ", ex);
            FacesUtil.addError("Erro ao Emitir Nota Fiscal Avulsa", "Contate o suporte do sistema!");
        }
    }

    public Exercicio getInicialExercicio() {
        return inicialExercicio;
    }

    public void setInicialExercicio(Exercicio inicialExercicio) {
        this.inicialExercicio = inicialExercicio;
    }

    public Exercicio getFinalExercicio() {
        return finalExercicio;
    }

    public void setFinalExercicio(Exercicio finalExercicio) {
        this.finalExercicio = finalExercicio;
    }


    public void removeItem(NFSAvulsaItem it) {
        selecionado.getItens().remove(it);
        if (selecionado.getItens().isEmpty()) {
            bloqueiaPrestador = false;
            bloqueiaTomador = false;
        }

    }

    public void alteraItem(NFSAvulsaItem it) {
        item = it;
    }

    public void buscarParcelas() {
        parcelas = Lists.newArrayList();
        List<CalculoNFSAvulsa> calculosNFSAvulsa = nFSAvulsaFacade.buscarCalculosDaNFSAvulsa(selecionado);
        if (calculosNFSAvulsa != null && !calculosNFSAvulsa.isEmpty()) {
            parcelas = new ConsultaParcela().addParameter(ConsultaParcela.Campo.CALCULO_ID,
                    ConsultaParcela.Operador.IN, calculosNFSAvulsa.stream().map(CalculoNFSAvulsa::getId).collect(Collectors.toList()))
                .executaConsulta().getResultados();
        }
    }

    public List<ResultadoParcela> getParcelas() {
        return parcelas;
    }

    public boolean liberaPlaca() {
        return (isPrestadorComCMC(selecionado) && selecionado.getCmcPrestador().getTipoAutonomo() != null && selecionado.getCmcPrestador().getTipoAutonomo().getNecessitaPlacas() != null && selecionado.getCmcPrestador().getTipoAutonomo().getNecessitaPlacas());
    }

    public boolean liberaImpressao() {
        if (!parcelas.isEmpty() && !configuracaoTributario.getEmiteSemPagamento()) {
            for (ResultadoParcela parcela : parcelas) {
                if (parcela.isEmAberto()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void acaoDoBotaoFiltrar() {
        inicio = 0;
        filtrar();
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return nFSAvulsaFacade.getCadastroEconomicoFacade().buscarCadastrosPorInscricaoOrCpfCnpjOrNome(parte.trim());
    }


    public List<SelectItem> getTipoTomadorPrestador() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        for (TipoTomadorPrestadorNF tipo : TipoTomadorPrestadorNF.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }


    public boolean isTemMaisResultados() {
        if (lista == null) {
            filtrar();
        }

        return lista.size() > maximoRegistrosTabela;
    }

    public boolean isTemAnterior() {
        return inicio > 0;
    }

    public void acaoBotaoFiltrar() {
        inicio = 0;
        filtrar();
    }

    public void proximos() {
        inicio += maximoRegistrosTabela;
        filtrar();
    }

    public void anteriores() {
        inicio -= maximoRegistrosTabela;
        if (inicio < 0) {
            inicio = 0;
        }
        filtrar();
    }


    public List getLista() {
        if (lista == null) {
            lista = new ArrayList<>();
        }
        return lista;
    }

    public void iniciarPesquisa() {
        inicio = 0;
        filtrar();
    }

    public void filtrar() {
        lista = nFSAvulsaFacade.filtrar(inicio, maximoRegistrosTabela, filtroPrestador, filtroTomador, numeroInicial, numeroFinal, emissaoInicial, emissaoFinal, inicialExercicio, finalExercicio);
    }

    public boolean permiteEdicao() {
        return selecionado != null && selecionado.getSituacao() != null && selecionado.getSituacao().equals(NFSAvulsa.Situacao.ABERTA);
    }

    private void validarItem() {
        ValidacaoException ve = new ValidacaoException();
        if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Quantidade não pode ser menor ou igual a zero.");
        }
        if (item.getValorUnitario() == null || item.getValorUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Valor Unitário não pode ser menor ou igual a zero.");
        }
        if (item.getUnidade() == null || item.getUnidade().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("A Unidade deve ser informada.");
        }
        if (item.getDescricao() == null || item.getDescricao().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("A Discriminação deve ser informada.");
        }
        if (liberaPlaca()) {
            if (item.getPlaca().length() < 7) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A placa informada é inválida.");
            }
        }
        ve.lancarException();
    }

    public List<String> listarPlacas() {
        List<String> placas = new ArrayList<>();
        if (selecionado.getCmcPrestador() != null && !selecionado.getCmcPrestador().getPlacas().isEmpty()) {
            for (PlacaAutomovelCmc placa : selecionado.getCmcPrestador().getPlacas()) {
                placas.add(placa.getPlaca());
            }
        }
        return placas;
    }

    public void novoItem() {
        if (podeLancarItem()) {
            item = new NFSAvulsaItem();
            item.setUnidade("UND");
            if (selecionado.getCmcPrestador() != null && !selecionado.getCmcPrestador().getPlacas().isEmpty()) {
                item.setPlaca(selecionado.getCmcPrestador().getPlacas().get(0).getPlaca());
            }
            FacesUtil.executaJavaScript("itensNota.show()");
        }
    }

    private boolean podeLancarItem() {
        boolean valida = true;
        if (selecionado.getPrestador() == null && selecionado.getCmcPrestador() == null) {
            FacesUtil.addError("Não foi possível continuar!", "Informe o prestador.");
            valida = false;
        }
        if (selecionado.getTomador() == null && selecionado.getCmcTomador() == null) {
            FacesUtil.addError("Não foi possível continuar!", "Informe o tomador.");
            valida = false;
        }
        return valida;
    }


    public boolean isUsuarioEspecial() {
        return usuarioEspecial;
    }

    public void setUsuarioEspecial(boolean usuarioEspecial) {
        this.usuarioEspecial = usuarioEspecial;
    }

    public boolean podeImprimir() {
        if (usuarioEspecial) {
            return true;
        } else {
            if (isPrestadorComCMC(selecionado)) {
                if (isAutonomo(selecionado)) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }


    @URLAction(mappingId = "listaNotaFiscalAvulsa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void setaUsuarioEspecial() {
        boolean valor = true;
        usuarioEspecial = valor;
    }


    public void proximaNotaValida() {
        if (selecionado.getNFSAvulsa() != null) {
            selecionado = nFSAvulsaFacade.recuperar(selecionado.getNFSAvulsa().getId());
            configuracaoTributario = nFSAvulsaFacade.getConfiguracaoTributarioFacade().retornaUltimo();
            if (!podeImprimir()) {
                FacesUtil.addWarn("Atenção!", "O prestador informado é um C.M.C. NÃO AUTÔNOMO!");
            }
        }
    }


    private boolean isPrestadorComCMC(NFSAvulsa nota) {
        if (nota == null) {
            return false;
        }
        return nota.getCmcPrestador() != null;
    }

    private boolean isAutonomo(NFSAvulsa nota) {
        if (nota.getCmcPrestador() != null) {
            return nota.getCmcPrestador().getNaturezaJuridica() != null && nota.getCmcPrestador().getNaturezaJuridica().getAutonomo();
        }
        return false;
    }


    public Boolean getAutonomo() {
        return isAutonomo(selecionado);
    }


    public void copiaNumeroInicialParaNumeroFinal() {
        this.numeroFinal = this.numeroInicial;
    }

    public void limparCamposLista() {
        filtroPrestador = null;
        filtroTomador = null;
        numeroInicial = null;
        numeroFinal = null;
        emissaoInicial = null;
        emissaoFinal = null;
        inicialExercicio = null;
        finalExercicio = null;
        lista = new ArrayList<>();
    }

    public int getMaximoRegistrosTabela() {
        return maximoRegistrosTabela;
    }

    public void setMaximoRegistrosTabela(int maximoRegistrosTabela) {
        this.maximoRegistrosTabela = maximoRegistrosTabela;
    }


    public void navegaDaNFSAEmissaoUsuarioEspecialParaPJ() {
        Web.navegacao("/tributario/nota-fiscal-avulsa/emissao-usuario-especial/novo/", "/tributario/configuracoes/pessoa/novapessoajuridica/", selecionado);
    }

    public void navegaDaNFSAEmissaoUsuarioEspecialParaPF() {
        Web.navegacao("/tributario/nota-fiscal-avulsa/emissao-usuario-especial/novo/", "/tributario/configuracoes/pessoa/novapessoafisica/", selecionado);
    }

    public void navegaDaNFSAEmissaoParaPJ() {
        Web.navegacao("/tributario/nota-fiscal-avulsa/emissao/novo/", "/tributario/configuracoes/pessoa/novapessoajuridica/", selecionado);
    }

    public void navegaDaNFSAEmissaoParaPF() {
        Web.navegacao("/tributario/nota-fiscal-avulsa/emissao/novo/", "/tributario/configuracoes/pessoa/novapessoafisica/", selecionado);
    }

    public void navegaParaEdita() {
        Web.navegacao(getUrlAtual(), "/tributario/notafiscal-avulsa/emissao/editar/" + getId() + "/", selecionado);
    }

    public void navegaParaEditaEspecial() {
        Web.navegacao(getUrlAtual(), "/tributario/nota-fiscal-avulsa/emissao-usuario-especial/editar/" + getId() + "/", selecionado);
    }

    public String caminhoAtual() {
        if (selecionado.getId() != null) {
            return getCaminhoPadrao() + "editar/" + getUrlKeyValue() + "/";
        }
        return getCaminhoPadrao() + "novo/";
    }

    public List<SituacaoCadastralPessoa> getSituacoesPesquisaPessoa() {
        return Lists.newArrayList(SituacaoCadastralPessoa.ATIVO);
    }

    public void poeNaSessao() {
        Web.poeNaSessao(selecionado);
        Web.poeTodosFieldsNaSessao(this);
    }

    public List<SituacaoCadastralCadastroEconomico> getSituacoesDisponiveis() {
        List<SituacaoCadastralCadastroEconomico> situacoes = new ArrayList<>();
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
        return situacoes;
    }

    public ComponentePesquisaGenerico getComponentePesquisaCMC() {
        PesquisaCadastroEconomicoControlador componente = (PesquisaCadastroEconomicoControlador) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
        List<SituacaoCadastralCadastroEconomico> situacao = Lists.newArrayList();
        situacao.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        situacao.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
        componente.setSituacao(situacao);
        return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
    }

    public void selecionarObjetoPesquisaGenericoCMC(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        if (prestador) {
            selecionado.setCmcPrestador((CadastroEconomico) obj);
            atualizaCmcPrestador();
        } else {
            selecionado.setCmcTomador((CadastroEconomico) obj);
        }
    }

    public void validaPessoaComCMC() {
        if (selecionado.getPrestador() != null) {
            List<CadastroEconomico> cmcs = nFSAvulsaFacade.getCadastroEconomicoFacade().recuperaCadastrosAtivosDaPessoa(selecionado.getPrestador());
            if (!cmcs.isEmpty()) {
                RequestContext.getCurrentInstance().execute("pessoaComCmc.show()");
            }
        }
    }


    public void naoConfirmaPessoaComCmc() {
        selecionado.setPrestador(null);
        RequestContext.getCurrentInstance().execute("pessoaComCmc.hide()");
        FacesUtil.atualizarComponente("Formulario");
    }

    public void setaPrestadorNull() {
        selecionado.setCmcPrestador(null);
        selecionado.setPrestador(null);
    }

    public void setaTomadorNull() {
        selecionado.setCmcTomador(null);
        selecionado.setTomador(null);
    }
}
