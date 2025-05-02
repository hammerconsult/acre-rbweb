package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "CONVITEUSUARIONFSE")
@Audited
public class ConviteUsuarioNfse extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private DadosPessoaisNfse dadosPessoais;

    @ManyToOne
    private CadastroEconomico cadastroEconomico;

    private Date convidadoEm;

    public ConviteUsuarioNfse() {
    }

    public ConviteUsuarioNfse(DadosPessoaisNfse dadosPessoaisTomador, CadastroEconomico cadastroEconomico, Date convidadoEm) {
        this.dadosPessoais = dadosPessoaisTomador;
        this.cadastroEconomico = cadastroEconomico;
        this.convidadoEm = convidadoEm;
    }

    public DadosPessoaisNfse getDadosPessoais() {
        return dadosPessoais;
    }

    public void setDadosPessoais(DadosPessoaisNfse dadosPessoais) {
        this.dadosPessoais = dadosPessoais;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public Date getConvidadoEm() {
        return convidadoEm;
    }

    public void setConvidadoEm(Date convidadoEm) {
        this.convidadoEm = convidadoEm;
    }

    @Override
    public Long getId() {
        return id;
    }
}
