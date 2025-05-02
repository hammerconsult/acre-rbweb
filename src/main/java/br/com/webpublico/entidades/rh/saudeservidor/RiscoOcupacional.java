package br.com.webpublico.entidades.rh.saudeservidor;

import br.com.webpublico.entidades.PrestadorServicos;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.enums.rh.esocial.LocalRiscoOcupacional;
import br.com.webpublico.interfaces.IHistoricoEsocial;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by carlos on 24/06/15.
 */
@Entity
@Audited
@Etiqueta("Risco Ocupacional")
public class RiscoOcupacional extends SuperEntidade implements Serializable, IHistoricoEsocial {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer horaExposicao;

    @ManyToOne
    private VinculoFP vinculoFP;

    @ManyToOne
    private PrestadorServicos prestadorServico;

    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data de início")
    private Date inicio;

    @Temporal(TemporalType.DATE)
    private Date fim;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de estabelecimento do ambiente de trabalho")
    private LocalRiscoOcupacional localRiscoOcupacional;

    @Obrigatorio
    @Etiqueta("Descrição do Setor")
    private String descricaoSetor;

    @Obrigatorio
    @Etiqueta("Descrição das Atividades")
    private String descricaoAtividade;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "riscoOcupacional")
    private List<AgenteNocivo> itemAgenteNovico;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "riscoOcupacional")
    private List<RegistroAmbiental> itemRegistroAmbiental;


    public RiscoOcupacional() {
        itemAgenteNovico = Lists.newArrayList();
        itemRegistroAmbiental = Lists.newArrayList();
    }

    public Integer getHoraExposicao() {
        return horaExposicao;
    }

    public void setHoraExposicao(Integer horaExposicao) {
        this.horaExposicao = horaExposicao;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<AgenteNocivo> getItemAgenteNovico() {
        return itemAgenteNovico;
    }

    public void setItemAgenteNovico(List<AgenteNocivo> itemAgenteNovico) {
        this.itemAgenteNovico = itemAgenteNovico;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public PrestadorServicos getPrestadorServico() {
        return prestadorServico;
    }

    public void setPrestadorServico(PrestadorServicos prestadorServico) {
        this.prestadorServico = prestadorServico;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public LocalRiscoOcupacional getLocalRiscoOcupacional() {
        return localRiscoOcupacional;
    }

    public void setLocalRiscoOcupacional(LocalRiscoOcupacional localRiscoOcupacional) {
        this.localRiscoOcupacional = localRiscoOcupacional;
    }

    public String getDescricaoSetor() {
        return descricaoSetor;
    }

    public void setDescricaoSetor(String descricaoSetor) {
        this.descricaoSetor = descricaoSetor;
    }

    public String getDescricaoAtividade() {
        return descricaoAtividade;
    }

    public void setDescricaoAtividade(String descricaoAtividade) {
        this.descricaoAtividade = descricaoAtividade;
    }

    public List<RegistroAmbiental> getItemRegistroAmbiental() {
        return itemRegistroAmbiental;
    }

    public void setItemRegistroAmbiental(List<RegistroAmbiental> itemRegistroAmbiental) {
        this.itemRegistroAmbiental = itemRegistroAmbiental;
    }

    @Override
    public String toString() {
        try {
            return vinculoFP.getMatriculaFP().getPessoa().getNome();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getDescricaoCompleta() {
        return this.vinculoFP != null ? this.vinculoFP.toString() : this.prestadorServico.toString();
    }

    @Override
    public String getIdentificador() {
        return this.getId().toString();
    }
}
