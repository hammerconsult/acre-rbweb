package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.CaracteristicasConstrucao;
import br.com.webpublico.entidades.Construcao;

public class VOConstrucao {

    private String tipoDoImovel;
    private String utilizacaoDoImovel;
    private String padraoResidencial;
    private String descricao;
    private Double area;

    public VOConstrucao(Construcao construcao) {
        for (CaracteristicasConstrucao caracteristicasConstrucao : construcao.getCaracteristicasConstrucao()) {
            if ("tipo_imovel".equals(caracteristicasConstrucao.getAtributo().getIdentificacao())) {
                tipoDoImovel = caracteristicasConstrucao.getValorAtributo().getValorDiscreto().getValor();
            } else if ("utilizacao_imovel".equals(caracteristicasConstrucao.getAtributo().getIdentificacao())) {
                utilizacaoDoImovel = caracteristicasConstrucao.getValorAtributo().getValorDiscreto().getValor();
            } else if ("padrao_residencial".equals(caracteristicasConstrucao.getAtributo().getIdentificacao())) {
                padraoResidencial = caracteristicasConstrucao.getValorAtributo().getValorDiscreto().getValor();
            }
        }
        this.descricao = construcao.getDescricao();
        this.area = construcao.getAreaConstruida();
    }

    public String getTipoDoImovel() {
        return tipoDoImovel;
    }

    public void setTipoDoImovel(String tipoDoImovel) {
        this.tipoDoImovel = tipoDoImovel;
    }

    public String getUtilizacaoDoImovel() {
        return utilizacaoDoImovel;
    }

    public void setUtilizacaoDoImovel(String utilizacaoDoImovel) {
        this.utilizacaoDoImovel = utilizacaoDoImovel;
    }

    public String getPadraoResidencial() {
        return padraoResidencial;
    }

    public void setPadraoResidencial(String padraoResidencial) {
        this.padraoResidencial = padraoResidencial;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }
}
