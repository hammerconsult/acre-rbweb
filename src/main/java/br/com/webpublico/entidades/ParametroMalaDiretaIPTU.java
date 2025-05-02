package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by Pedro on 19/02/2024.
 */
@Entity
@Audited
@Etiqueta("Parâmetro da Mala Direta de IPTU")
public class ParametroMalaDiretaIPTU extends SuperEntidade implements PossuidorArquivo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Exercício")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private Exercicio exercicio;
    @Etiqueta("Imagem")
    @Obrigatorio
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorArquivoComposicao detentorArquivoComposicao;
    @Obrigatorio
    @Etiqueta("Posição X")
    private Integer posicaoXDadosContribuinteNaImagem;
    @Obrigatorio
    @Etiqueta("Posição Y")
    private Integer posicaoYDadosContribuinteNaImagem;
    @Obrigatorio
    @Etiqueta("Texto da Mala Direta")
    private String textoMalaDireta;

    public ParametroMalaDiretaIPTU() {
        this.posicaoXDadosContribuinteNaImagem = 90;
        this.posicaoYDadosContribuinteNaImagem = 210;
        this.textoMalaDireta = getTextoMalaDiretaPadrao();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public Integer getPosicaoXDadosContribuinteNaImagem() {
        return posicaoXDadosContribuinteNaImagem;
    }

    public void setPosicaoXDadosContribuinteNaImagem(Integer posicaoXDadosContribuinteNaImagem) {
        this.posicaoXDadosContribuinteNaImagem = posicaoXDadosContribuinteNaImagem;
    }

    public Integer getPosicaoYDadosContribuinteNaImagem() {
        return posicaoYDadosContribuinteNaImagem;
    }

    public void setPosicaoYDadosContribuinteNaImagem(Integer posicaoYDadosContribuinteNaImagem) {
        this.posicaoYDadosContribuinteNaImagem = posicaoYDadosContribuinteNaImagem;
    }

    public String getTextoMalaDireta() {
        return textoMalaDireta;
    }

    public void setTextoMalaDireta(String textoMalaDireta) {
        this.textoMalaDireta = textoMalaDireta;
    }

    public String getTextoMalaDiretaPadrao() {
        return "&nbsp; &nbsp; &nbsp; Ilmo(a). Sr(a). Contribuinte, " +
            "<br/><br/> " +
            "&nbsp; &nbsp; &nbsp; Consta no Sistema de Administração Tributária – WebPúblico, a existência de débitos referente a IPTU (Imposto Predial e Territorial Urbano) e TSU (Taxa de Serviços Urbanos), exercício de ${EXERCICIO_PARAMETRO}.\n" +
            "<br/> " +
            "&nbsp; &nbsp; &nbsp; Regularize seus débitos e tenha a oportunidade de concorrer a vários prêmios, na 'Campanha IPTU em Dia dá Prêmios ${EXERCICIO_PARAMETRO}', que sortearão mais de 50 mil em prêmios. Aproveite, o próximo ganhador pode ser você.\n" +
            "<br/> " +
            "&nbsp; &nbsp; &nbsp; Informamos que a falta do(s) recolhimento(s) do(s) imposto(s) no prazo concedido implicará em inscrição na DÍVIDA ATIVA, com acréscimos de juros e multa.\n" +
            "<br/> " +
            "&nbsp; &nbsp; &nbsp; Caso seu débito já esteja quitado, favor desconsiderar esta Notificação. " +
            "<br/>";
    }

    @Override
    public String toString() {
        return exercicio.getAno().toString();
    }
}
