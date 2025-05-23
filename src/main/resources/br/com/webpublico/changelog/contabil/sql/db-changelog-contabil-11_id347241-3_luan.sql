merge into configuracaodiaria cd using (
    select case when lei.PROPOSITOATOLEGAL in ('ATO_DE_PESSOAL', 'BENEFICIO_PREVIDENCIARIO')
                    then (UPPER(case when lei.TIPOATOLEGAL is not null then
                                         case lei.TIPOATOLEGAL
                                             when 'RESOLUCAO' then 'Resolução'
                                             when 'PROJETO_DE_LEI' then 'Projeto de Lei'
                                             when 'OFICIO' then 'Ofício'
                                             when 'MEDIDA_PROVISORIA' then 'Medidas Provisórias'
                                             when 'LEI_COMPLEMENTAR' then 'Lei Complementar'
                                             when 'LEI_ORDINARIA' then 'Lei ordinária'
                                             when 'LEI_ORGANICA' then 'Lei Orgânica'
                                             when 'REGIMENTO_INTERNO' then 'Regimento Interno'
                                             when 'TERMO_POSSE' then 'Termo de Posse'
                                             when 'ORIENTACAO_TECNICA' then 'Orientação técnica'
                                             when 'RECOMENDACAO' then 'Recomendação'
                                             when 'INSTRUCAO_NORMATIVA' then 'Instrução Normativa'
                                             when 'RELATORIO' then 'Relatório'
                                             when 'AUTOGRAFO' then 'Autógrafo'
                                             when 'CONTROLE_EXTERNO' then 'Controle Externo'
                                             else lei.TIPOATOLEGAL end else '' end)
            || ' Nº ' || lei.NUMERO || (
                              case when lei.DATAEMISSAO is not null then ' Emitido em: ' || to_char(lei.DATAEMISSAO, 'dd/MM/yyyy') else '' end) || (
                              case when lei.DATAPROMULGACAO is not null then ', Promulgado em: ' || to_char(lei.DATAPROMULGACAO, 'dd/MM/yyyy') else '' end) || (
                              case when lei.DATAPUBLICACAO is not null then ', Publicado em: ' || to_char(lei.DATAPUBLICACAO, 'dd/MM/yyyy') else '' end) || (
                              case when unidadeLei.ID is not null then ', Órgão: ' || unidadeLei.DESCRICAO else '' end) || (
                              case when pjUeLei.ID is not null then ', Unid. Ext.: ' || pjUeLei.RAZAOSOCIAL else '' end))
                else 'Nº ' || lei.NUMERO || ' - ' || lei.NOME end as descricaoAto,
           obj.id as idCd
    from configuracaodiaria obj
             inner join atolegal lei on lei.id = obj.lei_id
             left join unidadeorganizacional unidadeLei on unidadeLei.id = lei.unidadeorganizacional_id
             left join unidadeexterna ueLei on ueLei.id = lei.unidadeexterna_id
             left join pessoajuridica pjUeLei on pjUeLei.id = uelei.pessoajuridica_id
) dados on (cd.id = dados.idCd)
    when matched then update set cd.nome = dados.descricaoAto;
