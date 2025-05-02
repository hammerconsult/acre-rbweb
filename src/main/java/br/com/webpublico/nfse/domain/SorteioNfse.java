package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.nfse.enums.SituacaoSorteioNfse;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by William on 19/01/2019.
 */
@Entity

@Audited
@GrupoDiagrama(nome = "NFSE")
@Etiqueta("Sorteio")
public class SorteioNfse extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Campanha")
    @ManyToOne
    private CampanhaNfse campanha;
    @Etiqueta("Situação do Sorteio")
    @Enumerated(EnumType.STRING)
    private SituacaoSorteioNfse situacao;
    @Etiqueta("Número do Sorteio")
    private Integer numero;
    @Obrigatorio
    @Etiqueta("Descrição do Sorteio")
    private String descricao;
    @Obrigatorio
    @Etiqueta("Início de Emissão da Nfs-e")
    @Temporal(TemporalType.DATE)
    private Date inicioEmissaoNotaFiscal;
    @Obrigatorio
    @Etiqueta("Fim de Emissão da Nfs-e")
    @Temporal(TemporalType.DATE)
    private Date fimEmissaoNotaFiscal;
    @Obrigatorio
    @Etiqueta("Data de Divulgação dos Cupons")
    @Temporal(TemporalType.DATE)
    private Date dataDivulgacaoCupom;
    @OneToMany(mappedBy = "sorteio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PremioSorteioNfse> premios;
    @Obrigatorio
    @Etiqueta("Data do Sorteio")
    @Temporal(TemporalType.DATE)
    private Date dataSorteio;
    @Obrigatorio
    @Etiqueta("Divulgação Resultado do Sorteio")
    @Temporal(TemporalType.DATE)
    private Date dataDivulgacaoSorteio;
    @Etiqueta("Numero Sorteado")
    private String numeroSorteado;

    public SorteioNfse() {
        super();
        situacao = SituacaoSorteioNfse.ABERTO;
        premios = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CampanhaNfse getCampanha() {
        return campanha;
    }

    public void setCampanha(CampanhaNfse campanha) {
        this.campanha = campanha;
    }

    public SituacaoSorteioNfse getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoSorteioNfse situacao) {
        this.situacao = situacao;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getInicioEmissaoNotaFiscal() {
        return inicioEmissaoNotaFiscal;
    }

    public void setInicioEmissaoNotaFiscal(Date inicioEmissaoNotaFiscal) {
        this.inicioEmissaoNotaFiscal = inicioEmissaoNotaFiscal;
    }

    public Date getFimEmissaoNotaFiscal() {
        return fimEmissaoNotaFiscal;
    }

    public void setFimEmissaoNotaFiscal(Date fimEmissaoNotaFiscal) {
        this.fimEmissaoNotaFiscal = fimEmissaoNotaFiscal;
    }

    public Date getDataDivulgacaoCupom() {
        return dataDivulgacaoCupom;
    }

    public void setDataDivulgacaoCupom(Date dataDivulgacaoCupom) {
        this.dataDivulgacaoCupom = dataDivulgacaoCupom;
    }

    public Date getDataSorteio() {
        return dataSorteio;
    }

    public void setDataSorteio(Date dataSorteio) {
        this.dataSorteio = dataSorteio;
    }

    public Date getDataDivulgacaoSorteio() {
        return dataDivulgacaoSorteio;
    }

    public void setDataDivulgacaoSorteio(Date dataDivulgacaoSorteio) {
        this.dataDivulgacaoSorteio = dataDivulgacaoSorteio;
    }

    public List<PremioSorteioNfse> getPremios() {
        return premios;
    }

    public void setPremios(List<PremioSorteioNfse> premios) {
        this.premios = premios;
    }

    public String getNumeroSorteado() {
        return numeroSorteado;
    }

    public void setNumeroSorteado(String numeroSorteado) {
        this.numeroSorteado = numeroSorteado;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public Integer proximaSequenciaPremio() {
        if (premios != null && !premios.isEmpty()) {
            Collections.sort(premios, new Comparator<PremioSorteioNfse>() {
                @Override
                public int compare(PremioSorteioNfse o1, PremioSorteioNfse o2) {
                    return o2.getSequencia().compareTo(o1.getSequencia());
                }
            });
            return premios.get(0).getSequencia() + 1;
        }
        return 1;
    }

    public Integer getQuantidadeCuponsPremiacao() {
        Integer quantidadeCuponsPremiacao = 0;
        if (premios != null) {
            for (PremioSorteioNfse premio : premios) {
                quantidadeCuponsPremiacao += premio.getQuantidade();
            }
        }
        return quantidadeCuponsPremiacao;
    }
}
