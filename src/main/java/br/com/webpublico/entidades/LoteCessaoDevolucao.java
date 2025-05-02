package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 29/01/15
 * Time: 17:13
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Devolução da Cessão de Bens Móveis")
public class LoteCessaoDevolucao extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número")
    private Long codigo;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data/Hora")
    @Temporal(TemporalType.TIMESTAMP)
    private Date data;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Solicitante")
    @ManyToOne
    private UsuarioSistema usuario;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @OneToOne
    @Etiqueta("Cessão")
    private LoteCessao loteCessao;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data de Devolução")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataDevolucao;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Motivo da Devolução")
    private String motivoDevolucao;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Observações")
    private String observacoes;

    @Obrigatorio
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    private SituacaoEventoBem situacao;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @OneToMany(mappedBy = "loteCessaoDevolucao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CessaoDevolucao> bensDevolvidos;

    public LoteCessaoDevolucao() {
        super();
        bensDevolvidos = new ArrayList<>();
        situacao = SituacaoEventoBem.EM_ELABORACAO;
        detentorArquivoComposicao = new DetentorArquivoComposicao();
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

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public LoteCessao getLoteCessao() {
        return loteCessao;
    }

    public void setLoteCessao(LoteCessao loteCessao) {
        this.loteCessao = loteCessao;
    }

    public List<CessaoDevolucao> getBensDevolvidos() {
        return bensDevolvidos;
    }

    public void setBensDevolvidos(List<CessaoDevolucao> bensDevolvidos) {
        this.bensDevolvidos = bensDevolvidos;
    }

    public Date getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(Date dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public String getMotivoDevolucao() {
        return motivoDevolucao;
    }

    public void setMotivoDevolucao(String motivoDevolucao) {
        this.motivoDevolucao = motivoDevolucao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public SituacaoEventoBem getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoEventoBem situacao) {
        this.situacao = situacao;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public Cessao[] cessaoSelecionadas() {
        Cessao[] cessao = new Cessao[this.getBensDevolvidos().size()];
        int x = 0;
        for (CessaoDevolucao cessaoDevolucao : this.getBensDevolvidos()) {
            cessao[x] = cessaoDevolucao.getCessao();
            cessao[x].setConservacaoBem(cessaoDevolucao.getConservacaoBem());
            x++;
        }
        return cessao;
    }

    public void atribuirConservacaoTransient(List<Cessao> list) {
        for (CessaoDevolucao cessaoDevolucao : this.getBensDevolvidos()) {
            for (Cessao cessao : list) {
                if (cessao.equals(cessaoDevolucao.getCessao())) {
                    cessao.setConservacaoBem(cessaoDevolucao.getConservacaoBem());
                }
            }
        }
    }

    public Boolean isConcluido() {
        return SituacaoEventoBem.CONCLUIDO.equals(situacao);
    }
}
