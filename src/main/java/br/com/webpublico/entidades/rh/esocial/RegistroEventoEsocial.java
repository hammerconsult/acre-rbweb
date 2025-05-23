package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoTrabalhadorEsocial;
import br.com.webpublico.enums.rh.esocial.TipoApuracaoFolha;
import br.com.webpublico.enums.rh.esocial.TipoClasseESocial;
import br.com.webpublico.enums.rh.esocial.TipoOperacaoESocial;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Etiqueta("Registro E-Social S1200")
@Entity
@Audited
public class RegistroEventoEsocial extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Mês")
    private Mes mes;

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ano")
    private Exercicio exercicio;

    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Folha de Pagamento")
    private TipoFolhaDePagamento tipoFolhaDePagamento;

    @ManyToOne
    @Tabelavel
    private Entidade entidade;

    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo Classe E-Social")
    private TipoClasseESocial tipoClasseESocial;

    @Enumerated(EnumType.STRING)
    private TipoTrabalhadorEsocial tipoTrabalhadorEsocial;

    @OneToMany(mappedBy = "registroEventoEsocial", cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private List<VinculoFPEventoEsocial> itemVinculoFP;

    @Transient
    private PrestadorServicos prestadorServicos;
    @Transient
    private VinculoFPEventoEsocial[] itemVinculoFPSelecionados;
    @Transient
    private Boolean marcarTodos;

    @Enumerated
    @Transient
    private TipoOperacaoESocial tipoOperacaoESocial;

    @Transient
    private TipoApuracaoFolha tipoApuracaoFolha;

    @Transient
    private PessoaFisica pessoaFisica;


    public RegistroEventoEsocial() {
        itemVinculoFP = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public TipoClasseESocial getTipoClasseESocial() {
        return tipoClasseESocial;
    }

    public void setTipoClasseESocial(TipoClasseESocial tipoClasseESocial) {
        this.tipoClasseESocial = tipoClasseESocial;
    }

    public TipoTrabalhadorEsocial getTipoTrabalhadorEsocial() {
        return tipoTrabalhadorEsocial;
    }

    public void setTipoTrabalhadorEsocial(TipoTrabalhadorEsocial tipoTrabalhadorEsocial) {
        this.tipoTrabalhadorEsocial = tipoTrabalhadorEsocial;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public List<VinculoFPEventoEsocial> getItensVinculo() {
        List<VinculoFPEventoEsocial> vinculosEvento = Lists.newArrayList();
        if (itemVinculoFP != null && !itemVinculoFP.isEmpty()) {
            for (VinculoFPEventoEsocial itemVinculo : itemVinculoFP) {
                if (itemVinculo.getVinculoFP() != null) {
                    vinculosEvento.add(itemVinculo);
                }
            }
        }
        return vinculosEvento;
    }

    public List<VinculoFPEventoEsocial> getItensPrestador() {
        List<VinculoFPEventoEsocial> prestadoresEvento = Lists.newArrayList();
        if (itemVinculoFP != null && !itemVinculoFP.isEmpty()) {
            for (VinculoFPEventoEsocial itemVinculo : itemVinculoFP) {
                if (itemVinculo.getPrestadorServico() != null) {
                    prestadoresEvento.add(itemVinculo);
                }
            }
        }
        return prestadoresEvento;
    }

    public List<VinculoFPEventoEsocial> getItemVinculoFP() {
        return itemVinculoFP;
    }

    public void setItemVinculoFP(List<VinculoFPEventoEsocial> itemVinculoFP) {
        this.itemVinculoFP = itemVinculoFP;
    }

    public PrestadorServicos getPrestadorServicos() {
        return prestadorServicos;
    }

    public void setPrestadorServicos(PrestadorServicos prestadorServicos) {
        this.prestadorServicos = prestadorServicos;
    }

    public TipoOperacaoESocial getTipoOperacaoESocial() {
        return tipoOperacaoESocial;
    }

    public void setTipoOperacaoESocial(TipoOperacaoESocial tipoOperacaoESocial) {
        this.tipoOperacaoESocial = tipoOperacaoESocial;
    }

    public VinculoFPEventoEsocial[] getItemVinculoFPSelecionados() {
        return itemVinculoFPSelecionados;
    }

    public void setItemVinculoFPSelecionados(VinculoFPEventoEsocial[] itemVinculoFPSelecionados) {
        this.itemVinculoFPSelecionados = itemVinculoFPSelecionados;
    }

    public Boolean getMarcarTodos() {
        return marcarTodos != null ? marcarTodos : false;
    }

    public void setMarcarTodos(Boolean marcarTodos) {
        this.marcarTodos = marcarTodos;
    }

    public TipoApuracaoFolha getTipoApuracaoFolha() {
        return tipoApuracaoFolha;
    }

    public void setTipoApuracaoFolha(TipoApuracaoFolha tipoApuracaoFolha) {
        this.tipoApuracaoFolha = tipoApuracaoFolha;
    }

    public void marcarTodosItensVinculos() {
        if (marcarTodos) {
            itemVinculoFPSelecionados = itemVinculoFP.toArray(new VinculoFPEventoEsocial[0]);
        } else {
            itemVinculoFPSelecionados = null;
        }
    }
}
