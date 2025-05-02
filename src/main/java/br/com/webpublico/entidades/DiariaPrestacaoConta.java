/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Positivo;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author reidocrime
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity

@Etiqueta("Prestação de Contas de Diária")
public class DiariaPrestacaoConta extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data")
    private Date dataValor;

    @ManyToOne
    private PropostaConcessaoDiaria propostaConcessaoDiaria;

    @Obrigatorio
    @Etiqueta("Nº do Documento")
    private String numeroNota;

    @Obrigatorio
    @Etiqueta("Série do Documento")
    private String serieDocumento;

    @Obrigatorio
    @Etiqueta("Razão Social")
    private String razaosocial;

    @Obrigatorio
    @Etiqueta("CPF/CNPJ")
    private String cpfCnpj;

    @Obrigatorio
    @Monetario
    @Positivo(permiteZero = false)
    @Etiqueta("Valor")
    private BigDecimal valor;

    public DiariaPrestacaoConta() {
        this.valor = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataValor() {
        return dataValor;
    }

    public void setDataValor(Date dataValor) {
        this.dataValor = dataValor;
    }

    public String getRazaosocial() {
        return razaosocial;
    }

    public void setRazaosocial(String razaosocial) {
        this.razaosocial = razaosocial;
    }

    public String getSerieDocumento() {
        return serieDocumento;
    }

    public void setSerieDocumento(String serieDocumento) {
        this.serieDocumento = serieDocumento;
    }

    public PropostaConcessaoDiaria getPropostaConcessaoDiaria() {
        return propostaConcessaoDiaria;
    }

    public void setPropostaConcessaoDiaria(PropostaConcessaoDiaria propostaConcessaoDiaria) {
        this.propostaConcessaoDiaria = propostaConcessaoDiaria;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(String numeroNota) {
        this.numeroNota = numeroNota;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    @Override
    public String toString() {
        return numeroNota + " " + DataUtil.getDataFormatada(dataValor) + " Valor: " + Util.formataValor(valor);
    }
}
