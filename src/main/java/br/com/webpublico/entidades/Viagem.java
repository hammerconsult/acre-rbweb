/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author Munif
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Frota")
@Etiqueta("Viagem")
public class Viagem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Veículo")
    @Pesquisavel
    @OneToOne
    private Veiculo veiculo;
    @Etiqueta("Responsável")
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Obrigatorio
    private PessoaFisica pessoaFisica;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Data Saída")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataSaida;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data Retorno")
    @Pesquisavel
    @Tabelavel
    private Date dataRetorno;
    @Tabelavel
    @Etiqueta("Quilometragem Saída")
    @Pesquisavel
    private Integer kmSaida;
    @Etiqueta("Quilometragem Retorno")
    @Tabelavel
    @Pesquisavel
    private Integer kmRetorno;
    @Tabelavel
    @Etiqueta("Descrição")
    @Pesquisavel
    private String descricao;
    @OneToMany(mappedBy = "viagem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItinerarioViagem> itinerarioViagem;
    @OneToMany(mappedBy = "viagem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ViagemManutencaoVeiculo> manutencoesVeiculo;
    @OneToMany(mappedBy = "viagem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ViagemAbastecimento> abastecimentos;

    public Viagem() {
        kmSaida = 0;
        kmRetorno = 0;
    }

    public Viagem(Viagem viagem, HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.setId(viagem.getId());
        this.setVeiculo(viagem.getVeiculo());
        this.setPessoaFisica(viagem.getPessoaFisica());
        this.setDataSaida(viagem.getDataSaida());
        this.setDataRetorno(viagem.getDataRetorno());
        this.setKmSaida(viagem.getKmSaida());
        this.setKmRetorno(viagem.getKmRetorno());
        this.setDescricao(viagem.getDescricao());
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public List<ViagemAbastecimento> getAbastecimentos() {
        return abastecimentos;
    }

    public void setAbastecimentos(List<ViagemAbastecimento> abastecimentos) {
        this.abastecimentos = abastecimentos;
    }

    public Date getDataRetorno() {
        return dataRetorno;
    }

    public void setDataRetorno(Date dataRetorno) {
        this.dataRetorno = dataRetorno;
    }

    public Date getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(Date dataSaida) {
        this.dataSaida = dataSaida;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<ItinerarioViagem> getItinerarioViagem() {
        if (itinerarioViagem != null) {
            Collections.sort(itinerarioViagem, new Comparator<ItinerarioViagem>() {
                @Override
                public int compare(ItinerarioViagem o1, ItinerarioViagem o2) {
                    return o1.getOrdem().compareTo(o2.getOrdem());
                }
            });
        }
        return itinerarioViagem;
    }

    public void setItinerarioViagem(List<ItinerarioViagem> itinerarioViagem) {
        this.itinerarioViagem = itinerarioViagem;
    }

    public Integer getKmRetorno() {
        return kmRetorno;
    }

    public void setKmRetorno(Integer kmRetorno) {
        this.kmRetorno = kmRetorno;
    }

    public Integer getKmSaida() {
        return kmSaida;
    }

    public void setKmSaida(Integer kmSaida) {
        this.kmSaida = kmSaida;
    }

    public List<ViagemManutencaoVeiculo> getManutencoesVeiculo() {
        return manutencoesVeiculo;
    }

    public void setManutencoesVeiculo(List<ViagemManutencaoVeiculo> manutencoesVeiculo) {
        this.manutencoesVeiculo = manutencoesVeiculo;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Viagem)) {
            return false;
        }
        Viagem other = (Viagem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Viagem de " + pessoaFisica + " com saída em " + new SimpleDateFormat("dd/MM/yyyy").format(dataSaida);
    }
}
