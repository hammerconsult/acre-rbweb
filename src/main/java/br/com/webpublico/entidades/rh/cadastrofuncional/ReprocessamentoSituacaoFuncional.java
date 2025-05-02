package br.com.webpublico.entidades.rh.cadastrofuncional;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Audited
@Entity
@Etiqueta("Reprocessamento Situação Funcional")
@Table(name = "REPROSITUACAOFUNCIONAL")
public class ReprocessamentoSituacaoFuncional extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Processamento")
    private Date dataProcessamento;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Responsável")
    private UsuarioSistema responsavel;

    @OneToMany(mappedBy = "reprocessamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private List<ReprocessamentoSituacaoFuncionalVinculo> reprocessamentoVinculo;

    public ReprocessamentoSituacaoFuncional() {
        this.reprocessamentoVinculo = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataProcessamento() {
        return dataProcessamento;
    }

    public void setDataProcessamento(Date dataProcessamento) {
        this.dataProcessamento = dataProcessamento;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public List<ReprocessamentoSituacaoFuncionalVinculo> getReprocessamentoVinculo() {
        return reprocessamentoVinculo;
    }

    public void setReprocessamentoVinculo(List<ReprocessamentoSituacaoFuncionalVinculo> reprocessamentoVinculo) {
        this.reprocessamentoVinculo = reprocessamentoVinculo;
    }
}
