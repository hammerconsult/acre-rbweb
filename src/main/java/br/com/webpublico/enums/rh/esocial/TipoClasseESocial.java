package br.com.webpublico.enums.rh.esocial;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.cadastrofuncional.AvisoPrevio;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.ProcessoAdministrativoJudicial;
import br.com.webpublico.entidades.rh.saudeservidor.CAT;
import br.com.webpublico.entidades.rh.saudeservidor.RiscoOcupacional;
import br.com.webpublico.entidadesauxiliares.rh.esocial.ReativacaoBeneficio;

public enum TipoClasseESocial {

    S1000("S-1000", "Informações do Empregador/Contribuinte/Órgão Público", ConfiguracaoEmpregadorESocial.class),
    S1005("S-1005", "Tabela de Estabelecimentos, Obras ou Unidades de Órgãos Públicos", ConfiguracaoEmpregadorESocial.class),
    S1010("S-1010", "Tabela de Rubricas", EventoFP.class),
    S1020("S-1020", "Tabela de Lotações Tributárias", Entidade.class),
    S1030("S-1030", "Tabela de Cargos/Empregos Públicos", Cargo.class),
    S1035("S-1035", "Tabela de Carreiras Públicas", Cargo.class),
    S1040("S-1040", "Tabela de Funções/Cargos em Comissão", Cargo.class),
    S1050("S-1050", "Tabela de Horários/Turnos de Trabalho", HorarioDeTrabalho.class),
    S1060("S-1060", "Tabela de Ambientes de Trabalho", ConfiguracaoEmpregadorESocial.class),
    S1070("S-1070", "Tabela de Processos Administrativos/Judiciais", ProcessoAdministrativoJudicial.class),

    S1200("S-1200", "Remuneração de trabalhador vinculado ao Regime Geral de Previd. Social", null),
    S1202("S-1202", "Remuneração de servidor vinculado a Regime Próprio de Previd. Social", null),
    S1207("S-1207", "Benefícios previdenciários - RPPS", null),
    S1210("S-1210", "Pagamentos de Rendimentos do Trabalho", null),
    S1270("S-1270", "Contratação de Trabalhadores Avulsos Não Portuários", null),
    S1280("S-1280", "Informações Complementares aos Eventos Periódicos", null),
    S1295("S-1280", "Solicitação de Totalização para Pagamento em Contingência", null),
    S1298("S-1298", "Reabertura dos Eventos Periódicos", null),
    S1299("S-1299", "Fechamento dos Eventos Periódicos", null),
    S1300("S-1300", "Contribuição Sindical Patronal", null),
    S2190("S-2190", "Admissão de Trabalhador - Registro Preliminar", VinculoFP.class),
    S2200("S-2200", "Cadastramento Inicial do Vínculo e Admissão/Ingresso de Trabalhador", ContratoFP.class),
    S2205("S-2205", "Alteração de Dados Cadastrais do Trabalhador", ContratoFP.class),
    S2206("S-2206", "Alteração de Contrato de Trabalho", ContratoFP.class),
    S2210("S-2210", "Comunicação de Acidente de Trabalho", CAT.class),
    S2220("S-2220", "Monitoramento da Saúde do Trabalhador", ASO.class),
    S2230("S-2230", "Afastamento Temporário", Afastamento.class),
    S2231("S-2231", "Cessão/Exercício em Outro Órgão", CedenciaContratoFP.class),
    S2240("S-2240", "Condições Ambientais do Trabalho", RiscoOcupacional.class),
    S2250("S-2250", "Aviso Prévio", AvisoPrevio.class),
    S2298("S-2298", "Reintegração", Reintegracao.class),
    S2299("S-2299", "Desligamento", ExoneracaoRescisao.class),
    S2300("S-2300", "Trabalhador Sem Vínculo de Emprego/Estatutário", VinculoFP.class),
    S2306("S-2306", "Trabalhador Sem Vínculo de Emprego/Estatutário - Alteração Contratual", VinculoFP.class),
    S2399("S-2399", "Desligamento Trabalhador Sem Vínculo de Emprego/Estatutário - Alteração Contratual", VinculoFP.class),
    S2400("S-2400", "Cadastro de Benefícios Previdenciários - RPPS", VinculoFP.class),
    S2405("S-2405", "Cadastro de Beneficiário - Entes Públicos", VinculoFP.class),
    S2410("S-2410", "Cadastro de Benefício - Entes Públicos - Início", VinculoFP.class),
    S2418("S-2418", "Reativação de Benefício", ReativacaoBeneficio.class),
    S2420("S-2420", "Cadastro de Benefícios – Entes Públicos - Término", VinculoFP.class),
    S3000("S-3000", "Exclusão de eventos", null),
    S5001("S-5001", "Informações das contribuições sociais por trabalhador", null),
    S5002("S-5002", "Imposto de Renda Retido na Fonte", null),
    S5011("S-5011", "Informações das contribuições sociais consolidadas por contribuinte", null),
    S5012("S-5012", "Informações do IRRF consolidadas por contribuinte", null);


    private String codigo;
    private String descricao;
    private Class classe;

    TipoClasseESocial(String codigo, String descricao, Class classe) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.classe = classe;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Class getClasse() {
        return classe;
    }

    @Override
    public String toString() {
        return codigo ;
    }

}
