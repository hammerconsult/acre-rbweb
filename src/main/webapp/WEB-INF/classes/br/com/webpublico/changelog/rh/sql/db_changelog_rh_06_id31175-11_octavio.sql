update eventofp evento set evento.incidenciatributariairrf_id = (select irrf.id from incidenciatributariairrf irrf where irrf.codigo = '13')
where evento.codigo = '151';
update eventofp evento set evento.incidenciatributariairrf_id = (select irrf.id from incidenciatributariairrf irrf where irrf.codigo = '33')
where evento.codigo = '418';
