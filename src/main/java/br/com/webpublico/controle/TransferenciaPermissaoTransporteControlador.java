package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteDetentorArquivoComposicao;
import br.com.webpublico.entidadesauxiliares.VoDebitosRBTrans;
import br.com.webpublico.enums.TipoCalculoRBTRans;
import br.com.webpublico.enums.TipoMotivoTransferenciaPermissao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TransferenciaPermissaoTransporteFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "transferenciaPermissaoTransporteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaTransferencia", pattern = "/transferencia-permissao-transporte/novo/",
        viewId = "/faces/tributario/rbtrans/transferencia/edita.xhtml"),
    @URLMapping(id = "editarTransferencia", pattern = "/transferencia-permissao-transporte/editar/#{transferenciaPermissaoTransporteControlador.id}/",
        viewId = "/faces/tributario/rbtrans/transferencia/edita.xhtml"),
    @URLMapping(id = "listarTransferencia", pattern = "/transferencia-permissao-transporte/listar/",
        viewId = "/faces/tributario/rbtrans/transferencia/lista.xhtml"),
    @URLMapping(id = "verTransferencia", pattern = "/transferencia-permissao-transporte/ver/#{transferenciaPermissaoTransporteControlador.id}/",
        viewId = "/faces/tributario/rbtrans/transferencia/visualizar.xhtml")
})
public class TransferenciaPermissaoTransporteControlador extends PrettyControlador<TransferenciaPermissaoTransporte> implements Serializable, CRUD {

    @EJB
    private TransferenciaPermissaoTransporteFacade transferenciaPermissaoTransporteFacade;
    private CadastroEconomico cadastroEconomico, cadastroEconomicoAntigo;
    private PermissaoTransporte permissaoAntiga;
    private Boolean podeIniciarTransferencia;
    private Boolean podeTransfererir;
    private List<MotoristaAuxiliar> motoristasAntigos;
    private VeiculoPermissionario veiculoAntigo;
    private List<EnderecoCorreio> enderecosAntigo, enderecosNovo;
    private List<Telefone> telefonesAntigo, telefonesNovo;
    private RG rgAntigo, rgNovo;
    private Map<CadastroEconomico, List<TaxaTransito>> taxas;
    private ParametrosTransitoTransporte parametros;
    private AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao;


    public TransferenciaPermissaoTransporteControlador() {
        super(TransferenciaPermissaoTransporte.class);
    }

    public List<CadastroEconomico> getCadastros() {
        if (taxas != null) {
            return Lists.newArrayList(taxas.keySet());
        } else {
            return null;
        }
    }

    public CadastroEconomico getCadastroEconomicoAntigo() {
        return cadastroEconomicoAntigo;
    }

    public Map<CadastroEconomico, List<TaxaTransito>> getTaxas() {
        return taxas;
    }

    public List<EnderecoCorreio> getEnderecosAntigo() {
        return enderecosAntigo;
    }

    public void setEnderecosAntigo(List<EnderecoCorreio> enderecosAntigo) {
        this.enderecosAntigo = enderecosAntigo;
    }

    public List<EnderecoCorreio> getEnderecosNovo() {
        return enderecosNovo;
    }

    public void setEnderecosNovo(List<EnderecoCorreio> enderecosNovo) {
        this.enderecosNovo = enderecosNovo;
    }

    public List<Telefone> getTelefonesAntigo() {
        return telefonesAntigo;
    }

    public void setTelefonesAntigo(List<Telefone> telefonesAntigo) {
        this.telefonesAntigo = telefonesAntigo;
    }

    public List<Telefone> getTelefonesNovo() {
        return telefonesNovo;
    }

    public void setTelefonesNovo(List<Telefone> telefonesNovo) {
        this.telefonesNovo = telefonesNovo;
    }

    public RG getRgAntigo() {
        return rgAntigo;
    }

    public void setRgAntigo(RG rgAntigo) {
        this.rgAntigo = rgAntigo;
    }

    public RG getRgNovo() {
        return rgNovo;
    }

    public void setRgNovo(RG rgNovo) {
        this.rgNovo = rgNovo;
    }

