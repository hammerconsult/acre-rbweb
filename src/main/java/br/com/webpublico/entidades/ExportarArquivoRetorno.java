package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

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
@Etiqueta("Exportação do Arquivo de Retorno E-consig")
public class ExportarArquivoRetorno implements Serializable {

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
    @Etiqueta("Total")
    private int contadorTotal;
    @Invisivel
    @Transient
    private boolean liberarPaineis;
    @Pesquisavel
    @Etiqueta("Tipo Folha de Pagamento")
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    @Etiqueta("Versão")
    private Integer versao;


    public ExportarArquivoRetorno() {
        liberado = false;
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

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    @Override
    public String toString() {
        return mes + "/" + ano;
    }
}
