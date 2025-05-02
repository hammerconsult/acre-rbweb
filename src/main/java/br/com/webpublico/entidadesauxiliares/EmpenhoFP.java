/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.Conta;

import java.math.BigDecimal;

/**
 * @author everton
 */
public class EmpenhoFP {

    private RecursoFP recursoFP;
    private Empenho empenho;
    private SubConta contaFinanceira;
    private Conta desdobramento;
    private Liquidacao liquidacao;
    private Pagamento pagamento;

    public RecursoFP getRecursoFP() {
        return recursoFP;
    }

    public void setRecursoFP(RecursoFP recursoFP) {
        this.recursoFP = recursoFP;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public Conta getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(Conta desdobramento) {
        this.desdobramento = desdobramento;
    }

    public Liquidacao getLiquidacao() {
        return liquidacao;
    }

    public void setLiquidacao(Liquidacao liquidacao) {
        this.liquidacao = liquidacao;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    public SubConta getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(SubConta contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
    }

//    public EmpenhoFP(FolhaDePagamento folhaDePagamento, RecursoFP recursoFP, DetalhamentoFonteRec detalhamentoFonteRec, DespesaORC despesaORC, UnidadeOrganizacional unidadeOrganizacional, FonteDespesaORC fonteDespesaORC, SubConta contaFinanceira, PessoaJuridica fornecedor, ClasseCredor classeCredor, Conta desdobramento, BigDecimal valor) {
//        this.recursoFP = recursoFP;
//        this.contaFinanceira = contaFinanceira;
//        this.desdobramento = desdobramento;
//        this.empenho = new Empenho();
//        this.empenho.setDespesaORC(despesaORC);
//        this.empenho.setUnidadeOrganizacional(unidadeOrganizacional);
//        this.empenho.setFonteDespesaORC(fonteDespesaORC);
//        this.empenho.setDetalhamentoFonteRec(detalhamentoFonteRec);
//        this.empenho.setFornecedor(fornecedor);
//        this.empenho.setClasseCredor(classeCredor);
//        this.empenho.setExercicio(folhaDePagamento.getCompetenciaFP().getExercicio());
//        this.empenho.setTipoEmpenhoIntegracao(recursoFP.getTipoIntegracao());
//        this.empenho.setTipoEmpenho(TipoEmpenho.ORDINARIO);
//        this.empenho.setCategoriaOrcamentaria(CategoriaOrcamentaria.NORMAL);
//        this.empenho.setFolhaDePagamento(folhaDePagamento);
//        this.empenho.setValor(valor);
//        this.empenho.setSaldo(valor);
//    }
}
