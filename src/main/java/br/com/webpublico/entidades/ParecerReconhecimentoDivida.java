package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoParecerReconhecimentoDivida;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Paracer do Reconhecimento da Dívida do Exercício")
@Table(name = "PARECERRECONHECIMENTODIV")
public class ParecerReconhecimentoDivida extends SuperEntidade implements PossuidorArquivo, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Reconehcimento de Dívida do Exercício")
    private ReconhecimentoDivida reconhecimentoDivida;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Parecer")
    private TipoParecerReconhecimentoDivida tipoParecer;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public ParecerReconhecimentoDivida() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReconhecimentoDivida getReconhecimentoDivida() {
        return reconhecimentoDivida;
    }

    public void setReconhecimentoDivida(ReconhecimentoDivida reconhecimentoDivida) {
        this.reconhecimentoDivida = reconhecimentoDivida;
    }

    public TipoParecerReconhecimentoDivida getTipoParecer() {
        return tipoParecer;
    }

    public void setTipoParecer(TipoParecerReconhecimentoDivida tipoParecer) {
        this.tipoParecer = tipoParecer;
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
