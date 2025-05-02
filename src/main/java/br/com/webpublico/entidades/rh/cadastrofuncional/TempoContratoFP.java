package br.com.webpublico.entidades.rh.cadastrofuncional;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.PessoaFisicaPortal;
import br.com.webpublico.pessoa.dto.TempoContratoFPDTO;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by William on 21/05/2019.
 */
@Entity
@Audited
public class TempoContratoFP extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String localTrabalho;
    @Temporal(TemporalType.DATE)
    private Date inicio;
    @Temporal(TemporalType.DATE)
    private Date fim;
    @ManyToOne
    @NotAudited
    private PessoaFisicaPortal pessoaFisicaPortal;

    static public List<TempoContratoFP> dtoTOTempoContratoFPList(List<TempoContratoFPDTO> dtos, PessoaFisicaPortal pessoa) {
        if (dtos == null) {
            return null;
        }
        List<TempoContratoFP> item = Lists.newArrayList();
        for (TempoContratoFPDTO dto : dtos) {
            item.add(dtoTOTempoContratoFP(dto, pessoa));
        }
        return item;
    }

    static public List<TempoContratoFPDTO> tempoContratoFPListToDto(List<TempoContratoFP> tempoContratoFP) {
        if (tempoContratoFP == null) {
            return null;
        }
        List<TempoContratoFPDTO> dtos = Lists.newArrayList();
        for (TempoContratoFP tempoContrato : tempoContratoFP) {
            TempoContratoFPDTO dto = new TempoContratoFPDTO();
            dto.setId(tempoContrato.getId());
            dto.setInicio(tempoContrato.getInicio());
            dto.setFim(tempoContrato.getFim());
            dto.setLocalTrabalhado(tempoContrato.getLocalTrabalho());
            dtos.add(dto);
        }
        return dtos;
    }

    static public TempoContratoFP dtoTOTempoContratoFP(TempoContratoFPDTO tempoContrato, PessoaFisicaPortal pessoa) {
        TempoContratoFP tempo = new TempoContratoFP();
        tempo.setId(tempoContrato.getId());
        tempo.setInicio(tempoContrato.getInicio());
        tempo.setFim(tempoContrato.getFim());
        tempo.setLocalTrabalho(tempoContrato.getLocalTrabalhado());
        tempo.setPessoaFisicaPortal(pessoa);
        return tempo;
    }


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocalTrabalho() {
        return localTrabalho;
    }

    public void setLocalTrabalho(String localTrabalho) {
        this.localTrabalho = localTrabalho;
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

    public PessoaFisicaPortal getPessoaFisicaPortal() {
        return pessoaFisicaPortal;
    }

    public void setPessoaFisicaPortal(PessoaFisicaPortal pessoaFisicaPortal) {
        this.pessoaFisicaPortal = pessoaFisicaPortal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TempoContratoFP that = (TempoContratoFP) o;
        return Objects.equals(localTrabalho != null ? localTrabalho.toUpperCase().trim() : null, that.localTrabalho != null ? that.localTrabalho.toUpperCase().trim() : null)
            && Objects.equals(DataUtil.getDataFormatada(inicio), DataUtil.getDataFormatada(that.inicio))
            && Objects.equals(DataUtil.getDataFormatada(fim), DataUtil.getDataFormatada(that.fim));
    }

    @Override
    public int hashCode() {
        return Objects.hash(localTrabalho, inicio, fim);
    }
}
