package br.com.webpublico.entidades;


import br.com.webpublico.pessoa.dto.CadastroEconomicoDTO;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Etiqueta("Empresa Irregular")
@Entity
@Audited
public class EmpresaIrregular extends SuperEntidade {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Exercício")
    @ManyToOne
    private Exercicio exercicio;
    @Pesquisavel
    @Etiqueta("Cadastro Econômico")
    @Obrigatorio
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    private String jsonCadastroEconomicoDTO;
    @OrderBy("id asc")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "empresaIrregular")
    private List<SituacaoEmpresaIrregular> situacoes;
    @Tabelavel
    @Transient
    @Etiqueta("Cadastro Econômico")
    private String descricaoCadastroEconomico;
    @Tabelavel
    @Etiqueta("Serviço")
    @Transient
    private String descricaoServico;
    @Transient
    @Etiqueta("Última Irregularidade")
    @Tabelavel
    @ManyToOne
    private Irregularidade irregularidade;
    @Transient
    @Tabelavel
    @Etiqueta("Inserida Por Último Em")
    @Temporal(TemporalType.DATE)
    private Date inseridaEm;
    @Transient
    @Tabelavel
    @Etiqueta("Removida Por Último Em")
    @Temporal(TemporalType.DATE)
    private Date removidaEm;
    @Transient
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoEmpresaIrregular.Situacao situacao;
    @Transient
    private String anoProtocolo;
    @Transient
    private String numeroProtocolo;
    @Transient
    private MonitoramentoFiscal monitoramentoFiscal;
    @Transient
    private UsuarioSistema usuarioSistema;
    @Transient
    private String observacao;

    public EmpresaIrregular() {
        this.situacoes = Lists.newArrayList();
    }

    public EmpresaIrregular(EmpresaIrregular empresaIrregular) {
        SituacaoEmpresaIrregular ultimaSituacao = empresaIrregular.getUltimaSituacao();
        if (empresaIrregular.getUltimaSituacao(SituacaoEmpresaIrregular.Situacao.INSERIDA) != null) {
            this.setIrregularidade(empresaIrregular.getUltimaSituacao(SituacaoEmpresaIrregular.Situacao.INSERIDA).getIrregularidade());
        }
        this.setSituacao(ultimaSituacao != null ? ultimaSituacao.getSituacao() : null);
        if (empresaIrregular.getUltimaSituacao(SituacaoEmpresaIrregular.Situacao.INSERIDA) != null) {
            this.setInseridaEm(empresaIrregular.getUltimaSituacao(SituacaoEmpresaIrregular.Situacao.INSERIDA).getData());
        }
        this.setObservacao(ultimaSituacao != null ? ultimaSituacao.getObservacao() : null);
        this.setId(empresaIrregular.getId());
        this.setCadastroEconomico(empresaIrregular.getCadastroEconomico());
        this.setJsonCadastroEconomicoDTO(empresaIrregular.getJsonCadastroEconomicoDTO());
        this.setCodigo(empresaIrregular.getCodigo());
        this.setExercicio(empresaIrregular.getExercicio());
        CadastroEconomicoDTO cadastroEconomicoDTO = getCadastroEconomicoDTO();
        String nomeCpfCnpj;
        if (cadastroEconomicoDTO.getPessoaFisica() == null) {
            nomeCpfCnpj = cadastroEconomicoDTO.getPessoaJuridica().getNome() + " " + "(" + cadastroEconomicoDTO.getPessoaJuridica().getCnpj() + ")";
        } else {
            nomeCpfCnpj = cadastroEconomicoDTO.getPessoaFisica().getNome() + " " + "(" + cadastroEconomicoDTO.getPessoaFisica().getCpf() + ")";
        }
        this.setDescricaoCadastroEconomico(getCadastroEconomicoDTO().getInscricaoCadastral() + " - " + nomeCpfCnpj);
        if (cadastroEconomicoDTO.getServicos() != null && !cadastroEconomicoDTO.getServicos().isEmpty()) {
            descricaoServico = cadastroEconomicoDTO.getServicos().get(0).toString();
        }
        SituacaoEmpresaIrregular situacaoEmpresaIrregular = empresaIrregular.getUltimaSituacao(SituacaoEmpresaIrregular.Situacao.RETIRADA);
        if (situacaoEmpresaIrregular != null) {
            this.setRemovidaEm(situacaoEmpresaIrregular.getData());
        }
        this.situacoes = empresaIrregular.getSituacoes();
    }

    public Irregularidade getIrregularidade() {
        return irregularidade;
    }

    public void setIrregularidade(Irregularidade irregularidade) {
        this.irregularidade = irregularidade;
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

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public String getJsonCadastroEconomicoDTO() {
        if (jsonCadastroEconomicoDTO == null && cadastroEconomico != null) {
            jsonCadastroEconomicoDTO = Util.converterObjetoParaJson(cadastroEconomico.toCadastroEconomicoDTO());
        }
        return jsonCadastroEconomicoDTO;
    }

    public void setJsonCadastroEconomicoDTO(String jsonCadastroEconomicoDTO) {
        this.jsonCadastroEconomicoDTO = jsonCadastroEconomicoDTO;
    }

    public CadastroEconomicoDTO getCadastroEconomicoDTO() {
        return Util.converterDeJsonParaObjeto(getJsonCadastroEconomicoDTO(), CadastroEconomicoDTO.class);
    }

    public Date getInseridaEm() {
        return inseridaEm;
    }

    public void setInseridaEm(Date inseridaEm) {
        this.inseridaEm = inseridaEm;
    }

    public Date getRemovidaEm() {
        return removidaEm;
    }

    public void setRemovidaEm(Date removidaEm) {
        this.removidaEm = removidaEm;
    }

    public SituacaoEmpresaIrregular.Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoEmpresaIrregular.Situacao situacao) {
        this.situacao = situacao;
    }

    public List<SituacaoEmpresaIrregular> getSituacoes() {
        return situacoes;
    }

    public void setSituacoes(List<SituacaoEmpresaIrregular> situacoes) {
        this.situacoes = situacoes;
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public MonitoramentoFiscal getMonitoramentoFiscal() {
        return monitoramentoFiscal;
    }

    public void setMonitoramentoFiscal(MonitoramentoFiscal monitoramentoFiscal) {
        this.monitoramentoFiscal = monitoramentoFiscal;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getDescricaoCadastroEconomico() {
        return descricaoCadastroEconomico;
    }

    public void setDescricaoCadastroEconomico(String descricaoCadastroEconomico) {
        this.descricaoCadastroEconomico = descricaoCadastroEconomico;
    }

    public String getDescricaoServico() {
        return descricaoServico;
    }

    public void setDescricaoServico(String descricaoServico) {
        this.descricaoServico = descricaoServico;
    }

    public SituacaoEmpresaIrregular getUltimaSituacao(SituacaoEmpresaIrregular.Situacao... situacao) {
        List<SituacaoEmpresaIrregular> situacoes = Lists.newArrayList(this.situacoes);
        Collections.sort(situacoes, new Comparator<SituacaoEmpresaIrregular>() {
            @Override
            public int compare(SituacaoEmpresaIrregular o1, SituacaoEmpresaIrregular o2) {
                return o2.getId().compareTo(o1.getId());
            }
        });
        if (!situacoes.isEmpty()) {
            if (situacao == null || situacao.length == 0) {
                return situacoes.get(0);
            } else {
                for (SituacaoEmpresaIrregular situacoe : situacoes) {
                    if (situacoe.getSituacao().equals(situacao[0])) {
                        return situacoe;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Long getId() {
        return id;
    }
}
