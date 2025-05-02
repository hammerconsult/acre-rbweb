update recursosistema set caminho = replace(caminho, 'agendamentolaudomedicosaud', 'agendamentopericiamedicasaud'),
                          nome = replace(nome, 'LAUDO MEDICO', 'PERICIA MEDICA')
where caminho like '%/saud/agendamentolaudomedicosaud/%';

update menu set caminho = '/tributario/saud/agendamentopericiamedicasaud/lista.xhtml',
                label = 'AGENDAMENTO DE PERÍCIA MÉDICA DO SAUD'
where caminho = '/tributario/saud/agendamentolaudomedicosaud/lista.xhtml';
