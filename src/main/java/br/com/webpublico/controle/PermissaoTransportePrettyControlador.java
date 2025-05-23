package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteDetentorArquivoComposicao;
import br.com.webpublico.entidadesauxiliares.AssistentePermissaoTransporte;
import br.com.webpublico.entidadesauxiliares.VoDebitosRBTrans;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.SessaoManual;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Created by AndreGustavo on 21/07/2014.
 */
@ManagedBean(name = "permissaoTransportePrettyControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "criarPermissaoTaxi",
        pattern = "/permissao-de-transporte/taxi/novo/",
        viewId = "/faces/tributario/rbtrans/permissaotransporte/edita.xhtml"),
    @URLMapping(id = "criarPermissaoMotoTaxi",
        pattern = "/permissao-de-transporte/moto-taxi/novo/",
        viewId = "/faces/tributario/rbtrans/permissaotransporte/edita.xhtml"),
    @URLMapping(id = "criarPermissaoFrete",
        pattern = "/permissao-de-transporte/frete/novo/",
        viewId = "/faces/tributario/rbtrans/permissaotransporte/edita.xhtml"),
    @URLMapping(id = "editarPermissao",
        pattern = "/permissao-de-transporte/editar/#{permissaoTransportePrettyControlador.id}/",
        viewId = "/faces/tributario/rbtrans/permissaotransporte/edita.xhtml"),
    @URLMapping(id = "listarPermissao",
        pattern = "/permissao-de-transporte/listar/",
        viewId = "/faces/tributario/rbtrans/permissaotransporte/lista.xhtml"),
    @URLMapping(id = "verPermissao",
        pattern = "/permissao-de-transporte/ver/#{permissaoTransportePrettyControlador.id}/",
        viewId = "/faces/tributario/rbtrans/permissaotransporte/visualizar.xhtml"),
    @URLMapping(id = "verHistoricoVeiculo",
        pattern = "/permissao-de-transporte/historico-de-veiculo/",
        viewId = "/faces/tributario/rbtrans/permissaotransporte/historicoveiculo.xhtml"),
    @URLMapping(id = "verHistoricoPermissao",
        pattern = "/permissao-de-transporte/historico-da-permissao/#{permissaoTransportePrettyControlador.id}/",
        viewId = "/faces/tributario/rbtrans/permissaotransporte/historico.xhtml")
}
)

public class PermissaoTransportePrettyControlador extends PrettyControlador<PermissaoTransporte> implements CRUD {

    private static final Logger logger = LoggerFactory.getLogger(PermissaoTransportePrettyControlador.class);
    @EJB
    private PermissaoTransporteFacade permissaoTransporteFacade;
    private ConverterAutoComplete converterCidade;
    private ConverterAutoComplete conveterVeiculoTransporte;
    private ParametrosTransitoTransporte parametro;
    private RG rgPermissionario;
    private Habilitacao cnhPermissionario;
    private List<EnderecoCorreio> enderecosPermissionario;
    private List<Telefone> telefonesPermissionario;
    private MotoristaAuxiliar motoristaAuxiliar;
    private Permissionario permissionario;
    private RG rgMotoristaAuxiliar;
    private Habilitacao cnhMotoristaAuxiliar;
    private List<EnderecoCorreio> enderecosMotoristaAuxiliar;
    private List<Telefone> telefonesMotoristaAuxiliar;
    private Marca marcaVeiculo;
    private VeiculoPermissionario veiculoPermissionario;
    private Map<String, DefaultStreamedContent> mapaFotos;
    private List<VoDebitosRBTrans> debitos;
    private int abaAtiva = 4;
    private AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao;
    private boolean baixaInsercao;
    private VeiculoPermissionario veiculoPermissionarioBaixaInsercao;
    private TipoCalculoRBTRans tipoCalculoSuporte;
    private boolean debitoRenovacaoPago = false;
    @SessaoManual
    private PontoTaxi pontoTaxi;
    private Date dataVencimentoPermissao;
    private TipoVerificacaoDebitoRenovacao tipoVerificacaoDebitoRenovacao;
    private TermoRBTrans termoSelecionado;

