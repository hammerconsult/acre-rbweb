INSERT INTO enquadramentofiscal(id, CADASTROECONOMICO_ID, PORTE, TIPOCONTRIBUINTE, REGIMEESPECIALTRIBUTACAO,
            TIPOPERIODOVALORESTIMADO, ISSESTIMADO, CONTABILIDADE, SIMPLESNACIONAL, IMUNEISS, ISENTO, SUBSTITUTOTRIBUTARIO,
            PRESTADORSERVICO, TIPONOTAFISCALSERVICO, CLASSIFICACAOATIVIDADE, ESCRITORIOCONTABIL_ID, TIPOISSQN, INICIOVIGENCIA)
     select hibernate_sequence.nextval, id, porte, tipocontribuinte,
            CASE WHEN coalesce(mei,0) = 1 THEN 'MICROEMPRESARIO_INDIVIDUAL' ELSE null END as REGIMEESPECIALTRIBUTACAO,
            tipoperiodovalorestimado, issestimado, 0, supersimples, imuneiss, isento, SUBSTITUTOTRIBUTARIO,
            CASE WHEN coalesce(classificacaoatividade,'') = 'PRESTACAO_DE_SERVICO' THEN 1 ELSE null END as PRESTADORSERVICO,
            'CONVENCIONAL', classificacaoatividade, ESCRITORIOCONTABIL_ID, tipoissqn,
            case when abertura is null then current_date else abertura end INICIOVIGENCIA
     from cadastroeconomico
