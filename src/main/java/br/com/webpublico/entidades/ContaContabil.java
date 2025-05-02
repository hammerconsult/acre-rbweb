package br.com.webpublico.entidades;

import br.com.webpublico.enums.NaturezaConta;
import br.com.webpublico.enums.NaturezaInformacao;
import br.com.webpublico.enums.NaturezaSaldo;
import br.com.webpublico.enums.SubSistema;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import org.hibernate.envers.Audited;

@Entity
@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta(value = "Conta Contábil")
public class ContaContabil extends Conta {

    private String migracao;
    @Enumerated(EnumType.STRING)
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Pesquisavel
    @Etiqueta(value = "Natureza do Saldo")
    private NaturezaSaldo naturezaSaldo;
    @Enumerated(EnumType.STRING)
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Pesquisavel
    @Etiqueta(value = "Natureza da Conta")
    private NaturezaConta naturezaConta;
    @Enumerated(EnumType.STRING)
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Pesquisavel
    @Etiqueta(value = "Natureza da Informação")
    private NaturezaInformacao naturezaInformacao;
    @Enumerated(EnumType.STRING)
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Pesquisavel
    @Etiqueta(value = "Subsistema")
    private SubSistema subSistema;

    public ContaContabil() {
        super();
        this.migracao = "";
        super.setdType("ContaContabil");
    }

    public String getMigracao() {
        return migracao;
    }

    public void setMigracao(String migracao) {
        this.migracao = migracao;
    }

    public NaturezaSaldo getNaturezaSaldo() {
        return naturezaSaldo;
    }

    public void setNaturezaSaldo(NaturezaSaldo naturezaSaldo) {
        this.naturezaSaldo = naturezaSaldo;
    }

    public NaturezaConta getNaturezaConta() {
        return naturezaConta;
    }

    public void setNaturezaConta(NaturezaConta naturezaConta) {
        this.naturezaConta = naturezaConta;
    }

    public NaturezaInformacao getNaturezaInformacao() {
        return naturezaInformacao;
    }

    public void setNaturezaInformacao(NaturezaInformacao naturezaInformacao) {
        this.naturezaInformacao = naturezaInformacao;
    }

    public SubSistema getSubSistema() {
        return subSistema;
    }

    public void setSubSistema(SubSistema subSistema) {
        this.subSistema = subSistema;
    }
}