    public PermissaoTransportePrettyControlador() {
        super(PermissaoTransporte.class);
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public boolean getConsultarSePodeAlterarAOpcaoDeVerificarDocumentos() {
        return selecionado.getTipoVerificacaoDebitoRenovacao() == null && TipoVerificacaoDebitoRenovacao.PENDENTE.equals(selecionado.getTipoVerificacaoDebitoRenovacao()) && TipoVerificacaoDebitoRenovacao.VERIFICADO.equals(this.getTipoVerificacaoDebitoRenovacao());
    }

    public TipoVerificacaoDebitoRenovacao getTipoVerificacaoDebitoRenovacao() {
        return tipoVerificacaoDebitoRenovacao;
    }

    public void setTipoVerificacaoDebitoRenovacao(TipoVerificacaoDebitoRenovacao tipoVerificacaoDebitoRenovacao) {
        this.tipoVerificacaoDebitoRenovacao = tipoVerificacaoDebitoRenovacao;
    }

    public void setDebitoRenovacaoPago(boolean debitoRenovacaoPago) {
        this.debitoRenovacaoPago = debitoRenovacaoPago;
    }

    public boolean isDebitoRenovacaoPago() {
        return debitoRenovacaoPago;
    }

    public Date getDataVencimentoPermissao() {
        return dataVencimentoPermissao;
    }

    public void setDataVencimentoPermissao(Date dataVencimentoPermissao) {
        this.dataVencimentoPermissao = dataVencimentoPermissao;
    }

    public TermoRBTrans getTermoSelecionado() {
        return termoSelecionado;
    }

    public void setTermoSelecionado(TermoRBTrans termoSelecionado) {
        this.termoSelecionado = termoSelecionado;
    }

    @Override
    public AbstractFacade getFacede() {
        return permissaoTransporteFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/permissao-de-transporte/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "criarPermissaoTaxi", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaPermissaoTaxi() {
        parametro = permissaoTransporteFacade.getParametrosTransitoTransporteFacade().recuperarParametroVigentePeloTipo(TipoPermissaoRBTrans.TAXI);
        definirSessao();
        if (parametro != null) {
            if (isSessao()) {
                try {
                    Web.pegaTodosFieldsNaSessao(this);
                    if (selecionado != null) {
                        recuperarAtributosDoVeiculoDaSessao();
                        if (selecionado.getPermissionarioVigente() != null) {
                            carregaListaDeFotos();
                        }
                    }
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage());
                }
            } else {
                novo();
                selecionado.setTipoPermissaoRBTrans(TipoPermissaoRBTrans.TAXI);
                selecionado.setNumero(permissaoTransporteFacade.sugereNumeroPermissaoPorTipo(selecionado.getTipoPermissaoRBTrans()));
                selecionado.setInicioVigencia(new Date());
                telefonesPermissionario = new ArrayList<>();
                enderecosPermissionario = new ArrayList<>();
            }
        } else {
            redireciona();
            FacesUtil.addAtencao("Não existe parâmetro de " + TipoPermissaoRBTrans.TAXI.getDescricao() + " cadastrado.");
        }
    }

    @URLAction(mappingId = "criarPermissaoMotoTaxi", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaPermissaoMotoTaxi() {
        parametro = permissaoTransporteFacade.getParametrosTransitoTransporteFacade().recuperarParametroVigentePeloTipo(TipoPermissaoRBTrans.MOTO_TAXI);
        definirSessao();
        if (parametro != null) {
            if (isSessao()) {
                try {
                    Web.pegaTodosFieldsNaSessao(this);
                    if (selecionado != null) {
                        recuperarAtributosDoVeiculoDaSessao();
                        if (selecionado.getPermissionarioVigente() != null) {
                            carregaListaDeFotos();
                        }
                    }
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage());
                }
            } else {
                novo();
                selecionado.setTipoPermissaoRBTrans(TipoPermissaoRBTrans.MOTO_TAXI);
                selecionado.setNumero(permissaoTransporteFacade.sugereNumeroPermissaoPorTipo(selecionado.getTipoPermissaoRBTrans()));
                selecionado.setInicioVigencia(new Date());
                telefonesPermissionario = new ArrayList<>();
                enderecosPermissionario = new ArrayList<>();
            }
        } else {
            redireciona();
            FacesUtil.addAtencao("Não existe parâmetro de " + TipoPermissaoRBTrans.MOTO_TAXI.getDescricao() + " cadastrado.");
        }
    }

    @URLAction(mappingId = "criarPermissaoFrete", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaPermissaoFrete() {
        parametro = permissaoTransporteFacade.getParametrosTransitoTransporteFacade().recuperarParametroVigentePeloTipo(TipoPermissaoRBTrans.FRETE);
        definirSessao();
        if (parametro != null) {
            if (isSessao()) {
                try {
                    Web.pegaTodosFieldsNaSessao(this);
                    if (selecionado != null) {
                        if (selecionado.getPermissionarioVigente() != null) {
                            carregaListaDeFotos();
                        }
                    }
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage());
                }
            } else {
                novo();
                selecionado.setTipoPermissaoRBTrans(TipoPermissaoRBTrans.FRETE);
                selecionado.setNumero(permissaoTransporteFacade.sugereNumeroPermissaoPorTipo(selecionado.getTipoPermissaoRBTrans()));
                selecionado.setInicioVigencia(new Date());
                telefonesPermissionario = new ArrayList<>();
                enderecosPermissionario = new ArrayList<>();
            }
        } else {
            redireciona();
            FacesUtil.addAtencao("Não existe parâmetro de " + TipoPermissaoRBTrans.FRETE.getDescricao() + " cadastrado.");
        }
    }

    private void recuperarAtributosDoVeiculoDaSessao() {
        Marca marca;
        Cor cor;
        TipoVeiculo tipoVeiculo;
        Modelo modelo;
        Combustivel combustivel;
        marca = (Marca) Web.pegaDaSessao(Marca.class);
        if (marca != null) {
            marcaVeiculo = marca;
        }
        cor = (Cor) Web.pegaDaSessao(Cor.class);
        if (cor != null) {
            veiculoPermissionario.getVeiculoTransporte().setCor(cor);
        }
        tipoVeiculo = (TipoVeiculo) Web.pegaDaSessao(TipoVeiculo.class);
        if (tipoVeiculo != null) {
            veiculoPermissionario.getVeiculoTransporte().setTipoVeiculo(tipoVeiculo);
        }
        modelo = (Modelo) Web.pegaDaSessao(Modelo.class);
        combustivel = (Combustivel) Web.pegaDaSessao(Combustivel.class);
        if (combustivel != null) {
            veiculoPermissionario.getVeiculoTransporte().setCombustivel(combustivel);
        }
    }

    public Integer buscarVagasDisponiveisDoPontoDeTaxi() {
        if (pontoTaxi != null) {
            Integer vagasOcupadas = permissaoTransporteFacade.getPontoTaxiFacade().buscarNumeroDeVagasOcupadas(pontoTaxi);
            int vagasDisponiveis = pontoTaxi.getTotalDeVagas() - vagasOcupadas;
            return vagasDisponiveis >= 0 ? vagasDisponiveis : 0;
        } else {
            return 0;
        }
    }

    public Integer buscarVagasDisponiveisDoPontoDeTaxiExcetoSelecionado() {
        if (pontoTaxi != null) {
            Integer vagasOcupadas = permissaoTransporteFacade.getPontoTaxiFacade().buscarNumeroDeVagasOcupadasExcetoPermissaoSelecionada(pontoTaxi, selecionado);
            return pontoTaxi.getTotalDeVagas() - vagasOcupadas;
        } else {
            return 0;
        }
    }

    public void recuperarDebitoRenovacaoNaoPago() {
        debitoRenovacaoPago = permissaoTransporteFacade.recuperarDebitoRenovacao(selecionado.getPermissionarioVigente().getCadastroEconomico());
        if (debitoRenovacaoPago) {
            tipoVerificacaoDebitoRenovacao = TipoVerificacaoDebitoRenovacao.VERIFICADO;
        } else {
            tipoVerificacaoDebitoRenovacao = TipoVerificacaoDebitoRenovacao.PENDENTE;
            selecionado.setDocumentosApresentados(false);
        }
    }

    public boolean getHabilitarDocumentos() {
        return debitoRenovacaoPago && (selecionado.getHabilitarVerificacaoDocumentos() == null || selecionado.getHabilitarVerificacaoDocumentos());
    }

    @Override
    @URLAction(mappingId = "verPermissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        definirSessao();

        selecionado = permissaoTransporteFacade.recuperar(getId());
        parametro = permissaoTransporteFacade.getParametrosTransitoTransporteFacade().recuperarParametroVigentePeloTipo(selecionado.getTipoPermissaoRBTrans());
        if (parametro != null) {
            permissionario = selecionado.getPermissionarioVigente();
            debitos = permissaoTransporteFacade.recuperaDebitosDeRBTransPorCMC(selecionado.getPermissionarioVigente().getCadastroEconomico());
            recuperarDebitoRenovacaoNaoPago();
            if (isSessao()) {
                try {
                    pontoTaxi = (PontoTaxi) Web.pegaDaSessao(PontoTaxi.class);
                    Web.pegaTodosFieldsNaSessao(this);
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage());
                }
            } else {
                novoMotoristaAuxiliar();
                pontoTaxi = null;
                novoVeiculoPermissionario();
                baixaInsercao = false;
                recuperaDependenciasPermissao();
                assistenteDetentorArquivoComposicao = new AssistenteDetentorArquivoComposicao(selecionado, new Date());
                operacao = Operacoes.VER;
            }
            carregaListaDeFotos();
        } else {
            redireciona();
            FacesUtil.addAtencao("Não existe parâmetro de " + selecionado.getTipoPermissaoRBTrans().getDescricao() + " cadastrado.");
        }
    }

    @Override
    public void novo() {
        super.novo();
        selecionado.setStatusLancamento(StatusLancamentoTransporte.AGUARDANDO_PGTO);
        selecionado.setInicioVigencia(SistemaFacade.getDataCorrente());
        novoPermissionario();
        novoVeiculoPermissionario();
        pontoTaxi = null;
    }

    public void atribuirMotoristaParaEditar(MotoristaAuxiliar ma) {
        motoristaAuxiliar = ma;
    }