    public AssistenteDetentorArquivoComposicao getAssistenteDetentorArquivoComposicao() {
        return assistenteDetentorArquivoComposicao;
    }

    public void setAssistenteDetentorArquivoComposicao(AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao) {
        this.assistenteDetentorArquivoComposicao = assistenteDetentorArquivoComposicao;
    }

    public List<MotoristaAuxiliar> getMotoristasAntigos() {
        return motoristasAntigos;
    }

    public void setMotoristasAntigos(List<MotoristaAuxiliar> motoristasAntigos) {
        this.motoristasAntigos = motoristasAntigos;
    }

    public VeiculoPermissionario getVeiculoAntigo() {
        return veiculoAntigo;
    }

    public void setVeiculoAntigo(VeiculoPermissionario veiculoAntigo) {
        this.veiculoAntigo = veiculoAntigo;
    }

    public Boolean getPodeIniciarTransferencia() {
        return podeIniciarTransferencia;
    }

    public Boolean getPodeTransfererir() {
        return podeTransfererir;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public PermissaoTransporte getPermissaoAntiga() {
        return permissaoAntiga;
    }

    public void setPermissaoAntiga(PermissaoTransporte permissaoAntiga) {
        this.permissaoAntiga = permissaoAntiga;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/transferencia-permissao-transporte/";
    }

    @Override
    public AbstractFacade getFacede() {
        return transferenciaPermissaoTransporteFacade;
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novaTransferencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        try {
            definirSessao();
            if (isSessao()) {
                Web.pegaTodosFieldsNaSessao(this);
            }
            if (selecionado == null) {
                instanciaNovo();
            }
        } catch (Exception e) {
            instanciaNovo();
            e.printStackTrace();
        }

    }

    private void instanciaNovo() {
        podeIniciarTransferencia = false;
        podeTransfererir = false;
        super.novo();
        selecionado.setMotivo(TipoMotivoTransferenciaPermissao.CESSAO);

    }

    @URLAction(mappingId = "verTransferencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.editar();
        assistenteDetentorArquivoComposicao = new AssistenteDetentorArquivoComposicao(selecionado.getCertidaoObitoRBTrans(), new Date());
        permissaoAntiga = transferenciaPermissaoTransporteFacade.getPermissaoTransporteFacade().recuperar(
            selecionado.getPermissaoAntiga().getId()
        );
        cadastroEconomico = transferenciaPermissaoTransporteFacade.getPermissaoTransporteFacade().getCadastroEconomicoFacade().recuperar(
            selecionado.getPermissaoNova().getId()
        );
        carregaCadastro();
        carregaPermissao();
    }

    @URLAction(mappingId = "editarTransferencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + getId());
        FacesUtil.addOperacaoNaoPermitida("Não é possível editar uma Transferência de Veículos");
    }


    public List<PermissaoTransporte> completaPermissoes(String parte) {
        return transferenciaPermissaoTransporteFacade.listaPermissoesPorCmc(parte.toLowerCase());
    }

    public List<CadastroEconomico> completaCMC(String parte) {
        if (permissaoAntiga != null) {
            return transferenciaPermissaoTransporteFacade.getPermissaoTransporteFacade().getCadastroEconomicoFacade().buscarCMCsPorNomeRazaoSocialOrCpfCnpjOrSituacao(parte);
        }
        return null;
    }

    public void recuperaPermissao() {
        permissaoAntiga = transferenciaPermissaoTransporteFacade.getPermissaoTransporteFacade().recuperar(permissaoAntiga.getId());
        parametros = transferenciaPermissaoTransporteFacade.getParametrosTransitoTransporteFacade()
            .recuperarParametroVigentePeloTipo(permissaoAntiga.getTipoPermissaoRBTrans());
        if (parametros != null) {
            carregaPermissao();
            validaPermissao();

        } else {
            FacesUtil.addAtencao("Não foi encontrado o parametro adequado para o tipo " + permissaoAntiga.getTipoPermissaoRBTrans().getDescricao() + ", verifique a configuração de parametros");
        }
    }

    private void carregaPermissao() {
        veiculoAntigo = permissaoAntiga.getVeiculoVigente();
        motoristasAntigos = permissaoAntiga.getMotoristasAuxiliaresVigentes();
        enderecosAntigo = permissaoAntiga.getPermissionarioVigente().getCadastroEconomico().getPessoa().getEnderecos();
        telefonesAntigo = permissaoAntiga.getPermissionarioVigente().getCadastroEconomico().getPessoa().getTelefones();
        if (permissaoAntiga.getPermissionarioVigente().getCadastroEconomico().getPessoa() instanceof PessoaFisica) {
            rgAntigo = ((PessoaFisica) permissaoAntiga.getPermissionarioVigente().getCadastroEconomico().getPessoa()).getRg();
        }
        cadastroEconomicoAntigo = permissaoAntiga.getPermissionarioVigente().getCadastroEconomico();
    }

    public void recuperaCadastro() {
        cadastroEconomico = transferenciaPermissaoTransporteFacade.getPermissaoTransporteFacade().getCadastroEconomicoFacade().recuperar(cadastroEconomico.getId());
        carregaCadastro();
        validaNovoCadastro();
        recuperaTaxas();
    }

    private void carregaCadastro() {
        enderecosNovo = cadastroEconomico.getPessoa().getEnderecos();
        telefonesNovo = cadastroEconomico.getPessoa().getTelefones();
        if (cadastroEconomico.getPessoa() instanceof PessoaFisica) {
            rgAntigo = ((PessoaFisica) cadastroEconomico.getPessoa()).getRg();
        }
    }

    private void validaNovoCadastro() {
        podeTransfererir = true;
        List<VoDebitosRBTrans> voDebitosRBTranses = transferenciaPermissaoTransporteFacade
            .getPermissaoTransporteFacade().recuperaDebitosVencidosDeRBTransPorCMC(cadastroEconomico);
        if (!voDebitosRBTranses.isEmpty()) {
            podeTransfererir = false;
            FacesUtil.addOperacaoNaoPermitida("Há débitos EM ABERTO para o C.M.C selecionado");
        }
    /*    List<PermissaoTransporte> permissaoTransportes = transferenciaPermissaoTransporteFacade
                .getPermissaoTransporteFacade().recuperaPermissoesVigentesDoResponsavelDoCMC(cadastroEconomico.getPessoa());
        if (!permissaoTransportes.isEmpty()) {
            podeTransfererir = false;
            FacesUtil.addOperacaoNaoPermitida("A pessoa vinculada ao C.M.C selecionado já tem outras permissões de transporte");
        }*/
        if (podeTransfererir) {
            FacesUtil.addOperacaoRealizada("A permissão já pode ser transferida");
        }
    }


    public void validaPermissao() {
        podeIniciarTransferencia = true;
        List<VoDebitosRBTrans> voDebitosRBTranses = transferenciaPermissaoTransporteFacade.getPermissaoTransporteFacade()
            .recuperaDebitosVencidosDeRBTransPorCMC(permissaoAntiga.getPermissionarioVigente().getCadastroEconomico());
        if (!voDebitosRBTranses.isEmpty()) {
            podeIniciarTransferencia = false;
            FacesUtil.addOperacaoNaoPermitida("Há débitos EM ABERTO para a permissão selecionada");
        }
        if (!permissaoAntiga.getMotoristasAuxiliaresVigentes().isEmpty()) {
            podeIniciarTransferencia = false;
            FacesUtil.addOperacaoNaoPermitida("Há motoristas vigêntes para a permissão selecionada, baixe os motoristas antes de transferir a permissão");
        }
        if (permissaoAntiga.isMotoTaxi()) {
            int quantidadeMaximaPermissoesMotoTaxi = transferenciaPermissaoTransporteFacade.getParametrosTransitoTransporteFacade().maximoTransferenciasMotoTaxi();
            if (transferenciaPermissaoTransporteFacade.getPermissaoTransporteFacade().quantidadeTransferencias(permissaoAntiga) >= quantidadeMaximaPermissoesMotoTaxi) {
                podeIniciarTransferencia = false;
                FacesUtil.addOperacaoNaoPermitida("Essa permissão de Moto-Taxi já possui o máximo de transferências permitidas.");
            }
        }
        if (podeIniciarTransferencia) {
            FacesUtil.addOperacaoRealizada("Selecione o C.M.C que irá receber a permissão de transporte");
        }
    }

    public void transferePermissao() {
        if (validaTransferencia()) {
            try {
                selecionado = transferenciaPermissaoTransporteFacade.transferePermissao(selecionado, permissaoAntiga, cadastroEconomico);
                gerarDebitosEDAM(selecionado.getCalculoRBTrans());

                if (selecionado.getCalculoMotoTaxi() != null) {
                    gerarDebitosEDAM(selecionado.getCalculoMotoTaxi());
                }
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
            } catch (Exception e) {
                FacesUtil.addErrorGenerico(e);
                e.printStackTrace();
            }
        }
    }

    public void gerarDebitosEDAM(CalculoRBTrans calculo) {
        transferenciaPermissaoTransporteFacade.getPermissaoTransporteFacade().getCalculoRBTransFacade().gerarDebito(calculo);
        transferenciaPermissaoTransporteFacade.getPermissaoTransporteFacade().getCalculoRBTransFacade().gerarDAM(calculo);
    }

    public boolean validaCertidaoObito(boolean valida) {
        if (TipoMotivoTransferenciaPermissao.FALECIMENTO.name().equals(selecionado.getMotivo().name())) {
            if (selecionado.getCertidaoObitoRBTrans().getMatricula() == null
                || selecionado.getCertidaoObitoRBTrans().getMatricula().isEmpty()) {
                valida = false;
                FacesUtil.addCampoObrigatorio("Informe a matrícula da certidão de óbito.");
            }
            if (selecionado.getCertidaoObitoRBTrans().getDataFalecimento() == null) {
                valida = false;
                FacesUtil.addCampoObrigatorio("Informe a data do óbito.");
            }
            if (selecionado.getCertidaoObitoRBTrans().getLocalSepultamento() == null
                || selecionado.getCertidaoObitoRBTrans().getLocalSepultamento().isEmpty()) {
                valida = false;
                FacesUtil.addCampoObrigatorio("Informe o local de sepultamento.");
            }
            if (selecionado.getCertidaoObitoRBTrans().getNomeOficio() == null
                || selecionado.getCertidaoObitoRBTrans().getNomeOficio().isEmpty()) {
                valida = false;
                FacesUtil.addCampoObrigatorio("Informe o nome do ofício.");
            }
        }
        return valida;
    }

    private boolean validaTransferencia() {
        boolean valida = true;

        valida = validaCertidaoObito(valida);

        if (permissaoAntiga == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("Informe a permissão de transporte que será transferida");
        }
        if (cadastroEconomico == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("Informe o C.M.C que irá receber a permissão de transporte");
        }
        if (selecionado.getMotivo() == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("Informe o Motivo da Transferência");
        }

        if (veiculoAntigo != null
            && !selecionado.getTransfereVeiculo()) {
            valida = false;
            FacesUtil.addOperacaoNaoPermitida("Para transferir uma permissão sem transferir o veículo vigente, deve-se baixar o veículo primeiro");
        }
        return valida;
    }

    public List<SelectItem> getTiposTransferencia() {
        return Util.getListSelectItem(TipoMotivoTransferenciaPermissao.values());
    }

    public void instanciaCertidaoObito() {
        selecionado.setCertidaoObitoRBTrans(new CertidaoObitoRBTrans(selecionado));
        assistenteDetentorArquivoComposicao = new AssistenteDetentorArquivoComposicao(selecionado.getCertidaoObitoRBTrans(), new Date());
    }

    public void cancelaInstanciaCertidaoObito() {
        selecionado.setCertidaoObitoRBTrans(null);
    }

    public void recuperaTaxas() {
        if (!selecionado.getTransfereVeiculo()) {
            selecionado.setGeraCredencialVeiculo(Boolean.FALSE);
        }

        if (veiculoAntigo != null
            && !selecionado.getTransfereVeiculo()) {

            FacesUtil.addOperacaoNaoPermitida("Para transferir uma permissão sem transferir o veículo vigente, deve-se baixar o veículo primeiro");
            taxas = new HashMap<>();
        } else {
            if (TipoMotivoTransferenciaPermissao.CESSAO.equals(selecionado.getMotivo())) {
                cancelaInstanciaCertidaoObito();
                taxas = Maps.newHashMap();
                List<TaxaTransito> taxaTransferencia = transferenciaPermissaoTransporteFacade
                    .getParametrosTransitoTransporteFacade()
                    .recuperarTaxaTransitoPeloTipo(parametros, TipoCalculoRBTRans.TRANSFERENCIA_PERMISSAO);

                addMapTaxas(taxas, cadastroEconomico, taxaTransferencia);

                if (veiculoAntigo != null) {
                    List<TaxaTransito> taxaBaixa = transferenciaPermissaoTransporteFacade
                        .getParametrosTransitoTransporteFacade().recuperarTaxaTransitoPeloTipo(parametros,
                            TipoCalculoRBTRans.BAIXA_VEICULO);

                    addMapTaxas(taxas, cadastroEconomicoAntigo, taxaBaixa);
                    if (selecionado.getTransfereVeiculo()) {
                        List<TaxaTransito> taxaVeiculo = transferenciaPermissaoTransporteFacade
                            .getParametrosTransitoTransporteFacade()
                            .recuperarTaxaTransitoPeloTipo(parametros, TipoCalculoRBTRans.INSERCAO_VEICULO);
                        addMapTaxas(taxas, cadastroEconomico, taxaVeiculo);

                        if (permissaoAntiga.isTaxi()) {
                            List<TaxaTransito> taxaVistoria = transferenciaPermissaoTransporteFacade
                                .getParametrosTransitoTransporteFacade()
                                .recuperarTaxaTransitoPeloTipo(parametros, TipoCalculoRBTRans.VISTORIA_VEICULO);
                            addMapTaxas(taxas, cadastroEconomico, taxaVistoria);
                        }
                    }
                }

                if (permissaoAntiga.isMotoTaxi()) {
                    int quantidade = transferenciaPermissaoTransporteFacade.getPermissaoTransporteFacade()
                        .quantidadeTransferencias(permissaoAntiga);

                    List<TaxaTransito> taxaTransferenciaMotoTaxi = transferenciaPermissaoTransporteFacade
                        .getParametrosTransitoTransporteFacade()
                        .recuperarTaxaTransitoTransferenciaMotoTaxi(parametros, quantidade + 1);

                    addMapTaxas(taxas, cadastroEconomicoAntigo, taxaTransferenciaMotoTaxi);
                }
            } else {
                instanciaCertidaoObito();
                taxas = Maps.newHashMap();
                if (veiculoAntigo != null) {
                    if (selecionado.getTransfereVeiculo()) {
                        List<TaxaTransito> taxaVeiculo = transferenciaPermissaoTransporteFacade
                            .getParametrosTransitoTransporteFacade()
                            .recuperarTaxaTransitoPeloTipo(parametros, TipoCalculoRBTRans.INSERCAO_VEICULO);
                        addMapTaxas(taxas, cadastroEconomico, taxaVeiculo);

                        if (permissaoAntiga.isTaxi()) {
                            List<TaxaTransito> taxaVistoria = transferenciaPermissaoTransporteFacade
                                .getParametrosTransitoTransporteFacade()
                                .recuperarTaxaTransitoPeloTipo(parametros, TipoCalculoRBTRans.VISTORIA_VEICULO);
                            addMapTaxas(taxas, cadastroEconomico, taxaVistoria);
                        }

                    }

                }
            }
        }
    }

    private void addMapTaxas(Map<CadastroEconomico, List<TaxaTransito>> mapa, CadastroEconomico ce, List<TaxaTransito> taxa) {
        if (mapa.containsKey(ce)) {
            mapa.get(ce).addAll(taxa);
        } else {
            mapa.put(ce, taxa);
        }
    }

    public void navegaNovoPermissionario() {
        Web.poeTodosFieldsNaSessao(this);
        Web.poeNaSessao("permissaoTransporte", permissaoAntiga);
        Web.navegacao(getUrlAtual(), "/tributario/cadastroeconomico/novo/permissionario/");
    }
}
