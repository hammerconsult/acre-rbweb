package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@GrupoDiagrama(nome = "Dívida Ativa")
@Entity
@Audited
@Etiqueta("Comunicação com a SoftPlan")
public class EventoManipulacaoCDA extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CertidaoDividaAtiva cda;
    @ManyToOne
    private ProcessoParcelamento parcelamento;
    @Enumerated(EnumType.STRING)
    private Tipo tipo;




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CertidaoDividaAtiva getCda() {
        return cda;
    }

    public void setCda(CertidaoDividaAtiva cda) {
        this.cda = cda;
    }

    public ProcessoParcelamento getParcelamento() {
        return parcelamento;
    }

    public void setParcelamento(ProcessoParcelamento parcelamento) {
        this.parcelamento = parcelamento;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }


    public static enum Tipo {
        INCLUSAO, ALTERACAO, RETIFICACAO
    }
}
