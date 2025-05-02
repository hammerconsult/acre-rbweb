package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 15/10/14
 * Time: 17:24
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Diário de Tráfego")
public class DiarioTrafego extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Ano")
    @Obrigatorio
    private Integer ano;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Mês")
    @Obrigatorio
    private Integer mes;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Veículo")
    @Obrigatorio
    @ManyToOne
    private Veiculo veiculo;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "diarioTrafego")
    private List<ItemDiarioTrafego> itensDiarioTrafego;
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private String observacao;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public DiarioTrafego() {
        itensDiarioTrafego = new ArrayList();
    }

    public DiarioTrafego(DiarioTrafego diarioTrafego, HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.id = diarioTrafego.getId();
        this.veiculo = diarioTrafego.getVeiculo();
        this.ano = diarioTrafego.getAno();
        this.mes = diarioTrafego.getMes();
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public List<ItemDiarioTrafego> getItensDiarioTrafego() {
        if (itensDiarioTrafego != null) {
            Collections.sort(itensDiarioTrafego, new Comparator<ItemDiarioTrafego>() {
                @Override
                public int compare(ItemDiarioTrafego o1, ItemDiarioTrafego o2) {
                    int retorno = o1.getDataLancamento().compareTo(o2.getDataLancamento());

                    if (retorno == 0) {
                        retorno = o1.getHoraSaida().compareTo(o2.getHoraSaida());
                    }
                    return retorno;
                }
            });
            Collections.sort(itensDiarioTrafego, new Comparator<ItemDiarioTrafego>() {
                @Override
                public int compare(ItemDiarioTrafego o1, ItemDiarioTrafego o2) {
                    if( o1.getDataLancamento().compareTo(o2.getDataLancamento()) == 0) {
                        return o1.getHoraSaida().compareTo(o2.getHoraSaida());
                    }
                    return 0;
                }
            });
        }
        return itensDiarioTrafego;
    }

    public void setItensDiarioTrafego(List<ItemDiarioTrafego> itensDiarioTrafego) {
        this.itensDiarioTrafego = itensDiarioTrafego;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    @Override
    public String toString() {
        return "Veículo: " + veiculo + " Ano: " + ano + " Mês: " + mes;
    }
}
