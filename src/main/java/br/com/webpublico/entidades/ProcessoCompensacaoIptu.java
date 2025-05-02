package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity

@GrupoDiagrama(nome = "Tributario")
@Audited
@Etiqueta("Processo de Débito")
public class ProcessoCompensacaoIptu implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Lançamento")
    private Date lancamento;
    private String observacao;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Invisivel
    @OneToMany(mappedBy = "processoCompensacaoIptu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcessoCompensacaoIptuItem> itens;
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;
    @Invisivel
    private BigDecimal ufmProcesso;

    public ProcessoCompensacaoIptu() {
        itens = new ArrayList<ProcessoCompensacaoIptuItem>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public List<ProcessoCompensacaoIptuItem> getItens() {
        return itens;
    }

    public List<ProcessoCompensacaoIptuItem> getItensIncorretos() {
        List<ProcessoCompensacaoIptuItem> retorno = Lists.newArrayList();
        for (ProcessoCompensacaoIptuItem item : itens) {
            if (ProcessoCompensacaoIptuItem.TipoItem.INCORRETO.equals(item.getTipoItem())) {
                retorno.add(item);
            }
        }
        return retorno;
    }

    public List<ProcessoCompensacaoIptuItem> getItensCorretos() {
        List<ProcessoCompensacaoIptuItem> retorno = Lists.newArrayList();
        for (ProcessoCompensacaoIptuItem item : itens) {
            if (ProcessoCompensacaoIptuItem.TipoItem.CORRETO.equals(item.getTipoItem())) {
                retorno.add(item);
            }
        }
        return retorno;
    }

    public void setItens(List<ProcessoCompensacaoIptuItem> itens) {
        this.itens = itens;
    }

    public Date getLancamento() {
        return lancamento;
    }

    public void setLancamento(Date lancamento) {
        this.lancamento = lancamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ProcessoCompensacaoIptu[ id=" + id + " ]";
    }

    public BigDecimal getUfmProcesso() {
        return ufmProcesso != null ? ufmProcesso : BigDecimal.ZERO;
    }

    public void setUfmProcesso(BigDecimal ufmProcesso) {
        this.ufmProcesso = ufmProcesso;
    }

    public String getCodigoExercicio() {
        if (exercicio != null) {
            return codigo + "/" + exercicio.getAno();
        }
        return codigo.toString();
    }
}
