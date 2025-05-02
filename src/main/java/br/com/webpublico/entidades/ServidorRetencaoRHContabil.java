package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Integração RH/Contábil - Retencao")
public class ServidorRetencaoRHContabil extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Retenção")
    @Tabelavel
    private RetencaoIntegracaoRHContabil retencao;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Servidor")
    @Tabelavel
    private VinculoFP vinculoFP;
    @Obrigatorio
    @Etiqueta("Valor")
    @Tabelavel
    private BigDecimal valor;

    public ServidorRetencaoRHContabil() {
        this.valor = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RetencaoIntegracaoRHContabil getRetencao() {
        return retencao;
    }

    public void setRetencao(RetencaoIntegracaoRHContabil retencao) {
        this.retencao = retencao;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
