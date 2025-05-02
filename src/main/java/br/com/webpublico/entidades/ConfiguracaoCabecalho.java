package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.InputStream;

/**
 * Created by mga on 24/07/2017.
 */

@Entity
@Audited
@Etiqueta("Configuração de Cabeçalho")
public class ConfiguracaoCabecalho extends SuperEntidade implements PossuidorArquivo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Título")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private String titulo;

    @Etiqueta("Subtitulo")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private String subTitulo;

    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    @Obrigatorio
    @Etiqueta("Arquivo")
    private DetentorArquivoComposicao detentorArquivoComposicao;

    private Boolean principal;

    public ConfiguracaoCabecalho() {
        super();
        principal = Boolean.FALSE;
    }

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

    public String getSubTitulo() {
        return subTitulo;
    }

    public void setSubTitulo(String subTitulo) {
        this.subTitulo = subTitulo;
    }


    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public InputStream getLogoInputStream() {
        if (Util.isNotNull(this.detentorArquivoComposicao) && Util.isNotNull(this.detentorArquivoComposicao.getArquivoComposicao())) {
            return new Util().getImagemInputStream(this.detentorArquivoComposicao.getArquivoComposicao().getArquivo());
        }
        return null;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public Boolean getPrincipal() {
        return principal != null ? principal : Boolean.FALSE;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }
}
