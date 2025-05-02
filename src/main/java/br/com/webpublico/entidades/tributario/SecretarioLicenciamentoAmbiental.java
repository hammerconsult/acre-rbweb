package br.com.webpublico.entidades.tributario;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class SecretarioLicenciamentoAmbiental extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PessoaFisica secretario;
    @OneToOne(cascade = CascadeType.ALL)
    private Arquivo arquivo;
    private String decretoNomeacao;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public PessoaFisica getSecretario() {
        return secretario;
    }

    public void setSecretario(PessoaFisica secretario) {
        this.secretario = secretario;
    }

    public String getDecretoNomeacao() {
        return decretoNomeacao;
    }

    public void setDecretoNomeacao(String decretoNomeacao) {
        this.decretoNomeacao = decretoNomeacao;
    }

    @Override
    public String toString() {
        return getSecretario() != null ? getSecretario().getNome() : "";
    }

    public static SecretarioLicenciamentoAmbiental clonar(SecretarioLicenciamentoAmbiental secretario) {
        SecretarioLicenciamentoAmbiental novoSecretario = new SecretarioLicenciamentoAmbiental();
        novoSecretario.setSecretario(secretario.getSecretario());
        novoSecretario.setArquivo(Arquivo.clonar(secretario.getArquivo()));
        novoSecretario.setDecretoNomeacao(secretario.getDecretoNomeacao());
        return novoSecretario;
    }
}
