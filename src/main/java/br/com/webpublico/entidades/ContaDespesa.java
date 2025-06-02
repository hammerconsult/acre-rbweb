package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import org.hibernate.envers.Audited;

@Entity
@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta(value = "Conta de Despesa")
public class ContaDespesa extends Conta implements Serializable {

    private String migracao;
    @Enumerated(EnumType.STRING)
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Pesquisavel
    @Etiqueta(value = "Tipo de Conta")
    private TipoContaDespesa tipoContaDespesa;
    private String codigoReduzido;

    public ContaDespesa() {
        super();
        this.migracao = "";
        super.setdType("ContaDespesa");
    }

    public String getMigracao() {
        return migracao;
    }

    public void setMigracao(String migracao) {
        this.migracao = migracao;
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }

    public String getCodigoReduzido() {
        return codigoReduzido;
    }

    public void setCodigoReduzido(String codigoReduzido) {
        this.codigoReduzido = codigoReduzido;
    }

}
