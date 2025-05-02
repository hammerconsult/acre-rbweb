package br.com.webpublico.entidades.tributario;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class ProcessoLicenciamentoAmbientalPessoa extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Pessoa pessoa;
    @OneToOne(cascade = CascadeType.ALL)
    private EnderecoCorreio enderecoCorreio;
    @OneToOne(cascade = CascadeType.ALL)
    private EnderecoCorreio enderecoCorrespondencia;
    private String telefone;
    private String email;
    @OneToOne(cascade ={CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;

    public ProcessoLicenciamentoAmbientalPessoa() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public EnderecoCorreio getEnderecoCorreio() {
        return enderecoCorreio;
    }

    public void setEnderecoCorreio(EnderecoCorreio enderecoCorreio) {
        this.enderecoCorreio = enderecoCorreio;
    }

    public EnderecoCorreio getEnderecoCorrespondencia() {
        return enderecoCorrespondencia;
    }

    public void setEnderecoCorrespondencia(EnderecoCorreio enderecoCorrespondencia) {
        this.enderecoCorrespondencia = enderecoCorrespondencia;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }
}
