package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.interfaces.PossuidorArquivo;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ConfiguracaoNfseRelatorio extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String secretaria;
    private String departamento;
    private String endereco;
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao arquivoBrasao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(String secretaria) {
        this.secretaria = secretaria;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return this.arquivoBrasao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.arquivoBrasao = detentorArquivoComposicao;
    }
}
