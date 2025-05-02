package br.com.webpublico.entidades;

import br.com.webpublico.enums.Sexo;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Viagem por condutor OTT")
public class ViagemCondutorOtt extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Operadora de Tecnologia de Transporte")
    private OperadoraTecnologiaTransporte ott;
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Condutor da Operadora de Tecnologia de Transporte")
    private CondutorOperadoraTecnologiaTransporte condutorOtt;
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Placa do Veiculo")
    private VeiculoOperadoraTecnologiaTransporte veiculoOtt;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Distância da Corrida (KM)")
    private BigDecimal distanciaCorrida;
    @Pesquisavel
    @Etiqueta("Gênero do Passageiro")
    @Enumerated(EnumType.STRING)
    private Sexo generoPassageiro;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Valor da Corrida")
    @Monetario
    private BigDecimal valorCorrida;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Valor do Desconto")
    @Monetario
    private BigDecimal valorDesconto;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data da Viagem")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataViagem;

    public ViagemCondutorOtt() {
        this.distanciaCorrida = BigDecimal.ZERO;
        this.valorCorrida = BigDecimal.ZERO;
        this.valorDesconto = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OperadoraTecnologiaTransporte getOtt() {
        return ott;
    }

    public void setOtt(OperadoraTecnologiaTransporte ott) {
        this.ott = ott;
    }

    public CondutorOperadoraTecnologiaTransporte getCondutorOtt() {
        return condutorOtt;
    }

    public void setCondutorOtt(CondutorOperadoraTecnologiaTransporte condutorOtt) {
        this.condutorOtt = condutorOtt;
    }

    public VeiculoOperadoraTecnologiaTransporte getVeiculoOtt() {
        return veiculoOtt;
    }

    public void setVeiculoOtt(VeiculoOperadoraTecnologiaTransporte veiculoOtt) {
        this.veiculoOtt = veiculoOtt;
    }

    public BigDecimal getDistanciaCorrida() {
        return distanciaCorrida;
    }

    public void setDistanciaCorrida(BigDecimal distanciaCorrida) {
        this.distanciaCorrida = distanciaCorrida;
    }

    public Sexo getGeneroPassageiro() {
        return generoPassageiro;
    }

    public void setGeneroPassageiro(Sexo generoPassageiro) {
        this.generoPassageiro = generoPassageiro;
    }

    public BigDecimal getValorCorrida() {
        return valorCorrida;
    }

    public void setValorCorrida(BigDecimal valorCorrida) {
        this.valorCorrida = valorCorrida;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public Date getDataViagem() {
        return dataViagem;
    }

    public void setDataViagem(Date dataViagem) {
        this.dataViagem = dataViagem;
    }
}
