package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Notícias da Nfs-e")
public class NoticiaNfse extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Data da Notícia")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataNoticia;
    @Obrigatorio
    @Etiqueta("Título")
    private String titulo;
    @Obrigatorio
    @Etiqueta("Conteúdo")
    private String conteudo;
    @Obrigatorio
    @Etiqueta("Imagem")
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataNoticia() {
        return dataNoticia;
    }

    public void setDataNoticia(Date dataNoticia) {
        this.dataNoticia = dataNoticia;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
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
