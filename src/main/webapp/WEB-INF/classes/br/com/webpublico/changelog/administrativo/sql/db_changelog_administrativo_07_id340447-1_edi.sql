update unidademedida set mascaravalorunitario = mascaraquantidade
where MASCARAQUANTIDADE in ('DUAS_CASAS_DECIMAL', 'TRES_CASAS_DECIMAL', 'QUATRO_CASAS_DECIMAL');

update unidademedida set mascaravalorunitario = 'DUAS_CASAS_DECIMAL'
where mascaravalorunitario is null;
