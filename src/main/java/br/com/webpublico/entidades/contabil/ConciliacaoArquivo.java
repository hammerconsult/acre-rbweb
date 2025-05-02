package br.com.webpublico.entidades.contabil;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ContaBancariaEntidade;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Audited
@Entity
public class ConciliacaoArquivo extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data")
    private Date dataCadastro;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data da Conciliação")
    private Date dataConciliacao;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta Bancária")
    private ContaBancariaEntidade contaBancariaEntidade;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Etiqueta("Arquivo")
    private Arquivo arquivo;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "conciliacaoArquivo")
    private List<ConciliacaoArquivoMovimentoArquivo> movimentos;

    public ConciliacaoArquivo() {
        super();
        movimentos = Lists.newArrayList();
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

    public Date getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(Date dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public List<ConciliacaoArquivoMovimentoArquivo> getMovimentos() {
        return movimentos;
    }

    public void setMovimentos(List<ConciliacaoArquivoMovimentoArquivo> movimentos) {
        this.movimentos = movimentos;
    }

    @Override
    public String toString() {
        return DataUtil.getDataFormatada(dataCadastro) + " " + usuarioSistema.getNome();
    }
}
