/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.viewobjects;

import br.com.webpublico.enums.TipoCadastroDoctoOficial;
import br.com.webpublico.entidades.TipoDoctoOficial;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Leonardo
 */
public class DataTableSolicitacaoDoctoOficial implements Serializable {

    private Long id;
    private Long codigo;
    private Date data;
    private TipoDoctoOficial tipoDocumento;
    private TipoCadastroDoctoOficial tipoCadastroDoctoOficial;
    private String numeroCadastro;
    private String nomePessoaCadastro;
    private BigDecimal valor;

    public DataTableSolicitacaoDoctoOficial(Long id, Long codigo, Date data, TipoDoctoOficial tipoDocumento, TipoCadastroDoctoOficial tipoCadastroDoctoOficial, String numeroCadastro, String nomePessoaCadastro, BigDecimal valor) {
        this.id = id;
        this.codigo = codigo;
        this.data = data;
        this.tipoDocumento = tipoDocumento;
        this.numeroCadastro = numeroCadastro;
        this.nomePessoaCadastro = nomePessoaCadastro;
        this.valor = valor;
        this.tipoCadastroDoctoOficial = tipoCadastroDoctoOficial;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public TipoDoctoOficial getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDoctoOficial tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public void setNumeroCadastro(String numeroCadastro) {
        this.numeroCadastro = numeroCadastro;
    }

    public String getNomePessoaCadastro() {
        return nomePessoaCadastro;
    }

    public void setNomePessoaCadastro(String nomePessoaCadastro) {
        this.nomePessoaCadastro = nomePessoaCadastro;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getNumeroCadastro() {
        return numeroCadastro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoCadastroDoctoOficial getTipoCadastroDoctoOficial() {
        return tipoCadastroDoctoOficial;
    }

    public void setTipoCadastroDoctoOficial(TipoCadastroDoctoOficial tipoCadastroDoctoOficial) {
        this.tipoCadastroDoctoOficial = tipoCadastroDoctoOficial;
    }
}
