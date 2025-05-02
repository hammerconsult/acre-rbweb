package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 07/11/14
 * Time: 15:12
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Baixa de Veículo ou Equipamento/Máquina")
public class BaixaObjetoFrota extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Data")
    @Temporal(TemporalType.DATE)
    private Date dataBaixa;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Responsável")
    @ManyToOne
    private UsuarioSistema responsavel;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Tipo de Objeto")
    @Enumerated(EnumType.STRING)
    private TipoObjetoFrota tipoObjetoFrota;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Veículo ou Equipamento/Máquina")
    @ManyToOne
    private ObjetoFrota objetoFrota;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Motivo")
    @Length(maximo = 255)
    private String motivo;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public BaixaObjetoFrota() {
    }

    public BaixaObjetoFrota(BaixaObjetoFrota baixaObjetoFrota, HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.setId(baixaObjetoFrota.getId());
        this.setCodigo(baixaObjetoFrota.getCodigo());
        this.setDataBaixa(baixaObjetoFrota.getDataBaixa());
        this.setResponsavel(baixaObjetoFrota.getResponsavel());
        this.setMotivo(baixaObjetoFrota.getMotivo());
        this.setTipoObjetoFrota(baixaObjetoFrota.getTipoObjetoFrota());
        this.setObjetoFrota(baixaObjetoFrota.getObjetoFrota());
        this.setHierarquiaOrganizacional(hierarquiaOrganizacional);
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

    public Date getDataBaixa() {
        return dataBaixa;
    }

    public void setDataBaixa(Date dataBaixa) {
        this.dataBaixa = dataBaixa;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public TipoObjetoFrota getTipoObjetoFrota() {
        return tipoObjetoFrota;
    }

    public void setTipoObjetoFrota(TipoObjetoFrota tipoObjetoFrota) {
        this.tipoObjetoFrota = tipoObjetoFrota;
    }

    public ObjetoFrota getObjetoFrota() {
        return objetoFrota;
    }

    public void setObjetoFrota(ObjetoFrota objetoFrota) {
        this.objetoFrota = objetoFrota;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }
}
