DELETE
FROM concessaoferiaslicenca a
where a.periodoaquisitivofl_id = 871562161;

DELETE
FROM aprovacaoferias apf
WHERE apf.sugestaoferias_id = 872049395;

DELETE
FROM sugestaoferias sf
WHERE sf.periodoaquisitivofl_id = 871562161;

DELETE
FROM periodoaquisitivofl pa
WHERE pa.id = 871562161;