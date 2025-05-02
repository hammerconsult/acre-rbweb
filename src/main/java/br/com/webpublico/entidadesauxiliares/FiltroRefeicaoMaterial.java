package br.com.webpublico.entidadesauxiliares;


import br.com.webpublico.controlerelatorio.RefeicaoControlador;
import br.com.webpublico.entidades.*;

public class FiltroRefeicaoMaterial {

    private RefeicaoControlador.TipoFiltroMaterial tipoFiltroMaterial;

    private Pessoa fornecedor;

    private Contrato contrato;

    private Licitacao licitacao;

    private DispensaDeLicitacao dispensa;

    private Material material;

    public FiltroRefeicaoMaterial(RefeicaoControlador.TipoFiltroMaterial tipoFiltroMaterial) {
        this.tipoFiltroMaterial = tipoFiltroMaterial;
    }

    public RefeicaoControlador.TipoFiltroMaterial getTipoFiltroMaterial() {
        return tipoFiltroMaterial;
    }

    public void setTipoFiltroMaterial(RefeicaoControlador.TipoFiltroMaterial tipoFiltroMaterial) {
        this.tipoFiltroMaterial = tipoFiltroMaterial;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public DispensaDeLicitacao getDispensa() {
        return dispensa;
    }

    public void setDispensa(DispensaDeLicitacao dispensa) {
        this.dispensa = dispensa;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getCondicaoSql() {
        String sql = " ";
        String clausula = " where ";
        if (licitacao != null) {
            sql += clausula + " lic.id = " + licitacao.getId();
            clausula = " and ";
        }
        if (dispensa != null) {
            sql +=  clausula + " disp.id = " + dispensa.getId();
            clausula = " and ";
        }
        if (contrato != null) {
            sql += clausula + " cont.id = " + contrato.getId();
            clausula = " and ";
        }
        if (fornecedor != null) {
            sql += clausula + " p.id = " + fornecedor.getId();
        }
        return sql;
    }
}
