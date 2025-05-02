package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.AlvaraCnaes;
import br.com.webpublico.entidadesauxiliares.AlvaraEnderecos;
import br.com.webpublico.enums.SituacaoCalculoAlvara;
import br.com.webpublico.enums.TipoCalculoAlvara;
import br.com.webpublico.enums.TipoControleCalculoAlvara;
import br.com.webpublico.enums.TipoEndereco;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Audited
@Etiqueta("Cálculo Alvará")
@GrupoDiagrama(nome = "Alvara")
public class ProcessoCalculoAlvara extends ProcessoCalculo implements br.com.webpublico.interfaces.CalculoAlvara {
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;
    @Etiqueta("Número do Protocolo")
    private String numeroProtocolo;
    @Etiqueta("Motivo do Estorno")
    private String motivoEstorno;
    @Etiqueta("Data do Estorno")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEstorno;
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação do Cálculo de Alvará")
    private SituacaoCalculoAlvara situacaoCalculoAlvara;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Cálculo do Alvará")
    private TipoCalculoAlvara tipoCalculoAlvara;
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuario;
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Cadastro Econômico - CMC")
    private CadastroEconomico cadastroEconomico;
    @ManyToOne
    @Etiqueta("Alvará")
    private Alvara alvara;
    @ManyToOne
    @Etiqueta("Usuário do Estorno")
    private UsuarioSistema usuarioEstorno;
    @Pesquisavel
    @Etiqueta("Valor Total Calculado")
    private BigDecimal valorTotalCalculado;
    private Boolean renovacao;
    @OneToMany(mappedBy = "processoCalculoAlvara", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CNAEProcessoCalculoAlvara> cnaes;
    @OneToMany(mappedBy = "processoCalculoAlvara", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoAlvara> calculosAlvara;
    @OneToMany(mappedBy = "processoCalculoAlvara", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricoImpressaoAlvara> historicosAlvara;
    @OneToMany(mappedBy = "processoCalculoAlvara", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnderecoCalculoAlvara> enderecosAlvara;
    @OneToMany(mappedBy = "processoCalculoAlvara", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorarioFuncCalculoAlvara> horariosAlvara;
    @OneToMany(mappedBy = "processoCalculoAlvara", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CaracteristicaFuncCalculoAlvara> caracteristicasAlvara;
    @OneToMany(mappedBy = "processoCalculoAlvara", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LogAlvaraRedeSim> logsRedeSim;

    @Transient
    private Boolean emitir;
    @Transient
    private Boolean alterouFuncionamento;

    public ProcessoCalculoAlvara() {
        cnaes = Lists.newArrayList();
        calculosAlvara = Lists.newArrayList();
        historicosAlvara = Lists.newArrayList();
        enderecosAlvara = Lists.newArrayList();
        horariosAlvara = Lists.newArrayList();
        caracteristicasAlvara = Lists.newArrayList();
        logsRedeSim = Lists.newArrayList();
        valorTotalCalculado = BigDecimal.ZERO;
        renovacao = false;
        alterouFuncionamento = false;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    @Override
    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    @Override
    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getMotivoEstorno() {
        return motivoEstorno;
    }

    public void setMotivoEstorno(String motivoEstorno) {
        this.motivoEstorno = motivoEstorno;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public SituacaoCalculoAlvara getSituacaoCalculoAlvara() {
        return situacaoCalculoAlvara;
    }

    public void setSituacaoCalculoAlvara(SituacaoCalculoAlvara situacaoCalculoAlvara) {
        this.situacaoCalculoAlvara = situacaoCalculoAlvara;
    }

    public TipoCalculoAlvara getTipoCalculoAlvara() {
        return tipoCalculoAlvara;
    }

    public void setTipoCalculoAlvara(TipoCalculoAlvara tipoCalculoAlvara) {
        this.tipoCalculoAlvara = tipoCalculoAlvara;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    @Override
    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    @Override
    public Alvara getAlvara() {
        return alvara;
    }

    public void setAlvara(Alvara alvara) {
        this.alvara = alvara;
    }

    public UsuarioSistema getUsuarioEstorno() {
        return usuarioEstorno;
    }

    public void setUsuarioEstorno(UsuarioSistema usuarioEstorno) {
        this.usuarioEstorno = usuarioEstorno;
    }

    public List<CNAEProcessoCalculoAlvara> getCnaes() {
        return cnaes;
    }

    public void setCnaes(List<CNAEProcessoCalculoAlvara> cnaes) {
        this.cnaes = cnaes;
    }

    public List<CalculoAlvara> getCalculosAlvara() {
        return calculosAlvara;
    }

    public void setCalculosAlvara(List<CalculoAlvara> calculosAlvara) {
        this.calculosAlvara = calculosAlvara;
    }

    public List<HistoricoImpressaoAlvara> getHistoricosAlvara() {
        return historicosAlvara;
    }

    public void setHistoricosAlvara(List<HistoricoImpressaoAlvara> historicosAlvara) {
        this.historicosAlvara = historicosAlvara;
    }

    public List<EnderecoCalculoAlvara> getEnderecosAlvara() {
        if (enderecosAlvara != null) {
            enderecosAlvara.sort(Comparator.comparing(EnderecoCalculoAlvara::getSequencial));
        }
        return enderecosAlvara;
    }

    public void setEnderecosAlvara(List<EnderecoCalculoAlvara> enderecosAlvara) {
        this.enderecosAlvara = enderecosAlvara;
    }

    public List<HorarioFuncCalculoAlvara> getHorariosAlvara() {
        return horariosAlvara;
    }

    public void setHorariosAlvara(List<HorarioFuncCalculoAlvara> horariosAlvara) {
        this.horariosAlvara = horariosAlvara;
    }

    public List<CaracteristicaFuncCalculoAlvara> getCaracteristicasAlvara() {
        return caracteristicasAlvara;
    }

    public void setCaracteristicasAlvara(List<CaracteristicaFuncCalculoAlvara> caracteristicasAlvara) {
        this.caracteristicasAlvara = caracteristicasAlvara;
    }

    public BigDecimal getValorTotalCalculado() {
        return valorTotalCalculado;
    }

    public void setValorTotalCalculado(BigDecimal valorTotalCalculado) {
        this.valorTotalCalculado = valorTotalCalculado;
    }

    public Boolean getRenovacao() {
        return renovacao != null ? renovacao : Boolean.FALSE;
    }

    public void setRenovacao(Boolean renovacao) {
        this.renovacao = renovacao;
    }

    @Override
    public List<? extends Calculo> getCalculos() {
        return buscarRecalculos();
    }

    @Override
    public Boolean getEmitir() {
        return emitir != null ? emitir : Boolean.FALSE;
    }

    @Override
    public void setEmitir(Boolean emitir) {
        this.emitir = emitir;
    }

    public List<AlvaraCnaes> recuperarCnaes() {
        if (!cnaes.isEmpty()) {
            List<AlvaraCnaes> cnaesAlvara = Lists.newArrayList();
            Boolean possuiCnaeEstadual = cnaes.stream().anyMatch(c -> c.getExercidaNoLocal() &&
                c.getCnae().getInteresseDoEstado());
            for (CNAEProcessoCalculoAlvara cnae : cnaes) {
                AlvaraCnaes alvaraCnae = new AlvaraCnaes();
                alvaraCnae.setCodigoCnae(cnae.getCnae().getCodigoCnae());
                alvaraCnae.setDescricaoDetalhada(cnae.getCnae().getDescricaoDetalhada());
                alvaraCnae.setGrauDeRisco(cnae.getCnae().getGrauDeRisco().getDescricao());
                alvaraCnae.setInteresseDoEstado(cnae.getCnae().getInteresseDoEstado());
                alvaraCnae.setSanitario(cnae.getCnae().getFiscalizacaoSanitaria());
                alvaraCnae.setExercidaNoLocal(cnae.getExercidaNoLocal());
                alvaraCnae.setPrincipal(EconomicoCNAE.TipoCnae.Primaria.equals(cnae.getTipoCnae()));
                if (cnae.getExercidaNoLocal()) {
                    if (cnae.getCnae().getFiscalizacaoSanitaria() && possuiCnaeEstadual) {
                        alvaraCnae.setAmbito("Sanitário Estadual");
                    } else {
                        alvaraCnae.setAmbito(cnae.getCnae().getAmbito());
                        alvaraCnae.setLicenca(cnae.getCnae().getInteresseDoEstado() ? "" : cnae.getCnae().getLicenca());
                    }
                }
                cnaesAlvara.add(alvaraCnae);
            }
            cnaesAlvara.sort(Comparator
                .comparing(AlvaraCnaes::getExercidaNoLocal)
                .reversed()
                .thenComparing(AlvaraCnaes::getCodigoCnae));
            return cnaesAlvara;
        }
        return Lists.newArrayList();
    }

    public AlvaraEnderecos buscarEnderecoPrincipal() {
        if (enderecosAlvara == null) {
            return null;
        }
        return enderecosAlvara.stream()
            .filter(ea -> TipoEndereco.COMERCIAL.equals(ea.getTipoEndereco()))
            .map(this::transformarEnderecoCalculoAlvaraEmAlvaraEnderecos)
            .findFirst()
            .orElse(null);
    }

    private AlvaraEnderecos transformarEnderecoCalculoAlvaraEmAlvaraEnderecos(EnderecoCalculoAlvara ea) {
        AlvaraEnderecos alvaraEnderecos = new AlvaraEnderecos();
        alvaraEnderecos.setPrincipal(Boolean.FALSE);
        alvaraEnderecos.setSequencial(ea.getSequencial());
        alvaraEnderecos.setBairro(ea.getBairro());
        alvaraEnderecos.setLogradouro(ea.getLogradouro());
        alvaraEnderecos.setNumero(ea.getNumero());
        alvaraEnderecos.setComplemento(ea.getComplemento());
        alvaraEnderecos.setCep(ea.getCep());
        alvaraEnderecos.setAreaUtilizacao(ea.getAreaUtilizacao());
        return alvaraEnderecos;
    }

    public List<AlvaraEnderecos> buscarEnderecosSecundarios() {
        if (enderecosAlvara == null) {
            return Lists.newArrayList();
        }
        return enderecosAlvara.stream()
            .filter(ea -> !TipoEndereco.COMERCIAL.equals(ea.getTipoEndereco()))
            .map(this::transformarEnderecoCalculoAlvaraEmAlvaraEnderecos)
            .collect(Collectors.toList());
    }

    public List<CalculoAlvara> buscarCalculos() {
        List<CalculoAlvara> calculoAlvaras = buscarCalculos(TipoControleCalculoAlvara.CALCULO);
        calculoAlvaras.addAll(buscarCalculos(TipoControleCalculoAlvara.AGUARDANDO_CANCELAMENTO));
        return calculoAlvaras;
    }

    public List<CalculoAlvara> buscarRecalculos() {
        return buscarCalculos(TipoControleCalculoAlvara.RECALCULO);
    }

    public List<CalculoAlvara> buscarCalculosAtuais() {
        List<CalculoAlvara> calculoAtuais = Lists.newArrayList();
        calculoAtuais.addAll(buscarCalculos());
        calculoAtuais.addAll(buscarRecalculos());
        return calculoAtuais;
    }

    public List<CalculoAlvara> buscarCalculosCancelados() {
        return buscarCalculos(TipoControleCalculoAlvara.CANCELADO);
    }

    private List<CalculoAlvara> buscarCalculos(TipoControleCalculoAlvara tipoControle) {
        List<CalculoAlvara> calculos = Lists.newArrayList();
        if (calculosAlvara != null && !calculosAlvara.isEmpty()) {
            for (CalculoAlvara calculoAlvara : calculosAlvara) {
                if (tipoControle.equals(calculoAlvara.getControleCalculo())) {
                    calculos.add(calculoAlvara);
                }
            }
        }
        return calculos;
    }

    public Boolean getAlterouFuncionamento() {
        return alterouFuncionamento != null ? alterouFuncionamento : false;
    }

    public void setAlterouFuncionamento(Boolean alterouFuncionamento) {
        this.alterouFuncionamento = alterouFuncionamento;
    }

    public List<LogAlvaraRedeSim> getLogsRedeSim() {
        return logsRedeSim;
    }

    public void setLogsRedeSim(List<LogAlvaraRedeSim> logsRedeSim) {
        this.logsRedeSim = logsRedeSim;
    }

    public Boolean hasRecalculo() {
        for (CalculoAlvara calculoAlvara : this.getCalculosAlvara()) {
            if (TipoControleCalculoAlvara.RECALCULO.equals(calculoAlvara.getControleCalculo())) {
                return true;
            }
        }
        return false;
    }

    public boolean isDispensaLicenciamento() {
        return getCnaes().stream()
            .allMatch(ca -> GrauDeRiscoDTO.BAIXO_RISCO_A.equals(ca.getCnae().getGrauDeRisco()));
    }

    public EnderecoCalculoAlvara getEnderecoPrincipal() {
        return enderecosAlvara.stream()
            .filter(ea -> ea.getTipoEndereco().equals(TipoEndereco.COMERCIAL))
            .findFirst()
            .orElse(null);
    }

    public boolean isEstornado() {
        return SituacaoCalculoAlvara.ESTORNADO.equals(situacaoCalculoAlvara);
    }

    public boolean isEmAberto() {
        return SituacaoCalculoAlvara.EM_ABERTO.equals(situacaoCalculoAlvara);
    }

    public boolean isCalculado() {
        return SituacaoCalculoAlvara.CALCULADO.equals(situacaoCalculoAlvara);
    }
}
