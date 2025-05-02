package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Etiqueta("Comunicado Nota Premiada")
@Entity
@Audited
public class ComunicadoNotaPremiada extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue
    private Long id;


    @Obrigatorio
    @Etiqueta("Título")
    private String titulo;

    @Obrigatorio
    @Etiqueta("Início")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inicio;

    @Obrigatorio
    @Etiqueta("Fim")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fim;

    @Obrigatorio
    @Etiqueta("Documento")
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public ComunicadoNotaPremiada() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }
}
