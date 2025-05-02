package br.com.webpublico.entidades.rh;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@GrupoDiagrama(nome = "RecursosHumanos")
@Audited
@Entity
@Etiqueta("Item Envio Dados RBPonto")
public class ItemEnvioDadosRBPonto extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Servidor")
    private ContratoFP contratoFP;
    @Etiqueta("Data Inicial")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataInicial;
    @Etiqueta("Data Final")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataFinal;
    @Etiqueta("Envio Dados RBPonto")
    @ManyToOne
    private EnvioDadosRBPonto envioDadosRBPonto;
    private Long identificador;
    private String observacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public EnvioDadosRBPonto getEnvioDadosRBPonto() {
        return envioDadosRBPonto;
    }

    public void setEnvioDadosRBPonto(EnvioDadosRBPonto envioDadosRBPonto) {
        this.envioDadosRBPonto = envioDadosRBPonto;
    }

    public Long getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Long identificador) {
        this.identificador = identificador;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
