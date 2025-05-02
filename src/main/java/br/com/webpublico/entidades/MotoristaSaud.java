package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class MotoristaSaud extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date dataCadastro;
    @ManyToOne
    private PessoaFisica pessoaFisica;
    private Boolean ativo;

    @Transient
    private EnderecoCorreio enderecoPrincipal;
    @Transient
    private Telefone telefonePrincipal;
    @Transient
    private Habilitacao cnh;
    @Transient
    private Arquivo foto;

    public MotoristaSaud() {
        super();
        this.ativo = Boolean.TRUE;
        this.dataCadastro = new Date();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public EnderecoCorreio getEnderecoPrincipal() {
        return enderecoPrincipal;
    }

    public void setEnderecoPrincipal(EnderecoCorreio enderecoPrincipal) {
        this.enderecoPrincipal = enderecoPrincipal;
    }

    public Telefone getTelefonePrincipal() {
        return telefonePrincipal;
    }

    public void setTelefonePrincipal(Telefone telefonePrincipal) {
        this.telefonePrincipal = telefonePrincipal;
    }

    public Habilitacao getCnh() {
        return cnh;
    }

    public void setCnh(Habilitacao cnh) {
        this.cnh = cnh;
    }

    public Arquivo getFoto() {
        return foto;
    }

    public void setFoto(Arquivo foto) {
        this.foto = foto;
    }

    @Override
    public void realizarValidacoes() {
        super.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        if (pessoaFisica == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Pessoa Física deve ser informado.");
        } else {
            if (cnh == null) {
                ve.adicionarMensagemDeCampoObrigatorio("A Pessoa Física não possui CNH informada.");
            } else if (cnh.getValidade().before(new Date())) {
                ve.adicionarMensagemDeCampoObrigatorio("A CNH da Pessoa Física está vencida.");
            }
            if (telefonePrincipal == null) {
                ve.adicionarMensagemDeCampoObrigatorio("A Pessoa Física não possui telefone informado.");
            }
            if (enderecoPrincipal == null) {
                ve.adicionarMensagemDeCampoObrigatorio("A Pessoa Física não possui endereço informado.");
            }
            if (pessoaFisica.getDataNascimento() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("A Pessoa Física não possui data de nascimento informada.");
            }
            if (foto == null) {
                ve.adicionarMensagemDeCampoObrigatorio("A Pessoa Física não possui Foto informada.");
            }
        }
        ve.lancarException();
    }

    @Override
    public String toString() {
        return pessoaFisica != null ? pessoaFisica.getNome() : "";
    }
}
