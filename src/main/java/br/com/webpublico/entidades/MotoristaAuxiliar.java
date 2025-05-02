/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusLancamentoTransporte;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Motorista Auxiliar")
@Inheritance(strategy = InheritanceType.JOINED)
public class MotoristaAuxiliar implements Serializable, Comparable<MotoristaAuxiliar> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Cadastro Econômico")
    private CadastroEconomico cadastroEconomico;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Etiqueta("Inicio da Vigência")
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Etiqueta("Final da Vigência")
    private Date finalVigencia;
    @Etiqueta("Status")
    @Enumerated(EnumType.STRING)
    private StatusLancamentoTransporte statusLancamento;
    @Etiqueta("Permissão")
    @ManyToOne
    private PermissaoTransporte permissaoTransporte;
    @Etiqueta("Sindicato")
    private String sindicato;
    @Etiqueta("Pasta")
    private String pasta;
    @Etiqueta("Tipo")
    @Enumerated(EnumType.STRING)
    private TipoPermissaoRBTrans tipo;
    private Integer numero;
    @Transient
    @Invisivel
    private Long criadoEm;
    @OneToOne(mappedBy = "motoristaAuxiliar", cascade = CascadeType.ALL, orphanRemoval = true)
    private BaixaMotoristaAuxiliar baixaMotoristaAuxiliar;
    private String migracaoChave;
    private String nomeReduzidoMotorista;
    @OneToOne
    private CalculoRBTrans calculoRBTrans;


    public MotoristaAuxiliar() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public PermissaoTransporte getPermissaoTransporte() {
        return permissaoTransporte;
    }

    public void setPermissaoTransporte(PermissaoTransporte permissaoTransporte) {
        this.permissaoTransporte = permissaoTransporte;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public void setPessoaFisica(PessoaFisica pf) {
        this.cadastroEconomico.setPessoa(pf);
    }

    public PessoaFisica getPessoaFisica() {
        if (cadastroEconomico == null) {
            return new PessoaFisica();
        }
        return (PessoaFisica) cadastroEconomico.getPessoa();
    }

    public StatusLancamentoTransporte getStatusLancamento() {
        return statusLancamento;
    }

    public void setStatusLancamento(StatusLancamentoTransporte status) {
        this.statusLancamento = status;
    }

    public TipoPermissaoRBTrans getTipo() {
        return tipo;
    }

    public void setTipo(TipoPermissaoRBTrans tipo) {
        this.tipo = tipo;
    }

    public String getPasta() {
        return pasta;
    }

    public void setPasta(String pasta) {
        this.pasta = pasta;
    }

    public String getSindicato() {
        return sindicato;
    }

    public void setSindicato(String sindicato) {
        this.sindicato = sindicato;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BaixaMotoristaAuxiliar getBaixaMotoristaAuxiliar() {
        return baixaMotoristaAuxiliar;
    }

    public void setBaixaMotoristaAuxiliar(BaixaMotoristaAuxiliar baixaMotoristaAuxiliar) {
        this.baixaMotoristaAuxiliar = baixaMotoristaAuxiliar;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public String getNomeReduzidoMotorista() {
        return nomeReduzidoMotorista;
    }

    public void setNomeReduzidoMotorista(String nomeReduzidoMotorista) {
        this.nomeReduzidoMotorista = nomeReduzidoMotorista;
    }

    public CalculoRBTrans getCalculoRBTrans() {
        return calculoRBTrans;
    }

    public void setCalculoRBTrans(CalculoRBTrans calculoRBTrans) {
        this.calculoRBTrans = calculoRBTrans;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.MotoristaAuxiliar[ id=" + id + " ]";
    }

    public boolean isTodosOsDocumentosTrue() {
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.getType().equals(Boolean.class) || field.getType().equals(Boolean.TYPE)) {
                    return (boolean)field.get(this);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return true;
    }

    public void tornarTodosOsDocumentosTrue() {
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.getType().equals(Boolean.class) || field.getType().equals(Boolean.TYPE)) {
                    field.set(this, Boolean.TRUE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void tornarTodosOsDocumentosFalse() {
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.getType().equals(Boolean.class) || field.getType().equals(Boolean.TYPE)) {
                    field.set(this, Boolean.FALSE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public int compareTo(MotoristaAuxiliar o) {
        return o.id.compareTo(this.id);
    }
}
