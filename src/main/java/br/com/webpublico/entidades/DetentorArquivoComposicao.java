package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 01/07/14
 * Time: 16:45
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class DetentorArquivoComposicao extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "detentorArquivoComposicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArquivoComposicao> arquivosComposicao;

    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Arquivo Composição")
    private ArquivoComposicao arquivoComposicao;

    public DetentorArquivoComposicao() {
        super();
        this.arquivosComposicao = new ArrayList<>();
    }

    public DetentorArquivoComposicao(ArquivoComposicao arquivoComposicao) {
        this.arquivoComposicao = arquivoComposicao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ArquivoComposicao> getArquivosComposicao() {
        if (arquivosComposicao == null) arquivosComposicao = Lists.newArrayList();
        return arquivosComposicao;
    }

    public void setArquivosComposicao(List<ArquivoComposicao> arquivosComposicao) {
        this.arquivosComposicao = arquivosComposicao;
    }

    public ArquivoComposicao getArquivoComposicao() {
        return arquivoComposicao;
    }

    public void setArquivoComposicao(ArquivoComposicao arquivoComposicao) {
        this.arquivoComposicao = arquivoComposicao;
    }

    public static DetentorArquivoComposicao clonar(DetentorArquivoComposicao detentorArquivoComposicao) {
        if (detentorArquivoComposicao == null) return null;
        DetentorArquivoComposicao clone = new DetentorArquivoComposicao();
        clone.setCriadoEm(detentorArquivoComposicao.getCriadoEm());
        clone.setArquivoComposicao(ArquivoComposicao.clonar(detentorArquivoComposicao.getArquivoComposicao(), clone));
        if (!detentorArquivoComposicao.getArquivosComposicao().isEmpty()) {
            for (ArquivoComposicao arquivoComposicao : detentorArquivoComposicao.getArquivosComposicao()) {
                clone.getArquivosComposicao().add(ArquivoComposicao.clonar(arquivoComposicao, clone));
            }
        }
        return clone;
    }

    public ArquivoComposicao buscarPorNomeArquivo(String nome) {
        for (ArquivoComposicao arquivoComposicaoRegistrado : getArquivosComposicao()) {
            if (arquivoComposicaoRegistrado.getArquivo().getNome().equals(nome)) {
                return arquivoComposicaoRegistrado;
            }
        }
        return null;
    }

    public String montarNomesListaArquivosComposicao(boolean podeFazerDownload) {
        StringBuilder retorno = new StringBuilder();
        for (int i = 0; i < getArquivosComposicao().size(); i++) {
            if (i != 0) retorno.append("</br>");
            Arquivo arquivo = getArquivosComposicao().get(i).getArquivo();
            if (podeFazerDownload) {
                retorno.append("<a title='Download' ");
                retorno.append("target='_blank' ");
                retorno.append("download='").append(arquivo.getNome()).append("' ");
                retorno.append("href='").append("/arquivos/")
                    .append(arquivo.getNome())
                    .append("?id=").append(arquivo.getId()).append("'>");
                retorno.append(arquivo.getNome()).append("</a>");
            } else {
                retorno.append(arquivo.getNome());
            }
        }
        return retorno.toString();
    }
}
