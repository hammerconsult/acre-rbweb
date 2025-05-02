package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Carnage
 * Date: 12/11/13
 * Time: 09:56
 * To change this template use File | Settings | File Templates.
 */

@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Arquivo Holerite BB")
@Audited
@Entity
public class HoleriteBB extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Mês")
    private String mes;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Ano")
    private String ano;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Tipo de Folha")
    @Enumerated(EnumType.STRING)
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número da Remessa")
    private Long numeroRemessa;
    @Etiqueta("Versão")
    private Long versao;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data de geração")
    @Temporal(TemporalType.DATE)
    private Date dataGeracao;
    private String conteudoArquivo;
    @Transient
    private Boolean todosOrgaos;
    @Transient
    private Integer processados;
    @Transient
    private Integer total;
    @Transient
    private Long decorrido;
    @Transient
    private Long tempo;
    @Transient
    private String mensagens;
    @Transient
    private String mensagemExcecao;
    @Transient
    private Boolean calculando;
    @Transient
    private Integer quantidadeContratos;

    public HoleriteBB() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(Date dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public String getConteudoArquivo() {
        return conteudoArquivo;
    }

    public void setConteudoArquivo(String conteudoArquivo) {
        this.conteudoArquivo = conteudoArquivo;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public Long getNumeroRemessa() {
        return numeroRemessa;
    }

    public void setNumeroRemessa(Long numeroRemessa) {
        this.numeroRemessa = numeroRemessa;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Boolean getTodosOrgaos() {
        return todosOrgaos;
    }

    public void setTodosOrgaos(Boolean todosOrgaos) {
        this.todosOrgaos = todosOrgaos;
    }

    public Integer getProcessados() {
        return processados;
    }

    public void setProcessados(Integer processados) {
        this.processados = processados;
    }

    public Long getDecorrido() {
        return decorrido;
    }

    public void setDecorrido(Long decorrido) {
        this.decorrido = decorrido;
    }

    public Long getTempo() {
        return tempo;
    }

    public void setTempo(Long tempo) {
        this.tempo = tempo;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getMensagens() {
        return mensagens;
    }

    public void setMensagens(String mensagens) {
        this.mensagens = mensagens;
    }

    public String getMensagemExcecao() {
        return mensagemExcecao;
    }

    public void setMensagemExcecao(String mensagemExcecao) {
        this.mensagemExcecao = mensagemExcecao;
    }

    public Boolean getCalculando() {
        return calculando;
    }

    public void setCalculando(Boolean calculando) {
        this.calculando = calculando;
    }

    public Integer getQuantidadeContratos() {
        return quantidadeContratos;
    }

    public void setQuantidadeContratos(Integer quantidadeContratos) {
        this.quantidadeContratos = quantidadeContratos;
    }

    @Override
    public String toString() {
        return conteudoArquivo;
    }
}
