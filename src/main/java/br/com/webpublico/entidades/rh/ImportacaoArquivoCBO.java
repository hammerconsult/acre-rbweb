package br.com.webpublico.entidades.rh;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.rh.cbo.TipoCBO;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@GrupoDiagrama(nome = "RecursosHumanos")
@Audited
@Entity
public class ImportacaoArquivoCBO extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Responsável")
    private UsuarioSistema responsavel;
    @Etiqueta("Data de Importação")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataImportacao;
    @Etiqueta("Arquivo")
    @OneToOne(cascade ={CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;
    @Etiqueta("Tipo do CBO")
    @Enumerated(EnumType.STRING)
    private TipoCBO tipoCBO;

    public ImportacaoArquivoCBO() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public Date getDataImportacao() {
        return dataImportacao;
    }

    public void setDataImportacao(Date dataImportacao) {
        this.dataImportacao = dataImportacao;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public TipoCBO getTipoCBO() {
        return tipoCBO;
    }

    public void setTipoCBO(TipoCBO tipoCBO) {
        this.tipoCBO = tipoCBO;
    }
}
