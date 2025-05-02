package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributário")
@Etiqueta("Parecer de Trasferência de Movimentação do Cadastro Imobiliário")
public class ParecerTransferenciaMovBCI extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data do Parecer")
    private Date dataParecer;
    @Etiqueta("Justificativa")
    private String justificativa;
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Usuário do Parecer")
    private UsuarioSistema usarioParecer;
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Solicitação de Tranferência")
    private SolicitacaoTransfMovBCI solicitacao;
    @OneToMany(mappedBy = "parecer", orphanRemoval = true)
    @Etiqueta("Arquivos do Parecer")
    private List<ArquivoTransferenciaMovBCI> arquivos;

    public ParecerTransferenciaMovBCI() {
        this.arquivos = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataParecer() {
        return dataParecer;
    }

    public void setDataParecer(Date dataParecer) {
        this.dataParecer = dataParecer;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public SolicitacaoTransfMovBCI getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoTransfMovBCI solicitacao) {
        this.solicitacao = solicitacao;
    }

    public UsuarioSistema getUsarioParecer() {
        return usarioParecer;
    }

    public void setUsarioParecer(UsuarioSistema usarioParecer) {
        this.usarioParecer = usarioParecer;
    }

    public List<ArquivoTransferenciaMovBCI> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<ArquivoTransferenciaMovBCI> arquivos) {
        this.arquivos = arquivos;
    }
}
