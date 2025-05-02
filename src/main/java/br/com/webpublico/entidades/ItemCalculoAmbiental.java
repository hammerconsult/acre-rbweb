package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoLicencaAmbiental;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
@Etiqueta("Item Cálculo Ambiental")
public class ItemCalculoAmbiental extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Item Cálculo Alvará")
    private ItemCalculoAlvara itemCalculoAlvara;
    @ManyToOne
    @Etiqueta("CNAE")
    private CNAE cnaeAmbiental;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo Licença Ambiental")
    private TipoLicencaAmbiental licencaAmbiental;
    @Etiqueta("Classe e Valor em UFM")
    private String classeValorUFM;
    @Etiqueta("Variação da Classe")
    private BigDecimal variacaoClasse;
    @Etiqueta("Valor(R$)")
    private BigDecimal valor;
    @Etiqueta("Dispensa Licença")
    private Boolean dispensaLicenca;

    public ItemCalculoAmbiental() {
        this.valor = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemCalculoAlvara getItemCalculoAlvara() {
        return itemCalculoAlvara;
    }

    public void setItemCalculoAlvara(ItemCalculoAlvara itemCalculoAlvara) {
        this.itemCalculoAlvara = itemCalculoAlvara;
    }

    public CNAE getCnaeAmbiental() {
        return cnaeAmbiental;
    }

    public void setCnaeAmbiental(CNAE cnaeAmbiental) {
        this.cnaeAmbiental = cnaeAmbiental;
    }

    public TipoLicencaAmbiental getLicencaAmbiental() {
        return licencaAmbiental;
    }

    public void setLicencaAmbiental(TipoLicencaAmbiental licencaAmbiental) {
        this.licencaAmbiental = licencaAmbiental;
    }

    public String getClasseValorUFM() {
        return classeValorUFM;
    }

    public void setClasseValorUFM(String classeValorUFM) {
        this.classeValorUFM = classeValorUFM;
    }

    public BigDecimal getVariacaoClasse() {
        return variacaoClasse;
    }

    public void setVariacaoClasse(BigDecimal variacaoClasse) {
        this.variacaoClasse = variacaoClasse;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean getDispensaLicenca() {
        return dispensaLicenca != null ? dispensaLicenca : false;
    }

    public void setDispensaLicenca(Boolean dispensaLicenca) {
        this.dispensaLicenca = dispensaLicenca;
    }
}