    public void reativarMotorista() {
        try {
            motoristaAuxiliar.setFinalVigencia(null);
            motoristaAuxiliar = permissaoTransporteFacade.atualizarMotorista(motoristaAuxiliar);
            FacesUtil.redirecionamentoInterno(getUrlAtual());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void negaReativacaoMotorista() {
        novoMotoristaAuxiliar();
    }

    public void atualizarMotorista() {
        motoristaAuxiliar = permissaoTransporteFacade.atualizarMotorista(motoristaAuxiliar);
        carregaDependenciasMotoristaAuxiliar();
        FacesUtil.redirecionamentoInterno(getUrlAtual());
    }

    public void cancelarAtualizacaoMotorista() {
        novoMotoristaAuxiliar();
    }

    public void atribuiVeiculoPermissionarioParaEditar(VeiculoPermissionario vp) {
        veiculoPermissionario = (VeiculoPermissionario) Util.clonarObjeto(vp);
        if (veiculoPermissionario != null && veiculoPermissionario.getVeiculoTransporte() != null && veiculoPermissionario.getVeiculoTransporte().getModelo() != null) {
            marcaVeiculo = veiculoPermissionario.getVeiculoTransporte().getModelo().getMarca();
        }
    }


    public void reativarVeiculo() {
        try {
            veiculoPermissionario.setFinalVigencia(null);
            veiculoPermissionario = permissaoTransporteFacade.atualizaVeiculo(veiculoPermissionario);
            permissaoTransporteFacade.getCredencialRBTransFacade().baixarTodasCredenciaisPorPermissao(selecionado);
            FacesUtil.addAtencao("As credenciais da permissão foram baixadas!");
            FacesUtil.redirecionamentoInterno(getUrlAtual());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void negaReativacaoVeiculo() {
        novoVeiculoPermissionario();
    }

    public void atualizarVeiculo() {
        try {
            validarCamposVeiculo();
            veiculoPermissionario = permissaoTransporteFacade.atualizaVeiculo(veiculoPermissionario);
            permissaoTransporteFacade.getCredencialRBTransFacade().baixarTodasCredenciaisPorPermissao(selecionado);
            FacesUtil.atualizarComponente("tabelaVeiculos painel-geral-veiculo");
            FacesUtil.addAtencao("As credenciais da permissão foram baixadas!");
            FacesUtil.redirecionamentoInterno(getUrlAtual());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } finally {
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    public void cancelarAtualizacaoVeiculo() {
        novoVeiculoPermissionario();
    }

    public void atualizaPermissionario() {
        permissionario = (Permissionario) permissaoTransporteFacade.atualizaEntidade(permissionario);
    }


    public void uploadArquivo(FileUploadEvent evt) {
        assistenteDetentorArquivoComposicao.handleFileUpload(evt);
        permissaoTransporteFacade.retornaPermissaoPersistida(selecionado);

    }


    private void recuperaDependenciasPermissao() {
        if (permissionario.getCadastroEconomico().getPessoa() instanceof PessoaFisica) {
            permissionario = selecionado.getPermissionarioVigente();
            permissionario.setCadastroEconomico(permissaoTransporteFacade.getCadastroEconomicoFacade().recuperar(permissionario.getCadastroEconomico().getId()));
            permissionario.getCadastroEconomico().setPessoa(permissaoTransporteFacade.getPessoaFisicaFacade().recuperar(permissionario.getCadastroEconomico().getPessoa().getId()));
            if (permissionario.getCadastroEconomico().getPessoa() instanceof PessoaFisica) {
                rgPermissionario = permissaoTransporteFacade.getDocumentoPessoalFacade().recuperaRG((PessoaFisica) permissionario.getCadastroEconomico().getPessoa());
                cnhPermissionario = permissaoTransporteFacade.getDocumentoPessoalFacade().recuperaHabilitacao((PessoaFisica) permissionario.getCadastroEconomico().getPessoa());
            }
            telefonesPermissionario = permissaoTransporteFacade.getPessoaFisicaFacade().recuperarTelefonesPessoa(permissionario.getCadastroEconomico().getPessoa());
            enderecosPermissionario = permissaoTransporteFacade.getEnderecoCorreioFacade().recuperaEnderecosPessoa(permissionario.getCadastroEconomico().getPessoa());
        }
    }

    public void buscarDependenciasDoPermissionario() {
        if (permissionario.getCadastroEconomico().getPessoa() instanceof PessoaFisica) {
            permissionario.setCadastroEconomico(
                permissaoTransporteFacade.getCadastroEconomicoFacade().recuperar(
                    permissionario.getCadastroEconomico().getId()
                )
            );
            try {
                validarPermissionario(selecionado);
                permissionario.getCadastroEconomico().setPessoa(permissaoTransporteFacade.getPessoaFisicaFacade().recuperar(permissionario.getCadastroEconomico().getPessoa().getId()));
                if (permissionario.getCadastroEconomico().getPessoa() instanceof PessoaFisica) {
                    rgPermissionario = permissaoTransporteFacade.getDocumentoPessoalFacade().recuperaRG((PessoaFisica) permissionario.getCadastroEconomico().getPessoa());
                    cnhPermissionario = permissaoTransporteFacade.getDocumentoPessoalFacade().recuperaHabilitacao((PessoaFisica) permissionario.getCadastroEconomico().getPessoa());
                }
                telefonesPermissionario = permissaoTransporteFacade.getPessoaFisicaFacade().recuperarTelefonesPessoa(permissionario.getCadastroEconomico().getPessoa());
                enderecosPermissionario = permissaoTransporteFacade.getEnderecoCorreioFacade().recuperaEnderecosPessoa(permissionario.getCadastroEconomico().getPessoa());
                carregaListaDeFotos();
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
                novoPermissionario();
            }
        }
    }

    public void carregaDependenciasMotoristaAuxiliar() {
        if (motoristaAuxiliar.getCadastroEconomico().getPessoa() instanceof PessoaFisica) {
            motoristaAuxiliar.setCadastroEconomico(permissaoTransporteFacade.getCadastroEconomicoFacade().recuperar(motoristaAuxiliar.getCadastroEconomico().getId()));
            if (permissionario.getCadastroEconomico().getPessoa() instanceof PessoaFisica) {
                cnhMotoristaAuxiliar = permissaoTransporteFacade.getDocumentoPessoalFacade().recuperaHabilitacao((PessoaFisica) motoristaAuxiliar.getCadastroEconomico().getPessoa());
            }
            try {
                validaMotorista(motoristaAuxiliar);
                motoristaAuxiliar.getCadastroEconomico().setPessoa(permissaoTransporteFacade.getPessoaFisicaFacade().recuperar(motoristaAuxiliar.getCadastroEconomico().getPessoa().getId()));
                if (permissionario.getCadastroEconomico().getPessoa() instanceof PessoaFisica) {
                    rgMotoristaAuxiliar = permissaoTransporteFacade.getDocumentoPessoalFacade().recuperaRG((PessoaFisica) motoristaAuxiliar.getCadastroEconomico().getPessoa());
                }
                telefonesMotoristaAuxiliar = permissaoTransporteFacade.getPessoaFisicaFacade().recuperarTelefonesPessoa(motoristaAuxiliar.getCadastroEconomico().getPessoa());
                enderecosMotoristaAuxiliar = permissaoTransporteFacade.getEnderecoCorreioFacade().recuperaEnderecosPessoa(motoristaAuxiliar.getCadastroEconomico().getPessoa());
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            }
            carregaListaDeFotos();
        }
    }


    public void adicionarMotoristaAuxiliar() {
        try {
            validarInsercaoMotorista();
            motoristaAuxiliar.setPermissaoTransporte(selecionado);
            selecionado.getMotoristasAuxiliares().add(motoristaAuxiliar);
            permissaoTransporteFacade.gerarTermoPermissaoMotoristaAuxiliar(selecionado, parametro, motoristaAuxiliar);
            motoristaAuxiliar.setCalculoRBTrans(permissaoTransporteFacade.getCalculoRBTransFacade().calculaInsercaoMotorista(permissionario.getCadastroEconomico(), selecionado.getTipoPermissaoRBTrans()));
            gerarDebitosEDAM(motoristaAuxiliar.getCalculoRBTrans());
            selecionado = permissaoTransporteFacade.retornaPermissaoPersistida(selecionado);
            FacesUtil.addOperacaoRealizada("Motorista inserido com sucesso!");
            FacesUtil.redirecionamentoInterno(getUrlAtual());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void adicionarMotoristaGestor() {
        try {
            validarInsercaoMotorista();
            motoristaAuxiliar.setPermissaoTransporte(selecionado);
            selecionado.getMotoristasAuxiliares().add(motoristaAuxiliar);
            selecionado = permissaoTransporteFacade.retornaPermissaoPersistida(selecionado);
            FacesUtil.redirecionamentoInterno(getUrlAtual());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar o motorista");
        }
    }


    public void adicionarPermissionario() {
        permissionario.setPermissaoTransporte(selecionado);
        selecionado.getPermissionarios().add(permissionario);
    }

    public void baixarMotorista() {
        try {
            BaixaMotoristaAuxiliar baixa = new BaixaMotoristaAuxiliar();
            baixa.setUsuarioSistema(getSistemaControlador().getUsuarioCorrente());
            baixa.setRealizadaEm(new Date());
            baixa.setCalculoRBTrans(permissaoTransporteFacade.getCalculoRBTransFacade().calculaBaixaMotorista(permissionario.getCadastroEconomico(), selecionado.getTipoPermissaoRBTrans()));
            gerarDebitosEDAM(baixa.getCalculoRBTrans());
            motoristaAuxiliar.setFinalVigencia(new Date());
            baixa.setMotoristaAuxiliar(motoristaAuxiliar);
            motoristaAuxiliar.setBaixaMotoristaAuxiliar(baixa);
            selecionado = permissaoTransporteFacade.retornaPermissaoPersistida(selecionado);
            FacesUtil.addOperacaoRealizada("Motorista baixado com sucesso!");
            FacesUtil.redirecionamentoInterno(getUrlAtual());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
        }
    }

    public void preparaBaixaMotorista(MotoristaAuxiliar motoristaSelecionado) {
        motoristaAuxiliar = motoristaSelecionado;
    }

    public void negaBaixaMotorista() {
        novoMotoristaAuxiliar();
    }

    public void adicionarVeiculo() {
        try {
            validarCamposVeiculo();

            veiculoPermissionario.setPermissaoTransporte(selecionado);
            veiculoPermissionario.setCadastradoPor(getSistemaControlador().getUsuarioCorrente());
            veiculoPermissionario.getVistoriasVeiculo().add(criarNovaVistoriaVeículo());
            selecionado.getListaDeVeiculos().add(veiculoPermissionario);
            marcaVeiculo = null;
            novoVeiculoPermissionario();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarVeiculoVisualiza() {
        try {
            validarCamposVeiculo();
            selecionado = permissaoTransporteFacade.adicionarVeiculo(criarAssistente());
            FacesUtil.addAtencao("As credenciais da permissão foram baixadas!");
            FacesUtil.addOperacaoRealizada("Veículo inserido com sucesso!");
            FacesUtil.redirecionamentoInterno(getUrlAtual());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao adicionar veículo. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao adicionar veículo. Detalhes: " + e.getMessage());
        }
    }

    /*
     * Adicionar Veículo sem Taxas
     * para correção de migração
     */

    public void adicionarVeiculoGestor() {
        try {
            validarCamposVeiculo();
            selecionado = permissaoTransporteFacade.adicionarVeiculoGestor(criarAssistente());
            FacesUtil.addAtencao("As credenciais da permissão foram baixadas!");
            FacesUtil.redirecionamentoInterno(getUrlAtual());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void preparaBaixaVeiculo(VeiculoPermissionario veiculoSelecionado) {
        veiculoPermissionario = veiculoSelecionado;
    }

    public void preparaBaixaInsercaoVeiculo(VeiculoPermissionario veiculoSelecionado) {
        baixaInsercao = true;
        veiculoPermissionarioBaixaInsercao = veiculoSelecionado;
    }

    public void negaBaixaEinsercao() {
        baixaInsercao = false;
        veiculoPermissionarioBaixaInsercao = null;
    }

    public void baixarVeiculo() {
        try {
            validarBaixaVeiculo();
            selecionado = permissaoTransporteFacade.baixarVeiculo(criarAssistente());
            FacesUtil.addOperacaoRealizada("Veículo baixado com sucesso!");
            FacesUtil.redirecionamentoInterno(getUrlAtual());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao realizar Baixa de Veículo. Detalhes: " + e.getMessage());
            logger.error("Erro ao realizar baixa de veiculo. ", e);
        }
    }

    public void baixarAndInserirVeiculo() {
        try {
            validarBaixaVeiculo();
            validarCamposVeiculo();
            selecionado = permissaoTransporteFacade.baixarAndInserirVeiculo(criarAssistente());

            FacesUtil.addAtencao("As credenciais da permissão foram baixadas!");
            FacesUtil.addOperacaoRealizada("Processo de Baixa e Inserção realizado com sucesso!");
            FacesUtil.redirecionamentoInterno(getUrlAtual());

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao realizar Baixa e Inserção de Veículo. Detalhes: " + e.getMessage());
            logger.error("Erro ao realizar baixa e insercao de veiculo. ", e);
        }
    }

    private AssistentePermissaoTransporte criarAssistente() {
        AssistentePermissaoTransporte assistente = new AssistentePermissaoTransporte();
        assistente.setSelecionado(selecionado);
        assistente.setVeiculoPermissionarioBaixaInsercao(veiculoPermissionarioBaixaInsercao);
        assistente.setPermissionario(permissionario);
        assistente.setParametro(parametro);
        assistente.setVeiculoPermissionario(veiculoPermissionario);

        return assistente;
    }

    public void gerarDebitosEDAM(CalculoRBTrans calculo) {
        permissaoTransporteFacade.getCalculoRBTransFacade().gerarDebito(calculo);
    }

    public VistoriaVeiculo criarNovaVistoriaVeículo() {
        VistoriaVeiculo vistoria = new VistoriaVeiculo();
        vistoria.setVeiculoPermissionario(veiculoPermissionario);
        return vistoria;
    }

    public void removerVeiculo(VeiculoPermissionario veiculo) {
        selecionado.getListaDeVeiculos().remove(veiculo);
    }

    public void novoVeiculoPermissionario() {
        veiculoPermissionario = new VeiculoPermissionario();
        veiculoPermissionario.setInicioVigencia(new Date());
    }

    public void novoPermissionario() {
        permissionario = new Permissionario();
        permissionario.setInicioVigencia(new Date());
        rgPermissionario = null;
        cnhPermissionario = null;
        telefonesPermissionario = null;
        enderecosPermissionario = null;
    }

    public void novoMotoristaAuxiliar() {
        motoristaAuxiliar = new MotoristaAuxiliar();
        switch (selecionado.getTipoPermissaoRBTrans()) {
            case TAXI:
                motoristaAuxiliar.setTipo(TipoPermissaoRBTrans.TAXI);
                break;
            case MOTO_TAXI:
                motoristaAuxiliar.setTipo(TipoPermissaoRBTrans.MOTO_TAXI);
                break;
            case FRETE:
                motoristaAuxiliar.setTipo(TipoPermissaoRBTrans.FRETE);
                break;
        }

        motoristaAuxiliar.setInicioVigencia(new Date());
        rgMotoristaAuxiliar = null;
        cnhMotoristaAuxiliar = null;
        telefonesMotoristaAuxiliar = new ArrayList<>();
        enderecosMotoristaAuxiliar = new ArrayList<>();

    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            DigitoVencimento digitoVencimento = parametro.recuperarVencimentoPorDigitoTipo(permissaoTransporteFacade.retornaUltimoDigitoNumeroPermissao(selecionado.getNumero()), DigitoVencimento.TipoDigitoVencimento.LICENCIAMENTO);
            Calendar c = Calendar.getInstance();
            c.setTime(SistemaFacade.getDataCorrente());
            selecionado.setFinalVigencia(permissaoTransporteFacade.retornaDataFinalVigenciaPermissao(digitoVencimento.getDia(), digitoVencimento.getMes(), c.get(Calendar.YEAR) + 1));
            adicionarPermissionario();
            try {
                Integer numeroPermissao = permissaoTransporteFacade.sugereNumeroPermissaoPorTipo(selecionado.getTipoPermissaoRBTrans());
                if (!numeroPermissao.equals(selecionado.getNumero())) {
                    selecionado.setNumero(numeroPermissao);
                    FacesUtil.addInfo("Atenção", "A permissão foi salva com o número " + numeroPermissao);
                }

                selecionado = permissaoTransporteFacade.retornaPermissaoPersistida(selecionado);
                permissaoTransporteFacade.gerarTermoDePermissao(selecionado, parametro);
                if (selecionado.isMotoTaxi()) {
                    CalculoPermissao calculoPermissao = new CalculoPermissao();
                    calculoPermissao.setPermissaoTransporte(selecionado);
                    calculoPermissao.setCalculoRBTrans(permissaoTransporteFacade.getCalculoRBTransFacade().calculaOutorga(permissionario.getCadastroEconomico()));
                    gerarDebitosEDAM(calculoPermissao.getCalculoRBTrans());
                    selecionado.getCalculosPermissao().add(calculoPermissao);
                }

                if (selecionado.isFrete()) {
                    CalculoPermissao calculoPermissao = new CalculoPermissao();
                    calculoPermissao.setPermissaoTransporte(selecionado);
                    calculoPermissao.setCalculoRBTrans(permissaoTransporteFacade.getCalculoRBTransFacade().calculaAutorizacaoFuncionamento(permissionario.getCadastroEconomico()));
                    gerarDebitosEDAM(calculoPermissao.getCalculoRBTrans());
                    selecionado.getCalculosPermissao().add(calculoPermissao);
                    calculoPermissao = new CalculoPermissao();
                    calculoPermissao.setPermissaoTransporte(selecionado);
                    calculoPermissao.setCalculoRBTrans(permissaoTransporteFacade.getCalculoRBTransFacade().calculaRequerimento(permissionario.getCadastroEconomico(), selecionado.getTipoPermissaoRBTrans()));
                    gerarDebitosEDAM(calculoPermissao.getCalculoRBTrans());
                }

                for (VeiculoPermissionario vp : selecionado.getListaDeVeiculos()) {
                    if (vp.getFinalVigencia() == null) {
                        vp.setCalculoRBTrans(permissaoTransporteFacade.getCalculoRBTransFacade().calculaInsercaoVeiculo(permissionario.getCadastroEconomico(), selecionado.getTipoPermissaoRBTrans()));
                        gerarDebitosEDAM(vp.getCalculoRBTrans());
                        permissaoTransporteFacade.gerarTermoAutorizacaoVeiculo(selecionado, parametro, vp.getVeiculoTransporte());
                        if (selecionado.isTaxi()) {
                            vp.getVistoriasVeiculo().get(0).setCalculoRBTrans(permissaoTransporteFacade.getCalculoRBTransFacade().calculaVistoriaVeiculo(permissionario.getCadastroEconomico()));
                            gerarDebitosEDAM(vp.getVistoriasVeiculo().get(0).getCalculoRBTrans());
                        }
                    }
                }
                permissaoTransporteFacade.salva(selecionado);
                FacesUtil.redirecionamentoInterno("/permissao-de-transporte/ver/" + selecionado.getId());
            } catch (Exception ex) {
                FacesUtil.addError("Atenção!", "Ocorreu um erro ao realizar os cálculos. Detalhe: " + ex.getMessage());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<VeiculoPermissionario> veiculosVigentes() {
        List<VeiculoPermissionario> toReturn = new ArrayList<>();
        for (VeiculoPermissionario vp : selecionado.getListaDeVeiculos()) {
            if (vp.getFinalVigencia() == null) {
                toReturn.add(vp);
            }
        }
        return toReturn;
    }

    public List<MotoristaAuxiliar> motoristaAuxiliaresVigentes() {
        List<MotoristaAuxiliar> toReturn = new ArrayList<>();
        for (MotoristaAuxiliar motorista : selecionado.getMotoristasAuxiliares()) {
            if (motorista.getFinalVigencia() == null) {
                toReturn.add(motorista);
            }
        }
        return toReturn;
    }


    public void emitirDAM(ResultadoParcela resultadoParcela) throws Exception {
        emiteDAM(resultadoParcela);
    }

    public void emiteDAM(ResultadoParcela resultadoParcela) throws Exception {
        try {
            ParcelaValorDivida p = permissaoTransporteFacade.recuperaParcela(resultadoParcela.getIdParcela());


            DAM dam = permissaoTransporteFacade.getDamFacade().recuperaDAMPeloIdParcela(p.getId());
            if (dam == null) {
                dam = permissaoTransporteFacade.getDamFacade().geraDAM(p);
            }

            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.imprimirDamUnicoViaApi(Lists.newArrayList(dam));
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível gerar o DAM", "Ocorreu um problema no servidor, tente denovo, se o problema persistir entre em contato com o suporte");
            logger.error(e.getMessage());
        }
    }


    //  -------------------------------------------INICIO LISTENERS---------------------------------------------------------
    public void selecionaNumero() {
        if (selecionado.getNumero() != null) {
            try {
                ValidacaoException ve = new ValidacaoException();
                validarNumeroPermissao(selecionado, ve);
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        }
    }

//  ----------------------------------------------FIM LISTENERS---------------------------------------------------------

//  -------------------------------------------INICIO CONVERTERS--------------------------------------------------------

    public ConverterAutoComplete getConverterCidade() {
        if (converterCidade == null) {
            converterCidade = new ConverterAutoComplete(Cidade.class, permissaoTransporteFacade.getCidadeFacade());
        }
        return converterCidade;
    }

    public ConverterAutoComplete getConveterVeiculoTransporte() {
        if (conveterVeiculoTransporte == null) {
            conveterVeiculoTransporte = new ConverterAutoComplete(VeiculoTransporte.class, permissaoTransporteFacade.getVeiculoTransporteFacade());
        }

        return conveterVeiculoTransporte;
    }

    //  ---------------------------------------------FIM CONVERTERS---------------------------------------------------------

//  -------------------------------------------INICIO TERMOS------------------------------------------------------------

    public void verificarSeJaExisteConteudoTermo(TermoRBTrans termo) {
        termoSelecionado = termo;
        if (termoSelecionado.getDocumentoOficial() != null && termoSelecionado.getDocumentoOficial().getConteudo() != null) {
            FacesUtil.executaJavaScript("gerarNovoTermo.show()");
        } else {
            permissaoTransporteFacade.imprimirTermo(termoSelecionado, true);
        }
    }

    public void imprimirTermo(boolean novoConteudo) {
        permissaoTransporteFacade.imprimirTermo(termoSelecionado, novoConteudo);
        FacesUtil.executaJavaScript("gerarNovoTermo.hide()");
    }

//  ---------------------------------------------FIM TERMOS-------------------------------------------------------------


    //-----------------------------------------INICIO NAVIGATION METHODS ---------------------------------------------------
    public void navegaNovoPermissionario() {
        Web.poeTodosFieldsNaSessao(this);
        Web.poeNaSessao("permissaoTransporte", selecionado);
        Web.navegacao(getUrlAtual(), "/tributario/cadastroeconomico/novo/permissionario/");
    }

    public void navegaNovoMotorista() {
        Web.poeTodosFieldsNaSessao(this);
        Web.poeNaSessao("permissaoTransporte", selecionado);
        Web.navegacao(getUrlAtual(), "/tributario/cadastroeconomico/novo/motorista/");
    }

    public void navegaNovoPontoTaxi() {
        Web.poeTodosFieldsNaSessao(this);
        Web.navegacao(getUrlAtual(), "/ponto-de-taxi/novo/");
    }

    public void navegaNovaMarca() {
        Web.poeTodosFieldsNaSessao(this);
        Web.navegacao(getUrlAtual(), "/marca/novo/");
    }

    public void navegaNovoTipoVeiculo() {
        Web.poeTodosFieldsNaSessao(this);
        Web.navegacao(getUrlAtual(), "/rbtrans/tipo-veiculo/novo/");
    }

    public void navegaNovaCor() {
        Web.poeTodosFieldsNaSessao(this);
        Web.navegacao(getUrlAtual(), "/cor/novo/");
    }

    public void navegaNovoModelo() {
        Web.poeTodosFieldsNaSessao(this);
        Web.navegacao(getUrlAtual(), "/modelo/novo/");
    }

    public void navegaNovoCombustivel() {
        Web.poeTodosFieldsNaSessao(this);
        Web.navegacao(getUrlAtual(), "/frota/combustivel/novo/");
    }


//-----------------------------------------FIM NAVIGATION METHODS ------------------------------------------------------
//-----------------------------------------TRATAMENTO DE FOTO ----------------------------------------------------------

    public boolean mostraFoto(CadastroEconomico cmc) {
        if (cmc != null) {
            return pegaFoto(cmc.getInscricaoCadastral()) != null;
        }
        return false;

    }

    public void carregaListaDeFotos() {
        List<CadastroEconomico> cadastros = new ArrayList<>();
        if (permissionario.getCadastroEconomico() != null) {
            cadastros.add(permissionario.getCadastroEconomico());
        }
        if (motoristaAuxiliar != null) {
            if (motoristaAuxiliar.getCadastroEconomico() != null) {
                cadastros.add(motoristaAuxiliar.getCadastroEconomico());
            }
        }

        if (selecionado.getMotoristasAuxiliares() != null) {
            for (MotoristaAuxiliar motoristaAuxiliar : selecionado.getMotoristasAuxiliares()) {
                if (motoristaAuxiliar.getCadastroEconomico() != null) {
                    cadastros.add(motoristaAuxiliar.getCadastroEconomico());
                }
            }
        }
        carregaFotosNaSessao(cadastros);
    }


    public void carregaFotosNaSessao(List<CadastroEconomico> cadastros) {
        if (mapaFotos == null) {
            mapaFotos = Maps.newHashMap();
        }
        for (CadastroEconomico cmc : cadastros) {
            if (cmc.getPessoa() instanceof PessoaFisica) {
                Arquivo arq = cmc.getPessoa().getArquivo();
                if (arq != null) {
                    try {
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        for (ArquivoParte a : permissaoTransporteFacade.getPessoaFacade().getArquivoFacade().recuperaDependencias(arq.getId()).getPartes()) {
                            buffer.write(a.getDados());
                        }
                        InputStream inputStream = new ByteArrayInputStream(buffer.toByteArray());
                        DefaultStreamedContent foto = new DefaultStreamedContent(inputStream, arq.getMimeType(), arq.getNome());
                        mapaFotos.put(cmc.getInscricaoCadastral(), foto);
                    } catch (Exception ex) {
                        logger.error("Erro: ", ex);
                    }
                }
            }
        }
    }

    public DefaultStreamedContent getFoto() {
        String toReturn = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("cmcFoto");
        return pegaFoto(toReturn);
    }

    public DefaultStreamedContent pegaFoto(String cmc) {
        if (cmc != null) {
            if (mapaFotos == null) {
                mapaFotos = Maps.newHashMap();
            }
            DefaultStreamedContent defaultStreamedContent = mapaFotos.get(cmc);
            return defaultStreamedContent;
        }
        return null;
    }

    //--------------------------------------FIM TRATAMENTO DE FOTO ---------------------------------------------------------
//--------------------------------------------INÍCIO LISTS --------------------------------------------------------
    public List<SelectItem> getCategoriasVeiculo() {
        return Util.getListSelectItem(Arrays.asList(CategoriaVeiculo.values()));
    }

    public List<SelectItem> getTiposCalculo() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem("", null));
        for (TipoCalculoRBTRans tipo : TipoCalculoRBTRans.values()) {
            if (TipoCalculoRBTRans.TipoRequerenteDoCalculo.PERMISSIONARIO.equals(tipo.getTipoRequerente())) {
                List<TipoPermissaoRBTrans> tipoPermissao = Lists.newArrayList(tipo.getTiposPermissao());
                if (tipoPermissao.contains(selecionado.getTipoPermissaoRBTrans())) {
                    retorno.add(new SelectItem(tipo, tipo.getDescricao()));
                }
            }
        }
        return retorno;
    }
//-----------------------------------------------FIM LISTS --------------------------------------------------------

//--------------------------------------INÍCIO COMPLETE METHODS --------------------------------------------------------

    public List<CadastroEconomico> completaCMC(String parte) {
        return permissaoTransporteFacade.getCadastroEconomicoFacade().buscarCMCsPorNomeRazaoSocialOrCpfCnpjOrSituacao(parte);
    }

    public List<VeiculoTransporte> completarVeiculoTransporte(String parte) {
        return permissaoTransporteFacade.getVeiculoTransporteFacade().listaFiltrando(parte.trim().toLowerCase(), "placa");
    }

    public List<CadastroEconomico> completaMotorista(String parte) {
        return permissaoTransporteFacade.getCadastroEconomicoFacade().recuperaCMCmotorista(parte.toLowerCase());
    }

    public List<PontoTaxi> completaPontoTaxi(String parte) {
        return permissaoTransporteFacade.getPontoTaxiFacade().listaFiltrandoPontoTaxi(selecionado.getTipoPermissaoRBTrans(), parte.trim());
    }

    public List<Marca> completaMarca(String parte) {
        return permissaoTransporteFacade.getMarcaFacade().recuperarMarcasPeloTipo(parte.trim().toLowerCase(),
            TipoMarca.MARCA_VEICULO, TipoMarca.CARRO, TipoMarca.MOTOCICLETA, TipoMarca.VAN, TipoMarca.CAMINHAO);
    }

    public List<TipoVeiculo> completaTipoVeiculo(String parte) {
        return permissaoTransporteFacade.getTipoVeiculoFacade().listaFiltrando(parte.trim().toLowerCase(), "descricao");
    }


    public List<Cor> completaCor(String parte) {
        return permissaoTransporteFacade.getCorFacade().listaFiltrando(parte.trim().toLowerCase(), "descricao");
    }

    public List<Cidade> completaCidade(String parte) {
        List<Cidade> listaCidades = permissaoTransporteFacade.getCidadeFacade().listaFiltrando(parte.trim(), "nome");

        if (listaCidades == null) {
            return new ArrayList<Cidade>();
        } else {
            return listaCidades;
        }
    }

    public List<Modelo> completaModelo(String parte) {
        if (marcaVeiculo.getId() == null) {
            FacesUtil.addAtencao("Selecione a marca.");
        } else {
            try {
                if (marcaVeiculo != null) {
                    return permissaoTransporteFacade.getModeloFacade().listaPorMarca(parte.trim().toLowerCase(), marcaVeiculo);
                } else {
                    FacesUtil.addAtencao("A marca não foi selecionada.");
                    return new ArrayList<Modelo>();
                }
            } catch (Exception ex) {
                FacesUtil.addAtencao("A marca não foi selecionada.");
                return new ArrayList<Modelo>();
            }
        }
        return new ArrayList<Modelo>();
    }

    public List<Combustivel> completaCombustivel(String parte) {
        return permissaoTransporteFacade.getCombustivelFacade().listaFiltrando(parte.trim().toLowerCase(), "descricao");
    }

//--------------------------------------FIM COMPLETE METHODS -----------------------------------------------------------

    //----------------------------------------------INICIO GETTERS AND SETTERS----------------------------------------------
    private String getURLTipoPermissao() {
        switch (selecionado.getTipoPermissaoRBTrans()) {
            case TAXI:
                return "taxi/";
            case MOTO_TAXI:
                return "moto-taxi/";
            case FRETE:
                return "frete/";
        }
        return "";
    }

    private String getURLTipoOperacao() {
        if (getOperacao().equals(Operacoes.NOVO)) {
            return "novo/";
        }
        if (getOperacao().equals(Operacoes.EDITAR)) {
            return "editar/" + selecionado.getId() + "/";
        }
        if (getOperacao().equals(Operacoes.VER)) {
            return "ver/" + selecionado.getId() + "/";
        }
        return "";
    }

    public Permissionario getPermissionario() {
        return permissionario;
    }

    public void setPermissionario(Permissionario permissionario) {
        this.permissionario = permissionario;
    }


    public MotoristaAuxiliar getMotoristaAuxiliar() {
        return motoristaAuxiliar;
    }

    public void setMotoristaAuxiliar(MotoristaAuxiliar motoristaAuxiliar) {
        this.motoristaAuxiliar = motoristaAuxiliar;
    }

    public Map<String, DefaultStreamedContent> getMapaFotos() {
        return mapaFotos;
    }

    public void setMapaFotos(Map<String, DefaultStreamedContent> mapaFotos) {
        this.mapaFotos = mapaFotos;
    }

    public ParametrosTransitoTransporte getParametro() {
        return parametro;
    }

    public void setParametro(ParametrosTransitoTransporte parametro) {
        this.parametro = parametro;
    }

    public VeiculoPermissionario getVeiculoPermissionario() {
        return veiculoPermissionario;
    }

    public void setVeiculoPermissionario(VeiculoPermissionario veiculoPermissionario) {
        this.veiculoPermissionario = veiculoPermissionario;
    }

    public RG getRgPermissionario() {
        return rgPermissionario;
    }

    public void setRgPermissionario(RG rgPermissionario) {
        this.rgPermissionario = rgPermissionario;
    }

    public Habilitacao getCnhPermissionario() {
        return cnhPermissionario;
    }

    public void setCnhPermissionario(Habilitacao cnhPermissionario) {
        this.cnhPermissionario = cnhPermissionario;
    }

    public Marca getMarcaVeiculo() {
        return marcaVeiculo;
    }

    public void setMarcaVeiculo(Marca marcaVeiculo) {
        this.marcaVeiculo = marcaVeiculo;
    }

    public List<EnderecoCorreio> getEnderecosPermissionario() {
        return enderecosPermissionario;
    }

    public void setEnderecosPermissionario(List<EnderecoCorreio> enderecosPermissionario) {
        this.enderecosPermissionario = enderecosPermissionario;
    }

    public List<Telefone> getTelefonesPermissionario() {
        return telefonesPermissionario;
    }

    public void setTelefonesPermissionario(List<Telefone> telefonesPermissionario) {
        this.telefonesPermissionario = telefonesPermissionario;
    }

    public RG getRgMotoristaAuxiliar() {
        return rgMotoristaAuxiliar;
    }

    public void setRgMotoristaAuxiliar(RG rgMotoristaAuxiliar) {
        this.rgMotoristaAuxiliar = rgMotoristaAuxiliar;
    }

    public Habilitacao getCnhMotoristaAuxiliar() {
        return cnhMotoristaAuxiliar;
    }

    public void setCnhMotoristaAuxiliar(Habilitacao cnhMotoristaAuxiliar) {
        this.cnhMotoristaAuxiliar = cnhMotoristaAuxiliar;
    }

    public List<EnderecoCorreio> getEnderecosMotoristaAuxiliar() {
        return enderecosMotoristaAuxiliar;
    }

    public void setEnderecosMotoristaAuxiliar(List<EnderecoCorreio> enderecosMotoristaAuxiliar) {
        this.enderecosMotoristaAuxiliar = enderecosMotoristaAuxiliar;
    }

    public List<Telefone> getTelefonesMotoristaAuxiliar() {
        return telefonesMotoristaAuxiliar;
    }

    public void setTelefonesMotoristaAuxiliar(List<Telefone> telefonesMotoristaAuxiliar) {
        this.telefonesMotoristaAuxiliar = telefonesMotoristaAuxiliar;
    }

    public List<VoDebitosRBTrans> getDebitos() {
        return debitos;
    }

    public int getAbaAtiva() {
        return abaAtiva;
    }

    public void setAbaAtiva(int abaAtiva) {
        this.abaAtiva = abaAtiva;
    }

    public AssistenteDetentorArquivoComposicao getAssistenteDetentorArquivoComposicao() {
        return assistenteDetentorArquivoComposicao;
    }

    public void setAssistenteDetentorArquivoComposicao(AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao) {
        this.assistenteDetentorArquivoComposicao = assistenteDetentorArquivoComposicao;
    }

    public boolean isBaixaInsercao() {
        return baixaInsercao;
    }

    public void setBaixaInsercao(boolean baixaInsercao) {
        this.baixaInsercao = baixaInsercao;
    }

    public VeiculoPermissionario getVeiculoPermissionarioBaixaInsercao() {
        return veiculoPermissionarioBaixaInsercao;
    }

    public void setVeiculoPermissionarioBaixaInsercao(VeiculoPermissionario veiculoPermissionarioBaixaInsercao) {
        this.veiculoPermissionarioBaixaInsercao = veiculoPermissionarioBaixaInsercao;
    }

    public TipoCalculoRBTRans getTipoCalculoSuporte() {
        return tipoCalculoSuporte;
    }

    public void setTipoCalculoSuporte(TipoCalculoRBTRans tipoCalculoSuporte) {
        this.tipoCalculoSuporte = tipoCalculoSuporte;
    }

    public void validarBaixaVeiculo() {
        ValidacaoException ve = new ValidacaoException();
        if (!permissaoTransporteFacade.recuperaDebitosVencidosCMC(permissionario.getCadastroEconomico()).isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Permissionário possui débitos não pagos.");
        }
        ve.lancarException();
    }

    private void validarCamposVeiculo() {
        ValidacaoException ve = new ValidacaoException();

        if (permissionario.getCadastroEconomico() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para adicionar um veículo, deve-se selecionar um permissionário.");
        } else {
            if (!permissaoTransporteFacade.recuperaDebitosVencidosCMC(permissionario.getCadastroEconomico()).isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Permissionário possui débitos não pagos.");
            }
            if (selecionado.getVeiculoVigente() != null && !selecionado.getVeiculoVigente().equals(veiculoPermissionario) && !baixaInsercao) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A permissão já possuí um veículo vigente.");
            } else {
                if (StringUtils.isBlank(veiculoPermissionario.getVeiculoTransporte().getPlaca())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Placa do veículo deve ser informado.");
                }
                if (StringUtils.isBlank(veiculoPermissionario.getVeiculoTransporte().getEspecie())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Espécie do veículo deve ser informado.");
                }
                if (marcaVeiculo == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Marca do veículo deve ser informado.");
                }
                if (StringUtils.isBlank(veiculoPermissionario.getVeiculoTransporte().getNotaFiscal())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Nota Fiscal do veículo deve ser informado.");
                }
                if (veiculoPermissionario.getVeiculoTransporte().getCidade() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Cidade do emplacamento deve ser informado.");
                }
                if (StringUtils.isBlank(veiculoPermissionario.getVeiculoTransporte().getChassi())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Chassi do veículo deve ser informado.");
                }
                if (veiculoPermissionario.getVeiculoTransporte().getCombustivel() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Combustível do veículo deve ser informado.");
                }
                if (veiculoPermissionario.getVeiculoTransporte().getModelo() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Modelo do veículo deve ser informado.");
                }
                if (veiculoPermissionario.getVeiculoTransporte().getCor() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Cor Predominante do veículo deve ser informado.");
                }
                if (veiculoPermissionario.getVeiculoTransporte().getAnoFabricacao() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Ano de Fabricação do veículo deve ser informado.");
                } else if (permissaoTransporteFacade.getIdadeVeiculo(veiculoPermissionario.getVeiculoTransporte()) > parametro.getLimiteIdade()) {
                    ve.adicionarMensagemDeCampoObrigatorio("A idade do veículo não deve exceder " + parametro.getLimiteIdade() + " anos.");
                }
                if (veiculoPermissionario.getVeiculoTransporte().getAnoModelo() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Ano do Modelo do veículo deve ser informado.");
                }
            }
        }
        ve.lancarException();
    }

    public void validaMotorista(MotoristaAuxiliar motorista) {
        ValidacaoException ve = new ValidacaoException();

        if (!permissaoTransporteFacade.recuperaPermissoesAtivasDoPermissionario(motorista.getCadastroEconomico()).isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O C.M.C. selecionado é permissionário de outra permissão.");
        }

        if (!permissaoTransporteFacade.recuperaDebitosVencidosCMC(motorista.getCadastroEconomico()).isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Motorista possui débitos não pagos.");
        }

        if (cnhMotoristaAuxiliar == null) {
            ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "Atenção: O Motorista Auxiliar cadastrado não possui CNH");
        } else if (cnhMotoristaAuxiliar.getValidade() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A CNH do Motorista não possui validade.");
        } else if (cnhMotoristaAuxiliar.getValidade().before(new Date())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A CNH do Motorista está vencida.");
        }

        ve.lancarException();
    }

    private void validarPermissionario(PermissaoTransporte pt) {
        ValidacaoException ve = new ValidacaoException();
        List<PermissaoTransporte> permissoesAtivas = permissaoTransporteFacade.recuperaPermissoesAtivasDoPermissionario(permissionario.getCadastroEconomico());
        if (TipoPermissaoRBTrans.MOTO_TAXI.equals(pt.getTipoPermissaoRBTrans())) {
            if (!permissoesAtivas.isEmpty() && permissoesAtivas.size() >= 2) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O C.M.C. selecionado já possuí duas outras permissões.");
            }
        } else {
            if (!permissoesAtivas.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O C.M.C. selecionado já é permissionário de outra permissão.");
            }
        }
        List<PermissaoTransporte> permissoesVigentesDoResponsavelDoCMC = permissaoTransporteFacade.recuperaPermissoesVigentesDoResponsavelDoCMC(permissionario.getCadastroEconomico().getPessoa());
        switch (pt.getTipoPermissaoRBTrans()) {
            case TAXI:
                if (permissoesVigentesDoResponsavelDoCMC.size() >= 3) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O responsável pelo C.M.C. informado já possui 3 permissões vigentes.");
                }
                break;
            default:
                if (permissoesVigentesDoResponsavelDoCMC.size() > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O responsável pelo C.M.C. informado já possui uma permissão vigente.");
                }
        }

        if (!permissaoTransporteFacade.recuperaDebitosVencidosCMC(permissionario.getCadastroEconomico()).isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Permissionário possui débitos não pagos.");
        }
        ve.lancarException();
    }

    public void validarNumeroPermissao(PermissaoTransporte pt, ValidacaoException ve) {
        if (permissaoTransporteFacade.recuperaPermissaoVigentePorNumeroTipo(pt.getNumero(), pt.getTipoPermissaoRBTrans()) != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A permissão " + pt.getNumero() + " de " + pt.getTipoPermissaoRBTrans().getDescricao() + " já está em uso.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }


    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getNumero() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O número da permissão é um campo obrigatório.");
        } else {
            validarNumeroPermissao(selecionado, ve);
        }

        if (permissionario.getCadastroEconomico() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O permissionário é um campo obrigatório.");
        } else {
            validarPermissionario(selecionado);
        }
        if (permissionario.getNomeReduzido().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O nome reduzido é um campo obrigatório.");
        }
        if (permissaoTransporteFacade.numeroPermissaoRepetido(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Número de Permissão já existe.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
        validarDadosEspecificosPorTipoPermissao();
    }

    public void validarDadosEspecificosPorTipoPermissao() {
        switch (selecionado.getTipoPermissaoRBTrans()) {
            case TAXI:
            case FRETE:
                validarCamposTaxiEFrete();
            case MOTO_TAXI:
                validarCamposMotoTaxi();
        }
    }

    public void validarCamposTaxiEFrete() {
        if (selecionado.getPontoTaxi() != null) {
            pontoTaxi = selecionado.getPontoTaxi();
            validarVagasPontoDeTaxi();
        }
    }

    public void validarCamposMotoTaxi() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getPontoTaxi() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Ponto de Táxi é um campo obrigatório.");
        }

        if (selecionado.getVeiculoVigente() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário inserir um veículo.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
        pontoTaxi = selecionado.getPontoTaxi();
        validarVagasPontoDeTaxi();
    }


    private void validarInsercaoMotorista() {
        ValidacaoException ve = new ValidacaoException();
        if (getMotoristaAuxiliar().getCadastroEconomico() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Cadastro Econômico é um campo obrigatório.");
        } else {
            if (!permissaoTransporteFacade.recuperaDebitosVencidosCMC(permissionario.getCadastroEconomico()).isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Permissionário possui débitos não pagos.");
            }
            try {
                validaMotorista(motoristaAuxiliar);
            } catch (ValidacaoException val) {
                FacesUtil.printAllFacesMessages(val.getAllMensagens());
            }
        }

        if (getMotoristaAuxiliar().getNomeReduzidoMotorista() == null
            || getMotoristaAuxiliar().getNomeReduzidoMotorista().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O Nome Reduzido é um campo obrigatório.");
        }

        validarInsercaoMotoristaAuxiliar(ve);
        ve.lancarException();
    }

    private void validarInsercaoMotoristaAuxiliar(ValidacaoException ve) {
        if (motoristaAuxiliaresVigentes().size() >= 2) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A permissão já possui 2 motoristas auxiliares vigentes.");
        }
    }

    //----------------------------------------Termina Validações------------------------------------------------------------
    public boolean getPermissaoReativarVeiculoEspecial() {
        return permissaoTransporteFacade.getUsuarioSistemaFacade()
            .validaAutorizacaoUsuario(
                permissaoTransporteFacade.getSistemaFacade().getUsuarioCorrente()
                , AutorizacaoTributario.PERMITIR_REATIVAR_VEICULO_ESPECIAL);
    }

    public boolean getPermissaoInserirVeiculoEspecial() {
        return permissaoTransporteFacade.getUsuarioSistemaFacade()
            .validaAutorizacaoUsuario(
                permissaoTransporteFacade.getSistemaFacade().getUsuarioCorrente()
                , AutorizacaoTributario.PERMITIR_INSERIR_VEICULO_ESPECIAL);
    }

    public boolean getPermissaoBaixarVeiculoEspecial() {
        return permissaoTransporteFacade.getUsuarioSistemaFacade()
            .validaAutorizacaoUsuario(
                permissaoTransporteFacade.getSistemaFacade().getUsuarioCorrente()
                , AutorizacaoTributario.PERMITIR_BAIXAR_VEICULO_ESPECIAL);
    }

    public boolean getPermissaoReativarMotoristaEspecial() {
        return permissaoTransporteFacade.getUsuarioSistemaFacade()
            .validaAutorizacaoUsuario(
                permissaoTransporteFacade.getSistemaFacade().getUsuarioCorrente()
                , AutorizacaoTributario.PERMITIR_REATIVAR_MOTORISTA_ESPECIAL);
    }

    public boolean getPermissaoInserirMotoristaEspecial() {
        return permissaoTransporteFacade.getUsuarioSistemaFacade()
            .validaAutorizacaoUsuario(
                permissaoTransporteFacade.getSistemaFacade().getUsuarioCorrente()
                , AutorizacaoTributario.PERMITIR_INSERIR_MOTORISTA_ESPECIAL);
    }

    public boolean getPermissaoBaixarMotoristaEspecial() {
        return permissaoTransporteFacade.getUsuarioSistemaFacade()
            .validaAutorizacaoUsuario(
                permissaoTransporteFacade.getSistemaFacade().getUsuarioCorrente()
                , AutorizacaoTributario.PERMITIR_BAIXAR_MOTORISTA_ESPECIAL);
    }

    public void iniciarCamposCalculoSuporte() {
        tipoCalculoSuporte = null;
    }

    public void gerarCalculoSuporte() {
        if (tipoCalculoSuporte != null) {
            CalculoPermissao calculoPermissao = new CalculoPermissao();
            calculoPermissao.setPermissaoTransporte(selecionado);
            calculoPermissao.setCalculoRBTrans(permissaoTransporteFacade.getCalculoRBTransFacade().calcular(selecionado, tipoCalculoSuporte));
            gerarDebitosEDAM(calculoPermissao.getCalculoRBTrans());
            selecionado.getCalculosPermissao().add(calculoPermissao);
        } else {
            FacesUtil.addCampoObrigatorio("Informe o tipo de cálculo!");
        }
    }

    public boolean usuarioTemAutorizacaoParaGerarCalculo() {
        return permissaoTransporteFacade.getUsuarioSistemaFacade().validaAutorizacaoUsuario(getSistemaControlador().getUsuarioCorrente(), AutorizacaoTributario.GERAR_QUALQUER_CALCULO_PERMISSAO_TRANSITO_TRANSPORTE);
    }

    public boolean getRenderizarVencimento() {
        return selecionado != null && selecionado.getDocumentosApresentados() != null && (selecionado.getDocumentosApresentados() && debitoRenovacaoPago);
    }

    public Date recuperarCredencialDeMaiorVencimento() {
        dataVencimentoPermissao = permissaoTransporteFacade.recuperarCredencialDeMaiorVencimento(selecionado);
        selecionado.setTipoVerificacaoDebitoRenovacao(tipoVerificacaoDebitoRenovacao);
        selecionado.setHabilitarVerificacaoDocumentos(false);
        salvarPermissao();
        if (dataVencimentoPermissao != null) {
            return dataVencimentoPermissao;
        }
        return null;
    }

    public void documentoNaoVerificado() {
        selecionado.setDocumentosApresentados(false);
        salvarPermissao();
    }

    private void validarVagasDisponiveis() {
        ValidacaoException ve = new ValidacaoException();
        if (buscarVagasDisponiveisDoPontoDeTaxi() <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível informar esse Ponto de Taxi pois não existem vagas disponíveis!");
        }
        ve.lancarException();
    }

    public void salvarPermissao() {
        try {
            validarVagasDisponiveis();
            selecionado.setPontoTaxi(pontoTaxi);
            selecionado = permissaoTransporteFacade.recuperar(permissaoTransporteFacade.retornaPermissaoPersistida(selecionado).getId());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addErrorGenerico(ex);
        }
    }


    private void validarVagasPontoDeTaxi() {
        ValidacaoException ve = new ValidacaoException();
        if (pontoTaxi == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum Ponto de Taxi foi selecionado!");
            throw ve;
        }
        if (selecionado.getId() == null) {
            if (buscarVagasDisponiveisDoPontoDeTaxi() <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não há vagas disponíveis para o ponto de taxi " + pontoTaxi + "!");
                throw ve;
            }
        } else if (buscarVagasDisponiveisDoPontoDeTaxiExcetoSelecionado() <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não há vagas disponíveis para o ponto de taxi " + pontoTaxi + "!");
            throw ve;
        }
    }

    public void confirmarDocumentos() {
        if (selecionado.getDocumentosApresentados()) {
            FacesUtil.executaJavaScript("confirmarDocumentos.show()");
        } else {
            documentoNaoVerificado();
            FacesUtil.atualizarComponente("Formulario:tabViewGeral:panelVencimento");
        }
    }

    public PontoTaxi getPontoTaxi() {
        return pontoTaxi;
    }

    public void setPontoTaxi(PontoTaxi pontoTaxi) {
        this.pontoTaxi = pontoTaxi;
    }

    public void verificarModeloVeiculo() {
        if (veiculoPermissionario != null && marcaVeiculo != null && veiculoPermissionario.getVeiculoTransporte() != null &&
            veiculoPermissionario.getVeiculoTransporte().getModelo() != null &&
            !marcaVeiculo.equals(veiculoPermissionario.getVeiculoTransporte().getModelo().getMarca())) {

            veiculoPermissionario.getVeiculoTransporte().setModelo(null);
        }
    }
}
