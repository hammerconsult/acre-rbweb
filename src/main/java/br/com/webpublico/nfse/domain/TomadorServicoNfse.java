package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.domain.dtos.NfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.nfse.domain.dtos.TomadorServicoDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Etiqueta("Tomador de Serviço da Nota Fiscal")
public class TomadorServicoNfse extends SuperEntidade implements NfseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private CadastroEconomico prestador;

    @ManyToOne(cascade = CascadeType.ALL)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Dados Pessoais")
    private DadosPessoaisNfse dadosPessoaisNfse;

    private Boolean ativo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CadastroEconomico getPrestador() {
        return prestador;
    }

    public void setPrestador(CadastroEconomico prestador) {
        this.prestador = prestador;
    }

    public DadosPessoaisNfse getDadosPessoaisNfse() {
        return dadosPessoaisNfse;
    }

    public void setDadosPessoaisNfse(DadosPessoaisNfse dadosPessoaisNfse) {
        this.dadosPessoaisNfse = dadosPessoaisNfse;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public NfseDTO toNfseDto() {
        return new TomadorServicoDTO(this.id, prestador.toNfseDto(), dadosPessoaisNfse.toNfseDto());
    }

    @Override
    public String toString() {
        return "(" + dadosPessoaisNfse.getCpfCnpj() + ") " + dadosPessoaisNfse.getNomeRazaoSocial();
    }

    public TomadorServicoDTO toSimpleNfseDto() {
        return new TomadorServicoDTO(this.id, null, dadosPessoaisNfse != null ? dadosPessoaisNfse.toNfseDto() : null);
    }
}
