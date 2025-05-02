package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Table(name = "CONDUTORVEICULO")
@Entity
@Audited
@GrupoDiagrama(nome = "Tributário")
public class CondutorOperadoraVeiculoOperadora extends SuperEntidade{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Veículo - Operadora de Tecnologia de Transporte(OTT)")
    @ManyToOne
    private VeiculoOperadoraTecnologiaTransporte veiculoOTTransporte;

    @Etiqueta("Condutor OperadoraCondutor da Operadora de Tecnologia de Transporte(OTT)")
    @ManyToOne
    private CondutorOperadoraTecnologiaTransporte condutorOperaTransporte;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VeiculoOperadoraTecnologiaTransporte getVeiculoOTTransporte() {
        return veiculoOTTransporte;
    }

    public void setVeiculoOTTransporte(VeiculoOperadoraTecnologiaTransporte veiculoOTTransporte) {
        this.veiculoOTTransporte = veiculoOTTransporte;
    }

    public CondutorOperadoraTecnologiaTransporte getCondutorOperaTransporte() {
        return condutorOperaTransporte;
    }

    public void setCondutorOperaTransporte(CondutorOperadoraTecnologiaTransporte condutorOperaTransporte) {
        this.condutorOperaTransporte = condutorOperaTransporte;
    }

}
