/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.entidades.PagamentoExtra;
import br.com.webpublico.entidades.TransferenciaContaFinanceira;
import br.com.webpublico.entidades.TransferenciaMesmaUnidade;

/**
 *
 * @author reidocrime
 */
public enum TipoLancamentoEmBordero {

    PAGAMENTO("Pagamento", Pagamento.class, "1"),
    PAGAMENTO_EXTRA("Pagamento Extra", PagamentoExtra.class, "2"),
    TRANSFERENCIA_MESMA_UNIDADE("Tranferencia Mesma Unidade", TransferenciaMesmaUnidade.class, "3"),
    TRANSFERENCIA_FIANCEIRA("Tranferencia Financeira", TransferenciaContaFinanceira.class, "4");
    private String descricao;
    private Class classe;
    private String codigo;

    private TipoLancamentoEmBordero(String descricao, Class classe, String codigo) {
        this.descricao = descricao;
        this.classe = classe;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Class getClasse() {
        return classe;
    }

    public void setClasse(Class classe) {
        this.classe = classe;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }


}
