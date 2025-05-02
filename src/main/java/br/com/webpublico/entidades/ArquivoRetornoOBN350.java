/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author reidocrime
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Financeiro")
@Etiqueta("Arquivo - OBN350")
public class ArquivoRetornoOBN350 implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Arquivo")
    private Arquivo arquivo;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Importação")
    private Date dataImportacao;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Exercício")
    @ManyToOne
    private Exercicio exercicio;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Sequencial do Arquivo")
    private String sequencialArquivo;

    @NotAudited
    @OneToMany(mappedBy = "arquivo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArquivoOBN350Pagamento> pagamentos;

    @NotAudited
    @OneToMany(mappedBy = "arquivo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArquivoOBN350DespesaExtra> pagamentosExtras;

    @NotAudited
    @OneToMany(mappedBy = "arquivo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArquivoOBN350TransfFinanceira> transferenciaContaFinanceiras;

    @NotAudited
    @OneToMany(mappedBy = "arquivo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArquivoOBN350TransfMesmaUnid> transferenciaMesmaUnidades;

    @NotAudited
    @OneToMany(mappedBy = "arquivo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArquivoOBN350LiberacaoFinanc> liberacaoCotaFinanceiras;

    public ArquivoRetornoOBN350() {
        this.pagamentos = new ArrayList<>();
        this.pagamentosExtras = new ArrayList<>();
        this.transferenciaContaFinanceiras = new ArrayList<>();
        this.transferenciaMesmaUnidades = new ArrayList<>();
        this.liberacaoCotaFinanceiras = new ArrayList<>();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public List<ArquivoOBN350Pagamento> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<ArquivoOBN350Pagamento> pagamentos) {
        this.pagamentos = pagamentos;
    }

    public List<ArquivoOBN350DespesaExtra> getPagamentosExtras() {
        return pagamentosExtras;
    }

    public void setPagamentosExtras(List<ArquivoOBN350DespesaExtra> pagamentosEsxtras) {
        this.pagamentosExtras = pagamentosEsxtras;
    }

    public List<ArquivoOBN350TransfFinanceira> getTransferenciaContaFinanceiras() {
        return transferenciaContaFinanceiras;
    }

    public void setTransferenciaContaFinanceiras(List<ArquivoOBN350TransfFinanceira> transferenciaContaFinanceiras) {
        this.transferenciaContaFinanceiras = transferenciaContaFinanceiras;
    }

    public List<ArquivoOBN350TransfMesmaUnid> getTransferenciaMesmaUnidades() {
        return transferenciaMesmaUnidades;
    }

    public void setTransferenciaMesmaUnidades(List<ArquivoOBN350TransfMesmaUnid> transferenciaMesmaUnidades) {
        this.transferenciaMesmaUnidades = transferenciaMesmaUnidades;
    }

    public List<ArquivoOBN350LiberacaoFinanc> getLiberacaoCotaFinanceiras() {
        return liberacaoCotaFinanceiras;
    }

    public void setLiberacaoCotaFinanceiras(List<ArquivoOBN350LiberacaoFinanc> liberacaoCotaFinanceiras) {
        this.liberacaoCotaFinanceiras = liberacaoCotaFinanceiras;
    }

    public Date getDataImportacao() {
        return dataImportacao;
    }

    public void setDataImportacao(Date dataImportacao) {
        this.dataImportacao = dataImportacao;
    }

    public String getSequencialArquivo() {
        return sequencialArquivo;
    }

    public void setSequencialArquivo(String sequencialArquivo) {
        this.sequencialArquivo = sequencialArquivo;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }
}
