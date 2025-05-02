/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusLancamentoTransporte;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.enums.TipoVerificacaoDebitoRenovacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author cheles
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Permissão de Transporte")
public class PermissaoTransporte implements Serializable, PossuidorArquivo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Início de Vigência")
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Invisivel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número Permissão")
    private Integer numero;
    @Etiqueta("Status")
    @Enumerated(EnumType.STRING)
    private StatusLancamentoTransporte statusLancamento;
    @Invisivel
    @Transient
    private Long criadoEm;
    private String migracaoChave;

    @Tabelavel
    @Etiqueta("Tipo de Permissão")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoPermissaoRBTrans tipoPermissaoRBTrans;
    private String restricao;
    @Tabelavel
    @Etiqueta("Credencial Emitida")
    private Boolean emitiuCredencial;
    @ManyToOne
    private PontoTaxi pontoTaxi;
    @Transient
    @Tabelavel
    @Etiqueta("Permissionário")
    private String permissionarioNaLista;
    /**
     * Validações:
     * - O Motorista não pode ser Permissionário vigente;
     * - O Motorista não pode ser vigente em 2 permissoões ao mesmo tempo;
     * - O Motorista não pode ser cadastrado caso o CMC do permissionário possua débitos, da RBTrans e ISS, vencidos;
     * - O Motorista não pode ser cadastrado caso o CMC do Motorista possua débitos, da RBTrans e ISS, vencidos;
     * - O Motorista não pode ser cadastrado caso já tenha a quantidade necessárioa para a modalidade da permissão:
     * - Táxi = 2;
     * - Moto-Táxi = 1;
     * - Frete = 0
     */
    @OneToMany(mappedBy = "permissaoTransporte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MotoristaAuxiliar> motoristasAuxiliares;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "permissaoTransporte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Permissionario> permissionarios;
    @Etiqueta("Veículo")
    @OneToMany(mappedBy = "permissaoTransporte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VeiculoPermissionario> listaDeVeiculos;
    @OneToMany(mappedBy = "permissaoTransporte", cascade = CascadeType.ALL)
    private List<CalculoPermissao> calculosPermissao;
    @OneToMany(mappedBy = "permissaoTransporte")
    private List<SegundaViaCredencial> listaDeSegundaViaCredencial;
    @OneToMany(mappedBy = "permissaoTransporte", cascade = CascadeType.ALL)
    private List<BloqueioPermissaoFisacalizacao> listaBloqueioPermissaoFiscalizacao;
    @OneToMany(mappedBy = "permissaoTransporte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TermoRBTrans> listaTermos;
    @OneToMany(mappedBy = "permissaoTransporte", cascade = CascadeType.ALL)
    private List<RenovacaoPermissao> renovacoes;
    @OneToMany(mappedBy = "permissaoTransporte")
    private List<CredencialRBTrans> credenciais;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;
    private Boolean documentosApresentados = false;
    @Enumerated(EnumType.STRING)
    private TipoVerificacaoDebitoRenovacao tipoVerificacaoDebitoRenovacao;
    private Boolean habilitarVerificacaoDocumentos;

    public PermissaoTransporte() {
        criadoEm = System.nanoTime();
        emitiuCredencial = false;
        restricao = null;
        permissionarios = new ArrayList<>();
        motoristasAuxiliares = new ArrayList<>();
        listaDeVeiculos = new ArrayList<>();
        listaDeSegundaViaCredencial = new ArrayList<>();
        listaBloqueioPermissaoFiscalizacao = new ArrayList<>();
        calculosPermissao = new ArrayList<>();
        listaTermos = new ArrayList<>();
        renovacoes = new ArrayList<>();
        credenciais = new ArrayList<>();
    }

    public PermissaoTransporte(Long id, Date inicioVigencia, Integer numero, TipoPermissaoRBTrans tipoPermissaoRBTrans, CadastroEconomico cadastroEconomico) {
        this.id = id;
        this.inicioVigencia = inicioVigencia;
        this.numero = numero;
        this.tipoPermissaoRBTrans = tipoPermissaoRBTrans;
        this.permissionarioNaLista = cadastroEconomico.getCmcNomeCpfCnpj();
    }

    public Permissionario getPermissionarioVigente() {
        for (Permissionario permissionario : getPermissionarios()) {
            if (DataUtil.isVigente(permissionario.getInicioVigencia(), permissionario.getFinalVigencia())) {
                return permissionario;
            }
        }
        return null;
    }

    public PermissaoTransporte(Long id) {
        this.criadoEm = System.nanoTime();
        this.id = id;
    }

    public TipoVerificacaoDebitoRenovacao getTipoVerificacaoDebitoRenovacao() {
        return tipoVerificacaoDebitoRenovacao;
    }

    public void setTipoVerificacaoDebitoRenovacao(TipoVerificacaoDebitoRenovacao tipoVerificacaoDebitoRenovacao) {
        this.tipoVerificacaoDebitoRenovacao = tipoVerificacaoDebitoRenovacao;
    }

    public Boolean getHabilitarVerificacaoDocumentos() {
        return habilitarVerificacaoDocumentos;
    }

    public void setHabilitarVerificacaoDocumentos(Boolean habilitarVerificacaoDocumentos) {
        this.habilitarVerificacaoDocumentos = habilitarVerificacaoDocumentos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<TermoRBTrans> getListaTermos() {
        return listaTermos;
    }

    public void setListaTermos(List<TermoRBTrans> listaTermos) {
        this.listaTermos = listaTermos;
    }

    public TipoPermissaoRBTrans getTipoPermissaoRBTrans() {
        return tipoPermissaoRBTrans;
    }

    public void setTipoPermissaoRBTrans(TipoPermissaoRBTrans tipoPermissaoRBTrans) {
        this.tipoPermissaoRBTrans = tipoPermissaoRBTrans;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public PontoTaxi getPontoTaxi() {
        return pontoTaxi;
    }

    public void setPontoTaxi(PontoTaxi pontoTaxi) {
        this.pontoTaxi = pontoTaxi;
    }

    @Deprecated
    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public String getRestricao() {
        return restricao;
    }

    public void setRestricao(String restricao) {
        this.restricao = restricao;
    }

    @Deprecated
    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public StatusLancamentoTransporte getStatusLancamento() {
        return statusLancamento;
    }

    public void setStatusLancamento(StatusLancamentoTransporte statusPagamentoTransporte) {
        this.statusLancamento = statusPagamentoTransporte;
    }

    public Boolean getDocumentosApresentados() {
        return documentosApresentados;
    }

    public void setDocumentosApresentados(Boolean documentosApresentados) {
        this.documentosApresentados = documentosApresentados;
    }

    public List<Permissionario> getPermissionarios() {
        return permissionarios;
    }

    public void setPermissionarios(List<Permissionario> permissionarios) {
        this.permissionarios = permissionarios;
    }

    public List<VeiculoPermissionario> getListaDeVeiculos() {
        return listaDeVeiculos;
    }

    public void setListaDeVeiculos(List<VeiculoPermissionario> lista) {
        this.listaDeVeiculos = lista;
    }


    public List<MotoristaAuxiliar> getMotoristasAuxiliares() {
        return motoristasAuxiliares;
    }

    public List<MotoristaAuxiliar> getMotoristasAuxiliaresVigentes() {
        List<MotoristaAuxiliar> vigentes = Lists.newArrayList();
        for (MotoristaAuxiliar motorista : motoristasAuxiliares) {
            if (motorista.getFinalVigencia() == null) {
                vigentes.add(motorista);
            }
        }

        return vigentes;
    }

    public void setMotoristasAuxiliares(List<MotoristaAuxiliar> motoristasAuxiliares) {
        this.motoristasAuxiliares = motoristasAuxiliares;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public List<SegundaViaCredencial> getListaDeSegundaViaCredencial() {
        return listaDeSegundaViaCredencial;
    }

    public void setListaDeSegundaViaCredencial(List<SegundaViaCredencial> listaDeSegundaViaCredencial) {
        this.listaDeSegundaViaCredencial = listaDeSegundaViaCredencial;
    }

    public List<BloqueioPermissaoFisacalizacao> getListaBloqueioPermissaoFiscalizacao() {
        return listaBloqueioPermissaoFiscalizacao;
    }

    public void setListaBloqueioPermissaoFiscalizacao(List<BloqueioPermissaoFisacalizacao> listaBloqueioPermissaoFiscalizacao) {
        this.listaBloqueioPermissaoFiscalizacao = listaBloqueioPermissaoFiscalizacao;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    public VeiculoPermissionario getVeiculoVigente() {
        if (listaDeVeiculos == null) {
            novaListaDeVeiculos();
        }
        for (VeiculoPermissionario veiculo : listaDeVeiculos) {
            if (veiculo.getInicioVigencia() != null && veiculo.getFinalVigencia() == null) {
                return veiculo;
            }
        }

        return null;
    }

    public VeiculoPermissionario obterVeiculoSemVigencia() {
        if (listaDeVeiculos == null) {
            novaListaDeVeiculos();
        }
        for (VeiculoPermissionario veiculo : listaDeVeiculos) {
            if (veiculo.getInicioVigencia() != null && veiculo.getFinalVigencia() != null) {
                VeiculoPermissionario veiculoRetorno = new VeiculoPermissionario();
                if (veiculoRetorno.getId() == null || veiculo.getId() > veiculoRetorno.getId()) {
                    veiculoRetorno = veiculo;
                }
                return veiculoRetorno;
            }
        }

        return null;
    }

    public void novaListaDeVeiculos() {
        this.listaDeVeiculos = new ArrayList<VeiculoPermissionario>();
    }

    public boolean isTaxi() {
        return getTipoPermissaoRBTrans().equals(TipoPermissaoRBTrans.TAXI);
    }

    public boolean isMotoTaxi() {
        return getTipoPermissaoRBTrans().equals(TipoPermissaoRBTrans.MOTO_TAXI);
    }

    public boolean isFrete() {
        return getTipoPermissaoRBTrans().equals(TipoPermissaoRBTrans.FRETE);
    }

    public boolean todosOsDocumentosTrue() {
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.getType().equals(Boolean.class) || field.getType().equals(Boolean.TYPE)) {
                    return (boolean) field.get(this);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return true;
    }

    public String getIdentificacaoPermissionario() {
        String toReturn = "";

        Permissionario permissionarioVigente = getPermissionarioVigente();
        if (permissionarioVigente != null) {
            toReturn = permissionarioVigente.getCadastroEconomico() != null
                ? permissionarioVigente.getCadastroEconomico().getInscricaoCadastral() : "";
            toReturn = toReturn + (permissionarioVigente.getCadastroEconomico().getPessoa() != null
                ? " - " + permissionarioVigente.getCadastroEconomico().getPessoa().getNome() : "");
        }
        return toReturn;
    }

    public List<CalculoPermissao> getCalculosPermissao() {
        return calculosPermissao;
    }


    public String getDescricaoAutoComplete() {
        Permissionario permissionarioVigente = getPermissionarioVigente();
        StringBuilder sb = new StringBuilder();
        if (permissionarioVigente != null) {
            sb.append(numero);
            sb.append(" - ");
            if (permissionarioVigente.getCadastroEconomico() != null) {
                sb.append(permissionarioVigente.getCadastroEconomico().getInscricaoCadastral());
                if (permissionarioVigente.getCadastroEconomico().getPessoa() != null) {
                    sb.append(" - ");
                    sb.append(permissionarioVigente.getCadastroEconomico().getPessoa().getNomeAutoComplete());
                }
            }
            sb.append(" (").append(tipoPermissaoRBTrans.getDescricao()).append(")");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getDescricaoAutoComplete();
    }

    public String getPermissionarioNaLista() {
        return permissionarioNaLista;
    }

    public void setPermissionarioNaLista(String permissionarioNaLista) {
        this.permissionarioNaLista = permissionarioNaLista;
    }

    public void setCalculosPermissao(List<CalculoPermissao> calculosPermissao) {
        this.calculosPermissao = calculosPermissao;
    }

    public List<RenovacaoPermissao> getRenovacoes() {
        return renovacoes;
    }

    public void setRenovacoes(List<RenovacaoPermissao> renovacoes) {
        this.renovacoes = renovacoes;
    }

    public List<CredencialRBTrans> getCredenciais() {
        return credenciais;
    }

    public void setCredenciais(List<CredencialRBTrans> credenciais) {
        this.credenciais = credenciais;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }
}
