package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 09/10/13
 * Time: 17:19
 * To change this template use File | Settings | File Templates.
 */

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Audited
@Etiqueta("Arquivo do Econsig")
public class ArquivoEconsig implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Mês")
    private Mes mes;
    @Pesquisavel
    @Tabelavel
    private Integer ano;
    @Invisivel
    @Etiqueta("Itens Gerados do Arquivo")
    @OneToMany(mappedBy = "arquivoEconsig", cascade = CascadeType.ALL)
    private List<ArquivoEconsigItens> arquivoEconsigItens;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Dia da Operação")
    @Temporal(TemporalType.DATE)
    private Date dataRegistro;
    @Pesquisavel
    @Tabelavel
    private Integer total;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Qtde de registros corretos")
    private Integer quantidadeOk;

    @Etiqueta("Qtde de rejeitados")
    private Integer quantidadeRejeitados;
    @Invisivel
    @Etiqueta("Conteúdo do arquivo")
    private String conteudoArquivo;
    @OneToMany(mappedBy = "arquivoEconsig", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ErroEconsig> errosEconsig;

    public ArquivoEconsig() {
        arquivoEconsigItens = new ArrayList<>();
        quantidadeOk = 0;
        quantidadeRejeitados = 0;
        total = 0;
        errosEconsig = new ArrayList<>();
    }

    public List<ArquivoEconsigItens> getArquivoEconsigItens() {
        return arquivoEconsigItens;
    }

    public void setArquivoEconsigItens(List<ArquivoEconsigItens> arquivoEconsigItens) {
        this.arquivoEconsigItens = arquivoEconsigItens;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getQuantidadeOk() {
        return quantidadeOk;
    }

    public void setQuantidadeOk(Integer quantidadeOk) {
        this.quantidadeOk = quantidadeOk;
    }

    public Integer getQuantidadeRejeitados() {
        return quantidadeRejeitados;
    }

    public void setQuantidadeRejeitados(Integer quantidadeRejeitados) {
        this.quantidadeRejeitados = quantidadeRejeitados;
    }

    public String getConteudoArquivo() {
        return conteudoArquivo;
    }

    public void setConteudoArquivo(String conteudoArquivo) {
        this.conteudoArquivo = conteudoArquivo;
    }

    public List<ErroEconsig> getErrosEconsig() {
        return errosEconsig;
    }

    public void setErrosEconsig(List<ErroEconsig> errosEconsig) {
        this.errosEconsig = errosEconsig;
    }

    @Override
    public String toString() {
        return mes.getDescricao() + " de " + ano;
    }
}
