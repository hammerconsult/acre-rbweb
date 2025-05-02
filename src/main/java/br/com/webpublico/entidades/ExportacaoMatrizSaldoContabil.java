package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoBalanceteExportacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Audited
@Entity
@Table(name = "EXPORTACAOMSC")
@Etiqueta("Exportação Matriz Saldo Contábil")
public class ExportacaoMatrizSaldoContabil extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data Exportação")
    private Date dataExportacao;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo")
    private TipoBalanceteExportacao tipoBalanceteExportacao;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Mês")
    private Mes mes;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;
    @Obrigatorio
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Etiqueta("Arquivo")
    private Arquivo arquivo;


    public ExportacaoMatrizSaldoContabil() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataExportacao() {
        return dataExportacao;
    }

    public void setDataExportacao(Date dataExportacao) {
        this.dataExportacao = dataExportacao;
    }

    public TipoBalanceteExportacao getTipoBalanceteExportacao() {
        return tipoBalanceteExportacao;
    }

    public void setTipoBalanceteExportacao(TipoBalanceteExportacao tipoBalanceteExportacao) {
        this.tipoBalanceteExportacao = tipoBalanceteExportacao;
        if (TipoBalanceteExportacao.ENCERRAMENTO.equals(tipoBalanceteExportacao)) {
            mes = Mes.DEZEMBRO;
        }
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }
}
