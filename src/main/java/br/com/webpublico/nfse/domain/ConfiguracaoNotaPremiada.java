package br.com.webpublico.nfse.domain;


import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ConfiguracaoNotaPremiada extends SuperEntidade implements PossuidorArquivo{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String tituloPortalNotaPremiada;
    private String tituloAtendimento;
    private String enderecoAtendimento;
    private String horarioAtendimento;
    private String telefoneAtendimento;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    @Etiqueta("Imagem Nota Premiada")
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTituloPortalNotaPremiada() {
        return tituloPortalNotaPremiada;
    }

    public void setTituloPortalNotaPremiada(String tituloPortalNotaPremiada) {
        this.tituloPortalNotaPremiada = tituloPortalNotaPremiada;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public String getTituloAtendimento() {
        return tituloAtendimento;
    }

    public void setTituloAtendimento(String tituloAtendimento) {
        this.tituloAtendimento = tituloAtendimento;
    }

    public String getEnderecoAtendimento() {
        return enderecoAtendimento;
    }

    public void setEnderecoAtendimento(String enderecoAtendimento) {
        this.enderecoAtendimento = enderecoAtendimento;
    }

    public String getHorarioAtendimento() {
        return horarioAtendimento;
    }

    public void setHorarioAtendimento(String horarioAtendimento) {
        this.horarioAtendimento = horarioAtendimento;
    }

    public String getTelefoneAtendimento() {
        return telefoneAtendimento;
    }

    public void setTelefoneAtendimento(String telefoneAtendimento) {
        this.telefoneAtendimento = telefoneAtendimento;
    }
}
