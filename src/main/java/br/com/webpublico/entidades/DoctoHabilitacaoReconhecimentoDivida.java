package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Table(name = "DOCTOHABRECONHECIMENTODIV")
public class DoctoHabilitacaoReconhecimentoDivida extends SuperEntidade implements PossuidorArquivo, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Documento")
    @Obrigatorio
    private DoctoHabilitacao doctoHabilitacao;
    @ManyToOne
    @Etiqueta("Reconhecimento de Dívida do Exercício")
    private ReconhecimentoDivida reconhecimentoDivida;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public DoctoHabilitacaoReconhecimentoDivida() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DoctoHabilitacao getDoctoHabilitacao() {
        return doctoHabilitacao;
    }

    public void setDoctoHabilitacao(DoctoHabilitacao doctoHabilitacao) {
        this.doctoHabilitacao = doctoHabilitacao;
    }

    public ReconhecimentoDivida getReconhecimentoDivida() {
        return reconhecimentoDivida;
    }

    public void setReconhecimentoDivida(ReconhecimentoDivida reconhecimentoDivida) {
        this.reconhecimentoDivida = reconhecimentoDivida;
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
