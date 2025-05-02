package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class BancoContaConfTributario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private SubConta subConta;
    @ManyToOne
    private ConfiguracaoTributario configuracao;
    private Boolean ativo;
    @Transient
    private Long criadoEm;

    public BancoContaConfTributario() {
        criadoEm = System.nanoTime();
        ativo = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public ConfiguracaoTributario getConfiguracao() {
        return configuracao;
    }

    public void setConfiguracao(ConfiguracaoTributario configuracao) {
        this.configuracao = configuracao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
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
        StringBuilder sb = new StringBuilder("");
        if (subConta != null) {
            if (subConta.getContaBancariaEntidade() != null) {
                if (subConta.getContaBancariaEntidade().getAgencia() != null) {
                    if (subConta.getContaBancariaEntidade().getAgencia().getBanco() != null) {
                        sb.append(subConta.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco()).append(" - ");
                        sb.append(subConta.getContaBancariaEntidade().getAgencia().getBanco().getDescricao()).append(" - ");
                    }
                    sb.append(subConta.getContaBancariaEntidade().getAgencia().getNumeroAgencia()).append("-");
                    sb.append(subConta.getContaBancariaEntidade().getAgencia().getDigitoVerificador()).append(" - ");
                    sb.append(subConta.getContaBancariaEntidade().getAgencia().getNomeAgencia()).append(" - ");
                }
                sb.append(subConta.getContaBancariaEntidade().getNumeroConta()).append(" - ");
                sb.append(subConta.getContaBancariaEntidade().getDigitoVerificador()).append(" - ");
                sb.append(subConta.getContaBancariaEntidade().getNomeConta()).append(" - ");
            }
            sb.append(subConta.getCodigo());
        }
        return sb.toString().replace("null", " ");
    }
}
