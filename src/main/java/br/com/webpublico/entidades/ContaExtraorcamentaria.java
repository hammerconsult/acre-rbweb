/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoContaContabil;
import br.com.webpublico.enums.TipoContaExtraorcamentaria;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import org.hibernate.envers.Audited;

/**
 * @author venon
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta(value = "Conta Extraorçamentária")
public class ContaExtraorcamentaria extends Conta {

    @ManyToOne
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Pesquisavel
    @Etiqueta(value = "Conta Contábil")
    private Conta contaContabil;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Tipo de Conta")
    private TipoContaExtraorcamentaria tipoContaExtraorcamentaria;
    @ManyToOne
    @Etiqueta(value = "Conta Extraorçamentária")
    private ContaExtraorcamentaria contaExtraorcamentaria;
    @ManyToOne
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Pesquisavel
    @Etiqueta(value = "Tipo de Retenção")
    private TipoRetencao tipoRetencao;
//    @ManyToOne
//    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
//    @Pesquisavel
//    @Etiqueta(value = "Tipo de Conta")
//    private TipoContaExtra tipoContaExtra;

    public ContaExtraorcamentaria() {
        super();
        super.setdType("ContaExtraorcamentaria");
    }

    public ContaExtraorcamentaria(ContaContabil contaContabil, Boolean ativa, String codigo, Date dataRegistro, String descricao, Boolean permitirDesdobramento, PlanoDeContas planoDeContas, Conta superior, String rubrica, TipoContaContabil tipoContaContabil, String funcao, List<Conta> filhos, List<ContaEquivalente> contasEquivalentes) {
        super(ativa, codigo, dataRegistro, descricao, permitirDesdobramento, planoDeContas, superior, rubrica, tipoContaContabil, funcao, filhos, contasEquivalentes);
        this.contaContabil = contaContabil;
    }

    public Conta getContaContabil() {
        return contaContabil;
    }

    public void setContaContabil(Conta contaContabil) {
        this.contaContabil = contaContabil;
    }

    public ContaExtraorcamentaria getContaExtraorcamentaria() {
        return contaExtraorcamentaria;
    }

    public void setContaExtraorcamentaria(ContaExtraorcamentaria contaExtraorcamentaria) {
        this.contaExtraorcamentaria = contaExtraorcamentaria;
    }

    public TipoRetencao getTipoRetencao() {
        return tipoRetencao;
    }

    public void setTipoRetencao(TipoRetencao tipoRetencao) {
        this.tipoRetencao = tipoRetencao;
    }

    public TipoContaExtraorcamentaria getTipoContaExtraorcamentaria() {
        return tipoContaExtraorcamentaria;
    }

    public void setTipoContaExtraorcamentaria(TipoContaExtraorcamentaria tipoContaExtraorcamentaria) {
        this.tipoContaExtraorcamentaria = tipoContaExtraorcamentaria;
    }
}
