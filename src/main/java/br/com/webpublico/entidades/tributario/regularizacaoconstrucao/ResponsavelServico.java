package br.com.webpublico.entidades.tributario.regularizacaoconstrucao;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.tributario.regularizacaoconstrucao.TipoEspecialidadeServico;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributario")
@Etiqueta("Responsavel Técnico")
public class ResponsavelServico extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private Pessoa pessoa;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("CAU/CREA")
    private String cauCrea;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Especialidade")
    private TipoEspecialidadeServico tipoEspecialidadeServico;

    public ResponsavelServico() {
        super();
    }

    public ResponsavelServico(Pessoa pessoa, String cauCrea, TipoEspecialidadeServico tipoEspecialidadeServico) {
        this();
        this.pessoa = pessoa;
        this.cauCrea = cauCrea;
        this.tipoEspecialidadeServico = tipoEspecialidadeServico;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getCauCrea() {
        return cauCrea;
    }

    public void setCauCrea(String cauCrea) {
        this.cauCrea = cauCrea;
    }

    public TipoEspecialidadeServico getTipoEspecialidadeServico() {
        return tipoEspecialidadeServico;
    }

    public void setTipoEspecialidadeServico(TipoEspecialidadeServico tipoEspecialidadeServico) {
        this.tipoEspecialidadeServico = tipoEspecialidadeServico;
    }

    @Override
    public String toString() {
        return pessoa.getCpf_Cnpj() + " - " + pessoa.getNome();
    }

}
