package br.com.webpublico.entidades;

import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Etiqueta("Associação de Empenho com Fichas Financeiras")
@Audited
@Entity
public class EmpenhoVinculoFP extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data de Cadastro")
    private Date dataCadastro;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Empenho")
    @Obrigatorio
    private Empenho empenho;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "empenhoVinculoFP")
    private List<EmpenhoFichaFinanceiraFP> fichasFinanceiras;

    public EmpenhoVinculoFP() {
        super();
        fichasFinanceiras = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public List<EmpenhoFichaFinanceiraFP> getFichasFinanceiras() {
        return fichasFinanceiras;
    }

    public void setFichasFinanceiras(List<EmpenhoFichaFinanceiraFP> fichasFinanceiras) {
        this.fichasFinanceiras = fichasFinanceiras;
    }

    @Override
    public String toString() {
        return DataUtil.getDataFormatada(dataCadastro) + " - " + empenho;
    }
}
