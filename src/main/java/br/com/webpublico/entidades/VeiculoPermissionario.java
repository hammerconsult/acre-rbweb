/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.CategoriaVeiculo;
import br.com.webpublico.enums.StatusLancamentoTransporte;
import br.com.webpublico.enums.StatusPagamentoBaixa;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author cheles
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Veículo do Permissionário")
public class VeiculoPermissionario implements Serializable, Comparable<VeiculoPermissionario> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Inicio de Vigência")
    @Obrigatorio
    private Date inicioVigencia;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Final de Vigência")
    @Obrigatorio
    private Date finalVigencia;
    @Etiqueta("Permissão de Transporte")
    @ManyToOne
    private PermissaoTransporte permissaoTransporte;
    @Etiqueta("Veículo de Transporte")
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private VeiculoTransporte veiculoTransporte;
    @Etiqueta("Status do Pagamento")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private StatusLancamentoTransporte statusLancamento;
    private Boolean transferido;
    @Enumerated(EnumType.STRING)
    private CategoriaVeiculo categoriaVeiculo;
    @Invisivel
    @Transient
    private Long criadoEm;
    @OneToOne(mappedBy = "veiculoPermissionario")
    private BaixaVeiculoPermissionario baixaVeiculoPermissionario;
    @ManyToOne
    private CalculoRBTrans calculoRBTrans;
    /*
     * O correto aqui seria ter uma única referência para
     * ProcessoCalculoVeiculoPermissionario, porém, para manter o padrão de como
     * é utilizado, resolveu-se manter um List, e dentro dele sempre existirá
     * somente um elemento ProcessoCalculoVeiculoPermissionario.
     */
    @OneToMany(mappedBy = "veiculoPermissionario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VistoriaVeiculo> vistoriasVeiculo;
    private String migracaoChave;
    /**
     * Campo criado para alocar os dados da migração.
     * O Sistema deve usar o campo UsuárioSistema para novos registros.
     */
    private String usuarioLegado;
    @ManyToOne
    private UsuarioSistema cadastradoPor;
    @OneToMany(mappedBy = "veiculoPermissionario")
    private List<PropagandaTaxidoor> listaDePropagandaTaxidoor;

    public VeiculoPermissionario() {
        this.criadoEm = System.nanoTime();
        vistoriasVeiculo = new ArrayList<>();
        veiculoTransporte = new VeiculoTransporte();
    }

    public VeiculoPermissionario(PermissaoTransporte permissao) {
        super();
        this.permissaoTransporte = permissao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTransferido(boolean transferido) {
        this.transferido = transferido;
    }

    public Boolean getTransferido() {
        return transferido;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public StatusLancamentoTransporte getStatusLancamento() {
        return statusLancamento;
    }

    public void setStatusLancamento(StatusLancamentoTransporte statusPagamentoTransporte) {
        this.statusLancamento = statusPagamentoTransporte;
    }

    public PermissaoTransporte getPermissaoTransporte() {
        return permissaoTransporte;
    }

    public void setPermissaoTransporte(PermissaoTransporte permissaoTransporte) {
        this.permissaoTransporte = permissaoTransporte;
    }

    public VeiculoTransporte getVeiculoTransporte() {
        return veiculoTransporte;
    }

    public void setVeiculoTransporte(VeiculoTransporte veiculoTransporte) {
        this.veiculoTransporte = veiculoTransporte;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public BaixaVeiculoPermissionario getBaixaVeiculoPermissionario() {
        return baixaVeiculoPermissionario;
    }

    public void setBaixaVeiculoPermissionario(BaixaVeiculoPermissionario baixaVeiculoPermissionario) {
        this.baixaVeiculoPermissionario = baixaVeiculoPermissionario;
    }

    public List<VistoriaVeiculo> getVistoriasVeiculo() {
        return vistoriasVeiculo;
    }

    public void setVistoriasVeiculo(List<VistoriaVeiculo> vistoriasVeiculo) {
        this.vistoriasVeiculo = vistoriasVeiculo;
    }

    public CalculoRBTrans getCalculoRBTrans() {
        return calculoRBTrans;
    }

    public void setCalculoRBTrans(CalculoRBTrans calculoRBTrans) {
        this.calculoRBTrans = calculoRBTrans;
    }

    public String getUsuarioLegado() {
        return usuarioLegado;
    }

    public void setUsuarioLegado(String usuarioLegado) {
        this.usuarioLegado = usuarioLegado;
    }

    public UsuarioSistema getCadastradoPor() {
        return cadastradoPor;
    }

    public void setCadastradoPor(UsuarioSistema cadastradoPor) {
        this.cadastradoPor = cadastradoPor;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public CategoriaVeiculo getCategoriaVeiculo() {
        return categoriaVeiculo;
    }

    public void setCategoriaVeiculo(CategoriaVeiculo categoriaVeiculo) {
        this.categoriaVeiculo = categoriaVeiculo;
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "VeiculoPermissionario[ id=" + id + " ]";
    }

    @Override
    public int compareTo(VeiculoPermissionario vp) {
        return vp.inicioVigencia.compareTo(this.inicioVigencia);
    }

    public BaixaVeiculoPermissionario obterBaixaVeiculoPermissionario() {
        return this.getBaixaVeiculoPermissionario();
    }

    public VistoriaVeiculo recuperarVistoriaEmAberto() {
        try {
            for (VistoriaVeiculo vistoriaVeiculo : getVistoriasVeiculo()) {
                if (vistoriaVeiculo.getStatusPagamento().equals(StatusPagamentoBaixa.NAO_PAGO)) {
                    return vistoriaVeiculo;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public String getDescricao() {
        try {
            String marca = getVeiculoTransporte().getModelo().getMarca().getDescricao();
            String modelo = getVeiculoTransporte().getModelo().getDescricao();
            String anoFab = getVeiculoTransporte().getAnoFabricacao().toString();
            String anoMod = getVeiculoTransporte().getAnoModelo().toString();
            return marca + " " + modelo + " " + anoFab + "/" + anoMod;
        } catch (Exception ex) {
            String marca = "";
            String modelo = "";
            String anoFab = "";
            String anoMod = "";
            ex.printStackTrace();
            return marca + " " + modelo + " " + anoFab + "/" + anoMod;
        }


    }
}
