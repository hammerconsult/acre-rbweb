package br.com.webpublico.entidades.rh.exoneracao;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.MotivoExoneracaoRescisao;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
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
@Etiqueta("Exoneração / Rescisão do Servidor por Lote")
public class ExoneracaoRescisaoLote extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da Exoneração")
    @Tabelavel
    @Pesquisavel
    private Date dataExoneracao;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data da Operação")
    @Tabelavel
    @Pesquisavel
    private Date dataOperacao;
    @ManyToOne
    private MotivoExoneracaoRescisao motivoExoneracaoRescisao;
    @ManyToOne
    @Etiqueta("Ato Legal")
    @Tabelavel
    @Pesquisavel
    private AtoLegal atoLegal;
    @ManyToOne
    @Etiqueta("Usuário Responsável")
    @Tabelavel
    @Pesquisavel
    private UsuarioSistema usuarioSistema;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "exoneracaoRescisaoLote")
    private List<ExoneracaoRescisaoContrato> itemExoneracaoContrato;


    public ExoneracaoRescisaoLote() {
        itemExoneracaoContrato = Lists.newArrayList();
    }

    public MotivoExoneracaoRescisao getMotivoExoneracaoRescisao() {
        return motivoExoneracaoRescisao;
    }

    public void setMotivoExoneracaoRescisao(MotivoExoneracaoRescisao motivoExoneracaoRescisao) {
        this.motivoExoneracaoRescisao = motivoExoneracaoRescisao;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }


    public Date getDataExoneracao() {
        return dataExoneracao;
    }

    public void setDataExoneracao(Date dataExoneracao) {
        this.dataExoneracao = dataExoneracao;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public List<ExoneracaoRescisaoContrato> getItemExoneracaoContrato() {
        return itemExoneracaoContrato;
    }

    public void setItemExoneracaoContrato(List<ExoneracaoRescisaoContrato> itemExoneracaoContrato) {
        this.itemExoneracaoContrato = itemExoneracaoContrato;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
