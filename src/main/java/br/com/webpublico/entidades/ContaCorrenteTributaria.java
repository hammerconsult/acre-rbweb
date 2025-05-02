/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoDiferencaContaCorrente;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Fabio
 * @data 06/08/2018
 */
@GrupoDiagrama(nome = "Arrecadação")
@Entity
@Audited
public class ContaCorrenteTributaria extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Pesquisavel
    @Etiqueta("Código")
    @Tabelavel
    private Long codigo;
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Etiqueta("Data do Cadastro")
    @Tabelavel
    private Date dataCadastro;
    @Enumerated(EnumType.STRING)
    private Origem origem;
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Contribuinte")
    @Tabelavel
    private Pessoa pessoa;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "contaCorrente")
    private List<ParcelaContaCorrenteTributaria> parcelas;

    public ContaCorrenteTributaria() {
        this.parcelas = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<ParcelaContaCorrenteTributaria> getParcelas() {
        return parcelas;
    }

    public Origem getOrigem() {
        return origem;
    }

    public void setOrigem(Origem origem) {
        this.origem = origem;
    }

    public List<ParcelaContaCorrenteTributaria> getParcelasCreditos() {
        List<ParcelaContaCorrenteTributaria> pcct = Lists.newArrayList();
        for (ParcelaContaCorrenteTributaria parcela : parcelas) {
            if (TipoDiferencaContaCorrente.CREDITO_ARRECADACAO.equals(parcela.getTipoDiferenca())) {
                pcct.add(parcela);
            }
        }
        Collections.sort(pcct);
        return pcct;
    }

    public List<ParcelaContaCorrenteTributaria> getParcelasResiduos() {
        List<ParcelaContaCorrenteTributaria> pcct = Lists.newArrayList();
        for (ParcelaContaCorrenteTributaria parcela : parcelas) {
            if (TipoDiferencaContaCorrente.RESIDUO_ARRECADACAO.equals(parcela.getTipoDiferenca())) {
                pcct.add(parcela);
            }
        }
        Collections.sort(pcct);
        return pcct;
    }

    public void setParcelas(List<ParcelaContaCorrenteTributaria> parcelas) {
        this.parcelas = parcelas;
    }

    public enum Origem {
        ARRECADACAO("Lote de Arrecadação", "Arrecadação"),
        PROCESSO("Processo de Crédito em Conta Corrente", "Processo"),
        COMPENSACAO("Processo de Compensação de Débitos", "Compensação");

        private String descricao;
        private String descricaoCurta;

        Origem(String descricao, String descricaoCurta) {
            this.descricao = descricao;
            this.descricaoCurta = descricaoCurta;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getDescricaoCurta() {
            return descricaoCurta;
        }
    }
}
