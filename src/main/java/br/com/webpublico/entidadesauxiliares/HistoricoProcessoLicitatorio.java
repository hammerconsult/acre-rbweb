package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.comum.consultasql.View;
import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class HistoricoProcessoLicitatorio {

    private Long id;
    private Date data;
    private String numero;
    private String situacao;
    private String responsavel;
    private String unidadeOrganizacional;
    private Boolean geraRelatorio;
    private TipoMovimentoProcessoLicitatorio tipoMovimento;
    private View view;
    private List<View> viewDetalhes;

    public HistoricoProcessoLicitatorio() {
        viewDetalhes = Lists.newArrayList();
        view = new View();
        geraRelatorio =false;
    }

    public Boolean getGeraRelatorio() {
        return geraRelatorio;
    }

    public void setGeraRelatorio(Boolean geraRelatorio) {
        this.geraRelatorio = geraRelatorio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(String unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public TipoMovimentoProcessoLicitatorio getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(TipoMovimentoProcessoLicitatorio tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public List<View> getViewDetalhes() {
        return viewDetalhes;
    }

    public void setViewDetalhes(List<View> viewDetalhes) {
        this.viewDetalhes = viewDetalhes;
    }
}
