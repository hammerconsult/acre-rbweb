package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoArquivoTCE;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 17/04/14
 * Time: 11:41
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
public class LancamentoArquivoTCE implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Exercicio exercicio;
    @Enumerated(EnumType.STRING)
    private TipoArquivoTCE tipoArquivoTCE;
    public String numero, data, tipoLancamento, historico, unidade, orgao, clp;

    public LancamentoArquivoTCE() {
    }

    public LancamentoArquivoTCE(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(String tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getClp() {
        return clp;
    }

    public void setClp(String clp) {
        this.clp = clp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoArquivoTCE getTipoArquivoTCE() {
        return tipoArquivoTCE;
    }

    public void setTipoArquivoTCE(TipoArquivoTCE tipoArquivoTCE) {
        this.tipoArquivoTCE = tipoArquivoTCE;
    }
}
