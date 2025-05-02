package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 05/02/15
 * Time: 16:44
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "ARQUIVO_PAGAMENTOEXTRA")
public class ArquivoOBN350DespesaExtra implements Serializable{

    @Pesquisavel
    @Etiqueta("Validado")
    private Boolean validado;
    @Pesquisavel
    @Etiqueta("Descrição do Erro")
    private String descricaoErro;
    @ManyToOne
    @Id
    private ArquivoRetornoOBN350 arquivo;
    @ManyToOne
    @Id
    private PagamentoExtra pagamentoextra;

    public ArquivoOBN350DespesaExtra() {
    }

    public ArquivoOBN350DespesaExtra(PagamentoExtra pagamentoextra, ArquivoRetornoOBN350 arquivo) {
        this.pagamentoextra = pagamentoextra;
        this.arquivo = arquivo;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }

    public String getDescricaoErro() {
        return descricaoErro;
    }

    public void setDescricaoErro(String descricaoErro) {
        this.descricaoErro = descricaoErro;
    }

    public ArquivoRetornoOBN350 getArquivo() {
        return arquivo;
    }

    public void setArquivo(ArquivoRetornoOBN350 arquivo) {
        this.arquivo = arquivo;
    }

    public PagamentoExtra getPagamentoextra() {
        return pagamentoextra;
    }

    public void setPagamentoextra(PagamentoExtra pagamentoextra) {
        this.pagamentoextra = pagamentoextra;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArquivoOBN350DespesaExtra that = (ArquivoOBN350DespesaExtra) o;

        if (!arquivo.equals(that.arquivo)) return false;
        return pagamentoextra.equals(that.pagamentoextra);

    }

    @Override
    public int hashCode() {
        int result = arquivo.hashCode();
        result = 31 * result + pagamentoextra.hashCode();
        return result;
    }
}
