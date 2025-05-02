package br.com.webpublico.entidades.rh.cadastrofuncional;

import br.com.webpublico.entidades.Dependente;
import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.PessoaFisicaPortal;
import br.com.webpublico.pessoa.dto.DependenteDTO;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by William on 23/05/2019.
 */
@Entity
@Audited
public class DependenteInativacaoPortal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    private Dependente dependente;
    @NotAudited
    @ManyToOne
    private PessoaFisicaPortal pessoaFisicaPortal;

    static public List<DependenteInativacaoPortal> dtoToDepentendeInativacaoPortalList(List<DependenteDTO> dtos, PessoaFisicaPortal pessoa) {
        List<DependenteInativacaoPortal> item = Lists.newArrayList();
        for (DependenteDTO dto : dtos) {
            if (dto.getInativarDependente()){
                item.add(dtoToDependenteInativacaoPortal(dto, pessoa));
            }
        }
        return item;
    }

    static public DependenteInativacaoPortal dtoToDependenteInativacaoPortal(DependenteDTO dependenteDTO, PessoaFisicaPortal pessoa) {
        DependenteInativacaoPortal dependenteInativacao = new DependenteInativacaoPortal();
        dependenteInativacao.setDependente(Dependente.dtoToDependente(dependenteDTO) );
        dependenteInativacao.setPessoaFisicaPortal(pessoa);
        return dependenteInativacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Dependente getDependente() {
        return dependente;
    }

    public void setDependente(Dependente dependente) {
        this.dependente = dependente;
    }

    public PessoaFisicaPortal getPessoaFisicaPortal() {
        return pessoaFisicaPortal;
    }

    public void setPessoaFisicaPortal(PessoaFisicaPortal pessoaFisicaPortal) {
        this.pessoaFisicaPortal = pessoaFisicaPortal;
    }
}
