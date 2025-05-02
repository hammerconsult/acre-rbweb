package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoLicencaAmbiental;
import br.com.webpublico.enums.TipoLocalizacao;
import br.com.webpublico.enums.TipoMateriaPrima;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Alvara")
@Etiqueta("Parâmetro Meio Ambiente CNAE")
public class ParametrosCalcAmbiental extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("CNAE")
    private CNAE cnae;
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Licença Ambiental")
    private TipoLicencaAmbiental licencaAmbiental;
    @Etiqueta("Dispensa Licença")
    @Pesquisavel
    @Tabelavel
    private Boolean dispensaLicenca;
    @OneToMany(mappedBy = "parametroCalcAmbiental", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AtributosCalculoAmbiental> atributosCalculo;

    public ParametrosCalcAmbiental() {
        this.atributosCalculo = Lists.newArrayList();
        this.dispensaLicenca = Boolean.FALSE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }

    public TipoLicencaAmbiental getLicencaAmbiental() {
        return licencaAmbiental;
    }

    public void setLicencaAmbiental(TipoLicencaAmbiental licencaAmbiental) {
        this.licencaAmbiental = licencaAmbiental;
    }

    public Boolean getDispensaLicenca() {
        return dispensaLicenca;
    }

    public void setDispensaLicenca(Boolean dispensaLicenca) {
        this.dispensaLicenca = dispensaLicenca;
    }

    public List<AtributosCalculoAmbiental> getAtributosCalculo() {
        return atributosCalculo;
    }

    public void setAtributosCalculo(List<AtributosCalculoAmbiental> atributosCalculo) {
        this.atributosCalculo = atributosCalculo;
    }
}
