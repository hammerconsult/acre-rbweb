package br.com.webpublico.entidades.rh.portal.atualizacaocadastral;

import br.com.webpublico.enums.TipoTelefone;
import br.com.webpublico.pessoa.dto.TelefoneDTO;
import com.google.common.collect.Lists;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by peixe on 29/08/17.
 */
@Entity
public class TelefonePortal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PessoaFisicaPortal pessoaFisicaPortal;
    @ManyToOne
    private DependentePortal dependentePortal;
    private String telefone;
    @Enumerated(EnumType.STRING)
    private TipoTelefone tipoFone;
    private Boolean principal;
    private Date dataRegistro;
    private String pessoaContato;

    public TelefonePortal() {
    }

    public TelefonePortal(Long id, PessoaFisicaPortal pessoaFisicaPortal, String telefone, TipoTelefone tipoFone, Boolean principal, Date dataRegistro, String pessoaContato) {
        this.id = id;
        this.pessoaFisicaPortal = pessoaFisicaPortal;
        this.telefone = telefone;
        this.tipoFone = tipoFone;
        this.principal = principal;
        this.dataRegistro = dataRegistro;
        this.pessoaContato = pessoaContato;
    }

    public static List<TelefonePortal> dtoToTelefones(List<TelefoneDTO> dtos, PessoaFisicaPortal pessoa, DependentePortal dependente) {
        List<TelefonePortal> telefones = Lists.newLinkedList();
        if (dtos != null && !dtos.isEmpty()) {
            for (TelefoneDTO dto : dtos) {
                TelefonePortal f = dtoToTelefone(dto, pessoa, dependente);
                if (f != null) {
                    telefones.add(f);
                }
            }
        }
        return telefones;
    }

    private static TelefonePortal dtoToTelefone(TelefoneDTO dto, PessoaFisicaPortal pessoa, DependentePortal dependente) {
        TelefonePortal tel = new TelefonePortal();
        tel.setDataRegistro(dto.getDataRegistro());
        tel.setPessoaContato(dto.getPessoaContato());
        tel.setPessoaFisicaPortal(pessoa);
        tel.setDependentePortal(dependente);
        tel.setPrincipal(dto.getPrincipalTelefone());
        tel.setTelefone(dto.getTelefone());
        tel.setTipoFone(dto.getTipoFone() != null ? TipoTelefone.valueOf(dto.getTipoFone().name()) : null);
        return tel;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public TipoTelefone getTipoFone() {
        return tipoFone;
    }

    public void setTipoFone(TipoTelefone tipoFone) {
        this.tipoFone = tipoFone;
    }

    public Boolean getPrincipal() {
        return principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getPessoaContato() {
        return pessoaContato;
    }

    public void setPessoaContato(String pessoaContato) {
        this.pessoaContato = pessoaContato;
    }

    public DependentePortal getDependentePortal() {
        return dependentePortal;
    }

    public void setDependentePortal(DependentePortal dependentePortal) {
        this.dependentePortal = dependentePortal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TelefonePortal that = (TelefonePortal) o;
        return Objects.equals(telefone != null ? telefone.trim() : null, that.telefone != null ? that.telefone.trim() : null) && tipoFone == that.tipoFone && Objects.equals(principal, that.principal) && Objects.equals(pessoaContato, that.pessoaContato);
    }

    @Override
    public int hashCode() {
        return Objects.hash(telefone, tipoFone, principal, pessoaContato);
    }
}
