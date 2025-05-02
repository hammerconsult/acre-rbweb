package br.com.webpublico.nfse.domain.pgdas;

import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.nfse.domain.pgdas.registros.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta(value = "Arquivo PGDAS")
public class ArquivoPgdas extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Data de Importação")
    private Date dataImportacao;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Responsável")
    private UsuarioSistema responsavel;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Etiqueta("Abertura do Arquivo Digital")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistroAAAAA> registrosAAAAA;

    @Etiqueta("Indicação do Contribuinte e Dados da Apuração")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro00000> registros00000;

    @Etiqueta("Processo para não Optante")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro00001> registros00001;

    @Etiqueta("Valor Apurado pelo Cálculo")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro01000> registros01000;

    @Etiqueta("Receitas Brutas de Períodos Anteriores à Opção")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro01500> registros01500;

    @Etiqueta("Receitas Brutas de Períodos Anteriores à Opção no Mercado Interno")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro01501> registros01501;

    @Etiqueta("Receitas Brutas de Períodos Anteriores à Opção no Mercado Externo")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro01502> registros01502;

    @Etiqueta("Receitas Brutas de Períodos Anteriores, Valor Original e Tributos Fixos")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro02000> registros02000;

    @Etiqueta("Estabelecimento Filial")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro03000> registros03000;

    @Etiqueta("Atividade Selecionada para Cada Estabelecimento")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro03100> registros03100;

    @Etiqueta("Receita por Atividade com Percentual (faixa A)")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro03110> registros03110;

    @Etiqueta("Receita por Atividade com Percentual (faixa B)")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro03120> registros03120;

    @Etiqueta("Receita por Atividade com Percentual (faixa C)")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro03130> registros03130;

    @Etiqueta("Receita com Isenção (faixa A)")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro03111> registros03111;

    @Etiqueta("Receita com Isenção (faixa B)")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro03121> registros03121;

    @Etiqueta("Receita com Isenção (faixa C)")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro03131> registros03131;

    @Etiqueta("Receita por Atividade com Percentual (faixa A)")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro03112> registros03112;

    @Etiqueta("Receita por Atividade com Percentual (faixa B)")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro03122> registros03122;

    @Etiqueta("Receita por Atividade com Percentual (faixa C)")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro03132> registros03132;

    @Etiqueta("Folha de Salários")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro03500> registros03500;

    @Etiqueta("Perfil")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro04000> registros04000;

    @Etiqueta("Encerramento do Bloco 00000")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistro99999> registros99999;

    @Etiqueta("Encerramento do Arquivo Digital")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoPgdas")
    private List<PgdasRegistroZZZZZ> registrosZZZZZ;

    public ArquivoPgdas() {
        inicializarListas();
    }

    public void inicializarListas() {
        this.registros00000 = Lists.newArrayList();
        this.registrosAAAAA = Lists.newArrayList();
        this.registros00001 = Lists.newArrayList();
        this.registros01000 = Lists.newArrayList();
        this.registros01500 = Lists.newArrayList();
        this.registros01501 = Lists.newArrayList();
        this.registros01502 = Lists.newArrayList();
        this.registros02000 = Lists.newArrayList();
        this.registros03000 = Lists.newArrayList();
        this.registros03100 = Lists.newArrayList();
        this.registros03110 = Lists.newArrayList();
        this.registros03111 = Lists.newArrayList();
        this.registros03112 = Lists.newArrayList();
        this.registros03120 = Lists.newArrayList();
        this.registros03121 = Lists.newArrayList();
        this.registros03122 = Lists.newArrayList();
        this.registros03130 = Lists.newArrayList();
        this.registros03131 = Lists.newArrayList();
        this.registros03132 = Lists.newArrayList();
        this.registros03500 = Lists.newArrayList();
        this.registros04000 = Lists.newArrayList();
        this.registrosZZZZZ = Lists.newArrayList();
        this.registros99999 = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataImportacao() {
        return dataImportacao;
    }

    public void setDataImportacao(Date dataImportacao) {
        this.dataImportacao = dataImportacao;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public List<PgdasRegistroAAAAA> getRegistrosAAAAA() {
        return registrosAAAAA;
    }

    public void setRegistrosAAAAA(List<PgdasRegistroAAAAA> registrosAAAAA) {
        this.registrosAAAAA = registrosAAAAA;
    }

    public List<PgdasRegistro00000> getRegistros00000() {
        return registros00000;
    }

    public void setRegistros00000(List<PgdasRegistro00000> registros00000) {
        this.registros00000 = registros00000;
    }

    public List<PgdasRegistro00001> getRegistros00001() {
        return registros00001;
    }

    public void setRegistros00001(List<PgdasRegistro00001> registros00001) {
        this.registros00001 = registros00001;
    }

    public List<PgdasRegistro01000> getRegistros01000() {
        return registros01000;
    }

    public void setRegistros01000(List<PgdasRegistro01000> registros01000) {
        this.registros01000 = registros01000;
    }

    public List<PgdasRegistro01500> getRegistros01500() {
        return registros01500;
    }

    public void setRegistros01500(List<PgdasRegistro01500> registros01500) {
        this.registros01500 = registros01500;
    }

    public List<PgdasRegistro01501> getRegistros01501() {
        return registros01501;
    }

    public void setRegistros01501(List<PgdasRegistro01501> registros01501) {
        this.registros01501 = registros01501;
    }

    public List<PgdasRegistro01502> getRegistros01502() {
        return registros01502;
    }

    public void setRegistros01502(List<PgdasRegistro01502> registros01502) {
        this.registros01502 = registros01502;
    }

    public List<PgdasRegistro02000> getRegistros02000() {
        return registros02000;
    }

    public void setRegistros02000(List<PgdasRegistro02000> registros02000) {
        this.registros02000 = registros02000;
    }

    public List<PgdasRegistro03000> getRegistros03000() {
        return registros03000;
    }

    public void setRegistros03000(List<PgdasRegistro03000> registros03000) {
        this.registros03000 = registros03000;
    }

    public List<PgdasRegistro03100> getRegistros03100() {
        return registros03100;
    }

    public void setRegistros03100(List<PgdasRegistro03100> registros03100) {
        this.registros03100 = registros03100;
    }

    public List<PgdasRegistro03110> getRegistros03110() {
        return registros03110;
    }

    public void setRegistros03110(List<PgdasRegistro03110> registros03110) {
        this.registros03110 = registros03110;
    }

    public List<PgdasRegistro03120> getRegistros03120() {
        return registros03120;
    }

    public void setRegistros03120(List<PgdasRegistro03120> registros03120) {
        this.registros03120 = registros03120;
    }

    public List<PgdasRegistro03111> getRegistros03111() {
        return registros03111;
    }

    public void setRegistros03111(List<PgdasRegistro03111> registros03111) {
        this.registros03111 = registros03111;
    }

    public List<PgdasRegistro03112> getRegistros03112() {
        return registros03112;
    }

    public void setRegistros03112(List<PgdasRegistro03112> registros03112) {
        this.registros03112 = registros03112;
    }

    public List<PgdasRegistro03121> getRegistros03121() {
        return registros03121;
    }

    public void setRegistros03121(List<PgdasRegistro03121> registros03121) {
        this.registros03121 = registros03121;
    }

    public List<PgdasRegistro03122> getRegistros03122() {
        return registros03122;
    }

    public void setRegistros03122(List<PgdasRegistro03122> registros03122) {
        this.registros03122 = registros03122;
    }

    public List<PgdasRegistro03130> getRegistros03130() {
        return registros03130;
    }

    public void setRegistros03130(List<PgdasRegistro03130> registros03130) {
        this.registros03130 = registros03130;
    }

    public List<PgdasRegistro03131> getRegistros03131() {
        return registros03131;
    }

    public void setRegistros03131(List<PgdasRegistro03131> registros03131) {
        this.registros03131 = registros03131;
    }

    public List<PgdasRegistro03132> getRegistros03132() {
        return registros03132;
    }

    public void setRegistros03132(List<PgdasRegistro03132> registros03132) {
        this.registros03132 = registros03132;
    }

    public List<PgdasRegistro03500> getRegistros03500() {
        return registros03500;
    }

    public void setRegistros03500(List<PgdasRegistro03500> registros03500) {
        this.registros03500 = registros03500;
    }

    public List<PgdasRegistro04000> getRegistros04000() {
        return registros04000;
    }

    public void setRegistros04000(List<PgdasRegistro04000> registros04000) {
        this.registros04000 = registros04000;
    }

    public List<PgdasRegistro99999> getRegistros99999() {
        return registros99999;
    }

    public void setRegistros99999(List<PgdasRegistro99999> registros99999) {
        this.registros99999 = registros99999;
    }

    public List<PgdasRegistroZZZZZ> getRegistrosZZZZZ() {
        return registrosZZZZZ;
    }

    public void setRegistrosZZZZZ(List<PgdasRegistroZZZZZ> registrosZZZZZ) {
        this.registrosZZZZZ = registrosZZZZZ;
    }

    @Override
    public String toString() {
        return DataUtil.getDataFormatada(this.dataImportacao);
    }

    public PgdasRegistro03000 getUltimoRegistro03000() {
        try {
            return this.getRegistros03000().get(this.getRegistros03000().size() - 1);
        } catch (Exception e) {
            return null;
        }
    }

    public PgdasRegistro03100 getUltimoRegistro03100() {
        try {
            return this.getRegistros03100().get(this.getRegistros03100().size() - 1);
        } catch (Exception e) {
            return null;
        }
    }

    public PgdasRegistro03130 getUltimoRegistro03130() {
        try {
            return this.getRegistros03130().get(this.getRegistros03130().size() - 1);
        } catch (Exception e) {
            return null;
        }
    }



}
