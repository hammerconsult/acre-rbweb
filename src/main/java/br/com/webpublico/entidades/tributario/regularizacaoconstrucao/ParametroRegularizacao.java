package br.com.webpublico.entidades.tributario.regularizacaoconstrucao;

import br.com.webpublico.entidades.*;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributario")
@Etiqueta("Parâmetro de Regularização de Construção")
public class ParametroRegularizacao extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Secretaria")
    private String secretaria;
    @Obrigatorio
    @Etiqueta("Secretário")
    @ManyToOne
    private UsuarioSistema secretario;
    @OneToOne(cascade = CascadeType.ALL)
    private Arquivo assinaturaSecretario;
    @Obrigatorio
    @Etiqueta("Diretor do Departamento")
    @ManyToOne
    private UsuarioSistema diretor;
    @OneToOne(cascade = CascadeType.ALL)
    private Arquivo assinaturaDiretor;
    @Etiqueta("Dia do Mês Para Arquivo de Saída WebSevice")
    private Integer diaDoMesWebService;
    @Etiqueta("Prazo em dias para o vencimento do cartaz de alvará de construção")
    private Integer prazoCartaz;
    @Etiqueta("Prazo em dias para o vencimento da taxa de alvará de construção")
    private Integer prazoTaxaAlvara;
    @Etiqueta("Prazo em dias para o vencimento da taxa de vistoria de conclusão de obra")
    private Integer prazoTaxaVistoria;
    @Etiqueta("Prazo em dias para comunicação automática do contribuinte de vencimento do alvará de construção")
    private Integer prazoComunicacaoContribuinte;
    @Etiqueta("Dídida do Alvará")
    @ManyToOne
    private Divida dividaAlvara;
    @Etiqueta("Dídida do Habite-se")
    @ManyToOne
    private Divida dividaHabitese;
    @Etiqueta("Dídida da Taxa de Vistoria")
    @ManyToOne
    private Divida dividaTaxaVistoria;
    @Etiqueta("Tributo do Habite-se")
    @ManyToOne
    private Tributo tributoHabitese;
    @Etiqueta("Tributo da Taxa de Vistoria")
    @ManyToOne
    private Tributo tributoTaxaVistoria;
    @Etiqueta("Serviço de Construção da Taxa de Vistoria")
    @ManyToOne
    private ServicoConstrucao servicoConstrucao;
    @ManyToOne
    private TipoDoctoOficial tipoDoctoOficial;
    @ManyToOne
    private TipoDoctoOficial tipoDoctoOficialAlvaraImediato;
    @ManyToOne
    private TipoDoctoOficial tipoDoctoOficialHabitese;
    @Obrigatorio
    @Etiqueta("Valor da Alíquota ISSQN")
    private BigDecimal valorAliquotaISSQN;
    @OneToMany(mappedBy = "parametroRegularizacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VinculoServicoTributo> vinculosServicosTributos;

    public ParametroRegularizacao() {
        this.vinculosServicosTributos = Lists.newArrayList();
    }

    public ParametroRegularizacao(Exercicio exercicio, String secretaria, UsuarioSistema secretario,
                                  UsuarioSistema diretor, Integer diaDoMesWebService, Integer prazoCartaz,
                                  Integer prazoTaxaAlvara, Integer prazoTaxaVistoria,
                                  Integer prazoComunicacaoContribuinte) {
        this.exercicio = exercicio;
        this.secretaria = secretaria;
        this.secretario = secretario;
        this.diretor = diretor;
        this.diaDoMesWebService = diaDoMesWebService;
        this.prazoCartaz = prazoCartaz;
        this.prazoTaxaAlvara = prazoTaxaAlvara;
        this.prazoTaxaVistoria = prazoTaxaVistoria;
        this.prazoComunicacaoContribuinte = prazoComunicacaoContribuinte;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(String secretaria) {
        this.secretaria = secretaria;
    }

    public UsuarioSistema getSecretario() {
        return secretario;
    }

    public void setSecretario(UsuarioSistema secretario) {
        this.secretario = secretario;
    }

    public Arquivo getAssinaturaSecretario() {
        return assinaturaSecretario;
    }

    public void setAssinaturaSecretario(Arquivo assinaturaSecretario) {
        this.assinaturaSecretario = assinaturaSecretario;
    }

    public UsuarioSistema getDiretor() {
        return diretor;
    }

    public void setDiretor(UsuarioSistema diretor) {
        this.diretor = diretor;
    }

    public Arquivo getAssinaturaDiretor() {
        return assinaturaDiretor;
    }

    public void setAssinaturaDiretor(Arquivo assinaturaDiretor) {
        this.assinaturaDiretor = assinaturaDiretor;
    }

    public Integer getDiaDoMesWebService() {
        return diaDoMesWebService;
    }

    public void setDiaDoMesWebService(Integer diaDoMesWebService) {
        this.diaDoMesWebService = diaDoMesWebService;
    }

    public Integer getPrazoCartaz() {
        return prazoCartaz;
    }

    public void setPrazoCartaz(Integer prazoCartaz) {
        this.prazoCartaz = prazoCartaz;
    }

    public Integer getPrazoTaxaAlvara() {
        return prazoTaxaAlvara;
    }

    public void setPrazoTaxaAlvara(Integer prazoTaxaAlvara) {
        this.prazoTaxaAlvara = prazoTaxaAlvara;
    }

    public Integer getPrazoTaxaVistoria() {
        return prazoTaxaVistoria;
    }

    public void setPrazoTaxaVistoria(Integer prazoTaxaVistoria) {
        this.prazoTaxaVistoria = prazoTaxaVistoria;
    }

    public Integer getPrazoComunicacaoContribuinte() {
        return prazoComunicacaoContribuinte;
    }

    public void setPrazoComunicacaoContribuinte(Integer prazoComunicacaoContribuinte) {
        this.prazoComunicacaoContribuinte = prazoComunicacaoContribuinte;
    }

    public Divida getDividaAlvara() {
        return dividaAlvara;
    }

    public void setDividaAlvara(Divida dividaAlvara) {
        this.dividaAlvara = dividaAlvara;
    }

    public TipoDoctoOficial getTipoDoctoOficialAlvaraImediato() {
        return tipoDoctoOficialAlvaraImediato;
    }

    public void setTipoDoctoOficialAlvaraImediato(TipoDoctoOficial tipoDoctoOficialAlvaraImediato) {
        this.tipoDoctoOficialAlvaraImediato = tipoDoctoOficialAlvaraImediato;
    }

    public Divida getDividaHabitese() {
        return dividaHabitese;
    }

    public void setDividaHabitese(Divida dividaHabitese) {
        this.dividaHabitese = dividaHabitese;
    }

    public Divida getDividaTaxaVistoria() {
        return dividaTaxaVistoria;
    }

    public void setDividaTaxaVistoria(Divida dividaTaxaVistoria) {
        this.dividaTaxaVistoria = dividaTaxaVistoria;
    }

    public Tributo getTributoHabitese() {
        return tributoHabitese;
    }

    public void setTributoHabitese(Tributo tributoHabitese) {
        this.tributoHabitese = tributoHabitese;
    }

    public Tributo getTributoTaxaVistoria() {
        return tributoTaxaVistoria;
    }

    public void setTributoTaxaVistoria(Tributo tributoTaxaVistoria) {
        this.tributoTaxaVistoria = tributoTaxaVistoria;
    }

    public ServicoConstrucao getServicoConstrucao() {
        return servicoConstrucao;
    }

    public void setServicoConstrucao(ServicoConstrucao servicoConstrucao) {
        this.servicoConstrucao = servicoConstrucao;
    }

    public TipoDoctoOficial getTipoDoctoOficial() {
        return tipoDoctoOficial;
    }

    public void setTipoDoctoOficial(TipoDoctoOficial tipoDoctoOficial) {
        this.tipoDoctoOficial = tipoDoctoOficial;
    }

    public BigDecimal getValorAliquotaISSQN() {
        return valorAliquotaISSQN;
    }

    public void setValorAliquotaISSQN(BigDecimal valorAliquotaISSQN) {
        this.valorAliquotaISSQN = valorAliquotaISSQN;
    }

    public TipoDoctoOficial getTipoDoctoOficialHabitese() {
        return tipoDoctoOficialHabitese;
    }

    public void setTipoDoctoOficialHabitese(TipoDoctoOficial tipoDoctoOficialHabitese) {
        this.tipoDoctoOficialHabitese = tipoDoctoOficialHabitese;
    }

    public VinculoServicoTributo getVinculoPorServico(ServicoConstrucao servicoConstrucao) {
        for (VinculoServicoTributo vinculosServicosTributo : vinculosServicosTributos) {
            if (vinculosServicosTributo.getServicoConstrucao().equals(servicoConstrucao)) {
                return vinculosServicosTributo;
            }
        }
        return null;
    }

    public List<VinculoServicoTributo> getVinculosServicosTributos() {
        if (vinculosServicosTributos == null) vinculosServicosTributos = Lists.newArrayList();
        return vinculosServicosTributos;
    }

    public void setVinculosServicosTributos(List<VinculoServicoTributo> vinculosServicosTributos) {
        this.vinculosServicosTributos = vinculosServicosTributos;
    }

    @Override
    public String toString() {
        return "parametroregularizacao{" +
            "id=" + id +
            ", exercicio=" + exercicio +
            ", secretaria='" + secretaria + '\'' +
            ", secretario='" + secretario + '\'' +
            ", diretor='" + diretor + '\'' +
            ", diaDoMesWebService=" + diaDoMesWebService +
            ", prazoCartaz=" + prazoCartaz +
            ", prazoTaxaAlvara=" + prazoTaxaAlvara +
            ", prazoTaxaVistoria=" + prazoTaxaVistoria +
            ", prazoComunicacaoContribuinte=" + prazoComunicacaoContribuinte +
            '}';
    }
}
