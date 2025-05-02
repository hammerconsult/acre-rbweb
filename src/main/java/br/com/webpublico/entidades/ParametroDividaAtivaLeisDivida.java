package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Fabio
 */
@Entity

@Audited
@Etiqueta("Leis das Dívidas do Parâmetros Dívida Ativa")
@GrupoDiagrama(nome = "Divida Ativa")
@Table(name = "PARAMDIVIDAATIVALEISDIVIDA")
public class ParametroDividaAtivaLeisDivida implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ParametrosDividaAtiva parametrosDividaAtiva;
    @ManyToOne
    private Divida divida;
    @Etiqueta("Fundamentação Legal")
    private String fundamentacaoLegal;
    @Etiqueta("Descrição da Lei de Multas")
    private String capitulacaoMulta;
    @Etiqueta("Descrição da Lei de Juros")
    private String capitulacaoJuros;
    @Etiqueta("Descrição da Lei de Correção Monetária")
    private String capitulacaoCM;
    @Etiqueta("Descrição da Lei de Tributos/Impostos")
    private String capitulacaoTributo;
    @Transient
    private Long criadoEm;

    public ParametroDividaAtivaLeisDivida() {
        this.criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCapitulacaoCM() {
        return capitulacaoCM;
    }

    public void setCapitulacaoCM(String capitulacaoCM) {
        this.capitulacaoCM = capitulacaoCM;
    }

    public String getCapitulacaoJuros() {
        return capitulacaoJuros;
    }

    public void setCapitulacaoJuros(String capitulacaoJuros) {
        this.capitulacaoJuros = capitulacaoJuros;
    }

    public String getCapitulacaoMulta() {
        return capitulacaoMulta;
    }

    public void setCapitulacaoMulta(String capitulacaoMulta) {
        this.capitulacaoMulta = capitulacaoMulta;
    }

    public String getCapitulacaoTributo() {
        return capitulacaoTributo;
    }

    public void setCapitulacaoTributo(String capitulacaoTributo) {
        this.capitulacaoTributo = capitulacaoTributo;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public String getFundamentacaoLegal() {
        return fundamentacaoLegal;
    }

    public void setFundamentacaoLegal(String fundamentacaoLegal) {
        this.fundamentacaoLegal = fundamentacaoLegal;
    }

    public ParametrosDividaAtiva getParametrosDividaAtiva() {
        return parametrosDividaAtiva;
    }

    public void setParametrosDividaAtiva(ParametrosDividaAtiva parametrosDividaAtiva) {
        this.parametrosDividaAtiva = parametrosDividaAtiva;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }
}
