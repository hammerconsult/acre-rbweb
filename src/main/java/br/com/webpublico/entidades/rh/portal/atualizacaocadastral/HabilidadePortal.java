package br.com.webpublico.entidades.rh.portal.atualizacaocadastral;

import br.com.webpublico.entidades.Habilidade;
import br.com.webpublico.pessoa.dto.HabilidadeDTO;
import com.google.common.collect.Lists;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by peixe on 29/08/17.
 */
@Entity
public class HabilidadePortal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PessoaFisicaPortal pessoaFisicaPortal;
    @ManyToOne
    private Habilidade habilidade;


    public HabilidadePortal() {
    }

    public HabilidadePortal(PessoaFisicaPortal pessoaFisicaPortal, Habilidade habilidade) {
        this.pessoaFisicaPortal = pessoaFisicaPortal;
        this.habilidade = habilidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaFisicaPortal getPessoaFisicaPortal() {
        return pessoaFisicaPortal;
    }

    public void setPessoaFisicaPortal(PessoaFisicaPortal pessoaFisicaPortal) {
        this.pessoaFisicaPortal = pessoaFisicaPortal;
    }

    public Habilidade getHabilidade() {
        return habilidade;
    }

    public void setHabilidade(Habilidade habilidade) {
        this.habilidade = habilidade;
    }

    public static List<HabilidadePortal> dtoToHabilidadesPortal(List<HabilidadeDTO> habilidades, PessoaFisicaPortal pessoa) {
        List<HabilidadePortal> formacoes = Lists.newLinkedList();
        for (HabilidadeDTO dto : habilidades) {
            HabilidadePortal f = dtoToHabilidadePortal(dto, pessoa);
            if (f != null) {
                formacoes.add(f);
            }
        }
        return formacoes;
    }

    private static HabilidadePortal dtoToHabilidadePortal(HabilidadeDTO dto, PessoaFisicaPortal pessoa) {
        HabilidadePortal habilidadePortal = new HabilidadePortal();
        habilidadePortal.setHabilidade(Habilidade.toHabilidade(dto));
        habilidadePortal.setPessoaFisicaPortal(pessoa);
        return habilidadePortal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HabilidadePortal that = (HabilidadePortal) o;
        return Objects.equals(habilidade, that.habilidade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(habilidade);
    }
}
