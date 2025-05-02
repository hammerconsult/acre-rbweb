package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.ArquivoMargemVinculo;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 08/01/14
 * Time: 19:49
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Audited
@Etiqueta("Exportação do Arquivo de Margem")
public class ExportaArquivoMargem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Mês")
    private Integer mes;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Ano")
    private Integer ano;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Mês Referência Financeira")
    private Integer mesFinanceiro;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Ano Referência Financeira")
    private Integer anoFinanceiro;

    @Invisivel
    private String conteudo;
    @Invisivel
    private String erros;
    @Invisivel
    @Transient
    private boolean liberado;
    @Invisivel
    @Transient
    private boolean cancelar;
    @Invisivel
    @Transient
    private int contador;
    @Invisivel
    @Transient
    private int contadorTotal;
    @Invisivel
    @Transient
    private boolean liberarPaineis;
    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "exportaArquivoMargem", orphanRemoval = true)
    private List<ArquivoMargemVinculo> itemArquivoMargem;

    public ExportaArquivoMargem() {
        liberado = false;
        itemArquivoMargem = Lists.newArrayList();
    }

    public List<ArquivoMargemVinculo> getItemArquivoMargem() {
        return itemArquivoMargem;
    }

    public void setItemArquivoMargem(List<ArquivoMargemVinculo> itemArquivoMargem) {
        this.itemArquivoMargem = itemArquivoMargem;
    }

    public int getContador() {
        return contador;
    }

    public void setContador(int contador) {
        this.contador = contador;
    }

    public int getContadorTotal() {
        return contadorTotal;
    }

    public void setContadorTotal(int contadorTotal) {
        this.contadorTotal = contadorTotal;
    }

    public boolean isCancelar() {
        return cancelar;
    }

    public void setCancelar(boolean cancelar) {
        this.cancelar = cancelar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getErros() {
        return erros;
    }

    public void setErros(String erros) {
        this.erros = erros;
    }

    public boolean isLiberarPaineis() {
        return liberarPaineis;
    }

    public void setLiberarPaineis(boolean liberarPaineis) {
        this.liberarPaineis = liberarPaineis;
    }

    public boolean isLiberado() {
        return liberado;
    }

    public void setLiberado(boolean liberado) {
        this.liberado = liberado;
    }

    public void cancelarProcessamento() {
        cancelar = true;
    }

    public void somaContador() {
        contador++;
    }

    public Integer getMesFinanceiro() {
        return mesFinanceiro;
    }

    public void setMesFinanceiro(Integer mesFinanceiro) {
        this.mesFinanceiro = mesFinanceiro;
    }

    public Integer getAnoFinanceiro() {
        return anoFinanceiro;
    }

    public void setAnoFinanceiro(Integer anoFinanceiro) {
        this.anoFinanceiro = anoFinanceiro;
    }

    @Override
    public String toString() {
        return mes + "/" + ano;
    }
}
