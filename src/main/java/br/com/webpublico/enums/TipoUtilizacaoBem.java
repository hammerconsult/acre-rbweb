package br.com.webpublico.enums;


import com.google.common.collect.Lists;

import java.util.List;

public enum TipoUtilizacaoBem {

    BENS_USO_ESPECIAL("Bens de Uso Especial", "Bem de Uso Especial", "Bens de uso especial: compreendem os bens, tais como edifícios ou terrenos, destinados a serviço ou estabelecimento da administração federal, estadual ou municipal, inclusive os de suas autarquias e fundações públicas, como imóveis residenciais, terrenos, glebas, aquartelamento, aeroportos, açudes, fazendas, museus, hospitais, hotéis dentre outros."),
    BENS_DOMINIAIS("Bens Dominiais/Dominicais", "Bem Dominial/Dominical", "Bens dominiais/dominicais: compreendem os bens que constituem o patrimônio das pessoas jurídicas de direito público, como objeto de direito pessoal, ou real, de cada uma dessas entidades. Compreende ainda, não dispondo a lei em contrário, os bens pertencentes às pessoas jurídicas de direito público a que se tenha dado estrutura de direito privado, como apartamentos, armazéns, casas, glebas, terrenos, lojas, bens destinados a reforma agrária, dentre outros."),
    BENS_USO_COMUM_POVO("Bens de Uso Comum do Povo", "Bem de Uso Comum do Povo", "Bens de uso comum do povo: podem ser entendidos como os de domínio público, construídos ou não por pessoas jurídicas de direito público."),
    BENS_IMOVEIS_ANDAMENTO("Bens Imóveis em Andamento", "Bem Imóvel em Andamento", "Bens imóveis em andamento: compreendem os valores de bens imóveis em andamento, ainda não concluídos. Exemplos: obras em andamento, estudos e projetos (que englobem limpeza do terreno, serviços topográficos etc), benfeitoria em propriedade de terceiros, dentre outros."),
    DEMAIS_BENS_IMOVEIS("Demais Bens Imóveis", "Demais Bens Imóveis", "Demais bens imóveis: compreendem os demais bens imóveis não classificados anteriormente. Exemplo: bens imóveis locados para terceiros, imóveis em poder de terceiros, dentre outros bens.");

    private String descricao;
    private String singular;
    private String texto;

    TipoUtilizacaoBem(String descricao, String singular, String texto) {
        this.descricao = descricao;
        this.singular = singular;
        this.texto = texto;
    }

    public static List<TipoUtilizacaoBem> getTiposUtilizacaoPorTipoBem(TipoBem tipoBem) {
        List<TipoUtilizacaoBem> tipos = Lists.newArrayList();
        if (tipoBem != null) {
            switch (tipoBem) {
                case MOVEIS:
                case INTANGIVEIS:
                    tipos.add(TipoUtilizacaoBem.BENS_DOMINIAIS);
                    break;
                case IMOVEIS:
                    tipos.addAll(Lists.<TipoUtilizacaoBem>newArrayList(TipoUtilizacaoBem.values()));
                    break;
                default:
                    tipos = null;
            }
        }
        return tipos;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getSingular() {
        return singular;
    }

    public String getTexto() {
        return texto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
